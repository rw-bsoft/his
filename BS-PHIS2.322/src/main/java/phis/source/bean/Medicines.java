package phis.source.bean;

import java.math.BigDecimal;

public class Medicines {
	private int numKey;
	private Long YPXH;
	private String YPMC;
	private String YFGG;
	private String YPGG;
	private String YPDW;
	private Integer TYPE;
	private Long FYGB;
	// private String YPSL;
	private Integer PSPB;
	private Integer TSYP;// 特殊药品
	private Integer YBFL;// 医保分类
	private String YBFL_text;// 医保分类
	private Double YPJL;
	private String JLDW;
	private Double YCJL;
	private Long GYFF;// 给药方式
	private String GYFF_text;//
	private Long FYFS;// 发药方式
	private String FYFS_text;//
	private Integer JBYWBZ;
	// 以下是出入库用到的
	private String CDMC;
	private Long YPCD;
	private Double LSJG;// 药房零售价格
	private Double JHJG;// 药房进货价格

	private Integer YFBZ;
	private String YFDW;
	private Double PFJG;// 药房批发价格
	private Long KCSB;// 库存识别
	private Double KCSL;// 库存数量
	private String YPPH;// 批号
	private String YPXQ;// 药品效期

	// 病区
	private Integer YPLX;

	private String BFGG;
	private Integer BFBZ;
	private String BFDW;
	private Integer ZXBZ;
	private String YZMC;
	private Long YFSB;
	private String YFSB_text;
	// 药库
	private Double LSJE;
	private Double JHJE;
	private Double PFJE;
	private String JGID;
	private Double GYLJ;
	private Double GYJJ;
	private Long DWXH;
	private String DWMC;
	private String KSMC;
	private Long KSDM;
	private String KWBM;
	private double YPSL;
	private long YFKCSB;
	private double YKJH;
	private double YKLJ;
	private double YKPJ;
	private double YKJJ;
	private Integer ZSSF;
	private Integer JYLX;
	private double GCSL;
	private double DCSL;
	// 抗菌药物
	private Integer KSBZ;
	private BigDecimal YCYL;
	private Integer KSSDJ;
	private Integer YQSYFS;
	private Integer SFSP;
	private Integer ZFYP;
	private Integer DJFS;
	private String DJGS;
	private Double BZLJ;
	private Integer ISZT;
	
	//过敏药物类别
	private Integer GMYWLB;
	private String GMYWLB_text;
	private Integer YYBS;
	//医保对照
	private String YYZBM;

