package phis.application.sup.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @description 采购计划service
 * 
 */
public class ProcurementPlanService extends AbstractActionService implements
		DAOSupportable {
	
	
	/**
	 * 采购计划结果查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doPlanImportQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doPlanImportQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("供货单位列表查询失败", e);
		}
	}

	/**
	 * 
	 * @description 物资入库单记录保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckIn(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.saveCheckIn(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDjztByDjxh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doGetDjztByDjxh(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 审核
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doVerify(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 弃审
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doCancelVerify(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	
	/**
	 *物资列表查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doKCQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doKCQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("供货单位列表查询失败", e);
		}
	}
	
	/**
	 * 删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDelete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ProcurementPlanModel ppm = new ProcurementPlanModel(dao);
		try {
			ppm.doDelete(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("删除失败！", e);
		}
	}

}
