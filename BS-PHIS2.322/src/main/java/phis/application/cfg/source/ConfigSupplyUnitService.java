package phis.application.cfg.source;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;



import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @description 供货单位维护
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ConfigSupplyUnitService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);

	/**
	 * 取消注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateConfigSupplyUnitNormal(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
			csum.doUpdateConfigSupplyUnitNormal(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateConfigSupplyUnitCancel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
			csum.doUpdateConfigSupplyUnitCancel(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除供货单位
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDeleteConfigSupplyUnit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
			csum.doDeleteConfigSupplyUnit(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断供货单位是否在用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doIsUnitUsed(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
			csum.doIsUnitUsed(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存供货单位信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSupplyUnitInfomations(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String op = (String) req.get("op");
			if (StringUtils.isEmpty(op)) {
				op = "create";
			}
			ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
			csum.doSaveSupplyUnitInfomations(op, body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据单位序号取得单位状态
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDWZT(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			csum.doGetDWZT(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 供货单位列表查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSupplyUnitQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
		try {
			csum.doSupplyUnitQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("供货单位列表查询失败！", e);
		}
	}

	/**
	 * 保存图片信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePhoto(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			csum.doSavePhoto(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除图片信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemovePhoto(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			csum.doRemovePhoto(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据单位序号取得JGID
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetJGID(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigSupplyUnitModel csum = new ConfigSupplyUnitModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			csum.doGetJGID(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
