/*
 * @(#)AbstractEventAOPActionService.java Created on 2011-12-21 下午4:49:25
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
public abstract class AbstractEventAOPActionService extends
		AbstractEventActionService implements AOPSupportable {

	private String beforeService;
	private String afterService;
	private String beforeAction;
	private String afterAction;

	public String getBeforeService() {
		return beforeService;
	}

	public void setBeforeService(String beforeService) {
		this.beforeService = beforeService;
	}

	public String getAfterService() {
		return afterService;
	}

	public void setAfterService(String afterService) {
		this.afterService = afterService;
	}

	public String getBeforeAction() {
		return beforeAction;
	}

	public void setBeforeAction(String beforeAction) {
		this.beforeAction = beforeAction;
	}

	public String getAfterAction() {
		return afterAction;
	}

	public void setAfterAction(String afterAction) {
		this.afterAction = afterAction;
	}

}
