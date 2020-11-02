/**
 * @(#)PsychosisRecordModel.java Created on 2012-3-27 下午14:23:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.psy;

import java.math.BigDecimal;
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
import chis.source.dic.PsyVisitType;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitType;
import chis.source.dic.YesNo;
import chis.source.log.VindicateLogService;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsychosisVisitModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public PsychosisVisitModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取最后正常的随访记录
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastNormalVisitRecord(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append("PSY_PsychosisVisit")
				.append(" where visitEffect = :visitEffect and phrId = :phrId")
				.append(" order by visitDate desc ").toString();
		List<Map<String, Object>> rsList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitEffect", VisitEffect.CONTINUE);
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取正常高血压随访记录失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return (Map<String, Object>) rsList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 获取精神病首次随访记录
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsyFirstVisitRecordByPhrId(String phrId)
			throws ModelDataOperationException {
		String cndString = "['and',['eq', ['$', 'a.phrId'],['s', '" + phrId
				+ "']],['eq', ['$', 'a.type'], ['s', '0']]]";
		List<?> cnd = null;
		try {
			cnd = CNDHelper.toListCnd(cndString);
		} catch (ExpException e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "表达式转换错误!", e1);
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "visitId desc", PSY_PsychosisFirstVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取精神病首次随访记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 依据empiId获取精神病首次随访类型
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getPsyFirstVisitType(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select visitType as visitType from ")
				.append("PSY_PsychosisVisit")
				.append(" where empiId=:empiId and type = :type").toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("type", VisitType.FIRST_VISIT);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访类型失败！", e);
		}
		return rsList != null && rsList.size() > 0 ? (String) rsList.get(0)
				.get("visitType") : "";
	}

	/**
	 * 获取随访类型
	 * 
	 * @return
	 */
	public Map<String, Object> getPsyVisitByPkey(String visitId)
			throws ModelDataOperationException {

		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PSY_PsychosisVisit, visitId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访原随访记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 删除一条精神病随访记录
	 * 
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deletePsyVisitByPkey(String visitId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(visitId, PSY_PsychosisVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除精神病随访记录失败！", e);
		}
	}

	// ----------- 上：首次随访 ---下：一般随访-------------
	/**
	 * 保存随访记录
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePsyVisitRecord(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 获取精神病随访计划记录数
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Long getPsyVisitPlanNumber(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as numCount from ")
				.append("PUB_VisitPlan")
				.append(" a where a.recordId=:phrId and a.businessType=:businessType and a.planStatus=:planStatus")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("businessType", BusinessType.PSYCHOSIS);
		parameters.put("planStatus", PlanStatus.VISITED);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取精神病随访计划记录数失败！", e);
		}
		return rsMap != null ? (Long) rsMap.get("numCount") : 0;
	}

	/**
	 * 依据phrId检索精神病随访记录
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsychosisVisitRecordByPhrId(String phrId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "visitId desc", PSY_PsychosisVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检索精神病随访记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 依据档案号及随访类型获取随访记录[visitdate 降序]
	 * 
	 * @param phrId
	 * @param type
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsyVisitRecord(String phrId, String type)
			throws ModelDataOperationException {
		String pvhql = new StringBuffer(" from ").append("PSY_PsychosisVisit")
				.append(" where type = :type and phrId = :phrId")
				.append(" order by visitdate desc ").toString();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phrId", phrId);
		paramMap.put("type", type);

		List<Map<String, Object>> pvrList = null;
		try {
			pvrList = dao.doQuery(pvhql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取精神病随访记录失败！", e);
		}
		return pvrList;
	}

	/**
	 * 依据empiId 获取随访记录数
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Long getPsyVisitRecordNumberByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as numCount from ")
				.append("PSY_PsychosisVisit")
				.append(" where empiId=:empiId and type=:type").toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("type", VisitType.COMMON_VISIT);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录数失败！", e);
		}

		return rsMap != null ? (Long) rsMap.get("numCount") : 0;
	}

	/**
	 * 获取精神病随访信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsyVisitInfoList(String phrId)
			throws ModelDataOperationException {
		String fhql = new StringBuffer(
				"select a.visitType,a.visitDate,a.type,a.isReferral,b.recordId,b.empiId,b.sn from ")
				.append("PSY_PsychosisVisit").append(" a, ")
				.append("PUB_VisitPlan").append(" b ")
				.append(" where a.visitId = b.visitId ")
				.append(" and b.businessType = :businessType ")
				.append(" and recordId = :recordId ")
				.append(" order by a.visitDate desc ").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", String.valueOf(PlanStatus.NEED_VISIT));
		parameters.put("recordId", phrId);
		parameters.put("planStatus1", String.valueOf(PlanStatus.WRITEOFF));
		parameters.put("businessType", String.valueOf(BusinessType.PSYCHOSIS));
		parameters.put("currentDate", new Date());

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(fhql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访信息失败！", e);
		}
		return rsList;
	}

	// ************************** 随访用药 ********************************
	/**
	 * 获取随访用药记录
	 * 
	 * @param visitId
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsyVisitMedicineList(String visitId,
			String orderBy) throws ModelDataOperationException {
		String cndString = "['eq', ['$', 'visitId'], ['s', '" + visitId + "']]";
		List<?> cnd = null;
		try {
			cnd = CNDHelper.toListCnd(cndString);
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "表达转换错误！", e);
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, orderBy, PSY_PsychosisVisitMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访用药记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 依据随访ID（visitId）删除随访用药记录
	 * 
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteVisitMedicine(String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append("PSY_PsychosisVisitMedicine")
				.append(" where visitId=:visitId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitId", visitId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除随访用药失败！", e);
		}
	}

	/**
	 * 保存随访用药记录
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePsyVisitMedicine(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao
					.doSave(op, PSY_PsychosisVisitMedicine, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访用药数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访用药信息失败", e);
		}
		return rsMap;
	}

	/**
	 * 保存批量用药记录
	 * 
	 * @param medicineList
	 * @param phrId
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void saveBatchPsyVisitMedicine(
			List<Map<String, Object>> medicineList, String phrId, String visitId)
			throws ModelDataOperationException {
		if (null == medicineList || medicineList.size() == 0) {
			return;
		}
		HashMap<String, Object> vmBody = new HashMap<String, Object>();
		for (int i = 0; i < medicineList.size(); i++) {
			vmBody = (HashMap<String, Object>) medicineList.get(i);
			if (vmBody.size() == 0) {
				continue;
			}
			String medicineName = (String) vmBody.get("medicineName");
			String recordId = (String) vmBody.get("recordId");
			if(S.isEmpty(medicineName)){
				if (S.isEmpty(recordId)) {
					continue;
				} else{
					try {
						dao.doRemove(recordId, PSY_PsychosisVisitMedicine);
					} catch (PersistentDataOperationException e) {
						throw new ModelDataOperationException(
								Constants.CODE_DATABASE_ERROR, "保存随访用药时删除药品失败！", e);
					}
				}
			}
			vmBody.put("phrId", phrId);
			vmBody.put("visitId", visitId);
			if(vmBody.get("days") == null){
				vmBody.put("days", 1);
			}else if(vmBody.get("days") instanceof String){
				String dayString = (String)vmBody.get("days");
				if(S.isEmpty(dayString)){
					vmBody.put("days", 1);
				}else{
					vmBody.put("days", Integer.parseInt(dayString));
				}
			}
			String vmOp = "create";
			Date curDate = Calendar.getInstance().getTime();
			String curUid = UserUtil.get(UserUtil.USER_ID);
			String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
			if (StringUtils.isEmpty((String) vmBody.get("recordId"))) {
				vmBody.put("createUser", curUid);
				vmBody.put("createUnit", curUnitId);
				vmBody.put("createDate", curDate);
				vmBody.put("lastModifyUser", curUid);
				vmBody.put("lastModifyUnit", curUnitId);
				vmBody.put("lastModifyDate", curDate);
			} else {
				vmOp = "update";
				vmBody.put("lastModifyUser", curUid);
				vmBody.put("lastModifyUnit", curUnitId);
				vmBody.put("lastModifyDate", curDate);
			}
			this.savePsyVisitMedicine(vmOp, vmBody, true);
			vmBody.clear();
		}
	}

	/**
	 * 获取精神病随访信息
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getVisitInfoByPhrId(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.visitType,a.visitDate,a.type,a.isReferral,b.recordId,b.empiId,b.sn from ")
				.append(" PSY_PsychosisVisit a, PUB_VisitPlan b")
				.append(" where a.visitId = b.visitId ")
				.append(" and b.businessType = :businessType ")
				.append(" and recordId = :recordId ")
				.append(" order by a.visitDate desc ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", phrId);
		parameters.put("businessType", BusinessType.PSYCHOSIS);
		try {
			return dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取精神病随访信息失败！", e);
		}
	}

	/**
	 * 获取上次随访类型
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getLastVisitType(String phrId)
			throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer(
				"select visitType as visitType from ")
				.append("PSY_PsychosisVisit")
				.append(" where phrId = :phrId and visitType != :visitType order by visitDate desc");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitType", PsyVisitType.NOT_VISIT);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取上次随访类型失败！", e);
		}
		if (rsList != null) {
			return (String) rsList.get(0).get("visitType");
		} else {
			return null;
		}
	}

	public Long isHasVisitRecords(String empiId, Date visitDate)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recordNum from ")
				.append("PSY_PsychosisVisit")
				.append(" where empiId = :empiId and visitDate > :visitDate")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("visitDate", visitDate);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否已有随访记录失败！", e);
		}
		long recordNum = 0;
		if (rsMap != null) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum;
	}

	/**
	 * 获取随访分类
	 * 
	 * @param dangerousGrade
	 *            - 危险性
	 * @param insight
	 *            - 自知力
	 * @param adverseReactions
	 *            - 药物不良反应
	 * @param social
	 *            - 社交能力
	 * @param visitEffect
	 *            - 转归
	 * @param visitEffect
	 *            - 症状
	 * @return
	 */
	public static String getVisitType(String dangerousGrade, String insight,
			String adverseReactions, String social, String visitEffect) {
		// 转归 “失访” --未访到--取前一次随访时间间隔生成随访计划
		if (VisitEffect.LOST.equals(visitEffect)) {
			return "0";
		}
		// 危险性为3～5级 或 自知力缺乏 或 有药物不良反应-->不稳定
		if ("3".equals(dangerousGrade) || "4".equals(dangerousGrade)
				|| "5".equals(dangerousGrade) || "3".equals(insight)
				|| YesNo.YES.equals(adverseReactions)) {
			// 不稳定
			return "1";
		}
		// 若危险性为1～2级 或 自知力不全 或 社交能力较差 -->基本稳定
		if ("1".equals(dangerousGrade) || "2".equals(dangerousGrade)
				|| "2".equals(insight) || "3".equals(social)) {
			// 基本稳定
			return "2";
		}
		// 危险性为0级，且 自知力完全，社会能力处于一般 或 良好，无药物不良反应 -->稳定
		if ("0".equals(dangerousGrade) && "1".equals(insight)
				&& ("1".equals(social) || "2".equals(social))
				&& YesNo.NO.equals(adverseReactions)) {
			// 稳定
			return "3";
		}
		return "3"; // 失访
	}

	/**
	 * 
	 * @Description: 获取重症精神病教育指导信息
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-27 下午6:38:49
	 * @Modify:
	 */
	public Map<String, Object> getHealthGuidance(String wayId)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
					wayId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay",
					"s", "05");
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_JSBSF);
			if (list != null && list.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_JSBSF));
			}
			result.put("JKCFRecords", list);

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取重症精神病教育指导信息失败!", e);
		}
		return result;
	}

	/**
	 * 
	 * @Description: 保存重症精神病教育指导信息失败
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-27 下午6:43:06
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveHealthGuidance(String op,
			Map<String, Object> record, VindicateLogService vLogService)
			throws ModelDataOperationException {
		Map<String, Object> JKCF = (Map<String, Object>) record.get("JKCF");
		String wayId = record.get("wayId") + "";
		record.remove(JKCF);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> reBody = new HashMap<String, Object>();
		try {
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
					wayId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay",
					"s", "05");
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_JSBSF);
			for (Iterator<String> it = JKCF.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
				boolean isUpdate = false;
				for (Map<String, Object> map : list) {
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						m.put("id", map.get("id"));
						isUpdate = true;
						break;
					}
				}
				Map<String, Object> mBody = new HashMap<String, Object>();
				mBody.put("empiId", record.get("empiId"));
				mBody.put("phrId", record.get("phrId"));
				mBody.put("recordId", m.get("recordId"));
				mBody.put("wayId", wayId);
				mBody.put("recipeName", m.get("recipeName"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", record.get("examineUnit"));
				mBody.put("guideDate", record.get("guideDate"));
				mBody.put("guideUser", record.get("guideUser"));
				mBody.put("guideWay", "05");
				mBody.put("lastModifyUnit", manageUnit);
				mBody.put("lastModifyDate", new Date());
				mBody.put("lastModifyUser", user.getUserId() + "");
				if (isUpdate == true) {
					mBody.put("id", m.get("id"));
					updateList.add(mBody);
				} else {
					mBody.put("inputUnit", manageUnit);
					mBody.put("inputDate", new Date());
					mBody.put("inputUser", user.getUserId() + "");
					createList.add(mBody);
				}
			}
			for (Map<String, Object> map : list) {
				boolean isRemove = true;
				for (Iterator<String> it = JKCF.keySet().iterator(); it
						.hasNext();) {
					String key = (String) it.next();
					Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						isRemove = false;
						break;
					}
				}
				if (isRemove == true) {
					removeList.add(map);
				}
			}
			list.clear();
			for (Map<String, Object> map : createList) {
				Map<String, Object> result = dao.doSave("create",
						HER_HealthRecipeRecord_JSBSF, map, true);
				map.put("childId", result.get("id"));
				Map<String, Object> result2 = dao.doSave("create",
						HER_HealthRecipeRecord, map, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_JSBSF,
						"create", result.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "create",
						result2.get("id") + "", dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_JSBSF, map, true);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='05'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				result2.putAll(map);
				result2.put("id", zjId);
				dao.doSave("update", HER_HealthRecipeRecord, result2, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_JSBSF,
						"update", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "update",
						zjId, dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_JSBSF);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='05'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				dao.doRemove(zjId, HER_HealthRecipeRecord);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_JSBSF,
						"delete", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "delete",
						zjId, dao);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_JSBSF));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存重症精神病教育指导信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存重症精神病教育指导信息失败！", e);
		} catch (ServiceException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存重症精神病教育指导日志信息失败！", e);
		}
		return reBody;
	}
	
	/**
	 * 
	 * @Description:分页查询精神病随访计划
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-6-4 上午10:43:28
	 * @Modify:
	 */
	public Map<String, Object> listPsychosisVistPlan(Map<String, Object> req)throws ModelDataOperationException{
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
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController
					.instance()
					.get("chis.application.psy.schemas.PSY_PsychosisVisitPlanListView");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" PUB_VisitPlan a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" join PSY_PsychosisRecord d on a.empiId=d.empiId ")
				.append(" left join PSY_PsychosisVisit e on a.empiId=e.empiId and a.visitid=e.visitid");
		StringBuffer countSQL = new StringBuffer(
				"select count(a.planId) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where);
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询精神病随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select ").append(sfBuffer.substring(1)).append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where);
			}
			sql.append(" order by a.planDate ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询精神病随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tpList,
						"chis.application.psy.schemas.PSY_PsychosisVisitPlanListView"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}
}
