/*
 * @(#)OldPeopleAdapter.java Created on 2012-1-12 下午7:01:04
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
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
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class OldPeopleAdapter extends AbstractBusinessAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#getPlanType
	 * (chis.source.visitplan.CreatationParams, java.util.List,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public PlanType getPlanType(CreatationParams params,
			List<PlanInstance> planInstances, PlanType currentType, Context ctx)
			throws CreateVisitPlanException, ServiceException,
			ControllerException {
		Date birthday = params.getDateValue("$birthday");
		int age = BSCHISUtil.calculateAge(birthday,
				params.getDateValue("$nextPlanDate"));
		params.setIntValue("$age", age);
		return super.getPlanType(params, planInstances, currentType, ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ControllerException {
		VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));
		Date date = null;
		Date lastPlanDate;
		try {
			lastPlanDate = vpm.getLastPlanDate(params.getRecordId(),
					BusinessType.LNR);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException("Cannot get last begin date.", e);
		}
		if (lastPlanDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(lastPlanDate);
			c.add(planType.getFrequency(), planType.getCycle());
			date = c.getTime();
			params.setDateValue("$sectionCutOffDate", BSCHISUtil.getSectionCutOffDate(
					"oldPeopleEndMonth", true));
		} else {
			date = new Date();
		}
		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		Calendar c = Calendar.getInstance();
		c.setTime(params.getOnsetDate());
		c.add(Calendar.YEAR, 1);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression
	 * (chis.source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("'age'", params.getStringValue("$age"));
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
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#createPlan
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.schedule.PlanScheduleItem)
	 */
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem planScheduleItem) throws ServiceException {
		Map<String, Object> plan = super.createPlan(params, planScheduleItem);
		plan.put("extend1", BSCHISUtil.calculateAge(
				params.getDateValue("$birthday"),
				planScheduleItem.getPlanDate()));
		plan.put("extend2", BSCHISUtil.toString(params.getOnsetDate(), null));
		return plan;
	}
}
