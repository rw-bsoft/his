package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
public class HospitalPaymentProcessingService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPaymentProcessingService.class);

	/**
	 * 病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryBrxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPaymentProcessingModel ham = new HospitalPaymentProcessingModel(dao);
		try {
			ham.doQueryBrxx(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 保存缴款
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePayment(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPaymentProcessingModel ham = new HospitalPaymentProcessingModel(dao);
		try {
			ham.doSavePayment(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 注销缴款
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdatePayment(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPaymentProcessingModel ham = new HospitalPaymentProcessingModel(dao);
		try {
			ham.doUpdatePayment(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("缴款信息注销失败！",e);
		}
	}
	
	public void doGetReceiptNumber(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPaymentProcessingModel ham = new HospitalPaymentProcessingModel(dao);
		try {
			ham.doGetReceiptNumber(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
