package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.util.context.Context;

public class ChineseSymptomSearchModule extends AbstractSearchModule {
	public void execute(Map<String, Object> req, Map<String, Object> res,Context ctx) {
		String searchText = req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			String hql = "select distinct ZHBS as ZHBS,ZHMC as ZHMC,BZXX as BZXX from EMR_ZYZH where PYDM LIKE '" + searchText + "%'";
			Long count = dao.doCount("EMR_ZYZH","PYDM LIKE '"+ searchText + "%'", parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> list_sym = dao.doSqlQuery(hql.toString(),parameters);
		
			for (Map<String, Object> map_fyxx : list_sym) {
				if("null".equals(map_fyxx.get("ZHMC")+"")){
					map_fyxx.put("ZHMC", "");
				}
				if("null".equals(map_fyxx.get("BZXX")+"")){
					map_fyxx.put("BZXX", "");
				}
			}
			res.put("count", count);
			res.put("sym", list_sym);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
