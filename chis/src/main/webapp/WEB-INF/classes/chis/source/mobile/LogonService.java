/**
 * @(#)LogonService.java 创建于 2011-6-21 下午07:04:53
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.Constants;
import chis.source.dic.BusinessType;
import chis.source.dic.RolesList;
import chis.source.util.ApplicationUtil;
import chis.source.util.CNDHelper;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.net.rpc.Client;
import ctd.service.core.Service;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description 登录后应在系统数据库中添加登录记录（未完成）。
 * 
 */
public class LogonService implements Service {
	private static final Logger logger = LoggerFactory
			.getLogger(LogonService.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.core.service.Service#execute(org.json.JSONObject,
	 * org.json.JSONObject, ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		Map<String, Object> body = new HashMap<String, Object>();
		String uid = (String) req.get("uid");
		String psw = (String) ((Map<String, Object>) req.get("body"))
				.get("psw");
		String rid = (String) req.get("rid");
		User user = null;
		try {
			user = AccountCenter.getUser(uid);
		} catch (ControllerException e) {
			res.put(Service.RES_CODE, 406);
			res.put(Service.RES_MESSAGE, "user not found");
		}
		if (user == null) {
			res.put(Service.RES_CODE, 406);
			res.put(Service.RES_MESSAGE, "user not found");
			body.put(Service.RES_CODE, 406);
			body.put(Service.RES_MESSAGE, "user not found");
			return;
		}
		String pwd = MD5StringUtil.MD5Encode(psw);
		if (!user.validatePassword(pwd)) {
			res.put(Service.RES_CODE, 408);
			res.put(Service.RES_MESSAGE, "password error");
			body.put(Service.RES_CODE, 408);
			body.put(Service.RES_MESSAGE, "password error");
			return;
		} else if (user.isForbidden()) {
			res.put(Service.RES_CODE, 407);
			res.put(Service.RES_MESSAGE, "用户已禁用");
			body.put(Service.RES_CODE, 407);
			body.put(Service.RES_MESSAGE, "用户已禁用");
			return;
		}
		UserRoleToken token = null;
		Collection<UserRoleToken> userRoleTokens = user.getUserRoleTokens();
		for (UserRoleToken userRoleToken : userRoleTokens) {
			String roleId = userRoleToken.getRoleId();
			if (rid.equals(roleId)) {
				token = userRoleToken;
				ContextUtils.put(Context.USER_ROLE_TOKEN, userRoleToken);
				ContextUtils.put(Context.USER, userRoleToken);
			}
		}
		if (token == null) {
			res.put(Service.RES_CODE, 409);
			res.put(Service.RES_MESSAGE, "role not found");
			body.put(Service.RES_CODE, 409);
			body.put(Service.RES_MESSAGE, "role not found");
			return;
		}

		Application app = null;
		try {
			body.put("uid", user.getId());
			body.put("userName", user.getName());
			body.put("rid", token.getRoleId());
			body.put("manaUnitId_text", token.getManageUnitName());
			body.put("manaUnitId", token.getManageUnitId());
			String regionCode = token.getRegionCode();
			body.put("regionCode", regionCode);
			Dictionary d = DictionaryController.instance().get(
					"chis.dictionary.areaGrid");
			body.put("regionCode_text", d.getText(regionCode));
			body.put("allUsers", doLoadUserList(token));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			body.put("serverDate", sdf.format(new Date()));
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			body.put("serverDateTime", sdf1.format(new Date()));
			res.put("mapSign", user.getProperty("mapSign"));
			res.put("topUnitId", token.getOrganId());
			res.put("topUnitName", token.getOrganName());
			res.put("jobtitle", token.getRoleName());
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			logger.error(e.getMessage(), e);
		}
		res.put("childrenRegisterAge", Integer.parseInt((String) app
				.getProperty("childrenRegisterAge")));
		res.put("childrenDieAge",
				Integer.parseInt((String) app.getProperty("childrenDieAge")));
		res.put("oldPeopleAge",
				Integer.parseInt((String) app.getProperty("oldPeopleAge")));
		res.put("oldPeopleMode",
				Integer.parseInt((String) app.getProperty(BusinessType.LNR
						+ "_planMode")));
		res.put("hypertensionMode",
				Integer.parseInt((String) app.getProperty(BusinessType.GXY
						+ "_planMode")));
		res.put("diabetesMode",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_planMode")));
		res.put("diabetesPrecedeDays",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_precedeDays")));
		res.put("diabetesDelayDays",
				Integer.parseInt((String) app.getProperty(BusinessType.TNB
						+ "_delayDays")));
		res.put("pregnantMode",
				Integer.parseInt((String) app.getProperty(BusinessType.MATERNAL
						+ "_planMode")));
		res.put("validityDays",
				Integer.parseInt((String) app.getProperty("validityDays"))+"");
		res.put("topManage", ctx.get("server.manage.topUnit"));
		body.remove("password");
		res.put("body", body);
		body.put(Service.RES_CODE, 200);
		res.put(Service.RES_MESSAGE, "LogonSucess");
	}

	/**
	 * 获取当前登录者所属社区下的所有责任医生或者团队长列表
	 * 
	 * @param ctx
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> doLoadUserList(UserRoleToken user) {
		try {
			String manageUnit = user.getManageUnitId().substring(0, 9);
			List<Object> cnd1 = CNDHelper.createSimpleCnd("like",
					"manageUnitId", "s", manageUnit + "%");
			List<Object> cnd2 = CNDHelper.createInCnds("roleId", new String[] {
					RolesList.ZRYS, RolesList.TDZ });
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> allUsers = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> userList = (List<Map<String, Object>>) Client
					.rpcInvoke("platform.userLoader", "find", cnd);
			for (Map<String, Object> userMap : userList) {
				String userName = (String) userMap.get("name");
				List<Map<String, Object>> roles = (List<Map<String, Object>>) userMap
						.get("roles");
				for (Map<String, Object> role : roles) {
					Map<String, Object> oneUser = new HashMap<String, Object>();
					Integer id = (Integer) role.get("id");
					String userId = (String) role.get("userId");
					String manageUnitId = (String) role.get("manageUnitId");
					User user2 = AccountCenter.getUser(userId);
					UserRoleToken token = user2.getUserRoleToken(id);
					String manageUnitName = token.getManageUnitName();
					oneUser.put("userId", userId);
					oneUser.put("userName", userName);
					oneUser.put("manaUnitId_text", manageUnitName);
					oneUser.put("manaUnitId", manageUnitId);
					allUsers.add(oneUser);
				}
			}
			return allUsers;
		} catch (Exception e) {
			logger.error("loading user list is failure");
			e.printStackTrace();
		}
		return null;
	}
}
