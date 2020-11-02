package phis.application.war.source.temperature.drawshape.factory;

import phis.application.war.source.temperature.ChartProcessor;


public interface TemperatureShape {
	/**
	* @Title: drawGraphic
	* @Description: TODO(画图)
	* @param @param chart 
	* @param @param x
	* @param @param y    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void drawGraphic(ChartProcessor chart,double x,double y);
}
