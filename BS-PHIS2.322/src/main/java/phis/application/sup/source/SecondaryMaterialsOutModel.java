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

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 二级库房出库库管理Model
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class SecondaryMaterialsOutModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(SecondaryMaterialsOutModel.class);

	public SecondaryMaterialsOutModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void saveCheckIn(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_CK01");
		// 根据流转方式的特殊标识(tsbz)是否为1，如果为1则为特殊标识（如通过盘点产生的盘亏出库）则出库科室和经办人为空；不为1则必须填写，未填写则必须提示用户，并且返回
		List<Map<String, Object>> _lzfs = BSPHISUtil.uf_get_lzfs(
				Long.parseLong(rk.get("LZFS") + ""), dao, ctx);
		if (Long.parseLong(_lzfs.get(0).get("TSBZ") + "") != 1
				&& parseInt(rk.get("DJLX")) != 2) {
			if (rk.get("KSDM") == null || rk.get("KSDM") == ""
					|| rk.get("JBGH") == null || rk.get("JBGH") == "") {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库科室和经办人不能为空");
			}
		}
		rk.put("KFXH", user.getProperty("treasuryId"));
		rk.put("ZDRQ", new Date());
		rk.put("ZDGH", user.getUserId());
		rk.put("JGID", user.getManageUnit().getId());

		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		double djje = 0.0;
		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			if (mat.get("WZJE") == null || mat.get("WZJE") == "") {
				continue;
			}
			djje += Double.parseDouble(mat.get("WZJE").toString());
		}
		rk.put("DJJE", djje);
		try {
			if ("create".equals(op)) {
				Map<String, Object> _map = dao.doSave(op,
						BSPHISEntryNames.WL_CK01, rk, false);
				Long DJXH = (Long) _map.get("DJXH");
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", DJXH);
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
				}
			} else if ("update".equals(op)) {
				dao.doSave("update", BSPHISEntryNames.WL_CK01, rk, false);
				Long DJXH = Long.parseLong(rk.get("DJXH") + "");
				dao.removeByFieldValue("DJXH", DJXH, BSPHISEntryNames.WL_CK02);
				for (int i = 0; i < mats.size(); i++) {
					Map<String, Object> mat = mats.get(i);
					mat.put("DJXH", rk.get("DJXH"));
					dao.doSave("create", BSPHISEntryNames.WL_CK02, mat, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
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
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
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
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doGetCjxhByWzxh(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Integer wzxh = (Integer) body.get("WZXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", Long.parseLong(wzxh.toString()));
		try {
			Map<String, Object> map = dao.doLoad("from " + "WL_WZCJ"
					+ " where WZXH=:WZXH", parameters);
			if (map.size() == 0) {
				return;
			}
			res.put("cjxh", map.get("CJXH"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
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
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ from "
									+ "WL_CK02"
									+ " a,"
									+ "WL_CK01"
									+ " b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH=0 and (a.YKBZ<>1 or a.YKBZ=null)",
							parameters);
			List<Map<String, Object>> ck02ph = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ,a.YKBZ as YKBZ from "
									+ "WL_CK02"
									+ " a,"
									+ "WL_CK01"
									+ " b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH<>0 and (a.YKBZ<>1 or a.YKBZ=null)",
							parameters);
			for (int i = 0; i < ck02.size(); i++) {
				parameterswzkc.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				parameterswzkc.put("KFXH",
						Integer.parseInt(ck02.get(i).get("KFXH") + ""));
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzckwzsl = dao.doLoad(
						"select sum(WZSL-YKSL) as KYSL from " + "WL_WZKC"
								+ " where KFXH=:KFXH and WZXH=:WZXH",
						parameterswzkc);
				if (wzckwzsl.get("KYSL") != null) {
					if (Double.parseDouble(ck02.get(i).get("WZSL") + "") > Double
							.parseDouble(wzckwzsl.get("KYSL") + "")) {
						Map<String, Object> wzzdwzmc = dao.doLoad(
								"select WZMC as WZMC from " + "WL_WZZD"
										+ " where WZXH=:WZXH", parameterswzxx);
						res.put("WZMC", wzzdwzmc.get("WZMC"));
						shsign = 1;
					}
				} else {
					Map<String, Object> wzzdwzmc = dao.doLoad(
							"select WZMC as WZMC from " + "WL_WZZD"
									+ " where WZXH=:WZXH", parameterswzxx);
					res.put("WZMC", wzzdwzmc.get("WZMC"));
					shsign = 1;
				}
			}
			for (int i = 0; i < ck02ph.size(); i++) {
				if (ck02ph.get(i).get("THDJ") == null
						&& (ck02ph.get(i).get("YKBZ") == null || !ck02ph.get(i)
								.get("YKBZ").equals("1"))) {
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
					Map<String, Object> wzckwzsl = dao.doLoad(
							"select (WZSL-YKSL) as KYSL,YKSL as YKSL from "
									+ "WL_WZKC"
									+ " where KCXH=:KCXH and KFXH=:KFXH",
							parameterskcxh);
					if (wzckwzsl.get("KYSL") != null) {
						if (Double.parseDouble(ck02ph.get(i).get("WZSL") + "") > Double
								.parseDouble(wzckwzsl.get("KYSL") + "")) {
							Map<String, Object> wzzdwzmc = dao.doLoad(
									"select WZMC as WZMC from " + "WL_WZZD"
											+ " where WZXH=:WZXH",
									parameterswzxx);
							res.put("WZMC", wzzdwzmc.get("WZMC"));
							shsign = 1;
						}
					} else {
						Map<String, Object> wzzdwzmc = dao.doLoad(
								"select WZMC as WZMC from " + "WL_WZZD"
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
											+ "WL_WZKC"
											+ " where WZXH=:WZXH and KFXH=:KFXH and WZSL<>YKSL order by KCXH",
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
								// 增加lsje，lsjg
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02upd.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02upd.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(ck02
															.get(i).get("WZSL")
															+ ""));
								}
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
										"update "
												+ "WL_CK02"
												+ " set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate("update " + "WL_WZKC"
										+ " set YKSL=:YKSL where JLXH=:JLXH",
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
								// 增加lsje，lsjg
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02ins.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02ins.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* sysl);
								}

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
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								Session ss = (Session) ctx
										.get(Context.DB_SESSION);
								ss.flush();
								dao.doUpdate("update " + "WL_CK02"
										+ " set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate("update " + "WL_WZKC"
										+ " set YKSL=:YKSL where JLXH=:JLXH",
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
								// 增加lsje，lsjg
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02upd.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02upd.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(wzkc
															.get(j).get("KYSL")
															+ ""));
								}

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
										"update "
												+ "WL_CK02"
												+ " set WZSL=:WZSL,KCXH=:KCXH,WZJG=:WZJG,WZJE=:WZJE,LSJG=:LSJG,LSJE=:LSJE,CJXH=:CJXH,WZPH=:WZPH,SCRQ=:SCRQ,SXRQ=:SXRQ,MJPH=:MJPH where JLXH=:JLXH",
										parametersck02upd);
								dao.doUpdate("update " + "WL_WZKC"
										+ " set YKSL=:YKSL where JLXH=:JLXH",
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
								// 增加lsje，lsjg
								if (wzkc.get(j).get("LSJG") != null) {
									parametersck02ins.put(
											"LSJG",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ ""));
									parametersck02ins.put(
											"LSJE",
											Double.parseDouble(wzkc.get(j).get(
													"LSJG")
													+ "")
													* Double.parseDouble(wzkc
															.get(j).get("KYSL")
															+ ""));
								}

								parametersck02ins.put("WZPH",
										wzkc.get(j).get("WZPH") + "");
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
								parametersck02ins.put("MJPH",
										wzkc.get(j).get("MJPH") + "");
								parametersck02ins.put(
										"KCXH",
										Long.parseLong(wzkc.get(j).get("KCXH")
												+ ""));
								parametersck02ins.put(
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
								Map<String, Object> jlxhMap = dao.doSave(
										"create", BSPHISEntryNames.WL_CK02,
										parametersck02ins, false);
								Long jlxh = Long.parseLong(jlxhMap.get("JLXH")
										+ "");
								parameterssetck02glxh.put("GLXH", jlxh);
								parameterssetck02glxh.put("JLXH", jlxh);
								dao.doUpdate("update " + "WL_CK02"
										+ " set GLXH=:GLXH where JLXH=:JLXH",
										parameterssetck02glxh);
								dao.doUpdate("update " + "WL_WZKC"
										+ " set YKSL=:YKSL where JLXH=:JLXH",
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
					if (ck02ph.get(i).get("YKBZ") != null
							&& ck02ph.get(i).get("YKBZ").equals("1")) {
						continue;
					}
					int glfs = 0;
					parameterswzxx.put("WZXH",
							Long.parseLong(ck02ph.get(i).get("WZXH") + ""));
					Map<String, Object> wzzdglfs = dao.doLoad(
							"select GLFS as GLFS from " + "WL_WZZD"
									+ " where WZXH=:WZXH", parameterswzxx);
					if (wzzdglfs.get("GLFS") != null) {
						glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
					}
					if (glfs == 3) {
						if (ck02ph.get(i).get("ZBXH") != null) {
							parameterszczbupd.put(
									"ZBXH",
									Long.parseLong(ck02ph.get(i).get("ZBXH")
											+ ""));
							dao.doUpdate("update " + "WL_ZCZB"
									+ " set CLBZ=1 where ZBXH=:ZBXH",
									parameterszczbupd);
						}
					} else {
						parameterskcxh.put("KCXH",
								Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
						parameterskcxh.put("KFXH", Integer.parseInt(ck02ph.get(
								i).get("KFXH")
								+ ""));
						Map<String, Object> wzckwzsl = dao.doLoad(
								"select (WZSL-YKSL) as KYSL,YKSL as YKSL from "
										+ "WL_WZKC"
										+ " where KCXH=:KCXH and KFXH=:KFXH",
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
									"update "
											+ "WL_WZKC"
											+ " set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
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
										"update "
												+ "WL_KSZC"
												+ " set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
										parametersykslupd);
							}
						}
					}
				}
			}
			if (shsign == 0) {
				if (ck02.size() > 0 || ck02ph.size() >= 0) {
					Map<String, Object> parametersWZJE = new HashMap<String, Object>();
					parametersWZJE.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					List<Map<String, Object>> list_wzje = dao
							.doQuery(
									"select WZJE as WZJE from WL_CK02 where DJXH=:DJXH",
									parametersWZJE);
					double djje = 0.0;
					for (int i = 0; i < list_wzje.size(); i++) {
						Map<String, Object> map_wzje = list_wzje.get(i);
						if (map_wzje.get("WZJE") == null
								|| map_wzje.get("WZJE") == "") {
							continue;
						}
						djje += Double.parseDouble(map_wzje.get("WZJE")
								.toString());
					}
					String uid = user.getUserId();
					parametersck01upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck01upd.put("SHGH", uid);
					parametersck01upd.put("SHRQ", new Date());
					parametersck01upd.put("LZDH", getLzdh());
					parametersck01upd.put("DJJE", djje);
					dao.doUpdate(
							"update "
									+ "WL_CK01"
									+ " set LZDH=:LZDH,DJZT=1,SHRQ=:SHRQ,SHGH=:SHGH,DJJE=:DJJE where DJXH=:DJXH",
							parametersck01upd);
				}
			}
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
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append("WL_WZKC").append(" where KCXH=:KCXH");
		try {
			for (int i = 0; i < mats.size(); i++) {
				Map<String, Object> mat = mats.get(i);
				parameters.put("KCXH", ((Integer) mat.get("KCXH")).longValue());
				Map<String, Object> map = dao
						.doLoad(hql.toString(), parameters);
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
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
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
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			Integer kfxh = Integer
					.parseInt(user.getProperty("treasuryId") == null ? "0"
							: user.getProperty("treasuryId") + "");
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			String ckwzjslx = ParameterUtil.getParameter(jgid, "CKWZJSLX"
					+ kfxh, "0", "物资出库物资检索类型 0.按库存检索1.按批次检索", ctx);
			List<Map<String, Object>> ck02 = dao
					.doQuery(
							"select JLXH as JLXH,WZXH as WZXH,KCXH as KCXH,WZSL as WZSL,GLXH as GLXH,YKBZ as YKBZ from "
									+ "WL_CK02"
									+ " where DJXH=:DJXH and (YKBZ<>1 or YKBZ=null)",
							parameters);
			// 不管是按批次还是库存,都要更新WL_WZKC表里的YKSL
			for (int i = 0; i < ck02.size(); i++) {
				if (ck02.get(i).get("YKBZ") != null
						&& ck02.get(i).get("YKBZ").equals("1")) {
					continue;
				}
				parameterswzkcyksl.put("KCXH",
						Long.parseLong(ck02.get(i).get("KCXH") + ""));
				parameterswzkcyksl.put("KFXH", kfxh);
				Map<String, Object> wzkcyksl = dao.doLoad(
						"select YKSL as YKSL from " + "WL_WZKC"
								+ " where KCXH=:KCXH and KFXH=:KFXH",
						parameterswzkcyksl);

				parameterswzkcupd.put("KCXH",
						Long.parseLong(ck02.get(i).get("KCXH") + ""));
				parameterswzkcupd.put("KFXH", kfxh);
				parameterswzkcupd.put(
						"YKSL",
						Double.parseDouble(wzkcyksl.get("YKSL") + "")
								- Double.parseDouble(ck02.get(i).get("WZSL")
										+ ""));
				dao.doUpdate("update " + "WL_WZKC"
						+ " set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
						parameterswzkcupd);
			}
			if ("0".equals(ckwzjslx)) {
				List<Map<String, Object>> ck02wzsl = dao
						.doQuery(
								"select sum(WZSL) as WZSL,WZXH as WZXH from "
										+ "WL_CK02"
										+ " where DJXH=:DJXH and GLXH is not null  and (YKBZ<>1 or YKBZ=null) group by WZXH",
								parameters);
				for (int i = 0; i < ck02wzsl.size(); i++) {
					parametersck02wzsl.put("WZXH",
							Long.parseLong(ck02wzsl.get(i).get("WZXH") + ""));
					parametersck02wzsl.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					Map<String, Object> wzslMap = dao
							.doLoad("select WZSL as WZSL from "
									+ "WL_CK02"
									+ " where DJXH=:DJXH and WZXH=:WZXH  and (YKBZ<>1 or YKBZ=null) and GLXH is null",
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
							"update "
									+ "WL_CK02"
									+ " set WZSL=:WZSL,KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and WZXH=:WZXH and GLXH is null and (YKBZ<>1 or YKBZ=null)",
							parametersck02upd);
					dao.doUpdate(
							"update "
									+ "WL_CK02"
									+ " set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and KCXH is not null and (YKBZ<>1 or YKBZ=null)",
							parametersck02nowzslupd);
					dao.doUpdate(
							"delete from "
									+ "WL_CK02"
									+ " where DJXH=:DJXH and WZXH=:WZXH and GLXH is not null and (YKBZ<>1 or YKBZ=null)",
							parametersck02wzsl);
				}
				if (ck02wzsl.size() <= 0) {
					parametersck02nowzslupd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					dao.doUpdate(
							"update "
									+ "WL_CK02"
									+ " set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null  and (YKBZ<>1 or YKBZ=null)",
							parametersck02nowzslupd);
				}
			}
			dao.doUpdate("update " + "WL_CK01"
					+ " set SHRQ='',SHGH='',DJZT=0 where DJXH=:DJXH",
					parameters);
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
				.get("WL_CK02");
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append("WL_WZCJ")
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
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doCommit(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> mats = (List<Map<String, Object>>) body
				.get("WL_CK02");
		Map<String, Object> rk = (Map<String, Object>) body.get("WL_CK01");

		for (int i = 0; i < mats.size(); i++) {
			Map<String, Object> mat = mats.get(i);
			mat.put("KFXH", rk.get("KFXH"));
			mat.put("ZBLB", rk.get("ZBLB"));
			mat.put("YWRQ", rk.get("CKRQ"));
			mat.put("KSDM", rk.get("KSDM"));
			mat.put("ZRKS", rk.get("KSDM"));
			mat.put("GLXH", rk.get("THDJ"));
			if (rk.get("PDDJ") != null && rk.get("PDDJ") != ""
					&& Long.parseLong(rk.get("PDDJ") + "") > 0) {
				mat.put("YWFS", 1);
			} else {
				mat.put("YWFS", 0);
			}
		}

		if (BSPHISUtil.Uf_access(mats, Long.parseLong((rk.get("LZFS") + "")),
				dao, ctx)) {
			UserRoleToken user = UserRoleToken.getCurrent();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", Long.parseLong(rk.get("DJXH").toString()));
			parameters.put("DJZT", 2);
			parameters.put("JZGH", user.getUserId());
			parameters.put("JZRQ", new Date());
			try {
				dao.doSave("update", BSPHISEntryNames.WL_CK01, parameters,
						false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			} catch (ValidateException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records save fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
			}

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
			dao.doRemove(DJXH, BSPHISEntryNames.WL_CK01);
			dao.doRemove("DJXH", DJXH, BSPHISEntryNames.WL_CK02);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("删除失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败");
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
		int kfxh = 0;
		if (user.getProperty("treasuryId") != null
				&& user.getProperty("treasuryId") != "") {
			kfxh = Integer.parseInt(user.getProperty("treasuryId") + "");// 用户的机构ID
		}

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
				"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,c.CJMC as CJMC,b.GLFS as GLFS,b.WZDW as WZDW,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,to_char(a.SCRQ,'YYYY-MM-DD HH24:MI:SS') as SCRQ,to_char(a.SXRQ,'YYYY-MM-DD HH24:MI:SS') as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ from WL_CK02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH=b.WZXH");
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
						list.get(i).put("TJCKSL",
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
						list.get(i).put("TJCKSL",
								Double.parseDouble(ykslMap.get("YKSL") + ""));
					}
				}
			}
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_CK02);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetCK01Info(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = 0;
		if (user.getProperty("treasuryId") != null
				&& !"".equals(user.getProperty("treasuryId"))) {
			String treasuryId = user.getProperty("treasuryId") + "";
			KFXH = Integer.parseInt(treasuryId);
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			parameters.put("KFXH", KFXH);
			// 返回list的查询语句
			StringBuffer sql_list = new StringBuffer(
					"select * from (select b.DJXH as DJXH,b.LZFS as LZFS,b.LZDH as LZDH,b.CKRQ as CKRQ,b.CKFS as CKFS,"
							+ " b.KSDM as KSDM,b.SLGH as SLGH,b.DJZT as DJZT,b.DJJE as DJJE,b.JBGH as JBGH,"
							+ " b.DJBZ as DJBZ,b.SHRQ as SHRQ,b.JZRQ as JZRQ,b.DJLX as DJLX,"
							+ " b.ZDRQ as ZDRQ,b.JGID as JGID,b.KFXH as KFXH,b.ZBLB as ZBLB,b.CKKF as CKKF,"
							+ " b.JGFS as JGFS,b.ZDGH as ZDGH,b.SHGH as SHGH,b.JZGH as JZGH,b.THDJ as THDJ,"
							+ " b.PDDJ as PDDJ,b.QRBZ as QRBZ,b.QRRK as QRRK,b.QRRQ as QRRQ,b.QRGH as QRGH,b.RKDJ as RKDJ,"
							+ " (select max(g.BRXM) as BRXM from WL_XHMX g where g.djxh = b.djxh) as BRXM,"
							+ " (select max(g.BRLY) as BRLY from WL_XHMX g where g.djxh = b.djxh) as BRLY"
							+ " from  WL_CK01 b where b.DJLX<4 and b.KFXH =:KFXH)");
			int djzt = -1;
			if (req.containsKey("DJZT")) {
				djzt = parseInt(req.get("DJZT"));
				sql_list.append(" where DJZT =:DJZT ");
				parameters.put("DJZT", djzt);
			}
			if (req.containsKey("SHRQQ") && djzt == 1) {
				Date SHRQQ = sdf.parse(req.get("SHRQQ") + "");
				sql_list.append(" and SHRQ >=:SHRQQ ");
				parameters.put("SHRQQ", SHRQQ);
			}
			if (req.containsKey("SHRQZ") && djzt == 1) {
				Date SHRQZ = sdf.parse(req.get("SHRQZ") + "");
				sql_list.append(" and SHRQ <=:SHRQZ ");
				parameters.put("SHRQZ", SHRQZ);
			}
			if (req.containsKey("JZRQQ") && djzt == 2) {
				Date JZRQQ = sdf.parse(req.get("JZRQQ") + "");
				sql_list.append(" and JZRQ >=:JZRQQ ");
				parameters.put("JZRQQ", JZRQQ);
			}
			if (req.containsKey("JZRQZ") && djzt == 2) {
				Date JZRQZ = sdf.parse(req.get("JZRQZ") + "");
				sql_list.append(" and JZRQ <=:JZRQZ ");
				parameters.put("JZRQZ", JZRQZ);
			}

			sql_list.append(" order by LZDH desc ");
			List<Map<String, Object>> inofListCount = dao.doSqlQuery(
					sql_list.toString(), parameters);
			int total = inofListCount.size();

			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_CK01_EJKF);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房出库明细查询失败！");
		} catch (ParseException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "二级库房出库明细查询失败！");
		}
	}

	public void doGetBrxxByDjxh(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("DJXH", parseLong(body.get("DJXH")));
			String sql = "select max(b.BRXM) as BRXM,max(b.BRLY) as BRLY from WL_CK01 a ,WL_XHMX b "
					+ " where a.DJXH=b.DJXH and a.DJXH=:DJXH";
			Map<String, Object> map = dao.doLoad(sql, parameters);
			if (map.size() == 0) {
				return;
			}
			if (map.containsKey("BRXM")) {
				res.put("BRXM", map.get("BRXM"));
			}
			if (map.containsKey("BRLY")) {
				res.put("BRLY", map.get("BRLY"));
			}

		} catch (PersistentDataOperationException e) {

			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doQueryKtslByThdj(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long djxh = Long.parseLong(body.get("DJXH") + "");
		Long kcxh = Long.parseLong(body.get("KCXH") + "");
		Long jlxh = Long.parseLong(body.get("JLXH") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", djxh);
		parameters.put("KCXH", kcxh);
		try {
			Map<String, Object> mapthsl = dao
					.doLoad("select sum(b.WZSL) as WZSL from WL_CK01 a,WL_CK02 b where a.DJXH = b.DJXH and a.THDJ=:DJXH and b.KCXH=:KCXH",
							parameters);
			parameters.put("JLXH", jlxh);
			Map<String, Object> mapwzsl = dao
					.doLoad("select WZSL as WZSL from WL_CK02 where JLXH=:JLXH and DJXH=:DJXH and KCXH=:KCXH",
							parameters);
			double thsl = 0.00;
			double wzsl = 0.00;
			if (mapthsl != null) {
				if (mapthsl.get("WZSL") != null) {
					thsl = Double.parseDouble(mapthsl.get("WZSL") + "");
				}
			}
			if (mapwzsl != null) {
				if (mapwzsl.get("WZSL") != null) {
					wzsl = Double.parseDouble(mapwzsl.get("WZSL") + "");
				}
			}
			double thslresult = wzsl - thsl;
			res.put("THSL", String.format("%1$.2f", thslresult));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "可退数量获取失败");
		}
	}

	public String getLzdh() {
		String lzdh = "CK";
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
