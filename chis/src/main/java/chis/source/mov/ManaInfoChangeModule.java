/**
 * @(#)ManaInfoChangeModule.java Created on 2012-5-24 下午4:50:35
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mov;

import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.dic.YesNo;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.HQLHelper;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ManaInfoChangeModule implements BSCHISEntryNames {

	private BaseDAO dao;

	public ManaInfoChangeModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取用户档案信息
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPeopleRecords(String entryName,
			String empiId, String manageUnit)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("like", "manaUnitId", "s",
				manageUnit + "%");
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		if (entryName.equals(DEF_IntellectDeformityRecord)
				|| entryName.equals(DEF_BrainDeformityRecord)
				|| entryName.equals(DEF_LimbDeformityRecord)) {
			List<?> statusCnd = CNDHelper.createSimpleCnd("eq", "status", "s",
					Constants.CODE_STATUS_NORMAL);
			List<?> closeCnd = CNDHelper.createSimpleCnd("eq", "closeFlag",
					"s", YesNo.NO);
			List<?> otherCnd = CNDHelper.createArrayCnd("and", statusCnd,
					closeCnd);
			cnd = CNDHelper.createArrayCnd("and", cnd, otherCnd);
		}
		try {
			return dao.doQuery(cnd, null, entryName);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取用户档案信息失败", e);
		}
	}

	/**
	 * 保存修改管理医生记录
	 * 
	 * @param data
	 * @param op
	 * @param scheamName
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveMoveRecord(Map<String, Object> data, String op,
			String scheamName, Map<String, Object> res)
			throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, scheamName, data,
					true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存修改管理医生记录失败", e);
		}
	}

	/**
	 * 保存修改管理医生明细记录
	 * 
	 * @param data
	 * @param op
	 * @param scheamName
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveMoveRecordDetail(Map<String, Object> data, String op)
			throws ValidateException, ModelDataOperationException {
		try {
			dao.doSave(op, MOV_ManaInfoChangeDetail, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存修改管理医生明细记录失败", e);
		}
	}

	/**
	 * 获取修改管理医生记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "archiveMoveId", "s",
					pkey);
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MOV_ManaInfoChangeConfirm);
			if (list != null && list.size() > 0) {
				return (Map<String, Object>) list.get(0);
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取修改管理医生记录失败", e);
		}
	}

	/**
	 * 获取修改管理医生明细记录
	 * 
	 * @param archiveMoveId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getMoveRecordDetail(String archiveMoveId)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "archiveMoveId", "s",
					archiveMoveId);
			return dao.doList(cnd, null, MOV_ManaInfoChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取修改管理医生明细记录失败", e);
		}
	}

	/**
	 * 迁移档案信息
	 * 
	 * @param fileName
	 * @param params
	 * @param tableList
	 * @throws ModelDataOperationException
	 */
	public void moveRecord(String fileName, Map<String, Object> params,
			List<String> tableList) throws ModelDataOperationException {
		List<String> sqlList = BizColumnConfig.getUpdateSql(fileName, params,
				tableList);
		for (String sql : sqlList) {
			Map<String, Object> realParams = HQLHelper.selectParameters(sql,
					params);
			try {
				dao.doUpdate(sql, realParams);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "迁移档案信息失败", e);
			}
		}
	}

	/**
	 * 删除修改管理医生申请记录
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MOV_ManaInfoChangeConfirm);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除修改管理医生申请记录失败", e);
		}
	}

	/**
	 * 删除修改管理医生申请记录明细信息
	 * 
	 * @param archiveMoveId
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecordDetail(String archiveMoveId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("archiveMoveId", archiveMoveId,
					MOV_ManaInfoChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除修改管理医生申请记录明细信息失败", e);
		}
	}

	/**
	 * 删除修改管理医生申请记录明细信息
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecordDetailByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MOV_ManaInfoChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除修改管理医生申请记录明细信息失败", e);
		}
	}

}
