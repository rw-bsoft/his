/**
 * @(#)MHCMoveModule.java Created on 2012-5-21 下午4:50:35
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
import chis.source.util.HQLHelper;

import ctd.validator.ValidateException;

/**
 * @description 孕妇户籍地址修改
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class MHCMoveModule implements BSCHISEntryNames {

	private BaseDAO dao;

	public MHCMoveModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存孕妇户籍地址迁移记录
	 * 
	 * @param data
	 * @param op
	 * @param scheamName
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void saveMHCMoveRecord(Map<String, Object> data, String op,
			String scheamName, Map<String, Object> res)
			throws ValidateException, ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, scheamName, data,
					true);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕妇户籍地址迁移记录失败", e);
		}
	}

	/**
	 * 获取孕妇户籍地址迁移记录
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMHCMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			return dao.doLoad(MOV_MHC, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取孕妇户籍地址迁移记录失败", e);
		}
	}

	/**
	 * 迁移孕妇信息
	 * 
	 * @param params
	 * @throws ModelDataOperationException
	 */
	public void movePregnantMessage(Map<String, Object> params)
			throws ModelDataOperationException {
		List<String> sqlList = BizColumnConfig
				.getUpdateSql(BizColumnConfig.MOV_MHC,params);
		for (String sql : sqlList) {
			Map<String, Object> realParams = HQLHelper.selectParameters(sql,
					params);
			try {
				dao.doUpdate(sql, realParams);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "迁移孕妇信息失败", e);
			}
		}
	}

	/**
	 * 删除孕妇户籍地址迁移记录
	 * 
	 * @param pkey
	 * @throws
	 */
	public void deleteMHCMoveRecord(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, MOV_MHC);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除孕妇户籍地址迁移记录失败", e);
		}
	}
}
