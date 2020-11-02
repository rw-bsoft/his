/**
 * @(#)AbstractTaskListModel.java Created on 2012-3-19 上午11:23:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.task;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.PlanStatus;

import com.alibaba.fastjson.JSONException;

import ctd.schema.Schema;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class AbstractTaskListModel {

	private BaseDAO dao;

	public AbstractTaskListModel(BaseDAO dao) {
		this.dao = dao;
	}

	public AbstractTaskListModel(Context ctx) {
		this.dao = new BaseDAO();
	}

	/**
	 * schema 转sql 查询字段<br>
	 * 定义为protected 以便其子类可用
	 * 
	 * @param sc
	 * @return
	 */
	protected String buildQueryEntries(Schema sc) {
		StringBuffer sb = new StringBuffer(sc.getEntityName()).append(" a ");
		for (SchemaRelation sr : sc.getParentRelations()) {
			sb.append(",").append(sr.getFullEntryName());
		}
		for (SchemaRelation sr : sc.getChildRelations()) {
			sb.append(",").append(sr.getFullEntryName());
		}
		return sb.toString();
	}

	/**
	 * schema 转sql 查询字段<br>
	 * 定义为protected 以便其子类可用
	 * 
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			fields.append(",")
					.append(si.hasProperty("refAlias") ? si
							.getProperty("refAlias") : "a").append(".")
					.append(si.getId()).append(" as ").append(si.getId());
		}
		return fields.substring(1);
	}

	/**
	 * 获取工作列表
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param condition
	 * @param sc
	 * @param session
	 * @return
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public List<Map<String, Object>> getTaskList(String condition, Schema sc,
			int pageSize, int pageNo) throws ModelDataOperationException {
		int first = (pageNo - 1) * pageSize;

		String sortInfo = sc.getSortInfo() != null
				&& sc.getSortInfo().trim().length() != 0 ? new StringBuffer(
				" order by ").append(sc.getSortInfo()).toString() : "";
		String hql = new StringBuffer("select ").append(buildQueryFields(sc))
				.append(" from ").append(buildQueryEntries(sc))
				.append(" where a.planStatus=:planStatus ").append(condition)
				.append(sortInfo).toString();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		parameters.put("first", first);
		parameters.put("max", pageSize);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取工作列表失败", e);
		}
		return list;
	}

	/**
	 * 获取工作列表总数
	 * 
	 * @param jsonRes
	 * @param session
	 * @return
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public int getTotalCount(String condition, Schema sc)
			throws ModelDataOperationException {
		String chql = new StringBuffer("select count(*) as count from ")
				.append(buildQueryEntries(sc))
				.append(" where a.planStatus=:planStatus").append(condition)
				.toString();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(chql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取工作列表总数失败", e);
		}
		return map != null ? ((Long) map.get("count")).intValue() : 0;
	}
}
