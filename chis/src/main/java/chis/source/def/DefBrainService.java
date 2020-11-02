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

public class DefBrainService extends DefService {
	private static final Logger logger = LoggerFactory
			.getLogger(DefBrainService.class);
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
	protected void doGetBrainDeformityRecordList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException {
		String empiId = (String) req.get("empiId");
		DefBrainModel dlm = new DefBrainModel(dao);
		List<Map<String, Object>> list = SchemaUtil
				.setDictionaryMessageForList(dlm.getDeformityRecordList(empiId,
						DEF_BrainDeformityRecord), DEF_BrainDeformityRecord);
		Map<String,Object> healthMap = dlm.getHealthRecord(empiId);
		Map<String,Object> m = SchemaUtil.setDictionaryMessageForForm(healthMap, EHR_HealthRecord);
		for(int i = 0;i<list.size();i++){
			Map<String,Object> map = list.get(i);
			map.put("homeAddress", ((Map<String,Object>)m.get("regionCode")).get("text"));
			int count = dlm.getTrainingEvaluateCount((String)map.get("id"),DEF_BrainTrainingEvaluate);
			map.put("trainingEvaluateCount", count);
		}
		ControlRunner.run(DEF_BrainDeformityRecord, list, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
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
	protected void doInitializeBrainDeformityRecordData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException{
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> ids = (Map<String, Object>) body.get("ids");
		String empiId = (String) ids.get("empiId");
		DefBrainModel dlm = new DefBrainModel(dao);
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
		ControlRunner.run(DEF_BrainDeformityRecord, resBody, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
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
	protected void doSaveBrainDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		dlm.updatePastHistory(body,DEF_BrainDeformityRecord);
		Map<String, Object> m = dlm.saveDeformityRecord(op, DEF_BrainDeformityRecord, body, true);
		String id = (String) body.get("id");
		String empiId = (String) body.get("empiId");
		if("create".equals(id)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_BrainDeformityRecord, op, id, dao, empiId);
		vLogService.saveRecords("CANN", op, dao, empiId);
		m.putAll(body);
		ControlRunner.run(DEF_BrainDeformityRecord, m, ctx,new String[]{ControlRunner.CREATE,ControlRunner.UPDATE});
		res.put("body", m);
		if(op.equals("create")){
			dlm.insertDefEvaluateWorkList(m,DEF_BrainDeformityRecord);
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
	protected void doGetBrainTrainingEvaluateList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefBrainModel dlm = new DefBrainModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingEvaluateList(defId,
						DEF_BrainTrainingEvaluate), DEF_BrainTrainingEvaluate);
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
	protected void doLoadBrainTrainingEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> r = (Map<String, Object>) body.get("r");
		
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingEvaluateData((String)r.get("id"), DEF_BrainTrainingEvaluate), DEF_BrainTrainingEvaluate) ;
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
	protected void doLoadBrainMiddleEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("id");
		
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadMiddleEvaluateData(id, DEF_BrainMiddleEvaluate), DEF_BrainMiddleEvaluate) ;
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
	protected void doSaveBrainTrainingEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingEvaluate(op, DEF_BrainTrainingEvaluate, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_BrainTrainingEvaluate, op, id, dao);
		
		if(op.equals("update")){
			m.put("id", body.get("id"));
		}
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefEvaluateWorkList(body, DEF_BrainDeformityRecord);
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
	protected void doSaveBrainMiddleEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> m = dlm.saveMiddleEvaluate(op, DEF_BrainMiddleEvaluate, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_BrainMiddleEvaluate, op, id, dao);
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
	protected void doGetBrainTrainingPlanList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefBrainModel dlm = new DefBrainModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanList(defId,
						DEF_BrainTrainingPlan), DEF_BrainTrainingPlan);
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
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> plan = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingPlanData(id,DEF_BrainTrainingPlan),DEF_BrainTrainingPlan);
		resBody.put(DEF_BrainTrainingPlan+Constants.DATAFORMAT4FORM, plan);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanRecordList(id,
						DEF_BrainTrainingRecord), DEF_BrainTrainingRecord);
		resBody.put(DEF_BrainTrainingRecord+Constants.DATAFORMAT4LIST, list);
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
	protected void doSaveBrainTrainingPlan(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingPlan(op, DEF_BrainTrainingPlan, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_BrainTrainingPlan, op, id, dao);
		m.putAll(body);
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_BrainDeformityRecord);
			dlm.insertDefPlanWorkList(body, DEF_BrainDeformityRecord);
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
	protected void doSaveBrainTrainingRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m  = dlm.saveTrainingRecord(op, DEF_BrainTrainingRecord, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_BrainTrainingRecord, op, id, dao);
		m.putAll(SchemaUtil.setDictionaryMessageForForm(body,DEF_BrainTrainingRecord));
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_BrainDeformityRecord);
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
	protected void doLoadBrainTrainingRecord(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("initDataId");
		
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingRecord(id, DEF_BrainTrainingRecord), DEF_BrainTrainingRecord) ;
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
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> resBody ;
		resBody = dlm.whetherCanClose(id, "Brain");
		
		Map<String,Object> map = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluateData(id, DEF_BrainTerminalEvaluate), DEF_BrainTerminalEvaluate);
		resBody.put(DEF_BrainTerminalEvaluate+Constants.DATAFORMAT4FORM, map);
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
	protected void doSaveBrainTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		Map<String,Object> m  = dlm.saveTerminalEvaluate(op, DEF_BrainTerminalEvaluate, body, true);
		String id = (String) body.get("defId");
		try{
			dlm.updateRecordCloseFlag(body,DEF_BrainDeformityRecord);
		}catch (ModelDataOperationException e) {
			logger.error("save BrainTerminalEvaluate failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", m);
		vLogService.saveVindicateLog(DEF_BrainTerminalEvaluate, op, id, dao, empiId);
		
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
	protected void doSaveLogoutBrainDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		DefBrainModel dlm = new DefBrainModel(dao);
		try {
			if(StringUtils.trimToEmpty((String)body.get("cancellationReason")).equals("6")){
				dlm.logoutSingleDeformityRecord(body, cancellationReason, deadReason, DEF_BrainDeformityRecord);
			}else{
				dlm.logoutDeformityRecord(body, cancellationReason, deadReason, DEF_BrainDeformityRecord);
			}
			
		} catch (ModelDataOperationException e) {
			logger.error("Logout Brain record failed.", e);
			throw new ServiceException(e);
		}
		String id=(String) body.get("id");
		vLogService.saveVindicateLog(DEF_BrainDeformityRecord, "3", id, dao, empiId);
		vLogService.saveRecords("CANN", "logout", dao, empiId);
		
		if(cancellationReason.equals(CancellationReason.CANCELLATION)){
			try{
				dlm.deletePastHistory(empiId, cancellationReason, deadReason, DEF_BrainDeformityRecord);
			}catch (ModelDataOperationException e) {
				logger.error("Logout Brain record failed.", e);
				throw new ServiceException(e);
			}
		}
		
		res.put("body", body);
	}
	
	@SuppressWarnings("unchecked")
	protected void doQueryBrainTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException, ExpException{
		List<Object> cnd = (List<Object>) req.get("cnd");
		
		DefBrainModel dlm = new DefBrainModel(dao);
		Map<String, Object> m = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluate(cnd, DEF_BrainTerminalEvaluate), DEF_BrainTerminalEvaluate) ;
		res.put("body", m);
	}
	
}
