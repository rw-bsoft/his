package phis.application.fsb.source;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import ctd.dao.QueryResult;
import ctd.dao.SimpleDAO;
import ctd.dao.exception.DataAccessException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class FsbPaymentProcessingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FsbPaymentProcessingModel.class);

	public FsbPaymentProcessingModel(BaseDAO dao) {
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
			String SJHM = BSPHISUtil.GetFsbBillnumber("收据", dao, ctx);
			if ("false".equals(SJHM)) {
				SJHM = "";
				res.put("msg", "您的收据已用完或尚未申领，不能进行缴款处理!");
			}

			String ZYHM = BSPHISUtil.get_public_fillleft(
					body.get("value") + "", "0",
					BSPHISUtil.getRydjNo(manaUnitId, "JCHM", "", dao).length());
			// String ZYHM = body.get("value") + "";
			parameters.put("ZYHM", ZYHM);
			Map<String, Object> JC_BRRY = dao
					.doLoad("select ZYH as ZYH,ZYHM as ZYHM,BRXM as BRXM,BRXZ as BRXZ,RYRQ as RYRQ "
							+ "from JC_BRRY where CYPB < 8 AND JGID = :JGID AND ZYHM = :ZYHM",
							parameters);
			if (JC_BRRY != null) {
				Map<String, Object> BRXX = new HashMap<String, Object>();
				BRXX.put("JSLX", 0);
				BRXX.put("ZYH", JC_BRRY.get("ZYH"));
				BRXX = BSPHISUtil.gf_jc_gxmk_getjkhj(BRXX, dao, ctx);
				double JKHJ = Double.parseDouble(BRXX.get("JKHJ") + "");
				JC_BRRY.put("JKHJ", JKHJ);
				BRXX = BSPHISUtil.gf_jc_gxmk_getzfhj(BRXX, dao, ctx);
				double ZFHJ = Double.parseDouble(BRXX.get("ZFHJ") + "");
				JC_BRRY.put("ZFHJ", ZFHJ);
				double SYHJ = BSPHISUtil.getDouble(JKHJ - ZFHJ, 2);
				JC_BRRY.put("SYHJ", SYHJ);
				JC_BRRY.put("SJHM", SJHM);
				SimpleDateFormat sdfdatetime = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				JC_BRRY.put("JKRQ", sdfdatetime.format(new Date()));
				res.put("body", JC_BRRY);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "病人信息查询失败");
		}
	}

	// public static void main(String[] args) {
	// System.out.println(new Date());
	// }

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

		Map<String, Object> JC_TBKK = (Map<String, Object>) req.get("body");
		String sjhm = BSPHISUtil.GetFsbBillnumber("收据", dao, ctx);
		if (!"false".equals(sjhm)) {
			if (!sjhm.equals(JC_TBKK.get("SJHM"))) {
				JC_TBKK.put("SJHM", sjhm);
			}
		} else {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "您的收据已用完或尚未申领，不能进行缴款处理!");
		}
		JC_TBKK.put("JGID", manaUnitId);
		JC_TBKK.put("JSCS", 0);
		JC_TBKK.put("ZFPB", 0);
		JC_TBKK.put("ZCPB", 0);
		JC_TBKK.put("JKRQ", BSHISUtil.toDate(JC_TBKK.get("JKRQ") + ""));
		try {
			if (BSPHISUtil.SetFsbBillNumber("收据", sjhm, dao, ctx)) {
				res.put("body", dao.doSave("create", BSPHISEntryNames.JC_TBKK,
						JC_TBKK, false));
			} else {
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
					"UPDATE JC_TBKK SET ZFPB = 1, ZFRQ = :ZFRQ, ZFGH = :ZFGH  Where JKXH = :JKXH And JGID = :JGID",
					parameters);
			Map<String, Object> ZY_JKZF = new HashMap<String, Object>();
			ZY_JKZF.put("JKXH", pkey);
			dao.doSave("create", BSPHISEntryNames.JC_JKZF, parameters, false);
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

		String SJHM = BSPHISUtil.GetFsbBillnumber("收据", dao, ctx);
		if ("false".equals(SJHM)) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "请先维护收据号码!");
		}
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			Map<String, Object> FKFS = dao.doLoad(
					"select FKFS as FKFS from GY_FKFS"
							+ " where SYLX='2' and MRBZ='1'", body);
			if (FKFS == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "未维护默认住院付款方式!");
			}
			List<Map<String, Object>> list_FKFS = dao.doQuery(
					"select FKFS as FKFS,HMCD as HMCD from GY_FKFS"
							+ " where SYLX='2' and FKLB='2'", body);
			// if(list_FKFS!=null && list_FKFS.size()>0){
			// List<Object> FKFSs = new ArrayList<Object>();
			// for(int i = 0 ; i < list_FKFS.size() ; i ++){
			// Map<String, Object> FKFS_map = list_FKFS.get(i);
			// FKFSs.add(FKFS_map.get("FKFS"));
			// }
			res.put("fkfss", list_FKFS);
			// }
			body.put("SJHM", SJHM);
			body.put("JKFS", FKFS.get("FKFS"));
			res.put("body", body);

		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取默认付款方式失败!");
		}
	}
	
	/**
	 * 补号码
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetCompensationNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String zyhm = BSPHISUtil.get_public_fillleft(req.get("ZYHM") + "", "0",
				BSPHISUtil.getRydjNo(manaUnitId, "JCHM", "", dao).length());
		res.put("JCHM", zyhm);
	}
	
	/**
	 * 获取结算信息
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryJsxx(Map<String, Object> req, Map<String, Object> res,BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(1) as jscs,sum(a.fyhj)/count(1) as jcfy,sum(a.zfhj)/count(1) as jczf,sum(floor(a.jsrq-a.ksrq))/count(1) as pjcr from jc_jcjs a,jc_brry b where a.zyh=b.zyh ");
		try {
			List<Object> cnd = (List<Object>) req.get("cnd");
			if (cnd != null) {
				if (cnd.size() > 0) {
					ExpressionProcessor exp = new ExpressionProcessor();
					String where;
						where = " and " + exp.toString(cnd);
					
					hql.append(where);
				}
			}
			Map<String,Object> parameters  =new HashMap<String, Object>();
			Map<String,Object> ret = dao.doSqlQuery(hql.toString(), parameters).get(0);
			res.put("body", ret);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		}
	}
}
