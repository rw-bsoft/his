/**
 * @(#)NormalPopulationModel.java Created on 2012-3-30 上午10:58:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.nor;

import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class NormalPopulationModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public NormalPopulationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存非重点人群随访记录
	 * 
	 * @param op
	 * @param body
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> saveNormalPopulationVisitRecord(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		Map<String, Object> record;
		try {
			record = dao.doSave(op, EHR_NormalPopulationVisit, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存非重点人群随访记录失败！", e);
		}
		return record;
	}

}
