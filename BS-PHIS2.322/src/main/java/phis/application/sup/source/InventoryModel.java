package phis.application.sup.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class InventoryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(InventoryModel.class);

	public InventoryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 科室权限分配的科室信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetWzkcInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		String kcpdlx = ParameterUtil.getParameter(manaUnitId, "KCPDLX" + kfxh,
				"0", "库存盘点类型: 0.按库存盘点1.按批次盘点", ctx);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();

		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer();
		if ("0".equals(kcpdlx)) {
			sql.append(
					"SELECT a.JGID as JGID,a.KFXH as KFXH,a.WZXH as WZXH,a.CJXH as CJXH,0 as KCXH,c.CJMC as CJMC,a.WZJG as WZJG,a.LSJG as LSJG,'' as SCRQ,'' as SXRQ,'' as WZPH,'' as MJPH,b.GLFS as GLFS,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,SUM(a.WZSL) as KCSL,SUM(a.WZJE) as KCJE,sum(a.LSJE) as LSJE from ")
					.append("WL_WZKC")
					.append(" a,")
					.append("WL_WZZD")
					.append(" b,")
					.append("WL_SCCJ")
					.append(" c where a.KFXH =:KFXH AND b.WZXH = a.WZXH and a.CJXH=c.CJXH AND b.GLFS IN (1,2) GROUP BY a.JGID,a.KFXH,a.WZXH,a.CJXH,c.CJMC,a.WZJG,a.LSJG,b.GLFS,b.WZMC,b.WZGG,b.WZDW,b.PYDM,b.WBDM,b.JXDM,b.QTDM,b.HSLB ORDER BY a.WZXH");
		} else if ("1".equals(kcpdlx)) {
			sql.append(
					"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.KFXH as KFXH,a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.MJPH as MJPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.WZSL as KCSL,a.WZJG as WZJG,a.WZJE as KCJE,a.LSJG as LSJG,a.LSJE as LSJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS from ")
					.append("WL_WZKC")
					.append(" a,")
					.append("WL_WZZD")
					.append(" b,")
					.append("WL_SCCJ")
					.append(" c where a.WZXH = b.WZXH and a.CJXH=c.CJXH and a.KFXH=:KFXH and b.GLFS in (1,2)");
		}
		parameters.put("KFXH", kfxh);
		// parameterssize.put("KFXH", kfxh);
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_PD02 + "_KC");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
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
	public void doGetPD02Info(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.DJXH as DJXH,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.PCSL as PCSL,a.WZJG as WZJG,a.LSJG as LSJG,a.PCJE as PCJE,a.KCSL as KCSL,a.KCJE as KCJE,a.LSJE as LSJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS,b.ZBLB as ZBLB from WL_PD02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH = b.WZXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_PD02 + "_KC");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
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
	public void doGetPD02KSInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.KSDM as KSDM,a.DJXH as DJXH,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.PCSL as PCSL,a.WZJG as WZJG,a.PCJE as PCJE,a.KCSL as KCSL,a.KCJE as KCJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS,b.ZBLB as ZBLB from WL_PD02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH = b.WZXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_PD02 + "_KS");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
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
	public void doGetPD02TZInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"SELECT a.JLXH as JLXH,a.KCXH as KCXH,a.ZBXH as ZBXH,a.KSDM as KSDM,a.DJXH as DJXH,a.WZXH as WZXH,a.CJXH as CJXH,c.CJMC as CJMC,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.PCSL as PCSL,a.WZJG as WZJG,a.PCJE as PCJE,a.KCSL as KCSL,a.KCJE as KCJE,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB,b.GLFS as GLFS,b.ZBLB as ZBLB,d.WZBH as WZBH,a.CZBZ as CZBZ,d.WZZT as WZZT from WL_PD02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b,WL_ZCZB d where a.WZXH = b.WZXH AND a.ZBXH = d.ZBXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_PD02 + "_TZ");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetInventoryKSInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
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
		parameters.put("KFXH", kfxh);
		parameterssize.put("KFXH", kfxh);
		parameters.put("JGID", jgid);
		parameterssize.put("JGID", jgid);
		StringBuffer sql = new StringBuffer(
				"SELECT DISTINCT a.KSDM as KSDM,b.OFFICENAME as KSMC,b.PYCODE as PYDM,0 as XZBZ FROM WL_KSZC a LEFT JOIN SYS_Office b ON b.ID = a.KSDM WHERE a.WZXH IN (SELECT WZXH From WL_WZZD Where GLFS = 2 ) AND a.JGID=:JGID AND a.KFXH=:KFXH");
		try {
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and b.PYCODE like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim()
								.toLowerCase() + "'";
				sql.append(" " + qur);
			}
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryKSZC(List<Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH", kfxh);
				String KSDM = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> kszcMap = dao
						.doSqlQuery(
								"SELECT a.JLXH as JLXH,a.JGID as JGID,a.KSDM as KSDM,a.KFXH as KFXH,a.ZBLB as ZBLB,a.KCXH as KCXH,a.WZXH as WZXH,a.CJXH as CJXH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.WZPH as WZPH,a.WZSL as KCSL,a.WZJG as WZJG,a.WZJE as KCJE,a.ZRRQ as ZRRQ,a.YKSL as YKSL,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM, b.ZBLB as ZBLB,b.HSLB as HSLB,b.GLFS as GLFS FROM WL_KSZC a LEFT JOIN WL_WZZD b ON b.WZXH = a.WZXH WHERE a.KFXH=:KFXH and b.GLFS = 2 and a.KSDM in ("
										+ KSDM + ")", parameters);

				SchemaUtil.setDictionaryMassageForList(kszcMap,
						BSPHISEntryNames.WL_PD02 + "_KS");
				res.put("body", kszcMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetInventoryTZKSInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
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
		parameters.put("KFXH", kfxh);
		parameterssize.put("KFXH", kfxh);
		parameters.put("JGID", jgid);
		parameterssize.put("JGID", jgid);
		StringBuffer sql = new StringBuffer(
				"SELECT DISTINCT a.ZYKS as ZYKS,b.ID as KSDM,b.OFFICENAME as KSMC,b.PYCODE as PYDM,0 as  XZBZ FROM WL_ZCZB a LEFT JOIN SYS_Office b ON b.ID = a.ZYKS WHERE (b.ID <> 0 OR b.ID IS NOT NULL) AND a.JGID=:JGID AND a.KFXH=:KFXH");
		try {
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and b.PYCODE like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim()
								.toLowerCase() + "'";
				sql.append(" " + qur);
			}
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryZCZB(List<Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH", kfxh);
				String KSDM = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> kszcMap = dao
						.doSqlQuery(
								"SELECT a.ZBXH as ZBXH,a.KFXH as KFXH,a.ZBLB as ZBLB,a.WZBH as WZBH,a.WZXH as WZXH,a.CJXH as CJXH,a.KCXH as KCXH,a.ZCYZ as ZCYZ,a.CZYZ as CZYZ,a.ZYKS as KSDM,a.WZZT as WZZT,a.TZRQ as TZRQ,a.ZDTM as ZDTM,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,b.HSLB as HSLB FROM WL_ZCZB a,WL_WZZD b WHERE a.KFXH=:KFXH AND a.WZXH = b.WZXH AND a.WZZT >= -1 AND a.ZYKS in ("
										+ KSDM + ")", parameters);
				SchemaUtil.setDictionaryMassageForList(kszcMap,
						BSPHISEntryNames.WL_PD02 + "_TZ");
				res.put("body", kszcMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetInventoryWZZDInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
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
		parameters.put("KFXH", kfxh);
		parameterssize.put("KFXH", kfxh);
		StringBuffer sql = new StringBuffer(
				"select DISTINCT a.WZXH as WZXH,a.HSLB as HSLB,a.WZMC as WZMC,a.WZGG as WZGG,a.PYDM as PYDM,d.CJMC as CJMC,nvl(c.WZJG,0) as WZJG,nvl(c.LSJG,0) as LSJG,a.GLFS as GLFS,0 as KCSL from WL_WZZD a,WL_WZGS b,WL_WZCJ c,WL_SCCJ d where a.WZXH=b.WZXH and a.WZXH=c.WZXH and c.CJXH=d.CJXH and b.KFXH=:KFXH and a.GLFS<>3");
		try {
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and b."
						+ que[2].substring(3, que[2].indexOf("]")) + " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql.append(" " + qur);
			}
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryWZZD(List<Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String WZXH = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> kszcMap = dao
						.doSqlQuery(
								"select DISTINCT a.WZXH as WZXH,c.CJXH as CJXH,a.HSLB as HSLB,a.WZMC as WZMC,a.WZGG as WZGG,a.PYDM as PYDM,d.CJMC as CJMC,nvl(c.WZJG,0) as WZJG,nvl(c.LSJG,0) as LSJG,a.GLFS as GLFS,0 as KCSL from WL_WZZD a,WL_WZGS b,WL_WZCJ c,WL_SCCJ d where a.WZXH=b.WZXH and a.WZXH=c.WZXH and c.CJXH=d.CJXH and a.WZXH in ("
										+ WZXH + ")", parameters);
				SchemaUtil.setDictionaryMassageForList(kszcMap,
						BSPHISEntryNames.WL_PD02 + "_KC");
				res.put("body", kszcMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-17
	 * @description 保存盘点记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventory(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Long kfxh = Long.parseLong(user.getProperty("treasuryId") + "");// 用户的机构ID
		Map<String, Object> updatekfxx = new HashMap<String, Object>();
		Map<String, Object> pd = (Map<String, Object>) body.get("WL_PD01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("WL_PD02");
		try {
			if ("create".equals(op)) {
				pd.put("ZDGH", user.getUserId() + "");
				pd.put("ZDRQ", new Date());
				pd.put("JGID", jgid);
				Map<String, Object> djlxmap = dao.doSave("create",
						BSPHISEntryNames.WL_PD01, pd, false);
				Long djxh = Long.parseLong(djlxmap.get("DJXH") + "");
				for (int i = 0; i < meds.size(); i++) {
					meds.get(i).put("DJXH", djxh);
					dao.doSave("create", BSPHISEntryNames.WL_PD02, meds.get(i),
							false);
				}
				updatekfxx.put("KFXH", kfxh);
				updatekfxx.put("PDZT", 1);
				dao.doUpdate("update WL_KFXX set PDZT=:PDZT where KFXH=:KFXH",
						updatekfxx);
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", Long.parseLong(pd.get("DJXH") + ""));
				Long l = dao.doCount("WL_LRPD", "PDXH=:DJXH and DJZT=1",
						parameters);
				if (l > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "该盘点单已录入提交,不能修改");
				}
				StringBuffer deleteHql = new StringBuffer();
				deleteHql.append("delete from ").append("WL_PD02")
						.append(" where DJXH=:DJXH");
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.WL_PD01, pd, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("DJXH", Long.parseLong(pd.get("DJXH") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_PD02, med, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage record keeping validation fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records Save failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点记录保存失败");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-17
	 * @description 保存盘点记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	public void doSaveInventoryCommit(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String uid = user.getUserId() + "";
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterslzfs = new HashMap<String, Object>();
		Long rklzfs = 0L;
		Long cklzfs = 0L;
		Long djxh = 0L;
		Long djxhrk = 0L;
		Long djxhck = 0L;
		int pdlx = 1;
		if (body.get("DJXH") != null) {
			djxh = Long.parseLong(body.get("DJXH") + "");
		}
		if (body.get("PDLX") != null) {
			pdlx = Integer.parseInt(body.get("PDLX") + "");
		}
		parameters.put("DJXH", djxh);
		parameterslzfs.put("KFXH", kfxh);
		List<Map<String, Object>> list_rk = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list_ck = new ArrayList<Map<String, Object>>();
		List<Long> djxhrkList = new ArrayList<Long>();
		List<Long> djxhckList = new ArrayList<Long>();
		try {
			if (pdlx == 1) {
				List<Map<String, Object>> pyList = dao
						.doQuery(
								"select a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.LSJG as LSJG,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,(a.PCSL-a.KCSL) as WZSL,(a.PCSL-a.KCSL)*a.WZJG as WZJE,(a.PCSL-a.KCSL)*a.LSJG as LSJE,a.WZPH as WZPH,b.ZBLB as ZBLB from WL_PD02 a,WL_WZZD b where a.WZXH=b.WZXH and a.DJXH=:DJXH and a.PCSL>a.KCSL order by b.ZBLB",
								parameters);
				List<Map<String, Object>> pkList = dao
						.doQuery(
								"select a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.LSJG as LSJG,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,(a.KCSL-a.PCSL) as WZSL,(a.KCSL-a.PCSL)*a.WZJG as WZJE,(a.KCSL-a.PCSL)*a.LSJG as LSJE,a.WZPH as WZPH,a.KSDM as KSDM,b.ZBLB as ZBLB from WL_PD02 a,WL_WZZD b where a.WZXH=b.WZXH and a.DJXH=:DJXH and a.PCSL<a.KCSL order by b.ZBLB",
								parameters);
				List<Map<String, Object>> rklzfsList = dao
						.doQuery(
								"select FSXH as FSXH from WL_LZFS where DJLX='RK' and YWLB=1 and KFXH=:KFXH and TSBZ=1 and DJQZ='PY' order by FSXH",
								parameterslzfs);
				if (rklzfsList.size() > 0) {
					rklzfs = Long.parseLong(rklzfsList.get(0).get("FSXH") + "");
				}
				List<Map<String, Object>> cklzfsList = dao
						.doQuery(
								"select FSXH as FSXH from WL_LZFS where DJLX='CK' and YWLB=-1 and KFXH=:KFXH and TSBZ=1 and DJQZ='PK' order by FSXH",
								parameterslzfs);
				if (cklzfsList.size() > 0) {
					cklzfs = Long.parseLong(cklzfsList.get(0).get("FSXH") + "");
				}
				for (int i = 0; i < pyList.size(); i++) {
					Map<String, Object> map_rk = pyList.get(i);
					Long kcxh = BSPHISUtil.setKCXH(dao);
					map_rk.put("KCXH", kcxh);
					for (int j = 0; j < list_rk.size(); j++) {
						Map<String, Object> map_ls_rk = list_rk.get(j);
						if (map_ls_rk.get("ZBLB").equals(map_rk.get("ZBLB"))) {
							map_rk.put("DJXH", map_ls_rk.get("DJXH"));
						}
					}
					if (map_rk.get("DJXH") == null) {
						Map<String, Object> rk01Map = new HashMap<String, Object>();
						rk01Map.put("KFXH", kfxh);
						rk01Map.put("ZBLB", Integer.parseInt(pyList.get(i).get(
								"ZBLB")
								+ ""));
						rk01Map.put("JGID", jgid);
						rk01Map.put("LZFS", rklzfs);
						rk01Map.put("RKRQ", new Date());
						rk01Map.put("ZDGH", uid);
						rk01Map.put("ZDRQ", new Date());
						rk01Map.put("DJZT", 0);
						rk01Map.put("DRBZ", 0);
						rk01Map.put("DJLX", 3);
						rk01Map.put("PDDJ", djxh);
						Map<String, Object> rk01Req;
						rk01Req = dao.doSave("create",
								BSPHISEntryNames.WL_RK01, rk01Map, false);
						djxhrk = Long.parseLong(rk01Req.get("DJXH") + "");// 获取主键
						djxhrkList.add(djxhrk);
						// 保存主表获得的主键;
						Map<String, Object> map_l_rk = new HashMap<String, Object>();
						map_l_rk.put("ZBLB", map_rk.get("ZBLB"));// 把YJZH放到map_1中
						map_l_rk.put("DJXH", djxhrk);// 把YJXH放到map_1中
						list_rk.add(map_l_rk);// 把这些值放到list_中
						if (pyList.get(i).get("WZPH") != null) {
							map_rk.put("WZPH", pyList.get(i).get("WZPH") + "");
						} else {
							map_rk.put("WZPH", "");
						}
						map_rk.put("MJPH", "");
						map_rk.put("DJXH", djxhrk);// 把YJXH设置到要保存的明细中
					}
					// 接着保存明细
					dao.doSave("create", BSPHISEntryNames.WL_RK02, map_rk,
							false);
				}
				for (int i = 0; i < pkList.size(); i++) {
					Map<String, Object> map_ck = pkList.get(i);
					for (int j = 0; j < list_ck.size(); j++) {
						Map<String, Object> map_ls_ck = list_ck.get(j);
						if (map_ls_ck.get("ZBLB").equals(map_ck.get("ZBLB"))) {
							map_ck.put("DJXH", map_ls_ck.get("DJXH"));
						}
					}
					if (map_ck.get("DJXH") == null) {
						Map<String, Object> ck01Map = new HashMap<String, Object>();
						ck01Map.put("KFXH", kfxh);
						ck01Map.put("ZBLB", Integer.parseInt(pkList.get(i).get(
								"ZBLB")
								+ ""));
						ck01Map.put("JGID", jgid);
						ck01Map.put("LZFS", cklzfs);
						ck01Map.put("CKRQ", new Date());
						ck01Map.put("ZDGH", uid);
						ck01Map.put("ZDRQ", new Date());
						ck01Map.put("DJZT", 0);
						ck01Map.put("DJLX", 3);
						ck01Map.put("PDDJ", djxh);
						Map<String, Object> ck01Req;
						ck01Req = dao.doSave("create",
								BSPHISEntryNames.WL_CK01, ck01Map, false);
						djxhck = Long.parseLong(ck01Req.get("DJXH") + "");// 获取主键
						djxhckList.add(djxhck);
						// 保存主表获得的主键;
						Map<String, Object> map_l_ck = new HashMap<String, Object>();
						map_l_ck.put("ZBLB", map_ck.get("ZBLB"));// 把YJZH放到map_1中
						map_l_ck.put("DJXH", djxhck);// 把YJXH放到map_1中
						list_ck.add(map_l_ck);// 把这些值放到list_中
						if (pkList.get(i).get("WZPH") != null) {
							map_ck.put("WZPH", pkList.get(i).get("WZPH") + "");
						} else {
							map_ck.put("WZPH", "");
						}
						map_ck.put("MJPH", "");
						map_ck.put("DJXH", djxhck);// 把YJXH设置到要保存的明细中
					}
					// 接着保存明细
					dao.doSave("create", BSPHISEntryNames.WL_CK02, map_ck,
							false);
				}
			}
			Map<String, Object> updatepd02 = new HashMap<String, Object>();
			updatepd02.put("DJXH", djxh);
			updatepd02.put("JZRQ", new Date());
			updatepd02.put("JZGH", uid);
			updatepd02.put("DJZT", 2);
			dao.doUpdate(
					"update WL_PD01 set JZRQ=:JZRQ,JZGH=:JZGH,DJZT=:DJZT where DJXH=:DJXH",
					updatepd02);
			Map<String, Object> getDJZT = new HashMap<String, Object>();
			getDJZT.put("KFXH", kfxh);
			Long l = dao.doCount("WL_PD01", "KFXH=:KFXH and DJZT<>2", getDJZT);
			if (l <= 0) {
				Map<String, Object> updatekfxx = new HashMap<String, Object>();
				updatekfxx.put("KFXH", Long.parseLong(kfxh + ""));
				updatekfxx.put("PDZT", 0);
				dao.doUpdate("update WL_KFXX set PDZT=:PDZT where KFXH=:KFXH",
						updatekfxx);
				res.put(Service.RES_CODE, 601);
			}
			doUpdateDJJE(djxhrkList, djxhckList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-17
	 * @description 保存盘点记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	public void doSaveInventoryEjCommit(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String uid = user.getUserId() + "";
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterslzfs = new HashMap<String, Object>();
		List<Long> djxhrkejList = new ArrayList<Long>();
		List<Long> djxhckejList = new ArrayList<Long>();
		Long rklzfs = 0L;
		Long cklzfs = 0L;
		Long djxh = 0L;
		Long djxhrk = 0L;
		Long djxhck = 0L;
		if (body.get("DJXH") != null) {
			djxh = Long.parseLong(body.get("DJXH") + "");
		}
		parameters.put("DJXH", djxh);
		parameterslzfs.put("KFXH", kfxh);
		try {
			List<Map<String, Object>> pyList = dao
					.doQuery(
							"select a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.LSJG as LSJG,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,(a.PCSL-a.KCSL) as WZSL,(a.PCSL-a.KCSL)*a.WZJG as WZJE,(a.PCSL-a.KCSL)*a.LSJG as LSJE,a.WZPH as WZPH,b.ZBLB as ZBLB from WL_PD02 a,WL_WZZD b where a.WZXH=b.WZXH and a.DJXH=:DJXH and a.PCSL>a.KCSL order by b.ZBLB",
							parameters);
			List<Map<String, Object>> pkList = dao
					.doQuery(
							"select a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.LSJG as LSJG,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,(a.KCSL-a.PCSL) as WZSL,(a.KCSL-a.PCSL)*a.WZJG as WZJE,(a.KCSL-a.PCSL)*a.LSJG as LSJE,a.WZPH as WZPH,a.KSDM as KSDM,b.ZBLB as ZBLB from WL_PD02 a,WL_WZZD b where a.WZXH=b.WZXH and a.DJXH=:DJXH and a.PCSL<a.KCSL order by b.ZBLB",
							parameters);
			List<Map<String, Object>> rklzfsList = dao
					.doQuery(
							"select FSXH as FSXH from WL_LZFS where DJLX='RK' and YWLB=1 and KFXH=:KFXH and TSBZ=1 and DJQZ='PY' order by FSXH",
							parameterslzfs);
			if (rklzfsList.size() > 0) {
				rklzfs = Long.parseLong(rklzfsList.get(0).get("FSXH") + "");
			}
			List<Map<String, Object>> cklzfsList = dao
					.doQuery(
							"select FSXH as FSXH from WL_LZFS where DJLX='CK' and YWLB=-1 and KFXH=:KFXH and TSBZ=1 and DJQZ='PK' order by FSXH",
							parameterslzfs);
			if (cklzfsList.size() > 0) {
				cklzfs = Long.parseLong(cklzfsList.get(0).get("FSXH") + "");
			}
			if (pyList.size() > 0) {
				Map<String, Object> rk01Map = new HashMap<String, Object>();
				rk01Map.put("KFXH", kfxh);
				rk01Map.put("ZBLB", 0);
				rk01Map.put("JGID", jgid);
				rk01Map.put("LZFS", rklzfs);
				rk01Map.put("RKRQ", new Date());
				rk01Map.put("ZDGH", uid);
				rk01Map.put("ZDRQ", new Date());
				rk01Map.put("DJZT", 0);
				rk01Map.put("DRBZ", 0);
				rk01Map.put("DJLX", 3);
				rk01Map.put("PDDJ", djxh);
				Map<String, Object> rk01Req;
				rk01Req = dao.doSave("create", BSPHISEntryNames.WL_RK01,
						rk01Map, false);
				djxhrk = Long.parseLong(rk01Req.get("DJXH") + "");// 获取主键
				djxhrkejList.add(djxhrk);
				for (int i = 0; i < pyList.size(); i++) {
					pyList.get(i).put("DJXH", djxhrk);
					Long kcxh = BSPHISUtil.setKCXH(dao);
					pyList.get(i).put("KCXH", kcxh);
					if (pyList.get(i).get("WZPH") != null) {
						pyList.get(i).put("WZPH",
								pyList.get(i).get("WZPH") + "");
					} else {
						pyList.get(i).put("WZPH", "");
					}
					pyList.get(i).put("MJPH", "");
					dao.doSave("create", BSPHISEntryNames.WL_RK02,
							pyList.get(i), false);
				}
			}
			if (pkList.size() > 0) {
				Map<String, Object> ck01Map = new HashMap<String, Object>();
				ck01Map.put("KFXH", kfxh);
				ck01Map.put("ZBLB", 0);
				ck01Map.put("JGID", jgid);
				ck01Map.put("LZFS", cklzfs);
				ck01Map.put("CKRQ", new Date());
				ck01Map.put("ZDGH", uid);
				ck01Map.put("ZDRQ", new Date());
				ck01Map.put("DJZT", 0);
				ck01Map.put("DJLX", 3);
				ck01Map.put("PDDJ", djxh);
				Map<String, Object> ck01Req;
				ck01Req = dao.doSave("create", BSPHISEntryNames.WL_CK01,
						ck01Map, false);
				djxhck = Long.parseLong(ck01Req.get("DJXH") + "");// 获取主键
				djxhckejList.add(djxhck);
				for (int i = 0; i < pkList.size(); i++) {
					pkList.get(i).put("DJXH", djxhck);
					if (pkList.get(i).get("WZPH") != null) {
						pkList.get(i).put("WZPH",
								pkList.get(i).get("WZPH") + "");
					} else {
						pkList.get(i).put("WZPH", "");
					}
					pkList.get(i).put("MJPH", "");
					dao.doSave("create", BSPHISEntryNames.WL_CK02,
							pkList.get(i), false);
				}
			}
			Map<String, Object> updatepd02 = new HashMap<String, Object>();
			updatepd02.put("DJXH", djxh);
			updatepd02.put("JZRQ", new Date());
			updatepd02.put("JZGH", uid);
			updatepd02.put("DJZT", 2);
			dao.doUpdate(
					"update WL_PD01 set JZRQ=:JZRQ,JZGH=:JZGH,DJZT=:DJZT where DJXH=:DJXH",
					updatepd02);
			Map<String, Object> getDJZT = new HashMap<String, Object>();
			getDJZT.put("KFXH", kfxh);
			Long l = dao.doCount("WL_PD01", "KFXH=:KFXH and DJZT<>2", getDJZT);
			if (l <= 0) {
				Map<String, Object> updatekfxx = new HashMap<String, Object>();
				updatekfxx.put("KFXH", Long.parseLong(kfxh + ""));
				updatekfxx.put("PDZT", 0);
				dao.doUpdate("update WL_KFXX set PDZT=:PDZT where KFXH=:KFXH",
						updatekfxx);
				res.put(Service.RES_CODE, 602);
			}
			doUpdateDJJEEJ(djxhrkejList, djxhckejList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryDJZT(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> djztMap = dao.doLoad(
					"select DJZT as DJZT from " + "WL_PD01"
							+ " where DJXH=:DJXH", parameters);
			res.put("djzt", djztMap.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doGetInventoryDJSH(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("PDXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Long l = dao
					.doCount("WL_LRPD", "PDXH=:PDXH and DJZT=1", parameters);
			if (l <= 0) {
				res.put(Service.RES_CODE, 603);
			} else {
				res.put(Service.RES_CODE, 606);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doGetInventoryDJSHKS(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		parameters.put("PDXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			List<Map<String, Object>> kslist = dao
					.doSqlQuery(
							"select JLXH as JLXH from WL_PD02 where DJXH =:DJXH and KSDM not in (select KSDM from WL_LRPD a,WL_LRMX b where a.LRXH=b.LRXH and a.PDXH =:PDXH and a.DJZT=1)",
							parameters);
			if (kslist.size() > 0) {
				res.put(Service.RES_CODE, 604);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doQueryPDLR(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Long l = dao
					.doCount("WL_LRPD", "PDXH=:DJXH and DJZT=1", parameters);
			if (l <= 0) {
				res.put(Service.RES_CODE, 602);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doSaveInventoryVerify(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parametersupdsh = new HashMap<String, Object>();
		Map<String, Object> parametersupdpdlr = new HashMap<String, Object>();
		Map<String, Object> parametersgetpcslandpcje = new HashMap<String, Object>();
		Map<String, Object> parametersupdpd02 = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			parametersgetpcslandpcje.put("PDXH",
					Long.parseLong(body.get("DJXH") + ""));
			List<Map<String, Object>> lrmxList = dao
					.doQuery(
							"select b.PCSL as PCSL,b.PCJE as PCJE,b.LSJE as LSJE,b.MXXH as MXXH,b.PDBZ as PDBZ from WL_LRPD a,WL_LRMX b where a.LRXH=b.LRXH and a.PDXH=:PDXH and a.DJZT=1 and b.MXXH is not null",
							parametersgetpcslandpcje);
			for (int i = 0; i < lrmxList.size(); i++) {
				parametersupdpd02.put("JLXH",
						Long.parseLong(lrmxList.get(i).get("MXXH") + ""));
				if (lrmxList.get(i).get("PCSL") != null) {
					parametersupdpd02.put("PCSL", Double.parseDouble(lrmxList
							.get(i).get("PCSL") + ""));
				} else {
					parametersupdpd02.put("PCSL", 0.00);
				}
				if (lrmxList.get(i).get("PCJE") != null) {
					parametersupdpd02.put("PCJE", Double.parseDouble(lrmxList
							.get(i).get("PCJE") + ""));
				} else {
					parametersupdpd02.put("PCJE", 0.00);
				}
				if (lrmxList.get(i).get("LSJE") != null) {
					parametersupdpd02.put("LSJE", Double.parseDouble(lrmxList
							.get(i).get("LSJE") + ""));
				} else {
					parametersupdpd02.put("LSJE", 0.00);
				}
				if (lrmxList.get(i).get("PDBZ") != null) {
					parametersupdpd02.put("PDBZ",
							Integer.parseInt(lrmxList.get(i).get("PDBZ") + ""));
				} else {
					parametersupdpd02.put("PDBZ", 0);
				}
				dao.doUpdate(
						"update WL_PD02 set PCSL=:PCSL,PCJE=:PCJE,LSJE=:LSJE,CZBZ=:PDBZ where JLXH=:JLXH",
						parametersupdpd02);
			}
			List<Map<String, Object>> lrmxmxxhnullList = dao
					.doQuery(
							"select b.PCSL as PCSL,b.PCJE as PCJE,a.PDXH as DJXH,0 as KCSL,0 as KCJE,1 as LRBZ,b.WZXH as WZXH,b.CJXH as CJXH,b.WZJG as WZJG,b.LSJG as LSJG,b.LSJE as LSJE,b.PDBZ as PDBZ from WL_LRPD a,WL_LRMX b where a.LRXH=b.LRXH and a.PDXH=:PDXH and a.DJZT=1 and b.MXXH is null",
							parametersgetpcslandpcje);
			for (int i = 0; i < lrmxmxxhnullList.size(); i++) {
				dao.doSave("create", BSPHISEntryNames.WL_PD02,
						lrmxmxxhnullList.get(i), false);
			}
			parametersupdsh.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
			parametersupdsh.put("SHGH", user.getUserId() + "");
			parametersupdsh.put("SHRQ", new Date());
			parametersupdsh.put("LZDH", getLzdh());
			dao.doUpdate(
					"update "
							+ "WL_PD01"
							+ " set LZDH=:LZDH,SHRQ=:SHRQ,SHGH=:SHGH,DJZT=1 where DJXH=:DJXH",
					parametersupdsh);
			parametersupdpdlr
					.put("PDXH", Long.parseLong(body.get("DJXH") + ""));
			dao.doUpdate(
					"update " + "WL_LRPD" + " set DJZT=2 where PDXH=:PDXH",
					parametersupdpdlr);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点单审核失败!");
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	public void doSaveInventoryNoVerify(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parametersupdsh = new HashMap<String, Object>();
		Map<String, Object> parameterwzxh = new HashMap<String, Object>();
		Map<String, Object> parametersupdpdlr = new HashMap<String, Object>();
		try {
			parametersupdsh.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
			parameterwzxh.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
			List<Map<String, Object>> lrmxmxxhnullList = dao
					.doQuery(
							"select b.WZXH as WZXH from WL_LRPD a,WL_LRMX b where a.LRXH=b.LRXH and a.PDXH=:DJXH and a.DJZT=2 and b.MXXH is null",
							parametersupdsh);
			for (int i = 0; i < lrmxmxxhnullList.size(); i++) {
				parameterwzxh.put("WZXH", Long.parseLong(lrmxmxxhnullList
						.get(i).get("WZXH") + ""));
				dao.doUpdate(
						"delete from WL_PD02 where DJXH=:DJXH and WZXH=:WZXH",
						parameterwzxh);
			}
			dao.doUpdate("update " + "WL_PD01"
					+ " set SHRQ=null,SHGH=null,DJZT=0 where DJXH=:DJXH",
					parametersupdsh);
			parametersupdpdlr
					.put("PDXH", Long.parseLong(body.get("DJXH") + ""));
			dao.doUpdate(
					"update " + "WL_LRPD" + " set DJZT=1 where PDXH=:PDXH",
					parametersupdpdlr);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点单弃审失败!");
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
	public void doGetPDGLInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();

		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		String sql = "SELECT b.DJXH as PDXH,b.JLXH as MXXH,b.DJXH as DJXH,b.WZXH as WZXH,b.CJXH as CJXH,b.WZJG as WZJG,b.LSJG as LSJG,b.KCSL as KCSL,b.KCJE as KCJE,b.PCSL as PCSL,b.PCJE as PCJE,b.LSJE as LSJE,b.WZPH as WZPH,b.SCRQ as SCRQ,b.SXRQ as SXRQ,b.KCXH as KCXH,b.ZBXH as ZBXH, b.KSDM as KSDM,c.ZBLB as ZBLB,c.WZMC as WZMC,c.WZGG as WZGG,c.WZDW as WZDW,c.HSLB as HSLB,c.PYDM as PYDM,c.WBDM as WBDM,c.JXDM as JXDM,c.QTDM  as QTDM,c.GLFS as GLFS  FROM WL_PD01 a, WL_PD02 b,WL_WZZD c WHERE a.DJXH=b.DJXH and a.DJZT = 0 and a.KFXH=:KFXH and a.GLFS=1 AND b.WZXH = c.WZXH and a.DJXH not in (select PDXH from WL_LRPD) ORDER BY c.WZMC,c.WZGG,c.WZDW";
		parameters.put("KFXH", kfxh);
		// parameterssize.put("KFXH", kfxh);
		try {
			list = dao.doSqlQuery(sql, parameters);
			// listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_LRMX + "_KC");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryPD02KS(List<Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH", kfxh);
				String KSDM = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> kszcMap = dao
						.doSqlQuery(
								"SELECT a.JLXH as MXXH,a.DJXH as PDXH,a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.KCSL as KCSL,a.KCJE as KCJE,a.PCSL as PCSL,a.PCJE as PCJE,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,a.KSDM as KSDM,b.ZBLB as ZBLB,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,b.HSLB as HSLB,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM FROM WL_PD02 a,WL_WZZD b,WL_PD01 c WHERE a.WZXH = b.WZXH AND a.DJXH = c.DJXH and a.DJXH = c.DJXH AND c.GLFS = 2 AND c.DJZT < 2  AND c.KFXH =:KFXH AND a.KSDM in ("
										+ KSDM
										+ ") and a.DJXH not in (select PDXH from WL_LRPD) ORDER BY b.WZMC ASC, b.WZGG ASC, b.WZDW ASC",
								parameters);

				SchemaUtil.setDictionaryMassageForList(kszcMap,
						BSPHISEntryNames.WL_LRMX + "_KS");
				res.put("body", kszcMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		}
	}

	public void doGetInventoryPD02TZ(List<Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long kfxh = Integer
				.parseInt(user.getProperty("treasuryId") == null ? "0" : user
						.getProperty("treasuryId") + "");
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KFXH", kfxh);
				String KSDM = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> kszcMap = dao
						.doSqlQuery(
								"SELECT a.JLXH as MXXH,a.DJXH as PDXH,a.WZXH as WZXH,a.CJXH as CJXH,a.WZJG as WZJG,a.KCSL as KCSL,a.KCJE as KCJE,a.PCSL as PCSL,a.PCJE as PCJE,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,c.WZBH as WZBH,c.TZRQ as TZRQ,a.KSDM as KSDM,c.WZZT as WZZT,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,0 as CZPB FROM WL_PD02 a,WL_WZZD b, WL_ZCZB c,WL_PD01 d WHERE a.KSDM in ("
										+ KSDM
										+ ") AND a.WZXH = b.WZXH AND a.ZBXH = c.ZBXH AND a.DJXH = d.DJXH AND d.GLFS = 3 AND d.DJZT < 2  AND d.KFXH =:KFXH and a.DJXH not in (select PDXH from WL_LRPD)",
								parameters);
				SchemaUtil.setDictionaryMassageForList(kszcMap,
						BSPHISEntryNames.WL_LRMX + "_TZ");
				res.put("body", kszcMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-17
	 * @description 保存盘点记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveInventoryIn(Map<String, Object> body, String op,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> pd = (Map<String, Object>) body.get("WL_LRPD");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("WL_LRMX");
		try {
			if ("create".equals(op)) {
				pd.put("ZDGH", user.getUserId() + "");
				pd.put("ZDSJ", new Date());
				pd.put("JGID", jgid);
				if (meds.size() > 0) {
					if (meds.get(0).get("PDXH") != null) {
						pd.put("PDXH",
								Long.parseLong(meds.get(0).get("PDXH") + ""));
					}
				}
				Map<String, Object> djlxmap = dao.doSave("create",
						BSPHISEntryNames.WL_LRPD, pd, false);
				Long lrxh = Long.parseLong(djlxmap.get("LRXH") + "");
				for (int i = 0; i < meds.size(); i++) {
					meds.get(i).put("LRXH", lrxh);
					dao.doSave("create", BSPHISEntryNames.WL_LRMX, meds.get(i),
							false);
				}
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("LRXH", Long.parseLong(pd.get("LRXH") + ""));
				StringBuffer deleteHql = new StringBuffer();
				deleteHql.append("delete from ").append("WL_LRMX")
						.append(" where LRXH=:LRXH");
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.WL_LRPD, pd, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("LRXH", Long.parseLong(pd.get("LRXH") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_LRMX, med, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage record keeping validation fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records Save failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "盘点记录保存失败");
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
	public void doGetLRMXInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select a.JLXH as JLXH,a.LRXH as LRXH,a.PDXH as PDXH,a.MXXH as MXXH,a.WZXH as WZXH,a.CJXH as CJXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,a.WZJG as WZJG,a.LSJG as LSJG,a.KCSL as KCSL,a.PCSL as PCSL,a.PCJE as PCJE,a.LSJE as LSJE,c.CJMC as CJMC,a.KSDM as KSDM,a.PDBZ as PDBZ from WL_LRMX a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH=b.WZXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_LRMX + "_KC");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
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
	public void doGetLRMXKSInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select a.JLXH as JLXH,a.LRXH as LRXH,a.PDXH as PDXH,a.KSDM as KSDM,a.MXXH as MXXH,a.WZXH as WZXH,a.CJXH as CJXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,a.WZJG as WZJG,a.KCSL as KCSL,a.PCSL as PCSL,a.PCJE as PCJE,c.CJMC as CJMC,a.PDBZ as PDBZ from WL_LRMX a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH=b.WZXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_LRMX + "_KS");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
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
	public void doGetLRMXTZInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// List<Map<String, Object>> listsize = new ArrayList<Map<String,
		// Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		// Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		// int pageSize = 25;
		// if (req.containsKey("pageSize")) {
		// pageSize = (Integer) req.get("pageSize");
		// }
		// int pageNo = 0;
		// if (req.containsKey("pageNo")) {
		// pageNo = (Integer) req.get("pageNo") - 1;
		// }
		// parameters.put("first", pageNo * pageSize);
		// parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select a.JLXH as JLXH,a.LRXH as LRXH,a.PDXH as PDXH,a.KSDM as KSDM,a.PDBZ as PDBZ,a.MXXH as MXXH,a.WZXH as WZXH,a.CJXH as CJXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.WZPH as WZPH,a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.KCXH as KCXH,a.ZBXH as ZBXH,a.WZJG as WZJG,a.KCSL as KCSL,a.PCSL as PCSL,a.PCJE as PCJE,c.CJMC as CJMC,d.WZBH as WZBH,d.WZZT as WZZT from WL_LRMX a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b,WL_ZCZB d where a.WZXH=b.WZXH AND a.ZBXH = d.ZBXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			// listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_LRMX + "_TZ");
			// res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetInventoryInDJZT(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LRXH", Long.parseLong(body.get("LRXH") + ""));
		try {
			Map<String, Object> djztMap = dao.doLoad(
					"select DJZT as DJZT from " + "WL_LRPD"
							+ " where LRXH=:LRXH", parameters);
			res.put("djzt", djztMap.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doSaveSubitVerify(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LRXH", Long.parseLong(body.get("LRXH") + ""));
		parameters.put("SCSJ", new Date());
		try {
			dao.doUpdate("update " + "WL_LRPD"
					+ " set SCSJ=:SCSJ,DJZT=1 where LRXH=:LRXH", parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public String getLzdh() {
		String lzdh = "PD";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryPDXX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", kfxh);
		parameters.put("GLFS", Integer.parseInt(req.get("GLFS") + ""));

		try {
			Long l = dao.doCount("WL_PD01",
					"KFXH=:KFXH and DJZT<2 and GLFS=:GLFS", parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 600);
			} else {
				res.put(Service.RES_CODE, 700);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryPDXXEJ(Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& !"".equals(user.getProperty("treasuryId"))) {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", kfxh);
		try {
			Long l = dao
					.doCount("WL_PD01", "KFXH=:KFXH and DJZT<2", parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 605);
			} else {
				res.put(Service.RES_CODE, 701);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryPDLRXX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("PDXH", Long.parseLong(req.get("DJXH") + ""));
		try {
			Long l = dao.doCount("WL_LRPD", "PDXH=:PDXH", parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 601);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doRemoveInventory(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersPD = new HashMap<String, Object>();
		Map<String, Object> parameterskfxxupd = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		parameters.put("DJXH", Long.parseLong(req.get("DJXH") + ""));
		parametersPD.put("KFXH", kfxh);
		parameterskfxxupd.put("KFXH", Long.parseLong(kfxh + ""));
		try {
			dao.doUpdate("delete from WL_PD01 where DJXH=:DJXH", parameters);
			dao.doUpdate("delete from WL_PD02 where DJXH=:DJXH", parameters);
			Long l = dao.doCount("WL_PD01", "KFXH=:KFXH and DJZT<>2",
					parametersPD);
			if (l <= 0) {
				dao.doUpdate("update WL_KFXX set PDZT=0 WHERE KFXH=:KFXH",
						parameterskfxxupd);
				res.put(Service.RES_CODE, 102);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doRemoveInventoryIn(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LRXH", Long.parseLong(req.get("LRXH") + ""));
		try {
			dao.doUpdate("delete from WL_LRPD where LRXH=:LRXH", parameters);
			dao.doUpdate("delete from WL_LRMX where LRXH=:LRXH", parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doUpdateDJJE(List<Long> djxhrul, List<Long> djxhcul)
			throws ModelDataOperationException {
		try {
			for (int i = 0; i < djxhrul.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> parametersupd = new HashMap<String, Object>();
				parameters.put("DJXH", djxhrul.get(i));
				parametersupd.put("DJXH", djxhrul.get(i));
				Map<String, Object> wzjeMap = dao
						.doLoad("select sum(WZJE) as WZJE from WL_RK02 where DJXH=:DJXH",
								parameters);
				if (wzjeMap != null) {
					if (wzjeMap.get("WZJE") != null) {
						parametersupd.put("DJJE",
								Double.parseDouble(wzjeMap.get("WZJE") + ""));
					}
				}
				dao.doUpdate("update WL_RK01 set DJJE=:DJJE where DJXH=:DJXH",
						parametersupd);
			}
			for (int i = 0; i < djxhcul.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> parametersupd = new HashMap<String, Object>();
				parameters.put("DJXH", djxhcul.get(i));
				parametersupd.put("DJXH", djxhcul.get(i));
				Map<String, Object> wzjeMap = dao
						.doLoad("select sum(WZJE) as WZJE from WL_CK02 where DJXH=:DJXH",
								parameters);
				if (wzjeMap != null) {
					if (wzjeMap.get("WZJE") != null) {
						parametersupd.put("DJJE",
								Double.parseDouble(wzjeMap.get("WZJE") + ""));
					}
				}
				dao.doUpdate("update WL_CK01 set DJJE=:DJJE where DJXH=:DJXH",
						parametersupd);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doUpdateDJJEEJ(List<Long> djxhruejl, List<Long> djxhcuejl)
			throws ModelDataOperationException {
		try {
			for (int i = 0; i < djxhruejl.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> parametersupd = new HashMap<String, Object>();
				parameters.put("DJXH", djxhruejl.get(i));
				parametersupd.put("DJXH", djxhruejl.get(i));
				Map<String, Object> wzjeMap = dao
						.doLoad("select sum(WZJE) as WZJE from WL_RK02 where DJXH=:DJXH",
								parameters);
				if (wzjeMap != null) {
					if (wzjeMap.get("WZJE") != null) {
						parametersupd.put("DJJE",
								Double.parseDouble(wzjeMap.get("WZJE") + ""));
					}
				}
				dao.doUpdate("update WL_RK01 set DJJE=:DJJE where DJXH=:DJXH",
						parametersupd);
			}
			for (int i = 0; i < djxhcuejl.size(); i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				Map<String, Object> parametersupd = new HashMap<String, Object>();
				parameters.put("DJXH", djxhcuejl.get(i));
				parametersupd.put("DJXH", djxhcuejl.get(i));
				Map<String, Object> wzjeMap = dao
						.doLoad("select sum(WZJE) as WZJE from WL_CK02 where DJXH=:DJXH",
								parameters);
				if (wzjeMap != null) {
					if (wzjeMap.get("WZJE") != null) {
						parametersupd.put("DJJE",
								Double.parseDouble(wzjeMap.get("WZJE") + ""));
					}
				}
				dao.doUpdate("update WL_CK01 set DJJE=:DJJE where DJXH=:DJXH",
						parametersupd);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询科室账册里的科室代码数据
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryDJSH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = 0;
		int kfjd = Integer.parseInt(req.get("KFDJ") + "");
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", kfxh);
		int sign = this.queryDjzt("WL_RK01", parameters);
		if (sign == 1) {
			res.put(Service.RES_CODE, 801);
		}
		if (sign == 0) {
			sign = this.queryDjzt("WL_CK01", parameters);
			if (sign == 1) {
				res.put(Service.RES_CODE, 802);
			}
			if (sign == 2) {
				res.put(Service.RES_CODE, 804);
			}
			if (sign == 3) {
				res.put(Service.RES_CODE, 806);
			}
		}
		if (kfjd == 1) {
			if (sign == 0) {
				sign = this.queryDjzt("WL_BS01", parameters);
				if (sign == 1) {
					res.put(Service.RES_CODE, 803);
				}
			}
		} else if (kfjd == 2) {
			if (sign == 0) {
				sign = this.queryDjzt("WL_XHMX", parameters);
				if (sign == 1) {
					res.put(Service.RES_CODE, 805);
				}
			}
		}
	}

	public int queryDjzt(String tableName, Map<String, Object> parameters) {
		int sign = 0;
		try {
			if (!"WL_XHMX".equals(tableName)) {
				if (!"WL_CK01".equals(tableName)) {
					List<Map<String, Object>> djztList = dao.doQuery(
							"select DJZT as DJZT from " + tableName
									+ " where KFXH=:KFXH", parameters);
					for (int i = 0; i < djztList.size(); i++) {
						if (Integer.parseInt(djztList.get(i).get("DJZT") + "") == 1) {
							sign = 1;
							break;
						}
					}
				} else {
					List<Map<String, Object>> djztList = dao
							.doQuery(
									"select DJZT as DJZT from "
											+ tableName
											+ " where KFXH=:KFXH and DJLX<>7 and DJLX<>6",
									parameters);
					for (int i = 0; i < djztList.size(); i++) {
						if (Integer.parseInt(djztList.get(i).get("DJZT") + "") == 1) {
							sign = 1;
							break;
						}
					}
					if (sign == 0) {
						List<Map<String, Object>> djztListdb = dao.doQuery(
								"select DJZT as DJZT from " + tableName
										+ " where KFXH=:KFXH and DJLX=7",
								parameters);
						for (int i = 0; i < djztListdb.size(); i++) {
							if (Integer.parseInt(djztListdb.get(i).get("DJZT")
									+ "") == 1) {
								sign = 2;
								break;
							}
						}
					}
					if (sign == 0) {
						List<Map<String, Object>> djztListsl = dao.doQuery(
								"select DJZT as DJZT from " + tableName
										+ " where KFXH=:KFXH and DJLX=6",
								parameters);
						for (int i = 0; i < djztListsl.size(); i++) {
							if (Integer.parseInt(djztListsl.get(i).get("DJZT")
									+ "") == 1) {
								sign = 3;
								break;
							}
						}
					}
				}
			} else {
				long l = dao.doCount(tableName,
						"KFXH=:KFXH and ZTBZ=0 and DJXH is null", parameters);
				if (l > 0) {
					sign = 1;
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return sign;
	}
}
