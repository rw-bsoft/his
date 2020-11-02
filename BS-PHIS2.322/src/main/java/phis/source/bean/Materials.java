package phis.source.bean;

/**
 * 
 * @description 物资查询bean
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class Materials {
	private int numKey;
	private Long WZXH;
	private Long CJXH;
	private String CJMC;
	private String GHDW;
	
	private String WZMC;
	private String WZGG;
	private String WZDW;
	private Integer GLFS;
	private Long KCXH;
	private String WZPH;
	private String SCRQ;
	private String SXRQ;
	private Double WZJG;
	private Double LSJG;
	private String MJPH;
	private Double TJSL;
	private Integer BKBZ;
	private Integer KFXH;
	private Integer ZBLB;
	private Long ZBXH;
	private Long KSDM;

	private String KSMC;
	private Long DWXH ;
	private String DWMC;
	private Long WZBH;
	
	
	
	public Materials() {
		super();
	}

	public Materials(int numKey, Long wZXH, Long cJXH, String cJMC,
			String wZMC, String wZGG, String wZDW, Integer gLFS, Long kCXH,
			String wZPH, String sCRQ, String sXRQ, Double wZJG, Double lSJG,
			String mJPH, Double tJSL, Integer bKBZ, Integer kFXH, Integer zBLB,Long zBXH,
			Long kSDM, String kSMC, Long dWXH, String dWMC,String gHDW,Long wZBH) {
		super();
		this.numKey = numKey;
		WZXH = wZXH;
		CJXH = cJXH;
		CJMC = cJMC;
		WZMC = wZMC;
		WZGG = wZGG;
		WZDW = wZDW;
		GLFS = gLFS;
		KCXH = kCXH;
		WZPH = wZPH;
		SCRQ = sCRQ;
		SXRQ = sXRQ;
		WZJG = wZJG;
		LSJG = lSJG;
		MJPH = mJPH;
		TJSL = tJSL;
		BKBZ = bKBZ;
		KFXH = kFXH;
		ZBLB = zBLB;
		ZBXH = zBXH;
		KSDM = kSDM;
		KSMC = kSMC;
		DWXH = dWXH;
		DWMC = dWMC;
		GHDW = gHDW;
		WZBH = wZBH;
	}
	public Long getWZBH() {
		return WZBH;
	}

	public void setWZBH(Long wZBH) {
		WZBH = wZBH;
	}

	public Long getZBXH() {
		return ZBXH;
	}

	public void setZBXH(Long zBXH) {
		ZBXH = zBXH;
	}
	public String getGHDW() {
		return GHDW;
	}

	public void setGHDW(String gHDW) {
		GHDW = gHDW;
	}

	public Long getKSDM() {
		return KSDM;
	}

	public void setKSDM(Long kSDM) {
		KSDM = kSDM;
	}

	public String getKSMC() {
		return KSMC;
	}

	public void setKSMC(String kSMC) {
		KSMC = kSMC;
	}

	public int getNumKey() {
		return numKey;
	}

	public void setNumKey(int numKey) {
		this.numKey = numKey;
	}

	public Long getWZXH() {
		return WZXH;
	}

	public void setWZXH(Long wZXH) {
		WZXH = wZXH;
	}

	public Long getCJXH() {
		return CJXH;
	}

	public void setCJXH(Long cJXH) {
		CJXH = cJXH;
	}

	public String getCJMC() {
		return CJMC;
	}

	public void setCJMC(String cJMC) {
		CJMC = cJMC;
	}

	public String getWZMC() {
		return WZMC;
	}

	public void setWZMC(String wZMC) {
		WZMC = wZMC;
	}

	public String getWZGG() {
		return WZGG;
	}

	public void setWZGG(String wZGG) {
		WZGG = wZGG;
	}

	public String getWZDW() {
		return WZDW;
	}

	public void setWZDW(String wZDW) {
		WZDW = wZDW;
	}

	public Integer getGLFS() {
		return GLFS;
	}

	public void setGLFS(Integer gLFS) {
		GLFS = gLFS;
	}

	public Long getKCXH() {
		return KCXH;
	}

	public void setKCXH(Long kCXH) {
		KCXH = kCXH;
	}

	public String getWZPH() {
		return WZPH;
	}

	public void setWZPH(String wZPH) {
		WZPH = wZPH;
	}

	public String getSCRQ() {
		return SCRQ;
	}

	public void setSCRQ(String sCRQ) {
		SCRQ = sCRQ;
	}

	public String getSXRQ() {
		return SXRQ;
	}

	public void setSXRQ(String sXRQ) {
		SXRQ = sXRQ;
	}

	public Double getWZJG() {
		return WZJG;
	}

	public void setWZJG(Double wZJG) {
		WZJG = wZJG;
	}

	public String getMJPH() {
		return MJPH;
	}

	public void setMJPH(String mJPH) {
		MJPH = mJPH;
	}

	public Double getTJSL() {
		return TJSL;
	}

	public void setTJSL(Double tJSL) {
		TJSL = tJSL;
	}

	public Integer getBKBZ() {
		return BKBZ;
	}

	public void setBKBZ(Integer bKBZ) {
		BKBZ = bKBZ;
	}

	public Integer getKFXH() {
		return KFXH;
	}

	public void setKFXH(Integer kFXH) {
		KFXH = kFXH;
	}

	public Integer getZBLB() {
		return ZBLB;
	}

	public void setZBLB(Integer zBLB) {
		ZBLB = zBLB;
	}
	public Long getDWXH() {
		return DWXH;
	}

	public void setDWXH(Long dWXH) {
		DWXH = dWXH;
	}

	public String getDWMC() {
		return DWMC;
	}

	public void setDWMC(String dWMC) {
		DWMC = dWMC;
	}

	public Double getLSJG() {
		return LSJG;
	}

	public void setLSJG(Double lSJG) {
		LSJG = lSJG;
	}
	
	
}
