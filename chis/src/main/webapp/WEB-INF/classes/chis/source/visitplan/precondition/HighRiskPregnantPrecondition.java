/*
 * @(#)HighRiskPregnantPrecondition.java Created on 2012-1-31 下午3:09:26
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class HighRiskPregnantPrecondition extends AbstractPrecondition {

	private static final int GESTATIONAL_PERIOD_WEEKS = 42;// @@ 总孕周数。

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.PREGNANT_HIGH_RISK;
	}

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
						params.getRecordId(), null, params.getBusinessType(),
						BusinessType.MATERNAL);
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException(
						"Failed to delete exist plan.", e);
			}
		}
		if (params.getBooleanValue("$isPeriodOver") == true) {
			return false;
		}
		return true;
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
		String pregnantId = params.getStringValue("pregnantId");
		if (params.getRecordId() == null && pregnantId != null) {
			params.setRecordId(pregnantId);
		}
		String lmp = params.getStringValue("lastMenstrualPeriod");
		Date date = BSCHISUtil.toDate(lmp);
		Date createDate = BSCHISUtil
				.toDate(params.getStringValue("createDate"));
		// ** 若建档操作则需要生成42周计划，若为随访操作则不需要生成42周计划
		boolean isCreateRecord = params.getBooleanValue("isCreateRecord");
		if (isCreateRecord == true) {
			int period = BSCHISUtil.getFullWeeks(date, createDate);
			if (period > GESTATIONAL_PERIOD_WEEKS) {
				params.setBooleanValue("$isPeriodOver", true);
			}
		} else {
			Calendar clmp = Calendar.getInstance();
			clmp.setTime(date);
			Calendar c = Calendar.getInstance();
			c.setTime(createDate);
			Date planDate = params.getDateValue("planDate");
			int btwDays = 0;
			// ** 计算孕周，如果是提前做的，日期加上提前天数，如果是延后做的，日期减去延后天数
			if(planDate.compareTo(createDate) > 0){
				btwDays = BSCHISUtil.getPeriod(createDate, planDate);
				c.add(Calendar.DAY_OF_YEAR,btwDays);
			}else if (planDate.compareTo(createDate) < 0){
				btwDays = BSCHISUtil.getPeriod(planDate,createDate);
				clmp.add(Calendar.DAY_OF_YEAR, Integer.valueOf(btwDays));
			}
			int period = BSCHISUtil.getFullWeeks(clmp.getTime(), c.getTime());
			if (period >= GESTATIONAL_PERIOD_WEEKS) {
				params.setBooleanValue("$isPeriodOver", true);
			}
		}
		params.setDateValue("$lastMenstrualPeriod", date);
		// @@ 本轮计划总体的开始日期。
		params.setDateValue("$visitPlanBeginDate", date);
		try {
			params.setBooleanValue("$visitIntervalSame", Boolean
					.parseBoolean(ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
							params.getBusinessType() + "_visitIntervalSame")));
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> riskArray = (List<Map<String, Object>>) params
				.getObjectValue("highRisknesses");
		int frequence = getFrequence(riskArray);
		if (frequence == 0) {
			return false;
		}
		params.setIntValue("$frequence", frequence);

		// @@ 在第二次及以后的随访时能取到最近一次随访的开始日期。
		VisitPlanModel vpm = new VisitPlanModel(getDAO(ctx));
		Date lastPlanDate;
		try {
			lastPlanDate = vpm.getLastVisitedPlanDate(pregnantId,
					BusinessType.PREGNANT_HIGH_RISK, BusinessType.MATERNAL);
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Cannot get begin date of last visit plan.", e);
		}
		if (lastPlanDate == null) {
			// @@ 如果没有，可能是第一次随访，取本次随访的开始日期。
			String thisBegin = params.getStringValue("$thisPregWeekBeginDate");
			if (thisBegin != null) {
				lastPlanDate = BSCHISUtil.toDate(thisBegin);
			}
		}
		if (lastPlanDate != null) {
			params.setDateValue("$lastBeginDate", lastPlanDate);
		}
		return true;
	}

	/**
	 * 获取高危计划的间隔时间，取所有危险因素中随访间隔最小的。
	 * 
	 * @param riskList
	 * @return
	 * @throws CreateVisitPlanException
	 * @throws JSONException
	 */
	private int getFrequence(List<Map<String, Object>> riskList)
			throws CreateVisitPlanException {
		int frequence = 0;
		for (Map<String, Object> risk : riskList) {
			int temp;
			try {
				temp = (Integer) SchemaUtil.getValue(MHC_HighRiskVisitReason,
						"frequence", risk.get("frequence"));
			} catch (ValidateException e) {
				throw new CreateVisitPlanException("Cannot get frequence.", e);
			}
			if (temp == 0) {
				continue;
			}
			if (frequence == 0) {
				frequence = temp;
			} else if (temp < frequence) {
				frequence = temp;
			}
		}
		return frequence;
	}
}
