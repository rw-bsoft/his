/**
 * @(#)TcmService.java Created on 2018-07-11 上午10:26:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.znts.source;

import java.util.Map;

import phis.application.ccl.source.CheckApplyModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 
 * 
 * @author zhaojian</a>
 */
public class ZntsService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 获取url页面内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetUrlPageContent(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		ZntsModel tcm = new ZntsModel(dao);
		try {
			res.put("body", tcm.doGetUrlPageContent(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取url页面内容出错！",e);
		}
	}

	/**
	 * 获取妇幼保健内嵌页面url
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetFybjUrlPage(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		ZntsModel tcm = new ZntsModel(dao);
		try {
			res.put("body", tcm.doGetFybjUrlPage(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取妇幼保健内嵌页面url出错！",e);
		}catch (PersistentDataOperationException e) {
			throw new ServiceException("获取妇幼保健内嵌页面url出错！",e);
		}
	}

	/**
	 * 获取url页面内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSocketSend(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		ZntsModel tcm = new ZntsModel(dao);
		try {
			res.put("body", tcm.doSocketSend(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取url页面内容出错！",e);
		}
	}
}

