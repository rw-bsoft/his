/*
 * @(#)AdapterFactory.java Created on 2011-12-29 下午2:31:59
 *
 * 版权：版权所有 Bsoft 保留所有权力。
 */
package chis.source.visitplan.adapter;

import java.util.Map;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;

import chis.source.Constants;
import chis.source.dic.PlanMode;
import chis.source.util.ApplicationUtil;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class AdapterFactory {

	private Map<String, BusinessAdapter> adapters;

	public Map<String, BusinessAdapter> getAdapters() {
		return adapters;
	}

	public void setAdapters(Map<String, BusinessAdapter> adapters) {
		this.adapters = adapters;
	}

	/**
	 * @param businessType
	 * @return
	 * @throws ServiceException
	 */
	public BusinessAdapter getAdapter(String businessType)
			throws ServiceException {
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					businessType + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		if (planMode == null || PlanMode.BY_PLAN_TYPE.equals(planMode)
				|| PlanMode.BY_PLAN_MONTH.equals(planMode)) {
			return adapters.get(businessType);
		}
		// @@ 如果有业务模块专用的按预约生成计划的Adapter那么就使用这个专用的，否则使用通用的按预约生成计划的Adapter。
		if (PlanMode.BY_RESERVED.equals(planMode)) {
			BusinessAdapter adapter = adapters.get(businessType + "_planMode="
					+ planMode);
			return adapter == null ? adapters.get("planMode=" + planMode)
					: adapter;
		}
		return null;
	}
}
