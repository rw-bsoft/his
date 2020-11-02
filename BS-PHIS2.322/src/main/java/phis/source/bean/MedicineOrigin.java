package phis.source.bean;

public class MedicineOrigin {
	private int numKey;
	private long YPCD;
	private String CDMC;
	private String PYDM;
	private String WBDM;
	public MedicineOrigin(long YPCD,String CDMC,String PYDM){
		this.YPCD=YPCD;
		this.PYDM=PYDM;
		this.CDMC=CDMC;
	}
	public int getNumKey() {
		return numKey;
	}
	public void setNumKey(int numKey) {
		this.numKey = numKey;
	}
	public String getCDMC() {
		return CDMC;
	}
	public void setCDMC(String cDMC) {
		CDMC = cDMC;
	}
	public String getPYDM() {
		return PYDM;
	}
	public void setPYDM(String pYDM) {
		PYDM = pYDM;
	}
	public long getYPCD() {
		return YPCD;
	}
	public void setYPCD(long yPCD) {
		YPCD = yPCD;
	}
	public void setWBDM(String wBDM) {
		WBDM = wBDM;
	}
	public String getWBDM() {
		return WBDM;
	}
	
}
