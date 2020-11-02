/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 机构收费项目维护费用信息Service
 * 
 * @author zhangyq 2012.05
 */
public class ConfigDeptCostService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 费用限用保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	/*
	 * @SuppressWarnings("unchecked") protected void doSaveCost(Map<String,
	 * Object> jsonReq, Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
	 * throws ServiceException { ConfigDeptCostModel ccm = new
	 * ConfigDeptCostModel(dao); List<Map<String, Object>> body =
	 * (List<Map<String, Object>>) jsonReq.get("body"); String fyxh =
	 * body.get(0).get("FYXH")+""; try { ccm.doSaveCost(fyxh,body); } catch
	 * (ModelDataOperationException e) { throw new ServiceException(e); }
	 * jsonRes.put(RES_CODE, ServiceCode.CODE_OK); jsonRes.put(RES_MESSAGE,
	 * "费用限用保存成功"); }
	 */

	/**
	 * 机构项目调入查询
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doCostCallList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigDeptCostModel cdcm = new ConfigDeptCostModel(dao);
		List<Object> cnds = (List<Object>) req.get("cnd");
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String FYGB = ((Map<String, Object>) req.get("body")).get("FYGB") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("first", first * pageSize);
		parameters.put("max", pageSize);
		try {
			List<Object> l = cdcm.doCostCallList(FYGB, cnds, parameters, ctx);
			int count = (Integer) l.get(0);

			List<Map<String, Object>> rs = (List<Map<String, Object>>) l.get(1);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", count);
			res.put("body", rs);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 费用调入保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCallin(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigDeptCostModel ccm = new ConfigDeptCostModel(dao);
		List<Object> body = (List<Object>) req.get("body");
		try {
			if (body != null && body.size() > 0) {
				ccm.doSaveCallin(body, ctx);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "费用调入成功");
	}

	/**
	 * 费用调入保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCallinAll(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigDeptCostModel ccm = new ConfigDeptCostModel(dao);
		String FYGB = ((Map<String, Object>) req.get("body")).get("FYGB") + "";
		try {
			ccm.doSaveCallinAll(FYGB, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "费用全部调入成功");
	}

	/**
	 * 项目明细from修改回填数据
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doFromLoadItemDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigDeptCostModel ccm = new ConfigDeptCostModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String FYXH = body.get("FYXH") + "";
		try {
			Map<String, Object> rs = ccm.doFromLoadItemDetails(FYXH, ctx);
			res.put("body", rs);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 机构收费项目注销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutProject(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ConfigDeptCostModel cdcm = new ConfigDeptCostModel(dao);
		try {
			int i = cdcm.doLogoutProject(body);
			res.put(RES_CODE, i);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}

	/**
	 * 所有费用调入保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveAllCallin(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigDeptCostModel ccm = new ConfigDeptCostModel(dao);
		List<Integer> list_fygb = (List<Integer>) req.get("body");
		// String FYGB = ((Map<String,Object>)req.get("body")).get("FYGB")+"";
		try {
			for (Integer fygb : list_fygb) {
				ccm.doSaveCallinAll(fygb + "", ctx);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "费用全部调入成功");
	}
}
