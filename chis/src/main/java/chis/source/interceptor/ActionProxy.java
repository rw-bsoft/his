/**
 * @(#)ActionProxy.java Created on 2010-9-15 下午02:48:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import chis.source.service.AbstractActionService;
import chis.source.service.ActionExecutor;

import ctd.util.context.Context;

/**
 * @description <ActionExecutor/>的代理类。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class ActionProxy implements InvocationHandler {

	private ActionExecutor target = null;

	private List<ActionInterceptor> actionInterceptors = null;

	public List<ActionInterceptor> getActionInterceptors() {
		return actionInterceptors;
	}

	public void setActionInterceptors(List<ActionInterceptor> actionInterceptors) {
		this.actionInterceptors = actionInterceptors;
	}

	public ActionExecutor getTarget() {
		return target;
	}

	public void setTarget(ActionExecutor target) {
		this.target = target;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		HashMap<String, Object> req = (HashMap<String, Object>) args[0];
		HashMap<String, Object> res = (HashMap<String, Object>) args[1];
		Context ctx = (Context) args[2];
		AbstractActionService service = (AbstractActionService) args[3];
		String action = (String) req.get("serviceAction");
		for (ActionInterceptor ai : actionInterceptors) {
			if (ai.actionFilter(action)) {
				ai.beforeInvoke(req, res, ctx, service);
			}
		}
		method.invoke(target, args);
		for (ActionInterceptor ai : actionInterceptors) {
			if (ai.actionFilter(action)) {
				ai.afterInvoke(req, res, ctx, service);
			}
		}
		return res;
	}

}
