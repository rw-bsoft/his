/**
 * @(#)ManaInfoBatchChangeModule.java Created on 2012-5-33 下午4:50:35
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mov;

import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.BizColumnConfig;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.HQLHelper;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class ManaInfoBatchChangeModule implements BSCHISEntryNames {

	private BaseDAO dao;

	public ManaInfoBatchChangeModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存批量修改管理医生记录
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
					ServiceCode.CODE_DATABASE_ERROR, "保存批量修改管理医生记录失败", e);
		}
	}

	/**
	 * 保存批量修改管理医生明细记录
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
			dao.doSave(op, MOV_ManaInfoBatchChangeDetail, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存批量修改管理医生明细记录失败", e);
		}
	}

	/**
	 * 获取批量修改管理医生记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(MOV_EHRManaInfoBatchChangeConfirm, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取批量修改管理医生记录失败", e);
		}
	}

	/**
	 * 获取批量修改管理医生明细记录
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
			return dao.doList(cnd, null, MOV_ManaInfoBatchChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取批量修改管理医生明细记录失败", e);
		}
	}

	/**
	 * 迁移档案信息
	 * 
	 * @param fileName
	 * @param params
	 * @throws ModelDataOperationException
	 */
	public void moveRecord(String fileName, Map<String, Object> params,
			List<String> tableList) throws ModelDataOperationException {
		List<String> sqlList = null;
		if (tableList != null && tableList.size() > 0) {
			sqlList = BizColumnConfig.getUpdateSql(fileName, params, tableList);
		} else {
			sqlList = BizColumnConfig.getUpdateSql(fileName, params);
		}
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
	 * 删除批量修改管理医生申请记录
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MOV_EHRManaInfoBatchChangeConfirm);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除批量修改管理医生申请记录失败", e);
		}
	}

	/**
	 * 删除批量修改管理医生申请记录明细信息
	 * 
	 * @param archiveMoveId
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecordDetail(String archiveMoveId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("archiveMoveId", archiveMoveId,
					MOV_ManaInfoBatchChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除批量修改管理医生申请记录明细信息失败", e);
		}
	}

	/**
	 * 删除批量修改管理医生申请记录明细信息
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removeMoveRecordDetailByPkey(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MOV_ManaInfoBatchChangeDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除批量修改管理医生申请记录明细信息失败", e);
		}
	}

}
