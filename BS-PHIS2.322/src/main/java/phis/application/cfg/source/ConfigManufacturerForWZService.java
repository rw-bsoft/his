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

public class ConfigManufacturerForWZService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigManufacturerForWZService.class);

	/**
	 * 生产厂家维护——查询列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doManufacturerQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		try {
			advice.doManufacturerQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("生产厂家列表查询失败！", e);
		}
	}
	/**
	 * 生产厂商维护——增加 && 修改
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOperatForManufacturer(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException, ServiceException { 
		try {
			ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
			String schemaDetailsList = req.get("schemaDetailsList") + "";
			String op = (String) req.get("op");
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			advice.doOperatForManufacturer(body, res, schemaDetailsList, op, dao,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	/**
	 * 对照
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCompar(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		try {
			advice.doSaveCompar(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 修改状态
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCanceled(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
			advice.doCanceled(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
 
	/**
	 *查看物质列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doManufacturerForWZQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		try {
			advice.doManufacturerForWZQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("物质列表查询失败！", e);
		}
	}
	/**
	 *查看物质列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doManufacturerForSCCJQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		try {
			advice.doManufacturerForSCCJQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("物质列表查询失败！", e);
		}
	}
	/**
	 *查看证件信息列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doCertificateQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		try {
			advice.doCertificateQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查看证件信息列表！", e);
		}
	}
	
	public void doQueryCheckWZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigManufacturerForWZModel advice = new ConfigManufacturerForWZModel(dao);
		advice.doQueryCheckWZ(req,res,ctx);
	}
	
	
}
