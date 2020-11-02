package phis.source.search;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public abstract class AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(AbstractSearchModule.class);

	static final String MANDATORY = "1";
	static final String OPTIONAL = "2";

	protected Session ss = null;
	// 默认搜索方式为PYDM
	public String SEARCH_TYPE = "PYDM";

	// 默认匹配方式
	public String MATCH_TYPE = "%";

	/**
	 * 开启session 初始查询方式
	 */
	public void open(Context ctx) {
		ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
	}

	/**
	 * 公用查询抽象类，前台通过className调用对应子类 实现查询方法
	 * 
	 * @param req
	 * @param resp
	 */
	public abstract void execute(Map<String,Object> req,
			Map<String,Object> resp, Context ctx);

	/**
	 * 关闭session
	 */
	// public void close() {
	// if (ss != null && ss.isOpen()) {
	// ss.close();
	// }
	// }
	/**
	 * 输入的字符是否是汉字
	 * 
	 * @param a
	 *            char
	 * @return boolean
	 */
	public static boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}

	public static boolean containsChinese(String s) {
		if (null == s || "".equals(s.trim()))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i)))
				return true;
		}
		return false;
	}
}
