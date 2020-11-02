package phis.source.bean;

import java.util.List;


public class SymptomSearchData {
	private Long count;
	private List<Symptom> sym;
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Symptom> getSym() {
		return sym;
	}
	public void setSym(List<Symptom> sym) {
		this.sym = sym;
	}
	
}
