/**
 * 
 * @description 给药途径维护
 * @author zhangyq 2012.05.30
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
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.validator.ValidateException;

public class ConfigRouteAdministrationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigRouteAdministrationModel.class);

	public ConfigRouteAdministrationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 给药途径维护保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected Map<String, Object> doSaveRouteAdministration(String op,
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> remap = new HashMap<String, Object>();
		remap.put("NUM", "0");
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("XMMC", (body.get("XMMC") + "").trim());
			map.put("YPYF", 0l);
			if (body.get("YPYF") != null) {
				map.put("YPYF", Long.parseLong(body.get("YPYF").toString()));
			}
			if (dao.doCount("ZY_YPYF", "XMMC=:XMMC and YPYF<>:YPYF", map) > 0) {
				remap.put("NUM", "1");
				return remap;
			}
			if (body.get("FYMC") == null
					|| body.get("FYMC").toString().trim().length() == 0) {
				body.put("FYXH", "");
			}
			remap = dao.doSave(op, BSPHISEntryNames.ZY_YPYF, body, false);
		} catch (ValidateException e) {
			logger.error("fail to validate medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save medicine information.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
		return remap;
	}
}
