/**
 * @(#)ChisDefaultServiceRoute.java Created on 2013-05-07 上午09:17:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.service.base;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import chis.source.dispatcher.ActionSupportableServiceExecutor;
import chis.source.service.ActionSupportable;
import ctd.service.core.DefaultServiceRoute;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ChisDefaultServiceRoute extends DefaultServiceRoute {

	@Override
	public boolean route(String id, Map<String, Object> req,
			Map<String, Object> res, Context ctx) {
		if (StringUtils.isEmpty((String) req
				.get(ActionSupportable.P_SERVICE_ACTION))) {
			return false;
		} else {
			ActionSupportableServiceExecutor.execute(req, res, ctx);
			return true;
		}
	}

}
