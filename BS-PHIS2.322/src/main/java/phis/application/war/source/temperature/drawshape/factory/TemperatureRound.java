package phis.application.war.source.temperature.drawshape.factory;

import java.awt.Color;
import java.awt.Shape;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.ChartShape;
import phis.application.war.source.temperature.drawshape.ShapeBean;
import phis.source.utils.IdUtils;


/**
* @ClassName: DoubleRound
* @Description: TODO(实心圆)
* @author zhoufeng
* @date 2013-6-24 上午10:02:15
* 
*/
public class TemperatureRound extends AbstractTemperaurePoint implements TemperaturePoint {
	
	@Override
	public void drawGraphic(ChartProcessor chart,double x,double y) {
		String lineName=IdUtils.getInstanse().getUID().toString();
		if(bean.getRoundSize()==null)bean.setRoundSize(ShapeBean.round_size);
		if(bean.getLineColor()==null)bean.setLineColor(Color.RED);
		if(bean.getLineName()==null)bean.setLineName(lineName);
		if(bean.getLineVisible()==null)bean.setLineVisible(true);

		Shape shape=ChartShape.getShape(ChartShape.ROUND,bean.getRoundSize(),bean.getRoundSize());
		chart.addData(ChartProcessor.AXIS_LEFT, bean.getLineName(), x,y);
        chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,bean.getLineName(), true);
        chart.setLineColor(ChartProcessor.AXIS_LEFT, bean.getLineName(),bean.getLineColor());
        chart.setLineShape(ChartProcessor.AXIS_LEFT, bean.getLineName(),shape);
        chart.setLineVisible(ChartProcessor.AXIS_LEFT, bean.getLineName(),bean.getLineVisible());
        if(bean.getzIndex()!=null){
        	chart.setZIndex(ChartProcessor.AXIS_LEFT, bean.getLineName(), bean.getzIndex());
        }
		
	}
	public static void main(String[] args) {
		
	}

}
