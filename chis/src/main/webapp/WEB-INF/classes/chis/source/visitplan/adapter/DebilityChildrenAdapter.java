/*
 * @(#)DebilityChildrenAdapter.java Created on 2012-2-8 下午4:35:52
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.ManageYearUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.schedule.PlanScheduleItem;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DebilityChildrenAdapter extends AbstractBusinessAdapter {

	/**
	 * 随访最多访到几周岁。
	 */
	private static final int MAX_VISIT_AGE = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getPlanType(com.bsoft
	 * .bschis.source.visitplan.CreatationParams, java.util.List,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public PlanType getPlanType(CreatationParams params,
			List<PlanInstance> planInstances, PlanType currentType, Context ctx)
			throws CreateVisitPlanException, ControllerException {
		if (currentType != null) {
			return currentType;
		}
		return getPlanTypeByCode(params.getStringValue("$planTypeCode"),
				params.getBusinessType(), ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams, chis.source.visitplan.PlanType,
	 * ctd.util.context.Context)
	 */
	@Override
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		Date date = new Date();
		if (params.getStringValue("visitDate") != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(BSCHISUtil.toDate(params.getStringValue("visitDate")));
			c.add(planType.getFrequency(), planType.getCycle());
			// c.add(Calendar.MONTH, 1);
			date = c.getTime();
		}
		return date;
//		Date lastVisitDate;
//		try {
//			lastVisitDate = getCurrentYearLastPlanDate(params.getRecordId(),
//					params.getBusinessType(), ctx);
//		} catch (ModelDataOperationException e) {
//			throw new CreateVisitPlanException(
//					"Failed to get last visit date of current year.", e);
//		}
//		Calendar c = Calendar.getInstance();
//		if (lastVisitDate != null) {
//			c.setTime(lastVisitDate);
//			c.add(planType.getFrequency(), planType.getCycle());
//			return c.getTime();
//		} else {
//			return date;
//		}
	}

	/**
	 * 获取本年度最后一次随访的计划时间。
	 * 
	 * @param phrId
	 * @param instanceType
	 * @param session
	 * @param ctx
	 * @return
	 * @throws ServiceException 
	 * @throws PersistentDataOperationException
	 */
	protected Date getCurrentYearLastPlanDate(String recordId,
			String businessType, Context ctx)
			throws ModelDataOperationException, ServiceException {
		Date st = new VisitPlanModel(getDAO(ctx)).getLastPlanDate(recordId,
				businessType);
		if (st == null) {
			return null;
		}

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		ManageYearUtil util = new ManageYearUtil(new Date());
		Date d1 = util.getHypertensionCurYearStartDate();
		// 如果最后一次随访日期与当前日期不是在同一个管理年度。
		if (st.before(d1) && now.after(d1)) {
			return null;
		}
		return st;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	@Override
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		Calendar c = Calendar.getInstance();
		if (params.getStringValue("visitDate") != null) {
			c.setTime(BSCHISUtil.toDate(params.getStringValue("visitDate")));
			c.add(planType.getFrequency(), planType.getCycle());
		}else{
			c.add(planType.getFrequency(), planType.getCycle());
			c.add(Calendar.DAY_OF_YEAR, -1);
		}
		return c.getTime();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression
	 * (chis.source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("'type'", params.getStringValue("$type"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Failed to run instance expression.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#createPlan
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.schedule.PlanScheduleItem)
	 */
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem planScheduleItem) throws ServiceException {
		Map<String, Object> plan = super.createPlan(params, planScheduleItem);
		Date birthday = params.getDateValue("$birthday");
		Date datum = planScheduleItem.getPlanDate();
		int age = BSCHISUtil.getMonths(birthday, datum);
		plan.put("extend1", age);
		return plan;
	}
}
