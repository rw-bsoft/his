package phis.application.yb.source;

/**
 * 医保操作mdoe类集合 
 * @author Administrator
 *
 */
public enum YbModelType {

    ZQ("zq", "肇庆医保", MedicareModel4ZQYB.class),
    SG("sg", "韶关医保", MedicareModel4SGYB.class);
    
    public String getmDisplayName() {
		return mDisplayName;
	}

	public void setmDisplayName(String mDisplayName) {
		this.mDisplayName = mDisplayName;
	}

	public String getmDescriptionId() {
		return mDescriptionId;
	}

	public void setmDescriptionId(String mDescriptionId) {
		this.mDescriptionId = mDescriptionId;
	}

	public Class<? extends MedicareModel> getmClass() {
		return mClass;
	}

	public void setmClass(Class<? extends MedicareModel> mClass) {
		this.mClass = mClass;
	}

	private String mDisplayName;
    private String mDescriptionId;
    private Class<? extends MedicareModel> mClass;
    
    YbModelType(String displayName, String descriptionId,
            Class<? extends MedicareModel> klass) {
        mDisplayName = displayName;
        mDescriptionId = descriptionId;
        mClass = klass;
    }
}