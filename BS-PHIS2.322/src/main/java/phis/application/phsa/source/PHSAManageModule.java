package phis.application.phsa.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.organ.ManageUnit;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.dictionary.support.XMLDictionary;
import ctd.resource.ResourceCenter;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.xml.XMLHelper;

/**
 * 全院查询
 * 
 * @author Administrator
 * 
 */
public class PHSAManageModule {
	private BaseDAO dao;

	public PHSAManageModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 首页信息查询
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryHomeInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO phsaDao, Context ctx)
			throws ServiceException {
		Map<String, Object> map = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String tjsj = "";
		if (map == null) {
			tjsj = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(), 1));
		} else {
			tjsj = String.valueOf(map.get("queryDate"));
		}
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("TJSJ", tjsj);
		List<Map<String, Object>> resultList = null;
		// 医疗总收入、门诊人次、门诊均次费用、大处方数、入院人次、出院人次、在院人数、危重人数、高血压建档数、糖尿病建档数、老年人建档数、居民签约数、高血压控制率、糖尿病控制率。
		try {
			// 总收入
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("SELECT round(sum(GHF + YPF + YLF + QTF),2) as ZSR FROM FYMX WHERE JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("ZSR", 0);// 总收入
			} else {
				resultMap.put("ZSR",
						Object2Double(resultList.get(0).get("ZSR")));
			}
			// 门诊人次、门诊均次费用
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(MZRC) as MZRC, round(sum(GHJE+YLJE+YPJE+MZ_ZSR)/sum(MZRC),2) as JCFY from MZFYFX where  JGID like :JGID and to_char(TJSJ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("MZRC", 0);
				resultMap.put("JCFY", 0);
			} else {
				resultMap.put("MZRC",
						Object2Double(resultList.get(0).get("MZRC")));// 门诊人次
				double jcfy = Object2Double(resultList.get(0).get("JCFY"));
				jcfy = Math.floor(jcfy * 100) / 100;
				resultMap.put("JCFY", jcfy);// 门诊均次费用
			}
			// 大处方数
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(DCFS) as DCFS from MZ_DCF WHERE JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("DCFS", 0);
			} else {
				resultMap.put("DCFS",
						Object2Double(resultList.get(0).get("DCFS")));// 门诊大处方数
			}
			// 出院(1)、入院(2)、危重(3)、在院(4)人数
			// 入院人次
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(TJRS) as RYRS from RCTJ where TJBZ = '2' and JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("RYRS", 0);
			} else {
				resultMap.put("RYRS",
						Object2Double(resultList.get(0).get("RYRS")));// 入院人数
			}
			// 出院人次
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(TJRS) as CYRS from RCTJ where TJBZ = '1' and JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("CYRS", 0);
			} else {
				resultMap.put("CYRS",
						Object2Double(resultList.get(0).get("CYRS")));// 出院人数
			}
			// 在院人数
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(TJRS) as ZYRS from RCTJ where TJBZ = '4' and JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("ZYRS", 0);
			} else {
				resultMap.put("ZYRS",
						Object2Double(resultList.get(0).get("ZYRS")));// 在院人数
			}
			// 危重人数
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(TJRS) as WZRS from RCTJ where TJBZ = '3' and JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd') = :TJSJ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("WZRS", 0);
			} else {
				resultMap.put("WZRS",
						Object2Double(resultList.get(0).get("WZRS")));// 危重人数
			}
			// 高血压建档数、糖尿病建档数、老年人建档数、居民签约数、高血压控制率、糖尿病控制率
			parameterMap.put("JGID", manageUnit + "%");
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(GXYJDS) as GXYJDS, sum(TNBJDS) as TNBJDS, sum(LNRJDS) as LNRJDS, sum(QKQYRS) as JMQYS from phsa_jdszb where GXJGYS like:JGID and to_char(JDSJ,'yyyy-MM-dd') = :TJSJ ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("GXYJDS", 0);
				resultMap.put("TNBJDS", 0);
				resultMap.put("LNRJDS", 0);
				resultMap.put("JMQYS", 0);
			} else {
				resultMap.put("GXYJDS",
						Object2Double(resultList.get(0).get("GXYJDS")));// 高血压建档数
				resultMap.put("TNBJDS",
						Object2Double(resultList.get(0).get("TNBJDS")));// 糖尿病建档数
				resultMap.put("LNRJDS",
						Object2Double(resultList.get(0).get("LNRJDS")));// 老年人建档数
				resultMap.put("JMQYS",
						Object2Double(resultList.get(0).get("JMQYS")));// 居民签约数
			}
			// 高血压控制率、糖尿病控制率
			sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select  sum(GXYKZRS)/sum(GXYGLRS) as GXYKZL,  sum(TNBKZRS)/sum(TNBGLRS) as TNBKZL from phsa_sqglzb where GXJGYS like :JGID and to_char(TJSJ,'yyyy-MM-dd') = :TJSJ ");
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList == null || resultList.size() == 0) {
				resultMap.put("GXYKZL", 0);
				resultMap.put("TNBKZL", 0);
			} else {
				String GXYKZL = String.format(
						"%1$.2f",
						resultList.get(0).get("GXYKZL") == null ? 0 : Double
								.parseDouble(resultList.get(0).get("GXYKZL")
										+ "") * 100);
				String TNBKZL = String.format(
						"%1$.2f",
						resultList.get(0).get("TNBKZL") == null ? 0 : Double
								.parseDouble(resultList.get(0).get("TNBKZL")
										+ "") * 100);
				resultMap.put("GXYKZL", GXYKZL + "%");// 高血压控制率
				resultMap.put("TNBKZL", TNBKZL + "%");// 糖尿病控制率
			}
			res.putAll(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 总收入数据明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doZSRDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}
		sqlBuilder
				.append("select KSDM as KSDM, LB as LB, round(sum(GHF),2) as GHF, round(sum(YPF),2) as YPF, round(sum(YLF),2) as YLF, round(sum(QTF),2) as QTF from FYMX where JGID like :JGID and to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE group by KSDM, LB");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			if (resultList != null && resultList.size() > 0) {
				// 统计门诊和住院的医疗费用合计、药品费用合计、其他费用合计并设置三项合计金额
				double mz_ylfhj = 0, mz_ypfhj = 0, mz_qtfhj = 0, zy_ylfhj = 0, zy_ypfhj = 0, zy_qtfhj = 0;
				for (Map<String, Object> map : resultList) {
					if ("1".equals(map.get("LB") + "")) {// 门诊
						mz_ylfhj += Object2Double(map.get("YLF"));
						mz_ypfhj += Object2Double(map.get("YPF"));
						mz_qtfhj += Object2Double(map.get("QTF"));
					} else {// 住院
						zy_ylfhj += Object2Double(map.get("YLF"));
						zy_ypfhj += Object2Double(map.get("YPF"));
						zy_qtfhj += Object2Double(map.get("QTF"));
					}
					map.put("HJFY",
							String.format("%.2f", Object2Double(map.get("YLF"))
									+ Object2Double(map.get("YPF"))
									+ Object2Double(map.get("QTF"))
									+ Object2Double(map.get("GHF"))));
				}
				// 计算门诊和住院的医疗、药品、其他 比例
				double HJFY = 1;
				for (Map<String, Object> map : resultList) {
					HJFY = Double.parseDouble(map.get("HJFY") + "");
					if ("1".equals(map.get("LB") + "")) {// 门诊
						map.put("YL_ZB",
								String.format("%.2f",
										Object2Double(map.get("YLF")) / HJFY
												* 100)
										+ "%");
						map.put("GH_ZB",
								String.format("%.2f",
										Object2Double(map.get("GHF")) / HJFY
												* 100)
										+ "%");
						map.put("YP_ZB",
								String.format("%.2f",
										Object2Double(map.get("YPF")) / HJFY
												* 100)
										+ "%");
						map.put("QT_ZB",
								String.format("%.2f",
										Object2Double(map.get("QTF")) / HJFY
												* 100)
										+ "%");
					} else {// 住院
						map.put("YL_ZB",
								String.format("%.2f",
										Object2Double(map.get("YLF")) / HJFY
												* 100)
										+ "%");
						map.put("GH_ZB", "0.00%");
						map.put("YP_ZB",
								String.format("%.2f",
										Object2Double(map.get("YPF")) / HJFY
												* 100)
										+ "%");
						map.put("QT_ZB",
								String.format("%.2f",
										Object2Double(map.get("QTF")) / HJFY
												* 100)
										+ "%");
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.FYMX");
		} catch (Exception e) {
			e.printStackTrace();
		}

		res.put("body", resultList);
	}

	/**
	 * 均次费用明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doJCFYDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 还差一个合计
		sqlBuilder
				.append("select KSDM as KSDM, round(sum(GHJE),2) as GHF, round(sum(YLJE),2) as YLF, round(sum(YPJE),2) as YPF, sum(MZRC) as MZRC,round(sum(MZ_ZSR),2) as MZ_ZSR, round(sum(GHJE+YLJE+YPJE+MZ_ZSR),2) as HJFY from MZFYFX ");
		sqlBuilder
				.append("where JGID like :JGID and to_char(TJSJ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJSJ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			int mzrc = 0;
			// 重新计算合计人次
			if (resultList != null && resultList.size() > 0) {
				for (Map<String, Object> map : resultList) {
					// 当门诊人次为0时，因除数不能为0，所以将其改为1，不为0时用其原有值
					mzrc = Object2Integer(map.get("MZRC")) == 0 ? 1
							: Object2Integer(map.get("MZRC"));
					map.put("JCFY", Object2Double(map.get("HJFY")) / mzrc);// 计算合计人次
				}
			}
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.PHSA_JCFY");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 门诊人次明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doMZRCDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 还差一个合计
		sqlBuilder.append("select KSDM as KSDM,  sum(MZRC) as RS from MZFYFX ");
		sqlBuilder
				.append("where JGID LIKE :JGID and to_char(TJSJ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJSJ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 计算占比
	 * 
	 * @param resultList
	 */
	private void jsZB(List<Map<String, Object>> resultList) {
		double rs = 0;
		// 计算门诊总人次
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> map : resultList) {
				rs += Object2Integer(map.get("RS"));
			}
		}
		// 重新计算占比
		if (resultList != null && resultList.size() > 0) {
			for (Map<String, Object> map : resultList) {
				rs = rs == 0 ? 1 : rs;
				map.put("ZB",
						String.format("%.2f", Object2Double(map.get("RS"))
								* 100 / rs)
								+ "%");// 计算占比
			}
		}
	}

	/**
	 * 大处方数明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doDCFSDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		//
		sqlBuilder.append("select KSDM as KSDM,  sum(DCFS) as RS from MZ_DCF ");
		sqlBuilder
				.append("where JGID LIKE :JGID and  to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 入院人数明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doRYRSDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 出院(1)、入院(2)、危重(3)、在院(4)人数
		sqlBuilder
				.append("select KSDM as KSDM,  sum(TJRS) as RS from RCTJ where TJBZ=2 and ");
		sqlBuilder
				.append(" JGID LIKE :JGID and  to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 出院人数明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCYRSDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String beginDate = "", endDate = "";
		if (body == null || body.get("beginDate") == null
				|| body.get("endDate") == null) {
			beginDate = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(),
					1)) + " 00:00:00";
			endDate = BSHISUtil
					.toString(BSHISUtil.getDateBefore(new Date(), 1))
					+ " 23:59:59";
		} else {
			beginDate = String.valueOf(body.get("beginDate")) + " 00:00:00";
			endDate = String.valueOf(body.get("endDate")) + " 23:59:59";
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("BEGINDATE", beginDate);
		parameterMap.put("ENDDATE", endDate);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 出院(1)、入院(2)、危重(3)、在院(4)人数
		sqlBuilder
				.append("select KSDM as KSDM,  sum(TJRS) as RS from RCTJ where JGID LIKE :JGID and TJBZ=1 and ");
		sqlBuilder
				.append(" to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') >= :BEGINDATE and to_char(TJRQ,'yyyy-MM-dd HH24:MI:SS') < :ENDDATE ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 在院人数明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doZYRSDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String tjrq = "";
		if (body == null || body.get("beginDate") == null) {
			tjrq = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(), 1));
		} else {
			tjrq = String.valueOf(body.get("beginDate"));
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("TJRQ", tjrq);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 出院(1)、入院(2)、危重(3)、在院(4)人数
		sqlBuilder
				.append("select KSDM as KSDM,  sum(TJRS) as RS from RCTJ where JGID LIKE :JGID and TJBZ=4 and ");
		sqlBuilder.append("  to_char(TJRQ,'yyyy-MM-dd') = :TJRQ ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 危重人数明细
	 * 
	 * @param req
	 * @param res
	 * @param phsaDao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doWZRSDetails(Map<String, Object> req, Map<String, Object> res,
			BaseDAO phsaDao, Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> body = req.get("body") == null ? null
				: (Map<String, Object>) req.get("body");
		String tjrq = "";
		if (body == null || body.get("beginDate") == null) {
			tjrq = BSHISUtil.toString(BSHISUtil.getDateBefore(new Date(), 1));
		} else {
			tjrq = String.valueOf(body.get("beginDate"));
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("JGID", manageUnit + "%");
		parameterMap.put("TJRQ", tjrq);

		List<Map<String, Object>> resultList = null;
		StringBuilder sqlBuilder = new StringBuilder();
		// 出院(1)、入院(2)、危重(3)、在院(4)人数
		sqlBuilder
				.append("select KSDM as KSDM,  sum(TJRS) as RS from RCTJ where JGID LIKE :JGID and TJBZ=3 and ");
		sqlBuilder.append("  to_char(TJRQ,'yyyy-MM-dd') = :TJRQ ");
		sqlBuilder.append(" group by KSDM");
		try {
			resultList = phsaDao
					.doSqlQuery(sqlBuilder.toString(), parameterMap);
			jsZB(resultList);// 计算占比
			SchemaUtil.setDictionaryMassageForList(resultList,
					"phis.application.phsa.schemas.DATA_DETAILS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("body", resultList);
	}

	/**
	 * 排班医生查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();
		List<Object> cnd = (List<Object>) req.get("dateUp");
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters3 = new HashMap<String, Object>();
		try {
			String dateUp = cnd.get(0) + "";
			;
			String dateDown = cnd.get(6) + "";
			;
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			Date date_bin = matter.parse(dateUp + "");
			Date date_end = matter.parse(dateDown + "");
			parameters.put("JGID", manaUnitId);
			parameters.put("date_end", date_end);
			parameters.put("date_bin", date_bin);
			// 获取排班基本
			StringBuffer hql = new StringBuffer(
					"SELECT  NVL(c.PERSONNAME,' ') as PERSONNAME,b.ZBLB as ZBLB,b.GZRQ as GZRQ,a.KSMC as KSMC,b.YSDM as YSDM,b.KSDM as KSDM,b.ZBLB as ZBLB,a.ZJMZ  as ZJMZ FROM MS_YSPB  b  left join MS_GHKS a  on   a.JGID = b.JGID and a.KSDM = b.KSDM "
							+ " left join SYS_PERSONNEL c on b.YSDM=c.PERSONID "
							+ "where a.JGID=:JGID "
							+ "and b.GZRQ>=:date_bin "
							+ " and  b.GZRQ<=:date_end");
			hql.append(" ORDER BY b.GZRQ,a.KSMC");
			List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
			list_ret = dao.doSqlQuery(hql.toString(), parameters);
			// list_ret.add(0, pbksList);
			// 获取排班科室
			parameters3.put("JGID", manaUnitId);
			List<Map<String, Object>> list_ret3 = new ArrayList<Map<String, Object>>();
			StringBuffer hql3 = new StringBuffer();
			hql3.append(" SELECT a.KSDM as KSDM, a.KSMC as KSMC from MS_GHKS a where a.JGID =:JGID");
			hql3.append(" ORDER BY a.KSDM");
			list_ret3 = dao.doSqlQuery(hql3.toString(), parameters3);
			Map<String, Object> ksmcMap = new HashMap<String, Object>();
			if (list_ret3.size() > 0) {
				for (int i = 0; i < list_ret3.size(); i++) {
					ksmcMap.put("KSMC" + i, list_ret3.get(i).get("KSMC"));
					ksmcMap.put("KSDM" + i, list_ret3.get(i).get("KSDM"));
				}
			}
			ksmcMap.put("LENGTH", list_ret3 == null ? 0 : list_ret3.size());
			list_ret.add(0, ksmcMap);
			res.put("body", list_ret);
		} catch (PersistentDataOperationException e) {
			// logger.error("For inquiries, the default payment method fails.",
			// e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班医生查询失败！");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 对象转换成double数据
	 * 
	 * @param object
	 * @return
	 */
	private double Object2Double(Object object) {
		if (object == null) {
			return 0;
		} else {
			return Double.parseDouble(object + "");
		}
	}

	/**
	 * 对象转换成int数据
	 * 
	 * @param object
	 * @return
	 */
	private int Object2Integer(Object object) {
		if (object == null) {
			return 0;
		} else {
			return Integer.parseInt(object + "");
		}
	}

	@SuppressWarnings("unchecked")
	public void queryCRBDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String manageUnit = "";
		if (body.get("manageUnit") instanceof Map) {
			manageUnit = (String) ((Map<String, Object>) body.get("manageUnit"))
					.get("key");
		} else {
			manageUnit = (String) body.get("manageUnit");
		}
		String beginDate = (String) body.get("beginDate");
		if (beginDate.contains("T")) {
			beginDate = beginDate.replace("T", " ");
		}
		String endDate = (String) body.get("endDate");
		if (endDate.contains("T")) {
			endDate = endDate.replace("T", " ");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("beginDate", beginDate);
		parameters.put("endDate", endDate);
		parameters.put("manageUnit", manageUnit + "%");
		String sql = "select CRBBGKS.totalcount as CRBBGKS,CRBBLS.totalcount as CRBBLS,"
				+ "BGJSBLS.totalcount as BGJSBLS , m.manaunit as GXJG from  "
				+ "(select count(*) as totalcount,c.reportUnit as manaunit from idr_report c where "
				+ "c.createDate >=  to_date(:beginDate, 'yyyy-mm-dd hh24:mi:ss')  "
				+ "and c.createDate <=  to_date(:endDate, 'yyyy-mm-dd hh24:mi:ss') "
				+ "and c.reportUnit  like :manageUnit group by c.reportUnit) CRBBGKS,"
				+ "(select count(distinct d.BRID) as totalcount,d.jgid as manaunit from MS_BRZD d, GY_JBBM e  "
				+ "where d.zdxh = e.jbxh and e.jbbgk = '06' and "
				+ "d.zdsj >= to_date(:beginDate, 'yyyy-mm-dd hh24:mi:ss')  "
				+ "and d.zdsj <=  to_date(:endDate, 'yyyy-mm-dd hh24:mi:ss') "
				+ "and d.jgid like :manageUnit  group by d.jgid) CRBBLS,"
				+ "(select count(*) as totalcount,c.reportUnit as manaunit from idr_report c, MS_BRZD d, MS_BRDA e, GY_JBBM f where "
				+ "c.empiid = e.empiid and e.brid = d.brid and  d.zdxh = f.jbxh and f.jbbgk = '06' and "
				+ "((round(to_number(to_date(to_char(c.createDate, 'yyyy-mm-dd hh24:mi:ss'), "
				+ "'yyyy-mm-dd hh24:mi:ss') - d.zdsj) * 24) < 2) or "
				+ "(round(to_number(to_date(to_char(c.createDate,'yyyy-mm-dd hh24:mi:ss'),"
				+ "'yyyy-mm-dd hh24:mi:ss') - d.zdsj) * 24) < 24 and c.categoryAInfectious is null "
				+ "and c.otherCategoryInfectious is null)) and "
				+ "c.createDate >=to_date(:beginDate, 'yyyy-mm-dd hh24:mi:ss') "
				+ "and c.createDate <=to_date(:endDate, 'yyyy-mm-dd hh24:mi:ss') "
				+ "and c.reportUnit  like :manageUnit group by c.reportUnit)  BGJSBLS, "
				+ " (select t.manaunit from (select a.reportunit as manaunit from "
				+ "idr_report a group by a.reportunit union all select b.jgid as manaunit "
				+ "from MS_BRZD b group by b.jgid) t where t.manaunit like :manageUnit "
				+ "group by t.manaunit) m where m.manaunit=CRBBGKS.manaunit(+) "
				+ "and m.manaunit=CRBBLS.manaunit(+) and m.manaunit=BGJSBLS.manaunit(+)"
				+ " order by m.manaunit";
		List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			list_ret = dao.doSqlQuery(sql, parameters);
			int length = manageUnit.length();
			if (length == 2) {
				length = 4;
			} else if (length == 4) {
				length = 6;
			} else if (length == 6) {
				length = 9;
			} else if (length == 9) {
				length = 9;
			}
			List<String> manageIds = new ArrayList<String>();
			for (Map<String, Object> map : list_ret) {
				manageIds.add(map.get("GXJG") + "");
			}
			List<Map<String, Object>> manageList = getManageList();
			for (int i = 0; i < manageList.size(); i++) {
				Map<String, Object> manage = manageList.get(i);
				if (!manageIds.contains(manage.get("id") + "")
						&& (manage.get("id") + "").length() == length
						&& (manage.get("id") + "").startsWith(manageUnit)) {
					Map<String, Object> rem = new HashMap<String, Object>();
					rem.put("CRBBLS", 0);
					rem.put("CRBBGKS", 0);
					rem.put("BGJSBLS", 0);
					rem.put("GXJG", manage.get("id"));
					list_ret.add(rem);
				}
			}
			if (list_ret.size() == 0) {
				Map<String, Object> rem = new HashMap<String, Object>();
				rem.put("CRBBLS", 0);
				rem.put("CRBBGKS", 0);
				rem.put("BGJSBLS", 0);
				rem.put("GXJG", manageUnit);
				list_ret.add(rem);
			}
			list_ret = changeContainUnit(list_ret);
			for (Map<String, Object> map : list_ret) {
				String GXJG = (String) map.get("GXJG");
				if (GXJG == null
						|| (GXJG.length() != length)) {
					continue;
				}
				Map<String, Object> rem = new HashMap<String, Object>();
				double CRBBLS = Object2Double(map.get("CRBBLS"));
				double CRBBGKS = Object2Double(map.get("CRBBGKS"));
				double BGJSBLS = Object2Double(map.get("BGJSBLS"));
				if (CRBBLS == 0) {
					rem.put("CRBBGL", 0 + "%");
				} else {
					String CRBBGL = String.format("%1$.2f", CRBBGKS / CRBBLS
							* 100);
					rem.put("CRBBGL", CRBBGL + "%");
				}
				if (CRBBGKS == 0) {
					rem.put("CRBBGJSL", 0 + "%");
				} else {
					String CRBBGJSL = String.format("%1$.2f", BGJSBLS / CRBBGKS
							* 100);
					rem.put("CRBBGJSL", CRBBGJSL + "%");
				}
				rem.put("GXJG", map.get("GXJG"));
				result.add(rem);
			}
			result = SchemaUtil.setDictionaryMassageForList(result,
					"phis.application.phsa.schemas.PHSA_CRB");
			res.put("body", result);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询传染病失败！");
		}
	}

	private List<Map<String, Object>> changeContainUnit(
			List<Map<String, Object>> list_ret) {
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : list_ret) {
			Map<String, Object> m=new HashMap<String, Object>();
			String GXJG = (String) map.get("GXJG");
			double CRBBLS = Object2Double(map.get("CRBBLS"));
			double CRBBGKS = Object2Double(map.get("CRBBGKS"));
			double BGJSBLS = Object2Double(map.get("BGJSBLS"));
			for (Map<String, Object> map2 : list_ret) {
				String GXJG2 = (String) map2.get("GXJG");
				double CRBBLS2 = Object2Double(map2.get("CRBBLS"));
				double CRBBGKS2 = Object2Double(map2.get("CRBBGKS"));
				double BGJSBLS2 = Object2Double(map2.get("BGJSBLS"));
				if (GXJG2.startsWith(GXJG) && !GXJG2.equals(GXJG)) {
					CRBBLS = CRBBLS + CRBBLS2;
					CRBBGKS = CRBBGKS + CRBBGKS2;
					BGJSBLS = BGJSBLS + BGJSBLS2;
				}
			}
			m.put("GXJG", GXJG);
			m.put("CRBBLS", CRBBLS);
			m.put("CRBBGKS", CRBBGKS);
			m.put("BGJSBLS", BGJSBLS);
			result.add(m);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getManageList() {
		String domain = null;
		if (S.isEmpty(domain)) {
			domain = AppContextHolder.getName();
		}
		if (S.isEmpty(domain)) {
			domain = AppContextHolder.getConfigServerName();
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dictionary dic = null;
		try {
			dic = DictionaryController.instance().get(domain + ".@manageUnit");
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		HashMap<String, DictionaryItem> dicDoc = dic.getItems();
		for (Iterator it = dicDoc.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			DictionaryItem item = dicDoc.get(key);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", key);
			map.put("name", item.getText());
			list.add(map);
		}
		return list;
	}
}
