package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;



public class SuppliesOutFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long DJXH = 0l;
		if (request.get("DJXH") != null) {
			DJXH = Long.parseLong(request.get("DJXH") + "");
		}
		parameter.put("DJXH", DJXH);
		String sql = "select substr(t2.WZMC,0,9) as WZMC,trim(t2.WZGG) as WZGG,trim(t2.WZDW) as WZDW,t3.CJMC as SCCJ,t1.SLSL as SLSL,t1.WZSL as WZSL,t1.WZJG as WZJG ,t1.WZJE as WZJE,t1.LSJG as LSJG,t1.LSJE as LSJE,t1.WZPH as WZPH,t1.SXRQ as SXRQ from WL_CK02 t1,WL_WZZD t2,WL_SCCJ t3 where t1.wzxh = t2.wzxh and t1.cjxh =t3.cjxh and t1.djxh =:DJXH";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doSqlQuery(sql, parameter);
			// 先放记录集
			double HJJE = 0.0;
			double HJLSJE = 0.0;
			//double CJ = HJJE - HJLSJE;
			int culNum = 16;// 每页显示多少行
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
					rkMAP.put("SLSL", "");
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
			int pagNum = rklist.size() / culNum;// 总页数
			for (int i = 0; i < pagNum * culNum; i++) {
				if (rklist.get(i).get("WZSL") == null) {
					rklist.get(i).put("WZSL", "0.00");
				}
				if (rklist.get(i).get("SLSL") == null) {
					rklist.get(i).put("SLSL", "0");
				}
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
							"本页购进金额合计：  " + String.format("%1$.2f", HJJE));
					rklist.get(i).put("HJLSJE",
							"本页零售金额合计：  " + String.format("%1$.2f", HJLSJE));
					HJJE = 0.0;
					HJLSJE = 0.0;
				}
				records.add(rklist.get(i));
			}
			for (int i = pagNum * culNum; i < rklist.size(); i++) {
				if (rklist.get(i).get("WZSL") == null) {
					rklist.get(i).put("WZSL", "0");
				}
				if (rklist.get(i).get("SLSL") == null) {
					rklist.get(i).put("SLSL", "0");
				}
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
							"本页购进金额合计：  " + String.format("%1$.2f", HJJE));
					HJJE = 0.0;
					rklist.get(i).put("HJLSJE",
							"本页零售金额合计：  " + String.format("%1$.2f", HJLSJE));
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
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long DJXH = 0l;
		if (request.get("DJXH") != null) {
			DJXH = Long.parseLong(request.get("DJXH") + "");
		}
		parameters.put("DJXH", DJXH);
		String sql = "select t.LZDH as LZDH,t3.FSMC as LZFS ,t.CKRQ as CKRQ,t.DJBZ as DJBZ,(select t2.OFFICENAME as KSMC from SYS_Office t2 where t2.ID =t.KSDM)as CKKS,(select t2.PERSONNAME as YGXM from SYS_Personnel t2 where t2.PERSONID =t.ZDGH)as ZDGH ,(select t2.PERSONNAME as YGXM from SYS_Personnel t2 where t2.PERSONID =t.SHGH)as SHGH,(select t2.PERSONNAME as YGXM from SYS_Personnel t2 where t2.PERSONID =t.JZGH)as JZGH from WL_CK01 t ,WL_LZFS t3  where t.LZFS = t3.FSXH and t.DJXH =:DJXH";
		String ckmxsql = "select sum(t.wzje) as ZJE from WL_CK02 t where t.djxh =:DJXH";
		try {
			List<Map<String, Object>> resList = dao.doSqlQuery(ckmxsql,
					parameters);
			double ZJE = 0.00;// 零售金额合计

			for (int i = 0; i < resList.size(); i++) {
				if (resList.get(i).get("ZJE") != null) {
					ZJE += Double.parseDouble(resList.get(i).get("ZJE") + "");
				}
			}
			// 参数
			Map<String, Object> cfmap = dao.doLoad(sql, parameters);
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgname = user.getManageUnit().getName();
			String BZ = "";
			if (cfmap.get("DJBZ") != null) {
				BZ = cfmap.get("DJBZ") + "";
			}
			String LZDH = "";
			if (cfmap.get("LZDH") != null) {
				LZDH = cfmap.get("LZDH") + "";
			}
			String ZDR = "";
			if (cfmap.get("ZDGH") != null) {
				ZDR = cfmap.get("ZDGH") + "";
			}
			String SHR = "";
			if (cfmap.get("SHGH") != null) {
				SHR = cfmap.get("SHGH") + "";
			}
			String JZR = "";
			if (cfmap.get("JZGH") != null) {
				JZR = cfmap.get("JZGH") + "";
			}
			String CKKS = "";
			if (cfmap.get("CKKS") != null) {
				CKKS = cfmap.get("CKKS") + "";
			}
			response.put("TITLE", jgname + "物资出库单");
			response.put("CKFS", cfmap.get("DJBZ"));
			response.put("BZ", BZ);
			response.put("SHR", SHR);
			response.put("CKKS", CKKS);
			response.put("JZR", JZR);
			response.put("ZDR", ZDR);
			response.put("LZFS", cfmap.get("LZFS") + "");
			response.put("LZDH", LZDH);
			if (ZJE > 0) {
				response.put("ZJE", String.format("%1$.2f", ZJE));
			} else {
				response.put("ZJE", "0.00");
			}
			response.put("CKRQ", sfm.format(cfmap.get("CKRQ")));

			// 消耗明细增加病人姓名。按照目前的生成方式，生成出库单时要判断为同一个病人；这样单据打印出来才可以显示病人
			String brxmsql = "select a.BRID as BRID,a.BRXM as BRXM from WL_XHMX a where a.DJXH=:DJXH";
			List<Map<String, Object>> brList = dao.doSqlQuery(brxmsql,
					parameters);
			if (brList != null && brList.size() > 0) {
				String brxm = "";
				Map<String, Object> brxmMap = new HashMap<String, Object>();
				for (Map<String, Object> brMap : brList) {
					if (brMap.get("BRXM") != null && brMap.get("BRXM") != "") {
						brxm = brMap.get("BRXM") + "";
					}
					brxmMap.put(brMap.get("BRXM") + "", brMap.get("BRXM") + "");
				}
				if (brxmMap.size() == 1) {
					response.put("BRXM", "病人姓名:  " + brxm);
				}

			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
