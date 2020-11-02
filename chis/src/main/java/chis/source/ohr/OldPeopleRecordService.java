/**
 * @(#)OldPeopleRecordService.java Created on 2012-3-2 下午02:59:14
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.DiabetesRiskDataSource;
import chis.source.dic.DiagnosisType;
import chis.source.dic.HypertensionRiskDataSource;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.WorkType;
import chis.source.mdc.DiabetesRiskModel;
import chis.source.mdc.DiabetesSimilarityModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionSimilarityModel;
import chis.source.mdc.MDCBaseModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.LifeStyleModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class OldPeopleRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(OldPeopleRecordService.class);
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private VisitPlanCreator visitPlanCreator;

	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 初始化老年人档案服务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitializeRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		HealthRecordModel hrm = new HealthRecordModel(dao);
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> healthMap = hrm.getHealthRecordByEmpiId(empiId);
		if (healthMap == null) {
			return;
		}
		String key = (String) healthMap.get("manaDoctorId");
		Dictionary dic = DictionaryController.instance().getDic(
				"chis.dictionary.user01");
		String text = dic.getItem(key).getText();
		Map<String, Object> manaDoctorId = new HashMap<String, Object>();
		manaDoctorId.put("key", key);
		manaDoctorId.put("text", text);
		resMap.put("manaDoctorId", manaDoctorId);

		String unitKey = (String) healthMap.get("manaUnitId");
		Dictionary unitDic = DictionaryController.instance().getDic(
				"chis.@manageUnit");
		String unitText = unitDic.getItem(unitKey).getText();
		Map<String, Object> manaUnitId = new HashMap<String, Object>();
		manaUnitId.put("key", unitKey);
		manaUnitId.put("text", unitText);
		resMap.put("manaUnitId", manaUnitId);
		res.put("body", resMap);
	}

	/**
	 * 老年人档案保存服务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOldPeopleRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			oprModel.saveOldPeopleRecord(op, body, true);
			if ("create".equals(op)) {
				createVisitPlan(body, ctx, dao);
			}
			resBody.put("phrId", body.get("phrId"));
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_OldPeopleRecord, op, phrId, dao,
				empiId);
		vLogService.saveRecords("LAO", op, dao, empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, LNRDAGL_LNRFW, null, dao);
	}

	/**
	 * 加载老年人随访计划数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadPlanVisitRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String queryDate = (String) body.get("queryDate");
		String strDatumn = queryDate.substring(0, queryDate.lastIndexOf("T"));
		Date datumn = BSCHISUtil.toDate(strDatumn);
		try {
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			List<Map<String, Object>> records = oprModel
					.getCurrentYearVisitPlan(datumn, empiId);
			res.put("body", records);
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 当新建随访记录时，准备相关的页面初始化数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitOldPeopleVisitForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		res.put("body", body);
		try {
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			Map<String, Object> oldPeopleRecord = oprModel
					.getOldPeopleRecordByEmpiId(empiId);
			if (oldPeopleRecord != null) {
				body.putAll(oldPeopleRecord);
			}

			LifeStyleModel lsModel = new LifeStyleModel(dao);
			Map<String, Object> lifeStyleRecord = lsModel
					.getLifeStyleByEmpiId(empiId);
			if (lifeStyleRecord != null) {
				body.putAll(lifeStyleRecord);
			}

			HealthRecordModel mrModel = new HealthRecordModel(dao);
			List<Map<String, Object>> pastHistoryList = mrModel
					.getPastHistoryWithOldPeople(empiId);
			String pastHistory = getPastHistory(pastHistoryList);
			body.put("pastHistory", pastHistory);
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 组装既往史字符串
	 * 
	 * @param pastHistoryList
	 * @return
	 */
	private String getPastHistory(List<Map<String, Object>> pastHistoryList) {
		StringBuilder temp = new StringBuilder();
		for (Iterator<Map<String, Object>> i = pastHistoryList.iterator(); i
				.hasNext();) {
			Map<String, Object> m = i.next();
			String result = (String) m.get("diseaseText");
			if (result != null && !"".equals(result)) {
				temp.append(result);
				temp.append(",");
			}
		}
		String result = temp.toString();
		if (result.length() > 0) {
			return result.substring(0, result.lastIndexOf(","));
		}
		return result;
	}

	/**
	 * 保存老年人随访记录、体格检查、中医辩体描述
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked" })
	public void doSaveOldPeopleVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> manageRecord = (HashMap<String, Object>) body
				.get("visitRecord");
		if(manageRecord==null){
			manageRecord = body;
		}
		String empiId = (String) manageRecord.get("empiId");
		String phrId = (String) manageRecord.get("phrId");
		String planId = (String) manageRecord.get("planId");
		String inputDate = (String) manageRecord.get("inputDate");
		String visitEffect = (String) manageRecord.get("visitEffect");
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		resBody.putAll(manageRecord);

		try {
			if ("create".equals(op)) {
				Date oldlastVisitDate = BSCHISUtil.toDate(inputDate);
				HealthRecordModel mrModel = new HealthRecordModel(dao);
				mrModel.updateHealthRecordWithOldPeople(oldlastVisitDate,
						empiId);
			}
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			OldPeopleVisitModel opvModel = new OldPeopleVisitModel(dao);
			manageRecord.put("lastModifyUser", userId);
			manageRecord.put("lastModifyDate", formatter.format(new Date()));
			Map<String, Object> visitRecords = opvModel.saveOldPeopleVisit(op,
					manageRecord);
			resBody.putAll(visitRecords);

			String visitId;
			if ("create".equals(op)) {
				visitId = (String) visitRecords.get("visitId");
			} else {
				visitId = (String) manageRecord.get("visitId");
			}
//			vLogService.saveVindicateLog(MDC_OldPeopleVisit, op, visitId, dao,
//					empiId);

			VisitPlanModel vpMdodel = new VisitPlanModel(dao);
			HashMap<String, Object> visitRecord = new HashMap<String, Object>();
			visitRecord.put("planId", planId);
			String planStatus = PlanStatus.VISITED;
			if (VisitEffect.CONTINUE.equals(visitEffect)) {
				planStatus = PlanStatus.VISITED;
			} else if (VisitEffect.LOST.equals(visitEffect)) {
				planStatus = PlanStatus.LOST;
			} else if (VisitEffect.END.equals(visitEffect)) {
				planStatus = PlanStatus.WRITEOFF;
			}
			visitRecord.put("visitId", visitId);
			visitRecord.put("planStatus", planStatus);
			visitRecord.put("visitDate", inputDate);
			visitRecord.put("lastModifyUser", userId);
			visitRecord.put("lastModifyDate", new SimpleDateFormat(
					Constants.DEFAULT_SHORT_DATE_FORMAT).format(new Date()));
			vpMdodel.saveVisitPlan("update", visitRecord);
			vpMdodel.setOverDatePlan(phrId, inputDate.substring(0, 10),
					BusinessType.LNR);

			/* 注销老年人档案 */
			if (visitEffect.equals(VisitEffect.END)) {
				Map<String, Object> cancelMap = new HashMap<String, Object>();
				cancelMap.put("phrId", phrId);
				cancelMap.put("status", "1");
				cancelMap.put("cancellationUser", userId);
				cancelMap.put("cancellationDate", formatter.format(new Date()));
				cancelMap.put("cancellationUnit",
						UserUtil.get(UserUtil.MANAUNIT_ID));
				cancelMap.put("cancellationReason", CancellationReason.OTHER);
				cancelMap.put("lastModifyUser", userId);
				cancelMap.put("lastModifyDate", formatter.format(new Date()));
				cancelMap.put("lastModifyUnit",
						UserUtil.get(UserUtil.MANAUNIT_ID));
				cancelMap.put("deadReason", cancelMap.get("deadReason"));
				oprModel.saveOldPeopleRecord("update", cancelMap, false);

				// 注销未执行的老年人随访计划
				vpMdodel.logOutVisitPlan(vpMdodel.RECORDID, phrId,
						BusinessType.LNR);

			} else {
				// 重新生产随访计划
				createVisitPlan(manageRecord, ctx, dao);
			}
			/* 保存老年人健康体检列表 */
			List<Map<String, Object>> checkupList = (List<Map<String, Object>>) body
					.get("checkupList");
			if (checkupList != null && checkupList.size() > 0) {
				oprModel.deleteCheckupList(visitId);
				for (int i = 0; i < checkupList.size(); i++) {
					Map<String, Object> checkupMap = (HashMap<String, Object>) checkupList
							.get(i);
					checkupMap.put("recordId", visitId);
					checkupMap.put("empiId", empiId);
					checkupMap.put("phrId", phrId);
					oprModel.saveOldPeopleCheckup("create", checkupMap);
				}
			}

			/* 保存老年人中医遍体描述信息 */
			Map<String, Object> desMap = (HashMap<String, Object>) body
					.get("description");
			if (desMap != null && !desMap.isEmpty()) {
				oprModel.deleteDescription(visitId);
				desMap.put("visitId", visitId);
				oprModel.saveOldPeopleDescription("create", desMap);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
		// 高血压高危需求
		boolean blnConstriction = !BSCHISUtil.isBlank(manageRecord.get("sbp"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(manageRecord.get("dbp"));
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && (Integer) manageRecord.get("sbp") >= 120 && (Integer) manageRecord
					.get("sbp") <= 139)
					|| (blnDiastolic && (Integer) manageRecord.get("dbp") >= 80 && (Integer) manageRecord
							.get("dbp") <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk(empiId,
						(String) manageRecord.get("phrId"),
						(Integer) manageRecord.get("sbp"),
						(Integer) manageRecord.get("dbp"), "1");
			}
		}
		// 生成疑似记录
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && (Integer) manageRecord.get("sbp") >= 140)
					|| (blnConstriction && (Integer) manageRecord.get("dbp") >= 90)) {
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(
						dao);
				hsm.insertHypertensionSimilarity(empiId,
						(String) body.get("phrId"),
						Integer.parseInt(manageRecord.get("sbp") + ""),
						Integer.parseInt(manageRecord.get("dbp") + ""),
						manageRecord);
			}
		}
	}

	/**
	 * 生产随访计划
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	private void createVisitPlan(Map<String, Object> body, Context ctx,
			BaseDAO dao) throws ModelDataOperationException {
		try {
			String empiId = (String) body.get("empiId");
			String userId = (String) body.get("lastModifyUser");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("businessType", BusinessType.LNR);
			params.put("empiId", empiId);
			params.put("recordId", body.get("phrId"));
			params.put("birthday", body.get("birthday"));
			params.put("taskDoctorId", body.get("manaDoctorId"));
			String modeType = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, BusinessType.LNR + "_planMode");
			if (PlanMode.BY_RESERVED.equals(modeType)) {
				if (body.get("nextDate") != null) {
					params.put("reserveDate", body.get("nextDate"));
				} else {
					params.put("reserveDate", body.get("createDate"));
				}
			}
			boolean nextYear = false;
			if (body.get("nextYear") != null) {
				nextYear = (Boolean) body.get("nextYear");
			}
			params.put("$sectionCutOffDate", BSCHISUtil.getSectionCutOffDate(
					"oldPeopleEndMonth", nextYear));
			params.put("$planMode", modeType);
			PlanType planType = visitPlanCreator.create(params, ctx);
			if (planType == null) {
				return;
			}
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			oprModel.updateOldPeopleVisitStatus(planType.getPlanTypeCode(),
					empiId, userId);
		} catch (Exception e) {
			throw new ModelDataOperationException("生产老年人随访计划失败", e);
		}
	}

	/**
	 * 注销老年人档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogoutOldPeopleRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = StringUtils.trimToEmpty((String) body.get("phrId"));
		String empiId = (String) body.get("empiId");
		String deadReason = StringUtils.trimToEmpty((String) body
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) body
				.get("cancellationReason"));
		try {
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			oprModel.logoutOldPeopleRecord(phrId, cancellationReason,
					deadReason);
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			vpModel.logOutVisitPlan(vpModel.RECORDID, phrId, BusinessType.LNR);
			res.put(RES_BODY, new HashMap<String, Object>());
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_OldPeopleRecord, "3", phrId, dao,
				empiId);
		vLogService.saveRecords("LAO", "logout", dao, empiId);
	}

	/**
	 * 控制按钮权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadControl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("empiId", req.get("empiId"));
			Map<String, Boolean> update = ControlRunner.run(MDC_OldPeopleVisit,
					data, ctx, ControlRunner.CREATE, ControlRunner.UPDATE);
			res.put("body", update);
		} catch (Exception e) {
			logger.error("Save hospitalization message error!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销老年人档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLastVisitedData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		try {
			OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
			String visitId = oprModel.getLastOldPeopleVisitedData(empiId);
			res.put("visitId", visitId);
		} catch (ModelDataOperationException e) {
			logger.error("load visitedPlan is fail");
			throw new ServiceException(e);
		}
	}
}
