/**
 * @(#)ScheduleBase.java Created on 2012-5-25 下午12:44:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;

import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public abstract class ScheduleBase implements BSCHISEntryNames {
	private static final Log logger = LogFactory.getLog(ScheduleBase.class);

	protected SessionFactory sessionFactory = null;
	
	protected BaseDAO getBaseDAO(){
		Context ctx = new Context();
		Session session = sessionFactory.openSession();
		ctx.put(Context.DB_SESSION, session);
		BaseDAO dao = new BaseDAO();
		return dao;
	}
	
	/**
	 * 获得sessionFactory
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 设置sessionFactory
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
