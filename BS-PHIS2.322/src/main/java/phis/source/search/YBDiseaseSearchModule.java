package phis.source.search;

import java.util.List;
import java.util.Map;

import phis.source.bean.Disease;
import phis.source.utils.JSONUtil;
import ctd.util.context.Context;

public class YBDiseaseSearchModule extends AbstractSearchModule {
	/**
	 * 疾病查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String drugType = req.get("drugType").toString();// 药品类别
		if (drugType == null) {
			drugType = "1";
		}
		try {
			String hql = "select new com.bsoft.bsphis.bean.Disease(a.JBXH,a.JBMC,a.ICD10) from YB_JBBM a where a.PYDM LIKE '%"
					+ searchText + "%' or a.JBMC LIKE '%"+ searchText + "%' order by a.JBXH";
			String hql_count = "select count(a.JBXH) from YB_JBBM a where a.PYDM LIKE '%"
					+ searchText + "%' or a.JBMC LIKE '%"+ searchText + "%'";
			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Disease> Diseases = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < Diseases.size(); i++) {
				Diseases.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("disease", JSONUtil.ConvertObjToMapList(Diseases));
//			DiseaseSearchData disease = new DiseaseSearchData();
//			disease.setCount(count);
//			disease.setDisease(Diseases);
//			String retStr = JSONUtil.toJson(disease);
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
