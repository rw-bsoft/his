package phis.application.fsb.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.hos.source.HospitalCompensationNumberModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class FsbPaymentProcessingService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(FsbPaymentProcessingService.class);

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
		FsbPaymentProcessingModel ham = new FsbPaymentProcessingModel(dao);
		try {
			ham.doQueryBrxx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 保存缴款
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePayment(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FsbPaymentProcessingModel ham = new FsbPaymentProcessingModel(dao);
		try {
			ham.doSavePayment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 注销缴款
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdatePayment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FsbPaymentProcessingModel ham = new FsbPaymentProcessingModel(dao);
		try {
			ham.doUpdatePayment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("缴款信息注销失败！", e);
		}
	}

	public void doGetReceiptNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		FsbPaymentProcessingModel ham = new FsbPaymentProcessingModel(dao);
		try {
			ham.doGetReceiptNumber(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetCompensationNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		FsbPaymentProcessingModel hcnm = new FsbPaymentProcessingModel(dao);
		try {
			hcnm.doGetCompensationNumber(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 结算查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryJsxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FsbPaymentProcessingModel ham = new FsbPaymentProcessingModel(dao);
		try {
			ham.doQueryJsxx(req, res,dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
