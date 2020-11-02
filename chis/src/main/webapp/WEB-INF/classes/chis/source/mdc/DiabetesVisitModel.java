/**
 * @(#)DiabetesModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.log.VindicateLogService;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesVisitModel extends DiabetesModel {

	public DiabetesVisitModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 保存糖尿病随访信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesVisit(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesVisit, record, false);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病随访数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病随访信息失败！");
		}
		return m;
	}

	/**
	 * 根据随访ID删除随访用药记录
	 * 
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteDiabetesMedicine(String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MDC_DiabetesMedicine)
				.append(" where visitId =:visitId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitId", visitId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除糖尿病服药情况记录失败！");
		}
	}

	/**
	 * 获取本年度最后一条随访记录 的 ID<br/>
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastVistRecordVisitId(String empiId,
			Date startDate) throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append(MDC_DiabetesVisit)
				.append(" where empiId =:empiId and visitDate < :visitDate")
				.append(" order by visitId desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("visitDate", startDate);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		Map<String, Object> rsMap = null;
		if (rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

	/**
	 * 获取本年度最后一条已经随访记录 的 ID<br/>
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastVisitedPlan(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append(PUB_VisitPlan)
				.append(" where empiId =:empiId and planStatus=:planStatus")
				.append(" order by planId desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planStatus", PlanStatus.VISITED);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条已经随访计划失败！", e);
		}
		Map<String, Object> rsMap = null;
		if (rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

	/**
	 * 获取本年度最后一条未访随访记录 的 ID<br/>
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getUnVisitedPlan(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(PUB_VisitPlan)
				.append(" where empiId =:empiId and planStatus=:planStatus and businessType = :businessType ")
				.append(" order by planId desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		parameters.put("businessType", BusinessType.TNB);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条未访随访计划失败！", e);
		}
		Map<String, Object> rsMap = null;
		if (rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

	/**
	 * 保存糖尿病并发症信息
	 * 
	 * @param exp
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void saveComplicationList(String exp, String visitId)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.toListCnd(exp);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doQuery(cnd, null, MDC_DiabetesComplication);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "获取糖尿病并发症信息失败！");
			}
			if (rsList.size() > 0) {
				for (int i = 0; i < rsList.size(); i++) {
					HashMap<String, Object> medBody = (HashMap<String, Object>) rsList
							.get(i);
					medBody.put("visitId", visitId);
					try {
						dao.doSave("create", MDC_DiabetesComplication, medBody,
								true);
					} catch (ValidateException e) {
						throw new ModelDataOperationException(
								Constants.CODE_DATABASE_ERROR,
								"保存糖尿病并发症信息时数据验证失败！");
					} catch (PersistentDataOperationException e) {
						throw new ModelDataOperationException(
								Constants.CODE_DATABASE_ERROR, "保存糖尿病并发症信息失败！");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询表达式cnd格式转换失败！");
		}
	}

	/**
	 * 保存糖尿病服药情况
	 * 
	 * @param exp
	 * @param res
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicineList(String exp, Map<String, Object> res)
			throws ModelDataOperationException {

		List<?> cnd;
		try {
			cnd = CNDHelper.toListCnd(exp);
		} catch (Exception e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询表达式cnd格式转换失败！");
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, null, MDC_DiabetesMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病服药情况信息失败！");
		}
		if (rsList.size() > 0) {
			for (int i = 0; i < rsList.size(); i++) {
				HashMap<String, Object> medBody = (HashMap<String, Object>) rsList
						.get(i);
				medBody.put("visitId", ((HashMap<String, Object>) res
						.get("body")).get("visitId"));
				try {
					dao.doSave("create", MDC_DiabetesMedicine, medBody, true);
				} catch (ValidateException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "保存糖尿病服药情况时数据验证失败！");
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "保存糖尿病服药情况失败！");
				}
			}
			((HashMap<String, Object>) res.get("body")).put(
					"needInputMedicine", false);
		} else {
			((HashMap<String, Object>) res.get("body")).put(
					"needInputMedicine", true);
		}

	}

	/**
	 * 判断是否有糖尿病服药情况
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getCountDiabetesMedicine(String phrId, String visitId)
			throws ModelDataOperationException {
		StringBuffer sb = new StringBuffer("select count(*) as rsCount from ")
				.append(MDC_DiabetesMedicine).append(
						" where phrId=:phrId and visitId=:visitId");
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("phrId", phrId);
		pam.put("visitId", visitId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(sb.toString(), pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否有糖尿病服药情况失败!");
		}
		return rsMap == null ? 0 : Integer.valueOf(String.valueOf(rsMap
				.get("rsCount")));

	}

	/**
	 * 更新糖尿病记录体重信息
	 * 
	 * @param weight
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateDiabetesRecordWeight(double weight, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_DiabetesRecord)
				.append(" set weight = :weight")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("weight", weight);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新糖尿病档案身高体重失败！");
		}
	}

	/**
	 * 更新随访计划信息
	 * 
	 * @param planId
	 * @param updateFields
	 * @throws ModelDataOperationException
	 */
	public void updateVisitPlanPartInfo(String planId, String visitEffect,
			Map<String, Object> updateFields)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append(PUB_VisitPlan)
				.append(" set planStatus = :planStatus ")
				.append(",visitDate =:visitDate ,visitId =:visitId ")
				.append(",lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate,extend1=:extend1 ")
				.append(" where planId = :planId").toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		if (visitEffect.equals(VisitEffect.LOST)) {
			paraMap.put("planStatus", PlanStatus.LOST);
		} else if (visitEffect.equals(VisitEffect.END)) {
			paraMap.put("planStatus", PlanStatus.WRITEOFF);
		} else {
			paraMap.put("planStatus", PlanStatus.VISITED);
		}
		paraMap.put("visitDate", updateFields.get("visitDate"));
		paraMap.put("visitId", (String) updateFields.get("visitId"));
		paraMap.put("lastModifyUser",
				(String) updateFields.get("lastModifyUser"));
		paraMap.put("lastModifyDate", new Date());
		paraMap.put("planId", planId);
		paraMap.put("extend1", updateFields.get("visitResult"));
		try {
			dao.doUpdate(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新随访计划信息失败！");
		}
	}

	/**
	 * 更新过期随访计划
	 * 
	 * @param empiId
	 * @param userId
	 * @param endDate
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 */
	public void updateUntreatedVisitPlan(String empiId, String userId,
			String planDate) throws ModelDataOperationException, ParseException {
		String hql = new StringBuffer("update ")
				.append(PUB_VisitPlan)
				.append(" set planStatus = :planStatus")
				.append(",lastModifyUser = :lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where empiId = :empiId and businessType = :businessType")
				.append(" and planStatus = :planStatus0 and planDate <= :planDate")
				.toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("planStatus", PlanStatus.NOT_VISIT);
		paraMap.put("lastModifyUser", userId);
		paraMap.put("lastModifyDate", new Date());
		paraMap.put("empiId", empiId);
		paraMap.put("businessType", BusinessType.TNB);
		paraMap.put("planStatus0", PlanStatus.NEED_VISIT);
		paraMap.put("planDate", BSCHISUtil.toDate(planDate));

		try {
			dao.doUpdate(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！", e);
		}
	}

	/**
	 * 是否需要改变随访
	 * 
	 * @param lastVisitData
	 * @param body
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public int whetherNeedChangeVisit(Map<String, Object> lastVisitData,
			Map<String, Object> body, Map<String, Object> res) {
		double lfbs = 0;
		String lar = "", lcc = "";
		if (null != lastVisitData) {
			if (lastVisitData.get("fbs") != null) {
				double bd = (Double) lastVisitData.get("fbs");
				if (bd > 0)
					lfbs = bd;
				lar = (String) lastVisitData.get("adverseReactions"); // 药物不良反应
				lcc = (String) lastVisitData.get("complicationChange");
			}

		}
		String ar = (String) body.get("adverseReactions");
		String cc = (String) body.get("complicationChange");
		double fbs;
		if (body.get("fbs") == null || body.get("fbs").equals("")) {
			fbs = 0;
		} else {
			fbs = Double.valueOf(String.valueOf(body.get("fbs")));
		}
		Map<String, Object> resbody = (Map<String, Object>) res.get("body");
		int needReferral = 0;
		// 连续两次空腹血糖超标
		if (fbs >= 7.0) {
			needReferral = 1;
			if (lfbs >= 7.0) {
				resbody.put("needReferral", 1);
			}
		}
		// 连续两次药物不良反应
		if (ar != null && ar != "" && ar.equals("1")) {
			needReferral = 1;
			if (lar != null && lar.equals("2")) {
				resbody.put("needReferral", 1);
			}
		}
		// 原有并发症加重或有新并发症发行
		if (cc != null && cc.length() > 0) {
			needReferral = 1;
			resbody.put("needReferral", 1);
		}
		// 由不满意转满意
		if ((lfbs >= 7.0 || (lar != null && lar.equals("2")) || (lcc != null && lcc
				.trim().length() > 0))
				&& (fbs < 7.0 && !ar.equals("2") && (cc == null || cc.trim()
						.length() == 0))) {
			needReferral = 2;
		}
		return needReferral;
	}

	/**
	 * 生成糖尿病随访计划
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String createVisitPlan(Map<String, Object> body,
			VisitPlanCreator visitPlanCreator, Context ctx)
			throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String planMode = (String) app.getProperty(BusinessType.TNB
				+ "_planMode");
		if (planMode == null || planMode.equals("")) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "未配置随访计划生成方式，请到系统配置界面中配置。");
		}
		// 生成随访计划参数
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("recordId", body.get("phrId"));
		reqBody.put("fixType", body.get("fixType"));
		reqBody.put("businessType", BusinessType.TNB);
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("groupCode", body.get("groupCode"));
		reqBody.put("fixGroupDate", body.get("fixGroupDate"));
		reqBody.put("adverseReactions", body.get("adverseReactions"));
		reqBody.put("fbs", body.get("fbs"));
		reqBody.put("nextDate", body.get("nextDate"));
		reqBody.put("sn", body.get("sn"));
		reqBody.put("lastVisitDate", body.get("lastVisitDate"));
		reqBody.put("lastPlanDate", body.get("lastPlanDate"));
		reqBody.put("complicationChange", body.get("complicationChange"));
		reqBody.put("visitResult", body.get("visitResult"));
		reqBody.put("lastestData", body.get("lastestData"));
		reqBody.put("visitDate", body.get("visitDate"));
		reqBody.put("noChange", body.get("noChange"));
		reqBody.put("taskDoctorId", body.get("taskDoctorId"));
		if(body.containsKey("visitMeddle")){
			reqBody.put("visitMeddle",body.get("visitMeddle"));
			}
		if (PlanMode.BY_RESERVED.equals(planMode)) {
			reqBody.put("reserveDate", body.get("nextDate"));
		}
		boolean nextYear = false;
		if (body.get("nextYear") != null) {
			nextYear = (Boolean) body.get("nextYear");
		}
		reqBody.put("$sectionCutOffDate",
				BSCHISUtil.getSectionCutOffDate("diabetesEndMonth", nextYear));
		reqBody.put("$planMode", planMode);
		// 生成随访
		PlanType planType = null;
		try {
			planType = visitPlanCreator.create(reqBody, ctx);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成糖尿病随访计划失败！", e);
		}
		// 生成计划以后取回planTypeCode
		if (null != planType) {
			String planTypeCode = planType.getPlanTypeCode();
			if (planTypeCode == null) {
				planTypeCode = (String) app.getProperty(BusinessType.TNB
						+ "_planTypeCode");
			}
			this.updateDiabetesPlanTypeCode(planTypeCode,
					(String) body.get("phrId"));
		}
		return null;
	}

	/**
	 * 更新糖尿病计划类型
	 * 
	 * @param planTypeCode
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateDiabetesPlanTypeCode(String planTypeCode, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_DiabetesRecord)
				.append(" set planTypeCode=:planTypeCode where phrId=:phrId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("planTypeCode", planTypeCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新档案里的计划类型失败！", e);
		}
	}

	/**
	 * 保存糖尿病服务情况
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesMedicine(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesMedicine, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病服务数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病服务信息失败！");
		}
		return m;
	}

	public List<Map<String, Object>> getVisitMedicine(String phrId,
			String visitId) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd2, "recordId",
					MDC_DiabetesMedicine);
			SchemaUtil.setDictionaryMessageForList(list, MDC_DiabetesMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病随访药品失败。");
		}
		return list;
	}

	public List<Map<String, Object>> getVisitComplication(String phrId,
			String visitId) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd,
					"complicationId", MDC_DiabetesComplication);
			SchemaUtil.setDictionaryMessageForList(list,
					MDC_DiabetesComplication);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病并发症失败。");
		}
		return list;
	}

	public void removeMedicine(String recordId)
			throws PersistentDataOperationException {
		dao.doRemove(recordId, MDC_DiabetesMedicine);
	}

	public List<Map<String, Object>> getDiabetesDescription(String visitId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "recordId", MDC_DiabetesVisitDescription);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病中医变体失败。");
		}
		return list;

	}

	/**
	 * 保存糖尿病并发症信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesDescription(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesVisitDescription, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病中医辩体数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病中医辩体信息失败！");
		}
		return m;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getDiabetesHealthTeach(String wayId)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "03");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_TNBSF);
			if (list != null && list.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_TNBSF));
			}
			result.put("JKCFRecords", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病中医辩体失败。");
		}
		return result;

	}

	/**
	 * 保存糖尿病并发症信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> saveDiabetesHealthTeach(String op,
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
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "03");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_TNBSF);
			for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
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
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", record.get("examineUnit"));
				mBody.put("guideDate", record.get("guideDate"));
				mBody.put("guideUser", record.get("guideUser"));
				mBody.put("guideWay", "03");
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
				for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
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
						HER_HealthRecipeRecord_TNBSF, map, true);
				map.put("childId", result.get("id"));
				Map<String, Object> result2 = dao.doSave("create",
						HER_HealthRecipeRecord, map, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_TNBSF,
						"create", result.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "create",
						result2.get("id") + "", dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_TNBSF, map, true);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='03'")
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
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_TNBSF,
						"update", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "update",
						zjId, dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_TNBSF);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='03'")
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
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_TNBSF,
						"delete", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "delete",
						zjId, dao);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_TNBSF));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病健康教育验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病健康教育信息失败！");
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病健康教育信息日志失败！");
		}
		return reBody;
	}
	/**
	 * 保存APP糖尿病随访中健康教育
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void saveAppDiabetesVisitHealthTeach(Map<String, Object> m)
			throws ModelDataOperationException {
		try {
			dao.doInsert(HER_HealthRecipeRecord_TNBSF, m, false);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康教育数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康教育失败！");
		}
	}

	public Map<String, Object> getDiabetesVisitByPkey(String visitId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_DiabetesVisit, visitId);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病信息失败！");
		}
		return rsMap;
	}

	public Map<String, Object> getPerRecord(String empiId, Object date)
			throws PersistentDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("lt",
				"str(visitDate,'yyyy-MM-dd')", "s", date);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

		List<Map<String, Object>> l = dao.doQuery(cnd, "visitId desc",
				MDC_DiabetesVisit);
		if (l.size() > 0) {
			return (Map<String, Object>) l.get(0);
		}
		return null;
	}

	/**
	 * 删除糖尿病并发症数据
	 * 
	 * @param phrId
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteDiabetesComplication(String phrId, String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MDC_DiabetesComplication)
				.append(" where phrId = :phrId and visitId = :visitId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitId", visitId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除糖尿病并发症数据失败！");
		}
	}

	/**
	 * 保存糖尿病并发症信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesComplication(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesComplication, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病并发症数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病并发症信息失败！");
		}
		return m;
	}

	/**
	 * 查询被终止管理的糖尿病随访数据 *
	 * 
	 * @param phrId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> searchOverDiabetesVisit(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> visitRecord = new HashMap<String, Object>();
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "visitEffect", "s",
					VisitEffect.END);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			List<?> l = dao.doQuery(cnd, null, MDC_DiabetesVisit);
			if (null != l && l.size() > 0) {
				visitRecord = (Map<String, Object>) l.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询被终止管理的糖尿病随访数据失败！", e);
		}
		return visitRecord;
	}

	/**
	 * 更新终止管理的糖尿病随访数据
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void updateOverDiabetesVisit(String phrId, String visitId)
			throws ModelDataOperationException {
		try {
			String hql = new StringBuffer("update ")
					.append(MDC_DiabetesVisit)
					.append(" set visitEffect=:visitEffect where  phrId = :phrId ")
					.toString();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visitEffect", VisitEffect.CONTINUE);
			parameters.put("phrId", phrId);

			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("删除被终止管理的糖尿病随访数据失败！", e);
		}
	}

	/**
	 * 删除被终止管理的糖尿病随访数据
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void deleteOverDiabetesVisit(String phrId)
			throws ModelDataOperationException {
		try {
			String hql = new StringBuffer("delete ").append(MDC_DiabetesVisit)
					.append(" where visitEffect = :visitEffect")
					.append(" and phrId = :phrId ").toString();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visitEffect", VisitEffect.END);
			parameters.put("phrId", phrId);

			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("删除被终止管理的糖尿病随访数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:纸质化随访页面加载药品信息
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-2-9 下午2:16:25
	 * @Modify:
	 */
	public List<Map<String, Object>> getHtmlVisitMedicine(String visitId)
			throws ModelDataOperationException {
		List<Map<String, Object>> vmList = new ArrayList<Map<String, Object>>();
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "recordId asc", MDC_DiabetesMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "糖尿病随访页面取药品信息失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			for (int i = 0, len = rsList.size(); i < len; i++) {
				Map<String, Object> rMap = rsList.get(i);
				if (i < 4) {
					vmList.add(rMap);
				}
			}
		}
		// 取 胰岛素 记录
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq",
				"otherMedicineDesc", "s", "胰岛素");
		List<Object> cnd2 = CNDHelper.createArrayCnd("and", cnd, cnd1);
		List<Map<String, Object>> ydsList = null;
		try {
			ydsList = dao.doList(cnd2, "recordId asc", MDC_DiabetesMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "糖尿病随访页面取药品信息失败！", e);
		}
		if (ydsList != null && ydsList.size() > 0) {
			vmList.add(ydsList.get(0));
		}
		return vmList;
	}

	public double paresToDouble(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Double.parseDouble(value);
	}

	public Map<String, Object> getGroupConfig(String fbsNow, String pbsNow,
			String diabetesGroup, String empiId, String visitDate,
			String visitId, String diabetesType)
			throws ModelDataOperationException {
		Application app = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病组别失败。");
		}
		String startMonth = (String) app.getProperty("diabetesStartMonth");
		String endMonth = (String) app.getProperty("diabetesEndMonth");
		String manageType = (String) app.getProperty("2_manageType");
		String assessType = (String) app.getProperty("assessType");
		String badScore1 = (String) app.getProperty("badScore1");
		String badScore2 = (String) app.getProperty("badScore2");
		String scaleNum1 = (String) app.getProperty("scaleNum1");
		String scaleNum2 = (String) app.getProperty("scaleNum2");
		String threeTurn = (String) app.getProperty("threeTurn");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get("chis.dictionary.groups");
		} catch (ControllerException e2) {
			e2.printStackTrace();
		}
