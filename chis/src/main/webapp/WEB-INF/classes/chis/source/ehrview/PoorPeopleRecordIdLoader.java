/**
 * @(#)PoorPeopleRecordIdLoader.java Created on 2012-4-6 上午10:35:34
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.Map;

import chis.source.control.ControlRunner;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class PoorPeopleRecordIdLoader extends AbstractIdLoader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#hasStatus()
	 */
	@Override
	public boolean hasStatus() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return "EHR_PoorPeopleVisit";
	}
	
	@Override
	public String getEntityName() {
		return EHR_PoorPeopleVisit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "visitId";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "EHR_PoorPeopleVisit.visitId";
	}

	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException {
		return new HashMap<String, Object>();
	}

	/**
	 * 获取新建按钮控制
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException {
		return ControlRunner.run(getEntityName(), data, ctx,
				ControlRunner.CREATE);
	}
}
