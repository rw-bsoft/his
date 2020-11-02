/**
 * @(#)PoorPeopleRecordModel.java Created on 2012-3-28 下午05:19:04
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.poor;

import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class PoorPeopleRecordModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public PoorPeopleRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存贫困人群随访记录
	 * 
	 * @param op
	 * @param body
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> savePoorPeopleVisitRecord(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		try {
			return dao.doSave(op, EHR_PoorPeopleVisit, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("贫困人群随访记录保存失败！", e);
		}
	}

	/**
	 * 根据empiId查询贫困人口随访数据
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public List<?> getPoorPeopleRecordByEmpiId(String empiId)
			throws ModelDataOperationException, ValidateException {
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "c.status", "s",
					Constants.CODE_STATUS_WRITE_OFF);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			return dao.doList(cnd, null, EHR_PoorPeopleVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("贫困人群随访记录查询失败！", e);
		}
	}
}
