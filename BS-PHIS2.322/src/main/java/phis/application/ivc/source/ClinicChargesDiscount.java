package phis.application.ivc.source;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

//门诊收费打折 zhaojian 2018-06-14
public class ClinicChargesDiscount {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicChargesDiscount.class);

	public ClinicChargesDiscount(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取门诊收费优惠金额
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @param zfzje
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public double GetDiscount(Map<String, Object> req, Map<String, Object> res,
			Context ctx, double zfzje) throws ModelDataOperationException {

		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String MZSFDZ = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.MZSFDZ, ctx);
		// 系统参数MZSFDZ：a;b@c@d@e@f;b@c@d@e@f;
		// a:是否启用：1启用 0禁用
		// b:业务类型（家医签约用户）
		// c:病人性质 ：1000自费 6000新农合 3000医保 2000新医保
		// d:费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠
		// e:费用序号
		// f:费用优惠公式：如ZFJE*0.9、ZFJE-5

		try {
			// 获取启用标志
			if (MZSFDZ.equals("null") || MZSFDZ.equals("")
					|| MZSFDZ.substring(0, 1).equals("0")) {
				return 0;
			}
			String[] dzArray = MZSFDZ.split(";");
			Map<String, Object> mzxx = (Map<String, Object>) req.get("mzxx");
			Map<String, Object> njjbyjsxx = (Map<String, Object>) mzxx.get("njjbyjsxx");
			if(njjbyjsxx!=null){
				Map<String, Object> njjbyjsxx2 = (Map<String, Object>) njjbyjsxx.get("NJJBYJSXX");
				if(njjbyjsxx2!=null && !njjbyjsxx2.get("YLRYLB").toString().equals("51")){
					return 0;
				}
			}
			long brid = Long.parseLong(mzxx.get("BRID") + "");
			List<Map<String, Object>> body = (List<Map<String, Object>>) req
					.get("body");
			double yhje = 0;
			for (int i = 1; i < dzArray.length; i++) {
				// 获取业务类型
				String ywlx = dzArray[i].split("@")[0];
				// if (!validateByYwlx(req, res, ctx,
				// ywlx,mzxx.get("BRXZ").toString())) {
				if (!validateByYwlx(ywlx, brid, "")) {
					continue;
				}
				// 获取病人性质：1000自费 6000新农合 3000医保 2000新医保
				String brxz = dzArray[i].split("@")[1];
				if (!mzxx.get("BRXZ").toString().equals(brxz)) {
					continue;
				}
				// 费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠
				String fygb = dzArray[i].split("@")[2]; // 对应fygb字段
				// 费用序号
				String fyxh = dzArray[i].split("@")[3]; // 针对单个收费项目打折使用
														// 一般诊疗费7602
				// 费用优惠公式
				String yhgs = dzArray[i].split("@")[4];
				if (!fyxh.equals("0")) {
					double zjje = 0;
					double zfje = 0;
					for (int j = 0; j < body.size(); j++) {
						Map<String, Object> data = body.get(j);
						String YPXH = data.get("YPXH") + "";
						/*
						 * String HJJE = data.get("HJJE") + ""; String ZFBL =
						 * data.get("ZFBL") + ""; zjje =
						 * BSPHISUtil.getDouble(zjje +
						 * BSPHISUtil.getDouble(Double.parseDouble(HJJE), 2),
						 * 2); zfje = BSPHISUtil.getDouble(zfje +
						 * BSPHISUtil.getDouble(Double.parseDouble(HJJE)*
						 * Double.parseDouble(ZFBL), 2), 2);
						 */
						if (YPXH.equals(fyxh)) {
							yhje = 1;// 先写死
						}
					}
				} else if (!fygb.equals("0") && fyxh.equals("0")) {
					boolean isAllSameFy = true;// 是否全为同一费用
					for (int j = 0; j < body.size(); j++) {
						Map<String, Object> data = body.get(j);
						String FYGB = data.get("FYGB") + "";
						if (!FYGB.equals(fygb)) {
							isAllSameFy = false;
						}
					}
					if (isAllSameFy) {
						yhje = zfzje * Double.parseDouble(yhgs);// 先写死
					}
				} else if (fygb.equals("0") && fyxh.equals("0")) {
					yhje = zfzje * Double.parseDouble(yhgs);// 先写死
				}
			}
			return yhje;
		} catch (Exception e) {
			logger.error("获取门诊收费优惠金额失败", e);
			throw new ModelDataOperationException("获取门诊收费优惠金额失败!", e);
		}
	}


	/**
	 * 获取挂号收费优惠金额
	 *
	 * @param req
	 * @param res
	 * @param ctx
	 * @param zfzje
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public double GetGhsfDiscount(Map<String, Object> req, Map<String, Object> res,
							  Context ctx, double zfzje, String sfzh) throws ModelDataOperationException {

		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String GHSFDZ = ParameterUtil.getParameter(manaUnitId,
				BSPHISSystemArgument.GHSFDZ, ctx);
		// 系统参数MZSFDZ：a;b@c@d@e@f;b@c@d@e@f;
		// a:是否启用：1启用 0禁用
		// b:业务类型（家医签约用户）
		// c:病人性质 ：1000自费 6000新农合 3000医保 2000新医保
		// d:费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠
		// e:费用序号
		// f:费用优惠公式：如ZFJE*0.9、ZFJE-5
		try {
			// 获取启用标志
			if (GHSFDZ.equals("null") || GHSFDZ.equals("")
					|| GHSFDZ.substring(0, 1).equals("0")) {
				return 0;
			}
			String[] dzArray = GHSFDZ.split(";");
			Map<String, Object> ghxx = (Map<String, Object>) req.get("body");
			long brid = Long.parseLong(ghxx.get("BRID") + "");
			double yhje = 0;
			for (int i = 1; i < dzArray.length; i++) {
				// 获取业务类型
				String ywlx = dzArray[i].split("@")[0];
				// if (!validateByYwlx(req, res, ctx,
				// ywlx,mzxx.get("BRXZ").toString())) {
				if (!validateByYwlx_ghsf(ywlx, brid, sfzh)) {
					continue;
				}
				// 获取病人性质：1000自费 6000新农合 3000医保 2000新医保
				String brxz = dzArray[i].split("@")[1];
				if (!ghxx.get("BRXZ").toString().equals(brxz)) {
					continue;
				}
				// 费用类型(化验费、放射费、心电图等等),0表示不按类型按总价优惠
				String fygb = dzArray[i].split("@")[2]; // 对应fygb字段
				// 费用序号
				String fyxh = dzArray[i].split("@")[3]; // 针对单个收费项目打折使用
				// 一般诊疗费7602
				// 费用优惠公式
				String yhgs = dzArray[i].split("@")[4];
				if (!fyxh.equals("0")) {
					double zjje = 0;
					double zfje = 0;
					//诊疗项目
					if ((ghxx.get("ZLXM")+"").equals(fyxh)) {
						/*
						 * String HJJE = data.get("HJJE") + ""; String ZFBL =
						 * data.get("ZFBL") + ""; zjje =
						 * BSPHISUtil.getDouble(zjje +
						 * BSPHISUtil.getDouble(Double.parseDouble(HJJE), 2),
						 * 2); zfje = BSPHISUtil.getDouble(zfje +
						 * BSPHISUtil.getDouble(Double.parseDouble(HJJE)*
						 * Double.parseDouble(ZFBL), 2), 2);
						 */
						yhje = Double.parseDouble(yhgs);//获取固定优惠金额
					}
				} else if (fygb.equals("0") && fyxh.equals("0")) {
					yhje = zfzje * Double.parseDouble(yhgs);//按比例计算优惠金额
				}
			}
			return yhje;
		} catch (Exception e) {
			logger.error("获取门诊收费优惠金额失败", e);
			throw new ModelDataOperationException("获取门诊收费优惠金额失败!", e);
		}
	}

	// 业务类型校验
	public Boolean validateByYwlx(String ywlx, long brid, String sfzh)
			throws ModelDataOperationException {
		try {
			if (ywlx.equals("1")) {// 判定是否为签约用户
				//查询HIS系统中该病人是否已收过家医签约基本服务包（15316、16049）的费用（默认判定一年内）
				String sql_where = " a.YJXH=b.YJXH AND a.YLXH=c.FYXH AND b.BRID IN (SELECT A.BRID FROM MS_BRDA A,(SELECT SFZH FROM MS_BRDA WHERE BRID=:BRID AND SFZH IS NOT NULL) B WHERE A.SFZH=LOWER(B.SFZH) OR (A.SFZH=UPPER(B.SFZH))) AND b.ZFPB=0  "
						+ "AND b.FPHM IS NOT NULL AND c.FYMC='家庭医生签约基本服务包' AND c.ZFPB=0 AND b.KDRQ>ADD_MONTHS(SYSDATE,-12) ";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BRID", brid);
				long count = dao.doSqlCount("MS_YJ02 a,MS_YJ01 b,GY_YLSF c",
						sql_where, parameters);
				//查询健康档案系统中该病人是否已签约
				String sql_where2 = " (IDCARD=(SELECT LOWER(SFZH) FROM MS_BRDA WHERE BRID=:BRID AND SFZH IS NOT NULL)  OR IDCARD=(SELECT UPPER(SFZH) FROM MS_BRDA WHERE BRID=:BRID AND SFZH IS NOT NULL)) AND SIGNFLAG=1 AND SYSDATE>=SCEBEGINDATE AND SYSDATE<=(SCEENDDATE+1)";
				Map<String, Object> parameters2 = new HashMap<String, Object>();
				parameters2.put("BRID", brid);
				long count2 = dao.doSqlCount("MPI_DEMOGRAPHICINFO",
						sql_where2, parameters2);
				if (count == 0 && count2 == 0) {
					return false;
				}
				return true;
			}
			return false;
		} catch (PersistentDataOperationException e) {
			logger.error("业务类型校验失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "业务类型校验失败");
		}
	}

	// 业务类型校验(挂号收费)
	public Boolean validateByYwlx_ghsf(String ywlx, long brid, String sfzh)
			throws ModelDataOperationException {
		try {
			if (ywlx.equals("1")) {// 判定是否为签约用户
				String sql_where = "a.SCID=b.SCID and a.SPID=c.SPID and b.signFlag=1 and SYSDATE<=(b.endDate+1) and c.isGhsfjm=1 and b.favoreeempiid=d.empiid and d.BRID=:BRID";
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("BRID", brid);
				long count = dao.doSqlCount("SCM_SignContractPackage a,SCM_SignContractRecord b,SCM_ServicePackage c,MS_BRDA d",
						sql_where, parameters);
				if (count == 0) {
					return false;
				}
				return true;
			}
			return false;
		} catch (PersistentDataOperationException e) {
			logger.error("业务类型校验失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "业务类型校验失败");
		}
	}
}
