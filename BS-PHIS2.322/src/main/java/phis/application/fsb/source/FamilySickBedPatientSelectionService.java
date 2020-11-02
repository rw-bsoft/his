package phis.application.fsb.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class FamilySickBedPatientSelectionService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedPatientSelectionService.class);

	/**
	 * 获取病人列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetPatientList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetPatientList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取病人列表失败！",e);
		}
	}
	
	public void doQuerySelectionForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doQuerySelectionForm(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	
	public void doQuerySelectionList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doQuerySelectionList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取费用帐卡
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetSelectionForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetSelectionForm(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	/**
	 * 获取费用帐卡列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetSelectionList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetSelectionList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取费用帐卡列表失败！",e);
		}
	}
	
	/**
	 * 获取费用帐卡明细列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetSelectionDetailsList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetSelectionDetailsList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取费用帐卡列表失败！",e);
		}
	}
	
	/**
	 * 获取费用帐卡明细列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetSelectionFeesDetailsList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetSelectionFeesDetailsList(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取费用帐卡列表失败！",e);
		}
	}
	
	/**
	 * 结算查询发票号码和付款方式
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetSelectionFPHM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doGetSelectionFPHM(res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	/**
	 * 家床结算
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveSettleAccounts(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doSaveSettleAccounts(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	
	/**
	 * 取消结算
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateSettleAccounts(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientSelectionModel hpsm = new FamilySickBedPatientSelectionModel(dao);
		try {
			hpsm.doUpdateSettleAccounts(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	/**
	 * 发票打印
	 * @throws PrintException 
	 */
	public void doPrintMoth(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws PrintException {
		FamilySickBedPatientSelectionModel ospitalInvoiceByx = new FamilySickBedPatientSelectionModel(dao);
		//HospitalInvoiceByxFile ospitalInvoiceByx = new HospitalInvoiceByxFile();
		ospitalInvoiceByx.getParameters(req,res,ctx);
	}
	
}
