/**
 * @(#)HealthRecordIdLoader.java Created on Mar 16, 2010 6:53:43 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class HealthRecordIdLoader extends AbstractIdLoader {



	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "EHR_HealthRecord";
	}
	
	@Override
	public String getEntityName() {
		return EHR_HealthRecord;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "phrId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "phrId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}

}
