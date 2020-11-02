package phis.application.war.source.temperature.drawshape;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Customerbean {
	private List<Map<String, Object>> list;
	private List<String> dataList;
	private Date currentDate;  //当前日期
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
