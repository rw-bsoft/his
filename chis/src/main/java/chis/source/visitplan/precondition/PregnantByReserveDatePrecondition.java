/*
 * @(#)PregnantByReserveDatePrecondition.java Created on 2012-3-9 下午5:14:52
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.util.Calendar;
import java.util.Date;

import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanMode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class PregnantByReserveDatePrecondition extends AbstractPrecondition {

	private static final int GESTATIONAL_PERIOD_WEEKS = 42;// @@ 总孕周数。

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	public String getBusinessType() {
		return BusinessType.MATERNAL;
	}

	public boolean satisfied(CreatationParams params, BusinessAdapter adapter,
			Context ctx) throws CreateVisitPlanException, ServiceException {
		// @@ 如果本前置条件不符合当前档案，直接返回。
		if (!doFilter(params)) {
			return true;
		}
		if (doSatisfy(params, adapter, ctx) == false) {
			return false;
		}
		// @@ 删除原来未做的计划
		if (params.getObjectValue("deleteCurrentPlan") == null
				|| params.getBooleanValue("deleteCurrentPlan")) {
			try {
				new VisitPlanModel(getDAO(ctx)).deleteExistPlan(
						params.getRecordId(), null, params.getBusinessType(),
						BusinessType.MATERNAL);
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException(
						"Failed to delete exist plan.", e);
			}
		}
		if (params.getBooleanValue("$isPeriodOver") == true) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean doSatisfy(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException {
		String pregnantId = params.getStringValue("pregnantId");
		if (params.getRecordId() == null && pregnantId != null) {
			params.setRecordId(pregnantId);
		}
		Date date = BSCHISUtil.toDate(params
				.getStringValue("lastMenstrualPeriod"));
		Date reserveDate = BSCHISUtil.toDate(params
				.getStringValue("reserveDate"));
		// ** 若建档操作则需要生成42周计划，若为随访操作则不需要生成42周计划
		boolean isCreateRecord = params.getBooleanValue("isCreateRecord");
		if (isCreateRecord == true) {
			int period = BSCHISUtil.getFullWeeks(date, reserveDate);
			if (period > GESTATIONAL_PERIOD_WEEKS) {
				params.setBooleanValue("$isPeriodOver", true);
			}
		} else {
			Calendar clmp = Calendar.getInstance();
			clmp.setTime(date);
			Calendar c = Calendar.getInstance();
			c.setTime(reserveDate);
			Date planDate = params.getDateValue("planDate");
			int btwDays = 0;
			// ** 计算孕周，如果是提前做的，日期加上提前天数，如果是延后做的，日期减去延后天数
			if (planDate.compareTo(reserveDate) > 0) {
				btwDays = BSCHISUtil.getPeriod(reserveDate, planDate);
				c.add(Calendar.DAY_OF_YEAR, btwDays);
			} else if (planDate.compareTo(reserveDate) < 0) {
				btwDays = BSCHISUtil.getPeriod(planDate, reserveDate);
				clmp.add(Calendar.DAY_OF_YEAR, Integer.valueOf(btwDays));
			}
			int period = BSCHISUtil.getFullWeeks(clmp.getTime(), c.getTime());
			if (period >= GESTATIONAL_PERIOD_WEEKS) {
				params.setBooleanValue("$isPeriodOver", true);
			}
		}
		params.setDateValue("$lastMenstrualPeriod", date);
		try {
			return false == new VisitPlanModel(getDAO(ctx)).hasVisitPlan(
					params.getRecordId(), null, params.getBusinessType());
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException("Cannot check plan status.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.AbstractPrecondition#doFilter
	 * (chis.source.visitplan.CreatationParams)
	 */
	public boolean doFilter(CreatationParams params)
			throws CreateVisitPlanException, ServiceException {
		String businessType = params.getBusinessType();
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					businessType + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		return PlanMode.BY_RESERVED.equals(planMode)
				&& (businessType.equals(BusinessType.MATERNAL) || businessType
						.equals(BusinessType.PREGNANT_HIGH_RISK));
	}

}
