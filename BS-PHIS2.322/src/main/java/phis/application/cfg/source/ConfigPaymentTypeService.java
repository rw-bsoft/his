/**
 * 
 * @description 付款方式维护
 * 名称重复验证公用方法
 * @author zhangyq 2012.05.30
 */
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

public class ConfigPaymentTypeService extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPaymentTypeService.class);

	/**
	 * 付款方式作废
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doPaymentWayInvalidate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "作废成功";
		try {
			ConfigPaymentTypeModel cpm = new ConfigPaymentTypeModel(dao);
			code = cpm.doPaymentWayInvalidate(body);
			if (code == 301) {
				msg = "该付款方式为默认付款方式，不能作废!";
			} else if (code == 302) {
				msg = "该付款方式为货币误差，不能作废!";
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}

	/**
	 * 付款方式默认
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doUpdatePaymentDefault(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "修改成功";
		try {
			ConfigPaymentTypeModel cpm = new ConfigPaymentTypeModel(dao);
			code = cpm.doUpdatePaymentDefault(body);
			if (code == 301) {
				msg = "该付款方式已作废,不能设为默认付款方式!";
			} else if (code == 302) {
				msg = "货币误差不能为默认付款方式!";
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}

	/**
	 * 付款方式货币误差
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doUpdateCurrencyErrors(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "货币误差修改成功";
		try {
			ConfigPaymentTypeModel cpm = new ConfigPaymentTypeModel(dao);
			code = cpm.doUpdateCurrencyErrors(body);
			if (code == 301) {
				msg = "该付款方式已作废,不能设为货币误差付款方式!";
			} else if (code == 302) {
				msg = "默认付款方式不能为货币误差!";
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}

	/**
	 * 付款方式维护保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePayment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "付款方式保存成功";
		try {
			ConfigPaymentTypeModel cpm = new ConfigPaymentTypeModel(dao);
			res.put("body", cpm.doSavePayment(op, body));
			if ("1".equals(((Map<String, Object>) res.get("body")).get("NUM"))) {
				code = 400;
				msg = "该付款方式名称已存在，数据保存失败";
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}
}
