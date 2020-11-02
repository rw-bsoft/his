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
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 转科管理Model
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class TransferManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TransferManagementModel.class);

	public TransferManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> zk = (Map<String, Object>) body.get("WL_ZK01");

		zk.put("KFXH", user.getProperty("treasuryId"));
		zk.put("ZDRQ", new Date());
		zk.put("ZDGH", user.getUserId());
		zk.put("JGID", user.getManageUnit().getId());

		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_ZK02");
		double djje = 0.0;
		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			djje += Double.parseDouble(mat.get("WZJE").toString());
		}
		zk.put("DJJE", djje);
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_ZK01, zk, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", DJXH);
					dao.doSave("create",BSPHISEntryNames.WL_ZK02, mat, false);
					
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_ZK01, zk, false);
				dao.removeByFieldValue("DJXH",
						Long.parseLong(zk.get("DJXH") + ""),
						BSPHISEntryNames.WL_ZK02);
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", Long.parseLong(zk.get("DJXH") + ""));
					dao.doSave("create",BSPHISEntryNames.WL_ZK02, mat, false);
					
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		try {
			List<Map<String, Object>> list = dao.doQuery("from " + "WL_KFXX"
					+ " where KFXH=:KFXH", parameters);
			String kfzb = (String) list.get(0).get("KFZB");
			parameters.clear();
			list = dao.doSqlQuery("select ZBLB,ZBMC from " + "WL_ZBLB"
					+ " where ZBLB in(" + kfzb + ") order by ZBLB", parameters);
			res.put("list", list);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_ZK01,
					Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				return;
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	public void doGetCjxhByWzxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", Long.parseLong(body.get("WZXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad("select CJXH as CJXH from WL_WZCJ where WZXH=:WZXH",
					parameters);
			if (map == null || map.size() == 0) {
				return;
			}
			res.put("cjxh", map.get("CJXH"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> zk = (Map<String, Object>) body.get("WL_ZK01");
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_ZK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {

			// 如果物资管理方式为2，则根据转出科室和库存序号在科室账册中预扣数量加上本次转科数量。如果物资管理方式为3，则根据zbxh设置资产中的CLBZ为1。
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.clear();
				if (Integer.parseInt(mat.get("GLFS") + "") == 2) {
					parameters.put("ZCKS", Long.parseLong(zk.get("ZCKS") + ""));
					parameters
							.put("KCXH", Long.parseLong(mat.get("KCXH") + ""));
					parameters.put("WZSL",
							Double.parseDouble(mat.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_KSZC set YKSL=YKSL+:WZSL where KSDM=:ZCKS and KCXH=:KCXH",
							parameters);
				} else if (Integer.parseInt(mat.get("GLFS") + "") == 3) {
					parameters
							.put("ZBXH", Long.parseLong(mat.get("ZBXH") + ""));
					dao.doUpdate("update WL_ZCZB set CLBZ=1 where ZBXH=:ZBXH",
							parameters);
				}
			}
			parameters.clear();
			UserRoleToken user = UserRoleToken.getCurrent();
			parameters.put("LZDH", getLzdh());
			parameters.put("SHGH", user.getUserId() + "");
			parameters.put("DJZT", 1);
			parameters.put("SHRQ", new Date());
			parameters.put("DJXH", Long.parseLong(zk.get("DJXH").toString()));
			StringBuffer hql = new StringBuffer();
			hql.append("UPDATE ")
					.append("WL_ZK01")
					.append(" SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doIsEnoughInventory(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_ZK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select WZSL as WZSL from WL_WZKC where KCXH=:KCXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.put("KCXH", Long.parseLong(mat.get("KCXH") + ""));
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
			}
			res.put("isEnoughInventory", true);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> zk = (Map<String, Object>) body.get("WL_ZK01");
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_ZK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// 如果物资管理方式为2，则根据转出科室和库存序号在科室账册中预扣数量减去本次转科数量。如果物资管理方式为3，则根据zbxh设置资产中的CLBZ为0。
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.clear();
				if (Integer.parseInt(mat.get("GLFS") + "") == 2) {
					parameters.put("ZCKS", Long.parseLong(zk.get("ZCKS") + ""));
					parameters
							.put("KCXH", Long.parseLong(mat.get("KCXH") + ""));
					parameters.put("WZSL",
							Double.parseDouble(mat.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_KSZC set YKSL=YKSL-:WZSL where KSDM=:ZCKS and KCXH=:KCXH",
							parameters);
				} else if (Integer.parseInt(mat.get("GLFS") + "") == 3) {
					parameters
							.put("ZBXH", Long.parseLong(mat.get("ZBXH") + ""));
					dao.doUpdate("update WL_ZCZB set CLBZ=0 where ZBXH=:ZBXH",
							parameters);
				}
			}
			parameters.clear();
			parameters.put("LZDH", null);
			parameters.put("DJZT", 0);
			parameters.put("SHGH", null);
			parameters.put("SHRQ", null);
			parameters.put("DJXH", Long.parseLong(zk.get("DJXH").toString()));
			StringBuffer hql = new StringBuffer();
			hql.append("UPDATE ")
					.append("WL_ZK01")
					.append(" SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
			dao.doUpdate(hql.toString(), parameters);

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
				.get("WL_ZK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append(BSPHISEntryNames.WL_WZCJ)
				.append(" set WZJG=:WZJG where WZXH=:WZXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				Integer wzjg = (Integer) mat.get("WZJG");
				parameters.put("WZJG", wzjg.doubleValue());
				Integer wzxh = (Integer) mat.get("WZXH");
				parameters.put("WZXH", wzxh.longValue());
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_ZK02");
		Map<String, Object> zk = (Map<String, Object>) body.get("WL_ZK01");

		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			mat.put("KFXH", zk.get("KFXH"));
			mat.put("ZBLB", zk.get("ZBLB"));
			mat.put("ZRKS", zk.get("ZRKS"));
			mat.put("ZCKS", zk.get("ZCKS"));
		}

		if (BSPHISUtil.Uf_access(mats, Long.parseLong((zk.get("LZFS") + "")),
				dao, ctx)) {
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(zk.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			try {
				dao.doSave("update", BSPHISEntryNames.WL_ZK01, parameters, false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (ValidateException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "转科记录保存失败");
			}

		}
	}

	public void doGetKtslByKcxh(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long kcxh = Long.parseLong(body.get("KCXH") + "");
		User user = (User) ctx.get("user.instance");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KCXH", kcxh);
		parameters.put("KFXH",
				Integer.parseInt((String) user.getProperty("treasuryId")));
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
		String lzdh = "ZK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	@SuppressWarnings("unchecked")
	public void doKCQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("cnd") != null) {
			body = (Map<String, Object>) req.get("cnd");
		}
		Long ksdm = 0L;
		if (body.containsKey("KSDM")) {
			ksdm = Long.parseLong(body.get("KSDM") + "");
		}
		int zblb = 0;
		if (body.containsKey("ZBLB")) {
			zblb = (Integer) body.get("ZBLB");
		}

		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			parameters.put("ksdm", ksdm);
			parameters.put("zblb", zblb);
			// System.out.println(parameters.toString());

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"select null as wzbh,a.wzxh as wzxh,c.wzmc as wzmc,c.wzgg as wzgg,c.wzdw as wzdw,"
							+ " c.glfs as glfs,a.cjxh as cjxh,e.cjmc as cjmc,a.wzsl - a.yksl as wzsl,a.wzsl - a.yksl as tjsl,"
							+ " a.wzjg as wzjg,a.wzje as wzje,a.wzph as wzph,a.scrq as scrq,a.sxrq as sxrq,a.kcxh as kcxh,"
							+ " null as zbxh from wl_kszc a ,wl_wzzd c,wl_sccj e where  a.ksdm =:ksdm and a.zblb=:zblb and a.wzxh=c.wzxh and a.cjxh=e.cjxh and c.glfs=2 and (a.wzsl - a.yksl)>0"
							+ " union "
							+ "select b.wzbh as wzbh,b.wzxh as wzxh,d.wzmc as wzmc,d.wzgg as wzgg,d.wzdw as wzdw,"
							+ " d.glfs as glfs,b.cjxh as cjxh,f.cjmc as cjmc,1 as wzsl,1 as tjsl, b.czyz as wzjg, b.czyz as wzje,"
							+ " null as wzph,null as scrq,null as sxrq,b.kcxh as kcxh,b.zbxh as zbxh  from wl_zczb b,"
							+ " wl_wzzd d,wl_sccj f where b.zyks=:ksdm and b.zblb=:zblb and b.clbz=0 and b.WZZT=1 and b.wzxh=d.wzxh and b.cjxh=f.cjxh and d.glfs=3");
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("(" + sql_list.toString() + ")");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_ZK02_KC);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("引入获取数据失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "引入获取数据失败");
		}
	}

	public void doGetZK02Info(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long djxh = 0L;
		if (req.containsKey("DJXH")) {
			djxh = Long.parseLong(req.get("DJXH") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("djxh", djxh);

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"select a.jlxh as jlxh,a.djxh as djxh,"
							+ " e.wzsl-e.yksl as tjsl,"
							+ " a.wzxh as wzxh,c.wzmc as wzmc,c.wzgg as wzgg,c.wzdw as wzdw,c.glfs as glfs,"
							+ " a.cjxh as cjxh,b.cjmc as cjmc,a.wzsl as wzsl,a.wzjg as wzjg,a.wzje as wzje,a.wzph as wzph,"
							+ " a.scrq as scrq,a.sxrq as sxrq,a.kcxh as kcxh,a.zbxh as zbxh,a.wzbh as wzbh"
							+ " from wl_zk02 a, wl_sccj b, wl_wzzd c,wl_kszc e,wl_zk01 f where a.djxh = :djxh and a.wzxh = c.wzxh"
							+ " and a.cjxh = b.cjxh and a.kcxh=e.kcxh and c.glfs = 2 and a.djxh=f.djxh and e.ksdm=f.zcks"
							+ " union"
							+ " select a.jlxh as jlxh,a.djxh as djxh,"
							+ " 1 as tjsl,"
							+ " a.wzxh as wzxh,c.wzmc as wzmc,c.wzgg as wzgg,c.wzdw as wzdw,c.glfs as glfs,"
							+ " a.cjxh as cjxh,b.cjmc as cjmc,a.wzsl as wzsl,a.wzjg as wzjg,a.wzje as wzje,a.wzph as wzph,"
							+ " a.scrq as scrq,a.sxrq as sxrq,a.kcxh as kcxh,a.zbxh as zbxh,a.wzbh as wzbh"
							+ " from wl_zk02 a, wl_sccj b, wl_wzzd c where a.djxh = :djxh and a.wzxh = c.wzxh"
							+ " and a.cjxh = b.cjxh and c.glfs = 3");
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("(" + sql_list.toString() + ")");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> infoList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			for (Map<String, Object> infoMap : infoList) {
				if (infoMap.get("GLFS").equals("3")) {
					infoMap.put("TJSL", 1);
				}
			}
			SchemaUtil.setDictionaryMassageForList(infoList,
					BSPHISEntryNames.WL_ZK02);
			res.put("totalCount", total);
			res.put("body", infoList);
		} catch (PersistentDataOperationException e) {
			logger.error("库存列表查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存列表查询失败");
		}
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
			dao.doRemove(DJXH, BSPHISEntryNames.WL_ZK01);
			dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_ZK02);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}
}
