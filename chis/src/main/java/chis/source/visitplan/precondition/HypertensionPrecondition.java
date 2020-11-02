/*
 * @(#)HypertensionPrecondition.java Created on 2012-1-5 下午5:16:04
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitResult;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.util.context.Context;

/**
 * @description 高血压创建随访计划的前置条件。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class HypertensionPrecondition extends AbstractPrecondition implements
		BSCHISEntryNames {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy
	 * (chis.source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter,
	 * ctd.util.context.Context)
	 */
	public boolean doSatisfy(CreatationParams params, BusinessAdapter adapter,
			Context ctx) throws CreateVisitPlanException {
		// params.setIntValue("$cycle", 0);
		// 将过期未执行的计划置为未访状态
		String phrId = params.getStringValue("phrId");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = sdf.format(new Date());
		if (params.getRecordId() == null && phrId != null)
			params.setRecordId(phrId);
		try {
			new VisitPlanModel(getDAO(ctx)).setOverDatePlan(phrId, endDate,
					params.getBusinessType());
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Cannot set over date plan to unvisit.", e);
		}
		// 删除计划重新生成当天以后的随访计划
		// AbstractPrecondition 回到类里做

		String fixType = (String) params.getStringValue("fixType");
		if (fixType == null || fixType.length() == 0) {
			// 当前计划适合当前情况 并且不是结果不满意情况的最后一条
			// if (whetherChange(params, ctx)) {
			// if (!(null != params.getStringValue("lastestData")
			// && params.getStringValue("lastestData").equals("2") &&
			// visitResult == VisitResult.DISSATISFIED)) {
			// return false;
			// }
			// }
			// @@ 如果前次是随访结果不满意，并且后面还有未访的计划，本次就不需要再生成。
			return !hasPlan(params, ctx);
		}
		return true;
	}

	/**
	 * 检查当前是否还有未访的计划，并且是前次随访结果不满意生成的。
	 * 
	 * @param cp
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	private boolean hasPlan(CreatationParams cp, Context ctx)
			throws CreateVisitPlanException {
		String hql = new StringBuilder("select 1 from ")
				.append(PUB_VisitPlan)
				.append(" where empiId=:empiId and businessType=:businessType and planStatus=:planStatus and extend1=:extend1")
				.toString();
		BaseDAO dao = new BaseDAO();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empiId", cp.getStringValue("empiId"));
		params.put("businessType", cp.getStringValue("businessType"));
		params.put("extend1", cp.getIntValue("visitResult"));//VisitResult.DISSATISFIED
		params.put("planStatus", PlanStatus.NEED_VISIT);
		List<Map<String, Object>> res;
		try {
			res = dao.doQuery(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new CreateVisitPlanException("Failed to get plan instance.",
					e);
		}
		if (res != null && res.size() > 0) {
			return true;
		}
		return false;
	}

//	/**
//	 * @param cp
//	 * @param ctx
//	 * @return
//	 * @throws CreateVisitPlanException
//	 */
//	protected boolean whetherChange(CreatationParams cp, Context ctx)
//			throws CreateVisitPlanException {
//		String hql = new StringBuffer("select a.expression as expression from ")
//				.append(PUB_PlanInstance)
//				.append(" a,")
//				.append(MDC_HypertensionRecord)
//				.append(" b where b.empiId=:empiId and a.planTypeCode= b.planTypeCode and a.instanceType = :instanceType")
//				.toString();
//		BaseDAO dao = new BaseDAO();
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("empiId", cp.getStringValue("empiId"));
//		params.put("instanceType", cp.getStringValue("businessType"));
//		List<Map<String, Object>> res;
//		try {
//			res = dao.doQuery(hql, params);
//		} catch (PersistentDataOperationException e) {
//			throw new CreateVisitPlanException("Failed to get plan instance.",
//					e);
//		}
//		if (res == null) {
//			return false;
//		}
//		String expression = "";
//		if (res.size() == 1) {
//			Map<String, Object> expMap = (Map<String, Object>) res.get(0);
//			if (expMap != null && expMap.size() > 0) {
//				expression = (String) expMap.get("expression");
//			}
//		} else {
//			for (int i = 0; i < res.size(); i++) {
//				Map<String, Object> expMap = (Map<String, Object>) res.get(i);
//				if (expMap != null && expMap.size() > 0) {
//					String expString = (String) expMap.get("expression");
//					String exp = expString.replace("visitResult",
//							cp.getStringValue("visitResult")).replace("group",
//							cp.getStringValue("groupCode"));
//					boolean result = false;
//					try {
//						result = (Boolean) ExpRunner.run(exp, ctx);
//					} catch (ExpException e) {
//						throw new CreateVisitPlanException(
//								"Failed to run instance expression.", e);
//					}
//					if (result) {
//						expression = (String) expMap.get("expression");
//						break;
//					}
//				}
//			}
//		}
//		// String expression = (String) res.get(0).get("expression");
//		return runExpressionNoGroup(cp, expression, ctx);
//	}
//
//	/**
//	 * 分组不同，随访结果相同。
//	 * 
//	 * @throws JSONException
//	 * 
//	 * @see com.bsoft.hzehr.biz.pub.visitplan.AbstractVisitPlanCreator#runExpression(java.lang.String,
//	 *      java.lang.String, ctd.util.context.Context)
//	 */
//	protected boolean runExpressionNoGroup(CreatationParams cp,
//			String expression, Context ctx) throws CreateVisitPlanException {
//		expression = expression.replace("visitResult",
//				cp.getStringValue("visitResult")).replace("group",
//				cp.getStringValue("groupCode"));
//		boolean result;
//		try {
//			result = (Boolean) ExpRunner.run(expression, ctx);
//		} catch (ExpException e) {
//			throw new CreateVisitPlanException(
//					"Failed to run instance expression.", e);
//		}
//		// 第一次分组与第二次分组不同，随访结果满意，不重新生成计划。
//		if (!result) {
//			expression = expression.replace("group", "1");
//			expression = expression.replace("01", "1");
//			expression = expression.replace("02", "1");
//			expression = expression.replace("03", "1");
//			expression = expression.replace("visitResult",
//					cp.getStringValue("visitResult"));
//
//			try {
//				return (Boolean) ExpRunner.run(expression, ctx);
//			} catch (ExpException e) {
//				throw new CreateVisitPlanException(
//						"Failed to run instance expression.", e);
//			}
//		}
//		return result;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	public String getBusinessType() {
		return BusinessType.GXY;
	}

}
