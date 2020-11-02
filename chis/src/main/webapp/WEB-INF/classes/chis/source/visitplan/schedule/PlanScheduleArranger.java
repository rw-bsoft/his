/*
 * @(#)PlanScheduleArranger.java Created on 2011-12-29 下午2:13:55
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.schedule;

import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 随访计划日程表创建器。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface PlanScheduleArranger {

	/**
	 * 制定随访计划日程表。
	 * 
	 * @param params
	 * @param adapter
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 * @throws ControllerException 
	 */
	public PlanSchedule arrange(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException, ControllerException;
}
