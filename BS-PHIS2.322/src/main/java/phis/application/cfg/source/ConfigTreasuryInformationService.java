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
 * @description
 * 
 * @author shiwy 2013.03.18
 */
public class ConfigTreasuryInformationService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigTreasuryInformationService.class);

	/**
	 * 启用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateConfigTreasuryInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			String schemaList = req.get("schemaList") + "";
			ConfigTreasuryInformationModule ctim = new ConfigTreasuryInformationModule(
					dao);
			ctim.doUpdateConfigTreasuryInformation(body, schemaList, res, dao,
					ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	// 判断名称不能重复
	@SuppressWarnings("unchecked")
	public void doKfmcVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigTreasuryInformationModule ctim = new ConfigTreasuryInformationModule(
				dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ctim.doKfmcVerification(body, res, schemaDetailsList, op, dao, ctx);
	}

	// 判断科室是否已经使用
	public void doQueryEJKF(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigTreasuryInformationModule ctim = new ConfigTreasuryInformationModule(
				dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ctim.doQueryEJKF(req, res, schemaDetailsList, dao, ctx);
	}

	// 判断账簿类别是否启用
	public void doQueryZBLBXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		ConfigTreasuryInformationModule ctim = new ConfigTreasuryInformationModule(
				dao);
		ctim.doQueryZBLBXX(res, dao, ctx);
	}

	/**
	 * 初始化
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doInitialize(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {

			ConfigTreasuryInformationModule ctim = new ConfigTreasuryInformationModule(
					dao);
			ctim.doInitialize(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
