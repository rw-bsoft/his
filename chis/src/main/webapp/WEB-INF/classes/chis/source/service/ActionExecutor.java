/**
 * @(#)ServiceExecutor.java Created on 2010-9-8 下午07:18:52
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public interface ActionExecutor {

	/**
	 * @param req
	 * @param res
	 * @param ctx
	 * @param service
	 * @throws ServiceException
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx, AbstractActionService service)
			throws ServiceException;
}
