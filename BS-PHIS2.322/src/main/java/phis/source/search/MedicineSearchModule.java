package phis.source.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.bean.Medicines;
import phis.source.utils.JSONUtil;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class MedicineSearchModule extends AbstractSearchModule {
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
		String type = req.get("type") == null ? "1" : req.get("type").toString();
		Object yfsb = req.get("pharmacyId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid=user.getUserId();
		String manageUnit = user.getManageUnit().getId();
		// yfsb = "1";//需去掉
		if (yfsb == null || yfsb.equals("null")) {
			res.put("code", "501");
			res.put("msg", "请先设置发药药房信息!");
			return;
		}
		if (containsChinese(searchText)) {
			SEARCH_TYPE = "YPMC";
		}
		try {
			String zbmjgid="";
			Dictionary njjb;
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(manageUnit).getProperty("zbmjgid")+"";
			//update by caijy at 2014-9-29 for 自备药另外查询Sql
			Long count =0l;
			List<Medicines> Medicines = new ArrayList<Medicines>();
			if(req.containsKey("ZFYP")&&Integer.parseInt(req.get("ZFYP").toString())==1){
				String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,a.YFGG,a.YPDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.YFBZ,round(b.LSJG,4),b.YPCD,f.CDMC,a.TYPE,a.TSYP,a.YFDW,a.YBFL,a.JYLX,0.0,a.ZBLB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP) from YK_TYPK a,YK_YPBM c,YK_CDDZ f,YK_YPCD b where a.ZFYP=1 and ( a.YPXH = b.YPXH ) AND ( c.YPXH = a.YPXH )  AND ( a.ZFPB = 0 )  AND (b.YPCD=f.YPCD)  AND c."
						+ SEARCH_TYPE
						+ " LIKE :Search and a.TYPE=:type  group by a.YPXH,a.YPMC,a.YFGG,a.YPDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,a.YFBZ,b.LSJG,b.YPCD,f.CDMC,a.TYPE,a.TSYP,a.YFDW,a.YBFL,a.JYLX,a.ZBLB,a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP, a.ZFYP";
				String hql_count = "select count(*) as total from (select DISTINCT a.YPXH,b.YPCD from YK_TYPK a,YK_YPBM c,YK_CDDZ f,YK_YPCD b where ( a.YPXH = b.YPXH ) AND ( c.YPXH = a.YPXH )  AND ( a.ZFPB = 0 )  AND (b.YPCD=f.YPCD) AND c."
						+ SEARCH_TYPE + " LIKE :Search and a.TYPE=:type)";
				count = Long.parseLong(ss.createSQLQuery(hql_count)
						.setString("Search", searchText + "%").setInteger("type", Integer.parseInt(type)).uniqueResult()
						.toString());
				if(count>0){
					Medicines = ss.createQuery(hql)
							.setString("Search", searchText + "%")
							.setInteger("type", Integer.parseInt(type))
							.setFirstResult(Integer.parseInt(strStart))
							.setMaxResults(Integer.parseInt(strLimit)).list();
				}
			}else{
				String hql = "select DISTINCT new phis.source.bean.Medicines(a.YPXH,a.YPMC,b.YFGG," +
						" a.YPDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL,a.GYFF,b.YFBZ,round(d.LSJG,4),d.YPCD,f.CDMC," +
						" a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,sum(d.YPSL),a.ZBLB,a.KSBZ,a.YCYL,a.KSSDJ," +
						" a.YQSYFS,a.SFSP,a.ZFYP,h.YYZBM) from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g,YK_CDXX h " +
						" where ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) " +
						" AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( c.BMFL = 1 ) " +
						" AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND (d.YPCD=h.YPCD) AND (d.YPXH=h.YPXH) AND (h.JGID='"+zbmjgid+"') AND  b.YFSB=:YFSB" +
						" AND g.CFLX=:CFLX And d.JGID=:JGID AND g.JGID=:JGID AND c."
						+ SEARCH_TYPE
						+ " LIKE :Search group by a.YPXH,a.YPMC,b.YFGG,a.YPDW,a.PSPB,a.JLDW,a.YPJL,a.YCJL," +
						" a.GYFF,b.YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.ZBLB," +
						" a.KSBZ,a.YCYL,a.KSSDJ,a.YQSYFS,a.SFSP,a.ZFYP,h.YYZBM ";
				String hql_count = "select count(*) as total from (select DISTINCT a.YPXH,d.YPCD from YK_TYPK a," +
						" YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g where ( g.YPXH = a.YPXH )" +
						" AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND" +
						" ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( c.BMFL = 1 ) AND ( b.YFZF = 0 )" +
						" AND( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND b.YFSB=:YFSB AND g.CFLX=:CFLX And d.JGID=:JGID" +
						" AND g.JGID=:JGID AND c."
						+ SEARCH_TYPE+" LIKE :Search )";
				count = Long.parseLong(ss.createSQLQuery(hql_count)
						.setString("JGID", manageUnit)
						.setLong("YFSB", Long.parseLong(yfsb.toString()))
						.setInteger("CFLX", Integer.parseInt(type))
						.setString("Search", searchText + "%").uniqueResult()
						.toString());
				if(count>0){
					Medicines = ss.createQuery(hql).setString("JGID", manageUnit)
							.setLong("YFSB", Long.parseLong(yfsb.toString()))
							.setString("Search", searchText + "%")
							.setInteger("CFLX", Integer.parseInt(type))
							.setFirstResult(Integer.parseInt(strStart))
							.setMaxResults(Integer.parseInt(strLimit)).list();
				}
			
			}
			if (count > 0) {
				Dictionary dic = DictionaryController.instance().get(
						"phis.dictionary.drugMode");
				int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil
						.getParameter(manageUnit,
								BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
				double KCDJTS= MedicineUtils.parseDouble(ParameterUtil
						.getParameter(UserRoleToken.getCurrent().getManageUnit().getId(), BSPHISSystemArgument.KCDJTS, ctx));
				StringBuffer hql_djsl = new StringBuffer();
				hql_djsl.append("select sum(YPSL) as DJSL from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ <=:kcdjts ");
				for (int i = 0; i < Medicines.size(); i++) {
					// System.out.println(Medicines.get(i).getZBLB());
					// 库存冻结代码
					if (SFQYYFYFY == 1) {
						// 先删除过期的冻结库存
//						Session s = (Session) ctx.get(Context.DB_SESSION);
//						s.beginTransaction();
//						MedicineCommonModel model = new MedicineCommonModel(
//								new BaseDAO(ctx));
//						model.deleteKCDJ(manageUnit, ctx);
//						s.getTransaction().commit();
						double djsl = MedicineUtils.parseDouble(ss
								.createSQLQuery(hql_djsl.toString())
								.setLong("ypxh", Medicines.get(i).getYPXH())
								.setLong("ypcd", Medicines.get(i).getYPCD())
								.setLong("yfsb",
										Long.parseLong(yfsb.toString())).setDouble("kcdjts", KCDJTS)
								.uniqueResult());
						double sykc = Medicines.get(i).getKCSL() - djsl;
						if (sykc < 0) {
							sykc = 0;
						}
						Medicines.get(i).setKCSL(sykc);
					}
					// 库存冻结代码结束
					Medicines.get(i).setNumKey((i + 1 == 10) ? 0 : i + 1);
					Integer ybfl = Medicines.get(i).getYBFL();
					String yBFL_text = "";
					if (ybfl == 1) {
						yBFL_text = "甲";
					} else if (ybfl == 2) {
						yBFL_text = "乙";
					} else if (ybfl == 2) {
						yBFL_text = "丙";
					}
					if(Medicines.get(i).getYYZBM() != null && !(Medicines.get(i).getYYZBM()).equals("")){
						Medicines.get(i).setYYZBM("医保可报销");
					}
					if (Medicines.get(i).getYFGG() == null) {
						Medicines.get(i).setYFGG("");
					}
					if (Medicines.get(i).getGYFF() != null) {
						String gYFF_text = dic.getText(Medicines.get(i)
								.getGYFF() + "");
						Medicines.get(i).setGYFF_text(gYFF_text);
					}
					Medicines.get(i).setYBFL_text(yBFL_text);
					if (i >= 9)
						break;
				}
			}
			res.put("count", count);
			res.put("mds", JSONUtil.ConvertObjToMapList(Medicines));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
