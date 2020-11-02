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

public class LibraryEjSummaryFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		BaseDAO dao = new BaseDAO(ctx);
		String jgid = user.getManageUnitId();// 用户的机构ID
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		try {
			if (kfxh == 0) {
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
						kfxh = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			Long ksdm = 0L;
			if (request.get("ksdm") != null && request.get("ksdm") != "") {
				ksdm = Long.parseLong(request.get("ksdm") + "");
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
			parameters.put("ldt_qsrq", dateForm);
			parameters.put("ldt_jsrq", dateTo);
			StringBuffer sql = new StringBuffer(
					"SELECT WZZD.WZMC as WZMC,WZZD.WZGG as WZGG,WZZD.WZDW as WZDW,SUM(A.WZSL) as WZSL,SUM(A.WZJE) as WZJE,A.KSDM as KSDM,A.KSMC as KSMC FROM WL_WZZD WZZD,(select ck02.JLXH as JLXH,ck02.WZXH as WZXH,ck01.KSDM as KSDM,kdm.OFFICENAME as KSMC,lzfs.YWLB*ck02.WZSL*-1 as WZSL,lzfs.YWLB*ck02.WZJE*-1 as WZJE from WL_CK01 ck01 LEFT OUTER JOIN SYS_Office kdm ON ck01.KSDM = kdm.ID,WL_CK02 ck02,WL_LZFS lzfs where ck01.DJXH = ck02.DJXH and ck01.DJZT >= 2 and ck01.KFXH = :KFXH");
			if (ksdm != 0) {
				parameters.put("KSDM", ksdm);
				sql.append(" and ck01.KSDM=:KSDM");
			}
			sql.append(" and ck01.JZRQ >= :ldt_qsrq and ck01.JZRQ <= :ldt_jsrq and lzfs.FSXH = ck01.LZFS) A Where A.WZXH = WZZD.WZXH GROUP BY WZZD.WZMC,WZZD.WZGG,WZZD.WZDW,A.KSDM,A.KSMC ORDER BY A.KSDM,WZZD.WZMC,WZZD.WZGG");
			List<Map<String, Object>> cflist = dao.doSqlQuery(sql.toString(),
					parameters);
			for (int i = 0; i < cflist.size(); i++) {
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
		BaseDAO dao = new BaseDAO(ctx);
		String jgname = user.getManageUnitName();
		String jgid = user.getManageUnitId();// 用户的机构ID
		int kfxhsum = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxhsum = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		try {
			if (kfxhsum == 0) {
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
						kfxhsum = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			String kfmc = "";
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
			Long ksdm = 0L;
			if (request.get("ksdm") != null && request.get("ksdm") != "") {
				ksdm = Long.parseLong(request.get("ksdm") + "");
			}
			String dateFormstr = "";
			if (request.get("dateForm") != null) {
				dateFormstr = request.get("dateForm") + "";
			}
			String dateTostr = "";
			if (request.get("dateTo") != null) {
				dateTostr = request.get("dateTo") + "";
			}
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
			parameterssum.put("ldt_qsrq", dateForm);
			parameterssum.put("ldt_jsrq", dateTo);
			StringBuffer sqlsum = new StringBuffer(
					"SELECT SUM(A.WZSL) as WZSL,SUM(A.WZJE) as WZJE FROM WL_WZZD WZZD,(select ck02.JLXH as JLXH,ck02.WZXH as WZXH,ck01.KSDM as KSDM,kdm.OFFICENAME as KSMC,lzfs.YWLB*ck02.WZSL*-1 as WZSL,lzfs.YWLB*ck02.WZJE*-1 as WZJE from WL_CK01 ck01 LEFT OUTER JOIN SYS_Office kdm ON ck01.KSDM = kdm.ID,WL_CK02 ck02,WL_LZFS lzfs where ck01.DJXH = ck02.DJXH and ck01.DJZT >= 2 and ck01.KFXH = :KFXH");
			if (ksdm != 0) {
				parameterssum.put("KSDM", ksdm);
				sqlsum.append(" and ck01.KSDM=:KSDM");
			}
			sqlsum.append(" and ck01.JZRQ >= :ldt_qsrq and ck01.JZRQ <= :ldt_jsrq and lzfs.FSXH = ck01.LZFS) A Where A.WZXH = WZZD.WZXH");
			List<Map<String, Object>> cflistsum = dao.doSqlQuery(
					sqlsum.toString(), parameterssum);
			response.put("title", jgname);
			response.put("tagName", kfmc + "出库汇总报表");
			response.put("dataForm", dateFormstr);
			response.put("dateTo", dateTostr);
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
