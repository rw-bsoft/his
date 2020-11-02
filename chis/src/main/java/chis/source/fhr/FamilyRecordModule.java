/**
 * @(#)HealthRecordModule.java Created on 2011-12-31 下午03:36:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.fhr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.Hash;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description 家庭档案服务
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class FamilyRecordModule implements BSCHISEntryNames {

	Logger logger = LoggerFactory.getLogger(FamilyRecordModule.class);

	BaseDAO dao = null;

	public FamilyRecordModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @param familyId
	 * @param ownerName
	 * @throws ModelDataOperationException
	 */
	public void updateOnwerName(String familyId, String ownerName)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append(EHR_FamilyRecord)
				.append(" set ownerName = :ownerName where familyId = :familyId")
				.toString();
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("familyId", familyId);
		params.put("ownerName", ownerName);
		try {
			dao.doUpdate(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新家庭档案户主姓名失败！", e);
		}
	}

	/**
	 * 根据regionCode获取家庭档案的familyId
	 * 
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getFamilyIdByRegionCode(String regionCode)
			throws ModelDataOperationException {
		String hql = new StringBuilder("select familyId as familyId from  ")
				.append(EHR_FamilyRecord)
				.append(" where regionCode = :regionCode").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", regionCode);
		List<Map<String, Object>> list;
		String familyId = null;
		try {
			list = dao.doQuery(hql, parameters);
			if (list.size() > 0) {
				Map<String, Object> familyRecord = list.get(0);
				familyId = (String) familyRecord.get("familyId");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get familyId record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭信息失败.");
		}
		return familyId;
	}

	/**
	 * 根据主键查询家庭健康档案
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyRecordById(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> familyRecord = new HashMap<String, Object>();
		try {
			familyRecord = dao.doLoad(EHR_FamilyRecord, pkey);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get familyId record message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭信息失败！");
		}
		return familyRecord;
	}

	/**
	 * 根据网格地址查询家庭档案
	 * 
	 * @param empiId
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFamilyRecordByRegionCode(String regionCode)
			throws ModelDataOperationException {
		Map<String, Object> familyRecord = null;
		try {
			List<?> cnd1 = CNDHelper
					.createSimpleCnd("eq", "a.status", "s", "0");
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.regionCode", "s",
					regionCode);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> result = dao.doList(cnd, "",
					EHR_FamilyRecord);
			if (result != null && result.size() > 0) {
				familyRecord = result.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据网格地址查询家庭档案失败！");
		}
		return familyRecord;
	}

	/**
	 * 更新家庭健康档案
	 * 
	 * @param ownerName
	 * @param familyId
	 * @param userId
	 * @param date
	 * @throws ModelDataOperationException
	 */
	public void updateFamilyRecord(String ownerName, String familyId,
			String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(EHR_FamilyRecord)
				.append(" set ownerName=:ownerName,")
				.append("lastModifyUser=:lastModifyUser,")
				.append("lastModifyDate=:lastModifyDate ")
				.append("where familyId=:familyId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ownerName", ownerName);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("familyId", familyId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新家庭档案失败！", e);
		}
	}

	/**
	 * 保存家庭档案
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected Map<String, Object> saveFamilyRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		req.put("body", body);
		req.put("op", op);
		try {
			return dao
					.doSave(op, BSCHISEntryNames.EHR_FamilyRecord, body, true);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("家庭档案保存失败！", e);
		}
	}

	/**
	 * 保存家庭主要问题
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected Map<String, Object> saveFamilyProblem(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		Map<String, Object> req = new HashMap<String, Object>();
		req.put("body", body);
		req.put("op", op);
		try {
			return dao.doSave(op, BSCHISEntryNames.EHR_FamilyProblem, body,
					true);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("家庭主要问题保存失败！", e);
		}
	}

	/**
	 * 将家庭档案电话号码更新的各个家庭成员的个人信息中
	 * 
	 * @param familyId
	 * @param phoneNumber
	 * @throws ModelDataOperationException
	 */
	public void updateFamilyPhone(String familyId, String phoneNumber)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MPI_DemographicInfo)
				.append(" a set a.phoneNumber = :phoneNumber")
				.append(" where a.empiId in (select b.empiId")
				.append(" from EHR_HealthRecord b where")
				.append(" and b.familyId = :familyId)").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phoneNumber", phoneNumber);
		parameters.put("familyId", familyId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新个人主要信息电话号码失败！", e);
		}
	}

	/**
	 * 删除家庭主要问题
	 * 
	 * @param familyId
	 * @param value
	 * @throws ModelDataOperationException
	 */
	public void deleteFamilyProblem(String field, String value)
			throws ModelDataOperationException {
		try {
			dao.doRemove(field, value, EHR_FamilyProblem);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "家庭主要问题删除失败！", e);
		}
	}

	/**
	 * 删除家庭档案
	 * 
	 * @param familyId
	 * @throws ModelDataOperationException
	 */
	public void deleteFamilyRecord(String familyId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(familyId, EHR_FamilyRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "家庭档案删除失败！", e);
		}
	}

	/**
	 * 
	 * @Description:删除家庭档案的中间表
	 * @param middleId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-5-13 下午5:29:17
	 * @Modify:
	 */
	public void deleteFamilyMidleByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(EHR_FamilyMiddle)
				.append(" where empiId=:empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除家庭档案中间表记录失败！", e);
		}
	}

	public Map<String, Object> getFamilyContract(String empiId, String fC_Id)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.FS_EmpiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.FC_Id", "s",
					fC_Id);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> familyRecords = dao.doList(cnd, null,
					EHR_FamilyContractService);
			if (familyRecords.size() > 0) {
				return familyRecords.get(0);
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据empiId，FC_ID查询服务项目记录失败！");
		}

	}

	public Map<String, Object> saveFamilyContractBase(Map<String, Object> data,
			String op) throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, EHR_FamilyContractBase, data, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭签约记录失败。");
		}
	}

	public Map<String, Object> getFamilyContractByPkey(String fS_Id)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(EHR_FamilyContractService, fS_Id);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据FS_Id查询服务项目记录失败！");
		}
	}

	public Map<String, Object> saveFamilyContract(Map<String, Object> data,
			String op) throws ModelDataOperationException, ValidateException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, EHR_FamilyContractService, data, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭服务项目失败。");
		}
		return rsMap;
	}

	public List<Map<String, Object>> getFamilyContractBaseByFId(String familyId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "F_Id", "s", familyId);
		try {
			return dao.doList(cnd, "FC_Begin", EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据familyId查询家庭签约记录失败。");
		}
	}

	public Map<String, Object> getFamilyContractBaseByPkey(String fcId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(EHR_FamilyContractBase, fcId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据主键查询家庭签约记录失败。");
		}
	}

	public List<Map<String, Object>> getFamilyContractByFcId(String fcId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.FC_Id", "s", fcId);
		try {
			return dao.doList(cnd, null, EHR_FamilyContractService);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据FC_Id查询签约项目记录失败。");
		}
	}

	public void deleteFamilyContractBaseByFcId(String fcId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(fcId, EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除签约记录失败。");
		}

	}

	public void deleteFamilyContractServiceByFcId(String fcId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("FC_Id", fcId, EHR_FamilyContractService);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除签约服务失败。");
		}
	}

	public void deleteFamilyContractRecordByFcId(String fcId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("FC_Id", fcId, EHR_FamilyContractRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除签约服务失败。");
		}

	}

	public String getOwnerName(String familyId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId", "s",
				familyId);
		List<?> cnd2 = CNDHelper
				.createSimpleCnd("eq", "a.masterFlag", "s", "y");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					EHR_HealthRecord);
			if (list != null && list.size() > 0) {
				Map<String, Object> r = list.get(0);
				return (String) r.get("empiId");
			}
			return "";
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据FC_Id查询签约项目记录失败。");
		}
	}

	/**
	 * 修改家庭的状态,当没有家庭成员的时候，状态改为1，
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public String updateFamilyStatus(String familyId)
			throws ModelDataOperationException, ServiceException,
			PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_health = new HashMap<String, Object>();
		List<Map<String, Object>> map_health2 = new ArrayList<Map<String, Object>>();
		hql.append("select phrId as phrId from EHR_HealthRecord where familyId=:familyId");
		map_health.put("familyId", familyId);
		map_health2 = dao.doQuery(hql.toString(), map_health);
		if (map_health2 != null && map_health2.size() > 0) {
			// throw new
			// ModelDataOperationException(Constants.CODE_DATABASE_ERROR,
			// "该家庭有成员，不能删除！");

			return "n";
		} else {
			return "y";

		}

	}

	/**
	 * 删除家庭成员
	 * 
	 * @param phrid
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public void removeFamilyByid(String phrid)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_health = new HashMap<String, Object>();
		map_health.put("phrId", phrid);
		map_health.put("familyId", "");
		hql.append(" update EHR_HealthRecord  set familyId=:familyId where phrId=:phrId");
		dao.doUpdate(hql.toString(), map_health);

	}

	/**
	 * 删除家庭成员
	 * 
	 * @param phrid
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public void removeFamilyByFimlyId(String familyid)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_health = new HashMap<String, Object>();
		hql.setLength(0);
		hql.append("update EHR_FamilyRecord  set status=:status where familyId=:familyId ");
		map_health.put("status", "1");
		map_health.put("familyId", familyid);
		try {
			dao.doUpdate(hql.toString(), map_health);

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除家庭档案失败。");
		}

	}

	/**
	 * 判断一个家庭档案是否有户主,是：返回true
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public boolean HaveOwnerName(String familyId)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		try {
			Map<String, Object> familyMap = dao.doLoad(
					BSCHISEntryNames.EHR_FamilyRecord, familyId);
			if (familyMap != null & familyMap.size() > 0) {
				String ownerName = (String) familyMap.get("ownerName");
				if (ownerName != null && ownerName.length() > 0) {
					return true;
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询家庭档案是否有户主失败。");
		}
		return false;

	}

	/**
	 * 根据家庭档案编号查找户主
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> SelectOwnerName(String familyId)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Map<String, Object> healthMap = new HashMap<String, Object>();
		healthMap.put("familyId", familyId);
		healthMap.put("masterFlag", "y");
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select empiId as empiId from EHR_HealthRecord where familyId=:familyId and masterFlag=:masterFlag ");
			Map<String, Object> healthData = dao.doLoad(sb.toString(),
					healthMap);
			return healthData;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询家庭档案是否有户主失败。");
		}

	}

	/**
	 * 根据个人健康档案的主键查询
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> selectHealthInfo(Map<String, Object> data)
			throws ModelDataOperationException,
			PersistentDataOperationException {

		StringBuffer sb = new StringBuffer();
		sb.append(
				" select phrId as phrId ,empiId as empiId, regionCode as regionCode , manaDoctorId as manaDoctorId ,manaUnitId as manaUnitId ,familyId as familyId from ")
				.append(EHR_HealthRecord).append(" where phrId =:phrId ");
		Map<String, Object> mapHealthRecord = dao.doLoad(sb.toString(), data);
		return mapHealthRecord;

	}

	/**
	 * 根据传入的字段(单个)和schemas，获取该值，
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getValueByParameters(String entryName,
			String parameters, Map<String, Object> data)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Set<String> set = data.keySet();
		Iterator<String> iterator = set.iterator();
		String s = null;
		while (iterator.hasNext()) {
			s = iterator.next();
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select ").append(parameters).append(" as " + parameters)
				.append(" from " + entryName).append(" where " + s + "=:" + s);
		Map<String, Object> mapInfo = dao.doLoad(sb.toString(), data);
		return mapInfo;

	}

	// 根据户主迁出
	public void updateMasterFlagByEmpiId(String phrId, String masterFlag,
			String relaCode) throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> m = new HashMap<String, Object>();

		m.put("phrId", phrId);
		m.put("masterFlag", masterFlag);
		m.put("relaCode", relaCode);// 户主的关系
		m.put("familyId", "");
		hql.append("update EHR_HealthRecord ")
				.append("set masterFlag =:masterFlag , relaCode =:relaCode ,familyId=:familyId ")
				.append(" where phrId=:phrId");
		dao.doUpdate(hql.toString(), m);

	}

	// 根据家庭档案的id获取该家庭所有成员的empiId
	public List<String> getEmpiIdsByFamily(String cndSql)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> listData = new ArrayList<String>();
		List<Map<String, Object>> list = null;
		hql.append("select empiId as empiId EHR_HealthRecord ").append(
				" where ");
		if (cndSql != null && cndSql.length() > 0) {
			hql.append(cndSql);
		} else {
			return null;
		}
		list = dao.doQuery(hql.toString(), m);
		if (list != null && list.size() > 0) {
			for (Map<String, Object> data : list) {
				String s = (String) data.get("empiId");
				listData.add(s);
			}

		}
		return listData;
	}

	// 家庭成员：根据empiId(组)获取基本信息数据以及成员随访计划的数据
	@SuppressWarnings("unchecked")
	public Map<String, Object> getInfosAndPlanVisitByPhrId(
			Map<String, Object> req) throws PersistentDataOperationException,
			ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> PageMap = new HashMap<String, Object>();
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		String cndSQL = "";
		String toDataValue = "";
		String formDataValue = "";
		long countsAll = 0;
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}

		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			cndSQL = ExpressionProcessor.instance().toString(cnd);

		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式错误！", e);
		}
		// 总数 PUB_VisitPlan 33010200000000020
		countHql.append(" select count(*) as count ")
				.append(" from MPI_DemographicInfo a ,PUB_VisitPlan b ")
				.append(" where a.empiId=b.empiId and b.empiId in (select c.empiId as empiId from EHR_HealthRecord c where c.");
		formDataValue = (String) req.get("formDataValue");
		toDataValue = (String) req.get("toDataValue");
		if (cndSQL != null && cndSQL.length() > 0) {
			countHql.append(cndSQL + ")");
			// {toDataValue=2014-12-03 formDataValue=2014-12-01}// 从前台传条件过来，处理
			if (formDataValue != null && formDataValue.length() > 0) {
				if (toDataValue != null && toDataValue.length() > 0) {
					countHql.append(" and b.beginDate between ")
							.append(" to_date ( ")
							.append("'" + formDataValue + "'"
									+ ",'yyyy-mm-dd') ");
					countHql.append("and ")
							.append(" to_date ( ")
							.append("'" + toDataValue + "'" + ",'yyyy-mm-dd') ");
				} else {
					countHql.append(" and b.beginDate >=")
							.append(" to_date ( ")
							.append("'" + formDataValue + "'"
									+ ",'yyyy-mm-dd') ");
				}
			} else {
				if (toDataValue != null && toDataValue.length() > 0) {
					countHql.append(" and b.beginDate <=")
							.append(" to_date ( ")
							.append("'" + toDataValue + "'" + ",'yyyy-mm-dd') ");
				}
			}
		} else {
			countHql.append("familyId=\"\")");
			return null;
		}
		// 查询数据
		hql.append(
				" select b.empiId as empiId,b.recordId as recordId,a.personName as personName, a.sexCode as sexCode,a.idCard as idCard,a.birthday as birthday,")
				.append("b.businessType as businessType,b.planStatus as planStatus,b.beginVisitDate as beginVisitDate,"
						+ "b.planDate as planDate,b.visitDate as visitDate,b.beginDate as beginDate,b.endDate as endDate ")
				.append(" from MPI_DemographicInfo a ,PUB_VisitPlan b ")
				.append(" where a.empiId=b.empiId and b.empiId in (select c.empiId as empiId from EHR_HealthRecord c where c.");
		if (cndSQL != null && cndSQL.length() > 0) {
			hql.append(cndSQL + ")");
			if (formDataValue != null && formDataValue.length() > 0) {
				if (toDataValue != null && toDataValue.length() > 0) {
					hql.append(" and b.beginDate between ")
							.append(" to_date ( ")
							.append("'" + formDataValue + "'"
									+ ",'yyyy-mm-dd') ");
					hql.append("and ")
							.append(" to_date ( ")
							.append("'" + toDataValue + "'" + ",'yyyy-mm-dd') ");
				} else {
					hql.append(" and b.beginDate >=")
							.append(" to_date ( ")
							.append("'" + formDataValue + "'"
									+ ",'yyyy-mm-dd') ");
				}
			} else {
				if (toDataValue != null && toDataValue.length() > 0) {
					hql.append(" and b.beginDate <=")
							.append(" to_date ( ")
							.append("'" + toDataValue + "'" + ",'yyyy-mm-dd') ");
				}

			}
			hql.append(" order by  decode(b.planStatus, '3', '0','999')asc,b.planStatus asc ");
			// decode (b.planStatus, '3', '0','999') b.planStatus 表达式， 3表示字段的属性
			// 当等1时候，返回0，否则返回999 。 0表示返回值 自定义，999表示else后面的返回值
		} else {
			hql.append("familyId=\"\")");
			hql.append(" order by  decode(b.planStatus, '3', '0','999')asc,b.planStatus asc ");
			return null;
		}
		System.out.println(hql);
		Map<String, Object> countValue = dao.doLoad(countHql.toString(),
				PageMap);
		if (countValue != null && !countValue.isEmpty()) {
			countsAll = Long.parseLong(countValue.get("count") + "");
		}
		int first = (pageNo - 1) * pageSize;
		PageMap.put("first", first);
		PageMap.put("max", pageSize);
		listData = dao.doSqlQuery(hql.toString(), PageMap);
		// 将字段转成和schema的各个id一样
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(PUB_VisitPlanFamilyList);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"schema解析失败！", e1);
		}
		// listDataV
		List<Map<String, Object>> fqList = new ArrayList<Map<String, Object>>();
		if (listData != null && listData.size() > 0) {
			for (Map<String, Object> si : listData) {
				Map<String, Object> mapDataV = new HashMap<String, Object>();
				for (SchemaItem scc : sc.getItems()) {
					if (scc.hasProperty("refAlias") || scc.isVirtual()) {
						continue;
					}
					String s = scc.getId();
					mapDataV.put(s, si.get(s.toUpperCase()));
				}
				fqList.add(mapDataV);
			}
		}
		// SchemaUtil.setDictionaryMessageForList(fqList,
		// PUB_VisitPlanFamilyList)//根据字典id取相应的文本
		data.put("totalCount", countsAll);// 总数
		data.put("pageNo", pageNo);
		data.put("pageSize", pageSize);
		data.put("body", SchemaUtil.setDictionaryMessageForList(fqList,
				PUB_VisitPlanFamilyList));
		return data;
	}

	// 根据家庭档案的主键获取该家庭的签约代表
	public String getFcRepreByFamilyId(Map<String, Object> param)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> data = null;
		hql.append(" select FC_Repre as FC_Repre from ")
				.append(EHR_FamilyContractBase)
				.append(" where F_Id =:familyId ");
		data = dao.doLoad(hql.toString(), param);
		if (data != null && data.size() > 0) {
			return (String) data.get("FC_Repre");
		}
		return null;
	}

	// 获取一个家庭的成员人数
	public int getCountByFamilyId(Map<String, Object> param)
			throws PersistentDataOperationException {
		StringBuffer countHql = new StringBuffer();
		// Map<String, Object> data = null;
		long countsAll = 1;
		countHql.append(" select count(*) as count ").append(
				" from EHR_HealthRecord where  familyId=:familyId ");
		Map<String, Object> countValue = dao.doLoad(countHql.toString(), param);
		if (countValue != null && countValue.size() > 1) {

			countsAll = Long.parseLong(countValue.get("count") + "");
			return (int) countsAll;
		}
		return 1;
	}

	// 当家庭成员只有一个，而且是要删除该成员的情况下：删除该家庭的家庭签约的数据
	public void removeFcRepreByFamilyId(Map<String, Object> param)
			throws PersistentDataOperationException {
		String familyId = (String) param.get("familyId");
		dao.doRemove("F_Id", familyId, EHR_FamilyContractBase);

	}

	/**
	 * 
	 * @Description:保存家庭成员变动记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-15 下午3:18:36
	 * @Modify:
	 */
	public Map<String, Object> saveFMChangeRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, EHR_Record, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭成员变动记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存家庭成员变动记录时失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:
	 * @param FC_Id
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author FangY 2015-6-4 下午5:26:39
	 * @Modify:
	 */
	public void removeFamilyContractService(String FC_Id,String phrId, String empiId)
			throws ModelDataOperationException {
		if (StringUtils.isEmpty(empiId) || StringUtils.isEmpty(FC_Id)) {
			return;
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("FC_Id", FC_Id);
		String hql = new StringBuffer("delete from ")
				.append(EHR_FamilyContractService)
				.append(" where FC_Id=:FC_Id and FS_EmpiId=:empiId").toString();
		try {
			dao.doUpdate(hql, pMap);
			hql = new StringBuffer("update ").append(EHR_HealthRecord)
					.append(" set signFlag='n' ")
					.append("where phrId=:phrId").toString();
			Map<String, Object> pMap2 = new HashMap<String, Object>();
			pMap2.put("phrId", phrId);
			dao.doUpdate(hql, pMap2);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除个人家庭签约服务项目失败", e);
		}

	}

	/**
	 * 
	 * @Description:获取家庭签约记录ID
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 * @author FangY 2015-6-4 下午5:38:38
	 * @Modify:
	 */
	public String getFC_IdByFamilyId(String familyId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "F_Id", "s", familyId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "FC_Sign_Flag", "s", "1");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
//		String hql = new StringBuffer("select FC_Id as FC_Id from ")
//		.append(EHR_FamilyContractBase)
//		.append(" where FC_Sign_Flag='1' and F_Id=:familyId")
//		.toString();
//		Map<String, Object> pMap = new HashMap<String, Object>();
//		pMap.put("familyId", familyId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(cnd, EHR_FamilyContractBase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭签约记录ID失败!", e);
		}
		String FC_Id = "";
		if(rsMap != null && rsMap.size() > 0){
			FC_Id = (String) rsMap.get("FC_Id");
		}
		return FC_Id;
	}
	public void Sendtojsd(Map<String, Object> body,Map<String, Object> data)
			throws ModelDataOperationException {
		if(body.containsKey("idCard")){
			data.put("signer_iden", body.get("idCard")+"");
		}
		if(body.containsKey("personName")){
			data.put("signer_name", body.get("personName")+"");
		}
		String empiId=body.get("FS_EmpiId")+"";
		String mpisql="select a.sexcode as SEXCODE,a.address as ADDRESS,a.insuranceCode as INSURANCECODE, " +
				"a.contactPhone as CONTACTPHONE from  mpi_demographicinfo a where a.empiid=:empiId ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("empiId", empiId);
		try {
			Map<String, Object> maimap=dao.doSqlLoad(mpisql, p);
			data.put("signer_sex", (maimap.get("SEXCODE")+"").equals("1")?"1":"0");
			data.put("signer_address",maimap.get("ADDRESS")+"");
			data.put("signer_pay",maimap.get("INSURANCECODE")+"");
			data.put("signer_phone",maimap.get("CONTACTPHONE")+"");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String FC_Id=body.get("FC_Id")+"";
		String qyxxsql="select b.fs_persongroup as FS_PERSONGROUP,b.fs_kind as FS_KIND," +
				" b.fs_createdate as FS_CREATEDATE,a.fc_familydoctorid as FC_FAMILYDOCTORID," +
				" a.fc_familyteamid as FC_FAMILYTEAMID,a.fc_createunit as FC_CREATEUNIT " +
				" from ehr_familycontractbase a ,ehr_familycontractservice b "+
				" where a.fc_id=b.fc_id and b.fc_id=:FC_Id";
		p.remove("empiId");
		p.put("FC_Id", FC_Id);
		try {
			Map<String, Object> qyxxmap=dao.doSqlLoad(qyxxsql, p);
			String groupstr="";
			if(qyxxmap!=null && qyxxmap.size() >0 ){
				groupstr=qyxxmap.get("FS_PERSONGROUP")+"";
			}	
			String signer_category="";
			if(groupstr.indexOf("老年人") >=0){
				signer_category+=";01";
			}
			if(groupstr.indexOf("儿童") >=0){
				signer_category+=";02";
			}
			if(groupstr.indexOf("孕产妇") >=0){
				signer_category+=";03";
			}
			if(groupstr.indexOf("高血压") >=0){
				signer_category+=";04";
			}
			if(groupstr.indexOf("糖尿病") >=0){
				signer_category+=";05";
			}
			if(groupstr.indexOf("结核病") >=0){
				signer_category+=";06";
			}
			if(groupstr.indexOf("精神病") >=0){
				signer_category+=";07";
			}
			if(groupstr.indexOf("残疾") >=0){
				signer_category+=";08";
			}
			if(groupstr.indexOf("优抚") >=0){
				signer_category+=";09";
			}
			if(groupstr.indexOf("特扶") >=0){
				signer_category+=";10";
			}
			if(groupstr.indexOf("贫困") >=0){
				signer_category+=";11";
			}
			if(signer_category.length() >0){
				signer_category=signer_category.substring(1,signer_category.length());
			}else{
				signer_category="99";
			}
			data.put("signer_category" , signer_category);
			data.put("base_service","1");
			String FS_KIND=qyxxmap.get("FS_KIND")==null?"":qyxxmap.get("FS_KIND")+"";
			FS_KIND=FS_KIND.replaceAll(",",";");
			data.put("base_service_content",FS_KIND);
			if(FS_KIND.indexOf("15")>0){
				data.put("personality_service","1");
				data.put("personality_service_content","医院特色服务"); 
			} else{
				data.put("personality_service","0");
				data.put("personality_service_content",""); 
			}
			data.put("sign_date",qyxxmap.get("FS_CREATEDATE")+"");
			data.put("sign_limit_time","1");
			String FC_FAMILYDOCTORID=qyxxmap.get("FC_FAMILYDOCTORID")==null?"":qyxxmap.get("FC_FAMILYDOCTORID")+"";
			if(FC_FAMILYDOCTORID.length() >0){
				String docsql="select a.personname as PERSONNAME,a.cardnum as CARDNUM " +
						" from sys_personnel a where a.personid=:personid";
				Map<String, Object> docp=new HashMap<String, Object>();
				docp.put("personid", FC_FAMILYDOCTORID);
				Map<String, Object> docmap=dao.doSqlLoad(docsql, docp);
				data.put("doct_iden",docmap.get("CARDNUM")==null?"":docmap.get("CARDNUM")+"");
				data.put("doct_name",docmap.get("PERSONNAME")==null?"":docmap.get("PERSONNAME")+"");
			}
			String FC_FAMILYTEAMID=qyxxmap.get("FC_FAMILYTEAMID")==null?"":qyxxmap.get("FC_FAMILYTEAMID")+"";
			if(FC_FAMILYTEAMID.length() >0){
				data.put("doct_team_code",FC_FAMILYTEAMID);
				try {
					data.put("doct_team",DictionaryController.instance().get("chis.dictionary.familyteam").getText(FC_FAMILYTEAMID));
				} catch (ControllerException e) {
					e.printStackTrace();
				}
			}
			String FC_CREATEUNIT=qyxxmap.get("FC_CREATEUNIT")==null?"":qyxxmap.get("FC_CREATEUNIT")+"";
			if(FC_CREATEUNIT.length() >0){
				data.put("hospital_code",FC_CREATEUNIT);
				try {
					data.put("hospital_name",DictionaryController.instance().get("chis.@manageUnit").getText(FC_CREATEUNIT));
				} catch (ControllerException e) {
					e.printStackTrace();
				}
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		
	}
}
