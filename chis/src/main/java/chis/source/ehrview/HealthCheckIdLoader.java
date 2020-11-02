/**
 * @(#)HealthCheckIdLoader.java Created on 2012-4-23 上午08:27:29
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
public class HealthCheckIdLoader extends AbstractIdLoader {

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
		return "HC_HealthCheck";
	}
	
	@Override
	public String getEntityName() {
		return HC_HealthCheck;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "phrId";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "HC_HealthCheck.phrId";
	}

	/**
	 * @throws ServiceException
	 * @see chis.source.biz.ehrview.IdLoader#getControlData()
	 */
	public Map<String, Boolean> getControlData(Map<String, Object> data,
			Context ctx) throws ServiceException {
		return ControlRunner.run(getEntityName(), data, ctx,
				ControlRunner.UPDATE);
	}

	public Map<String, Object> load(String loadBy, Context ctx)
			throws ServiceException {
		return new HashMap<String, Object>();
	}
}
