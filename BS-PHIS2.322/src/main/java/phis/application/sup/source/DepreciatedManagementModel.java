package phis.application.sup.source;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class DepreciatedManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(DepreciatedManagementModel.class);

	public DepreciatedManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doQueryZJXX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		String cwyf = req.get("cwyf") + "";
		parameters.put("al_kfxh", kfxh);
		parameters.put("as_cwyf", cwyf);
		String sql = "SELECT WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.ZJFF as ZJFF,WL_WZZD.ZJNX as ZJNX,WL_WZZD.ZGZL as ZGZL,WL_WZZD.JCZL as JCZL,WL_ZCZB.WZXH as WZXH,WL_ZCZB.CJXH as CJXH,WL_SCCJ.CJMC as CJMC,WL_ZCZB.WZBH as WZBH,WL_ZCZB.JTZJ as JTZJ,WL_ZCZB.CZYZ as CZYZ, WL_ZCZB.ZJYS as ZJYS,WL_ZCZB.KCXH as KCXH,WL_YJZC.JLXH as JLXH,WL_YJZC.JZXH as JZXH,WL_YJZC.KFXH as KFXH,WL_YJZC.ZBLB as ZBLB,WL_YJZC.CWYF as CWYF,WL_YJZC.ZBXH as ZBXH,WL_YJZC.WZZT as WZZT,WL_YJZC.ZYKS as ZYKS,WL_ZCZB.QYRQ as QYRQ,WL_ZCZB.TZRQ as TZRQ,A.ZJJE as ZJJE,A.ZCXZ as ZCXZ,A.ZJSM as ZJSM,A.GZL as GZL FROM WL_YJZC WL_YJZC left outer join (select b.ZBXH as ZBXH,b.ZJJE as ZJJE,b.ZCXZ as ZCXZ,b.ZJSM as ZJSM,b.GZL as GZL from WL_ZJJL a,WL_ZJMX b where a.ZJXH=b.ZJXH and a.KFXH = :al_kfxh and a.CWYF = :as_cwyf) A on WL_YJZC.ZBXH=A.ZBXH, WL_ZCZB WL_ZCZB, WL_WZZD WL_WZZD,WL_SCCJ WL_SCCJ WHERE (WL_WZZD.WZXH = WL_ZCZB.WZXH) and(WL_ZCZB.CJXH=WL_SCCJ.CJXH) and (WL_YJZC.ZBXH = WL_ZCZB.ZBXH) and (WL_YJZC.KFXH = :al_kfxh) AND (WL_YJZC.CWYF = :as_cwyf) AND (WL_YJZC.WZZT = 1)";
		try {
			List<Map<String, Object>> zjxxList = dao
					.doSqlQuery(sql, parameters);
			SchemaUtil.setDictionaryMassageForList(zjxxList,
					BSPHISEntryNames.WL_YJZC_ZCZB_WZZD);
			res.put("body", zjxxList);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询折旧信息失败");
		}
	}

	public void doSaveZJXX(List<Map<String, Object>> req,
			Map<String, Object> res, String cwyf, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> returnref = new HashMap<String, Object>();
		Map<String, Object> zjjlMapsave = new HashMap<String, Object>();
		Map<String, Object> zjmxMapsave = new HashMap<String, Object>();
		Map<String, Object> zjjlpar = new HashMap<String, Object>();
		Map<String, Object> zbxhpar = new HashMap<String, Object>();
		Map<String, Object> zjjlmonthpar = new HashMap<String, Object>();
		String zjsm1 = null;
		String zjsm2 = null;
		StringBuffer mzmcstr1 = new StringBuffer();
		StringBuffer mzmcstr2 = new StringBuffer();
		zjjlMapsave.put("KFXH", kfxh);
		zjjlMapsave.put("JGID", jgid);
		zjjlMapsave.put("CWYF", cwyf);
		zjjlMapsave.put("ZJRQ", new Date());
		zjjlMapsave.put("ZXRQ", new Date());
		zjjlMapsave.put("CZGH", user.getUserId());
		zjjlpar.put("KFXH", kfxh);
		zjjlmonthpar.put("KFXH", kfxh);
		zjjlpar.put("CWYF", cwyf);
		try {
			long l = dao.doCount("WL_ZJJL", "KFXH=:KFXH and CWYF=:CWYF",
					zjjlpar);
			if (l > 0) {
				res.put(Service.RES_CODE, 608);
				res.put(Service.RES_MESSAGE, "本月已经折旧!");
			} else {
				SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
				List<Map<String, Object>> cwyflist = dao
						.doSqlQuery(
								"select max(CWYF) as CWYF from WL_ZJJL where KFXH=:KFXH",
								zjjlmonthpar);
				String zjjlcwyf = cwyf;
				if (cwyflist.size() > 0) {
					if (cwyflist.get(0).get("CWYF") != null) {
						zjjlcwyf = cwyflist.get(0).get("CWYF") + "";
					}
				}
				int year = parseInt(zjjlcwyf.substring(0, 4));
				int month = parseInt(zjjlcwyf.substring(4));
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, year);
				if (!zjjlcwyf.equals(cwyf)) {
					c.set(Calendar.MONTH, month);
				} else {
					c.set(Calendar.MONTH, month - 1);
				}
				c.set(Calendar.DATE, 10);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				String cwyfadd = sdfcwyf.format(c.getTime());
				if (!cwyfadd.equals(cwyf)) {
					res.put(Service.RES_CODE, 610);
					res.put(Service.RES_MESSAGE, "上月未折旧,本月折旧无法进行,请先做上一月的折旧处理!");
				} else {
					for (int i = 0; i < req.size(); i++) {
						String zjsm = null;
						Map<String, Object> zjjlxxMap = dao
								.doLoad("select ZJXH as ZJXH from WL_ZJJL where KFXH=:KFXH and CWYF=:CWYF",
										zjjlpar);
						if (zjjlxxMap == null) {
							Map<String, Object> zjxhMap = dao.doSave("create",
									BSPHISEntryNames.WL_ZJJL, zjjlMapsave,
									false);
							long zjxh = parseLong(zjxhMap.get("ZJXH"));
							doGetBczj(req.get(i), returnref);
							if (returnref.get("AS_ZJSM") != null) {
								zjsm = returnref.get("AS_ZJSM") + "";
							}
							if (zjsm == null || "已经到折旧年限".equals(zjsm)
									|| "已经折旧到残值".equals(zjsm)) {
								zjmxMapsave.put("ZJSM", "");
								if ("已经到折旧年限".equals(zjsm)
										|| "已经折旧到残值".equals(zjsm)) {
									zjmxMapsave.put("ZJSM", zjsm);
								}
								zjmxMapsave.put("ZJXH", zjxh);
								zjmxMapsave.put("KFXH", kfxh);
								zjmxMapsave.put("ZBLB", parseLong(req.get(i)
										.get("ZBLB")));
								zjmxMapsave.put("WZXH", parseLong(req.get(i)
										.get("WZXH")));
								zjmxMapsave.put("CJXH", parseLong(req.get(i)
										.get("CJXH")));
								zjmxMapsave.put("ZBXH", parseLong(req.get(i)
										.get("ZBXH")));
								zjmxMapsave.put("ZYKS", parseLong(req.get(i)
										.get("ZYKS")));
								zjmxMapsave.put("GZL", parseDouble(req.get(i)
										.get("GZL")));
								zjmxMapsave.put("ZJFF", parseInt(req.get(i)
										.get("ZJFF")));
								zjmxMapsave.put("ZJJE", parseDouble(String
										.format("%.2f",
												returnref.get("ADC_ZJJE"))));
								zjmxMapsave.put("ZCXZ", parseDouble(req.get(i)
										.get("CZYZ")));
								zjmxMapsave
										.put("LJZJ",
												parseDouble(req.get(i).get(
														"JTZJ"))
														+ parseDouble(String
																.format("%.2f",
																		returnref
																				.get("ADC_ZJJE"))));
								zjmxMapsave.put("JLLX", 0);
								zjmxMapsave.put("ZJRQ", new Date());
								zjmxMapsave.put("ZXRQ", new Date());
								dao.doSave("create", BSPHISEntryNames.WL_ZJMX,
										zjmxMapsave, false);
								zbxhpar.put("ZBXH",
										parseLong(req.get(i).get("ZBXH")));
								dao.doUpdate(
										"update WL_ZCZB set ZJYS=nvl(ZJYS,0)+1 where ZBXH=:ZBXH",
										zbxhpar);
								dao.doUpdate("update WL_ZCZB set JTZJ="
										+ zjmxMapsave.get("LJZJ")
										+ " where ZBXH=:ZBXH", zbxhpar);
							} else {
								String wzmc = null;
								if (returnref.get("AS_WZMC") != null) {
									wzmc = returnref.get("AS_WZMC") + "";
								}
								zjsm1 = zjsm;
								if (zjsm1.equals(zjsm)) {
									zjsm1 = zjsm;
									if (mzmcstr1.toString().indexOf(wzmc) < 0) {
										mzmcstr1.append(wzmc);
										mzmcstr1.append(",");
									}
								} else {
									zjsm2 = zjsm;
									if (mzmcstr2.toString().indexOf(wzmc) < 0) {
										mzmcstr2.append(wzmc);
										mzmcstr2.append(",");
									}
								}
							}
						} else {
							long zjxh = parseLong(zjjlxxMap.get("ZJXH"));
							zjmxMapsave.put("ZJXH", zjxh);
							doGetBczj(req.get(i), returnref);
							if (returnref.get("AS_ZJSM") != null) {
								zjsm = returnref.get("AS_ZJSM") + "";
							}
							if (zjsm == null || "已经到折旧年限".equals(zjsm)
									|| "已经折旧到残值".equals(zjsm)) {
								zjmxMapsave.put("ZJSM", "");
								if ("已经到折旧年限".equals(zjsm)
										|| "已经折旧到残值".equals(zjsm)) {
									zjmxMapsave.put("ZJSM", zjsm);
								}
								zjmxMapsave.put("KFXH", kfxh);
								zjmxMapsave.put("ZBLB", parseLong(req.get(i)
										.get("ZBLB")));
								zjmxMapsave.put("WZXH", parseLong(req.get(i)
										.get("WZXH")));
								zjmxMapsave.put("CJXH", parseLong(req.get(i)
										.get("CJXH")));
								zjmxMapsave.put("ZBXH", parseLong(req.get(i)
										.get("ZBXH")));
								zjmxMapsave.put("ZYKS", parseLong(req.get(i)
										.get("ZYKS")));
								zjmxMapsave.put("GZL", parseDouble(req.get(i)
										.get("GZL")));
								zjmxMapsave.put("ZJFF", parseInt(req.get(i)
										.get("ZJFF")));
								zjmxMapsave.put("ZJJE", parseDouble(String
										.format("%.2f",
												returnref.get("ADC_ZJJE"))));
								zjmxMapsave.put("ZCXZ", parseDouble(req.get(i)
										.get("CZYZ")));
								zjmxMapsave
										.put("LJZJ",
												parseDouble(req.get(i).get(
														"JTZJ"))
														+ parseDouble(String
																.format("%.2f",
																		returnref
																				.get("ADC_ZJJE"))));
								zjmxMapsave.put("JLLX", 0);
								zjmxMapsave.put("ZJRQ", new Date());
								zjmxMapsave.put("ZXRQ", new Date());
								dao.doSave("create", BSPHISEntryNames.WL_ZJMX,
										zjmxMapsave, false);
								zbxhpar.put("ZBXH",
										parseLong(req.get(i).get("ZBXH")));
								dao.doUpdate(
										"update WL_ZCZB set ZJYS=nvl(ZJYS,0)+1 where ZBXH=:ZBXH",
										zbxhpar);
								dao.doUpdate("update WL_ZCZB set JTZJ="
										+ zjmxMapsave.get("LJZJ")
										+ " where ZBXH=:ZBXH", zbxhpar);
							} else {
								String wzmc = null;
								if (returnref.get("AS_WZMC") != null) {
									wzmc = returnref.get("AS_WZMC") + "";
								}
								if (zjsm1 == null) {
									zjsm1 = zjsm;
								}
								if (zjsm1.equals(zjsm)) {
									zjsm1 = zjsm;
									if (mzmcstr1.toString().indexOf(wzmc) < 0) {
										mzmcstr1.append(wzmc);
										mzmcstr1.append(",");
									}
								} else {
									zjsm2 = zjsm;
									if (mzmcstr2.toString().indexOf(wzmc) < 0) {
										mzmcstr2.append(wzmc);
										mzmcstr2.append(",");
									}
								}
							}
						}
					}
				}
				String zjsmmasg1 = "";
				if (mzmcstr1.length() > 0) {
					zjsmmasg1 = zjsm1
							+ "["
							+ mzmcstr1.toString().substring(0,
									mzmcstr1.toString().length() - 1) + "]";
				}
				String zjsmmasg2 = "";
				if (mzmcstr2.length() > 0) {
					zjsmmasg2 = zjsm2
							+ "["
							+ mzmcstr2.toString().substring(0,
									mzmcstr2.toString().length() - 1) + "]";
				}

				if (mzmcstr1.length() > 0 || mzmcstr2.length() > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, zjsmmasg1 + ","
									+ zjsmmasg2 + ",已取消本次折旧!");
				}
			}
		} catch (ValidateException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "折旧失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "折旧失败");
		}
	}

	public void doGetBczj(Map<String, Object> req, Map<String, Object> returnref)
			throws ModelDataOperationException {
		int ai_zjff = parseInt(req.get("ZJFF"));// 折旧方法
		int ai_zjnx = parseInt(req.get("ZJNX"));// 折旧年限
		int ai_zjys = parseInt(req.get("ZJYS"));// 折旧月数
		double adc_jczl = parseDouble(req.get("JCZL"));// 净残值率
		double al_zgzl = parseDouble(req.get("ZGZL"));// 总工作量
		double al_gzl = parseDouble(req.get("GZL"));// 工作量
		double adc_czyz = parseDouble(req.get("CZYZ"));// 重置原值
		double adc_jtzj = parseDouble(req.get("JTZJ"));// 计提折旧
		double adc_zjje = 0.00;// 本次折旧
		String as_zjsm = null; // 折旧说明
		String as_wzmc = null; // 折旧说明
		double ldc_jtzj_all = 0.00; // 资产可折旧的总金额
		double ldc_zjl_year = 0.00;
		double ldc_zjl_month = 0.00; // 年折旧率,月折旧率
		double ldc_zjl_gzl = 0.00; // 工作量折旧率
		int li_used_year = 0;
		int li_used_month = 0;
		double ldc_yzje = 0.00;
		double ldc_yzje_pre = 0.00; // 已折金额
		int i;
		ldc_jtzj_all = adc_czyz * (1 - adc_jczl / 100); // 最多可以计提折旧的金额
		if (adc_jtzj >= ldc_jtzj_all) {
			adc_zjje = 0;
			as_zjsm = "已经折旧到残值";
			returnref.put("ADC_ZJJE", adc_zjje);
			returnref.put("AS_ZJSM", as_zjsm);
		} else {
			if (ai_zjff == 1) {
				if (ai_zjys >= (ai_zjnx * 12)) {
					adc_zjje = 0;
					as_zjsm = "已经到折旧年限";
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				} else {
					ldc_zjl_year = (1 - adc_jczl / 100) / ai_zjnx; // 年折旧率
					ldc_zjl_month = ldc_zjl_year / 12; // 月折旧率

					// 本次折旧金额
					adc_zjje = adc_czyz * ldc_zjl_month;
					if (adc_zjje > (ldc_jtzj_all - adc_jtzj)) {
						adc_zjje = ldc_jtzj_all - adc_jtzj;
					}
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				}
			} else if (ai_zjff == 2) {
				if (al_zgzl < 1 || al_zgzl == 0) {
					as_wzmc = req.get("WZMC") + "";
					as_zjsm = "用'总工作量法'折旧，总工作量不得为空或小于1(请到物资字典中维护)!";
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
					returnref.put("AS_WZMC", as_wzmc);
				} else if (al_gzl < 1 || al_gzl == 0) {
					as_wzmc = req.get("WZMC") + "";
					as_zjsm = "用'总工作量法'折旧，工作量不得为空或小于 1!";
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
					returnref.put("AS_WZMC", as_wzmc);
				} else {
					if (ai_zjys >= (ai_zjnx * 12)) {
						adc_zjje = 0;
						as_zjsm = "已经到折旧年限";
						returnref.put("ADC_ZJJE", adc_zjje);
						returnref.put("AS_ZJSM", as_zjsm);
					} else {
						ldc_zjl_gzl = (1 - adc_jczl / 100) / al_zgzl; // 每工作量折旧率
						// 本次折旧金额
						adc_zjje = adc_czyz * ldc_zjl_gzl * al_gzl; // 按工作量计算的折旧金额
						if (adc_zjje > (ldc_jtzj_all - adc_jtzj)) {
							adc_zjje = ldc_jtzj_all - adc_jtzj;
						}
						returnref.put("ADC_ZJJE", adc_zjje);
						returnref.put("AS_ZJSM", as_zjsm);
					}
				}
			} else if (ai_zjff == 3) {
				if (ai_zjys >= (ai_zjnx * 12)) {
					adc_zjje = 0;
					as_zjsm = "已经到折旧年限";
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				} else {
					ldc_yzje = adc_czyz;
					ldc_zjl_year = 2 / parseDouble(ai_zjnx);
					li_used_year = ai_zjys / 12;
					li_used_month = ai_zjys % 12;
					for (i = 1; i <= li_used_year; i++) {
						if (i < (ai_zjnx - 1)) {
							ldc_yzje -= ldc_yzje * ldc_zjl_year;
						} else if (i == (ai_zjnx - 1)) {
							li_used_month += 12;
						}
					}
					// ==============================================================================
					// 计算已完成余月折旧后现值单价（本次折旧前现值单价）
					// ==============================================================================
					if (li_used_month > 0) {
						if ((li_used_year + 1) < (ai_zjnx - 1)) { // 非最后两年
							ldc_yzje -= ldc_yzje * (ldc_zjl_year / 12)
									* li_used_month;
						} else { // 最后两年按月平均摊销
							ldc_yzje -= (ldc_yzje) / 24 * li_used_month;
						}
					}
					ldc_yzje_pre = adc_czyz - ldc_yzje;
					// ==============================================================================
					// 本次折旧后现值单价
					// ==============================================================================
					li_used_month = ai_zjys + 1;
					li_used_year = li_used_month / 12; // 已使用整年数
					// ==============================================================================
					// 如果折旧时已经超过折旧年限则折旧到折旧年限为止
					// ==============================================================================
					if (li_used_year >= ai_zjnx) {
						li_used_year = ai_zjnx;
					}
					li_used_month -= li_used_year * 12; // 已折旧余月数
					ldc_yzje = adc_czyz;
					for (i = 1; i <= li_used_year; i++) {
						if (i < (ai_zjnx - 1)) { // 非最后两年
							ldc_yzje -= ldc_yzje * ldc_zjl_year;
						} else if (i == (ai_zjnx - 1)) {
							li_used_month += 12;
						}
					}
					// ==============================================================================
					// 计算已完成余月折旧后现值单价（本次折旧前现值单价）
					// ==============================================================================
					if (li_used_month > 0) {
						if ((li_used_year + 1) < (ai_zjnx - 1)) {
							ldc_yzje -= ldc_yzje * (ldc_zjl_year / 12)
									* li_used_month;
						} else {
							ldc_yzje -= (ldc_yzje) / 24 * li_used_month;
						}
					}
					// 本次折旧金额
					adc_zjje = adc_czyz - ldc_yzje - ldc_yzje_pre;
					if (adc_zjje > (ldc_jtzj_all - adc_jtzj)) {
						adc_zjje = ldc_jtzj_all - adc_jtzj;
					}
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				}
			} else if (ai_zjff == 4) {
				if (ai_zjys >= (ai_zjnx * 12)) {
					adc_zjje = 0;
					as_zjsm = "已经到折旧年限";
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				} else {
					li_used_year = ai_zjys / 12 + 1;
					li_used_month = ai_zjys % 12;
					ldc_yzje_pre = 0;
					for (i = 1; i <= li_used_year; i++) {
						// 年折旧率
						ldc_zjl_year = (parseDouble(ai_zjnx) - i + 1) * 2
								/ parseDouble(ai_zjnx)
								/ (parseDouble(ai_zjnx) + 1);
						if (i == li_used_year) {
							ldc_yzje_pre += ldc_jtzj_all * (ldc_zjl_year / 12)
									* li_used_month;
						} else {
							ldc_yzje_pre += ldc_zjl_year * (1 - adc_jczl / 100)
									* adc_czyz;
						}
					}
					li_used_month = ai_zjys + 1; // 总共折旧月数
					li_used_year = li_used_month / 12; // 折算成年数
					if (li_used_year >= ai_zjnx) {
						li_used_year = ai_zjnx + 1;
						li_used_month = 0;
					} else {
						li_used_year++;
						li_used_month = li_used_month % 12; // 余下的月数
					}
					ldc_yzje = 0;
					for (i = 1; i <= li_used_year; i++) {
						// 年折旧率
						ldc_zjl_year = (parseDouble(ai_zjnx) - i + 1) * 2
								/ parseDouble(ai_zjnx)
								/ (parseDouble(ai_zjnx) + 1);
						if (i == li_used_year) {
							ldc_yzje += ldc_jtzj_all * (ldc_zjl_year / 12)
									* li_used_month;
						} else {
							ldc_yzje += ldc_zjl_year * (1 - adc_jczl / 100)
									* adc_czyz;
						}
					}
					// 本次折旧金额
					adc_zjje = ldc_yzje - ldc_yzje_pre; // 累计折旧-原累计折旧
					if (adc_zjje > (ldc_jtzj_all - adc_jtzj)) {
						adc_zjje = ldc_jtzj_all - adc_jtzj;
					}
					returnref.put("ADC_ZJJE", adc_zjje);
					returnref.put("AS_ZJSM", as_zjsm);
				}
			}
		}
	}

	public void doUpdateZJXX(List<Map<String, Object>> req,
			Map<String, Object> res, String cwyf, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		Map<String, Object> zjjlxxpar = new HashMap<String, Object>();
		Map<String, Object> zjjldelpar = new HashMap<String, Object>();
		Map<String, Object> zbxhpar = new HashMap<String, Object>();
		zjjlxxpar.put("KFXH", kfxh);
		zjjlxxpar.put("CWYF", cwyf);
		try {
			Map<String, Object> zjjlxxMap = dao
					.doLoad("select ZJXH as ZJXH from WL_ZJJL where KFXH=:KFXH and CWYF=:CWYF",
							zjjlxxpar);
			zjjldelpar.put("ZJXH", parseLong(zjjlxxMap.get("ZJXH")));
			for (int i = 0; i < req.size(); i++) {
				zbxhpar.put("ZBXH", parseLong(req.get(i).get("ZBXH")));
				zbxhpar.put("ZJXH", parseLong(zjjlxxMap.get("ZJXH")));
				Map<String, Object> zjmxxxMap = dao
						.doLoad("select ZJJE as ZJJE from WL_ZJMX where ZBXH=:ZBXH and ZJXH=:ZJXH",
								zbxhpar);
				zbxhpar.remove("ZJXH");
				dao.doUpdate(
						"update WL_ZCZB set ZJYS=nvl(ZJYS,0)-1 where ZBXH=:ZBXH",
						zbxhpar);
				if (zjmxxxMap != null) {
					dao.doUpdate("update WL_ZCZB set JTZJ=nvl(JTZJ,0)-"
							+ parseDouble(zjmxxxMap.get("ZJJE"))
							+ " where ZBXH=:ZBXH", zbxhpar);
				}
			}
			dao.doUpdate("delete from WL_ZJJL where ZJXH=:ZJXH", zjjldelpar);
			dao.doUpdate("delete from WL_ZJMX where ZJXH=:ZJXH", zjjldelpar);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消折旧失败");
		}
	}

	public void doQueryBYZJXX(Map<String, Object> req, Map<String, Object> res,
			String cwyf, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		Map<String, Object> zjjlxxpar = new HashMap<String, Object>();
		Map<String, Object> zjjlmonthpar = new HashMap<String, Object>();
		zjjlxxpar.put("KFXH", kfxh);
		zjjlxxpar.put("CWYF", cwyf);
		zjjlmonthpar.put("KFXH", kfxh);
		try {
			long l = dao.doCount("WL_ZJJL", "KFXH=:KFXH and CWYF=:CWYF",
					zjjlxxpar);
			if (l == 0) {
				res.put(Service.RES_CODE, 608);
				res.put(Service.RES_MESSAGE, "本月还没有折旧!");
			} else {
				List<Map<String, Object>> cwyflist = dao
						.doSqlQuery(
								"select max(CWYF) as CWYF from WL_ZJJL where KFXH=:KFXH",
								zjjlmonthpar);
				String zjjlcwyf = cwyf;
				if (cwyflist.size() > 0) {
					zjjlcwyf = cwyflist.get(0).get("CWYF") + "";
				}
				if (!zjjlcwyf.equals(cwyf)) {
					res.put(Service.RES_CODE, 611);
					res.put(Service.RES_MESSAGE, "只能取消最大值月份的折旧!");
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消折旧失败");
		}
	}

	public int parseInt(Object o) {
		if (o == null || o == "" || "null".equals(o) || "".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null || o == "" || "null".equals(o) || "".equals(o)) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null || o == "" || "null".equals(o) || "".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

}
