/**
 * @(#)LogonService.java 创建于 2011-6-21 下午07:04:53
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package phis.source.mobile;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.service.core.Service;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description 登录后应在系统数据库中添加登录记录（未完成）。
 * 
 */
public class LogonService extends AbstractActionService implements
		DAOSupportable {
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
		String uid = (String) ((Map<String, Object>) req.get("body"))
				.get("uid");
		String psw = (String) ((Map<String, Object>) req.get("body"))
				.get("psw");
		String rid = (String) req.get("rid");
		User user = null;
		try {
			user = AccountCenter.getUser(uid);
		} catch (ControllerException e) {
			e.printStackTrace();
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
		// try {
		body.put("uid", user.getId());
		body.put("userName", user.getName());
		body.put("rid", token.getRoleId());
		body.put("manaUnitId_text", token.getManageUnitName());
		body.put("manaUnitId", token.getManageUnitId());
		// String regionCode = token.getRegionCode();
		// body.put("regionCode", regionCode);
		// Dictionary d = DictionaryController.instance().get(
		// "phis.dictionary.areaGrid");
		// body.put("regionCode_text", d.getText(regionCode));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		body.put("serverDate", sdf.format(new Date()));
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		body.put("serverDateTime", sdf1.format(new Date()));
		res.put("mapSign", user.getProperty("mapSign"));
		res.put("topUnitId", token.getOrganId());
		res.put("topUnitName", token.getOrganName());
		res.put("jobtitle", token.getRoleName());
		// } catch (ControllerException e) {
		// logger.error(e.getMessage(), e);
		// }
		String jgid = token.getManageUnitId();
		String CARDORMZHM = ParameterUtil.getParameter(jgid, "CARDORMZHM", ctx);
		body.put("CARDORMZHM", CARDORMZHM);
		res.put("topManage", ctx.get("server.manage.topUnit"));
		body.remove("password");
		res.put("body", body);
		body.put(Service.RES_CODE, 200);
		res.put(Service.RES_MESSAGE, "LogonSucess");
	}
}
