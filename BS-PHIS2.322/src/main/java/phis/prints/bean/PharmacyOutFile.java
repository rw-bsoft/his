package phis.prints.bean; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PharmacyOutFile implements IHandler {
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
		String sql = "select yp.YPMC as YPMC, yp.YPGG as YPGG,yp.YPDW as YPDW,mx.CKSL as CKSL,mx.LSJG as LSJG,mx.JHJG as JHJG,mx.YPPH as YPPH,mx.YPXQ as YPXQ,kc.YPSL as YPSL,(mx.CKSL*mx.LSJG) as LSZZ,(mx.CKSL*mx.JHJG ) as JHZZ,CD.CDMC as YPCD from YF_CK01 ck, YF_CK02 mx left join YF_KCMX kc on mx.KCSB = kc.SBXH, YK_TYPK yp ,YK_CDDZ CD where ck.YFSB = mx.YFSB and ck.CKFS = mx.CKFS and ck.CKDH = mx.CKDH and mx.YPXH=yp.YPXH  and CD.YPCD = mx.YPCD and ck.YFSB=:YFSB and ck.CKFS=:CKFS and ck.CKDH=:CKDH";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doSqlQuery(sql, parameter);
			List<Map<String, Object>> clList = new ArrayList<Map<String, Object>>();
			for (int i = 1; i < rklist.size() + 1; i++) {
				Map<String, Object> map = rklist.get(i - 1);
				map.put("XH", i);
				clList.add(map);
			}
			rklist = clList;
			// 先放记录集
			double LSJE_F = 0.0;
			double JHJE_F = 0.0;
//			int culNum = 16;// 每页显示多少行
//			int pagNum = rklist.size() / culNum;// 总页数
//			if (rklist.size() % culNum != 0||rklist.size()==0) {
//				for (int i = 0; i < culNum; i++) {
//					Map<String, Object> parMAP = new HashMap<String, Object>();
//					parMAP.put("XH", "");
//					parMAP.put("YPMC", "");
//					parMAP.put("YPGG", "");
//					parMAP.put("YPDW", "");
//					parMAP.put("CKSL", "");
//					parMAP.put("LSJG", "");
//					parMAP.put("JHJG", "");
//					parMAP.put("YPPH", "");
//					parMAP.put("YPXQ", "");
//					parMAP.put("YPSL", "");
//					parMAP.put("LSZZ", "");
//					parMAP.put("JHZZ", "");
//					parMAP.put("YPCD", "");
//					rklist.add(parMAP);
//					if (rklist.size() % culNum == 0) {
//						break;
//					}
//				}
//			}
			for (int i = 0; i < rklist.size(); i++) {
				if (rklist.get(i).get("YPSL") == null) {
					rklist.get(i).put("YPSL", "0");
				}
				if (null != rklist.get(i).get("JHJG")
						&& !"".equals(rklist.get(i).get("JHJG"))) {
					rklist.get(i).put("JHJG",
							String.format("%1$.2f", rklist.get(i).get("JHJG")));
				}
				if (null != rklist.get(i).get("LSJG")
						&& !"".equals(rklist.get(i).get("LSJG"))) {
					rklist.get(i).put("LSJG",
							String.format("%1$.2f", rklist.get(i).get("LSJG")));
				}
				if (null != rklist.get(i).get("JHZZ")
						&& !"".equals(rklist.get(i).get("JHZZ"))) {
					rklist.get(i).put("JHZZ",
							String.format("%1$.2f", rklist.get(i).get("JHZZ")));
				}
				if (null != rklist.get(i).get("LSZZ")
						&& !"".equals(rklist.get(i).get("LSZZ"))) {
					rklist.get(i).put("LSZZ",
							String.format("%1$.2f", rklist.get(i).get("LSZZ")));
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
//				if (rklist.size() > culNum) {
//					if ((i + 1) % culNum == 0) {
//						rklist.get(i).put("BYMRJE",
//								"本页进货金额：" + String.format("%1$.2f", JHJE_F));
//						rklist.get(i).put("BYLSJE",
//								"本页零售金额：" + String.format("%1$.2f", LSJE_F));
//						rklist.get(i).put(
//								"BYGLC",
//								"本页购零差金额："
//										+ String.format("%1$.2f",
//												(LSJE_F - JHJE_F)));
//						LSJE_F = 0.0;
//						JHJE_F = 0.0;
//					}
//				}
				records.add(rklist.get(i));
			}
//			for (int i = pagNum * culNum; i < rklist.size(); i++) {
//				if (rklist.get(i).get("YPSL") == null) {
//					rklist.get(i).put("YPSL", "0");
//				}
//				if (null != rklist.get(i).get("JHJG")
//						&& !"".equals(rklist.get(i).get("JHJG"))) {
//					rklist.get(i).put("JHJG",
//							String.format("%1$.2f", rklist.get(i).get("JHJG")));
//				}
//				if (null != rklist.get(i).get("LSJG")
//						&& !"".equals(rklist.get(i).get("LSJG"))) {
//					rklist.get(i).put("LSJG",
//							String.format("%1$.2f", rklist.get(i).get("LSJG")));
//				}
//				if (null != rklist.get(i).get("JHZZ")
//						&& !"".equals(rklist.get(i).get("JHZZ"))) {
//					rklist.get(i).put("JHZZ",
//							String.format("%1$.2f", rklist.get(i).get("JHZZ")));
//				}
//				if (null != rklist.get(i).get("LSZZ")
//						&& !"".equals(rklist.get(i).get("LSZZ"))) {
//					rklist.get(i).put("LSZZ",
//							String.format("%1$.2f", rklist.get(i).get("LSZZ")));
//				}
//				if (null != rklist.get(i).get("JHZZ")
//						&& !"".equals(rklist.get(i).get("JHZZ"))) {
//					JHJE_F += Double
//							.parseDouble(rklist.get(i).get("JHZZ") + "");
//				}
//				if (null != rklist.get(i).get("LSZZ")
//						&& !"".equals(rklist.get(i).get("LSZZ"))) {
//					LSJE_F += Double
//							.parseDouble(rklist.get(i).get("LSZZ") + "");
//				}
//				if (null != rklist.get(i).get("YPXQ")
//						&& !"".equals(rklist.get(i).get("YPXQ"))) {
//					rklist.get(i).put(
//							"YPXQ",
//							sdf.format(BSHISUtil.toDate(rklist.get(i).get(
//									"YPXQ")
//									+ "")));
//				}
//				if (i == (rklist.size() - 1)) {
//					rklist.get(i).put("BYMRJE",
//							"本页进货金额：" + String.format("%1$.2f", JHJE_F));
//					rklist.get(i).put("BYLSJE",
//							"本页零售金额：" + String.format("%1$.2f", LSJE_F));
//					rklist.get(i)
//							.put("BYGLC",
//									"本页购零差金额："
//											+ String.format("%1$.2f",
//													(LSJE_F - JHJE_F)));
//					LSJE_F = 0.0;
//					JHJE_F = 0.0;
//				}
//				records.add(rklist.get(i));
//			}
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
		if (request.get("yfsb") != null) {
			yfsb = Long.parseLong(request.get("yfsb") + "");
		}
		if (request.get("ckfs") != null) {
			ckfs = Integer.parseInt(request.get("ckfs") + "");
		}
		if (request.get("ckdh") != null) {
			ckdh = Integer.parseInt(request.get("ckdh") + "");
		}
		parameters.put("YFSB", yfsb);
		parameters.put("CKFS", ckfs);
		parameters.put("CKDH", ckdh);
		String sql = "select ck.CKRQ as CKRQ,ck.KSDM as KSDM,ck.CKBZ as BZ from YF_CK01 ck where ck.YFSB=:YFSB and ck.CKFS=:CKFS and ck.CKDH=:CKDH";
		String ckmxsql = "select yp.YPMC as YPMC, yp.YPGG as YPGG,yp.YPDW as YPDW,mx.CKSL as CKSL,mx.LSJG as LSJG,mx.JHJG as JHJG,mx.YPPH as YPPH,mx.YPXQ as YPXQ,kc.YPSL as YPSL,(mx.CKSL*mx.LSJG) as LSZZ,(mx.CKSL*mx.JHJG ) as JHZZ,CD.CDMC as YPCD from YF_CK01 ck, YF_CK02 mx left join YF_KCMX kc on mx.KCSB = kc.SBXH,YK_TYPK yp ,YK_CDDZ CD where ck.YFSB = mx.YFSB and ck.CKFS = mx.CKFS and ck.CKDH = mx.CKDH and mx.YPXH=yp.YPXH  and CD.YPCD = mx.YPCD and ck.YFSB=:YFSB and ck.CKFS=:CKFS and ck.CKDH=:CKDH";
		try {
			List<Map<String, Object>> resList = dao.doSqlQuery(ckmxsql,
					parameters);
			double mrjehj = 0.00;// 买入金额合计
			double lsjehj = 0.00;// 零售金额合计

			for (int i = 0; i < resList.size(); i++) {
				mrjehj += Double.parseDouble(resList.get(i).get("JHZZ") + "");
				lsjehj += Double.parseDouble(resList.get(i).get("LSZZ") + "");
			}
			// 参数
			Map<String, Object> cfmap = dao.doLoad(sql, parameters);
			String ckfName = DictionaryController.instance().getDic("phis.dictionary.drugDelivery")
					.getText(ckfs + "");

			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			String username = user.getUserName();
			String jgname = user.getManageUnit().getName();
			String BZ = "";
			if (cfmap.get("BZ") != null) {
				BZ = cfmap.get("BZ") + "";
			}
			response.put("TITLE", jgname + "药品出库单");
			response.put("CKFS", ckfName);
			response.put("BZ", BZ);
			response.put("ZDR", username);
			response.put("CKRQ", sfm.format(cfmap.get("CKRQ")));
			response.put("CKDH", ckdh + "");
			response.put("MRJEHJ", String.format("%1$.2f", mrjehj));
			response.put("XSJEHJ", String.format("%1$.2f", lsjehj));
			response.put("GLCHJ", String.format("%1$.2f", (lsjehj - mrjehj)));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
