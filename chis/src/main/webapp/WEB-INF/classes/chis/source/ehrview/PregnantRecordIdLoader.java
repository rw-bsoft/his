/**
 * @(#)PregnantRecordIdLoader.java Created on Mar 16, 2010 9:10:58 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PregnantRecordIdLoader extends AbstractIdLoader {

	public String getLoadBy() {
		return "pregnantId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "MHC_PregnantRecord";
	}
	
	@Override
	public String getEntityName() {
		return MHC_PregnantRecord;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "pregnantId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "MHC_PregnantRecord.pregnantId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}

}
