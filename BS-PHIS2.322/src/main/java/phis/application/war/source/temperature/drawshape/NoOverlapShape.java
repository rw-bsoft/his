package phis.application.war.source.temperature.drawshape;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;

public class NoOverlapShape  extends AbstractShapeStrategy implements ShapeStrategy{
	public NoOverlapShape(ChartProcessor chart){
		this.chart=chart;
	}
	@Override
	public void draw(PTempInfo pTempInfo) {
		try {
			for(Map<String,Object> smtz:pTempInfo.getDetailInfo()){
				if("1".equals(MapUtils.getString(smtz, "XMH"))){//体温
					 drawTW(smtz, pTempInfo.getNumDay());
				}else if("2".equals(MapUtils.getString(smtz, "XMH"))){//脉搏
					drawMB(smtz, pTempInfo.getNumDay());
					//point.setRound_size(1);
				}else if("4".equals(MapUtils.getString(smtz, "XMH"))){//心率
					drawXL(smtz, pTempInfo.getNumDay());
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
