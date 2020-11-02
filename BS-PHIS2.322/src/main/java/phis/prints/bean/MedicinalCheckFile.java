package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.pub.source.PublicModel;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class MedicinalCheckFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String JGID = user.getManageUnit().getId();
		StringBuffer hql = new StringBuffer(
				"SELECT a.SBXH as SBXH,a.YFSB as YFSB,a.CKBH as CKBH,a.PDDH as PDDH,a.YPXH as YPXH,a.YPCD as YPCD,"
						+ "a.YPGG as YPGG,a.YFBZ as YFBZ,a.YFDW as YFDW,a.PQSL as PQSL,a.SPSL as SPSL,a.LSJG as LSJG,a.PFJG as PFJG,a.JHJG as JHJG,"
						+ "a.YPPH as YPPH,a.YPXQ as YPXQ,a.YLSE as YLSE,a.YPFE as YPFE,a.YJHE as YJHE,a.XLSE as XLSE,a.XPFE as XPFE,a.XJHE as XJHE,"
						+ "a.KCSB as KCSB,b.YPMC as YPMC,c.CDMC as CDMC,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.YPSX as YPSX,"
						+ "b.TSYP as TSYP,b.TYPE as TYPE,b.YPDM as YPDM,a.LRBZ as LRBZ,a.JGID as JGID,d.KWBM as KWBM from ");
		hql.append("YF_YK02");
		hql.append(" a,");
		hql.append("YK_TYPK");
		hql.append(" b,");
		hql.append("YK_CDDZ");
		hql.append(" c,");
		hql.append("YF_YPXX");
		hql.append(" d");
		hql.append(" where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.YFSB=d.YFSB and a.YPXH=d.YPXH and a.YFSB=:YFSB and a.PDDH=:PDDH and a.JGID=:JGID"
				+
				 " order by d.KWBM,b.YPMC,b.YPSX");//zhaojian 2017-10-10
				//" order by a.SBXH");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("YFSB", parseLong(request.get("yfsb")));
			parameters.put("PDDH", parseInteger(request.get("pddh")));
			// System.out.println(request.get("pddh"));

			List<Map<String, Object>> yp_list = new ArrayList<Map<String, Object>>();
			yp_list = dao.doQuery(hql.toString(), parameters);
			// System.out.println(hql.toString());
			// System.out.println(yp_list.size());
			if (yp_list.size() > 0 && yp_list != null) {
				for (Map<String, Object> yp : yp_list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("YPMC", yp.get("YPMC") + "");
					if (yp.get("YPGG") != null) {
						map.put("YPGG", yp.get("YPGG") + "");
					} else {
						map.put("YPGG", "");
					}
					if (yp.get("YFDW") != null) {
						map.put("YFDW", yp.get("YFDW") + "");
					} else {
						map.put("YFDW", "");
					}
					map.put("CDMC", yp.get("CDMC") + "");
					map.put("LSJG", String.format("%1$.4f",
							parseDouble(yp.get("LSJG"))));
					map.put("JHJG", String.format("%1$.4f",
							parseDouble(yp.get("JHJG"))));
					map.put("PQSL", String.format("%1$.2f",
							parseDouble(yp.get("PQSL"))));
					map.put("SPSL", String.format("%1$.2f",
							parseDouble(yp.get("SPSL"))));
					map.put("YK", String.format("%1$.2f", (parseDouble(yp
							.get("SPSL")) - parseDouble(yp.get("PQSL")))));
					map.put("KWBM", yp.get("KWBM") + "");
					records.add(map);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		BaseDAO dao = new BaseDAO(ctx);
		String JGID = user.getManageUnit().getId();
		// response.put("PDR", request.get("pdr"));
		String pdrq = request.get("pdrq")+"";
		response.put("PDRQ", pdrq.replace("_"," "));
		response.put("TITLE", jgname + "盘存清单");
		StringBuffer hql = new StringBuffer("");
		hql.append("select sum(a.PQSL*a.LSJG) as PQLSJG,sum(a.PQSL*a.JHJG) as PQJHJG,sum(a.SPSL*a.LSJG) as SPLSJG,sum(a.SPSL*a.JHJG) as SPJHJG");
		hql.append(" from YF_YK02 a where a.PDDH=:PDDH and a.JGID=:JGID and a.YFSB=:YFSB");
		try {
			StringBuffer hql_pdr = new StringBuffer();
			hql_pdr.append("select PERSONNAME as  PERSONNAME from SYS_Personnel where PERSONID=:pdrgh");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("pdrgh", request.get("pdr") + "");
			Map<String, Object> map_pdr = dao.doLoad(hql_pdr.toString(),
					map_par);
			if (map_pdr != null && map_pdr.size() > 0) {
				response.put("PDR", map_pdr.get("PERSONNAME"));
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			parameters.put("YFSB", parseLong(request.get("yfsb")));
			parameters.put("PDDH", parseInteger(request.get("pddh")));
			Map<String, Object> sumMap = new HashMap<String, Object>();
			sumMap = dao.doLoad(hql.toString(), parameters);
			if (sumMap != null) {
				response.put(
						"PQLSJG",
						String.format("%1$.4f",
								parseDouble(sumMap.get("PQLSJG"))));
				response.put(
						"PQJHJG",
						String.format("%1$.4f",
								parseDouble(sumMap.get("PQJHJG"))));
				response.put(
						"SPLSJG",
						String.format("%1$.4f",
								parseDouble(sumMap.get("SPLSJG"))));
				response.put(
						"SPJHJG",
						String.format("%1$.4f",
								parseDouble(sumMap.get("SPJHJG"))));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public int parseInteger(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
