/**
 * @(#)HealthRecordModule.java Created on 2011-12-31 下午03:36:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.RecordStatus;
import chis.source.dic.RelatedCode;
import chis.source.dic.YesNo;
import chis.source.empi.EmpiUtil;
import chis.source.log.VindicateLogService;
import chis.source.pub.PublicService;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;

import com.alibaba.fastjson.JSONException;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;

/**
 * @description 健康档案相关的数据库操作。
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class HealthRecordModel implements BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(HealthRecordModel.class);

	BaseDAO dao = null;

	public HealthRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存健康档案。
	 * 
	 * @param op
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws
	 * @throws ValidateException
	 */
	public Map<String, Object> saveHealthRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.EHR_HealthRecord, body,
					false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案保存失败", e);
		}
	}

	/**
	 * 将某人设成户主
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void setMaster(String empiId) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("EHR_HealthRecord")
				.append(" set masterFlag=:masterFlag,relaCode=:relaCode where empiId=:empiId");
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("masterFlag", YesNo.YES);
		args.put("relaCode", RelatedCode.MASTER);
		args.put("empiId", empiId);
		try {
			this.dao.doUpdate(hql.toString(), args);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("健康档案户主关系更新失败", e);
		}
	}

	/**
	 * 根据empiId获取健康档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHealthRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> healthRecord;
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			healthRecord = dao.doLoad(cnd, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child health record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案信息失败。");
		}
		return healthRecord;
	}

	/**
	 * 根据empiId获取健康档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHealthRecordListByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> healthRecord = null;
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					EHR_HealthRecord);
			if (list != null && list.size() > 0) {
				healthRecord = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child health record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案信息失败。");
		}
		return healthRecord;
	}

	public List<Map<String, Object>> getHealthRecordByEmpiId(
			List<String> empiIdList) throws ModelDataOperationException {
		if (empiIdList.size() == 0)
			return new ArrayList<Map<String, Object>>();
		StringBuffer empiIds = new StringBuffer();
		for (String empiId : empiIdList) {
			empiIds.append("\"").append(empiId).append("\",");
		}
		empiIds = empiIds.deleteCharAt(empiIds.length() - 1);
		List<Object> cnd = CNDHelper.createInCnd("a.empiId", empiIdList);
		try {
			return dao.doList(cnd, null, BSCHISEntryNames.EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("健康档案查询失败.", e);
		}
	}

	/**
	 * 根据phrId获取健康档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHealthRecordByPhrId(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> healthRecord;
		try {
			healthRecord = dao.doLoad(EHR_HealthRecord, phrId);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get child health record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案信息失败。");
		}
		return healthRecord;
	}

	/**
	 * 根据网格地址查询家庭档案编号
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getFamilyIdByRegionCode(String regionCode)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.regionCode", "s",
					regionCode);
			String hql="from "+EHR_FamilyRecord+" a where a.regionCode=:regionCode";
			Map<String, Object> parameters=new HashMap<String, Object>();
			parameters.put("regionCode", regionCode);
			parameters.put("first", 0);
			parameters.put("max", 1);
			List<Map<String, Object>> familyRecords = dao.doQuery(hql, parameters);
			if (familyRecords != null && familyRecords.size() > 0) {
				return (String) familyRecords.get(0).get("familyId");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get family record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭档案信息失败。");
		}
		return "";
	}

	/**
	 * 根据idCard获取健康档案信息
	 * 
	 * @param idCard
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHealthRecordByIdCard(String idCard,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> healthRecord = null;
		try {
			List<?> queryCnd = CNDHelper.createSimpleCnd("eq", "b.idCard", "s",
					EmpiUtil.idCard15To18(idCard.toUpperCase()));
			List<Map<String, Object>> relist = dao.doList(queryCnd, "",
					EHR_HealthRecord);
			if (relist.size() > 0) {
				healthRecord = relist.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("failed to get relative health record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康档案信息失败");
		}
		return healthRecord;
	}

	/**
	 * 根据phrId查询EmpiId
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getEmpiId(String phrId) throws ModelDataOperationException {
		String hql = new StringBuffer("select empiId as empiId from ")
				.append(EHR_HealthRecord).append(" where phrId = :phrId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		String empiId = null;
		try {
			Map<String, Object> map = dao.doLoad(hql, parameters);
			if (map != null && !map.isEmpty()) {
				empiId = (String) map.get("empiId");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get empiId from healrhRecord .", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取empiId失败。");
		}
		return empiId;
	}

	/**
	 * <chb>根据EmpiId 查询 phrId
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getPhrId(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("select phrId as phrId from ")
				.append(EHR_HealthRecord).append(" where empiId = :empiId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		String phrId = null;
		try {
			Map<String, Object> map = dao.doLoad(hql, parameters);
			if (map != null && !map.isEmpty()) {
				phrId = (String) map.get("phrId");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取phrId失败。", e);
		}
		return phrId;
	}

	/**
	 * 根据ID号删除个人既往史和个人主要问题数据
	 * 
	 * @param pastIds
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public void delPastHistory(List pastIds) throws ModelDataOperationException {
		String delPast = new StringBuilder("delete from ")
				.append(EHR_PastHistory)
				.append(" where pastHistoryId = :pastHistoryId").toString();
		String proPast = new StringBuilder("delete from ")
				.append(EHR_PersonProblem)
				.append(" where pastHistoryId = :pastHistoryId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (int i = 0; i < pastIds.size(); i++) {
			String pastId = (String) pastIds.get(i);
			parameters.put("pastHistoryId", pastId);
			try {
				dao.doUpdate(delPast, parameters);
				dao.doUpdate(proPast, parameters);
			} catch (PersistentDataOperationException e) {
				logger.error(
						"delete EHR_PastHistory and EHR_PersonProblem failed.",
						e);
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "删出个人既往史和个人主要问题失败。");
			}
		}
	}

	/**
	 * 保存个人既往史EHR_PastHistory
	 * 
	 * @param req
	 * @param res
	 * @param record
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public String savePastHistory(String op, Map<String, Object> body,
			VindicateLogService vLogService)
			throws ModelDataOperationException, ServiceException {
		String pastHistoryId = "";
		try {
			Map<String, Object> genValues = dao.doSave(op, EHR_PastHistory,
					body, true);
			if (op.equals("create")) {
				pastHistoryId = (String) genValues.get("pastHistoryId");
			} else {
				pastHistoryId = (String) body.get("pastHistoryId");
			}
			// 增加日志
			String empiId = (String) body.get("empiId");
			vLogService.saveVindicateLog(EHR_PastHistory, op, pastHistoryId,
					dao, empiId);
		} catch (PersistentDataOperationException e) {
			logger.error("save EHR_PastHistory record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存个人既史EHR_PastHistory失败！");
		}
		return pastHistoryId;
	}

	/**
	 * 收集录入个人主要问题所需要的数据
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> collectPersonProblemData(Map<String, Object> body)
			throws ModelDataOperationException, ValidateException {
		String pastHistoryId = (String) body.get("pastHistoryId");
		String sickRecordId = (String) body.get("sickRecordId");
		String vestingCode = (String) body.get("vestingCode");
		Schema sc;
		try {
			sc = SchemaController.instance().get(EHR_PersonProblem);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		List<SchemaItem> items = sc.getItems();
		String result = null;
		for (SchemaItem si : items) {
			String name = si.getId();
			if (name.equals("vestingCode")) {
				result = si.toDisplayValue(vestingCode);
			}
		}
		Map<String, Object> reBody = new HashMap<String, Object>();
		String cycleId = (String) getLifeCycleId(body).get("cycleId");
		reBody.put("cycleId", cycleId);
		reBody.put("problemName", body.get("diseaseText"));
		reBody.put("result", result);
		reBody.put("recordUnit", body.get("recordUnit"));
		reBody.put("recordUser", body.get("recordUser"));
		reBody.put("recordDate", body.get("recordDate"));
		reBody.put("occurDate", body.get("startDate"));
		reBody.put("solveDate", body.get("endDate"));
		reBody.put("pastHistoryId", pastHistoryId);
		reBody.put("empiId", body.get("empiId"));
		if (sickRecordId != null && !sickRecordId.equals("")) {
			reBody.put("sickRecordId", sickRecordId);
		}
		return reBody;
	}

	/**
	 * 根据EMPI找出出生日期算出年龄，算出生命周期，返回周期问题。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	public Map<String, Object> getLifeCycleId(Map<String, Object> body)
			throws ModelDataOperationException {
		int age = getAge(body);

		List<?> startCnd = CNDHelper.createSimpleCnd("le", "startAge", "i",
				Integer.valueOf(age));
		List<?> endCnd = CNDHelper.createSimpleCnd("ge", "endAge", "i",
				Integer.valueOf(age));
		List<?> cnd = CNDHelper.createArrayCnd("and", startCnd, endCnd);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, null, EHR_LifeCycle);
		} catch (PersistentDataOperationException e) {
			logger.error("find record of lifeCycle is failed. message:", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询生命周期失败！");
		}
		if (list == null || list.size() == 0) {
			logger.error("no lifeCycle Id found for age :" + age);
			throw new ModelDataOperationException(Constants.CODE_NOT_FOUND,
					"该年龄没有对应的生命周期定义");
		}
		Map<String, Object> lifeCycleMap = new HashMap<String, Object>();
		lifeCycleMap.put("cycleId", (String) list.get(0).get("cycleId"));
		lifeCycleMap.put("cycleName", (String) list.get(0).get("cycleName"));
		return lifeCycleMap;
	}

	/**
	 * 计算年龄
	 * 
	 * @param age
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("deprecation")
	public int getAge(Map<String, Object> body)
			throws ModelDataOperationException {
		String occurDate = (String) body.get("occurDate");
		if (occurDate == null) {
			occurDate = new Date().toLocaleString();
		}
		body.put("occurDate", occurDate);
		Map<String, Object> lcRecq = new HashMap<String, Object>();
		lcRecq.put("body", body);
		int age = 0;
		try {
			age = PublicService.getAge(lcRecq, new HashMap<String, Object>(),
					ContextUtils.getContext());
		} catch (PersistentDataOperationException e) {
			logger.error("calculate result of Age is failed. message:", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "计算年龄时出错。");
		}
		if (age < 0) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL, "该人没有登记出生日期！");
		}
		return age;
	}

	/**
	 * 新增个人既往史 其中包括（个人既往史、个人主要问题、家庭主要问题）
	 * 
	 * @param req
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addPersonalPastHistory(String op, Map<String, Object> body,
			Context ctx, VindicateLogService vLogService)
			throws ModelDataOperationException, ServiceException {
		List pastIds = (List) body.get("delPastId");

		// 如果已经有数据存在，先删掉已经存在的数据
		if (pastIds != null && pastIds.size() > 0) {
			delPastHistory(pastIds);
		}

		List records = (List) body.get("record");
		if (records == null || records.size() == 0) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL, "个人既往史数据为空！");
		}

		for (int i = 0; i < records.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) records
					.get(i);
			String pastHistoryId = savePastHistory(op, record, vLogService);
			String pastHisTypeCode = (String) record.get("pastHisTypeCode");
			String diseaseCode = (String) record.get("diseaseCode");
			if (!PastHistoryCode.FATHER.equals(pastHisTypeCode)
					&& !PastHistoryCode.MOTHER.equals(pastHisTypeCode)
					&& !PastHistoryCode.BROTHER.equals(pastHisTypeCode)
					&& !PastHistoryCode.CHILDREN.equals(pastHisTypeCode)
					&& !PastHistoryCode.HEREDOPTHIA.equals(pastHisTypeCode)
					&& !diseaseCode.equals(pastHisTypeCode
							+ PastHistoryCode.DISEASE_FREE)) {
				record.put("pastHistoryId", pastHistoryId);
				Map<String, Object> personalProblemMap = collectPersonProblemData(record);// 收集保存个人主页问题所需要的数据
				savePersonalAndFamilyProblem(op, personalProblemMap);
			}
		}
	}

	/**
	 * 更新个人既往史 其中包括（个人既往史、个人主要问题、家庭主要问题）
	 * 
	 * @param req
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public void updatePersonalPastHistory(String op, Map<String, Object> body,
			Context ctx, VindicateLogService vLogService)
			throws ModelDataOperationException, ServiceException {
		savePastHistory(op, body, vLogService);
		Map<String, Object> personalProMap = getPersonalProblemByPastHistoryId(body);
		if (personalProMap != null) {
			String sickRecordId = (String) personalProMap.get("sickRecordId");
			body.put("sickRecordId", sickRecordId);
			Map<String, Object> personalMap = collectPersonProblemData(body);// 收集保存个人主要问题问题所需要的数据
			savePersonalAndFamilyProblem(op, personalMap);
		}
	}

	/**
	 * 保存个人主要问题
	 * 
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> savePersonalProblem(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		try {
			return dao.doSave(op, EHR_PersonProblem, body, false);
		} catch (PersistentDataOperationException e) {
			logger.error("save personalProblem record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存个人主要问题失败！");
		}
	}

	/**
	 * 保存家庭主要问题
	 * 
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveFamilyProblem(String op, Map<String, Object> healthRecord,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		String familyId = (String) healthRecord.get("familyId");
		String personName = (String) healthRecord.get("personName");
		if (familyId == null || familyId.equals("")) {
			return;
		}
		Map<String, Object> reBody = new HashMap<String, Object>();
		reBody.put("familyId", familyId);
		reBody.put("happenDate", body.get("occurDate"));
		reBody.put("solveDate", body.get("solveDate"));
		reBody.put("empiId", body.get("empiId"));
		reBody.put("problemName", personName + "的问题");
		reBody.put("description", body.get("problemName"));
		reBody.put("solveResult", body.get("result"));
		reBody.put("recordUnit", body.get("recordUnit"));
		reBody.put("recordUser", body.get("recordUser"));
		reBody.put("recordDate", body.get("recordDate"));
		try {
			dao.doSave(op, EHR_FamilyProblem, reBody, false);
		} catch (Exception e) {
			logger.error("save EHR_FamilyProblem record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭主要问题失败！");
		}
	}

	/**
	 * 查询个人主要问题
	 * 
	 * @param pastHistoryId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getPersonalProblemByPastHistoryId(
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		String pastHistoryId = (String) body.get("pastHistoryId");
		List<Map<String, Object>> list = null;
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "pastHistoryId", "s",
					pastHistoryId);
			list = dao.doList(cnd, null, EHR_PersonProblem);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("search EHR_HealthRecord record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭主要问题时查询个人健康档案失败！");
		}
		return null;
	}

	/**
	 * 保存个人主要问题和家庭主要问题
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> savePersonalAndFamilyProblem(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> data = savePersonalProblem(op, body);// 先保存个人主要问题
		String empiId = (String) body.get("empiId");
		if ("create".equalsIgnoreCase(op)) {
			Map<String, Object> healthRecord = getHealthRecordListByEmpiId(empiId);// 查询个人健康档案信息
			if (healthRecord != null) {
				saveFamilyProblem(op, healthRecord, body);// 录入家庭主要问题
			}
		}
		return data;
	}

	/**
	 * 根据empiId查询个人既往史数据
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public List<Map<String, Object>> getPastHistoryByEmpiId(String empiId)
			throws ModelDataOperationException, ValidateException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			return dao.doList(cnd, null, EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			logger.error("search EHR_HealthRecord record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人既往史失败！");
		}
	}

	/**
	 * 根据empiId pastHisTypeCode(既往史类别) diseaseCode(结果描述编码)索定一条个人既往史记录
	 * 
	 * @param empiId
	 * @param pastHisTypeCode
	 * @param diseaseCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPastHistory(String empiId,
			String pastHisTypeCode, String diseaseCode)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append(EHR_PastHistory)
				.append(" where empiId = :empiId")
				.append(" and pastHisTypeCode = :pastHisTypeCode")
				.append(" and diseaseCode = :diseaseCode").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("pastHisTypeCode", pastHisTypeCode);
		parameters.put("diseaseCode", diseaseCode);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人既往史失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return rsList.get(0);
		} else {
			return null;
		}
	}

	public List<Map<String, Object>> getPastHistoryWithOldPeople(String empiId)
			throws ModelDataOperationException, ValidateException {
		List<Map<String, Object>> list = null;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.pastHisTypeCode",
					"s", PastHistoryCode.SCREEN);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			list = dao.doList(cnd, null, EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			logger.error("search EHR_HealthRecord record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人既往史失败！");
		}
		return list;
	}

	// 收集更新高血压、孕产妇、儿童所需数据
	@SuppressWarnings("rawtypes")
	public Map collectRelatedRecord(String empiId)throws ModelDataOperationException, ValidateException {
		List<Map<String, Object>> pastHistoryList = getPastHistoryByEmpiId(empiId);
		if (pastHistoryList == null) {
			return null;
		}
		List<String> code03 = new ArrayList<String>();
		List<String> code03Keys = new ArrayList<String>();
		List<String> code05 = new ArrayList<String>();
		List<String> code06 = new ArrayList<String>();
		List<String> code07 = new ArrayList<String>();
		List<String> code14 = new ArrayList<String>();
		List<String> code14Keys = new ArrayList<String>();
		Dictionary healthHis;
		try {
			healthHis = DictionaryController.instance().get("chis.dictionary.pastHistory");
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		String otherDeformity = "";
		String otherAllergicHistory = "";
		String otherPastHistory = "";
		String mentalDisease = "";
		String geneticHistory = "";
		String familyHistoryGXY="";
		
		for (int i = 0; i < pastHistoryList.size(); i++) {
			Map<?, ?> m = (Map<?, ?>) pastHistoryList.get(i);
			String diseaseCode = (String) m.get("diseaseCode");
			String pastHisTypeCode = (String) m.get("pastHisTypeCode");
			String diseaseText = (String) m.get("diseaseText");
			DictionaryItem it = healthHis.getItem(diseaseCode);
			if (PastHistoryCode.ALLERGIC.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_ALLERGIC_CODE.equals(diseaseCode)) {
					code03.add(diseaseText);
					if (otherAllergicHistory.equals("")) {
						otherAllergicHistory = diseaseText;
					} else {
						otherAllergicHistory += "," + diseaseText;
					}
					if (!code03Keys.contains(PastHistoryCode.PASTHIS_ALLERGIC_CODE)) {
						code03Keys.add(diseaseCode);
					}
				} else {
					code03.add(it.getText());
					code03Keys.add(diseaseCode);
				}
				continue;
			}
			if (PastHistoryCode.SCREEN.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE.equals(diseaseCode)) {
					code05.add("1");
				} else if (PastHistoryCode.PASTHIS_SCREEN_CARDIOPATHY.equals(diseaseCode)) {
					code05.add("2");
				} else if (PastHistoryCode.PASTHIS_SCREEN_RENAL.equals(diseaseCode)) {
					code05.add("3");
				} else if (PastHistoryCode.PASTHIS_SCREEN_LIVER.equals(diseaseCode)) {
					code05.add("4");
				} else if (PastHistoryCode.PASTHIS_SCREEN_HYPERTENSION.equals(diseaseCode)) {
					code05.add("5");
				} else if (PastHistoryCode.PASTHIS_SCREEN_ANAEMIA.equals(diseaseCode)) {
					code05.add("6");
				} else if (PastHistoryCode.PASTHIS_SCREEN_DIABETES.equals(diseaseCode)) {
					code05.add("7");
				} else if (PastHistoryCode.PASTHIS_SCREEN_OTHER.equals(diseaseCode)) {
					code05.add("8");
					otherPastHistory = diseaseText;
				}
				continue;
			}
			if (PastHistoryCode.OPERATION.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_OPERATION_EXISTENT.equals(diseaseCode)) {
					code06.add(diseaseText);
				} else {
					code06.add(it.getText());
				}
				continue;
			}
			if (PastHistoryCode.TRANSFUSION.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_TRANSFUSION_EXISTENT.equals(diseaseCode)) {
					code07.add(diseaseText);
				} else {
					code07.add(it.getText());
				}
				continue;
			}
			if (PastHistoryCode.FATHER.equals(pastHisTypeCode)) {
				familyHistoryGXY=this.buildHistoryGXY(familyHistoryGXY,diseaseCode);
				if (PastHistoryCode.PASTHIS_FATHER_PSYCHOSIS.equals(diseaseCode)) {
					mentalDisease = "2";
				}
				continue;
			}
			if (PastHistoryCode.MOTHER.equals(pastHisTypeCode)) {
				familyHistoryGXY=this.buildHistoryGXY(familyHistoryGXY,diseaseCode);
				if (PastHistoryCode.PASTHIS_MOTHER_PSYCHOSIS.equals(diseaseCode)) {
					mentalDisease = "2";
				}
				continue;
			}
			if (PastHistoryCode.BROTHER.equals(pastHisTypeCode)) {
				familyHistoryGXY=this.buildHistoryGXY(familyHistoryGXY,diseaseCode);
				if (PastHistoryCode.PASTHIS_BROTHER_PSYCHOSIS.equals(diseaseCode)) {
					mentalDisease = "2";
				}
				continue;
			}
			if (PastHistoryCode.CHILDREN.equals(pastHisTypeCode)) {
				familyHistoryGXY=this.buildHistoryGXY(familyHistoryGXY,diseaseCode);
				if (PastHistoryCode.PASTHIS_CHILDREN_PSYCHOSIS.equals(diseaseCode)) {
					mentalDisease = "2";
				}
				continue;
			}
			if (PastHistoryCode.HEREDOPTHIA.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_HEREDOPTHIA_CODE.equals(diseaseCode)) {
					geneticHistory = "1";
				}
				continue;
			}
			if (PastHistoryCode.DEFORMITY.equals(pastHisTypeCode)) {
				if (PastHistoryCode.PASTHIS_DEFORMITY_OTHER.equals(diseaseCode)) {
					code14.add(diseaseText);
					if (otherDeformity.equals("")) {
						otherDeformity = diseaseText;
					} else {
						otherDeformity += "," + diseaseText;
					}
					if (!code14Keys.contains(PastHistoryCode.PASTHIS_DEFORMITY_OTHER)) {
						code14Keys.add(diseaseCode);
					}
				} else {
					code14.add(it.getText());
					code14Keys.add(diseaseCode);
				}
				continue;
			}
		}
		Map<String, Object> mixMap = new HashMap<String, Object>();
		Map<String, Object> hypertensionRecordCnd = new HashMap<String, Object>();
		if(familyHistoryGXY.indexOf("01")>=0 && !familyHistoryGXY.equals(",01")){
			familyHistoryGXY=familyHistoryGXY.replace(",01","");
		}
		hypertensionRecordCnd.put("familyhistroy",familyHistoryGXY.replaceAll("0","").substring(1));
		hypertensionRecordCnd.put("empiId", empiId);
		mixMap.put("hypertensionRecordCnd", hypertensionRecordCnd);

		String familyHistory = "";
		if (!geneticHistory.equals("")) {
			if (!mentalDisease.equals("")) {
				familyHistory = geneticHistory + "," + mentalDisease;
			} else {
				familyHistory = geneticHistory;
			}
		} else {
			familyHistory = mentalDisease;
		}
		Map<String, Object> pregnantRecordCnd = new HashMap<String, Object>();
		pregnantRecordCnd.put("familyHistory", familyHistory);
		pregnantRecordCnd.put("operationHistory", this.join(code06));
		pregnantRecordCnd.put("pastHistory", this.join(code05, ",", true));
		pregnantRecordCnd.put("otherPastHistory", otherPastHistory);
		pregnantRecordCnd.put("allergicHistory", this.join(code03));
		pregnantRecordCnd.put("empiId", empiId);
		mixMap.put("pregnantRecordCnd", pregnantRecordCnd);

		Map<String, Object> childrenHealthCardCnd = new HashMap<String, Object>();
		childrenHealthCardCnd.put("allergicHistoryKey",this.join(code03Keys, ",", true));
		childrenHealthCardCnd.put("deformityKey",this.join(code14Keys, ",", true));
		childrenHealthCardCnd.put("otherDeformity", otherDeformity);
		childrenHealthCardCnd.put("otherAllergicHistory", otherAllergicHistory);
		childrenHealthCardCnd.put("empiId", empiId);
		mixMap.put("childrenHealthCardCnd", childrenHealthCardCnd);
		Map<String, Object> disabilityMonitorCnd = new HashMap<String, Object>();
		disabilityMonitorCnd.put("deformityKey",this.join(code14Keys, ",", true));
		disabilityMonitorCnd.put("empiId", empiId);
		mixMap.put("disabilityMonitorCnd", disabilityMonitorCnd);

		return mixMap;
	}
	public String buildHistoryGXY(String familyHistoryGXY,String diseaseCode){
		String bm=diseaseCode.substring(2);
		if(bm.equals("01")||bm.equals("02")||bm.equals("03")||bm.equals("04")||bm.equals("07")){
			if(familyHistoryGXY.indexOf(bm) ==-1){
				familyHistoryGXY+=","+bm;
			}
		}else{
			if(familyHistoryGXY.indexOf("99") ==-1){
				familyHistoryGXY+=","+99;
			}
		}
		return familyHistoryGXY;
	} 
	/**
	 * <chb>死亡时跟新健康档案信息
	 * 
	 * @param phrId
	 * @param deathDate
	 * @param deathReason
	 * @throws ModelDataOperationException
	 */
	public void updateDeadMessage(String phrId, String deathDate,
			String deathReason) throws ModelDataOperationException {
		String hql = new StringBuilder(" update ").append("EHR_HealthRecord")
				.append(" set deadFlag = :deadFlag, ")
				.append("deadDate = :deadDate ,deadReason = :deadReason ")
				.append("where phrId = :phrId and status = :status").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("deadFlag", YesNo.YES);
		parameters.put("deadDate", BSCHISUtil.toDate(deathDate));
		parameters.put("deadReason", deathReason);
		parameters.put("phrId", phrId);
		parameters.put("status", RecordStatus.CANCEL);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("update healthRecord dead message error .", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新健康档案死亡信息失败！");
		}
	}

	private String join(List<?> list) {
		return this.join(list, "、", false);
	}

	private String join(List<?> list, String regex, boolean isSaveKey) {
		if (regex.equals("")) {
			regex = "、";
		}
		StringBuffer sb = new StringBuffer();
		if (list.size() > 0) {
			sb.append(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				sb.append(regex + list.get(i).toString());
			}
		} else {
			if (!isSaveKey) {
				sb.append("无");
			}
		}
		return sb.toString();
	}

	/**
	 * 根据Id查询家庭档案记录
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyRecordById(String familyId)
			throws ModelDataOperationException {
		Map<String, Object> familyRecord;
		try {
			familyRecord = dao.doLoad(EHR_FamilyRecord, familyId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询家庭档案失败！");
		}
		return familyRecord;
	}

	/**
	 * 检查当前档案归属者是否是户主
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMasterInfo(String familyId, String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
					familyId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.masterFlag", "s",
					YesNo.YES);
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd4, cnd3);
			List<Map<String, Object>> rsList = dao.doList(cnd, null,
					EHR_HealthRecord);
			Map<String, Object> rsMap = null;
			if (rsList != null && rsList.size() > 0) {
				rsMap = rsList.get(0);
			}
			return rsMap;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
	}

	public List<Map<String, Object>> getHealthRecordByRegionCode(String empiId,
			String regionCode) throws ModelDataOperationException {
		List<Map<String, Object>> healthRecords;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("ne", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.regionCode", "s",
					regionCode);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			healthRecords = dao.doList(cnd, "", EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return healthRecords;
	}

	/**
	 * 获取该家庭中所有人员的档案列表。
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getRelatedRecordByRegionCode(
			String regionCode) throws ModelDataOperationException {
		List<Map<String, Object>> relatedRecords;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.regionCode", "s",
					regionCode);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.status", "s",
					RecordStatus.NOMAL);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			relatedRecords = dao.doList(cnd, "", EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return relatedRecords;
	}

	/**
	 * 获取该家庭中所有人员的档案列表。
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getRelatedRecordByFamilyId(String familyId)
			throws ModelDataOperationException {
		List<Map<String, Object>> relatedRecords;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
					familyId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.status", "s",
					RecordStatus.NOMAL);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			relatedRecords = dao.doList(cnd, "", EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return relatedRecords;
	}

	public Map<String, Object> getMasterRecordByFamilyId(String familyId)
			throws ModelDataOperationException {
		Map<String, Object> masterRecord = null;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.masterFlag", "s",
					YesNo.YES);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.relaCode", "s",
					RelatedCode.MASTER);
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
					familyId);
			List<?> cnd4 = CNDHelper.createArrayCnd("or", cnd1, cnd2);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

			List<Map<String, Object>> masterRecords = dao.doList(cnd, "",
					EHR_HealthRecord);
			if (masterRecords != null && masterRecords.size() > 0) {
				masterRecord = masterRecords.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return masterRecord;
	}

	public Map<String, Object> getMasterRecordByRegionCode(String regionCode)
			throws ModelDataOperationException {
		Map<String, Object> masterRecord = null;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.masterFlag", "s",
					YesNo.YES);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.status", "s",
					RecordStatus.NOMAL);
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.regionCode", "s",
					regionCode);
			List<?> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);

			masterRecord = dao.doLoad(cnd, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return masterRecord;
	}

	/**
	 * 根据户主家庭ID和户主标志查询健康档案
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getHealthRecordByFamilyId(String familyId)
			throws ModelDataOperationException {
		List<Map<String, Object>> healthRecords;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
					familyId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.masterFlag", "s",
					YesNo.YES);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			healthRecords = dao.doQueryNo(cnd, "", EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return healthRecords;
	}

	public List<Map<String, Object>> getHealthRecordByFamilyId2(String familyId)
			throws ModelDataOperationException {
		List<Map<String, Object>> healthRecords;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
					familyId);
			List<?> cnd2 = CNDHelper
					.createSimpleCnd("eq", "a.status", "s", "0");
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			healthRecords = dao.doList(cnd, null, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return healthRecords;
	}

	/**
	 * 根据empiId查询配偶的健康档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPartnerHealthRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		Map<String, Object> healthRecords = new HashMap<String, Object>();
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "partnerId", "s",
					empiId);
			healthRecords = dao.doLoad(cnd, EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人健康档案失败！");
		}
		return healthRecords;
	}

	/**
	 * 更新个人健康档案
	 * 
	 * @param familyId
	 * @param userId
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateHealthRecordByPhrId(String familyId, String userId,
			String phrId) throws ModelDataOperationException {
		String hql=" update EHR_HealthRecord set familyId=:familyId,lastModifyUser=:lastModifyUser," +
				   " lastModifyDate=:lastModifyDate where phrId=:phrId";
		if(familyId==null || familyId.length()<1){
			hql=" update EHR_HealthRecord set familyId=:familyId,masterFlag='n',relaCode=null,lastModifyUser=:lastModifyUser," +
				" lastModifyDate=:lastModifyDate where phrId=:phrId";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("familyId", familyId);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新家庭档案失败！", e);
		}
	}

	/**
	 * 更新个人健康档案
	 * 
	 * @param userId
	 * @param preFamilyId
	 * @throws ModelDataOperationException
	 */
	public void updateHealthRecordByFamilyId(String userId, String preFamilyId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set familyId=:nowFamilyId,")
				.append("lastModifyUser=:lastModifyUser,")
				.append("lastModifyDate=:lastModifyDate ")
				.append("where familyId=:preFamilyId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nowFamilyId", null);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("preFamilyId", preFamilyId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新家庭档案失败！", e);
		}
	}

	/**
	 * 通过网格地址更新个人健康档案家庭编号
	 * 
	 * @param familyId
	 * @param regionCode
	 * @throws ModelDataOperationException
	 */
	public void updateHealthRecordByRegionCode(String familyId,
			String regionCode) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set familyId=:familyId ")
				.append(" where regionCode=:regionCode ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("familyId", familyId);
		parameters.put("regionCode", regionCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新家庭档案失败！", e);
		}
	}

	/**
	 * 删除个人既往史
	 * 
	 * @param pastHistoryId
	 * @throws ModelDataOperationException
	 */
	public void deletePastHistory(String pastHistoryId)
			throws ModelDataOperationException {
		try {
			System.out.println("pastHistoryId = " + pastHistoryId);
			dao.doRemove(pastHistoryId, EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "个人既往史删除失败！", e);
		}
	}

	/**
	 * 更新老年人最后最后一次随访时间
	 * 
	 * @param userId
	 * @param preFamilyId
	 * @throws ModelDataOperationException
	 */
	public void updateHealthRecordWithOldPeople(Date oldlastVisitDate,
			String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set oldlastVisitDate=:oldlastVisitDate")
				.append(" where empiId =:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("oldlastVisitDate", oldlastVisitDate);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新个人健康档案失败！", e);
		}
	}

	/**
	 * 注销个人健康档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutHealthRecord(String phrId, String deadReason,
			String cancellationReason, String deadDate, String deadFlag)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason, ")
				.append(" deadFlag = :deadFlag, ")
				.append(" deadDate = :deadDate, ")
				.append(" isDiabetes = :isDiabetes, ")
				.append(" isHypertension =:isHypertension,masterFlag='n',relaCode='' ")
				.append(" where phrId = :phrId ")
				.append(" and status = :status1 ").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("deadFlag", deadFlag);
		parameters.put("deadDate", BSCHISUtil.toDate(deadDate));
		parameters.put("isDiabetes", YesNo.NO);
		parameters.put("isHypertension", YesNo.NO);
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_NORMAL);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销个人健康档案失败！", e);
		}
	}

	/**
	 * 恢复个人健康档案
	 * 
	 * @param phrId
	 * @param deadReason
	 * @param cancellationReason
	 * @param deadDate
	 * @param deadFlag
	 * @throws ModelDataOperationException
	 */
	public void revertHealthRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set deadFlag = :deadFlag,")
				.append(" deadDate = :deadDate,")
				.append(" deadReason = :deadReason,")
				.append(" cancellationReason=:cancellationReason,")
				.append(" cancellationUser = :cancellationUser,")
				.append(" cancellationUnit= :cancellationUnit,")
				.append(" cancellationDate = :cancellationDate,")
				.append(" status = :status,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyDate=:lastModifyDate,")
				.append(" lastModifyUnit=:lastModifyUnit")
				.append(" where phrId = :phrId and status = :status1")
				.toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("deadFlag", YesNo.NO);
		parameters.put("deadDate", BSCHISUtil.toDate(""));
		parameters.put("deadReason", "");
		parameters.put("cancellationReason", "");
		parameters.put("cancellationUser", "");
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复个人健康档案失败！", e);
		}
	}

	/**
	 * 清除个人健康档案慢病标志
	 * 
	 * @param phrId
	 * @param argField
	 *            为慢病的字段名称，例如：isHypertension、isDiabetes
	 * @throws ModelDataOperationException
	 */
	public void clearChronicDiseaseFlag(String phrId, String argField)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set ").append(argField).append(" = :argValue")
				.append(" where phrId =:phrId").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("argValue", YesNo.NO);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复个人健康档案失败！", e);
		}
	}

	/**
	 * 改变收入来源标志
	 * 
	 * @param empiId
	 * @param incomeSource
	 * @throws ModelDataOperationException
	 */
	public void updateIncomeSourceFlag(String empiId, String incomeSource)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set incomeSource=:incomeSource ")
				.append(" where  empiId=:empiId").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("incomeSource", incomeSource);
		parameters.put("empiId", empiId);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "改变收入来源标志失败！", e);
		}
	}

	/**
	 * 档案注销 或恢复时,如果注销原因为“死亡”，需要 修改死亡标识
	 * 
	 * @author ChenXianRui
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int setDeadFlag(String deadFlag, String deadReason, Date deadDate,
			String phrId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set deadFlag=:deadFlag")
				.append(",deadDate=:deadDate")
				.append(",deadReason=:deadReason")
				.append(" where phrId=:phrId").toString();

		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("deadFlag", deadFlag);
		pam.put("deadReason", deadReason);
		pam.put("deadDate", deadDate);
		pam.put("phrId", phrId);
		int updateRecordNum = 0;
		try {
			updateRecordNum = dao.doUpdate(hql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改健康档案失败！", e);
		}
		return updateRecordNum;
	}

	public void updateHealthRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("EHR_HealthRecord")
				.append(" set isCVDAssessRegister='1'")
				.append(" where empiId =:empiId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改健康档案失败！", e);
		}
	}

	/**
	 * 
	 * @Description:判断 该人 是否 迁出或死亡
	 * @param empiId
	 *            个人主索引
	 * @return true (是 迁出 或 死亡) false (不是 迁出的或死亡的)
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-7 上午10:20:01
	 * @Modify:
	 */
	public boolean isEmigrationOrDeath(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(EHR_HealthRecord)
				.append(" where empiId=:empiId")
				.append(" and status='1'")
				.append(" and (cancellationReason='1' or cancellationReason='2')")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断 该人 是否 迁出或死亡 失败", e);
		}
		boolean isEOD = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				isEOD = true;
			}
		}
		return isEOD;
	}
	//根据家庭代表家庭签约基本信息
	public Map<String, Object> getFamilyContractBasebyFC_Repre(String FC_Repre,Map<String, Object> res)
	throws ModelDataOperationException {
		Map<String, Object> record=new HashMap<String, Object>();
		List<Map<String, Object>> RecordList=new ArrayList<Map<String,Object>>();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "FC_Repre", "s", FC_Repre);
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "FC_Sign_Flag", "s", "1");
		List<?> cnds = CNDHelper.createArrayCnd("and", cnd, cnd1);
		try {
			RecordList=dao.doQuery(cnds, "", EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if(RecordList!=null && RecordList.size() >1){
			res.put("code", "402");
			res.put("msg", "存在多条签约记录，请联系管理员处理！");
		}else if(RecordList!=null && RecordList.size()==1){
			record=RecordList.get(0);
		}
		return record ;
	}
	
	/**
	 * 根据phrIds审核选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void verifyHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		String roleId = user.getRoleId();
		List<String> phrIds = (List<String>) body.get("phrIds");
		try {
			if(!"chis.22".equals(roleId) && !"chis.23".equals(roleId)){
				res.put("code", "406");
				res.put("msg", "当前角色没有审核权限，请联系管理员处理！");
				return;
			}
			if(phrIds!=null && phrIds.size()>0){
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				if("chis.22".equals(roleId)){//二级审核
					hql.append(" isFirstVerify =:isFirstVerify,unfirstVerifyPerson=null,unfirstVerifyDate=null, firstVerifyPerson =:firstVerifyPerson, firstVerifyDate=:firstVerifyDate");
					parameters.put("isFirstVerify", "1");
					parameters.put("firstVerifyPerson", userId);
					parameters.put("firstVerifyDate", new Date());
				}else if("chis.23".equals(roleId)){//三级审核
					hql.append(" isSecondVerify =:isSecondVerify, secondVerifyPerson =:secondVerifyPerson, secondVerifyDate=:secondVerifyDate");
					parameters.put("isSecondVerify", "1");
					parameters.put("secondVerifyPerson", userId);
					parameters.put("secondVerifyDate", new Date());
				}
				hql.append(" where phrId in(:phrIds) and status =:status");
				parameters.put("phrIds", phrIds);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案审核失败", e);
		}
	}
	
	/**
	 * 根据phrIds弃审选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void cancelVerifyHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		String roleId = user.getRoleId();
		List<String> phrIds = (List<String>) body.get("phrIds");
		try {
			if(!"chis.22".equals(roleId) && !"chis.23".equals(roleId)){
				res.put("code", "406");
				res.put("msg", "当前角色没有审核权限，请联系管理员处理！");
				return;
			}
			if(phrIds!=null && phrIds.size()>0){
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				if("chis.22".equals(roleId)){//二级弃审
					hql.append(" isFirstVerify =:isFirstVerify, unfirstVerifyDate=:unfirstVerifyDate,unfirstVerifyPerson =:unfirstVerifyPerson,firstVerifyPerson =null, firstVerifyDate=null,");
					hql.append(" isSecondVerify =null,unsecondVerifyPerson =null, unsecondVerifyDate=null, secondVerifyPerson =null, secondVerifyDate=null");
					parameters.put("isFirstVerify", "2");
					parameters.put("unfirstVerifyPerson", userId);
					parameters.put("unfirstVerifyDate", new Date());
				}else if("chis.23".equals(roleId)){//三级弃审
					hql.append(" isSecondVerify =:isSecondVerify, secondVerifyPerson =:secondVerifyPerson, secondVerifyDate=:secondVerifyDate");
					parameters.put("isSecondVerify", "0");
					parameters.put("secondVerifyPerson", "");
					parameters.put("secondVerifyDate", null);
				}
				hql.append(" where phrId in (:phrIds) and status =:status");
				parameters.put("phrIds", phrIds);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案弃审失败", e);
		}
	}
	/**
	 * 2019-04-29 Wangjl  根据phrId确认审核的健康档案(一级审核)
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doverifyHealthRecord(Map<String, Object> body) 
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		
		String phrId = (String) body.get("phrId");
		
		try {
				Map<String, Object> parameters = new HashMap<String, Object>();				
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				hql.append(" isoffer =:isoffer, unofferlastmodifyperson =null, unofferlastmodifydate =null,offerlastmodifyperson =:offerlastmodifyperson, offerlastmodifydate=:offerlastmodifydate");
				parameters.put("isoffer", "y");
				parameters.put("offerlastmodifyperson", userId);
				parameters.put("offerlastmodifydate", new Date());
				hql.append(" where phrId = :phrId and status =:status");
				parameters.put("phrId", phrId);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案审核失败", e);
		}
	}
	
	/**
	 * 2019-04-29 Wangjl  根据phrId确认取消审核的健康档案(一级审核)
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doCancelverifyHealthRecord(Map<String, Object> body) 
			throws ValidateException, ModelDataOperationException {
		 String phrId = (String) body.get("phrId");
		 UserRoleToken user = UserRoleToken.getCurrent();
		 String userId = user.getUserId();
		try {
				Map<String, Object> parameters = new HashMap<String, Object>();				
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				hql.append(" isoffer =:isoffer, offerlastmodifyperson = null, offerlastmodifydate = null, unofferlastmodifyperson =:unofferlastmodifyperson, unofferlastmodifydate =:unofferlastmodifydate,isfirstverify = null,unfirstverifyperson = null,unfirstverifydate = null,firstverifyperson = null,firstverifydate = null,issecondverify = null,unsecondverifyperson = null,unsecondverifydate = null,secondverifyperson = null,secondverifydate = null");
				hql.append(" where phrId = :phrId and status =:status");
				parameters.put("isoffer", "c");
				parameters.put("unofferlastmodifyperson", userId);
				parameters.put("unofferlastmodifydate", new Date());
				parameters.put("phrId", phrId);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案取消审核失败", e);
		}
	}
	/**
	 * 根据phrIds确认开放选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOpenHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		String roleId = user.getRoleId();
		List<String> phrIds = (List<String>) body.get("phrIds");
		try {
			if(!"chis.23".equals(roleId)){
				res.put("code", "406");
				res.put("msg", "当前角色没有开放的权限，请联系管理员处理！");
				return;
			}
			if(phrIds!=null && phrIds.size()>0){
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				hql.append(" isSecondVerify =:isSecondVerify,unsecondVerifyPerson =null, unsecondVerifyDate=null, secondVerifyPerson =:secondVerifyPerson, secondVerifyDate=:secondVerifyDate");
				parameters.put("isSecondVerify", "2");
				parameters.put("secondVerifyPerson", userId);
				parameters.put("secondVerifyDate", new Date());
				hql.append(" where phrId in (:phrIds) and status =:status");
				parameters.put("phrIds", phrIds);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案开放失败", e);
		}
	}
	/**
	 * 根据phrIds取消开放选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void cancelOpenHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
			throws ValidateException, ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		String roleId = user.getRoleId();
		List<String> phrIds = (List<String>) body.get("phrIds");
		try {
			if(!"chis.23".equals(roleId)){
				res.put("code", "406");
				res.put("msg", "当前角色没有审核权限，请联系管理员处理！");
				return;
			}
			if(phrIds!=null && phrIds.size()>0){
				Map<String, Object> parameters = new HashMap<String, Object>();
				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
						.append(" set");
				hql.append(" isSecondVerify =:isSecondVerify, secondVerifyPerson =null, secondVerifyDate=null, unsecondVerifyPerson =:unsecondVerifyPerson, unsecondVerifyDate=:unsecondVerifyDate");
				parameters.put("isSecondVerify", "3");
				parameters.put("unsecondVerifyPerson", userId);
				parameters.put("unsecondVerifyDate", new Date());
				hql.append(" where phrId in (:phrIds) and status =:status");
				hql.append(" where phrId in (:phrIds) and status =:status");
				parameters.put("phrIds", phrIds);
				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("健康档案取消开放失败", e);
		}
	}
	/**
	 * 根据phrIds标记选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
//	@SuppressWarnings("unchecked")
//	public void doSaveRemarkHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
//			throws ValidateException, ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String userId = user.getUserId();
//		String roleId = user.getRoleId();
//		List<String> phrIds = (List<String>) body.get("phrIds");
//		try {
//			if(!"chis.24".equals(roleId)){
//				res.put("code", "406");
//				res.put("msg", "当前角色没有标记权限，请联系管理员处理！");
//				return;
//			}
//			if(phrIds!=null && phrIds.size()>0){
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
//						.append(" set");
//				hql.append(" isRemarked =:isRemarked, remarkPerson =:remarkPerson, remarkDate=:remarkDate");
//				parameters.put("isRemarked", "1");
//				parameters.put("remarkPerson", userId);
//				parameters.put("remarkDate", new Date());
//				hql.append(" where phrId in(:phrIds) and status =:status");
//				parameters.put("phrIds", phrIds);
//				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
//				dao.doUpdate(hql.toString(), parameters);
//				
//				for(String phrId:phrIds){
//					int backTimes = 1;
//					Map<String, Object> paramMap = new HashMap<String, Object>();
//					paramMap.put("phrId", phrId);
//					String backHql = new StringBuffer("select backTimes as backTimes from ")
//							.append(EHR_HealthBackRecord)
//							.append(" where PHRID=:phrId").toString();
//					List<Map<String,Object>> rsCountMap = null;
//					try {
//						rsCountMap = dao.doQuery(backHql, paramMap);
//						if(rsCountMap!=null && rsCountMap.size()>0){
//							backTimes = rsCountMap.size()+1;
//						}
//					} catch (PersistentDataOperationException e) {
//						logger.error(
//								"Failed to count of EHR_HealthBackRecord with phrId = ["+phrId+"]", e);
//						throw new ModelDataOperationException(
//								Constants.CODE_DATABASE_ERROR, "查询退回记录失败！");
//					}
//					
//					String healthHql = new StringBuffer("select phrId as phrId,empiId as empiId,manaDoctorId as manaDoctorId,")
//							.append("offerLastModifyPerson as offerLastModifyPerson,firstVerifyPerson as firstVerifyPerson,")
//							.append("secondVerifyPerson as secondVerifyPerson from ")
//							.append(EHR_HealthRecord)
//							.append(" where phrId=:phrId").toString();
//					Map<String, Object> rsMap = null;
//					try {
//						rsMap = dao.doLoad(healthHql, paramMap);
//					} catch (PersistentDataOperationException e) {
//						logger.error(
//								"Failed to search of EHR_HealthRecord with phrId = ["+phrId+"]", e);
//						throw new ModelDataOperationException(
//								Constants.CODE_DATABASE_ERROR, "无健康档案记录！");
//					}
//					
//					//保存退回日志
//					UserRoleToken ur = UserRoleToken.getCurrent();
//					Map<String,Object> backRecord = new HashMap<String, Object>();
//					backRecord.put("phrId", phrId);
//					backRecord.put("backTimes", backTimes);
//					backRecord.put("empiId", rsMap.get("empiId")+"");
//					backRecord.put("backReason", "标记");
//					backRecord.put("backPerson", ur.getUserId());
//					backRecord.put("backDate", BSCHISUtil.toString(new Date(), null));
//					backRecord.put("lastVerifyLevel", '3');
//					backRecord.put("lastVerifyPerson", rsMap.get("secondVerifyPerson")+"");
//					try {
//						dao.doSave("create", EHR_HealthBackRecord, backRecord, true);
//					}catch (PersistentDataOperationException e) {
//						logger.error("create log data of PUB_Log failed.", e);
//						throw new ModelDataOperationException(
//								Constants.CODE_DATABASE_ERROR, "日志记录失败！");
//					}catch (ValidateException e) {
//						logger.error("create log data of PUB_Log failed.", e);
//						throw new ModelDataOperationException(
//								Constants.CODE_DATABASE_ERROR, "日志记录失败！");
//					}
//				}
//			}
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//			throw new ModelDataOperationException("健康档案标记失败", e);
//		}
//	}
	
	/**
	 * 根据phrIds取消标记选中的健康档案
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	//@SuppressWarnings("unchecked")
//	public void cancelRemarkHealthRecordByPhrIds(Map<String, Object> body,Map<String, Object> res) 
//			throws ValidateException, ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		String roleId = user.getRoleId();
//		List<String> phrIds = (List<String>) body.get("phrIds");
//		try {
//			if(!"chis.24".equals(roleId)){
//				res.put("code", "406");
//				res.put("msg", "当前角色没有权限，请联系管理员处理！");
//				return;
//			}
//			if(phrIds!=null && phrIds.size()>0){
//				Map<String, Object> parameters = new HashMap<String, Object>();
//				StringBuffer hql = new StringBuffer("update ").append("EHR_HealthRecord")
//						.append(" set");
//				hql.append(" isRemarked =:isRemarked, remarkPerson =:remarkPerson, remarkDate=:remarkDate");
//				hql.append(" where phrId in (:phrIds) and status =:status");				
//				parameters.put("isRemarked", "0");
//				parameters.put("remarkPerson", "");
//				parameters.put("remarkDate", null);
//				parameters.put("phrIds", phrIds);
//				parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
//				dao.doUpdate(hql.toString(), parameters);
//			}
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//			throw new ModelDataOperationException("健康档案取消标记失败", e);
//		}
//	}
//	
	/**
	 * 查询退回记录
	 * @param body
	 * @param res
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doQueryBackRecord(Map<String, Object> body,Map<String, Object> res) 
			throws ValidateException, ModelDataOperationException {
		String phrId = body.get("phrId")+"";
		String healthHql = new StringBuffer("select backReason as backReason from ")
				.append(EHR_HealthBackRecord)
				.append(" where phrId=:phrId and backReason != '标记' order by backTimes desc").toString();
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phrId", phrId);
		List<Map<String, Object>> rsMapList = null;
		try {
			rsMapList = dao.doQuery(healthHql, paramMap);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to search of EHR_HealthBackRecord with phrId = ["+phrId+"]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "无健康档案退回记录！");
		}
		return rsMapList;
	}
}
