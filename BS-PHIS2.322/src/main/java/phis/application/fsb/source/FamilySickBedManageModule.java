/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.fsb.source;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class FamilySickBedManageModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedManageModule.class);

	public FamilySickBedManageModule(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> doLoadNavTree(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		String hql = "SELECT a.LBBH as id,a.LBMC as text,a.LBMC,a.LBMC as MBMC,a.DYWD,a.BLLX,a.XSMC,a.LBBH as MBLB  FROM EMR_KBM_BLLB a,EMR_KBM_YDBLLB b WHERE a.YDLBBM = b.YDLBBM AND a.MLBZ = 1 and b.BJQLB<>13 and exists(select 1 from CHTEMPLATE where FRAMEWORKCODE=a.LBBH and INOROUTTYPE=2) order by ";
		Integer zybz = (Integer) (body.get("ZYBZ") == null ? 1 : body
				.get("ZYBZ"));
		Long jzh = Long.parseLong(body.get("JZH").toString());
		if (zybz == 1) {
			hql += " a.ZYPLXH";
		} else {
			hql += " a.CYPLXH";
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = dao.doSqlQuery(hql, null);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZH", jzh);
			for (Map<String, Object> map : list) {
				if ("1".equals(map.get("DYWD")))
					continue;
				String mxHql = "SELECT a.BLLB as id,a.BLMC as text,a.BLBH,a.BLLX,b.MBMC,a.MBLB FROM EMR_BL01 a left join V_EMR_BLMB b on (a.MBBH=b.MBBH and b.BLLX=a.BLLX) WHERE (a.JZXH = :JZH) AND a.BLLB = :BLLB AND a.BLZT <> 9 and a.DLLB <>-1 order by a.JZXH ASC,a.BLLB ASC,a.JLSJ ASC,a.BLBH  ASC";
				params.put("BLLB", map.get("ID"));
				List<Map<String, Object>> mxList = dao
						.doSqlQuery(mxHql, params);
				if (mxList.size() > 0) {
					for (Map<String, Object> mxMap : mxList) {
						mxMap.put("LBMC", map.get("TEXT"));
						mxMap.put("XSMC", map.get("XSMC"));
						mxMap.put("DYWD", map.get("DYWD"));
					}
					map.put("expanded", true);
					map.put("children", mxList);
				} else {
					map.put("leaf", true);
				}
			}
			res.put("emrTree", list);
			String ccjl = "select SBXH as SBXH,str(CCSJ,'yyyy-MM-dd hh24:mi:ss') as CCSJ from JC_CCJL where ZYH=:ZYH order by CCSJ desc";
			params.clear();
			params.put("ZYH", Long.parseLong(body.get("JZH").toString()));
			res.put("fdrTree", dao.doQuery(ccjl, params));
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据库发生异常!请联系管理员.", e);
		}
	}

	public Map<String, Object> doLoadPharmacy()
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JGID", manageUnit);
		try {
			List<Map<String, Object>> ret = dao
					.doQuery(
							"select a.DMSB as DMSB,a.YFSB as YFSB,b.YFMC as YFMC from "
									+ "JC_FYYF a,YF_YFLB b where a.YFSB=b.YFSB and a.JGID=:JGID and a.TYPE=1 and a.ZXPB=0 and a.GNFL=4",
							params);
			if (ret != null && ret.size() > 0) {
				res.put("fyyf", ret);
			}
			// 门诊处方权限信息获取
			String cf_hql = "SELECT PERSONNAME as PERSONNAME, PRESCRIBERIGHT as KCFQ, NARCOTICRIGHT as MZYQ, PSYCHOTROPICRIGHT as JSYQ,ANTIBIOTICLEVEL as KSSQX  FROM SYS_Personnel WHERE PERSONID=:YGDM";
			params.clear();
			params.put("YGDM", user.getUserId());
			res.put("MZ_YSQX", dao.doLoad(cf_hql, params));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("载入住院医嘱参数失败!", e);
		}
		return res;
	}

	/**
	 * 载入家床医嘱详细信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadDetailsInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		// UserRoleToken user = UserRoleToken.getCurrent();
		// String manageUnit = user.getManageUnitId();
		Long brid = Long.parseLong(body.get("BRID").toString());
		Long ypxh = Long.parseLong(body.get("YPXH").toString());
		Integer yplx = (Integer) body.get("YPLX");
		Long fygb = BSPHISUtil.getfygb(Long.parseLong(yplx.toString()), ypxh,
				dao, ctx);
		Object brxz = body.get("BRXZ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BRXZ", brxz);
		params.put("FYGB", fygb);
		params.put("TYPE", yplx);
		params.put("FYXH", ypxh);
		Map<String, Object> zfblMap = BSPHISUtil.getPayProportion(params, ctx,
				dao);
		ret.put("payProportion", zfblMap);
		if (yplx > 0) {
			params.clear();
			// params.put("JGID", manageUnit);
			params.put("BRID", brid);
			params.put("YPXH", ypxh);
			try {
				List<Map<String, Object>> list = dao
						.doQuery(
								"select BLFY as BLFY from GY_PSJL"
										+ " where BRID=:BRID and YPXH=:YPXH and PSJG=1",
								params);
				if (list.size() == 0) {
					ret.put("isAllergy", false);
				} else {
					ret.put("isAllergy", true);
					ret.put("BLFY", list.get(0).get("BLFY"));
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("查询病人过敏信息失败!", e);
			}
		}
		return ret;
	}

	/**
	 * 保存医嘱信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveWardPatientInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String jckcgl = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.JCKCGL, ctx);
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.WPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manageUnit,
						BSPHISSystemArgument.WPJFBZ, ctx));
		int wzsfxmjgzy = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
				: ParameterUtil.getParameter(manageUnit,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
		Map<String, Object> prarms = new HashMap<String, Object>();
		List<Map<String, Object>> yzxxs = (List<Map<String, Object>>) body
				.get("yzxx");
		Map<String, Object> ccxx = (Map<String, Object>) body.get("ccxx");

		Schema sc;
		try {
			sc = SchemaController.instance().get(
					"phis.application.fsb.schemas.JC_BRYZ_LS");
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取schema文件异常!", e);
		}
		List<SchemaItem> items = sc.getItems();
		SchemaItem item = null;
		for (SchemaItem sit : items) {
			if ("YZZH".equals(sit.getId())) {
				item = sit;
				break;
			}
		}
		Integer lastYzzh = -1;
		Date lastDate = null;
		Long YZZH = 0l;
		Long SBXH = null;// 查床记录主键
		try {
			if (ccxx != null && ccxx.size() > 0) {
				String op = (ccxx.get("SBXH") == null ? "create" : "update");
				Map<String, Object> m = dao.doSave(op,
						BSPHISEntryNames.JC_CCJL, ccxx, false);
				if (op.equals("update")) {
					SBXH = Long.parseLong(ccxx.get("SBXH").toString());
				} else {
					SBXH = Long.parseLong(m.get("SBXH").toString());
					res.put("ccjl_sbxh", SBXH);
				}
			}
			int yzpx = 0;
			double sysl = 0;
			for (int i = 0; i < yzxxs.size(); i++) {
				Map<String, Object> r = yzxxs.get(i);
				String op = (String) r.get("_opStatus");
				if (op.equals("remove")) {
					// if (wpjfbz == 1 && wzsfxmjgzy == 1) {
					// BSPHISUtil.deleteSuppliesZY(dao, ctx,
					// Long.parseLong(r.get("JLXH") + ""));
					// }
					dao.doRemove(Long.parseLong(r.get("JLXH").toString()),
							BSPHISEntryNames.JC_BRYZ + "_CQ");
					continue;
				}
				// 默认值设置
				if ((Integer) r.get("YZZH_SHOW") == lastYzzh) {
					r.put("KSSJ", lastDate);
					// r.put("YZPX", ++yzpx);
				} else {
					if ("create".equals(op)
							&& (r.get("YZZH") == null
									|| r.get("YZZH").toString().length() == 0 || r
									.get("YZZH").toString().equals("0"))) {
						YZZH = Long.parseLong(KeyManager.getKeyByName(
								"JC_BRYZ", item.getKeyRules(), item.getId(),
								ctx));
						res.put("YZZH", YZZH);
						yzpx = 0;
						// 查询医嘱表YZZH最大值
					}
					lastYzzh = Integer.parseInt(r.get("YZZH_SHOW").toString());
					lastDate = BSHISUtil.toDate(r.get("KSSJ").toString());
					r.put("KSSJ", lastDate);
				}
				if ("create".equals(op)
						&& (r.get("YZZH") == null
								|| r.get("YZZH").toString().length() == 0 || r
								.get("YZZH").toString().equals("0"))) {// 新增的记录且没有YZZH
					r.put("YZZH", YZZH);

				}
				if (r.get("TZSJ") != null
						&& r.get("TZSJ").toString().length() > 0) {
					r.put("TZSJ", BSHISUtil.toDate(r.get("TZSJ").toString()));
					if (BSHISUtil.dateCompare((Date) r.get("TZSJ"),
							(Date) r.get("KSSJ")) < 0) {
						throw new ModelDataOperationException("停嘱时间不能小于开嘱时间!");
					}
				}
				if (("1").equals(r.get("LSYZ") + "")) {
					r.put("TZSJ", r.get("KSSJ"));
					r.put("TZYS", r.get("YSGH") + "");
				}
				// r.put("BRKS", Long.parseLong(brxx.get("BRKS") == null ? "0"
				// : brxx.get("BRKS").toString()));
				// r.put("BRBQ", Long.parseLong(brxx.get("BRBQ") == null ? "0"
				// : brxx.get("BRBQ").toString()));
				// r.put("BRCH", brxx.get("BRCH"));
				// r.put("SRKS", Long.parseLong(brxx.get("BRBQ") == null ? "0"
				// : brxx.get("BRBQ").toString()));
				Integer yplx = (Integer) r.get("YPLX");
				if (yplx >= 1) {// 药品
					r.put("JFBZ", 1);
					if ("1".equals(r.get("ZFYP") + "")
							&& (r.get("YFSB") == null || "".equals(r
									.get("YFSB")))) {
						r.put("YFSB", 0);
					}
					String  zl_sql = "select YCYL as YCYL,YPMC as YPMC,KSBZ as KSBZ from YK_TYPK where YPXH=:ypxh";
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("ypxh", (long)(Integer)(r.get("YPXH")));
					Map<String, Object> map_zl = dao.doLoad(zl_sql, map_par);
					double yrxl = ((BigDecimal) map_zl.get("YCYL")).doubleValue();
					double ycsl = Double.valueOf(r.get("YCSL").toString());
					if(map_zl !=null && yrxl != 0){
						sysl = sysl + ycsl;
						if(sysl>yrxl){
							throw new ModelDataOperationException("抗菌药物"+map_zl.get("YPMC").toString()+"药品的录入总量超过一日限量!");
						}
//						System.out.println(r.get("JZXH"));
//						if((Integer)map_zl.get("KSBZ")==1){
//							String  spyl_sql = "select SPYL as SPYL from AMQC_SYSQ01 where  YPXH=:ypxh";
//							Map<String, Object> map_spyl = dao.doLoad(spyl_sql, map_par);
//							if(map_spyl !=null){
//								double spyl = (Double)map_spyl.get("SPYL");
//								if(ycsl>spyl){
//									throw new ModelDataOperationException("录入总量超过审批用量!");
//								}
//							}
//						}
					}
					if ((!("1".equals(r.get("SYBZ").toString())
							|| (r.get("QRSJ") != null && r.get("QRSJ")
									.toString().length() > 0) || (r.get("TZSJ") != null && r
							.get("TZSJ").toString().length() > 0)))
							&& !"1".equals(r.get("ZFYP") + "")
							&& "3".equals(jckcgl)) {
						prarms.put("pharmId", r.get("YFSB"));
						prarms.put("medId", r.get("YPXH"));
						prarms.put("medsource", r.get("YPCD"));
//						prarms.put(
//								"quantity",
//								Double.parseDouble(r.get("YCSL").toString())
//										* (Integer.parseInt(r.get("SRCS")
//												.toString())));
						//update by caijy for 家床里面总量就是总量 不需要乘以次数
						prarms.put(
								"quantity",
								Double.parseDouble(r.get("YCSL").toString()));
						prarms.put("lsjg", r.get("YPDJ"));
						Map<String, Object> ret = BSPHISUtil.checkInventory(
								prarms, dao, ctx);
						if ((Integer) ret.get("sign") == -1) {
							throw new ModelDataOperationException("药品【"
									+ r.get("YZMC")
									+ "】库存数量不足，库存数量："
									+ (ret.get("KCZL") == null ? 0
											: ret.get("KCZL")) + ",实际数量："
									+ prarms.get("quantity"));
						}
					}

				} else if (yplx == 0) {// 项目
					if (Integer.parseInt(r.get("JFBZ").toString()) != 3) {
						if (r.get("midifyYjzx") == null) {
							if (lastYzzh != (Integer) r.get("YZZH_SHOW")) {// 同组的第一项
								r.put("YJZX", 1);
								r.put("YZPX", 1);
								yzpx = 0;
							} else {
								r.put("YJZX", 0);
								// r.put("YZPX", ++yzpx);
							}
						}
					} else {
						r.put("YPXH", 0l);
						r.put("YJXH", 0l);
					}
					r.put("YJXH", 0l);
					r.put("YPYF", 0l);
					r.put("YPCD", 0l);
					r.put("YCJL", 0);
					r.put("YFSB", 0l);
				}
				Map<String, Object> where = new HashMap<String, Object>();
				where.put("LSBZ", 0);
				// 不修改确认时间，防止时间格式错误
				r.remove("QRSJ");

				// 项目 计费标志处理
				// add by liyunt
				if (r.get("YPLX") != null
						&& r.get("YPLX").toString().equals("0")
						&& !r.get("JFBZ").toString().equals("3")
				// && !r.get("JFBZ").toString().equals("2") 暂时去掉，看下有什么影响
				) {
					String jfbz = getJFBZByYJSYFYXH(r, ctx);
					r.put("JFBZ", jfbz);
					if ("9".equals(jfbz)) {
						r.put("ZXKS", null);
					}
				}
				r.put("YZPX", ++yzpx);
				String uniqueIdStr = r.get("uniqueId") + "";
				r.remove("uniqueId");
				if (SBXH != null && op.equals("create")) {
					r.put("CCJL", SBXH);
				}
				Map<String, Object> jlxhMap = dao.doSave(op,
						"phis.application.fsb.schemas.JC_BRYZ_CQ", r, where,
						false);
				r.put("uniqueId", uniqueIdStr);
				if (yplx == 0) {// 项目
					if (wpjfbz == 1 && wzsfxmjgzy == 1) {
						if (("1").equals(r.get("LSYZ") + "")) {
							if ("create".equals(op)) {
								BSPHISUtil.setSuppliesZY(dao, ctx, Long
										.parseLong(jlxhMap.get("JLXH") + ""));
							} else if ("update".equals(op)) {
								BSPHISUtil.setSuppliesZY(dao, ctx,
										Long.parseLong(r.get("JLXH") + ""));
							}
						}
					}
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("病区医嘱信息校验失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("病区医嘱保存失败!", e);
		} catch (NumberFormatException e) {
			throw new ModelDataOperationException("病区医嘱保存失败!", e);
		} catch (KeyManagerException e) {
			throw new ModelDataOperationException("病区医嘱保存失败!", e);
		}

	}

	public String getJFBZByYJSYFYXH(Map<String, Object> data, Context ctx)
			throws ModelDataOperationException,
			PersistentDataOperationException {
		Integer yjsy = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		if (data.get("JGID") != null && data.get("YPXH") != null) {
			String hql = "select   a.YJSY  as YJSY from"
					+ " GY_YLSF a,GY_FYBM b,GY_YLMX c "
					+ "where a.FYXH=b.FYXH " + "and a.FYXH=c.FYXH "
					+ "and c.ZFPB=0  " + "and a.ZFPB=0 " + "and a.ZYSY=1 "
					+ "and c.JGID=:JGID " + "and a.FYXH = :FYXH";
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("JGID", manageUnit);
			p.put("FYXH", Long.parseLong(data.get("YPXH").toString()));
			List<Map<String, Object>> r = dao.doQuery(hql, p);
			if (r.size() > 0) {
				yjsy = (Integer) r.get(0).get("YJSY");
			}
		}
		if (yjsy != null && yjsy == 1) {
			Map<String, Object> zxks = this.doGetZXKSByYPXH(data, ctx);
			if (zxks != null) {
				if (zxks.get("YJSY") != null
						&& "1".equals(zxks.get("YJSY").toString())) {
					return "1";
				} else {
					return "2";
				}
			} else {
				return "9";
			}
		} else {
			return "2"; // 既可由医技记费,也可由病区记费
		}
	}

	/**
	 * 获取执行科室
	 */
	public Map<String, Object> doGetZXKSByYPXH(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String hql = "select b.ID as ID,b.MEDICALLAB as YJSY FROM GY_YLMX a, SYS_Office b where a.FYKS = b.ID and a.FYXH = :FYXH and a.JGID = :JGID";
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("FYXH", ((Integer) body.get("YPXH")).longValue());
			params.put("JGID", manageUnit);
			List<Map<String, Object>> list = dao.doQuery(hql, params);
			if (list.size() > 0)
				return list.get(0);
			else
				return null;
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("获取执行科室错误!", e);
		}
	}

	public void savePersonalComboExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String SFQY = body.get("SFQY") + "";
		String sql = "ZTBH=:ZTBH";
		try {
			parameters.put("ZTBH", Long.parseLong(body.get("ZTBH").toString()));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(SFQY)) {
					// 启用
					parameters.put("SFQY", 0);
					dao.doUpdate("update " + schemaList
							+ " set SFQY=:SFQY where ZTBH=:ZTBH", parameters);
					res.put(Service.RES_CODE, 601);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("SFQY", 1);
					dao.doUpdate("update " + schemaList
							+ " set SFQY=:SFQY WHERE ZTBH=:ZTBH", parameters);
					res.put(Service.RES_CODE, 602);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 603);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/**
	 * 家床诊疗计划组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSavePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			for (int i = 0; i < body.size(); i++) {
				String op = (String) body.get(i).get("_opStatus");
				if ("3".equals(body.get(i).get("XMLX").toString())) {
					body.get(i).put("XMBH", 0);
				}
				dao.doSave(op, BSPHISEntryNames.JC_ZL_ZT02, body.get(i), false);
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
	}

	/**
	 * 处方组套明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doRemovePrescriptionDetails(Map<String, Object> body,
			long pkey, String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = "ZTBH=:ZTBH";
		String sql1 = "ZTBH=:ZTBH and SFQY=1";// 判断是否启用的sql
		try {
			dao.doRemove(pkey, "phis.application.fsb.schemas.JC_ZL_ZT02");
			parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			if (l == 0) {
				Long l1 = dao.doCount(schemaList, sql1, parameters);
				if (l1 > 0) {// 如果启用了 才取消启动
					dao.doUpdate(
							"update JC_ZL_ZT01 set SFQY=0 where ZTBH=:ZTBH",
							parameters);
					res.put(Service.RES_CODE, 604);
					res.put(Service.RES_MESSAGE, "该组套明细为空，自动取消启用");
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		}

	}

	/*
	 * 病历模板启用功能
	 */
	public void saveMedicalTemplateExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String QYBZ = body.get("QYBZ") + "";
		String sql = "JLXH=:JLXH and ZSXX is not null and XBS is not null";
		try {
			parameters.put("JLXH", body.get("JLXH"));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(QYBZ)) {
					// 启用
					parameters.put("QYBZ", 0);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ where JLXH=:JLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("QYBZ", 1);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ WHERE JLXH=:JLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 607);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/*
	 * 诊疗方案启用功能
	 */
	public void saveTherapeuticRegimenExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String QYBZ = body.get("QYBZ") + "";
		String sql = "ZLXH=:ZLXH and (BLMBBH is not null or CFZTBH is not null or XMZTBH is not null or JBXH is not null)";
		try {
			parameters.put("ZLXH", body.get("ZLXH"));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(QYBZ)) {
					// 启用
					parameters.put("QYBZ", 0);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ where ZLXH=:ZLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("QYBZ", 1);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ WHERE ZLXH=:ZLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 607);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	// 删除组套时判断组套明细是否有值 如果有 提示不能删
	public void removePrescriptionDel(Map<String, Object> body,
			Map<String, Object> res, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String sql = "ZTBH=:ZTBH";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
		try {
			Long l = dao.doCount("YS_MZ_ZT02", sql, parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "组套明细已存在，无法删除");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "组套明细校验失败");
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doSavePatientInfo(Long zyh,
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> resMap = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Map<String, Object> patientBase = (Map<String, Object>) body
				.get("patientBaseTab");
		List<Map<String, Object>> patientAllergyMeds = (List<Map<String, Object>>) body
				.get("patientAllergyMedTab");
		String op = "update";
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			// 基础信息
			if (patientBase != null) {
				dao.doSave("update", "phis.application.fsb.schemas.JC_BRXX",
						patientBase, false);
			}
			// 过敏药物
			if (patientAllergyMeds != null && patientAllergyMeds.size() > 0) {
				for (Map<String, Object> AllergyMed : patientAllergyMeds) {
					op = (String) AllergyMed.get("_opStatus");
					// AllergyMed.put("ZYH", zyh);
					AllergyMed.put("JGID", manageUnit);
					if ("remove".equals(op)) {
						parameters.clear();
						parameters.put("BRID", Long.parseLong(AllergyMed.get(
								"BRID").toString()));
						parameters.put("JGID", manageUnit);
						parameters.put("YPXH", Long.parseLong(AllergyMed.get(
								"YPXH").toString()));
						StringBuffer delSql = new StringBuffer(
								"delete from GY_PSJL");
						delSql.append(" where BRID=:BRID and YPXH=:YPXH and JGID=:JGID");
						dao.doUpdate(delSql.toString(), parameters);
					} else {
						if ("update".equals(op)) {
							parameters.clear();
							parameters.put("BRID", Long.parseLong(AllergyMed
									.get("BRID").toString()));
							parameters.put("JGID", manageUnit);
							parameters.put("YPXH", Long.parseLong(AllergyMed
									.get("YPXH").toString()));
							parameters.put("BLFY", AllergyMed.get("BLFY"));
							parameters.put("GMZZ", AllergyMed.get("GMZZ"));
							parameters.put("QTZZ", AllergyMed.get("QTZZ"));
							if (AllergyMed.get("YPXH_NEW") != null
									&& AllergyMed.get("YPXH_NEW").toString()
											.length() > 0) {
								parameters.put(
										"YPXH_NEW",
										Long.parseLong(AllergyMed.get(
												"YPXH_NEW").toString()));
							} else {
								parameters.put("YPXH_NEW", Long
										.parseLong(AllergyMed.get("YPXH")
												.toString()));
							}
							dao.doUpdate(
									"update GY_PSJL set YPXH=:YPXH_NEW,BLFY=:BLFY,GMZZ=:GMZZ,QTZZ=:QTZZ where BRID=:BRID and YPXH=:YPXH and JGID=:JGID",
									parameters);
						} else {
							dao.doSave(op,
									"phis.application.fsb.schemas.GY_PSJL_JC",
									AllergyMed, false);
						}
					}
				}
			}

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存病人信息失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("保存病人信息失败!", e);
		}
		return resMap;
	}

	public List<Map<String, Object>> doLoadPlanDetails(Map<String, Object> body)
			throws ModelDataOperationException {
		Object ztbh = body.get("ZTBH");
		if (ztbh != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ZTBH", Long.parseLong(ztbh.toString()));
			String sql = "select a.JLBH as JLBH,a.YPZH,a.XMBH,a.XMMC,a.XMLX,a.YCJL,a.SYPC,a.MRCS,a.YYTS,a.GYTJ,a.KSSJ,a.JSSJ,a.BZXX,b.JLDW from JC_ZL_ZT02 a left join YK_TYPK b on a.XMBH=b.YPXH where a.ZTBH=:ZTBH order by a.YPZH,a.JLBH";
			try {
				List<Map<String, Object>> list = dao.doSqlQuery(sql, params);
				SchemaUtil.setDictionaryMassageForList(list,
						"phis.application.fsb.schemas.JC_ZLJH");
				return list;
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("获取诊疗计划明细信息错误!", e);
			}
		}
		return null;
	}

	/**
	 * 保存家床病人诊疗计划
	 * 
	 * @Description:
	 * @param body
	 * @param ctx
	 * @author YuBo 2015-3-5 下午3:59:42
	 * @Modify:
	 */
	public void doSavePlanDetails(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		try {
			for (int i = 0; i < body.size(); i++) {
				String op = (String) body.get(i).get("_opStatus");
				if ("3".equals(body.get(i).get("XMLX").toString())) {
					body.get(i).put("XMBH", 0);
				}
				dao.doSave(op, BSPHISEntryNames.JC_ZLJH, body.get(i), false);
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	public void doSaveCaclLsbz(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			// 将可以转成历史的医嘱转成历史医嘱
			List<Map<String, Object>> listGY_SYPC = BSPHISUtil
					.u_his_share_yzzxsj(null, dao, ctx);
			List<Map<String, Object>> bryzs = dao
					.doQuery(
							"select JLXH as JLXH,YPLX as al_ypbz,KSSJ as ldt_kssj,QRSJ as ldt_qrsj,TZSJ as ldt_tzsj,LSYZ as ll_lsyz,SYPC as ls_sypc,YZZXSJ as ls_yzzxsj_str from "
									+ "JC_BRYZ where ZYH=:ZYH and LSBZ<>1",// yangl历史医嘱转化时过滤YZPB<>4
							// + " where ZYH=:ZYH and LSBZ<>1 and YZPB<>4",//
							// yangl历史医嘱转化时过滤YZPB<>4
							parameters);

			for (Map<String, Object> bryz : bryzs) {
				int lsbz = BSPHISUtil.uf_cacl_lsbz(listGY_SYPC, bryz, dao, ctx);
				if (lsbz == 1) {
					Map<String, Object> p = new HashMap<String, Object>();
					p.put("JLXH", bryz.get("JLXH"));
					dao.doUpdate("update JC_BRYZ set LSBZ=1 where JLXH=:JLXH",
							p);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException("计算历史医嘱失败!.", e);
		}

	}

	public void doSaveCancelSickBed(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			Long count = dao
					.doCount(
							"JC_BRYZ",
							"( ZYH = :ZYH ) AND (TJZX <> 2) AND ( JFBZ < 3 OR JFBZ = 9 )",
							parameters);
			if (count > 0) {
				throw new ModelDataOperationException("病人有未停未发医嘱，不能进行撤床操作!");
			}
			Long ty = dao
					.doCount(
							"JC_TYMX",
							"(TJBZ=1 or TYRQ is null) and ZYH=:ZYH",
							parameters);
			if (ty > 0) {
				throw new ModelDataOperationException("该病人存在未提交或未确认的退药申请，不能进行撤床操作!");
			}
			List<Map<String, Object>> l = dao.doSqlQuery(
					"select distinct ZDLB from JC_BRZD where ZYH=:ZYH",
					parameters);
			if (l.size() < 2) {
				throw new ModelDataOperationException(
						"建床诊断和撤床诊断未录入完全，不能进行撤床操作!");
			}
			dao.doSave("update", BSPHISEntryNames.JC_BRRY + "_CC", body, false);

		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException("撤床证明保存失败.", e);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException("撤床证明保存失败.", e);
		}

	}

	/**
	 * 医生站医嘱提交
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int doSaveDocSubmit(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		try {
			// modify by yangl 2014.2.26 增加抗菌药物判断
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("LSYZ", body.get("LSYZ"));
			params.put("ZYH", body.get("ZYH"));
			params.put("JGID", body.get("JGID"));
			// doCheckAntibacterial(params, ctx);
			body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			body.put("YSGH", (String) user.getUserId());
			body.put("JGID", manageUnit);

			return dao
					.doUpdate(
							"update JC_BRYZ SET YSTJ=1 where YSGH=:YSGH and YSTJ=0  and YSBZ=1 and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID",
							body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("住院医嘱提交失败!", e);
		}
	}

	/**
	 * 校验抗菌药物是否审批
	 * 
	 * @param body
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	public String doCheckAntibacterial(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String QYKJYWGL = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYKJYWGL, ctx);
		String QYZYKJYWSP = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYZYKJYWSP, ctx);
		body.put("JGID", manageUnit);
		if ("1".equals(QYKJYWGL) && "1".equals(QYZYKJYWSP)
				&& "1".equals(body.get("LSYZ").toString())) {// 参数启用且是临时医嘱时
			// 查询医嘱中所有需要审批的抗菌药物的数量
			String sql = "select b.YPXH as YPXH,b.YPMC as YPMC,sum(a.YCSL) as TOTALSL from JC_BRYZ a,YK_TYPK b ,AMQC_SYSQ01 c   where a.YPXH=b.YPXH  and a.ypxh = c.ypxh   and a.zyh =  c.jzxh  and c.jzlx = 6  and c.spjg = 0  and c.ZFBZ = 0 and b.KSBZ=1 and b.SFSP=1 and a.LSBZ =  0 and a.LSYZ=:LSYZ and a.YPXH=:YPXH and a.ZYH=:ZYH and a.JGID=:JGID group by b.YPXH,b.YPMC";
			try {
				List<Map<String, Object>> list = dao.doSqlQuery(sql, body);

				for (Map<String, Object> ypxx : list) {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("ZYH", body.get("ZYH").toString());
					// params.put("YSGH", body.get("YSGH"));
					params.put("JGID", manageUnit);
					params.put("YPXH",
							Long.parseLong(ypxx.get("YPXH").toString()));
					// 不论是谁申请，只要审批通过，其他医生也能使用
					Map<String, Object> qmqc_sysq01 = dao
							.doLoad("select sum(SPYL) as SPYL from AMQC_SYSQ01 where JZLX=6 and JZXH=:ZYH and YPXH=:YPXH and (SPJG=1 or SPJG=2) and ZFBZ=0 and JGID=:JGID group by YPXH",
									params);
					if (qmqc_sysq01 == null
							|| qmqc_sysq01.get("SPYL") == null
							|| qmqc_sysq01.get("SPYL").toString().trim()
									.length() == 0) {
						return "抗菌药物【" + ypxx.get("YPMC") + "】尚未审批或审批未通过!";
					} else {
						if (Double.parseDouble(ypxx.get("TOTALSL").toString()) > Double
								.parseDouble(qmqc_sysq01.get("SPYL").toString())) {
							return "抗菌药物【" + ypxx.get("YPMC")
									+ "】的用量超过审批用量，<br />已审批用量为"
									+ qmqc_sysq01.get("SPYL").toString()
									+ "!当前总用量为"
									+ ypxx.get("TOTALSL").toString();
						}
					}
				}
			} catch (NumberFormatException e) {
				throw new ModelDataOperationException("获取抗菌药物审批状态失败!", e);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("获取抗菌药物审批状态失败!", e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void doLoadAmqcCount(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		// List<Object> cnd = (List<Object>) req.get("cnd");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long ypxh = Long.parseLong(body.get("YPXH") + "");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", zyh);
			parameters.put("YPXH", ypxh);
			parameters.put("KSSJ", body.get("KSSJ"));
			List<Map<String, Object>> list = dao
					.doQuery(
							"select sum(YCSL) as SYSL from JC_BRYZ where ZYH=:ZYH and YPXH=:YPXH and str(KSSJ,'yyyy-mm-dd')=:KSSJ",
							parameters);
			if (list == null || list.size() == 0) {
				res.put("SYSL", 0);
			} else {
				res.put("SYSL", list.get(0).get("SYSL"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取抗菌药物使用数量错误!", e);
		}
	}
	
	/**
	 * 家床护士站退回医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int doSaveGoback(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			body.put("JLXH", Long.parseLong(body.get("JLXH").toString()));
			return dao.doUpdate(
					"update JC_BRYZ SET YSTJ=0 where JLXH=:JLXH and YSTJ=1",
					body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("家床医嘱退回失败!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public Object doLoadPlanSet(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnitId();
			Map<String, Object> yfxx = new HashMap<String, Object>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JGID", manageUnit);
			List<Map<String, Object>> fyyf = dao
					.doQuery(
							"select a.DMSB as DMSB,a.YFSB as YFSB,b.YFMC as YFMC from "
									+ "JC_FYYF a,YF_YFLB b where a.YFSB=b.YFSB and a.JGID=:JGID and a.TYPE=1 and a.ZXPB=0 and a.GNFL=4",
							params);
			for (Map<String, Object> yf : fyyf) {
				yfxx.put(yf.get("DMSB").toString(), yf.get("YFSB"));
			}
			List<Map<String, Object>> rds = (List<Map<String, Object>>) req
					.get("body");
			Map<String, Object> brxx = (Map<String, Object>) req.get("brxx");
			for (Map<String, Object> r : rds) {
				// 判断类型
				Object xmlx = r.get("JHLX"); // 项目类型 | 1：药品 2：项目 3：行为
				Object type = r.get("TYPE"); // 药品类别 | 1：西药 2：中成药 3：中草药
				if (xmlx != null && xmlx.toString().equals("1")) {// 药品调入
					params.clear();
					params.put("YFSB",
							Long.parseLong(yfxx.get(type + "").toString()));
					params.put("YPXH", Long.parseLong(r.get("XMBH").toString()));
					StringBuffer hql = new StringBuffer(
							"select b.YFSB as YFSB,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YCJL as YCJL,a.YPJL as YPJL,a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as YPLX,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ,a.KSBZ as KSBZ,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.PSPB as PSPB,a.SFSP as SFSP,a.YCJL as YCJL ");
					hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f ");
					hql.append(" where  ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND  b.YFSB=:YFSB  AND a.YPXH=:YPXH ORDER BY d.SBXH");
					List<Map<String, Object>> meds = dao.doQuery(
							hql.toString(), params);
					if (meds.size() > 0) {// 多产地取第一条记录
						Map<String, Object> zt_med = meds.get(0);
						// zt_med.putAll(med);
						Dictionary dic = DictionaryController.instance().get(
								"phis.dictionary.drugMode");
						if (zt_med.get("GYFF") != null
								&& zt_med.get("GYFF") != "null") {
							String gYFF_text = dic
									.getText((zt_med.get("GYFF") + ""));
							zt_med.put("GYFF_text", gYFF_text);
						}
						brxx.put("YPXH", zt_med.get("YPXH"));
						brxx.put("YPLX", zt_med.get("YPLX"));
						zt_med.putAll(doLoadDetailsInfo(brxx, ctx));
						r.putAll(zt_med);
					} else {
						r.put("errorMsg", "暂无库存!");
					}
				} else if (xmlx != null && xmlx.toString().equals("2")) {
					params.clear();
					params.put("FYXH", Long.parseLong(r.get("XMBH").toString()));
					params.put("JGID", manageUnit);
					StringBuffer hql = new StringBuffer(
							"select b.FYXH as FYXH,b.FYMC as FYMC,c.FYDJ as FYDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS,b.MZSY as MZSY,0 as YPLX");
					hql.append(" from GY_YLSF b,GY_YLMX c ");
					hql.append(" where b.FYXH = c.FYXH and c.JGID=:JGID and b.FYXH=:FYXH");
					try {
						List<Map<String, Object>> meds = dao.doQuery(
								hql.toString(), params);
						for (Map<String, Object> med : meds) {
							if (!"1".equals(med.get("MZSY") + "")) {
								med.put("errorMsg", "不存在!");
								continue;
							}
							if (med.get("FYKS") != null
									&& med.get("FYKS") != "") {
								med.put("FYKS_text",
										med.get("FYKS") == null ? ""
												: DictionaryController
														.instance()
														.get("phis.dictionary.department_mzyj")
														.getText(
																med.get("FYKS")
																		.toString()));
							}
							r.putAll(med);
						}
					} catch (Exception e) {
						throw new ModelDataOperationException("数据异常，请联系管理员!", e);
					}
				}
			}

			return rds;
		} catch (ControllerException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("载入诊疗计划失败!", e);
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("载入诊疗计划失败!", e);
		}
	}

	/**
	 * 保存家床诊断信息
	 * 
	 * @Description:
	 * @param body
	 * @author yangl 2015-4-8 下午5:34:56
	 * @Modify:
	 */
	public void doSaveClinicDiagnossis(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		for (Map<String, Object> r : body) {
			String op = r.get("JLBH") == null ? "create" : "update";
			try {
				UserRoleToken user = UserRoleToken.getCurrent();
				String manageUnit = user.getManageUnitId();
				if (op.equals("create")) {
					r.put("JGID", manageUnit);
					r.put("ZDYS", user.getUserId());
				}
				dao.doSave(op, BSPHISEntryNames.JC_BRZD, r, false);
			} catch (ValidateException e) {
				throw new ModelDataOperationException("保存家床诊断信息失败!", e);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("保存家床诊断信息失败!", e);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void doUpdateAdviceStatus(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		boolean isGroup = (Boolean) req.get("isGroup");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
		params.put("YZZH", Long.parseLong(body.get("YZZH").toString()));
		String hql = "update JC_BRYZ set LSBZ=0,TZSJ=null,TZYS=null where ZYH=:ZYH and YZZH=:YZZH";
		if (!isGroup) {
			hql += " and JLXH=:JLXH";
			params.put("JLXH", Long.parseLong(body.get("JLXH").toString()));
		}
		try {
			dao.doUpdate(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("取消医嘱停嘱失败", e);
		}
	}

	public void doRemoveFsbCheck(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		if (body.get("ZYH") == null || body.get("CCXH") == null) {
			logger.error("住院号或者查床记录为空!");
			throw new ModelDataOperationException("无效的请求参数!");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
		params.put("CCJL", Long.parseLong(body.get("CCXH").toString()));
		try {
			Long l = dao.doCount("JC_BRYZ", "ZYH=:ZYH and CCJL=:CCJL", params);
			if (l > 0) {
				throw new ModelDataOperationException("当前查床记录下存在医嘱信息，不允许删除!");
			}
			l = dao.doCount("JC_BRZD", "ZYH=:ZYH and CCXH=:CCJL", params);
			if (l > 0) {
				throw new ModelDataOperationException("当前查床记录下存在诊断信息，不允许删除!");
			}
			dao.doRemove(Long.parseLong(body.get("CCXH").toString()),
					BSPHISEntryNames.JC_CCJL);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除查询记录失败", e);
		}

	}

	/**
	 * 获取已经审批的数量
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doCheckApplyInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			body.put("JZXH", body.get("JZXH").toString());
			body.put("YPXH", Long.parseLong(body.get("YPXH").toString()));
			Map<String, Object> map1 = dao
					.doLoad("select sum(SPYL) as SPYL from AMQC_SYSQ01 where JZLX=6 and JZXH=:JZXH and YPXH=:YPXH and (SPJG=1 or SPJG=2) and ZFBZ=0 and JGID=:JGID group by YPXH",
							body);
			body.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			Map<String, Object> map2 = dao
					.doLoad("select sum(YCSL) as SYSL from JC_BRYZ where ZYH=:JZXH and YPXH=:YPXH and ZFBZ=0 and JGID=:JGID group by YPXH",
							body);
			Double spsl = 0.0;
			Double sysl = 0.0;
			if (map1 != null && map1.get("SPYL") != null
					&& map1.get("SPYL").toString().length() > 0) {
				spsl = Double.parseDouble(map1.get("SPYL").toString());
			}
			if (map2 != null && map2.get("SYSL") != null
					&& map2.get("SYSL").toString().length() > 0) {
				sysl = Double.parseDouble(map2.get("SYSL").toString());
			}
			res.put("SPSL", spsl - sysl > 0 ? spsl - sysl : 0);
			return res;
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("保存抗菌药物信息失败!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doLoadStopPatients(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String, Object>> cnd = (List<Map<String, Object>>) body
				.get("cnd");
		String openBy = (body.get("openBy") == null ? "nurse" : body.get(
				"openBy").toString());
		Integer ysbz = openBy.equals("nurse") ? 0 : 1;
		try {
			String where = ExpressionProcessor.instance().toString(cnd);
			// dao.doList(cnd, "", BSPHISEntryNames.JC_BRYZ);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("TJZRQ", BSHISUtil.getDate());
			parameters.put("YSBZ", ysbz);
			String sql = "select a.ZYH as ZYH,a.BRXM as BRXM from JC_BRRY a where exists(select 1 from JC_BRYZ b where ZYH=a.ZYH and b.TJZRQ=:TJZRQ and b.TZSJ is null and YSBZ=:YSBZ)  and "
					+ where;
			return dao.doSqlQuery(sql, parameters);
		} catch (ExpException e) {
			throw new ModelDataOperationException("解析表达式错误!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询需停嘱病人失败!", e);
		}
	}
}
