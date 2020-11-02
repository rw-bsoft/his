/*
 * @(#)CreateByReserveDateAdapter.java Created on 2012-3-9 下午5:08:55
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Date;
import java.util.List;

import chis.source.Constants;
import chis.source.util.ApplicationUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import ctd.controller.exception.ControllerException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class CreateByReserveDateAdapter extends AbstractBusinessAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.adapter.AbstractBusinessAdapter#getPlanType
	 * (chis.source.visitplan.CreatationParams, java.util.List,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public PlanType getPlanType(CreatationParams params,
			List<PlanInstance> planInstances, PlanType currentType, Context ctx)
			throws CreateVisitPlanException {
		PlanType planType = new PlanType();
		planType.setCycle(999);// @@ 要足够大，保证只能生成一次。
		planType.setFrequency(PlanType.FREQUENCY_YEAR);
		try {
			String strPrecedeDays = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, params.getBusinessType()
							+ "_precedeDays");
			planType.setPrecedeDays(Integer.parseInt(strPrecedeDays));
			String strDelayDays = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, params.getBusinessType()
							+ "_delayDays");
			planType.setDelayDays(Integer.parseInt(strDelayDays));

		} catch (ControllerException e) {
			throw new CreateVisitPlanException();
		}
		return planType;
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
			Context ctx) throws CreateVisitPlanException {
		return params.getDateValue("reserveDate");
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
		return params.getDateValue("reserveDate");
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
		return false;
	}

}
