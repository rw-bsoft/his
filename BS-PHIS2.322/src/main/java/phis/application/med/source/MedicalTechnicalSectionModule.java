package phis.application.med.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.util.Hash;

import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
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
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @author liyunt 医技业务
 */
public class MedicalTechnicalSectionModule {
	protected Logger logger = LoggerFactory
			.getLogger(MedicalTechnicalSectionModule.class);
	private BaseDAO dao;

	public MedicalTechnicalSectionModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 门诊医技单查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getMzList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		StringBuffer hql_mz = new StringBuffer(
				"SELECT e.CardNO as JZKH,d.BRID as BRID, a.YJXH as YJXH,a.TJHM as TJHM,d.BRXM as BRXM,a.ZXRQ as ZXRQ,a.KDRQ as KDRQ,a.HJGH as HJGH,c.FYMC as FYMC,b.YLXH as YLXH,a.YSDM as YSDM, a.BRID as BRID,a.ZXKS as ZXKS,b.YJXH as YJXH,a.FPHM as FPHM, a.KSDM as KSDM,a.ZXYS as ZXYS,a.ZFPB as ZFPB,a.ZXPB as ZXPB,d.CSNY as CSNY,d.BRXB as BRXB,0.00 as ZJJE,'' as ZDMC,'' as AGE,a.DJLY as DJLY,d.MZHM as MZHM FROM ")
				.append("MS_YJ01")
				.append(" a,MS_YJ02")
				.append(" b,GY_YLSF")
				.append(" c,MS_BRDA")
				.append(" d left join ")
				.append("MPI_Card")
				.append(" e on e.cardTypeCode=" + KLX
						+ " and e.empiId = d.EMPIID WHERE ( b.YJXH = a.YJXH ) "
						+ "AND d.BRID = a.BRID " + "AND( b.YLXH = c.FYXH ) "
						+ "AND  ( a.ZXPB = 0 OR a.ZXPB IS NULL ) "
						+ "AND ( a.ZFPB = 0 OR a.ZFPB = 9 OR a.ZFPB IS NULL ) "
						+ "AND ( b.YJZX = 1 ) " + "AND ( a.ZXKS = :al_zxks ) "
						+ "AND ( a.JGID = :al_jgid) "
						+ "AND ( b.JGID = :al_jgid)");
		String searchParam = req.get("search") != null ? (String) req
				.get("search") : null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_zxks",
				Long.parseLong(valueOfString(req.get("zxks"))));
		parameters.put("al_jgid", req.get("jgid"));
		if (!IsNull(searchParam)) {
			hql_mz.append(" AND e.CardNo = :JZKH");
			parameters.put("JZKH", searchParam);
		}
		if ("1".equals(ParameterUtil.getParameter(req.get("jgid") + "",
				BSPHISSystemArgument.ZXSSFDJ, ctx))) {
			hql_mz.append(" AND a.FPHM is not null");
		}
		hql_mz.append("   order by a.KDRQ desc");
		// parameters.put("al_yjxh",Long.parseLong(req.get("yjxh").toString()));
		try {
			List<Map<String, Object>> list_YJ_MZYW = dao.doSqlQuery(
					hql_mz.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYW,
					BSPHISEntryNames.YJ_MZYW);
			return list_YJ_MZYW;
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊医技列表失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊医技列表失败!");
		}
	}

	/**
	 * 住院医技单查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getZYListByParam(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String hql = "SELECT YJ_ZY01.YJXH as YJXH,"
				+ "YJ_ZY01.TJHM as TJHM,   " + "YJ_ZY01.BRXM as BRXM,   "
				+ "YJ_ZY01.ZYHM as ZYHM,   " + "YJ_ZY01.KDRQ as KDRQ,  "
				+ "YJ_ZY01.KSDM as KSDM,  " + "YJ_ZY01.YSDM as YSDM,  "
				+ "YJ_ZY01.ZXRQ as ZXRQ,  " + "YJ_ZY01.ZXKS as ZXKS,  "
				+ "YJ_ZY01.ZXYS as ZXYS,  " + "YJ_ZY01.ZXPB as ZXPB,  "
				+ "YJ_ZY01.HJGH as HJGH,  " + "YJ_ZY01.ZYSX as ZYSX,  "
				+ "YJ_ZY01.ZFPB as ZFPB,  " + "YJ_ZY01.ZYH as ZYH,  "
				+ "YJ_ZY02.YZXH as YZXH,  " + "YJ_ZY02.YLXH as YLXH,  "
				+ "YJ_ZY02.YJZX as YJZX, " + "ZY_BRRY.BRXB as BRXB,"
				+ "ZY_BRRY.CSNY as CSNY," + "ZY_BRRY.BRID as BRID,"
				+ "0.00 AS ZJJE," + "'' as	ZDMC," + "''  AS AGE "
				+ "FROM YJ_ZY01 as YJ_ZY01,  " + "     YJ_ZY02 as YJ_ZY02,"
				+ "		ZY_BRRY as ZY_BRRY"
				+ " WHERE ( YJ_ZY01.YJXH = YJ_ZY02.YJXH ) AND"
				+ "		( ZY_BRRY.ZYH = YJ_ZY01.ZYH ) AND"
				+ "     ( YJ_ZY01.ZXPB = 0 OR  " + "     YJ_ZY01.ZXPB = 9 OR  "
				+ "     YJ_ZY01.ZXPB IS NULL ) AND  "
				+ "     ( YJ_ZY02.YJZX = 1 ) AND  "
				+ "   ( YJ_ZY01.ZXKS = :al_zxks ) AND  "
				+ "    (YJ_ZY01.JGID = :al_jgid) AND  "
				+ "   ( YJ_ZY02.JGID = :al_jgid ) ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_zxks",
				Long.parseLong(valueOfString(req.get("zxks"))));
		parameters.put("al_jgid", Long.parseLong(req.get("jgid").toString()));
		if (req.get("yjxh") != null) {
			// hql_mz.append(" and ( MS_YJ01.YJXH = :al_yjxh)") ;
			parameters.put("al_yjxh",
					Long.parseLong(req.get("yjxh").toString()));
		}
		String searchParam = req.get("search") != null ? (String) req
				.get("search") : null;
		if (!IsNull(searchParam)) {
			hql += " AND CAST(YJ_ZY01.ZYHM as long)  = :al_zyhm";
			parameters.put("al_zyhm", Long.parseLong(searchParam));
		}
		hql += " order by YJ_ZY01.KDRQ desc";
		try {
			List<Map<String, Object>> list_YJ_MZYW = dao.doQuery(hql,
					parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYW,
					BSPHISEntryNames.YJ_ZYYW);
			return list_YJ_MZYW;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技列表失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技列表失败!");
		}
	}

	/**
	 * 住院医技单查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doQueryJCList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String hql = "SELECT JC_YJ01.YJXH as YJXH,"
				+ "JC_YJ01.TJHM as TJHM,   " + "JC_YJ01.BRXM as BRXM,   "
				+ "JC_YJ01.ZYHM as ZYHM,   " + "JC_YJ01.KDRQ as KDRQ,  "
				+ "JC_YJ01.KSDM as KSDM,  " + "JC_YJ01.YSDM as YSDM,  "
				+ "JC_YJ01.ZXRQ as ZXRQ,  " + "JC_YJ01.ZXKS as ZXKS,  "
				+ "JC_YJ01.ZXYS as ZXYS,  " + "JC_YJ01.ZXPB as ZXPB,  "
				+ "JC_YJ01.HJGH as HJGH,  " + "JC_YJ01.ZYSX as ZYSX,  "
				+ "JC_YJ01.ZFPB as ZFPB,  " + "JC_YJ01.ZYH as ZYH,  "
				+ "JC_YJ01.DJZT as DJZT,  "
				+ "JC_YJ02.YZXH as YZXH,  " + "JC_YJ02.YLXH as YLXH,  "
				+ "JC_YJ02.YJZX as YJZX, " + "JC_BRRY.BRXB as BRXB,"
				+ "JC_BRRY.CSNY as CSNY," + "JC_BRRY.BRID as BRID,"
				+ "0.00 AS ZJJE," + "'' as	ZDMC," + "''  AS AGE "
				+ "FROM JC_YJ01 as JC_YJ01,  " + "     JC_YJ02 as JC_YJ02,"
				+ "		JC_BRRY as JC_BRRY"
				+ " WHERE ( JC_YJ01.YJXH = JC_YJ02.YJXH ) AND"
				+ "		( JC_BRRY.ZYH = JC_YJ01.ZYH ) AND"
				+ "     ( JC_YJ01.ZXPB = 0 OR  " + "     JC_YJ01.ZXPB = 9 OR  "
				+ "     JC_YJ01.ZXPB IS NULL ) AND  "
				+ "     ( JC_YJ02.YJZX = 1 ) AND  "
				+ "   ( JC_YJ01.ZXKS = :al_zxks ) AND  "
				+ "    (JC_YJ01.JGID = :al_jgid) AND  "
				+ "   ( JC_YJ02.JGID = :al_jgid ) ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_zxks",
				Long.parseLong(valueOfString(req.get("zxks"))));
		parameters.put("al_jgid", req.get("jgid") + "");
		if (req.get("yjxh") != null) {
			// hql_mz.append(" and ( MS_YJ01.YJXH = :al_yjxh)") ;
			parameters.put("al_yjxh",
					Long.parseLong(req.get("yjxh").toString()));
		}
		String searchParam = req.get("search") != null ? (String) req
				.get("search") : null;
		if (!IsNull(searchParam)) {
			hql += " AND CAST(JC_YJ01.ZYHM as long)  = :al_zyhm";
			parameters.put("al_zyhm", Long.parseLong(searchParam));
		}
		hql += " order by JC_YJ01.KDRQ desc";
		try {
			List<Map<String, Object>> list_JC_YJ01_ZX = dao.doQuery(hql,
					parameters);
			SchemaUtil.setDictionaryMassageForList(list_JC_YJ01_ZX,
					BSPHISEntryNames.JC_YJ01_ZX);
			return list_JC_YJ01_ZX;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技列表失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技列表失败!");
		}
	}

	public List<Map<String, Object>> getMzList_Proj(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_mz = new StringBuffer(
				"SELECT b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,b.FYDW as FYDW,a.HJJE as HJJE,a.FYGB as FYGB,a.BZXX as BZXX FROM ")
				.append("MS_YJ02")
				.append(" a,GY_YLSF")
				.append(" b WHERE ( a.YJXH = :al_yjxh ) AND ( a.YLXH = b.FYXH ) AND ( a.JGID = :al_jgid)");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid", req.get("jgid"));
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql_mz.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.MS_YJ02_CIC);
			return list_YJ_MZYWMX;
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊医技项目失败!");
		}
	}

	public List<Map<String, Object>> doGetMzForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		long yjxh = Long.parseLong(req.get("yjxh") + "");
		StringBuffer hql_mz = new StringBuffer(
				"SELECT c.CardNO as JZKH,b.MZHM as MZHM,b.BRXZ as BRXZ,a.YJXH as YJXH,a.FPHM as FPHM,a.TJHM as TJHM,a.BRID as BRID,b.BRXM as BRXM,a.KDRQ as KDRQ,a.KSDM as KSDM,a.YSDM as YSDM,a.ZXRQ as ZXRQ,a.ZXKS as ZXKS,a.ZXYS as ZXYS,a.ZXPB as ZXPB,a.HJGH as HJGH,a.YJGL as YJGL,a.ZYSX as ZYSX,a.HYMX as HYMX,a.ZFPB as ZFPB,a.YJPH as YJPH,a.JGID as JGID,a.DJLY as DJLY FROM ")
				.append("MS_YJ01")
				.append(" a,MS_BRDA")
				.append(" b left join ")
				.append("MPI_Card")
				.append(" c on c.cardTypeCode="
						+ KLX
						+ " and c.empiId = b.EMPIID WHERE a.BRID = b.BRID and a.YJXH = :ll_yjxh and a.JGID = :al_jgid");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ll_yjxh", yjxh);
		parameters.put("al_jgid", JGID);
		try {
			List<Map<String, Object>> list_YJ_MZYW = dao.doSqlQuery(
					hql_mz.toString(), parameters);
			SchemaUtil.setDictionaryMassageForForm(list_YJ_MZYW,
					BSPHISEntryNames.YJ_MZFORM);
			return list_YJ_MZYW;
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊医技列表失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊医技列表失败!");
		}
	}

	public List<Map<String, Object>> doGetZYForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		long yjxh = Long.parseLong(req.get("yjxh") + "");
		String hql = (" SELECT YJ_ZY01.JGID as JGID ,"
				+ "YJ_ZY01.YJXH as YJXH ," + "YJ_ZY01.TJHM as TJHM ,"
				+ "YJ_ZY01.ZYHM as ZYHM ," + "YJ_ZY01.BRXM as BRXM ,"
				+ "YJ_ZY01.KDRQ as KDRQ ," + "YJ_ZY01.KSDM as KSDM ,"
				+ "YJ_ZY01.YSDM as YSDM ," + "YJ_ZY01.ZXRQ as ZXRQ ,"
				+ "YJ_ZY01.ZXKS as ZXKS ," + "YJ_ZY01.ZXYS as ZXYS ,"
				+ "YJ_ZY01.ZXPB as ZXPB ," + "YJ_ZY01.HJGH as HJGH ,"
				+ "YJ_ZY01.ZYSX as ZYSX ," + "YJ_ZY01.ZFPB as ZFPB ,"
				+ "YJ_ZY01.ZYH as ZYH ," + "ZY_BRRY.BRXZ as BRXZ ,"
				+ "ZY_BRRY.BRXM as BRXM ," + "ZY_BRRY.BRXB as BRXB ,"
				+ "ZY_BRRY.BRCH as BRCH ," + "ZY_BRRY.BRBQ as BRBQ ,"
				+ "YJ_ZY01.HYMX as HYMX ," + "YJ_ZY01.YJPH as YJPH,"
				+ "YJ_ZY01.FYBQ as FYBQ  " + " FROM YJ_ZY01 YJ_ZY01,   "
				+ "  ZY_BRRY ZY_BRRY  "
				+ "  WHERE ( YJ_ZY01.ZYH = ZY_BRRY.ZYH ) AND  "
				+ "   ( YJ_ZY01.YJXH = :al_yjxh ) AND  "
				+ "   ( YJ_ZY01.JGID = :al_jgid) AND "
				+ "  ( ZY_BRRY.JGID = :al_jgid)");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yjxh", yjxh);
		parameters.put("al_jgid", new Long(JGID));
		try {
			List<Map<String, Object>> list_YJ_MZYW = dao.doQuery(hql,
					parameters);
			SchemaUtil.setDictionaryMassageForForm(list_YJ_MZYW,
					BSPHISEntryNames.YJ_ZYFORM);
			return list_YJ_MZYW;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院病人信息失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院病人信息失败!");
		}
	}

	public List<Map<String, Object>> doGetJCForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
				// String JGID = user.get("manageUnit.id");
				UserRoleToken user = UserRoleToken.getCurrent();
				String JGID = user.getManageUnit().getId();
				long yjxh = Long.parseLong(req.get("yjxh") + "");
				String hql = (" SELECT JC_YJ01.JGID as JGID ,"
						+ "JC_YJ01.YJXH as YJXH ," + "JC_YJ01.TJHM as TJHM ,"
						+ "JC_YJ01.ZYHM as ZYHM ," + "JC_YJ01.BRXM as BRXM ,"
						+ "JC_YJ01.KDRQ as KDRQ ," + "JC_YJ01.KSDM as KSDM ,"
						+ "JC_YJ01.YSDM as YSDM ," + "JC_YJ01.ZXRQ as ZXRQ ,"
						+ "JC_YJ01.ZXKS as ZXKS ," + "JC_YJ01.ZXYS as ZXYS ,"
						+ "JC_YJ01.ZXPB as ZXPB ," + "JC_YJ01.HJGH as HJGH ,"
						+ "JC_YJ01.ZYSX as ZYSX ," + "JC_YJ01.ZFPB as ZFPB ,"
						+ "JC_YJ01.ZYH as ZYH ," + "JC_BRRY.BRXZ as BRXZ ,"
						+ "JC_BRRY.BRXM as BRXM ," + "JC_BRRY.BRXB as BRXB ,"
						+ "JC_YJ01.HYMX as HYMX ," + "JC_YJ01.YJPH as YJPH"
						 + " FROM JC_YJ01 JC_YJ01,   "
						+ "  JC_BRRY JC_BRRY  "
						+ "  WHERE ( JC_YJ01.ZYH = JC_BRRY.ZYH ) AND  "
						+ "   ( JC_YJ01.YJXH = :al_yjxh ) AND  "
						+ "   ( JC_YJ01.JGID = :al_jgid) AND "
						+ "  ( JC_BRRY.JGID = :al_jgid)");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("al_yjxh", yjxh);
				parameters.put("al_jgid", JGID);
				try {
					List<Map<String, Object>> list_JC_YJFORM = dao.doQuery(hql,
							parameters);
					SchemaUtil.setDictionaryMassageForForm(list_JC_YJFORM,
							BSPHISEntryNames.JC_YJFORM);
					return list_JC_YJFORM;
				} catch (PersistentDataOperationException e) {
					logger.error("获取家床病人信息失败!", e);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "获取家床病人信息失败!");
				}
	}

	public void doGetMzEditList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_mz = new StringBuffer(
				"SELECT b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,b.FYDW as FYDW,a.HJJE as HJJE,a.FYGB as FYGB,a.JGID as JGID,a.YJXH as YJXH,a.YJZX as YJZX,a.YLXH as YLXH,a.SBXH as SBXH,a.XMLX as XMLX,1 as xgpb FROM ")
				.append("MS_YJ02")
				.append(" a,GY_YLSF")
				.append(" b WHERE a.YJXH = :al_yjxh and a.YLXH = b.FYXH and a.JGID = :al_jgid");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid", req.get("jgid"));
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql_mz.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.MS_YJ02_CIC);
			res.put("body", list_YJ_MZYWMX);
		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊医技项目失败!");
		}
	}

	public void doGetZYEditList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String hql = "SELECT YJ_ZY02.YJXH as YJXH, "
				+ "GY_YLSF.FYMC as FYMC, "
				+ "GY_YLSF.FYDW as FYDW, "
				+ "YJ_ZY02.YLSL as YLSL, "
				+ "YJ_ZY02.YLDJ as YLDJ, "
				+ "(YJ_ZY02.YLSL*YJ_ZY02.YLDJ) as HJJE,"
				+ "YJ_ZY02.ZFBL as ZFBL , "
				+ "YJ_ZY02.YJZX as YJZX , "
				+ "YJ_ZY02.FYGB as FYGB ,  "
				+ "YJ_ZY02.YLXH as YLXH ,  "
				+ "GY_YLSF.FYDW as YLDW ,   "
				+ "YJ_ZY02.SBXH as SBXH , "
				+ "YJ_ZY02.XMLX as XMLX , "
				+ "YJ_ZY02.YZXH as YZXH ,  "
				+ "1   as XGPB ,   "
				+ "YJ_ZY02.YEPB as YEPB , "
				+ "YJ_ZY02.JGID as JGID   "
				+ " FROM YJ_ZY02 YJ_ZY02 ,  GY_YLSF GY_YLSF  "
				+ " WHERE YJ_ZY02.YLXH = GY_YLSF.FYXH and ( YJ_ZY02.YJXH = :ll_yjxh ) AND   "
				+ "( YJ_ZY02.JGID = :al_jgid ) "
				+ " ORDER BY YJ_ZY02.SBXH ASC ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ll_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid",
				Long.parseLong(valueOfString(req.get("jgid"))));
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.MS_YJ02_CIC);
			res.put("body", list_YJ_MZYWMX);
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技项目失败!");
		}
	}

	public void doGetJCEditList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String hql = "SELECT JC_YJ02.YJXH as YJXH, "
				+ "GY_YLSF.FYMC as FYMC, "
				+ "GY_YLSF.FYDW as FYDW, "
				+ "JC_YJ02.YLSL as YLSL, "
				+ "JC_YJ02.YLDJ as YLDJ, "
				+ "(JC_YJ02.YLSL*JC_YJ02.YLDJ) as HJJE,"
				+ "JC_YJ02.ZFBL as ZFBL , "
				+ "JC_YJ02.YJZX as YJZX , "
				+ "JC_YJ02.FYGB as FYGB ,  "
				+ "JC_YJ02.YLXH as YLXH ,  "
				+ "GY_YLSF.FYDW as YLDW ,   "
				+ "JC_YJ02.SBXH as SBXH , "
				+ "JC_YJ02.XMLX as XMLX , "
				+ "JC_YJ02.YZXH as YZXH ,  "
				+ "1   as XGPB ,   "
				+ "JC_YJ02.YEPB as YEPB , "
				+ "JC_YJ02.JGID as JGID   "
				+ " FROM JC_YJ02 JC_YJ02 ,  GY_YLSF GY_YLSF  "
				+ " WHERE JC_YJ02.YLXH = GY_YLSF.FYXH and ( JC_YJ02.YJXH = :ll_yjxh ) AND   "
				+ "( JC_YJ02.JGID = :al_jgid ) "
				+ " ORDER BY JC_YJ02.SBXH ASC ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ll_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid",
				valueOfString(req.get("jgid")));
		try {
			List<Map<String, Object>> list_JC_YJLIST = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_JC_YJLIST,
					BSPHISEntryNames.JC_YJLIST);
			res.put("body", list_JC_YJLIST);
		} catch (PersistentDataOperationException e) {
			logger.error("获取家床医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取家床医技项目失败!");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getZyList_Proj(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String hql = "SELECT GY_YLSF.FYMC as FYMC,  " + "YJ_ZY02.YLDJ as YLDJ,"
				+ "YJ_ZY02.YLSL as YLSL," + "YJ_ZY02.ZFBL as ZFBL,"
				+ "YJ_ZY02.YZXH as YZXH," + "YJ_ZY02.YLXH as YLXH,"
				+ "GY_YLSF.FYDW as FYDW," + "YJ_ZY02.YEPB as YEPB,"
				+ "(YJ_ZY02.YLSL*YJ_ZY02.YLDJ) as YLJE"
				+ " FROM YJ_ZY02 YJ_ZY02," + "GY_YLSF GY_YLSF "
				+ " WHERE ( YJ_ZY02.YLXH = GY_YLSF.FYXH ) and "
				+ " ( YJ_ZY02.YJXH = :al_yjxh ) AND  "
				+ " YJ_ZY02.JGID = :al_jgid ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid", Long.parseLong(JGID));
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.MS_YJ02_CIC);
			return list_YJ_MZYWMX;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技项目失败!");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doQueryJcList_Proj(
			Map<String, Object> req, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String hql = "SELECT GY_YLSF.FYMC as FYMC,  " + "JC_YJ02.YLDJ as YLDJ,"
				+ "JC_YJ02.YLSL as YLSL," + "JC_YJ02.ZFBL as ZFBL,"
				+ "JC_YJ02.YZXH as YZXH," + "JC_YJ02.YLXH as YLXH,"
				+ "GY_YLSF.FYDW as FYDW," + "JC_YJ02.YEPB as YEPB,"
				+ "(JC_YJ02.YLSL*JC_YJ02.YLDJ) as YLJE"
				+ " FROM JC_YJ02 JC_YJ02," + "GY_YLSF GY_YLSF "
				+ " WHERE ( JC_YJ02.YLXH = GY_YLSF.FYXH ) and "
				+ " ( JC_YJ02.YJXH = :al_yjxh ) AND  "
				+ " JC_YJ02.JGID = :al_jgid ";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_yjxh",
				Long.parseLong(valueOfString(req.get("yjxh"))));
		parameters.put("al_jgid", JGID);
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.JC_YJYWMX);
			return list_YJ_MZYWMX;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技项目失败!");
		}
	}

	public void doSaveMZYJAndProject(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			req = (Map<String, Object>) req.get("body");
			Map<String, Object> formValue = (Map<String, Object>) req
					.get("formValue");
			Map<String, Object> params = (Map<String, Object>) req.get("param");
			List<Map<String, Object>> listValue = (List<Map<String, Object>>) req
					.get("listValue");
			String XZPB = params.get("XZPB") + "";
			// 保存医技记录
			// formValue.put("YJXH", params.get("YJXH"));
			// formValue.put("XZPB", params.get("XZPB"));
			Map<String, Object> MS_YJ01 = new HashMap<String, Object>();
			long YJXH = 0;
			if ("1".equals(XZPB)) {
				formValue.put("JGID", JGID);
				formValue.put("ZXPB", 0);
				formValue.put("ZFPB", 0);
				formValue.put("DJZT", 0);
				formValue.put("DJLY", 4);
				if (formValue.get("ZXRQ") != null) {
					String zxrq = formValue.get("ZXRQ").toString();
					Date sdf;
					try {
						sdf = new SimpleDateFormat("yyyy-MM-dd").parse(zxrq);
						String sdf_current = new SimpleDateFormat("HH:mm:ss")
								.format(new Date());
						formValue.put("ZXRQ", zxrq + " " + sdf_current);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				formValue.put("KDRQ",
						new java.sql.Date(System.currentTimeMillis()));
				MS_YJ01 = dao.doSave("create", BSPHISEntryNames.MS_YJ01_CIC,
						formValue, false);
				YJXH = Long.parseLong(MS_YJ01.get("YJXH") + "");
			} else {
				if (formValue.get("ZXRQ") != null) {
					String zxrq = formValue.get("ZXRQ").toString();
					Date sdf;
					try {
						sdf = new SimpleDateFormat("yyyy-MM-dd").parse(zxrq);
						String sdf_current = new SimpleDateFormat("HH:mm:ss")
								.format(new Date());
						formValue.put("ZXRQ", zxrq + " " + sdf_current);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				YJXH = Long.parseLong(params.get("YJXH") + "");
				MS_YJ01 = dao.doSave("update", BSPHISEntryNames.MS_YJ01_CIC,
						formValue, false);
				dao.doRemove("YJXH", YJXH, BSPHISEntryNames.MS_YJ02_CIC);
			}
			for (int i = 0; i < listValue.size(); i++) {
				Map<String, Object> MS_YJ02 = listValue.get(i);
				MS_YJ02.put("YJXH", YJXH);
				if (i == 0) {
					MS_YJ02.put("YJZX", 1);
				} else {
					MS_YJ02.put("YJZX", 0);
				}
				MS_YJ02.put("YJZH", 1);
				MS_YJ02.put("JGID", JGID);
				MS_YJ02.put("XMLX", 0);
				MS_YJ02.put("DZBL", 1);
				dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC, MS_YJ02,
						false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("保存门诊医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存门诊医技项目失败!", e);
		} catch (ValidateException e) {
			logger.error("保存门诊医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存门诊医技项目失败!", e);
		}

	}

	public void doSaveZYYJAndProject(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			req = (Map<String, Object>) req.get("body");
			Map<String, Object> formValue = (Map<String, Object>) req
					.get("formValue");
			Map<String, Object> params = (Map<String, Object>) req.get("param");
			List<Map<String, Object>> listValue = (List<Map<String, Object>>) req
					.get("listValue");
			String XZPB = params.get("XZPB") + "";
			// 保存医技记录
			// formValue.put("YJXH", params.get("YJXH"));
			// formValue.put("XZPB", params.get("XZPB"));
			Map<String, Object> MS_YJ01 = new HashMap<String, Object>();
			long YJXH = 0;
			if ("1".equals(XZPB)) {
				formValue.put("JGID", JGID);
				formValue.put("ZXPB", 0);
				formValue.put("ZFPB", 0);
				formValue.put("DJZT", 0);
				formValue.put("DJLY", 4);
				formValue.put("HJGH", (String) user.getUserId());
				formValue.put("FYBQ", formValue.get("BRBQ"));
				formValue.put("ZYHM", formValue.get("ZYHM"));
				formValue.put("KDRQ",
						new java.sql.Date(System.currentTimeMillis()));
				formValue.put("TJSJ",
						new java.sql.Date(System.currentTimeMillis()));// 新增提交时间
				MS_YJ01 = dao.doSave("create", BSPHISEntryNames.YJ_ZY01,
						formValue, true);
				YJXH = Long.parseLong(MS_YJ01.get("YJXH") + "");
			} else {
				YJXH = Long.parseLong(params.get("YJXH") + "");
				MS_YJ01 = dao.doSave("update", BSPHISEntryNames.YJ_ZY01,
						formValue, false);
				dao.doRemove("YJXH", YJXH, BSPHISEntryNames.YJ_ZY02);
			}
			for (int i = 0; i < listValue.size(); i++) {
				Map<String, Object> YJ_ZY02 = listValue.get(i);
				YJ_ZY02.put("YJXH", YJXH);
				if (i == 0) {
					YJ_ZY02.put("YJZX", 1);
				} else {
					YJ_ZY02.put("YJZX", 0);
				}
				YJ_ZY02.put("YJZH", 1);
				YJ_ZY02.put("JGID", JGID);
				YJ_ZY02.put("XMLX", 0);
				YJ_ZY02.put("DZBL", 1);
				dao.doSave("create", BSPHISEntryNames.YJ_ZY02, YJ_ZY02, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存住院医技项目失败!");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存住院医技项目失败!");
		}

	}

	@SuppressWarnings("unchecked")
	public void doSaveJCYJAndProject(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();
			req = (Map<String, Object>) req.get("body");
			Map<String, Object> formValue = (Map<String, Object>) req
					.get("formValue");
			Map<String, Object> params = (Map<String, Object>) req.get("param");
			List<Map<String, Object>> listValue = (List<Map<String, Object>>) req
					.get("listValue");
			String XZPB = params.get("XZPB") + "";
			// 保存医技记录
			// formValue.put("YJXH", params.get("YJXH"));
			// formValue.put("XZPB", params.get("XZPB"));
			Map<String, Object> MS_YJ01 = new HashMap<String, Object>();
			long YJXH = 0;
			if ("1".equals(XZPB)) {
				formValue.put("JGID", JGID);
				formValue.put("ZXPB", 0);
				formValue.put("ZFPB", 0);
				formValue.put("DJZT", 0);
				formValue.put("DJLY", 4);
				formValue.put("HJGH", (String) user.getUserId());
				formValue.put("FYBQ", formValue.get("BRBQ"));
				formValue.put("ZYHM", formValue.get("ZYHM"));
				formValue.put("KDRQ",
						new java.sql.Date(System.currentTimeMillis()));
				formValue.put("TJSJ",
						new java.sql.Date(System.currentTimeMillis()));// 新增提交时间
				MS_YJ01 = dao.doSave("create", BSPHISEntryNames.JC_YJ01_ZX,
						formValue, true);
				YJXH = Long.parseLong(MS_YJ01.get("YJXH") + "");
			} else {
				YJXH = Long.parseLong(params.get("YJXH") + "");
				MS_YJ01 = dao.doSave("update", BSPHISEntryNames.JC_YJ01_ZX,
						formValue, false);
				dao.doRemove("YJXH", YJXH, BSPHISEntryNames.JC_YJ02_ZX);
			}
			for (int i = 0; i < listValue.size(); i++) {
				Map<String, Object> JC_YJ02 = listValue.get(i);
				JC_YJ02.put("YJXH", YJXH);
				if (i == 0) {
					JC_YJ02.put("YJZX", 1);
				} else {
					JC_YJ02.put("YJZX", 0);
				}
				JC_YJ02.put("YJZH", 1);
				JC_YJ02.put("JGID", JGID);
				JC_YJ02.put("XMLX", 0);
				JC_YJ02.put("DZBL", 1);
				dao.doSave("create", BSPHISEntryNames.JC_YJ02_ZX, JC_YJ02,
						false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存家床医技项目失败!");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存家床医技项目失败!");
		}

	}

	/*
	 * 医技单删除 门诊医技所开但未收费的都可删除， 其他则不能，住院医技删除YJ_ZY01、 YJ_ZY02表的对应数据，
	 * 同时更新ZY_BQYZ表的SYBZ、YJXH字段。
	 */
	@SuppressWarnings("unchecked")
	public void deleteMedicalTechnicalByParam(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> params_mz = (Map<String, Object>) body
				.get("params_mz");
		Map<String, Object> params_zy = (Map<String, Object>) body
				.get("params_zy");
		Map<String, Object> params_jc = (Map<String, Object>) body
				.get("params_jc");
		String mzzypb = valueOfString(body.get("MZZYPB"));
		Integer il_mzzypb = Integer.parseInt(mzzypb);
		Map<String, Object> parameters;
		try {
			switch (il_mzzypb) {
			case 0: // 门诊
				String mz_yjxh = valueOfString(params_mz.get("YJXH")), // 医技序号
				mz_djly = valueOfString(params_mz.get("DJLY")), // 单据来源
				mz_jgid = valueOfString(params_mz.get("JGID")),
				mz_ls_realFphm,
				mz_ll_zxpb;
				parameters = new HashMap<String, Object>();
				parameters.put("yjxh", mz_yjxh);
				parameters.put("jgid", mz_jgid);
				List<Map<String, Object>> list = dao
						.doQuery(
								"SELECT FPHM as FPHM,ZFPB as ZFPB  From MS_YJ01 Where yjxh = :yjxh  And JGID = :jgid ",
								parameters);
				if (list.size() > 0) {
					Map<String, Object> yjMap = list.get(0);
					mz_ls_realFphm = valueOfString(yjMap.get("FPHM"));
					mz_ll_zxpb = valueOfString(yjMap.get("ZXPB"));
					if (!IsNull(mz_ls_realFphm)) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "该检查单已收过费,不能被删除，将重新刷新列表!");
						return;
					}
					if (!IsNull(mz_djly) && !mz_djly.equals("4")) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "该检查单不是医技科室所开项目，不能删除!");
						return;
					}
					if (!IsNull(mz_ll_zxpb) && mz_ll_zxpb.equals("1")) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "该检查单已被他人执行,不能被删除，将重新刷新列表!");
						return;
					}
				} else {
					res.put("errorCode", 1);//
					res.put("errorMsg", "未找到门诊医技记录，将重新刷新列表!");
					return;
				}
				// 执行删除
				parameters = new HashMap<String, Object>();
				parameters.put("ll_yjxh", mz_yjxh);
				parameters.put("il_jgid", mz_jgid);
				int r1 = dao
						.doSqlUpdate(
								"DELETE From MS_YJ02 Where yjxh = :ll_yjxh  And JGID = :il_jgid ",
								parameters);
				int r2 = dao
						.doSqlUpdate(
								"DELETE From MS_YJ01 Where yjxh = :ll_yjxh  And JGID = :il_jgid ",
								parameters);
				if (r1 <= 0 || r2 <= 0) {
					res.put("errorCode", 1);//
					res.put("errorMsg", "删除门诊医技单失败!");
					return;
				}
				// end with delete mzyj
				break;
			case 1: // 住院
				String zy_jgid = valueOfString(params_zy.get("JGID"));
				String zy_yjxh = valueOfString(params_zy.get("YJXH"));
				ArrayList<Integer> zy_xm_yzxh = (ArrayList<Integer>) params_zy
						.get("YZXH_S");
				parameters = new HashMap<String, Object>();
				parameters.put("ll_yjxh", Long.parseLong(zy_yjxh));
				String zxpb = null;
				List<Map<String, Object>> listZxpb = dao
						.doQuery(
								"SELECT ZFPB as ZFPB From YJ_ZY01 Where YJXH = :ll_yjxh ",
								parameters);
				if (listZxpb.size() > 0) {
					Map<String, Object> zxpbMap = listZxpb.get(0);
					zxpb = valueOfString(zxpbMap.get("ZXPB"));
					if (zxpb.equals("1")) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "该检查单已被他人执行,不能被删除，将重新刷新列表!");
						return;
					}
					List<Map<String, Object>> hasZYTJ = dao
							.doQuery(
									"From YJ_ZY01 a , YJ_ZY02 b Where a.YJXH= b.YJXH and  a.YJXH = :ll_yjxh and b.YZXH>0",
									parameters);
					if (hasZYTJ.size() > 0) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "住院系统提交的医技项目不能被删除，将重新刷新列表!");
						return;
					}
					parameters.put("il_jgid", zy_jgid);
					int r3 = dao
							.doSqlUpdate(
									"DELETE From YJ_ZY02 Where YJXH = :ll_yjxh  And JGID = :il_jgid ",
									parameters);
					int r4 = dao
							.doSqlUpdate(
									"DELETE From YJ_ZY01 Where YJXH = :ll_yjxh  And JGID = :il_jgid ",
									parameters);
					if (r3 <= 0 || r4 <= 0) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "删除住院医技单失败!");
						return;
					}

					// 更新医嘱表的医技项目使用
					String hql = "UPDATE ZY_BQYZ Set SYBZ = 0,YJXH = 0 Where JLXH = :ll_yzxh ";
					for (Integer yzxh : zy_xm_yzxh) {
						if (IsNull(yzxh))
							continue;
						parameters = new HashMap<String, Object>();
						parameters.put("ll_yzxh", yzxh.longValue());
						dao.doUpdate(hql, parameters);
					}
				} else {
					res.put("errorCode", 1);//
					res.put("errorMsg", "未找到住院医技记录，将重新刷新列表!");
					return;
				}
				break;
			case 2: // 家床
				String jc_jgid = valueOfString(params_jc.get("JGID"));
				String jc_yjxh = valueOfString(params_jc.get("YJXH"));
				ArrayList<Integer> jc_xm_yzxh = (ArrayList<Integer>) params_jc
						.get("YZXH_S");
				parameters = new HashMap<String, Object>();
				parameters.put("ll_yjxh", Long.parseLong(jc_yjxh));
				String jc_zxpb = null;
				List<Map<String, Object>> jc_listZxpb = dao
						.doQuery(
								"SELECT ZFPB as ZFPB From JC_YJ01 Where YJXH = :ll_yjxh ",
								parameters);
				if (jc_listZxpb.size() > 0) {
					Map<String, Object> zxpbMap = jc_listZxpb.get(0);
					jc_zxpb = valueOfString(zxpbMap.get("ZXPB"));
					if (jc_zxpb.equals("1")) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "该检查单已被他人执行,不能被删除，将重新刷新列表!");
						return;
					}
					List<Map<String, Object>> hasZYTJ = dao
							.doQuery(
									"From JC_YJ01 a , JC_YJ02 b Where a.YJXH= b.YJXH and  a.YJXH = :ll_yjxh and b.YZXH>0",
									parameters);
					if (hasZYTJ.size() > 0) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "住院系统提交的医技项目不能被删除，将重新刷新列表!");
						return;
					}
					parameters.put("il_jgid", jc_jgid);
					int r3 = dao
							.doSqlUpdate(
									"DELETE From JC_YJ02 Where YJXH = :ll_yjxh  And JGID = :il_jgid ",
									parameters);
					int r4 = dao
							.doSqlUpdate(
									"DELETE From JC_YJ01 Where YJXH = :ll_yjxh  And JGID = :il_jgid ",
									parameters);
					if (r3 <= 0 || r4 <= 0) {
						res.put("errorCode", 1);//
						res.put("errorMsg", "删除住院医技单失败!");
						return;
					}

					// 更新医嘱表的医技项目使用
					String hql = "UPDATE JC_BRYZ Set SYBZ = 0,YJXH = 0 Where JLXH = :ll_yzxh ";
					for (Integer yzxh : jc_xm_yzxh) {
						if (IsNull(yzxh))
							continue;
						parameters = new HashMap<String, Object>();
						parameters.put("ll_yzxh", yzxh.longValue());
						dao.doUpdate(hql, parameters);
					}
				} else {
					res.put("errorCode", 1);//
					res.put("errorMsg", "未找到家床医技记录，将重新刷新列表!");
					return;
				}
				break;
			}

		} catch (PersistentDataOperationException e) {
			logger.error("删除医技记录错误!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除医技记录错误!");
		}
	}

	public void doDeleteJcProject(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String yjxharr = req.get("YJXH") + "";
		String yjxh = yjxharr.substring(1, yjxharr.length() - 1);
		parameters.put("il_jgid", jgid);
		try {
			dao.doSqlUpdate(
					"update JC_BRYZ set SYBZ=0,TJZX=0,QRSJ=null where YJXH in ("
							+ yjxh + ") And JGID = :il_jgid", parameters);
			int YJ01 = dao.doSqlUpdate("DELETE From JC_YJ01 Where YJXH in ("
					+ yjxh + ")  And JGID = :il_jgid ", parameters);
			int YJ02 = dao.doSqlUpdate("DELETE From JC_YJ02 Where YJXH in("
					+ yjxh + ")  And JGID = :il_jgid ", parameters);
			if (YJ01 <= 0 || YJ02 <= 0) {
				res.put("errorCode", 1);
				res.put("errorMsg", "家床医技单退回失败!");
				return;
			}
		} catch (PersistentDataOperationException e) {
			logger.error("删除医技记录错误!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "家床医技单退回失败!");
		}
	}

	/*
	 * 
	 * 医技项目执行 业务描述: 门诊执行需判断是否作废、 是否可选择执行科室，执行后更新ms_yj01表的zxpb字段等；
	 * 住院执行需判断是否在院，是否看选择执行科室， 执行后更新yj_zy01、yj_zy02表的zxpb，
	 * 插入zy_fymx表数据并更新zy_bqyz表字段
	 */
	@SuppressWarnings("unchecked")
	public void excuteMedicalTechnicalByParam(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> params_mz_list = (List<Map<String, Object>>) body
				.get("params_mz");
		List<Map<String, Object>> params_zy_list = (List<Map<String, Object>>) body
				.get("params_zy");
		List<Map<String, Object>> params_jc_list = (List<Map<String, Object>>) body
				.get("params_jc");
		Integer il_mzzypb = (Integer) body.get("MZZYPB");
		UserRoleToken user = UserRoleToken.getCurrent();
		HashMap<String, Object> parameters;
		try {
			switch (il_mzzypb) {
			case 0: // 门诊医技执行
				for (int i = 0; i < params_mz_list.size(); i++) {
					Map<String, Object> params_mz = params_mz_list.get(i);

					String mz_brid = valueOfString(params_mz.get("BRID")), mz_yjxh = valueOfString(params_mz
							.get("YJXH")), mz_zxys = valueOfString(params_mz
							.get("ZXYS")), mz_jgid = valueOfString(params_mz
							.get("JGID")), mz_zfpb, mz_fphm;
					if (mz_zxys == null || mz_zxys.equals("")) {
						mz_zxys = (String) user.getUserId();
					}
					parameters = new HashMap<String, Object>();
					parameters.put("yjxh", mz_yjxh);
					parameters.put("jgid", mz_jgid);
					List<Map<String, Object>> list = dao
							.doQuery(
									"SELECT FPHM as FPHM,ZFPB as ZFPB  From MS_YJ01 Where yjxh = :yjxh  And JGID = :jgid ",
									parameters);
					if (list.size() > 0) {
						Map<String, Object> zxpbMap = list.get(0);
						mz_zfpb = valueOfString(zxpbMap.get("ZFPB"));
						mz_fphm = valueOfString(zxpbMap.get("FPHM"));
						if (mz_zfpb.equals("1")) {
							res.put("errorCode", 1);//
							res.put("errorMsg", "该检查单已作废,不能被执行!");
							return;
						}
						if (!IsNull(mz_fphm)) {// 已经收费,执行门诊医技单
							boolean result = execute_mzyj(res, params_mz, ctx); // 执行门诊医技单
							if (!result) {
								return; // 执行失败,结束
							}
						} else {
							res.put("errorCode", 1);//
							res.put("errorMsg", "该检查单还没有收过费，不能被执行!");
							return;
						}
					}
				}
				break;
			case 1:// 住院医技执行
				for (int i = 0; i < params_zy_list.size(); i++) {
					Map<String, Object> params_zy = params_zy_list.get(i);

					String zy_yjxh = valueOfString(params_zy.get("YJXH")), zy_zxys = valueOfString(params_zy
							.get("ZXYS")), zy_zyh = valueOfString(params_zy
							.get("ZYH")), zy_jgid = valueOfString(params_zy
							.get("JGID"));
					if (IsNull(zy_zxys)) {
						zy_zxys = "";
					}
					if (0 < Integer.parseInt(zy_yjxh)) {
//						List<Map<String, Object>> cypbList = dao
//								.doQuery(
//										"SELECT CYPB as CYPB From ZY_BRRY Where zyh = :ll_zyh And JGID = :jgid",
//										new MapParameter()
//												.put("ll_zyh", zy_zyh).put(
//														"jgid", zy_jgid));
//						if (cypbList.size() > 0) {
//							int cypb = Integer.parseInt(valueOfString(cypbList
//									.get(0).get("CYPB")));
//							if (cypb >= 8) {
//								res.put("errorCode", 1);//
//								res.put("errorMsg", "该病人已出院! ");
//								return;
//							}
//						}

						if (IsNull(zy_zxys)) {
							zy_zxys = (String) user.getUserId();
						}// 设置默认执行医生
							// 执行住院医技单
						boolean r = execute_zyyj(res, params_zy, ctx);
						if (!r) {
							return; // 执行失败,结束
						}
						// end
					}
				}
				break;
			case 2:// 家床医技执行
				for (int i = 0; i < params_jc_list.size(); i++) {
					Map<String, Object> params_jc = params_jc_list.get(i);

					String jc_yjxh = valueOfString(params_jc.get("YJXH")), jc_zxys = valueOfString(params_jc
							.get("ZXYS")), jc_zyh = valueOfString(params_jc
							.get("ZYH")), jc_jgid = valueOfString(params_jc
							.get("JGID"));
					if (IsNull(jc_zxys)) {
						jc_zxys = "";
					}
					if (0 < Integer.parseInt(jc_yjxh)) {
//						List<Map<String, Object>> cypbList = dao
//								.doQuery(
//										"SELECT CYPB as CYPB From JC_BRRY Where zyh = :ll_zyh And JGID = :jgid",
//										new MapParameter()
//												.put("ll_zyh", jc_zyh).put(
//														"jgid", jc_jgid));
//						if (cypbList.size() > 0) {
//							int cypb = Integer.parseInt(valueOfString(cypbList
//									.get(0).get("CYPB")));
//							if (cypb >= 8) {
//								res.put("errorCode", 1);//
//								res.put("errorMsg", "该病人已出院! ");
//								return;
//							}
//						}

						if (IsNull(jc_zxys)) {
							jc_zxys = (String) user.getUserId();
						}// 设置默认执行医生
							// 执行住院医技单
						boolean r = execute_jcyj(res, params_jc, ctx);
						if (!r) {
							return; // 执行失败,结束
						}
						// end
					}
				}
				break;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行医技项目失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行医技项目失败!", e);
		} catch (ServiceException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行医技项目失败!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行医技项目失败!", e);
		}
	}

	/*
	 * 
	 * 医技项目执行 业务描述: 门诊执行需判断是否作废、 是否可选择执行科室，执行后更新ms_yj01表的zxpb字段等；
	 * 住院执行需判断是否在院，是否看选择执行科室， 执行后更新yj_zy01、yj_zy02表的zxpb，
	 * 插入zy_fymx表数据并更新zy_bqyz表字段
	 */
	@SuppressWarnings("unchecked")
	public void doQueryYjProject(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> params_mz_list = (List<Map<String, Object>>) body
				.get("params_mz");
		List<Map<String, Object>> params_zy_list = (List<Map<String, Object>>) body
				.get("params_zy");
		List<Map<String, Object>> params_jc_list = (List<Map<String, Object>>) body
				.get("params_jc");
		Integer il_mzzypb = (Integer) body.get("MZZYPB");
		UserRoleToken user = UserRoleToken.getCurrent();
		HashMap<String, Object> parameters;
		try {
			switch (il_mzzypb) {
			case 0: // 门诊医技执行
				for (int i = 0; i < params_mz_list.size(); i++) {
					Map<String, Object> params_mz = params_mz_list.get(i);

					String mz_brid = valueOfString(params_mz.get("BRID")), mz_yjxh = valueOfString(params_mz
							.get("YJXH")), mz_zxys = valueOfString(params_mz
							.get("ZXYS")), mz_jgid = valueOfString(params_mz
							.get("JGID")), mz_zfpb, mz_fphm;
					if (mz_zxys == null || mz_zxys.equals("")) {
						mz_zxys = (String) user.getUserId();
					}
					parameters = new HashMap<String, Object>();
					parameters.put("yjxh", mz_yjxh);
					parameters.put("jgid", mz_jgid);
					List<Map<String, Object>> list = dao
							.doQuery(
									"SELECT FPHM as FPHM,ZFPB as ZFPB  From MS_YJ01 Where yjxh = :yjxh  And JGID = :jgid ",
									parameters);
					if (list.size() > 0) {
						Map<String, Object> zxpbMap = list.get(0);
						mz_zfpb = valueOfString(zxpbMap.get("ZFPB"));
						mz_fphm = valueOfString(zxpbMap.get("FPHM"));
						if (mz_zfpb.equals("1")) {
							res.put("errorCode", 1);//
							res.put("errorMsg", "该检查单已作废,不能被执行!");
							return;
						}
						if (!IsNull(mz_fphm)) {// 已经收费,执行门诊医技单
							// boolean result = execute_mzyj(res, params_mz,
							// ctx);
							// // 执行门诊医技单
							// if (!result) {
							// return; // 执行失败,结束
							// }
						} else {
							res.put("errorCode", 1);//
							res.put("errorMsg", "该检查单还没有收过费，不能被执行!");
							return;
						}
					}
				}
				break;
			case 1:// 住院医技执行
				for (int i = 0; i < params_zy_list.size(); i++) {
					Map<String, Object> params_zy = params_zy_list.get(i);

					String zy_yjxh = valueOfString(params_zy.get("YJXH")), zy_zxys = valueOfString(params_zy
							.get("ZXYS")), zy_zyh = valueOfString(params_zy
							.get("ZYH")), zy_jgid = valueOfString(params_zy
							.get("JGID"));
					if (IsNull(zy_zxys)) {
						zy_zxys = "";
					}
					if (0 < Integer.parseInt(zy_yjxh)) {
						List<Map<String, Object>> cypbList = dao
								.doQuery(
										"SELECT CYPB as CYPB From ZY_BRRY Where zyh = :ll_zyh And JGID = :jgid",
										new MapParameter()
												.put("ll_zyh", zy_zyh).put(
														"jgid", zy_jgid));
						if (cypbList.size() > 0) {
							int cypb = Integer.parseInt(valueOfString(cypbList
									.get(0).get("CYPB")));
							if (cypb >= 8) {
								res.put("errorCode", 1);//
								res.put("errorMsg", "该病人已出院! ");
								return;
							}
						}

						if (IsNull(zy_zxys)) {
							zy_zxys = (String) user.getUserId();
						}// 设置默认执行医生
							// 执行住院医技单
							// boolean r = execute_zyyj(res, params_zy, ctx);
						// if (!r) {
						// return; // 执行失败,结束
						// }
						// end
					}
				}
				break;
			case 2:// 家床医技执行
				for (int i = 0; i < params_jc_list.size(); i++) {
					Map<String, Object> params_jc = params_jc_list.get(i);

					String jc_yjxh = valueOfString(params_jc.get("YJXH")), jc_zxys = valueOfString(params_jc
							.get("ZXYS")), jc_zyh = valueOfString(params_jc
							.get("ZYH")), jc_jgid = valueOfString(params_jc
							.get("JGID"));
					if (IsNull(jc_zxys)) {
						jc_zxys = "";
					}
					if (0 < Integer.parseInt(jc_yjxh)) {
						List<Map<String, Object>> cypbList = dao
								.doQuery(
										"SELECT CYPB as CYPB From JC_BRRY Where zyh = :ll_zyh And JGID = :jgid",
										new MapParameter()
												.put("ll_zyh", jc_zyh).put(
														"jgid", jc_jgid));
						if (cypbList.size() > 0) {
							int cypb = Integer.parseInt(valueOfString(cypbList
									.get(0).get("CYPB")));
							if (cypb >= 8) {
								res.put("errorCode", 1);//
								res.put("errorMsg", "该病人已出院! ");
								return;
							}
						}

						if (IsNull(jc_zxys)) {
							jc_zxys = (String) user.getUserId();
						}// 设置默认执行医生
							// 执行住院医技单
							// boolean r = execute_zyyj(res, params_zy, ctx);
						// if (!r) {
						// return; // 执行失败,结束
						// }
						// end
					}
				}
				break;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "执行医技项目失败!", e);
		}
	}

	/**
	 * 门诊医技单执行
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 */
	public boolean execute_mzyj(Map<String, Object> res,
			Map<String, Object> mz_params, Context ctx)
			throws PersistentDataOperationException, ValidateException {
		String ls_zxys = valueOfString(mz_params.get("ZXYS")), czgh = (String) (UserRoleToken
				.getCurrent().getUserId()), // 操作工号
		yjxh = valueOfString(mz_params.get("YJXH")), zxpb, ls_zddm = valueOfString(mz_params
				.get("ZDID")), // 医技诊断结果
		jgid = valueOfString(mz_params.get("JGID")), jcks = valueOfString(mz_params
				.get("KSDM"));

		String ls_jcxm, ll_xmxh, ls_tjhm, ls_brxm, ll_ksdm, ls_ysdm, ls_srgh, ll_id, ls_Yjph, ls_mzhm, ll_brxb, ldt_BirthDay, ls_brxb, ls_Age = null;
		Long ld_hjje;// 准备要插入的参数
		Date jfrq = new Date();// 计费日期
		HashMap<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters
				.put("yjxh", Long.parseLong(mz_params.get("YJXH").toString()));
		parameters.put("jgid", mz_params.get("JGID"));
		List<Map<String, Object>> list = dao
				.doQuery(
						"SELECT ZXPB as ZXPB From MS_YJ01 Where YJXH = :yjxh AND JGID = :jgid",
						parameters);
		if (list.size() > 0) {
			Map<String, Object> pbMap = list.get(0);
			zxpb = valueOfString(pbMap.get("ZXPB"));
		} else {
			res.put("errorCode", 1);//
			res.put("errorMsg", "该检查单已被他人删除,不能再被执行!");
			return false;
		}
		if (zxpb.equals("1")) {
			res.put("errorCode", 1);//
			res.put("errorMsg", "该检查单已执行,不能重复执行!");
			return false;
		}
		// 准备要插入参数值r1
		List<Map<String, Object>> r1 = dao
				.doQuery(
						"SELECT GY_YLSF.FYMC as FYMC,MS_YJ02.YLXH as YLXH  FROM MS_YJ01 as MS_YJ01,MS_YJ02 as MS_YJ02,GY_YLSF as  GY_YLSF WHERE MS_YJ01.YJXH = MS_YJ02.YJXH"
								+ " And MS_YJ02.YJZX = 1 And MS_YJ02.YLXH = GY_YLSF.FYXH And MS_YJ01.YJXH = :yjxh  AND MS_YJ01.JGID = :jgid  AND MS_YJ02.JGID = :jgid ",
						parameters);
		ls_jcxm = valueOfString(r1.get(0).get("FYMC"));// 发药名称
		ll_xmxh = valueOfString(r1.get(0).get("YLXH"));// 医疗序号

		// 准备要插入参数值r2
		parameters = new HashMap<String, Object>();
		parameters
				.put("yjxh", Long.parseLong(mz_params.get("YJXH").toString()));
		List<Map<String, Object>> r2 = dao
				.doQuery(
						"SELECT TJHM as TJHM,BRXM as BRXM,KSDM as KSDM,YSDM as YSDM,HJGH as HJGH,BRID as BRID, YJPH as YJPH "
								+ " From MS_YJ01 Where YJXH = :yjxh",
						parameters);
		// INTO :ls_tjhm,ls_brxm,ll_ksdm,ls_ysdm,ls_srgh,ll_id,ls_Yjph
		ls_tjhm = valueOfString(r2.get(0).get("TJHM"));
		ls_brxm = valueOfString(r2.get(0).get("BRXM"));
		ll_ksdm = valueOfString(r2.get(0).get("KSDM"));
		ls_ysdm = valueOfString(r2.get(0).get("YSDM"));
		ls_srgh = valueOfString(r2.get(0).get("HJGH"));
		ll_id = valueOfString(r2.get(0).get("BRID"));
		ls_Yjph = valueOfString(r2.get(0).get("YJPH"));
		// Into :ls_mzhm,ll_brxb,ldt_BirthDay
		parameters = new HashMap<String, Object>();
		parameters.put("brid", ll_id);
		List<Map<String, Object>> r3 = dao
				.doQuery(
						"SELECT MZHM as MZHM,BRXB as BRXB,CSNY  as CSNY From MS_BRDA Where BRID = :brid",
						parameters);
		ls_mzhm = valueOfString(r3.get(0).get("MZHM"));
		ll_brxb = valueOfString(r3.get(0).get("BRXB"));
		ldt_BirthDay = valueOfString(r3.get(0).get("CSNY"));
		// SELECT Sum(HJJE) Into :ld_hjje From MS_YJ02 Where YJXH = :ll_yjxh AND
		// JGID = :gl_jgid Group By YJXH
		parameters = new HashMap<String, Object>();
		parameters.put("yjxh", Long.parseLong(yjxh));
		parameters.put("jgid", jgid);
		ld_hjje = dao.doCount("MS_YJ02", " YJXH = :yjxh AND JGID = :jgid ",
				parameters); // 项目合计

		if (IsNull(ls_ysdm)) {
			ls_ysdm = "";
		}
		if (IsNull(ls_zxys)) {
			ls_zxys = czgh;
		}
		if (IsNull(ll_ksdm)) {
			ll_ksdm = "0";
		}
		if (IsNull(ll_brxb) || ll_brxb.equals("0")) {
			ll_brxb = "1";
		}
		if (IsNull(ll_ksdm)) {
			ll_ksdm = "0";
		}
		if (IsNull(ls_Yjph)) {
			ls_Yjph = "";
		}
		// 空值处理完毕
		if (ll_brxb.equals("1")) {
			ls_brxb = "男";
		} else {
			ls_brxb = "女";
		}
		if (IsNull(ldt_BirthDay)) {
			ls_Age = "";
		} else {
			Date d1 = null;
			try {
				d1 = new SimpleDateFormat("yyyy-MM-dd").parse(ldt_BirthDay);
				Date d2 = new Date();
				long diff = d2.getTime() - d1.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				ls_Age = valueOfString(days / 365);
			} catch (ParseException e) {
			}
		}

		// 开始执行
		MapParameter mp = new MapParameter()
				.put("ll_yjxh", Long.parseLong(yjxh)).put("gl_jgid", jgid)
				// .put("czgh", czgh)
				.put("ls_zxys", ls_zxys).put("ldt_jfrq", jfrq);
		dao.doUpdate(
				"UPDATE MS_YJ01 Set ZXPB = 1 ,ZXRQ = :ldt_jfrq ,ZXYS = :ls_zxys Where YJXH = :ll_yjxh  AND JGID = :gl_jgid",
				mp);
		// INSERT INTO YJ_BG01(JGID,YJXH,MBXH,MZZY,TJHM,BRHM,BRXM,BRXB,
		// BRNL,SJYS,SJKS, JCYS, JCKS, JCRQ, XMXH, XMMC,HJJE, YQDH,ZDDM,YJPH)
		// VALUES(
		// gl_jgid,al_yjxh,ll_mbxh,1,ls_tjhmnow,'ls_mzhm,ls_brxm,'ls_brxb,ls_Age,ls_ysdm),ll_ksdm,ls_zxys,il_yjksdm,ldt_jfrq,ll_xmxh,ls_jcxm,ld_hjje,ls_yqdh,ls_zddm,ls_Yjph）

		MapParameter saveData = new MapParameter()
				.put("JGID", jgid)
				.put("YJXH", yjxh)
				.put("MBXH", null)
				// ll_mbxh
				.put("MZZY", "1").put("TJHM", null).put("BRHM", ls_mzhm)
				.put("BRXM", ls_brxm).put("BRXB", ls_brxb).put("BRNL", ls_Age)
				.put("SJYS", ls_ysdm).put("SJKS", ll_ksdm).put("JCKS", ll_ksdm)
				.put("JCYS", ls_zxys).put("JCKS", jcks)
				// il_yjksdm --当前科室
				.put("JCRQ", jfrq).put("XMXH", ll_xmxh).put("XMMC", ls_jcxm)
				.put("HJJE", ld_hjje).put("YQDH", null)// ls_yqdh
				.put("ZDDM", ls_zddm).put("YJPH", ls_Yjph);
		Object o = dao.doLoad(BSPHISEntryNames.YJ_BG01, Long.parseLong(yjxh));
		if (o != null) // 已存在医技报告
			dao.doRemove(Long.parseLong(yjxh), BSPHISEntryNames.YJ_BG01);
		dao.doSave("create", BSPHISEntryNames.YJ_BG01, saveData, true);

		Session sess = (Session) ctx.get(Context.DB_SESSION);
		sess.flush();
		// end
		return true;
	}

	/****************************** add by zhangyq ******************************************/
	public void doQueryMZZXPB(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		long YJXH = Long.parseLong(req.get("body") + "");
		try {
			Map<String, Object> MS_YJ01 = dao.doLoad(
					BSPHISEntryNames.MS_YJ01_CIC, YJXH);
			// Map<String,Object> parameters = new HashMap<String, Object>();
			// parameters.put("YJXH", YJXH);
			// Map<String,Object> CYPB =
			// dao.doLoad("SELECT a.CYPB as CYPB from ZY_BRRY a,YJ_ZY01 b Where b.YJXH = :YJXH and a.ZYH = b.ZYH",
			// parameters);
			// MS_YJ01.putAll(CYPB);
			if (MS_YJ01 == null) {
				throw new ModelDataOperationException("您选取的医技单无效,可能是信息已被删除!");
			}
			res.put("body", MS_YJ01);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	public void doQueryZYZXPB(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		long YJXH = Long.parseLong(req.get("body") + "");
		try {
			Map<String, Object> MS_YJ01 = dao.doLoad(BSPHISEntryNames.YJ_ZY01,
					YJXH);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YJXH", YJXH);
			Map<String, Object> CYPB = dao
					.doLoad("SELECT a.CYPB as CYPB from ZY_BRRY a,YJ_ZY01 b Where b.YJXH = :YJXH and a.ZYH = b.ZYH",
							parameters);
			MS_YJ01.putAll(CYPB);
			res.put("body", MS_YJ01);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	public void doQueryJCZXPB(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		long YJXH = Long.parseLong(req.get("body") + "");
		try {
			Map<String, Object> JC_YJ01 = dao.doLoad(BSPHISEntryNames.JC_YJ01,
					YJXH);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YJXH", YJXH);
			Map<String, Object> CYPB = dao
					.doLoad("SELECT a.CYPB as CYPB from JC_BRRY a,JC_YJ01 b Where b.YJXH = :YJXH and a.ZYH = b.ZYH",
							parameters);
			JC_YJ01.putAll(CYPB);
			res.put("body", JC_YJ01);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	public void doQueryByMZHM(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		try {
			if (req.containsKey("JZKH")) {
				String JZKH = req.get("JZKH") + "";
				Map<String, Object> MPI_CardNoparameters = new HashMap<String, Object>();
				MPI_CardNoparameters.put("JZKH", JZKH);
				Map<String, Object> MPI_CardNo = dao.doLoad(
						"select a.empiId as empiId from MPI_Card a where a.cardTypeCode="
								+ KLX + " and a.cardNo=:JZKH",
						MPI_CardNoparameters);
				if (MPI_CardNo != null) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("EMPIID", MPI_CardNo.get("empiId"));
					Map<String, Object> MS_BRDA = dao
							.doLoad("select a.BRID as BRID,a.BRXM as BRXM,a.BRXZ as BRXZ from MS_BRDA a where a.EMPIID = :EMPIID",
									parameters);
					SchemaUtil.setDictionaryMassageForForm(MS_BRDA,
							BSPHISEntryNames.YJ_ZYFORM);
					res.put("body", MS_BRDA);
				}
			} else {
				String MZHM = req.get("MZHM") + "";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("MZHM", MZHM);
				Map<String, Object> MS_BRDA = dao
						.doLoad("select a.BRID as BRID,a.BRXM as BRXM,a.BRXZ as BRXZ from MS_BRDA a where a.MZHM = :MZHM",
								parameters);
				SchemaUtil.setDictionaryMassageForForm(MS_BRDA,
						BSPHISEntryNames.YJ_ZYFORM);
				res.put("body", MS_BRDA);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	public void doQueryByZYH(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String ZYH = req.get("ZYHM").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYHM", Long.parseLong(ZYH));
			parameters.put("JGID", JGID);
			Map<String, Object> MS_BRDA = dao
					.doLoad("select distinct a.BRID as BRID,ZYH as ZYH ,ZYHM as ZYHM,a.BRXM as BRXM,a.BRXZ as BRXZ,a.BRCH as BRCH,BRBQ as BRBQ from ZY_BRRY a where cast(a.ZYHM as long) = :ZYHM  and a.JGID=:JGID",
							parameters);
			SchemaUtil.setDictionaryMassageForForm(MS_BRDA,
					BSPHISEntryNames.YJ_ZYFORM);
			res.put("body", MS_BRDA);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("body", null);
			// throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	public void doQueryByJCHM(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String ZYH = req.get("ZYHM").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYHM", Long.parseLong(ZYH));
			parameters.put("JGID", JGID);
			Map<String, Object> MS_BRDA = dao
					.doLoad("select distinct a.BRID as BRID,a.ZYH as ZYH ,a.ZYHM as ZYHM,a.BRXM as BRXM,a.BRXZ as BRXZ,a.CYPB as CYPB from JC_BRRY a where cast(a.ZYHM as long) = :ZYHM  and a.JGID=:JGID",
							parameters);
			SchemaUtil.setDictionaryMassageForForm(MS_BRDA,
					BSPHISEntryNames.JC_YJFORM);
			res.put("body", MS_BRDA);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("body", null);
			// throw new ModelDataOperationException("查询是否执行失败！", e);
		}
	}

	/****************************** add by zhangyq ******************************************/
	public static boolean IsNull(Object s) {

		return s == null || s.toString().equals("")
				|| s.toString().toUpperCase().equals("NULL");
	}

	/**
	 * 住院医技单执行
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 * @throws ServiceException
	 */
	public boolean execute_zyyj(Map<String, Object> res,
			Map<String, Object> zy_params, Context ctx)
			throws PersistentDataOperationException,
			ModelDataOperationException, ServiceException, ParseException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Date ldt_jfrq = new Date(); // 当前时间
		String ls_qrgh = (String) user.getUserId(); // 操作工号
		String ll_yjxh = valueOfString(zy_params.get("YJXH"));
		String jgid = valueOfString(zy_params.get("JGID"));
		String ls_zddm = valueOfString(zy_params.get("ZDID"));
		String as_zxys = valueOfString(zy_params.get("ZXYS"));
		String zxks = valueOfString(zy_params.get("ZXKS"));
		// List<Map<String,Object>> zy_xmlist = (List<Map<String, Object>>)
		// zy_params.get("ZYXMLIST");
		String ls_tjhm, ls_zyhm, ls_brid,ls_brxm,ll_brxz,ls_ysgh, ll_fyks, ls_ysdm, ls_srgh, ll_zyh, ls_SpecimenId, ls_Yjph, ll_fybq, ls_brxb, ldt_BirthDay, ll_cypb, ll_jscs, ll_zlxz, // 诊疗小组
		ls_Age, // 变量声明
		ls_tjsj = "";// 2013-06-19 gejj 增加提交时间变量

		ls_tjhm = ls_zyhm = ls_brxm = ll_fyks = ls_ysdm = ls_srgh = ll_zyh = ls_SpecimenId = ls_Yjph = ll_fybq = ls_brxb = ldt_BirthDay = ll_cypb = ll_jscs = ll_zlxz = ls_Age = "";// 变量初始值

//		List<Map<String, Object>> r1 = dao
//				.doQuery(
//						"SELECT ZXPB as ZXPB From YJ_ZY01 Where YJXH = :ll_yjxh And JGID = :gl_jgid ",
//						new MapParameter().put("ll_yjxh",
//								Long.parseLong(ll_yjxh)).put("gl_jgid",
//								Long.parseLong(jgid)));
//		if (r1.size() > 0) {
//			Map<String, Object> r1Map = r1.get(0);
//			if (r1Map.get("ZXPB") != null && r1Map.get("ZXPB").equals("1")) {
//				res.put("errorCode", 1);//
//				res.put("errorMsg", "该检查单已被他人执行,不能再被执行！");
//				return false;
//			}
//		} else {
//			res.put("errorCode", 1);//
//			res.put("errorMsg", "该检查单已被他人删除,不能再被执行！");
//			return false;
//		}

		// SELECT TJHM, ZYHM, BRXM, KSls_ysdm,ls_srgh,ll_zyh,ls_SpecimenId,
		// :ls_Yjph,ll_fybq
		// From YJ_ZY01 Where YJXH = :ll_yjxh And JGID = :gl_jgid
		// ldt_fyrq = 当前时间DM, YSDM, HJGH, ZYH, BBBM, YJPH, FYBQ
		// INTO :ls_tjhm,ls_zyhm,ls_brxm,ll_fyks,

		/**
		 * 2013-06-19 gejj 查询结果中添加提交时间(TJSJ)记录 ，改字段用于往住院病区医嘱表中的确认时间
		 * */
		List<Map<String, Object>> r2 = dao
				.doQuery(
						"SELECT ZXPB as ZXPB,TJHM as TJHM,ZYHM as ZYHM,BRXM as BRXM,KSDM as KSDM,YSDM as YSDM,HJGH as HJGH,ZYH as ZYH,BBBM as BBBM,YJPH as YJPH,FYBQ as FYBQ"
								+ " , TJSJ as TJSJ From YJ_ZY01 Where YJXH = :ll_yjxh  And JGID = :gl_jgid",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid",
								Long.parseLong(jgid)));
		if (r2.size() > 0) {
			Map<String, Object> r2Map = r2.get(0);
			if (r2Map.get("ZXPB") != null && r2Map.get("ZXPB").equals("1")) {
				res.put("errorCode", 1);//
				res.put("errorMsg", "该检查单已被他人执行,不能再被执行！");
				return false;
			}
			ls_tjhm = valueOfString(r2.get(0).get("TJHM"));
			ls_zyhm = valueOfString(r2.get(0).get("ZYHM"));
			ls_brxm = valueOfString(r2.get(0).get("BRXM"));
			ll_fyks = valueOfString(r2.get(0).get("KSDM"));
			ls_ysdm = valueOfString(r2.get(0).get("YSDM"));
			ls_srgh = valueOfString(r2.get(0).get("HJGH"));
			ll_zyh = valueOfString(r2.get(0).get("ZYH"));
			ls_SpecimenId = valueOfString(r2.get(0).get("BBBM"));
			ls_Yjph = valueOfString(r2.get(0).get("YJPH"));
			ll_fybq = valueOfString(r2.get(0).get("FYBQ"));
			ls_tjsj = valueOfString(r2.get(0).get("TJSJ"));
		} else {
			res.put("errorCode", 1);//
			res.put("errorMsg", "该检查单已被他人删除,不能再被执行！");
			return false;
		}
		// SELECT BRXB,CSNY,CYPB,JSCS,ZLXZ
		// INTO :ll_brxb,:ldt_BirthDay,:ll_cypb,:ll_jscs,:s_hispublic_fymx.zlxz
		// From ZY_BRRY Where ZY_BRRY.ZYHM = :ls_zyhm And ZYH = :ll_zyh And JGID
		// = :gl_jgid ;
		// 查询住院病人基本信息
		List<Map<String, Object>> r3 = dao
				.doQuery(
						"SELECT BRID as BRID,ZSYS as YSGH,BRXZ as BRXZ,BRXB as BRXB,CSNY as CSNY,CYPB as CYPB,JSCS as JSCS,ZLXZ as ZLXZ"
								+ " From  ZY_BRRY as ZY_BRRY Where ZY_BRRY.ZYHM = :ls_zyhm And ZYH = :ll_zyh  And JGID = :gl_jgid ",
						new MapParameter().put("ls_zyhm", ls_zyhm)
								.put("ll_zyh", Long.parseLong(ll_zyh))
								.put("gl_jgid", jgid));
		if (r3.size() > 0) {
			ls_brid = valueOfString(r3.get(0).get("BRID"));
			ls_ysgh = valueOfString(r3.get(0).get("YSGH"));
			ll_brxz = valueOfString(r3.get(0).get("BRXZ"));
			ls_brxb = valueOfString(r3.get(0).get("BRXB"));
			ldt_BirthDay = valueOfString(r3.get(0).get("CSNY"));
			ll_cypb = valueOfString(r3.get(0).get("CYPB"));
			if (Integer.parseInt(ll_cypb) >= 8) {
				res.put("errorCode", 1);//
				res.put("errorMsg", "该病人已出院! ");
				return false;
			}
			ll_jscs = valueOfString(r3.get(0).get("JSCS"));
			ll_zlxz = valueOfString(r3.get(0).get("ZLXZ"));
		} else {
			res.put("errorCode", 1);//
			res.put("errorMsg", "已出院,不能再执行检查单!");
			return false;
		}

		// IF IsNull(ll_fybq) Or ll_fybq = 0 THEN
		// MessageBox("系统提示", "该病人在病区没有分配床位,请先到病区去!")
		// RETURN False
		// END IF
		if (IsNull(ll_fybq) || ll_fybq.equals("0")) {
			res.put("errorCode", 1);//
			res.put("errorMsg", "该病人在病区没有分配床位,请先到病区去!");
			return false;
		}

		if (ls_brxb.equals("1")) {
			ls_brxb = "男";
		} else {
			ls_brxb = "女";
		}
		if (IsNull(ldt_BirthDay)) {
			ls_Age = "";
		} else {
			Date d1 = null;
			try {
				d1 = new SimpleDateFormat("yyyy-MM-dd").parse(ldt_BirthDay);
				Date d2 = new Date();
				long diff = d2.getTime() - d1.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				ls_Age = valueOfString(days / 365);
			} catch (ParseException e) {
			}
		}
		// IF IsNull(as_zxys) THEN as_zxys = 操作工号
		// IF IsNull(ll_fyks) THEN ll_fyks = 0
		// If (IsNull(ll_brxb))Or(ll_brxb = 0) THEN ll_brxb = 3
		// //如果性别代码为空或0，则打上未知
		// IF IsNull(ls_brxm) THEN ls_brxm = ''
		// IF IsNull(ll_fybq) THEN ll_fybq = 0
		// IF IsNull(ls_Yjph) THEN ls_Yjph = ""
		if (IsNull(as_zxys)) {
			as_zxys = ls_qrgh;
		} // 操作工号
		if (IsNull(ll_fyks)) {
			ll_fyks = "0";
		}
		if (IsNull(ls_brxb) || ls_brxb.equals("0")) {
			ls_brxb = "3";
		}// 如果性别代码为空或0，则打上未知
		if (IsNull(ls_brxm)) {
			ls_brxm = "";
		}
		if (IsNull(ll_fybq)) {
			ll_fybq = "0";
		}
		if (IsNull(ls_Yjph)) {
			ls_Yjph = "";
		}
		//
		if (ls_zddm.equals("-1")) {
			res.put("errorCode", 1);//
			res.put("errorMsg", "选择医技诊断结果失败!");
			return false;
		}
		// 住院项目取消执行检索数据，参数：ll_yjxh
		String ll_ylxh, // 医技序号
		ld_yldj, // 医疗单价
		ll_fygb, // 费用归并
		ld_zfbl, // 自负比例
		ll_yzxh, // 医嘱序号
		ll_yepb, // 婴儿唯一号
//		ll_brxz, // 病人性质
		ls_fymc, ls_fydw, ld_ylsl, ls_jcxm, ll_xmxh, ld_hjje;
		ll_ylxh = ld_yldj = ll_fygb = ld_zfbl = ll_yzxh = ll_yepb = ls_fymc = ld_hjje = ls_jcxm = ll_xmxh = ls_fydw = ld_ylsl = new String();
		List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listFymx = new ArrayList<Map<String, Object>>();

		Map<String, Object> mPer = new HashMap<String, Object>();
		mPer.put("YJXH", Long.parseLong(zy_params.get("YJXH").toString()));
		mPer.put("JGID", Long.parseLong(jgid));
		List<Map<String, Object>> zy_xmlist = dao
				.doQuery(
						"select b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,a.YZXH as YZXH,a.YLXH as YLXH,b.FYDW as FYDW,a.FYGB as FYGB,a.YEPB as YEPB,a.YLSL * a.YLDJ as YLJE "
								+ " from YJ_ZY02 a, GY_YLSF b where a.YLXH = b.FYXH and a.YJXH =:YJXH and a.JGID =:JGID",
						mPer);

		for (Map<String, Object> xmMap : zy_xmlist) {
			ll_ylxh = valueOfString(xmMap.get("YLXH"));
			ld_yldj = valueOfString(xmMap.get("YLDJ"));
			ld_ylsl = valueOfString(xmMap.get("YLSL"));
			ll_fygb = valueOfString(xmMap.get("FYGB"));
			ld_zfbl = valueOfString(xmMap.get("ZFBL"));
			ll_yzxh = valueOfString(xmMap.get("YZXH"));
			ll_yepb = valueOfString(xmMap.get("YEPB"));
			// 取出该项目的项目名称和单位
			// SELECT FYMC ,FYDW Into :ls_fymc,:ls_fydw From GY_YLSF Where FYXH
			// = :ll_ylxh ;
			List<Map<String, Object>> r4 = dao
					.doQuery(
							"SELECT FYMC as FYMC,FYDW as FYDW  From GY_YLSF Where FYXH = :ll_ylxh",
							new MapParameter().put("ll_ylxh",
									Long.parseLong(ll_ylxh)));
			if (r4.size() > 0) {
				ls_fymc = valueOfString(r4.get(0).get("FYMC"));
				ls_fydw = valueOfString(r4.get(0).get("FYDW"));
			}
			// 加入性质 不加导致超限计算费用有问题
//			List<Map<String, Object>> r5 = dao
//					.doQuery(
//							"SELECT BRXZ as BRXZ From ZY_BRRY Where ZYH = :ll_zyh And JGID = :gl_jgid",
//							new MapParameter().put("ll_zyh",
//									Long.parseLong(ll_zyh))
//									.put("gl_jgid", jgid));
//			if (r5.size() > 0) {
//				ll_brxz = valueOfString(r5.get(0).get("BRXZ"));
//			}
			MapParameter mpFYMX = new MapParameter()
					.put("FYMC", ls_fymc)
					.put("FYDW", ls_fydw)
					.put("JLXH", ll_yzxh)
					// 医嘱需要公共类中 使用该字段作为医嘱序号,详细设计里面'0'
					.put("ZYH", ll_zyh).put("BRXZ", ll_brxz)
					.put("FYRQ", ldt_jfrq).put("FYXH", ll_ylxh)
					.put("FYSL", ld_ylsl).put("FYDJ", ld_yldj)
					.put("YSGH", ls_ysdm).put("SRGH", ls_srgh)
					.put("QRGH", ls_qrgh).put("FYKS", ll_fyks)
					.put("ZXKS", zxks).put("JFRQ", ldt_jfrq).put("XMLX", 3)
					.put("FYXM", ll_fygb).put("ZFBL", ld_zfbl).put("YPLX", 0)
					.put("YPCD", 0).put("FYBQ", ll_fybq).put("YZXH", ll_yzxh);
			if (ls_ysgh != null) {
				mpFYMX.put("YSGH", ls_ysgh);
			}
			listFymx.add(mpFYMX);
		}
		BSPHISUtil.uf_insert_fymx(listFymx, listForputFYMX, dao, ctx);// 费用项目插入
		BSPHISUtil.uf_update_fymx(listForputFYMX, "create",
				BSPHISEntryNames.ZY_FYMX, true, dao);
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
				dao);
		Map<String, Object> map_brxx = new HashMap<String, Object>();
		map_brxx.put("ZYHM", ls_zyhm);
		map_brxx.put("BRXM", ls_brxm);
		map_brxx.put("BRID", ls_brid);
		Map<String, Object> r = mmd.saveAllXhmx(listForputFYMX, 1, ctx,map_brxx);
		res.put("body", r);
		// 取出医技主项
		List<Map<String, Object>> r6 = dao
				.doQuery(
						"SELECT GY_YLSF.FYMC as FYMC,GY_YLSF.FYXH  as FYXH FROM YJ_ZY01 YJ_ZY01,YJ_ZY02 YJ_ZY02,GY_YLSF GY_YLSF WHERE YJ_ZY01.YJXH = YJ_ZY02.YJXH"
								+ " And YJ_ZY02.YJZX = 1 And YJ_ZY02.YLXH = GY_YLSF.FYXH And YJ_ZY01.YJXH = :ll_yjxh And YJ_ZY01.JGID = :gl_jgid  And YJ_ZY02.JGID = :gl_jgid",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid",
								Long.parseLong(jgid)));
		if (r6.size() > 0) {
			ls_jcxm = valueOfString(r6.get(0).get("FYMC"));
			ll_xmxh = valueOfString(r6.get(0).get("FYXH"));
		}
		// 取出检查单的合计金额
		List<Map<String, Object>> r7 = dao
				.doQuery(
						" SELECT Sum(YLSL*YLDJ)  as HJJE From YJ_ZY02 Where YJXH = :ll_yjxh And JGID = :gl_jgid Group By YJXH",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid",
								Long.parseLong(jgid)));
		if (r7.size() > 0) {
			ld_hjje = valueOfString(r7.get(0).get("HJJE"));
		}
		if (IsNull(ls_ysdm)) {
			ls_ysdm = "";
		}
		// 更新医技单
		dao.doUpdate(
				"UPDATE YJ_ZY01 Set ZXPB = 1 ,ZXRQ = :ldt_fyrq ,ZXYS = :as_zxys Where YJXH = :ll_yjxh   And JGID = :gl_jgid",
				new MapParameter().put("ldt_fyrq", ldt_jfrq)
						.put("as_zxys", as_zxys)
						.put("ll_yjxh", Long.parseLong(ll_yjxh))
						.put("gl_jgid", Long.parseLong(jgid)));
		// 遍历住院项目取消执行数据,ll_row是遍历序号
		for (Map<String, Object> xmMap : zy_xmlist) {
			ll_yzxh = valueOfString(xmMap.get("YZXH"));
			// IF Not(ll_yzxh = 0 Or IsNull(ll_yzxh)) THEN //如果为病区输入的检查单
			// //打上医技序号、确认时间、使用标志
			// UPDATE ZY_BQYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0
			// Where JLXH = :ll_yzxh And JGID = :gl_jgid ;
			// End if

			if (!(IsNull(ll_yzxh) || ll_yzxh.equals("0"))) {
				int wpjfbz = mmd.queryWPJFBZ(ctx);
				int wzsfxmjgzy = Integer.parseInt("".equals(ParameterUtil
						.getParameter(jgid, BSPHISSystemArgument.WZSFXMJGZY,
								ctx)) ? "0" : ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
				/**
				 * 2013-06-19 gejj 更新住院病区时，使用yj_zy01表中的提交时间 +1天 并取0点
				 * */
				SimpleDateFormat sdfdatetime = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				ls_tjsj = BSHISUtil.toString(sdfdatetime.parse(ls_tjsj),
						"yyyy-MM-dd");
				ls_tjsj = ls_tjsj + " 00:00:00";
				Date qrsj = BSHISUtil.getDateAfter(sdfdatetime.parse(ls_tjsj),
						1);
				MapParameter ZY_BQYZP = new MapParameter()
						.put("ldt_fyrq", qrsj)
						.put("ll_yjxh", Long.parseLong(ll_yjxh))
						.put("ll_yzxh", Long.parseLong(ll_yzxh))
						.put("gl_jgid", jgid);
				dao.doUpdate(
						"UPDATE ZY_BQYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0,LSBZ=1 Where JLXH = :ll_yzxh  And JGID = :gl_jgid and LSYZ = 1",
						ZY_BQYZP);
				dao.doUpdate(
						"UPDATE ZY_BQYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0 Where JLXH = :ll_yzxh   And JGID = :gl_jgid and LSYZ = 0",
						ZY_BQYZP);
				if (wpjfbz == 1 && wzsfxmjgzy == 1) {
					BSPHISUtil.updateXHMXZT(dao, Long.parseLong(ll_yzxh), "2",
							jgid);
				}
			}
		}
		// INSERT INTO YJ_BG01(JGID, YJXH, MBXH,MZZY, TJHM, BRHM, )
		// VALUES (gl_jgid,al_yjxh,ll_mbxh, 2,ls_tjhmnow,ls_zyhm,
		// BRXM, BRXB, BRNL, SJYS, SJKS, JCYS, JCKS, JCRQ, XMXH, XMMC, HJJE,
		// YQDH, ZDDM, BBBM, YJPH, ZYH
		// ls_brxm,ls_brxb,
		// ls_Age,ls_ysdm,ll_fyks,as_zxys,lu_yjks.il_yjksdm,ldt_jfrq,ll_xmxh,ls_jcxm,
		// ld_hjje,ls_yqdh, ls_zddm , ls_SpecimenId , ls_Yjph ,ll_zyh)

		// 生成医技报告单
		MapParameter saveData = new MapParameter()
				.put("JGID", jgid)
				.put("YJXH", ll_yjxh)
				.put("MBXH", null)
				.put("MZZY", 2)
				// 模板序号
				.put("TJHM", null).put("BRHM", ls_zyhm).put("BRXM", ls_brxm)
				.put("BRXB", ls_brxb).put("BRNL", ls_Age).put("SJYS", ls_ysdm)
				.put("SJKS", ll_fyks).put("JCYS", as_zxys).put("JCKS", zxks)
				.put("JCRQ", ldt_jfrq).put("XMXH", ll_xmxh)
				.put("XMMC", ls_jcxm).put("HJJE", ld_hjje).put("YQDH", null)
				// 仪器代码
				.put("ZDDM", ls_zddm).put("BBBM", ls_SpecimenId)
				.put("YJPH", ls_Yjph).put("ZYH", ll_zyh);
		Object o = dao
				.doLoad(BSPHISEntryNames.YJ_BG01, Long.parseLong(ll_yjxh));
		if (o != null) // 已存在医技报告
			dao.doRemove(Long.parseLong(ll_yjxh), BSPHISEntryNames.YJ_BG01);
		dao.doSave("create", BSPHISEntryNames.YJ_BG01, saveData, true);

		Session sess = (Session) ctx.get(Context.DB_SESSION);
		sess.flush();

		return true;
	}

	/**
	 * 住院医技单执行
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 * @throws ParseException
	 * @throws ServiceException
	 */
	public boolean execute_jcyj(Map<String, Object> res,
			Map<String, Object> zy_params, Context ctx)
			throws PersistentDataOperationException,
			ModelDataOperationException, ServiceException, ParseException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Date ldt_jfrq = new Date(); // 当前时间
		String ls_qrgh = (String) user.getUserId(); // 操作工号
		String ll_yjxh = valueOfString(zy_params.get("YJXH"));
		String jgid = valueOfString(zy_params.get("JGID"));
		String ls_zddm = valueOfString(zy_params.get("ZDID"));
		String as_zxys = valueOfString(zy_params.get("ZXYS"));
		String zxks = valueOfString(zy_params.get("ZXKS"));
		// List<Map<String,Object>> zy_xmlist = (List<Map<String, Object>>)
		// zy_params.get("ZYXMLIST");
		String ls_tjhm, ls_zyhm, ls_brxm, ll_fyks, ls_ysdm, ls_srgh, ll_zyh, ls_SpecimenId, ls_Yjph, ll_fybq, ls_brxb, ldt_BirthDay, ll_cypb, ll_jscs, ll_zlxz, // 诊疗小组
		ls_Age, // 变量声明
		ls_tjsj = "";// 2013-06-19 gejj 增加提交时间变量

		ls_tjhm = ls_zyhm = ls_brxm = ll_fyks = ls_ysdm = ls_srgh = ll_zyh = ls_SpecimenId = ls_Yjph = ll_fybq = ls_brxb = ldt_BirthDay = ll_cypb = ll_jscs = ll_zlxz = ls_Age = "";// 变量初始值

		List<Map<String, Object>> r1 = dao
				.doQuery(
						"SELECT ZXPB as ZXPB From JC_YJ01 Where YJXH = :ll_yjxh And JGID = :gl_jgid ",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid", jgid));
		if (r1.size() > 0) {
			Map<String, Object> r1Map = r1.get(0);
			if (r1Map.get("ZXPB") != null && r1Map.get("ZXPB").equals("1")) {
				res.put("errorCode", 1);//
				res.put("errorMsg", "该检查单已被他人执行,不能再被执行！");
				return false;
			}
		} else {
			res.put("errorCode", 1);//
			res.put("errorMsg", "该检查单已被他人删除,不能再被执行！");
			return false;
		}

		// SELECT TJHM, ZYHM, BRXM, KSls_ysdm,ls_srgh,ll_zyh,ls_SpecimenId,
		// :ls_Yjph,ll_fybq
		// From YJ_ZY01 Where YJXH = :ll_yjxh And JGID = :gl_jgid
		// ldt_fyrq = 当前时间DM, YSDM, HJGH, ZYH, BBBM, YJPH, FYBQ
		// INTO :ls_tjhm,ls_zyhm,ls_brxm,ll_fyks,

		/**
		 * 2013-06-19 gejj 查询结果中添加提交时间(TJSJ)记录 ，改字段用于往住院病区医嘱表中的确认时间
		 * */
		List<Map<String, Object>> r2 = dao
				.doQuery(
						"SELECT TJHM as TJHM,ZYHM as ZYHM,BRXM as BRXM,KSDM as KSDM,YSDM as YSDM,HJGH as HJGH,ZYH as ZYH,BBBM as BBBM,YJPH as YJPH,FYBQ as FYBQ"
								+ " , TJSJ as TJSJ From JC_YJ01 Where YJXH = :ll_yjxh  And JGID = :gl_jgid",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid", jgid));
		if (r2.size() > 0) {
			ls_tjhm = valueOfString(r2.get(0).get("TJHM"));
			ls_zyhm = valueOfString(r2.get(0).get("ZYHM"));
			ls_brxm = valueOfString(r2.get(0).get("BRXM"));
			ll_fyks = valueOfString(r2.get(0).get("KSDM"));
			ls_ysdm = valueOfString(r2.get(0).get("YSDM"));
			ls_srgh = valueOfString(r2.get(0).get("HJGH"));
			ll_zyh = valueOfString(r2.get(0).get("ZYH"));
			ls_SpecimenId = valueOfString(r2.get(0).get("BBBM"));
			ls_Yjph = valueOfString(r2.get(0).get("YJPH"));
			ll_fybq = valueOfString(r2.get(0).get("FYBQ"));
			ls_tjsj = valueOfString(r2.get(0).get("TJSJ"));
		}
		// SELECT BRXB,CSNY,CYPB,JSCS,ZLXZ
		// INTO :ll_brxb,:ldt_BirthDay,:ll_cypb,:ll_jscs,:s_hispublic_fymx.zlxz
		// From ZY_BRRY Where ZY_BRRY.ZYHM = :ls_zyhm And ZYH = :ll_zyh And JGID
		// = :gl_jgid ;
		// 查询住院病人基本信息
		List<Map<String, Object>> r3 = dao
				.doQuery(
						"SELECT BRXB as BRXB,CSNY as CSNY,CYPB as CYPB,JSCS as JSCS"// ZLXZ
																					// as
																					// ZLXZ"
								+ " From  JC_BRRY as JC_BRRY Where JC_BRRY.ZYHM = :ls_zyhm And ZYH = :ll_zyh  And JGID = :gl_jgid ",
						new MapParameter().put("ls_zyhm", ls_zyhm)
								.put("ll_zyh", Long.parseLong(ll_zyh))
								.put("gl_jgid", jgid));
		if (r3.size() > 0) {
			ls_brxb = valueOfString(r3.get(0).get("BRXB"));
			ldt_BirthDay = valueOfString(r3.get(0).get("CSNY"));
			ll_cypb = valueOfString(r3.get(0).get("CYPB"));
			ll_jscs = valueOfString(r3.get(0).get("JSCS"));
			// ll_zlxz = valueOfString(r3.get(0).get("ZLXZ"));
		} else {
			res.put("errorCode", 1);//
			res.put("errorMsg", "已出院,不能再执行检查单!");
			return false;
		}

		// IF IsNull(ll_fybq) Or ll_fybq = 0 THEN
		// MessageBox("系统提示", "该病人在病区没有分配床位,请先到病区去!")
		// RETURN False
		// END IF
		// if (IsNull(ll_fybq) || ll_fybq.equals("0")) {
		// res.put("errorCode", 1);//
		// res.put("errorMsg", "该病人在病区没有分配床位,请先到病区去!");
		// return false;
		// }

		if (ls_brxb.equals("1")) {
			ls_brxb = "男";
		} else {
			ls_brxb = "女";
		}
		if (IsNull(ldt_BirthDay)) {
			ls_Age = "";
		} else {
			Date d1 = null;
			try {
				d1 = new SimpleDateFormat("yyyy-MM-dd").parse(ldt_BirthDay);
				Date d2 = new Date();
				long diff = d2.getTime() - d1.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				ls_Age = valueOfString(days / 365);
			} catch (ParseException e) {
			}
		}
		// IF IsNull(as_zxys) THEN as_zxys = 操作工号
		// IF IsNull(ll_fyks) THEN ll_fyks = 0
		// If (IsNull(ll_brxb))Or(ll_brxb = 0) THEN ll_brxb = 3
		// //如果性别代码为空或0，则打上未知
		// IF IsNull(ls_brxm) THEN ls_brxm = ''
		// IF IsNull(ll_fybq) THEN ll_fybq = 0
		// IF IsNull(ls_Yjph) THEN ls_Yjph = ""
		if (IsNull(as_zxys)) {
			as_zxys = ls_qrgh;
		} // 操作工号
		if (IsNull(ll_fyks)) {
			ll_fyks = "0";
		}
		if (IsNull(ls_brxb) || ls_brxb.equals("0")) {
			ls_brxb = "3";
		}// 如果性别代码为空或0，则打上未知
		if (IsNull(ls_brxm)) {
			ls_brxm = "";
		}
		if (IsNull(ll_fybq)) {
			ll_fybq = "0";
		}
		if (IsNull(ls_Yjph)) {
			ls_Yjph = "";
		}
		//
		if (ls_zddm.equals("-1")) {
			res.put("errorCode", 1);//
			res.put("errorMsg", "选择医技诊断结果失败!");
			return false;
		}
		// 住院项目取消执行检索数据，参数：ll_yjxh
		String ll_ylxh, // 医技序号
		ld_yldj, // 医疗单价
		ll_fygb, // 费用归并
		ld_zfbl, // 自负比例
		ll_yzxh, // 医嘱序号
		ll_yepb, // 婴儿唯一号
		ll_brxz, // 病人性质
		ls_fymc, ls_fydw, ld_ylsl, ls_jcxm, ll_xmxh, ld_hjje;
		ll_ylxh = ld_yldj = ll_fygb = ld_zfbl = ll_yzxh = ll_yepb = ll_brxz = ls_fymc = ld_hjje = ls_jcxm = ll_xmxh = ls_fydw = ld_ylsl = new String();
		List<Map<String, Object>> listForputFYMX = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listFymx = new ArrayList<Map<String, Object>>();

		Map<String, Object> mPer = new HashMap<String, Object>();
		mPer.put("YJXH", Long.parseLong(zy_params.get("YJXH").toString()));
		mPer.put("JGID", jgid);
		List<Map<String, Object>> zy_xmlist = dao
				.doQuery(
						"select b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,a.YZXH as YZXH,a.YLXH as YLXH,b.FYDW as FYDW,a.FYGB as FYGB,a.YEPB as YEPB,a.YLSL * a.YLDJ as YLJE "
								+ " from JC_YJ02 a, GY_YLSF b where a.YLXH = b.FYXH and a.YJXH =:YJXH and a.JGID =:JGID",
						mPer);

		for (Map<String, Object> xmMap : zy_xmlist) {
			ll_ylxh = valueOfString(xmMap.get("YLXH"));
			ld_yldj = valueOfString(xmMap.get("YLDJ"));
			ld_ylsl = valueOfString(xmMap.get("YLSL"));
			ll_fygb = valueOfString(xmMap.get("FYGB"));
			ld_zfbl = valueOfString(xmMap.get("ZFBL"));
			ll_yzxh = valueOfString(xmMap.get("YZXH"));
			ll_yepb = valueOfString(xmMap.get("YEPB"));
			// 取出该项目的项目名称和单位
			// SELECT FYMC ,FYDW Into :ls_fymc,:ls_fydw From GY_YLSF Where FYXH
			// = :ll_ylxh ;
			List<Map<String, Object>> r4 = dao
					.doQuery(
							"SELECT FYMC as FYMC,FYDW as FYDW  From GY_YLSF Where FYXH = :ll_ylxh",
							new MapParameter().put("ll_ylxh",
									Long.parseLong(ll_ylxh)));
			if (r4.size() > 0) {
				ls_fymc = valueOfString(r4.get(0).get("FYMC"));
				ls_fydw = valueOfString(r4.get(0).get("FYDW"));
			}
			// 加入性质 不加导致超限计算费用有问题
			List<Map<String, Object>> r5 = dao
					.doQuery(
							"SELECT BRXZ as BRXZ From JC_BRRY Where ZYH = :ll_zyh And JGID = :gl_jgid",
							new MapParameter().put("ll_zyh",
									Long.parseLong(ll_zyh))
									.put("gl_jgid", jgid));
			if (r5.size() > 0) {
				ll_brxz = valueOfString(r5.get(0).get("BRXZ"));
			}
			MapParameter mpFYMX = new MapParameter()
					.put("FYMC", ls_fymc)
					.put("FYDW", ls_fydw)
					.put("JLXH", ll_yzxh)
					// 医嘱需要公共类中 使用该字段作为医嘱序号,详细设计里面'0'
					.put("ZYH", ll_zyh).put("BRXZ", ll_brxz)
					.put("FYRQ", ldt_jfrq).put("FYXH", ll_ylxh)
					.put("FYSL", ld_ylsl).put("FYDJ", ld_yldj)
					.put("YSGH", ls_ysdm).put("SRGH", ls_srgh)
					.put("QRGH", ls_qrgh).put("FYKS", ll_fyks)
					.put("ZXKS", zxks).put("JFRQ", ldt_jfrq).put("XMLX", 3)
					.put("FYXM", ll_fygb).put("ZFBL", ld_zfbl).put("YPLX", 0)
					.put("YPCD", 0).put("FYBQ", ll_fybq).put("YZXH", ll_yzxh);
			listFymx.add(mpFYMX);
		}
		BSPHISUtil.uf_insert_jc_fymx(listFymx, listForputFYMX, dao, ctx);// 费用项目插入
		BSPHISUtil.uf_update_jc_fymx(listForputFYMX, "create",
				BSPHISEntryNames.JC_FYMX, true, dao);
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
				dao);
		Map<String, Object> r = mmd.save_jcAllXhmx(listForputFYMX, 1, ctx);
		res.put("body", r);
		// 取出医技主项
		List<Map<String, Object>> r6 = dao
				.doQuery(
						"SELECT GY_YLSF.FYMC as FYMC,GY_YLSF.FYXH  as FYXH FROM JC_YJ01 JC_YJ01,JC_YJ02 JC_YJ02,GY_YLSF GY_YLSF WHERE JC_YJ01.YJXH = JC_YJ02.YJXH"
								+ " And JC_YJ02.YJZX = 1 And JC_YJ02.YLXH = GY_YLSF.FYXH And JC_YJ01.YJXH = :ll_yjxh And JC_YJ01.JGID = :gl_jgid  And JC_YJ02.JGID = :gl_jgid",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid", jgid));
		if (r6.size() > 0) {
			ls_jcxm = valueOfString(r6.get(0).get("FYMC"));
			ll_xmxh = valueOfString(r6.get(0).get("FYXH"));
		}
		// 取出检查单的合计金额
		List<Map<String, Object>> r7 = dao
				.doQuery(
						" SELECT Sum(YLSL*YLDJ)  as HJJE From JC_YJ02 Where YJXH = :ll_yjxh And JGID = :gl_jgid Group By YJXH",
						new MapParameter().put("ll_yjxh",
								Long.parseLong(ll_yjxh)).put("gl_jgid", jgid));
		if (r7.size() > 0) {
			ld_hjje = valueOfString(r7.get(0).get("HJJE"));
		}
		if (IsNull(ls_ysdm)) {
			ls_ysdm = "";
		}
		// 更新医技单
		dao.doUpdate(
				"UPDATE JC_YJ01 Set ZXPB = 1 ,ZXRQ = :ldt_fyrq ,ZXYS = :as_zxys Where YJXH = :ll_yjxh And JGID = :gl_jgid",
				new MapParameter().put("ldt_fyrq", ldt_jfrq)
						.put("as_zxys", as_zxys)
						.put("ll_yjxh", Long.parseLong(ll_yjxh))
						.put("gl_jgid", jgid));
		// 遍历住院项目取消执行数据,ll_row是遍历序号
		for (Map<String, Object> xmMap : zy_xmlist) {
			ll_yzxh = valueOfString(xmMap.get("YZXH"));
			// IF Not(ll_yzxh = 0 Or IsNull(ll_yzxh)) THEN //如果为病区输入的检查单
			// //打上医技序号、确认时间、使用标志
			// UPDATE ZY_BQYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0
			// Where JLXH = :ll_yzxh And JGID = :gl_jgid ;
			// End if

			if (!(IsNull(ll_yzxh) || ll_yzxh.equals("0"))) {
				int wpjfbz = mmd.queryWPJFBZ(ctx);
				int wzsfxmjgzy = Integer.parseInt("".equals(ParameterUtil
						.getParameter(jgid, BSPHISSystemArgument.WZSFXMJGZY,
								ctx)) ? "0" : ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJGZY, ctx));
				/**
				 * 2013-06-19 gejj 更新住院病区时，使用yj_zy01表中的提交时间 +1天 并取0点
				 * */
				SimpleDateFormat sdfdatetime = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				ls_tjsj = BSHISUtil.toString(sdfdatetime.parse(ls_tjsj),
						"yyyy-MM-dd");
				ls_tjsj = ls_tjsj + " 00:00:00";
				Date qrsj = BSHISUtil.getDateAfter(sdfdatetime.parse(ls_tjsj),
						1);
				MapParameter ZY_BQYZP = new MapParameter()
						.put("ldt_fyrq", qrsj)
						.put("ll_yjxh", Long.parseLong(ll_yjxh))
						.put("ll_yzxh", Long.parseLong(ll_yzxh))
						.put("gl_jgid", jgid);
				dao.doUpdate(
						"UPDATE JC_BRYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0,TJZX=2,LSBZ=1 Where JLXH = :ll_yzxh  And JGID = :gl_jgid and LSYZ = 1",
						ZY_BQYZP);
				dao.doUpdate(
						"UPDATE JC_BRYZ Set YJXH = :ll_yjxh , QRSJ = :ldt_fyrq,SYBZ = 0,TJZX=2 Where JLXH = :ll_yzxh   And JGID = :gl_jgid and LSYZ = 0",
						ZY_BQYZP);
				if (wpjfbz == 1 && wzsfxmjgzy == 1) {
					BSPHISUtil.updateXHMXZT(dao, Long.parseLong(ll_yzxh), "2",
							jgid);
				}
			}
		}
		// INSERT INTO YJ_BG01(JGID, YJXH, MBXH,MZZY, TJHM, BRHM, )
		// VALUES (gl_jgid,al_yjxh,ll_mbxh, 2,ls_tjhmnow,ls_zyhm,
		// BRXM, BRXB, BRNL, SJYS, SJKS, JCYS, JCKS, JCRQ, XMXH, XMMC, HJJE,
		// YQDH, ZDDM, BBBM, YJPH, ZYH
		// ls_brxm,ls_brxb,
		// ls_Age,ls_ysdm,ll_fyks,as_zxys,lu_yjks.il_yjksdm,ldt_jfrq,ll_xmxh,ls_jcxm,
		// ld_hjje,ls_yqdh, ls_zddm , ls_SpecimenId , ls_Yjph ,ll_zyh)

		// 生成医技报告单
		MapParameter saveData = new MapParameter()
				.put("JGID", jgid)
				.put("YJXH", ll_yjxh)
				.put("MBXH", null)
				.put("MZZY", 2)
				// 模板序号
				.put("TJHM", null).put("BRHM", ls_zyhm).put("BRXM", ls_brxm)
				.put("BRXB", ls_brxb).put("BRNL", ls_Age).put("SJYS", ls_ysdm)
				.put("SJKS", ll_fyks).put("JCYS", as_zxys).put("JCKS", zxks)
				.put("JCRQ", ldt_jfrq).put("XMXH", ll_xmxh)
				.put("XMMC", ls_jcxm).put("HJJE", ld_hjje).put("YQDH", null)
				// 仪器代码
				.put("ZDDM", ls_zddm).put("BBBM", ls_SpecimenId)
				.put("YJPH", ls_Yjph).put("ZYH", ll_zyh);
		Object o = dao
				.doLoad(BSPHISEntryNames.JC_BG01, Long.parseLong(ll_yjxh));
		if (o != null) // 已存在医技报告
			dao.doRemove(Long.parseLong(ll_yjxh), BSPHISEntryNames.JC_BG01);
		dao.doSave("create", BSPHISEntryNames.JC_BG01, saveData, true);

		Session sess = (Session) ctx.get(Context.DB_SESSION);
		sess.flush();

		return true;
	}

	static String valueOfString(Object o) {
		if (o != null) {
			return o.toString();
		}
		return "";

	}

	static class MapParameter extends HashMap<String, Object> {
		public MapParameter put(String key, Object value) {
			if (IsNull(value)) {
				super.put(key, null);
			} else {
				super.put(key, value);
			}
			return this;
		}

		public Object get(String key) {
			return super.get(key);
		}

	}

	@SuppressWarnings("unchecked")
	public void doQueryMX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
				.get("bodys");
		try {
			String perss = "";
			for (int i = 0; i < reqList.size(); i++) {
				if (i != (reqList.size() - 1)) {
					perss += Long.parseLong(reqList.get(i).get("YJXH") + "")
							+ ",";
				} else {
					perss += Long.parseLong(reqList.get(i).get("YJXH") + "");
				}
			}
			if (perss == "") {
				perss = "0";
			}
			StringBuffer hql_mz = new StringBuffer(
					"SELECT c.BRXM as BRXM,a.YJXH as YJXH,b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,b.FYDW as FYDW,a.HJJE as HJJE,a.FYGB as FYGB,a.BZXX as BZXX FROM ")
					.append("MS_YJ02")
					.append(" a,GY_YLSF")
					.append(" b,MS_YJ01 c WHERE c.YJXH=a.YJXH and ( a.YJXH in("
							+ perss + ")) AND ( a.YLXH = b.FYXH )");
			Map<String, Object> parameters = new HashMap<String, Object>();

			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql_mz.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.YJ_MZYWMX);
			res.put("body", list_YJ_MZYWMX);

		} catch (PersistentDataOperationException e) {
			logger.error("获取门诊医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取门诊医技项目失败!");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryZyMX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
				.get("bodys");
		String perss = "";
		for (int i = 0; i < reqList.size(); i++) {
			if (i != (reqList.size() - 1)) {
				perss += Long.parseLong(reqList.get(i).get("YJXH") + "") + ",";
			} else {
				perss += Long.parseLong(reqList.get(i).get("YJXH") + "");
			}
		}
		if (perss == "") {
			perss = "0";
		}
		String hql = "SELECT c.BRXM as BRXM,a.YJXH as YJXH,b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,a.YZXH as YZXH,a.YLXH as YLXH,b.FYDW as FYDW,a.YEPB as YEPB,(a.YLSL*a.YLDJ) as YLJE FROM YJ_ZY02 a,GY_YLSF b,YJ_ZY01 c WHERE c.YJXH=a.YJXH and ( a.YLXH = b.FYXH ) and ( a.YJXH in ("
				+ perss + ")) AND a.JGID = :al_jgid ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", Long.parseLong(jgid));
		try {
			List<Map<String, Object>> list_YJ_MZYWMX = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_YJ_MZYWMX,
					BSPHISEntryNames.YJ_ZYYWMX);
			res.put("body", list_YJ_MZYWMX);
			// return list_YJ_MZYWMX;
		} catch (PersistentDataOperationException e) {
			logger.error("获取住院医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取住院医技项目失败!");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryJcMX(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		List<Map<String, Object>> reqList = (List<Map<String, Object>>) req
				.get("bodys");
		String perss = "";
		for (int i = 0; i < reqList.size(); i++) {
			if (i != (reqList.size() - 1)) {
				perss += Long.parseLong(reqList.get(i).get("YJXH") + "") + ",";
			} else {
				perss += Long.parseLong(reqList.get(i).get("YJXH") + "");
			}
		}
		if (perss == "") {
			perss = "0";
		}
		String hql = "SELECT c.BRXM as BRXM,a.YJXH as YJXH,b.FYMC as FYMC,a.YLDJ as YLDJ,a.YLSL as YLSL,a.ZFBL as ZFBL,a.YZXH as YZXH,a.YLXH as YLXH,b.FYDW as FYDW,a.YEPB as YEPB,(a.YLSL*a.YLDJ) as YLJE FROM JC_YJ02 a,GY_YLSF b,JC_YJ01 c WHERE c.YJXH=a.YJXH and ( a.YLXH = b.FYXH ) and ( a.YJXH in ("
				+ perss + ")) AND a.JGID = :al_jgid ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_jgid", jgid);
		try {
			List<Map<String, Object>> list_JC_YJ02_ZX = dao.doQuery(
					hql.toString(), parameters);
			SchemaUtil.setDictionaryMassageForList(list_JC_YJ02_ZX,
					BSPHISEntryNames.JC_YJ02_ZX);
			res.put("body", list_JC_YJ02_ZX);
		} catch (PersistentDataOperationException e) {
			logger.error("获取家床医技项目失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取家床医技项目失败!");
		}
	}
}
