/**
 * @(#)DoctorCustomMadeService.java Created on 2013-5-3 下午1:28:06
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class DoctorCustomMadeService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(DoctorCustomMadeService.class);

	public void doSavePersonalSetInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorCustomMadeModel dcmm = new DoctorCustomMadeModel(dao);
		String op = (String) req.get("op");
		String schema = (String) req.get("schema");
		String uid = (String) req.get("uid");
		Map<String, Object> saveData = (Map<String, Object>) req.get("body");
		Object XSBL = saveData.get("XSBL");
		if (XSBL != null && !XSBL.equals("")) {
			saveData.put("XSBL", XSBL);
		} else {
			saveData.put("XSBL", "");
		}
		saveData.put("YHBH", uid);
		Map<String, Object> result = null;
		try {
			result = dcmm.savePersonalSetInfo(op, schema, saveData);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存个人参数设置失败！", e);
		}
		res.put("body", result);
	}

	public void doLoadPersonalSetInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorCustomMadeModel dcmm = new DoctorCustomMadeModel(dao);
		String uid = (String) req.get("uid");
		String schema = (String) req.get("schema");
		Map<String, Object> result = null;
		try {
			result = dcmm.loadPersonalSetInfo(uid, schema);
			if (result != null) {
				Object XSBL = result.get("XSBL");
				if (XSBL != null && !XSBL.equals("")) {
					Map<String, Object> map = getXSBLBykey(XSBL);
					result.put("XSBL", map);
				}
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException("加载个人参数设置失败！", e);
		}
		res.put("body", result);
	}

	private Map<String, Object> getXSBLBykey(Object XSBL) {
		Map<String, Object> m = new HashMap<String, Object>();
		if(XSBL==null){
			return m;
		}
		String xsbl=XSBL.toString();
		if (xsbl.endsWith("-2")) {
			m.put("key", "-2");
			m.put("text", "页宽");
		} else if (xsbl.endsWith("-1")) {
			m.put("key", "-1");
			m.put("text", "整页");
		} else {
			m.put("key", xsbl);
			m.put("text", xsbl);
		}
		return m;
	}

	/**
	 * 根据用户编号获取该用户的个人参数设置
	 * 
	 * @param dao
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String, Object> getPersonalSetInfo(BaseDAO dao,Context ctx)
			throws ServiceException {
		String schema = BSPHISEntryNames.EMR_YSXG_GRCS;
		Map<String, Object> result = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = (String)user.getUserId();
		if (uid == null) {
			return null;
		}
		DoctorCustomMadeModel dcmm = new DoctorCustomMadeModel(dao);
		try {
			result = dcmm.loadPersonalSetInfo(uid, schema);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}

}
