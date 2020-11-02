/**
 * @(#)WebServiceLogonController.java Created on 2013-8-15 下午2:49:03
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.util.ApplicationUtil;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.mvc.controller.JSONOutputMVCConroller;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.util.JSONUtils;
import ctd.util.ServletUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
@Controller("webServiceLogonController")
public class WebServiceLogonController extends JSONOutputMVCConroller {

	private static final Logger logger = LoggerFactory
			.getLogger(WebServiceLogonController.class);

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/webServiceLogonController/logon",method=RequestMethod.POST)
	public void doLogon(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		try {
			HashMap<String, Object> reqData = JSONUtils.parse(
					request.getInputStream(), HashMap.class);
			String uid = (String) reqData.get("uid");
			String pass = (String) reqData.get("psw");
			String urt = reqData.get("urole").toString();
			int urtint=0;
			if(urt.indexOf("chis")==-1 && urt.length()==2 ){
				urt="chis."+urt;
			}else if(urt.equals("chis.system")){
				urt="chis.system";
			}else{
				urt="chis.01";
			}
			
			if (uid == null || uid.equals("")) {
				responseOutPut(request, response, res);
				res.put("code", 500);
				res.put("msg", "无效的请求");
				logger.error("Invalid request");
				return;
			}
			User user = AccountCenter.getUser(uid);
			if (user == null) {
				responseOutPut(request, response, res);
				res.put("code", 500);
				res.put("msg", "用户不存在");
				logger.error("No such user[" + uid + "]");
				return;
			}
//			if (!user.validatePassword(pass)) {
//				responseOutPut(request, response, res);
//				res.put("code", 500);
//				res.put("msg", "密码错误");
//				logger.error("Invalid logon, userName/password incorrect.");
//				return;
//			}
			UserRoleToken token = null;
			Collection<UserRoleToken> userRoleTokens = user.getUserRoleTokens();
			for (UserRoleToken userRoleToken : userRoleTokens) {
				String roleId = userRoleToken.getRoleId();
				if (urt.indexOf(roleId)!=-1) {
					urtint=userRoleToken.getId();
					token = userRoleToken;
					ContextUtils.put(Context.USER_ROLE_TOKEN, userRoleToken);
					ContextUtils.put(Context.USER, userRoleToken);
				}
			}
			if (token == null) {
				responseOutPut(request, response, res);
				res.put("code", 500);
				res.put("msg", "没有找到对应的角色");
				logger.error("No such user[" + uid + "] with profile[" + urt
						+ "].");
				return;
			}
			System.out.println(token.toString());
			res.put("code", 200);
			HttpSession httpSession = request.getSession(false);
			if(httpSession == null || !uid.equals(httpSession.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY))){
				httpSession = request.getSession();
			}
			httpSession.setAttribute(UserRoleTokenUtils.SESSION_UID_KEY, uid);
			httpSession.setAttribute(UserRoleTokenUtils.SESSION_TOKEN_KEY,urtint);
			
			res.put("userRoleToken", token);
			HashMap<String, Object> body = new HashMap<String, Object>();
			body.put("serverDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			Application app = null;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e) {
				logger.error(e.getMessage(), e);
			}
			body.put("childrenRegisterAge", Integer.parseInt((String) app
					.getProperty("childrenRegisterAge")));
			body.put("childrenDieAge", Integer.parseInt((String) app
					.getProperty("childrenDieAge")));
			body.put("oldPeopleAge",
					Integer.parseInt((String) app.getProperty("oldPeopleAge")));
			body.put("oldPeopleMode",Integer.parseInt((String) app.getProperty(BusinessType.LNR
							+ "_planMode")));
			body.put("hypertensionMode",Integer.parseInt((String) app.getProperty(BusinessType.GXY
							+ "_planMode")));
			body.put("diabetesMode",Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_planMode")));
			body.put("diabetesPrecedeDays",Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_precedeDays")));
			body.put("diabetesDelayDays",
					Integer.parseInt((String) app.getProperty(BusinessType.TNB
							+ "_delayDays")));
			body.put("pregnantMode", Integer.parseInt((String) app
					.getProperty(BusinessType.MATERNAL + "_planMode")));
			res.put("res", body);
			responseOutPut(request, response, res);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("code", 500);
			res.put("msg", "登陆失败");
			logger.error("Logon failed.");
		}

	}
	
	private void responseOutPut(HttpServletRequest request, HttpServletResponse response,HashMap<String,Object> res) throws IOException{
		boolean gzip = ServletUtils.isAcceptGzip(request);
		jsonOutput(response,res,gzip);
	}
}
