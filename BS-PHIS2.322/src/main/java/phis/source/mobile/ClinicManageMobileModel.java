/**
 * @(#)ClinicManageMobileModel.java 创建于 2013年6月13日10:24:15
 * 
 * 版权：版本所有 bsoft.com.cn 保留所有权力。
 * 
 */
package phis.source.mobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description 家医诊疗服务
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a>
 */
public class ClinicManageMobileModel implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicManageMobileModel.class);

	public ClinicManageMobileModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取门诊——病人性质
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryNature(Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("MZSY", 1);
		String sql = "select  a.BRXZ as BRXZ , a.XZMC as XZMC from  GY_BRXZ a where a.MZSY=:MZSY and a.BRXZ not in ( select SJXZ from GY_BRXZ )";
		List<Map<String, Object>> dataList = null;
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			if (null != list && list.size() > 0) {
				dataList = new ArrayList<Map<String, Object>>();
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (Map<String, Object> map : list) {
					dataMap.put(map.get("XZMC").toString(),
							String.valueOf(map.get("BRXZ")));
				}
				dataList.add(dataMap);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人性质失败");
		}
		return dataList;
	}

	/**
	 * 获取常用药
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> LoadCommonDrugs(Context ctx,
			Map<String, Object> parameters) throws ModelDataOperationException {
		UserRoleToken user = (UserRoleToken) ctx.get(Context.USER_ROLE_TOKEN);
		// String sql = "select *"
		// + " from (select ys_mz_zt02x0_.JLBH as JLBH,"
		// + " ys_mz_zt01x1_.ZTBH as ZTBH,"
		// + " yk_typk2_.YPXH  as YPXH,"
		// + " yk_typk2_.YPMC  as YPMC,"
		// + " yk_typk2_.YPGG  as YPGG,"
		// + " yk_typk2_.JLDW  as JLDW,"
		// + " yk_typk2_.PSPB  as PSPB"
		// + " from YS_MZ_ZT02 ys_mz_zt02x0_,"
		// + " YS_MZ_ZT01 ys_mz_zt01x1_,"
		// + " YK_TYPK    yk_typk2_"
		// + " where ys_mz_zt02x0_.ZTBH = ys_mz_zt01x1_.ZTBH"
		// + " and ys_mz_zt02x0_.XMBH = yk_typk2_.YPXH"
		// + " and (ys_mz_zt01x1_.SSLB = 4 and ys_mz_zt01x1_.YGDM =:YGDM or"
		// + " ys_mz_zt01x1_.SSLB = 5 and ys_mz_zt01x1_.KSDM =:KSDM or"
		// + " ys_mz_zt01x1_.SSLB = 6)" + " and ys_mz_zt01x1_.ZTLB =:ZTLB"
		// + " and SFQY = 1)";

		String sql = "select * "
				+ " from (select ys_mz_zt02x0_.JLBH as JLBH,"
				+ " ys_mz_zt01x1_.ZTBH as ZTBH,"
				+ " yk_typk2_.YPXH     as YPXH,"
				+ " yk_typk2_.YPMC     as YPMC,"
				+ " yk_typk2_.YPGG     as YPGG,"
				+ " yk_typk2_.JLDW     as JLDW,"
				+ " yk_typk2_.PYDM     as PYDM"
				+ " from YS_MZ_ZT02 ys_mz_zt02x0_,"
				+ " YS_MZ_ZT01 ys_mz_zt01x1_,"
				+ " YK_TYPK    yk_typk2_"
				+ " where ys_mz_zt02x0_.ZTBH = ys_mz_zt01x1_.ZTBH"
				+ " and ys_mz_zt02x0_.XMBH = yk_typk2_.YPXH"
				+ " and (ys_mz_zt01x1_.SSLB = 4 and ys_mz_zt01x1_.YGDM =:YGDM or"
				+ " ys_mz_zt01x1_.SSLB = 5 and ys_mz_zt01x1_.KSDM =:KSDM or"
				+ " ys_mz_zt01x1_.SSLB = 6) and ys_mz_zt01x1_.ZTLB = :ZTLB"
				+ " and SFQY = 1 and ys_mz_zt01x1_.JGID = '"
				+ user.getManageUnit().getId() + "')";
		List<Map<String, Object>> res = null;
		try {
			res = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询常用药失败");
		}
		return res;
	}

	/**
	 * 获取处方组套
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> LoadPrescriptionGroup(Context ctx,
			Map<String, Object> parameters) throws ModelDataOperationException {

		String sql = "select *"
				+ " from (select ys_mz_zt01x0_.ZTBH as ZTBH,"
				+ " ys_mz_zt01x0_.ZTMC as ZTMC,"
				+ " ys_mz_zt01x0_.ZTLB as ZTLB,"
				+ " ys_mz_zt01x0_.SSLB as SSLB,"
				+ " ys_mz_zt01x0_.XMXQ as XMXQ,"
				+ " ys_mz_zt01x0_.SFQY as SFQY,"
				+ " ys_mz_zt01x0_.YGDM as YGDM,"
				+ " ys_mz_zt01x0_.JGID as JGID,"
				+ " ys_mz_zt01x0_.KSDM as KSDM"
				+ " from YS_MZ_ZT01 ys_mz_zt01x0_"
				+ " where ys_mz_zt01x0_.JGID =:JGID"
				+ " and (ys_mz_zt01x0_.KSDM =:KSDM and ys_mz_zt01x0_.SSLB = 2 or"
				+ " ys_mz_zt01x0_.YGDM =:YGDM and ys_mz_zt01x0_.SSLB = 1 or"
				+ " ys_mz_zt01x0_.SSLB = 3)" + " and ys_mz_zt01x0_.SFQY = 1"
				+ " and ys_mz_zt01x0_.ZTLB =:ZTLB"
				+ " order by ys_mz_zt01x0_.ZTBH desc)";

		List<Map<String, Object>> res = null;
		try {
			res = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询处方组套失败");
		}
		return res;
	}

	/**
	 * 根据empiid查询基本信息
	 * 
	 * @param ctx
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadUpdateData(Context ctx,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		String empiId = reqBody.get("empiId").toString();
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			Map<String, Object> brxz = dao.doLoad(cnd,
					BSPHISEntryNames.MS_BRDA, true);

			Map<String, Object> info = dao.doLoad(cnd,
					BSPHISEntryNames.MPI_DemographicInfo, true);
			res.putAll(brxz);
			res.putAll(info);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient baseInfo", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人基本信息失败");
		}
		return res;
	}

	/**
	 * 查询已收费的处方和处置
	 * 
	 * @param ctx
	 * @param reqBody
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> costCount(Context ctx,
			Map<String, Object> reqBody) throws ModelDataOperationException {
		String JZXH = reqBody.get("JZXH").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JZXH", Long.parseLong(JZXH));
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			long l = dao.doCount("MS_CF01", "JZXH=:JZXH and FPHM is not null",
					parameters);
			// 0、存在已收费处方；1、不存在已收费处方
			if (l > 0) {
				res.put("CF", "0");
			} else {
				res.put("CF", "1");
			}
			long m = dao.doCount("MS_YJ01", "JZXH=:JZXH and FPHM is not null",
					parameters);
			// 0、存在已收费处置；1、不存在已收费处置
			if (m > 0) {
				res.put("CZ", "0");
			} else {
				res.put("CZ", "1");
			}

		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  cost count", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询已收费的处方和处置失败！");
		}
		return res;
	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> doLoadMedicineInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Object ypxh = body.get("YPXH");
		long fygb = Long.parseLong(body.get("FYGB") + "");
		// 发药药房 和 药品类别
		Object pharmacyId = body.get("pharmacyId");
		String tabId = (String) body.get("tabId");
		// 药品产地选择方式,库存序号
		try {
			Map<String, Object> med = null;
			if (tabId.equals("clinicCommon")) {
				String orderBy = "ZTBH";
				String schema = YS_MZ_ZT02_MS;
				String cnds = "['eq',['$','JLBH'],['d'," + body.get("JLBH")
						+ "]]";
				List<Map<String, Object>> list = dao.doList(
						CNDHelper.toListCnd(cnds), orderBy, schema);
				if (list.size() > 0) {
					med = list.get(0);
				}
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("YFSB", Long.parseLong(pharmacyId.toString()));
			params.put("YPXH", Long.parseLong(ypxh.toString()));
			StringBuffer hql = new StringBuffer(
					"select a.JYLX as JYLX,a.KSBZ as KSBZ,a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,a.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as TYPE,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ ");
			hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f ");
			hql.append(" where  ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND  b.YFSB=:YFSB  AND a.YPXH = :YPXH ORDER BY d.SBXH");
			List<Map<String, Object>> meds = dao
					.doQuery(hql.toString(), params);
			if (meds.size() > 0) {// 多产地取第一条记录
				Map<String, Object> zt_med = meds.get(0);
				if (tabId.equals("clinicCommon")) {
					zt_med.putAll(med);
				}
				Dictionary dic = DictionaryController.instance().getDic(
						"phis.dictionary.drugMode");
				if(zt_med.get("GYFF") != null && zt_med.get("GYFF") != "null"){
					String gYFF_text = dic.getText(zt_med.get("GYFF") + "");
					zt_med.put("GYFF_text", gYFF_text);
					zt_med.put("GYTJ_text", gYFF_text);
				}
				Dictionary confirm = DictionaryController.instance().getDic(
						"phis.dictionary.confirm");
				String kSBZ_text = confirm.getText(zt_med.get("KSBZ") + "");
				zt_med.put("KSBZ_text", kSBZ_text);
				String jYLX_text = confirm.getText(zt_med.get("JYLX") + "");
				zt_med.put("JYLX_text", jYLX_text);
				zt_med.put("FYGB", fygb);
				return zt_med;
			} else {
				body.put("errorMsg", "暂无库存!");
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
		return body;
	}

	/**
	 * 获取常用项
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> LoadCommonTerm(Context ctx,
			Map<String, Object> parameters) throws ModelDataOperationException {

		String sql = "select *"
				+ " from (select ys_mz_zt02x0_.JLBH as JLBH,"
				+ " ys_mz_zt01x1_.ZTBH as ZTBH,"
				+ " gy_ylsf2_.FYXH as FYXH,"
				+ " gy_ylsf2_.FYMC as FYMC,"
				+ " ys_mz_zt02x0_.XMSL as XMSL,"
				+ " ys_mz_zt02x0_.YCJL as YCJL,"
				+ " ys_mz_zt02x0_.SYPC as SYPC,"
				+ " ys_mz_zt02x0_.MRCS as MRCS,"
				+ " ys_mz_zt02x0_.YYTS as YYTS,"
				+ " ys_mz_zt02x0_.GYTJ as GYTJ,"
				+ " ys_mz_zt01x1_.ZTLB as ZTLB,"
				+ " gy_ylsf2_.PYDM as PYDM"
				+ " from YS_MZ_ZT02 ys_mz_zt02x0_,"
				+ " YS_MZ_ZT01 ys_mz_zt01x1_,"
				+ " GY_YLSF    gy_ylsf2_"
				+ " where ys_mz_zt02x0_.ZTBH = ys_mz_zt01x1_.ZTBH"
				+ " and ys_mz_zt02x0_.XMBH = gy_ylsf2_.FYXH"
				+ " and (ys_mz_zt01x1_.SSLB = 4 and ys_mz_zt01x1_.YGDM =:YGDM or"
				+ " ys_mz_zt01x1_.SSLB = 5 and ys_mz_zt01x1_.KSDM =:KSDM or"
				+ " ys_mz_zt01x1_.SSLB = 6)" + " and ys_mz_zt01x1_.ZTLB = 4"
				+ " and SFQY = 1)";

		List<Map<String, Object>> res = null;
		try {
			res = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询常用项失败");
		}
		return res;
	}

	/**
	 * 获取组套
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> LoadGroup(Context ctx,
			Map<String, Object> parameters) throws ModelDataOperationException {

		String sql = "select *"
				+ " from (select ys_mz_zt01x0_.ZTBH as ZTBH,"
				+ " ys_mz_zt01x0_.ZTMC as ZTMC,"
				+ " ys_mz_zt01x0_.ZTLB as ZTLB,"
				+ " ys_mz_zt01x0_.SSLB as SSLB,"
				+ " ys_mz_zt01x0_.YGDM as YGDM,"
				+ " ys_mz_zt01x0_.KSDM as KSDM,"
				+ " ys_mz_zt01x0_.XMXQ as XMXQ,"
				+ " ys_mz_zt01x0_.SFQY as SFQY,"
				+ " ys_mz_zt01x0_.JGID as JGID"
				+ " from YS_MZ_ZT01 ys_mz_zt01x0_"
				+ " where ys_mz_zt01x0_.ZTLB = 4"
				+ " and ys_mz_zt01x0_.JGID =:JGID"
				+ " and (ys_mz_zt01x0_.KSDM =:KSDM and ys_mz_zt01x0_.SSLB = 2 or"
				+ " ys_mz_zt01x0_.YGDM =:YGDM and ys_mz_zt01x0_.SSLB = 1 or"
				+ " ys_mz_zt01x0_.SSLB = 3)" + " and ys_mz_zt01x0_.SFQY = 1"
				+ " order by ys_mz_zt01x0_.ZTBH desc)";

		List<Map<String, Object>> res = null;
		try {
			res = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询组套失败");
		}
		return res;
	}

	@SuppressWarnings("deprecation")
	public Map<String, Object> doLoadCostInfo(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JLBH", body.get("JLBH"));
			params.put("JGID", jgid);
			StringBuffer hql = new StringBuffer(
					"select distinct b.FYXH as YLXH,a.XMMC as FYMC,a.XMSL as YLSL,c.FYDJ as YLDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS");
			hql.append(" from YS_MZ_ZT02 a,GY_YLSF b,GY_YLMX c ");
			hql.append(" where  a.XMBH = b.FYXH AND  b.FYXH = c.FYXH and a.JLBH = :JLBH and c.JGID=:JGID ORDER BY a.ZTBH");
			Map<String, Object> meds = dao.doLoad(hql.toString(), params);
			if (meds.get("FYKS") != null && meds.get("FYKS") != "") {
				meds.put(
						"FYKS_text",
						meds.get("FYKS") == null ? "" : DictionaryController
								.instance()
								.get("phis.dictionary.department_mzyj")
								.getText(meds.get("FYKS").toString()));
				Object tag = meds.remove("FYKS");
				Object text = meds.remove("FYKS_text");
				meds.put("ZXKS", tag.toString());
				meds.put("ZXKS_text", text.toString());
			}
			return meds;
		} catch (PersistentDataOperationException e) {
			logger.error(
					"fail to load medicine information by database reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}catch (ControllerException e) {
			logger.error(
					"fail to load medicine information by dictionary reason", e);
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}

	}

	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> doLoadPersonalSet(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		// String uid=user.getId();
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();
		if (body.get("ZTBH") == null) {
			throw new ModelDataOperationException("无法获取组套信息!");
		}
		params.put("ZTBH", Long.parseLong(body.get("ZTBH").toString()));
		params.put("JGID", jgid);
		StringBuffer hql = new StringBuffer(
				"select distinct a.JLBH,b.FYXH as YLXH,a.XMMC as FYMC,a.XMSL as YLSL,a.YPZH as YPZH,c.FYDJ as YLDJ,b.FYGB as FYGB,b.FYDW as FYDW,b.XMLX as XMLX,c.FYKS as FYKS,b.MZSY as MZSY");
		hql.append(" from YS_MZ_ZT02 a,GY_YLSF b,GY_YLMX c ");
		hql.append(" where  a.XMBH = b.FYXH AND  b.FYXH = c.FYXH and a.ZTBH = :ZTBH and c.JGID=:JGID ORDER BY a.JLBH");
		try {
			List<Map<String, Object>> meds = dao
					.doQuery(hql.toString(), params);
			for (Map<String, Object> med : meds) {
				if (!"1".equals(med.get("MZSY") + "")) {
					med.put("errorMsg", "不存在!");
					continue;
				}
				if (med.get("FYKS") != null && med.get("FYKS") != "") {
					med.put("FYKS_text",
							med.get("FYKS") == null ? "" : DictionaryController
									.instance().getDic("phis.dictionary.department_mzyj")
									.getText(med.get("FYKS").toString()));
					Object tag = med.remove("FYKS");
					Object text = med.remove("FYKS_text");
					med.put("ZXKS", tag.toString());
					med.put("ZXKS_text", text.toString());
				}
				res.add(med);
			}
			return res;
		} catch (Exception e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
	}

	/**
	 * 
	 * 
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> LoadKSDM(Context ctx,
			Map<String, Object> parameters) throws ModelDataOperationException {
		// add by yangl 强制写入机构Ref id
		parameters.put("JGID", UserRoleToken.getCurrent().getManageUnit()
				.getRef());
		String sql = "select a.ID as KSDM, a.OFFICENAME as KSMC"
				+ " from SYS_Office a"
				+ " where ORGANIZCODE =:JGID"
				+ " and (OUTPATIENTCLINIC = 1 or MEDICALLAB = 1)"
				+ " and OFFICECODE not in (select PARENTID from SYS_Office where PARENTID is not null)";

		List<Map<String, Object>> res = null;
		try {
			res = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to query  patient nature", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询执行科室字典失败");
		}
		return res;
	}

	/**
	 * 辖区病人就诊
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
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
		Long SHIYB = Long.parseLong(ParameterUtil.getParameter(manageUnit,
				"SHIYB", "0", "市医保病人性质", ctx));
		Long SHENGYB = Long.parseLong(ParameterUtil.getParameter(manageUnit,
				"SHENGYB", "0", "省医保病人性质", ctx));
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
		String empiId = (String) body.get("empiId");
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("EMPIID", empiId);
		params1.put("BRID", brid);
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
			/*
			 * 系统参数 YXWGGBRJZ(允许为挂号病人就诊)只要挂号效期内没有挂号，都会默认挂号，不必到挂号科室挂号(移动目前没有挂号模块)
			 * if (!YXWGGBRJZ.equals("1")) { res.put("x-response-code", 601);
			 * res.put("x-response-msg", "无法获取就诊信息!"); throw new
			 * ModelDataOperationException("无法获取就诊信息!"); }
			 */

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
						res.put("x-response-code", 602);
						res.put("x-response-msg", "就诊号码未设置或已用完!");
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
					ms_ghmx.put("YDGH", "0");// 移动挂号
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
		;
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
								 * retMap.get("SBXH"); } else {
								 * res.put("x-response-code", 601);
								 * res.put("x-response-msg", "挂号信息无法调入，终止操作!");
								 * throw new
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
				jzmx_hql.append(" where GHXH=:GHXH and BRBH=:BRBH and KSDM=:KSDM and KSSJ>:KSSJ order by KSSJ desc");
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
							res.put("x-response-code", 603);
							res.put("x-response-msg", "对不起，其他医生正在处理该病人!");
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
				res.put("x-response-code", 601);
				res.put("x-response-msg", "挂号信息无法调入，终止操作!");
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
}
