package ctd.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.dao.exception.DataAccessException;
import ctd.dao.exception.DeleteDataAccessException;
import ctd.dao.exception.InsertDataAccessException;
import ctd.dao.exception.QueryDataAccessException;
import ctd.dao.exception.UpdateDataAccessException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.support.TableDictionary;
import ctd.schema.DataTypes;
import ctd.schema.DisplayModes;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.security.Permission;
import ctd.security.support.condition.ConditionActionTypes;
import ctd.security.support.condition.FilterCondition;
import ctd.service.core.ServiceException;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.converter.ConversionUtils;
import ctd.util.exception.CodedBaseException;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

@SuppressWarnings({ "unchecked", "deprecation" })
public class SimpleDAO {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleDAO.class);
	public final static int UN_PAGED_QUERY_WARNING_LEVEL = 1000;
	public final static String DEFAULT_QUERYCNDSTYPE = ConditionActionTypes.QUERY;
	protected Session ss;
	protected Transaction trx;
	protected Context ctx;
	protected Schema sc;
	protected static final String _TEXT = "_text";

	public SimpleDAO(String schemaName, Context ctx) throws DataAccessException {
		this(SchemaController.instance().getSchema(schemaName), ctx);
	}

	public SimpleDAO(Session ss, String schemaName, Context ctx)
			throws DataAccessException {
		this(ss, SchemaController.instance().getSchema(schemaName), ctx);
	}

	public SimpleDAO(Schema schema, Context ctx) throws DataAccessException {
		this.sc = schema;
		this.ctx = ctx;
		init();
	}

	public SimpleDAO(Session ss, Schema schema, Context ctx)
			throws DataAccessException {
		this.ss = ss;
		this.ctx = ctx;
		this.sc = schema;
		init();
	}

	protected void init() throws DataAccessException {
		if (ctx == null || sc == null) {
			throw new DataAccessException("Context or Schema is null");
		}
		if (ss == null) {
			try {
				if (ctx.has(Context.DB_SESSION)) {
					ss = (Session) ctx.get(Context.DB_SESSION);
				} else {
					throw new DataAccessException("MissingDatebaseConnection");
				}
			} catch (HibernateException e) {
				throw new DataAccessException("OpenDatebaseConnectFailed:"
						+ e.getMessage(), e);
			}
		}
		trx = ss.getTransaction(); // init transaction but not begin
	}

	public void beginTransaction() throws DataAccessException {
		try {
			trx = ss.getTransaction();
			trx.begin();
		} catch (HibernateException e) {
			throw new DataAccessException("BeginTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void commitTransaction() throws DataAccessException {
		if (trx == null) {
			return;
		}
		try {
			trx.commit();
		} catch (HibernateException e) {
			throw new DataAccessException("CommitTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void rollbackTransaction() throws DataAccessException {
		if (trx == null) {
			return;
		}
		try {
			trx.rollback();
		} catch (HibernateException e) {
			throw new DataAccessException("RollbackTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void destroy() throws DataAccessException {
		if (ss == null || !ss.isOpen()) {
			return;
		}
		try {
			ss.close();
		} catch (HibernateException e) {
			throw new DataAccessException("SessionCloseFailed:"
					+ e.getMessage(), e);
		}
	}

	public boolean isReady() {
		return !(ss == null || !ss.isOpen() || sc == null || ctx == null);
	}

	public Map<String, Object> create(Map<String, Object> rec)
			throws ServiceException {
		if (!isReady()) {
			throw new InsertDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!p.getMode().isCreatable()) {
			throw new InsertDataAccessException("entity[" + sc.getId()
					+ "] can't create for user["
					+ UserRoleToken.getCurrent().getUserId() + "]@role["
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}

		Map<String, Object> GenValues = new HashMap<String, Object>();
		ctx.put("r", rec);
		HashMap<String, Object> o = (HashMap<String, Object>) ((HashMap<String, Object>) rec)
				.clone();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			String fid = it.getId();
			Object ov = o.get(fid);
			Object v = null;
			if (it.isEvalValue()) { // generate by server
				try {
					v = it.eval();
					if (it.isCodedValue()) {
						Map<String, Object> ed = new HashMap<String, Object>();
						ed.put("key", v);
						ed.put("text", it.toDisplayValue(v));
						GenValues.put(fid, ed);
					} else {
						GenValues.put(fid, v);
					}
				} catch (CodedBaseException e) {
					throw new ServiceException(e);
				}
			} else {
				if (ov != null) {
					o.put(fid, it.getTypeValue(ov));
					fillUp_TextFieldOfLargeDic(it, String.valueOf(ov), sc, o);
				}
				continue;
			}
			o.put(fid, it.getTypeValue(v));
		}
		try {
			Serializable pKey = ss.save(sc.getEntityName(), o);
			if (sc.isCompositeKey()) {
				GenValues
						.putAll((Map<? extends String, ? extends Object>) pKey);
			} else {
				GenValues.put(sc.getKey(), pKey);
			}
			return GenValues;
		} catch (Exception e) {
			throw new InsertDataAccessException(e.getMessage(), e);
		}
	}

	public Map<String, Object> update(Map<String, Object> rec)
			throws ServiceException {
		if (!isReady()) {
			throw new UpdateDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!p.getMode().isUpdatable()) {
			throw new UpdateDataAccessException("entity[" + sc.getId()
					+ "] can't update for user["
					+ UserRoleToken.getCurrent().getUserId() + "]@role["
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		HashMap<String, Object> GenValues = new HashMap<String, Object>();
		ctx.put("r", rec);
		Object pKey = null;
		if (sc.isCompositeKey()) {
			Map<String, Object> pKeys = new HashMap<String, Object>();
			List<SchemaItem> pKeyItems = sc.getKeyItems();
			for (SchemaItem si : pKeyItems) {
				pKeys.put(si.getId(), si.toPersistValue(rec.get(si.getId())));
			}
			pKey = pKeys;
		} else {
			SchemaItem pKeyItem = sc.getKeyItem();
			String pKeyStrVal = String.valueOf(rec.get(pKeyItem.getId()));
			pKey = sc.getKeyItem().toPersistValue(pKeyStrVal);
		}
		HashMap<String, Object> o = (HashMap<String, Object>) ss.get(tableName,
				(java.io.Serializable) pKey);
		if (o == null) {
			throw new UpdateDataAccessException("UpdateRecordNotFound:{schema:"
					+ entryName + ",pKey:" + pKey + "}");
		}
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (!it.isUpdate()) {
				continue;
			}
			String fid = it.getId();
			Object ov = rec.get(fid);
			Object v = null;
			if (it.isEvalValue()) {
				try {
					v = it.eval();
					if (it.isCodedValue()) {
						Map<String, Object> ed = new HashMap<String, Object>();
						ed.put("key", v);
						ed.put("text", it.toDisplayValue(v));
						GenValues.put(fid, ed);
					} else {
						GenValues.put(fid, v);
					}
				} catch (CodedBaseException e) {
					throw new ServiceException(e);
				}
			} else {
				if (ov != null) {
					o.put(fid, it.toPersistValue(ov));
					fillUp_TextFieldOfLargeDic(it, String.valueOf(ov), sc, o);
				}
				continue;
			}
			o.put(fid, it.toPersistValue(v));
		}
		try {
			ss.update(o);
			return GenValues;
		} catch (Exception e) {
			throw new UpdateDataAccessException(e.getMessage(), e);
		}
	}

	public static void fillUp_TextFieldOfLargeDic(SchemaItem it,
			String itValue, Schema sc, Map<String, Object> rec) {
		if (!it.isCodedValue() || it.isNotCover()) {
			return;
		}
		Dictionary dic = it.getRefDic();
		// 目前只 考虑层级且数据库字典的情况
		if (dic != null && dic instanceof TableDictionary
				&& ((TableDictionary) dic).hasCodeRule()) {
			String text;
			if (it.hasProperty("refAlias")) {
				text = it.getProperty("refAlias") + "."
						+ it.getProperty("refItemId");
			} else {
				text = it.getId();
			}
			if (sc.getItem(text + _TEXT) != null) {
				rec.put(text + _TEXT, dic.getText(itValue));
			}
		}
	}

	public void remove(Object id) throws DeleteDataAccessException {
		if (!isReady()) {
			throw new DeleteDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isRemovable())) {
			throw new DeleteDataAccessException("entity[" + sc.getId()
					+ "] can't remove for user["
					+ UserRoleToken.getCurrent().getUserId() + "]@role["
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		try {
			StringBuffer hql = new StringBuffer("delete from ").append(
					sc.getEntityName()).append(" where ");
			Query q = null;
			if (sc.isCompositeKey()) {
				List<SchemaItem> pkeys = sc.getKeyItems();
				for (int i = 0; i < pkeys.size(); i++) {
					SchemaItem si = pkeys.get(i);
					hql.append(si.getId()).append("=:").append(si.getId())
							.append(" and ");
				}
				q = ss.createSQLQuery(hql.substring(0, hql.lastIndexOf(" and ")));
				q.setProperties((Map<String, Object>) id);
			} else {
				SchemaItem key = sc.getKeyItem();
				hql.append(key.getId()).append("=:").append(key.getId());
				q = ss.createQuery(hql.toString());
				q.setParameter(key.getId(), id);
			}
			int effectCount = q.executeUpdate();
			if (effectCount < 1) {
				throw new DeleteDataAccessException("RecordNotFound");
			}
		} catch (Exception e) {
			throw new DeleteDataAccessException(e.getMessage(), e);
		}
	}

	public int removeByFieldValue(String fName, Object v)
			throws DeleteDataAccessException {
		if (!isReady()) {
			throw new DeleteDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isRemovable())) {
			throw new DeleteDataAccessException("entity[" + sc.getId()
					+ "] can't remove for user["
					+ UserRoleToken.getCurrent().getUserId() + "]@role["
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		try {
			SchemaItem f = sc.getItem(fName);
			if (f == null) {
				throw new DeleteDataAccessException(
						"LookupFieldNotDefined:{schema:" + entryName
								+ ",fName:" + fName + "}");
			}
			StringBuffer hql = new StringBuffer("delete from ");
			hql.append(tableName).append(" where ").append(fName);
			if (v == null) {
				hql.append(" is null");
			} else {
				hql.append("=:").append(fName);
			}
			Query q = ss.createQuery(hql.toString());
			if (v != null) {
				q.setParameter(fName, v);
			}
			int effectCount = q.executeUpdate();
			return effectCount;
		} catch (Exception e) {
			throw new DeleteDataAccessException(e.getMessage(), e);
		}
	}

	public Map<String, Object> load(Object id) throws QueryDataAccessException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String tableName = sc.getEntityName();
		try {
			Map<String, Object> rec = (HashMap<String, Object>) ss.get(
					tableName, (java.io.Serializable) id);
			if (rec == null) {
				return rec;
			}
			Map<String, Object> clone = (Map<String, Object>) ((HashMap<String, Object>) rec)
					.clone();
			marshallRecord(clone);
			return clone;
		} catch (Exception e) {
			throw new QueryDataAccessException("LoadException:"
					+ e.getMessage(), e);
		}
	}

	public void marshallRecord(Map<String, Object> rec)
			throws CodedBaseException {
		if (rec == null) {
			return;
		}
		ctx.put("r", rec);
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			String fid = it.getId();
			// if(!StringUtils.isEmpty(it.getProperty("ref"))){
			// continue;
			// }
			if (it.isCodedValue()) {
				Map<String, Object> o = new HashMap<String, Object>();
				Object fv = rec.get(fid);
				o.put("key", fv);
				if (rec.containsKey(fid + "_text")) {
					o.put("text", rec.get(fid + "_text"));
				} else {
					o.put("text", it.toDisplayValue(fv));
				}
				rec.put(fid, o);
			}
			if (it.isVirtual() && it.isEvalValue()) {
				Object ev = it.eval();
				rec.put(fid, ev);
			}
			if (DataTypes.BINARY.equals(it.getType())) {
				rec.put(fid,
						ConversionUtils.convert(rec.get(fid), String.class));
			}
		}
	}

	public Map<String, Object> load(Map<String, Object> fields)
			throws QueryDataAccessException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		try {
			StringBuffer hql = new StringBuffer("from ").append(tableName)
					.append(" where ");
			for (String fName : fields.keySet()) {
				SchemaItem it = sc.getItem(fName);
				if (it == null) {
					throw new QueryDataAccessException(
							"LookupFieldNotDefined:{schema:" + entryName
									+ ",fName:" + fName + "}");
				}
				hql.append(fName).append("=:").append(fName).append(" and ");
			}
			Query q = ss
					.createQuery(hql.substring(0, hql.lastIndexOf(" and ")));
			q.setProperties(fields);
			// 事务的提交会使改变的map update到DB
			Map<String, Object> rec = (HashMap<String, Object>) q
					.uniqueResult();
			if (rec == null) {
				return rec;
			}
			Map<String, Object> clone = (Map<String, Object>) ((HashMap<String, Object>) rec)
					.clone();
			marshallRecord(clone);
			return clone;
		} catch (Exception e) {
			throw new QueryDataAccessException("LoadException:"
					+ e.getMessage(), e);
		}
	}

	public Map<String, Object> load(String fName, Object v)
			throws QueryDataAccessException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		try {
			SchemaItem it = sc.getItem(fName);
			if (it == null) {
				throw new QueryDataAccessException(
						"LookupFieldNotDefined:{schema:" + entryName
								+ ",fName:" + fName + "}");
			}
			StringBuffer hql = new StringBuffer("from ").append(tableName)
					.append(" where ").append(fName).append("=:").append(fName);
			Query q = ss.createQuery(hql.toString());
			q.setParameter(fName, v);
			// 事务的提交会使改变的map update到DB
			Map<String, Object> rec = (HashMap<String, Object>) q
					.uniqueResult();
			if (rec == null) {
				return rec;
			}
			Map<String, Object> clone = (Map<String, Object>) ((HashMap<String, Object>) rec)
					.clone();
			marshallRecord(clone);
			return clone;
		} catch (Exception e) {
			throw new QueryDataAccessException("LoadException:"
					+ e.getMessage(), e);
		}
	}

	public List<Map<String, Object>> find(String fName, Object v)
			throws QueryDataAccessException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		try {
			SchemaItem it = sc.getItem(fName);
			if (it == null) {
				throw new QueryDataAccessException(
						"LookupFieldNotDefined:{schema:" + entryName
								+ ",fName:" + fName + "}");
			}
			StringBuffer hql = new StringBuffer("from ").append(tableName)
					.append(" where ").append(fName).append("=:").append(fName);
			Query q = ss.createQuery(hql.toString());
			q.setParameter(fName, v);
			// 事务的提交会使改变的map update到DB
			List<Map<String, Object>> rec = q.list();
			if (rec == null) {
				return rec;
			}
			List<Map<String, Object>> clone = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> m : rec) {
				Map<String, Object> clonem = (Map<String, Object>) ((HashMap<String, Object>) m)
						.clone();
				marshallRecord(clonem);
				clone.add(clonem);
			}
			return clone;
		} catch (Exception e) {
			throw new QueryDataAccessException("FindException:"
					+ e.getMessage(), e);
		}
	}

	public List<Map<String, Object>> find(Map<String, Object> fields)
			throws QueryDataAccessException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		String entryName = sc.getId();
		String tableName = sc.getEntityName();
		try {
			StringBuffer hql = new StringBuffer("from ").append(tableName)
					.append(" where ");
			for (String fName : fields.keySet()) {
				SchemaItem it = sc.getItem(fName);
				if (it == null) {
					throw new QueryDataAccessException(
							"LookupFieldNotDefined:{schema:" + entryName
									+ ",fName:" + fName + "}");
				}
				hql.append(fName).append("=:").append(fName).append(" and ");
			}
			Query q = ss
					.createQuery(hql.substring(0, hql.lastIndexOf(" and ")));
			q.setProperties(fields);

			// 事务的提交会使改变的map update到DB
			List<Map<String, Object>> rec = q.list();
			if (rec == null) {
				return rec;
			}
			List<Map<String, Object>> clone = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> m : rec) {
				Map<String, Object> clonem = (Map<String, Object>) ((HashMap<String, Object>) m)
						.clone();
				marshallRecord(clonem);
				clone.add(clonem);
			}
			return clone;
		} catch (Exception e) {
			throw new QueryDataAccessException("FindException:"
					+ e.getMessage(), e);
		}
	}

	public Map<String, Object> loadWithParentRelation(Object id)
			throws ServiceException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		QueryContext qc = new QueryContext();
		List<?> cnd = JSONUtils.parse("['eq',['$','a." + sc.getKey()
				+ "'],['s'," + id + "]]", List.class);
		initQueryContext(qc, cnd, null, null);
		if (qc.getFieldCount() == 0) {
			throw new QueryDataAccessException("NoValidFieldForQuery");
		}
		String hql = qc.buildQueryHql();
		// hql += " and a."+sc.getKey().getId()+"=:key";
		try {
			Query q = ss.createQuery(hql);
			// q.setParameter("key", id);
			Object[] r = (Object[]) q.uniqueResult();
			if (r == null) {
				return null;
			}
			Map<String, Object> rec = new HashMap<String, Object>();
			int colCount = qc.getFieldCount();
			for (int j = 0; j < colCount; j++) {
				Object v = r[j];
				SchemaItem si = sc.getItem(qc.getFullFieldName(j));
				String name = si.getId();
				rec.put(name, v);
			}
			marshallRecord(rec);
			return rec;
		} catch (Exception e) {
			throw new QueryDataAccessException("LoadException:"
					+ e.getMessage(), e);
		}
	}

	public QueryResult find(List<?> queryCnd) throws ServiceException {
		return find(queryCnd, 1, -1, null, null);
	}

	public QueryResult find(List<?> queryCnd, int pageNo, int pageSize,
			String queryCndsType, String sortInfo) throws ServiceException {
		if (!isReady()) {
			throw new QueryDataAccessException("DAO Not Ready");
		}
		Permission p = sc.lookupPremission();
		if (!(p.getMode().isAccessible())) {
			throw new QueryDataAccessException("entity[" + sc.getId()
					+ "] can't load for user["
					+ UserRoleToken.getCurrent().getUserId() + "@"
					+ UserRoleToken.getCurrent().getRoleId() + "]");
		}
		QueryContext qc = new QueryContext();
		initQueryContext(qc, queryCnd, queryCndsType, sortInfo);
		if (qc.getFieldCount() == 0) {
			throw new QueryDataAccessException("NoValidFieldForQuery");
		}
		try {
			// query record count
			Date startDt = new Date();
			String hql = qc.buildCountHql();
			Query q = ss.createSQLQuery(hql);
			if ("left".equals(sc.getProperty("joinMethod"))) {
				q = ss.createSQLQuery(hql);
			} else {
				q = ss.createQuery(hql);
			}
			List<?> ls = q.list();
			int first = (pageNo - 1) * pageSize;
			long totalCount = Long.parseLong(ls.iterator().next().toString());// ((Long)
																				// ls.iterator().next()).longValue();
			// 返回记录过多时，抛出异常，判断是否有异常查询 add by yangl
			// 程序未控制的好的情况，有发现将整个业务表数据加载到内存中导致服务器宕机的情况
			if (pageSize <= 0 && totalCount > 9999) {
				LOGGER.error("Load too much data in memory:{schema:"
						+ sc.getId() + ",totalRecord:" + totalCount + "}\nhql="
						+ hql);
				throw new QueryDataAccessException("一次性加载数据过多，请联系管理员协助排查问题！");
			}

			if (totalCount == 0 || first > totalCount) {
				Date endDt = new Date();
				long cost = endDt.getTime() - startDt.getTime();
				return new QueryResult(totalCount, first, pageSize, hql, cost,
						null);
			}
			hql = qc.buildQueryHql();
			if ("left".equals(sc.getProperty("joinMethod"))) {
				q = ss.createSQLQuery(hql);
			} else {
				q = ss.createQuery(hql);
			}
			if (pageSize > 0) {
				q.setFirstResult(first);
				q.setMaxResults(pageSize);
			} else {
				if (totalCount > UN_PAGED_QUERY_WARNING_LEVEL) {
					LOGGER.warn("UnPagedRecordRetrive:{schema:" + sc.getId()
							+ ",totalRecord:" + totalCount + "}\nhql=" + hql);
				}
			}
			List<Object[]> records = q.list();
			Date endDt = new Date();
			long cost = endDt.getTime() - startDt.getTime();
			int colCount = qc.getFieldCount();
			int rowCount = records.size();
			List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < rowCount; i++) {
				Object[] r = records.get(i);
				Map<String, Object> o = new HashMap<String, Object>();
				rs.add(o);
				for (int j = 0; j < colCount; j++) {
					Object v = r[j];
					SchemaItem si = sc.getItem(qc.getFullFieldName(j));
					String name = si.getId();
					o.put(name, v);
					if (si.isCodedValue()) {
						if (sc.getItem(name + "_text") == null) {
							o.put(name + "_text", si.toDisplayValue(v));
						}
					}
				}
			}
			return new QueryResult(totalCount, first, pageSize, hql, cost, rs);
		} catch (Exception e) {
			throw new QueryDataAccessException(e.getMessage(), e);
		}
	}

	protected void initQueryContext(QueryContext qc, List<?> queryCnd,
			String queryCndsType, String sortInfo) throws ServiceException {
		qc.addEntryName(sc.getEntityName() + " a");
		List<Object> cnds = new ArrayList<Object>();
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if (!(p.getMode().isAccessible())) {
				continue;
			}
			if (it.getDisplayMode() == DisplayModes.NO_LIST_DATA) {
				continue;
			}
			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				qc.addField(fid, refAlias);
				if (loadedRelation.containsKey(refAlias)) {
					continue;
				}
				SchemaRelation sr = sc.getRelationByAlias((String) it
						.getProperty("refAlias"));
				if ("left".equals(sc.getProperty("joinMethod"))) {
					try {
						qc.addEntryNameByJoin(sr.getFullEntryName(),
								sr.getJoinWay(), sr.getJoinCondition());
					} catch (ExpException e) {
						throw new ServiceException(e);
					}
				} else {
					qc.addEntryName(sr.getFullEntryName());
					List<?> cd = sr.getJoinCondition();
					if (cd != null) {
						cnds.add(cd);
					}
				}
				loadedRelation.put(refAlias, true);
			} else {
				qc.addField(fid, "a");
			}
		}

		if (queryCnd != null && !queryCnd.isEmpty()) {
			cnds.add(queryCnd);
		}

		if (StringUtils.isEmpty(queryCndsType)) {
			queryCndsType = DEFAULT_QUERYCNDSTYPE;
		}
		FilterCondition c = (FilterCondition) sc.lookupCondition(queryCndsType);
		if (c != null) {
			List<Object> roleCnd = (List<Object>) c.getDefine();
			if (roleCnd != null && !roleCnd.isEmpty()) {
				cnds.add(roleCnd);
			}
		}
		List<Object> whereCnd = null;
		int cndCount = cnds.size();
		if (cndCount == 0) {
			whereCnd = (List<Object>) queryCnd;
		} else if (cndCount == 1) {
			whereCnd = (List<Object>) cnds.get(0);
		} else {
			whereCnd = new ArrayList<Object>();
			whereCnd.add("and");
			for (Object cd : cnds) {
				whereCnd.add((List<Object>) cd);
			}
		}
		if (whereCnd != null && !whereCnd.isEmpty()) {
			try {
				String where = " where "
						+ ExpressionProcessor.instance().toString(whereCnd);// ExpRunner.toString(whereCnd,ctx);
				qc.setWhere(where);
			} catch (ExpException e) {
				throw new ServiceException(e);
			}
		}
		// set sortinfo
		if (StringUtils.isEmpty(sortInfo)) {
			sortInfo = sc.getSortInfo();
		}
		qc.setSortInfo(sortInfo);

	}
}
