package phis.application.sup.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DaoSupport;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 采购计划Model
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ProcurementPlanModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ProcurementPlanModel.class);

	public ProcurementPlanModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> jh01 = (Map<String, Object>) body.get("WL_JH01");

		jh01.put("KFXH", user.getProperty("treasuryId"));
		jh01.put("ZDRQ", new Date());
		jh01.put("ZDGH", user.getUserId().toString());
		jh01.put("JGID", user.getManageUnit().getId() + "");
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_JH02");
		double djje = 0.0;
		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			if (mat.containsKey("WZJE")) {
				djje += Double.parseDouble(mat.get("WZJE").toString());
			}
		}
		jh01.put("DJJE", djje);
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_JH01, jh01, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", DJXH);
					dao.doSave("create", BSPHISEntryNames.WL_JH02, mat, false);

				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_JH01, jh01, false);
				dao.removeByFieldValue("DJXH",
						Long.parseLong(jh01.get("DJXH") + ""),
						BSPHISEntryNames.WL_JH02);
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", jh01.get("DJXH"));
					dao.doSave("create", BSPHISEntryNames.WL_JH02, mat, false);

				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "采购计划保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "采购计划保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_JH01,
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

	@SuppressWarnings("unchecked")
	public void doVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> jh01 = (Map<String, Object>) body.get("WL_JH01");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			parameters.put("LZDH", getLzdh());
			parameters.put("SHGH", user.getUserId());
			parameters.put("DJZT", 1);
			parameters.put("SHRQ", new Date());
			parameters.put("DJXH", Long.parseLong(jh01.get("DJXH").toString()));
			StringBuffer hql = new StringBuffer();
			hql.append("UPDATE WL_JH01 SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> jh01 = (Map<String, Object>) body.get("WL_JH01");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {

			parameters.put("LZDH", null);
			parameters.put("DJZT", 0);
			parameters.put("SHGH", null);
			parameters.put("SHRQ", null);
			parameters.put("DJXH", Long.parseLong(jh01.get("DJXH").toString()));
			StringBuffer hql = new StringBuffer();
			hql.append("UPDATE ")
					.append(BSPHISEntryNames.WL_JH01)
					.append(" SET LZDH=:LZDH,DJZT=:DJZT,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH");
			dao.doUpdate(hql.toString(), parameters);

		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "弃审失败");
		}
	}

	public String getLzdh() {
		String lzdh = "JH";
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

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"select a.wzxh as wzxh,c.wzmc as wzmc,c.wzgg as wzgg,c.wzdw as wzdw,"
							+ " c.glfs as glfs,a.cjxh as cjxh,e.cjmc as cjmc,a.wzsl - a.yksl as wzsl,a.wzsl - a.yksl as tjsl,"
							+ " a.wzjg as wzjg,a.wzje as wzje,a.wzph as wzph,a.scrq as scrq,a.sxrq as sxrq,a.kcxh as kcxh,"
							+ " null as zbxh from wl_kszc a ,wl_wzzd c,wl_sccj e where  a.ksdm =:ksdm and a.zblb=:zblb and a.wzxh=c.wzxh and a.cjxh=e.cjxh"
							+ " union "
							+ "select b.wzxh as wzxh,d.wzmc as wzmc,d.wzgg as wzgg,d.wzdw as wzdw,"
							+ " d.glfs as glfs,b.cjxh as cjxh,f.cjmc as cjmc,1 as wzsl,1 as tjsl,null as wzjg,null as wzje,"
							+ " null as wzph,null as scrq,null as sxrq,b.kcxh as kcxh,b.zbxh as zbxh  from wl_zczb b,"
							+ " wl_wzzd d,wl_sccj f where b.zyks=:ksdm and b.zblb=:zblb and b.clbz=0 and b.wzxh=d.wzxh and b.cjxh=f.cjxh");
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("(" + sql_list.toString() + ")");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_JH02);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("库存列表查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存列表查询失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doPlanImportQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			if (req.get("cnd") != null) {
				String[] djxhstr = req.get("cnd").toString().split(",");
				if (djxhstr.length > 3) {
					Long djxh = Long.parseLong(djxhstr[4]
							.toString()
							.trim()
							.substring(0,
									djxhstr[4].toString().trim().indexOf("]"))
							+ "");
					Map<String, Object> parMap = new HashMap<String, Object>();
					parMap.put("DJXH", djxh);
					List<Map<String, Object>> jhlist = dao
							.doQuery(
									"select JLXH as JLXH,WZMC as WZMC,WZGG as WZGG,WZDW as WZDW,WZSL as WZSL,KSDM as KSDM,SLSJ as SLSJ,DJXH as DJXH,ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,SLXH as SLXH,ZDBZ as ZDBZ,SQLY as SQLY,DJLX as DJLX from WL_JH02 where DJXH=:DJXH",
									parMap);
					SchemaUtil.setDictionaryMassageForList(jhlist,
							"phis.application.sup.schemas.WL_JH02");
					res.put("body", jhlist);
				}
			} else {
				if (req.get("cndw") != null) {
					body = (Map<String, Object>) req.get("cndw");
				}

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH", user.getProperty("treasuryId"));

				String ygdm = user.getUserId().toString();
				Map<String, Object> map_yg = dao.doLoad(
						BSPHISEntryNames.SYS_Personnel, ygdm);
				long ksdm = 0;
				if (map_yg.get("OFFICECODE") != null) {
					ksdm = Long.parseLong(map_yg.get("OFFICECODE") + "");
				}
				Date slsj = new Date();

				List<Map<String, Object>> infolist = new ArrayList<Map<String, Object>>();
				if (body.containsKey("1")) {
					// 方法1＝未确认的申领单
					StringBuffer sql_list = new StringBuffer(
							"SELECT a.ZBLB as zblb,a.KSDM as KSDM,"
									+ " b.SLXH as SLXH,b.WZXH as wzxh, b.CJXH as CJXH, c.WZMC as WZMC,c.WZGG as WZGG,"
									+ " c.WZDW as WZDW,b.WZSL as WZSL, b.JHBZ as JHBZ, d.SLSJ as SLSJ"
									+ " FROM WL_CK01 a,WL_CK02 b, WL_WZZD c,WL_SLXX d  WHERE a.KFXH =:KFXH "
									+ " And a.DJZT = 0 And a.DJXH = b.DJXH And b.WZXH = c.WZXH AND b.SLXH = d.JLXH "
									+ " AND nvl(b.JHBZ,0) <> 1 AND a.Djlx=6");
					List<Map<String, Object>> infolist_part = dao.doSqlQuery(
							sql_list.toString(), parameters);

					// 已经被引用的未确认申领单不能再引入
					List<Map<String, Object>> infolist_done = new ArrayList<Map<String, Object>>();
					Map<String, Object> parametersJH02 = new HashMap<String, Object>();
					for (int i = 0; i < infolist_part.size(); i++) {
						Map<String, Object> map_part = infolist_part.get(i);
						parametersJH02.put("SLXH",
								Long.parseLong(map_part.get("SLXH") + ""));
						Long count = dao.doCount("WL_JH02", "SLXH=:SLXH",
								parametersJH02);
						if (count == 0) {
							infolist_done.add(map_part);
						}
					}
					infolist.addAll(infolist_done);
				}
				if (body.containsKey("2")) {
					StringBuffer sql_list = new StringBuffer(
							"Select a.wzxh as wzxh,a.gcsl as gcsl,"
									+ " a.gcsl-nvl((select sum(wzsl) as wzsl from wl_wzkc c where c.wzxh=a.wzxh "
									+ " and c.kfxh=a.kfxh),0) as wzsl,(select max(zblb) as zblb from wl_wzkc c "
									+ " where c.wzxh=a.wzxh and c.kfxh=a.kfxh) as zblb,(select max(cjxh) as cjxh from "
									+ " wl_wzkc c where c.wzxh=a.wzxh and c.kfxh=a.kfxh) as cjxh,b.wzmc as wzmc,"
									+ " b.wzgg as wzgg,b.wzdw as wzdw From wl_kcyj a, wl_wzzd b Where a.wzxh = b.wzxh"
									+ " and b.glfs <> 3 And a.kfxh =:KFXH");
					List<Map<String, Object>> infolist_part = dao.doSqlQuery(
							sql_list.toString(), parameters);
					for (Map<String, Object> map : infolist_part) {
						Double wzsl = Double.parseDouble(map.get("WZSL") + "");
						if (wzsl > 0) {
							map.put("KSDM", ksdm);
							map.put("SLSJ", slsj);
							infolist.add(map);
						}
					}
				}
				if (body.containsKey("3")) {
					StringBuffer sql_list = new StringBuffer(
							"Select a.wzxh as wzxh,a.dcsl as dcsl,"
									+ " a.dcsl-nvl((select sum(wzsl) as wzsl from wl_wzkc c where c.wzxh=a.wzxh "
									+ " and c.kfxh=a.kfxh),0) as wzsl,(select max(zblb) as zblb from wl_wzkc c "
									+ " where c.wzxh=a.wzxh and c.kfxh=a.kfxh) as zblb,(select max(cjxh) as cjxh from "
									+ " wl_wzkc c where c.wzxh=a.wzxh and c.kfxh=a.kfxh) as cjxh,b.wzmc as wzmc,"
									+ " b.wzgg as wzgg,b.wzdw as wzdw From wl_kcyj a, wl_wzzd b Where a.wzxh = b.wzxh"
									+ " and b.glfs <> 3 And a.kfxh =:KFXH");
					List<Map<String, Object>> infolist_part = dao.doSqlQuery(
							sql_list.toString(), parameters);
					for (Map<String, Object> map : infolist_part) {
						Double wzsl = Double.parseDouble(map.get("WZSL") + "");
						if (wzsl > 0) {
							map.put("KSDM", ksdm);
							map.put("SLSJ", slsj);
							infolist.add(map);
						}
					}
				}

				SchemaUtil.setDictionaryMassageForList(infolist,
						"phis.application.sup.schemas.WL_JH02");
				res.put("body", infolist);
			}
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
			dao.doRemove(DJXH, BSPHISEntryNames.WL_JH01);
			dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_JH02);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}
}
