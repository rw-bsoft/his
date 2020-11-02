package phis.application.fsb.source;

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
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class FamilySickBedPatientManagementModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public FamilySickBedPatientManagementModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 病人管理form回填
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doLoadBrxx(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.JC_BRRY
					+ "_RYDJ", pkey);
			if(BRXX.get("MZZD")!=null&&Long.parseLong(BRXX.get("MZZD")+"")!=0){
				BRXX.put("JBMC", dao.doLoad("YB_JBBM", BRXX.get("MZZD")).get("JBMC"));
			}
//			if (BRXX.get("CSNY") != null) {
//				BSPHISUtil.getPersonAge((Date) BRXX.get("CSNY"), null);
//				BRXX.put("RYNL",
//						BSPHISUtil.getPersonAge((Date) BRXX.get("CSNY"), null)
//								.get("ages"));
//			}
			res.put("body", BRXX);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 病人注销
	 * 
	 * @param req
	 * @param ctx
	 */
	public void doUpdateCanceled(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Long pkey = Long.parseLong(req.get("pkey") + "");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.JC_BRRY,
					pkey);
			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
				res.put("body", "病人已通知出院，不能注销!");
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", pkey);
			long count = dao.doCount("JC_TBKK", "ZYH = :ZYH",
					parameters);
			if (count > 0) {
				Map<String, Object> JKJE = dao.doLoad(
						"SELECT sum(JKJE) as JKJE FROM "
								+ "JC_TBKK"
								+ " WHERE ZYH = :ZYH AND ZFPB = 0", parameters);
				if (JKJE.get("JKJE") != null) {
					if (Double.parseDouble(JKJE.get("JKJE") + "") != 0) {
						res.put("body", "此病人已有缴款发生，不能进行注销操作!");
						return;
					}
				}
			}
			count = dao
					.doCount(
							"JC_BRYZ",
							"( ZYH = :ZYH ) AND ( LSBZ = 0 OR (LSBZ= 2 AND QRSJ IS NULL) OR SYBZ = 1 ) AND ( JFBZ < 3 OR JFBZ = 9 )",
							parameters);
			if (count > 0) {
				res.put("body", "病人有未停未发医嘱，不能进行注销操作!");
				return;
			}
			Map<String, Object> ZY_FYMX = dao.doLoad(
					"SELECT COUNT(*) as COUNT,sum(ZJJE) as ZJJE FROM "
							+ "JC_FYMX" + " WHERE ZYH = :ZYH",
					parameters);
			count = Long.parseLong(ZY_FYMX.get("COUNT") + "");
			double zjje = 0;
			if (ZY_FYMX.get("ZJJE") != null) {
				zjje = Double.parseDouble(ZY_FYMX.get("ZJJE") + "");
			}
			if (count > 0) {
				if (zjje != 0) {
					res.put("body", "此病人已有费用发生，不能进行注销操作!");
					return;
				}
			}
//			count = dao.doCount("JC_RCJL",
//					"ZYH = :ZYH and CZLX=1 AND BQPB=0", parameters);
//			// 如果已有临床入院，则需要进行入院注销处理
//			if (count > 0) {
//				Map<String, Object> uf_ryrq_setparameters = new HashMap<String, Object>();
//				uf_ryrq_setparameters.put("ZYH", pkey);
//				uf_ryrq_setparameters.put("RYRQ", null);
//				BSPHISUtil.uf_ryrq_set(uf_ryrq_setparameters, dao, ctx);
//			}
//			count = dao.doCount("JC_CWSZ", "ZYH = :ZYH",
//					parameters);
//			if (count > 0) {
//				dao.doUpdate("UPDATE " + "JC_CWSZ"
//						+ " SET ZYH = NULL WHERE ZYH = :ZYH", parameters);
//			}
			parameters.put("CYRQ", new Date());
			StringBuffer hql=new StringBuffer();
			hql.append("UPDATE ").append("JC_BRRY").append(" SET CYPB = 99 , CYRQ = :CYRQ ");
			if(req.containsKey("isYb")){//如果是医保,则更新SRKH和ZYLSH
				hql.append(",SRKH=null ,ZYLSH=null");
			}
			hql.append("  WHERE ZYH = :ZYH");
			dao.doUpdate(hql.toString(),parameters);
