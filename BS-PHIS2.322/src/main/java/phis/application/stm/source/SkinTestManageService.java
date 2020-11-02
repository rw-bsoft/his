package phis.application.stm.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class SkinTestManageService extends AbstractActionService implements
		DAOSupportable {

	public void doLoadPatient(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		SkinTestManageModel m = new SkinTestManageModel(dao);
		try {
			res.put("body", m.loadPatient(req.get("pkey")));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("皮试病人查询失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveStartSkinTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SkinTestManageModel m = new SkinTestManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			m.doSaveStartSkinTest(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveStopSkinTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SkinTestManageModel m = new SkinTestManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			m.doSaveStopSkinTest(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveSkinTestResult(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SkinTestManageModel m = new SkinTestManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			m.doSaveSkinTestResult(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doRemoveSkinTest(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SkinTestManageModel m = new SkinTestManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			m.doRemoveSkinTest(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	

}