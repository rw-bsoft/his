package phis.source.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class BSPEMRUtil {
	private static Map<String, Map<String, Object>> cacheUserRoles = new HashMap<String, Map<String, Object>>();
	private static Map<String, List<Map<String, Object>>> cacheRolePermissions = new HashMap<String, List<Map<String, Object>>>();

	/**
	 * 书写权限
	 */
	public static final String SXQX = "SXQX";

	/**
	 * 查看权限
	 */
	public static final String CKQX = "CKQX";
	/**
	 * 审阅权限
	 */
	public static final String SYQX = "SYQX";
	/**
	 * 打印权限
	 */
	public static final String DYQX = "DYQX";
	/**
	 * 创建
	 */
	public static final String OP_CREATE = "1001";
	/**
	 * 修改
	 */
	public static final String OP_UPDATE = "1002";
	/**
	 * 删除
	 */
	public static final String OP_DELETE = "1003";
	/**
	 * 签名
	 */
	public static final String OP_SIGNED = "1004";
	/**
	 * 打印
	 */
	public static final String OP_PRINT = "1005";
	/**
	 * 访问
	 */
	public static final String OP_VISIT = "1006";
	/**
	 * 质控
	 */
	public static final String OP_QC = "1007";

	/**
	 * 取消签名
	 */
	public static final String OP_UNSIGNED = "1008";

	/**
	 * 保存操作日志
	 * 
	 * @param op
	 *            操作类型 传入以上定义的全局常量
	 * @param data
	 *            EMR_BLSJRZ需要保存的字段信息 必须传入RZNR YWID1 YWID2 YEID3
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public static void doSaveEmrOpLog(String op, Map<String, Object> record,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnitId();
			String uid = (String) user.getUserId();
			if (OP_VISIT.equals(op)) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("uid", uid);
				parameters.put("ZYH", record.get("YWID1"));
				long count = dao
						.doCount(
								"ZY_BRRY",
								"ZYH = :ZYH and (ZYYS = :uid or ZSYS = :uid or ZZYS = :uid)",
								parameters);
				if (count == 0) {
					String text = DictionaryController.instance()
							.get("phis.dictionary.review").getText(op);
					record.put("RZNR", text + "{" + record.get("RZNR") + "}.");
					record.put("SJXM", op);
					record.put("XTSJ", new Date());
					record.put("CZGH", (String) user.getUserId());
					record.put("IPDZ", "UNKNOW");
					record.put("JSJM", "UNKNOW");
					record.put("JGID", manageUnit);
					dao.doSave("create", BSPHISEntryNames.EMR_BLSJRZ, record,
							false);
				}
			} else {
				String text = DictionaryController.instance()
						.get("phis.dictionary.review").getText(op);
				record.put("RZNR", text + "{" + record.get("RZNR") + "}.");
				record.put("SJXM", op);
				record.put("XTSJ", new Date());
				record.put("CZGH", (String) user.getUserId());
				record.put("IPDZ", "UNKNOW");
				record.put("JSJM", "UNKNOW");
				record.put("JGID", manageUnit);
				dao.doSave("create", BSPHISEntryNames.EMR_BLSJRZ, record, false);
			}
		} catch (ValidateException e) {
			throw new ModelDataOperationException("病历日志保存失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("病历日志保存失败", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("病历日志保存失败", e);
		}
	}

	/**
	 * 根据医疗角色，病历类别，权限类别拿角色病历权限集合
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBM
	 * @param docRole
	 *            医疗角色 ，写角色序号，即SYS_USERS_YH中的YLJS
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return 四个权限的键值对
	 */
	public static Map<String, Object> getDocRolePermissions(String caseType,
			String docRole, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = null;
		if (cacheRolePermissions.containsKey((docRole))) {
			list = cacheRolePermissions.get(docRole);
		} else {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "JSXH", "s", docRole);
			try {
				list = dao.doList(cnd, null, BSPHISEntryNames.EMR_YLJSBLQX);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
			}
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			if (map.get("BLLB").toString().equals(caseType)) {
				return map;
			}
		}
		return null;
	}

	/**
	 * 根据病历类别拿角色病历权限
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBH
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return 四个权限的键值对
	 */
	public static Map<String, Object> getCaseHistoryContory(String caseType,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = (String) user.getUserId();
		Map<String, Object> roleMap = getDocRoleByUid(uid, dao);
		String docRole = roleMap.get("JSXH").toString();
		return getDocRolePermissions(caseType, docRole, dao, ctx);
	}

	/**
	 * 根据病历类别，权限类别拿角色病历权限
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBH
	 * @param contoryType
	 *            权限类别 ，判断的字符串，
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return
	 */
	public static boolean getCaseHistoryContory(String caseType,
			String contoryType, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = (String) user.getUserId();
		Map<String, Object> roleMap = getDocRoleByUid(uid, dao);
		Object docRole = roleMap.get("JSXH");
		if (docRole == null || docRole.toString().trim().length() == 0) {
			return false;
		}
		return getCaseHistoryContory(caseType, contoryType, docRole.toString(),
				dao, ctx);
	}

	public static boolean getCaseHistoryContory(String caseType,
			String contoryType, String docRole, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		if (docRole == null || docRole.equals("")) {
			return false;
		}
		Map<String, Object> map = getDocRolePermissions(caseType, docRole, dao,
				ctx);
		if (map == null)
			return false;
		return (Integer) map.get(contoryType) == 1;
	}

	/**
	 * 根据uid获取医疗角色
	 * 
	 * @param uid
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static Map<String, Object> getDocRoleByUid(String uid, BaseDAO dao)
			throws ModelDataOperationException {
		// if (cacheUserRoles.containsKey(uid)) {
		// return cacheUserRoles.get(uid);
		// }

		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.PERSONID", "s", uid);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> list = dao.doList(cnd, "",
					BSPHISEntryNames.SYS_USERS_YLJS);
			if (list.size() > 0) {
				map = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
		}
		// cacheUserRoles.put(uid, map);
		return map;
	}

	/**
	 * 修改单个用户医疗角色时调用
	 * 
	 * @param uid
	 */
	public static void reloadDocRoleByUid(String uid) {
		cacheUserRoles.remove(uid);
	}

	/**
	 * 重载所有用户的医疗角色
	 */
	public static void reloadAllDocRoles() {
		cacheUserRoles.clear();
	}

	/**
	 * 修改某医疗角色下的权限时重载
	 * 
	 * @param docRole
	 */
	public static void reloadDocPermissionByRole(String docRole) {
		cacheRolePermissions.remove(docRole);
	}

	/**
	 * 重载整个医疗权限
	 */
	public static void reloadAllDocPermission() {
		cacheRolePermissions.clear();
	}
}
