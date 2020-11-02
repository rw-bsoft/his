package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.util.context.Context;

public class HealthEducationSearchModule extends AbstractSearchModule {
	/**
	 * 健康教育查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toLowerCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select a.recipeName as RECIPENAME,b.diagnoseId, b.diagnoseName,b.ICD10,a.healthTeach,a.recipeNamePy ");
			hql.append(" from PUB_PelpleHealthTeach a,PUB_PelpleHealthDiagnose b ");
			hql.append(" where b.recordId = a.recordId and a.recipeNamePy like :PYDM order by length(a.recipeNamePy), b.ICD10,  a.recipeNamePy ");
			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append(" select count(1) from PUB_PelpleHealthTeach a,PUB_PelpleHealthDiagnose b");
			hqlCount.append(" where b.recordId = a.recordId and a.recipeNamePy like :PYDM ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("PYDM", searchText + "%");
			Long count = Long.parseLong(ss.createSQLQuery(hqlCount.toString())
					.setString("PYDM", searchText + "%").uniqueResult()
					.toString());
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = dao.doSqlQuery(hql.toString(), parameters);
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("healthEdu", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
