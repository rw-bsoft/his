/**
 * @(#)PublicService.java Created on 2012-1-5 上午11:40:38
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.pub.source;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.search.AbstractSearchModule;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:huangpf@bsoft.com.cn">huangpf</a>
 */
public class SearchService extends AbstractActionService implements
		DAOSupportable {

	static Logger logger = LoggerFactory.getLogger(SearchService.class);

	/**
	 * 获取系统参数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doLoadDicData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String packageName = "phis.source.search.";
		try {
			HttpServletRequest request = (HttpServletRequest) ctx.get(Context.HTTP_REQUEST);
			String clz = (String) req.get("className");
			AbstractSearchModule asm = (AbstractSearchModule) Class.forName(
					packageName + clz + "SearchModule").newInstance();
			
			Cookie cookies[] = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if (c.getName().equals(user.getUserId() + "_searchType")) {
						asm.SEARCH_TYPE = c.getValue();
					}
					if (c.getName().equals(user.getUserId() + "_matchType")) {
						String matchType = c.getValue();
						if ("ALL".equals(matchType)) {
							asm.MATCH_TYPE = "%";
						}else if("LEFT".equals(matchType)) {
							asm.MATCH_TYPE = "";
						}
					}
				}
			}
			asm.open(ctx);
			//response.setContentType("text/plain");
			asm.execute(req, res, ctx);
		} catch (Exception e) {
			logger.error("search service failed.", e);
		} finally {
			//close(ctx);
		}
	}

}
