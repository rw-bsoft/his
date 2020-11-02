package phis.application.reg.source;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import phis.application.mds.source.MedicineUtils;
import phis.application.pay.source.SelfHelpMachineService;
import phis.application.yb.source.MedicareModel;
import phis.application.yb.source.YBModel;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.schedule.AutoBuildSourceSchedule;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class RegisteredManagementModel{
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(RegisteredManagementModel.class);

	public RegisteredManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 排班科室查询
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQuerySchedulingDepartment(Map<String, Object> req,
											Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			String GHKSSFPB = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.GHKSSFPB, ctx);
			Calendar startc = Calendar.getInstance();
			int GHRQ = startc.get(Calendar.DAY_OF_WEEK);
			int ZBLB = startc.get(Calendar.HOUR_OF_DAY);
			if (req.containsKey("ghrq")) {
				if (req.get("ghrq") != "") {
					Date ghrqDate = sdf.parse(req.get("ghrq") + "");
					startc.setTime(ghrqDate);
					GHRQ = startc.get(Calendar.DAY_OF_WEEK);
				}
			}
			if (ZBLB < 12) {
				ZBLB = 1;
			} else {
				ZBLB = 2;
			}
			if (req.containsKey("zblb")) {
				if (req.get("zblb") != "") {
					ZBLB = Integer.parseInt(req.get("zblb") + "");
				}
			}
			if (!"1".equals(GHKSSFPB)) {
				Map<String, Object> par = new HashMap<String, Object>();
				String sql = "SELECT "
						+ GHRQ
						+ " as GHRQ,"
						+ ZBLB
						+ " as ZBLB,b.MZMC as MZMC,a.KSDM as GHKS,a.KSMC as KSMC,1 as KSPB,a.JGID as JGID,0 as GHXE,0 as YYXE,0 as YGRS,0 as YYRS,0 as JZXH,0 as TGBZ,a.ZLSFXM as ZLSFXM FROM MS_GHKS a,MS_MZLB b WHERE a.MZLB=b.MZLB and a.JGID = :JGID and a.KSDM not in (select GHKS FROM MS_KSPB WHERE JGID = :JGID and GHRQ=:GHRQ and ZBLB=:ZBLB)";
				par.put("JGID", manaUnitId);
				par.put("GHRQ", GHRQ);
				par.put("ZBLB", ZBLB);
				List<Map<String, Object>> inofList = dao.doQuery(
						sql.toString(), par);
				if (inofList.size() > 0) {
					DepartmentSchedulingModel dss = new DepartmentSchedulingModel(
							dao);
					req.put("body", inofList);
					dss.doSaveDepartmentScheduling(req, res, dao, ctx);
				}
			}
			parameters.put("JGID", manaUnitId);
			parameters.put("GHRQ", GHRQ);
			parameters.put("ZBLB", ZBLB);
			parameters.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			StringBuffer hql = new StringBuffer(
					"SELECT b.GHRQ as GHRQ,a.PYDM as PYDM,a.ZJMZ as ZJMZ,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.KSDM as KSDM,a.KSMC as KSMC,a.GHLB as GHLB,a.GHF as GHF,a.ZLF as ZLF,b.GHXE as GHXE,a.YGRS as YGRS,a.ZJMZ as ZJMZ,a.YYRS as YYRS,b.TGBZ as TGBZ,a.JJRGHF as JJRGHF,a.PYDM as PYDM,0 AS Selected,a.ZLSFXM as ZLSFXM  FROM MS_GHKS a,"
							+ "MS_KSPB b  WHERE ( a.KSDM = b.GHKS ) and ( ( a.JGID = :JGID ) AND  ( a.MZLB = :MZLB ) AND  ( b.GHRQ = :GHRQ ) AND  ( b.ZBLB = :ZBLB ) ) ");

			if ("2".equals(req.get("yytag") + "")) {
				hql = new StringBuffer(
						"SELECT b.GHRQ as GHRQ,a.PYDM as PYDM,a.ZJMZ as ZJMZ,a.WBDM as WBDM,a.JXDM as JXDM,a.QTDM as QTDM,a.KSDM as KSDM,a.KSMC as KSMC,a.GHLB as GHLB,a.GHF as GHF,a.ZLF as ZLF,b.GHXE as GHXE,b.YGRS as YGRS,a.ZJMZ as ZJMZ,b.YYRS as YYRS,b.TGBZ as TGBZ,a.JJRGHF as JJRGHF,a.PYDM as PYDM,0 AS Selected,a.ZLSFXM as ZLSFXM  FROM MS_GHKS a,"
								+ "MS_KSPB b  WHERE ( a.KSDM = b.GHKS ) and ( ( a.JGID = :JGID ) AND  ( a.MZLB = :MZLB ) AND  ( b.GHRQ = :GHRQ ) AND  ( b.ZBLB = :ZBLB ) ) ");
			}
			if (cnd != null) {
				ExpressionProcessor exp = new ExpressionProcessor();
				String where = " and " + exp.toString(cnd);
				hql.append(where);
			}
			hql.append(" ORDER BY a.DDXX ASC");
			List<Map<String, Object>> inofList = dao.doQuery(hql.toString(),
					parameters);
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.reg.schemas.MS_GHKS_MANAGE");
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班科室查询失败！");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班科室查询失败！");
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班科室查询失败！");
		}
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
	public void doQuerySchedulingDoctor(Map<String, Object> req,
										Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Object> cnd = (List<Object>) req.get("cnd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			Calendar startc = Calendar.getInstance();
			int ZBLB = startc.get(Calendar.HOUR_OF_DAY);
			if (ZBLB < 12) {
				ZBLB = 1;
			} else {
				ZBLB = 2;
			}
			if (req.containsKey("ghlb")) {
				if (!"".equals(req.get("ghlb"))) {
					ZBLB = Integer.parseInt(req.get("ghlb") + "");
				}
			}
			parameters.put("JGID", manaUnitId);
			parameters.put("ZBLB", ZBLB);
			startc.set(Calendar.HOUR_OF_DAY, 0);
			startc.set(Calendar.MINUTE, 0);
			startc.set(Calendar.SECOND, 0);
			startc.set(Calendar.MILLISECOND, 0);
			Date adt_begin = startc.getTime();
			startc.set(Calendar.HOUR_OF_DAY, 23);
			startc.set(Calendar.MINUTE, 59);
			startc.set(Calendar.SECOND, 59);
			startc.set(Calendar.MILLISECOND, 999);
			Date adt_end = startc.getTime();
			if (req.containsKey("ghsj")) {
				if (req.get("ghsj") != "") {
					adt_begin = sdftime.parse(sdf.format(sdf.parse(req
							.get("ghsj") + ""))
							+ " 00:00:00");
					adt_end = sdftime.parse(sdf.format(sdf.parse(req
							.get("ghsj") + ""))
							+ " 23:59:59");
				}
			}
			parameters.put("adt_begin", adt_begin);
			parameters.put("adt_end", adt_end);
			StringBuffer hql = new StringBuffer(
					"SELECT b.PYCODE as PYCODE,b.PERSONNAME as PERSONNAME,b.EXPERTCOST as EXPERTCOST,b.ISEXPERT as ISEXPERT,case b.ISEXPERT when 0 then '否' when 1 then '是' end as ISEXPERT_text,a.GHXE as GHXE,a.YGRS as YGRS,a.YSDM as YSDM,a.KSDM as KSDM,b.EXPERTCOST as EXPERTCOST,a.TGBZ as TGBZ,a.YYRS as YYRS,b.PRESCRIBERIGHT as PRESCRIBERIGHT,a.ZBLB as ZBLB,a.YYXE as YYXE,0 as Selected  FROM MS_YSPB a,"
							+ "SYS_Personnel b  WHERE a.YSDM = b.PERSONID and a.JGID = :JGID  and a.GZRQ >= :adt_begin and a.GZRQ <= :adt_end AND a.ZBLB = :ZBLB ");
			if (cnd != null) {
				ExpressionProcessor exp = new ExpressionProcessor();
				String where = " and " + exp.toString(cnd);
				hql.append(where);
			}
			hql.append(" ORDER BY b.PERSONNAME");
			List<Map<String, Object>> inofList = dao.doQuery(hql.toString(),
					parameters);
			res.put("body", inofList);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班医生查询失败！");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班医生查询失败！");
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "排班医生查询失败！");
		}
	}

	/**
	 * 挂号结算查询
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void doQueryRegisteredSettlement(Map<String, Object> req,
											Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("MRBZ", "1");
			List<Map<String, Object>> fkfsList = dao
					.doQuery(
							"select FKFS as FKFS,FKJD as FKJD,SRFS as SRFS from GY_FKFS where MRBZ=:MRBZ and SYLX=1",
							parameters);
			if (fkfsList.size() > 0) {
				Map<String, Object> fkfs = fkfsList.get(0);
				ParameterUtil pu = new ParameterUtil();
				String GHF_SFXM = pu.getParameter(ParameterUtil.getTopUnitId(),
						BSPHISSystemArgument.GHF, ctx);
				Map<String, Object> GHF_parameters = new HashMap<String, Object>();
				GHF_parameters.put("SFXM", Long.parseLong(GHF_SFXM));
				GHF_parameters.put("BRXZ",
						Long.parseLong(body.get("BRXZ") + ""));
				List<Map<String, Object>> GHF_zfbl = dao
						.doQuery(
								"SELECT ZFBL as ZFBL FROM GY_ZFBL Where SFXM = :SFXM AND BRXZ = :BRXZ",
								GHF_parameters);
				double ld_zfghf;// 挂号费
				double ld_zfbl;
				if (GHF_zfbl.size() == 0) {
					ld_zfbl = 1;
				} else {
					ld_zfbl = Double.parseDouble(GHF_zfbl.get(0).get("ZFBL")
							+ "");
				}
				if ("".equals(body.get("GHJE") + "")) {
					body.put("GHJE", 0);
				}
				ld_zfghf = Double.parseDouble(body.get("GHJE") + "") * ld_zfbl;
				String ZLF_SFXM = pu.getParameter(ParameterUtil.getTopUnitId(),
						BSPHISSystemArgument.ZLF, ctx);
				Map<String, Object> ZLF_parameters = new HashMap<String, Object>();
				ZLF_parameters.put("SFXM", Long.parseLong(ZLF_SFXM));
				ZLF_parameters.put("BRXZ",
						Long.parseLong(body.get("BRXZ") + ""));
				List<Map<String, Object>> ZLF_zfbl = dao
						.doQuery(
								"SELECT ZFBL as ZFBL FROM GY_ZFBL Where SFXM = :SFXM AND BRXZ = :BRXZ",
								ZLF_parameters);
				double id_zfzlf;// 诊疗费
				if (ZLF_zfbl.size() == 0) {
					ld_zfbl = 1;
				} else {
					ld_zfbl = Double.parseDouble(ZLF_zfbl.get(0).get("ZFBL")
							+ "");
				}
				id_zfzlf = Double.parseDouble(body.get("ZLJE") + "") * ld_zfbl;
				String ZJF_SFXM = pu.getParameter(ParameterUtil.getTopUnitId(),
						BSPHISSystemArgument.ZJF, ctx);
				Map<String, Object> ZJF_parameters = new HashMap<String, Object>();
				ZJF_parameters.put("SFXM", Long.parseLong(ZJF_SFXM));
				ZJF_parameters.put("BRXZ",
						Long.parseLong(body.get("BRXZ") + ""));
				List<Map<String, Object>> ZJF_zfbl = dao
						.doQuery(
								"SELECT ZFBL as ZFBL FROM GY_ZFBL Where SFXM = :SFXM AND BRXZ = :BRXZ",
								ZJF_parameters);
				double id_zfzjf;// 专家费
				if (ZJF_zfbl.size() == 0) {
					ld_zfbl = 1;
				} else {
					ld_zfbl = Double.parseDouble(ZJF_zfbl.get(0).get("ZFBL")
							+ "");
				}
				String zjfy = body.get("ZJFY") + "";
				if ("null".equals(zjfy)) {
					zjfy = "0";
				}
				id_zfzjf = Double.parseDouble(zjfy) * ld_zfbl;
				double zjje = Double.parseDouble(body.get("GHJE") + "")
						+ Double.parseDouble(body.get("ZLJE") + "")
						+ Double.parseDouble(zjfy)
						+ Double.parseDouble(body.get("BLJE") + "");
				double qtys = ld_zfghf + id_zfzlf + id_zfzjf
						+ Double.parseDouble(body.get("BLJE") + "");
				BigDecimal bzjje = new BigDecimal(zjje);
				double zjjeStr = bzjje.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				BigDecimal bqtys = new BigDecimal(qtys);
				double qtysStr = bqtys.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				double zfjeStr = bqtys.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				if (body.containsKey("GRXJZF")) {
					bqtys = new BigDecimal(body.get("GRXJZF") + "");
					zfjeStr = bqtys.setScale(1, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
				}
				if ("2".equals(fkfs.get("FKJD"))) {
					zfjeStr = bqtys.setScale(1, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
				}
				if ("1".equals(fkfs.get("FKJD"))) {
					zfjeStr = bqtys.setScale(0, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
				}
				Map<String, Object> jsxx = new HashMap<String, Object>();
				jsxx.put("FKFS", fkfs.get("FKFS"));
				jsxx.put("ZJJE", zjjeStr);
				jsxx.put("QTYS", qtysStr);
				jsxx.put("ZFJE", zfjeStr);
				if((body.get("BRXZ")+ "").equals("2000") && body.containsKey("njjbyjsxx")){
					Map<String, Object> njjbyjsxx = (Map<String, Object>) body.get("njjbyjsxx");
					qtysStr=Double.parseDouble(njjbyjsxx.get("BCXZZFZE") + "");
				}
				/*************************begin 调用优惠金额计算的公用方法 zhaojian 2019-05-09******************************/
				double yhje = 0;//优惠金额
				//身份证号为空的病人不予优惠
				if(qtysStr>0 && body.containsKey("IDCARD") && !(body.get("IDCARD")+ "").equals("")){
					yhje = new phis.application.ivc.source.ClinicChargesDiscount(dao).GetGhsfDiscount(req, res, ctx, qtysStr,(body.get("IDCARD")+ ""));
					if(yhje > 0){
						jsxx.put("JYGHYH", "true");
					}
				}
				jsxx.put("YHJE", yhje);
				jsxx.put("YHJEINIT", yhje);
				/*************************end 调用优惠金额计算的公用方法 zhaojian 2019-05-09******************************/
				res.put("body", jsxx);
			} else {
				res.put("body", false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
		}
	}

	/**
	 * 挂号信息保存
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRegisteredManagement(Map<String, Object> req,
										   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String userId = user.getUserId() + "";
		Map<String, Object> map_body = (Map<String, Object>) req.get("body");
		Map<String, Object> body = (Map<String, Object>) map_body.get("GHXX");
		Map<String, Object> zjyyData = (Map<String, Object>) map_body.get("ZJYYDATA");
		int yytag = 1;
		long jzxhqj = 1;
		long sbxhqj = 0;
		if (map_body.get("YYTAG") != null) {
			yytag = Integer.parseInt(map_body.get("YYTAG") + "");
		}
		String GHYJ = map_body.get("GHYJ")+"";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sfampm = new SimpleDateFormat("a");
			sdf.set2DigitYearStart(sdf.parse(body.get("GHSJ") + ""));
			DecimalFormat df = new DecimalFormat("#.00");
			body.put("JGID", manaUnitId);
			String GHSJ = body.get("GHSJ") + "";
			if (yytag == 1) {
				body.put("GHSJ", new Date());
			} else {
				body.put("GHSJ", sdf.parse(GHSJ));
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("JGID", manaUnitId);

			Map<String, Object> MS_GHMX = new HashMap<String, Object>();
			double ZJJE = Double.parseDouble(body.get("ZJJE") + "");
			double XJJE = Double.parseDouble(body.get("XJJE") + "");
			double ZFJE = Double.parseDouble(body.get("ZFJE") + "");
			double YZJM = (body.get("YZJM")==null?0:Double.parseDouble(body.get("YZJM")+ ""));
			double YHJE = Double.parseDouble(body.get("YHJE") + "");// 优惠金额 zhaojian 2019-05-10
			//double HBWC = Double.parseDouble(df.format(ZFJE - YZJM - XJJE));
			double HBWC = BSPHISUtil.getDouble(ZFJE - YZJM - XJJE - YHJE, 2);// 优惠金额 zhaojian 2019-05-10
			/**************begin 移动支付 zhaojian 2018-09-11****************/
			double ZPJE = Double.parseDouble(body.get("ZPJE") + "");
			Integer FFFS = Integer.parseInt(body.get("FKFS") + "");
			if (FFFS ==32 || FFFS==33) {//微信或支付宝
				HBWC = Double.parseDouble(df.format(ZFJE - YZJM - ZPJE));
			}
			/**************begin 移动支付 zhaojian 2018-09-11****************/
			parameters.put("BRID", Long.parseLong(body.get("BRID") + ""));
			long czpbl = dao.doCount("MS_GHMX", "BRID=:BRID and JGID=:JGID",parameters);
			parameters.remove("BRID");
			parameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			MS_GHMX.put("YHJE", YHJE);
			MS_GHMX.put("YHJEINIT", Double.parseDouble(body.get("YHJEINIT") + ""));
			MS_GHMX.put("GHCS", 1);
			//MS_GHMX.put("ZPJE", 0d);
			MS_GHMX.put("ZPJE", ZPJE);	//zhaojian 移动支付
			MS_GHMX.put("ZHJE", 0d);
			MS_GHMX.put("HBWC", HBWC);//zhaojian 移动支付
			MS_GHMX.put("THBZ", 0);
			MS_GHMX.put("FFFS", FFFS);//zhaojian 移动支付
			if (czpbl > 0) {
				MS_GHMX.put("CZPB", 0);
			} else {
				MS_GHMX.put("CZPB", 1);
			}
			MS_GHMX.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			MS_GHMX.put("YSPB", 0);
			MS_GHMX.put("SFFS", 0);
			MS_GHMX.put("JZZT", 0);
			MS_GHMX.put("JZJS", 0);
			MS_GHMX.put("CZGH", userId);
			MS_GHMX.put("BRXZ", body.get("BRXZ"));
			String jzhm = BSPHISUtil.getNotesNumberNotIncrement(userId,manaUnitId, 1, dao, ctx);
			MS_GHMX.put("JZHM", jzhm);
			MS_GHMX.put("XJJE", XJJE);
			MS_GHMX.put("BRID", body.get("BRID"));
			MS_GHMX.put("JGID", manaUnitId);
			if (yytag == 1) {
				MS_GHMX.put("GHSJ", new Date());
			} else {
				MS_GHMX.put("YYBZ", 1);
				MS_GHMX.put("GHSJ", sdf.parse(GHSJ));
			}

			if(zjyyData!=null){//add by LIZHI 2017-12-25 紫金数云预约信息挂号时间存预约时间
				if(zjyyData.get("STARTTTIME")!=null && !"".equals(zjyyData.get("STARTTTIME"))){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date appointTime = sf.parse(zjyyData.get("STARTTTIME")+"");
					Date nowDate = new Date();
					if(appointTime.after(nowDate)){
						MS_GHMX.put("GHSJ", appointTime);
						MS_GHMX.put("QHSJ",nowDate);//add by LIZHI 2017-12-28增加取号时的当前时间
						MS_GHMX.put("ZJYYBZ", '1');
					}else if(zjyyData.get("ENDTIME")!=null && !"".equals(zjyyData.get("ENDTIME"))){
						Date endTime = sf.parse(zjyyData.get("ENDTIME")+"");
						if(endTime.after(nowDate)){
							MS_GHMX.put("QHSJ",nowDate);//add by LIZHI 2017-12-28增加取号时的当前时间
							MS_GHMX.put("ZJYYBZ", '1');
						}
					}
				}
				if(zjyyData.get("ID")!=null && !"".equals(zjyyData.get("ID")+"")){
					long yyId = Long.parseLong(zjyyData.get("ID")+"");
					MS_GHMX.put("ZJYYID", yyId);
				}
			}

			String nowdate = sdf.format(new Date());
			if (nowdate.equals(sdf.format(sdf.parse(GHSJ)))) {
				if ("1".equals(body.get("GHLB"))) {
					String ampm = sfampm.format(new Date());
					if ("上午".equals(ampm)) {
						MS_GHMX.put("GHLB", 1);
					} else if ("下午".equals(ampm)) {
						MS_GHMX.put("GHLB", 2);
					}
				} else {
					MS_GHMX.put("GHLB", body.get("GHLB"));
				}
			} else {
				MS_GHMX.put("GHLB", body.get("GHLB"));
			}
			MS_GHMX.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			MS_GHMX.put("GHJE",Double.parseDouble(df.format(Double.parseDouble(body.get("GHJE") + ""))));
			MS_GHMX.put("ZLJE",Double.parseDouble(df.format(Double.parseDouble(body.get("ZLJE") + ""))));
			MS_GHMX.put("BLJE",Double.parseDouble(df.format(Double.parseDouble(body.get("BLJE") + ""))));
			String ZJFY = body.get("ZJFY") + "";
			if ("null".equals(ZJFY)) {
				ZJFY = "0";
			}
			MS_GHMX.put("ZJFY",Double.parseDouble(df.format(Double.parseDouble(ZJFY))));
			if (body.containsKey("YSDM")&& (body.get("YSDM") + "").length() > 0
					&& !"0".equals(body.get("YSDM") + "")) {
				MS_GHMX.put("YSDM", body.get("YSDM"));
				if (!"1".equals(body.get("YYBZ") + "")) {
					Calendar startc = sdf.getCalendar();
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
					if (yytag == 2) {
						ldt_begin = sdf.parse(GHSJ);
						ldt_end = sdf.parse(GHSJ);
					}
					parameters.put("YSDM", body.get("YSDM") + "");
					parameters.put("ldt_begin", ldt_begin);
					parameters.put("ldt_end", ldt_end);
					parameters.put("ZBLB",Integer.parseInt(MS_GHMX.get("GHLB") + ""));
					Map<String, Object> MJZXH = dao.doLoad("SELECT nvl(JZXH,0)+1 as JZXH FROM MS_YSPB WHERE JGID = :JGID AND GZRQ >= :ldt_begin AND GZRQ <= :ldt_end AND KSDM = :KSDM AND YSDM = :YSDM AND ZBLB = :ZBLB",
							parameters);
					if (MJZXH != null) {
						int JZXH = (Integer) MJZXH.get("JZXH");
						jzxhqj = JZXH;
						MS_GHMX.put("JZXH", JZXH);
					} else {
						MS_GHMX.put("JZXH", 1);
						jzxhqj = 1;
					}
				} else {
					MS_GHMX.put("JZXH", body.get("JZXH"));
				}
			} else {
				if (!"1".equals(body.get("YYBZ") + "")) {
					parameters.put("KSDM",Long.parseLong(body.get("KSDM") + ""));
					parameters.put("GHRQ",Integer.parseInt(body.get("GHRQ") + ""));
					parameters.put("ZBLB",Integer.parseInt(MS_GHMX.get("GHLB") + ""));
					Map<String, Object> MJZXH = dao.doLoad("SELECT nvl(JZXH,0)+1 as JZXH FROM MS_KSPB WHERE JGID = :JGID AND GHKS = :KSDM and ZBLB = :ZBLB and GHRQ = :GHRQ",
							parameters);
					if (MJZXH != null) {
						int JZXH = (Integer) MJZXH.get("JZXH");
						jzxhqj = JZXH;
						MS_GHMX.put("JZXH", JZXH);
					} else {
						jzxhqj = 1;
						MS_GHMX.put("JZXH", 1);
					}
					parameters.remove("KSDM");
					parameters.remove("GHRQ");
					parameters.remove("ZBLB");
				} else {
					MS_GHMX.put("JZXH", body.get("JZXH"));
				}
			}
			MS_GHMX.put("CZSJ", new Date());
			MS_GHMX.put("NJJBLSH", (body.get("NJJBLSH")==null?"":(body.get("NJJBLSH")+"")));
			MS_GHMX.put("NJJBYLLB", (body.get("NJJBYLLB")==null?"":(body.get("NJJBYLLB")+"")));
			MS_GHMX.put("YBMC", (body.get("YBMC")==null?"":(body.get("YBMC")+"")));
			/*********zhaojian 2019-04-04-南京金保 begin*********/
			if(map_body.containsKey("njjbmzjsxx")){
				MS_GHMX.put("QTYS", Double.parseDouble(df.format(Double.parseDouble(body.get("JJZF") + "") + Double.parseDouble(body.get("YBZF") + ""))));
				MS_GHMX.put("ZHJE", Double.parseDouble(body.get("ZHZF") + ""));
			} else {
				MS_GHMX.put("ZHJE", 0d);
				MS_GHMX.put("QTYS", 0d);
			}
			/*********zhaojian 2019-04-04-南京金保 end*********/
			/****************add by lizhi at 2017/08/28增加科室挂号排列序号*****************/
			long plxh = 1;
			String ghsjStr = "";
			if (yytag == 1) {
				ghsjStr = sdf.format(new Date());
			} else {
				ghsjStr = GHSJ.substring(0, 10);
			}
			String plxhSql = "select max(PLXH) as PLXH from MS_GHMX where KSDM=:KSDM ";
			if(!"".equals(ghsjStr)){
				plxhSql += " and to_char(GHSJ,'yyyy-MM-dd')=:GHSJ";
			}
			Map<String,Object> param = new HashMap<String, Object>();
			param.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			param.put("GHSJ", ghsjStr);
			Map<String, Object> plxhMap = dao.doLoad(plxhSql, param);
			if(plxhMap!=null && plxhMap.containsKey("PLXH") && plxhMap.get("PLXH")!=null){
				plxh = Long.parseLong(plxhMap.get("PLXH")+"")+1;
			}
			MS_GHMX.put("PLXH", plxh);
			/****************add by lizhi at 2017/08/28增加科室挂号排列序号*****************/
			//yx-2017-11-20-增加义诊-b
			if(body.containsKey("YZBZ") && "1".equals(body.get("YZBZ")+"")){
				MS_GHMX.put("YZBZ","1");
				Double yzjm=0.0d;
				if(body.containsKey("YZJM")){
					yzjm=Double.parseDouble(body.get("YZJM")+"");
					if(yzjm > ZJJE){
						MS_GHMX.put("YZJM",ZJJE);
					}else{
						MS_GHMX.put("YZJM",yzjm);
					}
				}
				else if(XJJE>0){
					yzjm=XJJE;
					MS_GHMX.put("YZJM",XJJE);
					MS_GHMX.put("XJJE",0.0d);
				}
				//MS_GHMX.put("QTYS",Double.parseDouble(df.format(ZJJE - yzjm)));
			}else{
				//MS_GHMX.put("QTYS",Double.parseDouble(df.format(ZJJE - ZFJE)));
				MS_GHMX.put("YZBZ","0");
				MS_GHMX.put("YZJM",0.0d);
			}
			//yx-2017-11-20-增加义诊-e
			
			/**************************************增加医保挂号减免标识****************************************************/
			MS_GHMX.put("JYJMBZ",body.get("JYJMBZ"));
			if("家医减免一元".equals(body.get("JYJMBZ"))){
//				MS_GHMX.put("YHJE", 1.00);//增加之后报表计算实际值有误差，不插入给减免标识即可
//				MS_GHMX.put("YHJEINIT", 1.00);
				res.put("JYGHYH", "true");
			}
			
			Map<String, Object> sbxh = dao.doSave("create",BSPHISEntryNames.MS_GHMX, MS_GHMX, false);
			sbxhqj = Long.parseLong(sbxh.get("SBXH") + "");
			/***************************zhaojian 2019-04-01 南京金保保存报销信息 begin***************************/
			//if(!(body.containsKey("YZBZ") && "1".equals(body.get("YZBZ")+""))){
			if(map_body.containsKey("njjbmzjsxx")){
				Map<String, Object> njjbdata=(Map<String, Object>)map_body.get("njjbmzjsxx");
				njjbdata.put("JGID", manaUnitId);
				njjbdata.put("GHXH", sbxh.get("SBXH"));
				njjbdata.put("JSSJ", new Date());
				njjbdata.put("ZFPB",0);
				try {
					dao.doSave("create", BSPHISEntryNames.NJJB_JSXX, njjbdata, false);
				} catch (ValidateException e) {
					throw new ModelDataOperationException(e.getMessage());
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(e.getMessage());
				}
/*					double BCTCZFJE = Double.parseDouble(njjbdata.get("BCTCZFJE") + "");//统筹支付
					double BCDBJZZF = Double.parseDouble(njjbdata.get("BCDBJZZF") + "");//大病救助
					double BCDBBXZF = Double.parseDouble(njjbdata.get("BCDBBXZF") + "");//大病保险
					double BCMZBZZF = Double.parseDouble(njjbdata.get("BCMZBZZF") + "");//民政补助
					MS_MZXX.put("QTYS",BSPHISUtil.getDouble(BCTCZFJE+BCDBJZZF+BCMZBZZF+BCDBBXZF, 2));*/
			}
			//}
			/***************************zhaojian 2019-04-01 南京金保保存报销信息 end***************************/
			if (HBWC != 0) {
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("HBWC", "1");
				Long FKFS = Long.parseLong(dao.doLoad("select FKFS as FKFS from GY_FKFS where HBWC=:HBWC and SYLX=1",
						parameters2).get("FKFS")+"");
				Map<String, Object> MS_GH_FKXX = new HashMap<String, Object>();
				MS_GH_FKXX.put("SBXH", sbxh.get("SBXH"));
				MS_GH_FKXX.put("FKFS", FKFS);
				MS_GH_FKXX.put("FKJE", HBWC);
				dao.doSave("create", BSPHISEntryNames.MS_GH_FKXX, MS_GH_FKXX,false);
			}
			Map<String, Object> MS_GH_FKXX = new HashMap<String, Object>();
			MS_GH_FKXX.put("SBXH", sbxh.get("SBXH"));
			MS_GH_FKXX.put("FKFS", body.get("FKFS"));
			/**************begin 移动支付 zhaojian 2018-09-17****************/
			if (FFFS ==1) {//现金
				MS_GH_FKXX.put("FKJE", XJJE);
			}
			else{
				MS_GH_FKXX.put("FKJE", ZPJE);
			}
			/**************end 移动支付 zhaojian 2018-09-17****************/
			dao.doSave("create", BSPHISEntryNames.MS_GH_FKXX, MS_GH_FKXX, false);
			if (MS_GHMX.containsKey("YSDM")
					&& (body.get("YSDM") + "").length() > 0
					&& !"0".equals(body.get("YSDM") + "")) {
				if ("1".equals(body.get("YYBZ") + "")) {
					Calendar startc = sdf.getCalendar();
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
					parameters.put("ZBLB",Integer.parseInt(body.get("ZBLB") + ""));
					if (yytag == 2) {
						ldt_begin = sdf.parse(GHSJ);
						ldt_end = sdf.parse(GHSJ);
						parameters.put("ZBLB",Integer.parseInt(body.get("GHLB") + ""));
					}
					parameters.put("YSDM", body.get("YSDM") + "");
					parameters.put("ldt_begin", ldt_begin);
					parameters.put("ldt_end", ldt_end);
					parameters.put("KSDM",Long.parseLong(body.get("KSDM") + ""));
					dao.doUpdate("UPDATE MS_YSPB SET YGRS=nvl(YGRS,0)+1,YYRS=nvl(YYRS,0)-1 WHERE JGID =:JGID AND GZRQ >= :ldt_begin AND GZRQ <= :ldt_end AND KSDM = :KSDM AND YSDM = :YSDM AND ZBLB = :ZBLB AND YYRS>0",
							parameters);
				} else {
					dao.doUpdate("UPDATE MS_YSPB SET YGRS=nvl(YGRS,0)+1,JZXH=nvl(JZXH,0)+1 WHERE JGID =:JGID AND GZRQ >= :ldt_begin AND GZRQ <= :ldt_end AND KSDM = :KSDM AND YSDM = :YSDM AND ZBLB = :ZBLB",
							parameters);
				}
				// dao.doUpdate("update MS_YSPB set YYRS=1,JZXH=1", null);
				parameters.clear();
			}
			parameters.put("JGID", manaUnitId);
			parameters.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
			if ("1".equals(body.get("YYBZ") + "")) {
				dao.doUpdate("UPDATE MS_GHKS SET YGRS=nvl(YGRS,0)+1,YYRS=nvl(YYRS,0)-1" +
						" WHERE JGID=:JGID AND KSDM=:KSDM AND YYRS>0",parameters);
				//modified by gaof 2014-1-3 修正挂号后ms_kspb记录增加错误，增加约束条件日期和上下午判别
				parameters.put("GHRQ", Integer.parseInt(body.get("GHRQ") + ""));
				parameters.put("ZBLB", Integer.parseInt(body.get("ZBLB") + ""));
				dao.doUpdate("UPDATE MS_KSPB SET YGRS = nvl(YGRS,0)+1,JZXH = nvl(JZXH,0)+ 1 WHERE JGID = :JGID AND GHKS = :KSDM  AND GHRQ=:GHRQ AND ZBLB=:ZBLB",
						parameters);
				//modified by gaof 2014-1-3 end
				Map<String, Object> MS_YYGHparameters = new HashMap<String, Object>();
				MS_YYGHparameters.put("YYXH",Long.parseLong(body.get("YYXH") + ""));
				dao.doUpdate("UPDATE MS_YYGH SET GHBZ = 1 WHERE YYXH = :YYXH AND GHBZ = 0",MS_YYGHparameters);
			} else {
				if (yytag == 2) {
					Map<String, Object> yyghMap = new HashMap<String, Object>();
					yyghMap.put("JGID", manaUnitId);
					yyghMap.put("YYMM", "0");
					yyghMap.put("BRID", Long.parseLong(body.get("BRID") + ""));
					yyghMap.put("GHRQ", sdf.parse(sdf.format(new Date())));
					yyghMap.put("KSDM", Long.parseLong(body.get("KSDM") + ""));
					yyghMap.put("ZBLB", Integer.parseInt(body.get("GHLB") + ""));
					if (MS_GHMX.containsKey("YSDM")&& (body.get("YSDM") + "").length() > 0
							&& !"0".equals(body.get("YSDM") + "")) {
						yyghMap.put("YSDM", body.get("YSDM") + "");
					}
					yyghMap.put("YYLB", 1);
					yyghMap.put("GHBZ", 1);
					yyghMap.put("YYRQ", sdf.parse(GHSJ));
					yyghMap.put("JZXH", jzxhqj);
					yyghMap.put("SBXH", sbxhqj);
					yyghMap.put("ZCID", 0l);
					dao.doSave("create", BSPHISEntryNames.MS_YYGH, yyghMap,false);
				} else {
					dao.doUpdate("UPDATE MS_GHKS SET YGRS=nvl(YGRS,0)+1,JZXH=nvl(JZXH,0)+1 " +
							"WHERE JGID=:JGID AND KSDM=:KSDM",parameters);
				}

				// modified by gaof 2014-1-3 修正挂号后ms_kspb记录增加错误，增加约束条件日期和上下午判别
				parameters.put("GHRQ", Integer.parseInt(body.get("GHRQ") + ""));
				if (yytag == 2) {
					parameters.put("ZBLB",Integer.parseInt(body.get("GHLB") + ""));
				} else {
					parameters.put("ZBLB",Integer.parseInt(body.get("ZBLB") + ""));
				}
				dao.doUpdate("UPDATE MS_KSPB SET YGRS = nvl(YGRS,0)+1,JZXH = nvl(JZXH,0)+ 1 WHERE JGID = :JGID AND GHKS = :KSDM  AND GHRQ=:GHRQ AND ZBLB=:ZBLB",
						parameters);
				//modified by gaof 2014-1-3 end
			}
			// 更新就诊号码
			BSPHISUtil.getNotesNumber(userId, manaUnitId, 1, dao, ctx);
			// 排队挂号处理
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("BRID", Long.parseLong(body.get("BRID") + ""));
//			params.put("JZHM", jzhm);
//			params.put("GHKS", Long.parseLong(body.get("KSDM") + ""));
//			params.put("JGID", manaUnitId);
//			params.put("SBXH", sbxh.get("SBXH"));
			// doMzpd(params, ctx);
			// res.(sbxh);
			res.putAll(sbxh);
