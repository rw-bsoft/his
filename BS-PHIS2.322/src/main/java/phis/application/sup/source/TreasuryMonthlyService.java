package phis.application.sup.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;



import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class TreasuryMonthlyService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTreasuryMonthly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TreasuryMonthlyModel tmm = new TreasuryMonthlyModel(dao);
		try {
			Map<String, Object> ret = tmm.doSaveTreasuryMonthly(body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTreasuryEjMonthly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TreasuryMonthlyModel tmm = new TreasuryMonthlyModel(dao);
		try {
			Map<String, Object> ret = tmm.doSaveTreasuryEjMonthly(body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDeleteTreasuryMonthly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TreasuryMonthlyModel tmm = new TreasuryMonthlyModel(dao);
		try {
			Map<String, Object> ret = tmm.doDeleteTreasuryMonthly(body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDeleteTreasuryEjMonthly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TreasuryMonthlyModel tmm = new TreasuryMonthlyModel(dao);
		try {
			Map<String, Object> ret = tmm.doDeleteTreasuryEjMonthly(body, ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doQueryYJJLInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		TreasuryMonthlyModel tmm = new TreasuryMonthlyModel(dao);
		tmm.doQueryYJJLInfo(req, res, ctx);
	}
}
