package phis.application.zyy.source;

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
public class ZyyService extends AbstractActionService implements DAOSupportable {

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
	public void doUpProcurementPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			ZyyModel mpm = new ZyyModel(dao);
			res.put("body", mpm.doUpProcurementPlan(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("上传药品采购计划出错！", e);
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传商品
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doUpProduct(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ZyyModel mpm = new ZyyModel(dao);
		try {
			res.put("body", mpm.doUpProduct(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("上传商品出错！", e);
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传供应商
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doUpSuppliers(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ZyyModel mpm = new ZyyModel(dao);
		try {
			res.put("body", mpm.doUpSuppliers(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("上传商品出错！", e);
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
