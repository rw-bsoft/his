package phis.source.message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PublicService;
import ctd.controller.watcher.WatchHelperEx;
import ctd.util.AppContextHolder;
import ctd.util.store.ActiveStore;
import ctd.util.store.StoreConstants;
import ctd.util.store.StoreException;

public class MsgManager {
	private static MsgManager msgManage = null;
	private BlockingQueue<Message> msgQueue = new ArrayBlockingQueue<Message>(
			500);
	private ActiveStore store;
	private String serverNodesPath;
	private Map<String, MsgClient> clients = new ConcurrentHashMap<String, MsgClient>();
	// private Map<String, String> roleSubscribe = new HashMap<String,
	// String>();
	static Logger logger = LoggerFactory.getLogger(PublicService.class);

	// {
	// // 后期扩展，通过维护模块给角色设置订阅消息
	// roleSubscribe.put("phis.50", "1001");//
	// 病人列表(phis.application.menu.QKZL/CIC/PatientList)
	// roleSubscribe.put("phis.50", "1002");//
	// 收费结算(phis.application.menu.QKZL/IVC/IVC13)
	// roleSubscribe.put("phis.50", "1003");//
	// 门诊发药(phis.application.menu.YYGL/PHA/PHA02)
	// }

	private MsgManager() {

	}

	public BlockingQueue<Message> getMsgQueue() {
		return msgQueue;
	}

	public Map<String, MsgClient> getClients() {
		return clients;
	}

	// public String getSubScribe(String roleId) {
	// return roleSubscribe.get(roleId);
	// }

	/**
	 * 单例管理类
	 * 
	 * @return
	 */
	public static MsgManager instance() {
		if (msgManage == null) {
			msgManage = new MsgManager();
			MessageSend msgSend = new MessageSend();
			Thread t1 = new Thread(msgSend);
			t1.start();// 开启消息分发线程
			logger.info("Message Send Thread Start...");
		}
		return msgManage;
	}

	public void add(Message msg) {
		// 判断当前phis域有效的服务数
		String domain = AppContextHolder.getName();
		serverNodesPath = StoreConstants.SERVERNODES_HOME + "/" + domain;
		store = AppContextHolder.getActiveStore();
		try {
			List<String> nodes = store.getChildren(serverNodesPath);
			// System.out.println("------" + nodes);
			if (nodes != null && nodes.size() > 1) {
				// 集群方式，通过metaQ广播消息
				WatchHelperEx.send("phisUpdate", msg, "messageSync");
			} else {
				msgQueue.add(msg);
			}
		} catch (StoreException e) {
			logger.error("获取phis服务错误!", e);
			msgQueue.add(msg);
		}
		// msgQueue.add(msg);
	}

	/**
	 * 仅供metaQ使用,业务代码不需要调用此方法
	 * 
	 * @param msg
	 */
	public void addWithNoMeta(Message msg) {
		msgQueue.add(msg);
	}

	public void addClient(String sessionId, MsgClient client) {
		clients.put(sessionId, client);
	}

	public void removeClient(String sessionId) {
		clients.remove(sessionId);
	}
}
