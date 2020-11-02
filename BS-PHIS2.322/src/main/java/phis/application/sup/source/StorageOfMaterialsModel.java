package phis.application.sup.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class StorageOfMaterialsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorageOfMaterialsModel.class);

	public StorageOfMaterialsModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_RK01");
		// 根据流转方式的特殊标识(tsbz)是否为1，如果为1则为特殊标识（如通过盘点产生的盘亏出库）则供货单位和经办人为空；不为1则必须填写，未填写则必须提示用户，并且返回
		List<Map<String, Object>> _lzfs = BSPHISUtil.uf_get_lzfs(
				Long.parseLong(rk.get("LZFS") + ""), dao, ctx);
		if (Long.parseLong(_lzfs.get(0).get("TSBZ") + "") != 1) {
			if (rk.get("DWXH") == null || rk.get("DWXH") == ""
					|| rk.get("JBGH") == null || rk.get("JBGH") == "") {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "供货单位和经办人不能为空");
			}
		}
		rk.put("KFXH", user.getProperty("treasuryId").toString());
		rk.put("ZDRQ", new Date());
		rk.put("ZDGH", user.getUserId());
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_RK02");
		double djje = 0.0;
		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			djje += Double.parseDouble(mat.get("WZJE").toString());
		}
		rk.put("DJJE", djje);
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_RK01_FORM, rk, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", DJXH);
					if (mat.get("KCXH") == null || mat.get("KCXH") == "") {
						Long kcxh = BSPHISUtil.setKCXH(dao);
						mat.put("KCXH", kcxh);
					}
					dao.doSave("create", BSPHISEntryNames.WL_RK02, mat, false);
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_RK01, rk, false);
				dao.removeByFieldValue("DJXH",
						Long.parseLong(rk.get("DJXH").toString()),
						BSPHISEntryNames.WL_RK02);

				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", rk.get("DJXH"));
					if (mat.get("KCXH") == null || mat.get("KCXH") == "") {
						Long kcxh = BSPHISUtil.setKCXH(dao);
						mat.put("KCXH", kcxh);
					}
					Map<String, Object> _map1 = dao.doSave("create",
							BSPHISEntryNames.WL_RK02, mat, false);
					if (mat.get("KCXH") != null
							&& Long.parseLong(mat.get("KCXH") + "") > 0) {
						Map<String, Object> parameters = new HashMap<String, Object>();
						parameters.put("KCXH",
								Long.parseLong(mat.get("KCXH") + ""));
						parameters.put("JLXH",
								Long.parseLong(_map1.get("JLXH") + ""));
						dao.doUpdate(
								"update WL_RK02 set KCXH=:KCXH where JLXH=:JLXH",
								parameters);
					}
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		try {
			List<Map<String, Object>> list = dao.doQuery(
					"from WL_KFXX where KFXH=:KFXH", parameters);
			String kfzb = (String) list.get(0).get("KFZB");
			parameters.clear();
			list = dao.doSqlQuery(
					"select ZBLB,ZBMC from WL_ZBLB  where ZBLB in(" + kfzb
							+ ") order by ZBLB", parameters);
			res.put("list", list);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_RK01,
					Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				return;
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doGetCjxhByWzxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", Long.parseLong(body.get("WZXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad("from "
					+ BSPHISEntryNames.WL_WZCJ + " where WZXH=:WZXH",
					parameters);
			if (map == null || map.size() == 0) {
				return;
			}
			res.put("cjxh", map.get("CJXH"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_RK01");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		parameters.put("LZDH", getLzdh());
		parameters.put("SHGH", user.getUserId());
		parameters.put("DJZT", 1);
		parameters.put("SHRQ", new Date());
		parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE WL_RK01 SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doIsEnoughInventory(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_RK02");
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select WZSL as WZSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.put("KCXH", Long.parseLong(mat.get("KCXH") + ""));
				parameters.put("KFXH", Integer.parseInt(user.getProperty(
						"treasuryId").toString()));
				Map<String, Object> map = dao
						.doLoad(hql.toString(), parameters);
				if (map == null || map.size() == 0) {
					res.put("isEnoughInventory", false);
					return;
				}
				Double kcsl = (Double) map.get("WZSL");
				Integer wzsl = (Integer) (mat.get("WZSL"));
				if (wzsl > kcsl) {
					res.put("isEnoughInventory", false);
					return;
				}

				parameters.put("WZSL", wzsl.doubleValue());
				dao.doUpdate(
						"update WL_WZKC set YKSL=YKSL+:WZSL where KCXH=:KCXH and KFXH=:KFXH",
						parameters);
				parameters.clear();
			}
			res.put("isEnoughInventory", true);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_RK01");
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_RK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LZDH", null);
		parameters.put("DJZT", 0);
		parameters.put("SHGH", null);
		parameters.put("SHRQ", null);
		parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE WL_RK01 SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
		try {
			dao.doUpdate(hql.toString(), parameters);
			if (rk.containsKey("THDJ") && !"0".equals(rk.get("THDJ"))) {
				parameters.clear();
				parameters.put("KFXH", Integer.parseInt(user.getProperty(
						"treasuryId").toString()));
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					parameters
							.put("KCXH", Long.parseLong(mat.get("KCXH") + ""));
					parameters.put("WZSL",
							Double.parseDouble(mat.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_WZKC set YKSL=YKSL-:WZSL where KCXH=:KCXH and KFXH=:KFXH",
							parameters);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "弃审失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doUpdateWzcj(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_RK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update WL_WZCJ set WZJG=:WZJG,LSJG=:LSJG where WZXH=:WZXH and CJXH=:CJXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				Double wzjg = Double.parseDouble(mat.get("WZJG") + "");
				Double lsjg = Double.parseDouble(mat.get("LSJG") + "");
				Long cjxh = Long.parseLong(mat.get("CJXH") + "");
				parameters.put("WZJG", wzjg);
				parameters.put("LSJG", lsjg);
				parameters.put("CJXH", cjxh);
				Integer wzxh = (Integer) mat.get("WZXH");
				parameters.put("WZXH", wzxh.longValue());
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCommit(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_RK02");
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_RK01");

		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			mat.put("KFXH", rk.get("KFXH"));
			mat.put("KSDM", rk.get("KSDM"));
			mat.put("ZBLB", rk.get("ZBLB"));
			mat.put("YWRQ", rk.get("RKRQ"));
			mat.put("ZRKS", rk.get("DWXH"));
			mat.put("GHDW", rk.get("DWXH"));
			mat.put("GLXH", rk.get("THDJ"));
			if (rk.get("PDDJ") != null && rk.get("PDDJ") != ""
					&& Long.parseLong(rk.get("PDDJ") + "") > 0) {
				mat.put("YWFS", 1);
			} else {
				mat.put("YWFS", 0);
			}
		}

		if (BSPHISUtil.Uf_access(mats, Long.parseLong((rk.get("LZFS") + "")),
				dao, ctx)) {
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			try {
				dao.doSave("update", BSPHISEntryNames.WL_RK01, parameters,
						false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (ValidateException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
			}

		}
	}

	public void doGetKtslByKcxh(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long kcxh = Long.parseLong(body.get("KCXH") + "");
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KCXH", kcxh);
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != ""
				&& !"null".equals(user.getProperty("treasuryId"))) {
			parameters.put("KFXH",
					Integer.parseInt(user.getProperty("treasuryId") + ""));
		} else {
			parameters.put("KFXH", 0);
		}
		try {
			Map<String, Object> map = dao
					.doLoad("select (WZSL-NVL(YKSL,0) ) as KTSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
							parameters);
			if (map == null || map.size() == 0 || map.get("KTSL") == null) {
				res.put("ktsl", 0D);
				return;
			}
			res.put("ktsl", (Double) map.get("KTSL"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "可退数量获取失败");
		}
	}

	public String getLzdh() {
		String lzdh = "RK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}
}
