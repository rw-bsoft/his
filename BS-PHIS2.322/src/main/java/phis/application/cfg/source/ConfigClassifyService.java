package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;



import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @description 分类类别维护
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ConfigClassifyService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);

	/**
	 * 分类类别列表查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doClassifyListQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doClassifyListQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("分类类别列表查询失败！", e);
		}
	}

	/**
	 * 保存规则列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDetailList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
			ccm.doSaveDetailList(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("分类规则列表保存失败！", e);
		}
	}
	
	/**
	 * 注销与启用类别
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doExcute(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doExcute(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("注销、启用失败！", e);
		}
	}
	
	/**
	 * 判断分类是否在用
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doIsClassifyUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doIsClassifyUsed(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("判断是否在用失败！", e);
		}
	}
	
	/**
	 * 删除分类
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteClassify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doDeleteClassify(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("删除分类失败！", e);
		}
	}
	
	/**
	 * 判断分类名称是否重复
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doIsLBMCUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doIsLBMCUsed(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("判断是否在用失败！", e);
		}
	}
	
	/**
	 * 保存分类类别
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSaveClassify(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doSaveClassify(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存分类失败！", e);
		}
	}
	
	/**
	 * 删除分类规则
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDeleteFLGZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigClassifyModel ccm = new ConfigClassifyModel(dao);
		try {
			ccm.doDeleteFLGZ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("删除分类规则失败！", e);
		}
	}
	


}
