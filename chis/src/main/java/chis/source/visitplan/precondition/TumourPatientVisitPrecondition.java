/**
 * @(#)TumourPatientVisitPrecondition.java Created on 2014-4-27 下午1:44:44
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.visitplan.precondition;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.visitplan.CreatationParams;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import chis.source.visitplan.adapter.BusinessAdapter;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPatientVisitPrecondition extends AbstractPrecondition
		implements BSCHISEntryNames {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.TPV;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * chis.source.visitplan.precondition.AbstractPrecondition#doSatisfy(chis
	 * .source.visitplan.CreatationParams,
	 * chis.source.visitplan.adapter.BusinessAdapter, ctd.util.context.Context)
	 */
	@Override
	protected boolean doSatisfy(CreatationParams params,
			BusinessAdapter adapter, Context ctx)
			throws CreateVisitPlanException, ServiceException {
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

		String group = (String) params.getStringValue("group");
		if (StringUtils.isNotEmpty(group)) {
			return !hasPlan(params, ctx);
		}
		return false;
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
				.append(" where recordId=:recordId and businessType=:businessType and planStatus=:planStatus and extend1=:extend1")
				.toString();
		BaseDAO dao = new BaseDAO();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("recordId", cp.getStringValue("recordId"));
		params.put("businessType", cp.getStringValue("businessType"));
		String group = (String) cp.getStringValue("group");
		params.put("extend1", Integer.parseInt(group));
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
}
