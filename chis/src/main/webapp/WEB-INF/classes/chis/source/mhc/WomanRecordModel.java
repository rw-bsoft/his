/**
 * @(#)WomanRecordModel.java Created on 2012-8-15 上午11:15:28
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mhc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.RecordStatus;
import chis.source.phr.HealthRecordModel;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class WomanRecordModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public WomanRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 注销妇女基本信息失败
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void logOutWomanDemographicInfo(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MPI_DemographicInfo)
				.append(" set status = :status where empiId = :empiId")
				.toString();
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("status", RecordStatus.CANCEL);
		param.put("empiId", empiId);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销妇女基本信息失败!", e);
		}

	}

	/**
	 * 获取妇女档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWomanRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		try {
			return dao.doLoad(cnd, MHC_WomanRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女档案信息失败!", e);
		}
	}

	/**
	 * 获取妇女档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWomanBaseInfoByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			Map<String, Object> res = dao.doLoad(MPI_DemographicInfo, empiId);
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女档案信息失败!", e);
		}
	}

	/**
	 * 获取妇女档案信息
	 * 
	 * @param womanRecordId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getWomanRecord(String womanRecordId)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(MHC_WomanRecord, womanRecordId);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取妇女档案信息失败!", e);
		}
	}

	/**
	 * 保存妇女档案信息
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveWomanRecord(Map<String, Object> data, String op,
			Map<String, Object> res) throws ValidateException,
			ModelDataOperationException {
		try {
			String mdId =  (String)new HealthRecordModel(dao).getHealthRecordByEmpiId(data.get("empiId").toString()).get("manaDoctorId");
			data.put("manaDoctorId", mdId);
			data.put("mhcDoctorId", mdId);
			data.put("vaginalDelivery", data.get("vaginaDeliver"));
			Map<String, Object> genValues = dao.doSave(op, MHC_WomanRecord,
					data, true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存妇女档案信息失败!", e);
		}
	}

	/**
	 * 注销妇女档案信息
	 * 
	 * @param whereField
	 * @param whereValue
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logOutWomanRecord(String whereField, String whereValue,
			String cancellationReason, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(MHC_WomanRecord).append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ").append(" where ")
				.append(whereField).append(" = :whereValue")
				.append(" and  status = :normal");

		String userId = UserUtil.get(UserUtil.USER_ID);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销档案信息失败!", e);
		}
	}

}
