/**
 * @(#)CaseHistoryControlService.java Created on 2013-4-23 上午11:12:19
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.SchemaUtil;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class CaseHistoryControlService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(CaseHistoryControlService.class);

	public void doListDoctorRoles(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		String schema = (String) req.get("schema");
		List<Map<String, Object>> result = null;
		try {
			result = chcm.listDoctorRoles(schema);
			SchemaUtil.setDictionaryMassageForList(result, schema);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医疗角色列表查询失败！", e);
		}
		res.put("body", result);
	}

	public void doSaveAndListRolesControy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		String schema = (String) req.get("schema");
		List<?> cnd = (List<?>) req.get("cnd");
		List<?> jsxhCnd = (List<?>) cnd.get(2);
		Long reqjsxh = Long.parseLong(jsxhCnd.get(1).toString());
		List<Map<String, Object>> result = null;
		List<Map<String, Object>> allResult = null;
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		try {
			result = chcm.listRolesControy(schema, cnd);
			allResult = chcm.getCaseHistoryType();
			l = allResult;
			for (int i = 0; i < result.size(); i++) {
				Map<String, Object> m = result.get(i);
				Long bllb = (Long) m.get("BLLB");
				for (int j = 0; j < allResult.size(); j++) {
					Long lbbh = Long.parseLong(allResult.get(j).get("LBBH").toString());
					String lbmc = (String) allResult.get(j).get("LBMC");
					if (lbbh.equals(bllb)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("QXXH", m.get("QXXH"));
						map.put("JSXH", m.get("JSXH"));
						map.put("SXQX", m.get("SXQX"));
						map.put("CKQX", m.get("CKQX"));
						map.put("SYQX", m.get("SYQX"));
						map.put("DYQX", m.get("DYQX"));
						map.put("BLLB", lbbh);
						map.put("LBMC", lbmc);
						updateList.add(map);
						l.remove(j);
					}
				}
			}
			for (int i = 0; i < l.size(); i++) {
				Map<String, Object> m = new HashMap<String, Object>();
				Long lbbh = Long.parseLong(l.get(i).get("LBBH").toString());
				String lbmc = (String) l.get(i).get("LBMC");
				m.put("BLLB", lbbh);
				m.put("JSXH", reqjsxh);
				m.put("SXQX", 0);
				m.put("CKQX", 0);
				m.put("SYQX", 0);
				m.put("DYQX", 0);
				m.put("LBMC", lbmc);
				createList.add(m);
			}
			List<Map<String, Object>> reList = chcm.saveLoadContory(createList);
			updateList.addAll(reList);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医疗角色权限列表查询失败！", e);
		}
		res.put("body", updateList);
	}

	private boolean getTrueOrFalse(Object object) {
		boolean flag = false;
		int value = (Integer) object;
		if (value == 1) {
			flag = true;
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	protected void doSaveUserManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> re = chcm.saveUserManage(op, body, ctx);
			DictionaryController.instance().reload("docRole");
			res.put("body", re);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("角色保存失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doSaveCaseContory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			List<Map<String, Object>> re = chcm.saveCaseContory(body,ctx);
			Session sess = (Session) ctx.get(Context.DB_SESSION);
			sess.flush();
			res.put("body", re);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("角色权限保存失败！", e);
		}
	}

	protected void doRemoveDoctorRoles(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		Object pkey = req.get("pkey");
		try {
			chcm.removeDoctorRoles(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("角色权限删除失败！", e);
		}
	}
	protected void doCheckDocRoleUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		Object pkey = req.get("pkey");
		boolean flag=true;
		try {
			flag=chcm.checkDocRoleUsed(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("检查医疗角色是否被使用失败！", e);
		}
		res.put("flag", flag);
	}

	/**
	 * 根据医疗角色，病历类别，权限类别拿角色病历权限
	 * 
	 * @param caseType
	 *            病历类别，写类别编码，即EMR_KBM_BLLB中的LBBM
	 * @param docRole
	 *            医疗角色 ，写角色序号，即SYS_USERS_YH中的YLJS
	 * @param contoryType
	 *            权限类别 ，判断的字符串，
	 * @param dao
	 * @param ctx
	 *            ‘SXQX’ 代表书写权限， ‘CKQX’ 代表查看权限， ‘SYQX’ 代表审阅权限， ‘DYQX’ 代表打印权限，
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean getCaseHistoryContory(String caseType,
			String docRole, String contoryType, BaseDAO dao, Context ctx) {
		CaseHistoryControlModel chcm = new CaseHistoryControlModel(dao);
		boolean contory = false;
		try {
			contory = chcm.getCaseHistoryContory(caseType, docRole,
					contoryType, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		return contory;
	}
}
