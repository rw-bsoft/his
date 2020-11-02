/**
 * @(#)TumourScreeningModel.java Created on 2014-3-28 下午4:44:56
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import ctd.validator.ValidateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.phr.HealthRecordModel;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourScreeningModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public TumourScreeningModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存T初筛数据
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-3-28 下午4:46:15
	 * @Modify:
	 */
	public Map<String, Object> saveTumourScreening(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourScreening, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存初筛信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存初筛信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:更新初筛记录中性质和高危标识
	 * @param empiId
	 * @param highRiskType
	 * @param year
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-22 下午6:18:57
	 * @Modify:
	 */
	public void updateNature(String empiId, String highRiskType, int year,
			String nature, String highRiskMark)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MDC_TumourScreening")
				.append(" set nature = :nature")
				.append(" , highRiskMark=:highRiskMark")
				.append(" , lastModifyUnit=:lastModifyUnit")
				.append(" ,lastModifyUser=:lastModifyUser")
				.append(" ,lastModifyDate=:lastModifyDate")
				.append(" where empiId=:empiId and highRiskType=:highRiskType and year=:year")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nature", nature);
		parameters.put("highRiskMark", highRiskMark);
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("empiId", empiId);
		parameters.put("highRiskType", highRiskType);
		parameters.put("year", year);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新初筛记录中性质和高危标识失败！", e);
		}
	}

	/**
	 * 
	 * @Description:更新初筛记录中性质和高危标识
	 * @param empiId
	 * @param highRiskType
	 * @param year
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-22 下午6:18:57
	 * @Modify:
	 */
	public void updateNature(String recordId, String nature, String highRiskMark)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MDC_TumourScreening")
				.append(" set nature = :nature")
				.append(" , highRiskMark=:highRiskMark")
				.append(" , lastModifyUnit=:lastModifyUnit")
				.append(" ,lastModifyUser=:lastModifyUser")
				.append(" ,lastModifyDate=:lastModifyDate")
				.append(" where recordId=:recordId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nature", nature);
		parameters.put("highRiskMark", highRiskMark);
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("recordId", recordId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新初筛记录中性质和高危标识失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤初筛记录
	 * @param recordId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午10:47:05
	 * @Modify:
	 */
	public void logoutTumourScreeningRecord(String recordId,
			String cancellationReason) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" set status=:status")
				.append(",cancellationReason=:cancellationReason")
				.append(",cancellationUser=:cancellationUser")
				.append(",cancellationDate=:cancellationDate")
				.append(",cancellationUnit=:cancellationUnit")
				.append(",lastModifyUnit=:lastModifyUnit")
				.append(",lastModifyUser=:lastModifyUser")
				.append(",lastModifyDate=:lastModifyDate")
				.append(" where recordId=:recordId").toString();
		Date curDate = Calendar.getInstance().getTime();
		String curUser = UserUtil.get(UserUtil.USER_ID);
		String curUnit = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_WRITE_OFF);
		pMap.put("cancellationReason", cancellationReason);
		pMap.put("cancellationUser", curUser);
		pMap.put("cancellationDate", curDate);
		pMap.put("cancellationUnit", curUnit);
		pMap.put("lastModifyUnit", curUnit);
		pMap.put("lastModifyUser", curUser);
		pMap.put("lastModifyDate", curDate);
		pMap.put("recordId", recordId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销肿瘤初筛记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销该人员的全部初筛记录
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午10:52:17
	 * @Modify:
	 */
	public void logoutMyAllTSR(String empiId, String cancellationReason)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" set status = :status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where empiId=:empiId").toString();
		Date curDate = Calendar.getInstance().getTime();
		String curUser = UserUtil.get(UserUtil.USER_ID);
		String curUnit = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_WRITE_OFF);
		pMap.put("cancellationReason", cancellationReason);
		pMap.put("cancellationUser", curUser);
		pMap.put("cancellationDate", curDate);
		pMap.put("cancellationUnit", curUnit);
		pMap.put("lastModifyUnit", curUnit);
		pMap.put("lastModifyUser", curUser);
		pMap.put("lastModifyDate", curDate);
		pMap.put("empiId", empiId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销该人员的全部初筛记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复肿瘤初筛记录
	 * @param recordId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午10:47:05
	 * @Modify:
	 */
	public void revertTumourScreeningRecord(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" set status = :status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where recordId=:recordId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_NORMAL);
		pMap.put("cancellationReason", "");
		pMap.put("cancellationUser", "");
		pMap.put("cancellationDate", null);
		pMap.put("cancellationUnit", "");
		pMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		pMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		pMap.put("lastModifyDate", Calendar.getInstance().getTime());
		pMap.put("recordId", recordId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复肿瘤初筛记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复该人员的全部初筛记录
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-17 上午10:52:17
	 * @Modify:
	 */
	public void revertMyAllTSR(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" set status=:status")
				.append(" , cancellationReason = :cancellationReason")
				.append(" , cancellationUser = :cancellationUser")
				.append(" , cancellationDate = :cancellationDate")
				.append(" , cancellationUnit = :cancellationUnit")
				.append(" , lastModifyUnit = :lastModifyUnit")
				.append(" , lastModifyUser = :lastModifyUser")
				.append(" , lastModifyDate = :lastModifyDate")
				.append(" where empiId=:empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("status", Constants.CODE_STATUS_NORMAL);
		pMap.put("cancellationReason", "");
		pMap.put("cancellationUser", "");
		pMap.put("cancellationDate", null);
		pMap.put("cancellationUnit", "");
		pMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		pMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		pMap.put("lastModifyDate", Calendar.getInstance().getTime());
		pMap.put("empiId", empiId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复该人员的全部初筛记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:是否已经有某类型的初筛记录
	 * @param empiId
	 * @param type
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-15 上午11:13:34
	 * @Modify:
	 */
	public boolean isTumourScreening(String empiId, String type)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourScreening)
				.append(" where empiId=:empiId and highRiskType=:type")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("type", type);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断某人是否已经有某类型的初筛记录失败！", e);
		}
		boolean isExist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:删除某人某类型的肿瘤初筛记录
	 * @param empiId
	 * @param highRiskType
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-25 上午10:53:26
	 * @Modify:
	 */
	public void deleteTumourScreening(String empiId, String highRiskType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete ").append(MDC_TumourScreening)
				.append(" where empiId=:empiId and highRiskType=:highRiskType")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除肿瘤初筛记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:保存检查结果
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-1 上午10:26:00
	 * @Modify:
	 */
	public Map<String, Object> saveCheckResult(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourScreeningCheckResult, record,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存检查结果数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存检查结果失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:判断谋个初筛记录
	 * @param screeningId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-1 上午11:17:31
	 * @Modify:
	 */
	public boolean getCriterionTypeIsTrace(String screeningId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select count(a.recordId) as countNum from ")
				.append(MDC_TumourScreeningCheckResult)
				.append(" a ")
				.append(" where a.criterionType='2' and a.screeningId=:screeningId")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("screeningId", screeningId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询初筛检查列表中是否有追踪项目失败！", e);
		}
		long countNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			countNum = (Long) rsMap.get("countNum");
		}
		return countNum > 0 ? true : false;
	}

	/**
	 * 
	 * @Description:检查初筛检查项目是否有结果
	 * @param screeningId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-1 下午1:22:18
	 * @Modify:
	 */
	public boolean getCheckItemIsHasResult(String screeningId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select count(a.recordId) as countNum from ")
				.append(MDC_TumourScreeningCheckResult).append(" a ")
				.append(" where a.checkResult is not null")
				.append(" and a.screeningId=:screeningId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("screeningId", screeningId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查初筛检查项目是否有结果失败！", e);
		}
		long countNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			countNum = (Long) rsMap.get("countNum");
		}
		return countNum > 0 ? true : false;
	}

	/**
	 * 
	 * @Description:更新 初筛记录中 是否追踪 为 是
	 * @param screeningId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-1 下午1:35:04
	 * @Modify:
	 */
	public void updateIsTraceOfTS(String screeningId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" a ").append(" set a.isTrace = '1' ")
				.append(" where a.isTrace = '0' ")
				.append(" and a.recordId=:screeningId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("screeningId", screeningId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新 初筛记录中 是否追踪 为 是 失败了", e);
		}
	}

	/**
	 * 
	 * @Description:更新 初筛记录中 追踪规范 为 是
	 * @param screeningId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-1 下午1:40:04
	 * @Modify:
	 */
	public void updateTraceNormOfTS(String screeningId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" a ").append(" set a.traceNorm = '1' ")
				.append(" where a.traceNorm = '0' ")
				.append(" and a.recordId=:screeningId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("screeningId", screeningId);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新 初筛记录中 追踪规范 为 是 失败了", e);
		}
	}

	/**
	 * 
	 * @Description:获取初筛检查项目,创建时间倒序
	 * @param screeningId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-22 下午8:20:23
	 * @Modify:
	 */
	public List<Map<String, Object>> getTSCheckItemList(String screeningId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "screeningId", "s",
				screeningId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "a.createDate desc",
					MDC_TumourScreeningCheckResult);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取初筛检查项目", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:自动转高危
	 * @param paraMap
	 *            （必须参数：empiId，phrId，highRiskType[高危类型]，highRiskSource[高危来源]，
	 *            recordId[初筛记录]）
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-23 上午9:20:59
	 * @Modify:
	 */
	public boolean autoTSTurnTHR(Map<String, Object> paraMap)
			throws ModelDataOperationException {
		String empiId = (String) paraMap.get("empiId");
		String highRiskType = (String) paraMap.get("highRiskType");
		String highRiskSource = (String) paraMap.get("highRiskSource");// 高危来源
		String recordId = (String) paraMap.get("recordId");// 初筛记录ID
		if (StringUtils.isEmpty(highRiskType)) {
			throw new ModelDataOperationException(Constants.CODE_UNKNOWN_ERROR,
					"肿瘤类型不能为空！");
		}
		boolean passport = false;
		// 判断该人是否已经迁出或死亡
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		boolean isEOD = hrModel.isEmigrationOrDeath(empiId);
		if (isEOD == false) {
			TumourCriterionModel tcModel = new TumourCriterionModel(dao);
			// 获取可用高危标准
			List<Map<String, Object>> thrcList = tcModel.getTHRCriterion(
					highRiskType, highRiskSource);
			if (thrcList != null && thrcList.size() > 0) {
				for (int i = 0, len = thrcList.size(); i < len; i++) {
					Map<String, Object> thrcMap = thrcList.get(i);
					String highRiskFactor = (String) thrcMap
							.get("highRiskFactor");
					String hrcId = (String) thrcMap.get("hrcId");
					String PSItemRelation = (String) thrcMap
							.get("PSItemRelation");// 初筛检查项目关系
					String traceItemRelation = (String) thrcMap
							.get("traceItemRelation");// 追踪检查项目关系
					if ("1".equals(highRiskFactor)) {// 问卷症状--有
						passport = tcModel.isExistTumourQuestionnaireSymptom(
								empiId, highRiskType, hrcId);
						if (passport) {
							passport = tcModel.isAccordWithCheckResult(
									recordId, hrcId, highRiskType,
									PSItemRelation, traceItemRelation);
						}
						if (passport) {
							break;
						}
					}
					// 高危因素选择“家族肿瘤史”“肿瘤疾病史”的标准，直接判断检查标准
					if ("2".equals(highRiskFactor)) {// 家族肿瘤史--有
					// passport = tcModel.isExitCancer(empiId, new String[] {
					// "0706", "0806", "0906", "1006" });
						if (passport) {
							break;
						} else {
							passport = tcModel.isAccordWithCheckResult(
									recordId, hrcId, highRiskType,
									PSItemRelation, traceItemRelation);
							if (passport) {
								break;
							}
						}
					}
					if ("3".equals(highRiskFactor)) {// 肿瘤疾病史--有
					// passport = tcModel.isExitCancer(empiId,
					// new String[] { "0206" }, new String[] { "02" });
						if (passport) {
							break;
						} else {
							passport = tcModel.isAccordWithCheckResult(
									recordId, hrcId, highRiskType,
									PSItemRelation, traceItemRelation);
							if (passport) {
								break;
							}
						}
					}
				}
			}
		}
		// turnTHRSucceed 为true 表示转高危成功
		boolean turnTHRSucceed = false;
		if (passport) {
			// 增加高危记录
			TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
			paraMap.put("screeningId", recordId);
			Map<String, Object> thrMap = thrModel.turnHighRiskToAddTHR(paraMap);
			turnTHRSucceed = (Boolean) thrMap.get("succeed");
		}
		return turnTHRSucceed;
	}
}
