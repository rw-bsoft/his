package phis.application.fsb.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

public class FamilySickBedBackMedicineModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedBackMedicineModel.class);

	public FamilySickBedBackMedicineModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
	 * @description 中心待退药记录查询
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryBackMedicine()
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct b.ZYH as ZYH,b.BRXM as BRXM,b.ZYHM as ZYHM from JC_TYMX a,JC_BRRY b where a.JGID=b.JGID and a.ZYH=b.ZYH and a.TJBZ=1 and a.TYRQ is null  and a.YFSB=:yfsb and a.JGID=:jgid ");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("jgid", user.getManageUnit().getId());
		map_par.put("yfsb",
				MedicineUtils.parseLong(user.getProperty("pharmacyId")));
		List<Map<String, Object>> list_ret = null;
		try {
			list_ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床待退药记录查询失败", e);
		}
		return list_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
	 * @description 中心退药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHospitalPharmacyBackMedicine(
			List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String userid = user.getUserId();
		List<String> list_jfslcg = new ArrayList<String>();// 保存退药数量大于计费数量的药品名称
		List<String> list_fyslcg = new ArrayList<String>();// 保存退药数量大于发药数量的药品名称
		StringBuffer hql_jscs = new StringBuffer();// 查询结算次数
		hql_jscs.append("select max(JSCS) as JSCS from JC_JCJS where ZYH=:zyh and JGID=:jgid and ZFPB=0");
		StringBuffer hql_ktsl_fymx = new StringBuffer();// 从发药明细查询可退数量
		hql_ktsl_fymx
				.append("select sum(FYSL) as KTSL from JC_FYMX where ZYH=:zyh  and FYXH=:ypxh and YPCD=:ypcd and JGID=:jgid ");
		StringBuffer hql_yffymx_sum = new StringBuffer();// 查询退药的药品发药总数量
		hql_yffymx_sum
				.append("select sum(YPSL) as YPSL from YF_JCFYMX where YZXH=:yzxh and JGID=:jgid and YPXH=:ypxh and YPCD=:ypcd and YPDJ=:lsjg ");
		List<?> cnd_yffymx;// 从yf_zyfymx里面查找出和退药对应的药品记录
		Date d = new Date();// 先取出来 保持记录里面的数据一样
		StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
		hql_yfbz.append("select YFBZ as YFBZ,YFSB as YFSB,YPXH as YPXH from YF_YPXX where YFSB in(:yfsb) ");
		StringBuffer hql_yksb = new StringBuffer();
		hql_yksb.append("select YKSB as YKSB,YPXH as YPXH from YK_YPXX where JGID=:jgid");
		Map<String, Object> map_ret = new HashMap<String, Object>();
		//StringBuffer hql_yz = new StringBuffer();
		//hql_yz.append("update JC_BRYZ set TJZX=0 where JLXH=:yzxh");
		try {
			List<Long> yfsbs = new ArrayList<Long>();// 存药房识别
			if (body == null || body.size() == 0) {
				return map_ret;
			}
			for (Map<String, Object> map : body) {
				boolean tag = false;
				for (Long yfsb : yfsbs) {
					if (yfsb == MedicineUtils.parseLong(map.get("YFSB"))) {
						tag = true;
						break;
					}
				}
				if (!tag) {
					yfsbs.add(MedicineUtils.parseLong(map.get("YFSB")));
				}
			}
			Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
			map_par_yfbz.put("yfsb", yfsbs);
			List<Map<String, Object>> list_yfbz = dao.doSqlQuery(
					hql_yfbz.toString(), map_par_yfbz);// 退药药房的所有药品包装
			Map<String, Object> map_par_yksb = new HashMap<String, Object>();
			map_par_yksb.put("jgid", jgid);
			List<Map<String, Object>> list_yksb = dao.doQuery(
					hql_yksb.toString(), map_par_yksb);
			// 退药申请直接退药,可能有多个药房的发药记录,循环 按药房退药
			for (Long yfsb : yfsbs) {
				// 更新YF_FYJL
				Map<String, Object> map_yf_fyjl_data = new HashMap<String, Object>();// yf_fyjl保存记录
				map_yf_fyjl_data.put("JGID", jgid);
				map_yf_fyjl_data.put("FYSJ", d);
				map_yf_fyjl_data.put("FYGH", userid);
				map_yf_fyjl_data.put("FYLX", 1);
				map_yf_fyjl_data.put("JGID", jgid);
				map_yf_fyjl_data.put("YFSB", yfsb);
				map_yf_fyjl_data.put("FYFS", 0);
				map_yf_fyjl_data.put("DYPB", 0);
				map_yf_fyjl_data.put("FYLX", 5);
				map_yf_fyjl_data = dao.doSave("create",
						"phis.application.fsb.schemas.JC_FYJL",
						map_yf_fyjl_data, false);
				for (Map<String, Object> map_tymx : body) {
					if (MedicineUtils.parseLong(map_tymx.get("YFSB")) != yfsb) {
						continue;
					}
					Map<String, Object> map_par_sfty = new HashMap<String, Object>();
					map_par_sfty.put("jlxh",
							MedicineUtils.parseLong(map_tymx.get("JLXH")));
					double ktsl = 0;
					double ktsl_cy = 0;
					// 查询药房包装
					Map<String, Object> map_yfyp = new HashMap<String, Object>();
					map_yfyp.put("YPXH",
							MedicineUtils.parseLong(map_tymx.get("YPXH")));
					map_yfyp.put("YFSB", yfsb);
					Map<String, Object> map_yfbz = MedicineUtils.getRecord(
							list_yfbz, new String[] { "YPXH", "YFSB" },
							map_yfyp, new String[] { "YPXH", "YFSB" });
					if (map_yfbz == null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR,
								"无对应的药房药品,请确认是否家床库存管理参数改变导致(不管理保存记录,改成直接发药退药提交会导致)");
					}
					int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
					double tysl = MedicineUtils.formatDouble(2,
							MedicineUtils.parseDouble(map_tymx.get("YPSL")));
					// 判断退药数量有没超过计费数量
					Map<String, Object> map_par = new HashMap<String, Object>();
					map_par.put("zyh",
							MedicineUtils.parseLong(map_tymx.get("ZYH")));
					map_par.put("jgid", jgid);
					map_par.put("ypxh",
							MedicineUtils.parseLong(map_tymx.get("YPXH")));
					map_par.put("ypcd",
							MedicineUtils.parseLong(map_tymx.get("YPCD")));
					Map<String, Object> map_ktsl_fymx = dao.doLoad(
							hql_ktsl_fymx.toString(), map_par);
					if (map_ktsl_fymx != null) {
						ktsl = MedicineUtils.parseDouble(map_ktsl_fymx
								.get("KTSL"));
					}
					if (ktsl + ktsl_cy + tysl < 0) {
						list_jfslcg.add(map_tymx.get("YPMC") + "");
						continue;
					}
					// 增加YF_ZYFYMX负记录
					map_par.remove("zyh");
					map_par.remove("jscs");
					map_par.put("yzxh",
							MedicineUtils.parseLong(map_tymx.get("YZID")));
					map_par.put("lsjg",
							MedicineUtils.parseDouble(map_tymx.get("YPJG")));
					Map<String, Object> map_jcfymx_sum = dao.doLoad(
							hql_yffymx_sum.toString(), map_par);
					if (map_jcfymx_sum == null
							|| MedicineUtils.parseDouble(map_jcfymx_sum
									.get("YPSL")) < tysl) {
						list_fyslcg.add(map_tymx.get("YPMC") + "");
						continue;
					}
					StringBuffer s_cnd = new StringBuffer();
					s_cnd.append("['and',['eq',['$','YPCD'],['d',")
							.append(MedicineUtils.parseLong(map_tymx
									.get("YPCD")))
							.append("]],['and',['eq',['$','YPDJ'],['d',")
							.append(MedicineUtils.parseDouble(map_tymx
									.get("YPJG")))
							.append("]],['and',['eq',['$','YPXH'],['d',")
							.append(MedicineUtils.parseLong(map_tymx
									.get("YPXH")))
							.append("]],['and',['eq',['$','YZXH'],['d',")
							.append(MedicineUtils.parseLong(map_tymx
									.get("YZID")))
							.append("]],['eq',['$','JGID'],['s',").append(jgid)
							.append("]]]]]]");
					cnd_yffymx = CNDHelper.toListCnd(s_cnd.toString());
					List<Map<String, Object>> list_jcfymx = dao.doList(
							cnd_yffymx, "YPSL desc",
							"phis.application.fsb.schemas.YF_JCFYMX");
					for (Map<String, Object> map_jcfymx : list_jcfymx) {
						if (MedicineUtils.parseDouble(map_jcfymx.get("YPSL")) < 0) {
							continue;
						}
						if (tysl == 0) {
							break;
						}
						// 下面3个价格调价用 故提取出来
						double lsjg = MedicineUtils.parseDouble(map_jcfymx
								.get("LSJG"));
						double pfjg = MedicineUtils.parseDouble(map_jcfymx
								.get("PFJG"));
						double jhjg = MedicineUtils.parseDouble(map_jcfymx
								.get("JHJG"));
						double jdsl = 0;
						if (MedicineUtils.parseDouble(map_jcfymx.get("YPSL")) > -tysl) {
							jdsl = tysl;
						} else {
							jdsl = -MedicineUtils.formatDouble(2, MedicineUtils
									.parseDouble(map_jcfymx.get("YPSL")));
						}
						map_jcfymx.put("TYGL", map_jcfymx.get("JLXH"));
						map_jcfymx.remove("JLXH");
						map_jcfymx.put("FYLX", 5);
						map_jcfymx.put("YPSL", jdsl);
						map_jcfymx.put("FYRQ", d);
						// map_jcfymx.put("JFID",map_yf_fyjl_data.get("JLID"));
						map_jcfymx.put("TYXH",
								MedicineUtils.parseLong(map_tymx.get("JLXH")));
						map_jcfymx.put("TJXH", 0);
						map_jcfymx.put("FYJE", -MedicineUtils.formatDouble(
								4,
								-jdsl
										* MedicineUtils.parseDouble(map_jcfymx
												.get("YPDJ"))));
						map_jcfymx.put("LSJE",
								-MedicineUtils.formatDouble(4, -jdsl * lsjg));
						map_jcfymx.put("PFJE",
								-MedicineUtils.formatDouble(4, -jdsl * pfjg));
						map_jcfymx.put("JHJE",
								-MedicineUtils.formatDouble(4, -jdsl * jhjg));

						saveIncreaseInventory(
								MedicineUtils.formatDouble(
										4,
										-jdsl
												* MedicineUtils
														.parseInt(map_jcfymx
																.get("YFBZ"))
												/ yfbz),
								MedicineUtils.parseLong(map_jcfymx.get("KCSB")),
								MedicineUtils.parseLong(map_jcfymx.get("YPXH")),
								MedicineUtils.parseLong(map_jcfymx.get("YPCD")),
								jgid, yfsb, MedicineUtils.parseLong(map_jcfymx
										.get("JLID")));
						// 处理调价
						Map<String, Object> map_yksb = MedicineUtils.getRecord(
								list_yksb, new String[] { "YPXH" }, map_yfyp,
								new String[] { "YPXH" });
						if (map_yksb != null) {
							savePriceAdjustment(
									MedicineUtils.parseLong(map_yksb
											.get("YKSB")), jgid, yfsb,
									map_jcfymx,
									-(jdsl * (MedicineUtils.parseInt(map_jcfymx
											.get("YFBZ")) / yfbz)), userid,
									yfbz);
						}

						tysl = MedicineUtils.formatDouble(4, tysl - jdsl);
						// 新增ZY_FYMX负记录

						Map<String, Object> map_jcyz = new HashMap<String, Object>();
						map_jcyz = dao.doLoad(
								"phis.application.fsb.schemas.JC_BRYZ_ZYFY",
								MedicineUtils.parseLong(map_tymx.get("YZID")));
						int yplx = MedicineUtils.parseInt(map_jcyz.get("YPLX"));
						long ypxh = MedicineUtils.parseLong(map_jcyz
								.get("YPXH"));
						long fyxm = BSPHISUtil.getfygb(yplx, ypxh, dao, ctx);
						FamilySickBedDoctorExecutionModel model = new FamilySickBedDoctorExecutionModel(
								dao);
						double zfbl = model.getZfbl(
								MedicineUtils.parseLong(map_tymx.get("ZYH")),
								MedicineUtils.parseLong(map_tymx.get("YPXH")),
								fyxm);
						Map<String, Object> map_jc_fymx = new HashMap<String, Object>();
						map_jc_fymx.put("JGID", jgid);
						map_jc_fymx.put("ZYH", map_tymx.get("ZYH"));
						map_jc_fymx.put("FYRQ", d);
						map_jc_fymx.put("FYXH", map_tymx.get("YPXH"));
						map_jc_fymx.put("FYMC", map_jcyz.get("YZMC"));
						map_jc_fymx.put("YPCD", map_tymx.get("YPCD"));
						map_jc_fymx.put("FYSL",
								-MedicineUtils.formatDouble(2, -jdsl));
						map_jc_fymx.put("FYDJ", map_tymx.get("YPJG"));
						map_jc_fymx.put(
								"ZJJE",
								-MedicineUtils.formatDouble(
										2,
										-MedicineUtils.parseDouble(map_jc_fymx
												.get("FYSL")) * lsjg));
						map_jc_fymx.put("ZFJE", -MedicineUtils.formatDouble(
								2,
								zfbl
										* (-MedicineUtils
												.parseDouble(map_jc_fymx
														.get("ZJJE")))));
						map_jc_fymx.put("YSGH", map_jcyz.get("YSGH"));
						map_jc_fymx.put("SRGH", userid);
						map_jc_fymx.put("QRGH", userid);
						map_jc_fymx.put("FYBQ", map_jcyz.get("SRKS"));
						map_jc_fymx.put("FYKS",
								MedicineUtils.parseLong(map_jcyz.get("BRKS")));
						map_jc_fymx.put("ZXKS",
								MedicineUtils.parseLong(map_jcyz.get("ZXKS")));
						map_jc_fymx.put("JFRQ", d);
						map_jc_fymx.put("XMLX", 2);
						map_jc_fymx.put("YPLX", map_jcyz.get("YPLX"));
						map_jc_fymx.put("FYXM", fyxm);
						map_jc_fymx.put("ZFBL", zfbl);
						map_jc_fymx.put("YZXH", map_tymx.get("YZID"));
						map_jc_fymx.put("JSCS", 0);
						map_jc_fymx.put("ZLJE", 0);
						map_jc_fymx.put("ZLXZ", map_tymx.get("ZLXZ"));
						map_jc_fymx.put("YEPB", map_tymx.get("YEPB"));
						map_jc_fymx.put("DZBL", 0);
						map_jc_fymx = dao.doSave("create",
								"phis.application.fsb.schemas.JC_FYMX",
								map_jc_fymx, false);
						map_jcfymx.put("JFID", map_jc_fymx.get("JLXH"));
						map_jcfymx.put("JLID", map_yf_fyjl_data.get("JLID"));
						dao.doSave("create",
								"phis.application.fsb.schemas.YF_JCFYMX",
								map_jcfymx, false);
					}
					// 更新BQ_TYMX表
					map_tymx.put("TYRQ", new Date());
					map_tymx.put("TJBZ", 0);
					map_tymx.put("JLID", map_yf_fyjl_data.get("JLID"));
					dao.doSave("update",
							"phis.application.fsb.schemas.JC_TYMX_TY",
							map_tymx, false);
//					Map<String, Object> map_yz_update = new HashMap<String, Object>();
//					map_yz_update.put("yzxh",
//							MedicineUtils.parseLong(map_tymx.get("YZID")));
					//dao.doUpdate(hql_yz.toString(), map_yz_update);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "家床药房退药失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "家床药房退药失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "家床药房退药失败", e);
		}
		StringBuffer ret = new StringBuffer();
		if (list_jfslcg.size() > 0) {
			ret.append("药品:");
			for (int i = 0; i < list_jfslcg.size(); i++) {
				ret.append("[").append(list_jfslcg.get(i)).append("]");
			}
			ret.append("退药数量超过计费数量");
		}
		if (list_fyslcg.size() > 0) {
			ret.append("药品:");
			for (int i = 0; i < list_fyslcg.size(); i++) {
				ret.append("[").append(list_fyslcg.get(i)).append("]");
			}
			ret.append("退药数量超过发药数量");
		}
		map_ret.put("msg", ret.toString());
		return map_ret;

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
	 * @description 中心退药全部退回中心
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveBackMedicineFullRefund(List<Map<String, Object>> body,
			Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		hql.append("update JC_TYMX  set TJBZ=0 where TJBZ=1 and ZYH=:zyh  and YFSB=:yfsb");
		for (Map<String, Object> map_ty : body) {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("zyh", MedicineUtils.parseLong(map_ty.get("ZYH")));
			map_par.put("yfsb", yfsb);
			try {
				dao.doUpdate(hql.toString(), map_par);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "家床退药全部退回中心失败", e);
			}
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-3-3
	 * @description 中心退药退回中心
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveBackMedicineRefund(List<Map<String, Object>> body)
			throws ModelDataOperationException {
		for (Map<String, Object> map_tymx : body) {
			map_tymx.put("TJBZ", 0);
			try {
				dao.doSave("update", "phis.application.fsb.schemas.JC_TYMX_TY",
						map_tymx, false);
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "家床退药退回中心验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "家床退药退回中心失败", e);
			}
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-4-8
	 * @description 判断是否有调价 ,如有调价 则新增调价记录
	 * @updateInfo
	 * @param yksb
	 *            药库识别
	 * @param jgid
	 *            机构id
	 * @param yfsb
	 *            药房识别
	 * @param body
	 *            存有药品的价格等信息
	 * @param tjsl
	 *            调价数量
	 * @param userid
	 *            用户id
	 * @param yfbz
	 *            药房包装
	 * @throws ModelDataOperationException
	 */
	public void savePriceAdjustment(long yksb, String jgid, long yfsb,
			Map<String, Object> body, double tjsl, String userid, int yfbz)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
			map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
			map_par.put("jgid", jgid);
			map_par.put("yfsb", yfsb);
			StringBuffer hql_fysj = new StringBuffer();
			hql_fysj.append("select FYSJ as FYSJ from JC_FYJL where JLID=:jlid ");
			map_par.clear();
			map_par.put("jlid", MedicineUtils.parseLong(body.get("JLID")));
			Map<String, Object> map_fysj = dao.doLoad(hql_fysj.toString(),
					map_par);
			if (map_fysj == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "发药时间查询失败");
			}
			StringBuffer hql_tjjl_count = new StringBuffer();// 查询是否有调价
			StringBuffer hql_tjjl_sum = new StringBuffer();// 查出最后调价记录主键
			map_par.clear();
			if (body.containsKey("KCSB")
					&& MedicineUtils.parseLong(body.get("KCSB")) != 0) {
				hql_tjjl_count
						.append(" KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum
						.append("select max(SBXH) as SBXH from YF_TJJL  where KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("kcsb", MedicineUtils.parseLong(body.get("KCSB")));
			} else {
				hql_tjjl_count
						.append(" YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum
						.append("select max(SBXH) as SBXH from YF_TJJL  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
				map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
				map_par.put("yfsb", yfsb);
			}
			map_par.put("fyrq", map_fysj.get("FYSJ"));
			long l = dao.doCount("YF_TJJL", hql_tjjl_count.toString(), map_par);
			if (l != 0) {
				Map<String, Object> map_tjjl_sbxh = dao.doLoad(
						hql_tjjl_sum.toString(), map_par);
				StringBuffer hql_tjjl = new StringBuffer();
				hql_tjjl.append("select XLSJ as XLSJ,XJHJ as XJHJ,XPFJ as XPFJ from YF_TJJL where SBXH=:sbxh");
				map_par.clear();
				map_par.put("sbxh",
						MedicineUtils.parseLong(map_tjjl_sbxh.get("SBXH")));
				Map<String, Object> map_tjjl_r = dao.doLoad(
						hql_tjjl.toString(), map_par);
				Map<String, Object> map_tjjl = new HashMap<String, Object>();
				map_tjjl.put("YPXH", MedicineUtils.parseLong(body.get("YPXH")));
				map_tjjl.put("YPGG", body.get("YPGG"));
				map_tjjl.put("YFDW", body.get("YFDW"));
				map_tjjl.put("JGID", jgid);
				map_tjjl.put("YFSB", yfsb);
				map_tjjl.put("YPCD", MedicineUtils.parseLong(body.get("YPCD")));
				map_tjjl.put("YFBZ", yfbz);
				map_tjjl.put("YKSB", yksb);// 药库识别
				map_tjjl.put("CKBH", 0);// 窗口编号先默认保存为0
				map_tjjl.put("TJFS", 1);// 调价方式先默认保存为1
				map_tjjl.put("TJDH", 0);// 调价单号先默认保存为0
				map_tjjl.put("TJRQ", new Date());// 调价日期
				map_tjjl.put("TJSL", tjsl);// 调价数量
				map_tjjl.put("XLSJ", map_tjjl_r.get("XLSJ"));// 新零售价
				map_tjjl.put("XPFJ", map_tjjl_r.get("XJHJ"));// 新批发价
				map_tjjl.put("XJHJ", map_tjjl_r.get("XPFJ"));// 原进货价
				map_tjjl.put(
						"YLSJ",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(body.get("LSJG"))
										* yfbz
										/ MedicineUtils.parseInt(body
												.get("YFBZ"))));// 原零售价
				map_tjjl.put(
						"YPFJ",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(body.get("PFJG"))
										* yfbz
										/ MedicineUtils.parseInt(body
												.get("YFBZ"))));// 原批发价
				map_tjjl.put(
						"YJHJ",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(body.get("JHJG"))
										* yfbz
										/ MedicineUtils.parseInt(body
												.get("YFBZ"))));// 原进货价
				map_tjjl.put("CZGH", userid);// 操作工号
				map_tjjl.put("TJWH", "住院退药");
				map_tjjl.put(
						"YLSE",
						MedicineUtils.formatDouble(4,
								(Double) map_tjjl.get("YLSJ"))
								* tjsl);// 原零售价
				map_tjjl.put(
						"YPFE",
						MedicineUtils.formatDouble(4,
								((Double) map_tjjl.get("YPFJ")) * tjsl));// 原批发金额
				map_tjjl.put(
						"YJHE",
						MedicineUtils.formatDouble(4,
								((Double) map_tjjl.get("YJHJ")) * tjsl));// 原进货金额
				map_tjjl.put(
						"XLSE",
						MedicineUtils.formatDouble(4,
								((Double) map_tjjl_r.get("XLSJ")) * tjsl));// 新零售金额
				map_tjjl.put(
						"XPFE",
						MedicineUtils.formatDouble(4,
								((Double) map_tjjl_r.get("XPFJ")) * tjsl));// 新批发金额
				map_tjjl.put(
						"XJHE",
						MedicineUtils.formatDouble(4,
								((Double) map_tjjl_r.get("XJHJ")) * tjsl));// 新进货金额
				map_tjjl.put("KCSB", MedicineUtils.parseLong(body.get("KCSB")));// 库存识别
				map_tjjl.put("KCSL", tjsl);// 库存数量
				dao.doSave("create", BSPHISEntryNames.YF_TJJL, map_tjjl, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "调价记录新增失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "调价记录新增验证失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-9-2
	 * @description 退药增加库存
	 * @updateInfo
	 * @param kcsl
	 * @param kcsb
	 * @param ypxh
	 * @param ypcd
	 * @param jgid
	 * @param yfsb
	 * @param jlid
	 * @throws ModelDataOperationException
	 */
	public void saveIncreaseInventory(double kcsl, long kcsb, long ypxh,
			long ypcd, String jgid, long yfsb, long jlid)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map_kcmx = dao.doLoad(BSPHISEntryNames.YF_KCMX,
					kcsb);
			if (map_kcmx != null) {
				double xzkc = MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
						+ kcsl);
				map_kcmx.put("YPSL", xzkc);
				map_kcmx.put(
						"LSJE",
						MedicineUtils.formatDouble(4, xzkc
								* MedicineUtils.parseDouble(map_kcmx.get("LSJG"))));
				map_kcmx.put(
						"JHJE",
						MedicineUtils.formatDouble(4, xzkc
								* MedicineUtils.parseDouble(map_kcmx.get("JHJG"))));
				map_kcmx.put(
						"PFJE",
						MedicineUtils.formatDouble(4, xzkc
								* MedicineUtils.parseDouble(map_kcmx.get("PFJG"))));
				dao.doSave("update", BSPHISEntryNames.YF_KCMX, map_kcmx, false);
			} else {
				StringBuffer hql_kcmx_insert = new StringBuffer();
				hql_kcmx_insert.append("insert into YF_KCMX select * from YF_KCMX_LS where SBXH=:kcsb");
				Map<String, Object> map_par_ls = new HashMap<String, Object>();
				map_par_ls.put("kcsb", kcsb);
				dao.doSqlUpdate(hql_kcmx_insert.toString(), map_par_ls);// 从历史表新增库存
				map_kcmx = dao.doLoad(BSPHISEntryNames.YF_KCMX, kcsb);
				map_kcmx.put("YPSL", kcsl);
				StringBuffer hql_fysj = new StringBuffer();
				hql_fysj.append("select FYSJ as FYSJ from JC_FYJL where JLID=:jlid ");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("jlid", MedicineUtils.parseLong(jlid));
				Map<String, Object> map_fysj = dao.doLoad(hql_fysj.toString(),
						map_par);
				if (map_fysj == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "发药时间查询失败");
				}
				StringBuffer hql_tjjl_count = new StringBuffer();
				hql_tjjl_count
						.append(" YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.clear();
				map_par.put("ypxh", MedicineUtils.parseLong(ypxh));
				map_par.put("ypcd", MedicineUtils.parseLong(ypcd));
				map_par.put("yfsb", yfsb);
				map_par.put("fyrq", map_fysj.get("FYSJ"));
				long l = dao.doCount("YF_TJJL",
						hql_tjjl_count.toString(), map_par);
				if (l != 0) {
					StringBuffer hql_tjjl_sum = new StringBuffer();
					hql_tjjl_sum
							.append("select max(SBXH) as SBXH from YF_TJJL where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
					Map<String, Object> map_tjjl_sbxh = dao.doLoad(
							hql_tjjl_sum.toString(), map_par);
					StringBuffer hql_tjjl = new StringBuffer();
					hql_tjjl.append(
							"select XLSJ as XLSJ,XJHJ as XJHJ,XPFJ as XPFJ from YF_TJJL where SBXH=:sbxh");
					map_par.clear();
					map_par.put("sbxh", MedicineUtils.parseLong(map_tjjl_sbxh.get("SBXH")));
					Map<String, Object> map_tjjl = dao.doLoad(
							hql_tjjl.toString(), map_par);
					map_kcmx.put(
							"LSJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_tjjl.get("XLSJ"))));
					map_kcmx.put(
							"JHJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_tjjl.get("XJHJ"))));
					map_kcmx.put(
							"PFJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_tjjl.get("XPFJ"))));
					map_kcmx.put("LSJG", MedicineUtils.parseDouble(map_tjjl.get("LSJG")));
					map_kcmx.put("JHJG", MedicineUtils.parseDouble(map_tjjl.get("XJHJ")));
					map_kcmx.put("PFJG", MedicineUtils.parseDouble(map_tjjl.get("XPFJ")));
				} else {
					StringBuffer hql_jg = new StringBuffer();
					hql_jg.append(
							"select distinct a.LSJG*(b.YFBZ/c.ZXBZ) as LSJG,a.PFJG*(b.YFBZ/c.ZXBZ) as PFJG,a.JHJG*(b.YFBZ/c.ZXBZ) as JHJG from YK_CDXX a,YF_YPXX b,YK_TYPK c  where a.YPXH=:ypxh and a.YPCD=:ypcd and a.JGID=:jgid and b.YFSB=:yfsb and a.JGID=b.JGID and a.YPXH=b.YPXH and a.YPXH=c.YPXH");
					map_par.clear();
					map_par.put("ypxh", ypxh);
					map_par.put("ypcd", ypcd);
					map_par.put("jgid", jgid);
					map_par.put("yfsb", yfsb);
					Map<String, Object> map_jg = dao.doLoad(hql_jg.toString(),
							map_par);
					if (map_jg == null) {
						throw new ModelDataOperationException(
								ServiceCode.CODE_DATABASE_ERROR, "价格查询失败");
					}
					map_kcmx.put(
							"LSJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_jg.get("LSJG"))));
					map_kcmx.put(
							"JHJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_jg.get("JHJG"))));
					map_kcmx.put(
							"PFJE",
							MedicineUtils.formatDouble(4,
									kcsl * MedicineUtils.parseDouble(map_jg.get("PFJG"))));

				}
				dao.doSave("update", BSPHISEntryNames.YF_KCMX, map_kcmx, false);
				dao.doRemove(kcsb, BSPHISEntryNames.YF_KCMX_LS);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "库存增加失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "库存增加失败", e);
		}
	}
}
