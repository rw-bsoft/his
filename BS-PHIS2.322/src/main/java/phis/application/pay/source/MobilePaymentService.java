/**
 * @(#)TcmService.java Created on 2018-07-11 上午10:26:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.pay.source;

import java.util.Map;

import phis.application.xnh.source.XnhModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 省中医馆平台接口调用
 * 
 * @author zhaojian</a>
 */
public class MobilePaymentService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 支付订单
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doPayOrder(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doPayOrder(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("支付订单出错！",e);
		}
	}

	/**
	 * 扫码支付退款接口
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRefund(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doRefund(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("扫码支付退款出错！",e);
		}
	}

	/**
	 * 订单查询接口
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doOrderQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doOrderQuery(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("订单查询出错！",e);
		}
	}

	/**
	 * 支付成功后更新订单信息，包括回传json、支付宝微信返回的交易号等信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
/*	@SuppressWarnings("unchecked")
	public void doUpdateOrderInfoAfterPaySuccess(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doUpdateOrderInfoAfterPaySuccess(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("支付成功后更新订单信息出错！",e);
		}
	}*/

	/**
	 * 支付成功后更新订单状态
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateOrderStatus(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doUpdateOrderStatus(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("支付成功后更新订单状态出错！",e);
		}
	}

	/**
	 * 退款查询接口
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRefundQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doRefundQuery(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("退款查询出错！",e);
		}
	}

	/**
	 * 查询需退款订单信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryNeedRefundOrder(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			res.put("body", mpm.doQueryNeedRefundOrder(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("退款查询出错！",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doGetDzMx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		//Map<String, Object> body=(Map<String,Object>)req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			mpm.getDzMx(req, res, ctx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//移动支付对账
	@SuppressWarnings("unchecked")
	protected void doMobilepaydz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			mpm.mobilepaydz(body, res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//移动支付冲账
	@SuppressWarnings("unchecked")
	protected void doMobilepaycz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		MobilePaymentModel mpm = new MobilePaymentModel(dao);
		try {
			mpm.mobilepaycz(body, res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
