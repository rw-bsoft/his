package ctd.mvc.controller.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.security.exception.SecurityException;
import ctd.util.S;
import ctd.util.exception.CodedBaseException;

public class UserRoleTokenUtils {
	public static final String SESSION_UID_KEY = "uid";
	public static final String SESSION_TOKEN_KEY = "token";

	@SuppressWarnings("unchecked")
	public static UserRoleToken getUserRoleToken(HttpServletRequest request) throws CodedBaseException{
		String uid;
		Integer urt =null;
		HttpSession httpSession = request.getSession(false);
		uid = request.getParameter(SESSION_UID_KEY);
		String temp = request.getParameter(SESSION_TOKEN_KEY);
		if(S.isEmpty(uid) || S.isEmpty(temp)){
			if(httpSession != null){
				uid = (String)httpSession.getAttribute(SESSION_UID_KEY);
				urt = (Integer)httpSession.getAttribute(SESSION_TOKEN_KEY);
			}
		}else{
			urt = Integer.parseInt(temp);
		}
		if(uid == null || urt == null){
			throw new SecurityException(SecurityException.NOT_LOGON, "NotLogon");
		}

		User user = AccountCenter.getUser(uid);
		UserRoleToken token = user.getUserRoleToken(urt);
		if(token == null){
			throw new ControllerException(ControllerException.INSTANCE_NOT_FOUND,"userRoleToken[" + urt + "] for user[" + uid + "] not found");
		}
		if(httpSession != null){
			HashMap<String, Object> prop = (HashMap<String, Object>)httpSession.getAttribute("properties");
			if(prop != null && prop.size() > 0){
				for(String k : prop.keySet()){
					token.setProperty(k, prop.get(k));
				}
			}
		}
		return token;
	}

}