//			if("6089".equals(BRXX.get("BRXZ")+"")){
//				JXMedicareModel jxmm = new JXMedicareModel(dao);
//				jxmm.saveTransferProperties(BRXX, ctx);
//			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	/**
	 * 病人性质转换
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateTransform(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		long pkey = Long.parseLong(req.get("pkey") + "");
		long brxz = Long.parseLong(req.get("brxz") + "");
		//Map<String, Object> YBXX = (Map<String, Object>)req.get("ybxx");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.JC_BRRY,
					pkey);
			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
				res.put("body", "病人已通知出院，不能转换!");
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ZYH", pkey);
			parameters.put("JGID", manaUnitId);
			List<Map<String, Object>> list_JC_FYMX = dao
					.doQuery(
							"SELECT JLXH as JLXH,ZYH as ZYH,FYRQ as FYRQ,FYXH as FYXH,FYMC as FYMC,YPCD as YPCD,FYSL as FYSL,FYDJ as FYDJ,ZJJE as ZJJE,ZFJE as ZFJE,ZLJE as ZLJE,YSGH as YSGH,SRGH as SRGH,QRGH as QRGH,FYBQ as FYBQ,FYKS as FYKS,ZXKS as ZXKS,JFRQ as JFRQ,XMLX as XMLX,YPLX as YPLX,FYXM as FYXM,JSCS as JSCS,ZFBL as ZFBL,YZXH as YZXH,HZRQ as HZRQ,YJRQ as YJRQ,ZLXZ as ZLXZ,JGID as JGID FROM "
									+ "JC_FYMX"
									+ " WHERE ( ZYH = :ZYH ) AND ( JSCS = 0 ) AND  ( JGID = :JGID )",
							parameters);
			list_JC_FYMX = BSPHISUtil.change(
					Long.parseLong(BRXX.get("BRXZ") + ""), brxz, list_JC_FYMX,
					dao, ctx);
			parameters.remove("JGID");
			parameters.put("BRXZ", brxz);
			//parameters.put("MZZD", Long.parseLong(YBXX.get("MZZD")==null?"0":YBXX.get("MZZD")+""));//门诊诊断
			StringBuffer hql=new StringBuffer();
			hql.append("update ").append("JC_BRRY").append(" set BRXZ = :BRXZ ");
//			dao.doUpdate("update " + BSPHISEntryNames.ZY_BRRY
//					+ " set BRXZ = :BRXZ where ZYH = :ZYH", parameters);
			//医保
			if(req.containsKey("YBGX")){//如果是医保
				Map<String,Object> YBGX=(Map<String,Object>)req.get("YBGX");
				if("1".equals(YBGX.get("TAG"))){//自费转市医保
					hql.append(",ZYLSH=:ZYLSH,SRKH=:SRKH,YYLSH=:YYLSH");
					parameters.put("ZYLSH", YBGX.get("ZYLSH")+"");
					parameters.put("SRKH", YBGX.get("SRKH")+"");
					parameters.put("YYLSH", YBGX.get("YYLSH")+"");
					parameters.put("GRBH", YBGX.get("GRBH")+"");
				}else if("2".equals(YBGX.get("TAG"))){//市医保转自费
					hql.append(",ZYLSH=null,SRKH=null,YYLSH=null,GRBH=null");
				}
			}
			hql.append(" where ZYH = :ZYH");
			dao.doUpdate(hql.toString(), parameters);
