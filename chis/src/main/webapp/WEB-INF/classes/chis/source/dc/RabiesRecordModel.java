/**
 * @(#)RabiesRecordModel.java Created on 2012-4-19 下午02:27:48
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.dc;

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

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class RabiesRecordModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public RabiesRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 检查是否有正常的狂犬病档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> whetherNeedRabiesRecord(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select rabiesId as rabiesId,empiId as empiId from ")
				.append(DC_RabiesRecord)
				.append(" where empiId=:empiId and status='0' and closeFlag='0'")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查是否有正常的狂犬病档案失败", e);
		}
		return map;
	}

	/**
	 * 注销狂犬病档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logoutRabiesRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(DC_RabiesRecord)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' or cancellationReason is null)")
				.append(" and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
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

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销狂犬病档案失败", e);
		}

	}

	/**
	 * 恢复狂犬病档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void revertRabiesRecord(String phrId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(DC_RabiesRecord)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and status = :status1 ")
				.append(" and (cancellationReason<>'6' or cancellationReason is null)");

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

	/**
	 * 狂犬病档案结案
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	protected void logoutRabies(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(DC_RabiesRecord)
				.append(" set closeFlag = :closeFlag, ")
				.append(" closedDate = :closedDate, ")
				.append(" closedDoctor = :closedDoctor, ")
				.append(" closedUnit = :closedUnit,")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit ")
				.append(" where rabiesId = :rabiesId").toString();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("closeFlag", Constants.CODE_CLOSEFLAG_YES);
		parameters.put("closedDate", new Date());
		parameters.put("closedDoctor", userId);
		parameters.put("closedUnit", manaUnitId);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", manaUnitId);
		parameters.put("rabiesId", recordId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "狂犬病档案结案失败", e);
		}
	}

	/**
	 * 获取狂犬病档案信息
	 * 
	 * @param schema
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadRabiesRecord(String schema, String pkey)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doLoad(schema, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取狂犬病档案信息失败", e);
		}
		return body;
	}

	/**
	 * 保存狂犬病档案信息
	 * 
	 * @param schema
	 * @param reqBody
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveRabiesRecord(String schema,
			Map<String, Object> reqBody, String op)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "验证狂犬病档案数据失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存狂犬病档案信息失败", e);
		}
		return body;
	}

	/**
	 * 保存接种记录信息
	 * 
	 * @param op
	 * @param reqBody
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveVaccination(String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, DC_Vaccination, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "验证接种记录数据失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存接种记录信息失败", e);
		}
		return body;
	}

	/**
	 * 注销当前狂犬病档案记录
	 * 
	 * @param empiId
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logoutRabiesRecordThis(String empiId,
			String cancellationReason, String rabiesId, String deadReason)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append(DC_RabiesRecord)
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where empiId = :empiId and (cancellationReason<>'6' or cancellationReason is null)")
				.append(" and rabiesId = :rabiesId")
				.append(" and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		String userId = UserUtil.get(UserUtil.USER_ID);
		String manaUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		parameters.put("status", "" + Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", manaUnitId);
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", manaUnitId);
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadReason", deadReason);
		parameters.put("empiId", empiId);
		parameters.put("rabiesId", rabiesId);
		parameters.put("normal", "" + Constants.CODE_STATUS_NORMAL);

		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销狂犬病档案失败", e);
		}

	}

	public List<Map<String, Object>> getRabiesRecordList(String schema, List<?> cnd)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "rabiesId", schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取狂犬病档案列表失败", e);
		}
		return list;
	}

	/**
	 * 通过empiId查询狂犬病档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getRabiesRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			return dao.doLoad(cnd, DC_RabiesRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取狂犬病档案信息失败", e);
		}
	}

	public List<Map<String, Object>> checkHasNotLogOut(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select rabiesId as rabiesId,empiId as empiId from ")
				.append(DC_RabiesRecord)
				.append(" where empiId=:empiId and status='0'").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		List<Map<String, Object>> map = null;
		try {
			map = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查是否有不注销的狂犬病档案失败", e);
		}
		return map;
	}

	public Map<String, Object> getPhrStatus(String empiId) throws ModelDataOperationException {
		Map<String, Object> map = new HashMap<String, Object>();

		String hql = new StringBuffer(
				"select status as status,empiId as empiId from ")
				.append(EHR_HealthRecord)
				.append(" where empiId=:empiId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			map = dao.doLoad(hql, parameters);
			if(map.get("status")!=null&&!map.get("status").equals("0")){
				map.put("create", false);
				map.put("update", false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "检查是否有不注销的狂犬病档案失败", e);
		}
		return map;
	}
}
