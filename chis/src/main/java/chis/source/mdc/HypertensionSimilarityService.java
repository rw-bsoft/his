/**
 * @(#)HypertensionService.java Created on 2012-1-18 上午9:57:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.DiagnosisType;
import chis.source.dic.WorkType;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.HealthRecordService;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionSimilarityService extends HypertensionService {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionSimilarityService.class);

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	@SuppressWarnings("unchecked")
	public void doSaveHypertensionSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getPhrIdByEmpiId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				body.put("phrId", phrId);
			}
		}
		body.put("diagnosisType", DiagnosisType.YS);
		String op = (String) req.get("op");
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_HypertensionSimilarity, body, false);
		res.put("body", m);
		String similarityId = (String) body.get("similarityId");
		if ("create".equals(op)) {
			similarityId = (String) m.get("similarityId");
		}
		body.put("similarityId", similarityId);
		dao.doSave(op, BSCHISEntryNames.MDC_HypertensionSimilarityC, body,
				false);
		vLogService.saveVindicateLog(MDC_HypertensionSimilarity, op,
				similarityId, dao, empiId);

		// 判断是否需要为35岁首诊测压保存数据
		HypertensionFCBPModel hfcbpModel = new HypertensionFCBPModel(dao);
		boolean needCreateHFCBP = false;
		try {
			needCreateHFCBP = hfcbpModel.isNeedHypertensionFCBP(empiId);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		if (needCreateHFCBP) {
			Map<String, Object> fcbpMap = new HashMap<String, Object>();
			fcbpMap.putAll(body);
			int constriction = (Integer) body.get("constriction");
			int diastolic = (Integer) body.get("diastolic");
			int hypertensionLevel = MDCBaseModel.decideHypertensionGrade(
					constriction, diastolic);
			fcbpMap.put("hypertensionLevel", hypertensionLevel);
			String userId = UserUtil.get(UserUtil.USER_ID);
			String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
			Date curDate = new Date();
			fcbpMap.put("measureDate", curDate);
			fcbpMap.put("measureDoctor", userId);
			fcbpMap.put("measureUnit", manaUnitId);
			fcbpMap.put("createUnit", manaUnitId);
			fcbpMap.put("createUser", userId);
			fcbpMap.put("createDate", curDate);
			try {
				hfcbpModel.saveHypertensionFCBP("create", fcbpMap, false);
			} catch (ModelDataOperationException e) {
				throw new ServiceException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHypertensionSimilarityCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String phrId = (String) body.get("phrId");
		String empiId = (String) body.get("empiId");
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getPhrIdByEmpiId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				body.put("phrId", phrId);
			}
		}
		String op = (String) req.get("op");
		Map<String, Object> m = dao.doSave(op,
				BSCHISEntryNames.MDC_HypertensionSimilarityC, body, false);
		res.put("body", m);
		String recordId = (String) body.get("recordId");
		if ("create".equals(op)) {
			recordId = (String) m.get("recordId");
		}
		vLogService.saveVindicateLog(MDC_HypertensionSimilarityC, op, recordId,
				dao, empiId);
	}

	@SuppressWarnings("unchecked")
	public void doSaveHighRiskHypertensionSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String similarityId = (String) body.get("similarityId");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("diagnosisType", DiagnosisType.GW);
		map.put("similarityId", body.get("similarityId"));
		UserRoleToken ur = UserRoleToken.getCurrent();
		map.put("diagnosisUnit", ur.getManageUnitId());
		map.put("diagnosisDate", new Date());
		map.put("diagnosisDoctor", ur.getUserId());
		List<Map<String, Object>> list = null;
		try {
			dao.doSave("update", BSCHISEntryNames.MDC_HypertensionSimilarity,
					map, false);
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			list = dao.doList(cnd, "a.registerDate desc",
					MDC_HypertensionSimilarityC);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to Save High Risk hypertension similarity record by empiId.",
					e);
			throw new ServiceException("高血压转高危失败！", e);
		}
		HypertensionRiskModel hsm = new HypertensionRiskModel(dao);
		int constriction = 0;
		int diastolic = 0;
		if (list != null && list.size() > 0) {
			constriction = Integer.parseInt(list.get(0).get("constriction")
					+ "");
			diastolic = Integer.parseInt(list.get(0).get("diastolic") + "");
		}
		hsm.insertHypertensionRisk(empiId, phrId, constriction, diastolic, "7");
		res.put("body", map);
		vLogService.saveVindicateLog(MDC_HypertensionSimilarity, "6",
				similarityId, dao, empiId);
	}

	@SuppressWarnings("unchecked")
	public void doSaveConfirmHypertensionSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ModelDataOperationException, ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String similarityId = (String) body.get("similarityId");
		String empiId = (String) body.get("empiId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("diagnosisType", DiagnosisType.QZ);
		map.put("similarityId", similarityId);
		UserRoleToken ur = UserRoleToken.getCurrent();
		map.put("diagnosisUnit", ur.getManageUnitId());
		map.put("diagnosisDate", new Date());
		map.put("diagnosisDoctor", ur.getUserId());
		dao.doSave("update", BSCHISEntryNames.MDC_HypertensionSimilarity, map,
				false);
		map.putAll(body);
		res.put("body", map);
		map.put("hasHyRecord", true);
		vLogService.saveVindicateLog(MDC_HypertensionSimilarity, "6",
				similarityId, dao, empiId);

		HypertensionModel recordModel = new HypertensionModel(dao);
		List<Map<String, Object>> l = recordModel
				.findHypertensionRecordByEmpiId(empiId);
		if (l == null || l.size() == 0) {
			map.put("hasHyRecord", false);
			Map<String, Object> workList = new HashMap<String, Object>();
			Calendar c = Calendar.getInstance();
			workList.put("recordId", body.get("phrId"));
			workList.put("empiId", body.get("empiId"));
			workList.put("beginDate", c.getTime());
			c.add(Calendar.YEAR, 1);
			workList.put("endDate", c.getTime());
			workList.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
			workList.put("count", 1);
			workList.put("doctorId", ur.getUserId());
			workList.put("manaUnitId", ur.getManageUnitId());
			recordModel.addHypertensionRecordWorkList(workList);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveEliminateHypertensionSimilarity(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws PersistentDataOperationException, DateParseException,
			ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String similarityId = (String) body.get("similarityId");
		String empiId = (String) body.get("empiId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("diagnosisType", DiagnosisType.PC);
		map.put("similarityId", body.get("similarityId"));
		UserRoleToken ur = UserRoleToken.getCurrent();
		map.put("diagnosisUnit", ur.getManageUnitId());
		map.put("diagnosisDate", new Date());
		map.put("diagnosisDoctor", ur.getUserId());
		dao.doSave("update", BSCHISEntryNames.MDC_HypertensionSimilarity, map,
				false);
		res.put("body", map);
		vLogService.saveVindicateLog(MDC_HypertensionSimilarity, "6",
				similarityId, dao, empiId);
	}

	/**
	 * 
	 * @Description:获取健康档案编号
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws PersistentDataOperationException
	 * @author ChenXianRui 2014-1-13 下午3:22:52
	 * @Modify:
	 */
	private String getPhrIdByEmpiId(String empiId, BaseDAO dao)
			throws PersistentDataOperationException {
		HealthRecordModel hrm = new HealthRecordModel(dao);
		String phrId = null;
		try {
			phrId = hrm.getPhrId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("保存高血压疑似时，获取phrId失败！", e);
			throw new PersistentDataOperationException("Failed to get phrId.",
					e);
		}
		return phrId;
	}

	/**
	 * 
	 * @Description:获取高血压疑似记录ID
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws PersistentDataOperationException
	 * @author ChenXianRui 2014-1-13 下午3:27:23
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetSimilarityIdByEmpiId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.diagnosisType", "s",
				DiagnosisType.YS);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			List<Map<String, Object>> hsList = dao.doList(cnd,
					"a.inputDate desc", MDC_HypertensionSimilarity);
			res.put("body", hsList);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to select hypertension similarity record by empiId.",
					e);
			throw new ServiceException("获取高血压疑似记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:依据empiId判断某人是否已经存在高血压疑似记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-2-20 下午2:51:44
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doIsExistHypertensionSimilarityRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		HypertensionSimilarityModel hsModel = new HypertensionSimilarityModel(
				dao);
		List<Map<String, Object>> hsList = null;
		try {
			hsList = hsModel.getHypertensionSimilarityByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to select hypertension similarity record by empiId.",
					e);
			throw new ServiceException("获取高血压疑似记录失败！", e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		boolean isExistHS = false;
		if (hsList != null && hsList.size() > 0) {
			isExistHS = true;
			resBodyMap.put("hsData", hsList.get(0));
		}
		resBodyMap.put("isExistHS", isExistHS);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:依据empiId判断某人是否已经存在高血压高危核实记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-2-20 下午2:51:44
	 * @Modify:
	 */
	public void doIfHypertensionRiskExist(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		HypertensionRiskModel hsModel = new HypertensionRiskModel(dao);
		List<Map<String, Object>> hsList = null;
		try {
			hsList = hsModel.getHypertensionRiskByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to select hypertension risk record by empiId.", e);
			throw new ServiceException("获取高血压高危核实记录失败！", e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		boolean isExistHR = false;
		if (hsList != null && hsList.size() > 0) {
			isExistHR = true;
			resBodyMap.put("hrData", hsList.get(0));
		}
		resBodyMap.put("isExistHR", isExistHR);
		Map<String, Object> hrMap = null;
		try {
			HealthRecordModel hrs = new HealthRecordModel(dao);
			hrMap = hrs.getHealthRecordByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to select hypertension risk record by empiId.", e);
			throw new ServiceException("获取高血压高危核实记录失败！", e);
		}
		boolean isExistHealthRecord = false;
		if (hrMap != null && hrMap.size() > 0) {
			isExistHealthRecord = true;
		}
		resBodyMap.put("isExistHealthRecord", isExistHealthRecord);
		Map<String, Object> hyMap = null;
		try {
			HypertensionModel hym = new HypertensionModel(dao);
			hyMap = hym.getHypertensionByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to select hypertension risk record by empiId.", e);
			throw new ServiceException("获取高血压高危核实记录失败！", e);
		}
		boolean isExistHyRecord = false;
		if (hyMap != null && hyMap.size() > 0) {
			isExistHyRecord = true;
		}
		resBodyMap.put("isExistHyRecord", isExistHyRecord);
		res.put("body", resBodyMap);
	}
}
