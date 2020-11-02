package phis.application.war.source;

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

public class DoctorAdviceSubmitQueryService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(DoctorAdviceSubmitQueryService.class);

	@SuppressWarnings("unchecked")
	public void doDoctorAdviceQueryVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		dsm.doDoctorAdviceQueryVerification(body, res, ctx);
	}

	public void doGetDoctorAdviceSubmitQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		dsm.doGetDoctorAdviceSubmitQuery(req, res, ctx);
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-12
	 * @description 病区医嘱提交 左边病人列表数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetDoctorAdviceBrQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorAdviceSubmitQueryModel model = new DoctorAdviceSubmitQueryModel(dao);
		try {
			List<Map<String, Object>> ret =model.getDoctorAdviceBrQuery(req, res, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public void doSaveDoctorAdviceSubmit(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		dsm.doSaveDoctorAdviceSubmit(body, res, ctx);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-13
	 * @description 退药申请病人信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBackApplicationPatientInformation(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			Map<String, Object> ret = dsm
					.queryBackApplicationPatientInformation(body, ctx);
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
	 * @createDate 2012-11-15
	 * @description 退药申请查询已发药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	@SuppressWarnings("unchecked")
	public void doQueryDispensingRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			List<Map<String, Object>> ret = dsm.queryDispensingRecords(body,
					ctx);
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
	 * @createDate 2012-11-16
	 * @description 查询可退数量
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryTurningBackNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			double ret = dsm.queryTurningBackNumber(body, ctx);
			res.put("ktsl", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-16
	 * @description 保存退药申请记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBackApplication(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			dsm.saveBackApplication(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-16
	 * @description 提交退药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCommitBackApplication(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			dsm.saveCommitBackApplication(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-15
	 * @description 退药申请查询退药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */

	public void doQuerytyRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorAdviceSubmitQueryModel dsm = new DoctorAdviceSubmitQueryModel(dao);
		try {
			dsm.querytyRecords(req,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
