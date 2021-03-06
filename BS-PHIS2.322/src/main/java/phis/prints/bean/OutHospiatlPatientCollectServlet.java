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
import ctd.dictionary.DictionaryController;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class OutHospiatlPatientCollectServlet extends DynamicPrint_BySZ {

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
			String ref = user.getManageUnit().getRef();
			String uid = user.getManageUnitId();

			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title = "出院病人汇总表";// (String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			int rq = (Integer) requestData.get("rqlx");
			if(rq == 1){
				title = "入院病人汇总表";
			}
			List<Object> ksdmarray = (List<Object>) requestData.get("ksdm");
			String ksdmstrs = ksdmarray + "";
			String ksdm = ksdmstrs.substring(1, ksdmstrs.length() - 1);
			int ksnum = ksdm.indexOf(",");
			String topUnitId = ParameterUtil.getTopUnitId();
			String rqlx = ParameterUtil.getParameter(topUnitId,
					BSPHISSystemArgument.ETLRQLX, "SFRQ",
					"收费日期(SFRQ)(住院统计自动使用JSRQ)、结帐日期(JZRQ)和汇总日期(HZRQ)", ctx);
			// 用于存放列,第一个列会在分页后继续显示
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			if (ksnum > 0) {
				l_data.clear();
				map.clear();
				StringBuilder sqlBuilder = new StringBuilder();
				// 生成表结构
				ColumnModel cm0 = new ColumnModel();
				cm0.setName("TOTAL");
				cm0.setText("序号");
				map.put("TOTAL", cm0);
				ColumnModel cm2 = new ColumnModel();
				cm2.setName("KSMC");
				cm2.setText("科室名称");
				map.put("KSMC", cm2);
				ColumnModel cm3 = new ColumnModel();
				cm3.setName("RS");
				cm3.setText("人数");
				map.put("RS", cm3);
				int bbbz = 10;// 报表标志
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();

				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("select ID as ID , OFFICENAME as OFFICENAME from sys_office where  ORGANIZCODE = '");
				sqlBuilder.append(ref);
				sqlBuilder.append("' and (OFFICECODE not in  ");
				sqlBuilder
						.append("(select PARENTID from SYS_Office  where PARENTID is not null)");
				sqlBuilder.append("  ) and HOSPITALDEPT='1' ");
				if (ksdm != null && !"".equals(ksdm) && !"null".equals(ksdm)) {
					sqlBuilder.append(" and ID in (").append(ksdm).append(")");
				}
				sqlBuilder.append(" and LOGOFF<>'1'");
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				// xzSize = list_data.size();
				List<HashMap<String, Object>> detail_list = null;
				String id = "";
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT sum(D.ZJJE) as ZJJE, D.SFXM as SFXM, D.KSDM as KSDM FROM ( ");
				sqlBuilder
						.append(" SELECT sum(ZJJE) AS ZJJE, FYXM as SFXM, FYKS as KSDM ");
				sqlBuilder
						.append(" FROM  ZY_BRRY A , ZY_FYMX B,ZY_ZYJS C WHERE A.ZYH = B.ZYH AND B.ZYH = C.ZYH and  B.JSCS = C.JSCS and B.JSXZ = C.JSXZ AND  A.JGID = '"
								+ uid + "' ");
				sqlBuilder.append(" and A.CYPB >= 8");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" and to_char(A.RYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.RYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 2:
						sqlBuilder
							.append(" and to_char(A.CYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.CYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 3:
						sqlBuilder
							.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 4:
						sqlBuilder
							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 5:
						sqlBuilder
							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
				}
//				if (rqlx.equals("SFRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("JZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("HZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND B.FYKS in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder.append(" GROUP BY B.FYKS,FYXM union all ");
				sqlBuilder
						.append(" SELECT -SUM(ZJJE) AS ZJJE, FYXM as SFXM, FYKS as KSDM ");
				sqlBuilder
						.append(" FROM  ZY_BRRY A , ZY_FYMX B,ZY_JSZF C WHERE A.ZYH = B.ZYH AND B.ZYH = C.ZYH and  B.JSCS = C.JSCS and B.JSXZ = C.JSXZ AND  A.JGID = '"
								+ uid + "' ");
				sqlBuilder.append(" and A.CYPB >= 8");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" and to_char(A.RYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.RYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 2:
						sqlBuilder
							.append(" and to_char(A.CYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.CYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 3:
						sqlBuilder
							.append(" and to_char(C.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 4:
						sqlBuilder
							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 5:
						sqlBuilder
							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
				}
//				if (rqlx.equals("SFRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("JZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("HZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND B.FYKS in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder.append(" GROUP BY B.FYKS,FYXM");
				sqlBuilder.append(") D GROUP BY D.SFXM,D.KSDM order by D.SFXM");
				detail_list = ss.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();

				StringBuilder rcBuilder = new StringBuilder();
				for (int h = 0; h < detail_list.size(); h++) {
					HashMap<String, Object> rcmap = detail_list.get(h);
					rcBuilder = new StringBuilder();
					rcBuilder.append(" SELECT count( distinct A.ZYHM) as ZYCR");
					rcBuilder
							.append(" FROM  ZY_BRRY A , ZY_FYMX B,ZY_ZYJS C WHERE A.ZYH = B.ZYH AND B.ZYH = C.ZYH and  B.JSCS = C.JSCS and B.JSXZ = C.JSXZ AND  A.JGID = '"
									+ uid + "' ");
					rcBuilder.append(" and A.CYPB >= 8");
					switch(rq){
						case 1:
							rcBuilder
								.append(" and to_char(A.RYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
									+ dateFrom
									+ "' and to_char(A.RYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
									+ dateTo + "' ");
							break;
						case 2:
							rcBuilder
								.append(" and to_char(A.CYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
									+ dateFrom
									+ "' and to_char(A.CYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
									+ dateTo + "' ");
							break;
						case 3:
							rcBuilder
								.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
									+ dateFrom
									+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
									+ dateTo + "' ");
							break;
						case 4:
							rcBuilder
								.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
									+ dateFrom
									+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
									+ dateTo + "' ");
							break;
						case 5:
							rcBuilder
								.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
									+ dateFrom
									+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
									+ dateTo + "' ");
							break;
					}
//					if (rqlx.equals("SFRQ")) {
//						rcBuilder
//								.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//										+ dateFrom
//										+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//										+ dateTo + "' ");
//					}
//					if (rqlx.equals("JZRQ")) {
//						rcBuilder
//								.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//										+ dateFrom
//										+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//										+ dateTo + "' ");
//					}
//					if (rqlx.equals("HZRQ")) {
//						rcBuilder
//								.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//										+ dateFrom
//										+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//										+ dateTo + "' ");
//					}
					rcBuilder.append(" AND B.FYKS =");
					rcBuilder.append(rcmap.get("KSDM"));
					List<Map<String, Object>> rc_list = ss
							.createSQLQuery(rcBuilder.toString())
							.setResultTransformer(
									Transformers.ALIAS_TO_ENTITY_MAP).list();
					if (rc_list.size() > 0) {
						rcmap.put("ZYRS", rc_list.get(0).get("ZYCR"));
					}
				}

				List<Map<String, Object>> list_xmgb = this.getListXmgb(bbbz,
						uid, ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						bbbz, uid, ss, list_xmgb);
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < detail_list.size(); j++) {
						Map<String, Object> m1 = detail_list.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
				for (int i = 0; i < list_column.size(); i++) {
					ColumnModel cm = new ColumnModel();
					cm.setName(list_column.get(i).get("GBXM").toString());
					cm.setText((String) list_column.get(i).get("XMMC"));
					map.put(list_column.get(i).get("GBXM").toString(), cm);
				}
				boolean isAdded = false;
				for (Map<String, Object> m : detail_list) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该科室
						if (d.get("KSDM").equals(m.get("KSDM"))) {
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
						map_ls.put("KSDM", m.get("KSDM"));
						for (int i = 0; i < list_data.size(); i++) {
							if (list_data.get(i).get("ID")
									.equals(m.get("KSDM"))) {
								map_ls.put("KSMC",
										list_data.get(i).get("OFFICENAME"));
							}
						}
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
					double valuezj = 0.00;
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
							valuezj+= Double.parseDouble(m.get(m_xmgb.get("GBXM")+"")+"");
						}
					}
					m.put("zj",String.format("%1$.2f", valuezj));
				}
				// 计算最底下一列的统计
				HashMap<String, Object> dataTotal = new HashMap<String, Object>();
				dataTotal.put("TOTAL", "合计");
				double valuezj = 0.00;
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
							valuezj += Double.parseDouble(d.get(column
									.get("GBXM") + "")
									+ "");
						}
					}
					dataTotal.put(column.get("GBXM").toString(),
							String.format("%1$.2f", value));
				}
				dataTotal.put("zj",String.format("%1$.2f", valuezj));
				l_data.add(dataTotal);
			} else {
				l_data.clear();
				map.clear();
				StringBuilder sqlBuilder;
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
				cm3.setName("BRCH");
				cm3.setText("病人床号");
				map.put("BRCH", cm3);
				cm3 = new ColumnModel();
				ColumnModel zdCm = new ColumnModel();
				zdCm.setName("ZDMC");
				switch(rq){
					case 1:
						cm3.setName("RYRQ");
						cm3.setText("入院日期");
						map.put("RYRQ", cm3);
						zdCm.setText("入院诊断");
						break;
					case 3:
						cm3.setName("JSRQ");
						cm3.setText("出院日期");
						map.put("JSRQ", cm3);
						zdCm.setText("出院诊断");
						break;
				}
				zdCm.setWdith(150);
				map.put("ZDMC", zdCm);
				ColumnModel cm4 = new ColumnModel();
				cm4.setName("BRXZ");
				cm4.setText("病人性质");
				map.put("BRXZ", cm4);
				int bbbz = 10;// 报表标志
				List<Map<String, Object>> list_column = new ArrayList<Map<String, Object>>();

				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("select ID as ID , OFFICENAME as OFFICENAME from sys_office where  ORGANIZCODE = '");
				sqlBuilder.append(ref);
				sqlBuilder.append("' and (OFFICECODE not in  ");
				sqlBuilder
						.append("(select PARENTID from SYS_Office  where PARENTID is not null)");
				sqlBuilder.append("  ) and HOSPITALDEPT='1' ");
				if (ksdm != null && !"".equals(ksdm) && !"null".equals(ksdm)) {
					sqlBuilder.append(" and ID = '").append(ksdm).append("'");
				}
				sqlBuilder.append(" and LOGOFF<>'1'");
				List<Map<String, Object>> list_data = ss
						.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				// xzSize = list_data.size();
				List<Map<String, Object>> detail_list = null;
				String id = "";
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT D.ZYHM as ZYHM,D.BRXM as BRXM,D.BRID as BRID,D.BRCH as BRCH,D.BRXZ as BRXZ,sum(D.ZJJE) as ZJJE, D.SFXM as SFXM ");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,D.RYRQ as RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,D.JSRQ as JSRQ");
						break;
				}
				sqlBuilder.append(" FROM ( ");
				sqlBuilder
						.append(" SELECT A.ZYHM as ZYHM,E.BRXM as BRXM,E.BRID as BRID,A.BRCH as BRCH,A.BRXZ as BRXZ,sum(ZJJE) AS ZJJE, B.FYXM as SFXM");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,to_char(A.RYRQ, 'yyyy-mm-dd') as RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,to_char(A.JSRQ, 'yyyy-mm-dd') as JSRQ");
						break;
				}
				sqlBuilder
						.append(" FROM  ZY_BRRY A , ZY_FYMX B,ZY_ZYJS C,MS_BRDA E WHERE A.ZYH = B.ZYH AND A.BRID=E.BRID AND B.ZYH = C.ZYH and  B.JSCS = C.JSCS and B.JSXZ = C.JSXZ AND  A.JGID = '"
								+ uid + "' ");
				sqlBuilder.append(" and A.CYPB >= 8");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" and to_char(A.RYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.RYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 2:
						sqlBuilder
							.append(" and to_char(A.CYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.CYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 3:
						sqlBuilder
							.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 4:
						sqlBuilder
							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 5:
						sqlBuilder
							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
				}
