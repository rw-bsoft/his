/**
 * @(#)MasterplateMaintainModel.java Created on 2012-11-19 下午04:41:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mpm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class MasterplateMaintainModel extends EmpiModel {

	private BaseDAO dao;

	public MasterplateMaintainModel(BaseDAO dao) {
		super(dao);
		this.dao = dao;
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

	@SuppressWarnings("rawtypes")
	public boolean checkHasSameField(String id, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameField = true;
		Map<String, Object> body = null;
		List cnd = CNDHelper.createSimpleCnd("eq", "a.id", "s", id);
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

	@SuppressWarnings("unused")
	public boolean hasUsedByMaintain(String fieldId)
			throws ModelDataOperationException {
		boolean hasUsed = false;
		Map<String, Object> body = null;
		List<Map<String, Object>> list = null;
		StringBuffer hql = new StringBuffer();
		hql.append("select ")
				.append(" a.masterplateId as masterplateId,b.fieldId as fieldId from ")
				.append(MPM_MasterplateMaintain)
				.append(" a, ")
				.append(MPM_FieldMasterRelation)
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

	@SuppressWarnings("rawtypes")
	public boolean checkHasSameDic(String keys, String fieldId, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameDic = true;
		Map<String, Object> body = null;
		List cnd1 = CNDHelper.createSimpleCnd("eq", "a.keys", "s", keys);
		List cnd2 = CNDHelper.createSimpleCnd("eq", "a.fieldId", "s", fieldId);
		List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
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

	public void removeDicMaintain(String dicId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove(dicId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除字典信息失败", e);
		}

	}

	@SuppressWarnings("rawtypes")
	public boolean checkHasSameMP(Map<String, Object> reqBody, String schema)
			throws ModelDataOperationException {
		boolean hasNoSameMP = true;
		List<Map<String, Object>> body = null;
		String masterplateName = (String) reqBody.get("masterplateName");
		String manaUnitId = (String) reqBody.get("manaUnitId");
		String masterplateType = (String) reqBody.get("masterplateType");
		List cnd1 = CNDHelper.createSimpleCnd("eq", "a.masterplateName", "s",
				masterplateName);
		List cnd2 = CNDHelper.createSimpleCnd("eq", "a.manaUnitId", "s",
				manaUnitId);
		List cnd3 = CNDHelper.createSimpleCnd("eq", "a.masterplateType", "s",
				masterplateType);
		List cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
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

	@SuppressWarnings("unused")
	public boolean hasDataInMaintain(String masterplateId)
			throws ModelDataOperationException {
		boolean hasData = false;
		Map<String, Object> body = null;
		List<Map<String, Object>> list = null;
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select empiId as empiId,masterplateId as masterplateId from ")
				.append(MPM_MasterplateData).append(" where masterplateId='")
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

	public void removeMasterplateMaintain(String masterplateId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove(masterplateId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除模版信息失败", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> listFieldByRelation(List queryCnd, int pageNo,
			int pageSize, String queryCndsType, String sortInfo)
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
		Schema sc = SchemaController.instance().getSchema(MPM_FieldMaintain);
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			hql.append("b." + it.getId() + " as " + it.getId() + ", ");
		}
		hql.append("a.relationId as relationId, ")
				.append(" a.masterplateId as masterplateId from ")
				.append(MPM_FieldMasterRelation).append(" a, ")
				.append(MPM_FieldMaintain).append(" b where a.masterplateId='")
				.append(masterplateId).append("' and a.fieldId=b.fieldId ");
		if (where.length() > 0) {
			hql.append(" and ").append(where);
		}
		hql.append(" order by ").append(sortInfo).append(" asc");
		String sql1 = "select count(*) from " + MPM_FieldMasterRelation
				+ " a, " + MPM_FieldMaintain + " b where a.masterplateId='"
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

	public void removeFieldRelation(String fieldId, String masterplateId)
			throws ModelDataOperationException {
		String hql = "delete from " + MPM_FieldMasterRelation
				+ " where fieldId='" + fieldId + "' and masterplateId='"
				+ masterplateId + "'";
		try {
			dao.doUpdate(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "解除字段与模版的联系失败", e);
		}

	}

	public void removeMasterplateRelation(String masterplateId)
			throws ModelDataOperationException {
		String hql = "delete from " + MPM_FieldMasterRelation
				+ " where masterplateId='" + masterplateId + "'";
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

	public void removeDicIdByFieldId(String fieldId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("delete from ").append(MPM_DictionaryMaintain)
				.append(" where fieldId='").append(fieldId).append("'");
		try {
			dao.doUpdate(hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除字段的字典失败", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean getHasListInfo(String fieldId)
			throws ModelDataOperationException {
		boolean hasListInfo = false;
		List<Map<String, Object>> body = null;
		List cnd = CNDHelper.createSimpleCnd("eq", "a.fieldId", "s", fieldId);
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

	public void removeSelectField(String masterplateId, String[] fields)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("delete from ").append(MPM_FieldMasterRelation)
				.append(" where masterplateId='").append(masterplateId)
				.append("'");
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

	public Map<String, Object> listFieldByMasterplateId(String masterplateId)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> body = null;
		String sql = "select b.fieldId as fieldId,b.alias as alias,b.id as id,"
				+ " b.type as type,b.dicRender as dicRender,b.notNull as notNull,"
				+ " b.maxValue as maxValue,b.minValue as minValue,"
				+ " b.defaultValue as defaultValue,b.length as length,b.pyCode as pyCode,b.remark as remark"
				+ " from " + MPM_FieldMasterRelation + " a, "
				+ MPM_FieldMaintain + " b where a.masterplateId='"
				+ masterplateId + "' and a.fieldId=b.fieldId ";
		try {
			body = dao.doQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询字段失败", e);
		}
		parameters.put("body", body);
		return parameters;
	}

	public Map<String, Object> saveMasterplateDate(Map<String, Object> reqBody,
			String op, Map<String, Object> masterInfo,
			List<Map<String, Object>> fields)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> result = null;
		Map<String, Object> record = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		record.put("masterplateId", masterInfo.get("masterplateId"));
		record.put("masterplateName", masterInfo.get("masterplateName"));
		record.put("masterplateTypezb", masterInfo.get("masterplateTypezb"));
		String dateType = (String) masterInfo.get("dateType");
		record.put("dateType", dateType);
		record.put("manaUnitId", reqBody.get("manageUnit"));
		record.put("regionCode", reqBody.get("areaGrid"));
		record.put("manageDoctor", reqBody.get("user"));
		if ("create".equals(op)) {
			record.put("inputUser", user.getUserId());
			record.put("inputUnit", user.getManageUnitId());
			record.put("inputDate", new Date());
		}
		record.put("lastModifyUnit", user.getManageUnitId());
		record.put("lastModifyUser", user.getUserId());
		record.put("lastModifyDate", new Date());
		String recordId = "";
		Map<String, Object> dataIds = null;
		if ("update".equals(op)) {
			recordId = (String) reqBody.get("recordId");
			record.put("recordId", recordId);
			dataIds = (Map<String, Object>) reqBody.get("dataIds");
			reqBody.remove("recordId");
			reqBody.remove("dataIds");
		}
		try {
			if (reqBody.get("LRSJ") != null) {
				if ("1".equals(dateType)) {
					String LRSJ = reqBody.get("LRSJ").toString();
					Date dateValue = sdf.parse(LRSJ + "-01-01");
					record.put("dateValue", dateValue);
				} else if ("2".equals(dateType)) {
					Integer LRSJ = Integer.parseInt(reqBody.get("LRSJ")
							.toString());
					String LRSJND = reqBody.get("LRSJND").toString();
					Date dateValue = sdf.parse(LRSJND + "-" + LRSJ * 3 + "-01");
					record.put("dateValue", dateValue);
					reqBody.remove("LRSJND");
				} else if ("3".equals(dateType)) {
					String LRSJ = reqBody.get("LRSJ").toString();
					String LRSJND = reqBody.get("LRSJND").toString();
					Date dateValue = sdf.parse(LRSJND + "-" + LRSJ + "-01");
					record.put("dateValue", dateValue);
					reqBody.remove("LRSJND");
				} else {
					Date dateValue = (Date) reqBody.get("LRSJ");
					record.put("dateValue", dateValue);
				}
			}
			result = dao.doSave(op, MPM_MasterplateData, record, true);
			reqBody.remove("LRSJ");
			reqBody.remove("manageUnit");
			reqBody.remove("areaGrid");
			reqBody.remove("user");
			reqBody.remove("masterplateId");
			if ("create".equals(op)) {
				recordId = (String) result.get("recordId");
			}
			Map<String, Object> resdataIds = new HashMap<String, Object>();
			for (Iterator i = reqBody.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				Object value = reqBody.get(key);
				String fieldId = "";
				for (int j = 0; j < fields.size(); j++) {
					Map<String, Object> f = fields.get(j);
					if (key.equals(f.get("id"))) {
						fieldId = (String) f.get("fieldId");
						break;
					}
				}
				data.put("recordId", recordId);
				data.put("fieldId", fieldId);
				data.put("fieldValue", value);
				data.put("id", key);
				if ("update".equals(op)) {
					data.put("dataId", dataIds.get(key));
				}
				Map<String, Object> map = dao.doSave(op, MPM_FieldData, data,
						true);
				if ("create".equals(op)) {
					resdataIds.put(key, map.get("dataId"));
				}
			}
			if ("create".equals(op)) {
				result.put("dataIds", resdataIds);
			} else {
				result.put("dataIds", dataIds);
			}
			result.putAll(loadMasterplateDate(recordId));
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存模版数据失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存模版数据失败", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存模版数据失败", e);
		}
		return result;
	}

	public Map<String, Object> getMasterplateMaintain(String masterplateId)
			throws ModelDataOperationException {
		Map<String, Object> result = null;
		try {
			result = dao.doLoad(MPM_MasterplateMaintain, masterplateId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询模版失败", e);
		}
		return result;
	}

	public Map<String, Object> listMasterplate(String schemaId, List queryCnd,
			int pageNo, int pageSize, String queryCndsType, String sortInfo)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			result = dao.doList(queryCnd, sortInfo, schemaId, pageNo, pageSize,
					queryCndsType);
			List<Map<String, Object>> list = (List<Map<String, Object>>) result
					.get("body");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				String dateType = (String) m.get("dateType");
				if ("1".equals(dateType)) {
					Date LRSJ = (Date) m.get("dateValue");
					String dateValue = sdf.format(LRSJ).substring(0, 4);
					Dictionary dic = DictionaryController.instance().get(
							"chis.dictionary.years");
					m.put("dateValue", dic.getText(dateValue));
				} else if ("2".equals(dateType)) {
					Date LRSJ = (Date) m.get("dateValue");
					String dateValue_nd = sdf.format(LRSJ).substring(0, 4);
					int dateValue = Integer.parseInt(sdf.format(LRSJ)
							.substring(5, 7)) / 3;
					Dictionary dic = DictionaryController.instance().get(
							"chis.dictionary.season");
					Dictionary dicn = DictionaryController.instance().get(
							"chis.dictionary.years");
					m.put("dateValue",
							dicn.getText(dateValue_nd + "")
									+ dic.getText(dateValue + ""));

				} else if ("3".equals(dateType)) {
					Date LRSJ = (Date) m.get("dateValue");
					String dateValue = sdf.format(LRSJ).substring(5, 7);
					String dateValue_nd = sdf.format(LRSJ).substring(0, 4);
					Dictionary dic = DictionaryController.instance().get(
							"chis.dictionary.month");
					Dictionary dicn = DictionaryController.instance().get(
							"chis.dictionary.years");
					m.put("dateValue",
							dicn.getText(dateValue_nd + "")
									+ dic.getText(dateValue));
				}
				body.add(m);
			}
			result.put("body", body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询模版失败", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询模版失败", e);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> loadMasterplateDate(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> body = null;
		List cnd = CNDHelper.createSimpleCnd("eq", "recordId", "s", pkey);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			body = dao.doLoad(MPM_MasterplateData, pkey);
			String dateType = (String) body.get("dateType");
			body = SchemaUtil.setDictionaryMessageForForm(body,
					MPM_MasterplateData);
			if ("1".equals(dateType)) {
				Date LRSJ = (Date) body.get("dateValue");
				String dateValue = sdf.format(LRSJ).substring(0, 4);
				Dictionary dic = DictionaryController.instance().get(
						"chis.dictionary.years");
				Map<String, Object> v = new HashMap<String, Object>();
				v.put("key", dateValue);
				v.put("text", dic.getText(dateValue));
				body.put("dateValue", v);
			} else if ("2".equals(dateType)) {
				Date LRSJ = (Date) body.get("dateValue");
				int dateValue = Integer.parseInt(sdf.format(LRSJ).substring(5,
						7)) / 3;
				Dictionary dic = DictionaryController.instance().get(
						"chis.dictionary.season");
				Map<String, Object> v = new HashMap<String, Object>();
				v.put("key", dateValue);
				v.put("text", dic.getText(dateValue + ""));
				body.put("dateValue", v);

				String dateValue_nd = sdf.format(LRSJ).substring(0, 4);
				Dictionary dicn = DictionaryController.instance().get(
						"chis.dictionary.years");
				v = new HashMap<String, Object>();
				v.put("key", dateValue_nd);
				v.put("text", dicn.getText(dateValue_nd));
				body.put("dateValue_nd", v);
			} else if ("3".equals(dateType)) {
				Date LRSJ = (Date) body.get("dateValue");
				String dateValue = sdf.format(LRSJ).substring(5, 7);
				Dictionary dic = DictionaryController.instance().get(
						"chis.dictionary.month");
				Map<String, Object> v = new HashMap<String, Object>();
				v.put("key", dateValue);
				v.put("text", dic.getText(dateValue));
				body.put("dateValue", v);
				String dateValue_nd = sdf.format(LRSJ).substring(0, 4);
				Dictionary dicn = DictionaryController.instance().get(
						"chis.dictionary.years");
				v = new HashMap<String, Object>();
				v.put("key", dateValue_nd);
				v.put("text", dicn.getText(dateValue_nd));
				body.put("dateValue_nd", v);
			}
			list = dao.doList(cnd, "", MPM_FieldData);
			Map<String, Object> dataIds = new HashMap<String, Object>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> m = list.get(i);
				body.put((String) m.get("id"), m.get("fieldValue"));
				dataIds.put((String) m.get("id"), m.get("dataId"));
			}
			body.put("dataIds", dataIds);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载模版数据失败", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载模版数据失败", e);
		}
		return body;
	}

	public void removeMasterplateDate(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MPM_MasterplateData);
			dao.doRemove("recordId", pkey, MPM_FieldData);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除模版数据失败", e);
		}

	}

}
