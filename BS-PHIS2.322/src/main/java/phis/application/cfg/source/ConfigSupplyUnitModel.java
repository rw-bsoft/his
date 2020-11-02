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
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 供货单位维护
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ConfigSupplyUnitModel implements BSPHISEntryNames {

	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigSupplyUnitModel.class);

	public ConfigSupplyUnitModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doUpdateConfigSupplyUnitNormal(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("DWXH", Long.parseLong(body.get("DWXH").toString()));
			parameters.put("DWZT", 1);
			dao.doUpdate("update " + schemaList
					+ " set DWZT=:DWZT WHERE DWXH=:DWXH", parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doUpdateConfigSupplyUnitCancel(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("DWXH", Long.parseLong(body.get("DWXH").toString()));
			parameters.put("DWZT", -1);
			dao.doUpdate("update " + schemaList
					+ " set DWZT=:DWZT WHERE DWXH=:DWXH", parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doDeleteConfigSupplyUnit(Map<String, Object> body,
			String schemaList, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("DWXH", Long.parseLong(body.get("DWXH").toString()));
			dao.doUpdate("delete from " + schemaList + " where DWXH=:DWXH",
					parameters);
			dao.doUpdate("delete from WL_ZJXX where DXXH=:DWXH and ZJDX=2",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doIsUnitUsed(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("DWXH", Long.parseLong(body.get("DWXH").toString()));
			Long countRK01 = dao.doCount("WL_RK01", "DWXH=:DWXH", parameters);
			Long countBHXX = dao.doCount("WL_BHXX", "DWXH=:DWXH", parameters);
			Long countCSZC = dao.doCount("WL_CSZC", "GHDW=:DWXH", parameters);
			res.put("countRK01", countRK01);
			res.put("countBHXX", countBHXX);
			res.put("countCSZC", countCSZC);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveSupplyUnitInfomations(String op,
			Map<String, Object> body, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> req = null;
		Long dwxh = null;
		ArrayList<Map<String, Object>> certificateInformations = (ArrayList<Map<String, Object>>) body
				.get("certificateinftab");
		try {
			// 判断：一个库房内的单位名称加上公共的单位名称不能重复
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			String TopJGID = ParameterUtil.getTopUnitId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("dwmc", body.get("DWMC"));
			parameters.put("JGID", JGID);
			parameters.put("TopJGID", TopJGID);
			// parameters.put("kfxh", 0);
			Long count = 0L;
			if ("update".equals(op)) {
				dwxh = Long.parseLong(body.get("DWXH") + "");
				parameters.put("dwxh", dwxh);
				count = dao
						.doCount(
								"WL_GHDW",
								"DWMC=:dwmc AND DWXH!=:dwxh and (JGID=:JGID or JGID=:TopJGID)",
								parameters);
			} else if ("create".equals(op)) {
				count = dao.doCount("WL_GHDW",
						"DWMC=:dwmc and (JGID=:JGID or JGID=:TopJGID)",
						parameters);
			}
			if (count > 0) {
				res.put("count", count);
				return;
			}

			// 判断所有的营业执照和经营许可证编号是唯一的，不可重复的
			if (certificateInformations != null
					&& certificateInformations.size() > 0) {
				Map<String, Object> parametersZJXX = new HashMap<String, Object>();
				Long countZJXX = 0L;
				String ZJBH = "";
				for (Map<String, Object> certificateMap : certificateInformations) {

					ZJBH = certificateMap.get("ZJBH") + "";
					parametersZJXX.put("ZJBH", ZJBH);
					if (certificateMap.containsKey("ZJXH")
							&& certificateMap.get("ZJXH") != "") {
						Long ZJXH = Long.parseLong(certificateMap.get("ZJXH")
								+ "");
						parametersZJXX.put("ZJXH", ZJXH);
						countZJXX = dao.doCount("WL_ZJXX",
								" ZJBH=:ZJBH AND ZJXH!=:ZJXH ", parametersZJXX);
					} else {
						countZJXX = dao.doCount("WL_ZJXX", " ZJBH=:ZJBH",
								parametersZJXX);
					}
					if (countZJXX > 0) {
						res.put("countZJXX", countZJXX);
						return;
					}
					parametersZJXX.clear();
				}
			}

			// 保存供货单位(WL_GHDW)基本信息
			Integer kfxh = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
			}
			body.put("KFXH", kfxh);
			req = dao.doSave(op, BSPHISEntryNames.WL_GHDW, body, false);
			// dwxh = Long.parseLong((op.equals("create") ? req.get("DWXH") :
			// body
			// .get("DWXH")) + "");
			if ("create".equals(op)) {
				dwxh = Long.parseLong(req.get("DWXH") + "");
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}

		// 保存证件信息(WL_ZJXX)
		try {
			if (dwxh == null) {
				return;
			}
			dao.removeByFieldValue("DXXH", dwxh, BSPHISEntryNames.WL_ZJXX);
			if (certificateInformations != null
					&& certificateInformations.size() > 0) {
				for (Map<String, Object> certificateMap : certificateInformations) {
					certificateMap.put("DXXH", dwxh);
					dao.doSave("create", BSPHISEntryNames.WL_ZJXX,
							certificateMap, false);
				}
			}
			// if (certificateInformations != null
			// && certificateInformations.size() > 0) {
			// for (Map<String, Object> certificateMap :
			// certificateInformations) {
			// if ("create".equals(certificateMap.get("_opStatus"))) {
			// certificateMap.put("DXXH", dwxh);
			// dao.doSave("create", BSPHISEntryNames.WL_ZJXX,
			// certificateMap, false);
			// } else {
			// dao.doSave("update", BSPHISEntryNames.WL_ZJXX,
			// certificateMap, false);// 如果需要以后改成只更新修改过的数据
			// }
			// }
			// }
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}

	}

	public void doGetDWZT(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			results = dao.doLoad(BSPHISEntryNames.WL_GHDW,
					Long.parseLong(body.get("DWXH").toString()));
			res.put("dwzt", results.get("DWZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doSupplyUnitQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID

		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		String TopJGID = ParameterUtil.getTopUnitId();
		Integer kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			parameters.put("JGID", JGID);
			parameters.put("KFXH", kfxh);
			parameters.put("TopJGID", TopJGID);

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.DWXH as DWXH,t.DWMC as DWMC, t.QYXZ as QYXZ,t.DWLX as DWLX,t.FRDB as FRDB, "
							+ "t.PYDM as PYDM, t.WBDM as WBDM,t.JXDM as JXDM,t.QTDM as QTDM,t.KHYH as KHYH, t.YHZH as YHZH, "
							+ "t.LXDZ as LXDZ,t.YZBM as YZBM,t.LXRY as LXRY, t.DHHM as DHHM,t.SJHM as SJHM,t.CZHM as CZHM,"
							+ "t.HLWZ as HLWZ,t.DZYJ as DZYJ,t.QYJJ as QYJJ,t.XKZH as XKZH,t.XKXQ as XKXQ,"
							+ "t.YYZZ as YYZZ,t.YYZZXQ as YYZZXQ,t.CWBM as CWBM,t.DWZT as DWZT FROM ");
			sql_list.append("WL_GHDW t ");
			sql_list.append("WHERE (t.JGID =:JGID AND t.KFXH=:KFXH) ");
			sql_list.append("OR t.JGID=:TopJGID ");
			sql_list.append("ORDER BY DWXH");
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
					BSPHISEntryNames.WL_GHDW);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("供货单位列表查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "供货单位列表查询失败");
		}
	}

	public void doSavePhoto(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tpxx", body.get("TPXX"));
		parameters.put("zjxh", ((Integer) body.get("ZJXH")).longValue());
		try {
			dao.doUpdate("UPDATE WL_ZJXX SET TPXX=:tpxx WHERE ZJXH=:zjxh",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doRemovePhoto(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZJXH", ((Integer) body.get("ZJXH")).longValue());
		parameters.put("TPXX", "");
		try {
			dao.doUpdate("UPDATE WL_ZJXX SET TPXX=:TPXX WHERE ZJXH=:ZJXH",
					parameters);
			// dao.doRemove(body.get("TPXX"), BSPHISEntryNames.FileResources);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doGetJGID(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			results = dao.doLoad(BSPHISEntryNames.WL_GHDW,
					Long.parseLong(body.get("DWXH").toString()));
			if (results == null || results.size() == 0) {
				return;
			}
			res.put("jgid", results.get("JGID"));
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}
}
