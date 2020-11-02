package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class WarehousingEjSummaryFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		String jgid = user.getManageUnitId();// 用户的机构ID

		int ckkf = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			ckkf = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int kfxh = 0;
		if (request.get("kfxh") != null && request.get("kfxh") != "") {
			kfxh = Integer.parseInt(request.get("kfxh") + "");
		}
		try {
			if (ckkf == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);

				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						ckkf = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}

			Date dateForm = new Date();
			if (request.get("dateForm") != null) {
				dateForm = sdftime.parse(request.get("dateForm") + " 00:00:00");
			}
			Date dateTo = new Date();
			if (request.get("dateTo") != null) {
				dateTo = sdftime.parse(request.get("dateTo") + " 23:59:59");
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KFXH", kfxh);
			parameters.put("CKKF", ckkf);
			parameters.put("ldt_qsrq", dateForm);
			parameters.put("ldt_jsrq", dateTo);
			String sql = "SELECT wzzd.WZMC as WZMC,wzzd.WZGG as WZGG,wzzd.WZDW as WZDW,A.WZXH as WZXH,SUM(A.RKSL) as WZSL,SUM(A.RKJE) as WZJE FROM WL_WZZD wzzd,(select ck02.JLXH as JLXH,ck02.WZXH as WZXH,ck02.CJXH as CJXH,lzfs.YWLB * ck02.WZSL*-1 as RKSL,lzfs.YWLB * ck02.WZJE*-1 as RKJE from WL_CK01 ck01,WL_CK02 ck02,WL_LZFS lzfs where ck01.DJXH = ck02.DJXH and ck01.QRBZ = 1 and ck01.CKKF =:CKKF and ck01.QRRQ >=:ldt_qsrq and ck01.QRRQ<=:ldt_jsrq and ck01.KFXH=:KFXH AND (ck01.THDJ is null or ck01.THDJ <= 0) And ck01.LZFS=lzfs.FSXH union all select rk02.JLXH as JLXH,rk02.WZXH as WZXH,rk02.CJXH as CJXH,lzfs.YWLB*rk02.WZSL as RKSL,lzfs.YWLB*rk02.WZJE as RKJE from WL_RK01 rk01,WL_RK02 rk02,WL_LZFS lzfs where rk01.DJXH = rk02.DJXH and rk01.JZRQ>=:ldt_qsrq and rk01.JZRQ<=:ldt_jsrq and rk01.KFXH=:KFXH And lzfs.FSXH=rk01.LZFS) A where A.WZXH = wzzd.WZXH GROUP BY wzzd.WZMC,wzzd.WZGG,wzzd.WZDW,A.WZXH ORDER BY wzzd.WZMC,wzzd.WZGG,wzzd.WZDW,A.WZXH";
			List<Map<String, Object>> cflist = dao.doSqlQuery(sql, parameters);
			int n = 0;
			for (int i = 0; i < cflist.size(); i++) {
				n++;
				cflist.get(i).put("XH", n);

				Double WZSL = 0.00;
				if (cflist.get(i).get("WZSL") != null) {
					WZSL = Double.parseDouble(cflist.get(i).get("WZSL") + "");
				}
				cflist.get(i).put("WZSL",
						String.format("%1$.2f", WZSL).toString());

				Double WZJE = 0.00;
				if (cflist.get(i).get("WZJE") != null) {
					WZJE = Double.parseDouble(cflist.get(i).get("WZJE") + "");
				}
				cflist.get(i).put("WZJE",
						String.format("%1$.4f", WZJE).toString());

				records.add(cflist.get(i));

			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		int ckkf = 0;
		BaseDAO dao = new BaseDAO(ctx);
		String jgid = user.getManageUnitId();// 用户的机构ID
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			ckkf = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		try {
			if (ckkf == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);

				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						ckkf = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			Long kfxh = 0L;
			String kfmc = "";
			if (request.get("kfxh") != null && request.get("kfxh") != "") {
				kfxh = Long.parseLong(request.get("kfxh") + "");
			}
			if (user.getProperty("treasuryName") != null) {
				kfmc = user.getProperty("treasuryName") + "";
			}
			if (kfmc == "") {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = dao
						.doQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1", null);

				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KFMC") != null
							&& listKfxx.get(i).get("KFMC") != "") {
						kfmc = listKfxx.get(i).get("KFMC") + "";
					}
				}

			}
			int kfxhsum = 0;
			if (request.get("kfxh") != null && request.get("kfxh") != "") {
				kfxhsum = Integer.parseInt(request.get("kfxh") + "");
			}
			String dateFormstr = "";
			if (request.get("dateForm") != null) {
				dateFormstr = request.get("dateForm") + "";
			}
			String dateTostr = "";
			if (request.get("dateTo") != null) {
				dateTostr = request.get("dateTo") + "";
			}
			String jgname = user.getManageUnitName();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("KFXH", kfxh);
			String sql = "select KFMC as KFMC from WL_KFXX WHERE KFXH=:KFXH";

			Date dateForm = new Date();
			if (request.get("dateForm") != null) {
				dateForm = sdftime.parse(request.get("dateForm") + " 00:00:00");
			}
			Date dateTo = new Date();
			if (request.get("dateTo") != null) {
				dateTo = sdftime.parse(request.get("dateTo") + " 23:59:59");
			}
			Map<String, Object> parameterssum = new HashMap<String, Object>();
			parameterssum.put("KFXH", kfxhsum);
			parameterssum.put("CKKF", ckkf);
			parameterssum.put("ldt_qsrq", dateForm);
			parameterssum.put("ldt_jsrq", dateTo);
			String sqlsum = "SELECT SUM(A.RKSL) as WZSL,SUM(A.RKJE) as WZJE FROM WL_WZZD wzzd,(select ck02.JLXH as JLXH,ck02.WZXH as WZXH,ck02.CJXH as CJXH,lzfs.YWLB * ck02.WZSL*-1 as RKSL,lzfs.YWLB * ck02.WZJE*-1 as RKJE from WL_CK01 ck01,WL_CK02 ck02,WL_LZFS lzfs where ck01.DJXH = ck02.DJXH and ck01.QRBZ = 1 and ck01.CKKF =:CKKF and ck01.QRRQ >=:ldt_qsrq and ck01.QRRQ<=:ldt_jsrq and ck01.KFXH=:KFXH AND (ck01.THDJ is null or ck01.THDJ <= 0) And ck01.LZFS=lzfs.FSXH union all select rk02.JLXH as JLXH,rk02.WZXH as WZXH,rk02.CJXH as CJXH,lzfs.YWLB*rk02.WZSL as RKSL,lzfs.YWLB*rk02.WZJE as RKJE from WL_RK01 rk01,WL_RK02 rk02,WL_LZFS lzfs where rk01.DJXH = rk02.DJXH and rk01.JZRQ>=:ldt_qsrq and rk01.JZRQ<=:ldt_jsrq and rk01.KFXH=:KFXH And lzfs.FSXH=rk01.LZFS) A where A.WZXH = wzzd.WZXH";
			List<Map<String, Object>> cflistsum = dao.doSqlQuery(sqlsum,
					parameterssum);
			Map<String, Object> cfmap = dao.doLoad(sql, parameters);
			response.put("title", jgname);
			response.put("tagName", kfmc + "入库汇总报表");
			response.put("dataForm", dateFormstr);
			response.put("dateTo", dateTostr);
			if (cfmap != null) {
				response.put("KFXH", cfmap.get("KFMC"));
			}
			if (cflistsum.size() > 0) {
				if (cflistsum.get(0).get("WZSL") != null) {
					Double WZSL = Double.parseDouble(cflistsum.get(0).get(
							"WZSL")
							+ "");
					response.put("SUMWZSL", String.format("%1$.2f", WZSL)
							.toString());
				} else {
					response.put("SUMWZSL", "0.00");
				}
				if (cflistsum.get(0).get("WZJE") != null) {
					Double WZJE = Double.parseDouble(cflistsum.get(0).get(
							"WZJE")
							+ "");
					response.put("SUMWZJE", String.format("%1$.4f", WZJE)
							.toString());
				} else {
					response.put("SUMWZJE", "0.0000");
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
