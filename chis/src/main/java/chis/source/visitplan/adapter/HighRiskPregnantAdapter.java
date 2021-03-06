/*
 * @(#)HighRiskPregnantAdapter.java Created on 2012-1-31 下午3:08:31
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
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
public class HighRiskPregnantAdapter extends AbstractBusinessAdapter {

	private static final int GESTATIONAL_PERIOD_WEEKS = 42;// @@ 总孕周数。

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
			throws CreateVisitPlanException, ServiceException, ControllerException {
		int period = BSCHISUtil.getFullWeeks(
				params.getDateValue("$visitPlanBeginDate"),
				params.getDateValue("$currentPlanDate"));
		params.setIntValue("$period", period);
		return super.getPlanType(params, planInstances, currentType, ctx);
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
		int frequence = params.getIntValue("$frequence");
		Calendar c = Calendar.getInstance();
		Date lastBeginDate = params.getDateValue("$lastBeginDate");
		if (lastBeginDate == null) {
			Date prCreateDate = params.getDateValue("createDate");
			Calendar ca = Calendar.getInstance();
			ca.setTime(prCreateDate);
			ca.add(Calendar.DATE, 1);
			prCreateDate = ca.getTime();
			c.setTime(BSCHISUtil.getBeginDateOfWeek(
					params.getDateValue("$lastMenstrualPeriod"), prCreateDate));
		} else {
			c.setTime(lastBeginDate);
			c.add(Calendar.WEEK_OF_YEAR, frequence);
		}
		return c.getTime();
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
		Date d = params.getDateValue("$lastMenstrualPeriod");
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.WEEK_OF_YEAR, GESTATIONAL_PERIOD_WEEKS + 1);
		c.add(Calendar.DAY_OF_YEAR, -1);
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
		expression = expression.replace("'frequence'",
				params.getStringValue("$frequence"));
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
		int week = BSCHISUtil.getFullWeeks(
				params.getDateValue("$lastMenstrualPeriod"),
				planScheduleItem.getPlanDate());
		plan.put("extend1", week);
		return plan;
	}
}
