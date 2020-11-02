/**
 * @(#)HealthCheckService.java Created on 2012-4-16 上午10:06:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.check;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.DiabetesRiskDataSource;
import chis.source.dic.DiagnosisType;
import chis.source.dic.WorkType;
import chis.source.mdc.DiabetesOGTTModel;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.DiabetesRiskModel;
import chis.source.mdc.DiabetesSimilarityModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionSimilarityModel;
import chis.source.pub.PublicModel;
import chis.source.pub.PublicService;

import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.service.adapter.ServiceAdaptItem;


/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class HealthCheckService extends AbstractActionService implements
		DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(HealthCheckService.class);

	/**
	 * 保存健康检查记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitAnnualHealthCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		PublicModel pm = new PublicModel(dao);
		try {
			String manaDoctorId = pm.getManaDoctor(empiId, EHR_HealthRecord);
			Map<String, Object> resBody = new HashMap<String, Object>();
			Dictionary dic = DictionaryController.instance().get(
					"chis.dictionary.user01");
			resBody.put("key", manaDoctorId);
			resBody.put("text", dic.getText(manaDoctorId));
			res.put("body", resBody);
		} catch (Exception e) {
			logger.error("init AnnualHealthCheck is failed");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存健康检查记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveAnnualHealthCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		WorkListModel wlModel = new WorkListModel(dao);
		Map<String, Object> value = new HashMap<String, Object>();
		try {
			value = hcModel.saveAnnualHealthCheck(op, body);
		} catch (ModelDataOperationException e) {
			logger.error("saving SaveAnnualHealthCheck is fail");
			throw new ServiceException(e);
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("workType", "17");
		parameters.put("empiId", empiId);
		try {
			wlModel.deleteWorkList(parameters);
		} catch (ModelDataOperationException e) {
			logger.error("saving SaveAnnualHealthCheck is fail");
			throw new ServiceException(e);
		}
		value.putAll(SchemaUtil.setDictionaryMessageForForm(body,
				HC_HealthCheck));
		resBody.putAll(value);
		String healthCheck = (String) resBody.get("healthCheck");
		vLogService.saveVindicateLog(HC_HealthCheck, op, healthCheck, dao,
				empiId);
		//打签约项目完成标识
		this.finishSCServiceTask(empiId, JKTJ_TJFW, null, dao);
		// 高血压高危需求
		boolean blnConstriction = !BSCHISUtil.isBlank(resBody
				.get("constriction"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(resBody.get("diastolic"));
		boolean blnConstriction_L = !BSCHISUtil.isBlank(resBody
				.get("constriction_L"));
		boolean blnDiastolic_L = !BSCHISUtil
				.isBlank(resBody.get("diastolic_L"));
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction
					&& Integer.parseInt(resBody.get("constriction") + "") >= 120 && Integer
					.parseInt(resBody.get("constriction") + "") <= 139)
					|| (blnDiastolic
							&& Integer.parseInt(resBody.get("diastolic") + "") >= 80 && Integer
							.parseInt(resBody.get("diastolic") + "") <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk((String) resBody.get("empiId"),
						(String) resBody.get("phrId"),
						Integer.parseInt(resBody.get("constriction") + ""),
						Integer.parseInt(resBody.get("diastolic") + ""), "3");
			}
		}
		if (blnConstriction_L || blnDiastolic_L) {
			if ((blnConstriction_L
					&& Integer.parseInt(resBody.get("constriction_L") + "") >= 120 && Integer
					.parseInt(resBody.get("constriction_L") + "") <= 139)
					|| (blnDiastolic_L
							&& Integer
									.parseInt(resBody.get("diastolic_L") + "") >= 80 && Integer
							.parseInt(resBody.get("diastolic_L") + "") <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk((String) resBody.get("empiId"),
						(String) resBody.get("phrId"),
						Integer.parseInt(resBody.get("constriction_L") + ""),
						Integer.parseInt(resBody.get("diastolic_L") + ""), "3");
			}
		}
		// 高血压疑似记录
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && Integer.parseInt(resBody
					.get("constriction") + "") >= 140)
					|| (blnDiastolic && Integer.parseInt(resBody
							.get("diastolic") + "") >= 90)) {
				HypertensionSimilarityModel hrm = new HypertensionSimilarityModel(
						dao);
				hrm.insertHypertensionSimilarity(
						(String) resBody.get("empiId"),
						(String) resBody.get("phrId"),
						Integer.parseInt(resBody.get("constriction") + ""),
						Integer.parseInt(resBody.get("diastolic") + ""),
						resBody);
			}
		}
		if (blnConstriction_L || blnDiastolic_L) {
			if ((blnConstriction_L && Integer.parseInt(resBody
					.get("constriction_L") + "") >= 140)
					|| (blnDiastolic_L && Integer.parseInt(resBody
							.get("diastolic_L") + "") >= 90)) {
				HypertensionSimilarityModel hrm = new HypertensionSimilarityModel(
						dao);
				hrm.insertHypertensionSimilarity(
						(String) resBody.get("empiId"),
						(String) resBody.get("phrId"),
						Integer.parseInt(resBody.get("constriction_L") + ""),
						Integer.parseInt(resBody.get("diastolic_L") + ""),
						resBody);
			}
		}
	}

	/**
	 * 保存住院情况数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveInhospitalSituation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try {
			Map<String, Object> key = hcModel.saveInhospitalSituation(op, body);
			if ("create".equals(op)) {
				body.put("situationId", key.get("situationId"));
			}
			body = SchemaUtil.setDictionaryMessageForList(body,
					HC_InhospitalSituation);
			vLogService.saveVindicateLog(HC_InhospitalSituation, op,
					(String) body.get("situationId"), dao);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("Save hospitalization message error!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存用药情况数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveMedicineSituation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try {
			Map<String, Object> key = hcModel.saveMedicineSituation(op, body);
			if ("create".equals(op)) {
				body.put("situationId", key.get("situationId"));
			}
			body = SchemaUtil.setDictionaryMessageForList(body,
					HC_MedicineSituation);
			vLogService.saveVindicateLog(HC_MedicineSituation, op,
					(String) body.get("situationId"), dao);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("Save hospitalization message error!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存辅助检查数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveAccessoryExaminationData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try {
			Map<String, Object> key = hcModel.saveAccessoryExaminationData(op,
					body, true);
			if ("create".equals(op)) {
				body.put("recordId", key.get("recordId"));
			}
			body = SchemaUtil.setDictionaryMessageForList(body,
					HC_AccessoryExamination);
			vLogService.saveVindicateLog(HC_AccessoryExamination, op,
					(String) body.get("recordId"), dao);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("Save AccessoryExamination message error!", e);
			throw new ServiceException(e);
		}

		// 空腹血糖6.1~＜7.0mmol/L新建糖血压高危核实
		if (!BSCHISUtil.isBlank(body.get("fbs"))) {
			double fbs = Double.valueOf((String) body.get("fbs"));
			if (fbs >= 6.1 && fbs < 7.0) {
				DiabetesOGTTModel drm = new DiabetesOGTTModel(dao);
				drm.insertDiabetesOGTTRecord((String) body.get("empiId"),
						(String) body.get("phrId"), fbs, null, body);
			}
			if (fbs >= 7) {
				DiabetesSimilarityModel dsm = new DiabetesSimilarityModel(dao);
				dsm.insertDiabetesSimilarity((String) body.get("recordId"),(String) body.get("empiId"),
						(String) body.get("phrId"), fbs, null,DiabetesRiskDataSource.HC, body);
			}
		}
	}

	/**
	 * 请求获取住院及用药情况数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadInhospitalAndMedicineData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthCheck = (String) body.get("healthCheck");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthCheckModel hcModel = new HealthCheckModel(dao);
			List<Map<String, Object>> inhospitalSituationData = hcModel
					.loadInhospitalSituationData(healthCheck);
			inhospitalSituationData = SchemaUtil.setDictionaryMessageForList(
					inhospitalSituationData, HC_InhospitalSituation);
			List<Map<String, Object>> medicineSituation = hcModel
					.loadMedicineSituationData(healthCheck);
			medicineSituation = SchemaUtil.setDictionaryMessageForList(
					medicineSituation, HC_MedicineSituation);
			if (inhospitalSituationData != null
					&& inhospitalSituationData.size() > 0) {
				resBody.put(HC_InhospitalSituation + Constants.DATAFORMAT4LIST,
						inhospitalSituationData);
			}
			if (medicineSituation != null && medicineSituation.size() > 0) {
				resBody.put(HC_MedicineSituation + Constants.DATAFORMAT4LIST,
						medicineSituation);
			}
		} catch (ModelDataOperationException e) {
			logger.error("loading LoadInhospitalAndMedicineData is fail");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doSaveWProgestationaskrecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try{
			body = hcModel.saveWProgestationask(op, body, true);
			body = SchemaUtil.setDictionaryMessageForList(body,
					hc_w_progestationask);
			vLogService.saveVindicateLog(hc_w_progestationask, op,
					(String) body.get("phrId"), dao);
		} catch (ModelDataOperationException e) {
			logger.error("保存妻子孕前检查询问失败！");
			throw new ServiceException(e);
		}
	}
	@SuppressWarnings("unchecked")
	protected void doSaveWProgestationCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try{
			String recordId="";
			if(op.equals("update")){
				recordId=body.get("recordId").toString();
			}
			body = hcModel.saveWProgestationCheck(op, body, false);
			vLogService.saveVindicateLog(hc_w_progestationcheck, op,
					(String) body.get("recordId"), dao);
			body.put("recordId", recordId);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("保存丈夫孕前检查信息失败！");
			throw new ServiceException(e);
		}
	}
	protected void doSaveMProgestationaskrecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try{
			body = hcModel.saveMProgestationask(op, body, true);
			body = SchemaUtil.setDictionaryMessageForList(body,
					hc_m_progestationask);
			vLogService.saveVindicateLog(hc_m_progestationask, op,
					(String) body.get("phrId"), dao);
		} catch (ModelDataOperationException e) {
			logger.error("保存丈夫孕前检查询问失败！");
			throw new ServiceException(e);
		}
	}
	@SuppressWarnings("unchecked")
	protected void doSaveMProgestationCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try{
			String recordId="";
			if(op.equals("update")){
				recordId=body.get("recordId").toString();
			}
			body = hcModel.saveMProgestationCheck(op, body, false);
			vLogService.saveVindicateLog(hc_m_progestationcheck, op,
					(String) body.get("recordId"), dao);
			body.put("recordId", recordId);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("保存妻子孕前检查信息失败！");
			throw new ServiceException(e);
		}
	}
	/**
	 * 加载生活方式数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLifestySituationData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthCheck = (String) body.get("healthCheck");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthCheckModel hcModel = new HealthCheckModel(dao);
			Map<String, Object> record = hcModel
					.loadLifestySituationData(healthCheck);
			record = SchemaUtil.setDictionaryMessageForForm(record,
					HC_LifestySituation);
			if (record != null) {
				resBody.putAll(record);
			}
		} catch (ModelDataOperationException e) {
			logger.error("loading LifestySituationData is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 加载查体数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadExaminationData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthCheck = (String) body.get("healthCheck");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthCheckModel hcModel = new HealthCheckModel(dao);
			Map<String, Object> record = hcModel
					.loadExaminationData(healthCheck);
			record = SchemaUtil.setDictionaryMessageForForm(record,
					HC_Examination);
			if (record != null) {
				resBody.putAll(record);
			}
		} catch (ModelDataOperationException e) {
			logger.error("loading ExaminationData is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 加载健康评价数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadHealthAssessmentData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthCheck = (String) body.get("healthCheck");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthCheckModel hcModel = new HealthCheckModel(dao);
			Map<String, Object> record = hcModel
					.loadHealthAssessmentData(healthCheck);
			record = SchemaUtil.setDictionaryMessageForForm(record,
					HC_HealthAssessment);
			if (record != null) {
				resBody.putAll(record);
			}
		} catch (ModelDataOperationException e) {
			logger.error("loading HealthAssessmentData is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 加载辅助检查数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadAccessoryExaminationData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String healthCheck = (String) body.get("healthCheck");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthCheckModel hcModel = new HealthCheckModel(dao);
			Map<String, Object> record = hcModel
					.loadAccessoryExaminationData(healthCheck);
			record = SchemaUtil.setDictionaryMessageForForm(record,
					HC_AccessoryExamination);
			if (record != null) {
				resBody.putAll(record);
			}
		} catch (ModelDataOperationException e) {
			logger.error("loading AccessoryExaminationData is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存非免疫规划预防接种数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveNonimmuneInoculation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> key = new HashMap<String, Object>();
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try {
			key = hcModel.saveNonimmuneInoculation(body, op);
			if ("create".equals(op)) {
				body.put("recordId", key.get("recordId"));
			}
			body = SchemaUtil.setDictionaryMessageForList(body,
					HC_NonimmuneInoculation);
			vLogService.saveVindicateLog(HC_NonimmuneInoculation, op,
					(String) body.get("recordId"), dao);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("save NonimmuneInoculation message error!", e);
		}
	}

	/**
	 * 控制健康年度检查新建按钮权限
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadCreateControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (Map<String, Object>) req.get("body");
		Map<String, Boolean> update = ControlRunner.run(HC_HealthCheck, data,
				ctx, ControlRunner.CREATE, ControlRunner.UPDATE);
		res.put("body", update);
	}

	/**
	 * 根据healthCheck删除健康检查表数据以及相关表所有此healthCheck的数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doDeleteHealthCheckRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String healthCheck = (String) req.get("pkey");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		try {
			hcModel.deleteHealthCheckRecord(healthCheck);
			hcModel.deleteLifestySituationRecord(healthCheck);
			hcModel.deleteExaminationRecord(healthCheck);
			hcModel.deleteHealthAssessmentRecord(healthCheck);
			hcModel.deleteInhospitalSituationRecord(healthCheck);
			hcModel.deleteMedicineSituationRecord(healthCheck);
			hcModel.deleteAccessoryExaminationRecord(healthCheck);
			hcModel.deleteNonimmuneInoculationRecord(healthCheck);
		} catch (ModelDataOperationException e) {
			logger.error("delete HealthCheck record is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:保存健康检查数据(HTML版本)
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-7-14 下午2:40:22
	 * @throws PersistentDataOperationException 
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHealthCheckHtml(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> hcData = (Map<String, Object>) reqBodyMap
				.get("hcData");
		Map<String, Object> lsData = (Map<String, Object>) reqBodyMap
				.get("lsData");
		Map<String, Object> exaData = (Map<String, Object>) reqBodyMap
				.get("exaData");
		Map<String, Object> aeData = (Map<String, Object>) reqBodyMap
				.get("aeData");
		Map<String, Object> haData = (Map<String, Object>) reqBodyMap
				.get("haData");
		List<Map<String, Object>> inhospitalListData = (List<Map<String, Object>>) reqBodyMap
				.get("inhospitalListData");
		List<Map<String, Object>> medicineListData = (List<Map<String, Object>>) reqBodyMap
				.get("medicineListData");
		List<Map<String, Object>> niListData = (List<Map<String, Object>>) reqBodyMap
				.get("niListData");
		String op = (String) req.get("op");
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Date curDate = new Date();
		Map<String, Object> comAddMap = new HashMap<String, Object>();
		comAddMap.put("createUser", curUserId);
		comAddMap.put("createUnit", curUnitId);
		comAddMap.put("createDate", curDate);
		comAddMap.put("lastModifyUser", curUserId);
		comAddMap.put("lastModifyUnit", curUnitId);
		comAddMap.put("lastModifyDate", curDate);
		Map<String, Object> comUpdateMap = new HashMap<String, Object>();
		comUpdateMap.put("lastModifyUser", curUserId);
		comUpdateMap.put("lastModifyUnit", curUnitId);
		comUpdateMap.put("lastModifyDate", curDate);
		
		//传日志到大数据接口 （健康体检任务提醒、健康危险因素提醒）--wdl
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String curDate1= sdf1.format( new Date());
		
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"JKWXYSTX\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.JKWXYSTX\",\n"+
			"\"methodDesc\":\"void doSaveHealthCheckHtml()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				String json1="{ \n"+
						"\"orgCode\":\""+curUnitId+"\",\n"+
						"\"orgName\":\""+organname+"\",\n"+
						"\"ip\":\""+ipc+"\",\n"+
						"\"opertime\":\""+curDate1+"\",\n"+
						"\"operatorCode\":\""+curUserId+"\",\n"+
						"\"operatorName\":\""+USER_NAME+"\",\n"+
						"\"callType\":\"02\",\n"+
						"\"apiCode\":\"JKTJRWTX\",\n"+
						"\"operSystemCode\":\"ehr\",\n"+
						"\"operSystemName\":\"健康档案系统\",\n"+
						"\"fromDomain\":\"ehr_yy\",\n"+
						"\"toDomain\":\"ehr_mb\",\n"+
						"\"clientAddress\":\""+ipc+"\",\n"+
						"\"serviceBean\":\"esb.JKTJRWTX\",\n"+
						"\"methodDesc\":\"void doSaveHealthCheck()\",\n"+
						"\"statEnd\":\""+curDate1+"\",\n"+
						"\"stat\":\"1\",\n"+
						"\"avgTimeCost\":\""+num+"\",\n"+
						"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
						"\"response\":\"200\"\n"+
					          "}";
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
            PublicService.httpURLPOSTCase(json1);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		// 基本信息
		Map<String, Object> hcMap = null;
		if ("create".equals(op)) {
			hcData.putAll(comAddMap);
		} else {
			hcData.putAll(comUpdateMap);
		}
		try {
			hcMap = hcModel.saveAnnualHealthCheck(op, hcData);
			hcData.putAll(hcMap);
		} catch (ModelDataOperationException e1) {
			logger.error("Save HC_HealthCheck data failure.", e1);
			throw new ServiceException(e1);
		}
		String healthCheck = (String) hcData.get("healthCheck");
		if ("create".equals(op)) {
			healthCheck = (String) hcMap.get("healthCheck");
		}
		// 高血压高危需求
		boolean blnConstriction = !BSCHISUtil.isBlank(hcData
				.get("constriction"));
		boolean blnDiastolic = !BSCHISUtil.isBlank(hcData.get("diastolic"));
		boolean blnConstriction_L = !BSCHISUtil.isBlank(hcData
				.get("constriction_L"));
		boolean blnDiastolic_L = !BSCHISUtil.isBlank(hcData.get("diastolic_L"));

		Integer constriction_L = null;
		Integer diastolic_L = null;
		if (hcData.get("constriction_L") instanceof String) {
			String cl = (String) hcData.get("constriction_L");
			if (S.isNotEmpty(cl)) {
				constriction_L = new Integer(Integer.parseInt(cl));
			}
		}
		if (hcData.get("constriction_L") instanceof Integer) {
			constriction_L = (Integer) hcData.get("constriction_L");
		}
		if (hcData.get("diastolic_L") instanceof String) {
			String dl = (String) hcData.get("diastolic_L");
			if (S.isNotEmpty(dl)) {
				diastolic_L = new Integer(Integer.parseInt(dl));
			}
		}
		if (hcData.get("diastolic_L") instanceof Integer) {
			diastolic_L = (Integer) hcData.get("diastolic_L");
		}
		Integer constriction = null;
		Integer diastolic = null;
		if (hcData.get("constriction") instanceof String) {
			String cl = (String) hcData.get("constriction");
			if (S.isNotEmpty(cl)) {
				constriction = new Integer(Integer.parseInt(cl));
			}
		}
		if (hcData.get("constriction") instanceof Integer) {
			constriction = (Integer) hcData.get("constriction");
		}
		if (hcData.get("diastolic") instanceof String) {
			String dl = (String) hcData.get("diastolic");
			if (S.isNotEmpty(dl)) {
				diastolic = new Integer(Integer.parseInt(dl));
			}
		}
		if (hcData.get("diastolic") instanceof Integer) {
			diastolic = (Integer) hcData.get("diastolic");
		}

		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && constriction.intValue() >= 120 && constriction
					.intValue() <= 139)
					|| (blnDiastolic && diastolic.intValue() >= 80 && diastolic
							.intValue() <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk((String) hcData.get("empiId"),
						(String) hcData.get("phrId"), constriction, diastolic,
						"3");
			}
		}
		if (blnConstriction_L || blnDiastolic_L) {
			if ((blnConstriction_L && constriction_L.intValue() >= 120 && constriction_L
					.intValue() <= 139)
					|| (blnDiastolic_L && diastolic_L.intValue() >= 80 && diastolic_L
							.intValue() <= 89)) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk((String) hcData.get("empiId"),
						(String) hcData.get("phrId"), constriction_L,
						diastolic_L, "3");
			}
		}
		// 高血压疑似记录
		if (blnConstriction || blnDiastolic) {
			if ((blnConstriction && constriction.intValue() >= 140)
					|| (blnDiastolic && diastolic.intValue() >= 90)) {
				HypertensionSimilarityModel hrm = new HypertensionSimilarityModel(
						dao);
				hrm.insertHypertensionSimilarity((String) hcData.get("empiId"),
						(String) hcData.get("phrId"), constriction, diastolic,
						hcData);
			}
		}
		if (blnConstriction_L || blnDiastolic_L) {
			if ((blnConstriction_L && constriction_L.intValue() >= 140)
					|| (blnDiastolic_L && diastolic_L >= 90)) {
				HypertensionSimilarityModel hrm = new HypertensionSimilarityModel(
						dao);
				hrm.insertHypertensionSimilarity((String) hcData.get("empiId"),
						(String) hcData.get("phrId"), constriction_L,
						diastolic_L, hcData);
			}
		}
		// 生活方式
		lsData.put("healthCheck", healthCheck);
		Map<String, Object> lsMap = null;
		try {
			lsMap = hcModel.loadLifestySituationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_LifestySituation by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		String lsop = "create";
		if (lsMap != null) {
			lsop = "update";
			lsData.put("lifestySituation", lsMap.get("lifestySituation"));
		}
		if ("create".equals(lsop)) {
			lsData.putAll(comAddMap);
		} else {
			lsData.putAll(comUpdateMap);
		}
		//溧阳获取老年人自理评估值替换页面本页面填的值
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(app.getProperty("aera").toString().equals("ly")){
		Calendar date = Calendar.getInstance(); 
		String empiId=hcData.get("empiId").toString();
		 Map<String, Object> parameters=new HashMap<String, Object>();
		 String findselfCare="select empiId as empiId ,ZPFS as ZPFS from MDC_OldPeopleSelfCare "+
		 		" where empiId=:empiId and to_char(createDate,'yyyy')=:year" +
		 		" order by createDate desc ";
		 parameters.put("empiId", empiId);
		 parameters.put("year", date.get(Calendar.YEAR)+"");
		 List<Map<String, Object>> ZPFSlist=dao.doQuery(findselfCare, parameters);
		if(ZPFSlist.size() >0){
			Integer ZPFS=BSCHISUtil.parseToInt(ZPFSlist.get(0).get("ZPFS").toString());
			String selfCare="";
			if(ZPFS >= 4 && ZPFS <= 8 ){
				selfCare="2";
			}else if(ZPFS >= 9 && ZPFS <= 18 ){
				selfCare="3";
			}else if(ZPFS >= 19 ){
				selfCare="4";
			}else{
				selfCare="1";
			}
			hcData.put("selfCare", selfCare);
		}
		
		}
		try {
			hcModel.saveLifestySituation(lsop, lsData, false);
		} catch (ModelDataOperationException e2) {
			logger.error("Save record of HC_LifestySituation failure.", e2);
			throw new ServiceException(e2);
		}
		// 查体
		exaData.put("healthCheck", healthCheck);
		Map<String, Object> exaMap = null;
		try {
			exaMap = hcModel.loadExaminationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_Examination by healthCheck[" + healthCheck
					+ "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		String exaop = "create";
		if (exaMap != null) {
			exaop = "update";
			exaData.put("examination", exaMap.get("examination"));
		}
		if ("create".equals(exaop)) {
			exaData.putAll(comAddMap);
		} else {
			exaData.putAll(comUpdateMap);
		}
		try {
			hcModel.saveExamination(exaop, exaData, false);
		} catch (ModelDataOperationException e2) {
			logger.error("Save record of HC_LifestySituation failure.", e2);
			throw new ServiceException(e2);
		}
		// 辅助检查
		aeData.put("healthCheck", healthCheck);
		Map<String, Object> aeMap = null;
		try {
			aeMap = hcModel.loadAccessoryExaminationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_AccessoryExamination by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		String aeop = "create";
		if (aeMap != null) {
			aeop = "update";
			aeData.put("recordId", aeMap.get("recordId"));
		}
		if ("create".equals(aeop)) {
			aeData.putAll(comAddMap);
		} else {
			aeData.putAll(comUpdateMap);
		}
		String sourceId=(String) aeData.get("recordId");
		try {
			Map<String, Object> aeResult = hcModel.saveAccessoryExaminationData(aeop, aeData, false);
			if("create".equals(aeop)){
				sourceId=(String) aeResult.get("recordId");
			}
		} catch (ModelDataOperationException e2) {
			logger.error("Save record of HC_AccessoryExamination failure.", e2);
			throw new ServiceException(e2);
		}
		// 空腹血糖6.1~＜7.0mmol/L新建糖血压高危核实
		if (!BSCHISUtil.isBlank(aeData.get("fbs"))) {
			double fbs = Double.valueOf((String) aeData.get("fbs"));
			if (fbs >= 6.1 && fbs < 7.0) {
				DiabetesOGTTModel drm = new DiabetesOGTTModel(dao);
				drm.insertDiabetesOGTTRecord((String) hcData.get("empiId"),
						(String) hcData.get("phrId"), fbs, null, hcData);
			}
			if (fbs >= 7) {
				DiabetesSimilarityModel dsm = new DiabetesSimilarityModel(dao);
				dsm.insertDiabetesSimilarity(sourceId,(String) hcData.get("empiId"),
						(String) hcData.get("phrId"), fbs, null,DiabetesRiskDataSource.HC, hcData);
			}
		}

		// 健康评价表
		haData.put("healthCheck", healthCheck);
		Map<String, Object> haMap = null;
		try {
			haMap = hcModel.loadHealthAssessmentData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_HealthAssessment by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		String haop = "create";
		if (haMap != null) {
			haop = "update";
			haData.put("assessmentId", haMap.get("assessmentId"));
		}
		if ("create".equals(haop)) {
			haData.putAll(comAddMap);
		} else {
			haData.putAll(comUpdateMap);
		}
		try {
			hcModel.saveHealthAssessment(haop, haData, false);
		} catch (ModelDataOperationException e2) {
			logger.error("Save record of HC_HealthAssessment failure.", e2);
			throw new ServiceException(e2);
		}
		// 保存住院数据 #医疗机构名称 如果为空该条数据不处理
		for (Map<String, Object> inhMap : inhospitalListData) {
			String medicalestablishmentName = (String) inhMap
					.get("medicalestablishmentName");
			if (StringUtils.isEmpty(medicalestablishmentName)) {
				continue;
			}
			String inhOp = op;
			inhMap.put("healthCheck", healthCheck);
			if ("create".equals(op)) {
				inhMap.put("createUser", curUserId);
				inhMap.put("createUnit", curUnitId);
				inhMap.put("createDate", curDate);
				inhMap.put("lastModifyUser", curUserId);
				inhMap.put("lastModifyUnit", curUnitId);
				inhMap.put("lastModifyDate", curDate);
				inhOp = "create";
			} else {
				String situationId = (String) inhMap.get("situationId");
				if (StringUtils.isEmpty(situationId)) {
					inhMap.put("createUser", curUserId);
					inhMap.put("createUnit", curUnitId);
					inhMap.put("createDate", curDate);
					inhMap.put("lastModifyUser", curUserId);
					inhMap.put("lastModifyUnit", curUnitId);
					inhMap.put("lastModifyDate", curDate);
					inhOp = "create";
				} else {
					inhMap.put("lastModifyUser", curUserId);
					inhMap.put("lastModifyUnit", curUnitId);
					inhMap.put("lastModifyDate", curDate);
					inhOp = "update";
				}
			}
			try {
				hcModel.saveInhospitalSituation(inhOp, inhMap);
			} catch (ModelDataOperationException e) {
				logger.error("Save HC_InhospitalSituation data failure.", e);
				throw new ServiceException(e);
			}
		}
		// 保存 用药情况数据 #药物名称 如果为空，该条数据不处理
		for (Map<String, Object> mMap : medicineListData) {
			String medicine = (String) mMap.get("medicine");
			if (StringUtils.isEmpty(medicine)|| mMap.get("medicineYield").toString().equals("3")) {
				if(!"create".equals(op)){
					if(!StringUtils.isEmpty(mMap.get("situationId").toString())){
					String deletemdc="delete from HC_MedicineSituation where situationId=:situationId ";
					Map<String, Object> p=new HashMap<String, Object>();
					p.put("situationId", mMap.get("situationId").toString());
					dao.doUpdate(deletemdc, p);
					}
				}
				continue;
			}
			String mOp = op;
			mMap.put("healthCheck", healthCheck);
			if ("create".equals(op)) {
				mMap.put("createUser", curUserId);
				mMap.put("createUnit", curUnitId);
				mMap.put("createDate", curDate);
				mMap.put("lastModifyUser", curUserId);
				mMap.put("lastModifyUnit", curUnitId);
				mMap.put("lastModifyDate", curDate);
				mOp = "create";
			} else {
				String situationId = (String) mMap.get("situationId");
				if (StringUtils.isEmpty(situationId)) {
					mMap.put("createUser", curUserId);
					mMap.put("createUnit", curUnitId);
					mMap.put("createDate", curDate);
					mMap.put("lastModifyUser", curUserId);
					mMap.put("lastModifyUnit", curUnitId);
					mMap.put("lastModifyDate", curDate);
					mOp = "create";
				} else {
					mMap.put("lastModifyUser", curUserId);
					mMap.put("lastModifyUnit", curUnitId);
					mMap.put("lastModifyDate", curDate);
					mOp = "update";
				}
			}
			try {
				hcModel.saveMedicineSituation(mOp, mMap);
			} catch (ModelDataOperationException e) {
				logger.error("Save HC_MedicineSituation data failure.", e);
				throw new ServiceException(e);
			}
		}
		// 保存 非免疫规划预防接种 数据 # 名称 如果为空，该条数据不处理
		for (Map<String, Object> niMap : niListData) {
			String name = (String) niMap.get("name");
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			String niOp = op;
			niMap.put("healthCheck", healthCheck);
			if ("create".equals(op)) {
				niMap.put("createUser", curUserId);
				niMap.put("createUnit", curUnitId);
				niMap.put("createDate", curDate);
				niMap.put("lastModifyUser", curUserId);
				niMap.put("lastModifyUnit", curUnitId);
				niMap.put("lastModifyDate", curDate);
				niOp = "create";
			} else {
				String recordId = (String) niMap.get("recordId");
				if (StringUtils.isEmpty(recordId)) {
					niMap.put("createUser", curUserId);
					niMap.put("createUnit", curUnitId);
					niMap.put("createDate", curDate);
					niMap.put("lastModifyUser", curUserId);
					niMap.put("lastModifyUnit", curUnitId);
					niMap.put("lastModifyDate", curDate);
					niOp = "create";
				} else {
					niMap.put("lastModifyUser", curUserId);
					niMap.put("lastModifyUnit", curUnitId);
					niMap.put("lastModifyDate", curDate);
					niOp = "update";
				}
			}
			try {
				hcModel.saveNonimmuneInoculation(niMap, niOp);
			} catch (ModelDataOperationException e) {
				logger.error("Save HC_NonimmuneInoculation data failure.", e);
				throw new ServiceException(e);
			}
		}
		
		if(app.getProperty("aera").toString().equals("ly")){
			if ("create".equals(op)) {
				String update=new StringBuffer("update EHR_HealthRecord set existHealthCheck='y'")
		                    .append("where empiid=:empiid").toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("empiid", hcData.get("empiId"));
				try {
				dao.doUpdate(update, parameters);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		String empiId1 = (String) hcData.get("empiId");
		//打签约项目完成标识
		if(!"".equals(reqBodyMap.get("taskCode"))&&reqBodyMap.get("taskCode")!=null){
			String taskCode = (String) reqBodyMap.get("taskCode");
			this.finishSCServiceTask(empiId1, taskCode,  dao);
		}
		this.finishSCServiceTask(empiId1, JKTJ_TJFW, null, ServiceAdaptItem.OLDMAN, dao);
		res.put("body", hcMap);
	}

	/**
	 * 
	 * @Description:获取HTML表单上需要的 住院（住院史2，家庭病床史2）、用药（6）、非免疫规划预防接种记录（3）
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-7-15 上午10:46:57
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetHMNIListOfHTML(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String healthCheck = (String) reqBodyMap.get("healthCheck");
		HealthCheckModel hcModel = new HealthCheckModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		// 基本信息
		Map<String, Object> hcMap = null;
		try {
			hcMap = hcModel.loadHealthCheckByPkey(healthCheck);
		} catch (ModelDataOperationException e) {
			logger.error("Select HC_HealthCheck by healthCheck[" + healthCheck
					+ "] and type[1]  failure.", e);
			throw new ServiceException(e);
		}
		resBody.put("hcData",
				SchemaUtil.setDictionaryMessageForForm(hcMap, HC_HealthCheck));
		// 生活方式
		Map<String, Object> lsMap = null;
		try {
			lsMap = hcModel.loadLifestySituationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_LifestySituation by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		resBody.put("lsData", SchemaUtil.setDictionaryMessageForForm(lsMap,
				HC_LifestySituation));
		// 查体
		Map<String, Object> exaMap = null;
		try {
			exaMap = hcModel.loadExaminationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_Examination by healthCheck[" + healthCheck
					+ "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		resBody.put("exaData",
				SchemaUtil.setDictionaryMessageForForm(exaMap, HC_Examination));
		// 辅助检查
		Map<String, Object> aeMap = null;
		try {
			aeMap = hcModel.loadAccessoryExaminationData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_AccessoryExamination by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		resBody.put("aeData", SchemaUtil.setDictionaryMessageForForm(aeMap,
				HC_AccessoryExamination));
		// 健康评价表
		Map<String, Object> haMap = null;
		try {
			haMap = hcModel.loadHealthAssessmentData(healthCheck);
		} catch (ModelDataOperationException e1) {
			logger.error("Select HC_HealthAssessment by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e1);
			throw new ServiceException(e1);
		}
		resBody.put("haData", SchemaUtil.setDictionaryMessageForForm(haMap,
				HC_HealthAssessment));
		// 取住院史
		List<Map<String, Object>> rsList = null;
		try {
			rsList = hcModel.loadInhospitalSituationData(healthCheck, "1");
		} catch (ModelDataOperationException e) {
			logger.error("Select HC_InhospitalSituation by healthCheck["
					+ healthCheck + "] and type[1]  failure.", e);
			throw new ServiceException(e);
		}
		if (rsList != null) {
			int rs = rsList.size();
			if (rs < 3) {
				resBody.put("ihList", rsList);
			} else {
				List<Map<String, Object>> ihList = new ArrayList<Map<String, Object>>();
				for (int idx = 0; idx < 2; idx++) {
					ihList.add(rsList.get(idx));
				}
				resBody.put("ihList", ihList);
			}
		}
		// 取 家庭病床史
		rsList = null;
		try {
			rsList = hcModel.loadInhospitalSituationData(healthCheck, "2");
		} catch (ModelDataOperationException e) {
			logger.error("Select HC_InhospitalSituation by healthCheck["
					+ healthCheck + "] and type[2]  failure.", e);
			throw new ServiceException(e);
		}
		if (rsList != null) {
			int rs = rsList.size();
			if (rs < 3) {
				resBody.put("hhList", rsList);
			} else {
				List<Map<String, Object>> hhList = new ArrayList<Map<String, Object>>();
				for (int idx = 0; idx < 2; idx++) {
					hhList.add(rsList.get(idx));
				}
				resBody.put("hhList", hhList);
			}
		}
		// 取 用药
		rsList = null;
		try {
			rsList = hcModel.loadMedicineSituationData(healthCheck);
		} catch (ModelDataOperationException e) {
			logger.error("Select HC_MedicineSituation by healthCheck["
					+ healthCheck + "] failure.", e);
			throw new ServiceException(e);
		}
		if (rsList != null) {
			// rsList = SchemaUtil.setDictionaryMessageForForm(rsList,
			// HC_MedicineSituation);
			int rs = rsList.size();
			if (rs < 7) {
				resBody.put("msList", rsList);
			} else {
				List<Map<String, Object>> msList = new ArrayList<Map<String, Object>>();
				for (int idx = 0; idx < 6; idx++) {
					msList.add(rsList.get(idx));
				}
				resBody.put("msList", msList);
			}
		}
		// 取 非免疫规划预防接种记录
		rsList = null;
		try {
			rsList = hcModel.loadNonimmuneInoculationData(healthCheck);
		} catch (ModelDataOperationException e) {
			logger.error("Select HC_NonimmuneInoculation by healthCheck["
					+ healthCheck + "] failure.", e);
			throw new ServiceException(e);
		}
		if (rsList != null) {
			int rs = rsList.size();
			if (rs < 3) {
				resBody.put("niList", rsList);
			} else {
				List<Map<String, Object>> niList = new ArrayList<Map<String, Object>>();
				for (int idx = 0; idx < 3; idx++) {
					niList.add(rsList.get(idx));
				}
				resBody.put("niList", niList);
			}
		}
		res.put("body", resBody);
	}
	public void doGetmdcflag(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = req.get("empiId")+"";
		String gxysql="select empiId as EMPIID from MDC_HypertensionRecord where empiId=:empiId";
		String tnbsql="select empiId as EMPIID from MDC_DiabetesRecord where empiId=:empiId";
		HashMap<String, Object> pa=new HashMap<String, Object>();
		pa.put("empiId", empiId);
		try {
			Map<String, Object> gxymap=dao.doSqlLoad(gxysql, pa);
			if(gxymap!=null && gxymap.size()>0){
				res.put("gxyflag", "y");
			}else{
				res.put("gxyflag", "n");
			}
			Map<String, Object> tnbmap=dao.doSqlLoad(tnbsql, pa);
			if(tnbmap!=null && tnbmap.size()>0){
				res.put("tnbflag", "y");
			}else{
				res.put("tnbflag", "n");
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void doLoadRepeatRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String,Object> body=(Map<String, Object>) req.get("body");
		String year=body.get("year")+"";
		String idcard=body.get("idcard")+"";
		idcard="%"+idcard+"%";
		String sql="select min(a.healthCheck) as healthCheck, b.empiid as empiId,b.idcard as ," +
				" b.personname,count(1)-1 as repeat " +
				" from hc_healthcheck a ,mpi_demographicinfo b "+
				" where a.empiid=b.empiid and to_char(a.checkdate,'yyyy')=:year"+
				" and b.idcard like:idcard " +
				" group by b.empiid,b.idcard,b.personname having count(1) >1";
		HashMap<String, Object> pa=new HashMap<String, Object>();
		pa.put("year", year);
		pa.put("idcard", idcard);
		try {
			List<Map<String, Object>> reslist=dao.doSqlQuery(sql, pa);
			res.put("body", reslist);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doListNeedCheckRecord(Map<String,Object> req,Map<String, Object> res,BaseDAO dao,Context ctx)
		throws ServiceException {
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> rm = null;
		String schemaId = (String) req.get("schema");
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String year = new Date().getYear() + "";
		if (req.containsKey("year")) {
			year = req.get("year") + "";
		}
		String checkType = "1";
		if (req.containsKey("checkType")) {
			checkType = (String) req.get("checkType");
		}
		HealthCheckModel hcm = new HealthCheckModel(dao);
		rm=hcm.queryNeedCheckList(schemaId,queryCnd,queryCndsType,sortInfo,pageSize,pageNo,curUnitId);
		List<Map<String,Object>> resBody=(List<Map<String, Object>>) rm.get("body");
		res.put("body",resBody);
		res.put("pageSize",rm.get("pageSize"));
		res.put("pageNo",rm.get("pageNo"));
		res.put("totalCount",rm.get("totalCount"));
	}
}
