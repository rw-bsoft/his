package phis.application.war.source;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import phis.source.utils.BSHISUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class NurseRecordModule {
	protected Logger logger = LoggerFactory.getLogger(NurseRecordModule.class);
	private BaseDAO dao;

	public NurseRecordModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取机构编号
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getJGBH() throws ModelDataOperationException {
		String jgbh = "";
		try {
			List<Map<String, Object>> result = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("SELECT JGBH as JGBH FROM ENR_JG01 WHERE JGMC = '一般护理记录单'");
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			if (result.size() > 0) {
				jgbh = String.valueOf(result.get(0).get("JGBH"));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("护理记录模块,获取机构编号失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块,获取机构编号失败");
		}
		return jgbh;
	}

	/**
	 * 查询护理结构表单
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void queryRecordMete(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> result = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuilder sqlBuilder = new StringBuilder();

			String jgbh = getJGBH();
			sqlBuilder
					.append("SELECT ENR_JG02.XMBH, ENR_JG02.JGBH, ENR_JG02.YSBH, ENR_JG02.XMMC, ENR_JG02.XSMC, ");
			sqlBuilder
					.append(" ENR_JG02.XMQZ, ENR_JG02.KSLH, ENR_JG02.JSLH, ENR_JG02.HDBZ, ENR_JG02.YSKZ, ");
			sqlBuilder
					.append(" ENR_JG02.SJGS, ENR_JG02.ZCZSX, ENR_JG02.ZCZXX, ENR_JG02.YXZSX, ENR_JG02.YXZXX, ");
			sqlBuilder
					.append(" ENR_JG02.SFBT, ENR_JG02.JZBJ, ENR_JG02.FZYT, ENR_JG02.XMDC, ENR_JG02.YMCLFS,");
			sqlBuilder
					.append(" ENR_JG02.HHJG, '' as yskz_bnt, ENR_JBYS.YSLX, ENR_JBYS.SJLX, ENR_JG02.XMKD, ");
			sqlBuilder
					.append(" ENR_JG02.ZDYXM FROM ENR_JBYS RIGHT OUTER JOIN ENR_JG02 ON ENR_JBYS.YSBH = ENR_JG02.YSBH ");
			sqlBuilder
					.append("  WHERE ENR_JG02.JGBH = :al_jgbh  order by ENR_JG02.YSBH ");
			// sqlBuilder.append("and ENR_JG02.JGID = :al_jgids"); 结构表中无机构ID

			parameters.put("al_jgbh", jgbh);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			res.put("body", result);
		} catch (PersistentDataOperationException e) {
			logger.error("护理记录模块,查询记录模型失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块,查询记录模型失败");
		}
	}

	/**
	 * 保存新增操作
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void save(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> parMap = (Map<String, Object>) req.get("PARAM");
			UserRoleToken user = UserRoleToken.getCurrent();
			String userid = user.getUserId() + "";
			String jgid = user.getManageUnitId();
			String tsStr = String.valueOf(parMap.get("TIME"));
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ts = Timestamp.valueOf(tsStr);
			// SimpleDateFormat sdfdatetime = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// Date date =
			// sdfdatetime.parse(sdfdatetime.format(BSHISUtil.toDate(tsStr)));
			String jgbh = getJGBH();
			parameters.put("JGID", jgid);
			parameters.put("ZYH",
					Long.parseLong(String.valueOf(parMap.get("ZYH"))));
			parameters.put("JGBH", Long.parseLong(jgbh));
			parameters.put("JLMC", "一般护理记录");
			parameters.put("BLLX", 2);
			parameters.put("DLLB", 0);
			parameters.put("DLJ", "");
			parameters.put("JLHS", 0);
			parameters.put("HYBZ", 0);
			parameters.put("JLSJ", ts);
			parameters.put("SXSJ", ts);
			parameters.put("XTSJ", ts);
			parameters.put("SXBQ",
					Long.parseLong(String.valueOf(parMap.get("BRBQDM"))));
			parameters.put("SXHS", userid);// 当前操作工号
			parameters.put("WCQM", 0);
			parameters.put("SYBZ", 0);
			parameters.put("SYHS", "");
			parameters.put("SYQM", 0);
			parameters.put("DYBZ", 0);
			parameters.put("JLZT", 0);
			parameters.put("ZJLX", 0);
			parameters.put("BLLB", 0);
			parameters.put("MBLB", 0);
			parameters.put("MBBH", 0);
			// parameters.put("ifnew", 1);
			parameters.put("DLHHBZ", 0);

			Map<String, Object> resuMap = dao.doSave("create",
					BSPHISEntryNames.ENR_JL01, parameters, false);
			Map<String, Object> hlmxbd = null, map = null;
			List<Map<String, Object>> list = (List<Map<String, Object>>) req
					.get("body");
			String ls_xsmc, ls_xmqz;
			for (int i = 0; i < list.size(); i++) {
				map = list.get(i);
				String ll_xmbh = String.valueOf(map.get("XMBH"));
				if ("".equals(ll_xmbh) || "null".equals(ll_xmbh)) {
					continue;
				}
				// String ll_mxbh = String.valueOf(map.get("MXBH"));
				// if("".equals(ll_mxbh) || "null".equals(ll_mxbh)){
				// continue;
				// }
				ls_xsmc = String.valueOf(map.get("XSMC"));
				ls_xmqz = String.valueOf(map.get("XMQZ"));
				if ("".equals(ls_xmqz) || "null".equals(ls_xmqz)) {// 项目取值为空则不保存
					continue;
				}
				hlmxbd = new HashMap<String, Object>();
				hlmxbd.put("JLBH", resuMap.get("JLBH"));
				hlmxbd.put("XMBH",
						Long.parseLong(String.valueOf(map.get("XMBH"))));
				hlmxbd.put("XMMC", String.valueOf(map.get("XMMC")));
				hlmxbd.put("XSMC", String.valueOf(map.get("XSMC")));
				hlmxbd.put("XMQZ", "");
				hlmxbd.put("KSLH",
						Long.parseLong(String.valueOf(map.get("KSLH"))));
				hlmxbd.put("JSLH",
						Long.parseLong(String.valueOf(map.get("JSLH"))));
				hlmxbd.put("HDBZ",
						Long.parseLong(String.valueOf(map.get("HDBZ"))));
				hlmxbd.put("YMCLFS",
						Long.parseLong(String.valueOf(map.get("YMCLFS"))));
				hlmxbd.put("HHJG",
						Long.parseLong(String.valueOf(map.get("HHJG"))));
				hlmxbd.put("XGBZ", 0);
				hlmxbd.put("GXBZ", 0);
				hlmxbd.put("JGID", jgid);

				hlmxbd.put("XSMC", ls_xsmc);
				hlmxbd.put("XMQZ", ls_xmqz);
				dao.doSave("create", BSPHISEntryNames.ENR_JL02, hlmxbd, false);
			}
		} catch (Exception e) {
			logger.error("护理记录模块，进行保存操作失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，保存操作失败");
		}

	}

	/**
	 * 更新护理记录录入
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void update(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parMap = (Map<String, Object>) req.get("PARAM");
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid = user.getUserId() + "";
		String jgid = user.getManageUnitId();
		String jlbh = String.valueOf(parMap.get("JLBH"));
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder
				.append("SELECT MXBH as MXBH , XMBH as XMBH, XMQZ as XMQZ FROM ENR_JL02 WHERE JLBH= :JLBH AND JGID = :JGID");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JLBH", Long.parseLong(jlbh));
		parameters.put("JGID", jgid);
		try {
			List<Map<String, Object>> enr_jl02 = (List<Map<String, Object>>) dao
					.doSqlQuery(sqlBuilder.toString(), parameters);
			Object object = req.get("body");
			Map<String, Object> map = null, hlmxbd = null, tmpMap = null;
			boolean updateFlag = false;
			String ls_xmqz, ls_xsmc;
			long mxbh = 0;
			if (object != null) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) object;
				// if(list.size() > 0){//录入表单不为空时，根据记录编号和机构ID删除表中已存在的记录
				// sqlBuilder = new StringBuilder();
				// sqlBuilder.append("DELETE FROM ENR_JL02 WHERE JLBH= :JLBH AND JGID = :JGID");
				// dao.doUpdate(sqlBuilder.toString(), parameters);
				// }
				for (int i = 0; i < list.size(); i++) {
					map = list.get(i);
					updateFlag = false;
					ls_xmqz = String.valueOf(map.get("XMQZ"));
					ls_xsmc = String.valueOf(map.get("XSMC"));
					hlmxbd = new HashMap<String, Object>();
					if ("".equals(ls_xmqz) || "null".equals(ls_xmqz)) {// 项目取值为空则不保存
						continue;
					} else {
						for (int j = 0; j < enr_jl02.size(); j++) {
							tmpMap = enr_jl02.get(j);
							// 如果该明细编号已经在数据库中存在则表示更新
							if (String.valueOf(tmpMap.get("XMBH")).equals(
									String.valueOf(map.get("XMBH")))) {
								mxbh = Long.parseLong(String.valueOf(tmpMap
										.get("MXBH")));
								updateFlag = true;
								// 如果这次更新的项目取值与数据库中的数据不一致，则修改标志为1否则使用本身保存的修改标志
								if (ls_xmqz.equals(String.valueOf(tmpMap
										.get("XMQZ")))) {
									hlmxbd.put("XGBZ", tmpMap.get("XGBZ"));
								} else {
									hlmxbd.put("XGBZ", 1);// 修改标志为1
								}
								break;
							}
						}
					}

					if (updateFlag) {// 更新时需要设计院有的明细编号
						hlmxbd.put("MXBH", mxbh);
					} else {// 对于新增加的修改标志为0
						hlmxbd.put("XGBZ", 0);
					}
					hlmxbd.put("JLBH", Long.parseLong(jlbh));
					hlmxbd.put("XMBH",
							Long.parseLong(String.valueOf(map.get("XMBH"))));
					hlmxbd.put("XMMC", String.valueOf(map.get("XMMC")));
					hlmxbd.put("XSMC", String.valueOf(map.get("XSMC")));
					hlmxbd.put("KSLH",
							Long.parseLong(String.valueOf(map.get("KSLH"))));
					hlmxbd.put("JSLH",
							Long.parseLong(String.valueOf(map.get("JSLH"))));
					hlmxbd.put("HDBZ",
							Long.parseLong(String.valueOf(map.get("HDBZ"))));
					hlmxbd.put("YMCLFS",
							Long.parseLong(String.valueOf(map.get("YMCLFS"))));
					hlmxbd.put("HHJG",
							Long.parseLong(String.valueOf(map.get("HHJG"))));
					hlmxbd.put("JGID", jgid);
					hlmxbd.put("XSMC", ls_xsmc);
					hlmxbd.put("XMQZ", ls_xmqz);
					if (updateFlag) {
						dao.doSave("update", BSPHISEntryNames.ENR_JL02, hlmxbd,
								false);
					} else {
						dao.doSave("create", BSPHISEntryNames.ENR_JL02, hlmxbd,
								false);
					}
				}
			}
		} catch (Exception e) {
			logger.error("护理记录模块，更新护理明细表单失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，更新护理明细表单失败");
		}

	}

	/**
	 * 根据记录编号获取护理明细表单
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void queryENR_JL02ByJLBH(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		StringBuilder sqlBuilder = new StringBuilder();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		sqlBuilder
				.append("SELECT ENR_JL02.MXBH as MXBH, ENR_JL02.JLBH as JLBH, ENR_JL02.XMBH as XMBH, ENR_JL02.XMMC as XMMC, ");
		sqlBuilder
				.append(" ENR_JL02.XSMC as XSMC, ENR_JL02.XMQZ as XMQZ, ENR_JL02.KSLH as KSLH, ENR_JL02.JSLH as JSLH, ");
		sqlBuilder
				.append(" ENR_JL02.HDBZ as HDBZ, ENR_JL02.YMCLFS as YMCLFS, ENR_JL02.HHJG as HHJG, ENR_JL02.XGBZ as XGBZ, 0 AS GXBZ ");
		sqlBuilder
				.append(" FROM ENR_JL02  WHERE ENR_JL02.JLBH = :al_jlbh AND (ENR_JL02.JGID = :al_jgid)");
		parameters.put("al_jgid", jgid);
		Map<String, Object> map = (Map<String, Object>) req.get("body");
		parameters.put("al_jlbh",
				Long.parseLong(String.valueOf(map.get("JLBH"))));
		try {
			List<Map<String, Object>> list = (List<Map<String, Object>>) dao
					.doSqlQuery(sqlBuilder.toString(), parameters);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			logger.error("护理记录模块，根据记录编号获取护理明细表单失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，根据记录编号获取护理明细表单失败");
		}
	}

	/**
	 * 删除护理记录
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void deleteENR_JL01(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = null;
		StringBuilder sqlBuilder = new StringBuilder();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ll_jlbh",
				((Map<String, Object>) req.get("body")).get("JLBH"));
		sqlBuilder
				.append("Select count(*) as ll_count From ENR_JL01 Where JLBH = :ll_jlbh And jlzt <> 9");
		try {
			list = dao.doSqlQuery(sqlBuilder.toString(), parameters);
			String ll_count = String.valueOf(list.get(0).get("LL_COUNT"));
			int count = Integer.parseInt(ll_count);
			if (count < 1) {
				res.put(Service.RES_MESSAGE, "未能找到匹配的护理记录");
			} else {
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("Update ENR_JL01 Set jlzt = 9 Where JLBH = :ll_jlbh And jlzt <> 9");
				dao.doSqlUpdate(sqlBuilder.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("护理记录模块，根据记录编号获取护理明细表单失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，根据记录编号获取护理明细表单失败");
		}
	}

	public static void main(String[] args) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		ts = Timestamp.valueOf("2013-05-31 12:14:11");
		// System.out.println(ts);
	}

	public void doSaveHLJH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parMap = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		parMap.put("JGID", jgid);
		Date ksrq = BSHISUtil.toDate((String) parMap.get("KSRQ"));
		Date tzrq = BSHISUtil.toDate((String) parMap.get("TZRQ"));
		if (ksrq!=null&&tzrq!=null&&ksrq.compareTo(tzrq) == 1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "停止日期不允许早于开始时间");
		}
		try {
			// parMap.put("KSRQ", ksrq);
			// parMap.put("TZRQ", tzrq);
			// System.out.println(parMap);
			if (parMap.containsKey("JLBH") && parMap.get("JLBH") != null
					&& parMap.get("JLBH") != "") {
				dao.doSave("update", BSPHISEntryNames.EMR_HLJH, parMap, false);
			} else {
				dao.doSave("create", BSPHISEntryNames.EMR_HLJH, parMap, false);
			}
		} catch (Exception e) {
			logger.error("护理记录模块，更新护理明细表单失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，更新护理明细表单失败");
		}

	}

	public void doRemoveHLJH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parMap = (Map<String, Object>) req.get("body");
		try {
			if (parMap.containsKey("JLBH") && parMap.get("JLBH") != null
					&& parMap.get("JLBH") != "") {
				Long jlbh = Long.parseLong(parMap.get("JLBH") + "");
				dao.doRemove(jlbh, BSPHISEntryNames.EMR_HLJH);
			}
		} catch (Exception e) {
			logger.error("护理记录模块，删除护理计划失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "护理记录模块，删除护理计划失败");
		}

	}
}
