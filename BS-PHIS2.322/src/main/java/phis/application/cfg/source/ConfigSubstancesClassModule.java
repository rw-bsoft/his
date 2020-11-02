package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigSubstancesClassModule {
	protected BaseDAO dao;

	public ConfigSubstancesClassModule() {
	}

	public ConfigSubstancesClassModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据主键查询物质分类树
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void queryNodInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String FLBM = req.get("ZDXH") + "";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FLBM", FLBM);
		parameters.put("JGID", manaUnitId);
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT t.ZDXH as ZDXH,t.LBXH as LBXH,t.FLMC as FLMC ,t.FLBM as FLBM,t.GZXH as GZXH,t.SJFL as SJFL from WL_FLZD t where t.JGID =:JGID and t.FLBM =:FLBM ");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		res.put("ret", inofList.get(0));
	}

	/**
	 * 类别序号
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryLBXH(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> reqMap = (Map<String, Object>) req.get("body");
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT b.GZXH as GZXH ,b.GZCD as GZCD from WL_FLLB a, WL_FLGZ b where a.LBXH=b.LBXH and a.LBXH=:LBXH and b.GZCC =:GZCC");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		if (inofList.size() <= 0) {
			res.put("ret", 0);
		} else {
			res.put("ret", inofList.get(0));
		}
	}

	/**
	 * 保存分类字典树
	 * 
	 * @param req
	 * @param res
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSubstancesClassTree(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ValidateException {
		Map<String, Object> treeMap = (Map<String, Object>) req.get("body");
		String op = req.get("op") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String MSG = "保存失败！";
		try {
			// 得到规则序号
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT b.GZXH as GZXH from WL_FLLB a, WL_FLGZ b where a.LBXH=b.LBXH and a.LBXH=:LBXH ");
			Map<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("LBXH", treeMap.get("LBXH"));
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), reqMap);
			int GZXH = 0;
			if (inofList.size() > 0) {
				GZXH = Integer.parseInt(inofList.get(0).get("GZXH") + "");
			} else {
				MSG = "没有维护规则序号！";
				throw new RuntimeException("没有维护规则序号");
			}

			// 判断不能重复
			StringBuffer sql_list_cf = new StringBuffer(
					"SELECT DISTINCT ZDXH as ZDXH,LBXH as LBXH,FLMC as FLMC,GZXH as GZXH,FLBM as FLBM,SJFL as SJFL from WL_FLZD where FLBM=:FLBM and JGID=:JGID ");
			Map<String, Object> reqMap_cf = new HashMap<String, Object>();
			reqMap_cf.put("FLBM", treeMap.get("FLBM"));
			reqMap_cf.put("JGID", manaUnitId);
			List<Map<String, Object>> inofList_cf = dao.doSqlQuery(
					sql_list_cf.toString(), reqMap_cf);

			if (treeMap.get("ZDXH") == null) { // 新增的时候
				if (inofList_cf.size() > 0) {
					MSG = "物资分类重复，请查看！";
					throw new RuntimeException("物资分类重复，请查看");
				}
			} else { // 修改
				long ZDXH = Long.parseLong(treeMap.get("ZDXH") + "");
				if (inofList_cf.size() > 0) {
					for (int i = 0; i < inofList_cf.size(); i++) {
						long ZDXH_x = Long.parseLong(inofList_cf.get(i).get(
								"ZDXH")
								+ "");
						if (ZDXH_x != ZDXH) {
							MSG = "物资分类重复，请查看！";
							throw new RuntimeException("物资分类重复，请查看");
						} else if (inofList_cf.size() > 1) {
							MSG = "物资分类重复，请查看！";
							throw new RuntimeException("物资分类重复，请查看");
						}
					}
				}
			}
			if ("create".equals(op)) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FLMC", treeMap.get("FLMC"));
				parameters.put("FLBM", treeMap.get("FLBM"));
				parameters.put("LBXH",
						Integer.parseInt(treeMap.get("LBXH") + ""));
				parameters.put("GZXH", GZXH);
				parameters.put("SJFL",
						Integer.parseInt(treeMap.get("SJFL") + ""));
				parameters.put("JGID", manaUnitId);
				dao.doSave(op, BSPHISEntryNames.WL_FLZD, parameters, false);
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters
						.put("ZDXH", Long.parseLong(treeMap.get("ZDXH") + ""));
				parameters
						.put("SJFL", Long.parseLong(treeMap.get("SJFL") + ""));
				parameters.put("FLMC", treeMap.get("FLMC"));
				parameters.put("FLBM", treeMap.get("FLBM"));
				dao.doSave(op, BSPHISEntryNames.WL_FLZD, parameters, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, MSG);
		}
	}

	/**
	 * 根据主键删除树节点
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doDeleteNode(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long nodeId = 0l;
		if (req.containsKey("nodeId")) {
			if (req.get("nodeId") != null) {
				nodeId = Long.parseLong(req.get("nodeId") + "");
			}
		}
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null) {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}

		Map<String, Object> perMap = new HashMap<String, Object>();
		perMap.put("ZDXH", nodeId);
		try {

			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t.FLBM as FLBM ,t.LBXH as LBXH FROM WL_FLZD t ");
			sql_list.append("WHERE t.ZDXH =:ZDXH ");
			List<Map<String, Object>> inofList_FLBM = dao.doSqlQuery(
					sql_list.toString(), perMap);
			String FLBM = inofList_FLBM.get(0).get("FLBM") + "";
			long LBXH = 0l;
			if (inofList_FLBM.get(0).containsKey("LBXH")) {
				if (inofList_FLBM.get(0).get("LBXH") != null) {
					LBXH = Integer.parseInt(inofList_FLBM.get(0).get("LBXH")
							+ "");
				}
			}

			perMap.clear();
			perMap.put("LBXH", LBXH);
			perMap.put("KFXH", KFXH);

			StringBuffer sql_count = new StringBuffer(
					"SELECT DISTINCT  t2.FLXH as FLXH,t2.LBXH as LBXH, t2.ZDXH as ZDXH,t1.WZXH as WZXH,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,t1.PYDM as PYDM, t1.WZZT as WZZT,t1.KWBH as KWBH,t1.BKBZ as BKBZ,t1.YCWC as YCWC,t1.JLBZ  as JLBZ FROM ");
			sql_count
					.append(" WL_WZGS t,WL_WZZD t1 LEFT OUTER JOIN WL_WZFL t2 ON (t1.WZXH = t2.WZXH),WL_FLZD t3  ");
			sql_count
					.append("WHERE ( t.WZXH = t1.WZXH ) AND ( t2.ZDXH = t3.ZDXH ) AND(t.KFXH = :KFXH) ");
			sql_count.append(" AND (t3.FLBM Like '" + FLBM + "%' )");
			sql_count.append(" AND (t2.LBXH = :LBXH ) ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_count.toString(), perMap);

			if (inofList.size() > 0) {
				throw new RuntimeException("此节点下有明细，不能删除！");
			} else {
				dao.doRemove(nodeId, BSPHISEntryNames.WL_FLZD);
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}

	}

	/**
	 * 物质分类--已分类物质查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doAlreadyClassQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		String treasuryLbxh = user.getProperty("treasuryLbxh") + "";
		if (KFXH == null) {
			throw new RuntimeException("请先选择库房！");
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		long ZDXH = Long.parseLong(req.get("ZDXH") + "");

		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("KFXH", KFXH);
		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  t2.FLXH as FLXH,t2.LBXH as LBXH, t2.ZDXH as ZDXH,t1.WZXH as WZXH,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,t1.PYDM as PYDM, t1.WZZT as WZZT,t1.KWBH as KWBH,t1.BKBZ as BKBZ,t1.YCWC as YCWC,t1.JLBZ as JLBZ,t1.ZBLB as ZBLB,t1.HSLB as HSLB  FROM ");
			sql_list.append(" WL_WZGS t,WL_WZZD t1 LEFT OUTER JOIN WL_WZFL t2 ON (t1.WZXH = t2.WZXH),WL_FLZD t3  ");
			sql_list.append("WHERE ( t.WZXH = t1.WZXH ) AND ( t2.ZDXH = t3.ZDXH ) AND(t.KFXH = :KFXH) ");
			// 加上模糊查询
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and t1."
						+ que[2].substring(3, que[2].indexOf("]")) + " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql_list.append(" " + qur);
			}

			if (treasuryLbxh.equals("0")) {

			} else if (ZDXH > -2) {
				resMap = dao.doLoad(BSPHISEntryNames.WL_FLZD, ZDXH);
				if (!resMap.get("FLBM").equals("-1")) {
					// reqMap.put("FLBM", resMap.get("FLBM"));
					reqMap.put("LBXH", resMap.get("LBXH"));
					sql_list.append(" AND (t3.FLBM Like '" + resMap.get("FLBM")
							+ "%' )");
					sql_list.append(" AND (t2.LBXH = :LBXH ) ");
				}
			}

			StringBuffer sqlLbxh = new StringBuffer(
					"SELECT DISTINCT  t1.WZXH as WZXH,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,t1.PYDM as PYDM, t1.WZZT as WZZT,t1.KWBH as KWBH,t1.BKBZ as BKBZ,t1.YCWC as YCWC,t1.JLBZ as JLBZ,t1.ZBLB as ZBLB,t1.HSLB as HSLB FROM ");
			sqlLbxh.append(" WL_WZGS t, WL_WZZD t1 ,WL_HSLB t2 ");
			sqlLbxh.append("WHERE ( t.WZXH = t1.WZXH ) and (t2.HSLB = t1.HSLB) AND(t.KFXH = :KFXH) ");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and t1."
						+ que[2].substring(3, que[2].indexOf("]")) + " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sqlLbxh.append(" " + qur);
			}

			List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
			if (treasuryLbxh.equals("0")) {
				if (ZDXH > -2) {
					// reqMap.put("HSLB", ZDXH);
					sqlLbxh.append(" AND (t2.HSBM Like '%" + ZDXH + "%' )");
				}
				inofList = dao.doSqlQuery(sqlLbxh.toString(), reqMap);
			} else {
				inofList = dao.doSqlQuery(sql_list.toString(), reqMap);
			}
			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZZD_FLWH);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取已分类物资信息列表失败！");
		}
	}

	/**
	 * 物质分类--未分类物质查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doAlreadyNoClassQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		String LBXH = user.getProperty("treasuryLbxh") + "";
		if (KFXH == null) {
			throw new RuntimeException("请先选择库房！");
		}
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("KFXH", KFXH);
		reqMap.put("LBXH", LBXH);
		try {
			// 返回list的查询语句
			/*
			 * StringBuffer sql_list = new StringBuffer(
			 * "SELECT DISTINCT  t2.FLXH as FLXH,t2.LBXH as LBXH, t2.ZDXH as ZDXH,t1.WZXH as WZXH,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,t1.PYDM as PYDM, t1.WZZT as WZZT,t1.KWBH as KWBH,t1.BKBZ as BKBZ,t1.YCWC as YCWC,t1.JLBZ  as JLBZ FROM "
			 * ); sql_list.append(
			 * " WL_WZZD t1,WL_WZGS t LEFT OUTER JOIN WL_WZFL t2 ON  ((t.WZXH = t2.WZXH) AND (t.JGID = t2.JGID)) "
			 * ); sql_list.append(
			 * " WHERE ( t.WZXH = t1.WZXH ) AND ( t2.ZDXH Is Null ) AND(t.KFXH = :KFXH) "
			 * );
			 */
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZXH as WZXH,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,t1.PYDM as PYDM, t1.WZZT as WZZT,t1.KWBH as KWBH,t1.BKBZ as BKBZ,t1.YCWC as YCWC,t1.JLBZ  as JLBZ,t1.ZBLB as ZBLB,t1.HSLB as HSLB FROM ");
			sql_list.append(" WL_WZZD t1,WL_WZGS t  ");
			sql_list.append(" WHERE ( t.WZXH = t1.WZXH ) AND ((t.WZXH NOT IN (SELECT t3.WZXH  FROM WL_WZFL t3) AND 0< :LBXH ) OR (0> :LBXH )) AND(t.KFXH = :KFXH) ");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), reqMap);

			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.cfg.schemas.WL_WZZD");
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取未分类物资信息列表失败！");
		}
	}

}
