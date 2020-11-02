package phis.application.war.source.temperature;

import java.util.Date;

public class PTempDetailInfo {
	private String breathe;
	private Date inspectionTime;
	private String temperature;
	private Integer temperatureType;
	private String pulse;
	private Integer type;//项目
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getBreathe() {
		return breathe;
	}

	public void setBreathe(String breathe) {
		this.breathe = breathe;
	}

	public Date getInspectionTime() {
		return inspectionTime;
	}

	public void setInspectionTime(Date inspectionTime) {
		this.inspectionTime = inspectionTime;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public Integer getTemperatureType() {
		return temperatureType;
	}

	public void setTemperatureType(Integer temperatureType) {
		this.temperatureType = temperatureType;
	}

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

}
