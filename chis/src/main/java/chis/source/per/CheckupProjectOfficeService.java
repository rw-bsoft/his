/**
 * @(#)CheckupProjectOfficeService.java Created on 2012-4-17 上午9:20:32
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
import chis.source.util.SchemaUtil;
import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 科室维护服务
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupProjectOfficeService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CheckupProjectOfficeService.class);

	/**
	 * 保存科室
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveProjectOffice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		List<?> cnd = (List<?>) reqBodyMap.get("cnd");
		CheckupProjectOfficeModel cpoModel = new CheckupProjectOfficeModel(dao);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = cpoModel.getProjectOfficeList(cnd);
		} catch (ModelDataOperationException e) {
			logger.error("Get checkup project office failed", e);
			throw new ServiceException(e);
		}
		reqBodyMap.remove("cnd");
		if (null != rsList && rsList.size() > 0) {
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap = SchemaUtil.setDictionaryMessageForForm(reqBodyMap,
					PER_ProjectOffice);
			res.put("body", resMap);
			res.put("isExist", true);
			return;
		}
		res.put("isExist", false);
		String op = (String) req.get("op");
		Map<String, Object> rsMap = null;
		try {
			rsMap = cpoModel.saveProjectOffice(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save checkup project office failed.", e);
			throw new ServiceException(e);
		}
		if (null != rsMap && rsMap.get("projectOfficeCode") != null) {
			reqBodyMap.put("projectOfficeCode", rsMap.get("projectOfficeCode"));
		} else {
			reqBodyMap.putAll(rsMap);
		}
		// 更新科室字典
		DictionaryController.instance().reload("chis.dictionary.projectOffice");
		//发送字典同步稍息
		SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.projectOffice");
		reqBodyMap = SchemaUtil.setDictionaryMessageForForm(reqBodyMap,
				PER_ProjectOffice);
		res.put("body", reqBodyMap);
		return;
	}

	/**
	 * 删除科室
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doDeleteProjectOffice(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String pkey = (String) reqBodyMap.get("pkey");
		boolean isUsed = false;
		CheckupProjectOfficeModel cpoModel = new CheckupProjectOfficeModel(dao);
		try {
			isUsed = cpoModel.ckeckRemoveProjectOfficeIsUsed(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Check project office is used failed.", e);
			throw new ServiceException(e);
		}
		res.put("isUsed", isUsed);
		if (isUsed) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "该科室已经被套餐记录使用,无法删除!");
			return;
		}
		try {
			cpoModel.deleteProjectOffice(pkey);
			// 更新科室字典
			DictionaryController.instance().reload("chis.dictionary.projectOffice");
			//发送字典同步稍息
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.projectOffice");
		} catch (ModelDataOperationException e) {
			logger.error("delete project office failed", e);
			throw new ServiceException(e);
		}
	}

}
