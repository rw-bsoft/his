/**
 * @(#)WorkListService.java Created on 2010-6-13 下午04:30:03
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.task;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.util.BSCHISUtil;
import ctd.schema.Schema;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class MHCTaskListService extends AbstractTaskListService {

	private static final Log logger = LogFactory
			.getLog(MHCTaskListService.class);
	/**
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer(super.buildQueryFields(sc));
		String currentDate = BSCHISUtil.toString(new Date(), null);
		fields.append(",(a.endDate-date('").append(currentDate)
				.append("')) as remainDays");
		fields.append(",a.endDate as lastDate");
		fields.append(",a.beginDate as earliestDate");
		return fields.toString();
	}

	/**
	 * @param jsonReq
	 * @param sc
	 * @param ctx
	 * @return
	 * @throws ExpException
	 */
	protected String initCondition(Map<String, Object> req, Schema sc,
			Context ctx) throws ExpException {
		StringBuffer condition = new StringBuffer(super.initCondition(req,
				sc, ctx));
		// @@ 除去28到34周。
		condition.append(" and (extend1<28 or extend1>34)");
		String fromDate = StringUtils.trimToEmpty((String) req
				.get("fromDate"));
		// @@ 计划开始日期在限定最后日期前，计划结束日期在限定最早日期后。
		if (fromDate.length() != 0) {
			condition.append(" and date('").append(fromDate)
					.append("')<=a.endDate");
		}
		String toDate = StringUtils.trimToEmpty((String) req.get("toDate"));
		if (toDate.length() != 0) {
			condition.append(" and date('").append(toDate)
					.append("')>=a.beginDate");
		}
		int taskType = req.get("taskType") == null ? 0
				: Integer.parseInt(req.get("taskType")+"");
		if (taskType == 0) {
			return condition.toString();
		}
		String currentDate = BSCHISUtil.toString(new Date(), null);
		condition.append(" and (a.endDate-date('").append(currentDate)
				.append("'))");
		switch (taskType) {
		case 1:// @@ 除去过期任务，包括到期和未到期的任务。
			condition.append(">=0");
			break;
		case 2:// @@ 剩余天数在设定值以内的任务，将近过期的。
			condition.append("<=").append(HURRY_UP_DAY)
					.append(" and (a.endDate-date('").append(currentDate)
					.append("'))>=0");
			break;
		case 3:// @@ 已超过最晚执行日期的任务。
			condition.append("<0");
			break;
		case 4:// @@ 在最早执行日期和最晚执行日期间的任务。
			condition.append(">=0 and (a.planDate-date('").append(currentDate)
					.append("'))<= 0");
			break;
		case 5:// @@ 未到执行日期的任务。
			condition.append(">(a.endDate-a.planDate)");
			break;
		default:
			logger.warn("No such task type code[" + taskType + "].");
		}

		return condition.toString();
	}
	
	protected String getType(){
		return "2";
	}
}
