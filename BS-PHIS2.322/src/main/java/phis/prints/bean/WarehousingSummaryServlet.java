package phis.prints.bean;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

public class WarehousingSummaryServlet extends DynamicPrint {
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
	 * 
	 * @param uid
	 * @param ss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListColumn(
			Map<String, Object> requestData, Session ss) {
		Long lbbm = Long.parseLong(requestData.get("lbbm").toString()
				.substring(1, requestData.get("lbbm").toString().length() - 1));
		String sql_column = "select HSLB as HSLB,HSBM as HSBM,HSMC as HSMC,ZBLB as ZBLB from WL_HSLB where SJHS ="
				+ lbbm;
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(sql_column.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (list_column.size() <= 0) {
			sql_column = "select HSLB as HSLB,HSMC as HSMC,HSBM as HSBM,ZBLB as ZBLB from WL_HSLB where HSLB ="
					+ lbbm;
			list_column = ss.createSQLQuery(sql_column.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
		}
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

			int zblb = 0;
			if (requestData.get("zblb") != null) {
				zblb = Integer.parseInt(requestData.get("zblb") + "");
			}
			int wzzd = 0;
			if (requestData.get("wzzd") != null) {
				wzzd = Integer.parseInt(requestData.get("wzzd") + "");
			}
			int lzfs = 0;
			if (requestData.get("lzfs") != null) {
				lzfs = Integer.parseInt(requestData.get("lzfs") + "");
			}
			if (zblb == 1) {
				return doPrintZBLB(request, response, config);

			}
			if (wzzd == 1) {
				return doPrintWZZD(request, response, config);
			}
			if (lzfs == 1) {// 执行科室
				// 主界面数据源表单是执行科室表单
				return doPrintLZFS(request, response, config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 按供货单位汇总
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
	protected List<JasperPrint> doPrintZBLB(Map<String, Object> request,
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
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
		// 取到机构id
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			int kfxh = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (kfxh == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ manaUnitId
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						kfxh = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			List<Map<String, Object>> list_column = getListColumn(requestData,
					ss);
			// 生成表结构
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("GHDW");
			cm1.setText("供货单位");
			cm1.setWdith(180);
			map.put("GHDW", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("HSLB").toString());
				cm.setText((String) column.get("HSMC"));
				map.put(column.get("HSLB").toString(), cm);
			}
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("ColumnTOTAL");
			cm2.setText("合计金额");
			map.put("ColumnTOTAL", cm2);
			String str = "";
			for (int a = 0; a < list_column.size(); a++) {
				str = str + list_column.get(a).get("ZBLB") + ",";
			}
			str = str.substring(0, str.length() - 1);
			String title = "入库汇总报表";//(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			for (int i = 0; i < list_column.size(); i++) {
				String hsbm = list_column.get(i).get("HSBM") + "";
				String hql = "SELECT A.DWXH as DWXH,ghdw.DWMC as DWMC,SUM(A.RKJE * A.YWLB) as RKJE FROM WL_WZZD b,(select rk01.ZBLB as ZBLB,rk01.DWXH as DWXH,rk02.JLXH as JLXH,rk02.WZXH as WZXH,rk02.WZJE as RKJE,lzfs.YWLB as YWLB from WL_RK02 rk02,WL_RK01 rk01,WL_LZFS lzfs where rk01.KFXH ="
						+ kfxh
						+ " and rk01.JZRQ >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd hh24:mi:ss') and rk01.JZRQ <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd hh24:mi:ss') and rk01.ZBLB in ("
						+ str
						+ ") and rk02.DJXH =rk01.DJXH and lzfs.FSXH = rk01.LZFS) A Left outer Join WL_GHDW ghdw ON A.DWXH = ghdw.DWXH WHERE A.WZXH = b.WZXH and b.HSLB in (select HSLB from WL_HSLB where ZBLB in ("
						+ str
						+ ") and hsbm like '"
						+ hsbm
						+ "%') GROUP BY A.DWXH, ghdw.DWMC";
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();

				// 生成数据
				for (int j = 0; j < list_data.size(); j++) {
					boolean isExist = false;
					int existIndex = 0;
					Map<String, Object> map_data = list_data.get(j);
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						if (map_data.get("DWMC") != null) {
							data.put("GHDW", map_data.get("DWMC"));
						} else {
							data.put("GHDW", "盘盈入库");
						}
						data.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
						l_data.add(data);
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部合并
						for (int h = 0; h < l_data.size(); h++) {
							Map<String, Object> d = l_data.get(h);
							// 如果存在
							if (d.get("GHDW").equals(map_data.get("DWMC"))) {
								isExist = true;
								existIndex = h;
								break;
							}
						}
					}
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						existData.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
					} else {
						HashMap<String, Object> data = new HashMap<String, Object>();
						if (map_data.get("DWMC") != null) {
							data.put("GHDW", map_data.get("DWMC"));
						} else {
							data.put("GHDW", "盘盈入库");
						}
						data.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
						l_data.add(data);
					}
				}

				for (int h = 0; h < l_data.size(); h++) {
					HashMap<String, Object> data = l_data.get(h);
					Set<String> set = data.keySet();
					Double columnTotal = 0D;
					for (Iterator<String> it = set.iterator(); it.hasNext();) {
						String name = it.next();
						if (!name.equals("GHDW") && !name.equals("ColumnTOTAL")) {
							columnTotal += Double.parseDouble(data.get(name)
									+ "");
						}
					}
					data.put("ColumnTOTAL",
							String.format("%1$.2f", columnTotal));
				}
			}
			// 计算统计
			HashMap<String, Object> dataTotal = new HashMap<String, Object>();
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
	 * 
	 * 按流转方式
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
	protected List<JasperPrint> doPrintLZFS(Map<String, Object> request,
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
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			int kfxh = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (kfxh == 0) {
				String YWLB = "6";
				List<Map<String, Object>> listKfxx = ss
						.createSQLQuery(
								"select a.KSDM as KSDM,b.KFMC as KFMC,b.EJKF as EJKF,a.MRBZ as MRBZ,b.KFLB as KFLB,b.LBXH as LBXH,b.GLKF as GLKF,b.WXKF as WXKF,b.CKFS as CKFS,b.CSBZ as CSBZ,b.ZJBZ as ZJBZ,b.ZJYF as ZJYF,b.HZPD as HZPD,b.PDZT as PDZT,b.KFZT as KFZT,b.KFZB as KFZB from GY_QXKZ a,WL_KFXX b where a.KSDM=b.KFXH and b.KFZT<>0 and a.YGDM='"
										+ user.getUserId()
										+ "' and a.JGID='"
										+ manaUnitId
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						kfxh = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			List<Map<String, Object>> list_column = getListColumn(requestData,
					ss);
			// 生成表结构
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("LZFS");
			cm1.setText("项目");
			map.put("LZFS", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("HSLB").toString());
				cm.setText((String) column.get("HSMC"));
				map.put(column.get("HSLB").toString(), cm);
			}
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("ColumnTOTAL");
			cm2.setText("合计金额");
			map.put("ColumnTOTAL", cm2);
			String str = "";
			for (int a = 0; a < list_column.size(); a++) {
				str = str + list_column.get(a).get("ZBLB") + ",";
			}
			str = str.substring(0, str.length() - 1);
			String title = "入库汇总报表";//(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < list_column.size(); i++) {
				String hsbm = list_column.get(i).get("HSBM") + "";
				String hql = "SELECT A.LZFS as DWXH,A.FSMC as DWMC,SUM(A.RKJE) as RKJE FROM WL_WZZD wzzd,(select rk01.ZBLB as ZBLB,rk01.LZFS as LZFS,rk02.JLXH as JLXH,rk02.WZXH as WZXH,lzfs.YWLB * rk02.WZJE as RKJE,lzfs.FSMC as FSMC from WL_RK02 rk02,WL_RK01 rk01,WL_LZFS lzfs where rk01.KFXH ="
						+ kfxh
						+ " and rk01.JZRQ >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd hh24:mi:ss') and rk01.JZRQ <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd hh24:mi:ss') and rk01.ZBLB in ("
						+ str
						+ ") and rk02.DJXH =rk01.DJXH and lzfs.FSXH = rk01.LZFS) A WHERE A.WZXH = wzzd.WZXH and wzzd.HSLB in (select HSLB as HSLB from WL_HSLB where ZBLB in ("
						+ str
						+ ") and hsbm like '"
						+ hsbm
						+ "%') GROUP BY A.LZFS,A.FSMC";
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				// 生成数据
				for (int j = 0; j < list_data.size(); j++) {
					boolean isExist = false;
					int existIndex = 0;
					Map<String, Object> map_data = list_data.get(j);
					if (l_data.size() == 0) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("LZFS", map_data.get("DWMC"));
						data.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
						l_data.add(data);
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部合并
						for (int h = 0; h < l_data.size(); h++) {
							Map<String, Object> d = l_data.get(h);
							// 如果存在
							if (d.get("LZFS").equals(map_data.get("DWMC"))) {
								isExist = true;
								existIndex = h;
								break;
							}
						}
					}
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						existData.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
					} else {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("LZFS", map_data.get("DWMC"));
						data.put(list_column.get(i).get("HSLB") + "",
								String.format("%1$.2f", map_data.get("RKJE")));
						l_data.add(data);
					}
				}
				for (int h = 0; h < l_data.size(); h++) {
					HashMap<String, Object> data = l_data.get(h);
					Set<String> set = data.keySet();
					Double columnTotal = 0D;
					for (Iterator<String> it = set.iterator(); it.hasNext();) {
						String name = it.next();
						if (!name.equals("LZFS") && !name.equals("ColumnTOTAL")) {
							columnTotal += Double.parseDouble(data.get(name)
									+ "");
						}
					}
					data.put("ColumnTOTAL",
							String.format("%1$.2f", columnTotal));
				}
			}
			// 计算统计
			HashMap<String, Object> dataTotal = new HashMap<String, Object>();
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
	 * 按物资字典
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
	protected List<JasperPrint> doPrintWZZD(Map<String, Object> request,
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
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();

			int kfxh = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			if (kfxh == 0) {
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
						kfxh = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			}
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String hslb = requestData
					.get("hslb")
					.toString()
					.substring(1,
							requestData.get("hslb").toString().length() - 1);
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// 生成表结构
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("XH");
			cm1.setText("序号");
			cm1.setWdith(30);
			map.put("XH", cm1);
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("WZMC");
			cm2.setText("物资名称");
			cm2.setWdith(200);
			map.put("WZMC", cm2);
			ColumnModel cm3 = new ColumnModel();
			cm3.setName("WZGG");
			cm3.setText("物资规格");
			map.put("WZGG", cm3);
			ColumnModel cm4 = new ColumnModel();
			cm4.setName("WZDW");
			cm4.setText("物资单位");
			map.put("WZDW", cm4);
			ColumnModel cm5 = new ColumnModel();
			cm5.setName("WZSL");
			cm5.setText("物资数量");
			map.put("WZSL", cm5);
			ColumnModel cm6 = new ColumnModel();
			cm6.setName("WZJE");
			cm6.setText("物资金额");
			map.put("WZJE", cm6);
			String title = "入库汇总报表";//(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			String hql = "SELECT B.WZXH as WZXH,a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,SUM(B.WZSL) as WZSL,SUM(B.WZJE) as WZJE FROM WL_WZZD a,(SELECT RK02.JLXH as JLXH,RK02.WZXH as WZXH,RK02.WZJG as WZJG,RK02.WZSL as WZSL,RK02.WZJE as WZJE FROM WL_RK01 RK01, WL_RK02 RK02, WL_WZZD WZZD WHERE RK01.DJXH = RK02.DJXH AND RK01.KFXH ="
					+ kfxh
					+ "  AND RK01.JZRQ  >=to_date('"
					+ dateFrom
					+ "','yyyy-mm-dd hh24:mi:ss') AND RK01.JZRQ <= to_date('"
					+ dateTo
					+ "','yyyy-mm-dd hh24:mi:ss') AND WZZD.WZXH = RK02.WZXH AND RK01.JGID='"
					+ jgid
					+ "' AND WZZD.HSLB IN ("
					+ hslb
					+ ") AND (RK01.THDJ IS NULL OR (RK01.THDJ <= 0 AND RK01.THDJ <> -1)) AND RK01.DJZT = 2 Union SELECT RK02.JLXH, RK02.WZXH, RK02.WZJG,0-RK02.WZSL,0-RK02.WZJE FROM WL_RK01 RK01,WL_RK02 RK02,WL_WZZD WZZD WHERE RK01.DJXH = RK02.DJXH AND RK01.KFXH="
					+ kfxh
					+ "  AND RK01.JZRQ  >=to_date('"
					+ dateFrom
					+ "','yyyy-mm-dd hh24:mi:ss') AND RK01.JZRQ <= to_date('"
					+ dateTo
					+ "','yyyy-mm-dd hh24:mi:ss') AND WZZD.WZXH = RK02.WZXH AND RK01.JGID ='"
					+ jgid
					+ "' AND WZZD.HSLB IN ("
					+ hslb
					+ ")  AND (RK01.THDJ > 0 Or RK01.THDJ = -1) AND RK01.DJZT = 2) B WHERE B.WZXH = a.WZXH AND WZSL <> 0 GROUP BY B.WZXH, a.WZMC, a.WZGG, a.WZDW";
			List<Map<String, Object>> list_data = ss
					.createSQLQuery(hql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			// 生成数据
			int n = 1;
			for (int i = 0; i < list_data.size(); i++) {
				Map<String, Object> map_data = list_data.get(i);
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("XH", n++);
				data.put("WZMC", map_data.get("WZMC"));
				data.put("WZGG", map_data.get("WZGG"));
				data.put("WZDW", map_data.get("WZDW"));
				data.put("WZSL", String.format("%1$.2f", map_data.get("WZSL")));
				data.put("WZJE", String.format("%1$.2f", map_data.get("WZJE")));
				l_data.add(data);
			}
			// 计算统计
			HashMap<String, Object> dataTotal = new HashMap<String, Object>();
			Double valuesl = 0D;
			Double valueje = 0D;
			for (Map<String, Object> column : l_data) {
				valuesl += Double.parseDouble(column.get("WZSL").toString()
						+ "");
				valueje += Double.parseDouble(column.get("WZJE").toString()
						+ "");
			}
			dataTotal.put("WZSL", String.format("%1$.2f", valuesl));
			dataTotal.put("WZJE", String.format("%1$.2f", valueje));
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
