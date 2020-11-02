package phis.source.bean;

public class Clinic {
	private int numKey;
	private Long FYXH;
	private String FYMC;
	private String FYDW;
	private Double BZJG;
	private Integer XMLX;
	private Double FYDJ;
	private Integer FYGB;
	private Long FYKS;
	private String FYKS_text;
	private String KSMC;
	
	// 病区
	private Integer YPLX;
	private String YZMC;
	//医技
	private Integer YJSY ; 
	private String JCDL;
	
	//医保标志
	private String YYZBM;
	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
	}

	public Clinic(int numKey, Long fYXH, String fYMC, String fYDW, int fYGB,
			Double fYDJ, String kSMC, Long fYKS) {
		this.numKey = numKey;
		FYXH = fYXH;
		FYMC = fYMC;
		FYDW = fYDW;
		FYDJ = fYDJ;
		FYGB = fYGB;
		FYKS = fYKS;
		KSMC = kSMC;
	}

	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG,
			Integer XMLX, Double FYDJ, Integer FYGB, Long FYKS) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
		this.XMLX = XMLX;
		this.FYDJ = FYDJ;
		this.FYGB = FYGB;
		this.FYKS = FYKS;
	}

	/**
	 * for医技 
	 * @param YJSY 
	 */
	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG,
			Integer XMLX, Double FYDJ, int FYGB, Long FYKS, Integer yPLX, Integer YJSY,String JCDL) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
		this.XMLX = XMLX;
		this.FYDJ = FYDJ;
		this.FYGB = FYGB;
		this.FYKS = FYKS;
		this.YPLX = yPLX;
		this.YJSY = YJSY;
		this.JCDL = JCDL;
	}

	/**
	 *  处置录入
	 * @param YJSY 
	 */
	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG,
			Integer XMLX, Double FYDJ, int FYGB, Long FYKS, Integer yPLX, Integer YJSY,String JCDL,String yyzbm) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
		this.XMLX = XMLX;
		this.FYDJ = FYDJ;
		this.FYGB = FYGB;
		this.FYKS = FYKS;
		this.YPLX = yPLX;
		this.YJSY = YJSY;
		this.JCDL = JCDL;
		this.YYZBM=yyzbm;
	}
	/**
	 * for 病区医嘱
	 * @param YJSY 
	 */
	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG,
			Integer XMLX, Double FYDJ, int FYGB, Long FYKS, Integer yPLX) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
		this.XMLX = XMLX;
		this.FYDJ = FYDJ;
		this.FYGB = FYGB;
		this.FYKS = FYKS;
		this.YPLX = yPLX;
	}
	/**
	 * for 项目组套
	 * @param FYXH
	 * @param FYMC
	 * @param FYDW
	 * @param BZJG
	 * @param XMLX
	 * @param FYDJ
	 * @param FYGB
	 * @param FYKS
	 * @param yPLX
	 */
	public Clinic(Long FYXH, String FYMC, String FYDW, Double BZJG,
			Integer XMLX, Double FYDJ, int FYGB, Long FYKS, Integer yPLX,
			Integer xmlx,String jcdl,String zt,String yyzbm) {
		this.FYXH = FYXH;
		this.FYMC = FYMC;
		this.FYDW = FYDW;
		this.BZJG = BZJG;
		this.XMLX = XMLX;
		this.FYDJ = FYDJ;
		this.FYGB = FYGB;
		this.FYKS = FYKS;
		this.YPLX = yPLX;
		this.XMLX=xmlx;
		this.JCDL=jcdl;
		this.YYZBM=yyzbm;
	}
	public Integer getNumKey() {
		return numKey;
	}

	public void setNumKey(int numKey) {
		this.numKey = numKey;
	}

	public Long getFYXH() {
		return FYXH;
	}

	public void setFYXH(Long fYXH) {
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

	public Double getBZJG() {
		return BZJG;
	}

	public void setBZJG(Double bZJG) {
		BZJG = bZJG;
	}

	public Integer getXMLX() {
		return XMLX;
	}

	public void setXMLX(Integer xMLX) {
		XMLX = xMLX;
	}

	public Double getFYDJ() {
		return FYDJ;
	}

	public void setFYDJ(Double fYDJ) {
		FYDJ = fYDJ;
	}

	public Integer getFYGB() {
		return FYGB;
	}

	public void setFYGB(Integer fYGB) {
		FYGB = fYGB;
	}

	public Long getFYKS() {
		return FYKS;
	}

	public void setFYKS(Long fYKS) {
		FYKS = fYKS;
	}

	public String getFYKS_text() {
		return FYKS_text;
	}

	public void setFYKS_text(String fYKS_text) {
		FYKS_text = fYKS_text;
	}

	public Integer getYPLX() {
		return YPLX;
	}

	public void setYPLX(Integer yPLX) {
		YPLX = yPLX;
	}

	public String getKSMC() {
		return KSMC;
	}

	public void setKSMC(String kSMC) {
		KSMC = kSMC;
	}

	public String getYZMC() {
		return YZMC;
	}

	public void setYZMC(String yZMC) {
		YZMC = yZMC;
	}

	public Integer getYJSY() {
		return YJSY;
	}

	public void setYJSY(Integer yJSY) {
		YJSY = yJSY;
	}

	public String getYYZBM() {
		return YYZBM;
	}

	public void setYYZBM(String yYZBM) {
		YYZBM = yYZBM;
	}

}
