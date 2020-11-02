package phis.application.hos.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/*
 * 日终结账和日终汇总
 */
public class HospitalizationCheckoutService extends AbstractActionService
		implements DAOSupportable {
	private static HospitalizationCheckoutService cck = null;
//	public static Date JZRQ = null;
	private HospitalizationCheckoutService() {
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-20
	 * @description 获取系统当前时间
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryDate(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		long date = System.currentTimeMillis();
		res.put("date", date);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2012-11-20
	 * @description 判断本日是否已做过结帐
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doIsreckon(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {

		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Object> ret = model.isreckon(body, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doCreate_jzrb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.doCreate_jzrb(body, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public Map<String, Object> doCreate_jzrb_preview(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		Map<String, Object> ret = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ret = model.doCreate_jzrb(body, ctx);
//			JZRQ=(Date) ret.get("JZRQ");
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		return ret;
	}

	public void doWf_Check(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			boolean ret = model.doWf_Check(body, ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doSave_jzrb(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.doSave_jzrb(body, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	// 日终结帐表单检索数据，参数是：adt_clrq_b、adt_clrq_e、当前工号、gl_jgid
	public Map<String, Object> doQuery_ZY_JZXX(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(
				dao);
		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> ret = model.doQuery_ZY_JZXX(body, ctx);
//			JZRQ=(Date) ret.get("JZRQ");
			return ret;
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

//	public void doGetJZRQ(Map<String, Object> req, Map<String, Object> res,
//			BaseDAO dao, Context ctx) throws ServiceException {
//		res.put("JZRQ", JZRQ);
//	}

	public static HospitalizationCheckoutService getInstance() {
		if (cck == null) {
			cck = new HospitalizationCheckoutService();
		}
		return cck;
	}
	//取消日报查询
	public void doQueryCancelCommit(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//			Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(dao);
		model.doQueryCancelCommit(req, res, dao, ctx);
	}
	
	//取消日报
	public void doCancelCommit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(dao);
		model.doCancelCommit(req, res, dao, ctx);
	}
	
	public void doQuerySQL(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(dao);
		model.doQuerySQL(req, res, dao, ctx);
	}
	
	public void doGetLastHZRQ(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		HospitalizationCheckoutModel model = new HospitalizationCheckoutModel(dao);
		model.doGetLastHZRQ(req, res, dao, ctx);
	}
	
}
