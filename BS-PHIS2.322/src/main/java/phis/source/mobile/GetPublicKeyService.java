/*
 * @(#)GetPublicKeyService.java Created on 2012-05-28 下午04:10:36
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.mobile;

import java.util.HashMap;
import java.util.Map;
import phis.source.security.EncryptUtil;
import ctd.service.core.Service;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 * 
 */
public class GetPublicKeyService implements Service {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ctd.core.service.Service#execute(org.json.JSONObject,
	 * org.json.JSONObject, ctd.util.context.Context)
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		Map<String, Object> resBody = (Map<String, Object>) res.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}
		resBody.put("publicKey", EncryptUtil.getPublicKey());
		res.put(RES_CODE, 200);
		res.put(RES_MESSAGE, "success");
	}

}
