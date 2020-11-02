/*
 * @(#)HypertensionInquirePrecondition.java Created on 2012-2-2 下午4:33:30
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class HypertensionInquirePrecondition extends AbstractPrecondition {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.HYPERINQUIRE;
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
		params.setIntValue("$cycle", 0);
		String phrId = params.getStringValue("phrId");
		if (params.getRecordId() == null && phrId != null)
			params.setRecordId(phrId);
		try {
			new VisitPlanModel(getDAO(ctx)).setOverDatePlan(phrId,null,
					params.getBusinessType());
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Cannot set over date plan to unvisit.", e);
		}
		return true;
	}

}
