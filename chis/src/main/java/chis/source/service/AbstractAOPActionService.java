/*
 * @(#)AbstractAOPActionSupportableService.java Created on 2011-12-19 上午10:49:16
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractAOPActionService extends AbstractActionService
		implements AOPSupportable {

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
