package ctd.controller.watcher;

import java.util.HashMap;

import ctd.util.AppContextHolder;
import ctd.util.message.MessageCenter;

public class WatchHelperEx extends WatchHelper {
	public static final String PARAMTERRELOAD = "paramterReload";

	/**
	 * PHIS消息同步
	 * 
	 * @param topic
	 *            ctd.controller.watcher.WatchHelper的静态成员变量
	 * @param o
	 *            传输的信息
	 * @param method
	 *            对应的方法
	 */
	public static void send(String topic, Object o, String method) {
		if (MessageCenter.getStore() != null) {
			HashMap<String, Object> msg = new HashMap<String, Object>();
			msg.put("body", o);
			msg.put("method", method);
			MessageCenter.pub(AppContextHolder.getConfigServiceId(topic), msg);
		}
	}
}
