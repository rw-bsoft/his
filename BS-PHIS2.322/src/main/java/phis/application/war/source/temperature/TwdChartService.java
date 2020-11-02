package phis.application.war.source.temperature;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.CollectionUtils;

import phis.application.war.source.temperature.drawshape.Customerbean;
import phis.application.war.source.temperature.drawshape.LineShapeContext;
import phis.application.war.source.temperature.drawshape.PointShapeContext;
import phis.application.war.source.temperature.drawshape.ShapeBean;
import phis.application.war.source.temperature.drawshape.ShapeContext;
import phis.application.war.source.temperature.drawshape.ShapeLineContext;
import phis.application.war.source.temperature.drawshape.TemCalUtils;
import phis.application.war.source.temperature.drawshape.factory.CharFactories;
import phis.application.war.source.temperature.drawshape.factory.TemperatureHollow;
import phis.application.war.source.temperature.drawshape.factory.TemperatureLine;
import phis.application.war.source.temperature.drawshape.factory.TemperaturePoint;
import phis.application.war.source.temperature.drawshape.factory.TemperatureTWSolidLine;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.IdUtils;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.mvc.controller.util.UserRoleTokenUtils;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;

/** 体温单实现类 */
/**
 * @ClassName: TwdChartService
 * @Description: TODO()
 * @author zhoufeng
 * @date 2013-6-21 下午02:47:58
 * 
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class TwdChartService {

	private int top_length = 3; // 顶端的高度
	private int top_rows = 10; // 顶部的行数
	private double top_row_height = top_length / (double) top_rows; // 顶部每行的高度

	private int bottom_length = 3; // 底部的高度
	private int bottom_rows = 10;// 底部的行数
	private double bottom_row_height = bottom_length / (double) bottom_rows; // 底部每行的高度

	private int grid_left = 6; // 左Y轴格子数
	private int grid_right = 0; // 右Y轴格子数

	private int x_unit = 6; // 多少个小格子组成一个大格子
	private double grid_row_height = 0.2; // 数据区每个格子的高度
	private double grid_col_width = 1.0;
	private int y_grid_length = 8; // y轴所有格子的长度
	private int x_grid_length = 42; // x轴所有格子的长度
	private int grid_rows = (int) Math.round(y_grid_length / grid_row_height); // 中间格子的行数

	private int start_temperature = 34; // 起始的体温 从底部高度开始算起
	private int start_pulse = 20; // 起始的脉搏 从底部高度开始算起

	private int x_length = grid_left + x_grid_length + grid_right; // x轴的长度
	private int y_length = top_length + y_grid_length + bottom_length; // y轴的长度

	private String tempType = "2";

	Font font = new Font("SansSerif", Font.PLAIN, 12);
	private double round_size = 8d;
	private Context ctx = null;
	private ChartProcessor chart;
	private Map map;
	
	public final static char[] upper = "零一二三四五六七八九十".toCharArray();

	public void initAllData(ChartProcessor chart, Map map)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		this.chart = chart;
		this.map = map;
		loadMapData(chart, map); // 加载业务数据
		initTopChart(chart, map);
		initGridChart(chart);
		initBottomChart(chart, map);
		initGridDynamicData(chart, map);
		initChart(chart);
		// destroy();// 销毁session
	}

	/**
	 * 加载业务数据
	 * 
	 * @param chart
	 * @param map
	 */
	public void loadMapData(ChartProcessor chart, Map map) {
		Long zhy = Long.parseLong(chart.getReq().getParameter("zyh"));
		String tempType = chart.getReq().getParameter("tempType");
		if (tempType != null) {
			this.tempType = tempType;
		}
		Integer currentWeek = Integer.parseInt(chart.getReq().getParameter(
				"currentWeek"));
		List<String> dataList = new ArrayList();
		map.put("dateList", dataList);
		if (ctx == null) {
			ctx = ContextUtils.getContext();
		}
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		if (ss == null) {
			SessionFactory sf = AppContextHolder.getBean(
					AppContextHolder.DEFAULT_SESSION_FACTORY,
					SessionFactory.class);
			ss = sf.openSession();
		}
		BaseDAO dao = new BaseDAO(ctx, ss);
		try {
			// 标题数据
			Map<String, Object> brxx = dao
					.doLoad(BSPHISEntryNames.ZY_BRRY, zhy);
			map.put("name", brxx.get("BRXM"));
			map.put("age", brxx.get("RYNL"));
			map.put("sex",
					DictionaryController.instance()
							.get("phis.dictionary.gender")
							.getText(brxx.get("BRXB").toString()));
			map.put("indate", BSHISUtil.toString((Date) brxx.get("RYRQ"),
					Constants.DEFAULT_SHORT_DATE_FORMAT));
			map.put("sectionAreaName",
					DictionaryController.instance()
							.get("phis.dictionary.department")
							.getText(brxx.get("BRKS").toString()));
			map.put("badNo", brxx.get("BRCH"));
			map.put("hisno", brxx.get("ZYHM"));
			map.put("manageName",
					DictionaryController.instance().get("phis.@manageUnit")
							.getText(brxx.get("JGID").toString()));

			map.put("brxx", brxx);

			// 日期
			Date ryrq = (Date) brxx.get("RYRQ");
			Date cyrq = (Date) brxx.get("CYRQ");
			if (cyrq == null) {
				cyrq = new Date();
			}
			int weeks = BSHISUtil.getWeeksForTem(ryrq, cyrq) - 1;
			int dateStart = (currentWeek) * 7;
			int dateEnd = dateStart + 6;
			if (currentWeek == weeks) {
				dateEnd = dateStart
						+ BSHISUtil.getPeriod(
								BSHISUtil.getDateAfter(ryrq, dateStart), cyrq);
			}

			for (int i = dateStart; i <= dateEnd; i++) {
				String date = BSHISUtil.toString(BSHISUtil
						.getDateAfter(ryrq, i));
				dataList.add(date);
			}

			// 中间数据
			List<PTempInfo> tempInfoList = new ArrayList<PTempInfo>();
			Map<String, Object> parameters = new HashMap<String, Object>();
			// parameters.put("endDate", BSHISUtil.getDateAfter(beginDate, 7));
			for (String date : dataList) {
				parameters.put("ZYH", zhy);
				parameters.put("CJSJ", date);

				PTempInfo ptemp = new PTempInfo();
				ptemp.setInspectionDate(BSHISUtil.toDate(date));
				getDetailData(dao, zhy, date, brxx, ptemp);

				tempInfoList.add(ptemp);
			}
			map.put("tempInfoList", tempInfoList);

			// 底下数据
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		} finally {
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
			ContextUtils.clear();
		}

	}

	/**
	 * @Title: getDetailData
	 * @Description: TODO(过滤特定时间段重复的记录,取离时间段始点最近的记录)
	 * @param @param dao
	 * @param @param zhy
	 * @param @param date
	 * @param @return
	 * @param @throws PersistentDataOperationException 设定文件
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	private void getDetailData(BaseDAO dao, Long zhy, String date, Map<String, Object> brxx,
			PTempInfo ptemp) throws PersistentDataOperationException {
		// int xmhs[] = { 1, 2, 3, 4, 5, 6, 7, 8, 31, 32, 33, 108, 145 };
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> fcList = new ArrayList<Map<String, Object>>();// 复测数据集
		List<Map<String, Object>> list = dao
				.doSqlQuery(
						"select XMH as XMH, to_char(CJSJ,'yyyy-MM-dd HH24:mi:ss') as CJSJ,to_char(CJSJ,'HH24') as HOUR,TZNR,XMXB,XMH,FCBZ,BZXX from BQ_SMTZ where ZYH="
								+ zhy
								+ " and to_char(CJSJ,'yyyy-MM-dd')='"
								+ date
								+ "' and XMH in (1, 2, 3, 4, 5, 6, 7, 8,11, 31, 32, 33, 108, 145) order by CJSJ,CJH",
						null);
		Map<String, Map<String, Object>> retMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> retfcMap = new HashMap<String, Map<String, Object>>();

		if(brxx!=null){
			Date ryDate = (Date) brxx.get("RYRQ");
			Date cyDate = (Date) brxx.get("CYRQ");
			if(ryDate!=null && BSHISUtil.dateCompare(
					ryDate,BSHISUtil.toDate(date))==0){
				list.add(getBzxx("入院",ryDate));
			}
			if(cyDate!=null && BSHISUtil.dateCompare(
					cyDate,BSHISUtil.toDate(date))==0){
				list.add(getBzxx("出院",cyDate));
			}
		}
		
		for (Map<String, Object> m : list) {
			int xmh = Integer.parseInt(m.get("XMH").toString());
			if (xmh == 1) {// 过滤体温的记录
				if (MapUtils.getInteger(m, "FCBZ") != 1) {
					Integer hour = Integer.parseInt(m.get("HOUR").toString());
					Integer index = hour / 4;
					if (!retMap.containsKey(index + "" + xmh)
							|| BSHISUtil.toDate(
									retMap.get(index + "" + xmh).get("CJSJ").toString())
									.getTime() >= BSHISUtil.toDate(
									m.get("CJSJ").toString()).getTime()) {
						retMap.put(index + "" + xmh, m);
					}

				} else {
					// 复测数组
					Integer hour = Integer.parseInt(m.get("HOUR").toString());
					Integer index = hour / 4;
					if (!retfcMap.containsKey(index + "" + xmh)
							|| BSHISUtil.toDate(
									retfcMap.get(index + "" + xmh).get("CJSJ")
											.toString()).getTime() >= BSHISUtil
									.toDate(m.get("CJSJ").toString()).getTime()) {
						retfcMap.put(index + "" + xmh, m);
					}
				}
			} else {
				if (xmh == 2 || xmh == 3 || xmh == 4 || xmh == 11) {// 2脉搏 3呼吸 4心率 11备注信息
					Integer hour = Integer.parseInt(m.get("HOUR").toString());
					Integer index = hour / 4;
					if (!retMap.containsKey(index + "" + xmh)
							|| BSHISUtil.toDate(
									retMap.get(index + "" + xmh).get("CJSJ")
											.toString()).getTime() >= BSHISUtil
									.toDate(m.get("CJSJ").toString()).getTime()) {
						retMap.put(index + "" + xmh, m);
					}
				} else if (xmh == 5 || xmh == 6) { // 5收缩压 6舒张压
					Integer hour = Integer.parseInt(m.get("HOUR").toString());
					Integer index = hour / 12;
					// if (!retMap.containsKey(index + xmh)
					// || BSHISUtil.toDate(
					// retMap.get(index + xmh).get("CJSJ").toString())
					// .getTime() > BSHISUtil.toDate(
					// m.get("CJSJ").toString()).getTime()) {
					// retMap.put(index + xmh, m);
					// }
					if (xmh == 5) {
						if (index == 0) {
							String boold1 = (ptemp.getBloodPressure1() == null ? ""
									: ptemp.getBloodPressure1())
									+ m.get("TZNR").toString();
							ptemp.setBloodPressure1(boold1);
						}
						if (index == 1) {
							String boold2 = (ptemp.getBloodPressure2() == null ? ""
									: ptemp.getBloodPressure2())
									+ m.get("TZNR").toString();
							ptemp.setBloodPressure2(boold2);
						}
					} else if (xmh == 6) {
						if (index == 0) {
							String boold1 = (ptemp.getBloodPressure1() == null ? ""
									: ptemp.getBloodPressure1())
									+ "/" + m.get("TZNR").toString();
							ptemp.setBloodPressure1(boold1);
						}
						if (index == 1) {
							String boold2 = (ptemp.getBloodPressure2() == null ? ""
									: ptemp.getBloodPressure2())
									+ "/" + m.get("TZNR").toString();
							ptemp.setBloodPressure2(boold2);
						}
					}
				} else {// 7体重 8身高
					if (xmh == 7) {// 体重
						ptemp.setWeight(m.get("TZNR") + "");
					} else if (xmh == 8) {// 身高
						ptemp.setHeight(m.get("TZNR") + "");
					} else if (xmh == 31) {// 大便
						ptemp.setPoopCount(m.get("TZNR") + "");
					} else if (xmh == 32) {// 小便
						ptemp.setUrineVolume(m.get("TZNR") + "");
					} else if (xmh == 33) {// 其他出量
						ptemp.setOutput(m.get("TZNR") + "");
					} else if (xmh == 108) {// 皮试
						ptemp.setSkinTest(m.get("TZNR") + "");
					} else if (xmh == 145) {// 其他入量
						ptemp.setIntake(m.get("TZNR") + "");
					}
				}
			}

		}
		fcList.addAll(retfcMap.values());
		retList.addAll(retMap.values());
		ptemp.setDetailInfo(retList);
		ptemp.setFcList(fcList);

	}
	
	/**
	 * 获取备注信息垂直文字
	 * @param title
	 * @param cdate
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Map<String, Object> getBzxx(String title, Date cdate){
		int cyHour = cdate.getHours();
		int cyMinute = cdate.getMinutes();
		Map<String, Object> bzxx = new HashMap<String, Object>();
		bzxx.put("XMH", "11");
		bzxx.put("CJSJ", BSHISUtil.toString(cdate,Constants.DEFAULT_DATE_FORMAT));
		bzxx.put("HOUR",cdate.getHours());
		bzxx.put("TZNR", title);
		bzxx.put("XMXB", "");//项目下标（体温类型等）
		bzxx.put("FCBZ", 0);//复测标志（0：首测  1：复测）
		StringBuffer cyStr = new StringBuffer();
		if(cyHour <= 10) {
			cyStr.append(upper[cyHour]+"时");
        } else if(cyHour < 20){
        	cyStr.append("十").append(upper[cyHour%10]+"时");
        } else{
        	cyStr.append(upper[cyHour/10]).append("十");
        	if(cyHour%10 != 0){
        		cyStr.append(upper[cyHour%10]);
        	}
        	cyStr.append("时");
        }
		if(cyMinute <= 10) {
			cyStr.append(upper[cyMinute]+"分");
        } else if(cyMinute < 20){
        	cyStr.append("十").append(upper[cyMinute%10]+"分");
        } else {
        	cyStr.append(upper[cyMinute/10]).append("十");
        	if(cyMinute%10 != 0){
        		cyStr.append(upper[cyMinute%10]);
        	}
        	cyStr.append("分");
        }
		bzxx.put("BZXX", cyStr);
		return bzxx;
	}

	// public Context initContext(HttpServletRequest req) {
	// Context ctx = Dispatcher.createContext(req);
	// UserRoleToken user = Dispatcher.getUser(req);
	// if (user != null) {
	// Context userCtx = new UserContext(user);
	// ctx.putCtx("user", userCtx);
	// }
	// if (!ctx.has(Context.DB_SESSION)) {
	// SessionFactory sf = (SessionFactory) AppContextHolder.get()
	// .getBean("mySessionFactory");
	// ctx.put(Context.DB_SESSION, sf.openSession());
	// }
	// return ctx;
	// }

	/**
	 * 初始化坐标轴、边框
	 * 
	 * @param chart
	 */
	private void initChart(ChartProcessor chart) {
		// 初始化各坐标轴
		chart.setXRange(0, x_length);
		chart.setXUnit(1);
		chart.setXVisible(false);
		chart.setBgHorizontalLineColor(Color.white);
		chart.setBgVerticalLineColor(Color.white);
		chart.setYUnit(ChartProcessor.AXIS_LEFT, 1);
		chart.setYRange(ChartProcessor.AXIS_LEFT, 0, y_length);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_left", 0, y_length);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "basic_left", false);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "basic_left", false);
		chart.setYVisible(ChartProcessor.AXIS_LEFT, false);
		// 四条边框
		// 上
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_frame_line_top", 0,
				y_length);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_frame_line_top",
				x_length, y_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_frame_line_top", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_frame_line_top",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_frame_line_top",
				ChartShape.LINE_WIDE);

		// 住院天数
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_top", 0,
				bottom_length + y_grid_length + top_row_height * 5);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_top", x_length,
				bottom_length + y_grid_length + top_row_height * 5);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "basic_line_top",
				false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_top",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_top",
				ChartShape.LINE_NORMAL);

		// 下
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_frame_line_bottom", 0,
				0.01);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_frame_line_bottom",
				x_length, 0.01);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_frame_line_bottom", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_frame_line_bottom",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_frame_line_bottom",
				ChartShape.LINE_NORMAL);
		// 左
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line", 0, 0);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line", 0, y_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "basic_y_line",
				false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_y_line",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_y_line",
				ChartShape.LINE_WIDE);

		// 左2
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line_sec", grid_left,
				0);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line_sec", grid_left,
				y_length - top_row_height * 5);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "basic_y_line_sec",
				false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_y_line_sec",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_y_line_sec",
				ChartShape.LINE_NORMAL);
		// 右
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line_right2",
				x_length - 0.0005, 0);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_y_line_right2",
				x_length - 0.0005, y_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_y_line_right2", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_y_line_right2",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_y_line_right2",
				ChartShape.LINE_NORMAL);
		// 呼吸
		chart.addData(ChartProcessor.AXIS_LEFT, "bottom_horizatal_line", 0,
				bottom_length);
		chart.addData(ChartProcessor.AXIS_LEFT, "bottom_horizatal_line",
				x_length - grid_right, bottom_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"bottom_horizatal_line", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "bottom_horizatal_line",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "bottom_horizatal_line",
				ChartShape.LINE_NORMAL);
		// 时间边框
		chart.addData(ChartProcessor.AXIS_LEFT, "top_horizatal_line", 0,
				y_length - top_length);
		chart.addData(ChartProcessor.AXIS_LEFT, "top_horizatal_line", x_length
				- grid_right, y_length - top_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"top_horizatal_line", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "top_horizatal_line",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "top_horizatal_line",
				ChartShape.LINE_NORMAL);
		// 初始化脉搏、体温坐标轴
		for (int i = 0; i < y_grid_length; i++) {
			Font font = new Font("SansSerif", Font.PLAIN, 12);
			String value_left = (start_temperature + i + 1) + "";
			String value_left_sec = (start_pulse + (i + 1) * 20) + "";
			double y = bottom_length + i + 0.8;
			if (i == y_grid_length - 1) {
				chart.addBackgroundValue("体温", grid_left / 6 * 5, y, font); //
				chart.addBackgroundValue("(℃)", grid_left / 6 * 5, y - 0.2,
						font); //
				chart.addBackgroundValue(value_left, grid_left / 6 * 5,
						y - 0.4, font); //
				chart.addBackgroundValue("脉搏", grid_left / 2, y, font,
						Color.RED);
				chart.addBackgroundValue("(次/分)", grid_left / 2, y - 0.2, font,
						Color.RED);
				chart.addBackgroundValue(value_left_sec, grid_left / 2,
						y - 0.4, font, Color.RED);
			} else {
				chart.addBackgroundValue(value_left, grid_left / 6 * 5,
						y + 0.2, font); //
				chart.addBackgroundValue(value_left_sec, grid_left / 2,
						y + 0.2, font, Color.RED);
			}
		}

		// 初始化脉搏类型图
		double x = 0.8;
		double y = bottom_length + 4;
		chart.addBackgroundValue("口温", x, y, font); //

		chart.addData(ChartProcessor.AXIS_LEFT, "example_kouwen", x + 1,
				y + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_kouwen",
				Color.BLUE);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_kouwen",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_kouwen", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_kouwen",
				ChartShape.getShape(ChartShape.ROUND, round_size, round_size));

		chart.addBackgroundValue("腋温", x, y - 0.2, font); //

		chart.addData(ChartProcessor.AXIS_LEFT, "example_yewen_1", x + 1,
				y - 0.2 + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_yewen_1",
				Color.BLUE);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_yewen_1",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_yewen_1", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_yewen_1",
				ChartShape.getLine(ChartShape.LINE, new Point2D.Double(-3, -3),
						new Point2D.Double(3, 3)));
		chart.addData(ChartProcessor.AXIS_LEFT, "example_yewen_2", x + 1,
				y - 0.2 + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_yewen_2",
				Color.BLUE);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_yewen_2",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_yewen_2", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_yewen_2",
				ChartShape.getLine(ChartShape.LINE, new Point2D.Double(-3, 3),
						new Point2D.Double(3, -3)));

		chart.addBackgroundValue("肛温", x, y - 0.4, font); //

		chart.addData(ChartProcessor.AXIS_LEFT, "example_gongwen", x + 1,
				y - 0.4 + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_gongwen",
				Color.BLUE);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_gongwen",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_gongwen", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_gongwen",
				ChartShape.getShape(ChartShape.ROUND, round_size, round_size));
		chart.setLineShapeFilledColor(ChartProcessor.AXIS_LEFT,
				"example_gongwen", Color.WHITE);

		chart.addBackgroundValue("脉搏", x, y - 0.6, font); //

		chart.addData(ChartProcessor.AXIS_LEFT, "example_maibo", x + 1,
				y - 0.6 + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_maibo", Color.RED);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_maibo",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_maibo", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_maibo",
				ChartShape.getShape(ChartShape.ROUND, round_size, round_size));

		chart.addBackgroundValue("心率", x, y - 0.8, font); //

		chart.addData(ChartProcessor.AXIS_LEFT, "example_xinlv", x + 1,
				y - 0.8 + 0.05);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "example_xinlv", Color.RED);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "example_xinlv",
				true);
		chart.setLineVisible(ChartProcessor.AXIS_LEFT, "example_xinlv", false);
		chart.setLineShape(ChartProcessor.AXIS_LEFT, "example_xinlv",
				ChartShape.getShape(ChartShape.ROUND, round_size, round_size));
		chart.setLineShapeFilledColor(ChartProcessor.AXIS_LEFT,
				"example_xinlv", Color.WHITE);

		// 时间 至 顶部 添加纵线 呼吸 至 底部 添加纵线
		// 纵向格子
		for (int i = 0; i <= x_grid_length; i++) {
			float chartShape = ChartShape.LINE_THIN;
			double temp_top_height = y_length - top_length + top_row_height * 1;
			double temp_bottom_height = bottom_length - bottom_row_height * 1.5;
			if (i % 6 == 0) {
				chartShape = ChartShape.LINE_WIDE;
				temp_top_height = y_length - top_length + top_row_height * 5;
				temp_bottom_height = 0;
			}
			String line_name = "line_time2top_vertical_sec_" + i;
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left + i,
					y_length - top_length);
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left + i,
					temp_top_height);
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, line_name,
					false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT, line_name, Color.BLACK);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT, line_name, chartShape);

			String line_name1 = "line_breather2bottom_vertical_sec_" + i;
			chart.addData(ChartProcessor.AXIS_LEFT, line_name1, grid_left + i,
					bottom_length);
			chart.addData(ChartProcessor.AXIS_LEFT, line_name1, grid_left + i,
					temp_bottom_height);
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, line_name1,
					false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT, line_name1,
					Color.BLACK);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT, line_name1, chartShape);
		}

	}

	/*****
	 * 顶部标题初始化
	 */
	private void initTopChart(ChartProcessor chart, Map map) {
		double x_start = 3; // 起始位置
		double x_step = (x_length) / 8;
		double y = 12.7;
		chart.addBackgroundValue("姓 名：" + map.get("name"), x_start, y, font);
		chart.addBackgroundValue("年 龄：" + map.get("age"), x_start + x_step, y,
				font);
		chart.addBackgroundValue("性 别：" + map.get("sex"), x_start + x_step
				* 1.8, y, font);

		chart.addBackgroundValue("科 别：" + map.get("sectionAreaName"), x_start
				+ x_step * 2.8, y, font);
		chart.addBackgroundValue("床 号：" + map.get("badNo"), x_start + x_step
				* 3.8, y, font);
		chart.addBackgroundValue("入 院 日 期：" + map.get("indate"), x_start
				+ x_step * 5, y, font);
		chart.addBackgroundValue("住院病历号：" + map.get("hisno"), x_start + x_step
				* 6.7, y, font);
		chart.addBackgroundValue("体  温  单", x_length / 2, bottom_length
				+ y_grid_length + top_row_height * 6 + 0.2, new Font("楷体",
				Font.BOLD, 26));
		chart.addBackgroundValue(map.get("manageName") + "", x_length / 2,
				bottom_length + y_grid_length + top_row_height * 8 + 0.1,
				new Font("楷体", Font.PLAIN, 20));

		/*****
		 * 顶部数据初始化
		 **/
		chart.addBackgroundValue("日            期", grid_left / 2, bottom_length
				+ y_grid_length + top_row_height * 4 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_date", 0,
				bottom_length + y_grid_length + top_row_height * 4);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_date",
				x_length - grid_right, bottom_length + y_grid_length
						+ top_row_height * 4);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_time_date", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_time_date",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_time_date",
				ChartShape.LINE_THIN);

		chart.addBackgroundValue("住  院  天  数", grid_left / 2, bottom_length
				+ y_grid_length + top_row_height * 3 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_in_day", 0,
				bottom_length + y_grid_length + top_row_height * 3);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_in_day",
				x_length - grid_right, bottom_length + y_grid_length
						+ top_row_height * 3);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_time_in_day", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_time_in_day",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_time_in_day",
				ChartShape.LINE_THIN);
		chart.addBackgroundValue("手 术 后 天 数", grid_left / 2, bottom_length
				+ y_grid_length + top_row_height * 2 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_ops_day", 0,
				bottom_length + y_grid_length + top_row_height * 2);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_ops_day",
				x_length - grid_right, bottom_length + y_grid_length
						+ top_row_height * 2);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_time_ops_day", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_time_ops_day",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_time_ops_day",
				ChartShape.LINE_THIN);
		chart.addBackgroundValue("产 后 天 数", grid_left / 2, bottom_length
				+ y_grid_length + top_row_height * 1 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_op_day", 0,
				bottom_length + y_grid_length + top_row_height * 1);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_time_op_day",
				x_length - grid_right, bottom_length + y_grid_length
						+ top_row_height * 1);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_time_op_day", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_time_op_day",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_time_op_day",
				ChartShape.LINE_THIN);
		// 时间节点2 ,6,10,14,18,22
		for (int i = 0; i < x_grid_length; i++) {
			Integer[] hours = { 2, 6, 10, 14, 18, 22 }; // 时间段
			int beginHour = hours[i < x_unit ? i : i % x_unit];
			Color hourColor = null;
			if (beginHour == 2 || beginHour == 6 || beginHour == 22)
				hourColor = Color.RED;
			else
				hourColor = Color.BLACK;

			chart.addBackgroundValue(beginHour + "", grid_left + i + 0.5,
					bottom_length + y_grid_length + 0.1, font, hourColor);
		}

		chart.addBackgroundValue("时	          间", grid_left / 2, bottom_length
				+ y_grid_length + 0.1, font, Color.BLACK);
		/*
		 * chart.addBackgroundValue("脉搏(次/分)", grid_left / 4 + 0.3,
		 * bottom_length + y_grid_length + 0.1, font, Color.BLACK);
		 */

		/**
		 * 体温和脉搏之间的纵轴
		 */
		chart.addData(ChartProcessor.AXIS_LEFT,
				"vertical_line_between_temp_pulse", grid_left * 3 / 4,
				bottom_length);
		chart.addData(ChartProcessor.AXIS_LEFT,
				"vertical_line_between_temp_pulse", grid_left * 3 / 4,
				bottom_length + y_grid_length);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"vertical_line_between_temp_pulse", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"vertical_line_between_temp_pulse", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"vertical_line_between_temp_pulse", ChartShape.LINE_THIN);

		/******************************
		 * 顶部动态数据展示 ****************************
		 */

		/**
		 * 日期信息显示 例子：2012-07-31、08-01、02、03、04、05
		 */
		Integer currentWeek = Integer.parseInt(chart.getReq().getParameter(
				"currentWeek"));
		List<String> dateList = (List<String>) map.get("dateList"); // 日期集合
		Date d = BSHISUtil.toDate(dateList.get(0)); //格式化第一天
		//获取第一天的年月
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		int lastYear = cal.get(Calendar.YEAR);
		int lastMouth = cal.get(Calendar.MONTH);
		String formatDate = "";
		SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf_2 = new SimpleDateFormat("MM-dd");
		SimpleDateFormat sdf_3 = new SimpleDateFormat("dd");
		for (int i = 0; i < 7; i++) {
			if(i == 0) { // 第一天
				if(currentWeek == 0) {//首周
					formatDate = sdf_1.format(cal.getTime());
				} else {//非首周
					formatDate = sdf_2.format(cal.getTime()); //考虑跨年情况
				}
			} else {
				//是否一个月的第一天
				if(cal.getTime().equals(BSHISUtil.getFirstDayOfMonth(cal.getTime()))) {
					if(cal.get(Calendar.MONTH) == 0) { // 一年的第一天
						formatDate = sdf_1.format(cal.getTime()); 
					}else {
						formatDate = sdf_2.format(cal.getTime()); 
					}
				} else {
					formatDate = sdf_3.format(cal.getTime()); 
				}
			}
			chart.addBackgroundValue(formatDate, grid_left * (i + 1)
					+ x_unit / 2, bottom_length + y_grid_length
					+ top_row_height * 4 + 0.1, font); // 日期
			cal.add(Calendar.DATE, 1);
		}
		
		// 住院天数
		Date ryrq = (Date) ((Map<String, Object>) map.get("brxx")).get("RYRQ");
		ryrq = BSHISUtil.toDate(BSHISUtil.toString(ryrq));
		if (dateList != null && dateList.size() > 0) {
			for (int i = 0; i < dateList.size(); i++) {
				int differDays = BSHISUtil.getDifferDays(
						BSHISUtil.toDate(dateList.get(i)), ryrq) + 1;
				chart.addBackgroundValue(String.valueOf(differDays), grid_left
						* (i + 1) + x_unit / 2, bottom_length + y_grid_length
						+ top_row_height * 3 + 0.1, font); // 日期
			}
		}

	}

	/***
	 * 中间格子初始化
	 */
	private void initGridChart(ChartProcessor chart) {
		// 纵向格子
		for (int i = 0; i <= x_grid_length; i++) {
			float chartShape = ChartShape.LINE_THIN;
			Color lineColor = Color.BLACK;
			if (i % 6 == 0) {
				chartShape = ChartShape.LINE_WIDE;
				lineColor = Color.RED;
			}
			String line_name = "basic_line_ruliang_vertical_sec_" + i;
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left + i,
					bottom_length);
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left + i,
					y_length - top_length);
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, line_name,
					false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT, line_name, lineColor);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT, line_name, chartShape);
		}

		// 横向格子
		for (int i = 0; i < grid_rows; i++) {
			String line_name = "grid_horizontal_line" + i;
			float horizontalShape = ChartShape.LINE_THIN;
			Color horizontalColor = Color.BLACK;
			if (i % 5 == 0) {
				horizontalShape = ChartShape.LINE_WIDE;
			}
			if (i == 15) // 37度对应线为红色
			{
				horizontalColor = Color.RED;
			}
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left,
					bottom_length + grid_row_height * i);
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, x_length
					- grid_right, bottom_length + grid_row_height * i);
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, line_name,
					false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT, line_name,
					horizontalColor);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT, line_name,
					horizontalShape);
		}
	}

	/**
	 * 底部数据初始化
	 */
	public void initBottomChart(ChartProcessor chart, Map map) {
		// 底部数据初始化
		chart.addBackgroundValue("呼 吸 (次/分)", grid_left / 2, bottom_length
				- bottom_row_height, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_huxi", 0,
				bottom_length - bottom_row_height * 1.5);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_huxi", x_length
				- grid_right, bottom_length - bottom_row_height * 1.5);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, "basic_line_huxi",
				false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_huxi",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_huxi",
				ChartShape.LINE_THIN);

		chart.addBackgroundValue("  血　压 mmHg", grid_left / 2, bottom_length
				- bottom_row_height * 2.5, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_xueya", 0,
				bottom_length - bottom_row_height * 3);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_xueya",
				x_length - grid_right, bottom_length - bottom_row_height * 3);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_xueya", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT, "basic_line_bottom_xueya",
				Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT, "basic_line_bottom_xueya",
				ChartShape.LINE_THIN);

		chart.addBackgroundValue("入    量    ml", grid_left / 2, bottom_length
				- bottom_row_height * 4 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_ruliang", 0,
				bottom_length - bottom_row_height * 4);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_ruliang",
				x_length - grid_right, bottom_length - bottom_row_height * 4);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_ruliang", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_ruliang", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_ruliang", ChartShape.LINE_THIN);
		chart.addBackgroundValue("出    量    ml", grid_left / 2, bottom_length
				- bottom_row_height * 5 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_chuliang",
				0, bottom_length - bottom_row_height * 5);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_chuliang",
				x_length - grid_right, bottom_length - bottom_row_height * 5);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_chuliang", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_chuliang", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_chuliang", ChartShape.LINE_THIN);

		chart.addBackgroundValue(" 大  便  (次/日) ", grid_left / 2, bottom_length
				- bottom_row_height * 6 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_dabiancishu", 0, bottom_length
						- bottom_row_height * 6);
		chart.addData(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_dabiancishu", x_length - grid_right,
				bottom_length - bottom_row_height * 6);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_dabiancishu", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_dabiancishu", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_dabiancishu", ChartShape.LINE_THIN);

		chart.addBackgroundValue("体　  重    kg", grid_left / 2, bottom_length
				- bottom_row_height * 7 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_tizhong", 0,
				bottom_length - bottom_row_height * 7);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_tizhong",
				x_length - grid_right, bottom_length - bottom_row_height * 7);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_tizhong", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_tizhong", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_tizhong", ChartShape.LINE_THIN);

		chart.addBackgroundValue("身    高　  cm", grid_left / 2, bottom_length
				- bottom_row_height * 8 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_shenggao",
				0, bottom_length - bottom_row_height * 8);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_shenggao",
				x_length - grid_right, bottom_length - bottom_row_height * 8);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_shenggao", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_shenggao", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_shenggao", ChartShape.LINE_THIN);

		chart.addBackgroundValue(" 尿 　 量    ml", grid_left / 2, bottom_length
				- bottom_row_height * 9 + 0.1, font);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_niaoliang",
				0, bottom_length - bottom_row_height * 9);
		chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_niaoliang",
				x_length - grid_right, bottom_length - bottom_row_height * 9);
		chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_niaoliang", false);
		chart.setLineColor(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_niaoliang", Color.BLACK);
		chart.setLineWidth(ChartProcessor.AXIS_LEFT,
				"basic_line_bottom_niaoliang", ChartShape.LINE_THIN);

		for (int i = 0; i < 7; i++) {
			String line_name = "basic_line__bottom_xueya_vertical" + i;
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left
					+ x_unit / 2 + x_unit * (i), bottom_length
					- bottom_row_height * 1.5);
			chart.addData(ChartProcessor.AXIS_LEFT, line_name, grid_left
					+ x_unit / 2 + x_unit * (i), bottom_length
					- bottom_row_height * 3);
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT, line_name,
					false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT, line_name, Color.black);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT, line_name,
					ChartShape.LINE_THIN);
		}

		for (int i = 1; i < 5; i++) {
			chart.addBackgroundValue("皮     试 ", grid_left / 2, bottom_length
					- bottom_row_height * (9 + i) + 0.1, font);
			chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_beizhu"
					+ i, 0, bottom_length - bottom_row_height * (9 + i));
			chart.addData(ChartProcessor.AXIS_LEFT, "basic_line_bottom_beizhu"
					+ i, x_length, bottom_length - bottom_row_height * (9 + i));
			chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,
					"basic_line_bottom_beizhu" + i, false);
			chart.setLineColor(ChartProcessor.AXIS_LEFT,
					"basic_line_bottom_beizhu" + i, Color.BLACK);
			chart.setLineWidth(ChartProcessor.AXIS_LEFT,
					"basic_line_bottom_beizhu" + i, ChartShape.LINE_THIN);
		}

		/******************************
		 * 底部动态数据展示 ****************************
		 */

		// begin
		List<String> dateList = (List<String>) map.get("dateList"); // 日期集合
		List<PTempInfo> tempInfoList = (List<PTempInfo>) map
				.get("tempInfoList"); // 体温单集合
		for (int i = 0; i < dateList.size(); i++) {
			PTempInfo resultInfo = null;
			if (!CollectionUtils.isEmpty(tempInfoList)) {
				for (PTempInfo tempInfo : tempInfoList) {
					if (dateList.get(i).equals(
							BSHISUtil.toString(tempInfo.getInspectionDate()))) {
						resultInfo = tempInfo;
						break;
					}

				}
				if (resultInfo != null) {

					chart.addBackgroundValue(
							resultInfo.getIntake() == null ? "" : resultInfo
									.getIntake() + "", grid_left * (i + 1)
									+ x_unit / 2, bottom_length
									- bottom_row_height * 4 + 0.1, font); // 入量
					chart.addBackgroundValue(
							resultInfo.getOutput() == null ? "" : resultInfo
									.getOutput() + "", grid_left * (i + 1)
									+ x_unit / 2, bottom_length
									- bottom_row_height * 5 + 0.1, font); // 出量
					chart.addBackgroundValue(
							resultInfo.getPoopCount() == null ? "" : resultInfo
									.getPoopCount() + "", grid_left * (i + 1)
									+ x_unit / 2, bottom_length
									- bottom_row_height * 6 + 0.1, font); // 大便次数
					chart.addBackgroundValue(
							resultInfo.getBloodPressure1() == null ? ""
									: resultInfo.getBloodPressure1() + "",
							grid_left * (i + 1) + 1.5, bottom_length
									- bottom_row_height * 3 + 0.2, font); // 血压1
					chart.addBackgroundValue(
							resultInfo.getBloodPressure2() == null ? ""
									: resultInfo.getBloodPressure2() + "",
							grid_left * (i + 1) + 4.5, bottom_length
									- bottom_row_height * 3 + 0.2, font); // 血压2
					chart.addBackgroundValue(resultInfo.getWeight(), grid_left
							* (i + 1) + x_unit / 2, bottom_length
							- bottom_row_height * 7 + 0.1, font); // 体重
					chart.addBackgroundValue(resultInfo.getHeight(), grid_left
							* (i + 1) + x_unit / 2, bottom_length
							- bottom_row_height * 8 + 0.1, font); // 身高
					chart.addBackgroundValue(
							resultInfo.getUrineVolume() == null ? ""
									: resultInfo.getUrineVolume() + "",
							grid_left * (i + 1) + x_unit / 2, bottom_length
									- bottom_row_height * 9 + 0.1, font); // 尿量
					chart.addBackgroundValue(
							resultInfo.getSkinTest() == null ? "" : resultInfo
									.getSkinTest() + "", grid_left * (i + 1)
									+ x_unit / 2, bottom_length
									- bottom_row_height * 10 + 0.1, font); // 皮试

				}
			}

		}

	}

	/**
	 * 动态格子数据的初始化
	 * 
	 * @param chart
	 * @param map
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("deprecation")
	public void initGridDynamicData(ChartProcessor chart, Map map)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		// //begin
		List<String> dateList = (List<String>) map.get("dateList"); // 日期集合
		List<PTempInfo> tempInfoList = (List<PTempInfo>) map
				.get("tempInfoList"); // 体温单集合
		if (CollectionUtils.isEmpty(tempInfoList)) {
			return;
		}
		/*************add by lizhi 拒测、外出未进行测量的，前后两次体温断开*************/
