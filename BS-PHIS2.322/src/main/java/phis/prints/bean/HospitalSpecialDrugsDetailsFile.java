package phis.prints.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class HospitalSpecialDrugsDetailsFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		parameter.put("jgid", JGID);
		Long yf = 0L;
		if (request.get("yfsb")!=null) {
			yf = Long.parseLong(request.get("yfsb") + "");
		}
		parameter.put("yfsb", yf);
		try {
			if (request.get("ypxh") != null) {
				parameter.put("ypxh", Long.parseLong(request.get("ypxh") + ""));
			}
			StringBuffer sqlString = new StringBuffer("select * from (");
			sqlString.append("select d.BRXM as XM,d.MZHM as MZHM,d.BRXB as XB,d.CSNY as CSNY,c.YPMC as YPM,c.YPGG as GG,SUM(a.YPSL) as SL,a.YPPH as PH,to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') as SYRQ from YF_MZFYMX a ");
			sqlString.append(" left join YK_TYPK c on a.YPXH = c.YPXH left join MS_CF01 f on a.CFSB = f.CFSB left join MS_BRDA d on d.BRID = f.BRID ");
			sqlString.append(" where a.JGID = :jgid and a.YFSB = :yfsb and c.tsyp <> 0 and a.ypxh=:ypxh ");
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
			sqlString.append(" group by d.BRXM,d.MZHM,d.BRXB,d.CSNY,c.YPMC,c.YPGG, a.YPPH ,a.FYRQ union all ");
			sqlString.append("select b.BRXM as XM,b.ZYHM as MZHM,b.BRXB as XB,b.CSNY as CSNY,c.YPMC as YPM,c.YPGG as GG,SUM(a.YPSL) as SL,a.YPPH as PH,to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') as SYRQ from YF_ZYFYMX a ");
			sqlString.append(" left join yk_typk c on a.ypxh = c.ypxh left join ZY_BRRY b on a.zyh = b.zyh ");
			sqlString.append(" where a.JGID = :jgid and a.YFSB = :yfsb and c.tsyp <> 0 and a.ypxh=:ypxh ");
			if (request.get("dateFrom") != null) {
				sqlString.append(" and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom2 ");
				parameter.put("dateFrom2", request.get("dateFrom")
						+ " 00:00:00");
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
			sqlString.append("  group by b.BRXM,b.ZYHM,b.BRXB,b.CSNY,c.YPMC,c.YPGG, a.YPPH ,a.FYRQ) order by MZHM,SYRQ");
			List<Map<String, Object>> ret = dao.doSqlQuery(
					sqlString.toString(), parameter);
			String lastMZHM = "";
			for (int i = 0; i < ret.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (lastMZHM.equals(ret.get(i).get("MZHM") + "")) {
					if (i % 16 == 0) {// 打印分页页面一页只显示16条数据，分页后的第一条数据需要显示姓名和门住号码
						map.put("XM", ret.get(i).get("XM"));
						map.put("MZHM", ret.get(i).get("MZHM"));
						map.put("XB",
								DictionaryController.instance()
										.get("phis.dictionary.gender")
										.getText(ret.get(i).get("XB") + ""));
						map.put("CSNY", ret.get(i).get("CSNY"));
						map.put("YPM", ret.get(i).get("YPM"));
						map.put("GG", ret.get(i).get("GG"));
						map.put("SL", ret.get(i).get("SL"));
						map.put("PH", ret.get(i).get("PH"));
						map.put("SYRQ", ret.get(i).get("SYRQ"));
					} else {
						map.put("KSSJ", "");
						map.put("KSRQ", "");
						map.put("XB",
								DictionaryController.instance()
										.get("phis.dictionary.gender")
										.getText(ret.get(i).get("XB") + ""));
						map.put("CSNY", ret.get(i).get("CSNY"));
						map.put("YPM", ret.get(i).get("YPM"));
						map.put("GG", ret.get(i).get("GG"));
						map.put("SL", ret.get(i).get("SL"));
						map.put("PH", ret.get(i).get("PH"));
						map.put("SYRQ", ret.get(i).get("SYRQ"));
					}
				} else {
					map.put("XM", ret.get(i).get("XM"));
					map.put("MZHM", ret.get(i).get("MZHM"));
					map.put("XB",
							DictionaryController.instance()
									.get("phis.dictionary.gender")
									.getText(ret.get(i).get("XB") + ""));
					map.put("CSNY", ret.get(i).get("CSNY"));
					map.put("YPM", ret.get(i).get("YPM"));
					map.put("GG", ret.get(i).get("GG"));
					map.put("SL", ret.get(i).get("SL"));
					map.put("PH", ret.get(i).get("PH"));
					map.put("SYRQ", ret.get(i).get("SYRQ"));
					lastMZHM = ret.get(i).get("MZHM") + "";
				}

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
		String userName = user.getUserName();// 用户名
		response.put("CZY", userName);// 操作员
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
