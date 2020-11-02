package phis.application.war.source.temperature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PTempInfo {
	private String weight;  //体重 
	private String height;	//身高
	private String urineVolume;
	private String Intake;
	private String output;
	private String poopCount;      // 大便
	private String bloodPressure1;  //收缩压
	private String bloodPressure2;  //舒张压
	private String skinTest;   //皮试
	private Date InspectionDate; //当天时间
	private List<Map<String, Object>> detailInfo;  //当天体温,脉搏,心率非复测数据
	private List<Map<String,Object>> fcList=new ArrayList<Map<String,Object>>();//当天体温,脉搏,心率复测数据
	

	private int numDay=0;
	public int getNumDay() {
		return numDay;
	}

	public void setNumDay(int numDay) {
		this.numDay = numDay;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getUrineVolume() {
		return urineVolume;
	}

	public void setUrineVolume(String urineVolume) {
		this.urineVolume = urineVolume;
	}

	/**
	 * 入量
	 * 
	 * @return
	 */
	public String getIntake() {
		return Intake;
	}

	public void setIntake(String intake) {
		Intake = intake;
	}

	/**
	 * 出量
	 * 
	 * @return
	 */
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * 大便次数
	 * 
	 * @return
	 */
	public String getPoopCount() {
		return poopCount;
	}

	public void setPoopCount(String poopCount) {
		this.poopCount = poopCount;
	}

	/**
	 * 血压1
	 * 
	 * @return
	 */
	public String getBloodPressure1() {
		return bloodPressure1;
	}

	public void setBloodPressure1(String bloodPressure1) {
		this.bloodPressure1 = bloodPressure1;
	}

	/**
	 * 血压2
	 * 
	 * @return
	 */
	public String getBloodPressure2() {
		return bloodPressure2;
	}

	public void setBloodPressure2(String bloodPressure2) {
		this.bloodPressure2 = bloodPressure2;
	}

	public Date getInspectionDate() {
		return InspectionDate;
	}

	public void setInspectionDate(Date inspectionDate) {
		InspectionDate = inspectionDate;
	}

	public List<Map<String, Object>> getDetailInfo() {
		return detailInfo;
	}

	public void setDetailInfo(List<Map<String, Object>> detailInfo) {
		this.detailInfo = detailInfo;
	}
	/**
	* @Description: TODO(皮试)
	* @throws
	*/
	public String getSkinTest() {
		return skinTest;
	}

	public void setSkinTest(String skinTest) {
		this.skinTest = skinTest;
	}

	public List<Map<String, Object>> getFcList() {
		return fcList;
	}

	public void setFcList(List<Map<String, Object>> fcList) {
		this.fcList = fcList;
	}
}
