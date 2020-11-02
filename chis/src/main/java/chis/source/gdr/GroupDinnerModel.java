/**
 * 	@(#)GroupDinnerModel.java Created on 2012-02-17 上午11:27:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.gdr;

import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import ctd.validator.ValidateException;

public class GroupDinnerModel extends EmpiModel {

	private BaseDAO dao;

	public GroupDinnerModel(BaseDAO dao) {
		super(dao);
		this.dao = dao;
	}

	/**
	 * 查询群宴登记数据 和 群宴首次执导是否录入
	 * 
	 * @param entryName
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getGroupDinnerRecord(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> gdRecordMap;
		try {
			gdRecordMap = dao.doLoad(GDR_GroupDinnerRecord, pkey);
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "gdrId", "s", pkey);
			List<Map<String, Object>> list = dao.doQuery(cnd, null,
					GDR_FirstGuide);
			if (list.size() > 0) {
				gdRecordMap.put("hasFirstGuide", true);
			} else {
				gdRecordMap.put("hasFirstGuide", false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取群宴管理信息失败", e);
		}
		return gdRecordMap;
	}

	/**
	 * 获取群宴第二次指导信息
	 * 
	 * @param cnd
	 * @param schema
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getSecondGuideInfo(List cnd, String schema)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doLoad(cnd, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取群宴第二次指导信息失败", e);
		}
		return body;
	}

	/**
	 * 获取群宴首次指导信息
	 * 
	 * @param cnd
	 * @param schema
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getFirstGuideInfo(List cnd, String schema)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doLoad(cnd, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取群宴首次指导信息失败", e);
		}
		return body;
	}

	/**
	 * 获取群宴回访信息
	 * 
	 * @param cnd
	 * @param schema
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getVisitInfo(List cnd, String schema)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doLoad(cnd, schema);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取群宴回访信息失败", e);
		}
		return body;
	}

	/**
	 * 保存群宴登记信息
	 * 
	 * @param reqBody
	 * @param schema
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveGroupDinnerRecord(
			Map<String, Object> reqBody, String schema, String op)
			throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "群宴登记信息数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存群宴登记信息失败", e);
		}
		return body;
	}

	/**
	 * 保存群宴首次指导信息
	 * 
	 * @param reqBody
	 * @param schema
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveFirstGuide(Map<String, Object> reqBody,
			String schema, String op) throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "群宴首次指导信息数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存群宴首次指导信息失败", e);
		}
		return body;
	}

	/**
	 * 保存群宴第二次指导信息
	 * 
	 * @param reqBody
	 * @param schema
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveSecondGuide(Map<String, Object> reqBody,
			String schema, String op) throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "群宴第二次指导信息数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存群宴第二次指导信息失败", e);
		}
		return body;
	}

	/**
	 * 保存群宴回访信息
	 * 
	 * @param reqBody
	 * @param schema
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveVisit(Map<String, Object> reqBody,
			String schema, String op) throws ModelDataOperationException {
		Map<String, Object> body;
		try {
			body = dao.doSave(op, schema, reqBody, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "群宴回访信息数据格式错误", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存群宴回访信息失败", e);
		}
		return body;
	}

	/**
	 * 删除群宴信息
	 * 
	 * @param reqBody
	 * @param schema
	 * @param op
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void removeGroupDinnerRecord(String gdrId, String schema)
			throws ModelDataOperationException {
		try {
			dao.doRemove("gdrId", gdrId, schema);
			dao.doRemove("gdrId", gdrId, GDR_FirstGuide);
			dao.doRemove("gdrId", gdrId, GDR_SecondGuide);
			dao.doRemove("gdrId", gdrId, GDR_Visit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除群宴信息失败", e);
		}
	}
}
