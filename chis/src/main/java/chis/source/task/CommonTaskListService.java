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

import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description 通用的获取工作列表的服务，主要包含慢病，精神病 以及老年人随访。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class CommonTaskListService extends AbstractTaskListService {

	private static final Log logger = LogFactory
			.getLog(CommonTaskListService.class);

	/**
	 * 要查询的字段。
	 * 
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer(super.buildQueryFields(sc));
		String currentDate = BSCHISUtil.toString(new Date(), null);
		// @@ 高血压跟糖尿病 的剩余天数使用计划日期来算，其他的用结束日期算。
		fields.append("a.endDate-date('")
				.append(currentDate)
				.append("') as remainDays,a.endDate as lastDate ,a.beginDate as earliestDate");

		// fields.append(",(case when a.businessType='1' or a.businessType='2' ")
		// .append("then ((a.planDate-date('")
		// .append(currentDate)
		// .append("'))+10) when a.businessType='10' then ((a.planDate-date('")
		// .append(currentDate).append("'))+7) else (a.endDate-date('")
		// .append(currentDate).append("')) end) as remainDays");
		// fields.append(",(case when a.businessType='1' or a.businessType='2' ")
		// .append("then sum_day(a.planDate+10) when a.businessType='10' then sum_day(a.planDate+7) else a.endDate end) as lastDate");
		// fields.append(",(case when a.businessType='1' or a.businessType='2' ")
		// .append("then sum_day(a.planDate-10) when a.businessType='10' then sum_day(a.planDate-7) else a.beginDate end) as earliestDate");
		return fields.toString();
	}

	/**
	 * 初始化查询条件。
	 * 
	 * @param jsonReq
	 * @param sc
	 * @param ctx
	 * @return
	 * @throws ExpException
	 */
	protected String initCondition(Map<String, Object> jsonReq, Schema sc,
			Context ctx) throws ExpException {
		StringBuffer condition = new StringBuffer(super.initCondition(jsonReq,
				sc, ctx));
		String fromDate = StringUtils.trimToEmpty((String) jsonReq
				.get("fromDate"));
		// @@ 计划开始日期在限定最后日期前，计划结束日期在限定最早日期后。
		if (fromDate.length() != 0) {
			condition.append(" and date('").append(fromDate).append("')<=")
					.append("a.endDate");
			// condition.append(" and date('").append(fromDate)
			// .append("')<=(case when a.businessType='1' or ")
			// .append("a.businessType='2' then sum_day(a.planDate+10) ")
			// .append("when a.businessType='10' then sum_day(a.planDate+7)")
			// .append(" else a.endDate end)");
		}
		String toDate = StringUtils.trimToEmpty((String) jsonReq.get("toDate"));
		if (toDate.length() != 0) {
			condition.append(" and date('").append(toDate).append("')>=")
					.append("a.beginDate");
			// condition.append(" and date('").append(toDate)
			// .append("')>=(case when a.businessType='1' or ")
			// .append("a.businessType='2' then sum_day(a.planDate-10)")
			// .append("when a.businessType='10' then sum_day(a.planDate-7)")
			// .append(" else a.planDate end)");
		}
		int taskType = jsonReq.get("taskType") == null ? 0
				: (Integer.parseInt(jsonReq.get("taskType")+""));
		if (taskType == 0) {
			return condition.toString();
		}
		String currentDate = BSCHISUtil.toString(new Date(), null);
		condition.append(" and a.endDate - date('").append(currentDate)
				.append("')");
		// condition.append(" and (case when a.businessType='1' or a.businessType='2' ")
		// .append("then (a.planDate-date('").append(currentDate)
		// .append("')+10) when a.businessType='10' then (a.planDate-date('")
		// .append(currentDate).append("')+7) else (a.endDate-date('")
		// .append(currentDate).append("')) end)");
		switch (taskType) {
		case 1:// @@ 除去过期任务，包括到期和未到期的任务。
			condition.append(">=0");
			break;
		case 2:// @@ 剩余天数在设定值以内的任务，将近过期的。
			condition.append("<=").append(HURRY_UP_DAY)
					.append(" and a.endDate - date('").append(currentDate)
					.append("')>=0");
			// condition
			// .append("<=")
			// .append(HURRY_UP_DAY)
			// .append(" and (case when a.businessType='1' or a.businessType='2' ")
			// .append("then (a.planDate-date('")
			// .append(currentDate)
			// .append("')+10) when a.businessType='10' then (a.planDate-date('")
			// .append(currentDate).append("')+7) else (a.endDate-date('")
			// .append(currentDate).append("')) end)>=0");
			break;
		case 3:// @@ 已超过最晚执行日期的任务。
			condition.append("<0");
			break;
		case 4:// @@ 在最早执行日期和最晚执行日期间的任务。
			condition.append(">=0 and ").append("date('").append(currentDate)
					.append("') - a.beginDate >=0");
			// condition
			// .append(">=0 and (case when a.businessType='1' or a.businessType='2' ")
			// .append("then (date('")
			// .append(currentDate)
			// .append("')-a.planDate+10) when a.businessType='10' then (date('")
			// .append(currentDate)
			// .append("')-a.planDate+7) else (date('")
			// .append(currentDate).append("')-a.planDate) end)>=0");
			break;
		case 5:// @@ 未到执行日期的任务。
			int hypertension;
			int diabetes;
			int psychosis;
			try {
				hypertension = Integer.parseInt(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, BusinessType.GXY
								+ "_precedeDays"))
						+ Integer.parseInt(ApplicationUtil.getProperty(
								Constants.UTIL_APP_ID, BusinessType.GXY
										+ "_delayDays"));
				diabetes = Integer.parseInt(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, BusinessType.TNB
								+ "_precedeDays"))
						+ Integer.parseInt(ApplicationUtil.getProperty(
								Constants.UTIL_APP_ID, BusinessType.TNB
										+ "_delayDays"));
				psychosis = Integer.parseInt(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, BusinessType.PSYCHOSIS
								+ "_precedeDays"))
						+ Integer.parseInt(ApplicationUtil.getProperty(
								Constants.UTIL_APP_ID, BusinessType.PSYCHOSIS
										+ "_delayDays"));
			} catch (ControllerException e) {
				throw new ExpException(e);
			}
			condition.append(">(case when a.businessType='1' then ")
					.append(hypertension)
					.append(" when a.businessType = '2' then ")
					.append(diabetes)
					.append(" when a.businessType = '10' then ")
					.append(psychosis)
					.append(" else (a.endDate-a.planDate) end)");
			// condition
			// .append(">(case when a.businessType='1' or a.businessType='2' then 20 when a.businessType='10' then 14 ")
			// .append("else (a.endDate-a.planDate) end)");
			break;
		case 6:// @@ 计划当天任务。
			condition.append(">=0 and ").append("date('").append(toDate)
					.append("') >= a.planDate and ").append("date('").append(fromDate)
					.append("') <= a.planDate");
		default:
			logger.warn("No such task type code[" + taskType + "].");
		}

		return condition.toString();
	}

	protected String getType() {
		return "1";
	}
}
