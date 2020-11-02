package chis.source.print.scriptlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class PregnantVisitScriptlet extends JRDefaultScriptlet {
	@Override
	public void afterColumnInit() throws JRScriptletException {
	}

	@Override
	public void afterDetailEval() throws JRScriptletException {
	}

	@Override
	public void afterGroupInit(String groupName) throws JRScriptletException {
	}

	@Override
	public void afterPageInit() throws JRScriptletException {
	}

	@Override
	public void afterReportInit() throws JRScriptletException {

		
		List<?> l = (List<?>) this.getParameterValue("heightFundusUterusList");

		XYSeriesCollection xyseriescollection = this.setStandardLine(l);

		// 定义图表对象
		JFreeChart jfreechart = ChartFactory.createXYLineChart("妊娠图", "孕周",
				"宫高", xyseriescollection, PlotOrientation.VERTICAL, true, true,
				false);
		jfreechart.setBackgroundPaint(Color.white);

		XYPlot xyplot = jfreechart.getXYPlot();
		xyplot.setBackgroundPaint(new Color(255, 253, 246));
		xyplot.setOutlineStroke(new BasicStroke(1.5f)); // 边框粗细
		// 横坐标
		ValueAxis vaxis = xyplot.getDomainAxis();
		vaxis.setAxisLineStroke(new BasicStroke(1.5f)); // 坐标轴粗细
		vaxis.setAxisLinePaint(new Color(215, 215, 215)); // 坐标轴颜色
		xyplot.setOutlineStroke(new BasicStroke(1.5f)); // 边框粗细
		vaxis.setLabelPaint(new Color(10, 10, 10)); // 坐标轴标题颜色
		vaxis.setTickLabelPaint(new Color(102, 102, 102)); // 坐标轴标尺值颜色
		vaxis.setLowerMargin(0.06d);// 分类轴下（左）边距
		vaxis.setUpperMargin(0.14d);// 分类轴下（右）边距,防止最后边的一个数据靠近了坐标轴。
		//vaxis.setAutoRange(false);
		vaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		ValueAxis valueAxis = xyplot.getRangeAxis();
		valueAxis.setUpperBound(45);
		valueAxis.setAutoRangeMinimumSize(1);
		valueAxis.setLowerBound(0);
		valueAxis.setAutoRange(false);
		valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		valueAxis.setAxisLineStroke(new BasicStroke(1.5f)); // 坐标轴粗细
		valueAxis.setAxisLinePaint(new Color(215, 215, 215)); // 坐标轴颜色
		valueAxis.setLabelPaint(new Color(10, 10, 10)); // 坐标轴标题颜色
		valueAxis.setTickLabelPaint(new Color(102, 102, 102)); // 坐标轴标尺值颜色

		xyplot.setRangeGridlinesVisible(true);
		xyplot.setDomainGridlinesVisible(true);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setBackgroundPaint(new Color(255, 253, 246));
		xyplot.setNoDataMessage("没有数据");// 没有数据时显示的文字说明。
		xyplot.setNoDataMessageFont(new Font("", Font.BOLD, 14));// 字体的大小，粗体。
		xyplot.setNoDataMessagePaint(new Color(87, 149, 117));// 字体颜色
		xyplot.setAxisOffset(new RectangleInsets(5d, 5d, 5d, 5d)); //

		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		if (l.size() > 0) {
			// 线的粗细
			xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(3.0f));
			// 线的颜色
			xylineandshaperenderer.setSeriesPaint(0, Color.RED);
			xylineandshaperenderer.setSeriesPaint(1, Color.YELLOW);
			xylineandshaperenderer.setSeriesPaint(2, Color.BLUE);
//			xylineandshaperenderer.setSeriesShapesVisible(1, true);  
			xylineandshaperenderer.setSeriesPaint(3, Color.GREEN);
		} else {
			xylineandshaperenderer.setSeriesPaint(0, Color.YELLOW);
			xylineandshaperenderer.setSeriesPaint(1, Color.BLUE);
			xylineandshaperenderer.setSeriesPaint(2, Color.GREEN);
		}
//		xylineandshaperenderer.setBaseItemLabelsVisible(true);
//		xylineandshaperenderer
//				.setBasePositiveItemLabelPosition(new ItemLabelPosition(
//						ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
//		xylineandshaperenderer
//				.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
//		xylineandshaperenderer.setBaseItemLabelPaint(new Color(102, 102, 102));

		if (null != jfreechart) {
			BufferedImage image = jfreechart.createBufferedImage(500, 500);
			this.setVariableValue("chart", image);
		}

	}

	@Override
	public void beforeColumnInit() throws JRScriptletException {
	}

	@Override
	public void beforeDetailEval() throws JRScriptletException {
	}

	@Override
	public void beforeGroupInit(String groupName) throws JRScriptletException {
	}

	@Override
	public void beforePageInit() throws JRScriptletException {
	}

	@Override
	public void beforeReportInit() throws JRScriptletException {
	}

	public String showInfor() throws JRScriptletException {
		return "the is scriptlet scriptlet scriptlet the,sscriptlet report the is ascriptlet report this is a scriptlet report this is a scriptlet report";
	}

	public XYSeriesCollection setStandardLine(List<?> l) {
		String series0 = "检查值";
		XYSeries xy0 = new XYSeries(series0, true, true);
		if (l != null && l.size() > 0) {
			for (int i = 0; i < l.size(); i++) {
				Map<?, ?> o = (HashMap<?, ?>) l.get(i);
				if (o.get("extend1".toUpperCase()) == null
						|| Integer.valueOf(o.get("extend1".toUpperCase())
								.toString()) < 20) {
					continue;
				}
				Object obj = o.get("heightFundusUterus".toUpperCase());
				if (obj != null) {
					double v = Double.valueOf(obj.toString());
					xy0.add(Double.parseDouble(o.get("extend1".toUpperCase())
						.toString()), v);
				}
			}
		}
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		xyseriescollection.addSeries(xy0);

		String series1 = "10th";
		String series2 = "50th";
		String series3 = "90th";
		double week20 = 20;
		double week21 = 21;
		double week22 = 22;
		double week23 = 23;
		double week24 = 24;
		double week25 = 25;
		double week26 = 26;
		double week27 = 27;
		double week28 = 28;
		double week29 = 29;
		double week30 = 30;
		double week31 = 31;
		double week32 = 32;
		double week33 = 33;
		double week34 = 34;
		double week35 = 35;
		double week36 = 36;
		double week37 = 37;
		double week38 = 38;
		double week39 = 39;
		double week40 = 40;
		double week41 = 41;

		XYSeries xy1 = new XYSeries(series1, true, true);
		xy1.add(week20, 15.3);
		xy1.add(week21, 17.6);
		xy1.add(week22, 18.7);
		xy1.add(week23, 19.0);
		xy1.add(week24, 22.0);
		xy1.add(week25, 21.0);
		xy1.add(week26, 22.3);
		xy1.add(week27, 21.4);
		xy1.add(week28, 22.4);
		xy1.add(week29, 24.0);
		xy1.add(week30, 24.8);
		xy1.add(week31, 26.3);
		xy1.add(week32, 25.3);
		xy1.add(week33, 26.0);
		xy1.add(week34, 27.8);
		xy1.add(week35, 29.0);
		xy1.add(week36, 29.8);
		xy1.add(week37, 29.8);
		xy1.add(week38, 30.0);
		xy1.add(week39, 29.5);
		xy1.add(week40, 30.0);
		xy1.add(week41, 31.8);
		xyseriescollection.addSeries(xy1);

		XYSeries xy2 = new XYSeries(series2, true, true);
		xy2.add(week20, 18.3);
		xy2.add(week21, 20.8);
		xy2.add(week22, 21.8);
		xy2.add(week23, 22.0);
		xy2.add(week24, 23.6);
		xy2.add(week25, 23.5);
		xy2.add(week26, 24.0);
		xy2.add(week27, 25.0);
		xy2.add(week28, 26.1);
		xy2.add(week29, 27.3);
		xy2.add(week30, 27.5);
		xy2.add(week31, 28.0);
		xy2.add(week32, 29.3);
		xy2.add(week33, 29.8);
		xy2.add(week34, 31.0);
		xy2.add(week35, 31.0);
		xy2.add(week36, 31.5);
		xy2.add(week37, 32.0);
		xy2.add(week38, 32.5);
		xy2.add(week39, 32.8);
		xy2.add(week40, 33.3);
		xy2.add(week41, 34.0);

		xyseriescollection.addSeries(xy2);

		XYSeries xy3 = new XYSeries(series3, true, true);
		xy3.add(week20, 21.4);
		xy3.add(week21, 23.2);
		xy3.add(week22, 24.2);
		xy3.add(week23, 24.5);
		xy3.add(week24, 25.1);
		xy3.add(week25, 25.9);
		xy3.add(week26, 27.3);
		xy3.add(week27, 28.0);
		xy3.add(week28, 29.0);
		xy3.add(week29, 30.0);
		xy3.add(week30, 31.0);
		xy3.add(week31, 30.0);
		xy3.add(week32, 32.0);
		xy3.add(week33, 32.3);
		xy3.add(week34, 33.8);
		xy3.add(week35, 33.3);
		xy3.add(week36, 34.5);
		xy3.add(week37, 35.0);
		xy3.add(week38, 35.7);
		xy3.add(week39, 35.8);
		xy3.add(week40, 35.3);
		xy3.add(week41, 37.3);
		xyseriescollection.addSeries(xy3);

		return xyseriescollection;

	}

}
