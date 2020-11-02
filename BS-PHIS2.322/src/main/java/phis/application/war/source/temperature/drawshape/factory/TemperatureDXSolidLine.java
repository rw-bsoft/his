package phis.application.war.source.temperature.drawshape.factory;

import groovy.lang.DeprecationException;

import java.awt.Color;
import java.util.Date;
import java.util.List;
import java.util.Map;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.drawshape.TemCalUtils;
import phis.source.utils.BSHISUtil;
import phis.source.utils.IdUtils;


public class TemperatureDXSolidLine extends AbstractTemperatureLine {


	@Override
	public void drawGraphic(ChartProcessor chart, List<Map<String, Object>> list,int numDay) {
		if(bean.getLineName()==null){
			bean.setLineName(IdUtils.getInstanse().getUID().toString());
		}
		if(bean.getLineColor()==null)bean.setFilledColor(Color.WHITE);
		for(Map<String,Object> smtz:list){
			double value=Double.parseDouble(smtz.get("TZNR").toString());
			Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
			double x=TemCalUtils.getXForTW(hour,numDay);
			double y=TemCalUtils.getYForTW(value);
			chart.addData(ChartProcessor.AXIS_LEFT, bean.getLineName(),x,y);
	        chart.setLineShapeVisible(ChartProcessor.AXIS_LEFT,bean.getLineName(), false);
	        chart.setLineColor(ChartProcessor.AXIS_LEFT, bean.getLineName(),bean.getLineColor());
	       //chart.setLineWidth(ChartProcessor.AXIS_LEFT, bean.getLineName(),this.lineWidth);
	        chart.setLineVisible(ChartProcessor.AXIS_LEFT, bean.getLineName(),true);
	        if(bean.getLineDash()!=null)chart.setLineDash(ChartProcessor.AXIS_LEFT, bean.getLineName(), Boolean.TRUE);
		}
	}
	
	@Deprecated
	@Override
	public void drawGraphic(ChartProcessor chart, double x, double y) {
		throw new DeprecationException("方法已经舍弃");
		
	}

}
