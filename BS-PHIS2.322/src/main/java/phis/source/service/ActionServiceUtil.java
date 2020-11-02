/*
 * @(#)ActionServiceUtil.java Created on 2011-12-22 下午2:37:14
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

import java.util.List;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class ActionServiceUtil {

	/**
	 * 判断一个服务的一个action是否是需要做事务控制的。
	 * 
	 * @param action
	 * @param service
	 * @return
	 */
	public static boolean needTransacted(String action,
			AbstractActionService service) {
		return actionMatch(action, service.getTransactedActions());
	}

	/**
	 * 判断一个服务的一个action是否是需要数据库操作的。
	 * 
	 * @param action
	 * @param service
	 * @return
	 */
	public static boolean needDBSupport(String action,
			AbstractActionService service) {
		return !actionMatch(action, service.getNoDBActions());
	}

	/**
	 * 判断给定的action是否包含在list中，如果list的字符串有通配符，判断action是否符合条件。
	 * 
	 * @param action
	 * @param list
	 * @return
	 */
	private static boolean actionMatch(String action, List<String> list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		for (String str : list) {
			if (str.equals(action)) {
				return true;
			}
			if (str.endsWith("*")
					&& action.startsWith(str.substring(0, str.length() - 1))) {
				return true;
			}
			if (str.startsWith("*") && action.endsWith(str.substring(1))) {
				return true;
			}
		}
		return false;
	}
}
