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
 * @description 二级库房出库库管理service
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class EquipmentWeighingManagementService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJlxxListDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doQueryJlxxListDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("需要登记的计量信息查询失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJlsb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doQueryJlsb(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("需要登记的计量信息查询失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJlxxInfoDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doQueryJlxxInfoDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询已登记的计量信息查询失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryWZXXList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doQueryWZXXList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询已登记的计量信息查询失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJLXXUPDATE(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doQueryJLXXUPDATE(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询已登记的计量信息查询失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveJLXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		String wzsl = req.get("wzsl") + "";
		try {
			ewmm.doSaveJLXX(body, res, op, wzsl, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("计量管理保存失败！", e);
		}
	}

	/**
	 * 
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateJLXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		try {
			ewmm.doUpdateJLXX(body, res, op, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("计量管理修改失败！", e);
		}
	}

	public void doRemoveEquipment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EquipmentWeighingManagementModel ewmm = new EquipmentWeighingManagementModel(
				dao);
		try {
			ewmm.doRemoveEquipment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("作废失败！", e);
		}
	}

}
