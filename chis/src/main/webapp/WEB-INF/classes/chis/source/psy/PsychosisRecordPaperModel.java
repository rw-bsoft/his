/**
 * @(#)PsyRecordPaperModel.java Created on 2011-3-31 下午11:36:43
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
public class PsychosisRecordPaperModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public PsychosisRecordPaperModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存记录纸信息
	 * 
	 * @param op
	 *            操作类型
	 * @param record
	 *            记录数据
	 * @param validate
	 *            是否对数据进行schema验证
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveRecordPapgerInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PSY_RecordPaper, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存记录纸信息数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存记录纸信息失败！", e);
		}
		return rsMap;
	}
}
