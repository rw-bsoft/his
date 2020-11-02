package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.BSHISUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HospitalAdmissionService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalAdmissionService.class);

	/**
	 * 病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryBrxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doQueryBrxx(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 查询就诊卡号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryJZKH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doQueryJZKH(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 获取住院号码和病案号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveQueryZYHM(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doSaveQueryZYHM(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 入院登记保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveRYDJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doSaveRYDJ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * 获取病人床号
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetBRCH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doGetBRCH(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取病人床号失败！", e);
		}
	}

	public void doGetDateTime(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		res.put("body", BSHISUtil.getDateTime());
	}
	
	public void doPrintMoth(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		HospitalAdmissionModel ham = new HospitalAdmissionModel(dao);
		try {
			ham.doPrintMoth(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

}
