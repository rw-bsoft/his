package phis.application.war.source.temperature.drawshape;

import java.util.Calendar;
import java.util.Date;

/**
* @ClassName: TemCalUtils
* @Description: TODO(体温单参数计算util)
* @author zhoufeng
* @date 2013-6-24 上午10:58:43
* 
*/
public class TemCalUtils {
	
	public final static double measurement=0.05f;  //重叠的误差
	/**
	* @Title: getXForTW
	* @Description: TODO(获得体温坐标轴的x值)
	* @param hour value 时间段(24小时制)
	* @param numDay 
	* @throws
	*/
	public static double getXForTW(Date hour,int numDay){
		double value=xValueByGrid(hour);
		double x=ShapeBean.grid_left + numDay * 6+value ;
		return x;
	}
	/**
	* @Title: getXForTW
	* @Description: TODO(获得体温坐标轴的y值)
	* @param @param value 数据
	* @param @return    设定文件
	* @return double    返回类型
	* @throws
	*/
	public static double getYForTW(double value){
		double y =value- (ShapeBean.start_temperature - ShapeBean.bottom_length);
		return y;
	}
	
	/**
	* @Title: getXForMB
	* @Description: TODO(获得脉搏坐标轴的x值)
	* @param @param value
	* @param @return    设定文件
	* @return double    返回类型
	* @throws
	*/
	public static double getXForMB(Date hour,int numDay){
		return getXForTW(hour, numDay);
	}
	/**
	* @Title: getXForMB
	* @Description: TODO(获得脉搏坐标轴的y值)
	* @param @param value
	* @param @return    设定文件
	* @return double    返回类型
	* @throws
	*/
	public static double getYForMB(double value){
		double height = ShapeBean.bottom_length+(value - ShapeBean.start_pulse)* ShapeBean.grid_row_height/4;
		return height;
	}
	
	public  static Integer tranfHour(Date hour){
		if(hour==null){
			throw new NullPointerException();
		}
		Calendar cal=Calendar.getInstance();
		cal.setTime(hour);
		int value=cal.get(Calendar.HOUR_OF_DAY);
		return tranfHour(value);
	}
	
	private static double xValueByGrid(Date hour){
		if(hour==null){
			throw new NullPointerException();
		}
		Calendar cal=Calendar.getInstance();
		cal.setTime(hour);
		/*int i=0;
		if(cal.get(Calendar.HOUR)>0&&cal.get(Calendar.HOUR)<2){
			cal.set(Calendar.HOUR, 24-cal.get(Calendar.HOUR));
			i=1;
		}
		int h=cal.get(Calendar.HOUR_OF_DAY)-2;*/
		int h=cal.get(Calendar.HOUR_OF_DAY);
		int m=cal.get(Calendar.MINUTE);
		int s=cal.get(Calendar.SECOND);
		//double xValue=(h*3600f+m*60+s)/3600f/4*ShapeBean.grid_col_width-i*ShapeBean.grid_col_width;
		double xValue=(h*3600f+m*60+s)/3600f/4*ShapeBean.grid_col_width;
		return xValue;
			
	}
	
	public static Integer tranfHour(Integer hour){
		return hour/ 4;
	}
	
	/**
	* @Title: isOverlap
	* @Description: TODO(判断两个点的中心是否重合)
	* @param @param twValue 体温值
	* @param @param mbValue 脉搏或者心率值
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	*/
	public static boolean isOverlap(double twValue,double mbValue,Date twHour,Date mbHour,int numDay){
		if(twHour==null||mbHour==null)return false;
		double twY = getYForTW(twValue);
		double mbY = getYForMB(mbValue);
		double twX = getXForTW(twHour,numDay);
		double mbX = getXForMB(mbHour,numDay);
		double xpow=Math.pow(Math.abs(twX-mbX), 2);
		double ypow=Math.pow(Math.abs(twY-mbY), 2);
		return Math.pow(xpow+ypow,0.5)<measurement;
	}
	
	public static boolean isOverlapMb(double xlValue,double mbValue,Date xlHour,Date mbHour,int numDay){
		if(xlHour==null||mbHour==null)return false;
		double mbY = getYForMB(mbValue);
		double xlY = getYForMB(xlValue);
		double mbX = getXForMB(mbHour,numDay);
		double xlX = getXForMB(xlHour,numDay);
		double xpow=Math.pow(Math.abs(xlX-mbX), 2);
		double ypow=Math.pow(Math.abs(xlY-mbY), 2);
		return Math.pow(xpow+ypow,0.5)<measurement;
	}
}