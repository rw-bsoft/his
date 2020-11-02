/**
 * @(#)DiabetesService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.DateParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DiabetesRiskStatus;
import chis.source.dic.HypertensionRiskStatus;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesRiskService extends DiabetesService {
	private static final Logger logger = LoggerFactory
			.getLogger(DiabetesRiskService.class);

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

	
	public void doSaveConfirmDiabetesRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String riskId = (String) body.get("riskId");
		String empiId = (String) body.get("empiId");
		
		if(body.get("result").equals("1")){
			body.put("status", "1");
			UserRoleToken ur = UserRoleToken.getCurrent();
			body.put("confirmUser", ur.getUserId());
			body.put("confirmDate", new Date());
			body.put("confirmUnit", ur.getManageUnitId());
			dao.doSave("update", BSCHISEntryNames.MDC_DiabetesRisk, body, false);
			vLogService.saveVindicateLog(MDC_DiabetesRisk, "6", riskId, dao, empiId);
			
			
			Map<String, Object> workList = new HashMap<String, Object>();
			Calendar c = Calendar.getInstance();
			workList.put("recordId", body.get("phrId"));
			workList.put("empiId", body.get("empiId"));
			workList.put("otherId", body.get("riskId"));
			workList.put("beginDate", c.getTime());
			c.add(Calendar.YEAR, 1);
			workList.put("endDate", c.getTime());
			workList.put("count", 1);
			workList.put("doctorId", ur.getUserId());
			workList.put("manaUnitId", ur.getManageUnitId());
		}else{
			dao.doRemove((String) body.get("riskId"), BSCHISEntryNames.MDC_DiabetesRisk);
			vLogService.saveVindicateLog(MDC_DiabetesRisk, "4", riskId, dao, empiId);
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void doSaveEliminateDiabetesRisk(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx) throws ValidateException, PersistentDataOperationException{
//		//已经合并到上面的方法中
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doInitializeDiabetesRiskAssessment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException{
		Map body = (Map) req.get("body");
		Map m = dao.doLoad(BSCHISEntryNames.MPI_DemographicInfo, (String) body.get("empiId"));
		Map resBody = new HashMap();
//		int age = BSCHISUtil.calculateAge((Date) m.get("birthday"), new Date());
//		Map riskiness = new HashMap();
//		if(age >= 45){
//			riskiness.put("key", "01");
//		}
//		resBody.put("riskiness", riskiness);
		resBody.put("sexCode",m.get("sexCode"));
		resBody.put("birthday",m.get("birthday"));
		res.put("body", resBody);
	}
	
	@SuppressWarnings("unchecked")
	public void doSaveDiabetesRiskAssessment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, DateParseException, ModelDataOperationException, ControllerException, ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recordId = (String) body.get("recordId");
		String empiId = (String) body.get("empiId");
		Map<String, Object> m = dao.doSave(op, BSCHISEntryNames.MDC_DiabetesRiskAssessment, body, false);
		if("create".equals(op)){
			recordId = (String) m.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesRiskAssessment, op, recordId, dao, empiId);
		
		if(body.get("estimateType").equals("1") && req.get("op").equals("create") ){
			Application app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			String planTypeCode = (String) app.getProperty(BusinessType.DiabetesRisk+"_planTypeCode");
			Map<String,Object> planType = dao.doLoad(PUB_PlanType, planTypeCode);
			if(planType == null){
				throw new ModelDataOperationException(
						Constants.CODE_RECORD_NOT_FOUND, "糖尿病高危人群随访参数未设置");
			}
			Calendar c = Calendar.getInstance();
			c.setTime(BSCHISUtil.toDate((String) body.get("estimateDate")));
			body.put("planDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR, -Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_precedeDays")));
			body.put("beginDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_precedeDays")));
//			c.add(frequency, cycle);
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_delayDays")));
			body.put("endDate", c.getTime());
			body.put("planStatus", PlanStatus.NEED_VISIT);
			body.put("sn", 0);
			DiabetesRiskModel drm = new DiabetesRiskModel(dao);
			drm.createDiabetesRiskVisitPlan(body);
			drm.updateFirstAssessmentDate(body);
		}
		res.put("body", m);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doSaveDiabetesRiskVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, DateParseException, ControllerException, ServiceException{
		Map body = (Map) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) body.get("empiId");
		String visitId = (String) body.get("visitId");
		Map m  =dao.doSave(op, BSCHISEntryNames.MDC_DiabetesRiskVisit, body, false);
		if("create".equals(op)){
			visitId = (String) m.get("visitId");
		}
		vLogService.saveVindicateLog(MDC_DiabetesRiskVisit, op, visitId, dao, empiId);
		
		Map plan = new HashMap();
		if(req.get("op").equals("create") ){
			Application app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			String planTypeCode = (String) app.getProperty(BusinessType.DiabetesRisk+"_planTypeCode");
			Map<String,Object> planType = dao.doLoad(PUB_PlanType, planTypeCode);
			int frequency =Integer.valueOf((String) planType.get("frequency")) ;
			int cycle = (Integer) planType.get("cycle") ;
			
			Calendar c = Calendar.getInstance();
			c.setTime(BSCHISUtil.toDate((String) body.get("visitDate")));
			c.add(frequency, cycle);
			body.put("planDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR, -Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_precedeDays")));
			body.put("beginDate", c.getTime());
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_precedeDays")));
			c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app.getProperty(BusinessType.DiabetesRisk+"_delayDays")));
			body.put("endDate", c.getTime());
			body.put("planStatus", PlanStatus.NEED_VISIT);
			body.put("sn", (Integer)body.get("sn")+1);
			DiabetesRiskModel drm = new DiabetesRiskModel(dao);
			drm.createDiabetesRiskVisitPlan(body);
			
			plan.put("visitId",m.get("visitId"));
		}
		//更新计划状态
		
		plan.put("planId", body.get("planId"));
		plan.put("visitDate", body.get("visitDate"));
		plan.put("planStatus", PlanStatus.VISITED);
		dao.doSave("update", BSCHISEntryNames.PUB_VisitPlan, plan, false);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doSaveCloseDiabetesRisk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws PersistentDataOperationException, DateParseException, ServiceException{
		Map body = (Map) req.get("body");
		
		if(body.get("effect").equals("2")){
			DiabetesRecordModel recordModel = new DiabetesRecordModel(dao);
			Map<String, Object> workList = new HashMap<String, Object>();
			Calendar c = Calendar.getInstance();
			workList.put("recordId", body.get("phrId"));
			workList.put("empiId", body.get("empiId"));
			workList.put("beginDate", c.getTime());
			c.add(Calendar.YEAR, 1);
			workList.put("endDate", c.getTime());
			workList.put("workType", WorkType.MDC_DIABETESRECORD);
			workList.put("count", 1);
			UserRoleToken ur = UserRoleToken.getCurrent();
			workList.put("doctorId", ur.getUserId());
			workList.put("manaUnitId", ur.getManageUnitId());
			recordModel.addDiabetesRecordWorkList(workList);
		}
		DiabetesRiskModel drm = new DiabetesRiskModel(dao);
		drm.writeOffDiabetesRiskVisitPlan(body);
		
		UserRoleToken ur = UserRoleToken.getCurrent();
		body.put("closeUser", ur.getUserId());
		body.put("closeUnit", ur.getManageUnitId());
		body.put("closeDate", Calendar.getInstance().getTime());
		body.put("status", HypertensionRiskStatus.CLOSE);
		dao.doSave("update", BSCHISEntryNames.MDC_DiabetesRisk, body, false);
		res.put("body", body);
		String riskId = (String) body.get("riskId");
		String empiId = (String) body.get("empiId");
		vLogService.saveVindicateLog(MDC_DiabetesRisk, "7", riskId, dao, empiId);
	}
	
}
