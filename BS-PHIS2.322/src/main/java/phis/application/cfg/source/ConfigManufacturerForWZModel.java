package phis.application.cfg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class ConfigManufacturerForWZModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public ConfigManufacturerForWZModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 生产厂家维护——查询列表
	 * 
	 * @param req
	 * @param res
	 */
	public void doManufacturerQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String KFXH = user.getProperty("treasuryId") + "";
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
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
		String sqlString = "";
		try {
			if (KFXH == null || KFXH.equals("0")||"null".equals(KFXH)) {
				parameters.put("JGID", JGID);
				sqlString = "WHERE (t.JGID =:JGID) ";
			} else {
				parameters.put("KFXH", KFXH);
				sqlString = "WHERE (t.KFXH =:KFXH) ";
			}

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  t.CJXH as CJXH,t.CJMC as CJMC, t.QYXZ as QYXZ,t.FRDB as FRDB,t.PYDM as PYDM, t.WBDM as WBDM,t.JXDM as JXDM,t.QTDM as QTDM,t.KHYH as KHYH, t.YHZH as YHZH, t.LXDZ as LXDZ,t.YZBM as YZBM,t.LXRY as LXRY, t.DHHM as DHHM,t.SJHM as SJHM,t.CZHM as CZHM,t.HLWZ as HLWZ,t.DZYJ as DZYJ,t.QYJJ as QYJJ,t.CJZT as CJZT,t.KFXH as KFXH FROM ");
			sql_list.append("WL_SCCJ t ");
			sql_list.append(sqlString);
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and t."
						+ que[2].substring(3, que[2].indexOf("]")) + " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				sql_list.append(" " + qur);
			}
			sql_list.append(" ORDER BY PYDM ");

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("WL_SCCJ t ");
			Sql_count.append(sqlString);
			if (!"null".equals(queryCnd) && queryCnd != null) {
				String[] que = queryCnd.split(",");
				String qur = "and t."
						+ que[2].substring(3, que[2].indexOf("]")) + " like '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
				Sql_count.append(" " + qur);
			}
			Sql_count.append(" ORDER BY PYDM ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_SCCJ);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人列表查询失败！");
		}
	}

	/**
	 * 生产厂商维护——增加 && 修改
	 * 
	 * @param body
	 * @param res
	 * @param schemaDetailsList
	 * @param op
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doOperatForManufacturer(Map<String, Object> body,
			Map<String, Object> res, String schemaDetailsList, String op,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		String Msg = "保存失败！";
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			String KFXH = user.getProperty("treasuryId") + "";

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("CJMC", body.get("CJMC"));
			String sqlString = "";
			if (KFXH != null  && !"null".equals(KFXH)) {
				parameters.put("KFXH", KFXH);
				sqlString = "WHERE (t.KFXH =:KFXH) and (t.CJMC =:CJMC)";
			} else {
				parameters.put("JGID", JGID);
				sqlString = "WHERE (t.CJMC =:CJMC) and (t.JGID =:JGID) ";
			}
			if ("update".equals(op)) {
				long CJXH = Long.parseLong(body.get("CJXH") + "");
				parameters.put("CJXH", CJXH);
				sqlString = sqlString + "  and (t.CJXH <>:CJXH)";
			}
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("WL_SCCJ t ");
			Sql_count.append(sqlString);

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");

			if (total > 0) {
				Msg = "厂家已存在!";
				throw new RuntimeException("厂家已存在!");
			}
			if ("create".equals(op)) {
				body.put("JGID", JGID);
				if (KFXH == null || "null".equals(KFXH)) {
					body.put("KFXH", 0);
				} else {
					body.put("KFXH", KFXH);
				}
				Map<String, Object> Max_CJXH = dao.doSave(op, BSPHISEntryNames.WL_SCCJ, body,
						false);
				// 别名信息的保存
				if (body.containsKey("aliasMsgtab")) {
					ArrayList<Map<String, Object>> aliasMsgs = (ArrayList<Map<String, Object>>)

					body.get("aliasMsgtab");
					if (aliasMsgs.size() > 0) {
						for (int i = 0; i < aliasMsgs.size(); i++) {
							Map<String, Object> aliasMsg = aliasMsgs.get(i);
							Map<String, Object> aliasMsgMap = new HashMap<String, Object>();
							if (aliasMsg.get("CJBM").equals(body.get("CJMC"))) {
								continue;
							}
							aliasMsgMap.put("CJXH", Max_CJXH.get("CJXH"));
							aliasMsgMap.put("CJBM", aliasMsg.get("CJBM"));
							aliasMsgMap.put("PYDM", aliasMsg.get("PYDM"));
							aliasMsgMap.put("WBDM", aliasMsg.get("WBDM"));
							aliasMsgMap.put("JXDM", aliasMsg.get("JXDM"));
							if (aliasMsg.get("QTDM") == "") {
							} else {
								aliasMsgMap.put("QTDM", aliasMsg.get("QTDM"));
							}
							dao.doSave(op, BSPHISEntryNames.WL_CJBM, aliasMsgMap, false);
						}
						Map<String, Object> aliasMsgMap = new HashMap<String, Object>();
						aliasMsgMap.put("CJXH", Max_CJXH.get("CJXH"));
						aliasMsgMap.put("CJBM", body.get("CJMC"));
						aliasMsgMap.put("PYDM", body.get("PYDM"));
						aliasMsgMap.put("WBDM", body.get("WBDM"));
						aliasMsgMap.put("JXDM", body.get("JXDM"));
						dao.doSave(op, BSPHISEntryNames.WL_CJBM, aliasMsgMap, false);
					} else {
						Map<String, Object> aliasMsgMap = new HashMap<String, Object>();
						aliasMsgMap.put("CJXH", Max_CJXH.get("CJXH"));
						aliasMsgMap.put("CJBM", body.get("CJMC"));
						aliasMsgMap.put("PYDM", body.get("PYDM"));
						aliasMsgMap.put("WBDM", body.get("WBDM"));
						aliasMsgMap.put("JXDM", body.get("JXDM"));
						dao.doSave(op, BSPHISEntryNames.WL_CJBM, aliasMsgMap, false);
					}
				} else {
					Map<String, Object> aliasMsgMap = new HashMap<String, Object>();
					aliasMsgMap.put("CJXH", Max_CJXH.get("CJXH"));
					aliasMsgMap.put("CJBM", body.get("CJMC"));
					aliasMsgMap.put("PYDM", body.get("PYDM"));
					aliasMsgMap.put("WBDM", body.get("WBDM"));
					aliasMsgMap.put("JXDM", body.get("JXDM"));
					dao.doSave(op, BSPHISEntryNames.WL_CJBM, aliasMsgMap, false);
				}
				// 证件信息的保存
				if (body.containsKey("cardtab")) {
					ArrayList<Map<String, Object>> aliasMsgs = (ArrayList<Map<String, Object>>) body
							.get("cardtab");
					if (aliasMsgs.size() > 0) {
						for (int i = 0; i < aliasMsgs.size(); i++) {
							Map<String, Object> aliasMsg = aliasMsgs.get(i);
							if (aliasMsg.get("ZJLX_text") == null
									|| aliasMsg.get("ZJLX_text") == "") {
								Msg = "证件信息不能为空！";
								throw new RuntimeException("证件信息不能为空！");
							}
							if (aliasMsg.get("ZJBH") == null
									|| aliasMsg.get("ZJBH") == "") {
								Msg = "证件编号不能为空！";
								throw new RuntimeException("证件编号不能为空！");
							}
							if (aliasMsg.get("FZRQ") == null
									|| aliasMsg.get("FZRQ") == "") {
								Msg = "发证日期不能为空！";
								throw new RuntimeException("发证日期不能为空！");
							}
							if (aliasMsg.get("SXRQ") == null
									|| aliasMsg.get("SXRQ") == "") {
								Msg = "失效日期不能为空！";
								throw new RuntimeException("失效日期不能为空！");
							} else {
								String SXRQ = aliasMsg.get("SXRQ") + "";
								String data = BSHISUtil.getDate();
								int compare = BSHISUtil.dateCompare(
										BSHISUtil.toDate

										(SXRQ), BSHISUtil.toDate(data));
								if (Integer.parseInt(aliasMsg.get("ZJZT") + "") == 1) {
									if (compare < 0) {
										Msg = "失效日期不能小于当前日期！";
										throw new RuntimeException(
												"失效日期不能小于当前日期");
									}
								}
							}
							String ZJBH = aliasMsg.get("ZJBH") + "";
							int ZJLX = Integer.parseInt(aliasMsg.get("ZJLX")
									+ "");
							String hqlWhere = " ZJBH=:ZJBH and ZJLX=:ZJLX ";
							Map<String, Object> hqlMap = new HashMap<String, Object>();
							hqlMap.put("ZJBH", ZJBH);
							hqlMap.put("ZJLX", ZJLX);
							long l = dao.doCount("WL_ZJXX", hqlWhere, hqlMap);
							if (l > 0) {
								Msg = "证件号码重复，请核对！";
								throw new RuntimeException("证件号码重复，请核对！");
							}
							aliasMsg.put("DXXH",
									Long.parseLong(Max_CJXH.get("CJXH") + ""));
							dao.doSave(op, BSPHISEntryNames.WL_ZJXX, aliasMsg, false);
						}
					}
				}
			} else {
				body.put("JGID", JGID);
				dao.doSave("update", BSPHISEntryNames.WL_SCCJ, body, false);
				if (body.containsKey("aliasMsgtab")) {
					long CJXH = Long.parseLong(body.get("CJXH") + "");
					dao.doUpdate("delete from  WL_CJBM " + " where CJXH="
							+ CJXH, null);
					ArrayList<Map<String, Object>> aliasMsgs = (ArrayList<Map<String, Object>>) body
							.get("aliasMsgtab");
					if (aliasMsgs.size() > 0) {
						for (int i = 0; i < aliasMsgs.size(); i++) {
							Map<String, Object> aliasMsg = aliasMsgs.get(i);
							Map<String, Object> aliasMsgMap = new HashMap<String, Object>();
							op = "create";
							aliasMsgMap.put("CJXH", body.get("CJXH"));
							aliasMsgMap.put("CJBM", aliasMsg.get("CJBM"));
							aliasMsgMap.put("PYDM", aliasMsg.get("PYDM"));
							aliasMsgMap.put("WBDM", aliasMsg.get("WBDM"));
							aliasMsgMap.put("JXDM", aliasMsg.get("JXDM"));
							if (aliasMsg.get("QTDM") == "") {
							} else {
								aliasMsgMap.put("QTDM", aliasMsg.get("QTDM"));
							}
							dao.doSave(op, BSPHISEntryNames.WL_CJBM, aliasMsgMap, false);
						}
					}
				}
				// 证件信息的修改保存
				if (body.containsKey("cardtab")) {
					long CJXH = Long.parseLong(body.get("CJXH") + "");
					// /dao.doUpdate("delete from  WL_ZJXX " + " where DXXH="+
					// CJXH, null);
					Map<String, Object> parMap = new HashMap<String, Object>();
					parMap.put("CJXH", CJXH);
					List<Map<String, Object>> resList = dao
							.doQuery(
									"select ZJXH as ZJXH from WL_ZJXX where DXXH=:CJXH ",
									parMap);

					ArrayList<Map<String, Object>> aliasMsgs = (ArrayList<Map<String, Object>>)

					body.get("cardtab");
					if (aliasMsgs.size() > 0) {
						for (int i = 0; i < aliasMsgs.size(); i++) {
							Map<String, Object> aliasMsg = aliasMsgs.get(i);
							if (aliasMsg.get("ZJLX_text") == null
									|| aliasMsg.get("ZJLX_text") == "") {
								Msg = "证件信息不能为空！";
								throw new RuntimeException("证件信息不能为空！");
							}
							if (aliasMsg.get("ZJBH") == null
									|| aliasMsg.get("ZJBH") == "") {
								Msg = "证件编号不能为空！";
								throw new RuntimeException("证件编号不能为空！");
							}
							if (aliasMsg.get("FZRQ") == null
									|| aliasMsg.get("FZRQ") == "") {
								Msg = "发证日期不能为空！";
								throw new RuntimeException("发证日期不能为空！");
							}
							if (aliasMsg.get("ZJZT") == null
									|| aliasMsg.get("ZJZT") == "") {
								Msg = "单据状态不能为空！";
								throw new RuntimeException("发证日期不能为空！");
							}
							if (aliasMsg.get("SXRQ") == null
									|| aliasMsg.get("SXRQ") == "") {
								Msg = "失效日期不能为空！";
								throw new RuntimeException("失效日期不能为空！");
							} else {
								String SXRQ = aliasMsg.get("SXRQ") + "";
								String data = BSHISUtil.getDate();
								int compare = BSHISUtil.dateCompare(
										BSHISUtil.toDate

										(SXRQ), BSHISUtil.toDate(data));
								if (Integer.parseInt(aliasMsg.get("ZJZT") + "") == 1) {
									if (compare < 0) {
										Msg = "失效日期不能小于当前日期！";
										throw new RuntimeException(
												"失效日期不能小于当前日期");
									}
								}
							}
							String ZJBH = aliasMsg.get("ZJBH") + "";
							int ZJLX = Integer.parseInt(aliasMsg.get("ZJLX")
									+ "");
							Map<String, Object> hqlMap = new HashMap<String, Object>();
							hqlMap.put("ZJBH", ZJBH);
							hqlMap.put("ZJLX", ZJLX);
							List<Map<String, Object>> list = dao
									.doQuery(
											"select ZJXH as ZJXH from WL_ZJXX where ZJBH=:ZJBH and ZJLX=:ZJLX ",
											hqlMap);
							if (list.size() > 1) {
								Msg = "证件号码重复，请核对！";
								throw new RuntimeException("证件号码重复，请核对！");
							} else if (list.size() == 1) {
								long ZJXH = Long.parseLong(list.get(0).get(
										"ZJXH")
										+ "");
								if (aliasMsg.get("ZJXH") != null) {
									long ZJXH_aliasMsg = Long.parseLong

									(aliasMsg.get("ZJXH") + "");
									if (ZJXH_aliasMsg != ZJXH) {
										Msg = "证件号码重复，请核对！";
										throw new RuntimeException(
												"证件号码重复，请核对！");
									}
								}
							}
							aliasMsg.put("DXXH", CJXH);
							if (aliasMsg.get("ZJXH") != null) {
								op = "update";
							} else {
								op = "create";
							}
							dao.doSave(op, BSPHISEntryNames.WL_ZJXX, aliasMsg, false);
						}

						for (int j = 0; j < resList.size(); j++) {
							int num = 0;
							long ZJXH = Long.parseLong(resList.get(j).get(
									"ZJXH")
									+ "");
							for (int k = 0; k < aliasMsgs.size(); k++) {
								if (aliasMsgs.get(k).get("ZJXH") != null) {
									Long ZJXH_k = Long.parseLong(aliasMsgs.get

									(k).get("ZJXH") + "");
									if (ZJXH == ZJXH_k) {
										num = num + 1;
									}
								}
							}
							if (num == 0) {
								dao.doUpdate("delete from  WL_ZJXX "
										+ " where ZJXH=" +

										ZJXH, null);
							}
						}

					} else {
						dao.doUpdate("delete from  WL_ZJXX " + " where DXXH="
								+ CJXH, null);
					}
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, Msg);
		}
	}

	public void doCanceled(Map<String, Object> body, String schemaList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("CJXH", Long.parseLong(body.get("CJXH") + ""));
			if (Integer.parseInt(body.get("CJZT") + "") == 1) {
				parameters.put("CJZT", -1);
			} else {
				parameters.put("CJZT", 1);
			}
			dao.doUpdate("update " + schemaList
					+ " set CJZT=:CJZT WHERE CJXH=:CJXH", parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/**
	 * 物质查询。
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doManufacturerForWZQuery(Map<String, Object> req,
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

		long CJXH = Long.parseLong(req.get("cnd") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CJXH", CJXH);
		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.CJXH as CJXH ,t.WZXH as WZXH, t.KFXH as KFXH , t.ZBLB as ZBLB,t.HSLB as HSLB,t.WZMC as WZMC, t.WZGG as WZGG , t.WZDW as WZDW, t.PYDM as PYDM, t.WBDM as WBDM, t.JXDM as JXDM,t.QTDM as QTDM, t.WZZT as WZZT, t.KWBH as KWBH, t.BKBZ as BKBZ, t.YCWC as YCWC,t.JLBZ as JLBZ, t.GCSL as GCSL, t.DCSL as DCSL,t.WZTM as  WZTM,t.ZDTM as ZDTM FROM ");
			sql_list.append("WL_WZCJ t1, WL_WZZD t  ");
			sql_list.append("WHERE t.WZXH = t1.WZXH AND t1.SYZT = 1 AND t1.CJXH =:CJXH ");
			sql_list.append(" ORDER BY KFXH ");

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("WL_WZCJ t1, WL_WZZD t ");
			Sql_count
					.append("WHERE t.WZXH = t1.WZXH AND t1.SYZT = 1 AND t1.CJXH =:CJXH");
			Sql_count.append(" ORDER BY KFXH ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZZD);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人列表查询失败！");
		}
	}

	/**
	 * 物质查询。
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doManufacturerForSCCJQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		long CJXH = Long.parseLong(req.get("cnd") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("CJXH", CJXH);
		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT t1.CJXH as CJXH ,t.WZXH as WZXH, t.KFXH as KFXH , t.ZBLB as ZBLB,t.HSLB as HSLB,t.WZMC as WZMC, t.WZGG as WZGG , t.WZDW as WZDW, t.PYDM as PYDM, t.WBDM as WBDM, t.JXDM as JXDM,t.QTDM as QTDM, t.WZZT as WZZT, t.KWBH as KWBH, t.BKBZ as BKBZ, t.YCWC as YCWC,t.JLBZ as JLBZ, t.GCSL as GCSL, t.DCSL as DCSL,t.WZTM as WZTM,t.ZDTM as ZDTM FROM ");
			sql_list.append("WL_WZCJ t1, WL_WZZD t  ");
			sql_list.append("WHERE t.WZXH = t1.WZXH AND t1.SYZT = 1 AND t1.CJXH =:CJXH ");
			sql_list.append(" ORDER BY KFXH ");

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("WL_WZCJ t1, WL_WZZD t ");
			Sql_count
					.append("WHERE t.WZXH = t1.WZXH AND t1.SYZT = 1 AND t1.CJXH =:CJXH");
			Sql_count.append(" ORDER BY KFXH ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_WZZD_SCCJ);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人列表查询失败！");
		}
	}

	/**
	 * 证件信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doCertificateQuery(Map<String, Object> req,
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
		Map<String, Object> map = (Map<String, Object>) req.get("cnd");

		long WZXH = Long.parseLong(map.get("WZXH") + "");
		long CJXH = Long.parseLong(map.get("CJXH") + "");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", WZXH);
		parameters.put("CJXH", CJXH);
		try {
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT DISTINCT  t.JLXH as JLXH,t.WZXH as WZXH, t.CJXH as CJXH,t.ZJXH as ZJXH,t1.ZJBH as ZJBH, t1.ZJLX as ZJLX,t1.FZRQ as FZRQ,t1.SXRQ as SXRQ,t1.ZJZT as ZJZT,0 as TPBZ FROM ");
			sql_list.append(" WL_WZZJ t, WL_ZJXX t1  ");
			sql_list.append("WHERE t.WZXH =:WZXH and  t.ZJXH = t1.ZJXH and (t.CJXH = :CJXH OR t.CJXH = 0) ");

			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append(" WL_WZZJ t, WL_ZJXX t1  ");
			Sql_count
					.append("WHERE t.WZXH =:WZXH and  t.ZJXH = t1.ZJXH and (t.CJXH = :CJXH OR t.CJXH = 0) ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			/*
			 * parameters.put("first", first * pageSize); parameters.put("max",
			 * pageSize);
			 */
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_ZJXX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人列表查询失败！");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveCompar(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		String resMsg = "对照失败！";
		try {
			List<Map<String, Object>> mats = (List<Map<String, Object>>) body
					.get("WL_WZXX");
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				Map<String, Object> parameters = new HashMap<String, Object>();

				Long CJXH = Long.parseLong(mat.get("CJXH") + "");
				Long WZXH = Long.parseLong(mat.get("WZXH") + "");
				Long ZJXH = Long.parseLong(mat.get("ZJXH") + "");

				parameters.put("CJXH", CJXH);
				parameters.put("WZXH", WZXH);
				parameters.put("ZJXH", ZJXH);

				String hqlWhere = "CJXH=:CJXH and WZXH=:WZXH and ZJXH=:ZJXH";
				long l = dao.doCount("WL_WZZJ", hqlWhere, parameters);
				if (l > 0) {
					resMsg = "存在已对照的物资，不能重复对照!";
					throw new RuntimeException("存在已对照的物资，不能重复对照!");
				}
				dao.doSave("create", BSPHISEntryNames.WL_WZZJ, parameters, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, resMsg);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryCheckWZ(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws

	ModelDataOperationException {
		Map<String, Object> reqMap = (Map<String, Object>) req.get("body");
		long CJXH = Long.parseLong(reqMap.get("CJXH") + "");
		long ZJXH = Long.parseLong(reqMap.get("ZJXH") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZJXH", ZJXH);
		parameters.put("CJXH", CJXH);

		StringBuffer sql_list = new StringBuffer(
				"SELECT WZXH as WZXH FROM WL_WZZJ WHERE CJXH =:CJXH and ZJXH =:ZJXH ");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		res.put("ret", inofList);
	}
}
