package phis.application.cfg.source;

import java.util.ArrayList;
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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigHsqxYgModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ConfigHsqxYgModel.class);

	public ConfigHsqxYgModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doSaveHSQX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			if (req.get("YGID") != null) {
				parameters.put("YGID", req.get("YGID"));
				Long l = dao.doCount("WL_HSQX", "YGID=:YGID", parameters);
				if (l > 0) {
					res.put(Service.RES_CODE, 612);
				} else {
					dao.doSave("create", BSPHISEntryNames.WL_HSQX, req, false);
				}
			}
			res.put("body", req);
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护士权限保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护士权限保存失败");
		}
	}

	public void doUpdateHSZBZ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String ygid = req.get("YGID") + "";
		int hszbz = Integer.parseInt(req.get("HSZBZ") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("YGID", ygid);
		parameters.put("HSZBZ", hszbz);
		try {
			dao.doUpdate("update WL_HSQX set HSZBZ=:HSZBZ where YGID=:YGID",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "更新护士长失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHSQXKS(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			if (req.get("YGID") != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("YGID", req.get("YGID") + "");
				dao.doUpdate(
						"delete from WL_HSQX where YGID=:YGID and KSDM<>0",
						parameters);
			}
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> map_ = body.get(i);// 一条一条放到map_中

				dao.doSave("create", BSPHISEntryNames.WL_HSQX, map_, false);
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

	public void doUpdateMRKS(Map<String, Object> body, String schemaList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersYGID = new HashMap<String, Object>();
		try {
			parameters.put("KSDM", Long.parseLong(body.get("KSDM").toString()));
			parameters.put("YGID", body.get("YGID") + "");
			parametersYGID.put("YGID", body.get("YGID") + "");
			dao.doUpdate("update " + schemaList
					+ " set MRZ=0 WHERE YGID=:YGID and MRZ=1", parametersYGID);
			dao.doUpdate("update " + schemaList
					+ " set MRZ=1 WHERE YGID=:YGID and KSDM=:KSDM", parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/**
	 * 科室权限分配的科室信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetKSDMInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getRef();// 用户的机构ID
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		String queryCnds = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		if (req.containsKey("cnds")) {
			queryCnds = req.get("cnds") + "";
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select a.ID as ID,a.OFFICENAME as OFFICENAME,a.PYCODE as PYCODE from SYS_Office a where a.ID not in (select ksdm from WL_HSQX");
		parameters.put("JGID", manaUnitId);
		parameterssize.put("JGID", manaUnitId);
		if (!"null".equals(queryCnds) && queryCnds != null) {
			String[] que = queryCnds.split(",");
			String qur = "where " + que[2].substring(1, que[2].indexOf("]"))
					+ "='" + que[4].substring(0, que[4].indexOf("]")).trim()
					+ "'";
			sql.append(" " + qur);
		}
		sql.append(") and a.ORGANIZCODE=:JGID and a.LOGOFF<>'1'");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ " like '%"
					+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.ID");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.cfg.schemas.SYS_Office_SELECT");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 二级库房对照--所有科室
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */

	public void doGetKSDMForEJInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getRef();// 用户的机构ID
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		String queryCnds = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		if (req.containsKey("cnds")) {
			queryCnds = req.get("cnds") + "";
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select a.ID as ID,a.OFFICENAME as OFFICENAME,a.PYCODE as PYCODE from SYS_Office a where a.ID not in (select KSDM from WL_KFDZ ");
		parameters.put("JGID", manaUnitId);
		parameterssize.put("JGID", manaUnitId);
		/*
		 * parameters.put("KFXH", KFXH); parameterssize.put("KFXH", KFXH);
		 */
		if (!"null".equals(queryCnds) && queryCnds != null) {
			String[] que = queryCnds.split(",");
			String qur = "where " + que[2].substring(1, que[2].indexOf("]"))
					+ "='" + que[4].substring(0, que[4].indexOf("]")).trim()
					+ "'";
			sql.append(" " + qur);
		}
		sql.append(") and a.ORGANIZCODE=:JGID and a.LOGOFF<>'1'");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ " like '%"
					+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.ID");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.SYS_Office_SELECT);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 二级库房科室对照
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveKFDZ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		int KFXH = Integer.parseInt(req.get("KFXH") + "");
		try {
			dao.removeByFieldValue("KFXH", KFXH, BSPHISEntryNames.WL_KFDZ);

			Map<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("KFXH", KFXH);
			StringBuffer sql = new StringBuffer(
					"select EJKF as EJKF from WL_KFXX where KFXH=:KFXH");

			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(),
					parMap);
			long ksdm = 0;
			if (list.size() > 0) {
				if (list.get(0).get("EJKF") != null) {
					ksdm = Long.parseLong(list.get(0).get("EJKF") + "");
				}
			}
			int num = 0;
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("KFXH", KFXH);
				long KSDM2 = ksdm;
				if (body.get(i).get("KSDM") != null) {
					KSDM2 = Long.parseLong(body.get(i).get("KSDM") + "");
				}
				if (body.get(i).get("ID") != null) {
					KSDM2 = Long.parseLong(body.get(i).get("ID") + "");
				}
				map.put("KSDM", KSDM2);
				if (KSDM2 == ksdm) {
					num = num + 1;
				}
				dao.doSave("create", BSPHISEntryNames.WL_KFDZ, map, false);
			}
			if (num == 0) {
				parMap.clear();
				parMap.put("KFXH", KFXH);
				parMap.put("KSDM", ksdm);
				dao.doSave("create", BSPHISEntryNames.WL_KFDZ, parMap, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
	}
}
