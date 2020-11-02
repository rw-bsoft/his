/*
 * @(#)ChildrenInquirePrecondition.java Created on 2012-2-7 上午11:39:27
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
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
public class ChildrenInquirePrecondition extends AbstractPrecondition {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.CD_IQ;
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
			throws CreateVisitPlanException, ServiceException {
		String birth = params.getStringValue("birthday");
		params.setDateValue("$birthday", BSCHISUtil.toDate(birth));
		boolean vis;
		try {
			vis = Boolean.valueOf(ApplicationUtil.getProperty(Constants.UTIL_APP_ID,params.getBusinessType() + "_visitIntervalSame"));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		params.setBooleanValue("$visitIntervalSame", vis);
		return true;
	}

}