//		boolean controlBad = false;// 是否控制不良
//		boolean needReferral = false;// 是否转诊
//		boolean needInsertPlan = false;// 是否需要插入计划
//		if (pbsNow != null && !"".equals(pbsNow)) {
//			if (paresToDouble(pbsNow) > paresToDouble(badScore2)) {
//				controlBad = true;
//			}
//		} else {
//			if (paresToDouble(fbsNow) > paresToDouble(badScore1)) {
//				controlBad = true;
//			}
//		}
		Map<String, Object> lastVisitData = null;
		try {
			lastVisitData = getLastVistRecordVisitId(empiId,
					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
//		result.put("controlBad", controlBad);
//		result.put("oldGroup", diabetesGroup);
//		if (lastVisitData != null && lastVisitData.get("pbs") != null
//				&& !"".equals(lastVisitData.get("pbs"))) {
//			if (paresToDouble(lastVisitData.get("pbs") + "") > paresToDouble(badScore2)) {
//				needReferral = true;
//			}
//		} else if (lastVisitData != null && lastVisitData.get("fbs") != null
//				&& !"".equals(lastVisitData.get("fbs"))) {
//			if (paresToDouble(lastVisitData.get("fbs") + "") > paresToDouble(badScore1)) {
//				needReferral = true;
//			}
//		}
//		result.put("needReferral", needReferral);
		// 本次随访控制不良，判断两周内是否有计划，有者不插入计划
		// 无则插入计划
//		if (controlBad == true && needReferral == false) {
//			boolean hasPlan = checkHasPlanInTwoWeek(empiId, visitDate);
//			if (hasPlan == false) {
//				needInsertPlan = true;
//			}
//		}
//		result.put("needInsertPlan", needInsertPlan);
		// 年度评估不需要转组
//		if ("2".equals(assessType)&& needInsertPlan==false) {
		if ("2".equals(assessType)) {
			result.put("needChangeGroup", false);
			result.put("needAssess", false);
			result.put("diabetesGroup", diabetesGroup);
			result.put("diabetesGroupName", dic.getText(diabetesGroup));

			// result.put("needChangeGroup", true);
			// result.put("needAssess", true);
			// result.put("diabetesGroup", "02");
			// result.put("diabetesGroupName", dic.getText("02"));
			return result;
		}
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		int controlBadCount = 0;// 该病人控制不良数
		boolean needTurn = false;
		String startDate = null;
		String endDate = null;
		try {
			startDate = drm.getStartDateForYear(startMonth);
			endDate = drm.getEndDateForYear(endMonth);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String sql4 = "select fbs as fbs,pbs as pbs,visitDate as visitDate from MDC_DiabetesVisit "
				+ "where empiId=:empiId and visitDate>=to_date('"
				+ startDate
				+ "','yyyy-mm-dd HH24:mi:ss') and visitDate<=to_date('"
				+ endDate + "','yyyy-mm-dd HH24:mi:ss') ";
		if (visitId != null && !"".equals(visitId)) {
			sql4 = sql4 + " and visitId<>:visitId ";
		}
		sql4 = sql4 + "order by visitDate desc";
		String sql5 = "select superDiagnose as superDiagnose from MDC_DiabetesOGTTRecord "
				+ "where empiId=:empiId and registerDate>=to_date('"
				+ startDate
				+ "','yyyy-mm-dd HH24:mi:ss') and registerDate<=to_date('"
				+ endDate + "','yyyy-mm-dd HH24:mi:ss')";
		String newGroup = diabetesGroup;
		if ("1".equals(manageType)) {
			newGroup = "01";
			needTurn = false;
		} else {
			List<Map<String, Object>> ogtt = null;
			Map<String, Object> groupPara = new HashMap<String, Object>();
			groupPara.put("empiId", empiId);
			if (visitId != null && !"".equals(visitId)) {
				groupPara.put("visitId", visitId);
			}
			List<Map<String, Object>> list = null;
			// 查询本年度已做的随访
			try {
				list = dao.doSqlQuery(sql4, groupPara);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("FBS", fbsNow);
				map.put("PBS", pbsNow);
				list.add(map);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			groupPara.remove(visitId);
			int visitCount = list.size();
			for (Map<String, Object> r : list) {
				double fbs = paresToDouble(r.get("FBS") + "");
				double pbs = paresToDouble(r.get("PBS") + "");
				if (r.get("PBS") != null && !"".equals(r.get("PBS"))) {
					if (pbs > paresToDouble(badScore2)) {
						controlBadCount++;
					}
				} else {
					if (fbs > paresToDouble(badScore1)) {
						controlBadCount++;
					}
				}
			}
			// 根据原组别和控制情况和一二三组转组标准判断该病人是否需要转组以及新组别
			if ((controlBadCount / visitCount) * 100 > paresToDouble(scaleNum2)
					&& "02".equals(diabetesGroup)) {
				needTurn = true;
				newGroup = "01";
			} else if (((visitCount - controlBadCount) / visitCount) * 100 > paresToDouble(scaleNum1)
					&& "01".equals(diabetesGroup)) {
				needTurn = true;
				newGroup = "02";
			} else if (threeTurn.indexOf("2") > -1
					&& "03".equals(diabetesGroup) && visitCount > 0
					&& list.get(visitCount - 1) != null) {
				newGroup = drm
						.getDiabetesGroupByConfig(
								diabetesType,
								paresToDouble(list.get(visitCount - 1).get(
										"FBS")
										+ ""),
								paresToDouble(list.get(visitCount - 1).get(
										"PBS")
										+ ""));
				if (!newGroup.equals(diabetesGroup)) {
					needTurn = true;
				}
			}
			if (threeTurn.indexOf("1") > -1 && "03".equals(diabetesGroup)) {
				try {
					ogtt = dao.doSqlQuery(sql5, groupPara);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				if (ogtt != null && ogtt.size() > 0) {
					for (Map<String, Object> m : ogtt) {
						String superDiagnose = m.get("SUPERDIAGNOSE") + "";
						if ("y".equals(superDiagnose)) {
							needTurn = true;
							newGroup = "02";
							break;
						}
					}
				}
			}
		}
		result.put("needChangeGroup", needTurn);
		result.put("needAssess", true);
		result.put("diabetesGroup", newGroup);
		result.put("diabetesGroupName", dic.getText(newGroup));
		return result;
	}

	public Map<String, Object> getGroupConfigPK(String fbsNow, String pbsNow,
			String diabetesGroup, String empiId, String visitDate,
			String visitId, String diabetesType, String planDate)
			throws ModelDataOperationException {
		Application app = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病组别失败。");
		}
		String startMonth = (String) app.getProperty("diabetesStartMonth");
		String endMonth = (String) app.getProperty("diabetesEndMonth");
		String manageType = (String) app.getProperty("2_manageType");
		String assessType = (String) app.getProperty("assessType");
		String badScore1 = (String) app.getProperty("badScore1");
		String badScore2 = (String) app.getProperty("badScore2");
		String scaleNum1 = (String) app.getProperty("scaleNum1");
		String scaleNum2 = (String) app.getProperty("scaleNum2");
		String threeTurn = (String) app.getProperty("threeTurn");
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get("chis.dictionary.groups");
		} catch (ControllerException e2) {
			e2.printStackTrace();
		}
		boolean controlBad = false;// 是否控制不良
		boolean needReferral = false;// 是否转诊
		boolean needInsertPlan = false;// 是否需要插入计划
		if (pbsNow != null && !"".equals(pbsNow)) {
			if (paresToDouble(pbsNow) > paresToDouble(badScore2)) {
				controlBad = true;
			}
		} 
		if(fbsNow != null && !"".equals(fbsNow)) {
			if (paresToDouble(fbsNow) > paresToDouble(badScore1)) {
				controlBad = true;
			}
		}
		Map<String, Object> lastVisitData = null;
		try {
			lastVisitData = getLastVistRecordVisitId(empiId,
					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		result.put("controlBad", controlBad);
		result.put("oldGroup", diabetesGroup);
		if (lastVisitData != null && lastVisitData.get("pbs") != null
				&& !"".equals(lastVisitData.get("pbs"))) {
			if (paresToDouble(lastVisitData.get("pbs") + "") > paresToDouble(badScore2)) {
				if(controlBad){
				needReferral = true;
				}
			}
		} else if (lastVisitData != null && lastVisitData.get("fbs") != null
				&& !"".equals(lastVisitData.get("fbs"))) {
			if (paresToDouble(lastVisitData.get("fbs") + "") > paresToDouble(badScore1)) {
				if(controlBad){
					needReferral = true;
				}
			}
		}
		result.put("needReferral", needReferral);
		// 本次随访控制不良，判断两周内是否有计划，有者不插入计划
		// 无则插入计划
		if (controlBad == true && needReferral == false) {
			boolean hasPlan = checkHasPlanInTwoWeek(empiId, planDate);
			if (hasPlan == false) {
				needInsertPlan = true;
			}
		}
		result.put("needInsertPlan", needInsertPlan);
		// 年度评估不需要转组
		if ("2".equals(assessType)&& needInsertPlan==false) {
			result.put("needChangeGroup", false);
			result.put("needAssess", false);
			result.put("diabetesGroup", diabetesGroup);
			result.put("diabetesGroupName", dic.getText(diabetesGroup));

			// result.put("needChangeGroup", true);
			// result.put("needAssess", true);
			// result.put("diabetesGroup", "02");
			// result.put("diabetesGroupName", dic.getText("02"));
			return result;
		}
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		int controlBadCount = 0;// 该病人控制不良数
		boolean needTurn = false;
		String startDate = null;
		String endDate = null;
		try {
			startDate = drm.getStartDateForYear(startMonth);
			endDate = drm.getEndDateForYear(endMonth);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String sql4 = "select fbs as fbs,pbs as pbs,visitDate as visitDate from MDC_DiabetesVisit "
				+ "where empiId=:empiId and visitDate>=to_date('"
				+ startDate
				+ "','yyyy-mm-dd HH24:mi:ss') and visitDate<=to_date('"
				+ endDate + "','yyyy-mm-dd HH24:mi:ss') ";
		if (visitId != null && !"".equals(visitId)) {
			sql4 = sql4 + " and visitId<>:visitId ";
		}
		sql4 = sql4 + "order by visitDate desc";
		String sql5 = "select superDiagnose as superDiagnose from MDC_DiabetesOGTTRecord "
				+ "where empiId=:empiId and registerDate>=to_date('"
				+ startDate
				+ "','yyyy-mm-dd HH24:mi:ss') and registerDate<=to_date('"
				+ endDate + "','yyyy-mm-dd HH24:mi:ss')";
		String newGroup = diabetesGroup;
		if ("1".equals(manageType)) {
			newGroup = "01";
			needTurn = false;
		} else {
			List<Map<String, Object>> ogtt = null;
			Map<String, Object> groupPara = new HashMap<String, Object>();
			groupPara.put("empiId", empiId);
			if (visitId != null && !"".equals(visitId)) {
				groupPara.put("visitId", visitId);
			}
			List<Map<String, Object>> list = null;
			// 查询本年度已做的随访
			try {
				list = dao.doSqlQuery(sql4, groupPara);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("FBS", fbsNow);
				map.put("PBS", pbsNow);
				list.add(map);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
			groupPara.remove(visitId);
			int visitCount = list.size();
			for (Map<String, Object> r : list) {
				double fbs = paresToDouble(r.get("FBS") + "");
				double pbs = paresToDouble(r.get("PBS") + "");
				if (r.get("PBS") != null && !"".equals(r.get("PBS"))) {
					if (pbs > paresToDouble(badScore2)) {
						controlBadCount++;
					}
				} else {
					if (fbs > paresToDouble(badScore1)) {
						controlBadCount++;
					}
				}
			}
			// 根据原组别和控制情况和一二三组转组标准判断该病人是否需要转组以及新组别
			if ((controlBadCount / visitCount) * 100 > paresToDouble(scaleNum2)
					&& "02".equals(diabetesGroup)) {
				needTurn = true;
				newGroup = "01";
			} else if (((visitCount - controlBadCount) / visitCount) * 100 > paresToDouble(scaleNum1)
					&& "01".equals(diabetesGroup)) {
				needTurn = true;
				newGroup = "02";
			} else if (threeTurn.indexOf("2") > -1
					&& "03".equals(diabetesGroup) && visitCount > 0
					&& list.get(visitCount - 1) != null) {
				newGroup = drm
						.getDiabetesGroupByConfig(
								diabetesType,
								paresToDouble(list.get(visitCount - 1).get(
										"FBS")
										+ ""),
								paresToDouble(list.get(visitCount - 1).get(
										"PBS")
										+ ""));
				if (!newGroup.equals(diabetesGroup)) {
					needTurn = true;
				}
			}
			if (threeTurn.indexOf("1") > -1 && "03".equals(diabetesGroup)) {
				try {
					ogtt = dao.doSqlQuery(sql5, groupPara);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				if (ogtt != null && ogtt.size() > 0) {
					for (Map<String, Object> m : ogtt) {
						String superDiagnose = m.get("SUPERDIAGNOSE") + "";
						if ("y".equals(superDiagnose)) {
							needTurn = true;
							newGroup = "02";
							break;
						}
					}
				}
			}
		}
		result.put("needChangeGroup", needTurn);
		result.put("needAssess", true);
		result.put("diabetesGroup", newGroup);
		result.put("diabetesGroupName", dic.getText(newGroup));
		return result;
	}
	
	public boolean checkHasPlanInTwoWeek(String empiId, String p_visitDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(p_visitDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		Date visitDate1 = new Date();
		visitDate1.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" from ")
				.append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='2'  and to_date(to_char(planDate,'yyyy-mm-dd'),'yyyy-mm-dd') = to_date(to_char(:visitDate1,'yyyy-mm-dd'),'yyyy-mm-dd')")
				.append(" order by planDate desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("visitDate1", visitDate1);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return true;
		}
		return false;
	}
	//查询两周内是否有干预随访
	public boolean checkHasMeddlePlanInTwoWeek(String empiId, String p_planDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(p_planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		cDay1.add(Calendar.DAY_OF_MONTH, 2);
		Date planDate = new Date();
		planDate.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" from ").append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='2' and planDate < :planDate1 and planDate > :planDate2")
				.append(" and visitMeddle in('1','2') order by planDate desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planDate1", planDate);
		try {
			parameters.put("planDate2",new SimpleDateFormat("yyyy-MM-dd").parse(p_planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return true;
		}
		return false;
	}
	public void deleteMeddlePlanInTwoWeek(String empiId, String p_planDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(p_planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		cDay1.add(Calendar.DAY_OF_MONTH, 2);
		Date planDate = new Date();
		planDate.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" delete ").append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='2' and planDate < :planDate1 and planDate > :planDate2")
				.append(" and visitMeddle in('1','2')  and visitId is null ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planDate1", planDate);
		try {
			parameters.put("planDate2",new SimpleDateFormat("yyyy-MM-dd").parse(p_planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "删除干预随访计划失败！", e);
		}
	}
	
	public void insertVisitPlan(HashMap<String, Object> body)
			throws ModelDataOperationException {
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("recordId", body.get("phrId"));
		reqBody.put("businessType", BusinessType.TNB);
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("groupCode", body.get("groupCode"));
		reqBody.put("fixGroupDate", body.get("fixGroupDate"));
		Calendar cDay1 = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(body
					.get("visitDate") + "");
//			date = new SimpleDateFormat("yyyy-MM-dd").parse(body
//					.get("lastPlanDate") + "");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.setTime(date);
		//cDay1.add(Calendar.DAY_OF_MONTH, 1);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("beginDate", date);
		reqBody.put("beginVisitDate", date);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("planDate", date);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("endDate", date);
		reqBody.put("planStatus", "0");
		reqBody.put("sn", body.get("sn"));
		reqBody.put("taskDoctorId", body.get("taskDoctorId"));
		UserRoleToken urt = UserRoleToken.getCurrent();
		reqBody.put("lastModifyUnit", urt.getManageUnitId());
		reqBody.put("lastModifyUser", urt.getUserId());
		reqBody.put("lastModifyDate", new Date());
		if(body.containsKey("visitMeddle")){
			reqBody.put("visitMeddle",body.get("visitMeddle"));
			}
		try {
			dao.doSave("create", PUB_VisitPlan, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		}

	}

	public void insertGroupRecord(Map<String, Object> body,
			HashMap<String, Object> planBody,
			VisitPlanCreator visitPlanCreator, Context ctx)
			throws ModelDataOperationException {
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("phrId", body.get("phrId"));
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("diabetesGroup", body.get("diabetesGroup"));
		reqBody.put("fixDate", new Date());
		String controlResult = "";
		if ((Boolean) body.get("controlBad") == true) {
			controlResult = "1";
		} else if ((Boolean) body.get("controlBad") == false) {
			controlResult = "2";
		}
		reqBody.put("controlResult", controlResult);
		reqBody.put("fbs", body.get("fbs"));
		reqBody.put("pbs", body.get("pbs"));
		reqBody.put("fixType", "5");
		reqBody.put("oldGroup", body.get("oldGroup"));
		UserRoleToken urt = UserRoleToken.getCurrent();
		reqBody.put("createUnit", urt.getManageUnitId());
		reqBody.put("createUser", urt.getUserId());
		reqBody.put("createDate", new Date());
		reqBody.put("lastModifyUnit", urt.getManageUnitId());
		reqBody.put("lastModifyUser", urt.getUserId());
		reqBody.put("lastModifyDate", new Date());
		try {
			dao.doSave("create", MDC_DiabetesFixGroup, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		}
		String visitDate = (String) planBody.get("visitDate");
		String diabetesGroup = (String) planBody.get("groupCode");
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		try {
			String nextDate = drm.getNextVisitDate(diabetesGroup,
					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
			planBody.put("fixGroupDate", visitDate);
			planBody.put("nextDate", nextDate);
			planBody.put("lastVisitDate", visitDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		planBody.put("visitMeddle", "0");
		if (drm.needCreatePlanNoAsses((String) body.get("empiId"), new Date())) {
			createVisitPlan(planBody, visitPlanCreator, ctx);
		}
	}

	public Map<String, Object> listDiabetesVistPlan(Map<String, Object> req)
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
			sc = SchemaController.instance().get(
					"chis.application.dbs.schemas.MDC_DiabetesVisitPlan");
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
				.append(" join MDC_DiabetesRecord d on a.empiId=d.empiId ")
				.append(" left join MDC_DiabetesVisit e on a.empiId=e.empiId and a.visitid=e.visitid");
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
					Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划统计总记录数时失败！", e);
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
						Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划时失败！", e);
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
						"chis.application.dbs.schemas.MDC_DiabetesVisitPlan"));
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
	
	public Map<String, Object> listDiabetesVistPlanQC(Map<String, Object> req)
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
			sc = SchemaController.instance().get(
					"chis.application.dbs.schemas.MDC_DiabetesVisitPlan");
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
				.append(" join MDC_DiabetesRecord d on a.empiId=d.empiId ")
				.append(" left join MDC_DiabetesVisit e on a.empiId=e.empiId and a.visitid=e.visitid");
		StringBuffer countSQL = new StringBuffer(
				"select count(distinct b.idCard) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where)
			.append(" and e.visitway in ('1', '4', '5') and e.visitEffect='1'");
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from (select ").append(sfBuffer.substring(1)).append(",row_number()over(partition by a.empiId order by a.planDate) rn ").append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where)
				.append(" and e.visitEffect='1'  and e.visitway in ('1', '4', '5'))");
			}
			sql.append(" where rn=1 ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询糖尿病随访计划时失败！", e);
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
						"chis.application.dbs.schemas.MDC_DiabetesVisitPlan"));
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
	
	public Boolean checklastvisitplan(String planId,String empiId) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select a.planid as planid  from pub_visitplan a ").
		    append(" where a.businesstype='2' and a.empiid='").append(empiId)
		    .append("' order by a.plandate desc");
		List<Map<String, Object>> rsList = null;
		Map<String, Object> pMap = new HashMap<String, Object>();
		try {
			rsList = dao.doSqlQuery(sql.toString(),pMap);
			if(rsList.size()>0){
				String b=rsList.get(0).get("PLANID").toString();
				if(rsList.get(0).get("PLANID").toString().equals(planId)){
					return true;
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		return false;
	}
	public Map<String, Object> checkHasVisitInPerCycle(String group,
			String empiId, Date visitDate) throws ModelDataOperationException {
		boolean hasVisit = false;
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> record = getLastVistRecordVisitId(empiId, visitDate);
		if (record != null && record.size() > 0) {
			String diabetesGroup = group;
			Date lastVisitDate = (Date) record.get("visitDate");
			Schema sc = null;
			String itemid = "";
			try {
				sc = SchemaController.instance().get(ADMIN_DiabetesConfig);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			if ("03".equals(diabetesGroup)) {
				itemid = "planType3";
			} else if ("02".equals(diabetesGroup)) {
				itemid = "planType2";
			} else {
				itemid = "planType1";
			}
			String exp = null;
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				if (itemid.equals(si.getId())) {
					exp = (String) si.getProperty("expression");
				}
			}
			String planTypeCode = null;
			if (exp != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "select planTypeCode as planTypeCode from "
						+ PUB_PlanInstance
						+ " where instanceType='2' and expression=:expression";
				parameters.put("expression", exp);
				Map<String, Object> plan = new HashMap<String, Object>();
				try {
					List<Map<String, Object>> list = dao.doQuery(sql,
							parameters);
					if (list != null && list.size() > 0) {
						planTypeCode = (String) list.get(0).get("planTypeCode");
					}

					if (planTypeCode != null) {
						plan = dao.doLoad(PUB_PlanType, planTypeCode);
					}
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				int frequency = BSCHISUtil.parseToInt(plan.get("frequency")
						+ "");
				int cycle = (Integer) plan.get("cycle");
				int allCycleDays = BSCHISUtil.getAllCycleDays(lastVisitDate,
						cycle, frequency);
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastVisitDate);
				cal.add(Calendar.DAY_OF_YEAR, allCycleDays / 2);
				Date lineDate = cal.getTime();
				result.put("lineDate",
						new SimpleDateFormat("yyyy年MM月dd日").format(lineDate));
				if (BSCHISUtil.dateCompare(visitDate, lineDate) < 0) {
					hasVisit = true;
				}
				result.put("hasVisit", hasVisit);
			}
		}
		return result;
	}
	public String getNearDiabetesGroup(String empiId) throws ModelDataOperationException {
		String diabetesGroup = "";
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "a.fixDate desc", MDC_DiabetesFixGroup);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询糖尿病最后一次分组失败！", e);
		}
		if (list != null && list.size() > 0) {
			diabetesGroup = (String) list.get(0).get("diabetesGroup");
		}
		return diabetesGroup;
	}	
	public void deleteDiabetesVistPlanbyplanId(String planId)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			String hql=" delete from PUB_VisitPlan where planId ='"+planId+"'and " +
					   " businessType='2'";
			String bfsql="insert into PUB_VisitPlan_delete(PLANID,VISITMEDDLE,TOAPP,TASKDOCTORID," +
					" LASTMODIFYDATE,LASTMODIFYUNIT,LASTMODIFYUSER,EXTEND1,SN,EXTEND2,REMARK,VISITID," +
					" VISITDATE,PLANSTATUS,PLANDATE,BEGINVISITDATE,ENDDATE,BEGINDATE,FIXGROUPDATE," +
					" GROUPCODE,BUSINESSTYPE,EMPIID,RECORDID) " +
					" select PLANID,VISITMEDDLE,TOAPP,TASKDOCTORID,sysdate," +
					" '"+user.getManageUnitId()+"','"+user.getUserId()+"',EXTEND1,SN,EXTEND2,REMARK,VISITID,VISITDATE,PLANSTATUS," +
					" PLANDATE,BEGINVISITDATE,ENDDATE,BEGINDATE,FIXGROUPDATE,GROUPCODE,BUSINESSTYPE,EMPIID," +
					" RECORDID from PUB_VisitPlan where planId ='"+planId+"'and businessType='2'";
			Session con =dao.getSession();
			Transaction t=con.beginTransaction();
			t.begin();
			con.createSQLQuery(bfsql).executeUpdate();
			con.createSQLQuery(hql).executeUpdate();
			t.commit();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除随访计划失败！", e);
		}
	}
}
