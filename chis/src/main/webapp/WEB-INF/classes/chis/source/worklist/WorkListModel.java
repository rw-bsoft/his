/**
 * @(#)WorkListModel.java Created on 2012-5-20 下午4:49:33
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.worklist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import chis.source.dic.WorkType;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.validator.ValidateException;

/**
 * 
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class WorkListModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public WorkListModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据任务名称和管辖机构获取工作任务,主要应用于档案迁移确认工作任务的获取
	 * 
	 * @param manaUnitId
	 * @param workType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWrokList(String manaUnitId, String workType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select a.workType as workType,count(distinct mdcid) as count from MDC_Ehr a ");
				
//		StringBuffer hql = new StringBuffer(
//				"select a.workType as workType,count(*) as count from ")
//				.append("PUB_WorkList a ");
//		if (workType.equals(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT)) {
//			hql.append(",PSY_PsychosisRecord b ");
//		} else if (workType.equals(WorkType.HYPERTENSION_YEAR_FIXGROUP)) {
//			hql.append(",MDC_HypertensionRecord b ");
//		}
		hql.append(" where ehrjgdm like :manaUnitId and a.workType = :workType");
		hql.append(" group by a.workType ");
		//if (workType.equals(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT)) {
//			hql.append(" and a.empiId=b.empiId and b.status='0'");
//		} else if (workType.equals(WorkType.HYPERTENSION_YEAR_FIXGROUP)) {
//			hql.append(" and a.empiId=b.empiId and b.status='0'");
//		}
//		hql.append(" group by a.workType");
		Map<String, Object> param = new HashMap<String, Object>();
		manaUnitId=manaUnitId+'%';
		param.put("manaUnitId", manaUnitId);
		param.put("workType", workType);
		try {
			System.out.println(hql.toString());
			return dao.doLoad(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取工作任务失败", e);
		}
	}

	public boolean isExistWorkList(String recordId, String empiId,
			String workType, BaseDAO dao) {
		if (recordId == null) {
			return false;
		}
		if (empiId == null) {
			return false;
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "a.recordId", "s",
				recordId);
		List<Object> cnd3 = CNDHelper.createSimpleCnd("eq", "a.workType", "s",
				workType);
		List<Object> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		List<Map<String, Object>> list = null;
		if (WorkType.MDC_HYPERTENSIONRECORD.equals(workType)
				|| WorkType.MDC_DIABETESRECORD.equals(workType)) {
			cnd = CNDHelper.createArrayCnd("and", cnd3, cnd1);
		}
		try {
			list = dao.doList(cnd, "a.empiId", PUB_WorkList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 根据任务名称，用户编号，管辖机构以及开始结束时间获取工作任务
	 * 
	 * @param manaUnitId
	 * @param workType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWrokList(String userId, String manaUnitId,
			String workType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
		"select a.workType as workType,count(distinct idcard) as count from ")
		.append("mdc_fromhis a group by a.workType ");
//		StringBuffer hql = new StringBuffer(
//				"select a.workType as workType,count(distinct a.recordId) as count from ")
//				.append("PUB_WorkList a");
//		if (workType.equals(WorkType.MDC_HYPERTENSIONRECORD)) {
//			hql = new StringBuffer(
//					"select a.workType as workType,count(distinct a.empiId) as count from ")
//					.append("PUB_WorkList a");
//		}
//		if (workType.equals(WorkType.PERSON_PREVIOUS_HISTORY)
//				|| workType.equals(WorkType.MDC_DIABETESRECORD)
//				|| workType.equals(WorkType.MDC_HYPERTENSIONRECORD)) {
//			hql.append(",EHR_HealthRecord b ");
//		} else if (workType.equals(WorkType.DEF_LIMB_EVALUATE)
//				|| workType.equals(WorkType.DEF_LIMB_PLAN)) {
//			hql.append(",DEF_LimbDeformityRecord b ");
//		} else if (workType.equals(WorkType.DEF_BRAIN_EVALUATE)
//				|| workType.equals(WorkType.DEF_BRAIN_PLAN)) {
//			hql.append(",DEF_BrainDeformityRecord b ");
//		} else if (workType.equals(WorkType.DEF_INTELLECT_EVALUATE)
//				|| workType.equals(WorkType.DEF_INTELLECT_PLAN)) {
//			hql.append(",DEF_IntellectDeformityRecord b ");
//		} else if (workType.equals(WorkType.HEALTHCHECK_YEAR_WARN)) {
//			hql.append(",HC_HealthCheck b ");
//		} else if (workType.equals(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT)) {
//			hql.append(",PSY_PsychosisRecord b ");
//		}
//		if (workType.equals(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT)) {
//			hql.append(" where a.doctorId=:doctorId")
//					.append("  and a.workType = :workType")
//					.append(" and a.empiId=b.empiId and b.status='0'");
//		} else {
//			hql.append(" where  a.doctorId=:doctorId  ")
//					.append(" and a.beginDate <= :beginDate and a.endDate >= :endDate and a.workType = :workType ");
//		}
//		if (workType.equals(WorkType.PERSON_PREVIOUS_HISTORY)
//				|| workType.equals(WorkType.MDC_DIABETESRECORD)
//				|| workType.equals(WorkType.MDC_HYPERTENSIONRECORD)
//				|| workType.equals(WorkType.DEF_LIMB_EVALUATE)
//				|| workType.equals(WorkType.DEF_LIMB_PLAN)
//				|| workType.equals(WorkType.DEF_BRAIN_EVALUATE)
//				|| workType.equals(WorkType.DEF_BRAIN_PLAN)
//				|| workType.equals(WorkType.DEF_INTELLECT_EVALUATE)
//				|| workType.equals(WorkType.DEF_INTELLECT_PLAN)) {
//			hql.append(" and a.empiId=b.empiId and b.status='0'");
//		} else if (workType.equals(WorkType.HEALTHCHECK_YEAR_WARN)) {
//			hql.append(" and a.recordId = b.healthCheck ");
//		}
//		hql.append(" group by a.workType");
		Map<String, Object> param = new HashMap<String, Object>();
		//param.put("doctorId", userId);
		// param.put("manaUnitId", manaUnitId);
		if (!workType.equals(WorkType.PSYCHOSIS_ANNUAL_ASSESSMENT)) {
			param.put("beginDate", new Date());
			param.put("endDate", new Date());
		}
		param.put("workType", workType);
		try {
			return dao.doLoad(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取工作任务失败", e);
		}
	}

	/**
	 * 查询用户工作任务
	 * 
	 * @param userId
	 * @param workTypes
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public List<Map<String, Object>> getWorkSummaryList(String userId,
			List<String> workTypes) throws ModelDataOperationException {
		StringBuffer condition = new StringBuffer("workType in (");
		for (int i = 0; i < workTypes.size(); i++) {
			condition.append("'").append(workTypes.get(i)).append("',");
		}
		if (workTypes.size() == 0) {
			condition.append("'',");
		}

		condition.setLength(condition.length() - 1);
		condition.append(")");

		String hql = new StringBuffer(
				"select workType as workType,count(*) as count from ")
				.append(PUB_WorkList)
				.append(" where doctorId=:doctorId and manaUnitId=:manaUnitId and beginDate<=:beginDate and endDate>=:endDate and ")
				.append(condition).append(" group by workType").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doctorId", userId);
		map.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID));
		map.put("beginDate", new Date());
		map.put("endDate", new Date());
		try {
			return dao.doQuery(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取工作任务失败", e);
		}
	}

	/**
	 * 增加工作任务
	 * 
	 * @param m
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> insertWorkList(Map<String, Object> m)
			throws ValidateException, ModelDataOperationException {
		Map<String, Object> record = new HashMap<String, Object>();
		if (m == null) {
			throw new ModelDataOperationException("没有对应的参数列表");
		}
		if (m.get("workType") == null) {
			throw new ModelDataOperationException("没有对应的工作计划");
		}
		if (m.get("recordId") == null) {
			throw new ModelDataOperationException("档案号获取失败");
		}
		if (m.get("empiId") == null) {
			throw new ModelDataOperationException("empiId获取失败");
		}
		record.put("recordId",
				StringUtils.trimToEmpty((String) m.get("recordId")));
		record.put("empiId", StringUtils.trimToEmpty((String) m.get("empiId")));
		record.put("workType", m.get("workType"));
		record.put("count", m.get("count") == null ? "1" : m.get("count"));
		if (m.get("manaUnitId") != null) {
			record.put("manaUnitId", m.get("manaUnitId"));
		}
		if (m.get("doctorId") != null) {
			record.put("doctorId", m.get("doctorId"));
		}
		if (m.get("beginDate") != null) {
			record.put("beginDate", m.get("beginDate"));
		}
		if (m.get("endDate") != null) {
			record.put("endDate", m.get("endDate"));
		}
		if (m.get("otherId") != null) {
			record.put("otherId", m.get("otherId"));
		}
		Map<String, Object> result;
		try {
			result = dao.doInsert(PUB_WorkList, record, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "添加工作任务失败", e);
		}
		return result;
	}

	/**
	 * 删除工作任务
	 * 
	 * @param m
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> deleteWorkList(Map<String, Object> m)
			throws ValidateException, ModelDataOperationException {
		if (m == null || m.get("workType") == null) {
			throw new ModelDataOperationException("没有对应的工作任务");
		}
		StringBuffer hql = new StringBuffer(
				"delete from PUB_WorkList where 1=1 ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Iterator it = m.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			hql.append(" and ").append(key).append("=:").append(key);
			parameters.put(key, m.get(key));
		}
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除工作任务失败", e);
		}
		return parameters;
	}

	/**
	 * 删去往年未完成的评估任务
	 * 
	 * @param workType
	 * @throws ModelDataOperationException
	 */
	public void deleteLastYearTask(String workType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(PUB_WorkList)
				.append(" where workType=:workType").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("workType", workType);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删去往年未完成的评估任务失败！", e);
		}
	}

	/**
	 * 增加任务列表记录
	 * 
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void insertWorkListRecord(Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		try {
			dao.doInsert(PUB_WorkList, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "新增任务记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "新增任务记录失败！", e);
		}
	}

	/**
	 * 保存任务列表记录
	 * 
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void saveWorkListRecord(Map<String, Object> record, String op,
			boolean validate) throws ModelDataOperationException {
		try {
			dao.doSave(op, PUB_WorkList, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存任务记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存任务记录失败！", e);
		}
	}

	/**
	 * 修改任务列表管辖机构
	 * 
	 * @param manaUnitId
	 * @param recordId
	 * @param workType
	 * @throws ModelDataOperationException
	 */
	public void updateWorkListManageUnit(String manaUnitId, String recordId,
			String workType) throws ModelDataOperationException {
		String hql = new StringBuffer(" update ")
				.append(PUB_WorkList)
				.append(" set  manaUnitId = :manaUnitId ")
				.append(" where recordId = :recordId and  workType = :workType")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("manaUnitId", manaUnitId);
		param.put("recordId", recordId);
		param.put("workType", workType);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改任务记录失败！", e);
		}
	}

	/**
	 * 以工作类型查询数据
	 * 
	 * @param workType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getWorkSummaryList(String workType)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "workType", "s", workType);
		try {
			return dao.doQuery(cnd, null, PUB_WorkList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "以工作类型查询数据失败！", e);
		}
	}

	/**
	 * 根据主键删除工作任务提醒
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void deleteWorkListByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, PUB_WorkList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据主键删除工作任务提醒失败！", e);
		}
	}

	/**
	 * 根据档案编号和任务编号删除工作任务提醒
	 * 
	 * @param recordId
	 * @param workType
	 * @throws ModelDataOperationException
	 */
	public void deleteWorkList(String recordId, String workType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(PUB_WorkList)
				.append(" where recordId = :recordId and workType = :workType")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("recordId", recordId);
		param.put("workType", workType);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除工作任务提醒失败！", e);
		}
	}

	/**
	 * 根据任务名称，用户编号，管辖机构以及开始结束时间获取工作任务
	 * 
	 * @param manaUnitId
	 * @param workType
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<Integer, Object> getMonthPlanReminderList(
			Map<String, Object> map, List<Integer> reminderTypes)
			throws ModelDataOperationException {
		List list = new ArrayList();
		for (Integer type : reminderTypes) {
			String reminderType = String.valueOf(type);
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) as cnt, a.businessType as businessType,a.planDate as planDate from PUB_VisitPlan a,");
			if (reminderType.equals(BusinessType.GXY)) {
				sql.append("MDC_HypertensionRecord").append(" b");
			} else if (reminderType.equals(BusinessType.TNB)) {
				sql.append("MDC_DiabetesRecord").append(" b");
			} else if (reminderType.equals(BusinessType.LNR)) {
				sql.append("MDC_OldPeopleRecord").append(" b");
			} else if (reminderType.equals(BusinessType.PSYCHOSIS)) {
				sql.append("PSY_PsychosisRecord").append(" b");
			} else if (reminderType.equals(BusinessType.CD_IQ)
					|| reminderType.equals(BusinessType.CD_CU)
					|| reminderType.equals(BusinessType.CD_DC)) {
				sql.append("CDH_HealthCard").append(" b");
			} else if (reminderType.equals(BusinessType.CD_DC)) {
				sql.append("CDH_DebilityChildren").append(" b");
			} else if (reminderType.equals(BusinessType.MATERNAL)
					|| reminderType.equals(BusinessType.PREGNANT_HIGH_RISK)) {
				sql.append("MHC_PregnantRecord").append(" b");
			}
			sql.append(" where a.businessType = :businessType");
			parameters.put("businessType", reminderType);
			if (reminderType.equals(BusinessType.GXY)
					|| reminderType.equals(BusinessType.TNB)
					|| reminderType.equals(BusinessType.LNR)
					|| reminderType.equals(BusinessType.PSYCHOSIS)) {
				sql.append(" and b.manaDoctorId = :manaDoctorId");
				parameters.put("manaDoctorId", UserUtil.get(UserUtil.USER_ID));
			} else if (reminderType.equals(BusinessType.CD_IQ)
					|| reminderType.equals(BusinessType.CD_CU)
					|| reminderType.equals(BusinessType.CD_DC)
					|| reminderType.equals(BusinessType.MATERNAL)
					|| reminderType.equals(BusinessType.PREGNANT_HIGH_RISK)) {
				sql.append(" and b.manaUnitId like :manaUnitId");
				parameters.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID)
						+ "%");
			}
			sql.append(" and a.empiId = b.empiId");
			sql.append(" and a.planStatus = :planStatus");
			// add
			sql.append(" and a.planDate>:firstDay");
			sql.append(" and a.planDate<:lastDay");
			// sql.append(" and a.beginDate<=:currentDay");
			// add
			sql.append(" group by a.businessType,a.planDate order by a.businessType");
			parameters.put("planStatus", PlanStatus.NEED_VISIT);
			// add
			parameters.put("firstDay", map.get("firstDay"));
			parameters.put("lastDay", map.get("lastDay"));
			// parameters.put("currentDay", map.get("currentDay"));
			// add
			try {
				list.addAll(dao.doQuery(sql.toString(), parameters));
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("获取工作任务失败", e);
			}
		}
		Map<Integer, Object> m = new HashMap<Integer, Object>();
		for (int i = 0; i < list.size(); i++) {
			Map record = (Map) list.get(i);
			Map dayMap = new HashMap();
			dayMap.put("cnt", record.get("cnt"));
			dayMap.put("businessType", record.get("businessType"));
			Calendar c = Calendar.getInstance();
			c.setTime((Date) record.get("planDate"));
			int key = c.get(Calendar.DAY_OF_MONTH);
			if (m.get(key) == null) {
				List t = new ArrayList();
				t.add(dayMap);
				m.put(key, t);
			} else {
				((List) m.get(key)).add(dayMap);
			}
		}
		return m;
	}
}
