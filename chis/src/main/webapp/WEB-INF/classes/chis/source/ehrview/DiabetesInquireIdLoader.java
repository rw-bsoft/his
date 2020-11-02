/**
 * @(#)DiabetesIdLoader.java Created on Mar 16, 2010 8:43:36 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;


/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class DiabetesInquireIdLoader extends AbstractIdLoader {


	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "MDC_DiabetesInquire";
	}
	
	@Override
	public String getEntityName() {
		return MDC_DiabetesInquire;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "inquireId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "MDC_DiabetesInquire.inquireId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return false;
	}
}
