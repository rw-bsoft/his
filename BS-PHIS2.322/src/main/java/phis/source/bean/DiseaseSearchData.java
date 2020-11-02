package phis.source.bean;

import java.util.List;

public class DiseaseSearchData {
	private Long count;
	private List<Disease> disease;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<Disease> getDisease() {
		return disease;
	}

	public void setDisease(List<Disease> disease) {
		this.disease = disease;
	}
}
