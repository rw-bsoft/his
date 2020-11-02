package phis.application.cfg.source;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigAccountingCategoryModuleService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);

	public void doQueryNodInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigAccountingCategoryModule cbcm = new ConfigAccountingCategoryModule(dao);
		cbcm.queryNodInfo(req, res);
	}

	public void doSaveNodInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		ConfigAccountingCategoryModule cbcm = new ConfigAccountingCategoryModule(dao);
		cbcm.saveNodInfo(req, res, ctx);
	}

	public void doUpdateNodInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		ConfigAccountingCategoryModule cbcm = new ConfigAccountingCategoryModule(dao);
		cbcm.updateNodInfo(req, res, ctx);
	}

	public void doDeleteNode(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigAccountingCategoryModule cbcm = new ConfigAccountingCategoryModule(dao);
		cbcm.deleteNode(req, res);
	}

	@SuppressWarnings("unchecked")
	public void doGetHslb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ConfigAccountingCategoryModule cbcm = new ConfigAccountingCategoryModule(dao);
		cbcm.doGetHslb(body, res);
	}
}
