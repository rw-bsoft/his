/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 收费项目维护
 * 
 * @author shiwy 2012.06.29
 */
public class ConfigChargingProjectsSaveService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 收费项目保存。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChargingProjects(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException {
		try {
			ConfigChargingProjectsSaveModel ccpm = new ConfigChargingProjectsSaveModel(dao);
			String op = (String) req.get("op");
			Map<String, Object> body = (Map<String, Object>) req
					.get("body");
				ccpm.doSaveChargingProjects(op,res, body,ctx);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
