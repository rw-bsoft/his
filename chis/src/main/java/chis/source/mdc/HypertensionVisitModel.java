/**
 * @(#)HypertensionVisitModel.java Created on 2012-1-5 上午11:18:57
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.log.VindicateLogService;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description 高血压随访业务模型类
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionVisitModel extends MDCBaseModel {

	/**
	 * @param dao
	 */
	public HypertensionVisitModel(BaseDAO dao) {
		super(dao);
	}

	public static Map<String, Object> planTypes = new HashMap<String, Object>();
	public static Map<String, Object> HyBPControl = new HashMap<String, Object>();

	/**
	 * 获取高血压随访信息
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionVisitByPkey(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_HypertensionVisit, pkey);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "取随访信息失败！");
		}
		return rsMap;
	}

	public List<Map<String, Object>> getFixGroupList(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "fixId", MDC_HypertensionFixGroup);
			SchemaUtil.setDictionaryMessageForList(list,
					MDC_HypertensionFixGroup);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病定转组失败。");
		}
		return list;
	}

	/**
	 * 获取随访计划的最大序号
	 * 
	 * @param empiId
	 * @param businessType
	 *            计划类型
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getVisitPlanMaxSN(String empiId, String businessType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select max(sn) as sn from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus<>'0'").toString();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("businessType", businessType);
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划的最大序号失败！");
		}
		int sn = 0;
		if (rsMap.size() > 0) {
			Integer maxSN = (Integer) rsMap.get("sn");
			if (maxSN != null) {
				sn = maxSN.intValue();
			}
		}

		return sn;
	}

	/**
	 * 依据empiId,visitDate降序 查询高血压随访表【MDC_HypertensionVisit】中的部分字段
	 * 
	 * @param selectFields
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPartOfHypertensionVisitByEmpiId(
			String selectFields, String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select ").append(selectFields)
				.append(" from ").append(MDC_HypertensionVisit)
				.append(" where empiId=:empiId order by visitDate desc")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询高血压随访信息失败！");
		}
		return rsList;
	}

	/**
	 * 检查是否有随访计划
	 * 
	 * @param empiId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkHasVisit(String empiId, String businessType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as count from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("businessType", businessType);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查是否有随访计划失败！");
		}
		if (rsMap != null && (Long) (rsMap.get("count")) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取本年度随访记录 注：为优化SQL,调用此方法时如果没有的字段,请自行追加
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCurYearLastVistRecord(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select a.visitDate as visitDate")
				.append(",b.planDate as planDate from ")
				.append("MDC_HypertensionVisit").append(" a,")
				.append("PUB_VisitPlan").append(" b ")
				.append(" where a.empiId =:empiId and a.visitId = b.visitId ")
				.append(" and a.visitDate <= :visitDate")
				.append(" order by a.visitId desc").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("visitDate", new Date());
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度随访记录失败！", e);
		}

		return rsList;
	}

	/**
	 * 生成随访计划
	 * 
	 * @param body
	 * @param visitPlanCreator
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void createVisitPlan(Map<String, Object> body,
			VisitPlanCreator visitPlanCreator)
			throws ModelDataOperationException {
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("empiId", body.get("empiId"));
		String phrId = (String) body.get("phrId");
		reqBody.put("recordId", phrId);
		reqBody.put("businessType", BusinessType.GXY);

		reqBody.put("fixGroupDate", body.get("fixGroupDate"));
		reqBody.put("groupCode", body.get("hypertensionGroup"));
		reqBody.put("visitResult", body.get("visitResult"));
		reqBody.put("reserveDate", body.get("nextDate"));// 下次预约时间

		reqBody.put("lastVisitDate", body.get("lastVisitDate"));
		reqBody.put("lastPlanDate", body.get("lastPlanDate"));
		reqBody.put("fixType", (body.get("fixType")));
		reqBody.put("sn", body.get("sn"));
		reqBody.put("lastestData", body.get("lastestData"));

		reqBody.put("instanceType", BusinessType.GXY);
		reqBody.put("constriction", body.get("constriction"));
		reqBody.put("diastolic", body.get("diastolic"));
		reqBody.put("nextDate", body.get("nextDate"));
		reqBody.put("lastComplication", body.get("lastComplication"));
		reqBody.put("complicationIncrease", body.get("complicationIncrease"));
		reqBody.put("taskDoctorId", body.get("taskDoctorId"));
		reqBody.put("visitMeddle","0");
		// 当年截止日期
		boolean nextYear = false;
		if (body.get("nextYear") != null) {
			nextYear = (Boolean) body.get("nextYear");
		}
		reqBody.put("$sectionCutOffDate", BSCHISUtil.getSectionCutOffDate(
				"hypertensionEndMonth", nextYear));
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.GXY + "_planMode");
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
		}
		reqBody.put("$planMode", planMode);
		PlanType planType = null;
		try {
			planType = visitPlanCreator.create(reqBody, dao.getContext());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
		}
		if (null == planType) {
			return;
			// throw new ModelDataOperationException(
			// Constants.CODE_DATABASE_ERROR, "生成随访计划失败！");
		}
		// @@ 更新档案里的计划类型。
		UserRoleToken ur = UserRoleToken.getCurrent();
		String userId = ur.getUserId();
		String planTypeCode = planType.getPlanTypeCode();
		if (planTypeCode != null && planTypeCode.length() != 0) {
			String hql = new StringBuffer("update ")
					.append("MDC_HypertensionRecord")
					.append(" set planTypeCode=:planTypeCode")
					.append(",lastModifyUser=:lastModifyUser")
					.append(",lastModifyDate=:lastModifyDate where phrId=:phrId")
					.toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planTypeCode", planTypeCode);
			parameters.put("phrId", phrId);
			parameters.put("lastModifyUser", userId);
			parameters.put("lastModifyDate", new Date());
			try {
				dao.doUpdate(hql, parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "生成随访计划后更新档案里的计划类型时失败！",
						e);
			}
		}
	}

	/**
	 * 是否需要生成随访 年度评估模式除首次生成外不生成计划，随访平估都要生成
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean needCreatePlanNoAsses(String empiId, Date endDate)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> rsList = null;
			String hql = new StringBuffer(" from ").append(PUB_VisitPlan)
					.append(" where empiId =:empiId  and businessType='1'")
					.append(" and planDate >= :planDate and planstatus='1' ").toString();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("empiId", empiId);
			param.put("planDate", endDate);
			rsList = dao.doQuery(hql, param);
			if (rsList == null || rsList.size() == 0) {
				return true;
			}
			List<Map<String, Object>> list = dao.doList(null, "",
					MDC_HypertensionAssessParamete);
			if (list != null && list.size() > 0
					&& "2".equals(list.get(0).get("assessType"))) {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度随访记录失败！", e);
		}

		return true;
	}
	public void deleteNoVisitPlan(String empiId, Date endDate)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			String hql="delete from PUB_VisitPlan where empiId ='"+empiId+"'and " +
					   "businessType='1' and to_char(planDate,'yyyy-mm-dd') >='"+sf.format(endDate)+"'" +
					   " and planStatus='0' ";
			Session con =dao.getSession();
			Transaction t=con.beginTransaction();
			t.begin();
			con.createSQLQuery(hql).executeUpdate();
			t.commit();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度随访记录失败！", e);
		}
	}
	
	
	/**
	 * 更新随访计划状态
	 * 
	 * @param visitPlan
	 * @param planStatus
	 * @param oldPlanStatus
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int updateVisitPlanStatus(Map<String, Object> visitPlan,
			String newPlanStatus, String oldPlanStatus)
			throws ModelDataOperationException {
		String fixGroupDate = (String) visitPlan.get("fixGroupDate");
		if (fixGroupDate == null || fixGroupDate.trim().length() == 0) {
			throw new ModelDataOperationException(
					"Fix group date cannot be null.");
		}
		Date fixDate = BSCHISUtil.toDate(fixGroupDate.substring(0, 10));
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus,lastModifyDate=:lastModifyDate")
				.append(",lastModifyUser=:lastModifyUser")
				.append(" where recordId=:recordId and businessType=:businessType")
				.append(" and planStatus=:planStatus0 and planDate<:endDate")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("planStatus", newPlanStatus);
		param.put("lastModifyDate", new Date());
		param.put("lastModifyUser",
				((User) dao.getContext().get("user.instance")).getId());
		param.put("recordId", (String) visitPlan.get("recordId"));
		param.put("businessType", (String) visitPlan.get("instanceType"));
		param.put("planStatus0", oldPlanStatus);
		param.put("endDate", fixDate);
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, param);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					"Set last year visit plan that is not visit to 'notVisited' failed.",
					e);
		}
		return rsInt;
	}
	
	public void updatehyPastVisitPlanStatus(String empiId, Date fixdate ) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus ='2' ")
				.append(" where empiId = :empiId and businessType ='1' ")
				.append(" and  to_char(planDate,'yyyy') < :year ")
				.toString();
		Calendar tempdate = Calendar.getInstance();
		tempdate.setTime(fixdate);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("empiId", empiId);
		map.put("year", ""+tempdate.get(Calendar.YEAR));
		try {
			dao.doUpdate(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！", e);
		}
	}
	
	/**
	 * 更新过期"应该"随访计划为"未访"
	 * 
	 * @param empiId
	 * @param beginDate
	 * @param userId
	 * @throws ModelDataOperationException
	 */
	public int updatePastDueVisitPlanStatus(String empiId, String beginDate,
			String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,lastModifyUser = :lastModifyUser")
				.append(",lastModifyDate = :lastModifyDate")
				.append(" where empiId = :empiId and businessType = :businessType")
				.append(" and planStatus = :planStatus0 and beginDate < :beginDate")
				.toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planStatus", PlanStatus.NOT_VISIT);
		map.put("businessType", BusinessType.GXY);
		map.put("empiId", empiId);
		map.put("planStatus0", PlanStatus.NEED_VISIT);
		map.put("beginDate", BSCHISUtil.toDate(beginDate));
		map.put("lastModifyUser", userId);
		map.put("lastModifyDate", new Date());
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！", e);
		}
		return rsInt;
	}

	/**
	 * 获取随访记录
	 * 
	 * @param visitId
	 * @return
	 * @throws CreateVisitPlanException
	 */
	public Map<String, Object> getVisitRecordByVisitId(String visitId)
			throws ModelDataOperationException {
		if (visitId == null) {
			return null;
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_HypertensionVisit, visitId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 据 empiId 获取最近随访日期
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastVisitDate(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select max(visitDate) as maxDate from ")
				.append(MDC_HypertensionVisit)
				.append(" where empiId = :empiId").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_RECORD_NOT_FOUND, "获取最近随访日期失败！", e);
		}
		if (rsMap == null) {
			return null;
		}
		return (Date) rsMap.get("maxDate");
	}

	/**
	 * 保存高血压随访信息
	 * 
	 * @param op
	 * @param entryName
	 * @param body
	 * @param vilidate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionVisitInfo(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "随访数据未通过验证！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "随访信息保存失败！");
		}
		return rsMap;
	}

	/**
	 * 更新随访计划状态
	 * 
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> updateHypertensionVisitPlan(
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave("update", PUB_VisitPlan, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新随访计划数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新随访计划失败！");
		}
		return rsMap;
	}

	/**
	 * 获取高血压随访用药情况
	 * 
	 * @param phrId
	 * @param visitId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHyperVisitMedicineList(String visitId,
			int pageSize, int pageNo) throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				visitId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doList(cnd, "recordId", MDC_HypertensionMedicine,
					pageNo, pageSize, "");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访用药记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 保存高血压随访用药情况
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHyperVisitMedicineInfo(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		if ("create".equals(op)) {
			if (StringUtils.isEmpty((String) record.get("createUser"))) {
				record.put("createUser", UserUtil.get(UserUtil.USER_ID));
			}
			if (StringUtils.isEmpty((String) record.get("createUnit"))) {
				record.put("createUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			}
			if (record.get("createDate") == null) {
				record.put("createDate", new Date());
			}
		}
		if (StringUtils.isEmpty((String) record.get("lastModifyUser"))) {
			record.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		}
		if (StringUtils.isEmpty((String) record.get("lastModifyUnit"))) {
			record.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		}
		if (record.get("lastModifyDate") == null) {
			record.put("lastModifyDate", BSCHISUtil.toString(new Date(), null));
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存服药情况数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存服药情况失败！");
		}
		return rsMap;
	}

	/**
	 * 删除随访用药
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	public void delHyperVisitMedicineByRecordId(String recordId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(recordId, MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除随访用药失败！", e);
		}
	}

	/**
	 * 根据随访IDs查询随访中医辨体信息
	 * 
	 * @param visitId
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHyperVisitDescriptionByVisitId(String visitId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(cnd, MDC_HyperVisitDescription);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访中医辨体信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 保存高血压随访中医辨体
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHyperVisitDescription(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_HyperVisitDescription, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访中医辨体数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访中医辨体信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 根据随访IDs查询随访中医辨体信息
	 * 
	 * @param visitId
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getHyperVisitHealthTeachByVisitId(String wayId)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "02");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_GXYSF);
			if (list != null && list.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_GXYSF));
			}
			result.put("JKCFRecords", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访中医辨体信息失败！", e);
		}
		return result;
	}
	
	/**
	 * 保存APP高血压随访中健康教育
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void saveAppHyperVisitHealthTeach(Map<String, Object> m)
			throws ModelDataOperationException {
		try {
			dao.doInsert(HER_HealthRecipeRecord_GXYSF, m, false);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康教育数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康教育失败！");
		}
	}
	

	/**
	 * 保存高血压随访健康教育
	 * 
	 * @param op
	 * @param record
	 * @param vLogService
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> saveHyperVisitHealthTeach(String op,
			Map<String, Object> record, VindicateLogService vLogService)
			throws ModelDataOperationException {
		Map<String, Object> JKCF = (Map<String, Object>) record.get("JKCF");
		String wayId = record.get("wayId") + "";
		record.remove(JKCF);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> reBody = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "02");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_GXYSF);
			for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
				boolean isUpdate = false;
				for (Map<String, Object> map : list) {
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						m.put("id", map.get("id"));
						isUpdate = true;
						break;
					}
				}
				Map<String, Object> mBody = new HashMap<String, Object>();
				mBody.put("empiId", record.get("empiId"));
				mBody.put("phrId", record.get("phrId"));
				mBody.put("recordId", m.get("recordId"));
				mBody.put("wayId", wayId);
				mBody.put("recipeName", m.get("recipeName"));
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", record.get("examineUnit"));
				mBody.put("guideDate", record.get("guideDate"));
				mBody.put("guideUser", record.get("guideUser"));
				mBody.put("guideWay", "02");
				mBody.put("lastModifyUnit", manageUnit);
				mBody.put("lastModifyDate", new Date());
				mBody.put("lastModifyUser", user.getUserId() + "");
				if (isUpdate == true) {
					mBody.put("id", m.get("id"));
					updateList.add(mBody);
				} else {
					mBody.put("inputUnit", manageUnit);
					mBody.put("inputDate", new Date());
					mBody.put("inputUser", user.getUserId() + "");
					createList.add(mBody);
				}
			}
			for (Map<String, Object> map : list) {
				boolean isRemove = true;
				for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						isRemove = false;
						break;
					}
				}
				if (isRemove == true) {
					removeList.add(map);
				}
			}
			list.clear();
			for (Map<String, Object> map : createList) {
				Map<String, Object> result = dao.doSave("create",
						HER_HealthRecipeRecord_GXYSF, map, true);
				map.put("childId", result.get("id"));
				Map<String, Object> result2 = dao.doSave("create",
						HER_HealthRecipeRecord, map, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYSF,
						"create", result.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "create",
						result2.get("id") + "", dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_GXYSF, map, true);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='02'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				result2.putAll(map);
				result2.put("id", zjId);
				dao.doSave("update", HER_HealthRecipeRecord, result2, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYSF,
						"update", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "update",
						zjId, dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_GXYSF);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='02'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				dao.doRemove(zjId, HER_HealthRecipeRecord);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYSF,
						"delete", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "delete",
						zjId, dao);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_TNBSF));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访健康教育数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访健康教育信息失败！", e);
		} catch (ServiceException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访健康教育日志信息失败！", e);
		}
		return reBody;
	}

	/**
	 * 注销 高血压档案
	 * 
	 * @param body
	 *            [phrId、cancellationReason、deadReason、cancellationDate、
	 *            cancellationUser]
	 * @throws ModelDataOperationException
	 */
	public void setHypertensionRecordLogout(Map<String, Object> body)
			throws ModelDataOperationException {
		String phrId = (String) body.get("phrId");
		//yx-客户要求不注销高血压档案
//		String hql = new StringBuffer("update ")
//				.append("MDC_HypertensionRecord")
//				.append(" set cancellationReason = :cancellationReason,")
//				.append("cancellationDate = :cancellationDate,")
//				.append("cancellationUnit = :cancellationUnit,")
//				.append("endCheck = :endCheck,")
//				.append("visitEffect = :visitEffect,")
//				.append("noVisitReason = :noVisitReason,")
//				.append("visitDate = :visitDate,")
//				.append("cancellationUser = :cancellationUser,deadReason = :deadReason")
//				.append(", status = :status,lastModifyUser=:lastModifyUser,")
//				.append("lastModifyDate=:lastModifyDate where phrId = :phrId")
//				.toString();
//
//		String cancellationReason = (String) body.get("cancellationReason");
//		String deadReason = (String) body.get("deadReason");
//		Date cancellationDate = (Date) body.get("cancellationDate");
//		String cancellationUser = (String) body.get("cancellationUser");
//		String cancellationUnit = (String) body.get("cancellationUnit");
//		String userId = UserRoleToken.getCurrent().getUserId();
//		String visitEffect = (String) body.get("visitEffect");
//		String noVisitReason = (String) body.get("noVisitReason");
//		String visitDate = (String) body.get("visitDate");
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("visitEffect", visitEffect);
//		parameters.put("noVisitReason", noVisitReason);
//		parameters.put("visitDate", BSCHISUtil.toDate(visitDate));
//		parameters.put("endCheck", "1");
//		parameters.put("cancellationReason", cancellationReason);
//		parameters.put("cancellationUser", cancellationUser);
//		parameters.put("cancellationUnit", cancellationUnit);
//		parameters.put("deadReason", deadReason);
//		parameters.put("cancellationDate", cancellationDate);
//		parameters.put("status",String.valueOf(Constants.CODE_STATUS_WRITE_OFF));
//		parameters.put("phrId", phrId);
//		parameters.put("lastModifyUser", userId);
//		parameters.put("lastModifyDate", new Date());
//		try {
//			dao.doUpdate(hql, parameters);
//		} catch (PersistentDataOperationException e) {
//			e.printStackTrace();
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "注销高血压档案失败！");
//		}
		// 如果确定注销将更新未执行的计划状态
		String hql2 = new StringBuffer("update PUB_VisitPlan")
				.append(" set planStatus= :planStatus where recordId=:recordId")
				.append(" and businessType=:businessType and planStatus=:planStatus1")
				.toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("recordId", phrId);
		pam.put("businessType", BusinessType.GXY);
		pam.put("planStatus", PlanStatus.WRITEOFF);
		pam.put("planStatus1", PlanStatus.NEED_VISIT);
		try {
			dao.doUpdate(hql2, pam);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(Constants.CODE_DATABASE_ERROR, "更新未执行的询问计划失败！");
		}
	}

	/**
	 * 查询被终止管理的高血压随访数据 *
	 * 
	 * @param phrId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> searchOverHypertensionVisit(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> visitRecord;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "visitEffect", "s",VisitEffect.END);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			visitRecord = dao.doLoad(cnd, MDC_HypertensionVisit);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询被终止管理的高血压随访数据失败！", e);
		}
		return visitRecord;
	}

	/**
	 * 删除被终止管理的高血压随访数据
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void deleteOverHypertensionVisit(String phrId, String visitId)
			throws ModelDataOperationException {
		try {
			String hql = new StringBuffer("delete ")
					.append(MDC_HypertensionVisit)
					.append(" where visitId = :visitId")
					.append(" and phrId = :phrId ").toString();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visitId", visitId);
			parameters.put("phrId", phrId);

			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("删除被终止管理的高血压随访数据失败！", e);
		}
	}

	/**
	 * 获取正常的随访记录
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getLastNormalVisit(String phrId)
			throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer(" from ")
				.append(MDC_HypertensionVisit)
				.append(" where visitEffect = '1' and phrId = :phrId order by visitDate desc");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取正常的随访记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 获取随访计划ID
	 * 
	 * @param empiId
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getVisitPlanId(String empiId, String visitId)
			throws ModelDataOperationException {
		String hqlString = new StringBuffer("select planId as planId from ")
				.append("PUB_VisitPlan")
				.append(" where businessType = :businessType  and empiId = :empiId and visitId = :visitId")
				.toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("empiId", empiId);
		paraMap.put("visitId", visitId);
		paraMap.put("businessType", BusinessType.GXY);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlString, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划ID失败！", e);
		}
		String planId = "";
		if (rsMap != null && rsMap.size() > 0) {
			planId = (String) rsMap.get("planId");
		}
		return planId;
	}

	/**
	 * 统计随访用药记录数
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getHyperVisitMedicineRecordNum(String phrId, String visitId)
			throws ModelDataOperationException {
		long recordNum = 0;
		String hql = new StringBuffer("select count(*) as recordNum from ")
				.append(MDC_HypertensionMedicine)
				.append(" where phrId = :phrId and visitId = :visitId ")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitId", visitId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计随访用药记录数失败！", e);
		}
		if (rsMap != null && rsMap.size() > 0) {
			recordNum = (Long) rsMap.get("recordNum");
		}
		return recordNum;
	}

	public void saveMedicineRecord(String op, Map<String, Object> medicineMap)
			throws ModelDataOperationException, ValidateException {
		try {
			dao.doSave(op, MDC_HypertensionMedicine, medicineMap, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访用药记录数失败！", e);
		}
	}

	public List<Map<String, Object>> getMedicinesByVisitId(String visitId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
		try {
			return dao.doList(cnd, "createDate desc", MDC_HypertensionMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "加载随访用药记录数失败！", e);
		}
	}

	/**
	 * 获取本年度最后一条随访记录 的 ID<br/>
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastVistRecordVisitId(String empiId,
			Date startDate) throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append(MDC_HypertensionVisit)
				.append(" where empiId =:empiId and visitDate < :visitDate")
				.append(" order by visitId desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("visitDate", startDate);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		Map<String, Object> rsMap = null;
		if (rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

//	public Map<String, Object> getGroupConfig(String constriction,
//			String diastolic, String riskiness, String targetHurt,
//			String complication, String hypertensionGroup, String empiId,
//			String visitDate, String visitEvaluate)
//			throws ModelDataOperationException {
//		Map<String, Object> result = new HashMap<String, Object>();
//		List<Map<String, Object>> AssessParamete = null;
//		List<?> cnd = null;
//		try {
//			cnd = CNDHelper.toListCnd("['eq',['s','1'],['s','1']]");
//			// 查询高危年度评估参数
//			AssessParamete = dao
//					.doList(cnd, "", MDC_HypertensionAssessParamete);
//		} catch (Exception e) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		String assessType = null;
//		if (AssessParamete != null && AssessParamete.size() > 0) {
//			assessType = (String) AssessParamete.get(0).get("assessType");
//		}
//		if (assessType == null || "".equals(assessType)) {
//			result.put("errorCode", "588");
//			result.put("errorMsg", "请先维护高血压模块参数中的评估类型！");
//			return result;
//		}
//		Dictionary dic = null;
//		try {
//			dic = DictionaryController.instance().get(
//					"chis.dictionary.hyperGroupExt");
//		} catch (ControllerException e2) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		boolean controlBad = false;// 是否控制不良
//		boolean needReferral = false;// 是否转诊
//		boolean needInsertPlan = false;// 是否需要插入计划
//		if (!"1".equals(visitEvaluate)) {
//			controlBad = true;
//		}
//		Map<String, Object> lastVisitData = null;
//		try {
//			lastVisitData = getLastVistRecordVisitId(empiId,
//					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
//		} catch (ParseException e2) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		result.put("controlBad", controlBad);
//		result.put("oldGroup", hypertensionGroup);
//		if (controlBad == true && lastVisitData != null
//				&& !"1".equals(lastVisitData.get("visitEvaluate"))) {
//			needReferral = true;
//		}
//		result.put("needReferral", needReferral);
//		// 本次随访控制不良，判断两周内是否有计划，有者不插入计划
//		// 无则插入计划
//		if (controlBad == true && needReferral == false) {
//			boolean hasPlan = checkHasPlanInTwoWeek(empiId, visitDate);
//			if (hasPlan == false) {
//				needInsertPlan = true;
//			}
//		}
//		result.put("needInsertPlan", needInsertPlan);
//		// 年度评估不需要转组
//		if ("2".equals(assessType)) {
//			result.put("needChangeGroup", false);
//			result.put("needAssess", false);
//			result.put("hypertensionGroup", hypertensionGroup);
//			result.put("hypertensionGroupName", dic.getText(hypertensionGroup));
//			return result;
//		}
//		List<Map<String, Object>> dbsList = null;
//		try {
//			cnd = CNDHelper.toListCnd("['eq',['$','a.empiId'],['s','" + empiId
//					+ "']]");
//			dbsList = dao.doList(cnd, "", MDC_DiabetesRecord);
//		} catch (Exception e) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		int riskLength = 0;
//		String riskinessInEstimate = "1";
//		if (riskiness != null && !riskiness.equals("")
//				&& !riskiness.equals("0")) {
//			String[] riskinessList = riskiness.split(",");
//			riskLength = riskinessList.length;
//		}
//		if (riskLength == 1 || riskLength == 2) {
//			riskinessInEstimate = "2";
//		}
//		if (riskLength >= 3) {
//			riskinessInEstimate = "3";
//		}
//		if (dbsList != null && dbsList.size() > 0) {
//			riskinessInEstimate = "3";
//		}
//		if (targetHurt != null && targetHurt.length() > 0
//				&& !targetHurt.equals("0")) {
//			riskinessInEstimate = "3";
//		}
//		if (complication != null && complication.length() > 0
//				&& !complication.equals("0")) {
//			riskinessInEstimate = "4";
//		}
//		// 查询根据评估影响因素和血压级别得出危险分层的数据
//		List<Map<String, Object>> Estimate = null;
//		try {
//			cnd = CNDHelper.toListCnd("['eq',['$','riskiness'],['s','"
//					+ riskinessInEstimate + "']]");
//			Estimate = dao.doList(cnd, "", MDC_EstimateDictionary);
//		} catch (ExpException e1) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		int riskLevelGrade = MDCBaseModel.decideHypertensionGrade(
//				paresToInt(constriction), paresToInt(diastolic));
//		if (riskLevelGrade > 3 || riskLevelGrade == 0) {
//			riskLevelGrade = 1;
//		}
//		result.put("hypertensionLevel", riskLevelGrade);
//		String riskLevel = "1";// 危险分层
//		if (Estimate == null || Estimate.size() == 0) {
//			result.put("errorCode", "588");
//			result.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
//			return result;
//		} else {
//			riskLevel = Estimate.get(0).get("HL" + riskLevelGrade) + "";
//		}
//		if (riskLevel == null || "".equals(riskLevel)) {
//			result.put("errorCode", "588");
//			result.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
//			return result;
//		}
//		result.put("riskLevel", riskLevel);
//		if (controlBad == true) {
//			cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s", "3");
//		} else {
//			cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s", "1");
//		}
//		// 查询根据控制情况和危险分层得出组别的数据
//		List<Map<String, Object>> Control = null;
//		try {
//			Control = dao.doList(cnd, "", MDC_HypertensionControl);
//		} catch (PersistentDataOperationException e) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		String newGroup = "";// 组别
//		if (Control == null || Control.size() == 0) {
//			result.put("errorCode", "588");
//			result.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
//			return result;
//		} else {
//			Map<String, Object> map = Control.get(0);
//			if (riskLevel.equals("1")) {
//				newGroup = (String) map.get("lowRisk");
//			} else if (riskLevel.equals("2")) {
//				newGroup = (String) map.get("middleRisk");
//			} else if (riskLevel.equals("3")) {
//				newGroup = (String) map.get("highRisk");
//			} else if (riskLevel.equals("4")) {
//				newGroup = (String) map.get("veryHighRisk");
//			}
//		}
//		if (newGroup == null || "".equals(newGroup)) {
//			result.put("errorCode", "588");
//			result.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
//			return result;
//		}
//		boolean needTurn = false;
//		if (!newGroup.equals(hypertensionGroup)) {
//			needTurn = true;
//		}
//		result.put("needChangeGroup", needTurn);
//		result.put("needAssess", true);
//		result.put("hypertensionGroup", newGroup);
//		result.put("hypertensionGroupName", dic.getText(newGroup));
//		return result;
//	}

	public Map<String, Object> getGroupConfig_pk(String constriction,
			String diastolic, String riskiness, String targetHurt,
			String complication, String hypertensionGroup, String empiId,
			String visitDate, String visitEvaluate,String planDate)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> AssessParamete = null;
		List<?> cnd = null;
		try {
			cnd = CNDHelper.toListCnd("['eq',['s','1'],['s','1']]");
			// 查询高危年度评估参数
			AssessParamete = dao
					.doList(cnd, "", MDC_HypertensionAssessParamete);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		String assessType = null;
		if (AssessParamete != null && AssessParamete.size() > 0) {
			assessType = (String) AssessParamete.get(0).get("assessType");
		}
		if (assessType == null || "".equals(assessType)) {
			result.put("errorCode", "588");
			result.put("errorMsg", "请先维护高血压模块参数中的评估类型！");
			return result;
		}
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(
					"chis.dictionary.hyperGroupExt");
		} catch (ControllerException e2) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		boolean controlBad = false;// 是否控制不良
//		boolean needReferral = false;// 是否转诊
//		boolean needInsertPlan = false;// 是否需要插入计划
		
		if (!"1".equals(visitEvaluate)) {
			controlBad = true;
		}
//		Map<String, Object> lastVisitData = null;
//		try {
//			lastVisitData = getLastVistRecordVisitId(empiId,
//					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
//		} catch (ParseException e2) {
//			throw new ModelDataOperationException(
//					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
//		}
//		result.put("controlBad", controlBad);
		result.put("oldGroup", hypertensionGroup);
//		if (controlBad == true && lastVisitData != null
//				&& !"1".equals(lastVisitData.get("visitEvaluate"))) {
//			needReferral = true;
//		}
//		result.put("needReferral", needReferral);
		// 本次随访控制不良，判断两周内是否有计划，有者不插入计划
		// 无则插入计划
//		if (controlBad == true && needReferral == false) {
//			boolean hasPlan = checkHasPlanInTwoWeek(empiId, planDate);
//			if (hasPlan == false) {
//				needInsertPlan = true;
//			}
//		}
//		result.put("needInsertPlan", needInsertPlan);
		// 年度评估不需要转组
		if ("2".equals(assessType)) {
			result.put("needChangeGroup", false);
			result.put("needAssess", false);
			result.put("hypertensionGroup", hypertensionGroup);
			result.put("hypertensionGroupName", dic.getText(hypertensionGroup));
			return result;
		}
		List<Map<String, Object>> dbsList = null;
		try {
			cnd = CNDHelper.toListCnd("['eq',['$','a.empiId'],['s','" + empiId
					+ "']]");
			dbsList = dao.doList(cnd, "", MDC_DiabetesRecord);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		int riskLength = 0;
		String riskinessInEstimate = "1";
		if (riskiness != null && !riskiness.equals("")
				&& !riskiness.equals("0")) {
			String[] riskinessList = riskiness.split(",");
			riskLength = riskinessList.length;
		}
		if (riskLength == 1 || riskLength == 2) {
			riskinessInEstimate = "2";
		}
		if (riskLength >= 3) {
			riskinessInEstimate = "3";
		}
		if (dbsList != null && dbsList.size() > 0) {
			riskinessInEstimate = "3";
		}
		if (targetHurt != null && targetHurt.length() > 0
				&& !targetHurt.equals("0")) {
			riskinessInEstimate = "3";
		}
		if (complication != null && complication.length() > 0
				&& !complication.equals("0")) {
			riskinessInEstimate = "4";
		}
		// 查询根据评估影响因素和血压级别得出危险分层的数据
		List<Map<String, Object>> Estimate = null;
		try {
			cnd = CNDHelper.toListCnd("['eq',['$','riskiness'],['s','"
					+ riskinessInEstimate + "']]");
			Estimate = dao.doList(cnd, "", MDC_EstimateDictionary);
		} catch (ExpException e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		int riskLevelGrade = MDCBaseModel.decideHypertensionGrade(
				paresToInt(constriction), paresToInt(diastolic));
		if (riskLevelGrade > 3 || riskLevelGrade == 0) {
			riskLevelGrade = 1;
		}
		result.put("hypertensionLevel", riskLevelGrade);
		String riskLevel = "1";// 危险分层
		if (Estimate == null || Estimate.size() == 0) {
			result.put("errorCode", "588");
			result.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
			return result;
		} else {
			riskLevel = Estimate.get(0).get("HL" + riskLevelGrade) + "";
		}
		if (riskLevel == null || "".equals(riskLevel)) {
			result.put("errorCode", "588");
			result.put("errorMsg", "请先维护高血压模块参数中的危险分层列表！");
			return result;
		}
		result.put("riskLevel", riskLevel);
		if (controlBad == true) {
			cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s", "3");
		} else {
			cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s", "1");
		}
		// 查询根据控制情况和危险分层得出组别的数据
		List<Map<String, Object>> Control = null;
		try {
			Control = dao.doList(cnd, "", MDC_HypertensionControl);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取是否需要定转组参数失败。");
		}
		String newGroup = "";// 组别
		if (Control == null || Control.size() == 0) {
			result.put("errorCode", "588");
			result.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
			return result;
		} else {
			Map<String, Object> map = Control.get(0);
			if (riskLevel.equals("1")) {
				newGroup = (String) map.get("lowRisk");
			} else if (riskLevel.equals("2")) {
				newGroup = (String) map.get("middleRisk");
			} else if (riskLevel.equals("3")) {
				newGroup = (String) map.get("highRisk");
			} else if (riskLevel.equals("4")) {
				newGroup = (String) map.get("veryHighRisk");
			}
		}
		if (newGroup == null || "".equals(newGroup)) {
			result.put("errorCode", "588");
			result.put("errorMsg", "请先维护高血压模块参数中的分组列表！");
			return result;
		}
		boolean needTurn = false;
		if (!newGroup.equals(hypertensionGroup)) {
			needTurn = true;
		}
		result.put("needChangeGroup", needTurn);
		result.put("needAssess", true);
		result.put("hypertensionGroup", newGroup);
		result.put("hypertensionGroupName", dic.getText(newGroup));
		return result;
	}

	
	
	public boolean checkHasPlanInTwoWeek(String empiId, String visitDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		Date visitDate1 = new Date();
		visitDate1.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" from ")
				.append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='1' and to_date(to_char(planDate,'yyyy-mm-dd'),'yyyy-mm-dd') = to_date(to_char(:visitDate1,'yyyy-mm-dd'),'yyyy-mm-dd')")
				.append(" order by planDate desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("visitDate1", visitDate1);
		
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return true;
		}
		return false;
	}
	public boolean checkHasMeddlePlanInTwoWeek(String empiId, String planDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		cDay1.add(Calendar.DAY_OF_MONTH, 2);
		Date planDate1 = new Date();
		planDate1.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" from ")
				.append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='1' and planDate < :planDate1 and planDate > :planDate2")
				.append(" and visitMeddle in('1','2') ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planDate1", planDate1);
		try {
			parameters.put("planDate2",
					new SimpleDateFormat("yyyy-MM-dd").parse(planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
		if (rsList != null && rsList.size() > 0) {
			return true;
		}
		return false;
	}	
	public void deleteMeddlePlanInTwoWeek(String empiId, String planDate)
			throws ModelDataOperationException {
		Calendar cDay1 = Calendar.getInstance();
		try {
			cDay1.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.add(Calendar.WEEK_OF_MONTH, 2);
		cDay1.add(Calendar.DAY_OF_MONTH, 2);
		Date planDate1 = new Date();
		planDate1.setTime(cDay1.getTimeInMillis());
		String hql = new StringBuffer(" delete ")
				.append(PUB_VisitPlan)
				.append(" where empiId =:empiId and businessType='1' and planDate < :planDate1 and planDate > :planDate2")
				.append(" and visitMeddle in('1','2') and visitId is null ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("planDate1", planDate1);
		try {
			parameters.put("planDate2",new SimpleDateFormat("yyyy-MM-dd").parse(planDate));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度最后一条随访记录 的 ID失败！", e);
		}
	}	
	public void insertVisitPlan(HashMap<String, Object> body)
			throws ModelDataOperationException {
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("recordId", body.get("phrId"));
		reqBody.put("businessType", BusinessType.GXY);
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("groupCode", body.get("groupCode"));
		reqBody.put("fixGroupDate", body.get("fixGroupDate"));
		Calendar cDay1 = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(body
					.get("visitDate") + "");
//			date = new SimpleDateFormat("yyyy-MM-dd").parse(body
//					.get("lastPlanDate") + "");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cDay1.setTime(date);
//		cDay1.add(Calendar.DAY_OF_MONTH, 1);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("beginDate", date);
		reqBody.put("beginVisitDate", date);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("planDate", date);
		cDay1.add(Calendar.WEEK_OF_MONTH, 1);
		date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		reqBody.put("endDate", date);
		reqBody.put("planStatus", "0");
		reqBody.put("sn", body.get("sn"));
		reqBody.put("taskDoctorId", body.get("taskDoctorId"));
		UserRoleToken urt = UserRoleToken.getCurrent();
		reqBody.put("lastModifyUnit", urt.getManageUnitId());
		reqBody.put("lastModifyUser", urt.getUserId());
		reqBody.put("lastModifyDate", new Date());
		if(body.containsKey("visitMeddle")){
		reqBody.put("visitMeddle",body.get("visitMeddle"));
		}
		try {
			dao.doSave("create", PUB_VisitPlan, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		}

	}

	public void insertGroupRecord(Map<String, Object> body,
			HashMap<String, Object> planBody,
			VisitPlanCreator visitPlanCreator, Context ctx)
			throws ModelDataOperationException {
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("phrId", body.get("phrId"));
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("hypertensionGroup", body.get("hypertensionGroup"));
		reqBody.put("fixDate", new Date());
		String controlResult = "";
		if ((Boolean) body.get("controlBad") == true) {
			controlResult = "3";
		} else if ((Boolean) body.get("controlBad") == false) {
			controlResult = "1";
		}
		reqBody.put("hypertensionLevel", body.get("hypertensionLevel"));
		reqBody.put("riskLevel", body.get("riskLevel"));
		reqBody.put("controlResult", controlResult);
		reqBody.put("constriction", body.get("constriction"));
		reqBody.put("diastolic", body.get("diastolic"));
		reqBody.put("fixType", "5");
		reqBody.put("targetHurt", body.get("targetHurt"));
		reqBody.put("riskiness", body.get("riskiness"));
		reqBody.put("complication", body.get("complication"));
		reqBody.put("height", body.get("height"));
		reqBody.put("weight", body.get("weight"));
		reqBody.put("bmi", body.get("bmi"));
		reqBody.put("waistLine", body.get("waistLine"));
		reqBody.put("oldGroup", body.get("oldGroup"));
		reqBody.put("manaUnitId", body.get("manaUnitId"));
		UserRoleToken urt = UserRoleToken.getCurrent();
		reqBody.put("fixUnit", urt.getManageUnitId());
		reqBody.put("fixUser", urt.getUserId());
		reqBody.put("lastModifyUnit", urt.getManageUnitId());
		reqBody.put("lastModifyUser", urt.getUserId());
		reqBody.put("lastModifyDate", new Date());
		try {
			dao.doSave("create", MDC_HypertensionFixGroup, reqBody, false);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "插入随访计划失败！", e);
		}
		String visitDate = (String) body.get("visitDate");
		String hypertensionGroup = (String) planBody.get("groupCode");
		try {
			String nextDate = getNextVisitDate(hypertensionGroup,
					new SimpleDateFormat("yyyy-MM-dd").parse(visitDate));
			planBody.put("fixGroupDate", visitDate);
			planBody.put("nextDate", nextDate);
			planBody.put("lastVisitDate", visitDate);
			reqBody.putAll(planBody);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (needCreatePlanNoAsses((String) body.get("empiId"), new Date())) {
			createVisitPlan(reqBody, visitPlanCreator);
		}
	}

	@SuppressWarnings("unchecked")
	public String getNextVisitDate(String hypertensionGroup, Date planDate) {
		Map<String, Object> record = (Map<String, Object>) planTypes
				.get(hypertensionGroup);
		Calendar cDay1 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cDay1.setTime(planDate);
		if (record != null) {
			int frequency =  BSCHISUtil.parseToInt(record.get("frequency")+"");
			int cycle = (Integer) record.get("cycle");
			cDay1.add(frequency, cycle);
			Date date = new Date();
			date.setTime(cDay1.getTimeInMillis());
			return sdf.format(date);
		}
		Schema sc = null;
		String itemid = "";
		try {
			sc = SchemaController.instance().get(ADMIN_HypertensionConfig);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if ("03".equals(hypertensionGroup)) {
			itemid = "planType3";
		} else if ("02".equals(hypertensionGroup)) {
			itemid = "planType2";
		} else {
			itemid = "planType1";
		}
		String exp = null;
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem si : items) {
			if (itemid.equals(si.getId())) {
				exp = (String) si.getProperty("expression");
			}
		}
		String planTypeCode = null;
		if (exp != null) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			String sql = "select planTypeCode as planTypeCode from "
					+ PUB_PlanInstance
					+ " where instanceType='1' and expression=:expression";
			try {
				parameters.put("expression", exp);
				List<Map<String, Object>> list = dao.doQuery(sql, parameters);
				if (list != null && list.size() > 0) {
					planTypeCode = (String) list.get(0).get("planTypeCode");
				}
				record = new HashMap<String, Object>();
				if (planTypeCode != null) {
					record = dao.doLoad(PUB_PlanType, planTypeCode);
				}
				planTypes.put(hypertensionGroup, record);
				int frequency = BSCHISUtil.parseToInt(record.get("frequency")+"");
				int cycle = (Integer) record.get("cycle");
				cDay1.add(frequency, cycle);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		Date date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		return sdf.format(date);
	}

	public double paresToDouble(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Double.parseDouble(value);
	}

	public int paresToInt(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	@SuppressWarnings("deprecation")
	public void getLastThreeDay(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			String endMonth = smm.getSystemConfigData("hypertensionEndMonth");
			String planMode = smm.getSystemConfigData(BusinessType.GXY+"_planMode");
			List<Map<String, Object>> AssessParamete = null;
			List<?> cnd = CNDHelper.toListCnd("['eq',['s','1'],['s','1']]");
			// 查询高危年度评估参数
			AssessParamete = dao
					.doList(cnd, "", MDC_HypertensionAssessParamete);
			String assessType = null;
			if (AssessParamete != null && AssessParamete.size() > 0) {
				assessType = (String) AssessParamete.get(0).get("assessType");
			}
			String assessDays = AssessParamete.get(0).get("assessDays") + "";
			String assessHour1 = AssessParamete.get(0).get("assessHour1") + "";
			String assessHour2 = AssessParamete.get(0).get("assessHour2") + "";
			Calendar cDay1 = Calendar.getInstance();
			Date date = new Date();
			date.setMonth(praseMonthInt(endMonth));
			cDay1.setTime(date);
			int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
			ArrayList<String> list = new ArrayList<String>();
			if (assessDays == null || "".equals(assessDays)) {
				assessDays = "3";
			}
			for (int i = 0; i < BSCHISUtil.parseToInt(assessDays); i++) {
				list.add((lastDay - i) + "");
			}
			body.put("days", list);
			body.put("endMonth", endMonth);
			body.put("assessHour1", assessHour1);
			body.put("assessHour2", assessHour2);
			body.put("assessType", assessType);
			body.put("planMode", planMode);
			res.put("body", body);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取年度评估参数失败！", e);
		}

	}

	private int praseMonthInt(String endMonth) {
		if (endMonth == null || "".equals(endMonth)) {
			return 11;
		}
		int month = 11;
		if (endMonth.startsWith("0")) {
			month = Integer.parseInt(endMonth.substring(1)) - 1;
		} else {
			month = Integer.parseInt(endMonth) - 1;
		}
		return month;
	}

	@SuppressWarnings({ "rawtypes", "null", "unchecked" })
	public void saveHypertensionYearAssess(VisitPlanCreator visitPlanCreator,
			Context ctx) throws ModelDataOperationException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		UserRoleToken urt = UserRoleToken.getCurrent();
		String startMonth = smm.getSystemConfigData("diabetesStartMonth");
		String endMonth = smm.getSystemConfigData("diabetesEndMonth");
		List<Object> cnd = new ArrayList<Object>();
		List<Map<String, Object>> records = null;
		try {
			List<Object> cnd11 = (List<Object>) CNDHelper
					.toListCnd("['eq',['s','1'],['s','1']]");
			// 查询高危年度评估参数
			List<Map<String, Object>> AssessParamete = dao.doList(cnd11, "",
					MDC_HypertensionAssessParamete);
			if (AssessParamete == null || AssessParamete.size() == 0) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
			}
			// 机构过滤，只对当前机构及下属机构进行年度评估
			List cnd0 = CNDHelper
					.toListCnd("['like',['$','a.manaUnitId'],['s','"
							+ urt.getManageUnitId() + "%']]");
			cnd.add(cnd0);
			// 排除注销档案，注销档案不需生成随访和转组记录
			Map<String, Object> mapAssess = AssessParamete.get(0);
			String recordWriteOff = mapAssess.get("recordWriteOff") + "";
			String newPatient = mapAssess.get("newPatient") + "";
			String notNormPatient = mapAssess.get("notNormPatient") + "";
			String oneGroup = mapAssess.get("oneGroup") + "";
			String oneGroupProportion = mapAssess.get("oneGroupProportion")
					+ "";
			String twoGroup = mapAssess.get("twoGroup") + "";
			String twoGroupProportion = mapAssess.get("twoGroupProportion")
					+ "";
			String threeGroup = mapAssess.get("threeGroup") + "";
			String threeGroupProportion = mapAssess.get("threeGroupProportion")
					+ "";
			if ("true".equals(recordWriteOff)) {
				List cnd1 = CNDHelper
						.toListCnd("['eq',['$','a.status'],['s','0']]");
				cnd.add(cnd1);
			}
			String startDate = getStartDateForYear(startMonth);
			String endDate = getEndDateForYear(endMonth);
			if (cnd.size() > 1) {
				cnd.add(0, "and");
			}
			// 获取需要评估的高血压档案
			String sql = "select a.phrId as phrId, a.empiId as empiId,a.manaDoctorId as manaDoctorId from "
					+ MDC_HypertensionRecord
					+ " a where "
					+ ExpressionProcessor.instance().toString(cnd);
			records = dao.doQuery(sql, null);
			// 根据年度评估维护的规范管理标准判断是否进行评估
			Map<String, Object> groupMap = null;
			String sql1 = "select count(planId) as count from PUB_VisitPlan where empiId=:empiId "
					+ "and businessType='1' and planDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and planDate<=to_date('"
					+ endDate + "','yyyy-mm-dd HH24:mi:ss')";
			String sql2 = "select planDate as planDate from PUB_VisitPlan where empiId=:empiId "
					+ "and businessType='1' "
					+ "and planDate=(select max(planDate) from PUB_VisitPlan"
					+ " where empiId=:empiId and businessType='1' and planDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and planDate<=to_date('"
					+ endDate + "','yyyy-mm-dd HH24:mi:ss'))";
			String sql3 = "select recordId from MDC_HypertensionYearAssess "
					+ "where empiId=:empiId and to_char(inputDate,'yyyy')=:year";
			String sql4 = "select constriction as constriction,diastolic as diastolic,"
					+ "visitDate as visitDate,riskiness as riskiness,targetHurt as targetHurt"
					+ ",complication as complication,hypertensionGroup as hypertensionGroup from MDC_HypertensionVisit "
					+ "where empiId=:empiId and visitDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and visitDate<=to_date('"
					+ endDate
					+ "','yyyy-mm-dd HH24:mi:ss') order by visitDate desc";
			String sql6 = "select empiId as empiId from MDC_HypertensionRecord where empiId=:empiId and createDate<to_date('"
					+ startDate + "','yyyy-mm-dd HH24:mi:ss')";
			String sql7 = "select empiId as empiId from MDC_HypertensionRecord where empiId=:empiId and status='0'";
			Map<String, Object> groupPara = new HashMap<String, Object>();
			List<Map<String, Object>> list = null;
			List<Map<String, Object>> mList = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			for (Map<String, Object> map : records) {
				// map：年度评估参数
				// 转组参数
				Map<String, Object> group = new HashMap<String, Object>();
				group.putAll(map);
				// 随访计划生成参数
				HashMap<String, Object> fixJsonReq = new HashMap<String, Object>();
				fixJsonReq.put("nextYear", true);
				fixJsonReq.putAll(map);
				String empiId = (String) map.get("empiId");

				groupPara.put("empiId", empiId);
				groupPara.put("year", sdf.format(new Date()));
				// 判断是否已经进行过该年度的年度评估，如是者跳过
				list = dao.doSqlQuery(sql3, groupPara);
				if (list != null && list.size() > 0) {
					continue;
				}
				groupPara.remove("year");
				// 查询最后一次转组记录以作参考
				List<Object> cndEmpiId = CNDHelper.createSimpleCnd("eq",
						"a.empiId", "s", empiId);
				List<Map<String, Object>> groupList = dao.doList(cndEmpiId,
						"fixDate desc", MDC_HypertensionFixGroup);
				if (groupList == null || groupList.size() == 0) {
					// 没有转组记录的病人就还没有生成随访计划，不进行评估
					continue;
				}
				groupMap = groupList.get(0);
				map.putAll(groupMap);
				group.putAll(groupMap);
				String hypertensionGroup = (String) groupMap
						.get("hypertensionGroup");
				boolean needTurn = false;
				String newGroup = hypertensionGroup;
				// 查询维护的控制情况参数
				Map<String, Object> BPControl = getHypertensionBPControl(hypertensionGroup);
				if (BPControl == null || BPControl.size() == 0) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
				}
				// 查询本年度已做的随访
				list = dao.doSqlQuery(sql4, groupPara);
				int visitCount = list.size();
				map.put("visitCount", visitCount);// 随访次数
				map.put("normManage", "1");
				Date lastVisitDate = null;
				// 获取控制情况
				String visitEvaluate = getControlFlag(list, hypertensionGroup);
				String riskiness = "";
				String targetHurt = "";
				String complication = "";
				String constriction = "";
				String diastolic = "";
				if (list != null && list.size() > 0) {
					lastVisitDate = (Date) list.get(0).get("VISITDATE");
					riskiness = (String) list.get(0).get("RISKINESS");
					targetHurt = (String) list.get(0).get("TARGETHURT");
					complication = (String) list.get(0).get("COMPLICATION");
					constriction = list.get(0).get("CONSTRICTION") + "";
					diastolic = list.get(0).get("DIASTOLIC") + "";
				}
				// 如果注销档案也需评估，判断档案是否注销，是则只生成年度评估，否则继续判断
				if (!"true".equals(recordWriteOff)) {
					mList = dao.doSqlQuery(sql7, groupPara);
					if (mList == null || mList.size() == 0) {
						saveAssessment(map);
						continue;
					}
				}
				group.put("controlResult", visitEvaluate);
				group.put("fixType", "6");
				group.put("fixDate", new Date());
				String nextDate = "";
				Date lastPlanDate = null;
				// 查询本年度最后一条计划日期以便确定下一年度计划
				mList = dao.doSqlQuery(sql2, groupPara);
				if (mList != null && mList.size() > 0) {
					lastPlanDate = (Date) mList.get(0).get("PLANDATE");
					nextDate = getNextVisitDate(hypertensionGroup, lastPlanDate);
				} else {
					nextDate = startDate.substring(0, 10).replace(
							startDate.substring(0, 4),
							paresToInt(startDate.substring(0, 4)) + 1 + "");
				}

				fixJsonReq.put("fixType", "1");
				fixJsonReq.put("instanceType", BusinessType.GXY);
				fixJsonReq.put("groupCode", hypertensionGroup);
				fixJsonReq.put("hypertensionGroup", hypertensionGroup);
				fixJsonReq.put("fixGroupDate", nextDate);
				fixJsonReq.put("nextDate", nextDate);
				fixJsonReq.put("adverseReactions", "");
				fixJsonReq.put("lastPlanDate", lastPlanDate);
				fixJsonReq.put("lastVisitDate", lastVisitDate);
				fixJsonReq.put("visitResult", VisitResult.SATISFIED);
				fixJsonReq.put("taskDoctorId", fixJsonReq.get("manaDoctorId"));

				// 判断是否新病人，如果新病人不评估，者只生成随访计划，否者继续判断
				mList = dao.doSqlQuery(sql6, groupPara);
				if ("true".equals(newPatient)) {
					if (mList != null && mList.size() > 0) {
						map.put("normManage", "3");
						saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
						continue;
					}
				} else {
					if (mList != null && mList.size() > 0) {
						// 新病人如果要评价的话，随访数必须大于如果是年初建档的计划数的一半
						int needVisitCount = getNeedVisitCount(hypertensionGroup);
						if (visitCount > needVisitCount) {
							map.put("normManage", "3");
							saveNextYearVisitPlan(fixJsonReq, visitPlanCreator,
									ctx);
							continue;
						}
					}
				}

				// 查询本年度计划随访数
				Map<String, Object> allPlan = dao.doSqlLoad(sql1, groupPara);
				int allCount = Integer.parseInt(allPlan.get("COUNT") + "");
				// 如果本年度随访数为0或者随访计划数为0，则生成随访
				if (visitCount == 0 || allCount == 0) {
					saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
					continue;
				}
				// 未规范管理的病人不进行年度评估
				if ("true".equals(notNormPatient)) {
					if (((visitCount * 100 / allCount) < paresToDouble(oneGroupProportion)
							&& "01".equals(hypertensionGroup) && "true"
								.equals(oneGroup))
							|| ((visitCount * 100 / allCount) < paresToDouble(twoGroupProportion)
									&& "02".equals(hypertensionGroup) && "true"
										.equals(twoGroup))
							|| ((visitCount * 100 / allCount) < paresToDouble(threeGroupProportion)
									&& "03".equals(hypertensionGroup) && "true"
										.equals(threeGroup))) {
						saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
						continue;
					}
				}
				// 根据原组别和控制情况和一二三组转组标准判断该病人是否需要转组以及新组别
				List<Map<String, Object>> dbsList = null;
				cnd = (List<Object>) CNDHelper
						.toListCnd("['eq',['$','a.empiId'],['s','" + empiId
								+ "']]");
				dbsList = dao.doList(cnd, "", MDC_DiabetesRecord);
				int riskLength = 0;
				String riskinessInEstimate = "1";
				if (riskiness != null && !riskiness.equals("")
						&& !riskiness.equals("0")) {
					String[] riskinessList = riskiness.split(",");
					riskLength = riskinessList.length;
				}
				if (riskLength == 1 || riskLength == 2) {
					riskinessInEstimate = "2";
				}
				if (riskLength >= 3) {
					riskinessInEstimate = "3";
				}
				if (dbsList != null && dbsList.size() > 0) {
					riskinessInEstimate = "3";
				}
				if (targetHurt != null && targetHurt.length() > 0
						&& !targetHurt.equals("0")) {
					riskinessInEstimate = "3";
				}
				if (complication != null && complication.length() > 0
						&& !complication.equals("0")) {
					riskinessInEstimate = "4";
				}
				// 查询根据评估影响因素和血压级别得出危险分层的数据
				List<Map<String, Object>> Estimate = null;
				cnd = (List<Object>) CNDHelper
						.toListCnd("['eq',['$','riskiness'],['s','"
								+ riskinessInEstimate + "']]");
				Estimate = dao.doList(cnd, "", MDC_EstimateDictionary);
				int riskLevelGrade = MDCBaseModel.decideHypertensionGrade(
						paresToInt(constriction), paresToInt(diastolic));
				if (riskLevelGrade > 3 || riskLevelGrade == 0) {
					riskLevelGrade = 1;
				}
				group.put("hypertensionLevel", riskLevelGrade);
				String riskLevel = "1";// 危险分层
				if (Estimate == null || Estimate.size() == 0) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
				} else {
					riskLevel = Estimate.get(0).get("HL" + riskLevelGrade) + "";
				}
				if (riskLevel == null || "".equals(riskLevel)) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
				}
				group.put("riskLevel", riskLevel);
				cnd = CNDHelper.createSimpleCnd("eq", "controlCondition", "s",
						visitEvaluate);
				// 查询根据控制情况和危险分层得出组别的数据
				List<Map<String, Object>> Control = null;
				Control = dao.doList(cnd, "", MDC_HypertensionControl);
				if (Control == null || Control.size() == 0) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
				} else {
					Map<String, Object> map2 = Control.get(0);
					if (riskLevel.equals("1")) {
						newGroup = (String) map2.get("lowRisk");
					} else if (riskLevel.equals("2")) {
						newGroup = (String) map2.get("middleRisk");
					} else if (riskLevel.equals("3")) {
						newGroup = (String) map2.get("highRisk");
					} else if (riskLevel.equals("4")) {
						newGroup = (String) map2.get("veryHighRisk");
					}
				}
				if (newGroup == null || "".equals(newGroup)) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "请先维护高血压高危年度评估参数。");
				}
				if (!newGroup.equals(hypertensionGroup)) {
					needTurn = true;
				}
				if (mList != null && mList.size() > 0) {
					nextDate = getNextVisitDate(newGroup, lastPlanDate);
				} else {
					nextDate = startDate.substring(0, 10).replace(
							startDate.substring(0, 4),
							paresToInt(startDate.substring(0, 4)) + 1 + "");
				}
				fixJsonReq.put("nextDate", nextDate);
				saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
				saveAssessment(map);
				if (needTurn == true) {
					group.put("hypertensionGroup", newGroup);
					group.put("oldGroup", hypertensionGroup);
					saveHypertensionGroup(group);
				}
			}

		} catch (ExpException e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取表达式失败。");
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取日期失败。");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病档案失败。");
		}

	}

	@SuppressWarnings("unchecked")
	private int getNeedVisitCount(String hypertensionGroup) {
		Map<String, Object> record = (Map<String, Object>) planTypes
				.get(hypertensionGroup);
		String frequency = (String) record.get("frequency");
		int cycle = (Integer) record.get("cycle");
		int times = (Integer) record.get("times");
		int NeedVisitCount = 0;
		if ("1".equals(frequency)) {
			NeedVisitCount = times / cycle / 2;
		} else if ("2".equals(frequency)) {
			NeedVisitCount = times * 12 / cycle / 2;
		} else if ("3".equals(frequency)) {
			NeedVisitCount = 52 / cycle / 2;
		} else if ("4".equals(frequency)) {
			NeedVisitCount = 365 / cycle / 2;
		}
		return NeedVisitCount;
	}

	private String getControlFlag(List<Map<String, Object>> records,
			String hypertensionGroup) {
		Map<String, Object> BPControl = getHypertensionBPControl(hypertensionGroup);
		int badControlCount = 0;
		int fineControlCount = 0;
		int goodControlCount = 0;
		for (Map<String, Object> r : records) {
			int constriction = paresToInt(r.get("CONSTRICTION") + "");
			int diastolic = paresToInt(r.get("DIASTOLIC") + "");
			if ((Boolean) BPControl.get("hasBad")) {
				if ((Integer) BPControl.get("badConstriction") > constriction
						|| (Integer) BPControl.get("badDiastolic") > diastolic) {
					badControlCount++;
				}
			}
			if ((Boolean) BPControl.get("hasFine")) {
				if ((Integer) BPControl.get("fineConstriction") > constriction
						|| (Integer) BPControl.get("fineDiastolic") > diastolic) {
					fineControlCount++;
				}
			}
			if ((Boolean) BPControl.get("hasGood")) {
				if ((Integer) BPControl.get("goodConstriction") > constriction
						|| (Integer) BPControl.get("goodDiastolic") > diastolic) {
					goodControlCount++;
				}
			}
		}
		if ((Boolean) BPControl.get("hasGood")) {
			if ((Integer) BPControl.get("goodCount") >= goodControlCount) {
				return "1";
			}
		}
		if ((Boolean) BPControl.get("hasFine")) {
			if ((Integer) BPControl.get("fineCount") >= fineControlCount) {
				return "2";
			}
		}
		if ((Boolean) BPControl.get("hasBad")) {
			if ((Integer) BPControl.get("badCount") <= badControlCount) {
				return "3";
			}
		}
		return "1";
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getHypertensionBPControl(
			String hypertensionGroup) {
		if ("99".equals(hypertensionGroup)) {
			return null;
		}
		if (HyBPControl.get(hypertensionGroup) != null) {
			return (Map<String, Object>) HyBPControl.get(hypertensionGroup);
		}
		List<Map<String, Object>> BPControl = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<?> cnd = CNDHelper.toListCnd("['eq',['$','groups'],['s','"
					+ hypertensionGroup + "']]");
			BPControl = dao.doList(cnd, "", MDC_HypertensionBPControl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int badCount = 0;
		int badConstriction = 0;
		int badDiastolic = 0;
		int goodCount = 0;
		int goodConstriction = 0;
		int goodDiastolic = 0;
		int fineCount = 0;
		int fineConstriction = 0;
		int fineDiastolic = 0;
		boolean hasBad = false;
		boolean hasGood = false;
		boolean hasFine = false;
		for (Map<String, Object> map2 : BPControl) {
			String controlCondition = (String) map2.get("controlCondition");
			int visitCount = (Integer) map2.get("visitCount");
			int constriction = (Integer) map2.get("SBP");
			int diastolic = (Integer) map2.get("DBP");
			if ("3".equals(controlCondition)) {
				hasBad = true;
				badCount = visitCount;
				badConstriction = constriction;
				badDiastolic = diastolic;
				continue;
			} else if ("1".equals(controlCondition)) {
				hasGood = true;
				goodCount = visitCount;
				goodConstriction = constriction;
				goodDiastolic = diastolic;
			} else if ("2".equals(controlCondition)) {
				hasFine = true;
				fineCount = visitCount;
				fineConstriction = constriction;
				fineDiastolic = diastolic;
			}
		}
		if (!hasBad && !hasGood && !hasFine) {
			return null;
		}
		result.put("badCount", badCount);
		result.put("badConstriction", badConstriction);
		result.put("badDiastolic", badDiastolic);
		result.put("fineCount", fineCount);
		result.put("fineConstriction", fineConstriction);
		result.put("fineDiastolic", fineDiastolic);
		result.put("goodCount", goodCount);
		result.put("goodConstriction", goodConstriction);
		result.put("goodDiastolic", goodDiastolic);
		result.put("hasBad", hasBad);
		result.put("hasGood", hasGood);
		result.put("hasFine", hasFine);
		HyBPControl.put(hypertensionGroup, result);
		return result;
	}

	/**
	 * 生成糖尿病下一年度随访计划
	 * 
	 * @param fixJsonReq
	 * @param visitPlanCreator
	 * @param ctx
	 */
	private void saveNextYearVisitPlan(HashMap<String, Object> fixJsonReq,
			VisitPlanCreator visitPlanCreator, Context ctx) {
		try {
			createVisitPlan(fixJsonReq, visitPlanCreator);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存年度评估记录
	 * 
	 * @param map
	 */
	private void saveAssessment(Map<String, Object> map) {
		try {
			UserRoleToken urt = UserRoleToken.getCurrent();
			map.put("inputUser", urt.getUserId());
			map.put("inputUnit", urt.getManageUnitId());
			map.put("inputDate", new Date());
			map.put("lastModifyUser", urt.getUserId());
			map.put("lastModifyUnit", urt.getManageUnitId());
			map.put("lastModifyDate", new Date());
			dao.doSave("create",
					"chis.application.hy.schemas.MDC_HypertensionYearAssess",
					map, true);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 保存转组记录
	 * 
	 * @param group
	 */
	private void saveHypertensionGroup(Map<String, Object> group) {
		UserRoleToken urt = UserRoleToken.getCurrent();
		group.put("createUser", urt.getUserId());
		group.put("createUnit", urt.getManageUnitId());
		group.put("createDate", new Date());
		group.put("lastModifyUser", urt.getUserId());
		group.put("lastModifyUnit", urt.getManageUnitId());
		group.put("lastModifyDate", new Date());
		try {
			dao.doSave("create", MDC_HypertensionFixGroup, group, true);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取配置的年度结束日期
	 * 
	 * @param endMonth
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public String getEndDateForYear(String endMonth) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (endMonth == null || "".equals(endMonth) || "12".equals(endMonth)) {
			source = sdf.format(new Date()) + "-12-31 23:59:59";
			return source;
		}
		int month = 11;
		if (endMonth.startsWith("0")) {
			month = Integer.parseInt(endMonth.substring(1)) - 1;
		} else {
			month = Integer.parseInt(endMonth) - 1;
		}
		Calendar cDay1 = Calendar.getInstance();
		Date date = new Date();
		date.setMonth(month);
		cDay1.setTime(date);
		int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		source = sdf.format(new Date()) + "-" + endMonth + "-" + lastDay
				+ " 23:59:59";
		return source;
	}

	/**
	 * 获取配置的年度开始日期
	 * 
	 * @param startMonth
	 * @return
	 * @throws ParseException
	 */
	public String getStartDateForYear(String startMonth) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (startMonth == null || "".equals(startMonth)
				|| "01".equals(startMonth)) {
			source = sdf.format(new Date()) + "-01-01 00:00:00";
			return source;
		}
		source = (Integer.parseInt(sdf.format(new Date())) - 1) + "-"
				+ startMonth + "-01 00:00:00";
		return source;
	}

	/**
	 * 根据schema设置字典文本数据,且该数据用sqlLoad查询得到
	 * 
	 * @param info
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> setDictionaryMessageForListFromSQL(
			Map<String, Object> info, String schemaName) {
		if (info == null || info.size() < 1) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		Schema schema = null;
		try {
			schema = SchemaController.instance().get(schemaName);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			String itemKey = item.getId();
			Object keyValue = info.get(itemKey.toUpperCase());
			if (keyValue == null) {
				continue;
			}
			data.put(itemKey, keyValue);

		}
		return data;
	}

	public Map<String, Object> listHypertensionVistPlan(Map<String, Object> req)
			throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(
					"chis.application.hy.schemas.MDC_HypertensionVisitPlan");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" PUB_VisitPlan a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" join MDC_HypertensionRecord d on a.empiId=d.empiId ")
				.append(" left join MDC_HypertensionVisit e on a.empiId=e.empiId and a.visitid=e.visitid");
		StringBuffer countSQL = new StringBuffer(
				"select count(a.planId) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where);
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询高血压随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select ").append(sfBuffer.substring(1)).append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where);
			}
			sql.append(" order by a.planDate ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询高血压随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body",
						SchemaUtil
								.setDictionaryMessageForList(tpList,
										"chis.application.hy.schemas.MDC_HypertensionVisitPlan"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;

	}
	
	public Map<String, Object> listHypertensionVistPlanQC(Map<String, Object> req)
			throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(
					"chis.application.hy.schemas.MDC_HypertensionVisitPlan");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
					.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" PUB_VisitPlan a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" join MDC_HypertensionRecord d on a.empiId=d.empiId ")
				.append(" left join MDC_HypertensionVisit e on a.empiId=e.empiId and a.visitid=e.visitid");
		StringBuffer countSQL = new StringBuffer(
				"select count(distinct b.idCard) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where)
			.append(" and e.visitway in ('1', '4', '5') and e.visitEffect='1'");
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询高血压随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from (select ").append(sfBuffer.substring(1)).append(",row_number()over(partition by a.empiId order by a.planDate) rn ").append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where)
				.append(" and e.visitEffect='1'  and e.visitway in ('1', '4', '5'))");
			}
			sql.append(" where rn=1 ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询高血压随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body",
						SchemaUtil
								.setDictionaryMessageForList(tpList,
										"chis.application.hy.schemas.MDC_HypertensionVisitPlan"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;

	}
	
	public Map<String, Object> checkHasVisitInPerCycle(String empiId,
			Date visitDate) throws ModelDataOperationException {
		boolean hasVisit = false;
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> record = getLastVistRecordVisitId(empiId, visitDate);
		if (record != null && record.size() > 0) {
			String hypertensionGroup = (String) record.get("hypertensionGroup");
			Date lastVisitDate = (Date) record.get("visitDate");
			Schema sc = null;
			String itemid = "";
			try {
				sc = SchemaController.instance().get(ADMIN_HypertensionConfig);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			if ("03".equals(hypertensionGroup)) {
				itemid = "planType3";
			} else if ("02".equals(hypertensionGroup)) {
				itemid = "planType2";
			} else {
				itemid = "planType1";
			}
			String exp = null;
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				if (itemid.equals(si.getId())) {
					exp = (String) si.getProperty("expression");
				}
			}
			String planTypeCode = null;
			if (exp != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "select planTypeCode as planTypeCode from "
						+ PUB_PlanInstance
						+ " where instanceType='1' and expression=:expression";
				parameters.put("expression", exp);
				Map<String, Object> plan = new HashMap<String, Object>();
				try {
					List<Map<String, Object>> list = dao.doQuery(sql,
							parameters);
					if (list != null && list.size() > 0) {
						planTypeCode = (String) list.get(0).get("planTypeCode");
					}

					if (planTypeCode != null) {
						plan = dao.doLoad(PUB_PlanType, planTypeCode);
					}
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				int frequency = BSCHISUtil.parseToInt(plan.get("frequency")
						+ "");
				int cycle = (Integer) plan.get("cycle");
				int allCycleDays = BSCHISUtil.getAllCycleDays(lastVisitDate,
						cycle, frequency);
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastVisitDate);
				cal.add(Calendar.DAY_OF_YEAR, allCycleDays / 2);
				Date lineDate = cal.getTime();
				result.put("lineDate",
						new SimpleDateFormat("yyyy年MM月dd日").format(lineDate));
				if (BSCHISUtil.dateCompare(visitDate, lineDate) < 0) {
					hasVisit = true;
				}
				result.put("hasVisit", hasVisit);
			}
		}
		return result;
	}
	public Map<String, Object> checkHasVisitInPerCycle(String group,
			String empiId, Date visitDate) throws ModelDataOperationException {
		boolean hasVisit = false;
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> record = getLastVistRecordVisitId(empiId, visitDate);
		if (record != null && record.size() > 0) {
			String hypertensionGroup = group;
			Date lastVisitDate = (Date) record.get("visitDate");
			Schema sc = null;
			String itemid = "";
			try {
				sc = SchemaController.instance().get(ADMIN_HypertensionConfig);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
			if ("03".equals(hypertensionGroup)) {
				itemid = "planType3";
			} else if ("02".equals(hypertensionGroup)) {
				itemid = "planType2";
			} else {
				itemid = "planType1";
			}
			String exp = null;
			List<SchemaItem> items = sc.getItems();
			for (SchemaItem si : items) {
				if (itemid.equals(si.getId())) {
					exp = (String) si.getProperty("expression");
				}
			}
			String planTypeCode = null;
			if (exp != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String sql = "select planTypeCode as planTypeCode from "
						+ PUB_PlanInstance
						+ " where instanceType='1' and expression=:expression";
				parameters.put("expression", exp);
				Map<String, Object> plan = new HashMap<String, Object>();
				try {
					List<Map<String, Object>> list = dao.doQuery(sql,
							parameters);
					if (list != null && list.size() > 0) {
						planTypeCode = (String) list.get(0).get("planTypeCode");
					}

					if (planTypeCode != null) {
						plan = dao.doLoad(PUB_PlanType, planTypeCode);
					}
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				int frequency = BSCHISUtil.parseToInt(plan.get("frequency")
						+ "");
				int cycle = (Integer) plan.get("cycle");
				int allCycleDays = BSCHISUtil.getAllCycleDays(lastVisitDate,
						cycle, frequency);
				Calendar cal = Calendar.getInstance();
				cal.setTime(lastVisitDate);
				cal.add(Calendar.DAY_OF_YEAR, allCycleDays / 2);
				Date lineDate = cal.getTime();
				result.put("lineDate",
						new SimpleDateFormat("yyyy年MM月dd日").format(lineDate));
				if (BSCHISUtil.dateCompare(visitDate, lineDate) < 0) {
					hasVisit = true;
				}
				result.put("hasVisit", hasVisit);
			}
		}
		return result;
	}
	public String getNearHypertensionGroup(String empiId)
			throws ModelDataOperationException {
		String hypertensionGroup = "";
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "a.fixDate desc", MDC_HypertensionFixGroup);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询高血压最后一次分组失败！", e);
		}
		if (list != null && list.size() > 0) {
			hypertensionGroup = (String) list.get(0).get("hypertensionGroup");
		}
		return hypertensionGroup;
	}
	public void deleteHypertensionVistPlanbyplanId(String planId)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			String hql=" delete from PUB_VisitPlan where planId ='"+planId+"'and " +
					   " businessType='1'";
			String bfsql="insert into PUB_VisitPlan_delete(PLANID,VISITMEDDLE,TOAPP,TASKDOCTORID," +
					" LASTMODIFYDATE,LASTMODIFYUNIT,LASTMODIFYUSER,EXTEND1,SN,EXTEND2,REMARK,VISITID," +
					" VISITDATE,PLANSTATUS,PLANDATE,BEGINVISITDATE,ENDDATE,BEGINDATE,FIXGROUPDATE," +
					" GROUPCODE,BUSINESSTYPE,EMPIID,RECORDID) " +
					" select PLANID,VISITMEDDLE,TOAPP,TASKDOCTORID,sysdate," +
					" '"+user.getManageUnitId()+"','"+user.getUserId()+"',EXTEND1,SN,EXTEND2,REMARK,VISITID,VISITDATE,PLANSTATUS," +
					" PLANDATE,BEGINVISITDATE,ENDDATE,BEGINDATE,FIXGROUPDATE,GROUPCODE,BUSINESSTYPE,EMPIID," +
					" RECORDID from PUB_VisitPlan where planId ='"+planId+"'and businessType='1'";
			Session con =dao.getSession();
			Transaction t=con.beginTransaction();
			t.begin();
			con.createSQLQuery(bfsql).executeUpdate();
			con.createSQLQuery(hql).executeUpdate();
			t.commit();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除随访计划失败！", e);
		}
	}
}
