package phis.application.war.source.temperature.drawshape.factory;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.TemShapeBean;


public abstract class AbstractTemperaurePoint implements TemperaturePoint {
	protected TemShapeBean bean;
	
	public TemperaturePoint setInBean(TemShapeBean bean){
		this.bean=bean;
		return this;
	};
	
	public TemShapeBean getInBean(){
		return this.bean;
	}
	@Override
	public abstract void drawGraphic(ChartProcessor chart, double x, double y);

}
