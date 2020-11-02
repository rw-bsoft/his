/*
 * @(#)AbstractService2.java Created on 2011-12-15 上午11:51:28
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.Constants;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractActionService extends AbstractService implements
		ActionSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractActionService.class);

	private ActionExecutor actionExecutor = null;
	private List<String> transactedActions;
	private List<String> noDBActions;

	/**
	 * @return
	 */
	public List<String> getTransactedActions() {
		return transactedActions;
	}

	/**
	 * 不需要操作数据库或者不需要传递Session的Action。
	 * 
	 * @return
	 */
	public List<String> getNoDBActions() {
		return noDBActions;
	}

	/**
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		String action = (String) req.get(P_SERVICE_ACTION);
		if (action == null || action.trim().length() == 0) {
			logger.error("Mandatory property missing: [{}].", P_SERVICE_ACTION);
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "非法的请求，" + P_SERVICE_ACTION + "未找到。");
			return;
		}
		if(actionExecutor==null) {
			actionExecutor = new DefaultActionExecutor();
		}
		actionExecutor.execute(req, res, ctx, this);
	}


	public ActionExecutor getActionExecutor() {
		return actionExecutor;
	}

	public void setActionExecutor(ActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	public void setTransactedActions(List<String> transactedActions) {
		this.transactedActions = transactedActions;
	}

	public void setNoDBActions(List<String> noDBActions) {
		this.noDBActions = noDBActions;
	}

}
