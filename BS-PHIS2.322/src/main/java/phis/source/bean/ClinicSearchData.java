package phis.source.bean;

import java.util.List;


public class ClinicSearchData {
	private Long count;
	private List<Clinic> clinic;
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Clinic> getClinic() {
		return clinic;
	}
	public void setClinic(List<Clinic> clinic) {
		this.clinic = clinic;
	}
}
