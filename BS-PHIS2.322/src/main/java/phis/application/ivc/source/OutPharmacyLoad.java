package phis.application.ivc.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

public class OutPharmacyLoad extends SimpleQuery {
	/**
	 * 查询发药药房
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String pkey = (String) req.get("pkey");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		BaseDAO dao = new BaseDAO();
		StringBuffer hql = new StringBuffer("select CSMC as CSMC,CSZ as CSZ from GY_XTCS");
		hql.append(" where JGID='"+manageUnit+"' and CSMC like '"+pkey+"%'");//modified by zhangxw
		try {
			Map<String,Object> body = new HashMap<String,Object>();
			List<Map<String, Object>> list = dao.doQuery(hql.toString(), null);
			for(Map<String, Object> p : list) {
				String csmc = "";
				Object csz = "";
				for(String key : p.keySet()) {
					if(key.equals("CSMC")) {
						csmc = (String) p.get(key);
					}else if(key.equals("CSZ")){
						csz = p.get(key);
					}
				}
				body.put(csmc, csz);
			}
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
			res.put(RES_MESSAGE, "获取门诊发药药房数据失败!");
			return;
		}
	}
}