	public Medicines() {

	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Double kCSL, Integer zSSF) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		KCSL = kCSL;
		ZSSF = zSSF;
	}

	public Medicines(Long yPXH, String yPMC, String bFGG, String bFDW,
			Integer bFBZ, Integer zXBZ, Long yPCD, Double lSJG, String cDMC) {
		YPXH = yPXH;
		YPMC = yPMC;
		BFGG = bFGG;
		BFDW = bFDW;
		BFBZ = bFBZ;
		ZXBZ = zXBZ;
		YPCD = yPCD;
		LSJG = lSJG;
		CDMC = cDMC;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			String yFDW, Integer pSPB, String jLDW, Double yPJL, Double yCJL,
			Long gYFF, Integer yFBZ) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		YFDW = yFDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Double kCSL, Long fYGB) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		KCSL = kCSL;
		FYGB = fYGB;
	}

	// 抗菌药物
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Double kCSL, Long fYGB, Integer kSBZ, BigDecimal yCYL,
			Integer kSSDJ, Integer yQSYFS, Integer sFSP, Integer zFYP) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		KCSL = kCSL;
		FYGB = fYGB;
		KSBZ = kSBZ;
		YCYL = yCYL;
		KSSDJ = kSSDJ;
		YQSYFS = yQSYFS;
		SFSP = sFSP;
		ZFYP = zFYP;
	}

	//处方录入增加药品医保对照标识
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
					 Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
					 Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
					 Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
					 Double kCSL, Long fYGB, Integer kSBZ, BigDecimal yCYL,
					 Integer kSSDJ, Integer yQSYFS, Integer sFSP, Integer zFYP, String yYZBM) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		KCSL = kCSL;
		FYGB = fYGB;
		KSBZ = kSBZ;
		YCYL = yCYL;
		KSSDJ = kSSDJ;
		YQSYFS = yQSYFS;
		SFSP = sFSP;
		ZFYP = zFYP;
		YYZBM = yYZBM;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG, Double kCSL) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
		KCSL = kCSL;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG, Double kCSL,
			Long yFSB) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
		KCSL = kCSL;
		YFSB = yFSB;
	}

	// 增加抗菌药物信息
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG, Double kCSL,
			Integer kSBZ, BigDecimal yCYL, Integer kSSDJ, Integer yQSYFS,
			Integer sFSP, Integer zFYP) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
		KCSL = kCSL;
		KSBZ = kSBZ;
		YCYL = yCYL;
		KSSDJ = kSSDJ;
		YQSYFS = yQSYFS;
		SFSP = sFSP;
		ZFYP = zFYP;
	}

	// 增加抗菌药物信息
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG, Double kCSL,
			Long yFSB, Integer kSBZ, BigDecimal yCYL, Integer kSSDJ,
			Integer yQSYFS, Integer sFSP, Integer zFYP) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
		KCSL = kCSL;
		YFSB = yFSB;
		KSBZ = kSBZ;
		YCYL = yCYL;
		KSSDJ = kSSDJ;
		YQSYFS = yQSYFS;
		SFSP = sFSP;
		ZFYP = zFYP;
	}
	//住院医生站
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer pSPB, String jLDW, Double yPJL, Double yCJL, Long gYFF,
			Integer yFBZ, Double lSJG, Long yPCD, String cDMC, Integer tYPE,
			Integer tSYP, String yFDW, Integer yBFL, Integer jYLX,
			Integer yPLX, Long fYFS, Integer zXBZ, String yPGG, Double kCSL,
			Long yFSB, Integer kSBZ, BigDecimal yCYL, Integer kSSDJ,
			Integer yQSYFS, Integer sFSP, Integer zFYP, String yYZBM) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		PSPB = pSPB;
		JLDW = jLDW;
		YPJL = yPJL;
		YCJL = yCJL;
		GYFF = gYFF;
		YFBZ = yFBZ;
		LSJG = lSJG;
		YPCD = yPCD;
		CDMC = cDMC;
		TYPE = tYPE;
		TSYP = tSYP;
		YFDW = yFDW;
		YBFL = yBFL;
		JYLX = jYLX;
		YPLX = yPLX;
		FYFS = fYFS;
		ZXBZ = zXBZ;
		YPGG = yPGG;
		KCSL = kCSL;
		YFSB = yFSB;
		KSBZ = kSBZ;
		YCYL = yCYL;
		KSSDJ = kSSDJ;
		YQSYFS = yQSYFS;
		SFSP = sFSP;
		ZFYP = zFYP;
		YYZBM = yYZBM;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
	}

	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Double yPJL, String jLDW) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		YPJL = yPJL;
		JLDW = jLDW;
	}
	
	public Medicines(Long yPXH, String yPMC, String yFGG, String yPDW,
			Integer gMYWLB, Integer yYBS) {
		YPXH = yPXH;
		YPMC = yPMC;
		YFGG = yFGG;
		YPDW = yPDW;
		GMYWLB = gMYWLB;
		YYBS = yYBS;
	}

	public String getJLDW() {
		return JLDW;
	}

	public void setJLDW(String jLDW) {
		JLDW = jLDW;
	}

	public int getNumKey() {
		return numKey;
	}

	public void setNumKey(int numKey) {
		this.numKey = numKey;
	}

	public Long getYPXH() {
		return YPXH;
	}

	public void setYPXH(Long yPXH) {
		YPXH = yPXH;
	}

	public String getYPMC() {
		return YPMC;
	}

	public void setYPMC(String yPMC) {
		YPMC = yPMC;
	}

	public String getYFGG() {
		return YFGG;
	}

	public void setYFGG(String yFGG) {
		YFGG = yFGG;
	}

	public String getYPDW() {
		return YPDW;
	}

	public void setYPDW(String yPDW) {
		YPDW = yPDW;
	}

	public Integer getPSPB() {
		return PSPB;
	}

	public void setPSPB(Integer pSPB) {
		PSPB = pSPB;
	}

	public Double getYPJL() {
		return YPJL;
	}

	public void setYPJL(Double yPJL) {
		YPJL = yPJL;
	}

	public String getCDMC() {
		return CDMC;
	}

	public void setCDMC(String cdmc) {
		CDMC = cdmc;
	}

	public Long getYPCD() {
		return YPCD;
	}

	public void setYPCD(Long yPCD) {
		YPCD = yPCD;
	}

	public Double getLSJG() {
		return LSJG;
	}

	public void setLSJG(Double lSJG) {
		LSJG = lSJG;
	}

	public Double getJHJG() {
		return JHJG;
	}

	public void setJHJG(Double jHJG) {
		JHJG = jHJG;
	}

	public Integer getYFBZ() {
		return YFBZ;
	}

	public void setYFBZ(Integer yFBZ) {
		YFBZ = yFBZ;
	}

	public String getYFDW() {
		return YFDW;
	}

	public void setYFDW(String yFDW) {
		YFDW = yFDW;
	}

	public Double getPFJG() {
		return PFJG;
	}

	public void setPFJG(Double pFJG) {
		PFJG = pFJG;
	}

	public Long getGYFF() {
		return GYFF;
	}

	public void setGYFF(Long gYFF) {
		GYFF = gYFF;
	}

	public Integer getTYPE() {
		return TYPE;
	}

	public void setTYPE(Integer tYPE) {
		TYPE = tYPE;
	}

	public Long getKCSB() {
		return KCSB;
	}

	public void setKCSB(Long kCSB) {
		KCSB = kCSB;
	}

	public Double getKCSL() {
		return KCSL;
	}

	public void setKCSL(Double kCSL) {
		KCSL = kCSL;
	}

	public Integer getTSYP() {
		return TSYP;
	}

	public void setTSYP(Integer tSYP) {
		TSYP = tSYP;
	}

	public String getYPPH() {
		return YPPH;
	}

	public void setYPPH(String yPPH) {
		YPPH = yPPH;
	}

	public String getYPXQ() {
		return YPXQ;
	}

	public void setYPXQ(String yPXQ) {
		YPXQ = yPXQ;
	}

	public Integer getYBFL() {
		return YBFL;
	}

	public void setYBFL(Integer yBFL) {
		YBFL = yBFL;
	}

	public String getYBFL_text() {
		return YBFL_text;
	}

	public void setYBFL_text(String yBFL_text) {
		YBFL_text = yBFL_text;
	}

	public String getGYFF_text() {
		return GYFF_text;
	}

	public void setGYFF_text(String gYFF_text) {
		GYFF_text = gYFF_text;
	}

	public Integer getJBYWBZ() {
		return JBYWBZ;
	}

	public void setJBYWBZ(Integer jBYWBZ) {
		JBYWBZ = jBYWBZ;
	}

	public Integer getYPLX() {
		return YPLX;
	}

	public void setYPLX(Integer yPLX) {
		YPLX = yPLX;
	}

	public Long getYFSB() {
		return YFSB;
	}

	public void setYFSB(Long yFSB) {
		YFSB = yFSB;
	}

	public String getYFSB_text() {
		return YFSB_text;
	}

	public void setYFSB_text(String yFSB_text) {
		YFSB_text = yFSB_text;
	}

	public String getBFGG() {
		return BFGG;
	}

	public void setBFGG(String bFGG) {
		BFGG = bFGG;
	}

	public Integer getBFBZ() {
		return BFBZ;
	}

	public void setBFBZ(Integer bFBZ) {
		BFBZ = bFBZ;
	}

	public String getBFDW() {
		return BFDW;
	}

	public void setBFDW(String bFDW) {
		BFDW = bFDW;
	}

	public Integer getZXBZ() {
		return ZXBZ;
	}

	public void setZXBZ(Integer zXBZ) {
		ZXBZ = zXBZ;
	}

	public Long getFYFS() {
		return FYFS;
	}

	public void setFYFS(Long fYFS) {
		FYFS = fYFS;
	}

	public String getFYFS_text() {
		return FYFS_text;
	}

	public void setFYFS_text(String fYFS_text) {
		FYFS_text = fYFS_text;
	}

	public String getYPGG() {
		return YPGG;
	}

	public void setYPGG(String yPGG) {
		YPGG = yPGG;
	}

	public String getYZMC() {
		return YZMC;
	}

	public void setYZMC(String yZMC) {
		YZMC = yZMC;
	}

	public Double getLSJE() {
		return LSJE;
	}

	public void setLSJE(Double lSJE) {
		LSJE = lSJE;
	}

	public Double getJHJE() {
		return JHJE;
	}

	public void setJHJE(Double jHJE) {
		JHJE = jHJE;
	}

	public Double getPFJE() {
		return PFJE;
	}

	public void setPFJE(Double pFJE) {
		PFJE = pFJE;
	}

	public String getJGID() {
		return JGID;
	}

	public void setJGID(String jGID) {
		JGID = jGID;
	}

	public Double getGYLJ() {
		return GYLJ;
	}

	public void setGYLJ(Double gYLJ) {
		GYLJ = gYLJ;
	}

	public Double getGYJJ() {
		return GYJJ;
	}

	public void setGYJJ(Double gYJJ) {
		GYJJ = gYJJ;
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

	public String getKSMC() {
		return KSMC;
	}

	public void setKSMC(String kSMC) {
		KSMC = kSMC;
	}

	public Long getKSDM() {
		return KSDM;
	}

	public void setKSDM(Long kSDM) {
		KSDM = kSDM;
	}

	public String getKWBM() {
		return KWBM;
	}

	public void setKWBM(String kWBM) {
		KWBM = kWBM;
	}

	public double getYPSL() {
		return YPSL;
	}

	public void setYPSL(double yPSL) {
		YPSL = yPSL;
	}

	public long getYFKCSB() {
		return YFKCSB;
	}

	public void setYFKCSB(long yFKCSB) {
		YFKCSB = yFKCSB;
	}

	public double getYKJH() {
		return YKJH;
	}

	public void setYKJH(double yKJH) {
		YKJH = yKJH;
	}

	public double getYKLJ() {
		return YKLJ;
	}

	public void setYKLJ(double yKLJ) {
		YKLJ = yKLJ;
	}

	public double getYKPJ() {
		return YKPJ;
	}

	public void setYKPJ(double yKPJ) {
		YKPJ = yKPJ;
	}

	public double getYKJJ() {
		return YKJJ;
	}

	public void setYKJJ(double yKJJ) {
		YKJJ = yKJJ;
	}

	public Integer getZSSF() {
		return ZSSF;
	}

	public void setZSSF(Integer zSSF) {
		ZSSF = zSSF;
	}

	public Integer getJYLX() {
		return JYLX;
	}

	public void setJYLX(Integer jYLX) {
		JYLX = jYLX;
	}

	public Long getFYGB() {
		return FYGB;
	}

	public void setFYGB(Long fYGB) {
		FYGB = fYGB;
	}

	public Integer getKSBZ() {
		return KSBZ;
	}

	public void setKSBZ(Integer kSBZ) {
		KSBZ = kSBZ;
	}

	public BigDecimal getYCYL() {
		return YCYL;
	}

	public void setYCYL(BigDecimal yCYL) {
		YCYL = yCYL;
	}

	public Integer getKSSDJ() {
		return KSSDJ;
	}

	public void setKSSDJ(Integer kSSDJ) {
		KSSDJ = kSSDJ;
	}

	public Integer getYQSYFS() {
		return YQSYFS;
	}

	public void setYQSYFS(Integer yQSYFS) {
		YQSYFS = yQSYFS;
	}

	public Integer getSFSP() {
		return SFSP;
	}

	public void setSFSP(Integer sFSP) {
		SFSP = sFSP;
	}

	public Double getYCJL() {
		return YCJL;
	}

	public void setYCJL(Double yCJL) {
		YCJL = yCJL;
	}

	public Integer getZFYP() {
		return ZFYP;
	}

	public void setZFYP(Integer zFYP) {
		ZFYP = zFYP;
	}

	public Integer getDJFS() {
		return DJFS;
	}

	public void setDJFS(Integer dJFS) {
		DJFS = dJFS;
	}

	public String getDJGS() {
		return DJGS;
	}

	public void setDJGS(String dJGS) {
		DJGS = dJGS;
	}

	public Double getBZLJ() {
		return BZLJ;
	}

	public void setBZLJ(Double bZLJ) {
		BZLJ = bZLJ;
	}

	public double getGCSL() {
		return GCSL;
	}

	public void setGCSL(double gCSL) {
		GCSL = gCSL;
	}

	public double getDCSL() {
		return DCSL;
	}

	public void setDCSL(double dCSL) {
		DCSL = dCSL;
	}

	public Integer getISZT() {
		return ISZT;
	}

	public void setISZT(Integer iSZT) {
		ISZT = iSZT;
	}

	public Integer getGMYWLB() {
		return GMYWLB;
	}

	public void setGMYWLB(Integer gMYWLB) {
		GMYWLB = gMYWLB;
	}

	public Integer getYYBS() {
		return YYBS;
	}

	public void setYYBS(Integer yYBS) {
		YYBS = yYBS;
	}

	public String getGMYWLB_text() {
		return GMYWLB_text;
	}

	public void setGMYWLB_text(String gMYWLB_text) {
		GMYWLB_text = gMYWLB_text;
	}

	public String getYYZBM() {return YYZBM;	}

	public void setYYZBM(String yYZBM) {
		YYZBM = yYZBM;
	}
}
