package phis.source.bean;


public class BusinessLock {
	private String businessType;// 业务类型
	private Long patientId;// 病人ID
	private Long staffId;// 员工工号
	private Long businessId;// 业务主键
	private String businessDesc; // 业务描述
	
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public Long getPatientId() {
		return patientId;
	}
	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}
	public Long getStaffId() {
		return staffId;
	}
	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}
	public Long getBusinessId() {
		return businessId;
	}
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	public String getBusinessDesc() {
		return businessDesc;
	}
	public void setBusinessDesc(String businessDesc) {
		this.businessDesc = businessDesc;
	}

	
	
	// Map<String, Object> busiKeys = new HashMap<String, Object>();
	// Map<String, Object> busiProps = new HashMap<String, Object>();
	//
	// public BusinessLock(Map<String, Object> keys, Map<String, Object> props)
	// {
	// busiKeys.putAll(keys);
	// busiProps.putAll(props);
	// }
	//
	// public Map<String, Object> getBusiKeys() {
	// return busiKeys;
	// }
	//
	// public void setBusiKeys(Map<String, Object> busiKeys) {
	// this.busiKeys = busiKeys;
	// }
	//
	// public Map<String, Object> getBusiProps() {
	// return busiProps;
	// }
	//
	// public void setBusiProps(Map<String, Object> busiProps) {
	// this.busiProps = busiProps;
	// }
	//
	// @Override
	// public boolean equals(Object o) {
	// if (!(o instanceof BusinessLock))
	// return false;
	// BusinessLock cp = (BusinessLock) o;
	// Set<String> otherBusiKeys = cp.getBusiKeys().keySet();
	// for (String key : busiKeys.keySet()) {
	// if (!otherBusiKeys.contains(key)
	// || !cp.getBusiKeys().get(key).equals(busiKeys.get(key)))
	// return false;
	// }
	// return true;
	// }

}
