/**
 * @(#)PregnantByReserveDateAdapter.java Created on 2012-5-12 下午1:29:45
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Map;

import ctd.service.core.ServiceException;

import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.schedule.PlanScheduleItem;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PregnantByReserveDateAdapter extends CreateByReserveDateAdapter {

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
