package phis.application.cic.source;

import java.util.HashMap;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleSave;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class OutPharmacySave extends SimpleSave {
	/**
	 * 查询发药药房
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		BaseDAO dao = new BaseDAO();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		for(String key : body.keySet()) {
			Map<String, Object> record = new HashMap<String, Object>();
			record.put("JGID", manageUnit);
			record.put("CSMC", key);
			record.put("CSZ", body.get(key));
			try {
				String op = "create";
				long l = dao.doCount("GY_XTCS", "CSMC='"+key+"' and JGID='"+manageUnit+"'", null);
				if(l>0) {
					String upSql = "update GY_XTCS set CSZ=:CSZ where JGID=:JGID and CSMC=:CSMC";
					dao.doSqlUpdate(upSql, record);
				}else {
					dao.doSave(op, BSPHISEntryNames.GY_XTCS_CIC, record, false);
				}
				ParameterUtil.reloadParams(manageUnit, key);
			} catch (ValidateException e) {
				res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
				res.put(RES_MESSAGE, "保存门诊发药药房数据失败!" + e.getMessage());
				e.printStackTrace();
				return;
			} catch (PersistentDataOperationException e) {
				res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
				res.put(RES_MESSAGE, "保存门诊发药药房数据失败!" + e.getMessage());
				e.printStackTrace();
				return;
			}
		}
	}
}
