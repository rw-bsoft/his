package phis.application.fsb.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

public class FamilySickBedDispensingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedDispensingModel.class);

	public FamilySickBedDispensingModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 家床发药按方式查询发药数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensingFs(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		int yzlx = MedicineUtils.parseInt(body.get("YZLX"));
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql = new StringBuffer();
		hql.append("select a.FYFS as FYFS,count(1) as FYTS,d.FSMC as FSMC,a.JGID as JGID from JC_TJ01 a,JC_TJ02 b,ZY_FYFS  d where a.FYFS=d.FYFS and a.JGID=:jgid  and a.TJYF=:yfsb  and a.JGID=b.JGID and a.TJXH=b.TJXH and a.FYBZ=0 and b.FYBZ=0 ");
		if (yzlx == 0) {
			hql.append(" and  ( a.YZLX=1)");
		} else if (yzlx == 1) {
			hql.append(" and  b.LSYZ=1 and a.YZLX=1");
		} else if (yzlx == 2) {
			hql.append(" and  b.LSYZ=0 and a.YZLX=1");
		} 
		hql.append(" group by a.FYFS,d.FSMC,a.JGID order by d.FSMC,a.FYFS ");
		List<Map<String, Object>> list_ret = null;
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("jgid", user.getManageUnit().getId());
			map_par.put("yfsb",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			list_ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床发药方式列表查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 家床发药病人列表
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensing_br(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			if (cnd != null) {
				StringBuffer hql = new StringBuffer();
				hql.append(
						"select distinct c.ZYH as ZYH,c.BRXM as BRXM,c.ZYHM as ZYHM,d.FSMC as FSMC,a.FYFS as FYFS,a.JGID as JGID from JC_TJ01 a,JC_TJ02 b,JC_BRRY c,ZY_FYFS d where a.TJXH=b.TJXH and a.FYBZ = 0 and b.FYBZ = 0 and a.JGID =:jgid and a.TJYF=:yfsb  and b.ZYH=c.ZYH and a.FYFS=d.FYFS and ")
						.append(ExpressionProcessor.instance().toString(cnd));
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("yfsb",
						MedicineUtils.parseLong(user.getProperty("pharmacyId")));
				map_par.put("jgid", user.getManageUnit().getId());
				ret = dao.doSqlQuery(hql.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "待发药记录按病人查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "待发药记录按病人查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 发药药品明细查询
	 * @updateInfo
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensing(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_kc = new ArrayList<Map<String, Object>>();// 用于存库存
		List<Map<String, Object>> list_kc_temp = new ArrayList<Map<String, Object>>();// 用于存已发的库存(临时减掉)
		List<Map<String, Object>> list_yz = new ArrayList<Map<String, Object>>();// 用于保存医嘱是否停用
		StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
		hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
		StringBuffer hql_kc = new StringBuffer();// 查询库存
		hql_kc.append("select YPXH as YPXH,YPCD as YPCD,sum(YPSL) as YPSL,LSJG as LSJG from YF_KCMX where YPCD=:ypcd and YPXH=:ypxh and YFSB=:yfsb and LSJG=:lsjg  and JYBZ!=1 group by YPXH,YPCD,LSJG ");
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		List<Map<String, Object>> list_tj02 = null;
		try {
			list_tj02 = dao.doList(cnd, null, "phis.application.fsb.schemas.JC_TJ02_YZFY");
			qzcl(list_tj02, yfsb);
			for (Map<String, Object> map_tj02 : list_tj02) {
				if (MedicineUtils.parseInt(map_tj02.get("CFTS")) == 0) {
					map_tj02.put("CFTS", "");
				}
				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
				map_par_yfbz.put("yfsb", yfsb);
				map_par_yfbz.put("ypxh",
						MedicineUtils.parseLong(map_tj02.get("YPXH")));
				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
						map_par_yfbz);
				if (map_yfbz == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "无对应药房药品信息!");
				}
				double fysl = MedicineUtils.formatDouble(4,
						MedicineUtils.parseDouble(map_tj02.get("FYSL"))
								* MedicineUtils.parseInt(map_tj02.get("YFBZ"))
								/ MedicineUtils.parseInt(map_yfbz.get("YFBZ")));
				map_tj02.put("ZT", "可发");
				map_tj02.put(
						"FYJE",
						MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(map_tj02.get("YCSL"))
										* MedicineUtils.parseDouble(map_tj02
												.get("YPDJ"))));
				int i_kc = 0;// 判断缓存中是否有该库存
				for (Map<String, Object> map_kc : list_kc) {
					if (MedicineUtils.compareMaps(map_kc, new String[] {
							"YPXH", "YPCD", "LSJG" }, map_tj02, new String[] {
							"YPXH", "YPCD", "YPDJ" })) {
						i_kc = 1;
						for (Map<String, Object> map_kc_temp : list_kc_temp) {
							if (MedicineUtils.compareMaps(map_kc, new String[] {
									"YPXH", "YPCD", "LSJG" }, map_kc_temp,
									new String[] { "YPXH", "YPCD", "LSJG" })) {
								if (MedicineUtils.parseDouble(map_kc
										.get("YPSL"))
										- MedicineUtils.parseDouble(map_kc_temp
												.get("YPSL")) - fysl < 0) {
									map_tj02.put("ZT", "缺药");
									break;
								} else {
									map_kc_temp.put(
											"YPSL",
											MedicineUtils
													.parseDouble(map_kc_temp
															.get("YPSL"))
													+ fysl);
								}
							}
						}
						break;
					}
				}
				if (i_kc == 0) {
					Map<String, Object> map_par_kc = new HashMap<String, Object>();
					map_par_kc.put("ypxh",
							MedicineUtils.parseLong(map_tj02.get("YPXH")));
					map_par_kc.put("ypcd",
							MedicineUtils.parseLong(map_tj02.get("YPCD")));
					map_par_kc.put("yfsb", yfsb);
					map_par_kc.put("lsjg",
							MedicineUtils.parseDouble(map_tj02.get("YPDJ")));
					List<Map<String, Object>> list_kc_q = dao.doSqlQuery(
							hql_kc.toString(), map_par_kc);
					if (list_kc_q == null || list_kc_q.size() == 0) {
						map_tj02.put("ZT", "缺药");
						continue;
					}
					Map<String, Object> map_kc = list_kc_q.get(0);
					if (MedicineUtils.parseDouble(map_kc.get("YPSL")) < fysl) {
						map_tj02.put("ZT", "缺药");
						continue;
					} else {
						list_kc.add(map_kc);
						Map<String, Object> map_kc_temp = new HashMap<String, Object>();
						for (String key : map_kc.keySet()) {
							map_kc_temp.put(key, map_kc.get(key));
						}
						map_kc_temp.put("YPSL", fysl);
						// map_tj02.put("FYJE", formatDouble(4,
						// fysl*parseDouble(map_kc_temp.get("LSJG"))));//张伟一定要求价格从库存里取..虽然没什么意义..
						list_kc_temp.add(map_kc_temp);
					}
				}
				int i_yz = 0;
				Map<String, Object> map_yz = new HashMap<String, Object>();
				for (Map<String, Object> map_yz_c : list_yz) {
					if (MedicineUtils.parseLong(map_yz_c.get("YZXH")) == MedicineUtils
							.parseLong(map_tj02.get("YZXH"))) {
						i_yz = 1;
						map_yz = map_yz_c;
						break;
					}
				}
				if (i_yz == 0) {
					map_yz = dao.doLoad("phis.application.fsb.schemas.JC_BRYZ_JCFY",
							MedicineUtils.parseLong(map_tj02.get("YZXH")));
					if (map_yz == null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "错误数据,无对应医嘱!");
					}
					list_yz.add(map_yz);
				}
				if (MedicineUtils.parseInt(map_yz.get("LSBZ")) == 1) {
					map_tj02.put("ZT", "停嘱");
					map_tj02.put("FYBZ", 3);// 停嘱不发
				} else if (MedicineUtils.parseInt(map_yz.get("LSYZ")) == 0
						&& map_yz.get("TZSJ") != null) {
					if (((Date) map_yz.get("TZSJ")).getTime() < ((Date) map_tj02
							.get("QRRQ")).getTime()) {
						map_tj02.put("ZT", "停嘱");
						map_tj02.put("FYBZ", 3);// 停嘱不发
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "发药明细查询失败", e);
		}
		return list_tj02;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 取整策略
	 * @updateInfo
	 * @param list_tj02
	 * @param yfsb
	 * @throws ModelDataOperationException
	 */
	public void qzcl(List<Map<String, Object>> list_tj02, long yfsb)
			throws ModelDataOperationException {
		StringBuffer hql_qzcl = new StringBuffer();// 查询取整策略
		hql_qzcl.append("select QZCL as QZCL from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
		for (int i = 0; i < list_tj02.size(); i++) {
			Map<String, Object> map_tj02 = list_tj02.get(i);
			Map<String, Object> map_par_qzcl = new HashMap<String, Object>();
			map_par_qzcl.put("ypxh",
					MedicineUtils.parseLong(map_tj02.get("YPXH")));
			map_par_qzcl.put("yfsb", yfsb);
			int qzcl = 1;
			try {
				Map<String, Object> map_qzcl = dao.doLoad(hql_qzcl.toString(),
						map_par_qzcl);
				if (map_qzcl == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "取整策略查询失败");
				}
				qzcl = MedicineUtils.parseInt(map_qzcl.get("QZCL"));
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "取整策略查询失败", e);
			}
			double fysl = 0;
			// 通过取整策略计算发药数量,发药金额
			if (qzcl == 0) {// 按次取整
				fysl = MedicineUtils.parseDouble(Math.ceil(MedicineUtils
						.parseDouble(map_tj02.get("YCSL"))));
			} else if (qzcl == 1) {// 按天取整
				int isqz = 0;
				for (int j = i + 1; j < list_tj02.size(); j++) {
					Map<String, Object> map_tj02_temp = list_tj02.get(j);
					// 判断后面有没相同医嘱的同种药品,有就跳出循环
					if (MedicineUtils.compareMaps(map_tj02_temp, new String[] {
							"YZXH", "YPXH", "YPCD", "YPDJ" }, map_tj02,
							new String[] { "YZXH", "YPXH", "YPCD", "YPDJ" })) {
						isqz = 1;
						fysl = MedicineUtils
								.formatDouble(2, MedicineUtils
										.parseDouble(map_tj02.get("YCSL")));
						break;
					}
				}
				if (isqz == 0) {
					int num = 0;
					for (Map<String, Object> m : list_tj02) {
						if (MedicineUtils
								.compareMaps(m, new String[] { "YZXH", "YPXH",
										"YPCD", "YPDJ" }, map_tj02,
										new String[] { "YZXH", "YPXH", "YPCD",
												"YPDJ" })) {
							num++;
						}
					}
					// 按每天发药数量去整时 取相同医嘱药品总和取整-除这条以外的记录和
					fysl = MedicineUtils.parseDouble(Math.ceil(MedicineUtils
							.parseDouble(map_tj02.get("YCSL")) * num))
							- MedicineUtils.formatDouble(
									2,
									MedicineUtils.parseDouble(map_tj02
											.get("YCSL")) * (num - 1));
				}
			} else {// 不取整
				fysl = MedicineUtils.formatDouble(2,
						MedicineUtils.parseDouble(map_tj02.get("YCSL")));
			}
			map_tj02.put("FYSL", fysl);
			map_tj02.put("QZCL", qzcl);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 发药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> savefamilySickBedDispensing(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Date d = new Date();
		Map<String, Object> ret = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制(12小时制会影响时间比对)
		List<Map<String, Object>> list_bqbr = (List<Map<String, Object>>) body
				.get("bq");// 家床tj01
		List<Map<String, Object>> list_fymx = (List<Map<String, Object>>) body
				.get("fymx");// 家床tj02
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();
		String userid = user.getUserId();
		StringBuffer hql_tj02_update = new StringBuffer();// jc_tj02更新发药标志
		hql_tj02_update
				.append("update JC_TJ02  set FYBZ=1,FYGH=:fygh,FYRQ=:fyrq ,FYJE=:fyje where JLXH=:jlxh");
		StringBuffer hql_tj02_bqyz = new StringBuffer();// 查询相同yzxh的提交是否都发药
		hql_tj02_bqyz.append(" YZXH=:yzxh and FYBZ=0");
		StringBuffer hql_yp = new StringBuffer();// 查询药品的基本药物标志
		hql_yp.append("select JYLX as JYLX from YK_TYPK  where YPXH=:ypxh");
		StringBuffer hql_bq01_update = new StringBuffer();// 更新jc_tj01的fybz
		hql_bq01_update.append("update JC_TJ01 set FYBZ=1 where TJXH=:tjxh");
		StringBuffer hql_count = new StringBuffer();// 查询相同tjxh的提交明细是否都已发药
		hql_count.append("  TJXH=:tjxh and FYBZ=0 ");
		StringBuffer hql_tj02_tz_update = new StringBuffer();// JC_tj02停嘱更新
		hql_tj02_tz_update.append("update JC_TJ02 set FYBZ=3 where JLXH=:jlxh");
		StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
		hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
		StringBuffer hql_sffy = new StringBuffer();// 查询是否已经发过药
		hql_sffy.append("select FYBZ as FYBZ from JC_TJ02 where JLXH=:jlxh");
		StringBuffer hql_yz_update=new StringBuffer();//更新家床医嘱
		hql_yz_update.append("update JC_BRYZ set SYBZ=:sybz,QRSJ=:qrsj,TJZX=2 where JLXH=:jlxh");
		try {
			// 更新YF_FYJL
			Map<String, Object> map_yf_fyjl_data = new HashMap<String, Object>();// yf_fyjl保存记录
			map_yf_fyjl_data.put("JGID", jgid);
			map_yf_fyjl_data.put("FYSJ", d);
			map_yf_fyjl_data.put("FYGH", userid);
			map_yf_fyjl_data.put("FYBQ",
					MedicineUtils.parseLong(list_bqbr.get(0).get("TJBQ")));
			if (list_fymx.size() > 0) {
				if (MedicineUtils.parseInt(list_fymx.get(0).get("YZLX")) == 2) {
					map_yf_fyjl_data.put("FYLX", 2);
				} else if (MedicineUtils.parseInt(list_fymx.get(0).get("YZLX")) == 3) {
					map_yf_fyjl_data.put("FYLX", 3);
				} else {
					map_yf_fyjl_data.put("FYLX", 1);
				}
			}
			map_yf_fyjl_data.put("JGID", jgid);
			map_yf_fyjl_data.put("YFSB", yfsb);
			map_yf_fyjl_data.put("FYFS",
					MedicineUtils.parseLong(list_bqbr.get(0).get("FYFS")));
			map_yf_fyjl_data.put("DYPB", 0);
			map_yf_fyjl_data = dao.doSave("create","phis.application.fsb.schemas.JC_FYJL",
					map_yf_fyjl_data, false);
			// 返回给打印用的
			Map<String, Object> otherRet = new HashMap<String, Object>();
			otherRet.put("FYBQ", list_bqbr.get(0).get("TJBQ"));
			otherRet.put("JLID", map_yf_fyjl_data.get("JLID"));
			otherRet.put("FYSJ", d);
			ret.put("otherRet", otherRet);
			// 计算发药数量和金额
			qzcl(list_fymx, yfsb);
			boolean isRollBack = true;// 判断是否要回滚,当没有记录新增时回滚
			for (Map<String, Object> map_fymx : list_fymx) {
				Map<String, Object> map_par_sffy = new HashMap<String, Object>();
				map_par_sffy.put("jlxh",
						MedicineUtils.parseLong(map_fymx.get("JLXH")));
				Map<String, Object> map_sffy = dao.doLoad(hql_sffy.toString(),
						map_par_sffy);
				if (map_sffy == null
						|| MedicineUtils.parseDouble(map_sffy.get("FYBZ")) != 0) {
					// System.out.println("已发");
					continue;
				}
				Map<String, Object> map_bqyz = new HashMap<String, Object>();// 家床医嘱
				map_bqyz = dao.doLoad("phis.application.fsb.schemas.JC_BRYZ_JCFY" ,
						MedicineUtils.parseLong(map_fymx.get("YZXH")));
				// 判断是否停嘱
				if (MedicineUtils.parseInt(map_fymx.get("FYBZ")) == 3) {
					Map<String, Object> map_par_tj02_tz = new HashMap<String, Object>();
					map_par_tj02_tz.put("jlxh",
							MedicineUtils.parseLong(map_fymx.get("JLXH")));
					dao.doUpdate(hql_tj02_tz_update.toString(), map_par_tj02_tz);
					isRollBack = false;
					Map<String, Object> map_par_bqyz = new HashMap<String, Object>();
					map_par_bqyz.put("yzxh",
							MedicineUtils.parseLong(map_fymx.get("YZXH")));
					long l = dao.doCount("JC_TJ02", hql_tj02_bqyz.toString(),
							map_par_bqyz);
					Map<String,Object> map_par_yzgx=new HashMap<String,Object>();
					map_par_yzgx.put("jlxh", MedicineUtils.parseLong(map_fymx.get("YZXH")));
					map_par_yzgx.put("sybz", 1);
					map_par_yzgx.put("qrsj", (Date) map_bqyz.get("QRSJ"));
					if (l == 0) {
						map_par_yzgx.put("sybz", 0);
						//map_bqyz.put("SYBZ", 0);
					}
					if (map_bqyz.get("QRSJ") == null
							|| ((Date) map_bqyz.get("QRSJ")).getTime() < (sdf
									.parse(map_fymx.get("QRRQ") + ""))
									.getTime()) {
//						map_bqyz.put("QRSJ",
//								sdf.parse(map_fymx.get("QRRQ") + ""));
						map_par_yzgx.put("qrsj", sdf.parse(map_fymx.get("QRRQ") + ""));
					}
					// 更新家床医嘱
					dao.doUpdate(hql_yz_update.toString(), map_par_yzgx);
//					dao.doSave("update", "phis.application.fsb.schemas.JC_BRYZ_JCFY",
//							map_bqyz, false);
					continue;
				}
				Map<String, Object> map_bqbr = new HashMap<String, Object>();// jc_tj02对应的jc_tj01记录
				for (int i = 0; i < list_bqbr.size(); i++) {
					if ((MedicineUtils.parseLong(list_bqbr.get(i).get("TJXH")) == MedicineUtils
							.parseLong(map_fymx.get("TJXH")))
							|| (MedicineUtils.parseLong(list_bqbr.get(i).get(
									"ZYH")) == MedicineUtils.parseLong(map_fymx
									.get("ZYH")))) {
						map_bqbr = list_bqbr.get(i);
					}
				}
				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
				map_par_yfbz.put("yfsb", yfsb);
				map_par_yfbz.put("ypxh",
						MedicineUtils.parseLong(map_fymx.get("YPXH")));
				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
						map_par_yfbz);
				if (map_yfbz == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "无对应的药房药品");
				}
				int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
				map_fymx.put(
						"YPDJ",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(map_fymx.get("YPDJ"))
										* yfbz
										/ MedicineUtils.parseInt(map_fymx
												.get("YFBZ"))));
				map_fymx.put("YFSB", yfsb);
				map_fymx.put(
						"YPSL",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(map_fymx.get("FYSL"))
										* MedicineUtils.parseInt(map_fymx
												.get("YFBZ")) / yfbz));
				PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
						dao);
				List<Map<String, Object>> list_fymx_temp = new ArrayList<Map<String, Object>>();
				list_fymx_temp.add(map_fymx);
				List<Map<String, Object>> list_ret = model
						.queryAndLessInventory(list_fymx_temp, ctx);
				if (list_ret == null
						|| (list_ret.size() == 1 && list_ret.get(0)
								.containsKey("ypxh"))) {
					isRollBack = false;
					StringBuffer s_kcbg = new StringBuffer();
					s_kcbg.append("[药品:").append(map_fymx.get("YPMC"))
							.append(",产地:").append(map_fymx.get("CDMC"))
							.append("]库存不够");
					// list_kcbg.add(s_kcbg.toString());
					ret.put("code", 9000);
					ret.put("msg", s_kcbg.toString());
					return ret;
					// continue;
				}

				int yplx = MedicineUtils.parseInt(map_bqyz.get("YPLX"));
				long fyxm = BSPHISUtil
						.getfygb(yplx,
								MedicineUtils.parseLong(map_fymx.get("YPXH")),
								dao, ctx);// 费用项目
				FamilySickBedDoctorExecutionModel domodel = new FamilySickBedDoctorExecutionModel(dao);
				double zfbl = domodel.getZfbl(
						MedicineUtils.parseLong(map_fymx.get("ZYH")),
						MedicineUtils.parseLong(map_fymx.get("YPXH")), fyxm);
				Map<String, Object> map_par_yp = new HashMap<String, Object>();
				map_par_yp.put("ypxh",
						MedicineUtils.parseLong(map_fymx.get("YPXH")));
				Map<String, Object> map_yp = dao.doLoad(hql_yp.toString(),
						map_par_yp);
				double fyje = 0;// 费用总金额
				for (Map<String, Object> map_kcmx : list_ret) {
					// 更新JC_FYMX
					Map<String, Object> map_jc_fymx = new HashMap<String, Object>();
					map_jc_fymx.put("JGID", jgid);
					map_jc_fymx.put("ZYH", map_fymx.get("ZYH"));
					map_jc_fymx.put("FYRQ", map_fymx.get("JFRQ"));
					map_jc_fymx.put("FYXH", map_fymx.get("YPXH"));
					map_jc_fymx.put("FYMC", map_bqyz.get("YZMC"));
					map_jc_fymx.put("YPCD", map_fymx.get("YPCD"));
					map_jc_fymx.put("FYSL", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
									* yfbz
									/ MedicineUtils.parseInt(map_fymx
											.get("YFBZ"))));
					map_jc_fymx.put("FYDJ", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_jc_fymx.put("ZJJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_jc_fymx.get("FYSL"))
									* MedicineUtils.parseDouble(map_jc_fymx
											.get("FYDJ"))));
					fyje += MedicineUtils.parseDouble(map_jc_fymx.get("ZJJE"));
					map_jc_fymx.put("ZFJE", MedicineUtils.formatDouble(
							2,
							zfbl
									* MedicineUtils.parseDouble(map_jc_fymx
											.get("ZJJE"))));
					map_jc_fymx.put("YSGH", map_bqyz.get("YSGH"));
					map_jc_fymx.put("SRGH", userid);
					map_jc_fymx.put("QRGH", userid);
					map_jc_fymx.put("FYBQ", map_bqyz.get("SRKS"));
					map_jc_fymx.put("FYKS", map_fymx.get("FYKS"));
					map_jc_fymx.put("ZXKS", map_bqyz.get("ZXKS") == null ? 0
							: map_bqyz.get("ZXKS"));
					map_jc_fymx.put("JFRQ", d);
					map_jc_fymx.put("XMLX", 2);
					map_jc_fymx.put("YPLX", map_bqyz.get("YPLX"));
					map_jc_fymx.put("FYXM", fyxm);
					map_jc_fymx.put("ZFBL", zfbl);
					map_jc_fymx.put("YZXH", map_fymx.get("YZXH"));
					map_jc_fymx.put("JSCS", 0);
					map_jc_fymx.put("ZLJE", 0);
					map_jc_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
					map_jc_fymx.put("YEPB", map_fymx.get("YEPB"));
					map_jc_fymx.put("DZBL", 0);
					map_jc_fymx = dao.doSave("create", "phis.application.fsb.schemas.JC_FYMX", map_jc_fymx, false);
					isRollBack = false;
					// 更新YF_JCFYMX
					Map<String, Object> map_yf_zyfymx_data = new HashMap<String, Object>();
					map_yf_zyfymx_data.put("JGID", jgid);
					map_yf_zyfymx_data.put("YFSB", yfsb);
					map_yf_zyfymx_data.put("CKBH", 0);
					map_yf_zyfymx_data.put("FYLX", 1);
					map_yf_zyfymx_data.put("ZYH", map_fymx.get("ZYH"));
					map_yf_zyfymx_data.put("FYRQ", map_fymx.get("JFRQ"));
					map_yf_zyfymx_data.put("YPXH", map_fymx.get("YPXH"));
					map_yf_zyfymx_data.put("YPCD", map_fymx.get("YPCD"));
					map_yf_zyfymx_data.put("YPGG", map_fymx.get("YFGG"));
					map_yf_zyfymx_data.put("YFDW", map_fymx.get("YFDW"));
					map_yf_zyfymx_data.put("YFBZ", map_fymx.get("YFBZ"));
					map_yf_zyfymx_data.put("YPSL", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
									* yfbz
									/ MedicineUtils.parseInt(map_fymx
											.get("YFBZ"))));
					map_yf_zyfymx_data.put("YPDJ", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_yf_zyfymx_data.put("ZFBL", zfbl);
					map_yf_zyfymx_data.put("QRGH", userid);
					map_yf_zyfymx_data.put("JFRQ", d);
					map_yf_zyfymx_data.put("YPLX", map_bqyz.get("YPLX"));
					map_yf_zyfymx_data.put("FYKS", map_fymx.get("FYKS"));
					map_yf_zyfymx_data.put("LYBQ", map_bqyz.get("SRKS"));
					map_yf_zyfymx_data.put(
							"ZXKS",
							map_bqyz.get("ZXKS") == null ? 0 : map_bqyz
									.get("ZXKS"));
					map_yf_zyfymx_data.put("YZXH", map_fymx.get("YZXH"));
					map_yf_zyfymx_data.put("YEPB", map_fymx.get("YEPB"));
					map_yf_zyfymx_data.put("ZFPB", zfbl == 1 ? 0 : 1);// zfbl =
																		// 1时是0
																		// 否则是1
					map_yf_zyfymx_data.put("FYFS", map_bqbr.get("FYFS"));
					map_yf_zyfymx_data.put("LSJG", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_yf_zyfymx_data.put("PFJG", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("PFJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_yf_zyfymx_data.put("JHJG", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("JHJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_yf_zyfymx_data.put("FYJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_yf_zyfymx_data
									.get("YPSL"))
									* MedicineUtils
											.parseDouble(map_yf_zyfymx_data
													.get("YPDJ"))));
					map_yf_zyfymx_data.put("LSJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_yf_zyfymx_data
									.get("LSJG"))
									* MedicineUtils
											.parseDouble(map_yf_zyfymx_data
													.get("YPSL"))));
					map_yf_zyfymx_data.put("PFJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_yf_zyfymx_data
									.get("PFJG"))
									* MedicineUtils
											.parseDouble(map_yf_zyfymx_data
													.get("YPSL"))));
					map_yf_zyfymx_data.put("JHJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_yf_zyfymx_data
									.get("JHJG"))
									* MedicineUtils
											.parseDouble(map_yf_zyfymx_data
													.get("YPSL"))));
					map_yf_zyfymx_data.put("YPPH", map_kcmx.get("YPPH"));
					map_yf_zyfymx_data.put("YPXQ", map_kcmx.get("YPXQ"));
					map_yf_zyfymx_data.put("TYGL", 0);
					map_yf_zyfymx_data.put(
							"JBYWBZ",MedicineUtils.parseInt(map_yp.get("JYLX")));
					map_yf_zyfymx_data.put("KCSB", map_kcmx.get("KCSB"));
					map_yf_zyfymx_data.put("TJXH", map_fymx.get("JLXH"));
					map_yf_zyfymx_data.put("TYXH", 0);
					map_yf_zyfymx_data
							.put("JLID", map_yf_fyjl_data.get("JLID"));
					map_yf_zyfymx_data.put("JFID", map_jc_fymx.get("JLXH"));
					dao.doSave("create","phis.application.fsb.schemas.YF_JCFYMX",
							map_yf_zyfymx_data, false);
				}
				// 更新jc_tj02
				Map<String, Object> map_par_tj02_update = new HashMap<String, Object>();
				map_par_tj02_update.put("jlxh",
						MedicineUtils.parseLong(map_fymx.get("JLXH")));
				map_par_tj02_update.put("fygh", userid + "");
				map_par_tj02_update.put("fyrq", d);
				map_par_tj02_update.put("fyje", fyje);
				dao.doUpdate(hql_tj02_update.toString(), map_par_tj02_update);
				Map<String, Object> map_par_bqyz = new HashMap<String, Object>();
				map_par_bqyz.put("yzxh",
						MedicineUtils.parseLong(map_fymx.get("YZXH")));
				long l = dao.doCount("JC_TJ02", hql_tj02_bqyz.toString(),
						map_par_bqyz);
				Map<String,Object> map_par_yzgx=new HashMap<String,Object>();
				map_par_yzgx.put("jlxh", MedicineUtils.parseLong(map_fymx.get("YZXH")));
				map_par_yzgx.put("sybz", 1);
				map_par_yzgx.put("qrsj", (Date) map_bqyz.get("QRSJ"));
				if (l == 0) {
					map_par_yzgx.put("sybz", 0);
					//map_bqyz.put("SYBZ", 0);
				}
				if (map_bqyz.get("QRSJ") == null
						|| ((Date) map_bqyz.get("QRSJ")).getTime() < (sdf
								.parse(map_fymx.get("QRRQ") + ""))
								.getTime()) {
//					map_bqyz.put("QRSJ",
//							sdf.parse(map_fymx.get("QRRQ") + ""));
					map_par_yzgx.put("qrsj", sdf.parse(map_fymx.get("QRRQ") + ""));
				}
				// 更新家床医嘱
				dao.doUpdate(hql_yz_update.toString(), map_par_yzgx);
