package phis.application.war.source.temperature.drawshape.factory;

import phis.application.war.source.temperature.TemShapeBean;


public interface TemperaturePoint extends TemperatureShape{
	
	
	/**
	* @Title: setLineShape
	* @Description: TODO(设置图形的名称)
	* @param @param shape
	* @param @return    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	public TemperaturePoint setInBean(TemShapeBean bean);
	
	public TemShapeBean getInBean();
}
