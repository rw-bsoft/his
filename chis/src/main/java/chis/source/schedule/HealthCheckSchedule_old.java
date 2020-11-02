/**
 * @(#)HypertensionYearFixGroupSchedule.java Created on 2010-9-14 上午11:11:02
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.quartz.JobExecutionException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import ctd.validator.ValidateException;

/**
 * @description 慢病健康检查
 * 
 * @author <a href="mailto:taoy@bsoft.com.cn">taoy</a>
 */
public class HealthCheckSchedule_old implements BSCHISEntryNames {

	private static final Log logger = LogFactory
			.getLog(HealthCheckSchedule_old.class);

	private SessionFactory sessionFactory = null;

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute() throws JobExecutionException {
		Session session = null;
		Transaction trx = null;
		try {
			session = sessionFactory.openSession();
			trx = session.beginTransaction();
			// @@ 清除上一年评估任务（年度健康检查），因评估不能补录。
			deleteLastYearTask(session);
			// @@ 以及年度检查
			createNeedYearCheckHealthe(session);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			throw new JobExecutionException(e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 删去往年未完成的年度检查。
	 * 
	 * @param session
	 * @throws PersistentDataOperationException
	 */
	private void deleteLastYearTask(Session session)
			throws PersistentDataOperationException {
		String hql = new StringBuffer("delete from ").append(PUB_WorkList)
				.append(" where workType=:workType").toString();
		try {
			Query query = session.createQuery(hql);
			query.setString("workType", "09");
			query.executeUpdate();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	/**
	 * 创建年度健康检查任务
	 * 
	 * @param session
	 * @throws PersistentDataOperationException
	 * @throws ValidateException 
	 */
	public void createNeedYearCheckHealthe(Session session)
			throws PersistentDataOperationException, ValidateException {
		// 高血压提醒
		String hql = new StringBuffer(
				"select a.phrId as recordId,a.empiId as empiId ,a.manaDoctorId as doctorId ,a.manaUnitId as manaUnitId,(select max(b.checkDate) from ")
				.append(HC_HealthCheck)
				.append(" b where a.phrId = b.phrId) as maxDate from ")
				.append(MDC_HypertensionRecord).append(" a")
				.append(" where a.status = :status").toString();
		List<Map<String, Object>> list = null;
		try {
			Query query = session.createQuery(hql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString("status",
					String.valueOf(Constants.CODE_STATUS_NORMAL));
			list = query.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
		// 糖尿病提醒
		String hql1 = new StringBuffer(
				"select a.phrId as recordId,a.empiId as empiId ,a.manaDoctorId as doctorId ,a.manaUnitId as manaUnitId,(select max(b.checkDate) from ")
				.append(HC_HealthCheck)
				.append(" b where a.phrId = b.phrId) as maxDate from ")
				.append(MDC_HypertensionRecord).append(" a")
				.append(" where a.status = :status").toString();
		List<Map<String, Object>> list1 = null;
		try {
			Query query1 = session.createQuery(hql1).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			query1.setString("status",
					String.valueOf(Constants.CODE_STATUS_NORMAL));
			list1 = query1.list();
			session.flush();
		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}

		HashSet<Map<String, Object>> set = new HashSet<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			set.add(map);
		}
		for (Map<String, Object> map : list1) {
			set.add(map);
		}

		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		for (Map<String, Object> r : set) {
			Date maxCreateDate = (Date) r.get("maxDate");
			if (maxCreateDate != null) {
				c.setTime(maxCreateDate);
				int maxYear = c.get(Calendar.YEAR);
				if (currentYear > maxYear) {
					r.remove("maxDate");
					r.put("workType", "09");
					BaseDAO dao = StatScheduleUtil.getDAO(session);
					dao.doInsert(PUB_WorkList, r, false);
				}
			} else {
				r.remove("maxDate");
				r.put("workType", "09");
				BaseDAO dao = StatScheduleUtil.getDAO(session);
				dao.doInsert(PUB_WorkList, r, false);
			}

		}
		logger.info("Has added " + set.size() + " tasks.");
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
