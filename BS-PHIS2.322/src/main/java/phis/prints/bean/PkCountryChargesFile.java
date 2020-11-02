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

public class PkCountryChargesFile extends DynamicPrint {
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
	private final static int bbbh_kdks = 1;
	private final static int bbbh_kdys = 2;
	private final static int bbbh_zxks = 3;
	private final static int bbbh_zxys = 4;

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
			return doPrintCountry(request, response, config);
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
	//按村卫生室统计
	protected List<JasperPrint> doPrintCountry(Map<String, Object> request,
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
		String jgid = user.getManageUnitId();
		
		StringBuffer hql = new StringBuffer();
		String sfxmstr="2,3,4,9,8,32,6,49,6,5,12,13";
		hql.append("select rownum as COLID,a.sfxm as SFXM  from gy_sfxm a where a.sfxm in ("+sfxmstr+")");
		List<Map<String, Object>> sfxmlist=ss.createSQLQuery(hql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		Map<String, Object> sfxmmapcol= new HashMap<String, Object>();
		for(Map<String, Object> one : sfxmlist){
			sfxmmapcol.put(one.get("COLID")+"",one.get("SFXM")+"");
		}
		List<JasperPrint> prints = new ArrayList<JasperPrint>();
		// 用于存放列,第一个列会在分页后继续显示
		LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
		// l_data 用于存放数据
		List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
		ColumnModel newcm=new ColumnModel();
		newcm.setName("JGMC");
		newcm.setText("机构名称");
		newcm.setWdith(160);
		map.put("JGMC", newcm);
		newcm = new ColumnModel();
		newcm.setName("MZRC");
		newcm.setText("门诊人次");
		map.put("MZRC", newcm);
		newcm = new ColumnModel();
		newcm.setName("ZJFY");
		newcm.setText("总计费用");
		map.put("ZJFY", newcm);
		newcm = new ColumnModel();
		newcm.setName("FHBC");
		newcm.setText("符合补偿");
		map.put("FHBC", newcm);
		newcm = new ColumnModel();
		newcm.setName("HLBC");
		newcm.setText("合疗补偿");
		map.put("HLBC", newcm);
		newcm = new ColumnModel();
		newcm.setName("YLJZ");
		newcm.setText("医疗救助");
		map.put("YLJZ", newcm);
		newcm = new ColumnModel();
		newcm.setName("SSFY");
		newcm.setText("实收费用");
		map.put("SSFY", newcm);
		for(int i=1;i <=sfxmmapcol.size();i++){
			ColumnModel cm = new ColumnModel();
			cm.setName("xm"+sfxmmapcol.get(i+"").toString());
			try {
				cm.setText(DictionaryController.instance().get("phis.dictionary.feesDic").getText(sfxmmapcol.get(i+"").toString()));
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			map.put("xm"+sfxmmapcol.get(i+"").toString(), cm);
		}
		// 获取表单参数
		HashMap<String, Object> requestData = (HashMap<String, Object>) config.get("requestData");
		String title =user.getManageUnitName()+"村费用统计";
		String dateFrom = (String) requestData.get("dateFrom");
		String dateTo = (String) requestData.get("dateTo");
		String jgsql="select distinct a.jgid as JGID from ms_mzxx a " +
				" where a.jgid like '"+jgid+"%' and length(a.jgid)>9 and " +
				" to_char(a.sfrq,'yyyy-mm-dd HH24:mi:ss') >='"+dateFrom+"' and" +
				" to_char(a.sfrq,'yyyy-mm-dd HH24:mi:ss')<='"+dateTo+"' order by a.jgid ";
		List<Map<String, Object>> jglist=ss.createSQLQuery(jgsql.toString())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		Map<Integer, String> jgmap=new HashMap<Integer, String>();
		for(int i=0;i<jglist.size();i++){
			Map<String, Object> one=jglist.get(i);
			jgmap.put(i,one.get("JGID")+"");
		}
		HashMap<String, Object> total=new HashMap<String, Object>();
		total.put("JGMC","合计：");
		for(int i=0;i<jgmap.size();i++){
			HashMap<String, Object> temp=new HashMap<String, Object>();
			try {
				temp.put("JGMC", DictionaryController.instance().get("platform.reg.dictionary.organizationDic").getText(jgmap.get(i).toString()));
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			String sfhzsql="select sum(jscs) as JSCS,sum(zjje) as ZJFY ,sum(sum04) as FHBC , " +
					   " sum(qtys) as HLBC,sum(SUM11) as YLJZ,sum(zfje) as SSFY,sum(hbwc) as HBWC from ("+
			           " select a.zjje as zjje,nvl(n.SUM04,0) as SUM04,nvl(n.SUM11,0) as SUM11,a.zfje as zfje,a.qtys as qtys ," +
					   " a.hbwc as hbwc ,1 as jscs from ms_mzxx a left join nh_bsoft_jsjl n on a.mzxh=n.mzxh " +
					   " where a.jgid= '"+jgmap.get(i)+"'" +
			           " and to_char(a.sfrq,'yyyy-mm-dd HH24:mi:ss') >='"+dateFrom+"' and to_char(a.sfrq,'yyyy-mm-dd HH24:mi:ss')<='"+dateTo+"'" +
			           " union all " +
			           " select -a.zjje as zjje,-nvl(n.SUM04,0) as SUM04,-nvl(n.SUM11,0) as SUM11,-a.zfje as zfje,-a.qtys as qtys," +
			           " -a.hbwc as hbwc,-1 as  jscs from ms_mzxx a join ms_zffp b on a.mzxh=b.mzxh " +
			           " left join nh_bsoft_jsjl n on a.mzxh=n.mzxh " +
			           " where  a.jgid = '"+jgmap.get(i)+"'" +
			           " and to_char(b.zfrq,'yyyy-mm-dd HH24:mi:ss') >='"+dateFrom+"' and to_char(b.zfrq,'yyyy-mm-dd HH24:mi:ss')<='"+dateTo+"' " +
			           "  )";
			List<Map<String, Object>> sfhzlist=ss.createSQLQuery(sfhzsql.toString())
							.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(Map<String, Object> one:sfhzlist){
				temp.put("MZRC", one.get("JSCS")+"");
				total.put("MZRC", 
					Long.parseLong(total.get("MZRC")==null?"0":total.get("MZRC")+"")+Long.parseLong(one.get("JSCS")==null?"0":one.get("JSCS")+""));
				temp.put("ZJFY", one.get("ZJFY")+"");
				total.put("ZJFY", 
					Double.parseDouble(total.get("ZJFY")==null?"0":total.get("ZJFY")+"")+Double.parseDouble(one.get("ZJFY")==null?"0":one.get("ZJFY")+""));
				temp.put("FHBC", one.get("FHBC")+"");
				total.put("FHBC", 
					Double.parseDouble(total.get("FHBC")==null?"0":total.get("FHBC")+"")+Double.parseDouble(one.get("FHBC")==null?"0":one.get("FHBC")+""));
				temp.put("HLBC", one.get("HLBC")+"");
				total.put("HLBC", 
					Double.parseDouble(total.get("HLBC")==null?"0":total.get("HLBC")+"")+Double.parseDouble(one.get("HLBC")==null?"0":one.get("HLBC")+""));
				temp.put("YLJZ", one.get("YLJZ")+"");
				total.put("YLJZ", 
					Double.parseDouble(total.get("YLJZ")==null?"0":total.get("YLJZ")+"")+Double.parseDouble(one.get("YLJZ")==null?"0":one.get("YLJZ")+""));
				temp.put("SSFY", one.get("SSFY")+"");
				total.put("SSFY", 
					Double.parseDouble(total.get("SSFY")==null?"0":total.get("SSFY")+"")+Double.parseDouble(one.get("SSFY")==null?"0":one.get("SSFY")+""));
				temp.put("HBWC", one.get("HBWC")+"");
				total.put("HBWC", 
					Double.parseDouble(total.get("HBWC")==null?"0": total.get("HBWC")+"")+Double.parseDouble(one.get("HBWC")==null?"0":one.get("HBWC")+""));
			}
			String sfmxsql="select sfxm as SFXM,sum(zjje) as ZJJE from ("
					+" select a.czgh,b.sfxm,b.zjje as zjje from ms_mzxx a "
					+" join ms_sfmx b on a.mzxh=b.mzxh join gy_sfxm c on b.sfxm=c.sfxm"
					+" where a.jgid = '"+jgmap.get(i)+"' and a.sfrq > to_date('"+dateFrom+
					"','yyyy-mm-dd HH24:mi:ss') and a.sfrq < to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')" +
					" and b.sfxm in ("+sfxmstr+")"
					+" union all"
					+" select a.czgh,b.sfxm,-b.zjje as zjje from ms_mzxx a "
					+" join ms_sfmx b on a.mzxh=b.mzxh"
					+" join gy_sfxm c on b.sfxm=c.sfxm"
					+" join ms_zffp d on a.mzxh=d.mzxh"
					+" where a.jgid = '"+jgmap.get(i)+"' and d.zfrq > to_date('"+dateFrom+"','yyyy-mm-dd HH24:mi:ss') " +
					" and d.zfrq < to_date('"+dateTo+"','yyyy-mm-dd HH24:mi:ss')" +
					" and b.sfxm in ("+sfxmstr+")"
					+")group by sfxm order by sfxm ";
			List<Map<String, Object>> sfmxlist=ss.createSQLQuery(sfmxsql.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			
			for(Map<String, Object> one:sfmxlist){
				if((one.get("SFXM")+"").equals("13")){
					temp.put("xm"+one.get("SFXM"), Double.parseDouble(one.get("ZJJE")==null?"0":one.get("ZJJE")+""));
				}else{
					temp.put("xm"+one.get("SFXM"), one.get("ZJJE")+"");
				}
				total.put("xm"+one.get("SFXM"),Double.parseDouble(total.get("xm"+one.get("SFXM"))==null?"0":total.get("xm"+one.get("SFXM"))+"")
						+Double.parseDouble(temp.get("xm"+one.get("SFXM"))+""));
				}
			l_data.add(temp);
		}
		total.put("ZJFY", BSPHISUtil.getDouble(total.get("ZJFY")==null?"0":total.get("ZJFY")+"",2));
		total.put("FHBC", BSPHISUtil.getDouble(total.get("FHBC")==null?"0":total.get("FHBC")+"",2));
		total.put("HLBC", BSPHISUtil.getDouble(total.get("HLBC")==null?"0":total.get("HLBC")+"",2));
		total.put("YLJZ", BSPHISUtil.getDouble(total.get("YLJZ")==null?"0":total.get("YLJZ")+"",2));
		total.put("SSFY", BSPHISUtil.getDouble(total.get("SSFY")==null?"0":total.get("SSFY")+"",2));
		total.put("HBWC", BSPHISUtil.getDouble(total.get("HBWC")==null?"0":total.get("HBWC")+"",2));
		for(int i=1;i <=sfxmmapcol.size();i++){
			total.put("xm"+sfxmmapcol.get(i+""), BSPHISUtil.getDouble(total.get("xm"+sfxmmapcol.get(i+"")),2));
		}
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