//				if (rqlx.equals("SFRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JSRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JSRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("JZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("HZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND B.FYKS in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder
						.append(" GROUP BY A.ZYHM,E.BRXM ");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,A.RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,A.JSRQ");
						break;
				}
				sqlBuilder.append(" ,E.BRID,A.BRCH,A.BRXZ,B.FYXM union all ");
				sqlBuilder
						.append(" SELECT A.ZYHM as ZYHM,E.BRXM as BRXM,E.BRID as BRID,A.BRCH as BRCH,A.BRXZ as BRXZ,-SUM(ZJJE) AS ZJJE, B.FYXM as SFXM");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,to_char(A.RYRQ, 'yyyy-mm-dd') as RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,to_char(A.JSRQ, 'yyyy-mm-dd') as JSRQ");
						break;
				}
				sqlBuilder
						.append(" FROM  ZY_BRRY A , ZY_FYMX B,ZY_JSZF C,MS_BRDA E WHERE A.ZYH = B.ZYH AND A.BRID=E.BRID AND B.ZYH = C.ZYH and  B.JSCS = C.JSCS and B.JSXZ = C.JSXZ AND  A.JGID = '"
								+ uid + "' ");
				sqlBuilder.append(" and A.CYPB >= 8");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" and to_char(A.RYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.RYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 2:
						sqlBuilder
							.append(" and to_char(A.CYRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(A.CYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 3:
						sqlBuilder
							.append(" and to_char(C.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 4:
						sqlBuilder
							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
					case 5:
						sqlBuilder
							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
								+ dateFrom
								+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
								+ dateTo + "' ");
						break;
				}
