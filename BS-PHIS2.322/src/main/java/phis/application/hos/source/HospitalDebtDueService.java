package phis.application.hos.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HospitalDebtDueService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalDebtDueService.class);

	/**
	 * 欠费信息查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSimpleQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		HospitalDebtDueModel hdb = new HospitalDebtDueModel(
				dao);
		hdb.doSimpleQuery(req, res, ctx);
	}
	
	/**
	 * 按性质催款查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryNatureDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			List<Map<String,Object>> rebody = new ArrayList<Map<String,Object>>();
			HospitalDebtDueModel hdd = new HospitalDebtDueModel(
					dao);
			rebody = hdd.doQueryNatureDunningConfig(req,res,dao,ctx);
			res.put("body", rebody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 按科室催款查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryDepartmentDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			List<Map<String,Object>> rebody = new ArrayList<Map<String,Object>>();
			HospitalDebtDueModel hdd = new HospitalDebtDueModel(
					dao);
			rebody = hdd.doQueryDepartmentDunningConfig(req,res,dao,ctx);
			res.put("body", rebody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存催款维护
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveDunningConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			HospitalDebtDueModel hdd = new HospitalDebtDueModel(
					dao);
			hdd.doSaveDunningConfig(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
