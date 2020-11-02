package phis.source.search;

import java.util.List;
import java.util.Map;

import phis.source.bean.Clinic;
import phis.source.utils.JSONUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class ClinicSearchModule extends AbstractSearchModule {
	/**
	 * 实现诊疗查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String syfs = (String) req.get("useType");// MZSY ZYSY
		String strStart = (String) req.get("start").toString();// 分页用
		String strLimit = (String) req.get("limit").toString();//
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if (syfs == null || syfs.trim().equals("")) {
			syfs = "MZSY";
		}
		if (containsChinese(searchText)) {
			SEARCH_TYPE = "FYMC";
		}
		try {
			String zbmjgid="";
			Dictionary njjb;
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			// 项目类型XMLX的nvl写法DB2下报错
			String hql = "select distinct new phis.source.bean.Clinic(a.FYXH,a.FYMC,a.FYDW,a.BZJG,a.XMLX,round(c.FYDJ,2),a.FYGB,c.FYKS,0,a.YJSY,a.JCDL,d.YYZBM) from GY_YLSF a,GY_FYBM b,GY_YLMX c,GY_YLMX d where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0 and a.FYXH=d.FYXH and d.ZFPB=0  and a.ZFPB=0 and c.JGID=:JGID and d.JGID='"+zbmjgid+"' and a."
					+ syfs
					+ "=1 and b."
					+ SEARCH_TYPE
					+ " LIKE :Search order by length(a.FYMC),a.FYXH ASC";
			String hql_count = "select count(a.FYXH) from GY_YLSF a,GY_FYBM b,GY_YLMX c where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and a."
					+ syfs
					+ "=1 and c.JGID=:JGID and b."
					+ SEARCH_TYPE
					+ " LIKE :Search";
			Long count = Long.parseLong(ss.createSQLQuery(hql_count)
					.setString("JGID", jgid)
					.setString("Search", searchText + "%").uniqueResult().toString());
			List<Clinic> clinic = ss.createQuery(hql).setString("JGID", jgid)
					.setString("Search", searchText + "%")
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < clinic.size(); i++) {
				clinic.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				clinic.get(i).setFYKS_text(
						clinic.get(i).getFYKS() == null ? ""
								: DictionaryController
										.instance()
										.get("phis.dictionary.department")
										.getText(
												clinic.get(i).getFYKS()
														.toString()));

				if(clinic.get(i).getYYZBM() != null && !(clinic.get(i).getYYZBM()).equals("")){
					clinic.get(i).setYYZBM("医保可报销");
				}
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
