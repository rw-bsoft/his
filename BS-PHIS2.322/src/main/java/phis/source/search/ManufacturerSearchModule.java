package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.bean.Materials;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class ManufacturerSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资名称查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		//String JGID = user.getManageUnit().getId();// 用户的机构ID
		String KFXH = user.getProperty("treasuryId").toString();
		
		String searchText = MATCH_TYPE+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(
					"select DWMC as DWMC,DWXH as DWXH from  ")
					.append(" WL_GHDW  ")
					.append(" where  DWZT = 1 and (KFXH = 0 or ")
					.append(" KFXH = ").append(KFXH).append(") and ( ")
					.append(SEARCH_TYPE).append(" like '").append(searchText)
					.append("%'  or WBDM LIKE '").append(searchText)
					.append("%') order by DWXH ");

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
				m.setDWXH(parseLong(mats.get(i).get("DWXH")));
				m.setDWMC(mats.get(i).get("DWMC") + "");
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
