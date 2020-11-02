/**
 * @(#)TumourHighRiskPrecondition.java Created on 2014-4-11 下午3:35:52
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
public class TumourHighRiskPrecondition extends AbstractPrecondition implements
		BSCHISEntryNames {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.visitplan.precondition.Precondition#getBusinessType()
	 */
	@Override
	public String getBusinessType() {
		return BusinessType.THR;
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
		String THRID = params.getStringValue("THRID");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = sdf.format(new Date());
		if (params.getRecordId() == null && THRID != null)
			params.setRecordId(THRID);
		try {
			new VisitPlanModel(getDAO(ctx)).setOverDatePlan(THRID, endDate,
					params.getBusinessType());
		} catch (ModelDataOperationException e) {
			throw new CreateVisitPlanException(
					"Cannot set over date plan to unvisit.", e);
		}
		
		String highRiskType = (String) params.getStringValue("highRiskType");
		String managerGroup = (String) params.getStringValue("managerGroup");
		if (StringUtils.isNotEmpty(highRiskType) && StringUtils.isNotEmpty(managerGroup)) {
			return !hasPlan(params, ctx);
		}
		return false;
	}

	/**
	 * 检查当前是否还有未访的计划。
	 * 组别(managerGroup) 1.变了 重新生成计划，2.未变，无计划，生成计划，有计划，则不再生成计划
	 * @param cp
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	private boolean hasPlan(CreatationParams cp, Context ctx)
			throws CreateVisitPlanException {
		String managerGroup = (String) cp.getStringValue("managerGroup");
		String oldMangerGroup =(String)cp.getStringValue("oldManagerGroup");
		if(managerGroup.equals(oldMangerGroup)){
			String hql = new StringBuilder("select 1 from ")
			.append(PUB_VisitPlan)
			.append(" where recordId=:recordId and businessType=:businessType and planStatus=:planStatus and extend1=:extend1 and extend2=:extend2")
			.toString();
			BaseDAO dao = new BaseDAO();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("recordId", cp.getStringValue("recordId"));
			params.put("businessType", cp.getStringValue("businessType"));
			String highRiskType = (String) cp.getStringValue("highRiskType");
			params.put("extend1", Integer.parseInt(managerGroup));
			params.put("extend2", highRiskType);
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
		}
		return false;
	}
}
