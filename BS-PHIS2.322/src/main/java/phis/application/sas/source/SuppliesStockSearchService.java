package phis.application.sas.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class SuppliesStockSearchService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 库存明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStockDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryStockDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("库存明细查询失败！", e);
		}
	}

	/**
	 * 库存明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStockEjDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryStockEjDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("库存明细查询失败！", e);
		}
	}

	/**
	 * 库存汇总
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStockCollect(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryStockCollect(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("库存汇总查询失败！", e);
		}
	}

	/**
	 * 库存汇总
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStockEjCollect(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryStockEjCollect(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("库存汇总查询失败！", e);
		}
	}
	
	/**
	 * 一级库房查看二级库房库存汇总
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryStockEjCollectByKfxh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryStockEjCollectByKfxh(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("库存汇总查询失败！", e);
		}
	}

	/**
	 * 科室账册——明细查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDepartmentBooksDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDepartmentBooksDetails(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("科室明细查询失败！", e);
		}
	}

	/**
	 * 科室账册——汇总查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDepartmentBooksCollect(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDepartmentBooksCollect(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}

	/**
	 * 固定置产查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryFixedAssetsList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryFixedAssetsList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}

	/**
	 * 入库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDocumentDetailforRK(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDocumentDetailforRK(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("入库明细查询失败！", e);
		}
	}

	/**
	 * 出库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDocumentDetailforCK(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDocumentDetailforCK(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("出库明细查询失败！", e);
		}
	}
	
	/**
	 * 二级库房出库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDocumentDetailforEJCK(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDocumentDetailforEJCK(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("出库明细查询失败！", e);
		}
	}

	/**
	 * 转科查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryChangeDepartments(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryChangeDepartments(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}
	/**
	 * 二级库房出库
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDocumentDetailforEjRK(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQueryDocumentDetailforEjRK(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}
	/**
	 *  低储预警查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerySuppliesStockLowWarning(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQuerySuppliesStockLowWarning(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}
	/**
	 *  高储预警查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerySuppliesStockHighWarning(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQuerySuppliesStockHighWarning(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}
	/**
	 *  库存失效预警
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuerySuppliesStockExpirationWarning(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.doQuerySuppliesStockExpirationWarning(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("汇总查询查询失败！", e);
		}
	}

	/**
	 * 
	 * @description 物资账簿类别获取
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetZblbByKfxh(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		SuppliesStockSearchModel smm = new SuppliesStockSearchModel(dao);
		try {
			smm.getZblbByKfxh(body, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
