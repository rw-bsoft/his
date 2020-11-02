package ctd.account.meta;

import java.util.HashMap;

import phis.source.message.Message;
import phis.source.message.MsgManager;
import ctd.util.annotation.RpcService;

public class PhisWatcher {
	@SuppressWarnings("unchecked")
	@RpcService
	public void onSubscribeMessage(Object message) {
		HashMap<String, Object> m = (HashMap<String, Object>) message;
		String method = (String) m.get("method");
		Object body = m.get("body");
		if ("messageSync".equals(method)) {
			Message msg = (Message) body;
			MsgManager.instance().addWithNoMeta(msg);
		}
	}
}
