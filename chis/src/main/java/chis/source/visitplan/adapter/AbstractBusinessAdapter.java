/*
 * @(#)AbstractBusinessAdapter.java Created on 2011-12-30 下午3:26:53
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.PlanStatus;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.schedule.PlanScheduleItem;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractBusinessAdapter implements BusinessAdapter,
		BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractBusinessAdapter.class);

	protected BaseDAO getDAO(Context ctx) {
		return new BaseDAO();
	}

	/**
	 * @param params
	 * @param expression
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	protected abstract boolean runExpression(CreatationParams params,
			String expression, Context ctx) throws CreateVisitPlanException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getPlanType(com.bsoft
	 * .bschis.source.visitplan.CreatationParams, java.util.List,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public PlanType getPlanType(CreatationParams params,
			List<PlanInstance> planInstances, PlanType currentType, Context ctx)
			throws CreateVisitPlanException, ServiceException, ControllerException {
		String planTypeCode = null;
		// @@ 如果配置了所有间隔都一致时，使用默认的计划类型。
		try {
			if (Boolean.valueOf(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, params.getBusinessType()
							+ "_visitIntervalSame"))) {
				planTypeCode = ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_planTypeCode");
			} else {
				for (PlanInstance pi : planInstances) {
					if (runExpression(params, pi.getExpression(), ctx)) {
						params.setObjectValue("$matchedPlanInstance", pi);
						if (currentType != null
								&& pi.getPlanTypeCode().equals(
										currentType.getPlanTypeCode())) {
							return currentType;
						}
						planTypeCode = pi.getPlanTypeCode();
						break;
					}
				}
			}

			if (planTypeCode != null) {
				return getPlanTypeByCode(planTypeCode,
						params.getBusinessType(), ctx);
			}
			params.setObjectValue("$matchedPlanInstance", null);
			// @@ 使用默认类型。
			String defaultType = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, params.getBusinessType()
							+ "_planTypeCode");
			if (defaultType != null) {
				return getPlanTypeByCode(defaultType, params.getBusinessType(),
						ctx);
			}
			throw new CreateVisitPlanException("Cannot get plan type.");
		} catch (ControllerException e) {
			throw new CreateVisitPlanException("Cannot get plan type.");
		}
	}

	/**
	 * 获取计划类型。
	 * 
	 * @param expression
	 * @param instanceType
	 * @param session
	 * @return
	 * @throws ControllerException 
	 */
	protected PlanType getPlanTypeByCode(String planTypeCode,
			String businessType, Context ctx) throws CreateVisitPlanException, ControllerException {
		PlanType planType = null;
		try {
			planType = new VisitPlanModel(getDAO(ctx)).getPlanType(
					planTypeCode, businessType);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Failed to get plan type of type code[" + planTypeCode
							+ "].", e);
		}
		if (planType == null) {
			logger.error("Plan type of code[{}] not found.", planTypeCode);
			return null;
		}
		return planType;
	}

	/*
	 * 子类可视情况进行覆盖。
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#createPlan(com.bsoft
	 * .bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.schedule.PlanScheduleItem)
	 */
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem planScheduleItem) throws ServiceException {
		Map<String, Object> plan = new HashMap<String, Object>();
		plan.put("empiId", params.getEmpiId());
		plan.put("recordId", params.getRecordId());
		plan.put("businessType", params.getBusinessType());
		plan.put("taskDoctorId", params.getTaskDoctorId());
		if (params.getStringValue("groupCode") != null) {
			plan.put("groupCode", params.getStringValue("groupCode"));
		}
		if (params.getStringValue("fixGroupDate") != null) {
			plan.put("fixGroupDate",
					BSCHISUtil.toDate(params.getStringValue("fixGroupDate")));
		}
		plan.put("planStatus", PlanStatus.NEED_VISIT);
		plan.put("planDate", planScheduleItem.getPlanDate());
		plan.put("beginDate", planScheduleItem.getBeginDate());
		plan.put("endDate", planScheduleItem.getEndDate());
		plan.put("sn", params.getIntValue("$sn"));
		if (null != (Integer) params.getIntValue("visitResult")) {
			plan.put("extend1", params.getIntValue("visitResult"));
		}
		return plan;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getFixedFirstPlanDate
	 * ()
	 */
	public Date getFixedFirstPlanDate(CreatationParams params)
			throws CreateVisitPlanException, ServiceException {
		return null;
	}
}
