/**
 * @(#)DefaultServiceImpl.java Created on 2010-9-8 下午06:47:07
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class DefaultActionExecutor implements ActionExecutor {

	private Map<String, Method> actionMathodCache = new ConcurrentHashMap<String, Method>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bsoft.bschis.ActionExecutor#execute(java.util.Map,
	 * java.util.Map, ctd.util.context.Context,
	 * com.bsoft.bschis.AbstractActionSupportableService)
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx, AbstractActionService service) throws ServiceException {
		String action = (String) req.get("serviceAction");
		List<Object> parameters = new ArrayList<Object>(5);
		parameters.add(req);
		parameters.add(res);
		parameters.add(ctx);

		try {
			invoke(action, parameters, service);
		} catch (InvocationTargetException e) {
			throw new ServiceException(e.getTargetException());
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 代理本类方法调用。
	 * 
	 * @param action
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private void invoke(String action, List<Object> parameters,
			AbstractActionService service) throws InvocationTargetException,
			PersistentDataOperationException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException {
		StringBuffer methodName = new StringBuffer("do");
		methodName.append(Character.toUpperCase(action.charAt(0)));
		methodName.append(action.substring(1));
		if (!ActionServiceUtil.needDBSupport(action, service)) {
			invokeNoDBSerivceMethod(service, methodName.toString(), parameters);
		} else {
			invokeSerivceMethod(service, methodName.toString(), parameters);
		}
	}

	/**
	 * @param service
	 * @param methodName
	 * @param parameters
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void invokeNoDBSerivceMethod(AbstractActionService service,
			String methodName, List<Object> parameters)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<Class<?>> clses = new ArrayList<Class<?>>(parameters.size() + 1);
		for (Iterator<Object> it = parameters.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof Map) {
				if (obj.getClass() == Context.class) {
					clses.add(Context.class);
				} else {
					clses.add(Map.class);
				}
			} else {
				clses.add(obj.getClass());
			}
		}
		Method method = getMethod(service.getClass(), methodName.toString(),
				clses.toArray(new Class<?>[clses.size()]));
		method.invoke(service,
				parameters.toArray(new Object[parameters.size()]));
	}

	/**
	 * @param service
	 * @param methodName
	 * @param session
	 * @param clses
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void invokeSerivceMethod(AbstractActionService service,
			String methodName, List<Object> parameters)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		List<Class<?>> clses = new ArrayList<Class<?>>(parameters.size() + 1);
		for (Iterator<Object> it = parameters.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof Map) {
				if (obj.getClass() == Context.class) {
					clses.add(Context.class);
				} else {
					clses.add(Map.class);
				}
			} else {
				clses.add(obj.getClass());
			}
		}
		if (service instanceof DAOSupportable) {
			clses.add(2, BaseDAO.class);
			BaseDAO dao = new BaseDAO();
			parameters.add(2, dao);
		}
		Method method = getMethod(service.getClass(), methodName.toString(),
				clses.toArray(new Class<?>[clses.size()]));
		method.invoke(service,
				parameters.toArray(new Object[parameters.size()]));
		return;

	}

	/**
	 * 查找服务方法。
	 * 
	 * @param clazz
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method getMethod(Class<?> clazz, String methodName,
			Class<?>... parameterTypes) throws NoSuchMethodException {
		StringBuilder key = new StringBuilder(clazz.getName());
		key.append(".").append(methodName).append("(");
		for (Class<?> cls : parameterTypes) {
			key.append(cls.getName()).append(",");
		}
		key.deleteCharAt(key.length() - 1).append(")");
		Method method = actionMathodCache.get(key.toString());
		if (method != null) {
			return method;
		}
		try {
			method = clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			if (clazz.getSuperclass() == null) {
				throw e;
			}
			method = getMethod(clazz.getSuperclass(), methodName,
					parameterTypes);
		}
		method.setAccessible(true);
		actionMathodCache.put(key.toString(), method);
		return method;
	}
}
