/**
 * @description 收费项目
 * 
 * @author shiwy 2012.06.29
 */
package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigChargingProjectsDelService extends AbstractActionService
		implements DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsDelService.class);

	/**
	 * 收费项目删除。删除收费项目的同时也要删除病人性质里引用的收费项目
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveChargingProjects(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		int sfxm = Integer.parseInt(((Map<String, Object>) req.get("body"))
				.get("sfxm") + "");
		try {
			ConfigChargingProjectsDelModel ccpm = new ConfigChargingProjectsDelModel(
					dao);
			ccpm.removeChargingProjects(sfxm, res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
