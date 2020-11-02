/**
 * @(#)TumourPastMedicalHistoryService.java Created on 2014-8-26 下午4:12:04
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPastMedicalHistoryService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourPastMedicalHistoryService.class);

	/**
	 * 
	 * @Description:保存肿瘤既往史
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-8-26 下午4:23:54
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourPMH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourPastMedicalHistoryModel tpmhModel = new TumourPastMedicalHistoryModel(
				dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tpmhModel.saveTPastMedicalHostory(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour past medical history record failure.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:判断是否有既往史
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-4-2 上午9:24:30
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doIsExistTPMH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		boolean isExistTPMH = false;
		if (S.isNotEmpty(empiId)) {
			TumourPastMedicalHistoryModel tpmhModel = new TumourPastMedicalHistoryModel(
					dao);
			try {
				isExistTPMH = tpmhModel.isExistTPMHRecord(empiId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Judge is exist tumour past medical history record failure.",
						e);
				throw new ServiceException(e);
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isExistTPMH", isExistTPMH);
		res.put("body", resBodyMap);
	}
}
