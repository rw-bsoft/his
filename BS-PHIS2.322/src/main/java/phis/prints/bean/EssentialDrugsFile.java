package phis.prints.bean;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class EssentialDrugsFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String datefrom = request.get("datefrom") + "";
		String dateto = request.get("dateto") + "";
		response.put("KSRQ", datefrom);
		response.put("JSRQ", dateto);
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		BaseDAO dao = new BaseDAO(ctx);
		// 入库统计
		String rzzlsql = "select sum(yk_rk02.RKSL) as RZZL from yk_typk yk_typk,yk_rk01 yk_rk01,yk_rk02 yk_rk02 where yk_typk.ypxh=yk_rk02.ypxh and yk_rk01.rkdh = yk_rk02.rkdh  and yk_rk01.XTSB = yk_rk02.XTSB And yk_rk01.XTSB = "
				+ yksb
				+ " And yk_rk01.rKFS = yk_rk02.rKFS And yk_rk01.CWPB=1 And yk_rk02.ysrq >=to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <=to_date('"
				+ dateto + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		String rjbzlsql = "select sum(yk_rk02.RKSL) as RJBZL from yk_typk yk_typk,yk_rk01 yk_rk01,yk_rk02 yk_rk02 where yk_typk.ypxh=yk_rk02.ypxh and yk_rk01.rkdh =yk_rk02.rkdh  and yk_rk01.XTSB =yk_rk02.XTSB And yk_rk01.XTSB ="
				+ yksb
				+ "  And yk_rk01.rKFS =yk_rk02.rKFS And yk_rk01.CWPB=1 And yk_rk02.ysrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX<>1";
		String rzjhhjjesql = "select sum(yk_rk02.JHHJ) as RZJHHJJE from yk_rk01 yk_rk01,yk_rk02 yk_rk02,yk_typk yk_typk where yk_rk01.XTSB =yk_rk02.XTSB And yk_rk01.XTSB ="
				+ yksb
				+ " And yk_rk01.rKFS =yk_rk02.rKFS and yk_typk.ypxh =yk_rk02.ypxh and yk_rk01.rkdh =yk_rk02.rkdh  And yk_rk01.CWPB=1 and yk_rk02.ysrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <= to_date('"
				+ dateto + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		String rjhhjbjesql = "select sum(yk_rk02.JHHJ) as RJHHJBJE from yk_rk01 yk_rk01, yk_rk02 yk_rk02, yk_typk yk_typk where yk_rk01.XTSB =yk_rk02.XTSB And yk_rk01.XTSB ="
				+ yksb
				+ " And yk_rk01.rKFS =yk_rk02.rKFS and yk_typk.ypxh =yk_rk02.ypxh and yk_rk01.rkdh =yk_rk02.rkdh  And yk_rk01.CWPB=1 and yk_rk02.ysrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX <>1";
		String lsjesql = "select sum(yk_rk02.LSJE) as LSJE from yk_rk01 yk_rk01,yk_rk02 yk_rk02,yk_typk yk_typk where yk_rk01.XTSB = yk_rk02.XTSB And yk_rk01.XTSB = "
				+ yksb
				+ "  And yk_rk01.rKFS = yk_rk02.rKFS and yk_typk.ypxh = yk_rk02.ypxh and yk_rk01.rkdh = yk_rk02.rkdh  And yk_rk01.CWPB=1  and yk_rk02.ysrq >= to_date('"
				+ datefrom
				+ " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <=to_date('"
				+ dateto + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')";
		String rjbjesql = "select sum(yk_rk02.LSJE) as RJBJE from yk_rk01 yk_rk01,yk_rk02 yk_rk02,yk_typk yk_typk where yk_rk01.XTSB =yk_rk02.XTSB And yk_rk01.XTSB ="
				+ yksb
				+ "  And yk_rk01.rKFS =yk_rk02.rKFS and yk_typk.ypxh =yk_rk02.ypxh and yk_rk01.rkdh =yk_rk02.rkdh  And yk_rk01.CWPB=1 and yk_rk02.ysrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_rk02.ysrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX<>1";
		// 出库统计
		String zzlsql = "select sum(yk_ck02.SFSL) as ZZL from yk_typk yk_typk,yk_ck01 yk_ck01,yk_ck02 yk_ck02 where yk_typk.ypxh=yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  and YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ " And YK_CK01.CKFS =YK_CK02.CKFS And yk_ck01.CKPB=1 And yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		String jbzlsql = "select sum(yk_ck02.SFSL) as JBZL from yk_typk yk_typk,yk_ck01 yk_ck01,yk_ck02 yk_ck02 where yk_typk.ypxh=yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  and YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ "  And YK_CK01.CKFS =YK_CK02.CKFS And yk_ck01.CKPB=1 And yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX<>1";
		String jhjesql = "select sum(yk_ck02.JHJE) as JHJE from yk_ck01 yk_ck01,yk_ck02 yk_ck02,yk_typk yk_typk where YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ " And YK_CK01.CKFS =YK_CK02.CKFS and yk_typk.ypxh =yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  And yk_ck01.CKPB=1 and yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		String jbjhjejesql = "select sum(yk_ck02.JHJE) as JBjhjeJE from yk_ck01 yk_ck01,yk_ck02 yk_ck02,yk_typk yk_typk where YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ "  And YK_CK01.CKFS =YK_CK02.CKFS and yk_typk.ypxh =yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  And yk_ck01.CKPB=1 and yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX<>1";
		String zjesql = "select sum(yk_ck02.LSJE) as ZJE from yk_ck01 yk_ck01,yk_ck02 yk_ck02,yk_typk yk_typk where YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ "  And YK_CK01.CKFS =YK_CK02.CKFS And yk_typk.ypsx not in (58,59,61) and yk_typk.ypxh =yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  And yk_ck01.CKPB=1 and yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		String jbjesql = "select sum(yk_ck02.LSJE) as JBJE from yk_ck01 yk_ck01,yk_ck02 yk_ck02,yk_typk yk_typk where YK_CK01.XTSB =YK_CK02.XTSB And YK_CK01.XTSB ="
				+ yksb
				+ "  And YK_CK01.CKFS =YK_CK02.CKFS and yk_typk.ypxh =yk_ck02.ypxh and yk_ck01.ckdh =yk_ck02.ckdh  And yk_ck01.CKPB=1 and yk_ck01.lyrq >= to_date('"
				+ datefrom
				+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') AND yk_ck01.lyrq <= to_date('"
				+ dateto
				+ " 23:59:59','yyyy-mm-dd hh24:mi:ss') and yk_typk.JYLX<>1";
		try {
			List<Map<String, Object>> rzzlList = dao.doSqlQuery(rzzlsql, null);
			if (rzzlList.size() > 0) {
				if (rzzlList.get(0).get("RZZL") != null) {
					response.put("RZZL", String.format("%1$.2f", rzzlList
							.get(0).get("RZZL")));
				} else {
					response.put("RZZL", "0.00");
				}
			} else {
				response.put("RZZL", "0.00");
			}
			List<Map<String, Object>> rjbzlList = dao
					.doSqlQuery(rjbzlsql, null);
			if (rjbzlList.size() > 0) {
				if (rjbzlList.get(0).get("RJBZL") != null) {
					response.put(
							"RJBZL",
							String.format("%1$.2f",
									rjbzlList.get(0).get("RJBZL")));
				} else {
					response.put("RJBZL", "0.00");
				}
			} else {
				response.put("RJBZL", "0.00");
			}
			Double rzzl = parseDouble(rzzlList.get(0).get("RZZL"));
			Double rjbzl = parseDouble(rjbzlList.get(0).get("RJBZL"));
			if (rjbzl == 0) {
				response.put("GRSLZB", "0.00%");
			} else {
				double grslzb = rjbzl / rzzl;
				response.put("GRSLZB", String.format("%1$.2f", (grslzb * 100))
						+ "%");
			}
			List<Map<String, Object>> rzjhhjjeList = dao.doSqlQuery(
					rzjhhjjesql, null);
			if (rzjhhjjeList.size() > 0) {
				if (rzjhhjjeList.get(0).get("RZJHHJJE") != null) {
					response.put(
							"RZJHHJJE",
							String.format("%1$.4f",
									rzjhhjjeList.get(0).get("RZJHHJJE")));

				} else {
					response.put("RZJHHJJE", "0.0000");
				}
			} else {
				response.put("RZJHHJJE", "0.0000");
			}
			List<Map<String, Object>> rjhhjbjeList = dao.doSqlQuery(
					rjhhjbjesql, null);
			if (rjhhjbjeList.size() > 0) {
				if (rjhhjbjeList.get(0).get("RJHHJBJE") != null) {
					response.put(
							"RJHHJBJE",
							String.format("%1$.4f",
									rjhhjbjeList.get(0).get("RJHHJBJE")));
				} else {
					response.put("RJHHJBJE", "0.0000");
				}
			} else {
				response.put("RJHHJBJE", "0.0000");
			}
			Double rzjhhjje = parseDouble(rzjhhjjeList.get(0).get("RZJHHJJE"));
			Double rjhhjbje = parseDouble(rjhhjbjeList.get(0).get("RJHHJBJE"));
			if (rjhhjbje == 0) {
				response.put("GRJJZB", "0.00%");
			} else {
				double grjjzb = rjhhjbje / rzjhhjje;
				response.put("GRJJZB", String.format("%1$.2f", (grjjzb * 100))
						+ "%");
			}
			List<Map<String, Object>> lsjeList = dao.doSqlQuery(lsjesql, null);
			if (lsjeList.size() > 0) {
				if (lsjeList.get(0).get("LSJE") != null) {
					response.put("LSJE", String.format("%1$.4f", lsjeList
							.get(0).get("LSJE")));
				} else {
					response.put("LSJE", "0.0000");
				}
			} else {
				response.put("LSJE", "0.0000");
			}
			List<Map<String, Object>> rjbjeList = dao
					.doSqlQuery(rjbjesql, null);
			if (rjbjeList.size() > 0) {
				if (rjbjeList.get(0).get("RJBJE") != null) {
					response.put(
							"RJBJE",
							String.format("%1$.4f",
									rjbjeList.get(0).get("RJBJE")));
				} else {
					response.put("RJBJE", "0.0000");
				}
			} else {
				response.put("RJBJE", "0.0000");
			}
			Double lsje = parseDouble(lsjeList.get(0).get("LSJE"));
			Double rjbje = parseDouble(rjbjeList.get(0).get("RJBJE"));
			if (rjbje == 0) {
				response.put("GRLJZB", "0.00%");
			} else {
				double grljzb = rjbje / lsje;
				response.put("GRLJZB", String.format("%1$.2f", (grljzb * 100))
						+ "%");
			}
			List<Map<String, Object>> zzlList = dao.doSqlQuery(zzlsql, null);
			if (zzlList.size() > 0) {
				if (zzlList.get(0).get("ZZL") != null) {
					response.put("ZZL",
							String.format("%1$.2f", zzlList.get(0).get("ZZL")));
				} else {
					response.put("ZZL", "0.00");
				}
			} else {
				response.put("ZZL", "0.00");
			}
			List<Map<String, Object>> jbzlList = dao.doSqlQuery(jbzlsql, null);
			if (jbzlList.size() > 0) {
				if (jbzlList.get(0).get("JBZL") != null) {
					response.put("JBZL", String.format("%1$.2f", jbzlList
							.get(0).get("JBZL")));
				} else {
					response.put("JBZL", "0.00");
				}
			} else {
				response.put("JBZL", "0.00");
			}
			Double zzl = parseDouble(zzlList.get(0).get("ZZL"));
			Double jbzl = parseDouble(jbzlList.get(0).get("JBZL"));
			if (jbzl == 0) {
				response.put("CKSLZB", "0.00%");
			} else {
				double ckslzb = jbzl / zzl;
				response.put("CKSLZB", String.format("%1$.2f", (ckslzb * 100))
						+ "%");
			}
			List<Map<String, Object>> jhjeList = dao.doSqlQuery(jhjesql, null);
			if (jhjeList.size() > 0) {
				if (jhjeList.get(0).get("JHJE") != null) {
					response.put("JHJE", String.format("%1$.4f", jhjeList
							.get(0).get("JHJE")));
				} else {
					response.put("JHJE", "0.0000");
				}
			} else {
				response.put("JHJE", "0.0000");
			}
			List<Map<String, Object>> jbjhjejeList = dao.doSqlQuery(
					jbjhjejesql, null);
			if (jbjhjejeList.size() > 0) {
				if (jbjhjejeList.get(0).get("JBJHJEJE") != null) {
					response.put(
							"JBJHJEJE",
							String.format("%1$.4f",
									jbjhjejeList.get(0).get("JBJHJEJE")));
				} else {
					response.put("JBJHJEJE", "0.0000");
				}
			} else {
				response.put("JBJHJEJE", "0.0000");
			}
			Double jhje = parseDouble(jhjeList.get(0).get("JHJE"));
			Double jbjhjeje = parseDouble(jbjhjejeList.get(0).get("JBJHJEJE"));
			if (jbjhjeje == 0) {
				response.put("CKJJZB", "0.00%");
			} else {
				double ckjjzb = jbjhjeje / jhje;
				response.put("CKJJZB", String.format("%1$.2f", (ckjjzb * 100))
						+ "%");
			}
			List<Map<String, Object>> zjeList = dao.doSqlQuery(zjesql, null);
			if (zjeList.size() > 0) {
				if (zjeList.get(0).get("ZJE") != null) {
					response.put("ZJE",
							String.format("%1$.4f", zjeList.get(0).get("ZJE")));
				} else {
					response.put("ZJE", "0.0000");
				}
			} else {
				response.put("ZJE", "0.0000");
			}
			List<Map<String, Object>> jbjeList = dao.doSqlQuery(jbjesql, null);
			if (jbjeList.size() > 0) {
				if (jbjeList.get(0).get("JBJE") != null) {
					response.put("JBJE", String.format("%1$.4f", jbjeList
							.get(0).get("JBJE")));
				} else {
					response.put("JBJE", "0.0000");
				}
			} else {
				response.put("JBJE", "0.0000");
			}
			Double zje = parseDouble(zjeList.get(0).get("ZJE"));
			Double jbje = parseDouble(jbjeList.get(0).get("JBJE"));
			if (jbje == 0) {
				response.put("CKLJZB", "0.00%");
			} else {
				double ckljzb = jbje / zje;
				response.put("CKLJZB", String.format("%1$.2f", (ckljzb * 100))
						+ "%");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author
	 * @createDate 2012-7-27
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	public int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}
}
