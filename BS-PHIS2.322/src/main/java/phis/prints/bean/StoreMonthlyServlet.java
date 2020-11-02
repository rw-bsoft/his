package phis.prints.bean;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.dao.exception.DataAccessException;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.DynamicPrint;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class StoreMonthlyServlet extends DynamicPrint {
	private final static int columnWidth = 830;
	public final static int textWidth = 80;
	public final static int textHeight = 15;
	public final static int fontSize = 10;
	private final static int titleFontSize = 16;
	private final static int columnHeaderHeight = 18;
	private final static boolean isColumnHeaderFontBond = true;
	private final static int titleHeight = 20;
	private final static int detailHeight = 15;
	private final static int pageFooterHeight = 15;

	/**
	 * 得到报表列集合
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumn(Session ss, Long zblb) {
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(
						"select * from WL_HSLB where hslb not in (select sjhs from WL_HSLB) and sjhs <>-1 and zblb ="
								+ zblb)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list_column;
	}

	/**
	 * 得到报表列集合ZBLB
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumnZBLB(Session ss, int kfxh) {
		List<Map<String, Object>> list_kfzb = ss
				.createSQLQuery("select kfzb from wl_kfxx where kfxh=" + kfxh)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		String kfzb = (String) list_kfzb.get(0).get("KFZB");

		List<Map<String, Object>> list_column = ss
				.createSQLQuery(
						"select ZBLB,ZBMC from WL_ZBLB where ZBLB in(" + kfzb
								+ ") order by ZBLB")
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		return list_column;
	}

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String type = (String) requestData.get("type");
			Integer bsfs = (Integer) requestData.get("bsfs");
			if ("HZ".equals(type)) {
				return doPrintKFHZ(request, response, config);
			} else if ("YB".equals(type)) {
				return doPrintKFYB(request, response, config);
			} else if ("BSHZ".equals(type) && bsfs == 0) {
				return doPrintBSHZKF(request, response, config);
			} else if ("BSHZ".equals(type) && bsfs == 1) {
				return doPrintBSHZKS(request, response, config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 打印库房月报
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintKFYB(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			int KFXH = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (KFXH == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid + "' and a.YWLB='" + YWLB + "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "库房收支月报表";//(String) config.get("title");
			String date = (String) requestData.get("date");
			Long zblb = Long.parseLong(requestData.get("zblb") + "");
			List<Map<String, Object>> list_column = getListColumn(ss, zblb);

			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

			// 格式化日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
			Integer year = Integer.parseInt(date.split("-")[0]);
			Integer month = Integer.parseInt(date.split("-")[1]);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DATE, 10);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.YEAR, year);
			cal1.set(Calendar.MONTH, month - 1);
			cal1.set(Calendar.DATE, 10);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);

			// 获取表单参数
			// 上期结存
			cal.set(Calendar.MONTH, month - 2);
			String cwyf = sdfcwyf.format(cal.getTime());
			String cwyf1 = sdfcwyf.format(cal1.getTime());
			StringBuffer hql_SQJC = new StringBuffer();
			hql_SQJC.append(
					" select wl_wzzd.hslb ,sum( wl_yjjg.qcje ) qcje , sum( wl_yjjg.rkje ) rkje ,"
							+ " sum( wl_yjjg.ckje ) ckje , sum( wl_yjjg.qmje ) qmje ,sum( wl_yjjg.bsje ) bsje ,"
							+ " sum( wl_yjjg.pyje ) pyje from wl_yjjl, wl_yjjg , wl_wzzd "
							+ "where wl_wzzd.wzxh = wl_yjjg.wzxh and wl_yjjg.jzxh = wl_yjjl.jzxh and wl_yjjl.kfxh  = ")
					.append(KFXH).append(" and wl_yjjl.zblb =").append(zblb)
					.append(" and wl_yjjl.cwyf =").append(cwyf1)
					.append(" group by wl_wzzd.hslb");
			List<Map<String, Object>> list_data_SQJC = ss
					.createSQLQuery(hql_SQJC.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			if (list_data_SQJC.size() > 0) {
				HashMap<String, Object> data_SQJC = new HashMap<String, Object>();
				// data_SQJC.put("TOTAL", 1);
				data_SQJC.put("XMMC", "上期结存");
				for (Map<String, Object> d : list_data_SQJC) {
					data_SQJC.put(d.get("HSLB") + "",
							String.format("%1$.2f", d.get("QMJE")));
				}
				l_data.add(data_SQJC);
				list_data.addAll(list_data_SQJC);
			} else {
				list_data_SQJC.clear();
				StringBuffer hql_SQJC1 = new StringBuffer();
				hql_SQJC1
						.append(" select wl_wzzd.hslb ,sum( wl_yjjg.qcje ) qcje , sum( wl_yjjg.rkje ) rkje ,"
								+ " sum( wl_yjjg.ckje ) ckje , sum( wl_yjjg.qmje ) qmje ,sum( wl_yjjg.bsje ) bsje ,"
								+ " sum( wl_yjjg.pyje ) pyje from wl_yjjl, wl_yjjg , wl_wzzd "
								+ "where wl_wzzd.wzxh = wl_yjjg.wzxh and wl_yjjg.jzxh = wl_yjjl.jzxh and wl_yjjl.kfxh  = ")
						.append(KFXH).append(" and wl_yjjl.zblb =")
						.append(zblb).append(" and wl_yjjl.cwyf =")
						.append(cwyf).append(" group by wl_wzzd.hslb");
				list_data_SQJC = ss.createSQLQuery(hql_SQJC1.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				HashMap<String, Object> data_SQJC = new HashMap<String, Object>();
				// data_SQJC.put("TOTAL", 1);
				data_SQJC.put("XMMC", "上期结存");
				for (Map<String, Object> d : list_data_SQJC) {
					data_SQJC.put(d.get("HSLB") + "",
							String.format("%1$.2f", d.get("QMJE")));
				}
				l_data.add(data_SQJC);

				// 本期入库、本期出库、本期报损、本期盘盈
				cal.set(Calendar.MONTH, month - 1);
				cal.set(Calendar.DATE, 1);
				String dateFrom = sdf.format(cal.getTime());
				int yjDate = Integer.parseInt(ParameterUtil.getParameter(jgid,
						"YJSJ_KF" + KFXH, "32", "库房月结时间 对应一个月的31天  32为月底结 ",
						ctx));
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (yjDate < lastDay) {
					cal.set(Calendar.DATE, yjDate + 1);
				} else {
					cal.set(Calendar.DATE, lastDay + 1);
				}
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				String dateTo = sdf.format(cal.getTime());
				StringBuffer hql = new StringBuffer();
				hql.append("select wl_wzzd.hslb ,sum( a.rkje ) rkje ,sum( a.ckje ) ckje ,sum ( a.pyje ) pyje ,"
						+ " sum( a.bsje ) bsje from  wl_wzzd,"
						+ "(select mxxh , wzxh ,cjxh,wzje rkje , 0.0000 ckje , 0.0000 pyje , 0.0000 bsje from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = 1 and ( wl_zcmx.DJLX = 'RK'  ) and wl_zcmx.zblb = "
						+ zblb
						+ " and	wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0"
						+ " Union all select mxxh , wzxh ,cjxh,( 0 - wzje ) rkje , 0.0000 ckje , 0.0000 pyje , 0.0000 bsje"
						+ " from wl_zcmx where wl_zcmx.kfxh ="
						+ KFXH
						+ " and	wl_zcmx.ywlb = -1  and	wl_zcmx.DJLX = 'RK' and"
						+ " wl_zcmx.zblb ="
						+ zblb
						+ " and wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and	wl_zcmx.fsrq    <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0	union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , wzje ckje , 0.0000 pyje , 0.0000 bsje 	from wl_zcmx"
						+ " where wl_zcmx.kfxh ="
						+ KFXH
						+ " and	wl_zcmx.ywlb = -1 and ( wl_zcmx.djlx = 'CK' or wl_zcmx.djlx = 'SL' ) and"
						+ "	wl_zcmx.zblb = "
						+ zblb
						+ " and	wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.fsrq    <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0 union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , ( 0 -  wzje )  ckje , 0.0000 pyje , 0.0000 bsje"
						+ " from wl_zcmx	where wl_zcmx.kfxh = "
						+ KFXH
						+ " and 	wl_zcmx.ywlb = 1 and ( wl_zcmx.djlx = 'CK'  ) "
						+ " and wl_zcmx.zblb = "
						+ zblb
						+ "  and	wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.ywfs =0	union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , 0.0000 ckje , ( 0 - wzje )  pyje, 0.0000 bsje"
						+ " from wl_zcmx 	where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = -1 and wl_zcmx.djlx = 'CK' and"
						+ " wl_zcmx.zblb = "
						+ zblb
						+ "  and  wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and 	wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =1 union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , 0.0000 ckje , wzje   pyje, 0.0000 bsje	from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = 1 and wl_zcmx.djlx = 'RK' and"
						+ " wl_zcmx.zblb = "
						+ zblb
						+ "  and wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.ywfs =1	union all "
						+ " select mxxh , wzxh ,cjxh, 0.0000 rkje , 0.0000  ckje , 0.0000 pyje , wzje bsje from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ " and	wl_zcmx.zblb = "
						+ zblb
						+ "  and wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and"
						+ " wl_zcmx.fsrq    <=  to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.DJLX = 'BS' AND wl_zcmx.ywfs = 0) a "
						+ " where a.wzxh = wl_wzzd.wzxh group by wl_wzzd.hslb");
				list_data = ss.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
			}
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("XMMC");
			cm1.setText("项目名称");
			map.put("XMMC", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("HSLB").toString());
				cm.setText((String) column.get("HSMC"));
				map.put(column.get("HSLB").toString(), cm);
			}
			ColumnModel cm3 = new ColumnModel();
			cm3.setName("ColumnTOTAL");
			cm3.setText("合计金额");
			map.put("ColumnTOTAL", cm3);

			// 生成数据
			HashMap<String, Object> data_bqrk = new HashMap<String, Object>();
			data_bqrk.put("XMMC", "本期入库");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqrk.put(map_data.get("HSLB") + "",
						String.format("%1$.2f", map_data.get("RKJE")));
			}
			l_data.add(data_bqrk);

			HashMap<String, Object> data_bqck = new HashMap<String, Object>();
			data_bqck.put("XMMC", "本期出库");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqck.put(map_data.get("HSLB") + "",
						String.format("%1$.2f", map_data.get("CKJE")));
			}
			l_data.add(data_bqck);

			HashMap<String, Object> data_bqbs = new HashMap<String, Object>();
			data_bqbs.put("XMMC", "本期报损");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqbs.put(map_data.get("HSLB") + "",
						String.format("%1$.2f", map_data.get("BSJE")));
			}
			l_data.add(data_bqbs);

			HashMap<String, Object> data_bqpy = new HashMap<String, Object>();
			data_bqpy.put("XMMC", "本期盘盈");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqpy.put(map_data.get("HSLB") + "",
						String.format("%1$.2f", map_data.get("PYJE")));
			}
			l_data.add(data_bqpy);

			HashMap<String, Object> data_bqjc = new HashMap<String, Object>();
			data_bqjc.put("XMMC", "本期结存");
			for (Map<String, Object> d : list_data_SQJC) {
				data_bqjc.put(d.get("HSLB") + "",
						String.format("%1$.2f", d.get("QMJE")));
			}
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				double sqjc = 0;
				if (data_bqjc.containsKey(map_data.get("HSLB") + "")) {
					sqjc = Double.parseDouble((data_bqjc.get(map_data
							.get("HSLB") + ""))
							+ "");
				}
				double bqjc = sqjc
						+ Double.parseDouble(map_data.get("RKJE") + "")
						- Double.parseDouble(map_data.get("CKJE") + "")
						- Double.parseDouble(map_data.get("BSJE") + "")
						+ Double.parseDouble(map_data.get("PYJE") + "");
				data_bqjc.put(map_data.get("HSLB") + "",
						String.format("%1$.2f", bqjc));
			}
			l_data.add(data_bqjc);

			for (int i = 0; i < l_data.size(); i++) {
				HashMap<String, Object> data = l_data.get(i);
				Set<String> set = data.keySet();
				double columnTotal = 0;
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String name = it.next();
					if (!name.equals("TOTAL") && !name.equals("XMMC")
							&& !name.equals("ColumnTOTAL")) {
						columnTotal += Double.parseDouble(data.get(name) + "");
					}
				}
				data.put("ColumnTOTAL", String.format("%1$.2f", columnTotal));
			}
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, true);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();

			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}
			return prints;
		} finally {
			ss.close();
		}
	}

	/**
	 * 打印库房汇总
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintKFHZ(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			int KFXH = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (KFXH == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid + "' and a.YWLB='" + YWLB + "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "库房收支汇总报表";//(String) config.get("title");
			String date = (String) requestData.get("date");
			List<Map<String, Object>> list_column = getListColumnZBLB(ss, KFXH);

			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

			// 格式化日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
			Integer year = Integer.parseInt(date.split("-")[0]);
			Integer month = Integer.parseInt(date.split("-")[1]);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DATE, 10);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.YEAR, year);
			cal1.set(Calendar.MONTH, month - 1);
			cal1.set(Calendar.DATE, 10);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			// 获取表单参数
			// 上期结存
			cal.set(Calendar.MONTH, month - 2);
			String cwyf = sdfcwyf.format(cal.getTime());
			String cwyf1 = sdfcwyf.format(cal1.getTime());
			StringBuffer hql_SQJC = new StringBuffer();

			hql_SQJC.append(
					" select wl_wzzd.zblb ,sum( wl_yjjg.qcje ) qcje , sum( wl_yjjg.rkje ) rkje ,"
							+ " sum( wl_yjjg.ckje ) ckje , sum( wl_yjjg.qmje ) qmje ,sum( wl_yjjg.bsje ) bsje ,"
							+ " sum( wl_yjjg.pyje ) pyje from wl_yjjl, wl_yjjg , wl_wzzd "
							+ "where wl_wzzd.wzxh = wl_yjjg.wzxh and wl_yjjg.jzxh = wl_yjjl.jzxh and wl_yjjl.kfxh  = ")
					.append(KFXH).append(" and wl_yjjl.cwyf =").append(cwyf1)
					.append(" group by wl_wzzd.zblb");
			List<Map<String, Object>> list_data_SQJC = ss
					.createSQLQuery(hql_SQJC.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			if (list_data_SQJC.size() > 0) {
				HashMap<String, Object> data_SQJC = new HashMap<String, Object>();
				data_SQJC.put("XMMC", "上期结存");
				for (Map<String, Object> d : list_data_SQJC) {
					data_SQJC.put(d.get("ZBLB") + "",
							String.format("%1$.2f", d.get("QMJE")));
				}
				l_data.add(data_SQJC);
				list_data.addAll(list_data_SQJC);
			} else {
				list_data_SQJC.clear();
				StringBuffer hql_SQJC1 = new StringBuffer();
				hql_SQJC1
						.append(" select wl_wzzd.zblb ,sum( wl_yjjg.qcje ) qcje , sum( wl_yjjg.rkje ) rkje ,"
								+ " sum( wl_yjjg.ckje ) ckje , sum( wl_yjjg.qmje ) qmje ,sum( wl_yjjg.bsje ) bsje ,"
								+ " sum( wl_yjjg.pyje ) pyje from wl_yjjl, wl_yjjg , wl_wzzd "
								+ "where wl_wzzd.wzxh = wl_yjjg.wzxh and wl_yjjg.jzxh = wl_yjjl.jzxh and wl_yjjl.kfxh  = ")
						.append(KFXH).append(" and wl_yjjl.cwyf =")
						.append(cwyf).append(" group by wl_wzzd.zblb");
				list_data_SQJC = ss.createSQLQuery(hql_SQJC1.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				HashMap<String, Object> data_SQJC = new HashMap<String, Object>();
				data_SQJC.put("XMMC", "上期结存");
				for (Map<String, Object> d : list_data_SQJC) {
					data_SQJC.put(d.get("ZBLB") + "",
							String.format("%1$.2f", d.get("QMJE")));
				}
				l_data.add(data_SQJC);
				// 本期入库、本期出库、本期报损、本期盘盈
				cal.set(Calendar.MONTH, month - 1);
				cal.set(Calendar.DATE, 1);
				String dateFrom = sdf.format(cal.getTime());
				int yjDate = Integer.parseInt(ParameterUtil.getParameter(jgid,
						"YJSJ_KF" + KFXH, "32", "库房月结时间 对应一个月的31天  32为月底结 ",
						ctx));
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (yjDate < lastDay) {
					cal.set(Calendar.DATE, yjDate + 1);
				} else {
					cal.set(Calendar.DATE, lastDay + 1);
				}
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				String dateTo = sdf.format(cal.getTime());
				StringBuffer hql = new StringBuffer();
				hql.append("select wl_wzzd.zblb ,sum( a.rkje ) rkje ,sum( a.ckje ) ckje ,sum ( a.pyje ) pyje ,"
						+ " sum( a.bsje ) bsje from  wl_wzzd,"
						+ "(select mxxh , wzxh ,cjxh,wzje rkje , 0.0000 ckje , 0.0000 pyje , 0.0000 bsje from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = 1 and ( wl_zcmx.DJLX = 'RK'  )  and	wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0"
						+ " Union all select mxxh , wzxh ,cjxh,( 0 - wzje ) rkje , 0.0000 ckje , 0.0000 pyje , 0.0000 bsje"
						+ " from wl_zcmx where wl_zcmx.kfxh ="
						+ KFXH
						+ " and	wl_zcmx.ywlb = -1  and	wl_zcmx.DJLX = 'RK' and wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and	wl_zcmx.fsrq    <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0	union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , wzje ckje , 0.0000 pyje , 0.0000 bsje 	from wl_zcmx"
						+ " where wl_zcmx.kfxh ="
						+ KFXH
						+ " and	wl_zcmx.ywlb = -1 and ( wl_zcmx.djlx = 'CK'   )  and	wl_zcmx.fsrq   >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.fsrq    <=to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =0 union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , ( 0 -  wzje )  ckje , 0.0000 pyje , 0.0000 bsje"
						+ " from wl_zcmx	where wl_zcmx.kfxh = "
						+ KFXH
						+ " and 	wl_zcmx.ywlb = 1 and ( wl_zcmx.djlx = 'CK'  ) and	wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.ywfs =0	union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , 0.0000 ckje , ( 0 - wzje )  pyje, 0.0000 bsje"
						+ " from wl_zcmx 	where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = -1 and wl_zcmx.djlx = 'CK'  and  wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and 	wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.ywfs =1 union all "
						+ " select mxxh ,wzxh ,cjxh, 0.0000 rkje , 0.0000 ckje , wzje   pyje, 0.0000 bsje	from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ " and wl_zcmx.ywlb = 1 and wl_zcmx.djlx = 'RK'  and wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.fsrq    <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ " and wl_zcmx.ywfs =1	union all "
						+ " select mxxh , wzxh ,cjxh, 0.0000 rkje , 0.0000  ckje , 0.0000 pyje , wzje bsje from wl_zcmx"
						+ " where wl_zcmx.kfxh = "
						+ KFXH
						+ "  and wl_zcmx.fsrq   >=  to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and"
						+ " wl_zcmx.fsrq    <=  to_date('"
						+ dateTo
						+ "','yyyy-mm-dd HH24:mi:ss')"
						+ "  and wl_zcmx.DJLX = 'BS' AND wl_zcmx.ywfs = 0) a "
						+ " where a.wzxh = wl_wzzd.wzxh group by wl_wzzd.zblb");
				list_data = ss.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
			}
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("XMMC");
			cm1.setText("项目名称");
			map.put("XMMC", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("ZBLB").toString());
				cm.setText((String) column.get("ZBMC"));
				map.put(column.get("ZBLB").toString(), cm);
			}
			ColumnModel cm3 = new ColumnModel();
			cm3.setName("ColumnTOTAL");
			cm3.setText("合计金额");
			map.put("ColumnTOTAL", cm3);

			// 生成数据
			HashMap<String, Object> data_bqrk = new HashMap<String, Object>();
			data_bqrk.put("XMMC", "本期入库");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqrk.put(map_data.get("ZBLB") + "",
						String.format("%1$.2f", map_data.get("RKJE")));
			}
			l_data.add(data_bqrk);

			HashMap<String, Object> data_bqck = new HashMap<String, Object>();
			data_bqck.put("XMMC", "本期出库");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqck.put(map_data.get("ZBLB") + "",
						String.format("%1$.2f", map_data.get("CKJE")));
			}
			l_data.add(data_bqck);

			HashMap<String, Object> data_bqbs = new HashMap<String, Object>();
			data_bqbs.put("XMMC", "本期报损");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqbs.put(map_data.get("ZBLB") + "",
						String.format("%1$.2f", map_data.get("BSJE")));
			}
			l_data.add(data_bqbs);

			HashMap<String, Object> data_bqpy = new HashMap<String, Object>();
			data_bqpy.put("XMMC", "本期盘盈");
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				data_bqpy.put(map_data.get("ZBLB") + "",
						String.format("%1$.2f", map_data.get("PYJE")));
			}
			l_data.add(data_bqpy);

			HashMap<String, Object> data_bqjc = new HashMap<String, Object>();
			data_bqjc.put("XMMC", "本期结存");
			for (Map<String, Object> d : list_data_SQJC) {
				data_bqjc.put(d.get("ZBLB") + "",
						String.format("%1$.2f", d.get("QMJE")));
			}
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				Double sqjc = 0D;
				if (data_bqjc.containsKey(map_data.get("ZBLB") + "")) {
					sqjc = Double.parseDouble((data_bqjc.get(map_data
							.get("ZBLB") + ""))
							+ "");
				}
				Double bqjc = sqjc
						+ Double.parseDouble(map_data.get("RKJE") + "")
						- Double.parseDouble(map_data.get("CKJE") + "")
						- Double.parseDouble(map_data.get("BSJE") + "")
						+ Double.parseDouble(map_data.get("PYJE") + "");
				data_bqjc.put(map_data.get("ZBLB") + "",
						String.format("%1$.2f", bqjc));
			}
			l_data.add(data_bqjc);

			for (int i = 0; i < l_data.size(); i++) {
				HashMap<String, Object> data = l_data.get(i);
				Set<String> set = data.keySet();
				Double columnTotal = 0D;
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String name = it.next();
					if (!name.equals("TOTAL") && !name.equals("XMMC")
							&& !name.equals("ColumnTOTAL")) {
						columnTotal += Double.parseDouble(data.get(name) + "");
					}
				}
				data.put("ColumnTOTAL", String.format("%1$.2f", columnTotal));
			}
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, true);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();

			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}
			return prints;
		} finally {
			ss.close();
		}
	}

	/**
	 * 打印报损汇总，在库报损
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintBSHZKF(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			int KFXH = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (KFXH == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid + "' and a.YWLB='" + YWLB + "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "报损汇总报表"; //(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			Long zblb = Long.parseLong(requestData.get("zblb") + "");
			Integer bsfs = (Integer) requestData.get("bsfs");

			StringBuffer hql = new StringBuffer();
			hql.append(
					" select  wl_kfxx.kfmc,wl_wzzd.zblb, wl_wzzd.hslb , sum( wl_bs02.wzje ) as wzje  from wl_bs01 ,"
							+ " wl_bs02 , wl_wzzd ,wl_kfxx where wl_bs01.djxh = wl_bs02.djxh	and wl_wzzd.wzxh = wl_bs02.wzxh"
							+ " and wl_bs01.kfxh = wl_kfxx.kfxh and wl_bs01.djzt = 2 and wl_bs01.bsfs = ")
					.append(bsfs)
					.append("	and wl_bs01.kfxh = ")
					.append(KFXH)
					.append(" and wl_wzzd.zblb=")
					.append(zblb)
					.append(" and	wl_bs01.jzrq >= to_date('")
					.append(dateFrom)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append(" and wl_bs01.jzrq <= to_date('")
					.append(dateTo)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append("and wl_bs01.djzt = 2 and wl_bs02.wzxh = wl_wzzd.wzxh ")
					.append(" group by wl_kfxx.kfmc,wl_wzzd.zblb , wl_wzzd.hslb ");

			List<Map<String, Object>> list_data = ss
					.createSQLQuery(hql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();

			List<Map<String, Object>> list_column = getListColumn(ss, zblb);

			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

			// 生成表结构
			ColumnModel cm0 = new ColumnModel();
			cm0.setName("TOTAL");
			cm0.setText("序号");
			cm0.setWdith(40);
			map.put("TOTAL", cm0);
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("BSKF");
			cm1.setText("报损库房");
			map.put("BSKF", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("HSLB").toString());
				cm.setText((String) column.get("HSMC"));
				map.put(column.get("HSLB").toString(), cm);
			}
			ColumnModel cm3 = new ColumnModel();
			cm3.setName("ColumnTOTAL");
			cm3.setText("合计金额");
			map.put("ColumnTOTAL", cm3);

			// 生成数据
			for (int i = 0; i < list_data.size(); i++) {
				boolean isExist = false;
				int existIndex = 0;
				Map<String, Object> map_data = list_data.get(i);
				if (l_data.size() == 0) {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("TOTAL", i + 1);
					data.put("BSKF", map_data.get("KFMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
					l_data.add(data);
					continue;
				}
				// 如果l_data中有元素
				if (l_data.size() > 0) {
					// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果存在
						if (d.get("BSKF").equals(map_data.get("KFMC"))) {
							isExist = true;
							existIndex = j;
							break;
						}
					}
				}
				if (isExist) {
					HashMap<String, Object> existData = l_data.get(existIndex);
					existData.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
				} else {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("TOTAL", i + 1);
					data.put("BSKF", map_data.get("KFMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
					l_data.add(data);
				}
			}

			for (int i = 0; i < l_data.size(); i++) {
				HashMap<String, Object> data = l_data.get(i);
				Set<String> set = data.keySet();
				Double columnTotal = 0D;
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String name = it.next();
					if (!name.equals("TOTAL") && !name.equals("BSKF")
							&& !name.equals("ColumnTOTAL")) {
						columnTotal += Double.parseDouble(data.get(name) + "");
					}
				}
				data.put("ColumnTOTAL", String.format("%1$.2f", columnTotal));
			}

			// 计算统计
			HashMap<String, Object> dataTotal = new HashMap<String, Object>();
			dataTotal.put("TOTAL", "总计");
			for (Map<String, Object> column : list_column) {
				Double value = 0D;
				Double allTotal = 0D;
				for (int j = 0; j < l_data.size(); j++) {
					Map<String, Object> d = l_data.get(j);
					if (d.get(column.get("HSLB").toString()) != null) {
						value += Double.parseDouble(d.get(column.get("HSLB")
								.toString()) + "");
					}
					allTotal += Double.parseDouble(d.get("ColumnTOTAL") + "");
				}
				dataTotal.put(column.get("HSLB").toString(),
						String.format("%1$.2f", value));
				dataTotal.put("ColumnTOTAL", String.format("%1$.2f", allTotal));
			}
			l_data.add(dataTotal);

			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, true);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();

			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}
			return prints;
		} finally {
			ss.close();
		}
	}

	/**
	 * 打印报损汇总，科室报损
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintBSHZKS(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			int KFXH = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (KFXH == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ jgid + "' and a.YWLB='" + YWLB + "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "报损汇总报表"; //(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			Long zblb = Long.parseLong(requestData.get("zblb") + "");
			Integer bsfs = (Integer) requestData.get("bsfs");

			StringBuffer hql = new StringBuffer();
			hql.append(
					" select  sys_office.officename as ksmc,wl_wzzd.zblb, wl_wzzd.hslb , sum( wl_bs02.wzje ) as wzje  from wl_bs01 ,"
							+ " wl_bs02 , wl_wzzd ,sys_office where wl_bs01.djxh = wl_bs02.djxh	and wl_wzzd.wzxh = wl_bs02.wzxh"
							+ " and wl_bs01.bsks = sys_office.id and wl_bs01.djzt = 2 and wl_bs01.bsfs = ")
					.append(bsfs)
					.append("	and wl_bs01.kfxh = ")
					.append(KFXH)
					.append(" and wl_wzzd.zblb=")
					.append(zblb)
					.append(" and	wl_bs01.jzrq >= to_date('")
					.append(dateFrom)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append(" and wl_bs01.jzrq <= to_date('")
					.append(dateTo)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append("and wl_bs01.djzt = 2 and wl_bs02.wzxh = wl_wzzd.wzxh ")
					.append(" group by sys_office.officename,wl_wzzd.zblb , wl_wzzd.hslb ");

			List<Map<String, Object>> list_data = ss
					.createSQLQuery(hql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();

			List<Map<String, Object>> list_column = getListColumn(ss, zblb);

			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

			// 生成表结构
			ColumnModel cm0 = new ColumnModel();
			cm0.setName("TOTAL");
			cm0.setText("序号");
			cm0.setWdith(40);
			map.put("TOTAL", cm0);
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("BSKS");
			cm1.setText("报损科室或部门");
			map.put("BSKS", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("HSLB").toString());
				cm.setText((String) column.get("HSMC"));
				map.put(column.get("HSLB").toString(), cm);
			}
			ColumnModel cm3 = new ColumnModel();
			cm3.setName("ColumnTOTAL");
			cm3.setText("合计金额");
			map.put("ColumnTOTAL", cm3);

			// 生成数据
			for (int i = 0; i < list_data.size(); i++) {
				boolean isExist = false;
				int existIndex = 0;
				Map<String, Object> map_data = list_data.get(i);
				if (l_data.size() == 0) {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("TOTAL", i + 1);
					data.put("BSKS", map_data.get("KSMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
					l_data.add(data);
					continue;
				}
				// 如果l_data中有元素
				if (l_data.size() > 0) {
					// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果存在
						if (d.get("BSKS").equals(map_data.get("KSMC"))) {
							isExist = true;
							existIndex = j;
							break;
						}
					}
				}
				if (isExist) {
					HashMap<String, Object> existData = l_data.get(existIndex);
					existData.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
				} else {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("TOTAL", i + 1);
					data.put("BSKS", map_data.get("KSMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.2f", map_data.get("WZJE")));
					l_data.add(data);
				}
			}

			for (int i = 0; i < l_data.size(); i++) {
				HashMap<String, Object> data = l_data.get(i);
				Set<String> set = data.keySet();
				Double columnTotal = 0D;
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String name = it.next();
					if (!name.equals("TOTAL") && !name.equals("BSKS")
							&& !name.equals("ColumnTOTAL")) {
						columnTotal += Double.parseDouble(data.get(name) + "");
					}
				}
				data.put("ColumnTOTAL", String.format("%1$.2f", columnTotal));
			}

			// 计算统计
			HashMap<String, Object> dataTotal = new HashMap<String, Object>();
			dataTotal.put("TOTAL", "总计");
			for (Map<String, Object> column : list_column) {
				Double value = 0D;
				Double allTotal = 0D;
				for (int j = 0; j < l_data.size(); j++) {
					Map<String, Object> d = l_data.get(j);
					if (d.get(column.get("HSLB").toString()) != null) {
						value += Double.parseDouble(d.get(column.get("HSLB")
								.toString()) + "");
					}
					allTotal += Double.parseDouble(d.get("ColumnTOTAL") + "");
				}
				dataTotal.put(column.get("HSLB").toString(),
						String.format("%1$.2f", value));
				dataTotal.put("ColumnTOTAL", String.format("%1$.2f", allTotal));
			}
			l_data.add(dataTotal);

			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel, true);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();

			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										l_data)));
			}
			return prints;
		} finally {
			ss.close();
		}
	}

	/**
	 * @param designs
	 *            空的一个JasperDesign集合，原因：当所有的列宽度超过一页宽度时候，自动扩展一页（递归实现）
	 * @param title
	 *            标题
	 * @param columnModel
	 *            列模型
	 * @return
	 * @throws JRException
	 */
	public List<JasperReport> getDynamicJasperReport(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, boolean isSeparate) throws JRException {
		List<JasperReport> reports = new ArrayList<JasperReport>();
		List<JasperDesign> process_designs = getDynamicJasperDesign(designs,
				title, columnModel, isSeparate);
		for (JasperDesign design : process_designs) {
			reports.add(JasperCompileManager.compileReport(design));
		}
		return reports;
	}

	private List<JasperDesign> getDynamicJasperDesign(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, boolean isSeparate) throws JRException {
		JasperDesign design = DynaGridPrintUtil.getJasperDesign(title, true);
		designs.add(design);
		JRDesignBand titleBand = DynaGridPrintUtil.getJRDesignBand(titleHeight);
		JRDesignBand columnHeaderBand = DynaGridPrintUtil
				.getJRDesignBand(columnHeaderHeight);
		JRDesignBand detailBand = DynaGridPrintUtil
				.getJRDesignBand(detailHeight);
		JRDesignBand pageFooter = DynaGridPrintUtil
				.getJRDesignBand(pageFooterHeight);
		setBand(design, titleBand, columnHeaderBand, detailBand, pageFooter);
		// 标题
		JRDesignStaticText titleText = DynaGridPrintUtil.getJRDesignStaticText(
				title, titleFontSize, true);
		parseTitleText(titleText);
		titleBand.addElement(titleText);
		int totalWidth = 0;
		if (!isSeparate) {
			for (int i = 0; i < columnModel.length; i++) {
				if (columnModel[i].isHide()) {
					continue;
				}
				int width = columnModel[i].getWdith();
				totalWidth += width;
				// 列标题
				JRDesignStaticText staticText = DynaGridPrintUtil
						.getJRDesignStaticText(columnModel[i].getText(),
								fontSize, isColumnHeaderFontBond);
				staticText.setWidth(width);
				staticText.setX(totalWidth - width);
				staticText.getLineBox().getPen().setLineWidth(1f);
				staticText.getLineBox().getPen()
						.setLineStyle(LineStyleEnum.SOLID);
				columnHeaderBand.addElement(staticText);
				// 字段
				JRDesignField field = new JRDesignField();
				field.setName(columnModel[i].getName());
				field.setValueClass(String.class);
				design.addField(field);
				// 字段组件
				JRDesignTextField textField = DynaGridPrintUtil
						.getJRDesignTextField(columnModel[i].getName(),
								String.class);
				textField.setWidth(width);
				textField.setX(totalWidth - width);
				textField.getLineBox().getPen().setLineWidth(1f);
				textField.getLineBox().getPen()
						.setLineStyle(LineStyleEnum.SOLID);
				// textField.setStretchWithOverflow(true); //是否换行打印
				detailBand.addElement(textField);
				// 底部页码
				getPageFooterTextField(pageFooter);
			}
			return designs;
		}
		totalWidth = 0;
		for (int i = 0; i < columnModel.length; i++) {
			if (columnModel[i].isHide()) {
				continue;
			}
			int width = columnModel[i].getWdith();
			totalWidth += width;
			if (totalWidth > columnWidth) {
				int z = 1;
				while (columnModel[i - z].isHide()) {
					z++;
				}
				columnModel[i - z].setWdith(columnModel[i - z].getWdith()
						+ columnWidth - (totalWidth - width));
				break;
			}
		}
		totalWidth = 0;
		for (int i = 0; i < columnModel.length; i++) {
			if (columnModel[i].isHide()) {
				continue;
			}
			int width = columnModel[i].getWdith();
			totalWidth += width;
			if (totalWidth > columnWidth) {
				ColumnModel[] models = new ColumnModel[columnModel.length - i
						+ 1];
				for (int j = 0; j < models.length; j++) {
					models[j] = new ColumnModel();
					if (j == 0) {
						models[0].setName(columnModel[0].getName());
						models[0].setText(columnModel[0].getText());
					} else {
						models[j].setName(columnModel[i + j - 1].getName());
						models[j].setText(columnModel[i + j - 1].getText());
					}
				}

				// System.arraycopy(columnModel, i, models, 1, models.length);
				return getDynamicJasperDesign(designs, title, models,
						isSeparate);
			}
			// 列标题
			JRDesignStaticText staticText = DynaGridPrintUtil
					.getJRDesignStaticText(columnModel[i].getText(), fontSize,
							isColumnHeaderFontBond);
			staticText.setWidth(width);
			staticText.setX(totalWidth - width);
			staticText.getLineBox().getPen().setLineWidth(1f);
			staticText.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
			columnHeaderBand.addElement(staticText);
			// 字段
			JRDesignField field = new JRDesignField();
			field.setName(columnModel[i].getName());
			field.setValueClass(String.class);
			design.addField(field);
			// 字段组件
			JRDesignTextField textField = DynaGridPrintUtil
					.getJRDesignTextField(columnModel[i].getName(),
							String.class);
			textField.setWidth(width);
			textField.setX(totalWidth - width);
			textField.getLineBox().getPen().setLineWidth(1f);
			textField.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
			// textField.setStretchWithOverflow(true); //是否换行打印
			detailBand.addElement(textField);
			// 底部页码
			getPageFooterTextField(pageFooter);
		}
		return designs;
	}

	private void setBand(JasperDesign jd, JRDesignBand title,
			JRDesignBand columnheader, JRDesignBand detail,
			JRDesignBand pagefooter) {
		jd.setTitle(title);
		jd.setColumnHeader(columnheader);
		JRDesignSection jrds = (JRDesignSection) jd.getDetailSection();
		jrds.addBand(detail);
		jd.setPageFooter(pagefooter);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-1-9
	 * @description 标题
	 * @updateInfo
	 * @param titleText
	 */
	private static void parseTitleText(JRDesignStaticText titleText) {
		titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(0);
		titleText.setY(0);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-1-9
	 * @description 获取底部页码
	 * @updateInfo
	 * @param pageFooter
	 */
	private static void getPageFooterTextField(JRDesignBand pageFooter) {
		JRDesignTextField f1 = DynaGridPrintUtil.getJRDesignTextField("",
				String.class);
		JRDesignTextField f2 = DynaGridPrintUtil.getJRDesignTextField("",
				String.class);
		f1.setEvaluationTime(EvaluationTimeEnum.NOW); // JREvaluationTime .now
		f2.setEvaluationTime(EvaluationTimeEnum.REPORT); // .report
		((JRDesignExpression) f1.getExpression())
				.setText("\"第\"+$V{PAGE_NUMBER}+\"页\"");
		((JRDesignExpression) f2.getExpression())
				.setText(" \"共\"+$V{PAGE_NUMBER}+\"页\"");
		f1.setX(320);
		f2.setX(400);
		pageFooter.addElement(f1);
		pageFooter.addElement(f2);
	}
}
