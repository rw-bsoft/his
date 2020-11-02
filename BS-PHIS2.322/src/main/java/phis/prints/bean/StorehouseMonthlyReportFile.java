package phis.prints.bean; 
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import phis.source.BaseDAO;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context; 

public class StorehouseMonthlyReportFile implements IHandler {

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
			BaseDAO dao = new BaseDAO(ctx);
			UserRoleToken user = UserRoleToken.getCurrent();
			String userName = user.getUserName();// 用户名
			String jgName = user.getManageUnit().getName();// 用户的机构名称
			String jgid = user.getManageUnit().getId();// 用户的机构ID 
			String YKACCOUNTPRICE = ParameterUtil.getParameter(jgid,
					"YKACCOUNTPRICE", "1", "药库记账标准价格，1表示零售价格，2表示进货价格，3表示批发价格",
					ctx);
			int zblb = parseInt(request.get("zblb"));
			Long xtsb = parseLong(request.get("xtsb"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateFrom = sdf.parse(parseString(request.get("dateFrom"))
					+ " 00:00:00");
			Date dateTo = sdf.parse(parseString(request.get("dateTo"))
					+ " 23:59:59");

			Map<String, Object> parameterCRKJE = new HashMap<String, Object>();
			parameterCRKJE.put("JGID", jgid);
			parameterCRKJE.put("XTSB", xtsb);
			parameterCRKJE.put("dateFrom", dateFrom);
			parameterCRKJE.put("dateTo", dateTo);
			// 调价
			String sql_tj = "SELECT sum(b.PFZZ)  AS PFZZJE,sum(b.PFJZ)  AS PFJZJE,"
					+ " sum(b.LSZZ)  AS LSZZJE,sum(b.LSJZ) AS LSJZJE"
					+ " FROM YK_TJ02 b,YK_TJ01 a,YK_TYPK c WHERE c.YPXH = b.YPXH AND "
					+ " a.XTSB = b.XTSB AND a.TJFS = b.TJFS AND "
					+ " a.TJDH = b.TJDH AND a.JGID = b.JGID AND "
					+ " b.YFSB = 0 AND a.ZYPB = 1 AND b.JGID =:JGID AND"
					+ " b.XTSB =:XTSB AND a.ZXRQ >:dateFrom AND a.ZXRQ <=:dateTo";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_tj += " AND c.TYPE=:ZBLB";
			} else {
			}
			Map<String, Object> map_tj = dao.doLoad(sql_tj, parameterCRKJE);

			// 入库
			String sql_rk = "select a.RKFS as RKFS,a.FSMC as XM,b.JHHJ as JHHJ,b.PFHJ as PFHJ,b.LSHJ as LSHJ,"
					+ " b.JHHJ-b.PFHJ as JPCJ,b.LSHJ-b.JHHJ as JXCJ,b.LSHJ-b.PFHJ as PLCJ"
					+ " from YK_RKFS a left join "
					+ " (SELECT YK_RK02.RKFS,sum(YK_RK02.JHHJ) AS JHHJ,sum(YK_RK02.PFJE) AS PFHJ,"
					+ "　　sum(YK_RK02.LSJE) AS LSHJ FROM  YK_RK02,YK_TYPK,yk_rk01 WHERE YK_TYPK.YPXH = YK_RK02.YPXH " +
					" and yk_rk01.RKFS=YK_RK02.RKFS and YK_RK02.XTSB=yk_rk01.XTSB AND yk_rk01.RKDH=YK_RK02.RKDH AND"
					+ "  YK_RK02.JGID =:JGID  AND  YK_RK02.XTSB =:XTSB AND YK_RK02.YSRQ > :dateFrom AND "
					+ "  YK_RK02.YSRQ <=:dateTo ";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_rk += " AND YK_TYPK.TYPE=:ZBLB GROUP BY YK_RK02.RKFS ) b on a.RKFS =b.RKFS"
						+ " where a.jgid=:JGID AND a.XTSB =:XTSB order by a.RKFS";
			} else {
				sql_rk += " GROUP BY YK_RK02.RKFS ) b on a.RKFS =b.RKFS"
						+ " where a.jgid=:JGID AND a.XTSB =:XTSB order by a.RKFS";
			}
			List<Map<String, Object>> list_rk = dao.doSqlQuery(sql_rk,
					parameterCRKJE);
			records.addAll(list_rk);
			// 入库合计
			Double _jhhj = 0d;
			Double _pfhj = 0d;
			Double _lshj = 0d;
			Double _jpcj = 0d;
			Double _jxcj = 0d;
			Double _plcj = 0d;
			for (Map<String, Object> rk : list_rk) {
				_jhhj += parseDouble(rk.get("JHHJ"));
				_pfhj += parseDouble(rk.get("PFHJ"));
				_lshj += parseDouble(rk.get("LSHJ"));
				_jpcj += parseDouble(rk.get("JPCJ"));
				_jxcj += parseDouble(rk.get("JXCJ"));
				_plcj += parseDouble(rk.get("PLCJ"));
			}
			Map<String, Object> columnRKHJ = new HashMap<String, Object>();
			columnRKHJ.put("XM", "合计");
			columnRKHJ.put("JHHJ", String.format("%1$.2f", parseDouble(_jhhj)));
			columnRKHJ.put("PFHJ", String.format("%1$.2f", parseDouble(_pfhj)));
			columnRKHJ.put("LSHJ", String.format("%1$.2f", parseDouble(_lshj)));
			columnRKHJ.put("JPCJ", String.format("%1$.2f", parseDouble(_jpcj)));
			columnRKHJ.put("JXCJ", String.format("%1$.2f", parseDouble(_jxcj)));
			columnRKHJ.put("PLCJ", String.format("%1$.2f", parseDouble(_plcj)));
			if ("1".equals(YKACCOUNTPRICE)) {
				columnRKHJ.put(
						"TJZZ",
						String.format("%1$.2f",
								parseDouble(map_tj.get("LSZZJE"))));
			} else if ("3".equals(YKACCOUNTPRICE)) {
				columnRKHJ.put(
						"TJZZ",
						String.format("%1$.2f",
								parseDouble(map_tj.get("PFZZJE"))));
			}
			records.add(columnRKHJ);
			// 出库
			String sql_ck = "select a.CKFS as CKFS,a.FSMC as XM,b.JHHJ as JHHJ,b.PFHJ as PFHJ,b.LSHJ as LSHJ,"
					+ " b.JHHJ-b.PFHJ as JPCJ,b.LSHJ-b.JHHJ as JXCJ,b.LSHJ-b.PFHJ as PLCJ"
					+ " from YK_CKFS a left join "
					+ " (SELECT YK_CK02.CKFS,sum(YK_CK02.JHJE) AS JHHJ,sum(YK_CK02.PFJE) AS PFHJ,"
					+ "　　sum(YK_CK02.LSJE) AS LSHJ FROM  YK_CK02,YK_TYPK,YK_CK01 WHERE YK_TYPK.YPXH = YK_CK02.YPXH AND"
					+ "  YK_CK01.XTSB = YK_CK02.XTSB AND YK_CK01.CKFS = YK_CK02.CKFS AND "
					+ "  YK_CK01.CKDH = YK_CK02.CKDH AND YK_CK01.JGID = YK_CK02.JGID AND"
					+ "  YK_CK01.CKPB = 1 AND "
					+ "  YK_CK02.JGID =:JGID  AND  YK_CK02.XTSB =:XTSB AND YK_CK01.CKRQ > :dateFrom AND "
					+ "  YK_CK01.CKRQ <=:dateTo ";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_ck += " AND YK_TYPK.TYPE=:ZBLB GROUP BY YK_CK02.CKFS ) b on a.CKFS =b.CKFS"
						+ " where a.JGID=:JGID AND a.XTSB =:XTSB order by a.CKFS";
			} else {
				sql_ck += " GROUP BY YK_CK02.CKFS ) b on a.CKFS =b.CKFS"
						+ " where a.JGID=:JGID AND a.XTSB =:XTSB order by a.CKFS";
			}
			List<Map<String, Object>> list_ck = dao.doSqlQuery(sql_ck,
					parameterCRKJE);
			Map<String, Object> column = new HashMap<String, Object>();
			column.put("CRFS", "出库");
			column.put("XM", "项目");
			column.put("JHHJ", "进价合计");
			column.put("PFHJ", "批价合计");
			column.put("LSHJ", "零售合计");
			column.put("JPCJ", "进批差价");
			column.put("JXCJ", "进销差价");
			column.put("PLCJ", "批零差价");
			column.put("TJZZ", "调价减值");
			records.add(column);
			records.addAll(list_ck);
			// 出库合计
			_jhhj = 0d;
			_pfhj = 0d;
			_lshj = 0d;
			_jpcj = 0d;
			_jxcj = 0d;
			_plcj = 0d;
			for (Map<String, Object> ck : list_ck) {
				_jhhj += parseDouble(ck.get("JHHJ"));
				_pfhj += parseDouble(ck.get("PFHJ"));
				_lshj += parseDouble(ck.get("LSHJ"));
				_jpcj += parseDouble(ck.get("JPCJ"));
				_jxcj += parseDouble(ck.get("JXCJ"));
				_plcj += parseDouble(ck.get("PLCJ"));
			}
			Map<String, Object> columnCKHJ = new HashMap<String, Object>();
			columnCKHJ.put("XM", "合计");
			columnCKHJ.put("JHHJ", String.format("%1$.2f", parseDouble(_jhhj)));
			columnCKHJ.put("PFHJ", String.format("%1$.2f", parseDouble(_pfhj)));
			columnCKHJ.put("LSHJ", String.format("%1$.2f", parseDouble(_lshj)));
			columnCKHJ.put("JPCJ", String.format("%1$.2f", parseDouble(_jpcj)));
			columnCKHJ.put("JXCJ", String.format("%1$.2f", parseDouble(_jxcj)));
			columnCKHJ.put("PLCJ", String.format("%1$.2f", parseDouble(_plcj)));
			if ("1".equals(YKACCOUNTPRICE)) {
				columnCKHJ.put(
						"TJZZ",
						String.format("%1$.2f",
								parseDouble(map_tj.get("LSJZJE"))));
			} else if ("3".equals(YKACCOUNTPRICE)) {
				columnCKHJ.put(
						"TJZZ",
						String.format("%1$.2f",
								parseDouble(map_tj.get("PFJZJE"))));
			}
			records.add(columnCKHJ);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 格式化数据
		for (Map<String, Object> re : records) {
			if ("出库".equals(re.get("CRFS"))) {
				continue;
			}
			if (parseDouble(re.get("JHHJ")) == 0
					&& parseDouble(re.get("LSHJ")) == 0) {
				continue;
			}
			re.put("JHHJ", String.format("%1$.2f", parseDouble(re.get("JHHJ"))));
			re.put("PFHJ", String.format("%1$.2f", parseDouble(re.get("PFHJ"))));
			re.put("LSHJ", String.format("%1$.2f", parseDouble(re.get("LSHJ"))));
			re.put("JPCJ", String.format("%1$.2f", parseDouble(re.get("JPCJ"))));
			re.put("JXCJ", String.format("%1$.2f", parseDouble(re.get("JXCJ"))));
			re.put("PLCJ", String.format("%1$.2f", parseDouble(re.get("PLCJ"))));
			if (parseDouble(re.get("TJZZ")) != 0) {
				re.put("TJZZ",
						String.format("%1$.2f", parseDouble(re.get("TJZZ"))));
			}
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String userName = user.getUserName();// 用户名
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		String jgid = user.getManageUnit().getId();// 用户的机构ID 
		String YKACCOUNTPRICE = ParameterUtil.getParameter(jgid,
				"YKACCOUNTPRICE", "1", "药库记账标准价格，1表示零售价格，2表示进货价格，3表示批发价格", ctx); 
		response.put("TITLE", jgname + "药库财务月报");
		int zblb = parseInt(request.get("zblb"));
		if (zblb == 1) {
			response.put("ZBLB", "西药");
		} else if (zblb == 2) {
			response.put("ZBLB", "中成药");
		} else if (zblb == 3) {
			response.put("ZBLB", "中草药");
		} else if (zblb == 0) {
			response.put("ZBLB", "全部");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		SimpleDateFormat sdfZBRQ = new SimpleDateFormat("yyyy.MM.dd");
		response.put("CWYF", sdf.format(new Date()));
		response.put("ZBR", userName);
		response.put("ZBRQ", sdfZBRQ.format(new Date()));
		Long xtsb = parseLong(request.get("xtsb"));

		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date dateFrom = sdfDate.parse(parseString(request.get("dateFrom"))
					+ " 00:00:00");
			Date dateTo = sdfDate.parse(parseString(request.get("dateTo"))
					+ " 23:59:59");
			Map<String, Object> parameterCRKJE = new HashMap<String, Object>();
			parameterCRKJE.put("JGID", jgid);
			parameterCRKJE.put("XTSB", xtsb);
			parameterCRKJE.put("dateFrom", dateFrom);
			parameterCRKJE.put("dateTo", dateTo);

			// 调价
			String sql_tj = "SELECT sum(b.PFZZ)  AS PFZZJE,sum(b.PFJZ)  AS PFJZJE,"
					+ " sum(b.LSZZ)  AS LSZZJE,sum(-b.LSJZ) AS LSJZJE"
					+ " FROM YK_TJ02 b,YK_TJ01 a,YK_TYPK c WHERE c.YPXH = b.YPXH AND "
					+ " a.XTSB = b.XTSB AND a.TJFS = b.TJFS AND "
					+ " a.TJDH = b.TJDH AND a.JGID = b.JGID AND "
					+ " b.YFSB = 0 AND a.ZYPB = 1 AND b.JGID =:JGID AND"
					+ " b.XTSB =:XTSB AND a.ZXRQ >:dateFrom AND a.ZXRQ <=:dateTo";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_tj += " AND c.TYPE=:ZBLB";
			} else {
			}
			Map<String, Object> map_tj = dao.doLoad(sql_tj, parameterCRKJE);

			// 入库
			String sql_rk = "select a.RKFS as RKFS,a.FSMC as XM,b.JHHJ as JHHJ,b.PFHJ as PFHJ,b.LSHJ as LSHJ,"
					+ " b.JHHJ-b.PFHJ as JPCJ,b.LSHJ-b.JHHJ as JXCJ,b.LSHJ-b.PFHJ as PLCJ"
					+ " from YK_RKFS a left join "
					+ " (SELECT YK_RK02.RKFS,sum(YK_RK02.JHHJ) AS JHHJ,sum(YK_RK02.PFJE) AS PFHJ,"
					+ "　　sum(YK_RK02.LSJE) AS LSHJ FROM  YK_RK02,YK_TYPK WHERE YK_TYPK.YPXH = YK_RK02.YPXH AND"
					+ "  YK_RK02.JGID =:JGID  AND  YK_RK02.XTSB =:XTSB AND YK_RK02.YSRQ > :dateFrom AND "
					+ "  YK_RK02.YSRQ <=:dateTo ";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_rk += " AND YK_TYPK.TYPE=:ZBLB GROUP BY YK_RK02.RKFS ) b on a.RKFS =b.RKFS"
						+ " where a.jgid=:JGID AND a.XTSB =:XTSB order by a.RKFS";
			} else {
				sql_rk += " GROUP BY YK_RK02.RKFS ) b on a.RKFS =b.RKFS"
						+ " where a.jgid=:JGID AND a.XTSB =:XTSB order by a.RKFS";
			}
			List<Map<String, Object>> list_rk = dao.doSqlQuery(sql_rk,
					parameterCRKJE);
			// 本月收入
			Double bysr = 0d;
			for (Map<String, Object> rk : list_rk) {
				if ("1".equals(YKACCOUNTPRICE)) {
					bysr += parseDouble(rk.get("LSHJ"));
				} else if ("2".equals(YKACCOUNTPRICE)) {
					bysr += parseDouble(rk.get("JHHJ"));
				} else if ("3".equals(YKACCOUNTPRICE)) {
					bysr += parseDouble(rk.get("PFHJ"));
				}
			}
			if ("1".equals(YKACCOUNTPRICE)) {
				bysr += parseDouble(map_tj.get("LSZZJE"));
			} else if ("3".equals(YKACCOUNTPRICE)) {
				bysr += parseDouble(map_tj.get("PFZZJE"));
			}
			response.put("BYSR", String.format("%1$.2f", parseDouble(bysr)));
			// 出库
			String sql_ck = "select a.CKFS as CKFS,a.FSMC as XM,b.JHHJ as JHHJ,b.PFHJ as PFHJ,b.LSHJ as LSHJ,"
					+ " b.JHHJ-b.PFHJ as JPCJ,b.LSHJ-b.JHHJ as JXCJ,b.LSHJ-b.PFHJ as PLCJ"
					+ " from YK_CKFS a left join "
					+ " (SELECT YK_CK02.CKFS,sum(YK_CK02.JHJE) AS JHHJ,sum(YK_CK02.PFJE) AS PFHJ,"
					+ "　　sum(YK_CK02.LSJE) AS LSHJ FROM  YK_CK02,YK_TYPK,YK_CK01 WHERE YK_TYPK.YPXH = YK_CK02.YPXH AND"
					+ "  YK_CK01.XTSB = YK_CK02.XTSB AND YK_CK01.CKFS = YK_CK02.CKFS AND "
					+ "  YK_CK01.CKDH = YK_CK02.CKDH AND YK_CK01.JGID = YK_CK02.JGID AND"
					+ "  YK_CK01.CKPB = 1 AND "
					+ "  YK_CK02.JGID =:JGID  AND  YK_CK02.XTSB =:XTSB AND YK_CK01.CKRQ > :dateFrom AND "
					+ "  YK_CK01.CKRQ <=:dateTo ";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_ck += " AND YK_TYPK.TYPE=:ZBLB GROUP BY YK_CK02.CKFS ) b on a.CKFS =b.CKFS"
						+ " where a.JGID=:JGID AND a.XTSB =:XTSB order by a.CKFS";
			} else {
				sql_ck += " GROUP BY YK_CK02.CKFS ) b on a.CKFS =b.CKFS"
						+ " where a.JGID=:JGID AND a.XTSB =:XTSB order by a.CKFS";
			}
			List<Map<String, Object>> list_ck = dao.doSqlQuery(sql_ck,
					parameterCRKJE);
			// 本月支出
			Double byzc = 0d;
			for (Map<String, Object> ck : list_ck) {
				if ("1".equals(YKACCOUNTPRICE)) {
					byzc += parseDouble(ck.get("LSHJ"));
				} else if ("2".equals(YKACCOUNTPRICE)) {
					byzc += parseDouble(ck.get("JHHJ"));
				} else if ("3".equals(YKACCOUNTPRICE)) {
					byzc += parseDouble(ck.get("PFHJ"));
				}
			}
			if ("1".equals(YKACCOUNTPRICE)) {
				byzc += parseDouble(map_tj.get("LSJZJE"));
			} else if ("3".equals(YKACCOUNTPRICE)) {
				byzc += parseDouble(map_tj.get("PFJZJE"));
			}
			response.put("BYZC", String.format("%1$.2f", parseDouble(byzc)));
			// 上月结存
			Map<String, Object> parameterSYJC = new HashMap<String, Object>();
			parameterSYJC.put("JGID", jgid);
			parameterSYJC.put("XTSB", xtsb);
			Calendar c = Calendar.getInstance();
			c.setTime(dateTo);
			c.add(Calendar.MONTH, -1);
			c.set(Calendar.DAY_OF_MONTH, 10);
			c.set(Calendar.HOUR_OF_DAY, 12);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			SimpleDateFormat sdfDateCWYF = new SimpleDateFormat("yyyy-MM");
			parameterSYJC.put("CWYF", sdfDateCWYF.format(c.getTime()));

			String sqlSYJC = "SELECT sum(a.JHJE) AS JHHJ,sum(a.PFJE) AS PFHJ,"
					+ " sum(a.LSJE) AS LSHJ FROM YK_YJJG a,YK_TYPK b"
					+ " WHERE a.YPXH = b.YPXH AND a.JGID =:JGID AND a.XTSB =:XTSB AND"
					+ " to_char(a.CWYF,'yyyy-mm') =:CWYF";
			if (zblb != 0) {
				parameterSYJC.put("ZBLB", zblb);
				sqlSYJC += " AND b.TYPE=:ZBLB";
			}
			Map<String, Object> map_SYJC = dao.doLoad(sqlSYJC, parameterSYJC);
			double syjc = 0d;
			if ("1".equals(YKACCOUNTPRICE)) {
				syjc = parseDouble(map_SYJC.get("LSHJ"));
			} else if ("2".equals(YKACCOUNTPRICE)) {
				syjc = parseDouble(map_SYJC.get("JHHJ"));
			} else if ("3".equals(YKACCOUNTPRICE)) {
				syjc = parseDouble(map_SYJC.get("PFHJ"));
			}
			response.put("SYJC", String.format("%1$.2f", syjc));
			// 本月结存
			double byjc = syjc + bysr - byzc;
			response.put("BYJC", String.format("%1$.2f", byjc));

			// 票未到
			String sql_pwd = "SELECT sum(b.JHHJ) AS JHHJ,sum(b.PFJE) AS PFHJ,"
					+ " sum(b.LSJE) AS LSHJ  FROM  YK_TYPK c,YK_RK01 a,YK_RK02 b"
					+ " WHERE c.YPXH = b.YPXH AND a.XTSB = b.XTSB AND"
					+ " a.RKFS = b.RKFS AND a.RKDH = b.RKDH AND"
					+ " a.JGID = b.JGID AND a.PWD = 1 AND"
					+ " a.JGID =:JGID AND a.XTSB =:XTSB AND a.RKRQ >:dateFrom AND"
					+ " a.RKRQ <=:dateTo";
			if (zblb != 0) {
				parameterCRKJE.put("ZBLB", zblb);
				sql_pwd += " AND c.TYPE=:ZBLB";
			} else {
			}
			Map<String, Object> map_pwd = dao.doLoad(sql_pwd, parameterCRKJE);
			double pwdjhhj = parseDouble(map_pwd.get("JHHJ"));
			double pwdlshj = parseDouble(map_pwd.get("LSHJ"));
			response.put("PWDJHHJ", String.format("%1$.2f", pwdjhhj));
			response.put("PWDLSHJ", String.format("%1$.2f", pwdlshj));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public String parseString(Object o) {
		if (o == null) {
			return new String("");
		}
		return o + "";
	}
}
