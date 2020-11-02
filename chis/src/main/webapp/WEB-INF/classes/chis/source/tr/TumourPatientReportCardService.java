/**
 * @(#)TumourPatientReportCardService.java Created on 2014-4-27 下午3:30:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.dic.CancellationReason;
import chis.source.dic.PlanStatus;
import chis.source.dic.TumourAnnulControl;
import chis.source.dic.YesNo;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.HealthRecordService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPatientReportCardService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourPatientReportCardService.class);

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
	 * @Description:初始化肿瘤患者报卡信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-28 下午4:32:28
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doInitializeTPRCForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		// String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String TPRCID = (String) reqBodyMap.get("TPRCID");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		String op = "create";
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		Map<String, Object> rsMap = null;
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		try {
			rsMap = tprcModel.getTPRCbyPkey(TPRCID);
		} catch (ModelDataOperationException e) {
			logger.error("Get tumour patient report card failure.", e);
			throw new ServiceException(e);
		}
		if (rsMap != null && rsMap.size() > 0) {
			op = "update";
			resBodyMap.putAll(rsMap);
		} else {
			TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
			try {
				rsMap = tcModel.getTC(empiId, highRiskType);
			} catch (ModelDataOperationException e1) {
				logger.error("Get tumour confirmed record by phrId failure.",
						e1);
				throw new ServiceException(e1);
			}
			if (rsMap != null && rsMap.size() > 0) {
				resBodyMap.put("clinicStageCode", rsMap.get("cancerStage"));
				resBodyMap.put("T", rsMap.get("T"));
				resBodyMap.put("N", rsMap.get("N"));
				resBodyMap.put("M", rsMap.get("M"));
				resBodyMap.put("phrId", rsMap.get("phrId"));
				resBodyMap.put("empiId", empiId);
			}
			// 取档案档案中责任医生，管理机构
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> hrMap = null;
			try {
				hrMap = hrModel.getHealthRecordByEmpiId(empiId);
			} catch (chis.source.ModelDataOperationException e) {
				logger.error("Failed to get EHR .", e);
				throw new ServiceException(e);
			}
			if (hrMap != null) {
				resBodyMap.put("manaDoctorId", hrMap.get("manaDoctorId"));
				resBodyMap.put("manaUnitId", hrMap.get("manaUnitId"));
			}
		}
		if (resBodyMap != null && resBodyMap.size() > 0) {
			resBodyMap = SchemaUtil.setDictionaryMessageForForm(resBodyMap,
					MDC_TumourPatientReportCard);
		}
		resBodyMap.put("op", op);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:获取基本核实数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-28 下午5:46:59
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTumourBaseCheckInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String TPRCID = (String) reqBodyMap.get("TPRCID");
		String empiId = (String) reqBodyMap.get("empiId");
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = tprcModel.getTumourBaseCheckInfo(TPRCID);
		} catch (ModelDataOperationException e) {
			logger.error("Get tumour base check info failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (rsList != null && rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		if (rsMap.size() == 0) {
			// 取人个生活习惯中“吸烟情况”
			Map<String, Object> lsMap = null;
			try {
				lsMap = tprcModel.getSmokeCaseOfLifeStyle(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get smoke case of EHR_LifeStyle failure.", e);
				throw new ServiceException(e);
			}
			if (lsMap != null && lsMap.size() > 0) {
				rsMap.putAll(lsMap);
			}
			// 获取既往史中肿瘤家族史信息
			Map<String, Object> phMap = null;
			try {
				phMap = tprcModel.getTumourPastHistory(empiId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Get tumour family history of EHR_PastHistory failure.",
						e);
				throw new ServiceException(e);
			}
			if (phMap != null && phMap.size() > 0) {
				rsMap.putAll(phMap);
				rsMap.put("tumourFamilyHistory", "y");
			}
		}
		rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
				MDC_TumourPatientBaseCase);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:获取首次随访记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-28 下午5:48:39
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTumourFirstVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String TPRCID = (String) reqBodyMap.get("TPRCID");
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tprcModel.getFirstVisitRecord(TPRCID);
		} catch (ModelDataOperationException e) {
			logger.error("Get tumour first visit record.failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (rsMap != null && rsMap.size() > 0) {
			resBodyMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					MDC_TumourPatientFirstVisit);
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:保存肿瘤患者报卡信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-28 下午5:50:00
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourPatientReportCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> tprcMap = (Map<String, Object>) reqBodyMap
				.get("tprcData");
		Map<String, Object> tpfvbMap = (Map<String, Object>) reqBodyMap
				.get("tpfvbData");
		Map<String, Object> tpfvMap = (Map<String, Object>) reqBodyMap
				.get("tpfvData");
		String op = (String) req.get("op");
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		String phrId = (String) tprcMap.get("phrId");
		String empiId = (String) tprcMap.get("empiId");
		String highRiskType = (String) tprcMap.get("highRiskType");
		String TPRCID = (String) tprcMap.get("TPRCID");
		//String firstVisit = (String) tpfvMap.get("firstVisit");
		if (StringUtils.isNotEmpty(TPRCID)) {
			op = "update";
		} else {
			if (StringUtils.isNotEmpty(highRiskType)) {
				Map<String, Object> oMap = null;
				try {
					oMap = tprcModel.getTPRC(empiId, highRiskType);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Get record of MDC_TumourPatientReportCard by empiId["
									+ empiId + "] and highRiskType["
									+ highRiskType + "] failure.", e);
					throw new ServiceException(e);
				}
				if (oMap != null && oMap.size() > 0) {
					op = "update";
					TPRCID = (String) oMap.get("TPRCID");
					tprcMap.put("TPRCID", TPRCID);
				} else {
					op = "create";
				}
			} else {
				op = "create";
			}
		}
		Map<String, Object> resTPRCMap = new HashMap<String, Object>();
		try {
			resTPRCMap = tprcModel.saveTPRC(op, tprcMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour patient report card record failure.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			TPRCID = (String) resTPRCMap.get("TPRCID");
			tprcMap.put("TPRCID", TPRCID);
			// 修改确诊中 是否传报 为 是
			TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
			try {
				tcModel.updateNotification(YesNo.YES, empiId, highRiskType);
			} catch (ModelDataOperationException e) {
				logger.error(
						"update notification of  MDC_TumourConfirmed record failure.",
						e);
				throw new ServiceException(e);
			}
		}
		vLogService.saveVindicateLog(MDC_TumourPatientReportCard, op, TPRCID,
				dao, empiId);
		vLogService.saveRecords("BAO",op,dao,empiId);
		//保存分组信息
		Map<String, Object> resTPFVBMap = new HashMap<String, Object>();
		if (tpfvbMap != null) {
			tpfvbMap.put("TPRCID", TPRCID);
			String firstVisitId = (String) tpfvbMap.get("firstVisitId");
			String fvOp = op;
			if(StringUtils.isEmpty(firstVisitId)){
				fvOp = "create";
			}
			try {
				resTPFVBMap = tprcModel.saveTPBC(fvOp, tpfvbMap, true);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Save tumour patient base info check data failure.", e);
				throw new ServiceException(e);
			}
		}
		//保存首次随访，生成计划等
		Map<String, Object> resTPFVMap = new HashMap<String, Object>();
		String visitId = "";
		boolean validate = true;
		String vop = op;
		String annulControl = "";
		if (tpfvMap != null) {
			annulControl = (String) tpfvMap.get("annulControl");
			if (TumourAnnulControl.ANNUL_CONTROL.equals(annulControl)) {
				validate = false;
			}
			visitId = (String) tpfvMap.get("visitId");
			tpfvMap.put("TPRCID", TPRCID);
			if ("update".equals(op) && StringUtils.isEmpty(visitId)) {
				vop = "create";
			}
			try {
				resTPFVMap = tprcModel.saveTPFV(vop, tpfvMap, validate);
			} catch (ModelDataOperationException e) {
				logger.error("Save tumour patient first visit record failure.",
						e);
				throw new ServiceException(e);
			}
			if ("create".equals(vop)) {
				visitId = (String) resTPFVMap.get("visitId");
			}
			vLogService.saveVindicateLog(MDC_TumourPatientFirstVisit, op,
					visitId, dao, empiId);
			if (vop.equals("create")) {
				String agreeVisit = (String) tprcMap.get("agreeVisit");
				String isDeath = (String) tpfvMap.get("isDeath");
				if (YesNo.YES.equals(agreeVisit)
						&& TumourAnnulControl.NORMAL_CONTROL.equals(annulControl)
						&& YesNo.NO.equals(isDeath)) {
					// 生成 随访记录
					int CSV = 100;
					Object csvObject = tpfvMap.get("CSV");
					if (csvObject instanceof String) {
						CSV = Integer.parseInt((String) csvObject);
					} else {
						CSV = (Integer) csvObject;
					}
					if (CSV > 0) {
						Map<String, Object> genVPMap = new HashMap<String, Object>();
						genVPMap.put("empiId", empiId);
						genVPMap.put("phrId", phrId);
						genVPMap.put("recordId", TPRCID);
						genVPMap.put("businessType", BusinessType.TPV);
						genVPMap.put("createDate", BSCHISUtil.toString(new Date()));
						genVPMap.put("group", CSV + "");
						// 检查随访配置
						TumourPatientVisitModel tpvModel = new TumourPatientVisitModel(
								dao);
						try {
							tpvModel.hasPlanType((CSV + ""));
						} catch (ModelDataOperationException e1) {
							throw new ServiceException(e1);
						}
						// 生成计划
						PlanType pt = null;
						try {
							pt = visitPlanCreator.create(genVPMap, ctx);
						} catch (Exception e) {
							throw new ServiceException(
									Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
						}
						if (pt != null) {
							// 写入首次随访记录ID到计划中
							try {
								tprcModel.updateTPFirstVisitPlan(TPRCID, visitId);
							} catch (ModelDataOperationException e) {
								logger.error(
										"Update tumour first visit plan failure.",
										e);
								throw new ServiceException(e);
							}
						}
					}
				}
				if (TumourAnnulControl.ANNUL_CONTROL.equals(annulControl)) {// 已撤销*手动生成一条计划
					Application app = null;
					try {
						app = ApplicationUtil
								.getApplication(Constants.UTIL_APP_ID);
					} catch (ControllerException e) {
						throw new ServiceException(e);
					}
					Calendar calendar = Calendar.getInstance();
					Date currentDate = calendar.getTime();
					int delayDays = Integer.parseInt((String) app
							.getProperty(BusinessType.THR + "_delayDays"));
					int precedeDays = Integer.parseInt((String) app
							.getProperty(BusinessType.THR + "_precedeDays"));
					calendar.add(Calendar.DAY_OF_YEAR, -precedeDays);
					Date beginDate = calendar.getTime();
					calendar.add(Calendar.DAY_OF_YEAR, precedeDays + delayDays);
					Date endDate = calendar.getTime();
					Map<String, Object> pvMap = new HashMap<String, Object>();
					pvMap.put("empiId", empiId);
					pvMap.put("recordId", TPRCID);
					pvMap.put("visitId", visitId);
					pvMap.put("beginDate", beginDate);
					pvMap.put("endDate", endDate);
					pvMap.put("planDate", currentDate);
					pvMap.put("visitDate", currentDate);
					pvMap.put("businessType", BusinessType.TPV);
					pvMap.put("extend1", 0);
					pvMap.put("sn", 1);
					pvMap.put("planStatus", PlanStatus.CLOSE);
					pvMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
					pvMap.put("lastModifyDate", currentDate);
					pvMap.put("lastModifyUnit",
							UserUtil.get(UserUtil.MANAUNIT_ID));
					VisitPlanModel vpModel = new VisitPlanModel(dao);
					try {
						vpModel.saveVisitPlan("create", pvMap);
					} catch (ModelDataOperationException e) {
						throw new ServiceException(
								Constants.CODE_DATABASE_ERROR, "手动生成首次随访计划失败！",
								e);
					}
				}
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("tprcResData", resTPRCMap);
		resBodyMap.put("tpfvbResData", resTPFVBMap);
		resBodyMap.put("tpfvResData", resTPFVMap);
		boolean turnReport = (Boolean) reqBodyMap.get("turnReport");
		String status = (String) tprcMap.get("status");
		if(turnReport && Constants.CODE_STATUS_WRITE_OFF.equals(status)){
			try {
				tprcModel.revertTPRCRecord(TPRCID);
			} catch (ModelDataOperationException e) {
				logger.error("To revert tumour patient report and visit record failure.", e);
				throw new ServiceException(e);
			}
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			try {
				vpModel.revertVisitPlan(vpModel.RECORDID, TPRCID, BusinessType.TPV);
			} catch (ModelDataOperationException e) {
				logger.error("To revert visit plan failure.", e);
				throw new ServiceException(e);
			}
		}
		// ** 随访终止流程 =================================
		if (tpfvMap != null && !turnReport) {
			String annulCause = (String) tpfvMap.get("annulCause");
			String isDeath = (String) tpfvMap.get("isDeath");
			String cancellationReason = CancellationReason.OTHER;
			Map<String, Object> logoutMap = new HashMap<String, Object>();
			logoutMap.put("empiId", empiId);
			logoutMap.put("phrId", phrId);
			HealthRecordService hrService = new HealthRecordService();
			hrService.setvLogService(vLogService);
			if (TumourAnnulControl.ANNUL_CONTROL.equals(annulControl)) {// 已撤销
				if ("4".equals(annulCause)) {// 迁出
					// 注销全档及随访
					cancellationReason = CancellationReason.EMIGRATION;
					logoutMap.put("cancellationReason", cancellationReason);
					Map<String, Object> logoutReqMap = new HashMap<String, Object>();
					logoutReqMap.put("body", logoutMap);
					hrService.doLogoutAllRecords(logoutReqMap, res, dao, ctx);
				} else {
					// 只注销本档及随访
					if ("2".equals(annulCause)) {// 拒绝
						cancellationReason = CancellationReason.REPULSE;
					}
					try {
						tprcModel.logoutTPRCRecord(TPRCID, cancellationReason);
					} catch (ModelDataOperationException e) {
						logger.error(
								"logout tumour patient report card record failure.",
								e);
						throw new ServiceException(e);
					}
					vLogService.saveVindicateLog(MDC_TumourPatientReportCard,
							"3", TPRCID, dao, empiId);
					VisitPlanModel vpModel = new VisitPlanModel(dao);
					try {
						vpModel.logOutVisitPlan(vpModel.RECORDID, TPRCID,
								BusinessType.TPV);
					} catch (ModelDataOperationException e) {
						logger.error(
								"logout tumour patient visit plan failure.", e);
						throw new ServiceException(e);
					}
					// 注销高危
					TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
					try {
						thrModel.logoutTumourHighRiskRecord(empiId,
								highRiskType, cancellationReason);
					} catch (ModelDataOperationException e) {
						logger.error(
								"Logout record of MDC_TumourHighRisk by empiId["
										+ empiId + "] and highRiskType["
										+ highRiskType + "]", e);
						throw new ServiceException(e);
					}
					// 注销确诊
					TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
					try {
						tcModel.lonoutTumourConfirmedRecord(empiId,
								highRiskType, cancellationReason);
					} catch (ModelDataOperationException e) {
						logger.error(
								"Logout record of MDC_TumourConfirmed by empiId["
										+ empiId + "] and highRiskType["
										+ highRiskType + "]", e);
						throw new ServiceException(e);
					}
				}
			}
			if (YesNo.YES.equals(isDeath)) {// 死亡 注销全档及随访
				cancellationReason = CancellationReason.PASS_AWAY;
				logoutMap.put("cancellationReason", cancellationReason);
				logoutMap.put("deadReason", "患癌症");
				logoutMap.put("deadDate",
						BSCHISUtil.toString(Calendar.getInstance().getTime()));
				logoutMap.put("deadFlag", YesNo.YES);
				Map<String, Object> logoutReqMap = new HashMap<String, Object>();
				logoutReqMap.put("body", logoutMap);
				hrService.doLogoutAllRecords(logoutReqMap, res, dao, ctx);
			}
			if (TumourAnnulControl.ANNUL_CONTROL.equals(annulControl)
					|| YesNo.YES.equals(isDeath)) {
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("empiId", empiId);
				paraMap.put("phrId", phrId);
				paraMap.put("TPRCID", TPRCID);
				Map<String, Boolean> conMap = getTPRCControl(paraMap, ctx);
				resBodyMap.put("controlData", conMap);
			}
		}
		// ======================随访终止流程end============
		res.put("body", resBodyMap);
	}

	private Map<String, Boolean> getTPRCControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_TumourPatientReportCard, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_TumourPatientReportCard control error.", e);
			throw e;
		}
		return data;
	}

	/**
	 * 
	 * @Description:获取肿瘤现患表单权限制作参数
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-9-30 下午5:22:39
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTPRCControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Boolean> conMap = this.getTPRCControl(body, ctx);
		res.put("body", conMap);
	}

	/**
	 * 
	 * @Description:注销肿瘤患者报告卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-30 下午1:59:01
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String TPRCID = (String) reqBodyMap.get("TPRCID");
		// String phrId = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
				dao);
		try {
			tprcModel.logoutTPRCRecord(TPRCID, cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("logout tumour patient report card record failure.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MDC_TumourPatientReportCard, "3", TPRCID,
				dao, empiId);
		vLogService.saveRecords("BAO","logout",dao,empiId);
		VisitPlanModel vpModel = new VisitPlanModel(dao);
		try {
			vpModel.logOutVisitPlan(vpModel.RECORDID, TPRCID, BusinessType.TPV);
		} catch (ModelDataOperationException e) {
			logger.error("logout tumour patient visit plan failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> tprcMap = null;
		try {
			tprcMap = tprcModel.getTPRCbyPkey(TPRCID);
		} catch (ModelDataOperationException e) {
			logger.error("Get record of MDC_TumourPatientReportCard by pkey["
					+ TPRCID + "]", e);
			throw new ServiceException(e);
		}
		String highRiskType = "";
		if (tprcMap != null) {
			highRiskType = (String) tprcMap.get("highRiskType");
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
	}

	@SuppressWarnings("unchecked")
	public void doSaveTumourPatientReportDieCard(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		String regionCode = (String) body.get("regionCode");
		String op = (String) req.get("op");
		boolean logoutAll = true;
		try {
			if (phrId == null || "".equals(phrId)) {
				HealthRecordModel hm = new HealthRecordModel(dao);
				Map<String, Object> healthRecord = hm
						.getHealthRecordByEmpiId(empiId);
				if (healthRecord == null) {
					Map<String, Object> record = new HashMap<String, Object>();
					Context c = new Context();
					c.put("regionCode", regionCode);
					ctx.put("codeCtx", c);
					record.put("empiId", empiId);
					record.put("masterFlag", "n");
					record.put("manaDoctorId", body.get("manaDoctorId"));
					record.put("manaUnitId", body.get("manaUnitId"));
					record.put("regionCode", regionCode);
					record.put("deadFlag", "y");
					record.put("status", "1");
					Map<String, Object> pkey = hm.saveHealthRecord("create",
							record);
					phrId = (String) pkey.get("phrId");
					body.put("phrId", phrId);
				} else {
					phrId = (String) healthRecord.get("phrId");
					body.put("phrId", phrId);
				}
			}
			body.put("dieFlag", "y");
			TumourPatientReportCardModel tm = new TumourPatientReportCardModel(
					dao);
			Map<String, Object> resBody = tm.saveTPRC(op, body, false);
			resBody.put("regionCode", regionCode);
			if(logoutAll){
				// 死亡注销所有档案级随访
				Map<String, Object> logoutMap = new HashMap<String, Object>();
				logoutMap.put("empiId", empiId);
				logoutMap.put("phrId", phrId);
				logoutMap.put("cancellationReason", CancellationReason.PASS_AWAY);
				logoutMap.put("deadReason", "患癌症");
				logoutMap.put("deadDate",
						BSCHISUtil.toString(Calendar.getInstance().getTime()));
				logoutMap.put("deadFlag", YesNo.YES);
				Map<String, Object> logoutReqMap = new HashMap<String, Object>();
				logoutReqMap.put("body", logoutMap);
				HealthRecordService hrService = new HealthRecordService();
				hrService.setvLogService(vLogService);
				hrService.doLogoutAllRecords(logoutReqMap, res, dao, ctx);
			}
			resBody.putAll(body);
			resBody = SchemaUtil.setDictionaryMessageForForm(resBody, MDC_TumourPatientReportCard);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doCheckUpRecordByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HealthRecordModel hm = new HealthRecordModel(dao);
		String empiId = (String) body.get("empiId");
		try {
			Map<String, Object> healthRecord = hm
					.getHealthRecordByEmpiId(empiId);
			if (healthRecord != null) {
				String phrId = (String) healthRecord.get("phrId");
				healthRecord = SchemaUtil.setDictionaryMessageForList(
						healthRecord, EHR_HealthRecord);
				String regionCode_text = (String) healthRecord
						.get("regionCode_text");
				res.put("regionCode_text", regionCode_text);
				res.put("phrId", phrId);
				res.put("regionCode", healthRecord.get("regionCode"));
				TumourPatientReportCardModel tm = new TumourPatientReportCardModel(
						dao);
				List<Map<String, Object>> records = tm.getTPRCbyPhrId(phrId);
				if (records != null && records.size() > 0) {
					for (int i = 0; i < records.size(); i++) {
						Map<String, Object> record = records.get(i);
						String dieFlag = (String) record.get("dieFlag");
						if (record != null && "y".equals(dieFlag)) {
							res.put("pkey", record.get("TPRCID"));
							return;
						}
					}
				}
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}
}
