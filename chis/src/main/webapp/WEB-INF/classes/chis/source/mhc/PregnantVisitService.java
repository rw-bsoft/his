/**
 * @(#)PregnantRecordService.java Created on 2012-4-9 下午5:10:46
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mhc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.MHCVisitResult;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.YesNo;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.VisitPlanModel;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PregnantVisitService extends MHCService {

	private static final Logger logger = LoggerFactory
			.getLogger(PregnantVisitService.class);

	@SuppressWarnings("unchecked")
	protected void doSaveVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> visitRecord = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) visitRecord.get("pregnantId");
		String empiId = (String) visitRecord.get("empiId");
		String op = (String) req.get("op");
		PregnantVisitModel pvm = new PregnantVisitModel(dao);
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			pvm.savePregnantVisitRecord(visitRecord, op, res);
		} catch (Exception e) {
			logger.error("Failed to save visit record.", e);
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		String visitId = null;
		if (op.equals("create")) {
			visitId = (String) resBody.get("visitId");
		} else {
			visitId = (String) visitRecord.get("visitId");
		}
		vLogService.saveVindicateLog(MHC_VisitRecord, op, visitId, dao, empiId);
		//标识签约任务完成
		int pregnantWeeks2 = (Integer) visitRecord.get("pregnantWeeks");
		this.finishSCServiceTask(empiId, CJ_YCFFW, pregnantWeeks2 + "", dao);
		try {// @@ 更新本次计划的状态。
			updatePlanState((String) visitRecord.get("planId"), visitId,
					(String) visitRecord.get("visitDate"),
					(String) visitRecord.get("ifLost"), dao, ctx);
			if (op.equals("create")) {
				pvm.updateOverVisitPlanState(
						(String) visitRecord.get("pregnantId"),
						(Integer) visitRecord.get("sn"));
			}
		} catch (Exception e) {
			logger.error("Update plan status failed.", e);
			throw new ServiceException(e);
		}

		visitRecord.put("visitId", visitId);

		// ** 保存胎儿信息
		saveFetals(op, visitRecord, pvm);

		List<Map<String, Object>> highRisknesses = (List<Map<String, Object>>) visitRecord
				.get("highRisknesses");
		// @@ 保存高危因素。
		if (highRisknesses != null) {
			boolean highRisknessesChanged = (Boolean) visitRecord
					.get("highRisknessesChanged");
			Date gsetational;
			try {
				gsetational = prm.getGsetational(pregnantId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get gsetational.", e);
				throw new ServiceException(e);
			}
			visitRecord.put("lastMenstrualPeriod",
					BSCHISUtil.toString(gsetational, null));
			String strVisitDate = (String) visitRecord.get("visitDate");
			Date visitDate = BSCHISUtil.toDate(strVisitDate.substring(0, 10));
			visitRecord.put("$thisPregWeekBeginDate", BSCHISUtil
					.getBeginDateOfWeek(
							(String) visitRecord.get("lastMenstrualPeriod"),
							visitDate));
			try {
				if (highRisknessesChanged) {
					prm.deleteHighRiskness(pregnantId, visitId);
					try {
						recreateVisitPlan(visitRecord, highRisknesses, ctx);
					} catch (Exception e) {
						logger.error("Failed to recreate visit plan.", e);
						throw new ServiceException(e);
					}
				}
				// ** 保存高危因素
				prm.saveHighRisknessReasons(visitRecord);
				Date screenDate = prm.getPregnantScreenDate(pregnantId);
				HashMap<String,Object> visits = (HashMap<String, Object>) visitRecord.clone();
				visits.put("visitDate", screenDate);
				// ** 删除高危因素一览表
				prm.deleteHighRisknessReasonsList(pregnantId);
				// ** 保存高危因素一览表
				prm.saveHighRisknessReasonsList(visits);
			} catch (Exception e) {
				logger.error("Failed to save high risknesses.", e);
				throw new ServiceException(e);
			}
		}

		// ** 保存妇科检查数据。
		List<Object> checkList = (List<Object>) visitRecord.get("checkUpList");
		if (checkList != null) {
			try {
				prm.deletePregnantCheckList(pregnantId, visitId);
				prm.savePregnantCheckList(checkList, pregnantId, visitId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save check list.", e);
				throw new ServiceException(e);
			}
		}

		// ** 中医辩体描述信息
		HashMap<String, Object> desc = (HashMap<String, Object>) visitRecord
				.get("description");
		if (desc != null) {
			String descOp = "";
			String recordId = (String) desc.get("recordId");
			if (recordId != null && !recordId.equals("")) {
				descOp = "update";
			} else {
				descOp = "create";
				desc.put("visitId", visitId);
			}
			try {
				pvm.saveDesc(descOp, desc);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save desc message.", e);
				throw new ServiceException(e);
			}

		}
		// lyl 如果随访结果为"终止妊娠" 则终止管理
		String visitResult = (String) visitRecord.get("visitResult");
		if (MHCVisitResult.YZZRS.equalsIgnoreCase(visitResult)) {
			HashMap<String, Object> body = new HashMap<String, Object>();
			body.put("empiId", visitRecord.get("empiId"));
			body.put("pregnantId", visitRecord.get("pregnantId"));
			body.put("gestationMode", visitRecord.get("gestationMode"));
			body.put("endUnit", visitRecord.get("hospitalCode"));
			body.put("endDoctor", visitRecord.get("doctorId"));
			body.put("endDate", visitRecord.get("endDate"));
			try {
				endManagePregnant(body, dao);
			} catch (Exception e) {
				logger.error("Failed to end manage pregnant record.", e);
				throw new ServiceException(e);
			}
			// **终止管理
			resBody.put("isEndManage", true);
		}
	}

	/**
	 * 更新随访计划
	 * 
	 * @param planId
	 * @param visitId
	 * @param visitDate
	 * @param ifLost
	 * @param dao
	 * @param ctx
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	private void updatePlanState(String planId, String visitId,
			String visitDate, String ifLost, BaseDAO dao, Context ctx)
			throws ValidateException, ModelDataOperationException {
		HashMap<String, Object> upBody = new HashMap<String, Object>();
		upBody.put("planId", planId);
		if (ifLost.equals(YesNo.YES)) {
			upBody.put("planStatus", PlanStatus.LOST);
		} else {
			String userId = UserUtil.get(UserUtil.USER_ID);
			upBody.put("lastModifyUser", userId);
			upBody.put("lastModifyDate", BSCHISUtil.toString(new Date(), null));
			upBody.put("planStatus", PlanStatus.VISITED);
		}
		upBody.put("visitDate", visitDate);
		upBody.put("visitId", visitId);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			vpm.saveVisitPlan("update", upBody);
		} catch (ModelDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改随访计划失败!", e);
		}
	}

	/**
	 * 保存多胞胎信息
	 * 
	 * @param op
	 * @param visitRecord
	 * @param pvm
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	private void saveFetals(String op, Map<String, Object> visitRecord,
			PregnantVisitModel pvm) throws ServiceException {
		String pregnantId = (String) visitRecord.get("pregnantId");
		String empiId = (String) visitRecord.get("empiId");
		String visitId = (String) visitRecord.get("visitId");
		// ** 保存单胎信息
		String fetalId = null;
		// ** 为确保正式版程序上已经录过的数据会有问题，此处作先查询后确定执行何种操作
		if (op.equalsIgnoreCase("update")) {
			List<Map<String, Object>> list;
			try {
				list = pvm.getFetals(visitId, YesNo.YES);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get fetal record.", e);
				throw new ServiceException(e);
			}
			if (list == null || list.size() < 1)
				op = "create";
			else {
				fetalId = list.get(0).get("fetalId").toString();
			}
		}
		HashMap<String, Object> fetal = new HashMap<String, Object>();
		if (fetalId != null && !"".equals(fetalId)) {
			fetal.put("fetalId", fetalId);
		}
		fetal.put("visitId", visitId);
		fetal.put("pregnantId", pregnantId);
		fetal.put("empiId", empiId);
		fetal.put("saveFlag", YesNo.YES);
		fetal.put("fetalPosition", visitRecord.get("fetalPosition"));
		fetal.put("fetalPositionFlag", visitRecord.get("fetalPositionFlag"));
		fetal.put("fetalHeartRate", visitRecord.get("fetalHeartRate"));
		fetal.put("fetalHeartFlag", visitRecord.get("fetalHeartFlag"));
		try {
			pvm.saveFetals(op, fetal);
		} catch (ModelDataOperationException e1) {
			logger.error("Failed to save fetal record.", e1);
			throw new ServiceException(e1);
		}

		// ** 保存多胞胎信息
		List<Map<String, Object>> fetalsData = (List<Map<String, Object>>) visitRecord
				.get("fetalsData");
		if (fetalsData != null) {
			try {
				pvm.deleteFetals(visitId, YesNo.NO);
				for (int i = 0; i < fetalsData.size(); i++) {
					HashMap<String, Object> fetData = (HashMap<String, Object>) fetalsData
							.get(i);
					fetData.put("visitId", visitId);
					fetData.put("pregnantId", pregnantId);
					fetData.put("empiId", empiId);
					fetData.put("saveFlag", YesNo.NO);
					pvm.saveFetals("create", fetData);
				}
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save fetals record.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 重新生成计划
	 * 
	 * @param visitRecord
	 * @param highRisknesses
	 * @param ctx
	 * @throws CreateVisitPlanException
	 * @throws ServiceException 
	 */
	private void recreateVisitPlan(Map<String, Object> visitRecord,
			List<Map<String, Object>> highRisknesses, Context ctx)
			throws CreateVisitPlanException, ServiceException {
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.MATERNAL + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		String ifLost = (String) visitRecord.get("ifLost");
		if (ifLost.equals(YesNo.YES) && PlanMode.BY_PLAN_TYPE.equals(planMode)) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empiId", visitRecord.get("empiId"));
		params.put("pregnantId", visitRecord.get("pregnantId"));
		params.put("lastMenstrualPeriod",
				visitRecord.get("lastMenstrualPeriod"));
		params.put("planDate", visitRecord.get("planDate"));
		params.put("isCreateRecord", false); // **标识不为建档操作
		if (PlanMode.BY_RESERVED.equals(planMode)) {
			Date nextDate = (Date) SchemaUtil.getValue(MHC_VisitRecord,
					"nextDate", visitRecord.get("nextDate"));
			params.put("reserveDate", nextDate);
			params.put("businessType", BusinessType.MATERNAL);
		} else if (PlanMode.BY_PLAN_TYPE.equals(planMode)) {
			params.put("createDate", visitRecord.get("visitDate"));
			String businessType = BusinessType.MATERNAL;
			if (highRisknesses != null && highRisknesses.size() > 0) {
				for (Map<String, Object> map : highRisknesses) {
					Object fqc = map.get("frequence");
					Integer frequence = (Integer) SchemaUtil.getValue(
							MHC_HighRiskVisitReason, "frequence", fqc);
					if (frequence > 0) {
						businessType = BusinessType.PREGNANT_HIGH_RISK;
						break;
					}
				}
			}
			params.put("businessType", businessType);
			params.put("highRisknesses", highRisknesses);
		}
		try {
			getVisitPlanCreator().create(params, ctx);
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
	}
}
