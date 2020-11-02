/**
 * @(#)PregnantModel.java Created on 2012-1-12 下午02:17:02
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mhc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.RecordStatus;
import chis.source.empi.EmpiModel;
import chis.source.log.VindicateLogService;
import chis.source.phr.HealthRecordModel;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">tianj</a>
 */
public class PregnantRecordModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public PregnantRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取妇女档案信息。(不包括孕次和产次)
	 * 
	 * @param empiId
	 * @param session
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getMaternalHelathCard(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select empiId as empiId,manaUnitId as manaUnitId, ")
				.append("manaDoctorId as manaDoctorId, ")
				.append("mhcDoctorId as mhcDoctorId, menarcheAge as menarcheAge, ")
				.append("menstrualPeriod as menstrualPeriod, ")
				.append("cycle as cycle, menstrualBlood as menstrualBlood, ")
				.append("dysmenorrhea as dysmenorrhea, trafficFlow as trafficFlow, ")
				.append("naturalAbortion as naturalAbortion, dyingFetus as dyingFetus, stillBirth as stillBirth, ")
				.append(" odinopoeia as odinopoeia, preterm as preterm, ")
				.append("dystocia as dystocia, abnormality as abnormality, ")
				.append("qweTimes as qweTimes, newbronDied as newbronDied, ")
				.append("ectopicpregnancy as ectopicpregnancy, phrId as phrId,")
				.append(" vesicularMole as vesicularMole, gynecologyOPS as")
				.append(" gynecologyOPS, modifyDate as modifyDate, modifyUnit")
				.append(" as modifyUnit, modifyRecordId as modifyRecordId")
				.append(" from ").append(MHC_WomanRecord)
				.append(" where empiId = :empiId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		try {
			return dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女档案信息失败！", e);
		}
	}

	/**
	 * 检验末次月经时间本人是否已经录过
	 * 
	 * @param date
	 * @param phrId
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkLastMenstrualPeriod(String date, String empiId,
			String pregnantId) throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer("select count(*) as count from ")
				.append(MHC_PregnantRecord);
		sb.append(" where lastMenstrualPeriod = :lastMenstrualPeriod and empiId = :empiId ");
		Map<String, Object> param = new HashMap<String, Object>();
		if (pregnantId != null && !"".equals(pregnantId)) {
			sb.append(" and  pregnantId != :pregnantId");
			param.put("pregnantId", pregnantId);
		}
		param.put("lastMenstrualPeriod", BSCHISUtil.toDate(date));
		param.put("empiId", empiId);
		try {
			Map<String, Object> result = dao.doLoad(sb.toString(), param);
			long num = (Long) result.get("count");
			if (num > 0) {
				return true;
			} else {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检验妇女末次月经信息失败！", e);
		}
	}

	/**
	 * 根据孕妇编号获取孕妇档案信息
	 * 
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPregnantRecord(String pregnantId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(MHC_PregnantRecord, pregnantId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇档案信息失败！", e);
		}

	}
	
	/**
	 * @Description:根据孕妇编号获取孕妇首次随访信息
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-5-4 下午5:21:53
	 * @Modify:
	 */
	public Map<String, Object> getPregnantFirstVisitRecord(String pregnantId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(MHC_FirstVisitRecord, pregnantId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇首次随访信息失败！", e);
		}

	}

	/**
	 * 更新孕产妇档案
	 * 
	 * @param parameters
	 * @throws ModelDataOperationException
	 */
	public void updatePregnantRecord(Map<String, Object> parameters)
			throws ModelDataOperationException {
		try {
			String sql = new StringBuffer("update ")
					.append("MHC_PregnantRecord")
					.append(" set familyHistory=:familyHistory,operationHistory=:operationHistory,")
					.append(" pastHistory=:pastHistory,otherPastHistory = :otherPastHistory,allergicHistory=:allergicHistory")
					.append(" where empiId =:empiId").toString();
			dao.doUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新孕产妇档案失败！", e);
		}
	}

	/**
	 * 获取产时记录
	 * 
	 * @param filedName
	 * @param idCard
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getDeliveryRecordByIdCard(
			String filedName, String idCard) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", filedName, "s", idCard);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MHC_DeliveryRecord);
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取产时记录失败！", e);
		}
	}

	/**
	 * 获取前次结案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastEndManageInfo(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select endDate as preGestationDate")
				.append(", gestationMode as preGestationMode from ")
				.append(MHC_EndManagement)
				.append(" where empiId=:empiId order by endDate desc")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取前次结案信息失败！", e);
		}
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取孕妇最后一条档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastPregnantRecord(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			List<Map<String, Object>> list = dao.doQuery(cnd,
					"pregnantId desc", MHC_PregnantRecord);
			if (list == null || list.size() < 1) {
				return null;
			} else {
				return list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇最后一条档案信息失败！", e);
		}
	}

	/**
	 * 根据empiId获取孕妇所有档案信息(连接查询)
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPregnantByEmpiIdJoin(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		try {
			return dao.doList(cnd, "a.createDate desc", MHC_PregnantRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取孕妇档案信息失败", e);
		}
	}

	/**
	 * 获取正常状态的孕妇信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPregnantRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			return dao.doQuery(cnd, "createDate desc", MHC_PregnantRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇档案信息失败！", e);
		}
	}

	/**
	 * 获取正常状态的孕妇信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPregnantRecordByPhrId(String phrId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "status", "s",
				RecordStatus.CANCEL);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doQuery(cnd, null, MHC_PregnantRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇档案信息失败！", e);
		}
	}

	/**
	 * 获取正常状态的孕妇信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getNormalPregnantRecord(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "status", "s",
				Constants.CODE_STATUS_NORMAL);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doQuery(cnd, null, MHC_PregnantRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取正常状态的孕妇档案信息失败！", e);
		}
	}

	/**
	 * 获取妇女基本信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWomanRecord(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			return dao.doLoad(cnd, MHC_WomanRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女基本信息失败！", e);
		}
	}

	/**
	 * 查询第一条妇女产后随访信息
	 * 
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFirstPostnatalVisitInfo(String pregnantId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "pregnantId", "s",
				pregnantId);
		try {
			List<Map<String, Object>> list = dao.doQuery(cnd, "visitId",
					MHC_PostnatalVisitInfo);
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女产后随访信息失败！", e);
		}
	}

	/**
	 * 查询第一条新生儿访视基本信息
	 * 
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getFirstBabyVisitInfo(String pregnantId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "pregnantId", "s",
				pregnantId);
		try {
			List<Map<String, Object>> list = dao.doQuery(cnd, "babyId",
					MHC_BabyVisitInfo);
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取新生儿访视基本信息失败！", e);
		}
	}

	/**
	 * 获取末次月经时间以及相应的孕妇档案号。
	 * 
	 * @param empiId
	 * @param session
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastMenstrualPeriod(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select pregnantId as pregnantId,")
				.append("lastMenstrualPeriod as lastMenstrualPeriod from ")
				.append(MHC_PregnantRecord)
				.append(" where empiId=:empiId and status=:status and ")
				.append("lastMenstrualPeriod=(select max(lastMenstrualPeriod) from ")
				.append(MHC_PregnantRecord)
				.append(" where empiId=:empiId and status=:status)").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("status", Constants.CODE_STATUS_NORMAL);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇上次末次月经信息失败！", e);
		}
		return map;
	}

	/**
	 * 获取用户末次月经时间
	 * 
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getGsetational(String pregnantId)
			throws ModelDataOperationException {
		if (pregnantId == null || pregnantId.trim().length() == 0) {
			return null;
		}
		StringBuffer hql = new StringBuffer(
				"select lastMenstrualPeriod as lastMenstrualPeriod ")
				.append(" from ").append(MHC_PregnantRecord)
				.append(" where pregnantId = :pregnantId ");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		Map<String, Object> data;
		try {
			data = dao.doLoad(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇末次月经信息失败！", e);
		}
		return (Date) data.get("lastMenstrualPeriod");
	}

	/**
	 * 保存孕妇档案
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePregnantRecord(Map<String, Object> data,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_PregnantRecord,
					data, true);
			return genValues;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇档案失败！", e);
		}
	}

	/**
	 * 保存孕妇首次随访信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePregnantFirstVisit(Map<String, Object> data,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					MHC_FirstVisitRecord, data, true);
			return genValues;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇首次随访信息失败！", e);
		}
	}

	/**
	 * 修改高危随访一览表的随访时间
	 * 
	 * @param screeningDate
	 * @param pregnantId
	 * @throws ModelDataOperationException
	 */
	public void updateHighRiskVisitReasonList(String screeningDate,
			String pregnantId) throws ModelDataOperationException {
		String sb = new StringBuilder("update ")
				.append("MHC_HighRiskVisitReasonList")
				.append(" set visitDate = :visitDate")
				.append(" where pregnantId = :pregnantId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		param.put("visitDate", BSCHISUtil.toDate(screeningDate));
		try {
			dao.doUpdate(sb, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇高危随访一览表信息失败！", e);
		}
	}

	/**
	 * 删除孕妇高危因素
	 * 
	 * @param pregnantId
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteHighRiskness(String pregnantId, String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MHC_HighRiskVisitReason)
				.append(" where pregnantId = :pregnantId ")
				.append("and visitId = :visitId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		param.put("visitId", visitId);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除孕妇高危因素失败！", e);
		}
	}

	/**
	 * 删除孕妇高危一览表
	 * 
	 * @param pregnantId
	 * @throws ModelDataOperationException
	 */
	public void deleteHighRisknessReasonsList(String pregnantId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MHC_HighRiskVisitReasonList)
				.append(" where pregnantId = :pregnantId ").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除孕妇高危一览表失败！", e);
		}
	}

	/**
	 * 删除孕妇体检信息
	 * 
	 * @param pregnantId
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deletePregnantCheckList(String pregnantId, Object visitId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("delete from ").append(
				MHC_PregnantWomanIndex).append(
				" where pregnantId = :pregnantId ");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		if (visitId == null) {
			hql.append(" and visitId is  null ");
		} else {
			hql.append(" and visitId = :visitId");
			param.put("visitId", visitId);
		}
		try {
			dao.doUpdate(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除孕妇体检信息失败！", e);
		}
	}

	/**
	 * 保存孕妇高危因素
	 * 
	 * @param body
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void saveHighRisknessReasons(HashMap<String, Object> body)
			throws ModelDataOperationException, ValidateException {
		List<Object> highRisknesses = (List<Object>) body.get("highRisknesses");
		if (highRisknesses == null || highRisknesses.size() < 1) {
			return;
		}
		String pregnantId = (String) body.get("pregnantId");
		String visitId = (String) body.get("visitId");
		String empiId = (String) body.get("empiId");
		for (int i = 0; i < highRisknesses.size(); i++) {
			HashMap<String, Object> reason = (HashMap<String, Object>) highRisknesses
					.get(i);
			reason.put("visitId", visitId);
			reason.put("pregnantId", pregnantId);
			reason.put("empiId", empiId);
			try {
				dao.doSave("create", MHC_HighRiskVisitReason, reason, true);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存孕妇高危因素失败！", e);
			}
		}
	}

	/**
	 * 保存孕妇体检信息
	 * 
	 * @param checkList
	 * @param pregnantId
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void savePregnantCheckList(List<Object> checkList,
			String pregnantId, String visitId)
			throws ModelDataOperationException, ValidateException {
		if (checkList == null || checkList.size() < 1) {
			return;
		}
		for (int i = 0; i < checkList.size(); i++) {
			HashMap<String, Object> check = (HashMap<String, Object>) checkList
					.get(i);
			check.put("pregnantId", pregnantId);
			check.put("visitId", visitId);
			try {
				dao.doSave("create", MHC_PregnantWomanIndex, check, true);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存孕妇体检信息失败！", e);
			}
		}
	}

	/**
	 * 获取孕妇产前筛查的筛查日期
	 * 
	 * @param pregnantId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getPregnantScreenDate(String pregnantId)
			throws ModelDataOperationException {
		String sb = new StringBuilder(
				"select screeningDate as screeningDate from ")
				.append(MHC_PregnantScreen)
				.append(" where pregnantId=:pregnantId").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		Map<String, Object> data;
		try {
			data = dao.doLoad(sb, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇产前筛查的筛查日期失败！", e);
		}
		if (data != null && data.size() > 0) {
			return (Date) data.get("screeningDate");
		} else {
			return null;
		}
	}

	/**
	 * 保存孕妇高危一览表
	 * 
	 * @param body
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveHighRisknessReasonsList(HashMap<String, Object> body)
			throws ValidateException, ModelDataOperationException {
		List<Object> reasons = (List<Object>) body.get("highRisknesses");
		if (reasons == null || reasons.size() < 1) {
			return;
		}
		String pregnantId = (String) body.get("pregnantId");
		String visitId = (String) body.get("visitId");
		String empiId = (String) body.get("empiId");
		Date visitDate = (Date) SchemaUtil
				.getValue(MHC_HighRiskVisitReasonList, "visitDate",
						body.get("visitDate"));
		Integer highRiskScore = (Integer) SchemaUtil.getValue(
				MHC_HighRiskVisitReasonList, "highRiskScore",
				body.get("highRiskScore"));
		String highRiskLevel = (String) body.get("highRiskLevel");
		HashMap<String, Object> reasonReq = new HashMap<String, Object>();
		reasonReq.put("visitId", visitId);
		reasonReq.put("pregnantId", pregnantId);
		reasonReq.put("empiId", empiId);
		reasonReq.put("visitDate", visitDate);
		reasonReq.put("highRiskScore", highRiskScore);
		reasonReq.put("highRiskLevel", highRiskLevel);
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < reasons.size(); i++) {
			HashMap<String, Object> reason = (HashMap<String, Object>) reasons
					.get(i);
			sb.append(reason.get("highRiskReasonId"));
			sb.append(",");
		}
		String str = sb.toString();
		reasonReq.put("highRiskReasonId", str.substring(0, str.length() - 1));
		reasonReq.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		reasonReq.put("lastModifyDate", BSCHISUtil.toString(new Date(), null));
		try {
			dao.doSave("create", MHC_HighRiskVisitReasonList, reasonReq, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇高危一览表失败！", e);
		}
	}

	/**
	 * 从提交的孕妇档案里更新妇女健康档案
	 * 
	 * @param data
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public void updateWomanRecord(HashMap<String, Object> data,
			VindicateLogService vLogService)
			throws ModelDataOperationException, ServiceException {
		String empiId = (String) data.get("empiId");
		String phrId = (String) data.get("phrId");
		Map<String, Object> map = getWomanRecord(empiId);
		String op;
		if (map == null || map.size() < 1) {
			op = "create";
		} else {
			op = "update";
			data.put("modifyDate", new Date());
			String unit = UserUtil.get(UserUtil.MANAUNIT_ID);
			data.put("modifyUnit", unit);
		}
		data.put("modifyRecordId", data.get("pregnantId"));
		try {
			dao.doSave(op, MHC_WomanRecord, data, true);
			vLogService.saveVindicateLog(MHC_WomanRecord, op, phrId, dao,
					empiId);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存妇女基本信息失败！", e);
		}
	}

	/**
	 * 获取孕妇首条高危因素
	 * 
	 * @param pregnantId
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFirstHighRiskReasons(String pregnantId,
			String visitId) throws ModelDataOperationException {
		String hql = new StringBuffer("select frequence as frequence from ")
				.append(MHC_HighRiskVisitReason)
				.append(" where pregnantId=:pregnantId and visitId=:visitId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		param.put("visitId", visitId);
		try {
			return dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇高危因素周期失败！", e);
		}
	}

	/**
	 * 获取孕妇体检信息
	 * 
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPregnantCheckUp(List<?> cnd)
			throws ModelDataOperationException {
		try {
			return dao.doQuery(cnd, "indexId", MHC_PregnantWomanIndex);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取孕妇体检信息失败！", e);
		}
	}

	/**
	 * 注销档案信息
	 * 
	 * @param whereField
	 * @param whereValue
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logOutPregnantRecord(String whereField, String whereValue,
			String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("MHC_PregnantRecord").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ").append(" where ")
				.append(whereField).append(" = :whereValue")
				.append(" and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销档案信息失败!", e);
		}
	}

	/**
	 * 保存孕妇终止管理信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveEndManageRecord(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doSave(op, MHC_EndManagement, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇终止管理信息失败！", e);
		}
	}

	/**
	 * 更新孕妇档案的状态。
	 * 
	 * @param pregnantId
	 * @param status
	 * @throws ModelDataOperationException
	 */
	public void updatePregnantRecordStatus(String pregnantId, String status)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MHC_PregnantRecord")
				.append(" set status=:status,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate where pregnantId = :pregnantId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("pregnantId", pregnantId);
		param.put("status", status);
		param.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		param.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改孕妇状态失败！", e);
		}
	}

	/**
	 * 孕妇随访计划结案
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	public void closePregnantVisitPlan(String recordId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus, lastModifyUser=:lastModifyUser, lastModifyUnit = :lastModifyUnit ,")
				.append(" lastModifyDate=:lastModifyDate where planStatus=:planStatus1 ")
				.append(" and  recordId = :recordId and businessType in ( :businessType1, :businessType2)");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		map.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		map.put("lastModifyDate", new Date());
		map.put("planStatus", PlanStatus.CLOSE);
		map.put("planStatus1", PlanStatus.NEED_VISIT);
		map.put("recordId", recordId);
		map.put("businessType1", BusinessType.MATERNAL);
		map.put("businessType2", BusinessType.PREGNANT_HIGH_RISK);
		try {
			dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销计划信息失败!", e);
		}
	}

	/**
	 * 保存孕妇产前筛查信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePregnantScreen(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_PregnantScreen,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕妇产前筛查信息失败。", e);
		}
	}

	/**
	 * 保存孕妇特殊情况信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveSpecialRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_PregnantSpecial,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕妇特殊情况信息失败。", e);
		}
	}

	/**
	 * 保存孕妇产后42天健康检查信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePostnatal42dayRecord(
			Map<String, Object> data, String op, Map<String, Object> res)
			throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					MHC_Postnatal42dayRecord, data, false);
			// res.put("body", genValues);
			return genValues;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕妇产后42天健康检查信息失败。", e);
		}
	}

	/**
	 * 保存孕妇产后访视信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePostnatalVisitInfo(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					MHC_PostnatalVisitInfo, data, false);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕妇产后访视信息失败。", e);
		}
	}

	/**
	 * 保存新生儿访视信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveBabyVisitInfo(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_BabyVisitInfo,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿访视信息失败。", e);
		}
	}

	/**
	 * 保存新生儿随访信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveBabyVisitRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_BabyVisitRecord,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿随访信息失败。", e);
		}
	}

	/**
	 * 获取终止管理列表数据
	 * 
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryEndManagement(Map<String, Object> req)
			throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		String schemaName = (String) req.get("schema");
		if (StringUtils.isEmpty(schemaName) || queryCnd == null
				|| queryCnd.size() == 0) {
			new ModelDataOperationException(Constants.CODE_INVALID_REQUEST,
					"参数获取失败！");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}

		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
					schemaName, pageNo, pageSize, queryCndsType);
			res.putAll(rsMap);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
		}
		return res;
	}

	/**
	 * 恢复妇保档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertPregnantRecord(String pregnantId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MHC_PregnantRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where pregnantId = :pregnantId ")
				.append(" and status = :status1").toString();

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
		parameters.put("pregnantId", pregnantId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复妇保档案失败！", e);
		}
	}

	/**
	 * 保存孕妇产时信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveDeliveryRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					MHC_DeliveryOnRecord, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇产时信息失败!", e);
		}
	}

	/**
	 * 保存孕妇产时新生儿信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDeliveryChildren(Map<String, Object> data,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					MHC_DeliveryOnRecordChild, data, true);
			return genValues;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存孕妇产时新生儿信息失败!", e);
		}
	}

	/**
	 * 孕妇html页面的初始化数据拼接
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> LoadHtmlData(Map<String, Object> body)
			throws ModelDataOperationException {
		String pregnantId = body.get("pregnantId") + "";
		String empiId = (String) body.get("empiId");
		Map<String, Object> backMap = new HashMap<String, Object>();// 返回的MAP

		//@@个人基本信息
		EmpiModel eModel = new EmpiModel(dao);
		Map<String,Object> bMap = eModel.getEmpiInfoByEmpiid(empiId);
		//@@健康档案
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> hrMap = hrModel.getHealthRecordListByEmpiId(empiId);
		//@@妇女档案
		Map<String, Object> BackMap_DA = new HashMap<String, Object>();
		if(S.isNotEmpty(pregnantId)){
			BackMap_DA = this.getPregnantRecord(pregnantId);
		}
		if(BackMap_DA == null){
			backMap.put("KEY_DA", "creat");// 返回前台判断是否有档案
			BackMap_DA = new HashMap<String, Object>();
		}
		//妇科手术史
		String gynecologyOPS = (String) BackMap_DA.get("gynecologyOPS");
		if(S.isEmpty(gynecologyOPS)){
			BackMap_DA.put("gynecologyOPS", "1");//无
		}else{
			BackMap_DA.put("gynecologyOPS", "2");//有
			BackMap_DA.put("gynecologyOPS_other", gynecologyOPS);//
		}
		//血型
		BackMap_DA.put("bloodTypeCode", bMap.get("bloodTypeCode"));// 血型
		BackMap_DA.put("rhBloodCode", bMap.get("rhBloodCode"));// rh血型
		//计算妇女年龄 age
		Date birthday = (Date) bMap.get("birthday");
		BackMap_DA.put("age", BSCHISUtil.calculateAge(birthday, null));
		//获取丈夫信息
		if(hrMap != null && hrMap.size() > 0){
			String partnerId = (String) hrMap.get("partnerId");//配偶的empiId
			if(S.isNotEmpty(partnerId)){
				Map<String, Object> pMap = eModel.getEmpiInfoByEmpiid(partnerId);
				BackMap_DA.put("husbandName", pMap.get("personName"));//丈夫姓名
				BackMap_DA.put("husbandPhone", pMap.get("mobileNumber"));//丈夫电话
				Date husbandBirthDay = (Date) pMap.get("birthday");
				BackMap_DA.put("husbandAGE", BSCHISUtil.calculateAge(husbandBirthDay, null));//丈夫年龄
			}
		}
		backMap.put("KEY_DA", "update");// 返回前台判断是否有档案
		backMap.put("DA", BackMap_DA);
		//@@获取首次随访信息
		Map<String, Object> BackMap_SF = null;
		if(S.isNotEmpty(pregnantId)){
			BackMap_SF = this.getPregnantFirstVisitRecord(pregnantId);
		}
		if(BackMap_SF != null && BackMap_SF.size() > 0){
			backMap.put("KEY_SF", "update");// 返回前台判断是否有随访
		}else{
			backMap.put("KEY_DA", "creat");// 返回前台判断是否有档案
			BackMap_SF = new HashMap<String, Object>();
			BackMap_SF.put("visitDoctorCode", UserUtil.get(UserUtil.USER_ID));
		}
		backMap.put("SF", BackMap_SF);
		//@@获取检验检查信息
		try {
			// 对检验检查MHC_PregnantWomanIndex进行查看，是否有数据
			Map<String, Object> JY_sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_JY = new HashMap<String, Object>();
			param_JY.put("pregnantId", pregnantId);
			String hql_JY = new StringBuffer(
					"select count(pregnantId) as size ")
					.append(" from ")
					.append(MHC_PregnantWomanIndex)
					.append(" where pregnantId = :pregnantId and indexValue  IS  NOT  null ")
					.toString();
			if(S.isNotEmpty(pregnantId)){
				JY_sizeMap = dao.doLoad(hql_JY, param_JY);
			}
			if (!"".equals(JY_sizeMap) && !"null".equals(JY_sizeMap)
					&& JY_sizeMap != null) {
				// 随访表MHC_PregnantWomanIndex有数据，查询数据返回到前台
				String size = JY_sizeMap.get("size") + "";
				if (!"0".equals(size) && size != "0") {
					List<Map<String, Object>> BackList_JY;
					String List_JY = new StringBuffer(
							"select pregnantId as pregnantId,empiId as empiId ,indexCode as indexCode ,indexName as indexName,indexValue as indexValue ,"
									+ " ifException as ifException, exceptionDesc as exceptionDesc,referenceValue as referenceValue"
									+ " ,ifException as ifException ")
							.append(" from ")
							.append(MHC_PregnantWomanIndex)
							.append(" where pregnantId = :pregnantId  and (indexValue  IS  NOT  null or ifException  IS  NOT  null  ) ")
							.toString();
					Map<String, Object> param_List = new HashMap<String, Object>();
					param_List.put("pregnantId", pregnantId);
					Map<String, Object> BackMap_JY = new HashMap<String, Object>();
					BackList_JY = dao.doQuery(List_JY, param_List);
					if (BackList_JY.size() > 0) {
						for (int i = 0; i < BackList_JY.size(); i++) {
							Map<String, Object> sizeMap = new HashMap<String, Object>();
							sizeMap = BackList_JY.get(i);
							String indexCode = sizeMap.get("indexCode") + "";
							String indexName = sizeMap.get("indexName") + "";
							String indexValue = sizeMap.get("indexValue") + "";
							String ifException = sizeMap.get("ifException")
									+ "";
							String exceptionDesc = sizeMap.get("exceptionDesc")
									+ "";
							BackMap_JY.put("JY_" + indexCode, indexValue);// 指标值
							BackMap_JY.put("JY_" + indexCode + "_name",
									indexName);// 名称
							BackMap_JY.put("JY_" + indexCode + "_if",
									ifException);// 是否异常
							BackMap_JY.put("JY_" + indexCode + "_other",
									exceptionDesc);// 异常描述
						}
					}
					// BackMap_JY.put("JY", BackList_JY);
					backMap.put("JY", BackMap_JY);
					backMap.put("KEY_JY", "update");// 返回前台判断是否有检验
				}
				// backMap.put("KEY_JY", "creat");// 返回前台判断是否有检验
			} else {
				backMap.put("KEY_JY", "creat");// 返回前台判断是否有检验
			}
			//System.out.println("-----------backMap=" + backMap.toString());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女检验检查信息失败！", e);
		}
		return backMap;
	}

	/**
	 * 孕妇html页面doSave
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> SaveHtmlData(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> backBody = new HashMap<String, Object>();
		// 对孕妇档案MHC_PregnantRecord进行查看，是否有数据
		Map<String, Object> Array_Map = (Map<String, Object>) body.get("data");
		// Map<String, Object> DA_Map = (Map<String, Object>)
		// Array_Map.get("DA");
		Map<String, Object> JY_Map = (Map<String, Object>) Array_Map.get("JY");
		String pregnantId = body.get("pregnantId") + "";
		try {
			Map<String, Object> param_DA = new HashMap<String, Object>();
			param_DA.put("pregnantId", pregnantId);
			// 对检验检查MHC_PregnantWomanIndex进行查看，是否有数据
			Map<String, Object> JY_sizeMap = new HashMap<String, Object>();
			Map<String, Object> param_JY = new HashMap<String, Object>();
			ArrayList<?> JyKey = (ArrayList<?>) JY_Map.get("JyKey");
			Dictionary mgDic = null;
			try {
				mgDic = DictionaryController.instance().get(
						BSCHISEntryNames.PWI_DIC);
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(
						Constants.CODE_TARGET_EXISTS, PWI_DIC + " 字典不存在！", e1);
			}
			if (JyKey != null && JyKey.size() > 0) {
				for (int i = 0; i < JyKey.size(); i++) {
					String indexCode = JyKey.get(i) + "";
					param_JY.put("pregnantId", pregnantId);
					param_JY.put("indexCode", indexCode);
					String hql_JY = new StringBuffer(
							"select indexId as indexId ")
							.append(" from ")
							.append(MHC_PregnantWomanIndex)
							.append(" where pregnantId = :pregnantId and indexCode = :indexCode ")
							.toString();
					JY_sizeMap = dao.doLoad(hql_JY, param_JY);
					Map<String, Object> set_JY = new HashMap<String, Object>();
					if(mgDic != null){
						set_JY.put("indexName", mgDic.getText(indexCode));
					}
					if (!"".equals(pregnantId) && pregnantId != null
							&& !"null".equals(pregnantId)
							&& !"".equals(JY_sizeMap)
							&& !"null".equals(JY_sizeMap) && JY_sizeMap != null) {// doUpdate
						// 随访表MHC_PregnantWomanIndex有数据，update
						set_JY.put("indexId", JY_sizeMap.get("indexId"));
						set_JY.put("pregnantId", pregnantId);
						set_JY.put("indexValue", JY_Map.get("JY_" + indexCode));
						if (!"".equals(JY_Map.get("JY_" + indexCode + "_other"))
								&& !"null".equals(JY_Map.get("JY_" + indexCode
										+ "_other"))
								&& JY_Map.get("JY_" + indexCode + "_other") != null) {
							set_JY.put("exceptionDesc",
									JY_Map.get("JY_" + indexCode + "_other"));
						}
						if (!"".equals(JY_Map.get("JY_" + indexCode + "_if"))
								&& !"null".equals(JY_Map.get("JY_" + indexCode
										+ "_if"))
								&& JY_Map.get("JY_" + indexCode + "_if") != null) {
							set_JY.put("indexValue",
									JY_Map.get("JY_" + indexCode + "_if"));
						}
						// String mapKay = "JY_" + indexCode + "_other";//
						// 判断是否有key
						// Iterator keys = JY_Map.keySet().iterator();
						// while (keys.hasNext()) {
						// String key = (String) keys.next();
						// if (mapKay.equals(key)) {
						// set_JY.put("exceptionDesc",
						// JY_sizeMap.get(mapKay));
						// }
						// }
						try {
							dao.doSave("update", MHC_PregnantWomanIndex,
									set_JY, true);
						} catch (ValidateException e) {
							backBody.put("update_JY", "false");// 返回前台判断是否有档案
							e.printStackTrace();
						}
						backBody.put("update_JY", "true");// 返回前台判断是否有档案
					} else {// doSave
							// 随访表MHC_PregnantWomanIndex无数据，插入
							// set_JY.put("indexId", JY_sizeMap.get("indexId"));
						set_JY.put("indexValue", JY_Map.get("JY_" + indexCode));
						set_JY.put("pregnantId", pregnantId);
						set_JY.put("indexCode", indexCode);
						if (!"".equals(JY_Map.get("JY_" + indexCode + "_other"))
								&& !"null".equals(JY_Map.get("JY_" + indexCode
										+ "_other"))
								&& JY_Map.get("JY_" + indexCode + "_other") != null) {
							set_JY.put("exceptionDesc",
									JY_Map.get("JY_" + indexCode + "_other"));
						}
						if (!"".equals(JY_Map.get("JY_" + indexCode + "_if"))
								&& !"null".equals(JY_Map.get("JY_" + indexCode
										+ "_if"))
								&& JY_Map.get("JY_" + indexCode + "_if") != null) {
							set_JY.put("indexValue",
									JY_Map.get("JY_" + indexCode + "_if"));
						}
						try {
							dao.doSave("create", MHC_PregnantWomanIndex,
									set_JY, true);
						} catch (ValidateException e) {
							backBody.put("creat_SF", "false");// 返回前台判断是否有档案
							e.printStackTrace();
						}
					}
				}
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

		return backBody;// 返回保存信息
	}

	/**
	 * 保存新生儿访视信息(html)
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveBabyVisitInfoToHtml(Map<String, Object> map)
			throws ValidateException, ModelDataOperationException {
		String babyId = (String) map.get("babyId");
		Map<String, Object> data = null;
		try {
			if (babyId != null && babyId.length() > 0) {
				data = dao.doSave("update", MHC_BabyVisitInfo, map, true);
			} else {
				data = dao.doSave("create", MHC_BabyVisitInfo, map, true);
				map.put("babyId", data.get("babyId"));
			}

			return map;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿访视基本信息失败！", e);
		}
	}

	/**
	 * 保存新生儿访视记录(html)
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveBabyVisitRecordToHtml(Map<String, Object> map)
			throws ValidateException, ModelDataOperationException {
		String visitId = (String) map.get("visitId");
		Map<String, Object> data = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			sb.append(" select visitId as visitId from ")
					.append(MHC_BabyVisitRecord)
					.append(" where babyId=:babyId and ")
					.append(" visitDate =to_date(:visitDate,'YYYY-MM-DD') ");
			param.put("babyId", map.get("babyId"));
			param.put("visitDate", map.get("visitDate"));
			if (visitId != null && visitId.length() > 0) {

				data = dao.doSave("update", MHC_BabyVisitRecord, map, true);
			} else {
				// 判断同个日期有没有信息，有，就update
				data = dao.doLoad(sb.toString(), param);
				if (data != null && data.size() > 0) {
					map.put("visitId", data.get("visitId"));
					data.clear();
					data = dao.doSave("update", MHC_BabyVisitRecord, map, true);
				} else {
					data = dao.doSave("create", MHC_BabyVisitRecord, map, true);
					map.put("visitId", data.get("visitId"));

				}

			}

			return map;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存新生儿访视记录失败！", e);
		}
	}

	/**
	 * 获取新生儿的随访记录
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> selectBabyVisitRecord(String visitId,
			String visitDate) throws ValidateException,
			ModelDataOperationException {

		Map<String, Object> data = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> datas = new HashMap<String, Object>();
		try {
			sb.append("select  visitId as visitId ,visitDate as visitDate,weight as weight,feedWay as feedWay ,eatNum as eatNum ,eatCount as eatCount,vomit as vomit,"
					+ " stoolStatus as stoolStatus,stoolTimes as stoolTimes,temperature as temperature,pulse as pulse,"
					+ " respiratoryFrequency as respiratoryFrequency,face as face,jaundice as jaundice,bregmaTransverse"
					+ " as bregmaTransverse,bregmaLongitudinal as bregmaLongitudinal,bregmaStatus as bregmaStatus,"
					+ " otherStatus as otherStatus,eye as eye,eyeAbnormal as eyeAbnormal,ear as ear,earAbnormal as earAbnormal,"
					+ " nose as nose,noseAbnormal as noseAbnormal ,mouse as mouse,mouseAbnormal as mouseAbnormal,anal as anal,"
					+ " heartlung as heartlung,heartLungAbnormal as heartLungAbnormal,genitalia as genitalia,genitaliaAbnormal as genitaliaAbnormal,"
					+ " spine as spine,spineAbnormal as spineAbnormal,umbilical as umbilical,umbilicalOther as umbilicalOther,"
					+ " abdominal as abdominal,abdominalabnormal as abdominalabnormal ,referral as referral,"
					+ " referralReason as referralReason,referralUnit as referralUnit ,guide as guide,visitDate as visitDate,"
					+ " nextVisitDate as nextVisitDate,nextVisitAddress as nextVisitAddress,visitDoctor as visitDoctor ,"
					+ " limbs as limbs,limbsAbnormal as limbsAbnormal,neck as neck,neck1 as neck1,skin as skin ,skinAbnormal as skinAbnormal from MHC_BabyVisitRecord"
					+ "  where visitId=:visitId and visitDate=to_date(:visitDate,'YYYY-MM-DD') ");
			if (visitId != null && visitId.length() > 0 && visitDate != null
					&& visitDate.length() > 0) {
				datas.put("visitId", visitId);
				datas.put("visitDate", visitDate);
				data = dao.doLoad(sb.toString(), datas);
				return data;
			}
			return null;

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取新生儿随访信息失败！", e);
		}
	}

	/**
	 * 保存新生儿访视记录(html)
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPregnantId(Map<String, Object> map)
			throws ValidateException, ModelDataOperationException {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			sb.append(" select pregnantId as pregnantId from MHC_PregnantRecord where empiId=:empiId ");
			param = dao.doLoad(sb.toString(), map);
			if (param != null && param.size() > 0) {

				return param;
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取孕妇档案编号失败！", e);
		}
	}

}
