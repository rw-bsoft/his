package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.bean.Project;
import phis.source.utils.JSONUtil;
import ctd.util.context.Context;

public class ProjectSearchModule extends AbstractSearchModule {
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		// User user = Dispatcher.getUser(req);
		// String jgid=user.get("manageUnit.id");// 用户的机构ID
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(
					"select distinct a.FYXH as FYXH,a.PYDM as PYDM,a.FYMC as FYMC,a.FYDW as FYDW,a.BZJG as FYDJ from ")
					.append("GY_YLSF")
					.append(" a,")
					.append("GY_FYBM")
					.append(" b where a.FYXH=b.FYXH  and a.ZYSY = 1 and a.ZFPB = 0 ")
					.append(" and b.").append(SEARCH_TYPE).append(" like '")
					.append(searchText).append("%' order by a.PYDM,a.FYXH");
			StringBuffer sql_count = new StringBuffer();
			sql_count.append("select count(*) as TOTAL from (")
					.append(sql.toString()).append(")");
			List<Map<String, Object>> l = ss
					.createSQLQuery(sql_count.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			Long count = Long.parseLong(l.get(0).get("TOTAL") + "");
			List<Map<String, Object>> projects = ss
					.createSQLQuery(sql.toString())
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			List<Project> list_projects = new ArrayList<Project>();
			for (int i = 0; i < projects.size(); i++) {
				Map<String, Object> map_project = projects.get(i);
				Project pro = new Project();
				pro.setNumKey((i + 1 == 10) ? 0 : i + 1);
				pro.setFYDJ(Double.parseDouble(map_project.get("FYDJ") + ""));
				pro.setFYDW(map_project.get("FYDW") + "");
				pro.setFYMC(map_project.get("FYMC") + "");
				pro.setFYXH(Long.parseLong(map_project.get("FYXH") + ""));
				list_projects.add(pro);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("project", JSONUtil.ConvertObjToMapList(list_projects));
//			ProjectSearchData projectserchData = new ProjectSearchData();
//			projectserchData.setCount(count);
//			projectserchData.setProject(list_projects);
//			String retStr = JSONUtil.toJson(projectserchData);
//			res.setCharacterEncoding("utf-8");
//			PrintWriter out = res.getWriter();
//			out.println(retStr);
//			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
