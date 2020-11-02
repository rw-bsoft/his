/**
 * @(#)VaccinateRecordIdLoader.java Created on Mar 16, 2010 8:51:10 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class VaccinateRecordIdLoader extends AbstractIdLoader {

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "PIV_VaccinateRecord";
	}
	
	@Override
	public String getEntityName() {
		return PIV_VaccinateRecord;
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
		return "PIV_VaccinateRecord.phrId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return false;
	}

}
