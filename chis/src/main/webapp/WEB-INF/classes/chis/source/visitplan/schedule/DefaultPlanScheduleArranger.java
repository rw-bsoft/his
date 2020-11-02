/*
 * @(#)DefaultPlanScheduleArranger.java Created on 2011-12-30 下午3:00:29
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 默认的随访计划日程表制定器。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DefaultPlanScheduleArranger implements PlanScheduleArranger {

	protected BaseDAO getDAO(Context ctx) {
		return new BaseDAO();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.schedule.PlanScheduleArranger#arrange(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter, ctd.util.context.Context)
	 */
	public PlanSchedule arrange(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException, ControllerException {
		@SuppressWarnings("unchecked")
		List<PlanInstance> planInstances = (List<PlanInstance>) params
				.getObjectValue("$planInstances");
		boolean vis;
		try {
			vis = Boolean.valueOf(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, params.getBusinessType()
							+ "_visitIntervalSame"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		if (planInstances == null && vis == false) {
			planInstances = new VisitPlanModel(getDAO(ctx))
					.getPlanInstanceExpressions(params.getBusinessType(), ctx);
			params.setObjectValue("$planInstances", planInstances);
		}
		PlanSchedule pSchedule = new PlanSchedule();
		PlanType pt = adapter.getPlanType(params, planInstances, null, ctx);
		if (adapter.getFixedFirstPlanDate(params) != null) {
			Date planDate = adapter.getFixedFirstPlanDate(params);
			params.setDateValue("$nextPlanDate", planDate);
			pt = adapter.getPlanType(params, planInstances, pt, ctx);
			pSchedule.add(planDate, pt, params);
			Calendar c = Calendar.getInstance();
			c.setTime(planDate);
			c.add(Calendar.DAY_OF_YEAR, 1);
			params.setDateValue("$nextPlanDate", c.getTime());
			pt = adapter.getPlanType(params, planInstances, pt, ctx);
		}
		Date onsetDate = adapter.getOnsetDate(params, pt, ctx);
		params.setOnsetDate(onsetDate);
		Date cutoutDate = adapter.getCutoutDate(params, pt, ctx);
		params.setCutoutDate(cutoutDate);
		if (BSCHISUtil.dateCompare(onsetDate, cutoutDate) > 0) {
			throw new CreateVisitPlanException(
					"Onset date is later than cutout data.");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(onsetDate);
		while (BSCHISUtil.dateCompare(c.getTime(), cutoutDate) <= 0) {
			Date planDate = c.getTime();
			params.setDateValue("$nextPlanDate", planDate);
			pt = adapter.getPlanType(params, planInstances, pt, ctx);
			if (pt == null) {
				break;
			}
			params.setObjectValue("$planType", pt);
			pSchedule.add(planDate, pt, params);
			c.add(pt.getFrequency(), pt.getCycle());
			// @@ 保持以后的“day_of_month”跟首次一致。
			if (pt.getFrequency() == PlanType.FREQUENCY_MONTH) {
				if (c.get(Calendar.MONTH) != 1
						&& pt.getFrequency() == PlanType.FREQUENCY_MONTH) {
					Calendar pbd = Calendar.getInstance();
					pbd.setTime(onsetDate);
					int day = pbd.get(Calendar.DAY_OF_MONTH) > c
							.getActualMaximum(Calendar.DAY_OF_MONTH) ? c
							.getActualMaximum(Calendar.DAY_OF_MONTH) : pbd
							.get(Calendar.DAY_OF_MONTH);
					c.set(Calendar.DAY_OF_MONTH, day);
				}
			}
			Date nextSectionOnsetDate = params
					.getDateValue("$nextSectionOnsetDate");
			// @@ 当有阶段截止日期时，并且增加一个计划周期后的时间超过这个截止时间，
			// @@ 那么下一次的计划从阶段截止日期后的一天开始，也就是进入下一个阶段，不再按照当前计划类型中的频率。
			if (nextSectionOnsetDate != null
					&& BSCHISUtil
							.dateCompare(c.getTime(), nextSectionOnsetDate) > 0) {
				c.setTime(nextSectionOnsetDate);
			}
		}
		return pSchedule;
	}
}
