package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISSystemArgument;
import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.TableDictionary;
import ctd.util.context.Context;

public class MedicineAmqcSearchModule extends AbstractSearchModule {

	protected Logger logger = LoggerFactory
			.getLogger(MedicineAmqcSearchModule.class);

	/**
	 * 抗菌药物查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String wardId = req.get("wardId") + "";
		String openBy = req.get("openBy") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		if (wardId == null || wardId == "" || "null".equals(wardId)) {
			wardId = (String) user.getProperty("wardId");
		}
		// 判断用户输入的是否是中文，若是，查询字段改为中文名称对应的字段
		if (containsChinese(searchText)) {
			SEARCH_TYPE = "YPMC";
		}
		searchText = MATCH_TYPE + searchText;
		// 根据包装类型区分查询的对象
		String bzlx = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.YZLR_BZLX, ctx);
		String QYZYKJYWSP = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.QYZYKJYWSP, ctx);
		if (!"1".equals(QYZYKJYWSP))// 不启用住院审批的情况下
			return;
		String hql = null;
		String hql_count = null;
		Long count = 0l;
		List<Medicines> Medicines = new ArrayList<Medicines>();
		if ("2".equals(bzlx)) {

			hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.BFGG,a.BFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.BFBZ as YFBZ,round((d.LSJG/b.YFBZ)*a.BFBZ,4) as LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,a.BFDW as YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,sum(d.YPSL) as YPSL,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) and a.KSBZ=1 and a.SFSP=1 "
					+ "And d.JGID=:JGID AND g.JGID=:JGID AND c."
					+ SEARCH_TYPE
					+ " LIKE :Search group by a.YPXH,a.YPMC,a.BFGG,a.BFDW,a.PSPB,a.JLDW,a.YPJL,a.GYFF,a.BFBZ as YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,a.BFDW as YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP";
			hql_count = "select count(*) as total from (select a.YPXH,d.YPCD from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( c.BMFL = 1 ) AND ( b.YFZF = 0 ) AND( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) and a.KSBZ=1 and a.SFSP=1 "
					+ " And d.JGID=:JGID AND g.JGID=:JGID AND c."
					+ SEARCH_TYPE
					+ " LIKE :Search)";
			count = Long.parseLong(ss.createSQLQuery(hql_count)
					.setString("JGID", jgid)
					.setString("Search", searchText + "%").uniqueResult()
					.toString());
			Medicines = ss.createQuery(hql).setString("JGID", jgid)
					.setString("Search", searchText + "%")
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
		} else {
			List<Long> yfsbs = new ArrayList<Long>();
			if (openBy.equals("fsb")) {
				List<Object> yfsbList = ss
						.createSQLQuery(
								"select distinct YFSB from JC_FYYF where JGID =:JGID")
						.setString("JGID", jgid).list();
				for (Object yfsb : yfsbList) {
					yfsbs.add(Long.parseLong(yfsb.toString()));
				}
			} else {
				List<Object> yfsbList = ss
						.createSQLQuery(
								"select distinct YFSB from BQ_FYYF where BQDM=:BQDM and JGID =:JGID")
						.setString("JGID", jgid)
						.setLong("BQDM", Long.parseLong(wardId)).list();
				for (Object yfsb : yfsbList) {
					yfsbs.add(Long.parseLong(yfsb.toString()));
				}
			}

			hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,b.YFBZ,round(d.LSJG,4),d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,sum(d.YPSL) as YPSL,b.YFSB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 )  AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "
					+ " and b.YFSB in (:YFSB) And d.JGID=:JGID  AND g.JGID=:JGID and a.KSBZ=1 and a.SFSP=1 AND  c."
					+ SEARCH_TYPE
					+ " LIKE :Search group by a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,b.YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.TYPE,a.FYFS,a.ZXBZ,a.YPGG,b.YFSB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP";
			hql_count = "select count(*) as total from (select DISTINCT a.YPXH,a.YPMC,b.YFGG,b.YFDW,a.PSPB,a.JLDW,a.YPJL,a.GYFF,b.YFBZ,round(d.LSJG,4),d.YPCD,f.CDMC from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g where a.TYPE<>3 and ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 )  AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) "
					+ " and b.YFSB in (:YFSB) And d.JGID=:JGID AND g.JGID=:JGID and a.KSBZ=1 and a.SFSP=1 AND c."
					+ SEARCH_TYPE + " LIKE :Search )";
			count = Long.parseLong(ss.createSQLQuery(hql_count)
					.setString("JGID", jgid).setParameterList("YFSB", yfsbs)
					.setString("Search", searchText + "%").uniqueResult()
					.toString());
			Medicines = ss.createQuery(hql).setString("JGID", jgid)
					.setParameterList("YFSB", yfsbs)
					.setString("Search", searchText + "%")
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit)).list();
			try {
				List<DictionaryItem> drugWay = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.drugWay"))
						.initAllItems();
				List<DictionaryItem> hairMedicineWay = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.hairMedicineWay"))
						.initAllItems();
				List<DictionaryItem> wardPharmacy = ((TableDictionary) DictionaryController
						.instance().get("phis.dictionary.wardPharmacy"))
						.initAllItems();
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
						for (DictionaryItem d : drugWay) {
							if (d.getKey().equals(
									Medicines.get(i).getGYFF() + "")) {
								Medicines.get(i).setGYFF_text(d.getText());
							}
						}
					}
					for (DictionaryItem d : hairMedicineWay) {
						if (d.getKey().equals(Medicines.get(i).getFYFS() + "")) {
							Medicines.get(i).setFYFS_text(d.getText());
						}
					}
					for (DictionaryItem d : wardPharmacy) {
						if (d.getKey().equals(Medicines.get(i).getYFSB() + "")) {
							Medicines.get(i).setYFSB_text(d.getText());
						}
					}
					Medicines.get(i).setYBFL_text(yBFL_text);
					if (i >= 9)
						break;
				}
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
			// System.out.println(res);
		}

	}

}
