package chis.source.rvc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import chis.source.dic.DiagnosisType;
import chis.source.dic.HypertensionRiskDataSource;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionSimilarityModel;
import chis.source.mdc.MDCBaseModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.PastHistoryModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class RetiredVeteranCadresService extends AbstractActionService
		implements DAOSupportable {
	private VisitPlanCreator visitPlanCreator;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(RetiredVeteranCadresService.class);

	@SuppressWarnings({ "deprecation", "unchecked" })
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
		PastHistoryModel phModel = new PastHistoryModel(dao);
		List<Map<String, Object>> list = phModel
				.queryPastHistoryByEmpiId(empiId);
		String pastHistory = "";
		String other = "";
		String surgicalHistory = "";
		String rtaumaHistory = "";
		String traumaPsychic = "";
		String allergicHistory = "";
		boolean flag = true;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> record = list.get(i);
			String diseaseCode = (String) record.get("diseaseCode");
			if (flag && "02".equals(diseaseCode.substring(0, 2))) {
				int code = Integer.parseInt(diseaseCode);
				String diseaseText = (String) record.get("diseaseText");
				switch (code) {
				case 201:
					pastHistory = "98";
					flag = false;
					break;
				case 202:
					pastHistory += "01,";
					break;
				case 203:
					pastHistory += "02,";
					break;
				case 204:
					other += "冠心病,";
					break;
				case 205:
					other += "慢性阻塞性肺疾病,";
					break;
				case 206:
					pastHistory += "03,";
					break;
				case 207:
					other += "脑卒中,";
					break;
				case 208:
					other += "重性精神疾病,";
					break;
				case 209:
					other += "结核病,";
					break;
				case 210:
					other += "肝脏疾病,";
					break;
				case 211:
					other += "先天畸形,";
					break;
				case 213:
					other += "肾脏疾病,";
					break;
				case 214:
					other += "贫血,";
					break;
				default:
					other += diseaseText + ",";
					break;
				}
			} else if ("0301".equals(diseaseCode)) {
				surgicalHistory = "2";
			} else if ("0302".equals(diseaseCode)) {
				surgicalHistory = "1";
			} else if ("0601".equals(diseaseCode)) {
				rtaumaHistory = "2";
			} else if ("0602".equals(diseaseCode)) {
				rtaumaHistory = "1";
			} else if ("01".equals(diseaseCode.substring(0, 2))) {
				int code = Integer.parseInt(diseaseCode);
				switch (code) {
				case 101:
					allergicHistory = "2";
					break;
				default:
					allergicHistory = "1";
					break;
				}
			} else if ("1107".equals(diseaseCode)) {
				traumaPsychic = "1";
			}
		}
		if (!"".equals(pastHistory) && flag) {
			pastHistory = pastHistory.substring(0, pastHistory.length() - 1);
			other = "";
		}
		if (!"".equals(other)) {
			other = other.substring(0, other.length() - 1);
			if (!"".equals(pastHistory)) {
				pastHistory += ",99";
			} else {
				pastHistory = "99";
			}
		}
		if ("".equals(surgicalHistory)) {
			surgicalHistory="3";
		}
		if ("".equals(rtaumaHistory)) {
			rtaumaHistory="3";
		}
		if ("".equals(traumaPsychic)) {
			traumaPsychic="3";
		}
		if ("".equals(allergicHistory)) {
			allergicHistory="3";
		}
		Map<String, Object> hisRecord = new HashMap<String, Object>();
		hisRecord.put("onceDisease", pastHistory);
		hisRecord.put("other", other);
		hisRecord.put("surgicalHistory", surgicalHistory);
		hisRecord.put("rtaumaHistory", rtaumaHistory);
		hisRecord.put("traumaPsychic", traumaPsychic);
		hisRecord.put("allergicHistory", allergicHistory);
		hisRecord = SchemaUtil.setDictionaryMessageForForm(hisRecord,
				RVC_RetiredVeteranCadresRecord);
		resMap.putAll(hisRecord);
		res.put("body", resMap);
	}

	@SuppressWarnings("unchecked")
	protected void doSaveRetiredVeteranCadresRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
				dao);
		rvcModule.saveRetiredVeteranCadresRecord(op, body);
		if ("create".equals(op)) {
			createVisitPlan(body, ctx, dao);
		}
		resBody.put("phrId", phrId);
		vLogService.saveVindicateLog(RVC_RetiredVeteranCadresRecord, op, phrId,
				dao, empiId);
		vLogService.saveRecords("LI", op,dao, empiId);
	}

	private void createVisitPlan(Map<String, Object> body, Context ctx,
			BaseDAO dao) throws ModelDataOperationException {
		try {
			String empiId = (String) body.get("empiId");
			String userId = (String) body.get("lastModifyUser");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("businessType", BusinessType.RVC);
			params.put("empiId", empiId);
			params.put("recordId", body.get("phrId"));
			// params.put("birthday", body.get("birthday"));
			params.put("taskDoctorId", body.get("manaDoctorId"));
			String modeType = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, BusinessType.RVC + "_planMode");
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
			params.put("$sectionCutOffDate",
					BSCHISUtil.getSectionCutOffDate("diabetesEndMonth", nextYear));
			params.put("$planMode", modeType);
			PlanType planType = visitPlanCreator.create(params, ctx);
			if (planType == null) {
				return;
			}
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			rvcModule.updateRVCVisitStatus(planType.getPlanTypeCode(), empiId,
					userId);
		} catch (Exception e) {
			throw new ModelDataOperationException("生产离休干部随访计划失败", e);
		}
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
			Map<String, Boolean> update = ControlRunner.run(
					RVC_RetiredVeteranCadresVisit, data, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
			res.put("body", update);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public void doGetVisitType(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			String modeType = ApplicationUtil.getProperty(
					Constants.UTIL_APP_ID, BusinessType.RVC + "_planMode");
			res.put("modeType", modeType);
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}

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
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			List<Map<String, Object>> records = rvcModule
					.getCurrentYearVisitPlan(datumn, empiId);
			res.put("body", records);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doLogoutRVCRecord(Map<String, Object> req,
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
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			rvcModule.logoutRVCRecord(phrId, cancellationReason,
					deadReason);
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			vpModel.logOutVisitPlan(vpModel.RECORDID, phrId, BusinessType.RVC);
			res.put(RES_BODY, new HashMap<String, Object>());
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(RVC_RetiredVeteranCadresRecord, "3",
				phrId, dao, empiId);
		vLogService.saveRecords("LI", "logout", dao, empiId);
	}

	@SuppressWarnings("unchecked")
	protected void doInitRVCVisitForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		res.put("body", body);
		try {
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			Map<String, Object> record = rvcModule.getRVCRecordByEmpiId(empiId);
			if (record != null) {
				body.putAll(record);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doSaveRVCVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> manageRecord = (Map<String, Object>) req
				.get("body");
		String op = (String) req.get("op");
		String empiId = (String) manageRecord.get("empiId");
		String phrId = (String) manageRecord.get("phrId");
		String planId = (String) manageRecord.get("planId");
		String createDate = (String) manageRecord.get("createDate");
		// String visitEffect = (String) manageRecord.get("visitEffect");
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		resBody.putAll(manageRecord);

		try {
			RetiredVeteranCadresModel rvcModule = new RetiredVeteranCadresModel(
					dao);
			manageRecord.put("lastModifyUser", userId);
			manageRecord.put("lastModifyDate", formatter.format(new Date()));
			Map<String, Object> visitRecords = rvcModule.saveRVCVisitRecord(op,
					manageRecord);
			resBody.putAll(visitRecords);

			String visitId;
			if ("create".equals(op)) {
				visitId = (String) visitRecords.get("visitId");
			} else {
				visitId = (String) manageRecord.get("visitId");
			}
			vLogService.saveVindicateLog(RVC_RetiredVeteranCadresVisit, op,
					visitId, dao, empiId);

			VisitPlanModel vpMdodel = new VisitPlanModel(dao);
			HashMap<String, Object> visitRecord = new HashMap<String, Object>();
			visitRecord.put("planId", planId);
			String planStatus = PlanStatus.VISITED;
			// if (VisitEffect.CONTINUE.equals(visitEffect)) {
			// planStatus = PlanStatus.VISITED;
			// } else if (VisitEffect.LOST.equals(visitEffect)) {
			// planStatus = PlanStatus.LOST;
			// } else if (VisitEffect.END.equals(visitEffect)) {
			// planStatus = PlanStatus.WRITEOFF;
			// }
			visitRecord.put("visitId", visitId);
			visitRecord.put("planStatus", planStatus);
			visitRecord.put("visitDate", createDate);
			visitRecord.put("lastModifyUser", userId);
			visitRecord.put("lastModifyDate", new SimpleDateFormat(
					Constants.DEFAULT_SHORT_DATE_FORMAT).format(new Date()));
			vpMdodel.saveVisitPlan("update", visitRecord);
			vpMdodel.setOverDatePlan(phrId, createDate.substring(0, 10),
					BusinessType.RVC);

			// if (visitEffect.equals(VisitEffect.END)) {
			// Map<String, Object> cancelMap = new HashMap<String, Object>();
			// cancelMap.put("phrId", phrId);
			// cancelMap.put("status", "1");
			// cancelMap.put("cancellationUser", userId);
			// cancelMap.put("cancellationDate", formatter.format(new Date()));
			// cancelMap.put("cancellationUnit",
			// UserUtil.get(UserUtil.MANAUNIT_ID));
			// cancelMap.put("cancellationReason", CancellationReason.OTHER);
			// cancelMap.put("lastModifyUser", userId);
			// cancelMap.put("lastModifyDate", formatter.format(new Date()));
			// cancelMap.put("lastModifyUnit",
			// UserUtil.get(UserUtil.MANAUNIT_ID));
			// cancelMap.put("deadReason", cancelMap.get("deadReason"));
			// rvcModule.saveOldPeopleRecord("update", cancelMap, false);
			//
			// // 注销未执行的老年人随访计划
			// vpMdodel.logOutVisitPlan(vpMdodel.RECORDID, phrId,
			// BusinessType.LNR);
			//
			// } else {
			// 重新生产随访计划
			createVisitPlan(manageRecord, ctx, dao);
			// }
		} catch (ModelDataOperationException e) {
			logger.error("save OldPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}

}