//			if(brxz==6089){
//				JXMedicareModel jxmm = new JXMedicareModel(dao);
//				//Map<String,Object> ZYLSHMap = new HashMap<String, Object>();
//				YBXX.put("ZYH", pkey);
//				Map<String,Object> ZYLSH = jxmm.saveTransferProperties(YBXX, ctx);
//				ZYLSH.put("BRXZ", brxz);
//				ZYLSH.put("ZYH", pkey);
//				ZYLSH.put("YBKH", YBXX.get("YBKH"));//医保卡号
//				ZYLSH.put("YBYE", YBXX.get("YBYE"));//医保卡余额
//				ZYLSH.put("MZZD", YBXX.get("MZZD"));//门诊诊断
//				dao.doSave("update", BSPHISEntryNames.ZY_BRRY + "_RYDJ", ZYLSH, false);
//			}else{
//				Map<String,Object> ZY_BRRY = dao.doLoad(BSPHISEntryNames.ZY_BRRY+"_RYDJ", pkey);
//				if(ZY_BRRY.get("YWLSH")!=null){
//					JXMedicareModel jxmm = new JXMedicareModel(dao);
//					jxmm.saveTransferProperties(ZY_BRRY, ctx);
////					ZY_BRRY.put("YBKH", null);
////					ZY_BRRY.put("YBYE", null);
////					ZY_BRRY.put("MZZD", null);
////					ZY_BRRY.put("YWLSH", null);
//					dao.doUpdate("UPDATE ZY_BRRY set YBKH=null,YWLSH=null where ZYH="+ZY_BRRY.get("ZYH"), null);
//				}
//			}
			for (int i = 0; i < list_JC_FYMX.size(); i++) {
				Map<String, Object> record = list_JC_FYMX.get(i);
				dao.doSave("update", BSPHISEntryNames.JC_FYMX, record, false);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人性质修改失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "修改费用明细失败");
		}
	}

	/**
	 * 病人管理列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPatientList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String al_jgid = user.getManageUnit().getId();
		int pageSize = Integer.parseInt(req.get("pageSize") + "");
		int pageNo = Integer.parseInt(req.get("pageNo") + "");
		List<Object> cnd = (List<Object>) req.get("cnd");
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("al_jgid", al_jgid);
			StringBuffer totalSql = new StringBuffer(
					"SELECT a.ZYH as ZYH FROM ");
			totalSql.append("JC_BRRY");
			totalSql.append(" a WHERE a.CYPB < 8 and a.JGID = :al_jgid ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + new ExpressionProcessor().toString(cnd);
					where = where.replace("str", "to_char");
					totalSql.append(where);
				}
			}
//			totalSql.append(" UNION ALL SELECT a.ZYH as ZYH FROM ");
//			totalSql.append("JC_BRRY");
//			totalSql.append(" a WHERE a.CYPB < 8 AND a.JGID = :al_jgid and NOT EXISTS ( SELECT 1 FROM ");
//			totalSql.append("JC_CWSZ");
//			totalSql.append(" b WHERE a.ZYH = b.ZYH )");
//			if (cnd != null) {
//				if (cnd.size() > 0) {
//					String where = " and " + ExpRunner.toString(cnd, ctx);
//					where = where.replace("str", "to_char");
//					totalSql.append(where);
//				}
//			}
			String JCZZLJTS = ParameterUtil.getParameter(al_jgid, "JCZZLJTS", ctx);
			StringBuffer sql = new StringBuffer(
			"SELECT a.ZYH as ZYH,a.JCBH as JCBH,a.RYNL as RYNL,a.ZYHM as ZYHM,a.BRXM as BRXM,a.KSRQ as KSRQ,a.CSNY as CSNY,a.BRXB as BRXB,a.SFZH as SFZH,a.BRXZ as BRXZ,a.RYRQ as RYRQ,a.CYPB as CYPB,ZRYS as ZRYS,ZRHS as ZRHS,LXDZ as LXDZ,JSRQ as JSRQ,"+JCZZLJTS+" as LJTS,JSRQ-sysdate as XCTS,SQFS as SQFS,a.JCZD as JCZD,a.JCLX as JCLX,a.JSRQ-a.KSRQ+1 as JCTS FROM ");
			sql.append("JC_BRRY");
			sql.append(" a WHERE a.CYPB < 8 and a.JGID = :al_jgid ");
			if (cnd != null) {
				if (cnd.size() > 0) {
					String where = " and " + new ExpressionProcessor().toString(cnd);
					where = where.replace("str", "to_char");
					sql.append(where);
				}
			}
			sql.append(" order by XCTS");
			List<Map<String, Object>> listTotal = dao.doSqlQuery(
					totalSql.toString(), parameters);
			res.put("totalCount", 0);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			if (listTotal != null) {
				int total = listTotal.size();
				res.put("totalCount", total);
				if (total > 0) {
					parameters.put("first", (pageNo - 1) * pageSize);
					parameters.put("max", pageSize);
					List<Map<String, Object>> listBRLB = dao.doSqlQuery(
							sql.toString(), parameters);
					for (int i = 0; i < listBRLB.size(); i++) {
						Map<String,Object> BRXX = listBRLB.get(i);
						if (BRXX.get("CSNY") != null) {
							BRXX.put("RYNL",
									BSPHISUtil.getPersonAge((Date) BRXX.get("CSNY"), (Date)BRXX.get("KSRQ"))
											.get("ages"));
							listBRLB.set(i, BRXX);
						}
						
//						int Ll_cypb = Integer.parseInt(listBRLB.get(i).get("CYPB")+"");
//						long Ll_zyh = Long.parseLong(listBRLB.get(i).get("ZYH")+"");
//						if (Ll_cypb == 1) {
//							continue;
//						}
//						Map<String, Object> ZY_RCJLparameters = new HashMap<String, Object>();
//						ZY_RCJLparameters.put("gl_jgid", al_jgid);
//						ZY_RCJLparameters.put("ll_zyh", Ll_zyh);
//						long ll_count = dao.doCount("JC_RCJL",
//								"JGID = :gl_jgid and ZYH=:ll_zyh and CZLX=-1",
//								ZY_RCJLparameters);
//						if (ll_count > 0) {
//							listBRLB.get(i).put("CYPB", 102);
//						}
					}
					SchemaUtil.setDictionaryMassageForList(listBRLB,
							"phis.application.fsb.schemas.JC_BRRY" + "_BRGL_LIST");
					res.put("body", listBRLB);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人列表失败");
		} catch (ExpException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取病人列表失败！");
		}

	}

	/**
	 * 病人信息修改
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doUpdateBRRY(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> BRXX = dao.doLoad(BSPHISEntryNames.JC_BRRY,
					body.get("ZYH"));
			if (1l == Long.parseLong(BRXX.get("CYPB") + "")) {
				res.put("body", "病人已通知出院，不能修改!");
				return;
			}
			Map<String, Object> ZY_BRRY = new HashMap<String, Object>();
			ZY_BRRY.put("ZYH", body.get("ZYH"));
			ZY_BRRY.put("LXRM", body.get("LXRM"));
			ZY_BRRY.put("LXGX", body.get("LXGX"));
			ZY_BRRY.put("LXDH", body.get("LXDH"));
			ZY_BRRY.put("JCZD", body.get("JCZD"));
			ZY_BRRY.put("ICD10", body.get("ICD10"));
			ZY_BRRY.put("ZDRQ", body.get("ZDRQ"));
			ZY_BRRY.put("BQZY", body.get("BQZY"));
			ZY_BRRY.put("JCYJ", body.get("JCYJ"));
			ZY_BRRY.put("JCLX", body.get("JCLX"));
			ZY_BRRY.put("KSRQ", body.get("KSRQ"));
			ZY_BRRY.put("JSRQ", body.get("JSRQ"));
			ZY_BRRY.put("ZRYS", body.get("ZRYS"));
			ZY_BRRY.put("ZRHS", body.get("ZRHS"));
			dao.doSave("update", BSPHISEntryNames.JC_BRRY + "_RYDJ", ZY_BRRY,
					false);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息修改失败");
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息修改失败");
		}
	}
}
