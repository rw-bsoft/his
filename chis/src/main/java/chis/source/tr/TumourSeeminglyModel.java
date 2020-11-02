/**
 * @(#)TumourSeeminglyModel.java Created on 2014-4-21 上午11:18:09
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.HashMap;
import java.util.Map;

import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourSeeminglyModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourSeeminglyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存疑似肿瘤信息
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-21 上午11:26:49
	 * @Modify:
	 */
	public Map<String, Object> saveTumourSeemingly(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourSeemingly, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存疑似肿瘤信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存疑似肿瘤信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:删除某人某类型的肿瘤初筛记录
	 * @param empiId
	 * @param highRiskType
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-23 上午9:57:16
	 * @Modify:
	 */
	public void deleteTumourSeemingly(String empiId, String highRiskType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete ").append(MDC_TumourSeemingly)
				.append(" where empiId=:empiId and highRiskType=:highRiskType")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除某人某类型的肿瘤初筛记录失败！", e);
		}
	}
}
