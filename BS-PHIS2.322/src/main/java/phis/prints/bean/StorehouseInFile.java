package phis.prints.bean; 
import java.text.DecimalFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;  

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;

import ctd.account.UserRoleToken; 
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 

public class StorehouseInFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long xtsb = 0l;
		int rkfs = 0;
		int rkdh = 0;
		if (request.get("xtsb") != null) {
			xtsb = Long.parseLong(request.get("xtsb") + "");
		}
		if (request.get("rkfs") != null) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
		}
		if (request.get("rkdh") != null) {
			rkdh = Integer.parseInt(request.get("rkdh") + "");
		}
		parameter.put("XTSB", xtsb);
		parameter.put("RKFS", rkfs);
		parameter.put("RKDH", rkdh);
		String sql = "SELECT T.RKFS as RKFS,T.JHHJ as JHZZ,T.LSJE as LSZZ,T.LSJE-T.JHHJ as CJHJ,T.RKDH as RKDH,"
				+ "T.YPCD as YPCD,T2.YPGG as YPGG,T2.YPDW as YPDW,T.RKSL as RKSL,T.LSJG as LSJG ,T.JHJG as JHJG,"
				+ "T.LSJE as LSJE,T.JHHJ as JHHJ,T.YPPH as YPPH,T.YPXQ as YPXQ,substring(T2.YPMC,0,9) as YPMC,CD.CDMC as YPCD "
				+ "FROM YK_RK02 T,YK_TYPK T2,YK_CDDZ CD where T.YPXH = T2.YPXH and T.XTSB =:XTSB and T.RKFS =:RKFS "
				+ "and T.RKDH =:RKDH and CD.YPCD = T.YPCD";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			// 先放记录集
			double LSJE_F = 0.0;
			double JHHJ_F = 0.0;
			// 每页显示多少行
			//int culNum = 25;
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();
			
			List<Map<String, Object>> clList = new ArrayList<Map<String, Object>>();
			DecimalFormat df = new DecimalFormat("#0.0000");
			for (int i = 1; i < rklist.size() + 1; i++) {
				Map<String, Object> map = rklist.get(i - 1);
				map.put("XH", i);
				map.put("CJHJ",df.format(Double.parseDouble(map.get("CJHJ")+"")));
				if (map.get("LSJG") != null && map.get("JHJG") != null) {
					double cj = Double.parseDouble(map.get("LSJG") + "")
							- Double.parseDouble(map.get("JHJG") + "");
					map.put("YPCJ", String.format("%1$.4f", cj));
				}
				clList.add(map);
			}
			rklist = clList;
			// 总页数
