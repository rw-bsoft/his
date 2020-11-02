/*
 * @(#)AbstractEventActionService.java Created on 2011-12-21 下午4:06:04
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.service.core.ServiceListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public abstract class AbstractEventActionService extends AbstractActionService implements
		EventSupportable {

	private Map<String, List<ServiceListener>> listeners = new ConcurrentHashMap<String, List<ServiceListener>>();;

	/*
	 * 注册事件监听器。
	 * 
	 * @see ctd.service.core.EventSupportService#on(java.lang.String,
	 * ctd.service.core.ServiceListener)
	 */
	public void on(String evName, ServiceListener sl) {
		List<ServiceListener> ls = listeners.get(evName);
		if (ls == null) {
			ls = new ArrayList<ServiceListener>();
			listeners.put(evName, ls);
		}
		ls.add(sl);
	}

	/*
	 * 移除事件监听器。
	 * 
	 * @see ctd.service.core.EventSupportService#un(java.lang.String,
	 * ctd.service.core.ServiceListener)
	 */
	public void un(String evName, ServiceListener sl) {
		List<ServiceListener> ls = listeners.get(evName);
		if (ls == null) {
			return;
		}
		int i = ls.indexOf(sl);
		if (i < 0) {
			return;
		}
		ls.remove(i);
	}

	/*
	 * 触发事件。
	 * 
	 * @see ctd.service.core.EventSupportService#fireEvent(java.lang.String,
	 * java.util.HashMap)
	 */
	public boolean fireEvent(String evName, HashMap<String, Object> message) {
		List<ServiceListener> ls = listeners.get(evName);
		if (ls == null) {
			return true;
		}
		boolean ret = true;
		for (ServiceListener l : ls) {
			if (!l.onMessage(message)) {
				ret = false;
			}
		}
		return ret;
	}

}
