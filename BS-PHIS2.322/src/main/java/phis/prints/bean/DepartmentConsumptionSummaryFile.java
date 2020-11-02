package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class DepartmentConsumptionSummaryFile implements IHandler {
	List<Map<String, Object>> ksxh = new ArrayList<Map<String, Object>>();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(ksxh);
	}

	@Override
	public void getParameters(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws PrintException {
		ksxh.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String title = user.getManageUnitName();
		String jgid = user.getManageUnitId();
		Long yksb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("storehouseId"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> countparameters = new HashMap<String, Object>();
		parameters.put("JGID", jgid);
		parameters.put("yksb", yksb);
		countparameters.put("JGID", jgid);
		countparameters.put("yksb", yksb);
		String zbmc = "全部";
		StringBuffer sql = new StringBuffer();
		sql.append("select a.CKKS as KSDM,COUNT(a.CKDH) as LYZS,SUM (PFJE) as PFHJ,SUM (LSJE) as LSHJ,SUM (JHJE) as JHHJ ");
		sql.append("from YK_CK01 a, YK_CK02 b,YK_CKFS c ");
		sql.append(" ,YK_TYPK d where d.YPXH = b.YPXH");
		sql.append(" and b.XTSB = a.XTSB and b.CKFS = a.CKFS and b.CKDH = a.CKDH and c.JGID = :JGID and c.XTSB=:yksb ");
		sql.append(" and a.CKPB = 1 and a.XTSB = c.XTSB and a.CKFS = c.CKFS and c.KSPB = 1");
		StringBuffer wheresql = new StringBuffer(
				"d.YPXH = b.YPXH and b.XTSB = a.XTSB and b.CKFS = a.CKFS and b.CKDH = a.CKDH and c.JGID = :JGID and c.XTSB=:yksb and a.CKPB = 1 and a.XTSB = c.XTSB and a.CKFS = c.CKFS and c.KSPB = 1");
		if (req.get("dateFrom") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			wheresql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')>=:dateFrom ");
			parameters.put("dateFrom", req.get("dateFrom") + " 00:00:00");
			countparameters.put("dateFrom", req.get("dateFrom") + " 00:00:00");
		}
		if (req.get("dateTo") != null) {
			sql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			wheresql.append(" and to_char(a.CKRQ,'yyyy-mm-dd hh24:mi:ss')<=:dateTo ");
			parameters.put("dateTo", req.get("dateTo") + " 23:59:59");
			countparameters.put("dateTo", req.get("dateTo") + " 23:59:59");
		}
		if (req.get("zblb") != null && !req.get("zblb").equals("")
				&& !req.get("zblb").equals("-1")) {
			String zblb = (String) req.get("zblb");
			zbmc = DictionaryController.instance()
					.getDic("phis.dictionary.prescriptionType").getItem(zblb)
					.getText();
			sql.append(" and d.TYPE=:zblb ");
			wheresql.append(" and d.TYPE=:zblb ");
			parameters.put("zblb", req.get("zblb") + "");
			countparameters.put("zblb", req.get("zblb") + "");
		} else {
			zbmc = "全部类别";
		}
		sql.append(" and a.CKKS is not null GROUP BY a.CKKS  ORDER BY a.CKKS ASC");
		wheresql.append(" and a.CKKS=:ksdm ");
		try {
			List<Map<String, Object>> ksrecs = dao.doSqlQuery(sql.toString(),
					parameters);
			BigDecimal LSZE = new BigDecimal("0");
			BigDecimal JHZE = new BigDecimal("0");
			for (int i = 0; i < ksrecs.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				String ksmc = DictionaryController.instance()
						.getDic("phis.dictionary.department")
						.getText(ksrecs.get(i).get("KSDM").toString() + " ");
				map.put("KSMC0", ksmc);
				countparameters.put("ksdm",
						Long.parseLong(ksrecs.get(i).get("KSDM") + ""));
				List<Map<String, Object>> LYZS0 = dao
						.doSqlQuery(
								"select distinct a.XTSB, a.CKFS, a.CKDH from YK_CK01 a, YK_CK02 b,YK_CKFS c,YK_TYPK d where "
										+ wheresql.toString(), countparameters);
				map.put("LYZS0", LYZS0.size() + "");
				map.put("PFHJ0", ksrecs.get(i).get("PFHJ") + " ");
				map.put("LSHJ0", ksrecs.get(i).get("LSHJ") + " ");
				map.put("JHHJ0", ksrecs.get(i).get("JHHJ") + " ");
				LSZE = LSZE.add(new BigDecimal(ksrecs.get(i).get("LSHJ") + ""));
				JHZE = JHZE.add(new BigDecimal(ksrecs.get(i).get("JHHJ") + ""));
				if (i < ksrecs.size() - 1) {
					i++;
					ksmc = DictionaryController
							.instance()
							.getDic("phis.dictionary.department")
							.getText(ksrecs.get(i).get("KSDM").toString() + " ");
					map.put("KSMC1", ksmc);
					countparameters.put("ksdm",
							Long.parseLong(ksrecs.get(i).get("KSDM") + ""));
					List<Map<String, Object>> LYZS1 = dao
							.doSqlQuery(
									"select distinct a.XTSB, a.CKFS, a.CKDH from YK_CK01 a, YK_CK02 b,YK_CKFS c,YK_TYPK d where "
											+ wheresql.toString(),
									countparameters);
					map.put("LYZS1", LYZS1.size() + "");
					map.put("PFHJ1", ksrecs.get(i).get("PFHJ") + " ");
					map.put("LSHJ1", ksrecs.get(i).get("LSHJ") + " ");
					map.put("JHHJ1", ksrecs.get(i).get("JHHJ") + " ");
					LSZE = LSZE.add(new BigDecimal(ksrecs.get(i).get("LSHJ")
							+ ""));
					JHZE = JHZE.add(new BigDecimal(ksrecs.get(i).get("JHHJ")
							+ ""));
				}
				ksxh.add(map);
			}
			res.put("title", title + "科室药品领用汇总表");
			res.put("datefrom", req.get("dateFrom"));
			res.put("dateto", req.get("dateTo"));
			res.put("zblb", zbmc);
			res.put("LSZE", LSZE);
			res.put("JHZE", JHZE);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
