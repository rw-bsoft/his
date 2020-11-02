package phis.application.pha.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;

/**
 * 处方审核Model
 * 
 * @author caijy
 * 
 */
public class PharmacyPrescriptionAuditModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyPrescriptionAuditModel.class);

	public PharmacyPrescriptionAuditModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 查询病人处方列表
	 * @updateInfo
	 * @param body
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPrescription(Map<String, Object> body,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		int sfjg = MedicineUtils.parseInt(body.get("sfjg"));
		Object cfhm = body.get("cfhm");
		Map<String, Object> map_par_cf = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		long adt_begin = 1;
		String adt_beginStr = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.CFXQ, ctx); // 处方效期判断
		if (adt_beginStr != "") {
			adt_begin = MedicineUtils.parseLong(adt_beginStr);
		}
		map_par_cf
				.put("adt_begin",
						new Date(
								((Calendar.getInstance().getTimeInMillis()) - (adt_begin * 24 * 60 * 60 * 1000))));
		map_par_cf.put("yfsb", yfsb);
		map_par_cf.put("jgid", jgid);
		map_par_cf.put("sfjg", sfjg);
		StringBuffer hql_cf_info = new StringBuffer(); // 处方信息
		StringBuffer hql_cf_condition = new StringBuffer(); // 处方信息查询条件
		hql_cf_info
				.append("select a.YFSB as YFSB, a.CFHM as CFHM, a.CFSB as CFSB, a.KFRQ as KFRQ, a.BRXM as BRXM, a.CFLX as CFLX,")
				.append("a.BRID as BRID, a.JGID as JGID, a.FPHM as FPHM, b.PERSONNAME as YSXM, c.OFFICENAME as KSMC")
				.append(" from MS_CF01 a,SYS_Personnel b,SYS_Office c");
		hql_cf_condition.append(" where a.YFSB =:yfsb AND a.JGID =:jgid");

		if (cfhm != null && StringUtils.isNotBlank((String) cfhm)) {
			map_par_cf.put("cfhm", MedicineUtils.parseInt(cfhm));
			hql_cf_condition.append(" AND a.cfhm =:cfhm");
		}
		hql_cf_condition
				.append(" and a.ZFPB = 0 and (a.FPHM is null or a.FPHM = '')  and (a.FYBZ = 0 or a.FYBZ = -1)")
				.append(" and a.YSDM = b.PERSONID and c.ID = a.KSDM")
				.append(" and EXISTS ")
				.append("(select 1 from MS_CF02 b  where a.JGID = b.JGID and a.CFSB = b.CFSB");
		if (sfjg != 0) {
			hql_cf_condition.append(" and b.SFJG =:sfjg");
		} else {
			hql_cf_condition
					.append(" and (b.SFJG =:sfjg OR b.SFJG = '' or b.SFJG is null)");
		}
		hql_cf_condition.append(" and KFRQ >=:adt_begin");
		hql_cf_condition.append(") order by a.KFRQ desc");
		hql_cf_info.append(hql_cf_condition);
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req, map_par_cf,
				hql_cf_info.toString(), null);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 检查系统启用门诊审方标志
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String queryAuditPharmacyEnable(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		return ParameterUtil.getParameter(jgid, BSPHISSystemArgument.QYMZSF,
				ctx);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 查询处方审核明细
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryAuditPrescriptionDetail(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql_presc = new StringBuffer(); // 处方
		hql_presc
				.append("select d.BRID as BRID, a.JGID as JGID, c.YPMC as YPMC, a.YFGG as YFGG, a.YFDW as YFDW,b.CDMC as CDMC, a.YPSL as YPSL, a.FYGB as FYGB, a.CFTS as CFTS, a.ZFBL as ZFBL,a.YPDJ as YPDJ, a.CFSB as CFSB, a.YPXH as YPXH, a.YPCD as YPCD, a.YPYF as YPYF,a.YFBZ as YFBZ, a.XMLX as XMLX, (SELECT SUM(YPSL)  FROM YF_KCMX where YFSB =:yfsb and YPXH = a.YPXH and YPCD = a.YPCD and JYBZ = 0 and JGID =:jgid) as YFKC,0 as JYBZ, a.SBXH as SBXH, a.YCJL as YCJL, a.GYTJ as GYTJ, a.YPZS as YPZS, a.YYTS as YYTS, a.HJJE as HJJE,a.PSJG as PSJG, a.PSPB as PSPB, a.YPZH as YPZH, a.SFJG as SFJG, a.SFGH as SFGH, a.SFYJ as SFYJ  from MS_CF02 a, YK_CDDZ b, YK_TYPK c, MS_CF01 d where a.CFSB =:cfsb and a.cfsb = d.cfsb and a.YPCD = b.YPCD and a.YPXH = c.YPXH and a.JGID =:jgid ");
		StringBuffer hql_spare = new StringBuffer(); // 自备药
		hql_spare
				.append("select b.BRID as BRID, a.JGID as JGID, a.BZMC as YPMC, a.YFGG as YFGG, a.YFDW as YFDW,'' as CDMC, a.YPSL as YPSL, a.FYGB as FYGB, a.CFTS as CFTS, a.ZFBL as ZFBL,a.YPDJ as YPDJ, a.CFSB as CFSB, a.YPXH as YPXH, a.YPCD as YPCD, a.YPYF as YPYF,a.YFBZ as YFBZ, a.XMLX as XMLX, 0.0000 as YFKC, 0 as JYBZ, a.SBXH as SBXH, a.YCJL as YCJL,a.GYTJ as GYTJ, a.YPZS as YPZS, a.YYTS as YYTS, a.HJJE as HJJE, a.PSJG as PSJG,a.PSPB as PSPB, a.YPZH as YPZH, a.SFJG as SFJG, a.SFGH as SFGH, a.SFYJ as SFYJ from  MS_CF02 a, MS_CF01 b where a.CFSB =:cfsb and a.cfsb = b.cfsb and a.JGID =:jgid and a.ypxh = 0");// 自备药药品序号为0
		Map<String, Object> presc_parmater = new HashMap<String, Object>();
		Map<String, Object> spare_parmater = new HashMap<String, Object>();
		int sfjg = MedicineUtils.parseInt(body.get("sfjg"));
		presc_parmater.put("cfsb", MedicineUtils.parseLong(body.get("cfsb")));
		presc_parmater.put("jgid", MedicineUtils.parseString(body.get("jgid")));
		presc_parmater.put("sfjg", sfjg);
		spare_parmater.putAll(presc_parmater);
		presc_parmater.put("yfsb", MedicineUtils.parseLong(body.get("yfsb")));
		if (sfjg != 0) {
			hql_presc.append(" and a.SFJG =:sfjg");
			hql_spare.append(" and a.SFJG =:sfjg");
		} else {
			hql_presc
					.append(" and (a.SFJG =:sfjg or a.SFJG = '' or a.SFJG is null)");
			hql_spare
					.append(" and (a.SFJG =:sfjg or a.SFJG = '' or a.SFJG is null)");
		}
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> prescription_list = null;
		List<Map<String, Object>> spare_list = null;
		try {
			prescription_list = dao.doSqlQuery(hql_presc.toString(),
					presc_parmater);
			spare_list = dao.doSqlQuery(hql_spare.toString(), spare_parmater);
			prescription_list.addAll(spare_list);
			if (null != prescription_list && prescription_list.size() > 0) {
				list = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> m : prescription_list) {
					if (m.get("YPYF") != null) {
						String ypyf = DictionaryController.instance()
								.get("phis.dictionary.useRate")
								.getText(m.get("YPYF") + "");
						m.put("YPYF_STR", ypyf);
					}
					if (m.get("GYTJ") != null) {// ctd.dictionary.DictionaryController
						String ypzs = DictionaryController.instance()
								.get("phis.dictionary.drugMode")
								.getText(m.get("GYTJ") + "");
						m.put("GYTJ_STR", ypzs);
					}
					if (m.get("SFGH") != null) { // 审方工号
						String sfry = DictionaryController.instance()
								.get("phis.dictionary.user")
								.getText(m.get("SFGH") + "");
						m.put("SFRY", sfry); // 审方人员
					}
					if (m.get("YPZS") != null) {
						String ypzs = DictionaryController.instance()
								.get("phis.dictionary.suggested")
								.getText(m.get("YPZS") + "");
						m.put("YPZS_STR", ypzs);
					}
					list.add(m);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "载入处方审核详细列表失败!", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "载入处方审核详细列表失败!", e);
		}
		return list;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 全部审核未审核的处方信息为审核状态
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void updateToAdoptAll(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			String sfgh = user.getUserId(); // 当前操作员工号
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(sdf.format(new Date()));
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> record = body.get(i);
				record.put("SFJG", 1);
				record.put("SFGH", sfgh);
				record.put("YFSB", yfsb);
				Map<String, Object> map = dao.doSave("update",
						BSPHISEntryNames.YF_CF02_SH, record, false);
				if (null != map) {
					record.put("SFSJ", date);
					record.put("SFBZ", record.get("SFJG"));
					record.put("CFZH",
							MedicineUtils.parseLong(record.get("YPZH")));
					dao.doSave("create", BSPHISEntryNames.YF_MZ_SFJL, record,
							false);
				}
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "全部审核通过操作失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "全部审核通过操作失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期转换失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 处方明细审核明细保存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveAuditPrescription(List<Map<String, Object>> body,
			Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			String sfgh = user.getUserId(); // 当前操作员工号
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(sdf.format(new Date()));
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> record = body.get(i);
				record.put("SFGH", sfgh);
				record.put("YFSB", yfsb);
				Map<String, Object> map = dao.doSave("update",
						BSPHISEntryNames.YF_CF02_SH, record, false);
				if (null != map) {
					record.put("SFSJ", date);
					record.put("SFBZ", record.get("SFJG"));
					record.put("CFZH",
							MedicineUtils.parseLong(record.get("YPZH")));
					dao.doSave("create", BSPHISEntryNames.YF_MZ_SFJL, record,
							false);
				}
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "处方明细审核失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "处方明细审核失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期转换失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-27
	 * @description 查询病人处方明细
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPrescriptionDetail(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> prescriptionInfo = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a.BRXM as BRXM, a.BRID as BRID,a.JZXH as JZXH,b.BRXB as BRXB, e.XZMC as XZMC, b.CSNY as CSNY, b.MZHM as MZHM, a.KFRQ as KFRQ, a.CFHM as CFHM, a.KSDM as KSDM, d.OFFICENAME as KSMC, c.PERSONNAME as YSXM FROM MS_CF01 a,MS_BRDA b,SYS_Personnel c,SYS_Office d,GY_BRXZ e WHERE a.CFSB =:cfsb AND a.JGID =:jgid AND a.YSDM = c.PERSONID AND a.BRID = b.BRID AND a.KSDM = d.ID AND b.BRXZ = e.BRXZ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("jgid", MedicineUtils.parseString(body.get("jgid")));
		parameters.put("cfsb", MedicineUtils.parseLong(body.get("cfsb")));
		try {
			prescriptionInfo = dao.doLoad(hql.toString(), parameters);
			if (prescriptionInfo != null) {
				int brxb = Integer.parseInt(prescriptionInfo.get("BRXB") + "");
				if (brxb == 1) {
					prescriptionInfo.put("XBMC", "男");
				} else if (brxb == 2) {
					prescriptionInfo.put("XBMC", "女");
				} else if (brxb == 9) {
					prescriptionInfo.put("XBMC", "未说明的性别");
				} else if (brxb == 0) {
					prescriptionInfo.put("XBMC", "未知的性别");
				}
				if (prescriptionInfo.get("CSNY") != null) {
					prescriptionInfo.put(
							"AGE",
							BSPHISUtil.getPersonAge(
									(Date) prescriptionInfo.get("CSNY"), null)
									.get("ages"));
				}
				parameters.remove("cfsb");
				parameters.put("JZXH", prescriptionInfo.get("JZXH"));
				List<Map<String,Object>> brzds = dao.doQuery("SELECT a.ZDMC as ZDMC FROM MS_BRZD a where a.JZXH = :JZXH and a.JGID = :jgid order by PLXH", parameters);
				StringBuilder brzdStr = new StringBuilder();
				for(int i = 0 ; i < brzds.size() ; i ++){
					if(brzdStr.length() > 0){
						brzdStr.append(",").append(brzds.get(i).get("ZDMC"));
					}else{
						brzdStr.append(brzds.get(i).get("ZDMC").toString());
					}
				}
				prescriptionInfo.put("BRZD", brzdStr);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "载入病人处方信息失败!", e);
		}
		return prescriptionInfo;
	}
}
