package phis.prints.bean; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

public class PharmacyInFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> parameter = new HashMap<String, Object>();
		Long yfsb = 0l;
		int rkfs = 0;
		int rkdh = 0;
		if (request.get("yfsb") != null) {
			yfsb = Long.parseLong(request.get("yfsb") + "");
		}
		if (request.get("rkfs") != null) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
		}
		if (request.get("rkdh") != null) {
			rkdh = Integer.parseInt(request.get("rkdh") + "");
		}
		parameter.put("YFSB", yfsb);
		parameter.put("RKFS", rkfs);
		parameter.put("RKDH", rkdh);
		String sql = "SELECT T.RKFS as RKFS,(T.RKSL*T.JHJG) as JHZZ,(T.RKSL*T.LSJG) as LSZZ,T.RKDH as RKDH,T.YPGG as YPGG,T.YFDW as YPDW,T.RKSL as RKSL,T.LSJG as LSJG ,T.JHJG as JHJG,T.LSJE as LSJE,T.JHJE as JHJE,T.YPPH as YPPH,T.YPXQ as YPXQ,T2.YPMC as YPMC,CD.CDMC as YPCD FROM YF_RK02 T,YK_TYPK T2,YK_CDDZ CD where T.YPXH = T2.YPXH and T.YFSB =:YFSB and T.RKFS =:RKFS and T.RKDH =:RKDH and CD.YPCD = T.YPCD";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			// 先放记录集
			double LSJE_F = 0.0;
			double JHJE_F = 0.0;
			// 每页显示多少行
			int culNum = 16;
			// 总页数
			int pagNum = rklist.size() / culNum;
			if (rklist.size() % culNum != 0||rklist.size()==0) {
				for (int i = 0; i < culNum; i++) {
					Map<String, Object> parMAP = new HashMap<String, Object>();
					parMAP.put("RKFS", "");
					parMAP.put("JHZZ", "");
					parMAP.put("LSZZ", "");
					parMAP.put("RKDH", "");
					parMAP.put("YPGG", "");
					parMAP.put("YPDW", "");
					parMAP.put("RKSL", "");
					parMAP.put("LSJG", "");
					parMAP.put("JHJG", "");
					parMAP.put("LSJE", "");
					parMAP.put("JHJE", "");
					parMAP.put("YPPH", "");
					parMAP.put("YPXQ", "");
					parMAP.put("YPMC", "");
					parMAP.put("YPCD", "");
					rklist.add(parMAP);
					if (rklist.size() % culNum == 0) {
						break;
					}
				}
			}
			for (int i = 0; i < pagNum * culNum; i++) {
				if (!"".equals(rklist.get(i).get("RKSL"))
						&& rklist.get(i).get("RKSL") != null) {
					rklist.get(i).put("YPSL",
							String.format("%1$.2f", rklist.get(i).get("RKSL")));
				}
				if (!"".equals(rklist.get(i).get("LSJG"))
						&& rklist.get(i).get("LSJG") != null) {
					rklist.get(i).put("LSJG",
							String.format("%1$.2f", rklist.get(i).get("LSJG")));
				}
				if (!"".equals(rklist.get(i).get("JHJG"))
						&& rklist.get(i).get("JHJG") != null) {
					rklist.get(i).put("JHJG",
							String.format("%1$.2f", rklist.get(i).get("JHJG")));
				}
				if (!"".equals(rklist.get(i).get("LSJE"))
						&& rklist.get(i).get("LSJE") != null) {
					rklist.get(i).put("LSJE",
							String.format("%1$.2f", rklist.get(i).get("LSJE")));
				}
				if (!"".equals(rklist.get(i).get("JHZZ"))
						&& rklist.get(i).get("JHZZ") != null) {
					rklist.get(i).put("JHZZ",
							String.format("%1$.2f", rklist.get(i).get("JHZZ")));
				}
				if (!"".equals(rklist.get(i).get("LSZZ"))
						&& rklist.get(i).get("LSZZ") != null) {
					rklist.get(i).put("LSZZ",
							String.format("%1$.2f", rklist.get(i).get("LSZZ")));
				}
				if (!"".equals(rklist.get(i).get("JHZZ"))
						&& rklist.get(i).get("JHZZ") != null) {
					JHJE_F += Double
							.parseDouble(rklist.get(i).get("JHZZ") + "");
				}
				if (!"".equals(rklist.get(i).get("LSZZ"))
						&& rklist.get(i).get("LSZZ") != null) {
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
				if (rklist.size() > culNum) {
					if ((i + 1) % culNum == 0) {
						rklist.get(i).put("JHJEF",
								"本页进货金额：" + String.format("%1$.2f", JHJE_F));
						rklist.get(i).put("LSJEF",
								"本页零售金额：" + String.format("%1$.2f", LSJE_F));
						rklist.get(i).put(
								"GLCF",
								"本页购零差金额："
										+ String.format("%1$.2f",
												(LSJE_F - JHJE_F)));
						LSJE_F = 0.0;
						JHJE_F = 0.0;
					}

				}
				records.add(rklist.get(i));
			}
			for (int i = pagNum * culNum; i < rklist.size(); i++) {
				if (!"".equals(rklist.get(i).get("RKSL"))
						&& rklist.get(i).get("RKSL") != null) {
					rklist.get(i).put("YPSL",
							String.format("%1$.2f", rklist.get(i).get("RKSL")));
				}
				if (!"".equals(rklist.get(i).get("LSJG"))
						&& rklist.get(i).get("LSJG") != null) {
					rklist.get(i).put("LSJG",
							String.format("%1$.2f", rklist.get(i).get("LSJG")));
				}
				if (!"".equals(rklist.get(i).get("JHJG"))
						&& rklist.get(i).get("JHJG") != null) {
					rklist.get(i).put("JHJG",
							String.format("%1$.2f", rklist.get(i).get("JHJG")));
				}
				if (!"".equals(rklist.get(i).get("LSJE"))
						&& rklist.get(i).get("LSJE") != null) {
					rklist.get(i).put("LSJE",
							String.format("%1$.2f", rklist.get(i).get("LSJE")));
				}
				if (!"".equals(rklist.get(i).get("JHZZ"))
						&& rklist.get(i).get("JHZZ") != null) {
					rklist.get(i).put("JHZZ",
							String.format("%1$.2f", rklist.get(i).get("JHZZ")));
				}
				if (!"".equals(rklist.get(i).get("LSZZ"))
						&& rklist.get(i).get("LSZZ") != null) {
					rklist.get(i).put("LSZZ",
							String.format("%1$.2f", rklist.get(i).get("LSZZ")));
				}
				if (!"".equals(rklist.get(i).get("JHZZ"))
						&& rklist.get(i).get("JHZZ") != null) {
					JHJE_F += Double
							.parseDouble(rklist.get(i).get("JHZZ") + "");
				}
				if (!"".equals(rklist.get(i).get("LSZZ"))
						&& rklist.get(i).get("LSZZ") != null) {
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
				if (i == (rklist.size() - 1)) {
					rklist.get(i).put("JHJEF",
							"本页进货金额：" + String.format("%1$.2f", JHJE_F));
					rklist.get(i).put("LSJEF",
							"本页零售金额：" + String.format("%1$.2f", LSJE_F));
					rklist.get(i)
							.put("GLCF",
									"本页购零差金额："
											+ String.format("%1$.2f",
													(LSJE_F - JHJE_F)));
					LSJE_F = 0.0;
					JHJE_F = 0.0;
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
		Long yfsb = 0l;
		int rkfs = 0;
		int rkdh = 0;
		if (request.get("yfsb") != null) {
			yfsb = Long.parseLong(request.get("yfsb") + "");
		}
		if (request.get("rkfs") != null) {
			rkfs = Integer.parseInt(request.get("rkfs") + "");
		}
		if (request.get("rkdh") != null) {
			rkdh = Integer.parseInt(request.get("rkdh") + "");
		}
		parameter.put("YFSB", yfsb);
		parameter.put("RKFS", rkfs);
		parameter.put("RKDH", rkdh);
		String sql = "SELECT (T.RKSL*T.JHJG) as JHZZ,(T.RKSL*T.LSJG) as LSZZ FROM YF_RK02 T,YK_TYPK T2 where T.YPXH = T2.YPXH and T.YFSB =:YFSB and T.RKFS =:RKFS and T.RKDH =:RKDH";
		double JHJE_Z = 0.0;
		double LSJE_Z = 0.0;
		DecimalFormat df = new DecimalFormat("#0.00");
		try {
			List<Map<String, Object>> rklist = dao.doQuery(sql, parameter);
			for (int i = 0; i < rklist.size(); i++) {
				JHJE_Z += Double.parseDouble(rklist.get(i).get("JHZZ") + "");
				LSJE_Z += Double.parseDouble(rklist.get(i).get("LSZZ") + "");
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			String username = user.getUserName();
			String jgname = user.getManageUnit().getName();
			response.put("LSJEZ", df.format(LSJE_Z));
			response.put("JHJEZ", df.format(JHJE_Z));
			response.put("GLC", df.format(LSJE_Z - JHJE_Z));
			response.put("TITLE", jgname + "药品入库单");
			response.put("RKDH", rkdh + "");
			String sqlMX = "SELECT T.RKRQ as RKRQ,T.RKDH as RKDH,T.RKFS as RKFS,T.RKBZ as BZ FROM YF_RK01 T where T.RKDH=:RKDH and T.YFSB =:YFSB and T.RKFS =:RKFS and T.RKDH =:RKDH";
			Map<String, Object> mapMX = dao.doLoad(sqlMX, parameter);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (mapMX != null && mapMX.size() > 0) {
				if (null != mapMX.get("RKRQ")) {
					response.put("RKRQ", sdf.format(BSHISUtil.toDate(mapMX
							.get("RKRQ") + "")));
				}
				if (null != mapMX.get("RKFS")) {
					response.put("RKFS",
							DictionaryController.instance().getDic("phis.dictionary.drugStorage")
									.getText(mapMX.get("RKFS") + ""));
				}
				if (mapMX.get("BZ") != null) {
					response.put("BZ", mapMX.get("BZ") + "");
				} else {
					response.put("BZ", "");
				}
				response.put("ZDR", username);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
