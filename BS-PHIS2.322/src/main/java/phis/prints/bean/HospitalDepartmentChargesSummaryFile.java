package phis.prints.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalDepartmentChargesSummaryFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		String qxsb = request.get("qxsb") + "";
		parameter.put("jgid", JGID);
		Long bq = 0L;
		if (request.get("bq") != null && request.get("bq") != ""
				&& !"".equals(request.get("bq"))
				&& !"null".equals(request.get("bq"))) {
			bq = Long.parseLong(request.get("bq") + "");
		}
		if ("bqgl".equals(qxsb)) {
			bq = MedicineUtils.parseLong(user.getProperty("wardId"));
		}
		try {
			StringBuffer sqlString = new StringBuffer(
					"SELECT b.SFMC as SFMC,b.PYDM as PYDM,b.SFXM as SFXM,b.ZYGB as ZYGB,sum(a.ZJJE) as ZJJE,sum(a.ZFJE) as ZFJE,0 as Sort_Num  FROM ZY_FYMX a, GY_SFXM b ");
			sqlString.append(" WHERE a.FYXM = b.SFXM and a.JGID = :jgid ");
			// if (request.get("qxsb") != null) {
			// int KSLB=1;
			// if(request.get("qxsb").equals("bqgl")){
			// KSLB=1;//模块在病区使用
			// }else if(request.get("qxsb").equals("zygl")){
			// KSLB=2;//模块在住院使用
			// }
			// sqlString.append(" and a.KSLB=:KSLB ");
			// parameter.put("KSLB",KSLB);
			// }
			if (request.get("dateFrom") != null) {
				sqlString
						.append(" and to_char(a.FYRQ,'yyyy-mm-dd HH24:MI:SS')>=:dateFrom ");
				parameter
						.put("dateFrom", request.get("dateFrom") + " 00:00:00");
			}
			if (request.get("dateTo") != null) {
				sqlString
						.append(" and to_char(a.FYRQ,'yyyy-mm-dd HH24:MI:SS')<=:dateTo ");
				parameter.put("dateTo", request.get("dateTo") + " 23:59:59");
			}
			if (bq != null && bq != 0) {
				sqlString.append(" and a.FYBQ=:KSDM ");
				parameter.put("KSDM", bq);
			}
			sqlString.append(" GROUP BY b.SFMC, b.PYDM, b.SFXM, b.ZYGB ");
			List<Map<String, Object>> rklist = dao.doQuery(
					sqlString.toString(), parameter);
			BigDecimal hj = new BigDecimal(0);
			for (int i = 0; i < rklist.size(); i++) {
				hj = hj.add(new BigDecimal(rklist.get(i).get("ZJJE") + ""));
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("XM0", rklist.get(i).get("SFMC").toString());
				map.put("JE0",
						String.format("%1$.2f", rklist.get(i).get("ZJJE")) + "");
				if (i < rklist.size() - 1) {
					i++;
					map.put("XM1", rklist.get(i).get("SFMC").toString());
					map.put("JE1",
							String.format("%1$.2f", rklist.get(i).get("ZJJE"))
									+ "");
					hj = hj.add(new BigDecimal(rklist.get(i).get("ZJJE") + ""));
				}
				if (i < rklist.size() - 1) {
					i++;
					map.put("XM2", rklist.get(i).get("SFMC").toString());
					map.put("JE2",
							String.format("%1$.2f", rklist.get(i).get("ZJJE"))
									+ "");
					hj = hj.add(new BigDecimal(rklist.get(i).get("ZJJE") + ""));
				}
				map.put("HJ", hj);
				records.add(map);
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
		String username = user.getUserName();
		response.put("CZY", username);
		Long bq = 0L;
		String dateFrom = "";
		String dateTo = "";
		String qxsb = request.get("qxsb") + "";
		if (request.get("bq") != null && request.get("bq") != ""
				&& !"".equals(request.get("bq"))
				&& !"null".equals(request.get("bq"))) {
			bq = Long.parseLong(request.get("bq") + "");
		}
		if ("bqgl".equals(qxsb)) {
			bq = MedicineUtils.parseLong(user.getProperty("wardId"));
		}
		try {
			if (null != request.get("dateFrom")) {
				dateFrom = request.get("dateFrom") + "";
			}
			if (null != request.get("dateTo")) {
				dateTo = request.get("dateTo") + "";
			}
			response.put("TITLE", TITLE + "病区科室收入核算");
			response.put("RYRQ", dateFrom + "至" + dateTo);
			if (bq != 0) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("KSDM", bq);
				String sqlString = "SELECT officename as KSMC FROM sys_office WHERE id =:KSDM and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString,
						parameter);
				response.put("KSMC", rklist.get(0).get("KSMC"));
			} else {
				response.put("KSMC", "全部病区");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
