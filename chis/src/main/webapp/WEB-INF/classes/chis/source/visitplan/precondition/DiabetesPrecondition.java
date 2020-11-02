/*
 * @(#)DiabetesPrecondition.java Created on 2012-1-12 下午4:39:04
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.util.HashMap;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.adapter.BusinessAdapter;

import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class DiabetesPrecondition extends AbstractPrecondition {

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
//		int visitResult = VISIT_RESULT_SATISFIED;// @@ 随访结果满意
//		params.setIntValue("visitResult", visitResult);

		String fixType = params.getStringValue("fixType");
		if (fixType == null || fixType.length() == 0) {
//			if (null != params.getStringValue("$visitResult")
//					&& params.getStringValue("$visitResult").equals(
//							VISIT_RESULT_CURING)) {
//				params.setIntValue("visitResult", VISIT_RESULT_DISSATISFIED);// @@随访结果不满意
//			}
			// 当前计划适合当前情况 并且不是最后一条记录
//			if (params.getBooleanValue("noChange")) {
//				return false;
//			}else{
//				return true;
//			}
		}
		return true;
	}

	/**
	 * @param cp
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	protected boolean whetherChange(CreatationParams cp, Context ctx)
			throws CreateVisitPlanException {
		
		return false;
	}

	/**
	 * 分组不同，随访结果相同。
	 * 
	 * @throws JSONException
	 * 
	 * @see com.bsoft.hzehr.biz.pub.visitplan.AbstractVisitPlanCreator#runExpression(java.lang.String,
	 *      java.lang.String, ctd.util.context.Context)
	 */
	protected boolean runExpressionNoGroup(CreatationParams cp,
			String expression, Context ctx) throws CreateVisitPlanException {
		expression = expression.replace("visitResult",
				cp.getStringValue("visitResult")).replace("group",
				cp.getStringValue("groupCode"));
		boolean result;
		try {
			result = (Boolean) ExpRunner.run(expression, ctx);
		} catch (ExpException e) {
			throw new CreateVisitPlanException(
					"Failed to run instance expression.", e);
		}
		// 第一次分组与第二次分组不同，随访结果满意，不重新生成计划。
		if (!result) {
			expression = expression.replace("group", "1");
			expression = expression.replace("01", "1");
			expression = expression.replace("02", "1");
			expression = expression.replace("03", "1");
			expression = expression.replace("visitResult",
					cp.getStringValue("visitResult"));

			try {
				return (Boolean) ExpRunner.run(expression, ctx);
			} catch (ExpException e) {
				throw new CreateVisitPlanException(
						"Failed to run instance expression.", e);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	public String getBusinessType() {
		return BusinessType.TNB;
	}
}
