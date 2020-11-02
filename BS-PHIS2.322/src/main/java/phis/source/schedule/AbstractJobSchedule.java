/**
 * @(#)HypertensionYearFixGroupSchedule.java Created on 2012-5-25 下午16:11:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionException;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import ctd.account.AccountCenter;
import ctd.account.user.User;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

//@author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
public abstract class AbstractJobSchedule implements IJobSchedule,
		BSPHISEntryNames {
	private static final Log logger = LogFactory
			.getLog(AbstractJobSchedule.class);

	protected SessionFactory sessionFactory = null;

	//获取BaseDAO
	public static BaseDAO getDAO(Session session) {
		BaseDAO dao = new BaseDAO(ContextUtils.getContext(), session);
		return dao;
	}

	//@see com.bsoft.bschis.schedule.IJobSchedule#execute()
	@Override
	public void execute() throws JobExecutionException {
		Session session = null;
		// Transaction trx = null;
		try {
			session = sessionFactory.openSession();
			// trx = session.beginTransaction();//开启事务
			Context ctx = ContextUtils.getContext();
			//if (!ctx.containsKey(Context.DB_SESSION)) {
				ctx.put(Context.DB_SESSION, session);
			//}
			BaseDAO dao = getDAO(session);

			if (!ctx.containsKey(Context.USER_ROLE_TOKEN)) {
				User user = AccountCenter.getUser("0310581X");
				Object[] urs = user.getUserRoleTokens().toArray();
				ctx.put(Context.USER_ROLE_TOKEN, urs[0]);
			}
			// 执行任务
			this.doJob(dao, ctx);
			
			// trx.commit();//提交事务
		} catch (Exception e) {
			// trx.rollback();
			logger.error("Execute the Job failed.", e);
			throw new JobExecutionException(e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	//定时任务业务定义
	public abstract void doJob(BaseDAO dao, Context ctx)
			throws ModelDataOperationException;
	//获得sessionFactory
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	//sessionFactory
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
