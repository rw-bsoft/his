package phis.application.sup.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class InventoryService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(InventoryService.class);

	public void doGetWzkcInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetWzkcInfo(req, res, ctx);
	}

	public void doGetPD02Info(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetPD02Info(req, res, ctx);
	}

	public void doGetPD02KSInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetPD02KSInfo(req, res, ctx);
	}

	public void doGetPD02TZInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetPD02TZInfo(req, res, ctx);
	}

	public void doGetInventoryKSInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetInventoryKSInfo(req, res, ctx);
	}

	@SuppressWarnings("unchecked")
	protected void doGetInventoryKSZC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		InventoryModel im = new InventoryModel(dao);

		List<Object> body = (List<Object>) req.get("body");
		try {
			im.doGetInventoryKSZC(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doGetInventoryTZKSInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetInventoryTZKSInfo(req, res, ctx);
	}

	@SuppressWarnings("unchecked")
	protected void doGetInventoryZCZB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		InventoryModel im = new InventoryModel(dao);

		List<Object> body = (List<Object>) req.get("body");
		try {
			im.doGetInventoryZCZB(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doGetInventoryWZZDInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetInventoryWZZDInfo(req, res, ctx);
	}

	@SuppressWarnings("unchecked")
	protected void doGetInventoryWZZD(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		InventoryModel im = new InventoryModel(dao);

		List<Object> body = (List<Object>) req.get("body");
		try {
			im.doGetInventoryWZZD(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventory(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveInventoryCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventoryCommit(body, ctx, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetInventoryDJZT(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doGetInventoryDJZT(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetInventoryDJSH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doGetInventoryDJSH(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetInventoryDJSHKS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doGetInventoryDJSHKS(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 判断盘点单有没有录入
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPDLR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doQueryPDLR(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 盘点单提交
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventoryVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventoryVerify(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 盘点单提交
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventoryNoVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventoryNoVerify(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetPDGLInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetPDGLInfo(req, res, ctx);
	}

	@SuppressWarnings("unchecked")
	protected void doGetInventoryPD02KS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		InventoryModel im = new InventoryModel(dao);

		List<Object> body = (List<Object>) req.get("body");
		try {
			im.doGetInventoryPD02KS(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doGetInventoryPD02TZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		InventoryModel im = new InventoryModel(dao);

		List<Object> body = (List<Object>) req.get("body");
		try {
			im.doGetInventoryPD02TZ(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventoryIn(body, op, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetLRMXInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetLRMXInfo(req, res, ctx);
	}

	public void doGetLRMXKSInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetLRMXKSInfo(req, res, ctx);
	}

	public void doGetLRMXTZInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		InventoryModel im = new InventoryModel(dao);
		im.doGetLRMXTZInfo(req, res, ctx);
	}

	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetInventoryInDJZT(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doGetInventoryInDJZT(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @description 单据状态获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSubitVerify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveSubitVerify(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveInventoryEjCommit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		try {
			im.doSaveInventoryEjCommit(body, ctx, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryPDXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		im.doQueryPDXX(body, res, ctx);
	}

	public void doQueryPDXXEJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		InventoryModel im = new InventoryModel(dao);
		im.doQueryPDXXEJ(res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doQueryPDLRXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		im.doQueryPDLRXX(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doRemoveInventory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		im.doRemoveInventory(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doRemoveInventoryIn(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		InventoryModel im = new InventoryModel(dao);
		im.doRemoveInventoryIn(body, res, ctx);
	}

	public void doQueryDJSH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		InventoryModel im = new InventoryModel(dao);
		im.doQueryDJSH(req,res, ctx);
	}

}
