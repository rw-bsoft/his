package phis.application.yb.example.HZ.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class GSMedicareUtil {
	private static final Object XNFPXLOBJ = new Object();

	/**
	 * 医保二乙标志和因公因伤标志病人药品和费用的自负比例获取
	 * 
	 * @param dao
	 *            数据库操作对象
	 * @param par
	 *            传入参数 ai_yplx 药品或者费用的类型(药品：yk_typk的type，费用：0) al_ypxh
	 *            药品序号或者费用序号 al_ypcd 药品的药品产地(费用时是空或者0)
	 * 
	 * @return 0 将0保存到处方或者医嘱表 -1 直接返回 -1000 界面需要弹出提示框(三种选择 是：3 ;否: 0 ;取消 : -1)
	 * @throws ModelDataOperationException
	 */
	public static int gf_yb_getscjr(BaseDAO dao, Map<String, Object> par)
			throws ModelDataOperationException {
		int ai_yplx = Integer.parseInt(par.get("ai_yplx") + "");// 药品或者费用的类型(药品：yk_typk的type，费用：0)
		long al_ypxh = Long.parseLong(par.get("al_ypxh") + "");// 药品序号或者费用序号
		long al_ypcd = Long.parseLong(par.get("al_ypcd") + "");// 药品的药品产地(费用时是空或者0)
		StringBuilder sqlBuilder = new StringBuilder();
		Map<String, Object> parameter = new HashMap<String, Object>();
		String ltd_date = BSHISUtil.getDate();// 当前时间
		Map<String, Object> resultMap = null;
		try {
			parameter.put("al_ypxh", al_ypxh);
			parameter.put("ldt_date", ltd_date);
			if (ai_yplx > 0) {// 药品类型大于0
				if (al_ypcd > 0) {// 药品产地大于0
					parameter.put("al_ypcd", al_ypcd);
					sqlBuilder
							.append("SELECT YBBM as YBBM FROM YB_YPDZ WHERE YPXH = :al_ypxh and YPCD = :al_ypcd AND YBPB = 1 AND");
					sqlBuilder.append(BSPHISUtil.toChar("KSSJ", "YYYY-MM-DD"));
					sqlBuilder.append(" <=:ldt_date and (");
					sqlBuilder.append(BSPHISUtil.toChar("ZZSJ", "YYYY-MM-DD"));
					sqlBuilder.append(" >=:ldt_date or ZZSJ is null)");
					resultMap = dao.doLoad(sqlBuilder.toString(), parameter);
				}
			} else {
				sqlBuilder
						.append("SELECT YBBM as YBBM FROM YB_FYDZ WHERE FYXH = :al_ypxh AND YBPB = 1 AND ");
				sqlBuilder.append(BSPHISUtil.toChar("KSSJ", "YYYY-MM-DD"));
				sqlBuilder.append("<=:ldt_date and (");
				sqlBuilder.append(BSPHISUtil.toChar("ZZSJ", "YYYY-MM-DD"));
				sqlBuilder.append(" >=:ldt_date or ZZSJ is null)");
				resultMap = dao.doLoad(sqlBuilder.toString(), parameter);
			}
			// 医保编码为空时，直接返回-1
			if (resultMap == null || resultMap.get("YBBM") == null
					|| "".equals(String.valueOf(resultMap.get("YBBM")))) {
				return -1;
			}
			sqlBuilder = new StringBuilder();
			parameter.clear();
			parameter.put("ls_ybbm", String.valueOf(resultMap.get("YBBM")));
			parameter.put("ldt_date", ltd_date);
			sqlBuilder
					.append("SELECT COUNT(*) as LI_COUNT FROM KA57 WHERE AKA231=:ls_ybbm AND ");
			sqlBuilder.append(BSPHISUtil.toChar("AKA234", "YYYY-MM-DD"));
			sqlBuilder.append(" <= :ldt_date");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					sqlBuilder.toString(), parameter);
			int li_count = Integer.parseInt(coun.get(0).get("LI_COUNT")
					.toString());
			if (li_count > 0) {
				return -1000;
			} else {
				return 1;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"医保二乙标志和因公因伤标志病人药品和费用的自负比例获取失败!", e);
		}
	}

	/**
	 * 根据私有参数BZLBFFHJGH获取员工代码
	 * 
	 * @param jgid
	 * @param ctx
	 * @param dao
	 * 
	 * @return 员工代码
	 * @throws ModelDataOperationException
	 */
	public static String getYGDM(String jgid, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		try {
			String ygbh = ParameterUtil.getParameter(jgid,
					"BZLBFFHJGH", ctx);
			String cnd = "['and',['eq',['$', 'YGBH'], ['s', '" + ygbh
					+ "']],['eq',['$', 'JGID'],['s', '" + jgid + "']]]";
			Map<String, Object> map = dao.doLoad(CNDHelper.toListCnd(cnd),
					"GY_YGDM");
			if (map == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR,
						"根据私有参数BZLBFFHJGH 未获取到对应的员工代码!");
			} else {
				String ygdm = String.valueOf(map.get("YGDM"));
				return ygdm;
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"根据私有参数BZLBFFHJGH 未获取到对应的员工代码失败" + e.getMessage());
		}

	}

	/**
	 * 获取虚拟发票序列(有同步操作)
	 * 
	 * @param jgid
	 * @param dao
	 * @return
	 * @throws ModelDataOperationException
	 */
	public static String getXNFPXL(String jgid, Context ctx, BaseDAO dao)
			throws ModelDataOperationException {
		try {
			synchronized (XNFPXLOBJ) {// 同步
				// long xnfpxl = Long.parseLong(ParameterUtil.getParameter(jgid,
				// BSPHISSystemArgument.XNFPXL, ctx));
				long xnfpxl = 1;
				String cnd = "['and',['eq',['$', 'CSMC'], ['s', 'XNFPXL']],['eq',['$', 'JGID'],['s', '"
						+ jgid + "']]]";
				Map<String, Object> result = dao.doLoad(
						CNDHelper.toListCnd(cnd), BSPHISEntryNames.GY_XTCS);
				if (result != null) {
					xnfpxl = Long.parseLong(String.valueOf(result.get("CSZ")));
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("CSZ", String.valueOf(xnfpxl + 1));
					map.put("JGID", jgid);
					StringBuilder sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("UPDATE ")
							.append(BSPHISEntryNames.GY_XTCS)
							.append(" SET CSZ = :CSZ WHERE CSMC = 'XNFPXL' AND JGID = :JGID");
					dao.doSqlUpdate(sqlBuilder.toString(), map);
				} else {
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("CSZ", String.valueOf(xnfpxl));
					parameter.put("JGID", jgid);
					parameter.put("CSMC", "XNFPXL");
					parameter.put("BZ", "虚拟发票序列");
					dao.doSave("create", BSPHISEntryNames.GY_XTCS, parameter,
							true);
				}
				return "MS" + xnfpxl;
			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取虚拟发票序列失败"
							+ e.getMessage());
		}

	}

	public static Map<String, Object> wf_CheckArchives(BaseDAO dao,
			String as_sfzh, String as_mzhm) throws ModelDataOperationException {
		String ll_BRID = null;
		String ls_MZHM = null;
		String sign = null;
		Map<String, Object> parametersbrxx = new HashMap<String, Object>();
		parametersbrxx.put("ls_SFZH", as_sfzh);
		Map<String, Object> ret = new HashMap<String, Object>();
		String mzxh = "";
		try {
			Map<String, Object> brxx = dao.doLoad("SELECT EMPIID as EMPIID,BRID as BRID, MZHM as MZHM, LXDZ as HKDZ," +
					" JTDH as JTDH,DWMC as DWMC,CSNY as CSNY,ZYDM as ZYDM From MS_BRDA Where SFZH = :ls_SFZH",
					parametersbrxx);
			if (brxx != null) {
				ll_BRID = brxx.get("BRID") + "";
				ls_MZHM = brxx.get("MZHM") + "";
				as_mzhm = ls_MZHM;
				mzxh = brxx.get("MZHM") + "";
			}
			if (ll_BRID != null && ll_BRID != "") {
				sign=brxx.get("EMPIID") + "";
			} else {
				sign="-1";
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "建档失败:" + e.getMessage());
		}
		ret.put("MZHM", mzxh);
		ret.put("Ll_return", sign);
		return ret;
	}

	public static boolean wf_savearchives(BaseDAO dao, String as_MZHM,
			String al_BRXZ, String as_SFZH, String as_BRXM, String al_BRXB,
			Date adt_csny, String as_dwmc, String as_YBKH, String jgid,
			String uid) throws ModelDataOperationException {
		Map<String, Object> brxxMap = new HashMap<String, Object>();
		brxxMap.put("MZHM", as_MZHM);
		brxxMap.put("BRXZ", al_BRXZ);
		brxxMap.put("BRXM", as_BRXM);
		brxxMap.put("BRXB", al_BRXB);
		brxxMap.put("SFZH", as_SFZH);
		brxxMap.put("YBKH", as_YBKH);
		brxxMap.put("CSNY", adt_csny);
		brxxMap.put("DWMC", as_dwmc);
		brxxMap.put("JDJG", jgid);
		brxxMap.put("JDSJ", new Date());
		brxxMap.put("XGSJ", new Date());
		brxxMap.put("JDR", uid);
		brxxMap.put("ZXBZ", 0);
		try {
			dao.doSave("create", "MS_BRDA", brxxMap, false);
			return true;
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "建档失败:" + e.getMessage());
			// return false;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "建档失败:" + e.getMessage());
			// return false;
		}
	}

	public static Map<String, Object> doQueryBrxx(BaseDAO dao,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		String ls_mzhm = null;
		String Ls_sfzh = req.get("GRSFH") + "";
		Map<String, Object> ret;
		try {
			ret = wf_CheckArchives(dao, Ls_sfzh, ls_mzhm);
		} catch (ModelDataOperationException e) {
			throw new ModelDataOperationException("查询失败!", e);
		}
		return ret;
	}

	public static Map<String, Double> doQueryDzxx(BaseDAO dao,
			Map<String, Object> cforyjxx, Context ctx, int sign)
			throws ModelDataOperationException {
		Map<String, Double> jeMap = new HashMap<String, Double>();
		Map<String, Object> ypdzpar = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user=UserRoleToken.getCurrent();
		String manaUnitId =user.getManageUnitId();// 用户的机构ID
		String ls_yydj = ParameterUtil.getParameter(manaUnitId,
				"SYB_YYDJ", ctx);
		String ls_zfyp = ParameterUtil.getParameter(manaUnitId,
				"SYB_ZFYP", ctx);
		String ls_kbzy = ParameterUtil.getParameter(manaUnitId,
				"SYB_KBZY", ctx);
		String ls_ypbm = null;
		String ls_zwmc = null;
		String ls_sflb = null;
		String ls_ypdj = null;
		long ll_zfpb = 0L;
		Map<String, Double> ld_zfbl = new HashMap<String, Double>();
		Map<String, Double> ld_fyxe = new HashMap<String, Double>();
		int li_bjbz = 0;
		long ll_ypxh = 0L;
		long ll_ypcd = 0L;
		double ll_ypsl = 0;
		// String Ls_ggdw = null;

		if (cforyjxx.containsKey("YPXH")) {
			ll_ypxh = parseLong(cforyjxx.get("YPXH") + "");
		} else if (cforyjxx.containsKey("FYXH")) {
			ll_ypxh = parseLong(cforyjxx.get("FYXH") + "");
		}
		if (cforyjxx.get("YPCD") != null && cforyjxx.get("YPCD") != "") {
			ll_ypcd = Long.parseLong(cforyjxx.get("YPCD") + "");
		}
		if (cforyjxx.get("ZFPB") != null) {
			ll_zfpb = Long.parseLong(cforyjxx.get("ZFPB") + "");
		}
		String Ls_bllx = cforyjxx.get("BLLX") + "";
		if (cforyjxx.containsKey("YPSL")) {
			ll_ypsl = parseDouble(cforyjxx.get("YPSL"))
					* parseDouble(cforyjxx.get("CFTS"));
		} else if (cforyjxx.containsKey("FYSL")) {
			if (cforyjxx.get("CFTS") != null) {
				ll_ypsl = parseDouble(cforyjxx.get("FYSL"))
						* parseDouble(cforyjxx.get("CFTS"));
			} else {
				ll_ypsl = parseDouble(cforyjxx.get("FYSL"));
			}
		}
		// Ls_ggdw = cforyjxx.get(i).get("YFGG") + "/"
		// + cforyjxx.get(i).get("YFDW");
		if (ll_zfpb == 1 && "16".equals(Ls_bllx)) {
			ls_ypbm = ls_zfyp;
		}
		try {
			Date ldt_fyrq = sdf.parse(BSHISUtil.getDateTime());
			ypdzpar.put("ll_ypxh", ll_ypxh);
			ypdzpar.put("ll_ypcd", ll_ypcd);
			ypdzpar.put("ldt_fyrq", ldt_fyrq);
			if (sign == 1) {
				Map<String, Object> ypdzxxMap = dao
						.doLoad("SELECT YBBM as YBBM,ZWMC as ZWMC,SFLB as SFLB,YPDJ as YPDJ,BJBZ as BJBZ FROM YB_YPDZ Where YBPB = 1 and YPXH=:ll_ypxh AND YPCD=:ll_ypcd AND KSSJ<=:ldt_fyrq and (ZZSJ>=:ldt_fyrq or ZZSJ is null)",
								ypdzpar);
				if (ypdzxxMap != null) {
					if (ypdzxxMap.get("YBBM") != null) {
						ls_ypbm = ypdzxxMap.get("YBBM") + "";
					}
					if (ypdzxxMap.get("ZWMC") != null) {
						ls_zwmc = ypdzxxMap.get("ZWMC") + "";
					}
					if (ypdzxxMap.get("SFLB") != null) {
						ls_sflb = ypdzxxMap.get("SFLB") + "";
					}
					if (cforyjxx.containsKey("YPDJ")) {
						if (cforyjxx.get("YPDJ") != null) {
							ls_ypdj = cforyjxx.get("YPDJ") + "";
						}
					} else if (cforyjxx.containsKey("FYDJ")) {
						if (cforyjxx.get("FYDJ") != null) {
							ls_ypdj = cforyjxx.get("FYDJ") + "";
						}
					}
					if (ypdzxxMap.get("BJBZ") != null) {
						li_bjbz = Integer.parseInt(ypdzxxMap.get("BJBZ") + "");
					}
					int li_return = gf_yb_getzfbl(dao, ls_ypbm, 1, ll_zfpb,
							Ls_bllx, ld_zfbl, ls_yydj, ld_fyxe, ctx);
					if (ld_zfbl.containsKey("ZFBL")) {
						cforyjxx.put("ZFBL", parseDouble(ld_zfbl.get("ZFBL")));
					} else {
						throw new ModelDataOperationException("取[" + ls_zwmc
								+ "]自负比例出错,该项目可能对照有误!");
					}
					if (li_return == -1) {
						if ("y".equals(ls_ypbm.substring(0, 1))
								&& (ld_zfbl == null || ld_zfbl.size() == 0)) {
							ls_ypbm = ls_kbzy;
							ld_zfbl.put("ZFBL", 0.00);
						} else {
							throw new ModelDataOperationException("取["
									+ ls_zwmc + "]自负比例出错,该项目可能对照有误!");
						}
					} else {
						if ("y".equals(ls_ypbm.substring(0, 1))
								&& parseDouble(ld_zfbl.get("ZFBL")) == 1) {
							if (!"16".equals(Ls_bllx)) {
								ls_ypbm = ls_zfyp;
							}
						}
					}
					double ld_zjje = parseDouble(ls_ypdj) * ll_ypsl;
					double ld_zfje = parseDouble(ld_zfbl.get("ZFBL")) * ld_zjje;
					double ld_grzl = 0.00;
					double ld_grzf = 0.00;
					int ll_zfbz = 0;
					if (parseDouble(ld_zfbl.get("ZFBL")) == 1) {
						ll_zfbz = 1;
					}
					if (ll_zfpb == 1) {
						ls_ypbm = ls_zfyp;
						ld_grzf = ld_zjje;
					} else {
						if (ll_zfbz == 1) {
							if ("x23".equals(ls_ypbm.substring(2, 3))
									&& "g".equals(ls_ypbm.substring(ls_ypbm
											.length() - 1))) {
							} else {
								ls_ypbm = ls_zfyp;
							}
							ld_grzf = ld_zjje;
						} else {
							if (parseDouble(ld_fyxe.get("FYXE")) > 0
									&& parseInt(ls_ypdj) > parseDouble(ld_fyxe
											.get("FYXE"))) { // 有限额
								// 个人自费金额
								ld_grzf = (parseInt(ls_ypdj) - parseDouble(ld_fyxe
										.get("FYXE"))) * ll_ypsl;
								// 自理金额
								ld_grzl = parseDouble(ld_fyxe.get("FYXE"))
										* parseDouble(ld_zfbl.get("ZFBL"))
										* ll_ypsl;
							} else {
								// 自理金额
								ld_grzl = ld_zfje;
							}
						}
					}
					jeMap.put("ld_zjje", BSPHISUtil.getDouble(ld_zjje, 2));
					jeMap.put("ld_zfje", BSPHISUtil.getDouble(ld_zfje, 2));
					jeMap.put("ld_grzl", BSPHISUtil.getDouble(ld_grzl, 2));
					jeMap.put("ld_grzf", BSPHISUtil.getDouble(ld_grzf, 2));
				}
			} else if (sign == 2) {
				ypdzpar.remove("ll_ypcd");
				Map<String, Object> fydzxxMap = dao
						.doLoad("SELECT YBBM as YBBM,XMMC as ZWMC,SFLB as SFLB,FYDJ as YPDJ,BJBZ as BJBZ FROM YB_FYDZ Where YBPB = 1 and FYXH=:ll_ypxh AND  KSSJ<=:ldt_fyrq and (ZZSJ>=:ldt_fyrq or ZZSJ is null)",
								ypdzpar);
				if (fydzxxMap != null) {
					if (fydzxxMap.get("YBBM") != null) {
						ls_ypbm = fydzxxMap.get("YBBM") + "";
					}
					if (fydzxxMap.get("ZWMC") != null) {
						ls_zwmc = fydzxxMap.get("ZWMC") + "";
					}
					if (fydzxxMap.get("SFLB") != null) {
						ls_sflb = fydzxxMap.get("SFLB") + "";
					}
					if (cforyjxx.containsKey("YPDJ")) {
						if (cforyjxx.get("YPDJ") != null) {
							ls_ypdj = cforyjxx.get("YPDJ") + "";
						}
					} else if (cforyjxx.containsKey("FYDJ")) {
						if (cforyjxx.get("FYDJ") != null) {
							ls_ypdj = cforyjxx.get("FYDJ") + "";
						}
					}
					if (fydzxxMap.get("BJBZ") != null) {
						li_bjbz = Integer.parseInt(fydzxxMap.get("BJBZ") + "");
					}
					int li_return = gf_yb_getzfbl(dao, ls_ypbm, 2, ll_zfpb,
							Ls_bllx, ld_zfbl, ls_yydj, ld_fyxe, ctx);
					if (ld_zfbl.containsKey("ZFBL")) {
						cforyjxx.put("ZFBL", parseDouble(ld_zfbl.get("ZFBL")));
					} else {
						throw new ModelDataOperationException("取[" + ls_zwmc
								+ "]自负比例出错,该项目可能对照有误!");
					}
					if (li_return == -1) {
						if ("y".equals(ls_ypbm.substring(1, 2))
								&& parseDouble(ld_zfbl.get("ZFBL")) == 0.00) {
							ls_ypbm = ls_kbzy;
						} else {
							throw new ModelDataOperationException("取["
									+ ls_zwmc + "]自负比例出错,该项目可能对照有误!");
						}
					} else {
						if ("y".equals(ls_ypbm.substring(1, 2))
								&& parseDouble(ld_zfbl.get("ZFBL")) == 1) {
							if (!"16".equals(Ls_bllx)) {
								ls_ypbm = ls_zfyp;
							}
						}
					}
					double ld_zjje = parseDouble(ls_ypdj) * ll_ypsl;
					double ld_zfje = parseDouble(ld_zfbl.get("ZFBL")) * ld_zjje;
					double ld_grzl = 0.00;
					double ld_grzf = 0.00;
					int ll_zfbz = 0;
					if (parseDouble(ld_zfbl.get("ZFBL")) == 1) {
						ll_zfbz = 1;
					}
					if (ll_zfpb == 1) {
						ls_ypbm = ls_zfyp;
						ld_grzf = ld_zjje;
					} else {
						if (ll_zfbz == 1) {
							if ("x23".equals(ls_ypbm.substring(3, 4))
									&& "g".equals(ls_ypbm.substring(ls_ypbm
											.length() - 1))) {
							} else {
								ls_ypbm = ls_zfyp;
							}
							ld_grzf = ld_zjje;
						} else {
							if (parseDouble(ld_fyxe.get("FYXE")) > 0
									&& parseInt(ls_ypdj) > parseDouble(ld_fyxe
											.get("FYXE"))) { // 有限额
								// 个人自费金额
								ld_grzf = (parseInt(ls_ypdj) - parseDouble(ld_fyxe
										.get("FYXE"))) * ll_ypsl;
								// 自理金额
								ld_grzl = parseDouble(ld_fyxe.get("FYXE"))
										* parseDouble(ld_zfbl.get("ZFBL"))
										* ll_ypsl;
							} else {
								// 自理金额
								ld_grzl = ld_zfje;
							}
						}
					}
					jeMap.put("ld_zjje", BSPHISUtil.getDouble(ld_zjje, 2));
					jeMap.put("ld_zfje", BSPHISUtil.getDouble(ld_zfje, 2));
					jeMap.put("ld_grzl", BSPHISUtil.getDouble(ld_grzl, 2));
					jeMap.put("ld_grzf", BSPHISUtil.getDouble(ld_grzf, 2));
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自理自费计算失败:"
							+ e.getMessage());
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自理自费计算失败:"
							+ e.getMessage());
		}

		return jeMap;
	}

	public static int gf_yb_getzfbl(BaseDAO dao, String ls_ypbm, long ll_xmlx,
			long ll_zfpb, String Ls_bllx, Map<String, Double> ad_zfbl,
			String ls_yydj, Map<String, Double> ld_fyxe, Context ctx)
			throws ModelDataOperationException {
		int li_return = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date adt_date = sdf.parse(BSHISUtil.getDateTime());
			if (ll_zfpb == 3) {
				double ld_sjxj = 0.00;
				double ld_syxj = 0.00;
				double ld_ejxj = 0.00;
				double ld_yjxj = 0.00;
				Map<String, Object> ka57par = new HashMap<String, Object>();
				ka57par.put("as_ybbm", ls_ypbm);
				ka57par.put("as_bllx", Ls_bllx);
				ka57par.put("as_xmlx", ll_xmlx + "");
				ka57par.put("adt_date", adt_date);
				Map<String, Object> ka57map = dao
						.doLoad("SELECT AKA232 as AKA232,AKA068 as AKA068,ZKA101 as ZKA101,ZKA102 as ZKA102,ZKA112 as ZKA112 FROM KA57 where AKA231 = :as_ybbm AND  AKC021 = :as_bllx AND  AKA230 = :as_xmlx AND  AKA234 <= :adt_date And (AKA235 >= :adt_date OR AKA235 IS Null)",
								ka57par);
				if (ka57map != null) {
					if (ka57map.get("AKA232") != null) {
						ad_zfbl.put("ZFBL",
								Double.parseDouble(ka57map.get("AKA232") + ""));
					}
					if (ka57map.get("AKA068") != null) {
						ld_sjxj = Double
								.parseDouble(ka57map.get("AKA068") + "");
					}
					if (ka57map.get("ZKA101") != null) {
						ld_syxj = Double
								.parseDouble(ka57map.get("ZKA101") + "");
					}
					if (ka57map.get("ZKA102") != null) {
						ld_ejxj = Double
								.parseDouble(ka57map.get("ZKA102") + "");
					}
					if (ka57map.get("ZKA112") != null) {
						ld_yjxj = Double
								.parseDouble(ka57map.get("ZKA112") + "");
					}
					if (ad_zfbl == null || ad_zfbl.size() == 0) {
						if (parseLong(ls_ypbm) < parseLong("9000000000000009")) {
							ad_zfbl.put("ZFBL", 1.00);
						} else {
							li_return = -1;
						}
					}
					if (ll_xmlx == 2) {
						if ("31".equals(ls_yydj)) {
							ld_fyxe.put("FYXE", ld_sjxj);
						} else if ("32".equals(ls_yydj)) {
							ld_fyxe.put("FYXE", ld_syxj);
						} else if ("20".equals(ls_yydj)) {
							ld_fyxe.put("FYXE", ld_ejxj);
						} else if ("10".equals(ls_yydj)) {
							ld_fyxe.put("FYXE", ld_yjxj);
						}
					}
				}
			} else {
				if (ll_xmlx == 2) {
					if (!"8".equals(Ls_bllx) && !"10".equals(Ls_bllx)
							&& !"12".equals(Ls_bllx)) {
						Map<String, Object> ka03Par = new HashMap<String, Object>();
						ka03Par.put("as_ybbm", ls_ypbm);
						String ls_znbz = null;
						Map<String, Object> ka03Map = dao
								.doLoad("SELECT ZKA120 as ZKA120 FROM KA03 Where AKA090 = :as_ybbm",
										ka03Par);
						if (ka03Map != null) {
							ls_znbz = ka03Map.get("ZKA120") + "";
							if ("1".equals(ls_znbz)) {
								ad_zfbl.put("ZFBL", 1.00);
							}
							li_return = 0;
						}
					}
				}
				// double ld_sjxj = 0.00;
				// double ld_syxj = 0.00;
				// double ld_ejxj = 0.00;
				// double ld_yjxj = 0.00;
				Map<String, Object> ka54par = new HashMap<String, Object>();
				ka54par.put("as_ybbm", ls_ypbm);
				ka54par.put("as_bllx", Ls_bllx);
				ka54par.put("as_xmlx", ll_xmlx + "");
				ka54par.put("adt_date", adt_date);
				Map<String, Object> ka54map = dao
						.doLoad("SELECT AKA232 as AKA232,AKA068 as AKA068,ZKA101 as ZKA101,ZKA102 as ZKA102,ZKA112 as ZKA112 FROM KA54 where AKA231 = :as_ybbm AND  AKC021 = :as_bllx AND  AKA230 = :as_xmlx AND  AKA234 <= :adt_date And (AKA235 >= :adt_date OR AKA235 IS Null)",
								ka54par);
				if (ka54map != null) {
					if (ka54map.get("AKA232") != null) {
						ad_zfbl.put("ZFBL",
								Double.parseDouble(ka54map.get("AKA232") + ""));
					}
					// if (ka54map.get("AKA068") != null) {
					// ld_sjxj = Double.parseDouble(ka54map.get("AKA068")
					// + "");
					// }
					// if (ka54map.get("ZKA101") != null) {
					// ld_syxj = Double.parseDouble(ka54map.get("ZKA101")
					// + "");
					// }
					// if (ka54map.get("ZKA102") != null) {
					// ld_ejxj = Double.parseDouble(ka54map.get("ZKA102")
					// + "");
					// }
					// if (ka54map.get("ZKA112") != null) {
					// ld_yjxj = Double.parseDouble(ka54map.get("ZKA112")
					// + "");
					// }
				}
				if (ll_xmlx == 2) {
					if (ad_zfbl == null || ad_zfbl.size() == 0) {
						if (parseLong(ls_ypbm) < parseLong("9000000000000009")) {
							ad_zfbl.put("ZFBL", 1.00);
						} else {
							li_return = -1;
						}
					}
				}
			}
			if (ld_fyxe == null || ld_fyxe.size() == 0) {
				ld_fyxe.put("FYXE", 0.00);
			}
			li_return = 0;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自理自费计算失败:"
							+ e.getMessage());
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自理自费计算失败:"
							+ e.getMessage());
		}
		return li_return;
	}

	public static double getSzybZfbl(Map<String, Object> par, String ybbm,
			String yybm, String zdbm, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"qzjSessionFactory");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Session ss = sf.openSession();
		BaseDAO emrDao = new BaseDAO(ctx, ss);
		String sLbbz = par.get("YPLX") + "";// 项目类别(我们程序取的)
		String sDylb = par.get("DYLB") + ""; // 待遇类别(读卡读出来的 病人信息)
		String sYydj = "1"; // 医院等级 （已作废）
		String sJzlx = par.get("JZLX") + ""; // 就诊类型(10,门诊20,住院)
		String sDfff = par.get("DFFBZ") + ""; // 单复方标志 0,1(如果草药开了一个 是0,否则是1)
		String sysj = sdf.format(new Date());// 当前时间
		double zfbl = 1;
		if (zdbm == null || "null".equals(zdbm)) {
			zdbm = "50464";
		}
		try {
			List<Map<String, Object>> blXX = emrDao.doSqlQuery(
					"select OraFGetZfbl_new('" + yybm + "','" + sLbbz + "','"
							+ ybbm + "','" + sDylb + "','" + sYydj + "','"
							+ sJzlx + "','" + sDfff + "','" + zdbm + "','"
							+ sysj + "') as ZFBL from dual", parameters);
			if (blXX.size() > 0) {
				zfbl = parseDouble(blXX.get(0).get("ZFBL"));
				if (zfbl == -10) {
					throw new ModelDataOperationException("医院对照不合法!");
				} else if (zfbl == -11) {
					throw new ModelDataOperationException("项目类型不合法!");
				} else if (zfbl == -12) {
					if (par.containsKey("YPMC")) {
						throw new ModelDataOperationException("【"
								+ par.get("YPMC") + "】医保编码不合法!");
					}
				} else if (zfbl == -13) {
					throw new ModelDataOperationException("待遇类别不合法!");
				} else if (zfbl == -14) {
					throw new ModelDataOperationException("医院等级不合法!");
				} else if (zfbl == -15) {
					throw new ModelDataOperationException("就诊类型不合法!");
				} else if (zfbl == -16) {
					throw new ModelDataOperationException("单复方不合法!");
				} else if (zfbl == -17) {
					throw new ModelDataOperationException("疾病编码不合法!");
				} else if (zfbl == -22) {
					if (par.containsKey("YPMC")) {
						throw new ModelDataOperationException("【"
								+ par.get("YPMC") + "】没有对照或者对照错误!");
					}
				} else if(zfbl<0){
					throw new ModelDataOperationException("取自负比例出错!");
				}
			} else {
				throw new ModelDataOperationException("取自负比例出错!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("取自负比例出错!");
		} finally {
			ss.close();
		}
		return zfbl;
	}

	public static double getSzybDjxe(Map<String, Object> par, String ybbm,
			String yybm, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"qzjSessionFactory");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Session ss = sf.openSession();
		BaseDAO emrDao = new BaseDAO(ctx, ss);
		String sJzlx = par.get("JZLX") + ""; // 就诊类型
		String sDylb = par.get("DYLB") + ""; // 待遇类别
		String sLbbz = par.get("YPLX") + ""; // 项目类别
		String sysj = sdf.format(new Date()); // 使用时间
		double djxe = 0.00;
		// Map<String, Object> parYYBM = new HashMap<String, Object>();
		// parYYBM.put("JGID", manaUnitId);
		// try {
		// Map<String, Object> yybmMap = dao
		// .doLoad("select YYBM as YYBM from YB_YYXX where JGID=:JGID and SZYB=2",
		// parYYBM);
		// if (yybmMap != null && yybmMap.size() > 0) {
		// if (yybmMap.get("YYBM") != null) {
		// yybm = yybmMap.get("YYBM") + "";
		// }
		// }
		// } catch (PersistentDataOperationException e1) {
		// e1.printStackTrace();
		// }
		try {
			List<Map<String, Object>> djxeXX = emrDao.doSqlQuery(
					"select OraFGetDjxe_NEW('" + yybm + "','" + sJzlx + "','"
							+ sDylb + "','" + sLbbz + "','" + ybbm + "','"
							+ sysj + "') as DJXE from dual", parameters);
			if (djxeXX.size() > 0) {
				djxe = parseDouble(djxeXX.get(0).get("DJXE"));
				if (djxe < 0) {
					throw new ModelDataOperationException("取单价限额出错djxe < 0!");
				}
			} else {
				throw new ModelDataOperationException("取单价限额出错!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("取单价限额出错!错误信息:"+e.getMessage());
		} finally {
			ss.close();
		}
		return djxe;
	}

	public static Map<String, Object> getSzybFyxx(Map<String, Object> par,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> fyxxMap = new HashMap<String, Object>();
		double zjje = 0.00;
		double zfje = 0.00;
		double zlje = 0.00;
		Map<String, Object> ybxx = getYBXX(par, dao, ctx);
		String ybbm = ybxx.get("YBBM") + "";
		String yybm = ybxx.get("YYBM") + "";
		String zdbm = ybxx.get("JBBM") + "";
		String zdmc = ybxx.get("ZDMC") + "";
		double ypdj = parseDouble(par.get("YPDJ"));
		double ypsl = parseDouble(par.get("YPSL"));
		int cfts = parseInt(par.get("CFTS"));
		double djxe = getSzybDjxe(par, ybbm, yybm, dao, ctx);
		double zfbl = getSzybZfbl(par, ybbm, yybm, zdbm, dao, ctx);
		if (par.containsKey("JLXH")) {
			if ("20".equals(par.get("JZLX") + "")) {
				Map<String, Object> updzfblpar = new HashMap<String, Object>();
				updzfblpar.put("ZFBL", zfbl);
				updzfblpar.put("JLXH", parseLong(par.get("JLXH")));
				try {
					dao.doUpdate("update ZY_FYMX set ZFBL=:ZFBL where JLXH=:JLXH",
							updzfblpar);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "更新住院费用明细表ZY_FYMX失败:ZFBL="+zfbl+",JLXH="+par.get("JLXH")
									+ e.getMessage());
				}
			}
		}
		fyxxMap.put("ZFBL", zfbl);
		zjje = ypdj * ypsl * cfts;

		if (zfbl == 1) {
			zfje = zjje;
		} else {
			if (djxe > 0) {
				if (ypdj > djxe) {
					zfje = (ypdj - djxe) * ypsl * cfts;// 自费金额
					double syje = zjje - zfje;// 剩余金额
					zlje = syje * zfbl;// 自理金额
				} else {
					zlje = zjje * zfbl;// 自理金额
				}
			} else {
				zlje = zjje * zfbl;// 自理金额
			}
		}
		fyxxMap.put("ZJJE", BSPHISUtil.getDouble(zjje, 2));
		fyxxMap.put("ZFJE", BSPHISUtil.getDouble(zfje, 2));
		fyxxMap.put("ZLJE", BSPHISUtil.getDouble(zlje, 2));
		fyxxMap.put("YBBM", ybbm);
		fyxxMap.put("ZDBM", zdbm);
		fyxxMap.put("ZDMC", zdmc);
		return fyxxMap;
	}

	public static Map<String, Object> getYBXX(Map<String, Object> par,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		Map<String, Object> ybxxpar = new HashMap<String, Object>();
		UserRoleToken user=UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date ldt_fyrq;
			ldt_fyrq = sdf.parse(BSHISUtil.getDateTime());
			Map<String, Object> ypdzpar = new HashMap<String, Object>();
			long ll_ypxh = parseLong(par.get("YPXH"));

			int ll_yplx = parseInt(par.get("YPLX"));
			ypdzpar.put("ll_ypxh", ll_ypxh);
			ypdzpar.put("ldt_fyrq", ldt_fyrq);
			if (ll_yplx == 1) {
				long ll_ypcd = parseLong(par.get("YPCD"));
				ypdzpar.put("ll_ypcd", ll_ypcd);
				Map<String, Object> ypdzxxMap = dao
						.doLoad("SELECT YBBM as YBBM,SPLX as SPLX FROM YB_YPDZ Where YBPB = 2 and YPXH=:ll_ypxh AND YPCD=:ll_ypcd AND KSSJ<=:ldt_fyrq and (ZZSJ>=:ldt_fyrq or ZZSJ is null)",
								ypdzpar);
				if (ypdzxxMap != null) {
					ybxxpar.put("YBBM", ypdzxxMap.get("YBBM") + "");
					ybxxpar.put("SPLX", ypdzxxMap.get("SPLX") + "");
				}else{
					throw new ModelDataOperationException("【"
							+ par.get("YPMC") + "】药品未对照,请先对照!");
				}
			} else if (ll_yplx == 2) {
				// ypdzpar.remove("ll_ypcd");
				Map<String, Object> fydzxxMap = dao
						.doLoad("SELECT YBBM as YBBM,SPLX as SPLX FROM YB_FYDZ Where YBPB = 2 and FYXH=:ll_ypxh AND  KSSJ<=:ldt_fyrq and (ZZSJ>=:ldt_fyrq or ZZSJ is null)",
								ypdzpar);
				if (fydzxxMap != null) {
					ybxxpar.put("YBBM", fydzxxMap.get("YBBM") + "");
					ybxxpar.put("SPLX", fydzxxMap.get("SPLX") + "");
				}else{
					throw new ModelDataOperationException("【"
							+ par.get("YPMC") + "】费用未对照,请先对照!");
				}
			}
			// 查询医院编码
			Map<String, Object> yybmpar = new HashMap<String, Object>();
			yybmpar.put("JGID", manaUnitId);
			Map<String, Object> yybmMap = dao
					.doLoad("SELECT YYBM as YYBM FROM YB_YYXX Where JGID =:JGID and SZYB=2",
							yybmpar);
			if (yybmMap != null) {
				ybxxpar.put("YYBM", yybmMap.get("YYBM") + "");
			}
			// 查询疾病编码
			Map<String, Object> jbbmpar = new HashMap<String, Object>();
			if ("10".equals(par.get("JZLX") + "")) {
				jbbmpar.put("CFSB", parseLong(par.get("CFSB")));
				if (parseLong(par.get("CFSB")) > 0) {
					if (par.get("ZDBM") != null) {
						ybxxpar.put("JBBM", par.get("ZDBM") + "");
						ybxxpar.put("ZDMC", par.get("ZDMC") + "");
					} else {
						if (ll_yplx == 1) {
							Map<String, Object> jbbmMap = dao
									.doLoad("select c.YBBM as YBBM,b.ZDMC as ZDMC from MS_CF01 a,MS_BRZD b,GY_JBBM c where a.CFSB=:CFSB and a.JZXH=b.JZXH and b.ZDXH=c.JBXH and b.ZZBZ=1",
											jbbmpar);
							if (jbbmMap != null) {
								ybxxpar.put("JBBM", jbbmMap.get("YBBM") + "");
								ybxxpar.put("ZDMC", jbbmMap.get("ZDMC") + "");
							}
						} else if (ll_yplx == 2) {
							Map<String, Object> jbbmMap = dao
									.doLoad("select c.YBBM as YBBM,b.ZDMC as ZDMC from MS_YJ01 a,MS_BRZD b,GY_JBBM c where a.YJXH=:CFSB and a.JZXH=b.JZXH and b.ZDXH=c.JBXH and b.ZZBZ=1",
											jbbmpar);
							if (jbbmMap != null) {
								ybxxpar.put("JBBM", jbbmMap.get("YBBM") + "");
								ybxxpar.put("ZDMC", jbbmMap.get("ZDMC") + "");
							}
						}
					}
				}
			} else if ("20".equals(par.get("JZLX") + "")) {
				jbbmpar.put("CFSB", parseLong(par.get("ZYH")));
				Map<String, Object> jbbmMap = dao
						.doLoad("select b.YBBM as YBBM,b.JBMC as JBMC from ZY_RYZD a,GY_JBBM b where a.ZDXH=b.JBXH and a.ZYH=:CFSB and a.ZDLB=2",
								jbbmpar);
				if (jbbmMap != null) {
					ybxxpar.put("JBBM", jbbmMap.get("YBBM") + "");
					ybxxpar.put("ZDMC", jbbmMap.get("JBMC") + "");
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取对照信息失败:"
							+ e.getMessage());
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取对照信息失败:"
							+ e.getMessage());
		}
		return ybxxpar;
	}

	public static Map<String, Object> getSzybZhb(Map<String, Object> par,
			Context ctx) throws ModelDataOperationException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"qzjSessionFactory");
		Session ss = sf.openSession();
		BaseDAO emrDao = new BaseDAO(ctx, ss);
		Map<String, Object> V_KA25 = new HashMap<String, Object>();
		Map<String, Object> parZHB = new HashMap<String, Object>();
		parZHB.put("YBBM", par.get("YBBM") + "");
		try {
			List<Map<String, Object>> zhbMap = emrDao
					.doSqlQuery(
							"select AKA099 as ZHB,AKA084 as ZXJLDW,AKA085 as YPZXDW from V_KA25 where AKA080=:YBBM",
							parZHB);
			if (zhbMap != null && zhbMap.size() > 0) {
				if (zhbMap.get(0) != null) {
					V_KA25 = zhbMap.get(0);
				}
			}
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取转换比失败:"
							+ e1.getMessage());
		} finally {
			ss.close();
		}
		return V_KA25;
	}

	public static double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	public static int parseInt(Object o) {
		if (o == null) {
			return new Integer(0);
		}
		return Integer.parseInt(o + "");
	}

	public static long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 省医保组装数据(在数据前后加上[$$]) chzhxiang 2013.09.25
	 * 
	 * @return
	 */
	public static String packagedata(String[] str) {

		String result = "";

		if (str == null || str.length == 0)
			return "";
		long length = str.length;

		if (length == 1)
			return "$$" + str[0] + "$$";

		for (int i = 0; i < length; i++) {
			if (str[i] == null)
				str[i] = "";

			if (i == 0) {
				result = "$$" + str[i] + "~";
				continue;
			}
			if (i == length - 1) {
				result += str[i] + "$$";
				return result;
			}
			result += str[i] + "~";
		}
		return result;
	}
}
