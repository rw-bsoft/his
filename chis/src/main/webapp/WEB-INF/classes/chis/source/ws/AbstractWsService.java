/**
 * @(#)AbstractService.java Created on Oct 29, 2009 10:30:18 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.io.StringReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import chis.source.Constants;
import chis.source.util.ApplicationUtil;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public abstract class AbstractWsService implements Service {

	private static final Log logger = LogFactory
			.getLog(AbstractWsService.class);

	// @@ Hibernate session工厂。
	private SessionFactory sessionFactory = null;

	/**
	 * 检查请求数据。
	 * 
	 * @param reqRoot
	 * @return
	 * @throws ServiceException
	 */
	protected boolean verifyRequest(Element reqRoot) throws ServiceException {
		if (isEmpty(reqRoot.elementText("user"))) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"用户名缺失。");
		}
		if (isEmpty(reqRoot.elementText("role"))) {
			throw new ServiceException(Constants.CODE_BUSINESS_DATA_NULL,
					"未指定角色。");
		}
		return true;
	}

	/**
	 * 预处理请求，如果成功返回code=0，失败返回code=1。
	 * 
	 * @param request
	 * @return 数组第一个元素表示成功与否（0：成功，1：失败），第二个元素表示回应的xml，如果成功第三个元素表示请求的xml。
	 */
	protected Object[] preExecute(String request) {
		logger.info(new StringBuffer("Received request msg [").append(request)
				.append("]."));
		Document resDoc = DocumentHelper.createDocument();
		resDoc.setRootElement(DocumentHelper.createElement("response"));
		Element responseEle = resDoc.getRootElement();
		Element codeEle = DocumentHelper.createElement("code");
		Element msgEle = DocumentHelper.createElement("msg");
		responseEle.add(codeEle);
		responseEle.add(msgEle);
		if (request == null) {
			logger.error("NULL request received.");
			codeEle.setText(String.valueOf(Constants.CODE_INVALID_REQUEST));
			msgEle.setText("空的请求信息！");
			Object[] res = new Object[2];
			res[0] = 1;
			res[1] = resDoc;
			return res;
		}
		// responseEle.add(roleEle);
		Document doc;
		SAXReader reader = new SAXReader();
		try {
			doc = reader.read(new StringReader(request));
		} catch (DocumentException e) {
			logger.error("Request parsing failed.", e);
			codeEle.setText(String.valueOf(Constants.CODE_REQUEST_PARSE_ERROR));
			msgEle.setText("请求信息解析失败！");
			Object[] res = new Object[2];
			res[0] = 1;
			res[1] = resDoc;
			return res;
		}

		Element rootElement = doc.getRootElement();
		try {
			verifyRequest(rootElement);
		} catch (ServiceException e) {
			logger.error("Failed to verify request.", e);
			codeEle.setText(String.valueOf(e.getCode()));
			msgEle.setText(e.getMessage());
			Object[] res = new Object[2];
			res[0] = 1;
			res[1] = resDoc;
			return res;
		}
		String user = rootElement.elementText("user");
		String password = rootElement.elementText("password");
		String role = rootElement.elementText("role");
		Integer urt = null;
		try {
			urt = checkUser(user, password, role);
			Element ridEle = DocumentHelper.createElement("roleId");
			ridEle.setText(urt.toString());
			rootElement.add(ridEle);
		} catch (Exception e) {
			String msg = new StringBuffer("Invalid user: ").append(user)
					.append(", password: ").append(password).append(", role: ")
					.append(role).toString();
			logger.error(msg, e);
			codeEle.setText(String
					.valueOf(Constants.CODE_SERVICE_AUTHORIZATION_FAIL));
			msgEle.setText(msg);
			Object[] res = new Object[2];
			res[0] = 1;
			res[1] = resDoc;
			return res;
		}
		logger.info("User validation passed.");
		// roleEle.setText(role);
		Object[] res = new Object[3];
		res[0] = 0;
		res[1] = resDoc;
		res[2] = doc;
		return res;
	}

	/**
	 * 获取url的根地址。
	 * @param host 服务器名
	 * @param port 端口号
	 * @return
	 * @throws UnknownHostException
	 */
	public String getRootAddr(String host,String port) throws UnknownHostException {
		Application acc = null;
		try {
			acc = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new UnknownHostException("获取配置信息失败!");
		}
		String localHost = (String) acc.getProperty("webBindAddr");
		if(StringUtils.isNotBlank(host)){
			localHost = host;
		}
		if (localHost == null || localHost.trim().length() == 0) {
			localHost = Inet4Address.getLocalHost().getHostAddress();
		}
		StringBuffer sb = new StringBuffer("http://").append(localHost);
		if(StringUtils.isNotBlank(port)){
			if(false == "80".equals(port)){
				sb.append(":").append(port.trim());
			}
		}else if (false == "80".equals(acc.getProperty("webBindPort"))) {
			sb.append(":").append(acc.getProperty("webBindPort"));
		}
		return sb.append("/").append(acc.getProperty("appName"))
				.append("/interface.jshtml?").toString();
	}

	/**
	 * 检验用户合法性。
	 * 
	 * @param user
	 * @param password
	 * @return 角色
	 * @throws HibernateException
	 */
	protected Integer checkUser(String uid, String password, String rname)
			throws Exception {
		User user = AccountCenter.getUser(uid);
		if (user == null) {
			throw new Exception("No such user[" + uid + "]");
		}
		if (!user.validatePassword(password)) {
			logger.error("Invalid logon, userName/password incorrect.");
			throw new Exception("Invalid logon, userName/password incorrect.");
		}

		Integer urt = null;
		for (UserRoleToken token : user.getUserRoleTokens()) {
			if (token.getRoleId().equals(rname)) {
				urt = token.getId();
			}
		}
		if (urt == null) {
			throw new Exception("No such user[" + uid + "] with profile[" + rname
					+ "].");
		}
		return urt;
	}

	/**
	 * 判断一个字符串为null或者空。
	 * 
	 * @param str
	 * @return
	 */
	protected boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
