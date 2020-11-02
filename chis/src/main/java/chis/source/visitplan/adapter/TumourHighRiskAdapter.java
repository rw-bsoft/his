/**
 * @(#)TumourHighRiskAdapter.java Created on 2014-4-11 下午3:33:27
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.schedule.PlanScheduleItem;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
@SuppressWarnings("deprecation")
public class TumourHighRiskAdapter extends AbstractBusinessAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(chis.source
	 * .visitplan.CreatationParams, chis.source.visitplan.PlanType,
	 * ctd.util.context.Context)
	 */
	@Override
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException,
			ControllerException {
		String createDate = params.getStringValue("createDate");
		if (createDate == null || createDate.trim().length() == 0) {
			throw new CreateVisitPlanException("Fix group date cannot be null.");
		}
		Date cd = BSCHISUtil.toDate(createDate.substring(0, 10));
		// @@ 如果没有前一次随访，建档日即是计划开始日期。
		// @@ 已经做过随访，如果随访日期跟建档日期不是同一天，计划开始日期即是建档日期。否则还需再作判断。
		Calendar c = Calendar.getInstance();
		Date lastVisitDate = BSCHISUtil.toDate(params
				.getStringValue("lastVisitDate"));
		Date lastPlanDate = BSCHISUtil.toDate(params
				.getStringValue("lastPlanDate"));
		Date lastPlanDate2;
		VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));
		try {
			lastPlanDate2 = vpm.getLastPlanDate(params.getRecordId(),
					BusinessType.THR);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException("Cannot get last begin date.", e);
		}
		if (lastVisitDate == null
				|| (BSCHISUtil.dateCompare(lastVisitDate, cd) < 0 && BSCHISUtil
						.dateCompare(lastPlanDate, cd) < 0)) {
			if (lastPlanDate2 != null
					&& BSCHISUtil.dateCompare(lastPlanDate2, cd) < 0) {
				params.setDateValue("$sectionCutOffDate", BSCHISUtil.getSectionCutOffDate(
						"tumourHighRiskEndMonth", true));
			}
			return cd;
		}
		Date d = cd;
		if (BSCHISUtil.dateCompare(lastVisitDate, cd) >= 0
				|| BSCHISUtil.dateCompare(lastPlanDate, cd) >= 0) {
			// @@ 建档当天已有一次随访，开始日期往后推一次。
			c.setTime(BSCHISUtil.dateCompare(lastVisitDate, lastPlanDate) > 0 ? lastVisitDate
					: lastPlanDate);
			c.add(planType.getFrequency(), planType.getCycle());
			d = c.getTime();
		}
		if (lastPlanDate2 != null
				&& BSCHISUtil.dateCompare(lastPlanDate2, d) < 0) {
			params.setDateValue("$sectionCutOffDate", BSCHISUtil.getSectionCutOffDate(
					"tumourHighRiskEndMonth", true));
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(chis.source
	 * .visitplan.CreatationParams, chis.source.visitplan.PlanType,
	 * ctd.util.context.Context)
	 */
	@Override
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException, ServiceException {
		String createDate = params.getStringValue("createDate");
		if (createDate == null || createDate.trim().length() == 0) {
			throw new CreateVisitPlanException("Fix group date cannot be null.");
		}
		Date cd = BSCHISUtil.toDate(createDate.substring(0, 10));
		Calendar c = Calendar.getInstance();
		c.setTime(cd);
		c.add(Calendar.YEAR, 1);
		// c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression(chis
	 * .source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("group",
				params.getStringValue("managerGroup"));
		expression = expression.replace("tumourType",
				params.getStringValue("highRiskType"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Failed to run instance expression.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#createPlan(chis
	 * .source.visitplan.CreatationParams,
	 * chis.source.visitplan.schedule.PlanScheduleItem)
	 */
	@Override
	public Map<String, Object> createPlan(CreatationParams params,
			PlanScheduleItem planScheduleItem) throws ServiceException {
		Map<String, Object> plan = new HashMap<String, Object>();
		plan.put("empiId", params.getEmpiId());
		plan.put("recordId", params.getRecordId());
		plan.put("businessType", params.getBusinessType());
		plan.put("taskDoctorId", params.getTaskDoctorId());
		if (params.getStringValue("groupCode") != null) {
			plan.put("groupCode", params.getStringValue("managerGroup"));
		}
		if (params.getStringValue("fixGroupDate") != null) {
			plan.put("fixGroupDate",
					BSCHISUtil.toDate(params.getStringValue("createDate")));
		}
		plan.put("planStatus", PlanStatus.NEED_VISIT);
		plan.put("planDate", planScheduleItem.getPlanDate());
		plan.put("beginDate", planScheduleItem.getBeginDate());
		plan.put("endDate", planScheduleItem.getEndDate());
		plan.put("sn", params.getIntValue("$sn"));
		if (null != params.getStringValue("managerGroup")) {
			plan.put("extend1",
					Integer.parseInt(params.getStringValue("managerGroup")));
		}
		if (null != params.getStringValue("highRiskType")) {
			plan.put("extend2", params.getStringValue("highRiskType"));
		}
		return plan;
	}

}
