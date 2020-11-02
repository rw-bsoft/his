/**
 * @(#)HypertensionService.java Created on Aug 18, 2009 5:27:07 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionException;
import org.springframework.web.context.WebApplicationContext;
import chis.source.BaseDAO;
import chis.source.schedule.DefEvaluateJobDetailSchedule;
import chis.source.schedule.HealthEducationSchedule;
import chis.source.schedule.StatSchedule;
import chis.source.schedule.healthCheck.HealthCheckSchedule;
import chis.source.schedule.hypertension.HypertensionYearFixGroupSchedule;
import chis.source.schedule.psychosis.PsyAnnualAssessmentSchedule;
import chis.source.service.AbstractActionService;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class TestService extends AbstractActionService {

	private static final Log logger = LogFactory.getLog(TestService.class);

	/**
	 * @see com.bsoft.hzehr.biz.AbstractService#getTransactedActions()
	 */
	public List<String> getTransactedActions() {
		List<String> list = new ArrayList<String>();
		return list;
	}

	protected void executeScheduleService(HashMap<String, Object> jsonReq,
			HashMap<String, Object> jsonRes, Session session, Context ctx)
			throws ServiceException, JobExecutionException {

//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);

		StatSchedule ss = new StatSchedule();
		ss.execute();

		HypertensionYearFixGroupSchedule hy = new HypertensionYearFixGroupSchedule();
		hy.setSessionFactory(sf);
		hy.execute();

		PsyAnnualAssessmentSchedule pas = new PsyAnnualAssessmentSchedule();
		pas.setSessionFactory(sf);
		pas.execute();

		HealthCheckSchedule hcs = new HealthCheckSchedule();
		hcs.setSessionFactory(sf);
		hcs.execute();

		DefEvaluateJobDetailSchedule dej = new DefEvaluateJobDetailSchedule();
		dej.setSessionFactory(sf);
		dej.execute();

		HealthEducationSchedule hes = new HealthEducationSchedule();
		hes.setSessionFactory(sf);
		hes.execute();
	}

	public static void main(String args[]) {
		System.out.println(File.separator);
	}

	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		// TODO Auto-generated method stub
		WebApplicationContext wac = (WebApplicationContext) ctx
		.get(Context.APP_CONTEXT);
		SessionFactory sessionFactory = (SessionFactory) wac
		.getBean("mySessionFactory");
		
		DefEvaluateJobDetailSchedule dej = new DefEvaluateJobDetailSchedule();
		dej.setSessionFactory(sessionFactory);
		try {
			dej.execute();
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Date d = new Date();
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
//		SessionFactory sessionFactory = (SessionFactory) wac
//				.getBean("mySessionFactory");
//		Session session = sessionFactory.openSession();
//		Transaction trx = session.beginTransaction();// 开启事务
//		trx = session.beginTransaction();// 开启事务
//		BaseDAO dao = getDAO(session);
//		StatModel statModel = new StatModel(dao, d);
//		try {
//			statModel.caculate();
//			trx.commit();// 提交事务
//		} catch (ModelDataOperationException e) {
//			trx.rollback();
//			e.printStackTrace();
//		} finally {
//			if (session != null && session.isOpen()) {
//				session.close();
//			}
//		}
	}

	public static BaseDAO getDAO() {
		BaseDAO dao = new BaseDAO();
		return dao;
	}

}
