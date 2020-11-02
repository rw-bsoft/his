/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.reg.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.syntax.jedit.InputHandler.end;

import phis.source.utils.BSHISUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 医生排班
 * 
 * @author shiwy 2012.08.28
 */
public class RegistrationDoctorPlanModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(RegistrationDoctorPlanModule.class);

	public RegistrationDoctorPlanModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 医生排班保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRegistrationDoctorPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> map_ = body.get(i);// 一条一条放到map_中
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("KSDM", Long.parseLong(map_.get("KSDM") + ""));
				parameters.put("YSDM", map_.get("YSDM"));
				parameters.put("ZBLB", Integer.parseInt(map_.get("ZBLB") + ""));
				parameters.put("GZRQ", formatter.parse(map_.get("GZRQ") + ""));
				parameters.put("JGID", map_.get("JGID"));
				map_.put("GZRQ", formatter.parse(map_.get("GZRQ") + ""));
				dao.doUpdate(
						"delete from MS_YSPB where KSDM=:KSDM and YSDM=:YSDM and ZBLB=:ZBLB and GZRQ=:GZRQ and JGID=:JGID",
						parameters);
				dao.doSave("create", BSPHISEntryNames.MS_YSPB, map_, false);
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 医生排班删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */

	public void doRemoveRegistrationDoctorPlan(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			parameters.put("YSDM", body.get("YSDM"));
			parameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
			parameters.put("GZRQ", formatter.parse(body.get("GZRQ") + ""));
			parameters.put("JGID", body.get("JGID"));
			dao.doUpdate(
					"delete from MS_YSPB where KSDM=:KSDM and YSDM=:YSDM and ZBLB=:ZBLB and GZRQ=:GZRQ and JGID=:JGID",
					parameters);

		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 医生排班复制
	 * 
	 * @param req
	 * @param res
	 * @param dao2
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ValidateException
	 */
	public void doSave_copyYSPB(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao2, Context ctx)
			throws ModelDataOperationException, ParseException,
			ValidateException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String endDay = req.get("endDay") + "";
		String beginDay = req.get("beginDay") + "";
		Date d_beginDay = BSHISUtil.toDate(beginDay);
		Date d_endDay = BSHISUtil.toDate(endDay);
		String sql_thisWeek = "select GZRQ as GZRQ,KSDM as KSDM,YSDM as YSDM, ZBLB as ZBLB,JGID as JGID,GHXE as GHXE,YGRS as YGRS,YYRS as YYRS,"
				+ "JZXH as JZXH,YYXE as YYXE, TGBZ as TGBZ from ms_yspb where to_char(GZRQ,'yyyymmdd')>="
				+ BSHISUtil.toString(d_beginDay, "yyyyMMdd")
				+ " and to_char(GZRQ,'yyyymmdd')< "
				+ BSHISUtil.toString(d_endDay, "yyyyMMdd")
				+ " and jgid="
				+ jgid + "  order by GZRQ,ZBLB";
		String sql_NextWeek = "select GZRQ as GZRQ,KSDM as KSDM,YSDM as YSDM, ZBLB as ZBLB,JGID as JGID,GHXE as GHXE,YGRS as YGRS,YYRS as YYRS,"
				+ "JZXH as JZXH,YYXE as YYXE, TGBZ as TGBZ from ms_yspb where to_char(GZRQ,'yyyymmdd')>="
				+ BSHISUtil.toString(
						BSHISUtil.getDateAfter(BSHISUtil.toDate(beginDay), 7),
						"yyyyMMdd")
				+ " and to_char(GZRQ,'yyyymmdd')< "
				+ BSHISUtil.toString(
						BSHISUtil.getDateAfter(BSHISUtil.toDate(endDay), 7),
						"yyyyMMdd")
				+ " and jgid="
				+ jgid
				+ "  order by GZRQ,ZBLB";
		try {
			List<Map<String, Object>> list_thisWeek = dao.doSqlQuery(
					sql_thisWeek, null);
			List<Map<String, Object>> list_nextWeek = dao.doSqlQuery(
					sql_NextWeek, null);
			if (list_thisWeek.size() == 0 || list_nextWeek.size() != 0) {
				return;
			}
			// String hql="";
			// for(int i=0;i<list_thisWeek.size();i++){
			// // Date nextWeekDay =
			// BSHISUtil.getDateAfter(BSHISUtil.toDate(list_thisWeek.get(i).get("GZRQ")+""),7);
			// list_thisWeek.get(i).put("GZRQ",BSHISUtil.getDateAfter(BSHISUtil.toDate(list_thisWeek.get(i).get("GZRQ")+""),7));
			// dao.doInsert(BSPHISEntryNames.MS_YSPB,list_thisWeek.get(i),
			// false);
			dao.doSqlUpdate(
					"insert into MS_YSPB select GZRQ+7 as GZRQ,KSDM as KSDM,YSDM as YSDM, ZBLB as ZBLB,JGID as JGID,GHXE as GHXE,YGRS as YGRS,YYRS as YYRS,"
							+ "JZXH as JZXH,YYXE as YYXE, TGBZ as TGBZ,YYKSSJ as YYKSSJ,YYJSSJ as YYJSSJ,YYJG as YYJG from ms_yspb where to_char(GZRQ,'yyyymmdd')>="
							+ BSHISUtil.toString(d_beginDay, "yyyyMMdd")
							+ " and to_char(GZRQ,'yyyymmdd')< "
							+ BSHISUtil.toString(d_endDay, "yyyyMMdd")
							+ " and jgid=" + jgid + "  order by GZRQ,ZBLB",
					null);
			// }
		} catch (PersistentDataOperationException e) {
			logger.error("fzpb failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班复制失败.");
		}
	}

	/**
	 * 挂号分类统计
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ParseException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void doRegCount(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException, ParseException,
			ValidateException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		if (body == null)
			return;
		String tjfs = body.get("TJFS") == null ? "1" : String.valueOf(body
				.get("TJFS"));
		String beginDate = body.get("beginDate") == null ? BSHISUtil.getDate()
				: String.valueOf(body.get("beginDate"));
		String endDate = body.get("endDate") == null ? BSHISUtil.getDate()
				: String.valueOf(body.get("endDate"));
		beginDate = beginDate + " 00 00 00";
		endDate = endDate + " 23 59 59";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		StringBuilder sqlBuilder = new StringBuilder();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		if ("1".equals(tjfs)) {// 按科室统计
			sqlBuilder
					.append("select c.KSMC as KSMC, c.ghrc as RC from ( select  b.KSMC as KSMC,mzks as mzks, COUNT(*) as GHRC ");
			sqlBuilder.append("  from ms_ghmx a, MS_GHKS b where");
			sqlBuilder.append(" (to_char(a.GHSJ, 'yyyy-mm-dd HH24:MI:SS') >= '"
					+ beginDate + "')");
			sqlBuilder
					.append(" and (to_char(a.GHSJ, 'yyyy-mm-dd HH24:MI:SS') <= '"
							+ endDate + "') and");
			sqlBuilder.append("  a.ksdm = b.ksdm and a.thbz <> 1 ");// .append("and a.MZLB = 1");
			sqlBuilder
					.append(" and a.jgid = '"
							+ manageUnit
							+ "'  group by b.mzks,b.KSMC  ) c , sys_office s where c.mzks = s.id");
		} else {// 按性质统计
			sqlBuilder
					.append("select s.xzmc as KSMC , c.ghrc as RC from ( select  brxz as brxz, COUNT(*) as GHRC ");
			sqlBuilder.append("  from ms_ghmx a where");
			sqlBuilder.append(" (to_char(a.GHSJ, 'yyyy-mm-dd HH24:MI:SS') >= '"
					+ beginDate + "')");
			sqlBuilder
					.append(" and (to_char(a.GHSJ, 'yyyy-mm-dd HH24:MI:SS') <= '"
							+ endDate + "')");
			sqlBuilder.append("  and a.thbz <> 1 ");// .append("and a.MZLB = 1");
			sqlBuilder
					.append(" and a.jgid = '"
							+ manageUnit
							+ "'  group by brxz  ) c , gy_brxz s where c.brxz  = s.brxz ");
		}
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		if (pageSize > 0) {
			parameterMap.put("first", pageNo * pageSize);
			parameterMap.put("max", pageSize);
		}
		try {
			List<Map<String, Object>> resultList = dao.doSqlQuery(
					sqlBuilder.toString(), parameterMap);
			long rs = 0;
			double ls = 0;
			for (Map<String, Object> map : resultList) {
				rs += this.Object2Long(map.get("RC"));
			}
			for (Map<String, Object> map : resultList) {
				map.put("BFB",
						String.format("%1$.2f", Object2Double(map.get("RC"))
								/ rs * 100)
								+ "%");
				ls += Object2Double(String.format("%1$.2f",
						Object2Double(map.get("RC")) / rs * 100));
			}
			double rl = ls - 100;
			if (resultList.size() > 0) {
				// 得到总的百分比
				if (rl > 0) {// 总的百分比比100大 那么就最后一行减去多余的误差
					resultList
							.get(resultList.size() - 1)
							.put("BFB",
									String.format(
											"%1$.2f",
											Object2Double(resultList
													.get(resultList.size() - 1)
													.get("BFB")
													.toString()
													.substring(
															0,
															resultList
																	.get(resultList
																			.size() - 1)
																	.get("BFB")
																	.toString()
																	.indexOf(
																			"%")))
													- rl)
											+ "%");
				} else {// 如果得到总的百分比比100小 那么转为正整数 最后一行加上多余的误差
					rl = -rl;
					resultList
							.get(resultList.size() - 1)
							.put("BFB",
									String.format(
											"%1$.2f",
											Object2Double(resultList
													.get(resultList.size() - 1)
													.get("BFB")
													.toString()
													.substring(
															0,
															resultList
																	.get(resultList
																			.size() - 1)
																	.get("BFB")
																	.toString()
																	.indexOf(
																			"%")))
													+ rl)
											+ "%");
				}
			}
			List<Map<String, Object>> listsize = dao.doSqlQuery(
					sqlBuilder.toString(), null);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long Object2Long(Object object) {
		if (object == null) {
			return 0;
		} else {
			return Long.parseLong(object + "");
		}
	}

	private double Object2Double(Object object) {
		if (object == null) {
			return 0;
		} else {
			return Double.parseDouble(object + "");
		}
	}
}
