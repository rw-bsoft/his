/**
 * @(#)EHRMoveModule.java Created on 2012-5-22 上午9:37:14
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mov;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.dic.AffirmType;
import chis.source.dic.ArchiveType;
import chis.source.dic.RecordStatus;
import chis.source.dic.YesNo;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class EHRMoveModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public EHRMoveModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * EHR 迁移申请 保存
	 * 
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveEHRMoveApply(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		String msg = "";
		String archiveType = (String) recordMap.get("archiveType");
		if (ArchiveType.HEALTH_ARCHIVE.equals(archiveType)) {
			msg = "个人健康";
		}
		if (ArchiveType.FAMILY_ARCHIVE.equals(archiveType)) {
			msg = "家庭";
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MOV_EHRApply, recordMap, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, msg + "档案迁移申请数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, msg + "档案迁移申请失败！", e);
		}
		return rsMap;
	}

	/**
	 * 保存迁移确认数据
	 * 
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveEHRMoveConfirm(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		String msg = "";
		String archiveType = (String) recordMap.get("archiveType");
		if (ArchiveType.HEALTH_ARCHIVE.equals(archiveType)) {
			msg = "个人健康";
		}
		if (ArchiveType.FAMILY_ARCHIVE.equals(archiveType)) {
			msg = "家庭";
		}
		String affirmType = (String) recordMap.get("affirmType");
		String atMsg = "确认";
		if (AffirmType.CANCEL.equals(affirmType)) {
			atMsg = "退回";
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MOV_EHRConfirm, recordMap, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, msg + "档案迁移" + atMsg
							+ "数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR,
					msg + "档案迁移" + atMsg + "失败！", e);
		}
		return rsMap;
	}

	public boolean isCanelEHR(String phrId) throws ModelDataOperationException {
		String hqlString = new StringBuffer("select status as status from ")
				.append(EHR_HealthRecord).append(" where phrId = :phrId")
				.toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("phrId", phrId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlString, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人健康档案失败！", e);
		}
		if(rsMap != null && rsMap.size() >0){
			String status = (String) rsMap.get("status");
			if(RecordStatus.CANCEL.equals(status)){
				return true;
			}else {
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * 判断所迁移个档该人是否为户主
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean isHeadOfAHousehold(String phrId)
			throws ModelDataOperationException {
		StringBuffer qmasterSql = new StringBuffer(
				"select  masterFlag as masterFlag from ").append(
				EHR_HealthRecord).append(" where phrId=:phrId ");
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("phrId", phrId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(qmasterSql.toString(), paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取档案户主标识失败！", e);
		}
		String masterFlag = "n";
		if (rsMap != null && rsMap.size() > 0) {
			masterFlag = (String) rsMap.get("masterFlag");
		}
		if (masterFlag == null) {
			return false;
		}
		return masterFlag.equals(YesNo.YES) ? true : false;
	}

	/**
	 * 清楚成员与户主关系
	 * 
	 * @param regionCode
	 * @throws ModelDataOperationException
	 */
	public void clearRelaCode(String regionCode)
			throws ModelDataOperationException {
		StringBuffer clearRelationSql = new StringBuffer("update ").append(
				EHR_HealthRecord).append(
				" set relaCode='' where regionCode =:regionCode");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", regionCode);
		try {
			dao.doUpdate(clearRelationSql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "清楚成员与户主关系失败！", e);
		}
	}

	/**
	 * 清除原家庭档案户主姓名
	 * 
	 * @param regionCode
	 * @throws ModelDataOperationException
	 */
	public void clearMasterName(String regionCode)
			throws ModelDataOperationException {
		StringBuffer clearMasterNameSql = new StringBuffer("update ")
				.append(EHR_FamilyRecord)
				.append(" set ownerName= '', lastModifyUser=:lastModifyUser,lastModifyDate=:lastModifyDate")
				.append(" where regionCode =:regionCode");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", regionCode);
		parameters.put("lastModifyUser",
				UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(clearMasterNameSql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "清除原家庭档案户主姓名失败！", e);
		}
	}

	/**
	 * 
	 * @param targetArea
	 * @param targetArea_text
	 * @param targetUnit
	 * @param targetDoctor
	 * @param archiveID
	 * @throws ModelDataOperationException
	 */
	public void moveHealthRcord(String targetArea, String targetArea_text,
			String targetUnit, String targetDoctor, String familyId,
			String archiveID) throws ModelDataOperationException {
		StringBuilder sb = new StringBuilder("update ")
				.append(EHR_HealthRecord).append(" set ");
		sb.append(" regionCode = :regionCode ,regionCode_text = :regionCode_text,masterFlag = :masterFlag ,");
		sb.append(" relaCode = :relaCode , manaUnitId = :manaUnitId ,");
		sb.append(" lastModifyUser = :lastModifyUser , lastModifyDate = :lastModifyDate ,");
		sb.append(" manaDoctorId = :manaDoctorId ,familyId = :familyId where phrId = :phrId");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("regionCode", targetArea);
		parameters.put("regionCode_text", targetArea_text);
		parameters.put("masterFlag", "");
		parameters.put("relaCode", "");
		parameters.put("manaUnitId", targetUnit);
		parameters.put("manaDoctorId", targetDoctor);
		parameters.put("familyId", familyId);
		parameters.put("phrId", archiveID);
		parameters.put("lastModifyUser",
				UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());

		try {
			dao.doUpdate(sb.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "个人健康档案迁移失败！", e);
		}
	}

	/**
	 * 查询子档记录
	 * 
	 * @param entryName
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getSubRecordList(String entryName,
			String phrId) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, null, entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取子档记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 删除子档记录
	 * 
	 * @param entryName
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void delSubRecord(String entryName, String phrId)
			throws ModelDataOperationException {
		StringBuffer delSql = new StringBuffer("delete from ")
				.append(entryName).append(" where phrId = :phrId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(delSql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除子档记录失败！", e);
		}
	}

	/**
	 * 保存子档数据
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSubRecord(String op, String entryName,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "子档数据保存失败！", e);
		}
		return rsMap;
	}

	/**
	 * 把原来的家庭问题归到新的家庭中去
	 * 
	 * @param targetFamilyId
	 * @param familyId
	 * @throws ModelDataOperationException
	 */
	public void mergerFamilyToNew(String targetFamilyId, String familyId)
			throws ModelDataOperationException {
		StringBuffer sbHql = new StringBuffer("update from ").append(
				EHR_FamilyProblem).append(
				" set familyId=:targetFamilyId where familyId=:familyId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("targetFamilyId", targetFamilyId);
		parameters.put("familyId", familyId);
		try {
			dao.doUpdate(sbHql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "把原来的家庭问题归到新的家庭中去失败！", e);
		}
	}

	/**
	 * 执行修改操作
	 * 
	 * @param tableName
	 * @param parameters
	 * @throws ModelDataOperationException
	 */
	public void executeUpdateSQL(String tableName,
			Map<String, Object> parameters) throws ModelDataOperationException {
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(tableName);
		List<String> sqlList = BizColumnConfig.getUpdateSql(
				BizColumnConfig.MOV_EHR, parameters, tableNames);
		try {
			dao.doUpdate(sqlList.get(0), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
	}

	/**
	 * 查询所有成员的PHRID
	 * 
	 * @param familyId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFamilyMember(String familyId)
			throws ModelDataOperationException {
		String ql = new StringBuffer("select phrId as phrId from ")
				.append(EHR_HealthRecord).append(" where familyId =:familyId")
				.toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("familyId", familyId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(ql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取家庭成员的档案号！", e);
		}
		return rsList;
	}

	/**
	 * 删除个人健康档案及家庭档案申请或作废记录
	 * 
	 * @param archiveMoveId
	 * @throws ModelDataOperationException
	 */
	public void delEHRMoveRecord(String archiveMoveId)
			throws ModelDataOperationException {
		try {
			dao.doRemove(archiveMoveId, MOV_EHRApply);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除档案迁移记录失败！", e);
		}
	}

	/**
	 * 获取迁移档案记录
	 * 
	 * @param entryName
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMoveEHR(String entryName, String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(entryName, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取迁移档案记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 据empiId获取个人主要问题
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPersonProblemByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hqlString = new StringBuffer(" from ").append(EHR_PersonProblem)
				.append(" where empiId = :empiId").toString();
		List<Map<String, Object>> rsList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			rsList = dao.doQuery(hqlString, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人主要问题失败！", e);
		}
		return rsList;
	}

	public int moveFamilyProblem(String familyProblemId, String newFamilyId)
			throws ModelDataOperationException {
		String hqlString = new StringBuffer("update ")
				.append(EHR_FamilyProblem).append(" set familyId = :familyId ")
				.append(" where familyProblemId = :familyProblemId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("familyId", newFamilyId);
		parameters.put("familyProblemId", familyProblemId);
		int ur = 0;
		try {
			ur = dao.doUpdate(hqlString, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "迁移家庭问题时失败！", e);
		}
		return ur;
	}
	
	/**
	 * 获取管辖机构
	 * 
	 * @param jgid
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getManageUnit(String jgid)
			throws ModelDataOperationException {
		String sql="select t.organizname as ORGANIZNAME from sys_organization t where t.organizcode="+jgid;
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSqlLoad(sql, null);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rsMap;
	}
}
