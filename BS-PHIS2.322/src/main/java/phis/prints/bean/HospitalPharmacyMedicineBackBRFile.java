package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalPharmacyMedicineBackBRFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		parameter.put("jgid", JGID);
		Long bq = 0L;
		if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Long.parseLong(user.getProperty("wardId") + "");
		}
		parameter.put("bqsb", bq);
		try {
			StringBuffer sqlString = new StringBuffer(
					"select a.YPGG as YPGG,a.YFDW as YFDW,sum(a.YPSL) as YPSL,a.YFSB as YFSB,a.YPXH as YPXH,d.CDMC as YPCD,b.YPMC as YPMC,b.YPSX as YPSX,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE,c.ZYHM as ZYHM,c.BRXM as BRXM from YF_ZYFYMX a,YK_TYPK b,ZY_BRRY c,YK_CDDZ d  where a.JGID=:jgid and a.LYBQ=:bqsb and a.YPXH=b.YPXH and a.ZYH=c.ZYH and a.JGID=c.JGID and a.YPCD=d.YPCD and a.ZYH=:ZYH and a.YPSL<0 ");
			parameter.put("ZYH", Long.parseLong(request.get("ZYH") + ""));
			if (request.get("dateFrom") != null) {
				sqlString
						.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
				parameter.put("dateFrom", request.get("dateFrom") + "");
			}
			if (request.get("dateTo") != null) {
				sqlString
						.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
				parameter.put("dateTo", request.get("dateTo") + "");
			}
			if (!"0".equals(request.get("YF"))) {
				sqlString.append(" and a.YFSB=:YF ");
				parameter.put("YF", MedicineUtils.parseLong(request.get("YF")));
			}
			sqlString
					.append(" group by a.YFSB,a.YPXH,a.YPCD,d.CDMC,b.YPMC,a.YPGG,b.YPSX,a.YFDW,a.YPDJ,c.ZYHM,c.BRXM");
			List<Map<String, Object>> rklist = dao.doQuery(
					sqlString.toString(), parameter);
			records.addAll(rklist);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String TITLE = user.getManageUnitName();
		int bq = 0;
		if (user.getProperty("wardId") != null
				&& user.getProperty("wardId") != "") {
			bq = Integer.parseInt(user.getProperty("wardId") + "");
		}
		try {
			if (null != request.get("dateFrom")) {
				response.put(
						"dateForm",
						sdf.format(BSHISUtil.toDate(request.get("dateFrom")
								+ "")));
			}
			if (null != request.get("dateTo")) {
				response.put("dateTo", sdf.format(BSHISUtil.toDate(request
						.get("dateTo") + "")));
			}
			response.put("TITLE", TITLE + "病区退药明细清单(按病人统计)");
			if (bq != 0) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("KSDM", Long.parseLong(request.get("bq") + ""));
				String sqlString = "SELECT officename as KSMC FROM sys_office WHERE id =:KSDM and hospitalArea = 1 ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString,
						parameter);
				response.put("BQ", rklist.get(0).get("KSMC"));
			}
			if (!"0".equals(request.get("YF"))) {
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("YFSB", Long.parseLong(request.get("YF") + ""));
				String sqlString = "SELECT YFMC as YFMC FROM YF_YFLB WHERE YFSB =:YFSB ";
				List<Map<String, Object>> rklist = dao.doSqlQuery(sqlString,
						parameter);
				response.put("YFMC", rklist.get(0).get("YFMC"));
			} else {
				response.put("YFMC", "全部药房");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
