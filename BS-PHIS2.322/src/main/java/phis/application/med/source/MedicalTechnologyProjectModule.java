package phis.application.med.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

/**
 * 医技项目取消Module层
 * 
 * @author bsoft
 * 
 */
public class MedicalTechnologyProjectModule {
	protected Logger logger = LoggerFactory
			.getLogger(MedicalTechnologyProjectModule.class);
	private BaseDAO dao;

	public MedicalTechnologyProjectModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 初始化医技科室信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void initMTDept(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, String>> list = (List<Map<String, String>>) req
				.get("body");
		Map<String, String> map = list.get(0);
		StringBuilder sqlBuilder = new StringBuilder();
		Map<String, Object> parameter = new HashMap<String, Object>();
		try {
			sqlBuilder
					.append("SELECT COUNT(1) as NUM FROM YJ_KSXX WHERE KSDM=:al_DeptId AND JGID = :gl_jgid");
			parameter.put("al_DeptId",
					Long.parseLong(String.valueOf(map.get("al_DeptId"))));
			parameter.put("gl_jgid",
					Long.parseLong(String.valueOf(map.get("gl_jgid"))));
			// 查询当前机构，当前医技科室是否存在
			List<Map<String, Object>> coun = dao.doSqlQuery(
					sqlBuilder.toString(), parameter);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			if (total < 1) {
				// 不存在则往YJ_KSXX表中插入当前科室，当前机构
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("INSERT INTO YJ_KSXX(KSDM,JGID,DABM) VALUES(:al_DeptId,:gl_jgid,'0')");
				dao.doSqlUpdate(sqlBuilder.toString(), parameter);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块，初始化医技科室信息失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，初始化医技科室信息失败");
		}
	}

	/**
	 * 医技项目取消，门诊项目查询病人
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryMSPatient(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		// int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		// int pageSize = Integer.parseInt(req.get("pageSize").toString());
		String carno = req.get("carno") + "";
		long zxks = Long.parseLong(req.get("zxks") + "");
		try {
			Date adt_Begin = null;
			if (req.get("strdate") != null && req.get("strdate") != "") {
				adt_Begin = sdf.parse(req.get("strdate") + " 00:00:00");

			}
			Date adt_End = null;
			if (req.get("enddate") != null && req.get("enddate") != "") {
				adt_End = sdf.parse(req.get("enddate") + " 23:59:59");
			}
			List<Map<String, Object>> result = null;
			StringBuilder sqlBuilder = new StringBuilder();
			Map<String, Object> parameter = new HashMap<String, Object>();
			String KLX = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "KLX", "04",
					"01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
			parameter.put("nl_zxks", zxks);
			parameter.put("al_jgid", manaUnitId);
			sqlBuilder
					.append("SELECT MPI_Card.CardNo as JZKH,MS_YJ01.YJXH as YJXH, MS_YJ01.TJHM as TJHM, MS_BRDA.MZHM as MZHM, MS_YJ01.BRXM as BRXM, ");
			sqlBuilder
					.append(" MS_YJ01.ZXRQ as ZXRQ, MS_YJ01.KDRQ as KDRQ, MS_YJ01.HJGH as HJGH, GY_YLSF.FYMC as FYMC, MS_YJ02.YLXH as YLXH, ");
			sqlBuilder
					.append(" MS_YJ01.YSDM as YSDM, MS_YJ01.BRID as BRID, MS_YJ01.ZXKS as ZXKS, MS_YJ01.FPHM as FPHM, ");
			sqlBuilder
					.append(" MS_YJ01.KSDM as KSDM, MS_YJ01.ZXYS as ZXYS, MS_YJ01.ZFPB as ZFPB, MS_YJ01.ZXPB as ZXPB  FROM MS_YJ01, ");
			sqlBuilder
					.append(" MS_YJ02, GY_YLSF, MS_BRDA left join MPI_Card on MPI_Card.cardTypeCode="
							+ KLX
							+ " and MPI_Card.empiId=MS_BRDA.EMPIID WHERE (MS_YJ02.YJXH = MS_YJ01.YJXH) AND  ");
			sqlBuilder
					.append("(MS_YJ02.YLXH = GY_YLSF.FYXH) AND (MS_BRDA.BRID = MS_YJ01.BRID)");
			sqlBuilder
					.append(" AND (MS_YJ01.ZXPB = 1) AND  (MS_YJ01.ZFPB = 0) AND (MS_YJ02.YJZX = 1)");
			sqlBuilder.append(" AND ( MS_YJ01.ZXKS = :nl_zxks )");
			if (adt_Begin != null) {
				parameter.put("strdate", adt_Begin);
				sqlBuilder.append(" AND (MS_YJ01.ZXRQ >=:strdate)");
			}
			if (adt_End != null) {
				parameter.put("enddate", adt_End);
				sqlBuilder.append("  AND (MS_YJ01.ZXRQ <=:enddate)");
			}
			sqlBuilder
					.append(" AND ( MS_YJ01.JGID =  :al_jgid) AND ( MS_YJ02.JGID =  :al_jgid)");
			if (!"".equals(carno) && !"null".equals(carno)) {
				sqlBuilder.append(" AND ( MPI_Card.CardNo like '%" + carno
						+ "%')");
			}
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
					sqlBuilder.toString());
			sBuilder.append(") t");

			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameter);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			// res.put("pageSize", pageSize);
			// res.put("pageNo", pageNo);
			// parameter.put("first", pageNo * pageSize);
			// parameter.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
			SchemaUtil.setDictionaryMassageForList(result,
					BSPHISEntryNames.MED_ZY_PATIENT);
			return result;
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块，按门诊业务查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，按门诊业务查询病人列表失败");
		} catch (ParseException e) {
			logger.error("医技项目取消模块，按门诊业务查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，按门诊业务查询病人列表失败");
		}
	}

	/**
	 * 医技项目取消，住院项目查询病人
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryZYPatient(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		String carno = req.get("carno") + "";
		long zxks = Long.parseLong(req.get("zxks") + "");
		try {
			Date adt_Begin = null;
			if (req.get("strdate") != null && req.get("strdate") != "") {
				adt_Begin = sdf.parse(req.get("strdate") + " 00:00:00");
			}
			Date adt_End = null;
			if (req.get("enddate") != null && req.get("enddate") != "") {
				adt_End = sdf.parse(req.get("enddate") + " 23:59:59");
			}
			List<Map<String, Object>> result = null;
			StringBuilder sqlBuilder = new StringBuilder();
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("al_zxks", zxks);
			parameter.put("al_jgid", manaUnitId);
			sqlBuilder
					.append("SELECT YJ_ZY01.YJXH as YJXH, YJ_ZY01.TJHM as TJHM, YJ_ZY01.BRXM as BRXM,YJ_ZY01.ZYHM as ZYHM, YJ_ZY01.KDRQ as KDRQ,");
			sqlBuilder
					.append(" YJ_ZY01.KSDM as KSDM, YJ_ZY01.YSDM as YSDM,  YJ_ZY01.ZXRQ as ZXRQ,  YJ_ZY01.ZXKS as ZXKS, YJ_ZY01.ZXYS as ZXYS, YJ_ZY01.ZXPB as ZXPB, ");

			sqlBuilder
					.append(" YJ_ZY01.HJGH as HJGH, YJ_ZY01.ZYSX as ZYSX,YJ_ZY01.ZFPB as ZFPB,YJ_ZY01.ZYH as ZYH,  YJ_ZY02.YZXH as YZXH, YJ_ZY02.YLXH as YLXH, ");
			sqlBuilder
					.append("  YJ_ZY02.YJZX as YJZX ,ZY_BRRY.BRID as BRID FROM YJ_ZY01, YJ_ZY02, ZY_BRRY  WHERE ( YJ_ZY01.YJXH = YJ_ZY02.YJXH ) AND");
			sqlBuilder.append(" ( YJ_ZY01.ZYH  = ZY_BRRY.ZYH  ) ");

			sqlBuilder
					.append("AND ( ZY_BRRY.CYPB < 8 ) AND ( YJ_ZY01.ZXPB = 1 ) AND ( YJ_ZY02.YJZX = 1 ) AND ( YJ_ZY01.ZXKS = :al_zxks)");
			if (adt_Begin != null) {
				parameter.put("strdate", adt_Begin);
				sqlBuilder.append(" AND (YJ_ZY01.ZXRQ >=:strdate)");
			}
			if (adt_End != null) {
				parameter.put("enddate", adt_End);
				sqlBuilder.append("  AND (YJ_ZY01.ZXRQ <=:enddate)");
			}
			sqlBuilder
					.append(" AND ( YJ_ZY01.JGID = :al_jgid) AND ( YJ_ZY02.JGID = :al_jgid)");
			if (!"".equals(carno) && !"null".equals(carno)) {
				sqlBuilder.append(" AND ( YJ_ZY01.ZYHM like '%" + carno
						+ "%')");
			}

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
					sqlBuilder.toString());
			sBuilder.append(") t");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameter);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameter.put("first", pageNo * pageSize);
			parameter.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
			SchemaUtil.setDictionaryMassageForList(result,
					BSPHISEntryNames.MED_ZY_PATIENT);
			return result;
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块，住院项目查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，住院项目查询病人列表失败");
		} catch (ParseException e) {
			logger.error("医技项目取消模块，按门诊业务查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，按门诊业务查询病人列表失败");
		}
	}

	/**
	 * 医技项目取消，家床项目查询病人
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doQueryJCPatient(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
		int pageSize = Integer.parseInt(req.get("pageSize").toString());
		String carno = req.get("carno") + "";
		long zxks = Long.parseLong(req.get("zxks") + "");
		try {
			Date adt_Begin = null;
			if (req.get("strdate") != null && req.get("strdate") != "") {
				adt_Begin = sdf.parse(req.get("strdate") + " 00:00:00");
			}
			Date adt_End = null;
			if (req.get("enddate") != null && req.get("enddate") != "") {
				adt_End = sdf.parse(req.get("enddate") + " 23:59:59");
			}
			List<Map<String, Object>> result = null;
			StringBuilder sqlBuilder = new StringBuilder();
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("al_zxks", zxks);
			parameter.put("al_jgid", manaUnitId);
			sqlBuilder
					.append("SELECT JC_YJ01.YJXH as YJXH, JC_YJ01.TJHM as TJHM, JC_YJ01.BRXM as BRXM,JC_YJ01.ZYHM as ZYHM, JC_YJ01.KDRQ as KDRQ,");
			sqlBuilder
					.append(" JC_YJ01.KSDM as KSDM, JC_YJ01.YSDM as YSDM,  JC_YJ01.ZXRQ as ZXRQ,  JC_YJ01.ZXKS as ZXKS, JC_YJ01.ZXYS as ZXYS, JC_YJ01.ZXPB as ZXPB, ");

			sqlBuilder
					.append(" JC_YJ01.HJGH as HJGH, JC_YJ01.ZYSX as ZYSX,JC_YJ01.ZFPB as ZFPB,JC_YJ01.ZYH as ZYH,  JC_YJ02.YZXH as YZXH, JC_YJ02.YLXH as YLXH, ");
			sqlBuilder
					.append("  JC_YJ02.YJZX as YJZX ,JC_BRRY.BRID as BRID FROM JC_YJ01, JC_YJ02, JC_BRRY  WHERE ( JC_YJ01.YJXH = JC_YJ02.YJXH ) AND");
			sqlBuilder.append(" ( JC_YJ01.ZYH  = JC_BRRY.ZYH  ) ");

			sqlBuilder
					.append("AND ( JC_BRRY.CYPB < 8 ) AND ( JC_YJ01.ZXPB = 1 ) AND ( JC_YJ02.YJZX = 1 ) AND ( JC_YJ01.ZXKS = :al_zxks)");
			if (adt_Begin != null) {
				parameter.put("strdate", adt_Begin);
				sqlBuilder.append(" AND (JC_YJ01.ZXRQ >=:strdate)");
			}
			if (adt_End != null) {
				parameter.put("enddate", adt_End);
				sqlBuilder.append("  AND (JC_YJ01.ZXRQ <=:enddate)");
			}
			sqlBuilder
					.append(" AND ( JC_YJ01.JGID = :al_jgid) AND ( JC_YJ02.JGID = :al_jgid)");
			if (!"".equals(carno) && !"null".equals(carno)) {
				sqlBuilder.append(" AND ( JC_YJ01.ZYHM like '%" + carno
						+ "%')");
			}

			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT COUNT(*) as NUM FROM (").append(
					sqlBuilder.toString());
			sBuilder.append(") t");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					sBuilder.toString(), parameter);
			int total = Integer.parseInt(coun.get(0).get("NUM").toString());
			res.put("totalCount", total);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			parameter.put("first", pageNo * pageSize);
			parameter.put("max", pageSize);
			result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
			SchemaUtil.setDictionaryMassageForList(result,
					BSPHISEntryNames.MED_JC_PATIENT);
			return result;
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块，家床项目查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，家床项目查询病人列表失败");
		} catch (ParseException e) {
			logger.error("医技项目取消模块，按门诊业务查询病人列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，按家床业务查询病人列表失败");
		}
	}

	/**
	 * 门诊业务医技取消
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void cancelMSBusi(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		List<Map<String, String>> cancelList = (List<Map<String, String>>) req
				.get("body");
		try {
			if (cancelList != null && cancelList.size() > 0) {
				// 提交的时候只会选择一条
				Map<String, String> map = cancelList.get(0);
				Map<String, Object> parameter = new HashMap<String, Object>();
				parameter.put("JGID", manaUnitId);
				parameter.put("YJXH", map.get("YJXH"));
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT ZXPB as ZXPB From MS_YJ01 Where YJXH = :YJXH And JGID = :JGID");
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				if (result.size() > 0
						&& "0".equals(String.valueOf(result.get(0).get("ZXPB")))) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "该检查单已被他人取消,不能再取消执行!");
					return;
				}
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("UPDATE MS_YJ01 Set ZXPB = 0 ,ZXRQ = Null Where YJXH = :YJXH And JGID = :JGID");
				dao.doSqlUpdate(sqlBuilder.toString(), parameter);
				//
				// sqlBuilder = new StringBuilder();
				// sqlBuilder.append("DELETE From YJ_BG02 Where YJXH = :YJXH And MZZY = 1  And JGID = :JGID");
				// dao.doSqlUpdate(sqlBuilder.toString(), parameter);

				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("DELETE From YJ_BG01 Where YJXH = :YJXH And MZZY = 1  And JGID = :JGID");
				dao.doSqlUpdate(sqlBuilder.toString(), parameter);
				res.put("x-response-code", Constants.CODE_OK);
			} else {
				res.put("x-response-code", "201");
				res.put("x-response-msg", "未选择需要取消的执行单!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块，取消门诊业务失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，取消门诊业务失败");
		}
	}

	/**
	 * 住院业务医技取消
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void cancelZYBusi(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String medicalId = String.valueOf(req.get("medicalId"));
		List<Map<String, String>> cancelList = (List<Map<String, String>>) req
				.get("body");
		long yjxh = 0;
		try {
			if (cancelList != null && cancelList.size() > 0) {
				// 提交的时候只会选择一条
				Map<String, String> map = cancelList.get(0);
				Map<String, Object> parameter = new HashMap<String, Object>();
				yjxh = Long.parseLong(String.valueOf(map.get("YJXH")));
				parameter.put("YJXH", yjxh);
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT ZY_BRRY.CYPB as CYPB FROM ZY_BRRY,YJ_ZY01 Where ZY_BRRY.ZYH = YJ_ZY01.ZYH And YJ_ZY01.yjxh = :YJXH ");
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				// 查询结果大于0并且出院判别>=8表示该病人已经出院，不能取消医技执行
				if (result.size() > 0
						&& Integer.parseInt(String.valueOf(result.get(0).get(
								"CYPB"))) == 8) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "该病人已出院，不能取消医技执行!");
					return;
				}
				sqlBuilder = new StringBuilder();
				parameter.put("JGID", manaUnitId);
				sqlBuilder
						.append("SELECT ZXPB as ZXPB From YJ_ZY01 Where YJXH = :YJXH And JGID = :JGID");
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				// 查询结果大于0并且执行判别=0表示该检查单已被他人取消，不能取消医技执行
				if (result.size() > 0
						&& "0".equals(String.valueOf(result.get(0).get("ZXPB")))) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "该检查单已被他人取消,不能再取消执行!");
					return;
				}
				// 取出医技单基本信息
				sqlBuilder = new StringBuilder();
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("YJXH", yjxh);
				param.put("JGID", Long.parseLong(manaUnitId));
				sqlBuilder
						.append("SELECT TJHM as TJHM,ZYHM as ZYHM,BRXM as BRXM,KSDM as KSDM,YSDM as YSDM,ZXYS as ZXYS,HJGH as HJGH,ZYH as ZYH,ZXRQ as ZXRQ,FYBQ as FYBQ From YJ_ZY01 Where YJXH = :YJXH And JGID = :JGID");
				List<Map<String, Object>> mtBaseInfo = dao.doQuery(
						sqlBuilder.toString(), param);
				if (mtBaseInfo == null || mtBaseInfo.size() == 0) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "未找到医技单基本信息!");
					return;
				}
				String ldt_fyrq = String.valueOf(mtBaseInfo.get(0).get("ZXRQ"));
				// 如果费用日期为空，则给当前时间
				if ("".equals(ldt_fyrq) || "null".equals(ldt_fyrq)) {
					ldt_fyrq = BSHISUtil.getDateTime();
				}
				// 病人性质
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT ZYHM as ZYHM,BRXM  as BRXM,BRID as BRID,BRXZ as BRXZ,CYPB as CYPB From ZY_BRRY Where ZYH = :ZYH And JGID = :JGID ");
				parameter = new HashMap<String, Object>();
				parameter.put("JGID", manaUnitId);
				parameter.put(
						"ZYH",
						Long.parseLong(String.valueOf(mtBaseInfo.get(0).get(
								"ZYH"))));
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				if (result == null || result.size() == 0) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "未找到病人性质信息!");
					return;
				}
				String ll_brxz = String.valueOf(result.get(0).get("BRXZ"));
				String ll_zyhm = String.valueOf(result.get(0).get("ZYHM"));
				String ll_brxm = String.valueOf(result.get(0).get("BRXM"));
				String ll_brid = String.valueOf(result.get(0).get("BRID"));
				// 住院项目取消
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT YJ_ZY02.SBXH as SBXH,  YJ_ZY02.YLXH as YLXH,  YJ_ZY02.YLDJ as YLDJ,  YJ_ZY02.YLSL as YLSL, ");
				sqlBuilder
						.append(" YJ_ZY02.FYGB as FYGB, YJ_ZY02.ZFBL as ZFBL, YJ_ZY02.YZXH as YZXH, YJ_ZY02.YEPB as YEPB ");
				sqlBuilder.append(" FROM YJ_ZY02 WHERE YJ_ZY02.YJXH = :YJXH");
				parameter = new HashMap<String, Object>();
				parameter.put("YJXH",
						Long.parseLong(String.valueOf(map.get("YJXH"))));
				List<Map<String, Object>> zyProjectCancelList = dao.doSqlQuery(
						sqlBuilder.toString(), parameter);
				if (zyProjectCancelList != null) {
					String ll_ylxh = "";
					Map<String, Object> zyProjectMap = null;
					List<Map<String, Object>> list_FYMX = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < zyProjectCancelList.size(); i++) {
						// 往zy_fymx表插入负记录，表示退费 调用住院费用明细处理公共类
						zyProjectMap = zyProjectCancelList.get(i);
						ll_ylxh = String.valueOf(zyProjectMap.get("YLXH"));
						sqlBuilder = new StringBuilder();
						sqlBuilder
								.append("	SELECT FYMC as FYMC ,FYDW as FYDW From GY_YLSF Where FYXH = :ll_ylxh");
						parameter = new HashMap<String, Object>();
						parameter.put("ll_ylxh", Long.parseLong(ll_ylxh));
						result = dao.doSqlQuery(sqlBuilder.toString(),
								parameter);
						// 住院费用明细处理.uf_insertfymx
						// String ll_zxks =
						// String.valueOf(fymxMap.get("ZXKS"));//获取执行科室
						Map<String, Object> fymxMap = new HashMap<String, Object>();
						fymxMap.put("ZYH", mtBaseInfo.get(0).get("ZYH"));// 住院号
						fymxMap.put("YPLX", 0);// 药品类型
						fymxMap.put("FYXH", ll_ylxh);// 费用序号，既药品序号
						fymxMap.put("BRXZ", ll_brxz);// 病人性质
						fymxMap.put("ZXKS", medicalId);// 执行科室*****
						fymxMap.put("FYKS", mtBaseInfo.get(0).get("KSDM"));// 费用科室
						fymxMap.put("FYXM", zyProjectMap.get("FYGB"));// 费用项目,获取费用归并
						fymxMap.put("FYSL", -Double.parseDouble(String
								.valueOf(zyProjectMap.get("YLSL"))));// 医疗数量取负数
						fymxMap.put("FYDJ", zyProjectMap.get("YLDJ"));// 费用单价
						fymxMap.put("ZFBL", zyProjectMap.get("ZFBL"));// 自负比例
						// 自理金额与自付金额需要取负数
						fymxMap.put(
								"ZJJE",
								Double.parseDouble(String.valueOf(zyProjectMap
										.get("YLDJ")))
										* -Double.parseDouble(String
												.valueOf(zyProjectMap
														.get("YLSL"))));// 总计金额
						double zfje = Double.parseDouble(String
								.valueOf(zyProjectMap.get("YLDJ")))
								* -Double.parseDouble(String
										.valueOf(zyProjectMap.get("YLSL")))
								* Double.parseDouble(String
										.valueOf(zyProjectMap.get("ZFBL")));
						fymxMap.put("ZFJE", zfje);// 自负金额
						fymxMap.put("ZLJE", zfje);// 自理金额
						fymxMap.put("YSGH", mtBaseInfo.get(0).get("YSDM"));// 医生工号
						fymxMap.put("FYRQ", ldt_fyrq);// 费用日期
						fymxMap.put("JFRQ", BSHISUtil.getDateTime());// 计费日期
						fymxMap.put("YPCD", 0);// 药品产地
						fymxMap.put("YZXH", zyProjectMap.get("YZXH"));// 医嘱序号
						fymxMap.put("JLXH", zyProjectMap.get("YZXH"));// 记录序号，在BSPHISUtil中使用医嘱序号
						fymxMap.put("FYMC", result.get(0).get("FYMC"));// 费用名称
						fymxMap.put("SRGH", mtBaseInfo.get(0).get("HJGH"));// 输入工号
						fymxMap.put("XMLX", 3);// 项目类型
						fymxMap.put("FYBQ",
								mtBaseInfo.get(0).get("FYBQ") == null ? 0
										: mtBaseInfo.get(0).get("FYBQ"));// 费用病区
						list_FYMX.add(fymxMap);
					}
					List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
					// 调用费用明细
					BSPHISUtil.uf_insert_fymx(list_FYMX, listForputFYMX, dao,
							ctx);
					sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("UPDATE YJ_ZY01 Set ZXPB = 0 ,ZXRQ = Null Where YJXH = :YJXH   And JGID = :JGID");
					parameter = new HashMap<String, Object>();
					parameter.put("YJXH",
							Long.parseLong(String.valueOf(map.get("YJXH"))));
					parameter.put("JGID", manaUnitId);
					dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					// Map<String,Object> parameters = new HashMap<String,
					// Object>();
					// parameters.put("YZXH",
					// Long.parseLong(String.valueOf(map.get("YZXH"))));
					// parameters.put("JGID", manaUnitId);
					// Map<String,Object> JLXH
					// =dao.doLoad("select max(JLXH) as JLXH from ZY_FYMX where YZXH=:YZXH and JGID=:JGID",
					// parameters);
					// Map<String,Object> ZY_FYMX =
					// dao.doLoad(BSPHISEntryNames.ZY_FYMX, JLXH.get("JLXH"));
					// ZY_FYMX.put("FYSL",
					// -Double.parseDouble(ZY_FYMX.get("FYSL")+""));
					// ZY_FYMX.put("ZJJE",
					// -Double.parseDouble(ZY_FYMX.get("ZJJE")+""));
					// ZY_FYMX.put("ZFJE",
					// -Double.parseDouble(ZY_FYMX.get("ZFJE")+""));
					// ZY_FYMX.remove("JLXH");
					// dao.doSave("create", BSPHISEntryNames.ZY_FYMX, ZY_FYMX,
					// false);
					/*
					 * 经与张伟确认不需要该执行语句 //取出医技主项 sqlBuilder = new StringBuilder();
					 * sqlBuilder.append(
					 * "SELECT GY_YLSF.FYMC as FYMC,GY_YLSF.FYXH as FYXH FROM YJ_ZY01,YJ_ZY02,GY_YLSF WHERE YJ_ZY01.YJXH = YJ_ZY02.YJXH "
					 * ); sqlBuilder.append(
					 * " And YJ_ZY02.YJZX = 1 And YJ_ZY02.ylxh = GY_YLSF.FYXH And YJ_ZY01.YJXH = :YJXH  And YJ_ZY01.JGID = :JGID  And YJ_ZY02.JGID = :JGID "
					 * ); parameter = new HashMap<String, Object>();
					 * parameter.put("YJXH",
					 * Long.parseLong(String.valueOf(map.get("YJXH"))));
					 * parameter.put("JGID", manaUnitId); result =
					 * dao.doSqlQuery(sqlBuilder.toString(), parameter);
					 */
					String ll_yzxh = "";
					String ll_lsbz = "";
					String ll_fycount = "";
					String ldt_fymx_jfrq = "";
					for (int i = 0; i < zyProjectCancelList.size(); i++) {
						// 病区输入的检查单取消执行医嘱表的确认时间、使用标志、历史医嘱标志置回
						zyProjectMap = zyProjectCancelList.get(i);
						ll_yzxh = String.valueOf(zyProjectMap.get("YZXH"));
						if (!"".equals(ll_yzxh) && !"null".equals(ll_yzxh)
								&& !"0".equals(ll_yzxh)) {
							sqlBuilder = new StringBuilder();
							sqlBuilder
									.append("SELECT  LSBZ as LSBZ From ZY_BQYZ Where JLXH = :ll_yzxh ");
							parameter = new HashMap<String, Object>();
							parameter.put("ll_yzxh", Long.parseLong(ll_yzxh));
							result = dao.doSqlQuery(sqlBuilder.toString(),
									parameter);
							if (result == null || result.size() == 0) {
								res.put("x-response-code", "201");
								res.put("x-response-msg", "病区医嘱中未找，该病人的记录!");
								return;
							}
							// 获取历史标志
							ll_lsbz = String.valueOf(result.get(0).get("LSBZ"));
							sqlBuilder = new StringBuilder();
							sqlBuilder
									.append("SELECT COUNT(*) as LL_FYCOUNT FROM ZY_FYMX WHERE YZXH = :ll_yzxh");
							result = dao.doSqlQuery(sqlBuilder.toString(),
									parameter);
							if ("1".equals(ll_lsbz)) {// 临时医嘱的确认时间置空，使用标志置1，历史标志置0
								sqlBuilder = new StringBuilder();
								sqlBuilder
										.append("UPDATE ZY_BQYZ Set QRSJ = NULL,SYBZ = 1,LSBZ = 0 Where JLXH = :ll_yzxh");
								parameter = new HashMap<String, Object>();
								parameter.put("ll_yzxh",
										Long.parseLong(ll_yzxh));
								dao.doSqlUpdate(sqlBuilder.toString(),
										parameter);
							} else {// 为长期医嘱的确认时间第一次置为空，后面取消时间置为前一天日期，使用标志置1
								ll_fycount = String.valueOf(result.get(0).get(
										"LL_FYCOUNT"));
								if ("1".equals(ll_fycount)) {
									sqlBuilder = new StringBuilder();
									sqlBuilder
											.append("UPDATE ZY_BQYZ Set QRSJ = NULL,SYBZ = 1 Where JLXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									dao.doSqlUpdate(sqlBuilder.toString(),
											parameter);
								} else {
									sqlBuilder = new StringBuilder();
									sqlBuilder.append("SELECT ").append(
											BSPHISUtil.toChar("MAX(JFRQ)",
													"yyyy-MM-dd HH24:mi:ss"));
									sqlBuilder
											.append(" as LDT_FYMX_JFRQ FROM ZY_FYMX WHERE YZXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									result = dao.doSqlQuery(
											sqlBuilder.toString(), parameter);
									ldt_fymx_jfrq = String.valueOf(result
											.get(0).get("LDT_FYMX_JFRQ"));

									sqlBuilder = new StringBuilder();
									sqlBuilder.append("SELECT ").append(
											BSPHISUtil.toChar("MAX(JFRQ)",
													"yyyy-MM-dd HH24:mi:ss"));
									sqlBuilder
											.append(" as LDT_FYMX_JFRQ FROM ZY_FYMX WHERE YZXH = :ll_yzxh AND ");
									sqlBuilder.append(
											BSPHISUtil.toChar("JFRQ",
													"yyyy-MM-dd HH24:mi:ss"))
											.append(" < :ldt_fymx_jfrq");
									// sqlBuilder.append(BSPHISUtil.toDate(ldt_fymx_jfrq,
									// "YYYY-MM-DD HH24:mi:ss"));
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									// parameter.put("ldt_fymx_jfrq",
									// BSPHISUtil.toDate(ldt_fymx_jfrq,
									// "YYYY-MM-DD HH24:mi:ss"));
									parameter.put("ldt_fymx_jfrq",
											ldt_fymx_jfrq);
									result = dao.doSqlQuery(
											sqlBuilder.toString(), parameter);
									ldt_fymx_jfrq = String.valueOf(result
											.get(0).get("LDT_FYMX_JFRQ"));

									sqlBuilder = new StringBuilder();
									// 修改医嘱中的确认时间和使用标识，修改条件为记录序号
									sqlBuilder
											.append("UPDATE ZY_BQYZ Set QRSJ = :ldt_fymx_jfrq,SYBZ = 1  Where JLXH = :ll_yzxh");
									// sqlBuilder.append("UPDATE ZY_BQYZ Set QRSJ = ");
									// sqlBuilder.append(ldt_fymx_jfrq).append(" ,SYBZ = 1  Where JLXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									parameter.put("ldt_fymx_jfrq", new Date());
									dao.doSqlUpdate(sqlBuilder.toString(),
											parameter);
								}
							}
						}
					}
					parameter = new HashMap<String, Object>();
					parameter.put("al_yjxh", yjxh);
					parameter.put("gl_jgid", manaUnitId);

					// 删除医技报告
					// sqlBuilder = new StringBuilder();
					// sqlBuilder.append("DELETE From YJ_BG02 Where YJXH = :al_yjxh And MZZY = 2  And JGID = :gl_jgid");
					//
					// dao.doSqlUpdate(sqlBuilder.toString(), parameter);

					sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("DELETE From YJ_BG01 Where YJXH = :al_yjxh And MZZY = 2  And JGID = :gl_jgid");
					dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					// 调用住院费用明细处理.uf_update
					BSPHISUtil.uf_update_fymx(listForputFYMX, "create",
							BSPHISEntryNames.ZY_FYMX, false, dao);
					// 物资消费数据生成
					ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
							dao);
					Map<String, Object> map_brxx = new HashMap<String, Object>();
					map_brxx.put("ZYHM", ll_zyhm);
					map_brxx.put("BRXM", ll_brxm);
					map_brxx.put("BRID", ll_brid);
					mmd.saveAllXhmx(listForputFYMX, 0, ctx,map_brxx);
				}
				res.put("x-response-code", Constants.CODE_OK);
			} else {
				res.put("x-response-code", "201");
				res.put("x-response-msg", "未选择需要取消的执行单!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块PersistentDataOperationException，住院业务医技取消失败",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，住院业务医技取消失败");
		} catch (Exception e) {
			logger.error("医技项目取消模块Exception，住院业务医技取消失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，住院业务医技取消失败");
		}
	}

	/**
	 * 住院业务医技取消
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doDeleteJCBusi(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> result = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String medicalId = String.valueOf(req.get("medicalId"));
		@SuppressWarnings("unchecked")
		List<Map<String, String>> cancelList = (List<Map<String, String>>) req
				.get("body");
		long yjxh = 0;
		try {
			if (cancelList != null && cancelList.size() > 0) {
				// 提交的时候只会选择一条
				Map<String, String> map = cancelList.get(0);
				Map<String, Object> parameter = new HashMap<String, Object>();
				yjxh = Long.parseLong(String.valueOf(map.get("YJXH")));
				parameter.put("YJXH", yjxh);
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT JC_BRRY.CYPB as CYPB FROM JC_BRRY,JC_YJ01 Where JC_BRRY.ZYH = JC_YJ01.ZYH And JC_YJ01.yjxh = :YJXH ");
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				// 查询结果大于0并且出院判别>=8表示该病人已经出院，不能取消医技执行
				if (result.size() > 0
						&& Integer.parseInt(String.valueOf(result.get(0).get(
								"CYPB"))) == 8) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "该病人已出院，不能取消医技执行!");
					return;
				}
				sqlBuilder = new StringBuilder();
				parameter.put("JGID", manaUnitId);
				sqlBuilder
						.append("SELECT ZXPB as ZXPB From JC_YJ01 Where YJXH = :YJXH And JGID = :JGID");
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				// 查询结果大于0并且执行判别=0表示该检查单已被他人取消，不能取消医技执行
				if (result.size() > 0
						&& "0".equals(String.valueOf(result.get(0).get("ZXPB")))) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "该检查单已被他人取消,不能再取消执行!");
					return;
				}
				// 取出医技单基本信息
				sqlBuilder = new StringBuilder();
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("YJXH", yjxh);
				param.put("JGID", manaUnitId);
				sqlBuilder
						.append("SELECT TJHM as TJHM,ZYHM as ZYHM,BRXM as BRXM,KSDM as KSDM,YSDM as YSDM,ZXYS as ZXYS,HJGH as HJGH,ZYH as ZYH,ZXRQ as ZXRQ,FYBQ as FYBQ From JC_YJ01 Where YJXH = :YJXH And JGID = :JGID");
				List<Map<String, Object>> mtBaseInfo = dao.doQuery(
						sqlBuilder.toString(), param);
				if (mtBaseInfo == null || mtBaseInfo.size() == 0) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "未找到医技单基本信息!");
					return;
				}
				String ldt_fyrq = String.valueOf(mtBaseInfo.get(0).get("ZXRQ"));
				// 如果费用日期为空，则给当前时间
				if ("".equals(ldt_fyrq) || "null".equals(ldt_fyrq)) {
					ldt_fyrq = BSHISUtil.getDateTime();
				}
				// 病人性质
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT BRXZ as BRXZ,CYPB as CYPB From JC_BRRY Where ZYH = :ZYH And JGID = :JGID ");
				parameter = new HashMap<String, Object>();
				parameter.put("JGID", manaUnitId);
				parameter.put(
						"ZYH",
						Long.parseLong(String.valueOf(mtBaseInfo.get(0).get(
								"ZYH"))));
				result = dao.doSqlQuery(sqlBuilder.toString(), parameter);
				if (result == null || result.size() == 0) {
					res.put("x-response-code", "201");
					res.put("x-response-msg", "未找到病人性质信息!");
					return;
				}
				String ll_brxz = String.valueOf(result.get(0).get("BRXZ"));
				// 住院项目取消
				sqlBuilder = new StringBuilder();
				sqlBuilder
						.append("SELECT JC_YJ02.SBXH as SBXH,JC_YJ02.YLXH as YLXH,JC_YJ02.YLDJ as YLDJ,JC_YJ02.YLSL as YLSL,");
				sqlBuilder
						.append(" JC_YJ02.FYGB as FYGB,JC_YJ02.ZFBL as ZFBL,JC_YJ02.YZXH as YZXH,JC_YJ02.YEPB as YEPB ");
				sqlBuilder.append(" FROM JC_YJ02 WHERE JC_YJ02.YJXH = :YJXH");
				parameter = new HashMap<String, Object>();
				parameter.put("YJXH",
						Long.parseLong(String.valueOf(map.get("YJXH"))));
				List<Map<String, Object>> zyProjectCancelList = dao.doSqlQuery(
						sqlBuilder.toString(), parameter);
				if (zyProjectCancelList != null) {
					String ll_ylxh = "";
					Map<String, Object> zyProjectMap = null;
					List<Map<String, Object>> list_FYMX = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < zyProjectCancelList.size(); i++) {
						// 往zy_fymx表插入负记录，表示退费 调用住院费用明细处理公共类
						zyProjectMap = zyProjectCancelList.get(i);
						ll_ylxh = String.valueOf(zyProjectMap.get("YLXH"));
						sqlBuilder = new StringBuilder();
						sqlBuilder
								.append("	SELECT FYMC as FYMC ,FYDW as FYDW From GY_YLSF Where FYXH = :ll_ylxh");
						parameter = new HashMap<String, Object>();
						parameter.put("ll_ylxh", Long.parseLong(ll_ylxh));
						result = dao.doSqlQuery(sqlBuilder.toString(),
								parameter);
						// 住院费用明细处理.uf_insertfymx
						// String ll_zxks =
						// String.valueOf(fymxMap.get("ZXKS"));//获取执行科室
						Map<String, Object> fymxMap = new HashMap<String, Object>();
						fymxMap.put("ZYH", mtBaseInfo.get(0).get("ZYH"));// 住院号
						fymxMap.put("YPLX", 0);// 药品类型
						fymxMap.put("FYXH", ll_ylxh);// 费用序号，既药品序号
						fymxMap.put("BRXZ", ll_brxz);// 病人性质
						fymxMap.put("ZXKS", medicalId);// 执行科室*****
						fymxMap.put("FYKS", mtBaseInfo.get(0).get("KSDM"));// 费用科室
						fymxMap.put("FYXM", zyProjectMap.get("FYGB"));// 费用项目,获取费用归并
						fymxMap.put("FYSL", -Double.parseDouble(String
								.valueOf(zyProjectMap.get("YLSL"))));// 医疗数量取负数
						fymxMap.put("FYDJ", zyProjectMap.get("YLDJ"));// 费用单价
						fymxMap.put("ZFBL", zyProjectMap.get("ZFBL"));// 自负比例
						// 自理金额与自付金额需要取负数
						fymxMap.put(
								"ZJJE",
								Double.parseDouble(String.valueOf(zyProjectMap
										.get("YLDJ")))
										* -Double.parseDouble(String
												.valueOf(zyProjectMap
														.get("YLSL"))));// 总计金额
						double zfje = Double.parseDouble(String
								.valueOf(zyProjectMap.get("YLDJ")))
								* -Double.parseDouble(String
										.valueOf(zyProjectMap.get("YLSL")))
								* Double.parseDouble(String
										.valueOf(zyProjectMap.get("ZFBL")));
						fymxMap.put("ZFJE", zfje);// 自负金额
						fymxMap.put("ZLJE", zfje);// 自理金额
						fymxMap.put("YSGH", mtBaseInfo.get(0).get("YSDM"));// 医生工号
						fymxMap.put("FYRQ", ldt_fyrq);// 费用日期
						fymxMap.put("JFRQ", BSHISUtil.getDateTime());// 计费日期
						fymxMap.put("YPCD", 0);// 药品产地
						fymxMap.put("YZXH", zyProjectMap.get("YZXH"));// 医嘱序号
						fymxMap.put("JLXH", zyProjectMap.get("YZXH"));// 记录序号，在BSPHISUtil中使用医嘱序号
						fymxMap.put("FYMC", result.get(0).get("FYMC"));// 费用名称
						fymxMap.put("SRGH", mtBaseInfo.get(0).get("HJGH"));// 输入工号
						fymxMap.put("XMLX", 3);// 项目类型
						fymxMap.put("FYBQ",
								mtBaseInfo.get(0).get("FYBQ") == null ? 0
										: mtBaseInfo.get(0).get("FYBQ"));// 费用病区
						list_FYMX.add(fymxMap);
					}
					List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
					// 调用费用明细
					BSPHISUtil.uf_insert_jc_fymx(list_FYMX, listForputFYMX,
							dao, ctx);
					sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("UPDATE JC_YJ01 Set ZXPB = 0 ,ZXRQ = Null Where YJXH = :YJXH   And JGID = :JGID");
					parameter = new HashMap<String, Object>();
					parameter.put("YJXH",
							Long.parseLong(String.valueOf(map.get("YJXH"))));
					parameter.put("JGID", manaUnitId);
					dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					// Map<String,Object> parameters = new HashMap<String,
					// Object>();
					// parameters.put("YZXH",
					// Long.parseLong(String.valueOf(map.get("YZXH"))));
					// parameters.put("JGID", manaUnitId);
					// Map<String,Object> JLXH
					// =dao.doLoad("select max(JLXH) as JLXH from ZY_FYMX where YZXH=:YZXH and JGID=:JGID",
					// parameters);
					// Map<String,Object> ZY_FYMX =
					// dao.doLoad(BSPHISEntryNames.ZY_FYMX, JLXH.get("JLXH"));
					// ZY_FYMX.put("FYSL",
					// -Double.parseDouble(ZY_FYMX.get("FYSL")+""));
					// ZY_FYMX.put("ZJJE",
					// -Double.parseDouble(ZY_FYMX.get("ZJJE")+""));
					// ZY_FYMX.put("ZFJE",
					// -Double.parseDouble(ZY_FYMX.get("ZFJE")+""));
					// ZY_FYMX.remove("JLXH");
					// dao.doSave("create", BSPHISEntryNames.ZY_FYMX, ZY_FYMX,
					// false);
					/*
					 * 经与张伟确认不需要该执行语句 //取出医技主项 sqlBuilder = new StringBuilder();
					 * sqlBuilder.append(
					 * "SELECT GY_YLSF.FYMC as FYMC,GY_YLSF.FYXH as FYXH FROM YJ_ZY01,YJ_ZY02,GY_YLSF WHERE YJ_ZY01.YJXH = YJ_ZY02.YJXH "
					 * ); sqlBuilder.append(
					 * " And YJ_ZY02.YJZX = 1 And YJ_ZY02.ylxh = GY_YLSF.FYXH And YJ_ZY01.YJXH = :YJXH  And YJ_ZY01.JGID = :JGID  And YJ_ZY02.JGID = :JGID "
					 * ); parameter = new HashMap<String, Object>();
					 * parameter.put("YJXH",
					 * Long.parseLong(String.valueOf(map.get("YJXH"))));
					 * parameter.put("JGID", manaUnitId); result =
					 * dao.doSqlQuery(sqlBuilder.toString(), parameter);
					 */
					String ll_yzxh = "";
					String ll_lsbz = "";
					String ll_fycount = "";
					String ldt_fymx_jfrq = "";
					for (int i = 0; i < zyProjectCancelList.size(); i++) {
						// 病区输入的检查单取消执行医嘱表的确认时间、使用标志、历史医嘱标志置回
						zyProjectMap = zyProjectCancelList.get(i);
						ll_yzxh = String.valueOf(zyProjectMap.get("YZXH"));
						if (!"".equals(ll_yzxh) && !"null".equals(ll_yzxh)
								&& !"0".equals(ll_yzxh)) {
							sqlBuilder = new StringBuilder();
							sqlBuilder
									.append("SELECT  LSBZ as LSBZ From JC_BRYZ Where JLXH = :ll_yzxh ");
							parameter = new HashMap<String, Object>();
							parameter.put("ll_yzxh", Long.parseLong(ll_yzxh));
							result = dao.doSqlQuery(sqlBuilder.toString(),
									parameter);
							if (result == null || result.size() == 0) {
								res.put("x-response-code", "201");
								res.put("x-response-msg", "病区医嘱中未找，该病人的记录!");
								return;
							}
							// 获取历史标志
							ll_lsbz = String.valueOf(result.get(0).get("LSBZ"));
							sqlBuilder = new StringBuilder();
							sqlBuilder
									.append("SELECT COUNT(*) as LL_FYCOUNT FROM JC_FYMX WHERE YZXH = :ll_yzxh");
							result = dao.doSqlQuery(sqlBuilder.toString(),
									parameter);
							if ("1".equals(ll_lsbz)) {// 临时医嘱的确认时间置空，使用标志置1，历史标志置0
								sqlBuilder = new StringBuilder();
								sqlBuilder
										.append("UPDATE JC_BRYZ Set QRSJ = NULL,SYBZ = 1,LSBZ = 0 Where JLXH = :ll_yzxh");
								parameter = new HashMap<String, Object>();
								parameter.put("ll_yzxh",
										Long.parseLong(ll_yzxh));
								dao.doSqlUpdate(sqlBuilder.toString(),
										parameter);
							} else {// 为长期医嘱的确认时间第一次置为空，后面取消时间置为前一天日期，使用标志置1
								ll_fycount = String.valueOf(result.get(0).get(
										"LL_FYCOUNT"));
								if ("1".equals(ll_fycount)) {
									sqlBuilder = new StringBuilder();
									sqlBuilder
											.append("UPDATE JC_BRYZ Set QRSJ = NULL,SYBZ = 1 Where JLXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									dao.doSqlUpdate(sqlBuilder.toString(),
											parameter);
								} else {
									sqlBuilder = new StringBuilder();
									sqlBuilder.append("SELECT ").append(
											BSPHISUtil.toChar("MAX(JFRQ)",
													"yyyy-MM-dd HH24:mi:ss"));
									sqlBuilder
											.append(" as LDT_FYMX_JFRQ FROM JC_FYMX WHERE YZXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									result = dao.doSqlQuery(
											sqlBuilder.toString(), parameter);
									ldt_fymx_jfrq = String.valueOf(result
											.get(0).get("LDT_FYMX_JFRQ"));

									sqlBuilder = new StringBuilder();
									sqlBuilder.append("SELECT ").append(
											BSPHISUtil.toChar("MAX(JFRQ)",
													"yyyy-MM-dd HH24:mi:ss"));
									sqlBuilder
											.append(" as LDT_FYMX_JFRQ FROM JC_FYMX WHERE YZXH = :ll_yzxh AND ");
									sqlBuilder.append(
											BSPHISUtil.toChar("JFRQ",
													"yyyy-MM-dd HH24:mi:ss"))
											.append(" < :ldt_fymx_jfrq");
									// sqlBuilder.append(BSPHISUtil.toDate(ldt_fymx_jfrq,
									// "YYYY-MM-DD HH24:mi:ss"));
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									// parameter.put("ldt_fymx_jfrq",
									// BSPHISUtil.toDate(ldt_fymx_jfrq,
									// "YYYY-MM-DD HH24:mi:ss"));
									parameter.put("ldt_fymx_jfrq",
											ldt_fymx_jfrq);
									result = dao.doSqlQuery(
											sqlBuilder.toString(), parameter);
									ldt_fymx_jfrq = String.valueOf(result
											.get(0).get("LDT_FYMX_JFRQ"));

									sqlBuilder = new StringBuilder();
									// 修改医嘱中的确认时间和使用标识，修改条件为记录序号
									sqlBuilder
											.append("UPDATE JC_BRYZ Set QRSJ = :ldt_fymx_jfrq,SYBZ = 1  Where JLXH = :ll_yzxh");
									// sqlBuilder.append("UPDATE ZY_BQYZ Set QRSJ = ");
									// sqlBuilder.append(ldt_fymx_jfrq).append(" ,SYBZ = 1  Where JLXH = :ll_yzxh");
									parameter = new HashMap<String, Object>();
									parameter.put("ll_yzxh",
											Long.parseLong(ll_yzxh));
									parameter.put("ldt_fymx_jfrq", new Date());
									dao.doSqlUpdate(sqlBuilder.toString(),
											parameter);
								}
							}
						}
					}
					parameter = new HashMap<String, Object>();
					parameter.put("al_yjxh", yjxh);
					parameter.put("gl_jgid", manaUnitId);

					// 删除医技报告
					// sqlBuilder = new StringBuilder();
					// sqlBuilder.append("DELETE From YJ_BG02 Where YJXH = :al_yjxh And MZZY = 2  And JGID = :gl_jgid");
					//
					// dao.doSqlUpdate(sqlBuilder.toString(), parameter);

					sqlBuilder = new StringBuilder();
					sqlBuilder
							.append("DELETE From JC_BG01 Where YJXH = :al_yjxh And MZZY = 2  And JGID = :gl_jgid");
					dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					// 调用住院费用明细处理.uf_update
					BSPHISUtil.uf_update_jc_fymx(listForputFYMX, "create",
							BSPHISEntryNames.JC_FYMX, false, dao);
					// 物资消费数据生成
					ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
							dao);
					mmd.save_jcAllXhmx(listForputFYMX, 0, ctx);
				}
				res.put("x-response-code", Constants.CODE_OK);
			} else {
				res.put("x-response-code", "201");
				res.put("x-response-msg", "未选择需要取消的执行单!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("医技项目取消模块PersistentDataOperationException，家床业务医技取消失败",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，家床业务医技取消失败");
		} catch (Exception e) {
			logger.error("医技项目取消模块Exception，家床业务医技取消失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技项目取消模块，家床业务医技取消失败");
		}
	}
}
