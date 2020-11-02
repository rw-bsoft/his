package phis.application.twr.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ReceiveRecordService extends AbstractActionService implements DAOSupportable{
	
	/**
	 * 根据病人empiid和转诊单号取转诊信息 
	 * liulichuang 
	 * 2013-11-24
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetRecordHistoryByEmpiidAndDh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ReceiveRecordModel umm = new ReceiveRecordModel(dao);
		try {
			res.put("body", umm.getRecordHistoryByEmpiidAndDh(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据病人empiid和检查申请单号取检查申请信息 
	 * liulichuang
	 * 2013-11-24
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetCheckHistoryByEmpiidAndDh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ReceiveRecordModel umm = new ReceiveRecordModel(dao);
		try {
			res.put("body", umm.getCheckHistoryByEmpiidAndDh(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据病人empiid和检查申请单号取设备预约信息 
	 * liulichuang
	 * 2013-11-24
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetEquipmentRecordByEmpiidAndDh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ReceiveRecordModel umm = new ReceiveRecordModel(dao);
		try {
			res.put("body", umm.getEquipmentRecordByEmpiidAndDh(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据病人empiid和检查申请单号取当天挂号信息 
	 * liulichuang
	 * 2013-11-24
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetRegisterHistoryByEmpiidAndDh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ReceiveRecordModel umm = new ReceiveRecordModel(dao);
		try {
			res.put("body", umm.getRegisterHistoryByEmpiidAndDh(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据病人empiid和检查申请单号取当天挂号信息 
	 * liulichuang
	 * 2013-11-24
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetRegisterReqHistoryByEmpiidAndDh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ReceiveRecordModel umm = new ReceiveRecordModel(dao);
		try {
			res.put("body", umm.getRegisterReqHistoryByEmpiidAndDh(body, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
 
}
