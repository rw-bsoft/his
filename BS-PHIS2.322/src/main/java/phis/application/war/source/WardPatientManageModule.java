package phis.application.war.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
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
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class WardPatientManageModule {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(WardPatientManageModule.class);

	public WardPatientManageModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取当前是否有医嘱变动信息 返回Map对象格式 title : 提示信息标题 message ： 提示信息内容
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadWardRemindInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> msg = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			String manageUnit = user.getManageUnitId();
			String brbq = user.getProperty("wardId") + "";
			// Date now = DateUtils.addMilliseconds(new Date(),
			// -Integer.parseInt(body.get("interval").toString()));
			String today = BSHISUtil.getDate() + " 00:00:00";
			Date now = DateUtils.parseDate(today,
					new String[] { "yyyy-MM-dd HH:mm:ss" });
			// System.out.println(now);
			StringBuilder message = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JGID", manageUnit);
			params.put("BQDM", brbq);
			params.put("beginTime", now);
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select distinct a.ZYHM as ZYHM,a.BRXM,a.BRCH from ZY_BRRY a,ZY_BQYZ b where a.ZYH=b.ZYH and a.BRBQ=:BQDM and a.JGID=:JGID and b.YSTJ=1 and b.FHBZ=0 and b.YZPB=0 and b.LSBZ=0 and a.CYPB=0 and b.KSSJ>=:beginTime",
							params);
			for (Map<String, Object> m : list) {
				message.append(m.get("BRCH") + "床病人【" + m.get("BRXM")
						+ "】有新开医嘱信息，请及时处理!<br />");
			}
			/*****begin停嘱提醒 qihao 2018-8-30******/
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("JGID", manageUnit);
			params1.put("BQDM", brbq);
			List<Map<String, Object>> list1 = dao
					.doSqlQuery(
							"select distinct a.ZYHM as ZYHM,a.BRXM,a.BRCH from ZY_BRRY a,ZY_BQYZ b where a.ZYH=b.ZYH and a.BRBQ=:BQDM and a.JGID=:JGID and B.LSYZ = 0 and b.TZFHBZ=0 and b.YZPB=0 and b.LSBZ=0 and a.CYPB=0 and b.TZSJ is not null",
							params1);
			for (Map<String, Object> m : list1) {
				message.append(m.get("BRCH") + "床病人【" + m.get("BRXM")
						+ "】有停嘱信息，请及时处理!<br />");
				}
			/*****end停嘱提醒 qihao 2018-8-30******/
			if (message.length() == 0)
				return null;
			msg.put("title", "病区病人管理");
			msg.put("message", message.toString());
			msg.put("hashcode", message.toString().hashCode());// 判定是否同一提示信息
			System.out.println(msg);
			return msg;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("无效的SQL语句!", e);
		} catch (ParseException e) {
			throw new ModelDataOperationException("无效的SQL语句!", e);
		}
	}

	/**
	 * 根据住院号码获取住院号
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doLoadZyhByZyhm(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			// 只支持oracle的写法 REGEXP_LIKE(zyhm,'^(0+)$')
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select a.ZYH as ZYH,a.BRID as BRID,b.EMPIID as EMPIID,a.BRBQ as BRBQ,a.MQZD as JBMC,a.CYRQ,a.MQZD as MQZD,a.ZYHM,a.BRXM,a.BRXZ,a.RYRQ,a.ZYCS,a.BRXB,a.BRQK,a.HLJB,a.ZSYS,a.CYPB,a.BRKS,a.CSNY,a.CYPB,a.ZKZT from "
									+ "ZY_BRRY a,MS_BRDA b where a.BRID=b.BRID and REGEXP_LIKE(a.ZYHM,'^(0*)'||:ZYHM||'$') and a.JGID=:JGID",
							body);
			for (Map<String, Object> record : list) {
				record.put(
						"BRXB_text",
						DictionaryController.instance()
								.get("phis.dictionary.gender")
								.getText(record.get("BRXB") + ""));
				record.put(
						"BRXZ_text",
						DictionaryController.instance()
								.get("phis.dictionary.patientProperties")
								.getText(record.get("BRXZ") + ""));
				record.put(
						"ZSYS_text",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(record.get("ZSYS") + ""));
				record.put(
						"BRKS_text",
						DictionaryController.instance()
								.get("phis.dictionary.department")
								.getText(record.get("BRKS") + ""));
				if (record.get("ZYH") != null
						&& record.get("ZYH").toString().trim().length() > 0) {
					Map<String, Object> ret = BSPHISUtil.getPersonAge(
							(Date) record.get("CSNY"),
							(Date) record.get("RYRQ"));
					record.put("AGE", ret.get("age"));
				}
			}
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("请输入有效的住院号码!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取数据字典失败!", e);
		}
	}

	/**
	 * 根据床位号码获取住院号
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doLoadZyhByCwh(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		try {
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select a.ZYH as ZYH,a.BRID as BRID,b.EMPIID as EMPIID,a.BRBQ as BRBQ,a.MQZD as JBMC,a.CYRQ,a.MQZD as MQZD,a.ZYHM,a.BRXM,a.BRXZ,a.RYRQ,a.ZYCS,a.BRXB,a.BRQK,a.HLJB,a.ZSYS,a.CYPB,a.BRKS,a.CSNY,a.CYPB,a.ZKZT from "
									+ "ZY_BRRY a,MS_BRDA b,ZY_CWSZ c where a.BRID=b.BRID and a.ZYH=c.ZYH and c.BRCH=:BRCH and a.JGID=:JGID",
							body);
			for (Map<String, Object> record : list) {
				record.put(
						"BRXB_text",
						DictionaryController.instance()
								.get("phis.dictionary.gender")
								.getText(record.get("BRXB") + ""));
				record.put(
						"BRXZ_text",
						DictionaryController.instance()
								.get("phis.dictionary.patientProperties")
								.getText(record.get("BRXZ") + ""));
				record.put(
						"ZSYS_text",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(record.get("ZSYS") + ""));
				record.put(
						"BRKS_text",
						DictionaryController.instance()
								.get("phis.dictionary.department")
								.getText(record.get("BRKS") + ""));
				if (record.get("ZYH") != null
						&& record.get("ZYH").toString().trim().length() > 0) {
					Map<String, Object> ret = BSPHISUtil.getPersonAge(
							(Date) record.get("CSNY"),
							(Date) record.get("RYRQ"));
					record.put("AGE", ret.get("age"));
				}
			}
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("请输入有效的床位号码!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取数据字典失败!", e);
		}
	}

	public void doLoadBedPatientInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		int pageNo = Integer.parseInt(req.get("pageNo") + "");
		int pageSize = Integer.parseInt(req.get("pageSize") + "");
		Object openBy = req.get("openBy");
		Object ksdm = req.get("KSDM");
		Object brqk = req.get("BRQK");
		Object hljb = req.get("HLJB");
		Integer zyzt = (Integer) req.get("ZYZT");
		int first = (pageNo - 1) * pageSize;
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String brbq = user.getProperty("wardId") + "";
		parameters.put("first", first);
		parameters.put("max", pageSize);
		parameters.put("JGID", manageUnit);
		String filterSql = "";
		String iconSql = "";
		if (ksdm != null) {
			if (Long.parseLong(ksdm.toString()) == -1) {// 我的病人
				parameters.put("ZSYS", (String) user.getUserId());
				filterSql = " and ZSYS=:ZSYS";
			} else if (Long.parseLong(ksdm.toString()) > 0
					&& Long.parseLong(ksdm.toString()) != 2) {//
				parameters.put("BRKS", Long.parseLong(ksdm.toString()));
				filterSql = " and BRKS=:BRKS";
			} else if (Long.parseLong(ksdm.toString()) == 2) {// 会诊病人标志
				parameters.put("ZSYS", (String) user.getUserId());

				// parameters.put("doc", req.get("GH").toString());

				filterSql = "and EXISTS (SELECT 1 FROM YS_ZY_HZSQ YS_ZY_HZSQ, YS_ZY_HZYQ  YS_ZY_HZYQ WHERE (a.ZYH = YS_ZY_HZSQ.JZHM ) AND	( YS_ZY_HZYQ.SQXH = YS_ZY_HZSQ.SQXH ) AND "
						+ "( YS_ZY_HZSQ.JSBZ = 0 ) AND  ( YS_ZY_HZSQ.ZFBZ = 0 ) AND  ( YS_ZY_HZSQ.TJBZ = 1  ) AND ( YS_ZY_HZYQ.DXLX = 2 AND YS_ZY_HZYQ.YQDX =:ZSYS) ) and ZSYS!=:ZSYS";
			}
		}
		if (brqk != null) {
			parameters.put("BRQK", brqk);
			filterSql += " and ";
			if (hljb != null) {
				filterSql += " (";
			}
			filterSql += " a.BRQK in (:BRQK)";
		}
		if (hljb != null) {
			parameters.put("HLJB", hljb);
			filterSql += (brqk == null ? " and" : " or") + " a.HLJB in (:HLJB)";
		}
		if (brqk != null && hljb != null) {
			filterSql += " )";
		}
		if (zyzt == null || zyzt == 9) {
			// and a.CYPB<=9
			String d = BSHISUtil.toString(DateUtils.addHours(new Date(), -72),
					Constants.DEFAULT_DATE_FORMAT);
			filterSql += " and (a.CYPB<1 or (a.CYPB>=1 and a.CYPB<9 and a.CYRQ>"
					+ BSPHISUtil.toDate(d, "yyyy-mm-dd HH24:mi:ss") + "))";
		} else if (zyzt == 1) {
			filterSql += " and a.CYPB<1";
		} else if (zyzt == 2) {
			String d = BSHISUtil.toString(DateUtils.addHours(new Date(), -72),
					Constants.DEFAULT_DATE_FORMAT);
			filterSql += " and a.CYPB>=1 and a.CYPB<9 and a.CYRQ>"
					+ BSPHISUtil.toDate(d, "yyyy-mm-dd HH24:mi:ss");
		}
		try {
			if (req.get("cnd") != null) {
				String s = ExpressionProcessor.instance().toString(
						(List<?>) req.get("cnd"));
				filterSql += " and " + s;
			}
			String today = BSHISUtil.getDate() + " 00:00:00";
			StringBuffer sql = new StringBuffer(
					" select a.JSCS,a.XKYZ,a.XTYZ,a.CYZ,a.BRBQ,a.BRID,c.EMPIID,"
							+ BSPHISUtil.toChar("a.RYRQ", "yyyy.MM.dd")
							+ " as RYRQS,a.CYRQ,a.MQZD as JBMC,a.MQZD as MQZD,a.ZYH as ZYH,a.ZYHM,a.BRXM,a.BRXZ,"+BSPHISUtil.toChar("a.RYRQ", "yyyy-MM-dd hh24:mi:ss")+" as RYRQ,a.RYRQ as RYRQD,a.ZYCS,b.BRCH,a.BRXB,a.BRQK,a.HLJB,a.ZSYS,a.CYPB,");
			sql.append("a.JCKS,b.YEWYH,a.CSNY, a.ZKZT,a.RYNL,a.ZDHZPB,a.BRKS,b.JCPB,b.CWKS,a.YSDM,b.FJHM,b.KSDM,b.CWXB,b.CWFY,b.ICU,a.ZZYS as ZZYS,a.GMYW_SIGN from ");
			sql.append("MS_BRDA c,ZY_CWSZ b");
			if (openBy != null && openBy.toString().equals("doctor")) {
				sql.append(",");
				iconSql = " and YSBZ=1 ";
			} else {
				parameters.put("KSDM", Long.parseLong(brbq));
				sql.append(" left join ");
				iconSql = " and (YSBZ=1 and YSTJ=1 or YSBZ=0) ";
			}
			sql.append(" (select t.*,(select count(*) from ZY_BQYZ where ZYH=t.ZYH and JGID=t.JGID and FHBZ=0 and YZPB=0 and LSBZ=0 and KSSJ>"
					+ BSPHISUtil.toDate(today, "yyyy-mm-dd HH24:mi:ss")
					+ " "
					+ iconSql
					+ ") XKYZ,(select count(*) from ZY_BQYZ where ZYH=t.ZYH and JGID=t.JGID and YZPB=0 and TZFHBZ=0 and LSBZ=0 and TZSJ>"
					+ BSPHISUtil.toDate(today, "yyyy-mm-dd HH24:mi:ss")
					+ " "
					+ iconSql
					+ ") XTYZ,(select count(*) from ZY_RCJL where JGID = t.JGID and ZYH=t.ZYH and CZLX=-1) CYZ,(select count(*) from GY_PSJL where GY_PSJL.BRID=t.BRID and GY_PSJL.PSJG=1) GMYW_SIGN from ZY_BRRY t) a "
					+ (openBy != null && openBy.toString().equals("doctor") ? " where (a.ZYH=b.ZYH and a.JGID=b.JGID and a.CYPB<=1)"
							: " on (a.ZYH=b.ZYH and a.JGID=b.JGID and a.BRBQ=:KSDM)"));
			if (openBy != null && openBy.toString().equals("doctor")) {
				sql.append(filterSql + " and ");
			} else {
				sql.append(" where  b.KSDM=:KSDM and ");
			}
			sql.append(" a.BRID = c.BRID and b.JGID=:JGID order by b.BRCH");

			List<Map<String, Object>> data = dao.doSqlQuery(sql.toString(),
					parameters);
			parameters.remove("first");
			parameters.remove("max");
			long count = 0l;
			if (openBy != null && openBy.toString().equals("doctor")) {
				count = dao.doCount("ZY_BRRY a,ZY_CWSZ b",
						"a.ZYH=b.ZYH and a.JGID=:JGID and a.CYPB<=1 "
								+ filterSql, parameters);
			} else {
				// parameters.put("KSDM", Long.parseLong(brbq));
				count = dao.doCount("ZY_CWSZ", "JGID=:JGID and KSDM=:KSDM",
						parameters);
			}
			String brchxsws = ParameterUtil.getParameter(manageUnit,
					BSPHISSystemArgument.BQCHXSWS, ctx);
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(brchxsws);

			if (brchxsws == null || brchxsws.trim().length() == 0
					|| !isNum.matches()) {
				brchxsws = "0";
			}
			for (Map<String, Object> record : data) {
				String brch = record.get("BRCH").toString();
				if (Integer.parseInt(brchxsws) > 0
						&& Integer.parseInt(brchxsws) < brch.length()) {
					record.put(
							"BRCH_SHOW",
							brch.substring(brch.length()
									- Integer.parseInt(brchxsws)));
				} else {
					record.put("BRCH_SHOW", brch);
				}
				if (record.get("ZYH") != null
						&& record.get("ZYH").toString().trim().length() > 0) {
					Map<String, Object> ret = BSPHISUtil.getPersonAge(
							(Date) record.get("CSNY"),
							(Date) record.get("RYRQD"));
					record.put("AGE", ret.get("age"));
				} else {
					continue;
				}
				// 过敏药物信息
				if (record.get("GMYW_SIGN") != null
						&& Integer.parseInt(record.get("GMYW_SIGN").toString()) > 0) {
					parameters.clear();
					parameters.put("BRID",
							Long.parseLong(record.get("BRID").toString()));
					List<Map<String, Object>> gmywList = dao
							.doQuery(
									"select b.YPMC as YPMC from GY_PSJL a,YK_TYPK b where a.BRID=:BRID and a.YPXH=b.YPXH",
									parameters);
					StringBuilder gmyws = new StringBuilder();
					for (int i = 0; i < gmywList.size(); i++) {
						gmyws.append(gmywList.get(i).get("YPMC"));
						if (i < gmywList.size() - 1) {
							gmyws.append(",");
						}
					}
					record.put("GMYW", gmyws.toString());
				}
				record.put(
						"BRXB_text",
						DictionaryController.instance()
								.get("phis.dictionary.gender")
								.getText(record.get("BRXB") + ""));
				record.put(
						"BRXZ_text",
						record.get("BRXZ") == null ? "" : DictionaryController
								.instance()
								.get("phis.dictionary.patientProperties")
								.getText(record.get("BRXZ") + ""));
				record.put(
						"ZSYS_text",
						DictionaryController.instance()
								.get("phis.dictionary.doctor")
								.getText(record.get("ZSYS") + ""));
				if (record.get("BRKS") != null) {
					record.put(
							"BRKS_text",
							DictionaryController.instance()
									.get("phis.dictionary.department")
									.getText(record.get("BRKS") + ""));
				}
			}

			res.put("body", data);
			res.put("totalCount", count);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("病人信息查询失败!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("病人信息查询失败!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("获取数据字典失败!", e);
		}

	}

	/**
	 * 获取病人过敏信息及入院诊断等信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadPatientExInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Long brid = Long.parseLong(body.get("BRID").toString());
		Long zyh = Long.parseLong(body.get("ZYH").toString());
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRID", brid);
			parameters.put("JGID", manageUnit);
			List<Map<String, Object>> ypgms = dao
					.doQuery(
							"select a.YPXH as YPXH,b.YPMC as YPMC,a.BLFY as BLFY from "
									+ "GY_PSJL a,YK_TYPK b where a.YPXH=b.YPXH and a.BRID=:BRID and a.JGID=:JGID",
							parameters);
			res.put("GMYW", ypgms);
			// List<Map<String, Object>> ryzds = dao
			// .doQuery(
			// "select a.ZDXH as ZDXH,b.JBMC as JBMC from "
			// + BSPHISEntryNames.ZY_RYZD
			// + " a,"
			// + BSPHISEntryNames.GY_JBBM
			// + " b where a.ZDXH=b.JBXH and a.ZYH=:ZYH and a.JGID=:JGID",
			// parameters);
			// res.put("RYZD", ryzds);
			parameters.clear();
			parameters.put("ZYH", zyh);
			parameters.put("JGID", manageUnit);
			long l = dao.doCount("ZY_RCJL",
					"JGID=:JGID and ZYH=:ZYH and CZLX=-1", parameters);
			res.put("CYZ", l);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("病人信息查询失败!", e);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doSavePatientInfo(Long zyh, String openBy,
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> resMap = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String f = "";
		if ("doctor".equals(openBy)) {
			f = "doc_";
		}
		Map<String, Object> patientBase = (Map<String, Object>) body.get(f
				+ "patientBaseTab");
		List<Map<String, Object>> patientClinics = (List<Map<String, Object>>) body
				.get(f + "patientClinicTab");
		List<Map<String, Object>> patientAllergyMeds = (List<Map<String, Object>>) body
				.get(f + "patientAllergyMedTab");
		String op = "update";
		try {
			// 基础信息
			// 判断是否已经有处方信息，若已经存在，不允许删除ZSYS
			if (patientBase.get("ZSYS") == null
					|| patientBase.get("ZSYS").toString().length() == 0) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ZYH", zyh);
				parameters.put("JGID", manageUnit);
				long l = dao.doCount("ZY_BQYZ", "ZYH=:ZYH and JGID=:JGID",
						parameters);
				if (l > 0) {
					throw new ModelDataOperationException(
							"该病人已发生医嘱业务，主任医师不能为空!");
				}
			}

			// 诊断信息
			Map<String, Object> parameters = new HashMap<String, Object>();
			String MQZD = "";
			String RYZD = "";
			//update  by caijy for 诊断保存业务逻辑修改
			StringBuffer hql_zd_delete=new StringBuffer();
			hql_zd_delete.append("delete from ZY_RYZD where ZYH=:zyh");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyh", zyh);
			dao.doUpdate(hql_zd_delete.toString(), map_par);
			/*********************add by lizhi 病人信息诊断同步到病案首页****************************/
			Map<String, Object> delparameters = new HashMap<String, Object>();
			delparameters.put("ZYH", zyh);
			dao.doUpdate("delete from EMR_ZYZDJL a where a.JZXH=:ZYH and ZDLB in (11,22,51)", delparameters);
			/*********************add by lizhi 病人信息诊断同步到病案首页****************************/
			if (patientClinics != null && patientClinics.size() > 0) {
				StringBuffer zdxhStr = new StringBuffer();
				for (Map<String, Object> clinic : patientClinics) {
					op = (String) clinic.get("_opStatus");
					if ("remove".equals(op)) {
						continue;
					}
					if (Integer.parseInt(clinic.get("ZDLB").toString()) == 2) {
						RYZD += clinic.get("JBMC") + ",";
					}
					MQZD += clinic.get("ZDMC") + ",";
					clinic.put("ZYH", zyh);
					clinic.put("JGID", manageUnit);
					dao.doSave("create",
							"phis.application.war.schemas.ZY_RYZD_BQ",
							clinic, false);
					zdxhStr.append(
							Long.parseLong(clinic.get("ZDXH").toString()))
							.append(",");
					/*********************add by lizhi 病人信息诊断同步到病案首页****************************/
					if(!"1".equals(clinic.get("ZDLB") + "") && !"2".equals(clinic.get("ZDLB") + "") 
							&& !"3".equals(clinic.get("ZDLB") + "") && !"4".equals(clinic.get("ZDLB") + "")){
						continue;
					}
					Map<String, Object> EMR_ZYZDJL = new HashMap<String, Object>();
					EMR_ZYZDJL.put("JZXH", Long.parseLong(clinic.get("ZYH") + ""));
					EMR_ZYZDJL.put("BRID", Long.parseLong(patientBase.get("BRID") + ""));
					EMR_ZYZDJL.put("JBXH", Long.parseLong(clinic.get("ZDXH") + ""));
					EMR_ZYZDJL.put("JGID", clinic.get("JGID") + "");
					EMR_ZYZDJL.put("CYZGDM", Integer.parseInt(clinic.get("ZGQK") + ""));
					EMR_ZYZDJL.put("JBBM", clinic.get("ICD10") + "");
					EMR_ZYZDJL.put("MSZD", clinic.get("ZDMC") + "");
					EMR_ZYZDJL.put("ZDRQ", clinic.get("ZDSJ"));
					EMR_ZYZDJL.put("ZDYS", clinic.get("ZDYS") + "");
					EMR_ZYZDJL.put("ZXLB", Integer.parseInt(clinic.get("ZXLB") + ""));
					EMR_ZYZDJL.put("ZZBZ", 0);
					if("1".equals(clinic.get("ZDLB") + "")){//门诊诊断
						EMR_ZYZDJL.put("ZDLB", 11);
					}
					if("2".equals(clinic.get("ZDLB") + "")){//入院诊断
						EMR_ZYZDJL.put("ZDLB", 22);
					}
					if ("3".equals(clinic.get("ZDLB") + "")) {//出院主诊断
						EMR_ZYZDJL.put("ZZBZ", 1);
						EMR_ZYZDJL.put("ZDLB", 51);
					}
					if ("4".equals(clinic.get("ZDLB") + "")) {//出院次诊断
						EMR_ZYZDJL.put("ZDLB", 51);
					}
					if("1".equals(clinic.get("ZXLB") + "")){//西医
						EMR_ZYZDJL.put("JBBW", Long.parseLong(clinic.get("ZDBW") + ""));
						EMR_ZYZDJL.put("FJBS", 0);
					}else{//中医症候
						EMR_ZYZDJL.put("JBBW", 0);
						EMR_ZYZDJL.put("FJBS", Long.parseLong(clinic.get("ZDBW") + ""));
					}
					EMR_ZYZDJL.put("RYBQDM", 1);
					EMR_ZYZDJL.put("ZNXH", 0);
					EMR_ZYZDJL.put("XTSJ", new Date());
					dao.doSave("create", BSPHISEntryNames.EMR_ZYZDJL,
							EMR_ZYZDJL, false);
					/*********************add by lizhi 病人信息诊断同步到病案首页****************************/
				}
				if (zdxhStr.length() == 0) {
					patientBase.put("RYZD", RYZD);
					patientBase.put("MQZD", MQZD);
				} else {
					String hql = "select JBPB as JBPB,JBBGK as JBBGK from GY_JBBM where JBXH in ("
							+ zdxhStr.substring(0, zdxhStr.lastIndexOf(","))
							+ ")";
					List<Map<String, Object>> jbbmList = dao.doQuery(hql, null);
					if (jbbmList != null) {
						StringBuffer JBPBStr = new StringBuffer();
						StringBuffer JBBGKStr = new StringBuffer();
						for (int i = 0; i < jbbmList.size(); i++) {
							Map<String, Object> map = jbbmList.get(i);
							if (map.get("JBPB") != null
									&& (map.get("JBPB") + "").length() > 0) {
								JBPBStr.append(map.get("JBPB"));
							}
							if (map.get("JBBGK") != null
									&& (map.get("JBBGK") + "").length() > 0) {
								JBBGKStr.append(map.get("JBBGK"));
							}
						}
						resMap.put("JBPB", JBPBStr);
						resMap.put("JBBGK", JBBGKStr);
					}
					patientBase.put("RYZD", RYZD);
					patientBase.put("MQZD", MQZD);
				}
			}
			/*if (patientClinics != null && patientClinics.size() > 0) {
				StringBuffer zdxhStr = new StringBuffer();
				for (Map<String, Object> clinic : patientClinics) {
					op = (String) clinic.get("_opStatus");
					clinic.put("ZYH", zyh);
					clinic.put("JGID", manageUnit);
					if ("remove".equals(op)) {
						parameters.put("ZYH", zyh);
						parameters
								.put("ZDLB", Integer.parseInt(clinic
										.get("ZDLB").toString()));
						parameters.put("ZDXH",
								Long.parseLong(clinic.get("ZDXH").toString()));
						StringBuffer delSql = new StringBuffer(
								"delete from ZY_RYZD");
						delSql.append(" where ZYH=:ZYH and ZDXH=:ZDXH and ZDLB=:ZDLB");
						dao.doUpdate(delSql.toString(), parameters);
					} else {
						if (Integer.parseInt(clinic.get("ZDLB").toString()) == 2) {
							RYZD += RYZD = clinic.get("JBMC") + ",";
						}
						MQZD += clinic.get("ZDMC") + ",";
						if (op.equals("update")) {
							StringBuffer update_hql = new StringBuffer();
							update_hql.append("update ZY_RYZD").append(
									" set ZDXH=:ZDXH,ZDLB=:ZDLB");
							parameters.clear();
							parameters.put("ZYH", zyh);
							parameters.put("ZDLB", Integer.parseInt(clinic.get(
									"ZDLB").toString()));
							parameters.put("ZDXH", Long.parseLong(clinic.get(
									"ZDXH").toString()));
							parameters.put("ZDXH_D", Long.parseLong(clinic.get(
									"ZDXH_D").toString()));
							parameters.put("ZDLB_D", Integer.parseInt(clinic
									.get("ZDLB_D").toString()));
							String zgqk = clinic.get("ZGQK").toString();
							if (StringUtils.isNotBlank(zgqk)) {
								parameters.put("ZGQK", Integer.parseInt(clinic
										.get("ZGQK").toString()));
								update_hql.append(",ZGQK=:ZGQK");
							}
							update_hql
									.append(" where ZYH=:ZYH and ZDXH=:ZDXH_D and ZDLB=:ZDLB_D");
							dao.doUpdate(update_hql.toString(), parameters);
						} else {
							dao.doSave(op,
									"phis.application.war.schemas.ZY_RYZD_BQ",
									clinic, false);
						}
						zdxhStr.append(
								Long.parseLong(clinic.get("ZDXH").toString()))
								.append(",");
					}
				}
				if (zdxhStr.length() == 0) {
					patientBase.put("RYZD", RYZD);
					patientBase.put("MQZD", MQZD);
				} else {
					String hql = "select JBPB as JBPB,JBBGK as JBBGK from GY_JBBM where JBXH in ("
							+ zdxhStr.substring(0, zdxhStr.lastIndexOf(","))
							+ ")";
					List<Map<String, Object>> jbbmList = dao.doQuery(hql, null);
					if (jbbmList != null) {
						StringBuffer JBPBStr = new StringBuffer();
						StringBuffer JBBGKStr = new StringBuffer();
						for (int i = 0; i < jbbmList.size(); i++) {
							Map<String, Object> map = jbbmList.get(i);
							if (map.get("JBPB") != null
									&& (map.get("JBPB") + "").length() > 0) {
								JBPBStr.append(map.get("JBPB"));
							}
							if (map.get("JBBGK") != null
									&& (map.get("JBBGK") + "").length() > 0) {
								JBBGKStr.append(map.get("JBBGK"));
							}
						}
						resMap.put("JBPB", JBPBStr);
						resMap.put("JBBGK", JBBGKStr);
					}
					patientBase.put("RYZD", RYZD);
					patientBase.put("MQZD", MQZD);
				}
			}*/
			dao.doSave("update", BSPHISEntryNames.ZY_BRRY, patientBase, false);
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
									"phis.application.war.schemas.GY_PSJL_BQ",
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

	/**
	 * 退床处理
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveTccl(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		BSPHISUtil.cwgl_tccl(body, dao, ctx);
	}

	public Map<String, Object> doLoadHospitalParams(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Long bqdm = Long.parseLong(body.get("BQDM").toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BQDM", bqdm);
		params.put("JGID", manageUnit);
		try {
			List<Map<String, Object>> ret = dao
					.doQuery(
							"select a.DMSB as DMSB,a.YFSB as YFSB,b.YFMC as YFMC from "
									+ "BQ_FYYF a,YF_YFLB b where a.YFSB=b.YFSB and a.JGID=:JGID and a.BQDM=:BQDM and a.TYPE=1 and a.ZXPB=0 and a.GNFL=4",
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

	public List<Map<String, Object>> doLoadAppendAdvice(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		Long ksdm = Long.parseLong(body.get("KSDM").toString());
		Long fyxh = Long.parseLong(body.get("FYXH").toString());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JGID", manageUnit);
		params.put("XMXH", fyxh);
		params.put("KSDM", ksdm);
		Object mzsy = body.get("MZSY");
		String sylb = "ZYSY";
		if (mzsy != null && "1".equals(mzsy.toString())) {
			sylb = "MZSY";
		}
		try {
			List<Map<String, Object>> res = dao
					.doQuery(
							"select a.XMXH as XMXH,a.SYLB as SYLB,a.GLXH as YPXH,b.FYMC as YZMC,a.FYSL as FYSL,c.FYDJ as YPDJ,b.XMLX as XMLX,b.FYDW as FYDW,c.FYKS as FYKS,b.FYGB as FYGB from GY_XMGL a,GY_YLSF b,GY_YLMX c where a.GLXH=b.FYXH and a.GLXH=c.FYXH and c.JGID=:JGID and a.XMXH=:XMXH AND  (a.KSDM=0 OR KSDM=:KSDM) and a.JGID=:JGID and c.ZFPB=0 and b.ZFPB=0 and b."
									+ sylb + "=1", params);
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("附加项目调入失败!", e);
		}
	}

	public List<Map<String, Object>> doLoadAppendAdviceTYSQ(
			List<Map<String, Object>> bodys, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		try {
			for (int i = 0; i < bodys.size(); i++) {
				Map<String, Object> body = bodys.get(i);
				Long ksdm = Long.parseLong(body.get("KSDM").toString());
				// Long fyxh = Long.parseLong(body.get("FYXH").toString());
				Long yzxh = Long.parseLong(body.get("YZXH").toString());
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("yzxh", yzxh);
				Map<String, Object> YPYF = dao
						.doLoad("select b.FYXH as FYXH from ZY_BQYZ a,ZY_YPYF b where a.YPYF = b.YPYF and a.JLXH= :yzxh",
								parameters);
				if (YPYF == null)
					return null;
				if (YPYF.get("FYXH") == null)
					return null;
				long fyxh = Long.parseLong(YPYF.get("FYXH") + "");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("JGID", manageUnit);
				params.put("XMXH", fyxh);
				params.put("KSDM", ksdm);
				Object mzsy = body.get("MZSY");
				String sylb = "ZYSY";
				if (mzsy != null && "1".equals(mzsy.toString())) {
					sylb = "MZSY";
				}
				List<Map<String, Object>> res = dao
						.doQuery(
								"select a.XMXH as XMXH,a.SYLB as SYLB,a.GLXH as YPXH,b.FYMC as YZMC,a.FYSL as FYSL,c.FYDJ as YPDJ,b.XMLX as XMLX,b.FYDW as FYDW,c.FYKS as FYKS,b.FYGB as FYGB from GY_XMGL a,GY_YLSF b,GY_YLMX c where a.GLXH=b.FYXH and a.GLXH=c.FYXH and c.JGID=:JGID and a.XMXH=:XMXH AND  (a.KSDM=0 OR KSDM=:KSDM) and a.JGID=:JGID and c.ZFPB=0 and b.ZFPB=0 and b."
										+ sylb + "=1", params);
				if (res.size() > 0) {
					return res;
				}
			}
			return null;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("附加项目调入失败!", e);
		}
	}

	/**
	 * 载入医嘱详细信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadDetailsInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
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
			Long brid = Long.parseLong(body.get("BRID").toString());
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
	 * 常用项和全部项目
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadClinicInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String tabId = (String) body.get("tabId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("JGID", jgid);
		params.put("FYXH", Long.parseLong(body.get("FYXH").toString()));
		Map<String, Object> clinic = null;
		try {
			if (tabId.equals("clinicCommon")) {
				String orderBy = "ZTBH";
				String schema = "phis.application.cic.schemas.YS_MZ_ZT02_XM";
				String cnds = "['eq',['$','JLBH'],['d'," + body.get("JLBH")
						+ "]]";
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schema);
				if (list.size() > 0) {
					clinic = list.get(0);
				}
			}
			StringBuffer hql = new StringBuffer(
					"select distinct a.FYXH as FYXH,a.FYMC as FYMC,a.FYDW as FYDW,a.BZJG as BZJG,a.XMLX as XMLX,c.FYDJ as FYDJ,a.FYGB as FYGB,c.FYKS as FYKS,0 as YPLX ,a.YJSY as YJSY  from ");
			hql.append("GY_YLSF a,GY_FYBM b,GY_YLMX c ");
			hql.append("where a.FYXH=b.FYXH and a.FYXH=c.FYXH and c.ZFPB=0  and a.ZFPB=0 and a.ZYSY=1 and c.JGID=:JGID and a.FYXH=:FYXH");

			Map<String, Object> zt_clinic = dao.doLoad(hql.toString(), params);
			if (tabId.equals("clinicCommon")) {
				if (clinic != null && zt_clinic != null) {
					zt_clinic.putAll(clinic);
				}
			}
			return zt_clinic;
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ExpException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	/**
	 * 常用药和全部药品
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadMedicineInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		Object ypxh = body.get("YPXH");
		// 发药药房
		// Object cflx = body.get("CFLX");
		// Map<String, Object> Yfxx = (Map<String, Object>) body.get("YFXX");
		String tabId = (String) body.get("tabId");
		String wardId = body.get("wardId") + "";
		// 药品产地选择方式,库存序号
		try {
			Map<String, Object> med = null;
			if (tabId.equals("medicineCommon")) {
				// cflx = body.get("ZTLB");
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
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BQDM", Long.parseLong(wardId));
			parameters.put("JGID", jgid);
			List<Map<String, Object>> yfsbs = dao
					.doSqlQuery(
							"select distinct YFSB as YFSB from BQ_FYYF where BQDM=:BQDM and JGID =:JGID",
							parameters);
			for (int i = 0; i < yfsbs.size(); i++) {
				// Object pharmacyId = getFyyf(cflx, Yfxx);
				Object pharmacyId = yfsbs.get(i).get("YFSB");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YFSB", Long.parseLong(pharmacyId.toString()));

				params.put("YPXH", Long.parseLong(ypxh.toString()));
				String bzlx = ParameterUtil.getParameter(jgid,
						BSPHISSystemArgument.YZLR_BZLX, ctx);
				StringBuffer hql = new StringBuffer();
				if ("2".equals(bzlx)) {
					hql.append("select a.YCJL as YCJL,a.YPXH as YPXH,a.YPMC as YPMC,a.BFGG as YFGG,a.BFDW as YFDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,a.BFBZ as YFBZ,round((d.LSJG/b.YFBZ)*a.BFBZ,4) as LSJG,d.YPCD as YPCD,f.CDMC as CDMC,a.TYPE as YPLX,a.TYPE as TYPE,a.TSYP as TSYP,a.JYLX as JYLX,a.FYFS as FYFS,a.ZXBZ as ZXBZ,a.YPGG as YPGG ,a.KSBZ as KSBZ,a.YCYL as YCYL,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.SFSP as SFSP,a.YCJL as YCJL");
				} else {
					hql.append("select a.YCJL as YCJL,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,b.YFDW as YFDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as CDMC,a.TYPE as YPLX,a.TYPE as TYPE,a.TSYP as TSYP,a.JYLX as JYLX,a.FYFS as FYFS,a.ZXBZ as ZXBZ,a.YPGG as YPGG ,a.KSBZ as KSBZ,a.YCYL as YCYL,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.SFSP as SFSP,a.YCJL as YCJL");
				}
				hql.append(" from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g ");
				hql.append(" where  ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 )  AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND b.YFSB=:YFSB  AND a.YPXH = :YPXH ORDER BY d.SBXH");
				List<Map<String, Object>> meds = dao.doQuery(hql.toString(),
						params);
				if (meds.size() > 0) {// 多产地取第一条记录
					Map<String, Object> zt_med = meds.get(0);
					if (tabId.equals("medicineCommon")) {
						zt_med.putAll(med);
					}
					Dictionary dic = DictionaryController.instance().get(
							"phis.dictionary.drugMode");
					String gYFF_text = dic
							.getText(zt_med.get("GYFF") == null ? "" : zt_med
									.get("GYFF") + "");
					zt_med.put("GYFF_text", gYFF_text);
					zt_med.put(
							"FYFS_text",
							DictionaryController.instance()
									.get("phis.dictionary.hairMedicineWay")
									.getText(zt_med.get("FYFS") + ""));
					Long fygb = BSPHISUtil.getfygb(
							Long.parseLong(zt_med.get("TYPE").toString()),
							Long.parseLong(ypxh.toString()), dao, ctx);
					zt_med.put("FYGB", fygb);
					zt_med.put("YFSB", Long.parseLong(pharmacyId.toString()));
					zt_med.put(
							"YFSB_text",
							DictionaryController.instance()
									.get("phis.dictionary.wardPharmacy")
									.getText(zt_med.get("YFSB") + ""));
					return zt_med;
				} else {
					if (i == yfsbs.size() - 1) {
						body.put("errorMsg", "暂无库存!");
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ExpException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ControllerException e) {
			logger.error(
					"fail to load medicine information by dictionary reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
		return null;
	}

	public List<Map<String, Object>> doLoadAdviceSet(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Object ztbh = body.get("ZTBH");
		Object brxz = body.get("BRXZ");
		Object zyh = body.get("ZYH");
		String wardId = body.get("wardId") + "";
		Integer ztlb = Integer.parseInt(body.get("ZTLB").toString());
		// 发药药房 和 药品类别
		// Object pharmacyId = body.get("pharmacyId");
		String orderBy = "YPZH,JLBH";
		String schema = "phis.application.cic.schemas.YS_MZ_ZT02_MS";
		String schemaWz = "phis.application.cic.schemas.YS_MZ_ZT02_XM";
		String cnds = "['eq',['$','ZTBH'],['d'," + ztbh + "]]";
		try {
			if (ztlb < 4) {
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schema);
				for (Map<String, Object> med : list) {
					Object ypxh = med.get("YPXH");
					// 根据包装类型区分查询的对象
					String bzlx = ParameterUtil.getParameter(manageUnit,
							BSPHISSystemArgument.YZLR_BZLX, ctx);
					StringBuffer hql = new StringBuffer();
					if ("2".equals(bzlx)) {
						hql.append("select a.YPXH as YPXH,a.YPMC as YPMC,a.BFGG as YFGG,a.BFDW as YFDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,a.BFBZ as YFBZ,round((d.LSJG/b.YFBZ)*a.BFBZ,4) as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as YPLX,a.TSYP as TSYP,a.FYFS as FYFS,a.YPGG as YPGG,a.KSBZ as KSBZ,a.YCYL as YCYL,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.SFSP as SFSP,a.YCJL as YCJL ");
						hql.append("from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g ");
						hql.append("where ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND g.JGID=:JGID and a.YPXH=:YPXH ");
					} else {
						hql.append("select a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,b.YFDW as YFDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,b.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as YPLX,a.TSYP as TSYP,a.FYFS as FYFS,a.YPGG as YPGG,a.KSBZ as KSBZ,a.YCYL as YCYL,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS,a.SFSP as SFSP,a.YCJL as YCJL ");
						hql.append("from YK_TYPK a,YF_YPXX b,YK_YPBM c,YF_KCMX d,YK_CDDZ f,YK_YPXX g ");
						hql.append("where ( g.YPXH = a.YPXH ) AND ( c.YPXH = a.YPXH ) AND ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND b.YFSB in (select distinct YFSB from BQ_FYYF where JGID =:JGID) AND g.JGID=:JGID and a.YPXH=:YPXH ");
					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("JGID", manageUnit);
					params.put("YPXH", ypxh);
					List<Map<String, Object>> meds = dao.doQuery(
							hql.toString(), params);
					if (meds.size() > 0) {// 取第一条记录
						Map<String, Object> zt_med = meds.get(0);
						zt_med.putAll(med);
						// 获取组套中药品自负比例和库存数量
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("YPLX", zt_med.get("YPLX"));
						data.put("YPXH", zt_med.get("YPXH"));
						data.put("BRXZ", brxz);
						data.put("ZYH", zyh);
						data.put("BRID", body.get("BRID"));
						zt_med.putAll(doLoadDetailsInfo(data, ctx));
						Map<String, Object> prarms = new HashMap<String, Object>();
						Map<String, Object> parameters = new HashMap<String, Object>();
						parameters.put("BQDM", Long.parseLong(wardId));
						parameters.put("JGID", manageUnit);
						List<Map<String, Object>> yfsbs = dao
								.doSqlQuery(
										"select distinct YFSB as YFSB from BQ_FYYF where BQDM=:BQDM and JGID =:JGID",
										parameters);
						for (int i = 0; i < yfsbs.size(); i++) {
							// prarms.put(
							// "pharmId",
							// getFyyf(zt_med.get("YPLX"),
							// (Map<String, Object>) body.get("YFXX")));
							prarms.put(
									"pharmId",
									Long.parseLong(yfsbs.get(i).get("YFSB")
											+ ""));
							zt_med.put(
									"YFSB",
									Long.parseLong(yfsbs.get(i).get("YFSB")
											+ ""));
							prarms.put("medId", zt_med.get("YPXH"));
							prarms.put("medsource", zt_med.get("YPCD"));
							prarms.put(
									"quantity",
									(Double.parseDouble(zt_med.get("YCJL")
											.toString()) / Double
											.parseDouble(zt_med.get("YPJL")
													.toString()))
											* (Integer.parseInt(zt_med.get(
													"MRCS").toString())));
							prarms.put("lsjg", zt_med.get("LSJG"));
							Map<String, Object> ret = BSPHISUtil
									.checkInventory(prarms, dao, ctx);
							if ((Integer) ret.get("sign") == -1
									&& i == (yfsbs.size() - 1)) {
								zt_med.put(
										"errorMsg",
										"药品【"
												+ zt_med.get("YPMC")
												+ "】库存数量不足，库存数量："
												+ (ret.get("KCZL") == null ? 0
														: ret.get("KCZL"))
												+ ",实际数量："
												+ prarms.get("quantity"));
							} else if ((Integer) ret.get("sign") == -1) {
								continue;
							} else {
								String fYFS_text = DictionaryController
										.instance()
										.get("phis.dictionary.hairMedicineWay")
										.getText(zt_med.get("FYFS").toString());
								zt_med.put("FYFS_text", fYFS_text);
								break;
							}
						}
						zt_med.put(
								"YFSB_text",
								DictionaryController.instance()
										.get("phis.dictionary.wardPharmacy")
										.getText(zt_med.get("YFSB") + ""));
						res.add(zt_med);
					} else {
						med.put("errorMsg", "药品【" + med.get("YPMC") + "】暂无库存!");
						res.add(med);
					}
				}
			} else if(ztlb == 5) {//add by lizhi 2017-12-04文字医嘱
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schemaWz);
//				if(list.size()>0){
//					Map<String, Object> wz = list.get(0);
//					wz.put("YPMC", wz.get("XMMC"));
//					res.add(wz);
//				}
				res.addAll(list);
			} else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ZTBH", Long.parseLong(body.get("ZTBH").toString()));
				params.put("JGID", manageUnit);
				StringBuffer hql = new StringBuffer(
						"select b.FYXH as FYXH,a.XMMC as XMMC,a.XMSL as XMSL,a.YPZH as YPZH,c.FYDJ as FYDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS,a.SYPC as SYPC,b.ZYSY as ZYSY,b.YJSY as YJSY");
				hql.append(" from YS_MZ_ZT02 a,GY_YLSF b,GY_YLMX c ");
				hql.append(" where  a.XMBH = b.FYXH AND  b.FYXH = c.FYXH and a.ZTBH = :ZTBH and c.JGID=:JGID ORDER BY a.YPZH,a.JLBH");
				List<Map<String, Object>> clinics = dao.doQuery(hql.toString(),
						params);
				for (Map<String, Object> clinic : clinics) {
					if (!"1".equals(clinic.get("ZYSY") + "")) {
						clinic.put("errorMsg", "不存在!");
						continue;
					}
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("YPLX", 0);
					data.put("YPXH", clinic.get("FYXH"));
					data.put("BRXZ", brxz);
					data.put("ZYH", zyh);
					clinic.putAll(doLoadDetailsInfo(data, ctx));
				}
				res.addAll(clinics);
			}
			return res;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ExpException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}
	
	/**
	 * 常用文字医嘱 add by lizhi 2017-12-04
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> doLoadCharacterInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		String tabId = (String) body.get("tabId");
		Map<String, Object> med = null;
		// 药品产地选择方式,库存序号
		try {
			if (tabId.equals("characterCommon")) {
				String orderBy = "ZTBH";
				String schema = "phis.application.cic.schemas.YS_MZ_ZT02_XM";
				String cnds = "['eq',['$','JLBH'],['d'," + body.get("JLBH")
						+ "]]";
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schema);
				if (list.size() > 0) {
					med = list.get(0);
//					med.put("YPMC", list.get(0).get("XMMC"));
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		} catch (ExpException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
		return med;
	}

	// private Long getFyyf(Object type, Map<String, Object> fyyfMap)
	// throws ModelDataOperationException {
	// try {
	// if ("1".equals(type.toString())) {
	// return Long.parseLong(fyyfMap.get("bqxyf").toString());
	// } else if ("2".equals(type.toString())) {
	// return Long.parseLong(fyyfMap.get("bqzyf").toString());
	// } else if ("3".equals(type.toString())) {
	// return Long.parseLong(fyyfMap.get("bqcyf").toString());
	// } else {
	// return null;
	// }
	// } catch (RuntimeException e) {
	// throw new ModelDataOperationException("获取药房信息错误!", e);
	// }
	// }

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
		Map<String, Object> brxx = (Map<String, Object>) body.get("brxx");
		String brxz="";
		try {
			Map<String, Object> ZY_BRRY = dao.doLoad(BSPHISEntryNames.ZY_BRRY,Long.parseLong(brxx.get("ZYH").toString()));
			if ("1".equals(ZY_BRRY.get("CYPB").toString())) {
				throw new ModelDataOperationException("该病人已通知出院,保存失败!");
			} else if (Integer.parseInt(ZY_BRRY.get("CYPB").toString()) >= 8) {
				throw new ModelDataOperationException("该病人已出院,保存失败!");
			}
			brxz=ZY_BRRY.get("BRXZ")+"";
		} catch (NumberFormatException e1) {
			throw new ModelDataOperationException("转换ZYH失败!", e1);
		} catch (PersistentDataOperationException e1) {
			throw new ModelDataOperationException("获取住院病人信息失败!", e1);
		}
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.WPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manageUnit,BSPHISSystemArgument.WPJFBZ, ctx));
		int wzsfxmjgzy = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.WZSFXMJGZY, ctx)) ? "0"
				: ParameterUtil.getParameter(manageUnit,BSPHISSystemArgument.WZSFXMJGZY, ctx));
		Map<String, Object> prarms = new HashMap<String, Object>();
		List<Map<String, Object>> yzxxs = (List<Map<String, Object>>) body.get("yzxx");
		List<Map<String, Object>> fjxxs = (List<Map<String, Object>>) body.get("fjxx");
		Schema sc;
		try {
			sc = SchemaController.instance().get("phis.application.war.schemas.ZY_BQYZ_LS");
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
		String njjbmsg="";
		try {
			// 删除附加项目
			int yzpx = 0;
			for (Map<String, Object> fjxx : fjxxs) {
				if (fjxx.get("_opStatus").equals("remove")) {
					dao.doRemove(Long.parseLong(fjxx.get("JLXH").toString()),BSPHISEntryNames.ZY_BQYZ);
				}
			}

			for (int i = 0; i < yzxxs.size(); i++) {
				Map<String, Object> r = yzxxs.get(i);
				String op = (String) r.get("_opStatus");
				if (op.equals("remove")) {
					if (wpjfbz == 1 && wzsfxmjgzy == 1) {
						BSPHISUtil.deleteSuppliesZY(dao, ctx,Long.parseLong(r.get("JLXH") + ""));
					}
					dao.doRemove(Long.parseLong(r.get("JLXH").toString()),BSPHISEntryNames.ZY_BQYZ);
					continue;
				}
				// 默认值设置
				if ((Integer) r.get("YZZH_SHOW") == lastYzzh) {
					r.put("KSSJ", lastDate);
				} else {
					if ("create".equals(op) && (r.get("YZZH")==null || r.get("YZZH").toString().length()==0
						|| r.get("YZZH").toString().equals("0"))) {
						// 查询医嘱表YZZH最大值
						YZZH = Long.parseLong(KeyManager.getKeyByName("ZY_BQYZ",item.getKeyRules(),item.getId(),ctx));
						res.put("YZZH",YZZH);
						yzpx = 0;
					}
					lastYzzh = Integer.parseInt(r.get("YZZH_SHOW").toString());
					lastDate = BSHISUtil.toDate(r.get("KSSJ").toString());
					r.put("KSSJ",lastDate);
				}
				if ("create".equals(op) && (r.get("YZZH") == null || r.get("YZZH").toString().length() == 0
					|| r.get("YZZH").toString().equals("0"))) {// 新增的记录且没有YZZH
					r.put("YZZH", YZZH);

				}
				if (r.get("TZSJ") != null && r.get("TZSJ").toString().length() > 0) {
					r.put("TZSJ", BSHISUtil.toDate(r.get("TZSJ").toString()));
					if (BSHISUtil.dateCompare((Date) r.get("TZSJ"),(Date) r.get("KSSJ")) < 0) {
						throw new ModelDataOperationException("停嘱时间不能小于开嘱时间!");
					}
				}
				if (("1").equals(r.get("LSYZ") + "")) {
					r.put("TZSJ", r.get("KSSJ"));
					r.put("TZYS", r.get("YSGH") + "");
				}
				r.put("BRKS", Long.parseLong(brxx.get("BRKS")==null ? "0":brxx.get("BRKS").toString()));
				r.put("BRBQ", Long.parseLong(brxx.get("BRBQ")==null ? "0":brxx.get("BRBQ").toString()));
				r.put("BRCH", brxx.get("BRCH"));
				r.put("SRKS", Long.parseLong(brxx.get("BRBQ")==null ? "0":brxx.get("BRBQ").toString()));
				Integer yplx = (Integer) r.get("YPLX");
				if (yplx >= 1) {// 药品
					r.put("JFBZ", 1);
					if ("1".equals(r.get("ZFYP")+"") && (r.get("YFSB") == null || "".equals(r.get("YFSB")))) {
						r.put("YFSB", 0);
					}
					if ((!("1".equals(r.get("SYBZ").toString()) 
						|| (r.get("QRSJ") != null && r.get("QRSJ").toString().length() > 0)
						|| (r.get("TZSJ") != null && r.get("TZSJ").toString().length() > 0)))
						&& !"1".equals(r.get("ZFYP") + "")) {
						prarms.put("pharmId", r.get("YFSB"));
						prarms.put("medId", r.get("YPXH"));
						prarms.put("medsource", r.get("YPCD"));
						prarms.put("quantity",Double.parseDouble(r.get("YCSL").toString())
										*(Integer.parseInt(r.get("SRCS").toString())));
						prarms.put("lsjg", r.get("YPDJ"));
						Map<String, Object> ret = BSPHISUtil.checkInventory(prarms,dao,ctx);
						if ((Integer) ret.get("sign") == -1) {
							throw new ModelDataOperationException("药品【"+r.get("YZMC")+"】库存数量不足，库存数量："
							+(ret.get("KCZL")==null ? 0: ret.get("KCZL")) + ",实际数量："+prarms.get("quantity"));
						}
					}
					//yx-2018-04-27南京金保判断是否对照自编码-b
					if(brxz.equals("2000")){
						Map<String, Object> p=new HashMap<String, Object>();
						p.put("JGID", manageUnit.substring(0,9));//溧水机构编码取主院
						p.put("YPXH", Long.parseLong(r.get("YPXH")+""));
						p.put("YPCD", Long.parseLong(r.get("YPCD")+""));
						List<Map<String, Object>> ml=dao.doQuery("select YYZBM as YYZBM from YK_CDXX where JGID=:JGID and YPXH=:YPXH and YPCD=:YPCD",p);
						if(ml!=null && ml.size()>0){
							Map<String, Object> ybyp = ml.get(0);
							if((ybyp.get("YYZBM")==null || "".equals(ybyp.get("YYZBM")+""))){//自编码为空
								if(njjbmsg.length()==0){
									njjbmsg+="医嘱中： "+r.get("YZMC");
								}else{
									njjbmsg+="、"+r.get("YZMC");
								}
							}
						}
					}
					//yx-2018-04-27南京金保判断是否对照自编码-e
				} else if (yplx == 0) {// 项目
					if (Integer.parseInt(r.get("JFBZ").toString()) != 3) {
						if (r.get("midifyYjzx") == null) {
							if (lastYzzh != (Integer) r.get("YZZH_SHOW")) {// 同组的第一项
								r.put("YJZX", 1);
								r.put("YZPX", 1);
								yzpx = 0;
							} else {
								r.put("YJZX", 0);
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
					//yx-2018-04-27南京金保判断是否对照自编码-b
					if(brxz.equals("2000") && (Integer.parseInt(r.get("JFBZ").toString()) != 3)){
						String sql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
						Map<String, Object> p=new HashMap<String, Object>();
						p.put("jgid",manageUnit.substring(0,9));//溧水是根据住院去判断
						p.put("fyxh",Long.parseLong(r.get("YPXH")+""));
						Map<String, Object> zbmmap=dao.doSqlLoad(sql,p);
						if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
							if(njjbmsg.length()==0){
								njjbmsg+="医嘱中： "+r.get("YZMC");
							}else{
								njjbmsg+="、"+r.get("YZMC");
							}
						}
					}
					//yx-2018-04-27南京金保判断是否对照自编码-e
				}
				Map<String, Object> where = new HashMap<String, Object>();
				where.put("LSBZ", 0);
				// 不修改确认时间，防止时间格式错误
				r.remove("QRSJ");
				// 项目 计费标志处理
				// add by liyunt
				if (r.get("YPLX")!= null && r.get("YPLX").toString().equals("0") && !r.get("JFBZ").toString().equals("3")
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
				Map<String, Object> jlxhMap = dao.doSave(op,"phis.application.war.schemas.ZY_BQYZ_CQ",r,where,false);
				r.put("uniqueId", uniqueIdStr);
				if (yplx == 0) {// 项目
					if (wpjfbz == 1 && wzsfxmjgzy == 1) {
						if (("1").equals(r.get("LSYZ") + "")) {
							if ("create".equals(op)) {
								BSPHISUtil.setSuppliesZY(dao,ctx,Long.parseLong(jlxhMap.get("JLXH")+""));
							} else if ("update".equals(op)) {
								BSPHISUtil.setSuppliesZY(dao,ctx,Long.parseLong(r.get("JLXH")+""));
							}
						}
					}
				}
				// lastYzzh = (Integer.parseInt(r.get("YZZH").toString()));
				// 更换组的时候判断下是否有附加项目需要保存
				if (fjxxs != null && fjxxs.size() > 0) {
					if ((i+1 >= yzxxs.size())|| yzxxs.get(i+1).get("YZZH_SHOW") != lastYzzh) {
						String uniqueId = (r.get("JLXH")!=null && r.get("JLXH").toString().length()>0)?"YZZH":"uniqueId";
						for (Map<String, Object> fjxx : fjxxs) {
							if (!fjxx.get("_opStatus").equals("remove") && !fjxx.get("_opStatus").equals("deal")) {
								if ((fjxx.get(uniqueId) + "").equals(r.get(uniqueId) + "")) {
									String ops = "create";
									if (fjxx.get("JLXH") != null && fjxx.get("JLXH").toString().trim().length() > 0) {
										ops = "update";
									}
									if (ops.equals("create")) {
										fjxx.put("YZZH", r.get("YZZH"));
									}
									fjxx.put("TPN", 0);
									fjxx.put("YFSB", 0);
									fjxx.put("FYFS", 0);
									fjxx.put("KSSJ", lastDate);
									// fjxx.put("TZSJ", r.get("TZSJ"));
									fjxx.put("BRKS", Long.parseLong(brxx.get("BRKS")==null?"0":brxx.get("BRKS").toString()));
									fjxx.put("BRBQ", Long.parseLong(brxx.get("BRBQ")==null?"0":brxx.get("BRBQ").toString()));
									fjxx.put("BRCH", brxx.get("BRCH"));
									fjxx.put("SRKS", Long.parseLong(brxx.get("BRBQ")==null?"0":brxx.get("BRBQ").toString()));
									fjxx.remove("QRSJ");
									if (("1").equals(fjxx.get("LSYZ") + "")) {
										fjxx.put("TZSJ", fjxx.get("KSSJ"));
										fjxx.put("TZYS", fjxx.get("YSGH"));
									}
									uniqueIdStr = fjxx.get("uniqueId") + "";
									fjxx.remove("uniqueId");
									dao.doSave(ops,"phis.application.war.schemas.ZY_BQYZ_CQ",fjxx,false);
									fjxx.put("uniqueId", uniqueIdStr);
									fjxx.put("_opStatus", "deal");
									//yx-2018-04-27南京金保判断是否对照自编码-b
									if(brxz.equals("2000")){
										String sql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
										Map<String, Object> p=new HashMap<String, Object>();
										p.put("jgid",manageUnit.substring(0,9));//溧水是根据住院去判断
										p.put("fyxh",Long.parseLong(fjxx.get("YPXH")+""));
										Map<String, Object> zbmmap=dao.doSqlLoad(sql,p);
										if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
											if(njjbmsg.length()==0){
												njjbmsg+="医嘱中： "+fjxx.get("YZMC");
											}else{
												njjbmsg+="、"+fjxx.get("YZMC");
											}
										}
									}
									//yx-2018-04-27南京金保判断是否对照自编码-e
								}
							}
						}
					}
				}
			}
			for (Map<String, Object> fjxx : fjxxs) {
				if (!fjxx.get("_opStatus").equals("remove") && !fjxx.get("_opStatus").equals("deal")) {
					String ops = "create";
					if (fjxx.get("JLXH") != null && fjxx.get("JLXH").toString().trim().length() > 0) {
						ops = "update";
					}
					fjxx.put("YFSB", 0);
					fjxx.put("FYFS", 0);
					fjxx.put("KSSJ",BSHISUtil.toDate(fjxx.get("KSSJ").toString()));
					// fjxx.put("TZSJ", r.get("TZSJ"));
					fjxx.put("BRKS",Long.parseLong(brxx.get("BRKS")==null?"0":brxx.get("BRKS").toString()));
					fjxx.put("BRBQ",Long.parseLong(brxx.get("BRBQ")==null?"0":brxx.get("BRBQ").toString()));
					fjxx.put("BRCH", brxx.get("BRCH"));
					fjxx.put("SRKS",Long.parseLong(brxx.get("BRBQ")==null?"0":brxx.get("BRBQ").toString()));
					fjxx.remove("QRSJ");
					if (("1").equals(fjxx.get("LSYZ") + "")) {
						fjxx.put("TZSJ", fjxx.get("KSSJ"));
						fjxx.put("TZYS", fjxx.get("YSGH"));
					}
					String uniqueIdStr = fjxx.get("uniqueId") + "";
					fjxx.remove("uniqueId");
					dao.doSave(ops, "phis.application.war.schemas.ZY_BQYZ_CQ",fjxx,false);
					fjxx.put("uniqueId", uniqueIdStr);
					fjxx.put("_opStatus", "deal");
					//yx-2018-04-27南京金保判断是否对照自编码-b
/*					if(brxz.equals("2000")){
						String sql="select a.yyzbm as YYZBM from gy_ylmx a where a.jgid=:jgid and a.fyxh=:fyxh";
						Map<String, Object> p=new HashMap<String, Object>();
						p.put("jgid",manageUnit.substring(0,9));//溧水是根据住院去判断
						p.put("fyxh",Long.parseLong(fjxx.get("YPXH")+""));
						Map<String, Object> zbmmap=dao.doSqlLoad(sql,p);
						if(zbmmap==null || zbmmap.get("YYZBM")==null || (zbmmap.get("YYZBM")+"").length()<1 ){
							if(njjbmsg.length()==0){
								njjbmsg+="医嘱中"+fjxx.get("YZMC");
							}else{
								njjbmsg+="、"+fjxx.get("YZMC");
							}
						}
					}*/
					//yx-2018-04-27南京金保判断是否对照自编码-e
				}
			}
