package phis.prints.bean;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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

public class HospitalAntibioticDrugsFile extends DynamicPrint {
	private final static int columnWidth = 400;
	public final static int textWidth = 80;
	public final static int textHeight = 15;
	public final static int fontSize = 10;
	private final static int titleFontSize = 16;
	private final static int columnHeaderHeight = 18;
	private final static boolean isColumnHeaderFontBond = true;
	private final static int titleHeight = 20;
	private final static int detailHeight = 15;
	private final static int pageFooterHeight = 15;

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		try {
			return doPrintAntibioticDrugs(request, response);
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
	//抗生素药品统计
	protected List<JasperPrint> doPrintAntibioticDrugs(Map<String, Object> request,
			Map<String, Object> response)
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
		String jgid = user.getManageUnitId();
		// 获取表单参数
		String title =user.getManageUnitName()+"抗生素处方统计";
		String dateFrom = (String) request.get("dateFrom");
		String dateTo = (String) request.get("dateTo");
				
		StringBuffer hql = new StringBuffer();
		hql.append("select YSDM,sum(case when ksssl >0 then 1 else 0 end ) KSSCFSL ,count(1) as CFSL from ("+
				" select a.ysdm,a.cfsb,sum(case when c.kssdj in ('1','2','3') then 1 else 0 end ) as ksssl" +
				" from ms_cf01 a join ms_cf02 b on a.cfsb=b.cfsb "+
				" join yk_typk c on b.ypxh=c.ypxh where a.jgid='"+jgid+"' and a.djly='1'"+                
				" and a.mzxh is not null and to_char(a.kfrq,'yyyy-mm-dd')>='"+dateFrom+"'"+
				" and to_char(a.kfrq,'yyyy-mm-dd')<='"+dateTo+"'"+
				" group by a.ysdm,a.cfsb ) group by YSDM");
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		List<JasperPrint> prints = new ArrayList<JasperPrint>();
		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		ColumnModel newcm=new ColumnModel();
		newcm.setName("YSXM");
		newcm.setText("医生姓名");
		newcm.setWdith(160);
		map.put("YSXM", newcm);
		newcm = new ColumnModel();
		newcm.setName("KSSCFSL");
		newcm.setText("抗生素处方数量");
		map.put("KSSCFSL", newcm);
		newcm = new ColumnModel();
		newcm.setName("CFSL");
		newcm.setText("处方数量");
		map.put("CFSL", newcm);
		newcm = new ColumnModel();
		newcm.setName("BFB");
		newcm.setText("百分比");
		map.put("BFB", newcm);
		HashMap<String, Object> total=new HashMap<String, Object>();
		total.put("YSXM", "合计：");
		for(HashMap<String, Object>  one :l_data){
			total.put("KSSCFSL", total.get("KSSCFSL")==null?
					Long.parseLong(one.get("KSSCFSL")+""):Long.parseLong(one.get("KSSCFSL")+"")+Long.parseLong(total.get("KSSCFSL")+""));
			total.put("CFSL", total.get("CFSL")==null?
					Long.parseLong(one.get("CFSL")+""):Long.parseLong(one.get("CFSL")+"")+Long.parseLong(total.get("CFSL")+""));
			one.put("BFB", BSPHISUtil.getDouble((Double.parseDouble(one.get("KSSCFSL")+"")
					/Double.parseDouble(one.get("CFSL")+"")*100)+"",2)+"%");
			try {
				one.put("YSXM", DictionaryController.instance().get("chis.dictionary.Personnel")
						.getText(one.get("YSDM")+""));
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
		if(total.get("KSSCFSL")!=null && total.get("CFSL")!=null ){
		total.put("BFB", BSPHISUtil.getDouble((Double.parseDouble(total.get("KSSCFSL")+"")
				/Double.parseDouble(total.get("CFSL")+"")*100)+"",2)+"%");
		}
		l_data.add(total);
		ColumnModel[] columnModel = map.values().toArray(
				new ColumnModel[map.size()]);
		List<JasperReport> reports = getDynamicJasperReport(
				new ArrayList<JasperDesign>(), title, columnModel, false);

		for (JasperReport r : reports) {
			prints.add(PrintUtil.getJasperPrint(r,new HashMap<String, Object>(), DynaGridPrintUtil
							.createJRBeanCollectionDataSource(columnModel,l_data)));
		}
		ss.close();
		return prints;
	}
}
