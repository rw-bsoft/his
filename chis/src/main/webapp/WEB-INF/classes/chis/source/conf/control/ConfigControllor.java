/**
 * @(#)ConfigControllor.java 创建于 2011-1-7 下午06:04:18
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package chis.source.conf.control;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.hibernate.Session;

import chis.source.PersistentDataOperationException;

import ctd.controller.exception.ControllerException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public interface ConfigControllor {

	/**
	 * 模块编号
	 * @param moduleId
	 */
	public void setModuleId(String moduleId);
	
	/**
	 * 模块内公共数据
	 * @param dataMap
	 */
	public void setModuleData(Map<String, Boolean> dataMap);
	
	
	/**
	 * 获取模块读写相关权限
	 * @param session
	 * @param ctx
	 * @return
	 * @throws PersistentDataOperationException
	 * @throws InvocationTargetException
	 * @throws ControllerException 
	 */
	public Map<String, Object> isReadOnly(Session session, Context ctx)
			throws PersistentDataOperationException, InvocationTargetException, ControllerException;
}
