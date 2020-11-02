package phis.application.ivc.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class AccountsStatisticsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(AccountsStatisticsModel.class);

	public AccountsStatisticsModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 统计前验证
	public void doAccountsStatisticsBefVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Date ldt_begin = null;
		Date ldt_end = null;
		// int REPORT_COUNTDATE_MZ =
		// Integer.parseInt(ParameterUtil.getParameter(
		// manaUnitId, BSPHISSystemArgument.REPORT_COUNTDATE_MZ, ctx));
		int REPORT_COUNTDATE_MZ = 1;
		try {
			if (req.get("dateFrom") != null) {
				ldt_begin = sdfdatetime
						.parse(req.get("dateFrom") + " 00:00:00");
			}
			if (req.get("dateFrom") != null) {
				ldt_end = sdfdatetime.parse(req.get("dateTo") + " 23:59:59");
			}
			Map<String, Object> mzhscountpar = new HashMap<String, Object>();
			mzhscountpar.put("gl_jgid", manaUnitId);
			mzhscountpar.put("ldt_begin", ldt_begin);
			mzhscountpar.put("ldt_end", ldt_end);
			mzhscountpar.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			Long ll_Count_mz = dao
					.doCount(
							"MS_MZHS",
							"JGID=:gl_jgid and GZRQ >=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
							mzhscountpar);
			Long ll_Count_yj = dao
					.doCount(
							"MS_YJHS",
							"JGID=:gl_jgid and GZRQ>=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
							mzhscountpar);
			if (ll_Count_mz > 0 || ll_Count_yj > 0) {
				res.put(Service.RES_CODE, 600);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	// 统计
	public void doSaveAccountsStatistics(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		Date ldt_begin = null;
		Date ldt_end = null;
		// int REPORT_COUNTDATE_MZ =
		// Integer.parseInt(ParameterUtil.getParameter(
		// manaUnitId, BSPHISSystemArgument.REPORT_COUNTDATE_MZ, ctx));
		int REPORT_COUNTDATE_MZ = 1;
		try {
			if (req.get("dateFrom") != null) {
				ldt_begin = sdfdatetime
						.parse(req.get("dateFrom") + " 00:00:00");
			}
			if (req.get("dateFrom") != null) {
				ldt_end = sdfdatetime.parse(req.get("dateTo") + " 23:59:59");
			}
			Map<String, Object> mzhscountpar = new HashMap<String, Object>();
			mzhscountpar.put("gl_jgid", manaUnitId);
			mzhscountpar.put("ldt_begin", ldt_begin);
			mzhscountpar.put("ldt_end", ldt_end);
			mzhscountpar.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			Long ll_Count_mz = dao
					.doCount(
							"MS_MZHS",
							"JGID=:gl_jgid and GZRQ >=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
							mzhscountpar);
			Long ll_Count_yj = dao
					.doCount(
							"MS_YJHS",
							"JGID=:gl_jgid and GZRQ>=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
							mzhscountpar);
			if (ll_Count_mz > 0) {
				ll_Count_mz = 0L;
				ll_Count_mz = dao
						.doCount(
								"MS_MZMX MS_MZMX,MS_MZHS MS_MZHS",
								"MS_MZMX.HZXH=MS_MZHS.HZXH and MS_MZHS.JGID=:gl_jgid and MS_MZHS.GZRQ>=:ldt_begin AND MS_MZHS.GZRQ<=:ldt_end AND MS_MZHS.TJFS=:ii_tjfs",
								mzhscountpar);
				if (ll_Count_mz > 0) {
					dao.doUpdate(
							"DELETE FROM MS_MZMX MS_MZMX WHERE exists (select MS_MZHS.HZXH from MS_MZHS MS_MZHS where MS_MZMX.HZXH = MS_MZHS.HZXH and MS_MZHS.JGID=:gl_jgid and MS_MZHS.GZRQ>=:ldt_begin AND MS_MZHS.GZRQ<=:ldt_end AND MS_MZHS.TJFS=:ii_tjfs)",
							mzhscountpar);
				}
				dao.doUpdate(
						" DELETE FROM MS_MZHS WHERE JGID=:gl_jgid and GZRQ>=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
						mzhscountpar);
			}

			if (ll_Count_yj > 0) {
				ll_Count_yj = 0L;
				ll_Count_yj = dao
						.doCount(
								"MS_YJMX MS_YJMX,MS_YJHS MS_YJHS",
								"MS_YJMX.HZXH=MS_YJHS.HZXH and MS_YJHS.JGID=:gl_jgid and MS_YJHS.GZRQ>=:ldt_begin AND MS_YJHS.GZRQ<=:ldt_end AND MS_YJHS.TJFS=:ii_tjfs",
								mzhscountpar);
				if (ll_Count_yj > 0) {
					dao.doUpdate(
							"DELETE FROM MS_YJMX MS_YJMX WHERE exists (select MS_YJHS.HZXH from MS_YJHS MS_YJHS where MS_YJHS.JGID=:gl_jgid and MS_YJHS.GZRQ>=:ldt_begin AND MS_YJHS.GZRQ<=:ldt_end AND MS_YJHS.TJFS=:ii_tjfs)",
							mzhscountpar);
				}
				dao.doUpdate(
						"DELETE FROM MS_YJHS WHERE JGID=:gl_jgid and GZRQ>=:ldt_begin AND GZRQ<=:ldt_end AND TJFS=:ii_tjfs",
						mzhscountpar);
			}
			Map<String, Object> mzhsmap = new HashMap<String, Object>();
			mzhsmap.put("gl_jgid", manaUnitId);
			mzhsmap.put("ldt_begin", ldt_begin);
			mzhsmap.put("ldt_end", ldt_end);
			mzhsmap.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			List<Map<String, Object>> ms_mzhslist = BSPHISUtil.wf_tj_mzhs(
					mzhsmap, dao, ctx);
			Map<String, Object> mzmxmap = new HashMap<String, Object>();
			mzmxmap.put("gl_jgid", manaUnitId);
			mzmxmap.put("ldt_begin", ldt_begin);
			mzmxmap.put("ldt_end", ldt_end);
			mzmxmap.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			List<Map<String, Object>> ms_mzmxlist = BSPHISUtil.wf_tj_mzmx(
					mzmxmap, dao, ctx);
			for (int i = 0; i < ms_mzhslist.size(); i++) {
				ms_mzhslist.get(i).put("JGID", manaUnitId);
				if(ms_mzhslist.get(i).get("YSDM")==null)return;
				Map<String, Object> mzhsReq = dao.doSave("create", BSPHISEntryNames.MS_MZHS,
						ms_mzhslist.get(i), false);
				Long hzxh = Long.parseLong(mzhsReq.get("HZXH") + "");// 获取主键
				for (int j = 0; j < ms_mzmxlist.size(); j++) {
					if(ms_mzhslist
							.get(i)
							.get("YSDM")!=null&&ms_mzmxlist.get(j).get("YSDM")!=null&&ms_mzhslist.get(i).get("KSDM")!=null&&ms_mzmxlist.get(j).get("KSDM")!=null
							&& ms_mzhslist.get(i).get("TJFS")!=null&&ms_mzmxlist
									.get(j).get("TJFS")!=null){
					if (Long.parseLong(ms_mzhslist.get(i).get("KSDM") + "") == Long
							.parseLong(ms_mzmxlist.get(j).get("KSDM") + "")
							&& ms_mzhslist
									.get(i)
									.get("YSDM")
									.toString()
									.equals(ms_mzmxlist.get(j).get("YSDM") + "")
							&& Integer.parseInt(ms_mzhslist.get(i).get("TJFS")
									+ "") == Integer.parseInt(ms_mzmxlist
									.get(j).get("TJFS") + "")
							&& sdfdatetime.parse(
									sdfdatetime.format(ms_mzhslist.get(i).get(
											"GZRQ"))).getTime() == sdfdatetime
									.parse(sdfdatetime.format(ms_mzmxlist
											.get(j).get("GZRQ"))).getTime()) {
						ms_mzmxlist.get(j).put("HZXH", hzxh);
						ms_mzmxlist.get(j).put("JGID", manaUnitId);
						dao.doSave("create", BSPHISEntryNames.MS_MZMX, ms_mzmxlist.get(j),
								false);
					}}
				}
			}
			Map<String, Object> yjhsmap = new HashMap<String, Object>();
			yjhsmap.put("gl_jgid", manaUnitId);
			yjhsmap.put("ldt_begin", ldt_begin);
			yjhsmap.put("ldt_end", ldt_end);
			yjhsmap.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			List<Map<String, Object>> ms_yjhslist = BSPHISUtil.wf_tj_yjhs(
					yjhsmap, dao, ctx);
			Map<String, Object> yjmxmap = new HashMap<String, Object>();
			yjmxmap.put("gl_jgid", manaUnitId);
			yjmxmap.put("ldt_begin", ldt_begin);
			yjmxmap.put("ldt_end", ldt_end);
			yjmxmap.put("ii_tjfs", REPORT_COUNTDATE_MZ);
			List<Map<String, Object>> ms_yjmxlist = BSPHISUtil.wf_tj_yjmx(
					yjmxmap, dao, ctx);
			for (int i = 0; i < ms_yjhslist.size(); i++) {
				ms_yjhslist.get(i).put("JGID", manaUnitId);
				Map<String, Object> yjhsReq = dao.doSave("create", BSPHISEntryNames.MS_YJHS,
						ms_yjhslist.get(i), false);
				Long hzxh = Long.parseLong(yjhsReq.get("HZXH") + "");// 获取主键
				for (int j = 0; j < ms_yjmxlist.size(); j++) {
					if (Long.parseLong(ms_yjhslist.get(i).get("KSDM") + "") == Long
							.parseLong(ms_yjmxlist.get(j).get("KSDM") + "")
							&& ms_yjhslist
									.get(i)
									.get("YSDM")
									.toString()
									.equals(ms_yjmxlist.get(j).get("YSDM")
											.toString())
							&& Integer.parseInt(ms_yjhslist.get(i).get("TJFS")
									+ "") == Integer.parseInt(ms_yjmxlist
									.get(j).get("TJFS") + "")
							&& sdfdatetime.parse(
									sdfdatetime.format(ms_yjhslist.get(i).get(
											"GZRQ"))).getTime() == sdfdatetime
									.parse(sdfdatetime.format(ms_yjmxlist
											.get(j).get("GZRQ"))).getTime()) {
						ms_yjmxlist.get(j).put("HZXH", hzxh);
						ms_yjmxlist.get(j).put("JGID", manaUnitId);
						dao.doSave("create", BSPHISEntryNames.MS_YJMX, ms_yjmxlist.get(j),
								false);
					}
				}
			}
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "时间转换失败!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "统计失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "统计失败,数据库处理异常!", e);
		}
	}
}
