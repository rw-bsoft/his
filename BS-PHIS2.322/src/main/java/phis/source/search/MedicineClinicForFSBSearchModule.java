package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISSystemArgument;
import phis.source.bean.Clinic;
import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.support.TableDictionary;
import ctd.util.context.Context;

public class MedicineClinicForFSBSearchModule extends AbstractSearchModule {

	protected Logger logger = LoggerFactory
			.getLogger(MedicineClinicForFSBSearchModule.class);

	/**
	 * 病人性质实现药品查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String wardId = req.get("wardId") + "";
		String yplx = req.get("TYPE") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		if (wardId == null || wardId == "" || "null".equals(wardId)) {
			wardId = (String) user.getProperty("wardId");
		}
		if (searchText.startsWith(".") || "0".equals(yplx)) {
			// 判断用户输入的是否是中文，若是，查询字段改为中文名称对应的字段
			if (containsChinese(searchText)) {
				SEARCH_TYPE = "FYMC";
			}
			searchText = searchText.startsWith(".") ? searchText.substring(1)
					: searchText;
			searchText = MATCH_TYPE + searchText;
			String hql = "select distinct new phis.source.bean.Clinic(a.FYXH,a.FYMC,a.FYDW,a.BZJG,a.XMLX,c.FYDJ,a.FYGB,c.FYKS,0,a.YJSY) from GY_YLSF a,GY_FYBM b,GY_YLMX c where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and a.ZYSY=1 and c.JGID=:JGID and b."
					+ SEARCH_TYPE + " LIKE :Search order by a.FYXH ASC";
			String hql_count = "select count(*) from GY_YLSF a,GY_FYBM b,GY_YLMX c where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and a.ZYSY=1 and c.JGID=:JGID and b."
					+ SEARCH_TYPE + " LIKE :Search";
			Long count = (Long) ss.createQuery(hql_count)
					.setString("JGID", jgid)
					.setString("Search", searchText + "%").uniqueResult();
			List<Clinic> clinic = ss.createQuery(hql).setString("JGID", jgid)
					.setString("Search", searchText + "%")
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			for (int i = 0; i < clinic.size(); i++) {
				clinic.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
				clinic.get(i).setYZMC(clinic.get(i).getFYMC());
				if (clinic.get(i).getFYDW() == null) {
					clinic.get(i).setFYDW("");
				}
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(clinic));
		} else {
			// 判断用户输入的是否是中文，若是，查询字段改为中文名称对应的字段
			if (containsChinese(searchText)) {
				SEARCH_TYPE = "YPMC";
			}
			searchText = MATCH_TYPE + searchText;
			// 根据包装类型区分查询的对象
			String bzlx = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.YZLR_BZLX, ctx);
			String hql = null;
			String hql_count = null;
			Long count = 0l;
			List<Medicines> Medicines = new ArrayList<Medicines>();
			// update by caijy at 2014-9-29 for 自备药另外查询Sql
			if (req.containsKey("ZFYP")
					&& Integer.parseInt(req.get("ZFYP").toString()) == 1) {
				hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.BFGG,a.BFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.BFBZ as YFBZ,b.LSJG as LSJG,b.YPCD,f.CDMC,a.TYPE,a.TSYP,a.BFDW as YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,0.0 as YPSL,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YK_CDDZ f,YK_YPCD b where a.TYPE<>3 and a.ZFPB = 0 and a.ZFYP=1 and a.YPXH=b.YPXH and b.YPCD=f.YPCD and a."
						+ SEARCH_TYPE + " LIKE :Search ";
				hql_count = "select count(*) as total from (select a.YPXH,b.YPCD from YK_TYPK a,YK_CDDZ f,YK_YPCD b where a.TYPE<>3 and a.ZFPB = 0 and a.ZFYP=1 and  a.YPXH=b.YPXH and b.YPCD=f.YPCD and a."
						+ SEARCH_TYPE + " LIKE :Search )";
				count = Long.parseLong(ss.createSQLQuery(hql_count)
						.setString("Search", searchText + "%").uniqueResult()
						.toString());
				Medicines = ss.createQuery(hql)
						.setString("Search", searchText + "%")
						.setFirstResult(Integer.parseInt(strStart))
						.setMaxResults(Integer.parseInt(strLimit)).list();
			} else {
				if ("2".equals(bzlx)) {
					hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.BFGG,a.BFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.BFBZ as YFBZ,round((d.LSJG/b.YFBZ)*a.BFBZ,4) as LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,a.BFDW as YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,sum(d.YPSL) as YPSL,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "

							+ "And d.JGID=:JGID AND g.JGID=:JGID AND a."
							+ SEARCH_TYPE
							+ " LIKE :Search group by a.YPXH,a.YPMC,a.BFGG,a.BFDW,a.PSPB,a.JLDW,a.YPJL,a.GYFF,a.BFBZ as YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,a.BFDW as YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP";
					hql_count = "select count(*) as total from (select a.YPXH,d.YPCD from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH )  AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( c.BMFL = 1 ) AND ( b.YFZF = 0 ) AND( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "
							+ " And d.JGID=:JGID AND g.JGID=:JGID AND a."
							+ SEARCH_TYPE + " LIKE :Search)";
					count = Long.parseLong(ss.createSQLQuery(hql_count)
							.setString("JGID", jgid)
							.setString("Search", searchText + "%")
							.uniqueResult().toString());
					Medicines = ss.createQuery(hql).setString("JGID", jgid)
							.setString("Search", searchText + "%")
							.setFirstResult(Integer.parseInt(strStart))
							.setMaxResults(Integer.parseInt(strLimit)).list();
				} else {
					int type = 1;
					if (req.containsKey("YFSZ")) {
						type = Integer.parseInt(req.get("YFSZ") + "");
					}
					List<Object> yfsbList = ss
							.createSQLQuery(
									"select distinct YFSB from JC_FYYF where JGID =:JGID and TYPE=:TYPE")
							.setInteger("TYPE", type).setString("JGID", jgid)
							.list();
					if (yfsbList.size() == 0) {
						res.put("code", "509");
						res.put("msg", "请设置家床发药药房!");
						return;
					}
					List<Long> yfsbs = new ArrayList<Long>();
					for (Object yfsb : yfsbList) {
						yfsbs.add(Long.parseLong(yfsb.toString()));
					}
					hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,b.YFBZ,round(d.LSJG,4),d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,sum(d.YPSL) as YPSL,b.YFSB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH )  AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 )  AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "
							+ " and b.YFSB in (:YFSB) And d.JGID=:JGID  AND g.JGID=:JGID AND a."
							+ SEARCH_TYPE
							+ " LIKE :Search group by a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,b.YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,b.YFSB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP";
					hql_count = "select count(*) as total from (select DISTINCT a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.GYFF,b.YFBZ,round(d.LSJG,4),d.YPCD,f.CDMC from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 )  AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "
							+ " and b.YFSB in (:YFSB) And d.JGID=:JGID AND g.JGID=:JGID AND a."
							+ SEARCH_TYPE + " LIKE :Search )";
					count = Long.parseLong(ss.createSQLQuery(hql_count)
							.setString("JGID", jgid)
							.setParameterList("YFSB", yfsbs)
							.setString("Search", searchText + "%")
							.uniqueResult().toString());
					Medicines = ss.createQuery(hql).setString("JGID", jgid)
							.setParameterList("YFSB", yfsbs)
							.setString("Search", searchText + "%")
							.setFirstResult(Integer.parseInt(strStart))
							.setMaxResults(Integer.parseInt(strLimit)).list();
				}
			}
			try {
				Dictionary drugWay = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.drugWay"));
				Dictionary hairMedicineWay = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.hairMedicineWay"));
				Dictionary wardPharmacy = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.wardPharmacy"));
				for (int i = 0; i < Medicines.size(); i++) {
					Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
					Medicines.get(i).setYZMC(Medicines.get(i).getYPMC());
					Integer ybfl = Medicines.get(i).getYBFL();
					String yBFL_text = "";
					if (ybfl == 1) {
						yBFL_text = "甲";
					} else if (ybfl == 2) {
						yBFL_text = "乙";
					} else if (ybfl == 2) {
						yBFL_text = "丙";
					}

					if (Medicines.get(i).getGYFF() != null) {
						Medicines.get(i)
								.setGYFF_text(
										drugWay.getText(Medicines.get(i)
												.getGYFF() + ""));
					}
					Medicines.get(i).setFYFS_text(
							hairMedicineWay.getText(Medicines.get(i).getFYFS()
									+ ""));
					Medicines.get(i).setYFSB_text(
							wardPharmacy.getText(Medicines.get(i).getYFSB()
									+ ""));
					Medicines.get(i).setYBFL_text(yBFL_text);
					if (i >= 9)
						break;
				}
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
		}

	}

}
