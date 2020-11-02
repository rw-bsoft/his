package phis.application.war.source.temperature.drawshape.factory;

import java.awt.Color;
import java.awt.geom.Point2D;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.ChartShape;
import phis.source.utils.IdUtils;


/**
* @ClassName: TemperatureCross
* @Description: TODO(画叉)
* @author zhoufeng
* @date 2013-6-25 上午09:52:47
* 
*/
public class TemperatureCross extends AbstractTemperaurePoint implements TemperaturePoint{

	@Override
	public void drawGraphic(ChartProcessor chart, double x, double y) {
		String lineName_1=IdUtils.getInstanse().getUID().toString();
		String lineName_2=IdUtils.getInstanse().getUID().toString();
		
		if(bean.getRoundSize()==null)bean.setRoundSize(new Double(3f));
		if(bean.getLineColor()==null)bean.setLineColor(Color.RED);
		if(bean.getLineVisible()==null)bean.setLineVisible(true);
		
		
		chart.addData(ChartProcessor.AXIS_LEFT, lineName_1, x,y);
        chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,lineName_1, true);
        chart.setLineColor(ChartProcessor.AXIS_LEFT, lineName_1,bean.getLineColor());
        chart.setLineShape(ChartProcessor.AXIS_LEFT, lineName_1,ChartShape.getLine(ChartShape.LINE,new Point2D.Double(-bean.getRoundSize(), -bean.getRoundSize()),new Point2D.Double(bean.getRoundSize(), bean.getRoundSize())));
        chart.setLineVisible(ChartProcessor.AXIS_LEFT, lineName_1,bean.getLineVisible());
        if(bean.getzIndex()!=null){
        	chart.setZIndex(ChartProcessor.AXIS_LEFT, lineName_1, bean.getzIndex());
        }
        
        chart.addData(ChartProcessor.AXIS_LEFT, lineName_2, x,y);
        chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,lineName_2, true);
        chart.setLineColor(ChartProcessor.AXIS_LEFT, lineName_2,bean.getLineColor());
        chart.setLineShape(ChartProcessor.AXIS_LEFT, lineName_2,ChartShape.getLine(ChartShape.LINE,new Point2D.Double(-bean.getRoundSize(), bean.getRoundSize()),new Point2D.Double(bean.getRoundSize(), -bean.getRoundSize())));
        chart.setLineVisible(ChartProcessor.AXIS_LEFT, lineName_2,bean.getLineVisible());
        if(bean.getzIndex()!=null){
        	chart.setZIndex(ChartProcessor.AXIS_LEFT, lineName_2, bean.getzIndex());
        }
	}

}
