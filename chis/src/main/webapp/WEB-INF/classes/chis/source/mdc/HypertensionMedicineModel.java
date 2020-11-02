/**
 * @(#)HypertensionMedicine.java Created on 2012-1-5 上午9:55:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ctd.account.user.User;
import ctd.validator.ValidateException;

/**
 * @description 高血压用药及门诊处方药品情况业务模型类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionMedicineModel implements BSCHISEntryNames {

	protected BaseDAO dao;

	/**
	 * @param dao
	 */
	public HypertensionMedicineModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 高血压用药情况 --高血压查看表单下 用药列表
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getRecordMedicine(String phrId)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				"0000000000000000");
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) dao
					.doQuery(cnd, "recordId",
							BSCHISEntryNames.MDC_HypertensionMedicine);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> objMap = (Map<String, Object>) list.get(i);
				Iterator<Entry<String, Object>> iter = objMap.entrySet()
						.iterator();
				String medicineId = "";
				String medicineName = "";
				while (iter.hasNext()) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter
							.next();
					if ("medicineId".equals(entry.getKey())) {
						medicineId = (String) entry.getValue();
					}
					if ("medicineName".equals(entry.getKey())) {
						medicineName = (String) entry.getValue();
					}
				}
				objMap.put("medicineName", medicineId);
				objMap.put("medicineName_text", medicineName);
				rsList.add(objMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压档案药品失败。");
		}
		return rsList;
	}

	/**
	 * 高血压门诊处方药品信息
	 * 
	 * @param empiId
	 * @param ctx
	 * @return
	 */
	public List<Map<String, Object>> getRecipeMedicineList(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.drugCode as medicineId, a.drugName as medicineName ")
				.append(",a.frequency as medicineFrequency ")
				.append(",a.onesDose as medicineDosage,a.empiId as empiId from ")
				.append(HIS_RecipeDetail).append(" a, ")
				.append(PUB_DrugDirectory).append(" b, ").append(HIS_Recipe)
				.append(" c ").append(" where a.empiId = :empiId")
				.append(" and a.drugCode = b.drugCode and b.mdcUse = '1' ")
				.append(" and a.recipeId = c.recipeId order by c.createDate")
				.toString();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询高血压处方药品信息失败！");
		}
		return rsList;
	}

	/**
	 * 根据phrid查用药信息
	 * 
	 * @param phrid
	 * @return List<Map<String, Object>>
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> findHypertensionMedicineByPhrid(
			String phrid) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrid", "s", phrid);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, null, MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压服药情况失败！");
		}
		return rsList;
	}

	/**
	 * 根据主键recordId取高血压用药记录信息
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionMedicineByRecordId(String recordId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "recordId", "s", recordId);
		Map<String, Object> rsInfo = null;
		try {
			rsInfo = dao.doLoad(cnd, MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压用药详细信息失败！");
		}
		return rsInfo;
	}

	/**
	 * 删除服药情况
	 * 
	 * @param phrId
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteMedicineList(String phrId, String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append("MDC_HypertensionMedicine")
				.append(" where phrId = :phrId and visitId = :visitId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitId", visitId);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除服药情况失败！");
		}
	}

	/**
	 * 保存服药情况
	 * 
	 * @param medicineLst
	 * @param phrId
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHyperMedicine(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_HypertensionMedicine, record, validate);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存服药情况失败！", e);
		}
		return rsMap;
	}

	/**
	 * 删除高血压用药记录
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void deleteHyperMedicineByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除用药记录失败！", e);
		}
	}

	/**
	 * 保存默认前次服药数据
	 * 
	 * @param lastVisitId
	 * @param thisVisitId
	 * @param phrId
	 * @throws ModelDataOperationException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	public boolean defaultMedicine(String lastVisitId, String thisVisitId,
			String phrId) throws ModelDataOperationException {
		HashMap<String, Object> req = new HashMap<String, Object>();
		req.put("schema", MDC_HypertensionMedicine);
		String cnd = "";
		if (lastVisitId.equals("0000000000000000")) {
			cnd = "['and', ['eq', ['$', 'visitId'], ['s', '0000000000000000']], "
					+ "['eq', ['$', 'phrId'], ['s', '" + phrId + "']]]";
		} else {
			cnd = "['eq', ['$', 'visitId'], ['s', '" + lastVisitId + "']]";
		}
		HashMap<String, Object> res = new HashMap<String, Object>();
		try {
			res.put("body", dao.doQuery(CNDHelper.toListCnd(cnd), "",
					MDC_HypertensionMedicine));
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询药品信息失败！", e);
		}

		List<Map<String, Object>> bd2 = (List<Map<String, Object>>) res
				.get("body");
		for (int i = 0; i < bd2.size(); i++) {
			HashMap<String, Object> m = (HashMap<String, Object>) bd2.get(i);
			m.put("lastModifyUser",UserUtil.get(UserUtil.USER_ID));
			m.put("lastModifyDate",new Date());
			m.put("visitId", thisVisitId);
			try {
				dao.doSave("create", MDC_HypertensionMedicine, m, true);
			} catch (ValidateException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存服药情况失败！", e);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存服药情况失败！", e);
			}
		}
		if(bd2.size()>0){
			return true;
		}else{
			return false;
		}
	}
}
