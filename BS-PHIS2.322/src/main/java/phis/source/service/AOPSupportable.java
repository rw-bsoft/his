/*
 * @(#)AOPSupportable.java Created on 2011-12-19 上午10:07:35
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.service;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface AOPSupportable {

	/**
	 * @return
	 */
	public String getBeforeService();
	
	/**
	 * @return
	 */
	public String getAfterService();
	
	/**
	 * @return
	 */
	public String getBeforeAction();
	
	/**
	 * @return
	 */
	public String getAfterAction();
}
