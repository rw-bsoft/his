/*
 * @(#)BschisDAO.java Created on 2011-12-15 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import phis.application.mds.source.MedicineUtils;
import phis.source.utils.CNDHelper;
import phis.source.utils.HQLHelper;
import ctd.controller.exception.ControllerException;
import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;
import ctd.validator.Validator;

/**
 * @description 基础的数据库操作。
 *
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 *
 */
public class BaseDAO {

	public static final String NEED_VALIDATION = "$needValidation";
	public static final String PAGE_NO = "pageNo";
	public static final String PAGE_SIZE = "pageSize";
	private Session session;
	private Context ctx;

	/**
	 * @param ctx
	 * @throws DataAccessException
	 */
	public BaseDAO() {
		this.ctx = ContextUtils.getContext();
		this.session = (Session) ctx.get(Context.DB_SESSION);
		if (this.session == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			this.session = sf.openSession();
			ctx.put(Context.DB_SESSION, this.session);
		}
	}

	/**
	 * @param ctx
	 * @throws DataAccessException
	 */
	public BaseDAO(Context ctx) {
		this.ctx = ctx;
		this.session = (Session) ctx.get(Context.DB_SESSION);
	}

	public BaseDAO(Context ctx, Session ss) {
		this.ctx = ctx;
		this.session = ss;
	}

	public Context getContext() {
		return this.ctx;
	}

	/**
	 * @return
	 */
	public boolean isReady() {
		return (session != null && session.isOpen());
	}

