/*
 * @(#)VisitPlanModel.java Created on 2011-12-27 下午3:26:51
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.visitplan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.ManageYearUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 * 
 */
public class VisitPlanModel implements BSCHISEntryNames {

	public String RECORDID = "recordId";

	public String EMPIID = "empiId";

	private BaseDAO dao;

	public VisitPlanModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取第一条随访计划ID(一人存在多条记录的档案如孕妇档案慎用)
	 * 
	 * @param empiId
	 * @param instanceType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getFirstPlanId(String empiId, String instanceType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select planId as planId from ")
				.append("PUB_VisitPlan")
				.append(" where empiId = :empiId and businessType = :businessType ")
				.append("order by beginDate").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("empiId", empiId);
		params.put("businessType", instanceType);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (list.size() > 0) {
			Map<String, Object> r = list.get(0);
			return (String) r.get("planId");
		}
		return null;
	}

	public Map<String, Object> getPlanbyVisitId( String visitId )
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append("PUB_VisitPlan")
				.append(" where visitId = :visitId  ").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("visitId", visitId);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (list.size() > 0) {
			Map<String, Object> r = list.get(0);
			return r;
		}
		return null;
	}
	/**
	 * 添加一条随访计划。
	 * 
	 * @param plan
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void addPlan(CreatationParams params, Map<String, Object> plan,
			Context ctx) throws ValidateException, ModelDataOperationException {
		// Schema sc = SchemaController.instance().getSchema(PUB_VisitPlan);
		// SchemaItem si = sc.getKey();
		// if (si != null && si.isGenerate()) {
		// plan.put(si.getId(),
		// MsockUtil.execute(sc.getTableName(), si.getKeyRules(), ctx));
		// }
		for (String key : plan.keySet()) {
			Object o = plan.get(key);
			if (o instanceof Date) {
				plan.put(key, BSCHISUtil.toString((Date) o));
			}
		}
		try {
			dao.doInsert(PUB_VisitPlan, plan, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 获取某份档案的随访计划中当前最大序号。
	 * 
	 * @param recordId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getMaxSN(String recordId, String businessType)
			throws ModelDataOperationException {
		String hql = new StringBuilder("select max(sn) as sn from ")
				.append("PUB_VisitPlan")
				.append(" where recordId=:recordId and businessType=:businessType")
				.toString();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("recordId", recordId);
		p.put("businessType", businessType);
		Map<String, Object> r;
		try {
			r = dao.doLoad(hql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (r == null || r.get("sn") == null) {
			return 0;
		}
		return (Integer) r.get("sn");
	}

	/**
	 * 根据planId获取一条随访计划。
	 * 
	 * @param planId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPlan(String planId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(PUB_VisitPlan, planId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取随访计划失败!", e);
		}
	}

	/**
	 * 删除某个日期以后的未处理过的计划。
	 * 
	 * @param recordId
	 *            档案编号
	 * @param beginDate
	 *            可以为null，当时null的时候，删除所有未访的计划。
	 * @param businessType
	 *            计划类型
	 * @throws ModelDataOperationException
	 */
	public void deleteExistPlan(String recordId, Date beginDate,
			String... businessType) throws ModelDataOperationException {
//		StringBuilder hql = new StringBuilder("delete from ").append(
//				"PUB_VisitPlan").append(
		StringBuilder hql = new StringBuilder("update ").append(
				"PUB_VisitPlan set planStatus='2' ").append(
				" where recordId = :recordId  and  planStatus = :planStatus");
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (beginDate != null) {
			hql.append(" and planDate>=:planDate");
			parameters.put("planDate", beginDate);
		}
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				parameters.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		parameters.put("recordId", recordId);
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (Exception e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 当前是否还有未执行的计划且在可访的范围内。
	 * 
	 * @param recordId
	 *            档案编号
	 * @param date
	 *            随访结束日期 当不为null时查询限制在endDate大于该值的计划，当为null时不限制计划的时间问题。
	 * @param businessType
	 *            计划类型
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean hasVisitPlan(String recordId, Date date,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as count from ")
				.append("PUB_VisitPlan").append(" where recordId=:recordId ")
				.append(" and planStatus=:planStatus");
		Map<String, Object> p = new HashMap<String, Object>();
		if (date != null) {
			hql.append(" and endDate>=:date");
			p.put("date", date);
		}
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				p.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		p.put("recordId", recordId);
		p.put("planStatus", PlanStatus.NEED_VISIT);
		Map<String, Object> r;
		try {
			r = dao.doLoad(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (r == null || (Long) r.get("count") == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前的计划类型，指的是记录在档案里的计划类型编码对应的类型，在本轮计划生成之前。
	 * 
	 * @param empiId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 */
	public PlanType getCurrentPlanType(String empiId, String businessType)
			throws ModelDataOperationException, ControllerException {
		String hql = new StringBuffer(
				"select a.planTypeCode as planTypeCode,a.frequency as frequency,")
				.append("a.cycle as cycle from ").append(PUB_PlanType)
				.append(" a,")
				.append(BusinessTypeMapper.getEntryName(businessType))
				.append(" b where b.empiId=:empiId and ")
				.append("a.planTypeCode = b.planTypeCode").toString();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("empiId", empiId);
		Map<String, Object> results;
		try {
			results = (Map<String, Object>) dao.doLoad(hql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (results == null) {
			throw new ModelDataOperationException("Plan type not found.");
		}
		PlanType pt = new PlanType();
		pt.setPlanTypeCode((String) results.get("planTypeCode"));
		pt.setFrequency(Integer.parseInt((String) results.get("frequency")));
		pt.setCycle((Integer) results.get("cycle"));
		String strPrecedeDays = ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, businessType + "_precedeDays");
		pt.setPrecedeDays(Integer.valueOf(strPrecedeDays));
		String strDelayDays = ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, businessType + "_delayDays");
		pt.setDelayDays(Integer.valueOf(strDelayDays));
		return pt;
	}

	/**
	 * @param planTypeCodes
	 * @return 本方法返回的PlanType实例中没有precedeDays和delayDays这两个属性。
	 * @throws ModelDataOperationException
	 */
	public List<PlanType> getPlanTypes(List<String> planTypeCodes)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select planTypeCode as planTypeCode, frequency as frequency,")
				.append("cycle as cycle from ")
				.append(BSCHISEntryNames.PUB_PlanType)
				.append(" where planTypeCode  in (:planTypeCodes)");
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("planTypeCodes", planTypeCodes);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		List<PlanType> pts = new ArrayList<PlanType>(list.size());
		for (Map<String, Object> map : list) {
			PlanType pt = new PlanType();
			pt.setPlanTypeCode((String) map.get("planTypeCode"));
			pt.setFrequency(Integer.parseInt((String) map.get("frequency")));
			pt.setCycle((Integer) map.get("cycle"));
			pts.add(pt);
		}
		return pts;
	}

	/**
	 * @param planTypeCode
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ControllerException
	 */
	public PlanType getPlanType(String planTypeCode, String businessType)
			throws ModelDataOperationException, ControllerException {
		StringBuffer hql = new StringBuffer(
				"select planTypeCode as planTypeCode, frequency as frequency,")
				.append("cycle as cycle from ")
				.append(BSCHISEntryNames.PUB_PlanType)
				.append(" where planTypeCode =:planTypeCode");
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("planTypeCode", planTypeCode);
		Map<String, Object> map;
		try {
			map = dao.doLoad(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		PlanType pt = new PlanType();
		pt.setPlanTypeCode((String) map.get("planTypeCode"));
		pt.setFrequency(Integer.parseInt((String) map.get("frequency")));
		pt.setCycle((Integer) map.get("cycle"));
		String strPrecedeDays = ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, businessType + "_precedeDays");
		pt.setPrecedeDays(Integer.valueOf(strPrecedeDays));
		String strDelayDays = ApplicationUtil.getProperty(
				Constants.UTIL_APP_ID, businessType + "_delayDays");
		pt.setDelayDays(Integer.valueOf(strDelayDays));
		return pt;
	}

	/**
	 * @throws ModelDataOperationException
	 */
	public void deletePlanTypes() throws ModelDataOperationException {
		String hql = new StringBuilder("delete from ").append(PUB_PlanType)
				.toString();
		try {
			dao.doUpdate(hql, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * @param op
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePlanType(String op,
			Map<String, Object> record) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, PUB_PlanType, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 前一年度的老年人随访计划的的起始日期。
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getOldPeopleLastYearBeginDate(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select max(extend2) as beginDate from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.toString();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("empiId", empiId);
		p.put("businessType", BusinessType.LNR);
		Map<String, Object> r;
		try {
			r = dao.doLoad(hql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (r != null && r.get("beginDate") != null) {
			return BSCHISUtil.toDate((String) r.get("beginDate"));
		} else {
			return null;
		}
	}

	/**
	 * 前一年度的老年人随访计划的的起始日期。
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getRVCLastYearBeginDate(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select max(extend2) as beginDate from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.toString();
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("empiId", empiId);
		p.put("businessType", BusinessType.RVC);
		Map<String, Object> r;
		try {
			r = dao.doLoad(hql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (r != null && r.get("beginDate") != null) {
			return BSCHISUtil.toDate((String) r.get("beginDate"));
		} else {
			return null;
		}
	}

	/**
	 * 获取最后一次随访计划的计划日期。
	 * 
	 * @param recordId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastEndedPlanDate(String recordId, String... businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select max(endDate) as endDate from ").append("PUB_VisitPlan")
				.append(" where recordId = :recordId ");
		Map<String, Object> p = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				p.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		p.put("recordId", recordId);
		Map<String, Object> m;
		try {
			m = dao.doLoad(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (m != null) {
			return (Date) m.get("endDate");
		} else {
			return null;
		}
	}

	/**
	 * 获取最后一次随访计划的计划日期。
	 * 
	 * @param recordId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastPlanDate(String recordId, String... businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select max(planDate) as planDate from ").append(
				"PUB_VisitPlan").append(" where recordId = :recordId ");
		Map<String, Object> p = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				p.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		p.put("recordId", recordId);
		Map<String, Object> m;
		try {
			m = dao.doLoad(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		if (m != null) {
			return (Date) m.get("planDate");
		} else {
			return null;
		}
	}

	/**
	 * 获取最后一次随访过的计划的某个字段值
	 * 
	 * @param fieldName
	 * @param recordId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	private Object getLastVisitedPlanFieldData(String fieldName,
			String recordId, String... businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select max(").append(fieldName)
				.append(") as ").append(fieldName).append(" from ")
				.append("PUB_VisitPlan")
				.append(" where recordId=:recordId and planStatus=:planStatus");
		Map<String, Object> p = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				p.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		p.put("recordId", recordId);
		p.put("planStatus", PlanStatus.VISITED);
		Map<String, Object> r;
		try {
			r = dao.doLoad(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取最后一次随访过的计划的"
							+ fieldName + "失败!", e);
		}
		if (r != null) {
			return r.get(fieldName);
		}
		return null;
	}

	/**
	 * 获得最后一次访过的随访计划的计划日期。
	 * 
	 * @param recordId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastVisitedPlanDate(String recordId, String... businessType)
			throws ModelDataOperationException {
		Object planDate = getLastVisitedPlanFieldData("planDate", recordId,
				businessType);
		if (planDate == null) {
			return null;
		} else {
			return (Date) planDate;
		}
	}

	/**
	 * 获取最后一次随访过的计划的编号
	 * 
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getLastVisitedPlanId(String recordId, String... businessType)
			throws ModelDataOperationException {
		Object planId = getLastVisitedPlanFieldData("planId", recordId,
				businessType);
		if (planId == null) {
			return null;
		} else {
			return (String) planId;
		}
	}

	/**
	 * 将过期未执行的计划置为未访状态。
	 * 
	 * @param recordId
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public void setOverDatePlan(String recordId, String endDate,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus1 where recordId=:recordId")
				.append(" and planStatus=:planStatus2");
		Map<String, Object> p = new HashMap<String, Object>();
		if (endDate != null && !endDate.equals("")) {
			hql.append(" and str(endDate,'yyyy-MM-dd') < :endDate");
			p.put("endDate", endDate);
		}
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				p.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		p.put("recordId", recordId);
		p.put("planStatus1", PlanStatus.NOT_VISIT);
		p.put("planStatus2", PlanStatus.NEED_VISIT);
		try {
			dao.doUpdate(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改过期的随访计划信息失败！", e);
		}
	}

	/**
	 * 恢复计划时将终止管理的计划置为未访状态。
	 * 
	 * @param recordId
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public void setEndedManagePlan(String visitId, String businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus where visitId=:visitId")
				.append(" and businessType = :businessType");
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("visitId", visitId);
		p.put("planStatus", PlanStatus.NOT_VISIT);
		p.put("businessType", businessType);
		try {
			dao.doUpdate(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改随访计划信息失败！", e);
		}
	}

	/**
	 * 根据planId将计划置为未访状态。
	 * 
	 * @param planId
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public void setUnvisitedPlanStatus(String planId, String businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus where planId=:planId")
				.append(" and businessType = :businessType");
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("planId", planId);
		p.put("planStatus", PlanStatus.NOT_VISIT);
		p.put("businessType", businessType);
		try {
			dao.doUpdate(hql.toString(), p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改随访计划信息失败！", e);
		}
	}

	/**
	 * @param businessType
	 * @param ctx
	 * @return
	 * @throws CreateVisitPlanException
	 */
	public List<PlanInstance> getPlanInstanceExpressions(String businessType,
			Context ctx) throws CreateVisitPlanException {
		BaseDAO dao = new BaseDAO();
		String hql = new StringBuilder("select planTypeCode as planTypeCode, ")
				.append("expression as expression from ")
				.append("PUB_PlanInstance")
				.append(" where instanceType=:instanceType").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("instanceType", businessType);
		List<Map<String, Object>> res = null;
		try {
			res = dao.doQuery(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new CreateVisitPlanException(
					"Cannot get plan instance exceptions.", e);
		}
		List<PlanInstance> list = new ArrayList<PlanInstance>(res.size());
		for (Map<String, Object> r : res) {
			PlanInstance pi = new PlanInstance();
			pi.setExpression((String) r.get("expression"));
			pi.setInstanceType(businessType);
			pi.setPlanTypeCode((String) r.get("planTypeCode"));
			list.add(pi);
		}
		return list;
	}

	/**
	 * @param instanceType
	 * @throws ModelDataOperationException
	 */
	public void deletePlanInstanceByInstanceType(String instanceType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(PUB_PlanInstance)
				.append(" where instanceType =:instanceType ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("instanceType", instanceType);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	public void savePlanInstanceRecord(String op, Map<String, Object> record)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doSave(op, PUB_PlanInstance, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 获取前一次随访的信息
	 * 
	 * @param empiId
	 * @param planId
	 * @param businessType
	 *            eg. 高血压 Constants.INSTANCE_TYPE_GXY <br>
	 *            糖尿病 Constants.INSTANCE_TYPE_TNB
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getLastVisitId(String empiId, String businessType,
			String planId) throws ModelDataOperationException {
		String hql = new StringBuffer("select max(visitId) as visitId from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType ")
				.append("and planId<:planId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("businessType", businessType);
		parameters.put("planId", planId);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "Get max visitId failid.", e);
		}
		if (map != null) {
			return (String) map.get("visitId");
		}
		return null;
	}

	/**
	 * 获取下一次需要随访的计划的日期字段的值
	 * 
	 * @param recordId
	 * @param dateFieldName
	 *            日期字段名
	 * @param filterDateValue
	 *            过滤条件
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	private Date getNextPlanRecordDate(String recordId, String dateFieldName,
			String filterDateValue, String... businessType)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select min(")
				.append(dateFieldName).append(") as ").append(dateFieldName)
				.append(" from ").append("PUB_VisitPlan")
				.append(" where recordId = :recordId ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (dateFieldName != null) {
			hql.append(" and  str(").append(dateFieldName)
					.append(",'yyyy-mm-dd') > :").append(dateFieldName);
			parameters.put(dateFieldName, filterDateValue);
		}
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				parameters.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		parameters.put("recordId", recordId);
		Map<String, Object> res = null;
		try {
			res = dao.doLoad(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取下一次随访日期失败", e);
		}
		if (res == null) {
			return null;
		}
		return (Date) res.get(dateFieldName);
	}

	/**
	 * 获取下次随访计划的开始日期
	 * 
	 * @param recordId
	 * @param filterDateValue
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getNextPlanBeginDate(String recordId, String filterDateValue,
			String... businessType) throws ModelDataOperationException {
		return getNextPlanRecordDate(recordId, "beginDate", filterDateValue,
				businessType);
	}

	/**
	 * 获取下次随访计划的计划日期
	 * 
	 * @param recordId
	 * @param filterDateValue
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getNextPlanDate(String recordId, String filterDateValue,
			String... businessType) throws ModelDataOperationException {
		return getNextPlanRecordDate(recordId, "planDate", filterDateValue,
				businessType);
	}

	/**
	 * 获取年度随访计划 <br/>
	 * 
	 * @param req
	 *            [startDate:'',endDate:'',empiId:'',instanceType:'']
	 * @throws ModelDataOperationException
	 */
	protected List<Map<String, Object>> getVisitPlan(Map<String, Object> req)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		String strStartDate = sdf.format(req.get("startDate"));
		String strEndDate = sdf.format(req.get("endDate"));
		String empiId = (String) req.get("empiId");
		String instanceType = (String) req.get("instanceType");

		String initCnd = "[\"and\", [\"eq\", [\"$\", \"empiId\"], [\"s\", \""
				+ empiId
				+ "\"]], [\"eq\", [\"$\", \"businessType\"], [\"s\", \""
				+ instanceType + "\"]]]";

		String dateCnd = "[\"and\", [\"ge\", [\"$\", \"str(planDate,'yyyy-MM-dd')\"], [\"s\", \""
				+ strStartDate
				+ "\"]], [\"le\", [\"$\", \"str(planDate,'yyyy-MM-dd')\"], [\"s\", \""
				+ strEndDate + "\"]]]";

		String cnd = "[\"and\", " + initCnd + ", " + dateCnd + "]]";
		try {
			return dao.doQuery(CNDHelper.toListCnd(cnd), "visitDate,planDate asc ",
					PUB_VisitPlan);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取年度随访计划失败！", e);
		}
	}

	/**
	 * 获取年度随访计划 <br/>
	 * 
	 * @param req
	 *            [startDate:'',endDate:'',empiId:'',instanceType:'']
	 * @throws ModelDataOperationException
	 */
	protected List<Map<String, Object>> getVisitPlanByEIRI(
			Map<String, Object> req) throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		String strStartDate = sdf.format(req.get("startDate"));
		String strEndDate = sdf.format(req.get("endDate"));
		String empiId = (String) req.get("empiId");
		String recordId = (String) req.get("recordId");
		String instanceType = (String) req.get("instanceType");

		String initCnd = "[\"and\", [\"eq\", [\"$\", \"empiId\"], [\"s\", \""
				+ empiId + "\"]], [\"eq\", [\"$\", \"recordId\"], [\"s\", \""
				+ recordId
				+ "\"]],[\"eq\", [\"$\", \"businessType\"], [\"s\", \""
				+ instanceType + "\"]]]";

		String dateCnd = "[\"and\", [\"ge\", [\"$\", \"str(planDate,'yyyy-MM-dd')\"], [\"s\", \""
				+ strStartDate
				+ "\"]], [\"le\", [\"$\", \"str(planDate,'yyyy-MM-dd')\"], [\"s\", \""
				+ strEndDate + "\"]]]";

		String cnd = "[\"and\", " + initCnd + ", " + dateCnd + "]]";
		try {
			return dao.doQuery(CNDHelper.toListCnd(cnd), " beginDate asc ",
					PUB_VisitPlan);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取年度随访计划失败！", e);
		}
	}

	/**
	 * 获取年度随访计划列表<br>
	 * 取年度随访/询问记录 共用方法
	 * 
	 * @param yearType
	 *            [1:上一年 2：本年 3 ：下一年]
	 * @param empiId
	 * @param current
	 *            向上或向下 的年数,本年度该参数无效
	 * @param businessType
	 *            随访/询问 随访计划类型 <br>
	 *            eg. 高血压随访 BusinessType.GXY
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public List<Map<String, Object>> getVisitPlan(int yearType, String empiId,
			int current, String businessType)
			throws ModelDataOperationException, ServiceException {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(empiId)) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL, "The empiId is null!");
		}
		reqMap.put("empiId", empiId);

		Date startDate = null;
		Date endDate = null;
		Calendar c = Calendar.getInstance();
		if (yearType == 1) {
			c.add(Calendar.YEAR, current + 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanPreYearStartDate(businessType);
			endDate = util.getVisitPlanPreYearEndDate(businessType);
		} else if (yearType == 2) {
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanCurYearStartDate(businessType);
			endDate = util.getVisitPlanCurYearEndDate(businessType);
		} else if (yearType == 3) {
			c.add(Calendar.YEAR, current - 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanNextYearStartDate(businessType);
			endDate = util.getVisitPlanNextYearEndDate(businessType);
		}

		reqMap.put("startDate", startDate);
		reqMap.put("endDate", endDate);
		reqMap.put("instanceType", businessType);

		return this.getVisitPlan(reqMap);
	}

	/**
	 * 获取年度随访计划列表<br>
	 * 取年度随访/询问记录 共用方法
	 * 
	 * @param yearType
	 *            [1:上一年 2：本年 3 ：下一年]
	 * @param empiId
	 * @param current
	 *            向上或向下 的年数,本年度该参数无效
	 * @param businessType
	 *            随访/询问 随访计划类型 <br>
	 *            eg. 高血压随访 BusinessType.GXY
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public List<Map<String, Object>> getVisitPlan(int yearType, String empiId,
			String recordId, int current, String businessType)
			throws ModelDataOperationException, ServiceException {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(recordId) || StringUtils.isEmpty(empiId)) {
			throw new ModelDataOperationException(
					Constants.CODE_BUSINESS_DATA_NULL,
					"The recordId or empiId is null!");
		}
		reqMap.put("recordId", recordId);
		reqMap.put("empiId", empiId);

		Date startDate = null;
		Date endDate = null;
		Calendar c = Calendar.getInstance();
		if (yearType == 1) {
			c.add(Calendar.YEAR, current + 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanPreYearStartDate(businessType);
			endDate = util.getVisitPlanPreYearEndDate(businessType);
		} else if (yearType == 2) {
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanCurYearStartDate(businessType);
			endDate = util.getVisitPlanCurYearEndDate(businessType);
		} else if (yearType == 3) {
			c.add(Calendar.YEAR, current - 1);
			ManageYearUtil util = new ManageYearUtil(c.getTime());
			startDate = util.getVisitPlanNextYearStartDate(businessType);
			endDate = util.getVisitPlanNextYearEndDate(businessType);
		}

		reqMap.put("startDate", startDate);
		reqMap.put("endDate", endDate);
		reqMap.put("instanceType", businessType);

		return this.getVisitPlanByEIRI(reqMap);
	}

	/**
	 * 保存或者更新随访计划
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveVisitPlan(String op, Map<String, Object> body)
			throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.PUB_VisitPlan, body, true);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("随访计划保存失败", e);
		}
	}

	/**
	 * 注销随访计划
	 * 
	 * @param whereField
	 *            可以确定档案的字段，如empiId,recordId，按照不同的情况使用不同的字段来注销档案
	 *            如注销单条体弱儿档案whereField用recordId,注销某人的所有体弱儿档案whereField用empiId
	 * @param whereValue
	 * @param businessType
	 *            可以为null,和whereField为empiId时一起使用，此时注销此人的所有档案
	 * @throws ModelDataOperationException
	 */
	public void logOutVisitPlan(String whereField, String whereValue,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyDate=:lastModifyDate")
				.append(" where planStatus=:planStatus1 ").append(" and ")
				.append(whereField).append(" = :whereValue");
		Map<String, Object> map = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				map.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		map.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		map.put("lastModifyDate", new Date());
		map.put("planStatus", PlanStatus.WRITEOFF);
		map.put("planStatus1", PlanStatus.NEED_VISIT);
		map.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销计划信息失败!", e);
		}
	}

	/**
	 * 恢复注销的随访计划
	 * 
	 * @param whereField
	 *            可以确定档案的字段，如empiId,recordId，按照不同的情况使用不同的字段来恢复档案
	 *            如恢复单条体弱儿档案whereField用recordId,恢复某人的所有体弱儿档案whereField用empiId
	 * @param whereValue
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public int revertVisitPlan(String whereField, String whereValue,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyUnit=:lastModifyUnit,")
				.append(" lastModifyDate=:lastModifyDate")
				.append(" where planStatus=:planStatus1 ").append(" and ")
				.append(whereField).append(" = :whereValue");
		Map<String, Object> map = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				map.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		map.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		map.put("lastModifyDate", new Date());
		map.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		map.put("planStatus", PlanStatus.NEED_VISIT);
		map.put("planStatus1", PlanStatus.WRITEOFF);
		map.put("whereValue", whereValue);
		try {
			return dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销计划信息失败!", e);
		}
	}

	/**
	 * 档案恢复时将恢复档案之前的随访计划置为未访
	 * 
	 * @param whereField
	 * @param whereValue
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int setPlanNotVisit(String whereField, String whereValue,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyUnit=:lastModifyUnit,")
				.append(" lastModifyDate=:lastModifyDate")
				.append(" where planStatus=:planStatus1 ")
				.append(" and endDate < :curDate ").append(" and ")
				.append(whereField).append(" = :whereValue");
		Map<String, Object> map = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				map.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		User user = (User) dao.getContext().get("user.instance");
		map.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		map.put("lastModifyDate", new Date());
		map.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		map.put("planStatus", PlanStatus.NOT_VISIT);
		map.put("planStatus1", PlanStatus.WRITEOFF);
		map.put("curDate", new Date());
		map.put("whereValue", whereValue);
		try {
			return dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新计划信息失败!", e);
		}
	}

	/**
	 * 变更随访计划日期
	 * 
	 * @param empiId
	 * @param dateBt
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public void changePlanDate(String empiId, int dateBt,
			String... businessType) throws ModelDataOperationException {
		User user = (User) dao.getContext().get("user.instance");
		StringBuilder sb = new StringBuilder(" update ")
				.append("PUB_VisitPlan")
				.append(" set beginDate = sum_day(beginDate+ ").append(dateBt)
				.append(") ,endDate = sum_day(endDate+ ").append(dateBt)
				.append(") ,beginVisitDate = sum_day(beginVisitDate+ ")
				.append(dateBt).append(") ,planDate = sum_day(planDate+ ")
				.append(dateBt)
				.append(") ,lastModifyUser = :lastModifyUser  ,")
				.append(" lastModifyDate = :lastModifyDate")
				.append(" where empiId = :empiId ");
		Map<String, Object> param = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				sb.append("  and  businessType=(");
			} else {
				sb.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				param.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			sb.append(args.substring(1));
		}
		param.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		param.put("lastModifyDate", new Date());
		param.put("empiId", empiId);
		try {
			dao.doUpdate(sb.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "变更计划日期信息失败!", e);
		}
	}

	/**
	 * 恢复被终止的随访计划
	 * 
	 * @param phrId
	 * @param visitId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int revertOverVisitPlan(String phrId, String visitId,
			String businessType) throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus ,")
				.append(" visitDate = :visitDate ,")
				.append(" visitId = :visitId ,")
				.append(" lastModifyUser = :lastModifyUser ,")
				.append(" lastModifyDate = :lastModifyDate ")
				.append(" where recordId = :recordId ")
				.append(" and visitId = :visitId1 ")
				.append(" and businessType= :businessType ");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planStatus", PlanStatus.NEED_VISIT);
		map.put("visitDate", BSCHISUtil.toDate(""));
		map.put("visitId", "");
		map.put("lastModifyUser", userId);
		map.put("lastModifyDate", new Date());
		map.put("recordId", phrId);
		map.put("visitId1", visitId);
		map.put("businessType", businessType);

		try {
			return dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复被终止管理的计划失败！", e);
		}
	}

	/**
	 * 恢复精神病随访计划
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int revertPsychosisVisitPlan(String phrId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus= :planStatus ")
				.append("where recordId=:recordId and planStatus=:planStatus1")
				.append(" and businessType=:businessType ")
				.append(" and endDate>=:currentDate ");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", String.valueOf(PlanStatus.NEED_VISIT));
		parameters.put("recordId", phrId);
		parameters.put("planStatus1", String.valueOf(PlanStatus.WRITEOFF));
		parameters.put("businessType", String.valueOf(BusinessType.PSYCHOSIS));
		parameters.put("currentDate", new Date());

		int updateRecordNum = 0;
		try {
			updateRecordNum = dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复随访计划失败！", e);
		}
		return updateRecordNum;
	}

	/**
	 * 将某条计划状态改为未访
	 * 
	 * @param planId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int updateVisitPlanNotVisit(String planId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus= :planStatus, ")
				.append(" lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where planId = :planId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", PlanStatus.NOT_VISIT);
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("planId", planId);
		try {
			return dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改随访计划状态失败！", e);
		}
	}

	/**
	 * 将以前未做随访计划置为失访
	 * 
	 * @param empiId
	 * @param businessType
	 * @param planDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int updateVisitPlanLostVisit(String empiId, String businessType,
			Date planDate) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where empiId = :empiId and businessType = :businessType and ")
				.append("planStatus = :planStatus0 and planDate < str(cast(:planDate as date))")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("planStatus", PlanStatus.LOST);
		parameters.put("empiId", empiId);
		parameters.put("businessType", businessType);
		parameters.put("planDate", planDate);
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("planStatus0", PlanStatus.NEED_VISIT);
		int updateRecordNum = 0;
		try {
			updateRecordNum = dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "将以前未做随访计划置为失访失败！", e);
		}
		return updateRecordNum;
	}

	/**
	 * 获取随访计划记录[planId 降序]
	 * 
	 * @param phrId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getVisitPlanList(String phrId,
			String businessType) throws ModelDataOperationException {
		String pvphql = new StringBuffer(" from ")
				.append("PUB_VisitPlan")
				.append(" where businessType = :businessType and recordId = :phrId")
				.append(" order by planId desc ").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phrId", phrId);
		paramMap.put("businessType", businessType);
		List<Map<String, Object>> vprList = null;
		try {
			vprList = dao.doQuery(pvphql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划记录失败！", e);
		}
		return vprList;
	}

	/**
	 * 检查是否存在随访计划
	 * 
	 * @param empiId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean checkHasVisitPlan(String empiId, String businessType)
			throws ModelDataOperationException {
		String pvphql = new StringBuffer(" from ")
				.append("PUB_VisitPlan")
				.append(" where businessType = :businessType and empiId = :empiId")
				.append(" order by planId desc ").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		paramMap.put("businessType", businessType);
		List<Map<String, Object>> vprList = null;
		try {
			vprList = dao.doQuery(pvphql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访计划记录失败！", e);
		}
		if (vprList != null && vprList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void updateLastVisitedPlanStatus(Map<String, Object> visitPlan,
			String newPlanStatus, String oldPlanStatus)
			throws ModelDataOperationException {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(
				"select max(planId) as planId from ").append("PUB_VisitPlan a")
				.append(" where ");
		if (visitPlan.get("planId") != null) {
			hql.append(" planId=:planId ");
			param.put("planId", (String) visitPlan.get("planId"));
		} else if (visitPlan.get("recordId") != null) {
			hql.append(" recordId=:recordId ");
			param.put("recordId", (String) visitPlan.get("recordId"));
		} else if (visitPlan.get("empiId") != null) {
			hql.append(" empiId=:empiId ");
			param.put("empiId", (String) visitPlan.get("empiId"));
		} else {
			hql.append(" 1=1 ");
		}
		hql.append(" and a.businessType=:businessType").append(
				" and a.planStatus=:planStatus0");
		param.put("businessType", (String) visitPlan.get("businessType"));
		param.put("planStatus0", oldPlanStatus);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql.toString(), param);
			if (list != null && list.size() > 0) {
				String planId = (String) list.get(0).get("planId");
				hql = new StringBuffer("update ")
						.append("PUB_VisitPlan")
						.append(" set planStatus=:planStatus,lastModifyDate=:lastModifyDate")
						.append(",lastModifyUser=:lastModifyUser")
						.append(" where planId=:planId");
				param = new HashMap<String, Object>();
				param.put("planId", planId);
				param.put("planStatus", newPlanStatus);
				param.put("lastModifyDate", new Date());
				param.put("lastModifyUser", UserRoleToken.getCurrent()
						.getUserId());
				dao.doUpdate(hql.toString(), param);
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					"Set status for last plan that is visit failed.", e);
		}
	}

	public void setVisitedPlanStatus(String phrId, String businessType,
			String planStatus) throws ModelDataOperationException {
		Map<String, Object> param = new HashMap<String, Object>();
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus,lastModifyDate=:lastModifyDate")
				.append(",lastModifyUser=:lastModifyUser")
				.append(" where recordId=:phrId and businessType=:businessType and visitId is not null").toString();
		param = new HashMap<String, Object>();
		param.put("phrId", phrId);
		param.put("planStatus", planStatus);
		param.put("businessType", businessType);
		param.put("lastModifyDate", new Date());
		param.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		try {
			dao.doUpdate(hql.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"set Visited Plan Status that is visit failed.", e);
		}
	}
}
