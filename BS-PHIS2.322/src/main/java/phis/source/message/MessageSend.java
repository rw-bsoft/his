package phis.source.message;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pub.source.PublicService;

public class MessageSend implements Runnable {
	static Logger logger = LoggerFactory.getLogger(PublicService.class);

	@Override
	public void run() {
		try {
			while (true) {
				BlockingQueue<Message> msgQueue = MsgManager.instance()
						.getMsgQueue();
				Message msg = msgQueue.take();
				Map<String, MsgClient> clients = MsgManager.instance()
						.getClients();
				for (String sessionId : clients.keySet()) {
					MsgClient client = clients.get(sessionId);
					if (msg.getBusinessId().equals("1000")) {// 系统消息
						client.setMsg(msg);
					} else {
						// 是否订阅,是否需要传递
						if (client.hasSubscribe(msg) && client.needMsg(msg)) {
							client.setMsg(msg);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error("消息分发线程发生异常!", e);
		}
	}
}
