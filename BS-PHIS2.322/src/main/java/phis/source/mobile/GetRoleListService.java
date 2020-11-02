/**
 * @(#)GetRoleListService.java Created on 2012-7-7 下午01:33:59
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.mobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.service.core.Service;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class GetRoleListService extends AbstractActionService implements
		DAOSupportable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.core.service.Service#execute(org.json.JSONObject,
	 * org.json.JSONObject, ctd.util.context.Context)
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String uid = (String) req.get("uid");
		String psw = (String) req.get("psw");
		if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(psw)) {
			res.put(Service.RES_CODE, 405);
			res.put(Service.RES_MESSAGE, "password emtry");
			return;
		}
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
			return;
		}
		String pwd = MD5StringUtil.MD5Encode(psw);
		if (!user.validatePassword(pwd)) {
			res.put(Service.RES_CODE, 408);
			res.put(Service.RES_MESSAGE, "password error");
			return;
		} else if (user.isForbidden()) {
			res.put(Service.RES_CODE, 407);
			res.put(Service.RES_MESSAGE, "用户已禁用");
			return;
		}
		Collection<UserRoleToken> list = user.getUserRoleTokens();
		List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
		for (UserRoleToken token : list) {
			Map<String, Object> role = new HashMap<String, Object>();
			String roleId = token.getRoleId();
			String roleName = token.getRoleName();
			String unitId = token.getManageUnitId();
			String unitName = token.getManageUnitName();
			role.put("key", unitId + "@" + roleId);
			role.put("text", roleName + "[" + unitName + "]");
			role.put("id", token.getId());
			roles.add(role);
		}

		res.put(Service.RES_CODE, 200);
		res.put(Service.RES_MESSAGE, "sucess");
		res.put("body", roles);

	}
}
