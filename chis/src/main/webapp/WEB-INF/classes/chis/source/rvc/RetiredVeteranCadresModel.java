package chis.source.rvc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.validator.ValidateException;

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

public class RetiredVeteranCadresModel implements BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(RetiredVeteranCadresModel.class);

	BaseDAO dao = null;

	public RetiredVeteranCadresModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> saveRetiredVeteranCadresRecord(String op,
			Map<String, Object> record) throws ModelDataOperationException,
			ValidateException {
		try {
			return dao.doSave(op, RVC_RetiredVeteranCadresRecord, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存离休干部档案失败。");
		}
	}

	public void updateRVCVisitStatus(String planTypeCode, String empiId,
			String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append(RVC_RetiredVeteranCadresRecord)
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
					ServiceCode.CODE_DATABASE_ERROR, "修改过期的离休干部随访计划信息失败！", e);
		}
	}

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
			parameters.put("businessType", BusinessType.RVC);
			parameters.put("sDate", sDate);
			parameters.put("eDate", eDate);
			records = dao.doQuery(hql, parameters);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询随访计划失败！", e);
		}
		return records;
	}

	public void logoutRVCRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		String userId = UserUtil.get(UserUtil.USER_ID);
		StringBuffer hql = new StringBuffer("update ")
				.append(RVC_RetiredVeteranCadresRecord)
				.append(" set status = :status, ")
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
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销离休干部档案失败！", e);
		}
	}

	public Map<String, Object> getRVCRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			return dao.doLoad(cnd, RVC_RetiredVeteranCadresRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询离休干部档案失败！");
		}
	}

	public Map<String, Object> saveRVCVisitRecord(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, RVC_RetiredVeteranCadresVisit, body, false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("老年人随访记录保存失败", e);
		}
	}

	public void revertRVCRecord(String phrId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(RVC_RetiredVeteranCadresRecord)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and status = :status1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		parameters.put("status", "" + Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", manaUnitId);
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复狂犬病档案失败", e);
		}
	}

}
