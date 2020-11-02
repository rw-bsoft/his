package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.JSONUtil;
import ctd.util.context.Context;

public class DiseaseSiteSearchModule extends AbstractSearchModule {
	/**
	 * 疾病查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		long JBXH = Long.parseLong(req.get("JBXH") + "");// 药品类别
		int ZXLB = Integer.parseInt(req.get("ZXLB") + "");// 中西类别
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		try {
			if (ZXLB == 1) {
				String tableName = "EMR_JBBW";
				String hqlWhere = "JBXH=:JBXH and PYDM LIKE :PYDM";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("PYDM", searchText + "%");
				parameters.put("JBXH", JBXH);
				String hql = "select a.JBBW as JBBW,a.SYMC as SYMC from EMR_JBBW a where a.JBXH=:JBXH and a.PYDM LIKE :PYDM order by a.JBBW";
				Long count = dao.doCount(tableName, hqlWhere, parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> Diseases = dao.doQuery(hql,
						parameters);
				for (int i = 0; i < Diseases.size(); i++) {
					Diseases.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
					if (i >= 9)
						break;
				}
				res.put("count", count);
				res.put("disease", Diseases);
			} else {
				String tableName = "EMR_ZYZH";
				String hqlWhere = "PYDM LIKE :PYDM";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("PYDM", searchText + "%");
				String hql = "select a.ZHBS as JBBW,a.ZHMC as SYMC from EMR_ZYZH a where a.PYDM LIKE :PYDM order by a.ZHBS";
				Long count = dao.doCount(tableName, hqlWhere, parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> Diseases = dao.doQuery(hql,
						parameters);
				for (int i = 0; i < Diseases.size(); i++) {
					Diseases.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
					if (i >= 9)
						break;
				}
				res.put("count", count);
				res.put("disease", Diseases);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
