
package chis.source.service.base;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.mobilempi.MobileAppService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.organ.Organ;
import ctd.account.organ.OrganController;
import ctd.account.role.Role;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.mvc.controller.support.LogonManager;
import ctd.mvc.controller.support.logon.AspectLogon;
import ctd.mvc.controller.support.logon.CommonAspectLogon;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.JSONUtils;
import ctd.util.MD5StringUtil;
import ctd.util.S;
import ctd.util.ServletUtils;
import ctd.util.codec.RSAUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ChisLogonManager extends LogonManager{
	private static final Logger logger = LoggerFactory.getLogger(ChisLogonManager.class);
	private static final String PASSWORD_NOT_RIGHT = "PasswordNotRight";
	private static final String STATUS_NOT_RIGHT = "StatusNotRight";
	private boolean encryptEnable = true;
	private AspectLogon aspectLogon = new CommonAspectLogon();
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/logon/myRoles",method=RequestMethod.POST)
	public void loadUserRoles(HttpServletRequest request,HttpServletResponse response){
		HashMap<String,Object> resData = new HashMap<String,Object>();
		try {
			HashMap<String,Object> reqData =JSONUtils.parse(request.getInputStream(), HashMap.class);
			String uid = (String)reqData.get("uid");
			String pwd = (String)reqData.get("pwd");
			User user = AccountCenter.getUser(uid);
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
				body.put("tokens", user.filterUserRoleTokens());
				body.put("userPhoto", user.getPhoto());
				resData.put(JSONProtocol.CODE,200);
				resData.put(JSONProtocol.BODY,body);
				HttpSession httpSession = request.getSession(false);
				if(httpSession == null || !uid.equals(httpSession.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY))){
					httpSession = request.getSession();
				}
				httpSession.setAttribute(Context.CLIENT_IP_ADDRESS, ServletUtils.getIpAddress(request));
				httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY, uid);
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
	
	@RequestMapping(value = "/logon/myApps", method = RequestMethod.POST, params = {"urt", "uid", "pwd", "deep" })
	public void loadAppDefines(@RequestParam(value = "urt") Integer urt,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "pwd") String pwd,
			@RequestParam(value = "deep") int deep, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			httpSession = request.getSession();
		}
		try {
			HashMap<String, Object> resData = new HashMap<String, Object>();
			User user = AccountCenter.getUser(uid);
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
				httpSession.setAttribute(Context.CLIENT_IP_ADDRESS,
						ServletUtils.getIpAddress(request));
				httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY,
						uid);
				UserRoleToken token = user.getUserRoleToken(urt);
				if (token == null) {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				} else {
					httpSession.setAttribute(
							UserRoleTokenUtils.SESSION_TOKEN_KEY, urt);
					token.setLastLoginTime(new Date());

					long lastModi = user.getlastModify();

					ContextUtils.put(Context.USER_ROLE_TOKEN, token);
					ContextUtils.put(Context.REQUEST_APPNODE_DEEP, deep);
					ContextUtils.put(Context.USER, token);
					ContextUtils.put(Context.HTTP_REQUEST, request);
					((Context)ContextUtils.get("server")).put("topUnit", getTopUnitId());
					
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
	
	@RequestMapping(value = { "/logon/myMob" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = {
			"urt","uid", "pwd", "deep" })
	public void loadMobileDefines(@RequestParam("urt") Integer urt,
			@RequestParam("uid") String uid,@RequestParam("pwd") String pwd,
			@RequestParam("deep") int deep,HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			httpSession = request.getSession();
		}
		try {
			User user = AccountCenter.getUser(uid);
			UserRoleToken token = user.getUserRoleToken(urt.intValue());
			HashMap<String, Object> resData = new HashMap<String, Object>();
			if (token == null) {
				response.setStatus(404);
			} else {
				if (encryptEnable) {
					pwd = decrypt(pwd);
				}
				pwd = MD5StringUtil.MD5Encode(pwd);
				if (!user.validatePassword(pwd)) {
					resData.put(JSONProtocol.CODE, 501);
					resData.put(JSONProtocol.MSG, PASSWORD_NOT_RIGHT);
				}
				httpSession.setAttribute("token", urt);
				httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY,
						uid);
				token.setLastLoginTime(new Date());
				Map<String, Object> info = MobileAppService.doGetUserInfo(uid);
				response.setContentType("application/json");
				response.setCharacterEncoding("utf-8");
				resData.put("body", info);
				resData.put("code", Integer.valueOf(200));
				boolean gzip = ServletUtils.isAcceptGzip(request);
				jsonOutput(response, resData, gzip);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.setStatus(500);
		} finally {
			ContextUtils.clear();
		}
	}
	
	@Override
	protected void addLogonInfo(Map<String, Object> body) throws ControllerException{ 
		UserRoleToken token = UserRoleToken.getCurrent();
		User user = AccountCenter.getUser(token.getUserId());
		if(user != null){
			body.put("userPhoto", user.getPhoto());
			body.put("pageCount", user.pageCount());
		}
		Organ organ = OrganController.instance().get(token.getOrganId());
		Object wel = organ.getProperty("appwelcome",Boolean.class);
		body.put("appwelcome", wel==null?true:wel);
		Object tabNumber = organ.getProperty("tabNumber");
		body.put("tabNumber", tabNumber);
		body.put("roleType", token.getRole().getType());
		body.put("userDomain", token.getDomain());
		body.put("domain", AppContextHolder.getName());
		Object sysMessage = organ.getProperty("sysMessage",Boolean.class);
		body.put("sysMessage", sysMessage==null?false:sysMessage);
		Object tabRemove = organ.getProperty("tabRemove", Boolean.class);
		body.put("tabRemove", tabRemove==null?true:tabRemove); 
		Role role = token.getRole();
		String banner = (String) role.getProperty("banner");
		if(!S.isEmpty(banner)){
			body.put("banner", banner);
		}
		String myDesktop = (String) organ.getProperty("myDesktop");
		if(!S.isEmpty(myDesktop)){
			body.put("myDesktop", myDesktop);
		}
		aspectLogon.afterLogon(body);
	}
     /**
     * ���ҽ��Ż�ȡҽ����Ϣ
     * @param request
     * @param response
     * @throws ServiceException 
     */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/logon/loginInfo",method=RequestMethod.POST)
	public void logonAppInfo(HttpServletRequest request,HttpServletResponse response) throws ServiceException{
		HashMap<String,Object> resData = new HashMap<String,Object>();
		String uid = "";
			try {
				HashMap<String, Object> reqData  = JSONUtils.parse(request.getInputStream(), HashMap.class);
				uid= (String)reqData.get("uid");
				try {
					User user = AccountCenter.getUser(uid);
					 if(user.isForbidden()){
						resData.put(JSONProtocol.CODE,404);
						resData.put(JSONProtocol.MSG,STATUS_NOT_RIGHT);
					}else{
						HashMap<String,Object> body = new HashMap<String,Object>();
						 UserRoleToken urt = null;
						 List<UserRoleToken> uts = new ArrayList<UserRoleToken>();
						if(user.filterUserRoleTokens()!=null){
							 uts = (List<UserRoleToken>) user.filterUserRoleTokens();
						    for(int i=0;i<uts.size();i++){
						       urt = uts.get(i);
						       String roleId = urt.getRoleId();
						    }
						}

						body.put("tokens", user.filterUserRoleTokens());
						body.put("userPhoto", user.getPhoto());		
						urt.setLastLoginTime(new Date());
//						Map<String, Object> info = MobileAppService.doGetUserInfo(uid);
						response.setContentType("application/json");
						response.setCharacterEncoding("utf-8");
						Map<String,Object> manageUnit = new HashMap<String, Object>();
						Map<String,Object> resDataChild = new HashMap<String, Object>();
						Map<String,Object> properties = new HashMap<String, Object>();
						Map<String,Object> resMap =  new HashMap<String, Object>();
						Map<String,Object> rMap =  new HashMap<String, Object>();
						for(int i = 0;i<uts.size();i++){
							UserRoleToken ur = uts.get(i);
//							if(uts.size()==1){
//								rMap = setUserInfo(manageUnit, resDataChild, properties, ur, resMap);	
//						}else{
							if(ur.getRoleId().equals("chis.01")){
								rMap = setUserInfo(manageUnit, resDataChild, properties, ur, resMap);
							}
							if(ur.getRoleId().equals("chis.100")){
								rMap = setUserInfo(manageUnit, resDataChild, properties, ur, resMap);
							}		
//						}
						}
						if(rMap.isEmpty()){
							resData.put(JSONProtocol.CODE,404);
							resData.put(JSONProtocol.MSG,STATUS_NOT_RIGHT);
							return;
						}
						Map<String, Object> info = MobileAppService.doGetUserInfo2(uid);
						resData.put("roles", rMap);
						resData.put("userInfo", info);
						resData.put("code", Integer.valueOf(200));	
					}
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				boolean gzip = ServletUtils.isAcceptGzip(request);
				jsonOutput(response, resData, gzip);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	/**
	 * 组装用户信息
	 * @param manageUnit
	 * @param properties
	 * @param resDataChild
	 * @param ur
	 * @param resMap
	 * @throws ControllerException
	 */
	public static Map<String,Object> setUserInfo(Map<String,Object> manageUnit,Map<String,Object> properties,Map<String,Object> resDataChild,UserRoleToken ur,Map<String,Object> resMap) throws ControllerException{				
	    resMap = new HashMap<String, Object>();
		Dictionary sd = DictionaryController.instance().get("chis.@manageUnit");
		DictionaryItem items = sd.getItem(ur.getManageUnitId());
		String type = "";
		String pyCode = "";
		if(items!=null){
			type = (String)items.getProperty("type");
		    pyCode = (String)items.getProperty("pyCode");
		}
		manageUnit.put("ref",ur.getManageUnitId());
		manageUnit.put("name", ur.getManageUnitName());
		manageUnit.put("id", ur.getManageUnitId());
		manageUnit.put("type",type);
//		manageUnit.put("id", ur.getManageUnitId());
		manageUnit.put("properties", null);
		manageUnit.put("pyCode",pyCode);
		resMap.put("manageUnitId", ur.getManageUnitId());
		resMap.put("roleId", ur.getRoleId());
		resMap.put("displayName", ur.getDisplayName());
		resMap.put("userName", ur.getUserName());
		resMap.put("userId", ur.getUserId());
		resMap.put("organName", ur.getOrganName());
		resMap.put("regionCode", ur.getRegionCode());
		resMap.put("logoff", ur.getLogoff());
		resMap.put("manageUnitName", ur.getManageUnitName());
		resMap.put("roleName", ur.getRoleName());
		resMap.put("organId", ur.getOrganId());
		resMap.put("id", ur.getId());
		resMap.put("properties", properties);
		resMap.put("manageUnit", manageUnit);	
		return resMap;
	}
	@SuppressWarnings("deprecation")
	private Object getTopUnitId() {
		Dictionary manage = DictionaryController.instance().getDic("chis.@manageUnit");
		String key = manage.getSlice("", 3, null).get(0).getKey();
		return key;
	}
	
	private String decrypt(String input){
		return RSAUtils.decryptStringByJs(input);
	}
	
	public AspectLogon getAspectLogon() {
		return aspectLogon;
	}

	public void setAspectLogon(AspectLogon aspectLogon) {
		this.aspectLogon = aspectLogon;
	}
}
