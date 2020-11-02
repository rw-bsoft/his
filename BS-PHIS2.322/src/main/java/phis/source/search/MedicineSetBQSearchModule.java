package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.bean.Clinic;
import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class MedicineSetBQSearchModule extends AbstractSearchModule {
	protected Logger logger = LoggerFactory
			.getLogger(MedicineSetBQSearchModule.class);

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String drugType = req.get("drugType")==null ? "1":req.get("drugType").toString();// 药品类别
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String zbmjgid="";
		Dictionary njjb;
		try {
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (drugType.equals("5")) {//add by LIZHI 2017-12-01增加文字组套
			List<Medicines> dataList = new ArrayList<Medicines>();
			Medicines wenziOrder = new Medicines();
			wenziOrder.setYPMC(searchText.replace("%", ""));
			wenziOrder.setYCJL(1.0);
			dataList.clear();
			dataList.add(wenziOrder);
			res.put("count", 1);
			res.put("mds", JSONUtil.ConvertObjToMapList(dataList));
		}else if (drugType.equals("4")) {
			String hql = "select distinct new phis.source.bean.Clinic(a.FYXH,a.FYMC,a.FYDW,a.BZJG,a.XMLX,c.FYDJ,a.FYGB,c.FYKS,0,nvl(a.XMLX,99),a.JCDL,'zt',nvl2(d.YYZBM, '医保', '自费')) from GY_YLSF a,GY_FYBM b,GY_YLMX c,GY_YLMX d where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0 and a.FYXH=d.FYXH and d.ZFPB=0  and a.ZFPB=0 and c.JGID='"
					+ jgid +"' and d.JGID='"+ zbmjgid
					+ "' and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%' order by a.FYXH ASC";
			String hql_count = "select count(*) from GY_YLSF a,GY_FYBM b,GY_YLMX c where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and c.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE
					+ " LIKE '"
					+ searchText
					+ "%'";
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
			res.put("mds", JSONUtil.ConvertObjToMapList(clinic));
		}else if(drugType.equals("1")){//add by LIZHI 2017-12-01 西药组套可维护成药
			String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.YPGG,a.YPDW,a.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.YFBZ) from YK_TYPK a,YK_YPBM b,YK_YPXX c  "
					+ "where b.YPXH = a.YPXH and b.BMFL = 1 and c.YKZF = 0 and c.YPXH = a.YPXH and a.ZFPB = 0 and a.TYPE in (1,2)"
					+ " and c.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";
			String hql_count = "select count(DISTINCT a.YPXH) from YK_TYPK a,YK_YPBM b,YK_YPXX c  "
					+ "where b.YPXH = a.YPXH and b.BMFL = 1 and c.YKZF = 0 and c.YPXH = a.YPXH and a.ZFPB = 0 and a.TYPE in (1,2)"
					+ " and c.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";

			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Medicines> Medicines = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();

			Dictionary dic=null;
			try {
				dic = DictionaryController.instance().get("phis.dictionary.drugMode");
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < Medicines.size(); i++) {
				Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				String gYFF_text = dic.getText(Medicines.get(i).getGYFF() + "");
				Medicines.get(i).setGYFF_text(gYFF_text);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
		} else {
			String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.YPGG,a.YPDW,a.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.YFBZ) from YK_TYPK a,YK_YPBM b,YK_YPXX c  "
					+ "where b.YPXH = a.YPXH and b.BMFL = 1 and c.YKZF = 0 and c.YPXH = a.YPXH and a.ZFPB = 0 and a.TYPE="
					+ drugType
					+ " and c.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";
			String hql_count = "select count(DISTINCT a.YPXH) from YK_TYPK a,YK_YPBM b,YK_YPXX c  "
					+ "where b.YPXH = a.YPXH and b.BMFL = 1 and c.YKZF = 0 and c.YPXH = a.YPXH and a.ZFPB = 0 and a.TYPE="
					+ drugType
					+ " and c.JGID='"
					+ jgid
					+ "' and b."
					+ SEARCH_TYPE + " LIKE '" + searchText + "%'";

			Long count = (Long) ss.createQuery(hql_count).uniqueResult();
			List<Medicines> Medicines = ss.createQuery(hql)
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();

			Dictionary dic=null;
			try {
				dic = DictionaryController.instance().get("phis.dictionary.drugMode");
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < Medicines.size(); i++) {
				Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				String gYFF_text = dic.getText(Medicines.get(i).getGYFF() + "");
				Medicines.get(i).setGYFF_text(gYFF_text);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
		}

	}

}