//				dao.doSave("update","phis.application.fsb.schemas.JC_BRYZ_ZYFY", map_bqyz,
//						false);
			}
			if (isRollBack) {
				Session session = (Session) ctx.get(Context.DB_SESSION);
				session.getTransaction().rollback();
				ret.put("code", 9000);
				ret.put("msg", "未找到发药记录");
				return ret;
			}
			// 更新jc_tj01(明细都发药后fybz更新为1)
			List<Long> list_tjxh = new ArrayList<Long>();
			for (Map<String, Object> map_fymx : list_fymx) {
				if (list_tjxh.contains(MedicineUtils.parseLong(map_fymx
						.get("TJXH")))) {
					continue;
				}
				list_tjxh.add(MedicineUtils.parseLong(map_fymx.get("TJXH")));
			}
			for (long tjxh : list_tjxh) {
				Map<String, Object> map_par_count = new HashMap<String, Object>();
				map_par_count.put("tjxh", tjxh);
				long l = dao.doCount("JC_TJ02", hql_count.toString(),
						map_par_count);
				if (l > 0) {
					continue;
				}
				dao.doUpdate(hql_bq01_update.toString(), map_par_count);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床发药失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "家床发药验证失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "家床发药失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 全退
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicineFullRefund(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_bqbr = (List<Map<String, Object>>) body
				.get("bq");// 家床tj01
		StringBuffer hql_tj01 = new StringBuffer();// jc_tj01的fybz改为1
		hql_tj01.append("update JC_TJ01 set FYBZ=1 where TJXH=:tjxh");
		StringBuffer hql_tj02 = new StringBuffer();// jc_tj02的fybz改为2
		hql_tj02.append("update JC_TJ02  set FYBZ=2 where TJXH=:tjxh and FYBZ!=1");
		StringBuffer hql_tj02_load = new StringBuffer();// 通过jc_tj01查找02记录
		hql_tj02_load
				.append(" select  distinct YZXH as YZXH from JC_TJ02 where TJXH=:tjxh");
		StringBuffer hql_tj02_bqyz = new StringBuffer();// 查询相同yzxh的tj02是否全都退回了
		hql_tj02_bqyz.append(" YZXH=:yzxh and YFBZ=0");
		StringBuffer hql_bqyz = new StringBuffer();// 更新家床医嘱
		hql_bqyz.append(" update JC_BRYZ set SYBZ=0,TJZX=0 where JLXH=:yzxh");
		StringBuffer hql_tjxh = new StringBuffer();// 查询提交序号
		hql_tjxh.append("select distinct a.TJXH as TJXH from JC_TJ01 a,JC_TJ02 b where a.TJXH=b.TJXH and b.ZYH=:zyh and a.FYBZ=0 and b.FYBZ=0");
		Map<String, Object> map_yzxh_isUpdate = new HashMap<String, Object>();// 用于存已经更新过的医嘱
		List<Long> list_tjxh = new ArrayList<Long>();
		try {
			for (Map<String, Object> map_bqbr : list_bqbr) {
				if (map_bqbr.containsKey("ZYH")) {
					Map<String, Object> map_par_tjxh = new HashMap<String, Object>();
					map_par_tjxh.put("zyh",
							MedicineUtils.parseLong(map_bqbr.get("ZYH")));
					List<Map<String, Object>> list_tjxh_t = dao.doSqlQuery(
							hql_tjxh.toString(), map_par_tjxh);
					if (list_tjxh_t == null || list_tjxh_t.size() == 0) {
						continue;
					}
					for (Map<String, Object> map_tjxh : list_tjxh_t) {
						list_tjxh.add(MedicineUtils.parseLong(map_tjxh
								.get("TJXH")));
					}
				} else {
					list_tjxh
							.add(MedicineUtils.parseLong(map_bqbr.get("TJXH")));
				}
			}
			for (long tjxh : list_tjxh) {
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("tjxh", tjxh);
				dao.doUpdate(hql_tj01.toString(), map_par);
				dao.doUpdate(hql_tj02.toString(), map_par);
				List<Map<String, Object>> list_yzxh = dao.doSqlQuery(
						hql_tj02_load.toString(), map_par);// 查询tj02中的yzxh
				for (Map<String, Object> map_yzxh : list_yzxh) {
					Map<String, Object> map_par_yzxh = new HashMap<String, Object>();
					long yzxh = MedicineUtils.parseLong(map_yzxh.get("YZXH"));
					map_par_yzxh.put("yzxh", yzxh);
					long l = dao.doCount("JC_TJ02", hql_tj02_bqyz.toString(),
							map_par_yzxh);
					if (l == 0) {
						// 判断yzxh是否已被更新过
						if (!map_yzxh_isUpdate.containsKey(yzxh + "")) {
							dao.doUpdate(hql_bqyz.toString(), map_par_yzxh);
							map_yzxh_isUpdate.put(yzxh + "", 1);
						}
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床全部退回失败", e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-5
	 * @description 医嘱退回
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicineRefund(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_fymx = (List<Map<String, Object>>) body
				.get("fymx");// 家床tj02
		StringBuffer hql_tj02_update = new StringBuffer();// 更新jc_tj02的发药标志字段
		hql_tj02_update
				.append("update JC_TJ02 set FYBZ=2 where JLXH=:jlxh and FYBZ!=1");
		StringBuffer hql_tj01_update = new StringBuffer();// 更新jc_tj01的发药标志字段
		hql_tj01_update.append("update JC_TJ01  set FYBZ=1 where TJXH=:tjxh");
		StringBuffer hql_tj02_count = new StringBuffer();// 查询提交明细是否都退回
		hql_tj02_count.append(" TJXH=:tjxh and FYBZ=0");
		StringBuffer hql_tj02_bqyz_count = new StringBuffer();// 用于判断相同yzxh的明细是否都已退回
		hql_tj02_bqyz_count.append(" YZXH=:yzxh and FYBZ=0");
		StringBuffer hql_bqyz_update = new StringBuffer();// 更新医嘱表的sybz字段
		hql_bqyz_update.append(" update JC_BRYZ set SYBZ=0,TJZX=0 where JLXH=:yzxh");
		Map<String, Object> map_yzxh_isUpdate = new HashMap<String, Object>();// 用于存已经更新过的医嘱
		try {
			for (Map<String, Object> map_fymx : list_fymx) {
				Map<String, Object> map_par_jlxh = new HashMap<String, Object>();
				map_par_jlxh.put("jlxh",
						MedicineUtils.parseLong(map_fymx.get("JLXH")));
				dao.doUpdate(hql_tj02_update.toString(), map_par_jlxh);
				Map<String, Object> map_par_yzxh = new HashMap<String, Object>();
				map_par_yzxh.put("yzxh",
						MedicineUtils.parseLong(map_fymx.get("YZXH")));
				long l = dao.doCount("JC_TJ02", hql_tj02_bqyz_count.toString(),
						map_par_yzxh);
				if (l == 0) {
					if (!map_yzxh_isUpdate.containsKey(map_fymx.get("YZXH")
							+ "")) {
						dao.doUpdate(hql_bqyz_update.toString(), map_par_yzxh);
						map_yzxh_isUpdate.put(map_fymx.get("YZXH") + "", 1);
					}
				}
			}
			List<Long> list_tjxh = new ArrayList<Long>();
			for (Map<String, Object> map_fymx : list_fymx) {
				if (list_tjxh.contains(MedicineUtils.parseLong(map_fymx
						.get("TJXH")))) {
					continue;
				}
				list_tjxh.add(MedicineUtils.parseLong(map_fymx.get("TJXH")));
			}
			for (long tjxh : list_tjxh) {
				Map<String, Object> map_par_tjxh = new HashMap<String, Object>();
				map_par_tjxh.put("tjxh", tjxh);
				long l = dao.doCount("JC_TJ02", hql_tj02_count.toString(),
						map_par_tjxh);
				if (l == 0) {
					dao.doUpdate(hql_tj01_update.toString(), map_par_tjxh);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医嘱退回失败", e);
		}
	}

}
