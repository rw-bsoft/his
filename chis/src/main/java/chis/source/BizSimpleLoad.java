/**
 * @(#)BizSimpleLoad.java Created on 2012-1-16 上午10:32:11
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.control.ControlRunner;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

/**
 * @description 加载单条数据服务，取代SimpleLoad.
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class BizSimpleLoad extends SimpleLoad {

	Logger log = LoggerFactory.getLogger(BizSimpleLoad.class);

	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Object reqPkey = req.get("pkey");
		String pkey = reqPkey == null ? null : String.valueOf(reqPkey);
		if (body != null) {
			if (pkey == null) {
				pkey = (String) body.get("pkey");
				if (pkey != null) {
					req.put("pkey", pkey);
				}
			}

			String fieldName = (String) body.get("fieldName");
			if (fieldName != null) {
				req.put("fieldName", fieldName);
			}
			String fieldValue = (String) body.get("fieldValue");
			if (fieldValue != null) {
				req.put("fieldValue", fieldValue);
			}
			req.put("fieldValue", body.get("fieldValue"));
		}
		super.execute(req, res, ctx);
		// 如果不是通过主键查询的,未找到记录不报404错误。
		if (pkey == null) {
			if (res.containsKey(Service.RES_CODE)
					&& 404 == (Integer) res.get(Service.RES_CODE)) {
				res.put(Service.RES_CODE, 200);
			}
		}

		String actions = (String) req.get("actions");
		// 需要进行权限验证
		if (actions != null) {
			if ("".equals(actions)) {
				actions = ControlRunner.UPDATE;
			}
			String schema = (String) req.get("schema");
			String[] actionArray = actions.split(",");
			try {
				Map<String, Object> resBody = (Map<String, Object>) res
						.get("body");
				ControlRunner.run(schema, resBody, ctx, actionArray);
			} catch (ServiceException e) {
				res.put(Service.RES_CODE, 500);
				res.put(Service.RES_MESSAGE, "列表数据验证失败!");
				e.printStackTrace();
			}
		}

	}
}
