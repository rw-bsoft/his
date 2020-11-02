package phis.application.war.source.temperature.drawshape.factory;

import java.util.List;
import java.util.Map;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.TemShapeBean;


public abstract class AbstractTemperatureLine implements TemperatureLine {

	protected TemShapeBean bean;
	@Deprecated
	public abstract void drawGraphic(ChartProcessor chart, double x, double y);
	
	@Override
	public abstract void drawGraphic(ChartProcessor chart,List<Map<String,Object>> list,int numDay);

	public TemperatureLine setInBean(TemShapeBean bean){
		this.bean=bean;
		return this;
	}
}
