package chis.source.print.instance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import chis.source.print.base.PrintImpl;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class ChildrenCasualty extends PrintImpl {

	private Integer getCount(Session ss, SQLQuery q, String queryString) {
		Integer count = 0;
		q = ss.createSQLQuery(queryString);
		@SuppressWarnings("rawtypes")
		List list = q.list();
		if (list.size() != 0) {
			count = ((BigDecimal) list.get(0)).intValue();
		}
		return count;
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		response.put(
				"manageUnit",
				dicKeyToText("chis.@manageUnit", request.get("manageUnit").toString()));
		response.put("year", request.get("year"));
		response.put("reportDate", new Date());
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SessionFactory factory = getSessionFactory(ctx);
		Session ss = factory.openSession();
		SQLQuery q = null;
		String sql = "select count(*) from MPI_DemographicInfo a";
		String old = "months_between(current_date, a.birthday)";
		String D = " where str(a.createTime,'yyyy')=" + request.get("year")
				+ " and a.createUnit like '" + request.get("manageUnit") + "%'";
		String M = "a.sexcode = 1"; // 男
		String F = "a.sexcode = 2"; // 女
		String BENDI = "a.registeredPermanent = 1"; // 本地
		String LIUDONG = "a.registeredPermanent = 2"; // 流动
		Transaction ta = ss.beginTransaction();
		try {
			Map<String, Object> r1 = new HashMap<String, Object>();
			r1.put("TYPE", "本地");
			r1.put("M_0_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + M
							+ " and " + old + "<12"));
			r1.put("M_1_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + M
							+ " and " + old + ">=12 and " + old + "<24"));
			r1.put("M_2_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + M
							+ " and " + old + ">=24 and " + old + "<36"));
			r1.put("M_3_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + M
							+ " and " + old + ">=36 and " + old + "<48"));
			r1.put("M_4_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + M
							+ " and " + old + ">=48 and " + old + "<72"));
			r1.put("M_0_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + M + " and " + old
							+ "<12 and a.empiId = b.empiId"));
			r1.put("M_1_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + M + " and " + old + ">=12 and "
							+ old + "<24 and a.empiId = b.empiId"));
			r1.put("M_2_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + M + " and " + old + ">=24 and "
							+ old + "<36 and a.empiId = b.empiId"));
			r1.put("M_3_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + M + " and " + old + ">=36 and "
							+ old + "<48 and a.empiId = b.empiId"));
			r1.put("M_4_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + M + " and " + old + ">=48 and "
							+ old + "<72 and a.empiId = b.empiId"));
			r1.put("F_0_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + F
							+ " and " + old + "<12"));
			r1.put("F_1_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + F
							+ " and " + old + ">=12 and " + old + "<24"));
			r1.put("F_2_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + F
							+ " and " + old + ">=24 and " + old + "<36"));
			r1.put("F_3_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + F
							+ " and " + old + ">=36 and " + old + "<48"));
			r1.put("F_4_COUNT",
					getCount(ss, q, sql + D + " and " + BENDI + " and " + F
							+ " and " + old + ">=48 and " + old + "<72"));
			r1.put("F_0_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + F + " and " + old
							+ "<12 and a.empiId = b.empiId"));
			r1.put("F_1_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + F + " and " + old + ">=12 and "
							+ old + "<24 and a.empiId = b.empiId"));
			r1.put("F_2_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + F + " and " + old + ">=24 and "
							+ old + "<36 and a.empiId = b.empiId"));
			r1.put("F_3_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + F + " and " + old + ">=36 and "
							+ old + "<48 and a.empiId = b.empiId"));
			r1.put("F_4_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ BENDI + " and " + F + " and " + old + ">=48 and "
							+ old + "<72 and a.empiId = b.empiId"));
			Map<String, Object> r2 = new HashMap<String, Object>();
			r2.put("TYPE", "流动");
			r2.put("M_0_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + M
							+ " and " + old + "<12"));
			r2.put("M_1_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + M
							+ " and " + old + ">=12 and " + old + "<24"));
			r2.put("M_2_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + M
							+ " and " + old + ">=24 and " + old + "<36"));
			r2.put("M_3_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + M
							+ " and " + old + ">=36 and " + old + "<48"));
			r2.put("M_4_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + M
							+ " and " + old + ">=48 and " + old + "<72"));
			r2.put("M_0_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + M + " and " + old
							+ "<12 and a.empiId = b.empiId"));
			r2.put("M_1_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + M + " and " + old
							+ ">=12 and " + old + "<24 and a.empiId = b.empiId"));
			r2.put("M_2_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + M + " and " + old
							+ ">=24 and " + old + "<36 and a.empiId = b.empiId"));
			r2.put("M_3_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + M + " and " + old
							+ ">=36 and " + old + "<48 and a.empiId = b.empiId"));
			r2.put("M_4_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + M + " and " + old
							+ ">=48 and " + old + "<72 and a.empiId = b.empiId"));
			r2.put("F_0_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + F
							+ " and " + old + "<12"));
			r2.put("F_1_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + F
							+ " and " + old + ">=12 and " + old + "<24"));
			r2.put("F_2_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + F
							+ " and " + old + ">=24 and " + old + "<36"));
			r2.put("F_3_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + F
							+ " and " + old + ">=36 and " + old + "<48"));
			r2.put("F_4_COUNT",
					getCount(ss, q, sql + D + " and " + LIUDONG + " and " + F
							+ " and " + old + ">=48 and " + old + "<72"));
			r2.put("F_0_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + F + " and " + old
							+ "<12 and a.empiId = b.empiId"));
			r2.put("F_1_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + F + " and " + old
							+ ">=12 and " + old + "<24 and a.empiId = b.empiId"));
			r2.put("F_2_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + F + " and " + old
							+ ">=24 and " + old + "<36 and a.empiId = b.empiId"));
			r2.put("F_3_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + F + " and " + old
							+ ">=36 and " + old + "<48 and a.empiId = b.empiId"));
			r2.put("F_4_COUNT_ACC",
					getCount(ss, q, sql + ",CDH_DefectRegister b" + D + " and "
							+ LIUDONG + " and " + F + " and " + old
							+ ">=48 and " + old + "<72 and a.empiId = b.empiId"));
			records.add(r1);
			records.add(r2);
			ta.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}

}
