/**
 * @(#)RevertRecordService.java Created on 2012-3-20 上午10:30:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.phr;

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
import chis.source.cdh.ChildrenHealthModel;
import chis.source.cdh.DebilityChildrenModel;
import chis.source.dc.RabiesRecordModel;
import chis.source.dea.DeathReportCardModel;
import chis.source.def.DefModel;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.RevertOrLogout;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.dic.VisitType;
import chis.source.empi.EmpiModel;
import chis.source.idr.IdrReportModel;
import chis.source.log.VindicateLogService;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.DiabetesVisitModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionVisitModel;
import chis.source.mhc.PregnantRecordModel;
import chis.source.ohr.OldPeopleRecordModel;
import chis.source.ohr.OldPeopleVisitModel;
import chis.source.psy.PsychosisRecordModel;
import chis.source.psy.PsychosisVisitModel;
import chis.source.rvc.RetiredVeteranCadresModel;
import chis.source.sch.SchistospmaModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.tr.TumourConfirmedModel;
import chis.source.tr.TumourHighRiskModel;
import chis.source.tr.TumourPatientReportCardModel;
import chis.source.tr.TumourQuestionnaireModel;
import chis.source.tr.TumourScreeningModel;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class RevertRecordService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(RevertRecordService.class);
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	private VisitPlanCreator visitPlanCreator;

	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 加载被注销档案记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadAllLogoutedRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = StringUtils.trimToEmpty((String) body.get("empiId"));
		int pageSize = (Integer) body.get("pageSize");
		int pageNo = (Integer) body.get("pageNo");
		try {
			RevertRecordModel rrModel = new RevertRecordModel(dao);
			List<Map<String, Object>> records = rrModel.loadAllLogoutedRecords(
					empiId, pageSize, pageNo);
			res.put("body", records);
		} catch (ModelDataOperationException e) {
			logger.error("error query revertedRecords.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 档案恢复服务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ControllerException
	 */
	@SuppressWarnings("unchecked")
	protected void revertRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx,
			VindicateLogService vLogService) throws ServiceException,
			ExpException, PersistentDataOperationException, ControllerException {
		this.vLogService = vLogService;
		this.doRevertRecords(req, res, dao, ctx);
	}

	/**
	 * 档案恢复服务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ControllerException
	 */
	@SuppressWarnings("unchecked")
	protected void doRevertRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ExpException,
			PersistentDataOperationException, ControllerException {
		String revertFlag = (String) req.get("revertFlag");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String type = (String) body.get("type");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");

		VisitPlanModel vpModel = new VisitPlanModel(dao);

		try {
			if (RevertOrLogout.HEALTHRECORD.equals(type)) {
				revertHealthRecord(empiId, phrId, revertFlag, dao, ctx, vpModel);
				revertIdrReport(empiId, dao); // **恢复个人健康档案的同时恢复传染病档案
				deleteChildrenDeadRecord(phrId, dao);
				deleteDeathReportCard(empiId, dao);
			} else if (RevertOrLogout.HYPERTENSION.equals(type)) {
				revertHypertensionRecord(phrId, empiId, dao, vpModel);
			} else if (RevertOrLogout.DIABETES.equals(type)) {
				revertDiabetesRecord(phrId, empiId, dao, vpModel);
			} else if (RevertOrLogout.PREGNANT.equals(type)) {
				revertPregnantRecord(phrId, dao, vpModel);
			} else if (RevertOrLogout.CHILDREN.equals(type)) {
				revertChildrenHealthRecord(empiId, phrId, revertFlag, dao,
						vpModel);
			} else if (RevertOrLogout.DEBILITYCHILDREN.equals(type)) {
				revertDebilityChildrenRecord(empiId, phrId, dao, vpModel);
			} else if (RevertOrLogout.PSYCHOSIS.equals(type)) {
				revertPsychosisRecord(phrId, empiId, dao, vpModel);
			} else if (RevertOrLogout.OLDPEOPLE.equals(type)) {
				revertOldPeopleRecord(phrId, empiId, dao, ctx, vpModel);
			} else if (RevertOrLogout.SCHISTOSPMA.equals(type)) {
				revertSchistospmaRecord(phrId, empiId, dao);
			} else if (RevertOrLogout.RABIES.equals(type)) {
				revertRabiesRecord(phrId, empiId, dao);
			} else if (RevertOrLogout.LIMBDEFORMITY.equals(type)) {
				revertDeformityRecord(phrId, empiId, dao,
						DEF_LimbDeformityRecord);
			} else if (RevertOrLogout.BRAINDEFORMITY.equals(type)) {
				revertDeformityRecord(phrId, empiId, dao,
						DEF_BrainDeformityRecord);
			} else if (RevertOrLogout.INTELLECTDEFORMITY.equals(type)) {
				revertDeformityRecord(phrId, empiId, dao,
						DEF_IntellectDeformityRecord);
			} else if (RevertOrLogout.TUMOUR_HIGH_RISK.equals(type)) {
				// ** add by ChenXR
				String THRID = (String) body.get("THRID");
				revertTumourHighRiskRecord(THRID, empiId, dao, vpModel);
			} else if (RevertOrLogout.TPRC.equals(type)) {
				// ** add by ChenXR
				String TPRCID = (String) body.get("TPRCID");
				revertTPRCRecord(TPRCID, empiId, dao, vpModel);
			} else if (RevertOrLogout.LXGB.equals(type)) {
				// ** add by YuB
				revertLXGBRecord(phrId, empiId, dao, vpModel);
			} else if (RevertOrLogout.TC.equals(type)) {
				// ** add by ChenXR
				String TCID = (String) body.get("TCID");
				revertTCRecord(TCID, empiId, dao);
			} else if (RevertOrLogout.TS.equals(type)) {
				String recordId = (String) body.get("recordId");
				revertTSRecord(recordId, empiId, dao);
			} else if (RevertOrLogout.TQ.equals(type)) {
				String gcId = (String) body.get("gcId");
				revertTQRecord(gcId, empiId, dao);
			} else if (RevertOrLogout.HYPERTENSIONRISK.equals(type)) {
				revertHypertensionRisk(phrId, empiId, dao, vpModel);
			}
			/**
			 * 档案注销恢复管理
			 * 删除注销标志    chenk  2020.5.12
			 */
			revertEhrrecordinfo(phrId, empiId, dao, vpModel);
			
			
		} catch (ModelDataOperationException e) {
			logger.error("error query revertedRecords.", e);
			throw new ServiceException(e);
		}
	}

	private void revertLXGBRecord(String phrId, String empiId, BaseDAO dao,
			VisitPlanModel vpModel) throws ServiceException,
			ModelDataOperationException {
		RetiredVeteranCadresModel rvcModel = new RetiredVeteranCadresModel(dao);
		rvcModel.revertRVCRecord(phrId);
		vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.RVC);
		vLogService.saveVindicateLog(RVC_RetiredVeteranCadresRecord, "5",
				phrId, dao, empiId);
	}

	/**
	 * 恢复狂犬病档案
	 * 
	 * @param phrId
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertRabiesRecord(String phrId, String empiId, BaseDAO dao)
			throws ModelDataOperationException, ServiceException {
		RabiesRecordModel rrm = new RabiesRecordModel(dao);
		rrm.revertRabiesRecord(phrId);
		vLogService.saveVindicateLog(DC_RabiesRecord, "5", phrId, dao, empiId);
	}

	/**
	 * 恢复血吸虫档案
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertSchistospmaRecord(String phrId, String empiId,
			BaseDAO dao) throws ModelDataOperationException, ServiceException {
		SchistospmaModel sm = new SchistospmaModel(dao);
		sm.revertrevertSchistospmaRecord(phrId);
		vLogService.saveVindicateLog(SCH_SchistospmaRecord, "5", phrId, dao,
				empiId);
	}

	/**
	 * 恢复个人健康档案（所有业务子档及其随访计划）
	 * 
	 * @param empiId
	 * @param phrId
	 * @param revertFlag
	 * @param dao
	 * @param ctx
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ControllerException
	 */
	private void revertHealthRecord(String empiId, String phrId,
			String revertFlag, BaseDAO dao, Context ctx, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException, ExpException,
			PersistentDataOperationException, ControllerException {
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		hrModel.revertHealthRecord(phrId);
		vLogService.saveVindicateLog(EHR_HealthRecord, "5", phrId, dao, empiId);
		if ("yes".equals(revertFlag)) {
			revertHypertensionRecord(phrId, empiId, dao, vpModel);
			revertHypertensionRisk(phrId, empiId, dao, vpModel);
			revertDiabetesRecord(phrId, empiId, dao, vpModel);
			revertPregnantRecord(phrId, dao, vpModel);
			revertChildrenHealthRecord(empiId, phrId, revertFlag, dao, vpModel);
			revertPsychosisRecord(phrId, empiId, dao, vpModel);
			revertOldPeopleRecord(phrId, empiId, dao, ctx, vpModel);
			revertSchistospmaRecord(phrId, empiId, dao);
			revertRabiesRecord(phrId, empiId, dao);
			revertDeformityRecord(phrId, empiId, dao, DEF_LimbDeformityRecord);
			revertDeformityRecord(phrId, empiId, dao, DEF_BrainDeformityRecord);
			revertDeformityRecord(phrId, empiId, dao,
					DEF_IntellectDeformityRecord);
			revertTumourHighRiskRecord(null, empiId, dao, vpModel);
			revertLXGBRecord(phrId, empiId, dao, vpModel);
			revertTPRCRecord(null, empiId, dao, vpModel);
			revertTCRecord(null, empiId, dao);
			revertTSRecord(null, empiId, dao);
			revertTQRecord(null, empiId, dao);
		}
	}

	/**
	 * 删除孕产妇死亡登记
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 */
	private void deleteDeathReportCard(String empiId, BaseDAO dao)
			throws ModelDataOperationException {
		DeathReportCardModel drcModel = new DeathReportCardModel(dao);
		drcModel.deleteDeathReportCard(empiId);
	}

	/**
	 * 删除儿童死亡登记
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 */
	private void deleteChildrenDeadRecord(String phrId, BaseDAO dao)
			throws ModelDataOperationException {
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		chModel.deleteChildrenDeadRecord(phrId);
	}

	/**
	 * 恢复传染病档案
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 */
	private void revertIdrReport(String empiId, BaseDAO dao)
			throws ModelDataOperationException {
		IdrReportModel idrm = new IdrReportModel(dao);
		idrm.revertIdrReport(empiId);
	}

	/**
	 * 恢复老年人档案及随访计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 * @throws ServiceException
	 */
	private void revertOldPeopleRecord(String phrId, String empiId,
			BaseDAO dao, Context ctx, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException,
			ControllerException {
		OldPeopleRecordModel oprModel = new OldPeopleRecordModel(dao);
		OldPeopleVisitModel opvModel = new OldPeopleVisitModel(dao);
		oprModel.revertOldPeopleRecord(phrId);// 恢复老年人专项档案
		vLogService.saveVindicateLog(MDC_OldPeopleRecord, "5", phrId, dao,
				empiId);
		Map<String, Object> lastMap = opvModel.getLastVisitPlan(phrId);
		if (lastMap == null) {
			return;
		}
		Map<String, Object> visitMap = opvModel.getEndedOldPeopleVisit(phrId);
		String modeType = null;
		try {
			modeType = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.LNR + "_planMode");
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		Calendar c = Calendar.getInstance();
		if (visitMap != null && !visitMap.isEmpty()) {// 通过随访终止管理方式注销
			String endVisitId = (String) visitMap.get("visitId");
			Date endDate = (Date) lastMap.get("endDate");
			if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
				vpModel.setEndedManagePlan(endVisitId, BusinessType.LNR);// 置为未访
				if (PlanMode.BY_PLAN_TYPE.equals(modeType)) {// 按照固定计划方式
					createFixedOldPeopleVisitPlan(oprModel, lastMap, ctx, dao);
				} else {// 按照下次预约方式
					createManualOldPeopleVisitPlan(oprModel, lastMap, ctx, dao);
				}
			} else {
				vpModel.revertOverVisitPlan(phrId, endVisitId, BusinessType.LNR);
				opvModel.deleteOverOldPeopleVisit(phrId, endVisitId);
			}
			if (!lastMap.containsKey("visitId")
					|| !endVisitId.equals(lastMap.get("visitId"))) {// 终止管理的计划非最后一条随访计划
				vpModel.revertVisitPlan(vpModel.RECORDID, phrId,
						BusinessType.LNR);
			}
		} else {// 通过档案注销方式注销
			Date endDate = (Date) lastMap.get("endDate");
			if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
				if (PlanMode.BY_PLAN_TYPE.equals(modeType)) {// 按照固定计划方式
					createFixedOldPeopleVisitPlan(oprModel, lastMap, ctx, dao);
				} else {// 按照下次预约方式
					String planId = (String) lastMap.get("planId");
					vpModel.setUnvisitedPlanStatus(planId, BusinessType.LNR);
					createManualOldPeopleVisitPlan(oprModel, lastMap, ctx, dao);
				}
			}
			vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.LNR);
		}
	}

	/**
	 * 按照固定随访方式生成老年人随访
	 * 
	 * @param oprModel
	 * @param body
	 * @param ctx
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 * @throws ServiceException
	 */
	private void createFixedOldPeopleVisitPlan(OldPeopleRecordModel oprModel,
			Map<String, Object> body, Context ctx, BaseDAO dao)
			throws ModelDataOperationException, ServiceException,
			ControllerException {
		try {
			String empiId = (String) body.get("empiId");
			String recordId = (String) body.get("recordId");
			Map<String, Object> oldpeople = oprModel
					.getOldPeopleRecordByEmpiId(empiId);
			String manaDoctorId = (String) oldpeople.get("manaDoctorId");

			EmpiModel empiModel = new EmpiModel(dao);
			Map<String, Object> perInfo = empiModel.getEmpiInfoByEmpiid(empiId);
			Date birthday = (Date) perInfo.get("birthday");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("businessType", BusinessType.LNR);
			params.put("empiId", empiId);
			params.put("recordId", recordId);
			params.put("birthday", birthday.toString());
			params.put("taskDoctorId", manaDoctorId);

			PlanType planType = visitPlanCreator.create(params, ctx);
			if (planType == null) {
				return;
			}
			String userId = UserRoleToken.getCurrent().getUserId();

			oprModel.updateOldPeopleVisitStatus(planType.getPlanTypeCode(),
					empiId, userId);
		} catch (CreateVisitPlanException e) {
			throw new ModelDataOperationException("生产老年人随访计划失败", e);
		}
	}

	/**
	 * 按照下次预约方式生成老年人随访
	 * 
	 * @param oprModel
	 * @param body
	 * @param ctx
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 * @throws ServiceException
	 */
	private void createManualOldPeopleVisitPlan(OldPeopleRecordModel oprModel,
			Map<String, Object> body, Context ctx, BaseDAO dao)
			throws ModelDataOperationException, ServiceException,
			ControllerException {
		try {
			String empiId = (String) body.get("empiId");
			String recordId = (String) body.get("recordId");
			Map<String, Object> oldpeople = oprModel
					.getOldPeopleRecordByEmpiId(empiId);
			String manaDoctorId = (String) oldpeople.get("manaDoctorId");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("businessType", BusinessType.LNR);
			params.put("empiId", empiId);
			params.put("recordId", recordId);
			params.put("taskDoctorId", manaDoctorId);
			params.put("reserveDate", new Date());

			PlanType planType = visitPlanCreator.create(params, ctx);
			if (planType == null) {
				return;
			}
			String userId = UserRoleToken.getCurrent().getUserId();

			oprModel.updateOldPeopleVisitStatus(planType.getPlanTypeCode(),
					empiId, userId);
		} catch (CreateVisitPlanException e) {
			throw new ModelDataOperationException("生产老年人随访计划失败", e);
		}
	}

	/**
	 * 恢复高血压档案及随访计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertHypertensionRecord(String phrId, String empiId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException {
		HypertensionModel hsModel = new HypertensionModel(dao);
		HypertensionVisitModel hvModel = new HypertensionVisitModel(dao);
		hsModel.revertHypertensionRecord(phrId);
		vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.GXY,
				BusinessType.HYPERINQUIRE);
		vLogService.saveVindicateLog(MDC_HypertensionRecord, "5", phrId, dao,
				empiId);
		Map<String, Object> visitMap = hvModel
				.searchOverHypertensionVisit(phrId);
		if (visitMap != null && !visitMap.isEmpty()) {
			String visitId = (String) visitMap.get("visitId");
			vpModel.revertOverVisitPlan(phrId, visitId, BusinessType.GXY);
			hvModel.deleteOverHypertensionVisit(phrId, visitId);
		}
	}

	/**
	 * 恢复高血压高危档案及随访计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertHypertensionRisk(String phrId, String empiId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException {
		HypertensionRiskModel hsModel = new HypertensionRiskModel(dao);
		// HypertensionVisitModel hvModel = new HypertensionVisitModel(dao);
		hsModel.revertHypertensionRisk(empiId);
		// vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.GXY,
		// BusinessType.HYPERINQUIRE);
		// Map<String, Object> visitMap = hvModel
		// .searchOverHypertensionVisit(phrId);
		// if (visitMap != null && !visitMap.isEmpty()) {
		// String visitId = (String) visitMap.get("visitId");
		// vpModel.revertOverVisitPlan(phrId, visitId, BusinessType.GXY);
		// hvModel.deleteOverHypertensionVisit(phrId, visitId);
		// }
	}
	
	
	
	/**
	 * 档案注销恢复管理
	 * 删除注销标志
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertEhrrecordinfo(String phrId, String empiId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException {
		HypertensionRiskModel hsModel = new HypertensionRiskModel(dao);
		// HypertensionVisitModel hvModel = new HypertensionVisitModel(dao);
		//hsModel.revertEhrrecordinfo(dao, empiId);
		hsModel.revertEhrrecordinfo(empiId);
		// vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.GXY,
		// BusinessType.HYPERINQUIRE);
		// Map<String, Object> visitMap = hvModel
		// .searchOverHypertensionVisit(phrId);
		// if (visitMap != null && !visitMap.isEmpty()) {
		// String visitId = (String) visitMap.get("visitId");
		// vpModel.revertOverVisitPlan(phrId, visitId, BusinessType.GXY);
		// hvModel.deleteOverHypertensionVisit(phrId, visitId);
		// }
	}
	

	/**
	 * 恢复儿童档案及随访计划
	 * 
	 * @param empiId
	 * @param phrId
	 * @param revertFlag
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 * @throws ControllerException
	 */
	private void revertChildrenHealthRecord(String empiId, String phrId,
			String revertFlag, BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ExpException,
			PersistentDataOperationException, ServiceException,
			ControllerException {
		ChildrenHealthModel chModel = new ChildrenHealthModel(dao);
		chModel.revertHealthCardRecord(phrId);
		vLogService.saveVindicateLog(CDH_HealthCard, "5", phrId, dao, empiId);
		chModel.deleteChildrenDeadRecord(phrId);
		vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.CD_IQ,
				BusinessType.CD_CU);
		if ("yes".equals(revertFlag)) {
			revertDebilityChildrenRecord(empiId, phrId, dao, vpModel);
		}
	}

	/**
	 * 恢复体弱儿童档案及随访计划
	 * 
	 * @param empiId
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException
	 * @throws ControllerException
	 */
	private void revertDebilityChildrenRecord(String empiId, String phrId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ExpException,
			PersistentDataOperationException, ServiceException,
			ControllerException {

		DebilityChildrenModel dcModel = new DebilityChildrenModel(dao);
		Map<String, Object> m = dcModel.getLastDebilityChildrenRecord(phrId);
		dcModel.revertDebilityChildrenRecord(phrId);
		vLogService.saveVindicateLog(CDH_DebilityChildren, "5", phrId, dao,
				empiId);
		if (m != null
				&& m.get("cancellationReason") != null
				&& !m.get("cancellationReason").equals(
						CancellationReason.CANCELLATION)) {
			String recordId = (String) m.get("recordId");
			Map<String, Object> lastPlan = dcModel
					.getLastDebilityChildrenVisitPlan(recordId);
			if (BSCHISUtil.dateCompare(new Date(),
					(Date) lastPlan.get("endDate")) > 0) {
				Map<String, Object> body = new HashMap<String, Object>();
				body.put("recordId", recordId);
				body.put("empiId", empiId);
				EmpiModel em = new EmpiModel(dao);
				Date birthday = em.getBirthday(empiId);
				body.put("birthday", birthday);
				body.put("debilityReason", m.get("debilityReason"));

				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();
				c1.setTime(birthday);

				int age = 0;
				try {
					age = Integer.parseInt(ApplicationUtil.getProperty(
							Constants.UTIL_APP_ID, "childrenRegisterAge"));
				} catch (Exception e) {
					throw new ModelDataOperationException(e);
				}
				c1.add(Calendar.YEAR, age);
				if (BSCHISUtil.dateCompare(c1.getTime(), c2.getTime()) >= 0) {
					this.createDebilityChildrenVisitPlan(body,
							BusinessType.CD_DC, dao.getContext());
				}
			}
		}

		vpModel.revertVisitPlan(vpModel.EMPIID, empiId, BusinessType.CD_DC);
	}

	private String createDebilityChildrenVisitPlan(Map<String, Object> body,
			String businessType, Context ctx) throws ServiceException,
			ControllerException {
		Map<String, Object> planReq = new HashMap<String, Object>();
		planReq.put("recordId", body.get("recordId"));
		planReq.put("businessType", businessType);
		planReq.put("empiId", body.get("empiId"));
		planReq.put("birthday", body.get("birthday"));
		if (body.get("debilityReason") != null) {
			planReq.put("debilityReason", body.get("debilityReason"));
		}
		if (body.get("visitDate") != null) {
			planReq.put("visitDate", body.get("visitDate"));
		}
		try {
			PlanType planType = getVisitPlanCreator().create(planReq, ctx);
			return planType == null ? null : planType.getPlanTypeCode();
		} catch (CreateVisitPlanException e) {
			throw new ServiceException("生成计划失败!", e);
		}

	}

	/**
	 * 恢复妇保档案及随访计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertPregnantRecord(String phrId, BaseDAO dao,
			VisitPlanModel vpModel) throws ModelDataOperationException,
			ServiceException {
		PregnantRecordModel prModel = new PregnantRecordModel(dao);
		List<Map<String, Object>> records = prModel
				.getPregnantRecordByPhrId(phrId);
		if (records != null && records.size() > 0) {
			Map<String, Object> rMap = records.get(0);
			String pregnantId = (String) rMap.get("pregnantId");
			String empiId = (String) rMap.get("empiId");
			prModel.revertPregnantRecord(pregnantId);
			vLogService.saveVindicateLog(MHC_PregnantRecord, "5", pregnantId,
					dao, empiId);
			vpModel.revertVisitPlan(vpModel.RECORDID, pregnantId,
					BusinessType.MATERNAL, BusinessType.PREGNANT_HIGH_RISK);
		}
	}

	private void revertDiabetesPlan(String phrId, BaseDAO dao)
			throws ModelDataOperationException, ServiceException,
			ControllerException {
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		Map<String, Object> diabetesRecord = drm.getDiabetesRecordByPkey(phrId);
		List<Map<String, Object>> fixGroupList = drm
				.getFixGroupListByPhrId(phrId);
		if (fixGroupList != null && fixGroupList.size() > 0) {
			Map<String, Object> map = fixGroupList.get(0);
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("recordId", phrId);
			params.put("fixType", map.get("fixType"));
			params.put("instanceType", BusinessType.TNB);
			params.put("empiId", map.get("empiId"));
			params.put("groupCode", map.get("diabetesGroup"));
			params.put("fixGroupDate", formatter.format(new Date()));
			params.put("businessType", BusinessType.TNB);
			params.put("visitResult", VisitResult.SATISFIED);
			params.put("taskDoctorId", diabetesRecord.get("manaDoctorId"));
			String planMode = null;
			try {
				planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
						BusinessType.TNB + "_planMode");
			} catch (ControllerException e1) {
				throw new ModelDataOperationException(e1);
			}
			if (planMode == null || planMode.equals("")) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR,
						"未配置随访计划生成方式，请到系统配置界面中配置。");
			}
			if (PlanMode.BY_RESERVED.equals(planMode)) {
				params.put("reserveDate", new Date());
			}
			try {
				visitPlanCreator.create(params, dao.getContext());
			} catch (CreateVisitPlanException e) {
				throw new ModelDataOperationException("生成糖尿病随访计划失败", e);
			}
		}
	}

	/**
	 * 恢复糖尿病档案及更新随访计划，如果没有可更新的随访计划，则重新生成计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws ExpException
	 * @throws PersistentDataOperationException
	 * @throws ControllerException
	 */
	private void revertDiabetesRecord(String phrId, String empiId, BaseDAO dao,
			VisitPlanModel vpModel) throws ModelDataOperationException,
			ServiceException, ExpException, PersistentDataOperationException,
			ControllerException {
		DiabetesRecordModel drm = new DiabetesRecordModel(dao);
		drm.revertDiabetesRecord(phrId);
		vLogService.saveVindicateLog(MDC_DiabetesRecord, "5", phrId, dao,
				empiId);
		Map<String, Object> lastPlan = drm.getLastDiabetesVisitPlan(phrId);
		if (lastPlan == null) {
			return;
		}

		if (BSCHISUtil.dateCompare(new Date(), (Date) lastPlan.get("endDate")) > 0) {
			String planMode;
			try {
				planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
						BusinessType.TNB + "_planMode");
			} catch (ControllerException e) {
				throw new ModelDataOperationException(e);
			}
			StringBuffer sql = new StringBuffer(
					"update PUB_VisitPlan set planStatus=:planStatus,visitId=:visitId,visitDate=:visitDate where recordId =:recordId and planStatus=:planStatus1");
			if (planMode.equals(PlanMode.BY_PLAN_TYPE)) {
				sql.append(" and visitId is not null ");
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planStatus", PlanStatus.NOT_VISIT);
			parameters.put("planStatus1", PlanStatus.WRITEOFF);
			parameters.put("visitId", "");
			parameters.put("visitDate", BSCHISUtil.toDate(""));
			parameters.put("recordId", lastPlan.get("recordId"));
			dao.doUpdate(sql.toString(), parameters);

			this.revertDiabetesPlan(phrId, dao);
		} else {
			// String visitId = (String) lastPlan.get("visitId");
			DiabetesVisitModel dvm = new DiabetesVisitModel(dao);
			dvm.deleteOverDiabetesVisit(phrId);

			String sql = "update PUB_VisitPlan set planStatus=:planStatus ,visitId=:visitId,visitDate=:visitDate where recordId =:recordId and planStatus=:planStatus1 and visitId is not null";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planStatus", PlanStatus.NEED_VISIT);
			parameters.put("planStatus1", PlanStatus.WRITEOFF);
			parameters.put("visitId", "");
			parameters.put("visitDate", BSCHISUtil.toDate(""));
			parameters.put("recordId", lastPlan.get("recordId"));
			dao.doUpdate(sql, parameters);
		}
		vpModel.revertVisitPlan(vpModel.RECORDID, phrId, BusinessType.TNB);
	}

	/**
	 * 恢复精神病档案及更新随访计划，如果没有可更新的随访计划，则重新生成计划
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 * @throws ServiceException
	 */
	private void revertPsychosisRecord(String phrId, String empiId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException,
			ControllerException {
		PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		// 恢复档案
		prModel.revertPsychosisRecord(phrId);
		vLogService.saveVindicateLog(PSY_PsychosisRecord, "5", phrId, dao,
				empiId);
		// 恢复或生成随访计划
		String planMode = null;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.PSYCHOSIS + "_planMode");
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e);
		}
		// 按照固定随访时间
		if (PlanMode.BY_PLAN_TYPE.equals(planMode)) {
			List<Map<String, Object>> pvrList = pvModel.getPsyVisitRecord(
					phrId, VisitType.COMMON_VISIT);
			List<Map<String, Object>> vprList = vpModel.getVisitPlanList(phrId,
					BusinessType.PSYCHOSIS);
			int pvrNum = 0;
			if (pvrList != null) {
				pvrNum = pvrList.size();
			}
			if (vprList == null || vprList.size() == 0) {
				return;
			}
			Map<String, Object> lvpMap = (Map<String, Object>) vprList.get(0);// 最后一条随访计划
			int sn = (Integer) lvpMap.get("sn");
			if (pvrNum > 0) {// 有随访记录
				Map<String, Object> pvrMap = (Map<String, Object>) pvrList
						.get(0);// 最后一条随访记录
				String visitEffect = (String) pvrMap.get("visitEffect");
				if (VisitEffect.END.equals(visitEffect)) {// 最后一条随访记录是--随访终止管理
					String pvrVisitId = (String) pvrMap.get("visitId");
					Map<String, Object> stopMap = new HashMap<String, Object>();
					String planId = "";
					int count = 0;
					for (int i = 0; i < vprList.size(); i++) {
						Map<String, Object> vpMap = (Map<String, Object>) vprList
								.get(i);
						String vpVisitId = (String) vpMap.get("visitId");
						count += i;
						if (pvrVisitId.equals(vpVisitId)) {
							planId = (String) vpMap.get("planId");
							break;
						}
					}
					stopMap = (Map<String, Object>) vprList.get(count);
					if (count == 0) {// 终止管理是最后一条计划
						Date endDate = (Date) stopMap.get("endDate");
						Calendar c = Calendar.getInstance();
						if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
							// 计划置为“未访”，首条计划的日期为恢复日期 *生成新计划
							vpModel.updateVisitPlanNotVisit(planId);
							// 生成计划
							this.createPsyVisitPlan(phrId, endDate, sn, dao);
						} else {
							// 删除随访记录
							pvModel.deletePsyVisitByPkey(pvrVisitId);
							// 恢复计划
							vpModel.revertOverVisitPlan(phrId, pvrVisitId,
									BusinessType.PSYCHOSIS);
						}
					} else {
						Date endDate = (Date) lvpMap.get("endDate");
						Calendar c = Calendar.getInstance();
						if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
							// 计划置为“未访”，首条计划的日期为恢复日期 *生成新计划
							vpModel.updateVisitPlanNotVisit(planId);
							// 生成计划
							this.createPsyVisitPlan(phrId, endDate, sn, dao);
						} else {
							// 删除随访记录
							pvModel.deletePsyVisitByPkey(pvrVisitId);
							// 恢复计划
							vpModel.revertOverVisitPlan(phrId, pvrVisitId,
									BusinessType.PSYCHOSIS);
						}
						// 恢复注销的计划（状态为9）
						vpModel.revertVisitPlan(vpModel.RECORDID, phrId,
								BusinessType.PSYCHOSIS);
					}

				} else {// 档案注销
					Date endDate = (Date) lvpMap.get("endDate");
					Calendar c = Calendar.getInstance();
					if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
						// 恢复计划，生成新计划
						vpModel.setPlanNotVisit(vpModel.RECORDID, phrId,
								BusinessType.PSYCHOSIS);
						// 生成计划
						this.createPsyVisitPlan(phrId, endDate, sn, dao);
					} else {
						// 恢复计划，不生成新计划
						vpModel.revertVisitPlan(vpModel.RECORDID, phrId,
								BusinessType.PSYCHOSIS);
					}
				}

			} else {// 无随访记录-档案被注销
				Date endDate = (Date) lvpMap.get("endDate");
				Calendar c = Calendar.getInstance();
				if (BSCHISUtil.dateCompare(c.getTime(), endDate) > 0) {
					// 恢复计划，生成新计划
					vpModel.setPlanNotVisit(vpModel.RECORDID, phrId,
							BusinessType.PSYCHOSIS);
					// 生成计划
					this.createPsyVisitPlan(phrId, endDate, sn, dao);
				} else {
					// 恢复计划，不生成新计划
					vpModel.revertVisitPlan(vpModel.RECORDID, phrId,
							BusinessType.PSYCHOSIS);
				}
			}
		}
	}

	/**
	 * 生成精神病随访计划
	 * 
	 * @param phrId
	 * @throws ControllerException
	 * @throws ServiceException
	 */
	private void createPsyVisitPlan(String phrId, Date endDate, int sn,
			BaseDAO dao) throws ModelDataOperationException, ServiceException,
			ControllerException {
		PsychosisVisitModel pvModel = new PsychosisVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = pvModel.getLastNormalVisitRecord(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get psychosis normal visit record failed.", e);
			throw new ModelDataOperationException(e);
		}
		if (rsMap == null) {
			return;
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("recordId", phrId);
		paraMap.put("empiId", rsMap.get("empiId"));
		paraMap.put("businessType", BusinessType.PSYCHOSIS);
		paraMap.put("lastVisitEndDate", endDate);
		paraMap.put("planBeginDate", BSCHISUtil.toString(new Date()));
		paraMap.put("referral", rsMap.get("referral"));
		paraMap.put("type", rsMap.get("type"));
		paraMap.put("visitType", rsMap.get("visitType"));
		paraMap.put("sn", sn + 1);
		PsychosisRecordModel prModel = new PsychosisRecordModel(dao);
		Map<String, Object> prMap = null;
		try {
			prMap = prModel.getPsyRecordByPhrId(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Get Pyschosis record failedl.", e);
			throw new ModelDataOperationException(e);
		}
		if (prMap != null) {
			paraMap.put("taskDoctorId", prMap.get("taskDoctorId"));
		} else {
			paraMap.put("taskDoctorId", UserUtil.get(UserUtil.USER_ID));
		}
		try {
			visitPlanCreator.create(paraMap, dao.getContext());
		} catch (CreateVisitPlanException e) {
			logger.error("Generate psychosis visit plan failed.", e);
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 恢复残疾人档案
	 * 
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	private void revertDeformityRecord(String phrId, String empiId,
			BaseDAO dao, String tableName) throws ModelDataOperationException,
			ServiceException {
		DefModel dfModel = new DefModel(dao);
		dfModel.revertDeformityRecord(phrId, tableName);
		vLogService.saveVindicateLog(tableName, "5", phrId, dao, empiId);
	}

	/**
	 * 
	 * @Description:恢复肿瘤高危档案
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-18 下午6:10:01
	 * @throws ServiceException
	 * @Modify:
	 */
	private void revertTumourHighRiskRecord(String THRID, String empiId,
			BaseDAO dao, VisitPlanModel vpModel)
			throws ModelDataOperationException, ServiceException {
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		if (THRID != null) {// 恢复一条记录
			thrModel.revertTumourHighRiskRecord(THRID);
			vpModel.revertVisitPlan(vpModel.RECORDID, THRID, BusinessType.THR);
		} else {// 恢复该人的全部记录
			thrModel.revertAllTHRRecord(empiId);
			vpModel.revertVisitPlan(vpModel.EMPIID, empiId, BusinessType.THR);
		}
		vLogService.saveVindicateLog(MDC_TumourHighRisk, "5", THRID, dao,
				empiId);
	}

	/**
	 * 
	 * @Description:恢复肿瘤报告卡
	 * @param phrId
	 * @param dao
	 * @param vpModel
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 下午2:44:49
	 * @throws ServiceException
	 * @Modify:
	 */
	public void revertTPRCRecord(String TPRCID, String empiId, BaseDAO dao,
			VisitPlanModel vpModel) throws ModelDataOperationException,
			ServiceException {
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		if (TPRCID != null) {// 恢复一条记录
			tprcModel.revertTPRCRecord(TPRCID);
			vpModel.revertVisitPlan(vpModel.RECORDID, TPRCID, BusinessType.TPV);
		} else {// 恢复该人的全部记录
			tprcModel.revertAllTPRCRecord(empiId);
			vpModel.revertVisitPlan(vpModel.EMPIID, empiId, BusinessType.TPV);
		}
		vLogService.saveVindicateLog(MDC_TumourPatientReportCard, "5", TPRCID,
				dao, empiId);
	}

	/**
	 * 
	 * @Description:恢复肿瘤确诊记录
	 * @param TCID
	 * @param empiId
	 * @param dao
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午4:42:42
	 * @throws ServiceException
	 * @Modify:
	 */
	public void revertTCRecord(String TCID, String empiId, BaseDAO dao)
			throws ModelDataOperationException, ServiceException {
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		if (TCID != null) {// 恢复一条记录
			tcModel.revertTCRecord(TCID);
		} else {// 恢复该人的全部记录
			tcModel.revertAllTCRecord(empiId);
		}
		vLogService.saveVindicateLog(MDC_TumourConfirmed, "5", TCID, dao,
				empiId);
	}

	/**
	 * 
	 * @Description:恢复肿瘤初筛记录
	 * @param recordId
	 * @param empiId
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @author ChenXianRui 2014-10-17 上午11:41:37
	 * @Modify:
	 */
	public void revertTSRecord(String recordId, String empiId, BaseDAO dao)
			throws ModelDataOperationException, ServiceException {
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		if (recordId != null) {
			tsModel.revertTumourScreeningRecord(recordId);
		} else {
			tsModel.revertMyAllTSR(empiId);
		}
		vLogService.saveVindicateLog(MDC_TumourScreening, "5", recordId, dao,
				empiId);
	}

	/**
	 * 
	 * @Description:恢复问卷
	 * @param gcId
	 * @param empiId
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @author ChenXianRui 2014-10-17 上午11:41:11
	 * @Modify:
	 */
	public void revertTQRecord(String gcId, String empiId, BaseDAO dao)
			throws ModelDataOperationException, ServiceException {
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		if (gcId != null) {
			tqModel.revertTumourQuestionnaire(gcId);
		} else {
			tqModel.revertMyAllTHQ(empiId);
		}
		vLogService.saveVindicateLog(PHQ_GeneralCase, "5", gcId, dao, empiId);
	}
}
