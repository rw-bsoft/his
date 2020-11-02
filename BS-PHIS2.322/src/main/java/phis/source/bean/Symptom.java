package phis.source.bean;

public class Symptom {
	private Long ZHBS;
	private String ZHMC;
	private String BZXX;

	public Symptom(Long zHBS, String zHMC, String bZXX) {
		super();
		ZHBS = zHBS;
		ZHMC = zHMC;
		BZXX = bZXX;
	}

	public Symptom() {
		super();
	}

	public Long getZHBS() {
		return ZHBS;
	}

	public void setZHBS(Long zHBS) {
		ZHBS = zHBS;
	}

	public String getZHMC() {
		return ZHMC;
	}

	public void setZHMC(String zHMC) {
		ZHMC = zHMC;
	}

	public String getBZXX() {
		return BZXX;
	}

	public void setBZXX(String bZXX) {
		BZXX = bZXX;
	}

}
