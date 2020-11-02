package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

public class LeaveHospitalLoad extends SimpleLoad {
	/**
	 * 查询病人就诊信息
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		parameters.put("ZYH", Long.parseLong(req.get("pkey").toString()));
		parameters.put("JGID", manageUnit);
		try {
			StringBuffer sql = new StringBuffer(
					" select a.ZYH as ZYH,a.ZYHM,a.BRXM,a.BRCH,a.BRXB,a.GZDW,a.RYQK,a.BRKS,a.CYPB,"
							+ BSPHISUtil.toChar("a.RYRQ","yyyy-mm-dd HH24:mi:ss")
							+ " as RYRQ,"+ BSPHISUtil.toChar("b.LCRQ","yyyy-mm-dd HH24:mi:ss")
							+ " as CYRQ,b.CYFS,b.BZXX,a.BRXZ,a.YBZY from ");
			sql.append("ZY_BRRY a");
			sql.append(" left join ZY_RCJL b on (a.ZYH=b.ZYH and a.JGID=b.JGID and b.BQPB=0 and b.CZLX=-1 ) ");
			sql.append(" where a.JGID=:JGID and a.ZYH=:ZYH");

			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					parameters);
			if (list.size() > 0) {
				res.put("body", list.get(0));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
