/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 统一业务权限维护Service
 * 
 * @author yangl 2012.06
 */
public class ConfigCommBusPermissionService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 保存统一业务权限
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePermission(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		String busType = (String) jsonReq.get("busType");
		Long ksdm = Long.parseLong(jsonReq.get("ksdm").toString());
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> body = (List<Map<String, Object>>) jsonReq
				.get("body");
		try {
			// Map<String, Object> params = new HashMap<String, Object>();
			// params.put("tableName", BSPHISEntryNames.GY_QXKZ);
			// params.put("YWLB", busType);
			// params.put("JGID", manageUnit);
			// params.put("KSDM", ksdm);
			dao.doUpdate("delete from GY_QXKZ where YWLB='" + busType + "' and JGID='" + manageUnit
					+ "' and KSDM=" + ksdm + "", null);
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> qxkz = body.get(i);
				if (qxkz.get("MRBZ") == null || qxkz.get("MRBZ").equals(""))
					qxkz.put("MRBZ", 0);
				qxkz.put("YWLB", busType);
				qxkz.put("JGID", manageUnit);
				qxkz.put("KSDM", ksdm);
				qxkz.put("YGDM", qxkz.get("PERSONID"));
				dao.doSave("create", BSPHISEntryNames.GY_QXKZ, qxkz, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException("业务权限设置失败，请联系管理员！", e);
		}

		jsonRes.put(RES_CODE, ServiceCode.CODE_OK);
		jsonRes.put(RES_MESSAGE, "Success");
	}

}
