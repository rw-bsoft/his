package phis.prints.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class YbDrugListFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();
		String JGID = user.getManageUnit().getId();
		StringBuffer hql = new StringBuffer(
				"SELECT a.JGID as JGID,a.YPXH as YPXH,a.YPCD as YPCD,a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.CDMC as CDMC,a.PYDM as PYDM,a.YYZBM as YYZBM,a.LSJG as LSJG from ");
		hql.append("V_YPXX_YB a");
		hql.append(" where a.JGID=:JGID");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			if(!"".equals(request.get("PYDM")+"") && request.get("PYDM")!=null){
				hql.append(" and a.PYDM like :PYDM || '%'");
				parameters.put("PYDM", request.get("PYDM")+"");
			}
			hql.append(" order by a.YPXH,a.YPCD");
			List<Map<String, Object>> yp_list = new ArrayList<Map<String, Object>>();
			yp_list = dao.doQuery(hql.toString(), parameters);
			if (yp_list.size() > 0 && yp_list != null) {
				for (Map<String, Object> yp : yp_list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("YPMC", yp.get("YPMC") + "");
					if (yp.get("YPGG") != null) {
						map.put("YPGG", yp.get("YPGG") + "");
					} else {
						map.put("YPGG", "");
					}
					if (yp.get("YPDW") != null) {
						map.put("YPDW", yp.get("YPDW") + "");
					} else {
						map.put("YPDW", "");
					}
					map.put("CDMC", yp.get("CDMC") + "");
					map.put("PYDM", yp.get("PYDM") + "");
					if (yp.get("YYZBM") != null) {
						map.put("YYZBM", yp.get("YYZBM") + "");
					} else {
						map.put("YYZBM", "");
					}
					map.put("LSJG", String.format("%1$.4f",
							parseDouble(yp.get("LSJG"))));
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
		response.put("TITLE", jgname + "医保药品对照清单");
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
