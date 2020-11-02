package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.bean.Materials;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class ApplyRegisterSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资名称查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();

		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(
					"select a.WZXH as WZXH,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,a.GLFS as GLFS,a.ZBLB as ZBLB,b.KFXH as KFXH from ")
					.append("WL_WZZD a,")
					.append("WL_WZGS b,")
					.append("WL_WZBM e")
					.append(" where a.WZXH=b.WZXH and a.WZXH=e.WZXH  and b.KFXH in (select KFXH from WL_KFXX  where JGID = ")
					.append(JGID).append(" and kflb = 1) ").append(" and e.")
					.append(SEARCH_TYPE).append(" like '").append(searchText)
					.append("%' order by b.WZXH");

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
				m.setWZMC(mats.get(i).get("WZMC") + "");
				if ((mats.get(i).get("WZGG") + "").equals("null")) {
					m.setWZGG("");
				} else {
					m.setWZGG(mats.get(i).get("WZGG") + "");
				}
				m.setWZDW(mats.get(i).get("WZDW") + "");
				if (mats.get(i).get("KFXH") != "") {
					m.setKFXH(Integer.parseInt(mats.get(i).get("KFXH") + ""));
				} else {
					m.setKFXH(0);
				}
				if (mats.get(i).get("ZBLB") != "") {
					m.setZBLB(Integer.parseInt(mats.get(i).get("ZBLB") + ""));
				} else {
					m.setZBLB(0);
				}
				m.setCJXH(mats.get(i).get("CJXH") == null ? 0L : parseLong(mats
						.get(i).get("CJXH")));
				m.setCJMC(mats.get(i).get("CJMC") + "");
				Materials.add(m);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("mats", JSONUtil.ConvertObjToMapList(Materials));
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
