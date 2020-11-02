/*
 * @(#)ChildrenAdapter.java Created on 2012-1-31 下午5:26:17
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.Constants;
import chis.source.conf.ChildrenConfigManageService;
import chis.source.util.ApplicationUtil;
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
public class ChildrenAdapter extends AbstractBusinessAdapter {

	private static final int MAX_PLAN_MONTHS = 72;

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#
	 * getFixedFirstPlanDate(chis.source.visitplan.CreatationParams)
	 */
	public Date getFixedFirstPlanDate(CreatationParams params)
			throws CreateVisitPlanException, ServiceException {
		Date birth = params.getDateValue("$birthday");
		Calendar c = Calendar.getInstance();
		c.setTime(birth);
		int firstPlanDays;
		try {
			firstPlanDays = Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID,
					ChildrenConfigManageService.CHILDREN_FIRST_VISIT_DAYS));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		c.add(Calendar.DAY_OF_YEAR, firstPlanDays);
		return c.getTime();
	}

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
		Date nextPlanDate = params.getDateValue("$nextPlanDate");
		if (nextPlanDate == null) {
			return null;
		}
		Date birthday = params.getDateValue("$birthday");
		int period = getPeriod(params, birthday, nextPlanDate);
		params.setIntValue("$period", period);
		// @@ if $ageDays is true, that is to say this is the first plan.
		if (params.getBooleanValue("$ageDays")) {
			PlanType pt = new PlanType();
			try {
				pt.setCycle(Integer.valueOf(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, "childrenFirstVistDays")));
				pt.setDelayDays(Integer.valueOf(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_delayDays")));
				pt.setFrequency(PlanType.FREQUENCY_DAY);
				pt.setPrecedeDays(Integer.valueOf(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_precedeDays")));
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
			return pt;
		}
		PlanType pt = super
				.getPlanType(params, planInstances, currentType, ctx);
		if (params.getCutoutDate() == null
				|| params.getBooleanValue("$visitIntervalSame")) {
			return pt;
		}
		Date nextSectionOnsetDate = params
				.getDateValue("$nextSectionOnsetDate");
		// @@ 当下阶段开始日期为null，或者当前时间已经超过下阶段开始日期，需要重新计算。
		if (nextSectionOnsetDate == null
				|| BSCHISUtil.dateCompare(nextPlanDate, nextSectionOnsetDate) >= 0) {
			PlanInstance pi = (PlanInstance) params
					.getObjectValue("$matchedPlanInstance");
			Calendar c = Calendar.getInstance();
			c.setTime(birthday);
			int tmp = period;
			CreatationParams cp = new CreatationParams();
			while (tmp <= MAX_PLAN_MONTHS) {
				tmp++;
				cp.setIntValue("$period", tmp);
				for (PlanInstance ins : planInstances) {
					if (pi != ins
							&& runExpression(cp, ins.getExpression(), ctx)) {
						c.add(Calendar.MONTH, tmp);
						params.setDateValue("$nextSectionOnsetDate",
								c.getTime());
						return pt;
					}
				}
			}
			// @@ 如果前面都没有找到说明已经到最后一个阶段。
			params.setDateValue("$nextSectionOnsetDate", null);
		}
		return pt;
	}

	/**
	 * @param params
	 * @param birthday
	 * @param datum
	 * @return
	 * @throws ServiceException
	 * @throws CreateVisitPlanException
	 */
	protected int getPeriod(CreatationParams params, Date birthday, Date datum)
			throws CreateVisitPlanException, ServiceException {
		int age = getAgeDays(birthday, datum);
		int firstPlanDays;
		try {
			firstPlanDays = Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID,
					ChildrenConfigManageService.CHILDREN_FIRST_VISIT_DAYS));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		params.setBooleanValue("$ageDays", true);
		if (age != firstPlanDays) {
			age = getAgeMonths(birthday, datum);
			params.setBooleanValue("$ageDays", false);
		}
		return age;
	}

	/**
	 * 如果以天数计算婴儿大小等于首次随访天数，视作满月，返回1。
	 * 
	 * @param visitPlan
	 * @param birthday
	 * @param datum
	 * @return
	 * @throws ServiceException 
	 * @throws CreateVisitPlanException
	 */
	protected int getMonthAge(CreatationParams params, Date birthday, Date datum) throws ServiceException {
		try {
			int age = getAgeDays(birthday, datum);
			int firstPlanDays = Integer.parseInt(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID,
					ChildrenConfigManageService.CHILDREN_FIRST_VISIT_DAYS));
			if (age != firstPlanDays) {
				return getAgeMonths(birthday, datum);
			} else {
				return 1;
			}
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 以天数计算年龄，如果给定的出生日期在当前日期之后，将返回-1。
	 * 
	 * @param birthday
	 * @return
	 */
	protected int getAgeDays(Date birthday, Date datum) {
		if (birthday.after(datum)) {
			return -1;
		}
		return BSCHISUtil.getPeriod(birthday, datum);
	}

	/**
	 * 以月数计算年龄，如果给定的出生日期在当前日期之后将返回-1。
	 * 
	 * @param birthday
	 * @return
	 */
	protected int getAgeMonths(Date birthday, Date datum) {
		return BSCHISUtil.getMonths(birthday, datum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	@Override
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException {
		Date birthday = params.getDateValue("$birthday");
		Calendar c = Calendar.getInstance();
		c.setTime(birthday);
		if (params.getBooleanValue("$visitIntervalSame")) {
			// @@ 在首次之后，加一个间隔，如果3月一次，就从首次之后的第三个月开始。
			int day = c.get(Calendar.DAY_OF_MONTH);
			c.setTime(getFixedFirstPlanDate(params));
			c.add(planType.getFrequency(), planType.getCycle());
			c.set(Calendar.DAY_OF_MONTH, day);
			return c.getTime();
		}
		@SuppressWarnings("unchecked")
		List<PlanInstance> planInstances = (List<PlanInstance>) params
				.getObjectValue("$planInstances");
		int period = 1;
		CreatationParams cp = new CreatationParams();
		while (period < MAX_PLAN_MONTHS) {
			for (PlanInstance pi : planInstances) {
				cp.setIntValue("$period", period);
				if (runExpression(cp, pi.getExpression(), ctx)) {
					c.add(Calendar.MONTH, period);
					return c.getTime();
				}
			}
			period++;
		}
		throw new CreateVisitPlanException("Cannot get plan onset date.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	@Override
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		Date birthday = params.getDateValue("$birthday");
		Calendar c = Calendar.getInstance();
		c.setTime(birthday);
		c.add(Calendar.MONTH, MAX_PLAN_MONTHS);
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
		expression = expression.replace("'months'",
				params.getStringValue("$period"));
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
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#createPlan
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.schedule.PlanScheduleItem)
	 */
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem planScheduleItem) throws ServiceException {
		Map<String, Object> plan = super.createPlan(params, planScheduleItem);
		Date birthday = params.getDateValue("$birthday");
		Date datum = planScheduleItem.getPlanDate();
		int age = getMonthAge(params, birthday, datum);
		plan.put("extend1", age);
		return plan;
	}
}
