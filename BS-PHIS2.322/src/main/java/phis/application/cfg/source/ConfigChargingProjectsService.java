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

public class ConfigChargingProjectsService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsService.class);

	@SuppressWarnings("unchecked")
	public void doChargingProjectsVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigChargingProjectsModel ccpm = new ConfigChargingProjectsModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ccpm.chargingProjectsVerification(body, res, schemaDetailsList, op,
				dao, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doLogoutMedicalItems(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long fyxh = Long.parseLong(body.get("FYXH") + "");
		int zfpbVlaue = (Integer) body.get("ZFPB");
		int zfpb;
		if (zfpbVlaue == 0) {
			zfpb = 1;
		} else {
			zfpb = 0;
		}
		ConfigChargingProjectsModel ccpm = new ConfigChargingProjectsModel(dao);
		try {
			int i = ccpm.logoutMedicalItems(fyxh, zfpb);
			res.put(RES_CODE, i);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
