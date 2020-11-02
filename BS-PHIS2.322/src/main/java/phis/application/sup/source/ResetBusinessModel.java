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

public class ResetBusinessModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ResetBusinessModel.class);

	public ResetBusinessModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		
		Map<String, Object> cZ01_Map = (Map<String, Object>) body.get("WL_CZ01");
		List<Map<String, Object>> cZ02_List = (List<Map<String, Object>>) body.get("WL_CZ02");
		cZ01_Map.put("KFXH", user.getProperty("treasuryId"));
		cZ01_Map.put("ZDRQ", new Date());
		cZ01_Map.put("ZDGH", user.getUserId());
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,BSPHISEntryNames.WL_CZ01, cZ01_Map, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < cZ02_List.size(); i++) {
					Map<String, Object> mat = cZ02_List.get(i);
					mat.put("DJXH", DJXH);
					dao.doSave("create", BSPHISEntryNames.WL_CZ02, mat, false);
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_CZ01, cZ01_Map, false);
				dao.removeByFieldValue("DJXH", Long.parseLong(cZ01_Map.get("DJXH")+""),BSPHISEntryNames.WL_CZ02);
				for (int i = 0; i < cZ02_List.size(); i++) {
					Map<String, Object> mat = cZ02_List.get(i);
					mat.put("DJXH", cZ01_Map.get("DJXH"));
					dao.doSave("create", BSPHISEntryNames.WL_CZ02, mat, false);
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
					ServiceCode.CODE_DATABASE_ERROR,"入库记录保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_CZ01,Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				throw new RuntimeException("该物资已被删除！");
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "该物资已被删除！");
		}
	}

	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException, ValidateException {
		List<Map<String, Object>> list_cz02 = (List<Map<String, Object>>) body.get("WL_CZ02");
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_CZ01");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		parameters.put("LZDH", getLzdh());
		parameters.put("SHGH", user.getUserId());
		parameters.put("DJZT", 1);
		parameters.put("SHRQ", new Date());
		parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE  WL_CZ01 SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
		try {
			dao.doUpdate(hql.toString(), parameters);
			for (int i = 0; i < list_cz02.size(); i++) {
				long ZBXH = Long.parseLong(list_cz02.get(i).get("ZBXH")+"");
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("ZBXH", ZBXH);
				record.put("CLBZ", 1);
				dao.doSave("update", BSPHISEntryNames.WL_ZCZB, record, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException, ValidateException {
		List<Map<String, Object>> list_cz02 = (List<Map<String, Object>>) body.get("WL_CZ02");
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_CZ01");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LZDH", null);
		parameters.put("DJZT", 0);
		parameters.put("SHGH", null);
		parameters.put("SHRQ", null);
		parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
		StringBuffer hql = new StringBuffer();
		hql.append("UPDATE WL_CZ01 SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
		try {
			dao.doUpdate(hql.toString(), parameters);
			for (int i = 0; i < list_cz02.size(); i++) {
				long ZBXH = Long.parseLong(list_cz02.get(i).get("ZBXH")+"");
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("ZBXH", ZBXH);
				record.put("CLBZ",0);
				dao.doSave("update", BSPHISEntryNames.WL_ZCZB, record, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "弃审失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body.get("WL_CZ02");
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_CZ01");

		if (BSPHISUtil.Uf_access(mats, Long.parseLong((rk.get("LZFS") + "")),dao, ctx)) {
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			try {
				dao.doSave("update", BSPHISEntryNames.WL_CZ01, parameters,false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (ValidateException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,"入库记录保存失败");
			}

		}
	}
	
	public String getLzdh() {
		String lzdh = "CZ";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}
	
	@SuppressWarnings("unchecked")
	public void doDelete(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("body") != null) {
			body = (Map<String, Object>) req.get("body");
		}
		Long DJXH = Long.parseLong(body.get("DJXH") + "");
		try {
			dao.doRemove(DJXH, BSPHISEntryNames.WL_CZ01);
			dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_CZ02);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}
	
	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		try {
			List<Map<String, Object>> list = dao.doQuery("select KFZB as KFZB from WL_KFXX  where KFXH=:KFXH",
					parameters);
			String kfzb = (String) list.get(0).get("KFZB");
			parameters.clear();
			list = dao.doSqlQuery("select ZBLB,ZBMC from WL_ZBLB  where ZBLB in(" + kfzb
					+ ") order by ZBLB", parameters);
			res.put("list", list);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取账簿类中的库房序号失败！");
		}
	}

}
