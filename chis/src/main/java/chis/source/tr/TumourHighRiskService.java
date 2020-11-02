/**
 * @(#)TumourHighRiskService.java Created on 2014-4-2 下午3:25:52
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourHighRiskService.class);

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
	 * @Description:分页加载肿瘤高危档案列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-13 上午8:46:22
	 * @Modify:
	 */
	public void doLoadTHRListView(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrModel.loadTHRPage(req);
		} catch (ModelDataOperationException e) {
			logger.error(
					"paging and querying tumour high risk records failure.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}

	/**
	 * 
	 * @Description:获取高危肿瘤基本信息和查检结果，初始化基本信息页面
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-2 下午3:32:20
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doInitializeRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		// String phrId = (String) body.get("phrId");
		String THRID = (String) body.get("THRID");
		String highRiskType = (String) body.get("highRiskType");
		String empiId = (String) body.get("empiId");
		String highRiskSource = (String) body.get("highRiskSource");

		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		Map<String, Object> rsMap = null;
		if (S.isNotEmpty(THRID)) {
			try {
				rsMap = thrModel.getTumourHighRiskRecord(THRID);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get MDC_TumourHighRisk by THRID["
						+ THRID + "]", e);
				throw new ServiceException(e);
			}
		} else {
			if (S.isNotEmpty(empiId) && S.isNotEmpty(highRiskType)) {
				try {
					rsMap = thrModel.getTHR(empiId, highRiskType);
				} catch (ModelDataOperationException e) {
					logger.error("Failed to get MDC_TumourHighRisk by empiId["
							+ empiId + "] and highRiskType[" + highRiskType
							+ "]", e);
					throw new ServiceException(e);
				}
			}
		}
		Map<String, Object> thrMap = new HashMap<String, Object>();
		if (null != rsMap) {
			highRiskSource = (String) rsMap.get("thrMap");
			thrMap.putAll(rsMap);
			thrMap.put("op", "update");
		} else {
			thrMap.put("empiId", empiId);
			thrMap.put("op", "create");
		}
		// String hrStatus = "";
		Map<String, Object> resBody = new HashMap<String, Object>();
		if ("create".equals(thrMap.get("op"))) {
			// 判断高危管理级别 == 初筛 检查结果中无 阳性 检查结果 为 常规组
			String screeningId = (String) body.get("screeningId");
			String managerGroup = "";
			try {
				managerGroup = thrModel.getManagerGroup(screeningId);
			} catch (ModelDataOperationException e1) {
				logger.error("Get managerGroup of tumour high risk failure .",
						e1);
				throw new ServiceException(e1);
			}
			thrMap.put("managerGroup", managerGroup);
			// 获取个人档案，取出其他责任医生相关信息初始化责任医生及管辖机构
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> hrMap = null;
			try {
				hrMap = hrModel.getHealthRecordByEmpiId(empiId);
			} catch (chis.source.ModelDataOperationException e) {
				logger.error("Failed to get EHR .", e);
				throw new ServiceException(e);
			}
			if (hrMap != null) {
				Map<String, Object> hrFormMap = SchemaUtil
						.setDictionaryMessageForForm(hrMap, EHR_HealthRecord);
				thrMap.put("manaDoctorId", hrFormMap.get("manaDoctorId"));
				thrMap.put("manaUnitId", hrFormMap.get("manaUnitId"));
				// hrStatus = (String) hrMap.get("status");
			}
			// 高危因素
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			String highRiskFactor = "";
			try {
				highRiskFactor = tqModel.getHighRiskFactor(empiId,
						highRiskType, highRiskSource);
			} catch (ModelDataOperationException e1) {
				logger.error("Get high risk factor of turmour failure.", e1);
				throw new ServiceException(e1);
			}
			thrMap.put("highRiskFactor", highRiskFactor);
			thrMap.put("highRiskType", highRiskType);
		} else {
			// 高危因素
			String oldHRF = (String) thrMap.get("highRiskFactor");
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			String highRiskFactor = "";
			try {
				highRiskFactor = tqModel.getHighRiskFactor(empiId,
						highRiskType, highRiskSource);
			} catch (ModelDataOperationException e1) {
				logger.error("Get high risk factor of turmour failure.", e1);
				throw new ServiceException(e1);
			}
			if(S.isNotEmpty(oldHRF)){
				String[] oldHRFArray = oldHRF.split(";");
				for (int i = 0, len = oldHRFArray.length; i < len; i++) {
					if (highRiskFactor.indexOf(oldHRFArray[i]) == -1) {
						highRiskFactor = highRiskFactor + ";" + oldHRFArray[i];
					}
				}
			}
			thrMap.put("highRiskFactor", highRiskFactor);

			Map<String, Boolean> hrControlMap = this.getTHRControl(thrMap, ctx);
			resBody.put("MDC_TumourHighRisk" + "_actions", hrControlMap);
		}
		resBody.put(MDC_TumourHighRisk + Constants.DATAFORMAT4FORM, SchemaUtil
				.setDictionaryMessageForForm(thrMap, MDC_TumourHighRisk));

		List<Map<String, Object>> rsList = null;
		try {
			rsList = thrModel.getAllCheckResult(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to get all MDC_TumourScreeningCheckResult record of the person.",
					e);
			throw new ServiceException(e);
		}
		List<Map<String, Object>> tscrList = new ArrayList<Map<String, Object>>();
		if (rsList != null && rsList.size() > 0) {
			tscrList = SchemaUtil.setDictionaryMessageForList(rsList,
					MDC_TumourScreeningCheckResult);
		}
		resBody.put(MDC_TumourScreeningCheckResult + Constants.DATAFORMAT4LIST,
				tscrList);

		res.put("body", resBody);
	}

	private Map<String, Boolean> getTHRControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_TumourHighRisk, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_TumourHighRisk control error.", e);
			throw e;
		}
		return data;
	}

	/**
	 * 
	 * @Description:保存高危肿瘤记录 <br/>
	 *                       肿瘤按 empiId+高危类别（肿瘤类别）来判断档案唯一性
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-2 下午5:07:29
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourHighRiskRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		String THRID = (String) reqBodyMap.get("THRID");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		// 肿瘤按 empiId(phrId)+高危类别（肿瘤类别）来判断档案唯一性
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		Map<String, Object> oMap = null;
		try {
			oMap = thrModel.getTHR(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourHighRisk by empiId[" + empiId
					+ "] and highRiskType[" + highRiskType + "] failure.", e);
			throw new ServiceException(e);
		}
		if (StringUtils.isNotBlank(THRID)) {
			op = "update";
		} else {
			if (oMap != null && oMap.size() > 0) {
				op = "update";
				THRID = (String) oMap.get("THRID");
				reqBodyMap.put("THRID", THRID);
			} else {
				op = "create";
			}
		}
		boolean isGenVisitPlan = false;
		if (oMap != null) {
			String createStatus = (String) oMap.get("createStatus");
			if ("0".equals(createStatus)) {// 未档案
				isGenVisitPlan = true;
			}
		} else {
			if ("create".equals(op)) {
				isGenVisitPlan = true;
			}
		}
		// 保存档案
		reqBodyMap.put("createStatus", "1");// 建卡标志
		Date turnHighRiskDate = BSCHISUtil.toDate((String) reqBodyMap
				.get("turnHighRiskDate"));
		Date curDate = Calendar.getInstance().getTime();
		int dayes = BSCHISUtil.getPeriod(turnHighRiskDate, curDate);
		if (dayes < 30) {
			reqBodyMap.put("timelyCreation", "1");// 建卡及时
		}
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUserUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		reqBodyMap.put("createCardUser", curUserId);
		reqBodyMap.put("createCardUnit", curUserUnitId);
		reqBodyMap.put("createCardDate", curDate);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrModel.saveTumourHighRisk(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save MDC_TumourHighRisk.", e);
			throw new ServiceException(e);
		}
		vLogService
				.saveVindicateLog(MDC_TumourHighRisk, op, phrId, dao, empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLYH.get(highRiskType),"create", dao, empiId);
		if (rsMap == null || rsMap.size() == 0) {
			rsMap = new HashMap<String, Object>();
		}
		rsMap.putAll(reqBodyMap);
		if ("create".equals(op)) {
			THRID = (String) rsMap.get("THRID");
			reqBodyMap.put("THRID", THRID);// 生成计划时用
		}
		// 如果是从初筛转过来的，则先更新其初筛记录标识
		Boolean turnHighRisk = (Boolean) reqBodyMap.get("turnHighRisk");
		if (turnHighRisk != null && turnHighRisk.booleanValue() == true) {
			String tsRecordId = (String) reqBodyMap
					.get("tumourScreeningRecordId");
			Map<String, Object> tsMap = new HashMap<String, Object>();
			tsMap.put("recordId", tsRecordId);
			tsMap.put("nature", TumourNature.T_HighRisk);
			tsMap.put("highRiskMark", YesNo.YES);
			TumourScreeningModel tsModel = new TumourScreeningModel(dao);
			try {
				tsModel.saveTumourScreening("update", tsMap, false);
			} catch (ModelDataOperationException e) {
				logger.error("Update tumour screening record failure.", e);
				throw new ServiceException(e);
			}
			vLogService.saveVindicateLog(MDC_TumourScreening, "8", tsRecordId,
					dao, empiId);
			// 如果已经存在高危记录
			if (StringUtils.isNotEmpty(THRID)) {
				// 判断是否是注销的状态
				boolean isLogout = false;
				try {
					isLogout = thrModel.isLogoutTHR(THRID);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Determine whether the toumour high risk record is logout failure.",
							e);
					throw new ServiceException(e);
				}
				if (isLogout) {
					// 恢复档案
					try {
						thrModel.revertTumourHighRiskRecord(THRID);
					} catch (ModelDataOperationException e) {
						logger.error("Revert tumour high risk record failure.",
								e);
						throw new ServiceException(e);
					}
				}
				try {
					isLogout = thrModel.isLogoutTHRVisit(THRID);
				} catch (ModelDataOperationException e1) {
					throw new ServiceException(e1);
				}
				if (isLogout) {
					// 恢复随访计划
					VisitPlanModel vpModel = new VisitPlanModel(dao);
					try {
						vpModel.revertVisitPlan(vpModel.RECORDID, THRID,
								BusinessType.THR);
					} catch (ModelDataOperationException e) {
						logger.error(
								"Revert tumour high risk visit plan failure.",
								e);
						throw new ServiceException(e);
					}
				}
			}
		}
		// 保存转组信息
		boolean isGroupChange = false;
		String OldManagerGroup = "";
		if (op.equals("update")) {
			String managerGroup = (String) reqBodyMap.get("managerGroup");
			if (oMap != null) {
				OldManagerGroup = (String) oMap.get("managerGroup");
			}
			if (!managerGroup.equals(OldManagerGroup)) {
				isGroupChange = true;
			}
		}
		if (isGenVisitPlan || isGroupChange) {
			Map<String, Object> thrGroupMap = new HashMap<String, Object>();
			thrGroupMap.put("empiId", empiId);
			thrGroupMap.put("THRID", THRID);
			thrGroupMap.put("highRiskType", reqBodyMap.get("highRiskType"));
			thrGroupMap.put("fixGroupDate", reqBodyMap.get("turnHighRiskDate"));
			if (isGroupChange) {
				thrGroupMap.put("oldFixGroup", OldManagerGroup);
			} else {
				thrGroupMap.put("oldFixGroup", reqBodyMap.get("managerGroup"));
			}
			thrGroupMap.put("fixGroup", reqBodyMap.get("managerGroup"));
			thrGroupMap.put("createUser", curUserId);
			thrGroupMap.put("createUnit", curUserUnitId);
			thrGroupMap.put("createDate", curDate);
			thrGroupMap.put("lastModifyUnit", curUserUnitId);
			thrGroupMap.put("lastModifyUser", curUserId);
			thrGroupMap.put("lastModifyDate", curDate);
			Map<String, Object> fgMap = null;
			try {
				fgMap = thrModel.saveTumourHighRiskGroup("create", thrGroupMap,
						true);
			} catch (ModelDataOperationException e1) {
				logger.error("Save tumour high risk group failure.", e1);
				throw new ServiceException(e1);
			}
			String groupId = "";
			if (fgMap != null) {
				groupId = (String) fgMap.get("groupId");
			}
			vLogService.saveVindicateLog(MDC_TumourHighRiskGroup, "1", groupId,
					dao, empiId);
		}
		// 生成随访计划
		if (isGenVisitPlan) {
			try {
				thrModel.createVisitPlan(reqBodyMap, visitPlanCreator);
			} catch (ModelDataOperationException e) {
				logger.error("Generate visit plan failed.", e);
				throw new ServiceException(e);
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
				MDC_TumourHighRisk);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:注销肿瘤高危档案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-18 下午4:38:57
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String THRID = (String) reqBodyMap.get("THRID");
		// String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskTypes = (String) reqBodyMap.get("highRiskType");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		try {
			thrModel.logoutTumourHighRiskRecord(THRID, cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("logout tumour high risk record failure.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourHighRisk, "3", THRID, dao,
				empiId);
		vLogService.saveRecords(BSCHISEntryNames.RECORDSMAP_ZLYH.get(highRiskTypes), "logout", dao, empiId);
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			vpModel.logOutVisitPlan(vpModel.RECORDID, THRID, BusinessType.THR);
		} catch (ModelDataOperationException e) {
			logger.error("logout tumour high risk visit plan failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> thrMap = null;
		try {
			thrMap = thrModel.getTumourHighRiskRecord(THRID);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourHighRisk by pkey[" + THRID
					+ "] failure.", e);
			throw new ServiceException(e);
		}
		String highRiskType = "";
		if (thrMap != null) {
			highRiskType = (String) thrMap.get("highRiskType");
		}
		// 注销确诊
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		try {
			tcModel.lonoutTumourConfirmedRecord(empiId, highRiskType,
					cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("Logout record of MDC_TumourConfirmed by empiId["
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
	}

	/**
	 * 
	 * @Description:转正常
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-2 下午3:20:12
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doTurnNormal(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		try {
			thrModel.turnNormal(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("high risk tumour turn normal failure.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:依据empiId，highRiskType判断是否存在肿瘤高危档案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-12 下午3:08:15
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doIsExitTHR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		boolean isExistTHR = false;
		try {
			isExistTHR = thrModel.isExistTHR(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Judge is exist the tumour high risk record failure.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isExistTHR", isExistTHR);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:判断是否有肿瘤既往史
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-26 上午9:33:51
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doExistTPMHRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		TumourPastMedicalHistoryModel tpmhModel = new TumourPastMedicalHistoryModel(
				dao);
		boolean isExistTPMH = false;
		try {
			isExistTPMH = tpmhModel.isExistTPMHRecord(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Judge is exist tumour past medical history record failure.",
					e);
			throw new ServiceException(e);
		}
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		// 判断健康档案注销原因 是否为 迁出或为死亡
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
		resBodyMap.put("isExistTPMH", isExistTPMH);
		resBodyMap.put("isED", isED);
		res.put("body", resBodyMap);
	}
}
