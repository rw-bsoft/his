package phis.source.utils;

import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * 序列工具类
 * 
 * @author yangl
 * 
 */
public class SequenceUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(SimpleDAO.class);

	/**
	 * 目前只实现oracle序列部分，若有其他数据库，可后续补全
	 * 
	 * @param entryName
	 *            获取entryName对应表当前最大值，用于自动创建序列
	 * @param rec
	 *            主键生成策略，主要获取seqName参数(seqName：序列名称)
	 * @param pkey
	 *            entryName需要序列的字段
	 * @param obj
	 *            暂时不启用，后期扩展
	 * @return
	 * @throws DataAccessException
	 */
	public static String getKey(String entryName, HashMap<String, String> rec,
			String pkey, Object obj) throws DataAccessException {
		Context ctx = ContextUtils.getContext();
		Session ss = null;
		Object key = null;
		if (ctx.has(Context.DB_SESSION)) {
			ss = (Session) ctx.get(Context.DB_SESSION);
		} else {
			throw new DataAccessException("MissingDatebaseConnection");
		}
		// 判断是否有存在序列名称
		// 判断entryName是否为全路径形式，如果是，截取最后
		if(entryName.contains(".")) {
			entryName = entryName.substring(entryName.lastIndexOf(".")+1);
		}
		String seqName = "seq_" + entryName; // 默认序列名称
		if (rec.containsKey("seqName")) {
			seqName = rec.get("seqName");
		}
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String dialectName = ((SessionFactoryImpl) sf).getDialect().getClass()
				.getName();
		// oracle 序列实现
		if (dialectName.contains("MyOracle")) {
			StringBuilder sql = new StringBuilder("select ");
			sql.append(seqName);
			sql.append(".nextval from dual");
			try {
				key = ss.createSQLQuery(sql.toString()).uniqueResult();
			} catch (HibernateException e) {
				logger.error("can't find sequence [" + seqName
						+ "],attempt to create it and try again");
				// 尝试创建序列并重试 oracle
				Long startValue = 1l;
				if (rec.containsKey("startPos")) {
					startValue = Long.parseLong(rec.get("startPos").toString());
				}
				Object maxValue = ss.createQuery(
						"select max(" + pkey + ") from " + entryName)
						.uniqueResult();
				if (maxValue != null
						&& Long.parseLong(maxValue.toString()) > startValue) {
					startValue = Long.parseLong(maxValue.toString()) + 1;
				}
				StringBuilder seqSql = new StringBuilder("create sequence ");
				seqSql.append(seqName);
				seqSql.append(" minvalue ");
				seqSql.append(startValue);
				seqSql.append(" maxvalue 9999999999 start with ");
				seqSql.append(startValue);
				seqSql.append(" increment by 1 cache 20");
				ss.createSQLQuery(seqSql.toString()).executeUpdate();
				ss.flush();
				key = ss.createSQLQuery(sql.toString()).uniqueResult();
			}
		}
		return key == null ? null : key.toString();
	}

}
