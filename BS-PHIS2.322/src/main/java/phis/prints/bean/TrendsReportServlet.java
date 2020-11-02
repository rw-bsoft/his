/**
 * @(#)TrendsReportServlet.java Created on 2013-8-5 下午5:27:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import ctd.print.ColumnModel;
import ctd.print.DynaGridPrintUtil;
import ctd.print.DynamicPrint;
import ctd.print.PrintException;
import ctd.print.PrintUtil;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.JSONUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class TrendsReportServlet extends DynamicPrint {
	private final static String defaultContent = "text/html;charset=UTF-8";
	private final static String defaultErrorMsg = "打印服务器繁忙，请稍后再试。若问题仍然存在，请与系统管理员联系。";
	private final static int pageWidth = 560;
	private final static int pageHeight = 920;
	private final static int columnWidth = 900;
	public final static int textWidth = 100;
	public final static int textHeight = 22;
	public final static int fontSize = 16;
	private final static int titleFontSize = 24;
	private final static int columnHeaderHeight = 30;
	private final static boolean isColumnHeaderFontBond = true;
	private final static int titleHeight = 40;
	private final static int detailHeight = 22;
	private final static int pageFooterHeight = 22;
	private final static int pageHeaderHeight = 22;
	private static final String fontName = "宋体";
	private static final String pdfFontName = "STSong-Light";
	private static final String pdfEncoding = "UniGB-UCS2-H";
	private final static int bbbh_xzzf = 5;
	private final static int bbbh_mxzf = 6;
	private final static int bbbh_hzzf = 7;

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			String file = (String) config.get("file");
			Service sv = (Service) AppContextHolder.getBean(file);
			Context ctx = ContextUtils.getContext();
			HashMap<String, Object> res = new HashMap<String, Object>();
			sv.execute(config, res, ctx);
			String title = "医生医技检查收入统计表";//(String) res.get("title");
			ColumnModel[] columnModel = (ColumnModel[]) res.get("columnModel");
			Map<String, Object> pageHeaderData = (Map<String, Object>) res
					.get("pageHeaderData");
			List<HashMap<String, Object>> listData = (List<HashMap<String, Object>>) res
					.get("listData");
			List<JasperReport> reports = getDynamicJasperReport(
					new ArrayList<JasperDesign>(), title, columnModel,
					pageHeaderData, false);
			List<JasperPrint> prints = new ArrayList<JasperPrint>();
			for (JasperReport r : reports) {
				prints.add(PrintUtil.getJasperPrint(r,
						new HashMap<String, Object>(), DynaGridPrintUtil
								.createJRBeanCollectionDataSource(columnModel,
										listData)));
			}
			return prints;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			ColumnModel[] columnModel, Map<String, Object> pageHeaderData,
			boolean isSeparate) throws JRException {
		List<JasperReport> reports = new ArrayList<JasperReport>();
		List<JasperDesign> process_designs = getDynamicJasperDesign(designs,
				title, columnModel, pageHeaderData, isSeparate);
		for (JasperDesign design : process_designs) {
			reports.add(JasperCompileManager.compileReport(design));
		}
		return reports;
	}

	private List<JasperDesign> getDynamicJasperDesign(
			List<JasperDesign> designs, String title,
			ColumnModel[] columnModel, Map<String, Object> pageHeaderData,
			boolean isSeparate) throws JRException {
		JasperDesign design = getJasperDesign(title, true);
		designs.add(design);
		JRDesignBand titleBand = DynaGridPrintUtil.getJRDesignBand(titleHeight);
		JRDesignBand columnHeaderBand = DynaGridPrintUtil
				.getJRDesignBand(columnHeaderHeight);
		JRDesignBand detailBand = DynaGridPrintUtil
				.getJRDesignBand(detailHeight);
		JRDesignBand pageFooter = DynaGridPrintUtil
				.getJRDesignBand(pageFooterHeight);
		JRDesignBand pageHeader = DynaGridPrintUtil
				.getJRDesignBand(pageHeaderHeight);
		setBand(design, titleBand, columnHeaderBand, detailBand, pageFooter,
				pageHeader);
		// 标题
		JRDesignStaticText titleText = DynaGridPrintUtil.getJRDesignStaticText(
				title, titleFontSize, true);
		parseTitleText(titleText);
		titleBand.addElement(titleText);
		getPageHeaderTextField(pageHeader, pageHeaderData);
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
				staticText.setFontSize(fontSize);
				staticText.setHeight(columnHeaderHeight);
				staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
				staticText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
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
				textField.setFontSize(fontSize);
				textField.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
				textField.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
				textField.setHeight(detailHeight);
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
						pageHeaderData, isSeparate);
			}
			// 列标题
			JRDesignStaticText staticText = DynaGridPrintUtil
					.getJRDesignStaticText(columnModel[i].getText(), fontSize,
							isColumnHeaderFontBond);
			staticText.setFontSize(fontSize);
			staticText.setHeight(columnHeaderHeight);
			staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
			staticText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
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
			textField.setFontSize(fontSize);
			textField.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
			textField.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
			textField.setHeight(detailHeight);
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

	private void getPageHeaderTextField(JRDesignBand pageFooter,
			Map<String, Object> pageHeaderData) {
		int totalWidth = 0;
		for (int i = 0; i < pageHeaderData.values().size(); i++) {
			JRDesignStaticText staticText = DynaGridPrintUtil
					.getJRDesignStaticText((String) pageHeaderData.values()
							.toArray()[i], fontSize, isColumnHeaderFontBond);
			staticText.setFontSize(fontSize);
			staticText.setHeight(pageHeaderHeight);
			staticText.setWidth(columnWidth - 20);
			staticText.setX(10);
			staticText.setY(pageHeaderHeight * i);
			pageFooter.addElement(staticText);
		}
	}

	private void setBand(JasperDesign jd, JRDesignBand title,
			JRDesignBand columnheader, JRDesignBand detail,
			JRDesignBand pagefooter, JRDesignBand pageHeader) {
		jd.setTitle(title);
		jd.setPageHeader(pageHeader);
		jd.setColumnHeader(columnheader);
		JRDesignSection jrds = (JRDesignSection) jd.getDetailSection();
		jrds.addBand(detail);
		jd.setPageFooter(pagefooter);
	}

	private static void parseTitleText(JRDesignStaticText titleText) {
		titleText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		titleText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		titleText.setHeight(titleHeight);
		titleText.setWidth(columnWidth);
		titleText.setX(0);
		titleText.setY(0);
	}

	private static void getPageFooterTextField(JRDesignBand pageFooter) {
		JRDesignTextField f1 = getJRDesignTextField("", String.class);
		JRDesignTextField f2 = getJRDesignTextField("", String.class);
		f1.setEvaluationTime(EvaluationTimeEnum.NOW); // JREvaluationTime .now
		f2.setEvaluationTime(EvaluationTimeEnum.REPORT); // .report
		((JRDesignExpression) f1.getExpression())
				.setText("\"第\"+$V{PAGE_NUMBER}+\"页\"");
		((JRDesignExpression) f2.getExpression())
				.setText(" \"共\"+$V{PAGE_NUMBER}+\"页\"");
		f1.setX(300);
		f2.setX(400);
		pageFooter.addElement(f1);
		pageFooter.addElement(f2);
	}

	/**
	 * @param name
	 *            field的name
	 * @param clazz
	 *            value的class类型
	 * @return
	 */
	public static JRDesignTextField getJRDesignTextField(String name,
			Class clazz) {
		JRDesignTextField textField = new JRDesignTextField();
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$F{" + name + "}");
		expression.setValueClass(clazz);
		textField.setExpression(expression);
		textField.setFontSize(fontSize);
		textField.setFontName(fontName);
		textField.setPdfFontName(pdfFontName);
		textField.setPdfEncoding(pdfEncoding);
		textField.setHeight(textHeight);
		textField.setWidth(textWidth);
		textField.setBlankWhenNull(true);
		textField.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		return textField;
	}

	/**
	 * @param name
	 *            名称
	 * @param isLandscape
	 *            是否横向
	 * @return
	 */
	public static JasperDesign getJasperDesign(String name, boolean isLandscape) {
		JasperDesign design = new JasperDesign();
		design.setName(name);
		if (!isLandscape) {
			design.setPageWidth(pageWidth);
			design.setPageHeight(pageHeight);
			design.setOrientation(OrientationEnum.PORTRAIT);
		} else {
			design.setPageWidth(pageHeight);
			design.setPageHeight(pageWidth);
			design.setOrientation(OrientationEnum.LANDSCAPE);
		}
		design.setLeftMargin((pageHeight - columnWidth) / 2);
		design.setRightMargin((pageHeight - columnWidth) / 2);
		design.setTopMargin(7);
		design.setBottomMargin(8);
		design.setColumnCount(1);
		design.setColumnWidth(columnWidth);
		design.setColumnSpacing(0);
		design.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		return design;
	}
}
