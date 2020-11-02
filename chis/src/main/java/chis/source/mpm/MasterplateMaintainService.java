/**
 * @(#)MasterplateMaintainService.java Created on 2012-11-19 下午04:41:02
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.mobile.DictionaryUtil;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;

import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class MasterplateMaintainService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
	.getLogger(MasterplateMaintainService.class);
	
	
	protected void doCheckHasUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		String fieldId = (String) req.get("fieldId");
		boolean hasUsed=false;
		try {
			hasUsed = mmm.hasUsedByMaintain(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("Check Field Has Used is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "检查字段是否已被使用失败");
			throw new ServiceException(e);
		}
		res.put("hasUsed", hasUsed);
	}
	
	protected void doCheckIsUsedMasterplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		String masterplateId = (String) req.get("masterplateId");
		boolean isUsedMasterplate=false;
		try {
			isUsedMasterplate = mmm.hasDataInMaintain(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("Check Masterplate is Used is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "检查模版是否已被使用失败");
			throw new ServiceException(e);
		}
		res.put("isUsedMasterplate", isUsedMasterplate);
	}
	protected void doCheckHasSameMP(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String masterplateName=(String) reqBody.get("masterplateName");
		String oldMasterplateName=(String) req.get("masterplateName");
		boolean hasNoSameMP=true;
		try {
			hasNoSameMP=mmm.checkHasSameMP(reqBody,schema);
			if(masterplateName.equals(oldMasterplateName)){
				hasNoSameMP=true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameMP is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名模版失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameMP", hasNoSameMP);	
	}
	
	
	protected void doSaveSelectField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		String fieldIds = (String) req.get("fieldIds");
		String masterplateId = (String) req.get("masterplateId");
		String[] fields = fieldIds.split(",");
		try {
			mmm.saveSelectField(masterplateId,fields);
		} catch (ModelDataOperationException e) {
			logger.error("save Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存选中字段失败");
			throw new ServiceException(e);
		}
		
	}
	
	protected void doRemoveSelectField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		String fieldIds = (String) req.get("fieldIds");
		String masterplateId = (String) req.get("masterplateId");
		String[] fields = fieldIds.split(",");
		try {
			mmm.removeSelectField(masterplateId,fields);
		} catch (ModelDataOperationException e) {
			logger.error("remove Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除选中字段失败");
			throw new ServiceException(e);
		}
		
	}
	
	protected void doRemoveFieldRelation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String fieldId = (String) req.get("pkey");
		String masterplateId=(String) reqBody.get("masterplateId");
		try {
			mmm.removeFieldRelation(fieldId,masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("remove FieldRelation is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "解除字段与模版的联系失败");
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void doListSelectField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		List queryCnd = null;
		if(req.containsKey("cnd")){
			queryCnd = (List)req.get("cnd");
		}
		String queryCndsType = null;
		if(req.containsKey("queryCndsType")){
			queryCndsType = (String)req.get("queryCndsType");
		}
		String sortInfo = null;
		if(req.containsKey("sortInfo")){
			sortInfo = (String)req.get("sortInfo");
		}
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int pageNo = 1;
		if(req.containsKey("pageNo")){
			pageNo = (Integer)req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = mmm.listSelectField(queryCnd, pageNo, pageSize, queryCndsType, sortInfo);
		} catch (ExpException e) {
			logger.error("list Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "列出字段失败");
			throw new ServiceException(e);
		} catch (ModelDataOperationException e) {
			logger.error("list Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "列出字段失败");
			throw new ServiceException(e);
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.put("totalCount", result.get("totalCount"));
		res.put("body", result.get("body"));
	}
	@SuppressWarnings("rawtypes")
	protected void doListRemoveField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		List queryCnd = null;
		if(req.containsKey("cnd")){
			queryCnd = (List)req.get("cnd");
		}
		String queryCndsType = null;
		if(req.containsKey("queryCndsType")){
			queryCndsType = (String)req.get("queryCndsType");
		}
		String sortInfo = null;
		if(req.containsKey("sortInfo")){
			sortInfo = (String)req.get("sortInfo");
		}
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int pageNo = 1;
		if(req.containsKey("pageNo")){
			pageNo = (Integer)req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = mmm.listRemoveField(queryCnd, pageNo, pageSize, queryCndsType, sortInfo);
		} catch (ExpException e) {
			logger.error("list Remove Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "列出字段失败");
			throw new ServiceException(e);
		} catch (ModelDataOperationException e) {
			logger.error("list Remove Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "列出字段失败");
			throw new ServiceException(e);
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.put("totalCount", result.get("totalCount"));
		res.put("body", result.get("body"));
	}
	
	@SuppressWarnings("rawtypes")
	protected void doListFieldByRelation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		List queryCnd = null;
		if(req.containsKey("cnd")){
			queryCnd = (List)req.get("cnd");
		}
		String queryCndsType = null;
		if(req.containsKey("queryCndsType")){
			queryCndsType = (String)req.get("queryCndsType");
		}
		String sortInfo = null;
		if(req.containsKey("sortInfo")){
			sortInfo = (String)req.get("sortInfo");
		}
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int pageNo = 1;
		if(req.containsKey("pageNo")){
			pageNo = (Integer)req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = mmm.listFieldByRelation(queryCnd, pageNo, pageSize, queryCndsType, sortInfo);
		} catch (ExpException e) {
			logger.error("list Field ByRelation is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "列出字段失败");
			throw new ServiceException(e);
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		res.put("totalCount", result.get("totalCount"));
		res.put("body", result.get("body"));
	}
	
	protected void doCheckHasSameField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String id=(String) reqBody.get("id");
		String oldId=(String) req.get("id");
		boolean hasNoSameField=true;
		try {
			hasNoSameField=mmm.checkHasSameField(id,schema);
			if(id.equals(oldId)){
				hasNoSameField=true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameField is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名字段失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameField", hasNoSameField);	
	}
	
	protected void doGetHasListInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String fieldId=(String) reqBody.get("fieldId");
		boolean hasListInfo=false;
		try {
			hasListInfo=mmm.getHasListInfo(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("Get List Info is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在字段值域失败");
			throw new ServiceException(e);
		}
		res.put("hasListInfo", hasListInfo);	
	}
	
	protected void doCheckHasSameDic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String keys=(String) reqBody.get("keys");
		String oldKeys=(String) req.get("keys");
		String fieldId=(String) reqBody.get("fieldId");
		boolean hasNoSameDic=true;
		try {
			hasNoSameDic=mmm.checkHasSameDic(keys,fieldId,schema);
			if(keys.equals(oldKeys)){
				hasNoSameDic=true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameDic is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名字典项失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameDic", hasNoSameDic);	
	}

	
	protected void doSaveFieldMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body=null;
		boolean hasUsed=false;
		try {
			if(op.equals("update")){
				String fieldId=(String) reqBody.get("fieldId");
				hasUsed=mmm.hasUsedByMaintain(fieldId);
			}
			if(hasUsed==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行修改操作！");
				return;
			}
			body=mmm.saveFieldMaintain(reqBody,schema,op);
		} catch (ModelDataOperationException e) {
			logger.error("save FieldMaintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存字段信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);	
	}
	protected void doSaveDicMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body=null;
		boolean hasUsed=false;
		try {
			if(op.equals("update")){
				String fieldId=(String) reqBody.get("fieldId");
				hasUsed=mmm.hasUsedByMaintain(fieldId);
			}
			if(hasUsed==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行修改操作！");
				return;
			}
			body=mmm.saveDicMaintain(reqBody,schema,op);
		} catch (ModelDataOperationException e) {
			logger.error("save dic Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存字典信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);	
	}
	protected void doSaveMasterplateMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body=null;
		boolean hasData=false;
		try {
			if(op.equals("update")){
				String masterplateId=(String) reqBody.get("masterplateId");
				hasData=mmm.hasDataInMaintain(masterplateId);
			}
			if(hasData==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "模版已经产生业务数据,不能进行修改操作！");
				return;
			}
			body=mmm.saveMasterplateMaintain(reqBody,schema,op);
		} catch (ModelDataOperationException e) {
			logger.error("save MasterplateMaintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存模版信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);	
	}
	
	protected void doRemoveDicByFieldId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String fieldId = (String) req.get("fieldId");
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		try {
			mmm.removeDicIdByFieldId(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("doRemoveDicByFieldId is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字段的字典信息失败");
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unused")
	protected void doRemoveFieldMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String fieldId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		boolean hasUsed=false;
		try {
			hasUsed=mmm.hasUsedByMaintain(fieldId);
			if(hasUsed==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行删除操作！");
				return;
			}
			mmm.removeFieldMaintain(fieldId, schema);
			mmm.removeDicIdByFieldId(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("remove Field Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字段信息失败");
			throw new ServiceException(e);
		}
	}
	
	protected void doRemoveDicMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String dicId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		String fieldId=(String) reqBody.get("fieldId");
		boolean hasUsed=false;
		try {
			hasUsed=mmm.hasUsedByMaintain(fieldId);
			if(hasUsed==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行删除操作！");
				return;
			}
			mmm.removeDicMaintain(dicId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove dic Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字典信息失败");
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unused")
	protected void doRemoveMasterplateMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MasterplateMaintainModel mmm = new MasterplateMaintainModel(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String masterplateId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		boolean hasUsed=false;
		try {
			hasUsed=mmm.hasDataInMaintain(masterplateId);
			if(hasUsed==true){
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "本模板已被家医服务或健康问卷使用，不能进行删除操作！");
				return;
			}
			mmm.removeMasterplateMaintain(masterplateId, schema);
			mmm.removeMasterplateRelation(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("remove Masterplate Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除模版信息失败");
			throw new ServiceException(e);
		}
	}
	
	public void doSaveMasterplateDate(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		String op = (String) jsonReq.get("op");
		String masterplateId = (String) reqBody.get("masterplateId");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try {
			Map<String, Object> masterInfo = maintainModel.getMasterplateMaintain(masterplateId);
			List<Map<String, Object>> fields = (List<Map<String, Object>>) maintainModel
					.listFieldByMasterplateId(masterplateId).get("body");
			Map<String, Object> body = maintainModel.saveMasterplateDate(
					reqBody, op,masterInfo,fields);
			jsonRes.put("body", body);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doRemoveMasterplateDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try {
			maintainModel.removeMasterplateDate(pkey);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doLoadMasterplateDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		Map<String, Object> info=null;
		try {
			info=maintainModel.loadMasterplateDate(pkey);
			res.put("body", info);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	public void doListMasterplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schemaId = (String)req.get("schema");
		List queryCnd = null;
		if(req.containsKey("cnd")){
			queryCnd = (List)req.get("cnd");
		}
		String queryCndsType = null;
		if(req.containsKey("queryCndsType")){
			queryCndsType = (String)req.get("queryCndsType");
		}
		String sortInfo = null;
		if(req.containsKey("sortInfo")){
			sortInfo = (String)req.get("sortInfo");
		}
		int pageSize= 25;
		if(req.containsKey("pageSize")){
			pageSize = (Integer)req.get("pageSize");
		}
		int pageNo = 1;
		if(req.containsKey("pageNo")){
			pageNo = (Integer)req.get("pageNo");
		}
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try{
			Map<String, Object> body = maintainModel.listMasterplate(schemaId,queryCnd, pageNo, pageSize, queryCndsType, sortInfo);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount",body.get("totalCount"));
			res.put("body", body.get("body"));
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetMasterplate(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		List resPlate = new ArrayList();
		Map<String, Object> res = new HashMap<String, Object>();
		String masterplateId = (String) jsonReq.get("body");
		MasterplateMaintainModel maintainModel = new MasterplateMaintainModel(
				dao);
		try {
			res = maintainModel.listFieldByMasterplateId(masterplateId);
			if (res != null && res.size() > 0) {
				resPlate = DictionaryUtil.setDictionaryOnePlate(res, dao);
			}

		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if (resPlate == null) {
			resPlate = new ArrayList();
			resPlate.add(res);
		}
		jsonRes.put("body", resPlate);

	}

}
