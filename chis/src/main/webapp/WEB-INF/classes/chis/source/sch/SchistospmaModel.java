/**
 * @(#)SchistospmaService.java Created on 2012-5-15 下午03:11:37
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.sch;

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
import ctd.account.user.User;
import ctd.service.core.ServiceException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class SchistospmaModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public SchistospmaModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 保存血吸虫病档案
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSchistospmaRecord(String op,
			Map<String, Object> body) throws ServiceException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, SCH_SchistospmaRecord, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存血吸虫病档案失败！", e);
		}
	}

	/**
	 * 结案
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	public void closeSchistospmaRecord(String schisRecordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer().append("update ")
				.append("SCH_SchistospmaRecord").append(" set ")
				.append("closeFlag = :closeFlag,")
				.append("closedDoctor = :closedDoctor,")
				.append("closedUnit = :closedUnit,")
				.append("closedDate = :closedDate,")
				.append("lastModifyUser = :lastModifyUser,")
				.append("lastModifyDate = :lastModifyDate ")
				.append("where schisRecordId = :schisRecordId").toString();
		String userId =  UserRoleToken.getCurrent().getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("closeFlag", Constants.CODE_CLOSEFLAG_YES);
		parameters.put("schisRecordId", schisRecordId);
		parameters.put("closedDoctor", userId);
		parameters.put("closedUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("closedDate", new Date());
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("结案失败！", e);
		}
	}

	/**
	 * 根据 empiId 查询未结案档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> notCloseRecord(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper
					.createSimpleCnd("eq", "a.status", "s", "0");
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "a.closeFlag", "s",
					"0");
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			cnd = CNDHelper.createArrayCnd("and", cnd, cnd3);
			return dao.doList(cnd, null, SCH_SchistospmaRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据 empiId 查询未结案档案失败！", e);
		}
	}

	/**
	 * 根据 empiId 查询是否已有结案的档案
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> wirteOffrecord(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper
					.createSimpleCnd("eq", "a.status", "s", "1");
			List<?> cnd3 = CNDHelper.createSimpleCnd("ne",
					"a.cancellationReason", "s", "6");
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			cnd = CNDHelper.createArrayCnd("and", cnd, cnd3);
			return dao.doList(cnd, null, SCH_SchistospmaRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据 empiId 查询是否已有结案的档案失败！", e);
		}
	}

	/**
	 * 根据 phrId 注销该用户的所有血吸虫档案或单条血吸虫档案
	 * 
	 * @param value
	 * @param cancellationReason
	 * @param deadReason
	 * @param logOutAll
	 * @throws ModelDataOperationException
	 */
	public void logoutSchistospmaRecord(String value,
			String cancellationReason, String deadReason, boolean logOutAll)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("SCH_SchistospmaRecord")
				.append(" set cancellationUnit = :cancellationUnit,")
				.append("cancellationUser = :cancellationUser,")
				.append("cancellationDate = :cancellationDate,")
				.append("cancellationReason = :cancellationReason,")
				.append("lastModifyUser = :lastModifyUser,")
				.append("lastModifyDate = :lastModifyDate,")
				.append("status = :status");
		String userId =  UserRoleToken.getCurrent().getUserId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cancellationUnit",
				UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		if (logOutAll) {
			hql.append(",deadReason = :deadReason")
					.append(" where phrId = :phrId")
					.append(" and (cancellationReason<>'6' or cancellationReason is null)")
					.append(" and status = :status2");
			parameters.put("deadReason", deadReason);
			parameters.put("phrId", value);
			parameters.put("status2", Constants.CODE_STATUS_NORMAL);
		} else {
			hql.append(" where schisRecordId = :schisRecordId");
			parameters.put("schisRecordId", value);
		}
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"根据 phrId 注销该用户的所有血吸虫档案或单条血吸虫档案失败！", e);
		}
	}

	/**
	 * 恢复所有血吸虫档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertrevertSchistospmaRecord(String phrId)
			throws ModelDataOperationException {
		String userId =  UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("SCH_SchistospmaRecord")
				.append(" set cancellationUnit = :cancellationUnit,")
				.append("cancellationUser = :cancellationUser,")
				.append("cancellationDate = :cancellationDate,")
				.append("cancellationReason = :cancellationReason,")
				.append("lastModifyUser = :lastModifyUser,")
				.append("lastModifyDate = :lastModifyDate,")
				.append("status = :status")
				.append(" where phrId = :phrId")
				.append(" and status = :status1")
				.append(" and (cancellationReason<>'6' or cancellationReason is null)")
				.toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationReason", "");
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("恢复血吸虫档案失败！", e);
		}
	}

	/**
	 * 保存血吸虫病随访记录
	 * 
	 * @param op
	 * @param body
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public void saveSchistospmaVisitInfo(String op, Map<String, Object> body)
			throws ServiceException, ModelDataOperationException {
		try {
			dao.doSave(op, SCH_SchistospmaVisit, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存血吸虫病随访记录失败！", e);
		}
	}
	
	
	/**
	 * 通过empiId查询血吸虫病档案记录
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getSchistospmaRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "a.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			return dao.doLoad(cnd, SCH_SchistospmaRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取血吸虫病档案信息失败", e);
		}
	}

}
