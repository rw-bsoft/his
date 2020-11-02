package phis.prints.bean;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dao.exception.DataAccessException;
import ctd.dictionary.DictionaryController;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.DynamicPrint;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class DoctorsAccountingServlet extends DynamicPrint {
	// private final static String defaultContent = "text/html;charset=UTF-8";
	// private final static Log logger = LogFactory.getLog(PrintServlet.class);
	// private final static String defaultErrorMsg =
	// "打印服务器繁忙，请稍后再试。若问题仍然存在，请与系统管理员联系。";
	// private static WebApplicationContext wac;
	// private final static int pageWidth = 595;
	// private final static int pageHeight = 842;
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
	// private static final String fontName = "宋体";
	// private static final String pdfFontName = "STSong-Light";
	// private static final String pdfEncoding = "UniGB-UCS2-H";
	private final static int bbbh_kdks = 1;
	private final static int bbbh_kdys = 2;
	private final static int bbbh_zxks = 3;
	private final static int bbbh_zxys = 4;

	// public void init(ServletConfig sc) throws ServletException {
	// WebApplicationContext w = AppContextHolder.get();
	// if (w != null) {
	// wac = w;
	// } else {
	// wac = WebApplicationContextUtils
	// .getRequiredWebApplicationContext(sc.getServletContext());
	// }
	// logger.info("PrintServlet inited...");
	// }

	// public void doGet(Map<String, Object> request, Map<String, Object>
	// response)
	// throws ServletException, IOException {
	// response.setContentType(defaultContent);
	// String uid = (String) request.getSession().getAttribute("uid");
	// ServletOutputStream sos = response.getOutputStream();
	// if (StringUtils.isEmpty(uid)) {
	// logger.error("not logon.");
	// writeMsg(sos, "链接已失效，请登录.");
	// return;
	// }
	// try {
	// doPrint(request, response);
	// } catch (Exception e) {
	// logger.error(e.getMessage(), e);
	// writeMsg(sos, defaultErrorMsg);
	// }
	// }

	// private void writeMsg(OutputStream outputstream, String msg)
	// throws UnsupportedEncodingException, IOException {
	// outputstream.write(msg.getBytes("UTF-8"));
	// }

	/**
	 * 得到报表列集合
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumn(int bbbh, String uid,
			Session ss, List<Map<String, Object>> list_xmgb) {
		StringBuffer sql_column = new StringBuffer();
		sql_column
				.append("select GY_XMGB.GBXM,GY_XMGB.SXH from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid)
				.append(" group by GY_XMGB.GBXM,GY_XMGB.SXH order by SXH");
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(sql_column.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_column.size() == 0 || list_column == null) {
			StringBuffer sql_column_default = new StringBuffer();
			sql_column_default
					.append("SELECT SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM");
			List<Map<String, Object>> list_column_default = ss
					.createSQLQuery(sql_column_default.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_column_default;
		}
		// 把收费项目和归并名称的关系存到一个map中
		Map<BigDecimal, String> map_xmgb_relation = new HashMap<BigDecimal, String>();
		for (Map<String, Object> m_xmgb : list_xmgb) {
			map_xmgb_relation.put((BigDecimal) m_xmgb.get("SFXM"),
					(String) m_xmgb.get("XMMC"));
		}
		// 找到对应的在报表显示的项目名称
		for (Map<String, Object> m_column : list_column) {
			String xmmc = map_xmgb_relation.get(m_column.get("GBXM"));
			m_column.put("XMMC", xmmc);
		}
		return list_column;
	}

	/**
	 * 得到项目归并规则
	 * 
	 * @author gaof
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListXmgb(int bbbh, String uid,
			Session ss) {
		StringBuffer sql_xmgb = new StringBuffer();
		sql_xmgb.append(
				"select GY_XMGB.Sfxm,GY_XMGB.GBXM,GY_XMGB.Xmmc,GY_XMGB.Sxh from GY_XMGB"
						+ " where GY_XMGB.Bbbh = ").append(bbbh)
				.append(" and GY_XMGB.JGID = ").append(uid);
		List<Map<String, Object>> list_xmgb = ss
				.createSQLQuery(sql_xmgb.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// 如果没有对应的，则使用默认规则
		if (list_xmgb.size() == 0 || list_xmgb == null) {
			StringBuffer sql_xmgb_default = new StringBuffer();
			sql_xmgb_default
					.append("SELECT SFXM,SFXM AS GBXM, SFMC AS XMMC FROM GY_SFXM");
			List<Map<String, Object>> list_xmgb_default = ss
					.createSQLQuery(sql_xmgb_default.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			return list_xmgb_default;
		}
		return list_xmgb;
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
			int ib_ks = 0;
			if (requestData.get("ib_ks") != null) {
				ib_ks = Integer.parseInt(requestData.get("ib_ks") + "");
			}
			int ib_ys = 0;
			if (requestData.get("ib_ys") != null) {
				ib_ys = Integer.parseInt(requestData.get("ib_ys") + "");
			}
			int ib_kd = 0;
			if (requestData.get("ib_kd") != null) {
				ib_kd = Integer.parseInt(requestData.get("ib_kd") + "");
			}
			int ib_zx = 0;
			if (requestData.get("ib_zx") != null) {
				ib_zx = Integer.parseInt(requestData.get("ib_zx") + "");
			}
			int ib_sfy = 0;
			if (requestData.get("ib_sfy") != null) {
				ib_sfy = Integer.parseInt(requestData.get("ib_sfy") + "");
			}
			if (ib_ks == 1) {
				if (ib_kd == 1) { // 开单科室
					// 主界面数据源表单是开单科室表单
					return doPrintKdks(request, response, config);
				} else if (ib_zx == 1) {// 执行科室
					// 主界面数据源表单是执行科室表单
					return doPrintZxks(request, response, config);
				}
			} else if (ib_ys == 1) {
				if (ib_kd == 1) { // 开单医生
					// 主界面数据源表单是开单医生表单
					return doPrintKdys(request, response, config);
				} else if (ib_zx == 1) {// 执行医生
					// 主界面数据源表单是执行医生表单
					return doPrintZxys(request, response, config);
				}
			}else if (ib_sfy==1){//收费员
				return doPrintSfy(request, response, config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打印开单科室
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintKdks(Map<String, Object> request,
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="门诊医生核算表";// (String) config.get("title");
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		List<Object> ksdm = (List<Object>) requestData.get("ksdm");

		// int REPORT_COUNTDATE_MZ =
		// Integer.parseInt(ParameterUtil.getParameter(
		// uid, BSPHISSystemArgument.REPORT_COUNTDATE_MZ, ctx));
		int REPORT_COUNTDATE_MZ = 1;
		if (requestData.get("tjfs") != null) {
			REPORT_COUNTDATE_MZ = Integer
					.parseInt(requestData.get("tjfs") + "");
		}
		// Session ss = (Session) ctx.get(Context.DB_SESSION);
		StringBuffer hql = new StringBuffer();
		hql.append(
				"SELECT MS_MZHS.KSDM,SYS_Office.OFFICENAME as KSMC,MS_MZMX.SFXM,"
						+ " sum(MS_MZMX.SFJE) as SFJE,SUM(MS_MZMX.ZFJE) AS ZFJE"
						+ " FROM MS_MZMX,MS_MZHS,SYS_Office WHERE MS_MZMX.HZXH = MS_MZHS.HZXH "
						+ " and MS_MZHS.KSDM = SYS_Office.ID ")
				.append("AND  MS_MZHS.JGID = ")
				.append(uid)
				.append(" and MS_MZHS.GZRQ >=  to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_MZHS.GZRQ <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_MZHS.TJFS = ")
				.append(REPORT_COUNTDATE_MZ)
				.append(" and MS_MZHS.KSDM in (")
				.append(ksdm.toString().substring(1,
						ksdm.toString().length() - 1))
				.append(")")
				.append(" GROUP BY MS_MZHS.KSDM,SYS_Office.OFFICENAME,MS_MZMX.SFXM");

		List<Map<String, Object>> list_data = ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_kdks, uid, ss);
		List<Map<String, Object>> list_columnALL = getListColumn(bbbh_kdks,
				uid, ss, list_xmgb);
		List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list_columnALL.size(); i++) {
			Map<String, Object> m = list_columnALL.get(i);
			for (int j = 0; j < list_data.size(); j++) {
				Map<String, Object> m1 = list_data.get(j);
				if (m1.get("SFXM").toString().equals(m.get("GBXM").toString())
						&& !list_column.contains(m)) {
					list_column.add(m);
				}
			}
		}

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		map.put("TOTAL", cm0);
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("KSMC");
		cm2.setText("科室名称");
		map.put("KSMC", cm2);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			map.put(column.get("GBXM").toString(), cm);
		}
		ColumnModel cm3 = new ColumnModel();
		cm3.setName("KSHJ");
		cm3.setText("合计");
		map.put("KSHJ", cm3);
		DecimalFormat df = new DecimalFormat("#0.00");
		int i = 1;
		for (Map<String, Object> m : list_data) {
			boolean isExist = false;
			int existIndex = 0;
			// 遍历list_xmgb集合找到对应的归并项目
			for (Map<String, Object> m_xmgb : list_xmgb) {
				if (m.get("SFXM").equals(m_xmgb.get("SFXM")) && Double.parseDouble(m.get("SFJE")+"")>0) {
					// 如果l_data中没有元素
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put(m_xmgb.get("GBXM").toString(),
									df.format(m.get("SFJE")));
						}
						l_data.add(data);
						i++;
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果此病人姓名已存在于l_data，则全部累加
						for (int j = 0; j < l_data.size(); j++) {
							Map<String, Object> d = l_data.get(j);
							// 如果有该病人姓名
							if (d.get("KSMC").equals(m.get("KSMC"))) {
								isExist = true;
								existIndex = j;
								break;
							}
						}
					}
					// 如果有该病人姓名
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						// 如果该病人姓名已经有这个归并项目
						if (existData
								.containsKey(m_xmgb.get("GBXM").toString())) {
							BigDecimal oldValue = new BigDecimal(
									existData
											.get(m_xmgb.get("GBXM").toString())
											+ "");
							BigDecimal newValue = new BigDecimal(m.get("SFJE")
									+ "");
							existData.put(
									m_xmgb.get("GBXM").toString(),
										df.format(oldValue.add(newValue)));
						} else {// 如果没有这个归并项目
							existData.put(m_xmgb.get("GBXM").toString(),
									df.format(m.get("SFJE")));
						}
					} else {// 如果没有该病人姓名
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put(m_xmgb.get("GBXM").toString(),
									df.format(m.get("SFJE")));
						}
						l_data.add(data);
						i++;
					}
				}
			}
		}
		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal(d.get(column.get("GBXM")
							.toString()) + ""));
				}
			}
			dataTotal.put(column.get("GBXM").toString(),
					df.format(value));
		}
		l_data.add(dataTotal);
		/****************add by LIZHI 2017-10-11增加按科室费用合计*******************/
		if(l_data.size()>0){
			for (Map<String, Object> rowData : l_data) {
				BigDecimal value = new BigDecimal(0);
				for (Map<String, Object> column : list_column) {
					if(rowData.containsKey(column.get("GBXM").toString())){
						value = value.add(new BigDecimal(rowData.get(column.get("GBXM")
								.toString()) + ""));
					}else{
						rowData.put(column.get("GBXM").toString(), "0.00");
					}
				}
				rowData.put("KSHJ", df.format(value));
			}
		}
		/****************add by LIZHI 2017-10-11增加按科室费用合计*******************/

		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);
		List<JasperPrint> prints = new ArrayList<JasperPrint>();

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,
					new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,
									l_data)));
		}
		// PrintUtil.exportToMap<String, Object>(1, prints, request, response,
		// title);
		ss.close();
		return prints;
	}

	/**
	 * 打印执行科室
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintZxks(Map<String, Object> request,
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="门诊医生核算表";// (String) config.get("title");
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		List<Object> ksdm = (List<Object>) requestData.get("ksdm");

		// int REPORT_COUNTDATE_MZ =
		// Integer.parseInt(ParameterUtil.getParameter(
		// uid, BSPHISSystemArgument.REPORT_COUNTDATE_MZ, ctx));
		int REPORT_COUNTDATE_MZ = 1;
		if (requestData.get("tjfs") != null) {
			REPORT_COUNTDATE_MZ = Integer
					.parseInt(requestData.get("tjfs") + "");
		}
		// Session ss = (Session) ctx.get(Context.DB_SESSION);
		StringBuffer hql = new StringBuffer();

		hql.append(
				"SELECT MS_YJHS.KSDM,SYS_Office.OFFICENAME as KSMC,MS_YJMX.SFXM,"
						+ " sum(MS_YJMX.SFJE) as SFJE,SUM(MS_YJMX.ZFJE) AS ZFJE "
						+ " FROM MS_YJMX,MS_YJHS,SYS_Office WHERE MS_YJHS.HZXH = MS_YJMX.HZXH"
						+ " and MS_YJHS.KSDM = SYS_Office.ID  ")
				.append(" AND MS_YJHS.JGID = ")
				.append(uid)
				.append(" and MS_YJHS.GZRQ >=  to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_YJHS.GZRQ <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_YJHS.TJFS = ")
				.append(REPORT_COUNTDATE_MZ)
				.append(" AND MS_YJHS.KSDM IN (")
				.append(ksdm.toString().substring(1,
						ksdm.toString().length() - 1))
				.append(")")
				.append(" GROUP BY MS_YJHS.KSDM,SYS_Office.OFFICENAME,MS_YJMX.SFXM");

		List<Map<String, Object>> list_data = ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_zxks, uid, ss);
		List<Map<String, Object>> list_columnALL = getListColumn(bbbh_zxks,
				uid, ss, list_xmgb);
		List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list_data.size(); i++) {
			Map<String, Object> m = list_data.get(i);
			for (int j = 0; j < list_columnALL.size(); j++) {
				Map<String, Object> m1 = list_columnALL.get(j);
				if (m1.get("GBXM").toString().equals(m.get("SFXM").toString())
						&& !list_column.contains(m1)) {
					list_column.add(m1);
				}
			}
		}

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		map.put("TOTAL", cm0);
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("KSMC");
		cm2.setText("科室名称");
		map.put("KSMC", cm2);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			map.put(column.get("GBXM").toString(), cm);
		}

		int i = 1;
		for (Map<String, Object> m : list_data) {
			boolean isExist = false;
			int existIndex = 0;
			// 遍历list_xmgb集合找到对应的归并项目
			for (Map<String, Object> m_xmgb : list_xmgb) {
				if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
					// 如果l_data中没有元素
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
						l_data.add(data);
						i++;
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果此病人姓名已存在于l_data，则全部累加
						for (int j = 0; j < l_data.size(); j++) {
							Map<String, Object> d = l_data.get(j);
							// 如果有该病人姓名
							if (d.get("KSMC").equals(m.get("KSMC"))) {
								isExist = true;
								existIndex = j;
								break;
							}
						}
					}
					// 如果有该病人姓名
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						// 如果该病人姓名已经有这个归并项目
						if (existData
								.containsKey(m_xmgb.get("GBXM").toString())) {
							BigDecimal oldValue = new BigDecimal(
									existData
											.get(m_xmgb.get("GBXM").toString())
											+ "");
							BigDecimal newValue = new BigDecimal(m.get("SFJE")
									+ "");
							existData.put(
									m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",
											oldValue.add(newValue)));
						} else {// 如果没有这个归并项目
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
					} else {// 如果没有该病人姓名
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
						l_data.add(data);
						i++;
					}
				}
			}
		}
		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal(d.get(column.get("GBXM")
							.toString()) + ""));
				}
			}
			dataTotal.put(column.get("GBXM").toString(),
					String.format("%1$.2f", value));
		}
		l_data.add(dataTotal);

		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);
		List<JasperPrint> prints = new ArrayList<JasperPrint>();

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,
					new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,
									l_data)));
		}
		// PrintUtil.exportToMap<String, Object>(1, prints, request, response,
		// title);
		ss.close();
		return prints;
	}

	/**
	 * 打印开单医生
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintKdys(Map<String, Object> request,
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();
		DecimalFormat df = new DecimalFormat("#0.00");
		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="门诊医生核算表（不含诊查费）";
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
				
		//处方统计
		String cfsql="select ysdm as YSDM,sum(XYCFS) as XYCFS,sum(ZYCFS) as ZYCFS ,sum(CYCFS) as CYCFS from ("+
				" select a.ysdm,case a.cflx when 1 then 1 else 0 end  as XYCFS,case a.cflx when 2 then 1 else 0 end as ZYCFS,"+
				" case a.cflx when 3 then 1 else 0 end  as CYCFS"+
				" from ms_cf01 a join ms_mzxx b on a.mzxh=b.mzxh where a.jgid='"+uid+"'" +
				" and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" union all "+
				" select a.ysdm,case a.cflx when 1 then -1 else 0 end  as XYCFS,case a.cflx when 2 then -1 else 0 end  as ZYCFS,"+
				" case a.cflx when 3 then -1 else 0 end  as CYCFS"+
				" from ms_cf01 a join ms_zffp c on a.mzxh=c.mzxh where a.jgid='"+uid+"'" +
				" and c.zfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and c.zfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" ) group by ysdm";
		List<Map<String, Object>> cflist = ss.createSQLQuery(cfsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		HashMap<String, HashMap<String, Object>> data=new HashMap<String, HashMap<String, Object>>();
		HashMap<String, Object> tot=new HashMap<String, Object>();
		tot.put("YSXM","合计:");
		tot.put("FYHJ",0.00);
		if(cflist.size()==0){
			tot.put("XYCFS", 0);
			tot.put("ZYCFS", 0);
			tot.put("CYCFS", 0);
		}else{
			for(Map<String, Object> one :cflist){
				tot.put("XYCFS", tot.get("XYCFS")==null?Long.parseLong(one.get("XYCFS")+""):
					Long.parseLong(tot.get("XYCFS")+"")+Long.parseLong(one.get("XYCFS")+""));
				tot.put("ZYCFS", tot.get("ZYCFS")==null?Long.parseLong(one.get("ZYCFS")+""):
					Long.parseLong(tot.get("ZYCFS")+"")+Long.parseLong(one.get("ZYCFS")+""));
				tot.put("CYCFS", tot.get("CYCFS")==null?Long.parseLong(one.get("CYCFS")+""):
					Long.parseLong(tot.get("CYCFS")+"")+Long.parseLong(one.get("CYCFS")+""));
				if(data.get(one.get("YSDM")+"")==null){
					HashMap<String, Object> temp=new HashMap<String, Object>();
					temp.put("XYCFS", one.get("XYCFS"));
					temp.put("ZYCFS", one.get("ZYCFS"));
					temp.put("CYCFS", one.get("CYCFS"));
					try {
						temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					data.put(one.get("YSDM")+"", temp);
				}else{
					data.get(one.get("YSDM")+"").put("XYCFS", one.get("XYCFS"));
					data.get(one.get("YSDM")+"").put("ZYCFS", one.get("ZYCFS"));
					data.get(one.get("YSDM")+"").put("CYCFS", one.get("CYCFS"));
				}
			}
		}
		
		 //减免统计
//		String jmje="select sum(jylyjmje) as JYLYJMJE,sum(ycfjmje) as YCFJMJE from ("+
//		" select * from ms_mzxx where jgid='"+uid+"'" +
//		" and sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
//		" and  sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
//		" and zfpb='0')";
//		List<Map<String, Object>> jm = ss.createSQLQuery(jmje.toString())
//				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//		System.out.println("++++++"+jm);
//		String jyjm=jm.get(0).get("JYLYJMJE")==null?"0.0":jm.get(0).get("JYLYJMJE").toString();
//		String ycfjm=jm.get(0).get("YCFJMJE")==null?"0.0":jm.get(0).get("YCFJMJE").toString();	
//String title ="门诊医生核算表 (家医履约减免总金额："+jyjm+" 孕产妇减免总金额："+ycfjm+")" ;				
		
		
		//检查单统计
		String jcsql="select ysdm as YSDM,sum(jcdsl) as JCDSL from ("+
				" select a.ysdm,1 as jcdsl"+
				" from ms_yj01 a join ms_mzxx b on a.mzxh=b.mzxh where a.jgid='"+uid+"'" +
				" and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" union all "+
				" select a.ysdm,-1 as jcdsl"+
				" from ms_yj01 a join ms_zffp c on a.mzxh=c.mzxh where a.jgid='"+uid+"'" +
				" and c.zfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
				" and c.zfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" ) group by ysdm";
		List<Map<String, Object>> jclist = ss.createSQLQuery(jcsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if(jclist.size()==0){
			tot.put("JCDSL", 0);
		}else{
			for(Map<String, Object> one :jclist){
				tot.put("JCDSL", tot.get("JCDSL")==null?Long.parseLong(one.get("JCDSL")+""):
					Long.parseLong(tot.get("JCDSL")+"")+Long.parseLong(one.get("JCDSL")+""));
				if(data.get(one.get("YSDM")+"")==null){
					HashMap<String, Object> temp=new HashMap<String, Object>();
					temp.put("JCDSL", one.get("JCDSL"));
					try {
						temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					data.put(one.get("YSDM")+"", temp);
				}else{
					data.get(one.get("YSDM")+"").put("JCDSL", one.get("JCDSL"));
				}
			}
		}
		//就诊统计
		String jzsql="select a.ysdm as YSDM ,count(1) as JZRC from ys_mz_jzls a "+
				" where a.jgid='"+uid+"' and a.kssj >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and  a.kssj <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" group by a.ysdm ";
		List<Map<String, Object>> jzlist = ss.createSQLQuery(jzsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if(jzlist.size()==0){
			tot.put("JZRC", 0);
		}else{
			for(Map<String, Object> one :jzlist){
				tot.put("JZRC", tot.get("JZRC")==null?Long.parseLong(one.get("JZRC")+""):
					Long.parseLong(tot.get("JZRC")+"")+Long.parseLong(one.get("JZRC")+""));
				if(data.get(one.get("YSDM")+"")==null){
					HashMap<String, Object> temp=new HashMap<String, Object>();
					temp.put("JZRC", one.get("JZRC"));
					try {
						temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					data.put(one.get("YSDM")+"", temp);
				}else{
					data.get(one.get("YSDM")+"").put("JZRC", one.get("JZRC"));
				}
			}
		}
		//处方金额统计
		String cfjesql="select ysdm as YSDM ,fygb as FYGB,sum(hjje) as HJJE from ("+
				" select a.ysdm as ysdm,d.fygb as fygb,d.hjje as hjje"+
				" from ms_cf01 a join ms_mzxx b on a.mzxh=b.mzxh join ms_cf02 d on a.cfsb=d.cfsb " +
				" where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
				" union all "+
				" select a.ysdm as ysdm,d.fygb as fygb,-d.hjje as hjje"+
				" from ms_cf01 a join ms_zffp c on a.mzxh=c.mzxh join ms_cf02 d on a.cfsb=d.cfsb"+
				" where a.jgid='"+uid+"' and c.zfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
				" and c.zfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
				" ) group by ysdm,fygb";
		List<Map<String, Object>> cfjelist = ss.createSQLQuery(cfjesql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(Map<String, Object> one :cfjelist){
			tot.put(one.get("FYGB")+"", df.format(tot.get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
				Double.parseDouble(tot.get(one.get("FYGB")+"")+"")+Double.parseDouble(one.get("HJJE")+"")));
			tot.put("FYHJ",df.format(Double.parseDouble(one.get("HJJE")+"")+Double.parseDouble(tot.get("FYHJ")+"")));
			
			if(data.get(one.get("YSDM")+"")==null){
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put(one.get("FYGB")+"", one.get("HJJE"));
				temp.put("FYHJ", df.format(one.get("HJJE")));
				try {
					temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
				} catch (ControllerException e) {
					e.printStackTrace();
				}
				data.put(one.get("YSDM")+"", temp);
			}else{
				data.get(one.get("YSDM")+"").put(one.get("FYGB")+"",
						data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
							Double.parseDouble(one.get("HJJE")+"")+
							Double.parseDouble(data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")+""));
				data.get(one.get("YSDM")+"").put("FYHJ", df.format(data.get(one.get("YSDM")+"").get("FYHJ")==null?
						Double.parseDouble(one.get("HJJE")+""):Double.parseDouble(data.get(one.get("YSDM")+"").get("FYHJ")+"")+
						Double.parseDouble(one.get("HJJE")+"")));
			}
		}
		//检查金额去掉特殊
		String jcjesql="select ysdm as YSDM ,fygb as FYGB,sum(hjje) as HJJE from ("+
				" select a.ysdm as ysdm,d.fygb as fygb,d.hjje as hjje"+
				" from ms_yj01 a join ms_mzxx b on a.mzxh=b.mzxh"+
				" join ms_yj02 d on a.yjxh=d.yjxh"+
				//" join gy_ylsf e on d.ylxh=e.fyxh"+
				" where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+ 
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+ 
				//" and e.fymc not like '%一般诊疗费%' and e.zycjgb is  null"+
				" union all"+
				" select a.ysdm as ysdm,d.fygb as fygb,-d.hjje as hjje"+
				" from ms_yj01 a join ms_zffp c on a.mzxh=c.mzxh"+
				" join ms_yj02 d on a.yjxh=d.yjxh"+
				//" join gy_ylsf e on d.ylxh=e.fyxh"+
				" where a.jgid='"+uid+"' and c.zfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')"+
				" and c.zfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
				//" and e.fymc not like '%一般诊疗费%' and e.zycjgb is  null"+
				" ) group by ysdm,fygb";
		List<Map<String, Object>> jcjelist = ss.createSQLQuery(jcjesql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();		
		for(Map<String, Object> one :jcjelist){
			tot.put(one.get("FYGB")+"", df.format(tot.get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
				Double.parseDouble(tot.get(one.get("FYGB")+"")+"")+Double.parseDouble(one.get("HJJE")+"")));
			tot.put("FYHJ",df.format(Double.parseDouble(one.get("HJJE")+"")+Double.parseDouble(tot.get("FYHJ")+"")));
			if(data.get(one.get("YSDM")+"")==null){
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put(one.get("FYGB")+"", one.get("HJJE"));
				temp.put("FYHJ", df.format(one.get("HJJE")));
				try {
					temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
				} catch (ControllerException e) {
					e.printStackTrace();
				}
				data.put(one.get("YSDM")+"", temp);
			}else{
				data.get(one.get("YSDM")+"").put(one.get("FYGB")+"",
						data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")==null?Double.parseDouble(one.get("HJJE")+""):
							Double.parseDouble(one.get("HJJE")+"")+
							Double.parseDouble(data.get(one.get("YSDM")+"").get(one.get("FYGB")+"")+""));
				data.get(one.get("YSDM")+"").put("FYHJ", df.format(data.get(one.get("YSDM")+"").get("FYHJ")==null?
						Double.parseDouble(one.get("HJJE")+""):Double.parseDouble(data.get(one.get("YSDM")+"").get("FYHJ")+"")+
						Double.parseDouble(one.get("HJJE")+"")));
			}
		}
		//检查特殊金额
		String jctsjesql="select ysdm as YSDM,sum(ybzlf) as YBZLF,sum(zysyjsje) as ZYSYJSJE,sum(zyzlf) as ZYZLF from ("+
				" select a.ysdm as ysdm,case when e.fymc like '%一般诊疗费%' then d.hjje else 0 end as ybzlf,"+
				" case when e.zycjgb is not null  then d.hjje else 0 end as zysyjsje,"+
				" case when e.zyzlf =1  then d.hjje else 0 end as zyzlf"+
				" from ms_yj01 a join ms_mzxx b on a.mzxh=b.mzxh"+
				" join ms_yj02 d on a.yjxh=d.yjxh"+
				" join gy_ylsf e on d.ylxh=e.fyxh"+
				" where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" and(e.fymc like '%一般诊疗费%' or e.zycjgb is not null or e.zyzlf =1)"+
				" union all "+
				" select a.ysdm as ysdm,case when e.fymc like '%一般诊疗费%' then -d.hjje else 0 end as ybzlf,"+
				" case when e.zycjgb is not null  then -d.hjje else 0 end as zysyjsje,"+
				" case when e.zyzlf =1  then -d.hjje else 0 end as zyzlf"+
				" from ms_yj01 a join ms_zffp c on a.mzxh=c.mzxh"+
				" join ms_yj02 d on a.yjxh=d.yjxh"+
				" join gy_ylsf e on d.ylxh=e.fyxh"+
				" where a.jgid='"+uid+"' and c.zfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
				" and c.zfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')"+
				" and (e.fymc like '%一般诊疗费%' or e.zycjgb is not null or e.zyzlf =1)"+
				" ) group by ysdm";
		List<Map<String, Object>> jctsjelist = ss.createSQLQuery(jctsjesql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if(jctsjelist.size()==0){
			tot.put("YBZLF", 0);
			tot.put("ZYSYJSJE", 0);
			tot.put("ZYZLF", 0);
			tot.put("FYHJ", 0);
		}else{
			for(Map<String, Object> one :jctsjelist){
				tot.put("YBZLF", df.format(tot.get("YBZLF")==null?Double.parseDouble(one.get("YBZLF")+""):
					Double.parseDouble(tot.get("YBZLF")+"")+Double.parseDouble(one.get("YBZLF")+"")));
				tot.put("ZYSYJSJE", df.format(tot.get("ZYSYJSJE")==null?Double.parseDouble(one.get("ZYSYJSJE")+""):
					Double.parseDouble(tot.get("ZYSYJSJE")+"")+Double.parseDouble(one.get("ZYSYJSJE")+"")));
				tot.put("ZYZLF", df.format(tot.get("ZYZLF")==null?Double.parseDouble(one.get("ZYZLF")+""):
					Double.parseDouble(tot.get("ZYZLF")+"")+Double.parseDouble(one.get("ZYZLF")+"")));
				tot.put("FYHJ",df.format(Double.parseDouble(tot.get("FYHJ")+"")));	
				if(data.get(one.get("YSDM")+"")==null){
					HashMap<String, Object> temp=new HashMap<String, Object>();
					temp.put("YBZLF", one.get("YBZLF"));
					temp.put("ZYSYJSJE", one.get("ZYSYJSJE"));
					temp.put("ZYZLF", one.get("ZYZLF"));
					temp.put("FYHJ", df.format(Double.parseDouble(one.get("YBZLF")+"")+Double.parseDouble(one.get("ZYSYJSJE")+"")+Double.parseDouble(one.get("ZYZLF")+"")));				
					try {
						temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
					} catch (ControllerException e) {
						e.printStackTrace();
					}
					data.put(one.get("YSDM")+"", temp);
				}else{
					data.get(one.get("YSDM")+"").put("YBZLF",
							data.get(one.get("YSDM")+"").get("YBZLF")==null?Double.parseDouble(one.get("YBZLF")+""):
								Double.parseDouble(one.get("YBZLF")+"")+
								Double.parseDouble(data.get(one.get("YSDM")+"").get("YBZLF")+""));
					data.get(one.get("YSDM")+"").put("ZYSYJSJE",
							data.get(one.get("YSDM")+"").get("ZYSYJSJE")==null?Double.parseDouble(one.get("ZYSYJSJE")+""):
								Double.parseDouble(one.get("ZYSYJSJE")+"")+
								Double.parseDouble(data.get(one.get("YSDM")+"").get("ZYSYJSJE")+""));
					data.get(one.get("YSDM")+"").put("ZYZLF",
							data.get(one.get("YSDM")+"").get("ZYZLF")==null?Double.parseDouble(one.get("ZYZLF")+""):
								Double.parseDouble(one.get("ZYZLF")+"")+
								Double.parseDouble(data.get(one.get("YSDM")+"").get("ZYZLF")+""));
					data.get(one.get("YSDM")+"").put("FYHJ", df.format(data.get(one.get("YSDM")+"").get("FYHJ")==null?0.0:Double.parseDouble(data.get(one.get("YSDM")+"").get("FYHJ")+"")));				
					
				}
			}
		}
		
		//家医孕产妇减免sql
				String jyycfjmsql="select * from (select ysdm,-sum(jylyjmje) as JYLYJMJE,-sum(ycfjmje) as YCFJMJE from("+
								  " select distinct b.fphm, a.ysdm,b.jylyjmje,b.ycfjmje from"+
						          " ms_yj01 a join ms_mzxx b on a.mzxh=b.mzxh join ms_yj02 d on a.yjxh=d.yjxh"+
                                  " where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss')" +
                                  " and b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') and b.zfpb='0'" +
                                  " ) group by ysdm) where JYLYJMJE!='0' or YCFJMJE!='0'";
				List<Map<String, Object>> jyycfjmlist = ss.createSQLQuery(jyycfjmsql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				if(jctsjelist.size()==0){
					tot.put("JYLYJMJE", 0);
					tot.put("YCFJMJE", 0);				
					tot.put("FYHJ", 0);
				}else{
					for(Map<String, Object> one :jyycfjmlist){
						tot.put("JYLYJMJE", df.format(tot.get("JYLYJMJE")==null?Double.parseDouble(one.get("JYLYJMJE")+""):
							Double.parseDouble(tot.get("JYLYJMJE")+"")+Double.parseDouble(one.get("JYLYJMJE")+"")));
						tot.put("YCFJMJE", df.format(tot.get("YCFJMJE")==null?Double.parseDouble(one.get("YCFJMJE")+""):
							Double.parseDouble(tot.get("YCFJMJE")+"")+Double.parseDouble(one.get("YCFJMJE")+"")));						
						tot.put("FYHJ",df.format(Double.parseDouble(one.get("JYLYJMJE")+"")+Double.parseDouble(tot.get("FYHJ")+"")
										+Double.parseDouble(one.get("YCFJMJE")+"")));						
						if(data.get(one.get("YSDM")+"")==null){
							HashMap<String, Object> temp=new HashMap<String, Object>();
							temp.put("JYLYJMJE", one.get("JYLYJMJE"));
							temp.put("YCFJMJE", one.get("YCFJMJE"));
							temp.put("FYHJ", df.format(Double.parseDouble(one.get("JYLYJMJE")+"")+Double.parseDouble(one.get("YCFJMJE")+"")));
							try {
								temp.put("YSXM", DictionaryController.instance().get("phis.dictionary.user").getText(one.get("YSDM")+""));
							} catch (ControllerException e) {
								e.printStackTrace();
							}
							data.put(one.get("YSDM")+"", temp);
						}else{
							data.get(one.get("YSDM")+"").put("JYLYJMJE",
									data.get(one.get("YSDM")+"").get("JYLYJMJE")==null?Double.parseDouble(one.get("JYLYJMJE")+""):
										Double.parseDouble(one.get("JYLYJMJE")+"")+
										Double.parseDouble(data.get(one.get("YSDM")+"").get("JYLYJMJE")+""));
							data.get(one.get("YSDM")+"").put("YCFJMJE",
									data.get(one.get("YSDM")+"").get("YCFJMJE")==null?Double.parseDouble(one.get("YCFJMJE")+""):
										Double.parseDouble(one.get("YCFJMJE")+"")+
										Double.parseDouble(data.get(one.get("YSDM")+"").get("YCFJMJE")+""));									
							data.get(one.get("YSDM")+"").put("FYHJ", df.format(data.get(one.get("YSDM")+"").get("FYHJ")==null?0.0
									:Double.parseDouble(data.get(one.get("YSDM")+"").get("FYHJ")+"")
									+Double.parseDouble(one.get("JYLYJMJE")+"")+Double.parseDouble(one.get("YCFJMJE")+"")));
									
						}
					}
				}
		
		String xmsql="select s.sfxm as SFXM,s.sfmc as SFMC  from gy_sfxm s ,("+
				" select distinct d.fygb from ms_yj01 a join ms_mzxx b on a.mzxh=b.mzxh "+
				" join ms_yj02 d on a.yjxh=d.yjxh"+
				" where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" union "+
				" select distinct d.fygb from ms_cf01 a  join ms_mzxx b on a.mzxh=b.mzxh "+
				" join ms_cf02 d on a.cfsb=d.cfsb"+
				" where a.jgid='"+uid+"' and b.sfrq >=to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') "+
				" and  b.sfrq <=to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss') "+
				" ) t where s.sfxm=t.fygb order by s.sfxm";
		List<Map<String, Object>> list_column = ss.createSQLQuery(xmsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("YSXM");
		cm0.setText("医生姓名");
		map.put("YSXM", cm0);
		cm0 = new ColumnModel();
		cm0.setName("JZRC");
		cm0.setText("就诊人次");
		map.put("JZRC", cm0);
		ColumnModel cm4 = new ColumnModel();
		cm4.setName("JCDSL");
		cm4.setText("检查单");
		map.put("JCDSL", cm4);
		ColumnModel cm5 = new ColumnModel();
		cm5.setName("XYCFS");
		cm5.setText("西药方");
		map.put("XYCFS", cm5);
		ColumnModel cm6 = new ColumnModel();
		cm6.setName("ZYCFS");
		cm6.setText("中药方");
		map.put("ZYCFS", cm6);
		ColumnModel cm7 = new ColumnModel();
		cm7.setName("CYCFS");
		cm7.setText("草药方");
		map.put("CYCFS", cm7);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("SFXM").toString());
			cm.setText((String) column.get("SFMC"));
			map.put(column.get("SFXM").toString(), cm);
		}
		ColumnModel cm = new ColumnModel();
		cm.setName("YBZLF");
		cm.setText("一般诊疗费");
		map.put("YBZLF", cm);
		cm = new ColumnModel();
		cm.setName("ZYSYJSJE");
		cm.setText("中医适宜技术");
		map.put("ZYSYJSJE", cm);
		cm = new ColumnModel();
		cm.setName("ZYZLF");
		cm.setText("其中中医治疗费");
		map.put("ZYZLF", cm);
		cm = new ColumnModel();
		cm.setName("JYLYJMJE");
		cm.setText("家医履约减免费");
		map.put("JYLYJMJE", cm);		
		cm = new ColumnModel();
		cm.setName("YCFJMJE");
		cm.setText("孕产妇减免费");
		map.put("YCFJMJE", cm);		
		cm = new ColumnModel();
		cm.setName("FYHJ");
		cm.setText("费用合计");
		map.put("FYHJ", cm);

		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
		Iterator iter = data.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			HashMap<String, Object> dataMap = (HashMap<String, Object>)entry.getValue();
			for (Map.Entry<String, ColumnModel> mapEntry : map.entrySet()) { 
				if(!dataMap.containsKey(mapEntry.getValue().getName())){
					dataMap.put(mapEntry.getValue().getName(), "0");
				}
			}
			l_data.add(dataMap);
		}
		l_data.add(tot);
		
		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);
		List<JasperPrint> prints = new ArrayList<JasperPrint>();

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,
					new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,
									l_data)));
		}
		ss.close();
		return prints;
	}

	/**
	 * 打印执行医生
	 * 
	 * @author gaof
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws DataAccessException
	 * @throws JRException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintZxys(Map<String, Object> request,
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="门诊医生核算表";// (String) config.get("title");
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		List<Object> ksdm = (List<Object>) requestData.get("ksdm");

		// int REPORT_COUNTDATE_MZ =
		// Integer.parseInt(ParameterUtil.getParameter(
		// uid, BSPHISSystemArgument.REPORT_COUNTDATE_MZ, ctx));
		int REPORT_COUNTDATE_MZ = 1;
		if (requestData.get("tjfs") != null) {
			REPORT_COUNTDATE_MZ = Integer
					.parseInt(requestData.get("tjfs") + "");
		}
		// Session ss = (Session) ctx.get(Context.DB_SESSION);
		StringBuffer hql = new StringBuffer();

		hql.append(
				"SELECT MS_YJHS.YSDM,SYS_Personnel.OFFICECODE,MS_YJMX.SFXM,sum(MS_YJMX.SFJE) as SFJE, "
						+ " SUM(MS_YJMX.ZFJE) AS ZFJE,SYS_Office.OFFICENAME as KSMC,SYS_Personnel.PERSONNAME as YGXM FROM MS_YJMX,MS_YJHS,SYS_Personnel,"
						+ " SYS_Office WHERE MS_YJHS.HZXH = MS_YJMX.HZXH and	MS_YJHS.YSDM = SYS_Personnel.PERSONID "
						+ " AND SYS_Personnel.OFFICECODE = SYS_Office.ID ")
				.append(" AND MS_YJHS.JGID = ")
				.append(uid)
				.append(" and MS_YJHS.GZRQ >=  to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_YJHS.GZRQ <=  to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')")
				.append(" And MS_YJHS.TJFS = ")
				.append(REPORT_COUNTDATE_MZ)
				.append(" AND MS_YJHS.KSDM IN (")
				.append(ksdm.toString().substring(1,
						ksdm.toString().length() - 1))
				.append(")")
				.append("GROUP BY MS_YJHS.YSDM,SYS_Personnel.OFFICECODE,MS_YJMX.SFXM,SYS_Office.OFFICENAME,SYS_Personnel.PERSONNAME");

		List<Map<String, Object>> list_data = ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		List<Map<String, Object>> list_xmgb = getListXmgb(bbbh_zxys, uid, ss);
		List<Map<String, Object>> list_columnALL = getListColumn(bbbh_zxys,
				uid, ss, list_xmgb);
		List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list_data.size(); i++) {
			Map<String, Object> m = list_data.get(i);
			for (int j = 0; j < list_columnALL.size(); j++) {
				Map<String, Object> m1 = list_columnALL.get(j);
				if (m1.get("GBXM").toString().equals(m.get("SFXM").toString())
						&& !list_column.contains(m1)) {
					list_column.add(m1);
				}
			}
		}
		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();

		// 生成表结构
		ColumnModel cm0 = new ColumnModel();
		cm0.setName("TOTAL");
		cm0.setText("");
		map.put("TOTAL", cm0);
		ColumnModel cm2 = new ColumnModel();
		cm2.setName("KSMC");
		cm2.setText("科室名称");
		map.put("KSMC", cm2);
		ColumnModel cm3 = new ColumnModel();
		cm3.setName("YGXM");
		cm3.setText("医生姓名");
		map.put("YGXM", cm3);
		for (Map<String, Object> column : list_column) {
			ColumnModel cm = new ColumnModel();
			cm.setName(column.get("GBXM").toString());
			cm.setText((String) column.get("XMMC"));
			map.put(column.get("GBXM").toString(), cm);
		}

		int i = 1;
		for (Map<String, Object> m : list_data) {
			boolean isExist = false;
			int existIndex = 0;
			// 遍历list_xmgb集合找到对应的归并项目
			for (Map<String, Object> m_xmgb : list_xmgb) {
				if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
					// 如果l_data中没有元素
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put("YGXM", m.get("YGXM"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
						l_data.add(data);
						i++;
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果此病人姓名已存在于l_data，则全部累加
						for (int j = 0; j < l_data.size(); j++) {
							Map<String, Object> d = l_data.get(j);
							// 如果有该科室且有该医生
							if (d.get("KSMC").equals(m.get("KSMC"))
									&& d.get("YGXM").equals(m.get("YGXM"))) {
								isExist = true;
								existIndex = j;
								break;
							}
						}
					}
					// 如果有该科室且有该医生
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						// 如果该病人姓名已经有这个归并项目
						if (existData
								.containsKey(m_xmgb.get("GBXM").toString())) {
							BigDecimal oldValue = new BigDecimal(
									existData
											.get(m_xmgb.get("GBXM").toString())
											+ "");
							BigDecimal newValue = new BigDecimal(m.get("SFJE")
									+ "");
							existData.put(
									m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f",
											oldValue.add(newValue)));
						} else {// 如果没有这个归并项目
							existData.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
					} else {// 如果没有该病人姓名
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("TOTAL", i + "");
						if (m.get("SFXM").equals(m_xmgb.get("SFXM"))) {
							data.put("KSMC", m.get("KSMC"));
							data.put("YGXM", m.get("YGXM"));
							data.put(m_xmgb.get("GBXM").toString(),
									String.format("%1$.2f", m.get("SFJE")));
						}
						l_data.add(data);
						i++;
					}
				}
			}
		}
		// 计算统计
		HashMap<String, Object> dataTotal = new HashMap<String, Object>();
		dataTotal.put("TOTAL", "合计");
		for (Map<String, Object> column : list_column) {
			BigDecimal value = new BigDecimal(0);
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				if (d.get(column.get("GBXM").toString()) != null) {
					value = value.add(new BigDecimal(d.get(column.get("GBXM")
							.toString()) + ""));
				}
			}
			dataTotal.put(column.get("GBXM").toString(), value);
		}
		l_data.add(dataTotal);

		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);
		List<JasperPrint> prints = new ArrayList<JasperPrint>();

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,
					new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,
									l_data)));
		}
		// PrintUtil.exportToMap<String, Object>(1, prints, request, response,
		// title);
		ss.close();
		return prints;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-1-9
	 * @description 获取上下文ctx
	 * @updateInfo
	 * @param request
	 * @return
	 */
	// private Context getContext(Map<String, Object> request) {
	// Context ctx = Dispatcher.createContext(request);
	// ctx.put(Context.APP_CONTEXT, wac);
	// User user = Dispatcher.getUser(request);
	// if (user != null) {
	// Context userCtx = new UserContext(Dispatcher.getUser(request));
	// ctx.putCtx("user", userCtx);
	// }
	// SessionFactory sf = (SessionFactory) wac.getBean("mySessionFactory");
	// ctx.put(Context.DB_SESSION, sf.openSession());
	// return ctx;
	// }

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
	//按收费员统计
	protected List<JasperPrint> doPrintSfy(Map<String, Object> request,
			Map<String, Object> response, HashMap<String, Object> config)
			throws IOException, DataAccessException, JRException,
			IllegalAccessException, InstantiationException {
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
			ctx.put(Context.DB_SESSION, ss);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();

		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config
				.get("requestData");
		String title ="门诊收费核算表";// (String) config.get("title");
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		StringBuffer hql = new StringBuffer();
		hql.append("select rownum as COLID,a.sfxm as SFXM  from gy_sfxm a ");
		List<Map<String, Object>> sfxmlist=ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//		Map<String, Object> sfxmmap= new HashMap<String, Object>();
		Map<String, Object> sfxmmapcol= new HashMap<String, Object>();
		for(Map<String, Object> one : sfxmlist){
//			sfxmmap.put(one.get("SFXM")+"", one.get("COLID"));
			sfxmmapcol.put(one.get("COLID")+"",one.get("SFXM")+"");
		}
		List<JasperPrint> prints = new ArrayList<JasperPrint>();
		StringBuffer sql=new StringBuffer();
		sql.append("select czgh as CZGH,sfxm as SFXM,sum(zjje) as ZJJE from ("
				+" select a.czgh,b.sfxm,b.zjje as zjje from ms_mzxx a "
				+" join ms_sfmx b on a.mzxh=b.mzxh"
				+" join gy_sfxm c on b.sfxm=c.sfxm"
				+" where a.jgid like '"+uid+"%' and a.sfrq > to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss') and a.sfrq < to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')"
						
				+" and a.zfpb='0' and a.fphm is not null union all"
				+" select b.czgh,13 as SFXM, sum(b.xjje+b.zpje+b.zhje+b.qtys) as zjje from ms_ghmx b  "
				+" where b.jgid  like '"+uid+"%' and b.ghsj > to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss') and b.ghsj < to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')"
				+"and b.thbz = '0' group by b.czgh, 13 )"
				+" group by czgh,sfxm order by czgh ");
		List<Map<String, Object>> sfmxlist=ss.createSQLQuery(sql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		StringBuffer hjsql=new StringBuffer();
		hjsql.append("select czgh as CZGH,sum(ZHZF) as ZHZF,sum(zfje) as XJZF  from ("
				+" select a.czgh,(a.qtys+a.zhje) as ZHZF,a.zfje as zfje  from ms_mzxx a "
				+" where a.jgid like '"+uid+"%' and a.sfrq > to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss') and a.sfrq < to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')"
				+" union all"
				+" select a.czgh,-(a.qtys+a.zhje) as ZHZF,-a.zfje as zfje  from ms_mzxx a "
				+" join ms_zffp d on a.mzxh=d.mzxh"
				+" where a.jgid like '"+uid+"%' and d.zfrq > to_date('")
				.append(dateFrom)
				.append("','yyyy-mm-dd HH24:mi:ss') and d.zfrq < to_date('")
				.append(dateTo)
				.append("','yyyy-mm-dd HH24:mi:ss')"
				+")"
				+" group by czgh order by czgh ");
		List<Map<String, Object>> hjlist=ss.createSQLQuery(hjsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		String ybzlfsql="select czgh as CZGH,hjje as YBZLF from ("+
				" select a.czgh,sum(a.xjje+a.zpje+a.zhje+a.qtys) as hjje from ms_ghmx a"+
				" where  "+
				" a.jgid like '"+uid+"%' and to_char(a.ghsj, 'yyyy-mm-dd HH24:mi:ss') >= '"+dateFrom+"'"+
				" and to_char(a.ghsj, 'yyyy-mm-dd HH24:mi:ss') <= '"+dateTo+"'"+
				" and a.thbz = '0' "+
				" group by a.czgh )";
		List<Map<String, Object>> zyzlflist=ss.createSQLQuery(ybzlfsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
		ColumnModel newcm = new ColumnModel();
		newcm.setName("TOTAL");
		newcm.setText("");
		map.put("TOTAL", newcm);
		newcm = new ColumnModel();
		newcm.setName("SFY");
		newcm.setText("收费员");
		map.put("SFY", newcm);
		for(int i=1;i <=sfxmmapcol.size();i++){
			ColumnModel cm = new ColumnModel();
			cm.setName("xm"+sfxmmapcol.get(i+"").toString());
			try {
				cm.setText(DictionaryController.instance().get("phis.dictionary.feesDic").getText(sfxmmapcol.get(i+"").toString()));
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put("xm"+sfxmmapcol.get(i+"").toString(), cm);
		}
		newcm = new ColumnModel();
		newcm.setName("YBZLF");
		newcm.setText("一般诊疗费");
		map.put("YBZLF", newcm);
		newcm = new ColumnModel();
		newcm.setName("ALLFY");
		newcm.setText("总计");
		map.put("ALLFY", newcm);
		newcm = new ColumnModel();
		newcm.setName("GRZHZF");
		newcm.setText("个人账户支付");
		map.put("GRZHZF", newcm);
		newcm = new ColumnModel();
		newcm.setName("XJZF");
		newcm.setText("现金支付");
		map.put("XJZF", newcm);
		
		String czgh="";
		HashMap<String, Object> temp=new HashMap<String, Object>();
		int count=1;
		HashMap<String, Object> total=new HashMap<String, Object>();
		double allsffy=0.0;
		double grzhzf=0.0;
		double xjzf=0.0;
		double ybzlfhj=0.0;
		for(int i=0;i<sfmxlist.size();i++){
			Map<String, Object> one=sfmxlist.get(i);
			//诊疗费和一般诊疗费区分开
			if("13".equals(one.get("SFXM")+"")){
				for(Map<String, Object> ybzlf:zyzlflist){
					if((ybzlf.get("CZGH")+"").equals(one.get("CZGH")+"")){
						one.put("ZJJE", Double.parseDouble(one.get("ZJJE")+"")-Double.parseDouble(ybzlf.get("YBZLF")+""));
						ybzlfhj+=Double.parseDouble(ybzlf.get("YBZLF")+"");
						total.put("YBZLF", total.get("YBZLF")==null?ybzlf.get("YBZLF"):
							Double.parseDouble(total.get("YBZLF")+"")+Double.parseDouble(ybzlf.get("YBZLF")+""));
						break;
					}
				}
			}
			if(czgh.equals("")){
				czgh=one.get("CZGH")+"";
				temp.put("TOTAL", count);
				temp.put("CZGH", czgh);
				try {
					temp.put("SFY", DictionaryController.instance().get("phis.dictionary.user").getText(czgh));
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (!czgh.equals(one.get("CZGH")+"")){
				count++;
				double tempzhzf=0.0;
				double tempxjzf=0.0;
				for(Map<String, Object> hjone : hjlist){
					if((hjone.get("CZGH")+"").equals(czgh)){
						tempzhzf=Double.parseDouble(hjone.get("ZHZF")+"");
						tempxjzf=Double.parseDouble(hjone.get("XJZF")+"");
						grzhzf+=tempzhzf;
						xjzf+=tempxjzf;
						break;
					}
				}
				for(Map<String, Object> ybzlf:zyzlflist){
					if((ybzlf.get("CZGH")+"").equals(temp.get("CZGH")+"")){
						allsffy+=Double.parseDouble(ybzlf.get("YBZLF")+"");
						temp.put("YBZLF", Double.parseDouble(ybzlf.get("YBZLF")+""));
						break;
					}
				}
				temp.put("ALLFY",String.format("%.4f",allsffy));
				temp.put("GRZHZF",String.format("%.4f",tempzhzf));
				temp.put("XJZF",String.format("%.4f",tempxjzf));
				l_data.add(temp);
				allsffy=0.0;
				temp=new HashMap<String, Object>();
				czgh=one.get("CZGH")+"";
				temp.put("TOTAL", count);
				temp.put("CZGH", czgh);
				try {
					temp.put("SFY", DictionaryController.instance().get("phis.dictionary.user").getText(czgh));
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			temp.put("xm"+one.get("SFXM"),one.get("ZJJE"));
			allsffy=allsffy+Double.parseDouble(one.get("ZJJE")+"");
			double zjje=total.get("xm"+one.get("SFXM"))==null?
					0.0:Double.parseDouble(total.get("xm"+one.get("SFXM"))+"");
			total.put("xm"+one.get("SFXM"),String.format("%.4f",Double.parseDouble(one.get("ZJJE")+"")+zjje));
			double allfy=total.get("ALLFY")==null?0.0:Double.parseDouble(total.get("ALLFY")+"");
			total.put("ALLFY",String.format("%.4f",Double.parseDouble(one.get("ZJJE")+"")+allfy));
			if(i==sfmxlist.size()-1){
				count++;
				double tempzhzf=0.0;
				double tempxjzf=0.0;
				for(Map<String, Object> hjone : hjlist){
					if((hjone.get("CZGH")+"").equals(czgh)){
						tempzhzf=Double.parseDouble(hjone.get("ZHZF")+"");
						tempxjzf=Double.parseDouble(hjone.get("XJZF")+"");
						grzhzf+=tempzhzf;
						xjzf+=tempxjzf;
						break;
					}
				}
				for(Map<String, Object> ybzlf:zyzlflist){
					if((ybzlf.get("CZGH")+"").equals(temp.get("CZGH")+"")){
						allsffy+=Double.parseDouble(ybzlf.get("YBZLF")+"");
						temp.put("YBZLF", Double.parseDouble(ybzlf.get("YBZLF")+""));
						break;
					}
				}
				temp.put("ALLFY",String.format("%.4f",allsffy));
				temp.put("GRZHZF",String.format("%.4f",tempzhzf));
				temp.put("XJZF",String.format("%.4f",tempxjzf));
				l_data.add(temp);
			}
		}
		total.put("ALLFY", String.format("%.4f",Double.parseDouble(total.get("ALLFY")+"")+ybzlfhj));
		total.put("TOTAL", "合计:");
		total.put("GRZHZF",String.format("%.4f",grzhzf));
		total.put("XJZF",String.format("%.4f",xjzf));
		l_data.add(total);
		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,
					new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,
									l_data)));
		}
		ss.close();
		return prints;
	}
}
