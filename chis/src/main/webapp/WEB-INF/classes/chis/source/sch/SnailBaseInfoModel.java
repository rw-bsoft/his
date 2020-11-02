/**
 * @(#)schis.sourcetospmaBaseInfoModel.java Created on 2012-5-15 上午10:12:49
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
public class SnailBaseInfoModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public SnailBaseInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> saveSnailBaseInfo(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查螺灭螺基本情况数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存查螺灭螺基本情况失败", e);
		}
		return body;
	}

	public Map<String, Object> saveSnailFindInfo(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查螺记录数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存查螺记录失败", e);
		}
		return body;
	}
	public Map<String, Object> saveSnailKillInfo(String schema, String op,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		Map<String, Object> body = null;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "灭螺记录数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存灭螺记录失败", e);
		}
		return body;
	}
	public void removeSnailBaseInfo(String snailBaseInfoId, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(snailBaseInfoId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除查螺灭螺基本情况失败", e);
		}
	}
	public void removeSnailFindInfo(String snailFindInfoId, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(snailFindInfoId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除查螺记录失败", e);
		}
	}
	public void removeSnailKillInfo(String snailKillInfoId, String schema) throws ModelDataOperationException {
		try {
			dao.doRemove(snailKillInfoId, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除灭螺记录失败", e);
		}
	}
}
