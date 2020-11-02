/**
 * @(#)AbstractPlanTypeAdapter.java Created on 2014-9-19 上午10:34:11
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public abstract class AbstractVisitPlanRelevantModel {

	protected BaseDAO dao;

	public AbstractVisitPlanRelevantModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:获取随访计划类型
	 * @param params
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-19 上午11:20:51
	 * @Modify:
	 */
	protected String getPlanTypeCode(Map<String, Object> params)
			throws ModelDataOperationException {
		String planTypeCode = "";
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			String businessType = (String) params.get("businessType");
			List<PlanInstance> plList = vpModel.getPlanInstanceExpressions(
					businessType, dao.getContext());
			for (PlanInstance pi : plList) {
				if (runExpression(params, pi.getExpression())) {
					planTypeCode = pi.getPlanTypeCode();
					break;
				}
			}
		} catch (CreateVisitPlanException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划类型失败！", e);
		}
		return planTypeCode;
	}

	/**
	 * @param params
	 * @param expression
	 * @return
	 * @throws CreateVisitPlanException
	 */
	protected abstract boolean runExpression(Map<String, Object> params,
			String expression) throws ModelDataOperationException;
}
