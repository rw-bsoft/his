package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.util.context.Context;

public class SurgerySearchModule extends AbstractSearchModule {
	/**
	 * 实现药品查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(
				"select a.SSNM as SSNM,a.SSDM as SSDM,a.SSMC as SSMC from ");
		hql.append("GY_SSDM a where a.ZJDM LIKE '");
		hql.append(searchText);
		hql.append("%' or a.SSMC like '");
		hql.append(searchText);
		hql.append("%' or a.SSDM like '");
		hql.append(searchText);
		hql.append("%'");
		hql.append(" order by length(a.SSMC)");
		try {
			Long count = dao.doCount("GY_SSDM a", "a.ZJDM LIKE '" + searchText
					+ "%' or a.SSMC like '" + searchText
					+ "%' or a.SSDM like '" + searchText + "%'", parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> SSXX = dao.doSqlQuery(hql.toString(),
					parameters);
			res.put("count", count);
			res.put("ss", SSXX);
//			String retStr = JSONUtil.toJson(SSXX);
//			retStr = "{\"count\":" + count + ",\"ss\":" + retStr + "}";
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
