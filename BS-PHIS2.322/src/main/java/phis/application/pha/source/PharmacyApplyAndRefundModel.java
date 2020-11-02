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
import ctd.account.organ.OrganController;
import ctd.controller.exception.ControllerException;
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
 * 药房申领和退药model
 * @author Administrator
 *
 */
public class PharmacyApplyAndRefundModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyApplyAndRefundModel.class);

	public PharmacyApplyAndRefundModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 药房已申领和未申领数据查询
	 * @updateInfo
	 * @param body
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryMedicinesApply(Map<String, Object> body,
			Map<String, Object> req, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		StringBuffer hql_ckfs = new StringBuffer();
		if (body.get("yksb") == null || MedicineUtils.parseLong(body.get("yksb")) == 0) {
			return ret;
		}
		try {
			long yksb = MedicineUtils.parseLong(body.get("yksb"));
			UserRoleToken user = UserRoleToken.getCurrent();;
			long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			hql.append(
					"select a.SQRQ as SQRQ,a.CKFS as CKFS,a.LYPB as LYPB,a.CKDH as CKDH ,a.XTSB as XTSB,a.CKRQ as CKRQ,a.CKBZ as CKBZ,(a.SQTJ+a.CKPB+1) as CZPB,a.LYRQ as LYRQ,a.JGID as JGID,sum(b.JHJE) as JHJE,sum(b.LSJE) as LSJE from YK_CK01 a,YK_CK02 b where a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and a.XTSB=:xtsb and a.CKFS=:ckfs and ")
					.append(ExpressionProcessor.instance().toString((List<Object>) body.get("cnd")).replaceAll("str", "to_char"))
					.append(" group by a.SQRQ,a.CKFS,a.CKDH,a.CKRQ,a.CKBZ,(a.SQTJ+a.CKPB+1),a.LYRQ,a.JGID,a.XTSB,a.LYPB order by a.CKDH desc");
			hql_ckfs.append("select b.LYFS as CKFS from YK_YKLB a,YF_LYFS  b where a.YKSB=b.YKSB and a.JGID=b.JGID and b.YFSB=:yfsb  and a.YKSB=:yksb");
			Map<String, Object> map_par_ckfs = new HashMap<String, Object>();
			map_par_ckfs.put("yfsb", yfsb);
			map_par_ckfs.put("yksb", yksb);
			Map<String, Object> map_ckfs = dao.doLoad(hql_ckfs.toString(),
					map_par_ckfs);
			if (map_ckfs == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "领药方式查询失败,请关闭模块重新打开");
			}
			int ckfs = MedicineUtils.parseInt(map_ckfs.get("CKFS"));
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("xtsb", yksb);
			map_par.put("ckfs", ckfs);
			MedicineCommonModel model=new MedicineCommonModel(dao);
			ret=model.getPageInfoRecord(req, map_par, hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房领药数据查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "药房领药数据查询失败", e);
		}

		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 修改确认前判断领药单是否被删除和确认
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationApplyDelete(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb ");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
			map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
			map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
			long l = dao.doCount("YK_CK01", hql.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("领药单已经被删除!",9000);
			}
			hql.append(" and LYPB=1");
			l = dao.doCount("YK_CK01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("领药单已经被确认!",9000);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "判断是否已经删除和确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 删除领药单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeApplyData(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh and SQTJ=0 ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
		try {
			long l = dao.doCount("YK_CK01", hql.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("数据已发生变化，不能删除该单据!",9000);
			}
			hql = new StringBuffer();
			hql.append("delete from YK_CK02  where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh");
			dao.doUpdate(hql.toString(), map_par);
			hql = new StringBuffer();
			hql.append("delete from YK_CK01 where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh");
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药单删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 领药单提交
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void updateApplyData(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("update YK_CK01  set SQTJ = 1 where XTSB=:xtsb and CKDH=:ckdh and CKFS=:ckfs and SQTJ=0");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
		map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
		map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药单提交失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 查询领药库房
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<List<Object>> queryStorehouse(Context ctx)
			throws ModelDataOperationException {
		List<List<Object>> ret = new ArrayList<List<Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String organId = UserRoleToken.getCurrent().getOrganId();
		StringBuffer hql = new StringBuffer();
		hql.append("select a.YKSB as YKSB,a.YKMC as YKMC,a.JGID as JGID from YK_YKLB a,YF_LYFS  b where a.YKSB=b.YKSB and a.JGID=b.JGID and b.YFSB=:yfsb  and a.SYBZ=2 and b.LYFS>0 order by a.JGID desc");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		try {
			List<Map<String, Object>> list_yklb = dao.doQuery(hql.toString(),
					map_par);
			for (Map<String, Object> map_yklb : list_yklb) {
				List<Object> l = new ArrayList<Object>();
				l.add(MedicineUtils.parseLong(map_yklb.get("YKSB")));
				l.add(map_yklb.get("YKMC")
						+ "("
						+OrganController.lookupManageUnit(MedicineUtils.parseString(map_yklb.get("JGID")),organId).getName()
						+ ")");
				ret.add(l);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药库房查询失败", e);
		} catch (ControllerException e) {
			MedicineUtils.throwsException(logger, "领药库房查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-10
	 * @description 查询领药方式
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryCkfs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		long yksb = MedicineUtils.parseLong(body.get("yksb"));
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		StringBuffer hql = new StringBuffer();
		hql.append("select LYFS as LYFS from YF_LYFS where YKSB=:yksb and YFSB=:yfsb ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("yksb", yksb);
		int lyfs=0;
		try {
			Map<String, Object> map_lyfs = dao.doLoad(hql.toString(), map_par);
			lyfs=MedicineUtils.parseInt(map_lyfs.get("LYFS"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药方式查询失败", e);
		}
		return lyfs;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-13
	 * @description 药品领药单保存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMedicinesApply(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list_ck02 = (List<Map<String, Object>>) body
				.get("YK_CK02");
		Map<String, Object> map_ck01 = (Map<String, Object>) body
				.get("YK_CK01");
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		long yksb = MedicineUtils.parseLong(map_ck01.get("XTSB"));
		String op = body.get("op") + "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String jgid = getJGIDByYKSB(yksb);
			if ("create".equals(op)) {
				StringBuffer hql_ckdh = new StringBuffer();
				StringBuffer hql_ckdh_update = new StringBuffer();
				hql_ckdh.append("select CKDH as CKDH from YK_CKFS  where CKFS=:ckfs and XTSB=:xtsb");
				hql_ckdh_update
						.append("update YK_CKFS set CKDH=CKDH+1 where CKFS=:ckfs and XTSB=:xtsb");
				Map<String, Object> map_par_ckdh = new HashMap<String, Object>();
				map_par_ckdh.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
				map_par_ckdh.put("xtsb", yksb);
				Map<String, Object> map_ckdh = dao.doLoad(hql_ckdh.toString(),
						map_par_ckdh);
				int ckdh = MedicineUtils.parseInt(map_ckdh.get("CKDH"));
				map_ck01.put("CKDH", ckdh);
				map_ck01.put("YFSB", yfsb);
				map_ck01.put("JGID", jgid);
				map_ck01.put("CZGH", MedicineUtils.parseString(user.getUserId()));
				dao.doSave("create", BSPHISEntryNames.YK_CK01_FORM, map_ck01,
						false);
				for (Map<String, Object> map_ck02 : list_ck02) {
					map_ck02.put("CKDH", ckdh);
					map_ck02.put("CKFS", MedicineUtils.parseInt(map_ck01.get("CKFS")));
					map_ck02.put("XTSB", yksb);
					map_ck02.put("JGID", jgid);
					if (map_ck02.get("YPXQ") != null
							&& map_ck02.get("YPXQ") != "") {
						map_ck02.put("YPXQ",
								sdf.parse(map_ck02.get("YPXQ") + ""));
					}
					// map_ck02.put("SFSL", parseDouble(map_ck02.get("SQSL")));
					dao.doSave("create", BSPHISEntryNames.YK_CK02_SLTY_LIST,
							map_ck02, false);
				}
				dao.doUpdate(hql_ckdh_update.toString(), map_par_ckdh);
			} else {
				StringBuffer hql_ck02_delete = new StringBuffer();
				hql_ck02_delete
						.append("delete from YK_CK02  where CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
				map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
				map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
				Map<String, Object> r = verificationStorehouseCheckOutDelete(map_par);
				if (MedicineUtils.parseInt(r.get("code")) != 200) {
					return r;
				}
				dao.doUpdate(hql_ck02_delete.toString(), map_par);
				for (Map<String, Object> map_ck02 : list_ck02) {
					map_ck02.put("CKDH", MedicineUtils.parseInt(map_ck01.get("CKDH")));
					map_ck02.put("CKFS", MedicineUtils.parseInt(map_ck01.get("CKFS")));
					map_ck02.put("XTSB", yksb);
					map_ck02.put("JGID", jgid);
					// map_ck02.put("SFSL", parseDouble(map_ck02.get("SQSL")));
					dao.doSave("create", BSPHISEntryNames.YK_CK02_LIST,
							map_ck02, false);
				}
				dao.doSave("update", BSPHISEntryNames.YK_CK01_FORM, map_ck01,
						false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "领药单保存失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "药品效期有问题", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-13
	 * @description  根据YKSB查询JGID(跨机构申领和退药用)
	 * @updateInfo
	 * @param yksb
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getJGIDByYKSB(long yksb) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select JGID as JGID from YK_YKLB  where YKSB=:yksb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		String jgid = "";
		try {
			jgid = dao.doLoad(hql.toString(), map_par).get("JGID") + "";
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "获取JGID失败", e);
		}
		return jgid;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-13
	 * @description 申领单确认前判断药库出库单是否删除或确认掉了
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verificationStorehouseCheckOutDelete(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb ");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ckdh", MedicineUtils.parseInt(body.get("ckdh")));
			map_par.put("ckfs", MedicineUtils.parseInt(body.get("ckfs")));
			map_par.put("xtsb", MedicineUtils.parseLong(body.get("xtsb")));
			long l = dao.doCount("YK_CK01", hql.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("出库单已经被删除!",9000);
			}
			hql.append(" and (CKPB=1 or LYPB=1 or SQTJ=1)");
			l = dao.doCount("YK_CK01", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("出库单已经被确认!",9000);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "判断是否已经删除和确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 领药单确认
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMedicinesApplyCommit(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_ck02 = (List<Map<String, Object>>) body
				.get("YK_CK02");
		Map<String, Object> map_ck01 = (Map<String, Object>) body
				.get("YK_CK01");
		Date nowDate = new Date();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String userid = MedicineUtils.parseString(user.getUserId());// 用户ID
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb and LYPB=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
		map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
		map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
		try {
			long l = dao.doCount("YK_CK01",
					hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已确认入库,不能重复操作!请刷新窗口!");
			}
			StringBuffer hql_bz = new StringBuffer();// 查询包装
			hql_bz.append("select a.ZXBZ as ZXBZ,b.YFBZ as YFBZ from YK_TYPK a,YF_YPXX b where a.YPXH=b.YPXH and b.YPXH=:ypxh and b.YFSB=:yfsb");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//主键改为取序列了，下面两句没啥用注销
//			PharmacyInventoryManageModel model=new PharmacyInventoryManageModel(dao);
//			model.synchronousPrimaryKey(ctx);//保存库存前先同步药房库存表主键
			for (Map<String, Object> map_ck02 : list_ck02) {
				if(MedicineUtils.parseDouble(map_ck02.get("SFSL"))==0){//update by caijy for 实发数量是0的数据 不进行库存加减
					continue;
				}
				map_par.clear();
				map_par.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
				map_par.put("yfsb", yfsb);
				StringBuffer hql_kc = new StringBuffer();// 查询库存
				hql_kc.append("select SBXH as SBXH from YF_KCMX  where YPXH=:ypxh and LSJG=:lsjg and JHJG=:jhjg and PFJG=:pfjg and YPCD=:ypcd and YFSB=:yfsb");
				Map<String, Object> map_bz = dao.doLoad(hql_bz.toString(),
						map_par);
				if(map_bz==null){
					return MedicineUtils.getRetMap("未查询到药品信息（"+ map_ck02.get("YPMC").toString()+"）："+hql_bz.toString().replaceAll(":ypxh", map_ck02.get("YPXH").toString()).replaceAll(":yfsb", yfsb+""));
				}
				if (map_ck02.get("YPPH") == null
						|| "".equals(map_ck02.get("YPPH"))) {
					hql_kc.append(" and YPPH is null");
				} else {
					hql_kc.append(" and YPPH=:ypph");
					map_par.put("ypph", map_ck02.get("YPPH"));
				}
				if (map_ck02.get("YPXQ") == null
						|| "".equals(map_ck02.get("YPXQ"))) {
					hql_kc.append(" and YPXQ is null");
				} else {
					hql_kc.append(" and YPXQ=:ypxq");
					map_par.put("ypxq", sdf.parse(map_ck02.get("YPXQ") + ""));
				}
				double zxbz = MedicineUtils.parseDouble(map_bz.get("ZXBZ"));
				double yfbz = MedicineUtils.parseDouble(map_bz.get("YFBZ"));
				map_par.put("ypcd", MedicineUtils.parseLong(map_ck02.get("YPCD")));
				map_par.put(
						"lsjg",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("LSJG"))
								* (yfbz / zxbz)));
				map_par.put(
						"jhjg",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("JHJG"))
								* (yfbz / zxbz)));
				map_par.put(
						"pfjg",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("PFJG"))
								* (yfbz / zxbz)));
				List<Map<String, Object>> list_kc = dao.doQuery(
						hql_kc.toString(), map_par);
				Map<String, Object> map_kc = null;
				if (list_kc != null && list_kc.size() > 0) {
					map_kc = dao.doLoad(BSPHISEntryNames.YF_KCMX,
							MedicineUtils.parseLong(list_kc.get(0).get("SBXH")));
					map_kc.put(
							"YPSL",
							MedicineUtils.parseDouble(map_kc.get("YPSL"))
									+ MedicineUtils.formatDouble(4,
											MedicineUtils.parseDouble(map_ck02.get("SFSL"))
													* (zxbz / yfbz)));
					map_kc.put("LSJE", MedicineUtils.parseDouble(map_kc.get("LSJE"))
							+ MedicineUtils.parseDouble(map_ck02.get("LSJE")));
					map_kc.put("PFJE", MedicineUtils.parseDouble(map_kc.get("PFJE"))
							+ MedicineUtils.parseDouble(map_ck02.get("PFJE")));
					map_kc.put("JHJE", MedicineUtils.parseDouble(map_kc.get("JHJE"))
							+ MedicineUtils.parseDouble(map_ck02.get("JHJE")));
					dao.doSave("update", BSPHISEntryNames.YF_KCMX, map_kc,
							false);
				} else {
					map_kc = new HashMap<String, Object>();
					// for (String key : map_ck02.keySet()) {
					// map_kc.put(key, map_ck02.get(key));
					// }
					// map_kc.remove("SBXH");
					map_kc.put("YPCD", MedicineUtils.parseLong(map_ck02.get("YPCD")));
					map_kc.put(
							"LSJG",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("LSJG"))
									* (yfbz / zxbz)));
					map_kc.put("LSJE", MedicineUtils.parseDouble(map_ck02.get("LSJE")));
					map_kc.put(
							"YPSL",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("SFSL"))
									* (zxbz / yfbz)));
					map_kc.put(
							"JHJG",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("JHJG"))
									* (yfbz / zxbz)));
					map_kc.put("JHJE", MedicineUtils.parseDouble(map_ck02.get("JHJE")));
					map_kc.put(
							"PFJG",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ck02.get("PFJG"))
									* (yfbz / zxbz)));
					map_kc.put("PFJE", MedicineUtils.parseDouble(map_ck02.get("PFJE")));
					map_kc.put("JGID", jgid);
					map_kc.put("YFSB", yfsb);
					map_kc.put("CKBH", 0);
					map_kc.put("YPXH", MedicineUtils.parseLong(map_ck02.get("YPXH")));
					map_kc.put("YKLJ", MedicineUtils.parseDouble(map_ck02.get("LSJG")));
					map_kc.put("YKJJ", MedicineUtils.parseDouble(map_ck02.get("JHJG")));
					map_kc.put("YKPJ", MedicineUtils.parseDouble(map_ck02.get("PFJG")));
					map_kc.put("JYBZ", 0);
					map_kc.put("YKKCSB", MedicineUtils.parseLong(map_ck02.get("KCSB")));
					map_kc.put("YPPH", map_ck02.get("YPPH") + "");
					map_kc.put("YPXQ", map_ck02.get("YPXQ") == null ? null
							: sdf.parse(map_ck02.get("YPXQ") + ""));
					dao.doSave("create", BSPHISEntryNames.YF_KCMX_CSH, map_kc,
							false);
				}
			}
			StringBuffer hql_ck01_update = new StringBuffer();
			hql_ck01_update
					.append("update YK_CK01  set LYRQ=:lyrq,LYPB=:lypb,LYGH=:lygh,YFSB=:yfsb where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh");
			map_par.clear();
			map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
			map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
			map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
			map_par.put("lyrq", nowDate);
			map_par.put("lypb", 1);
			map_par.put("lygh", userid);
			map_par.put("yfsb", yfsb);
			dao.doUpdate(hql_ck01_update.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药单确认失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "领药单确认失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "领药单确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 药品申领查询药库库存
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double queryKcsl_yfyk(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select sum(KCSL) as KCSL from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		double ykkc=0;
		try {
			map_par.put("jgid", getJGIDByYKSB(MedicineUtils.parseLong(body.get("YKSB"))));
			Map<String, Object> map_kcsl = dao.doLoad(hql.toString(), map_par);
			if(map_kcsl!=null&&MedicineUtils.parseDouble(map_kcsl.get("KCSL"))!=0&&!"null".equals(map_kcsl.get("KCSL"))){
				ykkc=MedicineUtils.parseDouble(map_kcsl.get("KCSL"));
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存数量查询失败", e);
		}
		return ykkc;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 药房确认退药和未确认退药数据查询
	 * @updateInfo
	 * @param body
	 * @param req
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryMedicinesApplyRefund(
			Map<String, Object> body, Map<String, Object> req,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		StringBuffer hql_ckfs = new StringBuffer();
		if (body.get("yksb") == null || MedicineUtils.parseLong(body.get("yksb")) == 0) {
			return ret;
		}
		try {
			long yksb = MedicineUtils.parseLong(body.get("yksb"));
			UserRoleToken user = UserRoleToken.getCurrent();
			long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
			hql.append(
					"select a.SQRQ as SQRQ,a.CKFS as CKFS,a.LYPB as LYPB,a.CKDH as CKDH ,a.XTSB as XTSB,a.CKRQ as CKRQ,a.CKBZ as CKBZ,a.LYRQ as LYRQ,a.JGID as JGID,sum(b.JHJE) as JHJE,sum(b.LSJE) as LSJE from YK_CK01 a,YK_CK02  b where a.XTSB=b.XTSB and a.CKFS=b.CKFS and a.CKDH=b.CKDH and a.XTSB=:xtsb and a.CKFS=:ckfs and a.YFSB=:yfsb and ")
					.append(ExpressionProcessor.instance().toString((List<Object>) body.get("cnd")).replaceAll("str", "to_char"))
					.append(" group by a.CKFS,a.CKDH,a.CKRQ,a.CKBZ,(a.SQTJ+a.CKPB+1),a.LYRQ,a.JGID,a.XTSB,a.LYPB,a.SQRQ order by a.CKDH desc");
			hql_ckfs.append("select CKFS as CKFS from YK_CKFS  where XTSB=:yksb and DYFS=6");
			Map<String, Object> map_par_ckfs = new HashMap<String, Object>();
			map_par_ckfs.put("yksb", yksb);
			Map<String, Object> map_ckfs = dao.doLoad(hql_ckfs.toString(),
					map_par_ckfs);
			if (map_ckfs == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "请先维护药库出库[申领退药]的对应方式!");
			}
			int ckfs = MedicineUtils.parseInt(map_ckfs.get("CKFS"));
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("xtsb", yksb);
			map_par.put("ckfs", ckfs);
			map_par.put("yfsb", yfsb);
			MedicineCommonModel model=new MedicineCommonModel(dao);
			ret=model.getPageInfoRecord(req, map_par, hql.toString(), null);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药房领药数据查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "药房领药数据查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 申领退药方式查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryTyCkfs(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		long yksb = MedicineUtils.parseLong(body.get("yksb"));
		StringBuffer hql = new StringBuffer();
		hql.append("select CKFS as CKFS,FSMC as FSMC from YK_CKFS  where XTSB=:yksb and DYFS=6");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		Map<String, Object> map_lyfs=null;
		try {
			map_lyfs = dao.doLoad(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药方式查询失败", e);
		}
		return map_lyfs;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 提交药房退药单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveMedicinesApplyRefundCommit(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_ck02 = (List<Map<String, Object>>) body
				.get("YK_CK02");
		Map<String, Object> map_ck01 = (Map<String, Object>) body
				.get("YK_CK01");
		Date nowDate = new Date();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb =MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String userid =MedicineUtils.parseString(user.getUserId()) ;// 用户ID
		StringBuffer hql_count = new StringBuffer();
		hql_count
				.append(" CKDH=:ckdh and CKFS=:ckfs and XTSB=:xtsb and LYPB=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
		map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
		map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
		try {
			long l = dao.doCount("YK_CK01",
					hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已确认入库,不能重复操作!请刷新窗口!");
			}
			StringBuffer hql_bz = new StringBuffer();// 查询包装
			hql_bz.append("select a.ZXBZ as ZXBZ,b.YFBZ as YFBZ from YK_TYPK  a,YF_YPXX  b where a.YPXH=b.YPXH and b.YPXH=:ypxh and b.YFSB=:yfsb");
			List<Map<String, Object>> list_kc = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map_ck02 : list_ck02) {
				map_par.clear();
				map_par.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
				map_par.put("yfsb", yfsb);
				Map<String, Object> map_bz = dao.doLoad(hql_bz.toString(),
						map_par);
				double zxbz = MedicineUtils.parseDouble(map_bz.get("ZXBZ"));
				double yfbz = MedicineUtils.parseDouble(map_bz.get("YFBZ"));
				double ypsl = -MedicineUtils.formatDouble(4,
						MedicineUtils.parseDouble(map_ck02.get("SQSL")) * (zxbz / yfbz));
				Map<String, Object> map_kc = new HashMap<String, Object>();
				map_kc.put("YPSL", ypsl);
				map_kc.put("KCSB", MedicineUtils.parseLong(map_ck02.get("YFKCSB")));
				list_kc.add(map_kc);
			}
			PharmacyInventoryManageModel model=new PharmacyInventoryManageModel(dao);
			Map<String, Object> map_ret_kc = model.lessInventory(list_kc, ctx);
			if (MedicineUtils.parseInt(map_ret_kc.get("code")) != 200) {
				return map_ret_kc;
			}
			StringBuffer hql_ck01_update = new StringBuffer();
			hql_ck01_update
					.append("update YK_CK01  set LYRQ=:lyrq,LYPB=1,LYGH=:lygh,YFSB=:yfsb,SQTJ=1 where XTSB=:xtsb and CKFS=:ckfs and CKDH=:ckdh");
			map_par.clear();
			map_par.put("xtsb", MedicineUtils.parseLong(map_ck01.get("XTSB")));
			map_par.put("ckfs", MedicineUtils.parseInt(map_ck01.get("CKFS")));
			map_par.put("ckdh", MedicineUtils.parseInt(map_ck01.get("CKDH")));
			map_par.put("lyrq", nowDate);
			map_par.put("lygh", userid);
			map_par.put("yfsb", yfsb);
			dao.doUpdate(hql_ck01_update.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药单确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-24
	 * @description 申领退药明细查询(用于解决库存数量为0不能关联出记录的问题)
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryMedicinesApplyRefundDetail(
			List<Object> cnd) throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			ret = dao.doList(cnd, null, BSPHISEntryNames.YK_CK02_SLTY_LIST);
			Map<String, Object> map_ck01 = dao.doLoad(cnd,
					BSPHISEntryNames.YK_CK01_FORM);
			StringBuffer hql_kcsl = new StringBuffer();
			StringBuffer hql_bz = new StringBuffer();
			hql_kcsl.append("select YPSL as KCSL from YF_KCMX  where SBXH=:kcsb");
			hql_bz.append("select a.ZXBZ as ZXBZ,b.YFBZ as YFBZ from YK_TYPK  a,YF_YPXX  b where a.YPXH=b.YPXH and b.YFSB=:yfsb and b.YPXH=:ypxh");
			for (Map<String, Object> map_ck02 : ret) {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("kcsb", MedicineUtils.parseLong(map_ck02.get("YFKCSB")));
				Map<String, Object> map_kcsl = dao.doLoad(hql_kcsl.toString(),
						map_par);
				if (map_kcsl != null && map_kcsl.get("KCSL") != null) {
					map_par.clear();
					map_par.put("ypxh", MedicineUtils.parseLong(map_ck02.get("YPXH")));
					map_par.put("yfsb", MedicineUtils.parseLong(map_ck01.get("YFSB")));
					Map<String, Object> map_bz = dao.doLoad(hql_bz.toString(),
							map_par);
					map_ck02.put(
							"KCSL",
							MedicineUtils.parseDouble(map_kcsl.get("KCSL"))
									* (MedicineUtils.parseDouble(map_bz.get("YFBZ")) / MedicineUtils.parseDouble(map_bz
											.get("ZXBZ"))));
				} else {
					map_ck02.put("KCSL", 0);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "退药明细查询失败", e);
		}
		return ret;
	}
}
