package phis.application.cfg.source;

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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 分类类别维护
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ConfigClassifyModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryModule.class);

	public ConfigClassifyModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doClassifyListQuery(Map<String, Object> req,
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
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			parameters.put("JGID", JGID);

			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"SELECT t.LBXH as LBXH,t.JGID as JGID,t.LBMC as LBMC,t.ZFBZ as ZFBZ FROM WL_FLLB t WHERE t.JGID = :JGID order by LBXH");
			// 返会列数的查询语句
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("WL_FLLB t ");
			Sql_count.append("WHERE (t.JGID =:JGID) ");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> infoList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			// for (int i = 0; i < infoList.size(); i++) {
			// System.out.println(infoList.get(i).toString());
			// }
			SchemaUtil.setDictionaryMassageForList(infoList,
					BSPHISEntryNames.WL_FLLB);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", infoList);
		} catch (PersistentDataOperationException e) {
			logger.error("分类类别列表查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "分类类别列表查询失败");
		}
	}

	public void doSaveDetailList(List<Map<String, Object>> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Integer LBXH = (Integer) body.get(0).get("LBXH");
		try {
			dao.removeByFieldValue("LBXH", LBXH, BSPHISEntryNames.WL_FLGZ);
			if (body.get(0).get("GZMC") == null) {
				return;
			}
			for (Map<String, Object> detail : body) {
				dao.doSave("create", BSPHISEntryNames.WL_FLGZ, detail, false);
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

	}

	@SuppressWarnings("unchecked")
	public void doExcute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		// System.out.println(body.get("LBXH").getClass().getName());
		// System.out.println(body.get("ZFBZ").getClass().getName());
		Integer LBXH = Integer.parseInt(body.get("LBXH") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LBXH", LBXH);

		try {
			// 医院库房维护中使用的分类类别在分类类别维护中不能注销
			Long countKFXX = 0l;
			countKFXX = dao.doCount("WL_KFXX", "LBXH=:LBXH", parameters);
			// System.out.println("----------"+countKFXX);
			res.put("countKFXX", countKFXX);
			if (countKFXX != 0) {
				return;
			}
			// 只有维护了规则能才启用，没有维护规则，不能启用
			Long countFLGZ = -1l;
			if (Integer.parseInt((String) body.get("ZFBZ")) == 1) {
				countFLGZ = dao.doCount("WL_FLGZ", "LBXH=:LBXH", parameters);
				if (countFLGZ == 0) {
					res.put("countFLGZ", countFLGZ);
					return;
				}
			}
			res.put("countFLGZ", countFLGZ);

			parameters.put("ZFBZ", Integer.parseInt((String) body.get("ZFBZ")));
			dao.doUpdate("UPDATE WL_FLLB SET ZFBZ=:ZFBZ WHERE LBXH=:LBXH",
					parameters);
			if (Integer.parseInt((String) body.get("ZFBZ")) == -1) {
				dao.doRemove("LBXH", LBXH, BSPHISEntryNames.WL_FLZD);
			} else if (Integer.parseInt((String) body.get("ZFBZ")) == 1) {
				parameters.clear();
				parameters.put("LBXH", LBXH);
				parameters.put("FLMC", (String) body.get("LBMC"));
				parameters.put("FLBM", LBXH);
				parameters.put("JGID", JGID);
				parameters.put("SJFL", -1);
				// System.out.println(parameters.toString()+"-------");
				dao.doSave("create", BSPHISEntryNames.WL_FLZD, parameters,
						false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();

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

	@SuppressWarnings("unchecked")
	public void doIsClassifyUsed(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LBXH", body.get("LBXH"));
		try {
		  if(!"".equals(body.get("LBXH")) && !"null".equals(body.get("LBXH"))){
			Boolean isStartUsing = false;
			Map<String, Object> fllb_map = dao.doLoad(BSPHISEntryNames.WL_FLLB,
					Integer.parseInt(body.get("LBXH") + ""));
			if (fllb_map.containsKey("ZFBZ")
					&& Integer.parseInt(fllb_map.get("ZFBZ") + "") == 1) {
				isStartUsing = true;
			}

			Long countKFXX = dao.doCount("WL_KFXX", "LBXH=:LBXH", parameters);

			Long countFLZD = 0L;
			// dao.doCount(BSPHISEntryNames.WL_FLZD,
			// "LBXH=:LBXH", parameters);
			Map<String, Object> map = (Map<String, Object>) dao
					.doLoad("select count(*) as count from WL_FLZD a,WL_WZFL b where a.LBXH=:LBXH and b.ZDXH=a.ZDXH",
							parameters);
			if (map.size() > 0 && map.get("count") != null) {
				countFLZD = Long.parseLong(map.get("count") + "");
			}
			res.put("isStartUsing", isStartUsing);
			res.put("countKFXX", countKFXX);
			res.put("countFLZD", countFLZD);
		  }
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doDeleteClassify(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			dao.doRemove(body.get("LBXH"), BSPHISEntryNames.WL_FLLB);
			dao.doRemove("LBXH", body.get("LBXH"), BSPHISEntryNames.WL_FLGZ);
		} catch (PersistentDataOperationException e) {
			logger.error("Delete failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	public void doDeleteFLGZ(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Integer gzxh = (Integer) req.get("body");
		try {
			dao.doRemove("GZXH", gzxh, BSPHISEntryNames.WL_FLGZ);
		} catch (PersistentDataOperationException e) {
			logger.error("Delete failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doIsLBMCUsed(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			// 已注销的分类类别不能修改
			if (body.get("OP").equals("update")) {
				Map<String, Object> map_fllb = dao.doLoad(
						BSPHISEntryNames.WL_FLLB,
						Integer.parseInt(body.get("LBXH") + ""));
				if (map_fllb.size() == 0) {

				}
				Integer zfzt = Integer.parseInt(map_fllb.get("ZFBZ") + "");
				if (zfzt == -1) {
					res.put("isClassifyCancel", true);
					return;
				}
			}

			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			parameters.clear();
			parameters.put("LBMC", body.get("LBMC"));
			parameters.put("JGID", JGID);
			Long countLBMC = 0L;
			if (body.get("OP").equals("create")) {
				countLBMC = dao.doCount("WL_FLLB", "LBMC=:LBMC AND JGID=:JGID",
						parameters);
			} else if (body.get("OP").equals("update")) {
				parameters.put("LBXH", body.get("LBXH"));
				countLBMC = dao
						.doCount(BSPHISEntryNames.WL_FLLB,
								"LBMC=:LBMC AND LBXH!=:LBXH AND JGID=:JGID",
								parameters);
			}

			res.put("countLBMC", countLBMC);
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "操作失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveClassify(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String LBMC = (String) body.get("LBMC");
		Long LBXH = 0L;
		// System.out.println(body.toString());
		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			String op = (String) body.get("OP");
			if ("create".equals(op)) {
				parameters.put("LBMC", LBMC);
				parameters.put("JGID", JGID);
				parameters.put("ZFBZ", -1);
				parameters.put("SYBZ", 0);
				Map<String, Object> map_fllb = dao.doSave(op,
						BSPHISEntryNames.WL_FLLB, parameters, false);
				LBXH = Long.parseLong(map_fllb.get("LBXH") + "");
				parameters.clear();
				// parameters.put("LBXH", LBXH);
				// parameters.put("FLMC", LBMC);
				// parameters.put("FLBM", LBXH);
				// parameters.put("JGID", JGID);
				// parameters.put("SJFL", -1);
				// dao.doSave(op, BSPHISEntryNames.WL_FLZD, parameters, false);
			} else if ("update".equals(op)) {
				if (body.get("LBXH") != null && body.get("LBXH") != "") {
					LBXH = Long.parseLong(body.get("LBXH") + "");
				}
				parameters.put("LBMC", LBMC);
				parameters.put("LBXH", LBXH);
				dao.doSave(op, BSPHISEntryNames.WL_FLLB, parameters, false);
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

	}
}
