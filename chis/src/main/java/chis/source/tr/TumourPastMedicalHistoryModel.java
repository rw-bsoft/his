/**
 * @(#)TumourPastMedicalHistoryModel.java Created on 2014-8-26 下午4:12:31
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
public class TumourPastMedicalHistoryModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourPastMedicalHistoryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存肿瘤既往史
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-26 下午4:18:02
	 * @Modify:
	 */
	public Map<String, Object> saveTPastMedicalHostory(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourPastMedicalHistory, record,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤既往史时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤既往史失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:判断某人是否有肿瘤既往史记录
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-28 下午3:22:36
	 * @Modify:
	 */
	public boolean isExistTPMHRecord(String empiId)
			throws ModelDataOperationException {
		String hqlString = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourPastMedicalHistory)
				.append(" where empiId=:empiId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlString, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断某人是否有肿瘤既往史记录失败！", e);
		}
		boolean isExist = false;
		if(rsMap != null){
			long recNum = (Long) rsMap.get("recNum");
			if(recNum > 0){
				isExist = true;
			}
		}
		return isExist;
	}
}
