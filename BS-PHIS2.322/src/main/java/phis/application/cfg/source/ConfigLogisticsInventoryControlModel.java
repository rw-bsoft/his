package phis.application.cfg.source;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class ConfigLogisticsInventoryControlModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigLogisticsInventoryControlModel.class);

	public ConfigLogisticsInventoryControlModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-29
	 * @description 物资信息查询
	 * @updateInfo
	 * @param cnd
	 * @param parameters
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Object> queryLogisticsInformation(List<?> cnd,
			Map<String, Object> parameters, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String topId = ParameterUtil.getTopUnitId();
		List<Object> ret = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select WZXH as WZXH,WZMC as WZMC,WZGG as WZGG,WZDW as WZDW,PYDM as PYDM ,WZZT as WZZT from ")
				.append("WL_WZZD")
				.append(" a where SFBZ = 1 and GLFS=1 and ((JGID=:jgid  and KFXH in ( select KFXH from WL_KFXX where KFLB = 1 AND JGID = :jgid)) or JGID=:topId) ");
		try {
			if (cnd != null) {
				hql.append(" and ").append(ExpRunner.toString(cnd, ctx));
			}
			StringBuffer hql_count = new StringBuffer();
			hql_count.append("select count(*) as NUM from (")
					.append(hql.toString()).append(")");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("jgid", jgid);
			map_par.put("topId", topId);
			ret.add(dao.doSqlQuery(hql_count.toString(), map_par).get(0)
					.get("NUM"));
			parameters.put("jgid", jgid);
			parameters.put("topId", topId);
			ret.add(dao.doSqlQuery(hql.toString(), parameters));
		} catch (ExpException e) {
			logger.error("Logistics information acquisition failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物流信息获取失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Logistics information acquisition failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物流信息获取失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-29
	 * @description his物流物品对照保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveLogisticsInformation(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long fyxh = Long.parseLong(body.get("FYXH") + "");
		List<Map<String, Object>> list_fywz = (List<Map<String, Object>>) body
				.get("fywz");
		StringBuffer hql = new StringBuffer();
		hql.append("delete from ").append("GY_FYWZ")
				.append(" where FYXH=:fyxh and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("fyxh", fyxh);
		map_par.put("jgid", jgid);
		try {
			dao.doUpdate(hql.toString(), map_par);
			for (Map<String, Object> map_fywz : list_fywz) {
				if (Double.parseDouble(map_fywz.get("WZSL") + "") == 0) {
					continue;
				}
				dao.doSave("create", BSPHISEntryNames.GY_FYWZ, map_fywz, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "his物流物品对照保存失败");
		} catch (ValidateException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "his物流物品对照保存失败");
		} catch (Exception e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "his物流物品对照保存失败");
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 获取物品计费标志
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryWPJFBZ(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WPJFBZ, ctx)) ? "0" : ParameterUtil
				.getParameter(jgid, BSPHISSystemArgument.WPJFBZ, ctx));
		return wpjfbz;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-7-19
	 * @description 获取门诊物品计费标志
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryWPJFBZ_MZ(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.MZWPJFBZ, ctx));
		return wpjfbz;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 验证病区是否开启物品计费标志 并判断是否存在二级库房
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationWPJFBZ(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long bq = 0;// 当前病区
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "ok");
		int wpjfbz = 0;
		try {// 住院
			if (body.containsKey("bq")) {
				bq = parseLong(body.get("bq"));
				wpjfbz = queryWPJFBZ(ctx);
				if (wpjfbz == 0) {
					return ret;
				}
			} else {// 门诊
				wpjfbz = queryWPJFBZ_MZ(ctx);
				if (wpjfbz == 0) {
					return ret;
				}
				long ghgl = parseLong(body.get("GHGL"));
				long ghks = parseLong(body.get("GHKS"));
				if (ghgl == 0 && ghks == 0) {
					ret.put("code", 9000);
					ret.put("msg", "未挂号病人,无法处理物资信息!");
					return ret;
				}
				StringBuffer hql_ghks = new StringBuffer();
				Map<String, Object> map_par_ghks = new HashMap<String, Object>();
				if (ghks == 0) {
					hql_ghks.append(
							"select c.MZKS as KSDM,b.OFFICENAME as KSMC from ")
							.append("MS_GHMX")
							.append(" a,")
							.append("SYS_Office")
							.append(" b,")
							.append("MS_GHKS")
							.append(" c where a.KSDM=c.KSDM and a.SBXH=:ghgl and c.MZKS=b.ID");
					map_par_ghks.put("ghgl", ghgl);
				} else {
					hql_ghks.append(
							"select c.MZKS as KSDM,b.OFFICENAME as KSMC from ")
							.append("SYS_Office").append(" b,")
							.append("MS_GHKS")
							.append(" c where c.KSDM=:ksdm and c.MZKS=b.ID");
					map_par_ghks.put("ksdm", ghks);
				}
				// hql_ghks.append("select KSDM as KSDM from ").append(BSPHISEntryNames.MS_GHMX).append(" where SBXH=:ghgl");
				// map_par_ghks.put("ghgl", ghgl);
				Map<String, Object> map_ghks = dao.doLoad(hql_ghks.toString(),
						map_par_ghks);
				if (map_ghks == null || map_ghks.size() == 0) {
					ret.put("code", 9000);
					ret.put("msg", "挂号科室查询错误!");
					return ret;
				}
				bq = parseLong(map_ghks.get("KSDM"));
			}
			StringBuffer hql = new StringBuffer();
			hql.append(" KSDM =:bq ");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("bq", bq);
			long l = dao.doCount("WL_KFDZ", hql.toString(), map_par);
			if (l == 0) {
				ret.put("code", 9000);
				ret.put("msg", "当前病区或科室没有物资对应库房!");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "库存信息查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 物资消耗明细数据保存(项目医嘱,附加计价)
	 * @updateInfo
	 * @param list_fymx
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public String saveXhmx(List<Map<String, Object>> list_fymx, Context ctx)
			throws ModelDataOperationException {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql = new StringBuffer();
		hql.append("select KFXH as KFXH from ").append("WL_KFDZ")
				.append("  where KSDM=:ejkf");
		try {
			for (Map<String, Object> map_fymx : list_fymx) {
				Map<String, Object> map_xhmx = new HashMap<String, Object>();
				map_xhmx.put("JGID", map_fymx.get("JGID"));
				map_xhmx.put("KSDM", parseLong(map_fymx.get("KSDM")));
				map_xhmx.put("KSMC", map_fymx.get("KSMC"));
				map_xhmx.put("BRID", parseLong(map_fymx.get("BRID")));
				map_xhmx.put("BRHM", map_fymx.get("BRHM"));
				map_xhmx.put("BRLY", "2");
				map_xhmx.put("BRXM", map_fymx.get("BRXM"));
				map_xhmx.put("XHRQ", d);
				map_xhmx.put("WZXH", parseLong(map_fymx.get("WZXH")));
				map_xhmx.put("WZSL", parseDouble(map_fymx.get("WZSL")));
				map_xhmx.put("ZTBZ", 0);
				map_xhmx.put("XMJE", parseDouble(map_fymx.get("ZJJE")));
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("ejkf", parseLong(map_fymx.get("KSDM")));
				List<Map<String, Object>> list_kfxh = dao.doQuery(
						hql.toString(), map_par);
				if (list_kfxh != null && list_kfxh.size() > 0) {
					map_xhmx.put("KFXH", list_kfxh.get(list_kfxh.size() - 1)
							.get("KFXH"));
				}
				dao.doSave("create", BSPHISEntryNames.WL_XHMX, map_xhmx, false);
			}
			return sdf.format(d);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		} catch (ValidateException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-6-3
	 * @description 物资消耗明细数据保存(医技执行,医技取消执行)
	 * @updateInfo
	 * @param listForputFYMX
	 * @param tag
	 *            用于标识是医技执行还是医技取消,0是取消 1是执行
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveAllXhmx(
			List<Map<String, Object>> listForputFYMX, int tag, Context ctx,Map<String, Object> map_brxx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String bq = (String) user.getProperty("biz_MedicalId");// 病区。
		HttpSession session = (HttpSession) ctx.get(Context.WEB_SESSION);
		Date d = new Date();
		if (bq == null) {
			throw new RuntimeException("请先选择病区！");
		}
		StringBuffer hql_xhmx = new StringBuffer();// 查询费用与物资对照信息
		hql_xhmx.append("select WZXH as WZXH,WZSL as WZSL from ")
				.append("GY_FYWZ").append(" where FYXH=:fyxh and JGID=:jgid");
//		StringBuffer hql_brxx = new StringBuffer();// 病人信息查询
//		hql_brxx.append("select ZYHM as ZYHM,BRXM  as BRXM,BRID as BRID from ")
//				.append("ZY_BRRY").append(" where ZYH=:zyh");
		StringBuffer hql_kfxh = new StringBuffer();
		hql_kfxh.append("select KFXH as KFXH from ").append("WL_KFDZ")
				.append(" where KSDM=:ejkf");
		int wpjfbz = queryWPJFBZ(ctx);
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
		if (listForputFYMX == null) {
			return null;
		}
		long brid = 0;
		long ksdm = 0;
		String ksmc = "";
		try {
			if (wpjfbz == 1 && wzsfxmjg == 0) {
				for (Map<String, Object> parametersForputFYMX : listForputFYMX) {
					if (Integer.parseInt(parametersForputFYMX.get("YPLX") + "") == 0) {// 项目才统计
						Map<String, Object> map_par = new HashMap<String, Object>();
						map_par.put(
								"fyxh",
								Long.parseLong(parametersForputFYMX.get("FYXH")
										+ ""));
						map_par.put("jgid", jgid);
						List<Map<String, Object>> list_fywz = dao.doQuery(
								hql_xhmx.toString(), map_par);
						if (list_fywz != null && list_fywz.size() > 0) {
							map_par.clear();
							map_par.put("zyh", parametersForputFYMX.get("ZYH"));
//							Map<String, Object> map_brxx = dao.doLoad(
//									hql_brxx.toString(), map_par);
							for (Map<String, Object> map_fywz : list_fywz) {
								brid = parseLong(map_brxx.get("BRID"));
								ksdm = parseLong(bq);
								ksmc = (String) user.getProperty("wardName");
								Map<String, Object> m = new HashMap<String, Object>();
								m.put("JGID", jgid);
								m.put("KSDM", parseLong(bq));
								m.put("KSMC",
										session.getAttribute("MedicalName"));
								m.put("BRID", parseLong(map_brxx.get("BRID")));
								m.put("BRHM", map_brxx.get("ZYHM"));
								m.put("BRLY", "2");
								m.put("BRXM", map_brxx.get("BRXM"));
								m.put("XHRQ", d);
								m.put("WZXH", parseLong(map_fywz.get("WZXH")));
								double wzsl = formatDouble(
										2,
										parseDouble(map_fywz.get("WZSL"))
												* parseDouble(parametersForputFYMX
														.get("FYSL")));
								if (tag == 0) {
									wzsl = -wzsl;
								}
								m.put("WZSL", wzsl);
								m.put("ZTBZ", 0);
								m.put("XMJE", parseDouble(parametersForputFYMX
										.get("ZJJE")));
								Map<String, Object> map_par_kfxh = new HashMap<String, Object>();
								map_par_kfxh.put("ejkf", parseLong(bq));
								List<Map<String, Object>> list_kfxh = dao
										.doQuery(hql_kfxh.toString(),
												map_par_kfxh);
								if (list_kfxh != null && list_kfxh.size() > 0) {
									m.put("KFXH",
											list_kfxh.get(list_kfxh.size() - 1)
													.get("KFXH"));
								}
								dao.doSave("create", BSPHISEntryNames.WL_XHMX,
										m, false);
							}
						}
					}
				}
				Map<String, Object> ret = new HashMap<String, Object>();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				ret.put("BRID", brid);
				ret.put("XHRQ", sdf.format(d));
				ret.put("KSMC", ksmc);
				ret.put("KSDM", ksdm);
				return ret;
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		} catch (ValidateException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-6-3
	 * @description 物资消耗明细数据保存(医技执行,医技取消执行)
	 * @updateInfo
	 * @param listForputFYMX
	 * @param tag
	 *            用于标识是医技执行还是医技取消,0是取消 1是执行
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> save_jcAllXhmx(
			List<Map<String, Object>> listForputFYMX, int tag, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String bq = (String) user.getProperty("biz_MedicalId");// 病区。
		HttpSession session = (HttpSession) ctx.get(Context.WEB_SESSION);
		Date d = new Date();
		if (bq == null) {
			throw new RuntimeException("请先选择病区！");
		}
		StringBuffer hql_xhmx = new StringBuffer();// 查询费用与物资对照信息
		hql_xhmx.append("select WZXH as WZXH,WZSL as WZSL from ")
				.append("GY_FYWZ").append(" where FYXH=:fyxh and JGID=:jgid");
		StringBuffer hql_brxx = new StringBuffer();// 病人信息查询
		hql_brxx.append("select ZYHM as ZYHM,BRXM  as BRXM,BRID as BRID from ")
				.append("JC_BRRY").append(" where ZYH=:zyh");
		StringBuffer hql_kfxh = new StringBuffer();
		hql_kfxh.append("select KFXH as KFXH from ").append("WL_KFDZ")
				.append(" where KSDM=:ejkf");
		int wpjfbz = queryWPJFBZ(ctx);
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
		if (listForputFYMX == null) {
			return null;
		}
		long brid = 0;
		long ksdm = 0;
		String ksmc = "";
		try {
			if (wpjfbz == 1 && wzsfxmjg == 0) {
				for (Map<String, Object> parametersForputFYMX : listForputFYMX) {
					if (Integer.parseInt(parametersForputFYMX.get("YPLX") + "") == 0) {// 项目才统计
						Map<String, Object> map_par = new HashMap<String, Object>();
						map_par.put(
								"fyxh",
								Long.parseLong(parametersForputFYMX.get("FYXH")
										+ ""));
						map_par.put("jgid", jgid);
						List<Map<String, Object>> list_fywz = dao.doQuery(
								hql_xhmx.toString(), map_par);
						if (list_fywz != null && list_fywz.size() > 0) {
							map_par.clear();
							map_par.put("zyh", parametersForputFYMX.get("ZYH"));
							Map<String, Object> map_brxx = dao.doLoad(
									hql_brxx.toString(), map_par);
							for (Map<String, Object> map_fywz : list_fywz) {
								brid = parseLong(map_brxx.get("BRID"));
								ksdm = parseLong(bq);
								ksmc = (String) user.getProperty("wardName");
								Map<String, Object> m = new HashMap<String, Object>();
								m.put("JGID", jgid);
								m.put("KSDM", parseLong(bq));
								m.put("KSMC",
										session.getAttribute("MedicalName"));
								m.put("BRID", parseLong(map_brxx.get("BRID")));
								m.put("BRHM", map_brxx.get("ZYHM"));
								m.put("BRLY", "2");
								m.put("BRXM", map_brxx.get("BRXM"));
								m.put("XHRQ", d);
								m.put("WZXH", parseLong(map_fywz.get("WZXH")));
								double wzsl = formatDouble(
										2,
										parseDouble(map_fywz.get("WZSL"))
												* parseDouble(parametersForputFYMX
														.get("FYSL")));
								if (tag == 0) {
									wzsl = -wzsl;
								}
								m.put("WZSL", wzsl);
								m.put("ZTBZ", 0);
								m.put("XMJE", parseDouble(parametersForputFYMX
										.get("ZJJE")));
								Map<String, Object> map_par_kfxh = new HashMap<String, Object>();
								map_par_kfxh.put("ejkf", parseLong(bq));
								List<Map<String, Object>> list_kfxh = dao
										.doQuery(hql_kfxh.toString(),
												map_par_kfxh);
								if (list_kfxh != null && list_kfxh.size() > 0) {
									m.put("KFXH",
											list_kfxh.get(list_kfxh.size() - 1)
													.get("KFXH"));
								}
								dao.doSave("create", BSPHISEntryNames.WL_XHMX,
										m, false);
							}
						}
					}
				}
				Map<String, Object> ret = new HashMap<String, Object>();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				ret.put("BRID", brid);
				ret.put("XHRQ", sdf.format(d));
				ret.put("KSMC", ksmc);
				ret.put("KSDM", ksdm);
				return ret;
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		} catch (ValidateException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-7-19
	 * @description 门诊收费物资消耗明细保存
	 * @updateInfo
	 * @param yjxh
	 * @param ghgl
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveMzWzxx(String yjxh, long ghks, long ghgl, long mzxh,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJG, ctx));
		int wpjfbz = queryWPJFBZ_MZ(ctx);
		if (wpjfbz == 0) {
			return;
		}
		if (wzsfxmjg == 1) {
			return;
		}
		long ksdm = 0;
		try {
			// if(ghgl==0){
			// throw new ModelDataOperationException(
			// ServiceCode.CODE_DATABASE_ERROR, "未找到挂号记录");
			// }
			StringBuffer hql_ghks = new StringBuffer();// 去挂号记录里面找到就诊科室,如果找不到
														// 则不能收费
			Map<String, Object> map_par_ghks = new HashMap<String, Object>();
			if (ghks == 0) {
				hql_ghks.append(
						"select c.MZKS as KSDM,b.OFFICENAME as KSMC from ")
						.append("MS_GHMX")
						.append(" a,")
						.append("SYS_Office")
						.append(" b,")
						.append("MS_GHKS")
						.append(" c where a.KSDM=c.KSDM and a.SBXH=:ghgl and c.MZKS=b.ID");
				map_par_ghks.put("ghgl", ghgl);
			} else {
				hql_ghks.append(
						"select c.MZKS as KSDM,b.OFFICENAME as KSMC from ")
						.append("SYS_Office").append(" b,").append("MS_GHKS")
						.append(" c where c.KSDM=:ksdm and c.MZKS=b.ID");
				map_par_ghks.put("ksdm", ghks);
			}
			Map<String, Object> map_ghks;
			map_ghks = dao.doLoad(hql_ghks.toString(), map_par_ghks);
			if (map_ghks == null || map_ghks.size() == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "挂号科室查询失败");
			}
			ksdm = parseLong(map_ghks.get("KSDM"));
			String ksmc = map_ghks.get("KSMC") + "";
			StringBuffer hql = new StringBuffer();// 查询医技信息
			hql.append(
					"select a.JGID as JGID,a.BRID as BRID,a.BRXM as BRXM,b.YLXH as YLXH,b.YLSL as YLSL,b.HJJE as HJJE from ")
					.append("MS_YJ01").append(" a,").append("MS_YJ02")
					.append(" b where a.YJXH=b.YJXH and a.YJXH in (")
					.append(yjxh).append(")");
			List<Map<String, Object>> list_yjxx = dao.doSqlQuery(
					hql.toString(), null);
			StringBuffer hql_kfxh = new StringBuffer();// 查询库房序号
			hql_kfxh.append("select KFXH as KFXH from ").append("WL_KFDZ")
					.append("  where KSDM=:ksdm");
			Map<String, Object> map_par_kfxh = new HashMap<String, Object>();
			map_par_kfxh.put("ksdm", ksdm);
			Map<String, Object> map_kfxh = dao.doLoad(hql_kfxh.toString(),
					map_par_kfxh);
			if (map_kfxh == null || map_kfxh.size() == 0) {// 如果没有对照,也不能收费
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前科室没有物资对应库房");
			}
			long kfxh = parseLong(map_kfxh.get("KFXH"));
			Date d = new Date();
			StringBuffer hql_xhmx = new StringBuffer();// 查询费用与物资对照信息
			hql_xhmx.append("select WZXH as WZXH,WZSL as WZSL from ")
					.append("GY_FYWZ")
					.append(" where FYXH=:fyxh and JGID=:jgid");
			for (Map<String, Object> map_yjxx : list_yjxx) {
				Map<String, Object> map_par_fywz = new HashMap<String, Object>();
				map_par_fywz.put("fyxh", parseLong(map_yjxx.get("YLXH")));
				map_par_fywz.put("jgid", jgid);
				List<Map<String, Object>> list_fywz = dao.doQuery(
						hql_xhmx.toString(), map_par_fywz);
				for (Map<String, Object> map_fywz : list_fywz) {
					Map<String, Object> map_xhmx = new HashMap<String, Object>();
					map_xhmx.put("JGID", map_yjxx.get("JGID"));
					map_xhmx.put("KSDM", ksdm);
					map_xhmx.put("KSMC", ksmc);
					map_xhmx.put("BRID", parseLong(map_yjxx.get("BRID")));
					map_xhmx.put("BRHM", "");
					map_xhmx.put("BRLY", "1");
					map_xhmx.put("BRXM", map_yjxx.get("BRXM"));
					map_xhmx.put("XHRQ", d);
					map_xhmx.put("WZXH", parseLong(map_fywz.get("WZXH")));
					map_xhmx.put(
							"WZSL",
							formatDouble(2, parseDouble(map_fywz.get("WZSL"))
									* parseDouble(map_yjxx.get("YLSL"))));
					map_xhmx.put("ZTBZ", 0);
					map_xhmx.put("XMJE", parseDouble(map_yjxx.get("HJJE")));
					map_xhmx.put("KFXH", kfxh);
					map_xhmx.put("MZXH", mzxh);
					dao.doSave("create", BSPHISEntryNames.WL_XHMX, map_xhmx,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		} catch (ValidateException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗数据保存失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-7-19
	 * @description 发票作废前判断下物资是否出库
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationMzfpzf(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "ok");
		int wpjfbz = queryWPJFBZ_MZ(ctx);
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJG, ctx));
		if (wpjfbz == 0) {
			return ret;
		}
		try {
			if (wpjfbz == 1) {
				if (wzsfxmjg == 1) {
					String fphm = body.get("FPHM") + "";
					StringBuffer hql_count = new StringBuffer();
					hql_count
							.append("select count(*) as NUM from wl_ck02 e, wl_ck01 d where d.djzt =2 and  d.djxh =e.djxh and d.djxh in (SELECT  distinct DJXH FROM WL_XHMX a,MS_MZXX b,MS_YJ01 c,MS_YJ02 d WHERE a.MZXH=d.SBXH and d.YJXH=c.YJXH and c.MZXH=b.MZXH and b.FPHM=:fphm and DJXH IS NOT NULL AND DJXH > 0 AND ZTBZ = 1)");
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("fphm", fphm);
					List<Map<String, Object>> list_count = dao.doSqlQuery(
							hql_count.toString(), map_par);
					if (list_count == null || list_count.size() < 1) {
						return ret;
					}
					long l = parseLong(list_count.get(0).get("NUM"));
					if (l > 0) {
						hql_count = new StringBuffer();
						hql_count
								.append("select count(*) as NUM from wl_ck02 e, wl_ck01 d where d.djzt =2 and  d.djxh =e.djxh and d.thdj in (SELECT  distinct DJXH FROM WL_XHMX a,MS_MZXX b,MS_YJ01 c,MS_YJ02 d WHERE a.MZXH=d.SBXH and d.YJXH=c.YJXH and c.MZXH=b.MZXH and b.FPHM=:fphm and DJXH IS NOT NULL AND DJXH > 0 AND ZTBZ = 1)");
						long ll = parseLong(dao
								.doSqlQuery(hql_count.toString(), map_par)
								.get(0).get("NUM"));
						if (l != ll) {
							ret.put("code", 9000);
							ret.put("msg", "物品已出库,不能作废发票!");
						}
					}
				} else {
					String fphm = body.get("FPHM") + "";
					StringBuffer hql_count = new StringBuffer();
					hql_count
							.append("select count(*) as NUM from ")
							.append("WL_XHMX")
							.append(" a,")
							.append("MS_MZXX")
							.append(" b where a.MZXH=b.MZXH and b.FPHM=:fphm and a.DJXH IS NOT NULL AND a.DJXH > 0 AND a.ZTBZ = 1 GROUP BY a.DJXH");
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("fphm", fphm);
					List<Map<String, Object>> list_count = dao.doSqlQuery(
							hql_count.toString(), map_par);
					if (list_count == null || list_count.size() < 1) {
						return ret;
					}
					long l = parseLong(list_count.get(0).get("NUM"));
					if (l > 0) {
						hql_count = new StringBuffer();
						hql_count
								.append("select count(*) as NUM from WL_CK01 d,(SELECT  distinct DJXH FROM WL_XHMX a,MS_MZXX b WHERE a.MZXH = b.MZXH and b.FPHM=:fphm and DJXH IS NOT NULL AND DJXH > 0 AND ZTBZ = 1  ) c where d.THDJ = c.DJXH and d.DJZT =2 ");
						long ll = parseLong(dao
								.doSqlQuery(hql_count.toString(), map_par)
								.get(0).get("NUM"));
						if (l != ll) {
							ret.put("code", 9000);
							ret.put("msg", "物品已出库,不能作废发票!");
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "物资消耗查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description 门诊处方附加项目新增数据生成
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryAddData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long deptid = parseLong(user.getProperty("reg_departmentId"));
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		long sbxh = parseLong(body.get("SBXH"));
		int brxz = parseInt(body.get("BRXZ"));
		long gytj = parseLong(body.get("GYTJ"));
		StringBuffer hql = new StringBuffer();
		hql.append("select FYXH as FYXH from ").append("ZY_YPYF")
				.append(" where YPYF=:gytj");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("gytj", gytj);
		try {
			Map<String, Object> map_fyxh = dao.doLoad(hql.toString(), map_par);
			hql = new StringBuffer();
			hql.append("select MZKS as MZKS from ").append("MS_GHKS")
					.append(" where  KSDM=:ksdm");
			map_par.clear();
			map_par.put("ksdm", deptid);
			Map<String, Object> map_mzks = dao.doLoad(hql.toString(), map_par);
			hql = new StringBuffer();
			hql.append(
					"select XMXH as XMXH,SYLB as SYLB,KSDM as KSDM,GLXH as GLXH,FYSL as GLSL from ")
					.append("GY_XMGL")
					.append(" where XMXH=:fyxh and ( KSDM=:ksdm or KSDM=0) and JGID=:jgid");
			map_par.put("fyxh", map_fyxh.get("FYXH"));
			map_par.put("ksdm", map_mzks.get("MZKS"));
			map_par.put("jgid", jgid);
			List<Map<String, Object>> list_xmgl = dao.doQuery(hql.toString(),
					map_par);
			StringBuffer hql_sfxm = new StringBuffer();
			hql_sfxm.append(
					"select a.FYXH as FYXH,a.XMLX as XMLX,a.FYMC as FYMC,a.PYDM as PYDM,a.FYDW as FYDW,b.FYDJ as FYDJ,a.FYGB as FYGB,b.FYKS as FYKS,a.TSTS as TSTS,a.YJJK as YJJK ,a.MZSQ as  JCSQ,a.YJSY as YJSY from ")
					.append("GY_YLSF")
					.append(" a,")
					.append("GY_YLMX")
					.append(" b where a.FYXH=b.FYXH and a.MZSY=1 and b.JGID=:jgid and a.FYXH=:fyxh order by a.PYDM,a.FYXH asc");
			int i = 0;
			for (Map<String, Object> map_xmgl : list_xmgl) {
				Map<String, Object> map_par_sfmx = new HashMap<String, Object>();
				map_par_sfmx.put("jgid", jgid);
				map_par_sfmx.put("fyxh", map_xmgl.get("GLXH"));
				Map<String, Object> map_sfmx = dao.doLoad(hql_sfxm.toString(),
						map_par_sfmx);
				if (map_sfmx == null) {
					continue;
				}
				Map<String, Object> map_yj02 = new HashMap<String, Object>();
				map_yj02.put("JGID", jgid);
				map_yj02.put("YLXH", map_sfmx.get("FYXH"));
				map_yj02.put("XMLX", map_sfmx.get("XMLX"));
				map_yj02.put("YLDJ", map_sfmx.get("FYDJ"));
				map_yj02.put("YLSL", map_xmgl.get("GLSL"));
				map_yj02.put("FYGB", map_sfmx.get("FYGB"));
				Map<String, Object> map_zfbl = new HashMap<String, Object>();
				map_zfbl.put("TYPE", 1);
				map_zfbl.put("BRXZ", brxz);
				map_zfbl.put("FYXH", map_sfmx.get("FYXH"));
				map_zfbl.put("FYGB", map_sfmx.get("FYGB"));
				map_yj02.put(
						"ZFBL",
						BSPHISUtil.getPayProportion(map_zfbl, ctx, dao).get(
								"ZFBL"));
				if (i == 0) {
					map_yj02.put("YJZX", 1);
				} else {
					map_yj02.put("YJZX", 0);
				}
				map_yj02.put("BZXX", "处方附加项目");
				map_yj02.put("FYMC", map_sfmx.get("FYMC"));
				map_yj02.put("FYDW", map_sfmx.get("FYDW"));
				map_yj02.put("CFBZ", 0);
				map_yj02.put("FJGL", sbxh);
				map_yj02.put("FJLB", 2);
				map_yj02.put(
						"HJJE",
						formatDouble(2, parseDouble(map_sfmx.get("FYDJ"))
								* parseDouble(map_xmgl.get("GLSL"))));
				ret.add(map_yj02);
				i++;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to save his control logistics items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "数据查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description 删除附加项目
	 * @updateInfo
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void removeAdditional(long sbxh) throws ModelDataOperationException {
		try {
			Map<String, Object> map_yj01 = dao.doLoad(
					CNDHelper.toListCnd("['eq',['$','FJGL'],['d'," + sbxh
							+ "]]"), BSPHISEntryNames.MS_YJ01_CIC);
			if (map_yj01 == null) {
				return;
			}
			StringBuffer hql = new StringBuffer();
			hql.append("delete from ").append("MS_YJ02")
					.append(" where YJXH=:yjxh");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yjxh", map_yj01.get("YJXH"));
			dao.doUpdate(hql.toString(), map_par);
			dao.doRemove(map_yj01.get("YJXH"), BSPHISEntryNames.MS_YJ01_CIC);
		} catch (PersistentDataOperationException e) {
			logger.error("Delete the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除附加项目失败");
		} catch (ExpException e) {
			logger.error("Delete the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除附加项目失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description 删除所有附加项目
	 * @updateInfo
	 * @param cfsb
	 * @throws ModelDataOperationException
	 */
	public void removeAllAdditional(long cfsb)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("cfsb", cfsb);
			// 删除附加信息
			String del_yj02 = "delete MS_YJ02 where YJXH in (select YJXH from MS_YJ01 where FJGL in (select distinct YPZH from MS_CF02 where CFSB=:cfsb))";
			dao.doSqlUpdate(del_yj02, map_par);
			String del_yj01 = "delete MS_YJ01 where FJGL in (select distinct YPZH from MS_CF02 where CFSB=:cfsb)";
			dao.doSqlUpdate(del_yj01, map_par);
			StringBuffer hql = new StringBuffer();
			hql.append("select SBXH as SBXH from ").append("MS_CF02")
					.append(" where CFSB=:cfsb");

			List<Map<String, Object>> list_cf02 = dao.doQuery(hql.toString(),
					map_par);
			for (Map<String, Object> map_cf02 : list_cf02) {
				removeAdditional(parseLong(map_cf02.get("SBXH")));
			}

		} catch (PersistentDataOperationException e) {
			logger.error("Delete the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除附加项目失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description 新增的处方明细的附加项目保存
	 * @updateInfo
	 * @param map_cf02
	 * @param list_fjInsertData
	 * @param sbxh
	 * @param map_jbxx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveFjInsertData(Map<String, Object> map_cf02,
			List<Map<String, Object>> list_fjInsertData, long sbxh,
			Map<String, Object> map_jbxx) throws ModelDataOperationException {
		try {
			if (list_fjInsertData == null) {
				return;
			}
			for (Map<String, Object> map_fjInsertData : list_fjInsertData) {
				if (parseLong(map_fjInsertData.get("YPXH")) == parseLong(map_cf02
						.get("YPXH"))
						&& parseInt(map_fjInsertData.get("YPZH")) == parseInt(map_cf02
								.get("YPZH"))) {
					List<Map<String, Object>> list_yj02 = (List<Map<String, Object>>) map_fjInsertData
							.get("D");
					if (list_yj02.size() > 0) {
						Map<String, Object> map_yj01 = new HashMap<String, Object>();
						map_yj01.put("JGID", map_jbxx.get("JGID"));
						map_yj01.put("JZXH", parseLong(map_jbxx.get("JZXH")));
						map_yj01.put("BRID", parseLong(map_jbxx.get("BRID")));
						map_yj01.put("BRXM", map_jbxx.get("BRXM"));
						map_yj01.put("KDRQ", new Date());
						map_yj01.put("KSDM", parseLong(map_jbxx.get("KSDM")));
						map_yj01.put("YSDM", map_jbxx.get("YSDM"));
						map_yj01.put("ZXKS", parseLong(map_jbxx.get("KSDM")));
						map_yj01.put("ZXYS", map_jbxx.get("YSDM"));
						map_yj01.put("ZXPB", 0);
						map_yj01.put("CFBZ", 0);
						map_yj01.put("SCBZ", 0);
						map_yj01.put("FJGL", sbxh);
						map_yj01.put("FJLB", 2);
						map_yj01.put("ZFPB", 0);
						map_yj01.put("DJLY", 1);
						map_yj01.put("DJZT", 0);
						Map<String, Object> map_key = dao.doSave("create",
								BSPHISEntryNames.MS_YJ01_CIC, map_yj01, false);
						long yjxh = parseLong(map_key.get("YJXH"));
						for (Map<String, Object> map_yj02 : list_yj02) {
							map_yj02.put("YJXH", yjxh);
							dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC,
									map_yj02, false);
						}
					}
				}
			}
		} catch (ValidateException e) {
			logger.error("Failed to add additional items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "新增附加项目失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to add additional items", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "新增附加项目失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description 修改的处方明细的附加项目保存
	 * @updateInfo
	 * @param map_fjChangeData
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveFjChangeData(Map<String, Object> map_fjChangeData)
			throws ModelDataOperationException {
		if (map_fjChangeData == null || map_fjChangeData.size() == 0) {
			return;
		}
		try {
			for (String key : map_fjChangeData.keySet()) {
				long sbxh = parseLong(key);
				Map<String, Object> map_yj01 = dao.doLoad(
						CNDHelper.toListCnd("['eq',['$','FJGL'],['d'," + sbxh
								+ "]]"), BSPHISEntryNames.MS_YJ01_CIC);
				if (map_yj01 == null) {
					continue;
				}
				StringBuffer hql = new StringBuffer();
				hql.append("delete from ").append("MS_YJ02")
						.append(" where YJXH=:yjxh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yjxh", map_yj01.get("YJXH"));
				dao.doUpdate(hql.toString(), map_par);
				for (Map<String, Object> map_yj02 : (List<Map<String, Object>>) map_fjChangeData
						.get(key)) {
					map_yj02.put("YJXH", map_yj01.get("YJXH"));
					dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC,
							map_yj02, false);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("update the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改附加项目失败");
		} catch (ExpException e) {
			logger.error("update the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改附加项目失败");
		} catch (ValidateException e) {
			logger.error("update the attached project failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改附加项目失败");
		}
	}

	public List<Map<String, Object>> queryCfmx(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		String zbmjgid="";
		Map<String, Object> param=new HashMap<String, Object>();
		try {
			Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			String where = ExpressionProcessor.instance().toString(cnd);
			StringBuffer hql = new StringBuffer();
			param.put("JGID", zbmjgid);
			// String sql=HQLHelper.buildQueryHql(cnd, null, "MS_CF02_CF", ctx);
			hql.append(
					"select b.YYBS as YYBS,b.GMYWLB as GMYWLB,d.FPHM as FPHM,b.JYLX as JYLX,a.SBXH as SBXH," +
					" a.YPXH as YPXH,a.CFSB as CFSB,a.YPZH as YPZH,b.YPMC as YPMC,a.YFGG as YFGG,a.YFBZ as YFBZ," +
					" a.CFTS as CFTS,a.YCJL as YCJL,b.JLDW as JLDW,b.YPJL as YPJL,a.YPYF as YPYF,a.MRCS as MRCS," +
					" a.YYTS as YYTS,a.YPSL as YPSL,a.YFDW as YFDW,a.GYTJ as GYTJ,a.YPZS as YPZS,a.YPCD as YPCD," +
					" a.YPDJ as YPDJ,a.ZFBL as ZFBL,a.BZXX as BZXX,a.HJJE as HJJE,a.PSPB as PSPB,a.PSJG as PSJG," +
					" a.FYGB as FYGB,b.KSBZ as KSBZ, b.YCYL as YCYL,b.TYPE as TYPE,b.TSYP as TSYP,b.JYLX as JYLX," +
					" a.BZMC as BZMC,a.SFJG as SFJG, a.ZFPB as ZFPB ,c.KPDY as KPDY,a.SYYY,a.YQSY,a.SQYS," +
					" a.ZFYP as ZFYP,b.ZFYP as ZBY,a.JZ as JZ,decode(e.YYZBM,'',' ','医保可报销') as YYZBM")
					.append(" from MS_CF02 a left outer join YK_TYPK b on a.YPXH=b.YPXH left outer join ZY_YPYF c on a.GYTJ=c.YPYF left outer join MS_CF01 d on a.CFSB=d.CFSB left outer join YK_CDXX e on e.JGID=:JGID and a.YPXH=e.YPXH and a.YPCD=e.YPCD where ")
					.append(where).append(" order by a.YPZH,a.SBXH");
			ret = dao.doSqlQuery(hql.toString(), param);
			if (ret == null) {
				return ret;
			}

			// Schema schema =
			// SchemaController.instance().getSchema("MS_CF02_CF");
			// List<SchemaItem> itemList = schema.getAllItemsList();
			Schema schema = SchemaController.instance().get(
					"phis.application.cic.schemas.MS_CF02_CF");
			List<SchemaItem> itemList = schema.getItems();
			for (Map<String, Object> map_cf02 : ret) {
				if (parseLong(map_cf02.get("YPXH")) == 0) {
					map_cf02.put("YPMC", map_cf02.get("BZMC"));
					map_cf02.put("YPJL", 1);
				}
				for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
						.hasNext();) {
					SchemaItem item = iterator.next();
					if (item.isCodedValue()) {
						String schemaKey = item.getId();
						if (map_cf02.get(schemaKey) == null) {
							continue;
						}
						String key = StringUtils.trimToEmpty(map_cf02.get(
								schemaKey).toString());
						if (!key.equals("") && key != null) {
							// Map<String, String> dicValue = new
							// HashMap<String, String>();
							// dicValue.put("key", key);
							// dicValue.put("text", item.getDisplayValue(key));
							map_cf02.put(schemaKey + "_text",
									item.getDisplayValue(key));
						}
					}
				}
			}
		} catch (ExpException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		} catch (ControllerException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处方明细查询失败");
		}
		return ret;
	}
	/**
	 * 
	 * @author zhaojian
	 * @createDate 2019-7-24
	 * @description 门诊医生站处置录入列表信息查询
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryYjmx(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		String zbmjgid="";
		Map<String, Object> param=new HashMap<String, Object>();
		try {
			Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			String where = ExpressionProcessor.instance().toString(cnd);
			StringBuffer hql = new StringBuffer();
			param.put("JGID", zbmjgid);
			hql.append(
					"select a.SBXH as SBXH,a.YJZH as YJZH,a.JGID as JGID,a.YJXH as YJXH,a.YLXH as YLXH,a.XMLX as XMLX," +
					"a.YJZX as YJZX,b.FYMC as FYMC,b.FYDW as FYDW,b.JCDL as JCDL,a.YLDJ as YLDJ,a.YLSL as YLSL," +
					"a.HJJE as HJJE,c.ZXRQ as ZXRQ,c.YSDM as YSDM,c.KSDM as KSDM,c.ZXKS as ZXKS,a.JCBWDM as JCBWDM," +
					"c.MZXH as MZXH,c.FPHM as FPHM,c.DJLY as DJLY,c.SQID as SQID,c.BGSJ as BGSJ,a.FYGB as FYGB," +
					"a.ZFBL as ZFBL,a.BZXX as BZXX,a.DZBL as DZBL,a.ZFPB as ZFPB,a.SPBH as SPBH,decode(e.YYZBM,'',' ','医保可报销') as YYZBM ")
					.append("from MS_YJ02 a,GY_YLSF b,MS_YJ01 c,GY_YLMX d,GY_YLMX e where ")
					.append(where).append(" and b.FYXH=a.YLXH and c.YJXH=a.YJXH and d.FYXH=a.YLXH and d.JGID=a.JGID and e.FYXH=a.YLXH and e.JGID=:JGID ")
					.append(" order by a.YJZH,a.SBXH");
			ret = dao.doSqlQuery(hql.toString(), param);
			if (ret == null) {
				return ret;
			}
			Schema schema = SchemaController.instance().get(
					"phis.application.cic.schemas.MS_YJ02_CZ");
			List<SchemaItem> itemList = schema.getItems();
			for (Map<String, Object> map_yj02 : ret) {
/*				if (parseLong(map_yj02.get("YPXH")) == 0) {
					map_yj02.put("YPMC", map_yj02.get("BZMC"));
					map_yj02.put("YPJL", 1);
				}*/
				for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
						.hasNext();) {
					SchemaItem item = iterator.next();
					if (item.isCodedValue()) {
						String schemaKey = item.getId();
						if (map_yj02.get(schemaKey) == null) {
							continue;
						}
						String key = StringUtils.trimToEmpty(map_yj02.get(
								schemaKey).toString());
						if (!key.equals("") && key != null) {
							// Map<String, String> dicValue = new
							// HashMap<String, String>();
							// dicValue.put("key", key);
							// dicValue.put("text", item.getDisplayValue(key));
							map_yj02.put(schemaKey + "_text",
									item.getDisplayValue(key));
						}
					}
				}
			}
		} catch (ExpException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处置明细查询失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处置明细查询失败");
		} catch (ControllerException e) {
			logger.error("Prescription detail query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "处置明细查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null || "".equals(o)) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null || "".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null || "".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public double formatDouble(int number, double data) {
		// if (number > 8) {
		// return 0;
		// }
		// double x = Math.pow(10, number);
		// return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data, number);
	}
}
