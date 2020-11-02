/**
 * @(#)VisitPlanCreater.java Created on Nov 25, 2009 9:44:38 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan.schedule;

import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 随访计划日程表处理器，将日程表落实成真正的随访计划。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public interface PlanScheduleProcessor {

	/**
	 * 将日程表落实成真正的随访计划。
	 * 
	 * @param params
	 * @param adapter
	 * @param schedule
	 * @param ctx
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	public void process(CreatationParams params, BusinessAdapter adapter, PlanSchedule schedule, Context ctx)
			throws CreateVisitPlanException, ServiceException;

}
