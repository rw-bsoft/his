package phis.source.search;

import java.util.List;
import java.util.Map;

import phis.source.BSPHISSystemArgument;
import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class MedicineSetSearchModule extends AbstractSearchModule {
	/**
	 * 实现药品查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String drugType = req.get("drugType") == null ? "1" : req.get(
				"drugType").toString();// 药品类别
		String ksbz = req.get("KSBZ") == null ? "1" : req.get("KSBZ")
				.toString();
		;
		String values = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		// add by yangl 额外增加条件
		String cnd = "";
		if (ksbz != null && ksbz.trim().length() > 0) {
			cnd = " and a.KSBZ=1";
		}
		if ("1".equals(drugType)) {
			values = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.YS_MZ_FYYF_XY, ctx);
		} else if ("2".equals(drugType)) {
			values = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.YS_MZ_FYYF_ZY, ctx);
		} else if ("3".equals(drugType)) {
			values = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.YS_MZ_FYYF_CY, ctx);
		}
		if ("".equals(values) || values == null) {
			values = "0";
		}
		try {
			String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.YFGG,a.YPDW,c.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,c.YFBZ) from YK_TYPK a,YK_YPBM b,YF_YPXX c,YK_YPXX d where a.YPXH=d.YPXH and a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.ZFPB=0 and b.BMFL=1 and c.YFZF=0 and d.CFLX="
					+ drugType
					+ " and c.YFSB="
					+ values
					+ " AND d.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%' " + cnd;
			String hql_count = "select count(DISTINCT a.YPXH) from YK_TYPK a,YK_YPBM b,YF_YPXX c,YK_YPXX d where a.YPXH=d.YPXH and a.YPXH=b.YPXH and a.YPXH=c.YPXH and a.ZFPB=0 and b.BMFL=1 and c.YFZF=0 and d.CFLX="
					+ drugType
					+ " and c.YFSB="
					+ values
					+ " AND d.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%' " + cnd;
			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Medicines> Medicines = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			Dictionary dic = DictionaryController.instance().get(
					"phis.dictionary.drugMode");
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
