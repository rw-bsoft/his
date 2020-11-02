/*
 * @(#)VisitPlanCreator.java Created on 2011-12-29 下午2:30:46
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan;

import java.util.Map;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface VisitPlanCreator {

	/**
	 * @param params
	 * @param ctx
	 * @return 如果计划未生成将返回null，否则返回本次生成计划所用的计划类型，如果是分段的返回最后一段的类型。
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 * @throws ControllerException 
	 */
	public PlanType create(Map<String, Object> params, Context ctx)
			throws CreateVisitPlanException, ServiceException, ControllerException;
}
