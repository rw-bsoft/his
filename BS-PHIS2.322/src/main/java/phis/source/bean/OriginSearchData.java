package phis.source.bean;

import java.util.List;

public class OriginSearchData {
	private Long count;
	private List<MedicineOrigin> origins;
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<MedicineOrigin> getOrigins() {
		return origins;
	}
	public void setOrigins(List<MedicineOrigin> origins) {
		this.origins = origins;
	}
	
}
