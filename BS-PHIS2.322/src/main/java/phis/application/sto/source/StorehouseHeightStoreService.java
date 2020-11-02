package phis.application.sto.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class StorehouseHeightStoreService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 药库高低储提示查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYKGDC(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		StorehouseHeightStoreModel mmd = new StorehouseHeightStoreModel(dao);
		try {
			mmd.queryYKGDC(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

}
