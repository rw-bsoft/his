package phis.application.war.source.temperature;

import java.awt.Color;

public class TemShapeBean {
	private String lineName;
	private Double roundSize;
	private Color lineColor;
	private Boolean lineShapeVisible;
	private Color lineShapeFilledColor;
	private Boolean lineVisible;
	private Color filledColor;
	private Boolean lineDash; //线是否是虚线
	
	private Integer zIndex;
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	public Double getRoundSize() {
		return roundSize;
	}
	public void setRoundSize(Double roundSize) {
		this.roundSize = roundSize;
	}
	public Color getLineColor() {
		return lineColor;
	}
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	public Boolean getLineShapeVisible() {
		return lineShapeVisible;
	}
	public void setLineShapeVisible(Boolean lineShapeVisible) {
		this.lineShapeVisible = lineShapeVisible;
	}
	public Color getLineShapeFilledColor() {
		return lineShapeFilledColor;
	}
	public void setLineShapeFilledColor(Color lineShapeFilledColor) {
		this.lineShapeFilledColor = lineShapeFilledColor;
	}
	public Boolean getLineVisible() {
		return lineVisible;
	}
	public void setLineVisible(Boolean lineVisible) {
		this.lineVisible = lineVisible;
	}
	public Color getFilledColor() {
		return filledColor;
	}
	public void setFilledColor(Color filledColor) {
		this.filledColor = filledColor;
	}
	
	public Integer getzIndex() {
		return zIndex;
	}
	public void setzIndex(Integer zIndex) {
		this.zIndex = zIndex;
	}
	public Boolean getLineDash() {
		return lineDash;
	}
	public void setLineDash(Boolean lineDash) {
		this.lineDash = lineDash;
	}

	
}
