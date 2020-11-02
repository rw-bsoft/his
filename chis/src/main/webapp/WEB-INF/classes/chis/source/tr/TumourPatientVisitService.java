/**
 * @(#)TumourPatientVisitService.java Created on 2014-4-29 下午4:33:08
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
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
import chis.source.phr.HealthRecordService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPatientVisitService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourPatientVisitService.class);

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

	// = 获取上一年度 、 本年度 、下一年度随访计划三个服务
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
		String recordId = StringUtils.trimToEmpty((String) req.get("recordId"));
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.TPV;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, recordId, current,
					instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of Previous year.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取肿瘤高危人员上年度随访计划失败！");
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
		String recordId = StringUtils.trimToEmpty((String) req.get("recordId"));
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = 0;
		String instanceType = BusinessType.TPV;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, recordId, current,
					instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取肿瘤高危人员本年度随访计划失败！");
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
		String recordId = StringUtils.trimToEmpty((String) req.get("recordId"));
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.TPV;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, recordId, current,
					instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get visit plan of current.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取肿瘤高危人员下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:获取随访表单按键控制权限
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-29 下午4:52:37
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTPVisitControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = this.getTPVisitControl(reqBodyMap, ctx);
		res.put("body", data);
	}
	
	private Map<String, Boolean> getTPVisitControl(Map<String, Object> paraMap,
			Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_TumourPatientVisit, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_TumourPatientVisit control error.", e);
			throw e;
		}
		return data;
	}

	// @@ --------------------- 随访------------------------------------------
	/**
	 * 
	 * @Description:保存随访记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-30 上午10:21:31
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTPVisit(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourPatientVisitModel tpvModel = new TumourPatientVisitModel(dao);
		String empiId = (String) reqBodyMap.get("empiId");
		boolean validate = true;
		String annulControl = (String) reqBodyMap.get("annulControl");
		if (TumourAnnulControl.ANNUL_CONTROL.equals(annulControl)) {
			validate = false;
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = tpvModel.saveTPVisitRecord(op, reqBodyMap, validate);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour patient visit record failure.", e);
			throw new ServiceException(e);
		}
		String visitId = (String) reqBodyMap.get("visitId");
		if ("create".equals(op)) {
			visitId = (String) rsMap.get("visitId");
		}
		vLogService.saveVindicateLog(MDC_TumourPatientVisit, op, visitId, dao,
				empiId);
		// 随访终止流程
		String annulCause = (String) reqBodyMap.get("annulCause");
		String isDeath = (String) reqBodyMap.get("isDeath");
		String TPRCID = (String) reqBodyMap.get("TPRCID");
		String cancellationReason = CancellationReason.OTHER;
		String phrId = (String) reqBodyMap.get("phrId");
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
				TumourPatientReportCardModel tprcModel = new TumourPatientReportCardModel(
						dao);
				try {
					tprcModel.logoutTPRCRecord(TPRCID, cancellationReason);
				} catch (ModelDataOperationException e) {
					logger.error(
							"logout tumour patient report card record failure.",
							e);
					throw new ServiceException(e);
				}
				vLogService.saveVindicateLog(MDC_TumourPatientReportCard, "3",
						TPRCID, dao, empiId);
				VisitPlanModel vpModel = new VisitPlanModel(dao);
				try {
					vpModel.logOutVisitPlan(vpModel.RECORDID, TPRCID,
							BusinessType.TPV);
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
			Map<String, Boolean> conMap = getTPVisitControl(paraMap, ctx);
			rsMap.put("control", conMap);
		}
		// ----------------- 下面是做一些相关数据同步的更新操作 begin------------
		if (op.equals("create")) {
			String planId = (String) reqBodyMap.get("planId");
			// @@ 更新随访计划---更新当前随访计划记录中相关数据
			HashMap<String, Object> upBody = new HashMap<String, Object>();
			upBody.put("planId", planId);
			upBody.put("planStatus", PlanStatus.VISITED);
			upBody.put("visitDate", reqBodyMap.get("visitDate"));
			upBody.put("visitId", visitId);
			upBody.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
			upBody.put("lastModifyDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			try {
				tpvModel.updateTPVisitPlan(upBody, true);
			} catch (ModelDataOperationException e) {
				logger.error("Update plan status failed.", e);
				throw new ServiceException(e);
			}
			// 更改前面未处理过的随访计划的状态。
			// 将本次随访之前未做的随访计划状态设置为"未访"
			UserRoleToken ur = UserRoleToken.getCurrent();
			String userId = ur.getUserId();
			if (op.equals("create")) {
				String beginDate = (String) reqBodyMap.get("beginDate");
				try {
					tpvModel.updatePastDueVisitPlanStatus(TPRCID, beginDate,
							userId);
				} catch (ModelDataOperationException e) {
					logger.error("Update status of history visit plan failed.",
							e);
					throw new ServiceException(e);
				}
			}
			// 更新下一次随访提醒时------为本次的下次预约时间
			String nextPlanId = (String) reqBodyMap.get("nextPlanId");
			Date nextDate = null;
			if (reqBodyMap.get("nextDate") instanceof String) {
				nextDate = BSCHISUtil.toDate((String) reqBodyMap
						.get("nextDate"));
			} else if (reqBodyMap.get("nextDate") instanceof Date) {
				nextDate = (Date) reqBodyMap.get("nextDate");
			}
			if (StringUtils.isNotEmpty(nextPlanId) && null != nextDate) {
				try {
					tpvModel.setNextRemindDate(BusinessType.TPV, nextPlanId,
							nextDate);
				} catch (Exception e) {
					logger.error("Update warn date of next visit failed.", e);
					throw new ServiceException(e);
				}
			}
			// ----------------- 上面是做一些相关数据同步的更新操作 end-------------
			// String annulControl = (String) reqBodyMap.get("annulControl");
			// String isDeath = (String) reqBodyMap.get("isDeath");
			if (annulControl.equals(TumourAnnulControl.NORMAL_CONTROL)
					&& YesNo.NO.equals(isDeath)) {
				// 生成随访计划
				try {
					tpvModel.createVisitPlan(reqBodyMap, visitPlanCreator);
				} catch (ModelDataOperationException e) {
					logger.error("Generate visit plan failure.", e);
					throw new ServiceException(e);
				}
			}
		}
		res.put("body", rsMap);
	}
}
