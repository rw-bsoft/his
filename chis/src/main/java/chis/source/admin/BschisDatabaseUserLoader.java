//package chis.source.admin;
//
//import java.util.List;
//import java.util.Map;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.context.ApplicationContext;
//
//import ctd.dao.exception.InsertDataAccessException;
//import ctd.util.AppContextHolder;
//import ctd.util.context.Context;
//
//public class BschisDatabaseUserLoader extends DatabaseUserLoader {
//	public static void removeUser(Map<String, Object> rec, Context ctx)
//			throws InsertDataAccessException {
//		ApplicationContext wac = AppContextHolder.get();
//		SessionFactory sf = (SessionFactory)wac.getBean("mySessionFactory");
//		Session ss = null;
//		String count_hql = "select count(*) from " + RoleEntry + " where "
//				+ fieldUserId + "=?";
//		String id = (String) rec.get(fieldUserId);
//		try {
//			ss = sf.openSession();
//			ss.beginTransaction();
//			List ls = ss.createQuery(count_hql).setParameter(0, id).list();
//			long n = ((Long) ls.iterator().next()).longValue();
//			if (n == 1) { // 只有一条 用户信息
//				String userHql = "delete from " + UserEntry + " where "
//						+ fieldUserId + "=?";
//				ss.createQuery(userHql).setParameter(0, id).executeUpdate();
//			}
//			String rloeHql = "delete from " + RoleEntry + " where "
//					+ fieldUserId + "=? ";
//			ss.createQuery(rloeHql).setParameter(0, id).executeUpdate();
//			ss.getTransaction().commit();
//		} catch (Exception e) {
//			ss.getTransaction().rollback();
//			throw new InsertDataAccessException(e.getMessage(), e);
//		} finally {
//			if (ss != null && ss.isOpen()) {
//				ss.close();
//			}
//		}
//
//	}
//}
