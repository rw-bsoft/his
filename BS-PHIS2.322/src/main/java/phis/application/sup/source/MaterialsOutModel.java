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
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class MaterialsOutModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MaterialsOutModel.class);

	public MaterialsOutModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-4-16
	 * @description 保存物资出库记录
	 * @updateInfo
	 * @param body
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMaterialsOut(Map<String, Object> body, String op)
			throws ModelDataOperationException {
		Map<String, Object> parameterskfxx = new HashMap<String, Object>();
		Map<String, Object> ck = (Map<String, Object>) body.get("WL_CK01");
		List<Map<String, Object>> meds = (List<Map<String, Object>>) body
				.get("WL_CK02");
		double djje = 0.00;
		try {
			if (ck.get("KSDM") != null) {
				parameterskfxx.put("EJKF", Long.parseLong(ck.get("KSDM") + ""));
				Map<String, Object> kfxhMap = dao.doLoad(
						"select KFXH as KFXH from WL_KFXX where EJKF=:EJKF",
						parameterskfxx);
				if (kfxhMap != null) {
					if (kfxhMap.get("KFXH") != null) {
						ck.put("CKKF",
								Integer.parseInt(kfxhMap.get("KFXH") + ""));
					}
				}
			}
			if (ck.containsKey("DBTH") && (Boolean) ck.get("DBTH")) {
				ck.put("QRRK", Long.parseLong(ck.get("LZFS") + ""));
			}
			for (int i = 0; i < meds.size(); i++) {
				double wzje = 0.00;
				if (meds.get(i).get("WZJE") != null) {
					wzje = Double.parseDouble(meds.get(i).get("WZJE") + "");
				}
				djje = djje + wzje;
			}
			if ("create".equals(op)) {
				ck.put("DJJE", djje);
				Map<String, Object> djlxmap = dao.doSave("create",
						BSPHISEntryNames.WL_CK01, ck, false);
				Long djxh = Long.parseLong(djlxmap.get("DJXH") + "");
				for (int i = 0; i < meds.size(); i++) {
					meds.get(i).put("DJXH", djxh);
					if (meds.get(i).get("WZPH") != null) {
						meds.get(i).put("WZPH", meds.get(i).get("WZPH") + "");
					} else {
						meds.get(i).put("WZPH", "");
					}
					if (meds.get(i).get("MJPH") != null) {
						meds.get(i).put("MJPH", meds.get(i).get("MJPH") + "");
					} else {
						meds.get(i).put("MJPH", "");
					}
					dao.doSave("create", BSPHISEntryNames.WL_CK02, meds.get(i),
							false);
				}
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("DJXH", Long.parseLong(ck.get("DJXH") + ""));
				StringBuffer deleteHql = new StringBuffer();
				deleteHql.append("delete from WL_CK02").append(
						" where DJXH=:DJXH");
				dao.doUpdate(deleteHql.toString(), parameters);
				dao.doSave("update", BSPHISEntryNames.WL_CK01, ck, false);
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					med.put("DJXH", Long.parseLong(ck.get("DJXH") + ""));
					if (med.get("WZPH") != null) {
						med.put("WZPH", med.get("WZPH") + "");
					} else {
						med.put("WZPH", "");
					}
					if (med.get("MJPH") != null) {
						med.put("MJPH", med.get("MJPH") + "");
					} else {
						med.put("MJPH", "");
					}
					dao.doSave("create", BSPHISEntryNames.WL_CK02, med, false);
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage record keeping validation fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存验证失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records Save failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	/**
	 * 
	 * @author shiwy // * @createDate 2013-4-16
	 * @description 打开出库单提交页面前校验数据是否已经删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doVerificationMaterialsOutDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		Long djxh = Long.parseLong(body.get("DJXH") + "");
		StringBuffer hql_rk_isDelete = new StringBuffer();// 入库记录是否已经被删除
		Map<String, Object> m = new HashMap<String, Object>();
		hql_rk_isDelete.append(" DJXH=:DJXH");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", djxh);
		try {
			Long l = dao.doCount("WL_CK01", hql_rk_isDelete.toString(),
					parameters);
			if (l == 0) {
				m.put("code", ServiceCode.CODE_RECORD_REPEAT);
				m.put("msg", "该出库单已经删除,请刷新页面");
				return m;
			}
			m.put("code", ServiceCode.CODE_OK);
			m.put("msg", "ok");
			return m;
		} catch (PersistentDataOperationException e) {
			logger.error("Query storage records are deleted failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询出库记录是否被删除失败");
		}
	}

	public void doGetMaterialsOutDJZT(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Map<String, Object> djztMap = dao.doLoad(
					"select DJZT as DJZT from WL_CK01  where DJXH=:DJXH",
					parameters);
			res.put("djzt", djztMap.get("DJZT"));
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doQueryMaterialsOutLZFS(Map<String, Object> body,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("DJXH", Long.parseLong(body.get("DJXH") + ""));
		try {
			Long l = dao.doCount("WL_CK01 a,WL_LZFS b",
					"a.LZFS=b.FSXH and b.DJLX='DB' and DJXH=:DJXH", parameters);
			if (l <= 0) {
				res.put(Service.RES_CODE, 600);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		}
	}

	public void doSaveMaterialsOutVerify(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
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
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ from WL_CK02 a,WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH=0 and (a.YKBZ<>1 or a.YKBZ is null)",
							parameters);
			List<Map<String, Object>> ck02ph = dao
					.doQuery(
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,b.KFXH as KFXH,b.THDJ as THDJ,a.YKBZ as YKBZ from WL_CK02 a,WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and a.KCXH<>0 and (a.YKBZ<>1 or a.YKBZ is null)",
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
					Map<String, Object> wzckwzsl = dao
							.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterskcxh);
					if (Double.parseDouble(ck02ph.get(i).get("WZSL") + "") > Double
							.parseDouble(wzckwzsl.get("KYSL") + "")) {
						Map<String, Object> wzzdwzmc = dao
								.doLoad("select WZMC as WZMC from WL_WZZD where WZXH=:WZXH",
										parameterswzxx);
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
									"select JLXH as JLXH,KCXH as KCXH,WZJG as WZJG,LSJG as LSJG,CJXH as CJXH,WZPH as WZPH,str(SCRQ,'YYYY-MM-DD HH24:MI:SS') as SCRQ,str(SXRQ,'YYYY-MM-DD HH24:MI:SS') as SXRQ,MJPH as MJPH,(WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where WZXH=:WZXH and KFXH=:KFXH and WZSL<>YKSL order by KCXH",
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
					if (ck02ph.get(i).get("YKBZ") != null
							&& ck02ph.get(i).get("YKBZ").equals("1")) {
						continue;
					}
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
					}
					parameterskcxh.put("KCXH",
							Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
					parameterskcxh.put("KFXH",
							Integer.parseInt(ck02ph.get(i).get("KFXH") + ""));
					Map<String, Object> wzckwzsl = dao
							.doLoad("select (WZSL-YKSL) as KYSL,YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
									parameterskcxh);
					if (ck02ph.get(i).get("THDJ") == null) {
						parametersykslupd.put("KCXH",
								Long.parseLong(ck02ph.get(i).get("KCXH") + ""));
						parametersykslupd.put("KFXH", Integer.parseInt(ck02ph
								.get(i).get("KFXH") + ""));
						parametersykslupd.put(
								"YKSL",
								Double.parseDouble(wzckwzsl.get("YKSL") + "")
										+ Double.parseDouble(ck02ph.get(i).get(
												"WZSL")
												+ ""));
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
								parametersykslupd);
					} else {
						if (glfs == 2) {
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
									"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
									parametersykslupd);
						}
					}

				}
			}
			if (shsign == 0) {
				if (ck02.size() > 0 || ck02ph.size() >= 0) {
					String uid = user.getUserId();
					parametersck01upd.put("DJXH",
							Long.parseLong(body.get("DJXH") + ""));
					parametersck01upd.put("SHGH", uid);
					parametersck01upd.put("SHRQ", new Date());
					parametersck01upd.put("LZDH", getLzdh());
					dao.doUpdate(
							"update WL_CK01 set LZDH=:LZDH,DJZT=1,SHRQ=:SHRQ,SHGH=:SHGH where DJXH=:DJXH",
							parametersck01upd);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "出库记录保存失败");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	public void doSaveMaterialsOutNoVerify(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
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
							"select a.ZBXH as ZBXH,a.JLXH as JLXH,a.WZXH as WZXH,a.KCXH as KCXH,a.WZSL as WZSL,a.GLXH as GLXH,b.KFXH as KFXH,b.THDJ as THDJ,a.YKBZ as YKBZ from WL_CK02 a,WL_CK01 b where a.DJXH=b.DJXH and a.DJXH=:DJXH and (a.YKBZ<>1 or a.YKBZ is null)",
							parameters);
			// 不管是按批次还是库存,都要更新WL_WZKC表里的YKSL
			int sign = 0;
			for (int i = 0; i < ck02.size(); i++) {
				if (ck02.get(i).get("YKBZ") != null
						&& ck02.get(i).get("YKBZ").equals("1")) {
					continue;
				}
				int glfs = 0;
				parameterswzxx.put("WZXH",
						Long.parseLong(ck02.get(i).get("WZXH") + ""));
				Map<String, Object> wzzdglfs = dao.doLoad(
						"select GLFS as GLFS from WL_WZZD where WZXH=:WZXH",
						parameterswzxx);
				if (wzzdglfs.get("GLFS") != null) {
					glfs = Integer.parseInt(wzzdglfs.get("GLFS") + "");
					sign = glfs;
				}

				if (glfs == 3) {
					if (ck02.get(i).get("ZBXH") != null) {
						parameterszczbupd.put("ZBXH",
								Long.parseLong(ck02.get(i).get("ZBXH") + ""));
						dao.doUpdate(
								"update WL_ZCZB set CLBZ=0 where ZBXH=:ZBXH",
								parameterszczbupd);
					}
				}
				parameterswzkcyksl.put("KCXH",
						Long.parseLong(ck02.get(i).get("KCXH") + ""));
				parameterswzkcyksl.put("KFXH",
						Integer.parseInt(ck02.get(i).get("KFXH") + ""));
				Map<String, Object> wzkcyksl = dao
						.doLoad("select YKSL as YKSL from WL_WZKC where KCXH=:KCXH and KFXH=:KFXH",
								parameterswzkcyksl);
				if (ck02.get(i).get("THDJ") == null) {
					parameterswzkcupd.put("KCXH",
							Long.parseLong(ck02.get(i).get("KCXH") + ""));
					parameterswzkcupd.put("KFXH",
							Integer.parseInt(ck02.get(i).get("KFXH") + ""));
					parameterswzkcupd.put(
							"YKSL",
							Double.parseDouble(wzkcyksl.get("YKSL") + "")
									- Double.parseDouble(ck02.get(i)
											.get("WZSL") + ""));
					dao.doUpdate(
							"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
							parameterswzkcupd);
				} else {
					if (glfs == 2) {
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
								"update WL_KSZC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH",
								parameterswzkcupd);
					}
				}

			}
			if (sign != 3) {
				if ("0".equals(ckwzjslx)) {
					List<Map<String, Object>> ck02wzsl = dao
							.doQuery(
									"select sum(WZSL) as WZSL,WZXH as WZXH from WL_CK02 where DJXH=:DJXH and GLXH is not null and (YKBZ<>1 or YKBZ is null) group by WZXH",
									parameters);
					for (int i = 0; i < ck02wzsl.size(); i++) {
						parametersck02wzsl.put("WZXH", Long.parseLong(ck02wzsl
								.get(i).get("WZXH") + ""));
						parametersck02wzsl.put("DJXH",
								Long.parseLong(body.get("DJXH") + ""));
						Map<String, Object> wzslMap = dao
								.doLoad("select WZSL as WZSL from WL_CK02 where DJXH=:DJXH and WZXH=:WZXH and GLXH is null and (YKBZ<>1 or YKBZ is null)",
										parametersck02wzsl);
						parametersck02upd.put("DJXH",
								Long.parseLong(body.get("DJXH") + ""));
						parametersck02nowzslupd.put("DJXH",
								Long.parseLong(body.get("DJXH") + ""));
						parametersck02upd.put("WZXH", Long.parseLong(ck02wzsl
								.get(i).get("WZXH") + ""));
						parametersck02upd.put(
								"WZSL",
								Double.parseDouble(ck02wzsl.get(i).get("WZSL")
										+ "")
										+ Double.parseDouble(wzslMap
												.get("WZSL") + ""));
						dao.doUpdate(
								"update WL_CK02 set WZSL=:WZSL,KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0,WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and WZXH=:WZXH and GLXH is null and (YKBZ<>1 or YKBZ is null)",
								parametersck02upd);
						dao.doUpdate(
								"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and KCXH is not null and (YKBZ<>1 or YKBZ is null)",
								parametersck02nowzslupd);
						dao.doUpdate(
								"delete from WL_CK02 where DJXH=:DJXH and WZXH=:WZXH and GLXH is not null and (YKBZ<>1 or YKBZ is null)",
								parametersck02wzsl);
					}
					if (ck02wzsl.size() <= 0) {
						parametersck02nowzslupd.put("DJXH",
								Long.parseLong(body.get("DJXH") + ""));
						dao.doUpdate(
								"update WL_CK02 set KCXH=0,CJXH='',WZJG=0,WZJE=0,LSJG=0,LSJE=0, WZPH='',SCRQ='',SXRQ='',MJPH='' where DJXH=:DJXH and GLXH is null and (YKBZ<>1 or YKBZ is null)",
								parametersck02nowzslupd);
					}
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

	public void doSaveCommit(List<Map<String, Object>> ids_ywmx,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		double djje = 0;
		int djlx = 0;
		if (ids_ywmx.get(0).get("DJLX") != null) {
			djlx = Integer.parseInt(ids_ywmx.get(0).get("DJLX") + "");
		}
		try {
			long al_lzfs = Long.parseLong(ids_ywmx.get(0).get("LZFS") + "");
			boolean back = BSPHISUtil.Uf_access(ids_ywmx, al_lzfs, dao, ctx);
			if (!back) {
				throw new RuntimeException("确认失败！");
			} else {
				UserRoleToken user = UserRoleToken.getCurrent();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("DJXH", ids_ywmx.get(0).get("DJXH"));
				if (djlx != 7) {
					map.put("DJZT", 2);
					map.put("JZRQ", new Date());
					map.put("JZGH", user.getUserId());
				} else {
					map.put("QRBZ", 1);
					map.put("QRGH", user.getUserId());
					map.put("QRRQ", new Date());
				}
				for (int i = 0; i < ids_ywmx.size(); i++) {
					if (ids_ywmx.get(i).get("WZJE") != null) {
						djje += Double.parseDouble(ids_ywmx.get(i).get("WZJE")
								+ "");
					}
				}
				map.put("DJJE", djje);
				dao.doSave("update", BSPHISEntryNames.WL_CK01, map, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

	public void doUpdateVerify(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long djxh = 0L;
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (req.get("DJXH") != null) {
			djxh = Long.parseLong(req.get("DJXH") + "");
		}
		parameters.put("DJXH", djxh);
		try {
			dao.doUpdate("update WL_CK01 set DJZT=0 where DJXH=:DJXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doUpdateLZFS(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long djxh = 0L;
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (req.get("DJXH") != null) {
			djxh = Long.parseLong(req.get("DJXH") + "");
		}
		parameters.put("DJXH", djxh);
		Long lzfs = 0L;
		if (req.get("LZFS") != null) {
			lzfs = Long.parseLong(req.get("LZFS") + "");
		}
		parameters.put("LZFS", lzfs);
		try {
			dao.doUpdate("update WL_CK01 set LZFS=:LZFS where DJXH=:DJXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询物资信息
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetFixedAssetsInformation(Map<String, Object> req,
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
		Map<String, Object> parthdj = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		int wzxh = 0;
		String tableName = null;
		long djxh = 0l;
		long kcxh = 0l;
		int th = 0;
		if (req.containsKey("cnd")) {
			wzxh = (Integer) req.get("cnd");
		}
		if (req.containsKey("tableName")) {
			tableName = req.get("tableName") + "";
		}
		if (req.containsKey("djxh")) {
			djxh = Long.parseLong(req.get("djxh") + "");
		}
		if (req.containsKey("kcxh")) {
			kcxh = Long.parseLong(req.get("kcxh") + "");
		}
		if (req.containsKey("th")) {
			th = Integer.parseInt(req.get("th") + "");
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
		parameters.put("WZXH", wzxh);
		parameterssize.put("WZXH", wzxh);
		String sql = "select a.ZBXH as ZBXH,a.WZBH as WZBH,b.WZXH as WZXH,b.WZMC as WZMC,c.CJXH as CJXH,c.CJMC as CJMC,a.KCXH as KCXH,b.WZGG as WZGG,b.WZDW as WZDW,a.ZCYZ as ZCYZ,1 as TJSL,0 as BKBZ,3 as GLFS,a.WZZT as WZZT,to_char(a.QYRQ,'YYYY-MM-DD') as QYRQ from WL_ZCZB a,WL_WZZD b,WL_SCCJ c where a.WZXH=b.WZXH and a.CJXH=c.CJXH and a.WZZT=0 and CLBZ=0 and a.WZXH=:WZXH and a.KFXH=:KFXH";
		parthdj.put("DJXH", djxh);
		try {
			if (tableName != null) {
				List<Map<String, Object>> djxhList = dao.doSqlQuery(
						"select nvl(THDJ,0) as THDJ from " + tableName
								+ " where DJXH=:DJXH", parthdj);
				int thdj = 0;
				if (djxhList.size() > 0) {
					thdj = Integer.parseInt(djxhList.get(0).get("THDJ") + "");
				}
				if (thdj > 0 || th == 1) {
					parameters.put("KCXH", kcxh);
					parameterssize.put("KCXH", kcxh);
					sql = "select a.ZBXH as ZBXH,a.WZBH as WZBH,b.WZXH as WZXH,b.WZMC as WZMC,c.CJXH as CJXH,c.CJMC as CJMC,a.KCXH as KCXH,b.WZGG as WZGG,b.WZDW as WZDW,a.ZCYZ as ZCYZ,1 as TJSL,0 as BKBZ,3 as GLFS,a.WZZT as WZZT,to_char(a.QYRQ,'YYYY-MM-DD') as QYRQ from WL_ZCZB a,WL_WZZD b,WL_SCCJ c where a.WZXH=b.WZXH and a.CJXH=c.CJXH and a.WZZT=0 and CLBZ=0 and a.WZXH=:WZXH and a.KFXH=:KFXH and a.KCXH=:KCXH";
				}
			}
			list = dao.doSqlQuery(sql, parameters);
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("WZZT_text", "在库");
			}
			listsize = dao.doSqlQuery(sql, parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doGetFixedAssets(List<Object> body, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		try {
			if (body.size() > 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				String ZBXH = body.toString().substring(1,
						body.toString().length() - 1);
				List<Map<String, Object>> wzzdMap = dao
						.doQuery(
								"select b.WZXH as WZXH,b.WZMC as WZMC,c.CJXH as CJXH,c.CJMC as CJMC,b.WZGG as WZGG,b.WZDW as WZDW,1 as TJCKSL,1 as WZSL,1 as KTSL,a.CZYZ as WZJG,(1*a.CZYZ) as WZJE,a.KCXH as KCXH,3 as GLFS,a.KCXH as KCXH,a.ZBXH as ZBXH,'' as WZPH,'' as MJPH from WL_ZCZB a,WL_WZZD b,WL_SCCJ c where a.WZXH=b.WZXH and a.CJXH=c.CJXH and a.WZZT=0 and a.ZBXH in("
										+ ZBXH + ")", parameters);
				for (int i = 0; i < wzzdMap.size(); i++) {
					wzzdMap.get(i).put("GLFS_text", "台账管理");
				}
				res.put("body", wzzdMap);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：入失败.");
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
				"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,c.CJMC as CJMC,b.GLFS as GLFS,b.WZDW as WZDW,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,to_char(a.SCRQ,'YYYY-MM-DD') as SCRQ,to_char(a.SXRQ,'YYYY-MM-DD') as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ from WL_CK02 a left outer join WL_SCCJ c on a.CJXH=c.CJXH,WL_WZZD b where a.WZXH=b.WZXH");
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
						&& !"0".equals(list.get(i).get("KCXH") + "")) {
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

	/**
	 * 科室权限分配的科室信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetCK02DBDJInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		Map<String, Object> parameterskcxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
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
				"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.GLFS as GLFS,b.WZDW as WZDW,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,to_char(a.SCRQ,'YYYY-MM-DD') as SCRQ,to_char(a.SXRQ,'YYYY-MM-DD') as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ,c.KFXH as KFXH from WL_CK02 a,WL_WZZD b,WL_CK01 c where a.WZXH=b.WZXH and a.DJXH=c.DJXH");
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
				int kfxh = 0;
				if (list.get(i).get("KFXH") != null) {
					kfxh = Integer.parseInt(list.get(i).get("KFXH") + "");
				}// 用户的机构ID}
				if (list.get(i).get("KCXH") != null
						&& !"0".equals(list.get(i).get("KCXH") + "")) {
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
					BSPHISEntryNames.WL_CK02 + "_DB");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
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
	public void doGetCK02DBGLInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		Map<String, Object> parameterskcxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
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
				"select a.JLXH as JLXH,a.DJXH as DJXH,a.WZXH as WZXH,b.WZMC as WZMC,b.WZGG as WZGG,b.GLFS as GLFS,b.WZDW as WZDW,a.CJXH as CJXH,a.WZSL as WZSL,a.WZJG as WZJG,a.WZJE as WZJE,a.LSJG as LSJG,a.LSJE as LSJE,a.WZPH as WZPH,a.MJPH as MJPH,to_char(a.SCRQ,'YYYY-MM-DD') as SCRQ,to_char(a.SXRQ,'YYYY-MM-DD') as SXRQ,a.GLXH as GLXH,a.THMX as THMX,a.KCXH as KCXH,a.ZBXH as ZBXH,a.SLXH as SLXH,a.SLSL as SLSL,a.WFSL as WFSL,a.JHBZ as JHBZ,c.KFXH as KFXH from WL_CK02 a,WL_WZZD b,WL_CK01 c where a.WZXH=b.WZXH and a.DJXH=c.DJXH");
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
				int kfxh = 0;
				if (list.get(i).get("KFXH") != null) {
					kfxh = Integer.parseInt(list.get(i).get("KFXH") + "");
				}// 用户的机构ID}
				if (list.get(i).get("KCXH") != null
						&& !"0".equals(list.get(i).get("KCXH") + "")) {
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
					BSPHISEntryNames.WL_CK02 + "_DB");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public String getLzdh() {
		String lzdh = "CK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		lzdh += sdf.format(new Date());
		return lzdh;
	}

	public void doRemoveMaterialsOut(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long djxh = 0L;
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (req.get("DJXH") != null) {
			djxh = Long.parseLong(req.get("DJXH") + "");
		}
		parameters.put("DJXH", djxh);
		try {
			dao.doUpdate("delete from WL_CK01 where DJXH=:DJXH", parameters);
			dao.doUpdate("delete from WL_CK02 where DJXH=:DJXH", parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public void doRemoveAllocationManagement(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long djxh = 0L;
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (req.get("DJXH") != null) {
			djxh = Long.parseLong(req.get("DJXH") + "");
		}
		parameters.put("DJXH", djxh);
		try {
			dao.doUpdate("update WL_CK01 set DJZT=-1 where DJXH=:DJXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据流转方式查询流转方式表中的特殊标志。
	 */
	public void doQueryLzfs_Tsbz(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String FSXH = req.get("LZFS") + "";
		int TSBZ = 0;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FSXH", Long.parseLong(FSXH));
		StringBuffer sql_list = new StringBuffer(
				"SELECT DISTINCT TSBZ as TSBZ FROM WL_LZFS where FSXH =:FSXH ");
		List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
		try {
			inofList = dao.doSqlQuery(sql_list.toString(), parameters);
			if (inofList.size() > 0) {
				TSBZ = Integer.parseInt(inofList.get(0).get("TSBZ") + "");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		res.put("ret", TSBZ);
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
			e.printStackTrace();
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "可退数量获取失败");
		}
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

}
