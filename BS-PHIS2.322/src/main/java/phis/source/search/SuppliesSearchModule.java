package phis.source.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class SuppliesSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		int ejkf = Integer.parseInt(user.getProperty("treasuryEjkf") + "");
		String glfs = "a.GLFS<>3";
		if (req.get("tag") != null) {
			if (Integer.parseInt(req.get("tag") + "") == 2) {
				glfs = "a.GLFS=2";
			} else if (Integer.parseInt(req.get("tag") + "") == 3) {
				glfs = "a.GLFS=3";
			}
		}
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manageUnit);
		parameters.put("KFXH", kfxh);
		StringBuffer hql = new StringBuffer();
		if (ejkf > 0) {
			hql = new StringBuffer(
					"SELECT a.WZXH as WZXH,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,c.CJXH as CJXH,c.CJMC as CJMC,e.KFXH as KFXH,a.ZBLB as ZBLB,b.WZJG as WZJG,b.LSJG as LSJG from ");
			hql.append("WL_WZZD");
			hql.append(" a,");
			hql.append("WL_WZCJ");
			hql.append(" b,");
			hql.append("WL_SCCJ");
			hql.append(" c,");
			hql.append("WL_EJJK");
			hql.append(" e,");
			hql.append("WL_WZBM");
			hql.append(" f where a.WZXH=b.WZXH AND a.WZXH=f.WZXH AND b.CJXH=c.CJXH AND (e.KFXH =:KFXH or (e.JGID=:JGID AND e.KSDM=0) or e.KFXH = 0) AND e.WZZT=1 AND a.WZXH=e.WZXH AND ");
			hql.append(glfs);
			hql.append(" AND f.PYDM LIKE '");
			hql.append(searchText);
			hql.append("%' ORDER BY a.WZXH");
		} else {
			hql = new StringBuffer(
					"SELECT a.WZXH as WZXH,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,c.CJXH as CJXH,c.CJMC as CJMC,e.KFXH as KFXH,a.ZBLB as ZBLB,b.WZJG as WZJG,b.LSJG as LSJG from ");
			hql.append("WL_WZZD");
			hql.append(" a,");
			hql.append("WL_WZCJ");
			hql.append(" b,");
			hql.append("WL_SCCJ");
			hql.append(" c,");
			hql.append("WL_WZGS");
			hql.append(" e,");
			hql.append("WL_WZBM");
			hql.append(" f where a.WZXH=b.WZXH AND a.WZXH=f.WZXH AND b.CJXH=c.CJXH AND e.JGID =:JGID AND e.KFXH =:KFXH AND a.WZZT=1 AND a.WZXH=e.WZXH AND ");
			hql.append(glfs);
			hql.append(" AND f.PYDM LIKE '");
			hql.append(searchText);
			hql.append("%' ORDER BY a.WZXH");
		}
		try {
			Long count = dao
					.doCount(
							"WL_WZZD a,WL_WZCJ b,WL_SCCJ c,WL_KFXX d,WL_WZGS e",
							"a.WZXH=b.WZXH AND b.CJXH=c.CJXH AND e.KFXH=d.KFXH AND a.JGID =:JGID AND a.KFXH =:KFXH AND a.WZZT=1 AND a.WZXH=e.WZXH AND "
									+ glfs
									+ " AND a.PYDM LIKE '"
									+ searchText
									+ "%'", parameters);
			parameters.put("first", Integer.parseInt(strStart));
			parameters.put("max", Integer.parseInt(strLimit));
			List<Map<String, Object>> WZXX = dao.doSqlQuery(hql.toString(),
					parameters);
			for (int i = 0; i < WZXX.size(); i++) {
				if (WZXX.get(i).get("WZGG") != null) {
					WZXX.get(i).put("WZGG", WZXX.get(i).get("WZGG"));
				} else {
					WZXX.get(i).put("WZGG", "");
				}
				if (WZXX.get(i).get("WZDW") != null) {
					WZXX.get(i).put("WZDW", WZXX.get(i).get("WZDW"));
				} else {
					WZXX.get(i).put("WZDW", "");
				}
				if (WZXX.get(i).get("CJMC") != null) {
					WZXX.get(i).put("CJMC", WZXX.get(i).get("CJMC"));
				} else {
					WZXX.get(i).put("CJMC", "");
				}
			}
			res.put("count", count);
			res.put("mds", WZXX);
			// String retStr = JSONUtil.toJson(WZXX);
			// retStr = "{\"count\":" + count + ",\"mds\":" + retStr + "}";
			// res.setCharacterEncoding("utf-8");
			// PrintWriter out = res.getWriter();
			// out.println(retStr);
			// out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
