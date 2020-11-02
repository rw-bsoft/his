/**
 * @(#)TumourHighRiskVisitService.java Created on 2014-4-15 上午9:52:35
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
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.YesNo;
import chis.source.phr.HealthRecordService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
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
public class TumourHighRiskVisitService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourHighRiskVisitService.class);

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
		String instanceType = BusinessType.THR;
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
		String instanceType = BusinessType.THR;
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
		String instanceType = BusinessType.THR;
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
	 * @Description: 获取随访表单按键控制权限
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-16 上午9:14:42
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTHRVisitControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_TumourHighRiskVisit, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionVisit control error.", e);
			throw e;
		}
		res.put("body", data);
	}

	// @@ --------------------- 随访------------------------------------------
	/**
	 * 加载随访初始数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doVisitInitialize(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		// String empiId = (String) reqBody.get("empiId");
		// 获取高危类别和高危组别-->档案
		// String phrId = (String) reqBody.get("phrId");
		String THRID = (String) reqBody.get("THRID");
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrModel.getTumourHighRiskRecord(THRID);
		} catch (ModelDataOperationException e1) {
			logger.error("Get tumour high risk record by THRID failur.", e1);
			throw new ServiceException(e1);
		}
		if (rsMap != null && rsMap.size() > 0) {
			resBody.put("thrRecord", SchemaUtil.setDictionaryMessageForForm(
					rsMap, MDC_TumourHighRisk));
		}
		// 获得随访配置参数
		try {
			Application app = ApplicationUtil
					.getApplication(Constants.UTIL_APP_ID);
			String planMode = (String) app.getProperty(BusinessType.THR
					+ "_planMode");
			// String precedeDays = (String) app.getProperty(BusinessType.THR
			// + "_precedeDays");
			// String delayDays = (String) app.getProperty(BusinessType.THR
			// + "_delayDays");
			resBody.put("planMode", planMode);// 计划生成方式（随访结果或预约时间）
			// resBody.put("precedeDays", precedeDays);// 随访提前天数
			// resBody.put("delayDays", delayDays);// 随访延迟天数
			// 管理年度
			resBody.put("year", BSCHISUtil.toString(new Date()).substring(0, 4));
		} catch (ControllerException e) {
			logger.error("Get visit configuration parameter failure.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

	/**
	 * 
	 * @Description: 加载肿瘤高危随访记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-16 上午10:10:16
	 * @Modify:
	 */
	public void doGetTHRVisit(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String pkey = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		TumourHighRiskVisitModel thrVisitModel = new TumourHighRiskVisitModel(
				dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrVisitModel.getTHRVisit(pkey, schema);
		} catch (ModelDataOperationException e) {
			logger.error("Get health guidance of psychosis visit Failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(rsMap,
				MDC_TumourHighRiskVisit));
	}

	/**
	 * 
	 * @Description: 保存肿瘤高危随访记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-16 上午10:10:16
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTHRVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourHighRiskVisitModel thrVisitModel = new TumourHighRiskVisitModel(
				dao);
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		String THRID = (String) reqBodyMap.get("THRID");
		String planId = (String) reqBodyMap.get("planId");
		String visitEffect = (String) reqBodyMap.get("visitEffect");
		String fixGroup = (String) reqBodyMap.get("fixGroup");
		if ("create".equals(op)) {
			// 保存分组信息
			Map<String, Object> thrGroupMap = new HashMap<String, Object>();
			boolean isFixGroupChange = false;
			List<Map<String, Object>> rsList = null;
			try {
				rsList = thrVisitModel.getTHRVisitRecords(THRID);
			} catch (ModelDataOperationException e) {
				logger.error("Get tumour high risk visit recrods failure.", e);
				throw new ServiceException(e);
			}
			if (rsList != null && rsList.size() > 0) {
				Map<String, Object> thrVisitMap = rsList.get(0);
				String lastFixGroup = (String) thrVisitMap.get("fixGroup");
				if (visitEffect.equals(VisitEffect.LOST)) {// @@
															// 暂时失访。随访分组取上次随访分组
					fixGroup = lastFixGroup;
				}
				// 失访时，不增加分组评估记录
				if (!fixGroup.equals(lastFixGroup)) {
					isFixGroupChange = true;
					thrGroupMap.put("oldFixGroup", lastFixGroup);
				}
			} else {
				Map<String, Object> thrMap = null;
				try {
					thrMap = thrModel.getTumourHighRiskRecord(THRID);
				} catch (ModelDataOperationException e) {
					logger.error("Get tumour high risk record failure.", e);
					throw new ServiceException(e);
				}
				if (thrMap != null && thrMap.size() > 0) {
					String managerGroup = (String) thrMap.get("managerGroup");
					if (!fixGroup.equals(managerGroup)) {
						isFixGroupChange = true;
						thrGroupMap.put("oldFixGroup", managerGroup);
					}
				}
			}
			if (isFixGroupChange) {
				Date curDate = new Date();
				thrGroupMap.put("empiId", reqBodyMap.get("empiId"));
				thrGroupMap.put("THRID", reqBodyMap.get("THRID"));
				thrGroupMap.put("highRiskType", reqBodyMap.get("highRiskType"));
				thrGroupMap.put("fixGroupDate", curDate);
				String curUserId = UserUtil.get(UserUtil.USER_ID);
				String curUserUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
				thrGroupMap.put("createUser", curUserId);
				thrGroupMap.put("createUnit", curUserUnitId);
				thrGroupMap.put("createDate", curDate);
				thrGroupMap.put("lastModifyUnit", curUserUnitId);
				thrGroupMap.put("lastModifyUser", curUserId);
				thrGroupMap.put("lastModifyDate", curDate);
				thrGroupMap.put("fixGroup", fixGroup);
				Map<String, Object> gMap = null;
				try {
					gMap = thrModel.saveTumourHighRiskGroup("create",
							thrGroupMap, true);
				} catch (ModelDataOperationException e1) {
					logger.error("Save tumour high risk group failure.", e1);
					throw new ServiceException(e1);
				}
				String groupId = "";
				if (gMap != null) {
					groupId = (String) gMap.get("groupId");
				}
				vLogService.saveVindicateLog(MDC_TumourHighRiskGroup, "1",
						groupId, dao, empiId);
			}
		}
		Calendar calendar = Calendar.getInstance();
		Date lastVisitDate = calendar.getTime();
		try {
			lastVisitDate = thrVisitModel.getLastVisitDate(empiId, THRID,
					planId, BusinessType.THR);
		} catch (ModelDataOperationException e) {
			logger.error("Get this last visit Date failure.", e);
			throw new ServiceException(e);
		}
		reqBodyMap.put("lastVisitDate", lastVisitDate);
		// 保存随访记录
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrVisitModel.saveTHRVisit(op, reqBodyMap, vLogService);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save tumour high risk visit record.", e);
			throw new ServiceException(e);
		}
		if (rsMap == null) {
			rsMap = new HashMap<String, Object>();
		}
		rsMap.put("JKCFRecords", reqBodyMap.get("JKCFRecords"));
		String visitId = (String) reqBodyMap.get("visitId");
		if ("create".equals(op)) {
			visitId = (String) rsMap.get("visitId");
		}
		vLogService.saveVindicateLog(MDC_TumourHighRiskVisit, op, visitId, dao,
				empiId);
		// ----------------- 下面是做一些相关数据同步的更新操作 begin------------
		if (op.equals("create")) {
			// @@ 更新随访计划---更新当前随访计划记录中相关数据
			HashMap<String, Object> upBody = new HashMap<String, Object>();
			upBody.put("planId", planId);
			if (visitEffect.equals(VisitEffect.LOST)) {// @@ 暂时失访。随访分组取上次随访分组
				upBody.put("planStatus", PlanStatus.LOST);
			} else if (visitEffect.equals(VisitEffect.END)) {// @@ 终止管理
																// 注销本档案及计划，不再生成计划
				upBody.put("planStatus", PlanStatus.CLOSE);
			} else {
				upBody.put("planStatus", PlanStatus.VISITED);
			}
			upBody.put("visitDate", reqBodyMap.get("visitDate"));
			upBody.put("visitId", visitId);
			upBody.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
			upBody.put("lastModifyDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			try {
				thrVisitModel.updateTHRVisitPlan(upBody, true);
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
					thrVisitModel.updatePastDueVisitPlanStatus(empiId, THRID,
							beginDate, userId);
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
					thrVisitModel.setNextRemindDate(BusinessType.THR,
							nextPlanId, nextDate);
				} catch (Exception e) {
					logger.error("Update warn date of next visit failed.", e);
					throw new ServiceException(e);
				}
			}
		}
		// ----------------- 上面是做一些相关数据同步的更新操作 end-------------
		if (visitEffect.equals(VisitEffect.END)) {// 注销本档案及计划
			Map<String, Object> logoutMap = new HashMap<String, Object>();
			logoutMap.put("empiId", empiId);
			logoutMap.put("phrId", phrId);
			String terminatedReason = (String) reqBodyMap
					.get("terminatedReason");
			if ("1".equals(terminatedReason) || "2".equals(terminatedReason)) {
				HealthRecordService hrService = new HealthRecordService();
				hrService.setvLogService(vLogService);
				String cancellationReason = CancellationReason.OTHER;
				if ("1".equals(terminatedReason)) {// 死亡
					// 注销全部档案及随访
					cancellationReason = CancellationReason.PASS_AWAY;
					Date deadDate = (Date) reqBodyMap.get("deadDate");
					String deadReason = (String) reqBodyMap.get("deadReason");
					cancellationReason = CancellationReason.PASS_AWAY;
					logoutMap.put("cancellationReason", cancellationReason);
					logoutMap.put("deadReason", deadReason);
					logoutMap.put("deadDate", deadDate);
					logoutMap.put("deadFlag", YesNo.YES);
					Map<String, Object> logoutReqMap = new HashMap<String, Object>();
					logoutReqMap.put("body", logoutMap);
					hrService.doLogoutAllRecords(logoutReqMap, res, dao, ctx);
				}
				if ("2".equals(terminatedReason)) {// 迁出
					// 注销全档及随访
					cancellationReason = CancellationReason.EMIGRATION;
					logoutMap.put("cancellationReason", cancellationReason);
					Map<String, Object> logoutReqMap = new HashMap<String, Object>();
					logoutReqMap.put("body", logoutMap);
					hrService.doLogoutAllRecords(logoutReqMap, res, dao, ctx);
				}
			} else {// 只注销本档及随访
				String cancellationReason = CancellationReason.OTHER;
				try {
					thrModel.logoutTumourHighRiskRecord(THRID,
							cancellationReason);
				} catch (ModelDataOperationException e) {
					logger.error("logout tumour high risk record failure.", e);
					throw new ServiceException(e);
				}
				vLogService.saveVindicateLog(MDC_TumourHighRisk, "3", THRID,
						dao, empiId);
				VisitPlanModel vpModel = new VisitPlanModel(dao);
				try {
					vpModel.logOutVisitPlan(vpModel.RECORDID, THRID,
							BusinessType.THR);
				} catch (ModelDataOperationException e) {
					logger.error("logout tumour high risk visit plan failure.",
							e);
					throw new ServiceException(e);
				}
			}
		} else {
			// 生成新的随访计划
			try {
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("empiId", empiId);
				paraMap.put("THRID", reqBodyMap.get("THRID"));
				paraMap.put("recordId", reqBodyMap.get("THRID"));
				Application app = ApplicationUtil
						.getApplication(Constants.UTIL_APP_ID);
				String planMode = (String) app.getProperty(BusinessType.THR
						+ "_planMode");
				if (PlanMode.BY_RESERVED.equals(planMode)) {
					paraMap.put("createDate", reqBodyMap.get("nextDate"));
				} else {
					paraMap.put("createDate", reqBodyMap.get("visitDate"));
				}
				paraMap.put("highRiskType", reqBodyMap.get("highRiskType"));
				paraMap.put("managerGroup", fixGroup);
				thrModel.hasPlanType(BusinessType.THR, fixGroup,
						(String) reqBodyMap.get("highRiskType"));
				thrModel.createVisitPlan(paraMap, visitPlanCreator);
			} catch (ModelDataOperationException e) {
				logger.error("Generate visit plan failed.", e);
				throw new ServiceException(e);
			} catch (ControllerException e) {
				logger.error("Get visit configuration parameter failure.", e);
				throw new ServiceException(e);
			}
		}

		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:分页加载肿瘤高危随访计划列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-3-17 下午4:21:36
	 * @Modify:
	 */
	public void doLoadTHRVistPlanListView(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TumourHighRiskVisitModel thrvModel = new TumourHighRiskVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = thrvModel.loadTHRVisitPlanPageList(req);
		} catch (ModelDataOperationException e) {
			logger.error(
					"paging and querying tumour high risk visit plan records failure.",
					e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
}
