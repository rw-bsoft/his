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

public class WarNursesAssessServlet extends DynamicPrint {
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
		Long ksdm = Long.parseLong(requestData.get("wardId") + "");
		String sql_column = "select a.YGDM as YGDM,b.PERSONNAME as PERSONNAME from GY_QXKZ a,SYS_PERSONNEL b where a.YGDM=b.PERSONID and a.YWLB=4 and a.KSDM="
				+ ksdm;
		List<Map<String, Object>> list_column = ss
				.createSQLQuery(sql_column.toString())
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
			return doPrintHSPG(request, response, config);
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
	@SuppressWarnings({ "unchecked", "unused" })
	protected List<JasperPrint> doPrintHSPG(Map<String, Object> request,
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
		// 取到机构id
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			int wardId = 0;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				wardId = Integer.parseInt(user.getProperty("wardId") + "");
			}
			DecimalFormat df2 = new DecimalFormat("0.00");
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			List<Map<String, Object>> list_column = getListColumn(requestData,
					ss);
			// 生成表结构
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("FYMC");
			cm1.setText("项目");
			cm1.setWdith(180);
			map.put("FYMC", cm1);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("YGDM").toString());
				cm.setText((String) column.get("PERSONNAME"));
				map.put(column.get("YGDM").toString(), cm);
			}
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("ColumnTOTAL");
			cm2.setText("合计金额");
			map.put("ColumnTOTAL", cm2);
			String title = "护士评估";// (String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			title = title + "(" + dateFrom.substring(0, 10) + " - "
					+ dateTo.substring(0, 10) + ")";
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < list_column.size(); i++) {
				String ygdm = list_column.get(i).get("YGDM") + "";
				String hql = "select sum(t.ZJJE) as FYJE,t.FYXH as FYXH,t.FYMC as FYMC from ZY_FYMX t WHERE  t.FYBQ ="
						+ wardId
						+ " and t.SRGH='"
						+ ygdm
						+ "' and t.JFRQ >=to_date('"
						+ dateFrom
						+ "','yyyy-mm-dd hh24:mi:ss') and t.JFRQ <= to_date('"
						+ dateTo
						+ "','yyyy-mm-dd hh24:mi:ss') and t.YPLX=0 and t.YZXH <> 0 group by t.FYXH,t.FYMC";
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
						data.put("FYMC", map_data.get("FYMC"));
						data.put(list_column.get(i).get("YGDM") + "",
								df2.format(map_data.get("FYJE")));
						l_data.add(data);
						continue;
					}
					// 如果l_data中有元素
					if (l_data.size() > 0) {
						// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部合并
						for (int h = 0; h < l_data.size(); h++) {
							Map<String, Object> d = l_data.get(h);
							// 如果存在
							if (d.get("FYMC").equals(map_data.get("FYMC"))) {
								isExist = true;
								existIndex = h;
								break;
							}
						}
					}
					if (isExist) {
						HashMap<String, Object> existData = l_data
								.get(existIndex);
						existData.put(list_column.get(i).get("YGDM") + "",
								df2.format(map_data.get("FYJE")));
					} else {
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("FYMC", map_data.get("FYMC"));
						data.put(list_column.get(i).get("YGDM") + "",
								df2.format(map_data.get("FYJE")));
						l_data.add(data);
					}
				}

				for (int h = 0; h < l_data.size(); h++) {
					HashMap<String, Object> data = l_data.get(h);
					Set<String> set = data.keySet();
					Double columnTotal = 0D;
					for (Iterator<String> it = set.iterator(); it.hasNext();) {
						String name = it.next();
						if (!name.equals("YSGH") && !name.equals("ColumnTOTAL")
								&& !name.equals("FYMC")) {
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
			Double allTotal = 0D;
			for (int j = 0; j < l_data.size(); j++) {
				Map<String, Object> d = l_data.get(j);
				allTotal += Double.parseDouble(d.get("ColumnTOTAL") + "");
			}
			dataTotal.put("ColumnTOTAL", String.format("%1$.2f", allTotal));
			l_data.add(dataTotal);
			ColumnModel[] columnModel = map.values().toArray(
					new ColumnModel[map.size()]);
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel,
					list_column, false);
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
			ColumnModel[] columnModel, List<Map<String, Object>> list_column,
			boolean isSeparate) throws JRException {
		List<JasperReport> reports = new ArrayList<JasperReport>();
		List<JasperDesign> process_designs = getDynamicJasperDesign(designs,
				title, columnModel, list_column, isSeparate);
		for (JasperDesign design : process_designs) {
			reports.add(JasperCompileManager.compileReport(design));
		}
		return reports;
	}

	private List<JasperDesign> getDynamicJasperDesign(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, List<Map<String, Object>> list_column,
			boolean isSeparate) throws JRException {
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
		parseTitleText(titleText, list_column);
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
				staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
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
				if (i == 0) {
					textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
				} else {
					textField.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
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
						list_column, isSeparate);
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
			List<Map<String, Object>> list_column) {
		// titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(50 * list_column.size());
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
