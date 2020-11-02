package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberService;
import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;



import ctd.util.context.Context;

public class ConfigInventoryInitialService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberService.class);

	@SuppressWarnings("unchecked")
	public void doSaveInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> ListBody = (List<Map<String, Object>>) req
				.get("listBody");
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doSaveInventoryIn(body, ListBody, res, dao, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doSaveDepartmentsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> ListBody = (List<Map<String, Object>>) req
				.get("listBody");
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doSaveDepartmentsIn(body, ListBody, res, dao, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doSaveAssetsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> ListBody = (List<Map<String, Object>>) req
				.get("listBody");
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doSaveAssetsIn(body, ListBody, res, dao, ctx);
	}

	public void doBackfillingInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doBackfillingInventoryIn(req, res, dao, ctx);
	}

	public void doBackfillingAssetsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doBackfillingAssetsIn(req, res, dao, ctx);
	}

	public void doDeleteInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doDeleteInventoryIn(req, res, dao, ctx);
	}
	public void doDeleteAssetsIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doDeleteAssetsIn(req, res, dao, ctx);
	}
	public void doSaveTransferInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) {
		ConfigInventoryInitialModule ciim = new ConfigInventoryInitialModule(
				dao);
		ciim.doSaveTransferInventoryIn(req, res, dao, ctx);
	}
}
