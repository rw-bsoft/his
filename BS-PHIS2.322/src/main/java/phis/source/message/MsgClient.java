package phis.source.message;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;

public class MsgClient {
	private UserRoleToken user;
	private String subscribe;
	private BlockingQueue<Message> msgQueue = new ArrayBlockingQueue<Message>(5);// 客户端通过此队列获取返回的信息
	private static final Integer TIMEOUT = 20; // 长轮询时间
	static Logger logger = LoggerFactory.getLogger(MsgClient.class);

	public UserRoleToken getUser() {
		return user;
	}

	public void setUser(UserRoleToken user) {
		this.user = user;
	}

	public String getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}

	public void setMsg(Message msg) {
		msgQueue.add(msg);
	}

	/**
	 * 判断是否订阅消息
	 * 
	 * @Description:
	 * @param msg
	 * @return
	 * @author YangL 2015-6-3 下午2:30:05
	 * @Modify:
	 */
	public boolean hasSubscribe(Message msg) {
		try {
			Dictionary dic = DictionaryController.instance().get(
					"phis.dictionary.roleSubscribe");
			DictionaryItem di = dic.getItem(msg.getBusinessId());
			String roleTypes = di.getProperty("roleTypes") == null ? null : di
					.getProperty("roleTypes").toString();
			if (roleTypes != null) {
				if(di.getProperty("ref")!=null) {
					msg.setModuleId(di.getProperty("ref").toString());
				}
				if (roleTypes.indexOf(user.getRoleId()) != -1)
					return true;
				if (user.getRole().getType() != null
						&& user.getRole().getType().length() > 0
						&& roleTypes.indexOf(user.getRole().getType()) != -1)
					return true;
			}
		} catch (ControllerException e) {
			logger.error("get role subscribe info error!", e);
		}
		return false;
	}

	public Message getMsg() {
		try {
			return msgQueue.poll(TIMEOUT * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * 判断客户端是否需要此消息
	 * 
	 * @param msg
	 * @return true表示客户端需要此消息
	 */
	public boolean needMsg(Message msg) {
		Map<String, Object> keywords = msg.getKeywords();
		if (keywords.size() == 0)
			return true;
		for (String key : keywords.keySet()) {
			if (user.getProperty(key) != null
					&& keywords.get(key) != null
					&& user.getProperty(key).toString()
							.equals(keywords.get(key).toString())) {
				return true;
			}
		}
		return false;
	}
}
