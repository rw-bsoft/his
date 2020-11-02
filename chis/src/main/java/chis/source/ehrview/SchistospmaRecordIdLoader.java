/**
 * @(#)TumourIdLoader.java Created on Mar 16, 2010 8:48:35 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:liyl@bsoft.com.cn">liyl</a>
 */
public class SchistospmaRecordIdLoader extends AbstractIdLoader {

	// public static final String MANA_DOCTOR_ID = "inputUser";
	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "SCH_SchistospmaRecord";
	}
	
	@Override
	public String getEntityName() {
		return SCH_SchistospmaRecord;
	}


	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "schisRecordId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "SCH_SchistospmaRecord.schisRecordId";
	}

	public String getManaInfoEntryName() {
		return EHR_HealthRecord;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasCloseFlag()
	 */
	public boolean hasCloseFlag() {
		return true;
	}

}
