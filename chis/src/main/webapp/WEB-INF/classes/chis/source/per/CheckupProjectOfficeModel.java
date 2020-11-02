/**
 * @(#)CheckupProjectOfficeModel.java Created on 2012-4-17 上午9:21:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupProjectOfficeModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public CheckupProjectOfficeModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取体检科室记录
	 * 
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getProjectOfficeList(List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, null, PER_ProjectOffice);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检科室记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 保存科室信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveProjectOffice(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_ProjectOffice, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存科室信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存科室信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 检查科室是否被使用
	 * 
	 * @param projectOfficeId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean ckeckRemoveProjectOfficeIsUsed(String projectOfficeId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(*) as recordNum from ").append(PER_ComboDetail)
				.append(" where projectOfficeId= :projectOfficeId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("projectOfficeId", projectOfficeId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查科室是否被使用失败！", e);
		}
		long recordNum = 0;
		if (null != rsMap) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum > 0 ? true : false;
	}

	/**
	 * 删除科室记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void deleteProjectOffice(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, PER_ProjectOffice);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除科室失败！", e);
		}
	}
}
