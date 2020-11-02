/**
 * @(#)FamilyContractModel.java Created on 2012-11-19 下午03:01:03
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class FamilyContractModel implements BSCHISEntryNames {

	BaseDAO dao;

	public FamilyContractModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 根据字段名和字段值获取相关字段
	 * 
	 * @param fieldCode
	 * @param fieldValue
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFieldsByCodeAndValue(String fieldCode,
			String fieldValue) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "fieldId", "s",
				fieldCode);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "fieldValue", "s",
				fieldValue);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> fields = dao.doList(cnd, null,
					MPM_FieldData);
			return fields;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据字段名和字段值获取相关字段失败！");
		}
	}

	/**
	 * 根据主表Id和字段名获取一个字段
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getOneField(String recordId, String fieldCode)
			throws ModelDataOperationException {
		Map<String, Object> field = null;
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "recordId", "s",
				recordId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "fieldId", "s",
				fieldCode);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> fields = dao.doList(cnd, null,
					MPM_FieldData);
			if (fields != null && fields.size() > 0) {
				field = new HashMap<String, Object>();
				field = fields.get(0);
			}
			return field;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据主表Id和字段名获取一个字段失败！");
		}
	}

	/**
	 * 获取一条数据
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getOneRecord(String recordId)
			throws ModelDataOperationException {
		Map<String, Object> data = null;
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "recordId", "s", recordId);
		try {
			List<Map<String, Object>> fields = dao.doList(cnd, null,
					MPM_FieldData);
			if (fields != null && fields.size() > 0) {
				data = new HashMap<String, Object>();
				for (Map<String, Object> field : fields) {
					data.put((String) field.get("fieldId"),
							field.get("fieldValue"));
				}
				data.put("reocrdId", recordId);
			}
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取一条数据失败！");
		}
	}

	/**
	 * 获取多条信息
	 * 
	 * @param empiId
	 * @param masterplateName
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getAllRecord(String empiId,
			String masterplateName) throws ModelDataOperationException {
		List<Map<String, Object>> datas = null;
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "masterplateName", "s",
				masterplateName);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> records = dao.doList(cnd, null,
					MPM_MasterplateData);
			if (records != null && records.size() > 0) {
				for (Map<String, Object> record : records) {
					String recordId = (String) record.get("recordId");
					Map<String, Object> data = getOneRecord(recordId);
					if (data != null) {
						if (datas == null) {
							datas = new ArrayList<Map<String, Object>>();
						}
						datas.add(data);
					}
				}
			}
			return datas;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取多条数据失败！");
		}
	}

	/**
	 * 保存一条body数据
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> saveBody(String op, Map<String, Object> body)
			throws ModelDataOperationException, ValidateException {
		try {
			String recordId = (String) body.get("recordId");
			Map<String, Object> saveData = new HashMap<String, Object>();
			saveData.putAll((Map<String, Object>) body.get("data"));
			Map<String, Object> req = dao.doSave(op, MPM_MasterplateData, body,
					false);
			if ("update".equals(op)) {
				req.put("recordId", recordId);
			} else {
				recordId = (String) req.get("recordId");
			}
			saveData(op, recordId, saveData);
			return req;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存body数据失败！");
		}
	}

	/**
	 * 保存body中的data数据
	 * 
	 * @param op
	 * @param recordId
	 * @param saveData
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("rawtypes")
	public void saveData(String op, String recordId,
			Map<String, Object> saveData) throws ModelDataOperationException,
			ValidateException {
		Set set = saveData.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Entry b = (Entry) it.next();
			Map<String, Object> data = null;
			if ("create".equals(op)) {
				data = new HashMap<String, Object>();
				data.put("recordId", recordId);
				data.put("fieldId", b.getKey());
				data.put("fieldValue", b.getValue());
			} else {
				data = getOneField(recordId, (String) b.getKey());
				data.put("fieldValue", b.getValue());
			}
			try {
				dao.doSave(op, MPM_FieldData, data, false);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存data数据失败！");
			}
		}
	}

	public void remove(String recordId) throws ModelDataOperationException {
		try {
			dao.doRemove(recordId, MPM_MasterplateData);
			StringBuffer hql = new StringBuffer("delete from ").append(
					MPM_FieldData).append(" where recordId = :recordId");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("recordId", recordId);
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据主表recordId删除数据失败失败！");
		}
	}

	public Map<String, Object> saveContractBase(Map<String, Object> formData,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, EHR_FamilyContractBase, formData, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存签约信息失败", e);
		}
	}

	public Map<String, Object> saveContractProject(Map<String, Object> record,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, EHR_FamilyContractService, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存家庭签约服务项目失败", e);
		}
	}

	public List<Map<String, Object>> getFamilyContractByFCId(String FC_Id)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "FC_Id", "s", FC_Id);
		try {
			return dao.doList(cnd, null, EHR_FamilyContractService);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据FC_Id获取家庭签约服务项目失败", e);
		}
	}

	public void updateFamilyRecord(Map<String, Object> familyData)
			throws ModelDataOperationException, ValidateException {
		try {
			dao.doSave("update", EHR_FamilyRecord, familyData, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("家庭档案保存失败！", e);
		}
	}

	public void deleteDataByFCId(String entryName, String fcId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("delete form ").append(entryName)
				.append(" where FC_Id=:FC_Id");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FC_Id", fcId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据FC_Id删除" + entryName
					+ "表中数据失败！", e);
		}
	}

	public List<Map<String, Object>> getHealthRecordByFamilyId(String familyId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
				familyId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.status", "s", "0");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doList(cnd, null, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据familyId查询个人健康档案失败！", e);
		}
	}

	public List<Map<String, Object>> getFamilyContractByFCIdAndEmpiId(
			String empiId, String fcId) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "FC_Id", "s", fcId);
		List<?> cnd2 = CNDHelper
				.createSimpleCnd("eq", "FS_EmpiId", "s", empiId);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "ServiceFlag", "s", "1");
		List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		try {
			return dao.doList(cnd, null, EHR_FamilyContractService);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据FC_Id和empiId获取家庭签约服务项目失败",
					e);
		}
	}

	public List<Map<String, Object>> getFamilyBaseDataByFCId(String fcId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "FC_Id", "s", fcId);
		try {
			return dao.doList(cnd, null, EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据FC_Id获取家庭签约基本信息失败", e);
		}
	}

	public void deleteFamilyContractById(String FS_Id)
			throws ModelDataOperationException {
		try {
			dao.doRemove(FS_Id, EHR_FamilyContractService);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据FS_Id删除家庭签约服务项目失败", e);
		}
	}

	/**
	 * 根据familyId查询家庭档案
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyRecordByFamilyId(String familyId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "familyId", "s", familyId);
		try {
			return dao.doLoad(cnd, EHR_FamilyRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据familyId获取家庭档案失败", e);
		}
	}

	/**
	 * 根据familyId和master查询户主信息
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHealthRecordByFamilyIdAndMaster(
			String familyId) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
				familyId);
		List<?> cnd2 = CNDHelper
				.createSimpleCnd("eq", "a.masterFlag", "s", "y");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"根据familyId和masterFlag获取户主相关信息失败", e);
		}
	}

	/**
	 * 根据familyId查询家庭档案
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyContractBaseByFamilyIdAndFCSignFlag(
			String familyId) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "F_ID", "s", familyId);
		List<?> cnd2 = CNDHelper
				.createSimpleCnd("eq", "FC_Sign_Flag", "s", "1");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"根据familyId和masterFlag获取户主相关信息失败", e);
		}
	}

	public void updateMasterplateDataByEmpiId(String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(MPM_MasterplateData).append(" set ServiceFlag='2'")
				.append(" where empiId=:empiId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("解约字段数据表数据失败", e);
		}
	}

	public void updateFamilyContractService(String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(EHR_FamilyContractService)
				.append(" set ServiceFlag='2'")
				.append(" where FS_EmpiId=:FS_EmpiId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FS_EmpiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("解约家庭签约服务项目数据失败", e);
		}
	}
}
