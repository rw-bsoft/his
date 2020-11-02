/**
 * @(#)RestarContext.java Created on 2012-8-7 下午03:20:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import java.util.Date;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.quartz.JobExecutionException;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.pub.StatModel;
import chis.source.schedule.AbstractJobSchedule;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class RestarContext implements Service {
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
//		WebApplicationContext wac = (WebApplicationContext) ctx
//				.get(Context.APP_CONTEXT);
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		AbstractJobSchedule job = new AbstractJobSchedule() {
			public void doJob(BaseDAO dao) throws ModelDataOperationException {
				Date d = new Date();
				StatModel statModel = new StatModel(dao, d);
				statModel.caculate();
			}
		};
		job.setSessionFactory(sf);
		try {
			job.execute();
		} catch (JobExecutionException e) {
		}
	}
}
