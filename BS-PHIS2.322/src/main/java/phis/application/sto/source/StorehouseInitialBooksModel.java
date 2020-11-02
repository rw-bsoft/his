package phis.application.sto.source;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
/**
 * 药库初始账册
 * @author caijy
 *
 */
public class StorehouseInitialBooksModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseInitialBooksModel.class);

	public StorehouseInitialBooksModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 保存初始信息
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicinesStorehouseInitialData(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		StorehouseBasicInfomationModel sbi=new StorehouseBasicInfomationModel(dao);
		Map<String,Object> cswc = sbi.initialQuery(ctx);
		if (MedicineUtils.parseInt(cswc.get("code")) == 200) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "已转账,不能修改初始数据!");
		}
		Map<String, Object> map_cdxx = (Map<String, Object>) body.get("cdxx");
		List<Map<String, Object>> list_kcmx = (List<Map<String, Object>>) body
				.get("kcmx");
		long ypxh = MedicineUtils.parseLong(map_cdxx.get("YPXH"));
		long ypcd = MedicineUtils.parseLong(map_cdxx.get("YPCD"));
		String jgid = MedicineUtils.parseString(map_cdxx.get("JGID"));
		StringBuffer hql_cdxx_update = new StringBuffer();// 更新cdxx表
		StringBuffer hql_kcmx_delete = new StringBuffer();// 删除库存明细表数据
		hql_cdxx_update
				.append("update YK_CDXX  set LSJG=:lsjg,JHJG=:jhjg,LSJE=:lsje,JHJE=:jhje,KCSL=:kcsl where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		hql_kcmx_delete.append("delete from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
		Map<String, Object> map_par_kcmx = new HashMap<String, Object>();
		map_par_cdxx.put("lsjg", MedicineUtils.parseDouble(map_cdxx.get("LSJG")));
		map_par_cdxx.put("jhjg", MedicineUtils.parseDouble(map_cdxx.get("JHJG")));
		map_par_cdxx.put("lsje", MedicineUtils.parseDouble(map_cdxx.get("LSJE")));
		map_par_cdxx.put("jhje", MedicineUtils.parseDouble(map_cdxx.get("JHJE")));
		map_par_cdxx.put("kcsl", MedicineUtils.parseDouble(map_cdxx.get("KCSL")));
		map_par_cdxx.put("ypxh", ypxh);
		map_par_cdxx.put("ypcd", ypcd);
		map_par_cdxx.put("jgid", jgid);
		map_par_kcmx.put("ypxh", ypxh);
		map_par_kcmx.put("ypcd", ypcd);
		map_par_kcmx.put("jgid", jgid);
		try {
			dao.doUpdate(hql_cdxx_update.toString(), map_par_cdxx);
			dao.doUpdate(hql_kcmx_delete.toString(), map_par_kcmx);
			StorehouseStockManageModel ssm=new StorehouseStockManageModel(dao);
			ssm.synchronousPrimaryKey(ctx);//处理下主键问题
			for (Map<String, Object> map_kcmx : list_kcmx) {
				dao.doSave("create", BSPHISEntryNames.YK_KCMX, map_kcmx, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "初始账册保存失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "初始账册保存失败!", e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-30
	 * @description 初始账册form数据查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> initialDataQuery(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select c.JGID as JGID,a.YPMC as YPMC,a.YPGG as YPGG,a.YPDW as YPDW,b.CDMC as CDMC,c.YPXH as YPXH,c.YPCD as YPCD,c.KCSL as KCSL,c.JHJG as JHJG,c.PFJG as PFJG,c.LSJG as LSJG,c.JHJE as JHJE,c.PFJE as PFJE,c.LSJE as LSJE,d.LSJG as GYLJ,d.JHJG as GYJJ from YK_TYPK a,YK_CDDZ b,YK_CDXX c,YK_YPCD d where a.YPXH=c.YPXH and b.YPCD=c.YPCD and c.YPCD=d.YPCD and c.YPXH=d.YPXH and c.YPXH=:ypxh and c.YPCD=:ypcd and c.JGID=:jgid");
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("ypxh")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("ypcd")));
			map_par.put("jgid", MedicineUtils.parseString(body.get("jgid")));
			ret = dao.doLoad(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "初始数据查询失败!", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-12-31
	 * @description 药库初始转账
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveInitialTransfer(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		Date end = c.getTime();
		c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
		c.set(Calendar.DATE, 1);
		Date begin = c.getTime();
		c.set(Calendar.DATE, 10);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		Date init_date = c.getTime();
		StringBuffer hql_cdxx = new StringBuffer();// 查询初始账册表单
		StringBuffer hql_kcmx_sum = new StringBuffer();// 查询库存明细表单总金额
		StringBuffer hql_cdxx_sum = new StringBuffer();// 查询初始账册表单总金额
		StringBuffer hql_cdxx_where = new StringBuffer();// 初始账册表单查询条件
		StringBuffer hql_kcmx = new StringBuffer();
		StringBuffer hql_yklb_update = new StringBuffer();// 更新药库
		hql_cdxx_where
				.append("YK_CDDZ a,YK_TYPK b,YK_YPCD c,YK_CDXX d,YK_YPXX e,YK_YKLB  f where ( c.YPXH = b.YPXH) and ( c.YPCD = a.YPCD ) and ( d.YPXH = b.YPXH) and ( d.YPCD = a.YPCD ) and ( d.JGID = e.JGID ) and ( d.YPXH = e.YPXH ) and ( b.YPXH = e.YPXH ) and ( f.YKSB = e.YKSB ) and ( e.YKZF=0 ) and ( c.ZFPB=0 ) and ( d.ZFPB=0 ) and ( b.ZFPB = 0) and  ( f.YKSB = :yksb ) and ( d.JGID = :jgid )");
		hql_cdxx_sum.append(
				"select sum(d.LSJE) as LSJE ,sum(d.JHJE) as JHJE from ")
				.append(hql_cdxx_where.toString());
		hql_cdxx.append(
				"select b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,a.CDMC as CDMC,d.JHJG as JHJG,d.PFJG as PFJG,d.LSJG as LSJG,d.KCSL as KCSL,d.LSJE as LSJE,d.PFJE as PFJE,d.JHJE as JHJE,b.YPDM as YPDM,e.KWBM as KWBM,b.YPSX as YPSX,b.PYDM as PYDM,b.WBDM as WBDM,b.JXDM as JXDM,b.QTDM as QTDM,d.YPXH as YPXH,d.YPCD as YPCD,d.DJFS as DJFS,d.DJGS as DJGS from ")
				.append(hql_cdxx_where.toString());
		hql_kcmx_sum
				.append("select sum(a.LSJE) as LSJE ,sum(a.JHJE) as JHJE from YK_KCMX a,YK_TYPK b,YK_YPXX c where a.YPXH=b.YPXH and a.JGID=c.JGID and b.YPXH=c.YPXH and c.YKSB=:yksb and c.JGID=:jgid");
		hql_kcmx.append(
				"select sum(a.LSJE) as LSJE,sum(a.JHJE) as JHJE,sum(a.KCSL) as KCSL from YK_KCMX a,YK_TYPK b,YK_YPXX c where a.YPXH=b.YPXH and a.JGID=c.JGID and b.YPXH=c.YPXH and c.YKSB=:yksb and c.JGID=:jgid and a.YPXH=:ypxh and a.YPCD=:ypcd");
		hql_yklb_update.append("update YK_YKLB  set SYBZ = 2 Where YKSB = :yksb");
		Map<String, Object> map_par_cdxx = new HashMap<String, Object>();
		map_par_cdxx.put("yksb", yksb);
		map_par_cdxx.put("jgid", jgid);
		try {
			List<Map<String, Object>> list_cdxx = dao.doQuery(
					hql_cdxx.toString(), map_par_cdxx);
			for (Map<String, Object> map_cdxx : list_cdxx) {
				Map<String, Object> map_par_kcmx = new HashMap<String, Object>();
				map_par_kcmx.put("ypxh", MedicineUtils.parseLong(map_cdxx.get("YPXH")));
				map_par_kcmx.put("ypcd", MedicineUtils.parseLong(map_cdxx.get("YPCD")));
				map_par_kcmx.put("yksb", yksb);
				map_par_kcmx.put("jgid", jgid);
				List<Map<String, Object>> list_kcmx = dao.doSqlQuery(
						hql_kcmx.toString(), map_par_kcmx);
				Map<String, Object> map_kcmx = list_kcmx.get(0);
				if (MedicineUtils.parseDouble(map_kcmx.get("KCSL")) != MedicineUtils.parseDouble(map_cdxx
						.get("KCSL"))
						|| MedicineUtils.parseDouble(map_kcmx.get("LSJE")) != MedicineUtils.parseDouble(map_cdxx
								.get("LSJE"))
						|| MedicineUtils.parseDouble(map_kcmx.get("JHJE")) != MedicineUtils.parseDouble(map_cdxx
								.get("JHJE"))) {
					return MedicineUtils.getRetMap("药品:" + map_cdxx.get("YPMC") + " 产地:"
							+ map_cdxx.get("CDMC") + " 与存入YK_KCMX中数据不相符,请检查!");
				}
			}
			Map<String, Object> map_cdxx_sum = dao.doSqlQuery(
					hql_cdxx_sum.toString(), map_par_cdxx).get(0);
			Map<String, Object> map_kcmx_sum = dao.doSqlQuery(
					hql_kcmx_sum.toString(), map_par_cdxx).get(0);
			if (MedicineUtils.parseDouble(map_cdxx_sum.get("LSJE")) != MedicineUtils.parseDouble(map_kcmx_sum
					.get("LSJE"))
					|| MedicineUtils.parseDouble(map_cdxx_sum.get("JHJE")) != MedicineUtils.parseDouble(map_kcmx_sum
							.get("JHJE"))) {
				return MedicineUtils.getRetMap("初始账册数据有错误");
			}
			for (Map<String, Object> map_cdxx : list_cdxx) {
				if (MedicineUtils.parseDouble(map_cdxx.get("KCSL")) == 0
						&& (MedicineUtils.parseDouble(map_cdxx.get("LSJE")) != 0 || MedicineUtils.parseDouble(map_cdxx
								.get("JHJE")) != 0)) {
					return MedicineUtils.getRetMap("药品:" + map_cdxx.get("YPMC") + " 产地:"
							+ map_cdxx.get("CDMC") + " 初始账册数据有错误，请修改！");
				}
				// 保存月结结果
				Map<String, Object> map_yjjg = new HashMap<String, Object>();
				map_yjjg.put("JGID", jgid);
				map_yjjg.put("XTSB", yksb);
				map_yjjg.put("CWYF", init_date);
				map_yjjg.put("YPXH", MedicineUtils.parseLong(map_cdxx.get("YPXH")));
				map_yjjg.put("YPCD", MedicineUtils.parseLong(map_cdxx.get("YPCD")));
				map_yjjg.put("KCSL", MedicineUtils.parseDouble(map_cdxx.get("KCSL")));
				map_yjjg.put("LSJE", MedicineUtils.parseDouble(map_cdxx.get("LSJE")));
				map_yjjg.put("PFJE", MedicineUtils.parseDouble(map_cdxx.get("PFJE")));
				map_yjjg.put("JHJE", MedicineUtils.parseDouble(map_cdxx.get("JHJE")));
				map_yjjg.put("LSJG", MedicineUtils.parseDouble(map_cdxx.get("LSJG")));
				map_yjjg.put("JHJG", MedicineUtils.parseDouble(map_cdxx.get("JHJG")));
				map_yjjg.put("PFJG", MedicineUtils.parseDouble(map_cdxx.get("PFJG")));
				dao.doSave("create", BSPHISEntryNames.YK_YJJG, map_yjjg, false);
				// 保存实物月结明细
				Map<String, Object> map_swyj = new HashMap<String, Object>();
				map_swyj.put("JGID", jgid);
				map_swyj.put("XTSB", yksb);
				map_swyj.put("CWYF", init_date);
				map_swyj.put("YPXH", MedicineUtils.parseLong(map_cdxx.get("YPXH")));
				map_swyj.put("YPCD", MedicineUtils.parseLong(map_cdxx.get("YPCD")));
				map_swyj.put("KCSL", MedicineUtils.parseDouble(map_cdxx.get("KCSL")));
				map_swyj.put("LSJE", MedicineUtils.parseDouble(map_cdxx.get("LSJE")));
				map_swyj.put("PFJE", MedicineUtils.parseDouble(map_cdxx.get("PFJE")));
				map_swyj.put("JHJE", MedicineUtils.parseDouble(map_cdxx.get("JHJE")));
				dao.doSave("create", BSPHISEntryNames.YK_SWYJ, map_swyj, false);
			}
			Map<String, Object> map_jzjl = new HashMap<String, Object>();
			map_jzjl.put("JGID", jgid);
			map_jzjl.put("XTSB", yksb);
			map_jzjl.put("CWYF", init_date);
			map_jzjl.put("QSSJ", begin);
			map_jzjl.put("ZZSJ", end);
			dao.doSave("create", BSPHISEntryNames.YK_JZJL, map_jzjl, false);
			Map<String, Object> map_par_yklb = new HashMap<String, Object>();
			map_par_yklb.put("yksb", yksb);
			dao.doUpdate(hql_yklb_update.toString(), map_par_yklb);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "初始转账保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "初始转账保存失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	
	

}
