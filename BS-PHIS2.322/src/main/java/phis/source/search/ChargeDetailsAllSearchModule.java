package phis.source.search;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class ChargeDetailsAllSearchModule extends AbstractSearchModule {
	/**
	 * 实现费用明细查询功能
	 */
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		String searchText = req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();//
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		BaseDAO dao = new BaseDAO();
		Map<String, Object> parameters = new HashMap<String, Object>();
		DecimalFormat dft = new DecimalFormat("0.0000");
		parameters.put("manageUnit", manageUnit);
		String zbmjgid="";
		Dictionary njjb;
		try {
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(manageUnit).getProperty("zbmjgid")+"";
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (searchText.startsWith(".")) {
			searchText = searchText.substring(searchText.indexOf(".") + 1);
			searchText = MATCH_TYPE + searchText;
			StringBuffer hql = new StringBuffer(
					"select distinct a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.FYGB as FYGB,round(c.FYDJ,4) as FYDJ,c.FYKS as FYKS,d.OFFICENAME as CDMC,decode(e.YYZBM,'',' ','医保可报销') as YYZBM from ");
			hql.append("GY_YLSF");
			hql.append(" a,");
			hql.append("GY_FYBM");
			hql.append(" b,GY_YLMX e,");
			hql.append("GY_YLMX");
			hql.append(" c left outer join ");
			hql.append("SYS_Office");
			hql.append(" d on d.ID = c.FYKS where a.FYXH = b.FYXH AND a.FYXH = c.FYXH AND a.FYXH=e.FYXH AND a.ZYSY = 1 AND a.ZFPB = 0 AND c.ZFPB = 0  AND b.");
			hql.append(SEARCH_TYPE);
			hql.append(" LIKE '");
			hql.append(searchText);
			hql.append("%' AND c.JGID = :manageUnit AND e.JGID='"+zbmjgid+"'");
			try {
				Long count = dao
						.doCount(
								"GY_YLSF a,GY_FYBM b,GY_YLMX c",
								"a.FYXH = b.FYXH AND a.FYXH = c.FYXH AND a.ZYSY = 1 AND a.ZFPB = 0 AND c.ZFPB = 0 AND c.JGID = :manageUnit AND b."
										+ SEARCH_TYPE
										+ " LIKE '"
										+ searchText
										+ "%'", parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> FYXX = dao.doSqlQuery(hql.toString(),
						parameters);
				for (Map<String, Object> map_fyxx : FYXX) {
					map_fyxx.put("FYDJ", dft.format(map_fyxx.get("FYDJ")));
					if ("null".equals(map_fyxx.get("CDMC") + "")) {
						map_fyxx.put("CDMC", "");
					}
					if ("null".equals(map_fyxx.get("FYDW") + "")) {
						map_fyxx.put("FYDW", "");
					}
				}
				res.put("count", count);
				res.put("mds", FYXX);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (searchText.startsWith("/")) {
			searchText = searchText.substring(searchText.indexOf("/") + 1);
			searchText = MATCH_TYPE + searchText;
			StringBuffer hql = new StringBuffer(
					"select distinct a.ZTBH as FYXH,a.ZTMC as FYMC from ");
			hql.append("YS_MZ_ZT01");
			hql.append(" a where a.SFQY=1 and a.SSLB=3 and a.ZTLB=4 AND a.");
			hql.append(SEARCH_TYPE);
			hql.append(" LIKE '");
			hql.append(searchText);
			hql.append("%' AND a.JGID = :manageUnit");
			try {
				Long count = dao.doCount("YS_MZ_ZT01 a",
						"a.SFQY=1 and a.SSLB=3 and a.ZTLB=4 AND a.JGID = :manageUnit AND a."
								+ SEARCH_TYPE + " LIKE '" + searchText + "%'",
						parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> FYXX = dao.doSqlQuery(hql.toString(),
						parameters);
				for (Map<String, Object> map_fyxx : FYXX) {
					map_fyxx.put("isZT", 1);
					if ("null".equals(map_fyxx.get("CDMC") + "")) {
						map_fyxx.put("CDMC", "");
					}
					if ("null".equals(map_fyxx.get("FYDW") + "")) {
						map_fyxx.put("FYDW", "");
					}
				}
				res.put("count", count);
				res.put("mds", FYXX);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			searchText = MATCH_TYPE + searchText;
			StringBuffer hql = new StringBuffer(
					"select distinct a.YPXH as FYXH,a.YPMC as FYMC,a.BFGG as BFGG,a.BFDW as FYDW,a.ZBLB as FYGB,a.BFBZ as BFBZ,a.ZXBZ as ZXBZ,a.TYPE as TYPE,d.YPCD as YPCD,round(d .LSJG * (a.BFBZ / a.ZXBZ),4) as FYDJ,e.CDMC as CDMC,decode(g.YYZBM,'',' ','医保可报销') as YYZBM from ");
			hql.append("YK_TYPK");
			hql.append(" a,");
			hql.append("YK_YPBM");
			hql.append(" b,");
			hql.append("YK_YPXX");
			hql.append(" c,");
			hql.append("YK_CDXX");
			hql.append(" d,");
			hql.append("YK_CDXX");
			hql.append(" g,");
			hql.append("YK_CDDZ");
			hql.append(" e,");
			hql.append("YK_YPCD");
			hql.append(" f where a.YPXH = d.YPXH AND b.YPXH = d.YPXH AND c.YPXH = d.YPXH AND d.YPCD = e.YPCD AND a.YPXH = b.YPXH AND a.YPXH = c.YPXH AND c.YKZF = 0 AND a.ZFPB = 0 AND c.JGID = d.JGID AND b.PYDM LIKE '");
			hql.append(searchText);
			hql.append("%' AND c.JGID =:manageUnit AND f.YPXH = d.YPXH AND f.YPCD = d.YPCD AND f.ZFPB = 0 AND d.ZFPB = 0  AND c.YPXH = g.YPXH AND d.YPCD = g.YPCD AND e.YPXH = g.YPXH AND g.JGID='"+zbmjgid+"' ORDER BY a.YPXH");
			try {
				Long count = dao
						.doCount(
								"YK_TYPK a,YK_YPBM b,YK_YPXX c,YK_CDXX d,YK_CDDZ e, YK_YPCD f",
								"a.YPXH = d.YPXH AND b.YPXH = d.YPXH AND c.YPXH = d.YPXH AND d.YPCD = e.YPCD AND a.YPXH = b.YPXH AND a.YPXH = c.YPXH AND c.YKZF = 0 AND a.ZFPB = 0 AND c.JGID = d.JGID AND b.PYDM LIKE '"
										+ searchText
										+ "%' AND c.JGID =:manageUnit AND f.YPXH = d.YPXH AND f.YPCD = d.YPCD AND f.ZFPB = 0 AND d.ZFPB = 0 ",
								parameters);
				parameters.put("first", Integer.parseInt(strStart));
				parameters.put("max", Integer.parseInt(strLimit));
				List<Map<String, Object>> FYXX = dao.doSqlQuery(hql.toString(),
						parameters);
				for (Map<String, Object> map_fyxx : FYXX) {
					map_fyxx.put("FYDJ", dft.format(map_fyxx.get("FYDJ")));
				}
				res.put("count", count);
				res.put("mds", FYXX);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
