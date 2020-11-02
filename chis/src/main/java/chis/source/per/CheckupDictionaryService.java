/**
 * @(#)CheckupDictionaryService.java Created on 2012-4-23 上午10:24:36
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SendDictionaryReloadSynMsg;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupDictionaryService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CheckupDictionaryService.class);

	/**
	 * 删除字典项
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDelDicItem(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		String pkey = (String) req.get("pkey");
		CheckupDictionaryModel cdmModel = new CheckupDictionaryModel(dao);
		boolean isUsed = false;
		try {
			isUsed = cdmModel.checkDictionaryItemIsUsed(pkey);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Check physical examination dictionary item is used failed.",
					e);
			throw new ServiceException(e);
		}
		if (isUsed) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "该体检项目已经被套餐记录使用,无法删除!");
			return;
		}
		try {
			cdmModel.removeDictionaryItem(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("delete physical examination dictionary item failed.",
					e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存字典项
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDictionaryItem(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		List<?> cnds = (List<?>) reqBodyMap.get("cnds");
		CheckupDictionaryModel cdmModel = new CheckupDictionaryModel(dao);
		boolean isExist = false;
		try {
			isExist = cdmModel.checkDicItemIsExist(cnds);
		} catch (ModelDataOperationException e) {
			logger.error("Check dictionary is exist failed.", e);
			throw new ServiceException(e);
		}
		if (isExist) {
			res.put(RES_CODE, Constants.CODE_RECORD_EXSIT);
			res.put(RES_MESSAGE, "该体检项目已存在!");
			return;
		}
		reqBodyMap.remove("cnds");
		String op = (String) req.get("op");
		try {
			if ("create".equals(op)) {// 获取默认排序序号
				Integer orderNo = 0;
				if (!BSCHISUtil.isBlank(reqBodyMap.get("orderNo"))) {
					orderNo = (Integer) reqBodyMap.get("orderNo");
				}
				String projectType = (String) reqBodyMap.get("projectType");
				if (orderNo == null || orderNo == 0) {
					reqBodyMap.put("orderNo",
							cdmModel.getDefaultOrderNo(projectType));
				}
			}
			Map<String, Object> resBody = cdmModel.saveDicItem(op, reqBodyMap,
					true);
			resBody.put("orderNo", reqBodyMap.get("orderNo"));
			res.put("body", resBody);
			//更新加载体检明细字典
			DictionaryController.instance().reload("chis.dictionary.perCombo");
			DictionaryController.instance().reload("chis.dictionary.projects");
			//发送字典同步稍息
			SendDictionaryReloadSynMsg.instance().sendSynMsg("chis.dictionary.perCombo");
		} catch (ModelDataOperationException e) {
			logger.error("Save dictionary item failed.", e);
			throw new ServiceException(e);
		}
	}
}
