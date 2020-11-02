package phis.source.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.controller.watcher.WatchHelper;
import ctd.controller.watcher.WatchHelperEx;
import ctd.controller.watcher.WatcherCommands;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

/**
 * 该类从系统参数GY_XTCS中根据系统参数名称获取系统参数
 * 
 * @author 2048
 * 
 */

public class ParameterUtil {

	public static final String JGID = "JGID";// 机构ID
	public static final String CSMC = "CSMC";// 参数名称
	public static final String CSZ = "CSZ";// 参数值
	public static final String MRZ = "MRZ";// 默认值
	public static final String BZ = "BZ";// 备注 BZ
	public static final String SSLB = "SSLB";// 备注 BZ

	private static Map<String, Object> cacheParameter = new ConcurrentHashMap<String, Object>();

	/**
	 * 获取系统顶级节点ID
	 * 
	 * @return
	 */

	public static String getTopUnitId() {
		String organId = "";
		try {
			organId = UserRoleToken.getCurrent().getOrganId();
		} catch (Exception e) {
			Dictionary manage;
			try {
				manage = DictionaryController.instance()
						.get("phis.@manageUnit");
				organId = manage.getSlice("", 3, null).get(0).getKey();
			} catch (ControllerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (organId != null && organId.indexOf(".") >= 0) {
			return organId.substring(organId.indexOf(".") + 1);
		}
		return organId;
	}

	/**
	 * 初始化机构下所有参数
	 * 
	 * @param _JGID
	 * @param ctx
	 */
	public static void initParameters(String _JGID, Context ctx) {
		for (String _CSMC : BSPHISSystemArgument.defaultValue.keySet()) {
			if (BSPHISSystemArgument.dynamicParamter.containsKey(_CSMC)) {// 动态参数不默认生成
				continue;
			}
			getParameter(_JGID, _CSMC,
					BSPHISSystemArgument.defaultValue.get(_CSMC),
					BSPHISSystemArgument.defaultAlias.get(_CSMC),
					BSPHISSystemArgument.defaultCategory.get(_CSMC), ctx);
		}

		for (String _CSMC : BSPHISSystemArgument.defaultPubValue.keySet()) {
			if (BSPHISSystemArgument.dynamicParamter.containsKey(_CSMC)) {// 动态参数不默认生成
				continue;
			}
			getParameter(getTopUnitId(), _CSMC,
					BSPHISSystemArgument.defaultPubValue.get(_CSMC),
					BSPHISSystemArgument.defaultPubAlias.get(_CSMC),
					BSPHISSystemArgument.defaultPubCategory.get(_CSMC), ctx);
		}
	}

	/**
	 * 描述:获取系统参数
	 * 
	 * 1、 该类从系统参数GY_XTCS中根据系统参数名称获取系统参数 2、如果系统参数表中没有系统该系统参数名称的系统参数,
	 * 则该函数会自动增加一条系统参数, 参数名称为传入的系统参数名称, 参数值为传入的缺省值
	 * 3、该函数相关的函数为setParameter用于写入系统参数
	 * 
	 * @param _JGID
	 * @param _CSMC
	 * @param _MRZ
	 * @param _BZ
	 */
	public static String getParameter(String _JGID, String _CSMC, String _MRZ,
			String _BZ, String _SSLB, Context ctx) {
		BaseDAO dao = new BaseDAO();
		_CSMC = _CSMC.toUpperCase();
		String v = (String) cacheParameter.get(_JGID + _CSMC);
		if (v != null) {
			return v;
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", JGID, "s", _JGID);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", CSMC, "s", _CSMC);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> l = dao.doQuery(cnd, "",
					BSPHISEntryNames.GY_XTCS);
			if (l.size() > 0) {
				v = l.get(0).get(CSZ) == null ? "" : l.get(0).get(CSZ)
						.toString();
				cacheParameter.put(_JGID + _CSMC, v);
				return v;
			} else {
				setParameter(_JGID, _CSMC, _MRZ, _BZ, _SSLB, ctx);
				return _MRZ;
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String getParameter(String _JGID, String _CSMC, String _MRZ,
			String _BZ, Context ctx) {
		String _SSLB = "";
		BaseDAO dao = new BaseDAO();
		_CSMC = _CSMC.toUpperCase();
		String v = (String) cacheParameter.get(_JGID + _CSMC);
		if (v != null && !"".equals(v)) {
			return v;
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", JGID, "s", _JGID);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", CSMC, "s", _CSMC);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> l = dao.doQuery(cnd, "",
					BSPHISEntryNames.GY_XTCS);
			if (l.size() > 0) {
				v = l.get(0).get(CSZ) == null ? "" : l.get(0).get(CSZ)
						.toString();
				cacheParameter.put(_JGID + _CSMC, v);
				return v;
			} else {
				for (String CSMC_ : BSPHISSystemArgument.defaultValue.keySet()) {
					if (_CSMC.equals(CSMC_)) {
						_SSLB = BSPHISSystemArgument.defaultCategory.get(_CSMC);
					}
				}
				if (_SSLB.length() == 0) {
					for (String CSMC_ : BSPHISSystemArgument.defaultPubValue
							.keySet()) {
						if (_CSMC.equals(CSMC_)) {
							_SSLB = BSPHISSystemArgument.defaultPubCategory
									.get(_CSMC);
						}
					}
				}
				setParameter(_JGID, _CSMC, _MRZ, _BZ, _SSLB, ctx);
				return _MRZ;
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 描述:获取系统参数
	 * 
	 * 1、 该类从系统参数GY_XTCS中根据系统参数名称获取系统参数 2、如果系统参数表中没有系统该系统参数名称的系统参数,
	 * 则该函数会自动增加一条系统参数, 参数名称为传入的系统参数名称, 参数值为传入的缺省值
	 * 3、该函数相关的函数为setParameter用于写入系统参数
	 * 
	 * @param _JGID
	 * @param _CSMC
	 * @param _MRZ
	 * @param _BZ
	 */

	public static String getParameter(String _JGID, String _CSMC, Context ctx) {
		BaseDAO dao = new BaseDAO();
		String _MRZ = null;
		String _BZ = "";// 备注
		String _SSLB = "";// 所属类别
		_CSMC = _CSMC.toUpperCase();
		String v = null;
		if(cacheParameter.get(_JGID + _CSMC)!=null){
			v = (String) cacheParameter.get(_JGID + _CSMC);
		}
		if (v != null && !"null".equals(v)) {
			return v;
		}
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", JGID, "s", _JGID);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", CSMC, "s", _CSMC);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> l = dao.doQuery(cnd, "",
					BSPHISEntryNames.GY_XTCS);
			if (l.size() > 0) {
				v = l.get(0).get(CSZ) == null ? "" : l.get(0).get(CSZ)
						.toString();
				// 集群问题,暂时取消缓存
				cacheParameter.put(_JGID + _CSMC, v);
				return v;
			} else {
				_MRZ = BSPHISSystemArgument.defaultValue.get(_CSMC);
				_BZ = BSPHISSystemArgument.defaultAlias.get(_CSMC);
				_SSLB = BSPHISSystemArgument.defaultCategory.get(_CSMC);
				setParameter(_JGID, _CSMC, _MRZ, _BZ, _SSLB, ctx);
				return _MRZ;
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 描述: 设置系统参数
	 * 
	 * 1、 该函数将系统参数写入系统参数GY_XTCS中 2、 和该函数相关的函数为getParameter用于获取系统参数 3、
	 * 该函数在一般情况下无需使用, 因为在使用getParameter获取系统参数时若没有该系统参数，会自动加入。
	 * 
	 * @param _JGID
	 * @param _CSMC
	 * @param _CSZ
	 * @param _BZ
	 */

	private static boolean setParameter(String _JGID, String _CSMC,
			String _CSZ, String _BZ, String _SSLB, Context ctx) {
		BaseDAO dao = new BaseDAO();
		StringBuffer hql = new StringBuffer("INSERT INTO ");
		hql.append("GY_XTCS").append(" (").append(JGID).append(",")
				.append(CSMC).append(",").append(CSZ).append(",").append(BZ)
				.append(",").append(SSLB).append(") values ('");
		hql.append(_JGID).append("','").append(_CSMC).append("','")
				.append(_CSZ).append("','").append(_BZ).append("','")
				.append(_SSLB).append("')");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			dao.doSqlUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 重新载入某个参数
	 * 
	 * @param _JGID
	 * @param _CSMC
	 */
	public static void reloadParams(String _JGID, String _CSMC) {
		cacheParameter.remove(_JGID + _CSMC);
		WatchHelper.send(WatchHelperEx.PARAMTERRELOAD, _JGID + _CSMC,
				WatcherCommands.CMD_RELOAD);

	}

	/**
	 * 重新载入某个参数
	 * 
	 * @param _JGID
	 * @param _CSMC
	 */
	public static void reloadParamsWithNoMate(String paramterId) {
		cacheParameter.remove(paramterId);
	}

	/**
	 * 重新载入所有参数
	 */
	public static void reloadAllParams() {
		cacheParameter.clear();
	}

	/**
	 * 获取URL中IP
	 * 
	 * @param URL
	 *            : http://192.168.1.222:8188/CHIS
	 * @return
	 */
	public static String getURLIP(String URL) {
		String IP = URL.substring(URL.indexOf("//") + 2, URL.lastIndexOf(":"));
		return IP;
	}
}
