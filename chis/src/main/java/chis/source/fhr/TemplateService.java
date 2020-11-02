package chis.source.fhr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public class TemplateService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TemplateService.class);

	protected void doCheckHasUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String fieldId = (String) req.get("fieldId");
		boolean hasUsed = false;
		try {
			hasUsed = tm.hasUsedByMaintain(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("Check Field Has Used is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "检查字段是否已被使用失败");
			throw new ServiceException(e);
		}
		res.put("hasUsed", hasUsed);
	}

	protected void doRemoveDicByFieldId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String fieldId = (String) req.get("fieldId");
		TemplateModule tm = new TemplateModule(dao);
		try {
			tm.removeDicIdByFieldId(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("doRemoveDicByFieldId is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字段的字典信息失败");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doGetHasListInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String fieldId = (String) reqBody.get("fieldId");
		boolean hasListInfo = false;
		try {
			hasListInfo = tm.getHasListInfo(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("Get List Info is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在字段值域失败");
			throw new ServiceException(e);
		}
		res.put("hasListInfo", hasListInfo);
	}

	protected void doRemoveDicMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String dicId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		String fieldId = (String) reqBody.get("fieldId");
		boolean hasUsed = false;
		try {
			hasUsed = tm.hasUsedByMaintain(fieldId);
			if (hasUsed == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行删除操作！");
				return;
			}
			tm.removeDicMaintain(dicId, schema);
		} catch (ModelDataOperationException e) {
			logger.error("remove dic Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字典信息失败");
			throw new ServiceException(e);
		}
	}

	protected void doSaveDicMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = null;
		boolean hasUsed = false;
		try {
			if (op.equals("update")) {
				String fieldId = (String) reqBody.get("fieldId");
				hasUsed = tm.hasUsedByMaintain(fieldId);
			}
			if (hasUsed == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行修改操作！");
				return;
			}
			body = tm.saveDicMaintain(reqBody, schema, op);
		} catch (ModelDataOperationException e) {
			logger.error("save dic Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存字典信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	@SuppressWarnings("unchecked")
	protected void doCheckHasSameDic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String keys = (String) reqBody.get("keys");
		String oldKeys = (String) req.get("keys");
		String fieldId = (String) reqBody.get("fieldId");
		boolean hasNoSameDic = true;
		try {
			hasNoSameDic = tm.checkHasSameDic(keys, fieldId, schema);
			if (keys.equals(oldKeys)) {
				hasNoSameDic = true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameDic is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名字典项失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameDic", hasNoSameDic);
	}

	protected void doRemoveFieldMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String fieldId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		boolean hasUsed = false;
		try {
			hasUsed = tm.hasUsedByMaintain(fieldId);
			if (hasUsed == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行删除操作！");
				return;
			}
			tm.removeFieldMaintain(fieldId, schema);
			tm.removeDicIdByFieldId(fieldId);
		} catch (ModelDataOperationException e) {
			logger.error("remove Field Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除字段信息失败");
			throw new ServiceException(e);
		}
	}

	protected void doCheckHasSameField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String id = (String) reqBody.get("id");
		String oldId = (String) req.get("id");
		boolean hasNoSameField = true;
		try {
			hasNoSameField = tm.checkHasSameField(id, schema);
			if (id.equals(oldId)) {
				hasNoSameField = true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameField is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名字段失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameField", hasNoSameField);
	}

	protected void doRemoveMasterplateMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String masterplateId = (String) req.get("pkey");
		String schema = (String) req.get("schema");
		boolean hasUsed = false;
		try {
			hasUsed = tm.hasDataInMaintain(masterplateId);
			if (hasUsed == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "本模板已被家医服务或健康问卷使用，不能进行删除操作！");
				return;
			}
			tm.removeMasterplateMaintain(masterplateId, schema);
			tm.removeMasterplateRelation(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("remove Masterplate Maintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除模版信息失败");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doCheckHasSameMP(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String masterplateName = (String) reqBody.get("masterplateName");
		String oldMasterplateName = (String) req.get("masterplateName");
		boolean hasNoSameMP = true;
		try {
			hasNoSameMP = tm.checkHasSameMP(reqBody, schema);
			if (masterplateName.equals(oldMasterplateName)) {
				hasNoSameMP = true;
			}
		} catch (ModelDataOperationException e) {
			logger.error("doCheckHasSameMP is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "查询是否存在同名模版失败");
			throw new ServiceException(e);
		}
		res.put("hasNoSameMP", hasNoSameMP);
	}

	protected void doListFieldByRelation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		List<?> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = tm.listFieldByRelation(queryCnd, pageNo, pageSize,
					queryCndsType, sortInfo);
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

	protected void doCheckIsUsedMasterplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String masterplateId = (String) req.get("masterplateId");
		boolean isUsedMasterplate = false;
		try {
			isUsedMasterplate = tm.hasDataInMaintain(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("Check Masterplate is Used is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "检查模版是否已被使用失败");
			throw new ServiceException(e);
		}
		res.put("isUsedMasterplate", isUsedMasterplate);
	}

	protected void doSaveMasterplateMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = null;
		boolean hasData = false;
		try {
			if (op.equals("update")) {
				String masterplateId = (String) reqBody.get("masterplateId");
				hasData = tm.hasDataInMaintain(masterplateId);
			}
			if (hasData == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "模版已经产生业务数据,不能进行修改操作！");
				return;
			}
			body = tm.saveMasterplateMaintain(reqBody, schema, op);
		} catch (ModelDataOperationException e) {
			logger.error("save MasterplateMaintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存模版信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	protected void doRemoveFieldRelation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String fieldId = (String) req.get("pkey");
		String masterplateId = (String) reqBody.get("masterplateId");
		try {
			tm.removeFieldRelation(fieldId, masterplateId);
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
		TemplateModule tm = new TemplateModule(dao);
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = tm.listSelectField(queryCnd, pageNo, pageSize,
					queryCndsType, sortInfo);
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

	protected void doSaveSelectField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String fieldIds = (String) req.get("fieldIds");
		String masterplateId = (String) req.get("masterplateId");
		String[] fields = fieldIds.split(",");
		try {
			tm.saveSelectField(masterplateId, fields);
		} catch (ModelDataOperationException e) {
			logger.error("save Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存选中字段失败");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void doListRemoveField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> result;
		try {
			result = tm.listRemoveField(queryCnd, pageNo, pageSize,
					queryCndsType, sortInfo);
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

	protected void doRemoveSelectField(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		String fieldIds = (String) req.get("fieldIds");
		String masterplateId = (String) req.get("masterplateId");
		String[] fields = fieldIds.split(",");
		try {
			tm.removeSelectField(masterplateId, fields);
		} catch (ModelDataOperationException e) {
			logger.error("remove Select Field is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "删除选中字段失败");
			throw new ServiceException(e);
		}
	}

	protected void doSaveFieldMaintain(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TemplateModule tm = new TemplateModule(dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> body = null;
		boolean hasUsed = false;
		try {
			if (op.equals("update")) {
				String fieldId = (String) reqBody.get("fieldId");
				hasUsed = tm.hasUsedByMaintain(fieldId);
			}
			if (hasUsed == true) {
				res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
				res.put(Service.RES_MESSAGE, "字段已被模版使用,不能进行修改操作！");
				return;
			}
			body = tm.saveFieldMaintain(reqBody, schema, op);
		} catch (ModelDataOperationException e) {
			logger.error("save FieldMaintain is fail");
			res.put(Service.RES_CODE, ServiceCode.CODE_SERVICE_ERROR);
			res.put(Service.RES_MESSAGE, "保存字段信息失败");
			throw new ServiceException(e);
		}
		res.put("body", body);
	}

	@SuppressWarnings("unchecked")
	protected void doGetServiceRecordTemplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String masterplateId = (String) body.get("masterplateId");
		TemplateModule tm = new TemplateModule(dao);
		try {
			List<Map<String, Object>> fields = tm
					.getFieldByMasterplateId(masterplateId);
			if (fields != null && fields.size() > 0) {
				fields = setDictionaryOnePlate(fields, dao);
			}
			Map<String, Object> schema = new HashMap<String, Object>();
			schema.put("items", fields);
			res.put("schema", schema);
		} catch (ModelDataOperationException e) {
			logger.error("doGetServiceRecordTemplate is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 具体某一个服务记录模板所有字段解析
	 * 
	 * @param body
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	public static List<Map<String, Object>> setDictionaryOnePlate(
			List<Map<String, Object>> body, BaseDAO dao)
			throws ServiceException {
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> filelds = null;
		for (Map<String, Object> fieldAttribute : body) {
			String dicRender = (String) fieldAttribute.get("dicRender");
			if ("0".equals(dicRender)) {
				data.add(fieldAttribute);
			} else if ("1".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationField(filelds, fieldAttribute, dicRender));
			} else if ("2".equals(dicRender)) {
				filelds = doListFilelds(fieldAttribute, dao);
				data.add(recombinationField(filelds, fieldAttribute, dicRender));
			}

		}
		return data;
	}

	/**
	 * 根据fieldId查询某一个字段的全部信息
	 * 
	 * @param fieldAttribute
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	private static List<Map<String, Object>> doListFilelds(
			Map<String, Object> fieldAttribute, BaseDAO dao)
			throws ServiceException {
		String fieldId = (String) fieldAttribute.get("fieldId");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "fieldId", "s", fieldId);
		List<Map<String, Object>> dicFields = null;
		try {
			dicFields = dao.doList(cnd1, null, EHR_DictionaryMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("获取字段属性信息失败", e);
		}
		return dicFields;
	}

	/**
	 * 
	 * @param filelds
	 * @param fieldAttribute
	 * @return
	 */
	public static final String Dic = "dic";
	public static final String Text = "text";
	public static final String Keys = "keys";

	private static Map<String, Object> recombinationField(
			List<Map<String, Object>> filelds,
			Map<String, Object> fieldAttribute, String dicRender) {
		Map<String, Object> rFilelds = new HashMap<String, Object>();
		List<List<String>> dic = new ArrayList<List<String>>();
		for (Map<String, Object> data : filelds) {
			List<String> rFieldDic = new ArrayList<String>();
			Object text = data.get(Text);
			Object keys = data.get(Keys);
			if (text != null && keys != null) {
				rFieldDic.add((String)data.get(Keys));
				rFieldDic.add((String)data.get(Text));
				rFilelds.put(keys.toString(), data);
			}
			dic.add(rFieldDic);
		}
		rFilelds.putAll(fieldAttribute);
		rFilelds.put(Dic, dic);
		return rFilelds;
	}

	public void doLoadMasterplateDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		TemplateModule tm = new TemplateModule(dao);
		try {
			List<Map<String, Object>> dicFields = tm
					.getDicFieldsByRecordId(pkey);
			Map<String, Object> info = tm.loadMasterplateDate(pkey);
			if (dicFields != null && dicFields.size() > 0) {
				for (Map<String, Object> dicField : dicFields) {
					String id = (String) dicField.get("id");
					String keys = (String) dicField.get("keys");
					String text = (String) dicField.get("text");
					if (info.get(id) != null && !"".equals(info.get(id))) {
						String key = (String) info.get("id");
						if (keys.equals(key)) {
							Map<String, Object> dic = new HashMap<String, Object>();
							dic.put("key", key);
							dic.put("text", text);
							info.put(id, dic);
						}
					}
				}
			}
			res.put("body", info);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doSaveMasterplateDate(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		Map<String, Object> saveData = new HashMap<String, Object>();
		saveData.putAll(reqBody);
		String op = (String) jsonReq.get("op");
		String recordId = (String) reqBody.get("recordId");
		// String empiId = (String) reqBody.get("empiId");
		// String masterplateId = (String) reqBody.get("masterplateId");
		TemplateModule tm = new TemplateModule(dao);
		try {
			tm.deleteOldRecordById(recordId);
			Map<String, Object> rec = tm.saveMasterplateData(saveData, op);
			if ("create".equals(op)) {
				recordId = (String) rec.get("recordId");
			}else{
				rec.put("recordId", recordId);
			}
			Set<String> keys = reqBody.keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (!"recordId".equals(key) && !"empiId".equals(key)
						&& !"masterplateId".equals(key)
						&& !"masterplateName".equals(key)) {
					Map<String, Object> fieldData = new HashMap<String, Object>();
					fieldData.put("recordId", recordId);
					fieldData.put("id", key);
					fieldData.put("fieldValue", reqBody.get(key));
					tm.saveFieldData(fieldData);
				}
			}
			jsonRes.put("body", rec);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
