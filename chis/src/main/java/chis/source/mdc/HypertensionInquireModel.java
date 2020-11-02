/**
 * @(#)HypertensionInquireModel.java Created on 2012-1-17 上午9:58:07
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionInquireModel extends MDCBaseModel {

	public HypertensionInquireModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 依据高血压询问主键值取取询问记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getHypertensionInquireByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			Map<String, Object> data = dao
					.doLoad(MDC_HypertensionInquire, pkey);
			data = SchemaUtil.setDictionaryMessageForForm(data,
					MDC_HypertensionInquire);
			return data;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压询问信息失败！", e);
		}
	}

	/**
	 * 新增高血压询问
	 * 
	 * @param reqBody
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String addSingleInquirePlan(Map<String, Object> reqBody)
			throws ModelDataOperationException {
		String recordId = (String) reqBody.get("phrId");
		String empiId = (String) reqBody.get("empiId");
		HashMap<String, Object> rec = new HashMap<String, Object>();
		rec.put("recordId", recordId);
		rec.put("empiId", empiId);
		rec.put("businessType", BusinessType.HYPERINQUIRE);
		rec.put("sn", 0);
		String planDate = (String) reqBody.get("beginDate");
		rec.put("beginDate", planDate);
		rec.put("endDate", planDate);
		rec.put("beginVisitDate", planDate);
		rec.put("planStatus", String.valueOf(PlanStatus.NEED_VISIT));
		rec.put("planDate", planDate);
		HypertensionVisitModel hvm = new HypertensionVisitModel(dao);
		rec.put("extend2", hvm.getLastVisitDate(empiId));

		HashMap<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("body", rec);
		reqMap.put("schema", PUB_VisitPlan);
		reqMap.put("op", "create");

		try {
			Map<String, Object> genValues = dao.doSave("create", PUB_VisitPlan,
					rec, true);
			return (String) genValues.get("planId");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "增加随访计划数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "新增随访计划失败！", e);
		}
	}

	/**
	 * 更新询问<br>
	 * 更新询问（随访）状态为 已访<br>
	 * 【planStatus = 0:应访 1:已访 2:失访 3:未访 4:过访 9:档案注销】<br>
	 * 
	 * @param planId
	 *            计划ID
	 * @param inquireDate
	 *            询问日期，与随访日期同字段
	 * @param inquireId
	 *            询问ID
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int updateInquirePlan(String planId, String inquireDate,
			String inquireId) throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus, visitId=:visitId")
				.append(",visitDate=:visitDate,lastModifyUser=:lastModifyUser")
				.append(",lastModifyDate=:lastModifyDate")
				.append(" where planId=:planId").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("visitId", inquireId);
		map.put("visitDate", BSCHISUtil.toDate(inquireDate));
		map.put("planStatus", PlanStatus.VISITED);
		map.put("planId", planId);
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
	 * 重建本期的询问计划
	 * 
	 * @param empiId
	 * @param phrId
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void recreateInquirePlan(String empiId, String phrId,
			VisitPlanCreator visitPlanCreator)
			throws ModelDataOperationException {
		//
		Date visitDate = getLastVisitDate(empiId);
		// @@ 没有随访，不生成询问计划。
		if (visitDate == null) {
			return;
		}
		// @@ 取得已访的询问里的最后一条计划日期。
		Date planDate = getLastVisitedDate(empiId);
		if (planDate != null) {
			if (BSCHISUtil.dateCompare(visitDate, planDate) < 0) {
				visitDate = planDate;
			}
		}
		// @@ 查找下一次随访计划日期。
		Date nextPlanDate = getNextPlanDate(empiId);
		if (nextPlanDate == null) {
			return;
		}
		// @@ 把未访的询问计划删了。
		deleteNotVisitPlan(empiId);

		// @@ 生成询问计划
		HashMap<String, Object> jobj = new HashMap<String, Object>();
		jobj.put("businessType", BusinessType.HYPERINQUIRE);
		jobj.put("empiId", empiId);
		jobj.put("recordId", phrId);
		jobj.put("visitDate", BSCHISUtil.toString(visitDate, null));
		jobj.put("nextPlanDate", nextPlanDate);
		// jobj.put("nextPlanId", nextPlanId);

		try {
			visitPlanCreator.create(jobj, dao.getContext());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成询问计划失败！", e);
		}

		// @@ 过期的计划状态置为未访。
		setPlanStatusNotVisit(empiId);
	}

	/**
	 * 据 empiId获取最近随访日期
	 * 
	 * @param empiId
	 * @param planType
	 * @param planStatus
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastVisitDate(String empiId)
			throws ModelDataOperationException {
		String hql0 = new StringBuffer(
				"select max(visitDate) as lastVisitDate from ")
				.append("PUB_VisitPlan")
				.append(" where empiId = :empiId and businessType = :businessType")
				.append(" and planStatus <> :planStatus").toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("businessType", BusinessType.GXY);
		pam.put("planStatus", PlanStatus.NEED_VISIT);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql0, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取最近随访日期失败！", e);
		}
		if (rsMap != null) {
			return (Date) rsMap.get("lastVisitDate");
		}
		return null;
	}

	/**
	 * 取得已访的询问里的最后一条计划日期
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getLastVisitedDate(String empiId)
			throws ModelDataOperationException {
		String hql1 = new StringBuffer("select max(planDate) as planDate from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus=:planStatus").toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("businessType", BusinessType.HYPERINQUIRE);
		pam.put("planStatus", PlanStatus.VISITED);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql1, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取已访的询问最后随访日期失败！", e);
		}
		if (rsMap != null) {
			return (Date) rsMap.get("planDate");
		}
		return null;
	}

	/**
	 * 查找下一次随访计划日期
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Date getNextPlanDate(String empiId)
			throws ModelDataOperationException {
		String vhql = new StringBuffer("select min(planDate) as planDate from ")
				.append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus=:planStatus")
				.append(" and str(planDate, 'yyyy-mm-dd')>=:planDate")
				.toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("businessType", BusinessType.GXY);
		pam.put("planStatus", PlanStatus.NEED_VISIT);
		pam.put("planDate", BSCHISUtil.toString(new Date(), null));
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(vhql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取下一次随访计划日期失败！", e);
		}
		if (rsMap != null) {
			return (Date) rsMap.get("planDate");
		}
		return null;
	}

	/**
	 * 删除未访的询问计划
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void deleteNotVisitPlan(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus in(:planStatus0,:planStatus)")
				.toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("empiId", empiId);
		pam.put("businessType", BusinessType.HYPERINQUIRE);
		pam.put("planStatus0", PlanStatus.NOT_VISIT);
		pam.put("planStatus", PlanStatus.NEED_VISIT);
		try {
			dao.doUpdate(hql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除未访的询问计划失败！", e);
		}
	}

	/**
	 * 过期的计划状态置为未访
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public int setPlanStatusNotVisit(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus0")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus=:planStatus")
				.append(" and endDate<:endDate").toString();
		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("planStatus0", PlanStatus.NOT_VISIT);
		pam.put("empiId", empiId);
		pam.put("businessType", BusinessType.HYPERINQUIRE);
		pam.put("planStatus", PlanStatus.NEED_VISIT);
		pam.put("endDate", new Date());
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, pam);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "过期的计划状态置为未访失败！", e);
		}
		return rsInt;
	}

	/**
	 * 更新过期随访计划
	 * 
	 * @param empiId
	 * @param beginDate
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int updateOverDataPlan(String empiId, String beginDate)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus=:planStatus,lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planStatus=:planStatus0 and beginDate<:beginDate")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planStatus", PlanStatus.NOT_VISIT);
		parameters.put("businessType", BusinessType.HYPERINQUIRE);
		parameters.put("empiId", empiId);
		parameters.put("planStatus0", PlanStatus.NEED_VISIT);
		parameters.put("beginDate", BSCHISUtil.toDate(beginDate));
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！", e);
		}
		return rsInt;
	}

	/**
	 * 关闭随访
	 * 
	 * @param empiId
	 * @param beginDate
	 * @throws ModelDataOperationException
	 */
	public int closeVisit(String empiId, String beginDate)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and beginDate>:beginDate and planStatus=:planStatus")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("businessType", BusinessType.GXY);
		parameters.put("beginDate", BSCHISUtil.toDate(beginDate));
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "关闭随访失败！", e);
		}
		return rsInt;
	}

	/**
	 * 关闭询问
	 * 
	 * @param empiId
	 * @param planId
	 * @throws ModelDataOperationException
	 */
	public int closeInquire(String empiId, String planId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append("PUB_VisitPlan")
				.append(" where empiId=:empiId and businessType=:businessType")
				.append(" and planId>:planId and planStatus=:planStatus")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("businessType", BusinessType.HYPERINQUIRE);
		parameters.put("planId", planId);
		parameters.put("planStatus", PlanStatus.NEED_VISIT);
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "关闭询问失败！", e);
		}
		return rsInt;
	}

	/**
	 * 获取上次询问记录的血压值
	 * 
	 * @param empiId
	 * @param inquireDate
	 *            询问日期
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastHyperInquireRecord(String empiId,
			String inquireDate) throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select constriction as constriction,diastolic as diastolic from ")
				.append(MDC_HypertensionInquire)
				.append(" where empiId = :empiId and inquireDate < :inquireDate")
				.append(" order by inquireDate desc").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("inquireDate", BSCHISUtil.toDate(inquireDate));
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取上次询问记录的血压值失败！", e);
		}
		if(rsList != null && rsList.size() >0){
			return rsList.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 查询当天随访记录
	 * 
	 * @param phrId
	 * @param inquireDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long CheckHasCurInquireRecord(String phrId, String inquireDate)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as ct from ")
				.append(MDC_HypertensionInquire)
				.append(" where inquireDate=:inquireDate and phrId=:phrId ")
				.toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("inquireDate", BSCHISUtil.toDate(inquireDate));
		paramMap.put("phrId", phrId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "当天随访记录失败！", e);
		}
		return rsMap == null ? 0 : (Long) rsMap.get("ct");
	}

	/**
	 * 保存高血压询问记录
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionInquireInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_HypertensionInquire, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压询问记录数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压询问记录失败！", e);
		}
		return rsMap;
	}
}
