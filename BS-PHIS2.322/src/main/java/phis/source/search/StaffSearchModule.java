package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.util.context.Context;

public class StaffSearchModule extends AbstractSearchModule {
	/**
	 * 实现员工查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		//UserRoleToken user = UserRoleToken.getCurrent();
		//String manageUnit = user.getManageUnit().getId();
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		//parameters.put("JGID", manageUnit);
		parameters.put("PERSONID", searchText.substring(1));
		StringBuffer hql = new StringBuffer(
				"SELECT a.PERSONID as PERSONID,a.PERSONNAME as PERSONNAME FROM ");
		hql.append("SYS_Personnel");
		hql.append(" a where (a.PERSONID=:PERSONID OR a.PYCODE LIKE '");
		hql.append(searchText.toLowerCase());
		hql.append("%') ORDER BY a.PERSONID");
		try {
			Long count = dao.doCount("SYS_Personnel a",
					"(a.PERSONID=:PERSONID OR a.PYCODE LIKE '"
							+ searchText + "%')", parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> YGXX = dao.doSqlQuery(hql.toString(),
					parameters);
			
			res.put("count", count);
			res.put("mds", YGXX);
//			String retStr = JSONUtil.toJson(YGXX);
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
