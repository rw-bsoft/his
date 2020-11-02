package chis.source.phr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.web.context.WebApplicationContext;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.account.accredit.result.AuthorizeResult;
import ctd.controller.exception.ControllerException;
import ctd.dao.exception.DataAccessException;
import ctd.schema.DisplayModes;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.security.Condition;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

@SuppressWarnings({ "unchecked" })
public class SimpleQuery4HealthRecord implements Service {
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleQuery4HealthRecord.class);

	public void execute(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) {
		String schemaId = "";
		schemaId = (String) jsonReq.get("schema");

		if (StringUtils.isEmpty(schemaId)) {
			logger.error("schemaId missing");
			return;
		}
		try {
			Schema sc = SchemaController.instance().get(schemaId);
			if (sc == null) {
				logger.error("schema[" + schemaId + "] not found");
				return;
			}
			doBeforeExec(jsonReq, jsonRes, ctx);
			doQuery(jsonReq, jsonRes, ctx);
			doAfterExec(jsonReq, jsonRes, ctx);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonRes.put(Service.RES_CODE, 501);
			jsonRes.put(Service.RES_MESSAGE,
					"DataAccessException:" + e.getMessage());
		}
	}

	public void doAfterExec(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx)
			throws DataAccessException {

	}

	public void doBeforeExec(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx)
			throws DataAccessException {

	}

	@SuppressWarnings("rawtypes")
	protected void doQuery(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx)
			throws DataAccessException, ExpException {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		Schema sc;
		try {
			sc = SchemaController.instance().get((String) jsonReq.get("schema"));
		} catch (ControllerException e1) {
			throw new DataAccessException(e1.getMessage(),e1);
		}

		String queryCndsType = "";
		if (null == jsonReq.get("queryCndsType")) {
			queryCndsType = "filter";
		} else {
			queryCndsType = (String) jsonReq.get("queryCndsType");
		}
		List cnd = (List) jsonReq.get("cnd");
		String sortInfo;
		if (null == jsonReq.get("sortInfo")) {
			sortInfo = sc.getSortInfo();
		} else {
			sortInfo = (String) jsonReq.get("sortInfo");
		}

		QueryContext qc = new QueryContext();
		initQueryContext(qc, sc, ctx, cnd, queryCndsType, sortInfo);
		if (qc.getFieldCount() == 0) {
			return;
		}

		int pageSize = 50;
		int pageNo = 1;
		if (null != jsonReq.get("pageSize")) {
			pageSize = (Integer) jsonReq.get("pageSize");
		}

		if (null != jsonReq.get("pageNo")) {
			pageNo = (Integer) jsonReq.get("pageNo");
		}

		int first = (pageNo - 1) * pageSize;
		long totalCount = 0;
		jsonRes.put("totalCount", totalCount);
		jsonRes.put("pageSize", pageSize);
		jsonRes.put("pageNo", pageNo);

		List body = new ArrayList();
		jsonRes.put("body", body);

		Session ss = null;
		try {
			ss = sf.openSession();
			// query record count
			String hql = qc.buildCountHql();
			Query q = ss.createQuery(hql);
			List ls = q.list();
			totalCount = ((Long) ls.iterator().next()).longValue();
			if (totalCount == 0 || first > totalCount) {
				return;
			}
			jsonRes.put("totalCount", totalCount);
			// query data;
			hql = qc.buildQueryHql();
			q = ss.createQuery(hql);
			q.setFirstResult(first);
			q.setMaxResults(pageSize);
			List records = q.list();
			int colCount = qc.getFieldCount();
			int rowCount = records.size();
			for (int i = 0; i < rowCount; i++) {
				Object[] r = (Object[]) records.get(i);
				Map<String, Object> jsonRec = new HashMap<String, Object>();
				body.add(jsonRec);
				for (int j = 0; j < colCount; j++) {
					Object v = r[j];
					String name = qc.getFieldName(j);
					// 儿童的临时身份证显示时清空。保存时会重新生成一个。
					if (("certificateNo".equals(name) || "idCard".equals(name))
							&& v != null && v.toString().startsWith("T-")) {
						jsonRec.put(name, "");
						continue;
					}

					jsonRec.put(name, v);
					SchemaItem si = sc.getItem(qc.getFullFieldName(j));
					if (si.isCodedValue()) {
						Object dv = null;
						boolean isCacheDic = false;
						for (int k = 0; k < colCount; k++) {
							if (qc.getFieldName(k).equals(name + "_text")) {
								dv = r[k];
								isCacheDic = true;
								break;
							}
						}
						if (dv != null) {
							jsonRec.put(name + "_text", dv);
						} else {
							if (!isCacheDic) {
								jsonRec.put(name + "_text",
										si.toDisplayValue(v));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailureException(e.getMessage(), e);
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
		jsonRes.put(Service.RES_CODE, 200);
		jsonRes.put(Service.RES_MESSAGE, "QuerySuccess");
	}

	@SuppressWarnings("rawtypes")
	private void initQueryContext(QueryContext qc, Schema sc, Context ctx,
			List queryCnd, String queryCndsType, String sortInfo)
			throws ExpException {
		String tableName = sc.getEntityName();
		qc.addEntryName(tableName + " a");

		List<List> cnds = new ArrayList<List>();
		Map<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			if (sc.lookupPremission().getMode().isAccessible() == false) {
				continue;
			}
			if (sc.lookupPremission().getMode().isAccessible()
					|| it.getDisplayMode() == DisplayModes.NO_LIST_DATA) {
				continue;
			}
			if (it.hasProperty("refAlias")) {

				String refAlias = (String) it.getProperty("refAlias");
				qc.addField(fid, refAlias);
				if (loadedRelation.containsKey(refAlias)) {
					continue;
				}
				SchemaRelation sr = sc.getRelationByAlias((String) it
						.getProperty("refAlias"));

				qc.addEntryName(sr.getFullEntryName());
				List cd = (List) sr.getJoinCondition();
				if (cd != null && cd.size() > 0) {
					cnds.add(cd);
				}
				loadedRelation.put(refAlias, true);
			} else {
				qc.addField(fid, "a");
			}
		}
		if (queryCnd != null) {
			if (!queryCnd.toString().contains("b.")) {
				qc.setCountSingleTable(true);
			}
			cnds.add(queryCnd);
		}

		// init where
		if (sc.lookupPremission().getMode().isAccessible() == false) {
			qc.setWhere("where 1=2");
		} else {
			// add role query condition
			Condition c =  sc.lookupCondition(queryCndsType);
			if (c != null) {
				List exp = (List)c.getDefine();
				if (exp != null) {
					cnds.add(exp);
				}
			}
			// start
			List whereCnd = null;
			int cndCount = cnds.size();
			if (cndCount == 0) {
				whereCnd = queryCnd;
			} else {
				if (cndCount == 1) {
					whereCnd = cnds.get(0);
				} else {
					whereCnd = new ArrayList();
					whereCnd.add("and");
					for (List cd : cnds) {
						whereCnd.add(cd);
					}
				}
			}

			if (whereCnd != null) {
				String where = " where " + ExpressionProcessor.instance().toString(whereCnd);
				qc.setWhere(where);
			}
		}
		// set sortinfo
		qc.setSortInfo(sortInfo);
	}

}

class QueryContext {
	private boolean countSingleTable = false;
	private List<String> fields = new ArrayList<String>();
	private List<String> alias = new ArrayList<String>();
	private List<String> entryNames = new ArrayList<String>();
	private StringBuffer fullEntryName = new StringBuffer();
	private String where = "";
	private String sortInfo = "";

	public void setCountSingleTable(boolean countSingleTable) {
		this.countSingleTable = countSingleTable;
	}

	public void addField(String f) {
		addField(f, "");
	}

	public void addField(String f, String a) {
		fields.add(f);
		alias.add(a);
	}

	public String getFieldName(int i) {
		return fields.get(i);
	}

	public String getFullFieldName(int i) {
		String a = alias.get(i) + ".";
		if (a.equals("a.")) {
			a = "";
		}
		return a + fields.get(i);
	}

	public List<String> getAllFields() {
		return fields;
	}

	public void addEntryName(String entry) {

		if (entryNames.contains(entry)) {
			return;
		}
		if (entryNames.size() > 0) {
			fullEntryName.append(",");
		}
		fullEntryName.append(entry);
		entryNames.add(entry);
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setSortInfo(String sortInfo) {
		this.sortInfo = sortInfo;
	}

	public String buildCountHql() {
		if (countSingleTable) {
			String t = fullEntryName.toString(), w = where;
			t = t.substring(0, t.indexOf(","));
			w = w.replace("b.empiId = a.empiId", "1 = 1");
			return new StringBuffer("select count(*) from ").append(t)
					.append(w).toString();
		}
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(fullEntryName).append(where);
		return hql.toString();
	}

	public int getFieldCount() {
		return fields.size();
	}

	public String buildQueryHql() {
		StringBuffer hql = new StringBuffer();
		String sort = "";

		int n = fields.size();
		for (int i = 0; i < n; i++) {
			String f = alias.get(i) + "." + fields.get(i);
			hql.append(",").append(f);
		}
		if (sortInfo != null && !sortInfo.equals("")) {
			sort = " order by " + sortInfo;
		}
		hql.append(" from ").append(fullEntryName).append(where).append(sort);
		return "select " + hql.substring(1);
	}
}
