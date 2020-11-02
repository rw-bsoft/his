package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.BSPHISEntryNames;
import phis.source.bean.Materials;
import phis.source.bean.MaterialsSearchData;
import phis.source.utils.JSONUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class MaintenanceServiceForSbSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资名称查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String searchText = MATCH_TYPE + req.get("query").toString().toUpperCase();
		String strStart = req.get("start") + "";// 分页用
		String strLimit = req.get("limit") + "";
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(
					"select d.WZBH as WZBH,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,d.ZCYZ as ZCYZ,d.ZBXH as ZBXH,d.CCBH as CCBH,f.DWMC as GHDW,e.KSMC as KSMC,d.TZRQ as TZRQ, c.CJMC as CJMC,d.WZXH as WZXH,d.CJXH as CJXH from ")
					.append("WL_WZZD a,")
					.append("WL_WZBM b,")
					.append("WL_SCCJ c,")
					.append("WL_GHDW f,")
					.append("WL_ZCZB d")
					.append(" left outer join gy_ksdm e on d.zyks = e.ksdm ")
					.append(" where b.wzxh = a.wzxh and a.wzxh = d.wzxh and d.cjxh = c.cjxh  and d.ghdw = f.dwxh and d.wzzt = 1 ")
					.append(" and b.").append(SEARCH_TYPE).append(" like '")
					.append(searchText).append("%' order by b.WZXH");

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
				m.setWZBH(parseLong(mats.get(i).get("WZBH")));
				m.setWZMC(mats.get(i).get("WZMC") + "");
				if ((mats.get(i).get("WZGG") + "").equals("null")) {
					m.setWZGG("");
				} else {
					m.setWZGG(mats.get(i).get("WZGG") + "");
				}
				m.setWZDW(mats.get(i).get("WZDW") + "");
				if (mats.get(i).get("ZBXH") != "") {
					m.setZBXH(Long.parseLong(mats.get(i).get("ZBXH") + ""));
				} else {
					m.setZBXH((long) 0);
				}
				m.setCJMC(mats.get(i).get("CJMC") + "");
				m.setCJXH(Long.parseLong(mats.get(i).get("CJXH") + ""));
				m.setGHDW(mats.get(i).get("GHDW") + "");
				Materials.add(m);
				if (i >= 9)
					break;
			}
			MaterialsSearchData mts = new MaterialsSearchData();
			mts.setCount(count);
			mts.setMats(Materials);
			res.put("count", count);
			res.put("mats", JSONUtil.ConvertObjToMapList(Materials));
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
