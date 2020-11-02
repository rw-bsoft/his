package phis.source.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.mvc.controller.util.UserRoleTokenUtils;

/**
 * 1、在线人数统计 2、限制单用户登录 3、在线用户管理
 * 
 * @author Yangl
 * 
 */
public class OnlineUserController {
	private static final Logger logger = LoggerFactory
			.getLogger(OnlineUserController.class);
	/**
	 * 内存版本
	 */
	private static Map<String, Object> onlineUsers = new ConcurrentHashMap<String, Object>();

	public static int addUser(String uid, HttpSession session) {
		onlineUsers.put(uid, session);
		return onlineUsers.size();
	}

	public static int removeUser(String uid) {
		onlineUsers.remove(uid);
		return onlineUsers.size();
	}

	public static int getUsersCount() {
		return onlineUsers.size();
	}

	public static boolean hasUser(String uid) {
		// 增加过期判断
		if (onlineUsers.containsKey(uid)) {
			HttpSession ss = getUserSession(uid);
			if (ss != null) {
				try {
					Object oldUid = ss
							.getAttribute(UserRoleTokenUtils.SESSION_UID_KEY);
					if (oldUid == null || !oldUid.toString().equals(uid)) {
						onlineUsers.remove(uid);
						return false;
					}
				} catch (RuntimeException e) {
					logger.error("session已失效!");
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static HttpSession getUserSession(String uid) {
		return (HttpSession) onlineUsers.get(uid);
	}

	public static Map<String, Object> getUsers() {
		return onlineUsers;
	}

}
