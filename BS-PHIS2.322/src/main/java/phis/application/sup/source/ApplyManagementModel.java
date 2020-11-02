package phis.application.sup.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ApplyManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ApplyManagementModel.class);

	public ApplyManagementModel(BaseDAO dao) {
		this.dao = dao;

	}

	@SuppressWarnings("unchecked")
	public void doDelete(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("body") != null) {
			body = (Map<String, Object>) req.get("body");
		}
		Long DJXH = Long.parseLong(body.get("DJXH") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parametersSLXX = new HashMap<String, Object>();
		parameters.put("DJXH", DJXH);
		try {
			dao.doRemove(DJXH, BSPHISEntryNames.WL_CK01);
			// 如果本次实发数量等于WL_SLXX中对应记录的科室申请数量WZSL则更新WL_SLXX中SLZT为提交状态（SLZT =0）
			List<Map<String, Object>> list = dao
					.doQuery(
							"select WZSL as WZSL,SLSL as SLSL,SLXH as SLXH from WL_CK02 where DJXH=:DJXH",
							parameters);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				Double WZSL = Double.parseDouble(map.get("WZSL") + "");
				Double SLSL = Double.parseDouble(map.get("SLSL") + "");
				if (WZSL.equals(SLSL)) {
					parametersSLXX.put("JLXH",
							Long.parseLong(map.get("SLXH") + ""));
					dao.doUpdate("update WL_SLXX set SLZT=0 where JLXH=:JLXH",
							parametersSLXX);
				}
			}
			dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_CK02);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSLXXKSQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {

		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("cnd") != null) {
			body = (Map<String, Object>) req.get("cnd");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		Integer KFXH = Integer.parseInt(body.get("KFXH") + "");
		Integer ZBLB = Integer.parseInt(body.get("ZBLB") + "");
		Long SLKS = 0L;
		if (body.containsKey("KSDM") && body.get("KSDM") != "") {
			SLKS = Long.parseLong(body.get("KSDM") + "");
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		parameters.put("ZBLB", ZBLB);

		StringBuffer sql_list = new StringBuffer();
		if (SLKS == 0) {
			sql_list.append(" select SLKS as SLKS,SLGH as SLGH "
					+ "from WL_SLXX where KFXH=:KFXH and ZBLB=:ZBLB and SLZT=0 group by SLKS,SLGH");
		} else {
			parameters.put("SLKS", SLKS);
			sql_list.append(" select SLKS as SLKS,SLGH as SLGH "
					+ "from WL_SLXX where KFXH=:KFXH and ZBLB=:ZBLB and SLKS=:SLKS and SLZT=0 group by SLKS,SLGH");
		}

		try {

			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("(" + sql_list.toString() + ")");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_SLXX_KS);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);

		} catch (PersistentDataOperationException e) {
			logger.error("引入科室加载失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "引入科室加载失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doSLXXDetailQuery(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		Map<String, Object> body = new HashMap<String, Object>();
		if (req.get("cnd") != null) {
			body = (Map<String, Object>) req.get("cnd");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		Integer KFXH = Integer.parseInt(body.get("KFXH") + "");
		Integer ZBLB = Integer.parseInt(body.get("ZBLB") + "");
		Long SLKS = Long.parseLong(body.get("SLKS") + "");
		String SLGH = body.get("SLGH") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", KFXH);
		parameters.put("ZBLB", ZBLB);
		parameters.put("SLKS", SLKS);
		parameters.put("SLGH", SLGH);

		StringBuffer sql_list = new StringBuffer();
		sql_list.append(" select a.JLXH as JLXH,a.JLXH as SLXH,a.WZXH as WZXH,a.SLKS as SLKS,a.SLGH as SLGH,"
				+ " a.WZMC as WZMC,a.WZGG as WZGG,b.glfs as glfs,(a.WZSL- nvl((select sum(wzsl)  from wl_ck02 where slxh=a.jlxh),0)) as WZSL,"
				+ "(a.WZSL- nvl((select sum(wzsl)  from wl_ck02 where slxh=a.jlxh),0)) as SLSL,0 as WFSL,a.WZDW as WZDW,"
				+ " a.SLSJ as SLSJ,"
				+ "(select sum(b.WZSL-b.YKSL) from wl_wzkc b where b.KFXH = a.kfxh  and b.ZBLB =a.zblb  and wzxh=a.wzxh) as TJSL"
				+ " from WL_SLXX a,wl_wzzd b where a.KFXH =:KFXH  and a.ZBLB =:ZBLB and a.SLKS =:SLKS and a.SLGH =:SLGH and a.SLZT = 0 "
				+ " and a.wzxh =b.wzxh and  (a.WZSL - nvl((select sum(wzsl) from wl_ck02 where slxh = a.jlxh), 0))>0");
		try {
			StringBuffer Sql_count = new StringBuffer(
					"SELECT COUNT(*) as NUM FROM ");
			Sql_count.append("(" + sql_list.toString() + ")");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					Sql_count.toString(), parameters);

			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_SLXX_DETAIL);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);

		} catch (PersistentDataOperationException e) {
			logger.error("引入科室加载失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "引入科室加载失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doRejectSLXX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			for (Map<String, Object> map : body) {
				parameters.put("JLXH", Long.parseLong(map.get("JLXH") + ""));
				dao.doUpdate("update WL_SLXX set SLZT=-9 where JLXH=:JLXH",
						parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> ck01 = (Map<String, Object>) body.get("WL_CK01");
		List<Map<String, Object>> ck02 = (List<Map<String, Object>>) body
				.get("WL_CK02");

		ck01.put("ZDRQ", new Date());
		ck01.put("ZDGH", user.getUserId());
		ck01.put("DJLX", 6);
		ck01.put("JGID", user.getManageUnit().getId());

		double djje = 0.0;
		for (int i = 0; i < ck02.size(); i++) {
			Map<String, Object> mat = ck02.get(i);
			if (mat.get("WZJE") != null) {
				djje += Double.parseDouble(mat.get("WZJE").toString());
			}
			if (!mat.containsKey("KCXH") || mat.get("KCXH") == ""
					|| mat.get("KCXH") == null) {
				mat.put("KCXH", 0L);
			}
		}
		ck01.put("DJJE", djje);

		try {
			if (ck01.containsKey("KSDM")) {
				Map<String, Object> parametersCKKF = new HashMap<String, Object>();
				parametersCKKF.put("KSDM",
						Long.parseLong(ck01.get("KSDM") + ""));
				List<Map<String, Object>> list_ckkf = dao.doQuery(
						"select KFXH as KFXH from WL_KFXX where EJKF=:KSDM",
						parametersCKKF);
				if (list_ckkf != null && list_ckkf.size() > 0
						&& list_ckkf.get(0).containsKey("KFXH")) {
					ck01.put("CKKF",
							Long.parseLong(list_ckkf.get(0).get("KFXH") + ""));
				}
			}
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_CK01, ck01, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < ck02.size(); i++) {
					Map<String, Object> mat = ck02.get(i);
					mat.put("DJXH", DJXH);
					mat.put("SLXH", Long.parseLong(mat.get("SLXH") + ""));
					mat.put("WZSL", Double.parseDouble(mat.get("WZSL") + ""));
					mat.put("SLSL", Double.parseDouble(mat.get("SLSL") + ""));
					mat.put("WFSL", Double.parseDouble(mat.get("WFSL") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
					Double WZSL = Double.parseDouble(mat.get("WZSL") + "");
					Double SLSL = Double.parseDouble(mat.get("SLSL") + "");
					if (WZSL.equals(SLSL)) {
						Map<String, Object> parametersSLXX = new HashMap<String, Object>();
						parametersSLXX.put("JLXH",
								Long.parseLong(mat.get("SLXH") + ""));
						dao.doUpdate(
								"update WL_SLXX set SLZT=1 where JLXH=:JLXH",
								parametersSLXX);
					}
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_CK01, ck01, false);
				dao.removeByFieldValue("DJXH",
						Long.parseLong(ck01.get("DJXH") + ""),
						BSPHISEntryNames.WL_CK02);
				for (int i = 0; i < ck02.size(); i++) {
					Map<String, Object> mat = ck02.get(i);
					mat.put("DJXH", ck01.get("DJXH"));
					mat.put("SLXH", Long.parseLong(mat.get("SLXH") + ""));
					mat.put("WZSL", Double.parseDouble(mat.get("WZSL") + ""));
					mat.put("SLSL", Double.parseDouble(mat.get("SLSL") + ""));
					mat.put("WFSL", Double.parseDouble(mat.get("WFSL") + ""));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
					Double WZSL = Double.parseDouble(mat.get("WZSL") + "");
					Double SLSL = Double.parseDouble(mat.get("SLSL") + "");
					if (WZSL.equals(SLSL)) {
						Map<String, Object> parametersSLXX = new HashMap<String, Object>();
						parametersSLXX.put("JLXH",
								Long.parseLong(mat.get("SLXH") + ""));
						dao.doUpdate(
								"update WL_SLXX set SLZT=1 where JLXH=:JLXH",
								parametersSLXX);
					}
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
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void getZblbByKfxh(Map<String, Object> body, Map<String, Object> res)
			throws ModelDataOperationException {
		Integer kfxh = (Integer) body.get("KFXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KFXH", Long.parseLong(kfxh.toString()));
		try {
			List<Map<String, Object>> list = dao.doQuery(
					"from WL_KFXX  where KFXH=:KFXH", parameters);
			String kfzb = (String) list.get(0).get("KFZB");
			parameters.clear();
			list = dao.doSqlQuery(
					"select ZBLB,ZBMC from WL_ZBLB  where ZBLB in(" + kfzb
							+ ") order by ZBLB", parameters);
			res.put("list", list);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doGetDjztByDjxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(BSPHISEntryNames.WL_CK01,
					Long.parseLong(body.get("DJXH") + ""));
			if (map.size() == 0) {
				return;
			}
			res.put("djzt", map.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doGetCjxhByWzxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", Long.parseLong(body.get("WZXH") + ""));
		try {
			Map<String, Object> map = dao.doLoad(
					"from WL_WZCJ  where WZXH=:WZXH", parameters);
			if (map == null || map.size() == 0) {
				return;
			}
			res.put("cjxh", map.get("CJXH"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doVerify(Map<String, Object> body, String op, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameterswzkc = new HashMap<String, Object>();
		Map<String, Object> parameterswzxx = new HashMap<String, Object>();
		Map<String, Object> parameterskcxh = new HashMap<String, Object>();
		Map<String, Object> parametersck02upd = new HashMap<String, Object>();
		Map<String, Object> parametersck02ins = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupd = new HashMap<String, Object>();
		Map<String, Object> parametersykslupd = new HashMap<String, Object>();
		Map<String, Object> parameterssetck02glxh = new HashMap<String, Object>();
		Map<String, Object> parametersck01upd = new HashMap<String, Object>();
		Map<String, Object> parameterszczbupd = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		UserRoleToken user = UserRoleToken.getCurrent();
		double sysl = 0.00;
		int shsign = 0;
		try {
			List<Map<String, Object>> ck02 = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ,a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ from "
									+ "WL_CK02 "
									+ " a,"
									+ "WL_CK01 "
									+ " b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH=0",
							parameters);
			List<Map<String, Object>> ck02ph = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ from "
									+ " WL_CK02 a,"
									+ "WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH<>0",
							parameters);
			for (int i = 0; i < ck02.size(); i++) {
				parameterswzkc.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(ck02.get(i).get("KFXH") + ""));
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzckwzsl = dao
						.doLoad("select sum(WZSL-YKSL) as KYSL from WL_WZKC where KFXH=:KFXH and WZXH=:WZXH",
								parameterswzkc);
				if (Double.parseDouble(ck02.get(i).get("WZSL") + "") > Double
						.parseDouble(wzckwzsl.get("KYSL") + "")) {
					Map<String, Object> wzzdwzmc = dao
							.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
									parameterswzxx);
					res.put("WZMC", wzzdwzmc.get("WZMC"));
					shsign = 1;
				}
			}
			for (int i = 0; i < ck02ph.size(); i++) {
				if (ck02ph.get(i).get("THDJ") == null) {
					parameterswzkc.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					parameterswzkc.put("KFXH",
							Integer.parseInt(ck02ph.get(i).get("KFXH") + ""));
					parameterskcxh.put("KCXH",
							Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
					parameterskcxh.put("KFXH",
							Integer.parseInt(ck02ph.get(i).get("KFXH") + ""));
					parameterswzxx.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					Map<String, Object> wzckwzsl = dao
							.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterskcxh);
					if (Double.parseDouble(ck02ph.get(i).get("WZSL") + "") > Double
							.parseDouble(wzckwzsl.get("KYSL") + "")) {
						Map<String, Object> wzzdwzmc = dao.doLoad(
								"select WZMC as WZMC from "
										+ BSPHISEntryNames.WL_WZZD
										+ " where WZXH=:WZXH", parameterswzxx);
						res.put("WZMC", wzzdwzmc.get("WZMC"));
						shsign = 1;
					}
				}
			}
			if (shsign == 0) {
				for (int i = 0; i < ck02.size(); i++) {
					parameterswzkc.put("WZXH",
							Long.parseLong(ck02.get(i).get("WZXH") + ""));
					parameterswzkc.put("KFXH",
							Integer.parseInt(ck02.get(i).get("KFXH") + ""));
					sysl = Double.parseDouble(ck02.get(i).get("WZSL") + "");
					List<Map<String, Object>> wzkc = dao
							.doQuery(
									"select JLXH as JLXH,KCXH as KCXH,WZJG as WZJG,LSJG as LSJG,CJXH as CJXH,WZPH as WZPH,str(SCRQ,'YYYY-MM-DD HH24:MI:SS') as SCRQ,str(SXRQ,'YYYY-MM-DD HH24:MI:SS') as SXRQ,MJPH as MJPH,(WZSL-YKSL) as KYSL,YKSL as YKSL from "
											+ " WL_WZKC where WZXH=:WZXH and KFXH=:KFXH and WZSL<>YKSL order by KCXH",
									parameterswzkc);

					for (int j = 0; j < wzkc.size(); j++) {
						if (sysl <= Double.parseDouble(wzkc.get(j).get("KYSL")
								+ "")) {
							if (sysl == Double.parseDouble(ck02.get(i).get(
									"WZSL")
									+ "")) {
								parametersck02upd.put("WZSL", sysl);
								parametersck02upd.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02upd.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02upd.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(ck02
														.get(i).get("WZSL")
														+ ""));
								parametersck02upd.put("LSJG", parseDouble(wzkc
										.get(j).get("LSJG")));
								parametersck02upd.put("LSJE", parseDouble(wzkc
										.get(j).get("LSJG"))
										* parseDouble(ck02.get(i).get("WZSL")));
								parametersck02upd.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02upd.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02upd.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02upd.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02upd.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02upd.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02upd.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02upd.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02upd.put("MJPH", "");
								}
								parametersck02upd.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ sysl);
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								dao.doUpdate(
										"update WL_CK02 set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
							} else {
								parametersck02ins.put("DJXH",
										Long.parseLong(body.get("DJXH") + ""));
								parametersck02ins.put(
										"WZXH",
										Long.parseLong(ck02.get(i).get("WZXH")
												+ ""));
								parametersck02ins.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								parametersck02ins.put("WZSL", sysl);
								parametersck02ins.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02ins.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* sysl);
								parametersck02ins.put("LSJG", parseDouble(wzkc
										.get(j).get("LSJG")));
								parametersck02ins.put("LSJE", parseDouble(wzkc
										.get(j).get("LSJG"))
										* parseDouble(ck02.get(i).get("WZSL")));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02ins.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02ins.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02ins.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02ins.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02ins.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02ins.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02ins.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02ins.put("MJPH", "");
								}
								parametersck02ins.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02ins.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parametersck02ins.put(
										"SLXH",
										Long.parseLong(ck02.get(i).get("SLXH")
												+ ""));
								if (ck02.get(i).get("JHBZ") != null) {
									parametersck02ins.put(
											"JHBZ",
											Integer.parseInt(ck02.get(i).get(
													"JHBZ")
													+ ""));
								} else {
									parametersck02ins.put("JHBZ", 0);
								}
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ sysl);
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								Map<String, Object> jlxhMap = dao.doSave(
										"create", BSPHISEntryNames.WL_CK02,
										parametersck02ins, false);
								Session ss = (Session) ctx
										.get(Context.DB_SESSION);
								ss.flush();
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								dao.doUpdate(
										"update WL_CK02 set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
							}
							break;
						} else {
							if (j == 0) {
								parametersck02upd.put(
										"WZSL",
										Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ ""));
								parametersck02upd.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02upd.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02upd.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parametersck02upd.put("LSJG", parseDouble(wzkc
										.get(j).get("LSJG")));
								parametersck02upd.put("LSJE", parseDouble(wzkc
										.get(j).get("LSJG"))
										* parseDouble(ck02.get(i).get("WZSL")));
								parametersck02upd.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02upd.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02upd.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02upd.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02upd.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02upd.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02upd.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02upd.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02upd.put("MJPH", "");
								}
								parametersck02upd.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								dao.doUpdate(
										"update WL_CK02 set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
								sysl = sysl
										- Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ "");
							} else {
								parametersck02ins.put("DJXH",
										Long.parseLong(body.get("DJXH") + ""));
								parametersck02ins.put(
										"WZXH",
										Long.parseLong(ck02.get(i).get("WZXH")
												+ ""));
								parametersck02ins.put(
										"CJXH",
										Long.parseLong(wzkc.get(j).get("CJXH")
												+ ""));
								parametersck02ins.put(
										"WZSL",
										Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ ""));
								parametersck02ins.put(
										"WZJG",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ ""));
								parametersck02ins.put(
										"WZJE",
										Double.parseDouble(wzkc.get(j).get(
												"WZJG")
												+ "")
												* Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parametersck02ins.put("LSJG", parseDouble(wzkc
										.get(j).get("LSJG")));
								parametersck02ins.put("LSJE", parseDouble(wzkc
										.get(j).get("LSJG"))
										* parseDouble(ck02.get(i).get("WZSL")));
								if (wzkc.get(j).get("WZPH") != null) {
									parametersck02ins.put("WZPH", wzkc.get(j)
											.get("WZPH") + "");
								} else {
									parametersck02ins.put("WZPH", "");
								}
								if (wzkc.get(j).get("SCRQ") != null) {
									parametersck02ins.put(
											"SCRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SCRQ")
													+ ""));
								} else {
									parametersck02ins.put("SCRQ", null);
								}
								if (wzkc.get(j).get("SXRQ") != null) {
									parametersck02ins.put(
											"SXRQ",
											sdfTime.parse(wzkc.get(j).get(
													"SXRQ")
													+ ""));
								} else {
									parametersck02ins.put("SXRQ", null);
								}
								if (wzkc.get(j).get("MJPH") != null) {
									parametersck02ins.put("MJPH", wzkc.get(j)
											.get("MJPH") + "");
								} else {
									parametersck02ins.put("MJPH", "");
								}
								parametersck02ins.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02ins.put(
										"JLXH",
										Long.parseLong(ck02.get(i).get("JLXH")
												+ ""));
								parametersck02ins.put(
										"SLXH",
										Long.parseLong(ck02.get(i).get("SLXH")
												+ ""));
								if (ck02.get(i).get("JHBZ") != null) {
									parametersck02ins.put(
											"JHBZ",
											Integer.parseInt(ck02.get(i).get(
													"JHBZ")
													+ ""));
								} else {
									parametersck02ins.put("JHBZ", 0);
								}
								parameterswzkcupd.put(
										"YKSL",
										Double.parseDouble(wzkc.get(j).get(
												"YKSL")
												+ "")
												+ Double.parseDouble(wzkc
														.get(j).get("KYSL")
														+ ""));
								parameterswzkcupd.put(
										"JLXH",
										Long.parseLong(wzkc.get(j).get("JLXH")
												+ ""));
								Map<String, Object> jlxhMap = dao.doSave(
										"create", BSPHISEntryNames.WL_CK02,
										parametersck02ins, false);
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								Session ss = (Session) ctx
										.get(Context.DB_SESSION);
								ss.flush();
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								dao.doUpdate(
										"update WL_CK02 set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate(
										"update WL_WZKC set YKSL=:YKSL where JLXH=:JLXH",
										parameterswzkcupd);
								sysl = sysl
										- Double.parseDouble(wzkc.get(j).get(
												"KYSL")
												+ "");
							}
						}
					}
				}
			}
			if (shsign == 0) {
				for (int i = 0; i < ck02ph.size(); i++) {
					int glfs = 0;
					parameterswzxx.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					Map<String, Object> wzzdglfs = dao
							.doLoad("select GLFS as GLFS from WL_WZZD where WZXH=:WZXH",
									parameterswzxx);
					if (wzzdglfs.get("GLFS") != null) {
						glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
					}
					if (glfs == 3) {
						if (ck02ph.get(i).get("ZBXH") != null) {
							parameterszczbupd.put(
									"ZBXH",
									Long.parseLong(ck02ph.get(i).get("ZBXH")
											+ ""));
							dao.doUpdate(
									"update WL_ZCZB set CLBZ=1 where ZBXH=:ZBXH",
									parameterszczbupd);
						}
					} else {
						parameterskcxh.put("KCXH",
								Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
						parameterskcxh.put("KFXH", Integer.parseInt(ck02ph.get(
								i).get("KFXH")
								+ ""));
						Map<String, Object> wzckwzsl = dao
								.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
										parameterskcxh);
						if (ck02ph.get(i).get("THDJ") == null) {
							parametersykslupd.put(
									"KCXH",
									Long.parseLong(ck02ph.get(i).get("KCXH")
											+ ""));
							parametersykslupd.put(
									"KFXH",
									Integer.parseInt(ck02ph.get(i).get("KFXH")
											+ ""));
							parametersykslupd.put(
									"YKSL",
									Double.parseDouble(wzckwzsl.get("YKSL")
											+ "")
											+ Double.parseDouble(ck02ph.get(i)
													.get("WZSL") + ""));
							dao.doUpdate(
									"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
									parametersykslupd);
						} else {
							if (glfs == 2) {
								parametersykslupd.put(
										"KCXH",
										Long.parseLong(ck02ph.get(i)
												.get("KCXH") + ""));
								parametersykslupd.put(
										"KFXH",
										Integer.parseInt(ck02ph.get(i).get(
												"KFXH")
												+ ""));
								parametersykslupd.put(
										"YKSL",
										Double.parseDouble(wzckwzsl.get("YKSL")
												+ "")
												+ Double.parseDouble(ck02ph
														.get(i).get("WZSL")
														+ ""));
								dao.doUpdate(
										"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
										parametersykslupd);
							}
						}
					}
				}
			}
			if (shsign == 0) {
				if (ck02.size() > 0 || ck02ph.size() > 0) {

					Map<String, Object> parametersDJXH = new HashMap<String, Object>();
					parametersDJXH.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					List<Map<String, Object>> ck02_done = dao
							.doQuery(
									"select WZJE as WZJE  from WL_CK02 where DJXH=:DJXH",
									parametersDJXH);
					double djje = 0.0;
					for (int i = 0; i < ck02_done.size(); i++) {
						Map<String, Object> mat = ck02_done.get(i);
						if (mat.containsKey("WZJE")) {
							djje += Double.parseDouble(mat.get("WZJE")
									.toString());
						}
					}
					parametersck01upd.put("DJJE", djje);

					String uid = user.getUserId();
					parametersck01upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck01upd.put("SHGH", uid);
					parametersck01upd.put("SHRQ", new Date());
					parametersck01upd.put("LZDH", getLzdh());
					dao.doUpdate(
							"update WL_CK01 set LZDH=:LZDH,DJZT=1,SHRQ=:SHRQ,SHGH=:SHGH,DJJE=:DJJE where DJXH=:DJXH",
							parametersck01upd);
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		} catch (ParseException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doIsEnoughInventory(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> ck02 = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select WZSL as WZSL from WL_WZKC").append(
				" where KCXH=:KCXH");
		try {
			for (int i = 0; i < ck02.size(); i++) {
				Map<String, Object> mat = ck02.get(i);
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
					ServiceCode.CODE_DATABASE_ERROR, "入库记录保存失败");
		}
	}

	public void doCancelVerify(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcupd = new HashMap<String, Object>();
		Map<String, Object> parameterswzkcyksl = new HashMap<String, Object>();
		Map<String, Object> parametersck02wzsl = new HashMap<String, Object>();
		Map<String, Object> parametersck02upd = new HashMap<String, Object>();
		Map<String, Object> parametersck02nowzslupd = new HashMap<String, Object>();
		Map<String, Object> parameterswzxx = new HashMap<String, Object>();
		Map<String, Object> parameterszczbupd = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			long kfxh = Integer
					.parseInt(user.getProperty("treasuryId") == null ? "0"
							: user.getProperty("treasuryId") + "");
			String jgid = user.getManageUnit().getId();
			String ckwzjslx = ParameterUtil.getParameter(jgid, "CKWZJSLX"
					+ kfxh, "0", "物资出库物资检索类型 0.按库存检索1.按批次检索", ctx);
			List<Map<String, Object>> ck02 = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,a.GLXH as GLXH,b.KFXH as KFXH,b.THDJ as THDJ from "
									+ " WL_CK02 a,"
									+ " WL_CK01 b where a.DJXH=:DJXH and a.DJXH=b.DJXH",
							parameters);
			Boolean isGLFS3 = false;
			for (int i = 0; i < ck02.size(); i++) {
				int glfs = 0;
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzzdglfs = dao.doLoad(
						"select GLFS as GLFS from WL_WZZD where WZXH=:WZXH",
						parameterswzxx);
				if (wzzdglfs.get("GLFS") != null) {
					glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
				}

				if (glfs == 3) {
					if (ck02.get(i).get("ZBXH") != null) {
						parameterszczbupd.put("ZBXH",
								Long.parseLong(ck02.get(i).get("ZBXH") + ""));
						dao.doUpdate(
								"update WL_ZCZB set CLBZ=0 where ZBXH=:ZBXH",
								parameterszczbupd);
					}
					isGLFS3 = true;
				} else {
					parameterswzkcyksl.put("KCXH",
							Long.parseLong(ck02.get(i).get("KCXH") + ""));
					parameterswzkcyksl.put("KFXH",
							Integer.parseInt(ck02.get(i).get("KFXH") + ""));
					Map<String, Object> wzkcyksl = dao
							.doLoad("select YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterswzkcyksl);
					if ("0".equals(ck02.get(i).get("THDJ") + "")) {
						parameterswzkcupd.put("KCXH",
								Long.parseLong(ck02.get(i).get("KCXH") + ""));
						parameterswzkcupd.put("KFXH",
								Integer.parseInt(ck02.get(i).get("KFXH") + ""));
						parameterswzkcupd.put(
								"YKSL",
								Double.parseDouble(wzkcyksl.get("YKSL") + "")
										- Double.parseDouble(ck02.get(i).get(
												"WZSL")
												+ ""));
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
								parameterswzkcupd);
					} else {
						if (glfs == 2) {
							parameterswzkcupd.put("KCXH", Long.parseLong(ck02
									.get(i).get("KCXH") + ""));
							parameterswzkcupd.put(
									"KFXH",
									Integer.parseInt(ck02.get(i).get("KFXH")
											+ ""));
							parameterswzkcupd.put(
									"YKSL",
									Double.parseDouble(wzkcyksl.get("YKSL")
											+ "")
											- Double.parseDouble(ck02.get(i)
													.get("WZSL") + ""));
							dao.doUpdate(
									"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
									parameterswzkcupd);
						}
					}
				}
			}
			if ("0".equals(ckwzjslx) && !isGLFS3) {
				List<Map<String, Object>> ck02wzsl = dao
						.doQuery(
								"select sum(WZSL) as WZSL,WZXH as WZXH from WL_CK02 where DJXH=:DJXH and GLXH is not null group by WZXH",
								parameters);
				for (int i = 0; i < ck02wzsl.size(); i++) {
					parametersck02wzsl.put("WZXH",
							Long.parseLong(ck02wzsl.get(i).get("WZXH") + ""));
					parametersck02wzsl.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					Map<String, Object> wzslMap = dao
							.doLoad("select WZSL as WZSL from WL_CK02 where DJXH=:DJXH and WZXH=:WZXH and GLXH is null",
									parametersck02wzsl);
					parametersck02upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck02nowzslupd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck02upd.put("WZXH",
							Long.parseLong(ck02wzsl.get(i).get("WZXH") + ""));
					parametersck02upd
							.put("WZSL",
									Double.parseDouble(ck02wzsl.get(i).get(
											"WZSL")
											+ "")
											+ Double.parseDouble(wzslMap
													.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_CK02 set WZSL=:WZSL,KCXH=0,CJXH='',WZJG=0,WZJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and WZXH=:WZXH and GLXH is null",
							parametersck02upd);
					dao.doUpdate(
							"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and KCXH is not null",
							parametersck02nowzslupd);
					dao.doUpdate(
							"delete from WL_CK02 where DJXH=:DJXH and WZXH=:WZXH and GLXH is not null",
							parametersck02wzsl);
				}
				if (ck02wzsl.size() <= 0) {
					parametersck02nowzslupd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					dao.doUpdate(
							"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null",
							parametersck02nowzslupd);
				}
			}
			dao.doUpdate(
					"update WL_CK01 set SHRQ='',SHGH='',DJZT=0 where DJXH=:DJXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doUpdateWzcj(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		List<Map<String, Object>> ck02 = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update WL_WZCJ set WZJG=:WZJG where WZXH=:WZXH");
		try {
			for (int i = 0; i < ck02.size(); i++) {
				Map<String, Object> mat = ck02.get(i);
				Integer wzjg = (Integer) mat.get("WZJG");
				parameters.put("WZJG", wzjg.doubleValue());
				Integer wzxh = (Integer) mat.get("WZXH");
				parameters.put("WZXH", wzxh.longValue());
				dao.doUpdate(hql.toString(), parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCommit(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ck02 = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> ck01 = (Map<String, Object>) body.get("WL_CK01");
		for (int i = 0; i < ck02.size(); i++) {
			Map<String, Object> mat = ck02.get(i);
			mat.put("KFXH", ck01.get("KFXH"));
			mat.put("ZBLB", ck01.get("ZBLB"));
			mat.put("YWRQ", ck01.get("CKRQ"));
			mat.put("ZRKS", ck01.get("KSDM"));
			mat.put("KSDM", ck01.get("KSDM"));
			mat.put("DJXH", ck01.get("DJXH"));
			if (ck01.get("PDDJ") != null && ck01.get("PDDJ") != ""
					&& Long.parseLong(ck01.get("PDDJ") + "") > 0) {
				mat.put("YWFS", 1);
			} else {
				mat.put("YWFS", 0);
			}
		}

		if (BSPHISUtil.Uf_access(ck02, Long.parseLong((ck01.get("LZFS") + "")),
				dao, ctx)) {
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(ck01.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			try {
				dao.doSave("update", BSPHISEntryNames.WL_CK01, parameters,
						false);

				Boolean hasWF = false;
				Map<String, Object> map_ck01 = new HashMap<String, Object>();
				for (int i = 0; i < ck02.size(); i++) {
					Map<String, Object> mat = ck02.get(i);
					if(mat.get("WFSL") != null){
						if (Double.parseDouble(mat.get("WFSL") + "") > 0) {
							ck01.put("DJJE", 0);
							ck01.put("DJZT", 0);
							ck01.put("SHGH", null);
							ck01.put("SHRQ", null);
							ck01.put("DJBZ", "由单据[" + ck01.get("LZDH") + "]未发数量生成");
							ck01.put("LZDH", null);
							ck01.remove("DJXH");
							map_ck01 = dao.doSave("create",
									BSPHISEntryNames.WL_CK01, ck01, false);
							hasWF = true;
							break;
						}
					}
				}
				if (hasWF) {
					for (int i = 0; i < ck02.size(); i++) {
						Map<String, Object> mat = ck02.get(i);
						if (Double.parseDouble(mat.get("WFSL") + "") > 0) {
							mat.put("DJXH",
									Long.parseLong(map_ck01.get("DJXH") + ""));
							mat.put("SLXH",
									Long.parseLong(mat.get("SLXH") + ""));
							mat.put("WZSL",
									Double.parseDouble(mat.get("WFSL") + ""));
							mat.put("SLSL",
									Double.parseDouble(mat.get("SLSL") + ""));
							mat.put("WFSL", 0D);
							mat.put("KCXH", 0L);
							mat.put("WZJG", 0D);
							mat.put("WZJE", 0D);
							mat.remove("JLXH");
							dao.doSave("create", BSPHISEntryNames.WL_CK02, mat,
									false);
						}
					}
				}

				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (ValidateException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "记录保存失败");
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "记录保存失败");
			}

		}

	}

	public void doGetKtslByKcxh(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long kcxh = Long.parseLong(body.get("KCXH") + "");
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("KCXH", kcxh);
		parameters.put("KFXH",
				Integer.parseInt(user.getProperty("treasuryId").toString()));
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

	public void doGetCK02Info(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		Map<String, Object> parameterskcxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = Integer.parseInt(user.getProperty("treasuryId").toString());// �û��Ļ�ID
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
		StringBuffer sql = new StringBuffer(
				"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,c.CJMC as CJMC,"
						+ "b.GLFS as GLFS,b.WZDW as WZDW,a.CJXH as CJXH,"
						+ "a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,"
						+ "a.WZPH as WZPH,a.MJPH as MJPH,to_char(a.SCRQ,'YYYY-MM-DD HH24:MI:SS') as SCRQ,"
						+ "to_char(a.SXRQ,'YYYY-MM-DD HH24:MI:SS') as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,"
						+ "a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ from WL_CK02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH=b.WZXH");
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = "and a." + que[2].substring(3, que[2].indexOf("]"))
					+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();

			sql.append(" " + qur);
		}
		sql.append(" ORDER BY a.JLXH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get("KCXH") != null
						&& (Long.parseLong(list.get(i).get("KCXH") + "") != 0)) {
					parameterskcxh.put("KCXH",
							Long.parseLong(list.get(i).get("KCXH") + ""));
					parameterskcxh.put("KFXH", kfxh);
					Map<String, Object> ykslMap = dao
							.doLoad("select sum(WZSL-YKSL) as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterskcxh);
					if (ykslMap.get("YKSL") != null) {
						list.get(i).put("TJSL",
								Double.parseDouble(ykslMap.get("YKSL") + ""));
					}
				} else {
					parameterswzxh.put("WZXH",
							Long.parseLong(list.get(i).get("WZXH") + ""));
					parameterswzxh.put("KFXH", kfxh);
					Map<String, Object> ykslMap = dao
							.doLoad("select sum(WZSL-YKSL) as YKSL from WL_WZKC where WZXH=:WZXH and KFXH=:KFXH",
									parameterswzxh);
					if (ykslMap.get("YKSL") != null) {
						list.get(i).put("TJSL",
								Double.parseDouble(ykslMap.get("YKSL") + ""));
					}
				}
			}
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.sup.schemas.WL_CK02_SLGL");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public String getLzdh() {
		String lzdh = "SL";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}
}
