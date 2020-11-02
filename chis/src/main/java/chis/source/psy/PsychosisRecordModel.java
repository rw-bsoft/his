/**
 * @(#)PsychosisRecordModel.java Created on 2012-3-22 上午10:25:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.psy;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsychosisRecordModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public PsychosisRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 精神病档案管理主页面分页查询
	 * 
	 * @param cnd
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param queryCndsType
	 *            权限过虑数据
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> findPsyRecordPageList(List<?> cnd, int pageNo,
			int pageSize, String orderBy, String queryCndsType)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doList(cnd, orderBy, PSY_PsychosisRecord, pageNo,
					pageSize, queryCndsType);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询精神病档案失败！请重试", e);
		}
		return rsMap;
	}

	/**
	 * 根据档案号取出精神病信息 ,
	 * 
	 * @param phrId
	 * @param ifSetDicInfo
	 *            当参数ifSetDicInfo设置为true时做字典转换处理（表单格式）
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPsyRecordByPhrId(String phrId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);

		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(cnd, PSY_PsychosisRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取精神病档案信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 保存精神病档案信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePsyRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PSY_PsychosisRecord, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存精神病档案时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存精神病档案失败！", e);
		}
		return rsMap;
	}

	/**
	 * 注销精神病档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int logoutPsychosisRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("PSY_PsychosisRecord").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId")
				.append(" and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil
				.get(UserUtil.MANAUNIT_ID);

		parameters.put("status", "" + Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", manaUnitId);
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", manaUnitId);
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", phrId);
		parameters.put("normal", "" + Constants.CODE_STATUS_NORMAL);

		int updateRcordNum = 0;
		try {
			updateRcordNum = dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销精神病档案失败！", e);
		}
		return updateRcordNum;
	}

	/**
	 * 恢复精神病档案记录的注销状态
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void revertPsychosisRecord(String phrId)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("PSY_PsychosisRecord")
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复精神病档案失败！", e);
		}
	}

	/**
	 * 通过empiId 查询精神病档案
	 * 
	 * @param empiId
	 * @param needDictionary
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getPsychosisRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			return dao.doLoad(cnd, PSY_PsychosisRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询精神病档案失败！");
		}
	}
}
