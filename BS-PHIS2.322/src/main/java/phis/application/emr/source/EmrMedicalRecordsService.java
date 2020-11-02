package phis.application.emr.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 处方信息维护
 * 
 * @author yangl</a>
 */
public class EmrMedicalRecordsService extends AbstractActionService implements
		DAOSupportable {

	public void doLoadMedicalRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doLoadMedicalRecords(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doSaveMedicalRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doSaveMedicalRecords(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryUpdateCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doQueryUpdateCount(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryUpdate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doQueryUpdate(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doSaveSignature(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doSaveSignature(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doUpdateSignature(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doUpdateSignature(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doZYSSJLLoad(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doZYSSJLLoad(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doZYZDJLLoad(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doZYZDJLLoad(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	public void doSaveCommon(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doSaveCommon(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	public void doLoadCommon(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doLoadCommon(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doQueryBASY(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doQueryBASY(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doSaveZYZDJL(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doSaveZYZDJL(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doQueryUser(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try {
			cmm.doQueryUser(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * @description 住院病案首页删除手术记录
	 * @author tanc
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteOperationRecord(Map<String, Object> req, Map<String, Object> res,
										BaseDAO dao, Context ctx) throws ServiceException{
		EmrMedicalRecordsModel cmm = new EmrMedicalRecordsModel(dao);
		try{
			cmm.doDeleteOperationRecord(req);
		}catch (ModelDataOperationException e) {
			res.put(RES_MESSAGE, e.getMessage());
			res.put(RES_CODE, e.getCode());
			throw new ServiceException(e);
		}
	}
}