/*			if(njjbmsg.length()>0){
				res.put("njjbmsg",njjbmsg+"未对照医保自编码，将导致不能出院！");
			}*/
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

	/**
	 * 保存项目附加信息
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveAddProjects(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> r = (Map<String, Object>) body.get(i);
				String op = (String) r.get("_opStatus");
				if (op.equals("remove")) {
					dao.doRemove(Long.parseLong(r.get("JLXH").toString()),
							BSPHISEntryNames.GY_XMGL);
					continue;
				}
				dao.doSave(op, BSPHISEntryNames.GY_XMGL, r, false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("附加项目保存失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("附加项目信息校验失败!", e);
		}

	}

	/**
	 * 复核医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public int doSaveReview(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		try {
			body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			body.put("FHGH", user.getUserId() + "");
			body.put("JGID", manageUnit);
			body.put("FHSJ", new Date());
			String uid = (String) user.getUserId();
			// modify by yangl 2014.2.26 增加抗菌药物判断
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("LSYZ", 1);
			params.put("ZYH", body.get("ZYH"));
			params.put("JGID", body.get("JGID"));
			doCheckAntibacterial(params, ctx);

			// 获取是否可以为同一个工号复核
			String is_fhsr_same = ParameterUtil.getParameter(manageUnit,
					BSPHISSystemArgument.YZLRFHTYGH, ctx);
			if ("0".equals(is_fhsr_same)) {// 0不可为同一工号
				body.put("CZGH", uid);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("JGID", manageUnit);
				map.put("LSYZ", body.get("LSYZ"));
				map.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
				map.put("CZGH", uid);
				/*****begin停嘱复核 qihao 2018-8-30******/
					StringBuffer countSb = new StringBuffer();
					countSb.append("((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (TZFHBZ=0 or TZFHBZ is null) and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID and CZGH = :CZGH");
					StringBuffer sb = new StringBuffer();
					sb.append("update ZY_BQYZ SET TZFHBZ=1, FHGH=:FHGH,TZFHSJ=:FHSJ where ((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (TZFHBZ=0 or TZFHBZ is null) and TZSJ is not null and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID and CZGH != :CZGH");
					/*****end停嘱复核 qihao 2018-8-30******/
					StringBuffer countSb1 = new StringBuffer();
					countSb1.append("((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (FHBZ=0 or FHBZ is null) and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID and CZGH = :CZGH");
					StringBuffer sb1 = new StringBuffer();
					sb1.append("update ZY_BQYZ SET FHBZ=1, FHGH=:FHGH,FHSJ=:FHSJ where ((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (FHBZ=0 or FHBZ is null) and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID and CZGH != :CZGH");
				   
					if(body.containsKey("JLXHS")){//add by LIZHI 2018-04-04 增加单个医嘱复核功能
						List<Object> l=(List<Object>)body.get("JLXHS");
						List<Long> jlxhs=new ArrayList<Long>();
						for(Object o:l){
							jlxhs.add(MedicineUtils.parseLong(o));
						}
						map.put("JLXHS", jlxhs);
						body.put("JLXHS",jlxhs);
						countSb.append(" and JLXH in (:JLXHS)");
						sb.append(" and JLXH in (:JLXHS)");
						countSb1.append(" and JLXH in (:JLXHS)");
						sb1.append(" and JLXH in (:JLXHS)");
					
					}
					long count = dao.doCount("ZY_BQYZ",countSb.toString(),map);
					long count1 = dao.doCount("ZY_BQYZ",countSb1.toString(),map);
					int rs = dao.doUpdate(sb.toString(),body);
					int rs1 = dao.doUpdate(sb1.toString(),body);
					if (count > 0) {
						body.put("RMESSAGE", "1");
					}
                  return rs+rs1;	
			} else {// 可为同一个工号
				/*****begin停嘱复核 qihao 2018-8-30******/
					StringBuffer sb2 = new StringBuffer();
					sb2.append("update ZY_BQYZ SET TZFHBZ=1 , FHGH=:FHGH,TZFHSJ=:FHSJ where ((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (TZFHBZ=0 or TZFHBZ is null) and TZSJ is not null and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID");
					/*****end停嘱复核 qihao 2018-8-30******/
					StringBuffer sb3 = new StringBuffer();
					sb3.append("update ZY_BQYZ SET FHBZ=1 , FHGH=:FHGH,FHSJ=:FHSJ where ((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (FHBZ=0 or FHBZ is null) and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID");
					if(body.containsKey("JLXHS")){
						List<Object> l=(List<Object>)body.get("JLXHS");
						List<Long> jlxhs=new ArrayList<Long>();
						for(Object o:l){
							jlxhs.add(MedicineUtils.parseLong(o));
						}
						body.put("JLXHS",jlxhs);
						sb2.append(" and JLXH in (:JLXHS)");
						sb3.append(" and JLXH in (:JLXHS)");
					}
					int t=dao.doUpdate(sb2.toString(),body);
					int t1=dao.doUpdate(sb3.toString(),body);
					return t+t1;
				
				}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("住院医嘱复核失败!", e);
		}
	}

	/**
	 * 取消复核医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int doSaveUnReview(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		try {
			body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			body.put("YZZH", Long.parseLong(body.get("YZZH").toString()));
			body.put("FHGH", (String) user.getUserId());
			body.put("JGID", manageUnit);
				StringBuffer sb1 = new StringBuffer();
			sb1.append("update ZY_BQYZ SET FHBZ=0 ,FHGH=null,FHSJ=null where FHGH=:FHGH and FHBZ=1 and SYBZ<>1 and QRSJ is null and ZYH=:ZYH and JGID=:JGID and YZZH=:YZZH");
			/*****begin取消停嘱复核 qihao 2018-8-30******/
			StringBuffer sb2 = new StringBuffer();
			sb2.append("update ZY_BQYZ SET TZFHBZ=0 ,FHGH=null,TZFHSJ=null where FHGH=:FHGH and TZFHBZ=1 and SYBZ<>1 and QRSJ is null and ZYH=:ZYH and JGID=:JGID and YZZH=:YZZH");
			/*****begin取消停嘱复核 qihao 2018-8-30******/
			
			int t=dao.doUpdate(sb1.toString(),body);
			int t1=dao.doUpdate(sb2.toString(),body);
			return t+t1;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("住院医嘱取消复核失败!", e);
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
			doCheckAntibacterial(params, ctx);
			body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
			body.put("YSGH", (String) user.getUserId());
			body.put("JGID", manageUnit);

			return dao
					.doUpdate(
							"update ZY_BQYZ SET YSTJ=1 where YSGH=:YSGH and YSTJ=0  and YSBZ=1 and LSYZ=:LSYZ and ZYH=:ZYH and JGID=:JGID",
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
	public void doCheckAntibacterial(Map<String, Object> body, Context ctx)
			throws PersistentDataOperationException,
			ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String QYKJYWGL = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYKJYWGL, ctx);
		String QYZYKJYWSP = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYZYKJYWSP, ctx);
		if ("1".equals(QYKJYWGL) && "1".equals(QYZYKJYWSP)
				&& "1".equals(body.get("LSYZ").toString())) {// 参数启用且是临时医嘱时
			// 查询医嘱中所有需要审批的抗菌药物的数量
			String sql = "select b.YPXH as YPXH,b.YPMC as YPMC,sum(a.YCSL) as TOTALSL from ZY_BQYZ a,YK_TYPK b ,AMQC_SYSQ01 c   where a.YPXH=b.YPXH  and a.ypxh = c.ypxh   and a.zyh =  c.jzxh  and c.jzlx = 1  and c.spjg = 0  and c.ZFBZ = 0 and b.KSBZ=1 and b.SFSP=1 and a.LSBZ =  0 and a.LSYZ=:LSYZ and a.ZYH=:ZYH and a.JGID=:JGID group by b.YPXH,b.YPMC";

			List<Map<String, Object>> list = dao.doSqlQuery(sql, body);
			for (Map<String, Object> ypxx : list) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ZYH", body.get("ZYH").toString());
				// params.put("YSGH", body.get("YSGH"));
				params.put("JGID", body.get("JGID"));
				params.put("YPXH", Long.parseLong(ypxx.get("YPXH").toString()));
				// 不论是谁申请，只要审批通过，其他医生也能使用
				Map<String, Object> qmqc_sysq01 = dao
						.doLoad("select sum(SPYL) as SPYL from AMQC_SYSQ01 where JZLX=1 and JZXH=:ZYH and YPXH=:YPXH and (SPJG=1 or SPJG=2) and ZFBZ=0 and JGID=:JGID group by YPXH",
								params);
				if (qmqc_sysq01 == null
						|| qmqc_sysq01.get("SPYL") == null
						|| qmqc_sysq01.get("SPYL").toString().trim().length() == 0) {
					throw new ModelDataOperationException("抗菌药物【"
							+ ypxx.get("YPMC") + "】尚未审批或审批未通过!");
				} else {
					if (Double.parseDouble(ypxx.get("TOTALSL").toString()) > Double
							.parseDouble(qmqc_sysq01.get("SPYL").toString())) {
						throw new ModelDataOperationException("抗菌药物【"
								+ ypxx.get("YPMC") + "】的用量超过审批用量，<br />已审批用量为"
								+ qmqc_sysq01.get("SPYL").toString()
								+ "!当前总用量为" + ypxx.get("TOTALSL").toString());
					}
				}
			}
		}
	}

	/**
	 * 病区退回医嘱
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public int doSaveGoback(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			//zhaojian 2017-09-20 增加修改检查开单的计费标志，以便护士站退回后医生可以将检查单作废
			int res = dao.doUpdate("update ZY_BQYZ SET JFBZ=9 where JLXH in ("+body.get("JLXH")+") and YSTJ=1 and XMLX=4 and ZFPB=0",null);//针对检查开单
			return dao.doUpdate("update ZY_BQYZ SET YSTJ=0 where JLXH in ("+body.get("JLXH")+") and YSTJ=1",null);
			/*return dao.doUpdate(
					"update ZY_BQYZ SET YSTJ=0 where JLXH in ("+body.get("JLXH")+") and YSTJ=1",
					null);*/
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("住院医嘱退回失败!", e);
		}
	}

	/**
	 * 出院证明
	 */
	public void doSaveLeaveHospitalProve(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		BSPHISUtil.uf_cyrq_set(body, dao, ctx);
		if (body.containsKey("JLXH")) {
			if (body.get("JLXH") != null && body.get("JLXH") != "") {
				body.put("JLXH", body.get("JLXH") + "");
				saveCyYz(body, ctx);
			}
		}
	}

	/**
	 * 出院判别
	 */
	public Map<String, Object> doCanLeaveHospital(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			// 判断是否有出院证
			long l = dao.doCount("ZY_RCJL",
					"ZYH=:ZYH and CZLX=-1 AND BQPB=0 and JGID =:JGID", body);
			if (l > 0) {
				ret.put("hasLeaveHosProve", true);
			}
			// 将可以转成历史的医嘱转成历史医嘱
			List<Map<String, Object>> listGY_SYPC = BSPHISUtil
					.u_his_share_yzzxsj(null, dao, ctx);
			BSPHISUtil.uf_lsyz(listGY_SYPC, body, dao, ctx);
			// 判断是否有药品医嘱未停或未发药
			l = dao.doCount(
					"ZY_BQYZ",
					"LSBZ=0 and JFBZ<>3 and ZFBZ=0 and YPLX>0 and ZYH=:ZYH and JGID=:JGID and ZFYP!=1",
					body);
			if (l > 0) {
				ret.put("hasErrorMeds", true);
			}
			// 判断是否有项目医嘱未停或未执行
			l = dao.doCount(
					"ZY_BQYZ",
					"LSBZ=0 and JFBZ<>3 and ZFBZ=0 and YPLX=0 and ZYH=:ZYH and JGID=:JGID",
					//zhaojian 2017-09-28 解决检验项目历史标志为1时确认时间却为空的情况，通知出院时未提醒问题
					//"(LSBZ=0 or(QRSJ is null or QRsj='')) and JFBZ<>3 and ZFBZ=0 and YPLX=0 and ZYH=:ZYH and JGID=:JGID",
					body);
			if (l > 0) {
				ret.put("hasErrorPros", true);
			}
			// 判断是否未提交或未确认的退药单
			l = dao.doCount("BQ_TYMX",
					"(TJBZ=1 or TYRQ is null) and ZYH=:ZYH and JGID=:JGID",
					body);
			if (l > 0) {
				ret.put("hasErrorRetMed", true);
			}
			// 判断是否有项目医嘱未停或未执行 YJ_ZY01
			body.put("JGID", Long.parseLong(body.get("JGID").toString()));
			l = dao.doCount("YJ_ZY01",
					"ZYH=:ZYH and JGID=:JGID and (ZXPB = 0 OR ZXPB IS NULL)",
					body);
			if (l > 0) {
				ret.put("hasErrorPros", true);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("出院判别未知错误!", e);
		}
		return ret;
	}

	/**
	 * 出院通知
	 */
	public void doSaveLeaveHospital(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		body.put("ZYH", Long.parseLong(body.get("ZYH").toString()));
		try {
			Map<String, Object> rcjl = dao
					.doLoad("select LCRQ as LCRQ,CYFS as CYFS,BZXX as BZXX from ZY_RCJL"
							+ " where ZYH=:ZYH and CZLX=-1 AND BQPB=0 and JGID =:JGID",
							body);
			if (rcjl == null) {
				throw new ModelDataOperationException("该病人还未办理出院证，请先办理出院证!");
			}
			long l = dao.doCount("ZY_BRRY",
					"ZYH=:ZYH and CYPB>=8 and JGID=:JGID", body);
			if (l > 0) {
				throw new ModelDataOperationException("该病人已出院，不能对该病人作确认出院通知手续!");
			}
			body.put("CZRQ", new Date());
			body.put("CZR", (String) user.getUserId());
			body.put("CZLX", 3);
			body.put("CYSJ", rcjl.get("LCRQ"));
			body.put("CYFS", rcjl.get("CYFS"));
			dao.doSave("create", BSPHISEntryNames.ZY_CYJL, body, false);

			Map<String, Object> brry = new HashMap<String, Object>();
			brry.put("CYRQ", rcjl.get("LCRQ"));
			brry.put("CYFS", rcjl.get("CYFS"));
			brry.put("ZYH", body.get("ZYH"));
			brry.put("JGID", body.get("JGID"));
			dao.doUpdate(
					"update ZY_BRRY set CYRQ=:CYRQ,CYFS=:CYFS,CYPB=1 where ZYH=:ZYH and JGID=:JGID",
					brry);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("确认出院通知失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("确认出院通知校验失败!", e);
		}

	}

	/**
	 * 取消出院通知
	 */
	public void doSaveCancelLeaveHospital(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long ZYH = Long.parseLong(body.get("ZYH").toString());
		body.put("ZYH", ZYH);
		try {
			long l = dao.doCount("ZY_BRRY",
					"ZYH=:ZYH and CYPB>=8 and JGID=:JGID", body);
			if (l > 0) {
				throw new ModelDataOperationException(
						"该病人已出院，不能对该病人作取消出院结算证明手续!");
			}
			// Map<String, Object> ZY_BRRY =
			// dao.doLoad(BSPHISEntryNames.ZY_BRRY,
			// ZYH);
			// if (ZY_BRRY.get("YWLSH") != null && ZY_BRRY.get("YWLSH") != ""
			// && (Long.parseLong(ZY_BRRY.get("YWLSH") + "") > 0l)) {
			// if ("1".equals(ZY_BRRY.get("SCBZ") + "")) {
			// throw new ModelDataOperationException(
			// "当前病人为医保病人，请先撤销已上传的明细!");
			// }
			// }
			body.put("CZRQ", new Date());
			body.put("CZR", (String) user.getUserId());
			body.put("CZLX", 4);
			body.put("CYSJ", new Date());
			body.put("CYFS", 0);
			dao.doSave("create", BSPHISEntryNames.ZY_CYJL, body, false);
			Map<String, Object> brry = new HashMap<String, Object>();
			brry.put("CYRQ", null);
			brry.put("CYFS", null);
			brry.put("ZYH", body.get("ZYH"));
			brry.put("JGID", body.get("JGID"));
			dao.doUpdate(
					"update ZY_BRRY set CYRQ=:CYRQ,CYFS=:CYFS,CYPB=0 where ZYH=:ZYH and JGID=:JGID",
					brry);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("取消出院通知失败!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("取消出院通知校验失败!", e);
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

	/**
	 * 查询医嘱复核列表
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDoctorReviewList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			int pageNo = Integer.parseInt(req.get("pageNo").toString()) - 1;
			int pageSize = Integer.parseInt(req.get("pageSize").toString());
			List<Object> cnds = (List<Object>) req.get("cnd");
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID

			StringBuilder sqlBuilder = new StringBuilder();
			Map<String, Object> parameter = new HashMap<String, Object>();
			sqlBuilder
					.append("SELECT c.ZYHM as ZYHM,c.BRXM as BRXM, c.BRCH as BRCH, a.YZMC as YZMC, a.YCJL as YCJL, a.YCSL as YCSL, ");
			sqlBuilder
					.append(" a.SYPC as SYPC, b.XMMC as XMMC, a.YPDJ as YPDJ, a.YSGH as YSGH, ");
			sqlBuilder.append(
					BSPHISUtil.toChar("a.TZSJ", "YYYY-MM-DD HH24:MI:SS"))
					.append(" as TZSJ, a.TZYS as TZYS, a.MZCS as MZCS,");
			sqlBuilder.append(BSPHISUtil.toChar("a.KSSJ",
					"YYYY-MM-DD HH24:MI:SS"));
			sqlBuilder
					.append(" as KSSJ, a.MRCS as MRCS, a.CZGH as CZGH, a.QRSJ as QRSJ, a.YPXH as YPXH, a.ZYH as ZYH, a.JLXH as JLXH, ");
			sqlBuilder
					.append(" a.YJZX as YJZX, a.ZXKS as ZXKS, a.FHGH as FHGH, a.YPYF as YPYF, a.YZZH as YZZH, a.YEPB as YEPB, a.BZXX as BZXX, ");
			sqlBuilder
					.append(" a.FYSX as FYSX, a.YFSB as YFSB, a.LSYZ as LSYZ, a.YZPB as YZPB, a.LSBZ as LSBZ, a.JFBZ as JFBZ, a.YPLX as YPLX, ");
			sqlBuilder
					.append(" d.JLDW as JLDW FROM ZY_BQYZ a left outer join ZY_YPYF b on a.YPYF = b.YPYF left outer join YK_TYPK d on a.YPXH = d.YPXH ,ZY_BRRY c  ");
			sqlBuilder
					.append(" WHERE ((a.YSBZ=1 and a.YSTJ = 1) or a.YSBZ=0) and ( a.ZYH = c.ZYH) AND ( a.JGID = :al_jgid ) AND ");
			sqlBuilder
					.append(" ( a.SRKS = :al_hsql ) AND   ( a.FHGH IS NULL) AND ( a.YZPB = 0 ) AND ( c.CYPB = 0 ) ");
			if (cnds != null) {
				String where = " and "
						+ ExpressionProcessor.instance().toString(cnds);
				sqlBuilder.append(where);
			}
			sqlBuilder.append(" ORDER BY c.BRCH,a.YZZH ");
			parameter.put("al_jgid", JGID);
			parameter.put("al_hsql",
					Long.parseLong(user.getProperty("wardId") + ""));

			System.out.println(parameter + " ====== " + sqlBuilder.toString());
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

			List<Map<String, Object>> list = dao.doSqlQuery(
					sqlBuilder.toString(), parameter);
			SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BQYZ_FH");
			res.put("body", list);
		} catch (Exception e) {
			logger.error("查询医嘱复核列表失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询医嘱复核列表失败"
							+ e.getMessage());
		}

	}

	/**
	 * 保存批量复核
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @param type
	 *            1 为单病人 2 为全部
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveBatchReview(Map<String, Object> req,
			Map<String, Object> res, Context ctx, int type)
			throws ModelDataOperationException {

		try {
			boolean tsbz = false;
			String brch = "", tmpBrch = "", tmpCzgh = "";
			if (type == 1) {
				brch = ((Map<String, Object>) req.get("body")).get("BRCH") + "";
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			String uid = (String) user.getUserId();
			String is_fhsr_same = ParameterUtil.getParameter(JGID,
					BSPHISSystemArgument.YZLRFHTYGH, ctx);
			StringBuilder sqlBuilder = new StringBuilder();
			Map<String, Object> parameter = new HashMap<String, Object>();
			sqlBuilder
					.append("SELECT c.BRXM as BRXM, c.BRCH as BRCH, a.YZMC as YZMC, a.YCJL as YCJL, a.YCSL as YCSL, ");
			sqlBuilder
					.append(" a.SYPC as SYPC, b.XMMC as XMMC, a.YPDJ as YPDJ, a.YSGH as YSGH, a.TZSJ as TZSJ, a.TZYS as TZYS, a.MZCS as MZCS,");
			sqlBuilder
					.append(" a.KSSJ as KSSJ, a.MRCS as MRCS, a.CZGH as CZGH, a.QRSJ as QRSJ, a.YPXH as YPXH, a.ZYH as ZYH, a.JLXH as JLXH, ");
			sqlBuilder
					.append(" a.YJZX as YJZX, a.ZXKS as ZXKS, a.FHGH as FHGH, a.YPYF as YPYF, a.YZZH as YZZH, a.YEPB as YEPB, a.BZXX as BZXX, ");
			sqlBuilder
					.append(" a.FYSX as FYSX, a.YFSB as YFSB, a.LSYZ as LSYZ, a.YZPB as YZPB, a.LSBZ as LSBZ, a.JFBZ as JFBZ, a.YPLX as YPLX, ");
			sqlBuilder
					.append(" d.JLDW as JLDW FROM ZY_BQYZ a left outer join ZY_YPYF b on a.YPYF = b.YPYF left outer join YK_TYPK d on a.YPXH = d.YPXH ,ZY_BRRY c  ");
			sqlBuilder
					.append(" WHERE ((a.YSBZ=1 and a.YSTJ = 1) or a.YSBZ=0) and ( a.ZYH = c.ZYH) AND ( a.JGID = :al_jgid ) AND ");
			sqlBuilder
					.append(" ( a.SRKS = :al_hsql ) AND   ( a.FHGH IS NULL) AND ( a.YZPB = 0 ) AND ( c.CYPB = 0 ) ");
			sqlBuilder.append(" ORDER BY c.BRCH,a.YZZH ");
			parameter.put("al_jgid", JGID);
			parameter.put("al_hsql",
					Long.parseLong(user.getProperty("wardId") + ""));
			List<Map<String, Object>> list = dao.doSqlQuery(
					sqlBuilder.toString(), parameter);
			// Map<String, Object> body = null;
			if (list != null) {
				for (Map<String, Object> map : list) {
					tmpBrch = String.valueOf(map.get("BRCH"));
					tmpCzgh = String.valueOf(map.get("CZGH"));
					// 如果type为2说明为全部按钮操作， 或者 type为1并且病人床号相等
					if ((type == 2) || (type == 1 && brch.equals(tmpBrch))) {
						// 0：不可为同一个人 1：可为同一个人
						if (!"1".equals(is_fhsr_same) && uid.equals(tmpCzgh)) {
							tsbz = true;
						} else {
							// body = new HashMap<String, Object>();
							// body.put("ZYH", Long.parseLong(map.get("ZYH") +
							// ""));
							// body.put("LSYZ", Integer.parseInt(map.get("LSYZ")
							// + ""));
							// this.doSaveReview(body, ctx);
							sqlBuilder = new StringBuilder();
							sqlBuilder
									.append("UPDATE ZY_BQYZ SET FHBZ=1, FHGH=:FHGH,FHSJ=:FHSJ WHERE JLXH = :JLXH AND ((YSBZ=1 and YSTJ=1) or YSBZ=0)  and (FHBZ=0 or FHBZ is null) AND JGID = :JGID");
							parameter = new HashMap<String, Object>();
							parameter.put("FHGH", uid);
							parameter.put("JLXH",
									Long.parseLong(map.get("JLXH") + ""));
							parameter.put("JGID", JGID);
							parameter.put("FHSJ", new Date());
							dao.doSqlUpdate(sqlBuilder.toString(), parameter);
						}
					}

				}
			}
			if (tsbz) {
				res.put("body", "保存医嘱批量复核成功!但有医嘱,因录入与复核人不能为同人限制没有复核!");
			} else {
				res.put("body", "保存医嘱批量复核成功!");
			}
		} catch (Exception e) {
			logger.error("保存医嘱批量复核失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存医嘱批量复核失败"
							+ e.getMessage());
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
		String hql = "update ZY_BQYZ set LSBZ=0,TZSJ=null,TZYS=null where ZYH=:ZYH and YZZH=:YZZH";
		if (!isGroup) {
			hql += " and JLXH=:JLXH";
			params.put("JLXH", Long.parseLong(body.get("JLXH").toString()));
		}
		try {
			dao.doUpdate(hql, params);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存医嘱批量复核失败", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHerbInfo(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameter = new HashMap<String, Object>();
			List<Map<String, Object>> list_herb = (List<Map<String, Object>>) req
					.get("body");
			Long yzzh = 0l;
			String jgid = "";
			Long zyh = 0l;
			Map<String, Object> map_form_dateBase = new HashMap<String, Object>();
			for (Map<String, Object> map_herb : list_herb) {
				if (map_herb.containsKey("JLXH") && map_herb.get("JLXH") != "") {
					map_form_dateBase = dao.doLoad(
							"phis.application.war.schemas.ZY_BQYZ_CY",
							map_herb.get("JLXH"));
					yzzh = Long.parseLong(map_herb.get("YZZH") + "");
					jgid = (String) map_herb.get("JGID");
					zyh = Long.parseLong(map_herb.get("ZYH") + "");
					break;
				}
			}
			parameter.put("YZZH", yzzh);
			parameter.put("JGID", jgid);
			parameter.put("ZYH", zyh);
			dao.doUpdate(
					"delete from ZY_BQYZ where JGID=:JGID and ZYH=:ZYH and YZZH=:YZZH",
					parameter);
			for (Map<String, Object> map_herb : list_herb) {
				map_form_dateBase.put("KSSJ", new Date());
				map_form_dateBase.put("YZMC", map_herb.get("YZMC") + "");
				map_form_dateBase.put("YCSL",
						Double.parseDouble(map_herb.get("YCSL") + ""));
				map_form_dateBase.put("JZ",
						Integer.parseInt(map_herb.get("JZ") + ""));
				map_form_dateBase.put("SYPC", map_herb.get("SYPC") + "");
				map_form_dateBase.put("YPZS",
						Integer.parseInt(map_herb.get("YPZS") + ""));
				map_form_dateBase.put("YPYF",
						Long.parseLong(map_herb.get("YPYF") + ""));
				map_form_dateBase.put("CFTS",
						Integer.parseInt(map_herb.get("CFTS") + ""));
				dao.doSave("create", BSPHISEntryNames.ZY_BQYZ,
						map_form_dateBase, true);

			}
		} catch (Exception e) {
			logger.error("保存草药医嘱失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存草药医嘱失败"
							+ e.getMessage());
		}
	}

	public Map<String, Object> doLoadClinicsCharge(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> res = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		try {
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select a.ZYH as ZYH,to_char(a.FYRQ, 'yyyy-MM-dd') as FYRQ,a.FYXH as FYXH,a.FYMC as FYMC,sum(a.FYSL) as FYSL,a.FYDJ as FYDJ,a.YSGH as YSGH,b.personName as YSXM from ZY_FYMX a"
									+ " left join sys_personnel b on a.YSGH = b.personId where a.ZYH = :ZYH and a.YPLX = 0 and a.JSCS=0 group by a.ZYH,a.FYXH, a.FYMC, a.FYDJ,a.YSGH, to_char(a.FYRQ, 'yyyy-MM-dd'),b.personName"
									+ " having sum(a.FYSL) > 0 order by to_char(a.FYRQ, 'yyyy-MM-dd') desc",
							params);
			res.put("body", list);
			res.put("totalCount", list.size());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病区费用信息出错!", e);
		}
		return res;
	}

	public void doSaveRefundClinic(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			if (body != null && body.size() > 0) {
				for (Map<String, Object> tfmx : body) {
					params.put("FYXH", tfmx.get("FYXH"));
					params.put("ZYH", tfmx.get("ZYH"));
					params.put("FYDJ", tfmx.get("FYDJ"));
					params.put("FYRQ", tfmx.get("FYRQ"));
					// 按FYXH FYDJ FYRQ来确定一组记录
					List<Map<String, Object>> list = dao
							.doSqlQuery(
									"select JLXH as JLXH,JGID,ZYH,FYRQ,FYXH,FYMC,YPCD,FYSL,FYDJ,ZJJE,ZFJE,YSGH,SRGH,QRGH,FYBQ,FYKS,ZXKS,JFRQ,XMLX,YPLX,FYXM,JSCS,ZFBL,YZXH,HZRQ,YJRQ,ZLJE,ZLXZ,YEPB,DZBL "
											+ "from ZY_FYMX where ZYH=:ZYH and FYXH=:FYXH and FYDJ=:FYDJ and to_char(FYRQ,'yyyy-MM-dd')=:FYRQ",
									params);
					if (list.size() > 0) {
						Map<String, Object> r = list.get(0);// 同组的取任意一条作为基础信息
						r.remove("JLXH");
						r.put("FYSL", -Double.parseDouble(tfmx.get("FYSL")
								.toString()));
						r.put("ZJJE",
								(Double) r.get("FYSL")
										* Double.parseDouble(r.get("FYDJ")
												.toString()));
						r.put("ZFJE",
								(Double) r.get("ZJJE")
										* Double.parseDouble(r.get("ZFBL")
												.toString()));
						r.put("ZLJE",
								(Double) r.get("ZJJE")
										* Double.parseDouble(r.get("ZFBL")
												.toString()));
						r.put("JFRQ", new Date());
						r.put("SRGH", user.getUserId());
						dao.doSave("create", BSPHISEntryNames.ZY_FYMX, r, false);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("报错病区退费信息出错!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("报错病区退费信息出错!", e);
		}
	}

	// @SuppressWarnings("unchecked")
	public Map<String, Object> doQueryHerbInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		// List<Object> cnd = (List<Object>) req.get("cnd");
		long zyh = Long.parseLong(req.get("ZYH") + "");
		long yzzh = Long.parseLong(req.get("YZZH") + "");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", zyh);
			parameters.put("YZZH", yzzh);

			List<Map<String, Object>> list = dao
					.doQuery(
							"select a.JLXH as JLXH,a.YPXH as YPXH,a.YZMC as YZMC,a.YCJL as YCJL,a.JZ as JZ,a.YCSL as YCSL,a.YFDW as YFDW,a.YFBZ as YFBZ,a.YPDJ as YPDJ,a.YPLX as YPLX,a.YPCD as YPCD,a.MRCS as MRCS,a.YZZXSJ as YZZXSJ,a.SRCS as SRCS,a.FYFS as FYFS,a.SYPC as SYPC,a.XMLX as XMLX,a.YFSB as YFSB,b.JLDW as JLDW,b.YPJL as YPJL from ZY_BQYZ a,YK_TYPK b where a.YPXH = b.YPXH and a.ZYH = :ZYH and a.YZZH=:YZZH",
							parameters);
			list = SchemaUtil.setDictionaryMassageForList(list,
					"phis.application.war.schemas.ZY_BQYZ_CY_List");
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取病区费用信息出错!", e);
		}
		return res;
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
							"select sum(YCSL) as SYSL from ZY_BQYZ where ZYH=:ZYH and YPXH=:YPXH and str(KSSJ,'yyyy-mm-dd')=:KSSJ",
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
	 * 生成会诊医嘱记录----------------------------------------
	 */
	public Map<String, Object> saveCyYz(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		int cyfs = 0;
		if (body.get("CYFS") != null && body.get("CYFS") != "") {
			cyfs = Integer.parseInt(body.get("CYFS") + "");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Long zyh = Long.parseLong(body.get("ZYH").toString());// 住院号
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ZYH", zyh);
			params.put("JGID", jgid);
			Map<String, Object> brry = dao
					.doLoad("select BRKS as BRKS,BRBQ as BRBQ,BRCH as BRCH FROM ZY_BRRY Where JGID=:JGID and ZYH=:ZYH",
							params);
			map.put("ZYH", zyh);// 住院号
			if (brry != null) {
				if (brry.get("BRKS") != null) {
					map.put("BRKS", Long.parseLong(brry.get("BRKS").toString()));// 病人科室
					map.put("SRKS", Long.parseLong(brry.get("BRKS").toString()));// 输入科室
					map.put("ZXKS", Long.parseLong(brry.get("BRKS").toString()));// 执行科室

				}
				if (brry.get("BRBQ") != null) {
					map.put("BRBQ", Long.parseLong(brry.get("BRBQ").toString()));// 病人病区
				}
				if (brry.get("BRCH") != null) {
					map.put("BRCH", brry.get("BRCH") + "");// 病人床号
				}
			}
			Map<String, Object> zyyspar = new HashMap<String, Object>();
			zyyspar.put("ZYH", zyh);
			map.put("YPXH", Long.parseLong("0"));//
			map.put("YPCD", (long) 0);
			map.put("XMLX", 10);
			map.put("YPLX", 0);// 药片类型
			map.put("TZFHBZ", 0);// 停嘱复核标志
			map.put("FHBZ", 0);
			map.put("YCJL", (double) 1);// 一次剂量
			map.put("YCSL", (double) 1);// 一次数量
			map.put("LSYZ", 1);// 临时医嘱
			map.put("MRCS", 1);
			map.put("MZCS", 0);
			map.put("JFBZ", 3);// 计费标志
			map.put("YPYF", (long) 0);
			map.put("SRCS", 1);// 首日次数
			if (cyfs != 5) {
				map.put("YZMC", "出院");// 药嘱名称
				map.put("YDLB", "303");// 约定类别
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				String ofdate = sdf.format(new Date());
				String y = ofdate.substring(0, 4);
				String m = ofdate.substring(4, 6);
				String d = ofdate.substring(6, 8);
				String h = ofdate.substring(8, 10);
				String f = ofdate.substring(10);
				map.put("YZMC", y + "年" + m + "月" + d + "日" + h + "时" + f + "分"
						+ "死亡");// 药嘱名称
				map.put("YDLB", "304");// 约定类别
			}
			map.put("YWID", Long.parseLong(body.get("JLXH").toString()));// 申请序号
			map.put("CZGH", user.getUserId());// 转抄
			Map<String, Object> zyysmap = dao.doLoad(
					"select ZSYS as ZSYS from ZY_BRRY where ZYH=:ZYH", zyyspar);
			if (zyysmap != null) {
				if (zyysmap.containsKey("ZSYS")) {
					if (zyysmap.get("ZSYS") != null) {
						map.put("YSGH", zyysmap.get("ZSYS") + "");// 开嘱医生
					}
				}
			}
			List<Map<String, Object>> yzzhlist = dao.doSqlQuery(
					"select max(YZZH) as YZZH from ZY_BQYZ where ZYH=:ZYH",
					zyyspar);
			if (yzzhlist.size() > 0) {
				if (yzzhlist.get(0).get("YZZH") != null) {
					map.put("YZZH",
							Long.parseLong(yzzhlist.get(0).get("YZZH") + "") + 1);// 开嘱医生
				} else {
					map.put("YZZH", 1l);
				}
			}
			map.put("KSSJ", new Date());// 开嘱时间
			map.put("JGID", jgid);// 机构ID
			map.put("SFJG", "0");
			map.put("YPDJ", (double) 0);
			map.put("SYBZ", 0);
			map.put("ZFPB", 0);
			map.put("YJZX", 0);
			map.put("YJXH", (long) 0);
			map.put("ZFBZ", 0);// 作废标志
			map.put("FYSX", 0);
			map.put("YEPB", 0);
			map.put("YFSB", (long) 0);
			map.put("LSYZ", 1);
			map.put("LSBZ", 0);
			map.put("YZPB", 0);
			map.put("JFBZ", 3);
			map.put("TPN", 0);
			int zyysqy = Integer.parseInt(ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.ZYYSQY, ctx));
			if (zyysqy == 1) {
				map.put("YSBZ", 1);
			} else {
				map.put("YSBZ", 0);
			}
			map.put("YSTJ", 0);
			map.put("PSPB", 0);
			map.put("SYPC", "ST");
			Map<String, Object> map2 = dao.doInsert(BSPHISEntryNames.ZY_BQYZ,
					map, false);
			return map2;
		} catch (Exception e) {
			throw new ModelDataOperationException("出院证保存失败!", e);
		}
	}

	public void doQueryJzyfsz(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		String brbq = user.getProperty("wardId") + "";
		if(brbq==null || brbq.equals("null") || brbq.length()==0){
			res.put("code", "502");
			res.put("msg", "未获取到用户"+user.getUserId()+"的登录病区");
			logger.error("未获取到用户"+user.getUserId()+"的登录病区");
			return;
		}
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BQDM", Long.parseLong(brbq));
			parameters.put("JGID", manageUnit);
			parameters.put("TYPE", 2);
			List<Map<String, Object>> yfsbjzList = dao
					.doSqlQuery(
							"select * from BQ_FYYF where BQDM=:BQDM and JGID =:JGID and TYPE=:TYPE",
							parameters);

			if (yfsbjzList.size() < 3) {
				res.put("JZYF", 0);
			} else {
				res.put("JZYF", 1);
			}
			parameters.put("TYPE", 3);
			List<Map<String, Object>> yfsbdyList = dao
					.doSqlQuery(
							"select * from BQ_FYYF where BQDM=:BQDM and JGID =:JGID and TYPE=:TYPE",
							parameters);
			if (yfsbdyList.size() < 3) {
				res.put("CYDY", 0);
			} else {
				res.put("CYDY", 1);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取抗菌药物使用数量错误!", e);
		}
	}

	public void doQueryKJYWParams(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnitId();
		int qykjywglvalue = Integer.parseInt(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.QYKJYWGL, ctx));
		int kjysytsvalue = Integer.parseInt(ParameterUtil.getParameter(
				manageUnit, BSPHISSystemArgument.KJYSYTS, ctx));
		res.put("QYKJYWGL", qykjywglvalue);
		res.put("KJYSYTS", kjysytsvalue);
	}

	public void doQueryZY_BRRY(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		try {
			long zyh = Long.parseLong(req.get("ZYH") + "");
			Map<String, Object> zy_brry;
			zy_brry = dao.doLoad(BSPHISEntryNames.ZY_BRRY, zyh);
			res.put("ZY_BRRY", zy_brry);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取抗菌药物使用数量错误!", e);
		}

	}

	public void doQueryclinicManageList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long ZYH = Long.parseLong(req.get("ZYH") + "");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", ZYH);
		try {
			String sql_list = "select a.ZYH as ZYH,a.ZDLB as ZDLB,a.ZXLB as ZXLB,a.ZDXH as ZDXH,a.ZDMC as ZDMC,a.ICD10 as ICD10,"
					+ " a.ZDBW as ZDBW,b.ZHMC as ZHMC,a.ZDYS as ZDYS,to_char(a.ZDSJ,'yyyy-mm-dd hh24:mi:ss') as ZDSJ,a.JGID as JGID,a.ZGQK as ZGQK,"
					+ " a.TXBZ as TXBZ,a.JBPB as JBPB from ZY_RYZD a left join EMR_ZYZH b on a.ZDBW = b.ZHBS "
					+ " where a.ZYH=:ZYH";

			List<Map<String, Object>> inofList = dao.doSqlQuery(sql_list,
					parameters);

			// for (int i = 0; i < inofList.size(); i++) {
			// if (inofList.get(i).get("CFLX") != null) {
			// inofList.get(i).put("ZXLB", inofList.get(i).get("CFLX"));
			// }
			// }
			for (int i = 0; i < inofList.size(); i++) {
				if (inofList.get(i).get("ZHMC") == null) {
					inofList.get(i).put(
							"ZHMC",
							DictionaryController.instance()
									.getDic("phis.dictionary.position")
									.getText(inofList.get(i).get("ZDBW") + ""));
				}
			}
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.war.schemas.ZY_RYZD_BQ");
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载病人诊断信息出错！");
		}
	}
	public void doGetDoctorAdviceStatus(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String sql="select count(1) as COUNT from BQ_TJ02 where YZXH=:YZXH and ZYH=:ZYH" +
				" and YPXH=:YPXH and YPCD=:YPCD and FYBZ=0";
		try {
			Map<String, Object>countmap= dao.doSqlLoad(sql, body);
			if(countmap!=null && countmap.size() >0){
				if(Integer.parseInt(countmap.get("COUNT")+"")>0){
					res.put("flag", true);
				}else{
					res.put("flag", false);
				}
			}else{
				res.put("flag", false);
			}
		} catch (PersistentDataOperationException e) {
			res.put("msg","查询医嘱状态失败！");
			e.printStackTrace();
		}
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
	public Map<String, Object> doLoadOtherYZXX(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Map<String, Object> res = new HashMap<String, Object>();
		String zyh = body.get("zyh")+"";
		Integer yzlx = Integer.parseInt(body.get("yzlx")+"");
		Integer js =  Integer.parseInt(body.get("js")+"");//1医生站 0护士站
		try {
			StringBuffer hql = new StringBuffer();
			String sql ="";
			switch(yzlx){
			case 1:sql="and xmlx=1 and lsyz=0";break;
		    case 2:sql="and xmlx=1 and lsyz=1";break;
		    case 3:sql="and xmlx=2";break;
		    case 4:sql="and xmlx=3";break;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();			
			// 当天(开方日期)、全区、所有科室 该病人的处方
			hql.append("select a.ypxh,a.jlxh,a.ypxh||'_'||a.ypcd as ypbh,c.ypmc,a.ycjl,c.jldw,a.sypc,a.ypyf,e.xmmc,a.kssj," +
					"a.tzsj,a.yzzxsj,a.yzzh,a.lsbz,case when a.zfbz=1 then 1 when a.lsyz=1 then 0 when a.tzsj is not null and a.tzys is not null then 2 " +
					"when a.xmlx=3 then 3 else 0 end as yzlb,i.officecode,i.officename,h.id as kzysbm,h.name as kzys,'' as cfh," +
					"a.ycsl,a.yfdw,0 as purpose,'' as ssbm,0 as meditime,a.bzxx,'' as ds,'' as dsj " +
					"from zy_bqyz a inner join zy_brry b on a.zyh=b.zyh inner join yk_typk c on a.ypxh=c.ypxh " +
					"inner join yk_ypsx d on c.ypsx=d.ypsx left join zy_ypyf e on a.ypyf=e.ypyf " +
					"left join base_user h on h.id=a.ysgh left join sys_office i on i.officecode=a.brks " +
					"left join sys_office j on j.officecode=a.brbq " +
					"where a.ypcd is not null and a.ypcd<>0 and a.zyh=:ZYH "+sql+" and ysbz=:JS");
			parameters.put("ZYH", zyh);
			parameters.put("JS", js);
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					parameters);
			res.put("yzxxs", list);
		} catch (PersistentDataOperationException e) {
			logger.error("fail to load zy_bqyz information by database reason",
					e);
			throw new ModelDataOperationException("合理用药,医嘱信息查找失败！", e);
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
	public Map<String, Object> doLoadOtherBrzd(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		String zyh = body.get("zyh")+"";
		Map<String, Object> res = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = " select zdxh,icd10,zdmc,zdsj from zy_ryzd where zyh=:ZYH";
		parameters.put("ZYH", zyh);
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
}
