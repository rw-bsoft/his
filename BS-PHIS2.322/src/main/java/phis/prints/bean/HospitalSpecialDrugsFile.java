package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalSpecialDrugsFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		parameter.put("jgid", JGID);
		Long yf = 0L;
		if (user.getProperty("pharmacyId") != null
				&& user.getProperty("pharmacyId") != "") {
			yf = Long.parseLong(user.getProperty("pharmacyId") + "");
		}
		parameter.put("yfsb", yf);
		try {
			StringBuffer sqlString = new StringBuffer("select YPXH as YPXH,YPM as YPM ,GG as GG,sum(SL) as SL from (");
			sqlString.append("select c.YPXH as YPXH,c.YPMC as YPM,c.YPGG as GG,SUM(a.YPSL) as SL from YF_MZFYMX a ");
			sqlString.append(" left join YK_TYPK c on a.YPXH = c.YPXH left join MS_CF01 f on a.CFSB = f.CFSB " );
			sqlString.append(" where a.JGID = :jgid and a.YFSB = :yfsb and c.tsyp <> 0 ");
			if (request.get("dateFrom") != null) {
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
				parameter.put("dateFrom", request.get("dateFrom") + " 00:00:00");
			}
			if (request.get("dateTo") != null) {
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
				parameter.put("dateTo", request.get("dateTo") + " 23:59:59");
			}
			if (request.get("yplx") != null && !request.get("yplx").equals("0")
					&& request.get("yplx") != "") {// 特殊药品类型pecialMedicines.dic
				sqlString.append(" AND c.TSYP = :yplx");
				parameter.put("yplx", request.get("yplx") + "");
			}
			sqlString.append(" group by c.YPXH,c.YPMC,c.YPGG union all ");
			sqlString.append("select c.YPXH as YPXH,c.YPMC as YPM," +
					" c.YPGG as GG,SUM(a.YPSL) as SL from YF_ZYFYMX a ");
			sqlString.append(" left join yk_typk c on a.ypxh = c.ypxh ");
			sqlString.append(" where a.JGID = :jgid and a.YFSB = :yfsb and c.tsyp <> 0 ");
			if (request.get("dateFrom") != null) {
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom2 ");
				parameter.put("dateFrom2", request.get("dateFrom")+ " 00:00:00");
			}
			if (request.get("dateTo") != null) {
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo2 ");
				parameter.put("dateTo2", request.get("dateTo") + " 23:59:59");
			}
			if (request.get("yplx") != null && !request.get("yplx").equals("0")
					&& request.get("yplx") != "") {// 特殊药品类型pecialMedicines.dic
				sqlString.append(" AND c.TSYP = :yplx");
				parameter.put("yplx", request.get("yplx") + "");
			}
			sqlString.append("  group by c.YPXH,c.YPMC,c.YPGG) group by YPXH,YPM,GG order by YPXH ");
			List<Map<String, Object>> ret = dao.doSqlQuery(
					sqlString.toString(), parameter);
			for(int i=0;i<ret.size();i++){
				ret.get(i).put("XH", i+1);
				records.add(ret.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnitName();
		String userName = user.getUserName();// 用户名
		response.put("ZBR", userName);// 操作员
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd");
		response.put("ZBRQ",sdftime.format(new Date()));
		String JGID = user.getManageUnitId();// 用户的机构ID
		Long YF = 0L;
		String dateFrom = "";
		String dateTo = "";
		String yplx = "全部";
		if (user.getProperty("pharmacyId") != null
				&& user.getProperty("pharmacyId") != "") {
			YF = Long.parseLong(user.getProperty("pharmacyId") + "");
		}
		try {
			if (null != request.get("dateFrom")) {
				dateFrom = request.get("dateFrom") + "";
			}
			if (null != request.get("dateTo")) {
				dateTo = request.get("dateTo") + "";
			}
			if (null != request.get("yplx") && !request.get("yplx").equals("")&&!"0".equals(request.get("yplx"))) {
				String ypdm = (String) request.get("yplx");
				yplx = DictionaryController.instance()
						.getDic("phis.dictionary.pecialMedicines")
						.getText(ypdm);

			}
			response.put("TITLE", TITLE + "药房特殊药品统计");
			response.put("SJ", dateFrom + "至" + dateTo);
			response.put("YPLX", yplx);
			if (YF != 0) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("yfsb", YF);
				parameter.put("jgid", JGID);
				String sqlString = "SELECT YFMC FROM YF_YFLB WHERE YFSB =:yfsb and JGID = :jgid";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString,
						parameter);
				response.put("YFMC", rklist.get(0) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
