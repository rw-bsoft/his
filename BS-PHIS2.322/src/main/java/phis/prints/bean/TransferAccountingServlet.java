package phis.prints.bean;

import java.io.IOException;
import java.net.URLDecoder;
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

public class TransferAccountingServlet extends DynamicPrint {
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

	@SuppressWarnings("unchecked")
	public List<JasperPrint> doPrint(Map<String, Object> request,
			Map<String, Object> response) throws PrintException {
		try {
			String strConfig = (String) request.get("config");
			strConfig = URLDecoder.decode(strConfig, "UTF-8");
			HashMap<String, Object> config = JSONUtils.parse(strConfig,
					HashMap.class);
			return doPrintKSHZ(request, response, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打印科室汇总
	 */
	@SuppressWarnings("unchecked")
	protected List<JasperPrint> doPrintKSHZ(Map<String, Object> request,
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
			// 取到机构id
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
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
										+ manaUnitId
										+ "' and a.YWLB='"
										+ YWLB
										+ "' and a.MRBZ=1")
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
				for (int i = 0; i < listKfxx.size(); i++) {
					if (listKfxx.get(i).get("KSDM") != null
							&& listKfxx.get(i).get("KSDM") != "") {
						KFXH = Integer.parseInt(listKfxx.get(i).get("KSDM")
								+ "");
					}
				}

			} // 获取表单参数
			HashMap<String, Object> requestData = (HashMap<String, Object>) config
					.get("requestData");
			String title ="转科汇总报表"; //(String) config.get("title");
			String dateFrom = (String) requestData.get("dateFrom");
			String dateTo = (String) requestData.get("dateTo");
			Long zblb = Long.parseLong(requestData.get("zblb") + "");
			StringBuffer hql = new StringBuffer();
			hql.append(
					" select wl_wzzd.hslb , wl_zk01.zrks ,wl_zk01.zcks ,	sum( wl_zk02.wzje ) wzje,"
							+ " (select officename from sys_office where sys_office.id=wl_zk01.zrks) as zrksmc,"
							+ " (select officename from sys_office where sys_office.id=wl_zk01.zcks) as zcksmc"
							+ " from wl_zk01, wl_zk02 , wl_wzzd where wl_zk01.djxh = wl_zk02.djxh "
							+ " and wl_zk01.kfxh =")
					.append(KFXH)
					.append(" and wl_zk01.zblb=")
					.append(zblb)
					.append(" and	wl_zk01.jzrq >= to_date('")
					.append(dateFrom)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append(" and wl_zk01.jzrq <= to_date('")
					.append(dateTo)
					.append("','yyyy-mm-dd HH24:mi:ss')")
					.append("and wl_zk01.djzt = 2 and wl_zk02.wzxh = wl_wzzd.wzxh ")
					.append(" group by wl_wzzd.hslb , wl_zk01.zcks , wl_zk01.zrks order by wl_zk01.zcks , wl_zk01.zrks");

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
			cm1.setName("ZCKS");
			cm1.setText("转出科室");
			map.put("ZCKS", cm1);
			ColumnModel cm2 = new ColumnModel();
			cm2.setName("ZRKS");
			cm2.setText("转入科室");
			map.put("ZRKS", cm2);
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
					data.put("ZCKS", map_data.get("ZCKSMC"));
					data.put("ZRKS", map_data.get("ZRKSMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.4f", map_data.get("WZJE")));
					l_data.add(data);
					continue;
				}
				// 如果l_data中有元素
				if (l_data.size() > 0) {
					// 遍历报表的数据集合：l_data，如果已存在于l_data，则全部累加
					for (int j = 0; j < l_data.size(); j++) {
						Map<String, Object> d = l_data.get(j);
						// 如果存在
						if (d.get("ZCKS").equals(map_data.get("ZCKS"))
								&& d.get("ZRKS").equals(map_data.get("ZRKS"))) {
							isExist = true;
							existIndex = j;
							break;
						}
					}
				}
				if (isExist) {
					HashMap<String, Object> existData = l_data.get(existIndex);
					existData.put(map_data.get("HSLB") + "",
							String.format("%1$.4f", map_data.get("WZJE")));
				} else {
					HashMap<String, Object> data = new HashMap<String, Object>();
					data.put("TOTAL", i + 1);
					data.put("ZCKS", map_data.get("ZCKSMC"));
					data.put("ZRKS", map_data.get("ZRKSMC"));
					data.put(map_data.get("HSLB") + "",
							String.format("%1$.4f", map_data.get("WZJE")));
					l_data.add(data);
				}
			}

			for (int i = 0; i < l_data.size(); i++) {
				HashMap<String, Object> data = l_data.get(i);
				Set<String> set = data.keySet();
				Double columnTotal = 0D;
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String name = it.next();
					if (!name.equals("TOTAL") && !name.equals("ZCKS")
							&& !name.equals("ZRKS")
							&& !name.equals("ColumnTOTAL")) {
						columnTotal += Double.parseDouble(data.get(name) + "");
					}
				}
				data.put("ColumnTOTAL", String.format("%1$.4f", columnTotal));
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
						String.format("%1$.4f", value));
				dataTotal.put("ColumnTOTAL", String.format("%1$.4f", allTotal));
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
