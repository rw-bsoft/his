package phis.application.cfg.source;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigMaterialInformationManagementService extends
		AbstractActionService implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);

	public void doQueryLBXH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doQueryLBXH(req, res);
	}

	public void doQueryJLXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doQueryJLXX(req, res);
	}

	public void doSaveData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doSaveData(req, res, ctx);
	}

	public void doQueryIfCanInvalid(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doQueryIfCanInvalid(req, res);
	}

	public void doGetList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doGetList(req, res, ctx);
	}

	public void doGetMaterialInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doGetMaterialInformation(req, res, ctx);
	}

	public void doGetMaterialInformationEjkf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ValidateException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doGetMaterialInformationEjkf(req, res, ctx);
	}

	/**
	 * 物资引入的保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCallin(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		List<Object> body = (List<Object>) req.get("body");
		try {
			cbcm.doSaveCallin(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "物资引入成功");
	}

	/**
	 * 物资引入的保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveGDCMaterialInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			cbcm.doSaveGDCMaterialInfo(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "修改高低储成功");
	}

	/**
	 * 二级库房物资引入的保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCallinEjkf(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		List<Object> body = (List<Object>) req.get("body");
		try {
			cbcm.doSaveCallinEjkf(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "物资引入成功");
	}

	/**
	 * 物资注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateCanceledMaterial(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doUpdateCanceledMaterial(body, schemaList, res, dao, ctx);
	}

	public void doGetEJJKInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doGetEJJKInfo(req, res, ctx);
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-10
	 * @description 修改库存预警的高低储
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateKCYJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		try {
			cbcm.doUpdateKCYJ(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 二级建库注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateCanceledEjjk(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String schemaList = req.get("schemaList") + "";
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doUpdateCanceledEjjk(body, schemaList, res, dao, ctx);
	}

	/**
	 * 查询所有核算类别
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doQueryHSLB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigMaterialInformationManagementModule cbcm = new ConfigMaterialInformationManagementModule(
				dao);
		cbcm.doQueryHSLB(req, res);
	}
}
