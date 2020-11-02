package phis.source.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.service.configure.dictionary.ManageUnitDictionary;
import ctd.service.remote.UserLoaderImpl;
import ctd.service.remote.base.UserLoader;

/**
 * 员工代理类，获取平台的用户信息，解析后提供给phis项目使用
 * 
 * @author Yangl
 * 
 */
public class U {

	/**
	 * 获取科室信息
	 * 
	 * @param filter
	 *            过滤条件
	 * @return
	 */
	public static List<HashMap<String, Object>> loadDepartment(String filter) {
		return ManageUnitDictionary.getItems("phis.@manageUnit", filter, null,
				new Integer(1));
	}

	/**
	 * 获取员工信息
	 * 
	 * @param req
	 * @return
	 */
	public static List<Map<String, Object>> loadStaff(
			HashMap<String, Object> req) {
		UserLoader ul = new UserLoaderImpl();
		return ul.find(req);
	}
}
