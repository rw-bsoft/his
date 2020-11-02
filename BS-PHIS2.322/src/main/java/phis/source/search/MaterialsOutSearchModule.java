package phis.source.search;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.Transformers;

import phis.source.utils.BSPHISUtil;
import phis.source.utils.JSONUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.T;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class MaterialsOutSearchModule extends AbstractSearchModule {
	/**
	 * 实现物资出库的物资查询功能
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		String jgid = user.getManageUnit().getId();
		String ckwzjslx = ParameterUtil.getParameter(jgid, "CKWZJSLX" + kfxh,
				"0", "物资出库物资检索类型 0.按库存检索1.按批次检索", ctx);
		String searchText = req.get("query").toString().toUpperCase();
		String strStart = req.get("start").toString();// 分页用
		String strLimit = req.get("limit").toString();
		Long al_Zblb = 0L;
		if (req.get("zblb") != null) {
			al_Zblb = Long.parseLong(req.get("zblb") + "");
		}
		int al_Kfxh = 0;
		if (req.get("kfxh") != null && req.get("kfxh") != "") {
			al_Kfxh = Integer.parseInt(req.get("kfxh") + "");
		}
		try {
			StringBuffer sql = new StringBuffer();
			if (al_Zblb == 0) {
				sql.append(
						"SELECT b.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,0 as KCXH,0 as CJXH,'' as CJMC,'' as WZPH,null as SCRQ,null as SXRQ,0 as WZJG,'' as MJPH,SUM(c.WZSL-c.YKSL) as TJSL,b.BKBZ as BKBZ,b.GLFS as GLFS from ")
						.append("WL_WZZD")
						.append(" b,")
						.append("WL_WZKC")
						.append(" c where b.WZXH in (select distinct a.WZXH from WL_WZBM a where")
						.append(" (a.")
						.append(SEARCH_TYPE)
						.append(" like '")
						.append(searchText)
						.append("%' or a.WBDM ")
						.append(" like '")
						.append(searchText)
						.append("%')) and b.WZXH=c.WZXH AND c.KFXH=")
						.append(al_Kfxh)
						.append(" group by b.WZXH,b.WZMC,b.WZGG,b.WZDW,b.BKBZ,b.GLFS order by b.WZXH");
			} else {
				if ("0".equals(ckwzjslx)) {
					sql.append(
							"SELECT b.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,0 as KCXH,0 as CJXH,'' as CJMC,'' as WZPH,null as SCRQ,null as SXRQ,0 as WZJG, '' as MJPH,SUM(c.WZSL-c.YKSL) as TJSL,b.BKBZ as BKBZ,b.GLFS as GLFS from ")
							.append("WL_WZZD")
							.append(" b,")
							.append("WL_WZKC")
							.append(" c where b.WZXH in (select distinct a.WZXH from WL_WZBM a where")
							.append(" (a.")
							.append(SEARCH_TYPE)
							.append(" like '")
							.append(searchText)
							.append("%' or a.WBDM ")
							.append(" like '")
							.append(searchText)
							.append("%')) and b.WZXH=c.WZXH AND c.KFXH=")
							.append(al_Kfxh)
							.append(" and ( c.ZBLB =")
							.append(al_Zblb)
							.append(" Or c.ZBLB = 0)")
							.append(" group by b.WZXH,b.WZMC,b.WZGG,b.WZDW,b.BKBZ,b.GLFS order by b.WZXH");
				} else if ("1".equals(ckwzjslx)) {
					sql.append(
							"SELECT c.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.KCXH as KCXH,c.CJXH as CJXH,d.CJMC as CJMC,c.WZPH as WZPH,c.SCRQ as SCRQ,c.SXRQ as SXRQ,c.WZJG as WZJG,c.LSJG as LSJG,c.MJPH as MJPH,c.WZSL- c.YKSL as TJSL,b.BKBZ as BKBZ,b.GLFS as GLFS from ")
							.append("WL_WZBM")
							.append(" a,")
							.append("WL_WZZD")
							.append(" b,")
							.append("WL_WZKC")
							.append(" c,")
							.append("WL_SCCJ")
							.append(" d where a.WZXH=b.WZXH AND b.WZXH=c.WZXH AND c.CJXH=d.CJXH AND c.KFXH=")
							.append(kfxh).append(" AND ( c.ZBLB =")
							.append(al_Zblb).append(" Or c.ZBLB = 0) AND (a.")
							.append(SEARCH_TYPE).append(" like '")
							.append(searchText).append("%' or a.WBDM ")
							.append(" like '").append(searchText)
							.append("%') order by b.WZXH,d.CJXH");
				}
			}
			StringBuffer sql_count = new StringBuffer();
			sql_count.append("select count(*) as TOTAL from (")
					.append(sql.toString()).append(")");
			List<Map<String, Object>> l = ss
					.createSQLQuery(sql_count.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			Long count = Long.parseLong(l.get(0).get("TOTAL") + "");
			List<Map<String, Object>> supps = ss.createSQLQuery(sql.toString())
					.setFirstResult(Integer.parseInt(strStart))
					.setMaxResults(Integer.parseInt(strLimit))
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			List<Supplies> Supplies = new ArrayList<Supplies>();
			for (int i = 0; i < supps.size(); i++) {
				Supplies s = new Supplies();
				s.setWZXH(parseLong(supps.get(i).get("WZXH")));
				s.setWZMC(supps.get(i).get("WZMC") + "");
				s.setCJXH(supps.get(i).get("CJXH") == null ? 0L
						: parseLong(supps.get(i).get("CJXH")));
				s.setCJMC(supps.get(i).get("CJMC") == null ? "" : supps.get(i)
						.get("CJMC") + "");
				s.setNumKey((i + 1 == 10) ? 0 : i + 1);
				s.setWZGG(supps.get(i).get("WZGG") == null ? "" : supps.get(i)
						.get("WZGG") + "");
				s.setWZDW(supps.get(i).get("WZDW") == null ? "" : supps.get(i)
						.get("WZDW") + "");
				s.setKCXH(supps.get(i).get("KCXH") == null ? 0L
						: parseLong(supps.get(i).get("KCXH")));
				s.setWZPH(supps.get(i).get("WZPH") == null ? "" : supps.get(i)
						.get("WZPH") + "");
				s.setSCRQ(supps.get(i).get("SCRQ") == null ? "" : T
						.formatDateTime((Date) supps.get(i).get("SCRQ")));
				s.setSXRQ(supps.get(i).get("SXRQ") == null ? "" : T
						.formatDateTime((Date) supps.get(i).get("SXRQ")));
				s.setWZJG(parseDouble(supps.get(i).get("WZJG")));
				if (supps.get(i).get("LSJG") != null
						&& supps.get(i).get("LSJG") != "") {
					s.setLSJG(Double.parseDouble(supps.get(i).get("LSJG") + ""));
				}else{
					s.setLSJG(Double.parseDouble(supps.get(i).get("WZJG") + ""));
				}
				s.setTJSL(parseDouble(supps.get(i).get("TJSL")));
				s.setBKBZ(parseInt(supps.get(i).get("BKBZ")));
				s.setGLFS(parseInt(supps.get(i).get("GLFS")));
				s.setMJPH(supps.get(i).get("MJPH") == null ? "" : supps.get(i)
						.get("MJPH") + "");
				Supplies.add(s);
				if (i >= 9)
					break;
			}
			res.put("count", count);
			res.put("sups", JSONUtil.ConvertObjToMapList(Supplies));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-7-27
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-10
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-8-20
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
//		if (number > 8) {
//			return 0;
//		}
//		double x = Math.pow(10, number);
//		return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data,number);
	}
}
