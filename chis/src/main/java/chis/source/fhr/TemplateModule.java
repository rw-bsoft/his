package chis.source.fhr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class TemplateModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public TemplateModule(BaseDAO dao) {
		this.dao = dao;
	}

	public boolean hasUsedByMaintain(String fieldId)
			throws ModelDataOperationException {
		boolean hasUsed = false;
		List<Map<String, Object>> list = null;
		StringBuffer hql = new StringBuffer();
		hql.append("select")
				.append(" a.masterplateId as masterplateId,b.fieldId as fieldId from ")
				.append("MPM_MasterplateMaintain a, MPM_FieldMasterRelation")
				.append(" b where a.masterplateId=b.masterplateId and b.fieldId='")
				.append(fieldId).append("'").toString();
		try {
			list = dao.doQuery(hql.toString(), null);
			if (list != null && list.size() > 0) {
				hasUsed = true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询字段是否已被模版使用失败", e);
		}
		return hasUsed;
	}

	public void removeDicIdByFieldId(String fieldId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("delete from MPM_DictionaryMaintain where fieldId='")
				.append(fieldId).append("'");
		try {
			dao.doUpdate(hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除字段的字典失败", e);
		}
	}

	public boolean getHasListInfo(String fieldId)
			throws ModelDataOperationException {
		boolean hasListInfo = false;
		List<Map<String, Object>> body = null;
		List<?> cnd = CNDHelper
				.createSimpleCnd("eq", "a.fieldId", "s", fieldId);
		try {
			body = dao.doQuery(cnd, "dicId", MPM_DictionaryMaintain);
			if (body != null && body.size() > 0) {
				hasListInfo = true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在字段值域失败", e);
		}
		return hasListInfo;
	}

	public void removeDicMaintain(String dicId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove(dicId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除字典信息失败", e);
		}

	}

	public Map<String, Object> saveDicMaintain(Map<String, Object> reqBody,
			String schema, String op) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存字典信息失败", e);
		}
		return body;
	}

	public boolean checkHasSameDic(String keys, String fieldId, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameDic = true;
		Map<String, Object> body = null;
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.keys", "s", keys);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.fieldId", "s",
				fieldId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			body = dao.doLoad(cnd, schema);
			if (body != null && body.size() > 0) {
				hasNoSameDic = false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在同名字典项失败", e);
		}
		return hasNoSameDic;
	}

	public void removeFieldMaintain(String fieldId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove(fieldId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除字段信息失败", e);
		}

	}

	public boolean checkHasSameField(String id, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameField = true;
		Map<String, Object> body = null;
		String manageUnitId = UserRoleToken.getCurrent().getManageUnitId();
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.id", "s", id);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.inputUnit", "s",
				manageUnitId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			body = dao.doLoad(cnd, schema);
			if (body != null && body.size() > 0) {
				hasNoSameField = false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在同名字段失败", e);
		}
		return hasNoSameField;
	}

	public boolean hasDataInMaintain(String masterplateId)
			throws ModelDataOperationException {
		boolean hasData = false;
		List<Map<String, Object>> list = null;
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select masterplateId as masterplateId from MPM_MasterplateData where masterplateId='")
				.append(masterplateId).append("'").toString();
		try {
			list = dao.doQuery(hql.toString(), null);
			if (list != null && list.size() > 0) {
				hasData = true;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询模版是否已有数据失败", e);
		}
		return hasData;
	}

	public void removeMasterplateMaintain(String masterplateId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove(masterplateId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除模版信息失败", e);
		}
	}

	public void removeMasterplateRelation(String masterplateId)
			throws ModelDataOperationException {
		String hql = "delete from MPM_FieldMasterRelation where masterplateId='"
				+ masterplateId + "'";
		try {
			dao.doUpdate(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "解除字段与模版的联系失败", e);
		}
	}

	public boolean checkHasSameMP(Map<String, Object> reqBody, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameMP = true;
		List<Map<String, Object>> body = null;
		String masterplateName = (String) reqBody.get("masterplateName");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.masterplateName", "s",
				masterplateName);
		try {
			body = dao.doQuery(cnd, "", schema);
			if (body != null && body.size() > 0) {
				hasNoSameMP = false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在同名模版失败", e);
		}
		return hasNoSameMP;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> listFieldByRelation(List<?> queryCnd,
			int pageNo, int pageSize, String queryCndsType, String sortInfo)
			throws ExpException {
		Map<String, Object> result = new HashMap<String, Object>();
		Context ctx = dao.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		String masterplateId = (String) queryCnd.get(queryCnd.size() - 1);
		queryCnd.remove(queryCnd.size() - 1);
		if (queryCnd.size() == 0) {
			queryCnd = null;
		}
		String where = "";
		if (queryCnd != null) {
			where = ExpressionProcessor.instance().toString(queryCnd);
		}

		if (sortInfo == null) {
			sortInfo = "a.fieldId";
		}
		StringBuffer hql = new StringBuffer().append("select ");
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(MPM_FieldMaintain);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			hql.append("b." + it.getId() + " as " + it.getId() + ", ");
		}
		hql.append("a.relationId as relationId, ")
				.append(" a.masterplateId as masterplateId from MPM_FieldMasterRelation a, ")
				.append("MPM_FieldMaintain b where a.masterplateId='")
				.append(masterplateId).append("' and a.fieldId=b.fieldId ");
		if (where.length() > 0) {
			hql.append(" and ").append(where);
		}
		hql.append(" order by ").append(sortInfo).append(" asc");
		String sql1 = "select count(*) from MPM_FieldMasterRelation a, MPM_FieldMaintain b where a.masterplateId='"
				+ masterplateId + "' and a.fieldId=b.fieldId ";
		StringBuffer sql = new StringBuffer(sql1);
		if (where.length() > 0) {
			sql.append(" and ").append(where);
		}
		Query q = ss.createQuery(sql.toString());
		List ls = q.list();
		int first = (pageNo - 1) * pageSize;
		long totalCount = ((Long) ls.iterator().next()).longValue();
		q = ss.createQuery(hql.toString());
		if (pageSize > 0) {
			q.setFirstResult(first);
			q.setMaxResults(pageSize);
		}
		List<Object[]> records = q.list();
		int rowCount = records.size();
		List<String> fieldNames = new ArrayList<String>();
		int colCount = 0;
		for (SchemaItem it : items) {
			fieldNames.add(it.getId());
			colCount++;
		}
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < rowCount; i++) {
			Object[] r = records.get(i);
			Map<String, Object> o = new HashMap<String, Object>();
			rs.add(o);
			for (int j = 0; j < colCount; j++) {
				Object v = r[j];
				SchemaItem si = sc.getItem(fieldNames.get(j));
				String name = si.getId();
				o.put(name, v);
				if (si.isCodedValue()) {
					if (sc.getItem(name + "_text") == null) {
						o.put(name + "_text", si.toDisplayValue(v));
					}
				}
			}
		}
		result.put("totalCount", totalCount);
		result.put("body", rs);
		return result;
	}

	public Map<String, Object> saveMasterplateMaintain(
			Map<String, Object> reqBody, String schema, String op)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存模版信息失败", e);
		}
		return body;
	}

	public void removeFieldRelation(String fieldId, String masterplateId)
			throws ModelDataOperationException {
		String hql = "delete from  MPM_FieldMasterRelation where fieldId='"
				+ fieldId + "' and masterplateId='" + masterplateId + "'";
		try {
			dao.doUpdate(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "解除字段与模版的联系失败", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> listSelectField(List queryCnd, int pageNo,
			int pageSize, String queryCndsType, String sortInfo)
			throws ExpException, ModelDataOperationException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		Map<String, Object> body = new HashMap<String, Object>();
		List<Map<String, Object>> records1 = null;
		List<Map<String, Object>> records2 = null;
		List<String> fieldIds = new ArrayList<String>();
		String masterplateId = (String) queryCnd.get(queryCnd.size() - 1);
		queryCnd.remove(queryCnd.size() - 1);
		if (queryCnd.size() == 0) {
			queryCnd = null;
		}
		String cnd1 = "['eq',['$','masterplateId'],['s','" + masterplateId
				+ "']]";
		try {
			records1 = dao.doList(CNDHelper.toListCnd(cnd1), sortInfo,
					MPM_FieldMasterRelation);
			for (Map<String, Object> map : records1) {
				if (map.get("fieldId") != null) {
					fieldIds.add((String) map.get("fieldId"));
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询联系表失败", e);
		}
		try {
			records2 = dao.doList(queryCnd, sortInfo, MPM_FieldMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询字段表失败", e);
		}
		int first = (pageNo - 1) * pageSize;
		int size = first + pageSize;
		for (Map<String, Object> map : records2) {
			String fieldId = (String) map.get("fieldId");
			if (fieldId != null && !fieldIds.contains(fieldId)) {
				records.add(map);
			}
		}
		body.put("totalCount", records.size());
		if (records.size() > first) {
			if (records.size() < size) {
				size = records.size();
			}
			records = records.subList(first, size);
		}
		body.put("body", records);
		return body;
	}

	public void saveSelectField(String masterplateId, String[] fields)
			throws ValidateException, ModelDataOperationException {
		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("masterplateId", masterplateId);
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			reqBody.put("fieldId", field);
			try {
				dao.doSave("create", MPM_FieldMasterRelation, reqBody, true);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "保存联系表失败", e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> listRemoveField(List queryCnd, int pageNo,
			int pageSize, String queryCndsType, String sortInfo)
			throws ExpException, ModelDataOperationException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		Map<String, Object> body = new HashMap<String, Object>();
		List<Map<String, Object>> records1 = null;
		List<Map<String, Object>> records2 = null;
		List<String> fieldIds = new ArrayList<String>();
		String masterplateId = (String) queryCnd.get(queryCnd.size() - 1);
		queryCnd.remove(queryCnd.size() - 1);
		if (queryCnd.size() == 0) {
			queryCnd = null;
		}
		String cnd1 = "['eq',['$','masterplateId'],['s','" + masterplateId
				+ "']]";
		try {
			records1 = dao.doList(CNDHelper.toListCnd(cnd1), sortInfo,
					MPM_FieldMasterRelation);
			for (Map<String, Object> map : records1) {
				if (map.get("fieldId") != null) {
					fieldIds.add((String) map.get("fieldId"));
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询联系表失败", e);
		}
		try {
			records2 = dao.doList(queryCnd, sortInfo, MPM_FieldMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询字段表失败", e);
		}
		int first = (pageNo - 1) * pageSize;
		int size = first + pageSize;
		for (Map<String, Object> map : records2) {
			String fieldId = (String) map.get("fieldId");
			if (fieldId != null && fieldIds.contains(fieldId)) {
				records.add(map);
			}
		}
		body.put("totalCount", records.size());
		if (records.size() > first) {
			if (records.size() < size) {
				size = records.size();
			}
			records = records.subList(first, size);
		}
		body.put("body", records);
		return body;
	}

	public void removeSelectField(String masterplateId, String[] fields)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("delete from MPM_FieldMasterRelation where masterplateId='")
				.append(masterplateId).append("'");
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			StringBuffer sql = new StringBuffer(hql.toString());
			sql.append(" and fieldId='").append(field).append("'");
			try {
				dao.doUpdate(sql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "解除字段与模版的联系失败", e);
			}
		}
	}

	public Map<String, Object> saveFieldMaintain(Map<String, Object> reqBody,
			String schema, String op) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存字段信息失败", e);
		}
		return body;
	}

	public List<Map<String, Object>> getTemplateCountByManageUnitId(
			String manageUnitId) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("like", "inputUnit", "s",
				manageUnitId + "%");
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "whmb", "s", "01");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doList(cnd, null, MPM_MasterplateMaintain);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据机构ID查询模板失败", e);
		}
	}

	public List<Map<String, Object>> getFieldByMasterplateId(
			String masterplateId) throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.fieldId as fieldId, a.alias as alias, a.id as id, a.type as type, a.dicRender as dicRender, a.notNull as notNull, a.defaultType as defaultType, a.defaultValue as defaultValue, a.length as length ")
				.append("from MPM_FieldMaintain a where a.fieldId in(")
				.append("select b.fieldId as fieldId from MPM_FieldMasterRelation b where b.masterplateId='")
				.append(masterplateId).append("')").toString();
		try {
			return dao.doQuery(hql, new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据模版编号查询模板所有字段失败", e);
		}
	}

	public Map<String, Object> loadMasterplateDate(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> record = null;
		StringBuffer hql = new StringBuffer(
				"select a.recordId as recordId, b.id as id, b.fieldValue as fieldValue")
				.append(" from MPM_MasterplateData a, MPM_FieldData b")
				.append(" where a.recordId=b.recordId and a.recordId=:recordId");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("recordId", pkey);
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			if (list != null && list.size() > 0) {
				record = new HashMap<String, Object>();
				for (Map<String, Object> field : list) {
					record.put((String) field.get("id"),
							field.get("fieldValue"));
				}
			}
			record.put("recordId", pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载模块数据失败", e);
		}
		return record;
	}

	public List<Map<String, Object>> getDicFieldsByRecordId(String pkey)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select a.fieldId as fieldId, a.keys as keys, a.text as text, b.id as id")
				.append(" from MPM_DictionaryMaintain a, MPM_FieldMaintain b, MPM_FieldMasterRelation c, MPM_MasterplateData d")
				.append(" where d.masterplateId=c.masterplateId and c.fieldId = b.fieldId and c.fieldId = a.fieldId and b.dicRender<>'0'")
				.append(" and d.recordId=:recordId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", pkey);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql.toString(),
					parameters);
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据记录id查询字典字段失败", e);
		}
	}

	public void deleteOldRecordById(String recordId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("recordId", recordId, MPM_FieldData);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据记录id删除数据失败", e);
		}
	}

	public Map<String, Object> saveMasterplateData(Map<String, Object> reqBody,
			String op) throws ValidateException, ModelDataOperationException {
		String uid = UserRoleToken.getCurrent().getUserId();
		String manageUnitId = UserRoleToken.getCurrent().getManageUnitId();
		if ("create".equals(op)) {
			reqBody.put("inputUser", uid);
			reqBody.put("inputUnit", manageUnitId);
			reqBody.put("inputDate", new Date());
		}
		reqBody.put("lastModifyUser", uid);
		reqBody.put("lastModifyUnit", manageUnitId);
		reqBody.put("lastModifyDate", new Date());
		try {
			return dao.doSave(op, MPM_MasterplateData, reqBody, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存主记录失败", e);
		}
	}

	public void saveFieldData(Map<String, Object> fieldData)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doInsert(MPM_FieldData, fieldData, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存字段数据失败", e);
		}

	}
}
