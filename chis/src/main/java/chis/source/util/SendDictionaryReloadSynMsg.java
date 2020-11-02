/**
 * @(#)SendDictionaryReloadSynMsg.java Created on 2013-12-23 下午4:58:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.util;

import java.util.HashMap;

import ctd.controller.watcher.WatchHelper;
import ctd.controller.watcher.WatcherCommands;
import ctd.util.AppContextHolder;
import ctd.util.message.MessageCenter;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class SendDictionaryReloadSynMsg {
	private volatile static SendDictionaryReloadSynMsg instance;

	private SendDictionaryReloadSynMsg() {
		instance = this;
	}

	public static synchronized SendDictionaryReloadSynMsg instance() {
		if (instance == null) {
			synchronized (SendDictionaryReloadSynMsg.class) {
				if (instance == null) {
					instance = new SendDictionaryReloadSynMsg();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * @Description:修改字典后发送MetaQ稍息，通知平台同步字典
	 * @param id
	 * @author ChenXianRui 2013-12-23 下午5:06:34
	 * @Modify:
	 */
	public void sendSynMsg(String id) {
		if (MessageCenter.getStore() != null) {
			HashMap<String, Object> msg = new HashMap<String, Object>();
			msg.put("cmd", WatcherCommands.CMD_RELOAD);
			msg.put("id", id);
			MessageCenter.pub(AppContextHolder.getConfigServiceId(WatchHelper.DICTIONARY), msg);
		}
	}
}
