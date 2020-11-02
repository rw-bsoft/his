package phis.application.ophthal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;



public class OphthalService extends AbstractActionService implements DAOSupportable {
	private static Logger logger = LoggerFactory.getLogger(OphthalService.class);
	
	
	/**
	 * 当前是否是眼科
	 * @param req
	 * @param res
	 * @param dao
	 * @param cxt
	 * @throws ServiceException
	 */
	public void doIsOphthal(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context cxt)
			throws ServiceException {
		OphthalModel module = new OphthalModel(dao);
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map map = (Map)req.get("body");
		String regDepartmentId = String.valueOf(map.get("regDepartmentId"));
		try {
			boolean  isOphthal=  module.isOphthal(regDepartmentId);
			result.put("isOphthal", (isOphthal?1:0));
			res.put("body", result);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
