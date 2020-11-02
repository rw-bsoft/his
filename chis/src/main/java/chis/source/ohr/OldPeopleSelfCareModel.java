/**
 * @(#)OldPeopleSelfCareModel.java Created on 2013年8月5日 下午1:56:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class OldPeopleSelfCareModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public OldPeopleSelfCareModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存老年人自我评估
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013年8月5日 下午2:30:17
	 * @Modify:
	 */
	public Map<String, Object> saveOPSC(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_OldPeopleSelfCare, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存老年人自我评估时数据验证失败!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存老年人自我评估失败!", e);
		}
		return rsMap;
	}
}
