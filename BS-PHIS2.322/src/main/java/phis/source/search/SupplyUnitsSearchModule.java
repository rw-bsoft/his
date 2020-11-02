package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class SupplyUnitsSearchModule extends AbstractSearchModule {
	/**
	 * 实现供货单位查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		// System.out.println(req);
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", kfxh);
		StringBuffer hql = new StringBuffer(
				"SELECT a.DWXH as DWXH,a.DWMC as DWMC FROM ");
		hql.append("WL_GHDW");
		hql.append(" a where (a.KFXH=0 or a.KFXH=:KFXH) AND a.DWZT=1 AND a.PYDM LIKE '");
		hql.append(searchText);
		hql.append("%' ORDER BY a.DWXH");
		try {
			Long count = dao.doCount("WL_GHDW a",
					"(a.KFXH=0 or a.KFXH=:KFXH) AND a.DWZT=1 AND a.PYDM LIKE '"
							+ searchText + "%'", parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> GHDWXX = dao.doSqlQuery(hql.toString(),
					parameters);
			res.put("count", count);
			res.put("mds", GHDWXX);
//			String retStr = JSONUtil.toJson(GHDWXX);
//			retStr = "{\"count\":" + count + ",\"mds\":" + retStr + "}";
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
