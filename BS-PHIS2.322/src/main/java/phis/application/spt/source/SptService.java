package phis.application.spt.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description 省平台接口调用
 * 
 * @author Wangjl</a>
 */
public class SptService extends AbstractActionService implements
		DAOSupportable {
	
	/**
	 * 省平台药品下载
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doMedicinesDownload(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		SptModel mpm = new SptModel(dao);
		try {
			res.put("body", mpm.doMedicinesDownloads(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("药品信息下载出错！",e);
		}
	}
	/**
	 * 省平台供应商下载
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doFactoryDownload(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		SptModel mpm = new SptModel(dao);
		try {
			res.put("body", mpm.doFactoryDownload(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("供应商信息下载出错！",e);
		}
	}
	/**
	 * 省平台配送单药品下载
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDistributionDownload(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> spt = (Map<String, Object>) req.get("body");
		SptModel mpm = new SptModel(dao);
		try {
			res.put("body", mpm.doDistributionDownloads(spt, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("配送单信息下载出错！",e);
		}
	}
	
	//省平台药品目录与医院药品目录对照
		protected void doUpdatezbbm(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();
			Map<String, Object> body=(Map<String,Object>)req.get("body");
			String SPT_YPXH= body.get("SPT_YPXH").toString();

			SptModel mpm = new SptModel(dao);
			try {
				res.put("body", mpm.getypxxbyzbbm(body, ctx));
			} catch (ModelDataOperationException e) {
				throw new ServiceException("药品信息下载出错！",e);
			}
			String ypxx = res.get("body").toString();
			if (ypxx != null &&ypxx.length() > 0 && ypxx!="[]") {
						res.put("errormsg", "省平台药品统一编码为"+SPT_YPXH+"已被其他药品对照！");
						return;
			} else {
				res.put("errormsg", "");
			Map<String,Object> p=new HashMap<String,Object>();
				String upypxxsql="update YK_CDXX set ZBBM=:SPT_YPXH where YPXH=:YY_YPXH AND JGID=:JGID AND YPCD=:YY_YPCD";
				p.put("SPT_YPXH", body.get("SPT_YPXH").toString());
				p.put("YY_YPXH",Long.parseLong(body.get("YY_YPXH").toString()));
				p.put("YY_YPCD",Long.parseLong(body.get("YY_YPCD").toString()));
				p.put("JGID",jgid);
				try {
					dao.doUpdate(upypxxsql, p);
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//省平台药品目录与医院药品取消目录对照
				protected void doUpdatenozbbm(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
					Map<String, Object> body=(Map<String,Object>)req.get("body");
					String YPXH= body.get("YPXH").toString();
					String ZBBM= body.get("ZBBM").toString();
					String YPCD= body.get("YPCD").toString();
					String JGID= body.get("JGID").toString();
					Map<String,Object> p1=new HashMap<String,Object>();
					String upzbbmsql="update YK_CDXX set ZBBM=null where ZFPB='1'  and JGID=:JGID and ZBBM is not null";
					p1.put("JGID",JGID);
					try {
						dao.doUpdate(upzbbmsql, p1);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Map<String,Object> p=new HashMap<String,Object>();
						String upypxxsql="update YK_CDXX set ZBBM=null where YPXH=:YPXH AND JGID=:JGID AND YPCD=:YPCD and ZBBM=:ZBBM";
						p.put("YPXH", Long.parseLong(YPXH));
						p.put("ZBBM",ZBBM);
						p.put("YPCD",Long.parseLong(YPCD));
						p.put("JGID",JGID);
						try {
							dao.doUpdate(upypxxsql, p);
						} catch (PersistentDataOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
		
		/**
		 * 上传药品库存
		 * 
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws Exception 
		 */
		@SuppressWarnings("unchecked")
		public void doUpLoadphysicalDetail(Map<String, Object> req, Map<String, Object> res,
				BaseDAO dao, Context ctx) throws Exception {
			Map<String,Object> body = (Map<String, Object>) req.get("body");
			SptModel mpm = new SptModel(dao);
			try {
				res.put("body", mpm.doUpLoadphysicalDetail(body, ctx));
			} catch (ModelDataOperationException e) {
				throw new ServiceException("上传药品库存信息出错！",e);
			} catch (ExpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * 上传出库药品(消耗)
		 * 
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws Exception 
		 */
		@SuppressWarnings("unchecked")
		public void doUpLoadphysicalDetails(Map<String, Object> req, Map<String, Object> res,
				BaseDAO dao, Context ctx) throws Exception {
			Map<String,Object> body = (Map<String, Object>) req.get("body");
			SptModel mpm = new SptModel(dao);
			try {
				res.put("body", mpm.doUpLoadphysicalDetails(body, ctx));
			} catch (ModelDataOperationException e) {
				throw new ServiceException("上传药品出库信息出错！",e);
			} catch (ExpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * 上传采购计划
		 * 
		 * @param req
		 * @param res
		 * @param dao
		 * @param ctx
		 * @throws Exception 
		 */
		@SuppressWarnings("unchecked")
		public void doUpProcurementPlan(Map<String, Object> req, Map<String, Object> res,
				BaseDAO dao, Context ctx) throws Exception {
			Map<String,Object> body = (Map<String, Object>) req.get("body");
			SptModel mpm = new SptModel(dao);
			try {
				res.put("body", mpm.doUpProcurementPlan(body, ctx));
			} catch (ModelDataOperationException e) {
				throw new ServiceException("上传药品采购计划出错！",e);
			} catch (ExpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
