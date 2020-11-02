/**
 * @(#)MedicineAllCHISSearchModule.java Created on 2015-1-20 上午11:22:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.common.entity;

import java.util.List;
import java.util.Map;

import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class MedicineAllCHISSearchModule extends AbstractSearchModule {
	/**
	 * 公卫用实现药品查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		try {
			String hql = "select DISTINCT new chis.source.common.entity.Medicines(a.YPXH,a.YPMC,a.YPGG,a.YPDW,a.YPJL,a.JLDW,a.YCJL) from YK_TYPK a,YK_YPBM b where a.YPXH=b.YPXH  and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%' order by length(a.YPMC),a.YPXH";
			String hql_count = "select count(DISTINCT a.YPXH) from YK_TYPK a,YK_YPBM b where a.YPXH=b.YPXH and a.ZFPB=0 and b.BMFL=1 and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";
			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Medicines> Medicines = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < Medicines.size(); i++) {
				Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
