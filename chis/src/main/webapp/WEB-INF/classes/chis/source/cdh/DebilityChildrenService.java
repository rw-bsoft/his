/**
 * @(#)DebilityChildrenService.java Created on 2012-1-12 下午4:03:19
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.empi.EmpiModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 体弱儿档案服务
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class DebilityChildrenService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(DebilityChildrenService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 检测是否存在体弱儿档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCheckDebilityChildrenRecordExists(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmpiModel em = new EmpiModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		boolean recordExists = false;
		try {
			recordExists = em
					.isRecordExists(CDH_DebilityChildren, empiId, true);
		} catch (ModelDataOperationException e) {
			logger.error("Check existance of Debility children record failed.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		resBody.put("recordExists", recordExists);
	}

	/**
	 * 体弱儿童建档数据初始化。
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doDocCreateInitialization(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String phrId = (String) reqBody.get("phrId");
		Map<String, Object> childRecord;
		try {
			childRecord = chm.getChildHealthCardByPhrId(phrId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Initialize creatation of debility children record failed.",
					e);
			throw new ServiceException(e);
		}
		if (childRecord == null) {
			logger.error("Initialization data of debility children record not found.");
			res.put(RES_CODE, ServiceCode.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "初始化体弱儿档案数据失败。");
			return;
		}
		res.put("body", SchemaUtil.setDictionaryMessageForForm(childRecord,
				CDH_HealthCard));
	}

	/**
	 * 体弱儿童随访初始信息。
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	protected void doVisitInitialization(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String recordId = (String) reqBody.get("recordId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			// chb 获取指导意见。
			String correction = dcm.getCorrection(recordId);
			resBody.put("correction", correction);
			// chb 获取下一次随访计划的开始日期
			String thisPlanDate = (String) reqBody.get("planDate");
			VisitPlanModel vpm = new VisitPlanModel(dao);
			Date nextBeginDate = vpm.getNextPlanDate(recordId, thisPlanDate,
					BusinessType.CD_DC);
			if (nextBeginDate == null) {
				Calendar c = Calendar.getInstance();
				c.setTime(BSCHISUtil.toDate(thisPlanDate));
				c.add(Calendar.MONTH, 1);
				nextBeginDate = c.getTime();
			}
			resBody.put("nextBeginDate",
					BSCHISUtil.toString(nextBeginDate, null));
			// chb 获取最近一次儿童体格检查的身高体重
			String planId = vpm.getLastVisitedPlanId(recordId,
					BusinessType.CD_CU);
			if(planId!=null && planId.trim().length()>0){
				Map<String, Object> plan = vpm.getPlan(planId);
				int monthAge = Integer.parseInt((String) plan.get("extend1"));
				String schemaName = null;
				if (monthAge < 1) {
					schemaName = BSCHISEntryNames.CDH_CheckupInOne;
				} else if (monthAge < 3) {
					schemaName = BSCHISEntryNames.CDH_CheckupOneToTwo;
				} else {
					schemaName = BSCHISEntryNames.CDH_CheckupThreeToSix;
				}
				String checkupId = (String) plan.get("visitId");
				ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
				Map<String, Object> checkinfo = ccm.getCheckupRecord(schemaName,
						checkupId);
				if (checkinfo != null && !checkinfo.isEmpty()) {
					resBody.put("weight", checkinfo.get("weight"));
					resBody.put("height", checkinfo.get("height"));
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Init visit information failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

	/**
	 * 保存体弱儿童档案。
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDebilityChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmpiModel empi = new EmpiModel(dao);
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		try {
			int age = empi.getAge(empiId);
			String childrenRegisterAge = null;
			try {
				 childrenRegisterAge =ApplicationUtil.getProperty(Constants.UTIL_APP_ID, "childrenRegisterAge");
			} catch (ControllerException e1) {
				throw new ServiceException(e1);
			}
			if (age > Integer.parseInt(childrenRegisterAge)) {
				res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
				res.put(RES_MESSAGE, "该儿童已超过" + childrenRegisterAge
						+ "周岁，不可再建体弱儿档案。");
				return;
			}
			if (op.equals("create")) {
				reqBody.put("planTypeCode", "-1"); // @@ 建档时预置-1值。
			}
			dcm.saveDebilityChildrenRecord(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Save children debility record failed.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) reqBody.get("recordId");
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			recordId = (String) resBody.get("recordId");
			if (recordId != null) {
				reqBody.put("recordId", recordId);
			}
			String planTypeCode = createVisitPlan(reqBody, BusinessType.CD_DC,
					ctx);
			if (planTypeCode != null) {
				try {
					dcm.updateDebilityPlanType(planTypeCode, empiId);
				} catch (ModelDataOperationException e) {
					logger.error(
							"update children debility record planTypeCode  failed.",
							e);
					throw new ServiceException(e);
				}
			}
		}
		vLogService.saveVindicateLog(CDH_DebilityChildren, op, recordId, dao, empiId);
		vLogService.saveRecords("RUO", op, dao, empiId);
	}

	/**
	 * 体弱儿童档案结案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCloseDebilityChildrenRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String recordId = (String) reqBody.get("recordId");
		String empiId = (String) reqBody.get("empiId");
		try {
			dcm.closeDebilityRecord(recordId);
		} catch (ModelDataOperationException e) {
			logger.error("close children debility record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_DebilityChildren, "7", recordId, dao, empiId);
	}

	/**
	 * 保存体弱儿随访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDebilityChildrenVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String visitDate = (String) reqBody.get("visitDate");
		String empiId = (String) reqBody.get("empiId");
		String visitId = (String) reqBody.get("visitId");
		Object ht = reqBody.get("length");
		Object wt = reqBody.get("weight");
		try {
			Double height = null;
			if (ht != null && !ht.toString().equals("")) {
				height = (Double) SchemaUtil.getValue(CDH_CheckupInOne,
						"height", ht);
			}
			Double weight = null;
			if (wt != null && !wt.toString().equals("")) {
				weight = (Double) SchemaUtil.getValue(CDH_CheckupInOne,
						"weight", wt);
			}
			EmpiModel empi = new EmpiModel(dao);
			Map<String, Object> data = empi.getSexAndBirthday(empiId);
			if (data != null) {
				String sex = (String) data.get("sexCode");
				Date birthday = (Date) data.get("birthday");
				Map<String, Object> wh = dcm.getWH(height, weight, sex);
				if (wh == null) {
					wh = new HashMap<String, Object>();
					wh.put("appraiseWH", "");
				}
				reqBody.putAll(wh);
				int age = BSCHISUtil.getMonths(birthday,
						BSCHISUtil.toDate(visitDate));
				Map<String, Object> why = dcm.getWHY(height, weight, sex, age);
				if (why == null) {
					why = new HashMap<String, Object>();
					why.put("appraiseWY", "");
					why.put("appraiseHY", "");
				}
				reqBody.putAll(why);
			}
			dcm.saveDebilityChildrenVisit(reqBody, op, res);
			HashMap<String, Object> resBody = (HashMap<String, Object>) res
					.get("body");
			if (op.equals("create")) {
				visitId = (String) resBody.get("visitId");
				reqBody.put("visitId", visitId);
			}
			vLogService.saveVindicateLog(CDH_DebilityChildrenVisit, op, visitId, dao, empiId);
			resBody.putAll(SchemaUtil.setDictionaryMessageForForm(reqBody,
					CDH_DebilityChildrenVisit));
			HashMap<String, Object> upBody = new HashMap<String, Object>();
			upBody.put("planId", reqBody.get("planId"));
			upBody.put("visitDate", reqBody.get("visitDate"));
			upBody.put("visitId", reqBody.get("visitId"));
			upBody.put("planStatus", PlanStatus.VISITED);
			upBody.put("lastModifyUser",
					 UserRoleToken.getCurrent().getUserId());
			upBody.put("lastModifyDate", BSCHISUtil.toString(new Date(), null));
			VisitPlanModel vpm = new VisitPlanModel(dao);
			vpm.saveVisitPlan("update", upBody);
			Map<String, Object> record = dcm
					.getDebilityChildrenRecordByRecordId((String) reqBody
							.get("recordId"));
			reqBody.put("debilityReason", record.get("debilityReason"));
			createVisitPlan(reqBody, BusinessType.CD_DC, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("save children debility visit record failed.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 保存体弱儿随访化验信息列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDebilityChildrenVisitCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		Map<String, Object> reqBody = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String visitId = (String) reqBody.get("visitId");
		try {
			if (op.equals("update")) {
				dcm.deleteCheckList(visitId);
			}
			ArrayList<HashMap<String, Object>> chkLst = (ArrayList<HashMap<String, Object>>) reqBody
					.get("checkList");
			if (chkLst == null || chkLst.size() < 1) {
				return;
			}
			for (HashMap<String, Object> check : chkLst) {
				check.put("visitId", visitId);
				check.put("empiId", reqBody.get("empiId"));
				check.put("recordId", reqBody.get("recordId"));
				dcm.saveCheck(check);
			}
			vLogService.saveVindicateLog(CDH_DebilityChildrenCheck, "1", visitId, dao, (String)reqBody.get("empiId"));
		} catch (ModelDataOperationException e) {
			logger.error("Saving check data failed.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 获取上一个年度的随访计划。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	protected void doGetPreYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			res.put("body", dcm.getVisitPlan(req, 1));
		} catch (ModelDataOperationException e) {
			logger.error("faild to get debility children visit plan.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取本年度的随访计划。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	protected void doGetCurYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			res.put("body", dcm.getVisitPlan(req, 2));
		} catch (ModelDataOperationException e) {
			logger.error("faild to get debility children visit plan.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销体弱儿档案(单条)
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogOutDebilityChildrenByRecordId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String empiId = (String) data.get("empiId");
		String recordId = (String) data.get("recordId");
		if (recordId == null || recordId.equals("")) {
			return;
		}
		String deadReason = StringUtils.trimToEmpty((String) data
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) data
				.get("cancellationReason"));
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			dcm.logOutDebilityChildrenRecord("recordId", recordId,
					cancellationReason, deadReason);
			vpm.logOutVisitPlan(vpm.RECORDID, recordId, BusinessType.CD_DC);
		} catch (ModelDataOperationException e) {
			logger.error("logout debility children  record error .", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(CDH_DebilityChildren, "3", recordId, dao, empiId);
		vLogService.saveRecords("RUO", "logout", dao, empiId);
	}

	/**
	 * 注销体弱儿档案(全部)
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogOutDebilityChildrenByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String empiId = (String) data.get("empiId");
		if (empiId == null || empiId.equals("")) {
			return;
		}
		String deadReason = StringUtils.trimToEmpty((String) data
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) data
				.get("cancellationReason"));
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			dcm.logOutDebilityChildrenRecord("empiId", empiId,
					cancellationReason, deadReason);
			vpm.logOutVisitPlan(vpm.EMPIID, empiId, BusinessType.CD_DC);
		} catch (ModelDataOperationException e) {
			logger.error("logout debility children  record error .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取下一个年度的随访计划。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	protected void doGetNextYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			res.put("body", dcm.getVisitPlan(req, 3));
		} catch (ModelDataOperationException e) {
			logger.error("faild to get debility children visit plan.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据疾病类型获取体弱儿指导意见数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetChildCorrection(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String diseaseType = (String) data.get("diseaseType");
		if (diseaseType == null || diseaseType.equals("")) {
			return;
		}
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			Map<String, Object> map = dcm.getChildCorrection(diseaseType);
			if (map == null || map.size() < 1) {
				return;
			}
			res.put("body", SchemaUtil.setDictionaryMessageForForm(map,
					CDH_DebilityCorrectionDic));
		} catch (ModelDataOperationException e) {
			logger.error("faild to get debility children correction  plan.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 检验下次随访计划是否已经随访过
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetNextPlanVisited(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String planId = (String) data.get("planId");
		String recordId = (String) data.get("recordId");
		DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
		try {
			boolean result = dcm.getNextPlanVisited(planId, recordId);
			res.put("nextPlanVisted", result);
		} catch (ModelDataOperationException e) {
			logger.error("faild to check next plan  visited.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 生成随访计划
	 * 
	 * @param body
	 * @param businessType
	 *            计划类型
	 * @param ctx
	 * @throws ServiceException
	 */
	private String createVisitPlan(Map<String, Object> body,
			String businessType, Context ctx) throws ServiceException {
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
		} catch (Exception e) {
			throw new ServiceException("生成计划失败!", e);
		}

	}

}
