/**
 * @(#)MDCService.java Created on 2012-1-12 下午1:20:06
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MDCService extends AbstractActionService implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(MDCService.class);

	/**
	 * 判断是否需要年度评估提醒 ，是否为最后随访记录
	 * 
	 * @param body
	 *            [empiId]
	 * @param planDate
	 * @param businessType
	 * @param dao
	 * @return -1 表示不需要提醒，<br/>
	 *         0 没有随访记录 <br/>
	 *         1 表示需要提醒下次随访时要做年度评估，(倒数第二条)<br/>
	 *         2 表示要提醒本次需要年度评估（倒数第一条） 
	 * @throws ModelDataOperationException
	 */
	protected int alarmAndIfLast(Map<String, Object> body, Date planDate,
			String businessType, BaseDAO dao)
			throws ModelDataOperationException {
		MDCBaseModel mbm = new MDCBaseModel(dao);
		String empiId = (String) body.get("empiId");
		if (planDate == null) {
			return 0;
		}
		List<Map<String, Object>> list = mbm.getListVisitPlan(empiId,
				businessType, planDate);
		
		int count = (list == null ? -1 : list.size());
		if (count == 2) {
			return 1;
		}
		if (count == 1) {
			return 2;
		}
		return -1;
	}

	/**
	 * 获取随访计划的最小间隔时间(以及计划模式)
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetMinStep(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String thisPlanId = (String) reqBody.get("planId");
		String empiId = (String) reqBody.get("empiId");
		String businessType = String.valueOf(reqBody.get("businessType"));
		MDCBaseModel mbm = new MDCBaseModel(dao);

		// 获取随访计划的最小间隔时间
//		int minStep;
//		try {
//			minStep = mbm.getMinStep(empiId, businessType);
//		} catch (ModelDataOperationException e) {
//			logger.error("Failed to get minStep of PUB_PlanType.", e);
//			res.put(RES_CODE, e.getCode());
//			res.put(RES_MESSAGE, e.getMessage());
//			throw new ServiceException(e);
//		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}

		// 获取下次随访计划
		Map<String, Object> nextPlan;
		try {
			nextPlan = mbm.getNextVisitPlan(empiId, businessType, thisPlanId);
		} catch (ModelDataOperationException e) {
			logger.error("Get next visitPlan failed.", e);
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		if (nextPlan != null) {
			resBody.put("nextRemindDate", nextPlan.get("beginVisitDate"));
			resBody.put("nextPlanId", nextPlan.get("planId"));
		}

		// 获得planMode
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, businessType + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		resBody.put("planMode", planMode);
//		resBody.put("minStep", minStep);
	}
}
