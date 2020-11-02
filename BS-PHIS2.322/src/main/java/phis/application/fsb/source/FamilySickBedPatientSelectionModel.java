package phis.application.fsb.source;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.print.PrintException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class FamilySickBedPatientSelectionModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public FamilySickBedPatientSelectionModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void doGetPatientList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		List<Object> cnd = (List<Object>) req.get("cnd");
		String schema = (String) req.get("schema");
		try {
			res.put("body",
					dao.doList(cnd, "", "phis.application.fsb.schemas."
							+ schema));
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQuerySelectionForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		doGetSelectionForm(req, res, ctx);
		body.putAll((Map<String, Object>) res.get("body"));
		Map<String, Object> parameters = new HashMap<String, Object>();
		int ZYH = Integer.parseInt(body.get("ZYH") + "");
		int JSCS = Integer.parseInt(body.get("JSCS") + "");
		parameters.put("ZYH", ZYH);
		parameters.put("JSCS", JSCS);
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "ZYH", "i", ZYH);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "JSCS", "i", JSCS);
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			Map<String, Object> BRRY = dao
					.doLoad(BSPHISEntryNames.JC_BRRY, ZYH);
			Map<String, Object> ZYJS = dao.doLoad(cnd,
					BSPHISEntryNames.JC_JCJS);
			ZYJS.put("ZZRQ", BRRY.get("JSRQ"));
			body.putAll(BRRY);
			body.putAll(ZYJS);
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doQuerySelectionList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		parameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		int jscs = (Integer) body.get("JSCS");
		try {
			List<Map<String, Object>> FYZKBD = dao
					.doSqlQuery(
							"select distinct ZYH as ZYH,JSCS as JSCS,ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,sum(ZLJE) as ZLJE from (SELECT b.ZYH as ZYH,b.JSCS as JSCS,a.ZYGB as ZYGB,sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "
									+ "GY_SFXM a,JC_FYMX b WHERE a.SFXM = b.FYXM AND b.ZYH = :ZYH AND b.JSCS = "
									+ jscs
									+ " GROUP BY a.ZYGB,b.ZYH,b.JSCS union select "+body.get("ZYH")+" as ZYH,"+jscs+" as JSCS,ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "
									+ "GY_SFXM a where a.ZYSY = 1 and a.sfxm not in (SELECT FYXM from "
									+ "JC_FYMX where ZYH = :ZYH)) GROUP BY ZYH,ZYGB,JSCS order by ZYGB",
							parameters);
//			List<Map<String, Object>> FYZKBD = dao
//					.doSqlQuery(
//							"select distinct ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,sum(ZLJE) as ZLJE from (SELECT a.ZYGB as ZYGB,sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "
//									+ "GY_SFXM a,FYMX b WHERE (a.SFXM = b.FYXM ) AND (b.ZYH = :ZYH)  AND b.JSCS = "
//									+ jscs
//									+ " GROUP BY a.ZYGB union select ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "
//									+ "GY_SFXM a where a.ZYSY = 1 and a.sfxm not in (SELECT FYXM from "
//									+ "FYMX where ZYH = :ZYH)) GROUP BY ZYGB order by ZYGB",
//
//							parameters);
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.fsb.schemas.JC_JSGL_LIST");
			List<Map<String, Object>> RFYZKBD = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < FYZKBD.size(); i = i + 2) {
				Map<String, Object> FYZK = new HashMap<String, Object>();
				for (Map.Entry<String, Object> m : FYZKBD.get(i).entrySet()) {
					FYZK.put(m.getKey(), m.getValue());
				}
				if (i + 1 < FYZKBD.size()) {
					for (Map.Entry<String, Object> m : FYZKBD.get(i + 1)
							.entrySet()) {
						if (m.getKey().indexOf("text") > 0) {
							FYZK.put("ZYGB2_text", m.getValue());
						} else {
							FYZK.put(m.getKey() + "2", m.getValue());
						}
					}
				}
				RFYZKBD.add(FYZK);
			}
			res.put("body", RFYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetSelectionForm(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		parameters.put("ZYH", ZYH);
		try {
			if ("5".equals(body.get("JSLX") + "") || "4".equals(body.get("JSLX") + "")) {
				long count = dao.doCount("JC_BRRY", "CYPB = 1 AND ZYH = :ZYH",
						parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
			} else if ("10".equals(body.get("JSLX") + "")) {
				long count = dao
						.doCount(
								"JC_BRRY a,JC_JCJS b",
								"a.ZYH = b.ZYH and b.ZFPB = 0 and b.JSLX <> 4 and a.ZYH = :ZYH",
								parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
				}
			} else if ("1".equals(body.get("JSLX") + "")) {
				// if(body.get("YWLSH")!=null&&body.get("YWLSH")!=""&&(Long.parseLong(body.get("YWLSH")+"")>0l)){
				// throw new ModelDataOperationException(
				// ServiceCode.CODE_DATABASE_ERROR, "医保病人不能中途结算!");
				// }
				long count = dao
						.doCount(
								"JC_BRRY",
								"CYPB = 0 AND BRXZ NOT IN ( SELECT BRXZ FROM GY_BRXZ WHERE DBPB > 0 )  AND ZYH = :ZYH",
								parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在中途结算病人列表!");
				}
			}
			Map<String, Object> BRXX = dao
					.doLoad("SELECT ZYH as ZYH,JSRQ as ZZRQ,JCLX as JCLX,ZYHM as ZYHM,BRXM as BRXM,BRXZ as BRXZ,RYRQ as RYRQ,CYRQ as CYRQ,CYPB as CYPB,0 as JSLX,0.00 as FYHJ,0.00 as ZFHJ,0.00 as JKHJ,nvl(JSCS,0) as JSCS,KSRQ as KSRQ,KSRQ as LJRQ,HZRQ as HZRQ,HZRQ as XTRQ,'' as REMARK,0 as ZFPB FROM "
							+ "JC_BRRY" + " WHERE ZYH = :ZYH ", parameters);
			if ("5".equals(body.get("JSLX") + "") || "4".equals(body.get("JSLX") + "")) {
				Map<String, Object> CYRQ = dao.doLoad(
						"select CYRQ as JSRQ from " + "JC_BRRY"
								+ " where ZYH = :ZYH", parameters);
				BRXX.putAll(CYRQ);
			}
			if ("-1".equals(body.get("JSLX") + "")) {
				BRXX.put("JSLX", body.get("JSLX"));
			}
			if (body.containsKey("JSCS")) {
				BRXX.put("JSCS", body.get("JSCS"));
			}
//			if (BRXX.get("JSRQ") == null) {
//				BRXX.put("JSRQ", body.get("JSRQ"));
//			}
			if ("10".equals(body.get("JSLX") + "")) {
				BRXX = BSPHISUtil.gf_jc_gxmk_getjsje(BRXX, dao, ctx);
				BRXX.put("ZYHM", (String) body.get("FPHM"));// 用于LoadData数据时，将发票号码
															// 显示在家床号码的TextField上
			} else if ("5".equals(body.get("JSLX") + "")
					|| "4".equals(body.get("JSLX") + "")
					|| "1".equals(body.get("JSLX") + "")
					|| "0".equals(body.get("JSLX") + "")
					|| "-1".equals(body.get("JSLX") + "")) {
				Map<String, Object> GY_SFXMparameters = new HashMap<String, Object>();
				GY_SFXMparameters.put("ZYH",
						Long.parseLong(body.get("ZYH") + ""));
				GY_SFXMparameters.put("JGID", JGID);

				String jscs = "";
				if ("10".equals(body.get("JSLX") + "")) {
					jscs = "";
				} else if("-1".equals(body.get("JSLX") + "")){
					jscs = " and JSCS = "+body.get("JSCS");
				}else{
					jscs = " and JSCS = 0 ";
				}

				Map<String, Object> FYXX = dao.doLoad(
						"SELECT sum(a.ZJJE) as ZJJE FROM JC_FYMX a WHERE a.ZYH = :ZYH "
								+ jscs + " and a.JGID = :JGID",
						GY_SFXMparameters);
				Map<String, Object> YL_FYXX = dao
						.doLoad("SELECT sum(a.ZJJE) as ZJJE FROM JC_FYMX a WHERE a.ZYH = :ZYH and a.JGID = :JGID and YPLX = 0"+jscs,
								GY_SFXMparameters);
				Map<String, Object> YP_FYXX = dao
						.doLoad("SELECT sum(a.ZJJE) as ZJJE FROM JC_FYMX a WHERE a.ZYH = :ZYH and a.JGID = :JGID and YPLX <> 0"+jscs,
								GY_SFXMparameters);
				BRXX = BSPHISUtil.gf_jc_gxmk_getjkhj(BRXX, dao, ctx);
				BRXX = BSPHISUtil.gf_jc_gxmk_getzfhj(BRXX, dao, ctx);
				BRXX.put("FYHJ", 0);
				if (FYXX.get("ZJJE") != null) {
					BRXX.put("FYHJ", FYXX.get("ZJJE"));
				} else {
					BRXX.put("FYHJ", 0);
				}
				if (YL_FYXX.get("ZJJE") != null) {
					BRXX.put("YL_FYHJ", YL_FYXX.get("ZJJE"));
				} else {
					BRXX.put("YL_FYHJ", 0);
				}
				if (YP_FYXX.get("ZJJE") != null) {
					BRXX.put("YP_FYHJ", YP_FYXX.get("ZJJE"));
				} else {
					BRXX.put("YP_FYHJ", 0);
				}
			}
			BRXX.put("TCJE", "0.00");
			BRXX.put("ZHZF", "0.00");
			// if ("5".equals(body.get("JSLX") + "")){
			// if(body.get("YWLSH")!=null&&body.get("YWLSH")!=""&&(Long.parseLong(body.get("YWLSH")+"")>0l)){
			// JXMedicareModel jxmm = new JXMedicareModel(dao);
			// String cyyy = "";
			// Map<String,Object> JC_BRRY = dao.doLoad(BSPHISEntryNames.JC_BRRY,
			// ZYH);
			// SchemaUtil.setDictionaryMassageForList(JC_BRRY,"JC_BRRY_CY2");
			// cyyy = JC_BRRY.get("CYFS_text")+"";
			// String cyzdbm = "";
			// String cyzdmc = "";
			// List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "ZYH", "i",
			// (int)ZYH);
			// List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "ZDLB", "i",
			// 3);
			// List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			// List<Map<String,Object>> cyzd = dao.doList(cnd, "ZDXH",
			// BSPHISEntryNames.JC_RYZD);
			// if(cyzd!=null&&cyzd.size()>0){
			// cyzdbm = cyzd.get(0).get("ZDXH")+"";
			// cyzdmc = dao.doLoad(BSPHISEntryNames.GY_JBBM,
			// Long.parseLong(cyzdbm)).get("JBMC")+"";
			// }
			// Map<String,Object> ybmap = new HashMap<String, Object>();
			// ybmap.put("YWLSH", body.get("YWLSH"));//医院流水号
			// ybmap.put("CYYY", cyyy);//撤床原因
			// ybmap.put("CYZDBM", cyzdbm);//撤床诊断编码
			// ybmap.put("CYZDMC", cyzdmc);//撤床诊断名称
			// ybmap.put("ZHSYBZ", 1);//帐号使用标志
			// ybmap.put("ZTJSBZ", 0);//TJSBZ中途结算标志0非中途1中途结算
			// Map<String,Object> YBJS =
			// jxmm.saveHospitalizationPreSettlementCosts(ybmap, ctx);
			// BRXX.put("ZFHJ", YBJS.get("自费费用"));
			// BRXX.put("TCJE", YBJS.get("统筹支出"));
			// BRXX.put("ZHZF", YBJS.get("本次帐户支付"));
			// BRXX.putAll(YBJS);
			// }
			// }
//			SchemaUtil.setDictionaryMassageForForm(BRXX, "phis.application.fsb.schemas.JC_JSGL_FORM");
			res.put("body", BRXX);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡失败");
		}
	}

	@SuppressWarnings("unchecked")
	public void doGetSelectionList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		parameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		String jscs = " AND b.JSCS = 0";
		int ijscs = 0;
		if ("10".equals(body.get("JSLX") + "")) {
			jscs = " AND b.JSCS = " + (Integer) body.get("JSCS");
			ijscs = (Integer) body.get("JSCS");
		} else if (("5".equals(body.get("JSLX") + ""))
				|| ("1".equals(body.get("JSLX") + ""))) {
			jscs = " AND b.JSCS = 0";
		}

		try {
			List<Map<String, Object>> FYZKBD = dao
					.doSqlQuery(
							"select distinct ZYH as ZYH,JSCS as JSCS,ZYGB as ZYGB,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,sum(ZLJE) as ZLJE from (SELECT b.ZYH as ZYH,b.JSCS as JSCS,a.ZYGB as ZYGB,sum(b.ZJJE) as ZJJE,sum(b.ZFJE) as ZFJE,sum(b.ZLJE) as ZLJE FROM "
									+ "GY_SFXM a,JC_FYMX b WHERE a.SFXM = b.FYXM AND b.ZYH = :ZYH "
									+ jscs
									+ " GROUP BY a.ZYGB,b.ZYH,b.JSCS union select "+body.get("ZYH")+" as ZYH,"+ijscs+" as JSCS,ZYGB as ZYGB,0 as ZJJE,0 as ZFJE,0 as ZLJE from "
									+ "GY_SFXM"
									+ " a where a.ZYSY = 1 and a.sfxm not in (SELECT FYXM from "
									+ "JC_FYMX"
									+ " where ZYH = :ZYH)) GROUP BY ZYH,ZYGB,JSCS order by ZYGB",
							parameters);
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.fsb.schemas.JC_JSGL_LIST");
			List<Map<String, Object>> RFYZKBD = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < FYZKBD.size(); i = i + 2) {
				Map<String, Object> FYZK = new HashMap<String, Object>();
				for (Map.Entry<String, Object> m : FYZKBD.get(i).entrySet()) {
					FYZK.put(m.getKey(), m.getValue());
				}
				if (i + 1 < FYZKBD.size()) {
					for (Map.Entry<String, Object> m : FYZKBD.get(i + 1)
							.entrySet()) {
						if (m.getKey().indexOf("text") > 0) {
							FYZK.put("ZYGB2_text", m.getValue());
						} else {
							FYZK.put(m.getKey() + "2", m.getValue());
						}
					}
				}
				RFYZKBD.add(FYZK);
			}
			res.put("body", RFYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doGetSelectionDetailsList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long fyxm = Long.parseLong(body.get("ZYGB") + "");
		String jscs = body.get("JSCS") + "";
		parameters.put("ZYH", zyh);
		parameters.put("FYXM", fyxm);
		try {
			List<Map<String, Object>> FYZKBD = dao
					.doSqlQuery(
							"SELECT ZYH as ZYH,JSCS as JSCS,ZJJE as ZJJE,ZFJE as ZFJE,to_char(FYRQ,'yyyy-mm-dd') as FYRQ,FYXM as FYXM,YSGH as YSGH,FYKS as FYKS,ZXKS as ZXKS,FYSL as FYSL,FYDJ as FYDJ,ZFBL as ZFBL,FYMC as FYMC,FYXH as FYXH FROM JC_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "
									+ jscs,
							parameters);
			SchemaUtil.setDictionaryMassageForList(FYZKBD,
					"phis.application.fsb.schemas.JC_FYMX_MX");
			res.put("body", FYZKBD);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doGetSelectionFeesDetailsList(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long zyh = Long.parseLong(body.get("ZYH") + "");
		long fyxm = Long.parseLong(body.get("ZYGB") + "");
		String jscs = body.get("JSCS") + "";
		String openBy = req.get("openBy")+"";
		parameters.put("ZYH", zyh);
		parameters.put("FYXM", fyxm);
		try {
			if("sfxm".equals(openBy)){
				List<Map<String, Object>> FYZKBD = dao
						.doSqlQuery(
								"SELECT ZYH as ZYH,JSCS as JSCS,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,sum(ZLJE) as ZLJE,to_char(FYRQ,'yyyy-mm-dd') as FYRQ,FYXM as FYXM,YSGH as YSGH,FYKS as FYKS,ZXKS as ZXKS FROM JC_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "
										+ jscs
										+ " GROUP BY ZYH,JSCS,to_char(FYRQ,'yyyy-mm-dd'),FYXM,YSGH,FYKS,ZXKS",
								parameters);
				SchemaUtil.setDictionaryMassageForList(FYZKBD,
						"phis.application.fsb.schemas.JC_FYMX_SFXM");
				res.put("body", FYZKBD);
			}else if("mxxm".equals(openBy)){
				List<Map<String, Object>> FYZKBD = dao
						.doSqlQuery(
								"SELECT ZYH as ZYH,JSCS as JSCS,sum(FYSL) as FYSL,sum(ZJJE) as ZJJE,sum(ZFJE) as ZFJE,FYXH as FYXH,FYMC as FYMC FROM JC_FYMX WHERE ZYH = :ZYH and FYXM = :FYXM and JSCS = "
										+ jscs
										+ " GROUP BY ZYH,JSCS,FYXH,FYMC",
								parameters);
				SchemaUtil.setDictionaryMassageForList(FYZKBD,
						"phis.application.fsb.schemas.JC_FYMX_MXXM");
				res.put("body", FYZKBD);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取费用帐卡列表失败");
		}
	}
	/**
	 * 获取发票号码
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	public void doGetSelectionFPHM(Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String fphm = BSPHISUtil.GetFsbBillnumber("发票", dao, ctx);
		if (!"false".equals(fphm)) {
			res.put("FPHM", fphm);
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, Object> FKFS = dao.doLoad(
					"SELECT FKFS as FKFS,FKJD as FKJD FROM " + "GY_FKFS"
							+ " WHERE SYLX = 2 AND MRBZ = 1", parameters);
			if (FKFS != null) {
				body.putAll(FKFS);
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "默认付款方式未设置!");
			}
			Map<String, Object> HBWC = dao.doLoad(
					"SELECT FKFS as WCFS,FKJD as WCJD FROM " + "GY_FKFS"
							+ " WHERE SYLX = 2 AND HBWC = 1", parameters);
			if (HBWC != null) {
				body.putAll(HBWC);
			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "默认货币误差未设置!");
			}
			res.put("body", body);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "付款方式获取失败");
		}
	}

	/**
	 * 家床结算
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveSettleAccounts(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String userId = (String) user.getUserId();
		Map<String, Object> map_jsxx = (Map<String, Object>) req.get("body");
		Map<String, Object> body = (Map<String, Object>) map_jsxx.get("JSXX");
		Map<String, Object> parameters = new HashMap<String, Object>();
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		Date date = new Date();
		parameters.put("ZYH", ZYH);
//		Map<String, Object> CYRQ = new HashMap<String, Object>();
		try {
			Date ksrq = BSHISUtil.toDate(body.get("KSRQ") + "");
			if("4".equals(body.get("JSLX") + "")){
				long count = dao.doCount("JC_BRRY", "CYPB = 1 AND ZYH = :ZYH",
						parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
//				CYRQ = dao.doLoad(
//						"select CYRQ as CYRQ from JC_BRRY where ZYH = :ZYH",parameters);
				Map<String, Object> JC_JCJS = new HashMap<String, Object>();
				JC_JCJS.put("ZYH", body.get("ZYH"));
				JC_JCJS.put("JSCS", Integer.parseInt(body.get("JSCS") + "") + 1);
				JC_JCJS.put("JSLX", body.get("JSLX"));
				if(Integer.parseInt(body.get("JSCS") + "")>=1){
					long countjs = dao.doCount("JC_JCJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from JC_JCJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
				JC_JCJS.put("KSRQ", ksrq);
				JC_JCJS.put("ZZRQ", date);
				JC_JCJS.put("BRXZ", body.get("BRXZ"));
				JC_JCJS.put("FYHJ", body.get("FYHJ"));
				JC_JCJS.put("ZFHJ", body.get("ZFHJ"));
				JC_JCJS.put("CZGH", userId);
				JC_JCJS.put("ZFPB", 0);
				JC_JCJS.put("FYZJ", body.get("FYHJ"));
				JC_JCJS.put("ZFZJ", body.get("ZFHJ"));
				JC_JCJS.put("JSZJ", body.get("JSJE"));
				JC_JCJS.put("JGID", JGID);
				JC_JCJS.put("JKHJ", body.get("JKHJ"));
				JC_JCJS.put("JKZJ", body.get("JKHJ"));// 缴款总计
				JC_JCJS.put("YSJE", body.get("YSJE"));
				JC_JCJS.put("JSRQ", date);
				JC_JCJS.put("FPHM", "");
				
				// 将JC_TBKK打上结算次数
				Map<String, Object> JC_TBKKparameters = new HashMap<String, Object>();
				JC_TBKKparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				JC_TBKKparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				JC_TBKKparameters.put("JGID", JGID);
				dao.doUpdate(
						"UPDATE JC_TBKK SET JSCS = :UJSCS Where ZYH = :ZYH  And JGID = :JGID and JSCS = 0 ",
						JC_TBKKparameters);
				// 将JC_FYMX打上结算次数
				Map<String, Object> JC_FYMXparameters = new HashMap<String, Object>();
				JC_FYMXparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				JC_FYMXparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				JC_FYMXparameters.put("JGID", JGID);

				dao.doUpdate(
						"UPDATE JC_FYMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
						JC_FYMXparameters);
				// 将JC_HCMX打上结算次数
//				dao.doUpdate(
//						"UPDATE JC_HCMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
//						JC_FYMXparameters);
				// 写结算费用明细表JC_FYMX_JS
				Map<String, Object> JC_FYMX_JSparameters = new HashMap<String, Object>();
				JC_FYMX_JSparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				JC_FYMX_JSparameters.put("ZYH",
						Long.parseLong(body.get("ZYH") + ""));
				dao.doSqlUpdate(
						"INSERT INTO JC_FYMX_JS SELECT * FROM JC_FYMX WHERE ZYH = :ZYH And JSCS = :UJSCS",
						JC_FYMX_JSparameters);
				// 清空病人床位
//				Map<String, Object> JC_CWSZparameters = new HashMap<String, Object>();
//				JC_CWSZparameters.put("JGID", JGID);
//				JC_CWSZparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));

				Map<String, Object> JC_BRRYparameters = new HashMap<String, Object>();
				JC_BRRYparameters.put("ZZRQ", JC_JCJS.get("ZZRQ"));
				JC_BRRYparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				JC_BRRYparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				JC_BRRYparameters.put("JGID", JGID);
				// 根据不同结算类型 清空床位方式不同
				dao.doUpdate(
						"UPDATE JC_BRRY SET CCJSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = :ZZRQ ,CYPB = 9 Where ZYH = :ZYH And JGID = :JGID",
						JC_BRRYparameters);
//				dao.doUpdate(
//						"UPDATE  JC_CWSZ Set ZYH = Null,YEWYH = Null Where ZYH = :ZYH  And JGID = :JGID",
//						JC_CWSZparameters);
				// 准备家床付款信息表单数据
				Map<String, Object> JC_FKXX = new HashMap<String, Object>();
				JC_FKXX.put("FKFS", body.get("FKFS"));
				JC_FKXX.put("FKJE", body.get("YSJE"));
				JC_FKXX.put("JGID", JGID);
				JC_FKXX.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				JC_FKXX.put("JSCS", (Integer) body.get("JSCS") + 1);
				dao.doSave("create", BSPHISEntryNames.JC_JCJS, JC_JCJS, false);
				Session session = (Session) ctx.get(Context.DB_SESSION);
				session.flush();
				// 写结算明细(JC_JSMX)
				Map<String, Object> JC_JSMXparameters = new HashMap<String, Object>();
				JC_JSMXparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
				JC_JSMXparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				dao.doSqlUpdate(
						"INSERT INTO "
								+ "JC_JSMX"
								+ " (ZYH,JSCS,KSDM,FYXM,ZJJE,ZFJE,ZLJE,JGID) SELECT ZYH,:UJSCS,FYKS,FYXM,sum(ZJJE) ZJJE,sum(ZFJE) ZFJE,sum(ZLJE) ZLJE,JGID FROM "
								+ "JC_FYMX"
								+ " WHERE ZYH = :ZYH AND JSCS = :UJSCS GROUP BY ZYH,FYKS,FYXM,JGID Having ( sum(ZJJE) <> 0 Or sum(ZFJE) <> 0 Or sum(ZLJE) <> 0 )",
						JC_JSMXparameters);
				// Map<String,Object> JLXH =
				dao.doSave("create", BSPHISEntryNames.JC_FKXX, JC_FKXX, false);
				if (Double.parseDouble(body.get("WCJE") + "") != 0) {
					Map<String, Object> JC_HBWC = new HashMap<String, Object>();
					JC_HBWC.put("FKFS", body.get("WCFS"));
					JC_HBWC.put("FKJE", body.get("WCJE"));
					JC_HBWC.put("JGID", JGID);
					JC_HBWC.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
					JC_HBWC.put("JSCS", (Integer) body.get("JSCS") + 1);
					dao.doSave("create", BSPHISEntryNames.JC_FKXX, JC_HBWC, false);
				}
				return;
			}
			Date zzrq = date;
			
			if ("5".equals(body.get("JSLX") + "")) {
				long count = dao.doCount("JC_BRRY", "CYPB = 1 AND ZYH = :ZYH",
						parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在结算病人列表!");
				}
				Map<String, Object> CYRQ = dao.doLoad(
						"select CYRQ as CYRQ from JC_BRRY where ZYH = :ZYH",
						parameters);
				zzrq = (Date) CYRQ.get("CYRQ");
				if(Integer.parseInt(body.get("JSCS") + "")>=1){
					long countjs = dao.doCount("JC_JCJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from JC_JCJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
				// CYRQ =
				// dao.doLoad("select CYSJ as CYRQ from "+BSPHISEntryNames.JC_CYJL+" where JLXH=(select max(JLXH) from "+BSPHISEntryNames.JC_CYJL+" where ZYH=:ZYH and CZLX=3)",
				// parameters);
			} else if ("1".equals(body.get("JSLX") + "")) {
				long count = dao
						.doCount(
								"JC_BRRY",
								"CYPB = 0 AND BRXZ NOT IN ( SELECT BRXZ FROM GY_BRXZ WHERE DBPB > 0 )  AND ZYH = :ZYH",
								parameters);
				if (count == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在中途结算病人列表!");
				}
				if(Integer.parseInt(body.get("JSCS") + "")>=1){
					long countjs = dao.doCount("JC_JCJS", "ZFPB = 0 AND ZYH = :ZYH",
							parameters);
					if (countjs > 0) {
						Map<String, Object> KSRQ = dao.doLoad(
								"select max(ZZRQ) as ZZRQ from JC_JCJS where ZYH = :ZYH and ZFPB=0",
								parameters);
						ksrq = (Date) KSRQ.get("ZZRQ");
					}
				}
			}

			Map<String, Object> JC_JCJS = new HashMap<String, Object>();
			JC_JCJS.put("ZYH", body.get("ZYH"));
			JC_JCJS.put("JSCS", Integer.parseInt(body.get("JSCS") + "") + 1);
			JC_JCJS.put("JSLX", body.get("JSLX"));
			JC_JCJS.put("KSRQ", ksrq);
			JC_JCJS.put("ZZRQ", zzrq);
			JC_JCJS.put("BRXZ", body.get("BRXZ"));
			JC_JCJS.put("FYHJ", body.get("FYHJ"));
			JC_JCJS.put("ZFHJ", body.get("ZFHJ"));
			JC_JCJS.put("CZGH", userId);
			JC_JCJS.put("ZFPB", 0);
			JC_JCJS.put("FYZJ", body.get("FYHJ"));
			JC_JCJS.put("ZFZJ", body.get("ZFHJ"));
			JC_JCJS.put("JSZJ", body.get("JSJE"));
			JC_JCJS.put("JGID", JGID);
			JC_JCJS.put("JKHJ", body.get("JKHJ"));
			JC_JCJS.put("JKZJ", body.get("JKHJ"));// 缴款总计
			JC_JCJS.put("YSJE", body.get("YSJE"));
			JC_JCJS.put("JSRQ", date);
			JC_JCJS.put("FPHM", body.get("FPHM"));
			if ("5".equals(body.get("JSLX") + "")) {// 正常结算

			}
			if (BSPHISUtil.SetFsbBillNumber("发票", body.get("FPHM") + "", dao, ctx)) {

			} else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "更新发票号码失败!");
			}
			// 将JC_TBKK打上结算次数
			Map<String, Object> JC_TBKKparameters = new HashMap<String, Object>();
			JC_TBKKparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
			JC_TBKKparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
			JC_TBKKparameters.put("JGID", JGID);
			dao.doUpdate(
					"UPDATE JC_TBKK SET JSCS = :UJSCS Where ZYH = :ZYH  And JGID = :JGID and JSCS = 0 ",
					JC_TBKKparameters);
			// 将JC_FYMX打上结算次数
			Map<String, Object> JC_FYMXparameters = new HashMap<String, Object>();
			JC_FYMXparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
			JC_FYMXparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
			JC_FYMXparameters.put("JGID", JGID);

			dao.doUpdate(
					"UPDATE JC_FYMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
					JC_FYMXparameters);
			// 将JC_HCMX打上结算次数
//			dao.doUpdate(
//					"UPDATE JC_HCMX SET JSCS = :UJSCS WHERE ZYH = :ZYH AND JSCS = 0 And JGID = :JGID",
//					JC_FYMXparameters);
			// 写结算费用明细表JC_FYMX_JS
			Map<String, Object> JC_FYMX_JSparameters = new HashMap<String, Object>();
			JC_FYMX_JSparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
			JC_FYMX_JSparameters.put("ZYH",
					Long.parseLong(body.get("ZYH") + ""));
			dao.doSqlUpdate(
					"INSERT INTO JC_FYMX_JS SELECT * FROM JC_FYMX WHERE ZYH = :ZYH And JSCS = :UJSCS",
					JC_FYMX_JSparameters);
			// 清空病人床位
//			Map<String, Object> JC_CWSZparameters = new HashMap<String, Object>();
//			JC_CWSZparameters.put("JGID", JGID);
//			JC_CWSZparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));

			Map<String, Object> JC_BRRYparameters = new HashMap<String, Object>();
			JC_BRRYparameters.put("ZZRQ", JC_JCJS.get("ZZRQ"));
			JC_BRRYparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
			JC_BRRYparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
			JC_BRRYparameters.put("JGID", JGID);
			// 根据不同结算类型 清空床位方式不同
			if ("5".equals(body.get("JSLX") + "")) {// 正常结算
				dao.doUpdate(
						"UPDATE JC_BRRY SET JSRQ = :ZZRQ,CCJSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = :ZZRQ ,CYPB = 8 Where ZYH = :ZYH And JGID = :JGID",
						JC_BRRYparameters);
//				dao.doUpdate(
//						"UPDATE  JC_CWSZ Set ZYH = Null,YEWYH = Null Where ZYH = :ZYH  And JGID = :JGID",
//						JC_CWSZparameters);
				// modify by yangl 去除清空BRCH操作
				// dao.doUpdate(
				// "UPDATE "
				// + BSPHISEntryNames.JC_BRRY
				// + " Set BRCH = Null Where ZYH = :ZYH  And JGID = :JGID",
				// JC_CWSZparameters);
			} else if ("1".equals(body.get("JSLX") + "")) {// 中途结算
				dao.doUpdate(
						"UPDATE JC_BRRY SET CCJSRQ = :ZZRQ,JSCS = :UJSCS,CYRQ = null ,CYPB = 0 Where ZYH = :ZYH And JGID = :JGID",
						JC_BRRYparameters);
			}

			// 准备家床付款信息表单数据
			Map<String, Object> JC_FKXX = new HashMap<String, Object>();
			JC_FKXX.put("FKFS", body.get("FKFS"));
			JC_FKXX.put("FKJE", body.get("YSJE"));
			JC_FKXX.put("JGID", JGID);
			JC_FKXX.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
			JC_FKXX.put("JSCS", (Integer) body.get("JSCS") + 1);
			dao.doSave("create", BSPHISEntryNames.JC_JCJS, JC_JCJS, false);
			Session session = (Session) ctx.get(Context.DB_SESSION);
			session.flush();
			// 写结算明细(JC_JSMX)
			Map<String, Object> JC_JSMXparameters = new HashMap<String, Object>();
			JC_JSMXparameters.put("UJSCS", (Integer) body.get("JSCS") + 1);
			JC_JSMXparameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
			dao.doSqlUpdate(
					"INSERT INTO "
							+ "JC_JSMX"
							+ " (ZYH,JSCS,KSDM,FYXM,ZJJE,ZFJE,ZLJE,JGID) SELECT ZYH,:UJSCS,FYKS,FYXM,sum(ZJJE) ZJJE,sum(ZFJE) ZFJE,sum(ZLJE) ZLJE,JGID FROM "
							+ "JC_FYMX"
							+ " WHERE ZYH = :ZYH AND JSCS = :UJSCS GROUP BY ZYH,FYKS,FYXM,JGID Having ( sum(ZJJE) <> 0 Or sum(ZFJE) <> 0 Or sum(ZLJE) <> 0 )",
					JC_JSMXparameters);
			// Map<String,Object> JLXH =
			dao.doSave("create", BSPHISEntryNames.JC_FKXX, JC_FKXX, false);
			if (Double.parseDouble(body.get("WCJE") + "") != 0) {
				Map<String, Object> JC_HBWC = new HashMap<String, Object>();
				JC_HBWC.put("FKFS", body.get("WCFS"));
				JC_HBWC.put("FKJE", body.get("WCJE"));
				JC_HBWC.put("JGID", JGID);
				JC_HBWC.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				JC_HBWC.put("JSCS", (Integer) body.get("JSCS") + 1);
				dao.doSave("create", BSPHISEntryNames.JC_FKXX, JC_HBWC, false);
			}
			if ("5".equals(body.get("JSLX") + "")) {
				// try{
				// if(map_jsxx.containsKey("YBJS")){
				// Map<String,Object>
				// map_zyjs=(Map<String,Object>)map_jsxx.get("YBJS");
				// map_zyjs.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
				// map_zyjs.put("JSRQ", new Date());
				// map_zyjs.put("CZGH", userId);
				// map_zyjs.put("FPHM", body.get("FPHM"));
				// map_zyjs.put("JGID", JGID);
				// map_zyjs.put("BRXZ", Integer.parseInt(body.get("BRXZ")+""));
				// dao.doSave("create", BSPHISEntryNames.YB_ZYJS, map_zyjs,
				// false);
				// }else if(map_jsxx.containsKey("SZYB")){//省医保
				//
				// MedicareSYBModel mm = new MedicareSYBModel(dao);
				// Map<String, Object> jyqr = (Map<String, Object>) map_jsxx
				// .get("jyqr");
				// mm.doSaveSzYbjyqr("update", jyqr, res, ctx);
				// Map<String,Object>
				// map_zyjs=(Map<String,Object>)map_jsxx.get("SZYBJS");
				// // map_zyjs.put("MZXH", mzxh.get("MZXH"));
				// map_zyjs.put("FPHM", JC_JCJS.get("FPHM"));
				// map_zyjs.put("JKFS", JC_FKXX.get("FKFS"));
				// map_zyjs.put("JGID", JGID);
				// map_zyjs.put("JSRQ", new Date());
				// map_zyjs.put("CZGH", userId);
				// mm.saveSzYbzyjsxx("create",map_zyjs, ctx);
				// }
				// }catch(Exception e){
				// throw new ModelDataOperationException(
				// ServiceCode.CODE_DATABASE_ERROR,
				// "撤床结算失败,医保端结算成功,本地结算失败:"+e.getMessage());
				// }
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "撤床结算失败!");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "撤床结算失败!");
		}
	}

	/**
	 * 取消结算，作废
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateSettleAccounts(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String userId = (String) user.getUserId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long ZYH = Long.parseLong(body.get("ZYH") + "");
		int JSCS = (Integer) body.get("JSCS");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		try {
			long count = dao
					.doCount(
							"JC_BRRY a,JC_JCJS b",
							"a.ZYH = b.ZYH and b.ZFPB = 0 and b.JSLX <> 4 and a.ZYH = :ZYH",
							parameters);
			if (count == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "当前病人已不在发票作废病人列表!");
			}
			Map<String, Object> parameters2 = new HashMap<String, Object>();
			parameters2.put("ZYH", ZYH);
			parameters2.put("JGID", JGID);
			count = dao.doCount("JC_BRRY",
					"ZYH=:ZYH and JGID=:JGID and cypb<8", parameters2);
			if (count > 0) {
				if (("5".equals(body.get("JSBS") + ""))) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"当前病人已在院,不能进行发票作废!");
				}
			}else{
				if (("1".equals(body.get("JSBS") + ""))) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"当前病人已撤床结算,不能进行中途结算发票作废!");
				}
			}
			// 将结算发票作废
			Map<String, Object> JC_JCJSparameters = new HashMap<String, Object>();
			JC_JCJSparameters.put("ZFRQ", new Date());
			JC_JCJSparameters.put("ZFGH", userId);
			JC_JCJSparameters.put("ZYH", ZYH);
			JC_JCJSparameters.put("JGID", JGID);
			JC_JCJSparameters.put("JSCS", JSCS);
			dao.doUpdate(
					"UPDATE JC_JCJS SET ZFPB = 1,ZFRQ = :ZFRQ,ZFGH = :ZFGH WHERE ZYH=:ZYH AND JGID = :JGID AND ZFPB = 0 And JSCS =:JSCS",
					JC_JCJSparameters);
			Map<String, Object> JC_JSZF = new HashMap<String, Object>();
			JC_JSZF.put("ZYH", ZYH);
			JC_JSZF.put("JSCS", JSCS);
			JC_JSZF.put("JGID", JGID);
			JC_JSZF.put("ZFGH", userId);
			JC_JSZF.put("ZFRQ", new Date());
			dao.doSave("create", BSPHISEntryNames.JC_JSZF, JC_JSZF, false);
			// 清除JC_FYMX中结算次数
			Map<String, Object> JC_FYMXparameters = new HashMap<String, Object>();
			JC_FYMXparameters.put("ZYH", ZYH);
			JC_FYMXparameters.put("JSCS", JSCS);
			JC_FYMXparameters.put("JGID", JGID);
			dao.doUpdate(
					"UPDATE JC_FYMX Set JSCS = 0 Where ZYH = :ZYH AND JSCS = :JSCS and JGID = :JGID",
					JC_FYMXparameters);
			// 清除JC_TBKK中结算次数
			Map<String, Object> JC_TBKKparameters = new HashMap<String, Object>();
			JC_TBKKparameters.put("ZYH", ZYH);
			JC_TBKKparameters.put("JSCS", JSCS);
			JC_TBKKparameters.put("JGID", JGID);
			long ll_Count = dao.doCount("JC_TBKK",
					"ZYH = :ZYH AND JSCS = :JSCS  and JGID = :JGID",
					JC_TBKKparameters);
			if (ll_Count > 0) {
				dao.doUpdate(
						"UPDATE JC_TBKK Set JSCS = 0 Where ZYH = :ZYH AND JSCS = :JSCS  and JGID = :JGID",
						JC_TBKKparameters);
			}
			Map<String, Object> JC_BRRYparameters = new HashMap<String, Object>();
			JC_BRRYparameters.put("ZYH", ZYH);
			JC_BRRYparameters.put("JGID", JGID);
			if (("5".equals(body.get("JSBS") + ""))) {
				dao.doUpdate(
						"UPDATE JC_BRRY SET CYPB = 1,CCJSRQ = null Where ZYH  = :ZYH  and JGID = :JGID",
						JC_BRRYparameters);
//			} else if (("1".equals(body.get("JSBS") + ""))) {
//				dao.doUpdate(
//						"UPDATE JC_BRRY SET CYPB = 0,JSRQ = null Where ZYH  = :ZYH  and JGID = :JGID",
//						JC_BRRYparameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算作废失败!");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "结算作废失败!");
		}
	}
	
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdftime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fphm = request.get("fphm") + "";
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> JSXX = new HashMap<String, Object>();
		List<Map<String, Object>> SFXMS = new ArrayList<Map<String, Object>>();
		Map<String, Object> JKJES = new HashMap<String, Object>();
		Map<String, Object> YBJES = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(
				"select a.BRXZ as BRXZ,a.JSCS as JSCS,a.JKHJ as JKHJ,d.XZMC as RYLB,c.PERSONNAME as SYY,b.ZYH as ZYH,b.ZYHM as XLH,b.BRXM as XM,to_char(a.KSRQ,'yyyy-mm-dd hh24:mi:ss') as RYRQ,to_char(a.KSRQ,'yyyymmdd') as RYRQ1,to_char(a.JSRQ,'yyyy-mm-dd hh24:mi:ss') as CYRQ,to_char(a.JSRQ,'yyyymmdd') as CYRQ1,to_char(b.RYRQ, 'mm') as RYMM,to_char(b.RYRQ, 'dd') as RYDD,to_char(b.CYRQ, 'mm') as CYMM,to_char(b.CYRQ, 'dd') as CYDD,a.FYHJ as HJJE,a.ZFHJ as ZFJE,to_char(a.JSRQ, 'yyyy') as YYYY,to_char(a.JSRQ, 'mm') as MM,to_char(a.JSRQ, 'dd') as DD from ");
		hql.append("JC_JCJS");
		hql.append(" a,");
		hql.append("JC_BRRY");
		hql.append(" b,");
		hql.append("SYS_Personnel");
		hql.append(" c,");
		hql.append("GY_BRXZ");
		hql.append(" d where a.BRXZ = d.BRXZ and a.CZGH = c.PERSONID and a.ZYH = b.ZYH and a.FPHM = :FPHM and a.JGID = :JGID");
		parameters.put("FPHM", fphm);
		parameters.put("JGID", JGID);
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		StringBuffer hql1 = new StringBuffer(
				"select b.ZYPL as ZYPL,nvl(b.MCSX,b.SFMC) as MCSX,sum(a.ZJJE) as ZJJE from ");
		hql1.append("GY_SFXM");
		hql1.append(" b left outer join JC_FYMX a on a.FYXM = b.SFXM and a.ZYH = :ZYH and a.JSCS = :JSCS group by b.ZYPL,b.MCSX,b.SFMC");
		StringBuffer hql2 = new StringBuffer("select sum(a.JKJE) as JKJE from ");
		hql2.append("JC_TBKK");
		hql2.append(" a");
		hql2.append(" where a.ZYH = :ZYH and a.JSCS=:JSCS and a.ZFPB=0");
		
		//医保家床结算sql语句
		StringBuffer hql4 = new StringBuffer("select a.JYLSH as JYLSH,a.ZXLSH as ZXLSH,a.ZYH as ZYH,a.FPHM as FPHM,a.JSRQ as JSRQ,a.CZGH as CZGH,a.DWBH as DWBH,a.YLLB as YLLB," +
				"a.RYLB as RYLB,a.YLFZE as YLFZE,a.GRZFJE as GRZFJE,a.YLYPZL as YLYPZL,a.TJZL as TJZL,a.TZZL as TZZL,a.QFZFJE as QFZFJE," +
				"a.QFZHZF as QFZHZF,a.QFXJZF as QFXJZF,a.FDZL as FDZL,a.YSZHZF as YSZHZF,a.YSXJZF as YSXJZF,a.FDZF1 as FDZF1,a.FDZF2 as FDZF2," +
				"a.FDZF3 as FDZF3,a.FDZF4 as FDZF4,a.FDZF5 as FDZF5,a.CDGRZF as CDGRZF,a.BNZHZF as BNZHZF,a.LNZHZF as LNZHZF,a.TCZF as TCZF," +
				"a.GRXJZF as GRXJZF,a.JZJZF as JZJZF,a.GWJJZF as GWJJZF,a.ZFPB as ZFPB,a.ZFRQ as ZFRQ,a.CARDNO as CARDNO,a.BRXZ as BRXZ,a.RYXZ as RYXZ," +
				"a.LJFY as LJFY,a.YYDM as YYDM,a.YYLSH as YYLSH,a.YWZQH as YWZQH,a.KLZJE as KLZJE,a.ZFYWZQH as ZFYWZQH,a.LXJJ as LXJJ," +
				"a.ZNTCJJ as ZNTCJJ,a.LFJJ as LFJJ,a.LXJSJJ as LXJSJJ,a.KNJZJJ as KNJZJJ,a.FYDM as FYDM,a.DWLX as DWLX,a.DYLB as DYLB,a.LNJMJJ as LNJMJJ," +
				"a.SNETJJ as SNETJJ,a.NMGJJ as NMGJJ,a.JCTS as JCTS,a.BBLB as BBLB,a.GFKZJJ as GFKZJJ,a.GFJFJJ as GFJFJJ,a.LFKZJJ as LFKZJJ," +
				"a.LFJFJJ as LJJFJJ,a.FSZJJ as FSZJJ,a.FSJJJ as FSJJJ,a.FTJJJ as FTJJJ,a.FJJJJ as FJJJJ,a.FCJJJ as FCJJJ,a.BJDJ as BJDJ,a.CQZNJJ as CQZNJJ," +
				"a.CQGFJJ as CQGFJJ,a.CQLFJJ as CQLFJJ,a.CQH as CQH,a.ZFLSH as ZFLSH,a.XNHJJ as XNHJJ,a.DXSJJ as DXSJJ,a.EJBJZF as EJBJZF,a.ZYCS as ZYCS," +
				"a.LMJJ as LMJJ,a.LNZHZL as LNZHZL,a.CJDD as CJDD,a.SCZF as SCZF,a.SCZL as SCZL,a.YYCD as YYCD,a.DBBZ as DBBZ,a.QBNZHYE as QBNZHYE," +
				"a.QLNZHYE as QLNZHYE,a.QZHYE as QZHYE,a.HBNZHYE as HBNZHYE,a.HLNZHYE as HLNZHYE,a.HZHYE as HZHYE,a.JGID as JGID,a.JSCS as JSCS" +
				" from ");
		hql4.append(" where a.ZYH=:ZYH and a.FPHM=:FPHM and a.JGID=:JGID");
		
		try {
			if (dao.doQuery(hql.toString(), parameters) != null
					&& dao.doQuery(hql.toString(), parameters).size() > 0) {
				JSXX = dao.doQuery(hql.toString(), parameters).get(0);
				parameters1.put("ZYH", JSXX.get("ZYH"));
				parameters1.put("JSCS", JSXX.get("JSCS"));				
				SFXMS = dao.doSqlQuery(hql1.toString(), parameters1);
				JKJES = dao.doQuery(hql2.toString(), parameters1).get(0);
				for (int i = 0; i < SFXMS.size(); i++) {
					Map<String, Object> SFXM = SFXMS.get(i);
					if (SFXM.get("ZYPL")!=null&&(SFXM.get("ZYPL")+"").length()>0) {
						if("其 它".equals(SFXM.get("MCSX") + "")||"其它".equals(SFXM.get("MCSX") + "")){
							response.put("QTPL", SFXM.get("ZYPL"));
							response.put("SFXM"+SFXM.get("ZYPL"), SFXM.get("MCSX") + "");
							if(SFXM.get("ZJJE")!=null&&(SFXM.get("ZJJE")+"").length()>0){
								response.put("XMJE"+SFXM.get("ZYPL"), SFXM.get("ZJJE") + "");
							}else{
								response.put("XMJE"+SFXM.get("ZYPL"), "0.00");
							}
							continue;
						}else{
							if(SFXM.get("ZJJE")!=null&&(SFXM.get("ZJJE")+"").length()>0){
								if(Double.parseDouble(SFXM.get("ZJJE")+"")>0){
									response.put("SFXM"+SFXM.get("ZYPL"), SFXM.get("MCSX") + "");
									response.put("XMJE"+SFXM.get("ZYPL"), SFXM.get("ZJJE") + "");
								}
//							}else{
//								response.put("XMJE"+SFXM.get("ZYPL"), "0.00");
							}
						}
					} else if(SFXM.get("ZJJE")!=null&&(SFXM.get("ZJJE")+"").length()>0){
						if(Double.parseDouble(SFXM.get("ZJJE")+"")>0){
							if (response.containsKey("QTJE")) {
								response.put(
										"QTJE",
										(Double.parseDouble(response.get("QTJE") + "") + Double
												.parseDouble(SFXM.get("ZJJE") + ""))
												+ "");
							} else {
								response.put("QTJE", SFXM.get("ZJJE") + "");
							}
						}
					}
//					if ("1".equals(SFXM.get("ZYPL") + "")) {						
//						response.put("XYF", "西药费 "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));						
//					} else if ("2".equals(SFXM.get("ZYPL") + "")) {
//						response.put("ZCY", "中成药 "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("3".equals(SFXM.get("ZYPL") + "")) {
//						response.put("ZCAOY", "中草药  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("4".equals(SFXM.get("ZYPL") + "")) {
//						response.put("CWF", "床位费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("5".equals(SFXM.get("ZYPL") + "")) {
//						response.put("ZCF", "诊查费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("6".equals(SFXM.get("ZYPL") + "")) {
//						response.put("JCF", "检查费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("7".equals(SFXM.get("ZYPL") + "")) {
//						response.put("JYF", "检验费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("8".equals(SFXM.get("ZYPL") + "")) {
//						response.put("ZLF", "诊疗费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("9".equals(SFXM.get("ZYPL") + "")) {
//						response.put("SSF", "手术费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("10".equals(SFXM.get("ZYPL") + "")) {
//						response.put("SXF", "输血费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("11".equals(SFXM.get("ZYPL") + "")) {
//						response.put("HLF", "化疗费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("12".equals(SFXM.get("ZYPL") + "")) {
//						response.put("CLF", "磁疗费  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//					} else if ("13".equals(SFXM.get("ZYPL") + "")) {
//						response.put("QT", "其它  "+String.format("%1$.2f", parseDouble(SFXM.get("ZJJE"))));
//						
//					} 
				}
				if (response.containsKey("QTJE")) {
					if (response.containsKey("XMJE"+response.get("QTPL"))&&(response.get("XMJE"+response.get("QTPL"))+"").length()>0) {
						response.put("XMJE"+response.get("QTPL"), Double.parseDouble(response.get("XMJE"+response.get("QTPL"))+"")+Double.parseDouble(response.get("QTJE")+""));
					}else{
						response.put("XMJE"+response.get("QTPL"), Double.parseDouble(response.get("QTJE")+""));
					}
				}else{
					if(response.containsKey("SFXM" + response.get("QTPL"))){
						response.remove("SFXM" + response.get("QTPL"));
						response.remove("XMJE" + response.get("QTPL"));
					}
				}
				if("1".equals(request.get("flag")))
				{
					response.put("BD","补打");///add zhangch 
				}
				double jkje = 0.00;
				if (JKJES.get("JKJE") != null) {
					jkje = Double.parseDouble(JKJES.get("JKJE") + "");
				}
				response.put("ZYYJJ", String.format("%1$.2f", jkje));
				//年月日
				response.put("N", JSXX.get("YYYY") + "");
				response.put("Y", JSXX.get("MM") + "");
				response.put("R", JSXX.get("DD") + "");
				response.put("ZYHM", JSXX.get("XLH") + "");//家床号码
				response.put("BRXM", JSXX.get("XM") + "");//姓名
				response.put("RYLB", JSXX.get("RYLB") + "");//人员类别
				response.put("SYY", JSXX.get("SYY") + "");//收银员
				if (JSXX.get("RYMM") != null) {
					response.put("FM", JSXX.get("RYMM") + "");
				}
				if (JSXX.get("RYDD") != null) {
					response.put("FD", JSXX.get("RYDD") + "");
				}
				if (JSXX.get("CYMM") != null) {
					response.put("TM", JSXX.get("CYMM") + "");
				}
				if (JSXX.get("CYDD") != null) {
					response.put("TD", JSXX.get("CYDD") + "");
				}
				int days = 0;
				if (JSXX.get("RYRQ") != null && JSXX.get("CYRQ") != null) {
					days = BSHISUtil.getDifferDays(
							sdftime.parse((JSXX.get("CYRQ") + "").substring(0, 10)+" 00:00:00"),
							sdftime.parse((JSXX.get("RYRQ") + "").substring(0, 10)+" 00:00:00"));
				}
				response.put("DAYS", days + "");
				
				String zYRQ = "";
				if(JSXX.get("CYRQ1")!=null){
					zYRQ = JSXX.get("RYRQ1")+"-"+JSXX.get("CYRQ1")+"("+days+"天)";
				} else {
					if(JSXX.get("RYRQ") != null){
						days = BSHISUtil.getDifferDays(
								sdftime.parse((sdftime.format(new Date())).substring(0, 10)+" 00:00:00"),
								sdftime.parse((JSXX.get("RYRQ") + "").substring(0, 10)+" 00:00:00"));
					}
					
					zYRQ = JSXX.get("RYRQ1")+"-"+"至今("+days+"天)";
				}				
				response.put("ZYRQ", zYRQ + "");//家床日期
				
				double hjje = 0.00;
				if (JSXX.get("HJJE") != null) {
					hjje = Double.parseDouble(JSXX.get("HJJE") + "");
				}
				response.put("FYHJ", String.format("%1$.2f", hjje));//费用合计
				double zfje = parseDouble(JSXX.get("ZFJE"));//自费金额(自理自费)
				response.put("ZFJE", String.format("%1$.2f", zfje));
				response.put("HJDX", numberToRMB(hjje));
				double jkhj = Double.parseDouble(parseDouble(JSXX.get("JKHJ")) + "");//预缴款
				response.put("JKHJ", String.format("%1$.2f", jkhj));
				
				if(zfje-jkhj>=0){
					response.put("CYBJ", String.format("%1$.2f", (zfje-jkhj)));//补缴
					response.put("BJXJ",String.format("%1$.2f", (zfje-jkhj)));//补缴现金
					response.put("CYTK","0.00");//撤床退款
					response.put("TKXJ", "0.00");//退款现金
					
				} else {
					response.put("CYBJ", "0.00");
					response.put("JSMXXJ","0,00");
					response.put("CYTK",String.format("%1$.2f", -(zfje-jkhj)));//撤床退款
					StringBuffer hqlTKXJ = new StringBuffer(
							"select -sum(a.FKJE) as TKXJ from JC_FKXX a,JC_JCJS b,GY_FKFS c where a.ZYH=b.ZYH and a.JSCS=b.JSCS and a.FKFS=c.FKFS and c.HBWC=0  and a.ZYH = :ZYH and a.JSCS = :JSCS");
					Map<String,Object> parametersTKXJ =new HashMap<String,Object>();
					parametersTKXJ.put("ZYH", JSXX.get("ZYH"));
					parametersTKXJ.put("JSCS", JSXX.get("JSCS"));
					Map<String,Object> TKXJ_map = dao.doLoad(hqlTKXJ.toString(), parametersTKXJ);
					response.put("TKXJ",String.format("%1$.2f", TKXJ_map.get("TKXJ")));//退款现金
				}
				
				
				//医保相关
				String SHIYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHIYB", "0", "市医保病人性质", ctx);
				String SHENGYB = ParameterUtil.getParameter(
						ParameterUtil.getTopUnitId(), "SHENGYB", "0", "省医保病人性质", ctx);
				//此处判断是否为医保用户 JSXX.get("BRXZ")
				if(JSXX.get("BRXZ").toString().equals(SHIYB)){
					parameters1.remove("JSCS");
					parameters1.put("FPHM", fphm);
					parameters1.put("JGID", JGID);
					List<Map<String,Object>> list = dao.doQuery(hql4.toString(), parameters1);
					if(list!=null && list.size()>0){
						YBJES = list.get(0);
					}
					
				} else if(JSXX.get("BRXZ").toString().equals(SHENGYB)){
					
				}
				if(YBJES.size()>0 && YBJES!=null){
					String ybCard = YBJES.get("CARDNO") + "";
					response.put("FPHM",fphm+" 医保卡号："+ybCard);
					double bnzhzf = parseDouble(YBJES.get("BNZHZF"));
					response.put("BNZHZF",String.format("%1$.2f", bnzhzf) );//本年账户
					double lnzhzf = parseDouble(YBJES.get("LNZHZF"));
					response.put("LNZHZF",String.format("%1$.2f", lnzhzf) );//历年账户
					double tczf = parseDouble(YBJES.get("TCZF"));
					double lxjj = parseDouble(YBJES.get("LXJJ"));
					double lfjj = parseDouble(YBJES.get("LFJJ"));
					double zntcjj = parseDouble(YBJES.get("ZNTCJJ"));
					double lxjsjj = parseDouble(YBJES.get("LXJSJJ"));
					double snetjj = parseDouble(YBJES.get("SNETJJ"));
					double lnjmjj = parseDouble(YBJES.get("LNJMJJ"));
					double gwjjzf = parseDouble(YBJES.get("GWJJZF"));
					double nmgjj = parseDouble(YBJES.get("NMGJJ"));
					double gfkzjj = parseDouble(YBJES.get("GFKZJJ"));
					double gfjfjj = parseDouble(YBJES.get("GFJFJJ"));
					double lfkzjj = parseDouble(YBJES.get("LFKZJJ"));
					double lfjfjj = parseDouble(YBJES.get("LFJFJJ"));
					double cqznjj = parseDouble(YBJES.get("CQZNJJ"));
					double cqgfjj = parseDouble(YBJES.get("CQGFJJ"));
					double cqlfjj = parseDouble(YBJES.get("CQLFJJ"));
					double xnhjj = parseDouble(YBJES.get("XNHJJ"));
					double dxsjj = parseDouble(YBJES.get("DXSJJ"));
					double jzjzf = parseDouble(YBJES.get("JZJZF"));
					double knjzjj = parseDouble(YBJES.get("KNJZJJ"));
					double lmjj = parseDouble(YBJES.get("LMJJ"));
					double grxjzf = parseDouble(YBJES.get("GRXJZF"));
					double ylypzl = parseDouble(YBJES.get("YLYPZL"));
					double grzfje  = parseDouble(YBJES.get("GRZFJE"));
					double ybzh = tczf + lxjsjj + lfjj + zntcjj + snetjj + lnjmjj + gwjjzf + nmgjj + gfkzjj
							+ gfjfjj + lfkzjj + lfjfjj + cqznjj + cqgfjj + cqlfjj + xnhjj + dxsjj + jzjzf + knjzjj + lmjj; //医保账户
					response.put("YBZH",String.format("%1$.2f", ybzh));
					double ybhj = hjje-zfje;
					response.put("YBHJ",String.format("%1$.2f", ybhj));
					
					//此处判断为市医保还是省医保 JSXX.get("BRXZ")
					
					if(JSXX.get("BRXZ").toString().equals(SHIYB)){//市医保
						StringBuffer bz = new StringBuffer("");
						bz.append("       本年账户支付:" + String.format("%1$.2f", bnzhzf));
						bz.append(" 本年账户支付:" + String.format("%1$.2f", lnzhzf));
						double lnzhzl = parseDouble(YBJES.get("LNZHZL"));
						bz.append("（其中自理:" + String.format("%1$.2f", lnzhzl));
						bz.append(" 自负:" + String.format("%1$.2f", (lnzhzf-lnzhzl)) + "）");						
						double tcjj = tczf + lxjsjj + lfjj + zntcjj + snetjj + lnjmjj + gwjjzf + nmgjj + gfjfjj + lfkzjj + cqznjj + cqgfjj + cqlfjj + xnhjj + dxsjj + lmjj;
						bz.append("统筹基金：" + String.format("%1$.2f", tcjj));
						bz.append(" 重病补助：" + String.format("%1$.2f", jzjzf));
						bz.append(" 困难补助基金支付:" + String.format("%1$.2f", knjzjj));
						double sczl = parseDouble(YBJES.get("SCZL"));
						double sczf = parseDouble(YBJES.get("SCZF"));
						if(sczl>0 || sczf>0){
							bz.append(" 其中伤残基金支付自负：" + String.format("%1$.2f", sczf));
							bz.append(" 其中伤残基金支付自理：" + String.format("%1$.2f", sczl));
						}
						//double grxjzf = parseDouble(YBJES.get("GRXJZF"));
						bz.append("现金支付：" + String.format("%1$.2f", grxjzf));
						
						double tjzl = parseDouble(YBJES.get("TJZL"));
						double tzzl = parseDouble(YBJES.get("TZZL"));
						
						double zfzl = ylypzl + tjzl + tzzl + grzfje - lnzhzl -sczl;
						bz.append("（自费自理：" + String.format("%1$.2f", zfzl));
						double ld_temp = grxjzf - (ylypzl + tjzl + tzzl - lnzhzl) -  grzfje;
						if(ld_temp>0) {
							bz.append("自负：" + ld_temp + "）");
						} else {
							bz.append("）");
						}
						response.put("BZ",bz.toString());//市医保备注						
					} else {//省医保备注
						StringBuffer bz = new StringBuffer("");
						bz.append("       本年账户支付:" + String.format("%1$.2f", bnzhzf));
						bz.append(" 历年账户支付:" + String.format("%1$.2f", lnzhzf));
						bz.append(" 统筹基金:" + String.format("%1$.2f", tczf));
						bz.append(" 公务基金支付:" + String.format("%1$.2f", gwjjzf));
						bz.append(" 重病补助:" + String.format("%1$.2f", jzjzf));
						bz.append(" 现金支付:" + String.format("%1$.2f", grxjzf));
						bz.append("(自理自费:"+String.format("%1$.2f", ylypzl+grzfje));
						double ld_temp = grxjzf - ylypzl - grzfje;
						if(ld_temp>0){
							bz.append("自负：" + String.format("%1$.2f", ld_temp) + ")");
						} else {
							bz.append(")");
						}
						response.put("BZ", bz.toString());
					}					
				} else {
					response.put("FPHM",fphm);//医疗卡号					
					response.put("BNZHZF","0.00");
					response.put("LNZHZF","0.00");
					response.put("YBZH", "0.00");
					response.put("YBHJ", String.format("%1$.2f", hjje-zfje)); //医保合计
					response.put("BZ", "");
				}
			}
			response.put(BSPHISSystemArgument.FPYL,ParameterUtil.getParameter(JGID, BSPHISSystemArgument.FPYL, ctx));
			response.put(BSPHISSystemArgument.JCJSDYJMC,ParameterUtil.getParameter(JGID, BSPHISSystemArgument.JCJSDYJMC, ctx));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String numberToZH4(String s) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		String[] zhnum1_0 = { "", "拾", "佰", "仟" };
		StringBuilder sb = new StringBuilder();
		if (s.length() != 4)
			return null;
		for (int i = 0; i < 4; i++) {
			char c1 = s.charAt(i);
			if (c1 == '0' && i > 1 && s.charAt(i - 1) == '0')
				continue;
			if (c1 != '0' && i > 1 && s.charAt(i - 1) == '0')
				sb.append('零');
			if (c1 != '0') {
				sb.append(zhnum_0.charAt(c1 - 48));
				sb.append(zhnum1_0[4 - i - 1]);
			}
		}
		return new String(sb);
	}

	public static String numberToZH(long n) {
		String[] zhnum2 = { "", "万", "亿", "万亿", "亿亿" };
		StringBuilder sb = new StringBuilder();
		String strN = "000" + n;
		int strN_L = strN.length() / 4;
		strN = strN.substring(strN.length() - strN_L * 4);
		for (int i = 0; i < strN_L; i++) {
			String s1 = strN.substring(i * 4, i * 4 + 4);
			String s2 = numberToZH4(s1);
			sb.append(s2);
			if (s2.length() != 0)
				sb.append(zhnum2[strN_L - i - 1]);
		}
		String s = new String(sb);
		if (s.length() != 0 && s.startsWith("零"))
			s = s.substring(1);
		return s;
	}

	public static String numberToZH(double d) {
		return numberToZH("" + d);
	}

	/**
	 * Description: 数字转化成整数
	 * 
	 * @param str
	 * @param fan
	 * @return
	 */
	public static String numberToZH(String str) {
		String zhnum_0 = "零壹贰叁肆伍陆柒捌玖";
		StringBuilder sb = new StringBuilder();
		int dot = str.indexOf(".");
		if (dot < 0)
			dot = str.length();

		String zhengshu = str.substring(0, dot);
		sb.append(numberToZH(Long.parseLong(zhengshu)));
		if (dot != str.length()) {
			sb.append("点");
			String xiaoshu = str.substring(dot + 1);
			for (int i = 0; i < xiaoshu.length(); i++) {
				sb.append(zhnum_0.charAt(Integer.parseInt(xiaoshu.substring(i,
						i + 1))));
			}
		}
		String s = new String(sb);
		if (s.startsWith("零"))
			s = s.substring(1);
		if (s.startsWith("一十"))
			s = s.substring(1);
		while (s.endsWith("零")) {
			s = s.substring(0, s.length() - 1);
		}
		if (s.endsWith("点"))
			s = s.substring(0, s.length() - 1);
		return s;
	}

	public String numberToRMB(double rmb) {
		String strRMB = "" + rmb;
		DecimalFormat nf = new DecimalFormat("#.#");
		nf.setMaximumFractionDigits(2);
		strRMB = nf.format(rmb).toString();
		strRMB = numberToZH(strRMB);
		if (strRMB.indexOf("点") >= 0) {
			strRMB = strRMB + "零";
			strRMB = strRMB.replaceAll("点", "圆");
			String s1 = strRMB.substring(0, strRMB.indexOf("圆") + 1);
			String s2 = strRMB.substring(strRMB.indexOf("圆") + 1);
			strRMB = s1 + s2.charAt(0) + "角" + s2.charAt(1) + "分整";
		} else {
			strRMB = strRMB + "圆整";
		}
		return strRMB;
	}

	/**
	 * 空转换double
	 */
	public double parseDouble(Object o) {
		if (o == null) {
			return new Double(0.00);
		}
		return Double.parseDouble(o + "");
	}
}
