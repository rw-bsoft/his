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

public class ConfigHsqxYgService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigHsqxYgService.class);

	/**
	 * 护士权限保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHSQX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			chym.doSaveHSQX(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 护士权限保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateHSZBZ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			chym.doUpdateHSZBZ(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 护士权限保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveHSQXKS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		try {
			chym.doSaveHSQXKS(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 处方组套启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateMRKS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
			chym.doUpdateMRKS(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetKSDMInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		chym.doGetKSDMInfo(req, res, ctx);
	}
	/**
	 * 二级库房对照--所有科室
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetKSDMForEJInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		chym.doGetKSDMForEJInfo(req, res, ctx);
	}
	
	/**
	 * 二级库房科室对照
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveKFDZ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigHsqxYgModel chym = new ConfigHsqxYgModel(dao);
		try {
			chym.doSaveKFDZ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
