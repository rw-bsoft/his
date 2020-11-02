/**
 * @(#)CheckupSetMealModel.java Created on 2012-4-17 下午1:55:00
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupSetMealModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public CheckupSetMealModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 检查套餐名是否重复
	 * 
	 * @param name
	 * @param manaUnitId
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkSetMealNameIsExist(String name, String manaUnitId,
			String pkey) throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer(
				"select count(*) as recordNum from ").append(PER_Combo).append(
				" where manaUnitId = :manaUnitId and name = :name ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", name);
		parameters.put("manaUnitId", manaUnitId);
		if (StringUtils.isNotEmpty(pkey)) {
			hqlBuffer.append(" and id != :pkey");
			parameters.put("pkey", pkey);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查套餐名是否重复失败！", e);
		}
		long recordNum = 0;
		if (null != rsMap) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum > 0 ? true : false;
	}

	/**
	 * 保存体检套餐
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSetMeal(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_Combo, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检套餐时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检套餐失败！", e);
		}
		return rsMap;
	}

	/**
	 * 验证体检套餐是否存在
	 * 
	 * @param op
	 * @param comboId
	 * @param officeId
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkSetMealDetailIsExist(String op, String comboId,
			String officeId, String pkey) throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer(
				"select count(*) as recordNum from ")
				.append(PER_ComboDetail)
				.append(" where comboId = :comboId and projectOfficeId = :officeId ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("comboId", comboId);
		parameters.put("officeId", officeId);
		if ("update".equals(op)) {
			hqlBuffer.append(" and id != :pkey ");
			parameters.put("pkey", pkey);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "验证体检套餐是否存在失败！", e);
		}
		long recordNum = 0;
		if (null != rsMap) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum > 0 ? true : false;
	}

	/**
	 * 保存体检套餐明细
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSetMealDetail(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_ComboDetail, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_NAME_NOT_MATCHED, "保存体检套餐明细数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检套餐明细失败！", e);
		}
		return rsMap;
	}

	/**
	 * 检查体检套餐是否被使用
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkSetMealIsUsed(String pkey)
			throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer(
				"select count(*) as recordNum  from ").append(
				PER_CheckupRegister).append(" where checkupType= :pkey");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pkey", pkey);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查体检套餐是否被使用失败！", e);
		}
		long recordNum = 0;
		if (null != rsMap) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum > 0 ? true : false;
	}

	/**
	 * 删除体检套餐
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void deleteSetMeal(String pkey) throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer("delete from ").append(
				PER_ComboDetail).append(" where comboId = :pkey");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pkey", pkey);
		try {
			dao.doUpdate(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除体检套餐明细失败！", e);
		}
		try {
			dao.doRemove(pkey, PER_Combo);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除体检套餐失败！", e);
		}
	}

	/**
	 * 判断套餐是否已经被使用
	 * 
	 * @param setMealId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean setMealUsed(String setMealId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recordNum from ")
				.append(PER_CheckupRegister)
				.append(" where checkupType = :setMealId").toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("setMealId", setMealId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断该套餐是否被使用失败！", e);
		}
		long recordNum = 0;
		if (rsMap != null) {
			recordNum = ((Long) rsMap.get("recordNum")).longValue();
		}
		return recordNum > 0 ? true : false;
	}

	public List<Map<String, Object>> checkNeedNode(String val) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "projectType", "s",
				val);
		List<Map<String, Object>> map = null;
		try {
			map = dao.doList(cnd, "projectType",
					PER_CheckupDict);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断字典类型是否应选择失败！", e);
		}
		return map;
	}
}
