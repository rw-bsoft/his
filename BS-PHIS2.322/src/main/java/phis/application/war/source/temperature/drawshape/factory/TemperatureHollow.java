package phis.application.war.source.temperature.drawshape.factory;

import java.awt.Color;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.ChartShape;
import phis.application.war.source.temperature.drawshape.ShapeBean;
import phis.source.utils.IdUtils;


/**
* @ClassName: TemperatureHollow
* @Description: TODO(画空心圆)
* @author zhoufeng
* @date 2013-6-25 上午09:52:05
* 
*/
public class TemperatureHollow extends AbstractTemperaurePoint implements TemperaturePoint{

	@Override
	public void drawGraphic(ChartProcessor chart, double x, double y) {
		String lineName=IdUtils.getInstanse().getUID().toString();
		if(bean.getRoundSize()==null)bean.setRoundSize(ShapeBean.round_size);
		if(bean.getLineColor()==null)bean.setLineColor(Color.RED);
		if(bean.getLineName()==null)bean.setLineName(lineName);
		if(bean.getLineVisible()==null)bean.setLineVisible(true);
		if(bean.getFilledColor()==null)bean.setFilledColor(Color.WHITE);
				
		chart.addData(ChartProcessor.AXIS_LEFT, bean.getLineName(), x,y);
        //chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,lineName, true);
        chart.setLineColor(ChartProcessor.AXIS_LEFT, lineName,bean.getLineColor());
        chart.setLineShape(ChartProcessor.AXIS_LEFT, bean.getLineName(),ChartShape.getShape(ChartShape.ROUND, bean.getRoundSize(),bean.getRoundSize()));
        //chart.setLineVisible(ChartProcessor.AXIS_LEFT, lineName,true);
        //chart.setLineWidth(ChartProcessor.AXIS_LEFT, lineName, 1f);
        if(bean.getFilledColor()!=null)chart.setLineShapeFilledColor(ChartProcessor.AXIS_LEFT, lineName, bean.getFilledColor());
        if(bean.getzIndex()!=null){
        	chart.setZIndex(ChartProcessor.AXIS_LEFT, bean.getLineName(), bean.getzIndex());
        }
        
	}

}
