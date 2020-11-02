package phis.application.pha.source;

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

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

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
 * 药房盘点model
 * 
 * @author caijy
 * 
 */
public class PharmacyInventoryProcessingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PharmacyInventoryProcessingModel.class);

	public PharmacyInventoryProcessingModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 盘点处理-开始
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePharmacyInventoryProcessing(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		StringBuffer hql_count = new StringBuffer();
		hql_count.append("RKPB=0 and YFSB=:yfsb");
		Map<String, Object> ret = MedicineUtils.getRetMap("有单子未确认",9000);
		long l = 0;
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		try {
			// 判断是否有单子未确认
			l = dao.doCount("YF_RK01", hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count.append("CKPB=0 and YFSB=:yfsb");
			l = dao.doCount("YF_CK01", hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count
					.append("CKBZ = 1 and RKBZ = 0 and TYPB = 0 and SQYF=:yfsb");
			l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count
					.append("CKBZ = 0 and RKBZ = 1 and TYPB = 1 and MBYF=:yfsb");
			l = dao.doCount("YF_DB01", hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count
					.append("CKPB = 1 and LYPB = 0 and YFSB=:yfsb and b.DYFS!=6 and a.CKFS=b.CKFS and a.XTSB=b.XTSB and a.JGID=:jgid and a.CZGH=c.PERSONID");
			map_par.put("jgid", jgid);
			l = dao.doCount("YK_CK01 a,YK_CKFS b,SYS_Personnel  c",
					hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count
					.append("CKPB = 0 and LYPB = 1 and YFSB=:yfsb and b.DYFS=6 and a.CKFS=b.CKFS and a.XTSB=b.XTSB and a.JGID=:jgid and a.CZGH=c.PERSONID");
			l = dao.doCount(" YK_CK01 a,YK_CKFS b,SYS_Personnel c ",
					hql_count.toString(), map_par);
			if (l > 0) {
				return ret;
			}
			hql_count = new StringBuffer();
			hql_count.append("YFSB=:yfsb and PDWC = 0 and JGID=:jgid");
			l = dao.doCount("YF_YK01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已有未完成盘点单,不能生成新单!请刷新窗口!");
			}
			hql_count = new StringBuffer();
			hql_count.append("YFSB=:yfsb and JGID=:jgid");
			l = dao.doCount("YF_KCMX", hql_count.toString(), map_par);
			if (l == 0) {
				return MedicineUtils.getRetMap("药房没有库存，不能生成盘点单!");
			}
			int kcpd_pc = queryKCPD_PC(ctx);
			// int kcpd_pc = 1;// 暂时先定死,只能按批次盘点
			Map<String, Object> map_pd01 = new HashMap<String, Object>();// 盘点主表
			map_pd01.put("JGID", jgid);
			map_pd01.put("YFSB", yfsb);
			map_pd01.put("CKBH", 0);
			map_pd01.put("PDRQ", new Date());
			map_pd01.put("CZGH", userId);
			map_pd01.put("PDWC", 0);
			map_pd01.put("HZWC", 0);
			map_pd01 = dao.doSave("create", BSPHISEntryNames.YF_YK01, map_pd01,
					false);
			int pddh = MedicineUtils.parseInt(map_pd01.get("PDDH"));
			// StringBuffer hql=new StringBuffer();
			// hql.append("select a.YPXH as YPXH,a.YPCD as YPCD,a.YPSL as YPSL,b.YFGG as YFGG,b.")
			List<Map<String, Object>> list_kcmx;
			if (kcpd_pc == 1) {
				list_kcmx = dao.doList(
						CNDHelper.toListCnd("['and',['eq',['$','a.YFSB'],['l',"
								+ yfsb + "]]]"),
						null, BSPHISEntryNames.YF_KCMX);
			} else {
				StringBuffer hql_kcmx = new StringBuffer();
				hql_kcmx.append("select 0 as SBXH,a.YPXH as YPXH,a.YPCD as YPCD,sum(a.YPSL) as YPSL,b.YFGG as YFGG,b.YFBZ as YFBZ,b.YFDW as YFDW, max(a.LSJG) as LSJG,max(a.PFJG) as PFJG,max(a.JHJG) as JHJG,sum(a.LSJE) as LSJE,sum(a.PFJE) as PFJE,sum(a.JHJE) as JHJE from YF_KCMX a,YF_YPXX b where a.YPXH=b.YPXH and a.YFSB=b.YFSB and a.YFSB=:yfsb  group by a.YPXH,a.YPCD,b.YFGG,b.YFBZ,b.YFDW");
				map_par.remove("jgid");
				list_kcmx = dao.doSqlQuery(hql_kcmx.toString(), map_par);
			}
			for (Map<String, Object> map_kcmx : list_kcmx) {
				Map<String, Object> map_pd02 = new HashMap<String, Object>();
				map_pd02.put("JGID", jgid);
				map_pd02.put("YFSB", yfsb);
				map_pd02.put("CKBH", 0);
				map_pd02.put("PDDH", pddh);
				map_pd02.put("YPXH", map_kcmx.get("YPXH"));
				map_pd02.put("YPCD", map_kcmx.get("YPCD"));
				map_pd02.put("PQSL", map_kcmx.get("YPSL"));
				map_pd02.put("SPSL", 0);
				map_pd02.put("YPGG", map_kcmx.get("YFGG"));
				map_pd02.put("YFBZ", map_kcmx.get("YFBZ"));
				map_pd02.put("YFDW", map_kcmx.get("YFDW"));
				map_pd02.put("LSJG", map_kcmx.get("LSJG"));
				map_pd02.put("PFJG", map_kcmx.get("PFJG"));
				map_pd02.put("JHJG", map_kcmx.get("JHJG"));
				map_pd02.put("KCSB", map_kcmx.get("SBXH"));
				map_pd02.put("YLSE", map_kcmx.get("LSJE"));
				map_pd02.put("YPFE", map_kcmx.get("PFJE"));
				map_pd02.put("YJHE", map_kcmx.get("JHJE"));
				map_pd02.put("XLSE", 0);
				map_pd02.put("XPFE", 0);
				map_pd02.put("XJHE", 0);
				if (kcpd_pc == 1) {
					map_pd02.put("YPPH", map_kcmx.get("YPPH"));
					map_pd02.put("YPXQ", map_kcmx.get("YPXQ"));
				}
				dao.doSave("create", BSPHISEntryNames.YF_YK02, map_pd02, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点单开始失败!", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点单开始失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "盘点单开始失败!", e);
		}
		return MedicineUtils.getRetMap();

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-25
	 * @description 查询药房盘点参数
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int queryKCPD_PC(Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String kcpd_pc = ParameterUtil.getParameter(jgid, "YF_PCPD" + yfsb,
				BSPHISSystemArgument.defaultValue.get("YF_PCPD"),
				BSPHISSystemArgument.defaultAlias.get("YF_PCPD"),BSPHISSystemArgument.defaultCategory.get("YF_PCPD"), ctx);
		if ("0".equals(kcpd_pc)) {
			return 0;
		}
		return 1;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 盘点汇总
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePharmacyInventoryProcessingHz(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int ckbh = MedicineUtils.parseInt(body.get("CKBH"));
		int tag = MedicineUtils.parseInt(body.get("TAG"));
		StringBuffer hql = new StringBuffer();
		hql.append("select HZWC as HZWC,PDWC as PDWC from YF_YK01 where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("pddh", pddh);
		map_par.put("ckbh", ckbh);
		try {
			Map<String, Object> map_hzwc = dao.doLoad(hql.toString(), map_par);
			if (MedicineUtils.parseInt(map_hzwc.get("PDWC")) == 1) {
				return MedicineUtils.getRetMap("该盘点单已完成!");
			}
			if (tag == 0) {
				// 取消汇总
				if (map_hzwc != null
						&& MedicineUtils.parseInt(map_hzwc.get("HZWC")) == 0) {
					return MedicineUtils.getRetMap("该盘点单未汇总,无需取消汇总!");
				}
				StringBuffer hql_update = new StringBuffer();
				hql_update
						.append("update YF_YK01 set HZWC=0 where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh");
				dao.doUpdate(hql_update.toString(), map_par);
				hql_update = new StringBuffer();
				hql_update
						.append("update YF_YK02  set LRBZ=0,SPSL=0 where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh");
				dao.doUpdate(hql_update.toString(), map_par);
			} else {
				// 汇总
				if (map_hzwc != null
						&& MedicineUtils.parseInt(map_hzwc.get("HZWC")) == 1) {
					return MedicineUtils.getRetMap("该盘点单已经是汇总完成状态,无需再汇总!");
				}
				hql = new StringBuffer();
				hql.append(" PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and LRWC=0");
				long l = dao.doCount("YF_YK02_GRLR", hql.toString(), map_par);
				if (l > 0) {
					return MedicineUtils.getRetMap("有人未完成,无法汇总!");
				}
				StringBuffer hql_update = new StringBuffer();
				hql_update
						.append("update YF_YK01  set HZWC=1 where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh");
				dao.doUpdate(hql_update.toString(), map_par);
				StringBuffer cnd = new StringBuffer();
				cnd.append("['and',['eq',['$','YFSB'],['l',").append(yfsb)
						.append("]],['eq',['$','PDDH'],['i',").append(pddh)
						.append("]],['eq',['$','CKBH'],['i',").append(ckbh)
						.append("]]]");
				List<Map<String, Object>> list_pd02 = dao.doList(
						CNDHelper.toListCnd(cnd.toString()), null,
						BSPHISEntryNames.YF_YK02);
				StringBuffer hql_grrl = new StringBuffer();
				hql_grrl.append("select SBXH as SBXH,YFBZ as YFBZ,YKBZ as YKBZ,YKSL as YKSL,SPSL as SPSL,PQSL as PQSL,KCSB as KCSB,YPXH as YPXH,YPCD as YPCD,XLSE as XLSE,XJHE as XJHE,LSJG as LSJG,JHJG as JHJG,YPPH as YPPH,YPXQ as YPXQ from YF_YK02_GRLR where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and LRBZ=1");
				List<Map<String, Object>> list_pd02_grrl = dao.doQuery(
						hql_grrl.toString(), map_par);
				StringBuffer hql_ypmc = new StringBuffer();
				hql_ypmc.append("select YPMC as YPMC from YK_TYPK where YPXH=:ypxh");
				for (Map<String, Object> map_pd02 : list_pd02) {
					if(MedicineUtils.parseDouble(map_pd02.get("PQSL"))==0){//新增的数据特殊处理.
						continue;
					}
					if (MedicineUtils.parseLong(map_pd02.get("KCSB")) != 0) {// KCSB不是0则就是按照批次盘点的
						for (Map<String, Object> map_pd02_grrl : list_pd02_grrl) {
							if (MedicineUtils.parseLong(map_pd02.get("KCSB")) == MedicineUtils
									.parseLong(map_pd02_grrl.get("KCSB"))) {
								double spsl = MedicineUtils
										.parseDouble(map_pd02_grrl.get("SPSL"))
										+ MedicineUtils
												.formatDouble(
														2,
														MedicineUtils
																.parseDouble(map_pd02_grrl
																		.get("YKSL"))
																* MedicineUtils
																		.parseInt(MedicineUtils
																				.parseInt(map_pd02_grrl
																						.get("YKBZ")))
																* MedicineUtils
																		.parseInt(map_pd02_grrl
																				.get("YFBZ")));
								map_pd02.put(
										"SPSL",
										MedicineUtils.parseDouble(map_pd02
												.get("SPSL")) + spsl);
//								map_pd02.put(
//										"XLSE",
//										MedicineUtils.parseDouble(map_pd02
//												.get("XLSE"))
//												+ MedicineUtils
//														.parseDouble(map_pd02_grrl
//																.get("XLSE")));
//								map_pd02.put(
//										"XJHE",
//										MedicineUtils.parseDouble(map_pd02
//												.get("XJHE"))
//												+ MedicineUtils
//														.parseDouble(map_pd02_grrl
//																.get("XJHE")));
								if (MedicineUtils.parseDouble(map_pd02
										.get("SPSL")) + spsl > 999999.99) {
									Map<String, Object> map_par_ypmc = new HashMap<String, Object>();
									map_par_ypmc.put("ypxh", MedicineUtils
											.parseLong(map_pd02.get("YPXH")));
									Map<String, Object> map_ypmc = dao.doLoad(
											hql_ypmc.toString(), map_par_ypmc);
									String ypmc = "";
									if (map_ypmc != null
											&& map_ypmc.size() != 0) {
										ypmc = map_ypmc.get("YPMC") + "";
									}
									return MedicineUtils.getRetMap("药品[" + ypmc
											+ "]数量长度超过最长长度限制,不能完成汇总!");
								}
								map_pd02.put("LRBZ", 1);
							}
						}
					} else {// 不按批次盘点
						for (Map<String, Object> map_pd02_grrl : list_pd02_grrl) {
							if (MedicineUtils.parseLong(map_pd02.get("YPXH")) == MedicineUtils
									.parseLong(map_pd02_grrl.get("YPXH"))
									&& MedicineUtils.parseLong(map_pd02
											.get("YPCD")) == MedicineUtils
											.parseLong(map_pd02_grrl
													.get("YPCD"))&&MedicineUtils.parseDouble(map_pd02_grrl.get("PQSL"))!=0) {
								double spsl = MedicineUtils
										.parseDouble(map_pd02_grrl.get("SPSL"))
										+ MedicineUtils
												.formatDouble(
														2,
														MedicineUtils
																.parseDouble(map_pd02_grrl
																		.get("YKSL"))
																* MedicineUtils
																		.parseInt(MedicineUtils
																				.parseInt(map_pd02_grrl
																						.get("YKBZ")))
																* MedicineUtils
																		.parseInt(map_pd02_grrl
																				.get("YFBZ")));
								map_pd02.put(
										"SPSL",
										MedicineUtils.parseDouble(map_pd02
												.get("SPSL")) + spsl);
//								map_pd02.put(
//										"XLSE",
//										MedicineUtils.parseDouble(map_pd02
//												.get("XLSE"))
//												+ MedicineUtils
//														.parseDouble(map_pd02_grrl
//																.get("XLSE")));
//								map_pd02.put(
//										"XJHE",
//										MedicineUtils.parseDouble(map_pd02
//												.get("XJHE"))
//												+ MedicineUtils
//														.parseDouble(map_pd02_grrl
//																.get("XJHE")));
								if (MedicineUtils.parseDouble(map_pd02
										.get("SPSL")) + spsl > 999999.99) {
									Map<String, Object> map_par_ypmc = new HashMap<String, Object>();
									map_par_ypmc.put("ypxh", MedicineUtils
											.parseLong(map_pd02.get("YPXH")));
									Map<String, Object> map_ypmc = dao.doLoad(
											hql_ypmc.toString(), map_par_ypmc);
									String ypmc = "";
									if (map_ypmc != null
											&& map_ypmc.size() != 0) {
										ypmc = map_ypmc.get("YPMC") + "";
									}
									return MedicineUtils.getRetMap("药品[" + ypmc
											+ "]数量长度超过最长长度限制,不能完成汇总!");
								}
								map_pd02.put("LRBZ", 1);
							}
						}
					}
				}
				for(Map<String, Object> map_pd02_grrl : list_pd02_grrl){//特殊处理新增数据
					if(MedicineUtils.parseDouble(map_pd02_grrl.get("PQSL"))!=0){
						continue;
					}
					boolean h=false;
					for(Map<String,Object> map_pd02:list_pd02){
						if(MedicineUtils.compareMaps(map_pd02_grrl, new String[]{"YPXH","YPCD","LSJG","JHJG","YPPH","YPXQ"}, map_pd02, new String[]{"YPXH","YPCD","LSJG","JHJG","YPPH","YPXQ"})){
							h=true;
							double spsl = MedicineUtils
									.parseDouble(map_pd02_grrl.get("SPSL"))
									+ MedicineUtils
											.formatDouble(
													2,
													MedicineUtils
															.parseDouble(map_pd02_grrl
																	.get("YKSL"))
															* MedicineUtils
																	.parseInt(MedicineUtils
																			.parseInt(map_pd02_grrl
																					.get("YKBZ")))
															* MedicineUtils
																	.parseInt(map_pd02_grrl
																			.get("YFBZ")));
							map_pd02.put(
									"SPSL",
									MedicineUtils.parseDouble(map_pd02
											.get("SPSL")) + spsl);
//							map_pd02.put(
//									"XLSE",
//									MedicineUtils.parseDouble(map_pd02
//											.get("XLSE"))
//											+ MedicineUtils
//													.parseDouble(map_pd02_grrl
//															.get("XLSE")));
//							map_pd02.put(
//									"XJHE",
//									MedicineUtils.parseDouble(map_pd02
//											.get("XJHE"))
//											+ MedicineUtils
//													.parseDouble(map_pd02_grrl
//															.get("XJHE")));
							map_pd02.put("LRBZ", 1);
							if (MedicineUtils.parseDouble(map_pd02
									.get("SPSL")) + spsl > 999999.99) {
								Map<String, Object> map_par_ypmc = new HashMap<String, Object>();
								map_par_ypmc.put("ypxh", MedicineUtils
										.parseLong(map_pd02.get("YPXH")));
								Map<String, Object> map_ypmc = dao.doLoad(
										hql_ypmc.toString(), map_par_ypmc);
								String ypmc = "";
								if (map_ypmc != null
										&& map_ypmc.size() != 0) {
									ypmc = map_ypmc.get("YPMC") + "";
								}
								return MedicineUtils.getRetMap("药品[" + ypmc
										+ "]数量长度超过最长长度限制,不能完成汇总!");
							}
						}
					}
					if(!h){
						double spsl = MedicineUtils
								.parseDouble(map_pd02_grrl.get("SPSL"))
								+ MedicineUtils
										.formatDouble(
												2,
												MedicineUtils
														.parseDouble(map_pd02_grrl
																.get("YKSL"))
														* MedicineUtils
																.parseInt(MedicineUtils
																		.parseInt(map_pd02_grrl
																				.get("YKBZ")))
														* MedicineUtils
																.parseInt(map_pd02_grrl
																		.get("YFBZ")));
					Map<String,Object> map_pdmx=new HashMap<String,Object>();
					map_pdmx.putAll(dao.doLoad(BSPHISEntryNames.YF_YK02_GRLR, MedicineUtils.parseLong(map_pd02_grrl.get("SBXH"))));
					map_pdmx.remove("SBXH");
					map_pdmx.put("SPSL", spsl);
					map_pdmx.put("LRBZ", 1);
					Map<String,Object> map_ret_key=dao.doSave("create", BSPHISEntryNames.YF_YK02, map_pdmx, false);
					map_pdmx.put("SBXH", map_ret_key.get("SBXH"));
					list_pd02.add(map_pdmx);
					}
				}
				for (Map<String, Object> map_pd02 : list_pd02) {
					dao.doSave("update", BSPHISEntryNames.YF_YK02, map_pd02,
							false);
				}
				//add by caijy for xlse和xjhe数据不对的问题
				StringBuffer hql_update_je=new StringBuffer();
				hql_update_je.append("update YF_YK02 set XLSE=SPSL*LSJG,XJHE=JHJG*SPSL where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh");
				dao.doUpdate(hql_update_je.toString(), map_par);
				hql_update_je=new StringBuffer();
				hql_update_je.append("update YF_YK02 set XLSE=YLSE,XJHE=YJHE where PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and SPSL=PQSL");
				dao.doUpdate(hql_update_je.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			if (tag == 0) {
				MedicineUtils.throwsException(logger, "盘点处理取消汇总失败", e);
			} else {
				MedicineUtils.throwsException(logger, "盘点处理汇总失败", e);
			}
		} catch (ExpException e) {
			if (tag == 0) {
				MedicineUtils.throwsException(logger, "盘点处理取消汇总失败", e);
			} else {
				MedicineUtils.throwsException(logger, "盘点处理汇总失败", e);
			}
		} catch (ValidateException e) {
			if (tag == 0) {
				MedicineUtils.throwsException(logger, "盘点处理取消汇总失败", e);
			} else {
				MedicineUtils.throwsException(logger, "盘点处理汇总失败", e);
			}
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 药房盘点前判断下是否有变动
	 * @author caijy
	 * @createDate 2017-5-22
	 * @description 
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> checkPharmacyInventoryProcessingWc(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int ckbh = MedicineUtils.parseInt(body.get("CKBH"));
		StringBuffer cnd = new StringBuffer();
		cnd.append("['and',['eq',['$','YFSB'],['l',").append(yfsb)
				.append("]],['eq',['$','PDDH'],['i',").append(pddh)
				.append("]],['eq',['$','CKBH'],['i',").append(ckbh)
				.append("]]]");
		try {
			StringBuffer hql_count = new StringBuffer();
			hql_count
					.append(" PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and PDWC=1");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("pddh", pddh);
			map_par.put("yfsb", yfsb);
			map_par.put("ckbh", ckbh);
			long l = dao.doCount("YF_YK01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已盘点完成,不能重复操作!请刷新窗口");
			}
			List<?> cnds = CNDHelper.toListCnd(cnd.toString());
			List<Map<String, Object>> list_pd02 = dao.doList(cnds, null,
					BSPHISEntryNames.YF_YK02);
			Map<String,Object> map_par_yfsb=new HashMap<String,Object>();
			map_par_yfsb.put("yfsb", yfsb);
			//按库存识别查询库存并缓存
			List<Map<String,Object>> list_kc=dao.doQuery("select YPSL as YPSL,SBXH as KCSB from YF_KCMX where YFSB=:yfsb",map_par_yfsb );
			//按药品序号和产地查询库存并缓存
			List<Map<String,Object>> list_kc_sum=dao.doSqlQuery("select sum(YPSL) as YPSL,YPXH as YPXH,YPCD as YPCD from YF_KCMX where YFSB=:yfsb group by YPXH, YPCD",map_par_yfsb );
			//查询药品信息
			//List<Map<String,Object>> list_ypmc=dao.doQuery("select YPXH as YPXH,YPMC as YPMC from YK_TYPK ", null);
			List<String> l_yp=new ArrayList<String>();//用于存库存变动药品,只存3个
			for (Map<String, Object> map_pd02 : list_pd02) {
				if(MedicineUtils.parseDouble(map_pd02.get("PQSL"))==0){
					continue;
				}
				if (MedicineUtils.parseInt(map_pd02.get("LRBZ")) == 0) {
					return MedicineUtils.getRetMap("存在未盘点药品,请盘点完成后再进行'完成'处理!");
				}
				if(l_yp.size()>2){
					break;
				}
				Map<String,Object> map_kc=null;//用于存放对应的库存记录
				if(MedicineUtils.parseLong(map_pd02.get("KCSB")) != 0){
					map_kc=MedicineUtils.getRecord(list_kc, MedicineUtils.parseLong(map_pd02.get("KCSB")), "KCSB");
				}else{
					map_kc=MedicineUtils.getRecord(list_kc_sum, new String[]{"YPXH","YPCD"}, map_pd02, new String[]{"YPXH","YPCD"});	
				}
				if(map_kc==null||MedicineUtils.parseDouble(map_pd02.get("PQSL"))!=MedicineUtils.parseDouble(map_kc.get("YPSL"))){
					Map<String,Object> map_par_ypxh=new HashMap<String,Object>();
					map_par_ypxh.put("ypxh", MedicineUtils.parseLong(map_pd02.get("YPXH")));
					l_yp.add(dao.doLoad("select YPMC as YPMC from YK_TYPK where YPXH=:ypxh", map_par_ypxh).get("YPMC")+"");
					//l_yp.add(MedicineUtils.getRecord(list_ypmc, MedicineUtils.parseLong(map_pd02.get("YPXH")), "YPXH").get("YPMC")+"");	
					continue;
				}
			}
			if(l_yp.size()>0){
				StringBuffer s=new StringBuffer();
				for(String ypmc:l_yp){
					s.append(ypmc).append(",");
				}
				s.append("库存存在变动,请选择重新生成盘点单或者点击自动校准");
				return MedicineUtils.getRetMap(s.toString());
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点处理完成前校验数据失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点处理完成前校验数据失败", e);
		} 
		return MedicineUtils.getRetMap();
	}
	/**
	 * 盘点输入自动校准-修改实盘数量,盘前数量=当前库存数量
	 * @author caijy
	 * @createDate 2017-5-23
	 * @description 
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePdZdjz(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int ckbh = MedicineUtils.parseInt(body.get("CKBH"));
		StringBuffer cnd = new StringBuffer();
		cnd.append("['and',['eq',['$','YFSB'],['l',").append(yfsb)
				.append("]],['eq',['$','PDDH'],['i',").append(pddh)
				.append("]],['eq',['$','CKBH'],['i',").append(ckbh)
				.append("]]]");
		try {
			StringBuffer hql_count = new StringBuffer();
			hql_count
					.append(" PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and PDWC=1");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("pddh", pddh);
			map_par.put("yfsb", yfsb);
			map_par.put("ckbh", ckbh);
			long l = dao.doCount("YF_YK01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已盘点完成,不能重复操作!请刷新窗口");
			}
			List<?> cnds = CNDHelper.toListCnd(cnd.toString());
			List<Map<String, Object>> list_pd02 = dao.doList(cnds, null,
					BSPHISEntryNames.YF_YK02);
			Map<String,Object> map_par_yfsb=new HashMap<String,Object>();
			map_par_yfsb.put("yfsb", yfsb);
			//按库存识别查询库存并缓存
			List<Map<String,Object>> list_kc=dao.doQuery("select YPSL as YPSL,SBXH as KCSB,LSJE as LSJE,JHJE as JHJE from YF_KCMX where YFSB=:yfsb",map_par_yfsb );
			//按药品序号和产地查询库存并缓存
			List<Map<String,Object>> list_kc_sum=dao.doSqlQuery("select sum(YPSL) as YPSL,sum(LSJE) as LSJE,sum(JHJE) as JHJE,YPXH as YPXH,YPCD as YPCD from YF_KCMX where YFSB=:yfsb group by YPXH, YPCD",map_par_yfsb );
			for (Map<String, Object> map_pd02 : list_pd02) {
				if (MedicineUtils.parseInt(map_pd02.get("LRBZ")) == 0) {
					return MedicineUtils.getRetMap("存在未盘点药品,请盘点完成后再进行'完成'处理!");
				}
				Map<String,Object> map_kc=null;//用于存放对应的库存记录
				if(MedicineUtils.parseLong(map_pd02.get("KCSB")) != 0){
					map_kc=MedicineUtils.getRecord(list_kc, MedicineUtils.parseLong(map_pd02.get("KCSB")), "KCSB");
				}else{
					map_kc=MedicineUtils.getRecord(list_kc_sum, new String[]{"YPXH","YPCD"}, map_pd02, new String[]{"YPXH","YPCD"});	
				}
				double kcsl=0;
				double lsje=0;
				double jhje=0;
				if(map_kc!=null){
					kcsl=MedicineUtils.parseDouble(map_kc.get("YPSL"));
					lsje=MedicineUtils.parseDouble(map_kc.get("LSJE"));
					jhje=MedicineUtils.parseDouble(map_kc.get("JHJE"));
				}
				if(MedicineUtils.parseDouble(map_pd02.get("PQSL"))!=kcsl){
					map_pd02.put("PQSL", kcsl);
					map_pd02.put("SPSL", kcsl);
					map_pd02.put("YLSE", lsje);
					map_pd02.put("XLSE", lsje);
					map_pd02.put("YJHE", jhje);
					map_pd02.put("XJHE", jhje);
					try {
						dao.doSave("update", BSPHISEntryNames.YF_YK02, map_pd02, false);
					} catch (ValidateException e) {
						MedicineUtils.throwsException(logger, "更新数据失败:"+e.getMessage(), e);
					}
				}
			}
			
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点数据自动校准失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点数据自动校准失败", e);
		} 
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 盘点处理完成
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePharmacyInventoryProcessingWc(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 获取JGID
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int ckbh = MedicineUtils.parseInt(body.get("CKBH"));
		StringBuffer cnd = new StringBuffer();
		cnd.append("['and',['eq',['$','YFSB'],['l',").append(yfsb)
				.append("]],['eq',['$','PDDH'],['i',").append(pddh)
				.append("]],['eq',['$','CKBH'],['i',").append(ckbh)
				.append("]]]");
		try {
			StringBuffer hql_count = new StringBuffer();
			hql_count
					.append(" PDDH=:pddh and YFSB=:yfsb and CKBH=:ckbh and PDWC=1");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("pddh", pddh);
			map_par.put("yfsb", yfsb);
			map_par.put("ckbh", ckbh);
			long l = dao.doCount("YF_YK01", hql_count.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("已盘点完成,不能重复操作!请刷新窗口");
			}
			List<?> cnds = CNDHelper.toListCnd(cnd.toString());
			Map<String, Object> map_pd01 = dao.doLoad(cnds,
					BSPHISEntryNames.YF_YK01);
			List<Map<String, Object>> list_pd02 = dao.doList(cnds, null,
					BSPHISEntryNames.YF_YK02);
			for (Map<String, Object> map_pd02 : list_pd02) {
				if (MedicineUtils.parseInt(map_pd02.get("LRBZ")) == 0) {
					return MedicineUtils.getRetMap("存在未盘点药品,请盘点完成后再进行'完成'处理!");
				}
			}
			hql_count = new StringBuffer();
			hql_count.append("ZXRQ>:pdrq and ZYPB=1 and JGID=:jgid");
			map_par.clear();
			map_par.put("pdrq", map_pd01.get("PDRQ"));
			map_par.put("jgid", jgid);
			l = dao.doCount("YK_TJ01", hql_count.toString(), map_par);
			if (l > 0) {
				StringBuffer hql_tj = new StringBuffer();
				hql_tj.append(
						"select b.KCSB as KCSB,b.XLSJ as LSJG,b.XJHJ as JHJG,b.YPXH as YPXH ,b.YPCD as YPCD from ")
						.append("YK_TJ01")
						.append(" a,")
						.append("YF_TJJL")
						.append(" b where a.TJDH=b.TJDH and a.TJFS=b.TJFS and a.JGID=b.JGID and b.TJSL>0 and a.ZYPB = 1 and b.YFSB=:yfsb and a.ZXRQ>:pdrq and a.JGID=:jgid");
				map_par.put("yfsb", yfsb);
				List<Map<String, Object>> list_tjjl = dao.doQuery(
						hql_tj.toString(), map_par);
				for (Map<String, Object> map_tjjl : list_tjjl) {
					for (Map<String, Object> map_pd02 : list_pd02) {
						boolean sure = false;
						if (MedicineUtils.parseLong(map_pd02.get("KCSB")) != 0) {
							if (MedicineUtils.parseLong(map_tjjl.get("KCSB")) == MedicineUtils
									.parseLong(map_pd02.get("KCSB"))) {
								sure = true;
							}
						} else {
							if (MedicineUtils.parseLong(map_tjjl.get("YPXH")) == MedicineUtils
									.parseLong(map_pd02.get("YPXH"))
									&& MedicineUtils.parseLong(map_tjjl
											.get("YPCD")) == MedicineUtils
											.parseLong(map_pd02.get("YPCD"))) {
								sure = true;
							}
						}
						if (sure) {
							map_pd02.put(
									"YLSE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_pd02
													.get("YLSE"))
													+ (MedicineUtils
															.parseDouble(map_tjjl
																	.get("LSJG")) - MedicineUtils
															.parseDouble(map_pd02
																	.get("LSJG")))
													* MedicineUtils
															.parseDouble(map_pd02
																	.get("SPSL"))));
							map_pd02.put(
									"YPFE",
									MedicineUtils.formatDouble(
											2,
											MedicineUtils.parseDouble(map_pd02
													.get("YPFE"))
													+ (MedicineUtils
															.parseDouble(map_tjjl
																	.get("PFJG")) - MedicineUtils
															.parseDouble(map_pd02
																	.get("PFJG")))
													* MedicineUtils
															.parseDouble(map_pd02
																	.get("SPSL"))));
							map_pd02.put("LSJG", MedicineUtils
									.parseDouble(map_tjjl.get("LSJG")));
							map_pd02.put("JHJG", MedicineUtils
									.parseDouble(map_tjjl.get("JHJG")));
						}
					}
				}
			}
			StringBuffer hql_kc = new StringBuffer();// 按批次盘点,有库存识别
			hql_kc.append("select a.SBXH as SBXH,a.YPSL as YPSL,LSJG as LSJG,JHJG as JHJG from YF_KCMX  a where a.SBXH=:kcsb");
			StringBuffer hql_kc_pc_sl = new StringBuffer();// 不按批次盘点,没有库存识别,查询库存数量
			hql_kc_pc_sl
					.append("select sum(YPSL) as YPSL from YF_KCMX a where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb");
			StringBuffer hql_kc_update = new StringBuffer();// 增加库存(有库存识别)
			hql_kc_update
					.append("update YF_KCMX set YPSL=(:ypsl+YPSL),LSJE=round((:ypsl+YPSL)*LSJG,2),JHJE=round((:ypsl+YPSL)*JHJG,2) where SBXH=:kcsb");
			StringBuffer hql_kc_update_pc = new StringBuffer();// 增加库存(无库存识别)
			hql_kc_update_pc
					.append("update YF_KCMX  set YPSL=(:ypsl+YPSL),LSJE=round((:ypsl+YPSL)*LSJG,2),JHJE=round((:ypsl+YPSL)*JHJG,2) where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd  and rownum<2");
			StringBuffer hql_kc_update_pc_wkc = new StringBuffer();// 增加库存(无库存识别,无库存)
			hql_kc_update_pc_wkc
					.append("update YF_KCMX  set YPSL=(:ypsl+YPSL),LSJE=round((:ypsl+YPSL)*LSJG,2),JHJE=round((:ypsl+YPSL)*JHJG,2) where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and JHJG=:jhjg and rownum<2");
			StringBuffer hql_kc_insert = new StringBuffer();// 按批次盘点,有库存识别
			hql_kc_insert
					.append("insert into YF_KCMX select * from YF_KCMX_LS  where SBXH=:kcsb");
			StringBuffer hql_kc_insert_pc = new StringBuffer();// 按批次盘点,无库存识别
			hql_kc_insert_pc
					.append("insert into YF_KCMX select * from YF_KCMX_LS where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and JHJG=:jhjg and rownum<2");
			StringBuffer hql_ypmc = new StringBuffer();// 查询药品名称
			hql_ypmc.append("select YPMC as YPMC from YK_TYPK where YPXH=:ypxh");
			List<Map<String, Object>> list_jkc = new ArrayList<Map<String, Object>>();// 需要减掉的库存(无库存识别)
			for (Map<String, Object> map_pd02 : list_pd02) {
				double yksl = MedicineUtils.parseDouble(map_pd02.get("SPSL"))
						- MedicineUtils.parseDouble(map_pd02.get("PQSL"));
				map_pd02.put("XLSE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(map_pd02.get("SPSL"))
										* MedicineUtils.parseDouble(map_pd02
												.get("LSJG")))));
				map_pd02.put("XJHE", MedicineUtils.formatDouble(
						2,
						MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(map_pd02.get("SPSL"))
										* MedicineUtils.parseDouble(map_pd02
												.get("JHJG")))));
				if (yksl == 0) {
					continue;
				}
				if (MedicineUtils.parseLong(map_pd02.get("KCSB")) != 0) {
					Map<String, Object> map_par_kc = new HashMap<String, Object>();
					map_par_kc.put("kcsb",
							MedicineUtils.parseLong(map_pd02.get("KCSB")));
					Map<String, Object> map_kc = dao.doLoad(hql_kc.toString(),
							map_par_kc);
					if (yksl < 0) {
						if (map_kc == null
								|| MedicineUtils
										.parseDouble(map_kc.get("YPSL")) < -yksl) {
							throw new ModelDataOperationException(
									ServiceCode.CODE_DATABASE_ERROR,
									"由于在盘点过程中的库存变动导致库存不足无法出库请确认!");
						}

					} else {
						if (map_kc == null) {
							dao.doSqlUpdate(hql_kc_insert.toString(),
									map_par_kc);
						}
					}
					map_par_kc.put("ypsl", yksl);
					dao.doUpdate(hql_kc_update.toString(), map_par_kc);
				} else {
					if(MedicineUtils.parseDouble(map_pd02.get("PQSL"))!=0){//处理非新增的不按批次盘点数据
						Map<String, Object> map_par_kc = new HashMap<String, Object>();
						map_par_kc.put("ypxh",
								MedicineUtils.parseLong(map_pd02.get("YPXH")));
						map_par_kc.put("ypcd",
								MedicineUtils.parseLong(map_pd02.get("YPCD")));
						map_par_kc.put("yfsb",
								MedicineUtils.parseLong(map_pd02.get("YFSB")));
						Map<String, Object> map_kc_sl = dao.doLoad(
								hql_kc_pc_sl.toString(), map_par_kc);
						if (yksl < 0) {
							if (map_kc_sl == null
									|| map_kc_sl.size() == 0
									|| MedicineUtils.parseDouble(map_kc_sl
											.get("YPSL")) == 0
									|| MedicineUtils.parseDouble(map_kc_sl
											.get("YPSL")) < -yksl) {
								Map<String, Object> map_par_ypmc = new HashMap<String, Object>();
								map_par_ypmc.put("ypxh", MedicineUtils
										.parseLong(map_pd02.get("YPXH")));
								Map<String, Object> map_ypmc = dao.doLoad(
										hql_ypmc.toString(), map_par_ypmc);
								throw new ModelDataOperationException(
										ServiceCode.CODE_DATABASE_ERROR, "药品"
												+ map_ypmc.get("YPMC")
												+ "由于在盘点过程中的库存变动导致库存不足无法出库请确认!");
							}
							Map<String, Object> map_jkc = new HashMap<String, Object>();
							map_jkc.put("YPXH",
									MedicineUtils.parseLong(map_pd02.get("YPXH")));
							map_jkc.put("YPCD",
									MedicineUtils.parseLong(map_pd02.get("YPCD")));
							map_jkc.put("YFSB",
									MedicineUtils.parseLong(map_pd02.get("YFSB")));
							map_jkc.put("YPDJ",
									MedicineUtils.parseDouble(map_pd02.get("LSJG")));
							map_jkc.put("YPSL", -yksl);
							list_jkc.add(map_jkc);
						} else {
							if (map_kc_sl == null
									|| map_kc_sl.size() == 0
									|| MedicineUtils.parseDouble(map_kc_sl
											.get("YPSL")) == 0) {
								map_par_kc.put("lsjg", MedicineUtils
										.parseDouble(map_pd02.get("LSJG")));
								map_par_kc.put("jhjg", MedicineUtils
										.parseDouble(map_pd02.get("JHJG")));
								dao.doSqlUpdate(hql_kc_insert_pc.toString(),
										map_par_kc);
								map_par_kc.put("ypsl", yksl);
								dao.doUpdate(hql_kc_update_pc_wkc.toString(),
										map_par_kc);
							}
							map_par_kc.put("ypsl", yksl);
							map_par_kc.remove("lsjg");
							map_par_kc.remove("jhjg");
							dao.doUpdate(hql_kc_update_pc.toString(), map_par_kc);
						}
					}else{//新增的数据,增加库存
						Map<String,Object> map_kc=new HashMap<String,Object>();
						map_kc.putAll(map_pd02);
						map_kc.remove("SBXH");
						map_kc.put("YPSL", MedicineUtils.parseDouble(map_pd02.get("SPSL")));
						map_kc.put("JHJE", MedicineUtils.parseDouble(map_pd02.get("XJHE")));
						map_kc.put("PFJE", MedicineUtils.parseDouble(map_pd02.get("XPFE")));
						map_kc.put("LSJE", MedicineUtils.parseDouble(map_pd02.get("XLSE")));
						map_kc.put("JYBZ", 0);
						map_kc.put("YKLJ", new Double(0));
						map_kc.put("YKJJ", new Double(0));
						map_kc.put("YKPJ", new Double(0));
						dao.doSave("create", BSPHISEntryNames.YF_KCMX_CSH, map_kc,
								false);
					}
					
				}
				dao.doSave("update", BSPHISEntryNames.YF_YK02, map_pd02, false);
			}
			if (list_jkc.size() > 0) {
//				PharmacyInventoryManageModel model = new PharmacyInventoryManageModel(
//						dao);
				List<Map<String, Object>> list_inventory =queryAndLessInventory(list_jkc, ctx);
				if (list_inventory == null
						|| (list_inventory.size() == 1 && !list_inventory
								.get(0).containsKey("YPSL"))) {// 如果错误,或者库存不足
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "盘点处理完成保存数据失败");
				}
			}
			// 完后删除库存是0的记录
			Map<String, Object> map_kcsl_par = new HashMap<String, Object>();
			map_kcsl_par.put("yfsb", yfsb);
			StringBuffer sql_kc_ls_insert = new StringBuffer();
			sql_kc_ls_insert
					.append("insert into YF_KCMX_LS select * from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_kcsl_par);
			StringBuffer hql_kc_delete = new StringBuffer();
			hql_kc_delete
					.append("delete from YF_KCMX where YPSL=0 and YFSB=:yfsb");
			dao.doUpdate(hql_kc_delete.toString(), map_kcsl_par);
			map_pd01.put("PDWC", 1);
			map_pd01.put("WCRQ", new Date());
			dao.doSave("update", BSPHISEntryNames.YF_YK01, map_pd01, false);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点处理完成保存数据失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点处理完成保存数据失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "盘点处理完成保存数据失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-10-12
	 * @description 减库存(无kcsb)
	 * @updateInfo 查询库存的时候 去掉库存禁用条件,禁用的库存也能查询出来进行减库存
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryAndLessInventory(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		if (body == null || body.size() == 0) {
			return null;
		}
		List<Map<String, Object>> list_kc_update_ret = new ArrayList<Map<String, Object>>();// 需要更新的库存集合(返回用)
		Session session = (Session) ctx.get(Context.DB_SESSION);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		try {
			//库存冻结代码
			int SFQYYFYFY=0;
			double KCDJTS= MedicineUtils.parseDouble(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.KCDJTS, ctx));
			try{
			SFQYYFYFY= MedicineUtils.parseInt(ParameterUtil
					.getParameter(jgid, BSPHISSystemArgument.SFQYYFYFY, ctx));
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.SFQYYFYFY, e);
			}
//			if(SFQYYFYFY==1){//如果启用库存冻结
//				//先删除过期的冻结库存
//				MedicineCommonModel model=new MedicineCommonModel(dao);
//				model.deleteKCDJ(jgid, ctx);
//			}
			
			// 出库顺序
			List<String> list_order = new ArrayList<String>();
			int cksx_ypxq_yf=0;
			try{
			cksx_ypxq_yf = MedicineUtils
					.parseInt(ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.CKSX_YPXQ_YF, ctx));// 是否按效期排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_YPXQ_YF, e);
			}
			if (cksx_ypxq_yf > 0) {
				String cksx_ypxq_order_yf = MedicineUtils
						.parseString(ParameterUtil.getParameter(jgid,
								BSPHISSystemArgument.CKSX_YPXQ_ORDER_YF, ctx));
				if ("A".equals(cksx_ypxq_order_yf)) {
					list_order.add("YPXQ");
				} else {
					list_order.add("YPXQ desc");
				}
			}
			int cksx_kcsl_yf=0;
			try{
			 cksx_kcsl_yf = MedicineUtils
					.parseInt(ParameterUtil.getParameter(jgid,
							BSPHISSystemArgument.CKSX_KCSL_YF, ctx));// 是否按库存排序
			}catch(Exception e){
				MedicineUtils.throwsSystemParameterException(logger, BSPHISSystemArgument.CKSX_KCSL_YF, e);
			}
			if (cksx_kcsl_yf > 0) {
				String cksx_kcsl_order_yf = MedicineUtils
						.parseString(ParameterUtil.getParameter(jgid,
								BSPHISSystemArgument.CKSX_KCSL_ORDER_YF, ctx));
				if ("A".equals(cksx_kcsl_order_yf)) {
					if (list_order.size() > 0) {
						list_order.add(",YPSL");
					} else {
						list_order.add("YPSL");
					}
				} else {
					if (list_order.size() > 0) {
						list_order.add(",YPSL desc");
					} else {
						list_order.add("YPSL desc");
					}
				}
			}
			long yfsb = 0;
			StringBuffer hql_kcdj=new StringBuffer();//查询冻结数量
			hql_kcdj.append("select YPSL as YPSL,YFBZ as YFBZ from YF_KCDJ where LSJG=:lsjg and YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and sysdate-DJSJ <=:kcdjts");
			StringBuffer hql_yfbz=new StringBuffer();//查询药房包装,用于计算冻结的实际数量
			hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
			StringBuffer hql_kcsl_sum=new StringBuffer();//查询总的库存数量,用于减掉冻结的和当前要发的比较
			hql_kcsl_sum.append("select sum(YPSL) as KCSL from YF_KCMX where YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg");
			for (int i = 0; i < body.size(); i++) {
				List<Map<String, Object>> list_kc_update = new ArrayList<Map<String, Object>>();// 需要更新的库存集合
				Map<String, Object> map_ypkc = body.get(i);

				if (map_ypkc.get("YPXH") == null
						|| map_ypkc.get("YPCD") == null
						|| map_ypkc.get("YFSB") == null
						|| map_ypkc.get("YPDJ") == null
						|| map_ypkc.get("YPSL") == null) {
					return null;
				}
				long ypxh = MedicineUtils.parseLong(map_ypkc.get("YPXH"));
				long ypcd = MedicineUtils.parseLong(map_ypkc.get("YPCD"));
				yfsb = MedicineUtils.parseLong(map_ypkc.get("YFSB"));
				// 增加以下代码 用于增加站点发药减掉中心的库存
				if (map_ypkc.containsKey("SJFYBZ")
						&& MedicineUtils.parseInt(map_ypkc.get("SJFYBZ")) == 1) {
					yfsb = MedicineUtils.parseLong(map_ypkc.get("SJYF"));
				}
				double ypdj = MedicineUtils.parseDouble(map_ypkc.get("YPDJ"));
				double ypsl = 0;
				if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
					ypsl = MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_ypkc.get("YPSL"))
									* MedicineUtils.parseInt(map_ypkc
											.get("CFTS")));
				} else {
					ypsl = MedicineUtils.parseDouble(map_ypkc.get("YPSL"));
				}
				//库存冻结代码
				if(SFQYYFYFY==1){
					//long cfsb=MedicineUtils.parseLong(map_ypkc.get("CFSB"));//有传就是发药,不传就是0
					double djsl=0;//冻结的总数量
					double kcsl=0;//总的库存数量
					Map<String,Object> map_par_kcdj=new HashMap<String,Object>();
					map_par_kcdj.put("ypxh", ypxh);
					map_par_kcdj.put("ypcd", ypcd);
					map_par_kcdj.put("yfsb", yfsb);
					//map_par_kcdj.put("cfsb", cfsb);
					map_par_kcdj.put("kcdjts", KCDJTS);
					map_par_kcdj.put("lsjg", ypdj);
					List<Map<String,Object>> list_kcdj=dao.doQuery(hql_kcdj.toString(), map_par_kcdj);
					//map_par_kcdj.remove("cfsb");
					map_par_kcdj.remove("kcdjts");
					//map_par_kcdj.remove("lsjg");
					List<Map<String,Object>> list_kcsl=dao.doSqlQuery(hql_kcsl_sum.toString(), map_par_kcdj);
					if(list_kcsl!=null&&list_kcsl.size()>0&&list_kcsl.get(0)!=null){
						kcsl=MedicineUtils.parseDouble(list_kcsl.get(0).get("KCSL"));
					}
					if(list_kcdj!=null&&list_kcdj.size()>0){
						Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
						map_par_yfbz.put("yfsb", yfsb);
						map_par_yfbz.put("ypxh", ypxh);
						Map<String,Object> map_yfbz=dao.doLoad(hql_yfbz.toString(), map_par_yfbz);
						int yfbz=MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
						for(Map<String,Object> map_kcdj:list_kcdj){
							djsl+=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, map_kcdj.get("YPSL"), map_kcdj.get("YFBZ"))/yfbz);
						}
					}
					if(kcsl-djsl<ypsl){//如果库存不够
						session.getTransaction().rollback();
						List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
						Map<String, Object> map_no = new HashMap<String, Object>();
						map_no.put("ypxh", ypxh);
						list_no.add(map_no);
						return list_no;
					}
				}
				StringBuffer hql_kc_xtjg = new StringBuffer();// 相同价格的药品库存查询
				hql_kc_xtjg
						.append("select YPSL as YPSL,SBXH as KCSB,JGID as JGID,YFSB as YFSB,YPXH as YPXH,YPCD as YPCD,YPPH as YPPH,YPXQ as YPXQ,LSJG as LSJG ,PFJG as PFJG,JHJG as JHJG,YKJH as YKJH from YF_KCMX where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and LSJG=:ypdj ");
				if (list_order.size() > 0) {
					StringBuffer order = new StringBuffer();
					order.append(" order by ");
					for (String o : list_order) {
						order.append(o);
					}
					hql_kc_xtjg.append(order.toString());
				}
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ypxh", ypxh);
				parameters.put("ypcd", ypcd);
				parameters.put("yfsb", yfsb);
				parameters.put("ypdj", ypdj);
				List<Map<String, Object>> list_kc_xtjg = dao.doQuery(
						hql_kc_xtjg.toString(), parameters);
				// 有相同价格的库存
				if (list_kc_xtjg != null) {
					for (int j = 0; j < list_kc_xtjg.size(); j++) {
						Map<String, Object> map_kc_update = new HashMap<String, Object>();// 存要更新的库存
						Map<String, Object> map_kc_xtjg = list_kc_xtjg.get(j);
						double ypsl_kc_xtjg = MedicineUtils
								.parseDouble(map_kc_xtjg.get("YPSL"));// 相同价格的库存数量
						long kcsb_kc_xtjg = MedicineUtils.parseLong(map_kc_xtjg
								.get("KCSB"));// 相同价格的库存识别
						for (String key : map_ypkc.keySet()) {
							map_kc_xtjg.put(key, map_ypkc.get(key));
						}
						if (ypsl_kc_xtjg < ypsl) {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", 0);
							list_kc_update.add(map_kc_update);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg
										.put("YPSL",
												MedicineUtils
														.formatDouble(
																2,
																ypsl_kc_xtjg
																		/ MedicineUtils
																				.parseInt(map_ypkc
																						.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl_kc_xtjg);
							}
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = ypsl - ypsl_kc_xtjg;
						} else {
							map_kc_update.put("kcsb", kcsb_kc_xtjg);
							map_kc_update.put("ypsl", ypsl_kc_xtjg - ypsl);
							if (MedicineUtils.parseInt(map_ypkc.get("CFLX")) == 3) {
								map_kc_xtjg
										.put("YPSL",
												MedicineUtils.formatDouble(
														2,
														ypsl
																/ MedicineUtils
																		.parseInt(map_ypkc
																				.get("CFTS"))));
							} else {
								map_kc_xtjg.put("YPSL", ypsl);
							}
							list_kc_update.add(map_kc_update);
							list_kc_update_ret.add(map_kc_xtjg);
							ypsl = 0;
							break;
						}
					}
				}
				// 库存不够
				if (ypsl > 0) {
					session.getTransaction().rollback();
					List<Map<String, Object>> list_no = new ArrayList<Map<String, Object>>();
					Map<String, Object> map_no = new HashMap<String, Object>();
					map_no.put("ypxh", ypxh);//这个key不要随便改,住院药房发药根据有没ypxh(小写)来判断是否库存不够
					list_no.add(map_no);
					return list_no;
				}
				StringBuffer hql_kc_update = new StringBuffer();// 库存更新
				hql_kc_update
						.append("update YF_KCMX set YPSL=:ypsl,JHJE=JHJG*:ypsl,PFJE=PFJG*:ypsl,LSJE=LSJG*:ypsl where SBXH=:sbxh");
				for (int x = 0; x < list_kc_update.size(); x++) {
					Map<String, Object> map_kc_update = list_kc_update.get(x);
					Map<String, Object> parameter = new HashMap<String, Object>();
					parameter.put("ypsl", MedicineUtils
							.parseDouble(map_kc_update.get("ypsl")));
					parameter.put("sbxh", map_kc_update.get("kcsb"));
					dao.doUpdate(hql_kc_update.toString(), parameter);
				}
			}
			Map<String, Object> map_kcsl_par = new HashMap<String, Object>();
			map_kcsl_par.put("yfsb", yfsb);
			StringBuffer sql_kc_ls_insert = new StringBuffer();
			sql_kc_ls_insert
					.append("insert into YF_KCMX_LS  select * from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doSqlUpdate(sql_kc_ls_insert.toString(), map_kcsl_par);
			StringBuffer hql_kc_delete = new StringBuffer();
			hql_kc_delete
					.append("delete from YF_KCMX  where YPSL=0 and YFSB=:yfsb");
			dao.doUpdate(hql_kc_delete.toString(), map_kcsl_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "减库存失败", e);
		}
		return list_kc_update_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 删除盘点单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removePharmacyInventoryProcessing(
			Map<String, Object> body) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append(" YFSB=:yfsb and PDDH=:pddh ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		map_par.put("pddh", MedicineUtils.parseInt(body.get("PDDH")));
		try {
			long l = dao.doCount("YF_YK02_GRLR", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils
						.getRetMap("当前盘点单有相应的盘点录入单,无法删除,请先删除相应的盘点录入单!");
			}
			hql = new StringBuffer();
			hql.append("delete from YF_YK02 where YFSB=:yfsb and PDDH=:pddh and CKBH=:ckbh");
			map_par.put("ckbh", MedicineUtils.parseInt(body.get("CKBJ")));
			dao.doUpdate(hql.toString(), map_par);
			hql = new StringBuffer();
			hql.append("delete from YF_YK01 where YFSB=:yfsb and PDDH=:pddh and CKBH=:ckbh");
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点单删除失败!", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 获取盘点状态(是否完成 是否汇总,谁完成)
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryState_pc(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		StringBuffer hql_pdzt = new StringBuffer();// 查询盘点状态
		hql_pdzt.append("select PDWC as PDWC,HZWC as HZWC from YF_YK01 where YFSB=:yfsb and PDDH=:pddh and CKBH=:ckbh");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", MedicineUtils.parseLong(body.get("YFSB")));
		map_par.put("pddh", MedicineUtils.parseInt(body.get("PDDH")));
		map_par.put("ckbh", MedicineUtils.parseInt(body.get("CKBH")));
		try {
			Map<String, Object> map_pdzt = dao.doLoad(hql_pdzt.toString(),
					map_par);
			Map<String, Object> map_wc = new HashMap<String, Object>();
			map_wc.put("LB", "是否完成");
			map_wc.put("SFWC", "未完成");
			if (MedicineUtils.parseInt(map_pdzt.get("PDWC")) == 1) {
				map_wc.put("SFWC", "已完成");
			}
			ret.add(map_wc);
			Map<String, Object> map_hz = new HashMap<String, Object>();
			map_hz.put("LB", "是否汇总");
			map_hz.put("SFWC", "未汇总");
			if (MedicineUtils.parseInt(map_pdzt.get("HZWC")) == 1) {
				map_hz.put("SFWC", "已汇总");
			}
			ret.add(map_hz);
			StringBuffer hql_grlr = new StringBuffer();// 查询个人录入情况
			hql_grlr.append("select b.PERSONNAME as CZY,sum(a.LRWC) as LRWC from YF_YK02_GRLR a,SYS_Personnel  b where a.LRRY=b.PERSONID and a.YFSB=:yfsb and a.PDDH=:pddh and a.CKBH=:ckbh group by b.PERSONNAME");
			List<Map<String, Object>> list_grlr = dao.doQuery(
					hql_grlr.toString(), map_par);
			for (Map<String, Object> map_grlr : list_grlr) {
				Map<String, Object> map_state = new HashMap<String, Object>();
				map_state.put("LB", map_grlr.get("CZY"));
				map_state.put("SFWC", "未完成");
				if (MedicineUtils.parseInt(map_grlr.get("LRWC")) > 0) {
					map_state.put("SFWC", "已完成");
				}
				ret.add(map_state);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点状态查询失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 药房盘点录修改数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void savePharmacyInventoryProcessingXgsl(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		List<Map<String, Object>> list_pd02_grlr = (List<Map<String, Object>>) body
				.get("PD02");
		long sbxh=MedicineUtils.parseLong(body.get("sbxh"));
		double spsl = 0;
		//int pddh = 0;
		//long kcsb = 0;
		//long yfsb = 0;
		//long ypxh = 0;
		//long ypcd = 0;
		//double lsjg = 0;
		//double jhjg = 0;
		//int i = 0;
		try {
			for (Map<String, Object> map_pd02_grlr : list_pd02_grlr) {
				//if (i == 0) {
					//pddh = MedicineUtils.parseInt(map_pd02_grlr.get("PDDH"));
					//kcsb = MedicineUtils.parseLong(map_pd02_grlr.get("KCSB"));
					//yfsb = MedicineUtils.parseLong(map_pd02_grlr.get("YFSB"));
					//ypxh = MedicineUtils.parseLong(map_pd02_grlr.get("YPXH"));
					//ypcd = MedicineUtils.parseLong(map_pd02_grlr.get("YPCD"));
					//lsjg = MedicineUtils.parseDouble(map_pd02_grlr.get("LSJG"));
					//jhjg = MedicineUtils.parseDouble(map_pd02_grlr.get("JHJG"));
				//}
				spsl += MedicineUtils.parseDouble(map_pd02_grlr.get("SPSL"))
						+ MedicineUtils.formatDouble(
								2,
								MedicineUtils.parseDouble(map_pd02_grlr
										.get("YKSL"))
										* MedicineUtils.parseInt(MedicineUtils
												.parseInt(map_pd02_grlr
														.get("YKBZ")))
										* MedicineUtils.parseInt(map_pd02_grlr
												.get("YFBZ")));
				map_pd02_grlr.put("XLSE", MedicineUtils.formatDouble(2, spsl*MedicineUtils.parseDouble(map_pd02_grlr.get("LSJG"))));
				map_pd02_grlr.put("XJHE", MedicineUtils.formatDouble(2, spsl*MedicineUtils.parseDouble(map_pd02_grlr.get("JHJG"))));
				dao.doSave("update", BSPHISEntryNames.YF_YK02_GRLR,
						map_pd02_grlr, false);
			}
			StringBuffer hql = new StringBuffer();
			hql.append("update YF_YK02  set SPSL=:spsl where SBXH=:sbxh ");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("sbxh", sbxh);
			map_par.put("spsl", spsl);
			dao.doUpdate(hql.toString(), map_par);
			StringBuffer hql_je = new StringBuffer();
			hql_je.append("update YF_YK02 set XLSE=YLSE,XJHE=YJHE where SBXH=:sbxh and PQSL=SPSL");
			map_par.remove("spsl");
			map_par.remove("BZ");
			dao.doUpdate(hql_je.toString(), map_par);
//			if (kcsb != 0) {
//				hql.append("update YF_YK02  set SPSL=:spsl where YFSB=:yfsb and PDDH=:pddh and KCSB=:kcsb");
//				Map<String, Object> map_par = new HashMap<String, Object>();
//				map_par.put("spsl", spsl);
//				map_par.put("yfsb", yfsb);
//				map_par.put("pddh", pddh);
//				map_par.put("kcsb", kcsb);
//				dao.doUpdate(hql.toString(), map_par);
//			} else {
//				hql.append("update YF_YK02  set SPSL=:spsl where YFSB=:yfsb and PDDH=:pddh and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and JHJG=:jhjg");
//				Map<String, Object> map_par = new HashMap<String, Object>();
//				map_par.put("spsl", spsl);
//				map_par.put("yfsb", yfsb);
//				map_par.put("pddh", pddh);
//				map_par.put("ypxh", ypxh);
//				map_par.put("ypcd", ypcd);
//				map_par.put("lsjg", lsjg);
//				map_par.put("jhjg", jhjg);
//				dao.doUpdate(hql.toString(), map_par);
//			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "盘点处理修改数量失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点处理修改数量失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 盘点处理完成盈亏金额查询
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryPharmacyInventoryProcessingWc(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> ret = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int ckbh = MedicineUtils.parseInt(body.get("CKBH"));
		StringBuffer cnd = new StringBuffer();
		cnd.append("['and',['eq',['$','YFSB'],['l',").append(yfsb)
				.append("]],['eq',['$','PDDH'],['i',").append(pddh)
				.append("]],['eq',['$','CKBH'],['i',").append(ckbh)
				.append("]]]");
		try {
			List<?> cnds = CNDHelper.toListCnd(cnd.toString());
			List<Map<String, Object>> list_pd02 = dao.doList(cnds, null,
					BSPHISEntryNames.YF_YK02);
			double pylsje = 0, pklsje = 0, pyjhje = 0, pkjhje = 0;
			for (Map<String, Object> map_pd02 : list_pd02) {
				if (MedicineUtils.parseDouble(map_pd02.get("SPSL"))
						- MedicineUtils.parseDouble(map_pd02.get("PQSL")) > 0) {
					pylsje += MedicineUtils
							.formatDouble(
									2,
									(MedicineUtils.parseDouble(map_pd02
											.get("SPSL")) - MedicineUtils
											.parseDouble(map_pd02.get("PQSL")))
											* MedicineUtils
													.parseDouble(map_pd02
															.get("LSJG")));
					pyjhje += MedicineUtils
							.formatDouble(
									2,
									(MedicineUtils.parseDouble(map_pd02
											.get("SPSL")) - MedicineUtils
											.parseDouble(map_pd02.get("PQSL")))
											* MedicineUtils
													.parseDouble(map_pd02
															.get("JHJG")));
				} else if (MedicineUtils.parseDouble(map_pd02.get("SPSL"))
						- MedicineUtils.parseDouble(map_pd02.get("PQSL")) < 0) {
					pklsje += MedicineUtils
							.formatDouble(
									2,
									(MedicineUtils.parseDouble(map_pd02
											.get("PQSL")) - MedicineUtils
											.parseDouble(map_pd02.get("SPSL")))
											* MedicineUtils
													.parseDouble(map_pd02
															.get("LSJG")));
					pkjhje += MedicineUtils
							.formatDouble(
									2,
									(MedicineUtils.parseDouble(map_pd02
											.get("PQSL")) - MedicineUtils
											.parseDouble(map_pd02.get("SPSL")))
											* MedicineUtils
													.parseDouble(map_pd02
															.get("JHJG")));
				}
			}
			ret.put("PYLSJE", pylsje);
			ret.put("PKLSJE", pklsje);
			ret.put("PYJHJE", pyjhje);
			ret.put("PKJHJE", pkjhje);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点处理金额查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "盘点处理金额查询失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 盘点录入初始数据增加
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePharmacyInventoryInitData(Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		StringBuffer hql = new StringBuffer();
		hql.append("select PDDH as PDDH from YF_YK01  where YFSB=:yfsb and PDWC = 0 and HZWC = 0 and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("jgid", jgid);
		Map<String, Object> ret = MedicineUtils.getRetMap();
		try {
			Map<String, Object> map_pddh = dao.doLoad(hql.toString(), map_par);
			if (map_pddh == null
					|| MedicineUtils.parseInt(map_pddh.get("PDDH")) == 0) {
				return MedicineUtils.getRetMap("没有需要录入的盘点单!");
			}
			int pddh = MedicineUtils.parseInt(map_pddh.get("PDDH"));
			body.put("pddh", pddh);
			hql = new StringBuffer();
			hql.append("select sum(LRWC) as LRWC from YF_YK02_GRLR  where YFSB=:yfsb and PDDH=:pddh and LRRY = :userId and JGID=:jgid");
			map_par.put("pddh", pddh);
			map_par.put("userId", userId);
			List<Map<String, Object>> list_lrwc = dao.doSqlQuery(
					hql.toString(), map_par);
			if (list_lrwc != null && list_lrwc.get(0) != null
					&& list_lrwc.get(0).get("LRWC") != null
					&& MedicineUtils.parseInt(list_lrwc.get(0).get("LRWC")) > 0) {
				body.put("pdwc", 1);
				ret.put("body", body);
				return ret;
			}
			// 如果没有记录,新增记录
			if (list_lrwc.get(0).get("LRWC") == null) {
				hql = new StringBuffer();
				hql.append("select a.YPXH as YPXH,a.YPCD as YPCD,a.YPGG as YPGG,a.YFBZ as YFBZ,a.YFDW as YFDW,a.PQSL as PQSL,a.PQSL as SPSL,a.LSJG as LSJG,a.PFJG as PFJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,a.YLSE as YLSE,a.YPFE as YPFE,a.YJHE as YJHE,a.YLSE as XLSE,a.YPFE as XPFE,a.YJHE as XJHE,a.KCSB as KCSB,b.ZXBZ as YKBZ,b.YPDW as YKDW from YF_YK02 a,YK_TYPK b where a.YPXH=b.YPXH and a.PDDH=:pddh and a.YFSB=:yfsb and a.JGID=:jgid");
				map_par.remove("userId");
				List<Map<String, Object>> list_yk02 = dao.doQuery(
						hql.toString(), map_par);
				for (Map<String, Object> map_yk02 : list_yk02) {
					map_yk02.put("JGID", jgid);
					map_yk02.put("YFSB", yfsb);
					map_yk02.put("CKBH", 0);
					map_yk02.put("PDDH", pddh);
					map_yk02.put("LRRY", userId);
					map_yk02.put("YKSL", 0);
					// map_yk02.put("SPSL", 0);
					map_yk02.put("LRWC", 0);
					map_yk02.put("LRBZ", 1);
					dao.doSave("create", BSPHISEntryNames.YF_YK02_GRLR,
							map_yk02, false);
				}
			}
			body.put("pdwc", 0);
			ret.put("body", body);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "初始盘点录入数据失败!", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "初始盘点录入数据失败!", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 删除盘点录入
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> removePharmacyInventoryEntry(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		StringBuffer hql = new StringBuffer();
		hql.append(" YFSB=:yfsb and PDDH=:pddh and LRRY=:lrry and JGID=:jgid and LRBZ=1");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("pddh", MedicineUtils.parseInt(body.get("PDDH")));
		map_par.put("lrry", userId);
		map_par.put("jgid", jgid);
		try {
			long l = dao.doCount("YF_YK02_GRLR", hql.toString(), map_par);
			if (l > 0) {
				return MedicineUtils.getRetMap("有已录入的数据,不能删除");
			}
			hql = new StringBuffer();
			hql.append("delete from YF_YK02_GRLR where YFSB=:yfsb and PDDH=:pddh and LRRY=:lrry and JGID=:jgid ");
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点录入删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 保存盘点单
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void savePharmacyInventoryEntry(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_pd02 = (List<Map<String, Object>>) body
				.get("PD02");
		try {
			for (Map<String, Object> map_pd02 : list_pd02) {
				dao.doSave("update", BSPHISEntryNames.YF_YK02_GRLR, map_pd02,
						false);
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "盘点录入保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "盘点录入保存失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 完成/取消完成 盘点单
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void savePharmacyInventoryEntryWc(Map<String, Object> body,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		int pddh = MedicineUtils.parseInt(body.get("PDDH"));
		int tag = MedicineUtils.parseInt(body.get("TAG"));
		List<Map<String, Object>> list_pd02 = (List<Map<String, Object>>) body
				.get("PD02");
		// 如果有修改数据 先保存
		if (list_pd02 != null && list_pd02.size() > 0) {
			savePharmacyInventoryEntry(body);
		}
		try {
			if (tag == 1) {
				StringBuffer hql_sum = new StringBuffer();
				hql_sum.append("select sum(LRBZ) as LRBZ from YF_YK02_GRLR  where YFSB=:yfsb and PDDH=:pddh and LRRY = :userId and JGID=:jgid");
				Map<String, Object> map_par_sum = new HashMap<String, Object>();
				map_par_sum.put("pddh", pddh);
				map_par_sum.put("userId", userId);
				map_par_sum.put("jgid", jgid);
				map_par_sum.put("yfsb", yfsb);
				List<Map<String, Object>> list_lrwc = dao.doSqlQuery(
						hql_sum.toString(), map_par_sum);
				if (list_lrwc != null
						&& list_lrwc.get(0) != null
						&& list_lrwc.get(0).get("LRBZ") != null
						&& MedicineUtils.parseInt(list_lrwc.get(0).get("LRBZ")) == 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "没有已盘点的药品,无法完成盘点");
				}
			} else {
				StringBuffer hql_count = new StringBuffer();
				hql_count.append(" YFSB=:yfsb and PDDH=:pddh and HZWC=1");
				Map<String, Object> map_count = new HashMap<String, Object>();
				map_count.put("yfsb", yfsb);
				map_count.put("pddh", pddh);
				long l = dao
						.doCount("YF_YK01", hql_count.toString(), map_count);
				if (l > 0) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "盘点单已汇总,无法取消完成!");
				}
			}
			StringBuffer hql = new StringBuffer();
			hql.append("update YF_YK02_GRLR  set LRWC=:tag where PDDH=:pddh and YFSB=:yfsb and LRRY =:lrry and JGID=:jgid");
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("yfsb", yfsb);
			map_par.put("pddh", pddh);
			map_par.put("lrry", userId);
			map_par.put("jgid", jgid);
			map_par.put("tag", tag);
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			if (tag == 0) {
				MedicineUtils.throwsException(logger, "盘点录入取消完成失败", e);
			} else {
				MedicineUtils.throwsException(logger, "盘点录入完成失败", e);
			}
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-26
	 * @description 重置,为了将实盘数量变成0 用于删除盘点单
	 * @updateInfo
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void savePharmacyInventoryEntryCz(Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = MedicineUtils.parseString(user.getUserId());
		StringBuffer hql = new StringBuffer();
		hql.append("update YF_YK02_GRLR set SPSL=0,LRBZ=0 where YFSB=:yfsb and LRRY =:lrry and JGID=:jgid and LRWC=0");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		map_par.put("lrry", userId);
		map_par.put("jgid", jgid);
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "重置失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-10
	 * @description 盘点录入新增
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> savePharmacyInventoryEntryAdd(Map<String,Object> body) throws ModelDataOperationException{
		UserRoleToken user = UserRoleToken.getCurrent();
		String userId = MedicineUtils.parseString(user.getUserId());
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer hql_repeat=new StringBuffer();//查询有没相同记录
		hql_repeat.append(" PDDH=:pddh and LRRY=:lrry and YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and JHJG=:jhjg");
		StringBuffer hql_kc_repeat=new StringBuffer();//检查下库存里面有没该记录
		hql_kc_repeat.append(" YFSB=:yfsb and YPXH=:ypxh and YPCD=:ypcd and LSJG=:lsjg and JHJG=:jhjg");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("pddh", MedicineUtils.parseInt(body.get("PDDH")));
		map_par.put("lrry", userId);
		map_par.put("yfsb", yfsb);
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
		map_par.put("lsjg", MedicineUtils.parseDouble(body.get("LSJG")));
		map_par.put("jhjg", MedicineUtils.parseDouble(body.get("JHJG")));
		try {
		if(body.containsKey("YPPH")&&!"".equals(MedicineUtils.parseString(body.get("YPPH")))){
			hql_repeat.append(" and YPPH=:ypph ");
			hql_kc_repeat.append( " and YPPH=:ypph " );
			map_par.put("ypph", MedicineUtils.parseString(body.get("YPPH")));
		}else{
			hql_repeat.append(" and YPPH is null ");
			hql_kc_repeat.append(" and YPPH is null ");
		}
		if(body.containsKey("YPXQ")&&!"".equals(MedicineUtils.parseString(body.get("YPXQ")))){
			hql_repeat.append(" and YPXQ=:ypxq ");
			hql_kc_repeat.append(" and YPXQ=:ypxq ");
			map_par.put("ypxq",sdf.parse(body.get("YPXQ")+""));
		}else{
			hql_repeat.append(" and YPXQ is null ");
			hql_kc_repeat.append(" and YPXQ is null ");
		}
			long l=dao.doCount("YF_YK02_GRLR", hql_repeat.toString(), map_par);
			if(l>0){
				return MedicineUtils.getRetMap("录入中有相同批次药品!不需要新增");
			}
			map_par.remove("pddh");
			map_par.remove("lrry");
			l=dao.doCount("YF_KCMX", hql_kc_repeat.toString(), map_par);
			if(l>0){
				return MedicineUtils.getRetMap("异常错误:库存中已有该批次药品.请重新生成盘点单");
			}
			body.put("PQSL", 0);
			body.put("YKSL", 0);
			body.put("SPSL", 0);
			body.put("YFSB", yfsb);
			body.put("YLSE", 0);
			body.put("YJHE", 0);
			body.put("XLSE", 0);
			body.put("XJHE", 0);
			body.put("KCSB", 0);
			body.put("JGID", jgid);
			body.put("CKBH", 0);
			body.put("LRRY", userId);
			body.put("LRWC", 0);
			body.put("LRBZ", 1);
			dao.doSave("create", BSPHISEntryNames.YF_YK02_GRLR,
					body, false);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "新增失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "新增失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "新增失败,日期格式错误", e);
		}
		return MedicineUtils.getRetMap();
	}

}
