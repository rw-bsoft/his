/**
 * @(#)ChildrenCheckupModel.java Created on 2012-1-13 上午11:11:23
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.Appraise;
import chis.source.dic.BusinessType;
import chis.source.dic.DevEvaluation;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */

public class ChildrenCheckupModel implements BSCHISEntryNames {
	private BaseDAO dao;

	/**
	 * @param dao
	 */
	public ChildrenCheckupModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存儿童体格检查信息
	 * 
	 * @param data
	 * @param op
	 * @param schema
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveCheckupRecord(Map<String, Object> data, String op,
			String schema, Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, schema, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童体格检查信息失败。", e);
		}
	}

	/**
	 * 获取儿童体格检查信息
	 * 
	 * @param schema
	 * @param checkupId
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getCheckupRecord(String schema, String checkupId)
			throws ValidateException, ModelDataOperationException {
		try {
			return dao.doLoad(schema, checkupId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童体格检查信息失败。", e);
		}
	}

	/**
	 * 保存儿童体格检查中医辨体信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveCheckupDescription(Map<String, Object> data, String op,
			Map<String, Object> res) throws ModelDataOperationException,
			ValidateException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					CDH_ChildrenCheckupDescription, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童体格检查中医辨体信息失败。", e);
		}
	}

	/**
	 * 保存儿童体格检查健康教育信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveCheckupHealthTeach(Map<String, Object> data, String op,
			Map<String, Object> res) throws ModelDataOperationException,
			ValidateException {
		try {
			Map<String, Object> genValues = dao.doSave(op,
					CDH_CheckupHealthTeach, data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存儿童体格检查健康教育信息失败", e);
		}
	}

	/**
	 * 获取儿童体格检查中医辨体信息
	 * 
	 * @param checkupType
	 * @param checkupId
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getCheckupDescription(String checkupType,
			String checkupId) throws ModelDataOperationException,
			ValidateException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "checkupType", "s",
				checkupType);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "checkupId", "s",
				checkupId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, CDH_ChildrenCheckupDescription);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童体格检查中医辨体信息失败。", e);
		}
	}

	/**
	 * 获取儿童体格检查健康教育信息
	 * 
	 * @param checkupType
	 * @param checkupId
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getCheckupHealthTeach(String checkupType,
			String checkupId) throws ModelDataOperationException,
			ValidateException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "checkupType", "s",
				checkupType);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "checkupId", "s",
				checkupId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, CDH_CheckupHealthTeach);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童体格检查健康教育信息失败", e);
		}
	}

	/**
	 * 获取下次随访计划是否已经做过
	 * 
	 * @param checkupStage
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean getNextPlanVisited(String checkupStage, String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuilder("select visitDate as visitDate from ")
				.append(PUB_VisitPlan)
				.append(" where extend1 > :extend1 and businessType = :businessType and recordId = :recordId  order by planId asc")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("extend1", checkupStage);
		parameters.put("recordId", recordId);
		parameters.put("businessType", BusinessType.CD_CU);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list == null || list.size() < 1) {
				return false;
			} else {
				Map<String, Object> map = list.get(0);
				Date visitDate = (Date) map.get("visitDate");
				if (visitDate != null) {
					return true;
				} else {
					return false;
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取下一条随访计划失败", e);
		}

	}

	/**
	 * 处理儿童体检计划
	 * 
	 * @param op
	 * @param checkUp
	 * @throws ModelDataOperationException
	 */
	public void updatePlan(String op, Map<String, Object> checkUp)
			throws ModelDataOperationException {
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		StringBuffer hql;
		Map<String, Object> param;
		String checkupId = (String) checkUp.get("checkupId");
		Date checkupDate = BSCHISUtil.toDate((String) (checkUp
				.get("checkupDate")));
		if (op.equals("create")) {
			Object checkupStage = checkUp.get("checkupStage");
			hql = new StringBuffer(" update ")
					.append(PUB_VisitPlan)
					.append(" set visitId =:visitId ,visitDate =:visitDate,planStatus=:planStatus,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate where recordId =:recordId and businessType =:businessType and extend1=:extend1");
			param = new HashMap<String, Object>();
			param.put("recordId", checkUp.get("phrId"));
			param.put("businessType", BusinessType.CD_CU);
			param.put("visitDate", checkupDate);
			param.put("planStatus", PlanStatus.VISITED);
			param.put("visitId", checkupId);
			param.put(
					"extend1",
					checkupStage != null ? Integer.valueOf(checkupStage
							.toString()) : 0);
			param.put("lastModifyUser", userId);
			param.put("lastModifyDate", new Date());
		} else {
			hql = new StringBuffer(" update ")
					.append(PUB_VisitPlan)
					.append(" set  visitDate =:visitDate,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate where visitId =:visitId and businessType =:businessType");
			param = new HashMap<String, Object>();
			param.put("visitId", checkupId);
			param.put("visitDate", checkupDate);
			param.put("businessType", BusinessType.CD_CU);
			param.put("lastModifyUser", userId);
			param.put("lastModifyDate", new Date());
		}
		try {
			dao.doUpdate(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处理儿童体检计划失败", e);
		}

	}

	/**
	 * 根据年龄和性别获取对应标准表(WHO或9City)的年龄别身高体重标准
	 * 
	 * @param sexCode
	 * @param age
	 * @param entryName
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getAgeForHW(String sexCode, int age,
			String entryName) throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select wSD2 as wSD2,wSD2neg as wSD2neg,hSD2 as hSD2,hSD2neg as hSD2neg  from ")
				.append(entryName)
				.append(" where sexCode =:sexCode1 and age = ")
				.append("(select max(age) from ").append(entryName)
				.append(" where sexCode =:sexCode2 and age = :age ) ")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sexCode1", sexCode);
		parameters.put("age", age);
		parameters.put("sexCode2", sexCode);
		Map<String, Object> reBody = null;
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list != null && list.size() > 0) {
				reBody = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取年龄别身高体重标准失败", e);
		}
		return reBody;
	}

	/**
	 * 根据年龄和性别获取BMI标准
	 * 
	 * @param sexCode
	 * @param age
	 * @param entryName
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getAgeForBMI(String sexCode, int age)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select BMISD1 as sD1,BMISD2neg as sD2neg   from ")
				.append("CDH_WHOBMI")
				.append(" where sexCode =:sexCode1 and age = ")
				.append("(select max(age) from ").append("CDH_WHOBMI")
				.append(" where sexCode =:sexCode2 and age = :age ) ")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sexCode1", sexCode);
		parameters.put("age", age);
		parameters.put("sexCode2", sexCode);
		Map<String, Object> reBody = null;
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if (list != null && list.size() > 0) {
				reBody = list.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取BMI标准失败", e);
		}
		return reBody;
	}

	/**
	 * 获取体重的标准评价
	 * 
	 * @param record
	 * @param v
	 * @return
	 */
	public String getAppraiseWY(Map<String, Object> record, double v) {
		double wSD2 = (Double) record.get("wSD2") != null ? ((Double) record
				.get("wSD2")).doubleValue() : 0;
		double wSD2neg = (Double) record.get("wSD2neg") != null ? ((Double) record
				.get("wSD2neg")).doubleValue() : 0;
		if (v > wSD2) {
			return Appraise.SHANG;
		} else if (v <= wSD2 && v >= wSD2neg) {
			return Appraise.ZHONG;
		} else if (v < wSD2neg) {
			return Appraise.XIA;
		} else {
			return null;
		}
	}

	/**
	 * 获取身高的标准评价
	 * 
	 * @param record
	 * @param v
	 * @return
	 */
	public String getAppraiseHY(Map<String, Object> record, double v) {
		double hSD2 = (Double) record.get("hSD2") != null ? ((Double) record
				.get("hSD2")).doubleValue() : 0;
		double hSD2neg = (Double) record.get("hSD2neg") != null ? ((Double) record
				.get("hSD2neg")).doubleValue() : 0;
		if (v > hSD2) {
			return Appraise.SHANG;
		} else if (v <= hSD2 && v >= hSD2neg) {
			return Appraise.ZHONG;
		} else if (v < hSD2neg) {
			return Appraise.XIA;
		} else {
			return null;
		}
	}

	/**
	 * 根据WHO年龄别体重身高标准获取儿童体格检查体格发育评价
	 * 
	 * @param whoage
	 * @param weight
	 * @param height
	 * @return
	 */
	public String getDevEvaluationAWH(Map<String, Object> whoage,
			double weight, double height) {
		StringBuilder sb = new StringBuilder();
		double wSD2neg = (Double) whoage.get("wSD2neg") != null ? ((Double) whoage
				.get("wSD2neg")).doubleValue() : 0;
		double hSD2neg = (Double) whoage.get("hSD2neg") != null ? ((Double) whoage
				.get("hSD2neg")).doubleValue() : 0;
		if (weight < wSD2neg) {
			sb.append(DevEvaluation.LBW); // ** 低体重
		}
		if (height < hSD2neg) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(DevEvaluation.STUNT);// ** 发育迟缓
		}
		return sb.toString();
	}

	/**
	 * 根据BMI标准获取儿童体格检查体格发育评价
	 * 
	 * @param bmis
	 * @param bmi
	 * @return
	 */
	public String getDevEvaluationBMI(Map<String, Object> bmis, double bmi) {
		double sD2neg = (Double) bmis.get("sD2neg") != null ? ((Double) bmis
				.get("sD2neg")).doubleValue() : 0;
		double sD1 = (Double) bmis.get("sD1") != null ? ((Double) bmis
				.get("sD1")).doubleValue() : 0;
		if (bmi < sD2neg) { // ** 消瘦
			return DevEvaluation.MARASMUS;
		}
		if (bmi > sD1) {
			return DevEvaluation.OVERWEIGHT; // ** 超重
		}
		return null;
	}

	/**
	 * 获取儿童体格检查健康指导信息
	 * 
	 * @Description:
	 * @param checkupType
	 * @param checkupId
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @author ChenXianRui 2013-5-12 下午4:03:55
	 * @Modify:
	 */
	public Map<String, Object> getHealthGuidance(String checkupType,
			String checkupId) throws ModelDataOperationException,
			ValidateException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "checkupType", "s",
				checkupType);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "checkupId", "s",
				checkupId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, CDH_HealthGuidance);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取儿童体格检查健康指导信息失败", e);
		}
	}

	/**
	 * 保存儿童健康指导信息
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-12 下午4:10:45
	 * @Modify:
	 */
	public Map<String, Object> saveHealthGuidance(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsmMap = null;
		try {
			rsmMap = dao.doSave(op, CDH_HealthGuidance, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存儿童健康指导信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存儿童健康指导信失败！", e);
		}
		return rsmMap;
	}

	/**
	 * @Description:根据月龄获取矫治记录
	 * @param age
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-16 下午1:50:24
	 * @Modify:
	 */
	public List<Map<String, Object>> getDictCorrection(String age)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "age", "s", age);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "recordId desc", CDH_DictCorrection);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取矫治记录失败！", e);
		}
		return rsList;
	}

	public Map<String, Object> getVisitPlan(String checkupStage, String phrId)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "extend1", "s",
				checkupStage);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "recordId", "s",
				phrId);
		List<Object> cnd3 = CNDHelper.createSimpleCnd("eq", "businessType",
				"s", BusinessType.CD_IQ);
		List<Object> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "", PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取儿童询问计划失败！", e);
		}
		return rsList.get(0);
	}

	public Map<String, Object> saveInquireRecord(Map<String, Object> quireBody, String op)
			throws ModelDataOperationException {
		Map<String, Object> result =null;
		try {
			result=dao.doSave(op, CDH_Inquire, quireBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存儿童询问计划失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存儿童询问计划失败！", e);
		}
		return result;

	}

}
