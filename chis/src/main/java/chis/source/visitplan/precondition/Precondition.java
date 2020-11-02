/*
 * @(#)Precondition.java Created on 2012-1-5 下午5:03:46
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;


/**
 * @description 排计划之前的条件检查，以确定是否满足排计划的要求。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface Precondition {

	/**
	 * 创建计划前的前置条件，当返回为true时才可以进入下一步，否则就中断计划的创建。
	 * 
	 * @param params
	 * @param ctx
	 * @param adapter
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	public boolean satisfied(CreatationParams params, BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException;

	/**
	 * 判断该条件是否适用于本次创建过程。
	 * 
	 * @param params
	 * @return 
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	public boolean doFilter(CreatationParams params)
			throws CreateVisitPlanException, ServiceException;
	
	/**
	 * 本条件适用的业务模块。
	 * 
	 * @return
	 */
	public String getBusinessType();
}
