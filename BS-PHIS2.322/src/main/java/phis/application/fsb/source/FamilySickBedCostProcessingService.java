package phis.application.fsb.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public class FamilySickBedCostProcessingService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedCostProcessingService.class);

	/**
	 * 病人、费用信息查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryItemInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryItemInfo(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("信息查询失败！",e);
		}
	}
	/**
	 * 格式化数字
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doNumberFormat(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		hpm.doNumberFormat(req,res,ctx);
	}
	
	/**
	 * 获取自负比例
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryCost(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryCost(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取自负比例失败！",e);
		}
	}
	
	/**
	 * 费用明细保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveCost(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doSaveCost(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	/**
	 * 退费信息查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryRefundInfo(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryRefundInfo(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("费用明细查询失败！",e);
		}
	}
	public void doQueryRefundInfo1(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryRefundInfo1(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("费用明细查询失败！",e);
		}
	}
	public void doQueryCostList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryCostList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("费用明细查询失败！",e);
		}
	}
	public void doQueryCostMx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		try {
			hpm.doQueryCostMx(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("费用明细查询失败！",e);
		}
	}
	
	public void doHospitalCostMxQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		hpm.doHospitalCostMxQuery(req,res,ctx,dao);
	}
	public void doHospitalCostDetalsQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		hpm.doHospitalCostDetalsQuery(req,res,ctx,dao);
	}
	
	public void doHospitalCostSumQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		FamilySickBedCostProcessingModel hpm = new FamilySickBedCostProcessingModel(dao);
		hpm.doHospitalCostSumQuery(req,res,ctx,dao);
	}

}
