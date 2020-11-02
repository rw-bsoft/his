/**
 * @(#)UserFulExpressionsService.java Created on 2013-5-6 上午9:56:33
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

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
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class UserFulExpressionsService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(UserFulExpressionsService.class);

	public void doListUserFulExpressions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String stpType = (String) req.get("stpType");
		String ygdm = (String) req.get("ygdm");
		String ksdm = req.get("ksdm")!=null?req.get("ksdm").toString():null;
		UserFulExpressionsModel ufem = new UserFulExpressionsModel(dao);
		List<Map<String, Object>> list = ufem.listUserFulExpressions(stpType,
				ygdm, ksdm);
		res.put("body", list);
		res.put("totalCount", list.size());
	}

	public void doGetCountByPTNAME(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String PTNAME = (String) req.get("PTNAME");
		String SPTTYPE = req.get("SPTTYPE").toString();
		String SPTCODE = (String) req.get("SPTCODE");
		UserFulExpressionsModel ufem = new UserFulExpressionsModel(dao);
		boolean flag = ufem.getCountByPTNAME(PTNAME, SPTTYPE, SPTCODE);
		res.put("flag", flag);
	}
	
	@SuppressWarnings("unchecked")
	public void doSaveUserFulExpressions(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		UserFulExpressionsModel ufem = new UserFulExpressionsModel(dao);
		Map<String, Object> body = null;
		try {
			body = ufem.saveUserFulExpressions(reqBody,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存常用语失败！", e);
		}
		res.put("body", body);
	}
	
	@SuppressWarnings("unchecked")
	public void doGetTxtOrXmlData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserFulExpressionsModel ufem = new UserFulExpressionsModel(dao);
		String PTID= req.get("PTID").toString();
		String type= (String) req.get("type");
		String body=null;
		try {
			body=ufem.getTxtOrXmlData(PTID,type);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询常用语文本失败！", e);
		}
		res.put("body", body);
	}
	
	@SuppressWarnings("unchecked")
	public void doRemoveUserFul(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		UserFulExpressionsModel ufem = new UserFulExpressionsModel(dao);
		String pkey= req.get("pkey").toString();
		try {
			ufem.removeUserFul(pkey,ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存常用语失败！", e);
		}
	}
}
