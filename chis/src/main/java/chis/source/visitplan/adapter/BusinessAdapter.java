/*
 * @(#)BusinessAdapter.java Created on 2011-12-29 下午2:32:48
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.schedule.PlanScheduleItem;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 
 *              业务模块和VisitPlanCreator之间的桥接器，使用统一的接口从业务数据中抽取VisitPlanCreator所需的数据。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface BusinessAdapter {

	/**
	 * 计划整体排列开始日期。
	 * 
	 * @param params
	 * @param planType
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 * @throws ControllerException 
	 */
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException, ControllerException;

	/**
	 * 计划整体排列的截止日期。
	 * 
	 * @param params
	 * @param planType
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException;

	/**
	 * 获取计划类型。
	 * 
	 * @param params
	 * @param planInstances
	 * @param currentType
	 *            当前在类型，如果没有可以传null。
	 * @param ctx
	 * @return
	 * @throws ServiceException 
	 * @throws ControllerException 
	 */
	public PlanType getPlanType(CreatationParams params,
			List<PlanInstance> planInstances, PlanType currentType,
			Context ctx) throws CreateVisitPlanException, ServiceException, ControllerException;

	/**
	 * 创建一条随访计划的记录。
	 * 
	 * @param params
	 * @param plan
	 * @return
	 * @throws ServiceException 
	 */
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem plan) throws ServiceException;

	/**
	 * 固定的首次计划的日期，通常这次的计划排列规则与后面的计划不同，需要特别处理的，比如儿童的首次。
	 * @param params 
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	public Date getFixedFirstPlanDate(CreatationParams params) throws CreateVisitPlanException, ServiceException;
}
