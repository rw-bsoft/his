/**
 * 
 * @description 用户管理
 * @author zhangyq 2012.06.14
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

public class SystemInitializationService extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(SystemInitializationService.class);

	/**
	 * 系统初始化
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveSystemInit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			SystemInitializationModel sim = new SystemInitializationModel(dao);
			sim.doSaveSystemInit(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
