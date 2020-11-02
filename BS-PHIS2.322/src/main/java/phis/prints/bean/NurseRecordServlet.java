package phis.prints.bean;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;
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

public class NurseRecordServlet extends DynamicPrint {
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

		String sql_column = "select b.XMBH as XMBH,b.XMMC as XMMC from ENR_JG01 a,ENR_JG02 b where a.JGBH=b.JGBH";
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
			return doPrintHSJL(request, response, config);
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
	protected List<JasperPrint> doPrintHSJL(Map<String, Object> request,
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
			String jgid = user.getManageUnit().getId();
			// 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			LinkedHashMap<String, ColumnModel> map = new LinkedHashMap<String, ColumnModel>();
			List<String> colname = new ArrayList<String>();
			int m = 0;
			List<Map<String, Object>> list_column = getListColumn(requestData,
					ss);
			// 生成表结构
			ColumnModel cm1 = new ColumnModel();
			cm1.setName("HLSJ");
			cm1.setText("护理时间");
			cm1.setWdith(106);
			map.put("HLSJ", cm1);
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("HLRY");
			cm2.setText("护理人员");
			cm2.setWdith(80);
			map.put("HLRY", cm2);
			for (Map<String, Object> column : list_column) {
				ColumnModel cm = new ColumnModel();
				cm.setName(column.get("XMBH").toString());
				cm.setText((String) column.get("XMMC"));
				cm.setWdith(80);
				colname.add(m, column.get("XMBH").toString());
				m++;
				map.put(column.get("XMBH").toString(), cm);
			}
			String title = "护理记录查询";// (String) config.get("title");
			String zyh = "0";
			if (requestData.get("zyh") != null) {
				zyh = requestData.get("zyh") + "";
			}
			String dateFromHL = requestData.get("dateFromHL") + "";
			String dateToHL = requestData.get("dateToHL") + "";
			// l_data 用于存放数据
			List<HashMap<String, Object>> l_data = new ArrayList<HashMap<String, Object>>();
			// 病人信息
			StringBuffer hqlbrxx = new StringBuffer(
					"select distinct d.ZYHM as ZYHM,e.BRXM as BRXM,f.OFFICENAME as KSDM,f1.OFFICENAME as BRBQ,(case when d.BRXB=1 then '男' when d.BRXB=2 then '女' when d.BRXB=9 then '未说明的性别' when d.BRXB=0 then '未知的性别' end) as BRXB,d.BRCH as BRCH,(case when d.CYPB=0 then '在院病人' when d.CYPB=1 then '出院证明' when d.CYPB=2 then '预结出院' when d.CYPB=8 then '正常出院' when d.CYPB=9 then '终结出院' when d.CYPB=99 then '注销出院' end) as CYPB,(case when d.HLJB=0 then '特级' when d.HLJB=1 then '一级' when d.HLJB=2 then '二级' when d.HLJB=3 then '三级' end) as HLJB,(case when d.BRQK=1 then '危重' when d.BRQK=2 then '急诊' when d.BRQK=3 then '一般' when d.BRQK=4 then '其他' end) as BRQK,d.CSNY as CSNY from ENR_JL01 a,ENR_JL02 b,SYS_PERSONNEL c,ZY_BRRY d,MS_BRDA e,SYS_OFFICE f,SYS_OFFICE f1 where a.JLBH=b.JLBH and a.SXHS=c.PERSONID and a.ZYH=d.ZYH and d.BRID=e.BRID and d.BRKS=f.ID and d.BRBQ=f1.ID and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')>='"
							+ dateFromHL
							+ "' and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')<='"
							+ dateToHL + "'");
			hqlbrxx.append(" and a.JGID=" + jgid);
			if (!"0".equals(zyh)) {
				try {
					zyh = BSPHISUtil.get_public_fillleft(
							zyh,
							"0",
							ParameterUtil.getParameter(jgid, "ZYHM",
									"0000000001", "住院号码", ctx).length());
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
				hqlbrxx.append(" and d.ZYHM=" + zyh);
			}
			hqlbrxx.append(" and a.JLZT<>9");
			List<Map<String, Object>> list_databrxx = ss
					.createSQLQuery(hqlbrxx.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
					.list();
			for (int n = 0; n < list_databrxx.size(); n++) {
				HashMap<String, Object> BRdata = new HashMap<String, Object>();// l_data中放入病人信息
				Map<String, Object> map_databrxx = list_databrxx.get(n);
				BRdata.put("HLSJ", "住院号码:" + map_databrxx.get("ZYHM"));
				BRdata.put("HLRY", "姓名:" + map_databrxx.get("BRXM"));
				BRdata.put(colname.get(0).toString(),
						"性别:" + map_databrxx.get("BRXB") + "");
				if (map_databrxx.get("CSNY") != null) {
					BRdata.put(
							colname.get(1).toString(),
							"年龄:"
									+ BSPHISUtil.getPersonAge(
											(Date) map_databrxx.get("CSNY"),
											null).get("ages"));
				} else {
					BRdata.put(colname.get(1).toString(), "年龄:无");
				}
				if (map_databrxx.get("KSDM") != null) {
					BRdata.put(colname.get(2).toString(),
							"科室:" + map_databrxx.get("KSDM") + "");
				} else {
					BRdata.put(colname.get(2).toString(), "科室:无");
				}
				if (map_databrxx.get("BRBQ") != null) {
					BRdata.put(colname.get(3).toString(),
							"病区:" + map_databrxx.get("BRBQ") + "");
				} else {
					BRdata.put(colname.get(3).toString(), "病区:无");
				}
				if (map_databrxx.get("BRCH") != null) {
					BRdata.put(colname.get(4).toString(),
							"床号:" + map_databrxx.get("BRCH") + "床");
				} else {
					BRdata.put(colname.get(4).toString(), "床号:无");
				}
				if (map_databrxx.get("CYPB") != null) {
					BRdata.put(colname.get(5).toString(),
							"判别:" + map_databrxx.get("CYPB") + "");
				} else {
					BRdata.put(colname.get(5).toString(), "判别:无");
				}
				if (map_databrxx.get("HLJB") != null) {
					BRdata.put(colname.get(6).toString(),
							"护理:" + map_databrxx.get("HLJB") + "");
				} else {
					BRdata.put(colname.get(6).toString(), "护理:无");
				}
				l_data.add(BRdata);
				// 护理时间
				StringBuffer hlsjhql = new StringBuffer(
						"select distinct to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss') as JLSJ,b.XMBH as XMBH from ENR_JL01 a,ENR_JL02 b,ZY_BRRY d where a.JLBH=b.JLBH and a.ZYH=d.ZYH  and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')>='"
								+ dateFromHL
								+ "' and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')<='"
								+ dateToHL
								+ "' and d.ZYHM="
								+ Long.parseLong(map_databrxx.get("ZYHM") + ""));
				hlsjhql.append(" and a.JLZT<>9 order by to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')");
				List<Map<String, Object>> list_hlsj = ss
						.createSQLQuery(hlsjhql.toString())
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				// 生成数据
				for (int j = 0; j < list_hlsj.size(); j++) {
					HashMap<String, Object> data = new HashMap<String, Object>();
					// 护理信息
					String hlsj = list_hlsj.get(j).get("JLSJ") + "";
					StringBuffer sql = new StringBuffer(
							"select to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss') as JLSJ,c.PERSONNAME as PERSONNAME,b.XMQZ as XMQZ,b.XMBH as XMBH from ENR_JL01 a,ENR_JL02 b,SYS_PERSONNEL c,ZY_BRRY d where a.JLBH=b.JLBH and a.SXHS=c.PERSONID and a.ZYH=d.ZYH and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')>='"
									+ dateFromHL
									+ "' and to_char(a.JLSJ,'yyyy-mm-dd hh24:mi:ss')='"
									+ hlsj
									+ "' and d.ZYHM="
									+ Long.parseLong(map_databrxx.get("ZYHM")
											+ "") + " and a.JLZT<>9");
					List<Map<String, Object>> list_data = ss
							.createSQLQuery(sql.toString())
							.setResultTransformer(
									Transformers.ALIAS_TO_ENTITY_MAP).list();
					for (int i = 0; i < list_column.size(); i++) {
						Map<String, Object> m1 = list_column.get(i);
						for (int h = 0; h < list_data.size(); h++) {
							Map<String, Object> m2 = list_data.get(h);
							if (m2.get("XMBH").toString()
									.equals(m1.get("XMBH").toString())) {
								data.put("HLSJ", "  " + m2.get("JLSJ"));
								data.put("HLRY", m2.get("PERSONNAME"));
								data.put(list_column.get(i).get("XMBH") + "",
										m2.get("XMQZ") + "");
							}
						}
					}
					l_data.add(data);
					for (int r = 0; r < l_data.size() - 1; r++) {
						for (int y = l_data.size() - 1; y > r; y--) {
							if (l_data.get(y).equals(l_data.get(r))) {
								l_data.remove(y);
							}
						}
					}
				}
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

				if (i > 5 && i < 8) {
					totalWidth += 70;
				} else if (i > 7) {
					totalWidth += 50;
				} else {
					totalWidth += width;
				}
				// 列标题
				JRDesignStaticText staticText = DynaGridPrintUtil
						.getJRDesignStaticText(columnModel[i].getText(),
								fontSize, isColumnHeaderFontBond);
				if (i > 5 && i < 8) {
					staticText.setWidth(70);
				} else if (i > 7) {
					staticText.setWidth(50);
				} else {
					staticText.setWidth(width);
				}
				staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
				if (i > 5 && i < 8) {
					staticText.setX(totalWidth - 70);
				} else if (i > 7) {
					staticText.setX(totalWidth - 50);
				} else {
					staticText.setX(totalWidth - width);
				}
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
				if (i > 5 && i < 8) {
					textField.setWidth(70);
				} else if (i > 7) {
					textField.setWidth(50);
				} else {
					textField.setWidth(width);
				}
				if (i > 5 && i < 8) {
					textField.setX(totalWidth - 70);
				} else if (i > 7) {
					textField.setX(totalWidth - 50);
				} else {
					textField.setX(totalWidth - width);
				}
				textField.getLineBox().getPen().setLineWidth(1f);
				textField.getLineBox().getPen()
						.setLineStyle(LineStyleEnum.SOLID);
				// textField.setStretchWithOverflow(true); //是否换行打印
				detailBand.addElement(textField);
				// 底部页码
				// getPageFooterTextField(pageFooter);
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
			// getPageFooterTextField(pageFooter);
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
	// private static void getPageFooterTextField(JRDesignBand pageFooter) {
	// JRDesignTextField f1 = DynaGridPrintUtil.getJRDesignTextField("",
	// String.class);
	// JRDesignTextField f2 = DynaGridPrintUtil.getJRDesignTextField("",
	// String.class);
	// f1.setEvaluationTime(EvaluationTimeEnum.NOW); // JREvaluationTime .now
	// f2.setEvaluationTime(EvaluationTimeEnum.REPORT); // .report
	// ((JRDesignExpression) f1.getExpression())
	// .setText("\"第\"+$V{PAGE_NUMBER}+\"页\"");
	// ((JRDesignExpression) f2.getExpression())
	// .setText(" \"共\"+$V{PAGE_NUMBER}+\"页\"");
	// f1.setX(320);
	// f2.setX(400);
	// pageFooter.addElement(f1);
	// pageFooter.addElement(f2);
	// }

}
