/**
 * @(#)HypertensionYearFixGroupSchedule.java Created on 2012-5-25 下午16:11:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.quartz.JobExecutionException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import ctd.account.AccountCenter;
import ctd.account.user.User;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public abstract class AbstractJobSchedule implements IJobSchedule,
		BSCHISEntryNames {
	private static final Log logger = LogFactory
			.getLog(AbstractJobSchedule.class);

	protected SessionFactory sessionFactory = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.schedule.IJobSchedule#execute()
	 */
	@Override
	public void execute() throws JobExecutionException {
		Session session = null;
		Transaction trx = null;
		try {
			session = sessionFactory.openSession();
			trx = session.beginTransaction();// 开启事务
			
			Context ctx = ContextUtils.getContext();
//			if (!ctx.containsKey(Context.DB_SESSION)) {
				ctx.put(Context.DB_SESSION, session);
//			}

			BaseDAO dao = new BaseDAO(ctx, session);

			if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
				User user = AccountCenter.getUser("system");
				Object[] urs = user.getUserRoleTokens().toArray();
				ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
			}
			// 执行任务
			this.doJob(dao);

			trx.commit();// 提交事务
		} catch (Exception e) {
			trx.rollback();
			logger.error("Execute the Job failed.", e);
			throw new JobExecutionException(e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 定时任务业务定义
	 * 
	 * @param dao
	 */
	public abstract void doJob(BaseDAO dao) throws ModelDataOperationException;

	/**
	 * 获得sessionFactory
	 * 
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 设置sessionFactory
	 * 
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
