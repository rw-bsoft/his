/**
 * @(#)PublicInfoModel.java Created on 2012-5-8 上午10:26:52
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.admin;

import java.util.List;
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
public class PublicInfoModel implements BSCHISEntryNames{
	private BaseDAO dao;

	public PublicInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void removePublicInfo(String pkey, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除公告维护信息失败");
		}
		
	}

	public Map<String, Object> savePublicInfo(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body=null;
		try {
			body=dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存公告维护时数据验证失败");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存公告维护信息失败");
		}
		return body;
	}

	public Map<String, Object> listPublicInfo(List queryCnd, int pageNo,
			int pageSize, String schema, String queryCndsType, String sortInfo) throws ModelDataOperationException {
		Map<String, Object> body=null;
		try {
			body=dao.doList(queryCnd, sortInfo, schema, pageNo, pageSize, queryCndsType);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "列出公告维护信息失败");
		}
		return body;
	}
}