//			if (map_body.containsKey("YBXX")) {
//				MedicareModel mm = new MedicareModel(dao);
//				mm.saveYbghxx((Map<String, Object>) map_body.get("YBXX"));
//			}
			if((!"false".equals(GHYJ))&&(!"null".equals(GHYJ))){
				Map<String, Object> GHYJ_parameters = new HashMap<String, Object>();
				GHYJ_parameters.put("YJXH", Long.parseLong(GHYJ));
				dao.doUpdate("update MS_GHYJ set GHBZ=1 where YJXH=:YJXH", GHYJ_parameters);
			}
			//以下保存医保挂号信息
			if("3000".equals(body.get("BRXZ")+"")){
				if (map_body.containsKey("YBXX")) {
					parameters.clear();
					parameters.put("JGID", manaUnitId);
					parameters.put("SBXH", sbxhqj);
					parameters.put("BRXZ", Long.parseLong(body.get("BRXZ") + ""));
					long ybgh = dao.doCount("YB_GHXX", "BRXZ=:BRXZ and JGID=:JGID and SBXH=:SBXH",parameters);
					String op = ybgh>0 ? "update" : "create";
					Map<String, Object> ybghxxMap = new HashMap<String, Object>();
					ybghxxMap = (Map<String, Object>) map_body.get("YBXX");
					ybghxxMap.put("JGID", manaUnitId);
					ybghxxMap.put("SBXH", sbxhqj);
					ybghxxMap.put("BRXZ", body.get("BRXZ"));
					ybghxxMap.put("CZGH", sbxh.get("CZGH"));
					ybghxxMap.put("JSSJ", sdf2.parse(sdf2.format(new Date())));
					ybghxxMap.put("JZRQ", sdf.parse(sdf.format(new Date())));
					dao.doSave(op, BSPHISEntryNames.YB_GHXX, ybghxxMap,false);
				}
			}
			//yx-2017-09-27-同步到排队叫号系统-b
			if(ParameterUtil.getParameter(manaUnitId, "PDJH", ctx).equals("1")){
				MS_GHMX.put("BRXM",body.get("BRXM"));
				doPdjh(MS_GHMX,res);
			};
			//yx-2017-09-27-同步到排队叫号系统-e

			if(zjyyData!=null){//add by lizhi 2017-12-25 紫金数云预约信息更新状态：3已就诊
				if(zjyyData.get("ID")!=null && !"".equals(zjyyData.get("ID")+"")){
					long yyId = Long.parseLong(zjyyData.get("ID")+"");
					Map<String,Object> zjyy_parameters = new HashMap<String, Object>();
					zjyy_parameters.put("ID", yyId);
					dao.doUpdate("update APPOINTMENT_RECORD set STATUS = 2 where ID=:ID",
							zjyy_parameters);
				}
			}
		} catch (ValidateException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
		} catch (ParseException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
		}
	}

	/**
	 * 挂号单据查询
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doQueryGhdj(Map<String, Object> req, Map<String, Object> res,
							Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		try {
			if (req.containsKey("YBXX")) {
				Map<String, Object> map_ybxx = (Map<String, Object>) req
						.get("YBXX");
				StringBuffer hql_ybbrxx = new StringBuffer();
				hql_ybbrxx
						.append("select a.MZHM as MZHM from MS_BRDA a,")
						.append("YB_CBRYJBXX b where a.BRID=b.BRID and b.GRBH=:grbh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("grbh", map_ybxx.get("GRBH") + "");
				Map<String, Object> map_ybbrxx = dao.doLoad(
						hql_ybbrxx.toString(), map_par);
				if (map_ybbrxx == null || map_ybbrxx.size() == 0) {
					res.put("x-response-code", 9000);
					res.put("x-response-msg", "未找到该卡对应的挂号信息");
					return;
				}
				req.put("JZKH", map_ybbrxx.get("MZHM") + "");
			}
			if (req.containsKey("JZKH")) {
				String JZKH = req.get("JZKH") + "";
				Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
				MPI_Cardparameters.put("JZKH", JZKH);
				Map<String, Object> MPI_Card = dao
						.doLoad("select empiId as empiId from MPI_Card where cardNo=:JZKH and cardTypeCode="+KLX,
								MPI_Cardparameters);
				if (MPI_Card != null) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("EMPIID", MPI_Card.get("empiId"));
					Map<String, Object> M_BRID = dao
							.doLoad("select BRID as BRID from MS_BRDA where EMPIID=:EMPIID",
									parameters);
					if (M_BRID == null) {
						res.put("count", -1);
						return;
					}
					parameters.clear();
					long BRID = Long.parseLong(M_BRID.get("BRID") + "");
					res.put("BRID", BRID);
					parameters.put("ai_brid", BRID);
					parameters.put("ai_jgid", manaUnitId);
					long count = dao
							.doCount(
									"MS_GHMX",
									"BRID = :ai_brid  and JGID  =:ai_jgid and JZJS = 0 AND THBZ = 0 AND JZZT = 0",
									parameters);
					if (count == 1) {
						String hql = "SELECT a.SBXH as SBXH,a.GHSJ as GHSJ,a.KSDM as KSDM,b.KSMC as GHKS,a.YSDM as YSDM,c.PERSONNAME as GHYS,a.JZHM as JZHM,a.GHJE as GHF,a.ZLJE as ZLF,a.ZJFY as ZJF,a.BLJE as BLF,a.XJJE as XJJE,d.BRXM as BRXM,e.XZMC as BRXZ,a.BRXZ as XZDM FROM "
								+ "MS_GHMX a left join SYS_Personnel c on a.YSDM = c.PERSONID,MS_GHKS b,MS_BRDA d,GY_BRXZ"
								+ " e WHERE a.BRXZ = e.BRXZ and a.BRID = d.BRID and b.KSDM = a.KSDM and a.BRID = :ai_brid  and a.JGID  =:ai_jgid and a.JZJS = 0 AND THBZ = 0 AND a.JZZT = 0";
						List<Map<String, Object>> Ghdj = dao.doSqlQuery(hql,
								parameters);
						res.put("body", Ghdj.get(0));
					}
					res.put("count", count);
				} else {
					res.put("count", -1);
				}
			} else {
				String MZHM = req.get("MZHM") + "";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("MZHM", MZHM);
				Map<String, Object> M_BRID = dao.doLoad(
						"select BRID as BRID from MS_BRDA where MZHM=:MZHM",
						parameters);
				if (M_BRID == null) {
					res.put("count", -1);
					return;
				}
				parameters.clear();
				long BRID = Long.parseLong(M_BRID.get("BRID") + "");
				res.put("BRID", BRID);
				parameters.put("ai_brid", BRID);
				parameters.put("ai_jgid", manaUnitId);
				long count = dao
						.doCount(
								"MS_GHMX",
								"BRID = :ai_brid  and JGID  =:ai_jgid and JZJS = 0 AND THBZ = 0 AND JZZT = 0",
								parameters);
				if (count == 1) {
					if (!req.containsKey("YBXX")) {// 如果没有读卡,判断下是否是医保挂号
						long n = dao
								.doCount(
										"MS_GHMX a,YB_MZJS b ",
										"a.MZXH=b.MZXH and a.BRID = :ai_brid  and a.JGID  =:ai_jgid and a.JZJS = 0 AND a.THBZ = 0 AND a.JZZT = 0",
										parameters);
						if (n > 0) {
							res.put("x-response-code", 9000);
							res.put("x-response-msg", "医保挂号的病人,请先读卡");
							return;
						}
					}
					String hql = "SELECT a.SBXH as SBXH,a.GHSJ as GHSJ,a.KSDM as KSDM,b.KSMC as GHKS,a.YSDM as YSDM,c.PERSONNAME as GHYS,a.JZHM as JZHM,a.GHJE as GHF,a.ZLJE as ZLF,a.ZJFY as ZJF,a.BLJE as BLF,a.XJJE as XJJE,d.BRXM as BRXM,e.XZMC as BRXZ FROM "
							+ "MS_GHMX a left join SYS_Personnel c on a.YSDM = c.PERSONID,"
							+ "MS_GHKS b,MS_BRDA d,GY_BRXZ"
							+ " e WHERE a.BRXZ = e.BRXZ and a.BRID = d.BRID and b.KSDM = a.KSDM and a.BRID = :ai_brid  and a.JGID  =:ai_jgid and a.JZJS = 0 AND THBZ = 0 AND a.JZZT = 0";
					List<Map<String, Object>> Ghdj = dao.doSqlQuery(hql,
							parameters);
					res.put("body", Ghdj.get(0));
				}
				res.put("count", count);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 挂号单据查询
	 *
	 * @param BRID
	 * @param res
	 * @param ctx
	 */
	public void doQueryGhdjs(String BRID, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("ai_brid", Long.parseLong(BRID));
			parameters.put("ai_jgid", manaUnitId);
			String hql = "SELECT a.SBXH as SBXH,a.GHSJ as GHSJ,a.KSDM as KSDM,b.KSMC as GHKS,a.YSDM as YSDM,c.PERSONNAME as GHYS,a.JZHM as JZHM,a.GHJE as GHF,a.ZLJE as ZLF,a.ZJFY as ZJF,a.BLJE as BLF,a.XJJE as XJJE,d.BRXM as BRXM,e.XZMC as BRXZ,a.BRXZ as XZDM from "
					+ "MS_GHMX a left join SYS_Personnel c on a.YSDM = c.PERSONID,MS_GHKS b,MS_BRDA d,"
					+ "GY_BRXZ e WHERE a.BRXZ = e.BRXZ and a.BRID = d.BRID and b.KSDM = a.KSDM and a.BRID = :ai_brid  and a.JGID  =:ai_jgid and a.JZJS = 0 AND THBZ = 0 AND a.JZZT = 0 order by a.SBXH desc";
			List<Map<String, Object>> Ghdj = dao.doSqlQuery(hql, parameters);
			res.put("body", Ghdj);
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 退号
	 *
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// @SuppressWarnings("unchecked")
	public void doSaveRetireRegistered(Map<String, Object> body,
									   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String SBXH = body.get("SBXH") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		String userId = user.getUserId() + "";
		Map<String, Object> MS_YSPB_parameters = new HashMap<String, Object>();
		Map<String, Object> MS_GHMX_parameters = new HashMap<String, Object>();
		Map<String, Object> MS_GHKS_parameters = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar startc = Calendar.getInstance();
		try {
			MS_GHMX_parameters.put("SBXH", Long.parseLong(SBXH));
			dao.doUpdate("update MS_GHMX set THBZ = 1 where SBXH = :SBXH",
					MS_GHMX_parameters);
			Map<String, Object> MS_GHMX = dao.doLoad(BSPHISEntryNames.MS_GHMX,
					Long.parseLong(SBXH));
			startc.setTime(new Date());
			int HOUR = startc.get(Calendar.HOUR_OF_DAY);
			int zblb = 0;
			if (HOUR < 12) {
				zblb = 1;
			} else {
				zblb = 2;
			}
			Map<String, Object> MS_THMX = new HashMap<String, Object>();
			MS_THMX.put("SBXH", Long.parseLong(SBXH));
			MS_THMX.put("JGID", manaUnitId);
			MS_THMX.put("CZGH", userId);
			MS_THMX.put("MZLB", MS_GHMX.get("MZLB"));
			MS_THMX.put("THRQ", new Date());
			dao.doInsert(BSPHISEntryNames.MS_THMX, MS_THMX, false);

			if (sdf.format(new Date()).equals(
					sdf.format((Date) MS_GHMX.get("GHSJ")))
					&& Integer.parseInt(MS_GHMX.get("GHLB") + "") == zblb) {
				MS_GHKS_parameters.put("JGID", manaUnitId);
				MS_GHKS_parameters.put("KSDM",
						Long.parseLong(MS_GHMX.get("KSDM") + ""));
				dao.doUpdate(
						"UPDATE MS_GHKS Set YGRS = YGRS - 1 Where JGID = :JGID and KSDM = :KSDM",
						MS_GHKS_parameters);
			}else if(sdf.format(new Date()).equals(
					sdf.format((Date) MS_GHMX.get("GHSJ"))) && Integer.parseInt(MS_GHMX.get("GHLB")+"") == 2){
				int GHRQ = startc.get(Calendar.DAY_OF_WEEK);
				MS_GHKS_parameters.put("JGID", manaUnitId);
				MS_GHKS_parameters.put("KSDM",
						Long.parseLong(MS_GHMX.get("KSDM") + ""));
				MS_GHKS_parameters.put("GHRQ", GHRQ);
				MS_GHKS_parameters.put("ZBLB", 2);
				dao.doUpdate("UPDATE MS_KSPB SET YGRS = nvl(YGRS,0)-1,JZXH = nvl(JZXH,0)- 1 WHERE JGID = :JGID AND GHKS = :KSDM and GHRQ=:GHRQ and ZBLB=:ZBLB", MS_GHKS_parameters);
			}else if(((Date) MS_GHMX.get("GHSJ")).getTime()>new Date().getTime()){
				startc.setTime((Date) MS_GHMX.get("GHSJ"));
				int GHRQ = startc.get(Calendar.DAY_OF_WEEK);
				MS_GHKS_parameters.put("JGID", manaUnitId);
				MS_GHKS_parameters.put("KSDM",
						Long.parseLong(MS_GHMX.get("KSDM") + ""));
				MS_GHKS_parameters.put("GHRQ", GHRQ);
				MS_GHKS_parameters.put("ZBLB", Integer.parseInt(MS_GHMX.get("GHLB")+""));
				dao.doUpdate("UPDATE MS_KSPB SET YGRS = nvl(YGRS,0)-1,JZXH = nvl(JZXH,0)- 1 WHERE JGID = :JGID AND GHKS = :KSDM and GHRQ=:GHRQ and ZBLB=:ZBLB", MS_GHKS_parameters);
			}
			if (MS_GHMX.containsKey("YSDM")) {
				Date ldt_begin = sdftime.parse(sdf.format(sdf.parse(MS_GHMX
						.get("GHSJ") + ""))
						+ " 00:00:00");
				Date ldt_end = sdftime.parse(sdf.format(sdf.parse(MS_GHMX
						.get("GHSJ") + ""))
						+ " 23:59:59");
				MS_YSPB_parameters.put("JGID", manaUnitId);
				MS_YSPB_parameters.put("ldt_begin", ldt_begin);
				MS_YSPB_parameters.put("ldt_end", ldt_end);
				MS_YSPB_parameters.put("YSDM", MS_GHMX.get("YSDM"));
				MS_YSPB_parameters.put("ZBLB",
						Integer.parseInt(MS_GHMX.get("GHLB") + ""));
				MS_YSPB_parameters.put("KSDM",
						Long.parseLong(MS_GHMX.get("KSDM") + ""));
				dao.doUpdate(
						"UPDATE MS_YSPB SET YGRS = YGRS - 1 WHERE JGID = :JGID AND GZRQ >= :ldt_begin AND GZRQ <= :ldt_end AND KSDM = :KSDM AND YSDM = :YSDM AND ZBLB = :ZBLB",
						MS_YSPB_parameters);
			}
			// 排队系统 退号处理
			// doMzth(Long.parseLong(SBXH), ctx);
			if (body.containsKey("YBXX")) {
				MedicareModel model = new MedicareModel(dao);
				model.saveYbGhth((Map<String, Object>) body.get("YBXX"));
			}
			// else if(body.containsKey("jyqr")){
			// Map<String,Object> jyqr = (Map<String,Object>)body.get("jyqr");
			// MedicareSYBModel msm = new MedicareSYBModel(dao);
			// msm.doSaveSzYbjyqr("update", jyqr, res, ctx);
			// }
			//南京金保作废-金保结算表打标记
			String brxz=body.get("BRXZ")==null?"":body.get("BRXZ")+"";
			if(brxz.equals("2000")){
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("GHXH", SBXH);
				String upsql="update NJJB_JSXX set ZFPB='1' where GHXH=:GHXH";
				dao.doSqlUpdate(upsql, pa);
			}
			//自助机退费处理，调用启航退费接口 zhaojian 2019-05-22
//			if((MS_GHMX.get("FFFS")+"").equals("39") || (MS_GHMX.get("FFFS")+"").equals("40")){
//				new SelfHelpMachineService().doGhRefund(MS_GHMX, dao);				
//			}
			res.put("ms_ghmx", MS_GHMX);
			
			//挂号退号，履约记录减一次病删除相应的履约记录 xiaheng 2020-05-06
			Map<String, Object>parameterMap = new HashMap<String, Object>();
			parameterMap.put("SBXH",Long.parseLong(SBXH));
			String sql = "select SCID, SERVICEITEMSID as TASKCODE from SCM_newservice where SBXH=:SBXH";
			List<Map<String, Object>> listMap = dao.doSqlQuery(sql, parameterMap);		
			if(listMap.size() > 0){
				//履约次数减一
				Map<String, Object> upMap = listMap.get(0);			
	            String hql = new StringBuffer("update ").append(" SCM_INCREASEITEMS ").append(" set SERVICETIMES = SERVICETIMES -1")
	                    .append(" where SCID =:SCID and TASKCODE =:TASKCODE ").toString();
	            upMap.put("SCID", Long.parseLong(upMap.get("SCID").toString()));
	            dao.doUpdate(hql,upMap); 
	            
				//履约记录删除
				dao.doRemove("SBXH", Long.parseLong(SBXH), BSPHISEntryNames.SCM_NewService);
			}

			
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退号失败");
		} catch (ValidateException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退号失败");
		} catch (ParseException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退号失败");
		} catch (ServiceException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退号失败");
		}
	}

	// @SuppressWarnings("unchecked")
	// public void doSaveRetireRegisteredSzyb(Map<String, Object> body,
	// Map<String, Object> res, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String uid = user.getUserId();
	// // String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
	// Map<String, Object> jyqr = (Map<String, Object>) body.get("jyqr");
	// MedicareSYBModel msm = new MedicareSYBModel(dao);
	// msm.doSaveSzYbjyqr("update", jyqr, res, ctx);
	// Map<String, Object> szybzf = (Map<String, Object>) body.get("szybzf");
	// szybzf.put("ZFPB", 1);
	// szybzf.put("ZFRQ", new Date());
	// szybzf.put("ZFGH", uid);
	// msm.saveSzYbghxx("update", szybzf, ctx);
	// }

	/**
	 * 挂号查询
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRegisteredQuery(Map<String, Object> req,
								  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int first = 0;
		if (req.containsKey("pageNo")) {
			first = (Integer) req.get("pageNo") - 1;
		}
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		try {
			// parameters.put("JGID", manaUnitId);
			StringBuffer hql = new StringBuffer(
					"SELECT CASE WHEN b.FFFS=32 THEN b.ZPJE ELSE 0 END AS WXJE,CASE WHEN b.FFFS=33 THEN b.ZPJE ELSE 0 END AS ZFBJE,d.CardNo as JZKH,a.MZHM as MZHM,a.BRXM as BRXM,b.KSDM as KSMC,b.SBXH as SBXH,b.KSDM as KSDM,b.YSDM as YSXM,"
							+ BSPHISUtil.toChar("b.GHSJ",
							"yyyy-mm-dd hh24:mi:ss")
							+ " as GHSJ,b.GHJE as GHF,b.ZLJE as ZLF,b.ZJFY as ZJF,b.BLJE as BLF,b.GHJE as GHJE,b.ZLJE as ZLJE, b.BLJE as BLJE,b.ZJFY as ZJFY,b.THBZ as THBZ,b.XJJE as XJJE,b.ZPJE as ZPJE,b.ZHJE as ZHJE,b.QTYS as QTYS,b.XJJE+b.QTYS+b.HBWC+b.ZPJE as HJJE,b.JZHM as JZHM,b.CZGH as CZGH,c.CZGH as THGH,"
							+ BSPHISUtil.toChar("c.THRQ",
							"yyyy-mm-dd hh24:mi:ss")
							+ " as THRQ,b.BRXZ as BRXZ,b.YZJM as YZJM,b.JYJMBZ as JYJMBZ FROM MS_BRDA a left join MPI_Card d on d.cardTypeCode="+KLX+" and a.EMPIID = d.empiId,");
			hql.append("MS_GHMX b left join MS_THMX c on (b.SBXH = c.SBXH)");
			hql.append(" WHERE b.BRID = a.BRID and b.JGID = '" + manaUnitId
					+ "'");
			StringBuffer countHql = new StringBuffer(
					"select count(*) as NUM FROM MS_BRDA a left join MPI_Card d on d.cardTypeCode="+KLX+" and a.EMPIID = d.empiId,");
			countHql.append("MS_GHMX b left join MS_THMX c on (b.SBXH = c.SBXH)");
			countHql.append(" WHERE b.BRID = a.BRID and b.JGID = '"
					+ manaUnitId + "'");
			if (cnd != null) {
				if (cnd.size() > 0) {
					ExpressionProcessor exp = new ExpressionProcessor();
					String where = " and " + exp.toString(cnd);
					hql.append(where);
					countHql.append(where);
				}
			} else {
				String where = " and "
						+ BSPHISUtil.toChar("b.GHSJ", "yyyy-mm-dd") + " = '"
						+ BSHISUtil.getDate() + "'";
				hql.append(where);
				countHql.append(where);
			}
			hql.append(" ORDER BY b.SBXH desc");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					countHql.toString(), null);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(hql.toString(),
					parameters);
			SchemaUtil.setDictionaryMassageForList(inofList,
					"phis.application.reg.schemas.MS_GHCX");
			Map<String, Object> totalbody=new HashMap<String, Object>();
			totalbody.put("HJJE","0.00");
			totalbody.put("XJJE","0.00");
			totalbody.put("WXJE","0.00");
			totalbody.put("ZFBJE","0.00");
			for(int i=0;i<inofList.size();i++){
				Map<String, Object> one=inofList.get(i);
				if(!(one.get("THBZ")+"").equals("0")){
					continue;
				}
				totalbody.put("HJJE",Double.parseDouble(totalbody.get("HJJE")+"")+Double.parseDouble(one.get("HJJE")+""));
				totalbody.put("XJJE",Double.parseDouble(totalbody.get("XJJE")+"")+Double.parseDouble(one.get("XJJE")+""));
				totalbody.put("WXJE",Double.parseDouble(totalbody.get("WXJE")+"")+Double.parseDouble(one.get("WXJE")+""));
				totalbody.put("ZFBJE",Double.parseDouble(totalbody.get("ZFBJE")+"")+Double.parseDouble(one.get("ZFBJE")+""));
			}
			totalbody.put("HJJE",BSPHISUtil.getDouble(totalbody.get("HJJE")+"",2));
			totalbody.put("XJJE",BSPHISUtil.getDouble(totalbody.get("XJJE")+"",2));
			totalbody.put("WXJE",BSPHISUtil.getDouble(totalbody.get("WXJE")+"",2));
			totalbody.put("ZFBJE",BSPHISUtil.getDouble(totalbody.get("ZFBJE")+"",2));
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
			res.put("totalbody", totalbody);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号查询失败！");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号查询失败！");
		}
	}

	/**
	 * 获取就诊号码 班次转换
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("static-access")
	public void doUpdateDoctorNumbers(Map<String, Object> req,
									  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnit().getId();
			String userId = user.getUserId() + "";
			BSPHISUtil rmm = new BSPHISUtil();
			String JZHM;
			JZHM = rmm.getNotesNumberNotIncrement(userId, manaUnitId, 1, dao,
					ctx);
			Calendar startc = Calendar.getInstance();
			int ZBLB = startc.get(Calendar.HOUR_OF_DAY);
			if (ZBLB < 12) {
				ZBLB = 1;
			} else {
				ZBLB = 2;
			}
			String GHSJ = BSHISUtil.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dnow = sdf.parse(GHSJ);
			sdf.set2DigitYearStart(dnow);
			Calendar now = sdf.getCalendar();
			Map<String, Object> DQGHRQparameters = new HashMap<String, Object>();
			DQGHRQparameters.put("JGID", manaUnitId);
			DQGHRQparameters.put("CSMC", BSPHISSystemArgument.DQGHRQ);
			Map<String, Object> DQZBLBparameters = new HashMap<String, Object>();
			DQZBLBparameters.put("JGID", manaUnitId);
			DQZBLBparameters.put("CSMC", BSPHISSystemArgument.DQZBLB);
			Map<String, Object> MS_YYGHparameters = new HashMap<String, Object>();
			MS_YYGHparameters.put("JGID", manaUnitId);
			MS_YYGHparameters.put("YYRQ", dnow);
			Map<String, Object> MS_GHKSparameters = new HashMap<String, Object>();
			MS_GHKSparameters.put("JGID", manaUnitId);
			List<Map<String, Object>> DQGHRQ = dao
					.doQuery(
							"select CSZ as CSZ from GY_XTCS where JGID=:JGID and CSMC=:CSMC",
							DQGHRQparameters);
			List<Map<String, Object>> DQZBLB = dao
					.doQuery(
							"select CSZ as CSZ from GY_XTCS where JGID=:JGID and CSMC=:CSMC",
							DQZBLBparameters);
			if (DQGHRQ.size() > 0 && DQZBLB.size() > 0) {
				if (!GHSJ.equals(DQGHRQ.get(0).get("CSZ"))
						|| !(ZBLB + "").equals(DQZBLB.get(0).get("CSZ"))) {
					DQZBLBparameters.put("CSZ", ZBLB + "");
					DQGHRQparameters.put("CSZ", GHSJ);
					dao.doUpdate(
							"UPDATE GY_XTCS set CSZ = :CSZ where JGID=:JGID and CSMC=:CSMC",
							DQZBLBparameters);
					dao.doUpdate(
							"UPDATE GY_XTCS set CSZ = :CSZ where JGID=:JGID and CSMC=:CSMC",
							DQGHRQparameters);
					dao.doUpdate(
							"UPDATE MS_GHKS set YGRS = 0,YYRS = 0 where JGID=:JGID",
							MS_GHKSparameters);
					dao.doUpdate(
							"UPDATE MS_YYGH set GHBZ = 2 Where JGID = :JGID and YYRQ < :YYRQ and GHBZ = 0",
							MS_YYGHparameters);// 将过期的预约挂号置标志2
					List<Map<String, Object>> KSDMS = dao
							.doQuery(
									"select KSDM as KSDM from MS_GHKS where JGID = :JGID",
									MS_GHKSparameters);
					for (int i = 0; i < KSDMS.size(); i++) {
						Map<String, Object> ll_ygrsparameters = new HashMap<String, Object>();
						ll_ygrsparameters.put("JGID", manaUnitId);
						ll_ygrsparameters.put("KSDM",
								Long.parseLong(KSDMS.get(i).get("KSDM") + ""));
						ll_ygrsparameters.put("YYRQ", dnow);
						ll_ygrsparameters.put("ZBLB", ZBLB);
						Map<String, Object> MS_GHKSparameters2 = new HashMap<String, Object>();
						long ll_ygrs = dao
								.doCount(
										"MS_YYGH",
										"JGID = :JGID and KSDM = :KSDM and YYRQ = :YYRQ and ZBLB = :ZBLB and (GHBZ = 3 or GHBZ = 1)",
										ll_ygrsparameters);
						long ll_yyrs = dao
								.doCount(
										"MS_YYGH",
										"JGID = :JGID and KSDM = :KSDM and YYRQ = :YYRQ and ZBLB = :ZBLB and GHBZ = 0",
										ll_ygrsparameters);
						MS_GHKSparameters2.put("JGID", manaUnitId);
						MS_GHKSparameters2.put("KSDM",
								Long.parseLong(KSDMS.get(i).get("KSDM") + ""));
						MS_GHKSparameters2.put("ll_ygrs", (int) ll_ygrs);
						MS_GHKSparameters2.put("ll_yyrs", (int) ll_yyrs);
						dao.doUpdate(
								"UPDATE MS_GHKS set YGRS = :ll_ygrs,YYRS = :ll_yyrs Where JGID = :JGID and KSDM = :KSDM",
								MS_GHKSparameters2);
					}
					Map<String, Object> MS_KSPBparameters = new HashMap<String, Object>();
					MS_KSPBparameters
							.put("GHRQ", now.get(Calendar.DAY_OF_WEEK));
					MS_KSPBparameters.put("JGID", manaUnitId);
					MS_KSPBparameters.put("ZBLB", ZBLB);
					List<Map<String, Object>> MS_KSPBS = dao
							.doQuery(
									"SELECT GHKS as GHKS,JZXH as JZXH FROM MS_KSPB WHERE GHRQ = :GHRQ and ZBLB = :ZBLB and JGID = :JGID",
									MS_KSPBparameters);
					for (int i = 0; i < MS_KSPBS.size(); i++) {
						Long ls_ksdm = Long.parseLong(MS_KSPBS.get(i).get(
								"GHKS")
								+ "");
						int ll_jzxh = Integer.parseInt(MS_KSPBS.get(i).get(
								"JZXH")
								+ "");
						Map<String, Object> MS_GHKSparameters3 = new HashMap<String, Object>();
						MS_GHKSparameters3.put("KSDM", ls_ksdm);
						MS_GHKSparameters3.put("JZXH", ll_jzxh);
						MS_GHKSparameters3.put("JGID", manaUnitId);
						dao.doUpdate(
								"UPDATE MS_GHKS SET JZXH = :JZXH Where JGID = :JGID and KSDM = :KSDM",
								MS_GHKSparameters3);
					}
					dao.doUpdate(
							"UPDATE MS_KSPB SET JZXH = 0,YGRS = 0,YYRS = 0 Where JGID = :JGID and GHRQ = :GHRQ and ZBLB = :ZBLB",
							MS_KSPBparameters);// 清当前班次的科室排班中的就诊序号,已挂人数和预约人数
					Map<String, Object> MS_GHMXparameters = new HashMap<String, Object>();
					MS_GHMXparameters.put("JGID", manaUnitId);
					MS_GHMXparameters.put("GHSJ", dnow);
					dao.doUpdate(
							"UPDATE MS_GHMX SET YYBZ = 0 Where JGID = :JGID AND GHSJ = :GHSJ AND YYBZ = 1",
							MS_GHMXparameters);// 当前班次的收费挂号预约到当前班次就不再是预约了,而变成了挂号
				}
			} else {
				if (DQGHRQ.size() == 0) {
					Map<String, Object> GY_XTCS = new HashMap<String, Object>();
					GY_XTCS.put("JGID", manaUnitId);
					GY_XTCS.put("CSMC", BSPHISSystemArgument.DQGHRQ);
					GY_XTCS.put("CSZ", GHSJ);
					dao.doSave("create", BSPHISEntryNames.GY_XTCS, GY_XTCS,
							false);
				}
				if (DQZBLB.size() == 0) {
					Map<String, Object> GY_XTCS = new HashMap<String, Object>();
					GY_XTCS.put("JGID", manaUnitId);
					GY_XTCS.put("CSMC", BSPHISSystemArgument.DQZBLB);
					GY_XTCS.put("CSZ", ZBLB + "");
					dao.doSave("create", BSPHISEntryNames.GY_XTCS, GY_XTCS,
							false);
				}
			}
			String BLJE = ParameterUtil.getParameter(manaUnitId, "BLF", ctx);
			res.put("JZHM", JZHM);
			res.put("ZBLB", ZBLB + "");
			res.put("GHSJ", GHSJ);
			res.put("BLJE", BLJE);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号初始化失败！");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号初始化失败！");
		} catch (ParseException e1) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号初始化失败！");
		}
	}

	/**
	 * 根据卡号查询可以转科的挂号单
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryTurnDept(Map<String, Object> req,
								Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("CARDNO", String.valueOf(req.get("body")));
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			int ghxq = Integer.parseInt(ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.GHXQ, ctx));
			String xqjsfs = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.XQJSFS, ctx);
			String hql = "SELECT b.BRID as BRID , b.BRXM as BRXM FROM MPI_Card a , MS_BRDA b ";
			hql += " where a.CARDNO =:CARDNO and a.EMPIID = b.EMPIID ";
			List<Map<String, Object>> brList = dao
					.doSqlQuery(hql, parameterMap);
			if (brList == null || brList.size() == 0) {
				return;
			}
			Map<String, Object> M_BRID = brList.get(0);

			parameterMap.clear();
			/*
			 * 　Adt_begin、Adt_end：通过参数ghxq计算得到对应的日期，XQJSFS计算得到集体的时间， 例如：ghxq是n时，
			 * 当XQJSFS为0（精确到时分），Adt_begin=（当前日期-n）的日期 + 当前时间，Adt_end = 当前日期时间,
			 * 当XQJSFS为1（23:59:59），Adt_begin=（当前日期-n+1）的日期 + 0时0分0秒，Adt_end =
			 * 当前日期时间
			 */
			String adt_begin = "", adt_end = "", format = "", dbformat = "";
			if ("0".equals(xqjsfs)) {
				format = "yyyy-MM-dd HH:mm";
				dbformat = "yyyy-MM-dd HH24:mi";
				adt_begin = BSHISUtil.toString(
						BSHISUtil.getDateBefore(new Date(), ghxq), format);
				adt_end = BSHISUtil.toString(new Date(), format);
				parameterMap.put("adt_begin", adt_begin);
				parameterMap.put("adt_end", adt_end);
			} else if ("1".equals(xqjsfs)) {
				format = "yyyy-MM-dd HH:mm:ss";
				dbformat = "yyyy-MM-dd HH24:mi:ss";
				adt_begin = BSHISUtil.toString(
						BSHISUtil.getDateBefore(new Date(), (ghxq - 1)),
						"yyyy-MM-dd");
				adt_begin = adt_begin + " 00:00:00";
				adt_end = BSHISUtil.toString(new Date(), format);
				parameterMap.put("adt_begin", adt_begin);
				parameterMap.put("adt_end", adt_end);
			}
			long BRID = Long.parseLong(M_BRID.get("BRID") + "");
			String brxm = String.valueOf(M_BRID.get("BRXM"));
			parameterMap.put("al_brid", BRID);
			parameterMap.put("al_jgid", jgid);
			// res.put("BRID", BRID);
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select tmp.* , c.PERSONNAME as YGXM from (");
			sqlBuilder
					.append("SELECT a.JZHM as JZHM,a.KSDM as KSDM,b.KSMC as GHKS,a.YSDM as YSDM, ")
					.append(BSPHISUtil
							.toChar("a.GHSJ", "yyyy-MM-dd HH24:mi:ss"));
			sqlBuilder
					.append(" as GHSJ,a.JZZT as JZZT,a.GHJE as GHF,a.ZLJE as ZLF,a.sbxh as sbxh , a.zjfy as ZJFY from ");
			sqlBuilder
					.append("MS_GHMX a ,MS_GHKS  b WHERE a.KSDM = b.KSDM and a.BRID = :al_brid and a.jgid = :al_jgid and a.thbz = 0 and ");
			sqlBuilder.append(BSPHISUtil.toChar("a.ghsj", dbformat))
					.append("  >= :adt_begin and ")
					.append(BSPHISUtil.toChar("a.ghsj", dbformat))
					.append(" <= :adt_end");
			sqlBuilder
					.append(" )  tmp left join SYS_Personnel c on  tmp.YSDM = c.PERSONID");

			List<Map<String, Object>> list = dao.doSqlQuery(
					sqlBuilder.toString(), parameterMap);
			String jzzt = "";
			for (Map<String, Object> map : list) {
				jzzt = String.valueOf(map.get("JZZT"));
				map.put("BRXM", brxm);
				map.put("BRID", BRID);
				map.put("JZZT_TEXT", jzzt.equals("0") ? "挂号"
						: "1".equals(jzzt) ? "就诊中" : "1".equals(jzzt) ? "暂挂"
						: "就诊结束");
			}
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "根据卡号查询可以转科的挂号单失败！");
		}
	}

	/**
	 * 查询科室收费
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doCheckKSFY(Map<String, Object> req, Map<String, Object> res,
							Context ctx) throws ModelDataOperationException {

		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("JGID", jgid);
			parameterMap.put("KSDM",
					Long.parseLong(String.valueOf(req.get("body"))));
			String sql = "SELECT KSMC as KSMC ,GHF as GHF,ZLF as ZLF,YGRS as YGRS  From MS_GHKS Where JGID = :JGID And KSDM = :KSDM";
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameterMap);
			if (list != null && list.size() > 0) {
				res.put("ghf", list.get(0).get("GHF"));
				res.put("zlf", list.get(0).get("ZLF"));
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询科室收费失败！");
		}

	}

	@SuppressWarnings("unchecked")
	public void doCommitTurnDept(Map<String, Object> req,
								 Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		/*
		 * Map<String, Object> body = (Map<String, Object>) req.get("body");
		 * UserRoleToken user = UserRoleToken.getCurrent(); String jgid =
		 * user.getManageUnitId(); try { SimpleDateFormat sdf = new
		 * SimpleDateFormat("HH24"); String ghsj =
		 * String.valueOf(body.get("GHSJ")); Date ghDate =
		 * BSHISUtil.toDate(ghsj); Date nowDate = new Date(); int d =
		 * BSHISUtil.getPeriod(ghDate, nowDate); int ghH =
		 * Integer.parseInt(sdf.format(ghDate)); int nowH =
		 * Integer.parseInt(sdf.format(nowDate)); StringBuilder sqlBuilder = new
		 * StringBuilder(); Map<String, Object> parameterMap = new
		 * HashMap<String, Object>(); if (d == 0 && ((nowH < 12 && ghH < 12) ||
		 * (nowH >= 12 && ghH >= 12))) { parameterMap.put("ls_xgks",
		 * Long.parseLong(String.valueOf(body.get("XGKS")))); sqlBuilder
		 * .append("UPDATE MS_GHKS Set YGRS = YGRS - 1 Where KSDM = :ls_xgks");
		 * dao.doSqlUpdate(sqlBuilder.toString(), parameterMap); } Map<String,
		 * Object> MS_GHMX = dao.doLoad(BSPHISEntryNames.MS_GHMX,
		 * Long.parseLong(String.valueOf(body.get("SBXH")))); if
		 * (MS_GHMX.get("YSDM") != null) { // JGID = :JGID AND GZRQ >=
		 * :ldt_begin AND GZRQ <= :ldt_end AND // KSDM = :KSDM AND YSDM = :YSDM
		 * AND ZBLB = :ZBLB Map<String, Object> parameters = new HashMap<String,
		 * Object>(); parameters.put("KSDM",
		 * Long.parseLong(String.valueOf(body.get("XGKS"))));
		 * parameters.put("YSDM", String.valueOf(MS_GHMX.get("YSDM"))); Calendar
		 * startc = Calendar.getInstance(); startc.setTime((Date)
		 * MS_GHMX.get("GHSJ")); String month = (startc.get(Calendar.MONTH) > 8)
		 * ? ((startc .get(Calendar.MONTH) + 1) + "") : ("0" + (startc
		 * .get(Calendar.MONTH) + 1)); String day =
		 * (startc.get(Calendar.DAY_OF_MONTH) > 9) ? (startc
		 * .get(Calendar.DAY_OF_MONTH) + "") : ("0" + startc
		 * .get(Calendar.DAY_OF_MONTH)); String GHRQ = startc.get(Calendar.YEAR)
		 * + "-" + month + "-" + day; int ZBLB =
		 * startc.get(Calendar.HOUR_OF_DAY); if (ZBLB < 12) { ZBLB = 1; } else {
		 * ZBLB = 2; } parameters.put("GZRQ", GHRQ); parameters.put("ZBLB",
		 * ZBLB); parameters.put("JGID", jgid); dao.doSqlUpdate(
		 * "update MS_YSPB set YGRS = YGRS - 1 where to_char(GZRQ,'yyyy-mm-dd') = :GZRQ and KSDM = :KSDM and YSDM = :YSDM and ZBLB = :ZBLB and JGID = :JGID"
		 * , parameters); } sqlBuilder = new StringBuilder();
		 * parameterMap.clear(); parameterMap.put("Ll_ksdm",
		 * Long.parseLong(String.valueOf(body.get("KSDM")))); sqlBuilder
		 * .append(
		 * "UPDATE MS_GHKS Set YGRS = YGRS + 1,JZXH = JZXH + 1 Where KSDM =:Ll_ksdm"
		 * ); dao.doSqlUpdate(sqlBuilder.toString(), parameterMap);
		 * 
		 * sqlBuilder = new StringBuilder(); sqlBuilder
		 * .append("SELECT JZXH as JZXH FROM MS_GHKS Where KSDM = :Ll_ksdm");
		 * Map<String, Object> map = dao.doLoad(sqlBuilder.toString(),
		 * parameterMap);
		 * 
		 * sqlBuilder = new StringBuilder(); parameterMap.put("ll_jzxh",
		 * map.get("JZXH")); parameterMap.put("ll_sbxh",
		 * Long.parseLong(String.valueOf(body.get("SBXH")))); sqlBuilder
		 * .append(
		 * "UPDATE MS_GHMX Set KSDM = :Ll_ksdm ,JZXH = :ll_jzxh, YSDM = null Where SBXH = :ll_sbxh"
		 * ); dao.doSqlUpdate(sqlBuilder.toString(), parameterMap); } catch
		 * (PersistentDataOperationException e) { throw new
		 * ModelDataOperationException("转科失败!", e); }
		 */
		// update by caijy for 张伟2要求转科先退号再挂号.详见2.4.1缺陷766
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select a.JGID as JGID,a.BRID as BRID,a.BRXZ as BRXZ,a.GHSJ as GHSJ,a.GHLB as GHLB,a.JZYS as JZYS")
				.append(",a.JZXH as JZXH,a.GHCS as GHCS,a.GHJE as GHJE,a.ZLJE as ZLJE,a.ZJFY as ZJFY,a.BLJE as BLJE,a.XJJE as XJJE,a.ZPJE as ZPJE,a.ZHJE as ZHJE")
				.append(",a.HBWC as HBWC,a.QTYS as QTYS,a.ZHLB as ZHLB,a.JZJS as JZJS,a.ZDJG as ZDJG,a.THBZ as THBZ,a.CZGH as CZGH,a.JZRQ as JZRQ,a.HZRQ as HZRQ")
				.append(",a.CZPB as CZPB,a.JZHM as JZHM,a.MZLB as MZLB,a.YYBZ as YYBZ,a.YSPB as YSPB,a.DZSB as DZSB,a.SFFS as SFFS,a.JZZT as JZZT,a.YDGH as YDGH")
				.append(",a.ZDXH as ZDXH,b.FKFS as FKFS from MS_GHMX a,MS_GH_FKXX b where a.SBXH=:sbxh and a.SBXH=b.SBXH");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sbxh", MedicineUtils.parseLong(body.get("SBXH")));
		try {
			Map<String, Object> map_ghmx_old = dao.doLoad(hql.toString(),
					map_par);
			map_ghmx_old.put("GHSJ", body.get("GHSJ"));
			map_ghmx_old.put("KSDM", body.get("KSDM"));
			map_ghmx_old.put(
					"ZJJE",
					MedicineUtils.parseDouble(map_ghmx_old.get("HBWC"))
							+ MedicineUtils.parseDouble(map_ghmx_old
							.get("XJJE"))
							+ MedicineUtils.parseDouble(map_ghmx_old
							.get("QTYS")));
			map_ghmx_old.put(
					"ZFJE",
					MedicineUtils.parseDouble(map_ghmx_old.get("HBWC"))
							+ MedicineUtils.parseDouble(map_ghmx_old
							.get("XJJE")));
			map_ghmx_old.put("ZBLB", body.get("ZBLB"));
			map_ghmx_old.put("GHRQ", body.get("GHRQ"));
			map_ghmx_old.put("YYBZ", 0);
			Map<String, Object> b = new HashMap<String, Object>();
			b.put("GHXX", map_ghmx_old);
			doSaveRetireRegistered(body, res, ctx);// 退号
			req.put("body", b);
			doSaveRegisteredManagement(req, res, ctx);// 挂号
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("转科失败!", e);
		}

	}

	/**
	 * 初始化排队信息
	 *
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// public void doUpdateQueue(Context ctx) throws ModelDataOperationException
	// {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String jgid = user.getManageUnit().getId();
	// String qymzpd = ParameterUtil.getParameter(jgid,
	// BSPHISSystemArgument.QYMZPD, ctx);
	// if (qymzpd != null && qymzpd.equals("1")) {
	// try {
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// SimpleDateFormat matter = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm:ss");
	// Date dayBegin = matter.parse(BSHISUtil.getDate() + " 00:00:00");
	// parameters.put("idt_today", dayBegin);
	// parameters.put("gl_jgid", jgid);
	// dao.doSqlUpdate(
	// "update PD_KSXH set PDXH = 1,GZRQ = :idt_today where GZRQ < :idt_today and JGID = :gl_jgid",
	// parameters);
	// dao.doSqlUpdate(
	// "delete from PD_JZLB where GHRQ < :idt_today and JGID = :gl_jgid",
	// parameters);
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("初始化门诊排队信息错误!", e);
	// } catch (ParseException e) {
	// throw new ModelDataOperationException("初始化门诊排队信息错误!", e);
	// }
	// }
	//
	// }

	/**
	 * 门诊挂号排队处理
	 *
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// public void doMzpd(Map<String, Object> params, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String jgid = user.getManageUnit().getId();
	// String qymzpd = ParameterUtil.getParameter(jgid,
	// BSPHISSystemArgument.QYMZPD, ctx);
	// if (qymzpd != null && qymzpd.equals("1")) {
	// try {
	// Map<String, Object> p = new HashMap<String, Object>();
	// p.put("GHKS", params.get("GHKS"));
	// p.put("JGID", params.get("JGID"));
	// SimpleDateFormat matter = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm:ss");
	// Date dayBegin = matter.parse(BSHISUtil.getDate() + " 00:00:00");
	// p.put("GZRQ", dayBegin);
	// int pdxh = getPdxh(p);
	// if (pdxh <= 0) {
	// // throw new ModelDataOperationException("获取门诊排队序号失败.");
	// } else {
	// params.put("PDXH", pdxh);
	// params.put("GHRQ", new Date());
	// dao.doSqlUpdate(
	// "insert into PD_JZLB (SBXH,JGID,GHKS,PDXH, BRID, JZHM, GHRQ, JZBZ, ZFPB) VALUES(:SBXH,:JGID,:GHKS,:PDXH,:BRID,:JZHM,:GHRQ, 0, 0)",
	// params);
	// }
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("保存门诊排队信息失败.", e);
	// } catch (ParseException e) {
	// throw new ModelDataOperationException("保存门诊排队信息失败.", e);
	// }
	// }
	// }

	/**
	 * 门诊排队退号
	 *
	 * @param params
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// public void doMzth(Object sbxh, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String jgid = user.getManageUnit().getId();
	// String qymzpd = ParameterUtil.getParameter(jgid,
	// BSPHISSystemArgument.QYMZPD, ctx);
	// if (qymzpd != null && qymzpd.equals("1")) {
	// try {
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put("ldt_today", new Date());
	// parameters.put("as_sbxh", sbxh);
	// parameters.put("gl_jgid", jgid);
	// dao.doSqlUpdate(
	// "update PD_JZLB set JZBZ = 1,ZFPB = 2,ZFRQ = :ldt_today where SBXH = :as_sbxh and JGID = :gl_jgid",
	// parameters);
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("保存门诊排队信息失败.", e);
	// }
	// }
	// }

	/**
	 * 获取门诊排队序号
	 *
	 * @param params
	 * @throws ModelDataOperationException
	 */
	public int getPdxh(Map<String, Object> params)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> list = dao
					.doSqlQuery(
							"select PDXH as PDXH from PD_KSXH where GHKS = :GHKS and GZRQ = :GZRQ  and JGID = :JGID",
							params);
			if (list == null || list.size() == 0) {
				return -1;
			}
			Integer pdxh = Integer.parseInt(list.get(0).get("PDXH").toString());
			if (pdxh == null || pdxh <= 0) {
				return -1;
			}
			dao.doSqlUpdate(
					"update PD_KSXH set PDXH=PDXH+1 where GHKS = :GHKS and GZRQ = :GZRQ  and JGID = :JGID",
					params);
			return pdxh;
		} catch (PersistentDataOperationException e) {
			Log.error("获取门诊排队序号失败.", e);
			return -1;
		}
	}

	public void doPrintMoth(Map<String, Object> request,
							Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		response.put("title", jgname + "挂号");
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String sbxh = request.get("sbxh") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		parameters.put("SBXH", Long.parseLong(sbxh));
		String sql = "select brda.BRXZ as BRXZ,mzlb.MZMC as MZMC,ghks.KSMC as KSMC,ghmx.YSDM as YSDM,ghmx.CZGH as CZGH,"
				+ "brda.MZHM as MZHM, ghmx.JZHM as JZHM, brda.BRXM as BRXM, brda.BRXB as BRXB,ghmx.BLJE as BLJE,ghmx.ZJFY as ZJFY,"
				+ "ghmx.GHJE as GHJE,ghmx.ZLJE as ZLJE,ghmx.JZXH as JZXH,ghmx.GHSJ as GHSJ,ghmx.GHLB as GHLB,"
				+ "ghks.GHXE as GHXE,ghks.YGRS as YGRS,ghks.YYRS as YYRS,ghmx.BLJE as BLJE "// 挂号限额,已挂人数,预约人数
				+ "from MS_BRDA brda,MS_GHKS ghks,MS_GHMX ghmx,MS_MZLB mzlb where mzlb.MZLB=ghmx.MZLB and ghmx.KSDM=ghks.KSDM  and ghmx.BRID=brda.BRID and ghmx.JGID=:JGID  and ghmx.SBXH=:SBXH";
		try {
			Map<String, Object> res = dao.doLoad(sql, parameters);
			if (res != null) {
				String brxb = DictionaryController.instance()
						.getDic("phis.dictionary.gender")
						.getText(res.get("BRXB") + "");
				String ysxm = DictionaryController.instance()
						.getDic("phis.dictionary.doctor")
						.getText(res.get("CZGH") + "");
				response.put("mzhm", res.get("MZHM"));
				response.put("jzhm", res.get("JZHM"));
				response.put("xm", res.get("BRXM"));
				response.put("xb", brxb);
				if (Double.parseDouble(res.get("GHJE") + "") > 0) {
					response.put("ghf", "挂号费：" + res.get("GHJE") + "");
				}
				if (Double.parseDouble(res.get("ZLJE") + "") > 0) {
					response.put("zlf", "诊疗费：" + res.get("ZLJE") + "");
				}
				if (Double.parseDouble(res.get("BLJE") + "") > 0) {
					response.put("blf", "病历费：" + res.get("BLJE") + "");
				}
				if (Double.parseDouble(res.get("ZJFY") + "") > 0) {
					response.put("zjf", "专家费：" + res.get("ZJFY") + "");
				}
				response.put("mzmc", res.get("MZMC"));
				// 上午/下午
				String ghlbstr = res.get("GHLB") + "";
				if ("1".equals(ghlbstr)) {
					response.put("ampm", "上午");
				} else if ("2".equals(ghlbstr)) {
					response.put("ampm", "下午");
				}
				response.put("rq", sfm.format(res.get("GHSJ")));
				response.put("ghks", res.get("KSMC"));
				response.put("ysxm", ysxm);
				response.put("ghxe", res.get("GHXE"));
				// 已挂人数,预约人数
				response.put("ygrs", res.get("YGRS"));
				response.put("yyrs", res.get("YYRS"));
				// 门诊序号
				response.put("mzxh", res.get("JZXH"));
				// 病历本费
				response.put(
						"BLJE",
						"病历费："
								+ String.format("%1$.2f",
								parseDouble(res.get("BLJE"))));

				// 医保信息
				// 本年账户支付：yb_ghjs.BNZHZF
				// 历年账户支付：yb_ghjs.LNZHZF
				// 统筹基金：yb_ghjs.TCZF + yb_ghjs.lxjj + yb_ghjs.lfjj +
				// yb_ghjs.zntcjj + yb_ghjs.LXJSJJ + yb_ghjs.snetjj +
				// yb_ghjs.lnjmjj + GWJJZF + yb_ghjs.zztcjj + TXTCJJ +
				// yb_ghjs.nmgjj + yb_ghjs.gfkzjj + yb_ghjs.gfjfjj +
				// yb_ghjs.lfkzjj + yb_ghjs.lfjfjj + yb_ghjs.cqznjj +
				// yb_ghjs.cqgfjj + yb_ghjs.cqlfjj + yb_ghjs.xnhjj +
				// yb_ghjs.dxsjj
				// 医疗救助基金支付：yb_ghjs.KNJZJJ
				// 现金支付：yb_ghjs.GRXJZF
				// 注：表关联ms_ghmx.sbxh = yb_ghjs.sbxh

				// 医保相关
				String brxz = res.get("BRXZ") + "";
				String SHIYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质",
						ctx);
				String SHENGYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHENGYB", "0",
						"省医保病人性质", ctx);
				String YHYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "YHYB", "0", "余杭医保病人性质",
						ctx);
				Map<String, Object> parametersYB = new HashMap<String, Object>();
				parametersYB.put("SBXH", Long.parseLong(sbxh));
				if (brxz.equals(SHIYB)) {
					String sqlYBXX = "select BNZHZF as BNZHZF,LNZHZF as LNZHZF,"
							+ " (TCZF+LXJJ+LFJJ+ZNTCJJ+LXJSJJ+SNETJJ+LNJMJJ+GWJJZF+ZZTCJJ+TXTCJJ+NMGJJ+GFKZJJ+GFJFJJ"
							+ " +LFKZJJ+LFJFJJ+CQZNJJ+CQGFJJ+CQLFJJ+XNHJJ+DXSJJ) as TCJJ,"
							+ " KNJZJJ as KNJZJJ,GRXJZF as GRXJZF from YB_GHJS where SBXH=:SBXH";
					Map<String, Object> map_YB = dao.doLoad(sqlYBXX,
							parametersYB);
					if (map_YB == null || map_YB.size() == 0) {
						return;
					}
					if (map_YB.containsKey("BNZHZF")
							&& map_YB.get("BNZHZF") != null) {
						response.put("BNZHZF", "本年账户支付:" + map_YB.get("BNZHZF")
								+ "");
					}
					if (map_YB.containsKey("LNZHZF")
							&& map_YB.get("LNZHZF") != null) {
						response.put("LNZHZF", "历年账户支付:" + map_YB.get("LNZHZF")
								+ "");
					}
					if (map_YB.containsKey("TCJJ")
							&& map_YB.get("TCJJ") != null) {
						response.put("TCJJ", "统筹基金:" + map_YB.get("TCJJ") + "");
					}
					if (map_YB.containsKey("KNJZJJ")
							&& map_YB.get("KNJZJJ") != null) {
						response.put("KNJZJJ",
								"医疗救助基金支付:" + map_YB.get("KNJZJJ") + "");
					}
					if (map_YB.containsKey("GRXJZF")
							&& map_YB.get("GRXJZF") != null) {
						response.put("GRXJZF", "现金支付:" + map_YB.get("GRXJZF")
								+ "");
					}
				} else if (brxz.equals(SHENGYB)) {
					String sqlSYB = "select BNZH as BNZH,WNZH as WNZH,TCJJ as TCJJ,DBJZ as DBJZ,GRXJ as GRXJ"
							+ " from SJYB_GHJS where SBXH=:SBXH";
					Map<String, Object> map_SYB = dao.doLoad(sqlSYB,
							parametersYB);
					if (map_SYB == null || map_SYB.size() == 0) {
						return;
					}
					if (map_SYB.containsKey("BNZH")
							&& map_SYB.get("BNZH") != null) {
						response.put("BNZHZF", "本年账户支付:" + map_SYB.get("BNZH")
								+ "");
					}
					if (map_SYB.containsKey("WNZH")
							&& map_SYB.get("WNZH") != null) {
						response.put("LNZHZF", "历年账户支付:" + map_SYB.get("WNZH")
								+ "");
					}
					if (map_SYB.containsKey("TCJJ")
							&& map_SYB.get("TCJJ") != null) {
						response.put("TCJJ", "统筹基金:" + map_SYB.get("TCJJ") + "");
					}
					if (map_SYB.containsKey("DBJZ")
							&& map_SYB.get("DBJZ") != null) {
						response.put("KNJZJJ",
								"医疗救助基金支付:" + map_SYB.get("DBJZ") + "");
					}
					if (map_SYB.containsKey("GRXJ")
							&& map_SYB.get("GRXJ") != null) {
						response.put("GRXJZF", "现金支付:" + map_SYB.get("GRXJ")
								+ "");
					}
				} else if (brxz.equals(YHYB)) {
					String sqlYHYB = "select GRDNZH as GRDNZH,GRLNZH as GRLNZH,"
							+ "(TCJJ+DBJZJJ+LXJJ+BLJJ+GWYBZJJ+MZFYDXJZ+SHYLJZJJ+JMYBTC) as TCJJ,GRXJZF as GRXJZF from YHYB_GHJS where SBXH=:SBXH";
					Map<String, Object> map_YHYB = dao.doLoad(sqlYHYB,
							parametersYB);
					if (map_YHYB == null || map_YHYB.size() == 0) {
						response.put("BNZHZF", "本年账户支付:0.0");
						response.put("LNZHZF", "历年账户支付:0.0");
						response.put("TCJJ", "统筹基金:0.0");
						// response.put("KNJZJJ", "医疗救助基金支付:0.0");
						response.put("GRXJZF", "现金支付:0.0");
					} else {
						if (map_YHYB.containsKey("GRDNZH")
								&& map_YHYB.get("GRDNZH") != null) {
							response.put("BNZHZF",
									"本年账户支付:" + map_YHYB.get("GRDNZH") + "");
						}
						if (map_YHYB.containsKey("GRLNZH")
								&& map_YHYB.get("GRLNZH") != null) {
							response.put("LNZHZF",
									"历年账户支付:" + map_YHYB.get("GRLNZH") + "");
						}
						if (map_YHYB.containsKey("TCJJ")
								&& map_YHYB.get("TCJJ") != null) {
							response.put("TCJJ", "统筹基金:" + map_YHYB.get("TCJJ")
									+ "");
						}
						if (map_YHYB.containsKey("GRXJZF")
								&& map_YHYB.get("GRXJZF") != null) {
							response.put("GRXJZF",
									"现金支付:" + map_YHYB.get("GRXJZF") + "");
						}
					}
				}
			}
			response.put(BSPHISSystemArgument.FPYL, ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.FPYL, ctx));
			response.put(BSPHISSystemArgument.GHDYJMC,
					ParameterUtil.getParameter(manaUnitId,
							BSPHISSystemArgument.GHDYJMC, ctx));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 挂号预检保存
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveGHYJ(Map<String, Object> req,
						   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = user.getUserId();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Map<String, Object> GHYJ = new HashMap<String, Object>();
			GHYJ.put("YJRQ", sdf.parse(body.get("GHSJ") + ""));
			GHYJ.put("BRID", body.get("BRID"));
			GHYJ.put("ZBLB", body.get("ZBLB"));
			GHYJ.put("KSDM", body.get("KSDM"));
			GHYJ.put("YSDM", body.get("YSDM"));
			GHYJ.put("JGID", manaUnitId);
			GHYJ.put("CZGH", userId);
			GHYJ.put("GHBZ",0);
			dao.doSave("create", BSPHISEntryNames.MS_GHYJ, GHYJ, false);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号预检失败！");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号预检失败！");
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号预检失败！");
		}
	}

	public void doPrintMoth2(Map<String, Object> request,
							 Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy.MM.dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		response.put("title", jgname + "挂号");
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String sbxh = request.get("sbxh") + "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		parameters.put("SBXH", Long.parseLong(sbxh));
		String sql = "select brda.BRXZ as BRXZ,mzlb.MZMC as MZMC,ghks.KSMC as KSMC,ghmx.YSDM as YSDM,ghmx.CZGH as CZGH,"
				+ "brda.MZHM as MZHM, ghmx.JZHM as JZHM, brda.BRXM as BRXM, brda.BRXB as BRXB,ghmx.BLJE as BLJE,ghmx.ZJFY as ZJFY,"
				+ "ghmx.GHJE as GHJE,ghmx.XJJE as XJJE,ghmx.ZLJE as ZLJE,ghmx.JZXH as JZXH,ghmx.GHSJ as GHSJ,ghmx.GHLB as GHLB,"
				+ "ghks.GHXE as GHXE,ghks.YGRS as YGRS,ghks.YYRS as YYRS,ghmx.BLJE as BLJE,to_char(ghmx.GHSJ,'yyyy') as YYYY,"
				+ "to_char(ghmx.GHSJ,'MM') as MM,to_char(ghmx.GHSJ,'dd') as DD,ghmx.PLXH as PLXH, "// 挂号限额,已挂人数,预约人数
				+ "jsxx.BCYLFZE as BCYLFZE,jsxx.BCTCZFJE as BCTCZFJE,jsxx.BCZHZFZE as BCZHZFZE,jsxx.BCXZZFZE as BCXZZFZE,ghmx.YZJM as YZJM,ghmx.YZBZ as YZBZ,ghmx.YHJE as YHJE,ghmx.JYJMBZ as JYJMBZ "
				+ "from MS_BRDA brda,MS_GHKS ghks,MS_GHMX ghmx left join NJJB_JSXX jsxx on ghmx.SBXH=jsxx.GHXH,MS_MZLB mzlb where mzlb.MZLB=ghmx.MZLB and ghmx.KSDM=ghks.KSDM  and ghmx.BRID=brda.BRID and ghmx.JGID=:JGID  and ghmx.SBXH=:SBXH";
		try {
			Map<String, Object> res = dao.doSqlLoad(sql, parameters);
			if (res != null) {
				String brxb = DictionaryController.instance()
						.getDic("phis.dictionary.gender")
						.getText(res.get("BRXB") + "");
				String ysxm = DictionaryController.instance()
						.getDic("phis.dictionary.doctor")
						.getText(res.get("CZGH") + "");

				String jsfs = DictionaryController.instance()
						.getDic("phis.dictionary.patientProperties_MZ")
						.getText(res.get("BRXZ") + "");
				// 门诊序号
				response.put("MZXH", res.get("JZXH"));
				response.put("MZHM", res.get("MZHM"));
				response.put("JZHM", res.get("JZHM"));
				response.put("XM", res.get("BRXM"));
				response.put("BRXZ", res.get("BRXZ"));
				response.put("XB", brxb);
				response.put("JSFS", jsfs);
				response.put("XJJE", res.get("XJJE"));
				response.put("YYYY", res.get("YYYY"));
				response.put("MM", res.get("MM"));
				response.put("DD", res.get("DD"));
				response.put("KSMC", res.get("KSMC"));
				response.put("JGMC", user.getManageUnit().getName());
				response.put("SFY", ysxm);
				response.put("PLXH", res.get("PLXH"));
				response.put("GHSJ", sfm.format((Date)res.get("GHSJ")));
				response.put("SFXM1", "挂号总费用");
				response.put("XMJE1", res.get("ZLJE"));
				response.put("JYJMBZ", res.get("JYJMBZ"));
				String bxmx = "";
				if(res.get("BCYLFZE")!=null){
					if(((res.get("YZBZ")+"").equals("1") && Double.parseDouble(res.get("YZJM")+"")>0)||(res.get("YHJE")!=null&&Double.parseDouble(res.get("YHJE")+"")>0)){
						bxmx = "总费用："+res.get("ZLJE")+" 医保统筹支付："+res.get("BCTCZFJE")+" 个人支付："+(Double.parseDouble(res.get("BCYLFZE")+"")-Double.parseDouble(res.get("BCTCZFJE")+""))+" 减免："+BSHISUtil.doublesum(Double.parseDouble(res.get("YZJM")+""),Double.parseDouble(res.get("YHJE")+""));
						//response.put("YBBXMX", "总费用："+res.get("ZLJE")+" 医保统筹支付："+res.get("BCTCZFJE")+" 个人支付："+(Double.parseDouble(res.get("BCYLFZE")+"")-Double.parseDouble(res.get("BCTCZFJE")+""))+"义诊减免："+res.get("YZJM"));
					}else{
						//response.put("YBBXMX", "总费用："+res.get("ZLJE")+" 医保统筹支付："+res.get("BCTCZFJE")+" 个人支付："+(Double.parseDouble(res.get("BCYLFZE")+"")-Double.parseDouble(res.get("BCTCZFJE")+"")));
						bxmx = "总费用："+res.get("ZLJE")+" 医保统筹支付："+res.get("BCTCZFJE")+" 个人支付："+(Double.parseDouble(res.get("BCYLFZE")+"")-Double.parseDouble(res.get("BCTCZFJE")+""));
					}
				}//else{response.put("YBBXMX","");}
				if((res.get("BRXZ")+"").equals("1000") && (((res.get("YZBZ")+"").equals("1") && Double.parseDouble(res.get("YZJM")+"")>0) ||(res.get("YHJE")!=null&&Double.parseDouble(res.get("YHJE")+"")>0))){
					bxmx = "总费用："+res.get("ZLJE")+" 减免："+BSHISUtil.doublesum(Double.parseDouble(res.get("YZJM")+""),Double.parseDouble(res.get("YHJE")+""));
				}
				response.put("BXMX",bxmx);
				response.put("HJJE", res.get("ZLJE"));
/*				if((res.get("YZBZ")+"").equals("1") && Double.parseDouble(res.get("YZJM")+"")>0){
					response.put("HJJE", (Double.parseDouble(res.get("ZLJE")+"")-Double.parseDouble(res.get("YZJM")+"")));
					response.put("XMJE1", (Double.parseDouble(res.get("ZLJE")+"")-Double.parseDouble(res.get("YZJM")+"")));
				}*/
				if(Double.parseDouble(res.get("ZLJE")+"")>0){
					response.put("MXMC1", "一般诊疗费");
					response.put("MXSL1", 1);
					response.put("MXJE1", res.get("ZLJE"));

					String[] upint = { "零", "壹", "贰", "叁", "肆", "伍","陆", "柒", "捌", "玖" };
					double hjje = Double.parseDouble(res.get("ZLJE")+ "");
/*					if((res.get("YZBZ")+"").equals("1") && Double.parseDouble(res.get("YZJM")+"")>0){
						hjje = Double.parseDouble(res.get("ZLJE")+"")-Double.parseDouble(res.get("YZJM")+"");
					}*/
					int w = (int) (hjje / 10000) % 10;
					int q = (int) (hjje / 1000) % 10;
					int b = (int) (hjje / 100) % 10;
					int s = (int) (hjje / 10) % 10;
					int y = (int) (hjje) % 10;
					int j = (int) (hjje * 10) % 10;
					String fStr = String.format("%.0f", hjje * 100);
					int f = Integer.parseInt(fStr) % 10;
					if (f == 0) {
						String jStr = String.format("%.0f", hjje * 10);
						j = Integer.parseInt(jStr) % 10;
					}
					String zjestr="";
					if(w >0)zjestr=zjestr+upint[w]+"万";
					if(q >0)zjestr=zjestr+upint[q]+"仟";
					if(b >0)zjestr=zjestr+upint[b]+"佰";
					if(s >0)zjestr=zjestr+upint[s]+"拾";
					if(y >0)zjestr=zjestr+upint[y]+"圆";
					if(j >0)zjestr=zjestr+upint[j]+"角";
					if(f >0)zjestr=zjestr+upint[f]+"分";
					response.put("DXZJE", zjestr);//大写总金额
					if(zjestr.equals("")){
						response.put("DXZJE", "零圆整");//大写总金额
					}
				}else{
					response.put("DXZJE", "零圆整");//大写总金额
				}
			}
			response.put(BSPHISSystemArgument.FPYL, ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.FPYL, ctx));
			response.put(BSPHISSystemArgument.GHDYJMC,
					ParameterUtil.getParameter(manaUnitId,
							BSPHISSystemArgument.GHDYJMC, ctx));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
	//连接外部的排队叫号
	public void doPdjh(Map<String, Object> ghxx,Map<String, Object> res){
		String driver = "oracle.jdbc.driver.OracleDriver";
		String ljxx="";
		try {
			ljxx=DictionaryController.instance().get("phis.dictionary.pdjhljxx").getText("ljxx");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		String[] arr=ljxx.split("\\|");
		if(arr.length!=3){
			res.put("msg","本机构启用了排队叫号，但字典pdjhljxx中的配置不正确，请联系管理员！但不影响正常业务。");
			res.put("code", "902");
			return;
		}
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+arr[0], arr[1], arr[2]);
		}catch(Exception e) {
			res.put("code", "902");
			res.put("msg","排队叫号系统数据库连接失败,可能是字典pdjhljxx配置错误或数据库问题，但不影响正常业务。");
			return;
		}
		try {
			CallableStatement proc = null; //创建执行存储过程的对象
			proc = conn.prepareCall("{call pro_pd_dlb(?,?,?,?,?,?,?,?,?)}"); //设置存储过程 call为关键字.
			proc.setString(1,ghxx.get("KSDM")+"");//挂号科室编码
			proc.setString(2,ghxx.get("JGID")+"");//机构ID
			proc.setLong(3,Long.parseLong(ghxx.get("PLXH")+""));//排队序号
			proc.setString(4,ghxx.get("BRXM")+"");//病人姓名
			proc.setLong(5,Long.parseLong(ghxx.get("KSDM")+""));//挂号科室编码
			if(ghxx.containsKey("YFPDJH") && "y".equals(ghxx.get("YFPDJH")+"")){
				proc.setString(6,ghxx.get("KSMC")+"");//挂号科室名称
				proc.setString(7,"");//医生编码
				proc.setString(8,"");//医生名称
			}else{
				proc.setString(6,DictionaryController.instance().
						get("phis.dictionary.ghdepartment").getText(ghxx.get("KSDM")+""));//挂号科室名称
				proc.setString(7,ghxx.get("YSDM")==null?"":ghxx.get("YSDM")+"");//医生编码
				proc.setString(8,ghxx.get("YSDM")==null?"":DictionaryController.instance().
						get("phis.dictionary.user").getText(ghxx.get("YSDM")+""));//医生名称
			}
			proc.setString(9,ghxx.get("BRID")+"");//病人ID
			proc.execute();//执行
		}catch(Exception e) {
			res.put("msg","执行存储过程排队叫号存储过程失败,但不影响正常业务。");
			res.put("code", "902");
			return;
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					System.out.println("关闭数据库失败！");
				}
			}
		}
	}

	/**
	 * 查询预约挂号记录
	 * @param request
	 * @param response
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> doQueryAppointmentRecord(Map<String, Object> request,
															 Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sfm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgname = user.getManageUnit().getName();// 用户的机构名称
		Map<String, Object> body = (Map<String, Object>) request.get("body");
		//获取页面传过来的参数
		String dateF=MedicineUtils.parseString(body.get("ds"));//开始时间
		String dateT=MedicineUtils.parseString(body.get("de"));//结束时间
		if(dateF==null||dateT==null){
			return null;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct a.ID as ID,a.PATIENTNAME as PATIENTNAME,a.PATIENTCARD as PATIENTCARD,a.PATIENTMOBILE as PATIENTMOBILE, " +
				"a.DOCTOR as DOCTOR,a.HOSPITAL as HOSPITAL,a.SECTION as SECTION,a.STARTTTIME as STARTTTIME,a.STATUS as STATUS,a.ENDTIME as ENDTIME " +
				"from APPOINTMENT_RECORD a " +
				"where to_char(a.ENDTIME,'yyyy-mm-dd')>=:datef and to_char(a.STARTTTIME,'yyyy-mm-dd')<=:datet ");
		hql.append("order by a.STARTTTIME");
		parameters.put("datef", dateF);
		parameters.put("datet", dateT);
//		hql.append("and HOSPITAL=:hospital");
//		parameters.put("hospital", jgname);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = dao.doSqlQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		response.put("body", list);
		return list;
	}

	/**
	 * 预约挂号取号
	 * @param request
	 * @param response
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAppointmentRecord(Map<String, Object> request,
										Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		Map<String, Object> body = (Map<String, Object>) request.get("body");//获取页面传过来的参数
		String SBXH=MedicineUtils.parseString(body.get("sbxh"));
		String STATUS=MedicineUtils.parseString(body.get("status"));
		try {
			if(SBXH!=null && !"".equals(SBXH)){
				String hql = "update APPOINTMENT_RECORD set STATUS=:STATUS where ID=:ID";
				Map<String,Object> parameters = new HashMap<String, Object>();
				parameters.put("ID", Long.parseLong(SBXH));
				parameters.put("STATUS", Integer.parseInt(STATUS));
				dao.doUpdate(hql, parameters);
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void doSaveYyxx(Map<String, Object> req,Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId=user.getUserId();
		Long hyxh=Long.parseLong(req.get("HYXH")+"");
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,10);
		StringBuffer yyxxsql=new StringBuffer();
		yyxxsql.append("select a.ZBLB as ZBLB,a.HYKS as HYKS,a.HYJG as HYJG,a.HYYS as HYYS,")
				.append("to_char(a.HYSJ,'yyyy-MM-dd HH:mi:ss') as HYSJ,a.BRID as BRID,a.SOURCE as SOURCE,")
				.append("a.CARDID as CARDID,a.CARDTYPE as CARDTYPE,a.LOGINUSERCARDTYPE as LOGINUSERCARDTYPE,")
				.append("a.LOGINUSERIDCARD as LOGINUSERIDCARD,b.KSMC as KSMC,c.PERSONNAME as PERSONNAME,")
				.append("a.GHF as GHF,a.ZLF as ZLF,a.PATIENTNAME as PATIENTNAME,a.PATIENTGENDER as PATIENTGENDER,")
				.append("a.TELEPHONENO as TELEPHONENO,a.ADDRESS as ADDRESS,(to_char(a.HYSJ,'HH')) as SDHH,")
				.append("case when to_char(hysj,'mi') >='30' then '30' else '00' end  as SDMI")
				.append(" from  MS_YYGHHY a join MS_GHKS b on a.HYKS=b.KSDM")
				.append("left join sys_personnel c on a.HYYS=c.PERSONID  where a.HYXH="+hyxh+" and a.HYZT='2'")
				.append(" and to_char(a.HYSJ,'yyyy-MM-dd HH:mi:ss')>='"+df.format(cal.getTime())+"'");
		try{
			Map<String,Object> yyxx=dao.doSqlLoad(yyxxsql.toString(),null);
			if(yyxx!=null && yyxx.size()>0){
				Map<String, Object> ghxx=new HashMap<String,Object>();
				ghxx.put("JGID",yyxx.get("HYJG"));
				ghxx.put("BRID",Long.parseLong(yyxx.get("BRID")+""));
				String hysj=yyxx.get("HYSJ")+"";
				try{
					ghxx.put("GHSJ",df.parse(hysj));
				}catch(ParseException e){
					e.printStackTrace();
				}
				ghxx.put("GHLB","1");
				Long ksdm=Long.parseLong(yyxx.get("HYKS")+"");
				ghxx.put("KSDM",ksdm);
				String doctors=yyxx.get("HYYS")==null?"":yyxx.get("HYYS")+"";
				ghxx.put("YSDM",doctors);
				ghxx.put("JZXH",0);
				ghxx.put("GHCS",1);
				double ghf=Double.parseDouble(yyxx.get("GHF")+"");
				double zlf=Double.parseDouble(yyxx.get("ZLF")+"");
				ghxx.put("GHJE",ghf);
				ghxx.put("ZLJE",zlf);
				ghxx.put("ZJFY",0d);
				ghxx.put("BLJE",0d);
				double xjje=ghf+zlf;
				ghxx.put("XJJE",xjje);
				ghxx.put("ZPJE",0d);
				ghxx.put("ZHJE",0d);
				ghxx.put("HBWC",0d);
				ghxx.put("QTYS",0d);
				ghxx.put("JZJS",0);
				ghxx.put("THBZ",0);
				ghxx.put("CZGH",userId);
				ghxx.put("CZPB",1);
				double dd=Math.random();
				ghxx.put("JZHM","10"+(dd+"").substring(2,12));
				ghxx.put("MZLB",dao.doSqlLoad("select MZLB as MZLB from MS_MZLB a " +
						" where a.jgid='"+yyxx.get("HYJG")+"'",null).get("MZLB"));
				ghxx.put("YYBZ",0);
				ghxx.put("YSPB",0);
				ghxx.put("SFFS",0);
				ghxx.put("JZZT",0);
				ghxx.put("JKZSB",0);
				ghxx.put("HYXH",hyxh);
				Date czsj=new Date();
				String ds=df.format(czsj);
				ghxx.put("CZSJ",czsj);
				ghxx.put("YZJM",0d);
				ghxx.put("YZBZ","0");
				ghxx.put("ZJYYBZ","0");
				ghxx.put("BRXZ",1000L);
				//保存挂号信息
				try{
					Map<String,Object>re=dao.doSave("create",BSPHISEntryNames.MS_GHMX,ghxx,false);
					Long sbxh=Long.parseLong((re.get("SBXH")+""));
					dao.doSqlUpdate("update MS_YYGHHY set HYZT='4',ZFJE="+xjje+",GHXH="+sbxh+" where HYXH="+hyxh,null);
					StringBuffer s=new StringBuffer();
					s.append("<Response><Result><Record><yyid>"+hyxh+"</yyid><HospitalId>"+yyxx.get("HYJG")+"</HospitalId>");
					String Source=yyxx.get("SOURCE")==null?"":yyxx.get("SOURCE")+"";
					String WorkId=yyxx.get("HYJG")+"_"+hyxh;
					s.append("<Source>"+Source+"</Source><WorkId>"+WorkId+"</WorkId><OrderTime>"+hysj+"</OrderTime>");
					s.append("<CardId>"+yyxx.get("CARDID")+"</CardId><CardType>"+yyxx.get("CARDTYPE")+"</CardType>");
					s.append("<PatientName>"+yyxx.get("PATIENTNAME")+"</PatientName>")
							.append("<PatientGender>"+yyxx.get("PATIENTGENDER")+"</PatientGender>")
							.append("<TelePhoneNo>"+yyxx.get("TELEPHONENO")+"</TelePhoneNo><Address>"+yyxx.get("ADDRESS")+"</Address>")
							.append("<SeqCode>"+sbxh+"</SeqCode><deptid>gh"+ksdm+"</deptid><creat__time>"+ds+"</creat__time>")
							.append("<LoginUserCardType>"+yyxx.get("LOGINUSERCARDTYPE")+"</LoginUserCardType>")
							.append("<LoginUserIdCard>"+yyxx.get("LOGINUSERIDCARD")+"</LoginUserIdCard>")
							.append("<doctors>"+doctors+"</doctors><doctorName>"+yyxx.get("PERSONNAME")+"</doctorName>");
					String StartTime="";
					String SDHH=yyxx.get("SDHH")+"";
					String SDMI=yyxx.get("SDMI")+"";
					StartTime=SDHH+":"+SDMI;
					String EndTime="";
					if(SDMI.equals("00")){
						EndTime=SDHH+":30";
					}else{
						int h=Integer.parseInt(SDHH)+1;
						if((h+"").length()==1){
							EndTime="0"+h+":00";
						}else{
							EndTime=h+":00";
						}
					}
					s.append("<time>"+StartTime+"-"+EndTime+"</time><deptname>"+yyxx.get("KSMC")+"</deptname>")
							.append("<orderdate>"+hysj.substring(0,10)+"</orderdate>");
					String PeriodCode="1";
					if((yyxx.get("ZBLB")+"").equals("2")){
						PeriodCode="3";
					}
					s.append("<PeriodCode>"+PeriodCode+"</PeriodCode></Record></Result>")
							.append("<ResultCode>0</ResultCode><ErrorMsg>处理成功</ErrorMsg></Response>");
					String res=AutoBuildSourceSchedule.postTowebService("hcnInterface.registrationSynchronizer","addData",s.toString());
					try{
						Document document = DocumentHelper.parseText(res);
						Element element0 = document.getRootElement();
						Element code = element0.element("code");
						if(code.getTextTrim().equals("200")){
							logger.info(new Date()+"发送签约信息成功！");
						}else{
							logger.info(new Date()+"发送签约信息失败！");
						}
					}catch(DocumentException e){
						e.printStackTrace();
					}
				}catch(ValidateException e){
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
				}
			}else{
				response.put("code","502");
				response.put("msg","未查询到符合信息的可预约记录，请核实!");
			}
		}catch(PersistentDataOperationException e){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "挂号信息保存失败！");
		}
	}
	public void doSaveRetreatYyxx(Map<String, Object> req,Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		//业务要求，前台只能取消当天号
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId=user.getUserId();
		Long hyxh=Long.parseLong(req.get("HYXH")+"");
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dfd=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		StringBuffer yyxxsql=new StringBuffer();
		yyxxsql.append("select a.HYJG as HYJG,a.HYYS as HYYS,a.GHXH as GHXH,a.BRID as BRID,")
				.append("to_char(a.HYSJ,'yyyy-MM-dd HH:mi:ss') as HYSJ,a.SOURCE as SOURCE,")
				.append("a.CARDID as CARDID,a.CARDTYPE as CARDTYPE,a.HYKS as HYKS,")
				.append("a.PATIENTNAME as PATIENTNAME,a.TELEPHONENO as TELEPHONENO")
				.append(" from  MS_YYGHHY a where a.HYXH="+hyxh+" and a.HYZT='4'")
				.append(" and to_char(a.HYSJ,'yyyy-MM-dd')='"+dfd.format(cal.getTime())+"'");
		try{
			Map<String,Object> yyxx=dao.doSqlLoad(yyxxsql.toString(),null);
			if(yyxx!=null && yyxx.size()>0){
				Long ghxh=Long.parseLong(yyxx.get("GHXH")+"");
				if(dao.doCount("MS_GHMX","SBXH="+ghxh+" and HYXH="+hyxh+" and THBZ=0",null)!=0){
					//更改挂号信息状态
					dao.doSqlUpdate("update MS_GHMX set THBZ=1 where SBXH="+ghxh,null);
					StringBuffer thxxsql=new StringBuffer();
					thxxsql.append("select a.SBXH as SBXH,a.JGID as JGID,a.CZGH as CZGH,")
							.append("a.MZLB as MZLB,a.KSDM as KSDM from MS_GHMX a where SBXH="+ghxh);
					Map<String,Object> thxxmap=dao.doSqlLoad(thxxsql.toString(),null);
					thxxmap.put("THRQ",new Date());
					thxxmap.put("CZGH",userId);
					try{
						//插入信息到退号表
						dao.doInsert(BSPHISEntryNames.MS_THMX,thxxmap,false);
						StringBuffer zfsql=new StringBuffer();
						zfsql.append("update MS_YYGHHY set HYZT='9' where HYXH="+hyxh);
						//更改号源表状态
						dao.doSqlUpdate(zfsql.toString(),null);
						StringBuffer s=new StringBuffer();
						s.append("<Response><Result><Record><yyid>"+hyxh+"</yyid>")
								.append("<HospitalId>"+yyxx.get("HYJG")+"</HospitalId>")
								.append("<Source>"+yyxx.get("SOURCE")+"</Source>");
						String WorkId=df.format(new Date())+"_"+hyxh;
						s.append("<WorkId>"+WorkId+"</WorkId><OrderTime>"+yyxx.get("HYSJ")+"</OrderTime>")
								.append("<CardId>"+yyxx.get("CARDID")+"</CardId>")
								.append("<CardType>"+yyxx.get("CARDTYPE")+"</CardType>")
								.append("<PatientName>"+yyxx.get("PATIENTNAME")+"</PatientName>")
								.append("<TelePhoneNo>"+yyxx.get("TELEPHONENO")+"</TelePhoneNo>")
								.append("<SeqCode>"+yyxx.get("GHXH")+"</SeqCode><state>5</state>")
								.append("<deptid>"+"gh"+yyxx.get("HYKS")+"</deptid><creat__time>"+df.format(new Date())+"</creat__time>")
								.append("</Record></Result><ResultCode>0</ResultCode><ErrorMsg>处理成功</ErrorMsg></Response>");
						String res=AutoBuildSourceSchedule.postTowebService("hcnInterface.registrationSynchronizer","removeData",s.toString());
						try{
							Document document = DocumentHelper.parseText(res);
							Element element0 = document.getRootElement();
							Element code = element0.element("code");
							if(code.getTextTrim().equals("200")){
								logger.info(new Date()+"发送取消签约信息成功！");
							}else{
								logger.info(new Date()+"发送取消签约信息失败！");
							}
						}catch(DocumentException e){
							e.printStackTrace();
						}
					}catch(ValidateException e){
						e.printStackTrace();
					}
				}else{
					response.put("code","502");
					response.put("msg","未查询到已预约的挂号信息，请核实!");
				}
			}else{
				response.put("code","502");
				response.put("msg","前台只能取消当天的预约，请核实!");
			}
		}catch(PersistentDataOperationException e){
			e.printStackTrace();
		}
	}
}
