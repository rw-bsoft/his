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

public class ConfigAntibioticsPermissionsService extends AbstractActionService implements
DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(ConfigAntibioticsPermissionsService.class);

	/**
	 * 抗生素权限保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSaveAntibioticsPermissions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		ConfigAntibioticsPermissionsModel capm = new ConfigAntibioticsPermissionsModel(dao);
		try {
			capm.doSaveAntibioticsPermissions(req,dao);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 抗生素权限删除 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doRemoveAntibioticsPermissions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		ConfigAntibioticsPermissionsModel capm = new ConfigAntibioticsPermissionsModel(dao);
		try {
			capm.doRemoveAntibioticsPermissions(req, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
}
