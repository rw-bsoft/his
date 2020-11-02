/**
 * @(#)CheckupDictionaryModel.java Created on 2012-4-23 上午10:25:41
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
public class CheckupDictionaryModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public CheckupDictionaryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 检查字典项是否被引用
	 * 
	 * @param dicItemId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkDictionaryItemIsUsed(String dicItemId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(*) as recordNum from ").append(PER_ComboDetail)
				.append(" where itemId like  '%").append(dicItemId)
				.append("%' ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		// parameters.put("itemId", dicItemId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查字典项是否被引用失败！", e);
		}
		long recordNum = 0;
		if (null != rsMap) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum > 0 ? true : false;
	}

	/**
	 * 删除字典项
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removeDictionaryItem(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, PER_CheckupDict);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除字典项失败！", e);
		}
	}

	/**
	 * 检测字典项是否已经存在
	 * 
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkDicItemIsExist(List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, null, PER_CheckupDict);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查字典项是否存在失败！", e);
		}
		if (null != rsList) {
			return rsList.size() > 0 ? true : false;
		}
		return false;
	}

	/**
	 * 保存字典项
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveDicItem(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		try {
			return dao.doSave(op, PER_CheckupDict, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存字典项数验证失败!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存字典项失败！", e);
		}
	}
	
	/***
	 * 获取默认的排序序号。
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public int getDefaultOrderNo(String projectType) throws ModelDataOperationException{
		String hql = new StringBuffer("select max(orderNo) as orderNo from ")
		.append(PER_CheckupDict).append(" where projectType=:projectType").toString();
		Map<String, Object> res =null;
		try {
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("projectType", projectType);
			res = dao.doLoad(hql,params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取默认排序序号失败!",e);
		}
		if(res.get("orderNo")!=null){
			Integer orderNo = (Integer)res.get("orderNo");
			return orderNo+10;
		}
		return 0 ;
	}

	/**
	 * 获取体检全部有效字典
	 * 
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCheckupDict(List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;

		/*
		 * try { rsList = dao .doQuery(cnd, " checkupProjectId ase ",
		 * PER_CheckupDict); } catch (PersistentDataOperationException e) {
		 * throw new ModelDataOperationException( Constants.CODE_DATABASE_ERROR,
		 * "获取体检字典失败", e); }
		 */
		StringBuffer sbBuffer = new StringBuffer(" from ").append(
				PER_CheckupDict).append(
				" where length(checkupDic)>0 order by checkupProjectId asc");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("first", 0);
		parameters.put("max", 999999);
		try {
			rsList = dao.doQuery(sbBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检字典失败", e);
		}
		return rsList;
	}

	/**
	 * 根据体检字典表主键获取一个字典项
	 * 
	 * @param checkupProjectId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getCheckupDictByPkey(String checkupProjectId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PER_CheckupDict, checkupProjectId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检字典项失败！", e);
		}
		return rsMap;
	}
}
