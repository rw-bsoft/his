package phis.application.sas.source;

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
import ctd.util.context.Context;

public class SuppliesStockSearchModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(SuppliesStockSearchModel.class);

	public SuppliesStockSearchModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 库存明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryStockDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		int ZBLB = Integer.parseInt(req.get("ZBLB") + "");

		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("ZBLB", ZBLB);
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,t.WZSL as WZSL,t.YKSL as YKSL,t.WZSL-t.YKSL as KYSL,t.WZJG as WZJG,t.WZJE as WZJE,t.SCRQ as SCRQ,");
			sql_list.append("t.SXRQ as SXRQ,t.WZPH as WZPH,t.MJPH as MJPH,t1.HSLB as HSLB,t.JLXH as JLXH,t.KCXH as KCXH,t.KFXH as KFXH,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM,t1.KWBH as KWBH,t.FSRQ as FSRQ FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH AND t.ZBLB =:ZBLB)");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = "";
				if (que[4].indexOf("]") == -1) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = "and ( t1.WZMC  like '" + parString
						+ "' or t1.PYDM LIKE '" + parString.toUpperCase() + "'"
						+ " or t1.WBDM LIKE '" + parString.toUpperCase()
						+ "' ) ";

				sql_list.append(qur);
			}

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			Sql_count
					.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH AND t.ZBLB =:ZBLB)");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = "";
				if (que[4].indexOf("]") == -1) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = "and ( t1.WZMC  like '" + parString
						+ "' or t1.PYDM LIKE '" + parString.toUpperCase() + "'"
						+ " or t1.WBDM LIKE '" + parString.toUpperCase()
						+ "' ) ";
				Sql_count.append(qur);
			}

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存明细查询失败！");
		}
	}

	/**
	 * 库存明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryStockEjDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t2.CJMC as CJMC,t1.WZDW as WZDW,t.WZSL as WZSL,t.YKSL as YKSL,t.WZSL-t.YKSL as KYSL,t.WZJG as WZJG,t.WZJE as WZJE,t.SCRQ as SCRQ,");
			sql_list.append("t.SXRQ as SXRQ,t.WZPH as WZPH,t.MJPH as MJPH,t1.HSLB as HSLB,t.JLXH as JLXH,t.KCXH as KCXH,t.KFXH as KFXH,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM,t1.KWBH as KWBH,t.FSRQ as FSRQ FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH)");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = null;
				if (que.length > 5) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = "and ( t1.WZMC  like '" + parString
						+ "' or t1.PYDM LIKE '" + parString.toUpperCase() + "'"
						+ " or t1.WBDM LIKE '" + parString.toUpperCase()
						+ "' ) ";
				sql_list.append(qur);
			}
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			Sql_count
					.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH)");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = null;
				if (que.length > 5) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = "and ( t1.WZMC  like '" + parString
						+ "' or t1.PYDM LIKE '" + parString.toUpperCase() + "'"
						+ " or t1.WBDM LIKE '" + parString.toUpperCase()
						+ "' ) ";
				Sql_count.append(qur);
			}

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_EJ_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存明细查询失败！");
		}
	}

	/**
	 * 库存汇总查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryStockCollect(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int ZBLB = 0;
		if (req.get("ZBLB") != null && req.get("ZBLB") != "") {
			ZBLB = Integer.parseInt(req.get("ZBLB") + "");
		}

		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("ZBLB", ZBLB);
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,Sum(t.WZSL) as WZSL,Sum(t.YKSL) as YKSL,Sum(t.WZSL)-Sum(t.YKSL) as KYSL,Sum(t.WZJE) as WZJE,");
			sql_list.append("t1.HSLB as HSLB,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH AND t.ZBLB =:ZBLB) ");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String parString = null;
				if (que.length > 5) {
					parString = que[5].substring(0, que[5].indexOf("]")).trim();
				} else {
					parString = que[4].substring(0, que[4].indexOf("]")).trim();
				}
				String qur = " and ( t1.WZMC  like '" + parString
						+ "' or t1.PYDM LIKE '" + parString.toUpperCase()
						+ "' " + " or t1.WBDM LIKE '" + parString.toUpperCase()
						+ "' )";
				sql_list.append(qur);
			}

			sql_list.append(" GROUP BY t1.WZMC,t1.WZGG,t1.WZDW,t1.HSLB,t1.ZBLB,t.WZXH,t1.PYDM,t1.WBDM,t1.JXDM ");

			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_HZ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存汇总查询失败！");
		}
	}

	/**
	 * 二级库房库存汇总查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryStockEjCollect(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			if (req.get("cnd") != null) {
				queryCnd = req.get("cnd") + "";
			}
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,Sum(t.WZSL) as WZSL,Sum(t.YKSL) as YKSL,Sum(t.WZSL)-Sum(t.YKSL) as KYSL,Sum(t.WZJE) as WZJE,");
			sql_list.append("t1.HSLB as HSLB,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) and (t.KFXH =:KFXH)");
			if (queryCnd != null) {
				String textName = null;
				String textValue = null;
				String[] que = queryCnd.split(",");
				if (que.length > 5) {
					textName = que[3];
					textName = textName.substring(3, textName.length() - 1);
					textValue = que[5].substring(0, que[5].indexOf("]"));
					textValue = textValue.substring(1);
				} else {
					textName = que[2];
					textName = textName.substring(3, textName.length() - 1);
					textValue = que[4].substring(0, que[4].indexOf("]"));
					textValue = textValue.substring(1);
				}
				sql_list.append(" and ( t1." + textName + " like '" + textValue
						+ "' or t1.PYDM like '" + textValue.toUpperCase() + "'"
						+ " or t1.WBDM like '" + textValue.toUpperCase() + "')");
			}
			sql_list.append(" GROUP BY t1.WZMC,t1.WZGG,t1.WZDW,t1.HSLB,t1.ZBLB,t.WZXH,t1.PYDM,t1.WBDM,t1.JXDM");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_EJ_HZ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "一级库房库存汇总查询失败！");
		}
	}

	/**
	 * 一级库房查看二级库房库存汇总查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryStockEjCollectByKfxh(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			if (req.get("cnd") != null) {
				queryCnd = req.get("cnd") + "";
			}
		}
		String ZBLB = "";
		if (req.containsKey("ZBLB")) {
			if (req.get("ZBLB") != null) {
				ZBLB = req.get("ZBLB") + "";
			}
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,Sum(t.WZSL) as WZSL,Sum(t.YKSL) as YKSL,Sum(t.WZSL)-Sum(t.YKSL) as KYSL,Sum(t.WZJE) as WZJE,");
			sql_list.append("t1.HSLB as HSLB,t1.ZBLB as ZBLB,t.WZXH as WZXH,t1.PYDM as PYDM,");
			sql_list.append("t1.WBDM as WBDM,t1.JXDM as JXDM FROM ");
			sql_list.append(" WL_WZKC t, WL_WZZD t1,WL_SCCJ t2 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t2.CJXH) ");
			if (ZBLB != "") {
				parameters.put("ZBLB", ZBLB);
				sql_list.append(" and (t1.ZBLB =:ZBLB)");
			}
			if (queryCnd != null) {
				String textName = null;
				String textValue = null;
				String[] que = queryCnd.split(",");
				if (que.length > 5) {
					textName = que[3];
					textName = textName.substring(3, textName.length() - 1);
					textValue = que[5].substring(0, que[5].indexOf("]"));
					textValue = textValue.substring(1);
				} else {
					textName = que[2];
					textName = textName.substring(3, textName.length() - 1);
					textValue = que[4].substring(0, que[4].indexOf("]"));
					textValue = textValue.substring(1);
				}
				if (textName.equals("KFXH")) {
					sql_list.append(" and ( t." + textName + " = " + textValue
							+ " )");
				} else {
					sql_list.append(" and ( t1." + textName + " like '"
							+ textValue + "' or t1.PYDM like '"
							+ textValue.toUpperCase() + "'"
							+ " or t1.WBDM like '" + textValue.toUpperCase()
							+ "')");
				}

			}
			sql_list.append(" GROUP BY t1.WZMC,t1.WZGG,t1.WZDW,t1.HSLB,t1.ZBLB,t.WZXH,t1.PYDM,t1.WBDM,t1.JXDM");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_EJ_HZ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房库存汇总查询失败！");
		}
	}

	/**
	 * 科室账册---明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDepartmentBooksDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int ZBLB = Integer.parseInt(req.get("cnd") + "");

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
			parameters.put("ZBLB", ZBLB);
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t.KSDM as KSDM,t2.OFFICENAME as KSMC,t1.WZMC as WZMC,t1.WZGG as WZGG,t.CJXH as CJXH,t3.CJMC as CJMC,t1.WZDW as WZDW,SUM(t.WZSL) as WZSL,");
			sql_list.append("t.WZJG as WZJG,SUM(t.WZJE)as WZJE,t.KCXH as KCXH,t.ZBLB as ZBLB,t2.PYCODE as PYDM,t.ZRRQ as ZRRQ FROM ");
			sql_list.append(" WL_KSZC t, WL_WZZD t1,SYS_Office  t2,WL_SCCJ t3 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t3.CJXH) and (t.KSDM = t2.ID) and (t.KFXH =:KFXH) and t.ZBLB =:ZBLB ");
			sql_list.append(" GROUP BY t.KSDM,t2.OFFICENAME,t1.WZMC,t1.WZGG,t.CJXH,t3.CJMC,t1.WZDW,t.WZJG,t.KCXH,t.ZBLB,t2.PYCODE,t.ZRRQ ");
			sql_list.append(" ORDER BY t2.OFFICENAME ASC,t1.WZMC ASC,t1.WZGG ASC ");

			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_KSZC_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "科室明细查询失败！");
		}
	}

	/**
	 * 科室账册---汇总查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDepartmentBooksCollect(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int ZBLB = Integer.parseInt(req.get("cnd") + "");

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
			parameters.put("ZBLB", ZBLB);
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t.KSDM as KSDM,t2.OFFICENAME as KSMC,t1.WZMC as WZMC,t1.WZGG as WZGG,t1.WZDW as WZDW,SUM(t.WZSL) as WZSL,");
			sql_list.append("SUM(t.WZJE)as WZJE,t.ZBLB as ZBLB,t2.PYCODE as PYDM FROM ");
			sql_list.append(" WL_KSZC t, WL_WZZD t1,SYS_Office  t2,WL_SCCJ t3 ");
			sql_list.append(" WHERE (t.WZXH = t1.WZXH) and (t.CJXH = t3.CJXH) and t.KSDM = t2.ID and (t.KFXH =:KFXH) and t.ZBLB =:ZBLB ");
			sql_list.append(" GROUP BY t.KSDM,t2.OFFICENAME,t1.WZMC,t1.WZGG,t1.WZDW,t.ZBLB,t2.PYCODE ");
			sql_list.append(" ORDER BY t2.OFFICENAME ASC,t1.WZMC ASC,t1.WZGG ASC ");

			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_KSZC_HZ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "科室汇总查询失败！");
		}
	}

	/**
	 * 固定置产查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryFixedAssetsList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT a.WZBH as WZBH, d.WZMC as WZMC,d.WZGG as WZGG,e.CJMC as CJMC,d.WZDW as WZDW,");
			sql_list.append("a.ZCYZ as ZCYZ,a.CZYZ as CZYZ,a.TZRQ as TZRQ,a.QYRQ as QYRQ,a.ZRRQ as ZRRQ,a.BSRQ as BSRQ,b.OFFICENAME as OFFICENAME,a.WZZT as WZZT, ");
			sql_list.append("d.HSLB as HSLB,a.ZBXH as ZBXH,c.DWMC as DWMC,a.XHGG as XHGG,a.XHMC as XHMC,f.HSMC as HSMC FROM ");
			sql_list.append(" WL_ZCZB a LEFT OUTER JOIN SYS_Office b ON a.ZYKS = b.ID LEFT OUTER JOIN WL_GHDW c ON a.GHDW = c.DWXH,WL_WZZD d,WL_SCCJ e,WL_HSLB f ");
			sql_list.append(" WHERE ( d.WZXH = a.WZXH ) AND (d.HSLB = f.HSLB) AND ( a.CJXH = e.CJXH ) and (a.KFXH =:KFXH) ");
			// 单个条件查询，高级查询
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String pd1 = queryCnd.substring(queryCnd.indexOf("[") + 1,
						queryCnd.indexOf(","));
				if (pd1.endsWith("and") || pd1.endsWith("or")) {
					String pd2 = queryCnd.substring(queryCnd.indexOf(",") + 1,
							queryCnd.indexOf("]]]") + 3);
					int length = pd2.length();
					for (int i = 0; i < length; i++) {
						if (pd2.length() < 1) {
							break;
						}
						int num = pd2.indexOf("]],");
						if (num > 0) {
							String pd3 = pd2.substring(0,
									pd2.indexOf("]],") + 2);
							String[] que = pd3.split(",");
							if (que[0].lastIndexOf("ne") > 0) {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " != '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "'  ";
								sql_list.append(qur);
							} else if (que[0].lastIndexOf("isNull") > 0) {
								if (que[2].indexOf("not") > 0) {
									String qur = pd1
											+ que[4].substring(0,
													que[4].indexOf("]"))
											+ " Is not Null ";
									sql_list.append(qur);
								} else {
									String qur = pd1
											+ que[4].substring(0,
													que[4].indexOf("]"))
											+ " Is Null ";
									sql_list.append(qur);
								}
							} else if (que[4].lastIndexOf("%]") > 0) {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " like '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "'" + " ";
								sql_list.append(qur);
							} else {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " = '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "'";
								sql_list.append(qur);
							}
							pd2 = pd2.substring(num + 3, pd2.length());
							i = num + 3;
						} else if (pd2.lastIndexOf("]]]") > 0) {
							String pd3 = pd2.substring(0,
									pd2.lastIndexOf("]]]") + 1);
							String[] que = pd3.split(",");
							if (que[0].lastIndexOf("ne") > 0) {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " != '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "' ";
								sql_list.append(qur);
							} else if (que[0].lastIndexOf("isNull") > 0) {
								if (que[2].indexOf("not") > 0) {
									String qur = pd1
											+ que[4].substring(0,
													que[4].indexOf("]"))
											+ " Is not Null ";
									sql_list.append(qur);
								} else {
									String qur = pd1
											+ que[4].substring(0,
													que[4].indexOf("]"))
											+ " Is Null ";
									sql_list.append(qur);
								}
							} else if (que[4].lastIndexOf("%]") > 0) {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " like '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "'" + "  ";
								sql_list.append(qur);
							} else {
								String qur = pd1
										+ que[2].substring(0,
												que[2].indexOf("]"))
										+ " = '"
										+ que[4].substring(0,
												que[4].indexOf("]")).trim()
										+ "' ";
								sql_list.append(qur);
							}
							pd2 = "";
						}
					}
				} else {
					String qur = "";
					String[] que = queryCnd.split(",");
					if (que[2].substring(3, que[2].indexOf("]")).equals("WZZT")) {
						qur = "and "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " = '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "'";
					} else if (que[2].substring(3, que[2].indexOf("]")).equals(
							"WZMC")) {
						qur = " and ( "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim()
								+ "'  or d.pydm like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim().toUpperCase() + "')";
					} else {
						qur = " and "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "'";
					}
					sql_list.append(qur);
				}
			}
			sql_list.append(" order by  a.WZBH");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_ZCZB_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "一级库房固定置产查询失败！");
		}
	}

	/**
	 * 入库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDocumentDetailforRK(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();

		try {
			parameters.put("KFXH", KFXH);
			parameterssize.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT a.DJXH as DJXH, a.LZFS as LZFS ,a.LZDH as LZDH,d.WZMC as WZMC,d.WZGG as WZGG,c.CJXH as CJXH,d.WZDW as WZDW,c.WZSL as WZSL,");
			sql_list.append("c.WZJG as WZJG,c.WZJE as WZJE,c.WZPH as WZPH, a.JZRQ as JZRQ,a.DWXH as DWXH ,c.MJPH as MJPH,a.JBGH as JBGH,d.HSLB as HSLB, ");
			sql_list.append("c.FPHM as FPHM,e.YWLB as YWLB,a.DJZT as DJZT,a.ZBLB as ZBLB,e.DJLX as DJLX,a.THDJ as THDJ,b.DWMC as DWMC,a.RKRQ as RKRQ, ");
			sql_list.append("c.WZXH as WZXH,a.DJJE as DJJE,a.ZDGH as ZDGH, a.JZGH as JZGH,c.FKJE as FKJE,d.BKBZ as BKBZ,d.YCWC as YCWC,d.JLBZ as JLBZ,");
			sql_list.append("a.ZDRQ as ZDRQ,a.SHGH as SHGH FROM ");
			sql_list.append(" WL_RK01 a  LEFT OUTER JOIN WL_GHDW b  ON a.DWXH = b.DWXH,WL_RK02 c,WL_WZZD d,WL_LZFS e ");
			sql_list.append(" WHERE ( a.DJXH = c.DJXH ) and ( c.WZXH = d.WZXH ) and ( a.LZFS = e.FSXH ) and  ( ( a.DJZT = 2 ) ) AND  ");
			sql_list.append(" c.WZSL <> 0 and a.KFXH =:KFXH ");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				if (que.length > 5) {
					if (que[3].substring(0, que[3].indexOf("]")).trim()
							.equals("a.LZFS")) {
						String qur = "and a.LZFS ='"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim() + "'";
						sql_list.append(qur);
					} else if (que[3].substring(0, que[3].indexOf("]")).trim()
							.equals("d.WZMC")) {
						sql_list.append(" and ( d.WZMC like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim()
								+ "' or d.PYDM like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim().toUpperCase()
								+ "'"
								+ " or d.WBDM like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim().toUpperCase() + "')");
					} else {
						String qur = "and "
								+ que[3].substring(0, que[3].indexOf("]"))
								+ " like '%"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim() + "%'";
						sql_list.append(qur);
					}
				} else {
					if (que[2].substring(0, que[2].indexOf("]")).trim()
							.equals("a.LZFS")) {
						String qur = "and a.LZFS ='"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "'";
						sql_list.append(qur);
					} else if (que[2].substring(0, que[2].indexOf("]")).trim()
							.equals("d.WZMC")) {
						sql_list.append(" and ( d.WZMC like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim()
								+ "' or d.PYDM like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim().toUpperCase()
								+ "'"
								+ " or d.WBDM like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim().toUpperCase() + "')");
					} else {
						String qur = "and "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "%'";
						sql_list.append(qur);
					}
				}
			}
			sql_list.append(" order by LZDH desc");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameterssize);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_RK01_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "一级库房入库明细查询失败！");
		}
	}

	/**
	 * 出库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDocumentDetailforCK(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT b.LZFS as LZFS,b.LZDH as LZDH,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,c.WZDW as WZDW,a.WZJE as WZJE,b.KSDM as KSDM,");
			sql_list.append("a.WZPH as WZPH,a.MJPH as MJPH,b.JBGH as JBGH,b.ZDGH as ZDGH ,b.JZRQ as JZRQ ,b.ZDRQ as ZDRQ,b.SHRQ as SHRQ,");
			sql_list.append("b.SHGH as SHGH,b.DJZT as DJZT,b.DJXH as DJXH,'CK' as DJLX ,Null as LYKF,c.WZMC as WZMC,c.WZGG as WZGG ,d.CJMC as CJMC,");
			sql_list.append("b.THDJ as THDJ,a.WZXH as WZXH,b.CKRQ as CKRQ,a.JLXH as JLXH,b.JZGH as JZGH FROM");
			sql_list.append("  WL_CK01 b, WL_CK02 a ,WL_WZZD c ,WL_SCCJ d");
			sql_list.append(" WHERE (a.DJXH = b.DJXH) and (b.DJZT >= 2) and b.KFXH =:KFXH and c.WZXH = a.WZXH and d.CJXH = a.CJXH ");
			// sql_list.append(" a.WZSL <> 0  and (b.THDJ = 0  Or  b.THDJ IS NULL ) ");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				if (que.length > 5) {
					if (que[3].substring(0, que[3].indexOf("]")).trim()
							.equals("a.LZFS")) {
						String qur = "and b.LZFS ='"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim() + "'";
						sql_list.append(qur);
					} else if (que[3].substring(0, que[3].indexOf("]")).trim()
							.equals("c.WZMC")) {
						sql_list.append(" and ( c.WZMC like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim()
								+ "' or c.PYDM like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim().toUpperCase()
								+ "'"
								+ " or c.WBDM like '"
								+ que[5].substring(0, que[5].indexOf("]"))
										.trim().toUpperCase() + "')");
					} else {
						String qur = "and "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "%'";
						sql_list.append(qur);
					}
				} else {
					if (que[2].substring(0, que[2].indexOf("]")).trim()
							.equals("a.LZFS")) {
						String qur = "and b.LZFS ='"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "'";
						sql_list.append(qur);
					} else if (que[2].substring(0, que[2].indexOf("]")).trim()
							.equals("c.WZMC")) {
						sql_list.append(" and ( c.WZMC like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim()
								+ "' or c.PYDM like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim().toUpperCase()
								+ "'"
								+ " or c.WBDM like '"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim().toUpperCase() + "')");
					} else {
						String qur = "and "
								+ que[2].substring(0, que[2].indexOf("]"))
								+ " like '%"
								+ que[4].substring(0, que[4].indexOf("]"))
										.trim() + "%'";
						sql_list.append(qur);
					}
				}
			}
			sql_list.append(" order by LZDH desc");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_CK02_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "一级库房出库明细查询失败！");
		}
	}

	/**
	 * 二级库房出库明细查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDocumentDetailforEJCK(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"select * from (select e.FSXH as LZFS,b.LZDH as LZDH,b.CKRQ as CKRQ,c.WZMC as WZMC, c.PYDM as PYDM,"
							+ " c.WBDM as WBDM,c.WZGG as WZGG,"
							+ " c.WZDW as WZDW,d.CJMC as CJMC,a.WZSL as WZSL, f.KSDM as KSDM,"
							+ " (select g.BRXM as BRXM from WL_XHMX g where g.djxh=a.djxh and g.kfxh=b.kfxh and g.wzxh=a.wzxh and g.wzsl =a.wzsl) as BRXM,"
							+ " (select g.BRLY as BRLY from WL_XHMX g where g.djxh=a.djxh and g.kfxh=b.kfxh and g.wzxh=a.wzxh and g.wzsl =a.wzsl) as BRLY"
							+ " from WL_CK02 a,WL_CK01 b,WL_WZZD c,WL_SCCJ d,wl_lzfs e ,SYS_Office f"
							+ " where a.DJXH=b.DJXH and a.WZXH=c.WZXH  and a.CJXH=d.CJXH and b.LZFS=e.fsxh"
							+ " and b.KSDM=f.KSDM and b.DJZT=2 and b.KFXH=:KFXH) ");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				// if (que[2].substring(0, que[2].indexOf("]")).trim()
				// .equals("a.BRXM")
				// || que[2].substring(0, que[2].indexOf("]")).trim()
				// .equals("a.BRLY")) {
				//
				// } else
				if (que[2].substring(0, que[2].indexOf("]")).trim()
						.equals("b.LZFS")) {
					String qur = "where LZFS ='"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "'";
					sql_list.append(qur);
				} else if (que[2].substring(0, que[2].indexOf("]")).trim()
						.equals("c.WZMC")) {
					sql_list.append(" where ( WZMC like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "' or PYDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase()
							+ "'"
							+ " or WBDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase() + "')");
				} else {
					String qur = "where "
							+ que[2].substring(3, que[2].indexOf("]"))
							+ " like '%"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "%'";
					sql_list.append(qur);
				}
			}
			sql_list.append(" order by LZDH desc ");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_CK02_CX_EJ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房出库明细查询失败！");
		}
	}

	/**
	 * 转科查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryChangeDepartments(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT b.LZFS as LZFS,b.LZDH as LZDH,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,c.WZDW as WZDW,a.WZJE as WZJE,b.KSDM as KSDM,");
			sql_list.append("a.WZPH as WZPH,a.MJPH as MJPH,b.JBGH as JBGH,b.ZDGH as ZDGH ,b.JZRQ as JZRQ ,b.ZDRQ as ZDRQ,b.SHRQ as SHRQ,");
			sql_list.append("b.SHGH as SHGH,b.DJZT as DJZT,b.DJXH as DJXH,'CK' as DJLX ,Null as LYKF,c.WZMC as WZMC,c.WZGG as WZGG ,d.CJMC as CJMC,");
			sql_list.append("b.THDJ as THDJ,a.WZXH as WZXH,b.CKRQ as CKRQ,a.JLXH as JLXH,b.JZGH as JZGH FROM");
			sql_list.append("  WL_CK01 b, WL_CK02 a ,WL_WZZD c ,WL_SCCJ d");
			sql_list.append(" WHERE (a.DJXH = b.DJXH) and (b.DJZT >= 2) and b.KFXH =:KFXH and c.WZXH = a.WZXH and d.CJXH = a.CJXH and  ");
			sql_list.append(" a.WZSL <> 0  and (b.THDJ = 0  Or  b.THDJ IS NULL )  ");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				// System.out.println(que[2].substring(0,
				// que[2].indexOf("]")).trim());
				if (que[2].substring(0, que[2].indexOf("]")).trim()
						.equals("c.WZMC")) {
					sql_list.append(" and ( c.WZMC like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "' or c.PYDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase()
							+ "'"
							+ " or c.WBDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase() + "')");
				} else {
					String qur = "and "
							+ que[2].substring(0, que[2].indexOf("]"))
							+ " like '%"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "'";
					sql_list.append(qur);
				}

			}
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_CK02_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "科室汇总查询失败！");
		}
	}

	/**
	 * 二级库房入库
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryDocumentDetailforEjRK(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("CKKF", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  kfxh as KFXH, djxh as DJXH,djlx as DJLX,djzt as DJZT,lzdh as LZDH,lzfs as LZFS,jzrq as JZRQ,jzgh as JZGH, jlxh as JLXH,wzxh as WZXH,cjxh as CJXH,");
			sql_list.append(" wzsl as WZSL,wzjg as WZJG,wzje as WZJE,fsmc as FSMC,wzmc as WZMC,wzgg as WZGG,wzdw as WZDW,ckkf as CKKF ,cjmc as CJMC from");
			sql_list.append(" (select wl_ck01.kfxh,wl_ck01.djxh,wl_ck01.djlx,wl_ck01.djzt,wl_ck01.lzdh,wl_ck01.qrrk as lzfs ,wl_ck01.qrrq as jzrq,wl_ck01.qrgh as jzgh,");
			sql_list.append(" wl_ck02.jlxh,wl_ck02.wzxh,wl_ck02.cjxh,wl_ck02.wzsl,wl_ck02.wzjg,wl_ck02.wzje,wl_lzfs.fsmc,wl_wzzd.wzmc,wl_wzzd.pydm,wl_wzzd.wbdm,wl_wzzd.wzgg, wl_wzzd.wzdw,wl_ck01.ckkf,wl_sccj.cjmc ");
			sql_list.append(" from wl_ck01, wl_ck02,wl_lzfs,wl_wzzd,wl_sccj ");
			sql_list.append(" where wl_ck01.djxh = wl_ck02.djxh and qrbz = 1 and wl_lzfs.fsxh = wl_ck01.lzfs and wl_wzzd.wzxh = wl_ck02.wzxh and wl_sccj.cjxh = wl_ck02.cjxh ");
			sql_list.append(" union all select wl_rk01.kfxh, wl_rk01.djxh, wl_rk01.djlx, wl_rk01.djzt,wl_rk01.lzdh,wl_rk01.lzfs,wl_rk01.jzrq, wl_rk01.jzgh, ");
			sql_list.append(" wl_rk02.jlxh, wl_rk02.wzxh,wl_rk02.cjxh,wl_rk02.wzsl,wl_rk02.wzjg,wl_rk02.wzje,wl_lzfs.fsmc,wl_wzzd.wzmc,wl_wzzd.pydm,wl_wzzd.wbdm,wl_wzzd.wzgg, wl_wzzd.wzdw, wl_rk01.kfxh as ckkf,wl_sccj.cjmc ");
			sql_list.append(" from wl_rk01, wl_rk02,wl_lzfs,wl_wzzd,wl_sccj ");
			sql_list.append(" where wl_rk01.djxh = wl_rk02.djxh and djzt = 2 and wl_lzfs.fsxh = wl_rk01.lzfs  and wl_wzzd.wzxh = wl_rk02.wzxh and wl_sccj.cjxh = wl_rk02.cjxh ) a where a.ckkf =:CKKF ");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				if (que[2].substring(0, que[2].indexOf("]")).trim()
						.equals("a.WZMC")) {
					sql_list.append(" and ( a.WZMC like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "' or a.PYDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase()
							+ "'"
							+ " or a.WBDM like '"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
									.toUpperCase() + "')");
				} else {
					String qur = "and "
							+ que[2].substring(0, que[2].indexOf("]"))
							+ " like '%"
							+ que[4].substring(0, que[4].indexOf("]")).trim()
							+ "'";
					sql_list.append(qur);
				}
			}
			sql_list.append(" order by lzdh desc ");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_RK01_EJ_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "科室汇总查询失败！");
		}
	}

	/**
	 * 低储预警查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySuppliesStockLowWarning(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  a.WZXH as WZXH, b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.GCSL as GCSL,");
			sql_list.append(" a.DCSL as DCSL,sum(c.WZSL - c.YKSL) as WZSL from");
			sql_list.append(" WL_WZZD b, WL_KCYJ a, WL_WZKC c");
			sql_list.append(" where a.KFXH =:KFXH  AND b.WZXH = a.WZXH AND a.WZXH = c.WZXH  AND a.KFXH = c.KFXH ");
			sql_list.append(" GROUP BY a.WZXH,a.GCSL, a.DCSL, b.WZMC, b.WZGG, b.WZDW ");
			sql_list.append(" Having sum(c.WZSL - c.YKSL) < a.dcsl ");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and " + que[2].substring(0, que[2].indexOf("]"))
						+ " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql_list.append(qur);
			}
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_KCYJ_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "低储预警查询失败！");
		}
	}

	/**
	 * 高储预警查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySuppliesStockHighWarning(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW,a.GCSL as GCSL,");
			sql_list.append(" a.DCSL as DCSL,sum(c.WZSL - c.YKSL) as WZSL from");
			sql_list.append(" WL_WZZD b, WL_KCYJ a, WL_WZKC c");
			sql_list.append(" where a.KFXH =:KFXH  AND b.WZXH = a.WZXH AND a.WZXH = c.WZXH  AND a.KFXH = c.KFXH ");
			sql_list.append(" GROUP BY a.WZXH,a.GCSL, a.DCSL, b.WZMC, b.WZGG, b.WZDW ");
			sql_list.append(" Having sum(c.WZSL - c.YKSL) > a.gcsl ");

			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and " + que[2].substring(0, que[2].indexOf("]"))
						+ " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql_list.append(qur);
			}
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_KCYJ_CX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "高储预警查询失败！");
		}
	}

	/**
	 * 库存失效预警
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQuerySuppliesStockExpirationWarning(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			KFXH = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}

		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  b.ZBLB as ZBLB, b.WZMC as WZMC,b.WZGG as WZGG,b.WZDW as WZDW, b.SXYJ as SXYJ,");
			sql_list.append(" a.CJXH as CJXH,a.WZPH as WZPH,a.MJPH as MJPH, a.SCRQ as SCRQ,a.SXRQ as SXRQ,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,c.CJMC as CJMC,d.ZBMC as ZBMC FROM");
			sql_list.append(" WL_WZKC a, WL_WZZD b, WL_SCCJ c, WL_ZBLB d ");
			sql_list.append(" WHERE (a.WZXH = b.WZXH) AND (a.CJXH = c.CJXH) AND (b.ZBLB = d.ZBLB)  AND (a.KFXH =:KFXH) and (sysdate + sxyj)>a.SXRQ ");
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and " + que[2].substring(0, que[2].indexOf("]"))
						+ " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql_list.append(qur);
			}
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			int total = inofListCount.size();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZKC_YJ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存失效预警查询失败！");
		}
	}

	/**
	 * 得到账簿类别
	 * 
	 * @param body
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (kfxh != null) {
			parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		} else {
			parameters.put("KFXH", 0L);
		}
		try {
			List<Map<String, Object>> list = dao.doQuery("from " + "WL_KFXX"
					+ " where KFXH=:KFXH", parameters);
			String kfzb = "0";
			if (list.size() > 0) {
				kfzb = (String) list.get(0).get("KFZB");
			}
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
}
