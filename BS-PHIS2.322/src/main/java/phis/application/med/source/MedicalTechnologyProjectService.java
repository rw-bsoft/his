package phis.application.med.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 医技项目取消Service层
 * 
 * @author bsoft
 * 
 */
public class MedicalTechnologyProjectService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 初始化医技科室信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitMTDept(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			module.initMTDept(req, res, ctx);
			// 并获取BMSZ的初始值
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
			String result = ParameterUtil.getParameter(manaUnitId, "BMSZ", ctx);
			res.put(RES_CODE, Constants.CODE_OK);
			res.put("body", result);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 门诊业务查询病人列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryMSPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			res.put("body", module.queryMSPatient(req, res, ctx));
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 住院业务查询病人列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryZYPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			res.put("body", module.queryZYPatient(req, res, ctx));
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床业务查询病人列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJCPatient(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			res.put("body", module.doQueryJCPatient(req, res, ctx));
			res.put(RES_CODE, Constants.CODE_OK);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 取消门诊业务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCancelMSBusi(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			module.cancelMSBusi(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 取消住院业务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCancelZYBusi(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			module.cancelZYBusi(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 取消家床业务
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteJCBusi(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalTechnologyProjectModule module = new MedicalTechnologyProjectModule(
				dao);
		try {
			module.doDeleteJCBusi(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doYJBG01Search(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		long YJXH = Long.parseLong(req.get("YJXH") + "");
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("YJXH", YJXH);
		StringBuffer sql_list = new StringBuffer(
				"select ZDDM as ZDDM from yj_bg01 where YJXH=:YJXH");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
			if (inofList.get(0).get("ZDDM") != null
					|| inofList.get(0).get("ZDDM") != "") {
				res.put("ret", inofList.get(0).get("ZDDM") + "");
			} else {
				res.put("ret", 0);
			}
		} catch (PersistentDataOperationException e) {
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	public void doQueryJCBG01Search(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		long YJXH = Long.parseLong(req.get("YJXH") + "");
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("YJXH", YJXH);
		StringBuffer sql_list = new StringBuffer(
				"select ZDDM as ZDDM from JC_BG01 where YJXH=:YJXH");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
			if (inofList.get(0).get("ZDDM") != null
					|| inofList.get(0).get("ZDDM") != "") {
				res.put("ret", inofList.get(0).get("ZDDM") + "");
			} else {
				res.put("ret", 0);
			}
		} catch (PersistentDataOperationException e) {
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
