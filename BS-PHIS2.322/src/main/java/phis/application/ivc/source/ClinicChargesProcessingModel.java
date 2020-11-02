package phis.application.ivc.source;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ctd.net.rpc.util.ServiceAdapter;
import ctd.util.S;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Hcnservices.Exception_Exception;
import Hcnservices.HCNWebservices;
import Hcnservices.HCNWebservicesService;

import com.bsoft.pix.server.EmpiIdGenerator;

import phis.application.pay.source.SelfHelpMachineService;
import phis.source.utils.BSHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.application.cfg.source.ConfigLogisticsInventoryControlModel;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyDispensingModel;
import phis.application.pix.source.EmpiInterfaceImpi;
import phis.application.pix.source.EmpiService;
import phis.application.pix.source.EmpiUtil;
import phis.application.reg.source.RegisteredManagementModel;
import phis.application.yb.source.MedicareModel;
import phis.application.yb.source.YBModel;

public class ClinicChargesProcessingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicChargesProcessingModel.class);

	public ClinicChargesProcessingModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 查询是否有有效的发药窗口
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadOpenPharmacyWin(Map<String, Object> req,
									  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String QYFYCK = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.QYFYCK, ctx);
		Map<String, Object> body = new HashMap<String, Object>();
		String warnMsg = "";
		if ("1".equals(QYFYCK)) {
			try {
				String hql = "select CKBH as CKBH from YF_CKBH where JGID=:JGID and YFSB=:YFSB and QYPB=1 order by PDCF";
				List<Object> fyyfList = (List<Object>) req.get("body");
				Set<Long> fyyfSet = new HashSet<Long>();
				for (Object fyyf : fyyfList) {
					fyyfSet.add(Long.parseLong(fyyf.toString()));
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JGID", manaUnitId);
				for (Long fyyf : fyyfSet) {
					parameters.put("YFSB", fyyf);
					List<Map<String, Object>> list = dao.doQuery(hql,
							parameters);
					if (list == null || list.size() <= 0) {
						warnMsg += "【"
								+ DictionaryController.instance()
								.get("phis.dictionary.pharmacy")
								.getText(fyyf.toString()) + "】";
					}
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException("查询发药窗口错误，请联系管理员!", e);
			} catch (ControllerException e) {
				throw new ModelDataOperationException("获取数据字典失败!", e);
			}

		}
		if (warnMsg.length() > 0) {
			body.put("warnMsg", warnMsg + "未找到有效的发药窗口!");
		}
		body.put("QYFYCK", QYFYCK);
		res.put("body", body);
	}

	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
							  Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String userid=user.getUserId();
		// 医保相关
		// Long SHIYB = Long.parseLong(ParameterUtil.getParameter(manaUnitId,
		// "SHIYB", "0", "市医保病人性质", ctx));
		// Long SHENGYB = Long.parseLong(ParameterUtil.getParameter(manaUnitId,
		// "SHENGYB", "0", "省医保病人性质", ctx));
		String YMJDYSGH = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.YMJDYSGH, ctx);
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		Map<String, Object> person = null;
		try {
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date lastday = matter.parse(BSHISUtil.getDate() + " 00:00:00");
			//yx-2017-05-22-增加判断有么有日结提醒开始
			//日结sql
			String rjsql="select count(1) as WJZSL from MS_MZXX where JGID=:JGID and CZGH=:CZGH and SFRQ <:SFRQ and JZRQ is null ";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("JGID", manaUnitId);
			p.put("CZGH", userid);
			p.put("SFRQ", lastday);
			Map<String, Object> wjcount= dao.doSqlLoad(rjsql, p);
			if(wjcount!=null && Integer.parseInt(wjcount.get("WJZSL")+"")>0 ){
				res.put("code", "502");
				res.put("msg", "您还有收费信息未日结，请先日结再收费");
				return;
			}
			//yx-2017-05-22-增加判断有么有日结提醒结束
			if (req.containsKey("JZKH")) {
				String JZKH = (String) req.get("JZKH");
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JZKH", JZKH);
				Map<String, Object> MPI_Card = dao
						.doLoad("select empiId as empiId,status as status from MPI_Card " +
										" where cardNo=:JZKH and cardTypeCode in('"+KLX+"','03')",
								parameters);
				if (MPI_Card != null) {
					if(parseLong(MPI_Card.get("status"))==1){
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已挂失");
					}
					if(parseLong(MPI_Card.get("status"))==2){
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已注销");
					}
					if(parseLong(MPI_Card.get("status"))==3){
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "该卡号已失效");
					}
					String empiId = (String) MPI_Card.get("empiId");
					String topUnitId = ParameterUtil.getTopUnitId();
					String BRXZ = ParameterUtil.getParameter(topUnitId,
							BSPHISSystemArgument.MRXZ, ctx);
					// update by caijy at 2014.10.11 for 增加病人性质大类的查询
					// 用于收费信息保存时如果大类是医保的未录入诊断 不允许保存
					String sql = "SELECT a.EMPIID as EMPIID,a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXZ as BRXZ,b.XZDL as XZDL,a.BRXB as BRXB,a.CSNY as CSNY FROM MS_BRDA a,GY_BRXZ b WHERE a.BRXZ=b.BRXZ and a.EMPIID=:EMPIID";
					Map<String, Object> JZKHparameters = new HashMap<String, Object>();
					JZKHparameters.put("EMPIID", empiId);
					person = dao.doLoad(sql, JZKHparameters);
					if (person != null) {
						person.put("JZKH", JZKH);
						// person = ListPerson.get(0);
						if (person.get("CSNY") != null) {
							person.put(
									"AGE",
									BSPHISUtil.getPersonAge(
											(Date) person.get("CSNY"), null)
											.get("ages"));
						}
						if (person.get("BRXZ") == null) {
							person.put("BRXZ", BRXZ);
						}
					}
				}
			} else {
				String MZHM = (String) req.get("MZHM");
				String topUnitId = ParameterUtil.getTopUnitId();
				String BRXZ = ParameterUtil.getParameter(topUnitId,
						BSPHISSystemArgument.MRXZ, ctx);
				// update by caijy at 2014.10.11 for 增加病人性质大类的查询
				// 用于收费信息保存时如果大类是医保的未录入诊断 不允许保存
				String sql = "SELECT a.EMPIID as EMPIID,a.BRID as BRID,a.MZHM as MZHM,a.BRXM as BRXM,a.BRXZ as BRXZ,b.XZDL as XZDL,a.BRXB as BRXB,a.CSNY as CSNY FROM MS_BRDA a,GY_BRXZ b WHERE a.BRXZ=b.BRXZ and a.MZHM=:MZHM";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("MZHM", MZHM);
				person = dao.doLoad(sql, parameters);
				if (person != null) {
					// person = ListPerson.get(0);
					if (person.get("CSNY") != null) {
						person.put(
								"AGE",
								BSPHISUtil.getPersonAge(
										(Date) person.get("CSNY"), null).get(
										"ages"));
					}
					if (person.get("BRXZ") == null) {
						person.put("BRXZ", BRXZ);
					}
					Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
					MPI_Cardparameters.put("empiId", person.get("EMPIID"));
					Map<String, Object> MPI_Card = dao
							.doLoad("select cardNo as cardNo from MPI_Card where cardTypeCode="+KLX+" and empiId=:empiId and rownum=1",
									MPI_Cardparameters);
					if (MPI_Card != null) {
						person.put("JZKH", MPI_Card.get("cardNo"));
					}
				}
			}
			if (person != null) {
				String GHXQ = ParameterUtil.getParameter(manaUnitId,
						BSPHISSystemArgument.GHXQ, ctx);
				// midify by yangl 增加按天计算挂号效期
				String XQJSFS = ParameterUtil.getParameter(manaUnitId,
						BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
				String MTSQYCGHF = ParameterUtil.getParameter(manaUnitId,
						BSPHISSystemArgument.MTSQYCGHF, ctx);
				String WGHMS = ParameterUtil.getParameter(manaUnitId,
						BSPHISSystemArgument.YXWGHBRJZ, ctx);
				// 挂号有效期
				Date now = new Date();
				Date dayEnd = matter.parse(BSHISUtil.getDate() + " 23:59:59");
				if ("1".equals(XQJSFS + "")) {
					now = matter.parse(BSHISUtil.getDate() + " 23:59:59");
				}
				if (req.containsKey("YYTAG")) {
					int yytag = Integer.parseInt(req.get("YYTAG") + "");
					if (yytag == 2) {
						now = matter.parse(req.get("GHDT") + " 23:59:59");
					}
				}
				if (GHXQ == null || !GHXQ.matches("[0-9]+")) {
					throw new ModelDataOperationException("挂号效期参数设置错误，请联系管理员!");
				}
				Map<String, Object> MS_GHMXparameters = new HashMap<String, Object>();
				MS_GHMXparameters.put("GHSJ",
						DateUtils.addDays(now, -Integer.parseInt(GHXQ)));
				MS_GHMXparameters.put("JGID", manaUnitId);
				MS_GHMXparameters.put("BRID",
						Long.parseLong(person.get("BRID") + ""));
				MS_GHMXparameters.put("NOW", now);
				String hql = "SELECT a.SBXH as SBXH,a.KSDM as KSDM,b.KSMC as KSMC FROM MS_GHMX a,MS_GHKS b WHERE a.KSDM = b.KSDM and a.JGID = :JGID AND a.BRID = :BRID AND a.THBZ = 0 AND a.GHSJ >:GHSJ AND a.GHSJ < :NOW order by a.SBXH desc";
				List<Map<String, Object>> ListGHXX = dao.doQuery(hql,
						MS_GHMXparameters);
				long GHGL = 0;
				if (ListGHXX.size() > 0) {
					Map<String, Object> GHXX = ListGHXX.get(0);
					GHGL = Long.parseLong(GHXX.get("SBXH") + "");
					person.put("GHKS", GHXX.get("KSDM"));
					person.put("KSMC", GHXX.get("KSMC"));
					// add by yangl 增加自动插入诊疗费(收费处)
					if (req.get("useBy") != null
							&& req.get("useBy").toString().equals("charges")) {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("TEMP_GHGL", GHGL);
						params.put("GHGL", GHGL);
						// params.put("BRID", parseLong(person.get("BRID")));
						// params.put("BRXZ", parseLong(person.get("BRXZ")));
						params.putAll(person);
						doSaveZlf(params, ctx);
					}
					// 判断规定病种
					// Map<String, Object> gdbzparameters = new HashMap<String,
					// Object>();
					// StringBuffer gdbzhql = new StringBuffer();
					// gdbzhql.append("select count(*) as COUNT  from yb_zxsp,yb_ghjs,ms_ghmx where yb_zxsp.yyxh = yb_ghjs.jylsh and yb_ghjs.sbxh = ms_ghmx.sbxh and "
					// + "ms_ghmx.sbxh = "
					// + GHGL
					// + " and ms_ghmx.brxz = "
					// + SHIYB);// 只限于市医保
					// List<Map<String, Object>> gdbzlist = dao.doSqlQuery(
					// gdbzhql.toString(), gdbzparameters);
					// // 规定病种记录
					// long pdgdbz = 0;
					// Map<String, Object> count_gdbz = gdbzlist.get(0);
					// if (Integer.parseInt(count_gdbz.get("COUNT") + "") != 0)
					// {
					// pdgdbz = 1;
					// res.put("PDGDBZ", pdgdbz);
					// } else {
					// res.put("PDGDBZ", pdgdbz);
					// }

				} else {
					// 如果建档医生是疫苗建档医生，优先处理
					if (YMJDYSGH != null && YMJDYSGH.trim().length() > 0
							&& YMJDYSGH.indexOf(req.get("logonName") + "") >= 0) {
						// 自动插入
						Session ss = (Session) ctx.get(Context.DB_SESSION);
						ss.beginTransaction();
						doSaveGhmx(person, res, ctx);
						ss.getTransaction().commit();
						GHGL = Long.parseLong(res.get("GHGL").toString());
					} else if ("1".equals(WGHMS)) {
						person.put("chooseGhks", 1);// 弹出挂号科室选择
						// 无挂号模式新建档病人自动插入挂号明细
						if (req.containsKey("newPerson")) {
							Session ss = (Session) ctx.get(Context.DB_SESSION);
							ss.beginTransaction();
							person.put("ifNew", true);
							doSaveGhmx(person, res, ctx);
							ss.getTransaction().commit();
						}
						GHGL = 0;
					}
				}
				if (req.containsKey("YYTAG")) {
					int yytag = Integer.parseInt(req.get("YYTAG") + "");
					if (yytag == 2) {
						MS_GHMXparameters.put("GHSJ",
								matter.parse(req.get("GHDT") + " 00:00:00"));
						MS_GHMXparameters.put("NOW",
								matter.parse(req.get("GHDT") + " 23:59:59"));
						// 第一次挂免费号，第二个正常挂号收取挂号费
						hql = "SELECT SBXH as SBXH FROM MS_GHMX WHERE JGID = :JGID AND BRID = :BRID AND THBZ = 0 AND GHSJ >=:GHSJ AND GHSJ <=:NOW AND GHJE + ZLJE > 0 order by SBXH desc";
					} else {
						MS_GHMXparameters.put(
								"GHSJ",
								DateUtils.addDays(dayEnd,
										-Integer.parseInt("1")));
						MS_GHMXparameters.put("NOW", new Date());
						// 第一次挂免费号，第二个正常挂号收取挂号费
						hql = "SELECT SBXH as SBXH FROM MS_GHMX WHERE JGID = :JGID AND BRID = :BRID AND THBZ = 0 AND GHSJ >:GHSJ AND GHSJ < :NOW AND GHJE + ZLJE > 0 order by SBXH desc";
					}
				} else {
					MS_GHMXparameters.put("GHSJ",
							DateUtils.addDays(dayEnd, -Integer.parseInt("1")));
					MS_GHMXparameters.put("NOW", new Date());
					// 第一次挂免费号，第二个正常挂号收取挂号费
					hql = "SELECT SBXH as SBXH FROM MS_GHMX WHERE JGID = :JGID AND BRID = :BRID AND THBZ = 0 AND GHSJ >:GHSJ AND GHSJ < :NOW AND GHJE + ZLJE > 0 order by SBXH desc";
				}
				List<Map<String, Object>> list = dao.doQuery(hql,
						MS_GHMXparameters);
				if ("1".equals(MTSQYCGHF)) {// 挂号模式使用，
					if (list.size() > 0) {
						person.put("JTYGH", 1);// 今天已挂号
					}
				}
				person.put("GHGL", GHGL);
				if ("1".equals(WGHMS) && list.size() > 0
						&& !req.containsKey("newPerson")) {// 新病人就诊费在收费处正常调入时产生
					person.put("TEMP_GHGL", list.get(0).get("SBXH"));
					Session ss = (Session) ctx.get(Context.DB_SESSION);
					ss.beginTransaction();
					doSaveZlf(person, ctx);
					ss.getTransaction().commit();
				}
				// Map<String, Object> bllxpar = new HashMap<String, Object>();
				// bllxpar.put("BRID", parseLong(person.get("BRID")));
				// Map<String, Object> bllxMap = dao
				// .doLoad("select BLLX as BLLX from YB_CBRYJBXX where BRID=:BRID",
				// bllxpar);
				// if (bllxMap != null && bllxMap.size() > 0) {
				// if (bllxMap.get("BLLX") != null) {
				// person.put("BLLX", bllxMap.get("BLLX") + "");
				// }
				// }
				res.put("body", person);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		} catch (ParseException e) {
			throw new ModelDataOperationException("挂号效期转化错误!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException("挂号效期转化错误!", e);
		}
	}

	private void doSaveZlf(Map<String, Object> body, Context ctx)
			throws ParseException, PersistentDataOperationException,
			ValidateException, NumberFormatException,
			ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String MTSQYCGHF = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.MTSQYCGHF, ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dayEnd = matter.parse(BSHISUtil.getDate() + " 23:59:59");
		long l = 0;
		// 拱墅模式：增加挂号明细费用查询，判断挂号是否已经收费了（ms_ghmx的ghje+zlje>0表示已经收费了）
		if (!"1".equals(MTSQYCGHF)) {
			parameters.put("FJGL", body.get("TEMP_GHGL"));
			Map<String, Object> sumJe = dao
					.doLoad("select sum(GHJE+ZLJE) as GHF from MS_GHMX WHERE SBXH=:FJGL",
							parameters);
			if (sumJe != null && sumJe.get("GHF") != null
					&& Double.parseDouble(sumJe.get("GHF").toString()) > 0) {
				// 已经存在收过费的挂号费用
				return;
			}
			l = dao.doCount("MS_YJ01 a,YS_MZ_JZLS b",
					"a.FJGL=b.GHXH and a.FJLB=3 and a.FJGL=:FJGL", parameters);
		} else {
			parameters.put("JGID", manaUnitId);
			parameters.put("NOW", new Date());
			parameters.put("BRID", Long.parseLong(body.get("BRID").toString()));
			parameters.put("GHSJ",
					DateUtils.addDays(dayEnd, -Integer.parseInt("1")));
			parameters.put("NOW", new Date());
			Map<String, Object> sumJe = dao
					.doLoad("select sum(GHJE+ZLJE) as GHF from MS_GHMX WHERE JGID = :JGID AND BRID = :BRID AND THBZ = 0 AND GHSJ >:GHSJ AND GHSJ < :NOW)",
							parameters);
			if (sumJe != null && sumJe.get("GHF") != null
					&& Double.parseDouble(sumJe.get("GHF").toString()) > 0) {
				// 已经存在收过费的挂号费用
				return;
			}
			l = dao.doCount(
					"MS_YJ01",
					"FJLB=3 and FJGL in (select SBXH from MS_GHMX WHERE JGID = :JGID AND BRID = :BRID AND THBZ = 0 AND GHSJ >:GHSJ AND GHSJ < :NOW)",
					parameters);
		}
		if (l <= 0) {
			// 插入诊疗费用
			parameters.clear();
			parameters.put("SBXH", Long.parseLong(body.get("GHGL").toString()));
			List<Map<String, Object>> list = dao
					.doQuery(
							"select a.MZKS as MZKS,b.KSDM as KSDM,b.YSDM as YSDM from MS_GHKS a,MS_GHMX b where a.KSDM=b.KSDM and b.SBXH=:SBXH",
							parameters);
			Map<String, Object> ms_ghmx = null;
			if (list.size() > 0) {
				ms_ghmx = list.get(0);
			} else {
				throw new ModelDataOperationException("挂号科室信息错误，请检查基础数据是否正确!");
			}
			// 查询医疗项目中自动插入为1的
			List<Map<String, Object>> fyList = dao
					.doQuery(
							"select FYXH as YLXH,FYDJ as YLDJ from GY_YLMX where ZDCR=1 and ZFPB=0 and JGID='"
									+ manaUnitId + "'", null);
			if (fyList.size() > 0) {
				Map<String, Object> ms_yj01 = new HashMap<String, Object>();
				ms_yj01.put("JGID", manaUnitId);
				ms_yj01.put("BRID", body.get("BRID"));
				ms_yj01.put("BRXM", body.get("BRXM"));
				ms_yj01.put("KDRQ", new Date());
				ms_yj01.put("KSDM", ms_ghmx.get("MZKS"));
				ms_yj01.put("YSDM", ms_ghmx.get("YSDM"));
				ms_yj01.put("ZXKS", ms_ghmx.get("MZKS"));
				ms_yj01.put("ZXPB", 0);
				ms_yj01.put("ZFPB", 0);
				ms_yj01.put("CFBZ", 0);
				ms_yj01.put("JZXH", body.get("JZXH"));
				ms_yj01.put("FJGL", body.get("GHGL"));
				ms_yj01.put("FJLB", 3);
				ms_yj01.put("DJZT", 0);
				ms_yj01.put("DJLY", 6);
				Map<String, Object> keyGen = dao.doSave("create",
						BSPHISEntryNames.MS_YJ01_CIC, ms_yj01, false);
				Long yjxh = (Long) keyGen.get("YJXH");

				Map<String, Object> payArgs = new HashMap<String, Object>();
				payArgs.put("TYPE", 0);
				int num = 0;
				for (Map<String, Object> fyxx : fyList) {
					Map<String, Object> ms_yj02 = new HashMap<String, Object>();
					ms_yj02.put("JGID", manaUnitId);
					ms_yj02.put("YJXH", yjxh);
					ms_yj02.put("YLXH", fyxx.get("YLXH"));
					ms_yj02.put("XMLX", 0);
					ms_yj02.put("YJZX", (num == 0 ? 1 : 0));
					ms_yj02.put("YLDJ", fyxx.get("YLDJ"));
					ms_yj02.put("YLSL", 1);
					ms_yj02.put("HJJE", fyxx.get("YLDJ"));
					ms_yj02.put("FYGB", BSPHISUtil.getfygb(0l,
							Long.parseLong(fyxx.get("YLXH").toString()), dao,
							ctx));
					payArgs.put("FYXH", fyxx.get("YLXH"));
					payArgs.put("FYGB", ms_yj02.get("FYGB"));
					payArgs.put("BRXZ", body.get("BRXZ"));
					ms_yj02.put(
							"ZFBL",
							BSPHISUtil.getPayProportion(payArgs, ctx, dao).get(
									"ZFBL"));
					ms_yj02.put("DZBL", 0.0);
					ms_yj02.put("YJZH", 1);
					dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC, ms_yj02,
							false);
				}
			}
		}
	}

	public Map<String, Object> doSaveGhmx(Map<String, Object> body,
										  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		// 自动插入一条挂号记录
		Map<String, Object> ms_ghmx = new HashMap<String, Object>();
		// 根据当前医生获取就诊号码
		String clinicId = BSPHISUtil.getNotesNumber(user.getUserId() + "",
				manaUnitId, 1, dao, ctx);
		if (clinicId == null) {
			throw new ModelDataOperationException("就诊号码未设置或已用完!");
		}
		String BLF = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.BLF, ctx);
		ms_ghmx.put("JZHM", clinicId);
		ms_ghmx.put("JZXH", 1);
		ms_ghmx.put("JGID", manaUnitId);
		ms_ghmx.put("BRID", Long.parseLong(body.get("BRID").toString()));
		ms_ghmx.put("BRXZ", body.get("BRXZ"));
		ms_ghmx.put("GHSJ", new Date());
		ms_ghmx.put("GHLB", 1);
		try {
			// 自动获取挂号科室
			if (body.get("GHKS") != null
					&& body.get("GHKS").toString().length() > 0) {
				ms_ghmx.put("KSDM", body.get("GHKS"));
			} else {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("JGID", manaUnitId);
				List<Map<String, Object>> ghksList = dao.doQuery(
						"select KSDM as KSDM from MS_GHKS where JGID=:JGID",
						parameters);
				if (ghksList.size() > 0) {
					ms_ghmx.put("KSDM", ghksList.get(0).get("KSDM"));
				} else {
					throw new ModelDataOperationException("自动挂号失败:未找到有效的挂号科室!");
				}
			}
			ms_ghmx.put("JZYS", user.getUserId());
			ms_ghmx.put("CZGH", user.getUserId());
			// 设置默认值
			String hqlWhere = "BRID=:BRID and JGID=:JGID";
			Map<String, Object> params = new HashMap<String, Object>();
			params.clear();
			params.put("BRID", Long.parseLong(body.get("BRID").toString()));
			params.put("JGID", manaUnitId);
			Long l = dao.doCount("MS_GHMX", hqlWhere, params);
			ms_ghmx.put("CZPB", l > 0 ? 0 : 1);
			ms_ghmx.put("GHLB", 1);
			ms_ghmx.put("GHJE", 0.00);
			ms_ghmx.put("GHCS", 1);
			ms_ghmx.put("THBZ", 0);
			ms_ghmx.put("ZLJE", 0.00);
			ms_ghmx.put("ZJFY", 0.00);
			if (body.get("ifNew") != null
					&& body.get("ifNew").toString().equals("true")) {
				if (body.get("BRXZ") != null
						&& body.get("BRXZ").toString().length() > 0
						&& Integer.parseInt(body.get("BRXZ").toString()) >= 3
						&& Integer.parseInt(body.get("BRXZ").toString()) < 10) {
					ms_ghmx.put("BLJE", 0.00);
					ms_ghmx.put("XJJE", 0.00);
				} else {
					ms_ghmx.put("BLJE", Double.parseDouble(BLF));
					ms_ghmx.put("XJJE", Double.parseDouble(BLF));
				}
			} else {
				ms_ghmx.put("BLJE", 0.00);
				ms_ghmx.put("XJJE", 0.00);
			}
			ms_ghmx.put("ZPJE", 0.00);
			ms_ghmx.put("ZHJE", 0.00);
			ms_ghmx.put("HBWC", 0.00);
			ms_ghmx.put("QTYS", 0.00);
			ms_ghmx.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			ms_ghmx.put("YSPB", 0);
			ms_ghmx.put("SFFS", 0);
			ms_ghmx.put("JZZT", 0);
			ms_ghmx.put("CZSJ", new Date());
			Map<String, Object> keyGen = dao.doSave("create",
					BSPHISEntryNames.MS_GHMX, ms_ghmx, false);
			Map<String, Object> MS_GH_FKXX = new HashMap<String, Object>();
			if (body.get("ifNew") != null
					&& body.get("ifNew").toString().equals("true")) {
				MS_GH_FKXX.put("SBXH", keyGen.get("SBXH"));
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("MRBZ", "1");
				Map<String, Object> fkfs = dao
						.doLoad("select FKFS as FKFS,FKJD as FKJD,SRFS as SRFS from GY_FKFS where MRBZ=:MRBZ and SYLX=1",
								parameters);
				if (fkfs == null) {
					throw new ModelDataOperationException("请先设置默认付款方式!");
				}
				MS_GH_FKXX.put("FKFS", fkfs.get("FKFS"));
				MS_GH_FKXX.put("FKJE", Double.parseDouble(BLF));
				dao.doSave("create", BSPHISEntryNames.MS_GH_FKXX, MS_GH_FKXX,
						false);

			}
			body.put("GHGL", keyGen.get("SBXH"));
			body.put("TEMP_GHGL", keyGen.get("SBXH"));
			doSaveZlf(body, ctx);
			res.put("GHGL", keyGen.get("SBXH"));
			return body;
		} catch (PersistentDataOperationException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("自动插入挂号信息错误!", e);
		} catch (ValidateException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("自动插入挂号信息错误!", e);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("自动插入挂号信息错误!", e);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			throw new ModelDataOperationException("系统参数【病历费】设置错误，请设置有效数字!", e);
		}

	}

	/**
	 * 卡号门诊号码查询
	 *
	 * @param CardOrMZHM
	 * @param res
	 */
	public Map<String, Object> doCheckCardOrMZHM(Map<String, Object> req,
												 Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jsid = user.getManageUnitId();
		String cardOrMZHM = ParameterUtil.getParameter(jsid, "CARDORMZHM", "1",
				"卡号门诊号码判断,1为卡号2为门诊号码", ctx);
		res.put("cardOrMZHM", cardOrMZHM);
		return res;
	}

	/**
	 * 单据查询
	 *
	 * @param bRID
	 * @param res
	 * @throws ControllerException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDJ(Map<String, Object> body, List<Object> cnds,
						  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ControllerException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		long BRID = Long.parseLong(body.get("BRID") + "");
		long KDRQ = 1;
		String KDRQStr = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.CFXQ, ctx);
		if (KDRQStr != "") {
			KDRQ = Long.parseLong(KDRQStr);
		}
		int QYMZSF = 0;
		String QYMZSFtr = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.QYMZSF, ctx);
		if (QYMZSFtr != "") {
			QYMZSF = Integer.parseInt(QYMZSFtr);
		}
		if (cnds != null && cnds.size() > 0) {
			KDRQ = Long.parseLong((((List<Object>) cnds.get(2)).get(1) + "")
					.split("%")[0]);
		}
		String zbmjgid="";
		Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
		zbmjgid=njjb.getItem(manaUnitId).getProperty("zbmjgid")+"";
		parameters.put("BRID", BRID);
		parameters.put("JGID", manaUnitId);
		parameters.put("QYMZSF", QYMZSF);
		parameters.put("ZBMJGID", zbmjgid);
		parameters
				.put("KDRQ",
						new Date(
								((Calendar.getInstance().getTimeInMillis()) - (KDRQ * 24 * 60 * 60 * 1000))));
		parameters1.put("BRID", BRID);
		parameters1.put("JGID", manaUnitId);
		parameters1.put("QYMZSF", QYMZSF);
		parameters1.put("ZBMJGID", zbmjgid);
		parameters1
				.put("KDRQ",
						new Date(
								((Calendar.getInstance().getTimeInMillis()) - (KDRQ * 24 * 60 * 60 * 1000))));
		StringBuffer hql1 = new StringBuffer(
				"SELECT "
						+ KDRQ
						+ " as XQ,a.CFLX as CFLX,case a.CFLX when 1 then '西药方' when 2 then '中药方' else '草药方' end as DJLX_text,a.KFRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.CFSB as CFSB,SUM(b.HJJE) as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text," +
						"case when not exists (select 1 from MS_CF02 c, yk_cdxx d " +
						"where c.CFSB = a.CFSB and d.jgid = :ZBMJGID and c.ypxh = d.ypxh and c.ypcd = d.ypcd and length(d.yyzbm) > 0) then 1 " +
						"when not exists (select 1 from MS_CF02 c, yk_cdxx d " +
						"where c.CFSB = a.CFSB and d.jgid = :ZBMJGID and c.ypxh = d.ypxh and c.ypcd = d.ypcd and (d.yyzbm is null or d.yyzbm = '')) then 2 else 0 end as DJMS ");// 0自费和医保药品 1全自费药品 2全医保药品
		hql1.append("FROM MS_CF01 a,MS_CF02 b WHERE b.ZFYP!=1 and a.CFSB = b.CFSB AND a.JGID =:JGID and a.BRID =:BRID and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.FYBZ = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KFRQ >:KDRQ) and ( a.DJLY <> 7 OR a.DJLY IS NULL) and ");
		hql1.append("( :QYMZSF = 0 or not exists (select 1 from MS_CF02 c  where a.CFSB = c.CFSB AND a.JGID = :JGID and a.BRID = :BRID and a.KFRQ > :KDRQ and ");
		hql1.append("  ( c.SFJG <> 1  or  c.SFJG is null )) ) ");

		StringBuffer hql2 = new StringBuffer(
				"SELECT e.XQ as XQ,e.CFLX as CFLX,e.DJLX_text as DJLX_text,e.KDRQ as KDRQ,e.KDKS as KDKS,e.KDYS as KDYS,e.CFSB as CFSB,e.ZXKS as ZXKS,SUM(e.HJJE) as HJJE,e.LSBZ as LSBZ,e.JZXH as JZXH,e.DJLY as DJLY,e.DJLY_text as DJLY_text,e.DJMS as DJMS FROM ( ");
		hql2.append("SELECT "
				+ KDRQ
				+ " as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,a.ZXKS as ZXKS,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text," +
				"case when not exists (select 1 from MS_YJ02 c, gy_ylmx d " +
				"where c.YJXH = a.YJXH and d.jgid = :ZBMJGID and c.YLXH = d.fyxh and length(d.yyzbm) > 0) then 1 " +
				"when not exists (select 1 from MS_YJ02 c, gy_ylmx d " +
				"where c.YJXH = a.YJXH and d.jgid = :ZBMJGID and c.YLXH = d.fyxh and (d.yyzbm is null or d.yyzbm = '')) then 2 else 0 end as DJMS ");// 0自费和医保项目 1全自费项目 2全医保项目
		hql2.append("FROM MS_YJ01 a,MS_YJ02 b WHERE a.YJXH=b.YJXH AND a.JGID =:JGID and a.BRID =:BRID and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) and (a.FJGL IS NULL OR a.FJGL = 0) ");

		hql2.append(" union all SELECT "
				+ KDRQ
				+ " as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,a.ZXKS as ZXKS,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text," +
				"case when not exists (select 1 from MS_YJ02 c, gy_ylmx d " +
				"where c.YJXH = a.YJXH and d.jgid = :ZBMJGID and c.YLXH = d.fyxh and length(d.yyzbm) > 0) then 1 " +
				"when not exists (select 1 from MS_YJ02 c, gy_ylmx d " +
				"where c.YJXH = a.YJXH and d.jgid = :ZBMJGID and c.YLXH = d.fyxh and (d.yyzbm is null or d.yyzbm = '')) then 2 else 0 end as DJMS "); //0自费和医保项目 1全自费项目 2全医保项目
		hql2.append("FROM MS_YJ01 a,MS_YJ02 b WHERE a.YJXH=b.YJXH AND a.JGID =:JGID and a.BRID =:BRID and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) "
				+ "  and  (a.FJGL IS NOT NULL AND a.FJGL > 0 )  and "
				+ " (a.FJLB=3 or :QYMZSF = 0 or exists (SELECT 1 FROM MS_CF01 c,MS_CF02 d where c.CFSB = d.CFSB AND c.JGID =:JGID and c.BRID =:BRID  and (d.SFJG = 1 and d.SFJG is not null ) and c.KFRQ > :KDRQ and c.ZFPB = 0  and ( c.CFBZ is null or c.CFBZ <> 2 )  and ( c.DJLY <> 7 OR c.DJLY IS NULL)  and a.FJGL = d.YPZH ))) e ");
		try {
//			// add by caijy for 先选择科室再查询单据
//			// List<Long> l=new ArrayList<Long>();
//			long ksdm = 0;
//			if (body.containsKey("KSDMS") && body.get("KSDMS") != null
//					&& !"".equals(body.get("KSDMS") + "")) {
//				// List<Object> list_ksdms=(List<Object>)body.get("KSDMS");
//				// for(Object o:list_ksdms){
//				// l.add(MedicineUtils.parseLong(o));
//				// }
//				ksdm = MedicineUtils.parseLong(body.get("KSDMS"));
//				hql1.append(" and a.KSDM =(select MZKS from MS_GHKS where KSDM=:ksdms)");
//				hql2.append(" where e.KDKS =(select MZKS from MS_GHKS where KSDM=:ksdms)");
//				parameters.put("ksdms", ksdm);
//				parameters1.put("ksdms", ksdm);
//			}
			hql1.append(" GROUP BY a.CFLX,a.KFRQ,a.KSDM,a.YSDM,a.CFSB,a.JZXH,a.DJLY order by a.KFRQ");
			hql2.append(" GROUP BY e.XQ,e.CFLX,e.DJLX_text,e.KDRQ,e.KDKS,e.KDYS,e.CFSB,e.ZXKS,e.LSBZ,e.JZXH,e.DJLY,e.DJLY_text,e.DJMS order by e.KDRQ");
			// System.out.println(hql1.toString());
			List<Map<String, Object>> listCF = dao.doSqlQuery(hql1.toString(),
					parameters);
			List<Map<String, Object>> listYJ = dao.doSqlQuery(hql2.toString(),
					parameters1);
			// 规定病种

			// for (int i = 0; i < listCF.size(); i++) {
			// if (Long.parseLong(body.get("pdgdbz").toString()) == 1) {
			// listCF.get(i).put("GDBZ", "是");
			// } else {
			// listCF.get(i).put("GDBZ", "");
			// }
			// }
			// for (int i = 0; i < listYJ.size(); i++) {
			// listYJ.get(i).put("DJLY_text", listYJ.get(i).get("DJLY_TEXT"));
			// if (parseLong(body.get("pdgdbz")) == 1) {
			// listYJ.get(i).put("GDBZ", "是");
			// } else {
			// listYJ.get(i).put("GDBZ", "");
			// }
			// }

			List<Map<String, Object>> listDJ = new ArrayList<Map<String, Object>>();
			listDJ.addAll(listCF);
			listDJ.addAll(listYJ);
			SchemaUtil.setDictionaryMassageForList(listDJ,
					"phis.application.ivc.schemas.MS_CFD");
			if (listDJ.size() > 0) {
				res.put("count", 1);
				res.put("KDRQ", KDRQ);
				res.put("body", listDJ);
			} else {
				res.put("count", 0);
				res.put("body", listDJ);

			}
			// 以下代码收费处增加诊断查询
			String GHXQ = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.GHXQ, ctx);
			String XQJSFS = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			if ("1".equals(XQJSFS + "")) {
				now = matter.parse(BSHISUtil.getDate() + " 23:59:59");
			}
			if (GHXQ == null || !GHXQ.matches("[0-9]+")) {
				throw new ModelDataOperationException("挂号效期参数设置错误，请联系管理员!");
			}
			Map<String, Object> MS_GHMXparameters = new HashMap<String, Object>();
			MS_GHMXparameters.put("GHSJ",
					DateUtils.addDays(now, -Integer.parseInt(GHXQ)));
			MS_GHMXparameters.put("JGID", manaUnitId);
			MS_GHMXparameters.put("BRID", BRID);
			MS_GHMXparameters.put("NOW", new Date());
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT a.ZDXH as ZDXH,b.JBMC as JBMC,b.ICD10 as ICD10 FROM MS_GHMX a,GY_JBBM b WHERE a.ZDXH=b.JBXH and a.JGID = :JGID AND a.BRID = :BRID AND a.THBZ = 0 AND a.GHSJ >:GHSJ AND a.GHSJ < :NOW ");
			// if(l.size()>0){
			// hql.append(" and KSDM in (:ksdm)");
			// MS_GHMXparameters.put("ksdm", l);
			// }
//			if (ksdm != 0) {
//				hql.append(" and a.KSDM =:ksdm");
//				MS_GHMXparameters.put("ksdm", ksdm);
//			}
			hql.append(" order by SBXH desc");
			List<Map<String, Object>> ListGHXX = dao.doQuery(hql.toString(),
					MS_GHMXparameters);
			if (ListGHXX != null && ListGHXX.size() > 0) {
				res.put("ZDXH",MedicineUtils.parseLong(ListGHXX.get(0).get("ZDXH")));
				res.put("JBMC",MedicineUtils.parseString(ListGHXX.get(0).get("JBMC")));
				res.put("ICD10",MedicineUtils.parseString(ListGHXX.get(0).get("ICD10")));
			} else {
				res.put("ZDXH", 0);
				res.put("JBMC", "");
				res.put("ICD10", "");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		} catch (ParseException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 根据就诊序号查询单据
	 *
	 * @param bRID
	 * @param res
	 */
	public void doQueryDJByJZXH(Map<String, Object> body,
								Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		long BRID = Long.parseLong(body.get("BRID") + "");
		long KDRQ = 1;
		long jzxh = Long.parseLong(body.get("JZXH") + "");
		String KDRQStr = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.CFXQ, ctx);
		if (KDRQStr != "") {
			KDRQ = Long.parseLong(KDRQStr);
		}
		int QYMZSF = 0;
		String QYMZSFtr = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.QYMZSF, ctx);
		if (QYMZSFtr != "") {
			QYMZSF = Integer.parseInt(QYMZSFtr);
		}

		parameters.put("BRID", BRID);
		parameters.put("JGID", manaUnitId);
		parameters.put("QYMZSF", QYMZSF);
		parameters.put("JZXH", jzxh);
		parameters
				.put("KDRQ",
						new Date(
								((Calendar.getInstance().getTimeInMillis()) - (KDRQ * 24 * 60 * 60 * 1000))));
		parameters1.put("BRID", BRID);
		parameters1.put("JZXH", jzxh);
		parameters1.put("JGID", manaUnitId);
		parameters1.put("QYMZSF", QYMZSF);
		parameters1
				.put("KDRQ",
						new Date(
								((Calendar.getInstance().getTimeInMillis()) - (KDRQ * 24 * 60 * 60 * 1000))));
		StringBuffer hql1 = new StringBuffer(
				"SELECT "
						+ KDRQ
						+ " as XQ,a.CFLX as TYPE,case a.CFLX when 1 then '西药方' when 2 then '中药方' else '草药方' end as DJLX_text,a.KFRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.CFSB as CFSB,SUM(b.HJJE) as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text FROM ");
		hql1.append("MS_CF01 a,MS_CF02 b WHERE a.CFSB = b.CFSB AND a.JGID =:JGID and a.BRID =:BRID and a.JZXH = :JZXH and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.FYBZ = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KFRQ >:KDRQ) and ( a.DJLY <> 7 OR a.DJLY IS NULL) and ");
		hql1.append("( :QYMZSF = 0 or not exists (select 1 from MS_CF02 c  where a.CFSB = c.CFSB AND a.JGID = :JGID and a.BRID = :BRID and a.KFRQ > :KDRQ and ");
		hql1.append("  ( c.SFJG <> 1  or  c.SFJG is null )) ) GROUP BY a.CFLX,a.KFRQ,a.KSDM,a.YSDM,a.CFSB,a.JZXH,a.DJLY order by a.KFRQ");

		StringBuffer hql2 = new StringBuffer(
				"SELECT e.XQ as XQ,e.CFLX as CFLX,e.DJLX_text as DJLX_text,e.KDRQ as KDRQ,e.KDKS as KDKS,e.KDYS as KDYS,e.CFSB as CFSB,e.ZXKS as ZXKS,SUM(e.HJJE) as HJJE,e.LSBZ as LSBZ,e.JZXH as JZXH,e.DJLY as DJLY,e.DJLY_text as DJLY_text FROM ( ");
		hql2.append("SELECT "
				+ KDRQ
				+ " as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,a.ZXKS as ZXKS,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text FROM ");
		hql2.append("MS_YJ01 a,MS_YJ02 b WHERE a.YJXH=b.YJXH AND a.JGID =:JGID and a.BRID =:BRID and a.JZXH = :JZXH and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) and (a.FJGL IS NULL OR a.FJGL = 0) ");

		hql2.append(" union all SELECT "
				+ KDRQ
				+ " as XQ,0 as CFLX,'检查单' as DJLX_text,a.KDRQ as KDRQ,a.KSDM as KDKS,a.YSDM as KDYS,a.YJXH as CFSB,a.ZXKS as ZXKS,b.HJJE as HJJE,0 as LSBZ,a.JZXH as JZXH,a.DJLY as DJLY,case a.DJLY when 1 then '医生开单' when 2 then '药房划价' when 3 then '药房退药' when 4 then '医技划价' when 5 then '体检开单' when 6 then '收费划价' when 7 then '门诊退费' end as DJLY_text FROM ");
		hql2.append("MS_YJ01 a,MS_YJ02 b WHERE a.YJXH=b.YJXH AND a.JGID =:JGID and a.BRID =:BRID and a.JZXH = :JZXH and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL) "
				+ "  and  (a.FJGL IS NOT NULL AND a.FJGL > 0 )  and "
				+ " (a.FJLB=3 or :QYMZSF = 0 or exists (SELECT 1 FROM MS_CF01 c,MS_CF02 d where c.CFSB = d.CFSB AND c.JGID =:JGID and c.BRID =:BRID  and c.JZXH = :JZXH and (d.SFJG = 1 and d.SFJG is not null ) and c.KFRQ > :KDRQ and c.ZFPB = 0  and ( c.CFBZ is null or c.CFBZ <> 2 )  and ( c.DJLY <> 7 OR c.DJLY IS NULL)  and a.FJGL = d.YPZH ))) e GROUP BY e.XQ,e.CFLX,e.DJLX_text,e.KDRQ,e.KDKS,e.KDYS,e.CFSB,e.ZXKS,e.LSBZ,e.JZXH,e.DJLY,e.DJLY_text order by e.KDRQ");
		try {
			// System.out.println(hql1.toString());
			List<Map<String, Object>> listCF = dao.doSqlQuery(hql1.toString(),
					parameters);
			List<Map<String, Object>> listYJ = dao.doSqlQuery(hql2.toString(),
					parameters1);
			// 规定病种

			// for (int i = 0; i < listCF.size(); i++) {
			// if (Long.parseLong(body.get("pdgdbz").toString()) == 1) {
			// listCF.get(i).put("GDBZ", "是");
			// } else {
			// listCF.get(i).put("GDBZ", "");
			// }
			// }
			// for (int i = 0; i < listYJ.size(); i++) {
			// listYJ.get(i).put("DJLY_text", listYJ.get(i).get("DJLY_TEXT"));
			// if (Long.parseLong(body.get("pdgdbz").toString()) == 1) {
			// listYJ.get(i).put("GDBZ", "是");
			// } else {
			// listYJ.get(i).put("GDBZ", "");
			// }
			// }

			List<Map<String, Object>> listDJ = new ArrayList<Map<String, Object>>();
			listDJ.addAll(listCF);
			listDJ.addAll(listYJ);
			SchemaUtil.setDictionaryMassageForList(listDJ,
					"phis.application.ivc.schemas.MS_CFD");
			// MedicareModel mm = new MedicareModel(dao);
			// if (listDJ.size() > 0) {
			// res.put("count", 1);
			// res.put("KDRQ", KDRQ);
			// res.put("body", listDJ);
			// res.put("QDJL", mm.doQueryADelSMK_QDJL(ctx));// 签到记录
			// } else {
			// res.put("count", 0);
			// res.put("body", listDJ);
			// res.put("QDJL", mm.doQueryADelSMK_QDJL(ctx));// 签到记录
			// }
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 单据明细查询
	 *
	 * @param bRID
	 * @param res
	 * @throws ControllerException
	 */
	public void doQueryDJDetails(List<Map<String, Object>> body, String brxz,
								 String fpcx, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, ControllerException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		StringBuffer cfsbs = new StringBuffer();
		StringBuffer yjxhs = new StringBuffer();
		for (int i = 0; i < body.size(); i++) {
			Map<String, Object> dj = body.get(i);
			if ("0".equals(String.valueOf(dj.get("CFLX")))) {
				if (yjxhs.length() > 0) {
					yjxhs.append(",");
					yjxhs.append(dj.get("CFSB"));
				} else {
					yjxhs.append(dj.get("CFSB"));
				}
			} else {
				if (cfsbs.length() > 0) {
					cfsbs.append(",");
					cfsbs.append(dj.get("CFSB"));
				} else {
					cfsbs.append(dj.get("CFSB"));
				}
			}
		}
		int mzsfFlag = 0;
		if (!"1".equals(fpcx)) {
			mzsfFlag = Integer.parseInt(ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.QYMZSF, ctx));
		}
		try {
			List<Map<String, Object>> listDJ = new ArrayList<Map<String, Object>>();
			String zbmjgid="";
			Dictionary njjb=DictionaryController.instance().get("phis.dictionary.NJJB");
			zbmjgid=njjb.getItem(manaUnitId).getProperty("zbmjgid")+"";
			if (cfsbs.length() != 0) {
				StringBuffer hql1 = new StringBuffer(
						"select a.CFSB as CFSB,a.BRID as BRID,b.YPZH as YPZH,case a.CFLX when 1 then '西药方:' when 2 then '中药方:' else '草药方:' end as DJLX_text,b.SBXH as SBXH, b.YPXH as YPXH, c.YPMC as YPMC,a.CFHM as CFHM, to_char(a.KFRQ,'yyyy-mm-dd HH24:mi:ss') as KFRQ, a.CFLX as CFLX, a.KSDM as KSDM,a.YSDM as YSDM,a.DJLY as DJLY,b.YPCD as YPCD, b.YFDW as YFDW, b.CFSB as CFSB, b.YFGG as YFGG, b.YPDJ as YPDJ,b.YPDJ as YPDJ_Y, b.YPSL as YPSL,b.HJJE as HJJE, b.ZFBL as ZFBL, b.YPYF as YPYF,d.MZGB as FYGB,nvl(d.MCSX,d.SFMC) as GBMC,b.CFTS as CFTS,a.DJYBZ as DJYBZ,a.YFSB as YFSB,b.SFJG as SFJG,b.YCSL as YCSL,b.ZFPB as ZFPB,0 as ZXKS,c.NHBM_BSOFT as NHBM_BSOFT,decode(e.YYZBM,'',' ','医保可报销') as YYZBM from ");
				hql1.append("MS_CF01 a,MS_CF02 b,YK_TYPK c,GY_SFXM d,YK_CDXX e where b.ZFYP!=1 and d.SFXM = b.FYGB and c.YPXH=b.YPXH and a.CFSB=b.CFSB and (b.YPCD=e.YPCD) AND (b.YPXH=e.YPXH) AND e.JGID='"+zbmjgid+"' and a.JGID = '");
				hql1.append(manaUnitId).append("'");
				// 判断是否启用处方审核:1.未启用则直接显示录入的处方信息2.启用则在改处方明细审核通过后才显示
				if (mzsfFlag == 1) {// 启用门诊审方
					hql1.append(" and b.SFJG = 1");
				}
				hql1.append(" and (b.CFSB in (" + cfsbs
						+ ")) order by a.CFSB,b.YPZH");
				List<Map<String, Object>> listDJ1 = dao.doSqlQuery(
						hql1.toString(), null);
				listDJ.addAll(listDJ1);
			}
			if (yjxhs.length() != 0) {
				StringBuffer hql2 = new StringBuffer(
						"select a.BRID as BRID,a.YJXH as YJXH,1 as YPZH,'检查单 : ' as DJLX_text,b.SBXH as SBXH, b.YLXH as YPXH, c.FYMC as YPMC, a.TJHM as CFHM, to_char(a.KDRQ,'yyyy-mm-dd HH24:mi:ss') as KFRQ,0 as CFLX, a.KSDM as KSDM,a.YSDM as YSDM,a.DJLY as DJLY, c.FYDW as YFDW, b.YJXH as CFSB,'' as YFGG, b.YLDJ as YPDJ,b.YLDJ as YPDJ_Y, b.YLSL as YPSL, b.HJJE as HJJE, b.ZFBL as ZFBL,'' as YPYF, d.MZGB as FYGB,nvl(d.MCSX,d.SFMC) as GBMC,1 as CFTS,b.YLSL as YCSL,b.ZFPB as ZFPB,a.ZXKS as ZXKS,c.NHBM_BSOFT as NHBM_BSOFT,decode(e.YYZBM,'',' ','医保可报销') as YYZBM from ");
				hql2.append("MS_YJ01 a,MS_YJ02 b,GY_YLSF c,GY_SFXM d,GY_YLMX e where d.SFXM = b.FYGB and c.FYXH=b.YLXH and a.YJXH=b.YJXH and e.JGID='"+zbmjgid+"' and b.YLXH=e.FYXH and e.ZFPB=0 and a.JGID = '"
						+ manaUnitId + "' and (b.YJXH in (" + yjxhs + "))");
				if (mzsfFlag == 1) {
					hql2.append(" and (a.FJLB=3 or exists (SELECT 1 FROM MS_CF01 d,MS_CF02 e where (d.CFSB = e.CFSB and e.SFJG = 1 and d.ZFPB = 0  and ( d.CFBZ is null or d.CFBZ <> 2 )  and ( d.DJLY <> 7 OR d.DJLY IS NULL)  and a.FJGL = e.YPZH) or a.FJGL is null))");
				}
				hql2.append(" order by a.YJXH,b.YJZH");
				List<Map<String, Object>> listDJ2 = dao.doSqlQuery(
						hql2.toString(), null);
				listDJ.addAll(listDJ2);
			}
			SchemaUtil.setDictionaryMassageForList(listDJ,
					"phis.application.ivc.schemas.MS_CF02_FEE");
			res.put("body", listDJ);
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 门诊结算
	 *
	 * @param body
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */

	@SuppressWarnings({ "unchecked", "static-access" })
	public  synchronized void doSaveOutpatientSettlement(Map<String, Object> body,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> MS_MZXX = (Map<String, Object>) body.get("MZXX");
		MS_MZXX.put("MZXH", 0);
		BSPHISUtil bpu = new BSPHISUtil();
		String fphm = bpu.getNotesNumber(uid, manaUnitId, 2, dao, ctx);
		List<Map<String, Object>> datas = (List<Map<String, Object>>) body.get("data");
		if (MS_MZXX.containsKey("mxsave")&& (Boolean) MS_MZXX.get("mxsave")) {
			body.put("body", datas);
			//保存收费信息
			doSaveSettle(body, res, ctx);
			datas = (List<Map<String, Object>>) res.get("data");
		}
		if(datas==null || datas.size()==0){
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"无收费记录,请重新结算!");
		}
		double sfjszfy=0.0;
		/*********************begin 2019-07-18 zhaojian 家医履约费用减免处理*********************/
		String jylyfyxhs = "0";//家医履约费用序号
		double jylyfyjmhj=0.0;//家医履约费用减免合计
		for(int i=0;i<datas.size();i++){
			Map<String, Object> one=datas.get(i);
			sfjszfy+=Double.parseDouble(one.get("HJJE")+"");
			if(one.get("JYLYJM")!=null && (one.get("JYLYJM")+"").equals("1")){
				jylyfyxhs += ","+one.get("YPXH");
				jylyfyjmhj +=Double.parseDouble(one.get("JYLYJMJE")+"");
			}
		}
		MS_MZXX.put("JYLYJMJE", jylyfyjmhj);
		/*********************end 2019-07-18 zhaojian 家医履约费用减免处理*********************/

		/*********************start 孕产妇费用减免处理*********************/
		String ycfjmypxh = "0";
		double ycfjmhj=0.0;
		for(int i=0;i<datas.size();i++){
			Map<String, Object> one=datas.get(i);
			if(one.get("YCFJMJE")!=null && (Double.parseDouble(one.get("YCFJMJE")+""))>0){
				ycfjmypxh += ","+one.get("YPXH");
				ycfjmhj +=Double.parseDouble(one.get("YCFJMJE")+"");
			}
		}
		MS_MZXX.put("YCFJMJE", ycfjmhj);
		/*********************end 孕产妇费用减免处理*********************/

		if(Math.abs(Double.parseDouble(MS_MZXX.get("ZJJE")+"")-sfjszfy)>0.09){
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"总金额与明细总金额相差太大，请重新结算！");
		}
		//如果是农合结算，先进行补偿结算再走系统流程
		String nhjsbz="";//农合结算标记
		String bxid="";//农合报销id	
		String NHKH="";//农合卡号
		String yybh="";
		String ip="";
		String port="";
		String Year="";
		int error=0;
		if(MS_MZXX.containsKey("NHYJSXX")){
			Map<String, Object> NHYJSXX = (Map<String, Object>) MS_MZXX.get("NHYJSXX");
			URL url = this.getwebserviceurl(manaUnitId);
			HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
			HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
			JSONObject request=new JSONObject();
			Calendar d = Calendar.getInstance();
			d.setTime(new Date());
			Year=d.get(Calendar.YEAR)+"";
			String fprq=(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(d.getTime());
			String qzjxx=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId);

			if(qzjxx!=null && qzjxx.length() >0){
				String newargs[]=qzjxx.split(":");
				yybh=newargs[0];
				ip=newargs[1];
				port=newargs[2];
				try {
					request.put("operator", uid);
					request.put("jzlx", "1");
					request.put("djid", NHYJSXX.get("DJID")+"");
					request.put("Year",Year);
					request.put("fprq",fprq);
					request.put("fphm",fphm);
					request.put("zyh","");
					request.put("yybh",yybh);
					request.put("ztjsbj","0");
					request.put("ip",ip );
					request.put("port",port );
					request.put("serviceId", "FybxJs");
					request.put("msgType", "2");
					int savejsjlerr=0;
					try {
						//补偿结算
						String renhjsxx=hcn.doFYbxjs(request.toString());
						JSONObject nhjsxx=new JSONObject(renhjsxx);
						if(nhjsxx.optString("code").equals("1")){
							nhjsbz="1";
							NHKH=NHYJSXX.get("NHKH")+"";
							bxid=nhjsxx.optString("bxid");
							NHYJSXX.put("BXID", bxid);
							NHYJSXX.put("YEAR", Year);
							NHYJSXX.put("JZLB", "1");//门诊
							NHYJSXX.put("ICKH", NHKH);
							NHYJSXX.put("ZFPB", 0);
							NHYJSXX.put("JGID", manaUnitId);
							NHYJSXX.put("JSRQ",d.getTime());
							MS_MZXX.put("YLJZJE", MS_MZXX.get("SUM11"));
							MS_MZXX.put("BXID", bxid);
							try {
								//为避免出现农合结算了我们没结算而无法处理问题，这里实时提交Session
								Session ss = (Session) ctx.get(Context.DB_SESSION);
								ss.beginTransaction();
								dao.doSave("create", BSPHISEntryNames.NH_BSOFT_JSJL, NHYJSXX, false);
								ss.getTransaction().commit();
							} catch (ValidateException e) {
								savejsjlerr=1;
								throw new ModelDataOperationException(e.getMessage());
							} catch (PersistentDataOperationException e) {
								savejsjlerr=1;
								throw new ModelDataOperationException(e.getMessage());
							}finally{
								//取消补偿结算
								if(savejsjlerr==1){
									JSONObject qxjsxx=new JSONObject();
									qxjsxx.put("operator", uid);
									qxjsxx.put("invoiceId", fphm);
									qxjsxx.put("cardid", NHKH);
									qxjsxx.put("yybh", yybh);
									qxjsxx.put("Year", Year);
									qxjsxx.put("bxid", bxid);
									qxjsxx.put("ip",ip );
									qxjsxx.put("port",port );
									qxjsxx.put("serviceId", "ReSaveFybx");
									qxjsxx.put("msgType", "2");

									String reqxjs=hcn.doReSaveFybx(qxjsxx.toString());
									JSONObject qxjs=new JSONObject(reqxjs);
									if(qxjs.optString("code").equals("1")){
										nhjsbz="0";
									}else{
										throw new ModelDataOperationException(qxjs.optString("msg"));
									}
								}
							}
						}else{
							if(nhjsxx.optString("msg").indexOf("已存在发票号码相同")>-1){
								throw new ModelDataOperationException(nhjsxx.optString("msg")+"，请向后调整一位发票号！");
							}else{
								throw new ModelDataOperationException(nhjsxx.optString("msg"));
							}
						}
					} catch (Exception_Exception e) {
						throw new ModelDataOperationException(e.getMessage());
					}
				} catch (JSONException e) {
					throw new ModelDataOperationException(e.getMessage());
				}
			}
		}

		String ISSMK = body.get("ISSMK") == null ? "0" : String.valueOf(body.get("ISSMK"));
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.WZSFXMJG, ctx));
		Date date = new Date();
		try {
			Map<String, Object> jsData = body.get("jsData") == null ? null
					: (Map<String, Object>) body.get("jsData");
			double id_zjje = Double.parseDouble(MS_MZXX.get("ZJJE") + "");
			double id_zfje = Double.parseDouble(MS_MZXX.get("ZFJE") + "");
			double id_xjje = Double.parseDouble(MS_MZXX.get("YSK") + "");
			double id_hbwc = BSPHISUtil.getDouble(id_zfje - id_xjje, 2);
			double id_jkje = Double.parseDouble(MS_MZXX.get("JKJE") + "");
			double id_tzje = BSPHISUtil.getDouble(id_jkje - id_xjje, 2);
			/**************begin 移动支付 zhaojian 2018-09-11****************/
			double id_zpje = 0;
			Integer FFFS = Integer.parseInt(body.get("FFFS") + "");
			if (FFFS ==32 || FFFS==33) {//微信或支付宝
				id_xjje = 0;
				id_zpje = Double.parseDouble(MS_MZXX.get("YSK") + "");
				id_hbwc = BSPHISUtil.getDouble(id_zfje - id_zpje,2);
				id_tzje = BSPHISUtil.getDouble(id_jkje - id_zpje, 2);
			}
			MS_MZXX.put("ZPJE", id_zpje);
			MS_MZXX.put("FFFS", FFFS);
			MS_MZXX.put("XJJE", id_xjje);// 现金金额
			/**************end 移动支付 zhaojian 2018-09-11****************/

			MS_MZXX.put("QTYS", BSPHISUtil.getDouble(id_zjje - id_zfje, 2));
			if(body.containsKey("FILEDATA")){
				MS_MZXX.put("QTYS", MS_MZXX.get("JJZF"));
				MS_MZXX.put("ZHJE", MS_MZXX.get("ZHZF"));
			}else if(body.containsKey("njjbmzjsxx")){
				MS_MZXX.put("QTYS", MS_MZXX.get("JJZF"));
				MS_MZXX.put("ZHJE", MS_MZXX.get("ZHZF"));
			} else {
				MS_MZXX.put("ZHJE", 0d);
			}

			MS_MZXX.put("ZJJE", id_zjje);
			MS_MZXX.put("ZFJE", id_zfje);
			MS_MZXX.put("MZGL", 0);
			MS_MZXX.put("JGID", manaUnitId);// 机构ID
			MS_MZXX.put("HBWC", id_hbwc);// 货币误差
			MS_MZXX.put("ZFPB", 0);// 作废判别
			MS_MZXX.put("THPB", 0);// 退号判别
			MS_MZXX.put("SFFS", 0);// 收费方式
			if ("1".equals(ISSMK)) {// ISSMK 0 为窗口结算 1 为医生站结算
				MS_MZXX.put("SFFS", 1);// 收费方式置为1
			}
			MS_MZXX.put("HBBZ", 0);// 合并标志
			MS_MZXX.put("SFRQ", date);// 收费日期
			MS_MZXX.put("CZGH", uid);
			MS_MZXX.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			MS_MZXX.put("TZJE", id_tzje);
			MS_MZXX.put("FPHM", fphm);
			res.put("FPHM", fphm);
			/*******************add by lizhi 判断医保总费用和系统费用是否一致*********************/
			if(body.containsKey("FILEDATA")){
				Map<String, Object> ybData = (Map<String, Object>) body.get("FILEDATA");
				Double zfy = Double.parseDouble(ybData.get("ZFY")+"");//医保总金额
				Double zhzf = Double.parseDouble(ybData.get("ZHZF")+"");//医保账户支付
				Double ybzf = Double.parseDouble(ybData.get("YBZF")+"");//医保医保支付
				Double mzbz = Double.parseDouble(ybData.get("MZBZ")+"");//医保民政补助
				Double xjzf = Double.parseDouble(ybData.get("XJZF")+"");//医保现金支付
				Double zjje = Double.parseDouble(MS_MZXX.get("ZJJE")+"");//门诊总金额
				Double zhje = Double.parseDouble(MS_MZXX.get("ZHJE")+"");//门诊账户金额
				Double qtys = Double.parseDouble(MS_MZXX.get("QTYS")+"");//门诊其他应收
				/**************begin 移动支付 zhaojian 2018-09-11****************/
				//Double xjje = Double.parseDouble(MS_MZXX.get("XJJE")+"");//门诊现金金额
				Double xjje = Double.parseDouble(MS_MZXX.get("YSK")+"");//门诊现金金额
				/**************end 移动支付 zhaojian 2018-09-11****************/
				Double ybzje = ybzf+zhzf+xjzf+mzbz;//医保总金额=医保支付+个人账户支付+现金支付+民政补助
				Double msZfy = zhje+qtys+xjje;//门诊总费用 = 账户金额+其他应收+现金金额
				if(Math.abs(zfy-zjje)>=0.05 || Math.abs(zjje-ybzje)>=0.05 || Math.abs(ybzje-msZfy)>=0.05){//巫总说的：误差不能超过0.05
					error=1;
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "医保总费用和系统总费用不一致，门诊结算失败！");
				}
			}
			/*******************add by lizhi 判断医保总费用和系统总费用是否一致*********************/
			//yx-2017-09-14-改成先查mzxh完成业务再保存mzxx
			Map<String, Object> temp=new HashMap<String, Object>();
			temp.put("XH", 1);
			Map<String, Object> mzxhmap=dao.doSqlLoad("select SEQ_MS_MZXX.NEXTVAL as MZXH from  dual where 1=:XH", temp);
			MS_MZXX.put("MZXH", Long.parseLong(mzxhmap.get("MZXH")+""));
			Map<String, Object> mzxh = MS_MZXX;

			//农合收费更新nh_bsoft_jsjl中的mzxh
			if(nhjsbz.equals("1")){
				Map<String, Object> p=new HashMap<String, Object>();
				p.put("BXID", bxid);
				p.put("YEAR", Year);
				p.put("MZXH", mzxh.get("MZXH"));
				dao.doUpdate("update NH_BSOFT_JSJL set MZXH=:MZXH  where BXID=:BXID and YEAR=:YEAR",p);
			}
			//yx-南京金保保存报销信息-2017-07-27-b
			if(body.containsKey("njjbmzjsxx")){
				Map<String, Object> njjbdata=(Map<String, Object>)body.get("njjbmzjsxx");
				njjbdata.put("JGID", manaUnitId);
				njjbdata.put("MZXH", mzxh.get("MZXH"));
				njjbdata.put("JSSJ", new Date());
				njjbdata.put("ZFPB",0);
				try {
					dao.doSave("create", BSPHISEntryNames.NJJB_JSXX, njjbdata, false);
				} catch (ValidateException e) {
					throw new ModelDataOperationException(e.getMessage());
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(e.getMessage());
				}
				double BCTCZFJE = Double.parseDouble(njjbdata.get("BCTCZFJE") + "");//统筹支付
				double BCDBJZZF = Double.parseDouble(njjbdata.get("BCDBJZZF") + "");//大病救助
				double BCDBBXZF = Double.parseDouble(njjbdata.get("BCDBBXZF") + "");//大病保险
				double BCMZBZZF = Double.parseDouble(njjbdata.get("BCMZBZZF") + "");//民政补助
				MS_MZXX.put("QTYS",BSPHISUtil.getDouble(BCTCZFJE+BCDBJZZF+BCMZBZZF+BCDBBXZF, 2));
			}
			//yx-南京金保保存报销信息-2017-07-27-e
			/**************以下是医保结算保存************************/
			if(body.containsKey("FILEDATA")){
				Map<String, Object> ybData = (Map<String, Object>) body.get("FILEDATA");
				Map<String, Object> mzxx = new HashMap<String, Object>();
				mzxx.put("MZXH", mzxh);
				mzxx.put("MZXX", MS_MZXX);
				mzxx.put("FILEDATA", ybData);
				YBModel yb = new YBModel(dao);
				yb.doSaveMzjshz(mzxx, null, null);
			}
			/**************医保结算保存结束************************/

			int fpzs = Integer.parseInt(MS_MZXX.get("FPZS") + "");
			for (int i = 0; i < fpzs - 1; i++) {
				bpu.getNotesNumber(uid, manaUnitId, 2, dao, ctx);
			}
			Set<Long> fygbs = new HashSet<Long>();
			Set<Long> cfsbs = new HashSet<Long>();
			Set<Long> yjxhs = new HashSet<Long>();
			Map<Long, List<Long>> cfsb_xy = new HashMap<Long, List<Long>>();
			Map<Long, List<Long>> cfsb_zy = new HashMap<Long, List<Long>>();
			Map<Long, List<Long>> cfsb_cy = new HashMap<Long, List<Long>>();
			// 药房允许切换时
			Set<Long> fyyf_xy = new HashSet<Long>();// 西药
			Set<Long> fyyf_zy = new HashSet<Long>();// 中药
			Set<Long> fyyf_cy = new HashSet<Long>();// 草药
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> data = datas.get(i);
				fygbs.add(Long.parseLong(data.get("FYGB") + ""));
				if ("0".equals(data.get("CFLX") + "")) {
					if (data.containsKey("YJXH")) {
						yjxhs.add(Long.parseLong(data.get("YJXH") + ""));
					} else {
						yjxhs.add(Long.parseLong(data.get("CFSB") + ""));
					}
				} else {
					int cfs = cfsbs.size();
					cfsbs.add(Long.parseLong(data.get("CFSB") + ""));
					if (cfs != cfsbs.size()) {
						Long yfsb = Long.parseLong(data.get("YFSB") + "");
						if ("1".equals(data.get("CFLX") + "")) {// 以前是对比YS_MZ_FYYF_XY来判断类型，现在改为CFLX
							fyyf_xy.add(yfsb);
							if (cfsb_xy.containsKey(yfsb)) {
								cfsb_xy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
							} else {
								List<Long> list = new ArrayList<Long>();
								list.add(Long.parseLong(data.get("CFSB") + ""));
								cfsb_xy.put(yfsb, list);
							}
						} else if ("2".equals(data.get("CFLX") + "")) {
							fyyf_zy.add(yfsb);
							if (cfsb_zy.containsKey(yfsb)) {
								cfsb_zy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
							} else {
								List<Long> list = new ArrayList<Long>();
								list.add(Long.parseLong(data.get("CFSB") + ""));
								cfsb_zy.put(yfsb, list);
							}
						} else if ("3".equals(data.get("CFLX") + "")) {
							fyyf_cy.add(yfsb);
							if (cfsb_cy.containsKey(yfsb)) {
								cfsb_cy.get(yfsb).add(Long.parseLong(data.get("CFSB") + ""));
							} else {
								List<Long> list = new ArrayList<Long>();
								list.add(Long.parseLong(data.get("CFSB") + ""));
								cfsb_cy.put(yfsb, list);
							}
						}
					}
				}
			}
			Iterator<Long> it = fygbs.iterator();
			while (it.hasNext()) {
				long fygb = it.next();
				Map<String, Object> sfmx = new HashMap<String, Object>();
				sfmx.put("MZXH", mzxh.get("MZXH"));
				sfmx.put("JGID", manaUnitId);
				sfmx.put("SFXM", fygb);
				sfmx.put("FPHM", MS_MZXX.get("FPHM"));
				double zjje = 0;
				double zfje = 0;
				for (int i = 0; i < datas.size(); i++) {
					Map<String, Object> data = datas.get(i);
					if (fygb == Long.parseLong(data.get("FYGB") + "")) {
						String HJJE = data.get("HJJE") + "";
						String ZFBL = data.get("ZFBL") + "";
						zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE),2), 2);
						zfje = BSPHISUtil.getDouble(zfje + BSPHISUtil.getDouble(Double.parseDouble(HJJE)
								* Double.parseDouble(ZFBL), 2), 2);
					}
				}
				sfmx.put("ZJJE", zjje);
				sfmx.put("ZFJE", zfje);
				dao.doSave("create", BSPHISEntryNames.MS_SFMX, sfmx, false);
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FPHM", MS_MZXX.get("FPHM"));
			parameters.put("MZXH", mzxh.get("MZXH"));
			Iterator<Long> cf = cfsbs.iterator();
			StringBuffer cfsb = new StringBuffer();
			List<Long> cfsb_list = new ArrayList<Long>();
			while (cf.hasNext()) {
				String cfsbStr = cf.next() + "";
				if (cfsb.length() > 0) {
					cfsb.append(",");
					cfsb.append(cfsbStr);
				} else {
					cfsb.append(cfsbStr);
				}
				cfsb_list.add(Long.parseLong(cfsbStr));
			}

			Iterator<Long> yj = yjxhs.iterator();
			StringBuffer yjxh = new StringBuffer();
			while (yj.hasNext()) {
				if (yjxh.length() > 0) {
					yjxh.append(",");
					yjxh.append(yj.next());
				} else {
					yjxh.append(yj.next());
				}
			}
			if (yjxh.length() != 0) {
				parameters.put("HJGH", uid);// add by liyunt
				dao.doUpdate("update MS_YJ01 set FPHM=:FPHM,MZXH=:MZXH,HJGH =:HJGH where YJXH in ("
						+ yjxh + ") and FPHM is null and MZXH is null",parameters);

				/* 更新发票号码到SCM_SignContractRecord（为了做收费判断） 开始*/
				List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultList = dao.doSqlQuery("select SCID as SCID, YJXH as YJXH from scm_increaseserver where YJXH in (" + yjxh + ")", null);
				if(resultList.size() > 0){
					resultMap = resultList.get(0);
					String SCID = resultMap.get("SCID").toString();
					String YJXH = resultMap.get("YJXH").toString();
					dao.doSqlUpdate("update SCM_SignContractRecord set FPHM=(select FPHM from MS_YJ01 where YJXH="+YJXH+"), payOrNot='1' where SCID="+SCID+"", null);
				}
				/* 更新发票号码到SCM_SignContractRecord（为了做收费判断） 结束*/




				if (wpjfbz == 1 && wzsfxmjg == 1) {
					Map<String, Object> parameterssbxh = new HashMap<String, Object>();
					parameterssbxh.put("MZXH",Long.parseLong(mzxh.get("MZXH") + ""));
					List<Map<String, Object>> sbxhList = dao.doQuery(
							"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH",
							parameterssbxh);
					for (int q = 0; q < sbxhList.size(); q++) {
						Long sbxh = 0L;
						if (sbxhList.get(q).get("SBXH") != null) {
							sbxh = Long.parseLong(sbxhList.get(q).get("SBXH")+"");
							BSPHISUtil.updateXHMXZT(dao, sbxh, "1", manaUnitId);
						}
					}
				}
				parameters.remove("HJGH");
				ConfigLogisticsInventoryControlModel cic = new ConfigLogisticsInventoryControlModel(dao);
				cic.saveMzWzxx(yjxh.toString(), Long.parseLong(MS_MZXX.get("GHKS")==null ? "0":MS_MZXX.get("GHKS")+""),
						Long.parseLong(MS_MZXX.get("GHGL") == null ? "0": MS_MZXX.get("GHGL") + ""), Long
								.parseLong(mzxh.get("MZXH") + ""), ctx);
			}
			if (cfsb.length() != 0) {
				Map<String, Object> parameters1 = new HashMap<String, Object>();
				//Wangjl，窗口编号5为西药房自助机用，6为中药房自助机用
				String hql = "select CKBH as CKBH from YF_CKBH where JGID=:JGID and YFSB=:YFSB and QYPB=1 and  CKBH<>'5' and  CKBH<>'6'  order by PDCF";
				String up_cf01 = "update MS_CF01 set FPHM=:FPHM,MZXH=:MZXH,FYCK=:FYCK where CFSB in (:CFSB) and YFSB=:YFSB and FPHM is null and MZXH is null";
				parameters1.put("JGID", manaUnitId);
				if (cfsb_xy.size() > 0) {// 西药处方
					for (Long yfsb : fyyf_xy) {
						parameters1.put("YFSB", yfsb);
						parameters1.remove("CKBH");
						List<Map<String, Object>> ll_ckbh_xyfList = dao.doQuery(hql, parameters1);
						int ll_ckbh_xyf = -1;
						if (ll_ckbh_xyfList.size() > 0) {
							ll_ckbh_xyf = Integer.parseInt(ll_ckbh_xyfList.get(0).get("CKBH")+"");
						}
						parameters.put("FYCK", ll_ckbh_xyf);
						parameters.put("CFSB", cfsb_xy.get(yfsb));
						parameters.put("YFSB", yfsb);
						int updatecfsl = dao.doUpdate(up_cf01, parameters);

						if (ll_ckbh_xyf > 0) { // 西药房
							parameters1.put("CKBH", ll_ckbh_xyf);
							dao.doUpdate("update YF_CKBH set PDCF = PDCF+"+updatecfsl+
									" where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",parameters1);
						}
					}
				}
				if (cfsb_zy.size() > 0) {// 中药处方
					for (Long yfsb : fyyf_zy) {
						parameters1.put("YFSB", yfsb);
						parameters1.remove("CKBH");
						List<Map<String, Object>> ll_ckbh_zyfList = dao.doQuery(hql, parameters1);
						int ll_ckbh_zyf = -1;
						if (ll_ckbh_zyfList.size() > 0) {
							ll_ckbh_zyf = Integer.parseInt(ll_ckbh_zyfList.get(0).get("CKBH")+ "");
						}
						parameters.put("FYCK", ll_ckbh_zyf);
						parameters.put("CFSB", cfsb_zy.get(yfsb));
						parameters.put("YFSB", yfsb);
						int updatecfsl = dao.doUpdate(up_cf01, parameters);
						if (updatecfsl != cfsb_zy.get(yfsb).size()) {
							error=1;
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"门诊结算失败,有单据已结算，请重新导入后再结算!");
						}
						if (ll_ckbh_zyf > 0) { // 中药房
							parameters1.put("CKBH", ll_ckbh_zyf);
							dao.doUpdate(" update YF_CKBH set PDCF = PDCF + "+ updatecfsl+
											" where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",
									parameters1);
						}
					}
				}
				if (cfsb_cy.size() > 0) {// 草药处方
					for (Long yfsb : fyyf_cy) {
						parameters1.put("YFSB", yfsb);
						parameters1.remove("CKBH");
						List<Map<String, Object>> ll_ckbh_cyfList = dao
								.doQuery(hql, parameters1);
						int ll_ckbh_cyf = -1;
						if (ll_ckbh_cyfList.size() > 0) {
							ll_ckbh_cyf = Integer.parseInt(ll_ckbh_cyfList.get(0).get("CKBH")+ "");
						}

						parameters.put("FYCK", ll_ckbh_cyf);
						parameters.put("CFSB", cfsb_cy.get(yfsb));
						parameters.put("YFSB", yfsb);
						int updatecfsl = dao.doUpdate(up_cf01, parameters);
						if (updatecfsl != cfsb_cy.get(yfsb).size()) {
							error=1;
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,"门诊结算失败,有单据已结算，请重新导入后再结算!");
						}
						if (ll_ckbh_cyf > 0) { // 草药房
							parameters1.put("CKBH", ll_ckbh_cyf);
							dao.doUpdate("update YF_CKBH set PDCF = PDCF + "+ updatecfsl
									+" where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",parameters1);
						}
					}

				}
				//yx-2018-05-29-药房排队叫号实现-b
				if(ParameterUtil.getParameter(manaUnitId, "YFPDJH", ctx).equals("1")){
					String YFPDJHCFLX=ParameterUtil.getParameter(manaUnitId,"YFPDJHCFLX",ctx);
					Map<String, Object> pdjhxx=new HashMap<String, Object>();
					pdjhxx.put("JGID", manaUnitId);
					pdjhxx.put("BRXM", MS_MZXX.get("BRXM"));
					pdjhxx.put("BRXM", MS_MZXX.get("BRXM"));
					pdjhxx.put("BRID", MS_MZXX.get("BRID"));
					List<Map<String, Object>> fyxxs=new ArrayList<Map<String, Object>>();
					for (int i = 0; i < datas.size(); i++) {
						Map<String, Object> data = datas.get(i);
						Long cflx=Long.parseLong(data.get("CFLX")+"");
						Boolean add=false;
						if(YFPDJHCFLX.indexOf("0")>=0){
							add=true;
						}else{
							if(YFPDJHCFLX.indexOf("1")>=0){
								if(cflx==1){
									add=true;
								}
							}
							if(YFPDJHCFLX.indexOf("2")>=0){
								if(cflx==2){
									add=true;
								}
							}
							if(YFPDJHCFLX.indexOf("3")>=0){
								if(cflx==3){
									add=true;
								}
							}
						}
						if(add){
							Map<String, Object> fyxx=new HashMap<String, Object>();
							fyxx.put("YSDM",data.get("YSDM"));
							fyxx.put("KSDM",data.get("YFSB"));
							if(!fyxxs.contains(fyxx)){
								fyxxs.add(fyxx);
							}
						}
					}
					for(Map<String, Object> one:fyxxs){
						pdjhxx.putAll(one);
						String countsql="select count(distinct a.MZXH) +1 as YFFYSL "+
								" from MS_MZXX a,MS_CF01 b where a.MZXH=b.MZXH and a.FPHM=b.FPHM "+
								" and  to_char(a.sfrq,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') "+
								" and a.JGID=:JGID and b.YFSB=:YFSB";
						Map<String, Object> p=new HashMap<String, Object>();
						p.put("JGID", manaUnitId);
						p.put("YFSB",Long.parseLong(one.get("KSDM")+""));
						Map<String, Object> ma=dao.doSqlLoad(countsql,p);
						pdjhxx.put("PLXH", ma.get("YFFYSL"));
						String yfmcsql="select a.YFMC as YFMC from YF_YFLB a where a.JGID=:JGID and a.YFSB=:YFSB";
						Map<String, Object> yfmcmap=dao.doSqlLoad(yfmcsql,p);
						pdjhxx.put("KSMC",yfmcmap.get("YFMC")+"");
						pdjhxx.put("YFPDJH","y");
						RegisteredManagementModel rmm = new RegisteredManagementModel(dao);
						rmm.doPdjh(pdjhxx,res);
					}
				}
				//yx-2018-05-29-药房排队叫号实现-e

			}
			Map<String, Object> countParameters = new HashMap<String, Object>();
			countParameters.put("MZXH", Long.parseLong(mzxh.get("MZXH") + ""));

			long YJ02count = dao.doCount("MS_YJ01 a,MS_YJ02 b",
					"a.YJXH=b.YJXH and a.MZXH=:MZXH", countParameters);
			long CF02count = dao.doCount("MS_CF01 a,MS_CF02 b",
					"a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",
					countParameters);
			if ((YJ02count + CF02count) != datas.size()) {
				error=1;
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
						"门诊结算失败,结算费用数量已发生变化，请重新导入后再结算!");
			}
			//2019-07-18 zhaojian 家医履约费用减免处理
			if(jylyfyxhs.equals("0")){
				jylyfyxhs = "";
			}
			else{
				jylyfyxhs = " and b.YLXH not in("+jylyfyxhs+")";
			}
			//孕产妇减免处理
			if(ycfjmypxh.equals("0")){
				ycfjmypxh = "";
			}
			else{
				ycfjmypxh = " and b.YLXH not in("+ycfjmypxh+")";
			}
			double YJ02hjje = Double.parseDouble(dao.doLoad("select nvl(sum(b.HJJE),0) as HJJE " +
					" from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH" + jylyfyxhs + ycfjmypxh,countParameters).get("HJJE")+ "");
			double CF02hjje = Double.parseDouble(dao.doLoad("select nvl(sum(b.HJJE),0) as HJJE from MS_CF01 a,MS_CF02 b" +
					" where a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",countParameters).get("HJJE")+"");
			if ((BSPHISUtil.getDouble(YJ02hjje + CF02hjje, 2)) != BSPHISUtil
					.getDouble(id_zjje, 2)) {
				error=1;
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
						"门诊结算失败,结算费用金额已发生变化，请重新导入后再结算!");
			}

			if (id_hbwc != 0) {
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("HBWC", "1");
				Long FKFS = Long.parseLong(dao.doLoad("select FKFS as FKFS from GY_FKFS where SYLX=1 and HBWC=:HBWC",
						parameters2).get("FKFS")+ "");
				Map<String, Object> MS_FKXX = new HashMap<String, Object>();
				MS_FKXX.put("JGID", manaUnitId);
				MS_FKXX.put("MZXH", mzxh.get("MZXH"));
				MS_FKXX.put("FKFS", FKFS);
				MS_FKXX.put("FKJE", id_hbwc);
				dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX, false);
			}
			Map<String, Object> MS_FKXX = new HashMap<String, Object>();
			MS_FKXX.put("JGID", manaUnitId);
			MS_FKXX.put("MZXH", mzxh.get("MZXH"));
			if (body.containsKey("FFFS")) {
				if((body.get("FFFS")+"").length() >0){
					MS_FKXX.put("FKFS",Long.valueOf(String.valueOf(body.get("FFFS"))));
				}else{
					MS_FKXX.put("FKFS",Long.valueOf("1"));
				}
			}
			/**************begin 移动支付 zhaojian 2018-09-17****************/
			if (FFFS ==1) {//现金
				MS_FKXX.put("FKJE", BSPHISUtil.getDouble(id_xjje, 2));
			}
			else{
				MS_FKXX.put("FKJE", BSPHISUtil.getDouble(id_zpje, 2));
			}
			/**************end 移动支付 zhaojian 2018-09-17****************/

			dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX, false);
			PharmacyDispensingModel mpmm = new PharmacyDispensingModel(dao);
			String SFZJFY = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.SFZJFY, ctx);
			String YDCZSF = MS_MZXX.get("YDCZSF") + "";// 移动收费判别字段，0为移动收费
			if ("0".equals(YDCZSF)) {// 如果为0，则为移动收费，设置收费直接发药参数
				SFZJFY = "1";
			}
			if ("1".equals(SFZJFY)) {
				for (int i = 0; i < cfsb_list.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("cfsb", cfsb_list.get(i));
					map.put("fygh", uid);
					Map<String, Object> m = mpmm.saveDispensing(map, ctx);
					if (m.containsKey("x-response-code")) {
						if (Integer.parseInt(m.get("x-response-code") + "") > 300) {
							error=1;
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR,
									m.get("x-response-msg")+"");
						}
					}
				}
			}
			if (body.containsKey("details")) {
				List<Map<String, Object>> dta = (List<Map<String, Object>>) body.get("details");
				Map<String, Object> bllxpar = new HashMap<String, Object>();
				for (int i = 0; i < dta.size(); i++) {
					Map<String, Object> detail = dta.get(i);
					if ("0".equals(String.valueOf(detail.get("CFLX")))) {
						bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
						bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
						dao.doUpdate("update MS_YJ02 set ZFBL=:ZFBL where SBXH=:SBXH",bllxpar);
					} else {
						bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
						bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
						dao.doUpdate("update MS_CF02 set ZFBL=:ZFBL where SBXH=:SBXH",bllxpar);
					}
				}
			}
			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
			if (SFQYYFYFY == 1 && MZKCDJSJ == 2) {// 如果启用库存冻结,并且在收费时冻结
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manaUnitId, ctx);
				StringBuffer hql_yfbz = new StringBuffer();
				hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YPXH=:ypxh and YFSB=:yfsb");
				for (Map<String, Object> map_cf02 : datas) {
					if ("0".equals(map_cf02.get("CFLX") + "")) {
						continue;
					}
					Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
					map_par_yfbz.put("ypxh",MedicineUtils.parseLong(map_cf02.get("YPXH")));
					map_par_yfbz.put("yfsb",MedicineUtils.parseLong(map_cf02.get("YFSB")));
					int yfbz = MedicineUtils.parseInt(dao.doLoad(
							hql_yfbz.toString(), map_par_yfbz).get("YFBZ"));
					Map<String, Object> map_kcdj = new HashMap<String, Object>();
					map_kcdj.put("JGID", manaUnitId);
					map_kcdj.put("YFSB",MedicineUtils.parseLong(map_cf02.get("YFSB")));
					map_kcdj.put("CFSB",MedicineUtils.parseLong(map_cf02.get("CFSB")));
					map_kcdj.put("YPXH",MedicineUtils.parseLong(map_cf02.get("YPXH")));
					map_kcdj.put("YPCD",MedicineUtils.parseLong(map_cf02.get("YPCD")));
					map_kcdj.put("YPSL",MedicineUtils.simpleMultiply(2,map_cf02.get("YPSL"), map_cf02.get("CFTS")));
					map_kcdj.put("YFBZ", yfbz);
					map_kcdj.put("DJSJ", new Date());
					map_kcdj.put("JLXH",MedicineUtils.parseLong(map_cf02.get("SBXH")));
					dao.doSave("create","phis.application.pha.schemas.YF_KCDJ", map_kcdj,false);
				}
			}
			// 库存冻结代码结束
			if (jsData != null) {// 如果是医保结算
				MedicareModel model = new MedicareModel(dao);
				Map<String, Object> map_body = new HashMap<String, Object>();// 将需要保存到YB_MZJS的数据放到该变量中
				map_body.put("MZXH", mzxh.get("MZXH"));// 门诊序号 MS_MZXX主键
				map_body.put("FPHM", MS_MZXX.get("FPHM") + "");// 发票号码
				map_body.put("JGID", manaUnitId);// 机构ID
				map_body.put("ZFPB", 0);// 作废判别,1为作废
				model.saveYbMzjs(map_body);
			}
			if (MS_MZXX.containsKey("ZDXH")
					&& MedicineUtils.parseLong(MS_MZXX.get("ZDXH")) != 0
					&& MS_MZXX.containsKey("GHGL")
					&& MedicineUtils.parseLong(MS_MZXX.get("GHGL")) != 0) {
				StringBuffer hql = new StringBuffer();
				hql.append("update MS_GHMX set ZDXH=:zdxh where SBXH=:ghgl");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zdxh",MedicineUtils.parseLong(MS_MZXX.get("ZDXH")));
				map_par.put("ghgl",MedicineUtils.parseLong(MS_MZXX.get("GHGL")));
				dao.doUpdate(hql.toString(), map_par);
			}
			dao.doSave("create",BSPHISEntryNames.MS_MZXX, MS_MZXX, false);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (ValidateException e) {
			error=1;
			logger.error("ValidateException Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "门诊结算失败");
		} catch (PersistentDataOperationException e) {
			error=1;
			logger.error("PersistentDataOperationException Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "门诊结算失败");
		}finally {
			Map<String, Object> map_pa = new HashMap<String, Object>();
			map_pa.put("MZXH", MS_MZXX.get("MZXH"));
			if(error==1){
				try {
					dao.doSqlUpdate("delete from MS_SFMX where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("delete from MS_FKXX where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("update MS_CF01 set FPHM=null,MZXH=null,FYCK=null where MZXH=:MZXH", map_pa);
					dao.doSqlUpdate("update MS_YJ01 set FPHM=null,MZXH=null,HJGH=null where MZXH=:MZXH", map_pa);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				if(nhjsbz.equals("1")){
					//执行程序出错如发现已结算要去农合取消结算
					URL url = this.getwebserviceurl(manaUnitId);
					HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
					HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
					JSONObject qxjsxx=new JSONObject();
					try {
						qxjsxx.put("operator", uid);
						qxjsxx.put("invoiceId", fphm);
						qxjsxx.put("cardid", NHKH);
						qxjsxx.put("yybh", yybh);
						qxjsxx.put("Year", Year);
						qxjsxx.put("bxid", bxid);
						qxjsxx.put("ip",ip );
						qxjsxx.put("port",port );
						qxjsxx.put("serviceId", "ReSaveFybx");
						qxjsxx.put("msgType", "2");
						try {
							String reqxjs=hcn.doReSaveFybx(qxjsxx.toString());
							JSONObject qxjs=new JSONObject(reqxjs);
							if(qxjs.optString("code").equals("1")){
								nhjsbz="0";
								try {dao.doSqlUpdate("update NH_BSOFT_JSJL a set a.ZFPB='1' where a.MZXH=:MZXH",map_pa);
								} catch (PersistentDataOperationException e) {
									e.printStackTrace();
								}
							}else{
								throw new ModelDataOperationException(qxjs.optString("msg"));
							}
						} catch (Exception_Exception e1) {
							e1.printStackTrace();
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

		}
	}

	/**
	 * 复制发票号码
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCopyFphm(Map<String, Object> req,
							   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String fphm = (String) body.get("FPHM");
		Map<String, Object> MZXX = (Map<String, Object>) body.get("MZXX");
		String GHKS = MZXX.get("GHKS").toString();
		parameters.put("JGID", manaUnitId);
		parameters.put("FPHM", fphm);
		try {
			long n = dao.doCount("MS_MZXX", "JGID=:JGID and FPHM=:FPHM",
					parameters);
			if (n > 0) {
				// 处方
				List<Object> djs = new ArrayList<Object>();
				List<Map<String, Object>> cfList = dao
						.doQuery(
								"select CFSB as CFSB,CFLX as CFLX from MS_CF01 where JGID=:JGID and FPHM=:FPHM",
								parameters);
				if (cfList.size() > 0) {
					Map<String, Object> yfsbsparameters = new HashMap<String, Object>();
					yfsbsparameters.put("JGID", manaUnitId);
					List<Map<String, Object>> yfsbs = dao
							.doQuery(
									"select CSMC as CSMC,CSZ as CSZ from GY_XTCS where JGID=:JGID and CSMC like 'YS_MZ_FYYF_"+GHKS+"_%'",
									yfsbsparameters);
					String YS_MZ_FYYF_XY = "0";
					String YS_MZ_FYYF_ZY = "0";
					String YS_MZ_FYYF_CY = "0";
					for (int i = 0; i < yfsbs.size(); i++) {
						if (("YS_MZ_FYYF_"+GHKS+"_XY").equals(yfsbs.get(i).get("CSMC")
								+ "")) {
							YS_MZ_FYYF_XY = (String) yfsbs.get(i).get("CSZ");
						} else if (("YS_MZ_FYYF_"+GHKS+"_ZY").equals(yfsbs.get(i).get(
								"CSMC")
								+ "")) {
							YS_MZ_FYYF_ZY = (String) yfsbs.get(i).get("CSZ");
						} else if (("YS_MZ_FYYF_"+GHKS+"_CY").equals(yfsbs.get(i).get(
								"CSMC")
								+ "")) {
							YS_MZ_FYYF_CY = (String) yfsbs.get(i).get("CSZ");
						}
					}
					if (YS_MZ_FYYF_XY.trim().length() == 0
							|| YS_MZ_FYYF_ZY.trim().length() == 0
							|| YS_MZ_FYYF_CY.trim().length() == 0||"null".equals(YS_MZ_FYYF_XY)||"null".equals(YS_MZ_FYYF_ZY)||"null".equals(YS_MZ_FYYF_CY)) {
						Map<String, Object> rebody = new HashMap<String, Object>();
						rebody.put("msg", "发票复制失败,请先设置发药药房!");
						res.put("body", rebody);
						return;
					}
					for (int i = 0; i < cfList.size(); i++) {
						Map<String, Object> cfparameters = new HashMap<String, Object>();
						cfparameters.put("CFSB", cfList.get(i).get("CFSB"));
						List<Map<String, Object>> cf02s = dao
								.doQuery(
										"select YPXH as medId,YPCD as medsource,YPSL as quantity,case "
												+ cfList.get(i).get("CFLX")
												+ " when 1 then "
												+ YS_MZ_FYYF_XY
												+ " when 2 then "
												+ YS_MZ_FYYF_ZY
												+ " else "
												+ YS_MZ_FYYF_CY
												+ " end  as pharmId,YPDJ as YPDJ from MS_CF02 where CFSB=:CFSB",
										cfparameters);
						for (int j = 0; j < cf02s.size(); j++) {
							cf02s.get(j).put("lsjg", cf02s.get(j).get("YPDJ"));
							Map<String, Object> ci = BSPHISUtil.checkInventory(
									cf02s.get(j), dao, ctx);
							if (Integer.parseInt(ci.get("sign") + "") < 0) {
								Map<String, Object> YPXHparameters = new HashMap<String, Object>();
								YPXHparameters.put("YPXH",
										cf02s.get(j).get("medId"));
								Map<String, Object> rebody = dao
										.doLoad("select YPMC as YPMC from YK_TYPK where YPXH=:YPXH",
												YPXHparameters);
								rebody.put("msg",
										"发票复制失败,\"" + rebody.get("YPMC")
												+ "\"库存不足!");
								res.put("body", rebody);
								return;
							}
						}
					}
					for (int i = 0; i < cfList.size(); i++) {
						Map<String, Object> cf01Pkey = cfList.get(i);
						String CFLX = cf01Pkey.get("CFLX") + "";
						cf01Pkey.remove("CFLX");
						List<Map<String, Object>> cf02List = dao
								.doQuery(
										"select SBXH as SBXH,case "
												+ CFLX
												+ " when 1 then "
												+ YS_MZ_FYYF_XY
												+ " when 2 then "
												+ YS_MZ_FYYF_ZY
												+ " else "
												+ YS_MZ_FYYF_CY
												+ " end  as YFSB from MS_CF02 where CFSB=:CFSB",
										cf01Pkey);
						if (cf02List.size() == 0)
							return;
						Map<String, Object> cf01 = dao.doLoad(
								BSPHISEntryNames.MS_CF01, cf01Pkey.get("CFSB"));
						cf01.putAll(MZXX);
						cf01.put("DJLY", 6);
						cf01.remove("CFSB");
						cf01.remove("FYRQ");
						cf01.remove("PYGH");
						cf01.remove("FYGH");
						cf01.remove("FPHM");
						cf01.remove("MZXH");
						cf01.remove("JZXH");
						cf01.put("KFRQ", new Date());
						cf01.put("PYBZ", 0);
						cf01.put("FYBZ", 0);
						cf01.put("TYBZ", 0);
						cf01.put("ZFPB", 0);
						cf01.put("YXPB", 0);
						cf01.put("DJYBZ", 0);
						Map<String, Object> newCfsb = dao.doSave("create",
								"phis.application.cic.schemas.MS_CF01_FPFZ",
								cf01, false);
						for (int j = 0; j < cf02List.size(); j++) {
							Map<String, Object> cfsb = new HashMap<String, Object>();
							cfsb.put("CFSB", newCfsb.get("CFSB"));
							cfsb.put("CFLX", cf01.get("CFLX"));
							// cfsb.put("DJLX", "1");
							djs.add(cfsb);
							Map<String, Object> cf02Pkey = cf02List.get(j);
							Map<String, Object> cf02 = dao.doLoad(
									BSPHISEntryNames.MS_CF02,
									cf02Pkey.get("SBXH"));
							cf02.put("CFSB", newCfsb.get("CFSB"));
							cf02.remove("SBXH");
							Map<String, Object> PayProportionMap = new HashMap<String, Object>();
							PayProportionMap.put("TYPE", cf02.get("XMLX"));
							PayProportionMap.put("BRXZ", MZXX.get("BRXZ"));
							PayProportionMap.put("FYXH", cf02.get("YPXH"));
							PayProportionMap.put("FYGB", cf02.get("FYGB"));
							Map<String, Object> zfblMap = BSPHISUtil
									.getPayProportion(PayProportionMap, ctx,
											dao);
							if (zfblMap != null) {
								cf02.put("ZFBL", zfblMap.get("ZFBL"));
							} else {
								cf02.put("ZFBL", 1);
							}
							Map<String, Object> ypdjProportion = new HashMap<String, Object>();
							ypdjProportion.put("JGID", manaUnitId);
							ypdjProportion.put("YPXH",
									Long.parseLong(cf02.get("YPXH") + ""));
							ypdjProportion.put(
									"YFSB",
									Long.parseLong(cf02List.get(j).get("YFSB")
											+ ""));
							ypdjProportion.put("YPCD", cf02.get("YPCD"));
							List<Map<String, Object>> ypdjList = dao
									.doQuery(
											"select LSJG as LSJG from YF_KCMX where JGID=:JGID and YPXH=:YPXH and YFSB=:YFSB and YPCD=:YPCD and JYBZ=0",
											ypdjProportion);
							if (ypdjList.size() > 0) {
								if (Double.parseDouble(ypdjList.get(0).get(
										"LSJG")
										+ "") > 0) {
									cf02.put("YPDJ", ypdjList.get(0)
											.get("LSJG"));
								}
								cf02.put(
										"HJJE",
										Double.parseDouble(cf02.get("YPDJ")
												+ "")
												* Double.parseDouble(cf02
												.get("YPSL") + "")
												* Integer.parseInt(cf02
												.get("CFTS") + ""));
							}
							dao.doSave("create", BSPHISEntryNames.MS_CF02,
									cf02, false);
						}
					}
				}
				// 医技
				List<Map<String, Object>> yjList = dao
						.doQuery(
								"select YJXH as YJXH from MS_YJ01 where JGID=:JGID and FPHM=:FPHM",
								parameters);
				if (yjList.size() > 0) {
					for (int i = 0; i < yjList.size(); i++) {
						Map<String, Object> yj01Pkey = yjList.get(i);
						List<Map<String, Object>> yj02List = dao
								.doQuery(
										"select SBXH as SBXH from MS_YJ02 where YJXH=:YJXH",
										yj01Pkey);
						if (yj02List.size() == 0)
							return;
						Map<String, Object> yj01 = dao.doLoad(
								BSPHISEntryNames.MS_YJ01_CIC,
								yj01Pkey.get("YJXH"));
						yj01.remove("YJXH");
						yj01.remove("ZXRQ");
						yj01.remove("FPHM");
						yj01.remove("MZXH");
						yj01.put("KDRQ", new Date());
						yj01.put("YJGL", 0);
						yj01.put("ZFPB", 0);
						yj01.put("JZXH", 0);
						yj01.put("ZXPB", 0);
						yj01.put("DJLY", 6);
						yj01.putAll(MZXX);
						Map<String, Object> newYjsb = dao.doSave("create",
								BSPHISEntryNames.MS_YJ01_CIC, yj01, false);
						for (int j = 0; j < yj02List.size(); j++) {
							Map<String, Object> yjxh = new HashMap<String, Object>();
							yjxh.put("CFSB", newYjsb.get("YJXH"));
							// yjxh.put("DJLX", "2");
							yjxh.put("CFLX", "0");
							djs.add(yjxh);
							Map<String, Object> yj02Pkey = yj02List.get(j);
							Map<String, Object> yj02 = dao.doLoad(
									BSPHISEntryNames.MS_YJ02_CIC,
									yj02Pkey.get("SBXH"));
							yj02.put("YJXH", newYjsb.get("YJXH"));
							yj02.remove("SBXH");
							Map<String, Object> PayProportionMap = new HashMap<String, Object>();
							PayProportionMap.put("TYPE", yj02.get("XMLX"));
							PayProportionMap.put("BRXZ", MZXX.get("BRXZ"));
							PayProportionMap.put("FYXH", yj02.get("YLXH"));
							PayProportionMap.put("FYGB", yj02.get("FYGB"));
							Map<String, Object> zfblMap = BSPHISUtil
									.getPayProportion(PayProportionMap, ctx,
											dao);
							if (zfblMap != null
									&& Double.parseDouble(zfblMap.get("ZFBL")
									+ "") > 0) {
								yj02.put("ZFBL", zfblMap.get("ZFBL"));
							} else {
								yj02.put("ZFBL", 1);
							}
							Map<String, Object> fydjProportion = new HashMap<String, Object>();
							fydjProportion.put("JGID", manaUnitId);
							fydjProportion.put("FYXH",
									Long.parseLong(yj02.get("YLXH") + ""));
							List<Map<String, Object>> fydjList = dao
									.doQuery(
											"select FYDJ as FYDJ from GY_YLMX where JGID=:JGID and FYXH=:FYXH and ZFPB=0",
											fydjProportion);
							if (fydjList.size() > 0) {
								if (Double.parseDouble(fydjList.get(0).get(
										"FYDJ")
										+ "") > 0) {
									yj02.put("YLDJ", fydjList.get(0)
											.get("FYDJ"));
								}
								yj02.put(
										"HJJE",
										Double.parseDouble(yj02.get("YLDJ")
												+ "")
												* Double.parseDouble(yj02
												.get("YLSL") + ""));
							}
							dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC,
									yj02, false);
						}
					}
				}
				res.put("body", djs);
			} else {
				res.put("body", false);
			}
			// dao.doLoad("select MZXH as MZXH from "+BSPHISEntryNames.MS_MZXX+" where JGID=:JGID and FPHM=:FPHM",
			// parameters);
		} catch (ValidateException e) {
			logger.error("Failed to copy invoice number.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "复制发票号码失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to copy invoice number.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "复制发票号码失败");
		}
	}

	/**
	 * 发票作废 发票查询
	 *
	 * @param fphm
	 * @param res
	 * @param ctx
	 */
	public void doQueryFphm(String fphm, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		List<Map<String, Object>> djs = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> persons = dao
					.doSqlQuery(
							" select a.EMPIID as EMPIID,a.MZHM as MZHM,a.BRXM as BRXM,b.BRXZ as BRXZ," +
									" a.BRXB as BRXB,a.CSNY as CSNY,b.ZFPB as ZFPB,c.JZRQ as JZRQ,b.BXID as BXID," +
									" a.NHKH as NHKH,a.SHBZKH as SHBZKH,b.GHGL as GHGL from "+
									" MS_BRDA a,MS_MZXX b left outer join MS_ZFFP c on b.MZXH=c.MZXH where a.BRID=b.BRID and b.JGID=:JGID and b.FPHM=:FPHM",
							parameters);
			Map<String, Object> person = null;
			if (persons.size() > 0) {
				person = persons.get(0);
			}
			if (person != null && person.get("CSNY") != null) {
				person.put("AGE",
						BSPHISUtil
								.getPersonAge((Date) person.get("CSNY"), null)
								.get("ages"));
				Map<String, Object> MPI_Cardparameters = new HashMap<String, Object>();
				MPI_Cardparameters.put("EMPIID", person.get("EMPIID"));
				Map<String, Object> MPI_Card = dao
						.doLoad("select a.cardNo as cardNo from MPI_Card a where a.cardTypeCode="+KLX+" and a.empiId=:EMPIID",
								MPI_Cardparameters);
				if (MPI_Card != null) {
					person.put("JZKH", MPI_Card.get("cardNo"));
				}
			}
			res.put("body", person);
			parameters.remove("JGID");
			List<Map<String, Object>> cfsbs = dao
					.doQuery(
							"select CFSB as CFSB,CFLX as CFLX from MS_CF01 where FPHM=:FPHM",
							parameters);
			djs.addAll(cfsbs);
			List<Map<String, Object>> yjxhs = dao
					.doQuery(
							"select YJXH as CFSB,0 as CFLX from MS_YJ01 where FPHM=:FPHM",
							parameters);
			djs.addAll(yjxhs);
			res.put("djs", djs);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to copy invoice number.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票号码查询失败");
		}

	}

	/**
	 * 发票预作废
	 * add by zhaojian 2019-02-20
	 *
	 * @param fphm
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doValidateBeforeVoidInvoice(Map<String, Object> body,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String fphm = body.get("FPHM") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		Session ss = (Session) ctx.get(Context.DB_SESSION);
		ss.beginTransaction();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.WZSFXMJG, ctx));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		try {
			Map<String, Object> MZXX = dao.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,BRXM as BRXM,FFFS as FFFS,BRID as BRID,JGID as JGID,FPHM as FPHM " +
					" from MS_MZXX where FPHM=:FPHM and JGID=:JGID",parameters);
			if (1 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
				res.put("msg", "该发票号码已作废!");
				return;
			}
			parameters.remove("FPHM");
			parameters.put("MZXH", MZXX.get("MZXH"));
			long ll_found = dao.doCount("MS_YJ01",
					"JGID=:JGID and MZXH=:MZXH and ZXPB>=1", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中的项目已执行过,不能作废!");
				return;
			}
			ll_found = 0;
			ll_found = dao.doCount("MS_CF01","JGID=:JGID and MZXH=:MZXH and FYBZ = 1", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中的药品已发出,不能作废!");
				return;
			}
			ll_found = dao.doCount("MS_CF01","JGID=:JGID and MZXH=:MZXH and FYBZ = 3", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中有药品退药,不能作废!");
				return;
			}
			dao.doUpdate("update MS_MZXX set ZFPB = ZFPB Where JGID=:JGID and MZXH=:MZXH",parameters);
			long ll_Count = dao.doCount("MS_CF01", "JGID=:JGID and MZXH=:MZXH",
					parameters);
			if (ll_Count > 0) {
				dao.doUpdate("update MS_CF01 set ZFPB = ZFPB Where JGID=:JGID and MZXH=:MZXH",parameters);
			}
			ll_Count = dao.doCount("MS_YJ01", "JGID=:JGID and MZXH=:MZXH",parameters);
			if (ll_Count > 0) {
				dao.doUpdate("update MS_YJ01 set ZFPB = ZFPB Where JGID=:JGID and MZXH=:MZXH",parameters);
				if (wpjfbz == 1 && wzsfxmjg == 1) {
					List<Map<String, Object>> sbxhlist = dao.doQuery(
							"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c,WL_CK01 d Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.DJXH=d.DJXH and c.BRLY='1' and d.DJZT<2 and a.JGID=:JGID and a.MZXH=:MZXH",
							parameters);
					for (int i = 0; i < sbxhlist.size(); i++) {
						BSPHISUtil.setSuppliesYKSL(dao,ctx,Long.parseLong(sbxhlist.get(i).get("SBXH")+""), 1);
					}
					Map<String, Object> parametersxhmxupd = new HashMap<String, Object>();
					List<Map<String, Object>> sbxhlistupd = dao.doQuery(
							"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c Where a.YJXH=b.YJXH " +
									"and b.SBXH=c.MZXH and c.BRLY='1' and a.JGID=:JGID and a.MZXH=:MZXH",
							parameters);
					for (int i = 0; i < sbxhlistupd.size(); i++) {
						parametersxhmxupd.put("MZXH",Long.parseLong(sbxhlistupd.get(i).get("SBXH")+ ""));
						parametersxhmxupd.put("BRLY", "1");
						dao.doUpdate("update WL_XHMX set ZTBZ=ZTBZ where MZXH=:MZXH and BRLY=:BRLY",parametersxhmxupd);
					}
				}
			}
			//发票作废后核销家医签约信息 zhaojian 2019-08-31
			cancelSignContractInfo(fphm, manaUnitId, ctx, false);
		} catch (PersistentDataOperationException e) {
			logger.error("Void invoice fails.", e);
			ss.getTransaction().rollback();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票预作废失败");
		}
		ss.getTransaction().rollback();
	}


	/**
	 * 发票作废
	 *
	 * @param fphm
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateVoidInvoice(Map<String, Object> body,Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String fphm = body.get("FPHM") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,BSPHISSystemArgument.WZSFXMJG, ctx));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		try {
/*			Map<String, Object> MZXX = dao.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,BRXM as BRXM " +
					" from MS_MZXX where FPHM=:FPHM and JGID=:JGID",parameters);*/
			Map<String, Object> MZXX = dao.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,BRXM as BRXM,FFFS as FFFS,BRID as BRID,JGID as JGID,FPHM as FPHM " +
					" from MS_MZXX where FPHM=:FPHM and JGID=:JGID",parameters);
			if (1 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
				res.put("msg", "该发票号码已作废!");
				return;
			}
			parameters.remove("FPHM");
			parameters.put("MZXH", MZXX.get("MZXH"));
			long ll_found = dao.doCount("MS_YJ01",
					"JGID=:JGID and MZXH=:MZXH and ZXPB>=1", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中的项目已执行过,不能作废!");
				return;
			}
			ll_found = 0;
			ll_found = dao.doCount("MS_CF01","JGID=:JGID and MZXH=:MZXH and FYBZ = 1", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中的药品已发出,不能作废!");
				return;
			}
			ll_found = dao.doCount("MS_CF01","JGID=:JGID and MZXH=:MZXH and FYBZ = 3", parameters);
			if (ll_found > 0) {
				res.put("msg", "该发票中有药品退药,不能作废!");
				return;
			}
			dao.doUpdate("update MS_MZXX set ZFPB = 1 Where JGID=:JGID and MZXH=:MZXH",parameters);
			long ll_Count = dao.doCount("MS_CF01", "JGID=:JGID and MZXH=:MZXH",
					parameters);
			if (ll_Count > 0) {
				dao.doUpdate("update MS_CF01 set ZFPB = 1 Where JGID=:JGID and MZXH=:MZXH",parameters);
			}
			ll_Count = dao.doCount("MS_YJ01", "JGID=:JGID and MZXH=:MZXH",parameters);
			if (ll_Count > 0) {
				dao.doUpdate("update MS_YJ01 set ZFPB = 1 Where JGID=:JGID and MZXH=:MZXH",parameters);
				if (wpjfbz == 1 && wzsfxmjg == 1) {
					List<Map<String, Object>> sbxhlist = dao.doQuery(
							"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c,WL_CK01 d Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.DJXH=d.DJXH and c.BRLY='1' and d.DJZT<2 and a.JGID=:JGID and a.MZXH=:MZXH",
							parameters);
					for (int i = 0; i < sbxhlist.size(); i++) {
						BSPHISUtil.setSuppliesYKSL(dao,ctx,Long.parseLong(sbxhlist.get(i).get("SBXH")+""), 1);
					}
					Map<String, Object> parametersxhmxupd = new HashMap<String, Object>();
					List<Map<String, Object>> sbxhlistupd = dao.doQuery(
							"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c Where a.YJXH=b.YJXH " +
									"and b.SBXH=c.MZXH and c.BRLY='1' and a.JGID=:JGID and a.MZXH=:MZXH",
							parameters);
					for (int i = 0; i < sbxhlistupd.size(); i++) {
						parametersxhmxupd.put("MZXH",Long.parseLong(sbxhlistupd.get(i).get("SBXH")+ ""));
						parametersxhmxupd.put("BRLY", "1");
						dao.doUpdate("update WL_XHMX set ZTBZ=-1 where MZXH=:MZXH and BRLY=:BRLY",parametersxhmxupd);
					}
				}
			}
			parameters.put("FPHM", fphm);
			parameters.put("CZGH", uid);
			parameters.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			parameters.put("ZFRQ", new Date());
			dao.doSave("create", BSPHISEntryNames.MS_ZFFP, parameters, false);
			if (body.containsKey("YBXX")) {
				MedicareModel model = new MedicareModel(dao);
				model.saveYbFpzf((Map<String, Object>) body.get("YBXX"));
			}
			//农合的进行农合退费
			if(body.containsKey("zfnhdk") && body.get("zfnhdk").equals("1")){
				String qzjxx=DictionaryController.instance().getDic("phis.dictionary.Hcnqzj").getText(manaUnitId);
				if(qzjxx!=null && qzjxx.length() >0){
					String newargs[]=qzjxx.split(":");

					JSONObject zfxx=new JSONObject();
					try {
						zfxx.put("operator",uid);
						zfxx.put("invoiceId", fphm);
						zfxx.put("cardid", body.get("nhkh")+"");
						Calendar d = Calendar.getInstance();
						d.setTime(new Date());
						zfxx.put("yybh",newargs[0]);
						zfxx.put("Year", d.get(Calendar.YEAR)+"");
						zfxx.put("bxid", body.get("bxid")+"");
						zfxx.put("ip",newargs[1] );
						zfxx.put("port",newargs[2] );
						zfxx.put("serviceId", "ReSaveFybx");
						zfxx.put("msgType", "2");
						URL url = this.getwebserviceurl(manaUnitId);
						HCNWebservicesService HCNWeb= new HCNWebservicesService(url);
						HCNWebservices hcn=HCNWeb.getHCNWebservicesPort();
						try {
							String rezfxx=hcn.doReSaveFybx(zfxx.toString());
							JSONObject zf=new JSONObject(rezfxx);
							if(zf.optString("code").equals("1")){
								//作废成功后修改NH_BSOFT_JSJL的zfpb状态
								String sql="update NH_BSOFT_JSJL set ZFRQ=sysdate,ZFPB=1 where MZXH=:MZXH";
								Map<String, Object> p = new HashMap<String, Object>();
								p.put("MZXH",Long.parseLong(MZXX.get("MZXH")+""));
								dao.doUpdate(sql, p);
							}else{
								//yx-2017-07-16-农合作废而his没作废的，不抛出异常-b
								if(zf.optString("msg").indexOf("已有退费记录")>0){

									String sql="update NH_BSOFT_JSJL set ZFRQ=sysdate,ZFPB=1 where MZXH=:MZXH";
									Map<String, Object> p = new HashMap<String, Object>();
									p.put("MZXH",Long.parseLong(MZXX.get("MZXH")+""));
									dao.doUpdate(sql, p);
								}else{
									throw new ModelDataOperationException(zf.optString("msg"));
								}
								//yx-2017-07-16-农合作废而his没作废的，不抛出异常-e
							}
						} catch (Exception_Exception e) {
							e.printStackTrace();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					throw new ModelDataOperationException("未获取到农合配置信息，请核对该机构是否有报销权限！");
				}
			}
			//南京金保作废-金保结算表打标记
			String brxz=body.get("BRXZ")==null?"":body.get("BRXZ")+"";
			if(brxz.equals("2000")){
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("MZXH", parameters.get("MZXH"));
				String upsql="update NJJB_JSXX set ZFPB='1' where MZXH=:MZXH";
				dao.doSqlUpdate(upsql, pa);
			}

			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			if (SFQYYFYFY == 1) {
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manaUnitId, ctx);
				StringBuffer hql_cfsb = new StringBuffer();// 查询发票对应的CFSB
				hql_cfsb.append("select CFSB as CFSB from MS_CF01 where JGID=:jgid and MZXH=:mzxh");
				StringBuffer hql_kcdj_delete = new StringBuffer();// 删除对应的CFSB的冻结记录
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_cfsb = new HashMap<String, Object>();
				map_par_cfsb.put("jgid", manaUnitId);
				map_par_cfsb.put("mzxh",
						MedicineUtils.parseLong(MZXX.get("MZXH")));
				List<Map<String, Object>> list_cfsb = dao.doQuery(
						hql_cfsb.toString(), map_par_cfsb);
				if (list_cfsb != null && list_cfsb.size() > 0) {
					for (Map<String, Object> map_cfsb : list_cfsb) {
						Map<String, Object> map_par_delete = new HashMap<String, Object>();
						map_par_delete.put("cfsb",
								MedicineUtils.parseLong(map_cfsb.get("CFSB")));
						dao.doUpdate(hql_kcdj_delete.toString(), map_par_delete);
					}
				}
			}
			//自助机退费处理，调用启航退费接口 zhaojian 2019-05-24
//			if((MZXX.get("FFFS")+"").equals("39") || (MZXX.get("FFFS")+"").equals("40")){
//				new SelfHelpMachineService().doRefund(MZXX, dao);
//			}
			//发票作废后核销家医签约信息 zhaojian 2019-08-31
			cancelSignContractInfo(fphm, manaUnitId, ctx, false);
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
			// 库存冻结代码结束
			res.put("mzxx", MZXX);
		} catch (ValidateException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "作废发票失败");
		} catch (PersistentDataOperationException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "作废发票失败");
		} catch (ServiceException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "作废发票失败");
		}
	}

	/**
	 * 发票作废后核销家医签约信息
	 * zhaojian 2019-08-31
	 *
	 * @param fphm
	 * @param manaUnitId
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	private void cancelSignContractInfo(String fphm, String manaUnitId, Context ctx, boolean isCancel) throws ModelDataOperationException{
		try {
			/****************************发票作废核销****************************/
			if(!isCancel){
				/****签约包退费操作******/
		/*		String fyxh = ParameterUtil.getParameter(manaUnitId, "JYFY", ctx);
				if(!fyxh.equals("null")){
					StringBuffer jyqy_hql = new StringBuffer();
					Map<String , Object> parm = new HashMap<String, Object>();
					jyqy_hql.append("select  b.SBXH as SBXH from MS_YJ01 a, MS_YJ02 b where a.YJXH=b.YJXH and a.FPHM=:FPHM and a.JGID=:JGID and a.ZFPB=0 and b.YLXH=:FYXH and b.ZFPB=0");
					parm.put("FYXH", fyxh);
					parm.put("FPHM",fphm);
					parm.put("JGID",manaUnitId);
					List<Map<String, Object>> jyqy_res = dao.doSqlQuery(jyqy_hql.toString(), parm);
					if (jyqy_res != null && jyqy_res.size() > 0) {
						//该方法成功继续下面的代码，否则报错返回前台即可
						//todo 自行判断是否需要作废
						CheckNoServerDone(jyqy_res);
					}
				}*/
				/****履约服务项退费操作******/
				List<Map<String, Object>> gpRecords = dao.doList(CNDHelper.createSimpleCnd("eq", "fphm", "s", fphm), null, BSPHISEntryNames.SCM_LOG02);
				for (Map<String, Object> gpRecord : gpRecords) {
					Map<String, Object> parm = new HashMap<String, Object>();
					StringBuffer hql = new StringBuffer();
					//核销履约记录
					hql.append("update SCM_NEWSERVICE set deleted=1 where SCIID=:sciid and fphm=:fphm");
					parm.put("fphm", fphm);
					parm.put("sciid", gpRecord.get("sciId"));
					dao.doSqlUpdate(hql.toString(), parm);
					hql.delete(0,hql.length());
					//核销履约次数
					hql.append("update SCM_INCREASEITEMS set SERVICETIMES=SERVICETIMES -:costtimes where SCIID=:sciid");
					parm.remove("fphm");
					parm.put("costtimes", gpRecord.get("costTimes"));
					dao.doSqlUpdate(hql.toString(), parm);
				}
			}
			/****************************取消发票作废核销****************************/
			else{
				/****取消签约包退费操作******/
				/****取消履约服务项退费操作******/
				List<Map<String, Object>> gpRecords = dao.doList(CNDHelper.createSimpleCnd("eq", "fphm", "s", fphm), null, BSPHISEntryNames.SCM_LOG02);
				for (Map<String, Object> gpRecord : gpRecords) {
					Map<String, Object> parm = new HashMap<String, Object>();
					StringBuffer hql = new StringBuffer();
					//核销履约记录
					hql.append("update SCM_NEWSERVICE set deleted=0 where SCIID=:sciid and fphm=:fphm");
					parm.put("fphm", fphm);
					parm.put("sciid", gpRecord.get("sciId"));
					dao.doSqlUpdate(hql.toString(), parm);
					hql.delete(0,hql.length());
					//核销履约次数
					hql.append("update SCM_INCREASEITEMS set SERVICETIMES=SERVICETIMES +:costtimes where SCIID=:sciid");
					parm.remove("fphm");
					parm.put("costtimes", gpRecord.get("costTimes"));
					dao.doSqlUpdate(hql.toString(), parm);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("发票作废后核销家医签约信息失败！", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票作废后核销家医签约信息失败！");
		}
	}

	// @SuppressWarnings("unchecked")
	// public void doUpdateVoidInvoiceSzyb(Map<String, Object> body,
	// Map<String, Object> res, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String uid = user.getUserId();
	// // String manaUnitId = user.getManageUnitId();// 用户的机构ID
	// Map<String, Object> jyqr = (Map<String, Object>) body.get("jyqr");
	// MedicareSYBModel msm = new MedicareSYBModel(dao);
	// msm.doSaveSzYbjyqr("update", jyqr, res, ctx);
	// Map<String, Object> szybzf = (Map<String, Object>) body.get("szybzf");
	// szybzf.put("ZFPB", 1);
	// szybzf.put("ZFRQ", new Date());
	// szybzf.put("ZFGH", uid);
	// msm.saveSzYbMzjsxx("update", szybzf, ctx);
	// }

	/**
	 * 取消发票作废
	 *
	 * @param fphm
	 * @param res
	 * @param ctx
	 */
	public void doUpdateCanceledVoidInvoice(String fphm,
											Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		// String uid = user.getUserId();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.WZSFXMJG, ctx));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		try {
			Map<String, Object> MZXX = dao
					.doLoad("select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ,THPB as THPB from MS_MZXX where FPHM=:FPHM and JGID=:JGID",
							parameters);
			if ("1".equals(MZXX.get("THPB") + "")) {
				// throw new ModelDataOperationException(
				// ServiceCode.CODE_DATABASE_ERROR, "退费发票不可以取消作废!");
				res.put("msg", "退费发票不可以取消作废!");
				return;
			}
			if (0 == Integer.parseInt(MZXX.get("ZFPB") + "")) {
				res.put("msg", "该发票号码已取消作废!");
				return;
			}
			long ll_found = dao.doCount("MS_MZXX",
					"FPHM =:FPHM and JGID=:JGID and THPB = 1", parameters);
			if (ll_found > 0) {
				res.put("msg", "退费发票不可以取消作废!");
				return;
			}
			parameters.remove("FPHM");
			parameters.put("MZXH", MZXX.get("MZXH"));
			dao.doUpdate(
					"update MS_MZXX set ZFPB = 0 Where JGID=:JGID and MZXH=:MZXH",
					parameters);
			long ll_Count = dao.doCount("MS_CF01", "JGID=:JGID and MZXH=:MZXH",
					parameters);
			if (ll_Count > 0) {
				dao.doUpdate(
						"update MS_CF01 set ZFPB = 0 Where JGID=:JGID and MZXH=:MZXH",
						parameters);
				// 库存冻结代码
				int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil
						.getParameter(manaUnitId,
								BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
				if (SFQYYFYFY == 1) {
					// 先删除过期的冻结库存
					//MedicineCommonModel model = new MedicineCommonModel(dao);
					//model.deleteKCDJ(manaUnitId, ctx);
					StringBuffer hql_cfsb = new StringBuffer();// 查询发票对应的CFSB
					hql_cfsb.append("select CFSB as CFSB,YFSB as YFSB from MS_CF01 where JGID=:JGID and MZXH=:MZXH");
					StringBuffer hql_cf02 = new StringBuffer();// 查询cf02记录
					hql_cf02.append("select YPXH as YPXH,YPCD as YPCD,YPSL as YPSL,CFTS as CFTS,YFBZ as YFBZ,SBXH as SBXH from MS_CF02 where CFSB=:cfsb");
					List<Map<String, Object>> list_cfsb = dao.doQuery(
							hql_cfsb.toString(), parameters);
					if (list_cfsb != null && list_cfsb.size() > 0) {
						for (Map<String, Object> map_cfsb : list_cfsb) {
							Map<String, Object> map_par_cfsb = new HashMap<String, Object>();
							map_par_cfsb.put("cfsb", MedicineUtils
									.parseLong(map_cfsb.get("CFSB")));
							List<Map<String, Object>> list_cf02 = dao.doQuery(
									hql_cf02.toString(), map_par_cfsb);
							if (list_cf02 != null && list_cf02.size() > 0) {
								for (Map<String, Object> map_cf02 : list_cf02) {
									Map<String, Object> map_kcdj = new HashMap<String, Object>();
									map_kcdj.put("JGID", manaUnitId);
									map_kcdj.put("YFSB", MedicineUtils
											.parseLong(map_cfsb.get("YFSB")));
									map_kcdj.put("CFSB", MedicineUtils
											.parseLong(map_cfsb.get("CFSB")));
									map_kcdj.put("YPXH", MedicineUtils
											.parseLong(map_cf02.get("YPXH")));
									map_kcdj.put("YPCD", MedicineUtils
											.parseLong(map_cf02.get("YPCD")));
									map_kcdj.put("YPSL", MedicineUtils
											.simpleMultiply(2,
													map_cf02.get("YPSL"),
													map_cf02.get("CFTS")));
									map_kcdj.put("YFBZ", MedicineUtils
											.parseInt(map_cf02.get("YFBZ")));
									map_kcdj.put("DJSJ", new Date());
									map_kcdj.put("JLXH", MedicineUtils
											.parseLong(map_cf02.get("SBXH")));
									dao.doSave(
											"create",
											"phis.application.pha.schemas.YF_KCDJ",
											map_kcdj, false);
								}
							}

						}
					}
				}
				// 库存冻结代码结束
			}
			ll_Count = dao.doCount("MS_YJ01", "JGID=:JGID and MZXH=:MZXH",
					parameters);
			if (ll_Count > 0) {
				dao.doUpdate(
						"update MS_YJ01 set ZFPB = 0 Where JGID=:JGID and MZXH=:MZXH",
						parameters);
				if (wpjfbz == 1 && wzsfxmjg == 1) {
					List<Map<String, Object>> sbxhlist = dao
							.doQuery(
									"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c,WL_CK01 d Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.DJXH=d.DJXH and c.BRLY='1' and d.DJZT<2 and a.JGID=:JGID and a.MZXH=:MZXH",
									parameters);
					for (int i = 0; i < sbxhlist.size(); i++) {
						BSPHISUtil
								.setSuppliesYKSL(
										dao,
										ctx,
										Long.parseLong(sbxhlist.get(i).get(
												"SBXH")
												+ ""), 0);
					}
					Map<String, Object> parametersxhmxupd = new HashMap<String, Object>();
					List<Map<String, Object>> sbxhlistupd = dao
							.doQuery(
									"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.BRLY='1' and a.JGID=:JGID and a.MZXH=:MZXH",
									parameters);
					for (int i = 0; i < sbxhlistupd.size(); i++) {
						parametersxhmxupd.put(
								"MZXH",
								Long.parseLong(sbxhlistupd.get(i).get("SBXH")
										+ ""));
						parametersxhmxupd.put("BRLY", "1");
						dao.doUpdate(
								"update WL_XHMX set ZTBZ=0 where MZXH=:MZXH and BRLY=:BRLY",
								parametersxhmxupd);

					}
				}
			}
			dao.doRemove(MZXX.get("MZXH"), BSPHISEntryNames.MS_ZFFP);
			//取消发票作废后取消核销家医签约信息 zhaojian 2019-08-31
			cancelSignContractInfo(fphm, manaUnitId, ctx, true);
		} catch (PersistentDataOperationException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "作废发票失败");
		} catch (ValidateException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "作废发票失败");
		}
	}

	/**
	 * 结算计算
	 *
	 * @param body
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void doQueryPayment(Map<String, Object> req,
							   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("MRBZ", "1");
			Map<String, Object> fkfs = dao
					.doLoad("select FKFS as FKFS,FKJD as FKJD,SRFS as SRFS from GY_FKFS where MRBZ=:MRBZ and SYLX=1",
							parameters);
			if (fkfs != null) {
				Map<String, Object> jsxx = new HashMap<String, Object>();
				double zjje = 0;
				double zfje = 0;
				if(req.containsKey("SUM01")){
					for (int i = 0; i < body.size(); i++) {
						Map<String, Object> data = body.get(i);
						String HJJE = data.get("HJJE") + "";
						zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE),
								2),2);
					}
					zfje=zjje-BSPHISUtil.getDouble(Double.parseDouble(req.get("SUM01")+ ""),2);
					jsxx.put("JJZF",BSPHISUtil.getDouble(Double.parseDouble(req.get("SUM01")+ ""),2));
				} else if (!req.containsKey("jsxx")) {
					for (int i = 0; i < body.size(); i++) {
						Map<String, Object> data = body.get(i);
						String HJJE = data.get("HJJE")==null?"0": data.get("HJJE")+ "";
						String ZFBL = data.get("ZFBL")==null?"1": data.get("ZFBL")+ "";
						zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE),
								2),2);
						zfje = BSPHISUtil.getDouble(zfje + BSPHISUtil.getDouble(Double.parseDouble(HJJE)
								* Double.parseDouble(ZFBL), 2),2);
					}

				} else {
					Map<String, Object> rjsxx = (Map<String, Object>) req
							.get("jsxx");
					zjje = BSPHISUtil.getDouble(
							Double.parseDouble(rjsxx.get("ZJJE") + ""), 2);
					zfje = BSPHISUtil.getDouble(
							Double.parseDouble(rjsxx.get("ZFJE") + ""), 2);
					jsxx.put("JJZF", BSPHISUtil.getDouble(
							Double.parseDouble(rjsxx.get("JJZF") + ""), 2));
					jsxx.put("ZHZF", BSPHISUtil.getDouble(
							Double.parseDouble(rjsxx.get("ZHZF") + ""), 2));
				}
				double ysk = BSPHISUtil.getDouble(zfje,
						Integer.parseInt(fkfs.get("FKJD") + "") - 1);
				jsxx.put("FKFS", fkfs.get("FKFS"));
				jsxx.put("ZJJE", zjje);
				jsxx.put("ZFJE", zfje);
				jsxx.put("YSK", ysk);
				BSPHISUtil bpu = new BSPHISUtil();
				String fphm = bpu.getNotesNumberNotIncrement(uid, manaUnitId,
						2, dao, ctx);
				jsxx.put("FPHM", fphm);
				int fpzs = doQueryFPZS(body, ctx);
				jsxx.put("FPZS", fpzs);
				String lastFPHM = bpu.getFPHM(uid, manaUnitId, fpzs, dao, ctx);
				jsxx.put("lastFPHM", lastFPHM);
				res.put("body", jsxx);
			} else {
				res.put("body", false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询默认付款方式失败！");
		}
	}

	/**
	 * 收款发票查询
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doQueryReceivablesInvoice(Map<String, Object> req,
										  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
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
		List<Object> cnd = (List<Object>) req.get("cnd");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("JGID", manaUnitId);
			StringBuffer hql = new StringBuffer(
					"select CASE WHEN a.FFFS=32 THEN a.ZPJE ELSE 0 END AS WXJE,CASE WHEN a.FFFS=33 THEN a.ZPJE ELSE 0 END AS ZFBJE,c.CardNo as JZKH,a.CBXZ as CBXZ,a.FPHM as FPHM,a.BRXM as BRXM,a.BRXZ as BRXZ,a.CZGH as YSFY, a.SFFS as SFFS, a.MZLB as MZLB, "
							+ BSPHISUtil.toChar("a.SFRQ", "yyyy-mm-dd hh24:mi")
							+ " as SFRQ,a.XJJE as XJJE,a.ZPJE as ZPJE,a.ZHJE as ZHJE,case a.ZFPB when 1 then '作废' else '' end as ZFPB,a.THPB as THPB,a.FPGL as FPGL,a.FYZH as FYZH,a.ZFJE as ZFJE,a.MZXH as MZXH,a.ZJJE as ZJJE,QTYS as QTYS,HBWC as HBWC,"
							+ BSPHISUtil.toChar("a.JZRQ", "yyyy-mm-dd hh24:mi")
							+ " as JZRQ,"
							+ BSPHISUtil.toChar("a.HZRQ", "yyyy-mm-dd hh24:mi")
							+ " as HZRQ,"
							+ BSPHISUtil.toChar("b.ZFRQ", "yyyy-mm-dd hh24:mi")// 添加两个字段YDCZSF和YDCZFPBD，用于移动出诊
							+ " as ZFRQ,a.JJZFJE as JJZFJE,a.YDCZSF as YDCZSF,a.YDCZFPBD as YDCZFPBD FROM MS_MZXX a left join "
							+ "MS_ZFFP b on (a.MZXH = b.MZXH ),MS_BRDA d left join "
							+ "MPI_Card c on c.cardTypeCode="+KLX+" and c.empiId = d.EMPIID WHERE a.BRID=d.BRID and a.JGID = :JGID ");
			StringBuffer countHql = new StringBuffer(
					"select count(*) as NUM FROM MS_MZXX a left join MS_ZFFP b on (a.MZXH = b.MZXH ),"
							+ "MS_BRDA d left join MPI_Card"
							+ " c on c.cardTypeCode="+KLX+" and c.empiId = d.EMPIID WHERE a.BRID=d.BRID and a.JGID = :JGID ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					ExpressionProcessor exp = new ExpressionProcessor();
					String where = " and " + exp.toString(cnd);
					hql.append(where);
					countHql.append(where);
				}
			} else {
				String where = " and "
						+ BSPHISUtil.toChar("a.SFRQ", "yyyy-mm-dd") + " = '"
						+ BSHISUtil.getDate() + "'";
				hql.append(where);
				countHql.append(where);
			}
			hql.append(" ORDER BY a.SFRQ DESC");
			List<Map<String, Object>> coun = dao.doSqlQuery(
					countHql.toString(), parameters);
			int total = Integer.parseInt(coun.get(0).get("NUM") + "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> inofList = dao.doSqlQuery(hql.toString(),parameters);
			SchemaUtil.setDictionaryMassageForList(inofList,"phis.application.ivc.schemas.MS_MZXX_MZSK");
			parameters.remove("first");
			parameters.remove("max");
			List<Map<String, Object>> inofListall = dao.doSqlQuery(hql.toString(),parameters);
			Map<String, Object> totalbody=new HashMap<String, Object>();
			totalbody.put("ZJJE","0.00");
			totalbody.put("XJJE","0.00");
			totalbody.put("WXJE","0.00");
			totalbody.put("ZFBJE","0.00");
			totalbody.put("ZHJE","0.00");
			totalbody.put("QTYS","0.00");
			totalbody.put("HBWC","0.00");
			for(int i=0;i<inofListall.size();i++){
				Map<String, Object> one=inofListall.get(i);
				if((one.get("ZFPB")+"").equals("作废")){
					continue;
				}
				totalbody.put("ZJJE",Double.parseDouble(totalbody.get("ZJJE")+"")+Double.parseDouble(one.get("ZJJE")+""));
				totalbody.put("XJJE",Double.parseDouble(totalbody.get("XJJE")+"")+Double.parseDouble(one.get("XJJE")+""));
				totalbody.put("WXJE",Double.parseDouble(totalbody.get("WXJE")+"")+Double.parseDouble(one.get("WXJE")+""));
				totalbody.put("ZFBJE",Double.parseDouble(totalbody.get("ZFBJE")+"")+Double.parseDouble(one.get("ZFBJE")+""));
				totalbody.put("ZHJE",Double.parseDouble(totalbody.get("ZHJE")+"")+Double.parseDouble(one.get("ZHJE")+""));
				totalbody.put("QTYS",Double.parseDouble(totalbody.get("QTYS")+"")+Double.parseDouble(one.get("QTYS")+""));
				totalbody.put("HBWC",Double.parseDouble(totalbody.get("HBWC")+"")+Double.parseDouble(one.get("HBWC")+""));
			}
			totalbody.put("ZJJE",BSPHISUtil.getDouble(totalbody.get("ZJJE")+"",2));
			totalbody.put("XJJE",BSPHISUtil.getDouble(totalbody.get("XJJE")+"",2));
			totalbody.put("WXJE",BSPHISUtil.getDouble(totalbody.get("WXJE")+"",2));
			totalbody.put("ZFBJE",BSPHISUtil.getDouble(totalbody.get("ZFBJE")+"",2));
			totalbody.put("ZHJE",BSPHISUtil.getDouble(totalbody.get("ZHJE")+"",2));
			totalbody.put("QTYS",BSPHISUtil.getDouble(totalbody.get("QTYS")+"",2));
			totalbody.put("HBWC",BSPHISUtil.getDouble(totalbody.get("HBWC")+"",2));
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", inofList);
			res.put("totalbody", totalbody);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收款发票查询失败！");
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收款发票查询失败！");
		}
	}

	/**
	 * 收费病人查询
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryGhmx(Map<String, Object> req, Map<String, Object> res,
							Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		int adt_begin = 1;
		String adt_beginStr = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.GHXQ, ctx);
		if (adt_beginStr != "") {
			adt_begin = Integer.parseInt(adt_beginStr);
		}
		try {
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String XQJSFS = ParameterUtil.getParameter(jgid,BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
			Date adt_begin_data = DateUtils.addDays(new Date(), -adt_begin);
			if ("1".equals(XQJSFS)) {
				adt_begin_data = DateUtils.addDays(matter.parse(BSHISUtil.getDate() + " 23:59:59"), -adt_begin);
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("al_jgid", jgid);
			parameters.put("adt_begin", adt_begin_data);
			String sql="SELECT b.BRXM as BRXM,c.cardNo as JZKH,b.MZHM as MZHM,b.BRXZ as BRXZ,a.JZZT as JZZT," +
					" a.KSDM as KSDM,case when (select d.MZXH  from MS_MZXX d where a.SBXH=d.GHGL and rownum=1 ) " +
					" is not null then 1 else 0 end as ISSF,a.YBMC as YBMC " +
					" FROM MS_GHMX a join MS_BRDA b on b.BRID=a.BRID left outer join MPI_Card c on b.EMPIID=c.empiId" +
					" and c.cardTypeCode="+KLX+
					" WHERE (a.GHSJ>=:adt_begin) AND (a.THBZ=0) AND (a.JGID=:al_jgid) order by a.GHSJ desc";
			String sql_where=" (b.BRID =a.BRID) AND (a.GHSJ>=:adt_begin) AND (a.THBZ=0) AND (a.JGID=:al_jgid)";
			List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
			long count = dao.doCount("MS_GHMX a,MS_BRDA b",sql_where, parameters);
			list_ret = dao.doSqlQuery(sql, parameters);
			SchemaUtil.setDictionaryMassageForList(list_ret,"phis.application.ivc.schemas.MS_GHMX_SF");
			res.put("count", count);
			res.put("body", list_ret);
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "待发药列表查询失败");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自动插入
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	// @SuppressWarnings("unchecked")
	public void doSaveZDCR(Map<String, Object> req, Map<String, Object> res,
						   Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		// Map<String, Object> parameters = new HashMap<String, Object>();
		// long BRID = Long.parseLong(body.get("BRID") + "");
		// long KDRQ = 1;
		// String KDRQStr = ParameterUtil.getParameter(manaUnitId,
		// BSPHISSystemArgument.CFXQ, ctx);
		// if (KDRQStr != "") {
		// KDRQ = Long.parseLong(KDRQStr);
		// }
		// parameters.put("BRID", BRID);
		// parameters.put("JGID", manaUnitId);
		// parameters
		// .put("KDRQ",
		// new Date(
		// ((Calendar.getInstance().getTimeInMillis()) - (KDRQ * 24 * 60 * 60 *
		// 1000))));
		Map<String, Object> GY_YLMXparameters = new HashMap<String, Object>();
		GY_YLMXparameters.put("JGID", manaUnitId);
		try {
			// long count = dao
			// .doCount(
			// "MS_YJ01",
			// "JGID =:JGID and BRID =:BRID and ( FPHM is null or FPHM = '' ) and ZFPB = 0 and ZXPB = 0 and ( CFBZ is null or CFBZ <> 2 ) and ( KDRQ >:KDRQ)  and ( DJLY <> 7 OR DJLY IS NULL)",
			// parameters);
			// if (count > 0 && "no".equals(body.get("msg") + "")) {
			// List<Map<String, Object>> list_YJXH = dao
			// .doQuery(
			// "select a.YJXH as YJXH from MS_YJ01 a where a.JGID =:JGID and a.BRID =:BRID and ( a.FPHM is null or a.FPHM = '' ) and a.ZFPB = 0 and a.ZXPB = 0 and ( a.CFBZ is null or a.CFBZ <> 2 ) and ( a.KDRQ >:KDRQ)  and ( a.DJLY <> 7 OR a.DJLY IS NULL)",
			// parameters);
			// List<Map<String, Object>> list_YLXH = new ArrayList<Map<String,
			// Object>>();
			// for (int i = 0; i < list_YJXH.size(); i++) {
			// Map<String, Object> YJXH = list_YJXH.get(i);
			// list_YLXH
			// .addAll(dao
			// .doQuery(
			// "select YLXH as YLXH from MS_YJ02 where YJXH=:YJXH",
			// YJXH));
			// }
			// count = dao.doCount("GY_YLMX",
			// "ZDCR=1 and JGID = :JGID and ZFPB = 0",
			// GY_YLMXparameters);
			// if (count > 0) {
			// List<Object> alreadFYXH = new ArrayList<Object>();
			// String alreadFYMC = "";
			// List<Map<String, Object>> reslist_FYXH = new
			// ArrayList<Map<String, Object>>();
			// List<Map<String, Object>> list_FYXH = dao
			// .doQuery("select b.FYMC as YPMC,b.FYDW as YFDW,a.FYXH as YPXH,a.FYDJ as YLDJ,round(a.FYDJ,2) as LSJG,a.FYKS as FYKS,0 as TYPE,1 as YLSL,b.FYGB as FYGB,nvl(c.MCSX,c.SFMC) as GBMC from GY_YLMX a,GY_YLSF b,GY_SFXM c where c.SFXM = a.FYGB and a.FYXH = b.FYXH and a.ZDCR=1 and a.JGID = :JGID and a.ZFPB = 0",
			// GY_YLMXparameters);
			// boolean same = false;
			// boolean msg = false;
			// for (int i = 0; i < list_FYXH.size(); i++) {
			// Map<String, Object> FYXH = list_FYXH.get(i);
			// same = false;
			// for (int j = 0; j < list_YLXH.size(); j++) {
			// Map<String, Object> YLXH = list_YLXH.get(j);
			// if ((YLXH.get("YLXH") + "").equals(FYXH.get("YLXH")
			// + "")) {
			// same = true;
			// reslist_FYXH.add(FYXH);
			// alreadFYXH.add(YLXH.get("YLXH"));
			// alreadFYMC += FYXH.get("FYMC") + ",";
			// break;
			// }
			// }
			// if (!same) {
			// msg = true;
			// }
			// }
			// if (msg) {
			// Map<String, Object> YJXH = doSaveMS_YJ01(body, ctx);
			// doSaveMS_YJ02(alreadFYXH, list_FYXH, YJXH, body, res,
			// ctx);
			// if (alreadFYXH.size() > 0) {
			// res.put("msg",
			// alreadFYMC.substring(0,
			// alreadFYMC.length() - 1)
			// + "已存在,是否继续插入?");
			// res.put("list_FYXH", reslist_FYXH);
			// res.put("YJXH", YJXH);
			// res.put("body", body);
			// }
			// } else {
			// if (alreadFYXH.size() > 0) {
			// res.put("msg",
			// alreadFYMC.substring(0,
			// alreadFYMC.length() - 1)
			// + "已存在,是否继续插入?");
			// res.put("list_FYXH", reslist_FYXH);
			// res.put("body", body);
			// }
			// }
			// }
			// } else {
			long count = dao.doCount("GY_YLMX",
					"ZDCR=1 and JGID = :JGID and ZFPB = 0", GY_YLMXparameters);
			if (count > 0) {
				List<Map<String, Object>> list_FYXH = dao
						.doQuery(
								"select a.FYXH as YPXH,b.FYMC as YPMC,b.FYDW as YFDW,0 as TYPE,b.XMLX,round(a.FYDJ,2) as LSJG,b.FYGB as FYGB,a.FYKS as FYKS,nvl(c.MCSX,c.SFMC) as GBMC from GY_YLMX a,GY_YLSF b,GY_SFXM c where c.SFXM = b.FYGB and a.FYXH = b.FYXH and a.ZDCR=1 and a.JGID = :JGID and a.ZFPB = 0",
								GY_YLMXparameters);
				res.put("body", list_FYXH);
				// Map<String, Object> YJXH = doSaveMS_YJ01(body, ctx);
				// res.put("YJXH", YJXH);
				// doSaveMS_YJ02(null, list_FYXH, YJXH, body, res, ctx);
			}
			// }
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自动插入收费项目失败");
		}
	}

	public Map<String, Object> doSaveMS_YJ01(Map<String, Object> body,
											 Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Long KSDM = user.getProperty("biz_departmentId", Long.class);
		// Long KSDM = Long.parseLong(user.getProperty("biz_departmentId") +
		// "");
		String userId = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> MS_YJ01 = new HashMap<String, Object>();
			MS_YJ01.put("JGID", manaUnitId);
			MS_YJ01.put("BRID", body.get("BRID"));
			MS_YJ01.put("BRXM", body.get("BRXM"));
			MS_YJ01.put("KDRQ", new Date());
			MS_YJ01.put("KSDM", KSDM);
			MS_YJ01.put("YSDM", userId);
			MS_YJ01.put("ZXKS", 0);
			MS_YJ01.put("ZXPB", 0);
			MS_YJ01.put("ZFPB", 0);
			MS_YJ01.put("CFBZ", 0);
			MS_YJ01.put("JZXH", 0);
			MS_YJ01.put("DJZT", 0);
			MS_YJ01.put("DJLY", 6);
			return dao.doSave("create", BSPHISEntryNames.MS_YJ01_CIC, MS_YJ01,
					false);
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自动插入收费项目失败");
		} catch (ValidateException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自动插入收费项目失败");
		}
	}

	public void doSaveMS_YJ02(List<Object> alreadFYXH,
							  List<Map<String, Object>> list_FYXH, Map<String, Object> YJXH,
							  Map<String, Object> body, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		if (YJXH == null) {
			YJXH = doSaveMS_YJ01(body, ctx);
		}
		res.put("YJXH", YJXH);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		try {
			if (alreadFYXH == null) {
				for (int i = 0; i < list_FYXH.size(); i++) {
					Map<String, Object> MS_YJ02 = list_FYXH.get(i);
					MS_YJ02.put("YJXH", YJXH.get("YJXH"));
					MS_YJ02.put("JGID", manaUnitId);
					MS_YJ02.put("XMLX", 0);
					if (i == 0) {
						MS_YJ02.put("YJZX", 1);
					} else {
						MS_YJ02.put("YJZX", 0);
					}
					Map<String, Object> forZFBL = new HashMap<String, Object>();
					forZFBL.put("BRXZ", body.get("BRXZ"));
					forZFBL.put("FYXH", MS_YJ02.get("YLXH"));
					forZFBL.put("FYGB", MS_YJ02.get("FYGB"));
					forZFBL.put("TYPE", 0);
					MS_YJ02.put(
							"ZFBL",
							BSPHISUtil.getPayProportion(forZFBL, ctx, dao).get(
									"ZFBL"));
					dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC, MS_YJ02,
							false);
				}
			} else {
				for (int i = 0; i < list_FYXH.size(); i++) {
					Map<String, Object> MS_YJ02 = list_FYXH.get(i);
					boolean same = false;
					for (int j = 0; j < alreadFYXH.size(); j++) {
						if ((MS_YJ02.get("YLXH") + "").equals(alreadFYXH.get(j)
								+ "")) {
							same = true;
							break;
						}
					}
					if (same) {
						continue;
					}
					MS_YJ02.put("YJXH", YJXH.get("YJXH"));
					MS_YJ02.put("JGID", manaUnitId);
					MS_YJ02.put("XMLX", 0);
					if (i == 0) {
						MS_YJ02.put("YJZX", 1);
					} else {
						MS_YJ02.put("YJZX", 0);
					}
					Map<String, Object> forZFBL = new HashMap<String, Object>();
					forZFBL.put("BRXZ", body.get("BRXZ"));
					forZFBL.put("FYXH", MS_YJ02.get("YLXH"));
					forZFBL.put("FYGB", MS_YJ02.get("FYGB"));
					forZFBL.put("TYPE", 0);
					MS_YJ02.put(
							"ZFBL",
							BSPHISUtil.getPayProportion(forZFBL, ctx, dao).get(
									"ZFBL"));
					dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC, MS_YJ02,
							false);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自动插入收费项目失败");
		} catch (ValidateException e) {
			logger.error("Query failed to go drug list", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "自动插入收费项目失败");
		}
	}

	public void doQueryPayTypes(Map<String, Object> req,
								Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

	}

	// @SuppressWarnings("unchecked")
	// public void doQueryIsNeedVerify(Map<String, Object> body,
	// Map<String, Object> res, BaseDAO dao, Context ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// String JGID = user.getManageUnitId();// 用户的机构ID
	// String brxz = "";
	// int dylb = 0;
	// if (body.get("BRXZ") != null && body.get("BRXZ") != "") {
	// brxz = body.get("BRXZ") + "";
	// }
	// if (body.get("dylb") != null && body.get("dylb") != "") {
	// dylb = parseInt(body.get("dylb") + "");
	// }
	// String SHIYB = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
	// "SHIYB", "0", "市医保病人性质", ctx);
	// String SHENGYB = ParameterUtil.getParameter(
	// ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
	// if (!brxz.equals(SHIYB) && !brxz.equals(SHENGYB)) {
	// return;
	// }
	// try {
	// if (brxz.equals(SHENGYB)) {
	// String SZYB_LXLB = ParameterUtil.getParameter(JGID,
	// "SZYB_LXLB", "41,42,72,31,32,71,51,52,73",
	// "省医保离休病人类别，用逗号间隔的字符串，默认‘41,42,72,31,32,71,51,52,73",
	// ctx);
	// SessionFactory sf = (SessionFactory) AppContextHolder.get()
	// .getBean("qzjSessionFactory");
	// Session ss = sf.openSession();
	// BaseDAO emrDao = new BaseDAO(ctx, ss);
	// List<Map<String, Object>> cforyjList = new ArrayList<Map<String,
	// Object>>();
	// List<Map<String, Object>> spList = new ArrayList<Map<String, Object>>();
	// if (body.containsKey("Ddtails")) {
	// cforyjList = (List<Map<String, Object>>) body
	// .get("Ddtails");
	// }
	// Map<String, Object> sfspMap = new HashMap<String, Object>();
	// Map<String, Object> ypxxMap = new HashMap<String, Object>();
	// for (int i = 0; i < cforyjList.size(); i++) {
	// cforyjList.get(i)
	// .put("YPLX", cforyjList.get(i).get("DJLX"));
	// cforyjList.get(i).put("JZLX", 10);
	// Map<String, Object> ybbmList = GSMedicareUtil.getYBXX(
	// cforyjList.get(i), dao, ctx);
	// String Ls_yplx = null;
	// String Ls_Ybbm = ybbmList.get("YBBM") + "";
	// String Ls_jbbm = ybbmList.get("JBBM") + "";
	// String ls_Akc222 = null;
	// String Ls_xmbm = null;
	// Long ai_yplx = parseLong(cforyjList.get(i).get("DJLX") + "");
	// int Il_sjlx = 0;
	// if (SZYB_LXLB.length() <= 0) {
	// return;
	// }
	// String[] szyb_lxlbStrings = SZYB_LXLB.split(",");
	// for (int j = 0; j < szyb_lxlbStrings.length; j++) {
	// if (dylb == parseInt(szyb_lxlbStrings[j])) {
	// Il_sjlx = 1;
	// break;
	// }
	// }
	// if (Il_sjlx == 0) {
	// if (ai_yplx > 0) { // 离休药品不需审批
	// return;
	// }
	// if (ai_yplx == 0
	// && "F3402".equals(Ls_Ybbm.substring(0, 4)
	// .toUpperCase())
	// || "ZL".equals(Ls_Ybbm.substring(0, 1)
	// .toUpperCase())) {// 离休诊疗项目里的康复和zl不需审批
	// return;
	// }
	// if (ai_yplx == 0) {
	// Ls_yplx = "2";
	// } else {
	// Ls_yplx = "1";
	// }
	// ls_Akc222 = Ls_Ybbm.substring(1, 3);
	// sfspMap.put("Ls_Ybbm", Ls_Ybbm);
	// sfspMap.put("ls_Akc222", ls_Akc222);
	// sfspMap.put("Ls_yplx", Ls_yplx);
	// try {
	// List<Map<String, Object>> sfspList = emrDao
	// .doSqlQuery(
	// "SELECT AKC222 as XMBM from KA26 where (AKC222 = :Ls_Ybbm or AKC222 = :ls_Akc222) And AKC224 = :Ls_yplx",
	// sfspMap);
	// if (sfspList.size() > 0) {
	// Ls_xmbm = sfspList.get(0).get("XMBM") + "";
	// }
	// if (Ls_xmbm != null) {
	// if (Ls_xmbm.trim() != "") {
	// ypxxMap.put("YPXH",
	// cforyjList.get(i).get("YPXH"));
	// if ("1".equals(cforyjList.get(i)
	// .get("DJLX") + "")) {
	// Map<String, Object> jlandjldwMap = dao
	// .doLoad("select YPJL as YPJL,JLDW as JLDW from YK_TYPK where YPXH=:YPXH",
	// ypxxMap);
	// spList.get(i).put("YPJL",
	// jlandjldwMap.get("YPJL") + "");
	// spList.get(i).put("JLDW",
	// jlandjldwMap.get("JLDW") + "");
	// } else {
	// spList.get(i).put("YPJL", 1);
	// spList.get(i).put("JLDW", "次");
	// }
	// spList.add(cforyjList.get(i));
	// spList.get(i).put("JBBM", Ls_jbbm);
	// spList.get(i).put("YBBM", Ls_Ybbm);
	// }
	// }
	// } catch (PersistentDataOperationException e) {
	// throw new ModelDataOperationException("判断是否审批出错!");
	// } finally {
	// ss.close();
	// }
	// }
	// if (spList.size() > 0) {
	// res.put("isNeedVerify", 2);
	// }
	// res.put("list_dj", spList);
	// }
	// } else if (brxz.equals(SHIYB)) {
	// List<Map<String, Object>> djs = (List<Map<String, Object>>) body
	// .get("DJS");
	// String ptbjbz = "";
	// String rylb = "";
	// String SYB_LXLB = ParameterUtil.getParameter(JGID, "SYB_LXLB",
	// "31,32,33,34,3A,3B,3C,3D,3E,3F",
	// "市医保离休病人类别，用逗号间隔的字符串，默认‘31,32,33,34,3A,3B,3C,3D,3E,3F",
	// ctx);
	// String YB_BJGBQY = ParameterUtil.getParameter(JGID,
	// "YB_BJGBQY", "0", "保健干部是否启用，1表示启用，0表示不启用", ctx);
	// List<Map<String, Object>> list_dj = new ArrayList<Map<String, Object>>();
	// // 遍历单据
	// for (Map<String, Object> dj : djs) {
	// // System.out.println("dj:" + dj.toString()
	// // + "--------------------------");
	// if (parseLong(dj.get("DJLX")) == 1) {
	// // 处方
	// Map<String, Object> parametersCFSB = new HashMap<String, Object>();
	// long cfsb = parseLong(dj.get("DJXH"));
	// parametersCFSB.put("CFSB", cfsb);
	// List<Map<String, Object>> list_cf02 = dao
	// .doQuery(
	// "select 1 as DJLX,a.BRID as BRID,b.YPXH as YPXH,b.YPCD as YPCD"
	// + ",b.SBXH as SBXH,b.YPXH as YPXH,c.YPMC as MC,"
	// +
	// "(select YBBM from YB_YPDZ h where b.YPXH=h.YPXH and b.YPCD=h.YPCD and h.YBPB=1) as XMBM"
	// + " from MS_CF02 b,"
	// +
	// " YK_TYPK c,MS_CF01 a  where a.CFSB=b.CFSB and b.YPXH=c.YPXH and a.CFSB=:CFSB",
	// parametersCFSB);
	//
	// // Ls_btbjbz = 医保病人普通保健标志
	// // Ls_rylb = 医保病人人员类别
	// if (list_cf02.size() > 0) {
	// Map<String, Object> parametersBRID = new HashMap<String, Object>();
	// parametersBRID.put("BRID",
	// parseLong(list_cf02.get(0).get("BRID")));
	// List<Map<String, Object>> list_brxx = dao
	// .doQuery(
	// "select YLRYLB as RYLB,PTRYXSBJDYBZ as PTBJBZ "
	// + " from YB_CBRYJBXX where BRID=:BRID",
	// parametersBRID);
	// if (list_brxx.size() > 0) {
	// ptbjbz = list_brxx.get(0).get("PTBJBZ") + "";
	// rylb = list_brxx.get(0).get("RYLB") + "";
	// }
	// if ((ptbjbz.equals("1") || ptbjbz.equals("2")
	// || ptbjbz.equals("3") || ptbjbz.equals("5"))
	// && YB_BJGBQY.equals("1")) {
	// return;
	// }
	// }
	//
	// // 遍历cf02
	// for (Map<String, Object> cf02 : list_cf02) {
	// // 测试-------------------------
	// // System.out.println("cf02:" + cf02.toString()
	// // + "---------------------");
	// // list_dj.add(cf02);
	//
	// long ypxh = parseLong(cf02.get("YPXH"));
	// long ypcd = parseLong(cf02.get("YPCD"));
	//
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put("YPXH", ypxh);
	// parameters.put("YPCD", ypcd);
	// parameters.put("FYRQ", new Date());
	// String sql =
	// "select YBBM as YBBM,YPDJ as YPDJ,SPLX as SPLX,SPLX_SLX as SPLX_SLX,ZWMC as ZWMC"
	// + " from YB_YPDZ "
	// +
	// " where YBPB = 1 and YPXH=:YPXH AND YPCD=:YPCD and KSSJ<=:FYRQ and (ZZSJ>=:FYRQ or ZZSJ is null)";
	//
	// List<Map<String, Object>> list_ypdz = dao.doQuery(
	// sql, parameters);
	// if (list_ypdz.size() == 0) {
	// continue;
	// }
	// int SPLX = parseInt(list_ypdz.get(0).get("SPLX"));
	// int SPLX_SLX = parseInt(list_ypdz.get(0).get(
	// "SPLX_SLX"));
	// String[] syb_lxlbStrings = SYB_LXLB.split(",");
	// for (String str : syb_lxlbStrings) {
	// if (rylb.equals(str)) {
	// SPLX = SPLX_SLX;
	// break;
	// }
	// }
	// if (SPLX == 2) {
	// res.put("isNeedVerify", 1);
	// list_dj.add(cf02);
	// }
	// }
	//
	// } else if (parseLong(dj.get("DJLX")) == 2) {
	// // 处置
	// Map<String, Object> parametersYJXH = new HashMap<String, Object>();
	// long yjxh = parseLong(dj.get("DJXH"));
	// parametersYJXH.put("YJXH", yjxh);
	// List<Map<String, Object>> list_yj02 = dao
	// .doQuery(
	// "select 2 as DJLX,a.BRID as BRID,b.YLXH as YLXH,b.SBXH as SBXH,b.YLXH as YPXH,"
	// +
	// " c.FYMC as MC,(select YBBM from YB_FYDZ g where g.FYXH=b.YLXH and g.YBPB=1) as XMBM"
	// + " from MS_YJ01 a ,MS_YJ02 b,"
	// + " GY_FYBM c where a.YJXH=b.YJXH and b.YLXH=c.FYXH and a.YJXH=:YJXH",
	// parametersYJXH);
	//
	// // Ls_btbjbz = 医保病人普通保健标志
	// // Ls_rylb = 医保病人人员类别
	// if (list_yj02.size() > 0) {
	// Map<String, Object> parametersBRID = new HashMap<String, Object>();
	// parametersBRID.put("BRID",
	// parseLong(list_yj02.get(0).get("BRID")));
	// List<Map<String, Object>> list_brxx = dao
	// .doQuery(
	// "select YLRYLB as RYLB,PTRYXSBJDYBZ as PTBJBZ "
	// + " from YB_CBRYJBXX where BRID=:BRID",
	// parametersBRID);
	// if (list_brxx.size() > 0) {
	// ptbjbz = list_brxx.get(0).get("PTBJBZ") + "";
	// rylb = list_brxx.get(0).get("RYLB") + "";
	// }
	// if ((ptbjbz.equals("1") || ptbjbz.equals("2")
	// || ptbjbz.equals("3") || ptbjbz.equals("5"))
	// && YB_BJGBQY.equals("1")) {
	// return;
	// }
	// }
	//
	// // 遍历yj02
	// for (Map<String, Object> yj02 : list_yj02) {
	//
	// // 测试-----------------------
	// // System.out.println("yj02:" + yj02.toString()
	// // + "---------------------");
	// // list_dj.add(yj02);
	//
	// long ylxh = parseLong(yj02.get("YLXH"));
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put("FYXH", ylxh);
	// parameters.put("FYRQ", new Date());
	// String sql =
	// "select YBBM as YBBM,SPLX as SPLX,SPLX_LX as SPLX_SLX,XMMC as ZWMC"
	// + " from YB_FYDZ "
	// +
	// " where YBPB = 1 and FYXH=:FYXH and KSSJ<=:FYRQ and (ZZSJ>=:FYRQ or ZZSJ is null)";
	//
	// List<Map<String, Object>> list_ypdz = dao.doQuery(
	// sql, parameters);
	// if (list_ypdz.size() == 0) {
	// continue;
	// }
	// int SPLX = parseInt(list_ypdz.get(0).get("SPLX"));
	// int SPLX_SLX = parseInt(list_ypdz.get(0).get(
	// "SPLX_SLX"));
	// String[] syb_lxlbStrings = SYB_LXLB.split(",");
	// for (String str : syb_lxlbStrings) {
	// if (rylb.equals(str)) {
	// SPLX = SPLX_SLX;
	// break;
	// }
	// }
	// if (SPLX == 2) {
	// res.put("isNeedVerify", 1);
	// list_dj.add(yj02);
	// }
	// }
	// }
	// }
	// // 测试
	// // Map<String, Object> map1 = new HashMap<String, Object>();
	// // map1.put("SBXH", 2069);
	// // map1.put("MC", "艾司唑仑片(舒乐安定片)(J)");
	// // map1.put("DJLX", 1);
	// // list_dj.add(map1);
	// // Map<String, Object> map2 = new HashMap<String, Object>();
	// // map2.put("SBXH", 2070);
	// // map2.put("MC", "高点算");
	// // map2.put("DJLX", 1);
	// // list_dj.add(map2);
	// // Map<String, Object> map3 = new HashMap<String, Object>();
	// // map3.put("SBXH", 765);
	// // map3.put("MC", "体检费");
	// // map3.put("DJLX", 2);
	// // list_dj.add(map3);
	// // res.put("isNeedVerify", 1);
	//
	// res.put("list_dj", list_dj);
	// }
	// } catch (PersistentDataOperationException e) {
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * 打印真实发票
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDyzsfp(Map<String, Object> req, Map<String, Object> res,
							 BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> msxx = req.get("body") == null ? null
					: (Map<String, Object>) req.get("body");
			if (msxx != null) {
				UserRoleToken user = UserRoleToken.getCurrent();
				String uid = user.getUserId() + "";
				String manageUnit = user.getManageUnitId();
				String fphm = BSPHISUtil.getNotesNumber(uid, manageUnit, 2,
						dao, ctx);
				if (fphm == null) {
					throw new ModelDataOperationException();
				}
				// StringBuilder sqlBuilder = new StringBuilder();
				// sqlBuilder.append("UPDATE ").append(BSPHISEntryNames.MS_MZXX)
				// .append(" SET SJFP = :SJFP WHERE MZXH = :MZXH");
				// Map<String, Object> map = new HashMap<String, Object>();
				// map.put("SJFP", fphm);
				// map.put("MZXH", String.valueOf(msxx.get("MZXH")));
				// dao.doSqlUpdate(sqlBuilder.toString(), map);// 更新实际发票
				// map = new HashMap<String, Object>();
				// map.put("JGID", manageUnit);
				// map.put("SJFP", fphm);
				// map.put("DYSJ", new Date());
				// map.put("DYGH", uid);
				// map.put("MZLB", Long.parseLong(msxx.get("MZLB") + ""));
				// dao.doSave("create", BSPHISEntryNames.MS_SJFP, map, true);//
				// 保存门诊实际发票
				res.put("body", fphm);
				// MedicareModel mm = new MedicareModel(dao);
				// res.put("BRXZ", mm.queryYbBrxz(ctx));
			}
		} catch (ModelDataOperationException ex) {
			throw new ModelDataOperationException("发票号码未设置或已用完!", ex);
		} catch (Exception ex) {
			throw new ModelDataOperationException("打印真实发票失败!", ex);
		}
	}

	/**
	 * 查询病人性质
	 *
	 * @param body
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public String doCheckPatient(Map<String, Object> body, BaseDAO dao,
								 Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select a.BRXZ as BRXZ from ZY_ZYJS a where a.FPHM = :FPHM and a.JGID = :JGID");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", body.get("fphm"));
		parameters.put("JGID", body.get("jgid"));
		String SHIYB = ParameterUtil.getParameter(body.get("jgid") + "",
				"SHIYB", "0", "市医保病人性质", ctx);

		String SHENGYB = ParameterUtil.getParameter(body.get("jgid") + "",
				"SHENGYB", "0", "省医保病人性质", ctx);
		// System.out.println(SHIYB + "==" + SHENGYB);
		try {
			if (dao.doQuery(hql.toString(), parameters) != null
					&& dao.doQuery(hql.toString(), parameters).size() > 0) {
				Map<String, Object> JSXX = dao.doQuery(hql.toString(),
						parameters).get(0);
				String brxz = JSXX.get("BRXZ") + "";
				// System.out.println(brxz);
				if (brxz.equals(SHIYB)) {
					return "1";
				} else if (brxz.equals(SHENGYB)) {
					return "2";
				} else {
					return "0";
				}

			}
		} catch (Exception e) {
			throw new ModelDataOperationException("病人性质获取失败!", e);
		}
		return "0";
	}

	@SuppressWarnings("unchecked")
	public void doUpdateSPBH(Map<String, Object> body, Map<String, Object> res,
							 BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			// UserRoleToken user = UserRoleToken.getCurrent();
			// String JGID = user.getManageUnitId();// 用户的机构ID
			if (body.containsKey("passed") || body.get("passed") != null) {
				List<Map<String, Object>> list_passed = (List<Map<String, Object>>) body
						.get("passed");
				Map<String, Object> parameters = new HashMap<String, Object>();
				for (Map<String, Object> map : list_passed) {
					long sbxh = parseLong(map.get("SBXH"));
					long spbh = parseLong(map.get("SPBH"));
					int cflx = parseInt(map.get("CFLX"));
					parameters.put("SBXH", sbxh);
					parameters.put("SPBH", spbh);
					if (cflx == 0) {
						dao.doUpdate(
								"update MS_YJ02 set SPBH=:SPBH where SBXH=:SBXH",
								parameters);
					} else {
						dao.doUpdate(
								"update MS_CF02 set SPBH=:SPBH where SBXH=:SBXH",
								parameters);
					}
				}
			}
			if (body.containsKey("notPassed") || body.get("notPassed") != null) {
				List<Map<String, Object>> list_passed = (List<Map<String, Object>>) body
						.get("notPassed");
				Map<String, Object> parameters = new HashMap<String, Object>();
				for (Map<String, Object> map : list_passed) {
					long sbxh = parseLong(map.get("SBXH"));
					// long spbh = parseLong(map.get("SPBH"));
					int cflx = parseInt(map.get("CFLX"));
					parameters.put("SBXH", sbxh);
					// parameters.put("SPBH", spbh);
					if (cflx == 0) {
						dao.doUpdate(
								"update MS_YJ02 set ZFBL=1,ZFPB=2 where SBXH=:SBXH",
								parameters);
					} else {
						dao.doUpdate(
								"update MS_CF02 set ZFBL=1,ZFPB=2 where SBXH=:SBXH",
								parameters);
					}
				}
			}

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	public void doUpdateYdczfpbd(Map<String, Object> body,
								 Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		long mzxh = parseLong(body.get("MZXH"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("MZXH", mzxh);
		try {
			dao.doUpdate("update MS_MZXX set YDCZFPBD=1 where MZXH=:MZXH",
					parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
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

	/**
	 * 退费处理 发票查询
	 *
	 * @param fphm
	 * @param res
	 * @param ctx
	 */
	public void doQueryTFFphm(String fphm, Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FPHM", fphm);
		parameters.put("JGID", manaUnitId);
		// List<Map<String, Object>> djs = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> persons = dao
					.doSqlQuery(
							"select a.BRXM as BRXM,0 as TFJE,b.ZFPB as ZFPB,b.MZXH as MZXH from "
									+ "MS_BRDA a,MS_MZXX b left outer join MS_ZFFP c on b.MZXH=c.MZXH where a.BRID=b.BRID and b.JGID=:JGID and b.FPHM=:FPHM",
							parameters);
			Map<String, Object> person = null;
			if (persons.size() > 0) {
				person = persons.get(0);
				ClinicChargesProcessingService ccps = new ClinicChargesProcessingService();
				ccps.doGetNotesNumber(null, res, dao, ctx);
				person.put("FPHM", res.get("body"));

				List<Map<String, Object>> listDetails = new ArrayList<Map<String, Object>>();
				StringBuffer hql1 = new StringBuffer(
						"select b.ZFYP as ZFYP,a.CFSB as CFSB,a.BRID as BRID,b.YPZH as YPZH,b.SBXH as SBXH, b.YPXH as YPXH, c.YPMC as YPMC,a.CFHM as CFHM, a.KFRQ as KFRQ, a.CFLX as CFLX, a.KSDM as KSDM,a.YSDM as YSDM,a.DJLY as DJLY,b.YPCD as YPCD, b.YFDW as YFDW, b.CFSB as CFSB, b.YFGG as YFGG, b.YPDJ as YPDJ, b.YPSL as YPSL,b.HJJE as HJJE, b.ZFBL as ZFBL, b.YPYF as YPYF,b.FYGB as FYGB, b.CFTS as CFTS,a.DJYBZ as DJYBZ,a.YFSB as YFSB,b.SFJG as SFJG,b.YCSL as YCSL,b.ZFPB as ZFPB from ");
				hql1.append("MS_CF01 a,MS_CF02 b,YK_TYPK c,MS_CF01 d where c.YPXH=b.YPXH and a.CFSB=b.CFSB and ((d.CFSB=a.CFGL and a.DJLY=3 and d.JGID=:JGID and d.FPHM=:FPHM) or (a.CFSB=d.CFSB and a.JGID = :JGID and a.FPHM=:FPHM))");
				List<Map<String, Object>> listDetails1 = dao.doQuery(
						hql1.toString(), parameters);
				listDetails.addAll(listDetails1);
				StringBuffer hql2 = new StringBuffer(
						"select 0 as ZFYP, a.BRID as BRID,a.YJXH as YJXH,b.YJZH as YPZH,b.SBXH as SBXH, b.YLXH as YPXH, c.FYMC as YPMC, a.TJHM as CFHM, a.KDRQ as KFRQ,0 as CFLX, a.KSDM as KSDM,a.YSDM as YSDM,a.DJLY as DJLY, c.FYDW as YFDW, b.YJXH as CFSB,'' as YFGG, b.YLDJ as YPDJ, b.YLSL as YPSL, b.HJJE as HJJE, b.ZFBL as ZFBL,'' as YPYF, b.FYGB as FYGB,1 as CFTS,b.YLSL as YCSL,b.ZFPB as ZFPB from ");
				hql2.append("MS_YJ01 a,MS_YJ02 b,GY_YLSF c where c.FYXH=b.YLXH and a.YJXH=b.YJXH and a.JGID = :JGID and a.FPHM=:FPHM");
				List<Map<String, Object>> listDetails2 = dao.doQuery(
						hql2.toString(), parameters);
				listDetails.addAll(listDetails2);
				res.put("details", listDetails);
				// List<Map<String, Object>> fkxxList = new
				// ArrayList<Map<String, Object>>();
				Map<String, Object> MZXHparameters = new HashMap<String, Object>();
				MZXHparameters.put("JGID", manaUnitId);
				MZXHparameters.put("MZXH",
						Long.parseLong(persons.get(0).get("MZXH") + ""));
				List<Map<String, Object>> fkxxList = dao
						.doQuery(
								"select a.FKFS as FKFS,b.FKMC as FKMC,a.FKJE as FKJE from MS_FKXX a,GY_FKFS b where a.MZXH=:MZXH and a.JGID=:JGID and a.FKFS=b.FKFS and b.SYLX=1 order by a.JLXH desc",
								MZXHparameters);
				res.put("fkxxs", fkxxList);
				// long FKFS = Long.parseLong(fkxxList.get(0).get("FKFS")+"");
				// Map<String, Object> fkfsparameters = new HashMap<String,
				// Object>();
				// fkfsparameters.put("FKFS", FKFS);
				// Map<String, Object> fkfs = dao
				// .doLoad("select FKFS as FKFS,FKJD as FKJD,SRFS as SRFS from GY_FKFS where FKFS=:FKFS and SYLX=1",
				// fkfsparameters);
				Map<String, Object> MZXX = dao.doLoad(BSPHISEntryNames.MS_MZXX,
						Long.parseLong(persons.get(0).get("MZXH") + ""));
				MZXX.put("FKFS",
						Long.parseLong(fkxxList.get(0).get("FKFS") + ""));
				// MZXX.put(key, value)
				res.put("MZXX", MZXX);
			}
			res.put("body", person);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to copy invoice number.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票号码查询失败");
		} catch (ServiceException e) {
			logger.error("Failed to copy invoice number.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "发票号码查询失败");
		}

	}

	/**
	 * 单据明细查询
	 *
	 * @param bRID
	 * @param res
	 */
	public void doQueryTF01(Map<String, Object> req, Map<String, Object> res,
							BaseDAO dao, Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String fphm = (String) req.get("fphm");
		try {
			StringBuffer sql = new StringBuffer(
					"select a.CFSB as CFSB,a.CFLX as CFLX,case a.CFLX when 1 then '西药方:' when 2 then '中药方:' else '草药方:' end as DJLX_text,'' as BZXX,a.FYBZ as FYBZ,a.ZFPB as ZFPB,a.DJLY as DJLY,a.YSDM as YSDM,a.KSDM as KSDM,a.CFTS as CFTS,a.FPHM as FPHM from MS_CF01 a,MS_CF01 b where (b.CFSB=a.CFGL and a.DJLY=3 and b.JGID=:jgid and b.FPHM=:fphm) or (a.CFSB=b.CFSB and a.JGID=:jgid and a.FPHM=:fphm)");
			sql.append(" union all select a.YJXH as CFSB,0 as CFLX,'检查单 :' as DJLX_text,'' as BZXX,a.ZXPB as FYBZ,a.ZFPB as ZFPB,a.DJLY as DJLY,a.YSDM as YSDM,a.KSDM as KSDM,1 as CFTS,a.FPHM as FPHM from MS_YJ01 a where a.JGID=:jgid and a.FPHM=:fphm");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", manaUnitId);
			parameters.put("fphm", fphm);
			List<Map<String, Object>> list01 = dao.doSqlQuery(sql.toString(),
					parameters);
			for (int i = 0; i < list01.size(); i++) {
				Map<String, Object> map = list01.get(i);
				Map<String, Object> parameters_ys = new HashMap<String, Object>();
				if (map.get("YSDM") != null) {
					parameters_ys.put("PERSONID", map.get("YSDM"));
					map.putAll(dao
							.doLoad("select PERSONNAME as YSMC from SYS_Personnel where PERSONID=:PERSONID",
									parameters_ys));
				}
				Map<String, Object> parameters_ks = new HashMap<String, Object>();
				if (map.get("KSDM") != null) {
					parameters_ks.put("ID",
							Long.parseLong(map.get("KSDM") + ""));
					map.putAll(dao
							.doLoad("select OFFICENAME as KSMC from SYS_Office where ID=:ID",
									parameters_ks));
				}
				if ("0".equals(map.get("CFLX") + "")) {
					if ("1".equals(map.get("ZFPB") + "")) {
						map.put("BZXX", "已作废");
					} else if ("1".equals(map.get("FYBZ") + "")) {
						map.put("BZXX", "已执行");
					}
				} else {
					if ("1".equals(map.get("ZFPB") + "")) {
						map.put("BZXX", "已退药");
					} else if ("3".equals(map.get("DJLY") + "")
							&& map.get("FPHM") == null) {
						map.put("BZXX", "新处方");
					} else if ("1".equals(map.get("FYBZ") + "")) {
						map.put("BZXX", "已发药");
					}
				}
			}
			res.put("body", list01);
		} catch (PersistentDataOperationException e) {
			logger.error("Prescribing a single query to fail.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "单据查询失败");
		}
	}

	/**
	 * 退费结算计算
	 *
	 * @param body
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void doQueryTFPayment(Map<String, Object> req,
								 Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		Map<String, Object> MZXX = (Map<String, Object>) req.get("MZXX");
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			parameters.put("FKFS", Long.parseLong(MZXX.get("FKFS") + ""));
			Map<String, Object> fkfs = dao
					.doLoad("select FKFS as FKFS,FKJD as FKJD,SRFS as SRFS,FKMC as FKMC from GY_FKFS where FKFS=:FKFS and SYLX=1",
							parameters);
			Map<String, Object> jsxx = new HashMap<String, Object>();
			// jsxx.put("JJZF", "0.00");
			// jsxx.put("ZHZF", "0.00");
			double zjje = 0;
			double zfje = 0;
			if (!req.containsKey("jsxx")) {
				for (int i = 0; i < body.size(); i++) {
					Map<String, Object> data = body.get(i);
					String HJJE = data.get("HJJE") + "";
					String ZFBL = data.get("ZFBL") + "";
					zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE), 2), 2);
					zfje = BSPHISUtil.getDouble(zfje + BSPHISUtil.getDouble(Double.parseDouble(HJJE)
							* Double.parseDouble(ZFBL), 2), 2);
				}
			} else {
				Map<String, Object> rjsxx = (Map<String, Object>) req
						.get("jsxx");
				zjje = BSPHISUtil.getDouble(
						Double.parseDouble(rjsxx.get("ZJJE") + ""), 2);
				zfje = BSPHISUtil.getDouble(
						Double.parseDouble(rjsxx.get("ZFJE") + ""), 2);
				jsxx.put(
						"JJZF",
						BSPHISUtil.getDouble(
								Double.parseDouble(rjsxx.get("JJZF") + ""), 2));
				jsxx.put(
						"ZHZF",
						BSPHISUtil.getDouble(
								Double.parseDouble(rjsxx.get("ZHZF") + ""), 2));
			}
			double ysk = BSPHISUtil.getDouble(zfje,
					Integer.parseInt(fkfs.get("FKJD") + "") - 1);
			jsxx.put("FKFS", fkfs.get("FKFS"));
			jsxx.put("FKMC", fkfs.get("FKMC"));
			jsxx.put("ZJJE", zjje);
			jsxx.put("ZFJE", zfje);
			jsxx.put("YSK", ysk);
			BSPHISUtil bpu = new BSPHISUtil();
			String fphm = bpu.getNotesNumberNotIncrement(uid, manaUnitId, 2,
					dao, ctx);
			jsxx.put("FPHM", fphm);
			int fpzs = doQueryFPZS(body, ctx);
			jsxx.put("FPZS", fpzs);
			String lastFPHM = bpu.getFPHM(uid, manaUnitId, fpzs, dao, ctx);
			jsxx.put("lastFPHM", lastFPHM);
			res.put("body", jsxx);
			// 前台显示数据
			double tfje = BSPHISUtil.getDouble(
					BSPHISUtil.getDouble(MZXX.get("ZFJE"), 2) - zfje, 2);
			Map<String, Object> showjs = new HashMap<String, Object>();
			showjs.put("TFJE", tfje);
			double qtysk = BSPHISUtil.getDouble(-tfje,
					Integer.parseInt(fkfs.get("FKJD") + "") - 1);
			showjs.put("YSK", qtysk);
			res.put("showjs", showjs);
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算计算失败！");
		}
	}

	/**
	 * 退费结算
	 *
	 * @param body
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void doSaveRefundSettle(Map<String, Object> req,
								   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException, PersistentDataOperationException {

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> datas = (List<Map<String, Object>>) body
				.get("data");
		Map<String, Object> MZXX = (Map<String, Object>) body.get("MZXX");
		String yfphm = MZXX.get("FPHM") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getUserId() + "";
		String manaUnitId = user.getManageUnitId();// 用户的机构ID

		int wpjfbz = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.MZWPJFBZ, ctx));
		int wzsfxmjg = Integer.parseInt("".equals(ParameterUtil.getParameter(
				manaUnitId, BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
				: ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.WZSFXMJG, ctx));
		try {
			if (wpjfbz == 1) {
				if (wzsfxmjg == 1) {
					List<Map<String, Object>> TFXX_list = (List<Map<String, Object>>) MZXX
							.get("TFXX");
					// List<Object> ckdj_list = new ArrayList<Object>();
					// List<Map<String, Object>> cf02_list = new
					// ArrayList<Map<String, Object>>();
					for (int i = 0; i < TFXX_list.size(); i++) {
						Map<String, Object> TFXX = TFXX_list.get(i);
						Map<String, Object> parameters_map = new HashMap<String, Object>();
						parameters_map.put("CFSB",
								Long.parseLong(TFXX.get("CFSB") + ""));
						if ("0".equals(TFXX.get("CFLX") + "")) {
							StringBuffer hql_count = new StringBuffer();
							hql_count
									.append("select count(*) as NUM from wl_ck02 e, wl_ck01 d where d.djzt =2 and  d.djxh =e.djxh and d.djxh in (SELECT  distinct DJXH FROM WL_XHMX a,MS_YJ01 c,MS_YJ02 d WHERE a.MZXH=d.SBXH and d.YJXH=c.YJXH and c.YJXH=:yjxh and DJXH IS NOT NULL AND DJXH > 0 AND ZTBZ = 1)");
							Map<String, Object> map_par = new HashMap<String, Object>();
							map_par.put("yjxh", TFXX.get("CFSB"));
							List<Map<String, Object>> list_count = dao
									.doSqlQuery(hql_count.toString(), map_par);
							if (list_count != null && list_count.size() > 0) {
								long l = parseLong(list_count.get(0).get("NUM"));
								if (l > 0) {
									hql_count = new StringBuffer();
									hql_count
											.append("select count(*) as NUM from wl_ck02 e, wl_ck01 d where d.djzt =2 and  d.djxh =e.djxh and d.thdj in (SELECT  distinct DJXH FROM WL_XHMX a,MS_YJ01 c,MS_YJ02 d WHERE a.MZXH=d.SBXH and d.YJXH=c.YJXH and c.YJXH=:yjxh and DJXH IS NOT NULL AND DJXH > 0 AND ZTBZ = 1)");
									long ll = parseLong(dao
											.doSqlQuery(hql_count.toString(),
													map_par).get(0).get("NUM"));
									if (l != ll) {
										throw new ModelDataOperationException(
												ServiceCode.CODE_DATABASE_ERROR,
												"有检查单物资已出库,不能退费，请重新选择后再退!");
									}
								}
							}
						}
					}
				}
			}
			if (datas.size() > 0) {
				List<Map<String, Object>> FZXX_list = (List<Map<String, Object>>) MZXX
						.get("FZXX");
				List<Object> ckdj_list = new ArrayList<Object>();
				List<Map<String, Object>> cf02_list = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < FZXX_list.size(); i++) {
					Map<String, Object> FZXX = FZXX_list.get(i);
					Map<String, Object> parameters_map = new HashMap<String, Object>();
					parameters_map.put("CFSB",
							Long.parseLong(FZXX.get("CFSB") + ""));
					if ("0".equals(FZXX.get("CFLX") + "")) {
						Map<String, Object> MS_YJ01 = dao.doLoad(
								BSPHISEntryNames.MS_YJ01_CIC, FZXX.get("CFSB"));
						Map<String, Object> YJXH = dao.doSave("create",
								BSPHISEntryNames.MS_YJ01_CIC, MS_YJ01, false);
						List<Object> cnd1 = CNDHelper.createSimpleCnd("eq",
								"a.YJXH", "i",
								Long.parseLong(FZXX.get("CFSB") + ""));
						List<Map<String, Object>> MS_YJ02_list = dao.doList(
								cnd1, "SBXH", BSPHISEntryNames.MS_YJ02_CIC);
						for (int j = 0; j < MS_YJ02_list.size(); j++) {
							Map<String, Object> MS_YJ02 = MS_YJ02_list.get(j);
							MS_YJ02.put("YJXH",
									Long.parseLong(YJXH.get("YJXH") + ""));
							dao.doSave("create", BSPHISEntryNames.MS_YJ02_CIC,
									MS_YJ02, false);
						}
						dao.doUpdate(
								"update MS_YJ01 set FPHM=null,MZXH=null where YJXH = :CFSB",
								parameters_map);
					} else {
						Map<String, Object> MS_CF01 = dao.doLoad(
								BSPHISEntryNames.MS_CF01, FZXX.get("CFSB"));
						Map<String, Object> CFSB = dao.doSave("create",
								BSPHISEntryNames.MS_CF01, MS_CF01, false);
						List<Object> cnd1 = CNDHelper.createSimpleCnd("eq",
								"CFSB", "i",
								Long.parseLong(FZXX.get("CFSB") + ""));
						List<Map<String, Object>> MS_CF02_list = dao.doList(
								cnd1, "SBXH", BSPHISEntryNames.MS_CF02_CIC);
						for (int j = 0; j < MS_CF02_list.size(); j++) {
							Map<String, Object> MS_CF02 = MS_CF02_list.get(j);
							MS_CF02.put("CFSB",
									Long.parseLong(CFSB.get("CFSB") + ""));
							ckdj_list
									.add(Long.parseLong(CFSB.get("CFSB") + ""));
							cf02_list.add(MS_CF02);
							dao.doSave("create", BSPHISEntryNames.MS_CF02_CIC,
									MS_CF02, false);
						}
						dao.doUpdate(
								"update MS_CF01 set FPHM=null,MZXH=null where CFSB = :CFSB",
								parameters_map);
					}
				}

				// 库存冻结代码
				int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil
						.getParameter(manaUnitId,
								BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
				int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil
						.getParameter(manaUnitId,
								BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
				if (SFQYYFYFY == 1 && MZKCDJSJ == 1) {// 如果启用库存冻结,并且在开单时冻结
					// 先删除过期的冻结库存
					//MedicineCommonModel model = new MedicineCommonModel(dao);
					//model.deleteKCDJ(manaUnitId, ctx);
					// 先删除对应CFSB的冻结记录
					StringBuffer hql_kcdj_delete = new StringBuffer();
					hql_kcdj_delete
							.append("delete from YF_KCDJ where CFSB=:cfsb");
					Map<String, Object> map_par_delete = new HashMap<String, Object>();
					for (int i = 0; i < ckdj_list.size(); i++) {
						long djcfsb = Long.parseLong(ckdj_list.get(i) + "");
						map_par_delete.put("cfsb", djcfsb);
						dao.doSqlUpdate(hql_kcdj_delete.toString(),
								map_par_delete);
						for (Map<String, Object> map_cf02 : cf02_list) {
							if (djcfsb == Long.parseLong(map_cf02.get("CFSB")
									+ "")) {

								// if (map_cf02.get("_opStatus") != null
								// &&
								// map_cf02.get("_opStatus").equals("remove")) {
								// continue;
								// }
								Map<String, Object> map_kcdj = new HashMap<String, Object>();
								map_kcdj.put("JGID", manaUnitId);
								map_kcdj.put("YFSB", MedicineUtils
										.parseLong(map_cf02.get("YFSB")));
								map_kcdj.put("CFSB", MedicineUtils
										.parseLong(map_cf02.get("CFSB")));
								map_kcdj.put("YPXH", MedicineUtils
										.parseLong(map_cf02.get("YPXH")));
								map_kcdj.put("YPCD", MedicineUtils
										.parseLong(map_cf02.get("YPCD")));
								map_kcdj.put("YPSL", MedicineUtils
										.simpleMultiply(2,
												map_cf02.get("YPSL"),
												map_cf02.get("CFTS")));
								map_kcdj.put("YFBZ", MedicineUtils
										.parseInt(map_cf02.get("YFBZ")));
								map_kcdj.put("DJSJ", new Date());
								map_kcdj.put("JLXH", MedicineUtils
										.parseLong(map_cf02.get("SBXH")));
								dao.doSave("create",
										"phis.application.pha.schemas.YF_KCDJ",
										map_kcdj, false);
							}
						}
					}
				}
				// 结算

				BSPHISUtil bpu = new BSPHISUtil();
				Date date = new Date();
				Map<String, Object> MS_MZXX = (Map<String, Object>) body
						.get("MZXX");
				double id_zjje = Double.parseDouble(MS_MZXX.get("ZJJE") + "");
				double id_zfje = Double.parseDouble(MS_MZXX.get("ZFJE") + "");
				double id_xjje = Double.parseDouble(MS_MZXX.get("YSK") + "");
				double id_hbwc = BSPHISUtil.getDouble(id_zfje - id_xjje, 2);
				double id_jkje = Double.parseDouble(MS_MZXX.get("JKJE") + "");
				double id_tzje = BSPHISUtil.getDouble(id_jkje - id_xjje, 2);
				MS_MZXX.put("XJJE", id_xjje);// 现金金额
				MS_MZXX.put("ZPJE", 0d);

				MS_MZXX.put("QTYS", BSPHISUtil.getDouble(id_zjje - id_zfje, 2));
				MS_MZXX.put("ZHJE", 0d);
				MS_MZXX.put("ZJJE", id_zjje);
				MS_MZXX.put("ZFJE", id_zfje);
				MS_MZXX.put("MZGL", 0);
				MS_MZXX.put("JGID", manaUnitId);// 机构ID
				MS_MZXX.put("HBWC", id_hbwc);// 货币误差
				MS_MZXX.put("ZFPB", 0);// 作废判别
				MS_MZXX.put("THPB", 0);// 退号判别

				MS_MZXX.put("SFFS", 0);// 收费方式
				MS_MZXX.put("HBBZ", 0);// 合并标志
				MS_MZXX.put("SFRQ", date);// 收费日期
				MS_MZXX.put("CZGH", uid);
				MS_MZXX.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
				MS_MZXX.put("TZJE", id_tzje);
				String fphm = bpu.getNotesNumber(uid, manaUnitId, 2, dao, ctx);
				MS_MZXX.put("FPHM", fphm);
				MS_MZXX.remove("JZRQ");
				MS_MZXX.remove("HZRQ");
				res.put("FPHM", fphm);
				Map<String, Object> mzxh = dao.doSave("create",
						BSPHISEntryNames.MS_MZXX, MS_MZXX, false);
				int fpzs = Integer.parseInt(MS_MZXX.get("FPZS") + "");
				for (int i = 0; i < fpzs - 1; i++) {
					bpu.getNotesNumber(uid, manaUnitId, 2, dao, ctx);
				}
				Set<Long> fygbs = new HashSet<Long>();
				Set<Long> cfsbs = new HashSet<Long>();
				Set<Long> yjxhs = new HashSet<Long>();
				Map<Long, List<Long>> cfsb_xy = new HashMap<Long, List<Long>>();
				Map<Long, List<Long>> cfsb_zy = new HashMap<Long, List<Long>>();
				Map<Long, List<Long>> cfsb_cy = new HashMap<Long, List<Long>>();
				// 药房允许切换时
				Set<Long> fyyf_xy = new HashSet<Long>();// 西药
				Set<Long> fyyf_zy = new HashSet<Long>();// 中药
				Set<Long> fyyf_cy = new HashSet<Long>();// 草药
				for (int i = 0; i < datas.size(); i++) {
					Map<String, Object> data = datas.get(i);
					fygbs.add(Long.parseLong(data.get("FYGB") + ""));
					if ("0".equals(data.get("CFLX") + "")) {
						if (data.containsKey("YJXH")) {
							yjxhs.add(Long.parseLong(data.get("YJXH") + ""));
						} else {
							yjxhs.add(Long.parseLong(data.get("CFSB") + ""));
						}
					} else {
						int cfs = cfsbs.size();
						cfsbs.add(Long.parseLong(data.get("CFSB") + ""));
						if (cfs != cfsbs.size()) {
							Long yfsb = Long.parseLong(data.get("YFSB") + "");
							if ("1".equals(data.get("CFLX") + "")) {// 以前是对比YS_MZ_FYYF_XY来判断类型，现在改为CFLX
								// ll_xyfzs += 1;
								fyyf_xy.add(yfsb);
								if (cfsb_xy.containsKey(yfsb)) {
									cfsb_xy.get(yfsb).add(
											Long.parseLong(data.get("CFSB")
													+ ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB")
											+ ""));
									cfsb_xy.put(yfsb, list);
								}
							} else if ("2".equals(data.get("CFLX") + "")) {
								fyyf_zy.add(yfsb);
								if (cfsb_zy.containsKey(yfsb)) {
									cfsb_zy.get(yfsb).add(
											Long.parseLong(data.get("CFSB")
													+ ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB")
											+ ""));
									cfsb_zy.put(yfsb, list);
								}
							} else if ("3".equals(data.get("CFLX") + "")) {
								fyyf_cy.add(yfsb);
								if (cfsb_cy.containsKey(yfsb)) {
									cfsb_cy.get(yfsb).add(
											Long.parseLong(data.get("CFSB")
													+ ""));
								} else {
									List<Long> list = new ArrayList<Long>();
									list.add(Long.parseLong(data.get("CFSB")
											+ ""));
									cfsb_cy.put(yfsb, list);
								}
							}
						}
					}
				}
				Iterator<Long> it = fygbs.iterator();
				while (it.hasNext()) {
					long fygb = it.next();
					Map<String, Object> sfmx = new HashMap<String, Object>();
					sfmx.put("MZXH", mzxh.get("MZXH"));
					sfmx.put("JGID", manaUnitId);
					sfmx.put("SFXM", fygb);
					sfmx.put("FPHM", MS_MZXX.get("FPHM"));
					double zjje = 0;
					double zfje = 0;
					for (int i = 0; i < datas.size(); i++) {
						Map<String, Object> data = datas.get(i);
						if (fygb == Long.parseLong(data.get("FYGB") + "")) {
							String HJJE = data.get("HJJE") + "";
							String ZFBL = data.get("ZFBL") + "";
							zjje = BSPHISUtil.getDouble(zjje + BSPHISUtil.getDouble(Double.parseDouble(HJJE), 2), 2);
							zfje = BSPHISUtil.getDouble(zfje + BSPHISUtil.getDouble(Double.parseDouble(HJJE)* Double.parseDouble(ZFBL), 2), 2);
						}
					}
					sfmx.put("ZJJE", zjje);
					sfmx.put("ZFJE", zfje);
					dao.doSave("create", BSPHISEntryNames.MS_SFMX, sfmx, false);
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("FPHM", MS_MZXX.get("FPHM"));
				parameters.put("MZXH", mzxh.get("MZXH"));
				Iterator<Long> cf = cfsbs.iterator();
				StringBuffer cfsb = new StringBuffer();
				List<Long> cfsb_list = new ArrayList<Long>();
				while (cf.hasNext()) {
					String cfsbStr = cf.next() + "";
					if (cfsb.length() > 0) {
						cfsb.append(",");
						cfsb.append(cfsbStr);
					} else {
						cfsb.append(cfsbStr);
					}
					cfsb_list.add(Long.parseLong(cfsbStr));
				}
				Iterator<Long> yj = yjxhs.iterator();
				StringBuffer yjxh = new StringBuffer();
				while (yj.hasNext()) {
					if (yjxh.length() > 0) {
						yjxh.append(",");
						yjxh.append(yj.next());
					} else {
						yjxh.append(yj.next());
					}
				}
				if (yjxh.length() != 0) {
					parameters.put("HJGH", uid);// add by liyunt
					dao.doUpdate(
							"update MS_YJ01 set FPHM=:FPHM,MZXH=:MZXH,HJGH =:HJGH where YJXH in ("
									+ yjxh
									+ ") and FPHM is null and MZXH is null",
							parameters);
					if (wpjfbz == 1 && wzsfxmjg == 1) {
						Map<String, Object> parameterssbxh = new HashMap<String, Object>();
						parameterssbxh.put("MZXH",
								Long.parseLong(mzxh.get("MZXH") + ""));
						List<Map<String, Object>> sbxhList = dao
								.doQuery(
										"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH",
										parameterssbxh);
						for (int q = 0; q < sbxhList.size(); q++) {
							Long sbxh = 0L;
							if (sbxhList.get(q).get("SBXH") != null) {
								sbxh = Long.parseLong(sbxhList.get(q).get(
										"SBXH")
										+ "");
								BSPHISUtil.updateXHMXZT(dao, sbxh, "1",
										manaUnitId);
							}
						}
					}
					parameters.remove("HJGH");
					ConfigLogisticsInventoryControlModel cic = new ConfigLogisticsInventoryControlModel(
							dao);
					cic.saveMzWzxx(yjxh.toString(), Long.parseLong(MS_MZXX
									.get("GHKS") == null ? "0" : MS_MZXX.get("GHKS")
									+ ""),
							Long.parseLong(MS_MZXX.get("GHGL") == null ? "0"
									: MS_MZXX.get("GHGL") + ""), Long
									.parseLong(mzxh.get("MZXH") + ""), ctx);
				}
				if (cfsb.length() != 0) {
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					String hql = "select CKBH as CKBH from YF_CKBH where JGID=:JGID and YFSB=:YFSB and QYPB=1 order by PDCF";

					String up_cf01 = "update MS_CF01 set FPHM=:FPHM,MZXH=:MZXH,FYCK=:FYCK where CFSB in (:CFSB) and YFSB=:YFSB and FPHM is null and MZXH is null";
					parameters1.put("JGID", manaUnitId);
					if (cfsb_xy.size() > 0) {// 西药处方
						for (Long yfsb : fyyf_xy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_xyfList = dao
									.doQuery(hql, parameters1);
							int ll_ckbh_xyf = -1;
							if (ll_ckbh_xyfList.size() > 0) {
								ll_ckbh_xyf = Integer.parseInt(ll_ckbh_xyfList
										.get(0).get("CKBH") + "");
							}
							parameters.put("FYCK", ll_ckbh_xyf);
							parameters.put("CFSB", cfsb_xy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);

							if (ll_ckbh_xyf > 0) { // 西药房
								parameters1.put("CKBH", ll_ckbh_xyf);
								dao.doUpdate(
										"update YF_CKBH set PDCF = PDCF + "
												+ updatecfsl
												+ " where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",
										parameters1);
							}
						}
					}
					if (cfsb_zy.size() > 0) {// 中药处方
						for (Long yfsb : fyyf_zy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_zyfList = dao
									.doQuery(hql, parameters1);
							int ll_ckbh_zyf = -1;
							if (ll_ckbh_zyfList.size() > 0) {
								ll_ckbh_zyf = Integer.parseInt(ll_ckbh_zyfList
										.get(0).get("CKBH") + "");
							}
							parameters.put("FYCK", ll_ckbh_zyf);
							parameters.put("CFSB", cfsb_zy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);
							if (updatecfsl != cfsb_zy.get(yfsb).size()) {
								throw new ModelDataOperationException(
										ServiceCode.CODE_DATABASE_ERROR,
										"门诊结算失败,有单据已结算，请重新导入后在结算!");
							}
							if (ll_ckbh_zyf > 0) { // 中药房
								parameters1.put("CKBH", ll_ckbh_zyf);
								dao.doUpdate(
										"update YF_CKBH set PDCF = PDCF + "
												+ updatecfsl
												+ " where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",
										parameters1);
							}
						}
					}
					if (cfsb_cy.size() > 0) {// 草药处方
						for (Long yfsb : fyyf_cy) {
							parameters1.put("YFSB", yfsb);
							parameters1.remove("CKBH");
							List<Map<String, Object>> ll_ckbh_cyfList = dao
									.doQuery(hql, parameters1);
							int ll_ckbh_cyf = -1;
							if (ll_ckbh_cyfList.size() > 0) {
								ll_ckbh_cyf = Integer.parseInt(ll_ckbh_cyfList
										.get(0).get("CKBH") + "");
							}
							parameters.put("FYCK", ll_ckbh_cyf);
							parameters.put("CFSB", cfsb_cy.get(yfsb));
							parameters.put("YFSB", yfsb);
							int updatecfsl = dao.doUpdate(up_cf01, parameters);
							if (updatecfsl != cfsb_cy.get(yfsb).size()) {
								throw new ModelDataOperationException(
										ServiceCode.CODE_DATABASE_ERROR,
										"门诊结算失败,有单据已结算，请重新导入后在结算!");
							}
							if (ll_ckbh_cyf > 0) { // 草药房
								parameters1.put("CKBH", ll_ckbh_cyf);
								dao.doUpdate(
										"update YF_CKBH set PDCF = PDCF + "
												+ updatecfsl
												+ " where JGID=:JGID and YFSB=:YFSB and CKBH=:CKBH ",
										parameters1);
							}
						}
					}
				}
				Map<String, Object> countParameters = new HashMap<String, Object>();
				countParameters.put("MZXH",
						Long.parseLong(mzxh.get("MZXH") + ""));
				long YJ02count = dao.doCount("MS_YJ01 a,MS_YJ02 b",
						"a.YJXH=b.YJXH and a.MZXH=:MZXH", countParameters);
				long CF02count = dao.doCount("MS_CF01 a,MS_CF02 b",
						"a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",
						countParameters);
				if ((YJ02count + CF02count) != datas.size()) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"门诊结算失败,结算费用数量已发生变化，请重新导入后在结算!");
				}
				double YJ02hjje = Double
						.parseDouble(dao
								.doLoad("select nvl(sum(b.HJJE),0) as HJJE from MS_YJ01 a,MS_YJ02 b where a.YJXH=b.YJXH and a.MZXH=:MZXH",
										countParameters).get("HJJE")
								+ "");
				double CF02hjje = Double
						.parseDouble(dao
								.doLoad("select nvl(sum(b.HJJE),0) as HJJE from MS_CF01 a,MS_CF02 b where a.CFSB=b.CFSB and a.MZXH=:MZXH and b.ZFYP!=1",
										countParameters).get("HJJE")
								+ "");
				if ((BSPHISUtil.getDouble(YJ02hjje + CF02hjje, 2)) != BSPHISUtil
						.getDouble(id_zjje, 2)) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"门诊结算失败,结算费用金额已发生变化，请重新导入后在结算!");
				}
				if (id_hbwc != 0) {
					Map<String, Object> parameters2 = new HashMap<String, Object>();
					parameters2.put("HBWC", "1");
					Long FKFS = Long
							.parseLong(dao
									.doLoad("select FKFS as FKFS from GY_FKFS where SYLX=1 and HBWC=:HBWC",
											parameters2).get("FKFS")
									+ "");
					Map<String, Object> MS_FKXX = new HashMap<String, Object>();
					MS_FKXX.put("JGID", manaUnitId);
					MS_FKXX.put("MZXH", mzxh.get("MZXH"));
					MS_FKXX.put("FKFS", FKFS);
					MS_FKXX.put("FKJE", id_hbwc);
					dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX,
							false);
				}
				Map<String, Object> MS_FKXX = new HashMap<String, Object>();
				MS_FKXX.put("JGID", manaUnitId);
				MS_FKXX.put("MZXH", mzxh.get("MZXH"));
				if (body.containsKey("FFFS")) {
					MS_FKXX.put("FKFS",
							Long.valueOf(String.valueOf(body.get("FFFS"))));
				}
				MS_FKXX.put("FKJE", BSPHISUtil.getDouble(id_xjje, 2));
				dao.doSave("create", BSPHISEntryNames.MS_FKXX, MS_FKXX, false);
				// update by caijy for 张伟2要求 yf_mzfymx只能要药房模块新增记录.
				StringBuffer hql_fphm = new StringBuffer();
				hql_fphm.append("update YF_MZFYMX set FPHM=:fphm where CFSB=:cfsb and FPHM='退药'");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("fphm", MS_MZXX.get("FPHM"));
				for (int i = 0; i < cfsb_list.size(); i++) {
					map_par.put("cfsb", cfsb_list.get(i));
					dao.doUpdate(hql_fphm.toString(), map_par);
				}
				// PharmacyDispensingModel mpmm = new
				// PharmacyDispensingModel(dao);
				// String SFZJFY = ParameterUtil.getParameter(manaUnitId,
				// BSPHISSystemArgument.SFZJFY, ctx);
				// String YDCZSF = MS_MZXX.get("YDCZSF") + "";// 移动收费判别字段，0为移动收费
				// if ("0".equals(YDCZSF)) {// 如果为0，则为移动收费，设置收费直接发药参数
				// SFZJFY = "1";
				// }
				// if ("1".equals(SFZJFY)) {
				// for (int i = 0; i < cfsb_list.size(); i++) {
				// Map<String, Object> map = new HashMap<String, Object>();
				// map.put("cfsb", cfsb_list.get(i));
				// map.put("fygh", uid);
				// Map<String, Object> m = mpmm.saveDispensing(map, ctx);
				// if (m.containsKey("x-response-code")) {
				// if (Integer.parseInt(m.get("x-response-code") + "") > 300) {
				// throw new ModelDataOperationException(
				// ServiceCode.CODE_DATABASE_ERROR,
				// m.get("x-response-msg") + "");
				// }
				// }
				// }
				// }
				if (body.containsKey("details")) {
					List<Map<String, Object>> dta = (List<Map<String, Object>>) body
							.get("details");
					Map<String, Object> bllxpar = new HashMap<String, Object>();
					for (int i = 0; i < dta.size(); i++) {
						Map<String, Object> detail = dta.get(i);
						if ("0".equals(String.valueOf(detail.get("CFLX")))) {
							bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
							bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
							dao.doUpdate(
									"update MS_YJ02 set ZFBL=:ZFBL where SBXH=:SBXH",
									bllxpar);
						} else {
							bllxpar.put("SBXH", parseLong(detail.get("SBXH")));
							bllxpar.put("ZFBL", parseDouble(detail.get("ZFBL")));
							dao.doUpdate(
									"update MS_CF02 set ZFBL=:ZFBL where SBXH=:SBXH",
									bllxpar);
						}
					}
				}
			}
			// 作废
			Map<String, Object> parameters_zf = new HashMap<String, Object>();
			parameters_zf.put("FPHM", yfphm);
			parameters_zf.put("JGID", manaUnitId);
			Map<String, Object> MZXX_zf = dao.doLoad(
					"select MZXH as MZXH,ZFPB as ZFPB,SFRQ as SFRQ ,BRXM as BRXM from "
							+ "MS_MZXX where FPHM=:FPHM and JGID=:JGID",
					parameters_zf);
			parameters_zf.remove("FPHM");
			parameters_zf.put("MZXH", MZXX_zf.get("MZXH"));
			dao.doUpdate(
					"update MS_MZXX set ZFPB = 1,THPB = 1 Where JGID=:JGID and MZXH=:MZXH",
					parameters_zf);
			long ll_Count = dao.doCount("MS_CF01", "JGID=:JGID and MZXH=:MZXH",
					parameters_zf);
			if (ll_Count > 0) {
				dao.doUpdate(
						"update MS_CF01 set ZFPB = 1 Where JGID=:JGID and MZXH=:MZXH",
						parameters_zf);
			}
			ll_Count = dao.doCount("MS_YJ01", "JGID=:JGID and MZXH=:MZXH",
					parameters_zf);
			if (ll_Count > 0) {
				dao.doUpdate(
						"update MS_YJ01 set ZFPB = 1 Where JGID=:JGID and MZXH=:MZXH",
						parameters_zf);
				if (wpjfbz == 1 && wzsfxmjg == 1) {
					List<Map<String, Object>> sbxhlist = dao
							.doQuery(
									"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c,WL_CK01 d Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.DJXH=d.DJXH and c.BRLY='1' and d.DJZT<2 and a.JGID=:JGID and a.MZXH=:MZXH",
									parameters_zf);
					for (int i = 0; i < sbxhlist.size(); i++) {
						BSPHISUtil
								.setSuppliesYKSL(
										dao,
										ctx,
										Long.parseLong(sbxhlist.get(i).get(
												"SBXH")
												+ ""), 1);
					}
					Map<String, Object> parametersxhmxupd = new HashMap<String, Object>();
					List<Map<String, Object>> sbxhlistupd = dao
							.doQuery(
									"select b.SBXH as SBXH from MS_YJ01 a,MS_YJ02 b,WL_XHMX c Where a.YJXH=b.YJXH and b.SBXH=c.MZXH and c.BRLY='1' and a.JGID=:JGID and a.MZXH=:MZXH",
									parameters_zf);
					for (int i = 0; i < sbxhlistupd.size(); i++) {
						parametersxhmxupd.put(
								"MZXH",
								Long.parseLong(sbxhlistupd.get(i).get("SBXH")
										+ ""));
						parametersxhmxupd.put("BRLY", "1");
						dao.doUpdate(
								"update WL_XHMX set ZTBZ=-1 where MZXH=:MZXH and BRLY=:BRLY",
								parametersxhmxupd);
					}
				}
			}
			parameters_zf.put("FPHM", yfphm);
			parameters_zf.put("CZGH", uid);
			parameters_zf.put("MZLB", BSPHISUtil.getMZLB(manaUnitId, dao));
			parameters_zf.put("ZFRQ", new Date());
			dao.doSave("create", BSPHISEntryNames.MS_ZFFP, parameters_zf, false);

			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			if (SFQYYFYFY == 1) {
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manaUnitId, ctx);
				StringBuffer hql_cfsb = new StringBuffer();// 查询发票对应的CFSB
				hql_cfsb.append("select CFSB as CFSB from MS_CF01 where JGID=:jgid and MZXH=:mzxh");
				StringBuffer hql_kcdj_delete = new StringBuffer();// 删除对应的CFSB的冻结记录
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_cfsb = new HashMap<String, Object>();
				map_par_cfsb.put("jgid", manaUnitId);
				map_par_cfsb.put("mzxh",
						MedicineUtils.parseLong(MZXX_zf.get("MZXH")));
				List<Map<String, Object>> list_cfsb = dao.doQuery(
						hql_cfsb.toString(), map_par_cfsb);
				if (list_cfsb != null && list_cfsb.size() > 0) {
					for (Map<String, Object> map_cfsb : list_cfsb) {
						Map<String, Object> map_par_delete = new HashMap<String, Object>();
						map_par_delete.put("cfsb",
								MedicineUtils.parseLong(map_cfsb.get("CFSB")));
						dao.doUpdate(hql_kcdj_delete.toString(), map_par_delete);
					}
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退费结算失败！");
		} catch (ValidateException e) {
			logger.error("For inquiries, the default payment method fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退费结算失败！");
		}
		// 然后根据sciid 和 scinid 去冲减相应的服务次数
		//退费后家医核销
		List<Map<String, Object>> gpRecords = dao.doList(CNDHelper.createSimpleCnd("eq", "fphm", "s", yfphm), null, BSPHISEntryNames.SCM_LOG02);
		StringBuffer hql = new StringBuffer();
		hql.append("update SCM_INCREASEITEMS set SERVICETIMES = SERVICETIMES +:costtimes where SCIID =:sciid");
		for (Map<String, Object> gpRecord : gpRecords) {
			Map<String, Object> parm = new HashMap<String, Object>();
			parm.put("costtimes", gpRecord.get("costTimes"));
			parm.put("sciid", gpRecord.get("sciId"));
			dao.doUpdate(hql.toString(), parm);
		}

	}

	/**
	 * 收费预保存
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSettle(Map<String, Object> req, Map<String, Object> res,
							 Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String userId = user.getUserId();
		List<Map<String, Object>> datas = (List<Map<String, Object>>) req
				.get("body");
		List<Map<String, Object>> reData = new ArrayList<Map<String, Object>>();
		List<Object> cfsb_list = new ArrayList<Object>();
		Map<String, Object> mzxx = (Map<String, Object>) req.get("MZXX");
		if (mzxx.containsKey("removeData")) {
			Map<String, Object> removeData = new HashMap<String, Object>();
			removeData.put("body", mzxx.get("removeData"));
			doRemoveSettle(removeData, res, ctx);
			cfsb_list.addAll((List<Object>) mzxx.get("removeCFSB"));
		}
		try {
			long cfsb = 0;
			List<Map<String, Object>> djs = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> data = datas.get(i);
				if (!"6".equals(data.get("DJLY") + "")) {
					Map<String, Object> dj = new HashMap<String, Object>();
					dj.put("CFLX", data.get("CFLX"));
					dj.put("CFSB", Long.parseLong(data.get("CFSB") + ""));
					djs.add(dj);
					reData.add(data);
					continue;
				}
				String op = "create";
				if (data.get("CFSB") != null) {
					op = "update";
					cfsb = Long.parseLong(data.get("CFSB") + "");
					data.put("SBXH", data.get("SBXH"));
					data.put("YJXH", data.get("CFSB"));
				}
				if ("0".equals(data.get("CFLX") + "")) {
					data.put("YJZX", 0);
					if (data.get("CF_NEW") != null
							&& (data.get("CF_NEW") + "").length() > 0) {
						// Map<String, Object> yj01Map = new HashMap<String,
						data.put("KDRQ", new Date());
						data.put("ZFPB", 0);
						data.put("ZXPB", 0);
						data.put("CFBZ", 0);
						data.put("DJZT", 0);
						data.put("JGID", manaUnitId);
						data.put("JZXH", 0);
						data.put("DJLY", 6);
						data.put("YJZX", 1);
						data.put("HJGH", userId);
						data.put("BRID", mzxx.get("BRID"));
						data.put("BRXM", mzxx.get("BRXM"));
						Map<String, Object> yj01 = dao.doSave(op,
								BSPHISEntryNames.MS_YJ01_CIC, data, false);
						Map<String, Object> dj = new HashMap<String, Object>();
						if ("update".equals(op)) {
							cfsb = Long.parseLong(data.get("YJXH") + "");
						} else {
							cfsb = Long.parseLong(yj01.get("YJXH") + "");
							data.put("CFSB", cfsb);
						}
						dj.put("CFLX", data.get("CFLX"));
						dj.put("CFSB", cfsb);
						djs.add(dj);
						// }
					}
					data.put("YJZH", data.get("YPZH_show"));
					data.put("JGID", manaUnitId);
					data.put("YJXH", cfsb);
					data.put("YLXH", data.get("YPXH"));
					data.put("YLDJ", data.get("YPDJ"));
					data.put("YLSL", data.get("YPSL"));
					data.put("DZBL", 1);
					data.put("XMLX", data.get("CFLX"));
					data.put("FYGB", BSPHISUtil.getfygb(
							Long.parseLong(data.get("CFLX") + ""),
							Long.parseLong(data.get("YPXH") + ""), dao, ctx));
					Map<String, Object> yj02 = dao.doSave(op,
							BSPHISEntryNames.MS_YJ02_CIC, data, false);
					if (!"update".equals(op)) {
						data.put("SBXH", yj02.get("SBXH"));
						int wpjfbz = Integer
								.parseInt("".equals(ParameterUtil.getParameter(
										manaUnitId,
										BSPHISSystemArgument.MZWPJFBZ, ctx)) ? "0"
										: ParameterUtil.getParameter(
										manaUnitId,
										BSPHISSystemArgument.MZWPJFBZ,
										ctx));
						int wzsfxmjg = Integer
								.parseInt("".equals(ParameterUtil.getParameter(
										manaUnitId,
										BSPHISSystemArgument.WZSFXMJG, ctx)) ? "0"
										: ParameterUtil.getParameter(
										manaUnitId,
										BSPHISSystemArgument.WZSFXMJG,
										ctx));
						if (wzsfxmjg == 1 && wpjfbz == 1) {
							doSaveSupplies(
									Long.parseLong(yj02.get("SBXH") + ""),
									Long.parseLong(data.get("KSDM") + ""), ctx);
						}

					}
					data.put("CFSB", cfsb);
				} else {
					if (data.get("CF_NEW") != null
							&& (data.get("CF_NEW") + "").length() > 0) {
						String sjjg = null;
						long sjyf = 0;
						Map<String, Object> map_yfxx = dao.doLoad(
								BSPHISEntryNames.YF_YFLB_CIC,
								Long.parseLong(data.get("YFSB") + ""));
						if (map_yfxx.get("SJJG") != null
								&& !"".equals(map_yfxx.get("SJJG"))) {
							sjjg = map_yfxx.get("SJJG") + "";
						}
						if (map_yfxx.get("SJYF") != null
								&& !"".equals(map_yfxx.get("SJYF"))
								&& Long.parseLong(map_yfxx.get("SJYF") + "") != 0) {
							sjyf = Long.parseLong(map_yfxx.get("SJYF") + "");
						}
						if (sjyf != 0 && sjjg != null) {
							data.put("SJJG", sjjg);
							data.put("SJYF", sjyf);
							data.put("SJFYBZ", 1);
						}
						data.put("JGID", manaUnitId);
						data.put("PYBZ", 0);
						data.put("TYBZ", 0);
						data.put("KFRQ", new Date());
						data.put("FYBZ", 0);
						data.put("ZFPB", 0);
						data.put("YXPB", 0);
						data.put("DJYBZ", 0);
						data.put("BRID", mzxx.get("BRID"));
						data.put("BRXM", mzxx.get("BRXM"));
						// 获取主键值
						if (data.get("CFSB") != null) {
							data.put("CFHM", data.get("CFSB"));
						} else {
							Schema sc = SchemaController.instance().get(
									BSPHISEntryNames.MS_CF01);
							List<SchemaItem> items = sc.getItems();
							SchemaItem item = null;
							for (SchemaItem sit : items) {
								if ("CFSB".equals(sit.getId())) {
									item = sit;
									break;
								}
							}
							Long pkey = Long.parseLong(KeyManager.getKeyByName(
									"MS_CF01", item.getKeyRules(),
									item.getId(), ctx));
							data.put("CFSB", pkey);
							data.put("CFHM", pkey);
						}
						dao.doSave(op, BSPHISEntryNames.MS_CF01_CIC, data,
								false);

						Map<String, Object> dj = new HashMap<String, Object>();
						cfsb = Long.parseLong(data.get("CFSB") + "");
						data.put("CFSB", cfsb);
						dj.put("CFLX", data.get("CFLX"));
						dj.put("CFSB", cfsb);
						cfsb_list.add(cfsb);
						djs.add(dj);
					}
					data.put("YPZH", data.get("YPZH_show"));

					// 判断库存是否足够
					Map<String, Object> data_msg = new HashMap<String, Object>();
					data_msg.put("pharmId", data.get("YFSB"));
					data_msg.put("medId", data.get("YPXH"));
					data_msg.put("medsource", data.get("YPCD"));
					if (data.get("CFLX").toString().equals("3")) {
						data.put("YYTS", 1);
						data_msg.put("quantity",
								Double.parseDouble(data.get("YPSL").toString())
										* (Integer) data.get("CFTS"));
					} else {
						data_msg.put("quantity", data.get("YPSL"));
					}
					if (Integer.parseInt(data.get("YPXH") + "") != 0) {
						data_msg.put("lsjg", data.get("YPDJ"));
						data_msg.put("jlxh", data.get("SBXH"));
						Map<String, Object> inventory = BSPHISUtil
								.checkInventory(data_msg, dao, ctx);
						if ((Integer) inventory.get("sign") == -1) {
							throw new ModelDataOperationException("药品【"
									+ data.get("YPMC") + "】库存数量不足，库存数量："
									+ inventory.get("KCZL") + ",实际数量："
									+ data_msg.get("quantity"));
						}
					}
					data.put("JGID", manaUnitId);
					data.put("CFSB", cfsb);
					data.put("XMLX", data.get("CFLX"));
					data.put("YCSL", "0");
					data.put("FYGB", BSPHISUtil.getfygb(
							Long.parseLong(data.get("CFLX") + ""),
							Long.parseLong(data.get("YPXH") + ""), dao, ctx));
					Object ypzs = (data.get("YPZS"));
					if (ypzs == null || ypzs.toString().equals("0")) {
						data.put("YPZS", null);
					}
					if (!data.containsKey("ZFYP")) {
						data.put("ZFYP", 0);
					}
					Map<String, Object> cf02 = dao.doSave(op,
							BSPHISEntryNames.MS_CF02_CIC, data, false);
					if (!"update".equals(op)) {
						data.put("SBXH", cf02.get("SBXH"));
					}
					data.put("CFSB", cfsb);
				}
				reData.add(data);
			}

			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
			if (SFQYYFYFY == 1 && MZKCDJSJ == 1) {// 如果启用库存冻结,并且在开单时冻结
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manaUnitId, ctx);
				// 先删除对应CFSB的冻结记录
				StringBuffer hql_kcdj_delete = new StringBuffer();
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_delete = new HashMap<String, Object>();
				for (int i = 0; i < cfsb_list.size(); i++) {
					long djcfsb = Long.parseLong(cfsb_list.get(i) + "");
					map_par_delete.put("cfsb", djcfsb);
					dao.doSqlUpdate(hql_kcdj_delete.toString(), map_par_delete);
					for (Map<String, Object> map_cf02 : reData) {
						if (djcfsb == Long.parseLong(map_cf02.get("CFSB") + "")) {
							Map<String, Object> map_kcdj = new HashMap<String, Object>();
							map_kcdj.put("JGID", manaUnitId);
							map_kcdj.put("YFSB", MedicineUtils
									.parseLong(map_cf02.get("YFSB")));
							map_kcdj.put("CFSB", MedicineUtils
									.parseLong(map_cf02.get("CFSB")));
							map_kcdj.put("YPXH", MedicineUtils
									.parseLong(map_cf02.get("YPXH")));
							map_kcdj.put("YPCD", MedicineUtils
									.parseLong(map_cf02.get("YPCD")));
							map_kcdj.put(
									"YPSL",
									MedicineUtils.simpleMultiply(2,
											map_cf02.get("YPSL"),
											map_cf02.get("CFTS")));
							map_kcdj.put("YFBZ", MedicineUtils
									.parseInt(map_cf02.get("YFBZ")));
							map_kcdj.put("DJSJ", new Date());
							map_kcdj.put("JLXH", MedicineUtils
									.parseLong(map_cf02.get("SBXH")));
							dao.doSave("create",
									"phis.application.pha.schemas.YF_KCDJ",
									map_kcdj, false);
						}
					}
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
			res.put("djs", djs);
			res.put("data", reData);
			// add by caijy for 更新挂号表的诊断序号
			if (mzxx.containsKey("ZDXH")
					&& MedicineUtils.parseLong(mzxx.get("ZDXH")) != 0
					&& mzxx.containsKey("GHGL")
					&& MedicineUtils.parseLong(mzxx.get("GHGL")) != 0) {
				StringBuffer hql = new StringBuffer();
				hql.append("update MS_GHMX set ZDXH=:zdxh where SBXH=:ghgl");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zdxh", MedicineUtils.parseLong(mzxx.get("ZDXH")));
				map_par.put("ghgl", MedicineUtils.parseLong(mzxx.get("GHGL")));
				dao.doUpdate(hql.toString(), map_par);
			}
		} catch (ValidateException e) {
			logger.error("收费预结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		} catch (PersistentDataOperationException e) {
			logger.error("收费预结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		} catch (ControllerException e) {
			logger.error("收费预结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		} catch (NumberFormatException e) {
			logger.error("收费预结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		} catch (KeyManagerException e) {
			logger.error("收费预结算保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		}
	}

	// 根据处置开的项目去取对应的价格并插入到物资库存表
	public void doSaveSupplies(Long sbxhs, long ksdm, Context ctx) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterskfxh = new HashMap<String, Object>();
		// 根据sbxh取到该条项目的所有信息
		// User user = (User) ctx.get("user.instance");
		// String manaUnitId = user.get("manageUnit.id");// 用户的机构ID
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
		String hql = "select a.SBXH as SBXH,a.YLXH as FYXH,a.YLSL as CZSL,d.BZJG as BZJG,f.JGBZ as JGBZ,b.BRID as BRID,c.BRXM as BRXM,b.JGID as JGID from MS_YJ02 a,MS_YJ01 b,MS_BRDA c,GY_YLSF d,GY_YLMX f where a.YJXH=b.YJXH and b.BRID=c.BRID and a.YLXH=d.FYXH and d.FYXH=f.FYXH and f.JGID=:JGID and a.SBXH=:SBXH and b.MZXH is null";
		try {
			parameters.put("SBXH", sbxhs);
			parameters.put("JGID", manaUnitId);
			Map<String, Object> lisFYXX = dao.doLoad(hql, parameters);
			StringBuffer hql1 = new StringBuffer();
			hql1.append("select KFXH as KFXH from WL_KFDZ where KSDM=:ejkf");
			String ksmc = "";
			ksmc = DictionaryController.instance()
					.get("phis.dictionary.department").getText(ksdm + "");
			parameterskfxh.put("ejkf", ksdm);
			Map<String, Object> kfxhMap = dao.doLoad(hql1.toString(),
					parameterskfxh);
			int kfxh = 0;
			if (kfxhMap != null) {
				if (kfxhMap.get("KFXH") != null) {
					kfxh = Integer.parseInt(kfxhMap.get("KFXH") + "");
				}
			} else {
				throw new RuntimeException("当前科室没有对应的物资库房！");
			}
			if (lisFYXX != null) {
				double czsl = Double.parseDouble(lisFYXX.get("CZSL") + "");
				Long sbxh = Long.parseLong(lisFYXX.get("SBXH") + "");
				Long fyxh = Long.parseLong(lisFYXX.get("FYXH") + "");
				Long brid = Long.parseLong(lisFYXX.get("BRID") + "");
				int jgbz = 0;
				if (lisFYXX.get("JGBZ") != null) {
					jgbz = Integer.parseInt(lisFYXX.get("JGBZ") + "");
				}
				double bzjg = 0;
				if (lisFYXX.get("BZJG") != null) {
					bzjg = Double.parseDouble(lisFYXX.get("BZJG") + "");
				}
				String brxm = lisFYXX.get("BRXM") + "";
				String jgid = lisFYXX.get("JGID") + "";
				setSuppliesJG(dao, sbxh, fyxh, kfxh, czsl, brid, "1", ksdm,
						brxm, ksmc, -2, jgid, jgbz, bzjg, null);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}

	}

	// 根据处置开的项目去取对应的价格本计算金额
	public void setSuppliesJG(BaseDAO dao, Long sbxh, Long fyxh, int kfxh,
							  double czsl, Long brid, String brly, Long ksdm, String brxm,
							  String ksmc, int ztbz, String jgid, int jgbz, double bzjg,
							  String zyhm) {
		Map<String, Object> parameterswzxh = new HashMap<String, Object>();
		Map<String, Object> parameterswzjg = new HashMap<String, Object>();
		Map<String, Object> parametersupd = new HashMap<String, Object>();
		Map<String, Object> parametersykslupd = new HashMap<String, Object>();
		parameterswzxh.put("FYXH", fyxh);
		parameterswzxh.put("JGID", jgid);
		parametersupd.put("SBXH", sbxh);
		try {
			// 根据fyxh取对应的wzxh
			List<Map<String, Object>> lisWZXH = dao
					.doQuery(
							"select WZXH as WZXH,WZSL as WZSL from GY_FYWZ where FYXH=:FYXH and JGID=:JGID",
							parameterswzxh);
			// double wzjg = 0.00;
			// double czje = 0.00;
			for (int i = 0; i < lisWZXH.size(); i++) {
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
				for (int j = 0; j < lisWZKC.size(); j++) {// 第一个去做的金额
					double wzsl = Double.parseDouble(lisWZKC.get(j).get("WZSL")
							+ "");
					double yksl = Double.parseDouble(lisWZKC.get(j).get("YKSL")
							+ "");
					long kcxh = Long.parseLong(lisWZKC.get(j).get("KCXH") + "");
					parametersykslupd.put("KCXH", kcxh);
					parametersykslupd.put("KFXH", kfxh);
					parametersykslupd.put("JGID", jgid);
					long wzxhs = Long
							.parseLong(lisWZKC.get(j).get("WZXH") + "");
					if (wzslall <= (wzsl - yksl)) {
						// if (jgbz == 1) {
						// wzjg = Double.parseDouble(lisWZKC.get(j)
						// .get("WZJG") + "");
						// } else {
						// wzjg = bzjg;
						// }
						// czje += wzslall * wzjg;
						parametersykslupd.put("YKSL", yksl + wzslall);
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH and JGID=:JGID",
								parametersykslupd);
						Map<String, Object> parametersyxhmxsave = new HashMap<String, Object>();
						parametersyxhmxsave.put("BRHM", zyhm);
						parametersyxhmxsave.put("BRXM", brxm);
						parametersyxhmxsave.put("WZSL", wzslall);
						parametersyxhmxsave.put("KSMC", ksmc);
						parametersyxhmxsave.put("BRID", brid);
						parametersyxhmxsave.put("BRLY", brly);
						parametersyxhmxsave.put("XHRQ", new Date());
						parametersyxhmxsave.put("KSDM", ksdm);
						parametersyxhmxsave.put("JGID", jgid);
						parametersyxhmxsave.put("KFXH", kfxh);
						parametersyxhmxsave.put("WZXH", wzxhs);
						parametersyxhmxsave.put("ZTBZ", ztbz);
						parametersyxhmxsave.put("MZXH", sbxh);
						parametersyxhmxsave.put("KCXH", kcxh);
						dao.doSave("create", BSPHISEntryNames.WL_XHMX,
								parametersyxhmxsave, false);
						break;
					} else if (wzslall > (wzsl - yksl)) {
						wzslall = wzslall - (wzsl - yksl);
						parametersykslupd.put("YKSL", yksl + (wzsl - yksl));
						dao.doUpdate(
								"update WL_WZKC set YKSL=:YKSL where KCXH=:KCXH and KFXH=:KFXH and JGID=:JGID",
								parametersykslupd);
						if ((wzsl - yksl) > 0) {
							Map<String, Object> parametersyxhmxsave = new HashMap<String, Object>();
							parametersyxhmxsave.put("BRHM", zyhm);
							parametersyxhmxsave.put("BRXM", brxm);
							parametersyxhmxsave.put("WZSL", (wzsl - yksl));
							parametersyxhmxsave.put("KSMC", ksmc);
							parametersyxhmxsave.put("BRID", brid);
							parametersyxhmxsave.put("BRLY", brly);
							parametersyxhmxsave.put("XHRQ", new Date());
							parametersyxhmxsave.put("KSDM", ksdm);
							parametersyxhmxsave.put("JGID", jgid);
							parametersyxhmxsave.put("KFXH", kfxh);
							parametersyxhmxsave.put("WZXH", wzxhs);
							parametersyxhmxsave.put("ZTBZ", ztbz);
							parametersyxhmxsave.put("MZXH", sbxh);
							parametersyxhmxsave.put("KCXH", kcxh);
							dao.doSave("create", BSPHISEntryNames.WL_XHMX,
									parametersyxhmxsave, false);
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 药房处方划价保存
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePhamaryClinicInfo(Map<String, Object> req,
										Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		// String userId = user.getUserId();
		List<Map<String, Object>> datas = (List<Map<String, Object>>) req
				.get("body");
		List<Object> cfsb_list = new ArrayList<Object>();
		Map<String, Object> mzxx = (Map<String, Object>) req.get("MZXX");
		if (mzxx != null && mzxx.containsKey("removeData")) {
			List<Map<String, Object>> removeData = (List<Map<String, Object>>) mzxx
					.get("removeData");
			Map<String, Object> parameters = new HashMap<String, Object>();
			try {
				for (int i = 0; i < removeData.size(); i++) {
					Map<String, Object> body = removeData.get(i);
					parameters.clear();
					parameters.put("CFSB",
							Long.parseLong(body.get("CFSB") + ""));
					long count = dao.doCount("MS_CF02", "CFSB=:CFSB",
							parameters);
					if (count > 1) {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_CF02);
					} else {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_CF02);
						dao.doRemove(Long.parseLong(body.get("CFSB") + ""),
								BSPHISEntryNames.MS_CF01);
					}
				}
			} catch (PersistentDataOperationException e) {
				logger.error("删除操作执行失败!", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "删除操作执行失败!");
			}
			cfsb_list.addAll((List<Object>) mzxx.get("removeCFSB"));
		}
		try {
			long cfsb = 0;
			// List<Map<String, Object>> djs = new ArrayList<Map<String,
			// Object>>();
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> data = datas.get(i);
				data.put("ZFYP", 0);
				String op = "create";
				if (data.get("CFSB") != null) {
					op = "update";
					cfsb = Long.parseLong(data.get("CFSB") + "");
					data.put("SBXH", data.get("SBXH"));
					data.put("YJXH", data.get("CFSB"));
				}
				if (data.get("CF_NEW") != null
						&& (data.get("CF_NEW") + "").length() > 0) {
					String sjjg = null;
					long sjyf = 0;
					Map<String, Object> map_yfxx = dao.doLoad(
							BSPHISEntryNames.YF_YFLB_CIC,
							Long.parseLong(data.get("YFSB") + ""));
					if (map_yfxx.get("SJJG") != null
							&& !"".equals(map_yfxx.get("SJJG"))) {
						sjjg = map_yfxx.get("SJJG") + "";
					}
					if (map_yfxx.get("SJYF") != null
							&& !"".equals(map_yfxx.get("SJYF"))
							&& Long.parseLong(map_yfxx.get("SJYF") + "") != 0) {
						sjyf = Long.parseLong(map_yfxx.get("SJYF") + "");
					}
					if (sjyf != 0 && sjjg != null) {
						data.put("SJJG", sjjg);
						data.put("SJYF", sjyf);
						data.put("SJFYBZ", 1);
					}
					data.put("JGID", manaUnitId);
					data.put("PYBZ", 0);
					data.put("TYBZ", 0);
					data.put("KFRQ", new Date());
					data.put("FYBZ", 0);
					data.put("ZFPB", 0);
					data.put("YXPB", 0);
					data.put("DJYBZ", 0);
					// 获取主键值
					if (data.get("CFSB") != null) {
						data.put("CFHM", data.get("CFSB"));
					} else {
						Schema sc = SchemaController.instance().get(
								BSPHISEntryNames.MS_CF01);
						List<SchemaItem> items = sc.getItems();
						SchemaItem item = null;
						for (SchemaItem sit : items) {
							if ("CFSB".equals(sit.getId())) {
								item = sit;
								break;
							}
						}
						Long pkey = Long.parseLong(KeyManager.getKeyByName(
								"MS_CF01", item.getKeyRules(), item.getId(),
								ctx));
						data.put("CFSB", pkey);
						data.put("CFHM", pkey);
					}
					dao.doSave(op, BSPHISEntryNames.MS_CF01_CIC, data, false);

					// Map<String, Object> dj = new HashMap<String, Object>();
					cfsb = Long.parseLong(data.get("CFSB") + "");
					// data.put("CFSB", cfsb);
					// dj.put("CFLX", data.get("CFLX"));
					// dj.put("CFSB", cfsb);
					cfsb_list.add(cfsb);
					// djs.add(dj);
				}
				data.put("YPZH", data.get("YPZH_show"));

				// 判断库存是否足够
				Map<String, Object> data_msg = new HashMap<String, Object>();
				data_msg.put("pharmId", data.get("YFSB"));
				data_msg.put("medId", data.get("YPXH"));
				data_msg.put("medsource", data.get("YPCD"));
				if (data.get("CFLX").toString().equals("3")) {
					data.put("YYTS", 1);
					data_msg.put("quantity",
							Double.parseDouble(data.get("YPSL").toString())
									* (Integer) data.get("CFTS"));
				} else {
					data_msg.put("quantity", data.get("YPSL"));
				}
				if (Integer.parseInt(data.get("YPXH") + "") != 0) {
					data_msg.put("lsjg", data.get("YPDJ"));
					Map<String, Object> inventory = BSPHISUtil.checkInventory(
							data_msg, dao, ctx);
					if ((Integer) inventory.get("sign") == -1) {
						throw new ModelDataOperationException("药品【"
								+ data.get("YPMC") + "】库存数量不足，库存数量："
								+ inventory.get("KCZL") + ",实际数量："
								+ data_msg.get("quantity"));
					}
				}
				data.put("JGID", manaUnitId);
				data.put("CFSB", cfsb);
				data.put("XMLX", data.get("CFLX"));
				data.put("YCSL", "0");
				data.put("FYGB", BSPHISUtil.getfygb(
						Long.parseLong(data.get("CFLX") + ""),
						Long.parseLong(data.get("YPXH") + ""), dao, ctx));
				Object ypzs = (data.get("YPZS"));
				if (ypzs == null || ypzs.toString().equals("0")) {
					data.put("YPZS", null);
				}
				Map<String, Object> cf02 = dao.doSave(op,
						BSPHISEntryNames.MS_CF02_CIC, data, false);
				if (!"update".equals(op)) {
					data.put("SBXH", cf02.get("SBXH"));
				}
				data.put("CFSB", cfsb);
				// reData.add(data);
			}

			// 库存冻结代码
			int SFQYYFYFY = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.SFQYYFYFY, ctx));// 是否启用库存冻结
			int MZKCDJSJ = MedicineUtils.parseInt(ParameterUtil.getParameter(
					manaUnitId, BSPHISSystemArgument.MZKCDJSJ, ctx));// 库存冻结时间
			if (SFQYYFYFY == 1 && MZKCDJSJ == 1) {// 如果启用库存冻结,并且在开单时冻结
				// 先删除过期的冻结库存
				//MedicineCommonModel model = new MedicineCommonModel(dao);
				//model.deleteKCDJ(manaUnitId, ctx);
				// 先删除对应CFSB的冻结记录
				StringBuffer hql_kcdj_delete = new StringBuffer();
				hql_kcdj_delete.append("delete from YF_KCDJ where CFSB=:cfsb");
				Map<String, Object> map_par_delete = new HashMap<String, Object>();
				for (int i = 0; i < cfsb_list.size(); i++) {
					long djcfsb = Long.parseLong(cfsb_list.get(i) + "");
					map_par_delete.put("cfsb", djcfsb);
					dao.doSqlUpdate(hql_kcdj_delete.toString(), map_par_delete);
					for (Map<String, Object> map_cf02 : datas) {
						if (djcfsb == Long.parseLong(map_cf02.get("CFSB") + "")) {
							Map<String, Object> map_kcdj = new HashMap<String, Object>();
							map_kcdj.put("JGID", manaUnitId);
							map_kcdj.put("YFSB", MedicineUtils
									.parseLong(map_cf02.get("YFSB")));
							map_kcdj.put("CFSB", MedicineUtils
									.parseLong(map_cf02.get("CFSB")));
							map_kcdj.put("YPXH", MedicineUtils
									.parseLong(map_cf02.get("YPXH")));
							map_kcdj.put("YPCD", MedicineUtils
									.parseLong(map_cf02.get("YPCD")));
							map_kcdj.put(
									"YPSL",
									MedicineUtils.simpleMultiply(2,
											map_cf02.get("YPSL"),
											map_cf02.get("CFTS")));
							map_kcdj.put("YFBZ", MedicineUtils
									.parseInt(map_cf02.get("YFBZ")));
							map_kcdj.put("DJSJ", new Date());
							map_kcdj.put("JLXH", MedicineUtils
									.parseLong(map_cf02.get("SBXH")));
							dao.doSave("create",
									"phis.application.pha.schemas.YF_KCDJ",
									map_kcdj, false);
						}
					}
				}
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();
			}
			// res.put("djs", djs);
			// res.put("data", reData);
		} catch (ValidateException e) {
			logger.error("药房划价保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "药房划价保存失败!");
		} catch (PersistentDataOperationException e) {
			logger.error("药房划价保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "药房划价保存失败!");
		} catch (ControllerException e) {
			logger.error("药房划价保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "药房划价保存失败!");
		} catch (NumberFormatException e) {
			logger.error("药房划价保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "药房划价保存失败!");
		} catch (KeyManagerException e) {
			logger.error("药房划价保存失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "收费预结算保存失败!");
		}
	}

	/**
	 * 收费删除
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveSettle(Map<String, Object> req,
							   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> bodys = (List<Map<String, Object>>) req
				.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			for (int i = 0; i < bodys.size(); i++) {
				Map<String, Object> body = bodys.get(i);
				if ("0".equals(body.get("CFLX") + "")) {
					parameters.clear();
					parameters.put("YJXH",
							Long.parseLong(body.get("CFSB") + ""));
					long count = dao.doCount("MS_YJ02", "YJXH=:YJXH",
							parameters);
					if (count > 1) {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_YJ02);
						BSPHISUtil.deleteSupplies(dao, ctx,
								Long.parseLong(body.get("SBXH") + ""));
					} else {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_YJ02);
						BSPHISUtil.deleteSupplies(dao, ctx,
								Long.parseLong(body.get("SBXH") + ""));
						dao.doRemove(Long.parseLong(body.get("CFSB") + ""),
								BSPHISEntryNames.MS_YJ01_CIC);
					}
				} else {
					parameters.clear();
					parameters.put("CFSB",
							Long.parseLong(body.get("CFSB") + ""));
					long count = dao.doCount("MS_CF02", "CFSB=:CFSB",
							parameters);
					if (count > 1) {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_CF02);
					} else {
						dao.doRemove(Long.parseLong(body.get("SBXH") + ""),
								BSPHISEntryNames.MS_CF02);
						dao.doRemove(Long.parseLong(body.get("CFSB") + ""),
								BSPHISEntryNames.MS_CF01);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("删除操作执行失败!", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除操作执行失败!");
		}
	}

	public int doQueryFPZS(List<Map<String, Object>> datas, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String MZFPFDFS = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPFDFS, ctx);
		String MZFPSFLD = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPSFLD, ctx);
		String MZFPMXSL = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPMXSL, ctx);
		if ("0".equals(MZFPFDFS)) {
			if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {
				int n = datas.size() / Integer.parseInt(MZFPMXSL);
				if (Integer.parseInt(MZFPMXSL) * n < datas.size()) {
					n++;
				}
				return n;
			}
			return 1;
		} else if ("1".equals(MZFPFDFS)) {
			Map<String, String> fygbs = new HashMap<String, String>();
			for (Map<String, Object> data : datas) {
				fygbs.put(data.get("FYGB") + "", "");
				;
			}
			if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {
				int l = 0;
				for (String key : fygbs.keySet()) {
					int k = 0;
					for (Map<String, Object> data : datas) {
						if ((data.get("FYGB") + "").equals(key)) {
							k++;
						}
					}
					int n = k / Integer.parseInt(MZFPMXSL);
					if (Integer.parseInt(MZFPMXSL) * n < k) {
						n++;
					}
					l += n;
				}
				return l;
			}
			return fygbs.size();
		} else if ("2".equals(MZFPFDFS)) {
			Map<String, String> yfsbs = new HashMap<String, String>();
			Map<String, String> zxkss = new HashMap<String, String>();
			for (Map<String, Object> data : datas) {
				if ("0".equals(data.get("CFLX") + "")) {
					zxkss.put(data.get("ZXKS") + "", "");
				} else {
					yfsbs.put(data.get("YFSB") + "", "");
				}
			}
			if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {
				int l = 0;
				for (String key : zxkss.keySet()) {
					int k = 0;
					for (Map<String, Object> data : datas) {
						if ((data.get("ZXKS") + "").equals(key)) {
							k++;
						}
					}
					int n = k / Integer.parseInt(MZFPMXSL);
					if (Integer.parseInt(MZFPMXSL) * n < k) {
						n++;
					}
					l += n;
				}
				for (String key : yfsbs.keySet()) {
					int k = 0;
					for (Map<String, Object> data : datas) {
						if ((data.get("YFSB") + "").equals(key)) {
							k++;
						}
					}
					int n = k / Integer.parseInt(MZFPMXSL);
					if (Integer.parseInt(MZFPMXSL) * n < k) {
						n++;
					}
					l += n;
				}
				return l;
			}
			return zxkss.size() + yfsbs.size();
		}
		return 1;
	}

	public void doPrintMoth(Map<String, Object> request,
							Map<String, Object> response, Context ctx)
			throws ModelDataOperationException {
		BaseDAO dao = new BaseDAO(ctx);
		String fphm = request.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		//门诊发票分单方式
		String MZFPFDFS = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPFDFS, ctx);
		//门诊发票是否连打
		String MZFPSFLD = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPSFLD, ctx);
		//门诊发票连打明细数量
		String MZFPMXSL = ParameterUtil.getParameter(JGID,
				BSPHISSystemArgument.MZFPMXSL, ctx);
		List<Map<String, Object>> mzfps = new ArrayList<Map<String, Object>>();
		int page = 1;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> MZXX = new HashMap<String, Object>();
			StringBuffer hql = new StringBuffer("select b.MZHM as MZHM ,nvl(a.FPZS,1) as FPZS," +
					" nvl(a.HZJE,0) as HZJE,nvl(a.JJZFJE,0) as JJZFJE,nvl(a.MZJZJE,0) as MZJZJE," +
					" nvl(a.XJJE,0) as XJJE,a.BRXZ as BRXZ,d.XZMC as JSFS,c.PERSONNAME as SFY," +
					" a.MZXH as MZXH,b.MZHM as XLH,b.BRXM as XM,a.QTYS as JZ,a.ZJJE as HJJE," +
					" a.ZFJE as ZFJE,to_char(a.SFRQ,'yyyy') as YYYY," +
					//" a.ZFJE as ZFJE,to_char(a.SFRQ,'yyyy') as YYYY," + zhaojian 2019-07-18 增加家医履约减免金额
					" a.ZFJE as ZFJE,a.JYLYJMJE as JYLYJMJE,a.YCFJMJE as YCFJMJE,to_char(a.SFRQ,'yyyy') as YYYY," +
					" to_char(a.SFRQ,'mm') as MM,to_char(a.SFRQ,'dd') as DD,b.NHKH as NHKH ," +
					" b.YBKH as YBKH,b.SHBZKH as SHBZKH,b.BRXB as XB,a.GHGL as GHGL,e.YLRYLB as YLRYLB from ");
			hql.append("MS_MZXX a,");
			hql.append("MS_BRDA b left join NJJB_KXX e on b.SFZH=e.SFZH and b.SHBZKH=e.SHBZKH,");
			hql.append("SYS_Personnel c,");
			hql.append("GY_BRXZ d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.BRID = b.BRID and a.FPHM = :FPHM and a.JGID = :JGID");
			parameters.put("FPHM", fphm);
			parameters.put("JGID", JGID);
			MZXX = dao.doSqlQuery(hql.toString(), parameters).get(0);
			//2018-12-31 从卡信息中查询病人的人员类别，以便发票打印时区分职工医保和居民医保
			response.put("YLRYLB", MZXX.get("YLRYLB")+"");
			//add by Wangjl 在发票上增加药房排队序号
			List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp_mzfp = new HashMap<String, Object>();
			temp_mzfp.put("YFFYSL", "");//初始化药房排队号码为空值
			if(ParameterUtil.getParameter(JGID, "YFPDJH", ctx).equals("1")){
				String YFPDJHCFLX=ParameterUtil.getParameter(JGID,"YFPDJHCFLX",ctx);
				int pdyfsb=0;  //药房识别用于排队叫号
				String countsql = "";
				if((YFPDJHCFLX.indexOf("3")>=0)){
					int cflx=Integer.parseInt(dao.doLoad("select count(distinct CFLX) as CFLX from MS_CF01 where  FPHM = :FPHM and JGID = :JGID and CFLX = 3",
							parameters).get("CFLX") +"");//获取处方类型
					if (cflx == 1){
						pdyfsb = Integer.parseInt(dao.doLoad("select distinct YFSB as YFSB from MS_CF01 where  FPHM = :FPHM and JGID = :JGID and CFLX = 3",
								parameters).get("YFSB")+ "");//若药品处方类型和药品类型不一致，可能导致查出多个yfsb，系统会报错，请注意维护准确
						countsql="select count(distinct a.MZXH)  as YFFYSL "+
								" from MS_MZXX a,MS_CF01 b where  a.MZXH=b.MZXH and a.FPHM=b.FPHM "+
								" and  to_char(a.sfrq,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') "+
								" and a.JGID=:JGID and b.YFSB=:YFSB";
						HashMap<String, Object> pa =new HashMap<String, Object>();
						pa.put("JGID",JGID);
						pa.put("YFSB",pdyfsb);
						temp_mzfp.put("YFFYSL", dao.doSqlLoad(countsql, pa).get("YFFYSL")+"");
					}else {
						temp_mzfp.put("YFFYSL", "");
					}
				} else if((YFPDJHCFLX.indexOf("2")>=0) || (YFPDJHCFLX.indexOf("1")>=0) ){
					String sql="select distinct YFSB as YFSB from MS_CF01  where FPHM = :FPHM and JGID = :JGID and (CFLX = 2 or CFLX = 1)";
					List<Map<String, Object>> sqlList = new ArrayList<Map<String, Object>>();
					//sqlList= (List<Map<String, Object>>) dao.doLoad(sql,parameters);
					sqlList = dao.doSqlQuery(sql, parameters);
					if(sqlList.size()==0){ //无处方
						countsql = "select count(distinct a.MZXH)  as YFFYSL " +
								" from MS_MZXX a,MS_YJ01 b where  a.MZXH=b.MZXH and a.FPHM=b.FPHM " +
								" and  to_char(a.sfrq,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') " +
								" and a.JGID=:JGID";
					}else { //有处方
						pdyfsb = Integer.parseInt(dao.doLoad(sql, parameters).get("YFSB") + "");//若药品处方类型和药品类型不一致，可能导致查出多个yfsb，系统会报错，请注意维护准确
						countsql = "select count(distinct a.MZXH)  as YFFYSL " +
								" from MS_MZXX a,MS_CF01 b where  a.MZXH=b.MZXH and a.FPHM=b.FPHM " +
								" and  to_char(a.sfrq,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') " +
								" and a.JGID=:JGID and b.YFSB=:YFSB";
					}
				}else if((YFPDJHCFLX.indexOf("0")>=0) ){  //全药房启用排队叫号-暂时没做

				}
			}
//			String countsql="select count(1) as KSGHSL from MS_GHMX a where a.SBXH<=:SBXH " +
//					" and to_char(a.GHSJ,'yyyy-mm-dd')='"+MZXX.get("YYYY")+"-"+MZXX.get("MM")+"-"+MZXX.get("DD")+"'" +
//					" and a.KSDM=(select KSDM from MS_GHMX b where b.SBXH=:SBXH )";
//			HashMap<String, Object> pa =new HashMap<String, Object>();
//			pa.put("SBXH", Long.parseLong(MZXX.get("GHGL")+""));
//			response.put("BRXZ", MZXX.get("BRXZ")+"");
//			List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
//			Map<String, Object> temp_mzfp = new HashMap<String, Object>();
//			temp_mzfp.put("KSGHSL", dao.doSqlLoad(countsql, pa).get("KSGHSL")+"");
			temp_mzfp.put("MZHM", MZXX.get("MZHM")+"");
			temp_mzfp.put("MZXH", MZXX.get("MZXH")+"");
			/***********begin zhaojian 2019-07-18 增加家医履约减免金额***********/
			//temp_mzfp.put("GRZF", MZXX.get("ZFJE")+"");
			double jylyjmje =  Double.parseDouble(MZXX.get("JYLYJMJE")+"");
			String jyjmxx = "";
			if(jylyjmje>0){
				jyjmxx = "家医减免"+jylyjmje+"元";
			}
			/***********end zhaojian 2019-07-18 增加家医履约减免金额***********/

			/***********begin xiaheng 2020-06-22 增加孕产妇减免金额***********/
			//temp_mzfp.put("GRZF", MZXX.get("ZFJE")+"");
			double ycfjmje =  Double.parseDouble(MZXX.get("YCFJMJE")+"");
			String ycfjmxx = "";
			if(ycfjmje>0){
				if(jyjmxx != ""){
					ycfjmxx = ",孕产妇减免"+ycfjmje+"元";
				}else{
					ycfjmxx = "孕产妇减免"+ycfjmje+"元";
				}
			}
			/***********end xiaheng 2020-06-22 增加孕产妇减免金额***********/
			if(jyjmxx == "" && ycfjmxx == ""){
				temp_mzfp.put("GRZF", MZXX.get("ZFJE"));
			}else{
				temp_mzfp.put("GRZF", MZXX.get("ZFJE")+"("+jyjmxx+ycfjmxx+")");
			}
			temp_mzfp.put("FPHM", fphm);
			temp_mzfp.put("YYYY", MZXX.get("YYYY") + "");
			temp_mzfp.put("MM", MZXX.get("MM") + "");
			temp_mzfp.put("DD", MZXX.get("DD") + "");
			temp_mzfp.put("XM", MZXX.get("XM") + "");
			temp_mzfp.put("FYRQ",MZXX.get("YYYY")+"-"+MZXX.get("MM") + "-"+MZXX.get("DD"));
			temp_mzfp.put("JGMC", jgname);
			temp_mzfp.put("SFY", MZXX.get("SFY") + "");
			temp_mzfp.put("JSFS", MZXX.get("JSFS") + "");
			temp_mzfp.put("XLH", MZXX.get("XLH") + "");
			temp_mzfp.put("HJJE", MZXX.get("HJJE") + "");
			temp_mzfp.put("GRJF", MZXX.get("XJJE") + "");
			temp_mzfp.put("JZ", MZXX.get("JZ") + "");

			String[] upint = { "零", "壹", "贰", "叁", "肆", "伍","陆", "柒", "捌", "玖","拾" };

			double hjje = Double.parseDouble(MZXX.get("HJJE")+ "");
			BigDecimal numberOfMoney = new BigDecimal(hjje);
			String DXJE = NumberToCNY.number2CNMontrayUnit(numberOfMoney);
			int sw = (int) (hjje / 100000) % 10;
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
			Map<String, Object> temp_sfxms = new HashMap<String, Object>();
			//查询南京金保结算信息
			if("2000".equals(MZXX.get("BRXZ")+"")){
				List<?> cnd = CNDHelper.createSimpleCnd("eq", "MZXH", "s", MZXX.get("MZXH")+"");
				List<Map<String, Object>> jbjsl=dao.doList(cnd, "", BSPHISEntryNames.NJJB_JSXX);
				if(jbjsl!=null && jbjsl.size()==1){
					response.put("njjbjsxx", jbjsl.get(0));
				}else{
					System.out.println("未查询到金保结算记录或者存在多条记录请查看");
				}
			}


			if ("0".equals(MZFPFDFS)) {//明细分单方式
				if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {//发票连打
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					parameters1.put("MZXH", MZXX.get("MZXH"));
					String detailsSql = "select c.YPMC as YPMC,c.YPGG as YPGG,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and b.ZFYP!=1";
					detailsSql += " union all ";
					detailsSql += "select c.FYMC as YPMC,'' as YPGG,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH";
					List<Map<String, Object>> details = dao.doSqlQuery(detailsSql, parameters1);
					String BRXZ=MZXX.get("BRXZ")+"";
					/**************add by lizhi 20171009 医保明细项目只显示一列：每张发票显示数量为自费的一半*******************/
					if(BRXZ.equals("3000")){
						MZFPMXSL = String.valueOf(Integer.parseInt(MZFPMXSL)/2);
					}
					/**************add by lizhi 20171009 医保明细项目只显示一列：每张发票显示数量为自费的一半*******************/
					int n = details.size() / Integer.parseInt(MZFPMXSL);
					if (Integer.parseInt(MZFPMXSL) * n < details.size()) {
						n++;//计算发票张数
					}
					MZXX.put("FPZS",n);
					Map<String, Object> p = new HashMap<String, Object>();
					p.put("MZXH", MZXX.get("MZXH"));
					p.put("JGID", JGID);
					if(BRXZ.equals("6000")){
						//把备注加到mzxx中
						String bxsql="select BZ as BZ from NH_BSOFT_JSJL where MZXH=:MZXH and JGID=:JGID and ZFPB=0";
						Map<String, Object> nhbx =dao.doSqlLoad(bxsql, p);
						if(nhbx!=null && nhbx.size() >0){
							MZXX.put("BZ", nhbx.get("BZ"));
						}
					}
					if(BRXZ.equals("3000")){
						String ybbxsql=" select nvl(ZFY,0) as YBZFY,nvl(YBZF,0) as YBZF,nvl(ZHYE,0) as ZHYE, " +
								" TBR as YBKH from YB_YBMX where MZXH=:MZXH and JGID=:JGID ";
						Map<String, Object> ybbx =dao.doSqlLoad(ybbxsql, p);
						if(ybbx!=null && ybbx.size()>0){
							MZXX.put("YBZFY", ybbx.get("YBZFY"));
							MZXX.put("YBZF", ybbx.get("YBZF"));
							MZXX.put("YBZHYE", ybbx.get("ZHYE"));
							MZXX.put("YBKH", ybbx.get("YBKH"));
						}
					}

					for (int jk = 0; jk < n; jk++) {
						Map<String, Object> mzfp = new HashMap<String, Object>();
						mzfp.putAll(temp_mzfp);
						mzfp.put("XB",DictionaryController.instance().getDic("phis.dictionary.gender").getText(MZXX.get("XB")+""));
						if(BRXZ.equals("6000")){
							mzfp.put("YLZH", MZXX.get("NHKH")+"");
							mzfp.put("BZ", MZXX.get("BZ")+"");
						} else if(BRXZ.equals("3000")){
							mzfp.put("YLZH", MZXX.get("YBKH")+"");
							mzfp.put("YBZFY", MZXX.get("YBZFY")+"");
							mzfp.put("YBZF", MZXX.get("YBZF")+"");
							mzfp.put("YBZHYE", MZXX.get("YBZHYE")+"");
						}else if(BRXZ.equals("2000")){
							mzfp.put("YLZH", MZXX.get("SHBZKH")+"");
						}
						int maxdetal = (jk + 1) * Integer.parseInt(MZFPMXSL);
						if (maxdetal > details.size()) {
							maxdetal = details.size();
						}
						int k = 0;
						for (int i = (jk) * Integer.parseInt(MZFPMXSL); i < maxdetal; i++) {
							Map<String, Object> detail = details.get(i);
							mzfp.put("MXMC" + (++k),(detail.get("YPMC") + "").substring(0,(detail.get("YPMC") + "").length() > 13
									? 13: (detail.get("YPMC") + "").length()));
							mzfp.put("MXGG" + (k), detail.get("YPGG"));
							mzfp.put("MXDJ" + (k), detail.get("YPDJ"));
							mzfp.put("MXSL" + (k), detail.get("YPSL"));
							mzfp.put("MXJE" + (k), detail.get("HJJE"));
						}
						if (jk == 0) {
							StringBuffer hql1 = new StringBuffer("select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE,b.MZPL as MZPL from ");
							hql1.append("GY_SFXM b  join MS_SFMX a on a.SFXM = b.SFXM and a.MZXH = :MZXH group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
							SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
							//多余三项收费项目的发票张数增加
							if(SFXMS.size() >2){
								int tempn=SFXMS.size()/3;
								if (3*tempn < SFXMS.size()) {
									tempn++;//计算发票张数
								}
								if(tempn>n){
									n=tempn;
									MZXX.put("FPZS",n);
								}
							}
							for (int i = 0; i < SFXMS.size(); i++) {
								Map<String, Object> SFXM = SFXMS.get(i);
								temp_sfxms.put("SFXM" + (i+1),SFXM.get("MCSX") + "");
								if (SFXM.get("ZJJE") != null&& (SFXM.get("ZJJE") + "").length() > 0) {
									temp_sfxms.put("XMJE" + (i+1),SFXM.get("ZJJE") + "");
								}
								else {
									temp_sfxms.put("XMJE" + (i+1),"0.00");
								}
							}
							mzfp.putAll(temp_sfxms);
							if (mzfp.containsKey("QTJE")) {
								if (mzfp.containsKey("XMJE" + mzfp.get("QTPL"))&& (mzfp.get("XMJE" + mzfp.get("QTPL")) + "").length() > 0) {
									mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("XMJE"
											+ mzfp.get("QTPL"))+ "")
											+ Double.parseDouble(mzfp.get("QTJE") + ""));
								}
								else {
									mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("QTJE")+ ""));
								}
							} else {
								if (mzfp.containsKey("SFXM" + mzfp.get("QTPL"))) {
									mzfp.remove("SFXM" + mzfp.get("QTPL"));
									mzfp.remove("XMJE" + mzfp.get("QTPL"));
								}
							}

							String zjestr="";
							if(sw >0)zjestr=zjestr+upint[sw]+"拾";
							if(w >0)zjestr=zjestr+upint[w]+"万";
							if(q >0)zjestr=zjestr+upint[q]+"仟";
							if(b >0)zjestr=zjestr+upint[b]+"佰";
							if(s >0)zjestr=zjestr+upint[s]+"拾";
							if(y >0)zjestr=zjestr+upint[y]+"圆";
							if(j >0)zjestr=zjestr+upint[j]+"角";
							if(f >0)zjestr=zjestr+upint[f]+"分";
							mzfp.put("DXZJE", DXJE);//大写总金额
						}else{
							mzfp.putAll(temp_sfxms);
						}

						response.put(BSPHISSystemArgument.FPYL, ParameterUtil
								.getParameter(JGID, BSPHISSystemArgument.FPYL,
										ctx));
						response.put(BSPHISSystemArgument.MZHJSFDYJMC,
								ParameterUtil.getParameter(JGID,
										BSPHISSystemArgument.MZHJSFDYJMC, ctx));
						mzfp.put("PAGE", "(" + page + "/" + MZXX.get("FPZS")+ ")");
						mzfps.add(mzfp);
						page++;
					}
					response.put("mzfps", mzfps);
				}
				else {//非发票连打且没设置明细条数
					Map<String, Object> mzfp = new HashMap<String, Object>();
					mzfp.putAll(temp_mzfp);
					Map<String, Object> parameters1 = new HashMap<String, Object>();
					parameters1.put("MZXH", MZXX.get("MZXH"));
					String detailsSql = "select c.YPMC as YPMC,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and b.ZFYP!=1";
					detailsSql += " union all ";
					detailsSql += "select c.FYMC as YPMC,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH";
					List<Map<String, Object>> details = dao.doSqlQuery(detailsSql, parameters1);
					int detsize = details.size();
					if (Integer.parseInt(MZFPMXSL) != 0
							&& Integer.parseInt(MZFPMXSL) < detsize) {
						detsize = Integer.parseInt(MZFPMXSL);
					}
					for (int i = 0; i < detsize; i++) {

						Map<String, Object> detail = details.get(i);
						mzfp.put("MXMC" + (i + 1), detail.get("YPMC"));
						mzfp.put("MXDJ" + (i + 1), detail.get("YPDJ"));
						mzfp.put("MXSL" + (i + 1), detail.get("YPSL"));
						mzfp.put("MXJE" + (i + 1), detail.get("HJJE"));
					}
					if (detsize < details.size()) {
						mzfp.put("MXMC" + (detsize + 1), "*以上明细信息不全");
					}
					StringBuffer hql1 = new StringBuffer(
							"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE,b.MZPL as MZPL from ");
					hql1.append("GY_SFXM b left outer join MS_SFMX a on a.SFXM = b.SFXM and a.MZXH = :MZXH group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
					SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
					for (int i = 0; i < SFXMS.size(); i++) {
						Map<String, Object> SFXM = SFXMS.get(i);
						if (SFXM.get("MZPL") != null
								&& (SFXM.get("MZPL") + "").length() > 0) {
							if ("其 它".equals(SFXM.get("MCSX") + "")|| "其它".equals(SFXM.get("MCSX") + "")) {
								mzfp.put("QTPL", SFXM.get("MZPL"));
								mzfp.put("SFXM" + SFXM.get("MZPL"),SFXM.get("MCSX") + "");
								if (SFXM.get("ZJJE") != null&& (SFXM.get("ZJJE") + "").length() > 0) {
									mzfp.put("XMJE" + SFXM.get("MZPL"),SFXM.get("ZJJE") + "");
								} else {
									mzfp.put("XMJE" + SFXM.get("MZPL"), "0.00");
								}
								continue;
							} else {
								if (SFXM.get("ZJJE") != null && (SFXM.get("ZJJE") + "").length() > 0) {
									if (Double.parseDouble(SFXM.get("ZJJE")+ "") > 0) {
										mzfp.put("SFXM" + SFXM.get("MZPL"),SFXM.get("MCSX") + "");
										mzfp.put("XMJE" + SFXM.get("MZPL"),SFXM.get("ZJJE") + "");
									}
								}
							}
						} else if (SFXM.get("ZJJE") != null
								&& (SFXM.get("ZJJE") + "").length() > 0) {
							if (Double.parseDouble(SFXM.get("ZJJE") + "") > 0) {
								if (mzfp.containsKey("QTJE")) {
									mzfp.put("QTJE",(Double.parseDouble(mzfp.get("QTJE") + "") + Double.parseDouble(SFXM.get("ZJJE")+ ""))+ "");
								} else {
									mzfp.put("QTJE", SFXM.get("ZJJE") + "");
								}
							}
						}
					}
					if (mzfp.containsKey("QTJE")) {
						if (mzfp.containsKey("XMJE" + mzfp.get("QTPL"))&& (mzfp.get("XMJE" + mzfp.get("QTPL")) + "").length() > 0) {
							mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("XMJE"+ mzfp.get("QTPL"))
									+ "")+ Double.parseDouble(mzfp.get("QTJE") + ""));
						} else {
							mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("QTJE") + ""));
						}
					} else {
						if (mzfp.containsKey("SFXM" + mzfp.get("QTPL"))) {
							mzfp.remove("SFXM" + mzfp.get("QTPL"));
							mzfp.remove("XMJE" + mzfp.get("QTPL"));
						}
					}
					mzfp.put("SW", upint[sw]);
					mzfp.put("W", upint[w]);
					mzfp.put("Q", upint[q]);
					mzfp.put("B", upint[b]);
					mzfp.put("S", upint[s]);
					mzfp.put("Y", upint[y]);
					mzfp.put("J", upint[j]);
					mzfp.put("F", upint[f]);
					response.put(BSPHISSystemArgument.FPYL, ParameterUtil
							.getParameter(JGID, BSPHISSystemArgument.FPYL, ctx));
					response.put(BSPHISSystemArgument.MZHJSFDYJMC,
							ParameterUtil.getParameter(JGID,
									BSPHISSystemArgument.MZHJSFDYJMC, ctx));
					mzfp.put("PAGE", "(" + page + "/" + MZXX.get("FPZS") + ")");
					mzfps.add(mzfp);
					page++;
					response.put("mzfps", mzfps);
				}
			}
			else if ("1".equals(MZFPFDFS)) {// 按收费项目分单
				List<Map<String, Object>> fygbs = dao
						.doSqlQuery(
								"select SFXM as SFXM from MS_SFMX where FPHM=:FPHM and JGID=:JGID",
								parameters);
				for (Map<String, Object> fygb : fygbs) {
					parameters.put("SFXM",
							Long.parseLong(fygb.get("SFXM") + ""));
					if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {

						Map<String, Object> parameters1 = new HashMap<String, Object>();
						parameters1.put("MZXH", MZXX.get("MZXH"));
						parameters1.put("SFXM",
								Long.parseLong(fygb.get("SFXM") + ""));
						String detailsSql = "select  c.YPMC as YPMC,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and b.FYGB=:SFXM and b.ZFYP!=1";
						detailsSql += " union all ";
						detailsSql += "select c.FYMC as YPMC,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH and b.FYGB=:SFXM";
						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						int n = details.size() / Integer.parseInt(MZFPMXSL);
						if (Integer.parseInt(MZFPMXSL) * n < details.size()) {
							n++;
						}
						for (int jk = 0; jk < n; jk++) {
							Map<String, Object> mzfp = new HashMap<String, Object>();
							mzfp.putAll(temp_mzfp);
							int maxdetal = (jk + 1)
									* Integer.parseInt(MZFPMXSL);
							if (maxdetal > details.size()) {
								maxdetal = details.size();
							}
							int k = 0;
							for (int i = (jk) * Integer.parseInt(MZFPMXSL); i < maxdetal; i++) {
								Map<String, Object> detail = details.get(i);
								mzfp.put(
										"MXMC" + (++k),
										(detail.get("YPMC") + "").substring(
												0,
												(detail.get("YPMC") + "")
														.length() > 13 ? 13
														: (detail.get("YPMC") + "")
														.length()));
								mzfp.put("MXDJ" + (k), detail.get("YPDJ"));
								mzfp.put("MXSL" + (k), detail.get("YPSL"));
								mzfp.put("MXJE" + (k), detail.get("HJJE"));
							}
							if (jk == 0) {
								StringBuffer hql1 = new StringBuffer(
										"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE,b.MZPL as MZPL from ");
								hql1.append("GY_SFXM b left outer join MS_SFMX a on a.SFXM = b.SFXM and a.MZXH = :MZXH where a.SFXM = :SFXM group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
								MZXX = dao.doQuery(hql.toString(), parameters)
										.get(0);
								SFXMS = dao.doSqlQuery(hql1.toString(),
										parameters1);
								for (int i = 0; i < SFXMS.size(); i++) {
									Map<String, Object> SFXM = SFXMS.get(i);
									if (SFXM.get("MZPL") != null
											&& (SFXM.get("MZPL") + "").length() > 0) {
										if ("其 它".equals(SFXM.get("MCSX") + "")|| "其它".equals(SFXM.get("MCSX")+ "")) {
											mzfp.put("QTPL", SFXM.get("MZPL"));
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											if (SFXM.get("ZJJE") != null&& (SFXM.get("ZJJE") + "").length() > 0) {
												mzfp.put("XMJE"+ SFXM.get("MZPL"),SFXM.get("ZJJE") + "");
											} else {
												mzfp.put("XMJE"+ SFXM.get("MZPL"),"0.00");
											}
											continue;
										} else {

											if (SFXM.get("ZJJE") != null&& (SFXM.get("ZJJE") + "").length() > 0) {
												if (Double.parseDouble(SFXM
														.get("ZJJE") + "") > 0) {
													mzfp.put("SFXM"+ SFXM.get("MZPL"),SFXM.get("MCSX")+ "");
													mzfp.put("XMJE"+ SFXM.get("MZPL"),SFXM.get("ZJJE")+ "");
												}
											}
										}
									} else if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											if (mzfp.containsKey("QTJE")) {
												mzfp.put("QTJE",(Double.parseDouble(mzfp.get("QTJE")
														+ "") + Double.parseDouble(SFXM.get("ZJJE")+ ""))
														+ "");
											} else {
												mzfp.put("QTJE",SFXM.get("ZJJE") + "");
											}
										}
									}
								}
								if (mzfp.containsKey("QTJE")) {
									if (mzfp.containsKey("XMJE"
											+ mzfp.get("QTPL"))
											&& (mzfp.get("XMJE"
											+ mzfp.get("QTPL")) + "")
											.length() > 0) {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp.get("XMJE"
														+ mzfp.get("QTPL"))
														+ "")
														+ Double.parseDouble(mzfp
														.get("QTJE")
														+ ""));
									} else {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp
														.get("QTJE") + ""));
									}
								} else {
									if (mzfp.containsKey("SFXM"
											+ mzfp.get("QTPL"))) {
										mzfp.remove("SFXM" + mzfp.get("QTPL"));
										mzfp.remove("XMJE" + mzfp.get("QTPL"));
									}
								}
								mzfp.put("SW", upint[sw]);
								mzfp.put("W", upint[w]);
								mzfp.put("Q", upint[q]);
								mzfp.put("B", upint[b]);
								mzfp.put("S", upint[s]);
								mzfp.put("Y", upint[y]);
								mzfp.put("J", upint[j]);
								mzfp.put("F", upint[f]);
								if ("6103".equals(MZXX.get("BRXZ") + "")) {
									mzfp.put("BZ", "'小病'免费:3.00");
									mzfp.put("GRZF","个人缴费:"+ String.format("%1$.2f",(Double.parseDouble(MZXX
											.get("HJJE")
											+ "") - 3.00)));
								}
								if ("6104".equals(MZXX.get("BRXZ") + "")) {
									mzfp.put("GRZF", "个人缴费:3.00");
									mzfp.put(
											"BZ",
											"股民减免:"
													+ String.format(
													"%1$.2f",
													(Double.parseDouble(MZXX
															.get("HJJE")
															+ "") - 3.00)));
								}
								if ("6089".equals(MZXX.get("BRXZ") + "")) {
									mzfp.put("BZ",
											"民政救助金额:" + MZXX.get("MZJZJE") + "");
								}
							}
							mzfp.put("JSFS", MZXX.get("JSFS") + "");

							response.put(BSPHISSystemArgument.FPYL,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.FPYL, ctx));
							response.put(BSPHISSystemArgument.MZHJSFDYJMC,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.MZHJSFDYJMC,
											ctx));
							mzfp.put("PAGE","(" + page + "/" + MZXX.get("FPZS") + ")");
							mzfps.add(mzfp);
							page++;
						}
					} else {
						Map<String, Object> mzfp = new HashMap<String, Object>();
						mzfp.putAll(temp_mzfp);
						Map<String, Object> parameters1 = new HashMap<String, Object>();
						StringBuffer hql1 = new StringBuffer(
								"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE,b.MZPL as MZPL from ");
						hql1.append("GY_SFXM b left outer join MS_SFMX a on a.SFXM = b.SFXM and a.MZXH = :MZXH where a.SFXM = :SFXM group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");

						parameters1.put("MZXH", MZXX.get("MZXH"));
						parameters1.put("SFXM",Long.parseLong(fygb.get("SFXM") + ""));
						SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
						for (int i = 0; i < SFXMS.size(); i++) {
							Map<String, Object> SFXM = SFXMS.get(i);
							if (SFXM.get("MZPL") != null
									&& (SFXM.get("MZPL") + "").length() > 0) {
								if ("其 它".equals(SFXM.get("MCSX") + "")|| "其它".equals(SFXM.get("MCSX") + "")) {
									mzfp.put("QTPL", SFXM.get("MZPL"));
									mzfp.put("SFXM" + SFXM.get("MZPL"),SFXM.get("MCSX") + "");
									if (SFXM.get("ZJJE") != null && (SFXM.get("ZJJE") + "").length() > 0) {
										mzfp.put("XMJE" + SFXM.get("MZPL"),SFXM.get("ZJJE") + "");
									} else {
										mzfp.put("XMJE" + SFXM.get("MZPL"),"0.00");
									}
									continue;
								} else {

									if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											mzfp.put("XMJE" + SFXM.get("MZPL"),
													SFXM.get("ZJJE") + "");
										}
									}
								}
							} else if (SFXM.get("ZJJE") != null && (SFXM.get("ZJJE") + "").length() > 0) {
								if (Double.parseDouble(SFXM.get("ZJJE") + "") > 0) {
									if (mzfp.containsKey("QTJE")) {
										mzfp.put("QTJE",(Double.parseDouble(mzfp
												.get("QTJE") + "") + Double.parseDouble(SFXM
												.get("ZJJE") + ""))+ "");
									} else {
										mzfp.put("QTJE", SFXM.get("ZJJE") + "");
									}
								}
							}
						}
						if (mzfp.containsKey("QTJE")) {
							if (mzfp.containsKey("XMJE" + mzfp.get("QTPL"))
									&& (mzfp.get("XMJE" + mzfp.get("QTPL")) + "")
									.length() > 0) {
								mzfp.put("XMJE" + mzfp.get("QTPL"),
										Double.parseDouble(mzfp.get("XMJE"
												+ mzfp.get("QTPL"))+ "")
												+ Double.parseDouble(mzfp.get("QTJE") + ""));
							} else {
								mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("QTJE")+ ""));
							}
						} else {
							if (mzfp.containsKey("SFXM" + mzfp.get("QTPL"))) {
								mzfp.remove("SFXM" + mzfp.get("QTPL"));
								mzfp.remove("XMJE" + mzfp.get("QTPL"));
							}
						}
						mzfp.put("SW", upint[sw]);
						mzfp.put("W", upint[w]);
						mzfp.put("Q", upint[q]);
						mzfp.put("B", upint[b]);
						mzfp.put("S", upint[s]);
						mzfp.put("Y", upint[y]);
						mzfp.put("J", upint[j]);
						mzfp.put("F", upint[f]);

						String detailsSql = "select c.YPMC as YPMC,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and b.FYGB=:SFXM and b.ZFYP!=1";
						detailsSql += " union all ";
						detailsSql += "select c.FYMC as YPMC,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH and b.FYGB=:SFXM";
						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						// add by caijy at 2014.10.8 for自备药
						// for (Map<String, Object> d : details) {
						// if (Integer.parseInt(d.get("ZFYP") + "") == 1) {
						// d.put("YPMC", "(自备)" + d.get("YPMC"));
						// }
						// }
						int detsize = details.size();
						if (Integer.parseInt(MZFPMXSL) != 0
								&& Integer.parseInt(MZFPMXSL) < detsize) {
							detsize = Integer.parseInt(MZFPMXSL);
						}
						for (int i = 0; i < detsize; i++) {
							Map<String, Object> detail = details.get(i);
							mzfp.put("MXMC" + (i + 1), detail.get("YPMC"));
							mzfp.put("MXDJ" + (i + 1), detail.get("YPDJ"));
							mzfp.put("MXSL" + (i + 1), detail.get("YPSL"));
							mzfp.put("MXJE" + (i + 1), detail.get("HJJE"));
						}
						if (detsize < details.size()) {
							mzfp.put("MXMC" + (detsize + 1), "*以上明细信息不全");
						}
						response.put(BSPHISSystemArgument.FPYL, ParameterUtil
								.getParameter(JGID, BSPHISSystemArgument.FPYL,
										ctx));
						response.put(BSPHISSystemArgument.MZHJSFDYJMC,
								ParameterUtil.getParameter(JGID,
										BSPHISSystemArgument.MZHJSFDYJMC, ctx));
						mzfp.put("PAGE", "(" + page + "/" + MZXX.get("FPZS")
								+ ")");
						mzfps.add(mzfp);
						page++;
					}

				}
				response.put("mzfps", mzfps);
			} else if ("2".equals(MZFPFDFS)) {// 检查按执行科室分单，药品按药房分单

				List<Map<String, Object>> yfsbs = dao
						.doSqlQuery(
								"select distinct YFSB as YFSB from MS_CF01 a where FPHM=:FPHM and JGID=:JGID",
								parameters);
				List<Map<String, Object>> zxkss = dao
						.doSqlQuery(
								"select distinct ZXKS as ZXKS from MS_YJ01 a where FPHM=:FPHM and JGID=:JGID",
								parameters);
				for (Map<String, Object> yfsb : yfsbs) {
					parameters.put("YFSB",
							Long.parseLong(yfsb.get("YFSB") + ""));
					if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {
						Map<String, Object> parameters1 = new HashMap<String, Object>();
						parameters1.put("MZXH", MZXX.get("MZXH"));
						parameters1.put("YFSB",
								Long.parseLong(yfsb.get("YFSB") + ""));
						String detailsSql = "select c.YPMC as YPMC,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and a.YFSB=:YFSB and b.ZFYP!=1";
						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						for (Map<String, Object> d : details) {
							if (Integer.parseInt(d.get("ZFYP") + "") == 1) {
								d.put("YPMC", "(自备)" + d.get("YPMC"));
							}
						}
						int n = details.size() / Integer.parseInt(MZFPMXSL);
						if (Integer.parseInt(MZFPMXSL) * n < details.size()) {
							n++;
						}
						for (int jk = 0; jk < n; jk++) {
							Map<String, Object> mzfp = new HashMap<String, Object>();
							mzfp.putAll(temp_mzfp);
							int maxdetal = (jk + 1)
									* Integer.parseInt(MZFPMXSL);
							if (maxdetal > details.size()) {
								maxdetal = details.size();
							}
							int k = 0;
							for (int i = (jk) * Integer.parseInt(MZFPMXSL); i < maxdetal; i++) {
								Map<String, Object> detail = details.get(i);
								mzfp.put(
										"MXMC" + (++k),
										(detail.get("YPMC") + "").substring(
												0,
												(detail.get("YPMC") + "")
														.length() > 13 ? 13
														: (detail.get("YPMC") + "")
														.length()));
								mzfp.put("MXDJ" + (k), detail.get("YPDJ"));
								mzfp.put("MXSL" + (k), detail.get("YPSL"));
								mzfp.put("MXJE" + (k), detail.get("HJJE"));
							}
							if (jk == 0) {
								StringBuffer hql1 = new StringBuffer(
										"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.HJJE) as ZJJE,b.MZPL as MZPL from ");
								hql1.append("GY_SFXM b left outer join MS_CF02 a on a.FYGB = b.SFXM,MS_CF01 c where a.CFSB=c.CFSB and c.YFSB = :YFSB and c.FPHM=:FPHM and c.JGID=:JGID group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
								SFXMS = dao.doSqlQuery(hql1.toString(),
										parameters);
								for (int i = 0; i < SFXMS.size(); i++) {
									Map<String, Object> SFXM = SFXMS.get(i);
									if (SFXM.get("MZPL") != null
											&& (SFXM.get("MZPL") + "").length() > 0) {
										if ("其 它".equals(SFXM.get("MCSX") + "")
												|| "其它".equals(SFXM.get("MCSX")
												+ "")) {
											mzfp.put("QTPL", SFXM.get("MZPL"));
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											if (SFXM.get("ZJJE") != null
													&& (SFXM.get("ZJJE") + "")
													.length() > 0) {
												mzfp.put(
														"XMJE"
																+ SFXM.get("MZPL"),
														SFXM.get("ZJJE") + "");
											} else {
												mzfp.put(
														"XMJE"
																+ SFXM.get("MZPL"),
														"0.00");
											}
											continue;
										} else {

											if (SFXM.get("ZJJE") != null
													&& (SFXM.get("ZJJE") + "")
													.length() > 0) {
												if (Double.parseDouble(SFXM
														.get("ZJJE") + "") > 0) {
													mzfp.put(
															"SFXM"
																	+ SFXM.get("MZPL"),
															SFXM.get("MCSX")
																	+ "");
													mzfp.put(
															"XMJE"
																	+ SFXM.get("MZPL"),
															SFXM.get("ZJJE")
																	+ "");
												}
												// } else {
												// response.put("XMJE" +
												// SFXM.get("MZPL"), "0.00");
											}
										}
									} else if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											if (mzfp.containsKey("QTJE")) {
												mzfp.put(
														"QTJE",
														(Double.parseDouble(mzfp
																.get("QTJE")
																+ "") + Double
																.parseDouble(SFXM
																		.get("ZJJE")
																		+ ""))
																+ "");
											} else {
												mzfp.put("QTJE",
														SFXM.get("ZJJE") + "");
											}
										}
									}
								}
								if (mzfp.containsKey("QTJE")) {
									if (mzfp.containsKey("XMJE"
											+ mzfp.get("QTPL"))
											&& (mzfp.get("XMJE"
											+ mzfp.get("QTPL")) + "")
											.length() > 0) {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp.get("XMJE"
														+ mzfp.get("QTPL"))
														+ "")
														+ Double.parseDouble(mzfp
														.get("QTJE")
														+ ""));
									} else {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp
														.get("QTJE") + ""));
									}
								} else {
									if (mzfp.containsKey("SFXM"
											+ mzfp.get("QTPL"))) {
										mzfp.remove("SFXM" + mzfp.get("QTPL"));
										mzfp.remove("XMJE" + mzfp.get("QTPL"));
									}
								}
								mzfp.put("SW", upint[sw]);
								mzfp.put("W", upint[w]);
								mzfp.put("Q", upint[q]);
								mzfp.put("B", upint[b]);
								mzfp.put("S", upint[s]);
								mzfp.put("Y", upint[y]);
								mzfp.put("J", upint[j]);
								mzfp.put("F", upint[f]);
							}
							mzfp.put("SFY", MZXX.get("SFY") + "");
							mzfp.put("JSFS", MZXX.get("JSFS") + "");
							response.put(BSPHISSystemArgument.FPYL,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.FPYL, ctx));
							response.put(BSPHISSystemArgument.MZHJSFDYJMC,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.MZHJSFDYJMC,
											ctx));
							mzfp.put("PAGE","(" + page + "/" + MZXX.get("FPZS") + ")");
							mzfps.add(mzfp);
							page++;
						}
					} else {
						Map<String, Object> mzfp = new HashMap<String, Object>();
						mzfp.putAll(temp_mzfp);
						Map<String, Object> parameters1 = new HashMap<String, Object>();
						StringBuffer hql1 = new StringBuffer(
								"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.HJJE) as ZJJE,b.MZPL as MZPL from ");
						hql1.append("GY_SFXM b left outer join MS_CF02 a on a.FYGB = b.SFXM,MS_CF01 c where a.CFSB=c.CFSB and c.YFSB = :YFSB and c.FPHM=:FPHM and c.JGID=:JGID group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
						parameters1.put("MZXH", MZXX.get("MZXH"));
						parameters1.put("YFSB",
								Long.parseLong(yfsb.get("YFSB") + ""));
						SFXMS = dao.doSqlQuery(hql1.toString(), parameters);
						for (int i = 0; i < SFXMS.size(); i++) {
							Map<String, Object> SFXM = SFXMS.get(i);
							if (SFXM.get("MZPL") != null&& (SFXM.get("MZPL") + "").length() > 0) {
								if ("其 它".equals(SFXM.get("MCSX") + "")|| "其它".equals(SFXM.get("MCSX") + "")) {
									mzfp.put("QTPL", SFXM.get("MZPL"));
									mzfp.put("SFXM" + SFXM.get("MZPL"),SFXM.get("MCSX") + "");
									if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										mzfp.put("XMJE" + SFXM.get("MZPL"),
												SFXM.get("ZJJE") + "");
									} else {
										mzfp.put("XMJE" + SFXM.get("MZPL"),
												"0.00");
									}
									continue;
								} else {

									if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											mzfp.put("XMJE" + SFXM.get("MZPL"),
													SFXM.get("ZJJE") + "");
										}
									}
								}
							} else if (SFXM.get("ZJJE") != null
									&& (SFXM.get("ZJJE") + "").length() > 0) {
								if (Double.parseDouble(SFXM.get("ZJJE") + "") > 0) {
									if (mzfp.containsKey("QTJE")) {
										mzfp.put(
												"QTJE",
												(Double.parseDouble(mzfp
														.get("QTJE") + "") + Double.parseDouble(SFXM
														.get("ZJJE") + ""))
														+ "");
									} else {
										mzfp.put("QTJE", SFXM.get("ZJJE") + "");
									}
								}
							}
						}
						if (mzfp.containsKey("QTJE")) {
							if (mzfp.containsKey("XMJE" + mzfp.get("QTPL"))
									&& (mzfp.get("XMJE" + mzfp.get("QTPL")) + "")
									.length() > 0) {
								mzfp.put(
										"XMJE" + mzfp.get("QTPL"),
										Double.parseDouble(mzfp.get("XMJE"
												+ mzfp.get("QTPL"))
												+ "")
												+ Double.parseDouble(mzfp
												.get("QTJE") + ""));
							} else {
								mzfp.put("XMJE" + mzfp.get("QTPL"),Double.parseDouble(mzfp.get("QTJE")+ ""));
							}
						} else {
							if (mzfp.containsKey("SFXM" + mzfp.get("QTPL"))) {
								mzfp.remove("SFXM" + mzfp.get("QTPL"));
								mzfp.remove("XMJE" + mzfp.get("QTPL"));
							}
						}
						mzfp.put("SW", upint[sw]);
						mzfp.put("W", upint[w]);
						mzfp.put("Q", upint[q]);
						mzfp.put("B", upint[b]);
						mzfp.put("S", upint[s]);
						mzfp.put("Y", upint[y]);
						mzfp.put("J", upint[j]);
						mzfp.put("F", upint[f]);
						String detailsSql = "select c.YPMC as YPMC,b.YPSL as YPSL,b.YPDJ as YPDJ,b.HJJE as HJJE from MS_CF01 a,MS_CF02 b,YK_TYPK c where a.CFSB = b.CFSB and b.YPXH = c.YPXH and a.MZXH = :MZXH and a.YFSB=:YFSB and b.ZFYP!=1";
						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						int detsize = details.size();
						if (Integer.parseInt(MZFPMXSL) != 0
								&& Integer.parseInt(MZFPMXSL) < detsize) {
							detsize = Integer.parseInt(MZFPMXSL);
						}
						for (int i = 0; i < detsize; i++) {
							Map<String, Object> detail = details.get(i);
							mzfp.put("MXMC" + (i + 1), detail.get("YPMC"));
							mzfp.put("MXDJ" + (i + 1), detail.get("YPDJ"));
							mzfp.put("MXSL" + (i + 1), detail.get("YPSL"));
							mzfp.put("MXJE" + (i + 1), detail.get("HJJE"));
						}
						if (detsize < details.size()) {
							mzfp.put("MXMC" + (detsize + 1), "*以上明细信息不全");
						}
						response.put(BSPHISSystemArgument.FPYL, ParameterUtil
								.getParameter(JGID, BSPHISSystemArgument.FPYL,
										ctx));
						response.put(BSPHISSystemArgument.MZHJSFDYJMC,
								ParameterUtil.getParameter(JGID,
										BSPHISSystemArgument.MZHJSFDYJMC, ctx));
						mzfp.put("PAGE", "(" + page + "/" + MZXX.get("FPZS")
								+ ")");
						mzfps.add(mzfp);
						page++;
					}
				}
				for (Map<String, Object> zxks : zxkss) {
					if (zxks.get("ZXKS") == null || zxks.get("ZXKS") == "") {
						parameters.put("ZXKS", 0L);
					} else {
						parameters.put("ZXKS",
								Long.parseLong(zxks.get("ZXKS") + ""));
					}
					if ("1".equals(MZFPSFLD) && Integer.parseInt(MZFPMXSL) > 0) {
						Map<String, Object> parameters1 = new HashMap<String, Object>();
						parameters1.put("MZXH", MZXX.get("MZXH"));
						if (zxks.get("ZXKS") == null || zxks.get("ZXKS") == "") {
							parameters1.put("ZXKS", 0L);
						} else {
							parameters1.put("ZXKS",
									Long.parseLong(zxks.get("ZXKS") + ""));
						}

						String detailsSql = "select c.FYMC as YPMC,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH and nvl(a.ZXKS,0)=:ZXKS";

						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						int n = details.size() / Integer.parseInt(MZFPMXSL);
						if (Integer.parseInt(MZFPMXSL) * n < details.size()) {
							n++;
						}
						for (int jk = 0; jk < n; jk++) {
							Map<String, Object> mzfp = new HashMap<String, Object>();
							mzfp.putAll(temp_mzfp);
							int maxdetal = (jk + 1)
									* Integer.parseInt(MZFPMXSL);
							if (maxdetal > details.size()) {
								maxdetal = details.size();
							}
							int k = 0;
							for (int i = (jk) * Integer.parseInt(MZFPMXSL); i < maxdetal; i++) {
								Map<String, Object> detail = details.get(i);
								mzfp.put(
										"MXMC" + (++k),
										(detail.get("YPMC") + "").substring(
												0,
												(detail.get("YPMC") + "")
														.length() > 13 ? 13
														: (detail.get("YPMC") + "")
														.length()));
								mzfp.put("MXDJ" + (k), detail.get("YPDJ"));
								mzfp.put("MXSL" + (k), detail.get("YPSL"));
								mzfp.put("MXJE" + (k), detail.get("HJJE"));
							}
							if (jk == 0) {
								StringBuffer hql1 = new StringBuffer(
										"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.HJJE) as ZJJE,b.MZPL as MZPL from ");
								hql1.append("GY_SFXM b left outer join MS_YJ02 a on a.FYGB = b.SFXM,MS_YJ01 c where a.YJXH=c.YJXH and nvl(c.ZXKS,0) = :ZXKS and c.FPHM=:FPHM and c.JGID=:JGID group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
								SFXMS = dao.doSqlQuery(hql1.toString(),
										parameters);
								for (int i = 0; i < SFXMS.size(); i++) {
									Map<String, Object> SFXM = SFXMS.get(i);
									if (SFXM.get("MZPL") != null
											&& (SFXM.get("MZPL") + "").length() > 0) {
										if ("其 它".equals(SFXM.get("MCSX") + "")
												|| "其它".equals(SFXM.get("MCSX")
												+ "")) {
											mzfp.put("QTPL", SFXM.get("MZPL"));
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											if (SFXM.get("ZJJE") != null
													&& (SFXM.get("ZJJE") + "")
													.length() > 0) {
												mzfp.put(
														"XMJE"
																+ SFXM.get("MZPL"),
														SFXM.get("ZJJE") + "");
											} else {
												mzfp.put(
														"XMJE"
																+ SFXM.get("MZPL"),
														"0.00");
											}
											continue;
										} else {

											if (SFXM.get("ZJJE") != null
													&& (SFXM.get("ZJJE") + "")
													.length() > 0) {
												if (Double.parseDouble(SFXM
														.get("ZJJE") + "") > 0) {
													mzfp.put(
															"SFXM"
																	+ SFXM.get("MZPL"),
															SFXM.get("MCSX")
																	+ "");
													mzfp.put(
															"XMJE"
																	+ SFXM.get("MZPL"),
															SFXM.get("ZJJE")
																	+ "");
												}
											}
										}
									} else if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											if (mzfp.containsKey("QTJE")) {
												mzfp.put(
														"QTJE",
														(Double.parseDouble(mzfp
																.get("QTJE")
																+ "") + Double
																.parseDouble(SFXM
																		.get("ZJJE")
																		+ ""))
																+ "");
											} else {
												mzfp.put("QTJE",
														SFXM.get("ZJJE") + "");
											}
										}
									}
								}
								if (mzfp.containsKey("QTJE")) {
									if (mzfp.containsKey("XMJE"
											+ mzfp.get("QTPL"))
											&& (mzfp.get("XMJE"
											+ mzfp.get("QTPL")) + "")
											.length() > 0) {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp.get("XMJE"
														+ mzfp.get("QTPL"))
														+ "")
														+ Double.parseDouble(mzfp
														.get("QTJE")
														+ ""));
									} else {
										mzfp.put(
												"XMJE" + mzfp.get("QTPL"),
												Double.parseDouble(mzfp
														.get("QTJE") + ""));
									}
								} else {
									if (mzfp.containsKey("SFXM"
											+ mzfp.get("QTPL"))) {
										mzfp.remove("SFXM" + mzfp.get("QTPL"));
										mzfp.remove("XMJE" + mzfp.get("QTPL"));
									}
								}
								mzfp.put("SW", upint[sw]);
								mzfp.put("W", upint[w]);
								mzfp.put("Q", upint[q]);
								mzfp.put("B", upint[b]);
								mzfp.put("S", upint[s]);
								mzfp.put("Y", upint[y]);
								mzfp.put("J", upint[j]);
								mzfp.put("F", upint[f]);
							}
							mzfp.put("JSFS", MZXX.get("JSFS") + "");
							response.put(BSPHISSystemArgument.FPYL,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.FPYL, ctx));
							response.put(BSPHISSystemArgument.MZHJSFDYJMC,
									ParameterUtil.getParameter(JGID,
											BSPHISSystemArgument.MZHJSFDYJMC,
											ctx));
							mzfp.put("PAGE","(" + page + "/" + MZXX.get("FPZS") + ")");
							mzfps.add(mzfp);
							page++;
						}
					} else {
						Map<String, Object> mzfp = new HashMap<String, Object>();
						mzfp.putAll(temp_mzfp);
						Map<String, Object> parameters1 = new HashMap<String, Object>();
						StringBuffer hql1 = new StringBuffer(
								"select b.MZGB as MZGB,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.HJJE) as ZJJE,b.MZPL as MZPL from ");
						hql1.append("GY_SFXM b left outer join MS_YJ02 a on a.FYGB = b.SFXM,MS_YJ01 c where a.YJXH=c.YJXH and nvl(c.ZXKS,0) = :ZXKS and c.FPHM=:FPHM and c.JGID=:JGID group by b.MZGB,b.MZPL,b.MCSX,b.SFMC ");
						parameters.remove("YFSB");
						parameters1.put("MZXH", MZXX.get("MZXH"));
						parameters1.put("ZXKS",
								Long.parseLong(zxks.get("ZXKS") + ""));
						SFXMS = dao.doSqlQuery(hql1.toString(), parameters);
						for (int i = 0; i < SFXMS.size(); i++) {
							Map<String, Object> SFXM = SFXMS.get(i);
							if (SFXM.get("MZPL") != null
									&& (SFXM.get("MZPL") + "").length() > 0) {
								if ("其 它".equals(SFXM.get("MCSX") + "")
										|| "其它".equals(SFXM.get("MCSX") + "")) {
									mzfp.put("QTPL", SFXM.get("MZPL"));
									mzfp.put("SFXM" + SFXM.get("MZPL"),
											SFXM.get("MCSX") + "");
									if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										mzfp.put("XMJE" + SFXM.get("MZPL"),
												SFXM.get("ZJJE") + "");
									} else {
										mzfp.put("XMJE" + SFXM.get("MZPL"),
												"0.00");
									}
									continue;
								} else {

									if (SFXM.get("ZJJE") != null
											&& (SFXM.get("ZJJE") + "").length() > 0) {
										if (Double.parseDouble(SFXM.get("ZJJE")
												+ "") > 0) {
											mzfp.put("SFXM" + SFXM.get("MZPL"),
													SFXM.get("MCSX") + "");
											mzfp.put("XMJE" + SFXM.get("MZPL"),
													SFXM.get("ZJJE") + "");
										}
									}
								}
							} else if (SFXM.get("ZJJE") != null
									&& (SFXM.get("ZJJE") + "").length() > 0) {
								if (Double.parseDouble(SFXM.get("ZJJE") + "") > 0) {
									if (mzfp.containsKey("QTJE")) {
										mzfp.put(
												"QTJE",
												(Double.parseDouble(mzfp
														.get("QTJE") + "") + Double.parseDouble(SFXM
														.get("ZJJE") + ""))
														+ "");
									} else {
										mzfp.put("QTJE", SFXM.get("ZJJE") + "");
									}
								}
							}
						}
						if (mzfp.containsKey("QTJE")) {
							if (mzfp.containsKey("XMJE" + mzfp.get("QTPL"))
									&& (mzfp.get("XMJE" + mzfp.get("QTPL")) + "")
									.length() > 0) {
								mzfp.put(
										"XMJE" + mzfp.get("QTPL"),
										Double.parseDouble(mzfp.get("XMJE"
												+ mzfp.get("QTPL"))
												+ "")
												+ Double.parseDouble(mzfp
												.get("QTJE") + ""));
							} else {
								mzfp.put(
										"XMJE" + mzfp.get("QTPL"),
										Double.parseDouble(mzfp.get("QTJE")
												+ ""));
							}
						} else {
							if (mzfp.containsKey("SFXM" + mzfp.get("QTPL"))) {
								mzfp.remove("SFXM" + mzfp.get("QTPL"));
								mzfp.remove("XMJE" + mzfp.get("QTPL"));
							}
						}
						mzfp.put("SW", upint[sw]);
						mzfp.put("W", upint[w]);
						mzfp.put("Q", upint[q]);
						mzfp.put("B", upint[b]);
						mzfp.put("S", upint[s]);
						mzfp.put("Y", upint[y]);
						mzfp.put("J", upint[j]);
						mzfp.put("F", upint[f]);
						String detailsSql = "select c.FYMC as YPMC,b.YLSL as YPSL,b.YLDJ as YPDJ,b.HJJE as HJJE from MS_YJ01 a,MS_YJ02 b,GY_YLSF c where a.YJXH = b.YJXH and b.YLXH = c.FYXH and a.MZXH = :MZXH and a.ZXKS=:ZXKS";
						List<Map<String, Object>> details = dao.doSqlQuery(
								detailsSql, parameters1);
						int detsize = details.size();
						if (Integer.parseInt(MZFPMXSL) != 0
								&& Integer.parseInt(MZFPMXSL) < detsize) {
							detsize = Integer.parseInt(MZFPMXSL);
						}
						for (int i = 0; i < detsize; i++) {
							Map<String, Object> detail = details.get(i);
							mzfp.put("MXMC" + (i + 1), detail.get("YPMC"));
							mzfp.put("MXDJ" + (i + 1), detail.get("YPDJ"));
							mzfp.put("MXSL" + (i + 1), detail.get("YPSL"));
							mzfp.put("MXJE" + (i + 1), detail.get("HJJE"));
						}
						if (detsize < details.size()) {
							mzfp.put("MXMC" + (detsize + 1), "*以上明细信息不全");
						}
						response.put(BSPHISSystemArgument.FPYL, ParameterUtil
								.getParameter(JGID, BSPHISSystemArgument.FPYL,
										ctx));
						response.put(BSPHISSystemArgument.MZHJSFDYJMC,
								ParameterUtil.getParameter(JGID,
										BSPHISSystemArgument.MZHJSFDYJMC, ctx));
						mzfp.put("PAGE", "(" + page + "/" + MZXX.get("FPZS")
								+ ")");
						mzfps.add(mzfp);
						page++;
					}
				}
				response.put("mzfps", mzfps);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2014-9-19
	 * @description 收费查询单据前查询是否挂了多个科室
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public long queryKs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_temp = queryGHKS(body, ctx);
		if (!"200".equals(map_temp.get("code") + "")) {
			return 0;
		} else {
			List<Map<String, Object>> l = (List<Map<String, Object>>) map_temp
					.get("body");
			if (l.size() == 1) {
				return MedicineUtils.parseLong(l.get(0).get("KSDM"));
			}
			return -1;
		}
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2014-9-19
	 * @description 收费病人科室列表
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	// @SuppressWarnings("unchecked")
	public Map<String, Object> queryGHKS(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_ret = MedicineUtils.getRetMap();
		try {
//		Map<String,Object> map_temp=new HashMap<String,Object>();
//		doQueryDJ(body,null,map_temp,ctx);
			long BRID = Long.parseLong(body.get("BRID") + "");
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnit().getId();
			String GHXQ = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.GHXQ, ctx);
			String XQJSFS = ParameterUtil.getParameter(manaUnitId,
					BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
			SimpleDateFormat matter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			if ("1".equals(XQJSFS + "")) {
				now = matter.parse(BSHISUtil.getDate() + " 23:59:59");
			}
			if (GHXQ == null || !GHXQ.matches("[0-9]+")) {
				throw new ModelDataOperationException("挂号效期参数设置错误，请联系管理员!");
			}
			Map<String, Object> MS_GHMXparameters = new HashMap<String, Object>();
			MS_GHMXparameters.put("GHSJ",
					DateUtils.addDays(now, -Integer.parseInt(GHXQ)));
			MS_GHMXparameters.put("JGID", manaUnitId);
			MS_GHMXparameters.put("BRID",BRID);
			MS_GHMXparameters.put("NOW", new Date());
			StringBuffer hql_ghks =new StringBuffer();
			hql_ghks.append("SELECT c.JZXH as JZXH,b.KSMC as KSMC FROM MS_GHMX a,MS_GHKS b,YS_MZ_JZLS c WHERE c.GHXH=a.SBXH and a.KSDM=b.KSDM and a.JGID = :JGID AND a.BRID = :BRID AND a.THBZ = 0 AND a.GHSJ >:GHSJ AND a.GHSJ < :NOW ");
			List<Map<String,Object>> list_allRecord=dao.doSqlQuery(hql_ghks.toString(), MS_GHMXparameters);
			if(list_allRecord==null||list_allRecord.size()==0){
				return MedicineUtils.getRetMap("科室查询失败,请检查是否有单子已被收费");
			}
//		Set<Long> s=new HashSet<Long>();
//		for(Map<String,Object> map_record:list_allRecord){
//			s.add(MedicineUtils.parseLong(map_record.get("KDKS")));
//		}
//		if(s.size()<1){
//			return MedicineUtils.getRetMap("科室查询失败,请检查是否有单子已被收费");
//		}
//		StringBuffer hql=new StringBuffer();
//		hql.append("select KSDM as KSDM,KSMC as KSMC from MS_GHKS where KSDM in (:ksdms)");
//		Map<String,Object> map_par=new HashMap<String,Object>();
//		map_par.put("ksdms", s);
//		
//			List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
//			if(list_ret==null||list_ret.size()<1){
//				return MedicineUtils.getRetMap("科室查询失败,请检查科室基础数据是否有误");
//			}
			map_ret.put("body", list_allRecord);

		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "科室查询失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "科室查询失败", e);
		}
		return map_ret;
	}

	/**
	 * 健康证 默认信息查询
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPhysicalMr(Map<String, Object> req,
								  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		try {
			String BRXZ = ParameterUtil.getParameter(JGID, "MRXZ", "",
					"健康证人员默认的费用性质系统参数", ctx);
			if ("".equals(BRXZ) || "null".equals(BRXZ)) {
				StringBuffer sql = new StringBuffer(
						"select a.BRXZ as BRXZ, a.XZMC as XZMC  from GY_BRXZ a where a.BRXZ !=:BRXZ and a.MZSY=:MZSY and a.SJXZ>=:SJXZ ");
				Map<String, Object> parameters_sql = new HashMap<String, Object>();
				parameters_sql.put("BRXZ", "-1");
				parameters_sql.put("MZSY", "1");
				parameters_sql.put("SJXZ", "1");
				List<Map<String, Object>> list_sql = dao.doSqlQuery(
						sql.toString(), parameters_sql);
				Map<String, Object> ksmcMap_sql = new HashMap<String, Object>();

				if (list_sql.size() > 0) {
					ksmcMap_sql = list_sql.get(0);
					BRXZ = ksmcMap_sql.get("BRXZ") + "";
				}
			}

			EmpiService EmpMzhm = new EmpiService();
			EmpMzhm.doOutPatientNumber(req, res, dao, ctx);
			String MZHM = res.get("MZHM") + "";
			res.put("BRXZ", BRXZ);
			res.put("MZHM", MZHM);
			res.put("cardNo", MZHM);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 获取门诊号码
		catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 健康证 EMPIID保存
	 *
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doSavePhysicalMr(Map<String, Object> req,
								 Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> data = new HashMap<String, Object>();
		data = (Map<String, Object>) req.get("body");
		data.put("photo", "");// 图片置空
		Map<String, Object> body = data;// 挂号数据
		try {
			/**
			 * empiService EMPI个人基本信息保存
			 *
			 */

			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			String UserId = user.getUserId() + "";
			// String AGE = data.get("age") + "";
			// String s1=AGE;
			// char[] c=s1.toCharArray();
			// String pp="";
			// for (int i = 0; i<c.length; i++){
			// String tt=c[i]+"";
			// if(tt=="岁" || "岁".equals(tt)){
			// }else{
			// pp=pp+tt;
			// }
			// }
			// AGE=pp;
			// Calendar now = Calendar.getInstance();
			// String AgeYear = now.get(Calendar.YEAR) + "";
			// String BIRTHDAY = "";
			// if (!"".equals(AGE) && AGE != null && !"null".equals(AGE)) {
			// int t1 = Integer.parseInt(AGE);
			// int t2 = Integer.parseInt(AgeYear);
			// int t = t2 - t1;
			// BIRTHDAY = t + "/1/1";
			// }
			// data.put("CSNY", BIRTHDAY);
			data.put("CSNY", data.get("birthday"));
			EmpiService EmpMzhm = new EmpiService();
			try {
				EmpMzhm.doOutPatientNumber(req, res, dao, ctx);
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}// 获取门诊号码
			String MZHM = res.get("MZHM") + "";
			res.put("HTCS", "0");
			res.put("CSMC", "");
			if ("".equals(MZHM) || "null".equals(MZHM) || MZHM == null) {
				res.put("HTCS", "1");
				res.put("CSMC", "无法获取门诊号码！");
			} else {
				data.put("MZHM", MZHM);
				body.put("MZHM", MZHM);
				body.put("CSNY", data.get("birthday"));

				Map<String, Object> PIXData = EmpiUtil.changeToPIXFormat(data);
				try {
					data = EmpiUtil.submitPerson(dao, ctx, data, PIXData);
				} catch (ServiceException e1) {
					logger.error("Void invoice fails.", e1);
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "empiId号码获取失败");
				}
				String empiId = (String) data.get("empiId") + "";
				data.put("empiId", empiId);//
				res.put("empiId", empiId);//
				if ("".equals(empiId) || "null".equals(empiId)
						|| empiId == null) {
					res.put("HTCS", "1");
					res.put("CSMC", "无法获取EMPIID,请查证！");
				} else {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("BIRTHDAY", data.get("birthday"));// 出生日期
					parameters.put("CREATEUNIT", manaUnitId);// 建档机构
					parameters.put("CREATEUSER", UserId);// 建档人
					body.put("JGID", UserId);
					parameters.put("STATUS", 0);// 状态
					parameters.put("empiId", empiId);
					/**
					 * EMPI个人档案
					 *
					 */

					/**
					 * EMPI个人基本信息保存结束
					 *
					 */
					/**
					 * EMPI卡保存MPI_Card
					 *
					 */
					String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
							"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
					Map<String, Object> parameters_Card = new HashMap<String, Object>();
					String CARDNO = data.get("cardNo") + "";
					parameters_Card.put("cardNo", CARDNO);
					parameters_Card.put("empiId", empiId);
					parameters_Card.put("cardTypeCode", KLX);
					parameters_Card.put("createUnit", manaUnitId);
					parameters_Card.put("createUser", UserId);
					parameters_Card.put("status", "0");
					parameters_Card.put("lastModifyUnit", manaUnitId);
					parameters_Card.put("lastModifyUser", UserId);
					Map<String, Object> Card;
					try {
						Card = dao.doSave("create", BSPHISEntryNames.MPI_Card,
								parameters_Card, false);
						String cardId = Card.get("cardId") + "";
						parameters_Card.put("cardId", cardId);
						res.put("cardId", cardId);
					} catch (PersistentDataOperationException e1) {
						logger.error("Void invoice fails.", e1);
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"门诊号码获取卡类型保存失败");
					}
				}
			}

		} catch (ValidateException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

	/**
	 * 健康证 保存
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePhysical(Map<String, Object> req,
							   Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> data = new HashMap<String, Object>();
		data = (Map<String, Object>) req.get("body");
		data.put("photo", "");// 图片置空
		// Map<String, Object> GhData = data;// 挂号数据
		Map<String, Object> body = data;// 挂号数据
		try {
			/**
			 * empiService EMPI个人基本信息update
			 *
			 */

			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			String UserId = user.getUserId() + "";
			// String AGE = data.get("age") + "";
			// String s1=AGE;
			// char[] c=s1.toCharArray();
			// String pp="";
			// for (int i = 0; i<c.length; i++){
			// String tt=c[i]+"";
			// if(tt=="岁" || "岁".equals(tt)){
			// }else{
			// pp=pp+tt;
			// }
			// }
			// AGE=pp;
			// Calendar now = Calendar.getInstance();
			// String AgeYear = now.get(Calendar.YEAR) + "";
			// String BIRTHDAY = "";
			// if (!"".equals(AGE) && AGE != null && !"null".equals(AGE)) {
			// int t1 = Integer.parseInt(AGE);
			// int t2 = Integer.parseInt(AgeYear);
			// int t = t2 - t1;
			// BIRTHDAY = t + "/1/1";
			// }
			// data.put("CSNY", BIRTHDAY);
			// EmpiService EmpMzhm = new EmpiService();
			data.put("CSNY", data.get("birthday"));
			String MZHM = body.get("MZHM") + "";
			res.put("HTCS", "0");
			res.put("CSMC", "");
			if ("".equals(MZHM) || "null".equals(MZHM) || MZHM == null) {
				res.put("HTCS", "1");
				res.put("CSMC", "无法获取门诊号码！");
			} else {
				data.put("MZHM", MZHM);
				body.put("MZHM", MZHM);
				// body.put("CSNY", BIRTHDAY);
				body.put("CSNY", data.get("birthday"));
				String empiId = req.get("empiId") + "";
				data.put("empiId", empiId);//
				if ("".equals(empiId) || "null".equals(empiId)
						|| empiId == null) {
					res.put("HTCS", "1");
					res.put("CSMC", "无法获取EMPIID,请查证！");
				} else {
					String empiid_PD = "";
					StringBuffer sqlPB = new StringBuffer(
							"select a.empiid as empiid   from MPI_DEMOGRAPHICINFO a where a.empiid=:empiId  ");
					Map<String, Object> sqlMap = new HashMap<String, Object>();
					sqlMap.put("empiId", empiId);
					List<Map<String, Object>> listPB = dao.doSqlQuery(
							sqlPB.toString(), sqlMap);
					// Map<String, Object> ksmcMap_sql = new HashMap<String,
					// Object>();
					if (listPB.size() > 0) {
						empiid_PD = listPB.get(0) + "";
					}
					if ("".equals(empiid_PD) || "null".equals(empiid_PD)
							|| empiid_PD == null) {
						// 连mpi从chis传过来的个人信息 需要在本地保存下MPI
						Map<String, Object> parameters_MpiPd = new HashMap<String, Object>();
						parameters_MpiPd.put("EMPIID", empiId);// EMPI
						parameters_MpiPd.put("CARDNO", body.get("cardNo"));// 卡号
						// parameters_MpiPd.put("MZHM", MZHM);//门诊号码
						// parameters_MpiPd.put("photo", body.get("photo"));//
						// parameters_MpiPd.put("BRXZ", body.get("BRXZ"));//性质
						parameters_MpiPd.put("personName",
								body.get("personName"));// 姓名
						parameters_MpiPd.put("idCard", body.get("idCard"));// 身份证
						// parameters_MpiPd.put("sexCode",
						// body.get("sexCode"));//性别
						// parameters_MpiPd.put("birthday",
						// body.get("birthday"));//出生日期
						// parameters_MpiPd.put("age", body.get("age"));//年龄
						// parameters_MpiPd.put("workPlace",
						// body.get("workPlace"));//工作单位
						// parameters_MpiPd.put("mobileNumber",
						// body.get("mobileNumber"));//本人电话
						// parameters_MpiPd.put("phoneNumber",
						// body.get("phoneNumber"));//家庭电话
						// parameters_MpiPd.put("contact",
						// body.get("contact"));//联系人
						// parameters_MpiPd.put("contactPhone",
						// body.get("contactPhone"));//联系人电话
						// parameters_MpiPd.put("registeredPermanent",
						// body.get("registeredPermanent"));//户籍标志
						// parameters_MpiPd.put("nationCode",
						// body.get("nationCode"));//民族
						// parameters_MpiPd.put("bloodTypeCode",
						// body.get("bloodTypeCode"));//血型
						// parameters_MpiPd.put("rhBloodCode",
						// body.get("rhBloodCode"));//RH血型
						// parameters_MpiPd.put("educationCode",
						// body.get("educationCode"));//文化程度
						// parameters_MpiPd.put("workCode",
						// body.get("workCode"));//职业类别
						// parameters_MpiPd.put("maritalStatusCode",
						// body.get("maritalStatusCode"));//婚姻状况
						// parameters_MpiPd.put("homePlace",
						// body.get("homePlace"));//出生地
						// parameters_MpiPd.put("zipCode",
						// body.get("zipCode"));//邮政编码
						// parameters_MpiPd.put("address",
						// body.get("address"));//联系地址
						// parameters_MpiPd.put("email",
						// body.get("email"));//电子邮件
						// parameters_MpiPd.put("startWorkDate",
						// body.get("startWorkDate"));//参加工作日期
						// // parameters_MpiPd.put("nationalityCode",
						// body.get("nationalityCode"));//国籍
						// parameters_MpiPd.put("createUnit",
						// body.get("createUnit"));//建档人建档机构
						// parameters_MpiPd.put("createUser",
						// body.get("createUser"));//建档人
						// parameters_MpiPd.put("createTime",
						// body.get("createTime"));//建档时间
						// parameters_MpiPd.put("lastModifyUnit",
						// body.get("lastModifyUnit"));//最后修改机构
						// parameters_MpiPd.put("lastModifyTime",body.get("lastModifyTime"));//最后修改时间
						// parameters_MpiPd.put("lastModifyUser",
						// body.get("lastModifyUser"));//最后修改人
						parameters_MpiPd.put("status", 0);// 状态
						Map<String, Object> MpiPd;
						MpiPd = dao.doSave("create",
								BSPHISEntryNames.MPI_DemographicInfo_TJ,
								parameters_MpiPd, false);
					} else {
						Map<String, Object> parameters = new HashMap<String, Object>();
						// parameters.put("BIRTHDAY", BIRTHDAY);// 出生日期
						parameters.put("CREATEUNIT", manaUnitId);// 建档机构
						parameters.put("CREATEUSER", UserId);// 建档人
						body.put("JGID", UserId);
						parameters.put("STATUS", 0);// 状态
						parameters.put("empiId", empiId);
						String sqlUpdate = "update MPI_DEMOGRAPHICINFO set CREATEUNIT=:CREATEUNIT,CREATEUSER=:CREATEUSER,STATUS=:STATUS"
								+ " where empiid=:empiId ";
						try {
							dao.doSqlUpdate(sqlUpdate, parameters);
						} catch (PersistentDataOperationException e1) {
							logger.error("Void invoice fails.", e1);
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR,
									"EMPIIP更新失败");
						}
					}
					/**
					 * EMPI个人档案
					 *
					 */

					/**
					 * EMPI个人基本信息保存结束
					 *
					 */
					/**
					 * EMPI卡保存MPI_Card
					 *
					 */
					Map<String, Object> parameters_Card = new HashMap<String, Object>();
					String cardNo = data.get("cardNo") + "";
					String cardId = data.get("cardId") + "";
					String MPI_cal = "";
					String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
							"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
					if (!"".equals(cardNo) && !"null".equals(cardNo)
							&& cardNo != null) {
						StringBuffer sql = new StringBuffer(
								"select a.CARDNO as CARDNO  from MPI_Card a where a.cardTypeCode=:KLX and EMPIID=:EMPIID ");
						Map<String, Object> parameters_sql = new HashMap<String, Object>();
						parameters_sql.put("KLX", KLX);
						parameters_sql.put("EMPIID", empiId);
						List<Map<String, Object>> list_sql = dao.doSqlQuery(
								sql.toString(), parameters_sql);
						// Map<String, Object> ksmcMap_sql = new HashMap<String,
						// Object>();
						if (list_sql.size() > 0) {
							MPI_cal = list_sql.get(0) + "";
						}
					}
					if ("".equals(MPI_cal) || "null".equals(MPI_cal)
							|| MPI_cal == "") {
						/**
						 * EMPI卡保存MPI_Card
						 *
						 */
						String CARDNO = data.get("cardNo") + "";
						parameters_Card.put("cardNo", CARDNO);
						parameters_Card.put("empiId", empiId);
						parameters_Card.put("cardTypeCode", KLX);
						parameters_Card.put("createUnit", manaUnitId);
						parameters_Card.put("createUser", UserId);
						parameters_Card.put("status", "0");
						parameters_Card.put("lastModifyUnit", manaUnitId);
						parameters_Card.put("lastModifyUser", UserId);
						Map<String, Object> Card;
						try {
							Card = dao.doSave("create",
									BSPHISEntryNames.MPI_Card, parameters_Card,
									false);
						} catch (PersistentDataOperationException e1) {
							logger.error("Void invoice fails.", e1);
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR,
									"门诊号码获取卡类型保存失败");
						}
					} else {
						parameters_Card.put("CARDNO", cardNo);
						parameters_Card.put("CARDID", cardId);
						parameters_Card.put("CARDTYPECODE", KLX);
						// parameters_Card.put("createUnit", manaUnitId);
						parameters_Card.put("CREATEUSER", UserId);
						parameters_Card.put("EMPIID", empiId);
						parameters_Card.put("STATUS", "0");
						parameters_Card.put("LASTMODIFYUNIT", manaUnitId);
						parameters_Card.put("LASTMODIFYUSER", UserId);
						// Map<String, Object> Card;
						String sql_Card = "update MPI_Card set CARDNO=:CARDNO,EMPIID=:EMPIID,CARDTYPECODE=:CARDTYPECODE,"
								+ "  CREATEUSER=:CREATEUSER,STATUS=:STATUS,LASTMODIFYUNIT=:LASTMODIFYUNIT,LASTMODIFYUSER=:LASTMODIFYUSER "
								+ " where CARDID=:CARDID ";
						try {
							dao.doSqlUpdate(sql_Card, parameters_Card);
						} catch (PersistentDataOperationException e1) {
							logger.error("Void invoice fails.", e1);
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR, "卡类型更新失败");
						}
					}

					/**
					 * 后台处理挂号功能MZ_GHMX
					 *
					 */
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date toDay = new Date();
					String nowDay = sdf.format(toDay);
					try {
						sdf.set2DigitYearStart(sdf.parse(nowDay + ""));
					} catch (ParseException e) {
						e.printStackTrace();
					}// 挂号日期默认当天
					String KSDM_CS = ParameterUtil.getParameter(manaUnitId,
							"KSDM_TJ", "0", "健康证人员默认的挂号科室系统参数", ctx);
					if (!"".equals(KSDM_CS) && !"null".equals(KSDM_CS)
							&& KSDM_CS != null) {
						// body.put("KSDM",443);//默认科室代码
						body.put("KSDM", KSDM_CS);// 默认科室代码
					} else {
						body.put("KSDM", 443);// 默认科室代码
					}
					DecimalFormat df = new DecimalFormat("#.00");
					Map<String, Object> parametersGh = new HashMap<String, Object>();
					Map<String, Object> MS_GHMX = new HashMap<String, Object>();
					double ZJJE = Double.parseDouble(0 + "");// ZJJE支付金额 默认为0
					double XJJE = Double.parseDouble(0 + "");// 金额 默认为0
					double ZFJE = Double.parseDouble(0 + "");// 金额 默认为0
					double QTYS = Double.parseDouble(df.format(ZJJE - ZFJE));
					double HBWC = Double.parseDouble(df.format(ZFJE - XJJE));
					String MZHMGh = body.get("MZHM") + "";// 门诊号码
					long czpbl = 0;
					if (!"".equals(MZHMGh) && !"null".equals(MZHMGh)
							&& MZHMGh != null) {
						// 查看BRDA是否有人员信息
						StringBuffer sql = new StringBuffer(
								"select a.BRID as BRID  from MS_BRDA a where a.MZHM=:MZHMGh and a.EMPIID=:EMPIID ");
						Map<String, Object> parameters_sql = new HashMap<String, Object>();
						parameters_sql.put("MZHMGh", MZHMGh);
						parameters_sql.put("EMPIID", empiId);
						List<Map<String, Object>> list_sql = null;
						list_sql = dao.doSqlQuery(sql.toString(),
								parameters_sql);
						Map<String, Object> ksmcMap_sql = new HashMap<String, Object>();
						MS_GHMX.put("CZPB", 1);
						if (list_sql.size() > 0) {
							ksmcMap_sql = list_sql.get(0);
							String BRID = ksmcMap_sql.get("BRID") + "";
							body.put("BRID", BRID + "");// 病人id
							parametersGh.put("BRID", Long.parseLong(BRID + ""));
							parametersGh.put("JGID", manaUnitId);
							czpbl = dao.doCount("MS_GHMX",
									"BRID=:BRID and JGID=:JGID", parametersGh);
							if (czpbl > 0) {
								MS_GHMX.put("CZPB", 0);
							} else {
								MS_GHMX.put("CZPB", 1);
							}
							// 执行update BRDA
							// 新建档案
							Map<String, Object> MS_BRDA = new HashMap<String, Object>();
							Map<String, Object> brdaMAP = null;
							MS_BRDA.put("BRID", Long.parseLong(BRID + ""));// empiId
							MS_BRDA.put("EMPIID", empiId);// empiId
							MS_BRDA.put("MZHM", MZHM);// MZHM
							MS_BRDA.put("BRXM", body.get("personName"));
							MS_BRDA.put("FYZH", body.get("cardNo"));// 医疗证号
							MS_BRDA.put("SFZH", body.get("idCard"));// 身份证号
							MS_BRDA.put("BRXZ", body.get("BRXZ"));// 身份证号
							MS_BRDA.put("BRXB", body.get("sexCode"));// 性别
							MS_BRDA.put("CSNY", body.get("birthday"));// 出生日期
							MS_BRDA.put("HYZK", "");// 婚姻状况
							MS_BRDA.put("JDJG", manaUnitId);// 建档机构
							MS_BRDA.put("JDR", UserId);// 建档人
							MS_BRDA.put("XGSJ", toDay);// 修改时间
							MS_BRDA.put("DWMC", body.get("workPlace"));// 工作单位
							MS_BRDA.put("ZXBZ", 0);// 注销标志
							brdaMAP = dao.doSave("update",
									BSPHISEntryNames.MS_BRDA, MS_BRDA, false);
						} else {
							StringBuffer sql2 = new StringBuffer(
									"select a.BRID as BRID  from MS_BRDA a where a.EMPIID=:EMPIID ");
							Map<String, Object> parameters_sql2 = new HashMap<String, Object>();
							parameters_sql2.put("EMPIID", empiId);
							List<Map<String, Object>> list_sql2 = null;
							list_sql2 = dao.doSqlQuery(sql2.toString(),
									parameters_sql2);
							Map<String, Object> ksmcMap_sql2 = new HashMap<String, Object>();
							if (list_sql2.size() > 0) {
								ksmcMap_sql2 = list_sql2.get(0);
								String BRID2 = ksmcMap_sql2.get("BRID") + "";
								body.put("BRID", BRID2 + "");// 病人id
								parametersGh.put("BRID",
										Long.parseLong(BRID2 + ""));
								parametersGh.put("JGID", manaUnitId);
								czpbl = dao.doCount("MS_GHMX",
										"BRID=:BRID and JGID=:JGID",
										parametersGh);
								if (czpbl > 0) {
									MS_GHMX.put("CZPB", 0);
								} else {
									MS_GHMX.put("CZPB", 1);
								}
							}
						}
						if ("".equals(body.get("BRID"))
								|| "null".equals(body.get("BRID"))
								|| body.get("BRID") == null) {
							// 新建档案
							Map<String, Object> MS_BRDA = new HashMap<String, Object>();
							Map<String, Object> brdaMAP = null;
							MS_BRDA.put("EMPIID", empiId);// empiId
							MS_BRDA.put("MZHM", MZHM);// MZHM
							MS_BRDA.put("BRXM", body.get("personName"));
							MS_BRDA.put("FYZH", body.get("cardNo"));// 医疗证号
							MS_BRDA.put("SFZH", body.get("idCard"));// 身份证号
							MS_BRDA.put("BRXZ", body.get("BRXZ"));// 身份证号
							MS_BRDA.put("BRXB", body.get("sexCode"));// 性别
							MS_BRDA.put("CSNY", body.get("birthday"));// 出生日期
							MS_BRDA.put("HYZK", "");// 婚姻状况
							MS_BRDA.put("JDJG", manaUnitId);// 建档机构
							MS_BRDA.put("JDR", UserId);// 建档人
							MS_BRDA.put("XGSJ", toDay);// 修改时间
							MS_BRDA.put("JDSJ", toDay);// 建档时间
							MS_BRDA.put("ZXBZ", 0);// 注销标志
							brdaMAP = dao.doSave("create",
									BSPHISEntryNames.MS_BRDA, MS_BRDA, false);
							String BRID = brdaMAP.get("BRID") + "";
							body.put("BRID", BRID + "");// 病人id
						}
						if (!"".equals(body.get("BRID"))
								&& !"null".equals(body.get("BRID"))
								&& body.get("BRID") != null) {
							// 查询挂号是否存在
							String jgid = user.getManageUnitId();
							int adt_begin = 1;
							String adt_beginStr = ParameterUtil.getParameter(
									jgid, BSPHISSystemArgument.GHXQ, ctx);
							if (adt_beginStr != "") {
								adt_begin = Integer.parseInt(adt_beginStr);
							}
							SimpleDateFormat matter = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String XQJSFS = ParameterUtil.getParameter(jgid,
									BSPHISSystemArgument.XQJSFS, ctx);// 效期计算方式
							Date adt_begin_data = DateUtils.addDays(new Date(),
									-adt_begin);
							if ("1".equals(XQJSFS)) {
								try {
									adt_begin_data = DateUtils.addDays(
											matter.parse(BSHISUtil.getDate()
													+ " 23:59:59"), -1);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							Map<String, Object> parameters = new HashMap<String, Object>();
							parameters.put("al_jgid", jgid);
							parameters.put("adt_begin", adt_begin_data);
							StringBuffer hql = new StringBuffer(
									"SELECT a.SBXH as SBXH,b.BRXM as BRXM,c.cardNo as JZKH,b.MZHM as MZHM,b.BRXZ as BRXZ,a.JZZT as JZZT,a.KSDM as KSDM FROM MS_GHMX a,");
							hql.append(" MS_BRDA b left outer join MPI_Card");
							hql.append(" c on b.EMPIID = c.empiId and c.cardTypeCode="+KLX+" WHERE ( b.BRID = a.BRID )  AND ( a.GHSJ >= :adt_begin) AND ( a.THBZ = 0 ) "
									+ " AND ( a.JGID = :al_jgid) AND b.BRID = :BRID order by a.GHSJ desc");

							List<Map<String, Object>> list_ret = new ArrayList<Map<String, Object>>();
							parameters.put("BRID", body.get("BRID"));
							list_ret = dao.doSqlQuery(hql.toString(),
									parameters);
							Map<String, Object> map_ret = new HashMap<String, Object>();
							if (list_ret.size() > 0) {
								map_ret = list_ret.get(0);
							}
							if (list_ret.size() > 0
									&& !"".equals(map_ret.get("SBXH"))
									&& !"null".equals(map_ret.get("SBXH"))
									&& map_ret.get("SBXH") != null) {
								// 已有挂号update
								MS_GHMX.put("SBXH", Long.parseLong(map_ret
										.get("SBXH") + ""));// 病人id
								MS_GHMX.put("GHCS", 1);
								MS_GHMX.put("ZPJE", 0d);
								MS_GHMX.put("ZHJE", 0d);
								// MS_GHMX.put("ZFJE", 0);
								MS_GHMX.put("HBWC", HBWC);
								MS_GHMX.put("QTYS", 0d);
								MS_GHMX.put("THBZ", 0);

								MS_GHMX.put("MZLB",
										BSPHISUtil.getMZLB(manaUnitId, dao));
								MS_GHMX.put("YSPB", 0);
								MS_GHMX.put("SFFS", 0);
								MS_GHMX.put("JZZT", 0);
								MS_GHMX.put("JZJS", 0);
								MS_GHMX.put("CZGH", UserId);
								MS_GHMX.put("BRXZ", body.get("BRXZ"));
								MS_GHMX.put("XJJE", XJJE);
								MS_GHMX.put("QTYS", QTYS);
								MS_GHMX.put("BRID", body.get("BRID"));
								MS_GHMX.put("JGID", manaUnitId);
								MS_GHMX.put("GHSJ", new Date());
								MS_GHMX.put("GHLB", 1);
								MS_GHMX.put("KSDM",
										Long.parseLong(body.get("KSDM") + ""));// 挂号科室，先默认为内科
								MS_GHMX.put("GHJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								MS_GHMX.put("ZLJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								MS_GHMX.put("BLJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								String ZJFY = 0 + "";
								if ("null".equals(ZJFY)) {
									ZJFY = "0";
								}
								MS_GHMX.put("ZJFY", Double.parseDouble(df
										.format(Double.parseDouble(ZJFY))));
								MS_GHMX.put("JZXH", 1);// 就诊序号默认为1
								MS_GHMX.put("JKZSB", 1);// 健康证人员默认为1
								Map<String, Object> cf02 = null;
								try {
									cf02 = dao.doSave("update",
											BSPHISEntryNames.MS_GHMX_TJ,
											MS_GHMX, false);
								} catch (PersistentDataOperationException e) {
									logger.error("Void invoice fails.", e);
									throw new ModelDataOperationException(
											ServiceCode.CODE_DATABASE_ERROR,
											"挂号保存失败");
								}

							} else {
								parametersGh.put("BRID",
										Long.parseLong(body.get("BRID") + ""));// 病人id
								parametersGh.put("KSDM",
										Long.parseLong(body.get("KSDM") + ""));// 挂号科室，先默认为内科
								MS_GHMX.put("GHCS", 1);
								MS_GHMX.put("ZPJE", 0d);
								MS_GHMX.put("ZHJE", 0d);
								// MS_GHMX.put("ZFJE", 0);
								MS_GHMX.put("HBWC", HBWC);
								MS_GHMX.put("QTYS", 0d);
								MS_GHMX.put("THBZ", 0);

								MS_GHMX.put("MZLB",
										BSPHISUtil.getMZLB(manaUnitId, dao));
								MS_GHMX.put("YSPB", 0);
								MS_GHMX.put("SFFS", 0);
								MS_GHMX.put("JZZT", 0);
								MS_GHMX.put("JZJS", 0);
								MS_GHMX.put("CZGH", UserId);
								MS_GHMX.put("BRXZ", body.get("BRXZ"));
								String jzhm = BSPHISUtil
										.getNotesNumberNotIncrement(UserId,
												manaUnitId, 1, dao, ctx);
								MS_GHMX.put("JZHM", jzhm);
								MS_GHMX.put("XJJE", XJJE);
								MS_GHMX.put("QTYS", QTYS);
								MS_GHMX.put("BRID", body.get("BRID"));
								MS_GHMX.put("JGID", manaUnitId);
								MS_GHMX.put("GHSJ", new Date());
								MS_GHMX.put("GHLB", 1);
								MS_GHMX.put("KSDM",
										Long.parseLong(body.get("KSDM") + ""));// 挂号科室，先默认为内科
								MS_GHMX.put("GHJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								MS_GHMX.put("ZLJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								MS_GHMX.put("BLJE", Double.parseDouble(df
										.format(Double.parseDouble(0 + ""))));// 先默认为0
								String ZJFY = 0 + "";
								if ("null".equals(ZJFY)) {
									ZJFY = "0";
								}
								MS_GHMX.put("ZJFY", Double.parseDouble(df
										.format(Double.parseDouble(ZJFY))));
								MS_GHMX.put("JZXH", 1);// 就诊序号默认为1
								MS_GHMX.put("JKZSB", 1);// 健康证人员默认为1
								Map<String, Object> cf02 = null;
								try {
									cf02 = dao.doSave("create",
											BSPHISEntryNames.MS_GHMX_TJ,
											MS_GHMX, false);
								} catch (PersistentDataOperationException e) {
									logger.error("Void invoice fails.", e);
									throw new ModelDataOperationException(
											ServiceCode.CODE_DATABASE_ERROR,
											"挂号保存失败");
								}
								// 更新就诊号码
								BSPHISUtil.getNotesNumber(UserId, manaUnitId,
										1, dao, ctx);
								String sbxh = cf02.get("SBXH") + "";
								if (!"".equals(sbxh) && !"null".equals(sbxh)) {
									Map<String, Object> parameters2 = new HashMap<String, Object>();
									parameters2.put("JKZSB", 1);
									parameters2.put("SBXH", sbxh);
									String sqlUpdate2 = "update MS_GHMX set JKZSB=:JKZSB "
											+ " where SBXH=:SBXH ";
									try {
										dao.doSqlUpdate(sqlUpdate2, parameters2);
									} catch (PersistentDataOperationException e) {
										logger.error("Void invoice fails.", e);
										throw new ModelDataOperationException(
												ServiceCode.CODE_DATABASE_ERROR,
												"更新就诊号码失败");
									}
								}

								/**
								 * 后台处理挂号功能MZ_GHMX
								 *
								 */
								// parametersGh.remove("BRID");
							}

						} else {
							res.put("HTCS", "1");
							res.put("CSMC", "无法获档案BRID！");
						}
					}
				}
			}

		} catch (ValidateException e) {
			logger.error("Void invoice fails.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	//以下为家医退费的方法
	private void CheckNoServerDone(List<Map<String, Object>> jyqy_res) throws ModelDataOperationException {
		String sbxh = "";
		for (Map<String, Object> jyqy : jyqy_res) {
			sbxh = sbxh + "," + jyqy.get("SBXH");
		}
		StringBuffer hql = new StringBuffer();
		try {
			if (S.isNotBlank(sbxh)) {
				hql.append("select SCID as SCID from SCM_INCREASESERVER where SBXH in (:sbxh)");
				Map<String, Object> parm = new HashMap<String, Object>();
				parm.put("sbxh", sbxh.substring(1, sbxh.length()));
				List<Map<String, Object>> increaseRecords = null;

				increaseRecords = dao.doSqlQuery(hql.toString(), parm);

				int count = 0;
				//判断是否被使用
				for (Map<String, Object> record : increaseRecords
				) {
					String scid =  record.get("SCID").toString();
					StringBuffer hql_c = new StringBuffer();
					StringBuffer hql_p = new StringBuffer();

					hql_c.append("select count(1) as COUNT from SCM_SERVICECONTRACTPLANTASK where SCID =:scid and status='1'");
					hql_p.append("select count(1) as COUNT from SCM_INCREASESERVER a , SCM_INCREASEITEMS b where a.SCINID = b.SCINID and  b.SERVICETIMES <> 0 and a.SCID =:scid and b.LOGOFF <> '3'");

					Map<String, Object> parms = new HashMap<String, Object>();
					parms.put("scid" , scid);
					Map<String, Object> count1 = dao.doSqlLoad(hql_c.toString(), parms);
					Map<String, Object> count2 = dao.doSqlLoad(hql_p.toString(), parms);
					count = Integer.parseInt(count1.get("COUNT").toString()) + Integer.parseInt(count2.get("COUNT").toString());

					if (count == 0) {
						//todo 解约 phis , chis
						//phis 部分
						StringBuffer hql_pIn = new StringBuffer();
						hql_pIn.append("select SCINID as SCINID from SCM_INCREASESERVER where SCID =:scid");
						Map<String , Object> parm_pIn = new HashMap<String, Object>();
						parm_pIn.put("scid", scid);
						Map<String , Object> pIn = dao.doSqlLoad(hql_pIn.toString(),parm_pIn);

						StringBuffer hql_pLogoff = new StringBuffer();
						hql_pLogoff.append("update  SCM_INCREASEITEMS a set a.LOGOFF = '3' where a.SCINID = :scinid");
						Map<String, Object> parm_p = new HashMap<String, Object>();
						parm_p.put("scinid", pIn.get("SCINID"));
						dao.doUpdate(hql_pLogoff.toString(), parm_p);
						//chis 部分
						ServiceAdapter.invoke("chis.gpService","doStopSignStatus" , scid);
					} else {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "已有项目被使用，不允许解约");
					}
				}

			}
		} catch (Exception e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "解约失败！");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryCardNo(Map<String, Object> req,
							  Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String MZHM=req.get("MZHM")+"";
		String KLX = ParameterUtil.getParameter(ParameterUtil.getTopUnitId(),
				"KLX", "04", "01健康卡;02市民卡;03社保卡;04就诊卡。默认04", ctx);
		String sql="select b.cardNo as cardNo,b.status as status from MS_BRDA a,MPI_Card b where a.EMPIID=b.empiId and a.MZHM=:MZHM and b.cardTypeCode="+KLX;
		Map<String, Object> parameters=new HashMap<String, Object>();
		parameters.put("MZHM", MZHM);
		try {
			List<Map<String, Object>> listCard=dao.doSqlQuery(sql, parameters);
			if(listCard!=null&&listCard.size()>0){
				res.putAll(listCard.get(0));
			}
		} catch (PersistentDataOperationException e) {
			logger.error("查询卡号失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询卡号失败");
		}

	}
	protected URL getwebserviceurl(String jgid){
		URL url=null;
		try {
			url=new URL(DictionaryController.instance().get("phis.dictionary.HcnWebservice").getText(jgid.substring(0,9)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	@SuppressWarnings("unchecked")
	public void doUpdateHMS(Map<String, Object> req,Map<String, Object> res, Context ctx)throws ModelDataOperationException, ServiceException {
		String data = req.get("ids")+"";
		String status = req.get("status")+"";
		String[] ids=data.split(",");
		for(String applyId : ids){
			String requestParams =  "";
			//获取接口url	
			String url=DictionaryController.instance().getDic("chis.dictionary.httppost_drapply").getText("url");
			if("1".equals(status)){//已提交
				requestParams = "{\"serviceId\":\"hms.inspectReceipt\",\"method\":\"updateInspectStatus\",\"body\":[{\"applyId\":"+"\""+applyId+"\""+",\"status\":\"1\"}]}";
			}else if("2".equals(status)){//已收费
				requestParams = "{\"serviceId\":\"hms.inspectReceipt\",\"method\":\"updateInspectStatus\",\"body\":[{\"applyId\":"+"\""+applyId+"\""+",\"status\":\"2\"}]}";
			}else if("3".equals(status)){//已执行
				requestParams = "{\"serviceId\":\"hms.inspectReceipt\",\"method\":\"updateInspectStatus\",\"body\":[{\"applyId\":"+"\""+applyId+"\""+",\"status\":\"3\"}]}";
			}else if("4".equals(status)){//已作废
				requestParams = "{\"serviceId\":\"hms.inspectReceipt\",\"method\":\"updateInspectStatus\",\"body\":[{\"applyId\":"+"\""+applyId+"\""+",\"status\":\"4\"}]}";
			}
			//调用接口获取访问权限
			try {
				String accessToken = HttpclientUtil.sendHttpPost_JSON(url, requestParams);
			} catch (IOException e) {
				logger.error("诊疗平台付费状态修改失败！检查单号："+applyId,e);
				throw new ServiceException("请联系管理员,诊疗平台付费状态修改失败！检查单号："+applyId,e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> doQueryHMS(Map<String, Object> req,Map<String, Object> res, Context ctx)throws ModelDataOperationException, ServiceException, JSONException {
		String data = req.get("ids")+"";
		String[] ids=data.split(",");
		for(String applyId : ids){
			String requestParams =  "";
			//获取接口url	
			String url=DictionaryController.instance().getDic("chis.dictionary.httppost_drapply").getText("url");
			requestParams = "{\"serviceId\":\"hms.inspectReceipt\",\"method\":\"findInspectReceipt\",\"body\":[{\"applyId\":"+"\""+applyId+"\""+"}]}";
			//调用接口获取访问权限
			try {
				String accessToken = HttpclientUtil.sendHttpPost_JSON(url, requestParams);
				logger.info(accessToken);
				JSONObject jorm = new JSONObject(accessToken);
				if (jorm.optString("code").equals("200")) {
					res.put("code","200");
					res.put("status",jorm.optJSONArray("data").getJSONObject(0).optString("status")+"");
				}else{
					res.put("code",jorm.optString("code")+"");
					res.put("msg",jorm.optString("msg")+"");
					logger.info(res.toString());
					throw new ServiceException(res.toString());
				}

			} catch (IOException e) {
				logger.error("诊疗平台付费状态查询失败！检查单号："+applyId,e);
				throw new ServiceException("请联系管理员,诊疗平台查询状态失败！检查单号："+applyId,e);
			}
		}
		logger.info(res.toString());
		return res;
	}

	public void doChangeSCMStatus(Map<String, Object> req, Map<String, Object> res, Context ctx){
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		if(body.get("FPHM") != null){
			String fphm = body.get("FPHM").toString();
			String signFlag = body.get("SIGNFLAG").toString();
			String sql = "";
			if(signFlag.equals("2")){
				sql = "update SCM_SIGNCONTRACTRECORD set signFlag = "+signFlag+",stopdate=sysdate,stopreason='06',fphm='"+fphm+"ZF', payOrNot='2' where fphm = '"+fphm+"'";
			}else{
				sql = "update SCM_SIGNCONTRACTRECORD set signFlag = "+signFlag+",stopdate=null,stopreason=null,fphm='"+fphm+"', payOrNot='1' where fphm = '"+fphm+"ZF'";
			}
			try {
				dao.doSqlUpdate(sql, null);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		body = null;
	}
}
