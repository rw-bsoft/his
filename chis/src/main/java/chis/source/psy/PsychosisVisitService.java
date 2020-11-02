/**
 * @(#)PsychosisRecordModel.java Created on 2012-3-27 下午14:23:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.psy;

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
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsychosisVisitService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PsychosisVisitService.class);

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

	/**
	 * 获取首次随访模块初始化数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInitalizeFirstVisitModule(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		long pvrNum = 0;// 用phrId取到的首次随访记录数
		long pvpNum = 0;// 随访计划数据，判断是否已有随访
		resBody.put("pvrNum", pvrNum);
		resBody.put("pvpNum", pvpNum);
		// 取首次随访记录
		List<Map<String, Object>> pvrList = null;
		try {
			pvrList = pvModel.getPsyFirstVisitRecordByPhrId(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis first visit record failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> pvrMap = new HashMap<String, Object>();
		if (pvrList == null || pvrList.size() <= 0) {
			// 新建档
			Map<String, Object> initMap = new HashMap<String, Object>();
			Date curDate = Calendar.getInstance().getTime();
			initMap.put("visitDate", curDate);
			initMap.put("dangerousGrade", "0");
			initMap.put("visitType", "3");
			initMap.put("nextDate", curDate);
			initMap.put("visitDoctor", UserUtil.get(UserUtil.USER_ID));
			initMap.put("visitUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			resBody.put(PSY_PsychosisFirstVisit + Constants.DATAFORMAT4FORM,
					SchemaUtil.setDictionaryMessageForForm(initMap,
							PSY_PsychosisFirstVisit));
			resBody.put(PSY_PsychosisVisitMedicine + Constants.DATAFORMAT4LIST,
					"");
			return;
		}

		pvrMap = SchemaUtil.getDataListToForm(pvrList.get(0));
		resBody.put(PSY_PsychosisFirstVisit + Constants.DATAFORMAT4FORM, pvrMap);
		resBody.put("pvrNum", pvrList.size());
		String visitId = (String) pvrMap.get("visitId");
		Map<String, Object> medicine = (Map<String, Object>) pvrMap
				.get("medicine");
		resBody.put("medicine", medicine.get("key"));
		// 取首次随访用药
		List<Map<String, Object>> pvmList = null;
		try {
			pvmList = pvModel.getPsyVisitMedicineList(visitId, "recordId");
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis first visit medicine failed", e);
			throw new ServiceException(e);
		}
		resBody.put(PSY_PsychosisVisitMedicine + Constants.DATAFORMAT4LIST,
				pvmList);
		// 随访计划记录数
		try {
			pvpNum = pvModel.getPsyVisitPlanNumber(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis visit plan number failed.", e);
			throw new ServiceException(e);
		}
		resBody.put("pvpNum", pvpNum);
	}

	// ------------------- 取获取计划列表 BEGEN --------------------------------
	/**
	 * 获取上年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetPreYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.PSYCHOSIS;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of Previous year.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取精神病上年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取本年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetCurYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = 0;
		String instanceType = BusinessType.PSYCHOSIS;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取精神病本年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取下年度的随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetNextYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.PSYCHOSIS;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取精神病下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	// ------------------- 取获取计划列表 END --------------------------------
	/**
	 * 
	 * @Description:HTML普通随访表单数据加载
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-12-25 上午10:13:31
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLoadHtmlVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String visitId = (String) reqBodyMap.get("visitId");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (S.isEmpty(visitId)) {
			// 新建档
			Map<String, Object> initMap = new HashMap<String, Object>();
			Date curDate = Calendar.getInstance().getTime();
			initMap.put("visitDate", curDate);
			initMap.put("dangerousGrade", "0");
			initMap.put("visitType", "3");
			initMap.put("nextDate", curDate);
			initMap.put("visitDoctor", UserUtil.get(UserUtil.USER_ID));
			initMap.put("visitUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			resBodyMap.put(PSY_PsychosisVisit + Constants.DATAFORMAT4FORM,
					SchemaUtil.setDictionaryMessageForForm(initMap,
							PSY_PsychosisVisit));
			resBodyMap.put(PSY_PsychosisVisitMedicine
					+ Constants.DATAFORMAT4LIST, "");
			res.put("body", resBodyMap);
			return;
		}

		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Map<String, Object> vrMap = null;
		try {
			vrMap = pvModel.getPsyVisitByPkey(visitId);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis visit record by visitId failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> pvrMap = new HashMap<String, Object>();
		pvrMap = SchemaUtil.setDictionaryMessageForForm(vrMap,
				PSY_PsychosisVisit);
		resBodyMap.put(PSY_PsychosisVisit + Constants.DATAFORMAT4FORM, pvrMap);
		// 获取服药情况记录
		List<Map<String, Object>> pvmList = null;
		try {
			pvmList = pvModel.getPsyVisitMedicineList(visitId, "recordId");
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis visit medicine failed", e);
			throw new ServiceException(e);
		}
		resBodyMap.put(PSY_PsychosisVisitMedicine + Constants.DATAFORMAT4LIST,
				pvmList);
		res.put("body", resBodyMap);
	}

	/**
	 * 保存普通随访记录及用药情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePsychosisVisitAndMedicine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		// 保存随访记录
		Map<String, Object> visitData = (Map<String, Object>) reqBody
				.get("visitData");
		String visitOp = (String) req.get("visitOp");
		String type = (String) visitData.get("type");
		if(S.isEmpty(type)){
			visitData.put("type", '1');
		}
		String visitType = (String) visitData.get("visitType");
		if (StringUtils.isEmpty(visitType)) {// 重新计算随访类型
			String dangerousGrade = (String) visitData.get("dangerousGrade");
			String insight = (String) visitData.get("insight");
			String adverseReactions = (String) visitData
					.get("adverseReactions");
			String social = (String) visitData.get("social");
			String visitEffect = (String) visitData.get("visitEffect");
			visitType = PsychosisVisitModel.getVisitType(dangerousGrade,
					insight, adverseReactions, social, visitEffect);
			visitData.put("visitType", visitType);
		}
		/*
		 * 新标准--随访类型中加--0 （未访到） 如果 本次随访类型为未访到 ，则根据前一次的随访间隔生成计划 故 如果 visitType=0
		 * 则给 visitType 赋值为上次随访时 visitType的值
		 */
		visitType = (String) visitData.get("visitType");
		if ("0".equals(visitType)) {
			// 取前一次随访visitType
			try {
				visitType = pvModel.getLastVisitType((String) visitData
						.get("phrId"));
			} catch (ModelDataOperationException e) {
				logger.error("Get pervious visitType failed.", e);
				throw new ServiceException(e);
			}
		}
		// 这个要放在保存之前查，要不覆盖这后查出这为当前类型
		String oldVisitType = "";
		Date oldVisitDate = null;
		String visitId = (String) visitData.get("visitId");
		if (StringUtils.isNotEmpty("visitId")) {
			Map<String, Object> rsMap = null;
			try {
				rsMap = pvModel.getPsyVisitByPkey(visitId);
			} catch (ModelDataOperationException e) {
				logger.error("Get psychosis old visit type failedl.", e);
				throw new ServiceException(e);
			}
			if (rsMap != null) {
				oldVisitType = (String) rsMap.get("visitType");
				oldVisitDate = (Date) rsMap.get("visitDate");
			}
		}
		Map<String, Object> visitRecord = null;
		if (visitData.get("dangerousGrade") instanceof Integer) {
			visitData.put("dangerousGrade", visitData.get("dangerousGrade")
					.toString());
		}
		String visitEffect = (String) visitData.get("visitEffect");
		try {
			boolean validate = true;
			if (!VisitEffect.CONTINUE.equals(visitEffect)) {
				validate = false;
			}
			String visitUnit = (String) visitData.get("visitUnit");
			if(S.isEmpty(visitUnit)){
				visitData.put("visitUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			}
			visitRecord = pvModel.savePsyVisitRecord(visitOp,
					PSY_PsychosisVisit, visitData, validate);
		} catch (ModelDataOperationException e) {
			logger.error("Save psychosis visit failed.", e);
			throw new ServiceException(e);
		}
		resBody.put("visitData", visitRecord);
		if (visitOp.equals("create")) {
			visitId = (String) visitRecord.get("visitId");
		} else {
			visitId = (String) visitData.get("visitId");
		}
		String empiId = (String) visitData.get("empiId");
		vLogService.saveVindicateLog(PSY_PsychosisVisit, visitOp, visitId, dao,
				empiId);
		//标识签约任务完成
		Date visitDate = new Date();
		if (visitData.get("visitDate") instanceof String) {
			visitDate = BSCHISUtil.toDate((String) visitData.get("visitDate"));
		} else {
			visitDate = (Date) visitData.get("visitDate");
		}
		int month = visitDate.getMonth();
		this.finishSCServiceTask(empiId, JSBSF_JSBFW, month + "", dao);
		// 保存用药情况
		List<Map<String, Object>> medicineList = (List<Map<String, Object>>) reqBody
				.get("medicineList");
		if (null != medicineList && medicineList.size() > 0) {
			boolean deleteMedicine = (Boolean) reqBody.get("deleteMedicine");
			if (deleteMedicine) {// 删除用药--不服药 或是 对用药作了删除操作
				try {
					pvModel.deleteVisitMedicine(visitId);
				} catch (ModelDataOperationException e) {
					logger.error(
							"delete psychosis visit medicine recordes failed.",
							e);
					throw new ServiceException(e);
				}
			} else {
				// 保存用药
				String phrId = (String) visitData.get("phrId");
				try {
					pvModel.saveBatchPsyVisitMedicine(medicineList, phrId,
							visitId);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Save psychosis first visit use medicine failed.",
							e);
					throw new ServiceException(e);
				}
			}
		}

		// 更新随访计划
		Map<String, Object> vpBody = new HashMap<String, Object>();
		Date planDate = BSCHISUtil.toDate((String) reqBody.get("planDate"));
		vpBody.put("empiId", empiId);
		vpBody.put("visitDate",
				BSCHISUtil.toDate((String) visitData.get("visitDate")));
		vpBody.put("planId", reqBody.get("planId"));
		vpBody.put("planDate", planDate);
		vpBody.put("visitId", visitId);
		if (VisitEffect.END.equals(visitEffect)) {
			vpBody.put("planStatus", PlanStatus.WRITEOFF);
		} else {
			vpBody.put("planStatus", PlanStatus.VISITED);
		}
		if (VisitEffect.LOST.equals(visitEffect)) {
			vpBody.put("planStatus", PlanStatus.LOST);
		} else {
			vpBody.put("planStatus", PlanStatus.VISITED);
		}
		vpBody.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		vpBody.put("lastModifyDate", new Date());
		VisitPlanModel pvm = new VisitPlanModel(dao);
		try {
			pvm.saveVisitPlan("update", vpBody);
		} catch (ModelDataOperationException e) {
			logger.error("Update visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 将以前未做的随访计划改为失败
		try {
			pvm.updateVisitPlanLostVisit(empiId, BusinessType.PSYCHOSIS,
					planDate);
		} catch (ModelDataOperationException e) {
			logger.error("Update visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 新标准--加转归-如果 转归为 9 终止管理 ，则注销精神病档案不再生成随访计划
		if (VisitEffect.END.equals(visitEffect)) {
			// 注销精神病档案
			String phrId = (String) visitData.get("phrId");
			// 修改档案记录状态为--注销
			PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
			try {
				prModel.logoutPsychosisRecord(phrId, CancellationReason.OTHER,
						null);
			} catch (ModelDataOperationException e) {
				logger.error("Logout psychosis record failed.", e);
				throw new ServiceException(e);
			}
			// 注销随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			try {
				vpModel.logOutVisitPlan(vpModel.RECORDID, phrId,
						BusinessType.PSYCHOSIS);
			} catch (ModelDataOperationException e) {
				logger.error("Logout psychosis visit plan failed.", e);
				throw new ServiceException(e);
			}

			return;
		}
		// 判断是否要生成随访
		boolean needToGenVisitPlan = false;
		try {
			needToGenVisitPlan = this.needRecreateVisitPlan(visitType,
					oldVisitType, visitOp, empiId, oldVisitDate, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Judge is has visit records", e);
			throw new ServiceException(e);
		}
		// 创建随访计划
		if (needToGenVisitPlan) {
			Map<String, Object> genVisitMap = new HashMap<String, Object>();
			genVisitMap.put("recordId", visitData.get("phrId"));
			genVisitMap.put("empiId", visitData.get("empiId"));
			genVisitMap.put("businessType", BusinessType.PSYCHOSIS);
			genVisitMap.put("planBeginDate", reqBody.get("planDate"));// visitData.get("visitDate")
			genVisitMap.put("referral", visitData.get("referral"));
			// genVisitMap.put("isReferral", visitData.get("isReferral"));
			genVisitMap.put("type", visitData.get("type"));
			genVisitMap.put("visitType", visitType);
			PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
			String phrId = (String) visitData.get("phrId");
			Map<String, Object> prMap = null;
			try {
				prMap = prModel.getPsyRecordByPhrId(phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Get Pyschosis record failedl.", e);
				throw new ServiceException(e);
			}
			if (prMap != null) {
				genVisitMap.put("taskDoctorId", prMap.get("taskDoctorId"));
			} else {
				genVisitMap.put("taskDoctorId", UserUtil.get(UserUtil.USER_ID));
			}
			int sn = (Integer) reqBody.get("sn");
			genVisitMap.put("sn", sn);
			try {
				visitPlanCreator.create(genVisitMap, ctx);
			} catch (Exception e) {
				logger.error("Generate psychosis visit plan failed.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 判断是否要生成随访计划
	 * 
	 * @param visitType
	 * @param oldVisitType
	 * @param op
	 * @param empiId
	 * @param oldVisitDate
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	private boolean needRecreateVisitPlan(String visitType,
			String oldVisitType, String op, String empiId, Date oldVisitDate,
			BaseDAO dao) throws ModelDataOperationException {
		if ("create".equals(op)) {
			return true;
		}
		if (visitType.equals(oldVisitType)) {
			return false;
		}
		if (oldVisitDate != null && "update".equals(op)) {
			PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
			long recordNum = pvModel.isHasVisitRecords(empiId, oldVisitDate);
			if (recordNum > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取随访权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void getPsyVisitControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(PSY_PsychosisVisit, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check PSY_AnnualAssessment record control error .", e);
			throw e;
		}
		resBodyMap.put("_actions", data);
		res.put("body", resBodyMap);
	}

	/**
	 * 获取重症精神病健康指导信息
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-27 下午6:22:18
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetPsyHealthGuidance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String wayId = (String) reqBodyMap.get("wayId");
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = pvModel.getHealthGuidance(wayId);
		} catch (ModelDataOperationException e) {
			logger.error("Get health guidance of psychosis visit Failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:保存重症精神病健康指导信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-27 下午6:48:14
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSavePsyHealthGuidance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = pvModel.saveHealthGuidance(op, reqBodyMap, vLogService);
		} catch (ModelDataOperationException e) {
			logger.error("Save to Health guidance of psychosis viist failed.",
					e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}
	
	/**
	 * 
	 * @Description:分页查询精神病随访计划-列表展示
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-6-4 上午10:40:18
	 * @Modify:
	 */
	public void doListPsychosisVistPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PsychosisVisitModel drm = new PsychosisVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = drm.listPsychosisVistPlan(req);
		} catch (ModelDataOperationException e) {
			logger.error("list Psychosis Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
}
