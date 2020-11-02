/**
 * @(#)PlatformLogonManager.java Created on 2014-1-20 下午4:03:57
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package platform.source.service.base;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.organ.Organ;
import ctd.account.organ.OrganController;
import ctd.account.role.Role;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.mvc.controller.support.LogonManager;
import ctd.mvc.controller.support.logon.AspectLogon;
import ctd.mvc.controller.support.logon.CommonAspectLogon;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.MD5StringUtil;
import ctd.util.S;
import ctd.util.ServletUtils;
import ctd.util.codec.RSAUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PlatformLogonManager extends LogonManager {
	private static final Logger logger = LoggerFactory
			.getLogger(PlatformLogonManager.class);
	private static final String PASSWORD_NOT_RIGHT = "PasswordNotRight";
	private static final String STATUS_NOT_RIGHT = "StatusNotRight";
	private boolean encryptEnable = false;
	private AspectLogon aspectLogon = new CommonAspectLogon();

	@RequestMapping(value = "/logon/myApps", method = RequestMethod.POST, params = {
			"urt", "uid", "pwd", "deep" })
	public void loadAppDefines(@RequestParam(value = "urt") Integer urt,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "pwd") String pwd,
			@RequestParam(value = "deep") int deep, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
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

	private String decrypt(String input) {
		return RSAUtils.decryptStringByJs(input);
	}

	public AspectLogon getAspectLogon() {
		return aspectLogon;
	}

	public void setAspectLogon(AspectLogon aspectLogon) {
		this.aspectLogon = aspectLogon;
	}
}
