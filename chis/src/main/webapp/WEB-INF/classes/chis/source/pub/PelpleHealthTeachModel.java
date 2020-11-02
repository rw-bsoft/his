/**
 * @(#)PelpleHealthTeachModel.java Created on 2015-3-6 下午4:41:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PelpleHealthTeachModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(PelpleHealthTeachModel.class);

	protected BaseDAO dao = null;

	/**
	 * 
	 * @param dao
	 */
	public PelpleHealthTeachModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void saveSelectRecords(Map<String, Object> diagnose) throws ModelDataOperationException {
		UserRoleToken urt=UserRoleToken.getCurrent();
		diagnose.put("inputUnit", urt.getManageUnitId());
		diagnose.put("inputDate", new Date());
		diagnose.put("inputUser", urt.getUserId());
		diagnose.put("lastModifyUser", new Date());
		diagnose.put("lastModifyUnit", urt.getManageUnitId());
		diagnose.put("lastModifyDate", urt.getUserId());
		try {
			dao.doSave("create", PUB_PelpleHealthDiagnose, diagnose, true);
		} catch (ValidateException e) {
			logger.error("save Select Records failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存疾病信息失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("save Select Records failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存疾病信息失败！");
		}
		
	}

	public void clearSelectRecords(String recordId) throws ModelDataOperationException {
		try {
			dao.doRemove("recordId", recordId, PUB_PelpleHealthDiagnose);
		} catch (PersistentDataOperationException e) {
			logger.error("clear Select Records failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "清空疾病信息失败！");
		}
		
	}

}
