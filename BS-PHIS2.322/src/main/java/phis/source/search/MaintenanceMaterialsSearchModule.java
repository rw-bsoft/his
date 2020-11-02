package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.bean.Materials;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class MaintenanceMaterialsSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资名称查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		// 库房序号
		Integer kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		String JGID = user.getManageUnit().getId();

		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT c.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.KCXH as KCXH,c.CJXH as CJXH,d.CJMC as CJMC,c.WZPH as WZPH,c.SCRQ as SCRQ,c.SXRQ as SXRQ,c.WZJG as WZJG,c.LSJG as LSJG,c.MJPH as MJPH,c.WZSL- c.YKSL as TJSL,b.BKBZ as BKBZ,b.GLFS as GLFS from ")
					.append(" WL_WZBM a,")
					.append(" WL_WZZD b,")
					.append(" WL_WZKC c,")
					.append(" WL_SCCJ d where a.WZXH=b.WZXH AND b.WZXH=c.WZXH AND c.CJXH=d.CJXH and b.WZZT >0 and b.GLFS=1 AND c.KFXH=")
					.append(kfxh).append(" AND (c.WZSL - c.YKSL) > 0")
					.append(" AND (a.").append(SEARCH_TYPE).append(" like '")
					.append(searchText).append("%' or a.WBDM ")
					.append(" like '").append(searchText)
					.append("%') order by b.WZXH,d.CJXH");
			StringBuffer sql_count = new StringBuffer();
			sql_count.append("select count(*) as TOTAL from (")
					.append(sql.toString()).append(")");
			List<Map<String, Object>> l = ss
					.createSQLQuery(sql_count.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			Long count = Long.parseLong(l.get(0).get("TOTAL") + "");
			List<Map<String, Object>> mats = ss.createSQLQuery(sql.toString())
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			List<Materials> Materials = new ArrayList<Materials>();
			for (int i = 0; i < mats.size(); i++) {
				Materials m = new Materials();
				m.setNumKey((i + 1 == 10) ? 0 : i + 1);
				m.setWZXH(parseLong(mats.get(i).get("WZXH")));
				m.setKCXH(parseLong(mats.get(i).get("KCXH")));
				m.setWZMC(mats.get(i).get("WZMC") + "");
				if (mats.get(i).get("WZGG") != null
						&& mats.get(i).get("WZGG") != "") {
					m.setWZGG(mats.get(i).get("WZGG") + "");
				} else {
					m.setWZGG("");
				}
				if (mats.get(i).get("WZDW") != null
						&& mats.get(i).get("WZDW") != "") {
					m.setWZDW(mats.get(i).get("WZDW") + "");
				} else {
					m.setWZDW("");
				}

				m.setCJXH(mats.get(i).get("CJXH") == null ? 0L : parseLong(mats
						.get(i).get("CJXH")));
				if (mats.get(i).get("CJMC") != null
						&& mats.get(i).get("CJMC") != "") {
					m.setCJMC(mats.get(i).get("CJMC") + "");
				} else {
					m.setCJMC("");
				}
				m.setGLFS(Integer.parseInt(mats.get(i).get("GLFS") + ""));
				if (mats.get(i).get("WZJG") != null
						&& mats.get(i).get("WZJG") != "") {
					m.setWZJG(Double.parseDouble(mats.get(i).get("WZJG") + ""));
				}
				if (mats.get(i).get("LSJG") != null
						&& mats.get(i).get("LSJG") != "") {
					m.setLSJG(Double.parseDouble(mats.get(i).get("LSJG") + ""));
				} else {
					m.setLSJG(Double.parseDouble(mats.get(i).get("WZJG") + ""));
				}
				if (mats.get(i).get("TJSL") != null
						&& mats.get(i).get("TJSL") != "") {
					m.setTJSL(Double.parseDouble(mats.get(i).get("TJSL") + ""));
				} else {
					m.setTJSL(0.00);
				}
				Materials.add(m);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("mats", JSONUtil.ConvertObjToMapList(Materials));
			// MaterialsSearchData mts = new MaterialsSearchData();
			// mts.setCount(count);
			// mts.setMats(Materials);
			// String retStr = JSONUtil.toJson(mts);
			// res.setCharacterEncoding("utf-8");
			// PrintWriter out = res.getWriter();
			// out.println(retStr);
			// out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-7-27
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

}
