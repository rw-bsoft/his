/*
 * @(#)PregnantAdapter.java Created on 2012-1-13 下午2:44:16
 *
 * 版权：版权所有 B-Soft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
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
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PregnantAdapter extends AbstractBusinessAdapter {

	private static final int GESTATIONAL_PERIOD_WEEKS = 42;// @@ 总孕周数。

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
		int period = BSCHISUtil.getFullWeeks(
				params.getDateValue("$visitPlanBeginDate"), nextPlanDate);
		params.setIntValue("$period", period);
		PlanType pt = super
				.getPlanType(params, planInstances, currentType, ctx);
		if (params.getBooleanValue("$visitIntervalSame")) {
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
			c.setTime(params.getDateValue("$lastMenstrualPeriod"));
			int tmp = params.getIntValue("$period");
			CreatationParams cp = new CreatationParams();
			while (tmp <= GESTATIONAL_PERIOD_WEEKS) {
				tmp++;
				cp.setIntValue("$period", tmp);
				// @@ 找到下一个阶段的表达式。
				for (PlanInstance ins : planInstances) {
					if (pi != ins
							&& runExpression(cp, ins.getExpression(), ctx)) {
						c.add(Calendar.WEEK_OF_YEAR, tmp);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException, ControllerException {
		String pregnantId = params.getRecordId();
		VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));
		Date date;
		try {
			date = vpm.getLastVisitedPlanDate(pregnantId,
					BusinessType.MATERNAL, BusinessType.PREGNANT_HIGH_RISK);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Failed to get last visited plan date.", e);
		}
		Date datum = null;
		int thisVisitWeek = 0;
		Date lmp = params.getDateValue("$lastMenstrualPeriod");
		if (date == null) {
			// @@ 本轮计划中首次计划的基准日期。首次计划将根据基准日期确定孕周，
			// @@ 并据此确定本轮计划的首次计划具体日期。
			datum = new Date();
		} else {
			thisVisitWeek = BSCHISUtil.getFullWeeks(lmp, date);
			Date pd = addPeriodCycle(params, lmp, thisVisitWeek, ctx);
			datum = pd;
		}
		boolean visitIntervalSame = params
				.getBooleanValue("$visitIntervalSame");
		if (visitIntervalSame) {
			return BSCHISUtil.getBeginDateOfWeek(lmp, datum);
		}
		// @@ 到当前日期的孕周。
		int weeks = BSCHISUtil.getWeeks(lmp, datum);
		Calendar c = Calendar.getInstance();
		c.setTime(lmp);
		@SuppressWarnings("unchecked")
		List<PlanInstance> planInstances = (List<PlanInstance>) params
				.getObjectValue("$planInstances");
		CreatationParams cp = new CreatationParams();
		for (PlanInstance pi : planInstances) {
			cp.setIntValue("$period", weeks);
			boolean res = runExpression(cp, pi.getExpression(), ctx);
			if (!res) {
				continue;
			}
			// @@ 找到了对应阶段的表达式。
			PlanType pt = getPlanTypeByCode(pi.getPlanTypeCode(),
					params.getBusinessType(), ctx);
			int tmp = weeks;
			while (true) {
				tmp--;
				cp.setIntValue("$period", tmp);
				res = runExpression(cp, pi.getExpression(), ctx);
				if (res) {
					continue;
				}
				tmp++;
				int periodBeginWeek = tmp;
				// @@ 此时tmp值为这个阶段的起始孕周，循环加周期找到应从哪一周开始排。
				while (tmp <= weeks) {
					tmp += pt.getCycle();
				}
				if (tmp <= thisVisitWeek + pt.getCycle()) {
					tmp = tmp + pt.getCycle();
				}
				tmp = (tmp - pt.getCycle()) < periodBeginWeek ? periodBeginWeek
						: (tmp - pt.getCycle());
				c.add(Calendar.WEEK_OF_YEAR, tmp);
				// c.add(Calendar.DAY_OF_YEAR, -1);
				return c.getTime();
			}
		}
		// @@ 如果上面的表达式没有一个符合，执行以下判断。
		if (weeks >= GESTATIONAL_PERIOD_WEEKS) {
			c.add(Calendar.WEEK_OF_YEAR, weeks);
			return c.getTime();
		}
		// @@ 当前孕周小于开始生成计划的孕周，那么开始日期是第一个生成计划的孕周。
		// @@ 比如，计划从16周开始生成，当前是10周，那么就返回16周
		while (weeks <= GESTATIONAL_PERIOD_WEEKS) {
			weeks++;
			for (PlanInstance pi : planInstances) {
				cp.setIntValue("$period", weeks);
				boolean res = runExpression(cp, pi.getExpression(), ctx);
				if (res) {
					c.add(Calendar.WEEK_OF_YEAR, weeks);
					return c.getTime();
				}
			}
		}
		// @@ 如果最终没有符合的表达式。
		return BSCHISUtil.getBeginDateOfWeek(lmp, datum);
	}

	/**
	 * 给基准日期加上一个period所处阶段的间隔，比如period处在第3周，<br/>
	 * 第3周的随访计划类型是4周一次，那么返回datum+4周的日期。。
	 * 
	 * @param datum
	 * @param period
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException
	 * @throws ControllerException 
	 */
	protected Date addPeriodCycle(CreatationParams params, Date datum,
			int period, Context ctx) throws CreateVisitPlanException,
			ServiceException, ControllerException {
		Calendar c = Calendar.getInstance();
		c.setTime(datum);
		c.add(Calendar.WEEK_OF_YEAR, period);
		if (params.getBooleanValue("$visitIntervalSame")) {
			String planTypeCode;
			try {
				planTypeCode = ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_planTypeCode");
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
			PlanType pt = getPlanTypeByCode(planTypeCode,
					params.getBusinessType(), ctx);
			c.add(pt.getFrequency(), pt.getCycle());
			return c.getTime();
		}
		// @@ 如果间隔不一致，需要先找出当前处在哪一个阶段。
		@SuppressWarnings("unchecked")
		List<PlanInstance> planInstances = (List<PlanInstance>) params
				.getObjectValue("$planInstances");
		for (PlanInstance pi : planInstances) {
			CreatationParams cp = new CreatationParams();
			cp.setIntValue("$period", period);
			boolean res = runExpression(cp, pi.getExpression(), ctx);
			if (res) {
				PlanType pt = getPlanTypeByCode(pi.getPlanTypeCode(),
						params.getBusinessType(), ctx);
				c.add(pt.getFrequency(), pt.getCycle());
				Date d1 = c.getTime();
				// @@ 以下代码取得这个表达式表示的最大孕周，基准增加周期后不应超过这个阶段的最大值。
				int tmp = period + pt.getCycle();
				for (;; tmp += pt.getCycle()) {
					cp.setIntValue("$period", tmp);
					if (!runExpression(cp, pi.getExpression(), ctx)) {
						break;
					}
				}
				Date d2 = null;
				for (int i = tmp;; i--) {
					cp.setIntValue("$period", i);
					if (runExpression(cp, pi.getExpression(), ctx)) {
						c.add(Calendar.WEEK_OF_YEAR, i - pt.getCycle() - period);
						d2 = c.getTime();
						break;
					}

				}
				return BSCHISUtil.dateCompare(d1, d2) > 0 ? d2 : d1;
			}

		}
		return null;
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
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression
	 * (chis.source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("'weeks'",
				params.getStringValue("$period"));
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
		int week = BSCHISUtil.getFullWeeks(
				params.getDateValue("$lastMenstrualPeriod"),
				planScheduleItem.getPlanDate());
		plan.put("extend1", week);
		return plan;
	}
}
