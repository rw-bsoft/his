/**
 * @(#)DebilityChildrenIdLoader.java Created on Mar 16, 2010 8:55:25 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.Map;

import chis.source.control.ControlRunner;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class DebilityChildrenIdLoader extends AbstractIdLoader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.hzehr.biz.ehrview.AbstractIdLoader#getLoadBy()
	 */
	public String getLoadBy() {
		return "recordId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getEntryName()
	 */
	public String getEntryName() {
		return "CDH_DebilityChildren";
	}
	
	@Override
	public String getEntityName() {
		return CDH_DebilityChildren;
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdColumn()
	 */
	public String getIdColumn() {
		return "recordId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#getIdName()
	 */
	public String getIdName() {
		return "CDH_DebilityChildren.recordId";
	}

	/**
	 * @see com.bsoft.hzehr.biz.ehrview.IdLoader#hasStatus()
	 */
	public boolean hasStatus() {
		return true;
	}

	/**
	 * @throws ServiceException
	 * @see chis.source.biz.ehrview.IdLoader#getControlData()
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException {
		return ControlRunner.run(getEntityName(), data, ctx, ControlRunner.ALL);
	}

}