//			int pagNum = rklist.size() / culNum;
//			if (rklist.size() % culNum != 0 || rklist.size() == 0) {
//				for (int i = 0; i < culNum; i++) {
//					Map<String, Object> parMAP = new HashMap<String, Object>();
//					parMAP.put("XH", "");
//					parMAP.put("RKFS", "");
//					parMAP.put("JHZZ", "");
//					parMAP.put("LSZZ", "");
//					parMAP.put("RKDH", "");
//					parMAP.put("YPCD", "");
//					parMAP.put("YPGG", "");
//					parMAP.put("YPDW", "");
//					parMAP.put("RKSL", "");
//					parMAP.put("LSJG", "");
//					parMAP.put("JHJG", "");
//					parMAP.put("LSJE", "");
//					parMAP.put("JHHJ", "");
//					parMAP.put("YPPH", "");
//					parMAP.put("YPXQ", "");
//					parMAP.put("YPMC", "");
//					parMAP.put("YPCD", "");
//					rklist.add(parMAP);
//					if (rklist.size() % culNum == 0) {
//						break;
//					}
//				}
//			}
			for (int i = 0; i < rklist.size(); i++) {
				if (null != rklist.get(i).get("RKSL")
						&& !"".equals(rklist.get(i).get("RKSL"))) {
					rklist.get(i).put("YPSL",
							rklist.get(i).get("RKSL"));
				}
				if (null != rklist.get(i).get("LSJG")
						&& !"".equals(rklist.get(i).get("LSJG"))) {
					rklist.get(i).put("LSJG",
							String.format("%1$.4f", rklist.get(i).get("LSJG")));
				}
				if (null != rklist.get(i).get("JHJG")
						&& !"".equals(rklist.get(i).get("JHJG"))) {
					rklist.get(i).put("JHJG",
							String.format("%1$.4f", rklist.get(i).get("JHJG")));
				}
				if (null != rklist.get(i).get("LSJE")
						&& !"".equals(rklist.get(i).get("LSJE"))) {
					rklist.get(i).put("LSJE",
							String.format("%1$.4f", rklist.get(i).get("LSJE")));
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
					JHHJ_F += Double
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
//						rklist.get(i).put("JHJEF",
//								"本页进货金额：" + String.format("%1$.4f", JHHJ_F));
//						rklist.get(i).put("LSJEF",
//								"本页零售金额：" + String.format("%1$.4f", LSJE_F));
//						rklist.get(i).put(
//								"GLCF",
//								"本页购零差金额："
//										+ String.format("%1$.4f",
//												(LSJE_F - JHHJ_F)));
//						LSJE_F = 0.0;
//						JHHJ_F = 0.0;
//					}
//
//				}
				records.add(rklist.get(i));
			}
//			for (int i = pagNum * culNum; i < rklist.size(); i++) {
//				if (null != rklist.get(i).get("RKSL")
//						&& !"".equals(rklist.get(i).get("RKSL"))) {
//					rklist.get(i).put("YPSL",
//							rklist.get(i).get("RKSL"));
//				}
//				if (null != rklist.get(i).get("LSJG")
//						&& !"".equals(rklist.get(i).get("LSJG"))) {
//					rklist.get(i).put("LSJG",
//							String.format("%1$.4f", rklist.get(i).get("LSJG")));
//				}
//				if (null != rklist.get(i).get("JHJG")
//						&& !"".equals(rklist.get(i).get("JHJG"))) {
//					rklist.get(i).put("JHJG",
//							String.format("%1$.4f", rklist.get(i).get("JHJG")));
//				}
//				if (null != rklist.get(i).get("LSJE")
//						&& !"".equals(rklist.get(i).get("LSJE"))) {
//					rklist.get(i).put("LSJE",
//							String.format("%1$.4f", rklist.get(i).get("LSJE")));
//				}
//				if (null != rklist.get(i).get("JHZZ")
//						&& !"".equals(rklist.get(i).get("JHZZ"))) {
//					rklist.get(i).put("JHZZ",
//							String.format("%1$.4f", rklist.get(i).get("JHZZ")));
//				}
//				if (null != rklist.get(i).get("LSZZ")
//						&& !"".equals(rklist.get(i).get("LSZZ"))) {
//					rklist.get(i).put("LSZZ",
//							String.format("%1$.4f", rklist.get(i).get("LSZZ")));
//				}
//				if (null != rklist.get(i).get("JHZZ")
//						&& !"".equals(rklist.get(i).get("JHZZ"))) {
//					JHHJ_F += Double
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
//					rklist.get(i).put("JHJEF",
//							"本页进货金额：" + String.format("%1$.4f", JHHJ_F));
//					rklist.get(i).put("LSJEF",
//							"本页零售金额：" + String.format("%1$.4f", LSJE_F));
//					rklist.get(i)
//							.put("GLCF",
//									"本页购零差金额："
//											+ String.format("%1$.4f",
//													(LSJE_F - JHHJ_F)));
//					LSJE_F = 0.0;
//					JHHJ_F = 0.0;
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
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long xtsb = 0l;
		int rkfs = 0;
		int rkdh = 0;
		int pwd = 0;
		int fdjs = 0;
		if (request.get("xtsb") != null&&!"null".equals(request.get("xtsb")+"")) {
			xtsb = Long.parseLong(request.get("xtsb") + "");
		}
		if (request.get("rkfs") != null&&!"null".equals(request.get("rkfs")+"")) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
		}
		if (request.get("rkdh") != null&&!"null".equals(request.get("rkdh")+"")) {
			rkdh = Integer.parseInt(request.get("rkdh") + "");
		}
		if (request.get("pwd") != null&&!"null".equals(request.get("pwd")+"")) {
			pwd = Integer.parseInt(request.get("pwd") + "");
		}
		if (request.get("fdjs") != null&&!"null".equals(request.get("fdjs")+"")) {
			fdjs = Integer.parseInt(request.get("fdjs") + "");
		}
		parameter.put("XTSB", xtsb);
		parameter.put("RKFS", rkfs);
		parameter.put("RKDH", rkdh);
		String sql = "SELECT (T.RKSL*T.JHJG) as JHZZ,(T.RKSL*T.LSJG) as LSZZ,CD.CDMC as YPCD "
				+ "FROM YK_RK02 T,YK_TYPK T2,YK_CDDZ CD where T.YPXH = T2.YPXH and CD.YPCD = T.YPCD "
				+ "and T.XTSB =:XTSB and T.RKFS =:RKFS and T.RKDH =:RKDH";
		double JHHJ_Z = 0.0;
		double LSJE_Z = 0.0;
		DecimalFormat df = new DecimalFormat("#0.00");
		try {
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			for (int i = 0; i < rklist.size(); i++) {
				JHHJ_Z += Double.parseDouble(rklist.get(i).get("JHZZ") + "");
				LSJE_Z += Double.parseDouble(rklist.get(i).get("LSZZ") + "");
			}
			if(rklist != null){
				String YPCD = rklist.get(0).get("YPCD") + "";
			}
			
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();
			String jgname = user.getManageUnitName();
			String username = user.getUserName();
			// response.put("MRJEHJ", String.format("%1$.2f", JHHJ_Z));
			response.put("LSJEZ", df.format(LSJE_Z));
			response.put("JHJEZ", df.format(JHHJ_Z));
			response.put("GLC", df.format(LSJE_Z - JHHJ_Z));
			// response.put("YPCD", YPCD);
			response.put("TITLE", jgname + "药库入库单");
			response.put("RKDH", rkdh + "");
			String sqlMX = "SELECT T.CGRQ as CGRQ,T.RKDH as RKDH,T.RKFS as RKFS,T.RKBZ as RKBZ,D.DWMC as DWMC FROM YK_RK01 T,YK_JHDW D "
					+ "where T.RKDH=:RKDH and T.XTSB =:XTSB and T.RKFS =:RKFS and T.DWXH=D.DWXH";
			Map<String, Object> mapMX = dao.doLoad(sqlMX, parameter);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (mapMX != null && mapMX.size() > 0) {
				if (null != mapMX.get("CGRQ")) {
					response.put("CGRQ", sdf.format(BSHISUtil.toDate(mapMX
							.get("CGRQ") + "")));
				}
				if (null != mapMX.get("DWMC")) {
					response.put("YPCD", mapMX.get("DWMC"));
				}
				// String GRFS = "";
				// if (pwd == 0) {
				// GRFS = "货到票到";
				// } else if (pwd == 1) {
				// GRFS = "货到票未到";
				// } else if (pwd == 2) {
				// GRFS = "票到货未到";
				// }
			}
			response.put("FDJS", fdjs + "");
			response.put("ZDR", username);
			response.put("ZBRQ", sdf.format(new Date()));
			response.put("RKBZ", mapMX.get("RKBZ")!=null?mapMX.get("RKBZ"):"");
			response.put("GRFS",DictionaryController.instance().get("phis.dictionary.storeroomStorage").getText(rkfs + "")); 
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
