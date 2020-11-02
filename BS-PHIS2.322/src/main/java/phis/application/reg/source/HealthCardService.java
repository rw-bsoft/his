package phis.application.reg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HealthCardService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HealthCardService.class);
	
	/**
	 * 查询卡号是否存在
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckHasCardInfo(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx) 
			throws ServiceException {
		HealthCardModel model=new HealthCardModel(dao);
		try {
			model.doCheckHasCardInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
