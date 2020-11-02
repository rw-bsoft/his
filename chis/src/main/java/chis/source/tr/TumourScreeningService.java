/**
 * @(#)TumourScreeningService.java Created on 2014-3-28 下午4:44:23
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourScreeningService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourScreeningService.class);

	/**
	 * 
	 * @Description:保存T初筛信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-3-28 下午4:55:38
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourScreening(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		if (StringUtils.isEmpty(phrId)) {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			try {
				phrId = hrModel.getPhrId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get phrId by empiId failure.", e);
				throw new ServiceException(e);
			}
		}
		String op = (String) req.get("op");
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tsModel.saveTumourScreening(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save tumour screening data.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) reqBodyMap.get("recordId");
		if ("create".equals(op)) {
			recordId = (String) reqBodyMap.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_TumourScreening, op, recordId, dao,
				empiId);
		res.put("body", rsMap);
	}

	/**
	 * 
	 * @Description:注销T初筛记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-3-31 下午4:09:10
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutTumourScreening(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String recordId = (String) reqBodyMap.get("recordId");
		String cancellationReason = (String) reqBodyMap
				.get("cancellationReason");
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		try {
			tsModel.logoutTumourScreeningRecord(recordId, cancellationReason);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to logout tumour screening data.", e);
			throw new ServiceException(e);
		}
		String empiId = (String) req.get("empiId");
		vLogService.saveVindicateLog(MDC_TumourScreening, "3", recordId, dao,
				empiId);
	}

	/**
	 * 
	 * @Description:依据empiId <br/>
	 *                       #判断是否存在健康档案，如果存在 则返回phrId, <br/>
	 *                       #判断是否有肿瘤既往史记录，如果没有不能确诊<br/>
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-23 上午9:24:39
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doConfirmedQualificationExamination(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		// 获取phrId
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		String phrId = null;
		try {
			phrId = hrModel.getPhrId(empiId);
		} catch (chis.source.ModelDataOperationException e) {
			logger.error("Get phrId by empiId failure.", e);
			throw new ServiceException(e);
		}
		// 判断健康档案注销原因 是否为 迁出或为死亡
		boolean isED = false;
		try {
			isED = hrModel.isEmigrationOrDeath(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"To determine the health record of this person whether emigration or death failure.",
					e);
			throw new ServiceException(e);
		}
		TumourPastMedicalHistoryModel tpmhModel = new TumourPastMedicalHistoryModel(
				dao);
		boolean isExistTPMH = false;
		try {
			isExistTPMH = tpmhModel.isExistTPMHRecord(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Judge is exist tumour past medical history record failure.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isExistTPMH", isExistTPMH);
		resBodyMap.put("isED", isED);
		if (phrId != null) {
			resBodyMap.put("phrId", phrId);
			resBodyMap.put("success", true);
			resBodyMap.put("msg", "该人已有健康档案!");
		} else {
			resBodyMap.put("success", false);
			resBodyMap.put("msg", "该人没有健康档案，请先为该人创建健康档案!");
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:保存、更新检查结果记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-4-1 上午10:20:39
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckResult(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tsModel.saveCheckResult(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save tumour screening check result failure.", e);
			throw new ServiceException(e);
		}
		// 更新初筛记录中 “是否追踪” 和 “追踪规范”的状态
		String screeningId = (String) reqBodyMap.get("screeningId");
		String criterionType = (String) reqBodyMap.get("criterionType");
		String checkResult = (String) reqBodyMap.get("checkResult");
		boolean isCriterionTypeIsTrace = false;// 是否要检查 项目性质 是否追踪
		if ("1".equals(criterionType)) {// 初筛
			isCriterionTypeIsTrace = true;
		} else {
			try {
				isCriterionTypeIsTrace = tsModel
						.getCriterionTypeIsTrace(screeningId);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
		if (isCriterionTypeIsTrace) {
			// 更新 初筛记录中 是否追踪 为 是
			try {
				tsModel.updateIsTraceOfTS(screeningId);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
		boolean isUpdateTraceNorm = false;
		if (S.isNotEmpty(checkResult)) {
			isUpdateTraceNorm = true;
		} else {
			try {
				isUpdateTraceNorm = tsModel
						.getCheckItemIsHasResult(screeningId);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
		if (isUpdateTraceNorm) {
			// 更新 初筛记录中 追踪规范 为 是
			try {
				tsModel.updateTraceNormOfTS(screeningId);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
		//维护初筛主记录中检查结果
		List<Map<String, Object>> rsList = null;
		try {
			rsList = tsModel.getTSCheckItemList(screeningId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if(rsList != null && rsList.size() > 0){
			Map<String, Object> tsMap = new HashMap<String, Object>();
			tsMap.put("recordId", screeningId);
			for(int i=0;i<rsList.size();i++){
				if(i>3){
					break;
				}
				Map<String, Object> tsciMap = rsList.get(i);
				int idx = i+1;
				tsMap.put("checkItem"+idx, tsciMap.get("checkItem"));
				tsMap.put("checkResult"+idx, tsciMap.get("checkResult"));
			}
			//检查阳性
			String tcpv = "0";
			if("2".equals(checkResult)){
				tcpv = "1";
			}
			if("1".equals(tcpv)){//阳性时修改其值
				tsMap.put("checkPositive", tcpv);//检查阳性
				tsMap.put("syntheticalPositive", "1");//综合阳性
			}
			try {
				tsModel.saveTumourScreening("update", tsMap, false);
			} catch (ModelDataOperationException e) {
				logger.error("Update check result of MDC_TumourScreening failure.", e);
				throw new ServiceException(e);
			}
		}
		//检查转高危（自动转高危）
		Map<String, Object> tsDataMap = (Map<String, Object>) reqBodyMap.get("tsData");
		boolean turnTHRSucceed = false;
		try {
			turnTHRSucceed = tsModel.autoTSTurnTHR(tsDataMap);
		} catch (ModelDataOperationException e) {
			logger.error("tumour screening auto turn tumour high risk failure.", e);
			throw new ServiceException(e);
		}
		if(rsMap == null){
			rsMap = new HashMap<String, Object>();
		}
		rsMap.put("turnTHRSucceed", turnTHRSucceed);
		res.put("body", rsMap);
	}
}
