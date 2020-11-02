package phis.source.bean;

public class Project {
public Project() {
		super();
		// TODO Auto-generated constructor stub
	}
private int numKey;
private long FYXH;
private String FYMC;
private String FYDW;
private double FYDJ;
//医保对照
private String YYZBM;

public Project(long fYXH, String fYMC, String fYDW, double fYDJ) {
	super();
	FYXH = fYXH;
	FYMC = fYMC;
	FYDW = fYDW;
	FYDJ = fYDJ;
}
public Project(long fYXH, String fYMC, String fYDW, double fYDJ, String yYZBM) {
	super();
	FYXH = fYXH;
	FYMC = fYMC;
	FYDW = fYDW;
	FYDJ = fYDJ;
	YYZBM = yYZBM;
}
public long getFYXH() {
	return FYXH;
}
public void setFYXH(long fYXH) {
	FYXH = fYXH;
}
public String getFYMC() {
	return FYMC;
}
public void setFYMC(String fYMC) {
	FYMC = fYMC;
}
public String getFYDW() {
	return FYDW;
}
public void setFYDW(String fYDW) {
	FYDW = fYDW;
}
public double getFYDJ() {
	return FYDJ;
}
public void setFYDJ(double fYDJ) {
	FYDJ = fYDJ;
}
public int getNumKey() {
	return numKey;
}
public void setNumKey(int numKey) {
	this.numKey = numKey;
}
public String getYYZBM() {
	return YYZBM;	
}
public void setYYZBM(String yYZBM) {
	YYZBM = yYZBM;
}

}
