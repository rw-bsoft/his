/**
 * @(#)HypertensionFirstClinicalPressureService.java Created on 2014-1-23 下午2:07:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.DiagnosisType;
import chis.source.dic.WorkType;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;

/**
 * @description 疑似筛选条件：收缩压≥140和/或舒张压≥90这个是疑似的条件 35岁首诊测压：满35岁后，每年第一次测压为首诊测压（一年一次）
 *              35岁首诊测压 -如果血压正常 数据存 首诊表， 如果不正常存 首诊和和疑似表 -->时行核实业务操作
 *              如果该人已有疑似记录--则当年首诊测压为核实测压，数据存核实表
 * 
 *              如果这个人没有疑似的，要满足疑似条件才创建疑似记录； 如果这个人已经有疑似的，那首诊测压记录就作为疑似核实记录。
 *              其他情况，就跟疑似无关了，只管存首诊测压自己表
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFCBPService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionFCBPService.class);

	public void doSaveHyperFCBPPHIS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		HypertensionFCBPModel hfm = new HypertensionFCBPModel(dao);
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getPhrIdByEmpiId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				reqBodyMap.put("phrId", phrId);
			}
		}
		String hypertensionHistory = (String) reqBodyMap
				.get("hypertensionHistory");
		try {
			hfm.saveHypertensionFCBP(op, reqBodyMap, true);
			hfm.updateMsBcjl(reqBodyMap, true);
			if (hypertensionHistory != null && "1".equals(hypertensionHistory)) {
				HypertensionModel hm = new HypertensionModel(dao);
				List<Map<String, Object>> records = hm
						.findHypertensionRecordByEmpiId(empiId);
				WorkListModel wlk = new WorkListModel(dao);
				boolean isExistWl = wlk.isExistWorkList(phrId, empiId,
						WorkType.MDC_HYPERTENSIONRECORD, dao);
				if ((records == null || records.size() == 0) && !isExistWl) {
					// 没有高血压档案
					Map<String, Object> workList = new HashMap<String, Object>();
					Calendar c = Calendar.getInstance();
					workList.put("recordId", phrId);
					workList.put("empiId", empiId);
					workList.put("beginDate", c.getTime());
					c.add(Calendar.YEAR, 1);
					workList.put("endDate", c.getTime());
					workList.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
					workList.put("count", 1);
					UserRoleToken ur = UserRoleToken.getCurrent();
					workList.put("doctorId", ur.getUserId());
					workList.put("manaUnitId", ur.getManageUnitId());
					hm.addHypertensionRecordWorkList(workList);
				}
			} else if(BSCHISUtil.parseToInt(reqBodyMap.get("constriction")+ "") >140 ||
					  BSCHISUtil.parseToInt(reqBodyMap.get("diastolic")+ "")>90) {
				// 生成高危和疑似记录
				int constriction = BSCHISUtil.parseToInt(reqBodyMap
						.get("constriction") + "");
				int diastolic = BSCHISUtil.parseToInt(reqBodyMap
						.get("diastolic") + "");
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(
						dao);
				hsm.insertHypertensionSimilarity(empiId, phrId, constriction,
						diastolic, reqBodyMap);
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk(empiId, phrId, constriction,
						diastolic, "6");
			}
			//标识签约任务完成
			this.finishSCServiceTask(empiId, SSZCY_GXYFW, null, dao);

		} catch (ModelDataOperationException e) {
			logger.error("Failed to save hypertension FCBP PHIS record.", e);
			throw new ServiceException(e);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save hypertension FCBP PHIS record.", e);
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHyperFCBP(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		// 放值
		String empiId = (String) reqBodyMap.get("empiId");
		String fcbpId = (String) reqBodyMap.get("fcbpId");
		String phrId = (String) reqBodyMap.get("phrId");
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getPhrIdByEmpiId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				reqBodyMap.put("phrId", phrId);
			}
		}
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Date curDate = new Date();
		reqBodyMap.put("measureDate", curDate);
		reqBodyMap.put("measureDoctor", curUserId);
		reqBodyMap.put("measureUnit", curUnitId);
		reqBodyMap.put("createUnit", curUnitId);
		reqBodyMap.put("createUser", curUserId);
		reqBodyMap.put("createDate", curDate);

		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_Hypertension_FCBP, reqBodyMap, false);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save hypertension FCBP.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			fcbpId = (String) rsMap.get("fcbpId");
		}
		rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
				MDC_Hypertension_FCBP);
		vLogService.saveVindicateLog(MDC_Hypertension_FCBP, op, fcbpId, dao,
				empiId);
		res.put("body", rsMap);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, SSZCY_GXYFW, null, dao);
		// 是否存在疑似记录
		HypertensionSimilarityModel hsModel = new HypertensionSimilarityModel(
				dao);
		boolean isExistHS = false;
		try {
			isExistHS = hsModel.isExistHypertensionSimilarity(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get hypertension similarity record.", e);
			throw new ServiceException(e);
		}
		if (isExistHS) {
			// 存在则为存核实记录
			Map<String, Object> hsMap = new HashMap<String, Object>();
			hsMap.putAll(reqBodyMap);
			List<Map<String, Object>> hsList = null;
			try {
				hsList = hsModel.getHypertensionSimilarityByEmpiId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get hypertension similarity.", e);
				throw new ServiceException(e);
			}
			if (hsList != null && hsList.size() > 0) {
				Map<String, Object> hsMap2 = hsList.get(0);
				hsMap.put("similarityId", hsMap2.get("similarityId"));
				hsMap.put("registerDate", new Date());
				hsMap.put("manaUnitId", curUnitId);
				hsMap.put("inputUnit", curUnitId);
				hsMap.put("inputDate", new Date());
				hsMap.put("inputUser", curUserId);
				try {
					dao.doSave("create", MDC_HypertensionSimilarityC, hsMap,
							false);
				} catch (PersistentDataOperationException e) {
					logger.error("Failed to save MDC_HypertensionSimilarityC.",
							e);
					throw new ServiceException(e);
				}
			}
		} else {
			// 不存在则存为和疑似
			int constriction = (Integer) reqBodyMap.get("constriction");
			int diastolic = (Integer) reqBodyMap.get("diastolic");
			if (constriction >= 140 || diastolic >= 90) {
				Map<String, Object> hsMap = new HashMap<String, Object>();
				hsMap.putAll(reqBodyMap);
				hsMap.put("registerDate", new Date());
				hsMap.put("diagnosisType", DiagnosisType.YS);
				hsMap.put("manaUnitId", curUnitId);
				hsMap.put("inputUnit", curUnitId);
				hsMap.put("inputDate", new Date());
				hsMap.put("inputUser", curUserId);
				try {
					dao.doSave("create", MDC_HypertensionSimilarity, hsMap,
							false);
				} catch (PersistentDataOperationException e) {
					logger.error("Failed to save MDC_HypertensionSimilarity.",
							e);
					throw new ServiceException(e);
				}
			}
		}
	}

	/**
	 * 获取健康档案编号
	 * 
	 * @Description:
	 * @param empiId
	 * @param dao
	 * @return
	 * @throws PersistentDataOperationException
	 * @author ChenXianRui 2014-1-23 下午6:12:12
	 * @Modify:
	 */
	private String getPhrIdByEmpiId(String empiId, BaseDAO dao)
			throws ServiceException {
		HealthRecordModel hrm = new HealthRecordModel(dao);
		String phrId = null;
		try {
			phrId = hrm.getPhrId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("保存高血压疑似时，获取phrId失败！", e);
			throw new ServiceException("Failed to get phrId.", e);
		}
		return phrId;
	}
	
	public void doTriggerSaveHyperFCBPPHIS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		HypertensionFCBPModel hfm = new HypertensionFCBPModel(dao);
		if (StringUtils.isEmpty(phrId)) {
			// 从医疗过来，没有phrId,这时到库进去取一下
			phrId = this.getPhrIdByEmpiId(empiId, dao);
			if (StringUtils.isNotEmpty(phrId)) {
				reqBodyMap.put("phrId", phrId);
			}
		}
		String hypertensionHistory = (String) reqBodyMap
				.get("hypertensionHistory");
		try {
			hfm.saveHypertensionFCBP(op, reqBodyMap, true);
			if (hypertensionHistory != null && "1".equals(hypertensionHistory)) {
				HypertensionModel hm = new HypertensionModel(dao);
				List<Map<String, Object>> records = hm
						.findHypertensionRecordByEmpiId(empiId);
				WorkListModel wlk = new WorkListModel(dao);
				boolean isExistWl = wlk.isExistWorkList(phrId, empiId,
						WorkType.MDC_HYPERTENSIONRECORD, dao);
				if ((records == null || records.size() == 0) && !isExistWl) {
					// 没有高血压档案
					Map<String, Object> workList = new HashMap<String, Object>();
					Calendar c = Calendar.getInstance();
					workList.put("recordId", phrId);
					workList.put("empiId", empiId);
					workList.put("beginDate", c.getTime());
					c.add(Calendar.YEAR, 1);
					workList.put("endDate", c.getTime());
					workList.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
					workList.put("count", 1);
					UserRoleToken ur = UserRoleToken.getCurrent();
					workList.put("doctorId", ur.getUserId());
					workList.put("manaUnitId", ur.getManageUnitId());
					hm.addHypertensionRecordWorkList(workList);
				}
			} else if(BSCHISUtil.parseToInt(reqBodyMap.get("constriction")+ "") >140 ||
					  BSCHISUtil.parseToInt(reqBodyMap.get("diastolic")+ "")>90) {
				// 生成高危和疑似记录
				int constriction = BSCHISUtil.parseToInt(reqBodyMap
						.get("constriction") + "");
				int diastolic = BSCHISUtil.parseToInt(reqBodyMap
						.get("diastolic") + "");
				HypertensionSimilarityModel hsm = new HypertensionSimilarityModel(
						dao);
				hsm.insertHypertensionSimilarity(empiId, phrId, constriction,
						diastolic, reqBodyMap);
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				hrm.insertHypertensionRisk(empiId, phrId, constriction,
						diastolic, "6");
			}else{
				return;
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save hypertension FCBP PHIS record.", e);
			throw new ServiceException(e);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save hypertension FCBP PHIS record.", e);
			throw new ServiceException(e);
		}
	}
}
