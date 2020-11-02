/**
 * @(#)HealthCardIdLoader.java Created on Mar 16, 2010 8:52:21 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class HealthCardIdLoader extends AbstractIdLoader {

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "CDH_HealthCard";
	}
	
	@Override
	public String getEntityName() {
		return CDH_HealthCard;
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
		return "CDH_HealthCard.phrId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}
	
}
