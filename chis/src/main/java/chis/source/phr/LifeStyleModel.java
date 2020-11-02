package chis.source.phr;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

import ctd.validator.ValidateException;

public class LifeStyleModel implements BSCHISEntryNames {

	private static final Logger logger = LoggerFactory
			.getLogger(LifeStyleModel.class);

	protected BaseDAO dao;

	/**
	 * @param dao
	 */
	public LifeStyleModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> getLifeStyleByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		Map<String, Object> map = null;
		try {
			map = dao.doLoad(cnd, EHR_LifeStyle);
			return SchemaUtil.setDictionaryMessageForForm(map, EHR_LifeStyle);
		} catch (PersistentDataOperationException e) {
			logger.error("failed to get lifeStyle message.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人生活习惯失败。");
		}
	}

	/**
	 * 保存生活习惯
	 * 
	 * @param op
	 * @param body
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> saveLifeStyle(String op, Map<String, Object> body)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, EHR_LifeStyle, body, true);
		} catch (PersistentDataOperationException e) {
			logger.error("save lisfStyle record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存个人生活习惯失败！");
		}
	}

	/**
	 * 查询个人生活习惯
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> getLifeStyleById(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> lifeStyleRecord;
		try {
			lifeStyleRecord = dao.doLoad(EHR_LifeStyle, pkey);
		} catch (PersistentDataOperationException e) {
			logger.error("search lisfStyle record failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询个人生活习惯失败！");
		}
		return lifeStyleRecord;
	}
}
