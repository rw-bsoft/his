package phis.application.cic.source;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.SkinTestUnConfigException;
import phis.source.bean.Param;
import phis.source.service.ServiceCode;
import phis.source.service.remind.RemindServer;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.utils.T;
import phis.source.ws.client.crbbk.LSCrbbkMiddleServiceLocator;
import phis.source.ws.client.crbbk.LSCrbbkMiddleServiceSoap11BindingStub;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.net.rpc.util.ServiceAdapter;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.MD5StringUtil;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.validator.ValidateException;

public class ClinicManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ClinicManageModel.class);

	public ClinicManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> doGetClinicStatus(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select FPHM from MS_CF01 where CFSB=:CFSB ";
		try {
			params.put("CFSB", Long.parseLong(body.get("CFSB").toString()));
			List<Map<String, Object>> l = dao.doSqlQuery(hql, params);
			if (l.size() > 0) {
				return l.get(0);
			} else {
				return null;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取处方信息失败!", e);
		}
	}

	public boolean doQuerySkinTestStatus(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select 'x' from YS_MZ_PSJL where YPBH=:YPXH and CFSB=:CFSB and WCBZ>0";
		try {
			params.put("YPXH", Long.parseLong(body.get("YPXH").toString()));
			params.put("CFSB", Long.parseLong(body.get("CFSB").toString()));
			List<Map<String, Object>> l = dao.doSqlQuery(hql, params);
			return l.size() > 0;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取皮试信息失败!", e);
		}
	}

	public Map<String, Object> doVerifyDocInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		String uid = body.get("uid").toString();
		String psw = MD5StringUtil.MD5Encode((String) body.get("psw"));
		String kssdj = body.get("kssdj").toString();
		try {
			User user = AccountCenter.getUser(uid);
			if (user.validatePassword(psw)) {
				Map<String, Object> gy_ygdm = dao.doLoad(
						BSPHISEntryNames.SYS_Personnel, uid);
				String kssqx = (String) gy_ygdm.get("ANTIBIOTICLEVEL");
				if (kssqx == null || kssqx.indexOf(kssdj) < 0) {
					res.put("code", 503);
					res.put("msg", "工号【" + uid + "】的医生没有抗菌药物权限!");
				}
			} else {
				res.put("code", 501);
				res.put("msg", "密码错误!");
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			res.put("code", 502);
			res.put("msg", "工号【" + uid + "】的医生不存在!");
			return res;
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			res.put("code", 502);
			res.put("msg", "工号【" + uid + "】的医生不存在!");
			return res;
		} catch (ControllerException e) {
			logger.error(e.getMessage());
			res.put("code", 502);
			res.put("msg", "工号【" + uid + "】的医生不存在!");
			return res;
		}
		res.put("SQYS", uid);
		return res;
	}

	public Map<String, Object> loadCF01(Object jzxh, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		if (jzxh != null) {
			String hql = "select CFSB as CFSB,CFHM as CFHM,CFLX as CFLX,FPHM as FPHM,ZFPB as ZFPB from MS_CF01 where JZXH=:JZXH ORDER BY CFLX,CFHM";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZXH", jzxh);
			try {
				List<Map<String, Object>> list = dao.doQuery(hql, params);
				res.put("cf01s", list);
			} catch (PersistentDataOperationException e) {
				logger.error(
						"fail to load ms_cf02 information by database reason",
						e);
				throw new ModelDataOperationException("处方信息查找失败！", e);
			}
		}
		return res;
	}

	public List<Map<String, Object>> doLoadAddition(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
		try {
			return dao
					.doSqlQuery(
							"select a.SBXH as SBXH,a.YJZH,a.YJXH,a.YLXH,b.FYMC,b.FYDW,a.YLSL,a.YLDJ,a.HJJE,a.ZFBL,a.FYGB,(select FJGL from MS_YJ01 t where t.YJXH= a.YJXH) as YPZH from "
									+ BSPHISEntryNames.MS_YJ02_TABLE
									+ " a,"
									+ BSPHISEntryNames.GY_YLSF_TABLE
									+ " b where a.YLXH=b.FYXH and a.YJXH in (select YJXH from MS_YJ01 t where JZXH=:JZXH and FJGL is not null )",
							params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("附加项目加载失败！", e);
		}
	}

	public Map<String, Object> doLoadPssfxm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException,
			SkinTestUnConfigException {
		Map<String, Object> params = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		try {

			// 获取系统参数皮试收费对应项目序号
			String pssfdyxmbh = ParameterUtil.getParameter(manageUnit,
					BSPHISSystemArgument.PSSFDYXM, ctx);
			// 根据项目序号查找对应费用
			Map pssfdyxmParam = new HashMap();
			pssfdyxmParam.put("FYXH", Long.parseLong(pssfdyxmbh));
			pssfdyxmParam.put("JGID", manageUnit);

			System.out.println(pssfdyxmParam);

			// 判断是否设置了皮试收费对应项目参数如果没有则提示设置
			List pssfdyxmList = dao
					.doSqlQuery(
							"select a.FYXH as FYXH, a.FYGB as FYGB,a.FYDW as FYDW,a.FYMC as FYMC,a.BZJG as BZJG,b.FYDJ as FYDJ "
									+ "from GY_YLSF a,GY_YLMX b where a.FYXH=:FYXH and a.FYXH=b.FYXH and b.JGID=:JGID",
							pssfdyxmParam);
			if (pssfdyxmList.size() < 1) {
				throw new SkinTestUnConfigException();
			}

			pssfdyxmParam.put("BRXZ", body.get("BRXZ"));
			pssfdyxmParam.put("JGID", manageUnit);
			List pssfdyxmmxList = dao
					.doSqlQuery(
							"select a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.BZJG as BZJG,b.ZFBL as ZFBL,c.FYDJ as FYDJ "
									+ "from GY_YLSF a left join GY_FYJY b on a.fyxh=b.fyxh,GY_YLMX c "
									+ "where a.FYXH=:FYXH and b.BRXZ=:BRXZ and a.FYXH=c.FYXH and c.JGID=:JGID",
							pssfdyxmParam);

			// 皮试对应收费项目详细
			Map pssfdyxmMap = (Map) pssfdyxmList.get(0);

			Map pssfxm = new HashMap();
			if (pssfdyxmmxList.size() < 1) {
				// 根据病人性质，自负比例没设置的默认为0
				// pssfxm.put("_opStatus","create");
				// pssfxm.put("JGID",manageUnit);
				Map pssfdyxmmxMap = (Map) pssfdyxmList.get(0);
				pssfxm.put("YPXH", pssfdyxmMap.get("FYXH"));
				pssfxm.put("YLXH", pssfdyxmMap.get("FYXH"));
				pssfxm.put("YLSL", 1);
				pssfxm.put("ZFBL", 0);
				pssfxm.put("YLDJ", pssfdyxmmxMap.get("FYDJ"));
				pssfxm.put("HJJE", pssfdyxmmxMap.get("FYDJ"));
				pssfxm.put("FYGB", pssfdyxmMap.get("FYGB"));
				pssfxm.put("FYDW", pssfdyxmMap.get("FYDW"));
				pssfxm.put("FYMC", pssfdyxmMap.get("FYMC"));
				pssfxm.put("SBXH", pssfdyxmMap.get(""));
				pssfxm.put("_opStatus", "create");
			} else {
				Map pssfdyxmmxMap = (Map) pssfdyxmmxList.get(0);
				pssfxm.put("YPXH", pssfdyxmmxMap.get("FYXH"));
				pssfxm.put("YLXH", pssfdyxmMap.get("FYXH"));
				pssfxm.put("YLSL", 1);
				pssfxm.put("ZFBL", pssfdyxmmxMap.get("ZFBL"));
				pssfxm.put("YLDJ", pssfdyxmmxMap.get("FYDJ"));
				pssfxm.put("HJJE", pssfdyxmmxMap.get("FYDJ"));
				pssfxm.put("FYGB", pssfdyxmmxMap.get("FYGB"));
				pssfxm.put("FYDW", pssfdyxmmxMap.get("FYDW"));
				pssfxm.put("FYMC", pssfdyxmmxMap.get("FYMC"));
				pssfxm.put("SBXH", pssfdyxmmxMap.get(""));
				pssfxm.put("_opStatus", "create");
			}
			// System.out.println(pssfxm);
			return pssfxm;
		} catch (SkinTestUnConfigException e) {
			throw new SkinTestUnConfigException(
					"皮试对应项目号未维护或对应项目不存在,请去系统参数里维护PSSFDYXM！", e);
		} catch (Exception e) {
			throw new ModelDataOperationException("附加项目加载失败！", e);
		}
	}

	public Map<String, Object> doLoadSfps(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map sfps = new HashMap();
		try {
			params.put("CFSB", body.get("CFSB"));
			params.put("YPBH", body.get("YPBH"));
			List sfpsList = dao
					.doSqlQuery(
							"select WCBZ as WCBZ  from YS_MZ_PSJL where CFSB=:CFSB and YPBH=:YPBH",
							params);
			Map sfpsMap = (Map) sfpsList.get(0);
			if (!sfpsMap.get("WCBZ").toString().equals("0")) {
				sfps.put("sfps", true);
			}
			return sfps;
		} catch (Exception e) {
			throw new ModelDataOperationException("附加项目加载失败！", e);
		}
	}

	public Map<String, Object> doSfpsyp(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		try {
			// System.out.println(body);
			// System.out.println(body.get("ypList"));
			List ypList = (List) body.get("ypList");
			// System.out.println(ypList.size());

			return null;
		} catch (Exception e) {
			throw new ModelDataOperationException("附加项目加载失败！", e);
		}
	}

	public Map<String, Object> initClinicInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		// 判断挂号信息是否存在
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Long reg_ksdm = user.getProperty("reg_departmentId", Long.class);
		Long biz_ksdm = user.getProperty("biz_departmentId", Long.class);
		// 医保相关
		// Long SHIYB = Long.parseLong(ParameterUtil.getParameter(manageUnit,
		// "SHIYB", "0", "市医保病人性质", ctx));
		// Long SHENGYB = Long.parseLong(ParameterUtil.getParameter(manageUnit,
		// "SHENGYB", "0", "省医保病人性质", ctx));
		String YXWGGBRJZ = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.YXWGHBRJZ, ctx);// 是否自动挂号
		String GHXQ = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.GHXQ, ctx);// 挂号有效期
		String XQJSFS = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
		boolean checkValidity = true;// 是否需要校验挂号信息有效性
		Object regId = null;
		// 挂号有效期
		Date now = new Date();
		if ("1".equals(XQJSFS + "")) {
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				now = matter.parse(BSHISUtil.getDate() + " 23:59:59");
			} catch (ParseException e) {
				throw new ModelDataOperationException("挂号效期转化错误!", e);
			}
		}
		Date regBegin = DateUtils.addDays(now, -Integer.parseInt(GHXQ));
		// 取出年龄放入mpi
		String brid = (String) body.get("BRID");
		String empiId = (String) body.get("EMPIID");
		Map<String, Object> params1 = new HashMap<String, Object>();
		if(brid==null){
			params1.put("BRID", "");
		}else{
			params1.put("BRID", brid);
		}
		params1.put("EMPIID", empiId);
		int age = 0;
		try {
			Date birthday = (Date) dao
					.doLoad("select CSNY as CSNY from MS_BRDA where EMPIID=:EMPIID or BRID=:BRID",
							params1).get("CSNY");
			age = BSHISUtil.calculateAge(birthday, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无法获取病人信息!", e);
		}
		if (brid == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EMPIID", empiId);
			try {
				brid = (String) dao
						.doLoad("select BRID as BRID from MS_BRDA where EMPIID=:EMPIID",
								params).get("BRID");
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("无法获取病人信息!");
			}
		}
		Object sbxh = body.get("SBXH");// 挂号明细表的
		// 1、如果sbxh为空且YXWGGBRJZ为1时，自动插入挂号信息
		if (sbxh == null) {
			if (!YXWGGBRJZ.equals("1")) {
				res.put("code", 601);
				res.put("msg", "无法获取就诊信息!");
				throw new ModelDataOperationException("无法获取就诊信息!");
			}
			try {
				// 判断有效期内是否有挂号信息
				StringBuffer maxRegId_hql = new StringBuffer(
						"select max(SBXH) as SBXH from ");
				maxRegId_hql.append("MS_GHMX");
				maxRegId_hql
						.append(" where BRID=:BRID AND KSDM=:KSDM AND THBZ=0  AND GHSJ>:GHSJ");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("GHSJ", regBegin);
				params.put("BRID", Long.parseLong(brid));
				params.put("KSDM", reg_ksdm);
				Map<String, Object> retMap = dao.doLoad(
						maxRegId_hql.toString(), params);
				if (retMap.get("SBXH") != null) {
					regId = retMap.get("SBXH");
				} else {
					// 就诊序号和就诊号码生成策略
					Map<String, Object> ms_ghmx = new HashMap<String, Object>();
					// 根据当前医生获取就诊号码
					String clinicId = BSPHISUtil.getNotesNumber(
							(String) user.getUserId(), manageUnit, 1, dao, ctx);
					if (clinicId == null) {
						res.put("code", 602);
						res.put("msg", "就诊号码未设置或已用完!");
						throw new ModelDataOperationException("就诊号码未设置或已用完!");
					}
					ms_ghmx.put("JZHM", clinicId);
					ms_ghmx.put("JZXH", 1);
					ms_ghmx.put("JGID", manageUnit);
					ms_ghmx.put("BRID", Long.parseLong(brid));
					ms_ghmx.put("BRXZ", body.get("BRXZ"));
					ms_ghmx.put("GHSJ", new Date());
					ms_ghmx.put("GHLB", body.get("GHLB"));
					ms_ghmx.put("KSDM", reg_ksdm);
					ms_ghmx.put("JZYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
					ms_ghmx.put("CZGH", user.getUserId() + "");// 获取员工代码暂时用该方法替代
					// 设置默认值
					String hqlWhere = "BRID=:BRID and JGID=:JGID";
					params.clear();
					params.put("BRID", Long.parseLong(brid));
					params.put("JGID", manageUnit);
					Long l = dao.doCount("MS_GHMX", hqlWhere, params);
					ms_ghmx.put("CZPB", l > 0 ? 0 : 1);
					ms_ghmx.put("GHLB", 1);
					ms_ghmx.put("GHJE", 0.00);
					ms_ghmx.put("GHCS", 1);
					ms_ghmx.put("THBZ", 0);
					ms_ghmx.put("ZLJE", 0.00);
					ms_ghmx.put("ZJFY", 0.00);
					ms_ghmx.put("BLJE", 0.00);
					ms_ghmx.put("XJJE", 0.00);
					ms_ghmx.put("ZPJE", 0.00);
					ms_ghmx.put("ZHJE", 0.00);
					ms_ghmx.put("HBWC", 0.00);
					ms_ghmx.put("QTYS", 0.00);
					ms_ghmx.put("MZLB", BSPHISUtil.getMZLB(manageUnit, dao));
					ms_ghmx.put("YSPB", 0);
					ms_ghmx.put("SFFS", 0);
					ms_ghmx.put("JZZT", 0);
					ms_ghmx.put("CZSJ", new Date());
					Map<String, Object> keyGen = dao.doSave("create",
							BSPHISEntryNames.MS_GHMX_CIC, ms_ghmx, false);
					regId = keyGen.get("SBXH");
				}
				checkValidity = false;// 跳过MS_GHMX校验
			} catch (ValidateException e) {
				logger.error(
						"fail to auto save ms_ghmx information by database reason",
						e);
				throw new ModelDataOperationException("挂号信息校验失败!", e);
			} catch (PersistentDataOperationException e) {
				logger.error(
						"fail to auto save ms_ghmx information by database reason",
						e);
				throw new ModelDataOperationException("挂号信息保存失败!", e);
			}
		} else {
			regId = Long.parseLong(sbxh.toString());
		}
		try {
			if (checkValidity) {/*
								 * String hqlWhere =
								 * " BRID=:BRID AND KSDM=:KSDM  AND SBXH=:SBXH AND THBZ=0 "
								 * ; parameters.put("BRID",
								 * Long.parseLong(brid)); parameters.put("KSDM",
								 * reg_ksdm); parameters.put("SBXH",
								 * Long.parseLong(sbxh.toString())); //
								 * parameters.put("GHSJ", regBegin); long l =
								 * dao.doCount(BSPHISEntryNames.MS_GHMX,
								 * hqlWhere, parameters); if (l > 0) { regId =
								 * Long.parseLong(sbxh.toString()); } else { //
								 * parameters.put("KSDM", reg_ksdm);
								 * StringBuffer maxRegId_hql = new StringBuffer(
								 * "select max(SBXH) as SBXH from ");
								 * maxRegId_hql
								 * .append(BSPHISEntryNames.MS_GHMX);
								 * maxRegId_hql .append(
								 * " where BRID=:BRID AND THBZ=0 AND GHSJ>:GHSJ AND KSDM=:KSDM"
								 * ); parameters.remove("SBXH");
								 * parameters.put("GHSJ", regBegin); Map<String,
								 * Object> retMap = dao.doLoad(
								 * maxRegId_hql.toString(), parameters); if
								 * (retMap.get("SBXH") != null) { regId =
								 * retMap.get("SBXH"); } else { res.put("code",
								 * 601); res.put("msg", "挂号信息无法调入，终止操作!"); throw
								 * new
								 * ModelDataOperationException("挂号信息无法调入，终止操作!"
								 * ); } }
								 */
			}
			if (Long.parseLong(regId.toString()) > 0) {// 存在挂号信息，判断效期内是否有就诊记录
				// 更新MS_GHMX表的JZZT
				parameters.clear();
				parameters.put("SBXH", regId);
				dao.doUpdate(
						"update MS_GHMX set JZZT=1 where JZZT=0 and SBXH=:SBXH",
						parameters);
				// YS_MZ_JZLS表信息插入
				Map<String, Object> record = new HashMap<String, Object>();
				StringBuffer jzmx_hql = new StringBuffer(
						"select JZXH as JZXH,JZZT as JZZT,YSDM as YSDM from YS_MZ_JZLS");
				jzmx_hql.append(" where GHXH=:GHXH and BRBH=:BRBH and KSDM=:KSDM and (KSSJ>:KSSJ or FZRQ>:KSSJ) order by KSSJ desc");
				parameters.clear();
				parameters.put("GHXH", Long.parseLong(regId.toString()));
				parameters.put("BRBH", Long.parseLong(brid));
				parameters.put("KSDM", biz_ksdm);
				parameters.put("KSSJ", regBegin);
				List<Map<String, Object>> jzlsList = dao.doQuery(
						jzmx_hql.toString(), parameters);
				if (jzlsList.size() > 0) {
					// 判断就诊状态
					Integer jzzt = (Integer) jzlsList.get(0).get("JZZT");
					String ysdm = (String) jzlsList.get(0).get("YSDM");
					Long jzxh = (Long) jzlsList.get(0).get("JZXH");
					if (jzzt == 1) {
						if (!(user.getUserId() + "").equals(ysdm)) {// 获取员工代码暂时用该方法替代
							res.put("code", 603);
							res.put("msg", "对不起，其他医生正在处理该病人!");
							throw new ModelDataOperationException(
									"对不起，其他医生正在处理该病人!");
						}
					}
					// 修改状态为就诊中
					record.clear();
					record.put("JZXH", jzxh);
					record.put("JZZT", 1);
					record.put("KSDM", biz_ksdm);
					dao.doSave("update", BSPHISEntryNames.YS_MZ_JZLS, record,
							false);
					res.put("JZXH", jzxh);
					/*
					 * 注释拱墅区版本中有关医保的代码// 判断规定病种 Map<String, Object>
					 * gdbzparameters = new HashMap<String, Object>();
					 * StringBuffer hql = new StringBuffer(); // hql.append(
					 * "select count(*) as COUNT from yb_zxsp,yb_ghjs,ms_ghmx where yb_zxsp.yyxh = yb_ghjs.jylsh and yb_ghjs.sbxh = ms_ghmx.sbxh and "
					 * // + "ms_ghmx.sbxh = ("); // hql.append(
					 * "select ms_ghmx.sbxh from ys_mz_jzls,ms_ghmx where ys_mz_jzls.ghxh = ms_ghmx.sbxh and ys_mz_jzls.ghxh = ("
					 * ); // hql.append(
					 * "select ys_mz_jzls.ghxh from ys_mz_jzls,ms_cf01 where ys_mz_jzls.jzxh = ms_cf01.jzxh and ms_cf01.jzxh = "
					 * ); // hql.append(Long.parseLong(res.get("JZXH") + "") +
					 * " ))" // + " and ms_ghmx.brxz = " + SHIYB);// 只适用市医保
					 * hql.append(
					 * "select count(*) as COUNT from yb_zxsp a,yb_ghjs b,ms_ghmx c"
					 * );
					 * hql.append(" where a.yyxh = b.jylsh and b.sbxh = c.sbxh"
					 * ); hql.append(" and c.brxz =" + SHIYB +
					 * " and c.sbxh = (select ghxh from ys_mz_jzls where jzxh ="
					 * + Long.parseLong(res.get("JZXH") + "") + ")");
					 *
					 * List<Map<String, Object>> gdbzlist = dao.doSqlQuery(
					 * hql.toString(), gdbzparameters); // 规定病种记录 long pdgdbz =
					 * 0; Map<String, Object> count_gdbz = gdbzlist.get(0); if
					 * (Integer.parseInt(count_gdbz.get("COUNT") + "") != 0) {
					 * pdgdbz = 1; res.put("PDGDBZ", pdgdbz); } else {
					 * res.put("PDGDBZ", pdgdbz); }
					 */
				} else {
					// 插入就诊记录
					record.clear();
					record.put("GHXH", regId);
					record.put("BRBH", brid);
					record.put("KSDM", biz_ksdm);
					record.put("KSSJ", new Date());
					record.put("YSDM", user.getUserId() + "");// 获取员工代码暂时用该方法替代
					record.put("JZZT", 1);
					record.put("JGID", manageUnit);
					res = dao.doSave("create", BSPHISEntryNames.YS_MZ_JZLS,
							record, false);
				}
			} else {
				res.put("code", 601);
				res.put("msg", "挂号信息无法调入，终止操作!");
				throw new ModelDataOperationException("挂号信息无法调入，终止操作!");
			}

		} catch (ValidateException e) {
			throw new ModelDataOperationException("载入病人就诊信息失败，请联系管理员!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("载入病人就诊信息失败，请联系管理员!", e);
		}
		res.put("empiId", empiId);
		res.put("BRID", brid);
		res.put("age", age);
		return res;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> loadSystemParams(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<String> commons = (List<String>) body.get("commons");
		List<String> privates = (List<String>) body.get("privates");
		List<String> personals = (List<String>) body.get("personals");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		if (commons != null) {
			for (String _CSMC : commons) {
				retMap.put(_CSMC, ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), _CSMC, ctx));
			}
		}
		if (privates != null) {
			for (String _CSMC : privates) {
				retMap.put(_CSMC,
						ParameterUtil.getParameter(manageUnit, _CSMC, ctx));
			}
		}
		if (personals != null) {
			List<Object> cnd = CNDHelper.createSimpleCnd("eq", "YHBH", "s",
					user.getUserId() + "");// 获取员工代码暂时用该方法替代
			body = null;
			try {
				Map<String, Object> ysxg = dao.doLoad(cnd,
						BSPHISEntryNames.EMR_YSXG_GRCS);
				if (ysxg != null) {
					retMap.putAll(ysxg);
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("加载医生个人习惯失败!", e);
			}
		}

		return retMap;
	}

	/**
	 * 载入病历信息
	 *
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadClinicInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> clinicInfo = new HashMap<String, Object>();
		String type = (String) body.get("type");// type 1:病历 2：诊断 3：处方 处置 5：所有
		Long clinicId = Long.parseLong(((Integer) body.get("clinicId"))
				.toString());
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String[] types = type.split(",");
		try {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals("1") || types[i].equals("5")) {
                    //zhaojian 2017-11-14 门诊医生站增加症状的下拉框选择功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC
                    //String hql = "select ZSXX as ZSXX,XBS as XBS,JWS as JWS,TGJC as TGJC,FZJC as FZJC,T as T,P as P,R as R,SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT,FT as FT,FX as FX,PZ as PZ,QT as QT,DPY as DPY,W as W,H as H,BMI as BMI from MS_BCJL where JZXH=:JZXH";
                    String hql = "select ZSXX as ZSXX,XBS as XBS,JWS as JWS,TGJC as TGJC,FZJC as FZJC,T as T," +
                    		" P as P,R as R,SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT," +
                    		" FT as FT,FX as FX,PZ as PZ,QT as QT,DPY as DPY,W as W,H as H,BMI as BMI,ZZDM as ZZDM," +
                    		" ZZMC as ZZMC,JKJYNR as JKJYNR,GMS as GMS,BQGZ as BQGZ,RSBZ as RSBZ,YCFJM as YCFJM,YQJCS as YQJCS,QTBS as QTBS from MS_BCJL where JZXH=:JZXH";
                    Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", clinicId);
					Map<String, Object> result = dao.doLoad(hql, parameters);
					Map<String,Object> result_JWS = dao.doLoad("phis.application.cic.schemas.MS_BCJL_JWS",clinicId);
					Map<String,Object> result_ZYZZ = dao.doLoad("phis.application.cic.schemas.MS_BCJL_ZYZZ",clinicId);
					result.putAll(result_JWS);
					result.putAll(result_ZYZZ);
					String fzjc = null;
					if (result != null) {
						fzjc = result.get("FZJC") + "";
					}
					if (result != null && types[i].equals("5")) {
						List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId","s", clinicId);
						List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay","s", "01");
						List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
						List<Map<String, Object>> list = dao.doList(cnd, "recordId",
										"phis.application.cic.schemas.HER_HealthRecipeRecord_MZ");
						for(Map<String, Object> one : list){
							if(one.get("healthTeach")==null || "null".equals(one.get("healthTeach")+"") ){
								one.put("healthTeach", "");
							}
						}
						result.put("JKCFRecords", list);
					}
					List<Map<String, Object>> yjxx = dao.doSqlQuery(
									"select c.FYMC as FYMC,e.ZDMC as ZDMC from MS_YJ01 a,MS_YJ02 b,GY_YLSF c,YJ_BG01 d left outer join YJ_ZDJG e on d.ZDDM=e.ZDID where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.YJXH=d.YJXH and d.MZZY=1 and b.YJZX=1 and a.JZXH=:JZXH",
									parameters);
					StringBuffer mc = new StringBuffer();
					for (int j = 0; j < yjxx.size(); j++) {
						mc.append(yjxx.get(j).get("FYMC") + "");// 拿到fymc
						if (yjxx.get(j).get("ZDMC") != null && !"null".equals("ZDMC")) {// 如果诊断结果不为空
							mc.append(":").append(yjxx.get(j).get("ZDMC") + " | ");// 就加上诊断结果
						} else {
							if (j > 0) {
								mc.append(" | ");
							}
						}
					}
					if (mc.length() > 0) {
						String mcstr = "{"+ mc.toString().substring(0,mc.toString().length() - 3) + "}";
						if (mcstr.length() > 0) {
							result.put("YJXX", mcstr);
							if (fzjc.length() > 0) {
								if (fzjc.indexOf("{") >= 0 && fzjc.indexOf("}") >= 0) {
									String fzjcstr = fzjc.substring(fzjc.indexOf("{"),fzjc.indexOf("}") + 1);
									if (fzjcstr.length() != mcstr.length()) {
										result.put("FZJC",fzjc.substring(0,fzjc.indexOf("{"))
														+ " "+ fzjc.substring(fzjc.lastIndexOf("}") + 1));
									}
								}
							}
						}
					}
					clinicInfo.put("ms_bcjl", result);

				}
				if (types[i].equals("2") || types[i].equals("5")) {// 诊断
					StringBuffer hql = new StringBuffer(
							"select a.ICD10 as ICD10,a.ZDMC as ZDMC,a.DEEP as DEEP,a.ZZBZ as ZZBZ,"
									+ " a.FZBZ as FZBZ,a.CFLX as CFLX,b.ZHMC as ZHMC,JLBH as JLBH," +
									" a.ZDXH as ZDXH from ");
					hql.append(" MS_BRZD a left join EMR_ZYZH b on a.ZDBW=b.ZHBS where JZXH=:JZXH and JGID=:JGID order by PLXH ");
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", clinicId);
					parameters.put("JGID", manageUnit);
					clinicInfo.put("ms_brzd",
							dao.doSqlQuery(hql.toString(), parameters));

				}
				if (types[i].equals("3") || types[i].equals("5")) {// 处方处置
					// Map<String, Object> res = loadCF01(clinicId, ctx);
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("JZXH", clinicId);
					List<Map<String, Object>> cfsbList = dao
							.doQuery(
									"select CFSB as CFSB from MS_CF01 where JZXH=:JZXH and ZFPB<>1 ORDER BY CFLX,CFHM",
									parameters);
					String cfsbs = "";
					for (Map<String, Object> cfsb : cfsbList) {
						if (cfsbs.length() > 0) {
							cfsbs += ",";
						}
						cfsbs += cfsb.get("CFSB");
					}
					if (cfsbs.trim().length() > 0) {
						String cnd = "['in',['$','a.CFSB'],[" + cfsbs + "]]";
						ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
								dao);
						// clinicInfo.put("measures", dao.doList(
						// CNDHelper.toListCnd(cnd),
						// "c.CFLX,a.CFSB,a.SBXH",
						// BSPHISEntryNames.MS_CF02 + "_BL"));
						// update by caijy at 2014.10.9 for 自备药
						List<Map<String, Object>> list_mea = mmd.queryCfmx(
								CNDHelper.toListCnd(cnd), ctx);
						if (list_mea != null && list_mea.size() > 0) {
							for (Map<String, Object> m : list_mea) {
								if (Integer.parseInt(m.get("ZFYP") + "") == 1) {
									m.put("YPMC", "(自备)" + m.get("YPMC"));
								}
							}
						}
						clinicInfo.put("measures", list_mea);
					}
					String cnd = "['eq', ['$', 'c.JZXH'],['d', " + clinicId
							+ "]]";
					clinicInfo.put("disposal", dao.doList(
							CNDHelper.toListCnd(cnd), "YJZH ,SBXH",
							BSPHISEntryNames.MS_YJ02_CIC));
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("载入病人就诊信息失败，请联系管理员!", e);
		}
		return clinicInfo;
	}

	/**
	 * 载入结算信息
	 *
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadSettlementInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> clinicInfo = new HashMap<String, Object>();
		Long clinicId = Long.parseLong(((Integer) body.get("clinicId"))
				.toString());
		Map<String, Object> mzxx = new HashMap<String, Object>();
		try {
			String hql = "select CFSB as CFSB from MS_CF01 where JZXH=:JZXH and FPHM is null";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZXH", clinicId);
			List<Map<String, Object>> cfsbList = dao.doQuery(hql, params);
			String cfsbs = "";
			for (Map<String, Object> cfsb : cfsbList) {
				if (cfsbs.length() > 0) {
					cfsbs += ",";
				}
				cfsbs += cfsb.get("CFSB");
			}
			mzxx.put("CFSL", dao.doCount("MS_CF01", "JZXH=:JZXH", params));
			mzxx.put("JCSL", dao.doCount("MS_YJ01", "JZXH=:JZXH", params));
			Map<String, Object> map = dao
					.doLoad("select sum(b.HJJE) as CFJE from MS_CF01 a,MS_CF02 b where a.JZXH=:JZXH and a.CFSB=b.CFSB and b.ZFYP!=1 and a.ZFPB!=1",
							params);
			if (map == null || map.get("CFJE") == null) {
				mzxx.put("CFJE", 0);
			} else {
				mzxx.put("CFJE", map.get("CFJE"));
			}
			map = dao
					.doLoad("select sum(b.HJJE) as JCJE from MS_YJ01 a,MS_YJ02 b where a.JZXH=:JZXH and a.YJXH=b.YJXH",
							params);
			if (map == null || map.get("JCJE") == null) {
				mzxx.put("JCJE", 0);
			} else {
				mzxx.put("JCJE", map.get("JCJE"));
			}
			clinicInfo.put("MZXX", mzxx);
			if (cfsbs.trim().length() > 0) {
				String cnd = "['and',['in',['$','a.CFSB'],[" + cfsbs
						+ "]],['ne',['$','a.ZFYP'],['i',1]]]";
				clinicInfo.put("measures", dao.doList(CNDHelper.toListCnd(cnd),
						"a.CFSB,SBXH",
						"phis.application.cic.schemas.MS_CF02_CIC"));
			} else {
				clinicInfo.put("measures", new ArrayList<Object>());
			}
			String cnd = "['and',['eq', ['$', 'c.JZXH'],['i', " + clinicId
					+ "]],['isNull',['$','c.FPHM']]]";
			clinicInfo.put("disposal", dao.doList(CNDHelper.toListCnd(cnd),
					"YJZH ,SBXH", BSPHISEntryNames.MS_YJ02_CIC));
		} catch (Exception e) {
			throw new ModelDataOperationException("载入病人就诊信息失败，请联系管理员!", e);
		}
		return clinicInfo;
	}

	/**
	 * 功能:将剂量转换为数量
	 *
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadQuantity(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Double quantity = 0.0001;
		Map<String, Object> retQuantity = new HashMap<String, Object>();
		String pharmId = (String) body.get("pharmId");
		String medId = (String) body.get("medId");
		Double dosage = (Double) body.get("dosage");
		String hql = "Select BZLB as BZLB  From YF_YFLB "
				+ " Where YFSB = :YFSB";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("YFSB", pharmId);
		try {
			Map<String, Object> bMap = dao.doLoad(hql, params);
			int bzlb = (Integer) bMap.get("BZLB");
			Map<String, Object> mMap = null;
			if (bzlb == 1) {
				params.clear();
				params.put("YPXH", medId);
				String hql_ms = "Select YPJL as YPJL,ZXBZ as ZXBZ,YFBZ as YFBZ From "
						+ " YK_TYPK Where YPXH = :YPXH;";
				mMap = dao.doLoad(hql_ms, params);

			} else {
				// 住院，暂不实现
			}
			Integer ypbz = (Integer) mMap.get("YFBZ");
			Double ypjl = (Double) mMap.get("YPJL");
			if (ypbz == null || ypbz == 0) {
				ypbz = 1;
			}
			if (ypjl == null || ypjl == 0) {
				quantity = 0.0;
			} else {
				DecimalFormat myformat = new DecimalFormat("#0.0000");
				quantity = Double.parseDouble(myformat.format(dosage
						/ (ypjl * ypbz)));
				if (quantity == null || quantity == 0.00) {
					quantity = 0.0001;
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("剂量转换错误!", e);
		}
		retQuantity.put("quantity", quantity);
		return retQuantity;
	}

	/**
	 * 获取药品皮试历史信息
	 */
	public Map<String, Object> getGetSkinTest(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Object brid = body.get("BRID");
		Object YPXH = body.get("YPXH");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Date now = new Date();
		String PSXQ = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.PSXQ, ctx);
		Date regBegin = DateUtils.addDays(now, -Integer.parseInt(PSXQ));

		String cnds = "['and',['and',['eq',['$','a.BRBH'],['d'," + brid
				+ "]],['eq',['$','a.YPBH'],['d'," + YPXH
				+ "]]]],['ge',['$','a.PSSJ'],['todate',['s','"
				+ T.format(regBegin, T.DATETIME_FORMAT)
				+ "'],['s','yyyy-mm-dd hh24:mi:ss']]]]";
		try {
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnds), "SQSJ desc",
					BSPHISEntryNames.YS_MZ_PSJL_CIC);
			if (list.size() == 0) {
				Map<String, Object> gy_psjl = doLoadDetailsInfo(body, ctx);
				if (gy_psjl.get("isAllergy").equals(true)) {
					gy_psjl.put("PSJG", 1);
					return gy_psjl;
				}
				return null;
			} else {
				return list.get(0);
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("获取皮试信息失败!", e);
		}
	}

	/**
	 * 保存药品皮试信息
	 */
	public void saveGetSkinTest(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> record = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		record.put("BRBH", body.get("BRID"));
		record.put("YPBH", body.get("YPXH"));
		record.put("CFSB", body.get("CFSB"));
		record.put("JGID", manageUnit);
		record.put("WCBZ", 0);
		record.put("SQYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
		record.put("SQSJ", new Date());
		try {
			dao.doSave("create", "phis.application.cic.schemas.YS_MZ_PSJL",
					record, false);
		} catch (Exception e) {
			throw new ModelDataOperationException("保存皮试信息失败!", e);
		}
	}

	/**
	 * 保存处方信息到MS_CF01 MS_CF02
	 *
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doSaveClinicInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		// 处方号码，默认自动生成
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String uid=user.getUserId();
		String brxz=body.get("BRXZ")==null?"":body.get("BRXZ")+"";
		Map<String, Object> cf01 = (Map<String, Object>) body.get("formData");
		List<Map<String, Object>> cf02s = (List<Map<String, Object>>) body.get("listData");
		List<Map<String, Object>> fjxxs = (List<Map<String, Object>>) body.get("fjxxData");
		Long cfsb = null;
		try {
			// 删除附加项目
			Map<String, Object> parameters = new HashMap<String, Object>();
			if (fjxxs != null) {
				for (Map<String, Object> fjxx : fjxxs) {
					if (fjxx.get("_opStatus").equals("remove")) {
						dao.doRemove(Long.parseLong(fjxx.get("SBXH").toString()),BSPHISEntryNames.MS_YJ02_CIC);
					}
				}
			}
			String sjjg = null;
			long sjyf = 0;
			Map<String, Object> map_yfxx = dao.doLoad(BSPHISEntryNames.YF_YFLB_CIC,Long.parseLong(cf01.get("YFSB") + ""));
			if (map_yfxx.get("SJJG") != null && !"".equals(map_yfxx.get("SJJG"))) {
				sjjg = map_yfxx.get("SJJG") + "";
			}
			if (map_yfxx.get("SJYF") != null && !"".equals(map_yfxx.get("SJYF"))
				&& Long.parseLong(map_yfxx.get("SJYF") + "") != 0) {
				sjyf = Long.parseLong(map_yfxx.get("SJYF") + "");
			}
			if (sjyf != 0 && sjjg != null) {
				cf01.put("SJJG", sjjg);
				cf01.put("SJYF", sjyf);
				cf01.put("SJFYBZ", 1);
			}
			if (cf01.get("CFSB") == null) {
				cf01.put("JGID", manageUnit);
				cf01.put("PYBZ", 0);
				cf01.put("TYBZ", 0);
				cf01.put("KFRQ", BSHISUtil.toDate(cf01.get("KFRQ").toString()));
				cf01.put("FYBZ", 0);
				cf01.put("ZFPB", 0);
				cf01.put("YXPB", 0);
				if (!cf01.containsKey("DJLY")) {
					if (6 != Integer.parseInt(cf01.get("DJLY") + "")) {
						cf01.put("DJLY", 1);
					}
				}
				cf01.put("DJYBZ", 0);
				// 获取主键值
				Schema sc = SchemaController.instance().get(BSPHISEntryNames.MS_CF01);
				List<SchemaItem> items = sc.getItems();
				SchemaItem item = null;
				for (SchemaItem sit : items) {
					if ("CFSB".equals(sit.getId())) {
						item = sit;
						break;
					}
				}
				Long pkey = Long.parseLong(KeyManager.getKeyByName("MS_CF01",item.getKeyRules(), item.getId(), ctx));
				cf01.put("CFSB", pkey);
				cf01.put("CFHM", pkey);
				Map<String, Object> genKey = dao.doSave("create",BSPHISEntryNames.MS_CF01_CIC, cf01, false);
				cfsb = (Long) genKey.get("CFSB");
				res.putAll(genKey);
			} else {
				cfsb = Long.parseLong(cf01.get("CFSB").toString());
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("CFSB", cfsb);
				Long l = dao.doCount("MS_CF01","CFSB=:CFSB and MZXH is not null and MZXH>0", params);
				if (l > 0) {
					throw new ModelDataOperationException("当前处方已经收费，不允许编辑!");
				}
				cf01.put("KFRQ", BSHISUtil.toDate(cf01.get("KFRQ").toString()));
				dao.doSave("update", BSPHISEntryNames.MS_CF01_CIC, cf01, false);
				res.put("CFSB", cf01.get("CFSB"));
			}
			Schema sc = SchemaController.instance().get("phis.application.cic.schemas.MS_CF02_CF");
			List<SchemaItem> items = sc.getItems();
			SchemaItem item = null;
			for (SchemaItem sit : items) {
				if ("YPZH".equals(sit.getId())) {
					item = sit;
					break;
				}
			}
			Integer lastYzzh = -1;
			Long YPZH = 0l;
			List<Long> yjxhs = new ArrayList<Long>();
			String kssxzmsg="";//处方限制提示信息
			//获取配置参数门诊抗生素限制
			int MZKSSXZ = Integer.parseInt(ParameterUtil.getParameter(manageUnit,"MZKSSXZ", ctx));
			Integer kssqx=0;
			if(MZKSSXZ==1){
				//查询抗菌级别
				String kjqxsql="select s.ANTIBIOTICLEVEL as ANTIBIOTICLEVEL from SYS_PERSONNEL s where s.PERSONID=:uid ";
				Map<String, Object> p= new HashMap<String, Object>();
				p.put("uid", uid);
				Map<String, Object> kjqxmap=dao.doSqlLoad(kjqxsql, p);
				String kjqx="";
				kjqx=kjqxmap.get("ANTIBIOTICLEVEL")==null?"":kjqxmap.get("ANTIBIOTICLEVEL")+"";
				if(kjqx!=null && !kjqx.equals("null") && kjqx.length() > 0 ){
					if(kjqx.indexOf(",") >= 0 ){
						String[] arr=kjqx.split(",");
						for(int j=0;j<arr.length;j++){
							if(Integer.parseInt(arr[j])>kssqx){
								kssqx=Integer.parseInt(arr[j]);
							}
						}
					}else{
						kssqx=Integer.parseInt(kjqx);
					}
				}
			}
			String njjbcfmxmsg="";
			for (int i = 0; i < cf02s.size(); i++) {
				Map<String, Object> cf02 = cf02s.get(i);
				if(cf02.get("_opStatus") != null && !cf02.get("_opStatus").equals("remove"))
				if(MZKSSXZ==1){
					Map<String, Object> ypxx=new HashMap<String, Object>();
					String sql="select YPMC as YPMC,KSSDJ as KSSDJ from YK_TYPK where YPXH=:YPXH";
					Map<String, Object> pa=new HashMap<String, Object>();
					pa.put("YPXH", cf02.get("YPXH"));
					ypxx=dao.doSqlLoad(sql, pa);
					if(ypxx!=null){
						String kssdj=ypxx.get("KSSDJ")==null?"":ypxx.get("KSSDJ")+"";
						if(!kssdj.equals("null") && kssdj.length() > 0 ){
							if(Integer.parseInt(kssdj)>kssqx){
								if(kssxzmsg.length() > 0 ){
									kssxzmsg+=","+ypxx.get("YPMC")+"的抗生素级别为"+kssdj+"级";
								}else{
									kssxzmsg+=ypxx.get("YPMC")+"的抗生素级别为"+kssdj+"级";
								}
								continue;
							}
						}
					}
				}
				String uniqueId = ((cf02.get("YPZH") != null && Integer.parseInt(cf02.get("YPZH").toString()) > 0)
						|| (body.get("saveBy") != null)) ? "YPZH" : "YPZH_SHOW";
				if (cf02.get("_opStatus") != null && cf02.get("_opStatus").equals("remove")) {
					// 删除皮试信息
					if (cf02.get("PSPB") != null && ("1".equals(cf02.get("PSPB").toString())
						|| "2".equals(cf02.get("PSPB").toString()))) {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("CFSB", cf02.get("CFSB"));
						params.put("YPXH", cf02.get("YPXH"));
						dao.doSqlUpdate("delete from YS_MZ_PSJL where CFSB=:CFSB and YPBH=:YPXH",params);
						if ("1".equals(cf02.get("PSJG") + "")) {
							params.clear();
							params.put("YPXH", cf02.get("YPXH"));
							params.put("JGID", manageUnit);
							params.put("BRID",Long.parseLong(cf01.get("BRID").toString()));
							dao.doSqlUpdate("delete GY_PSJL where BRID=:BRID and JGID=:JGID and PSJG=1 and YPXH=:YPXH",params);
						}
					}
					dao.doRemove(Long.parseLong(cf02.get("SBXH") + ""),BSPHISEntryNames.MS_CF02_CIC);
					continue;
				}
				if (cf02.get(uniqueId) == lastYzzh && YPZH > 0) {
					cf02.put("YPZH", YPZH);
				} else {
					if ("create".equals(cf02.get("_opStatus")) && ((cf02.get("YPZH")==null
						|| cf02.get("YPZH").toString().length()==0
						|| cf02.get("YPZH").toString().equals("0"))
						|| (body.get("saveBy")!=null))) {
						YPZH = Long.parseLong(KeyManager.getKeyByName("MS_CF02", item.getKeyRules(), item.getId(),ctx));
						// 查询医嘱表YZZH最大值
						cf02.put("YPZH",YPZH);
					} else {
						if (body.get("saveBy") == null) {
							YPZH = Long.parseLong(cf02.get("YPZH").toString());
						}
					}
					lastYzzh = Integer.parseInt(cf02.get(uniqueId).toString());
				}
				// 判断库存是否足够
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("pharmId", cf01.get("YFSB"));
				data.put("medId", cf02.get("YPXH"));
				data.put("medsource", cf02.get("YPCD"));
				// 判断药品数量是否为空 *******
				if (cf02.get("YPSL") == null || cf02.get("YPSL").toString().trim().length()==0) {
					cf02.put("YPSL", getYPSL(cf02));
				}
				if (cf01.get("CFLX").toString().equals("3")) {
					cf01.put("YYTS", 1);
					data.put("quantity",Double.parseDouble(cf02.get("YPSL").toString())*(Integer)cf01.get("CFTS"));
				} else {
					data.put("quantity",cf02.get("YPSL"));
				}
				if (cf02.get("_opStatus") != null && cf02.get("_opStatus").equals("update")) {
					data.put("jlxh",cf02.get("SBXH"));
				}
				data.put("lsjg",cf02.get("YPDJ"));
				/**********add by lizhi医生开处方标志*************/
				data.put("isYscf", true);
				/**********add by lizhi医生开处方标志*************/
				if (cf02.get("ZFYP") == null) {
					cf02.put("ZFYP", 0);
				}
				if (Integer.parseInt(cf02.get("YPXH") + "")!=0 && Integer.parseInt(cf02.get("ZFYP") + "")!= 1) {
					Map<String, Object> inventory = BSPHISUtil.checkInventory(data,dao,ctx);
					if ((Integer) inventory.get("sign") == -1) {
						throw new ModelDataOperationException("药品【"+cf02.get("YPMC")+"】库存数量不足，库存数量："
								+inventory.get("KCZL")+",实际数量："+data.get("quantity"));
					}
				}
				//南京金保判断处方明细是否对照了自编码
				if(brxz.equals("2000")){
					Map<String, Object> p=new HashMap<String, Object>();String jgid = (String) user.getManageUnitId();// 用户的机构ID
					String zbmjgid="";
					Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
					zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
					p.put("JGID", zbmjgid);
					p.put("YPXH", Long.parseLong(cf02.get("YPXH")+""));
					p.put("YPCD", Long.parseLong(cf02.get("YPCD")+""));
					List<Map<String, Object>> ml=dao.doQuery("select YYZBM as YYZBM from YK_CDXX where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD",p);
					if(ml!=null && ml.size()>0){
						Map<String, Object> ybyp = ml.get(0);
						if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+""))){//自编码为空
							if(njjbcfmxmsg.length()==0){
								njjbcfmxmsg+="处方中"+cf02.get("YPMC");
							}else{
								njjbcfmxmsg+="、"+cf02.get("YPMC");
							}
						}
					}
				}
				if (cf02.get("_opStatus") == null || cf02.get("_opStatus").equals("create")) {// 新建
					cf02.put("JGID", manageUnit);
					cf02.put("CFSB", cfsb);
					cf02.put("XMLX", cf02.get("TYPE"));
					cf02.put("CFTS", cf01.get("CFTS"));
					double HJJE = Double.parseDouble(cf02.get("YPDJ").toString())
							*Double.parseDouble(cf02.get("YPSL").toString())
							*(Integer) cf02.get("CFTS");
					cf02.put("HJJE", HJJE);
					cf02.put("YCSL", "0");
					cf02.put("FYGB", BSPHISUtil.getfygb(Long.parseLong(cf02.get("TYPE")+""),
							Long.parseLong(cf02.get("YPXH")+""),dao,ctx));
					Object ypzs = (cf02.get("YPZS"));
					if (ypzs == null || ypzs.toString().equals("0")) {
						cf02.put("YPZS", null);
					}
					Map<String, Object> map_sbxh = dao.doSave("create",BSPHISEntryNames.MS_CF02_CIC, cf02, false);
					cf02.put("SBXH", map_sbxh.get("SBXH"));
				} else if (cf02.get("_opStatus").equals("update")) {
					Object ypzs = (cf02.get("YPZS"));
					if (ypzs == null || ypzs.toString().equals("0")) {
						cf02.put("YPZS", "");
					}
					cf02.put("CFTS", cf01.get("CFTS"));
					double HJJE = Double.parseDouble(cf02.get("YPDJ").toString())
							* Double.parseDouble(cf02.get("YPSL").toString())*(Integer) cf02.get("CFTS");
					cf02.put("HJJE", HJJE);
					dao.doSave("update", BSPHISEntryNames.MS_CF02_CIC, cf02,false);
				}
				// 增加皮试保存功能
				if ("1".equals(cf02.get("saveSkinTest") + "")) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("BRID", cf01.get("BRID"));
					m.put("YPXH", cf02.get("YPXH"));
					m.put("CFSB", cf02.get("CFSB"));
					saveGetSkinTest(m, ctx);
				}

				// 附加项目
				// 更换组的时候判断下是否有附加项目需要保存
				if (fjxxs != null && fjxxs.size() > 0) {
					if ((i+1 >= cf02s.size()) || cf02s.get(i + 1).get(uniqueId) != lastYzzh) {
						uniqueId = uniqueId.equals("YPZH") ? "YPZH": "uniqueId";
						// 判断医技01是否存在
						parameters.clear();
						Map<String, Object> ms_yj01 = null;
						if (uniqueId.equals("YPZH")) {
							parameters.put("YPZH", Long.parseLong(cf02.get("YPZH").toString()));
							ms_yj01=dao.doLoad("select YJXH as YJXH from MS_YJ01 where FJGL=:YPZH and FJLB=2",parameters);
						}
						Long yjxh=0l;
						if (ms_yj01==null) {// 不存在医技
							ms_yj01=new HashMap<String, Object>();
							ms_yj01.put("JGID", manageUnit);
							ms_yj01.put("BRID", cf01.get("BRID"));
							ms_yj01.put("BRXM", cf01.get("BRXM"));
							ms_yj01.put("KDRQ", new Date());
							ms_yj01.put("KSDM", cf01.get("KSDM"));
							ms_yj01.put("YSDM", user.getUserId() + "");// 获取员工代码暂时用该方法替代
							ms_yj01.put("ZXKS", cf01.get("KSDM"));
							ms_yj01.put("ZXPB", 0);
							ms_yj01.put("ZFPB", 0);
							ms_yj01.put("CFBZ", 0);
							ms_yj01.put("JZXH", body.get("JZXH"));
							ms_yj01.put("FJGL", YPZH);
							ms_yj01.put("FJLB", 2);
							ms_yj01.put("DJZT", 0);
							Map<String, Object> keyGen = dao.doSave("create",BSPHISEntryNames.MS_YJ01_CIC,ms_yj01,false);
							yjxh = (Long) keyGen.get("YJXH");
							yjxhs.add(yjxh);
						} else {
							yjxh = (Long) ms_yj01.get("YJXH");
							yjxhs.add(yjxh);
							ms_yj01.put("CFBZ", cf01.get("CFBZ"));
							dao.doSave("update", BSPHISEntryNames.MS_YJ01_CIC,ms_yj01, false);// 规定病种
						}
						int num = 0;
						Map<String, Object> payArgs = new HashMap<String, Object>();
						payArgs.put("TYPE", 0);
						payArgs.put("BRXZ", cf01.get("BRXZ"));
						for (Map<String, Object> fjxx : fjxxs) {
							if (!fjxx.get("_opStatus").equals("remove") && !fjxx.get("_opStatus").equals("deal")) {
								if ((fjxx.get(uniqueId) + "").equals(cf02.get(uniqueId) + "")) {
									String ops = "create";
									if (fjxx.get("SBXH") != null && fjxx.get("SBXH").toString().trim().length() > 0) {
										ops = "update";
									}
									if (ops.equals("create")) {// MS_YJ02
										Map<String, Object> ms_yj02 = new HashMap<String, Object>();
										ms_yj02.put("JGID", manageUnit);
										ms_yj02.put("YJXH", yjxh);
										ms_yj02.put("YLXH", fjxx.get("YLXH"));
										ms_yj02.put("XMLX", 0);
										ms_yj02.put("YJZX", (num == 0 ? 1 : 0));
										ms_yj02.put("YLDJ", fjxx.get("YLDJ"));
										ms_yj02.put("YLSL", fjxx.get("YLSL"));
										ms_yj02.put("HJJE", fjxx.get("HJJE"));
										ms_yj02.put("FYGB",BSPHISUtil.getfygb(0l,Long.parseLong(fjxx.get("YLXH").toString()),dao,ctx));
										payArgs.put("FYXH", fjxx.get("YLXH"));
										payArgs.put("FYGB", ms_yj02.get("FYGB"));
										payArgs.put("BRXZ", body.get("BRXZ"));
										ms_yj02.put("ZFBL",BSPHISUtil.getPayProportion(payArgs, ctx, dao).get("ZFBL"));
										ms_yj02.put("DZBL", 0.0);
										ms_yj02.put("YJZH", 0);
										dao.doSave("create",BSPHISEntryNames.MS_YJ02_CIC,ms_yj02, false);
									} else {// 更新MS_YJ02
										Map<String, Object> ms_yj02 = new HashMap<String, Object>();
										ms_yj02.put("SBXH", fjxx.get("SBXH"));
										ms_yj02.put("YLXH", fjxx.get("YLXH"));
										ms_yj02.put("YJZX", (num == 0 ? 1 : 0));
										ms_yj02.put("YLDJ", fjxx.get("YLDJ"));
										ms_yj02.put("YLSL", fjxx.get("YLSL"));
										ms_yj02.put("HJJE", fjxx.get("HJJE"));
										payArgs.put("FYXH", fjxx.get("YLXH"));
										payArgs.put("FYGB", fjxx.get("FYGB"));
										payArgs.put("BRXZ", body.get("BRXZ"));
										ms_yj02.put("ZFBL",BSPHISUtil.getPayProportion(payArgs, ctx, dao).get("ZFBL"));
										dao.doSave("update",BSPHISEntryNames.MS_YJ02_CIC,ms_yj02, false);
									}
									num++;
									fjxx.put("_opStatus","deal");
								}
							}
						}
					}
				}
			}

			if(kssxzmsg.length() > 0 ){
				kssxzmsg="本院启用了抗生素使用限制，您的抗生素权限为"+kssqx+"级，而药品："+kssxzmsg+"高于您的权限，请联系医院管理员设置";
				res.put("kssxzmsg", kssxzmsg);
			}
			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manageUnit, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manageUnit, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
			if (SFQYYFYFY == 1 && MZKCDJSJ == 1) {// 如果启用库存冻结,并且在开单时冻结
				// 先删除过期的冻结库存
//				MedicineCommonModel model = new MedicineCommonModel(dao);
//				model.deleteKCDJ(manageUnit, ctx);
				// 先删除对应CFSB的冻结记录
				StringBuffer hql_kcdj_delete = new StringBuffer();
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_delete = new HashMap<String, Object>();
				map_par_delete.put("cfsb",MedicineUtils.parseLong(cf01.get("CFSB")));
				dao.doSqlUpdate(hql_kcdj_delete.toString(), map_par_delete);
				for (Map<String, Object> map_cf02 : cf02s) {
					if (map_cf02.get("_opStatus") != null && map_cf02.get("_opStatus").equals("remove")) {
						continue;
					}
					if (MedicineUtils.parseInt(map_cf02.get("ZFYP")) == 1) {
						continue;
					}
					Map<String, Object> map_kcdj = new HashMap<String, Object>();
					map_kcdj.put("JGID",manageUnit);
					map_kcdj.put("YFSB",MedicineUtils.parseLong(cf01.get("YFSB")));
					map_kcdj.put("CFSB",MedicineUtils.parseLong(cf01.get("CFSB")));
					map_kcdj.put("YPXH",MedicineUtils.parseLong(map_cf02.get("YPXH")));
					map_kcdj.put("YPCD",MedicineUtils.parseLong(map_cf02.get("YPCD")));
					map_kcdj.put("YPSL",MedicineUtils.simpleMultiply(2,map_cf02.get("YPSL"), map_cf02.get("CFTS")));
					map_kcdj.put("YFBZ",MedicineUtils.parseInt(map_cf02.get("YFBZ")));
					map_kcdj.put("DJSJ",new Date());
					map_kcdj.put("JLXH",MedicineUtils.parseLong(map_cf02.get("SBXH")));
					dao.doSave("create","phis.application.pha.schemas.YF_KCDJ", map_kcdj,false);
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
			// 删除YJ02为空的YJ01
			if (fjxxs != null) {
				if (yjxhs.size() > 0) {
					parameters.clear();
					String YJXHStr = "(";
					for (int i = 0; i < yjxhs.size(); i++) {
						if (i == 0) {
							YJXHStr += yjxhs.get(i);
						} else {
							YJXHStr += "," + yjxhs.get(i);
						}
					}
					YJXHStr += ")";
					dao.doSqlUpdate("DELETE FROM MS_YJ01 a WHERE YJXH in "+ YJXHStr+
									" AND 0=(SELECT COUNT(1) FROM MS_YJ02 b WHERE b.YJXH=a.YJXH)",null);
					List<Map<String, Object>> YJXHS = dao.doQuery("select YJXH as YJXH from MS_YJ01 "
									+ " where YJXH in " + YJXHStr,null);
					if (YJXHS != null) {
						res.put("YJXH", YJXHS);
					}
				}
			}
			if(njjbcfmxmsg.length() >0){
				res.put("njjbcfmxmsg",njjbcfmxmsg+"未对照南京金保自编码，将导致收费处不能正常结算！");
			}
			res.put("CFHM", cf01.get("CFSB"));
			return res;
		} catch (ValidateException e) {
			throw new ModelDataOperationException("处方信息校验失败!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("处方信息保存失败!", e);
		} catch (NumberFormatException e) {
			throw new ModelDataOperationException("处方信息保存失败!", e);
		} catch (KeyManagerException e) {
			throw new ModelDataOperationException("获取处方主键失败!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(e.getMessage(), e);
		}
	}

	/**
	 * 获取处方录入模块公共信息参数 1、门诊发药药房 2、医生处方权限 ...
	 *
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadOutClinicInitParams(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		try {
			// 门诊处方权限信息获取
			String cf_hql = "SELECT PERSONNAME as YGXM, PRESCRIBERIGHT as KCFQ,  NARCOTICRIGHT as MZYQ,ANTIBIOTICLEVEL as KSSQX  FROM SYS_Personnel WHERE PERSONID=:YGDM";
			Map<String, Object> params = new HashMap<String, Object>();

			params.put("YGDM", user.getUserId());
			res.put("MZ_YSQX", dao.doLoad(cf_hql, params));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("处方信息保存失败!", e);
		}
		return res;
	}

	public Map<String, Object> doQueryMsBcjl(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String JZXH = body.get("JZXH") + "";
		String BRID = body.get("BRID") + "";
		try {
			String hql = "select SSY as SSY, SZY as SZY from MS_BCJL where JGID=:JGID and JZXH=:JZXH and BRID=:BRID";
			params.put("JZXH", Long.parseLong(JZXH));
			params.put("BRID", Long.parseLong(BRID));
			params.put("JGID", manageUnit);
			Map<String,Object> bcjl = dao.doLoad(hql, params);
			res.put("MS_BCJL", bcjl);

			if(bcjl!=null && bcjl.get("SSY")==null && bcjl.get("SZY")==null){//查询首诊测压，如果测量过：更新MS_BCJL
				String brdaHql = "select EMPIID as EMPIID from MS_BRDA where BRID=:BRID";
				params.clear();
				params.put("BRID", BRID);
				Map<String, Object> brda = dao.doLoad(brdaHql, params);
				if(brda!=null && brda.get("EMPIID")!=null){
					String empiId = brda.get("EMPIID")+"";
					//根据empiId查询首诊测压
					String fcbpHql = "select constriction as CONSTRICTION,diastolic as DIASTOLIC from MDC_Hypertension_FCBP where EMPIID=:EMPIID";
					params.clear();
					params.put("EMPIID", empiId);
					List<Map<String,Object>> fcbpList = dao.doQuery(fcbpHql, params);
					if(fcbpList.size()==1){
						Map<String,Object> fcbpMap = fcbpList.get(0);
						int constriction = 0;
						int diastolic = 0;
						if(fcbpMap!=null && fcbpMap.get("CONSTRICTION")!=null){
							constriction = Integer.parseInt(fcbpMap.get("CONSTRICTION")+"");
						}
						if(fcbpMap!=null && fcbpMap.get("DIASTOLIC")!=null){
							diastolic = Integer.parseInt(fcbpMap.get("DIASTOLIC")+"");
						}
						if(constriction>0 && diastolic>0){//更新MS_BCJL
							String bcjlhql = new StringBuffer("update MS_BCJL set SSY=:SSY, SZY=:SZY")
							.append(" where JZXH=:JZXH and BRID=:BRID")
							.toString();
							Map<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("SSY", constriction);
							parameters.put("SZY", diastolic);
							parameters.put("JZXH", Long.parseLong(JZXH));
							parameters.put("BRID", Long.parseLong(BRID));
							dao.doUpdate(bcjlhql, parameters);
						}
					}else if(fcbpList.size()==0){
						res.put("errorMsg", "病人超过35岁,必须测量血压！！！");
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("MS_BCJL信息保存失败!", e);
		}
		return res;
	}

	public void doSaveMsBcjl(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		Map<String, Object> JKCF = (Map<String, Object>) body.get("JKCF");
		String JZXH = body.get("JZXH") + "";
		String YBDM = body.get("YBDM") + "";
		String MSBZ = body.get("MSBZ") + "";
		body.remove(JKCF);
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		try {
			params.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			long l = dao.doCount("MS_BCJL", "JZXH=:JZXH", params);
			 String hqlz = "select b.paysource from ms_ghmx b where b.sbxh=(select ghxh from YS_MZ_JZLS a where a.jzxh=" +
			 		JZXH +
			 		" and rownum=1)";
				List<Map<String, Object>> listz = dao.doSqlQuery(hqlz, null);
						String qhpd = listz.get(0) + "";
			if(qhpd.equals("2")){//add by zhaohj  增加门慢门特功能
				dao.doUpdate(
						"update MS_GHMX set njjbyllb=" +
								MSBZ +
						" ,ybmc=" +
						YBDM +
						" where  SBXH=(select GHXH from YS_MZ_JZLS "
								+ " where JZXH=:JZXH)", params);}
			String op = l > 0 ? "update" : "create";
			body.put("JZYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
			body.put("JZKS", user.getProperty("biz_departmentId", Long.class));
			body.put("JGID", manageUnit);
			body.put("BLLX", 1);
			body.put("DYBZ", 0);
			//add by lizhi 2017-11-22高血压首诊测压
//			Map<String, Object> bcBody = dao.doLoad(
//					"phis.application.cic.schemas.MS_BCJL", Long.parseLong(body.get("JZXH").toString()));
//			if(bcBody!=null){
//				if(bcBody.get("SSY")!=null && !"".equals(bcBody.get("SSY")+"")){
//					body.put("SSY", Long.parseLong(bcBody.get("SSY")+""));
//				}
//				if(bcBody.get("SZY")!=null && !"".equals(bcBody.get("SZY")+"")){
//					body.put("SZY", Long.parseLong(bcBody.get("SZY")+""));
//				}
//			}
			//处理既往史
			body = this.doJWS(body,op);
			//处理主要症状
			body = this.doZYZZ(body,op);
			Map<String, Object> reBody = dao.doSave(op,
					BSPHISEntryNames.MS_BCJL, body, false);

			String BRID = body.get("BRID") + "";
			Map<String, Object> empiIdMap = dao.doLoad(
					"phis.application.cic.schemas.MS_BRDA", BRID);
			String empiId = empiIdMap.get("EMPIID") + "";

			List cnd = CNDHelper.createSimpleCnd("eq", "wayId", "s", JZXH);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					"phis.application.cic.schemas.HER_HealthRecipeRecord_MZ");
			for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
				boolean isUpdate = false;
				for (Map<String, Object> map : list) {
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						m.put("id", map.get("id"));
						isUpdate = true;
						break;
					}
				}
				Map<String, Object> mBody = new HashMap<String, Object>();
				mBody.put("empiId", empiId);
				mBody.put("recordId", m.get("recordId"));
				mBody.put("wayId", JZXH);
				mBody.put("recipeName", m.get("recipeName"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", user.getManageUnitName());
				mBody.put("guideDate", new Date());
				mBody.put("guideUser", user.getUserId() + "");
				mBody.put("guideWay", "01");
				mBody.put("lastModifyUnit", manageUnit);
				mBody.put("lastModifyDate", new Date());
				mBody.put("lastModifyUser", user.getUserId() + "");
				if (isUpdate == true) {
					mBody.put("id", m.get("id"));
					updateList.add(mBody);
				} else {
					mBody.put("inputUnit", manageUnit);
					mBody.put("inputDate", new Date());
					mBody.put("inputUser", user.getUserId() + "");
					createList.add(mBody);
				}
			}
			for (Map<String, Object> map : list) {
				boolean isRemove = true;
				for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						isRemove = false;
						break;
					}
				}
				if (isRemove == true) {
					removeList.add(map);
				}
			}
			list.clear();
			for (Map<String, Object> map : createList) {
				Map<String, Object> result = dao
						.doSave("create",
								"phis.application.cic.schemas.HER_HealthRecipeRecord_MZ",
								map, true);
				map.put("childId", result.get("id"));
				map.put("op", "create");
				map.put("action","saveHealthRecipeRecord");
				//yx-2017-11-23-由于公卫和医疗的主键生成方式不一样造成主键冲突，改成去公卫保存-b
				try {
					ServiceAdapter.invoke("chis.logonInfoLoader", "execute", map );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				dao.doSave("create",
//						"phis.application.cic.schemas.HER_HealthRecipeRecord",
//						map, true);
				//yx-2017-11-23-由于公卫和医疗的主键生成方式不一样造成主键冲突，改成去公卫保存-e
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update","phis.application.cic.schemas.HER_HealthRecipeRecord_MZ",
								map, true);
				String hql = new StringBuffer(" from HER_HealthRecipeRecord where childId = :childId and guideWay='01'").toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> result1=dao.doQuery(hql, parameters);
				if(result1!=null && result1.size() >0 ){
					Map<String, Object> result2 = result1.get(0);
					String zjId = result2.get("id") + "";
					result2.putAll(map);
					result2.put("id", zjId);
					dao.doSave("update","phis.application.cic.schemas.HER_HealthRecipeRecord",result2, true);
					result.putAll(map);
				}else{
					try {
						map.put("childId", map.get("id"));
						map.put("op", "create");
						map.put("action","saveHealthRecipeRecord");
						ServiceAdapter.invoke("chis.logonInfoLoader", "execute", map );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove(map.get("id"),
						"phis.application.cic.schemas.HER_HealthRecipeRecord_MZ");
				String hql = new StringBuffer(" from HER_HealthRecipeRecord")
						.append(" where childId = :childId and guideWay='01'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> result1=dao.doQuery(hql, parameters);
				if(result1!=null && result1.size() >0 ){
					Map<String, Object> result2 = result1.get(0);
					String zjId = result2.get("id") + "";
					dao.doRemove(zjId,"phis.application.cic.schemas.HER_HealthRecipeRecord");
				}
			}
			reBody.put("JKCFRecords", list);

			String hql_zd = "select a.ICD10 as ICD10,b.JKCF  as JKCF from MS_BRZD a left join GY_JBBM b on a.ICD10 = b.ICD10 where a.jzxh=" +
					JZXH +"and ZZBZ=1 order by PLXH";
			List<Map<String, Object>> list_zd = dao.doSqlQuery(hql_zd, null);
			reBody.put("reShowJkjyView", "NO");
			reBody.put("ICD10", "");
			String JKJYNR = body.get("JKJYNR") + "";
			//当主诊断 选择的疾病 JKCF 为1，且保存时  健康教育文本域内容是空时   重新弹出“引入健康教育弹窗”
			if(list_zd!=null && list_zd.size()>0 && "1".equals(list_zd.get(0).get("JKCF")) &&("".equals(JKJYNR) || "null".equals(JKJYNR))){
				reBody.put("reShowJkjyView", "YES");
				reBody.put("ICD10", list_zd.get(0).get("ICD10"));
			}

			res.put("body", reBody);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to save ms_bcjl information by database reason",
					e);
			throw new ModelDataOperationException("病历信息保存失败!", e);
		} catch (ValidateException e) {
			logger.error("validate fail to save ms_bcjl", e);
			throw new ModelDataOperationException("病历信息保存失败!", e);
		}
	}

	private Map<String, Object> doZYZZ(Map<String, Object> body, String op) throws PersistentDataOperationException, ValidateException {
		Map<String,Object> ZYZZ = new HashMap<String, Object>();
		ZYZZ.put("BRID",MedicineUtils.parseLong(body.get("BRID")));
		ZYZZ.put("JZXH",MedicineUtils.parseLong(body.get("JZXH")));
		if(body.get("wzz")!=null&&"1".equals(body.get("wzz").toString())){
			return body;
		}else {
			ZYZZ.put("qszz_1",body.get("qszz_1"));
			ZYZZ.put("qszz_2",body.get("qszz_2"));
			ZYZZ.put("qszz_3",body.get("qszz_3"));
			ZYZZ.put("qszz_4",body.get("qszz_4"));
			ZYZZ.put("qszz_5",body.get("qszz_5"));
			ZYZZ.put("hxdzz_1",body.get("hxdzz_1"));
			ZYZZ.put("hxdzz_2",body.get("hxdzz_2"));
			ZYZZ.put("hxdzz_3",body.get("hxdzz_3"));
			ZYZZ.put("hxdzz_4",body.get("hxdzz_4"));
			ZYZZ.put("hxdzz_5",body.get("hxdzz_5"));
			ZYZZ.put("hxdzz_6",body.get("hxdzz_6"));
			ZYZZ.put("hxdzz_7",body.get("hxdzz_7"));
			ZYZZ.put("xhdzz_1",body.get("xhdzz_1"));
			ZYZZ.put("xhdzz_2",body.get("xhdzz_2"));
			ZYZZ.put("xhdzz_3",body.get("xhdzz_3"));
			ZYZZ.put("pzzz_1",body.get("pzzz_1"));
			ZYZZ.put("pzzz_2",body.get("pzzz_2"));
			ZYZZ.put("pzzz_3",body.get("pzzz_3"));
			ZYZZ.put("pzzz_4",body.get("pzzz_4"));
			ZYZZ.put("cxzz_1",body.get("cxzz_1"));
			ZYZZ.put("cxzz_2",body.get("cxzz_2"));
			ZYZZ.put("cxzz_3",body.get("cxzz_3"));
			ZYZZ.put("cxzz_4",body.get("cxzz_4"));
			ZYZZ.put("cxzz_5",body.get("cxzz_5"));
			ZYZZ.put("cxzz_6",body.get("cxzz_6"));
			ZYZZ.put("sjxtzz_1",body.get("sjxtzz_1"));
			ZYZZ.put("sjxtzz_2",body.get("sjxtzz_2"));
			ZYZZ.put("sjxtzz_3",body.get("sjxtzz_3"));
		}
		dao.doSave(op,"phis.application.cic.schemas.MS_BCJL_ZYZZ",ZYZZ,false);
		return body;
	}

	private Map<String, Object> doJWS(Map<String, Object> body,String op) throws PersistentDataOperationException, ValidateException {
		Map<String, Object> JWS = new HashMap<String, Object>();
		JWS.put("JZXH",body.get("JZXH"));
		int JWS_FRMXJBS = MedicineUtils.parseInt(body.get("JWS_FRMXJBS"));
		int JWS_GXY = MedicineUtils.parseInt(body.get("JWS_GXY"));
		int JWS_TNB = MedicineUtils.parseInt(body.get("JWS_TNB"));
		int JWS_GXB = MedicineUtils.parseInt(body.get("JWS_GXB"));
		int JWS_NGS = MedicineUtils.parseInt(body.get("JWS_NGS"));
		int JWS_NCX = MedicineUtils.parseInt(body.get("JWS_NCX"));
		int JWS_XXGZBDFY = MedicineUtils.parseInt(body.get("JWS_XXGZBDFY"));
		int JWS_QT = MedicineUtils.parseInt(body.get("JWS_QT"));
		body.remove("JWS_FRMXJBS");
		body.remove("JWS_GXY");
		body.remove("JWS_TNB");
		body.remove("JWS_GXB");
		body.remove("JWS_NGS");
		body.remove("JWS_NCX");
		body.remove("JWS_XXGZBDFY");
		body.remove("JWS_QT");
		JWS.put("JWS_FRMXJBS",JWS_FRMXJBS);
		JWS.put("JWS_GXY",JWS_GXY);
		JWS.put("JWS_TNB",JWS_TNB);
		JWS.put("JWS_GXB",JWS_GXB);
		JWS.put("JWS_NGS",JWS_NGS);
		JWS.put("JWS_NCX",JWS_NCX);
		JWS.put("JWS_XXGZBDFY",JWS_XXGZBDFY);
		JWS.put("JWS_QT",JWS_QT);
		if(JWS_QT ==1){
			String JWS_QT_TEXT = MedicineUtils.parseString(body.get("JWS_QT_TEXT"));
			JWS.put("JWS_QT_TEXT",JWS_QT_TEXT);
			body.remove("JWS_QT_TEXT");
		}
		dao.doSave(op,"phis.application.cic.schemas.MS_BCJL_JWS",JWS,false);
		return  body;
	}


	public void doSaveClinicFinish(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		try {
			body.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			// 判断是否有处方处置,若都为0，则不更新就诊状态
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JZXH", body.get("JZXH"));

			long cf_count = dao.doCount(BSPHISEntryNames.MS_CF01_TABLE,
					"JZXH=:JZXH", params);
			long cz_count = dao.doCount(BSPHISEntryNames.MS_YJ01_TABLE,
					"JZXH=:JZXH", params);
			long zd_count = dao.doCount(BSPHISEntryNames.MS_BRZD_TABLE,
					"JZXH=:JZXH", params);
			/** 2014-9-4 modify by yaosq 加健康处方判断 **/
			params.put("JZXH", body.get("JZXH") + "");
			long jkjy_count = dao.doCount(
					BSPHISEntryNames.HER_HealthRecipeRecord_MZ, "wayId=:JZXH",
					params);
			params.put("JZXH", body.get("JZXH"));
			/** 2013-10-11 modify by gejj 去除门诊病历判断 **/
			long hl_count = dao.doCount("MS_BCJL", "JZXH=:JZXH", params);
			// if (cf_count == 0 && cz_count == 0 && zd_count == 0
			// && hl_count == 0) {
			//
			// }
			long omr_count = dao.doCount("OMR_BL01", "JZXH=:JZXH", params);
			/***************delete by lizhi 2017-07-06儿童体检病人，需暂挂后才能结束就诊。**************/
//			if (cf_count == 0 && cz_count == 0 && zd_count == 0
//					&& hl_count == 0 && jkjy_count == 0 && omr_count == 0) {
//				/** 2013-10-11 end **/
//				params.put("JZZT", 0);
//				dao.doUpdate(
//						"update MS_GHMX set JZZT=:JZZT where SBXH=(select GHXH from YS_MZ_JZLS"
//								+ " where JZXH=:JZXH)", params);
//				params.remove("JZZT");
//				dao.doUpdate("delete YS_MZ_JZLS where JZXH=:JZXH and JZZT=1",
//						params);
//				return;
//			}
			/***************delete by lizhi 2017-07-06 儿童体检病人，需暂挂后才能结束就诊。**************/
			/** 增加将病人去向和健康教育存到表MS_BCJL里 add by liuxy */
			Map<String, Object> params3 = new HashMap<String, Object>();
			params3.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			long l = dao.doCount("MS_BCJL", "JZXH=:JZXH", params3);
			if (l <= 0) {
				String op = "create";
				body.put("JZYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
				body.put("JZKS",
						user.getProperty("biz_departmentId", Long.class));
				body.put("JGID", manageUnit);
				body.put("BLLX", 1);
				dao.doSave(op, BSPHISEntryNames.MS_BCJL, body, false);
			}
			Map<String, Object> params2 = new HashMap<String, Object>();
			int brqx = parseInt(body.get("BRQX"));
			String jkjy = body.get("JKJY") + "";
			if (brqx > 0) {
				params2.put("BRQX", brqx);
				params2.put("JKJY", jkjy);
				params2.put("JZXH", body.get("JZXH"));
				// System.out.println("["+body.get("JZXH")+"]");
				// System.out.println("["+brqx+"]");
				String hql = "update MS_BCJL set BRQX=:BRQX,JKJY=:JKJY where JZXH=:JZXH";
				dao.doUpdate(hql, params2);
			}
			params.put("JZZT", body.get("JZZT"));
			dao.doUpdate(
					"update MS_GHMX set JZZT=:JZZT where (JZZT=1 or JZZT=2) and SBXH=(select GHXH from YS_MZ_JZLS "
							+ " where JZXH=:JZXH)", params);
			params.put("JSSJ", new Date());
			//就诊结束不更新医生
//			params.put("YSDM", user.getUserId() + "");// 获取员工代码暂时用该方法替代
			dao.doUpdate(
//					"update YS_MZ_JZLS set JZZT=:JZZT,JSSJ=:JSSJ,YSDM=:YSDM where JZXH=:JZXH and JZZT=1",
					"update YS_MZ_JZLS set JZZT=:JZZT,JSSJ=:JSSJ where JZXH=:JZXH and JZZT=1",
					params);

			/************add by LIZHI 2017-12-28更新紫金数云预约挂号状态：3已就诊**************/
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZXH", body.get("JZXH"));
			Map<String,Object> ghmx = dao.doLoad("select ZJYYID as ZJYYID from MS_GHMX where SBXH=(select GHXH from YS_MZ_JZLS "
							+ " where JZXH=:JZXH)", parameters);
			if(ghmx!=null && ghmx.get("ZJYYID")!=null && !"".equals(ghmx.get("ZJYYID")+"")){
				long yyid = Long.parseLong(ghmx.get("ZJYYID")+"");
				parameters.clear();
				parameters.put("ID", yyid);
				dao.doUpdate("update APPOINTMENT_RECORD set STATUS = 3 where ID=:ID",parameters);
			}
			/************add by LIZHI 2017-12-28更新紫金数云预约挂号状态：3已就诊**************/
			/*
			 * 拱墅版本中的功能暂时隐藏 params.clear(); params.put("JZZT", 1);
			 * params.put("JZXH", Long.parseLong(body.get("JZXH").toString()));
			 * params.put("BRID", body.get("BIRD")); doSavePdJzzt(params, ctx);
			 */
			// dao.doSave("update", BSPHISEntryNames.YS_MZ_JZLS, body, false);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to save ys_mz_jzls information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (Exception e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	public void doRemoveClinicInfo(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			Object pkey = body.get("pkey");
			Map<String, Object> params = new HashMap<String, Object>();
			long cfsb = Long.parseLong(pkey + "");
			params.put("CFSB", cfsb);
			Long l = dao.doCount("MS_CF01",
					"CFSB=:CFSB and MZXH is not null and MZXH>0", params);
			if (l > 0) {
				throw new ModelDataOperationException("当前处方已经收费，不允许编辑!");
			}
			ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(
					dao);
			mmd.removeAllAdditional(Long.parseLong(pkey + ""));
			// 删除皮试信息
			dao.doSqlUpdate("delete from YS_MZ_PSJL where CFSB=:CFSB", params);
			dao.doRemove("CFSB", Long.parseLong(pkey + ""),
					BSPHISEntryNames.MS_CF02_CIC);
			dao.doRemove(Long.parseLong(pkey + ""),
					BSPHISEntryNames.MS_CF01_CIC);
			// 库存冻结代码
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manageUnit, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manageUnit, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
			if (SFQYYFYFY == 1 && MZKCDJSJ == 1) {// 如果启用库存冻结,并且在开单时冻结
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manageUnit, ctx);
				StringBuffer hql_kcdj_delete = new StringBuffer();
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_delete = new HashMap<String, Object>();
				map_par_delete.put("cfsb", MedicineUtils.parseLong(pkey));
				dao.doSqlUpdate(hql_kcdj_delete.toString(), map_par_delete);
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to remove clinic information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	public Map<String, Object> doLoadMedicineInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		Object ypxh = body.get("YPXH");
		// 发药药房 和 药品类别
		Object pharmacyId = body.get("pharmacyId");
		String tabId = (String) body.get("tabId");
		// 药品产地选择方式,库存序号
		try {
			Map<String, Object> med = null;
			if (tabId.equals("clinicCommon")) {
				String orderBy = "ZTBH";
				String schema = "phis.application.cic.schemas.YS_MZ_ZT02_MS";
				String cnds = "['eq',['$','JLBH'],['d'," + body.get("JLBH")
						+ "]]";
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schema);
				if (list.size() > 0) {
					med = list.get(0);
				}
			}
			String zbmjgid="";
			Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("YFSB", Long.parseLong(pharmacyId.toString()));
			params.put("YPXH", Long.parseLong(ypxh.toString()));
			params.put("JGID", zbmjgid);
			StringBuffer hql = new StringBuffer(
					"select b.YFSB as YFSB,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YCJL as YCJL,a.YPJL as YPJL,a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as TYPE,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ,a.KSBZ as KSBZ,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.PSPB as PSPB,a.SFSP as SFSP,a.YCJL as YCJL,decode(e.YYZBM,'',' ','医保可报销') as YYZBM ");
			hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_CDXX e ");
			hql.append(" where  ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND e.JGID=:JGID and e.YPXH=d.YPXH and e.YPCD=d.YPCD AND  b.YFSB=:YFSB  AND a.YPXH = :YPXH ORDER BY d.SBXH");
			List<Map<String, Object>> meds = dao
					.doSqlQuery(hql.toString(), params);
			if (meds.size() > 0) {// 多产地取第一条记录
				Map<String, Object> zt_med = meds.get(0);
				if (tabId.equals("clinicCommon")) {
					zt_med.putAll(med);
				}
				Dictionary dic = DictionaryController.instance().get(
						"phis.dictionary.drugMode");
				if (zt_med.get("GYFF") != null && zt_med.get("GYFF") != "null") {
					String gYFF_text = dic.getText((zt_med.get("GYFF") + ""));
					zt_med.put("GYFF_text", gYFF_text);
				}
				return zt_med;
			} else {
				body.put("errorMsg", "暂无库存!");
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (Exception e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
		return body;
	}

	public List<Map<String, Object>> doLoadPersonalSet(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String) user.getManageUnitId();// 用户的机构ID
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Object ztbh = body.get("ZTBH");
		// 发药药房 和 药品类别
		Object pharmacyId = body.get("pharmacyId");
		// pharmacyId = "1";//需去掉
		String orderBy = "ZTBH";
		String schema = "phis.application.cic.schemas.YS_MZ_ZT02_MS";
		String cnds = "['eq',['$','ZTBH'],['d'," + ztbh + "]]";
		try {
			String zbmjgid="";
			Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnds), orderBy, schema);
			SchemaUtil.setDictionaryMassageForList(list, schema);
			for (Map<String, Object> med : list) {
				Object ypxh = med.get("YPXH");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YFSB", Long.parseLong(pharmacyId.toString()));
				params.put("YPXH", ypxh);
				params.put("JGID", zbmjgid);
				StringBuffer hql = new StringBuffer(
						"select a.FYFS as FYFS,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as TYPE,a.TYPE as YPLX,b.YFSB as YFSB,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ,a.ZBLB as FYGB,a.KSBZ as KSBZ,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.SFSP as SFSP,a.YCJL as YCJL,decode(e.YYZBM,'',' ','医保可报销') as YYZBM ");
				hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,YK_CDXX e ");
				hql.append(" where  ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND e.JGID=:JGID and e.YPXH=d.YPXH and e.YPCD=d.YPCD AND  b.YFSB=:YFSB   AND a.YPXH = :YPXH ORDER BY d.SBXH");
				List<Map<String, Object>> meds = dao.doSqlQuery(hql.toString(),
						params);
				if (meds.size() > 0) {// 取第一条记录
					Map<String, Object> zt_med = meds.get(0);
					// zt_med.putAll(med);
					zt_med.put("YPZH", med.get("YPZH"));
					zt_med.put("YCJL", med.get("YCJL"));
					zt_med.put("YYTS", med.get("YYTS"));
					zt_med.put("YPSL", med.get("XMSL"));
					zt_med.put("HJJE",
							Double.parseDouble(zt_med.get("YPDJ") + "")
									* Double.parseDouble(med.get("XMSL") + ""));
					zt_med.put("YPYF", med.get("SYPC"));
					zt_med.put("YPYF_text", med.get("SYPC_text"));
					zt_med.put("GYTJ", med.get("GYTJ"));
					zt_med.put("GYTJ_text", med.get("GYTJ_text"));
					zt_med.put("BZMC", med.get("BZMC"));
					zt_med.put("MRCS", med.get("MRCS"));
					zt_med.put("SYPC", med.get("SYPC"));
					zt_med.put("SYPC_text", med.get("SYPC_text"));
					// 获取组套中药品自负比例和库存数量
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("TYPE", zt_med.get("TYPE"));
					data.put("FYXH", zt_med.get("YPXH"));
					data.put("BRXZ", body.get("BRXZ"));
					data.put("FYGB", zt_med.get("FYGB"));
					Map<String, Object> zfbl = BSPHISUtil.getPayProportion(
							data, ctx, dao);
					zt_med.put("ZFBL", zfbl.get("ZFBL"));
					// 获取过敏信息改造
					body.put("YPXH", ypxh);
					zt_med.putAll(doLoadDetailsInfo(body, ctx));
					// zt_med.put("YPYF", zt_med.get("SYPC"));
					res.add(zt_med);
				} else {
					med.put("errorMsg", "暂无库存!");
					res.add(med);
				}
			}
			return res;
		} catch (Exception e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	public Map<String, Object> doLoadDetailsInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Long brid = Long.parseLong(body.get("BRID").toString());
		Long ypxh = Long.parseLong(body.get("YPXH").toString());
		params.put("BRID", brid);
		params.put("YPXH", ypxh);
		try {
			List<Map<String, Object>> list = dao.doQuery(
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
		return ret;
	}

	public void doLoadMedcineSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ControllerException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		long ztbh = Long.parseLong(body.get("YPXH") + "");
		try {
			String zbmjgid="";
			Dictionary njjb;
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(manageUnit).getProperty("zbmjgid")+"";
			List<Map<String, Object>> rebody = new ArrayList<Map<String, Object>>();
			// 发药药房 和 药品类别
			Object pharmacyId = body.get("pharmacyId");
			// pharmacyId = "1";//需去掉
			String orderBy = "ZTBH";
			String schema = "phis.application.cic.schemas.YS_MZ_ZT02_MS";
			String cnds = "['eq',['$','ZTBH'],['d'," + ztbh + "]]";
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnds), orderBy, schema);
			SchemaUtil.setDictionaryMassageForList(list, schema);
			for (Map<String, Object> med : list) {
				Object ypxh = med.get("YPXH");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YFSB", Long.parseLong(pharmacyId.toString()));
				params.put("YPXH", ypxh);
				StringBuffer hql = new StringBuffer("select a.YBFL as YBFL,"+ pharmacyId+ "as YFSB,a.YPXH as YPXH," +
						" a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL," +
						" a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text," +
						" a.TYPE as TYPE,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ,a.ZBLB as FYGB," +
						" a.KSBZ as KSBZ,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.ZBLB as FYGB," +
						" nvl(h.MCSX,h.SFMC) as GBMC,d.YPSL as YPSL,a.NHBM_BSOFT as NHBM_BSOFT,i.YYZBM as YYZBM ");
				hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f,GY_SFXM h,YK_CDXX i ");
				hql.append(" where h.SFXM = a.ZBLB and ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND (d.YPCD=i.YPCD) AND (d.YPXH=i.YPXH) AND (i.JGID='"+zbmjgid+"') AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND  b.YFSB=:YFSB   AND a.YPXH = :YPXH ORDER BY d.SBXH");
				List<Map<String, Object>> meds = dao.doQuery(hql.toString(),
						params);
				if (meds.size() > 0) {
					Map<String, Object> zt_med = meds.get(0);// 默认取第一条记录
					for (Map<String, Object> i_med : meds) {// 如果有库存数量大于所需数量的，就取那个。
						i_med.put("pharmId", i_med.get("YFSB"));
						i_med.put("medId", i_med.get("YPXH"));
						i_med.put("medsource", i_med.get("YPCD"));
						i_med.put("quantity", med.get("XMSL"));
						i_med.put("lsjg", i_med.get("LSJG"));
						Map<String, Object> yfck = BSPHISUtil.checkInventory(i_med, dao, ctx);
						if (Integer.parseInt(yfck.get("sign") + "") > 0) {
							zt_med = i_med;
							break;
						}
					}
					Integer ybfl = Integer.parseInt(zt_med.get("YBFL") + "");
					String yBFL_text = "";
					if (ybfl == 1) {
						yBFL_text = "甲";
					} else if (ybfl == 2) {
						yBFL_text = "乙";
					} else if (ybfl == 2) {
						yBFL_text = "丙";
					}
					zt_med.put("YPMC","(" + yBFL_text + ")" + zt_med.get("YPMC"));
					zt_med.put("YPZH", med.get("YPZH"));
					zt_med.put("YCJL", med.get("YCJL"));
					zt_med.put("YYTS", med.get("YYTS"));
					zt_med.put("YPSL", med.get("XMSL"));
					zt_med.put("HJJE",
							Double.parseDouble(zt_med.get("YPDJ") + "")
									* Double.parseDouble(med.get("XMSL") + ""));
					zt_med.put("YPYF", med.get("SYPC"));
					zt_med.put("YPYF_text", med.get("SYPC_text"));
					zt_med.put("GYTJ", med.get("GYTJ"));
					zt_med.put("GYTJ_text", med.get("GYTJ_text"));
					zt_med.put("BZMC", med.get("BZMC"));
					zt_med.put("MRCS", med.get("MRCS"));
					zt_med.put("SYPC", med.get("SYPC"));
					zt_med.put("SYPC_text", med.get("SYPC_text"));
					// 获取组套中药品自负比例和库存数量
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("TYPE", zt_med.get("TYPE"));
					data.put("CFLX", zt_med.get("TYPE"));
					data.put("FYXH", zt_med.get("YPXH"));
					data.put("BRXZ", body.get("BRXZ"));
					data.put("FYGB", zt_med.get("FYGB"));
					Map<String, Object> zfbl = BSPHISUtil.getPayProportion(
							data, ctx, dao);
					zt_med.put("ZFBL", zfbl.get("ZFBL"));
					// zt_med.put("YPYF", zt_med.get("SYPC"));
					zt_med.put("NHBM_BSOFT", zt_med.get("NHBM_BSOFT"));
					if(zt_med.get("YYZBM") != null && !(zt_med.get("YYZBM")+"").equals("")){
						zt_med.put("YYZBM", "医保可报销");
					}else{
						zt_med.put("YYZBM", "");
					}
					rebody.add(zt_med);
				} else {
					med.put("msg", "药品【" + med.get("YPMC") + "】库存不足!");
					rebody.add(med);
				}
			}
			res.put("body", rebody);
		} catch (ExpException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	public void doLoadProjectSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ControllerException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long ztbh = Long.parseLong(body.get("YPXH") + "");
		try {
			String zbmjgid="";
			Dictionary njjb;
			njjb = DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(jgid).getProperty("zbmjgid")+"";
			List<Map<String, Object>> rebody = new ArrayList<Map<String, Object>>();
			// 发药药房 和 药品类别
			// pharmacyId = "1";//需去掉
			String orderBy = "ZTBH";
			String schema = "phis.application.cic.schemas.YS_MZ_ZT02_XM";
			String cnds = "['eq',['$','ZTBH'],['d'," + ztbh + "]]";
			List<Map<String, Object>> list = dao.doList(
					CNDHelper.toListCnd(cnds), orderBy, schema);
			SchemaUtil.setDictionaryMassageForList(list, schema);
			for (Map<String, Object> med : list) {
				Object ypxh = Long.parseLong(med.get("XMBH") + "");
				Map<String, Object> params = new HashMap<String, Object>();
				// params.put("YFSB", Long.parseLong(pharmacyId.toString()));
				params.put("YPXH", ypxh);
				params.put("JGID", jgid);
				StringBuffer hql = new StringBuffer(
						" select b.FYXH as YPXH,a.FYMC as YPMC,b.FYDJ as LSJG,b.FYDJ as YPDJ,a.FYGB as FYGB," +
						" a.FYDW as YFDW,'0' as TYPE,a.BZJG,a.XMLX as XMLX,b.FYKS as FYKS," +
						" nvl(c.MCSX,c.SFMC) as GBMC,a.NHBM_BSOFT as NHBM_BSOFT,d.YYZBM as YYZBM ");
				hql.append(" from GY_YLSF a,GY_YLMX b,GY_SFXM c,GY_YLMX d ");
				hql.append(" where c.SFXM = a.FYGB and a.FYXH = :YPXH AND  a.FYXH = b.FYXH and b.JGID=:JGID and a.FYXH=d.FYXH and d.ZFPB=0 and d.JGID='"+zbmjgid+"'");
				List<Map<String, Object>> meds = dao.doQuery(hql.toString(),
						params);
				if (meds.size() > 0) {// 取第一条记录
					Map<String, Object> zt_med = meds.get(0);
					// zt_med.putAll(med);
					zt_med.put("YPZH", med.get("YPZH"));
					zt_med.put("YPSL", med.get("XMSL"));
					zt_med.put("HJJE",
							Double.parseDouble(zt_med.get("LSJG") + "")
									* Double.parseDouble(med.get("XMSL") + ""));
					zt_med.put("YPYF", med.get("SYPC"));
					zt_med.put("YPYF_text", med.get("SYPC_text"));
					zt_med.put("GYTJ", med.get("GYTJ"));
					zt_med.put("GYTJ_text", med.get("GYTJ_text"));
					zt_med.put("BZMC", med.get("BZMC"));
					zt_med.put("MRCS", med.get("MRCS"));
					zt_med.put("SYPC", med.get("SYPC"));
					zt_med.put("SYPC_text", med.get("SYPC_text"));
					// 获取组套中药品自负比例和库存数量
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("TYPE", zt_med.get("TYPE"));
					data.put("FYXH", zt_med.get("YPXH"));
					data.put("BRXZ", body.get("BRXZ"));
					data.put("FYGB", zt_med.get("FYGB"));
					Map<String, Object> zfbl = BSPHISUtil.getPayProportion(
							data, ctx, dao);
					zt_med.put("ZFBL", zfbl.get("ZFBL"));
					if(zt_med.get("YYZBM") != null && !(zt_med.get("YYZBM")+"").equals("")){
						zt_med.put("YYZBM", "医保可报销");
					}else{
						zt_med.put("YYZBM", "");
					}
					rebody.add(zt_med);
				} else {
					med.put("msg", "项目【" + med.get("XMMC") + "】不存在!");
					rebody.add(med);
				}
			}
			res.put("body", rebody);
		} catch (ExpException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	public void doSaveSkinTestResult(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> record = new HashMap<String, Object>();
			Integer psjg = (Integer) body.get("PSJG");
			if (psjg == -1) {// 阴性时改处方标志
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("CFSB", Long.parseLong(body.get("CFSB").toString()));
				String updateCf01 = "update MS_CF01 set CFBZ=0 where CFSB=:CFSB and CFBZ=2";
				dao.doSqlUpdate(updateCf01, params);

				// String updateYJ01CFBZ =
				// "update MS_YJ01 set CFBZ=0 where CFBZ=2 and JZXH=(select JZXH from MS_CF01 where CFSB=:CFSB)";
				// dao.doSqlUpdate(updateYJ01CFBZ, params);
			}
			record.put("PSJG", psjg);
			record.put("SBXH", body.get("SBXH"));
			dao.doSave("update", BSPHISEntryNames.MS_CF02_CIC, record, false);

			// 更新YS_MZ_PSJL
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer update_hql = new StringBuffer();
			update_hql.append("update YS_MZ_PSJL").append(
					" set PSJG =:PSJG, PSYS =:PSYS, PSSJ =:PSSJ");
			if (psjg == 0) { // 皮试结果为未知时,WCBZ为0
				update_hql.append(", WCBZ=0");
			} else {
				update_hql.append(", WCBZ=1");
			}
			update_hql
					.append(" where JGID =:JGID and YPBH =:YPBH and BRBH =:BRBH and WCBZ = 0");
			parameters.put("PSJG", psjg);
			parameters.put("PSYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
			parameters.put("PSSJ", new Date());
			parameters.put("JGID", manageUnit);
			parameters.put("YPBH", Long.parseLong(body.get("YPXH").toString()));
			parameters.put("BRBH", Long.parseLong(body.get("BRID").toString()));
			dao.doUpdate(update_hql.toString(), parameters);
			if (psjg == 1) { // 阳性时插入过敏药物信息
				Map<String, Object> gy_psjl = new HashMap<String, Object>();
				gy_psjl.put("BRID", body.get("BRID"));
				gy_psjl.put("YPXH", body.get("YPXH"));
				gy_psjl.put("JGID", manageUnit);
				gy_psjl.put("PSJG", 1);
				gy_psjl.put("PSLY", 1);
				dao.doSave("create", "phis.application.cic.schemas.GY_PSJL_MZ",
						gy_psjl, false);
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("皮试结果信息保存失败!", e);
		}
	}

	/**
	 * 验证科室排班和医生排班信息
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doVerificationReservationInfo(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		// System.out.println(user.getProperty("reg_departmentId"));
		try {
			Map<String, Object> jzlsparameters = new HashMap<String, Object>();
			int GHFZ = Integer.parseInt(body.get("GHFZ") + "");
			jzlsparameters.put("FZRQ",
					BSHISUtil.toDate(body.get("FZRQ").toString()));
			jzlsparameters.put("clinicId",
					Long.parseLong(body.get("clinicId").toString()));
			jzlsparameters.put("BRID",
					Long.parseLong(body.get("BRID").toString()));
			if (GHFZ == 0) {// 如果未开通挂号复诊
				dao.doUpdate(
						"update YS_MZ_JZLS "
								+ " set FZRQ=:FZRQ,GHFZ=0 where JZXH=:clinicId and BRBH=:BRID",
						jzlsparameters);
			} else {
				// 如果开通了挂号复诊
				body.put("FZRQ", BSHISUtil.toDate(body.get("FZRQ").toString()));
				Map<String, Object> yspbparameters = new HashMap<String, Object>();
				Map<String, Object> kspbparameters = new HashMap<String, Object>();
				Calendar startc = Calendar.getInstance();
				Date FZRQ = (Date) body.get("FZRQ");
				startc.setTime(FZRQ);
				startc.set(Calendar.HOUR_OF_DAY, 0);
				startc.set(Calendar.MINUTE, 0);
				startc.set(Calendar.SECOND, 0);
				startc.set(Calendar.MILLISECOND, 0);
				Date ldt_begin = startc.getTime();
				startc.set(Calendar.HOUR_OF_DAY, 23);
				startc.set(Calendar.MINUTE, 59);
				startc.set(Calendar.SECOND, 59);
				startc.set(Calendar.MILLISECOND, 999);
				Date ldt_end = startc.getTime();
				yspbparameters.put("YSDM", body.get("YSDM"));
				yspbparameters.put("KSDM",
						Long.parseLong(body.get("KSDM") + ""));
				yspbparameters.put("ZBLB",
						Integer.parseInt(body.get("ZBLB") + ""));
				yspbparameters.put("JGID", manageUnit);
				yspbparameters.put("ldt_Begin", ldt_begin);
				yspbparameters.put("ldt_End", ldt_end);
				kspbparameters.put("GHKS",
						Long.parseLong(body.get("KSDM") + ""));
				kspbparameters.put("ZBLB",
						Integer.parseInt(body.get("ZBLB") + ""));
				kspbparameters.put("JGID", manageUnit);
				kspbparameters.put("li_WeekDay",
						startc.get(Calendar.DAY_OF_WEEK));
				String sql = "YSDM = :YSDM and KSDM = :KSDM and ZBLB = :ZBLB and JGID = :JGID and GZRQ >= :ldt_Begin and GZRQ <= :ldt_End";
				Long l = dao.doCount("MS_YSPB", sql, yspbparameters);
				if (l > 0) {// 看看医生有没有排班
					String yspbsql = "SELECT JZXH as JZXH,YYXE as YYXE,YYRS as YYRS FROM MS_YSPB where YSDM = :YSDM and KSDM = :KSDM and ZBLB = :ZBLB and JGID = :JGID and GZRQ >= :ldt_Begin and GZRQ <= :ldt_End";
					Map<String, Object> yspbmap = dao.doLoad(yspbsql,
							yspbparameters);
					if (yspbmap != null) {
						res.put("yspb", yspbmap);
					}
				} else {// 医生没排版 看科室排班
					String kspbsql = "SELECT JZXH as JZXH,YYXE as YYXE,YYRS as YYRS FROM MS_KSPB where GHKS=:GHKS and ZBLB = :ZBLB and JGID = :JGID and GHRQ = :li_WeekDay";
					Map<String, Object> kspbmap = dao.doLoad(kspbsql,
							kspbparameters);
					if (kspbmap != null) {
						res.put("kspb", kspbmap);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("验证科室排班和医生排班信息失败!", e);
		}

	}

	/**
	 * 保存预约信息
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveReservationInfo(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Long jzxh = null;
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnit().getId();
			Map<String, Object> saveparameters = new HashMap<String, Object>();
			Map<String, Object> jzlsparameters = new HashMap<String, Object>();
			saveparameters.put("YYRQ", sdf.parse(body.get("FZRQ") + ""));
			saveparameters.put("GHRQ", sdf.parse(body.get("GHRQ") + ""));
			jzlsparameters.put("FZRQ", sdf.parse(body.get("FZRQ") + ""));
			body.put("FZRQ", BSHISUtil.toDate(body.get("FZRQ").toString()));
			Map<String, Object> yspbparameters = new HashMap<String, Object>();
			Map<String, Object> kspbparameters = new HashMap<String, Object>();
			Map<String, Object> yyghparameters = new HashMap<String, Object>();
			Calendar startc = Calendar.getInstance();
			Date FZRQ = (Date) body.get("FZRQ");
			startc.setTime(FZRQ);
			startc.set(Calendar.HOUR_OF_DAY, 0);
			startc.set(Calendar.MINUTE, 0);
			startc.set(Calendar.SECOND, 0);
			startc.set(Calendar.MILLISECOND, 0);
			Date ldt_begin = startc.getTime();
			startc.set(Calendar.HOUR_OF_DAY, 23);
			startc.set(Calendar.MINUTE, 59);
			startc.set(Calendar.SECOND, 59);
			startc.set(Calendar.MILLISECOND, 999);
			Date ldt_end = startc.getTime();
			// 医生排班条件
			yspbparameters.put("YSDM", body.get("YSDM"));
			yspbparameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			yspbparameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
			yspbparameters.put("JGID", manageUnit);
			yspbparameters.put("ldt_Begin", ldt_begin);
			yspbparameters.put("ldt_End", ldt_end);
			// 科室排班条件
			kspbparameters.put("GHKS", Long.parseLong(body.get("KSDM") + ""));
			kspbparameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
			kspbparameters.put("GHRQ", startc.get(Calendar.DAY_OF_WEEK));
			kspbparameters.put("JGID", manageUnit);
			// 就诊历史条件
			jzlsparameters.put("clinicId",
					Long.parseLong(body.get("clinicId").toString()));
			jzlsparameters.put("BRID",
					Long.parseLong(body.get("BRID").toString()));
			jzlsparameters.put("JGID", manageUnit);
			// 预约挂号条件
			yyghparameters.put("BRID", Long.parseLong(body.get("BRID") + ""));
			yyghparameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			yyghparameters.put("JGID", manageUnit);
			yyghparameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
			yyghparameters.put("ldt_Begin", ldt_begin);
			yyghparameters.put("ldt_End", ldt_end);
			String yyghsql = "BRID = :BRID and KSDM = :KSDM and JGID = :JGID and ZBLB=:ZBLB and YYRQ >= :ldt_Begin and YYRQ <= :ldt_End";
			Long yyghl = dao.doCount("MS_YYGH", yyghsql, yyghparameters);// 判断有没有预约过
			String yspbsql = "YSDM = :YSDM and KSDM = :KSDM and ZBLB = :ZBLB and JGID = :JGID and GZRQ >= :ldt_Begin and GZRQ <= :ldt_End";
			Long yspbl = dao.doCount("MS_YSPB", yspbsql, yspbparameters);// 判断医生有没有排班
			String kspbsql = "GHKS=:GHKS and ZBLB=:ZBLB and GHRQ=:GHRQ and JGID=:JGID";
			Long kspbl = dao.doCount("MS_KSPB", kspbsql, kspbparameters);// 判断医生有没有排班
			if (yyghl > 0) {// 如果已经预约 删除
				res.put(Service.RES_CODE, 701);
				res.put(Service.RES_MESSAGE,
						"该病人在本科室已经于 " + BSHISUtil.toString(FZRQ, "yyyy-MM-dd")
								+ "有预约!");
				dao.doUpdate(
						"delete from MS_YYGH"
								+ " where BRID = :BRID and KSDM = :KSDM and JGID = :JGID and ZBLB=:ZBLB and YYRQ >= :ldt_Begin and YYRQ <= :ldt_End",
						yyghparameters);
			} else {
				// 如果没有预约过 就更新
				if (yspbl > 0) {// 如果医生排班 就更新医生排班
					dao.doUpdate(
							"update MS_YSPB"
									+ " set JZXH = JZXH + 1,YYRS = YYRS + 1 where YSDM = :YSDM and KSDM = :KSDM and ZBLB = :ZBLB and JGID = :JGID and GZRQ >= :ldt_Begin and GZRQ <= :ldt_End",
							yspbparameters);
				}
				// 更新挂号科室
				dao.doUpdate(
						"update MS_KSPB"
								+ " set JZXH = JZXH + 1,YYRS = YYRS + 1 where GHKS=:GHKS and ZBLB=:ZBLB and GHRQ=:GHRQ and JGID=:JGID",
						kspbparameters);
				// 更新就诊历史
				dao.doUpdate(
						"update YS_MZ_JZLS "
								+ " set FZRQ=:FZRQ,GHFZ=1 where JZXH=:clinicId and BRBH=:BRID and JGID=:JGID",
						jzlsparameters);
			}
			if (yspbl > 0) {// 如果医生排班了 就获取医生排班的jzxh
				Map<String, Object> yspbjzxh = dao
						.doLoad("select JZXH as JZXH from MS_YSPB "
								+ " where YSDM = :YSDM and KSDM = :KSDM and ZBLB = :ZBLB and JGID = :JGID and GZRQ >= :ldt_Begin and GZRQ <= :ldt_End",
								yspbparameters);
				jzxh = Long.parseLong(yspbjzxh.get("JZXH") + "");
			} else if (kspbl > 0) {// 科室排班了就获取科室的jzxh
				Map<String, Object> kspbjzxh = dao
						.doLoad("select JZXH as JZXH from MS_KSPB "
								+ " where GHKS=:GHKS and ZBLB=:ZBLB and GHRQ=:GHRQ and JGID=:JGID",
								kspbparameters);
				jzxh = Long.parseLong(kspbjzxh.get("JZXH") + "");
			} else {
				jzxh = Long.parseLong(0 + "");
			}
			saveparameters.put("BRID", Long.parseLong(body.get("BRID") + ""));
			saveparameters.put("JGID", body.get("JGID"));
			saveparameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			saveparameters.put("YSDM", body.get("YSDM"));
			saveparameters.put("GHBZ", 0);
			saveparameters.put("ZCID", Long.parseLong(0 + ""));
			saveparameters.put("YYMM", "0");
			saveparameters.put("JZXH", jzxh);
			saveparameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
			saveparameters.put("YYLB", 1);
			// 并增加
			dao.doSave("create", "phis.application.cic.schemas.MS_YYGH",
					saveparameters, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("预约信息保存失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("预约信息保存失败!", e);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public Map<String, Object> doSaveClinicTherapeutic(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException, NumberFormatException, ControllerException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String KSDM = user.getProperty("biz_departmentId", String.class);
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Integer ZLXH = (Integer) body.get("ZLXH");
		String JZXH = body.get("JZXH").toString();
		String BRID = (String) body.get("BRID");
		String BRXZ = (String) body.get("BRXZ");
		String BRXM = (String) body.get("BRXM");
		String GHKS = body.get("GHKS").toString();
		parameters.put("JZXH", Long.parseLong(JZXH));

		// 载入诊疗方案
		try {
			// 判断是否能载入诊疗方案
			long l = dao.doCount("MS_CF01", "JZXH=:JZXH and FPHM is not null",
					parameters);
			if (l > 0) {
				throw new ModelDataOperationException("存在已收费的处方，不允许载入诊疗模版!");
			}
			long m = dao.doCount("MS_YJ01", "JZXH=:JZXH and FPHM is not null",
					parameters);
			if (m > 0) {
				throw new ModelDataOperationException("存在已收费的处置，不允许载入诊疗模版!");
			}
			Map<String, Object> zlfa = dao.doLoad(
					"phis.application.cic.schemas.GY_ZLFA01", ZLXH);
			Long blmbbh = (Long) zlfa.get("BLMBBH");// 病例模版序号
			Long cfztbh = (Long) zlfa.get("CFZTBH");// 处方组套序号
			Long xmztbh = (Long) zlfa.get("XMZTBH");// 项目组套序号
			Long jbxh = (Long) zlfa.get("JBXH");// 疾病序号
			if (blmbbh != null && blmbbh > 0) {
				Map<String, Object> blxx = dao.doLoad(
						"phis.application.cic.schemas.GY_BLMB_Y", blmbbh);
				if (blxx != null && blxx.get("JLXH") != null) {
					List<Object> cnd = CNDHelper.createSimpleCnd("eq", "JLXH",
							"s", blxx.get("JLXH") + "");
					List<Map<String, Object>> list = dao
							.doList(cnd, "a.id",
									"phis.application.cic.schemas.PUB_PelpleHealthTeach_MB");
					blxx.put("JKCFRecords", list);
				}
				data.put("blxx", blxx);
			}
			// 删除处方信息
			List<Map<String, Object>> cfsbList = dao.doQuery(
					"select CFSB as CFSB from MS_CF01 " + " where JZXH=:JZXH",
					parameters);
			for (Map<String, Object> cfsbMap : cfsbList) {
				if (cfsbMap != null) {
					dao.removeByFieldValue("CFSB", cfsbMap.get("CFSB"),
							BSPHISEntryNames.MS_CF02_CIC);
					dao.doRemove(cfsbMap.get("CFSB"),
							BSPHISEntryNames.MS_CF01_CIC);
				}
			}
			if (cfztbh != null && cfztbh > 0) {
				Map<String, Object> cfxxMap = new HashMap<String, Object>();
				// 根据组套类型获取药房识别，费用归并
				Map<String, Object> zt01Map = dao.doLoad(
						"phis.application.cic.schemas.YS_MZ_ZT01_CF", cfztbh);
				cfxxMap.put("ZTBH", cfztbh);
				cfxxMap.put("pharmacyId",
						getYFSB((Integer) zt01Map.get("ZTLB"), GHKS, ctx));
				cfxxMap.put("BRXZ", BRXZ);
				cfxxMap.put("BRID", BRID);
				List<Map<String, Object>> meds = doLoadPersonalSet(cfxxMap, ctx);
				String errorMsg = "";
				for (int i = 0; i < meds.size(); i++) {
					Map<String, Object> med = meds.get(i);
					if (med.get("errorMsg") != null) {
						errorMsg += "药品【" + med.get("YPMC") + "】"
								+ med.get("errorMsg") + "\n";
						meds.remove(med);
						i--;
					}
				}
				if (!"".equals(errorMsg)) {
					data.put("errorMsg", errorMsg);
				}
				cfxxMap.clear();
				Map<String, Object> formData = new HashMap<String, Object>();
				formData.put("JZXH", JZXH);
				formData.put("BRXZ", BRXZ);
				formData.put("BRXM", BRXM);
				formData.put("KSDM", KSDM);
				formData.put("YSDM", user.getUserId() + "");// 获取员工代码暂时用该方法替代
				formData.put("KFRQ", BSHISUtil.getDateTime());
				formData.put("CFLX", zt01Map.get("ZTLB"));
				formData.put("BRID", body.get("BRID"));
				formData.put("DJLY", 1);
				formData.put("CFTS", 1);
				formData.put("YFSB",
						getYFSB((Integer) zt01Map.get("ZTLB"), GHKS, ctx));
				cfxxMap.put("formData", formData);
				cfxxMap.put("listData", meds);
				cfxxMap.put("saveBy", "ztdr");// 组套调入
				doSaveClinicInfo(cfxxMap, ctx);
			}

			// 删除处置信息
			List<Map<String, Object>> yjxhList = dao.doQuery(
					"select YJXH as YJXH from MS_YJ01  where JZXH=:JZXH",
					parameters);
			for (Map<String, Object> yjxhMap : yjxhList) {
				if (yjxhMap != null) {
					dao.removeByFieldValue("YJXH", yjxhMap.get("YJXH"),
							BSPHISEntryNames.MS_YJ02_CIC);
					dao.doRemove(yjxhMap.get("YJXH"),
							BSPHISEntryNames.MS_YJ01_CIC);
				}
			}
			if (xmztbh != null && xmztbh > 0) {
				ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(
						dao);
				Map<String, Object> cz_body = new HashMap<String, Object>();
				cz_body.put("ZTBH", xmztbh);
				List<Map<String, Object>> yjList = cdem.doLoadPersonalSet(
						cz_body, ctx);
				for (Map<String, Object> yjMap : yjList) {
					if (yjMap.get("FYDJ") != null && yjMap.get("FYDJ") != "") {
						yjMap.put("FYDJ1",
								Double.parseDouble(yjMap.get("FYDJ") + ""));
					}
					if (yjMap.get("XMSL") != null && yjMap.get("XMSL") != "") {
						yjMap.put("XMSL1",
								Double.parseDouble(yjMap.get("XMSL") + ""));
					}
					yjMap.put("JGID", manageUnit);
					yjMap.put("YLXH", yjMap.get("FYXH"));
					yjMap.put("YJZH", yjMap.get("YPZH"));
					yjMap.put("YLDJ", yjMap.get("FYDJ"));
					yjMap.put("YLSL", yjMap.get("XMSL"));
					yjMap.put("HJJE", (Double) yjMap.get("FYDJ1")
							* (Double) yjMap.get("XMSL1"));
					yjMap.put("DZBL", 1);
					yjMap.put("YJXH", 0);
					if (yjMap.get("XMLX") != null && yjMap.get("XMLX") != "") {
						yjMap.put("XMLX",
								Integer.parseInt(yjMap.get("XMLX") + ""));
					} else {
						yjMap.put("XMLX", 0);
					}
					yjMap.put("YJZX", 0);
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("TYPE", 0);
					params.put("BRXZ", BRXZ);
					params.put("FYXH", yjMap.get("FYXH"));
					params.put("FYGB", yjMap.get("FYGB"));
					yjMap.put(
							"ZFBL",
							BSPHISUtil.getPayProportion(params, ctx, dao).get(
									"ZFBL"));
				}
				Map<String, Object> req = new HashMap<String, Object>();
				req.put("body", yjList);
				cdem.doSaveDisposalEntry(req, new HashMap<String, Object>(),
						dao, ctx, Integer.parseInt(JZXH.toString()),
						Integer.parseInt(BRID.toString()), BRXM, 1, 0);
			}
			// 删除诊断表数据
			dao.doRemove("JZXH", Long.parseLong(JZXH),
					BSPHISEntryNames.MS_BRZD_CIC);
			if (jbxh != null && jbxh > 0) {
				Map<String, Object> jbMap = dao.doLoad(
						"phis.application.cic.schemas.GY_JBBM", jbxh);
				Map<String, Object> dignosis = new HashMap<String, Object>();
				dignosis.put("ZDXH", jbxh);
				if (jbMap != null) {
					dignosis.put("ICD10", jbMap.get("ICD10"));
					dignosis.put("ZDMC", jbMap.get("JBMC"));
				}
				dignosis.put("ZDLB", 1);
				dignosis.put("ZDBW", 0);
				dignosis.put("DEEP", 0);
				dignosis.put("SJZD", 0);
				dignosis.put("ZZBZ", 1);
				dignosis.put("JZXH", JZXH);
				dignosis.put("BRID", BRID);
				dignosis.put("JGID", manageUnit);
				dignosis.put("ZDYS", user.getUserId() + "");// 获取员工代码暂时用该方法替代
				dignosis.put("ZDSJ", new Date());
				dignosis.put("FBRQ", new Date());
				dignosis.put("PLXH", 0);
				dignosis.put("FZBZ", 0);
				dignosis.put("CFLX", 1);
				dao.doSave("create", BSPHISEntryNames.MS_BRZD_CIC, dignosis,
						false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("诊疗方案信息不存在!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("诊疗方案导入错误!", e);
		}
		return data;
	}

	// private int getFYGB(int type, Context ctx) {
	// String p = "";
	// if (type == 1) {
	// p = "XYF";
	// } else if (type == 2) {
	// p = "ZYF";
	// } else if (type == 3) {
	// p = "CYF";
	// }
	// String v = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(), p,
	// ctx);
	// return Integer.parseInt(v);
	// }

	private long getYFSB(int type, String GHKS, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String p = "";
		if (type == 1) {
			p = "YS_MZ_FYYF_" + GHKS + "_XY";
		} else if (type == 2) {
			p = "YS_MZ_FYYF_" + GHKS + "_ZY";
		} else if (type == 3) {
			p = "YS_MZ_FYYF_" + GHKS + "_CY";
		}
		String v = ParameterUtil.getParameter(manageUnit, p, ctx);
		if ("null".equals(v)) {
			throw new ModelDataOperationException("请先设置发药药房!");
		}
		return Long.parseLong(v);
	}

	private double getYPSL(Map<String, Object> data) {
		Double YYZL = 0.0;
		Double YCJL = 0.0;
		Double YPJL = (Double) data.get("YPJL");
		Integer MRCS = (Integer) data.get("MRCS");
		Integer YYTS = (Integer) data.get("YYTS");
		Integer YFBZ = 0;
		if (data.get("YCJL") != null) {
			YCJL = (Double) data.get("YCJL");
		}
		if (data.get("YFBZ") != null) {
			YFBZ = (Integer) data.get("YFBZ");
		}
		YYZL = Math.ceil(YCJL / (YPJL * YFBZ) * MRCS * YYTS);
		return YYZL;
	}

	/**
	 * 登录科室时准备科室信息
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSavePdKsxxIn(Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			Map<String, Object> params = new HashMap<String, Object>();

			params.put("GHKS", user.getProperty("reg_departmentId", Long.class));
			if (user.getProperty("DDDM") == null) {
				params.put("ZSDM", "0");
			} else {
				params.put("ZSDM", user.getProperty("DDDM"));
			}
			params.put("JGID", jgid);
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select count(*) as num from PD_ZSQK where GHKS=:GHKS and ZSDM=:ZSDM and JGID=:JGID",
							params);
			params.put("YSDM", user.getUserId() + "");// 获取员工代码暂时用该方法替代
			params.put("KSMC", user.getProperty("DDXX"));
			if (list == null || list.size() == 0
					|| Integer.parseInt(list.get(0).get("NUM").toString()) <= 0) {
				if (params.get("ZSDM") == null
						|| "0".equals(params.get("ZSDM"))
						|| "".equals(params.get("ZSDM"))) {
				} else {
					dao.doSqlUpdate(
							"insert into PD_ZSQK (GHKS,ZSDM, QYBZ,JZZT,JZYS,KSMC,JGID,ZWDM)values(:GHKS,:ZSDM,1,0,:YSDM,:KSMC,:JGID,0)",
							params);
				}
			} else {
				dao.doSqlUpdate(
						"update PD_ZSQK set QYBZ=1,JZZT=1,JZYS=:YSDM ,KSMC=:KSMC where GHKS=:GHKS and ZSDM=:ZSDM and JGID=:JGID",
						params);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("设置科室排队信息失败!", e);
		}
	}

	/**
	 * 退出系统时注销科室信息
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSavePdKsxxOut(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.doSqlUpdate(
					"update PD_ZSQK set QYBZ=0,JZZT=0,JZYS=null where GHKS=:KSDM and ZSDM=:ZSDM and JGID=:JGID",
					body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("设置科室排队信息失败!", e);
		}
	}

	/**
	 * 呼叫/结束就诊时调用：更新排队就诊PD_ZSQK PD_JZLB
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// public void doSavePdJzzt(Map<String, Object> body, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String jgid = user.getManageUnit().getId();// 用户的机构ID
	// String qymzpd = ParameterUtil.getParameter(jgid,
	// BSPHISSystemArgument.QYMZPD, ctx);
	// if (qymzpd != null && qymzpd.equals("1")) {
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("GHKS", user.getProperty("reg_departmentId", Long.class));
	// if (user.getProperty("DDDM") == null) {
	// params.put("ZSDM", "0");
	// } else {
	// params.put("ZSDM", user.getProperty("DDDM"));
	// }
	// params.put("JGID", user.getManageUnit().getId());
	// params.put("JZZT", body.get("JZZT"));
	// try {
	// dao.doSqlUpdate(
	// "update PD_ZSQK set JZZT = :JZZT where GHKS =:GHKS and ZSDM=:ZSDM and JGID = :JGID",
	// params);
	// if ("1".equals(body.get("JZZT").toString())) {
	// params.clear();
	// params.put("BRID", body.get("BRID"));
	// params.put("JGID", user.getManageUnit().getId());
	// params.put("GHKS",
	// user.getProperty("reg_departmentId", Long.class));
	// dao.doSqlUpdate(
	// "update PD_JZLB set JZBZ=1 where BRID=:BRID and GHKS=:GHKS and JGID=:JGID",
	// params);
	// }
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("更新排队信息失败!", e);
	// }
	// }
	//
	// }

	/**
	 * 跳过排队信息
	 *
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveSkipInfo(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> brxxList = dao
					.doSqlQuery(
							"SELECT JZBRXX as JZBRXX,MZHM as MZHM FROM PD_ZSQK WHERE GHKS = :GHKS and JGID=:JGID",
							body);
			if (brxxList == null || brxxList.size() == 0
					|| brxxList.get(0).get("MZHM") == null) {
				throw new ModelDataOperationException("对不起，当前没有需要跳过的病人!");
			}
			body.put("MZHM", brxxList.get(0).get("MZHM"));
			dao.doSqlUpdate(
					"update PD_JZLB set JZBZ = 1 where BRID = (select BRID from MS_BRDA where MZHM=:MZHM) and GHKS=:GHKS and JGID =:JGID",
					body);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("更新排队信息失败!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		long c = new Date().getTime();
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", JGID);
			// long total =
			// dao.doCount(BSPHISEntryNames.MS_BRDA+" a,"+BSPHISEntryNames.MPI_DemographicInfo+" b",
			// "a.EMPIID = b.empiId and a.JDJG=:JGID", parameters);
			StringBuffer hql = new StringBuffer(
					"select distinct * from "
							+ "  (select a.BRID as BRID,a.EMPIID as EMPIID,c.cardNo as cardNo,a.MZHM as MZHM,a.BRXM as BRXM,a.FYZH as FYZH,");
			hql.append("a.BRXZ as BRXZ,a.BRXB as BRXB,a.CSNY as CSNY,a.SFZH as SFZH,b.idCard as idCard,a.HYZK as HYZK,a.ZYDM as ZYDM,a.MZDM as MZDM,a.XXDM as XXDM,");
			hql.append("a.GMYW as GMYW,a.DWXH as DWXH,a.DWMC as DWMC,a.DWDH as DWDH,a.DWYB as DWYB,a.HKDZ as HKDZ,a.JTDH as JTDH,a.HKYB as HKYB,a.JZCS as JZCS,");
			hql.append("a.JZRQ as JZRQ,a.CZRQ as CZRQ,a.JZKH as JZKH,a.SFDM as SFDM,a.JGDM as JGDM,a.GJDM as GJDM,a.LXRM as LXRM,a.LXGX as LXGX,a.LXDZ as LXDZ,");
			hql.append("a.LXDH as LXDH,a.DBRM as DBRM,a.DBGX as DBGX,a.SBHM as SBHM,a.YBKH as YBKH,a.ZZTX as ZZTX,a.JDJG as JDJG,"
					+ BSPHISUtil.toChar("a.JDSJ", "yyyy-MM-dd HH24:mi:ss")
					+ " as JDSJ,a.JDR as JDR,");
			hql.append("a.ZXBZ as ZXBZ,a.ZXR as ZXR,a.ZXSJ as ZXSJ,"
					+ BSPHISUtil.toChar("a.XGSJ", "yyyy-MM-dd HH24:mi:ss")
					+ " as XGSJ,a.CSD_SQS as CSD_SQS,a.CSD_S as CSD_S,a.CSD_X as CSD_X,a.JGDM_SQS as JGDM_SQS,");
			hql.append("a.JGDM_S as JGDM_S,a.XZZ_SQS as XZZ_SQS,a.XZZ_S as XZZ_S,a.XZZ_X as XZZ_X,a.XZZ_YB as XZZ_YB,a.XZZ_DH as XZZ_DH,a.HKDZ_SQS as HKDZ_SQS,");
			hql.append("a.HKDZ_S as HKDZ_S,a.HKDZ_X as HKDZ_X,a.XZZ_QTDZ as XZZ_QTDZ,a.HKDZ_QTDZ as HKDZ_QTDZ from MS_BRDA a,MPI_DemographicInfo b left outer join MPI_Card c");
			hql.append(" on c.cardTypeCode="
					+ KLX
					+ " and c.empiId = b.empiId where a.EMPIID = b.empiId and a.JDJG=:JGID");
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				hql.append(" and " + ExpRunner.toString(cnd, ctx));
			}
			hql.append(")");

			/*int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");*/

			hql.deleteCharAt(hql.length() - 1);
			String sort = ContextUtils.get(Context.HTTP_REQUEST,
					HttpServletRequest.class).getParameter("sort");
			String dir = ContextUtils.get(Context.HTTP_REQUEST,
					HttpServletRequest.class).getParameter("dir");
			if (!S.isEmpty(sort)) {
				hql.append(" order by " + sort.replace("_text", "") + " " + dir);
			} else {
				hql.append(" order by a.XGSJ desc");
			}
			hql.append(")");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			long a = new Date().getTime();
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			int total = list.size();
			long b = new Date().getTime();
			System.out.println(b-a);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.cic.schemas.MS_BRDA_CIC");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("cardNo", list.get(i).get("CARDNO"));
				list.get(i).put("idCard", list.get(i).get("IDCARD"));
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
			long d = new Date().getTime();
			System.out.println(d-c);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");// 用户的机构ID
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			String KLX = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "KLX", "04",
					"01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
			Map<String, Object> parameters = new HashMap<String, Object>();
			// parameters.put("JGID", JGID);
			// long total =
			// dao.doCount(BSPHISEntryNames.MS_BRDA+" a,"+BSPHISEntryNames.MPI_DemographicInfo+" b",
			// "a.EMPIID = b.empiId and a.JDJG=:JGID", parameters);
			StringBuffer hql = new StringBuffer(
					"select a.JZXH as JZXH,a.GHXH as GHXH,a.BRBH as BRBH,b.EMPIID as EMPIID,a.JGID as JGID,c.JZHM as JZHM,d.cardNo as JZKH,");
			hql.append("b.MZHM as MZHM,b.BRXZ as BRXZ,b.BRXM as BRXM,b.BRXB as BRXB,b.CSNY as CSNY,"
					+ BSPHISUtil.toChar("c.GHSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as GHSJ,c.CZPB as CZPB,a.KSDM as KSDM,a.YSDM as YSDM,");
			hql.append("a.ZYZD as ZYZD,"
					+ BSPHISUtil.toChar("a.KSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as KSSJ,"
					+ BSPHISUtil.toChar("a.JSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as JSSJ,a.JZZT as JZZT,a.YYXH as YYXH,a.FZRQ as FZRQ,a.GHFZ as GHFZ from ");
			hql.append(" YS_MZ_JZLS a,");
			hql.append(" MS_BRDA b left outer join ");
			hql.append(" MPI_Card d on d.cardTypeCode=" + KLX
					+ " and d.empiId = b.EMPIID,");
			hql.append(" MS_GHMX c where a.BRBH = b.BRID and a.GHXH=c.SBXH ");
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				hql.append(" and " + ExpRunner.toString(cnd, ctx));
			}
			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			hql.append(" order by a.JZXH desc");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.cic.schemas.YS_MZ_JZLS");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("cardNo", list.get(i).get("CARDNO"));
				list.get(i).put("idCard", list.get(i).get("IDCARD"));
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryWorkLogs(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");// 用户的机构ID
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			// parameters.put("JGID", JGID);
			// long total =
			// dao.doCount(BSPHISEntryNames.MS_BRDA+" a,"+BSPHISEntryNames.MPI_DemographicInfo+" b",
			// "a.EMPIID = b.empiId and a.JDJG=:JGID", parameters);
			String whereStr = "";
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				whereStr = " and " + ExpRunner.toString(cnd, ctx);
			}
			List<Map<String, Object>> countList = dao
					.doSqlQuery(
							"select count(*) as total from YS_MZ_JZLS a left outer join MS_BRZD c on  c.JZXH=a.JZXH and c.ZZBZ='1' left outer join MS_GHMX d on d.SBXH=a.GHXH left outer join MS_BCJL e on e.JZXH=a.JZXH,MS_BRDA b where b.BRID=a.BRBH "
									+ whereStr, null);
			long count = Long.parseLong(countList.get(0).get("TOTAL") + "");
			if (count > 0) {
				StringBuffer hql = new StringBuffer(
						"select a.JZXH as JZXH, a.BRBH as BRBH, a.GHXH as GHXH, a.KSSJ as KSSJ, a.JSSJ as JSSJ, b.BRXM as BRXM, b.BRXB as BRXB, b.SFZH as SFZH, b.ZYDM as ZYDM, c.FZBZ as FZBZ, c.FBRQ as FBRQ, c.ZDMC as ZDMC, b.LXDZ as LXDZ, b.DWMC as DWMC, b.JTDH as JTDH, d.CZPB as CZPB, e.JZKS as JZKS, e.JZYS as JZYS, e.ZSXX as ZSXX, e.XBS as XBS, e.JWS as JWS, e.TGJC as TGJC, e.FZJC as FZJC, e.CLCS as CLCS, e.T as T, e.P as P, e.R as R, e.SSY as SSY, e.SZY as SZY, e.KS as KS, e.YT as YT, e.HXKN as HXKN, e.OT as OT, e.FT as FT, e.FX as FX, e.PZ as PZ, e.QT as QT, e.BRQX as BRQX, e.JKJYNR as JKJYNR, b.CSNY as CSNY from YS_MZ_JZLS a left outer join MS_BRZD c on  c.JZXH=a.JZXH and c.ZZBZ='1' left outer join MS_GHMX d on d.SBXH=a.GHXH left outer join MS_BCJL e on e.JZXH=a.JZXH,MS_BRDA b where b.BRID=a.BRBH");
				// if (req.get("cnd") != null) {
				// List<Object> cnd = (List<Object>) req.get("cnd");
				// hql.append(" and " + ExpRunner.toString(cnd, ctx));
				// }
				if (whereStr.length() > 0) {
					hql.append(whereStr);
				}
				hql.append(" order by a.JZXH desc");
				parameters.put("first", first * pageSize);
				parameters.put("max", pageSize);
				List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
						parameters);
				SchemaUtil.setDictionaryMassageForList(list,
						"phis.application.cic.schemas.MS_BCJL_RZ");
				res.put("pageSize", pageSize);
				res.put("pageNo", first);
				res.put("totalCount", count);
				res.put("body", list);
			} else {
				res.put("pageSize", pageSize);
				res.put("pageNo", first);
				res.put("totalCount", count);
				res.put("body", new ArrayList());
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doQueryIsAllowed(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			String QYCFCZQZTJ = ParameterUtil.getParameter(JGID, "QYCFCZQZTJ",
					"0", "录入处方处置前置条件是否启用，如果启用，则未录入诊断不允许录入处方处置，1表示启用，0表示不启用",
					ctx);
			if (QYCFCZQZTJ.equals("0")) {
				res.put("isAllowed", 1);
				return;
			}

			Long JZXH = 0l;
			if (body.containsKey("JZXH") && body.get("JZXH") != "") {
				JZXH = Long.parseLong(body.get("JZXH") + "");
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZXH", JZXH);
			Map<String, Object> map_count = dao
					.doLoad("select count(JLBH) as COUNT from MS_BRZD where JZXH=:JZXH",
							parameters);
			int count = 0;
			if (map_count.containsKey("COUNT") && map_count.get("COUNT") != "") {
				count = Integer.parseInt(map_count.get("COUNT") + "");
			}
			if (count > 0) {
				res.put("isAllowed", 1);
			} else {
				res.put("isAllowed", 0);
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doQueryZDLR(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			Long JZXH = 0l;
			if (body.containsKey("JZXH") && body.get("JZXH") != "") {
				JZXH = Long.parseLong(body.get("JZXH") + "");
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZXH", JZXH);
			Map<String, Object> map_count = dao
					.doLoad("select count(JLBH) as COUNT from MS_BRZD where JZXH=:JZXH",
							parameters);
			int count = 0;
			if (map_count.containsKey("COUNT") && map_count.get("COUNT") != "") {
				count = Integer.parseInt(map_count.get("COUNT") + "");
			}
			if (count > 0) {
				res.put("isAllowed", 1);
			} else {
				res.put("isAllowed", 0);
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doQuerySystemArgumentCFMXSL(Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		String CFXYZYMXSL = ParameterUtil.getParameter(JGID, "CFXYZYMXSL", "0",
				"录入处方明细数量限制是否启用(西药中药)，0表示不启用,大于0则表示允许录入的最大处方明细数量", ctx);
		String CFCYMXSL = ParameterUtil.getParameter(JGID, "CFCYMXSL", "0",
				"录入处方明细数量限制是否启用(草药)，0表示不启用,大于0则表示允许录入的最大处方明细数量", ctx);
		res.put("CFXYZYMXSL", CFXYZYMXSL);
		res.put("CFCYMXSL", CFCYMXSL);
	}

	public void doQueryYCXL(Map<String, Object> body, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnit().getId();// 用户的机构ID
			Long YPXH = 0l;
			if (body.containsKey("YPXH") && body.get("YPXH") != "") {
				YPXH = Long.parseLong(body.get("YPXH") + "");
			}
			Long BRXZ = 0l;
			if (body.containsKey("BRXZ") && body.get("BRXZ") != "") {
				BRXZ = Long.parseLong(body.get("BRXZ") + "");
			}

			String SHIYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
			if (!BRXZ.toString().equals(SHIYB)) {
				res.put("YCXL", 0);
				return;
			}

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("YPXH", YPXH);
			Map<String, Object> map_YCXL = dao.doLoad(
					"select YCXL as YCXL from YK_TYPK where YPXH=:YPXH",
					parameters);
			double ycxl = 0;
			if (map_YCXL != null && map_YCXL.containsKey("YCXL")
					&& map_YCXL.get("YCXL") != null
					& map_YCXL.get("YCXL") != "") {
				ycxl = Double.parseDouble(map_YCXL.get("YCXL") + "");
			}
			res.put("YCXL", ycxl);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doIsAllowedSave(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();// 用户的机构ID
		try {
			Long JZXH = 0l;
			if (body.containsKey("JZXH") && body.get("JZXH") != "") {
				JZXH = Long.parseLong(body.get("JZXH") + "");
			}
			Long BRXZ = 0l;
			if (body.containsKey("BRXZ") && body.get("BRXZ") != "") {
				BRXZ = Long.parseLong(body.get("BRXZ") + "");
			}
			Double totalCount = 0d;
			if (body.containsKey("totalCount") && body.get("totalCount") != "") {
				totalCount = Double.parseDouble(body.get("totalCount") + "");
			}

			String SHIYB = ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
			if (!BRXZ.toString().equals(SHIYB)) {
				res.put("isAllowed", 1);
				return;
			}
			StringBuffer hql = new StringBuffer();
			hql.append("select count(*) as COUNT from YB_ZXSP a,YB_GHJS b,MS_GHMX c,YS_MZ_JZLS d "
					+ "where a.YYXH = b.JYLSH and b.SBXH = c.SBXH and c.SBXH=d.GHXH and d.JZXH=:JZXH");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZXH", JZXH);
			Map<String, Object> map_Count = dao.doLoad(hql.toString(),
					parameters);
			Long count = parseLong(map_Count.get("COUNT"));
			if (count == 0) {
				// 非规定病种
				String SYBCYXE = ParameterUtil.getParameter(JGID, "SYBCYXE",
						"0", "市医保病人草药限额", ctx);
				Double cyxe = parseDouble(SYBCYXE);
				if (cyxe != 0 && totalCount > cyxe) {
					res.put("isAllowed", 0);
				} else {
					res.put("isAllowed", 1);
				}

			} else if (count > 0) {
				// 规定病种
				// String SYBGDBZCYXE = ParameterUtil.getParameter(JGID,
				// "SYBGDBZCYXE", "0", "市医保规定病种病人草药限额", ctx);
				// Double gdbzcyxe = parseDouble(SYBGDBZCYXE);
				// if (gdbzcyxe != 0 && totalCount > gdbzcyxe) {
				// res.put("isAllowed", 0);
				// } else {
				// res.put("isAllowed", 1);
				// }
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public int parseInt(Object o) {
		if (o == null) {
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

	public static void main(String[] args) {
		// System.out.println(String.format("%1$.2f", 123.0));
	}

	// public void doQueryZSSF(String ypxh, Map<String, Object> res, BaseDAO
	// dao2,
	// Context ctx) throws ModelDataOperationException {
	// String sql = "select zssf as zssf from yk_typk where ypxh=" + ypxh;
	// try {
	// List<Map<String, Object>> list = dao.doSqlQuery(sql, null);
	// String zssf = list.get(0).get("ZSSF") + "";
	// res.put("zssf", zssf);
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("查询药品输液标志失败！");
	// }
	//
	// }

	public void doQueryBrqxAndJkjy(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		int jzxh = (Integer) body.get("JZXH");
		String sql = "select BRQX as BRQX,JKJY as JKJY from MS_BCJL where JZXH="
				+ jzxh;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JZXH", jzxh);
		try {
			if (dao.doQuery(sql, null) == null
					|| dao.doQuery(sql, null).size() <= 0) {
				res.put("BRQX", "1");
				return;
			}
			Map<String, Object> bcjl = dao.doQuery(sql, null).get(0);
			if (bcjl != null && bcjl.size() > 0) {
				if (bcjl.get("BRQX") != null
						&& !"".equals(bcjl.get("BRQX") + "")) {
					res.put("BRQX", bcjl.get("BRQX"));
				} else {
					res.put("BRQX", "1");
				}
				if (bcjl.get("JKJY") != null
						&& !"".equals(bcjl.get("JKJY") + "")) {
					res.put("JKJY", bcjl.get("JKJY"));
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询病人去向出错！");
		}
	}

	public void doQueryclinicManageList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long BRID = Long.parseLong(req.get("BRID") + "");
		long JZXH = Long.parseLong(req.get("JZXH") + "");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BRID", BRID);
		parameters.put("JZXH", JZXH);
		parameters.put("JGID", user.getManageUnit().getId());
		try {
			StringBuffer sql_list = new StringBuffer(
					"select c.JBBGK,a.JLBH as JLBH,a.BRID as BRID,a.JGID as JGID,a.JZXH as JZXH,a.DEEP as DEEP,a.SJZD as SJZD,a.ZDMC as ZDMC,a.ZZBZ as ZZBZ,a.ZDXH as ZDXH,a.PLXH as PLXH,a.ICD10 as ICD10,");
			sql_list.append("a.ZDBW as ZDBW,a.ZDYS as ZDYS,to_char(a.ZDSJ,'yyyy-mm-dd hh24:mi:ss') as ZDSJ,a.ZDLB as ZDLB,a.ZGQK as ZGQK,a.ZGSJ as ZGSJ,a.FZBZ as FZBZ,to_char(a.FBRQ,'yyyy-mm-dd') as FBRQ,b.ZHMC as ZHMC,a.CFLX as CFLX,a.JBPB as JBPB ");
			sql_list.append("from MS_BRZD a left join EMR_ZYZH b on a.ZDBW = b.ZHBS left join GY_JBBM c on a.ZDXH=c.JBXH  where a.BRID =:BRID and a.JZXH =:JZXH and a.JGID =:JGID order by a.PLXH ASC");

			List<Map<String, Object>> inofList = dao.doSqlQuery(
					sql_list.toString(), parameters);

			for (int i = 0; i < inofList.size(); i++) {
				if (inofList.get(i).get("CFLX") != null) {
					inofList.get(i).put("ZXLB", inofList.get(i).get("CFLX"));
				}
			}
			for (int i = 0; i < inofList.size(); i++) {
				if (inofList.get(i).get("ZHMC") != null) {
					inofList.get(i).put("MC", inofList.get(i).get("ZHMC") + "");
				} else {
					inofList.get(i).put(
							"MC",
							DictionaryController.instance()
									.getDic("phis.dictionary.position")
									.getText(inofList.get(i).get("ZDBW") + ""));
				}
			}
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.cic.schemas.MS_BRZD_CIC");
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载病人诊断信息出错！");
		}
	}

	/**
	 * 获取药房划价处方信息
	 *
	 * @param body
	 *            MZHM
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadCfxx(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		if (body.get("JZKH") == null) {
			throw new ModelDataOperationException("请输入有效的就诊卡号!");
		}
		try {
			String JZKH = (String) body.get("JZKH");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JZKH", JZKH);
			Map<String, Object> MPI_Card = dao.doLoad(
					"select empiId as empiId from MPI_Card where cardNo=:JZKH and cardTypeCode="
							+ KLX, parameters);
			if (MPI_Card != null) {
				String empiId = (String) MPI_Card.get("empiId");
				String sql = "SELECT a.EMPIID as EMPIID,a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXZ as BRXZ,a.BRXB as BRXB,a.CSNY as CSNY FROM MS_BRDA a WHERE a.EMPIID=:EMPIID";
				Map<String, Object> JZKHparameters = new HashMap<String, Object>();
				JZKHparameters.put("EMPIID", empiId);
				// person = dao.doLoad(sql, JZKHparameters);
				Map<String, Object> brxx = dao.doLoad(sql, JZKHparameters);
				if (brxx == null)
					throw new ModelDataOperationException("请输入有效的就诊卡号!");
				res.put("BRXX", brxx);
			} else {
				throw new ModelDataOperationException("请输入有效的就诊卡号!");
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病人信息错误!", e);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public void doQueryMaterialsPrice(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzjg = new HashMap<String, Object>();
		Map<String, Object> parameterjgbz = new HashMap<String, Object>();
		Map<String, Object> parameterskfxh = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				jgid, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.WZSFXMJG, ctx));
		if (wzsfxmjg != 1 || wpjfbz != 1) {
			return;
		}
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			long fyxh = Long.parseLong(body.get("YPXH") + "");
			double bzjg = Double.parseDouble(body.get("LSJG") + "");
			long KSDM = Long.parseLong(body.get("KSDM") + "");
			double czsl = Double.parseDouble(body.get("YPSL") + "");
			// 查询jgbz
			parameterjgbz.put("fyxh", fyxh);
			parameterjgbz.put("jgid", jgid);
			String hql = "select a.JGBZ as JGBZ from GY_YLMX a where a.FYXH=:fyxh and a.JGID=:jgid";
			Map<String, Object> jgbzMap = dao.doLoad(hql, parameterjgbz);
			int jgbz = 0;
			if (jgbzMap != null) {
				if (jgbzMap.get("JGBZ") != null) {
					jgbz = Integer.parseInt(jgbzMap.get("JGBZ") + "");
				}
			}
			// 查询kfxh
			StringBuffer hql1 = new StringBuffer();
			hql1.append("select KFXH as KFXH from WL_KFDZ where KSDM=:ksdm");
			parameterskfxh.put("ksdm", KSDM);
			Map<String, Object> kfxhMap = dao.doLoad(hql1.toString(),
					parameterskfxh);
			int kfxh = 0;
			if (kfxhMap != null) {
				if (kfxhMap.get("KFXH") != null) {
					kfxh = Integer.parseInt(kfxhMap.get("KFXH") + "");
				}
			} else {
				throw new RuntimeException("当前科室或病区没有对应的物资库房！");
			}
			// 查询wzsl
			parameterswzxh.put("FYXH", fyxh);
			parameterswzxh.put("JGID", jgid);
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisWZXH = dao
					.doQuery(
							"select a.WZXH as WZXH,a.WZSL as WZSL,b.WZMC as WZMC from GY_FYWZ a,WL_WZZD b where a.WZXH = b.WZXH and a.FYXH=:FYXH and a.JGID=:JGID",
							parameterswzxh);
			double wzjg = 0.00;
			double czje = 0.00;
			for (int i = 0; i < lisWZXH.size(); i++) {
				// 查询物资信息
				double wzslall = czsl
						* Double.parseDouble(lisWZXH.get(i).get("WZSL") + "");// 取到第一个物资的实际数量
				parameterswzjg.put("WZXH",
						Long.parseLong(lisWZXH.get(i).get("WZXH") + ""));
				parameterswzjg.put("JGID", jgid);
				parameterswzjg.put("KFXH", kfxh);
				List<Map<String, Object>> lisWZKC = dao
						.doQuery(
								"select LSJG as WZJG,WZSL as WZSL,KCXH as KCXH,YKSL as YKSL,WZXH as WZXH from WL_WZKC where WZXH=:WZXH and JGID=:JGID and KFXH=:KFXH order by JLXH",
								parameterswzjg);
				if (lisWZKC.size() == 0) {
					// msg = "项目为：" + FYMC + "所对应的的物资名称：" + WZMC_TS
					// + "库存不足不能保存!";
					throw new ModelDataOperationException("物资名称："
							+ lisWZXH.get(i).get("WZMC") + "库存不足!");
				}
				for (int j = 0; j < lisWZKC.size(); j++) {// 第一个去做的金额
					double wzsl = Double.parseDouble(lisWZKC.get(j).get("WZSL")
							+ "");
					double yksl = Double.parseDouble(lisWZKC.get(j).get("YKSL")
							+ "");
					if ((j == (lisWZKC.size() - 1)) && wzslall > (wzsl - yksl)) {
						throw new ModelDataOperationException("物资名称："
								+ lisWZXH.get(i).get("WZMC") + "库存不足!");
					}
					if (wzslall <= (wzsl - yksl)) {
						if (jgbz == 1) {
							wzjg = Double.parseDouble(lisWZKC.get(j)
									.get("WZJG") + "");
						} else {
							wzjg = bzjg;
						}
						czje += wzslall * wzjg;
						break;
					} else if (wzslall > (wzsl - yksl)) {
						wzslall = wzslall - (wzsl - yksl);
						if (jgbz == 1) {
							wzjg = Double.parseDouble(lisWZKC.get(j)
									.get("WZJG") + "");
						} else {
							wzjg = bzjg;
						}
						czje += (wzsl - yksl) * wzjg;
					}
				}
			}
			double wzdj = czje / czsl;
			res.put("body", wzdj);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取物质信息失败!", e);
		}

	}

	@SuppressWarnings("unchecked")
	public void doQueryOMRHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		// User user = (User) ctx.get("user.instance");
		// String JGID = user.get("manageUnit.id");// 用户的机构ID
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			// parameters.put("JGID", JGID);
			// long total =
			// dao.doCount(BSPHISEntryNames.MS_BRDA+" a,"+BSPHISEntryNames.MPI_DemographicInfo+" b",
			// "a.EMPIID = b.empiId and a.JDJG=:JGID", parameters);
			StringBuffer hql = new StringBuffer(
					"select a.JZXH as JZXH,a.GHXH as GHXH,a.BRBH as BRBH,b.EMPIID as EMPIID,a.JGID as JGID,c.JZHM as JZHM,b.MZHM as MZHM,b.BRXZ as BRXZ,b.BRXM as BRXM,b.BRXB as BRXB,b.CSNY as CSNY,d.BLBH as BLBH,d.BLLB as BLLB,d.BLMC as BLMC,d.BLLX as BLLX,d.MBLB as MBLB,d.BLZT as BLZT,"
							+ BSPHISUtil.toChar("c.GHSJ",
									"yyyy-MM-dd hh24:mi:ss")
							+ " as GHSJ,c.CZPB as CZPB,a.KSDM as KSDM,a.YSDM as YSDM,");
			hql.append("a.ZYZD as ZYZD,"
					+ BSPHISUtil.toChar("a.KSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as KSSJ,"
					+ BSPHISUtil.toChar("a.JSSJ", "yyyy-MM-dd hh24:mi:ss")
					+ " as JSSJ,a.JZZT as JZZT,a.YYXH as YYXH,a.FZRQ as FZRQ,a.GHFZ as GHFZ from ");
			hql.append(" YS_MZ_JZLS a,");
			hql.append(" MS_BRDA b,MS_GHMX c,");
			hql.append(" OMR_BL01 d,EMR_KBM_BLLB e where a.BRBH = b.BRID and a.GHXH=c.SBXH and d.JZXH = a.JZXH and a.BRBH = d.BRID and e.LBBH = d.BLLB");
			if (req.get("cnd") != null) {
				List<Object> cnd = (List<Object>) req.get("cnd");
				hql.append(" and " + ExpRunner.toString(cnd, ctx));
			}
			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			hql.append(" order by a.JZXH desc");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.cic.schemas.YS_MZ_JZLS_EMR");
			for (int i = 0; i < list.size(); i++) {
				list.get(i).put("cardNo", list.get(i).get("CARDNO"));
				list.get(i).put("idCard", list.get(i).get("IDCARD"));
			}
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取特殊药品判别
	 *
	 * @param body
	 *            MZHM
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void doGetTsypPb(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> pam = new HashMap<String, Object>();
		String TSYP = "";
		try {
			String YPXH = req.get("body") + "";
			pam.put("YPXH", YPXH);
			String hql = "select a.tsyp from YK_TYPK a where a.ypxh =:YPXH ";
			List<Map<String, Object>> rsMap = dao.doSqlQuery(hql.toString(),
					pam);
			if (!"".equals(rsMap) && rsMap != null && rsMap.size() > 0) {
				TSYP = rsMap.get(0) + "";
			} else {
				YPXH = "";
			}
			res.put("body", TSYP);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取特殊药品信息失败!", e);
		}
	}

	public void getHasClinicRecord(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String brid = body.get("brid") + "";
		String clinicId = body.get("clinicId") + "";
		List<Map<String, Object>> list = null;
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.BRBH", "l",
				Long.parseLong(brid));
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq",
				"to_char(a.KSSJ,'yyyy')", "s",
				Calendar.getInstance().get(Calendar.YEAR));
		List<Object> cnd3 = CNDHelper.createSimpleCnd("ne", "a.JZXH", "l",
				Long.parseLong(clinicId));
		List<Object> cnd4 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd3, cnd4);
		try {
			list = dao.doList(cnd, "",
					"phis.application.cic.schemas.YS_MZ_JZLS");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("查询本年度是否存在就诊记录失败!", e);
		}
		if (list != null && list.size() > 0) {
			res.put("hasClinicRecord", true);
		} else {
			res.put("hasClinicRecord", false);
		}

	}

	public void doSelectdybz(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		String jzxh = body.get("jzxh").toString();
		try {
			Map<String,Object> params = Param.instance().put("JZXH", Long.parseLong(jzxh));
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select sfdy as sfdy from YS_MZ_JZLS where JZXH=:JZXH ",
							params);
			long cf_count = dao.doCount(BSPHISEntryNames.MS_CF01_TABLE,
					"JZXH=:JZXH", params);
			long cz_count = dao.doCount(BSPHISEntryNames.MS_YJ01_TABLE,
					"JZXH=:JZXH", params);
			long zd_count = dao.doCount(BSPHISEntryNames.MS_BRZD_TABLE,
					"JZXH=:JZXH", params);
			long jkjy_count = dao.doCount(
					BSPHISEntryNames.HER_HealthRecipeRecord_MZ, "wayId=:JZXH",
					Param.instance().put("JZXH", jzxh));
			long hl_count = dao.doCount("MS_BCJL", "JZXH=:JZXH", params);
			long omr_count = dao.doCount("OMR_BL01", "JZXH=:JZXH", params);
			res.put("hasclinic", true);
			if (cf_count == 0 && cz_count == 0 && zd_count == 0
					&& hl_count == 0 && jkjy_count == 0 && omr_count == 0) {
				res.put("hasclinic", false);
			}else if (list.isEmpty()) {
				// throw new ModelDataOperationException("请先打印病历!");
				res.put("code", 301);
				res.put("msg", "请先打印病历!");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doSavedybz(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		String jzxh = body.get("jzxh").toString();
		try {
			dao.doSqlUpdate("UPDATE YS_MZ_JZLS set SFDY=1 where jzxh=:jzxh",
					Param.instance().put("jzxh", jzxh));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}

	}
	/**
	 * @Description:有医保卡和身份证号的病人属于可签约人群
	 * @param body
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 * @author
	 * @Modify:
	 */
	public void doQueryqybrxx(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		String empiid = body.get("empiid").toString();
		try {
			//该病人是否有医保卡
			List<Map<String, Object>> list = dao.doSqlQuery(
							" SELECT cardno as cardno, cardtypecode as cardtypecode, sfzh as sfzh " +
							" FROM mpi_card, ms_brda where mpi_card.empiid = ms_brda.empiid " +
							" and mpi_card.cardtypecode in ('98') and " +
							" mpi_card.empiid = :empiid",
							Param.instance().put("empiid", empiid));
			if (list.isEmpty()) {
				throw new ModelDataOperationException("该病人不属于签约范围!");
			}
			if(list.get(0).get("SFZH")==null || list.get(0).get("CARDNO")==null){
				throw new ModelDataOperationException("该病人信息不全!");
			}
			res.put("SFZH", list.get(0).get("SFZH"));
			res.put("CARDTYPECODE", list.get(0).get("CARDTYPECODE"));
			res.put("CARDNO", list.get(0).get("CARDNO"));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取病人相关信息
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doGetBrxx(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Object brid = body.get("brid");

		try {
			Map<String, Object> params = new HashMap();
			params.put("brid", brid);

			Map<String, Object> m = dao.doSqlLoad("select a.phrid from ehr_healthrecord a,ms_brda b where a.empiid=b.empiid and b.brid=:brid ",params);

			return m;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病人相关信息失败!", e);
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2015-6-10
	 * @description 病历书写向导XDBH查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryBlsxxd(Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql_ys = new StringBuffer();// 当前医生
		hql_ys.append("select XDBH  as XDBH  from YS_MZ_BLXD01 where ZFBZ=0 AND XDLB = :xdlb and SSLB = 1 AND SSDM = :ssdm");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("xdlb", MedicineUtils.parseString(body.get("XDLB")));
		map_par.put("ssdm", user.getId() + "");
		try {
			List<Map<String, Object>> l = dao.doQuery(hql_ys.toString(),
					map_par);
			if (l != null && l.size() > 0) {
				int xdbh = MedicineUtils.parseInt(l.get(0).get("XDBH"));
				if (xdbh != 0) {
					return xdbh;
				}
			}
			StringBuffer hql_ks = new StringBuffer();// 当前科室
			hql_ks.append("select XDBH  as XDBH  from YS_MZ_BLXD01 where ZFBZ=0 AND XDLB = :xdlb and SSLB = 2 AND SSDM = :ssdm");
			map_par.put("ssdm", user.getProperty("biz_departmentId") + "");
			l = dao.doQuery(hql_ks.toString(), map_par);
			if (l != null && l.size() > 0) {
				int xdbh = MedicineUtils.parseInt(l.get(0).get("XDBH"));
				if (xdbh != 0) {
					return xdbh;
				}
			}
			StringBuffer hql_qy = new StringBuffer();// 全院
			hql_qy.append("select XDBH  as XDBH  from YS_MZ_BLXD01 where ZFBZ=0 AND XDLB = :xdlb and SSLB = 3 ");
			map_par.remove("ssdm");
			l = dao.doQuery(hql_qy.toString(), map_par);
			if (l != null && l.size() > 0) {
				int xdbh = MedicineUtils.parseInt(l.get(0).get("XDBH"));
				if (xdbh != 0) {
					return xdbh;
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询XDBH失败", e);
		}
		return 0;
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2015-6-10
	 * @description 病历复制
	 * @updateInfo
	 * @param brid
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> blfzQuery(long brid,long jzxh)
			throws ModelDataOperationException {
        //zhaojian 2017-11-14 门诊医生站增加症状的下拉框选择功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC
        //String hql = "select ZSXX as ZSXX,XBS as XBS,JWS as JWS,TGJC as TGJC,FZJC as FZJC,T as T,P as P,R as R,SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT,FT as FT,FX as FX,PZ as PZ,QT as QT,DPY as DPY,W as W,H as H,BMI as BMI,BQGZ as BQGZ,RSBZ as RSBZ,JZXH as JZXH from MS_BCJL where BRID=:brid and JZXH!=:jzxh order by JZXH desc";
        String hql = "select ZSXX as ZSXX,XBS as XBS,JWS as JWS,TGJC as TGJC,FZJC as FZJC,T as T,P as P,R as R," +
        		" SSY as SSY,SZY as SZY,KS as KS,YT as YT,HXKN as HXKN,OT as OT,FT as FT,FX as FX,PZ as PZ," +
        		" QT as QT,DPY as DPY,W as W,H as H,BMI as BMI,BQGZ as BQGZ,RSBZ as RSBZ,YCFJM as YCFJM,JZXH as JZXH," +
        		" ZZDM as ZZDM,ZZMC as ZZMC,JKJYNR as JKJYNR,GMS as GMS from MS_BCJL where BRID=:brid and JZXH!=:jzxh order by JZXH desc";
        Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("brid", brid);
		map_par.put("jzxh", jzxh);
		try {
			List<Map<String, Object>> l = dao.doQuery(hql, map_par);
			if (l != null && l.size() > 0 && l.get(0) != null) {
				Map<String, Object> record = l.get(0);
				List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
						MedicineUtils.parseString(record.get("JZXH")));
				List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s",
						"01");
				List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
				List<Map<String, Object>> list = dao
						.doList(cnd, "recordId",
								"phis.application.cic.schemas.HER_HealthRecipeRecord_MZ");
				record.put("JKCFRecords", list);
				return record;
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void doRemoveBcjl(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			dao.removeByFieldValue("JZXH", Long.parseLong(body.get("clinicId").toString()), BSPHISEntryNames.MS_BCJL);
		} catch (NumberFormatException e) {
			logger.error("validate fail to delete ms_bcjl", e);
			throw new ModelDataOperationException("病历信息保存失败!", e);
		} catch (PersistentDataOperationException e) {
			logger.error("validate fail to delete ms_bcjl", e);
			throw new ModelDataOperationException("病历信息保存失败!", e);
		}
	}
	public void doQueryyuyuexx(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		String empiid = body.get("empiid").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String userid=user.getUserId();
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(
							" SELECT demo.idCard as IDCARD, '01' as CARDTYPECODE,demo.personName as PERSONNAME," +
							" demo.sexCode as SEXCODE,demo.mobileNumber as MOBILENUMBER," +
							" to_char(demo.birthday,'yyyy-mm-dd') as BIRTHDAY  " +
							" FROM MPI_DemographicInfo demo, ms_brda where demo.empiid = ms_brda.empiid " +
							" and demo.empiid = :empiid",
							Param.instance().put("empiid", empiid));
			if (list.isEmpty()) {
				throw new ModelDataOperationException("该病人不属于签约范围!");
			}
			if(list.get(0).get("IDCARD")==null){
				throw new ModelDataOperationException("该病人信息不全!");
			}
			res.put("IDCARD", list.get(0).get("IDCARD"));
			res.put("PERSONNAME", list.get(0).get("PERSONNAME"));
			res.put("SEXCODE", list.get(0).get("SEXCODE"));
			res.put("MOBILENUMBER", list.get(0).get("MOBILENUMBER"));
			res.put("BIRTHDAY", list.get(0).get("BIRTHDAY"));
			res.put("CARDTYPECODE", list.get(0).get("CARDTYPECODE"));
			res.put("CARDNO", list.get(0).get("CARDNO"));
			Map<String, Object> ysxx = dao.doSqlLoad(
					"select CARDNUM as CARDNUM from SYS_Personnel where PERSONID=:PERSONID",
					Param.instance().put("PERSONID", userid));
			res.put("CARDNUM", ysxx.get("CARDNUM"));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开处方前判断是否有诊断
	 * @param body
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doCheckHasDiagnose(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		Long brid = Long.parseLong(body.get("BRID").toString());
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = (String)user.getManageUnitId();
		//系统参数：诊断录入判断 0：不启用 1：启用  默认0
		int ZDLRPD = Integer.parseInt(ParameterUtil.getParameter(jgid,
				"ZDLRPD", ctx));
		if(ZDLRPD>0){
			Map<String, Object> para = new HashMap<String, Object>();
			para.put("JGID",jgid);
			para.put("BRID",brid);
			try {
				long ybgh = dao.doCount("MS_BRZD", "JGID=:JGID and BRID=:BRID",
						para);
				if(ybgh>0){
					res.put("HasDiagnose", true);
				}else{
					res.put("HasDiagnose", false);
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}else{
			res.put("HasDiagnose", true);
		}
	}

	/**
	 * 查询处方笺模板
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("deprecation")
	public void doQueryCfjmb(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		int index = Integer.parseInt(body.get("type").toString())-1;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
//		//系统参数：处方打印模板，0表示套打，1表示其他，默认是0
//		int CFDYMB = Integer.parseInt(ParameterUtil.getParameter(jgid,
//				"CFDYMB", ctx));
		String mbmcstr=DictionaryController.instance().getDic("phis.dictionary.cfjmb").getText(jgid);
		if(mbmcstr!=null && !"".equals(mbmcstr)){
			String[] mcStr = mbmcstr.split(",");
			res.put("cfjmbmc", mcStr[index]);
		}else{
			res.put("cfjmbmc", "");
		}
	}

	/**
	 * 检查库存数量是否足够
	 * @param body
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doCheckKcslEnough(List<Map<String, Object>> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String message = "";
		if(body.size()>0){
			for(Map<String, Object> cfd : body){
				if("".equals(cfd.get("YPCD"))){
					continue;
				}
				Long ypxh = Long.parseLong(cfd.get("YPXH")+"");
				Long ypcd = Long.parseLong(cfd.get("YPCD")+"");
				Double ypsl = Double.parseDouble(cfd.get("YPSL")+"");
				Map<String, Object> para = new HashMap<String, Object>();
				para.put("JGID",jgid);
				para.put("YPXH",ypxh);
				para.put("YPCD",ypcd);
				try {
					List<Map<String, Object>> list = dao.doSqlQuery("select b.JGID,b.YPXH,b.YPCD,b.XMLX,d.YPMC,(c.YPSL - (case b.XMLX when 3 then sum(b.YPSL*b.CFTS) else sum(b.YPSL) end)) as KCSL " +
							"from MS_CF01 a,MS_CF02 b,YF_KCMX c,YK_TYPK d " +
							"where a.JGID = b.JGID and a.JGID = c.JGID and a.CFSB = b.CFSB and a.YFSB = c.YFSB and b.YPXH = c.YPXH and b.YPCD = c.YPCD and b.YPXH = d.YPXH " +
							"and a.FPHM is not null and a.FYBZ = 0 and a.ZFPB = 0 and c.JYBZ = 0 and a.JGID=:JGID and b.YPXH=:YPXH and b.YPCD=:YPCD " +
							"group by b.JGID,b.YPXH,b.YPCD,b.XMLX,d.YPMC,c.YPSL",para);
					if(list.size()>0){
						Map<String, Object> dfycf = list.get(0);
						Long kcsl = Long.parseLong(dfycf.get("KCSL")+"");
						String ypmc = dfycf.get("YPMC")+"";
						if(kcsl - ypsl < 0){//库存不足
							message+="【"+ypmc+"】库存不足！库存数量："+kcsl.toString()+",实际数量："+cfd.get("YPSL")+";";
						}
					}
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(!"".equals(message)){
			throw new ModelDataOperationException(message);
		}

	}

	public void doQueryKsxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao2, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		if(req.get("deptId")==null){
			res.put("chinedept", 0);
			return;
		}
		String deptId = req.get("deptId").toString();
		String sql = "select b.ID as ksdm,b.OFFICENAME as ksmc,b.CHINEDEPT as chinedept " +
				"from SYS_Office b where b.ORGANIZCODE=:JGID and b.ID=:OFFICECODE";
		try {
			Map<String, Object> para = new HashMap<String, Object>();
			para.put("JGID",jgid);
			para.put("OFFICECODE",deptId);
			List<Map<String, Object>> list = dao.doSqlQuery(sql, para);
			res.put("chinedept", list.size()>0?list.get(0).get("CHINEDEPT"):null);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("科室信息查询失败！", e);
		}
	}

	/**
	 * 检查药品使用情况：在指定时间段内已开过哪些相同的药品或检查
	 * zhaojian 2017-11-02
	 * @param body
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doCheckYpsy(Map<String, Object> body, Context ctx,
			Map<String, Object> res)
			throws ModelDataOperationException {
		String sfzh = body.get("SFZH").toString();
		String brid = body.get("BRID").toString();
		String ypxhs = body.get("YPXH").toString();
		String ylxhs = body.get("YLXH").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> params = new HashMap<String, Object>();

		String hql = "";
		String hql_yp ="";
		String hql_jc="";
		try {
		if(!ypxhs.equals("")){
			// 获取系统参数 重复用药判定周期
			String cfyypdzq = ParameterUtil.getParameter(manageUnit,
					BSPHISSystemArgument.CFYYPDZQ, ctx);
			Integer txzq_yp =  (cfyypdzq.equals("null")||cfyypdzq.equals(""))?15:Integer.parseInt(cfyypdzq);
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.DATE, txzq_yp*(-1));
	        Date tagday = c.getTime();
	        hql_yp = "SELECT (SELECT ORGANIZNAME FROM SYS_ORGANIZATION WHERE ORGANIZCODE=A.JGID) ORGANIZNAME,KFRQ KDRQ," +
				"(SELECT YPMC FROM YK_TYPK WHERE YPXH=A.YPXH) MC," +
				"(SELECT CDMC FROM YK_CDDZ WHERE YPCD = A.YPCD) CDMC FROM (" +
						"SELECT YPXH,YPCD,JGID,(SELECT KFRQ FROM MS_CF01 WHERE CFSB=MS_CF02.CFSB) KFRQ" +
						" FROM MS_CF02 WHERE CFSB IN(" +
								"SELECT CFSB FROM MS_CF01 A,MS_BRDA B WHERE A.BRID=B.BRID AND A.BRID='"+brid+"'" +
								" AND A.KFRQ>TO_DATE('"+sdf.format(tagday)+"','YYYY-MM-DD HH24:MI:SS') " +
								" AND B.SFZH='"+sfzh+"'" +
								" AND A.ZFPB=0 AND A.FPHM IS NOT NULL) " +
						" AND YPXH IN("+ypxhs+")" +
				")A WHERE A.KFRQ=(SELECT MAX(KFRQ) FROM (" +
						"SELECT YPXH,JGID,(SELECT KFRQ FROM MS_CF01 WHERE CFSB=MS_CF02.CFSB) KFRQ" +
						" FROM MS_CF02 WHERE CFSB IN(" +
								"SELECT CFSB FROM MS_CF01 A,MS_BRDA B WHERE A.BRID=B.BRID AND A.BRID='"+brid+"'" +
								" AND A.KFRQ>TO_DATE('"+sdf.format(tagday)+"','YYYY-MM-DD HH24:MI:SS') " +
								" AND B.SFZH='"+sfzh+"'" +
								" AND A.ZFPB=0 AND A.FPHM IS NOT NULL)" +
						" AND YPXH IN("+ypxhs+")" +
				")B WHERE A.YPXH=B.YPXH)";
	        //params.put("YPXH", ypxhs);
		}
		if(!ylxhs.equals("")){
			// 获取系统参数 重复检查判定周期
			String cfjcpdzq = ParameterUtil.getParameter(manageUnit,
					BSPHISSystemArgument.CFJCPDZQ, ctx);
			Integer txzq_jc =  (cfjcpdzq.equals("null")||cfjcpdzq.equals(""))?15:Integer.parseInt(cfjcpdzq);
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.DATE, txzq_jc*(-1));
	        Date tagday = c.getTime();
	        hql_jc = "SELECT (SELECT ORGANIZNAME FROM SYS_ORGANIZATION WHERE ORGANIZCODE=A.JGID) ORGANIZNAME,KDRQ," +
				"(SELECT FYMC FROM GY_YLSF WHERE FYXH=A.YLXH) MC," +
				"'' CDMC FROM (" +
						"SELECT YLXH,JGID,(SELECT KDRQ FROM MS_YJ01 WHERE YJXH=MS_YJ02.YJXH) KDRQ" +
						" FROM MS_YJ02 WHERE YJXH IN(" +
								"SELECT YJXH FROM MS_YJ01 A,MS_BRDA B WHERE A.BRID=B.BRID AND A.BRID='"+brid+"'" +
								" AND A.KDRQ>TO_DATE('"+sdf.format(tagday)+"','YYYY-MM-DD HH24:MI:SS') " +
								" AND B.SFZH='"+sfzh+"'" +
								" AND A.ZFPB=0 AND A.FPHM IS NOT NULL)" +
						" AND YLXH IN("+ylxhs+") AND XMLX>0" +
				")A WHERE A.KDRQ=(SELECT MAX(KDRQ) FROM (" +
						"SELECT YLXH,JGID,(SELECT KDRQ FROM MS_YJ01 WHERE YJXH=MS_YJ02.YJXH) KDRQ" +
						" FROM MS_YJ02 WHERE YJXH IN(" +
								"SELECT YJXH FROM MS_YJ01 A,MS_BRDA B WHERE A.BRID=B.BRID AND A.BRID='"+brid+"'" +
								" AND A.KDRQ>TO_DATE('"+sdf.format(tagday)+"','YYYY-MM-DD HH24:MI:SS') " +
								" AND B.SFZH='"+sfzh+"'" +
								" AND A.ZFPB=0 AND A.FPHM IS NOT NULL) AND YLXH IN("+ylxhs+")" +
						" AND XMLX>0" +
				")B WHERE A.YLXH=B.YLXH)";
	        //params.put("YLXH", ylxhs);
		}
		if(!hql_yp.equals("") && !hql_jc.equals("")){
			hql = "SELECT * FROM ("+hql_yp+" UNION ALL "+hql_jc+") C ORDER BY ORGANIZNAME,KDRQ DESC";
		}
		else{
			hql = "SELECT * FROM ("+hql_yp+hql_jc+") C ORDER BY ORGANIZNAME,KDRQ DESC";
		}
		//params.put("SFZH", "'"+sfzh+"'");
		//params.put("BRID", "'"+brid+"'");
		List<Map<String, Object>> list = dao.doSqlQuery(hql, params);
		res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取以往处方或检查信息失败!", e);
		}
	}



	/**
	 * 根据省编码获取药品信息
	 * zhaojian 2017-11-21
	 * @param body
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doGetYpFromSbm(Map<String, Object> body, Context ctx,
			Map<String, Object> res)
			throws ModelDataOperationException {
		String ypxhs = body.get("YPXH").toString();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> params = new HashMap<String, Object>();
		String hql ="";
		try {
			if(!ypxhs.equals("")){
		        hql = "select to_char(wmsys.wm_concat(ypmc)) as ypmc from (" +
		        		"select ypmc as ypmc from yk_typk where sbm in(select distinct sbm from yk_typk where ypxh in("+ypxhs+")) " +
		        		"union " +
		        		"select trade_name as ypmc from gy_yp_ejyy where sbm in(select distinct sbm from yk_typk where ypxh in("+ypxhs+"))" +
		        		")";
		        List<Map<String, Object>> list = dao.doSqlQuery(hql, params);
				res.put("body", list);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("根据省编码获取药品信息失败!");
		}
	}

//用药提醒 wy

	public void doGetSbmByYpxh(Map<String, Object> body, Map<String, Object> res, Context ctx) throws ModelDataOperationException, PersistentDataOperationException {


		/** 查询医疗机构代码 */
		String manageUnit = (String) body.get("manageUnit");
		String dyHql = "select jgdm_dr as YLJGDM from ehr_manageunit where manageunitid='" + manageUnit + "'";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> dy = dao.doSqlLoad(dyHql, parameters);
		String yljgdm = "";
		if (dy != null) {
			yljgdm = (String) dy.get("YLJGDM");
		}


		StringBuffer sb = new StringBuffer();
		List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
		for (Map<String, Object> map : list) {
			String ypxh = map.get("YPXH") + "";
			String sbm = getSbmByXH(ypxh);
			String pra = "    <ITEM>        \n" +
					"      <BMLX>10</BMLX>\n" +
					"      <YBYPDM>" + sbm + "</YBYPDM>\n" +
					"    </ITEM>       \n";
			sb.append(pra);

		}
		String kh = body.get("KH") + "";
		String YYKSBM = body.get("YYKSBM") + "";
		String YYYSGH = body.get("YYYSGH") + "";
		String YSXM = body.get("YSXM") + "";
		String pxml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
				"<YL_ACTIVE_ROOT>\n" +
				"  <CFDDM>3103</CFDDM>           \n" +
				"  <KH>" + kh + "</KH>\n" +
				"  <KLX>01</KLX>           \n" +
				"  <YLJGDM>" + yljgdm + "</YLJGDM> \n" +
				"  <JZLX>100</JZLX>\n" +
				"  <YYKSBM>" + YYKSBM + "</YYKSBM>\n" +
				"  <YYYSGH>" + YYYSGH + "</YYYSGH>\n" +
				"  <AGENTIP>10.1.7.11</AGENTIP>\n" +
				"  <AGENTMAC>ff-ff-ff-ff-ff</AGENTMAC>\n" +
				"  <YSXM>" + YYYSGH + "</YSXM>\n" +
				"  <YP> \n" +
				sb.toString() +
				"  </YP>       \n" +
				"</YL_ACTIVE_ROOT>";
		System.out.println(pxml);
		String ip = body.get("ip") + "";
		RemindServer.sendMsgToRemind(pxml, ip);

	}

	private String getSbmByXH(String ypxh) throws PersistentDataOperationException {
		try{
			String sql = "select sbm as sbm from yk_typk where YPXH=:ypxh";
			Map<String, Object> pra = new HashMap<String, Object>();
			pra.put("ypxh", ypxh);

			List<Map<String, Object>> maps = dao.doSqlQuery(sql, pra);
			if (maps.size() > 0) {
				return maps.get(0).get("SBM") + "";
			}
		}catch(Exception e){
		}
		return "";
	}


	/**
	 * 合理用药 多科室多处方查询
	 *
	 * @param cfsbs
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unused")
	public Map<String, Object> loadOtherKSCF01(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> res = new HashMap<String, Object>();
		long brid = Long.parseLong(body.get("brid") + "");
		long cfsb = Long.parseLong(body.get("cfsb") + "");
		String ksrq = body.get("kfrq") + " 00:00:00";
		String zzrq = body.get("kfrq") + " 23:59:59";
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> parameters = new HashMap<String, Object>();
			// //当天(开方日期)、当前机构、当前机构下所有科室 该病人的处方
			// hql.append("select a.CFSB as CFSB,a.KSDM as KSDM,a.YSDM as YSDM,to_char(a.KFRQ,'YYYY-MM-DD HH24:MI:SS') as KFRQ,case a.CFLX  when 1 then '西药方' when 2 then '中成药方' else '草药方' end as CFLX,"
			// +
			// " b.SBXH as SBXH,b.YPXH as YPXH,c.YPMC as YPMC,c.ZXDW as ZXDW,c.JLDW as JLDW,c.YPDW as YPDW,b.YFGG as YFGG,b.YCJL as YCJL,b.YFDW as YFDW,b.YPYF as YPYF,b.YPZH as YPZH,b.GYTJ as GYTJ,d.XMMC as GYTJ_TEXT,"
			// + " e.KSMC as KSMC,f.YGXM as YSXM "
			// +
			// " FROM MS_CF01 a,MS_CF02 b,YK_TYPK c,ZY_YPYF d,GY_KSDM e,GY_YGDM f"
			// +
			// " where a.BRID=:BRID and a.JGID=:JGID and a.CFSB<>:CFSB and a.KFRQ >=:KSRQ  and a.KFRQ <=:ZZRQ and a.ZFPB = 0 "
			// +
			// " and a.CFSB = b.CFSB and b.YPXH = c.YPXH and b.GYTJ = d.YPYF and a.KSDM = e.KSDM and a.YSDM = f.YGDM "
			// + " order by a.CFSB ");
			// parameters.put("JGID", jgid);
			// 当天(开方日期)、全区、所有科室 该病人的处方
			hql.append("select a.CFSB as CFSB,a.KSDM as KSDM,a.YSDM as YSDM,to_char(a.KFRQ,'YYYY-MM-DD HH24:MI:SS') as KFRQ,case a.CFLX  when 1 then '西药方' when 2 then '中成药方' else '草药方' end as CFLX,"
					+ " b.SBXH as SBXH,b.YPXH as YPXH,c.YPMC as YPMC,c.ZXDW as ZXDW,c.JLDW as JLDW,c.YPDW as YPDW,b.YFGG as YFGG,b.YCJL as YCJL,b.YFDW as YFDW,b.YPYF as YPYF,b.YPZH as YPZH,b.GYTJ as GYTJ,d.XMMC as GYTJ_TEXT,"
					+ " e.KSMC as KSMC,f.YGXM as YSXM,g.MRCS || '/' || g.ZXZQ as YYPL,b.YYTS as YYTS, b.ypcd as YPCD,g.pcmc as PCMC,g.PCBM as PCBM,b.YPZH as YPZH,b.YPSL as YPSL "
					+ " FROM MS_CF01 a,MS_CF02 b,YK_TYPK c,ZY_YPYF d,GY_KSDM e,GY_YGDM f,GY_SYPC g"
					+ " where a.BRID=:BRID  and a.CFSB=:CFSB and a.KFRQ >=:KSRQ  and a.KFRQ <=:ZZRQ and a.ZFPB = 0 "
					+ " and a.CFSB = b.CFSB and b.YPXH = c.YPXH and b.GYTJ = d.YPYF and a.KSDM = e.KSDM and a.YSDM = f.YGDM and b.YPYF = g.PCBM "
					+ " order by a.CFSB ");
			parameters.put("BRID", brid);
			parameters.put("CFSB", cfsb);
			parameters.put("KSRQ", BSHISUtil.toDate(ksrq));
			parameters.put("ZZRQ", BSHISUtil.toDate(zzrq));
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			res.put("cf01s", list);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to load ms_cf02 information by database reason",
					e);
			throw new ModelDataOperationException("合理用药,处方信息查找失败！", e);
		}
		return res;
	}
	/**
	 * 合理用药 多科室多处方查询
	 *
	 * @param cfsbs
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unused")
	public Map<String, Object> loadOtherBrzd(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		long brid = Long.parseLong(body.get("brid") + "");
		long jzxh = Long.parseLong(body.get("jzxh") + "");
		Map<String, Object> res = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = " select zdmc as ZDMC,zdxh as ZDXH,ICD10 as ICD10 from ms_brzd where brid=:brid and jzxh=:jzxh";
		parameters.put("brid", brid);
		parameters.put("jzxh", jzxh);
		List<Map<String, Object>> list;
		try {
			list = dao.doSqlQuery(sql.toString(),
					parameters);
			res.put("brzd01", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("合理用药,病人诊断查找失败！", e);
		}

		return res;
	}
	public Map<String, Object> getjsdqyjcxx(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		String empiid = body.get("empiid") + "";
		Map<String, Object> res = new HashMap<String, Object>();
		String sql="select regionCode as REGIONCODE from EHR_HealthRecord where empiId=:empiId ";
		Map<String, Object> p=new HashMap<String, Object>();
		p.put("empiId", empiid);
		try {
			Map<String, Object> regionCodemap=dao.doSqlLoad(sql, p);
			if (regionCodemap!=null){
			    String regionCode=regionCodemap.get("REGIONCODE")==null?"":regionCodemap.get("REGIONCODE")+"";
			    if(regionCode!=null && regionCode.length() >0 ){
				    Map<String, Object> r=new HashMap<String, Object>();
			    	Map<String, Object> data=new HashMap<String, Object>();
			   	   String hql="select a.regionName as REGIONNAME from EHR_AreaGrid a where a.regionCode=:regionCode";
				if(regionCode.length() >2){
					r.put("regionCode", regionCode.substring(0,2));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_province", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_provincecode", regionCode.substring(0,2)+"");
				}
				if(regionCode.length() >4){
					r.put("regionCode", regionCode.substring(0,4));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_city", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_citycode", regionCode.substring(0,4)+"");
				}
				if(regionCode.length() >6){
					r.put("regionCode", regionCode.substring(0,6));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_district", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_districtcode",regionCode.substring(0,6)+"");
				}
				if(regionCode.length() >9){
					r.put("regionCode", regionCode.substring(0,9));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_street", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
					res.put("signer_streetcode",regionCode.substring(0,9)+"");
				}
				if(regionCode.length() >12){
					r.put("regionCode", regionCode.substring(0,12));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_community", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
				}
				if(regionCode.length() >15){
					r.put("regionCode", regionCode.substring(0,15));
					data=dao.doSqlLoad(hql, r);
					res.put("signer_unit", data.get("REGIONNAME")==null?"":data.get("REGIONNAME")+"");
				}
				String groupsql="select a.FS_PersonGroup as FS_PERSONGROUP from EHR_FamilyContractService a " +
						" where a.FS_EmpiId=:empiId";
				Map<String, Object> groupmap=dao.doSqlLoad(groupsql, p);
				String groupstr="";
				if(groupmap!=null && groupmap.size() >0 ){
					groupstr=groupmap.get("FS_PERSONGROUP")+"";
				}
				String signer_category="";
				String signer_category_type="";
				if(groupstr.indexOf("老年人") >=0){
					signer_category+=";01";
					signer_category_type=";65 岁以上老年人";
				}
				if(groupstr.indexOf("儿童") >=0){
					signer_category+=";02";
					signer_category_type=";0~6 岁儿童";
				}
				if(groupstr.indexOf("孕产妇") >=0){
					signer_category+=";03";
					signer_category_type=";孕产妇";
				}
				if(groupstr.indexOf("高血压") >=0){
					signer_category+=";04";
					signer_category_type=";高血压患者";
				}
				if(groupstr.indexOf("糖尿病") >=0){
					signer_category+=";05";
					signer_category_type=";糖尿病患者";
				}
				if(groupstr.indexOf("结核病") >=0){
					signer_category+=";06";
					signer_category_type=";结核病患者";
				}
				if(groupstr.indexOf("精神病") >=0){
					signer_category+=";07";
					signer_category_type=";重性精神病患者";
				}
				if(groupstr.indexOf("残疾") >=0){
					signer_category+=";08";
					signer_category_type=";残疾人";
				}
				if(groupstr.indexOf("优抚") >=0){
					signer_category+=";09";
					signer_category_type=";优抚对象";
				}
				if(groupstr.indexOf("特扶") >=0){
					signer_category+=";10";
					signer_category_type=";特扶人员";
				}
				if(groupstr.indexOf("贫困") >=0){
					signer_category+=";11";
					signer_category_type=";贫困人群";
				}
				if(signer_category.length() >0){
					signer_category=signer_category.substring(1,signer_category.length());
					signer_category_type=signer_category.substring(1,signer_category.length());
				}else{
					signer_category="99";
					signer_category_type="其他";
				}
				res.put("signer_category",signer_category);
				res.put("signer_category_type",signer_category_type);
				UserRoleToken user = UserRoleToken.getCurrent();
				String uid=user.getUserId();
				res.put("manageUnit",user.getManageUnit().getId());
				res.put("userIdcode",user.getUserId());
				String usql="select a.cardnum as CARDNUM from SYS_Personnel a where a.personId=:personId";
				p.remove("empiId");
				p.put("personId",uid);
				Map<String, Object> umap=dao.doSqlLoad(usql, p);
				res.put("doct_iden",umap.get("CARDNUM")==null?"":umap.get("CARDNUM")+"");

				if(body.containsKey("card") && "1".equals(body.get("card")+"")){
					String cardsql="select a.cardno as CARDNO from mpi_card a where a.cardtypecode in ('02','03') " +
							" and a.empiid=:empiId";
				if(p.containsKey("personId")){
					p.remove("personId");
				}
				p.put("empiId", empiid);
				List<Map<String, Object>> cardlist=dao.doSqlQuery(cardsql, p);
				if(cardlist!=null && cardlist.size() >0){
					res.put("referral_minum", cardlist.get(0).get("CARDNO")+"");
				}
				}
			}
		}else{
				res.put("signer_provincecode","32");
				res.put("signer_citycode", "3201");
				res.put("signer_districtcode","320124");
				res.put("signer_streetcode", "320124000");
				UserRoleToken user = UserRoleToken.getCurrent();
				String uid=user.getUserId();
				String manageUnit = user.getManageUnit().getId();
				res.put("userIdcode",user.getUserId());//2018-09-18 获取医生账号  Wangjls
				res.put("manageUnit",user.getManageUnit().getId());
			}
	} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 保存传染病报告卡
	 * @param req
	 * @param ctx
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doSaveIdrReport(Map<String, Object> req, Context ctx,
			Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String empiId = (String) reqBodyMap.get("empiId");
		String phrId = (String) reqBodyMap.get("phrId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("finishStatus", "0");
	//
		if(reqBodyMap.get("RecordID")==null){
			reqBodyMap.put("RecordID", Long.parseLong("1"));
		}
		Map<String, Object> rsMap = null;
		if(S.isEmpty(phrId)){
			Map<String, Object> hrMap = null;
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
			List<Map<String, Object>> list;
			try {
				StringBuffer hql = new StringBuffer();
				//Map<String, Object> parameters = new HashMap<String, Object>();
				hql.append("select phrId as phrId from EHR_HealthRecord where empiId=:empiId");
				parameters.put("empiId", empiId);
				list = dao.doQuery(hql.toString(), parameters);
				if (list != null && list.size() > 0) {
					hrMap = list.get(0);
				}
				if(hrMap != null && hrMap.size() > 0){
					phrId = (String) hrMap.get("phrId");
					reqBodyMap.put("phrId", phrId);
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			rsMap = dao.doSave(op, BSPHISEntryNames.IDR_Report, reqBodyMap, true);
			parameters.put("RecordID", Long.parseLong(reqBodyMap.get("RecordID")+""));
			StringBuffer hql2 = new StringBuffer();
			hql2.append("update IDR_Report set finishStatus=:finishStatus where RecordID=:RecordID");
			parameters.remove("empiId");
			dao.doUpdate(hql2.toString(), parameters);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("body", rsMap);
	}

	/**
	 * 根据门诊诊断记录编号查询是否已保存传染病报告卡
	 * @param req
	 * @param ctx
	 * @param res
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> doQueryIdrReport(Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		boolean saveFlag = false;
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String MS_BRZD_JLBH = String.valueOf(reqBodyMap.get("MS_BRZD_JLBH"));
		String EMPIID = String.valueOf(reqBodyMap.get("EMPIID"));
		String ICD10 = String.valueOf(reqBodyMap.get("ICD10"));
		if(MS_BRZD_JLBH!=null && !"".equals(MS_BRZD_JLBH) && !"null".equals(MS_BRZD_JLBH)){
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			StringBuffer hql = new StringBuffer();
			Map<String, Object> parameters = new HashMap<String, Object>();
			hql.append("select count(*) as num from IDR_Report where MS_BRZD_JLBH=:MS_BRZD_JLBH");
			parameters.put("MS_BRZD_JLBH", MS_BRZD_JLBH);
			List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
			StringBuffer hql1 = new StringBuffer();
			Map<String, Object> parameters1 = new HashMap<String, Object>();
			hql1.append("select count(*) as num from IDR_Report where empiId=:EMPIID and icd10=:ICD10");
			parameters1.put("EMPIID", EMPIID);
			parameters1.put("ICD10", ICD10);
			try {
				list = dao.doQuery(hql.toString(), parameters);
				if(list.size()>0){
					Map<String,Object> resultMap = list.get(0);
					long num = Long.parseLong(resultMap.get("num")+"");
					if(num>0){//报告卡已保存
						saveFlag = true;
						res.put("isSaved", saveFlag);
					}
				}

				list1 = dao.doQuery(hql1.toString(), parameters1);
				if(list1.size()>0){
					Map<String,Object> resultMap1 = list1.get(0);
					long num1 = Long.parseLong(resultMap1.get("num")+"");
					if(num1>0){//报告卡已保存
						saveFlag = true;
						res.put("isSaved", saveFlag);
					}
				}
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		res.put("isSaved", saveFlag);
		return res;
	}

	/**
	 * 审核传染病报告卡
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doVerify(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ir = (Map<String, Object>) body.get("IDR_Report");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("finishDate", new Date());
		parameters.put("finishReason", ir.get("finishReason")!=null?ir.get("finishReason").toString():"");
		parameters.put("finishStatus", ir.get("finishStatus")!=null?ir.get("finishStatus").toString():"");
		parameters.put("RecordID", Long.parseLong(ir.get("RecordID")+""));
		StringBuffer hql = new StringBuffer();
		hql.append("update IDR_Report set finishDate=:finishDate,finishReason=:finishReason,finishStatus=:finishStatus where RecordID=:RecordID");
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "审核失败");
		}
	}

	/**
	 * 弃审传染病报告卡
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doCancelVerify(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> ir = (Map<String, Object>) body.get("IDR_Report");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("finishDate", null);
		parameters.put("finishReason", null);
		parameters.put("finishStatus", "0");
		parameters.put("RecordID", ir.get("RecordID").toString());
		StringBuffer hql = new StringBuffer();
		hql.append("update IDR_Report set finishDate=:finishDate,finishReason=:finishReason,finishStatus=:finishStatus,finishStatusEnd=:finishStatus where RecordID=:RecordID");
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "弃审失败");
		}
	}

	/**
	 * 传染病报告卡退回
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doCancel(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> ir = (Map<String, Object>) body.get("IDR_Report");
		Map<String, Object> parameters = new HashMap<String, Object>();
	//	parameters.put("finishDate", null);
		//parameters.put("finishReason", null);
		//parameters.put("finishStatus", "0");
		parameters.put("RecordID", ir.get("RecordID").toString());
		parameters.put("finishStatus", "3");
		StringBuffer hql = new StringBuffer();
		hql.append("update IDR_Report set finishStatusEnd=:finishStatus where RecordID=:RecordID");
		try {
			dao.doSqlUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Storage records save fails", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退回失败");
		}
	}

	/**
	 * 审核传染病报告卡
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doReportIdr(Map<String, Object> body, Context ctx,Map<String, Object> res)
			throws ModelDataOperationException {
		Map<String, Object> idr = (Map<String, Object>) body.get("IDR_Report");
		String strxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<DataExchange>"
				+"<eventHead>"
				+"<entityName>IDR_NCD_CODRIS</entityName>";//交换公用的传染病IDR
		    String birthday = "";
		    String age = "";
	        String sexCode = "";
	        int year = Calendar.getInstance().get(Calendar.YEAR);
		if(idr.get("reportFlag").toString().equals("1")){
			strxml +="<operateType>Mod</operateType>";//Add:新增、Mod:修订、Del:删除
		}else{
			strxml +="<operateType>Add</operateType>";//Add:新增、Mod:修订、Del:删除
		}
		String jbbz = idr.get("categoryAInfectious").equals("")?"":idr.get("categoryAInfectious").toString();
		jbbz = idr.get("categoryBInfectious").equals("")?jbbz:idr.get("categoryBInfectious").toString();
		jbbz = idr.get("categoryCInfectious").equals("")?jbbz:idr.get("categoryCInfectious").toString();
		jbbz = idr.get("viralHepatitis").equals("")?jbbz:idr.get("viralHepatitis").toString();
		jbbz = idr.get("anthrax").equals("")?jbbz:idr.get("anthrax").toString();
		jbbz = idr.get("dysentery").equals("")?jbbz:idr.get("dysentery").toString();
		jbbz = idr.get("phthisis").equals("")?jbbz:idr.get("phthisis").toString();
		jbbz = idr.get("typhia").equals("")?jbbz:idr.get("typhia").toString();
		jbbz = idr.get("syphilis").equals("")?jbbz:idr.get("syphilis").toString();
		jbbz = idr.get("malaria").equals("")?jbbz:idr.get("malaria").toString();
		//通过字典对照获取疾病病种对应的省病种ICD10码、病种名称
		String jbICD10 = DictionaryController.instance().getDic("phis.dictionary.CrbbkJbbz").getText(jbbz);
		String jbmc = "";
		DictionaryItem dicCrbbk = DictionaryController.instance().getDic("phis.dictionary.CrbbkJbbz").getItem(jbbz);
		if(dicCrbbk!=null){
			jbmc = dicCrbbk.getProperty("name").toString();
		}
		  if ( idr.get("idCard").toString().length() == 15) {
	            birthday = "19" + idr.get("idCard").toString().substring(6, 8) + "-"
	                    + idr.get("idCard").toString().substring(8, 10) + "-"
	                    + idr.get("idCard").toString().substring(10, 12);
	            sexCode = Integer.parseInt(idr.get("idCard").toString().substring(idr.get("idCard").toString().length() - 3, idr.get("idCard").toString().length())) % 2 == 0 ? "2" : "1";
	            age = (year - Integer.parseInt("19" + idr.get("idCard").toString().substring(6, 8))) + "";
	        } else if (idr.get("idCard").toString().length() == 18) {
	            birthday = idr.get("idCard").toString().substring(6, 10) + "-"
	                    + idr.get("idCard").toString().substring(10, 12) + "-"
	                    + idr.get("idCard").toString().substring(12, 14);
	            sexCode = Integer.parseInt(idr.get("idCard").toString().substring(idr.get("idCard").toString().length() - 4, idr.get("idCard").toString().length() - 1)) % 2 == 0 ? "2" : "1";
	            age = (year - Integer.parseInt(idr.get("idCard").toString().substring(6, 10))) + "";
	        }


		//通过字典对照获取当前录入机构对应的省机构编码及名称
		//录入单位编码
		String lrdwbm = DictionaryController.instance().getDic("phis.dictionary.CrbbkJgdz").getText(idr.get("createUnit").toString().substring(0,9));
		String lrdwmc = "";//录入单位名称
		DictionaryItem dicLrdw = DictionaryController.instance().getDic("phis.dictionary.CrbbkJgdz").getItem(idr.get("createUnit").toString().substring(0,9));
		if(dicLrdw!=null){
			lrdwmc = dicLrdw.getProperty("name").toString();
		}
		//报告单位编码
		String bgdwbm = DictionaryController.instance().getDic("phis.dictionary.CrbbkJgdz").getText(idr.get("reportUnit").toString().substring(0,9));
		//报告单位名称
		String bgdwmc = "";
		DictionaryItem dicBgdw = DictionaryController.instance().getDic("phis.dictionary.CrbbkJgdz").getItem(idr.get("reportUnit").toString().substring(0,9));
		if(dicBgdw!=null){
			bgdwmc = dicBgdw.getProperty("name").toString();
		}
		//根据档案的管辖机构获取对应的省区划编码
		String qhbm = "";
		DictionaryItem dicQhbm = DictionaryController.instance().getDic("phis.dictionary.CrbbkJgdz").getItem(idr.get("manaUnitId").toString().substring(0,9));
		if(dicQhbm!=null){
			qhbm = dicQhbm.getProperty("areacode").toString();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//+"<RealAge>"+(idr.get("fullAge")==null?"":idr.get("fullAge").toString())+"</RealAge>"//实足年龄-岁
		if(idr.get("fullAge")==null){
			return;
		}
		String fullAge = idr.get("fullAge").toString();
		String realAge = fullAge.substring(0,fullAge.indexOf("岁"));
		String realMonth = fullAge.replace(realAge +"岁", "");
		realMonth = realMonth.indexOf("月")>-1?realMonth.substring(0,realMonth.indexOf("月")):realMonth;
		String realDay = fullAge.replace(realAge +"岁", "").replace(realMonth+"月", "");
		realDay = realDay.indexOf("天")>-1?realDay.substring(0,realDay.indexOf("天")):realDay;
/*		Integer[] countage = new Integer[]{0,0,0};
		try {
			countage = countAge(sdf.parse(idr.get("idCard").toString().substring(6, 10) + "-"
                    + idr.get("idCard").toString().substring(10, 12) + "-"
                    + idr.get("idCard").toString().substring(12, 14)+" 00:00:00"),sdf.parse(idr.get("createDate").toString()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			System.out.println("获取实足年龄报错："+e1.getMessage());
		}*/
		strxml +="</eventHead>"
				+"<eventBody>"
				+"<CardId></CardId>"//卡片编号，删除、修改时需提供，新增时为空
				+"<BaseInfo>"
				+"<PatientName>"+idr.get("personName").toString()+"</PatientName>"//患者/死者姓名
				+"<IDCardCode>"+idr.get("idCard").toString()+"</IDCardCode>"//证件号码
				+"<BirthDate>"+(idr.get("birthday")==null?"":idr.get("birthday").toString().substring(0,10))+"</BirthDate>"//出生日期
				//+"<RealAge>"+countage[0]+"</RealAge>"//实足年龄-岁
/*				+"<RealAge>"+(countage[0]==0?1:countage[0])+"</RealAge>"//实足年龄-岁
				+"<RealMonth>"+(countage[0]==0?0:countage[01])+"</RealMonth>"//实足年龄-月
				+"<RealDay>"+(countage[0]==0?0:countage[2])+"</RealDay>"//实足年龄-天
*/				+"<RealAge>"+realAge+"</RealAge>"//实足年龄-岁
				+"<RealMonth>"+realMonth+"</RealMonth>"//实足年龄-月
				+"<RealDay>"+realDay+"</RealDay>"//实足年龄-天
				+"<GenderCode>"+(idr.get("sexCode")==null?"":idr.get("sexCode").toString())+"</GenderCode>"//性别编码
				//+"<GenderCode>"+sexCode+"</GenderCode>"//性别编码
				+"<NationCode>"+(idr.get("nationCode")==null?"":idr.get("nationCode").toString())+"</NationCode>"//民族编码
				+"<MaritalStatusCode>"+(idr.get("maritalStatusCode")==null?"":idr.get("maritalStatusCode").toString())+"</MaritalStatusCode>"//婚姻状况代码
				+"<EducationLevelCode>"+(idr.get("educationCode")==null?"":idr.get("educationCode").toString())+"</EducationLevelCode>"//文化程度代码
				+"<EmployerOrgName>"+(idr.get("workPlace")==null?"":idr.get("workPlace").toString())+"</EmployerOrgName>"//死者的家属住址或患者工作单位
				+"<TeleCom>"+(idr.get("mobileNumber")==null?"":idr.get("mobileNumber").toString())+"</TeleCom>"//联系电话
				+"<OccupationCode>"+(idr.get("patientJob")==null?"":idr.get("patientJob").toString())+"</OccupationCode>"//职业编码/人群分类
				+"<OtherOccupationName>"+(idr.get("otherPatientJob")==null?"":idr.get("otherPatientJob").toString())+"</OtherOccupationName>"//其他具体职业
				+"<GuardianName>"+(idr.get("parentsName")==null?"":idr.get("parentsName").toString())+"</GuardianName>"//患者家长姓名
				+"<LivingAddressAttributionCode>"+(idr.get("birthPlace")==null?"":idr.get("birthPlace").toString())+"</LivingAddressAttributionCode>"//现住址类型编码
				+"<LivingAddressCode>"+qhbm+"</LivingAddressCode>"//现住址编码
				+"<LivingAddressDetails>"+(idr.get("address")==null?"":idr.get("address").toString())+"</LivingAddressDetails>"//现住址详细地址
				+"<DomicileAddressAttributionCode>"+(idr.get("birthPlace")==null?"":idr.get("birthPlace").toString())+"</DomicileAddressAttributionCode> "//户籍地类型编码
				+"<DomicileAddressCode>"+qhbm+"</DomicileAddressCode>"//户籍地编码
				+"<DomicileAdrressDetails>"+(idr.get("address")==null?"":idr.get("address").toString())+"</DomicileAdrressDetails>"//户籍地详细地址
				+"<DeathDate>"+(idr.get("deathDate")==null?"":idr.get("deathDate").toString().substring(0,10))+"</DeathDate>"//死亡日期
				+"</BaseInfo>"
				+"<IDRCard>"
				+"<CardID></CardID>"//卡片编号
				+"<CaseClassificationCode>"+((idr.get("casemixCategory2")==null || idr.get("casemixCategory2").equals(""))?"0":idr.get("casemixCategory2").toString())+"</CaseClassificationCode>"//病例分类
				+"<DiagnosisTypeName>"+(idr.get("casemixCategory1")==null?"":idr.get("casemixCategory1").toString())+"</DiagnosisTypeName>"//传染病诊断代码
				+"<DiseaseCode>"+jbbz+"</DiseaseCode>"//传染病编码
				+"<DiseaseICD10Code>"+jbICD10+"</DiseaseICD10Code>"//传染病ICD10编码
				+"<OnsetDate>"+(idr.get("dateAccident")==null?"":idr.get("dateAccident").toString().substring(0,10))+"</OnsetDate>"//发病日期
				//+"<DiagnosisDateHour>"+(idr.get("diagnosedDate")==null?"":idr.get("diagnosedDate").toString().substring(0,13))+"</DiagnosisDateHour>"//诊断日期-小时
				+"<DiagnosisDateHour>"+(idr.get("createDate")==null?"":idr.get("createDate").toString().substring(0,13))+"</DiagnosisDateHour>"//诊断日期-小时
				+"<OtherDiseaseName>"+(idr.get("otherCategoryInfectious")==null?"":idr.get("otherCategoryInfectious").toString())+"</OtherDiseaseName>"//其他重点监测传染病
				+"<OtherDiseaseNameMark></OtherDiseaseNameMark>"//其他疾病
				//****艾滋病/HIV等附卡(开始)****/
				+"<VenerealHistoryCode></VenerealHistoryCode>"//性病史
				+"<ContactHistoryCode></ContactHistoryCode>"//接触史编码
				+"<OtherContactHistory></OtherContactHistory>"//其他接触史
				+"<InfectionRouteName></InfectionRouteName>"//感染途径
				+"<OtherInfectionRouteName></OtherInfectionRouteName>"//其他感染途径
				+"<SampleSource></SampleSource>"//样本来源
				+"<OtherSampleSource></OtherSampleSource>"//其他样本来源
				+"<LaborTestConclusionCode></LaborTestConclusionCode>"//实验室检测结论编码
				+"<ConfirmedTestPositiveDate></ConfirmedTestPositiveDate>"//确认（替代策略、核酸）监测阳性日期
				+"<ConfirmedTestPositiveOrgName></ConfirmedTestPositiveOrgName>"//确认（替代策略、核酸）检测单位
				+"<AIDSDiagnosisDate></AIDSDiagnosisDate>"//艾滋病确诊时间
				+"<ChlamydialTrachomatisCode></ChlamydialTrachomatisCode>"//生殖道沙眼衣原体感染编码
				+"<ChlamydialTrachomatisName></ChlamydialTrachomatisName>"//生殖道沙眼衣原体感染名称
				//****艾滋病/HIV等附卡(结束)****/
				//****梅毒附卡(开始)****/
				+"<SyphilisTestConclusionCode></SyphilisTestConclusionCode>"//检验结果（含滴度）
				+"<SyphiliClinicalManifestation></SyphiliClinicalManifestation>"//特征性临床表现
				+"<SyphiliReportingDepartment></SyphiliReportingDepartment>"//报告科室
				//****梅毒附卡(结束)****/
				//****乙肝附卡(开始)****/
				+"<HBsAgPositiveTime></HBsAgPositiveTime>"
				+"<HBsAgFirstAppearYear></HBsAgFirstAppearYear>"
				+"<HBsAgFirstAppearMonth></HBsAgFirstAppearMonth>"
				+"<HBsAgFirstAppearUnknown></HBsAgFirstAppearUnknown>"
				+"<AlTCode></AlTCode>"
				+"<HBcIgM1TestConclusionCode></HBcIgM1TestConclusionCode>"
				+"<LiverPunctureTestConclusionCode></LiverPunctureTestConclusionCode>"
				+"<HBsAgToHBsTime></HBsAgToHBsTime>"
				//****乙肝附卡(结束)****/
				//****AFP附卡(开始)****/
				+"<PatientResidenceTypeCode></PatientResidenceTypeCode >"
				+"<PalsyDate></PalsyDate>"
				+"<PalsySymptom></PalsySymptom>"
				//****AFP附卡(结束)****/
				//****手足口附卡(开始)****/
				+"<IntensivePatientCode></IntensivePatientCode>"
				+"<LaborTestResultCode></LaborTestResultCode>"
				//****手足口附卡(结束)****/
				+"<CloseContactsSymptomCode>0</CloseContactsSymptomCode>"//密切接触者有无相同症状
				+"<OrgCountyName>江苏省南京市溧水区</OrgCountyName>"//报告地区
				+"<OrgName>"+bgdwbm+"</OrgName>"//报告单位编码
				+"<OrgAreaName>"+bgdwmc+"</OrgAreaName>"//报告单位名称
				+"<ReportName>"+(idr.get("reportDoctor_text")==null?"":idr.get("reportDoctor_text").toString())+"</ReportName>"//报告人
				+"<FillCardDate>"+(idr.get("fillDate")==null?"":idr.get("fillDate").toString().substring(0,10))+"</FillCardDate>"//报告时间（填卡时间）
				+"<RecordUnit>32012400</RecordUnit>"//录入地区编码
				+"<acceptName>"+lrdwbm+"</acceptName>"//录入单位编码
				+"<acceptAreaName>"+lrdwmc+"</acceptAreaName>"//录入单位名称
				+"<customer>"+(idr.get("reportDoctor_text")==null?"":idr.get("reportDoctor_text").toString())+"</customer>"//录入人
				+"<LoginName></LoginName>"//国家大疫情系统登陆账号名

				+"<OrgCountyCode>32012400</OrgCountyCode>"//报告单位所属县区编码
				+"<recordTime>"+(idr.get("createDate")==null?"":idr.get("createDate").toString().substring(0,10))+"</recordTime>"//录入时间
				+"<CardNotes>"+(idr.get("comments")==null?"":idr.get("comments").toString())+"</CardNotes>"//备注
				+"</IDRCard>"
				+"<DeleteInfo>"
				+"<DeletingReasonDetails></DeletingReasonDetails>"//删除原因（删除操作时使用）
				+"<DeletingTypeCode></DeletingTypeCode>"//删除类型编码（删除操作时使用）
				+"<DeletingTypeName></DeletingTypeName>"//删除类型名称（删除操作时使用）
				+"</DeleteInfo>"
				+"</eventBody>"
				+"</DataExchange>";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String url = ParameterUtil.getParameter(manageUnit.substring(0,6), "CRBBKMIDDLESERVICEIP", ctx);
		LSCrbbkMiddleServiceLocator service = new LSCrbbkMiddleServiceLocator();
		service.setLSCrbbkMiddleServiceHttpSoap11EndpointEndpointAddress(url);
		String resdata = "";
		try{
/*			LSCrbbkMiddleServiceSoap11BindingStub lsms = (LSCrbbkMiddleServiceSoap11BindingStub)service.getLSCrbbkMiddleServiceHttpSoap11Endpoint();
			System.out.println("----"+sdf.format(new Date())+" 开始调用传染病报卡上报接口中间服务："+url+"----");
			System.out.println("请求参数为："+strxml);
			resdata = lsms.reportCard(strxml);
			System.out.println("----"+sdf.format(new Date())+" 完成传染病报卡上报接口中间服务调用，返回信息："+resdata+"----");*/
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("RecordID", Long.parseLong(idr.get("RecordID").toString()));
			StringBuffer hql = new StringBuffer();
			hql.append("update IDR_Report set reportFlag=:reportFlag where RecordID=:RecordID");
//			if(resdata.contains("success")){
				parameters.put("reportFlag", "1");
				res.put("reportresult","上报成功！");
/*			}else{
				parameters.put("reportFlag", "2");
				res.put("reportresult",resdata);
			}*/
			try{
				dao.doUpdate(hql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				logger.error("Storage records update fails", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "更新上报状态失败");
			}
		}catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	public static Integer[] countAge(Date from,Date tager) {
		String age = "";
		int year = 0;
		int month = 0;
		int day = 0;
		if (from == null) {
			age = age + 0;
		} else {
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTime(from);
			c2.setTime(tager);
			if (c1.after(c2)) {
				throw new IllegalArgumentException("生日不能超过当前日期");
			}
			int from_year = c1.get(Calendar.YEAR);
			int from_month = c1.get(Calendar.MONTH) + 1;
			int from_day = c1.get(Calendar.DAY_OF_MONTH);
			System.out.println("以前：" + from_year + "-" + from_month + "-"
					+ from_day);
			int MaxDayOfMonth = c1.getActualMaximum(Calendar.DAY_OF_MONTH);
			// System.out.println(MaxDayOfMonth);
			int to_year = c2.get(Calendar.YEAR);
			int to_month = c2.get(Calendar.MONTH) + 1;
			int to_day = c2.get(Calendar.DAY_OF_MONTH);
			System.out.println("现在：" + to_year + "-" + to_month + "-" + to_day);

			year = to_year - from_year;
			if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) < 0) {
				year = year - 1;
			}
			if (year < 1) {// 小于一岁要精确到月份和天数
				System.out.println("--------");
				if (to_month - from_month > 0) {
					month = to_month - from_month;
					if (to_day - from_day < 0) {
						month = month - 1;
						day = to_day - from_day + MaxDayOfMonth;
					} else {
						day = to_day - from_day;
					}
				} else if (to_month - from_month == 0) {
					if (to_day - from_day > 0) {
						day = to_day - from_day;
					}
				}
			}
			if (year > 1) {
				age = age + year + "岁";
			} else if (month > 0) {
				age = age + month + "月" + day + "天";
			} else {
				age = age  + "0月"+ day + "天";
			}
		}
		// System.out.println(year + "--" + month + "--" + day);
		return new Integer[]{year,month,day};
	}
	/**
	 *
	 * @author zhaohj
	 * @param res
	 * @param ctx
	 * @createDate 2019-08-27
	 * @description 查询医保病种
	 * @updateInfo
	 * @param idcard
	 * @throws ModelDataOperationException
	 */
	public void doQueryYBMC(Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		String idCard = body.get("idCard") + "";
		params.put("idCard", idCard);

		try {
               String hql = "select a.jbbm as jbbm,a.jbmc as jbmc from YB_JBBM a ,(select regexp_substr(id, '[^,]+', 1, rownum) id" +
        		" from (select ybmmbz||','||ybmtbz as  id from njjb_kxx where sfzh = '" +idCard+
        		"' and rownum=1 )" +
        		" connect by rownum <= length(regexp_replace(id, '[^,]+'))+1) b where a.jbbm=b.id and a.zfpb=0";
			List<Map<String, Object>> list = dao.doSqlQuery(hql, null);
			if (list != null && list.size() > 0 && list.get(0) != null) {
				//int size=list.size();
			//	for(int i = 0; i <size; i = i + 1) {
				Map<String, Object> record = list.get(0);
				//list = dao.doSqlQuery(hql, null);
				params.put("ybmc", list);
				//return params;
			//	}
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return null;
		//reBody.put("JKCFRecords", list);
		res.put("body", params);
	}

	/**
	 *
	 * @author xh
	 * @createDate 2020-06-30
	 * @description 查询是否有处方
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doCheckCF (Map<String, Object> body, Context ctx,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		params.put("GHXH", body.get("GHXH"));
		try {
			long count = dao.doSqlCount("MS_CF01", "JZXH=(select JZXH as JZXH from YS_MZ_JZLS where GHXH= :GHXH)", params);
			if(count > 0){
				res.put("body", true);
			}else{
				res.put("body", false);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> doLoadGmyw(Object ypmc, Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		if (ypmc != null) {
			String hql = "select pydm as pydm,wbdm as wbdm from yk_typk where pspb=1 and YPMC =:YPMC";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("YPMC", ypmc);
			try {
				List<Map<String, Object>> list = dao.doQuery(hql, params);
				res.put("gy_gmyw", list);
			} catch (PersistentDataOperationException e) {
				logger.error("fail to load gy_gmyw information by database reason", e);
				throw new ModelDataOperationException("过敏药信息查找失败！", e);
			}
		}
		return res;
	}


	public List<Map<String, Object>> doLoadGmy(Map<String, Object> body, Context ctx, Map<String, Object> res) throws ModelDataOperationException {
		List<Map<String, Object>> ywgmy = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> fywgmy = new ArrayList<Map<String, Object>>();
		String brid = body.get("BRID") + "";
		Map<String, Object> parame = new HashMap<String, Object>();
		parame.put("BRID", brid);
		// 查询是否有该记录
		try {
			ywgmy = dao.doQuery("select  a.GMY as GMY,a.GMDJ as GMDJ from MS_GMJL a, MS_BRDA b where a.EMPIID = b.EMPIID and b.BRID = :BRID and ZFPB = 0 and SFYWBZ = 1 group by a.GMY,a.GMDJ", parame);
			fywgmy = dao.doQuery("select  a.GMY as GMY,a.GMDJ as GMDJ from MS_GMJL a, MS_BRDA b where a.EMPIID = b.EMPIID and b.BRID = :BRID and ZFPB = 0 and SFYWBZ = 0 group by a.GMY,a.GMDJ", parame);

		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("ywgm", ywgmy);
		res.put("fywgm", fywgmy);
		return ywgmy;
	}


	public void doSaveGmy(Map<String, Object> body, Map<String, Object> res, Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		List<Map<String, Object>> fywgm = new ArrayList<Map<String, Object>>();
		String brid = body.get("BRID") + "";
		String ywgm = "";
		if (!"".equals(body.get("FYWGM"))) {
			fywgm = (List<Map<String, Object>>) body.get("FYWGM");
		}
		if (!"".equals(body.get("YWGM"))) {
			ywgm = body.get("YWGM") + "";
		}
		String[] ywgm_arr = ywgm.split("\\,");
		Map<String, Object> parame = new HashMap<String, Object>();
		parame.put("BRID", brid);
		// 废除数据库中此次没有勾选的过敏记录
		try {
			List<Map<String, Object>> gmyList = dao.doQuery("select a.EMPIID as EMPIID,a.ZFPB as ZFPB,a.GMY as GMY from MS_GMJL a,MS_BRDA b where a.EMPIID=b.EMPIID and b.BRID=:BRID and ZFPB=0 ", parame);
			String[] arrStrings = new String[gmyList.size()];
			for (int j = 0; j < gmyList.size(); j++) {
				arrStrings[j] = gmyList.get(j).get("GMY") + "";
			}
			String[] arrStrings1 = new String[fywgm.size()];
			for (int j = 0; j < fywgm.size(); j++) {
				arrStrings1[j] = fywgm.get(j).get("name") + "";
			}
			String[] dd = new String[ywgm_arr.length + arrStrings1.length];
			System.arraycopy(ywgm_arr, 0, dd, 0, ywgm_arr.length);
			System.arraycopy(arrStrings1, 0, dd, ywgm_arr.length, arrStrings1.length);
			for (int i = 0; i < arrStrings.length; i++) {
				if (!Arrays.asList(dd).contains(arrStrings[i])) {
					// 作废该记录
					Map<String, Object> para = new HashMap<String, Object>();
					para.put("EMPIID", gmyList.get(0).get("EMPIID"));
					para.put("GMY", arrStrings[i]);
					para.put("ZFGH", uid);
					para.put("ZFSJ", new Date());
					// 作废原有数据
					dao.doUpdate("update MS_GMJL set ZFPB = 1,ZFGH=:ZFGH,ZFSJ=:ZFSJ WHERE  GMY=:GMY and EMPIID=:EMPIID", para);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ywgm_arr[0] == "") {// 防止没有药物过敏原时插入空数据

		} else {
			for (int i = 0; i < ywgm_arr.length; i++) {// 药物过敏原
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BRID", brid);
				parameters.put("GMY", ywgm_arr[i] + "");
				// 查询是否有该记录
				try {
					List<Map<String, Object>> cfList = dao.doQuery("select a.EMPIID as EMPIID from MS_GMJL a,MS_BRDA b where a.EMPIID=b.EMPIID and b.BRID=:BRID and a.GMY=:GMY and a.ZFPB=0", parameters);
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					parameters1.put("BRID", brid);
					List<Map<String, Object>> cfList1 = dao.doQuery("select a.EMPIID as EMPIID from MS_BRDA a where  a.BRID=:BRID ", parameters1);
					if (cfList.size() == 0) {// 不存在该记录
						// 新增记录
						Map<String, Object> gmjl = new HashMap<String, Object>();
						gmjl.put("EMPIID", cfList1.get(0).get("EMPIID"));
						gmjl.put("SFYWBZ", 1);// 否
						gmjl.put("GMY", ywgm_arr[i]);
						gmjl.put("GMDJ", "");
						gmjl.put("JLLY", "手工录入");
						gmjl.put("CZGH", uid);
						gmjl.put("CZSJ", new Date());
						gmjl.put("ZFPB", 0);
						try {
							dao.doInsert("phis.application.cic.schemas.MS_GMJL", gmjl, false);
						} catch (ValidateException e) {
							e.printStackTrace();
						}
					}
				} catch (PersistentDataOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < fywgm.size(); i++) {// 非药物过敏源
			Map<String, Object> dyfs = fywgm.get(i);
			String key = dyfs.get("name") + "";
			String value = dyfs.get("value") + "";
			Map<String, Object> MS_ZHFK = new HashMap<String, Object>();
			MS_ZHFK.put("GMDJ", value + "");
			MS_ZHFK.put("GMY", key + "");
			try {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BRID", brid);
				parameters.put("GMY", key + "");
				// 查询过敏等级是否相同
				List<Map<String, Object>> cfList = dao.doQuery("select a.GMDJ as GMDJ ,a.EMPIID as EMPIID from MS_GMJL a,MS_BRDA b where a.EMPIID=b.EMPIID and b.BRID=:BRID and a.ZFPB=0 and a.GMY=:GMY", parameters);

				if (cfList.size() > 0) {// 存在该记录

					if (!cfList.get(0).get("GMDJ").equals(value)) {// 等级不相同，作废原有数据，新增一条
						Map<String, Object> para = new HashMap<String, Object>();
						para.put("EMPIID", cfList.get(0).get("EMPIID"));
						para.put("GMY", key);
						para.put("ZFGH", uid);
						para.put("ZFSJ", new Date());
						// 作废原有数据
						dao.doUpdate("update MS_GMJL set ZFPB = 1,ZFGH=:ZFGH,ZFSJ=:ZFSJ WHERE  GMY=:GMY and EMPIID=:EMPIID", para);
						// 新增记录
						Map<String, Object> gmjl = new HashMap<String, Object>();
						gmjl.put("EMPIID", cfList.get(0).get("EMPIID"));
						gmjl.put("SFYWBZ", 0);// 否
						gmjl.put("GMY", key);
						gmjl.put("GMDJ", value);
						gmjl.put("JLLY", "手工录入");
						gmjl.put("CZGH", uid);
						gmjl.put("CZSJ", new Date());
						gmjl.put("ZFPB", 0);
						dao.doInsert("phis.application.cic.schemas.MS_GMJL", gmjl, false);
					}
				} else {// 没有该记录,新增
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					parameters1.put("BRID", brid);
					List<Map<String, Object>> cfList1 = dao.doQuery("select a.EMPIID as EMPIID from MS_BRDA a where  a.BRID=:BRID ", parameters1);
					Map<String, Object> gmjl = new HashMap<String, Object>();
					gmjl.put("EMPIID", cfList1.get(0).get("EMPIID"));
					gmjl.put("SFYWBZ", 0);// 否
					gmjl.put("GMY", key);
					gmjl.put("GMDJ", value);
					gmjl.put("JLLY", "手工录入");
					gmjl.put("CZGH", uid);
					gmjl.put("CZSJ", new Date());
					gmjl.put("ZFPB", 0);
					dao.doInsert("phis.application.cic.schemas.MS_GMJL", gmjl, false);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void doQueryBRXX(Map<String, Object> req,Map<String, Object> res,Context ctx)throws ModelDataOperationException  {
		long brid = 0l;
		String empiId = "";
		long jzxh = 0l;
		brid = MedicineUtils.parseLong(req.get("brid"));
		empiId = MedicineUtils.parseString(req.get("empiId"));
		jzxh = MedicineUtils.parseLong(req.get("jzxh"));
		Map<String,Object> params = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select BRID as BRID,BRXM as BRXM,BRXB as BRXB,CSNY as CSNY,SFZH as SFZH,BRDZ as BRDZ," +
				"BRDH as BRDH from MS_BRDA where BRID = :brid");
		StringBuffer hql1 = new StringBuffer();
		hql1.append("select T as T,P as P,R as R,SSY as SSY,SZY as SZY " +
				"from MS_BCJL where BRID = :brid and JZXH = (select JZXH from (select JZXH from YS_MZ_JZLS where BRBH = :brid order by JZXH desc) where ROWNUM = 1 )");
		try {
			params.put("brid", brid);
			List<Map<String, Object>> brxx = dao.doSqlQuery(hql.toString(), params);
			if(brxx != null && brxx.size()>0) {
				brxx.get(0).put("T","");
				brxx.get(0).put("P","");
				brxx.get(0).put("R","");
				brxx.get(0).put("SSY","");
				brxx.get(0).put("SZY","");
				List<Map<String, Object>> bcjl = dao.doSqlQuery(hql1.toString(), params);//查询当前就诊序号已填写信息
				if(bcjl != null && bcjl.size()>0){
					brxx.get(0).put("T",MedicineUtils.parseString(bcjl.get(0).get("T")));
					brxx.get(0).put("P",MedicineUtils.parseString(bcjl.get(0).get("P")));
					brxx.get(0).put("R",MedicineUtils.parseString(bcjl.get(0).get("R")));
					brxx.get(0).put("SSY",MedicineUtils.parseString(bcjl.get(0).get("SSY")));
					brxx.get(0).put("SZY",MedicineUtils.parseString(bcjl.get(0).get("SZY")));
				}
			}
			res.put("body",brxx.get(0));
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病人信息失败!", e);
		}
	}

	public void doQuerySBXH(Map<String, Object> req,Map<String, Object> res,Context ctx)throws ModelDataOperationException  {
		long jzxh = 0;
		jzxh = MedicineUtils.parseLong(req.get("jzxh"));
		Map<String,Object> params = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select SBXH as SBXH from MS_MZFR where LYBS=:jzxh and SJLY=2");
		try {
			params.put("jzxh",jzxh);
			List<Map<String, Object>> sbxh = dao.doSqlQuery(hql.toString(), params);
			if(sbxh != null && sbxh.size()>0) {
				res.put("SBXH",MedicineUtils.parseLong(sbxh.get(0).get("SBXH")));
			}else{
				res.put("SBXH",0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取识别序号失败!", e);
		}
	}

}
