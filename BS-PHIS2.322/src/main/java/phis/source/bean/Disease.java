package phis.source.bean;

public class Disease {
	private Integer numKey;
	public Long JBXH;
	public String JBMC;
	public String ICD10;
	public String JBPB;

	public Disease(Long jBXH, String jBMC, String iCD10, String jBPB) {
		JBXH = jBXH;
		JBMC = jBMC;
		ICD10 = iCD10;
		JBPB = jBPB;
	}

	public Integer getNumKey() {
		return numKey;
	}

	public void setNumKey(Integer numKey) {
		this.numKey = numKey;
	}

	public Long getJBXH() {
		return JBXH;
	}

	public void setJBXH(Long jBXH) {
		JBXH = jBXH;
	}

	public String getJBMC() {
		return JBMC;
	}

	public void setJBMC(String jBMC) {
		JBMC = jBMC;
	}

	public String getICD10() {
		return ICD10;
	}

	public void setICD10(String iCD10) {
		ICD10 = iCD10;
	}

	public String getJBPB() {
		return JBPB;
	}

	public void setJBPB(String jBPB) {
		JBPB = jBPB;
	}

}
