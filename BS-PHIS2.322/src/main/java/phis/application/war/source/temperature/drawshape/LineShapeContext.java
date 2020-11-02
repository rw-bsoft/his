package phis.application.war.source.temperature.drawshape;

import java.util.List;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;

public class LineShapeContext implements ShapeLineContext{
	private List<PTempInfo> pTempInfoList;
	private ShapeLineStrategy strategy;
	private ChartProcessor chart;
	private List<String> dateList;
	public LineShapeContext(List<PTempInfo> pTempInfoList,ChartProcessor chart,List<String> dateList){
		this.pTempInfoList=pTempInfoList;
		this.chart=chart;
		this.dateList=dateList;
	}
	@Override
	public ShapeLineStrategy setShapeStrategy(ShapeLineStrategy strategy) {
		this.strategy=strategy;
		return strategy;
	}

	@Override
	public void drawPoint() {
		this.setShapeStrategy(new LineShape(this.chart,this.dateList));
		strategy.draw(pTempInfoList);
	}

}
