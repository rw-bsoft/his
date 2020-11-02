package phis.application.fsb.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

public class FamilyPatientLoad extends SimpleLoad {
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
					" select a.CCQK as CCQK,a.JCBH,a.ZYH as ZYH,a.ZYHM,a.JCHM,a.BRXM,a.BRXB,a.BRXZ,a.LXDZ,a.LXRM,a.LXGX,a.LXDH,a.JCLX,a.SFZH,a.RYNL,a.RYQK,a.CYPB,a.KSRQ,a.JSRQ,a.CYFS,a.CYRQ from ");
			sql.append("JC_BRRY a");
			sql.append(" where a.JGID=:JGID and a.ZYH=:ZYH");

			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					parameters);
			if (list.size() > 0) {
				Map<String, Object> body = list.get(0);
				String hql = "select ZDMC as ZDMC,ICD10 as ICD10,ZDSJ as ZDSJ,ZDLB as ZDLB from JC_BRZD a where a.JGID=:JGID and a.ZYH=:ZYH";
				List<Map<String, Object>> brzdList = dao.doSqlQuery(hql,
						parameters);
				for (Map<String, Object> brzd : brzdList) {
					if ("1".equals(brzd.get("ZDLB").toString())) {
						body.put("JCZD", brzd.get("ZDMC"));
						body.put("ICD10_JC", brzd.get("ICD10"));
						body.put("ZDRQ", brzd.get("ZDSJ"));
					} else if ("2".equals(brzd.get("ZDLB").toString())) {
						body.put("CCZD", brzd.get("ZDMC"));
						body.put("ICD10_CC", brzd.get("ICD10"));
						body.put("CCRQ", brzd.get("ZDSJ"));
					}
				}
				res.put("body", body);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
