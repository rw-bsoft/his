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

public class DefIntellectService extends DefService {
	private static final Logger logger = LoggerFactory
			.getLogger(DefIntellectService.class);
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
	protected void doGetIntellectDeformityRecordList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException {
		String empiId = (String) req.get("empiId");
		DefIntellectModel dlm = new DefIntellectModel(dao);
		List<Map<String, Object>> list = SchemaUtil
				.setDictionaryMessageForList(dlm.getDeformityRecordList(empiId,
						DEF_IntellectDeformityRecord), DEF_IntellectDeformityRecord);
		Map<String,Object> healthMap = dlm.getHealthRecord(empiId);
		Map<String,Object> m = SchemaUtil.setDictionaryMessageForForm(healthMap, EHR_HealthRecord);
		for(int i = 0;i<list.size();i++){
			Map<String,Object> map = list.get(i);
			map.put("homeAddress", ((Map<String,Object>)m.get("regionCode")).get("text"));
			int count = dlm.getTrainingEvaluateCount((String)map.get("id"),DEF_IntellectTrainingEvaluate);
			map.put("trainingEvaluateCount", count);
		}
		ControlRunner.run(DEF_IntellectDeformityRecord, list, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
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
	protected void doInitializeIntellectDeformityRecordData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException{
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> ids = (Map<String, Object>) body.get("ids");
		String empiId = (String) ids.get("empiId");
		DefIntellectModel dlm = new DefIntellectModel(dao);
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
		ControlRunner.run(DEF_IntellectDeformityRecord, resBody, ctx, new String[] {ControlRunner.CREATE,ControlRunner.UPDATE});
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
	protected void doSaveIntellectDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		dlm.updatePastHistory(body,DEF_IntellectDeformityRecord);
		Map<String, Object> m = dlm.saveDeformityRecord(op, DEF_IntellectDeformityRecord, body, true);
		String id = (String) body.get("id");
		String empiId = (String) body.get("empiId");
		if("create".equals(id)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_IntellectDeformityRecord, op, id, dao, empiId);
		vLogService.saveRecords("CANZL", op, dao, empiId);
		m.putAll(body);
		ControlRunner.run(DEF_IntellectDeformityRecord, m, ctx,new String[]{ControlRunner.CREATE,ControlRunner.UPDATE});
		res.put("body", m);
		if(op.equals("create")){
			dlm.insertDefEvaluateWorkList(m,DEF_IntellectDeformityRecord);
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
	protected void doGetIntellectTrainingEvaluateList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefIntellectModel dlm = new DefIntellectModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingEvaluateList(defId,
						DEF_IntellectTrainingEvaluate), DEF_IntellectTrainingEvaluate);
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
	protected void doLoadIntellectTrainingEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		Map<String,Object> r = (Map<String, Object>) body.get("r");
		
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingEvaluateData((String)r.get("id"), DEF_IntellectTrainingEvaluate), DEF_IntellectTrainingEvaluate) ;
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
	protected void doLoadIntellectMiddleEvaluateData(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("id");
		
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadMiddleEvaluateData(id, DEF_IntellectMiddleEvaluate), DEF_IntellectMiddleEvaluate) ;
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
	protected void doSaveIntellectTrainingEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingEvaluate(op, DEF_IntellectTrainingEvaluate, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_IntellectTrainingEvaluate, op, id, dao);
		
		if(op.equals("update")){
			m.put("id", body.get("id"));
		}
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefEvaluateWorkList(body, DEF_IntellectDeformityRecord);
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
	protected void doSaveIntellectMiddleEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> m = dlm.saveMiddleEvaluate(op, DEF_IntellectMiddleEvaluate, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_IntellectMiddleEvaluate, op, id, dao);
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
	protected void doGetIntellectTrainingPlanList(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String defId = (String) req.get("defId");
		DefIntellectModel dlm = new DefIntellectModel(dao);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanList(defId,
						DEF_IntellectTrainingPlan), DEF_IntellectTrainingPlan);
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
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> plan = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingPlanData(id,DEF_IntellectTrainingPlan),DEF_IntellectTrainingPlan);
		resBody.put(DEF_IntellectTrainingPlan+Constants.DATAFORMAT4FORM, plan);
		List<Map<String, Object>> list = SchemaUtil.setDictionaryMessageForList(dlm.getTrainingPlanRecordList(id,
						DEF_IntellectTrainingRecord), DEF_IntellectTrainingRecord);
		resBody.put(DEF_IntellectTrainingRecord+Constants.DATAFORMAT4LIST, list);
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
	protected void doSaveIntellectTrainingPlan(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m = dlm.saveTrainingPlan(op, DEF_IntellectTrainingPlan, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_IntellectTrainingPlan, op, id, dao);
		m.putAll(body);
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_IntellectDeformityRecord);
			dlm.insertDefPlanWorkList(body, DEF_IntellectDeformityRecord);
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
	protected void doSaveIntellectTrainingRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String,Object> m  = dlm.saveTrainingRecord(op, DEF_IntellectTrainingRecord, body, true);
		String id = (String) body.get("id");
		if("create".equals(op)){
			id = (String) m.get("id");
		}
		vLogService.saveVindicateLog(DEF_IntellectTrainingRecord, op, id, dao);
		m.putAll(SchemaUtil.setDictionaryMessageForForm(body,DEF_IntellectTrainingRecord));
		res.put("body", m);
		if(op.equals("create")){
			dlm.deleteDefPlanWorkList(body, DEF_IntellectDeformityRecord);
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
	protected void doLoadIntellectTrainingRecord(
			Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
//		Map<String,Object> r = (Map<String, Object>) body.get("r");
		String id = (String) body.get("initDataId");
		
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> resBody = SchemaUtil.setDictionaryMessageForForm(dlm.loadTrainingRecord(id, DEF_IntellectTrainingRecord), DEF_IntellectTrainingRecord) ;
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
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> resBody ;
		resBody = dlm.whetherCanClose(id, "Intellect");
		
		Map<String,Object> map = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluateData(id, DEF_IntellectTerminalEvaluate), DEF_IntellectTerminalEvaluate);
		resBody.put(DEF_IntellectTerminalEvaluate+Constants.DATAFORMAT4FORM, map);
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
	protected void doSaveIntellectTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) body.get("empiId");
		Map<String,Object> m  = dlm.saveTerminalEvaluate(op, DEF_IntellectTerminalEvaluate, body, true);
		String id = (String) body.get("defId");
		try{
			dlm.updateRecordCloseFlag(body,DEF_IntellectDeformityRecord);
		}catch (ModelDataOperationException e) {
			logger.error("save IntellectTerminalEvaluate failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", m);
		vLogService.saveVindicateLog(DEF_IntellectTerminalEvaluate, op, id, dao, empiId);
		
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
	protected void doSaveLogoutIntellectDeformityRecord(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String cancellationReason = (String) body.get("cancellationReason");
		String deadReason = (String) body.get("deadReason");

		DefIntellectModel dlm = new DefIntellectModel(dao);
		try {
			if(StringUtils.trimToEmpty((String)body.get("cancellationReason")).equals("6")){
				dlm.logoutSingleDeformityRecord(body, cancellationReason, deadReason, DEF_IntellectDeformityRecord);
			}else{
				dlm.logoutDeformityRecord(body, cancellationReason, deadReason, DEF_IntellectDeformityRecord);
			}
			
		} catch (ModelDataOperationException e) {
			logger.error("Logout Intellect record failed.", e);
			throw new ServiceException(e);
		}
		String id=(String) body.get("id");
		vLogService.saveVindicateLog(DEF_IntellectDeformityRecord, "3", id, dao, empiId);
		vLogService.saveRecords("CANZL", "logout", dao, empiId);
		
		if(cancellationReason.equals(CancellationReason.CANCELLATION)){
			try{
				dlm.deletePastHistory(empiId, cancellationReason, deadReason, DEF_IntellectDeformityRecord);
			}catch (ModelDataOperationException e) {
				logger.error("Logout Intellect record failed.", e);
				throw new ServiceException(e);
			}
		}
		
		res.put("body", body);
	}
	
	@SuppressWarnings("unchecked")
	protected void doQueryIntellectTerminalEvaluate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException, PersistentDataOperationException, ServiceException, ExpException{
		List<Object> cnd = (List<Object>) req.get("cnd");
		
		DefIntellectModel dlm = new DefIntellectModel(dao);
		Map<String, Object> m = SchemaUtil.setDictionaryMessageForForm(dlm.loadTerminalEvaluate(cnd, DEF_IntellectTerminalEvaluate), DEF_IntellectTerminalEvaluate) ;
		res.put("body", m);
	}
	
}
