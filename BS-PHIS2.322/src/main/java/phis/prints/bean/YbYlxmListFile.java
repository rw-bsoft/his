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

public class YbYlxmListFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		StringBuffer hql = new StringBuffer(
				"SELECT a.JGID as JGID,a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.PYDM as PYDM,a.YYZBM as YYZBM,a.XMBM as XMBM,a.FYDJ as FYDJ,a.FYGL as FYGL from ");
		hql.append("V_YLMX_YB a");
		hql.append(" where a.JGID=:JGID ");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			if(!"".equals(request.get("PYDM")+"") && request.get("PYDM")!=null){
				hql.append(" and a.PYDM like :PYDM || '%'");
				parameters.put("PYDM", request.get("PYDM")+"");
			}
			hql.append(" ORDER BY a.FYGL,a.PYDM");
			List<Map<String, Object>> yp_list = new ArrayList<Map<String, Object>>();
			yp_list = dao.doQuery(hql.toString(), parameters);
			if (yp_list.size() > 0 && yp_list != null) {
				for (Map<String, Object> xm : yp_list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("FYMC", xm.get("FYMC") + "");
					if (xm.get("FYDW") != null) {
						map.put("FYDW", xm.get("FYDW") + "");
					} else {
						map.put("FYDW", "");
					}
					map.put("PYDM", xm.get("PYDM") + "");
					if (xm.get("YYZBM") != null) {
						map.put("YYZBM", xm.get("YYZBM") + "");
					} else {
						map.put("YYZBM", "");
					}
					if (xm.get("XMBM") != null) {
						map.put("XMBM", xm.get("XMBM") + "");
					} else {
						map.put("XMBM", "");
					}
					if (xm.get("FYGL") != null) {
						map.put("FYGL", xm.get("FYGL") + "");
					} else {
						map.put("FYGL", "");
					}
					map.put("FYDJ", String.format("%1$.4f",
							parseDouble(xm.get("FYDJ"))));
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
		response.put("TITLE", jgname + "医保项目对照清单");
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