//		List<Object> myList = new ArrayList<Object>();
//		List<PTempInfo> hasLinePTempInfoList = new ArrayList<PTempInfo>();
//		for (PTempInfo tempInfo : tempInfoList) {
//			List<Map<String,Object>> datailList = tempInfo.getDetailInfo();
//			
//			List<Map<String,Object>> jcList = new ArrayList<Map<String,Object>>();
//			PTempInfo hasLinePTempInfo = tempInfo;
//			hasLinePTempInfo.setDetailInfo(jcList);
//			
//			for(int i=0;i<datailList.size();i++){
//				Map<String,Object> itObj = datailList.get(i);
//				Map<String,Object> hasLineObj = itObj;
//				if("11".equals(MapUtils.getString(itObj, "XMH"))){
//					myList.add(hasLinePTempInfoList);
//					jcList = new ArrayList<Map<String,Object>>();
//					hasLinePTempInfo = tempInfo;
//					hasLinePTempInfo.setDetailInfo(jcList);
//					hasLinePTempInfoList = new ArrayList<PTempInfo>();
//					continue;
//				}
//				jcList.add(hasLineObj);
//			}
//			hasLinePTempInfoList.add(hasLinePTempInfo);
//		}
//		myList.add(hasLinePTempInfoList);
		/*************add by lizhi 拒测、外出未进行测量的，前后两次体温断开*************/
		drawLine(tempInfoList);// 画线
		for (int i = 0; i < dateList.size(); i++) {
			PTempInfo resultInfo = null;
			for (PTempInfo tempInfo : tempInfoList) {
				if (dateList.get(i).equals(
						BSHISUtil.toString(tempInfo.getInspectionDate()))) {
					resultInfo = tempInfo;
					break;
				}

			}
			// 画每天的体温脉搏心率图形

			drawMidGrid(resultInfo);

			// 画每天的呼吸
			List<Map<String, Object>> detailInfoList = resultInfo
					.getDetailInfo();

			for (Map<String, Object> detailInfo : detailInfoList) {
				int hour = Integer.parseInt(detailInfo.get("HOUR").toString());
				int index = hour / 4;
				/**
				 * 呼吸
				 */
				double y_breathe_height = bottom_length - bottom_row_height * 5
						/ 4;
				// double y_breathe_height = Math.round(tem);
				// 如每日记录呼吸2次以上，应当在相应的栏目内上下交错记录
				if (detailInfo.get("XMH").toString().equals("3")) {// normal
					chart.addBackgroundValue(detailInfo.get("TZNR") + "",
							grid_left + i * 6 + grid_col_width / 2 + index,
							y_breathe_height, font, Color.RED);
				}
				// 画备注信息
				if (detailInfo.get("XMH").toString().equals("11")) {
					String bzlx = detailInfo.get("TZNR").toString();
					String sym = "｜";
					if (bzlx.equals("死亡")) {
						sym = "于";
					}
					if(detailInfo.get("BZXX") != null) {
						bzlx += sym + S.obj2String(detailInfo.get("BZXX"));
					}
					if (detailInfo.get("TZNR").toString().equals("拒测")) {//add by lizhi 2017-11-20 拒测、外出体温单线断开，并显示在34℃~35℃之间
						bzlx = "拒测";
						chart.addVerticalValue(bzlx, grid_left + i * 6
								+ grid_col_width / 2 + index, // 0.6为水平偏移量，用来居中文字
								bottom_length + 1 - 0.18);
					}else if (detailInfo.get("TZNR").toString().indexOf("外出")>-1) {
						bzlx = "外出";
						chart.addVerticalValue(bzlx, grid_left + i * 6
								+ grid_col_width / 2 + index, // 0.6为水平偏移量，用来居中文字
								bottom_length + 1 - 0.18);
					}else{
						chart.addVerticalValue(bzlx, grid_left + i * 6
								+ grid_col_width / 2 + index, // 0.6为水平偏移量，用来居中文字
								bottom_length + y_grid_length - 0.18);
					}
				}
			}

		}
		// //end
	}

	/**
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @Title: drawMidGrid
	 * @Description: TODO(画中间表格图)
	 * @param @param resultInfo 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void drawMidGrid(PTempInfo resultInfo)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		List<Map<String, Object>> detailInfoList = resultInfo.getDetailInfo();
		Customerbean bean = new Customerbean();
		PTempInfo p = (PTempInfo) BeanUtils.cloneBean(resultInfo);
		List<String> dateList = ((List<String>) map.get("dateList"));

		// 设置x轴的偏移方位
		for (int i = 0; i < dateList.size(); i++) {
			String dateStr = dateList.get(i);
			if (dateStr.equals(BSHISUtil.toString(resultInfo
					.getInspectionDate()))) {
				p.setNumDay(i);
				resultInfo.setNumDay(i);
			}
		}
		for (int i = 0; i <= 5; i++) {// 每个时间段的非复测点图
			// Map<String,Map<String, Object>> tempMap=new HashMap<String,
			// Map<String,Object>>();
			List<Map<String, Object>> pDetailInfo = new ArrayList<Map<String, Object>>();
			p.setDetailInfo(pDetailInfo);
			for (Map<String, Object> tempObj : detailInfoList) {
				Integer index = TemCalUtils.tranfHour(BSHISUtil.toDate(tempObj
						.get("CJSJ").toString()));
				if (index == i) {
					pDetailInfo.add(tempObj);
				}
			}
			drawDateRegion(p);
		}

		// 画降温线
		for (Map<String, Object> smtz : resultInfo.getFcList()) {
			// 画点
			double value = Double.parseDouble(smtz.get("TZNR").toString());
			Date hour = BSHISUtil.toDate(smtz.get("CJSJ").toString());
			double x = TemCalUtils.getXForTW(hour, resultInfo.getNumDay());
			double y = TemCalUtils.getYForTW(value);
			try {
				TemperaturePoint point = (TemperaturePoint) CharFactories
						.newFactory(TemperatureHollow.class).newInstance();
				TemShapeBean tb = new TemShapeBean();
				tb.setLineColor(Color.RED);
				tb.setLineVisible(true);
				tb.setRoundSize(ShapeBean.round_size + 6);
				tb.setzIndex(500);
				point.setInBean(tb);
				point.drawGraphic(chart, x, y);

				// 画线
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list.add(smtz);
				Integer indext = TemCalUtils.tranfHour(BSHISUtil.toDate(smtz
						.get("CJSJ").toString()));
				for (Map<String, Object> tempObj : detailInfoList) {
					if (MapUtils.getInteger(tempObj, "XMH") != 1)
						continue;
					Integer index = TemCalUtils.tranfHour(BSHISUtil
							.toDate(tempObj.get("CJSJ").toString()));
					if (index == indext) {
						list.add(tempObj);
					}
				}
				String lineName = IdUtils.getInstanse().getUID().toString();
				TemperatureLine line = (TemperatureLine) CharFactories
						.newFactory(TemperatureTWSolidLine.class).newInstance();
				TemShapeBean mbbean = new TemShapeBean();
				mbbean.setLineName(lineName);
				mbbean.setLineDash(Boolean.TRUE);
				mbbean.setLineColor(Color.RED);
				line.setInBean(mbbean);
				line.drawGraphic(chart, list, resultInfo.getNumDay());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * @Title: drawDateRegion
	 * @Description: TODO(画每个区间的点)
	 * @param @param tempMap 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void drawDateRegion(PTempInfo pTempInfo) {
		if (pTempInfo.getDetailInfo() == null
				|| pTempInfo.getDetailInfo().size() < 1)
			return;
		ShapeContext context = new PointShapeContext(pTempInfo, this.chart);
		context.drawPoint();
	}

	/**
	 * @Title: drawLine
	 * @Description: TODO(画线)
	 * @param @param pTempInfo 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void drawLine(List<PTempInfo> pTempInfoList) {
		// if(pTempInfo.getDetailInfo()==null||pTempInfo.getDetailInfo().size()<1)return;
		List<String> dateList = ((List<String>) map.get("dateList"));
		ShapeLineContext context = new LineShapeContext(pTempInfoList,
				this.chart, dateList);
		context.drawPoint();
	}

	private void destroy() {
		if (ctx == null)
			return;
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.close();
	}

	public static void main(String[] args) {
		int a = 3, b = 7;
		// System.out.println(a / b);
	}
}