package phis.application.war.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 科室病区医嘱病人List
 * 
 * @author liws
 * 
 */
public class OrderCardsListService extends AbstractActionService implements
		DAOSupportable {

	public void doQueryPatientList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			OrderCardsModule module = new OrderCardsModule(dao);
			res.put("body", module.queryPatientList(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doQueryPatientListByZYHM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			OrderCardsModule module = new OrderCardsModule(dao);
			res.put("body", module.queryPatientListByZYHM(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doQueryPatientListByBRXM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			OrderCardsModule module = new OrderCardsModule(dao);
			res.put("body", module.queryPatientListByBRXM(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doQueryPatientListByBRCH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			OrderCardsModule module = new OrderCardsModule(dao);
			res.put("body", module.queryPatientListByBRCH(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
