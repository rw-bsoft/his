package phis.application.med.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DiagnosisMaintainService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 医技诊断结果维护模块，保存按钮
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException 
	 */
	public void doSaveZDJG(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		try{
			DiagnosisMaintainModule module = new DiagnosisMaintainModule(dao);
			module.doSaveZDJG(req, res, ctx);
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
