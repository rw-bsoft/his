package phis.application.fsb.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class FSBNurseRecordService extends AbstractActionService implements
		DAOSupportable {
	public void doQueryRecordMete(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.queryRecordMete(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSave(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			Object object = req.get("PARAM");
			if (object != null) {
				Map<String, Object> parMap = (Map<String, Object>) object;
				if (parMap.get("JLBH") != null) {// 记录编号不为空时，表示更新动作
					module.update(req, res, ctx);
				} else {// 记录编号为空时表示新增动作
					module.save(req, res, ctx);
				}
			}

			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据记录编号获取护理明细表单
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryENR_JL02ByJLBH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.queryENR_JL02ByJLBH(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除护理记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteENR_JL(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.deleteENR_JL01(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doSaveHLJH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.doSaveHLJH(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doRemoveHLJH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.doRemoveHLJH(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doSaveHLJL(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.doSaveHLJL(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doRemoveHLJL(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FSBNurseRecordModule module = new FSBNurseRecordModule(dao);
		try {
			module.doRemoveHLJL(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
