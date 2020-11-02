/*
 * @(#)CreateByReservDatePrecondition.java Created on 2012-3-9 下午5:14:52
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanMode;
import chis.source.util.ApplicationUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
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
public class CreateByReserveDatePrecondition extends AbstractPrecondition {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BUSINESS_TYPE_ALL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter, ctd.util.context.Context)
	 */
	@Override
	protected boolean doSatisfy(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException {
		String pregnantId = params.getStringValue("pregnantId");
		if (params.getRecordId() == null && pregnantId != null) {
			params.setRecordId(pregnantId);
		}
		return true;
		// try {
		// return false == new VisitPlanModel(getDAO(ctx)).hasVisitPlan(
		// params.getRecordId(), null, params.getBusinessType());
		// } catch (ModelDataOperationException e) {
		// throw new CreateVisitPlanException("Cannot check plan status.", e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.AbstractPrecondition#doFilter
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
		// ** 孕妇高危和产检不执行
		if (businessType.equals(BusinessType.MATERNAL)
				|| businessType.equals(BusinessType.PREGNANT_HIGH_RISK)) {
			return false;
		} else {
			return BUSINESS_TYPE_ALL.equals(getBusinessType())
					&& PlanMode.BY_RESERVED.equals(planMode);
		}
	}
}
