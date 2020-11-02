/**
 * @(#)TumourSeemingly.java Created on 2014-4-21 上午11:17:34
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.YesNo;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourSeeminglyService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(TumourSeeminglyService.class);

	/**
	 * 
	 * @Description: 保存疑似肿瘤记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-21 上午11:22:15
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourSeemingly(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourSeeminglyModel tsModel = new TumourSeeminglyModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tsModel.saveTumourSeemingly(op, reqBodyMap, false);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour seemingly record failure.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) reqBodyMap.get("recordId");
		if("create".equals(op)){
			recordId = (String) rsMap.get("recordId");
		}
		String empiId = (String) reqBodyMap.get("empiId");
		vLogService.saveVindicateLog(MDC_TumourSeemingly, op, recordId, dao, empiId);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:疑似肿瘤符合
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-21 下午1:56:22
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourSeeminglyRecheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String recheckResult = (String) reqBodyMap.get("recheckResult");
		if("1".equals(recheckResult)){
			reqBodyMap.put("nature", "2");
		}
		if("2".equals(recheckResult)){
			reqBodyMap.put("nature", "1");
			//保存到初筛
			Map<String, Object> tsMap = new HashMap<String, Object>();
			tsMap.put("empiId", reqBodyMap.get("empiId"));
			tsMap.put("highRiskType", reqBodyMap.get("highRiskType"));
			tsMap.put("year", reqBodyMap.get("year"));
			Date today = Calendar.getInstance().getTime();
			tsMap.put("TQDate", today);
			tsMap.put("screeningDate", today);
			String userId = UserUtil.get(UserUtil.USER_ID);
			tsMap.put("screeningDoctor", userId);
			tsMap.put("highRiskSource", "2");
			tsMap.put("nature", "1");
			tsMap.put("highRiskMark", "n");
			tsMap.put("criterionMark", "n");
			tsMap.put("highRiskFactor", reqBodyMap.get("highRiskFactor"));
			tsMap.put("status", "0");
			tsMap.put("createUser", userId);
			String unitId = UserUtil.get(UserUtil.MANAUNIT_ID);
			tsMap.put("createUnit", unitId);
			tsMap.put("createDate", today);
			tsMap.put("lastModifyUnit", unitId);
			tsMap.put("lastModifyUser", userId);
			tsMap.put("lastModifyDate", today);
			TumourScreeningModel tsm = new TumourScreeningModel(dao);
			try {
				tsm.saveTumourScreening("create", tsMap, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save tumour screening record failure.", e);
				throw new ServiceException(e);
			}
		}
		if("3".equals(recheckResult)){
			reqBodyMap.put("transferTreatment", YesNo.YES);
		}
		TumourSeeminglyModel tsModel = new TumourSeeminglyModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tsModel.saveTumourSeemingly(op, reqBodyMap, false);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour seemingly record failure.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) reqBodyMap.get("recordId");
		if("create".equals(op)){
			recordId = (String) rsMap.get("recordId");
		}
		String empiId = (String) reqBodyMap.get("empiId");
		vLogService.saveVindicateLog(MDC_TumourSeemingly, op, recordId, dao, empiId);
		res.put("body", rsMap);
	}
}
