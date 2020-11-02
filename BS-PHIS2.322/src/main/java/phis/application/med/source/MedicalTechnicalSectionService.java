package phis.application.med.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 医技业务处理
 * 
 * @author liyunt
 * 
 */
public class MedicalTechnicalSectionService extends AbstractActionService
		implements DAOSupportable {

	/**
	 * 门诊病人列表
	 */
	public void doGetMzList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao).getMzList(
					req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 门诊住院医技列表
	 */
	public void doGetZYList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao)
					.getZYListByParam(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床医技列表
	 */
	public void doQueryJCList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao)
					.doQueryJCList(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 病人医技项目列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetMzList_Proj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao)
					.getMzList_Proj(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetMzForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			res.put("body", mtsm.doGetMzForm(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetZYForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			res.put("body", mtsm.doGetZYForm(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetJCForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			res.put("body", mtsm.doGetJCForm(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetMzEditList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doGetMzEditList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetZYEditList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doGetZYEditList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doGetJCEditList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doGetJCEditList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 住院医技项目列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetZyList_Proj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao)
					.getZyList_Proj(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 家床医技项目列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJcList_Proj(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			res.put("body", new MedicalTechnicalSectionModule(dao)
					.doQueryJcList_Proj(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveMZYJAndProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao).doSaveMZYJAndProject(req,
					res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveZYYJAndProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao).doSaveZYYJAndProject(req,
					res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveJCYJAndProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao).doSaveJCYJAndProject(req,
					res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/****************************** add by zhangyq ******************************************/
	public void doQueryMZZXPB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryMZZXPB(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryZYZXPB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryZYZXPB(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryJCZXPB(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryJCZXPB(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryByMZHM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryByMZHM(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryByZYHM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryByZYH(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryByJCHM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryByJCHM(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/****************************** add by zhangyq ******************************************/
	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doExecuteYjProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao)
					.excuteMedicalTechnicalByParam(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYjProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao).doQueryYjProject(req, res,
					ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteYjProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao)
					.deleteMedicalTechnicalByParam(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteJcProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			new MedicalTechnicalSectionModule(dao)
					.doDeleteJcProject(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doQueryXTCS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String CSMC = req.get("CSMC") + "";
		String rext = ParameterUtil.getParameter(JGID, CSMC, ctx);
		res.put("body", rext);

	}

	public void doQueryPubXTCS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String JGID = ParameterUtil.getTopUnitId();
		String CSMC = req.get("CSMC") + "";
		String rext = ParameterUtil.getParameter(JGID, CSMC, ctx);
		res.put("body", rext);

	}

	public void doQueryMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryMX(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * 住院医技项目列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryZyMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryZyMX(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 住院医技项目列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryJcMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		try {
			MedicalTechnicalSectionModule mtsm = new MedicalTechnicalSectionModule(
					dao);
			mtsm.doQueryJcMX(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
