package phis.application.war.source;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class DoctorAdviceExecuteService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(DoctorAdviceExecuteService.class);

	/**
	 * 收费医嘱执行--病人列表查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doPatientQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doPatientQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("病人列表查询失败！", e);
		}
	}

	public void doDoctorAdviceDetailQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doDoctorAdviceDetailQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}

	/*
	 * 医嘱项目确认
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfirm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException,
			PersistentDataOperationException, ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		List<Map<String, Object>> body2 = (List<Map<String, Object>>) req.get("body2");
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		advice.doSaveConfirm(body,body2, res, ctx);
	}

	/*
	 * 刷新
	 */
	public void doDetailChargeQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doDetailChargeQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}

	public void doQueryFHSFXM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doQueryFHSFXM(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}

	/**
	 * 附加项目---查询列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	public void doAdditionProjectsQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doAdditionProjectsQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-16
	 * @description 附加项目执行模块 左边病人数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	public void doAdditionProjectsBrQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			List<Map<String, Object>> ret =advice.additionProjectsBrQuery(req, res, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-24
	 * @description  附加项目执行模块 左边项目数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	public void doAdditionProjectsXmQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			Set<Map<String, Object>> ret =advice.additionProjectsXmQuery(req, res, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}
	/**
	 * 附加项目 --- 提交
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfirmAdditional(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		advice.doSaveConfirmAdditional(body, res, ctx);
		

	}
	/**
	 * 费用医嘱附加计价单--查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 */
	public void doAdditionProjectsFeeQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException,
			PersistentDataOperationException, ParseException {
		DoctorAdviceExecuteModel advice = new DoctorAdviceExecuteModel(dao);
		try {
			advice.doAdditionProjectsFeeQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("医嘱明细查询失败！", e);
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-23
	 * @description 项目执行按项目-项目查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doXmQuery(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		DoctorAdviceExecuteModel mmm = new DoctorAdviceExecuteModel(dao);
		try {
			Set<Map<String,Object>> ret = mmm.xmQuery(ctx);
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
	 * @createDate 2015-1-20
	 * @description 医技项目退回
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryZxks(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		DoctorAdviceExecuteModel mmm = new DoctorAdviceExecuteModel(dao);
		try {
			List<Map<String,Object>> ret = mmm.queryZxks();
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
	 * @createDate 2015-1-21
	 * @description 查询当前科室已提交未执行医技
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryThyj(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		DoctorAdviceExecuteModel mmm = new DoctorAdviceExecuteModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			List<Map<String,Object>> ret = mmm.queryThyj(body);
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
	 * @createDate 2015-1-21
	 * @description 医技退回
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveYjth(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		DoctorAdviceExecuteModel mmm = new DoctorAdviceExecuteModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			 mmm.saveYjth(body);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
}
