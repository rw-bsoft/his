package phis.source.bean;

import java.util.List;

public class ClinicForMdsSearchData {
	private Long count;
	private List<Clinic> mds;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Clinic> getMds() {
		return mds;
	}

	public void setMds(List<Clinic> clinic) {
		this.mds = clinic;
	}
}
