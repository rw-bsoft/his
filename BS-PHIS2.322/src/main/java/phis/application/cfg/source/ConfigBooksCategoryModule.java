/**
 * @(#)ConfigBooksCategoryModule.java Created on 2013-11-05 下午13:58:58
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 账簿类别维护
 * 
 * @author shiwy 2013.11.05
 */
public class ConfigBooksCategoryModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryModule.class);

	public ConfigBooksCategoryModule(BaseDAO dao) {
		this.dao = dao;
	}

	// 账簿类别启用
	public void doUpdateConfigBooksCategory(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersZBLB = new HashMap<String, Object>();
		Map<String, Object> parametersHSLB = new HashMap<String, Object>();
		try {
			parameters.put("ZBLB", Long.parseLong(body.get("ZBLB").toString()));
			parametersZBLB.put("ZBLB",
					Long.parseLong(body.get("ZBLB").toString()));
			parameters.put("ZBZT", 1);
			dao.doUpdate("update " + schemaList
					+ " set ZBZT=:ZBZT WHERE ZBLB=:ZBLB", parameters);
			Map<String, Object> zblbmap = dao.doLoad(
					"SELECT JGID as JGID,ZBMC as HSMC,ZBMC as ZBMC,ZBLB as ZBLB ,-1 as SJHS FROM "
							+ schemaList + " WHERE ZBLB=:ZBLB", parametersZBLB);
			Map<String, Object> hslbMap = dao.doSave("create", BSPHISEntryNames.WL_HSLB,
					zblbmap, false);
			parametersHSLB.put("HSBM", hslbMap.get("HSLB") + "");
			parametersHSLB
					.put("HSLB", Long.parseLong(hslbMap.get("HSLB") + ""));
			dao.doUpdate("update WL_HSLB set HSBM=:HSBM where HSLB=:HSLB",
					parametersHSLB);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	// 判断名称不能重复
	public void doSxhAndZBMCVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String TOPID = ParameterUtil.getTopUnitId();
		if ("create".equals(op)) {
			if (body.get("SXH") != null && body.get("SXH") != "") {
				String sql = "SXH=:SXH and (JGID=:JGID or JGID=:TOPJGID)";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SXH", Long.parseLong(body.get("SXH") + ""));
				parameters.put("JGID", manaUnitId);
				parameters.put("TOPJGID", TOPID);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 612);
						res.put(Service.RES_MESSAGE, "顺序号已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "顺序号校验失败");
				}
			}
			if (body.get("ZBMC") != null && body.get("ZBMC") != "") {
				String sql = "ZBMC=:ZBMC and (JGID=:JGID or JGID=:TOPJGID)";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZBMC", body.get("ZBMC"));
				parameters.put("JGID", manaUnitId);
				parameters.put("TOPJGID", TOPID);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "账簿类别名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "账簿类别名称校验失败");
				}
			}
		} else {
			if (body.get("SXH") != null && body.get("SXH") != "") {
				String sql = "SXH=:SXH and (JGID=:JGID or JGID=:TOPJGID) and ZBLB<>:ZBLB";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SXH", Long.parseLong(body.get("SXH") + ""));
				parameters.put("ZBLB", Long.parseLong(body.get("ZBLB") + ""));
				parameters.put("JGID", manaUnitId);
				parameters.put("TOPJGID", TOPID);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 612);
						res.put(Service.RES_MESSAGE, "顺序号已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "顺序号校验失败");
				}
			}
			if (body.get("ZBMC") != null && body.get("ZBMC") != "") {
				String sql = "ZBMC=:ZBMC and (JGID=:JGID or JGID=:TOPJGID) and ZBLB<>:ZBLB";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZBMC", body.get("ZBMC"));
				parameters.put("ZBLB", Long.parseLong(body.get("ZBLB") + ""));
				parameters.put("JGID", manaUnitId);
				parameters.put("TOPJGID", TOPID);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "账簿类别名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					logger.error("Save failed.", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "账簿类别名称校验失败");
				}
			}
		}
	}
}
