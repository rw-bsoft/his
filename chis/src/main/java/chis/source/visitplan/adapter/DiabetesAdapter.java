/*
 * @(#)DiabetesAdapter.java Created on 2012-1-12 下午5:08:10
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.dic.VisitResult;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DiabetesAdapter extends AbstractBusinessAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getOnsetDate(com.bsoft
	 * .bschis.source.visitplan.CreatationParams, chis.source.visitplan.PlanType,
	 * ctd.util.context.Context)
	 */
	public Date getOnsetDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		int visitResult = params.getIntValue("visitResult");
		if (visitResult == VisitResult.DISSATISFIED) {
			Calendar c = Calendar.getInstance();
			Date visitDate = params.getDateValue("visitDate");
			if (visitDate != null) {
				c.setTime(visitDate);
				c.add(planType.getFrequency(), planType.getCycle());
				return c.getTime();
			}
			// Date lastVisitDate = BSCHISUtil.toDate(params
			// .getStringValue("lastVisitDate"));
			// if (lastVisitDate != null) {
			// c.setTime(lastVisitDate);
			// c.add(planType.getFrequency(), planType.getCycle());
			// return c.getTime();
			// }
		}
		Date fixGroupDate = params.getDateValue("fixGroupDate");
		if (fixGroupDate == null) {
			throw new CreateVisitPlanException("Fix group date cannot be null.");
		}
		// @@ 如果没有前一次随访，评估日即是计划开始日期。
		// @@ 已经做过随访，如果随访日期跟评估日期不是同一天，计划开始日期即是评估日期。否则还需再作判断。
		Calendar c = Calendar.getInstance();
		Date lastVisitDate = BSCHISUtil.toDate(params
				.getStringValue("lastVisitDate"));
		Date lastPlanDate = BSCHISUtil.toDate(params
				.getStringValue("lastPlanDate"));
		if (lastVisitDate == null
				|| (BSCHISUtil.dateCompare(lastVisitDate, fixGroupDate) < 0 && BSCHISUtil
						.dateCompare(lastPlanDate, fixGroupDate) < 0)) {
			return fixGroupDate;
		}
		Date d = fixGroupDate;
		if (BSCHISUtil.dateCompare(lastVisitDate, fixGroupDate) >= 0
				|| BSCHISUtil.dateCompare(lastPlanDate, fixGroupDate) >= 0) {
			c.setTime(BSCHISUtil.dateCompare(lastVisitDate, lastPlanDate) > 0 ? lastVisitDate
					: lastPlanDate);
			c.add(planType.getFrequency(), planType.getCycle());
			d = c.getTime();
		}
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.BusinessAdapter#getCutoutDate(com.
	 * bsoft.bschis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.PlanType, ctd.util.context.Context)
	 */
	public Date getCutoutDate(CreatationParams params, PlanType planType,
			Context ctx) throws CreateVisitPlanException {
		int visitResult = params.getIntValue("visitResult");
		if (visitResult == VisitResult.DISSATISFIED) {
			Calendar c = Calendar.getInstance();
			Date visitDate = params.getDateValue("visitDate");
			if (visitDate != null) {
				c.setTime(visitDate);
			}
			Map<String, Object> optMap = this.getOldPlanType(
					params.getEmpiId(), ctx);
			if(optMap == null){
				c.add(planType.getFrequency(), planType.getCycle());
			}else{
				String frequency = (String) optMap.get("frequency");
				int cycle = (Integer) optMap.get("cycle");
				c.add(Integer.parseInt(frequency), cycle);
			}
			return c.getTime();
		} else {
			// modify by yuhua 糖尿病从不满意变成满意 生成4次随访计划
			System.out.println(params.getDateValue("visitDate"));
			Calendar c = Calendar.getInstance();
			if (params.getStringValue("visitDate") == null) {
				c.add(Calendar.DAY_OF_YEAR, -1);
			}
			c.add(Calendar.YEAR, 1);
			return c.getTime();
		}
		// ManageYearUtil util = new ManageYearUtil(params.getOnsetDate());
		// return util.getDiabetesCurYearEndDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.adapter.AbstractBusinessAdapter#runExpression
	 * (chis.source.visitplan.CreatationParams, java.lang.String,
	 * ctd.util.context.Context)
	 */
	@Override
	protected boolean runExpression(CreatationParams params, String expression,
			Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("group",
				params.getStringValue("groupCode"));
		expression = expression.replace("visitResult",
				params.getStringValue("visitResult"));
		try {
			return (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Failed to run instance expression.", e);
		}
	}

	private Map<String, Object> getOldPlanType(String empiId, Context ctx)
			throws CreateVisitPlanException {
		String hql = new StringBuffer(
				"select a.frequency as frequency,a.cycle as cycle from ")
				.append(PUB_PlanType)
				.append(" a,")
				.append(MDC_DiabetesRecord)
				.append(" b where a.planTypeCode= b.planTypeCode and b.empiId=:empiId")
				.toString();
		BaseDAO dao = new BaseDAO();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empiId", empiId);
		Map<String, Object> resMap = null;
		try {
			resMap = dao.doLoad(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new CreateVisitPlanException("Failed to get old plan type.",
					e);
		}
		return resMap;
	}

}
