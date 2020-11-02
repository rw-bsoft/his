/**
 * @(#)PublicModel.java Created on 2012-1-12 上午9:43:35
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.def;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.RelatedCode;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.validator.ValidateException;


public class DefModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(DefModel.class);

	protected BaseDAO dao = null;

	/**
	 * 
	 * @param dao
	 */
	public DefModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> getDeformityRecordList(String empiId,
			String entryName) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "id", entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人档案列表失败。", e);
		}
		return list;

	}

	public Map<String, Object> loadDeformityRecordData(String id,
			String entryName) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(entryName, id);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人档案信息失败！", e);
		}
		return rsMap;
	}

	public Map<String, Object> getHealthRecord(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		Map<String, Object> map = null;
		try {
			list = dao.doQuery(cnd, null, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案失败。", e);
		}
		if (list.size() > 0) {
			map = (Map<String, Object>) list.get(0);
		}
		return map;
	}

	public int getTrainingEvaluateCount(String defId,String entryName)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.defId", "s",
				defId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, null, entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案失败。", e);
		}
		if (list.size() > 0) {
			return list.size();
		} else {
			return 0;
		}
	}

	public String getParentName(String regionCode)
			throws ModelDataOperationException {
		String sql = "select personName from MPI_DemographicInfo where empiId in (select empiId from EHR_HealthRecord where relaCode=:relaCode and regionCode=:regionCode)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("relaCode", RelatedCode.MASTER);
		parameters.put("regionCode", "");
		List<Map<String, Object>> rsList = null;
		String parentName = "";
		try {
			rsList = dao.doQuery(sql, parameters);
			if (rsList.size() > 0) {
				parentName = (String) ((Map<String,Object>) rsList.get(0)).get("personName");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家长姓名失败。", e);
		}
		return parentName;
	}

	public Map<String, Object> saveDeformityRecord(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public List<Map<String, Object>> getTrainingEvaluateList(String defId,
			String entryName) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "defId", "s", defId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "id", entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估列表失败。", e);
		}
		return list;
	}

	public Map<String, Object> loadTrainingEvaluateData(String id,
			String entryName) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(entryName, id);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估数据失败！", e);
		}
		return rsMap;
	}

	public Map<String, Object> loadMiddleEvaluateData(String id,
			String entryName) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "evaluateId", "s",
				id);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, null, entryName);
			if (null != list && list.size() > 0) {
				rsMap = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估数据失败！", e);
		}
		return rsMap;
	}

	public Map<String, Object> saveTrainingEvaluate(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public Map<String, Object> saveMiddleEvaluate(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public List<Map<String, Object>> getTrainingPlanList(String defId,
			String entryName) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "defId", "s", defId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "id", entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估列表失败。", e);
		}
		return list;

	}

	public Map<String, Object> loadTrainingPlanData(String id, String entryName)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(entryName, id);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估数据失败！", e);
		}
		return rsMap;
	}

	public List<Map<String, Object>> getTrainingPlanRecordList(String planId,
			String entryName) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "planId", "s",
				planId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "id", entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估列表失败。", e);
		}
		return list;

	}

	public Map<String, Object> saveTrainingPlan(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public Map<String, Object> saveTrainingRecord(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public Map<String, Object> loadTrainingRecord(String id, String entryName)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(entryName, id);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估数据失败！", e);
		}
		return rsMap;
	}

	public Map<String, Object> whetherCanClose(String id, String prefix)
			throws PersistentDataOperationException {
		String hql = new StringBuffer(" from DEF_")
				.append(prefix)
				.append("DeformityRecord a where exists (select 1 from DEF_")
				.append(prefix)
				.append("TrainingEvaluate b, DEF_")
				.append(prefix)
				.append("TrainingPlan c where a.id = b.defId and a.id = c.defId and exists (select 1 from DEF_")
				.append(prefix)
				.append("TrainingRecord d where c.id = d.planId)) and a.id=:id")
				.toString();
		Map<String, Object> rsMap = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", id);
		List<Map<String, Object>> l = dao.doQuery(hql, parameters);
		rsMap.put("totalCount", l.size());
		parameters = new HashMap<String, Object>();
		String sql = new StringBuffer(" from DEF_").append(prefix)
				.append("TrainingEvaluate a where defId=:defId order by id")
				.toString();
		parameters.put("defId", id);
		List<Map<String, Object>> list = dao.doQuery(sql, parameters);
		if (list.size() > 0) {
			Map<?, ?> m = (Map<?, ?>) list.get(0);
			rsMap.put("firstScore", m.get("score"));
			m = (Map<?, ?>) list.get(list.size() - 1);
			rsMap.put("lastScore", m.get("score"));
		}
		return rsMap;
	}

	public Map<String, Object> loadTerminalEvaluateData(String id,
			String entryName) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "defId", "s", id);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, null, entryName);
			if (null != list && list.size() > 0) {
				rsMap = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人训练评估数据失败！", e);
		}
		return rsMap;
	}

	public Map<String, Object> saveTerminalEvaluate(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存残疾人数据失败！", e);
		}
		return m;
	}

	public int updateRecordCloseFlag(Map<String,Object> m, String entryName)
			throws ModelDataOperationException {
		StringBuilder sql = new StringBuilder("update ").append(entryName)
				.append(" set closeFlag=:closeFlag where id=:id");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("closeFlag", YesNo.YES);
		parameters.put("id", m.get("defId"));
		int n = 0;
		try {
			n = dao.doUpdate(sql.toString(), parameters);

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家长姓名失败。", e);
		}
		this.deleteDefPlanWorkList(m, entryName);
		return n;
	}

	/**
	 * 通过empiId查询残疾人记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getDefRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.closeFlag", "s",
				YesNo.NO);
		List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

		List<Map<String, Object>> DefRecords;
		try {
			DefRecords = dao.doList(cnd, null, DEF_LimbDeformityRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取残疾人档案信息失败", e);
		}
		return DefRecords;
	}
	
	public List<Map<String, Object>> getDefIntellectRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.closeFlag", "s",
				YesNo.NO);
		List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

		List<Map<String, Object>> DefRecords;
		try {
			DefRecords = dao.doList(cnd, null, DEF_IntellectDeformityRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取残疾人档案信息失败", e);
		}
		return DefRecords;
	}
	
	public List<Map<String, Object>> getDefBrainRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.closeFlag", "s",
				YesNo.NO);
		List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

		List<Map<String, Object>> DefRecords;
		try {
			DefRecords = dao.doList(cnd, null, DEF_BrainDeformityRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取残疾人档案信息失败", e);
		}
		return DefRecords;
	}

	/**
	 * 注销残疾人档案档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutDeformityRecord(Map<String,Object> m, String cancellationReason,
			String deadReason,String entryName)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(entryName).append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", m.get("phrId"));

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销残疾人档案失败！", e);
		}
		
		this.deleteDefEvaluateWorkList(m, entryName);
		this.deleteDefPlanWorkList(m, entryName);
	}
	
	public void logoutSingleDeformityRecord(Map<String,Object> m, String cancellationReason,
			String deadReason,String entryName) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append(entryName)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append("where ").append("id ").append(" = :id ");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", "" + Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason",
				m.get("cancellationReason"));
		parameters.put("id", m.get("id"));

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("SetSubRecordLogoutNormal failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销残疾人档案失败！", e);
		}
		this.deleteDefEvaluateWorkList(m, entryName);
		this.deleteDefPlanWorkList(m, entryName);
	}

	
	/**
	 * 同步个人既往史
	 * @param record
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public void updatePastHistory(Map<String, Object> record,String entryName)
			throws PersistentDataOperationException, ValidateException {
		String deformityPositions[] = StringUtils.trimToEmpty(
				(String) record.get("deformityPosition")).split(",");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get("chis.dictionary.pastHistory");
		} catch (ControllerException e) {
			throw new PersistentDataOperationException("获取字典失败!");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", record.get("empiId"));
		String hql = "";
		
		HashMap<String,Object> m = new HashMap<String,Object>();
		UserRoleToken ur = UserRoleToken.getCurrent();
		if(entryName.indexOf("Limb")>0){
			if (deformityPositions.length > 0) {
				hql = "delete from EHR_PastHistory where empiId=:empiId and (diseaseCode=:diseaseCode or diseaseCode=:diseaseCode1)";
				parameters.put("diseaseCode",
						PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE);
				parameters.put("diseaseCode1",
						PastHistoryCode.PASTHIS_LIMBDEFORMITY_CODE);

				dao.doUpdate(hql, parameters);
				
				String diseaseCode = PastHistoryCode.PASTHIS_LIMBDEFORMITY_CODE;
				String diseaseText = dic.getItem(diseaseCode).getText();
				m.put("empiId", record.get("empiId"));
				m.put("diseaseCode", diseaseCode);
				m.put("diseaseText", diseaseText);
				m.put("pastHisTypeCode", PastHistoryCode.DEFORMITY);
				m.put("recordUnit", ur.getManageUnitId());
				m.put("recordDate", new Date());
				dao.doSave("create", EHR_PastHistory, m, false);
			}
//			}
		}else if(entryName.indexOf("Brain")>0){
			hql = "delete from EHR_PastHistory where empiId=:empiId and (diseaseCode=:diseaseCode1 or diseaseCode=:diseaseCode2)";
			parameters.put("diseaseCode1",
					PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE);
			parameters.put("diseaseCode2",
					PastHistoryCode.PASTHIS_BRAINDEFORMITY_CODE);
			dao.doUpdate(hql, parameters);
			
			String diseaseText = dic.getItem(PastHistoryCode.PASTHIS_BRAINDEFORMITY_CODE).getText();
			m.put("empiId", record.get("empiId"));
			m.put("diseaseCode", PastHistoryCode.PASTHIS_BRAINDEFORMITY_CODE);
			m.put("diseaseText", diseaseText);
			m.put("pastHisTypeCode",PastHistoryCode.DEFORMITY);
			m.put("recordUnit",ur.getManageUnitId());
			m.put("recordDate",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			
			dao.doSave("create", EHR_PastHistory, m, false);
		}else{
			hql = "delete from EHR_PastHistory where empiId=:empiId and (diseaseCode=:diseaseCode1 or diseaseCode=:diseaseCode2)";
			parameters.put("diseaseCode1",
					PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE);
			parameters.put("diseaseCode2",
					PastHistoryCode.PASTHIS_INTELLIGENCEDEFORMITY_CODE);
			
			dao.doUpdate(hql, parameters);
			
			String diseaseText = dic.getItem(PastHistoryCode.PASTHIS_INTELLIGENCEDEFORMITY_CODE).getText();
			m.put("empiId", record.get("empiId"));
			m.put("diseaseCode", PastHistoryCode.PASTHIS_INTELLIGENCEDEFORMITY_CODE);
			m.put("diseaseText", diseaseText);
			m.put("pastHisTypeCode",PastHistoryCode.DEFORMITY);
			m.put("recordUnit",ur.getManageUnitId());
			m.put("recordDate",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			dao.doSave("create", EHR_PastHistory, m, false);
		}
		
	}
	
	/**
	 * 删除个人既往史
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 * @throws ValidateException 
	 */
	public void deletePastHistory(String empiId, String cancellationReason,
			String deadReason,String entryName)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		String sql = "";
		
		if(entryName.indexOf("Limb")>0){
			sql = "delete from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode";
			parameters.put("diseaseCode", PastHistoryCode.PASTHIS_LIMBDEFORMITY_CODE);
		}else if(entryName.indexOf("Brain")>0){
			sql = "delete from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode";
			parameters.put("diseaseCode", PastHistoryCode.PASTHIS_BRAINDEFORMITY_CODE);
		}else if(entryName.indexOf("Intellect")>0){
			sql = "delete from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode ";
			parameters.put("diseaseCode", PastHistoryCode.PASTHIS_INTELLIGENCEDEFORMITY_CODE);
		}
		try {
			dao.doUpdate(sql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除个人既往史失败", e);
		}
		
//		try{
//			Map<String,Object> record = new HashMap<String,Object>();
//			record.put("empiId", empiId);
//			record.put("diseaseCode", PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE);
//			record.put("pastHisTypeCode", PastHistoryCode.DEFORMITY);
//			record.put("diseaseText", DictionaryController.instance().getDic("pastHistory").getItem(PastHistoryCode.PASTHIS_NOTDEFORMITY_CODE).getText());
//			record.put("recordUnit", UserUtil.get(UserUtil.MANAUNIT_ID, dao.getContext()));
//			record.put("recordDate", new Date());
//			record.put("recordUser", UserUtil.get(UserUtil.USER_ID, dao.getContext()));
//			dao.doInsert(EHR_PastHistory, record, false);
//		}catch(PersistentDataOperationException e){
//			throw new ModelDataOperationException(
//					ServiceCode.CODE_DATABASE_ERROR, "添加个人既往史失败", e);
//		}
	}
	
	/**
	 * 加入训练评估提醒
	 * @param m
	 * @param entryName
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public Map<String,Object> insertDefEvaluateWorkList(Map<String,Object> m ,String entryName)
			throws ModelDataOperationException, ValidateException, PersistentDataOperationException {
		Map<String,Object> result;
		String workType = "";
		if(entryName.indexOf("Limb")>0){
			workType = WorkType.DEF_LIMB_EVALUATE;
		}else if(entryName.indexOf("Brain")>0){
			workType = WorkType.DEF_BRAIN_EVALUATE;
		}else if(entryName.indexOf("Intellect")>0){
			workType = WorkType.DEF_INTELLECT_EVALUATE;
		}
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", StringUtils.trimToEmpty((String)m.get("id")));
		parameters.put("empiId", StringUtils.trimToEmpty((String)m.get("empiId")));
		parameters.put("workType", workType);
		parameters.put("count", "1");
		parameters.put("manaUnitId", m.get("manaUnitId"));
		parameters.put("doctorId", m.get("manaDoctorId"));
		Calendar c = Calendar.getInstance();
		parameters.put("beginDate", c.getTime());
		c.add(Calendar.YEAR, 1);
		parameters.put("endDate", c.getTime());
		try{
			result = dao.doInsert(PUB_WorkList, parameters, false);
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "添加训练计划计划失败", e);
		}
		
		return result;
	}

	/**
	 * 加入训练计划提醒
	 * @param m
	 * @param entryName
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public Map<String,Object> insertDefPlanWorkList(Map<String,Object> m,String entryName) throws ModelDataOperationException, ValidateException{
		Map<String,Object> result;
		String workType = "";
		if(entryName.indexOf("Limb")>0){
			workType = WorkType.DEF_LIMB_PLAN;
		}else if(entryName.indexOf("Brain")>0){
			workType = WorkType.DEF_BRAIN_PLAN;
		}else if(entryName.indexOf("Intellect")>0){
			workType = WorkType.DEF_INTELLECT_PLAN;
		}
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("recordId", StringUtils.trimToEmpty((String)m.get("defId")));
		parameters.put("empiId", StringUtils.trimToEmpty((String)m.get("empiId")));
		parameters.put("workType", workType);
		parameters.put("count", "1");
		parameters.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("doctorId", UserUtil.get(UserUtil.USER_ID));
		Calendar c = Calendar.getInstance();
		parameters.put("beginDate", c.getTime());
		c.add(Calendar.YEAR, 1);
		parameters.put("endDate", c.getTime());
		try{
			result = dao.doInsert(PUB_WorkList, parameters, false);
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "添加训练计划计划失败", e);
		}
		return result;
	}
	
	public void deleteDefEvaluateWorkList(Map<String, Object> m,
			String entryName) throws ModelDataOperationException {
		String hql = "delete from PUB_WorkList where workType=:workType and empiId=:empiId";
		Map<String, Object> parameters = new HashMap<String, Object>();
		String workType = "";
		if (entryName.indexOf("Limb") > 0) {
			workType = "10";
		} else if (entryName.indexOf("Brain") > 0) {
			workType = "11";
		} else if (entryName.indexOf("Intellect") > 0) {
			workType = "12";
		}
		parameters.put("empiId",
				StringUtils.trimToEmpty((String) m.get("empiId")));
		parameters.put("workType", workType);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除训练评估提醒失败", e);
		}
	}
	
	public void deleteDefPlanWorkList(Map<String, Object> m, String entryName)
			throws ModelDataOperationException {
		String hql = "delete from PUB_WorkList where workType=:workType and empiId=:empiId";
		Map<String, Object> parameters = new HashMap<String, Object>();
		String workType = "";
		if (entryName.indexOf("Limb") > 0) {
			workType = "13";
		} else if (entryName.indexOf("Brain") > 0) {
			workType = "14";
		} else if (entryName.indexOf("Intellect") > 0) {
			workType = "15";
		}
		parameters.put("empiId",
				StringUtils.trimToEmpty((String) m.get("empiId")));
		parameters.put("workType", workType);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除训练评估提醒失败", e);
		}
	}
	
	public Map<String, Object> loadTerminalEvaluate(List<Object> cnd,String entryName)
	throws ModelDataOperationException {
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(cnd, entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取残疾人档案列表失败。", e);
		}
		return map;
		
	}
	
	/**
	 * 恢复残疾人档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertDeformityRecord(String phrId, String tableName)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append(tableName)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' or cancellationReason is null)")
				.append(" and status = :status1")
				.toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复残疾人档案失败！", e);
		}
	}
	
}
