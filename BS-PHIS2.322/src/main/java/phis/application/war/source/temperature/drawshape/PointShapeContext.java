package phis.application.war.source.temperature.drawshape;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;

public class PointShapeContext extends AbstractShapeContext implements ShapeContext{
	private PTempInfo pTempInfo;
	private ShapeStrategy strategy;
	public PointShapeContext(PTempInfo pTempInfo,ChartProcessor chart){
		this.pTempInfo=pTempInfo;
		this.chart=chart;
	}
	@Override
	public ShapeStrategy setShapeStrategy(ShapeStrategy strategy) {
		this.strategy=strategy;
		return strategy;
	}


	@Override
	public void drawPoint() {
		this.setShapeStrategy(searchPointShape(this.pTempInfo));
		strategy.draw(this.pTempInfo);
	}

}
