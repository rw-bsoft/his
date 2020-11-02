package phis.application.fsb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedGenerateVerificationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public FamilySickBedGenerateVerificationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 产生数据前的验证
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGenerateVerification(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date id_dqrq = null;
		Date idt_hzrq = null;
		Date ldt_CurrentDateTime = null;
		if (req.get("id_dqrq") != null) {
			try {
				id_dqrq = sdfdate.parse(req.get("id_dqrq") + "");
				ldt_CurrentDateTime = sdfdate.parse(sdfdate.format(new Date()));
				if (id_dqrq.getTime() > ldt_CurrentDateTime.getTime()) {
					res.put(Service.RES_CODE, 604);
					return;
				}
				if (id_dqrq.getTime() == ldt_CurrentDateTime.getTime()) {
					idt_hzrq = sdfdatetime
							.parse(sdfdatetime.format(new Date()));
				} else {
					idt_hzrq = sdfdatetime.parse(sdfdate.format(id_dqrq)
							+ " 23:59:59");
				}
				Map<String, Object> idt_LastDate = new HashMap<String, Object>();
				BSPHISUtil.fsb_wf_IsGather(idt_hzrq, idt_LastDate, dao, ctx);
				if (idt_LastDate.get("idt_LastDate") == null) {
					res.put(Service.RES_CODE, 600);
					res.put("idt_hzrq", sdfdate.format(idt_hzrq));
				} else {
					int days = BSHISUtil.getDifferDays(idt_hzrq, sdfdatetime
							.parse(sdfdatetime.format(idt_LastDate
									.get("idt_LastDate"))));
					if (days < 0) {
						res.put(Service.RES_CODE, 601);
						res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						res.put("idt_LastDate", sdfdate.format(idt_LastDate
								.get("idt_LastDate")));
					} else if (days == 0) {
						res.put(Service.RES_CODE, 602);
						res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						res.put("idt_LastDate", sdfdate.format(idt_LastDate
								.get("idt_LastDate")));
					} else if (days > 1) {
						res.put(Service.RES_CODE, 603);
						res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						res.put("idt_LastDate", sdfdate.format(idt_LastDate
								.get("idt_LastDate")));
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 产生数据后的验证
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGenerateAfterVerification(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date id_dqrq = null;
		Date idt_hzrq = null;
		Date ldt_CurrentDateTime = null;
		if (req.get("id_dqrq") != null) {
			try {
				id_dqrq = sdfdate.parse(req.get("id_dqrq") + "");
				ldt_CurrentDateTime = sdfdate.parse(sdfdate.format(new Date()));
				if (id_dqrq.getTime() == ldt_CurrentDateTime.getTime()) {
					idt_hzrq = sdfdatetime
							.parse(sdfdatetime.format(new Date()));
				} else {
					idt_hzrq = sdfdatetime.parse(sdfdate.format(id_dqrq)
							+ " 23:59:59");
				}
				Map<String, Object> parametershzbd = new HashMap<String, Object>();
				parametershzbd.put("adt_jzrq",
						sdfdatetime.parse(sdfdatetime.format(idt_hzrq)));
				parametershzbd.put("al_jgid", manaUnitId);
				// 日结汇总汇总表单
				Long l = dao.doCount("JC_JZXX",
						"HZRQ IS NULL AND JZRQ<=:adt_jzrq AND JGID=:al_jgid",
						parametershzbd);
				if (l == 0) {
					res.put(Service.RES_CODE, 605);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查询前的验证
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryVerification(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Date adt_hzrq_start = null;
		Date adt_hzrq_end = null;
		try {
			if (req.get("kssj") != null) {
				adt_hzrq_start = sdfdate.parse(req.get("kssj") + "");
			}
			if (req.get("jssj") != null) {
				adt_hzrq_end = sdfdate.parse(req.get("jssj") + "");
			}
			Date ldt_CurrentDateTime = sdfdate
					.parse(sdfdate.format(new Date()));
			if (req.get("kssj") != null) {
				Date kssj = sdfdate.parse(req.get("kssj") + "");
				if (kssj.getTime() > ldt_CurrentDateTime.getTime()) {
					res.put(Service.RES_CODE, 800);
					return;
				}
			}
			if (req.get("jssj") != null) {
				Date jssj = sdfdate.parse(req.get("jssj") + "");
				if (jssj.getTime() > ldt_CurrentDateTime.getTime()) {
					res.put(Service.RES_CODE, 801);
					return;
				}
			}
			Map<String, Object> parametershzbd = new HashMap<String, Object>();
			parametershzbd.put("adt_hzrq_b", adt_hzrq_start);
			parametershzbd.put("adt_hzrq_e", adt_hzrq_end);
			parametershzbd.put("al_jgid", manaUnitId);
			// 判断有没有数据可查询
			List<Map<String, Object>> resultList = dao
					.doQuery(
							"SELECT JC_JZXX.CZGH as CZGH,sum(JC_JZXX.CYSR) as CYSR,sum(JC_JZXX.YJJE) as YJJE,sum(JC_JZXX.TPJE) as TPJE,sum(JC_JZXX.FPZS) as FPZS,sum(JC_JZXX.SJZS) as SJZS,sum(JC_JZXX.YSJE) as YSJE,sum(JC_JZXX.YSXJ) as YSXJ,sum(JC_JZXX.ZPZS) as ZPZS,sum(JC_JZXX.TYJJ) as TYJJ,sum(JC_JZXX.YSQT) as YSQT,sum(JC_JZXX.QTZS) as QTZS,sum(JC_JZXX.SRJE) as SRJE FROM JC_JZXX JC_JZXX WHERE JC_JZXX.HZRQ>=:adt_hzrq_b AND JC_JZXX.HZRQ<=:adt_hzrq_e AND JC_JZXX.JGID=:al_jgid group by JC_JZXX.CZGH",
							parametershzbd);
			if (resultList.size() <= 0) {
				res.put(Service.RES_CODE, 802);
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 汇总前的验证
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doCollectVerification(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date idt_hzrq = null;
		Date id_dqrq = null;
		Date ldt_CurrentDateTime = null;
		if (req.get("id_dqrq") != null) {
			try {
				id_dqrq = sdfdate.parse(req.get("id_dqrq") + "");
				ldt_CurrentDateTime = sdfdate.parse(sdfdate.format(new Date()));
				if (id_dqrq.getTime() == ldt_CurrentDateTime.getTime()) {
					idt_hzrq = sdfdatetime
							.parse(sdfdatetime.format(new Date()));
				} else {
					idt_hzrq = sdfdatetime.parse(sdfdate.format(id_dqrq)
							+ " 23:59:59");
				}
				Map<String, Object> idt_LastDate = new HashMap<String, Object>();
				int hzsign = BSPHISUtil.fsb_wf_IsGather(idt_hzrq, idt_LastDate,
						dao, ctx);
				if (hzsign == 1) {
					res.put(Service.RES_CODE, 700);
					return;
				}
				if (idt_LastDate.get("idt_LastDate") == null) {
					res.put(Service.RES_CODE, 701);
					res.put("idt_hzrq", sdfdate.format(idt_hzrq));
				} else {
					int days = BSHISUtil.getDifferDays(idt_hzrq, sdfdatetime
							.parse(sdfdatetime.format(idt_LastDate
									.get("idt_LastDate"))));
					if (days < 0) {
						res.put(Service.RES_CODE, 702);
						res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						res.put("idt_LastDate", sdfdate.format(idt_LastDate
								.get("idt_LastDate")));
					} else if (days == 0 || days == 1) {
						if (sdfdate.parse(sdfdate.format(idt_hzrq)).getTime() == sdfdate
								.parse(sdfdate.format(new Date())).getTime()) {
							res.put(Service.RES_CODE, 703);
						} else {
							res.put(Service.RES_CODE, 704);
							res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						}
					} else if (days > 1) {
						res.put(Service.RES_CODE, 705);
						res.put("idt_hzrq", sdfdate.format(idt_hzrq));
						res.put("idt_LastDate", sdfdate.format(idt_LastDate
								.get("idt_LastDate")));
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void doSaveCollect(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfdatetime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Date ldt_End = null;
		Date ldt_GatherDate = null;
		Date id_dqrq = null;
		Date idt_hzrq = null;
		Date ldt_CurrentDateTime = null;
		Map<String, Object> parametersll_count = new HashMap<String, Object>();
		Map<String, Object> parametersupd = new HashMap<String, Object>();
		try {
			if (req.get("summaryDate") != null) {
				ldt_CurrentDateTime = sdfdate.parse(sdfdate.format(new Date()));
				id_dqrq = sdfdate.parse(req.get("summaryDate") + "");
				if (id_dqrq.getTime() == ldt_CurrentDateTime.getTime()) {
					idt_hzrq = sdfdatetime
							.parse(sdfdatetime.format(new Date()));
				} else {
					idt_hzrq = sdfdatetime.parse(sdfdate.format(id_dqrq)
							+ " 23:59:59");
				}
			}
			if (idt_hzrq == new Date()) {
				ldt_End = idt_hzrq;
			} else {
				ldt_End = sdfdatetime.parse((sdfdate.format(BSHISUtil
						.getDateAfter(idt_hzrq, 1)) + " 00:00:00"));
			}
			ldt_GatherDate = sdfdatetime.parse(sdfdate.format(idt_hzrq)
					+ " 00:00:00");
			parametersll_count.put("idt_End", ldt_End);
			parametersll_count.put("gl_jgid", manaUnitId);
			parametersupd.put("ldt_GatherDate", ldt_GatherDate);
			parametersupd.put("idt_End", ldt_End);
			parametersupd.put("gl_jgid", manaUnitId);
			Long ll_count = 0L;
			ll_count = dao.doCount("JC_FYMX",
					"HZRQ IS Null AND JFRQ<:idt_End and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_FYMX Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JFRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
				dao.doUpdate(
						"UPDATE JC_FYMX_JS Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JFRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			ll_count = dao.doCount("JC_TBKK",
					"HZRQ IS Null AND JZRQ<:idt_End and JGID =:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_TBKK Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			ll_count = dao.doCount("JC_JKZF",
					"HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_JKZF Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			ll_count = dao.doCount("JC_JCJS",
					"HZRQ IS Null AND JZRQ<:idt_End  and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_JCJS Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			ll_count = dao.doCount("JC_JSZF",
					"HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_JSZF Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			ll_count = dao.doCount("JC_JZXX",
					"HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_JZXX Set HZRQ=:ldt_GatherDate Where HZRQ IS Null AND JZRQ<:idt_End and JGID=:gl_jgid",
						parametersupd);
			}
			BSPHISUtil.fsb_wf_Create_jzhz(idt_hzrq, dao, ctx);
			BSPHISUtil.fsb_wf_create_fyhz(idt_hzrq, dao, ctx);
			// 将表ZY_BRRY打上汇总日期、结帐日期
			ll_count = dao
					.doCount(
							"JC_BRRY JC_BRRY",
							"JC_BRRY.HZRQ IS NULL AND CYPB=8 AND JC_BRRY.CYRQ<:idt_End AND JC_BRRY.JGID=:gl_jgid and JC_BRRY.ZYH NOT IN (SELECT JC_JCJS.ZYH FROM JC_JCJS JC_JCJS WHERE (JC_JCJS.ZFPB=0) AND JC_JCJS.JGID=:gl_jgid and (JC_JCJS.JZRQ IS NULL OR JC_JCJS.JZRQ>=:idt_End))",
							parametersll_count);
			if (ll_count > 0) {
				dao.doUpdate(
						"UPDATE JC_BRRY JC_BRRY SET JC_BRRY.HZRQ=:ldt_GatherDate WHERE JC_BRRY.HZRQ IS NULL AND JC_BRRY.CYPB=8 AND JC_BRRY.CYRQ<:idt_End AND JC_BRRY.JGID=:gl_jgid and JC_BRRY.ZYH NOT IN (SELECT JC_JCJS.ZYH FROM JC_JCJS JC_JCJS WHERE (JC_JCJS.ZFPB=0) AND JC_JCJS.JGID=:gl_jgid and (JC_JCJS.JZRQ IS NULL OR JC_JCJS.JZRQ>=:idt_End))",
						parametersupd);
			}
			// 将费用明细JC_FYMX汇总到JC_SRHZ中:分别按费用科室(kslb=1)、执行科室(kslb=2)归并
			parametersll_count.clear();
			parametersll_count.put("ldt_GatherDate", ldt_GatherDate);
			parametersll_count.put("gl_jgid", manaUnitId);
			ll_count = dao.doCount("JC_FYMX",
					"HZRQ=:ldt_GatherDate and JGID=:gl_jgid",
					parametersll_count);
			if (ll_count > 0) {
//				List<Map<String, Object>> zy_srhz_list = dao
//						.doQuery(
//								"SELECT JC_FYMX.HZRQ as HZRQ,1 as KSLB,JC_FYMX.FYKS as FYKS,JC_FYMX.FYXM as FYXM,sum(JC_FYMX.ZJJE) as ZJJE,sum(JC_FYMX.ZFJE) as ZFJE,JC_FYMX.JGID as JGID FROM JC_FYMX JC_FYMX WHERE JC_FYMX.HZRQ=:ldt_GatherDate and JC_FYMX.JGID=:gl_jgid GROUP BY JC_FYMX.HZRQ,JC_FYMX.FYKS,JC_FYMX.FYXM,JC_FYMX.JGID",
//								parametersll_count);
//				for (int i = 0; i < zy_srhz_list.size(); i++) {
//					if (Double
//							.parseDouble(zy_srhz_list.get(i).get("ZJJE") + "")
//							+ Double.parseDouble(zy_srhz_list.get(i)
//									.get("ZFJE") + "") == 0.0) {
//						zy_srhz_list.remove(i);
//						i--;
//						continue;
//					}
//					Map<String, Object> savmap = new HashMap<String, Object>();
//					savmap.put(
//							"HZRQ",
//							sdfdatetime.parse(zy_srhz_list.get(i).get("HZRQ")
//									+ ""));
//					savmap.put("KSLB", zy_srhz_list.get(i).get("KSLB"));
//					savmap.put("KSDM", zy_srhz_list.get(i).get("FYKS"));
//					savmap.put("SFXM", zy_srhz_list.get(i).get("FYXM"));
//					savmap.put("ZJJE", zy_srhz_list.get(i).get("ZJJE"));
//					savmap.put("ZFJE", zy_srhz_list.get(i).get("ZFJE"));
//					savmap.put("JGID", zy_srhz_list.get(i).get("JGID"));
//					dao.doSave("create", BSPHISEntryNames.JC_SRHZ, savmap, false);
//				}
//				zy_srhz_list.clear();
				List<Map<String, Object>> zy_srhz_list = dao
						.doQuery(
								"SELECT JC_FYMX.HZRQ as HZRQ,JC_FYMX.FYXM as FYXM,sum(JC_FYMX.ZJJE) as ZJJE,sum(JC_FYMX.ZFJE) as ZFJE,JC_FYMX.JGID as JGID FROM JC_FYMX JC_FYMX WHERE JC_FYMX.HZRQ=:ldt_GatherDate and JC_FYMX.JGID=:gl_jgid GROUP BY JC_FYMX.HZRQ,JC_FYMX.FYXM,JC_FYMX.JGID",
								parametersll_count);
				for (int i = 0; i < zy_srhz_list.size(); i++) {
					if (Double
							.parseDouble(zy_srhz_list.get(i).get("ZJJE") + "")
							+ Double.parseDouble(zy_srhz_list.get(i)
									.get("ZFJE") + "") == 0.0) {
						zy_srhz_list.remove(i);
						i--;
						continue;
					}
					Map<String, Object> savmap = new HashMap<String, Object>();
					savmap.put(
							"HZRQ",
							sdfdatetime.parse(zy_srhz_list.get(i).get("HZRQ")
									+ ""));
					savmap.put("SFXM", zy_srhz_list.get(i).get("FYXM"));
					savmap.put("ZJJE", zy_srhz_list.get(i).get("ZJJE"));
					savmap.put("ZFJE", zy_srhz_list.get(i).get("ZFJE"));
					savmap.put("JGID", zy_srhz_list.get(i).get("JGID"));
					dao.doSave("create", BSPHISEntryNames.JC_SRHZ, savmap, false);
				}
			}
			res.put("idt_hzrq", sdfdate.format(idt_hzrq));
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ModelDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "汇总失败,数据库处理异常!", e);
		}
	}
	
	
	//取消汇总查询
		public void doQueryCancelCommit(
				Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
				Context ctx) throws ModelDataOperationException {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId();// 用户的机构名称
			String userId = user.getUserId();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
//			parameters.put("czgh", userId);
			try {
				List<Map<String,Object>> list = dao.doSqlQuery("select to_char(max(HZRQ ),'yyyy-mm-dd') as HZRQ from JC_JZHZ where JGID = :jgid", parameters);
				if(list.size()>0 && list.get(0).get("HZRQ")!=null){
					res.put("HZRQ", list.get(0).get("HZRQ"));
				}else{
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "当前没有汇总信息!");
				}
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "查询汇总信息出错!");
			}
			
		}
		
		//取消汇总查询
			public void doCancelCommit(
					Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
					Context ctx) throws ModelDataOperationException {
				UserRoleToken user = UserRoleToken.getCurrent();
				String jgid = user.getManageUnitId();// 用户的机构名称
				Map<String, Object> parameters = new HashMap<String, Object>();
				String hzrq = req.get("HZRQ")+"";
				SimpleDateFormat sdfdatetime = new SimpleDateFormat(
						"yyyy-MM-dd");
				Date hzdate = null;
				try {
					hzdate = sdfdatetime.parse(hzrq);
				} catch (ParseException e1) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消汇总信息出错!");
				}
				parameters.put("jgid", jgid);
				parameters.put("hzrq", hzdate);
				try {
					dao.doUpdate("update JC_FYMX set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_FYMX_JS set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_TBKK set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_JKZF set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_JCJS set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_JSZF set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_JZXX set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("update JC_BRRY set HZRQ=null where HZRQ =:hzrq and JGID=:jgid", parameters);
					dao.doUpdate("delete from JC_SRHZ where JGID = :jgid and HZRQ=:hzrq", parameters);
					dao.doUpdate("delete from JC_JZHZ where JGID = :jgid and HZRQ=:hzrq", parameters);
					dao.doUpdate("delete from JC_FYHZ where JGID = :jgid and HZRQ=:hzrq", parameters);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "取消汇总信息出错!");
				}
				
			}
}
