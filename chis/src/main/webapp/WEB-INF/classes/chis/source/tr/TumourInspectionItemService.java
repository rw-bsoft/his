/**
 * @(#)TumourInspectionItemService.java Created on 2014-11-5 下午2:17:03
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourInspectionItemService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourInspectionItemService.class);

	/**
	 * 
	 * @Description:保存肿瘤检查项目
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-5 下午2:21:32
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourInspectionItem(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String entryName = (String) req.get("schema");
		String itemType = (String) reqBodyMap.get("itemType");
		String definiteItemName = (String) reqBodyMap.get("definiteItemName");
		String itemId = (String) reqBodyMap.get("itemId");
		TumourInspectionItemModel tiiModel = new TumourInspectionItemModel(dao);
		boolean existTii = false;
		try {
			existTii = tiiModel.existTumourInspectionItem(itemType,
					definiteItemName,itemId);
		} catch (ModelDataOperationException e) {
			logger.error("Determines whether the record exists in the table.",
					e);
			throw new ServiceException(e);
		}
		if (existTii) {
			throw new ServiceException(Constants.CODE_RECORD_EXSIT,
					"该项目已经在该项目类别中存在！");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = tiiModel.saveTumourInspectionItem(op, entryName, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save the tumour inspection item record failure.", e);
			throw new ServiceException(e);
		}
		res.put("body",rsMap);
	}
}
