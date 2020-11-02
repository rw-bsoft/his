/**
 * @description 收费项目
 * 
 * @author shiwy 2012.06.29
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;

import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigChargingProjectsDelModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsDelModel.class);

	public ConfigChargingProjectsDelModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 收费项目删除。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> removeChargingProjects(int sfxm,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			String sql = "FYGB=:SFXM";
			map.put("SFXM", sfxm);
			// 删除收费项目时判断收费项目是否有明细 如果有 提示不能删
			Long l = dao.doCount("GY_YLSF", sql, map);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "收费项目明细已存在，无法删除");
			} else {
				dao.doUpdate("DELETE FROM GY_ZFBL WHERE SFXM=:SFXM", map);
				dao.doUpdate("DELETE FROM GY_SFXM WHERE SFXM=:SFXM", map);
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background validation: remove the patient the nature of the failure of.",
					e);
			throw new ModelDataOperationException("后台验证：删除收费项目失败.", e);
		}
		return null;
	}
}
