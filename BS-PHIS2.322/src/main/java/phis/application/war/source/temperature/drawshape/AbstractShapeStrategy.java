package phis.application.war.source.temperature.drawshape;

import java.awt.Color;
import java.util.Date;
import java.util.List;
import java.util.Map;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.TemShapeBean;
import phis.application.war.source.temperature.TwTypesEnum;
import phis.application.war.source.temperature.drawshape.factory.CharFactories;
import phis.application.war.source.temperature.drawshape.factory.TemperatureCross;
import phis.application.war.source.temperature.drawshape.factory.TemperatureDXSolidLine;
import phis.application.war.source.temperature.drawshape.factory.TemperatureHollow;
import phis.application.war.source.temperature.drawshape.factory.TemperatureLine;
import phis.application.war.source.temperature.drawshape.factory.TemperatureMBSolidLine;
import phis.application.war.source.temperature.drawshape.factory.TemperaturePoint;
import phis.application.war.source.temperature.drawshape.factory.TemperatureRound;
import phis.application.war.source.temperature.drawshape.factory.TemperatureTWSolidLine;
import phis.source.utils.BSHISUtil;

public abstract class AbstractShapeStrategy{
	protected ChartProcessor chart;
	
	
	/**
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	* @Title: drawTw
	* @Description: TODO(画标准口温图形)
	* @param     设定文件
	* @return void    返回类型
	* @throws
	*/
	protected TemperaturePoint drawKW(double x,double y) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureRound.class).newInstance();
		TemShapeBean tb=new TemShapeBean();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size);
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	protected TemperaturePoint drawKW(double x,double y,TemShapeBean tb) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureRound.class).newInstance();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size-2);
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	/**
	* @Title: drawTW
	* @Description: TODO(画体温)
	* @param @param smtz
	* @param @param numDay
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawTW(Map<String,Object> smtz,int numDay) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String XMXB=(String)smtz.get("XMXB");
		double value=Double.parseDouble(smtz.get("TZNR").toString());
		Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
		double x=TemCalUtils.getXForTW(hour,numDay);
		double y=TemCalUtils.getYForTW(value);
		if(TwTypesEnum.KW.getName().equals(XMXB)){
			return drawKW(x, y);
		}else if(TwTypesEnum.YW.getName().equals(XMXB)){
			return drawYW(x, y);
		}else if(TwTypesEnum.GW.getName().equals(XMXB)){
			return drawGW(x, y);
		}else {
			return null;
		}
	}
	
	/**
	* @Title: drawTW
	* @Description: TODO(画体温)
	* @param @param smtz
	* @param @param numDay
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawTW(Map<String,Object> smtz,int numDay,TemShapeBean bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String XMXB=(String)smtz.get("XMXB");
		double value=Double.parseDouble(smtz.get("TZNR").toString());
		Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
		double x=TemCalUtils.getXForTW(hour,numDay);
		double y=TemCalUtils.getYForTW(value);
		if(TwTypesEnum.KW.getName().equals(XMXB)){
			return drawKW(x, y,bean);
		}else if(TwTypesEnum.YW.getName().equals(XMXB)){
			return drawYW(x, y,bean);
		}else if(TwTypesEnum.GW.getName().equals(XMXB)){
			return drawGW(x, y,bean);
		}else {
			return null;
		}
	}
	
	/**
	* @Title: drawYW
	* @Description: TODO(腋温)
	* @param @param x
	* @param @param y
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawYW(double x,double y) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureCross.class).newInstance();
		TemShapeBean tb=new TemShapeBean();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(new Double(3f));
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	protected TemperaturePoint drawYW(double x,double y,TemShapeBean tb) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureCross.class).newInstance();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(new Double(3f));
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	/**
	* @Title: drawGW
	* @Description: TODO(画肛温)
	* @param @param x
	* @param @param y
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawGW(double x,double y) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureHollow.class).newInstance();
		
		TemShapeBean tb=new TemShapeBean();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size);
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	protected TemperaturePoint drawGW(double x,double y,TemShapeBean tb) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureHollow.class).newInstance();
		tb.setLineColor(Color.BLUE);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size-4);
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	/**
	* @Title: drawGW
	* @Description: TODO(画脉搏)
	* @param @param x
	* @param @param y
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawMB(double x,double y) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureRound.class).newInstance();
		TemShapeBean tb=new TemShapeBean();
		tb.setLineColor(Color.RED);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size);
		point.setInBean(tb);
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	protected TemperaturePoint drawMB(Map<String,Object> smtz,int numDay) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		double value=Double.parseDouble(smtz.get("TZNR").toString());
		Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
		double x=TemCalUtils.getXForMB(hour,numDay);
		double y=TemCalUtils.getYForMB(value);
		return drawMB(x, y);
	}
	/**
	* @Title: drawGW
	* @Description: TODO(画心率)
	* @param @param x
	* @param @param y
	* @param @return
	* @param @throws ClassNotFoundException
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException    设定文件
	* @return TemperaturePoint    返回类型
	* @throws
	*/
	protected TemperaturePoint drawXL(double x,double y) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureHollow.class).newInstance();
		TemShapeBean tb=new TemShapeBean();
		tb.setLineColor(Color.RED);
		tb.setLineVisible(true);
		tb.setRoundSize(ShapeBean.round_size);
		//tb.setFilledColor(Color.WHITE);
		point.setInBean(tb);
		
		point.drawGraphic(chart, x, y);
		return point;
	}
	
	protected TemperaturePoint drawXL(Map<String,Object> smtz,int numDay) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		double value=Double.parseDouble(smtz.get("TZNR").toString());
		Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
		double x=TemCalUtils.getXForMB(hour,numDay);
		double y=TemCalUtils.getYForMB(value);
		return drawXL(x, y);
	}
	
	
	protected TemperatureLine drawTWLine(List<Map<String,Object>> list,int numDay,TemShapeBean bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperatureLine point=(TemperatureLine) CharFactories.newFactory(TemperatureTWSolidLine.class).newInstance();
		point.setInBean(bean).drawGraphic(chart, list, numDay);
		return point;
	}
	protected TemperatureLine drawMBLine(List<Map<String,Object>> list,int numDay,TemShapeBean bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperatureLine point=(TemperatureLine) CharFactories.newFactory(TemperatureMBSolidLine.class).newInstance();
		point.setInBean(bean).drawGraphic(chart, list, numDay);
		return point;
	}
	
	protected TemperatureLine drawDXLine(List<Map<String,Object>> list,int numDay,TemShapeBean bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		TemperatureLine point=(TemperatureLine) CharFactories.newFactory(TemperatureDXSolidLine.class).newInstance();
		point.setInBean(bean).drawGraphic(chart, list, numDay);
		return point;
	}
}
