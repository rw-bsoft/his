/*
 * @(#)ActionSupportable.java Created on 2011-12-15 上午11:44:32
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

/**
 * @description An interface to mark a service class that is action supported,
 * that is to say, a service class can contains a serial of methods like below:
 *  			execute***
 *  the "***" represents an action which handing a request called by the client. 
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface ActionSupportable {

	/**
	 * the name of the "serviceAction" in the request data.
	 */
	public static final String P_SERVICE_ACTION = "serviceAction";
	
}
