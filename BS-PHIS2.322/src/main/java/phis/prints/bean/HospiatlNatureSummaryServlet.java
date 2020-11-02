package phis.prints.bean;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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

import phis.source.BSPHISSystemArgument;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class HospiatlNatureSummaryServlet extends DynamicPrint_BySZ {

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

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		// 取到机构id
		Context ctx = ContextUtils.getContext();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
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
			String title = "住院性质费用汇总表";// (String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			List<Object> tjxzarray = (List<Object>) requestData.get("tjxz");// 统计性质
			String tjxzstrs = tjxzarray + "";
			String tjxz = tjxzstrs.substring(1, tjxzstrs.length() - 1);
			int xznum = tjxz.indexOf(",");
			String topUnitId = ParameterUtil.getTopUnitId();
			String rqlx = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.ETLRQLX, "SFRQ",
					"收费日期(SFRQ)(住院统计自动使用JSRQ)、结帐日期(JZRQ)和汇总日期(HZRQ)", ctx);
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			if (xznum > 0) {
				String jbxz="2000";//金保
				String zfxz="1000";//自费
				title = title +"(住院人数：";
				map.clear();
				l_data.clear();
				//金保
				StringBuffer hql = new StringBuffer();
				hql.append("SELECT d.BRXZ as BRXZ,d.XZMC as XZMC, sum(d.ZYRS) as ZYRS,d.SFXM as SFXM, sum(d.ZJJE) as ZJJE,sum(d.ZFJE) as ZFJE from (");
				hql.append(
						"SELECT a.BRXZ as BRXZ,c.XZMC as XZMC, COUNT(a.ZYH) as ZYRS,b.FYXM as SFXM, sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_ZYJS a, ZY_JSMX b, GY_BRXZ c,ZY_BRRY e,MS_BRDA f WHERE (a.ZYH = b.ZYH) and (a.BRXZ = c.BRXZ) and a.ZYH=e.ZYH and e.BRID=f.BRID and a.jscs = b.jscs ")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hql.append(" and to_char(a.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hql.append(" and a.BRXZ in(");
				hql.append(jbxz);
				hql.append(" )");
				hql.append(" and b.JSXZ in(");
				hql.append(jbxz);
				hql.append(" )");
				hql.append(" GROUP BY a.BRXZ,c.XZMC,b.FYXM union all ");
				hql.append(
						"SELECT a.brxz as BRXZ,c.XZMC as XZMC,-COUNT(a.ZYH) as ZYRS,b.FYXM as SFXM ,-sum(b.ZJJE) as ZJJE,-sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_ZYJS a ,ZY_JSMX b ,GY_BRXZ c WHERE a.zyh=b.zyh and b.jsxz=c.brxz and a.jscs = b.jscs")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hql.append(" and to_char(a.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hql.append(" and a.BRXZ in(");
				hql.append(jbxz);
				hql.append(" )");
				hql.append(" and b.JSXZ in(");
				hql.append(jbxz);
				hql.append(" )");
				hql.append(" GROUP BY a.BRXZ,c.XZMC,b.FYXM) d");
				hql.append(" GROUP BY d.BRXZ,d.XZMC,d.SFXM order by d.SFXM");
				List<HashMap<String, Object>> list_data = ss
						.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				//自费
				StringBuffer hqlzf = new StringBuffer();
				hqlzf.append("SELECT d.BRXZ as BRXZ,d.XZMC as XZMC, sum(d.ZYRS) as ZYRS,d.SFXM as SFXM, sum(d.ZJJE) as ZJJE,sum(d.ZFJE) as ZFJE from (");
				hqlzf.append(
						"SELECT a.BRXZ as BRXZ,c.XZMC as XZMC, COUNT(a.ZYH) as ZYRS,b.FYXM as SFXM, sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_ZYJS a, ZY_JSMX b, GY_BRXZ c,ZY_BRRY e,MS_BRDA f WHERE (a.ZYH = b.ZYH) and (a.BRXZ = c.BRXZ) and a.ZYH=e.ZYH and e.BRID=f.BRID and a.jscs = b.jscs ")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hqlzf.append(" and to_char(a.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hqlzf.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hqlzf.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hqlzf.append(" and a.BRXZ in(");
				hqlzf.append(zfxz);
				hqlzf.append(" )");
				hqlzf.append(" and b.JSXZ in(");
				hqlzf.append(zfxz);
				hqlzf.append(" )");
				hqlzf.append(" GROUP BY a.BRXZ,c.XZMC,b.FYXM union all ");
				hqlzf.append(
						"SELECT a.brxz as BRXZ,c.XZMC as XZMC,-COUNT(a.ZYH) as ZYRS,b.FYXM as SFXM ,-sum(b.ZJJE) as ZJJE,-sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_ZYJS a ,ZY_JSMX b ,GY_BRXZ c WHERE a.zyh=b.zyh and b.jsxz=c.brxz and a.jscs = b.jscs ")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hqlzf.append(" and to_char(a.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hqlzf.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hqlzf.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hqlzf.append(" and a.BRXZ in(");
				hqlzf.append(zfxz);
				hqlzf.append(" )");
				hqlzf.append(" and b.JSXZ in(");
				hqlzf.append(zfxz);
				hqlzf.append(" )");
				hqlzf.append(" GROUP BY a.BRXZ,c.XZMC,b.FYXM) d");
				hqlzf.append(" GROUP BY d.BRXZ,d.XZMC,d.SFXM order by d.SFXM");
				List<HashMap<String, Object>> zflist_data = ss.createSQLQuery(hqlzf.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				list_data.addAll(zflist_data);
				for (int h = 0; h < list_data.size(); h++) {
					HashMap<String, Object> rcmap = list_data.get(h);
					StringBuffer rchql = new StringBuffer();
					rchql.append("SELECT COUNT(distinct a.ZYH) as ZYRS")
							.append(" FROM ZY_ZYJS a, ZY_JSMX b, GY_BRXZ c WHERE (a.ZYH = b.ZYH) and (a.BRXZ = c.BRXZ) and a.jscs = b.jscs ")
							.append(" and a.JGID=" + uid);
					if (rqlx.equals("SFRQ")) {
						rchql.append(" and to_char(a.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
					}
					if (rqlx.equals("JZRQ")) {
						rchql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
					}
					if (rqlx.equals("HZRQ")) {
						rchql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
					}
					rchql.append(" and a.BRXZ=");
					rchql.append(rcmap.get("BRXZ"));
					List<Map<String, Object>> rc_list = ss
							.createSQLQuery(rchql.toString())
							.setResultTransformer(
									Transformers.ALIAS_TO_ENTITY_MAP).list();
					if (rc_list.size() > 0) {
						rcmap.put("ZYRS", rc_list.get(0).get("ZYRS"));
					}
				}
				//标题增加住院人数
				StringBuffer rshql = new StringBuffer();;
				rshql.append("SELECT COUNT(distinct a.ZYH) as ZYRS")
				.append(" FROM ZY_ZYJS a, ZY_JSMX b, GY_BRXZ c WHERE (a.ZYH = b.ZYH) and (a.BRXZ = c.BRXZ) and a.jscs = b.jscs ")
				.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					rshql.append(" and to_char(a.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
					+ dateFrom
					+ "' and to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
					+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					rshql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
					+ dateFrom
					+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
					+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					rshql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
					+ dateFrom
					+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
					+ dateTo + "' ");
				}
				List<Map<String, Object>> rs_list = ss.createSQLQuery(rshql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				title = title + rs_list.get(0).get("ZYRS")+"人)";

				// 用于存放列,第一个列会在分页后继续显示
				List<Map<String, Object>> list_xmgb = this.getListXmgb(11, uid,
						ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						11, uid, ss, list_xmgb);
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < list_data.size(); j++) {
						Map<String, Object> m1 = list_data.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
				// 生成表结构
				ColumnModel cm0 = new ColumnModel();
				cm0.setName("TOTAL");
				cm0.setText("序号");
				map.put("TOTAL", cm0);
				ColumnModel cm2 = new ColumnModel();
				cm2.setName("XZMC");
				cm2.setText("性质名称");
				map.put("XZMC", cm2);
				ColumnModel cm3 = new ColumnModel();
				cm3.setName("RS");
				cm3.setText("人次");
				map.put("RC", cm3);
				for (int i = 0; i < list_column.size(); i++) {
					ColumnModel cm = new ColumnModel();
					cm.setName(list_column.get(i).get("GBXM").toString());
					cm.setText((String) list_column.get(i).get("XMMC"));
					map.put(list_column.get(i).get("GBXM").toString(), cm);
				}
				boolean isAdded = false;
				for (Map<String, Object> m : list_data) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该病人性质
						if (d.get("BRXZ").equals(m.get("BRXZ"))) {
							d.put(m.get("SFXM").toString(),
									String.format("%1$.2f", m.get("ZJJE")));
							isAdded = true; // 该条数据已添加，则标记为true
							d.put("RS", m.get("ZYRS") + "");
							break;
						}
					}
					// 遍历完l_data 没有找到该病人性质，则新增一个map放进去
					if (isAdded == false) {
						HashMap<String, Object> map_ls = new HashMap<String, Object>();
						map_ls.put("TOTAL", l_data.size() + 1);
						map_ls.put("BRXZ", m.get("BRXZ"));
						map_ls.put("XZMC", m.get("XZMC"));
						map_ls.put(m.get("SFXM") + "",
								String.format("%1$.2f", m.get("ZJJE")));
						map_ls.put("RS", m.get("ZYRS") + "");
						map_ls.put("SFXM", m.get("SFXM"));
						l_data.add(map_ls);
						isAdded = false;

					}

				}
				// 将有指定归并的项目，归并到指定项目
				for (Map<String, Object> m : l_data) {
					for (Map<String, Object> m_xmgb : list_xmgb) {
						if (m.containsKey(m_xmgb.get("SFXM") + "")
								&& m.containsKey(m_xmgb.get("GBXM") + "")) {
							if (!m_xmgb.get("SFXM").toString()
									.equals(m_xmgb.get("GBXM").toString())) {
								double gbxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("GBXM") + "") + "");
								double sfxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("SFXM") + "") + "");
								m.put(String.valueOf(m_xmgb.get("GBXM")),
										String.format("%1$.2f",
												(gbxmvalue + sfxmvalue)));
							}
						}
					}
				}
				// 计算最底下一列的统计
				HashMap<String, Object> dataTotal = new HashMap<String, Object>();
				dataTotal.put("KSMC", "合计");
				for (Map<String, Object> column : list_column) {
					double value = 0.00;
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						if (d.get(column.get("GBXM") + "") != null
								&& !"null"
										.equals(d.get(column.get("GBXM") + ""))) {
							value += Double.parseDouble(d.get(column
									.get("GBXM") + "")
									+ "");
						}
					}
					dataTotal.put(column.get("GBXM").toString(),
							String.format("%1$.2f", value));
				}
				l_data.add(dataTotal);
			} else {
				map.clear();
				l_data.clear();
				StringBuffer hql = new StringBuffer();
				hql.append("SELECT d.ZYHM as ZYHM,d.BRXM as BRXM,d.BRID as BRID,d.ZYTS as ZYTS,d.SFXM as SFXM, sum(d.ZJJE) as ZJJE,sum(d.ZFJE) as ZFJE from (");
				hql.append(
						"SELECT e.ZYHM as ZYHM,f.BRXM as BRXM ,f.BRID as BRID,round(nvl(e.CYRQ,sysdate)-e.RYRQ) as ZYTS,b.FYXM as SFXM, sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_ZYJS a, ZY_JSMX b, GY_BRXZ c,ZY_BRRY e,MS_BRDA f WHERE (a.ZYH = b.ZYH) and (a.BRXZ = c.BRXZ) and a.ZYH=e.ZYH and e.BRID=f.BRID and a.jscs = b.jscs ")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hql.append(" and to_char(a.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hql.append(" and a.BRXZ in(");
				hql.append(tjxz);
				hql.append(" )");
				hql.append(" and b.JSXZ in(");
				hql.append(tjxz);
				hql.append(" )");
				hql.append(" GROUP BY e.ZYHM,f.BRXM,f.BRID,(nvl(e.CYRQ,sysdate)-e.RYRQ),b.FYXM union all ");
				hql.append(
						"SELECT f.ZYHM as ZYHM,g.BRXM as BRXM ,g.BRID as BRID,round(nvl(f.CYRQ,sysdate)-f.RYRQ) as ZYTS,b.FYXM as SFXM, -sum(b.ZJJE) as ZJJE,-sum(b.ZFJE) as ZFJE ")
						.append(" FROM ZY_JSZF a, ZY_JSMX b, GY_BRXZ c,ZY_ZYJS e,ZY_BRRY f,MS_BRDA g WHERE (a.ZYH = b.ZYH) and (e.BRXZ = c.BRXZ) and a.ZYH=f.ZYH and f.BRID=g.BRID and a.ZYH = e.ZYH and a.jscs = b.jscs and a.jscs = e.jscs ")
						.append(" and a.JGID=" + uid);
				if (rqlx.equals("SFRQ")) {
					hql.append(" and to_char(a.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("JZRQ")) {
					hql.append(" and to_char(a.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				if (rqlx.equals("HZRQ")) {
					hql.append(" and to_char(a.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
							+ dateFrom
							+ "' and to_char(a.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
							+ dateTo + "' ");
				}
				hql.append(" and e.BRXZ in(");
				hql.append(tjxz);
				hql.append(" )");
				hql.append(" and b.JSXZ in(");
				hql.append(tjxz);
				hql.append(" )");
				hql.append(" GROUP BY f.ZYHM,g.BRXM,g.BRID,(nvl(f.CYRQ,sysdate)-f.RYRQ),b.FYXM) d");
				hql.append(" GROUP BY d.ZYHM,d.BRXM,d.BRID,d.ZYTS,d.SFXM order by d.SFXM");
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(hql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				// 用于存放列,第一个列会在分页后继续显示
				List<Map<String, Object>> list_xmgb = this.getListXmgb(11, uid,
						ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						11, uid, ss, list_xmgb);
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < list_data.size(); j++) {
						Map<String, Object> m1 = list_data.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
				// 生成表结构
				ColumnModel cm0 = new ColumnModel();
				cm0.setName("TOTAL");
				cm0.setText("序号");
				map.put("TOTAL", cm0);
				ColumnModel cm1 = new ColumnModel();
				cm1.setName("ZYHM");
				cm1.setText("住院号码");
				map.put("ZYHM", cm1);
				ColumnModel cm2 = new ColumnModel();
				cm2.setName("BRXM");
				cm2.setText("病人姓名");
				map.put("BRXM", cm2);
				ColumnModel cm3 = new ColumnModel();
				cm3.setName("ZYTS");
				cm3.setText("住院天数");
				map.put("ZYTS", cm3);
				for (int i = 0; i < list_column.size(); i++) {
					ColumnModel cm = new ColumnModel();
					cm.setName(list_column.get(i).get("GBXM").toString());
					cm.setText((String) list_column.get(i).get("XMMC"));
					map.put(list_column.get(i).get("GBXM").toString(), cm);
				}
				//增加最右侧总计列 zhaojian 2017-09-19
				ColumnModel cmzj = new ColumnModel();
				cmzj.setName("zj");
				cmzj.setText("总计");
				map.put("zj", cmzj);
				boolean isAdded = false;
				for (Map<String, Object> m : list_data) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该病人性质
						if (d.get("ZYHM").equals(m.get("ZYHM"))) {
							d.put(m.get("SFXM").toString(),
									String.format("%1$.2f", m.get("ZJJE")));
							isAdded = true; // 该条数据已添加，则标记为true
							if (m.get("ZYTS") != null && m.get("ZYTS") != ""
									&& !"null".equals(m.get("ZYTS"))
									&& !"".equals(m.get("ZYTS"))) {
								d.put("ZYTS", m.get("ZYTS"));
							} else {
								d.put("ZYTS", "0");
							}
							break;
						}
					}
					// 遍历完l_data 没有找到该病人性质，则新增一个map放进去
					if (isAdded == false) {
						HashMap<String, Object> map_ls = new HashMap<String, Object>();
						map_ls.put("TOTAL", l_data.size() + 1);
						map_ls.put("ZYHM", m.get("ZYHM"));
						map_ls.put("BRXM", m.get("BRXM"));
						map_ls.put(m.get("SFXM") + "",
								String.format("%1$.2f", m.get("ZJJE")));
						if (m.get("ZYTS") != null && m.get("ZYTS") != ""
								&& !"null".equals(m.get("ZYTS"))
								&& !"".equals(m.get("ZYTS"))) {
							map_ls.put("ZYTS", m.get("ZYTS"));
						} else {
							map_ls.put("ZYTS", "0");
						}
						l_data.add(map_ls);
						isAdded = false;

					}

				}
				// 将有指定归并的项目，归并到指定项目
				for (Map<String, Object> m : l_data) {
					for (Map<String, Object> m_xmgb : list_xmgb) {
						if (m.containsKey(m_xmgb.get("SFXM") + "")
								&& m.containsKey(m_xmgb.get("GBXM") + "")) {
							if (!m_xmgb.get("SFXM").toString()
									.equals(m_xmgb.get("GBXM").toString())) {
								double gbxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("GBXM") + "") + "");
								double sfxmvalue = Double.parseDouble(m
										.get(m_xmgb.get("SFXM") + "") + "");
								m.put(String.valueOf(m_xmgb.get("GBXM")),
										String.format("%1$.2f",
												(gbxmvalue + sfxmvalue)));
							}
						}
					}
				}				
				//计算最右侧一列的统计-总计 zhaojian 2017-09-19
				double valuezj = 0.00;
				for (int j = 0; j < l_data.size(); j++) {
					Map<String, Object> d = l_data.get(j);
					double value = 0.00;
					for (Map<String, Object> column : list_column) {
					if (d.get(column.get("GBXM") + "") != null
							&& !"null"
									.equals(d.get(column.get("GBXM") + ""))) {
						value += Double.parseDouble(d.get(column
								.get("GBXM") + "")
								+ "");
						valuezj += Double.parseDouble(d.get(column
								.get("GBXM") + "")
								+ "");
					}
				}
					d.put("zj",	String.format("%1$.2f", value));
			}
				// 计算最底下一列的统计
				HashMap<String, Object> dataTotal = new HashMap<String, Object>();
				dataTotal.put("ZYTS", "合计");
				for (Map<String, Object> column : list_column) {
					double value = 0.00;
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						if (d.get(column.get("GBXM") + "") != null
								&& !"null"
										.equals(d.get(column.get("GBXM") + ""))) {
							value += Double.parseDouble(d.get(column
									.get("GBXM") + "")
									+ "");
						}
					}
					dataTotal.put(column.get("GBXM").toString(),
							String.format("%1$.2f", value));
				}
				//增加最右侧总计列 zhaojian 2017-09-19
				dataTotal.put("zj",String.format("%1$.2f", valuezj));
				l_data.add(dataTotal);
			}
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

			return prints;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ss != null) {
				ss.close();
			}
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
		parseTitleText(titleText, columnModel.length);
		titleBand.addElement(titleText);
		int totalWidth = 0;
		if (!isSeparate) {
			if (columnModel.length > 1) {
				if ("XZMC".equals(columnModel[1].getName() + "")) {
					for (int i = 0; i < columnModel.length; i++) {
						if (columnModel[i].isHide()) {
							continue;
						}
						int width = columnModel[i].getWdith();
						totalWidth += width;
						// 列标题
						JRDesignStaticText staticText = DynaGridPrintUtil
								.getJRDesignStaticText(
										columnModel[i].getText(), fontSize,
										isColumnHeaderFontBond);
						staticText.setWidth(width);
						staticText
								.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
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
						if (i == 1) {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
						} else {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
						}
						textField.setX(totalWidth - width);
						textField.getLineBox().getPen().setLineWidth(1f);
						textField.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						// textField.setStretchWithOverflow(true); //是否换行打印
						detailBand.addElement(textField);
						// 底部页码
						getPageFooterTextField(pageFooter);
					}
				} else {
					for (int i = 0; i < columnModel.length; i++) {
						if (columnModel[i].isHide()) {
							continue;
						}
						int width = columnModel[i].getWdith();
						totalWidth += width;
						// 列标题
						JRDesignStaticText staticText = DynaGridPrintUtil
								.getJRDesignStaticText(
										columnModel[i].getText(), fontSize,
										isColumnHeaderFontBond);
						staticText.setWidth(width);
						staticText
								.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
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
						if (i > 0 && i < 3) {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
						} else {
							textField
									.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
						}
						textField.setX(totalWidth - width);
						textField.getLineBox().getPen().setLineWidth(1f);
						textField.getLineBox().getPen()
								.setLineStyle(LineStyleEnum.SOLID);
						// textField.setStretchWithOverflow(true); //是否换行打印
						detailBand.addElement(textField);
						// 底部页码
						getPageFooterTextField(pageFooter);
					}

				}
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
	private static void parseTitleText(JRDesignStaticText titleText,
			int columnLength) {
		// titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(50 * (columnLength / 2));
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
