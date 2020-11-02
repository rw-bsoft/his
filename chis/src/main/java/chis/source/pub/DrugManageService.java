/**
 * @(#)DrugManagerService.java Created on 2013-5-28 下午3:14:22
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DrugManageService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(DrugManageService.class);

	/**
	 * 
	 * @Description:保存药品更新信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-6-3 上午11:03:55
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDrug(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		DrugManageModel dModel = new DrugManageModel(dao);
		Map<String, Object> resMap = null;
		try {
			resMap = dModel.saveDrug(op, reqBodyMap, false);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to Save drug info .",e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("updateSuccess", true);
		if(resMap != null &resMap.size() > 0){
			resBodyMap.putAll(resBodyMap);
		}
		res.put("body", resBodyMap);
	}
	
	
	/**
	 * 
	 * @Description:保存导入药品
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-28 下午6:15:27
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveImportDrug(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> drugList = (List<Map<String, Object>>) reqBodyMap
				.get("drugList");
		DrugManageModel dModel = new DrugManageModel(dao);
		for (int i = 0; i < drugList.size(); i++) {
			Map<String, Object> drugMap = drugList.get(i);
			String mdcUse = (String) drugMap.get("mdcUse");
			long YPXH = (Integer) drugMap.get("YPXH");
			boolean exist = false;
			try {
				exist = dModel.existDrug(mdcUse, YPXH);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to judge drug is exist.", e);
				throw new ServiceException(e);
			}
			if (exist) {
				continue;
			} else {
				try {
					dModel.saveDrug("create", drugMap, false);
				} catch (ModelDataOperationException e) {
					logger.error("Failed to save drug.", e);
					throw new ServiceException(e);
				}
			}
		}
		res.put("success", true);
	}
	
	public void doLoadDicData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		DrugManageModel dModel = new DrugManageModel(dao);
		try {
			dModel.loadDicData(req,res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to Load Dic Data.", e);
			throw new ServiceException(e);
		}
	}

}
