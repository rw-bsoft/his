package phis.application.sup.source;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DepreciatedManagementService extends AbstractActionService
		implements DAOSupportable {
	/**
	 * 
	 * @description
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryZJXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DepreciatedManagementModel dmm = new DepreciatedManagementModel(dao);
		try {
			dmm.doQueryZJXX(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZJXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DepreciatedManagementModel dmm = new DepreciatedManagementModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		String cwyf = req.get("cwyf") + "";
		try {
			dmm.doSaveZJXX(body, res, cwyf, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateZJXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DepreciatedManagementModel dmm = new DepreciatedManagementModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		String cwyf = req.get("cwyf") + "";
		try {
			dmm.doUpdateZJXX(body, res, cwyf, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doQueryBYZJXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DepreciatedManagementModel dmm = new DepreciatedManagementModel(dao);
		String cwyf = req.get("cwyf") + "";
		try {
			dmm.doQueryBYZJXX(req, res, cwyf, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
