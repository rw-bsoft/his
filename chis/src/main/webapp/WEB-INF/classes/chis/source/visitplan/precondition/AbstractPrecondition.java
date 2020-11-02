/*
 * @(#)AbstractPrecondition.java Created on 2012-1-5 下午5:09:59
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.PlanMode;
import chis.source.util.ApplicationUtil;
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
public abstract class AbstractPrecondition implements Precondition,
		BSCHISEntryNames {

	public static final String BUSINESS_TYPE_ALL = "ALL";

	protected BaseDAO getDAO(Context ctx) {
		return new BaseDAO();
	}

	/**
	 * 检查是否符合条件。
	 * 
	 * @param params
	 * @param ctx
	 * @param adapter
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	protected abstract boolean doSatisfy(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.Precondition#satisfied(com.bsoft
	 * .bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter, ctd.util.context.Context)
	 */
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
						params.getRecordId(), null, params.getBusinessType());
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException(
						"Failed to delete exist plan.", e);
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.Precondition#doFilter(chis.source.visitplan
	 * .CreatationParams)
	 */
	public boolean doFilter(CreatationParams params)
			throws CreateVisitPlanException, ServiceException {
		String businessType = params.getBusinessType();
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					businessType + "_planMode");
			return (businessType.equals(getBusinessType()) || businessType
					.equals(BUSINESS_TYPE_ALL))
					&& (planMode == null ? true : (planMode
							.equals(PlanMode.BY_PLAN_TYPE)||planMode
							.equals(PlanMode.BY_PLAN_MONTH)));
		} catch (ControllerException e) {
			throw new CreateVisitPlanException("获取计划生成方式失败!");
		}
	}
}
