/**
 * @(#)ConfigControllorService.java 创建于 2011-1-7 下午08:14:35
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import chis.source.service.ServiceCode;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryItem;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ConfigControllorService implements Service {

	private static final Log logger = LogFactory
			.getLog(ConfigControllorService.class);

	private SessionFactory sessionFactory = null;

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.core.service.Service#execute(org.json.HashMap<String,Object>,
	 * org.json.HashMap<String,Object>, ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String,Object> req, Map<String,Object> res, Context ctx){
		Dictionary dic = DictionaryController.instance().getDic("chis.dictionary.sysConfNav");
		Map<String,Object> resBody = (HashMap<String, Object>) res.get("body");
		if (resBody == null) {
			resBody = new HashMap<String,Object>();
			res.put("body", resBody);
		}
		Session session = null;
		try {
			session = sessionFactory.openSession();
			checkAuthorization(dic, resBody, session, ctx);
		} catch (HibernateException e) {
			logger.error("Failed to open session.", e);
			res.put(RES_CODE, ServiceCode.CODE_NOT_FOUND);
			res.put(RES_MESSAGE, "数据库连接失败。");
			return;
		} catch (Exception e) {
			logger.error("Check changable right failed.", e);
			res.put(RES_CODE, ServiceCode.CODE_NOT_FOUND);
			res.put(RES_MESSAGE, "权限校验失败。");
			return;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * @param dic
	 * @param resBody
	 * @param session
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	private void checkAuthorization(Dictionary dic, Map<String,Object> resBody,
			Session session, Context ctx) throws ServiceException {
		for (DictionaryItem di : dic.itemsList()) {
			String cls = (String) di.getProperty("class");
			ConfigControllor controllor;
			try {
				controllor = ConfigControllorFactory.getInstance()
						.getConfigControllor(cls);
			} catch (Exception e) {
				throw new ServiceException("No such config controllor[" + cls
						+ "].", e);
			}
			controllor.setModuleId(di.getKey());
			controllor.setModuleData(new HashMap<String, Boolean>());
			Map<String, Object> map;
			try {
				map = controllor.isReadOnly(session, ctx);
			} catch (Exception e) {
				throw new ServiceException("Check changable right of["
						+ di.getKey() + "] failed.", e);
			}
			for (String key : map.keySet()) {
				resBody.put(key, map.get(key));
			}
		}
	}
}
