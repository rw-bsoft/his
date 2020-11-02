package phis.application.war.source.temperature.drawshape;

import java.awt.Color;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;
import phis.application.war.source.temperature.TemShapeBean;
import phis.application.war.source.temperature.drawshape.factory.CharFactories;
import phis.application.war.source.temperature.drawshape.factory.TemperaturePoint;
import phis.application.war.source.temperature.drawshape.factory.TemperatureRound;
import phis.source.utils.BSHISUtil;

public class PluHeartShape  extends AbstractShapeStrategy implements ShapeStrategy {
	
	public PluHeartShape(ChartProcessor chart){
		this.chart=chart;
	}
	@Override
	public void draw(PTempInfo pTempInfo) {
		try {
			for(Map<String,Object> smtz:pTempInfo.getDetailInfo()){
				if("1".equals(MapUtils.getString(smtz, "XMH"))){//体温
					 drawTW(smtz, pTempInfo.getNumDay());
				}else if("2".equals(MapUtils.getString(smtz, "XMH"))){//脉搏
					double value=Double.parseDouble(smtz.get("TZNR").toString());
					Date hour=BSHISUtil.toDate(smtz.get("CJSJ").toString());
					double x=TemCalUtils.getXForMB(hour,pTempInfo.getNumDay());
					double y=TemCalUtils.getYForMB(value);
					TemperaturePoint point=(TemperaturePoint) CharFactories.newFactory(TemperatureRound.class).newInstance();
					TemShapeBean tb=new TemShapeBean();
					tb.setLineColor(Color.RED);
					tb.setLineVisible(Boolean.TRUE);
					tb.setRoundSize(new Double(2f));
					tb.setzIndex(2000);
					point.setInBean(tb);
					point.drawGraphic(chart, x, y);
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
