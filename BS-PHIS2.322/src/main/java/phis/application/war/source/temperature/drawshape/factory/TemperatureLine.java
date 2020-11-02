package phis.application.war.source.temperature.drawshape.factory;

import java.util.List;
import java.util.Map;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.TemShapeBean;


/**
* @ClassName: TemperatureLine
* @Description: TODO(画线接口)
* @author zhoufeng
* @date 2013-7-8 下午04:41:38
* 
*/
public interface TemperatureLine  extends TemperatureShape{
	
	
	/**
	* @Title: drawGraphic
	* @Description: TODO(画线)
	* @param @param chart
	* @param @param list
	* @param @param numDay    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void drawGraphic(ChartProcessor chart,List<Map<String,Object>> list,int numDay);
	
	
	/**
	* @Title: setInBean
	* @Description: TODO(线的 样式VO)
	* @param @param bean
	* @param @return    设定文件
	* @return TemperatureLine    返回类型
	* @throws
	*/
	public TemperatureLine setInBean(TemShapeBean bean);
}
