/**
 * @(#)HypertensionService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DiagnosisType;
import chis.source.dic.HypertensionRiskStatus;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.tr.TumourHighRiskVisitModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionRiskService extends HypertensionService {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionRiskService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	@SuppressWarnings("unchecked")
	public void doListHypertensionRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		SimpleQuery query = new SimpleQuery();
		query.execute(req, res, ctx);
		List<Map<String, Object>> body = (List<Map<String, Object>>) res
				.get("body");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> para = new HashMap<String, Object>();
		String sql = "select empiId from MDC_HypertensionRecord where status='0' and empiId=:empiId";
		for (Map<String, Object> map : body) {
			String empiId = (String) map.get("empiId");
			para.put("empiId", empiId);
			List<Map<String, Object>> l = dao.doSqlQuery(sql, para);
			if (l != null && l.size() > 0) {
				map.put("hyCreate", "y");
				map.put("hyCreate_text", "是");
			} else {
				map.put("hyCreate", "n");
				map.put("hyCreate_text", "否");
			}
			result.add(map);
		}
		res.put("body", result);
	}

	@SuppressWarnings("unchecked")
	public void doSaveConfirmHypertensionRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		String riskId = (String) body.get("riskId");
		String empiId = (String) body.get("riskId");
		String sql = "update MDC_HypertensionRisk set statusCase=:statusCase,confirmUser"
				+ "=:confirmUser,confirmDate=:confirmDate,confirmUnit=:confirmUnit "
				+ "where riskId=:riskId";
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("statusCase", body.get("result"));
		UserRoleToken ur = UserRoleToken.getCurrent();
		para.put("confirmUser", ur.getUserId());
		para.put("confirmDate", new Date());
		para.put("confirmUnit", ur.getManageUnitId());
		para.put("riskId", riskId);
		dao.doUpdate(sql, para);
		vLogService.saveVindicateLog(MDC_HypertensionRisk, "6", riskId, dao,
				empiId);
		HypertensionRiskModel drm = new HypertensionRiskModel(dao);
		Map<String, Object> workList = new HashMap<String, Object>();
		Calendar c = Calendar.getInstance();
		workList.put("recordId", body.get("phrId"));
		workList.put("empiId", body.get("empiId"));
		workList.put("otherId", body.get("riskId"));
		workList.put("beginDate", c.getTime());
		c.add(Calendar.YEAR, 1);
		workList.put("endDate", c.getTime());
		workList.put("count", 1);
		workList.put("doctorId", ur.getUserId());
		workList.put("manaUnitId", ur.getManageUnitId());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doInitializeHypertensionRiskAssessment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, ExpException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resBody = new HashMap();
		HypertensionRiskModel hModel = new HypertensionRiskModel(dao);
		resBody = hModel.initializeHypertensionRiskAssessment(empiId);
		res.put("body", resBody);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doLogoutHypertensionRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");
		String deadDate = (String) body.get("deadDate");

		HypertensionRiskModel hModel = new HypertensionRiskModel(dao);
		try {
			hModel.logoutHypertensionRisk(empiId, cancellationReason,
					deadReason, deadDate);
		} catch (ModelDataOperationException e) {
			logger.error("Logout hypertension record failed.", e);
			throw new ServiceException(e);
		}
		// **注销未执行过的随访计划
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			String[] businessTypes = { BusinessType.HypertensionRisk };
			vpModel.logOutVisitPlan(vpModel.EMPIID, empiId, businessTypes);
		} catch (ModelDataOperationException e) {
			logger.error("Logout hypertension visit plan failed.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHypertensionRiskAssessment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ModelDataOperationException, ControllerException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recordId = (String) body.get("recordId");
		String empiId = (String) body.get("empiId");
		String createFlag = (String) body.get("createFlag");
		if ("0".equals(createFlag)) {
			body.put("createFlag", "1");
		}
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_HypertensionRiskAssessment, body, false);
		if ("create".equals(op)) {
			recordId = (String) m.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_HypertensionRiskAssessment, op,
				recordId, dao, empiId);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		boolean hasPlan = vpm.checkHasVisitPlan(empiId,
				BusinessType.HypertensionRisk);
		if (!hasPlan) {
			op = "create";
		}
		if ("create".equals(op)) {
			Application app = ApplicationUtil
					.getApplication(Constants.UTIL_APP_ID);
			String planTypeCode = (String) app
					.getProperty(BusinessType.HypertensionRisk
							+ "_planTypeCode");
			Map<String, Object> planType = dao.doLoad(PUB_PlanType,
					planTypeCode);
			if (planType == null) {
				throw new ModelDataOperationException(
						Constants.CODE_RECORD_NOT_FOUND, "高血压高危人群随访参数未设置");
			}

			Calendar c = Calendar.getInstance();
			c.setTime(BSCHISUtil.toDate((String) body.get("inputDate")));
			body.put("planDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR,
					-Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
			body.put("beginDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR,
					Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app
					.getProperty(BusinessType.HypertensionRisk + "_delayDays")));
			body.put("endDate", c.getTime());
			body.put("planStatus", PlanStatus.NEED_VISIT);
			body.put("sn", 0);
			HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
			hrm.createHypertensionRiskVisitPlan(body);
			hrm.updateFirstAssessmentDate(body);
		}
		res.put("body", m);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doSaveHypertensionRiskVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ControllerException, ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String visitId = (String) body.get("visitId");
		String empiId = (String) body.get("empiId");
		boolean needCancellation = (Boolean) body.get("needCancellation");
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_HypertensionRiskVisit, body, false);
		if ("create".equals(op)) {
			visitId = (String) m.get("visitId");
		}
		vLogService.saveVindicateLog(MDC_HypertensionRiskVisit, op, visitId,
				dao, empiId);
		HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
		hrm.saveHealthTeach(body, m, vLogService);
		Map plan = new HashMap();
		if ("create".equals(op) && !needCancellation) {
			Application app = ApplicationUtil
					.getApplication(Constants.UTIL_APP_ID);
			String planTypeCode = (String) app
					.getProperty(BusinessType.HypertensionRisk
							+ "_planTypeCode");
			Map<String, Object> planType = dao.doLoad(PUB_PlanType,
					planTypeCode);
			int frequency = Integer.valueOf((String) planType.get("frequency"));
			int cycle = (Integer) planType.get("cycle");

			Calendar c = Calendar.getInstance();
			c.setTime(BSCHISUtil.toDate((String) body.get("visitDate")));
			c.add(frequency, cycle);
			body.put("planDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR,
					-Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
			body.put("beginDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR,
					Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app
					.getProperty(BusinessType.HypertensionRisk + "_delayDays")));
			body.put("endDate", c.getTime());
			body.put("planStatus", PlanStatus.NEED_VISIT);
			Object objSN = body.get("sn");
			int intSN = objSN == null ? 0 : Integer.parseInt(objSN.toString());
			// body.put("sn", (Integer)body.get("sn")+1);
			body.put("sn", intSN + 1);

			hrm.createHypertensionRiskVisitPlan(body);

			plan.put("visitId", m.get("visitId"));
		}
		// 更新计划状态

		plan.put("planId", body.get("planId"));
		plan.put("visitDate", body.get("visitDate"));
		plan.put("planStatus", PlanStatus.VISITED);
		dao.doSave("update", BSCHISEntryNames.PUB_VisitPlan, plan, false);
		// 注销高危档案，更新转归情况
		if (needCancellation) {
			hrm.logoutHypertensionRisk(empiId, "9", null, null);
			hrm.updateHypertensionRisk(empiId, body, true);
			hrm.logOutVisitPlan("empiId", empiId, BusinessType.HypertensionRisk);
		} else {
			hrm.updateHypertensionRisk(empiId, body, false);
		}
		// 生成疑似记录
		boolean blnConstriction = !BSCHISUtil.isBlank(body.get("constriction"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(body.get("diastolic"));
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && (Integer) body.get("constriction") >= 140)
					|| (blnConstriction && (Integer) body.get("diastolic") >= 90)) {
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(
						dao);
				hsm.insertHypertensionSimilarity(empiId,
						(String) body.get("phrId"),
						Integer.parseInt(body.get("constriction") + ""),
						Integer.parseInt(body.get("diastolic") + ""), body);
			}
		}
	}

	/**
	 * 根据随访ID获取随访健康教育信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadHypertensionRiskVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String wayId = (String) reqBody.get("pkey");
		HypertensionRiskModel drm = new HypertensionRiskModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = drm.loadHypertensionRiskVisit(wayId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Get hypertension visit health teach by visitId failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}

	public void doListHypertensionRiskVistPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionRiskModel drm = new HypertensionRiskModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = drm.listHypertensionRiskVistPlan(req);
		} catch (ModelDataOperationException e) {
			logger.error(
					"paging and querying Hypertension high risk visit plan records failure.",
					e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doSaveCloseHypertensionRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		Map body = (Map) req.get("body");

		if (body.get("effect").equals("2")) {
			HypertensionModel recordModel = new HypertensionModel(dao);
			Map<String, Object> workList = new HashMap<String, Object>();
			Calendar c = Calendar.getInstance();
			workList.put("recordId", body.get("phrId"));
			workList.put("empiId", body.get("empiId"));
			workList.put("beginDate", c.getTime());
			c.add(Calendar.YEAR, 1);
			workList.put("endDate", c.getTime());
			workList.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
			workList.put("count", 1);
			UserRoleToken ur = UserRoleToken.getCurrent();
			workList.put("doctorId", ur.getUserId());
			workList.put("manaUnitId", ur.getManageUnitId());
			recordModel.addHypertensionRecordWorkList(workList);
		}
		HypertensionRiskModel drm = new HypertensionRiskModel(dao);
		drm.writeOffHypertensionRiskVisitPlan(body);

		UserRoleToken ur = UserRoleToken.getCurrent();
		body.put("closeUser", ur.getUserId());
		body.put("closeUnit", ur.getManageUnitId());
		body.put("closeDate", Calendar.getInstance().getTime());
		body.put("status", HypertensionRiskStatus.CLOSE);
		dao.doSave("update", BSCHISEntryNames.MDC_HypertensionRisk, body, false);
		res.put("body", body);
		String riskId = (String) body.get("riskId");
		String empiId = (String) body.get("riskId");
		vLogService.saveVindicateLog(MDC_HypertensionRisk, "7", riskId, dao,
				empiId);
	}

}
