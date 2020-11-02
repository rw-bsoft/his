package phis.application.war.source.temperature.drawshape;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;
import phis.source.utils.BSHISUtil;



public abstract class AbstractShapeContext {
	protected ChartProcessor chart;
	protected ShapeBean shapeBean;
	/**
	* @Title: searchShape
	* @Description: TODO(画点策略:目前体温,脉搏,心率的三者重合和体温,脉搏两者重合是同样的策略)
	* @param @param temperatureVal
	* @param @param pulseVal
	* @param @param heartRateValue
	* @return PayStrategy    返回类型
	* @throws
	*/
	public ShapeStrategy searchPointShape(PTempInfo pTempInfo){
		ShapeStrategy strategy=null;
		double temVal=0f;
		double pulseVal=0f;
		double heartVal=0f;
		Date temHour=null;
		Date pulseHour=null;
		Date heartHour=null;
		for(Map<String,Object> itObj:pTempInfo.getDetailInfo()){
			if("1".equals(MapUtils.getString(itObj, "XMH"))){
				temVal=MapUtils.getDouble(itObj, "TZNR");
				temHour=BSHISUtil.toDate(MapUtils.getString(itObj, "CJSJ"));
			}else if("2".equals(itObj.get("XMH").toString())){
				pulseVal=MapUtils.getDouble(itObj, "TZNR");;
				pulseHour=BSHISUtil.toDate(MapUtils.getString(itObj, "CJSJ"));
			}else if("4".equals(itObj.get("XMH").toString())){
				heartVal=MapUtils.getDouble(itObj, "TZNR");
				heartHour=BSHISUtil.toDate(MapUtils.getString(itObj, "CJSJ"));
			}
		}
		if(TemCalUtils.isOverlap(temVal, pulseVal, temHour, pulseHour,pTempInfo.getNumDay())&&TemCalUtils.isOverlapMb(pulseVal, heartVal, pulseHour, heartHour,pTempInfo.getNumDay())){//三者重合
			strategy=new TemPluHeartShape(chart);
			//strategy=new NoOverlapShape(chart);
		}else{
			if(TemCalUtils.isOverlap(temVal, pulseVal, temHour, pulseHour,pTempInfo.getNumDay())){//体温和脉搏重合
				strategy=new TemPluShap(chart);
			}else if(TemCalUtils.isOverlap(temVal, heartVal, temHour, heartHour,pTempInfo.getNumDay())){//体温和心率重合
				strategy=new TemHeartShape(chart);
			}else if(TemCalUtils.isOverlapMb(pulseVal, heartVal, pulseHour, heartHour,pTempInfo.getNumDay())){//脉搏和心率重合
				strategy=new PluHeartShape(chart);
			}else{//都不重合
				strategy=new NoOverlapShape(chart);
			}
		}
		return strategy;
	}
}
