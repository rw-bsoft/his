/**
 * @(#)OldPeopleRecordIdLoader.java Created on Mar 16, 2010 7:30:36 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class FdsrRecordIdLoader extends AbstractIdLoader {


	/**
	 * @see
	 */
	public String getEntryName() {
		return "FDSR";
	}
	

	@Override
	public String getEntityName() {
		return FDSR;
	}

	/**
	 * @see
	 */
	public String getIdColumn() {
		return "phrId";
	}

	/**
	 * @see
	 */
	public String getIdName() {
		return "FDSR.phrId";
	}

	/**
	 * @see
	 */
	public boolean hasStatus() {
		return true;
	}

}
