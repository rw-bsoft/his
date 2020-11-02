package phis.application.sup.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class TreasuryMonthlyModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreasuryMonthlyModel.class);

	public TreasuryMonthlyModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 采购入库时间条件查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<String> dateQuery(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<String> l = new ArrayList<String>();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int year = parseInt(body.get("year"));
		int month = parseInt(body.get("month"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
		StringBuffer hql_qszz = new StringBuffer();
		hql_qszz.append("select distinct QSSJ as QSSJ,ZZSJ as ZZSJ from ")
				.append("WL_YJJL")
				.append(" where CWYF=:cwyf and KFXH=:kfxh and CSBZ=0");
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 10);
		a.set(Calendar.HOUR_OF_DAY, 0);
		a.set(Calendar.MINUTE, 0);
		a.set(Calendar.SECOND, 0);
		Map<String, Object> map_par_qszz = new HashMap<String, Object>();
		map_par_qszz.put("cwyf", sdfcwyf.format(a.getTime()));
		map_par_qszz.put("kfxh", kfxh);
		try {
			Map<String, Object> map_qszz = dao.doLoad(hql_qszz.toString(),
					map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null
					&& map_qszz.get("ZZSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("QSSJ")));
				l.add(sdf.format((Date) map_qszz.get("ZZSJ")));
				return l;
			}
			a.set(Calendar.MONTH, month - 2);
			map_par_qszz.put("cwyf", sdfcwyf.format(a.getTime()));
			map_qszz = dao.doLoad(hql_qszz.toString(), map_par_qszz);
			if (map_qszz != null && map_qszz.get("QSSJ") != null) {
				l.add(sdf.format((Date) map_qszz.get("QSSJ")));
			}
			int yjDate = parseInt(ParameterUtil.getParameter(jgid, "YJSJ_KF"
					+ kfxh, "32", "库房月结时间 对应一个月的31天  32为月底结 ", ctx));
			if (l.size() == 0) {
				int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (yjDate < lastDay) {
					a.set(Calendar.DATE, yjDate + 1);
				} else {
					a.set(Calendar.DATE, lastDay + 1);
				}
				a.set(Calendar.HOUR_OF_DAY, 0);
				a.set(Calendar.MINUTE, 0);
				a.set(Calendar.SECOND, 0);
				l.add(sdf.format(a.getTime()));
			}
			a.set(Calendar.MONTH, month - 1);
			int lastDay = a.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (yjDate < lastDay) {
				a.set(Calendar.DATE, yjDate);
			} else {
				a.set(Calendar.DATE, lastDay);
			}
			a.set(Calendar.HOUR_OF_DAY, 23);
			a.set(Calendar.MINUTE, 59);
			a.set(Calendar.SECOND, 59);
			l.add(sdf.format(a.getTime()));
		} catch (PersistentDataOperationException e) {
			logger.error("Date query fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "日期查询失败");
		}
		return l;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doSaveTreasuryMonthly(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "月结成功");
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
		int year = parseInt(body.get("year"));
		int month = parseInt(body.get("month"));
		int op = parseInt(body.get("op"));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DATE, 10);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		try {
			String cwyf = sdfcwyf.format(c.getTime());
			c.set(Calendar.MONTH, month - 2);
			String cwyfsy = sdfcwyf.format(c.getTime());
			if (op == 1) {
				// 查询当前月是否月结
				StringBuffer hql_sfyj = new StringBuffer();
				StringBuffer hql_syyj = new StringBuffer();
				hql_sfyj.append("CWYF=:cwyf and KFXH=:KFXH AND CSBZ=0");
				hql_syyj.append("CWYF=:cwyf and KFXH=:KFXH");
				Map<String, Object> map_par_sfyj = new HashMap<String, Object>();
				map_par_sfyj.put("cwyf", cwyf);
				map_par_sfyj.put("KFXH", kfxh);
				long l = dao.doCount("WL_YJJL", hql_sfyj.toString(),
						map_par_sfyj);
				if (l > 0) {
					ret.put("code", 9001);
					ret.put("msg", "本月已过账，是否取消本月过账?");
					return ret;
				}
				// 查询上个月是否月结
				map_par_sfyj.put("cwyf", cwyfsy);
				l = dao.doCount("WL_YJJL", hql_syyj.toString(), map_par_sfyj);
				if (l == 0) {
					ret.put("code", 9000);
					ret.put("msg", "上月未结账,本月过账无法进行,请先做上一月的结账处理!");
					return ret;
				}
				List<String> list_qszzsj = dateQuery(body, ctx);
				if (sdf.parse(list_qszzsj.get(1)).getTime() > new Date()
						.getTime()) {
					ret.put("code", 9002);
					ret.put("msg", "设定月结截止日为" + list_qszzsj.get(1) + " 提前月结将使"
							+ sdf.format(new Date()) + "之后的业务做账到下个月, 是否继续?");
					return ret;
				}
				yjcl(cwyf, cwyfsy, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)), c.getTime(), jgid, kfxh);
			} else if (op == 2) {// 取消月结
				ret.put("code", 200);
				qxyj(cwyf, ret, kfxh);
				return ret;
			} else {
				ret.put("code", 200);
				List<String> list_qszzsj = dateQuery(body, ctx);
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
				yjcl(cwyf, cwyfsy, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)),
						sdf.parse(sdf.format(c.getTime())), jgid, kfxh);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		} catch (ParseException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doSaveTreasuryEjMonthly(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("code", 200);
		ret.put("msg", "月结成功");
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfcwyf = new SimpleDateFormat("yyyyMM");
		int year = parseInt(body.get("year"));
		int month = parseInt(body.get("month"));
		int op = parseInt(body.get("op"));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DATE, 10);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		try {
			String cwyf = sdfcwyf.format(c.getTime());
			c.set(Calendar.MONTH, month - 2);
			String cwyfsy = sdfcwyf.format(c.getTime());
			if (op == 1) {
				// 查询当前月是否月结
				StringBuffer hql_sfyj = new StringBuffer();
				StringBuffer hql_syyj = new StringBuffer();
				hql_sfyj.append(" CWYF=:cwyf and KFXH=:KFXH and CSBZ=0");
				hql_syyj.append("CWYF=:cwyf and KFXH=:KFXH");
				Map<String, Object> map_par_sfyj = new HashMap<String, Object>();
				map_par_sfyj.put("cwyf", cwyf);
				map_par_sfyj.put("KFXH", kfxh);
				long l = dao.doCount("WL_YJJL", hql_sfyj.toString(),
						map_par_sfyj);
				if (l > 0) {
					ret.put("code", 9001);
					ret.put("msg", "本月已过账，是否取消本月过账?");
					return ret;
				}
				// 查询上个月是否月结
				map_par_sfyj.put("cwyf", cwyfsy);
				l = dao.doCount("WL_YJJL", hql_syyj.toString(), map_par_sfyj);
				if (l == 0) {
					ret.put("code", 9000);
					ret.put("msg", "上月未结账,本月过账无法进行,请先做上一月的结账处理!");
					return ret;
				}
				List<String> list_qszzsj = dateQuery(body, ctx);
				if (sdf.parse(list_qszzsj.get(1)).getTime() > new Date()
						.getTime()) {
					ret.put("code", 9002);
					ret.put("msg", "设定月结截止日为" + list_qszzsj.get(1) + " 提前月结将使"
							+ sdf.format(new Date()) + "之后的业务做账到下个月, 是否继续?");
					return ret;
				}
				yjclej(cwyf, cwyfsy, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)), c.getTime(), jgid, kfxh);
			} else if (op == 2) {// 取消月结
				ret.put("code", 200);
				qxyjej(cwyf, ret, kfxh);
				return ret;
			} else {
				ret.put("code", 200);
				List<String> list_qszzsj = dateQuery(body, ctx);
				c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
				yjclej(cwyf, cwyfsy, sdf.parse(list_qszzsj.get(0)),
						sdf.parse(list_qszzsj.get(1)),
						sdf.parse(sdf.format(c.getTime())), jgid, kfxh);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		} catch (ParseException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结取消
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doDeleteTreasuryMonthly(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		Map<String, Object> ret = new HashMap<String, Object>();
		String cwyf = body.get("CWYF") + "";
		qxyj(cwyf, ret, kfxh);
		return ret;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结取消
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doDeleteTreasuryEjMonthly(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		Map<String, Object> ret = new HashMap<String, Object>();
		String cwyf = body.get("CWYF") + "";
		qxyjej(cwyf, ret, kfxh);
		return ret;
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param cwyf
	 * @param kssj
	 * @param zzsj
	 * @param precwyf
	 * @throws ModelDataOperationException
	 */
	public void yjcl(String cwyf, String cwyfsy, Date kssj, Date zzsj,
			Date precwyf, String jgid, int kfxh)
			throws ModelDataOperationException {
		Map<String, Object> syyjjlandyjjgMap = new HashMap<String, Object>();
		syyjjlandyjjgMap.put("CWYF", cwyfsy);
		syyjjlandyjjgMap.put("KFXH", kfxh);
		Map<String, Object> byyjjlandyjjgMap = new HashMap<String, Object>();
		byyjjlandyjjgMap.put("CWYF", cwyf);
		byyjjlandyjjgMap.put("KFXH", kfxh);
		Map<String, Object> zcmxMap = new HashMap<String, Object>();
		zcmxMap.put("QSFSRQ", kssj);
		zcmxMap.put("ZZFSRQ", zzsj);
		zcmxMap.put("KFXH", kfxh);
		String hql_syyjjl = "select JGID as JGID,KFXH as KFXH,ZBLB as ZBLB,CWYF as CWYF,QSSJ as QSSJ,ZZSJ as ZZSJ,JZSJ as JZSJ,CSBZ as CSBZ from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF";// 上月月结记录
		String hql_syyjjg = "select a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.RKSL as RKSL,a.RKJE as RKJE,a.CKSL as CKSL,a.CKJE as CKJE,a.BSSL as BSSL,a.BSJE as BSJE,a.PYSL as PYSL,a.PYJE as PYJE,a.QMSL as QCSL,a.QMJE as QCJE,a.QMSL as QMSL,a.QMJE as QMJE,a.QCLSJE as QCLSJE,a.RKLSJE as RKLSJE,a.CKLSJE as CKLSJE,a.JCLSJE as JCLSJE from WL_YJJG a,WL_YJJL b where a.JZXH=b.JZXH and b.KFXH=:KFXH AND b.CWYF=:CWYF";// 上月月结结果
		String hql_byyjjl = "select JZXH as JZXH,JGID as JGID,KFXH as KFXH,ZBLB as ZBLB,CWYF as CWYF,QSSJ as QSSJ,ZZSJ as ZZSJ,JZSJ as JZSJ,CSBZ as CSBZ from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND CSBZ=0";// 本月月结记录
		String hql_byyjjgjzxh = "select a.JLXH as JLXH,a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.RKSL as RKSL,a.RKJE as RKJE,a.CKSL as CKSL,a.CKJE as CKJE,a.BSSL as BSSL,a.BSJE as BSJE,a.PYSL as PYSL,a.PYJE as PYJE,a.QCSL as QCSL,a.QCJE as QCJE,a.QMSL as QMSL,a.QMJE as QMJE,a.QCLSJE as QCLSJE,a.RKLSJE as RKLSJE,a.CKLSJE as CKLSJE,a.JCLSJE as JCLSJE from WL_YJJG a,WL_YJJL b where a.JZXH=b.JZXH and b.KFXH=:KFXH AND b.CWYF=:CWYF AND b.CSBZ=0";// 本月月结结果
		String hql_sylskszc = "select JGID as JGID,CWYF as CWYF,KSDM as KSDM,KFXH as KFXH,ZBLB as ZBLB,KCXH as KCXH,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,ZRRQ as ZRRQ,YKSL as YKSL from WL_LSKSZC where KFXH=:KFXH AND CWYF=:CWYF";// 上月历史科室账册
		String hql_bylskszc = "select JLXH as JLXH,JGID as JGID,CWYF as CWYF,KSDM as KSDM,KFXH as KFXH,ZBLB as ZBLB,KCXH as KCXH,WZXH as WZXH,CJXH as CJXH,SCRQ as SCRQ,SXRQ as SXRQ,WZPH as WZPH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,ZRRQ as ZRRQ,YKSL as YKSL from WL_LSKSZC where KFXH=:KFXH AND CWYF=:CWYF";// 本月历史科室账册
		String hql_zcmxrkywlb1 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='RK' AND YWFS<>1 and YWLB=1 group by ZBLB,WZXH,CJXH";// 入库业务类别为1
		String hql_zcmxrkywlb2 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='RK' AND YWFS<>1 and YWLB=-1 group by ZBLB,WZXH,CJXH";// 入库业务类别为-1
		String hql_zcmxckorslywlb1 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='CK' or DJLX='SL') AND YWFS<>1 and YWLB=1 group by ZBLB,WZXH,CJXH";// 出库或者申领
		// 业务类别为1
		String hql_zcmxckorslywlb2 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='CK' or DJLX='SL') AND YWFS<>1 and YWLB=-1 group by ZBLB,WZXH,CJXH";// 出库或者申领
		// 业务类别为-1
		String hql_zcmxbs = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='BS' AND YWFS=0 group by ZBLB,WZXH,CJXH";// 报损

		String hql_zcmxpy = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='RK' AND YWFS=1 group by ZBLB,WZXH,CJXH";// 盘盈入库
		String hql_zcmxpk = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='CK' AND YWFS=1 group by ZBLB,WZXH,CJXH";// 盘亏出库
		String hql_zcmxksckorslywlb1 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,KCXH as KCXH,KSDM as KSDM,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and GLFS>1 and (DJLX='CK' or DJLX='SL') and YWLB=-1 group by ZBLB,WZXH,CJXH,KCXH,KSDM";// 科室帐数据出库或者申领业务类别为-1;
		String hql_zcmxksckorslywlb2 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,KCXH as KCXH,KSDM as KSDM,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and GLFS>1 and (DJLX='CK' or DJLX='SL') and YWLB=1 group by ZBLB,WZXH,CJXH,KCXH,KSDM";// 科室帐数据出库或者申领业务类别为1;
		String hql_zcmxksbs = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,KCXH as KCXH,KSDM as KSDM,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and GLFS>1 and DJLX='BS' and YWFS=1 group by ZBLB,WZXH,CJXH,KCXH,KSDM";// 科室帐数据报损;
		String hql_syyjzc = "select JLXH as JLXH,JZXH as JZXH,KFXH as KFXH,ZBLB as ZBLB,CWYF as CWYF,ZBXH as ZBXH,WZXH as WZXH,WZZT as WZZT,ZYKS as ZYKS,CZYZ as CZYZ,JGID as JGID from WL_YJZC where KFXH=:KFXH AND CWYF=:CWYF";// 上月月结记录
		String hql_zcmxzbxh = "select distinct ZBXH as ZBXH From WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and GLFS=3 order by ZBXH";// 出库或者申领
		String zcmxsql = "select ZBXH as ZBXH,JGID as JGID,ZBLB as ZBLB,DJLX as DJLX,KFXH as KFXH,WZXH as WZXH,YWLB as YWLB,FSRQ as FSRQ,KSDM as KSDM,sum(WZJE) as WZJE from WL_ZCMX where  KFXH = :KFXH and ZBXH = :ZBXH and FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and GLFS = 3 and DJLX in ('RK','CK','SL','BS','ZK','TK') group by ZBXH,JGID,ZBLB,DJLX,KFXH,WZXH,YWLB,FSRQ,KSDM order by FSRQ";
		// String hql_zcmxzc =
		// "select MXXH as MXXH,JGID as JGID,KFXH as KFXH,ZBLB as ZBLB,DJXH as DJXH,LZFS as LZFS,YWLB as YWLB,DJLX as DJLX,FSRQ as FSRQ,MXJL as MXJL,GLFS as GLFS,WZXH as WZXH,CJXH as CJXH,WZSL as WZSL,WZJG as WZJG,WZJE as WZJE,WZPH as WZPH,MJPH as MJPH,SCRQ as SCRQ,SXRQ as SXRQ,KCXH as KCXH,ZBXH as ZBXH,YWFS as YWFS,KSDM as KSDM,YWGH as YWGH,MXSM as MXSM,CWYF as CWYF from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=ZZFSRQ and GLFS=3";//
		// 资产账数据
		Map<String, Object> updyjjg = new HashMap<String, Object>();
		Map<String, Object> updlskszc = new HashMap<String, Object>();
		Map<String, Object> getYJJLJZXH = new HashMap<String, Object>();
		Map<String, Object> saveyjjg = new HashMap<String, Object>();
		Map<String, Object> savelskszc = new HashMap<String, Object>();

		try {
			List<Map<String, Object>> syyjjlList = dao.doQuery(hql_syyjjl,
					syyjjlandyjjgMap);
			List<Map<String, Object>> syyjjgList = dao.doQuery(hql_syyjjg,
					syyjjlandyjjgMap);
			List<Map<String, Object>> sylskszcList = dao.doQuery(hql_sylskszc,
					syyjjlandyjjgMap);
			List<Map<String, Object>> zcmxrkywlb1List = dao.doQuery(
					hql_zcmxrkywlb1, zcmxMap);
			List<Map<String, Object>> zcmxrkywlb2List = dao.doQuery(
					hql_zcmxrkywlb2, zcmxMap);
			List<Map<String, Object>> zcmxckorslywlb1List = dao.doQuery(
					hql_zcmxckorslywlb1, zcmxMap);
			List<Map<String, Object>> zcmxckorslywlb2List = dao.doQuery(
					hql_zcmxckorslywlb2, zcmxMap);
			List<Map<String, Object>> zcmxbsList = dao.doQuery(hql_zcmxbs,
					zcmxMap);
			List<Map<String, Object>> zcmxpybList = dao.doQuery(hql_zcmxpy,
					zcmxMap);
			List<Map<String, Object>> zcmxpkList = dao.doQuery(hql_zcmxpk,
					zcmxMap);

			List<Map<String, Object>> zcmxksckorslywlb1List = dao.doQuery(
					hql_zcmxksckorslywlb1, zcmxMap);
			List<Map<String, Object>> zcmxksckorslywlb2List = dao.doQuery(
					hql_zcmxksckorslywlb2, zcmxMap);
			List<Map<String, Object>> zcmxksbs = dao.doQuery(hql_zcmxksbs,
					zcmxMap);
			List<Map<String, Object>> syyjzcList = dao.doQuery(hql_syyjzc,
					syyjjlandyjjgMap);
			List<Map<String, Object>> zcmxzbxh = dao.doQuery(hql_zcmxzbxh,
					zcmxMap);
			// List<Map<String, Object>> zcmxzcList = dao.doQuery(hql_zcmxzc,
			// zcmxMap);
			for (int i = 0; i < syyjjlList.size(); i++) {
				syyjjlList.get(i).put("CWYF", cwyf);
				/**
				 * 上月没有月结 这个月月结两次 开始时间和结束时间没有改变。（见BUG 649） Modified by dingcj
				 */
				String s1 = cwyf.substring(0, 4);
				String s2 = cwyf.substring(4, 6);
				String dataString = s1 + "-" + s2 + "-01";
				Date date = BSHISUtil.toDate(dataString);
				Date QSSJ = BSHISUtil.getFirstDayOfMonth(date);
				Date ZZSJ = BSHISUtil.getLastDayOfMonth(date);
				syyjjlList.get(i).put("QSSJ", QSSJ);
				syyjjlList.get(i).put("ZZSJ", ZZSJ);
				syyjjlList.get(i).put("CSBZ", 0);
				dao.doSave("create", BSPHISEntryNames.WL_YJJL,
						syyjjlList.get(i), false);
			}
			for (int i = 0; i < syyjzcList.size(); i++) {
				syyjzcList.get(i).put("CWYF", cwyf);
				dao.doSave("create", BSPHISEntryNames.WL_YJZC,
						syyjzcList.get(i), false);
			}
			List<Map<String, Object>> byyjjlList = dao.doQuery(hql_byyjjl,
					byyjjlandyjjgMap);
			for (int i = 0; i < byyjjlList.size(); i++) {
				for (int j = 0; j < syyjjgList.size(); j++) {
					if (parseInt(byyjjlList.get(i).get("ZBLB")) == parseInt(syyjjgList
							.get(j).get("ZBLB"))) {
						syyjjgList.get(j).put("JZXH",
								parseLong(byyjjlList.get(i).get("JZXH")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								syyjjgList.get(j), false);
					}
				}
			}
			for (int i = 0; i < sylskszcList.size(); i++) {
				sylskszcList.get(i).put("CWYF", cwyf);
				dao.doSave("create", BSPHISEntryNames.WL_LSKSZC,
						sylskszcList.get(i), false);
			}
			Map<String, Object> parzbhx = new HashMap<String, Object>();
			Map<String, Object> parzcmx = new HashMap<String, Object>();
			Map<String, Object> yjzcMap = new HashMap<String, Object>();
			for (int i = 0; i < zcmxzbxh.size(); i++) {
				long zbhx = parseLong(zcmxzbxh.get(i).get("ZBXH"));
				parzbhx.put("ZBXH", zbhx);
				parzbhx.put("CWYF", cwyf);
				parzbhx.put("JGID", jgid);
				parzcmx.put("ZBXH", zbhx);
				parzcmx.put("QSFSRQ", kssj);
				parzcmx.put("ZZFSRQ", zzsj);
				parzcmx.put("KFXH", kfxh);
				long l = dao.doCount("WL_YJZC", "ZBXH=:ZBXH and CWYF=:CWYF and JGID=:JGID", parzbhx);
				List<Map<String, Object>> zcmxListsave = dao.doQuery(zcmxsql,
						parzcmx);
				if (l <= 0) {
					String djlx = null;
					int ywlb = 0;
					if (zcmxListsave.size() > 0) {
						djlx = zcmxListsave.get(0).get("DJLX") + "";
						ywlb = parseInt(zcmxListsave.get(0).get("YWLB"));
					}
					if (("CK".equals(djlx) || "SL".equals(djlx)) && ywlb == -1) {
						yjzcMap.put("JGID", zcmxListsave.get(0).get("JGID"));
						yjzcMap.put("KFXH", zcmxListsave.get(0).get("KFXH"));
						yjzcMap.put("ZBLB", zcmxListsave.get(0).get("ZBLB"));
						yjzcMap.put("CWYF", cwyf);
						yjzcMap.put("ZBXH", zbhx);
						yjzcMap.put("WZXH", zcmxListsave.get(0).get("WZXH"));
						yjzcMap.put("WZZT", 1);
						yjzcMap.put("ZYKS", zcmxListsave.get(0).get("KSDM"));
						yjzcMap.put("CZYZ", zcmxListsave.get(0).get("WZJE"));
						dao.doSave("create", BSPHISEntryNames.WL_YJZC, yjzcMap,
								false);
					}
				} else {
					String djlx = null;
					int ywlb = 0;
					if (zcmxListsave.size() > 0) {
						djlx = zcmxListsave.get(0).get("DJLX") + "";
						ywlb = parseInt(zcmxListsave.get(0).get("YWLB"));
					}
					if (("CK".equals(djlx) && ywlb == 1) || "TK".equals(djlx)
							|| "BS".equals(djlx)) {
						dao.doUpdate("delete from WL_YJZC where ZBXH=:ZBXH and CWYF=:CWYF and JGID=:JGID",
								parzbhx);
					} else if ("ZK".equals(djlx)) {
						if (zcmxListsave.size() > 0) {
							if (zcmxListsave.get(zcmxListsave.size() - 1).get(
									"KSDM") != null
									&& zcmxListsave
											.get(zcmxListsave.size() - 1).get(
													"KSDM") != "") {
								long zyks = Long.parseLong(zcmxListsave.get(
										zcmxListsave.size() - 1).get("KSDM")
										+ "");
								parzbhx.put("ZYKS", zyks);
								dao.doUpdate(
										"update WL_YJZC set ZYKS=:ZYKS where ZBXH=:ZBXH and CWYF=:CWYF and JGID=:JGID",
										parzbhx);
								parzbhx.remove("ZYKS");
							}
						}
						dao.doUpdate(
								"update WL_YJZC set WZZT=1 where ZBXH=:ZBXH and CWYF=:CWYF and JGID=:JGID",
								parzbhx);
					}
				}
			}
			if (zcmxrkywlb1List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList1 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxrkywlb1List.size(); i++) {
					int sign1 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double rksl = 0.00;
					double rkje = 0.00;
					double rklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxrkywlb1List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxrkywlb1List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxrkywlb1List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList1.size(); j++) {
						if (parseLong(zcmxrkywlb1List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList1
								.get(j).get("WZXH"))
								&& parseLong(zcmxrkywlb1List.get(i).get("CJXH")) == parseLong(byyjjgjzxhList1
										.get(j).get("CJXH"))) {
							sign1 = 1;
							rksl = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKSL"));
							rkje = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKJE"));
							rklsje = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList1.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList1.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList1.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList1.get(j).get("JLXH"));
							break;
						}
					}
					if (sign1 == 1) {
						updyjjg.put("RKSL", rksl + wzsl);
						updyjjg.put("RKLSJE", rklsje + lsje);
						updyjjg.put("RKJE", rkje + wzje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set RKSL=:RKSL,RKJE=:RKJE,RKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxrkywlb1List.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxrkywlb1List.get(i)
								.get("ZBLB")));
						saveyjjg.put("WZXH", parseLong(zcmxrkywlb1List.get(i)
								.get("WZXH")));
						saveyjjg.put("CJXH", parseLong(zcmxrkywlb1List.get(i)
								.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZSL")));
						saveyjjg.put("RKJE", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZJE")));
						saveyjjg.put("RKLSJE",
								parseDouble(zcmxrkywlb1List.get(i).get("LSJE")));
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE",
								parseDouble(zcmxrkywlb1List.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxrkywlb2List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList2 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxrkywlb2List.size(); i++) {
					int sign2 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double rksl = 0.00;
					double rkje = 0.00;
					double rklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxrkywlb2List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxrkywlb2List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxrkywlb2List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList2.size(); j++) {
						if (parseLong(zcmxrkywlb2List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList2
								.get(j).get("WZXH"))
								&& parseLong(zcmxrkywlb2List.get(i).get("CJXH")) == parseLong(byyjjgjzxhList2
										.get(j).get("CJXH"))) {
							sign2 = 1;
							rksl = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKSL"));
							rkje = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKJE"));
							rklsje = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList2.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList2.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList2.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList2.get(j).get("JLXH"));
							break;
						}
					}
					if (sign2 == 1) {
						updyjjg.put("RKSL", rksl - wzsl);
						updyjjg.put("RKJE", rkje - wzje);
						updyjjg.put("RKLSJE", rklsje - lsje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set RKSL=:RKSL,RKJE=:RKJE,RKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxrkywlb2List.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxrkywlb2List.get(i)
								.get("ZBLB")));
						saveyjjg.put("WZXH", parseLong(zcmxrkywlb2List.get(i)
								.get("WZXH")));
						saveyjjg.put("CJXH", parseLong(zcmxrkywlb2List.get(i)
								.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("RKJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("RKLSJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("LSJE")));
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxckorslywlb1List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList3 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxckorslywlb1List.size(); i++) {
					int sign3 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double cksl = 0.00;
					double ckje = 0.00;
					double cklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxckorslywlb1List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxckorslywlb1List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxckorslywlb1List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList3.size(); j++) {
						if (parseLong(zcmxckorslywlb1List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList3
								.get(j).get("WZXH"))
								&& parseLong(zcmxckorslywlb1List.get(i).get(
										"CJXH")) == parseLong(byyjjgjzxhList3
										.get(j).get("CJXH"))) {
							sign3 = 1;
							cksl = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKSL"));
							ckje = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKJE"));
							cklsje = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList3.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList3.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList3.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList3.get(j).get("JLXH"));
							break;
						}
					}
					if (sign3 == 1) {
						updyjjg.put("RKSL", cksl - wzsl);
						updyjjg.put("RKJE", ckje - wzje);
						updyjjg.put("RKLSJE", cklsje - lsje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set CKSL=:RKSL,CKJE=:RKJE,CKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxckorslywlb1List
								.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxckorslywlb1List
								.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxckorslywlb1List.get(i)
										.get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxckorslywlb1List.get(i)
										.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKSL", -parseDouble(zcmxckorslywlb1List
								.get(i).get("WZSL")));
						saveyjjg.put("CKJE", -parseDouble(zcmxckorslywlb1List
								.get(i).get("WZJE")));
						saveyjjg.put("CKLSJE", -parseDouble(zcmxckorslywlb1List
								.get(i).get("LSJE")));
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", parseDouble(zcmxckorslywlb1List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxckorslywlb1List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", parseDouble(zcmxckorslywlb1List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxckorslywlb2List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList4 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxckorslywlb2List.size(); i++) {
					int sign4 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double cksl = 0.00;
					double ckje = 0.00;
					double cklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxckorslywlb2List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxckorslywlb2List.get(i).get("WZJE"));
					if (zcmxckorslywlb2List.get(i).get("LSJE") != null
							&& zcmxckorslywlb2List.get(i).get("LSJE") != "") {
						lsje = parseDouble(zcmxckorslywlb2List.get(i).get(
								"LSJE"));
					}
					for (int j = 0; j < byyjjgjzxhList4.size(); j++) {
						if (parseLong(zcmxckorslywlb2List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList4
								.get(j).get("WZXH"))
								&& parseLong(zcmxckorslywlb2List.get(i).get(
										"CJXH")) == parseLong(byyjjgjzxhList4
										.get(j).get("CJXH"))) {
							sign4 = 1;
							cksl = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKSL"));
							ckje = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKJE"));
							cklsje = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList4.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList4.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList4.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList4.get(j).get("JLXH"));
							break;
						}
					}
					if (sign4 == 1) {
						updyjjg.put("RKSL", cksl + wzsl);
						updyjjg.put("RKJE", ckje + wzje);
						updyjjg.put("RKLSJE", cklsje + lsje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set CKSL=:RKSL,CKJE=:RKJE,CKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxckorslywlb2List
								.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxckorslywlb2List
								.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxckorslywlb2List.get(i)
										.get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxckorslywlb2List.get(i)
										.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKSL", parseDouble(zcmxckorslywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("CKJE", parseDouble(zcmxckorslywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("CKLSJE", parseDouble(zcmxckorslywlb2List
								.get(i).get("LSJE")));
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxckorslywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxckorslywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxckorslywlb2List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxbsList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList5 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxbsList.size(); i++) {
					int sign5 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double bssl = 0.00;
					double bsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxbsList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxbsList.get(i).get("WZJE"));
					if (zcmxbsList.get(i).get("LSJE") != null) {
						lsje = parseDouble(zcmxbsList.get(i).get("LSJE"));
					}
					for (int j = 0; j < byyjjgjzxhList5.size(); j++) {
						if (parseLong(zcmxbsList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList5
								.get(j).get("WZXH"))
								&& parseLong(zcmxbsList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList5
										.get(j).get("CJXH"))) {
							sign5 = 1;
							bssl = parseDouble(byyjjgjzxhList5.get(j).get(
									"BSSL"));
							bsje = parseDouble(byyjjgjzxhList5.get(j).get(
									"BSJE"));
							qmsl = parseDouble(byyjjgjzxhList5.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList5.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList5.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList5.get(j).get("JLXH"));
							break;
						}
					}
					if (sign5 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", bssl + wzsl);
						updyjjg.put("RKJE", bsje + wzje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set BSSL=:RKSL,BSJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB",
								parseInt(zcmxbsList.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxbsList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxbsList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxbsList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL",
								parseDouble(zcmxbsList.get(i).get("WZSL")));
						saveyjjg.put("BSJE",
								parseDouble(zcmxbsList.get(i).get("WZJE")));
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxbsList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxbsList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxbsList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxpybList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList6 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxpybList.size(); i++) {
					int sign6 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double pysl = 0.00;
					double pyje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxpybList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxpybList.get(i).get("WZJE"));
					lsje = parseDouble(zcmxpybList.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList6.size(); j++) {
						if (parseLong(zcmxpybList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList6
								.get(j).get("WZXH"))
								&& parseLong(zcmxpybList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList6
										.get(j).get("CJXH"))) {
							sign6 = 1;
							pysl = parseDouble(byyjjgjzxhList6.get(j).get(
									"PYSL"));
							pyje = parseDouble(byyjjgjzxhList6.get(j).get(
									"PYJE"));
							qmsl = parseDouble(byyjjgjzxhList6.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList6.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList6.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList6.get(j).get("JLXH"));
							break;
						}
					}
					if (sign6 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", pysl + wzsl);
						updyjjg.put("RKJE", pyje + wzje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set PYSL=:RKSL,PYJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxpybList.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxpybList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxpybList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxpybList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", parseDouble(zcmxpybList.get(i)
								.get("WZSL")));
						saveyjjg.put("PYJE", parseDouble(zcmxpybList.get(i)
								.get("WZJE")));
						saveyjjg.put("QMSL", parseDouble(zcmxpybList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxpybList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", parseDouble(zcmxpybList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxpkList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList7 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxpkList.size(); i++) {
					int sign7 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double pysl = 0.00;
					double pyje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxpkList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxpkList.get(i).get("WZJE"));
					lsje = parseDouble(zcmxpkList.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList7.size(); j++) {
						if (parseLong(zcmxpkList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList7
								.get(j).get("WZXH"))
								&& parseLong(zcmxpkList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList7
										.get(j).get("CJXH"))) {
							sign7 = 1;
							pysl = parseDouble(byyjjgjzxhList7.get(j).get(
									"PYSL"));
							pyje = parseDouble(byyjjgjzxhList7.get(j).get(
									"PYJE"));
							qmsl = parseDouble(byyjjgjzxhList7.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList7.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList7.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList7.get(j).get("JLXH"));
							break;
						}
					}
					if (sign7 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", pysl - wzsl);
						updyjjg.put("RKJE", pyje - wzje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set PYSL=:RKSL,PYJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB",
								parseInt(zcmxpkList.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxpkList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxpkList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxpkList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", -parseDouble(zcmxpkList.get(i)
								.get("WZSL")));
						saveyjjg.put("PYJE", -parseDouble(zcmxpkList.get(i)
								.get("WZJE")));
						saveyjjg.put("QMSL", -parseDouble(zcmxpkList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxpkList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxpkList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxksckorslywlb1List.size() > 0) {
				List<Map<String, Object>> bylskszcList1 = dao.doQuery(
						hql_bylskszc, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxksckorslywlb1List.size(); i++) {
					int sign8 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double wzsllskszc = 0.00;
					double wzjelskszc = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxksckorslywlb1List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxksckorslywlb1List.get(i).get("WZJE"));
					for (int j = 0; j < bylskszcList1.size(); j++) {
						if (zcmxksckorslywlb1List.get(i).get("KSDM") != null) {
							if (parseLong(zcmxksckorslywlb1List.get(i).get(
									"KCXH")) == parseLong(bylskszcList1.get(j)
									.get("KCXH"))
									&& parseLong(zcmxksckorslywlb1List.get(i)
											.get("KSDM")) == parseLong(bylskszcList1
											.get(j).get("KSDM"))) {
								sign8 = 1;
								wzsllskszc = parseDouble(bylskszcList1.get(j)
										.get("WZSL"));
								wzjelskszc = parseDouble(bylskszcList1.get(j)
										.get("WZJE"));
								jlxh = parseLong(bylskszcList1.get(j).get(
										"JLXH"));
								break;
							}
						}
					}
					if (sign8 == 1) {
						updlskszc.put("WZSL", wzsllskszc + wzsl);
						updlskszc.put("WZJE", wzjelskszc + wzje);
						updlskszc.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_LSKSZC set WZSL=:WZSL,WZJE=:WZJE where JLXH=:JLXH",
								updlskszc);
					} else {
						savelskszc.put("JGID", jgid);
						savelskszc.put("CWYF", cwyf);
						if (zcmxksckorslywlb1List.get(i).get("KSDM") != null) {
							savelskszc.put(
									"KSDM",
									parseLong(zcmxksckorslywlb1List.get(i).get(
											"KSDM")));
						}
						savelskszc.put("KFXH", kfxh);
						savelskszc.put("ZBLB", parseInt(zcmxksckorslywlb1List
								.get(i).get("ZBLB")));
						savelskszc.put("KCXH", parseLong(zcmxksckorslywlb1List
								.get(i).get("KCXH")));
						savelskszc.put("WZXH", parseLong(zcmxksckorslywlb1List
								.get(i).get("WZXH")));
						savelskszc.put("CJXH", parseLong(zcmxksckorslywlb1List
								.get(i).get("CJXH")));
						savelskszc.put(
								"WZSL",
								parseDouble(zcmxksckorslywlb1List.get(i).get(
										"WZSL")));
						savelskszc.put(
								"WZJE",
								parseDouble(zcmxksckorslywlb1List.get(i).get(
										"WZJE")));
						dao.doSave("create", BSPHISEntryNames.WL_LSKSZC,
								savelskszc, false);
					}
				}
			}
			if (zcmxksckorslywlb2List.size() > 0) {
				List<Map<String, Object>> bylskszcList2 = dao.doQuery(
						hql_bylskszc, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxksckorslywlb2List.size(); i++) {
					int sign9 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double wzsllskszc = 0.00;
					double wzjelskszc = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxksckorslywlb2List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxksckorslywlb2List.get(i).get("WZJE"));
					for (int j = 0; j < bylskszcList2.size(); j++) {
						if (parseLong(zcmxksckorslywlb2List.get(i).get("KCXH")) == parseLong(bylskszcList2
								.get(j).get("KCXH"))
								&& parseLong(zcmxksckorslywlb2List.get(i).get(
										"KSDM")) == parseLong(bylskszcList2
										.get(j).get("KSDM"))) {
							sign9 = 1;
							wzsllskszc = parseDouble(bylskszcList2.get(j).get(
									"WZSL"));
							wzjelskszc = parseDouble(bylskszcList2.get(j).get(
									"WZJE"));
							jlxh = parseLong(bylskszcList2.get(j).get("JLXH"));
							break;
						}
					}
					if (sign9 == 1) {
						updlskszc.put("WZSL", wzsllskszc - wzsl);
						updlskszc.put("WZJE", wzjelskszc - wzje);
						updlskszc.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_LSKSZC set WZSL=:WZSL,WZJE=:WZJE where JLXH=:JLXH",
								updlskszc);
					} else {
						savelskszc.put("JGID", jgid);
						savelskszc.put("CWYF", cwyf);
						savelskszc.put("KSDM", parseLong(zcmxksckorslywlb2List
								.get(i).get("KSDM")));
						savelskszc.put("KFXH", kfxh);
						savelskszc.put("ZBLB", parseInt(zcmxksckorslywlb2List
								.get(i).get("ZBLB")));
						savelskszc.put("KCXH", parseLong(zcmxksckorslywlb2List
								.get(i).get("KCXH")));
						savelskszc.put("WZXH", parseLong(zcmxksckorslywlb2List
								.get(i).get("WZXH")));
						savelskszc.put("CJXH", parseLong(zcmxksckorslywlb2List
								.get(i).get("CJXH")));
						savelskszc.put(
								"WZSL",
								-parseDouble(zcmxksckorslywlb2List.get(i).get(
										"WZSL")));
						savelskszc.put(
								"WZJE",
								-parseDouble(zcmxksckorslywlb2List.get(i).get(
										"WZJE")));
						dao.doSave("create", BSPHISEntryNames.WL_LSKSZC,
								savelskszc, false);
					}
				}
			}
			if (zcmxksbs.size() > 0) {
				List<Map<String, Object>> bylskszcList3 = dao.doQuery(
						hql_bylskszc, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxksbs.size(); i++) {
					int sign10 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double wzsllskszc = 0.00;
					double wzjelskszc = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxksbs.get(i).get("WZSL"));
					wzje = parseDouble(zcmxksbs.get(i).get("WZJE"));
					for (int j = 0; j < bylskszcList3.size(); j++) {
						if (parseLong(zcmxksbs.get(i).get("KCXH")) == parseLong(bylskszcList3
								.get(j).get("KCXH"))
								&& parseLong(zcmxksbs.get(i).get("KSDM")) == parseLong(bylskszcList3
										.get(j).get("KSDM"))) {
							sign10 = 1;
							wzsllskszc = parseDouble(bylskszcList3.get(j).get(
									"WZSL"));
							wzjelskszc = parseDouble(bylskszcList3.get(j).get(
									"WZJE"));
							jlxh = parseLong(bylskszcList3.get(j).get("JLXH"));
							break;
						}
					}
					if (sign10 == 1) {
						updlskszc.put("WZSL", wzsllskszc - wzsl);
						updlskszc.put("WZJE", wzjelskszc - wzje);
						updlskszc.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_LSKSZC set WZSL=:WZSL,WZJE=:WZJE where JLXH=:JLXH",
								updlskszc);
					} else {
						savelskszc.put("JGID", jgid);
						savelskszc.put("CWYF", cwyf);
						savelskszc.put("KSDM",
								parseLong(zcmxksbs.get(i).get("KSDM")));
						savelskszc.put("KFXH", kfxh);
						savelskszc.put("ZBLB",
								parseInt(zcmxksbs.get(i).get("ZBLB")));
						savelskszc.put("KCXH",
								parseLong(zcmxksbs.get(i).get("KCXH")));
						savelskszc.put("WZXH",
								parseLong(zcmxksbs.get(i).get("WZXH")));
						savelskszc.put("CJXH",
								parseLong(zcmxksbs.get(i).get("CJXH")));
						savelskszc.put("WZSL", -parseDouble(zcmxksbs.get(i)
								.get("WZSL")));
						savelskszc.put("WZJE", -parseDouble(zcmxksbs.get(i)
								.get("WZJE")));
						dao.doSave("create", BSPHISEntryNames.WL_LSKSZC,
								savelskszc, false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		} catch (ValidateException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 库房月结
	 * @updateInfo
	 * @param cwyf
	 * @param kssj
	 * @param zzsj
	 * @param precwyf
	 * @throws ModelDataOperationException
	 */
	public void yjclej(String cwyf, String cwyfsy, Date kssj, Date zzsj,
			Date precwyf, String jgid, int kfxh)
			throws ModelDataOperationException {
		Map<String, Object> syyjjlandyjjgMap = new HashMap<String, Object>();
		syyjjlandyjjgMap.put("CWYF", cwyfsy);
		syyjjlandyjjgMap.put("KFXH", kfxh);
		Map<String, Object> byyjjlandyjjgMap = new HashMap<String, Object>();
		byyjjlandyjjgMap.put("CWYF", cwyf);
		byyjjlandyjjgMap.put("KFXH", kfxh);
		Map<String, Object> zcmxMap = new HashMap<String, Object>();
		zcmxMap.put("QSFSRQ", kssj);
		zcmxMap.put("ZZFSRQ", zzsj);
		zcmxMap.put("KFXH", kfxh);
		String hql_syyjjl = "select JGID as JGID,KFXH as KFXH,ZBLB as ZBLB,CWYF as CWYF,QSSJ as QSSJ,ZZSJ as ZZSJ,JZSJ as JZSJ,CSBZ as CSBZ from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF";// 上月月结记录
		String hql_syyjjg = "select a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.RKSL as RKSL,a.RKJE as RKJE,a.CKSL as CKSL,a.CKJE as CKJE,a.BSSL as BSSL,a.BSJE as BSJE,a.PYSL as PYSL,a.PYJE as PYJE,a.QMSL as QCSL,a.QMJE as QCJE,a.QMSL as QMSL,a.QMJE as QMJE,a.QCLSJE as QCLSJE,a.RKLSJE as RKLSJE,a.CKLSJE as CKLSJE,a.JCLSJE as JCLSJE from WL_YJJG a,WL_YJJL b where a.JZXH=b.JZXH and b.KFXH=:KFXH AND b.CWYF=:CWYF";// 上月月结结果
		String hql_byyjjl = "select JZXH as JZXH,JGID as JGID,KFXH as KFXH,ZBLB as ZBLB,CWYF as CWYF,QSSJ as QSSJ,ZZSJ as ZZSJ,JZSJ as JZSJ,CSBZ as CSBZ from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND CSBZ=0";// 本月月结记录
		String hql_byyjjgjzxh = "select a.JLXH as JLXH,a.ZBLB as ZBLB,a.WZXH as WZXH,a.CJXH as CJXH,a.RKSL as RKSL,a.RKJE as RKJE,a.CKSL as CKSL,a.CKJE as CKJE,a.BSSL as BSSL,a.BSJE as BSJE,a.PYSL as PYSL,a.PYJE as PYJE,a.QCSL as QCSL,a.QCJE as QCJE,a.QMSL as QMSL,a.QMJE as QMJE,a.QCLSJE as QCLSJE,a.RKLSJE as RKLSJE,a.CKLSJE as CKLSJE,a.JCLSJE as JCLSJE from WL_YJJG a,WL_YJJL b where a.JZXH=b.JZXH and b.KFXH=:KFXH AND b.CWYF=:CWYF AND b.CSBZ=0";// 本月月结结果
		String hql_zcmxrkywlb1 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='RK' or DJLX='DR') AND YWFS<>1 and YWLB=1 group by ZBLB,WZXH,CJXH";// 入库业务类别为1
		String hql_zcmxrkywlb2 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='RK' or DJLX='DR') AND YWFS<>1 and YWLB=-1 group by ZBLB,WZXH,CJXH";// 入库业务类别为-1
		String hql_zcmxckorslywlb1 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='CK' or DJLX='DB') AND YWFS<>1 and YWLB=1 group by ZBLB,WZXH,CJXH";// 出库或者申领
		// //
		// 业务类别为1
		String hql_zcmxckorslywlb2 = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and (DJLX='CK' or DJLX='DB') AND YWFS<>1 and YWLB=-1 group by ZBLB,WZXH,CJXH";// 出库或者申领
		// //
		// 业务类别为-1
		String hql_zcmxbs = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='BS' AND YWFS=0 group by ZBLB,WZXH,CJXH";// 报损
		String hql_zcmxpy = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='RK' AND YWFS=1 group by ZBLB,WZXH,CJXH";// 盘盈入库
		String hql_zcmxpk = "select ZBLB as ZBLB,WZXH as WZXH,CJXH as CJXH,sum(WZSL) as WZSL,sum(WZJE) as WZJE,sum(LSJE) as LSJE from WL_ZCMX where KFXH=:KFXH AND FSRQ>=:QSFSRQ AND FSRQ<=:ZZFSRQ and DJLX='CK' AND YWFS=1 group by ZBLB,WZXH,CJXH";// 盘亏出库

		Map<String, Object> updyjjg = new HashMap<String, Object>();
		Map<String, Object> getYJJLJZXH = new HashMap<String, Object>();
		Map<String, Object> saveyjjg = new HashMap<String, Object>();

		try {
			List<Map<String, Object>> syyjjlList = dao.doQuery(hql_syyjjl,
					syyjjlandyjjgMap);
			List<Map<String, Object>> syyjjgList = dao.doQuery(hql_syyjjg,
					syyjjlandyjjgMap);
			List<Map<String, Object>> zcmxrkywlb1List = dao.doQuery(
					hql_zcmxrkywlb1, zcmxMap);
			List<Map<String, Object>> zcmxrkywlb2List = dao.doQuery(
					hql_zcmxrkywlb2, zcmxMap);
			List<Map<String, Object>> zcmxckorslywlb1List = dao.doQuery(
					hql_zcmxckorslywlb1, zcmxMap);
			List<Map<String, Object>> zcmxckorslywlb2List = dao.doQuery(
					hql_zcmxckorslywlb2, zcmxMap);
			List<Map<String, Object>> zcmxbsList = dao.doQuery(hql_zcmxbs,
					zcmxMap);
			List<Map<String, Object>> zcmxpybList = dao.doQuery(hql_zcmxpy,
					zcmxMap);
			List<Map<String, Object>> zcmxpkList = dao.doQuery(hql_zcmxpk,
					zcmxMap);

			for (int i = 0; i < syyjjlList.size(); i++) {
				syyjjlList.get(i).put("CWYF", cwyf);
				String s1 = cwyf.substring(0, 4);
				String s2 = cwyf.substring(4, 6);
				String dataString = s1 + "-" + s2 + "-01";
				Date date = BSHISUtil.toDate(dataString);
				Date QSSJ = BSHISUtil.getFirstDayOfMonth(date);
				Date ZZSJ = BSHISUtil.getLastDayOfMonth(date);
				syyjjlList.get(i).put("QSSJ", QSSJ);
				syyjjlList.get(i).put("ZZSJ", ZZSJ);
				syyjjlList.get(i).put("CSBZ", 0);
				dao.doSave("create", BSPHISEntryNames.WL_YJJL,
						syyjjlList.get(i), false);
			}
			List<Map<String, Object>> byyjjlList = dao.doQuery(hql_byyjjl,
					byyjjlandyjjgMap);
			for (int i = 0; i < byyjjlList.size(); i++) {
				for (int j = 0; j < syyjjgList.size(); j++) {
					syyjjgList.get(j).put("JZXH",
							parseLong(byyjjlList.get(i).get("JZXH")));
					dao.doSave("create", BSPHISEntryNames.WL_YJJG,
							syyjjgList.get(j), false);
				}
			}
			if (zcmxrkywlb1List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList1 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxrkywlb1List.size(); i++) {
					int sign1 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double rksl = 0.00;
					double rkje = 0.00;
					double rklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxrkywlb1List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxrkywlb1List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxrkywlb1List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList1.size(); j++) {
						if (parseLong(zcmxrkywlb1List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList1
								.get(j).get("WZXH"))
								&& parseLong(zcmxrkywlb1List.get(i).get("CJXH")) == parseLong(byyjjgjzxhList1
										.get(j).get("CJXH"))) {
							sign1 = 1;
							rksl = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKSL"));
							rkje = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKJE"));
							rklsje = parseDouble(byyjjgjzxhList1.get(j).get(
									"RKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList1.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList1.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList1.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList1.get(j).get("JLXH"));
							break;
						}
					}
					if (sign1 == 1) {
						updyjjg.put("RKSL", rksl + wzsl);
						updyjjg.put("RKJE", rkje + wzje);
						updyjjg.put("RKLSJE", rklsje + lsje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set RKSL=:RKSL,RKJE=:RKJE,RKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxrkywlb1List.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxrkywlb1List.get(i)
								.get("ZBLB")));
						saveyjjg.put("WZXH", parseLong(zcmxrkywlb1List.get(i)
								.get("WZXH")));
						saveyjjg.put("CJXH", parseLong(zcmxrkywlb1List.get(i)
								.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZSL")));
						saveyjjg.put("RKJE", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZJE")));
						saveyjjg.put("RKLSJE",
								parseDouble(zcmxrkywlb1List.get(i).get("LSJE")));
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxrkywlb1List.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE",
								parseDouble(zcmxrkywlb1List.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxrkywlb2List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList2 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxrkywlb2List.size(); i++) {
					int sign2 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double rksl = 0.00;
					double rkje = 0.00;
					double rklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxrkywlb2List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxrkywlb2List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxrkywlb2List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList2.size(); j++) {
						if (parseLong(zcmxrkywlb2List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList2
								.get(j).get("WZXH"))
								&& parseLong(zcmxrkywlb2List.get(i).get("CJXH")) == parseLong(byyjjgjzxhList2
										.get(j).get("CJXH"))) {
							sign2 = 1;
							rksl = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKSL"));
							rkje = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKJE"));
							rklsje = parseDouble(byyjjgjzxhList2.get(j).get(
									"RKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList2.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList2.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList2.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList2.get(j).get("JLXH"));
							break;
						}
					}
					if (sign2 == 1) {
						updyjjg.put("RKSL", rksl - wzsl);
						updyjjg.put("RKJE", rkje - wzje);
						updyjjg.put("RKLSJE", rklsje - lsje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set RKSL=:RKSL,RKJE=:RKJE,RKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxrkywlb2List.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxrkywlb2List.get(i)
								.get("ZBLB")));
						saveyjjg.put("WZXH", parseLong(zcmxrkywlb2List.get(i)
								.get("WZXH")));
						saveyjjg.put("CJXH", parseLong(zcmxrkywlb2List.get(i)
								.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("RKJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("RKLSJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("LSJE")));
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxrkywlb2List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxckorslywlb1List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList3 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxckorslywlb1List.size(); i++) {
					int sign3 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double cksl = 0.00;
					double ckje = 0.00;
					double cklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxckorslywlb1List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxckorslywlb1List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxckorslywlb1List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList3.size(); j++) {
						if (parseLong(zcmxckorslywlb1List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList3
								.get(j).get("WZXH"))
								&& parseLong(zcmxckorslywlb1List.get(i).get(
										"CJXH")) == parseLong(byyjjgjzxhList3
										.get(j).get("CJXH"))) {
							sign3 = 1;
							cksl = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKSL"));
							ckje = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKJE"));
							cklsje = parseDouble(byyjjgjzxhList3.get(j).get(
									"CKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList3.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList3.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList3.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList3.get(j).get("JLXH"));
							break;
						}
					}
					if (sign3 == 1) {
						updyjjg.put("RKSL", cksl - wzsl);
						updyjjg.put("RKJE", ckje - wzje);
						updyjjg.put("RKLSJE", cklsje - lsje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set CKSL=:RKSL,CKJE=:RKJE,CKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxckorslywlb1List
								.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxckorslywlb1List
								.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxckorslywlb1List.get(i)
										.get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxckorslywlb1List.get(i)
										.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKSL", -parseDouble(zcmxckorslywlb1List
								.get(i).get("WZSL")));
						saveyjjg.put("CKJE", -parseDouble(zcmxckorslywlb1List
								.get(i).get("WZJE")));
						saveyjjg.put("CKLSJE", -parseDouble(zcmxckorslywlb1List
								.get(i).get("LSJE")));
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", parseDouble(zcmxckorslywlb1List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxckorslywlb1List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", parseDouble(zcmxckorslywlb1List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxckorslywlb2List.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList4 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxckorslywlb2List.size(); i++) {
					int sign4 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double cksl = 0.00;
					double ckje = 0.00;
					double cklsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxckorslywlb2List.get(i).get("WZSL"));
					wzje = parseDouble(zcmxckorslywlb2List.get(i).get("WZJE"));
					lsje = parseDouble(zcmxckorslywlb2List.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList4.size(); j++) {
						if (parseLong(zcmxckorslywlb2List.get(i).get("WZXH")) == parseLong(byyjjgjzxhList4
								.get(j).get("WZXH"))
								&& parseLong(zcmxckorslywlb2List.get(i).get(
										"CJXH")) == parseLong(byyjjgjzxhList4
										.get(j).get("CJXH"))) {
							sign4 = 1;
							cksl = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKSL"));
							ckje = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKJE"));
							cklsje = parseDouble(byyjjgjzxhList4.get(j).get(
									"CKLSJE"));
							qmsl = parseDouble(byyjjgjzxhList4.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList4.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList4.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList4.get(j).get("JLXH"));
							break;
						}
					}
					if (sign4 == 1) {
						updyjjg.put("RKSL", cksl + wzsl);
						updyjjg.put("RKJE", ckje + wzje);
						updyjjg.put("RKLSJE", cklsje + lsje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set CKSL=:RKSL,CKJE=:RKJE,CKLSJE=:RKLSJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxckorslywlb2List
								.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB", parseInt(zcmxckorslywlb2List
								.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxckorslywlb2List.get(i)
										.get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxckorslywlb2List.get(i)
										.get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKSL", parseDouble(zcmxckorslywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("CKJE", parseDouble(zcmxckorslywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxckorslywlb2List
								.get(i).get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxckorslywlb2List
								.get(i).get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxckorslywlb2List
								.get(i).get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxbsList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList5 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxbsList.size(); i++) {
					int sign5 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double bssl = 0.00;
					double bsje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxbsList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxbsList.get(i).get("WZJE"));
					lsje = parseDouble(zcmxbsList.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList5.size(); j++) {
						if (parseLong(zcmxbsList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList5
								.get(j).get("WZXH"))
								&& parseLong(zcmxbsList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList5
										.get(j).get("CJXH"))) {
							sign5 = 1;
							bssl = parseDouble(byyjjgjzxhList5.get(j).get(
									"BSSL"));
							bsje = parseDouble(byyjjgjzxhList5.get(j).get(
									"BSJE"));
							qmsl = parseDouble(byyjjgjzxhList5.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList5.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList5.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList5.get(j).get("JLXH"));
							break;
						}
					}
					if (sign5 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", bssl + wzsl);
						updyjjg.put("RKJE", bsje + wzje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set BSSL=:RKSL,BSJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB",
								parseInt(zcmxbsList.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxbsList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxbsList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxbsList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL",
								parseDouble(zcmxbsList.get(i).get("WZSL")));
						saveyjjg.put("BSJE",
								parseDouble(zcmxbsList.get(i).get("WZJE")));
						saveyjjg.put("PYSL", 0.00);
						saveyjjg.put("PYJE", 0.00);
						saveyjjg.put("QMSL", -parseDouble(zcmxbsList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxbsList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxbsList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxpybList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList6 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxpybList.size(); i++) {
					int sign6 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double pysl = 0.00;
					double pyje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxpybList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxpybList.get(i).get("WZJE"));
					lsje = parseDouble(zcmxpybList.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList6.size(); j++) {
						if (parseLong(zcmxpybList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList6
								.get(j).get("WZXH"))
								&& parseLong(zcmxpybList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList6
										.get(j).get("CJXH"))) {
							sign6 = 1;
							pysl = parseDouble(byyjjgjzxhList6.get(j).get(
									"PYSL"));
							pyje = parseDouble(byyjjgjzxhList6.get(j).get(
									"PYJE"));
							qmsl = parseDouble(byyjjgjzxhList6.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList6.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList6.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList6.get(j).get("JLXH"));
							break;
						}
					}
					if (sign6 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", pysl + wzsl);
						updyjjg.put("RKJE", pyje + wzje);
						updyjjg.put("QMSL", qmsl + wzsl);
						updyjjg.put("QMJE", qmje + wzje);
						updyjjg.put("JCLSJE", jclsje + lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set PYSL=:RKSL,PYJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB", parseInt(zcmxpybList.get(i)
								.get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxpybList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxpybList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxpybList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", parseDouble(zcmxpybList.get(i)
								.get("WZSL")));
						saveyjjg.put("PYJE", parseDouble(zcmxpybList.get(i)
								.get("WZJE")));
						saveyjjg.put("QMSL", parseDouble(zcmxpybList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", parseDouble(zcmxpybList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", parseDouble(zcmxpybList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
			if (zcmxpkList.size() > 0) {
				List<Map<String, Object>> byyjjgjzxhList7 = dao.doQuery(
						hql_byyjjgjzxh, byyjjlandyjjgMap);
				for (int i = 0; i < zcmxpkList.size(); i++) {
					int sign7 = 0;
					double wzsl = 0.00;
					double wzje = 0.00;
					double lsje = 0.00;
					double pysl = 0.00;
					double pyje = 0.00;
					double qmsl = 0.00;
					double qmje = 0.00;
					double jclsje = 0.00;
					Long jlxh = 0L;
					wzsl = parseDouble(zcmxpkList.get(i).get("WZSL"));
					wzje = parseDouble(zcmxpkList.get(i).get("WZJE"));
					lsje = parseDouble(zcmxpkList.get(i).get("LSJE"));
					for (int j = 0; j < byyjjgjzxhList7.size(); j++) {
						if (parseLong(zcmxpkList.get(i).get("WZXH")) == parseLong(byyjjgjzxhList7
								.get(j).get("WZXH"))
								&& parseLong(zcmxpkList.get(i).get("CJXH")) == parseLong(byyjjgjzxhList7
										.get(j).get("CJXH"))) {
							sign7 = 1;
							pysl = parseDouble(byyjjgjzxhList7.get(j).get(
									"PYSL"));
							pyje = parseDouble(byyjjgjzxhList7.get(j).get(
									"PYJE"));
							qmsl = parseDouble(byyjjgjzxhList7.get(j).get(
									"QMSL"));
							qmje = parseDouble(byyjjgjzxhList7.get(j).get(
									"QMJE"));
							jclsje = parseDouble(byyjjgjzxhList7.get(j).get(
									"JCLSJE"));
							jlxh = parseLong(byyjjgjzxhList7.get(j).get("JLXH"));
							break;
						}
					}
					if (sign7 == 1) {
						updyjjg.remove("RKLSJE");
						updyjjg.put("RKSL", pysl - wzsl);
						updyjjg.put("RKJE", pyje - wzje);
						updyjjg.put("QMSL", qmsl - wzsl);
						updyjjg.put("QMJE", qmje - wzje);
						updyjjg.put("JCLSJE", jclsje - lsje);
						updyjjg.put("JLXH", jlxh);
						dao.doUpdate(
								"update WL_YJJG set PYSL=:RKSL,PYJE=:RKJE,QMSL=:QMSL,QMJE=:QMJE,JCLSJE=:JCLSJE where JLXH=:JLXH",
								updyjjg);
					} else {
						getYJJLJZXH.put("KFXH", kfxh);
						getYJJLJZXH.put("CWYF", cwyf);
						getYJJLJZXH.put("ZBLB",
								parseInt(zcmxpkList.get(i).get("ZBLB")));
						Map<String, Object> jzxhMap = dao
								.doLoad("select JZXH as JZXH from WL_YJJL where KFXH=:KFXH AND CWYF=:CWYF AND ZBLB=:ZBLB",
										getYJJLJZXH);
						Long jzxh = 0L;
						if (jzxhMap.get("JZXH") != null) {
							jzxh = parseLong(jzxhMap.get("JZXH"));
						}
						saveyjjg.put("JZXH", jzxh);
						saveyjjg.put("ZBLB",
								parseInt(zcmxpkList.get(i).get("ZBLB")));
						saveyjjg.put("WZXH",
								parseLong(zcmxpkList.get(i).get("WZXH")));
						saveyjjg.put("CJXH",
								parseLong(zcmxpkList.get(i).get("CJXH")));
						saveyjjg.put("QCSL", 0.00);
						saveyjjg.put("QCJE", 0.00);
						saveyjjg.put("RKSL", 0.00);
						saveyjjg.put("RKJE", 0.00);
						saveyjjg.put("CKSL", 0.00);
						saveyjjg.put("CKJE", 0.00);
						saveyjjg.put("QCLSJE", 0.00);
						saveyjjg.put("RKLSJE", 0.00);
						saveyjjg.put("CKLSJE", 0.00);
						saveyjjg.put("BSSL", 0.00);
						saveyjjg.put("BSJE", 0.00);
						saveyjjg.put("PYSL", -parseDouble(zcmxpkList.get(i)
								.get("WZSL")));
						saveyjjg.put("PYJE", -parseDouble(zcmxpkList.get(i)
								.get("WZJE")));
						saveyjjg.put("QMSL", -parseDouble(zcmxpkList.get(i)
								.get("WZSL")));
						saveyjjg.put("QMJE", -parseDouble(zcmxpkList.get(i)
								.get("WZJE")));
						saveyjjg.put("JCLSJE", -parseDouble(zcmxpkList.get(i)
								.get("LSJE")));
						dao.doSave("create", BSPHISEntryNames.WL_YJJG,
								saveyjjg, false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		} catch (ValidateException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "月结失败");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 取消月结
	 * @updateInfo
	 * @param cwyf
	 * @throws ModelDataOperationException
	 */
	public void qxyj(String cwyf, Map<String, Object> ret, int kfxh)
			throws ModelDataOperationException {
		StringBuffer hql_yjyz = new StringBuffer();
		hql_yjyz.append(" KFXH=:KFXH and CSBZ=0 and CWYF>:CWYF");
		Map<String, Object> map_par_yjyz = new HashMap<String, Object>();
		Map<String, Object> jzxhmap = new HashMap<String, Object>();
		map_par_yjyz.put("KFXH", kfxh);
		map_par_yjyz.put("CWYF", cwyf);
		try {
			Long l = dao.doCount("WL_YJJL", hql_yjyz.toString(), map_par_yjyz);
			if (l > 0) {
				ret.put("code", 9000);
				ret.put("msg", "只能取消最后一个月的账册!");
				return;
			}
			long zjjlcount = dao.doCount("WL_ZJJL",
					"KFXH=:KFXH and CWYF=:CWYF", map_par_yjyz);
			if (zjjlcount > 0) {
				ret.put(Service.RES_CODE, 9001);
				ret.put(Service.RES_MESSAGE, "本月已经做折旧,请先取消折旧!");
				return;
			}
			String hql_yjjl_delete = "delete from WL_YJJL where CWYF=:CWYF and KFXH=:KFXH and CSBZ=0";// 删除月结记录
			String queryJzxhList = "select JZXH as JZXH from WL_YJJL where CWYF=:CWYF and KFXH=:KFXH and CSBZ=0";// 找到jzxh
			String hql_yjjg_delete = "delete from WL_YJJG where JZXH=:JZXH";// 删除月结结果
			String hql_lskszc_delete = "delete from WL_LSKSZC where CWYF=:CWYF and KFXH=:KFXH";// 删除历史科室账册记录
			String hql_yjzc_delete = "delete from WL_YJZC where CWYF=:CWYF and KFXH=:KFXH";// 删除月结资产
			List<Map<String, Object>> jzxhList = dao.doQuery(queryJzxhList,
					map_par_yjyz);
			dao.doUpdate(hql_yjjl_delete, map_par_yjyz);
			dao.doUpdate(hql_yjzc_delete, map_par_yjyz);
			for (int i = 0; i < jzxhList.size(); i++) {
				jzxhmap.put("JZXH", parseLong(jzxhList.get(i).get("JZXH")));
				dao.doUpdate(hql_yjjg_delete, jzxhmap);
			}
			dao.doUpdate(hql_lskszc_delete, map_par_yjyz);
			ret.put("msg", "取消月结成功");
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消月结失败");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 取消月结
	 * @updateInfo
	 * @param cwyf
	 * @throws ModelDataOperationException
	 */
	public void qxyjej(String cwyf, Map<String, Object> ret, int kfxh)
			throws ModelDataOperationException {
		StringBuffer hql_yjyz = new StringBuffer();
		hql_yjyz.append(" KFXH=:KFXH and CSBZ=0 and CWYF>:CWYF");
		Map<String, Object> map_par_yjyz = new HashMap<String, Object>();
		Map<String, Object> jzxhmap = new HashMap<String, Object>();
		map_par_yjyz.put("KFXH", kfxh);
		map_par_yjyz.put("CWYF", cwyf);
		try {
			Long l = dao.doCount("WL_YJJL", hql_yjyz.toString(), map_par_yjyz);
			if (l > 0) {
				ret.put("code", 9000);
				ret.put("msg", "只能取消最后一个月的账册!");
				return;
			}
			String hql_yjjl_delete = "delete from WL_YJJL where CWYF=:CWYF and KFXH=:KFXH and CSBZ=0";// 删除月结记录
			String queryJzxhList = "select JZXH as JZXH from WL_YJJL where CWYF=:CWYF and KFXH=:KFXH and CSBZ=0";// 找到jzxh
			String hql_yjjg_delete = "delete from WL_YJJG where JZXH=:JZXH";// 删除月结结果
			List<Map<String, Object>> jzxhList = dao.doQuery(queryJzxhList,
					map_par_yjyz);
			dao.doUpdate(hql_yjjl_delete, map_par_yjyz);
			for (int i = 0; i < jzxhList.size(); i++) {
				jzxhmap.put("JZXH", parseLong(jzxhList.get(i).get("JZXH")));
				dao.doUpdate(hql_yjjg_delete, jzxhmap);
			}
			ret.put("msg", "取消月结成功");
		} catch (PersistentDataOperationException e) {
			logger.error("Monthly statement fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "取消月结失败");
		}
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 数据转换成long
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public long parseLong(Object o) {
		if (o == null) {
			return new Long(0);
		}
		return Long.parseLong(o + "");
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 数据转换成double
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0.00);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description 数据转换成int
	 * @updateInfo
	 * @param o
	 * @return
	 */
	public int parseInt(Object o) {
		if (o == null) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	/**
	 * 
	 * @author shiwy
	 * @createDate 2013-5-23
	 * @description double型数据转换
	 * @updateInfo
	 * @param number
	 *            保留小数点后几位
	 * @param data
	 *            数据
	 * @return
	 */
	public double formatDouble(int number, double data) {
		// if (number > 8) {
		// return 0;
		// }
		// double x = Math.pow(10, number);
		// return Math.round(data * x) / x;
		return BSPHISUtil.getDouble(data, number);
	}

	public void doQueryYJJLInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		int kfxh = parseInt(user.getProperty("treasuryId"));
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
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
		String sql = "select distinct t.JGID as JGID,t.KFXH as KFXH,t.CWYF as CWYF,t.QSSJ as QSSJ,t.ZZSJ as ZZSJ,t.JZSJ as JZSJ,t.CSBZ as CSBZ from WL_YJJL t where t.KFXH=:KFXH and CSBZ=0 order by t.JZSJ desc";
		parameters.put("KFXH", kfxh);
		parameterssize.put("KFXH", kfxh);
		try {
			list = dao.doSqlQuery(sql, parameters);
			listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.WL_YJJL);
			res.put("totalCount", parseLong(listsize.size()));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
