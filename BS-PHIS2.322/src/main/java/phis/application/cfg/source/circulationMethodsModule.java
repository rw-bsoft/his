package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class circulationMethodsModule {
	protected BaseDAO dao;

	public circulationMethodsModule() {
	}

	public circulationMethodsModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 修改流转方式的方式状态
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void disable(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException, ValidateException {
		String FSZT = "1".equals(req.get("FSZT") + "") == true ? "-1" : "1";
		String FSXH = req.get("pk") + "";
		Map<String, Object> record = new HashMap<String, Object>();
		record.put("FSZT", FSZT);
		record.put("FSXH", FSXH);
		try {
			dao.doSave("update", BSPHISEntryNames.WL_LZFS, record, false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "数据操作失败");
		}

	}

	// 判断名称不能重复
	public void doQueryFSMCVerification(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		if ("create".equals(op)) {
			if (body.get("FSMC") != null && body.get("FSMC") != "") {
				String sql = "FSMC=:FSMC and KFXH=:KFXH and DJLX=:DJLX";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FSMC", body.get("FSMC") + "");
				parameters.put("DJLX", body.get("DJLX") + "");
				parameters.put("KFXH", kfxh);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "方式名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "方式名称校验失败");
				}
			}
		} else {
			if (body.get("FSMC") != null && body.get("FSMC") != "") {
				String sql = "FSMC=:FSMC and KFXH=:KFXH and DJLX=:DJLX and FSXH<>:FSXH";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FSXH", Long.parseLong(body.get("FSXH") + ""));
				parameters.put("FSMC", body.get("FSMC") + "");
				parameters.put("DJLX", body.get("DJLX") + "");
				parameters.put("KFXH", kfxh);
				try {
					Long l = dao.doCount(schemaDetailsList, sql, parameters);
					if (l > 0) {
						res.put(Service.RES_CODE, 613);
						res.put(Service.RES_MESSAGE, "方式名称已经存在");
					}
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "方式名称校验失败");
				}
			}
		}
	}

}
