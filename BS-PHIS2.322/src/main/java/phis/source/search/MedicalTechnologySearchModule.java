package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class MedicalTechnologySearchModule extends AbstractSearchModule {
	/**
	 * 实现诊疗查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String syfs = req.get("useType") + "";// MZSY ZYSY
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		long FYKS = Long.parseLong(req.get("FYKS") + "");//
		if (syfs == null || syfs.trim().equals("")) {
			syfs = "MZSY";
		}
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		BaseDAO dao = new BaseDAO();
		try {
			// 项目类型XMLX的nvl写法DB2下报错
			Map<String, Object> parameters = new HashMap<String, Object>();
//			parameters.put("PYDM", searchText + "%");
			parameters.put("JGID", JGID);
			parameters.put("FYKS", FYKS);
			String hql = "SELECT a.FYMC as FYMC, b.FYDW as FYDW, a.PYDM as PYDM, a.FYXH as FYXH, b.FYGB as FYGB, c.FYKS as FYKS, c.FYDJ as FYDJ, a.BMFL as BMFL, 0 as TCBZ FROM GY_FYBM a, GY_YLSF b, GY_YLMX c WHERE ( a.FYXH = b.FYXH ) and (c.FYXH = b.FYXH ) and ( b.YJSY = 1  ) and ( b.ZFPB = 0 ) and ( ( c.FYKS is NULL ) OR ( c.FYKS = 0 ) OR ( c.FYKS = :FYKS ) ) and ( c.JGID = :JGID ) and a."+SEARCH_TYPE+" like '"+searchText+"%' order by length(b.FYMC),b.FYXH";
			long count = dao
					.doCount(
							"GY_FYBM a, GY_YLSF b, GY_YLMX c ",
							"( a.FYXH = b.FYXH ) and (c.FYXH = b.FYXH ) and ( b.YJSY = 1  ) and ( b.ZFPB = 0 ) and ( ( c.FYKS is NULL ) OR ( c.FYKS = 0 ) OR ( c.FYKS = :FYKS ) ) and ( c.JGID = :JGID ) and a."+SEARCH_TYPE+" like '"+searchText+"%'",
							parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> Diseases = dao.doQuery(hql, parameters);
			for (int i = 0; i < Diseases.size(); i++) {
				Diseases.get(i).put("numKey", (i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("disease", Diseases);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
