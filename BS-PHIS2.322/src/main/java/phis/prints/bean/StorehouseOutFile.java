package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.dictionary.DictionaryController;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class StorehouseOutFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long yfsb = 0l;
		int ckfs = 0;
		int ckdh = 0;
		if (request.get("yfsb") != null) {
			yfsb = Long.parseLong(request.get("yfsb") + "");
		}
		if (request.get("ckfs") != null) {
			ckfs = Integer.parseInt(request.get("ckfs") + "");
		}
		if (request.get("ckdh") != null) {
			ckdh = Integer.parseInt(request.get("ckdh") + "");
		}
		parameter.put("YFSB", yfsb);
		parameter.put("CKFS", ckfs);
		parameter.put("CKDH", ckdh);
		/**
		 * 2013-08-20 modify by gejj 在sql中增加and a.YPCD = kc.YPCD and a.YPPH =
		 * kc.YPPH 按产地和批号分类
		 **/
		// String sql =
		// "select a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,a.YPXH as YPXH,a.JGID as JGID,"
		// +
		// "a.YPCD as YPCD,a.SFSL as SFSL,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,"
		// + "a.YPXQ as YPXQ,a.LSZZ as LSZZ,a.JHZZ as JHZZ,a.CDMC as CDMC,"
		// +
		// " nvl((select sum(kcsl) as kcsl from Yk_Kcmx kc where a.JGID = kc.JGID"
		// +
		// " and a.YPXH = kc.YPXH and a.YPCD = kc.YPCD and a.YPPH = kc.YPPH ), 0) as KCSL from  "
		// +
		// "(select yp.YPMC as YPMC,yp.YPGG as YPGG,yp.YPDW as YPDW,mx.YPXH as YPXH,ck.JGID as JGID,"
		// +
		// "mx.YPCD as YPCD,mx.SFSL as SFSL,mx.LSJG as LSJG,mx.JHJG as JHJG,mx.YPPH as YPPH,"
		// +
		// "mx.YPXQ as YPXQ,(mx.SFSL * mx.LSJG) as LSZZ,(mx.SFSL * mx.JHJG) as JHZZ,CD.CDMC as CDMC"
		// +
		// " from YK_CK01 ck, YK_CK02 mx, YK_TYPK yp, YK_CDDZ CD where ck.XTSB = mx.XTSB"
		// +
		// " and ck.CKFS = mx.CKFS and ck.CKDH = mx.CKDH and mx.YPXH = yp.YPXH  and CD.YPCD = mx.YPCD"
		// + " and ck.YFSB =:YFSB and ck.CKFS =:CKFS and ck.CKDH =:CKDH) a ";
		String sql = "select mx.KCSB as KCSB, mx.XTSB as XTSB, yp.YPMC as YPMC,yp.YPGG as YPGG,yp.YPDW as YPDW,mx.YPXH as YPXH,ck.JGID as JGID,"
				+ "mx.YPCD as YPCD,mx.SFSL as SFSL,mx.LSJG as LSJG,mx.JHJG as JHJG,NVL(mx.YPPH,'') as YPPH,"
				+ "NVL(mx.YPXQ,'') as YPXQ,(mx.SFSL * mx.LSJG) as LSZZ,(mx.SFSL * mx.JHJG) as JHZZ,CD.CDMC as CDMC"
				+ " from YK_CK01 ck, YK_CK02 mx, YK_TYPK yp, YK_CDDZ CD where ck.XTSB = mx.XTSB"
				+ " and ck.CKFS = mx.CKFS and ck.CKDH = mx.CKDH and mx.YPXH = yp.YPXH  and CD.YPCD = mx.YPCD"
				+ " and ck.YFSB =:YFSB and ck.CKFS =:CKFS and ck.CKDH =:CKDH ";
		String sql2 = "";
		Map<String, Object> parameter2 = new HashMap<String, Object>();
		Map<String, Object> resultMap = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doSqlQuery(sql, parameter);
			/** 重新遍历查询结果，计算库存数量 **/
			for (Map<String, Object> map : rklist) {
				if (Long.parseLong(map.get("KCSB") + "") == 0) {
					sql2 = "select sum(KCSL) as KCSL from YK_KCMX where YPXH=:YPXH and YPCD=:YPCD and JGID=:jgid ";
					parameter2.clear();
					parameter2
							.put("YPXH", Long.parseLong(map.get("YPXH") + ""));
					parameter2
							.put("YPCD", Long.parseLong(map.get("YPCD") + ""));
					parameter2.put("jgid", map.get("JGID") + "");
				} else {
					sql2 = "select sum(KCSL) as KCSL from YK_KCMX where YPXH=:YPXH and YPCD=:YPCD and JGID=:jgid";
//					sql2 = "select KCSL as KCSL from YK_KCMX where YPXH=:YPXH and YPCD=:YPCD and JGID=:jgid and SBXH=:KCSB";
					parameter2.clear();
					parameter2
							.put("YPXH", Long.parseLong(map.get("YPXH") + ""));
					parameter2
							.put("YPCD", Long.parseLong(map.get("YPCD") + ""));
					parameter2.put("jgid", map.get("JGID") + "");
//					parameter2
//							.put("KCSB", Long.parseLong(map.get("KCSB") + ""));
				}
				resultMap = dao.doLoad(sql2, parameter2);
				map.remove("XTSB");
				map.remove("KCSB");
				map.put("KCSL",
						resultMap == null ? 0
								: resultMap.get("KCSL") == null ? 0 : resultMap
										.get("KCSL"));
			}
			// 先放记录集
			double LSJE_F = 0.0;
			double JHJE_F = 0.0;
			// int culNum = 16;// 每页显示多少行
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();
			List<Map<String, Object>> clList = new ArrayList<Map<String, Object>>();
			for (int i = 1; i < rklist.size() + 1; i++) {
				Map<String, Object> map = rklist.get(i - 1);
				map.put("XH", i);
				clList.add(map);
			}
			rklist = clList;
			// int pagNum = rklist.size() / culNum;// 总页数
			// if (rklist.size() % culNum != 0||rklist.size()==0) {
			// for (int i = 0; i < culNum; i++) {
			// Map<String, Object> parMAP = new HashMap<String, Object>();
			// parMAP.put("XH", "");
			// parMAP.put("YPMC", "");
			// parMAP.put("YPGG", "");
			// parMAP.put("YPDW", "");
			// parMAP.put("YPXH", "");
			// parMAP.put("JGID", "");
			// parMAP.put("YPCD", "");
			// parMAP.put("SFSL", "");
			// parMAP.put("LSJG", "");
			// parMAP.put("JHJG", "");
			// parMAP.put("YPPH", "");
			// parMAP.put("YPXQ", "");
			// parMAP.put("LSZZ", "");
			// parMAP.put("JHZZ", "");
			// parMAP.put("CDMC", "");
			// parMAP.put("kcsl", "");
			// rklist.add(parMAP);
			// if (rklist.size() % culNum == 0) {
			// break;
			// }
			// }
			// }
			for (int i = 0; i < rklist.size(); i++) {
				if (null != rklist.get(i).get("JHJG")
						&& !"".equals(rklist.get(i).get("JHJG"))) {
					rklist.get(i).put("JHJG",
							String.format("%1$.4f", rklist.get(i).get("JHJG")));
				}
				if (null != rklist.get(i).get("LSJG")
						&& !"".equals(rklist.get(i).get("LSJG"))) {
					rklist.get(i).put("LSJG",
							String.format("%1$.4f", rklist.get(i).get("LSJG")));
				}
				if (null != rklist.get(i).get("JHZZ")
						&& !"".equals(rklist.get(i).get("JHZZ"))) {
					rklist.get(i).put("JHZZ",
							String.format("%1$.4f", rklist.get(i).get("JHZZ")));
				}
				if (null != rklist.get(i).get("LSZZ")
						&& !"".equals(rklist.get(i).get("LSZZ"))) {
					rklist.get(i).put("LSZZ",
							String.format("%1$.4f", rklist.get(i).get("LSZZ")));
				}
				if (null != rklist.get(i).get("JHZZ")
						&& !"".equals(rklist.get(i).get("JHZZ"))) {
					JHJE_F += Double
							.parseDouble(rklist.get(i).get("JHZZ") + "");
				}
				if (null != rklist.get(i).get("LSZZ")
						&& !"".equals(rklist.get(i).get("LSZZ"))) {
					LSJE_F += Double
							.parseDouble(rklist.get(i).get("LSZZ") + "");
				}
				if (null != rklist.get(i).get("YPXQ")
						&& !"".equals(rklist.get(i).get("YPXQ"))) {
					rklist.get(i).put(
							"YPXQ",
							sdf.format(BSHISUtil.toDate(rklist.get(i).get(
									"YPXQ")
									+ "")));
				}
				// if (rklist.size() > culNum) {
				// if ((i + 1) % culNum == 0) {
				// rklist.get(i).put("BYMRJE",
				// "本页进货金额：" + String.format("%1$.4f", JHJE_F));
				// rklist.get(i).put("BYLSJE",
				// "本页零售金额：" + String.format("%1$.4f", LSJE_F));
				// rklist.get(i).put(
				// "BYGLC",
				// "本页购零差金额："
				// + String.format("%1$.4f",
				// (LSJE_F - JHJE_F)));
				// LSJE_F = 0.0;
				// JHJE_F = 0.0;
				// }
				// }
				records.add(rklist.get(i));
			}
			// for (int i = pagNum * culNum; i < rklist.size(); i++) {
			// if (null != rklist.get(i).get("JHJG")
			// && !"".equals(rklist.get(i).get("JHJG"))) {
			// rklist.get(i).put("JHJG",
			// String.format("%1$.4f", rklist.get(i).get("JHJG")));
			// }
			// if (null != rklist.get(i).get("LSJG")
			// && !"".equals(rklist.get(i).get("LSJG"))) {
			// rklist.get(i).put("LSJG",
			// String.format("%1$.4f", rklist.get(i).get("LSJG")));
			// }
			// if (null != rklist.get(i).get("JHZZ")
			// && !"".equals(rklist.get(i).get("JHZZ"))) {
			// rklist.get(i).put("JHZZ",
			// String.format("%1$.4f", rklist.get(i).get("JHZZ")));
			// }
			// if (null != rklist.get(i).get("LSZZ")
			// && !"".equals(rklist.get(i).get("LSZZ"))) {
			// rklist.get(i).put("LSZZ",
			// String.format("%1$.4f", rklist.get(i).get("LSZZ")));
			// }
			// if (null != rklist.get(i).get("JHZZ")
			// && !"".equals(rklist.get(i).get("JHZZ"))) {
			// JHJE_F += Double
			// .parseDouble(rklist.get(i).get("JHZZ") + "");
			// }
			// if (null != rklist.get(i).get("LSZZ")
			// && !"".equals(rklist.get(i).get("LSZZ"))) {
			// LSJE_F += Double
			// .parseDouble(rklist.get(i).get("LSZZ") + "");
			// }
			// if (null != rklist.get(i).get("YPXQ")
			// && !"".equals(rklist.get(i).get("YPXQ"))) {
			// rklist.get(i).put(
			// "YPXQ",
			// sdf.format(BSHISUtil.toDate(rklist.get(i).get(
			// "YPXQ")
			// + "")));
			// }
			// if (i == (rklist.size() - 1)) {
			// rklist.get(i).put("BYMRJE",
			// "本页进货金额：" + String.format("%1$.4f", JHJE_F));
			// rklist.get(i).put("BYLSJE",
			// "本页零售金额：" + String.format("%1$.4f", LSJE_F));
			// rklist.get(i)
			// .put("BYGLC",
			// "本页购零差金额："
			// + String.format("%1$.4f",
			// (LSJE_F - JHJE_F)));
			// LSJE_F = 0.0;
			// JHJE_F = 0.0;
			// }
			// records.add(rklist.get(i));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Long yfsb = 0l;
		int ckfs = 0;
		int ckdh = 0;
		Long ckks = 0L;
		if (request.get("yfsb") != null) {
			yfsb = Long.parseLong(request.get("yfsb") + "");
		}
		if (request.get("ckfs") != null) {
			ckfs = Integer.parseInt(request.get("ckfs") + "");
		}
		if (request.get("ckdh") != null) {
			ckdh = Integer.parseInt(request.get("ckdh") + "");
		}
		if (request.get("ckks") != null) {
			ckks = Long.parseLong(request.get("ckks") + "");
		}
		parameters.put("YFSB", yfsb);
		parameters.put("CKFS", ckfs);
		parameters.put("CKDH", ckdh);
		// System.out.println("-------------------" + request
		// + "------------------");
		String sql = "select ck.SQRQ as SQRQ,ck.CKKS as CKKS from YK_CK01 ck where ck.YFSB=:YFSB and ck.CKFS=:CKFS and ck.CKDH=:CKDH";
		String ckmxsql = "select yp.YPMC as YPMC,yp.YPGG as YPGG,yp.YPDW as YPDW,mx.YPCD as YPCD,"
				+ "mx.SFSL as SFSL,mx.LSJG as LSJG,mx.JHJG as JHJG,NVL(mx.YPPH,'') as YPPH,NVL(mx.YPXQ,'') as YPXQ,"
				+ "(mx.SFSL * mx.LSJG) as LSZZ,(mx.SFSL * mx.JHJG) as JHZZ"
				+ " from YK_CK01 ck, YK_CK02 mx, YK_TYPK yp where ck.XTSB = mx.XTSB"
				+ " and ck.CKFS = mx.CKFS and ck.CKDH = mx.CKDH and mx.YPXH = yp.YPXH and"
				+ " ck.YFSB =:YFSB and ck.CKFS =:CKFS and ck.CKDH =:CKDH";
		try {
			List<Map<String, Object>> resList = dao
					.doQuery(ckmxsql, parameters);
			double mrjehj = 0.00;// 买入金额合计
			double lsjehj = 0.00;// 零售金额合计

			for (int i = 0; i < resList.size(); i++) {
				mrjehj += Double.parseDouble(resList.get(i).get("JHZZ") + "");
				lsjehj += Double.parseDouble(resList.get(i).get("LSZZ") + "");
			}
			// 参数
			Map<String, Object> cfmap = dao.doLoad(sql, parameters);
			String ksmc = "";
			String ckfName = "";
			try {
				ckfName = DictionaryController.instance()
						.get("phis.dictionary.storehouseDelivery")
						.getText(ckfs + "");
				ksmc = DictionaryController.instance()
						.get("phis.dictionary.department").getText(ckks + "");
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgname = user.getManageUnitName();
			String username = user.getUserName();
			response.put("TITLE", jgname + "药品出库单");
			response.put("ZBRQ", BSHISUtil.getDate());
			response.put("CKKS", ksmc);
			response.put("ZDR", username);
			response.put("CKFS", ckfName);
			if (cfmap != null) {
				if (cfmap.get("SQRQ") != null) {
					response.put("SQRQ", sfm.format(cfmap.get("SQRQ")));
				} else {
					response.put("SQRQ", "");
				}
			}
			response.put("CKDH", ckdh + "");
			response.put("MRJEHJ", String.format("%1$.4f", mrjehj));
			response.put("XSJEHJ", String.format("%1$.4f", lsjehj));
			response.put("GLCHJ", String.format("%1$.4f", (lsjehj - mrjehj)));
			// System.out.println("---------------------");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
