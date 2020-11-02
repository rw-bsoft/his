package phis.source.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public abstract class AbstractOutputStream {
	protected Logger logger = LoggerFactory
			.getLogger(AbstractOutputStream.class);

	protected Session ss = null;

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
	public abstract void execute(HttpServletRequest req,
			HttpServletResponse resp, Context ctx);

	/**
	 * 关闭session
	 */
	public void close() {
		if (ss != null && ss.isOpen()) {
			ss.close();
		}
		ContextUtils.clear();
	}
}
