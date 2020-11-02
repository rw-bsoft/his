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

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;


import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigChargingProjectsDetailDelModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChargingProjectsDetailDelModel.class);

	public ConfigChargingProjectsDetailDelModel(BaseDAO dao) {
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
	public Map<String, Object> removeChargingProjectsDetail(int fyxh,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FYXH", fyxh);
				dao.doUpdate("DELETE FROM GY_YLSF WHERE FYXH=:FYXH", map);
				dao.doUpdate("DELETE FROM GY_FYBM WHERE FYXH=:FYXH", map);
				dao.doUpdate("DELETE FROM GY_FYJY WHERE FYXH=:FYXH", map);
			
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background validation: remove the patient the nature of the failure of.",
					e);
			throw new ModelDataOperationException("后台验证：删除收费项目明细失败.", e);
		}
		return null;
	}
}
