/**
 * @(#)PsychosisAnnualAssessmentModel.java Created on 2012-4-5 上午9:42:17
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.psy;

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
public class PsychosisAnnualAssessmentModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public PsychosisAnnualAssessmentModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存精神病年度评估
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePsyAnnualAssessment(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PSY_AnnualAssessment, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存年度评估数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存年度评估失败！", e);
		}
		return rsMap;
	}

}
