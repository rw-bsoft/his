package phis.source.search;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import phis.source.utils.JSONUtil;
import phis.source.bean.MedicineOrigin;
import phis.source.bean.OriginSearchData;

import ctd.util.context.Context;

public class OriginSearchModule extends AbstractSearchModule {
	/**
	 * 实现产地查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String type = req.get("type") == null ? null : req.get("type").toString();
		if (type == null) {
			type = "1";
		}
		if (containsChinese(searchText)) {
			SEARCH_TYPE = "CDMC";
		}
		try {
			String hql = "select new phis.source.bean.MedicineOrigin(YPCD,CDMC,PYDM) From YK_CDDZ where "+SEARCH_TYPE+" LIKE :searchText";
			String hql_count = "select count(*) from YK_CDDZ where "+SEARCH_TYPE+" LIKE :searchText";
			Long count = (Long) ss.createQuery(hql_count).setString("searchText",searchText+ "%").uniqueResult();
			List<MedicineOrigin> origins = ss.createQuery(hql).setString("searchText",searchText+ "%")
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < origins.size(); i++) {
				origins.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("origins", JSONUtil.ConvertObjToMapList(origins));
//			OriginSearchData msd = new OriginSearchData();
//			msd.setCount(count);
//			msd.setOrigins(origins);
//			String retStr = JSONUtil.toJson(msd);
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
