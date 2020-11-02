/**
 * @(#)PorblemCollectModel.java Created on 2012-5-21 下午04:28:55
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

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
public class PorblemCollectModel implements BSCHISEntryNames {
	private BaseDAO dao;
	public PorblemCollectModel(BaseDAO dao) {
		this.dao=dao;
	}
	public void removePublicInfo(String pkey, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除问题收集信息失败");
		}
	}
	public Map<String, Object> savePorblemCollect(String op, String schema,
			Map<String, Object> reqBody) throws ModelDataOperationException{
		Map<String, Object> body;
		try {
			body=dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "问题收集信息数据验证失败");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存问题收集信息失败");
		}
		return body;
	}

}
