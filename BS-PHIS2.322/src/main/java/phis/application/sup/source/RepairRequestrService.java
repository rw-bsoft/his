package phis.application.sup.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class RepairRequestrService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * @description 维修申请保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveform(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doSaveform(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @description 维修申请验收
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doAcceptanceform(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doAcceptance(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 维修申请 提交
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doCommit(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			List<Map<String, Object>> bodys = (List<Map<String, Object>>) req
					.get("body");
			for (int i = 0; i < bodys.size(); i++) {
				Map<String, Object> parMap = new HashMap<String, Object>();
				long WXXH = Long.parseLong(bodys.get(i).get("WXXH") + "");
				parMap.put("WXXH", WXXH);
				parMap.put("TJRQ", new Date());
				parMap.put("WXZT", 0);
				dao.doSave("update", BSPHISEntryNames.WL_WXBG, parMap, false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "提交失败！");
		}
	}

	/**
	 * 删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemove(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doRemove(req, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doUpdateWZBGWZXH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doUpdateWZBGWZXH(req, res);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * @description 维修管理-保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveWXGLform(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doSaveWXGLform(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @description 维修登记列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryWXDJ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doQueryWXDJ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * @description 维修登记列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryWXDJINFO(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doQueryWXDJINFO(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	/**
	 * 维修管理 审核 op=2 的时候是维修报告审核
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doAuditWXGLform(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String YGID = user.getUserId() + "";
		try {
			Long WXXH = Long.parseLong(req.get("body") + "");
			int op = Integer.parseInt(req.get("op") + "");
			Map<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("WXXH", WXXH);
			parMap.put("WXZT", op);
			if (op == 2) {
				parMap.put("SHGH", YGID);
				parMap.put("SHRQ", new Date());
			}
			dao.doSave("update", BSPHISEntryNames.WL_WXBG, parMap, false);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败！");
		}
	}

	/**
	 * 设备维修状况 form
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySbForm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doQuerySbForm(req, res, ctx);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败！");
		}

	}

	/**
	 * 查询设备维修单位
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryWXDW(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		RepairRequestrModel smm = new RepairRequestrModel(dao);
		try {
			smm.doQueryWXDW(req, res, ctx);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败！");
		}

	}

}
