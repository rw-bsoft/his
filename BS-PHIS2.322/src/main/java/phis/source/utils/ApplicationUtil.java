/**
 * @(#)ApplicationUtil.java Created on 2013-10-30 下午2:03:55
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.utils;

import ctd.app.Application;
import ctd.app.ApplicationController;
import ctd.controller.exception.ControllerException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ApplicationUtil {

	/**
	 * 获取application文件
	 * 
	 * @param appName
	 * @return
	 * @throws ControllerException
	 */
	public static Application getApplication(String appName)
			throws ControllerException {
		ApplicationController acr = ApplicationController.instance();
		return acr.get(appName);
	}

	/**
	 * 获取属性
	 * 
	 * @param appName
	 * @param propName
	 * @return
	 * @throws ControllerException
	 */
	public static String getProperty(String appName, String propName)
			throws ControllerException {
		Application app = getApplication(appName);
		return (String) app.getProperty(propName);
	}

	/**
	 * 设置属性
	 * 
	 * @param appName
	 * @param propName
	 * @return
	 * @throws ControllerException
	 */
	public static void setProperty(String appName, String propName,
			String propValue) throws ControllerException {
		Application app = getApplication(appName);
		app.setProperty(propName, propValue);
	}

}
