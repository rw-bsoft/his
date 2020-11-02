package phis.application.sup.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * 
 * @description 二级库房出库库管理Model
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class EquipmentWeighingManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(EquipmentWeighingManagementModel.class);

	public EquipmentWeighingManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doQueryJlxxListDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		int sfdj = 0;
		if (req.containsKey("sfdj")) {
			sfdj = parseInt(req.get("sfdj"));
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		try {
			parameters.put("al_kfxh", KFXH);
			parameterssize.put("al_kfxh", KFXH);
			// 返回list的查询语句
			String sql = "SELECT WZXH as WZXH, WZMC as WZMC, WZGG as WZGG, WZDW as WZDW, SUM(SL) as SL, WZJG as WZJG, KSDM as KSDM, CJXH as CJXH,CJMC as CJMC FROM (SELECT WZXH as WZXH, WZMC as WZMC, WZGG as WZGG, WZDW as WZDW, SUM(WZSL) as SL, WZJG as WZJG, KSDM as KSDM, CJXH as CJXH,CJMC as CJMC FROM (SELECT WL_WZZD.WZMC,WL_KSZC.KFXH,WL_WZZD.WZXH,WL_KSZC.CJXH,WL_WZZD.WZGG,WL_WZZD.WZDW, SUM(WL_KSZC.WZSL) WZSL,WL_KSZC.WZJG,WL_KSZC.KSDM,b.CJMC FROM WL_WZZD WL_WZZD, WL_KSZC WL_KSZC,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_KSZC.WZXH and WL_KSZC.CJXH=b.CJXH AND WL_WZZD.JLBZ = 1 AND WL_KSZC.KFXH = :al_kfxh GROUP BY WL_WZZD.WZMC,WL_WZZD.WZGG,WL_WZZD.WZDW, WL_WZZD.WZXH,WL_KSZC.CJXH,WL_KSZC.WZJG,WL_KSZC.KFXH,WL_KSZC.KSDM,b.CJMC UNION ALL SELECT WL_WZZD.WZMC, WL_WZKC.KFXH,WL_WZZD.WZXH,WL_WZKC.CJXH,WL_WZZD.WZGG,WL_WZZD.WZDW,SUM(WL_WZKC.WZSL) WZSL,WL_WZKC.WZJG,null KSDM,b.CJMC FROM WL_WZZD WL_WZZD, WL_WZKC WL_WZKC,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_WZKC.WZXH and WL_WZKC.CJXH=b.CJXH AND WL_WZZD.JLBZ = 1 AND WL_WZKC.KFXH = :al_kfxh GROUP BY WL_WZZD.WZMC,WL_WZZD.WZGG,WL_WZKC.CJXH,WL_WZZD.WZDW,WL_WZZD.WZXH,WL_WZKC.WZJG,WL_WZKC.KFXH,b.CJMC UNION ALL SELECT WL_WZZD.WZMC,WL_KSZC.KFXH,WL_WZZD.WZXH,WL_KSZC.CJXH,WL_WZZD.WZGG,WL_WZZD.WZDW,0 - COUNT(WL_JLXX.WZXH) WZSL,WL_KSZC.WZJG,WL_KSZC.KSDM,b.CJMC FROM WL_WZZD WL_WZZD, WL_JLXX WL_JLXX, WL_KSZC WL_KSZC,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_JLXX.WZXH AND WL_KSZC.CJXH=b.CJXH AND WL_WZZD.WZXH = WL_KSZC.WZXH AND WL_WZZD.JLBZ = 1 AND WL_JLXX.ZFBZ = 0 AND WL_KSZC.KSDM = WL_JLXX.KSDM AND WL_JLXX.KFXH = :al_kfxh GROUP BY WL_WZZD.WZMC,WL_KSZC.CJXH,WL_WZZD.WZGG,WL_WZZD.WZXH,WL_WZZD.WZDW,WL_KSZC.KFXH,WL_KSZC.WZJG,WL_KSZC.KSDM,b.CJMC UNION ALL SELECT WL_WZZD.WZMC,WL_JLXX.KFXH,WL_WZZD.WZXH,WL_JLXX.CJXH,WL_WZZD.WZGG,WL_WZZD.WZDW,0 - COUNT(WL_JLXX.WZXH) WZSL,WL_JLXX.WZDJ,WL_JLXX.KSDM,b.CJMC FROM WL_WZZD WL_WZZD, WL_JLXX WL_JLXX,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_JLXX.WZXH AND WL_JLXX.CJXH=b.CJXH AND WL_WZZD.JLBZ = 1 AND WL_JLXX.ZFBZ = 0 AND WL_JLXX.KSDM IS null AND WL_JLXX.KFXH = :al_kfxh GROUP BY WL_WZZD.WZMC, WL_JLXX.CJXH,WL_WZZD.WZGG,WL_WZZD.WZXH,WL_WZZD.WZDW,WL_JLXX.KFXH,WL_JLXX.WZDJ,WL_JLXX.KSDM,b.CJMC) A WHERE 0 ="
					+ sfdj
					+ " GROUP BY WZMC, WZGG, WZDW, WZJG, KSDM, WZXH, CJXH,CJMC UNION ALL SELECT WL_WZZD.WZXH as WZXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,COUNT(WL_JLXX.WZXH) as SL,WL_KSZC.WZJG as WZJG,WL_JLXX.KSDM as KSDM,WL_KSZC.CJXH as CJXH,b.CJMC as CJMC FROM WL_WZZD WL_WZZD, WL_JLXX WL_JLXX, WL_KSZC WL_KSZC,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_JLXX.WZXH AND WL_KSZC.CJXH=b.CJXH and WL_WZZD.WZXH = WL_KSZC.WZXH AND WL_WZZD.JLBZ = 1 AND WL_JLXX.ZFBZ = 0 AND WL_KSZC.KSDM = WL_JLXX.KSDM AND WL_JLXX.KFXH = :al_kfxh AND 1 ="
					+ sfdj
					+ " GROUP BY WL_WZZD.WZMC,WL_WZZD.WZGG,WL_WZZD.WZXH,WL_WZZD.WZDW,WL_KSZC.KFXH,WL_KSZC.WZJG,WL_KSZC.CJXH,WL_JLXX.KSDM,b.CJMC UNION ALL SELECT WL_WZZD.WZXH as WZXH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,COUNT(WL_JLXX.WZXH) as SL,WL_JLXX.WZDJ as WZJG,WL_JLXX.KSDM as KSDM,WL_JLXX.CJXH as CJXH,b.CJMC as CJMC FROM WL_WZZD WL_WZZD, WL_JLXX WL_JLXX,WL_SCCJ b WHERE WL_WZZD.WZXH = WL_JLXX.WZXH and WL_JLXX.CJXH=b.CJXH AND WL_WZZD.JLBZ = 1 AND WL_JLXX.ZFBZ = 0 AND WL_JLXX.KSDM IS null AND WL_JLXX.KFXH = :al_kfxh AND 1 ="
					+ sfdj
					+ " GROUP BY WL_WZZD.WZMC,WL_JLXX.CJXH,WL_WZZD.WZGG,WL_WZZD.WZXH,WL_WZZD.WZDW,WL_JLXX.KFXH,WL_JLXX.WZDJ,WL_JLXX.KSDM,b.CJMC) B having SUM(SL)>0 GROUP BY WZMC, WZGG, WZDW, WZJG, KSDM, WZXH, CJXH,CJMC ORDER BY WZMC";
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);

			List<Map<String, Object>> inofList = dao.doSqlQuery(sql.toString(),
					parameters);
			List<Map<String, Object>> inofListsize = dao.doSqlQuery(
					sql.toString(), parameterssize);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_JLXX_LIST);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", inofListsize.size());
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "需要登记的计量信息查询失败！");
		}
	}

	public void doQueryJlsb(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			Date datefrom = sdf.parse(req.get("datefrom") + "");
			Date dateto = sdf.parse(req.get("dateto") + "");
			System.out.println(datefrom + "<>" + dateto);
			int jlqjfl = 0;
			if (req.containsKey("jlqjfldics")) {
				if (req.get("jlqjfldics") != null
						&& req.get("jlqjfldics") != ""
						&& !"null".equals(req.get("jlqjfldics"))
						&& !"".equals(req.get("jlqjfldics"))) {
					jlqjfl = parseInt(req.get("jlqjfldics"));
				}
			}
			int jlfl = 0;
			if (req.containsKey("jlfldics")) {
				if (req.get("jlfldics") != null && req.get("jlfldics") != ""
						&& !"null".equals(req.get("jlfldics"))
						&& !"".equals(req.get("jlfldics"))) {
					jlfl = parseInt(req.get("jlfldics"));
				}
			}
			long kdsm = 0;
			if (req.containsKey("zyksdics")) {
				if (req.get("zyksdics") != null && req.get("zyksdics") != ""
						&& !"null".equals(req.get("zyksdics"))
						&& !"".equals(req.get("zyksdics"))) {
					kdsm = parseInt(req.get("zyksdics"));
				}
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> parameterssize = new HashMap<String, Object>();
			parameters.put("ll_kfxh", KFXH);
			parameters.put("dt_qsrq", datefrom);
			parameters.put("dt_jsrq", dateto);
			parameterssize.put("ll_kfxh", KFXH);
			parameterssize.put("dt_qsrq", datefrom);
			parameterssize.put("dt_jsrq", dateto);
			// 返回list的查询语句
			StringBuffer sbflsql = new StringBuffer(
					"SELECT WL_JLXX.JLBH as JLBH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_JLXX.KSDM as KSDM,WL_JLXX.CJXH as CJXH,WL_JLXX.DWXH as DWXH,WL_JLXX.JLQJFL as JLQJFL,WL_JLXX.JLLB as JLLB,WL_JLXX.JDZQ as JDZQ,WL_JLXX.CCBH as CCBH,WL_JLXX.JDRQ as JDRQ,WL_JLXX.JDJG as JDJG,WL_JLXX.QJBZ as QJBZ,WL_JLXX.XCJD as XCJD,WL_JLXX.DQJDXH as DQJDXH, WL_JLXX.GRRQ as GRRQ,WL_JLXX.GRGH as GRGH,WL_JLXX.WZDJ as WZDJ,WL_JLXX.CLFW as CLFW,WL_JLXX.ZQDJ as ZQDJ,WL_JLXX.FDZ as FDZ,WL_JLXX.DDMC as DDMC,WL_JLXX.JDJL as JDJL,WL_ZCZB.WZZT as WZZT,WL_ZCZB.QYRQ as QYRQ,WL_SCCJ.CJMC as CJMC FROM WL_JLXX WL_JLXX LEFT JOIN WL_ZCZB WL_ZCZB ON WL_JLXX.ZBXH = WL_ZCZB.ZBXH,WL_WZZD WL_WZZD,WL_SCCJ WL_SCCJ WHERE WL_JLXX.JLXH in (select JLXH from WL_JDXX) and WL_WZZD.WZXH = WL_JLXX.WZXH and WL_JLXX.CJXH=WL_SCCJ.CJXH and WL_JLXX.ZFBZ = 0 and WL_JLXX.KFXH = :ll_kfxh AND ( ( WL_JLXX.JDRQ >= :dt_qsrq AND  WL_JLXX.JDRQ <= :dt_jsrq )  OR WL_JLXX.JDRQ IS NULL)");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			if (jlqjfl != 0) {
				sbflsql.append(" and WL_JLXX.JLQJFL=:JLQJFL");
				parameters.put("JLQJFL", jlqjfl);
				parameterssize.put("JLQJFL", jlqjfl);
			}
			if (jlfl != 0) {
				sbflsql.append(" and WL_JLXX.JLLB=:JLLB");
				parameters.put("JLLB", jlfl);
				parameterssize.put("JLLB", jlfl);
			}
			if (kdsm != 0) {
				sbflsql.append(" and WL_JLXX.KSDM=:KSDM");
				parameters.put("KSDM", kdsm);
				parameterssize.put("KSDM", kdsm);
			}
			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sbflsql.toString(), parameters);
			List<Map<String, Object>> inofListsize = dao.doSqlQuery(
					sbflsql.toString(), parameterssize);

			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_JLSBCX);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", inofListsize.size());
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "需要登记的计量信息查询失败！");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void doQueryJlxxInfoDetails(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String KFXH = user.getProperty("treasuryId") + "";
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		int zfzt = 0;
		if (req.containsKey("zfzt")) {
			zfzt = parseInt(req.get("zfzt"));
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		try {
			parameters.put("al_Kfxh", KFXH);
			parameters.put("al_ztbz", zfzt);
			parameterssize.put("al_Kfxh", KFXH);
			parameterssize.put("al_ztbz", zfzt);
			// 返回list的查询语句
			String sql = "SELECT WL_JLXX.JLXH as JLXH,WL_JLXX.JGID as JGID,WL_JLXX.KFXH as KFXH,WL_JLXX.JLBH as JLBH,WL_JLXX.ZBXH as ZBXH,WL_JLXX.WZXH as WZXH,WL_JLXX.CJXH as CJXH,WL_JLXX.KSDM as KSDM,WL_JLXX.DWXH as DWXH,WL_JLXX.CCBH as CCBH,WL_JLXX.WZDW as WZDW,WL_JLXX.WZDJ as WZDJ,WL_JLXX.GRRQ as GRRQ,WL_JLXX.GRGH as GRGH,WL_JLXX.JLQJFL as JLQJFL,WL_JLXX.JLLB as JLLB,WL_JLXX.CLFW as CLFW,WL_JLXX.ZQDJ as ZQDJ,WL_JLXX.FDZ as FDZ,WL_JLXX.JDZQ as JDZQ,WL_JLXX.JDRQ as JDRQ,WL_JLXX.JDJG as JDJG,WL_JLXX.QJBZ as QJBZ,WL_JLXX.XCJD as XCJD,WL_JLXX.DQJDXH as DQJDXH,WL_JLXX.ZFBZ as ZFBZ,WL_JLXX.DDMC as DDMC,WL_JLXX.CCRQ as CCRQ,WL_JLXX.JDJL as JDJL,WL_JLXX.HGZH as HGZH,WL_JLXX.SJQD as SJQD,WL_JLXX.BGGH as BGGH,WL_WZZD.WZMC as WZMC,WL_GHDW.DWMC as DWMC,WL_SCCJ.CJMC as CJMC FROM WL_JLXX WL_JLXX left outer join WL_GHDW WL_GHDW on WL_GHDW.DWXH=WL_JLXX.DWXH,WL_WZZD WL_WZZD,WL_SCCJ WL_SCCJ WHERE WL_JLXX.WZXH=WL_WZZD.WZXH and WL_JLXX.CJXH=WL_SCCJ.CJXH  and WL_JLXX.KFXH = :al_Kfxh AND WL_JLXX.ZFBZ = :al_ztbz";
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(sql.toString(),
					parameters);
			List<Map<String, Object>> inofListsize = dao.doSqlQuery(
					sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(inofList,
					BSPHISEntryNames.WL_JLXX_INFO);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", inofListsize.size());
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询已登记的计量信息失败！");
		}
	}

	public void doQueryWZXXList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		long wzxh = parseLong(req.get("wzxh"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("WZXH", wzxh);
		try {
			Map<String, Object> wzxxMap = dao
					.doLoad("select a.WZMC as WZMC,a.WZGG as WZGG,a.WZDW as WZDW,a.GLFS as GLFS,a.WZXH as WZXH,b.CJMC as CJMC,b.CJXH as CJXH,c.WZJG as WZDJ from WL_WZZD a,WL_SCCJ b,WL_WZCJ c where b.CJXH=c.CJXH and a.WZXH=c.WZXH and a.WZXH=:WZXH",
							parameters);
			if (parseInt(wzxxMap.get("GLFS")) == 2) {
				Map<String, Object> kszcMap = dao
						.doLoad("select KSDM as KSDM,WZSL as SYSL,0 as ZBXH from WL_KSZC where WZXH=:WZXH",
								parameters);
				if (wzxxMap != null) {
					wzxxMap.putAll(kszcMap);
				}
			} else if (parseInt(wzxxMap.get("GLFS")) == 3) {
				List<Map<String, Object>> zczbList = dao
						.doSqlQuery(
								"select ZYKS as KSDM,CZYZ as SYSL,ZBXH as ZBXH from WL_ZCZB where WZXH=:WZXH and ZBXH not in(select nvl(ZBXH,0) from WL_JLXX)",
								parameters);
				if (zczbList.size() > 0) {
					wzxxMap.putAll(zczbList.get(0));
				}
			}
			res.put("wzxxjson", wzxxMap);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询物资信息失败！");
		}
	}

	public void doQueryJLXXUPDATE(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		long jlxh = parseLong(req.get("jlxh"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_Jlxh", jlxh);
		try {
			List<Map<String, Object>> jlxxList = dao
					.doSqlQuery(
							"SELECT WL_JLXX.JLXH as JLXH,WL_JLXX.KFXH as KFXH,WL_JLXX.JGID as JGID,WL_JLXX.JLBH as JLBH,WL_JLXX.ZBXH as ZBXH,WL_JLXX.WZXH as WZXH,WL_JLXX.CJXH as CJXH,WL_JLXX.KSDM as KSDM,WL_JLXX.DWXH as DWXH,WL_JLXX.CCBH as CCBH,WL_JLXX.WZDW as WZDW,WL_JLXX.WZDJ as WZDJ,WL_JLXX.GRRQ as GRRQ,WL_JLXX.GRGH as GRGH,WL_JLXX.JLQJFL as JLQJFL,WL_JLXX.JLLB as JLLB,WL_JLXX.CLFW as CLFW,WL_JLXX.ZQDJ as ZQDJ,WL_JLXX.FDZ as FDZ,WL_JLXX.JDZQ as JDZQ,WL_JLXX.JDRQ as JDRQ,WL_JLXX.JDJG as JDJG,WL_JLXX.QJBZ as QJBZ,WL_JLXX.XCJD as XCJD,WL_JLXX.DQJDXH as DQJDXH,WL_JLXX.ZFBZ as ZFBZ,WL_JLXX.DDMC as DDMC,WL_JLXX.CCRQ as CCRQ,WL_JLXX.JDJL as JDJL,WL_JLXX.HGZH as HGZH,WL_JLXX.SJQD as SJQD,WL_JLXX.BGGH as BGGH,WL_WZZD.WZMC as WZMC,WL_WZZD.WZGG as WZGG,WL_WZZD.WZDW as WZDW,WL_WZZD.ZBLB as ZBLB,WL_WZZD.GLFS as GLFS,WL_GHDW.DWMC as DWMC,0.00 as SBSL,1 as WZSL,0.00 as SYSL,WL_ZCZB.WZBH as WZBH FROM WL_WZZD WL_WZZD,WL_JLXX WL_JLXX LEFT OUTER JOIN WL_ZCZB WL_ZCZB ON (WL_JLXX.ZBXH = WL_ZCZB.ZBXH) LEFT OUTER JOIN WL_GHDW WL_GHDW ON (WL_GHDW.DWXH = WL_JLXX.DWXH) WHERE WL_JLXX.WZXH = WL_WZZD.WZXH AND WL_JLXX.JLXH = :al_Jlxh",
							parameters);
			if (jlxxList.size() > 0) {
				res.put("jlxxjson", jlxxList.get(0));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询物资信息失败！");
		}
	}

	public void doSaveJLXX(Map<String, Object> req, Map<String, Object> res,
			String op, String wzsl, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		int KFXH = parseInt(user.getProperty("treasuryId"));
		String jgid = user.getManageUnit().getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (req.get("CCRQ") != null && req.get("CCRQ") != ""
					&& !"null".equals(req.get("CCRQ"))
					&& !"".equals(req.get("CCRQ"))) {
				req.put("CCRQ", sdf.parse(req.get("CCRQ") + ""));
			}
			if (req.get("GRRQ") != null && req.get("GRRQ") != ""
					&& !"null".equals(req.get("GRRQ"))
					&& !"".equals(req.get("GRRQ"))) {
				req.put("GRRQ", sdf.parse(req.get("GRRQ") + ""));
			}
			if (req.get("XCJD") != null && req.get("XCJD") != ""
					&& !"null".equals(req.get("XCJD"))
					&& !"".equals(req.get("XCJD"))) {
				req.put("XCJD", sdf.parse(req.get("XCJD") + ""));
			}
			for (int i = 0; i < parseInt(wzsl); i++) {
				req.put("JGID", jgid);
				req.put("KFXH", KFXH);
				req.put("ZFBZ", 0);
				Map<String, Object> jlxhMap = dao.doSave(op,
						BSPHISEntryNames.WL_JLXX_FORM, req, false);
				long jlxh = parseLong(jlxhMap.get("JLXH"));
				String jlxhstr = jlxhMap.get("JLXH") + "";
				String jlbh = getJLBH(jlxhstr);
				parameters.put("JLXH", jlxh);
				parameters.put("JLBH", jlbh);
				dao.doUpdate("update WL_JLXX set JLBH=:JLBH where JLXH=:JLXH",
						parameters);
			}
		} catch (ValidateException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "计量管理保存失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "计量管理保存失败！");
		} catch (ParseException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "日期转换失败！");
		}
	}

	public void doUpdateJLXX(Map<String, Object> req, Map<String, Object> res,
			String op, Context ctx) throws ModelDataOperationException {
		try {
			dao.doSave(op, BSPHISEntryNames.WL_JLXX_FORM, req, false);
		} catch (ValidateException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "计量管理修改失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "计量管理修改失败！");
		}
	}

	public void doRemoveEquipment(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			Long JLXH = Long.parseLong(req.get("body") + "");
			int ZFBZ = Integer.parseInt(req.get("op") + "");
			Map<String, Object> parMap = new HashMap<String, Object>();
			parMap.put("JLXH", JLXH);
			List<Map<String, Object>> jdxxlist = dao
					.doQuery(
							"select JDXH as JDXH from WL_JDXX where JLXH=:JLXH",
							parMap);
			if (jdxxlist.size() > 0) {
				res.put(Service.RES_CODE, 601);
			} else {
				parMap.put("ZFBZ", ZFBZ);
				dao.doSave("update", BSPHISEntryNames.WL_JLXX, parMap, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			throw new ModelDataOperationException("作废失败!", e);
		}
	}

	public String getJLBH(String jlxh) {
		StringBuffer JLBH = new StringBuffer();
		if (jlxh.length() == 1) {
			JLBH.append("000000000").append(jlxh);
		} else if (jlxh.length() == 2) {
			JLBH.append("00000000").append(jlxh);
		} else if (jlxh.length() == 3) {
			JLBH.append("0000000").append(jlxh);
		} else if (jlxh.length() == 4) {
			JLBH.append("000000").append(jlxh);
		} else if (jlxh.length() == 5) {
			JLBH.append("00000").append(jlxh);
		} else if (jlxh.length() == 6) {
			JLBH.append("0000").append(jlxh);
		} else if (jlxh.length() == 7) {
			JLBH.append("000").append(jlxh);
		} else if (jlxh.length() == 8) {
			JLBH.append("00").append(jlxh);
		} else if (jlxh.length() == 9) {
			JLBH.append("0").append(jlxh);
		} else if (jlxh.length() == 10) {
			JLBH.append(jlxh);
		}
		return "JL" + JLBH.toString();
	}

	public int parseInt(Object o) {
		if (o == null || o == "" || "".equals(o) || "null".equals(o)) {
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
