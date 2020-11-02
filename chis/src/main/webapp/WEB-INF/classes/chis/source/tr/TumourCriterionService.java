/**
 * @(#)TumourCriterionService.java Created on 2014-3-23 上午10:41:27
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
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourCriterionService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourCriterionService.class);

	/**
	 * 
	 * @Description:保存问卷标准
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-3-23 上午10:44:33
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTQCriterion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		Map<String, Object> tqcMap = (Map<String, Object>) reqBodyMap
				.get("tqcRecord");
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTQCriterion(op, tqcMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save tumour questionnaire criterion.", e);
			throw new ServiceException(e);
		}
		String QCId = (String) tqcMap.get("recordId");
		if ("update".equals(op)) {
			String recordId = (String) tqcMap.get("recordId");
			String QMId = (String) tqcMap.get("QMId");
			// 删除标准明细
			try {
				tcModel.deleteQuestCriterionDetail(recordId, QMId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Failed to delete tumour questionnaire criterion detail",
						e);
				throw new ServiceException(e);
			}
		} else {
			QCId = (String) rsMap.get("recordId");
		}
		// 保存标准明细
		List<Map<String, Object>> tqcdList = (List<Map<String, Object>>) reqBodyMap
				.get("tqcdList");
		for (int i = 0, len = tqcdList.size(); i < len; i++) {
			Map<String, Object> tqcdMap = tqcdList.get(i);
			tqcdMap.put("QCId", QCId);
			try {
				tcModel.saveQuestCriterionDetail("create", tqcdMap, true);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Failed to save tumour questionnaire criterion detail.",
						e);
				throw new ServiceException(e);
			}
		}
		res.put("body", rsMap);
	}

	@SuppressWarnings("unchecked")
	public void doRemoveTQCriterion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String pkey = (String) reqBodyMap.get("pkey");
		String QMId = (String) reqBodyMap.get("QMId");
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
		// 删除标准明细
		try {
			tcModel.deleteQuestCriterionDetail(pkey, QMId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to delete tumour questionnaire criterion detail", e);
			throw new ServiceException(e);
		}
		// 删除标准
		try {
			tcModel.deleteQuestCriterion(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to delete tumour questionnaire criterion", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:保存筛转高危标准<br/> 年度 高危类别 高危因素 可唯一标识一条标准
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-3-27 上午9:09:37
	 * @Modify:2015-05-14 11:22 by ChenXianRui <br/>
	 * 	上海需求：放开同一年度、同一类别、同一来源、同一高危因素标准只能一个的判断，因为即使前面这些都相同，也还有可能是不同的检查项目标准。
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTHRCriterion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
//		int year = Calendar.getInstance().get(Calendar.YEAR);
//		if(reqBodyMap.get("year") instanceof String){
//			year = Integer.parseInt((String)reqBodyMap.get("year"));
//		}else{
//			year = (Integer) reqBodyMap.get("year");
//		}
//		String highRiskType = (String) reqBodyMap.get("highRiskType");
//		String highRiskFactor = (String) reqBodyMap.get("highRiskFactor");
//		String highRiskSource = (String) reqBodyMap.get("highRiskSource");
		boolean isExist = false;
//		if ("create".equals(op)) {
//			try {
//				isExist = tcModel.isExistCriterion(year, highRiskType,
//						highRiskFactor,highRiskSource);
//			} catch (ModelDataOperationException e) {
//				logger.error(
//						"Judge tumour turn high risk criterion is exist failure.",
//						e);
//				throw new ServiceException(e);
//			}
//		}
		Map<String, Object> resMap = null;
//		if (isExist) {
//			resMap = new HashMap<String, Object>();
//			resMap.put("isExist", isExist);
//			resMap.put("msg", "该年分该来源该类别该高危因素的初筛转高危标准已存在！");
//		} else {
			try {
				resMap = tcModel.saveTHRCriterion(op, reqBodyMap, true);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save tumour high risk criterion.", e);
				throw new ServiceException(e);
			}
//		}
		if (resMap == null) {
			resMap = new HashMap<String, Object>();
		}
		resMap.put("isExist", isExist);
		res.put("body", resMap);
	}

	/**
	 * 
	 * @Description:检查是否符合转高危标准
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-28 下午4:46:14
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doCheckTHRCriterion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		String highRiskSource = (String) reqBodyMap.get("highRiskSource");// 高危来源
		String recordId = (String) reqBodyMap.get("recordId");// 初筛记录ID
		if (StringUtils.isEmpty(highRiskType)) {
			throw new ServiceException(Constants.CODE_UNKNOWN_ERROR,
					"肿瘤类型不能为空！");
		}
		boolean passport = false;
		//判断该人是否已经迁出或死亡
		boolean isEOD = false;
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		try {
			isEOD = hrModel.isEmigrationOrDeath(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("To determine the health record of this person whether emigration or death failure.", e);
			throw new ServiceException(e);
		}
		if(isEOD == false){
			TumourCriterionModel tcModel = new TumourCriterionModel(dao);
			// 获取可用高危标准
			List<Map<String, Object>> thrcList = null;
			try {
				thrcList = tcModel.getTHRCriterion(highRiskType,highRiskSource);
			} catch (ModelDataOperationException e) {
				logger.error("Get tumour hight risk criterion record failure.", e);
				throw new ServiceException(e);
			}
			if (thrcList != null && thrcList.size() > 0) {
				for (int i = 0, len = thrcList.size(); i < len; i++) {
					Map<String, Object> thrcMap = thrcList.get(i);
					String highRiskFactor = (String) thrcMap.get("highRiskFactor");
					String hrcId = (String) thrcMap.get("hrcId");
					String PSItemRelation = (String) thrcMap.get("PSItemRelation");//初筛检查项目关系
					String traceItemRelation = (String) thrcMap.get("traceItemRelation");//追踪检查项目关系
					if ("1".equals(highRiskFactor)) {// 问卷症状--有
						try {
							passport = tcModel.isExistTumourQuestionnaireSymptom(
									empiId, highRiskType, hrcId);
						} catch (ModelDataOperationException e) {
							logger.error(
									"Judge whether exist tumour questionnaire symption failure.",
									e);
							throw new ServiceException(e);
						}
						if (passport) {
							try {
								passport = tcModel.isAccordWithCheckResult(
										recordId, hrcId, highRiskType,PSItemRelation,traceItemRelation);
							} catch (ModelDataOperationException e) {
								logger.error(
										"Judge whether accord with check resule failure.",
										e);
								throw new ServiceException(e);
							}
						} 
						if (passport) {
							break;
						} 
					}
					//高危因素选择“家族肿瘤史”“肿瘤疾病史”的标准，直接判断检查标准
					if ("2".equals(highRiskFactor)) {// 家族肿瘤史--有
//						try {
//							passport = tcModel.isExitCancer(empiId, new String[] {
//									"0706", "0806", "0906", "1006" });
//						} catch (ModelDataOperationException e) {
//							logger.error(
//									"Judge whether exist cancer record of EHR_PastHistory failure.",
//									e);
//							throw new ServiceException(e);
//						}
						if (passport) {
							break;
						} else {
							try {
								passport = tcModel.isAccordWithCheckResult(
										recordId, hrcId, highRiskType,PSItemRelation,traceItemRelation);
							} catch (ModelDataOperationException e) {
								logger.error(
										"Judge whether accord with check resule failure.",
										e);
								throw new ServiceException(e);
							}
							if (passport) {
								break;
							}
						}
					}
					if ("3".equals(highRiskFactor)) {// 肿瘤疾病史--有
//						try {
//							passport = tcModel.isExitCancer(empiId,
//									new String[] { "0206" }, new String[] { "02" });
//						} catch (ModelDataOperationException e) {
//							logger.error(
//									"Judge whether exist cancer record of EHR_PastHistory failure.",
//									e);
//							throw new ServiceException(e);
//						}
						if (passport) {
							break;
						} else {
							try {
								passport = tcModel.isAccordWithCheckResult(
										recordId, hrcId, highRiskType,PSItemRelation,traceItemRelation);
							} catch (ModelDataOperationException e) {
								logger.error(
										"Judge whether accord with check resule failure.",
										e);
								throw new ServiceException(e);
							}
							if (passport) {
								break;
							} 
						}
					}
				}
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		//resBodyMap.put("passport", passport);
		//turnTHRSucceed 为true 表示转高危成功
		boolean turnTHRSucceed = false;
		if(passport){
			//增加高危记录
			TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
			reqBodyMap.put("screeningId", recordId);
			dao.beginTransaction();
			Map<String, Object>  thrMap = null;
			try {
				thrMap = thrModel.turnHighRiskToAddTHR(reqBodyMap);
				dao.commitTransaction();
			} catch (ModelDataOperationException e) {
				logger.error("To add tumour high risk record failure.", e);
				dao.rollbackTransaction();
				throw new ServiceException(e);
			}finally{
				dao.closeSession();
			}
			turnTHRSucceed = (Boolean) thrMap.get("succeed");
		}
		resBodyMap.put("passport", passport);
		resBodyMap.put("turnTHRSucceed", turnTHRSucceed);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:保存肿瘤高危标准检查项目标准
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-6 上午10:25:18
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourHRCDetail(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String criterionSerialNumber = (String) reqBodyMap.get("criterionSerialNumber");
		String inspectionItem = (String) reqBodyMap.get("inspectionItem");
		String hrcdId = (String) reqBodyMap.get("hrcdId");
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
		boolean existTHRCIC = false;
		try {
			existTHRCIC = tcModel.existTHRCIC(criterionSerialNumber,inspectionItem, hrcdId);
		} catch (ModelDataOperationException e) {
			logger.error("Determines whether the record exists in the table.",
					e);
			throw new ServiceException(e);
		}
		if (existTHRCIC) {
			throw new ServiceException(Constants.CODE_RECORD_EXSIT,
					"该项目已经在该标准中存在！");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = tcModel.saveTHRCIC(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Save tumour hight risk inspection item criterion record failure",
					e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
	}
	
	/**
	 * 
	 * @Description:删除肿瘤高危确诊标准<br/>
	 *                         删除主表记录，标准项目，问卷（高危）
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-18 下午3:16:23
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveTHRCriterion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String hrcId = (String) reqBodyMap.get("hrcId");
		String highRiskFactor = (String) reqBodyMap.get("highRiskFactor");
		TumourCriterionModel tcModel = new TumourCriterionModel(dao);
		try {
			tcModel.removeTHRCriterion(hrcId, highRiskFactor);
		} catch (ModelDataOperationException e) {
			logger.error("Delete tumour high risk criterion failure.", e);
			throw new ServiceException(e);
		}
	}
}