	/**
	 * 往指定的表中插入一条数据。
	 *
	 * @param entryName
	 * @param data
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> doInsert(String entryName,
			Map<String, Object> data, boolean validate)
			throws PersistentDataOperationException, ValidateException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Schema sc = SchemaController.instance().get(entryName);
			SimpleDAO simpleDAO = null;
			simpleDAO = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, data, ctx);
			}
			return simpleDAO.create(data);
		} catch (DataAccessException e) {
			throw new PersistentDataOperationException(e);
		} catch (ServiceException e) {
			throw new PersistentDataOperationException(e);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 将数据保存到表
	 *
	 * @param op
	 * @param record
	 * @return
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> doSave(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ValidateException, PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		SimpleDAO dao = null;
		Map<String, Object> genValues = null;
		try {
			Schema sc = SchemaController.instance().get(entryName);
			;
			dao = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, record, ctx);
			}
			if (StringUtils.isEmpty(op)) {
				op = "create";
			}
			if (op.equals("create")) {
				genValues = dao.create(record);
			} else {
				genValues = dao.update(record);
			}
		} catch (DataAccessException e) {
			throw new PersistentDataOperationException(e);
		} catch (ServiceException e) {
			throw new PersistentDataOperationException(e);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}

		return genValues;
	}

	/**
	 * 将数据保存到表(add by yangl) 增加根据条件更新数据操作
	 *
	 * @param op
	 * @param record
	 * @return
	 * @throws ValidateException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> doSave(String op, String entryName,
			Map<String, Object> record, Map<String, Object> where,
			boolean validate) throws ValidateException,
			PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		SimpleDAO dao = null;
		Map<String, Object> genValues = null;
		try {
			Schema sc = SchemaController.instance().get(entryName);
			;
			dao = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, record, ctx);
			}
			if (StringUtils.isEmpty(op)) {
				op = "create";
			}
			if (op.equals("create")) {
				genValues = dao.create(record);
			} else {
				genValues = dao.update(record);
			}
		} catch (DataAccessException e) {
			throw new PersistentDataOperationException(e);
		} catch (ServiceException e) {
			throw new PersistentDataOperationException(e);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}

		return genValues;
	}

	/**
	 * 更新一条记录。注意：hql语句中日期型字段不要加“str（）”函数。
	 *
	 * @param hql
	 * @param parameters
	 * @throws PersistentDataOperationException
	 */
	public int doUpdate(String hql, Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createQuery(hql);
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					Object obj = parameters.get(key);
					setParameter(query, key, obj);
				}
			}
			int c = query.executeUpdate();
			session.flush();
			return c;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 根据主键删除一条数据。
	 *
	 * @param pkey
	 * @param sc
	 * @throws PersistentDataOperationException
	 */
	public void doRemove(Object pkey, String entryName)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e1) {
			throw new PersistentDataOperationException(e1);
		}
		;
		if (sc == null) {
			throw new PersistentDataOperationException(
					"Schema is not defined: " + entryName);
		}
		SimpleDAO dao = null;
		try {
			dao = new SimpleDAO(sc, ctx);
			dao.remove(pkey);
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 根据某一字段值删除数据
	 *
	 * @param field
	 * @param value
	 * @throws PersistentDataOperationException
	 */
	public void doRemove(String field, Object value, String entryName)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (sc == null) {
			throw new PersistentDataOperationException(
					"Schema is not defined: " + entryName);
		}
		SimpleDAO dao = null;
		try {
			dao = new SimpleDAO(sc, ctx);
			dao.removeByFieldValue(field, value);
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 根据主键查找,是否加载字典数据由ifSetDicInfo决定(单表查询)
	 *
	 * @param entryName
	 * @param pkey
	 * @return
	 * @throws PersistnetDataOperationException
	 */
	public Map<String, Object> doLoad(String entryName, String pkey)
			throws PersistentDataOperationException {
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		String key = sc.getKeyItem().getId();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", key, "s", pkey);
		return doLoad(cnd, entryName);
	}

	/**
	 * 根据主键查找,是否加载字典数据由ifSetDicInfo决定(单表查询)
	 *
	 * @param entryName
	 * @param pkey
	 * @return
	 * @throws PersistnetDataOperationException
	 */
	public Map<String, Object> doLoad(String entryName, Object pkey)
			throws PersistentDataOperationException {
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		String key = sc.getKeyItem().getId();
		// db2状态下不同主键对应的类型
		String keyType = "s";
		if (pkey instanceof Integer) {
			keyType = "i";
		} else if (pkey instanceof Long) {
			keyType = "d";
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", key, keyType, pkey);
		return doLoad(cnd, entryName);
	}

	/**
	 * 根据查询条件查询一条数据，当参数ifSetDicInfo设置为true时做字典转换处理(单表查询)
	 *
	 * @param cnd
	 * @param schemaName
	 * @param needDictionary
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doLoad(List<?> cnd, String schemaName)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createQuery(
					HQLHelper.buildQueryHql(cnd, null, schemaName, ctx))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Map<String, Object> m = (Map<String, Object>) query.uniqueResult();
			session.flush();
			return m;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 不处理字典 以Map对象返回唯一查询结果，map的key值由hql语句中定义。 hql中字段必须带别名。
	 * 注意：hql语句中日期型字段不要加“str（）”函数。
	 *
	 * @param hql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doLoad(String hql, Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createQuery(hql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			String[] paramNames=query.getNamedParameters();
			if (parameters != null && !parameters.isEmpty()) {
				for(String name:paramNames){
					Object obj = parameters.get(name);
					if(obj!=null){
						setParameter(query, name, obj);
					}
				}
			}
//			if (parameters != null && !parameters.isEmpty()) {
//				for (String key : parameters.keySet()) {
//					Object obj = parameters.get(key);
//					setParameter(query, key, obj);
//				}
//			}
			Map<String, Object> m = (Map<String, Object>) query.uniqueResult();
			session.flush();
			return m;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 单表查询、结果不做字典处理、无分页
	 *
	 * @param cnd
	 * @param orderBy
	 * @param entryName
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doQuery(List<?> cnd, String orderBy,
			String entryName) throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		if (sc == null) {
			throw new PersistentDataOperationException(
					"Schema is not defined: " + entryName);
		}
		List<Map<String, Object>> list;
		try {
			Query query = session.createQuery(
					HQLHelper.buildQueryHql(cnd, orderBy, entryName, ctx))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			list = (List<Map<String, Object>>) query.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		List<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> rec = new HashMap<String, Object>();
			resBody.add(rec);
			for (String key : map.keySet()) {
				rec.put(key, map.get(key));
				SchemaItem si = sc.getItem(key);
				if (si.isCodedValue()) {
					String value = String.valueOf(map.get(key));
					rec.put(key + "_text", si.toDisplayValue(value));
				}
			}
		}
		return resBody;
	}

	/**
	 * 单表查询、不对结果进行字典处理、支持分页
	 * 查询所有符合条件的记录，以一个列表返回，每条记录以一个Map对象表示，map的key值由hql语句中定义。 hql中字段必须带别名。
	 * 注意：1.hql语句中日期型字段不要加“str（）”函数。 2.在参数列表中可以指定”first“和”max“实现返回结果分页。 ADD BY
	 * LYL: first的值应该是 (当前页面-1)*每页条数，max 是每页的条数
	 *
	 * @param hql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doQuery(String hql,
			Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createQuery(hql);
			if (hql.indexOf(" as ") > 0) {
				query = query
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					if (key.equals("first")) {
						query.setFirstResult((Integer) parameters.get(key));
					} else if (key.equals("max")) {
						query.setMaxResults((Integer) parameters.get(key));
					} else {
						setParameter(query, key, parameters.get(key));
					}
				}
			}
			List<Map<String, Object>> l = (List<Map<String, Object>>) query
					.list();
			session.flush();
			return l;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * schema多表关联、处理字典项、无分页。
	 *
	 * @param cnd
	 * @param orderBy
	 * @param schema
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doList(List<?> cnd, String orderBy,
			String schema) throws PersistentDataOperationException {
		return (List<Map<String, Object>>) doList(cnd, orderBy, schema, 1, -1,
				"1").get("body");
	}

	/**
	 * schema多表关联、处理字典项、可分页
	 *
	 * @param cnd
	 * @param orderBy
	 * @param schema
	 * @param PageSize
	 * @param pageNo
	 * @param queryCndsType
	 *            值为""会查询出所有的值，跳过用户权限的数据过滤。
	 * @return Map key{body : 查询结果记录集, totalCount : 总记录数}
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> doList(List<?> cnd, String orderBy,
			String schema, int pageNo, int pageSize, String queryCndsType)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}

		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schema);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		if (sc == null) {
			throw new PersistentDataOperationException(
					"Schema is not defined: " + schema);
		}

		SimpleDAO dao = null;
		try {
			dao = new SimpleDAO(sc, ctx);
			QueryResult rs = dao.find(cnd, pageNo, pageSize, queryCndsType,
					orderBy);
			Map<String, Object> rsMap = new HashMap<String, Object>();
			rsMap.put("body", rs.getRecords());
			rsMap.put("totalCount", rs.getTotalCount());
			return rsMap;
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 设置查询参数。
	 *
	 * @param query
	 * @param name
	 * @param value
	 */
	private void setParameter(Query query, String name, Object value) {
		if (value instanceof Collection<?>) {
			query.setParameterList(name, (Collection<?>) value);
		} else if (value instanceof Object[]) {
			query.setParameterList(name, (Object[]) value);
		} else {
			query.setParameter(name, value);
		}
	}

	/**
	 * 根据指定字段条件删除数据。
	 *
	 * @param pkey
	 * @param sc
	 * @throws PersistentDataOperationException
	 */
	public void removeByFieldValue(String fName, Object v, String entryName)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		if (sc == null) {
			throw new PersistentDataOperationException(
					"Schema is not defined: " + entryName);
		}
		SimpleDAO dao = null;
		try {
			dao = new SimpleDAO(sc, ctx);
			dao.removeByFieldValue(fName, v);
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 根据查询条件查询一条数据，当参数needDictionary设置为true时做字典转换处理
	 *
	 * @param cnd
	 * @param schemaName
	 * @param needDictionary
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doLoad(List<?> cnd, String schemaName,
			boolean needDictionary) throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createQuery(
					HQLHelper.buildQueryHql(cnd, null, schemaName, ctx))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Map<String, Object> m = (Map<String, Object>) query.uniqueResult();
			session.flush();

			if (needDictionary) {
				Schema schema = SchemaController.instance().get(schemaName);
				List<SchemaItem> itemList = schema.getItems();
				for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
						.hasNext();) {
					SchemaItem item = iterator.next();
					if (item.isCodedValue()) {
						String schemaKey = item.getId();
						String key = StringUtils.trimToEmpty((String) m
								.get(schemaKey));
						if (!key.equals("") && key != null) {
							Map<String, String> dicValue = new HashMap<String, String>();
							dicValue.put("key", key);
							dicValue.put("text", item.toDisplayValue(key));
							m.put(schemaKey, dicValue);
						}
					}
				}
			}
			return m;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2012-6-7
	 * @description 支持sql查询的数据库操作方法,其他类似doQuery(String hql,Map<String, Object>
	 *              parameters)
	 * @updateInfo
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doSqlQuery(String sql,
			Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			SQLQuery query = session.createSQLQuery(sql);
			if (sql.toLowerCase().indexOf(" as ") > 0) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					if (key.equals("first")) {
						query.setFirstResult((Integer) parameters.get(key));
					} else if (key.equals("max")) {
						query.setMaxResults((Integer) parameters.get(key));
					} else {
						setParameter(query, key, parameters.get(key));
					}
				}
			}
			List<Map<String, Object>> l = (List<Map<String, Object>>) query
					.list();
			session.flush();
			return l;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}
	/**
	 * 查询得到元素类型为Map的list
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doSqlQueryForMap(String sql,
			Map<String, Object>... parameters)
					throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			SQLQuery query = session.createSQLQuery(sql);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			if (parameters != null && parameters.length>0) {
				Map<String, Object> parameter=parameters[0];
				if(parameter!=null && !parameter.isEmpty()){
					for (String key : parameter.keySet()) {
						if (key.equals("first")) {
							query.setFirstResult((Integer) parameter.get(key));
						} else if (key.equals("max")) {
							query.setMaxResults((Integer) parameter.get(key));
						} else {
							setParameter(query, key, parameter.get(key));
						}
					}
				}
			}
			List<Map<String, Object>> l = (List<Map<String, Object>>) query
					.list();
			session.flush();
			return l;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}
	/**
	 * 查询得到Map
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> doSqlLoad(String sql,
			Map<String, Object>... parameters)
					throws PersistentDataOperationException {
		List<Map<String, Object>> list=this.doSqlQueryForMap(sql, parameters);
		if(list==null || list.size()==0){
			return null;
		}
		if(list.size()>1){
			throw new PersistentDataOperationException("单记录查询返回多条记录！");
		}
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object> doSqlPagedQuery(String sql,Map<String,Object> request,Map<String,Object> sqlParams) throws PersistentDataOperationException{
		Map<String,Object> result=new HashMap<String, Object>();
		if(request.get(PAGE_NO)==null){
			throw new PersistentDataOperationException("paging query needs 'pageNo' parameter.");
		}
		if(request.get(PAGE_SIZE)==null){
			throw new PersistentDataOperationException("paging query needs 'pageSize' parameter.");
		}
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			String countSql="select count(*) from ("+sql+")";
			SQLQuery query = session.createSQLQuery(countSql);
			if (sqlParams != null && !sqlParams.isEmpty()) {
				for (String key : sqlParams.keySet()) {
					setParameter(query, key, sqlParams.get(key));
				}
			}
			int totalCount=((BigDecimal) query.uniqueResult()).intValue();
			
			int pageNo=(Integer)request.get(PAGE_NO);
			int pageSize=(Integer)request.get(PAGE_SIZE);
			int recordStart=(pageNo-1)*pageSize;
			query = session.createSQLQuery(sql);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setFirstResult(recordStart);
			query.setMaxResults(pageSize);
			if (sqlParams != null && !sqlParams.isEmpty()) {
				for (String key : sqlParams.keySet()) {
					setParameter(query, key, sqlParams.get(key));
				}
			}
			List<Map<String, Object>> record = (List<Map<String, Object>>) query.list();
			result.put("body", record);
			result.put("totalCount", totalCount);
			session.flush();
			return result;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}
	public Map<String,Object> doSqlPagedQuery(String sql,Map<String,Object> request) throws PersistentDataOperationException{
		return this.doSqlPagedQuery(sql, request,null);
	}
	
	/**
	 *
	 * @author caijy
	 * @createDate 2012-6-7
	 * @description 支持sql语句的update操作,其他同doUpdate(String hql,Map<String, Object>
	 *              parameters)
	 * @updateInfo
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws PersistentDataOperationException
	 */
	public int doSqlUpdate(String sql, Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			SQLQuery query = session.createSQLQuery(sql);
			setParameters(query, parameters);
			int c = query.executeUpdate();
			session.flush();
			return c;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2012-6-11
	 * @description 查询记录条数
	 * @updateInfo
	 * @param tableName
	 *            表名(支持多表)
	 * @param hqlWhere
	 *            where条件(不带"where")
	 * @param parameters
	 *            参数
	 * @return 记录条数
	 * @throws PersistentDataOperationException
	 */
	public Long doSqlCount(String tableName, String hqlWhere,
						   Map<String, Object> parameters)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) as TOTAL from ").append(tableName)
				.append(" where ").append(hqlWhere);
		try {
			List<Map<String, Object>> count = doSqlQuery(hql.toString(), parameters);
			if(count==null||count.get(0)==null||count.get(0).size()==0){
				return 0l;
			}
			return MedicineUtils.parseLong(count.get(0).get("TOTAL")) ;
		} catch (PersistentDataOperationException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2012-6-11
	 * @description 查询记录条数
	 * @updateInfo
	 * @param tableName
	 *            表名(支持多表)
	 * @param hqlWhere
	 *            where条件(不带"where")
	 * @param parameters
	 *            参数
	 * @return 记录条数
	 * @throws PersistentDataOperationException
	 */
	public Long doCount(String tableName, String hqlWhere,
			Map<String, Object> parameters)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) as TOTAL from ").append(tableName)
				.append(" where ").append(hqlWhere);
		try {
			List<Map<String, Object>> count = doQuery(hql.toString(), parameters);
			if(count==null||count.get(0)==null||count.get(0).size()==0){
				return 0l;
			}
			return MedicineUtils.parseLong(count.get(0).get("TOTAL")) ;
		} catch (PersistentDataOperationException e) {
			throw new PersistentDataOperationException(e);
		}
	}
	private void setParameters(Query query,Map<String, Object> parameters){
		String[] paramNames=query.getNamedParameters();
		if (paramNames!=null && paramNames.length>0 && parameters != null && !parameters.isEmpty()) {
			for(String name:paramNames){
				Object obj = parameters.get(name);
				if(obj!=null){
					setParameter(query, name, obj);
				}
			}
		}
	}
}
