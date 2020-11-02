package phis.source.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.util.AppContextHolder;
import ctd.validator.ValidateException;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 业务锁管理V1.0(实验版)
 * 
 * @author Yangl Business Concurrent Lock(BCL)
 */
public class BCLUtil {

	/**
	 * Map中可包含中的值 GY_SDJL
	 * 
	 * @param m
	 * @param p
	 *            需要判定条件及值
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> lock(Map<String, Object> m, BaseDAO dao)
			throws ModelDataOperationException {
		if (m == null)
			throw new ModelDataOperationException("无效的锁定信息");
		UserRoleToken user = UserRoleToken.getCurrent();
		// 查询互斥业务
		String sql_hc = "select HCYW as HCYW from GY_YWLB where YWXH=:YWXH";
		StringBuffer sql = new StringBuffer(
				"select a.JLXH as JLXH,a.YWXH as YWXH,a.CZGH as CZGH,"
						+ BSPHISUtil.toChar("a.SDSJ", "yyyy-mm-dd hh24:mi:ss")
						+ " as SDSJ,a.BRID as BRID,a.CZXM as CZXM,b.YWSDSC from GY_SDJL a,GY_YWLB b "
						+ " where a.YWXH=b.YWXH and a.YWXH in (:HCYW) and a.JGID=:JGID");
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("YWXH", m.get("YWXH"));
			List<Map<String, Object>> list_hc = dao.doSqlQuery(sql_hc, params);
			List<String> hcywList = new ArrayList<String>();
			if (list_hc != null && list_hc.size() >= 0) {
				String hcyw = (String) list_hc.get(0).get("HCYW");
				if (hcyw != null) {
					hcywList = Arrays.asList(hcyw.split(","));
				}
			}
			params.clear();
			Set<String> keys = m.keySet();
			for (String key : keys) {
				if (key.equals("YWXH"))
					continue;
				params.put(key, m.get(key));
				sql.append(" and " + key + "=:" + key);
			}
			params.put("HCYW", hcywList);
			params.put("JGID", user.getManageUnitId());
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					params);
			boolean ownerLock = false;
			if (list != null && list.size() > 0) {
				StringBuffer errorMsg = new StringBuffer();
				for (Map<String, Object> o : list) {
					// 判断锁定业务是否为当前操作用户本身
					String ywxh = (String) o.get("YWXH");
					String czgh = (String) o.get("CZGH");
					if (ywxh.equals(m.get("YWXH"))
							&& czgh.equals(user.getUserId())) {
						ownerLock = true;
						continue;
					}
					// 判断锁定时间
					String sdsj = (String) o.get("SDSJ");
					BigDecimal ywsdsc = (BigDecimal) o.get("YWSDSC");
					if (ywsdsc != null && ywsdsc.intValue() > 0) {
						SimpleDateFormat matter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						long nowTime = new Date().getTime();
						long sdTime = matter.parse(sdsj).getTime();
						if (nowTime - sdTime > ywsdsc.intValue() * 60 * 1000) {
							params.clear();
							params.put("JLXH", o.get("JLXH"));
							dao.doSqlUpdate(
									"delete from GY_SDJL where JLXH=:JLXH",
									params);
							continue;
						}
					}
					errorMsg.append("【"
							+ DictionaryController.instance()
									.get("phis.dictionary.busiLockType")
									.getText(o.get("YWXH").toString()) + "】");
					if (o.get("BRID") == null
							|| o.get("BRID").toString().trim().length() == 0) {
						errorMsg.append("正在操作业务<br>【操作医生】: " + o.get("CZXM"));
					} else {
						errorMsg.append("业务已锁定该病人!<br>【操作医生】: " + o.get("CZXM"));
					}
					errorMsg.append("<br>【锁定时间】: " + sdsj);
					errorMsg.append("<br>");
				}
				if (errorMsg.length() > 0) {
					throw new ModelDataOperationException(errorMsg.substring(0,
							errorMsg.length() - 4));
				} else {
					if (ownerLock)// 如果是自己占有的锁,则继续使用
						return null;
				}
			}

			m.put("SDSJ", new Date());
			m.put("CZGH", user.getUserId());
			m.put("CZXM", user.getUserName());
			m.put("JGID", user.getManageUnitId());
			Map<String, Object> r = dao.doSave("create",
					BSPHISEntryNames.GY_SDJL, m, false);
			return r;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取锁定信息失败", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取锁定信息失败", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("获取锁定信息失败", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("获取锁定信息失败", e);
		}

	}

	public static void unlock(Map<String, Object> m, BaseDAO dao)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer sql = new StringBuffer(
				"delete from GY_SDJL where  CZGH=:CZGH");
		Set<String> keys = m.keySet();
		for (String key : keys) {
			sql.append(" and " + key + "=:" + key);
		}
		m.put("CZGH", user.getUserId());
		try {
			dao.doSqlUpdate(sql.toString(), m);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("释放锁定信息失败", e);
		}
	}

	public static int checkLock(Map<String, Object> m, BaseDAO dao)
			throws ModelDataOperationException {
		// String ywxh = m.get("YWXH").toString();
		Object jlxh = m.get("bclLockKey").toString();
		String sql = "select 'X' from GY_SDJL where JLXH=:JLXH";// 判断后台记录是否存在
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JLXH", Long.parseLong(jlxh.toString()));
		try {
			List<Map<String, Object>> l = dao.doSqlQuery(sql, params);
			return l.size();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询锁定信息失败", e);
		}

	}

	public static void unlockCurrentUser(String uid)
			throws ModelDataOperationException {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String sql = "delete from GY_SDJL where CZGH=:CZGH";
		Session ss = null;
		try {
			ss = sf.openSession();
			Query q = ss.createSQLQuery(sql);
			q.setString("CZGH", uid);
			q.executeUpdate();
		} catch (Exception e) {
			throw new ModelDataOperationException("delete [" + uid
					+ "] BCL locks falied:" + e.getMessage(), e);
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

	public static void unlockAll() throws ModelDataOperationException {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String sql = "delete from GY_SDJL";
		Session ss = null;
		try {
			ss = sf.openSession();
			Query q = ss.createSQLQuery(sql);
			q.executeUpdate();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					"delete All BCL locks falied:" + e.getMessage(), e);
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

}
