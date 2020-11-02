/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 收费项目维护费用信息Service
 * 
 * @author zhangyq 2012.05
 */
public class ConfigCostService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 费用限制查询
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCostConstraintsList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigCostModel ccm = new ConfigCostModel(dao);
		Long fyxh = Long.parseLong(((Map<String, Object>) req.get("body"))
				.get("fyxh") + "");
		try {
			List<Map<String, Object>> rs = ccm.doCostConstraintsList(fyxh, ctx);
			res.put("body", rs);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 费用明细信息保存。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCostDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			ConfigCostModel ccm = new ConfigCostModel(dao);
			String op = (String) req.get("op");
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ccm.doSaveCostDetail(op, res, body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
