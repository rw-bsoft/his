package chis.source.common.entity;
public class AreaGrid {
	private int numKey;
	private String regionCode;
	
	private String parentCode;
	
	private String parentCode1;//(省)
	private String parentCode2;//(市)
	private String parentCode3;//(区)
	private String parentCode4;//(街道)
	private String parentCode5;//(社区)
	
	private String regionName;
	
	private String parentName1;//(省)
	private String parentName2;//(市)
	private String parentName3;//(区)
	private String parentName4;//(街道)
	private String parentName5;//(社区)

	private String regionNo;
	private String regionAlias;
	private String levelProp;
	private String pyCode;
	private String mapSign;
	private int orderNo;
	private String isBottom;
	private String isFamily;
	private String GPS;

	public AreaGrid() {

	}

	public AreaGrid(String regionName,String regionCode) {
		super();
		this.regionName = regionName;
		this.regionCode = regionCode;
	}

	public int getNumKey() {
		return numKey;
	}

	public void setNumKey(int numKey) {
		this.numKey = numKey;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getRegionNo() {
		return regionNo;
	}

	public void setRegionNo(String regionNo) {
		this.regionNo = regionNo;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionAlias() {
		return regionAlias;
	}

	public void setRegionAlias(String regionAlias) {
		this.regionAlias = regionAlias;
	}

	public String getLevelProp() {
		return levelProp;
	}

	public void setLevelProp(String levelProp) {
		this.levelProp = levelProp;
	}

	public String getPyCode() {
		return pyCode;
	}

	public void setPyCode(String pyCode) {
		this.pyCode = pyCode;
	}

	public String getMapSign() {
		return mapSign;
	}

	public void setMapSign(String mapSign) {
		this.mapSign = mapSign;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getIsBottom() {
		return isBottom;
	}

	public void setIsBottom(String isBottom) {
		this.isBottom = isBottom;
	}

	public String getIsFamily() {
		return isFamily;
	}

	public void setIsFamily(String isFamily) {
		this.isFamily = isFamily;
	}

	public String getGPS() {
		return GPS;
	}

	public void setGPS(String gPS) {
		GPS = gPS;
	}

	public String getParentCode1() {
		return parentCode1;
	}

	public void setParentCode1(String parentCode1) {
		this.parentCode1 = parentCode1;
	}

	public String getParentCode2() {
		return parentCode2;
	}

	public void setParentCode2(String parentCode2) {
		this.parentCode2 = parentCode2;
	}

	public String getParentCode3() {
		return parentCode3;
	}

	public void setParentCode3(String parentCode3) {
		this.parentCode3 = parentCode3;
	}

	public String getParentCode4() {
		return parentCode4;
	}

	public void setParentCode4(String parentCode4) {
		this.parentCode4 = parentCode4;
	}

	public String getParentCode5() {
		return parentCode5;
	}

	public void setParentCode5(String parentCode5) {
		this.parentCode5 = parentCode5;
	}

	public String getParentName1() {
		return parentName1;
	}

	public void setParentName1(String parentName1) {
		this.parentName1 = parentName1;
	}

	public String getParentName2() {
		return parentName2;
	}

	public void setParentName2(String parentName2) {
		this.parentName2 = parentName2;
	}

	public String getParentName3() {
		return parentName3;
	}

	public void setParentName3(String parentName3) {
		this.parentName3 = parentName3;
	}

	public String getParentName4() {
		return parentName4;
	}

	public void setParentName4(String parentName4) {
		this.parentName4 = parentName4;
	}

	public String getParentName5() {
		return parentName5;
	}

	public void setParentName5(String parentName5) {
		this.parentName5 = parentName5;
	}

	@Override
	public String toString() {
		return "AreaGrid [numKey=" + numKey + ", regionCode=" + regionCode
				+ ", parentCode=" + parentCode + ", parentCode1=" + parentCode1
				+ ", parentCode2=" + parentCode2 + ", parentCode3="
				+ parentCode3 + ", parentCode4=" + parentCode4
				+ ", parentCode5=" + parentCode5 + ", regionName=" + regionName
				+ ", parentName1=" + parentName1 + ", parentName2="
				+ parentName2 + ", parentName3=" + parentName3
				+ ", parentName4=" + parentName4 + ", parentName5="
				+ parentName5 + ", regionNo=" + regionNo + ", regionAlias="
				+ regionAlias + ", levelProp=" + levelProp + ", pyCode="
				+ pyCode + ", mapSign=" + mapSign + ", orderNo=" + orderNo
				+ ", isBottom=" + isBottom + ", isFamily=" + isFamily
				+ ", GPS=" + GPS + "]";
	}
	
	
	
	
	
}
