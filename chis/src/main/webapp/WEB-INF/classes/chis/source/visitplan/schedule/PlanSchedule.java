/*
 * @(#)PlanSchedule.java Created on 2011-12-29 上午11:33:04
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.PlanType;

/**
 * @description 随访计划日程表。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class PlanSchedule {

	private List<PlanScheduleItem> schedule = new ArrayList<PlanScheduleItem>();

	/**
	 * @param planDate
	 * @param planType
	 */
	public void add(Date planDate, PlanType planType, CreatationParams params) {
		Date sectionCutOffDate = params.getDateValue("$sectionCutOffDate");
		if (sectionCutOffDate != null
				&& BSCHISUtil.dateCompare(planDate, sectionCutOffDate) > 0) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(planDate);
		PlanScheduleItem plan = new PlanScheduleItem();
		plan.setPlanDate(planDate);
		String planMode = params.getStringValue("$planMode");
		int cycle = planType.getCycle();
		int frequency = planType.getFrequency();
		if ("3".equals(planMode)) {
			Date beginDate = BSCHISUtil.getFirstDayOfMonth(planDate);
			plan.setBeginDate(beginDate);
			int allCycleDays = BSCHISUtil.getAllCycleDaysMonth(planDate, cycle,
					frequency);
			Calendar cDay = Calendar.getInstance();
			cDay.setTime(beginDate);
			cDay.add(Calendar.DAY_OF_YEAR, allCycleDays-1);
			plan.setEndDate((sectionCutOffDate != null && BSCHISUtil
					.dateCompare(cDay.getTime(), sectionCutOffDate) > 0) ? sectionCutOffDate
					: cDay.getTime());
		} else {
			int precedeDaysCon = planType.getPrecedeDays();
			int delayDaysCon = planType.getDelayDays();
			int allCycleDays = BSCHISUtil.getAllCycleDays(planDate, cycle,
					frequency);
			int precedeDays = allCycleDays * precedeDaysCon / 100;
			int delayDays = allCycleDays * delayDaysCon / 100;
			c.add(Calendar.DAY_OF_YEAR, -precedeDays);
			plan.setBeginDate(c.getTime());
			c.add(Calendar.DAY_OF_YEAR, precedeDays + delayDays);
			plan.setEndDate((sectionCutOffDate != null && BSCHISUtil
					.dateCompare(c.getTime(), sectionCutOffDate) > 0) ? sectionCutOffDate
					: c.getTime());
		}
		schedule.add(plan);
	}

	public List<PlanScheduleItem> getSchedule() {
		return schedule;
	}
}