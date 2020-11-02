/**
 * @(#)AbstractConfigControllor.java 创建于 2011-1-7 下午06:40:01
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;

import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public abstract class AbstractConfigControllor implements ConfigControllor {

	protected String moduleId = null;

	protected Map<String, Boolean> dataMap = null;

	/**
	 * @return the entryName
	 */
	public abstract String getEntryName();

	/**
	 * @return the schemaName
	 */
	public abstract String getSchemaName();

	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId
	 *            the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @param dataMap
	 *            the dataMap to set
	 */
	public void setModuleData(Map<String, Boolean> dataMap) {
		this.dataMap = dataMap;
	}

	/**
	 * @return the dataMap
	 */
	public Map<String, Boolean> getModuleData() {
		if (dataMap == null) {
			dataMap = new HashMap<String, Boolean>();
		}
		return dataMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bsoft.hzehr.biz.conf.control.ConfigControllor#isReadOnly(org.hibernate
	 * .Session, ctd.util.context.Context)
	 */
	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException, InvocationTargetException,
			ControllerException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(moduleId + "_readOnly", btnReadOnly(session, ctx));
		map.put(moduleId + "_fieldReadOnly", fieldReadOnly(session, ctx));
		return map;
	}

	/**
	 * 获取页面按钮的只读权限
	 * 
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean btnReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException {
		return false;
	}

	/**
	 * 获取页面控件的只读权限
	 * 
	 * @param map
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws InvocationTargetException
	 */
	protected Map<String, Object> fieldReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException, InvocationTargetException {
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		Object[] parameters = new Object[2];
		parameters[0] = session;
		parameters[1] = ctx;
		Class<?>[] clses = new Class[2];
		clses[0] = Session.class;
		clses[1] = Context.class;
		Method method = null;
		try {
			Schema sc = SchemaController.instance().get(getSchemaName());
			for (SchemaItem item : sc.getItems()) {
				String id = item.getId();
				if (item.getDisplayMode() == 0 || item.getDisplayMode() == 1 || item.isHidden()) {
					continue;
				}
				StringBuffer methodName = new StringBuffer(id);
				methodName.append("ReadOnly");
				method = this.getClass().getDeclaredMethod(
						methodName.toString(), clses);
				method.setAccessible(true);
				Object obj = method.invoke(this, parameters);
				fieldMap.put(id + "_readOnly", obj);
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
		return fieldMap;
	}

	/**
	 * 判断是否有计划存在
	 * 
	 * @param instanceType1
	 * @param instanceType2
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean hasPlan(Session session, String... instanceTypes)
			throws PersistentDataOperationException {
		String planType = getEntryName() + "_hasPlan";
		if (dataMap.get(planType) != null) {
			return dataMap.get(planType);
		}
		StringBuffer hql = new StringBuffer("select count(*) from ")
				.append(BSCHISEntryNames.PUB_VisitPlan);
		boolean hasType = false;
		if (instanceTypes != null && instanceTypes.length > 0) {
			if (instanceTypes.length == 1) {
				hql.append(" where businessType =(");
			} else {
				hql.append(" where businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < instanceTypes.length; i++) {
				args.append(",:businessType").append(i);
			}
			args.append(")");
			hql.append(args.substring(1));
			hasType = true;
		}

		Long l = 0l;
		try {
			Query query = session.createQuery(hql.toString());
			if (hasType) {
				for (int i = 0; i < instanceTypes.length; i++) {
					query.setString("businessType" + i, instanceTypes[i]);
				}
			}
			l = (Long) query.uniqueResult();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		boolean hasPlan = l > 0 ? true : false;
		dataMap.put(planType, hasPlan);
		return hasPlan;
	}

	/**
	 * 判断是否有档案存在
	 * 
	 * @param session
	 * @return
	 * @throws PersistentDataOperationException
	 */
	protected boolean hasRecord(String tableName, Session session)
			throws PersistentDataOperationException {
		String entryName;
		if (tableName == null || "".equals(tableName)) {
			entryName = getEntryName();
		} else {
			entryName = tableName;
		}
		String recordType = getEntryName() + "_hasRecord";
		if (dataMap.get(recordType) != null) {
			return dataMap.get(recordType);
		}
		String hql = new StringBuffer("select count(*) from ")
				.append(entryName).toString();
		Long l = 0l;
		try {
			Query query = session.createQuery(hql);
			l = (Long) query.uniqueResult();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		boolean hasRecord = l > 0 ? true : false;
		dataMap.put(recordType, hasRecord);
		return hasRecord;
	}
}
