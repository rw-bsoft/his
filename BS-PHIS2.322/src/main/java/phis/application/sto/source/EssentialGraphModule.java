/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.sto.source;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 医生排班
 * 
 * @author shiwy 2012.08.28
 */
public class EssentialGraphModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(EssentialGraphModule.class);

	public EssentialGraphModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 挂号分类统计
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryEssentialGraph(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ParseException,
			ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		if (body == null)
			return;
		String datefrom = body.get("beginDate") == null ? BSHISUtil.getDate()
				: String.valueOf(body.get("beginDate"));
		String dateto = body.get("endDate") == null ? BSHISUtil.getDate()
				: String.valueOf(body.get("endDate"));
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
			Map<String, Object> EssentialGraphMap = new HashMap<String, Object>();
			List<Map<String, Object>> rzzlList = dao.doSqlQuery(rzzlsql, null);
			if (rzzlList.size() > 0) {
				if (rzzlList.get(0).get("RZZL") != null) {
					EssentialGraphMap.put("RZZL", String.format("%1$.2f",
							rzzlList.get(0).get("RZZL")));
				} else {
					EssentialGraphMap.put("RZZL", "0.00");
				}
			} else {
				EssentialGraphMap.put("RZZL", "0.00");
			}
			List<Map<String, Object>> rjbzlList = dao
					.doSqlQuery(rjbzlsql, null);
			if (rjbzlList.size() > 0) {
				if (rjbzlList.get(0).get("RJBZL") != null) {
					EssentialGraphMap.put(
							"RJBZL",
							String.format("%1$.2f",
									rjbzlList.get(0).get("RJBZL")));
				} else {
					EssentialGraphMap.put("RJBZL", "0.00");
				}
			} else {
				EssentialGraphMap.put("RJBZL", "0.00");
			}
			Double rzzl = parseDouble(rzzlList.get(0).get("RZZL"));
			Double rjbzl = parseDouble(rjbzlList.get(0).get("RJBZL"));
			if (rjbzl == 0) {
				EssentialGraphMap.put("GRSLZB", "0.00%");
			} else {
				double grslzb = rjbzl / rzzl;
				EssentialGraphMap.put("GRSLZB",
						String.format("%1$.2f", (grslzb * 100)) + "%");
			}
			List<Map<String, Object>> rzjhhjjeList = dao.doSqlQuery(
					rzjhhjjesql, null);
			if (rzjhhjjeList.size() > 0) {
				if (rzjhhjjeList.get(0).get("RZJHHJJE") != null) {
					EssentialGraphMap.put(
							"RZJHHJJE",
							String.format("%1$.4f",
									rzjhhjjeList.get(0).get("RZJHHJJE")));

				} else {
					EssentialGraphMap.put("RZJHHJJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("RZJHHJJE", "0.0000");
			}
			List<Map<String, Object>> rjhhjbjeList = dao.doSqlQuery(
					rjhhjbjesql, null);
			if (rjhhjbjeList.size() > 0) {
				if (rjhhjbjeList.get(0).get("RJHHJBJE") != null) {
					EssentialGraphMap.put(
							"RJHHJBJE",
							String.format("%1$.4f",
									rjhhjbjeList.get(0).get("RJHHJBJE")));
				} else {
					EssentialGraphMap.put("RJHHJBJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("RJHHJBJE", "0.0000");
			}
			Double rzjhhjje = parseDouble(rzjhhjjeList.get(0).get("RZJHHJJE"));
			Double rjhhjbje = parseDouble(rjhhjbjeList.get(0).get("RJHHJBJE"));
			if (rjhhjbje == 0) {
				EssentialGraphMap.put("GRJJZB", "0.00%");
			} else {
				double grjjzb = rjhhjbje / rzjhhjje;
				EssentialGraphMap.put("GRJJZB",
						String.format("%1$.2f", (grjjzb * 100)) + "%");
			}
			List<Map<String, Object>> lsjeList = dao.doSqlQuery(lsjesql, null);
			if (lsjeList.size() > 0) {
				if (lsjeList.get(0).get("LSJE") != null) {
					EssentialGraphMap.put("LSJE", String.format("%1$.4f",
							lsjeList.get(0).get("LSJE")));
				} else {
					EssentialGraphMap.put("LSJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("LSJE", "0.0000");
			}
			List<Map<String, Object>> rjbjeList = dao
					.doSqlQuery(rjbjesql, null);
			if (rjbjeList.size() > 0) {
				if (rjbjeList.get(0).get("RJBJE") != null) {
					EssentialGraphMap.put(
							"RJBJE",
							String.format("%1$.4f",
									rjbjeList.get(0).get("RJBJE")));
				} else {
					EssentialGraphMap.put("RJBJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("RJBJE", "0.0000");
			}
			Double lsje = parseDouble(lsjeList.get(0).get("LSJE"));
			Double rjbje = parseDouble(rjbjeList.get(0).get("RJBJE"));
			if (rjbje == 0) {
				EssentialGraphMap.put("GRLJZB", "0.00%");
			} else {
				double grljzb = rjbje / lsje;
				EssentialGraphMap.put("GRLJZB",
						String.format("%1$.2f", (grljzb * 100)) + "%");
			}
			List<Map<String, Object>> zzlList = dao.doSqlQuery(zzlsql, null);
			if (zzlList.size() > 0) {
				if (zzlList.get(0).get("ZZL") != null) {
					EssentialGraphMap.put("ZZL",
							String.format("%1$.2f", zzlList.get(0).get("ZZL")));
				} else {
					EssentialGraphMap.put("ZZL", "0.00");
				}
			} else {
				EssentialGraphMap.put("ZZL", "0.00");
			}
			List<Map<String, Object>> jbzlList = dao.doSqlQuery(jbzlsql, null);
			if (jbzlList.size() > 0) {
				if (jbzlList.get(0).get("JBZL") != null) {
					EssentialGraphMap.put("JBZL", String.format("%1$.2f",
							jbzlList.get(0).get("JBZL")));
				} else {
					EssentialGraphMap.put("JBZL", "0.00");
				}
			} else {
				EssentialGraphMap.put("JBZL", "0.00");
			}
			Double zzl = parseDouble(zzlList.get(0).get("ZZL"));
			Double jbzl = parseDouble(jbzlList.get(0).get("JBZL"));
			if (jbzl == 0) {
				EssentialGraphMap.put("CKSLZB", "0.00%");
			} else {
				double ckslzb = jbzl / zzl;
				EssentialGraphMap.put("CKSLZB",
						String.format("%1$.2f", (ckslzb * 100)) + "%");
			}
			List<Map<String, Object>> jhjeList = dao.doSqlQuery(jhjesql, null);
			if (jhjeList.size() > 0) {
				if (jhjeList.get(0).get("JHJE") != null) {
					EssentialGraphMap.put("JHJE", String.format("%1$.4f",
							jhjeList.get(0).get("JHJE")));
				} else {
					EssentialGraphMap.put("JHJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("JHJE", "0.0000");
			}
			List<Map<String, Object>> jbjhjejeList = dao.doSqlQuery(
					jbjhjejesql, null);
			if (jbjhjejeList.size() > 0) {
				if (jbjhjejeList.get(0).get("JBJHJEJE") != null) {
					EssentialGraphMap.put(
							"JBJHJEJE",
							String.format("%1$.4f",
									jbjhjejeList.get(0).get("JBJHJEJE")));
				} else {
					EssentialGraphMap.put("JBJHJEJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("JBJHJEJE", "0.0000");
			}
			Double jhje = parseDouble(jhjeList.get(0).get("JHJE"));
			Double jbjhjeje = parseDouble(jbjhjejeList.get(0).get("JBJHJEJE"));
			if (jbjhjeje == 0) {
				EssentialGraphMap.put("CKJJZB", "0.00%");
			} else {
				double ckjjzb = jbjhjeje / jhje;
				EssentialGraphMap.put("CKJJZB",
						String.format("%1$.2f", (ckjjzb * 100)) + "%");
			}
			List<Map<String, Object>> zjeList = dao.doSqlQuery(zjesql, null);
			if (zjeList.size() > 0) {
				if (zjeList.get(0).get("ZJE") != null) {
					EssentialGraphMap.put("ZJE",
							String.format("%1$.4f", zjeList.get(0).get("ZJE")));
				} else {
					EssentialGraphMap.put("ZJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("ZJE", "0.0000");
			}
			List<Map<String, Object>> jbjeList = dao.doSqlQuery(jbjesql, null);
			if (jbjeList.size() > 0) {
				if (jbjeList.get(0).get("JBJE") != null) {
					EssentialGraphMap.put("JBJE", String.format("%1$.4f",
							jbjeList.get(0).get("JBJE")));
				} else {
					EssentialGraphMap.put("JBJE", "0.0000");
				}
			} else {
				EssentialGraphMap.put("JBJE", "0.0000");
			}
			Double zje = parseDouble(zjeList.get(0).get("ZJE"));
			Double jbje = parseDouble(jbjeList.get(0).get("JBJE"));
			if (jbje == 0) {
				EssentialGraphMap.put("CKLJZB", "0.00%");
			} else {
				double ckljzb = jbje / zje;
				EssentialGraphMap.put("CKLJZB",
						String.format("%1$.2f", (ckljzb * 100)) + "%");
			}
			res.put("body", EssentialGraphMap);
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
