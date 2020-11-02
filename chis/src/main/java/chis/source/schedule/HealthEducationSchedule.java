package chis.source.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.quartz.JobExecutionException;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;

public class HealthEducationSchedule implements BSCHISEntryNames {
	private SessionFactory sessionFactory = null;

	public void execute() throws JobExecutionException {
		Session session = null;
		Transaction trx = null;
		try {
			session = sessionFactory.openSession();
			trx = session.beginTransaction();
			deleteTaskPlan(session);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			e.printStackTrace();
		} finally {
			if (session != null || session.isOpen())
				session.close();
		}
	}

	/**
	 * 删除过期的健康教育任务提醒
	 * 
	 * @param session
	 * @throws PersistentDataOperationException
	 */
	public void deleteTaskPlan(Session session)
			throws PersistentDataOperationException {
		try {

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			String date = new SimpleDateFormat("yyyy-MM-dd").format(cal
					.getTime());

			StringBuilder sql = new StringBuilder(
					"select b.exeId as recordId from ")
					.append(HER_EducationPlanSet).append(" a, ")
					.append(HER_EducationPlanExe)
					.append(" b where a.setId = b.setId and ")
					.append("a.endDate <= date('").append(date).append("')");

			Query query = session.createQuery(sql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, Object>> list = query.list();

			StringBuilder delSql = new StringBuilder("delete from ").append(
					PUB_WorkList).append(" where recordId = :recordId");
			query = session.createSQLQuery(delSql.toString());
			for (Map<String, Object> map : list) {
				query.setString("recordId", (String) map.get("recordId"));
				query.executeUpdate();
			}

		} catch (HibernateException e) {
			throw new PersistentDataOperationException(e);
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
