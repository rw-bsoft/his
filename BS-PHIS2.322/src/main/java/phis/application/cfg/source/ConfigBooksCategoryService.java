/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 账簿类别维护
 * 
 * @author shiwy 2013-11-05
 */
public class ConfigBooksCategoryService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);
	@SuppressWarnings("unchecked")
	public void doUpdateConfigBooksCategory(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigBooksCategoryModule cbcm = new ConfigBooksCategoryModule(dao);
			cbcm.doUpdateConfigBooksCategory(body, schemaList, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSxhAndZBMCVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigBooksCategoryModule cbcm = new ConfigBooksCategoryModule(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		cbcm.doSxhAndZBMCVerification(body, res, schemaDetailsList, op, dao,
				ctx);
	}

}
