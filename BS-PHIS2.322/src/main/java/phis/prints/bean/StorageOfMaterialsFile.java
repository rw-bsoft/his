package phis.prints.bean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;


import ctd.util.context.Context;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
//DynamicPrint
public class StorageOfMaterialsFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") + "");
		}
		parameter.put("DJXH", djxh);
		String sql = "select substring(b.WZMC,0,9) as WZMC,trim(b.WZGG) as WZGG,trim(b.WZDW) as WZDW,a.WZSL as WZSL,"
				+ " a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.SXRQ as SXRQ,c.CJMC as SCCJ"
				+ " from WL_RK02 a, WL_WZZD b,WL_SCCJ c "
				+ " where a.DJXH =:DJXH and a.WZXH = b.WZXH and a.CJXH =c.CJXH";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			// 先放记录集
			double HJJE = 0.0;
			double HJLSJE = 0.0;
			double CJ=0.0;
			// 每页显示多少行
			int culNum = 16;
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			if (JGID.equals("360121101")) {
				culNum = 7;
			}
			if (rklist.size() % culNum != 0) {
				for (int i = 0; i < culNum; i++) {
					Map<String, Object> rkMAP = new HashMap<String, Object>();
					rkMAP.put("WZMC", "");
					rkMAP.put("WZGG", "");
					rkMAP.put("WZDW", "");
					rkMAP.put("WZSL", "");
					rkMAP.put("WZJG", "");
					rkMAP.put("WZJE", "");
					rkMAP.put("LSJG", "");
					rkMAP.put("LSJE", "");
					rkMAP.put("WZPH", "");
					rkMAP.put("SXRQ", "");
					rkMAP.put("SCCJ", "");
					rklist.add(rkMAP);
					if (rklist.size() % culNum == 0) {
						break;
					}
				}
			}
			// 总页数
			int pagNum = rklist.size() / culNum;
			for (int i = 0; i < pagNum * culNum; i++) {
				if (rklist.get(i).get("WZSL") != null
						&& rklist.get(i).get("WZSL") != "") {
					rklist.get(i).put("WZSL",
							String.format("%1$.0f", rklist.get(i).get("WZSL")));
				}
				if (rklist.get(i).get("WZJG") != null
						&& rklist.get(i).get("WZJG") != "") {
					rklist.get(i).put("WZJG",
							String.format("%1$.2f", rklist.get(i).get("WZJG")));
				}
				if (rklist.get(i).get("WZJE") != null
						&& rklist.get(i).get("WZJE") != "") {
					rklist.get(i).put("WZJE",
							String.format("%1$.2f", rklist.get(i).get("WZJE")));
					HJJE += Double.parseDouble(rklist.get(i).get("WZJE") + "");
				}
				if (rklist.get(i).get("LSJG") != null
						&& rklist.get(i).get("LSJG") != "") {
					rklist.get(i).put("LSJG",
							String.format("%1$.2f", rklist.get(i).get("LSJG")));
				}
				if (rklist.get(i).get("LSJE") != null
						&& rklist.get(i).get("LSJE") != "") {
					rklist.get(i).put("LSJE",
							String.format("%1$.2f", rklist.get(i).get("LSJE")));
					HJLSJE += Double
							.parseDouble(rklist.get(i).get("LSJE") + "");
				}
				if (null != rklist.get(i).get("SXRQ")
						&& "" != rklist.get(i).get("SXRQ")) {
					rklist.get(i).put(
							"SXRQ",
							sdf.format(BSHISUtil.toDate(rklist.get(i).get(
									"SXRQ")
									+ "")));
				}
				if ((i + 1) % culNum == 0) {
					rklist.get(i).put("HJJE",
							"本页购进金额合计：" + String.format("%1$.2f", HJJE));
					rklist.get(i).put("HJLSJE",
							"本页零售金额合计：" + String.format("%1$.2f", HJLSJE));
					rklist.get(i).put("CJ",
							"本页差价合计：" + String.format("%1$.2f", HJLSJE-HJJE));
					CJ = 0.0;
					HJJE = 0.0;
					HJLSJE = 0.0;
				}
				records.add(rklist.get(i));
			}
			for (int i = pagNum * culNum; i < rklist.size(); i++) {
				if (rklist.get(i).get("WZSL") != null
						&& rklist.get(i).get("WZSL") != "") {
					rklist.get(i).put("WZSL",
							String.format("%1$.2f", rklist.get(i).get("WZSL")));
				}
				if (rklist.get(i).get("WZJG") != null
						&& rklist.get(i).get("WZJG") != "") {
					rklist.get(i).put("WZJG",
							String.format("%1$.2f", rklist.get(i).get("WZJG")));
				}
				if (rklist.get(i).get("WZJE") != null
						&& rklist.get(i).get("WZJE") != "") {
					rklist.get(i).put("WZJE",
							String.format("%1$.2f", rklist.get(i).get("WZJE")));
					HJJE += Double.parseDouble(rklist.get(i).get("WZJE") + "");
				}
				if (rklist.get(i).get("LSJE") != null
						&& rklist.get(i).get("LSJE") != "") {
					rklist.get(i).put("LSJE",
							String.format("%1$.2f", rklist.get(i).get("LSJE")));
					HJLSJE += Double
							.parseDouble(rklist.get(i).get("LSJE") + "");
				}
				if (null != rklist.get(i).get("SXRQ")
						&& "" != rklist.get(i).get("SXRQ")) {
					rklist.get(i).put(
							"SXRQ",
							sdf.format(BSHISUtil.toDate(rklist.get(i).get(
									"SXRQ")
									+ "")));
				}
				if (i == (rklist.size() - 1)) {
					rklist.get(i).put("HJJE",
							"本页购进金额合计：" + String.format("%1$.2f", HJJE));
					rklist.get(i).put("HJLSJE",
							"本页零售金额合计：" + String.format("%1$.2f", HJLSJE));
					rklist.get(i).put("CJ",
							"本页差价合计：" + String.format("%1$.2f", (HJLSJE-HJJE)));
					CJ = 0.0;
					HJJE = 0.0;
					HJLSJE = 0.0;

				}
				records.add(rklist.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long djxh = 0l;
		if (request.get("djxh") != null) {
			djxh = Long.parseLong(request.get("djxh") + "");
		}
		parameter.put("DJXH", djxh);
		String sql = "select a.DJJE as DJJE,(select trim(b.DWMC) from  WL_GHDW b where b.DWXH = a.DWXH)as DWMC,"
				+ " c.FSMC as LZFS,a.RKRQ as RKRQ,a.DJBZ as BZ,a.LZDH as LZDH,"
				+ " (select d.PERSONNAME  from SYS_Personnel d where d.PERSONID=a.ZDGH ) as ZDR,"
				+ " (select d.PERSONNAME  from SYS_Personnel d where d.PERSONID=a.SHGH ) as SHR,"
				+ " (select d.PERSONNAME  from SYS_Personnel d where d.PERSONID=a.JZGH ) as JZR "
				+ " from WL_RK01 a,WL_LZFS c where a.DJXH=:DJXH and a.LZFS=c.FSXH";
		double ZJE = 0.0;
		DecimalFormat df = new DecimalFormat("#0.00");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			for (int i = 0; i < rklist.size(); i++) {
				ZJE += Double.parseDouble(rklist.get(i).get("DJJE") + "");
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgname = user.getManageUnit().getName();
			response.put("TITLE", jgname + "物资入库单");
			if (null != rklist.get(0).get("LZFS")) {
				response.put("LZFS", rklist.get(0).get("LZFS") + "");
			}
			if (null != rklist.get(0).get("LZDH")) {
				response.put("LZDH", rklist.get(0).get("LZDH") + "");
			}
			if (null != rklist.get(0).get("DWMC")) {
				response.put("GHDW", rklist.get(0).get("DWMC") + "");
			}
			response.put("RKRQ", sdf.format(BSHISUtil.toDate(rklist.get(0).get(
					"RKRQ")
					+ "")));
			if (null != rklist.get(0).get("BZ")) {
				response.put("BZ", rklist.get(0).get("BZ") + "");
			}
			response.put("ZJE", df.format(ZJE));
			if (null != rklist.get(0).get("BZ")) {
				response.put("BZ", rklist.get(0).get("BZ") + "");
			}
			if (null != rklist.get(0).get("ZDR")) {
				response.put("ZDR", rklist.get(0).get("ZDR") + "");
			}
			if (null != rklist.get(0).get("SHR")) {
				response.put("SHR", rklist.get(0).get("SHR") + "");
			}
			if (null != rklist.get(0).get("JZR")) {
				response.put("JZR", rklist.get(0).get("JZR") + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
