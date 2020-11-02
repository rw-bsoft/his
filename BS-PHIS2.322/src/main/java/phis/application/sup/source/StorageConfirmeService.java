package phis.application.sup.source;

import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class StorageConfirmeService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 
	 * @description 确认入库
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		 StorageConfirmeModel smm = new  StorageConfirmeModel(dao);
		try {
			smm.doSaveCommit(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}


}
