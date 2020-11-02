/**
 * @(#)MedicalExpMaintainService.java Created on 2013-5-27 下午3:03:21
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.List;
import java.util.Map;

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
public class MedicalExpMaintainService extends AbstractActionService implements
		DAOSupportable {

	public void doLogoutMedicalExp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		String ZXBZ = req.get("ZXBZ").toString();
		MedicalExpMaintainModel memm = new MedicalExpMaintainModel(dao);
		try {
			memm.logoutMedicalExp(pkey,ZXBZ);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("注销医学表达式失败！", e);
		}
	}
	
	public void doCheckHasMedicalExp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		String BDSMC = (String) req.get("BDSMC");
		MedicalExpMaintainModel memm = new MedicalExpMaintainModel(dao);
		boolean flag= true;
		try {
			flag=memm.checkHasMedicalExp(schema,BDSMC);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("检查是否存在同名医学表达式失败！", e);
		}
		res.put("body", flag);
	}
	
	public void doSaveMedicalExpData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		Map<String, Object> reqBody=(Map<String, Object>) req.get("body");
		MedicalExpMaintainModel memm = new MedicalExpMaintainModel(dao);
		Map<String, Object> map = null;
		try {
			map=memm.saveMedicalExpData(schema,op,reqBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存医学表达式失败！", e);
		}
		res.put("body", map);
	}
	
	public void doSaveMedicalExpContent(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = req.get("pkey").toString();
		String valueB = (String) req.get("valueB");
		MedicalExpMaintainModel memm = new MedicalExpMaintainModel(dao);
		try {
			memm.saveMedicalExpContent(pkey,valueB);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("加载医学表达式失败！", e);
		}
	}

	public void doListMedicalExpRecords(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String schema = (String) req.get("schema");
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		MedicalExpMaintainModel memm = new MedicalExpMaintainModel(dao);
		Map<String, Object> map = null;
		try {
			map=memm.listMedicalExpRecords(queryCnd,schema,queryCndsType,sortInfo,pageSize,pageNo);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("注销医学表达式失败！", e);
		}
		res.putAll(map);
	}

}
