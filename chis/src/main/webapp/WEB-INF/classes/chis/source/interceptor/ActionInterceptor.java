/**
 * @(#)ServiceInterceptor.java Created on 2010-9-6 下午04:39:10
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.service.AbstractActionService;

import ctd.util.context.Context;

/**
 * @description <ActionExecutor/>拦截器。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public interface ActionInterceptor {

	/**
	 * 设置切入面。对应每个AbstractService实例里的action。
	 * 
	 * @param pointcuts
	 */
	public void setPointcuts(List<String> pointcuts);

	/**
	 * 过滤器。
	 * 
	 * @param action
	 * 
	 * @return
	 */
	public boolean actionFilter(String action);

	/**
	 * 在目标方法被调用前执行拦截。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @param service
	 * @throws Throwable
	 */
	public void beforeInvoke(Map<String, Object> req,
			Map<String, Object> res, Context ctx,
			AbstractActionService service) throws Throwable;

	/**
	 * 在目标方法被调用之后进行拦截。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @param service
	 * @throws Throwable
	 */
	public void afterInvoke(Map<String, Object> req,
			Map<String, Object> res, Context ctx,
			AbstractActionService service) throws Throwable;

}
