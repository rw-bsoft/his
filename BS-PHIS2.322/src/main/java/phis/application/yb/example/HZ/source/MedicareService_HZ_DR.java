package phis.application.yb.example.HZ.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class MedicareService_HZ_DR extends AbstractActionService{
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-16
	 * @description 查询数据库有没该医保卡信息 (杭州没有该流程)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYBKXX(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException{
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		try {
			Map<String,Object> ret=model.queryYBKXX(body);
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
	 * @createDate 2014-1-16
	 * @description 保存医保卡信息 (杭州没有该流程)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doSaveYBKXX(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		try {
			Map<String,Object> ret=model.saveYBKXX(body);
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
	 * @createDate 2014-1-17
	 * @description 查询医保参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doQueryYbbrxz(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		try {
			Map<String,Object> ret=model.queryYbbrxz(ctx);
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
	 * @createDate 2014-1-22
	 * @description 获取挂号登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbGhjscs(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbGhjscs(body,ctx);
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
	 * @createDate 2014-1-22
	 * @description 医保挂号退号参数查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbThcs(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbThcs(body,ctx);
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
	 * @createDate 2014-1-23
	 * @description 根据读卡信息查询病人的MZHM
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doQueryOutpatientAssociation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		String grbh=req.get("GRBH")+"";
		try {
			String mzgl =model.queryOutpatientAssociation(grbh,ctx);
			res.put("MZGL", mzgl);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 获取门诊收费登记,上传,预结算,结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbMzjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbMzjscs(body,ctx);
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
	 * @createDate 2014-1-24
	 * @description 医保结算成功,本地结算失败 用于查询取消结算的参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryMzqxjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryMzqxjscs(body,ctx);
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
	 * @createDate 2014-1-24
	 * @description 获取医保发票作废参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbFpzfcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbFpzfcs(body,ctx);
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
	 * @createDate 2014-1-24
	 * @description 获取医保入院登记参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbRydjcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			model.queryYbRydjcs(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-24
	 * @description 查询医保住院病人性质转换参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbZyxzzhcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyxzzhcs(body,ctx);
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
	 * @createDate 2014-2-14
	 * @description 根据医保卡信息查询住院号码
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryZyhmByYbkxx(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryZyhmByYbkxx(body);
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
	 * @createDate 2014-1-24
	 * @description 获取医保费用上传,住院预结算,住院结算参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbZyjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyjscs(body,ctx);
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
	 * @createDate 2014-1-24
	 * @description 更新费用表的费用上传标志
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	public void doUpdateFyScbz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-14
	 * @description 查询住院取消结算参数(结算失败)
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbZyqxjscs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbZyqxjscs(body,ctx);
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
	 * @createDate 2014-2-17
	 * @description 查询住院结算作废参数
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	
	@SuppressWarnings("unchecked")
	public void doQueryYbzyzfcs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicareModel_HZ_DR model=new MedicareModel_HZ_DR(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryYbzyzfcs(body,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
