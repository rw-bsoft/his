package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigAntibioticsPermissionsModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigAntibioticsPermissionsModel.class);

	public ConfigAntibioticsPermissionsModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 抗生素权限保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAntibioticsPermissions(Map<String, Object> req,
			BaseDAO dao) throws ModelDataOperationException {
		String schema = (String) req.get("schema");
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			if (BSPHISEntryNames.GY_KSQX.equals(schema)) {
				for (int i = 0; i < body.size(); i++) {
					// 先把有的删除掉 重新插入
					parameters.put("YSGH", body.get(i).get("YSGH"));
					parameters.put("YPXH", body.get(i).get("YPXH"));
					dao.doUpdate(
							"delete from GY_KSQX where YSGH=:YSGH and YPXH=:YPXH",
							parameters);
					dao.doSave("create", BSPHISEntryNames.GY_KSQX, body.get(i),
							false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
	}

	/**
	 * 抗生素权限删除 *@param req
	 * 
	 * @param req
	 * @param dao2
	 * @param ctx
	 */
	public void doRemoveAntibioticsPermissions(Map<String, Object> req,
			BaseDAO dao2, Context ctx) throws ModelDataOperationException {
		String schema = (String) req.get("schema");
		int pkey = (Integer) req.get("pkey");
		try {
			if (BSPHISEntryNames.GY_KSQX.equals(schema)) {
				dao.doRemove(pkey, BSPHISEntryNames.GY_KSQX);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Delete failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		}

	}
}
