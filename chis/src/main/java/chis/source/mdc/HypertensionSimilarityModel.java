/**
 * @(#)HypertensionModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.validator.ValidateException;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DiagnosisType;
import chis.source.dic.PlanStatus;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionSimilarityModel extends HypertensionModel {

	public HypertensionSimilarityModel(BaseDAO dao) {
		super(dao);
	}

	public List<Map<String, Object>> getHypertensionSimilarityByEmpiId(
			String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(MDC_HypertensionSimilarity)
				.append(" where empiId =:empiId and diagnosisType =:diagnosisType1")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("diagnosisType1", DiagnosisType.YS);
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压疑似记录失败", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:是否存在疑似记录
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-1-23 下午5:44:37
	 * @Modify:
	 */
	public boolean isExistHypertensionSimilarity(String empiId)
			throws ModelDataOperationException {
		List<Map<String, Object>> hsList = this
				.getHypertensionSimilarityByEmpiId(empiId);
		boolean isExistHS = false;
		if (hsList != null && hsList.size() > 0) {
			isExistHS = true;
		}
		return isExistHS;
	}

	/**
	 * 
	 * @Description:获取疑似记录
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-1-23 下午5:51:29
	 * @Modify:
	 */
	public List<Map<String, Object>> getHypertensionSimilarity(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(MDC_HypertensionSimilarity)
				.append(" where empiId =:empiId and diagnosisType != :diagnosisType1")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("diagnosisType1", DiagnosisType.QZ);
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压疑似记录失败");
		}
		return rsList;
	}

	/**
	 * 插入高血压疑似记录
	 * 
	 * @param empiId
	 * @param phrId
	 * @param constriction收缩压
	 *            (mmHg)
	 * @param diastolic舒张压
	 *            (mmHg)
	 * @param dataSource数据来源
	 */
	public void insertHypertensionSimilarity(String empiId, String phrId,
			Integer constriction, Integer diastolic, Map<String, Object> data) {
		if (constriction == null && diastolic == null) {
			return;
		}
		if (constriction != null && constriction >= 140) {
		} else if (diastolic != null && diastolic >= 90) {
		} else {
			return;
		}
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		boolean hasHypertensionRecord = false;
		boolean hasHypertensionSimilarity = false;
		try {
			List<Map<String, Object>> list1 = dao.doQuery(cnd, "a.empiId",
					MDC_HypertensionRecord);
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq",
					"a.diagnosisType", "s", DiagnosisType.YS);
			List<Object> cnd2 = CNDHelper.createArrayCnd("and", cnd, cnd1);
			List<Map<String, Object>> list2 = dao.doQuery(cnd2, "a.empiId",
					MDC_HypertensionSimilarity);
			if (list1 != null && list1.size() > 0) {
				hasHypertensionRecord = true;
			}
			if (list2 != null && list2.size() > 0) {
				hasHypertensionSimilarity = true;
			}
			int hypertensionLevel = getHypertensionLevel(constriction,
					diastolic);
			UserRoleToken urt = UserRoleToken.getCurrent();
			Map<String, Object> r = new HashMap<String, Object>();
			Map<String, Object> sMap = new HashMap<String, Object>();
			if (!hasHypertensionRecord && !hasHypertensionSimilarity) {
				r.put("empiId", empiId);
				r.put("phrId", phrId);
				r.put("constriction", constriction);
				r.put("diastolic", diastolic);
				r.put("diagnosisType", DiagnosisType.YS);
				r.put("registerDate", new Date());
				r.put("hypertensionLevel", hypertensionLevel + "");
				if (data.get("manaUnitId") != null) {
					r.put("manaUnitId", data.get("manaUnitId"));
				} else {
					r.put("manaUnitId", urt.getManageUnitId());
				}
				if (data.get("height") != null) {
					r.put("height", data.get("height"));
				}
				if (data.get("weight") != null) {
					r.put("weight", data.get("weight"));
				}
				if (data.get("bmi") != null) {
					r.put("bmi", data.get("bmi"));
				} else if (data.get("height") != null
						&& data.get("weight") != null) {
					double h = Double.valueOf(data.get("height") + "");
					double w = Double.valueOf(data.get("weight") + "");
					double bmi = w / (h * h / 10000);
					bmi = ((int) (bmi * 100)) / 100.0;
					r.put("bmi", bmi);
				}
				sMap = dao.doSave("create", MDC_HypertensionSimilarity, r,
						false);
			}
			String similarityId = "";
			if (!hasHypertensionRecord) {
				if (!hasHypertensionSimilarity) {
					similarityId = (String) sMap.get("similarityId");
				} else {
					Map<String, Object> map = list2.get(0);
					similarityId = (String) map.get("similarityId");
				}
				Map<String, Object> childMap = new HashMap<String, Object>();
				childMap.put("similarityId", similarityId);
				childMap.put("empiId", empiId);
				childMap.put("phrId", phrId);
				childMap.put("constriction", constriction);
				childMap.put("diastolic", diastolic);
				childMap.put("registerDate", new Date());
				childMap.put("hypertensionLevel", hypertensionLevel + "");
				if (data.get("manaUnitId") != null) {
					childMap.put("manaUnitId", data.get("manaUnitId"));
				} else {
					r.put("manaUnitId", urt.getManageUnitId());
				}
				if (data.get("height") != null) {
					childMap.put("height", data.get("height"));
				}
				if (data.get("weight") != null) {
					childMap.put("weight", data.get("weight"));
				}
				if (data.get("bmi") != null) {
					childMap.put("bmi", data.get("bmi"));
				} else if (data.get("height") != null
						&& data.get("weight") != null) {
					double h = Double.valueOf(data.get("height") + "");
					double w = Double.valueOf(data.get("weight") + "");
					double bmi = w / (h * h / 10000);
					bmi = ((int) (bmi * 100)) / 100.0;
					childMap.put("bmi", bmi);
				}
				dao.doSave("create", MDC_HypertensionSimilarityC, childMap,
						false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	private int getHypertensionLevel(Integer constriction, Integer diastolic) {
		if (constriction >= 180 || diastolic >= 110) {
			return 3; // @@ 3级（重度）
		}
		if ((constriction >= 160 && constriction <= 179)
				|| (diastolic >= 100 && diastolic <= 109)) {
			return 2; // @@ 2级（中度）
		}
		if ((constriction >= 140 && constriction <= 159)
				|| (diastolic >= 90 && diastolic <= 99)) {
			return 1; // @@ 1级（轻度）
		}
		if (constriction < 120 && diastolic < 80) {
			return 4; // @@ 理想血压
		}
		if (constriction < 130 && diastolic < 85) {
			return 5; // @@ 正常血压
		}
		if ((constriction >= 130 && constriction <= 139 && diastolic < 90)
				|| (diastolic >= 85 && diastolic <= 89 && constriction < 140)) {
			return 6; // @@ 正常高值
		}
		if (constriction >= 140 && diastolic < 90) {
			return 7; // @@ 单纯收缩性高血压
		}
		return 0;
	}
}
