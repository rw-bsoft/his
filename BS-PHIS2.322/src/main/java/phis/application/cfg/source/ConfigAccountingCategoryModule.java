package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;

import ctd.account.UserRoleToken;

import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigAccountingCategoryModule {
	protected BaseDAO dao;

	public ConfigAccountingCategoryModule() {
	}

	public ConfigAccountingCategoryModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据主键查询核算类别信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void queryNodInfo(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		String pk = req.get("pk") + "";
		try {
			ret = dao.doLoad(BSPHISEntryNames.WL_HSLB, pk);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		res.put("ret", ret);

	}

	/**
	 * 保存核算类别信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void saveNodInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ValidateException {
		String TOPID = ParameterUtil.getTopUnitId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		Map<String, Object> parametershsbm = new HashMap<String, Object>();
		Map<String, Object> parametershslb = new HashMap<String, Object>();
		Map<String, Object> parameterscount = new HashMap<String, Object>();
		parametershsbm.put("SJHS", Long.parseLong(record.get("SJHS") + ""));
		parameterscount.put("ZBLB", Integer.parseInt(record.get("ZBLB") + ""));
		parameterscount.put("HSMC", record.get("HSMC") + "");
		parameterscount.put("JGID", manaUnitId);
		parameterscount.put("JGIDTOP", TOPID);
		try {
			Long l = dao
					.doCount(
							"WL_HSLB",
							"HSMC=:HSMC and ZBLB=:ZBLB and (SJHS > -1) and (JGID=:JGID OR JGID=:JGIDTOP)",
							parameterscount);
			if (l > 0) {
				res.put(Service.RES_CODE, 615);
			} else {
				Map<String, Object> hslbMap = dao.doSave("create",
						BSPHISEntryNames.WL_HSLB, record, false);
				Map<String, Object> hsbmMap = dao.doLoad(
						"select HSBM as HSBM from WL_HSLB where HSLB=:SJHS",
						parametershsbm);
				parametershslb.put("HSLB",
						Long.parseLong(hslbMap.get("HSLB") + ""));
				String hsbm = hsbmMap.get("HSBM").toString()
						+ hslbMap.get("HSLB").toString();
				parametershslb.put("HSBM", hsbm);
				dao.doUpdate("update WL_HSLB set HSBM=:HSBM where HSLB=:HSLB",
						parametershslb);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

	/**
	 * 更新核算类别信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void updateNodInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ValidateException {
		String TOPID = ParameterUtil.getTopUnitId();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> record = (Map<String, Object>) req.get("body");
		Map<String, Object> parameterscount = new HashMap<String, Object>();
		parameterscount.put("HSLB", Long.parseLong(record.get("HSLB") + ""));
		parameterscount.put("ZBLB", Integer.parseInt(record.get("ZBLB") + ""));
		parameterscount.put("HSMC", record.get("HSMC") + "");
		parameterscount.put("JGID", manaUnitId);
		parameterscount.put("JGIDTOP", TOPID);

		try {
			Long l = dao
					.doCount(
							"WL_HSLB",
							"HSMC=:HSMC and ZBLB=:ZBLB and (SJHS > -1) and (JGID=:JGID OR JGID=:JGIDTOP) and HSLB<>:HSLB",
							parameterscount);
			if (l > 0) {
				res.put(Service.RES_CODE, 615);
			} else {
				dao.doSave("update", BSPHISEntryNames.WL_HSLB, record, false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

	/**
	 * 根据主键删除核算类别信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void deleteNode(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		String pk = req.get("pk") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("HSLB", Integer.parseInt(pk));
		try {
			Long l = dao.doCount("WL_WZZD", "HSLB=:HSLB", parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 616);
			} else {
				dao.doRemove(Long.parseLong(pk), BSPHISEntryNames.WL_HSLB);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}

	}

	/**
	 * 根据主键删除核算类别信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetHslb(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> wzzdMap = new HashMap<String, Object>();
		String TOPID = ParameterUtil.getTopUnitId();
		Long hslb = 0L;
		if (req.get("HSLB") != null) {
			hslb = Long.parseLong(req.get("HSLB") + "");
		}
		ret.put("HSLB", hslb);
		ret.put("JGID", TOPID);
		try {
			Long l = dao.doCount("WL_HSLB", "HSLB=:HSLB and JGID=:JGID", ret);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
			} else {
				wzzdMap.put("HSLB", Integer.parseInt(hslb + ""));
				Long l1 = dao.doCount("WL_WZZD", "HSLB=:HSLB", wzzdMap);
				if (l1 > 0) {
					res.put(Service.RES_CODE, 614);

				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
	}
}
