/*
 * @(#)SimpleVisitPlanCreator.java Created on 2011-12-28 下午5:39:19
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.schedule;

import java.util.Map;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 默认的随访计划日程表处理器。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DefaultPlanScheduleProcessor implements PlanScheduleProcessor {

	protected BaseDAO getDAO(Context ctx) {
		return new BaseDAO();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.schedule.PlanScheduleProcessor#process(com
	 * .bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter,
	 * chis.source.visitplan.schedule.PlanSchedule,
	 * ctd.util.context.Context)
	 */
	public void process(CreatationParams params, BusinessAdapter adapter,
			PlanSchedule schedule, Context ctx) throws CreateVisitPlanException, ServiceException {
		VisitPlanModel model = new VisitPlanModel(getDAO(ctx));
		int currentMaxSN;
		try {
			currentMaxSN = model.getMaxSN(params.getRecordId(),
					params.getBusinessType());
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException("Cannot get value of max sn.", e);
		}
		for (PlanScheduleItem plan : schedule.getSchedule()) {
			currentMaxSN++;
			params.setIntValue("$sn", currentMaxSN);
			Map<String, Object> map = adapter.createPlan(params, plan);
			try {
				model.addPlan(params, map, ctx);
			} catch (Exception e) {
				throw new CreateVisitPlanException(
						"Insert plan record to db failed.", e);
			}
		}
	}
}
