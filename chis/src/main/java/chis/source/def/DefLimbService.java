package chis.source.def;

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
import chis.source.control.ControlRunner;
import chis.source.dic.CancellationReason;
import chis.source.empi.EmpiModel;
import chis.source.util.SchemaUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public class DefLimbService extends DefService {
	private static final Logger logger = LoggerFactory
			.getLogger(DefLimbService.class);
	/**
	 * 获取残疾登记记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doGetLimbDeformityRecordList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException {
		String empiId = (String) req.get("empiId");
		DefLimbModel dlm = new DefLimbModel(dao);
		List<Map<String, Object>> list = SchemaUtil
				.setDictionaryMessageForList(dlm.getDeformityRecordList(empiId,
						DEF_LimbDeformityRecord), DEF_LimbDeformityRecord);
		Map<String,Object> healthMap = dlm.getHealthRecord(empiId);
		Map<String,Object> m = SchemaUtil.setDictionaryMessageForForm(healthMap, EHR_HealthRecord);
		for(int i = 0;i<list.size();i++){
			Map<String,Object> map = list.get(i);
			map.put("homeAddress", ((Map<String,Object>)m.get("regionCode")).get("text"));
			int count = dlm.getTrainingEvaluateCount((String)map.get("id"),DEF_LimbTrainingEvaluate);
			map.put("trainingEvaluateCount", count);
		}
		ControlRunner.run(DEF_LimbDeformityRecord, list, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
		res.put("body", list);
	}
//	}
	/**
	 * 初始化残疾人登记页面数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doInitializeLimbDeformityRecordData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException{
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> ids = (Map<String, Object>) body.get("ids");
		String empiId = (String) ids.get("empiId");
		DefLimbModel dlm = new DefLimbModel(dao);
		//获取家庭地址
		Map<String,Object> healthMap = dlm.getHealthRecord(empiId);
		Map<String,Object> resBody = new HashMap<String, Object>();
		resBody.put("empiId", empiId);
		if(healthMap != null){
			Map<String,Object> m = SchemaUtil.setDictionaryMessageForForm(healthMap, EHR_HealthRecord);
			String regionCode = (String) ((Map<String,Object>)m.get("regionCode")).get("key");
			//获取父母名字
			String parentName = dlm.getParentName(regionCode);
			resBody.putAll(m);
			resBody.put("homeAddress", ((Map<String,Object>)m.get("regionCode")).get("text"));
			resBody.put("parentName", parentName);
		}
		
		EmpiModel em = new EmpiModel(dao);
		Map<String,Object> mpiMap = em.getEmpiInfoByEmpiid(empiId);
		resBody.put("contactPhone", mpiMap.get("contactPhone"));
		
		res.put("body", resBody);
		ControlRunner.run(DEF_LimbDeformityRecord, resBody, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
	}
	/**
	 * 保存残疾登记页面的数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		dlm.updatePastHistory(body,DEF_LimbDeformityRecord);
		Map<String, Object> m = dlm.saveDeformityRecord(op, DEF_LimbDeformityRecord, body, true);
		String id = (String) body.get("id");
		String empiId = (String) body.get("empiId");
		if("create".equals(id)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_LimbDeformityRecord, op, id, dao, empiId);
		vLogService.saveRecords("CANZT", op,dao, empiId);
		m.putAll(body);
		ControlRunner.run(DEF_LimbDeformityRecord, m, ctx,new String[]{ControlRunner.CREATE,ControlRunner.UPDATE});
		res.put("body", m);
		if(op.equals("create")){
			dlm.insertDefEvaluateWorkList(m,DEF_LimbDeformityRecord);
		}
	}
	
	
	
	/**
	 * 获取训练评估列表数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	protected void doGetLimbTrainingEvaluateList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefLimbModel dlm = new DefLimbModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingEvaluateList(defId,
						DEF_LimbTrainingEvaluate), DEF_LimbTrainingEvaluate);
		res.put("body", list);
	}
	
	/**
	 * 获取训练评估页面数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLimbTrainingEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> r = (Map<String, Object>) body.get("r");
		
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingEvaluateData((String)r.get("id"), DEF_LimbTrainingEvaluate), DEF_LimbTrainingEvaluate) ;
		res.put("body", resBody);
	}
	/**
	 * 获取训练评估中期总结页面数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLimbMiddleEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("id");
		
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadMiddleEvaluateData(id, DEF_LimbMiddleEvaluate), DEF_LimbMiddleEvaluate) ;
		res.put("body", resBody);
	}
	
	/**
	 * 保存训练评估数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbTrainingEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingEvaluate(op, DEF_LimbTrainingEvaluate, body, true); 
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_LimbTrainingEvaluate, op, id, dao);
		
		if(op.equals("update")){
			m.put("id", body.get("id"));
		}
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefEvaluateWorkList(body, DEF_LimbDeformityRecord);
		}
	}
	
	/**
	 * 保存中期总结数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbMiddleEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> m = dlm.saveMiddleEvaluate(op, DEF_LimbMiddleEvaluate, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_LimbMiddleEvaluate, op, id, dao);
		if(op.equals("update")){
			m.put("id", body.get("id"));
		}
		res.put("body", m);
	}
	
	/**
	 * 获取训练计划列表数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	protected void doGetLimbTrainingPlanList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefLimbModel dlm = new DefLimbModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanList(defId,
						DEF_LimbTrainingPlan), DEF_LimbTrainingPlan);
		res.put("body", list);
	}
	
	/**
	 * 获取训练计划页面数据和训练记录列表数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetTrainingPlanDataAndRecordList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> resBody = new HashMap<String,Object>();
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) r.get("id");
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> plan = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingPlanData(id,DEF_LimbTrainingPlan),DEF_LimbTrainingPlan);
		resBody.put(DEF_LimbTrainingPlan+Constants.DATAFORMAT4FORM, plan);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanRecordList(id,
						DEF_LimbTrainingRecord), DEF_LimbTrainingRecord);
		resBody.put(DEF_LimbTrainingRecord+Constants.DATAFORMAT4LIST, list);
		res.put("body", resBody);
	}
	/**
	 * 保存训练计划数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbTrainingPlan(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingPlan(op, DEF_LimbTrainingPlan, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_LimbTrainingPlan, op, id, dao);
		m.putAll(body);
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_LimbDeformityRecord);
			dlm.insertDefPlanWorkList(body, DEF_LimbDeformityRecord);
		}
	}
	/**
	 * 保存训练记录数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbTrainingRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m  = dlm.saveTrainingRecord(op, DEF_LimbTrainingRecord, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_LimbTrainingRecord, op, id, dao);
		m.putAll(SchemaUtil.setDictionaryMessageForForm(body,DEF_LimbTrainingRecord));
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_LimbDeformityRecord);
		}
	}
	
	/**
	 * 获取训练计划数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadLimbTrainingRecord(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("initDataId");
		
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingRecord(id, DEF_LimbTrainingRecord), DEF_LimbTrainingRecord) ;
		res.put("body", resBody);
	}
	/**
	 * 判断是否可以结案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	protected void doWhetherCanClose(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws PersistentDataOperationException, ModelDataOperationException {
		String id = (String) req.get("id");
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> resBody ;
		resBody = dlm.whetherCanClose(id, "Limb");
		
		Map<String,Object> map = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluateData(id, DEF_LimbTerminalEvaluate), DEF_LimbTerminalEvaluate);
		resBody.put(DEF_LimbTerminalEvaluate+Constants.DATAFORMAT4FORM, map);
		res.put("body", resBody);
		
	}
	
	/**
	 * 保存结案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLimbTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) body.get("empiId");
		Map<String,Object> m  = dlm.saveTerminalEvaluate(op, DEF_LimbTerminalEvaluate, body, true);
		String id = (String) body.get("defId");
		try{
			dlm.updateRecordCloseFlag(body,DEF_LimbDeformityRecord);
		}catch (ModelDataOperationException e) {
			logger.error("save LimbTerminalEvaluate failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", m);
		vLogService.saveVindicateLog(DEF_LimbDeformityRecord, op, id, dao, empiId);
		
	}
	
	/**
	 * 注销档案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveLogoutLimbDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		DefLimbModel dlm = new DefLimbModel(dao);
		try {
			if(StringUtils.trimToEmpty((String)body.get("cancellationReason")).equals("6")){
				dlm.logoutSingleDeformityRecord(body, cancellationReason, deadReason, DEF_LimbDeformityRecord);
			}else{
				dlm.logoutDeformityRecord(body, cancellationReason, deadReason, DEF_LimbDeformityRecord);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Logout limb record failed.", e);
			throw new ServiceException(e);
		}
		String id=(String) body.get("id");
		vLogService.saveVindicateLog(DEF_LimbDeformityRecord, "3", id, dao, empiId);
		vLogService.saveRecords("CANZT", "logout",dao, empiId);
		
		if(cancellationReason.equals(CancellationReason.CANCELLATION)){
			try{
				dlm.deletePastHistory(empiId, cancellationReason, deadReason, DEF_LimbDeformityRecord);
			}catch (ModelDataOperationException e) {
				logger.error("Logout limb record failed.", e);
				throw new ServiceException(e);
			}
		}
		
		res.put("body", body);
	}
	
	@SuppressWarnings("unchecked")
	protected void doQueryLimbTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException, ExpException{
		List<Object> cnd = (List<Object>) req.get("cnd");
		
		DefLimbModel dlm = new DefLimbModel(dao);
		Map<String, Object> m = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluate(cnd, DEF_LimbTerminalEvaluate), DEF_LimbTerminalEvaluate) ;
		res.put("body", m);
	}
	
}
