/**
 * @(#)DiabetesFixGroupIdLoader.java Created on Mar 16, 2010 8:50:01 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

/**
 * @description  
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class DiabetesFixGroupIdLoader extends AbstractIdLoader {

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return MDC_DiabetesFixGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.hzehr.biz.ehrview.AbstractIdLoader#getManaInfoEntryName()
	 */
	public String getManaInfoEntryName() {
		return "MDC_DiabetesRecord";
	}
	
	@Override
	public String getEntityName() {
		return MDC_DiabetesRecord;
	}
	
	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "fixId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "MDC_DiabetesFixGroup.fixId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return false;
	}
}
