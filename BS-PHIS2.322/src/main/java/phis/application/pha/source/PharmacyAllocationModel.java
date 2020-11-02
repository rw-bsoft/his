package phis.application.pha.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

/**
 * 药房调拨model
 * 
 * @author caijy
 * 
 */
public class PharmacyAllocationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyAllocationModel.class);

	public PharmacyAllocationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请数据查询
	 * @updateInfo
	 * @param cnds
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMedicinesRequisition(List<Object> cnds,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		try {
			hql.append(
					"select a.JGID as JGID,a.SQYF as SQYF,a.SQDH as SQDH,a.SQRQ as SQRQ,a.RKRQ as RKRQ,a.CKRQ as CKRQ,")
					.append("sum(b.JHJE) as JHJE,sum(b.LSJE) as LSJE,a.TJBZ as TJBZ,a.CKBZ as CKBZ, a.BZXX as BZXX from  YF_DB01 a,YF_DB02 b  where ")
					.append(ExpressionProcessor.instance().toString(cnds)
							.replaceAll("str", "to_char"))
					.append(" and a.SQYF=b.SQYF and a.SQDH=b.SQDH and a.JGID=b.JGID  group by a.JGID ,a.SQYF,a.SQDH ,a.SQRQ,a.CKRQ ,a.RKRQ,a.TJBZ,a.CKBZ,a.BZXX");
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "调拨数据查询失败!", e);
		}
		MedicineCommonModel model=new MedicineCommonModel(dao);
		return model.getPageInfoRecord(req,
				new HashMap<String, Object>(), hql.toString(), null);
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请单修改提交前判断是否已经被删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationMedicinesRequisitionDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" SQDH=:sqdh and SQYF=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", MedicineUtils.parseInt(body.get("SQDH")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("SQYF")));
		try {
			long l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("数据已删除,已刷新页面!");
			}
			hql_count = new StringBuffer();
			hql_count
					.append(" SQDH=:sqdh and SQYF=:yfsb and TJBZ=1 and RKBZ=1");
			l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l == 1) {
				return MedicineUtils.getRetMap("数据已确认,已刷新页面!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "验证失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请单删除
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeMedicinesRequisition(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" SQDH=:sqdh and SQYF=:yfsb and TJBZ=0");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", MedicineUtils.parseInt(body.get("SQDH")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("SQYF")));
		try {
			long l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("数据已发生变化,已刷新页面!");
			}
			StringBuffer hql_delete = new StringBuffer();
			hql_delete
					.append("delete from YF_DB02  where SQDH=:sqdh and SQYF=:yfsb");
			dao.doUpdate(hql_delete.toString(), map_par);
			hql_delete = new StringBuffer();
			hql_delete
					.append("delete from YF_DB01  where SQDH=:sqdh and SQYF=:yfsb");
			dao.doUpdate(hql_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "删除失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请单提交
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveMedicinesRequisitionSubmit(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" SQDH=:sqdh and SQYF=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", MedicineUtils.parseInt(body.get("SQDH")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("SQYF")));
		try {
			long l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils
						.getRetMap("没有检索到本调拨单,可能已被删除,不能提交操作!窗口已刷新!");
			}
			StringBuffer hql_update = new StringBuffer();
			hql_update
					.append("update YF_DB01  set TJBZ = 1 where SQDH=:sqdh and SQYF=:yfsb");
			dao.doUpdate(hql_update.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "提交失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨出库 明细查询
	 * @updateInfo update by caijy at 2016年4月28日 16:36:37 for 价格从库存里面取,解决类似 30.82/7 四舍五入为4.4029 ,4.4029*7=30.8203 价格不同导致记录多条的问题
	 * @param body
	 * @param cnds
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMedicinesRequisitionDetailData(
			Map<String, Object> body, List<Object> cnds, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		try {
			StringBuffer hql_count = new StringBuffer();
			hql_count.append(" SQDH=:sqdh and SQYF=:sqyf");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("sqdh", MedicineUtils.parseInt(body.get("SQDH")));
			map_par.put("sqyf", MedicineUtils.parseLong(body.get("SQYF")));
			long l = dao.doCount("YF_DB02", hql_count.toString(), map_par);
			ret.put("totalCount", l);
			List<Map<String, Object>> list_db02 = dao.doList(cnds, null,
					"phis.application.pha.schemas.YF_DB02_LIST_CK");
			StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装等信息,判断是否改变用.
			StringBuffer hql_kcsl_kcsb = new StringBuffer();// 查询库存数量(调拨申请)
			StringBuffer hql_kcsl = new StringBuffer();// 查询库存数量(调拨退药)
			StringBuffer hql_kcjg=new StringBuffer();//查询对应的库存价格
			hql_yfbz.append("select YFBZ as YFBZ,YFDW as YFDW,YFGG as YFGG from YF_YPXX  where YFSB=:yfsb and YPXH=:ypxh");
			Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
			map_par_yfbz.put("yfsb", yfsb);
			hql_kcsl_kcsb
					.append("select YPSL as KCSL,LSJG as LSJG,JHJG as JHJG from YF_KCMX where SBXH=:kcsb");
			hql_kcsl.append("select sum(YPSL) as KCSL from YF_KCMX  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
			hql_kcjg.append("select LSJG as LSJG,JHJG as JHJG from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and round((LSJG*:qrbz)/:yfbz,4)=:lsjg  and round((JHJG*:qrbz)/:yfbz,4)=:jhjg ");
			Map<String, Object> map_par_kcsl = new HashMap<String, Object>();
			for (Map<String, Object> map_db02 : list_db02) {
				map_par_yfbz.put("ypxh",
						MedicineUtils.parseLong(map_db02.get("YPXH")));
				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
						map_par_yfbz);
				int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
				int qrbz = MedicineUtils.parseInt(map_db02.get("QRBZ"));
				if (map_db02.get("KCSB") != null
						&& MedicineUtils.parseLong(map_db02.get("KCSB")) != 0
						&& MedicineUtils.parseDouble(map_db02.get("QRSL")) > 0) {
					map_par_kcsl.put("kcsb",
							MedicineUtils.parseLong(map_db02.get("KCSB")));
					Map<String, Object> map_kcsl = dao.doLoad(
							hql_kcsl_kcsb.toString(), map_par_kcsl);
					map_db02.put(
							"KCSL",
							map_kcsl == null ? 0 : MedicineUtils
									.parseDouble(map_kcsl.get("KCSL")));
					map_db02.put("LSJG", MedicineUtils.parseDouble(map_kcsl.get("LSJG")));
					map_db02.put("JHJG", MedicineUtils.parseDouble(map_kcsl.get("JHJG")));
				} else {
					if (!map_par_kcsl.containsKey("yfsb")) {
						map_par_kcsl.put("yfsb", yfsb);
					}
					map_par_kcsl.put("ypxh",
							MedicineUtils.parseLong(map_db02.get("YPXH")));
					map_par_kcsl.put("ypcd",
							MedicineUtils.parseLong(map_db02.get("YPCD")));
					Map<String, Object> map_kcsl = dao.doLoad(
							hql_kcsl.toString(), map_par_kcsl);
					map_db02.put(
							"KCSL",
							map_kcsl == null ? 0 : MedicineUtils
									.parseDouble(map_kcsl.get("KCSL")));
					if (yfbz != qrbz) {
						map_par_kcsl.put("qrbz", qrbz);
						map_par_kcsl.put("yfbz", yfbz);
						map_par_kcsl.put("lsjg", MedicineUtils.parseDouble(map_db02.get("LSJG")));
						map_par_kcsl.put("jhjg", MedicineUtils.parseDouble(map_db02.get("JHJG")));
						List<Map<String,Object>> l_kcjg=dao.doSqlQuery(hql_kcjg.toString(), map_par_kcsl);
						if(l_kcjg!=null&&l_kcjg.size()>0){
							Map<String,Object> m_kcjg=l_kcjg.get(0);
							map_db02.put("LSJG", MedicineUtils.parseDouble(m_kcjg.get("LSJG")));
							map_db02.put("JHJG", MedicineUtils.parseDouble(m_kcjg.get("JHJG")));
						}else{
							map_db02.put(
									"LSJG",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_db02
													.get("LSJG")) * yfbz / qrbz));
							map_db02.put(
									"JHJG",
									MedicineUtils.formatDouble(
											4,
											MedicineUtils.parseDouble(map_db02
													.get("JHJG")) * yfbz / qrbz));
						}
					}
				}
				if (yfbz != qrbz) {
					map_db02.put(
							"YPSL",
							MedicineUtils.formatDouble(
									2,
									MedicineUtils.parseDouble(map_db02
											.get("YPSL")) * qrbz / yfbz));
					map_db02.put(
							"QRSL",
							MedicineUtils.formatDouble(
									2,
									MedicineUtils.parseDouble(map_db02
											.get("QRSL")) * qrbz / yfbz));
					
					map_db02.put("LSJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_db02.get("QRSL"))
									* MedicineUtils.parseDouble(map_db02
											.get("LSJG"))));
					map_db02.put("JHJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_db02.get("QRSL"))
									* MedicineUtils.parseDouble(map_db02
											.get("JHJG"))));
					map_db02.put("QRBZ", yfbz);
					map_db02.put("QRGG", map_yfbz.get("YFGG"));
					map_db02.put("QRDW", map_yfbz.get("YFDW"));
				}
			}
			ret.put("body", list_db02);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调拨数据查询失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请单保存
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMedicinesRequisition(
			Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> map_db01 = (Map<String, Object>) body.get("d01");
		List<Map<String, Object>> list_db02 = (List<Map<String, Object>>) body
				.get("d02");
		StringBuffer hql_ypxx = new StringBuffer();
		hql_ypxx.append("select YFZF as YFZF,YFBZ as YFBZ from YF_YPXX  where YPXH=:ypxh and YFSB=:yfsb ");
		try {
			for (Map<String, Object> map_db02 : list_db02) {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("ypxh",
						MedicineUtils.parseLong(map_db02.get("YPXH")));
				map_par.put("yfsb", yfsb);
				Map<String, Object> map_ypxx = dao.doLoad(hql_ypxx.toString(),
						map_par);
				if (map_ypxx == null || map_ypxx.size() == 0) {
					return MedicineUtils.getRetMap(map_db02.get("YPMC")
							+ "在本药房没有药品信息!");
				}
				if (MedicineUtils.parseInt(map_ypxx.get("YFZF")) == 1) {
					return MedicineUtils.getRetMap("药品已作废,不能调拨!");
				}
			}
			int dbdh = MedicineUtils.parseInt(map_db01.get("SQDH"));
			if ("create".equals(op)) {
				StringBuffer hql_dbdh = new StringBuffer();
				hql_dbdh.append("select max(SQDH) as SQDH from YF_DB01 where SQYF=:yfsb");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yfsb", yfsb);
				List<Map<String, Object>> list_dbdh = dao.doSqlQuery(
						hql_dbdh.toString(), map_par);
				dbdh = 1;
				if (list_dbdh != null && list_dbdh.size() != 0
						&& list_dbdh.get(0) != null
						&& list_dbdh.get(0).size() != 0
						&& list_dbdh.get(0).get("SQDH") != null) {
					dbdh = MedicineUtils.parseInt(list_dbdh.get(0).get("SQDH")) + 1;
				}
				map_db01.put("CZGH", user.getUserId());
				map_db01.put("SQDH", dbdh);
				map_db01.put("SQYF", yfsb);
				map_db01.put("JGID", jgid);
				dao.doSave("create", BSPHISEntryNames.YF_DB01, map_db01, false);
			} else {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yfsb",
						MedicineUtils.parseLong(map_db01.get("SQYF")));
				map_par.put("sqdh", dbdh);
				if ("update".equals(op)) {
					StringBuffer hql_tjbz = new StringBuffer();
					hql_tjbz.append("select TJBZ as TJBZ from YF_DB01  where SQDH=:sqdh and SQYF=:yfsb");
					Map<String, Object> map_tjbz = dao.doLoad(
							hql_tjbz.toString(), map_par);
					if (MedicineUtils.parseInt(map_tjbz.get("TJBZ")) == 1) {
						return MedicineUtils.getRetMap("该调拨单已提交,请刷新后重试!");
					}
				}
				StringBuffer hql_delete = new StringBuffer();
				hql_delete
						.append("delete from YF_DB02  where SQDH=:sqdh and SQYF=:yfsb");
				dao.doUpdate(hql_delete.toString(), map_par);
				dao.doSave("update", BSPHISEntryNames.YF_DB01, map_db01, false);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (Map<String, Object> map_db02 : list_db02) {
				map_db02.put("SQYF",
						MedicineUtils.parseLong(map_db01.get("SQYF")));
				map_db02.put("JGID", jgid);
				map_db02.put("SQDH", dbdh);
				if (map_db02.get("YPXQ") != null
						&& !"".equals(map_db02.get("YPXQ"))) {
					map_db02.put("YPXQ", sdf.parse(map_db02.get("YPXQ") + ""));
				}
				dao.doSave("create", BSPHISEntryNames.YF_DB02, map_db02, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调拨申请单保存失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "调拨申请单保存失败!", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "调拨申请单保存失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请确认
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveRequisitionCommit(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> map_db01 = (Map<String, Object>) body.get("d01");
		List<Map<String, Object>> list_db02 = (List<Map<String, Object>>) body
				.get("d02");
		int sqdh = MedicineUtils.parseInt(map_db01.get("SQDH"));
		long sqyf = MedicineUtils.parseLong(map_db01.get("SQYF"));
		StringBuffer hql = new StringBuffer();
		hql.append("SQDH=:sqdh and SQYF=:sqyf and RKBZ=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", sqdh);
		map_par.put("sqyf", sqyf);
		try {
			long l = dao.doCount("YF_DB01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已确认入库,不能重复操作!已刷新窗口");
			}
			StringBuffer hql_yfbz = new StringBuffer();
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX  where YFSB=:yfsb and YPXH=:ypxh");
			StringBuffer hql_kc_update = new StringBuffer();
			hql_kc_update
					.append("update YF_KCMX  set YPSL=YPSL+:ypsl,LSJE=LSJE+round(LSJG*:ypsl,2),PFJE=PFJE+round(PFJG*:ypsl,2),JHJE=JHJE+round(JHJG*:ypsl,2) where SBXH=:sbxh");
			StringBuffer hql_db02_update = new StringBuffer();
			hql_db02_update
					.append("update YF_DB02  set DRKCSB=:kcsb where SBXH=:sbxh");
			PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
					dao);
			model.synchronousPrimaryKey(ctx);// 保存库存前,同步库存表主键
			for (Map<String, Object> map_db02 : list_db02) {
				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
				map_par_yfbz.put("yfsb", yfsb);
				map_par_yfbz.put("ypxh",
						MedicineUtils.parseLong(map_db02.get("YPXH")));
				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
						map_par_yfbz);
				int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
				int qrbz = MedicineUtils.parseInt(map_db02.get("QRBZ"));
				if (yfbz != qrbz) {
					map_db02.put(
							"QRSL",
							MedicineUtils.formatDouble(
									2,
									MedicineUtils.parseDouble(map_db02
											.get("QRSL")) * qrbz / yfbz));
					map_db02.put(
							"LSJG",
							MedicineUtils.formatDouble(
									4,
									MedicineUtils.parseDouble(map_db02
											.get("LSJG")) * yfbz / qrbz));
					map_db02.put(
							"JHJG",
							MedicineUtils.formatDouble(
									4,
									MedicineUtils.parseDouble(map_db02
											.get("JHJG")) * yfbz / qrbz));
					map_db02.put("LSJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_db02.get("QRSL"))
									* MedicineUtils.parseDouble(map_db02
											.get("LSJG"))));
					map_db02.put("JHJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_db02.get("QRSL"))
									* MedicineUtils.parseDouble(map_db02
											.get("JHJG"))));
					map_db02.put("QRBZ", qrbz);
				}
				StringBuffer kcHql = new StringBuffer();
				Map<String, Object> parameter = new HashMap<String, Object>();
				kcHql.append(" select SBXH as SBXH from YF_KCMX  where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and round((LSJG*:qrbz)/:yfbz,4)=:lsjg  and round((JHJG*:qrbz)/:yfbz,4)=:jhjg ");
				if (map_db02.get("YPXQ") == null) {
					kcHql.append(" and YPXQ is null ");
				} else {
					kcHql.append(" and to_char(YPXQ,'yyyy-mm-dd')=:ypxq");
					parameter.put("ypxq",
							(map_db02.get("YPXQ") + "").split(" ")[0]);
				}
				if (map_db02.get("YPPH") == null
						|| "".equals(map_db02.get("YPPH"))) {
					kcHql.append(" and YPPH is null ");
				} else {
					kcHql.append(" and YPPH=:ypph");
					parameter.put("ypph", map_db02.get("YPPH"));
				}
				parameter.put("lsjg", map_db02.get("LSJG") == null ? 0
						: MedicineUtils.parseDouble(map_db02.get("LSJG")));
//				parameter.put("pfjg", map_db02.get("PFJG") == null ? 0
//						: MedicineUtils.parseDouble(map_db02.get("PFJG")));
				parameter.put("jhjg", map_db02.get("JHJG") == null ? 0
						: MedicineUtils.parseDouble(map_db02.get("JHJG")));
				parameter.put("yfsb", yfsb);
				parameter.put("ypxh",
						MedicineUtils.parseLong(map_db02.get("YPXH")));
				parameter.put("ypcd",
						MedicineUtils.parseLong(map_db02.get("YPCD")));
				parameter.put("qrbz", qrbz);
				parameter.put("yfbz", yfbz);
				Map<String, Object> kc = null;
				List<Map<String, Object>> kcs = dao.doSqlQuery(kcHql.toString(),
						parameter);
				if (kcs != null && kcs.size() > 0) {
					kc = kcs.get(0);
				}
				parameter.clear();
				Map<String, Object> map_par_db02_update = new HashMap<String, Object>();
				map_par_db02_update.put("sbxh",
						MedicineUtils.parseLong(map_db02.get("SBXH")));
				if (kc != null) {
					parameter.put("ypsl",
							MedicineUtils.parseDouble(map_db02.get("QRSL")));
//					parameter.put("lsje",
//							MedicineUtils.parseDouble(map_db02.get("LSJE")));
//					parameter.put("pfje",
//							MedicineUtils.parseDouble(map_db02.get("PFJE")));
//					parameter.put("jhje",
//							MedicineUtils.parseDouble(map_db02.get("JHJE")));
					parameter.put("sbxh", kc.get("SBXH"));
					dao.doSqlUpdate(hql_kc_update.toString(), parameter);
					map_par_db02_update.put("kcsb", kc.get("SBXH"));
				} else {
					map_db02.put("YPSL",
							MedicineUtils.parseDouble(map_db02.get("QRSL")));
					map_db02.put("YFSB", yfsb);
					map_db02.put("JYBZ", 0);
					map_db02.put("CKBH", 0);
					map_db02.put("YKLJ", new Double(0));
					map_db02.put("YKJJ", new Double(0));
					map_db02.put("YKPJ", new Double(0));
					Map<String, Object> map_kc = dao.doSave("create",
							BSPHISEntryNames.YF_KCMX_CSH, map_db02, false);
					map_par_db02_update.put("kcsb", map_kc.get("SBXH"));
				}
				dao.doUpdate(hql_db02_update.toString(), map_par_db02_update);
			}
			hql = new StringBuffer();
			hql.append("update YF_DB01  set RKBZ=1,RKGH=:rkgh,RKRQ=:rkrq where SQDH=:sqdh and SQYF=:sqyf");
			map_par.clear();
			map_par.put("rkgh", user.getUserId());
			map_par.put("rkrq", new Date());
			map_par.put("sqdh", sqdh);
			map_par.put("sqyf", sqyf);
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "确认失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "确认失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨申请查询当前药房药品库存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryKcsl_yfdbsq(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql_ypxx = new StringBuffer();
		hql_ypxx.append("select YFZF as YFZF,YFBZ as YFBZ from YF_YPXX where YPXH=:ypxh and YFSB=:yfsb ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("yfsb", yfsb);
		try {
			Map<String, Object> map_ypxx = dao.doLoad(hql_ypxx.toString(),
					map_par);
			if (map_ypxx == null || map_ypxx.size() == 0) {
				return MedicineUtils
						.getRetMap(body.get("YPMC") + "在本药房没有药品信息!");
			}
			if (MedicineUtils.parseInt(map_ypxx.get("YFZF")) == 1) {
				return MedicineUtils.getRetMap("药品已作废,不能调拨!");
			}
			int yfbz = MedicineUtils.parseInt(map_ypxx.get("YFBZ"));
			int mbbz = MedicineUtils.parseInt(body.get("MBBZ"));
			StringBuffer hql_yfkc = new StringBuffer();
			if (body.containsKey("KCSB")
					&& MedicineUtils.parseLong(body.get("KCSB")) != 0) {
				hql_yfkc.append("select YPSL as YPSL from YF_KCMX where SBXH=:sbxh");
				map_par.clear();
				map_par.put("sbxh", MedicineUtils.parseLong(body.get("KCSB")));
			} else {
				hql_yfkc.append("select sum(YPSL) as YPSL from YF_KCMX  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
				map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
			}
			List<Map<String, Object>> list_kc = dao.doSqlQuery(
					hql_yfkc.toString(), map_par);
			if (list_kc == null || list_kc.size() == 0
					|| list_kc.get(0) == null || list_kc.get(0).size() == 0
					|| list_kc.get(0).get("YPSL") == null) {
				//return MedicineUtils.getRetMap("药品无库存,不能调拨!");
				ret.put("KCSL",0);
			}else{
				ret.put("KCSL",
						MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(list_kc.get(0)
										.get("YPSL")) * yfbz / mbbz));
			}
			ret.put("code", 200);
			ret.put("msg", "ok");
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息查询失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 查询目标药房药品数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryTarHouseStore(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql_mbkc = new StringBuffer();
		hql_mbkc.append("select sum(YPSL) as YPSL from YF_KCMX  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("MBYF")));
		try {
			List<Map<String, Object>> list_kc = dao.doSqlQuery(
					hql_mbkc.toString(), map_par);
			if (list_kc == null || list_kc.size() == 0
					|| list_kc.get(0) == null || list_kc.get(0).size() == 0
					|| list_kc.get(0).get("YPSL") == null) {
				return MedicineUtils.getRetMap("目标药房无库存,不能申请调拨!");
			}
			ret.put("code", 200);
			ret.put("msg", "ok");
			ret.put("KCSL", list_kc.get(0).get("YPSL"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "目标药房库存信息查询失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨确认界面打开前判断是否已经确认
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationDeployInventorySubmit(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" SQDH=:sqdh and SQYF=:yfsb and CKBZ=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", MedicineUtils.parseInt(body.get("SQDH")));
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("SQYF")));
		try {
			long l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("数据已确认,已刷新页面!");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "验证是否确认失败!", e);
		}

		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨出库退回
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDeployInventoryBack(Map<String, Object> body)
			throws ModelDataOperationException {
		long sqyf = MedicineUtils.parseLong(body.get("SQYF"));
		int sqdh = MedicineUtils.parseInt(body.get("SQDH"));
		StringBuffer hql = new StringBuffer();
		hql.append(" SQYF=:sqyf and SQDH=:sqdh and TYPB=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqyf", sqyf);
		map_par.put("sqdh", sqdh);
		try {
			long l = dao.doCount("YF_DB01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("本调拨单为退药单不能退回操作!");
			}
			hql = new StringBuffer();
			hql.append(" SQYF=:sqyf and SQDH=:sqdh and CKBZ=1");
			l = dao.doCount("YF_DB01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("本调拨单已经出库，不能退回!");
			}
			hql = new StringBuffer();
			hql.append("update YF_DB01  set  TJBZ = 0 where SQDH=:sqdh and SQYF=:sqyf ");
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			logger.error("back failure", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "退回失败!");
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨出库确认 设计上是写 出库的时候
	 *              当前KCSB的数量不够要去同种价格的药减库存,由于当前程序写的是同一产地的药品只有一个库存的
	 *              (特殊情况,药房发完药,再申领退药不考虑),所以暂时kcsb对应 的没数量就终止
	 * @updateInfo update by caijy at 2016年4月28日 16:36:37 for 调拨退药 出库确认时 界面显示金额是负 现在存到数据库也是减 实际应该是加上正的金额
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveDeployInventoryCommit(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> map_db01 = (Map<String, Object>) body.get("d01");
		List<Map<String, Object>> list_db02 = (List<Map<String, Object>>) body
				.get("d02");
		int sqdh = MedicineUtils.parseInt(map_db01.get("SQDH"));
		long sqyf = MedicineUtils.parseLong(map_db01.get("SQYF"));
		StringBuffer hql = new StringBuffer();
		hql.append("SQDH=:sqdh and SQYF=:sqyf and CKBZ=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("sqdh", sqdh);
		map_par.put("sqyf", sqyf);
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
				dao);
		try {
			long l = dao.doCount("YF_DB01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已确认出库,不能重复操作!已刷新窗口");
			}
			// 调拨申请数据处理
			if (MedicineUtils.parseInt(map_db01.get("TYPB")) != 1) {
				hql = new StringBuffer();
				hql.append("select b.YPMC as YPMC from YK_TYPK b,YF_YPXX a  where a.YPXH=:ypxh and a.YFSB=:yfsb and a.YFZF=1");
				map_par.clear();
				map_par.put("yfsb", sqyf);
				List<Map<String, Object>> list_kc = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map_db02 : list_db02) {
					map_par.put("ypxh",
							MedicineUtils.parseLong(map_db02.get("YPXH")));
					Map<String, Object> map_yp = dao.doLoad(hql.toString(),
							map_par);
					if (map_yp != null && map_yp.size() > 0) {
						return MedicineUtils.getRetMap("目标药房已作废:"
								+ map_yp.get("YPMC") + ",不能确认出库!");
					}
					if (MedicineUtils.parseDouble(map_db02.get("QRSL")) == 0) {
						continue;
					}
					Map<String, Object> map_kc = new HashMap<String, Object>();
					map_kc.put("KCSB",
							MedicineUtils.parseLong(map_db02.get("KCSB")));
					map_kc.put("YPSL",
							MedicineUtils.parseDouble(map_db02.get("QRSL")));
					list_kc.add(map_kc);
				}
				// 确认前先进行数据的保存
				ret = saveMedicinesRequisition(body, "commit", ctx);
				if (MedicineUtils.parseInt(ret.get("code")) != 200) {
					return ret;
				}
				if (list_kc.size() != 0) {
					ret = model.lessInventory(list_kc, ctx);
					if (MedicineUtils.parseInt(ret.get("code")) != 200) {
						return MedicineUtils
								.getRetMap(ret.get("ypmc") + "库存不足");
					}
				}
			} else {
				// 调拨退药数据处理
				StringBuffer hql_kc_update = new StringBuffer();
				hql_kc_update
						.append("update YF_KCMX  set YPSL=YPSL+:ypsl,LSJE=LSJE+:lsje,PFJE=PFJE+:pfje,JHJE=JHJE+:jhje where SBXH=:sbxh");
				model.synchronousPrimaryKey(ctx);// 库存保存前同步库存表主键
				for (Map<String, Object> map_db02 : list_db02) {
					StringBuffer kcHql = new StringBuffer();
					Map<String, Object> parameter = new HashMap<String, Object>();
					kcHql.append(" select SBXH as SBXH from YF_KCMX  where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and PFJG=:pfjg and JHJG=:jhjg ");
					if (map_db02.get("YPXQ") == null) {
						kcHql.append(" and YPXQ is null ");
					} else {
						kcHql.append(" and to_char(YPXQ,'yyyy-mm-dd')=:ypxq");
						parameter.put("ypxq",
								(map_db02.get("YPXQ") + "").split(" ")[0]);
					}
					if (map_db02.get("YPPH") == null
							|| "".equals(map_db02.get("YPPH"))) {
						kcHql.append(" and YPPH is null ");
					} else {
						kcHql.append(" and YPPH=:ypph");
						parameter.put("ypph", map_db02.get("YPPH"));
					}
					parameter.put("lsjg", map_db02.get("LSJG") == null ? 0
							: MedicineUtils.parseDouble(map_db02.get("LSJG")));
					parameter.put("pfjg", map_db02.get("PFJG") == null ? 0
							: MedicineUtils.parseDouble(map_db02.get("PFJG")));
					parameter.put("jhjg", map_db02.get("JHJG") == null ? 0
							: MedicineUtils.parseDouble(map_db02.get("JHJG")));
					parameter.put("yfsb", yfsb);
					parameter.put("ypxh",
							MedicineUtils.parseLong(map_db02.get("YPXH")));
					parameter.put("ypcd",
							MedicineUtils.parseLong(map_db02.get("YPCD")));
					Map<String, Object> kc = null;
					List<Map<String, Object>> kcs = dao.doQuery(
							kcHql.toString(), parameter);
					if (kcs != null && kcs.size() > 0) {
						kc = kcs.get(0);
					}
					parameter.clear();
					if (kc != null) {
						parameter.put("ypsl", -MedicineUtils
								.parseDouble(map_db02.get("QRSL")));
						parameter
								.put("lsje", -MedicineUtils.parseDouble(map_db02
										.get("LSJE")));
						parameter
								.put("pfje", -MedicineUtils.parseDouble(map_db02
										.get("PFJE")));
						parameter
								.put("jhje", -MedicineUtils.parseDouble(map_db02
										.get("JHJE")));
						parameter.put("sbxh", kc.get("SBXH"));
						dao.doUpdate(hql_kc_update.toString(), parameter);
					} else {
						map_db02.put("YPSL", -MedicineUtils
								.parseDouble(map_db02.get("QRSL")));
						map_db02.put("YFSB", yfsb);
						map_db02.put("JYBZ", 0);
						map_db02.put("CKBH", 0);
						map_db02.put("LSJE", -MedicineUtils.parseDouble(map_db02
										.get("LSJE")));
						map_db02.put("JHJE", -MedicineUtils.parseDouble(map_db02
								.get("JHJE")));
						map_db02.put("YKLJ", new Double(0));
						map_db02.put("YKJJ", new Double(0));
						map_db02.put("YKPJ", new Double(0));
						dao.doSave("create", BSPHISEntryNames.YF_KCMX_CSH,
								map_db02, false);
					}
				}
			}
			hql = new StringBuffer();
			hql.append("update YF_DB01  set CKBZ=1,CKGH=:ckgh,CKRQ=:ckrq where SQDH=:sqdh and SQYF=:sqyf");
			map_par.clear();
			map_par.put("ckgh", user.getUserId());
			map_par.put("ckrq", new Date());
			map_par.put("sqdh", sqdh);
			map_par.put("sqyf", sqyf);
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "确认失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "确认失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 调拨退药确认
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveInventoryBackCommit(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> map_db01 = (Map<String, Object>) body.get("d01");
		List<Map<String, Object>> list_db02 = (List<Map<String, Object>>) body
				.get("d02");
		List<Map<String, Object>> list_kc = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map_db02 : list_db02) {
			Map<String, Object> map_kc = new HashMap<String, Object>();
			map_kc.put("KCSB", MedicineUtils.parseLong(map_db02.get("KCSB")));
			map_kc.put("YPSL", -MedicineUtils.parseDouble(map_db02.get("YPSL")));
			list_kc.add(map_kc);
		}
		PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
				dao);
		ret = model.lessInventory(list_kc, ctx);
		if (MedicineUtils.parseInt(ret.get("code")) != 200) {
			return MedicineUtils.getRetMap(ret.get("ypmc") + "库存不足");
		}
		StringBuffer hql = new StringBuffer();
		hql.append("update YF_DB01  set RKBZ=1,RKGH=:rkgh,RKRQ=:rkrq,TJBZ=1 where SQDH=:sqdh and SQYF=:sqyf");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("rkgh", user.getUserId());
		map_par.put("rkrq", new Date());
		map_par.put("sqdh", MedicineUtils.parseInt(map_db01.get("SQDH")));
		map_par.put("sqyf", MedicineUtils.parseLong(map_db01.get("SQYF")));
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "确认失败!", e);
		}
		return ret;
	}

}
