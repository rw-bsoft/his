package phis.application.sup.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ApplyRegisterService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * @description 申领登记保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveform(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ApplyRegisterModel smm = new ApplyRegisterModel(dao);
		try {
			smm.doSaveform(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 提交
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateSlZT(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> bodys = (List<Map<String, Object>>) req
				.get("body");
		ApplyRegisterModel smm = new ApplyRegisterModel(dao);
		smm.doUpdateSlZT(bodys, ctx);
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
	public void doRemove(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ApplyRegisterModel smm = new ApplyRegisterModel(dao);
		try {
			smm.doRemove(req, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 得到对应的物资在 所在库房的WL_WZKC中SUM(WZSL - YKSL)。
	 * 
	 * @throws ServiceException
	 */
	public void doQueryYksl(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ApplyRegisterModel cbcm = new ApplyRegisterModel(dao);
		cbcm.doQueryYksl(req, res);
	}

	/**
	 * 获取系统参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadSystemParams(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ApplyRegisterModel cbcm = new ApplyRegisterModel(dao);
		try {
			res.put("ret", (cbcm.loadSystemParams(body, ctx)));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryWzkf(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ApplyRegisterModel cbcm = new ApplyRegisterModel(dao);
		try {
			res.put("ret", (cbcm.doQueryWzkf(body, ctx)));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryKs(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ApplyRegisterModel cbcm = new ApplyRegisterModel(dao);
		try {
			cbcm.doQueryKs(ctx, res);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

}
