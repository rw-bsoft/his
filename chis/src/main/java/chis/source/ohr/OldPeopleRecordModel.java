/**
 * @(#)OldPeopleRecordModel.java Created on 2012-3-2 下午03:29:25
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.ManageYearUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class OldPeopleRecordModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public OldPeopleRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存老年人档案
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveOldPeopleRecord(String op,
			Map<String, Object> body, boolean volidate)
			throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.MDC_OldPeopleRecord, body,
					volidate);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("老年人档案保存失败", e);
		}
	}

	/**
	 * 查询老年人随访计划
	 * 
	 * @param datumn
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCurrentYearVisitPlan(Date datumn,
			String empiId) throws ModelDataOperationException {
		ManageYearUtil util = new ManageYearUtil(datumn);
		List<Map<String, Object>> records = null;
		try {
			Date sDate = util.getOldPeopleCurYearStartDate();
			Date eDate = util.getOldPeopleCurYearEndDate();
			String hql = new StringBuilder(
					"select planId as planId, recordId as recordId, ")
					.append("empiId as empiId, beginDate as beginDate, ")
					.append("endDate as endDate, planStatus as planStatus, ")
					.append("visitDate as visitDate, visitId as visitId, planDate")
					.append(" as planDate, beginVisitDate as beginVisitDate,")
					.append(" lastModifyDate as lastModifyDate,lastModifyUser  as lastModifyUser")
					.append(" from ")
					.append(PUB_VisitPlan)
					.append(" where empiId = :empiId and businessType = :businessType")
					.append(" and planDate >= :sDate and planDate <= :eDate")
					.append(" order by planDate").toString();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("empiId", empiId);
			parameters.put("businessType", BusinessType.LNR);
			parameters.put("sDate", sDate);
			parameters.put("eDate", eDate);
			records = dao.doQuery(hql, parameters);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询随访计划失败！", e);
		}
		return records;
	}

	/**
	 * 更新老年人随访计划
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateOldPeopleVisitStatus(String planTypeCode, String empiId,
			String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MDC_OldPeopleRecord")
				.append(" set planTypeCode = :planTypeCode,")
				.append(" lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("planTypeCode", planTypeCode);
		parameters.put("empiId", empiId);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改过期的老年人随访计划信息失败！", e);
		}
	}

	/**
	 * 删除老年人体检列表
	 * 
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteCheckupList(String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MDC_OldPeopleCheckup)
				.append(" where recordId = :visitId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitId", visitId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除老年人随访体检列表失败！", e);
		}
	}

	/**
	 * 删除老年人中医辨体描述
	 * 
	 * @param visitId
	 * @throws ModelDataOperationException
	 */
	public void deleteDescription(String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(MDC_OldPelpleDescription)
				.append(" where visitId = :visitId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitId", visitId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除老年人中医辨体描述失败！", e);
		}
	}

	/**
	 * 保存老年人体检列表
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected Map<String, Object> saveOldPeopleCheckup(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.MDC_OldPeopleCheckup, body,
					false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("老年人体检保存失败", e);
		}
	}

	/**
	 * 保存老年人中医辨体描述
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected Map<String, Object> saveOldPeopleDescription(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.MDC_OldPelpleDescription,
					body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("老年人中医辨体描述保存失败", e);
		}
	}

	/**
	 * 注销老年人档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutOldPeopleRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		String userId = UserUtil.get(UserUtil.USER_ID);
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_OldPeopleRecord").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销老年人档案失败！", e);
		}
	}

	/**
	 * 恢复老年人档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertOldPeopleRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_OldPeopleRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复老年人档案失败！", e);
		}
	}

	/**
	 * 根据empiId查询老年人数据
	 * 
	 * @param empiId
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getOldPeopleRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			return dao.doLoad(cnd, MDC_OldPeopleRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询老年人档案失败！");
		}
	}

	public Map<String, Object> getOldPeopleRecordByEmpiIdnostatus(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);

			return dao.doLoad(cnd, MDC_OldPeopleRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询老年人档案失败！");
		}
	}
	/**
	 * 根据empiId查询老年人数据
	 * 
	 * @param empiId
	 * @param regionCode
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getLastOldPeopleVisitedData(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select max(visitId) as visitId from ")
				.append(PUB_VisitPlan)
				.append(" where empiId=:empiId and businessType=:businessType ")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("businessType", BusinessType.LNR);
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
}
