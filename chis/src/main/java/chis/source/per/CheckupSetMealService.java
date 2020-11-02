/**
 * @(#)CheckupSetMealService.java Created on 2012-4-17 下午7:28:43
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.dictionary.DictionaryController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupSetMealService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CheckupSetMealService.class);

	/**
	 * 保存体检套餐
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSetMeal(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String name = (String) reqBodyMap.get("name");
		String manaUnitId = (String) reqBodyMap.get("manaUnitId");
		String pkey = (String) reqBodyMap.get("id");
		CheckupSetMealModel csmModel = new CheckupSetMealModel(dao);
		boolean nameIsExist = false;
		try {
			nameIsExist = csmModel.checkSetMealNameIsExist(name, manaUnitId,
					pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Check set meal name is exist failed.", e);
			throw new ServiceException(e);
		}
		if (nameIsExist) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "套餐名称[" + name + "]已经存在!");
			return;
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = csmModel.saveSetMeal(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save set meal failed.", e);
			throw new ServiceException(e);
		}
		// 更新套餐字典
		DictionaryController.instance().reload("chis.dictionary.perComboList");
		//发送字典同步稍息
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.perComboList");
		DictionaryController.instance().reload("chis.dictionary.perCombo");
		//发送字典同步稍息
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.perCombo");
		res.put("body", rsMap);
	}

	/**
	 * 保存体检套餐明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSetMealDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String pkey = (String) reqBodyMap.get("id");
		String comboId = (String) reqBodyMap.get("comboId");
		String officeId = (String) reqBodyMap.get("projectOfficeId");
		String projectOfficeName = (String) reqBodyMap
				.get("projectOfficeId_text");
		CheckupSetMealModel csmModel = new CheckupSetMealModel(dao);
		boolean isExist = false;
		try {
			isExist = csmModel.checkSetMealDetailIsExist(op, comboId, officeId,
					pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Check this office have the set meal.", e);
			throw new ServiceException(e);
		}
		if (isExist) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "科室代码[" + projectOfficeName + "]已经存在!");
			return;
		}
		reqBodyMap.remove("projectOfficeName");
		Map<String, Object> rsMap = null;
		try {
			rsMap = csmModel.saveSetMealDetail(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save set meal detail failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
		// 更新套餐字典
		DictionaryController.instance().reload("chis.dictionary.perComboList");
		//发送字典同步稍息
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.perComboList");
		// 更新套餐字典
		DictionaryController.instance().reload("chis.dictionary.perCombo");
		//发送字典同步稍息
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.perCombo");
	}
	
	
	public void doCheckNeedNode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String val = (String) req.get("val");
		boolean needNode=true;
		String values = (String) req.get("values");
		CheckupSetMealModel csmModel = new CheckupSetMealModel(dao);
		List<Map<String, Object>> result = null;
		try {
			result = csmModel.checkNeedNode(val);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		for (int i = 0; i < result.size(); i++) {
			String checkupProjectId=(String) result.get(i).get("checkupProjectId");
			if(!values.contains(checkupProjectId)){
				needNode=false;
				break;
			}
		}
		res.put("needNode", needNode);
	}

	/**
	 * 删除体检套餐
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRemoveSetMeal(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		CheckupSetMealModel csmModel = new CheckupSetMealModel(dao);
		boolean isUsed = false;
		try {
			isUsed = csmModel.checkSetMealIsUsed(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Check set meal is used failed.", e);
			throw new ServiceException(e);
		}
		if (isUsed) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "该套餐已经被体检记录使用,无法删除!");
			return;
		}
		try {
			csmModel.deleteSetMeal(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Delete set meal failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 判断套餐是否已经被使用
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetSetMealIsUsed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String setMealId = (String) reqBodyMap.get("setMealId");
		CheckupSetMealModel csmModel = new CheckupSetMealModel(dao);
		boolean isUsed = false;
		try {
			isUsed = csmModel.setMealUsed(setMealId);
		} catch (ModelDataOperationException e) {
			logger.error("Judge this checkup set meal is used failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isUsed", isUsed);
		res.put("body", resBodyMap);
	}
}
