/**
 * @(#)TumourConfirmedService.java Created on 2014-4-22 下午5:05:57
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.TumourNature;
import chis.source.dic.YesNo;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourConfirmedService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(TumourConfirmedService.class);

	/**
	 * 
	 * @Description:分页加载肿瘤确诊记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-22 下午3:45:45
	 * @Modify:
	 */
	public void doLoadTCPageList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.loadTCPageList(req);
		} catch (ModelDataOperationException e) {
			logger.error(
					"paging and querying tumour confirmed records failure.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}

	/**
	 * 
	 * @Description:处理肿瘤初筛转确诊业务
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-23 上午9:02:16
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourScreeningToConfirmed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		String recordId = (String) reqBodyMap.get("recordId");
		// 更新初筛记录中性质(为“确诊”)和高危标识（为“是”）
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		try {
			tsModel.updateNature(recordId, TumourNature.T_Confirmed, YesNo.YES);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Update nature and highRiskMark of MDC_TumourScreening failure.",
					e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourScreening, "6", recordId, dao,
				empiId);
		// 更新本高危记录“性质”字段，改为“确诊”
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		try {
			thrModel.updateNature(empiId, highRiskType,
					TumourNature.T_Confirmed);
		} catch (ModelDataOperationException e) {
			logger.error("Update nature of tumour hight risk record failure.",
					e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourHighRisk, "6", phrId, dao,
				empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLXH.get(highRiskType),op,dao,empiId);
		// 保存确诊记录
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> tcMap = null;
		try {
			tcMap = tcModel.getTC(empiId, highRiskType);
		} catch (ModelDataOperationException e1) {
			logger.error("Get tumour confirmed record failure.", e1);
			throw new ServiceException(e1);
		}
		if (tcMap != null && tcMap.size() > 0) {
			reqBodyMap.put("TCID", tcMap.get("TCID"));
			op = "update";
		} else {
			op = "create";
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTumourConfirmedRecord(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour confirmed record failure.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourConfirmed, op, phrId, dao,
				empiId);
		boolean turnConfirmed = (Boolean) reqBodyMap.get("turnConfirmed");
		String status = (String) reqBodyMap.get("status");
		if ("update".equals(op) && turnConfirmed
				&& Constants.CODE_STATUS_WRITE_OFF.equals(status)) {
			String TCID = (String) reqBodyMap.get("TCID");
			try {
				tcModel.revertTCRecord(TCID);
			} catch (ModelDataOperationException e) {
				logger.error("To revert the tumour confirmed record failure.",
						e);
				throw new ServiceException(e);
			}
		}
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:处理肿瘤高危转确诊业务
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-22 下午5:09:52
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTHRtoConfirmed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		int year = Calendar.getInstance().get(Calendar.YEAR);//默认当前年分
		if(reqBodyMap.get("year") instanceof String){
			year = Integer.parseInt((String)reqBodyMap.get("year"));
		}else{
			year = (Integer) reqBodyMap.get("year");
		}
		// 更新初筛记录中性质(为“确诊”)和高危标识（为“是”）
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		try {
			tsModel.updateNature(empiId, highRiskType, year,
					TumourNature.T_Confirmed, YesNo.YES);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Update nature and highRiskMark of MDC_TumourScreening failure.",
					e);
			throw new ServiceException(e);
		}
		// 更新本高危记录“性质”字段，改为“确诊”
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		try {
			thrModel.updateNature(empiId, highRiskType,
					TumourNature.T_Confirmed);
		} catch (ModelDataOperationException e) {
			logger.error("Update nature of tumour hight risk record failure.",
					e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourHighRisk, "6", phrId, dao,
				empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLXH.get(highRiskType),op,dao,empiId);
		// 保存确诊记录
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> tcMap = null;
		try {
			tcMap = tcModel.getTC(empiId, highRiskType);
		} catch (ModelDataOperationException e1) {
			logger.error("Get tumour confirmed record failure.", e1);
			throw new ServiceException(e1);
		}
		if (tcMap != null && tcMap.size() > 0) {
			reqBodyMap.put("TCID", tcMap.get("TCID"));
			op = "update";
		} else {
			op = "create";
		}
		reqBodyMap.put("confirmedSource", "2");
		reqBodyMap.put("notification", "n");
		reqBodyMap.put("status", "0");
		reqBodyMap.put("nature", "4");
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTumourConfirmedRecord(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour confirmed record failure.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourConfirmed, op, phrId, dao,
				empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLXH.get(highRiskType),op,dao,empiId);
		boolean turnConfirmed = (Boolean) reqBodyMap.get("turnConfirmed");
		String status = (String) reqBodyMap.get("status");
		if ("update".equals(op) && turnConfirmed
				&& Constants.CODE_STATUS_WRITE_OFF.equals(status)) {
			String TCID = (String) reqBodyMap.get("TCID");
			try {
				tcModel.revertTCRecord(TCID);
			} catch (ModelDataOperationException e) {
				logger.error("To revert the tumour confirmed record failure.",
						e);
				throw new ServiceException(e);
			}
		}
		// 注销肿瘤高危随做做的访计划
		Map<String, Object> thrMap = null;
		try {
			thrMap = thrModel.getTHR(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Get tumour high risk record failure.", e);
			throw new ServiceException(e);
		}
		if (thrMap != null && thrMap.size() > 0) {
			String THRID = (String) thrMap.get("THRID");
			// 注销高危未做的随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			try {
				vpModel.logOutVisitPlan(vpModel.RECORDID, THRID,
						BusinessType.THR);
			} catch (ModelDataOperationException e) {
				logger.error("Logout tumour high risk visit plan failure.", e);
				throw new ServiceException(e);
			}
		}

		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:保存肿瘤确诊记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-23 下午1:45:06
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourConfirmed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		String op = (String) req.get("op");
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> tcMap = null;
		try {
			tcMap = tcModel.getTC(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourConfirmed by empiId["
					+ empiId + "] and highRiskType[" + highRiskType
					+ "]  failure.", e);
			throw new ServiceException(e);
		}
		String oldCancerCase = "";
		if (tcMap != null && tcMap.size() > 0) {
			op = "update";
			reqBodyMap.put("TCID", tcMap.get("TCID"));
			oldCancerCase = (String) tcMap.get("cancerCase");
		} else {
			op = "create";
		}
		String nowCancerCase = (String) reqBodyMap.get("cancerCase");
		if("1".equals(oldCancerCase) && "2".equals(nowCancerCase)){
			//原来为癌前期，现在确诊了，将“确诊来源”置为“癌前期”
			reqBodyMap.put("confirmedSource", "3");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTumourConfirmedRecord(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour confirmed failure.", e);
			throw new ServiceException(e);
		}
		String phrId = (String) reqBodyMap.get("phrId");
		vLogService.saveVindicateLog(MDC_TumourConfirmed, op, phrId, dao,
				empiId);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:加载肿瘤专家评定及权限控制
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-24 上午10:15:58
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLoadTumourConfirmedReview(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (S.isNotEmpty(pkey)) {
			TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
			Map<String, Object> rsMap = null;
			try {
				rsMap = tcModel.getTumourConfirmedReview(pkey);
			} catch (ModelDataOperationException e) {
				logger.error("Load tumour confirmed review failure.", e);
				throw new ServiceException(e);
			}
			if (rsMap != null && rsMap.size() > 0) {
				resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
						MDC_TumourExpertReview);
			}
		}
		// 加载表单控制权限
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> conMap = new HashMap<String, Boolean>();
		try {
			conMap = ControlRunner.run(MDC_TumourConfirmed, reqBodyMap, ctx,
					ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_TumourConfirmed control error.", e);
			throw e;
		}
		conMap.put("create", true);
		resBodyMap.put(MDC_TumourExpertReview + "_control", conMap);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:保存专家评审信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-23 下午3:31:04
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourConfirmedReview(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTumourExpertReview(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour confirmed review failure.", e);
			throw new ServiceException(e);
		}
		String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		vLogService.saveVindicateLog(MDC_TumourExpertReview, op, phrId, dao,
				empiId);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:处理确诊转高危业务
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-23 下午4:50:12
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfirmedToHighRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String TCID = (String) reqBodyMap.get("TCID");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		int year = (Integer) reqBodyMap.get("year");
		// 将初筛中性质改为高危
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		try {
			tsModel.updateNature(empiId, highRiskType, year,
					TumourNature.T_HighRisk, YesNo.YES);
		} catch (ModelDataOperationException e) {
			logger.error("Update nature of tumour screening failure.", e);
			throw new ServiceException(e);
		}
		// 先判断有没有高危记录，没有则增加，有则 将高危人群管理中性质改为高危
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		Map<String, Object> thrMap = null;
		try {
			thrMap = thrModel.turnHighRiskToAddTHR(reqBodyMap);
		} catch (ModelDataOperationException e) {
			logger.error("To add tumour high risk record failure.", e);
			throw new ServiceException(e);
		}
		if (thrMap != null && thrMap.size() > 0) {
			boolean haveTHR = (Boolean) thrMap.get("haveTHR");
			if (haveTHR) {
				String THRID = (String) thrMap.get("THRID");
				reqBodyMap.put("THRID", THRID);
				try {
					thrModel.updateNature(empiId, highRiskType,
							TumourNature.T_HighRisk, THRID);
				} catch (ModelDataOperationException e) {
					logger.error("Update nature of tumour high risk failure.",
							e);
					throw new ServiceException(e);
				}
				vLogService.saveVindicateLog(MDC_TumourHighRisk, "8", TCID,
						dao, empiId);
			}
		}
		// 将确诊人群管理中性质改为高危
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		try {
			tcModel.updateNature(TCID, TumourNature.T_HighRisk);
		} catch (ModelDataOperationException e) {
			logger.error("Update nature of tumour confirmed failure", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourConfirmed, "8", TCID, dao,
				empiId);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.putAll(reqBodyMap);
		resBodyMap.put("success", true);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:注销肿瘤确认记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-24 上午10:00:59
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutTumourConfirmedRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String TCID = (String) reqBodyMap.get("TCID");
		// String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskTypes = (String) reqBodyMap.get("highRiskType");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		try {
			tcModel.lonoutTumourConfirmedRecord(TCID, cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout turmour confirmed record failure.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourConfirmed, "3", TCID, dao,
				empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLXH.get(highRiskTypes),"logout",dao,empiId);
		Map<String, Object> tcMap = null;
		try {
			tcMap = tcModel.getTumourConfirmedRecord(TCID);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourConfirmed by pkey[" + TCID
					+ "]", e);
			throw new ServiceException(e);
		}
		String highRiskType = "";
		if (tcMap != null) {
			highRiskType = (String) tcMap.get("highRiskType");
		}
		// 注销高危
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		try {
			thrModel.logoutTumourHighRiskRecord(empiId, highRiskType,
					cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout record of MDC_TumourHighRisk by empiId["
					+ empiId + "] and highRiskType[" + highRiskType + "]", e);
			throw new ServiceException(e);
		}
		// 注销报卡
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		try {
			tprcModel
					.logoutTPRCRecord(empiId, highRiskType, cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Logout record of MDC_TumourPatientReportCard by empiId["
							+ empiId + "] and highRiskType[" + highRiskType
							+ "]", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("success", true);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:获取确诊页面按钮权限控制
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-24 下午4:03:32
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTCRControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> conMap = new HashMap<String, Boolean>();
		try {
			conMap = ControlRunner.run(MDC_TumourConfirmed, reqBodyMap, ctx,
					ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_TumourConfirmed control error.", e);
			throw e;
		}
		conMap.put("create", true);
		res.put("body", conMap);
	}

	/**
	 * 
	 * @Description:检查是否有肿瘤现患报卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-25 上午11:31:30
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doHasTPRC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tprcModel.getTPRC(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourPatientReportCard by empiId["
					+ empiId + "] and highRiskType[" + highRiskType + "]", e);
			throw new ServiceException(e);
		}
		boolean hasTPRC = false;
		if (rsMap != null && rsMap.size() > 0) {
			hasTPRC = true;
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("hasTPRC", hasTPRC);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:获取高危确诊记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-7 下午4:23:08
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTCByEH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		String THRID = (String) reqBodyMap.get("THRID");
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> rsMap = null;
		if (S.isNotEmpty(empiId) && S.isNotEmpty(highRiskType)) {
			try {
				rsMap = tcModel.getTC(empiId, highRiskType);
			} catch (ModelDataOperationException e) {
				logger.error("Get record of MDC_TumourConfirmed by empiId["
						+ empiId + "] and highRiskType[" + highRiskType
						+ "] failure.", e);
				throw new ServiceException(e);
			}
		}
		if (S.isNotEmpty(THRID)) {
			TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
			Map<String, Object> thrMap = null;
			try {
				thrMap = thrModel.getTumourHighRiskRecord(THRID);
			} catch (ModelDataOperationException e) {
				logger.error("Get tumour high risk record failure", e);
				throw new ServiceException(e);
			}
			if (rsMap == null) {
				rsMap = new HashMap<String, Object>();
			}
			rsMap.put("highRiskType", thrMap.get("highRiskType"));
			rsMap.put("highRiskSource", thrMap.get("highRiskSource"));
			rsMap.put("manaDoctorId", UserUtil.get(UserUtil.USER_ID));
			rsMap.put("manaUnitId", UserUtil.get(UserUtil.MANAUNIT_ID));
		}
		Map<String, Object> fMap = new HashMap<String, Object>();
		fMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
				MDC_TumourConfirmed);
		res.put("body", fMap);
	}

	/**
	 * 
	 * @Description:判断健康档案注销原因 是否为 迁出 或 死亡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-12 下午3:36:09
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doJudgeHRisED(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		// 判断健康档案注销原因 是否为 迁出或为死亡
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		boolean isED = false;
		try {
			isED = hrModel.isEmigrationOrDeath(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"To determine the health record of this person whether emigration or death failure.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isED", isED);
		res.put("body", resBodyMap);
	}

	// ---------------------------癌前期管理------------------------------------------------
	/**
	 * 
	 * @Description:分页加载癌前期记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-23 上午10:28:56
	 * @Modify:
	 */
	public void doLoadTPrecancerPageList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.loadTPPageList(req);
		} catch (ModelDataOperationException e) {
			logger.error("paging and querying precancer records failure.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
}
