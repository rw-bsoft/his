package phis.prints.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.util.context.Context;

public class FsbSummarycategoryHospitalFile1 implements IHandler {
	List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(li);
		li.clear();
	}

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		int hzType = Integer.parseInt(request.get("hz") + "");// hz=3 为汇总(三)
																// hz=5 为汇总(五)
//		long bq = parseLong(request.get("bq"));
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		// User user = (User) ctx.get("user.instance");
		UserRoleToken user = UserRoleToken.getCurrent();
		// String Gl_jgid = user.get("manageUnit.id");
		String Gl_jgid = user.getManageUnit().getId();
		// String jgname = user.get("manageUnit.name");
		String jgname = user.getManageUnit().getName();
		// String czy = user.getName();
		String czy = (String) user.getUserName();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", Gl_jgid);
		try {
			String bqName = URLDecoder.decode(request.get("bqName") + "",
					"UTF-8");
			Date stardate = sdfdate.parse(request.get("beginDate") + "");
			Date enddate = sdfdate.parse(request.get("endDate") + "");
			parameters.put("adt_hzrq", stardate);
			parameters.put("adt_hzrq_end", enddate);

			StringBuffer hql3 = new StringBuffer();
			StringBuffer hql5 = new StringBuffer();
			hql3.append("SELECT g.ZYGB as ZYGB,g.SFMC as SFMC,sum(a.ZJJE) as ZJJE FROM JC_FYMX a,GY_SFXM g WHERE ( a.FYXM = g.SFXM ) and a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH||'_'||a.JSCS not in (select c.JLXH||'_'||c.jscs from JC_JCJS b, JC_FYMX_js c where c.ZYH = b.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_js d,JC_JCjs e,JC_jszf f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))");
			hql5.append("SELECT g.ZYGB as ZYGB, g.SFMC as SFMC, sum(a.ZJJE) as ZJJE FROM JC_FYMX a, GY_SFXM g WHERE (a.FYXM = g.SFXM) and a.JSCS<>0 AND a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_js d,JC_JCJS e,JC_JSZF f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))");
//			if (bq > 0) {// 某个病区
//				parameters.put("FYBQ", bq);
//				hql3.append(" and a.FYBQ=:FYBQ ");
//				hql5.append(" and a.FYBQ=:FYBQ ");
//			}
			hql3.append(" GROUP BY g.ZYGB,g.SFMC ");
			hql5.append(" GROUP BY g.ZYGB,g.SFMC");
			String sql = hzType == 3 ? hql3.toString() : hql5.toString();
			List<Map<String, Object>> xmflList = dao
					.doSqlQuery(sql, parameters);
			for (int i = 0; i < xmflList.size(); i = i + 3) {
				Map<String, Object> cf = new HashMap<String, Object>();
				cf.put("XM1", xmflList.get(i).get("SFMC"));
				cf.put("JE1",
						String.format("%1$.2f", xmflList.get(i).get("ZJJE")));
				if (i + 1 < xmflList.size()) {
					cf.put("XM2", xmflList.get(i + 1).get("SFMC"));
					cf.put("JE2",
							String.format("%1$.2f",
									xmflList.get(i + 1).get("ZJJE")));
				}
				if (i + 2 < xmflList.size()) {
					cf.put("XM3", xmflList.get(i + 2).get("SFMC"));
					cf.put("JE3",
							String.format("%1$.2f",
									xmflList.get(i + 2).get("ZJJE")));
				}
				li.add(cf);
			}
			// -------------合计--------------
			String[] lS_ksrq = (request.get("beginDate") + "").split("-| ");
			String ksrq = lS_ksrq[0] + "年" + lS_ksrq[1] + "月" + lS_ksrq[2]
					+ "日";
			String[] lS_jsrq = (request.get("endDate") + "").split("-| ");
			String jsrq = lS_jsrq[0] + "年" + lS_jsrq[1] + "月" + lS_jsrq[2]
					+ "日";
			Map<String, Object> hzparameters = new HashMap<String, Object>();
			hzparameters.put("al_jgid", Gl_jgid);
			hzparameters.put("adt_hzrq", stardate);
			hzparameters.put("adt_hzrq_end", enddate);
			StringBuffer hzhql3 = new StringBuffer();
			StringBuffer hzhql5 = new StringBuffer();
			hzhql3.append("select sum(a.ZJJE) as ZJJE from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH||'_'||a.JSCS not in (select c.JLXH||'_'||c.jscs from JC_JCJS b, JC_FYMX_JS c where c.ZYH = b.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_js d,JC_JCjs e,JC_jszf f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))");
			hzhql5.append("SELECT sum(a.ZJJE) as ZJJE FROM JC_FYMX a where a.HZRQ >= :adt_hzrq and a.JSCS<>0 and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_js d,JC_JCjs e,JC_jszf f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))");
//			if (bq > 0) {// 某个病区
//				hzparameters.put("FYBQ", bq);
//				hzhql3.append(" and a.FYBQ=:FYBQ ");
//				hzhql5.append(" and a.FYBQ=:FYBQ ");
//			}
			String hzsql = hzType == 3 ? hzhql3.toString() : hzhql5.toString();
			List<Map<String, Object>> xmhjList = dao.doSqlQuery(hzsql,
					hzparameters);
			double hjje = 0.00;
			if (xmhjList != null && xmhjList.size() > 0) {
				hjje = parseDouble(xmhjList.get(0).get("ZJJE"));
			}
			String ids_zyjs_hql = "select d.FKFS as FKFS,sum(d.FKJE) as FKJE,e.FKMC as FKMC from ("
//					+ "select a.FKFS as FKFS, (-1*a.FKJE) as FKJE from JC_FKXX a, JC_JSZF b,(select distinct ZYH,JSCS from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH not in (select d.jlxh from JC_FYMX_js d,JC_JCjs e,JC_jszf f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))) c where a.ZYH = c.ZYH and a.JSCS = c.JSCS and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"
//					+ " union all "
					+ "select a.FKFS as FKFS, a.FKJE as FKJE from JC_FKXX a, JC_JCJS b,(select distinct ZYH,JSCS from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_JS d,JC_JCJS e,JC_JSZF f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))) c where a.ZYH = c.ZYH and a.JSCS = c.JSCS and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"
					+ " union all "
					+ "select a.JKFS as FKFS, a.JKJE as FKJE from JC_TBKK a, JC_JCJS b,(select distinct ZYH,JSCS from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_JS d,JC_JCJS e,JC_JSZF f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))) c where a.ZYH = c.ZYH and a.JSCS = c.JSCS and a.ZYH = b.ZYH and a.JSCS = b.JSCS and b.HZRQ>=:adt_hzrq AND b.HZRQ<=:adt_hzrq_end AND b.JGID=:al_jgid"
					+ " union all "
					+ "select a.JKFS as FKFS, (-1*a.JKJE) as FKJE from JC_TBKK a, JC_JKZF b,JC_JCJS c,(select distinct ZYH,JSCS from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_JS d,JC_JCJS e,JC_JSZF f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))) d where a.ZYH = d.ZYH and a.JSCS = d.JSCS and b.JKXH = a.JKXH and a.ZYH = c.ZYH and a.JSCS = c.JSCS and c.HZRQ>=:adt_hzrq AND c.HZRQ<=:adt_hzrq_end AND c.JGID=:al_jgid"
					+ ") d left outer join GY_FKFS e on d.FKFS = e.FKFS group by d.FKFS,e.FKMC";
			String ids_brxz_hql = "select sum(c.FYHJ) as FYHJ,sum(c.ZFHJ) as ZFHJ,c.BRXZ as BRXZ,d.XZMC as XZMC,d.DBPB as DBPB from ("
					+ "SELECT a.FYHJ as FYHJ,a.ZFHJ as ZFHJ,a.BRXZ as BRXZ FROM JC_JCJS a,(select distinct ZYH,JSCS from JC_FYMX a where a.HZRQ >= :adt_hzrq and a.HZRQ <= :adt_hzrq_end and a.JGID = :al_jgid and (a.JLXH in (select c.JLXH from JC_JCJS b, JC_FYMX_JS c where b.ZYH = c.ZYH and c.JSCS = b.JSCS and b.HZRQ >= :adt_hzrq and b.HZRQ <= :adt_hzrq_end and b.JGID = :al_jgid and c.JLXH||'_'||c.JSCS not in (select d.jlxh||'_'||d.jscs from JC_FYMX_JS d,JC_JCJS e,JC_JSZF f where d.ZYH = e.ZYH and d.JSCS = e.JSCS and f.zyh = e.zyh and f.JSCS = e.JSCS and f.HZRQ >= :adt_hzrq and f.HZRQ <= :adt_hzrq_end and e.JGID = :al_jgid)))) b WHERE a.ZYH = b.ZYH and a.JSCS = b.JSCS and a.FYHJ<>a.ZFHJ and a.HZRQ>=:adt_hzrq AND a.HZRQ<=:adt_hzrq_end AND a.JGID=:al_jgid"
					+ ") c left outer join GY_BRXZ d on c.BRXZ = d.BRXZ group by c.BRXZ,d.XZMC,d.DBPB";
			List<Map<String, Object>> ids_zyjs = dao.doSqlQuery(ids_zyjs_hql,
					hzparameters);
			List<Map<String, Object>> ids_brxz = dao.doSqlQuery(ids_brxz_hql,
					hzparameters);
			String qtysFb = "";
			String jzjeSt = "0.00";
			if (ids_zyjs != null && ids_zyjs.size() != 0) {
				for (int j = 0; j < ids_zyjs.size(); j++) {
					qtysFb = qtysFb
							+ ids_zyjs.get(j).get("FKMC")
							+ ":"
							+ String.format("%1$.2f",
									ids_zyjs.get(j).get("FKJE")) + " ";
				}
			}
			if (ids_brxz != null && ids_brxz.size() != 0) {
				for (int j = 0; j < ids_brxz.size(); j++) {
					if (Integer.parseInt(ids_brxz.get(j).get("DBPB") + "") == 0) {
						jzjeSt = String.format(
								"%1$.2f",
								parseDouble(jzjeSt)
										+ (parseDouble(ids_brxz.get(j).get(
												"FYHJ")
												+ "") - parseDouble(ids_brxz
												.get(j).get("ZFHJ") + "")));
					} else {
						qtysFb = qtysFb
								+ ids_brxz.get(j).get("XZMC")
								+ ":"
								+ String.format(
										"%1$.2f",
										(parseDouble(ids_brxz.get(j)
												.get("FYHJ") + "") - parseDouble(ids_brxz
												.get(j).get("ZFHJ") + "")))
								+ " ";
					}
				}
				qtysFb = qtysFb + " " + "记账 :" + jzjeSt + " ";
			}
			// System.out.println("@@@"+qtysFb);
			response.put("qtysFb", qtysFb);
			response.put("title", jgname);
			response.put("KSMC", bqName);
			response.put("HZRQ", ksrq + " 至 " + jsrq);
			response.put("CZY", czy);
			response.put("HJJE", String.format("%1$.2f", hjje));// 总计金额
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}
}
