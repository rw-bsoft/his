package phis.application.odm.source;

import java.util.List;
import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class OwnedDrugManageService extends AbstractActionService implements DAOSupportable{
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-10-15
	 * @description 自备药使用查询左边List数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryYplb(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		OwnedDrugManageModel model = new OwnedDrugManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret = model.queryYplb( body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-10-15
	 * @description 自备药使用查询右边List数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryMx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		OwnedDrugManageModel model = new OwnedDrugManageModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret = model.queryMx( body);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
