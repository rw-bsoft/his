package phis.application.war.source;

import java.util.List;
import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class WardDoctorPrintService extends AbstractActionService implements DAOSupportable {
	
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-11
	 * @description 医嘱套打 左边病人列表数据查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryYzdyBrxx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WardDoctorPrintModel model = new WardDoctorPrintModel(dao);
		Map<String,Object> body= (Map<String,Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryYzdyBrxx(body);
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
	 * @createDate 2014-7-11
	 * @description 医嘱套打 查询医嘱信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryYz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WardDoctorPrintModel model = new WardDoctorPrintModel(dao);
		Map<String,Object> body= (Map<String,Object>) req.get("body");
		try {
			List<Map<String, Object>> ret = model.queryYz(body,ctx);
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
	 * @createDate 2014-7-15
	 * @description 医嘱套打前的业务操作
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveWardDoctorPrint(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WardDoctorPrintModel model = new WardDoctorPrintModel(dao);
		Map<String,Object> body= (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret = model.saveWardDoctorPrint(body,ctx);
			res.put("body", ret);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-18
	 * @description 查询指定行打印数据
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryDyjl(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WardDoctorPrintModel model = new WardDoctorPrintModel(dao);
		List<Object> cnd=(List<Object>)req.get("cnd");
		try {
			List<Map<String, Object>> ret = model.queryDyjl(cnd);
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
	 * @createDate 2014-7-29
	 * @description 查询是否需要套打参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQuerySFTD(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		WardDoctorPrintModel model = new WardDoctorPrintModel(dao);
		try {
			int sftd = model.querySFTD(ctx);
			res.put("sftd", sftd);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
}
