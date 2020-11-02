package phis.application.hos.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class HospitalPaymentProcessingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPaymentProcessingModel.class);

	public HospitalPaymentProcessingModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取病人信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBrxx(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", manaUnitId);
		try {
			String SJHM = BSPHISUtil.Getbillnumber("收据", dao, ctx);
			if("false".equals(SJHM)){
				SJHM = "";
				res.put("msg", "您的收据已用完或尚未申领，不能进行缴款处理!");
			}
			if ("BRCH".equals(body.get("key"))) {
				parameters.put("BRCH", body.get("value"));
				Map<String, Object> ZY_BRRY = dao
						.doLoad("select ZYH as ZYH,ZYHM as ZYHM,BRCH as BRCH,BRXM as BRXM,BRKS as BRKS,BRXZ as BRXZ,RYRQ as RYRQ,BRID as BRID,BRXB as BRXB,CSNY as CSNY,SFZH as SFZH from ZY_BRRY where CYPB < 8 AND JGID = :JGID AND BRCH = :BRCH",
								parameters);
				if (ZY_BRRY != null) {
					Map<String, Object> BRXX = new HashMap<String, Object>();
					BRXX.put("JSLX", 0);
					BRXX.put("ZYH", ZY_BRRY.get("ZYH"));
					BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, dao, ctx);
					double JKHJ = Double.parseDouble(BRXX.get("JKHJ") + "");
					ZY_BRRY.put("JKHJ", JKHJ);
					BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, dao, ctx);
					double ZFHJ = Double.parseDouble(BRXX.get("ZFHJ") + "");
					ZY_BRRY.put("ZFHJ", ZFHJ);
					double SYHJ = BSPHISUtil.getDouble(JKHJ - ZFHJ, 2);
					ZY_BRRY.put("SYHJ", SYHJ);
					ZY_BRRY.put("SJHM", SJHM);
					SimpleDateFormat sdfdatetime = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					ZY_BRRY.put("JKRQ", sdfdatetime.format(new Date()));
					res.put("body", ZY_BRRY);
				}
			} else {
				String ZYHM = BSPHISUtil.get_public_fillleft(body.get("value")+"","0",BSPHISUtil.getRydjNo(manaUnitId, "ZYHM", "",dao).length());
				parameters.put("ZYHM",ZYHM);
				Map<String, Object> ZY_BRRY = dao
						.doLoad("select ZYH as ZYH,ZYHM as ZYHM,BRCH as BRCH,BRXM as BRXM,BRKS as BRKS,BRXZ as BRXZ,RYRQ as RYRQ,BRID as BRID,BRXB as BRXB,CSNY as CSNY,SFZH as SFZH from ZY_BRRY where CYPB < 8 AND JGID = :JGID AND ZYHM = :ZYHM",
								parameters);
				if (ZY_BRRY != null) {
					Map<String, Object> BRXX = new HashMap<String, Object>();
					BRXX.put("JSLX", 0);
					BRXX.put("ZYH", ZY_BRRY.get("ZYH"));
					BRXX = BSPHISUtil.gf_zy_gxmk_getjkhj(BRXX, dao, ctx);
					double JKHJ = Double.parseDouble(BRXX.get("JKHJ") + "");
					ZY_BRRY.put("JKHJ", JKHJ);
					BRXX = BSPHISUtil.gf_zy_gxmk_getzfhj(BRXX, dao, ctx);
					double ZFHJ = Double.parseDouble(BRXX.get("ZFHJ") + "");
					ZY_BRRY.put("ZFHJ", ZFHJ);
					double SYHJ = BSPHISUtil.getDouble(JKHJ - ZFHJ, 2);
					ZY_BRRY.put("SYHJ", SYHJ);
					ZY_BRRY.put("SJHM", SJHM);
					SimpleDateFormat sdfdatetime = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					ZY_BRRY.put("JKRQ", sdfdatetime.format(new Date()));
					res.put("body", ZY_BRRY);
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}
	public static void main(String[] args) {
		System.out.println(new Date());
	}
	/**
	 * 缴款保存
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSavePayment(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		
		Map<String, Object> ZY_TBKK = (Map<String, Object>) req.get("body");
		String sjhm = BSPHISUtil.Getbillnumber("收据", dao, ctx);
		if (!"false".equals(sjhm)) {
			if (!sjhm.equals(ZY_TBKK.get("SJHM"))) {
				ZY_TBKK.put("SJHM", sjhm);
			}
		} else {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR,
					"您的收据已用完或尚未申领，不能进行缴款处理!");
		}
		ZY_TBKK.put("JGID", manaUnitId);
		ZY_TBKK.put("JSCS", 0);
		ZY_TBKK.put("ZFPB", 0);
		ZY_TBKK.put("ZCPB", 0);
		ZY_TBKK.put("JKRQ",BSHISUtil.toDate(ZY_TBKK.get("JKRQ")+""));
		try {
			if(BSPHISUtil.SetBillNumber("收据", sjhm, dao, ctx)){
				res.put("body", dao.doSave("create", BSPHISEntryNames.ZY_TBKK, ZY_TBKK, false));
			}else {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "更新收据号码失败!");
			}
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "缴款保存失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "缴款保存失败");
		}

	}

	/**
	 * 缴款注销
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 */
	public void doUpdatePayment(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = (String) user.getUserId();
		String manaUnitId = user.getManageUnit().getId();
		long pkey = Long.parseLong(req.get("pkey") + "");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZFRQ", new Date());
		parameters.put("ZFGH", userId);
		parameters.put("JKXH", pkey);
		parameters.put("JGID", manaUnitId);
		try {
			dao.doUpdate(
					"UPDATE ZY_TBKK SET ZFPB = 1, ZFRQ = :ZFRQ, ZFGH = :ZFGH  Where JKXH = :JKXH And JGID = :JGID",
					parameters);
			//Map<String, Object> ZY_JKZF = new HashMap<String, Object>();
			//ZY_JKZF.put("JKXH", pkey);
			dao.doSave("create", BSPHISEntryNames.ZY_JKZF, parameters, false);
			parameters.remove("ZFRQ");
			parameters.remove("ZFGH");
			Map<String, Object> zy_jkxx = dao.doSqlLoad("select b.BRID AS BRID,a.JGID AS JGID,a.SJHM AS SJHM,a.JKFS AS JKFS from ZY_TBKK a,ZY_BRRY b where a.ZYH=b.ZYH and a.JGID=b.JGID and a.JKXH = :JKXH And a.JGID = :JGID", parameters);
			res.put("zy_jkxx", zy_jkxx);
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	public void doGetReceiptNumber(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {

		String SJHM = BSPHISUtil.Getbillnumber("收据", dao, ctx);
		if("false".equals(SJHM)){
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "请先维护收据号码!");
		}
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			Map<String, Object> FKFS = dao
					.doLoad("select FKFS as FKFS from GY_FKFS"
							+ " where SYLX='2' and MRBZ='1'", body);
			if(FKFS == null){
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "未维护默认住院付款方式!");
			}
			List<Map<String, Object>> list_FKFS = dao.doQuery("select FKFS as FKFS,HMCD as HMCD from GY_FKFS"
					+ " where SYLX='2' and FKLB='2'", body);
//			if(list_FKFS!=null && list_FKFS.size()>0){
//				List<Object> FKFSs = new ArrayList<Object>();
//				for(int i = 0 ; i < list_FKFS.size() ; i ++){
//					Map<String, Object> FKFS_map = list_FKFS.get(i);
//					FKFSs.add(FKFS_map.get("FKFS"));
//				}
				res.put("fkfss", list_FKFS);
//			}
			body.put("SJHM", SJHM);
			body.put("JKFS", FKFS.get("FKFS"));
			res.put("body", body);
			
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取默认付款方式失败!");
		}
	}
}
