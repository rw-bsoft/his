package phis.source.search;

import java.util.List;
import java.util.Map;

import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;


public class MedicineBasicSearchModule extends AbstractSearchModule {

	/**
	 * 病人性质实现药品查询功能
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		try {
			String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.YPGG,a.YPDW) from YK_TYPK a,YK_YPBM b where a.YPXH=b.YPXH and a.ZFPB=0 and b.BMFL=1 and b."
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
			Dictionary dic = DictionaryController.instance().get("phis.dictionary.drugMode");
			for (int i = 0; i < Medicines.size(); i++) {
				Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				String gYFF_text = dic.getText(Medicines.get(i).getGYFF() + "");
				Medicines.get(i).setGYFF_text(gYFF_text);
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
