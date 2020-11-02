package phis.application.sto.source;

import java.util.ArrayList;
import java.util.Calendar;
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
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;

/**
 * 药库盘点model
 * 
 * @author caijy
 * 
 */
public class StorehouseStoreroomInventoryModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseStoreroomInventoryModel.class);

	public StorehouseStoreroomInventoryModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 删除盘点单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removeInventory(Map<String, Object> body)
			throws ModelDataOperationException {
		String pddh = body.get("pddh") + "";
		long yksb = MedicineUtils.parseLong(body.get("xtsb"));
		StringBuffer hql_count = new StringBuffer();
		hql_count.append(" XTSB=:yksb and PDDH=:pddh and PDPB=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		map_par.put("pddh", pddh);
		try {
			long l = dao.doCount("YK_PD01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("正在使用本盘点单,不能删除操作!");
			}
			hql_count = new StringBuffer();
			hql_count.append(" XTSB=:yksb and PDDH=:pddh and YSGH is not null");
			l = dao.doCount("YK_PD01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("数据已发生变化,不能删除该单据!");
			}
			StringBuffer hql_pd01_delete = new StringBuffer();
			StringBuffer hql_pd02_delete = new StringBuffer();
			hql_pd01_delete
					.append("delete from YK_PD01  where PDDH=:pddh and XTSB=:yksb and YSGH is null");
			hql_pd02_delete
					.append("delete from YK_PD02 where PDDH=:pddh and XTSB=:yksb ");
			dao.doUpdate(hql_pd01_delete.toString(), map_par);
			dao.doUpdate(hql_pd02_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点单号删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 查询库存盘点是否按批次盘参数
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean queryKCPD_PC(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String kcpd_pc = ParameterUtil.getParameter(jgid, "KCPD_PC" + yksb,"true","药库库存盘点是否按批次盘点,false是不按批次盘点,true是按批次盘点",BSPHISSystemArgument.defaultCategory.get("KCPD_PC"), ctx);
		if ("true".equals(kcpd_pc)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 查询kcsb和实盘数量,用于保存的时候更新数据
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> quertyInventoryData_PC_KCSB(
			Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		try {
			if ("create".equals(op)) {
				StringBuffer hql = new StringBuffer();
				hql.append("select a.SBXH as KCSB,a.KCSL as SPSL,a.YPXH as YPXH,a.YPCD as YPCD from YK_KCMX a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ  e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and d.YKSB=:yksb and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX ");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				ret = dao.doSqlQuery(hql.toString(), map_par);
			} else {
				StringBuffer hql = new StringBuffer();
				hql.append("select KCSB as KCSB,SPSL as SPSL,YPXH as YPXH,YPCD as YPCD from YK_PD02 where XTSB=:xtsb and PDDH=:pddh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
				map_par.put("pddh", body.get("PDDH") + "");
				ret = dao.doSqlQuery(hql.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 盘点单保存
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveInventory(Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_pd01 = (Map<String, Object>) body
				.get("YK_PD01");
		List<Map<String, Object>> list_pd02 = (List<Map<String, Object>>) body
				.get("YK_PD02");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		try {
			if ("create".equals(op)) {
				StringBuffer hql_count = new StringBuffer();
				hql_count.append(" YSGH is null and XTSB=:xtsb");
				Map<String, Object> map_par_count = new HashMap<String, Object>();
				map_par_count.put("xtsb", yksb);
				long l = dao.doCount("YK_PD01", hql_count.toString(),
						map_par_count);
				if (l > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR,
							"有未完成的盘点单,不能新增盘点单!");
				}
				int pddh = getPDDH(ctx);
				if (pddh == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "本年盘点单号使用已达上限");
				}
				map_pd01.put("PDDH", pddh);
				map_pd01.put("XTSB", yksb);
				map_pd01.put("JGID", yksb);
				StringBuffer hql = new StringBuffer();
				hql.append("select a.YPXH as YPXH,a.YPCD as YPCD,a.YPPH as YPPH,a.YPXQ as YPXQ,a.KCSL as ZMSL,a.JHJG as JHJG,a.PFJG as PFJG,a.LSJG as LSJG,a.JHJE as JHJE,a.PFJE as PFJE,a.LSJE as LSJE,a.BZLJ as BZLJ,a.SBXH as KCSB,a.TYPE as TYPE from YK_KCMX  a where SBXH=:kcsb");
				for (int i = 0; i < list_pd02.size(); i++) {
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("kcsb", MedicineUtils.parseLong(list_pd02
							.get(i).get("KCSB")));
					Map<String, Object> map_pd02 = dao.doLoad(hql.toString(),
							map_par);
					map_pd02.put(
							"SPSL",
							MedicineUtils.parseDouble(list_pd02.get(i).get(
									"SPSL")));
					map_pd02.put("JGID", jgid);
					map_pd02.put("XTSB", yksb);
					map_pd02.put("PDDH", pddh);
					dao.doSave("create", BSPHISEntryNames.YK_PD02, map_pd02,
							false);
				}
				dao.doSave("create", BSPHISEntryNames.YK_PD01, map_pd01, false);
			} else {
				StringBuffer hql = new StringBuffer();
				hql.append("select a.YPXH as YPXH,a.YPCD as YPCD,a.YPPH as YPPH,a.YPXQ as YPXQ,a.KCSL as ZMSL,a.JHJG as JHJG,a.PFJG as PFJG,a.LSJG as LSJG,a.JHJE as JHJE,a.PFJE as PFJE,a.LSJE as LSJE,a.BZLJ as BZLJ,a.SBXH as KCSB,a.TYPE as TYPE from YK_KCMX  a where SBXH=:kcsb");
				String pddh = map_pd01.get("PDDH") + "";
				for (Map<String, Object> map_pd02 : list_pd02) {
					Map<String, Object> yk_pd02 = dao.doLoad(CNDHelper
							.toListCnd("['and',['eq',['$','KCSB'],['d',"
									+ MedicineUtils.parseLong(map_pd02
											.get("KCSB"))
									+ "]],['eq',['$','PDDH'],['s'," + pddh
									+ "]]]"), BSPHISEntryNames.YK_PD02);
					if (yk_pd02 == null) {
						Map<String, Object> map_par = new HashMap<String, Object>();
						map_par.put("kcsb",
								MedicineUtils.parseLong(map_pd02.get("KCSB")));
						yk_pd02 = dao.doLoad(hql.toString(), map_par);
						yk_pd02.put("SPSL",
								MedicineUtils.parseDouble(map_pd02.get("SPSL")));
						yk_pd02.put("JGID", jgid);
						yk_pd02.put("XTSB", yksb);
						yk_pd02.put("PDDH", pddh);
						dao.doSave("create", BSPHISEntryNames.YK_PD02, yk_pd02,
								false);
					} else {
						yk_pd02.put("SPSL",
								MedicineUtils.parseDouble(map_pd02.get("SPSL")));
						dao.doSave("update", BSPHISEntryNames.YK_PD02, yk_pd02,
								false);
					}
				}
				dao.doSave("update", BSPHISEntryNames.YK_PD01, map_pd01, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "保存失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "保存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 获取盘点单号
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int getPDDH(Context ctx) throws ModelDataOperationException {
		long yksb = MedicineUtils.parseLong(UserRoleToken.getCurrent()
				.getProperty("storehouseId"));// 用户的药库识别
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int min = MedicineUtils.parseInt(year % 100 + "0000");// 本来是取年份的最后2位,由于现在是2013暂时不考虑0几的
		int max = min + 9999;
		StringBuffer hql = new StringBuffer();
		hql.append("select max(PDDH) as PDDH from YK_PD01  where XTSB=:yksb and PDDH>=:minValue and PDDH<=:maxValue");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yksb", yksb);
		map_par.put("minValue", min);
		map_par.put("maxValue", max);
		int pddh = 0;
		try {
			List<Map<String, Object>> list_pddh = dao.doSqlQuery(
					hql.toString(), map_par);
			if (list_pddh == null || list_pddh.size() == 0
					|| list_pddh.get(0).get("PDDH") == null) {
				return min;
			} else {
				if (MedicineUtils.parseInt(list_pddh.get(0).get("PDDH")) == max) {
					return 0;
				}
				pddh = MedicineUtils.parseInt(list_pddh.get(0).get("PDDH")) + 1;
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点单号查询失败", e);
		}
		return pddh;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 确认盘点单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveCommitInventory(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		Map<String, Object> map_pd01 = (Map<String, Object>) body
				.get("YK_PD01");
		String pddh = map_pd01.get("PDDH") + "";
		long yksb = MedicineUtils.parseLong(map_pd01.get("XTSB"));
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userid = user.getUserId();// 用户ID
		try {
			StringBuffer hql_count = new StringBuffer();
			hql_count.append(" PDDH=:pddh and XTSB=:yksb");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yksb", yksb);
			map_par.put("pddh", pddh);
			long l = dao.doCount("YK_PD01",
					hql_count.toString(), map_par);
			if (l == 0) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "盘点单已删除,不能确认操作!窗口已刷新!");
			}
			int pyfs=0;
			try{
			pyfs = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "PYFS_YK"
					+ yksb,BSPHISSystemArgument.defaultValue.get("PYFS_YK"),BSPHISSystemArgument.defaultAlias.get("PYFS_YK"),BSPHISSystemArgument.defaultCategory.get("PYFS_YK"), ctx));//盘盈方式
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, "PYFS_YK"+ yksb, e);
			}
			int pkfs=0;
			try{
			 pkfs = MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "PKFS_YK"
					+ yksb,BSPHISSystemArgument.defaultValue.get("PKFS_YK"),BSPHISSystemArgument.defaultAlias.get("PKFS_YK"),BSPHISSystemArgument.defaultCategory.get("PKFS_YK"), ctx));//盘亏方式
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, "PKFS_YK"+ yksb, e);
			}
			if (pyfs == 0 || pkfs == 0) {
				return MedicineUtils.getRetMap("未设置盘盈入库和/或盘亏出库方式!");
			}
			hql_count = new StringBuffer();
			hql_count.append("XTSB=:yksb and RKFS=:rkfs");
			map_par.remove("pddh");
			map_par.put("rkfs", pyfs);
			l = dao.doCount("YK_RKFS", hql_count.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("设置盘盈入库方式无效!");
			}
			hql_count = new StringBuffer();
			hql_count.append("XTSB=:yksb and CKFS=:ckfs");
			map_par.remove("rkfs");
			map_par.put("ckfs", pkfs);
			l = dao.doCount("YK_CKFS", hql_count.toString(),
					map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("设置盘亏出库方式无效!");
			}
			// 判断是否有调价
			hql_count = new StringBuffer();
			hql_count
					.append("ZYPB = 1 and to_char(ZXRQ,'yyyy-mm-dd hh24:mi:ss')>=:pdrq and JGID=:jgid");
			map_par.clear();
			map_par.put("jgid", jgid);
			map_par.put("pdrq", map_pd01.get("PDRQ"));
			l = dao.doCount("YK_TJ01", hql_count.toString(),
					map_par);
			int isTJ = 0;
			StringBuffer hql_tj = new StringBuffer();
			if (l > 0) {
				isTJ = 1;
				hql_tj.append("select LSJG as LSJG from YK_KCMX where SBXH=:kcsb");
			}
			saveInventory(body, "update", ctx);// 先对数据进行保存
			List<Map<String, Object>> list_pd02 = dao.doList(
					CNDHelper.toListCnd("['and',['eq',['$','PDDH'],['s',"
							+ pddh + "]],['eq',['$','XTSB'],['d'," + yksb
							+ "]]]"), null, BSPHISEntryNames.YK_PD02);
			Map<String, Object> map_rk = new HashMap<String, Object>();// 用于存入库主表
			List<Map<String, Object>> list_rkmx = new ArrayList<Map<String, Object>>();// 存入库明细
			Map<String, Object> map_ck = new HashMap<String, Object>();// 存出库主表
			List<Map<String, Object>> list_ckmx = new ArrayList<Map<String, Object>>();// 存出库明细
			int rkdh = 0;
			int ckdh = 0;
			Date nowDate = new Date();
			for (Map<String, Object> map_pd02 : list_pd02) {
				if (map_pd02.get("SPSL") == null) {
					continue;
				}
				// 如果有调价,则取最新的库存价格
				if (isTJ == 1) {
					Map<String, Object> map_par_kc = new HashMap<String, Object>();
					map_par_kc.put("kcsb", MedicineUtils.parseLong(map_pd02.get("KCSB")));
					Map<String, Object> map_kc = dao.doLoad(hql_tj.toString(),
							map_par_kc);
					if (map_kc == null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "数据异常");
					}
					map_pd02.put("LSJG", MedicineUtils.parseDouble(map_kc.get("LSJG")));
				}
				if (MedicineUtils.parseDouble(map_pd02.get("ZMSL")) == MedicineUtils.parseDouble(map_pd02
						.get("SPSL"))) {
					continue;
				}
				if (MedicineUtils.parseDouble(map_pd02.get("ZMSL")) < MedicineUtils.parseDouble(map_pd02
						.get("SPSL"))) {
					if (rkdh == 0) {
						StringBuffer hql_rkdh = new StringBuffer();
						hql_rkdh.append("select RKDH as RKDH from YK_RKFS where XTSB=:yksb and RKFS=:rkfs");
						Map<String, Object> map_par_rkdh = new HashMap<String, Object>();
						map_par_rkdh.put("yksb", yksb);
						map_par_rkdh.put("rkfs", pyfs);
						Map<String, Object> map_rkfs = dao.doLoad(
								hql_rkdh.toString(), map_par_rkdh);
						rkdh = MedicineUtils.parseInt(map_rkfs.get("RKDH"));
						map_rk.put("RKDH", rkdh);
						map_rk.put("PWD", 0);
						map_rk.put("XTSB", yksb);
						map_rk.put("RKFS", pyfs);
						map_rk.put("CWPB", 0);
						map_rk.put("DJFS", 1);
						map_rk.put("RKPB", 0);
						map_rk.put("RKRQ", nowDate);
						map_rk.put("CZGH", userid);
						map_rk.put("LRRQ", nowDate);
						map_rk.put("JGID", jgid);
					}
					Map<String, Object> map_rkmx = new HashMap<String, Object>();
					map_rkmx.put("RKDH", rkdh);
					map_rkmx.put("XTSB", yksb);
					map_rkmx.put("RKFS", pyfs);
					map_rkmx.put("YPKL", 1);
					map_rkmx.put("JBYWBZ", 1);
					map_rkmx.put("YPXH", map_pd02.get("YPXH"));
					map_rkmx.put("YPCD", map_pd02.get("YPCD"));
					map_rkmx.put("YPPH", map_pd02.get("YPPH"));
					map_rkmx.put("YPXQ", map_pd02.get("YPXQ"));
					map_rkmx.put("PFJG", map_pd02.get("PFJG"));
					map_rkmx.put("LSJG", map_pd02.get("LSJG"));
					map_rkmx.put("JHJG", map_pd02.get("JHJG"));
					map_rkmx.put("BZLJ", map_pd02.get("BZLJ"));
					map_rkmx.put("RKSL", MedicineUtils.parseDouble(map_pd02.get("SPSL"))
							- MedicineUtils.parseDouble(map_pd02.get("ZMSL")));
					map_rkmx.put("HGSL", MedicineUtils.parseDouble(map_pd02.get("SPSL"))
							- MedicineUtils.parseDouble(map_pd02.get("ZMSL")));
					map_rkmx.put(
							"JHHJ",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_rkmx.get("RKSL"))
									* MedicineUtils.parseDouble(map_rkmx.get("JHJG"))));
					map_rkmx.put(
							"FKJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_rkmx.get("RKSL"))
									* MedicineUtils.parseDouble(map_rkmx.get("JHJG"))));
					map_rkmx.put(
							"YFJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_rkmx.get("RKSL"))
									* MedicineUtils.parseDouble(map_rkmx.get("JHJG"))));
					map_rkmx.put("TYPE", map_pd02.get("TYPE"));
					map_rkmx.put(
							"PFJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_rkmx.get("RKSL"))
									* MedicineUtils.parseDouble(map_rkmx.get("PFJG"))));
					map_rkmx.put(
							"LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_rkmx.get("RKSL"))
									* MedicineUtils.parseDouble(map_rkmx.get("LSJG"))));
					map_rkmx.put("DJFS", 0);
					map_rkmx.put("DJGS", "");
					map_rkmx.put("KCSB", map_pd02.get("KCSB"));
					map_rkmx.put("JGID", jgid);
					list_rkmx.add(map_rkmx);
				} else {
					if (ckdh == 0) {
						StringBuffer hql_ckdh = new StringBuffer();
						hql_ckdh.append("select CKDH as CKDH from YK_CKFS  where XTSB=:yksb and CKFS=:ckfs");//查询出库方式
						Map<String, Object> map_par_ckdh = new HashMap<String, Object>();
						map_par_ckdh.put("yksb", yksb);
						map_par_ckdh.put("ckfs", pkfs);
						Map<String, Object> map_ckfs = dao.doLoad(
								hql_ckdh.toString(), map_par_ckdh);
						ckdh = MedicineUtils.parseInt(map_ckfs.get("CKDH"));
						map_ck.put("CKDH", ckdh);
						map_ck.put("XTSB", yksb);
						map_ck.put("CKFS", pkfs);
						map_ck.put("YFSB", 0);
						map_ck.put("CKPB", 0);
						map_ck.put("SQRQ", nowDate);
						map_ck.put("CZGH", userid);
						map_ck.put("SQTJ", 1);
						map_ck.put("JGID", jgid);
						map_ck.put("LYPB", 0);
					}
					Map<String, Object> map_ckmx = new HashMap<String, Object>();
					map_ckmx.put("CKDH", ckdh);
					map_ckmx.put("XTSB", yksb);
					map_ckmx.put("CKFS", pkfs);
					map_ckmx.put("YPXH", map_pd02.get("YPXH"));
					map_ckmx.put("YPCD", map_pd02.get("YPCD"));
					map_ckmx.put("YPPH", map_pd02.get("YPPH"));
					map_ckmx.put("YPXQ", map_pd02.get("YPXQ"));
					map_ckmx.put("PFJG", map_pd02.get("PFJG"));
					map_ckmx.put("LSJG", map_pd02.get("LSJG"));
					map_ckmx.put("JHJG", map_pd02.get("JHJG"));
					map_ckmx.put("BZLJ", map_pd02.get("BZLJ"));
					map_ckmx.put("SQSL", MedicineUtils.parseDouble(map_pd02.get("ZMSL"))
							- MedicineUtils.parseDouble(map_pd02.get("SPSL")));
					map_ckmx.put("SFSL", MedicineUtils.parseDouble(map_pd02.get("ZMSL"))
							- MedicineUtils.parseDouble(map_pd02.get("SPSL")));
					map_ckmx.put(
							"JHJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ckmx.get("SFSL"))
									* MedicineUtils.parseDouble(map_ckmx.get("JHJG"))));
					map_ckmx.put("TYPE", map_pd02.get("TYPE"));
					map_ckmx.put(
							"PFJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ckmx.get("SFSL"))
									* MedicineUtils.parseDouble(map_ckmx.get("PFJG"))));
					map_ckmx.put(
							"LSJE",
							MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(map_ckmx.get("SFSL"))
									* MedicineUtils.parseDouble(map_ckmx.get("LSJG"))));
					map_ckmx.put("KCSB", map_pd02.get("KCSB"));
					map_ckmx.put("JGID", jgid);
					map_ckmx.put("JBYWBZ", 1);
					list_ckmx.add(map_ckmx);
				}
			}
			if (map_rk.size() != 0) {
				dao.doSave("create", BSPHISEntryNames.YK_RK01, map_rk, false);
				for (Map<String, Object> map_rkmx : list_rkmx) {
					dao.doSave("create", BSPHISEntryNames.YK_RK02, map_rkmx,
							false);
				}
				StringBuffer hql_rkfs_update = new StringBuffer();//入库方式更新
				hql_rkfs_update
						.append("update YK_RKFS  set RKDH=RKDH+1 where RKFS=:rkfs and XTSB=:yksb");
				Map<String, Object> map_par_rkfs_update = new HashMap<String, Object>();
				map_par_rkfs_update.put("rkfs", pyfs);
				map_par_rkfs_update.put("yksb", yksb);
				dao.doUpdate(hql_rkfs_update.toString(), map_par_rkfs_update);
			}
			if (map_ck.size() != 0) {
				dao.doSave("create", BSPHISEntryNames.YK_CK01_FORM, map_ck,
						false);
				for (Map<String, Object> map_ckmx : list_ckmx) {
					dao.doSave("create", BSPHISEntryNames.YK_CK02, map_ckmx,
							false);
				}
				StringBuffer hql_ckfs_update = new StringBuffer();//更新出库方式对应的出库单号
				hql_ckfs_update
						.append("update YK_CKFS  set CKDH=CKDH+1 where CKFS=:ckfs and XTSB=:yksb");
				Map<String, Object> map_par_ckfs_update = new HashMap<String, Object>();
				map_par_ckfs_update.put("ckfs", pkfs);
				map_par_ckfs_update.put("yksb", yksb);
				dao.doUpdate(hql_ckfs_update.toString(), map_par_ckfs_update);
			}
			StringBuffer hql_pd01_update = new StringBuffer();//更新盘点01
			hql_pd01_update
					.append("update YK_PD01  set RKDH=:rkdh,CKDH=:ckdh,YSGH=:czgh,ZXRQ=:now where PDDH=:pddh and XTSB=:yksb");
			Map<String, Object> map_par_pd01_update = new HashMap<String, Object>();
			map_par_pd01_update.put("rkdh", rkdh);
			map_par_pd01_update.put("ckdh", ckdh);
			map_par_pd01_update.put("czgh", userid);
			map_par_pd01_update.put("now", nowDate);
			map_par_pd01_update.put("pddh", pddh);
			map_par_pd01_update.put("yksb", yksb);
			dao.doUpdate(hql_pd01_update.toString(), map_par_pd01_update);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点单提交失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点单提交失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "盘点单提交失败", e);
		}
		return MedicineUtils.getRetMap();

	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 按批次盘存数据查询
	 * @updateInfo
	 * @param body
	 * @param req
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> quertyInventoryData_PC(Map<String, Object> body,
			Map<String, Object> req, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			if ("create".equals(op)) {
				StringBuffer hql = new StringBuffer();
				hql.append(
						"select a.SBXH as KCSB,a.KCSL as SPSL,b.YPMC as YPMC,b.PYDM as PYDM,b.YPGG as YPGG,b.YPDW as YPDW,e.CDMC as YPCD,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,a.KCSL as KCSL from YK_KCMX  a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and d.YKSB=:yksb and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX ");
				if(req.containsKey("cnd")&&req.get("cnd")!=null){
					List<?> cnd=(List<?>)req.get("cnd");
					try {
						hql.append(" and ").append(ExpressionProcessor.instance().toString(cnd));
					} catch (ExpException e) {
						MedicineUtils.throwsException(logger, "盘点明细查询失败", e);
					}
				}
				hql.append(" order by b.PYDM");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				MedicineCommonModel model=new MedicineCommonModel(dao);
				ret=model.getPageInfoRecord(req,map_par,hql.toString(),null);
			} else {
				StringBuffer hql = new StringBuffer();
				hql.append(
						"select a.KCSB as KCSB,a.SPSL as SPSL,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,e.CDMC as YPCD,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,a.ZMSL as KCSL from YK_PD02 a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and a.XTSB=:yksb and a.XTSB=d.YKSB and a.PDDH=:pddh and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX ");
				if(req.containsKey("cnd")&&req.get("cnd")!=null){
					List<?> cnd=(List<?>)req.get("cnd");
					try {
						hql.append(" and ").append(ExpressionProcessor.instance().toString(cnd));
					} catch (ExpException e) {
						MedicineUtils.throwsException(logger, "盘点明细查询失败", e);
					}
				}
				hql.append(" order by a.KCSB");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				map_par.put("pddh", body.get("PDDH") + "");
				MedicineCommonModel model=new MedicineCommonModel(dao);
				ret=model.getPageInfoRecord(req,map_par,hql.toString(),null);
			}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 盘存明细数据查询
	 * @updateInfo
	 * @param body
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> quertyInventoryDataDetail(
			Map<String, Object> body, String op, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		long ypxh = MedicineUtils.parseLong(body.get("YPXH"));
		try {
			if ("create".equals(op)) {
				StringBuffer hql = new StringBuffer();
				hql.append(
						"select a.SBXH as KCSB,a.KCSL as SPSL,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,e.CDMC as YPCD,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,a.KCSL as KCSL from YK_KCMX a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and d.YKSB=:yksb and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX and a.YPXH=:ypxh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				map_par.put("ypxh", ypxh);
				ret = dao.doQuery(hql.toString(), map_par);
			} else {
				StringBuffer hql = new StringBuffer();
				hql.append(
						"select a.KCSB as KCSB,a.SPSL as SPSL,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,e.CDMC as YPCD,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,a.ZMSL as KCSL from YK_PD02 a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and a.XTSB=:yksb and a.XTSB=d.YKSB and a.PDDH=:pddh and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX and a.YPXH=:ypxh");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				map_par.put("ypxh", ypxh);
				map_par.put("pddh", body.get("PDDH") + "");
				ret = dao.doQuery(hql.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询失败", e);
		}
		return ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-1-7
	 * @description 查询库存盘点明细 不按批次
	 * @updateInfo
	 * @param body
	 * @param parameters
	 * @param op
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> quertyInventoryData(Map<String, Object> body,
			Map<String, Object> req, String op, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb =MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		MedicineCommonModel model=new MedicineCommonModel(dao);
			if ("create".equals(op)) {
				StringBuffer hql_sum = new StringBuffer();
				hql_sum.append(
						"select a.YPCD as YPCD,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,sum(a.KCSL) as KCSL,sum(a.KCSL) as SPSL,c.SXMC as YPSX,a.YPXH as YPXH from YK_KCMX a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and d.YKSB=:yksb and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX ");
				if(req.containsKey("cnd")&&req.get("cnd")!=null){
					List<?> cnd=(List<?>)req.get("cnd");
					try {
						hql_sum.append(" and ").append(ExpressionProcessor.instance().toString(cnd));
					} catch (ExpException e) {
						MedicineUtils.throwsException(logger, "盘点明细查询失败", e);
					}
				}
				hql_sum.append(" group by b.YPMC,b.YPGG,b.YPDW,c.SXMC,a.YPXH,a.YPCD order by b.YPMC");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", yksb);
				map_par.put("jgid", jgid);
				ret=model.getPageInfoRecord(req, map_par, hql_sum.toString(), null);
			} else {
				StringBuffer hql_sum = new StringBuffer();
				hql_sum.append(
						"select a.YPCD as YPCD,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,sum(a.ZMSL) as KCSL,sum(a.SPSL) as SPSL,c.SXMC as YPSX,a.YPXH as YPXH,a.PDDH as PDDH from YK_PD02 a,YK_TYPK b,YK_YPSX c,YK_YPXX d,YK_CDDZ e where a.YPXH=b.YPXH and a.YPCD=e.YPCD and a.XTSB=:yksb and a.XTSB=d.YKSB and a.PDDH=:pddh and a.YPXH=d.YPXH and a.JGID=:jgid and b.YPSX=c.YPSX ");
				if(req.containsKey("cnd")&&req.get("cnd")!=null){
					List<?> cnd=(List<?>)req.get("cnd");
					try {
						hql_sum.append(" and ").append(ExpressionProcessor.instance().toString(cnd));
					} catch (ExpException e) {
						MedicineUtils.throwsException(logger, "盘点明细查询失败", e);
					}
				}
				hql_sum.append(" group by b.YPMC,b.YPGG,b.YPDW,c.SXMC,a.YPXH,a.PDDH,a.YPCD order by b.YPMC");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yksb", MedicineUtils.parseLong(body.get("XTSB")));
				map_par.put("pddh", body.get("PDDH") + "");
				map_par.put("jgid", jgid);
				ret=model.getPageInfoRecord(req, map_par, hql_sum.toString(), null);
			}
			return ret;
	}
	
	

}
