/*
 * @(#)EventSupportable.java Created on 2011-12-21 下午3:53:43
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

import java.util.HashMap;

import ctd.service.core.ServiceListener;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public interface EventSupportable {

	public void on(String evName, ServiceListener sl);

	public void un(String evName, ServiceListener sl);

	public boolean fireEvent(String evName, HashMap<String, Object> message);
}
