package phis.source.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class MedicineFEESearchModule extends AbstractSearchModule {
	/**
	 * 实现药品查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = MATCH_TYPE
				+ req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		String type = req.get("type") == null ? "1" : req.get("type")
				.toString();
		Object yfsb = req.get("pharmacyId");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		BaseDAO dao = new BaseDAO();
		// manageUnit = "440402001001";//需去掉
		// yfsb = "1";//需去掉
		if (yfsb == null) {
			return;
		}

		try {
			String zbmjgid="";
			Dictionary njjb;
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(manageUnit).getProperty("zbmjgid")+"";
			List<Map<String, Object>> FYXX = new ArrayList<Map<String, Object>>();
			String SEARCH_TYPE_Set = SEARCH_TYPE;
			//yx 2017-05-07 修改根据输入指的开始符.来判断怎么查询数据
			String searchypflag="1";
			if((".").equals(searchText.substring(1, 2)) ){
				searchypflag="0";
			}
			if (searchypflag.equals("1")) {
				if(type.equals("0")){
					type="1";
				}
				if (containsChinese(searchText)) {
					SEARCH_TYPE = "YPMC";
					SEARCH_TYPE_Set = "ZTMC";
				}
				String hql = "select * from (select a.YPXH,a.YPMC,b.YFGG,a.YPDW,a.PSPB,a.JLDW,a.YPJL," +
						" a.GYFF,b.YFBZ,round(d.LSJG,4) as LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW," +
						" a.YBFL,a.JYLX,sum(d.YPSL) as YPSL,a.ZBLB as FYGB,nvl(h.MCSX,h.SFMC) as GBMC," +
						" 0 as isZT,a.NHBM_BSOFT as NHBM_BSOFT,i.YYZBM as YYZBM from YK_TYPK a,YF_YPXX b,YK_YPBM c," +
						" YF_KCMX d,YK_CDDZ f,YK_YPXX g,GY_SFXM h,YK_CDXX i where h.SFXM = a.ZBLB and (g.YPXH = a.YPXH)" +
						" AND (c.YPXH=a.YPXH) AND (a.YPXH =d.YPXH) AND (b.YPXH =a.YPXH) AND " +
						" (b.YFSB=d.YFSB) AND (a.ZFPB=0) AND (c.BMFL =1) AND (b.YFZF =0) AND " +
						" (d.JYBZ=0) AND (d.YPCD=f.YPCD) AND (d.YPCD=i.YPCD) AND (d.YPXH=i.YPXH) AND (i.JGID='"+zbmjgid+"') AND  b.YFSB=:YFSB AND g.CFLX=:CFLX And " +
						" d.JGID=:JGID AND g.JGID=:JGID AND c.";
				hql += SEARCH_TYPE;
				hql += " LIKE :Search group by a.YPXH,a.YPMC,b.YFGG,a.YPDW,a.PSPB,a.JLDW,a.YPJL,a.GYFF," +
						" b.YFBZ,d.LSJG,d.YPCD,f.CDMC,a.TYPE,a.TSYP,b.YFDW,a.YBFL,a.JYLX,a.ZBLB,h.MCSX," +
						" h.SFMC,a.NHBM_BSOFT,i.YYZBM ";
				hql += " union all ";
				hql +="select a.ZTBH as YPXH,'(组套)'||a.ZTMC as YPMC,'' as YFGG,'' as YPDW,0 as PSPB,'' as JLDW," +
					   " 0 as YPJL,0 as GYFF,0 as YFBZ,0 as LSJG,0 as YPCD,'' as CDMC,"
						+ type
						+ " as TYPE,0 as TSYP,'' as YFDW,0 as YBFL,0 as JYLX,0 as YPSL,0 as FYGB,'' as GBMC," +
						" 1 as isZT,'' as NHBM_BSOFT,'' as YYZBM from ";
				hql += "YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB=:CFLX AND a.";
				hql += SEARCH_TYPE_Set;
				hql += " LIKE :Search AND a.JGID = :JGID) order by ISZT desc,length(YPMC), YPMC";
				String hql_count = "select count(*) as total from (select DISTINCT a.YPXH,d.YPCD from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g,GY_SFXM h where h.SFXM = a.ZBLB and ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( c.BMFL = 1 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND  b.YFSB=:YFSB AND g.CFLX=:CFLX And d.JGID=:JGID AND g.JGID=:JGID AND c."
						+ SEARCH_TYPE + " LIKE :Search";
				hql_count += " union all ";
				hql_count += "select a.ZTBH as YPXH,0 as YPCD from YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB=:CFLX AND a.";
				hql_count += SEARCH_TYPE_Set;
				hql_count += " LIKE :Search AND a.JGID = :JGID)";
				Long count = Long.parseLong(ss.createSQLQuery(hql_count)
						.setString("JGID", manageUnit)
						.setLong("YFSB", Long.parseLong(yfsb.toString()))
						.setInteger("CFLX", Integer.parseInt(type))
						.setString("Search", searchText + "%").uniqueResult()
						.toString());
				if (count > 0) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JGID", manageUnit);
					parameters.put("YFSB", Long.parseLong(yfsb.toString()));
					parameters.put("Search", searchText + "%");
					parameters.put("CFLX", Integer.parseInt(type));
					parameters.put("first", Integer.parseInt(strStart));
					parameters.put("max", Integer.parseInt(strLimit));
					FYXX = dao.doSqlQuery(hql, parameters);
					Dictionary dic = DictionaryController.instance().get(
							"phis.dictionary.drugMode");
					int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil
							.getParameter(manageUnit,
									BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
					double KCDJTS = MedicineUtils.parseDouble(ParameterUtil
							.getParameter(UserRoleToken.getCurrent()
									.getManageUnit().getId(),
									BSPHISSystemArgument.KCDJTS, ctx));
					StringBuffer hql_djsl = new StringBuffer();
					hql_djsl.append("select sum(YPSL) as DJSL from YF_KCDJ where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ <=:kcdjts ");
					for (int i = 0; i < FYXX.size(); i++) {
						if (SFQYYFYFY == 1) {
							// 先删除过期的冻结库存
							// Session s = (Session)
							// ctx.get(Context.DB_SESSION);
							// s.beginTransaction();
							// MedicineCommonModel model = new
							// MedicineCommonModel(
							// new BaseDAO(ctx));
							// model.deleteKCDJ(manageUnit, ctx);
							// s.getTransaction().commit();
							Map<String, Object> YPSLparameters = new HashMap<String, Object>();
							YPSLparameters.put("ypxh", Long.parseLong(FYXX.get(
									i).get("YPXH")
									+ ""));
							YPSLparameters.put("ypcd", Long.parseLong(FYXX.get(
									i).get("YPCD")
									+ ""));
							YPSLparameters.put("yfsb",
									Long.parseLong(yfsb.toString()));
							YPSLparameters.put("kcdjts", KCDJTS);
							List<Map<String, Object>> YPSL_list = dao
									.doSqlQuery(hql_djsl.toString(),
											YPSLparameters);
							double djsl = 0;
							if (YPSL_list.get(0).get("DJSL") != null) {
								djsl = Long.parseLong(YPSL_list.get(0).get(
										"DJSL")
										+ "");
							}
							double sykc = Double.parseDouble(FYXX.get(i).get(
									"YPSL")
									+ "")
									- djsl;
							if (sykc < 0) {
								sykc = 0;
							}
							FYXX.get(i).put("KCSL", sykc);
						}
						FYXX.get(i).put("numKey", ((i + 1 == 10) ? 0 : i + 1));
						Integer ybfl = Integer.parseInt(FYXX.get(i).get("YBFL")
								+ "");
						String yBFL_text = "";
						if (ybfl == 1) {
							yBFL_text = "甲";
						} else if (ybfl == 2) {
							yBFL_text = "乙";
						} else if (ybfl == 2) {
							yBFL_text = "丙";
						}
						if (FYXX.get(i).get("GYFF") != null) {
							String gYFF_text = dic.getText(FYXX.get(i).get("GYFF")+ "");
							FYXX.get(i).put("GYFF_text", gYFF_text);
						}
						FYXX.get(i).put("YBFL_text", yBFL_text);
						if (yBFL_text.length() > 0) {
							FYXX.get(i).put("YPMC","(" + yBFL_text + ")"+ FYXX.get(i).get("YPMC"));
						}
						if (FYXX.get(i).get("YFGG") == null) {
							FYXX.get(i).put("YFGG", "");
						}
						if (FYXX.get(i).get("YFDW") == null) {
							FYXX.get(i).put("YFDW", "");
						}
						if (FYXX.get(i).get("CDMC") == null) {
							FYXX.get(i).put("CDMC", "");
						}
						if ("1".equals(FYXX.get(i).get("ISZT") + "")) {
							FYXX.get(i).put("LSJG", "");
							FYXX.get(i).put("KCSL", "");
						}
						if(FYXX.get(i).get("YYZBM") != null && !(FYXX.get(i).get("YYZBM")+"").equals("")){
							FYXX.get(i).put("YYZBM", "医保可报销");
						}else{
							FYXX.get(i).put("YYZBM", "");
						}
						if (i >= 9)
							break;
					}
				}
				res.put("count", count);
				res.put("mds", FYXX);
			} else {
//				if(type.equals("1")|| searchText.indexOf(".")<0 || !(".").equals(searchText.substring(1, 2))){
//					res.put("count", 0);
//					res.put("mds", FYXX);
//					return;
//				}
				searchText = "%"+searchText.substring(2);
				if (containsChinese(searchText)) {
					SEARCH_TYPE = "FYMC";
					SEARCH_TYPE_Set = "ZTMC";
				}
				// 项目类型XMLX的nvl写法DB2下报错
				String hql = "select * from (select a.FYXH as YPXH,a.FYMC as YPMC,a.FYDW as YFDW,'0' as TYPE," +
						" a.BZJG,a.XMLX,round(c.FYDJ,2) as LSJG,a.FYGB as FYGB,nvl(d.MCSX,d.SFMC) as GBMC," +
						" c.FYKS,0 as isZT,a.NHBM_BSOFT as NHBM_BSOFT,e.YYZBM as YYZBM from GY_YLSF a,GY_FYBM b,GY_YLMX c,GY_YLMX e," +
						" GY_SFXM d where d.SFXM = a.FYGB and a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0 and a.FYXH=e.FYXH and e.ZFPB=0 " +
						" and a.ZFPB=0 and c.JGID=:JGID and e.JGID='"+zbmjgid+"' and a.MZSY=1 and b."
						+ SEARCH_TYPE + " LIKE :Search ";
				hql += " union all ";
				hql += "select a.ZTBH as YPXH,'(组套)'||a.ZTMC as YPMC,'' as YFDW,'0' as TYPE,0 as BZJG,0 as XMLX,0 as LSJG,0 as FYGB,'' as GBMC,0 as FYKS,1 as isZT,'' as NHBM_BSOFT,'' as YYZBM from ";
				hql += "YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB=4 AND a.";
				hql += SEARCH_TYPE_Set;
				hql += " LIKE :Search AND a.JGID = :JGID) order by ISZT desc,length(YPMC), YPMC";
				String hql_count = "select count(*) as total from (select a.FYXH from GY_YLSF a,GY_FYBM b,GY_YLMX c,GY_SFXM d where d.SFXM = a.FYGB and a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and c.JGID=:JGID and a.MZSY=1 and b."
						+ SEARCH_TYPE + " LIKE :Search";
				hql_count += " union all ";
				hql_count += "select a.ZTBH as FYXH from YS_MZ_ZT01 a where a.SFQY=1 and a.SSLB=3 and a.ZTLB=4 AND a.";
				hql_count += SEARCH_TYPE_Set;
				hql_count += " LIKE :Search AND a.JGID = :JGID)";
				Long count = Long.parseLong(ss.createSQLQuery(hql_count)
						.setString("JGID", manageUnit)
						.setString("Search", searchText + "%").uniqueResult()
						.toString());
				if (count > 0) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JGID", manageUnit);
					parameters.put("Search", searchText + "%");
					parameters.put("first", Integer.parseInt(strStart));
					parameters.put("max", Integer.parseInt(strLimit));
					FYXX = dao.doSqlQuery(hql, parameters);
					// ss.createQuery(hql).setString("JGID", manageUnit)
					// .setString("Search", searchText + "%")
					// .setFirstResult(Integer.parseInt(strStart))
					// .setMaxResults(Integer.parseInt(strLimit)).list();
					for (int i = 0; i < FYXX.size(); i++) {
						if ("1".equals(FYXX.get(i).get("ISZT") + "")) {
							FYXX.get(i).put("LSJG", "");
						}
						FYXX.get(i).put("numKey", ((i + 1 == 10) ? 0 : i + 1));
						FYXX.get(i).put("YBFL_text", "检");
						FYXX.get(i).put("FYKS_text",FYXX.get(i).get("FYKS") == null ? "": DictionaryController
														.instance().get("phis.dictionary.department").getText(
																FYXX.get(i).get("FYKS").toString()));
						if(FYXX.get(i).get("YYZBM") != null && !(FYXX.get(i).get("YYZBM")+"").equals("")){
							FYXX.get(i).put("YYZBM", "医保可报销");
						}else{
							FYXX.get(i).put("YYZBM", "");
						}
						if (i >= 9)
							break;
					}
				}
				res.put("count", count);
				res.put("mds", FYXX);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
