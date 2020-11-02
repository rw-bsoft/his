package phis.source.bean;

import java.util.List;


public class MedicinesSearchData {
	private Long count;
	private List<Medicines> mds;
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Medicines> getMds() {
		return mds;
	}
	public void setMds(List<Medicines> mds) {
		this.mds = mds;
	}
}
