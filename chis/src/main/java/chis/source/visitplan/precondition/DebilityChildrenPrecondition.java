/*
 * @(#)DebilityChildrenPrecondition.java Created on 2012-2-8 下午3:58:09
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DebilityChildrenPrecondition extends AbstractPrecondition {

	public final static Map<Integer, Integer> convertToDay = new HashMap<Integer, Integer>();

	static {
		convertToDay.put(3, 7); // ** 周
		convertToDay.put(2, 30); // ** 月
		convertToDay.put(1, 365); // ** 年
		convertToDay.put(6, 1); // ** 天

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.CD_DC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter, ctd.util.context.Context)
	 */
	@Override
	protected boolean doSatisfy(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException {
		String birth = params.getStringValue("birthday");
		params.setDateValue("$birthday", BSCHISUtil.toDate(birth));

		if (params.getStringValue("debilityReason") == null) {
			// @@ 如果计划已有并且请求里没有体弱原因，取消创建计划。
			try {
				if (false == canCreatePlan(params.getStringValue("recordId"),
						params.getStringValue("planDate"), ctx)) {
					return false;
				}
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException(e.getMessage(), e);
			}
		}
		String planTypeCode = null;
		try {
			if (!Boolean.parseBoolean(ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, "debilityChildrenExceptionalCase"))) {
				planTypeCode = ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_planTypeCode");
			} else {
				List<PlanInstance> planInstances = new VisitPlanModel(
						getDAO(ctx)).getPlanInstanceExpressions(
						params.getBusinessType(), ctx);
				params.setObjectValue("$planInstances", planInstances);
				planTypeCode = getPlanTypeCode(params, planInstances, ctx);
			}
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		if (planTypeCode == null || planTypeCode.equals("")) {
			throw new CreateVisitPlanException("Cann't get plan type code.");
		}

		String prePlanType = params.getStringValue("planTypeCode");
		if (planTypeCode.equals(prePlanType)) {
			return false;
		}
		params.setStringValue("$planTypeCode", planTypeCode);

		if (params.getStringValue("visitDate") != null) {
			Date birthday = BSCHISUtil
					.toDate(params.getStringValue("birthday"));
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(birthday);
			int age;
			try {
				age = Integer.parseInt(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, "childrenRegisterAge"));
				c1.add(Calendar.YEAR, age);
				List<PlanInstance> planInstances = (List<PlanInstance>) params
						.getObjectValue("$planInstances");
				boolean vis = Boolean.valueOf(ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, params.getBusinessType()
								+ "_visitIntervalSame"));
				if (planInstances == null && vis == false) {
					planInstances = new VisitPlanModel(getDAO(ctx))
							.getPlanInstanceExpressions(
									params.getBusinessType(), ctx);
				}
				PlanType pt = adapter.getPlanType(params, planInstances, null,
						ctx);
				c2.setTime(adapter.getOnsetDate(params, pt, ctx));
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
			if (BSCHISUtil.dateCompare(c1.getTime(), c2.getTime()) <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断该次随访是否是最后做的一次，并且原计划生成规则是一月一次的。
	 * 
	 * TODO 我也不理解这个方法是干嘛的。
	 * 
	 * @param recordId
	 * @param planDate
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	private boolean canCreatePlan(String recordId, String planDate, Context ctx)
			throws ModelDataOperationException {
		Date lastVisitedPlanDate = new VisitPlanModel(getDAO(ctx))
				.getLastVisitedPlanDate(recordId, BusinessType.CD_DC);
		if (lastVisitedPlanDate == null) {
			return false;
		}
		if (false == BSCHISUtil.toString(lastVisitedPlanDate).equals(planDate)) {
			return false;
		}
		return true;
	}

	/**
	 * @param params
	 * @param planTypeCodeLst
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	protected List<String> getMatchedPlanTypeCodeList(CreatationParams params,
			List<PlanInstance> planInstances, Context ctx)
			throws CreateVisitPlanException {
		List<String> checkResult = new ArrayList<String>();
		String debilityReason = params.getStringValue("debilityReason");
		StringTokenizer st = new StringTokenizer(debilityReason, ",");
		while (st.hasMoreTokens()) {
			String reason = st.nextToken();
			params.setStringValue("$type", reason);
			for (PlanInstance pi : planInstances) {
				if (runExpression(params, pi.getExpression(), ctx)) {
					checkResult.add(pi.getPlanTypeCode());
				}
				continue;
			}
		}

		if (checkResult == null || checkResult.size() < 1) {
			return null;
		} else {
			return checkResult;
		}
	}

	/**
	 * @param params
	 * @param expression
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("type", params.getStringValue("$type"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Failed to run instance expression.", e);
		}
	}

	/**
	 * 获取计划类型。
	 * 
	 * @param jsonRec
	 * @param planTypeCodeLst
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws CreateVisitPlanException
	 * @throws ServiceException
	 */
	protected String getPlanTypeCode(CreatationParams params,
			List<PlanInstance> planInstances, Context ctx)
			throws CreateVisitPlanException, ServiceException {
		List<String> matchPlanType = getMatchedPlanTypeCodeList(params,
				planInstances, ctx);
		String planTypeCode = null;
		if (matchPlanType == null || matchPlanType.size() == 0) {
			try {
				planTypeCode = ApplicationUtil.getProperty(
						Constants.UTIL_APP_ID, BusinessType.CD_DC
								+ "_planTypeCode");
			} catch (ControllerException e) {
				throw new ServiceException(e);
			}
		} else {
			try {
				planTypeCode = getMaxFrequencePlanTypeCode(matchPlanType, ctx);
			} catch (ModelDataOperationException e) {
				throw new CreateVisitPlanException(
						"failed to get minStep plan type code.", e);
			}
		}
		return planTypeCode;
	}

	/**
	 * 获取频率最大的计划生成方式。
	 * 
	 * @param planTypeCodeList
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	private String getMaxFrequencePlanTypeCode(List<String> planTypeCodeList,
			Context ctx) throws ModelDataOperationException {
		List<PlanType> pts = new VisitPlanModel(getDAO(ctx))
				.getPlanTypes(planTypeCodeList);
		if (pts == null || pts.size() == 0) {
			return null;
		}
		PlanType tmpPt = null;
		for (PlanType pt : pts) {
			if (tmpPt == null) {
				tmpPt = pt;
			} else {
				if (tmpPt.getCycle() * convertToDay.get(tmpPt.getFrequency()) > pt
						.getCycle() * convertToDay.get(pt.getFrequency())) {
					tmpPt = pt;
				}
			}
		}
		return tmpPt.getPlanTypeCode();
	}

}
