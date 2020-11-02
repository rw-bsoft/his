/*
 * @(#)ActionSupportableServiceExecutor.java Created on 2011-12-15 下午5:52:46
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.dispatcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.service.AOPSupportable;
import chis.source.service.AbstractActionService;
import chis.source.service.AbstractService;
import chis.source.service.ActionServiceUtil;
import chis.source.service.ActionSupportable;
import chis.source.service.ServiceCode;

import ctd.service.core.ExecuteResult;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.JSONProtocol;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 支持Action的service执行器。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class ActionSupportableServiceExecutor {

	private static final Logger logger = LoggerFactory
			.getLogger(ActionSupportableServiceExecutor.class);
	public static final String BEFORE = "$before$";
	public static final String AFTER = "$after$";

	/**
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 */
	public static ExecuteResult execute(Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		String serviceId = (String) req.get(AbstractService.P_SERVICE_ID);
		String serviceAction = (String) req
				.get(ActionSupportable.P_SERVICE_ACTION);
		if (AppContextHolder.get() == null) {
			return new ExecuteResult(ServiceCode.CODE_NOT_FOUND,
					"NoApplicationContext");
		}

		Service sv = getServiceById(serviceId);
		if (sv == null) {
			return new ExecuteResult(ServiceCode.CODE_SERIVCE_NOT_FOUND,
					"Service[" + serviceId + "]not found.");
		}
		int code = ServiceCode.CODE_OK;
		String msg = "Success";
		try {
			openTransaction(sv, serviceAction, ctx);
			invoke(sv, req, res, ctx);
			commit(ctx);
		} catch (ServiceException e) {
			e.printStackTrace();
			rollback(ctx);
			logger.error("service[{}.{}] execute failure.", new Object[] {
					serviceId, serviceAction, e });
			Throwable t = getRootException(e);
			if (t instanceof ValidateException) {
				logger.error("************Validate Error Msg*************\n"
						+ e.getMessage()
						+ "\n**************************************");
				res.put("body", ((ValidateException) t).getValidMessage());
			}
			code = e.getCode();
			msg = e.getMessage();
			setCode(code, msg, res);
		} catch (Exception e) {
			e.printStackTrace();
			rollback(ctx);
			logger.error("service[{}.{}] execute failed.", new Object[] {
					serviceId, serviceAction, e });
			code = ServiceCode.CODE_UNKNOWN_ERROR;
			msg = "ServiceExecuteFailed:" + e;
			Throwable t = getRootException(e);
			if (t instanceof ValidateException) {
				logger.error("************Validate Error Msg*************\n"
						+ e.getMessage()
						+ "\n**************************************");
				res.put("body", ((ValidateException) t).getValidMessage());
			}
			code = 500;
			msg = e.getMessage();
			setCode(code, msg, res);
			
		} finally {
			close(ctx);
		}
		return digResult(code, msg, res);
	}

	public static void setCode(int code, String msg, Map<String, Object> res){
		res.put(JSONProtocol.CODE, code);
		res.put(JSONProtocol.MSG, msg);
	}
	/**
	 * 获取栈顶的异常。
	 * 
	 * @param t
	 * @return
	 */
	private static Throwable getRootException(Throwable t) {
		if (t.getCause() != null) {
			return getRootException(t.getCause());
		}
		return t;
	}

	/**
	 * @param sv
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	private static void invoke(Service sv, Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ServiceException {
		if (sv instanceof AOPSupportable) {
			AOPSupportable as = (AOPSupportable) sv;
			String bid = as.getBeforeService();
			if (!StringUtils.isEmpty(bid)) {
				Service aspsv = getServiceById(bid);
				if (aspsv == null) {
					throw new ServiceException(
							ServiceCode.CODE_SERIVCE_NOT_FOUND, "ServiceIsNULL");
				}
				Map<String, Object> beforeRes = new HashMap<String, Object>();
				beforeRes.put(AbstractService.P_SERVICE_ID, bid);
				res.put(BEFORE, beforeRes);
				String baction = as.getBeforeAction();
				@SuppressWarnings("unchecked")
				Map<String, Object> aspReq = (Map<String, Object>) ((HashMap<String, Object>) req)
						.clone();
				if (!StringUtils.isEmpty(baction)) {
					beforeRes.put(ActionSupportable.P_SERVICE_ACTION, baction);
					aspReq.put(ActionSupportable.P_SERVICE_ACTION, baction);
				}
				invoke(aspsv, aspReq, beforeRes, ctx);
				// ExecuteResult r = digResult(ServiceCode.CODE_OK, "Success",
				// beforeRes);
				// if (r.getCode() != ServiceCode.CODE_OK) {
				// logger.error(
				// "AOPService[{}.{}] beforeAspect execute failure.error info:{}",
				// new Object[] { bid, baction, r });
				// throw new ServiceException(r.getCode(), r.getMsg());
				// }
			}
		}
		sv.execute(req, res, ctx);
		// ExecuteResult er = digResult(ServiceCode.CODE_OK, "Success", res);
		// if (er.getCode() != ServiceCode.CODE_OK) {
		// throw new ServiceException(er.getCode(), er.getMsg());
		// }

		if (sv instanceof AOPSupportable) {
			AOPSupportable as = (AOPSupportable) sv;
			String aid = as.getAfterService();
			if (!StringUtils.isEmpty(aid)) {
				Service aspsv = getServiceById(aid);
				if (aspsv == null) {
					throw new ServiceException(
							ServiceCode.CODE_SERIVCE_NOT_FOUND, "ServiceIsNULL");
				}
				Map<String, Object> afterRes = new HashMap<String, Object>();
				afterRes.put(AbstractService.P_SERVICE_ID, aid);
				String aaction = as.getAfterAction();
				@SuppressWarnings("unchecked")
				Map<String, Object> aspReq = (Map<String, Object>) ((HashMap<String, Object>) req)
						.clone();
				if (!StringUtils.isEmpty(aaction)) {
					afterRes.put(ActionSupportable.P_SERVICE_ACTION, aaction);
					aspReq.put(ActionSupportable.P_SERVICE_ACTION, aaction);
				}
				res.put(AFTER, afterRes);
				invoke(aspsv, aspReq, afterRes, ctx);
				// ExecuteResult r = digResult(ServiceCode.CODE_OK, "Success",
				// afterRes);
				// if (r.getCode() != ServiceCode.CODE_OK) {
				// logger.error(
				// "AOPService[{}.{}] afterAspect execute failure.error info:{}",
				// new Object[] { aid, aaction, r });
				// throw new ServiceException(r.getCode(), r.getMsg());
				// }
			}
		}
	}

	/**
	 * @param serviceId
	 * @return
	 */
	private static Service getServiceById(String serviceId) {
		return (Service) AppContextHolder.get().getBean(serviceId);
	}

	/**
	 * @param defaultCode
	 * @param defaultMsg
	 * @param res
	 * @return
	 */
	private static ExecuteResult digResult(int defaultCode, String defaultMsg,
			Map<String, Object> res) {
		if (res.get(Service.RES_CODE) != null) {
			defaultCode = Integer.valueOf(res.get(Service.RES_CODE).toString());
		}
		if (res.get(Service.RES_MESSAGE) != null) {
			defaultMsg = res.get(Service.RES_MESSAGE).toString();
		}
		return new ExecuteResult(defaultCode, defaultMsg);
	}

	/**
	 * 根据一定的条件开户数据库事务。
	 * 
	 * @param sv
	 * @param serviceAction
	 * @param ctx
	 */
	private static void openTransaction(Service sv, String serviceAction,
			Context ctx) {
		if (sv instanceof AbstractActionService) {
			AbstractActionService aass = (AbstractActionService) sv;
			if (ActionServiceUtil.needDBSupport(serviceAction, aass)) {
				if (!ctx.has(Context.DB_SESSION)) {
					SessionFactory sf = (SessionFactory) AppContextHolder.get()
							.getBean("mySessionFactory");
					ctx.put(Context.DB_SESSION, sf.openSession());
				}
				if (ActionServiceUtil.needTransacted(serviceAction, aass)) {
					Session ss = (Session) ctx.get(Context.DB_SESSION);
					if (ss.isOpen()) {
						Transaction tc = ss.getTransaction();
						if (!tc.isActive()) {
							ss.clear();
							tc.begin();
						}
					}
				}
			}
		}
	}

	/**
	 * 如果已经开启了事务，提交事务。
	 * 
	 * @param ctx
	 */
	private static void commit(Context ctx) {
		if (ctx.has(Context.DB_SESSION)) {
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if (ss.isOpen()) {
				Transaction tc = ss.getTransaction();
				if (tc.isActive()) {
					tc.commit();
				}
			}
		}
	}

	/**
	 * 如果已经开启了事务，回滚事务。
	 * 
	 * @param ctx
	 */
	private static void rollback(Context ctx) {
		if (ctx.has(Context.DB_SESSION)) {
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if (ss.isOpen()) {
				Transaction tc = ss.getTransaction();
				if (tc.isActive()) {
					tc.rollback();
				}
			}
		}
	}

	/**
	 * 关闭session。
	 * 
	 * @param ctx
	 */
	private static void close(Context ctx) {
		if (ctx.has(Context.DB_SESSION)) {
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if (ss != null && ss.isOpen()) {
				ss.close();
			}
		}
	}
}
