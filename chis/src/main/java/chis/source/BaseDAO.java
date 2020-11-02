/*
 * @(#)Bschis.sourceDAO.java Created on 2011-12-15 下午3:03:44
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.util.CNDHelper;
import chis.source.util.HQLHelper;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.controller.exception.ControllerException;
import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
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
	private Session session;
	private Context ctx;
	private Transaction trx;

	Logger log = LoggerFactory.getLogger(BaseDAO.class);

	/**
	 * @Description:依据spring中配置的SessionFactory之bean的名称
	 *                                                 获取新的Session来构建一个新的BaseDao对象
	 *                                                 用于切换数据源
	 * @param sessionFactoryBeanName
	 * @param ctx
	 * @return
	 * @author ChenXianRui 2017-6-14 上午9:40:19
	 * @Modify:
	 */
	public static BaseDAO getBaseDAO(String sessionFactoryBeanName, Context ctx) {
		SessionFactory sf = AppContextHolder.getBean(sessionFactoryBeanName,
				SessionFactory.class);
		Session mdbSession = sf.openSession();
		ctx.put(Context.DB_SESSION, mdbSession);
		BaseDAO dao = new BaseDAO(ctx, mdbSession);
		return dao;
	}
	/**
	 * @Description:依据spring中配置的SessionFactory之bean的名称
	 *                                                 获取新的Session来构建一个新的BaseDao对象
	 *                                                 用于切换数据源
	 * @param sessionFactoryBeanName
	 * @return
	 * @author ChenXianRui 2017-6-14 上午9:40:19
	 * @Modify:
	 */
	public static BaseDAO getBaseDAO(String sessionFactoryBeanName) {
		SessionFactory sf = AppContextHolder.getBean(sessionFactoryBeanName,
				SessionFactory.class);
		Session mdbSession = sf.openSession();
		Context ctx = new Context();
		ctx.put(Context.DB_SESSION, mdbSession);
		BaseDAO dao = new BaseDAO(ctx, mdbSession);
		return dao;
	}
	/**
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
	
	public BaseDAO(Context ctx, Session ss) {
		this.ctx = ctx;
		this.session = ss;
	}
	
	public Session getSession() {
		return session;
	}

	public void beginTransaction() throws DataAccessException {
		try {
			trx = session.getTransaction();
			trx.begin();
		} catch (HibernateException e) {
			throw new DataAccessException("BeginTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void commitTransaction() throws ServiceException {
		if (trx == null) {
			return;
		}
		try {
			trx.commit();
		} catch (HibernateException e) {
			throw new ServiceException("CommitTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void rollbackTransaction() throws ServiceException {
		if (trx == null) {
			return;
		}
		try {
			trx.rollback();
		} catch (HibernateException e) {
			throw new ServiceException("RollbackTransactionFailed:"
					+ e.getMessage(), e);
		}
	}

	public void closeSession() throws ServiceException {
		if (session == null || !session.isOpen()) {
			return;
		}
		try {
			session.close();
		} catch (HibernateException e) {
			throw new ServiceException("SessionCloseFailed:" + e.getMessage(),
					e);
		}
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
		SimpleDAO simpleDAO = null;
		try {
			Schema sc = SchemaController.instance().get(entryName);
			simpleDAO = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, data, ctx);
			}
			return simpleDAO.create(data);
			// Map<String,Object> res = simpleDAO.create(data);
			// return SchemaUtil.setDictionaryMessageForForm(res, entryName);
		} catch (DataAccessException e) {
			throw new PersistentDataOperationException(e);
		} catch (ServiceException e) {
			if (e instanceof ValidateException) {
				ValidateException ee = (ValidateException) e;
				log.error(ee.getValidMessage());
				e.printStackTrace();
			}
			throw new PersistentDataOperationException(e);
		} catch (ControllerException e) {
			e.printStackTrace();
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
			dao = new SimpleDAO(sc, ctx);
			if (validate) {
				Validator.validate(sc, record, ctx);
			}
			if (StringUtils.isEmpty(op)) {
				op = "create";
			}
			if (op.equals("create")) {
				return dao.create(record);
				// genValues = dao.create(record);
				// genValues = SchemaUtil.setDictionaryMessageForForm(genValues,
				// entryName);
			} else {
				genValues = dao.update(record);
			}
		} catch (DataAccessException e) {
			throw new PersistentDataOperationException(e);
		} catch (Exception e) {
			if (e instanceof ValidateException) {
				ValidateException ee = (ValidateException) e;
				log.error(ee.getValidMessage());
				e.printStackTrace();
			}
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
	 * 更新一条记录。注意：hql语句中日期型字段不要加“str（）”函数。
	 * 
	 * @param hql
	 * @param parameters
	 * @throws PersistentDataOperationException
	 */
	public int doSqlUpdate(String hql, Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createSQLQuery(hql);
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
	public void doRemove(String pkey, String entryName)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Schema sc = SchemaController.instance().get(entryName);
			if (sc == null) {
				throw new PersistentDataOperationException(
						"Schema is not defined: " + entryName);
			}
			SimpleDAO dao = null;

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
		try {
			Schema sc = SchemaController.instance().get(entryName);
			if (sc == null) {
				throw new PersistentDataOperationException(
						"Schema is not defined: " + entryName);
			}
			SimpleDAO dao = null;

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
		Schema sc;
		try {
			sc = SchemaController.instance().get(entryName);
		} catch (ControllerException e) {
			throw new PersistentDataOperationException(e);
		}
		String key = sc.getKey();
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
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					Object obj = parameters.get(key);
					setParameter(query, key, obj);
				}
			}
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
	public Map<String, Object> doSqlLoad(String hql, Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			Query query = session.createSQLQuery(hql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					Object obj = parameters.get(key);
					setParameter(query, key, obj);
				}
			}
			Map<String, Object> m = (Map<String, Object>) query.uniqueResult();
			session.flush();
			return m;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 单表查询、结果做字典处理、无分页
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
		List<Map<String, Object>> list;
		try {
			sc = SchemaController.instance().get(entryName);
			if (sc == null) {
				throw new PersistentDataOperationException(
						"Schema is not defined: " + entryName);
			}
			Query query = session.createQuery(
					HQLHelper.buildQueryHql(cnd, orderBy, entryName, ctx))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			list = (List<Map<String, Object>>) query.list();
			session.flush();
		} catch (Exception e) {
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
	 * 单表查询、结果不做字典处理、无分页
	 * @param cnd
	 * @param orderBy
	 * @param entryName
	 * @return
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doQueryNo(List<?> cnd, String orderBy,
			String entryName) throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		Schema sc = null;
		List<Map<String, Object>> list;
		try {
			sc = SchemaController.instance().get(entryName);
			if (sc == null) {
				throw new PersistentDataOperationException(
						"Schema is not defined: " + entryName);
			}
			Query query = session.createQuery(
					HQLHelper.buildQueryHql(cnd, orderBy, entryName, ctx))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			list = (List<Map<String, Object>>) query.list();
			session.flush();
		} catch (Exception e) {
			throw new PersistentDataOperationException(e);
		}
		List<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> rec = new HashMap<String, Object>();
			resBody.add(rec);
			for (String key : map.keySet()) {
				rec.put(key, map.get(key));
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
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					if ("first".equals(key)) {
						query.setFirstResult((Integer) parameters.get(key));
					} else if ("max".equals(key)) {
						query.setMaxResults((Integer) parameters.get(key));
					} else {
						setParameter(query, key, parameters.get(key));
					}
				}
			}
			if (sql.indexOf(" as ") > 0) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}
			List<Map<String, Object>> l = (List<Map<String, Object>>) query.list();
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
		try {
			Schema sc = SchemaController.instance().get(schema);
			if (sc == null) {
				throw new PersistentDataOperationException(
						"Schema is not defined: " + schema);
			}
			SimpleDAO dao = new SimpleDAO(sc, ctx);
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
			return  Long.parseLong(count.get(0).get("TOTAL")+"");
		} catch (PersistentDataOperationException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
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
	 * @modify by ChenXR 2017-04-07
	 */
	public Long doCount(String tableName, String hqlWhere,
						Map<String, Object> parameters)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) as TOTAL from ").append(tableName)
				.append(" where ").append(hqlWhere);
		try {
			List<Map<String, Object>> count = doQuery(hql.toString(),
					parameters);
			if (count == null || count.get(0) == null
					|| count.get(0).size() == 0) {
				return 0L;
			}
			return (Long) count.get(0).get("TOTAL");
		} catch (PersistentDataOperationException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 根据某一字段值单表查询数据、不对结果进行字典处理、不支持分页
	 *
	 * @param field
	 * @param value
	 * @throws PersistentDataOperationException
	 */
	public List<Map<String, Object>> doQuery(String field, Object value,
											 String entryName) throws PersistentDataOperationException {
		String hql = new StringBuffer().append("from ").append(entryName)
				.append(" where ").append(field).append(" =:fieldValue")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fieldValue", value);
		return doQuery(hql, parameters);
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
	public List<Map<String,Object>> doSqlQueryList(String sql,
												   Map<String, Object> parameters)
			throws PersistentDataOperationException {
		if (!isReady()) {
			throw new PersistentDataOperationException("DAO is not ready.");
		}
		try {
			SQLQuery query = session.createSQLQuery(sql);
			if (parameters != null && !parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					if ("first".equals(key)) {
						query.setFirstResult((Integer) parameters.get(key));
					} else if ("max".equals(key)) {
						query.setMaxResults((Integer) parameters.get(key));
					} else {
						setParameter(query, key, parameters.get(key));
					}
				}
			}
			if (sql.indexOf(" as ") > 0) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}
			List<Map<String, Object>> l = (List<Map<String, Object>>) query.list();
			session.flush();
			return l;
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

}
