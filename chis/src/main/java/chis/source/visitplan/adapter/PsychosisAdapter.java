/*
 * @(#)PsychosisAdapter.java Created on 2012-2-10 上午11:39:07
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.schedule.PlanScheduleItem;

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
public class PsychosisAdapter extends AbstractBusinessAdapter {

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
		String planBeginDate = params.getStringValue("planBeginDate");
		Calendar c = Calendar.getInstance();
		c.setTime(BSCHISUtil.toDate(planBeginDate.substring(0, 10)));

		Date lastVisitEndDate = params.getDateValue("lastVisitEndDate");
		if(lastVisitEndDate != null 
				&& (BSCHISUtil.dateCompare(c.getTime(), lastVisitEndDate) > 0)){
			// @@ 档案注销恢复时 如果恢复日期晚于最后一条随访计划的结束日期 生成恢复日期当天的随访计划 --add by CHENXR
			return c.getTime();
		}
		Date lastVisitDate = params.getDateValue("lastVisitDate");
		if (lastVisitDate != null
				&& (BSCHISUtil.dateCompare(c.getTime(), lastVisitDate) < 0)) {
			c.setTime(lastVisitDate);
		}
		if (planType.getFrequency() == PlanType.FREQUENCY_MONTH) {
			c.add(Calendar.MONTH, planType.getCycle());
			return c.getTime();
		}
		if (planType.getFrequency() == PlanType.FREQUENCY_WEEK) {
			c.add(Calendar.WEEK_OF_YEAR, planType.getCycle());
			return c.getTime();
		}
		throw new CreateVisitPlanException("Cannot get plan begin date.");
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
//		Calendar c = Calendar.getInstance();
//		c.setTime(params.getOnsetDate());
//		c.add(Calendar.YEAR, 1); // @@ 默认是生成一年的计划。
//		return c.getTime();
		//新标准--随访计划每次生成一条 
		return params.getOnsetDate();// @@ 开始日期 即 结果日期
		
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
		expression = expression.replace("visitType",
				params.getStringValue("visitType"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Cannot run plan instance expression.", e);
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
		if (params.getBooleanValue("referral")) {
			plan.put("extend2", "1");// @@ 转诊。
		} else {
			plan.put("extend2", "0");// @@ 正常随访。
		}
		plan.put("extend1", 0);
		return plan;
	}
}
