package phis.source.search;

import java.util.List;
import java.util.Map;

import phis.source.bean.Clinic;
import phis.source.utils.JSONUtil;
import ctd.util.context.Context;

public class ClinicBasicSearchModule extends AbstractSearchModule {

	/**
	 * 实现诊疗查询功能
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		try {
			String hql = "select distinct new phis.source.bean.Clinic(a.FYXH,a.FYMC,a.FYDW,a.BZJG) from GY_YLSF a,GY_FYBM b where a.FYXH=b.FYXH and a.ZFPB=0 and a.MZSY=1 and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%' order by length(a.FYMC),a.FYXH";
			String hql_count = "select count(*) from GY_YLSF a,GY_FYBM b where a.FYXH=b.FYXH and a.ZFPB=0 and a.MZSY=1 and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";
			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Clinic> clinic = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < clinic.size(); i++) {
				clinic.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("clinic", JSONUtil.ConvertObjToMapList(clinic));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
