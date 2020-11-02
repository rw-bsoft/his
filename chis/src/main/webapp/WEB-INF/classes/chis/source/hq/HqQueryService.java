package chis.source.hq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bsoft.mpi.util.SchemaUtil;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HqQueryService extends AbstractActionService implements DAOSupportable{
	
	/**
	 * 查询健康高危人群
	 * @param req
	 * @param res
	 * @param ctx
	 * @param dao
	 * @throws ServiceException
	 */
	public void doQueryHealthCheckPeople(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		HqQueryModel hqModule = new HqQueryModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hqModule.doQueryHealthCheckPeople(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	public void doQueryHealthCheckNextPeople(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		HqQueryModel hqModule = new HqQueryModel(dao);
		Map<String, Object> rsMap = null;
		req.put("needcheck", "1");//需要过滤数据标记
		try {
			rsMap = hqModule.doQueryHealthCheckPeople(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	public void doLoadHighRiskRecord(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		String phrid=req.get("PHRID")+"";
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrid);
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			data=dao.doLoad(cnd, MDC_HighRiskRecord);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if(data!=null && data.size() >0 ){
			data=SchemaUtil.setDictionaryMassageForForm(data, MDC_HighRiskRecord);
			res.put("data",data);
		}
	}
	public void doSaveHighRiskRecord(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		Map<String, Object> savedata=(Map<String, Object>)req.get("savedata");
		String phrid=savedata.get("phrId")+"";
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrid);
		Map<String, Object> data=new HashMap<String, Object>();
		try {
			data=dao.doLoad(cnd, MDC_HighRiskRecord);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if(data!=null && data.size() >0 ){
			try {
				dao.doSave("update", MDC_HighRiskRecord, savedata, true);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}else{
			try {
				dao.doSave("create", MDC_HighRiskRecord, savedata, true);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}
	public void doGetPreYearVisitPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.HIGH_RISK;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(1, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高危上年度随访计划失败！");
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
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = 0;
		String instanceType = BusinessType.HIGH_RISK;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(2, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高危本年度随访计划失败！");
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
		String empiId = StringUtils.trimToEmpty((String) req.get("empiId"));
		int current = (Integer) req.get("current");
		String instanceType = BusinessType.HIGH_RISK;
		List<Map<String, Object>> rsList = null;
		try {
			VisitPlanModel vpm = new VisitPlanModel(dao);
			rsList = vpm.getVisitPlan(3, empiId, current, instanceType);
			res.put("body", rsList);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高危下年度随访计划失败！");
			throw new ServiceException(e);
		}
	}
	//2018-1-16-保存高危随访
	public void doSaveHighRiskVisit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HqQueryModel hq=new HqQueryModel(dao);
		try {
			hq.doSaveHighRiskVisit(req, res, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doAddHighRiskPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> plandata=(Map<String, Object>)req.get("plandata");
		HqQueryModel hq=new HqQueryModel(dao);
		try {
			hq.doInsertHighRiskPlan(plandata, res, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	public void doDeleteHighRiskPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> plandata=(Map<String, Object>)req.get("plandata");
		String planId=plandata.get("planId")+"";
		if(planId.length()<10){
			res.put("code","400");
			res.put("msg","传入信息有误，无法删除数据！");
			return;
		}
		String sql="select visitId as VISITID from PUB_VisitPlan where planId=:planId";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("planId", planId);
		try {
			Map<String, Object> data=dao.doSqlLoad(sql, p);
			if(data!=null){
				String visitId=data.get("VISITID")==null?"":data.get("VISITID")+"";
				if(visitId.length() >0){
					res.put("code","400");
					res.put("msg","该计划已有随访记录，无法删除该计划！");
				}else{
					String delsql="delete from PUB_VisitPlan where planId=:planId";
					dao.doUpdate(delsql, p);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询高危人群管理
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryHighRiskRecordList(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		HqQueryModel hqModule = new HqQueryModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hqModule.doQueryHighRiskRecordList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	
	/**
	 * 
	 * @description 修改档案当前状态
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HqQueryModel hq = new HqQueryModel(dao);
		try {
			hq.doVerify(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
