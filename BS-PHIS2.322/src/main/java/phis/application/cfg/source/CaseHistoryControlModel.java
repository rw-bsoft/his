/**
 * @(#)CaseHistoryControlModel.java Created on 2013-4-23 上午11:12:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPEMRUtil;
import phis.source.utils.CNDHelper;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.MD5StringUtil;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CaseHistoryControlModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(CaseHistoryControlModel.class);

	public CaseHistoryControlModel(BaseDAO dao) {
		this.dao = dao;
	}

	public List<Map<String, Object>> listDoctorRoles(String schema)
			throws ModelDataOperationException {

		List<Map<String, Object>> result = null;
		try {
			String hql = "select JSXH as JSXH,JSMC as JSMC,JSLX as JSLX,JSJB as JSJB,JSSM as JSSM from EMR_YLJS";
			result = dao.doQuery(hql, null);
		} catch (PersistentDataOperationException e) {
			logger.error("list doctor roles fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色列表查询失败！");
		}
		return result;
	}

	protected Map<String, Object> saveUserManage(String op,
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		try {
			remap = dao.doSave(op, BSPHISEntryNames.EMR_YLJS, body, true);
			Session sess = (Session) ctx.get(Context.DB_SESSION);
			sess.flush();
		} catch (ValidateException e) {
			logger.error("save doctor roles fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色列表保存失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save doctor roles fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色列表保存失败！");
		}
		return remap;
	}

	public List<Map<String, Object>> listRolesControy(String schema, List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		try {
			result = dao.doList(cnd, "BLLB", schema);
		} catch (PersistentDataOperationException e) {
			logger.error("list doctor roles fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色权限列表查询失败！");
		}
		return result;
	}

	protected List<Map<String, Object>> getCaseHistoryType()
			throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		Session session = sf.openSession();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		try {
			String hql = "select LBBH as LBBH,LBMC as LBMC from EMR_KBM_BLLB order by LBBH asc";
			Query q = session.createQuery(hql);
			List<Object[]> records = q.list();
			for (int i = 0; i < records.size(); i++) {
				Object[] r = records.get(i);
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("LBBH", r[0]);
				o.put("LBMC", r[1]);
				rs.add(o);
			}
		} finally {
			session.close();
		}
		return rs;
	}

	public List<Map<String, Object>> saveCaseContory(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		String docRole = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ctxList = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> record = body.get(i);
				docRole = parseToLong(record.get("JSXH")).toString();
				Map<String, Object> map = new HashMap<String, Object>();
				map.putAll(record);
				map.put("QXXH", parseToLong(record.get("QXXH")));
				map.put("JSXH", parseToLong(record.get("JSXH")));
				map.put("BLLB", parseToLong(record.get("BLLB")));
				map.put("SXQX", changeOneOrZero(record.get("SXQX")));
				map.put("CKQX", changeOneOrZero(record.get("CKQX")));
				map.put("SYQX", changeOneOrZero(record.get("SYQX")));
				map.put("DYQX", changeOneOrZero(record.get("DYQX")));
				ctxList.add(map);
				remap = dao.doSave("update", BSPHISEntryNames.EMR_YLJSBLQX,
						map, true);
				list.add(remap);
			}
			if (docRole != null) {
				BSPEMRUtil.reloadDocPermissionByRole(docRole);
			}
		} catch (ValidateException e) {
			logger.error("save roles contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色权限列表保存失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save roles contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色权限列表保存失败！");
		}
		return list;
	}

	private Long parseToLong(Object object) {
		if (object instanceof Long) {
			return (Long) object;
		}
		return Long.parseLong(object.toString());
	}

	private int changeOneOrZero(Object flag) {
		if (flag instanceof Integer) {
			return (Integer) flag;
		}
		boolean f = (Boolean) flag;
		if (f == true) {
			return 1;
		}
		return 0;
	}

	public List<Map<String, Object>> saveLoadContory(
			List<Map<String, Object>> createList)
			throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		String docRole = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < createList.size(); i++) {
				Map<String, Object> record = createList.get(i);
				docRole = parseToLong(record.get("JSXH")).toString();
				remap = dao.doSave("create", BSPHISEntryNames.EMR_YLJSBLQX,
						record, true);
				remap.putAll(record);
				list.add(remap);
			}
			if (docRole != null) {
				BSPEMRUtil.reloadDocPermissionByRole(docRole);
			}
		} catch (ValidateException e) {
			logger.error("save load roles contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载的医疗角色权限列表保存失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save load roles contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载的医疗角色权限列表保存失败！");
		}
		return list;
	}

	public void removeDoctorRoles(Object pkey)
			throws ModelDataOperationException {
		Long key = parseToLong(pkey);
		String hql = "delete from EMR_YLJSBLQX where JSXH=" + key;
		try {
			dao.doRemove(key, BSPHISEntryNames.EMR_YLJS);
			dao.doUpdate(hql, null);
			BSPEMRUtil.reloadDocPermissionByRole(key.toString());
		} catch (PersistentDataOperationException e) {
			logger.error("remove roles contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医疗角色权限列表删除失败！");
		}
	}

	public boolean getCaseHistoryContory(String caseType, String docRole,
			String contoryType, Context ctx) throws ModelDataOperationException {
		if (docRole == null || docRole.equals("")) {
			return false;
		}
		Integer contory = null;
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "JSXH", "s", docRole);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					BSPHISEntryNames.EMR_YLJSBLQX);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				if (map.get("BLLB").toString().equals(caseType)) {
					contory = (Integer) map.get(contoryType);
				}
			}
			Context caseCtx = new Context();
			caseCtx.put(docRole, list);
			ctx.putCtx("_caseContory", caseCtx);
		} catch (PersistentDataOperationException e) {
			logger.error("get contory fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
		}
		if (contory != null && contory.equals(1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据医疗角色，病历类别，权限类别拿角色病历权限
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBH
	 * @param docRole
	 *            医疗角色 ，写角色序号，即SYS_USERS_YH中的YLJS
	 * @param contoryType
	 *            权限类别 ，判断的字符串，
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return
	 */
	public static boolean getCaseHistoryContory(String caseType,
			String docRole, String contoryType, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		if (docRole == null || docRole.equals("")) {
			return false;
		}
		Integer contory = null;
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "JSXH", "s", docRole);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					BSPHISEntryNames.EMR_YLJSBLQX);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				if (map.get("BLLB").toString().equals(caseType)) {
					contory = (Integer) map.get(contoryType);
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
		}
		if (contory != null && contory.equals(1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据医疗角色，病历类别拿角色病历权限
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBH
	 * @param docRole
	 *            医疗角色 ，写角色序号，即SYS_USERS_YH中的YLJS
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return 四个权限的键值对
	 */
	public static Map<String, Object> getCaseHistoryContory(String caseType,
			String docRole, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		if (docRole == null || docRole.equals("")) {
			result.put("SXQX", false);
			result.put("CKQX", false);
			result.put("SYQX", false);
			result.put("DYQX", false);
			return result;
		}
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "JSXH", "s", docRole);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					BSPHISEntryNames.EMR_YLJSBLQX);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				if (map.get("BLLB").toString().equals(caseType)) {
					for (int j = 0; j < map.size(); j++) {
						result.put("SXQX", map.get("SXQX").equals(1) ? true
								: false);
						result.put("CKQX", map.get("CKQX").equals(1) ? true
								: false);
						result.put("SYQX", map.get("SYQX").equals(1) ? true
								: false);
						result.put("DYQX", map.get("DYQX").equals(1) ? true
								: false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
		}
		return result;
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
			Context ctx, BaseDAO dao) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String docRole = getDocRoleByUid(uid, dao);
		Map<String, Object> result = getCaseHistoryContory(caseType, docRole,
				ctx, dao);
		return result;
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
	public static boolean getCaseHistoryContory(BaseDAO dao, String caseType,
			String contoryType, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId();
		String docRole = getDocRoleByUid(uid, dao);
		boolean result = getCaseHistoryContory(caseType, docRole, contoryType,
				ctx, dao);
		return result;
	}

	private static String getDocRoleByUid(String uid, BaseDAO dao)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "userId", "s", uid);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(cnd, BSPHISEntryNames.SYS_USERS);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取出医疗角色权限失败！");
		}
		String result = null;
		if (map != null) {
			result = (String) map.get("YLJS");
		}
		return result;
	}

	public boolean checkDocRoleUsed(Object pkey)
			throws ModelDataOperationException {
		boolean flag = true;
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "medicalRoles", "s", pkey);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					BSPHISEntryNames.SYS_USERS_YLJS);
			if (list != null && list.size() > 0) {
				flag = false;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("checkDocRoleUsed fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查医疗角色是否被使用失败！");
		}
		return flag;
	}
}
