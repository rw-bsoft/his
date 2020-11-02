/**
 * @(#)PsychosisRecordService.java Created on 2012-3-22 上午10:24:18
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
import chis.source.dic.PastHistoryCode;
import chis.source.phr.HealthRecordModel;
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
public class PsychosisRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PsychosisRecordService.class);

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
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doInitializePsyRecordForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("pkey");
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("empiId", empiId);
		resBody.put("phrId", phrId);
		// 根据empiId取健康档案中责任医生、管辖机构等
		HealthRecordModel hrm = new HealthRecordModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrm.getHealthRecordByEmpiId(empiId);
			hrMap = SchemaUtil.setDictionaryMessageForForm(hrMap,
					EHR_HealthRecord);
		} catch (ModelDataOperationException e) {
			logger.error("Get health record info failed.", e);
			throw new ServiceException(e);
		}
		if (hrMap != null) {
			resBody.put("manaUnitId", hrMap.get("manaUnitId"));
			resBody.put("manaDoctorId", hrMap.get("manaDoctorId"));
		}
		// if (phrId == null) {
		// }
		// 加载精神病档案信息
		PsychosisRecordModel psyrm = new PsychosisRecordModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = psyrm.getPsyRecordByPhrId(phrId);
			rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					PSY_PsychosisRecord);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis record by phrId failed.", e);
			throw new ServiceException(e);
		}
		String op = "";
		if (rsMap != null) {
			op = "update";
			resBody.putAll(rsMap);
		} else {
			op = "create";
			resBody.put("fillTableDate", Calendar.getInstance().getTime());
			resBody.put("doctorSign", UserUtil.get(UserUtil.USER_ID));
		}
		resBody.put("op", op);
		res.put("body", resBody);
	}

	/**
	 * 保存精神病档案及第一次随访信息--创建随访计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public void doSavePsychosisRecordAndFirstVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		// 保存精神病档案
		Map<String, Object> recordBody = (Map<String, Object>) req
				.get("recordData");
		String empiId = (String) recordBody.get("empiId");
		String phrId = (String) recordBody.get("phrId");
		String recordOp = (String) req.get("recordOp");
		String psychosisType = (String) req.get("psychosisType");
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);

		// 这个要放在保存之前查，要不覆盖这后查出这为当前类型
		String oldVisitType = "";
		try {
			oldVisitType = pvModel.getPsyFirstVisitType(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis old visit type failedl.", e);
			throw new ServiceException(e);
		}

		PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
		Map<String, Object> prMap = null;
		try {
			prMap = prModel.savePsyRecord(recordOp, recordBody, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save psychosis record failed.", e);
			throw new ServiceException(e);
		}
		prMap.put("phrId", phrId);
		resBody.put("psyRcordForm", prMap);
		vLogService.saveVindicateLog(PSY_PsychosisRecord, recordOp, phrId, dao,
				empiId);
		vLogService.saveRecords("JING", recordOp, dao,empiId);

		// 保存个人既往史()
		try {
			savePastHistory(empiId, recordBody, dao, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Save past history failed.", e);
			throw new ServiceException(e);
		}

		// 下面是对首次随访信息及首次随访用药信息处理
		Map<String, Object> visitBody = (Map<String, Object>) req
				.get("firstVisitData");
		if (null == visitBody) {
			if ("paper".equals(psychosisType)) {
				Map<String, Object> rMap = SchemaUtil.setDictionaryMessageForForm(
						recordBody, PSY_PsychosisRecord);
				resBody.put("recordHTMLData", rMap);
			}
			//重新计算权限
			Map<String, Boolean> conMap =  this.getPSYControl(recordBody,ctx);
			resBody.put("PSY_PsychosisRecord_control", conMap);
			return;
		}

		String visitOp = (String) req.get("visitOp");
		String visitType = (String) visitBody.get("visitType");

		if (StringUtils.isEmpty(visitType)) {// 重新计算随访类型
			String dangerousGrade = (String) visitBody.get("dangerousGrade");
			String insight = (String) visitBody.get("insight");
			String adverseReactions = (String) visitBody
					.get("adverseReactions");
			String social = (String) visitBody.get("social");
			String visitEffect = (String) visitBody.get("visitEffect");
			visitType = PsychosisVisitModel.getVisitType(dangerousGrade,
					insight, adverseReactions, social, visitEffect);
			visitBody.put("visitType", visitType);
		}

		// 保存首次随访记录
		String fvisitId = (String) visitBody.get("visitId");
		Map<String, Object> pvMap = null;
		try {
			if (visitBody.get("dangerousGrade") instanceof Integer) {
				visitBody.put("dangerousGrade", visitBody.get("dangerousGrade")
						.toString());
			}
			String visitUnit = (String) visitBody.get("visitUnit");
			if(S.isEmpty(visitUnit)){
				visitBody.put("visitUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			}
			pvMap = pvModel.savePsyVisitRecord(visitOp,
					PSY_PsychosisFirstVisit, visitBody, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save psychosis first visit failed.", e);
			throw new ServiceException(e);
		}
		if("create".equals(visitOp)){
			if(pvMap != null && pvMap.size() > 0){
				fvisitId = (String) pvMap.get("visitId");
			}
			//生成首次随访计划记录
			Map<String, Object> fvpMap = new HashMap<String, Object>();
			Date curDate = Calendar.getInstance().getTime();
			fvpMap.put("planDate", curDate);
			fvpMap.put("beginDate", curDate);
			fvpMap.put("endDate", curDate);
			fvpMap.put("planStatus", "1");
			fvpMap.put("visitDate", curDate);
			fvpMap.put("businessType", BusinessType.PSYCHOSIS);
			fvpMap.put("recordId", phrId);
			fvpMap.put("empiId", empiId);
			fvpMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			fvpMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
			fvpMap.put("lastModifyDate", curDate);
			fvpMap.put("visitId", fvisitId);
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			try {
				vpModel.saveVisitPlan("create", fvpMap);
			} catch (ModelDataOperationException e) {
				logger.error("Save firest visit plan record failure.", e);
				throw new ServiceException(e);
			}
		}
		
		// 判断是否需要重新生成随访计划
/*		boolean recreatePlan = false;
		try {
			recreatePlan = this.needRecreateVisitPlan(visitOp, visitType,
					oldVisitType, empiId, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Judge whether need to recreate visit plane "
					+ "failed.", e);
			throw new ServiceException(e);
		}

		// 重新生成随访计划
		if (recreatePlan) {
			String visitDate = (String) visitBody.get("visitDate");
			visitDate =visitDate.replace("T", " ").substring(0, 10);
			Date oldVisitDate = BSCHISUtil.toDate(visitDate);
			Calendar c = Calendar.getInstance();
			if (BSCHISUtil.dateCompare(c.getTime(), oldVisitDate) > 0) {
				visitDate = BSCHISUtil.toString(c.getTime());
			}
			Map<String, Object> genVisitPamMap = new HashMap<String, Object>();
			genVisitPamMap.put("recordId", phrId);
			genVisitPamMap.put("empiId", empiId);
			genVisitPamMap.put("visitType", visitType);
			genVisitPamMap.put("businessType", BusinessType.PSYCHOSIS);
			genVisitPamMap.put("planBeginDate", visitDate);
			genVisitPamMap.put("referral", visitBody.get("referral"));
			genVisitPamMap.put("taskDoctorId", recordBody.get("manaDoctorId"));
			// genVisitPamMap.put("isReferral", isReferral);
			genVisitPamMap.put("type", '0');
			String sn = (String) visitBody.get("sn");
			if (StringUtils.isNotEmpty(sn)) {
				genVisitPamMap.put("sn", sn);
			}
			try {
				visitPlanCreator.create(genVisitPamMap, ctx);
			} catch (Exception e) {
				logger.error("Generate psychosis visit plan failed.", e);
				throw new ServiceException(e);
			}
		}*/

		// 将随访ID放到res中
		String visitId = "";
		if ("create".equals(visitOp)) {
			if (pvMap != null) {
				visitId = (String) pvMap.get("visitId");
				// resBody.put("visitId", visitId);
			}
		} else {
			visitId = (String) visitBody.get("visitId");
			pvMap.put("visitId", visitId);
			// resBody.put("visitId", visitId);
		}
		resBody.put("firstVistForm", pvMap);

		// 保存首次随访用药
		boolean deleteMedicine = (Boolean) visitBody.get("deleteMedicine");
		if (deleteMedicine) {
			try {
				pvModel.deleteVisitMedicine(visitId);
			} catch (ModelDataOperationException e) {
				logger.error("Delete psychosisi visit medicine failed.", e);
				throw new ServiceException(e);
			}
		} else {
			List<Map<String, Object>> medicineList = (List<Map<String, Object>>) req
					.get("medicineList");
			if (null != medicineList && medicineList.size() > 0) {
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
		if ("paper".equals(psychosisType)) {
			Map<String, Object> rMap = SchemaUtil.setDictionaryMessageForForm(
					recordBody, PSY_PsychosisRecord);
			resBody.put("recordHTMLData", rMap);
			visitBody.put("visitId", visitId);
			Map<String, Object> fvDataMap = new HashMap<String, Object>();
			Map<String, Object> fvMap = SchemaUtil.setDictionaryMessageForForm(
					visitBody, PSY_PsychosisFirstVisit);
			fvDataMap.put(PSY_PsychosisFirstVisit + Constants.DATAFORMAT4FORM, fvMap);
			// 取首次随访用药
			List<Map<String, Object>> pvmList = null;
			try {
				pvmList = pvModel.getPsyVisitMedicineList(visitId, "recordId");
			} catch (ModelDataOperationException e) {
				logger.error("Get psychosis first visit medicine failed", e);
				throw new ServiceException(e);
			}
			fvDataMap.put(PSY_PsychosisVisitMedicine + Constants.DATAFORMAT4LIST,
					pvmList);
			resBody.put("fvHTMLData", fvDataMap);
			//重新计算权限
			Map<String, Boolean> conMap =  this.getPSYControl(recordBody,ctx);
			resBody.put(PSY_PsychosisRecord+"_control", conMap);
		}
	}

	private Map<String, Boolean> getPSYControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(PSY_PsychosisRecord, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check PSY_PsychosisRecord control error.", e);
			throw e;
		}
		return data;
	}
	
	/**
	 * 保存个人既往史
	 * 
	 * @param empiId
	 * @param body
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void savePastHistory(String empiId, Map<String, Object> body,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ServiceException {
		Map<String, Object> phMap = new HashMap<String, Object>();
		phMap.put("empiId", empiId);
		phMap.put("pastHisTypeCode", PastHistoryCode.SCREEN);
		phMap.put("methodsCode", "3");
		phMap.put("diseaseCode", PastHistoryCode.PASTHIS_PSYCHOSIS_CODE);
		phMap.put("diseaseText", "重性精神疾病");
		phMap.put("startDate", body.get("diseasedTime"));
		phMap.put("confirmDate", body.get("recentDiagnoseTime"));
		phMap.put("recordUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		phMap.put("recordUser", UserUtil.get(UserUtil.USER_ID));
		phMap.put("recordDate", new Date());
		phMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		phMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		phMap.put("lastModifyDate", new Date());
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> phRecordMap = hrModel.getPastHistory(empiId,
				PastHistoryCode.SCREEN, PastHistoryCode.PASTHIS_PSYCHOSIS_CODE);
		String phOp = "update";
		if (phRecordMap == null) {
			phOp = "create";
		} else {
			phMap.put("pastHistoryId", phRecordMap.get("pastHistoryId"));
		}
		hrModel.savePastHistory(phOp, phMap, vLogService);
	}

	/**
	 * 判断是否需要重新生成随访计划
	 * 
	 * @param visitType
	 *            随访类型
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	private boolean needRecreateVisitPlan(String op, String visitType,
			String oldVisitType, String empiId, BaseDAO dao)
			throws ModelDataOperationException {
/*		if ("create".equals(op)) {
			return true;
		}*/
		// 新的随访分类与原来的相同，不需要重新生成计划
		if (visitType.equals(oldVisitType)) {
			return false;
		}
		// 已经做过随访，不再重新生成计划
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Long numCountLong = pvModel.getPsyVisitRecordNumberByEmpiId(empiId);
		if (numCountLong.longValue() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 精神病档案注销－子档注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutFormPsychosisRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		String deadReason = (String) reqBodyMap.get("deadReason");
		// 修改档案记录状态为--注销
		PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
		try {
			prModel.logoutPsychosisRecord(phrId, cancellationReason, deadReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout psychosis record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(PSY_PsychosisRecord, "3", phrId, dao,
				empiId);
		vLogService.saveRecords("JING", "logout", dao,empiId);

		// 注销随访计划
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			vpModel.logOutVisitPlan(vpModel.RECORDID, phrId,
					BusinessType.PSYCHOSIS);
		} catch (ModelDataOperationException e) {
			logger.error("Logout psychosis visit plan failed.", e);
			throw new ServiceException(e);
		}
		// 如果是“死亡”，则在健康档案中打上死亡标识
		if (null != cancellationReason
				&& cancellationReason.equals(CancellationReason.PASS_AWAY)) {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			// String deadFlag = (String) reqBodyMap.get("deadFlag");
			// String deadReason = (String) reqBodyMap.get("deadReason");
			String strDeadDate = (String) reqBodyMap.get("deadDate");
			Date deadDate = BSCHISUtil.toDate(strDeadDate);
			try {
				hrModel.setDeadFlag(CancellationReason.PASS_AWAY, deadReason,
						deadDate, phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Update health record deadFlag failed.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 取消注销--恢复档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRevertPsychosisRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String phrId = (String) reqBodyMap.get("phrId");
		// 恢复档案记录状态
		PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
		try {
			prModel.revertPsychosisRecord(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Revert psychosis record failed.", e);
			throw new ServiceException(e);
		}

		// 恢复随访计划
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		int updateRecordNum = 0;
		try {
			updateRecordNum = vpModel.revertPsychosisVisitPlan(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Revert psychosis visit plan failed.", e);
			throw new ServiceException(e);
		}

		// 如果没有可恢复的计划则要去生成随访计划
		if (updateRecordNum <= 0) {
			PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = pvModel.getPsyVisitInfoList(phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Get some psychosis visit info failed.", e);
				throw new ServiceException(e);
			}
			if (null != rsList && rsList.size() > 0) {
				Map<String, Object> viMap = rsList.get(0);

				Map<String, Object> genVisitPamMap = new HashMap<String, Object>();
				genVisitPamMap.put("recordId", viMap.get("recordId"));
				genVisitPamMap.put("visitType", viMap.get("visitType"));
				genVisitPamMap.put("businessType", "" + BusinessType.PSYCHOSIS);
				genVisitPamMap.put("empiId", viMap.get("empiId"));
				genVisitPamMap.put("visitDate", viMap.get("visitDate"));
				genVisitPamMap.put("type", viMap.get("type"));
				genVisitPamMap.put("isReferral", viMap.get("isReferral"));
				genVisitPamMap.put("sn", viMap.get("sn"));

				try {
					visitPlanCreator.create(genVisitPamMap, ctx);
				} catch (Exception e) {
					logger.error("Generate psychosis visit plan failed.", e);
					throw new ServiceException(e);
				}
			}
		}
		// 如果是死亡，则要去除健康档案里的死亡标识
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		if (null != cancellationReason
				&& cancellationReason.equals(CancellationReason.PASS_AWAY)) {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			String deadFlag = (String) reqBodyMap.get("deadFlag");
			String deadReason = (String) reqBodyMap.get("deadReason");
			String strDeadDate = (String) reqBodyMap.get("deadDate");
			Date deadDate = BSCHISUtil.toDate(strDeadDate);
			try {
				hrModel.setDeadFlag(deadFlag, deadReason, deadDate, phrId);
			} catch (ModelDataOperationException e) {
				logger.error("Update health record deadFlag failed.", e);
				throw new ServiceException(e);
			}
		}
	}
}
