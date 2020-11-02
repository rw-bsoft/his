/**
 * @(#)SchistospmaManageModel.java Created on 2012-5-14 下午04:08:40
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.sch;

import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class SchistospmaManageModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public SchistospmaManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> saveSchistospmaManage(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "血吸虫综合治理数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存血吸虫综合治理失败", e);
		}
		return body;
	}

	public void removeSchistospmaManage(String schisManageId, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(schisManageId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除血吸虫综合治理失败", e);
		}
		
	}
}