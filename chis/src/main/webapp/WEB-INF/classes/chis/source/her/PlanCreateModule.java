/**
 * @(#)HealthEducationModule.java Created on 2012-4-24 下午03:31:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class PlanCreateModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public PlanCreateModule(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 加载计划制定情况数据
	 * 
	 * @param setId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadPlanSet(String setId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.setId", "s", setId);
		try {
			return dao.doLoad(cnd, HER_EducationPlanSet);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载计划制定情况数据失败!", e);
		}
	}

	/**
	 * 保存计划制定情况数据
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePlanSet(String op, Map<String, Object> body)
			throws ServiceException, ModelDataOperationException {
		try {
			return dao.doSave(op, HER_EducationPlanSet, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存计划制定情况数据失败！", e);
		}
	}

	/**
	 * 删除健康教育计划数据
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removePlanSetBySetId(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, HER_EducationPlanSet);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除健康教育计划数据失败！", e);
		}
	}

	/**
	 * 注销健康教育计划
	 * 
	 * @param setId
	 * @throws ModelDataOperationException
	 */
	public void logOutHealthEducationSet(String setId)
			throws ModelDataOperationException {
		String hql = new StringBuffer().append("update ")
				.append(HER_EducationPlanSet)
				.append(" set status = '1' where setId = :setId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("setId", setId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("注销健康教育计划失败！", e);
		}
	}
}