//				if (rqlx.equals("SFRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.ZFRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.ZFRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("JZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.JZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.JZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
//				if (rqlx.equals("HZRQ")) {
//					sqlBuilder
//							.append(" and to_char(C.HZRQ, 'yyyy-mm-dd hh24:mi:ss') >= '"
//									+ dateFrom
//									+ "' and to_char(C.HZRQ,'yyyy-mm-dd hh24:mi:ss') <= '"
//									+ dateTo + "' ");
//				}
				if (list_data != null && list_data.size() > 0) {
					sqlBuilder.append(" AND B.FYKS in (");
					for (int i = 0; i < list_data.size(); i++) {
						id = String.valueOf(list_data.get(i).get("ID"));
						sqlBuilder.append(id);
						if (i != list_data.size() - 1) {
							sqlBuilder.append(" , ");
						}
					}
					sqlBuilder.append(")");
				}
				sqlBuilder
						.append(" GROUP BY A.ZYHM,E.BRXM");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,A.RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,A.JSRQ");
						break;
				}
				sqlBuilder.append(" ,E.BRID,A.BRCH,A.BRXZ,B.FYXM");
				sqlBuilder
						.append(") D GROUP BY D.ZYHM,D.BRXM");
				switch(rq){
					case 1:
						sqlBuilder
							.append(" ,D.RYRQ");
						break;
					case 3:
						sqlBuilder
							.append(" ,D.JSRQ");
						break;
				}
				sqlBuilder.append(" ,D.BRID,D.BRCH,D.BRXZ,D.SFXM");
				detail_list = ss.createSQLQuery(sqlBuilder.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				List<Map<String, Object>> list_xmgb = this.getListXmgb(bbbz,
						uid, ss);
				List<Map<String, Object>> list_columnALL = this.getListColumn(
						bbbz, uid, ss, list_xmgb);
				for (int i = 0; i < list_columnALL.size(); i++) {
					Map<String, Object> m = list_columnALL.get(i);
					for (int j = 0; j < detail_list.size(); j++) {
						Map<String, Object> m1 = detail_list.get(j);
						if (m1.get("SFXM").toString()
								.equals(m.get("GBXM").toString())
								&& !list_column.contains(m)) {
							list_column.add(m);
						}
					}
				}
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
				for (Map<String, Object> m : detail_list) {
					isAdded = false;
					// 遍历报表的数据集合：l_data，如果此病人性质已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果有该科室
						if (d.get("ZYHM").equals(m.get("ZYHM"))) {
							d.put(m.get("SFXM").toString(),
									String.format("%1$.2f", m.get("ZJJE")));
							isAdded = true; // 该条数据已添加，则标记为true
							if (m.get("BRCH") != null && m.get("BRCH") != "") {
								d.put("BRCH", m.get("BRCH") + "");
							} else {
								d.put("BRCH", "");
							}
							if (m.get("BRXZ") != null && m.get("BRXZ") != "") {
								d.put("BRXZ",
										DictionaryController
												.instance()
												.getDic("phis.dictionary.patientProperties_ZY")
												.getText(m.get("BRXZ") + ""));
							} else {
								d.put("BRXZ", "");
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
						map_ls.put("RYRQ", m.get("RYRQ"));
						map_ls.put("JSRQ", m.get("JSRQ"));
						map_ls.put(m.get("SFXM") + "",
								String.format("%1$.2f", m.get("ZJJE")));
						if (m.get("BRCH") != null && m.get("BRCH") != "") {
							map_ls.put("BRCH", m.get("BRCH") + "");
						} else {
							map_ls.put("BRCH", "");
						}
						if (m.get("BRXZ") != null && m.get("BRXZ") != "") {
							map_ls.put(
									"BRXZ",
									DictionaryController
											.instance()
											.getDic("phis.dictionary.patientProperties_ZY")
											.getText(m.get("BRXZ") + ""));
						} else {
							map_ls.put("BRXZ", "");
						}
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
					//add by lizhi 2017-12-14增加入出院诊断
					String zyhm = m.get("ZYHM")+"";
					if(m.get("ZYHM")!=null && !"".equals(zyhm)){
						StringBuffer sb = new StringBuffer();
						sb.append("select b.ZDMC as ZDMC from ZY_BRRY a, ZY_RYZD b where a.JGID = b.JGID and a.ZYH = b.ZYH and a.ZYHM = "+zyhm);
						switch(rq){
							case 1:
								sb.append(" and b.ZDLB = 2");
								break;
							case 3:
								sb.append(" and b.ZDLB = 3");
								break;
						}
						List<Map<String,Object>> dataList = ss.createSQLQuery(sb.toString())
								.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
								.list();
						String zdmc = "";
						if(dataList.size()>0){
							for(int i=0;i<dataList.size();i++){
								Map<String, Object> data = dataList.get(i);
								if(i==0){
									zdmc+=data.get("ZDMC")+"";
								}else{
									zdmc+=","+data.get("ZDMC");
								}
							}
						}
						m.put("ZDMC", zdmc);
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
				dataTotal.put("TOTAL", "合计");
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
			ColumnModel cm = new ColumnModel();
			cm.setName("zj");
			cm.setText("合计");
			map.put("zj", cm);
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
				if ("KSMC".equals(columnModel[1].getName() + "") ) {
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
						if (i > 0 && i < 5) {
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
