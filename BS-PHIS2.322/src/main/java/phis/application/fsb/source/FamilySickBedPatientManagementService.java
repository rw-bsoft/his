package phis.application.fsb.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
public class FamilySickBedPatientManagementService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedPatientManagementService.class);

	/**
	 * 病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doLoadBrxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientManagementModel ham = new FamilySickBedPatientManagementModel(dao);
		try {
			ham.doLoadBrxx(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("患者信息查询失败！",e);
		}
	}
	
	/**
	 * 病人注销
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateCanceled(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientManagementModel ham = new FamilySickBedPatientManagementModel(dao);
		try {
			ham.doUpdateCanceled(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("注销入院失败!",e);
		}
	}
	
	/**
	 * 病人性质转换
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateTransform(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientManagementModel ham = new FamilySickBedPatientManagementModel(dao);
		try {
			ham.doUpdateTransform(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("住院病人注销失败！",e);
		}
	}
	
	public void doGetPatientList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientManagementModel ham = new FamilySickBedPatientManagementModel(dao);
		try {
			ham.doGetPatientList(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取病人列表失败！",e);
		}
	}
	

	/**
	 * 病人信息修改
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateBRRY(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		FamilySickBedPatientManagementModel ham = new FamilySickBedPatientManagementModel(dao);
		try {
			ham.doUpdateBRRY(req,res,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("病人信息修改失败！",e);
		}
	}
}
