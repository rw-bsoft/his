/*
 * @(#)HypertensionAdapter.java Created on 2011-12-31 下午5:08:05
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
public class HypertensionAdapter extends AbstractBusinessAdapter {

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
			Date lastVisitDate = BSCHISUtil.toDate(params
					.getStringValue("lastVisitDate"));
			if (lastVisitDate != null) {
				c.setTime(lastVisitDate);
				c.add(planType.getFrequency(), planType.getCycle());
				return c.getTime();
			}
		}
		String fixGroupDate = params.getStringValue("fixGroupDate");
		if (fixGroupDate == null || fixGroupDate.trim().length() == 0) {
			throw new CreateVisitPlanException("Fix group date cannot be null.");
		}
		Date fixDate = BSCHISUtil.toDate(fixGroupDate.substring(0, 10));
		// @@ 如果没有前一次随访，评估日即是计划开始日期。
		// @@ 已经做过随访，如果随访日期跟评估日期不是同一天，计划开始日期即是评估日期。否则还需再作判断。
		Calendar c = Calendar.getInstance();
		Date lastVisitDate = BSCHISUtil.toDate(params
				.getStringValue("lastVisitDate"));
		Date lastPlanDate = BSCHISUtil.toDate(params
				.getStringValue("lastPlanDate"));
		if (lastVisitDate == null
				|| (BSCHISUtil.dateCompare(lastVisitDate, fixDate) < 0 && BSCHISUtil
						.dateCompare(lastPlanDate, fixDate) < 0)) {
			return fixDate;
		}
		Date d = fixDate;
		if (BSCHISUtil.dateCompare(lastVisitDate, fixDate) >= 0
				|| BSCHISUtil.dateCompare(lastPlanDate, fixDate) >= 0) {
			// @@ 评估当天已有一次随访，开始日期往后推一次。
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
			Date lastVisitDate = BSCHISUtil.toDate(params
					.getStringValue("lastVisitDate"));
			if (lastVisitDate != null) {
				c.setTime(lastVisitDate);
				Map<String, Object> optMap = this.getOldPlanType(
						params.getEmpiId(), ctx);
				if(optMap == null){
					c.add(planType.getFrequency(), planType.getCycle());
				}else{
					String frequency = (String) optMap.get("frequency");
					int cycle = (Integer) optMap.get("cycle");
					c.add(Integer.parseInt(frequency), cycle);
				}
			} else {
				c.add(Calendar.MONTH, 2);
			}
			return c.getTime();
		}

		String fixGroupDate = params.getStringValue("fixGroupDate");
		if (fixGroupDate == null || fixGroupDate.trim().length() == 0) {
			throw new CreateVisitPlanException("Fix group date cannot be null.");
		}
		Date fixDate = BSCHISUtil.toDate(fixGroupDate.substring(0, 10));
		Calendar c = Calendar.getInstance();
		c.setTime(fixDate);
		c.add(Calendar.YEAR, 1);
		c.add(Calendar.DAY_OF_YEAR, -1);
		return c.getTime();
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

	/**
	 * 获取原来的计划生成类型
	 * 
	 * @param empiId
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	private Map<String, Object> getOldPlanType(String empiId, Context ctx)
			throws CreateVisitPlanException {
		String hql = new StringBuffer(
				"select a.frequency as frequency,a.cycle as cycle from ")
				.append(PUB_PlanType)
				.append(" a,")
				.append(MDC_HypertensionRecord)
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
