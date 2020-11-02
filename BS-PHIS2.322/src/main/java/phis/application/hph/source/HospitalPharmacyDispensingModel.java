package phis.application.hph.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;
import phis.application.mds.source.MedicineUtils;
import phis.application.pha.source.PharmacyInventoryManageModel;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;

/**
 * 住院发药model
 * 
 * @author caijy
 * 
 */
public class HospitalPharmacyDispensingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPharmacyDispensingModel.class);

	public HospitalPharmacyDispensingModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 发药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveHospitalPharmacyDispensing(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Date d = new Date();
		Map<String, Object> ret = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制(12小时制会影响时间比对)
		List<Map<String, Object>> list_bqbr = (List<Map<String, Object>>) body
				.get("bq");// 病区tj01
		List<Map<String, Object>> list_fymx = (List<Map<String, Object>>) body
				.get("fymx");// 病区tj02
		// List<String> list_kcbg = new ArrayList<String>();//
		// 存那些库存不够的药,格式[药品:ypmc,产地:cddz]
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));// 用户的药房识别
		String jgid = user.getManageUnit().getId();
		String userid = user.getUserId();
		StringBuffer hql_tj02_update = new StringBuffer();// bq_tj02更新发药标志
		hql_tj02_update
				.append("update BQ_TJ02  set FYBZ=1,FYGH=:fygh,FYRQ=:fyrq ,FYJE=:fyje where JLXH=:jlxh");
		StringBuffer hql_tj02_bqyz = new StringBuffer();// 查询相同yzxh的提交是否都发药
		hql_tj02_bqyz.append(" YZXH=:yzxh and FYBZ=0");
		//hql_tj02_bqyz.append("select count(*) as NUM,YZXH as YZXH from BQ_TJ02 where FYBZ=0 and YZXH in (:yzxhs) group by YZXH");
		StringBuffer hql_yp = new StringBuffer();// 查询药品的基本药物标志
//		hql_yp.append("select JYLX as JYLX from YK_TYPK  where YPXH=:ypxh");
		hql_yp.append("select JYLX as JYLX,YPXH as YPXH,ZBLB as ZBLB,YPMC as YPMC from YK_TYPK where YPXH in (:ypxhs)");
		StringBuffer hql_bq01_update = new StringBuffer();// 更新bq_tj01的fybz
		//hql_bq01_update.append("update BQ_TJ01 set FYBZ=1 where TJXH=:tjxh");
		hql_bq01_update.append("update BQ_TJ01 set FYBZ=1 where TJXH in (:tjxhs)");
		StringBuffer hql_count = new StringBuffer();// 查询相同tjxh的提交明细是否都已发药
		hql_count.append("  TJXH=:tjxh and FYBZ=0 ");
		StringBuffer hql_tj02_tz_update = new StringBuffer();// bq_tj02停嘱更新
		hql_tj02_tz_update.append("update BQ_TJ02 set FYBZ=3 where JLXH=:jlxh");
		StringBuffer hql_yfbz = new StringBuffer();// 查询药房包装
//		hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
		hql_yfbz.append("select YFBZ as YFBZ,YPXH as YPXH,QZCL as QZCL from YF_YPXX where YFSB=:yfsb and YPXH in (:ypxhs)");
		StringBuffer hql_sffy = new StringBuffer();// 查询是否已经发过药
//		hql_sffy.append("select FYBZ as FYBZ from BQ_TJ02 where JLXH=:jlxh");
		hql_sffy.append("select FYBZ as FYBZ,JLXH as JLXH from BQ_TJ02 where JLXH in(:jlxhs)");
		StringBuffer hql_tjdyz=new StringBuffer();//查询提交单中的所有医嘱信息
		hql_tjdyz.append("select JLXH as JLXH,QRSJ as QRSJ,ZXKS as ZXKS,SRKS as SRKS,YPLX as YPLX,YSGH as YSGH,YZMC as YZMC from ZY_BQYZ where JLXH in (:yzxhs)");
		try {
			//将需要的数据 都缓存起来
			Set<Long> l_ypxhs=new HashSet<Long>();//所有需要查询的药品序号
			Set<Long> l_yzxhs=new HashSet<Long>();//所有需要查询的医嘱序号
			Set<Long> l_jlxhs=new HashSet<Long>();//所有需要查询bq_tj02的jlxh
			for(Map<String,Object> m_temp: list_fymx ){
				l_ypxhs.add(MedicineUtils.parseLong(m_temp.get("YPXH")));
				l_yzxhs.add(MedicineUtils.parseLong(m_temp.get("YZXH")));
				l_jlxhs.add(MedicineUtils.parseLong(m_temp.get("JLXH")));
			}
			Map<String,Object> map_par_temp=new HashMap<String,Object>();
			map_par_temp.put("yzxhs", l_yzxhs);
//			List<Map<String,Object>> list_tj02_temp=dao.doSqlQuery(hql_tj02_bqyz.toString(), map_par_temp);//存有当前医嘱中未发药的提交记录数量
			List<Map<String,Object>> list_bqyz_temp=dao.doSqlQuery(hql_tjdyz.toString(), map_par_temp);//存有提交单中的所有医嘱信息
			map_par_temp.clear();
			map_par_temp.put("ypxhs", l_ypxhs);
			List<Map<String,Object>> list_ykyp_temp=dao.doSqlQuery(hql_yp.toString(), map_par_temp);//存有药品信息
			map_par_temp.put("yfsb", yfsb);
			List<Map<String,Object>> list_yfyp_temp=dao.doSqlQuery(hql_yfbz.toString(), map_par_temp);//存有药房药品信息
			map_par_temp.clear();
			map_par_temp.put("jlxhs", l_jlxhs);
			List<Map<String,Object>> list_fybz_tj02_temp=dao.doSqlQuery(hql_sffy.toString(), map_par_temp);//存有tj02的发药标志
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
			map_yf_fyjl_data = dao.doSave("create", BSPHISEntryNames.YF_FYJL,
					map_yf_fyjl_data, false);
			// 返回给打印用的
			Map<String, Object> otherRet = new HashMap<String, Object>();
			otherRet.put("FYBQ", list_bqbr.get(0).get("TJBQ"));
			otherRet.put("JLID", map_yf_fyjl_data.get("JLID"));
			otherRet.put("FYSJ", d);
			ret.put("otherRet", otherRet);
			// 计算发药数量和金额
			qzcl(list_fymx, yfsb,list_yfyp_temp);
			getZFBL(list_fymx,list_ykyp_temp);
			boolean isRollBack = true;// 判断是否要回滚,当没有记录新增时回滚
			for (Map<String, Object> map_fymx : list_fymx) {
//				Map<String, Object> map_par_sffy = new HashMap<String, Object>();
//				map_par_sffy.put("jlxh",
//						MedicineUtils.parseLong(map_fymx.get("JLXH")));
//				Map<String, Object> map_sffy = dao.doLoad(hql_sffy.toString(),
//						map_par_sffy);
				Map<String, Object> map_sffy =MedicineUtils.getRecord(list_fybz_tj02_temp, MedicineUtils.parseLong(map_fymx.get("JLXH")), "JLXH");
				if (map_sffy == null
						|| MedicineUtils.parseDouble(map_sffy.get("FYBZ")) != 0) {
					// System.out.println("已发");
					continue;
				}
//				Map<String, Object> map_bqyz = new HashMap<String, Object>();// 病区医嘱
//				map_bqyz = dao.doLoad(BSPHISEntryNames.ZY_BQYZ_ZYFY,
//						MedicineUtils.parseLong(map_fymx.get("YZXH")));
				Map<String, Object> map_bqyz =MedicineUtils.getRecord(list_bqyz_temp, MedicineUtils.parseLong(map_fymx.get("YZXH")), "JLXH");
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
					long l = dao.doCount("BQ_TJ02", hql_tj02_bqyz.toString(),
							map_par_bqyz);
//					Map<String,Object> map_sffy_temp=MedicineUtils.getRecord(list_tj02_temp, MedicineUtils.parseLong(map_fymx.get("YZXH")), "YZXH");
//					long l=MedicineUtils.parseLong(map_sffy_temp.get("NUM"));
					if (l == 0) {
						map_bqyz.put("SYBZ", 0);
					}
					if (map_bqyz.get("QRSJ") == null
							|| ((Date) map_bqyz.get("QRSJ")).getTime() < (sdf
									.parse(map_fymx.get("QRRQ") + ""))
									.getTime()) {
						map_bqyz.put("QRSJ",
								sdf.parse(map_fymx.get("QRRQ") + ""));
					}
					// if (map_bqyz.get("TZSJ") != null) {
					// map_bqyz.put("LSBZ", 1);
					// }
					// 更新病区医嘱
					dao.doSave("update", BSPHISEntryNames.ZY_BQYZ_ZYFY,
							map_bqyz, false);
					continue;
				}
				Map<String, Object> map_bqbr = new HashMap<String, Object>();// bq_tj02对应的bq_tj01记录
				for (int i = 0; i < list_bqbr.size(); i++) {
					if ((MedicineUtils.parseLong(list_bqbr.get(i).get("TJXH")) == MedicineUtils
							.parseLong(map_fymx.get("TJXH")))
							|| (MedicineUtils.parseLong(list_bqbr.get(i).get(
									"ZYH")) == MedicineUtils.parseLong(map_fymx
									.get("ZYH")))) {
						map_bqbr = list_bqbr.get(i);
					}
				}
//				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
//				map_par_yfbz.put("yfsb", yfsb);
//				map_par_yfbz.put("ypxh",
//						MedicineUtils.parseLong(map_fymx.get("YPXH")));
//				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
//						map_par_yfbz);
				Map<String, Object> map_yfbz=MedicineUtils.getRecord(list_yfyp_temp, MedicineUtils.parseLong(map_fymx.get("YPXH")), "YPXH");
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
				// 更新库存
				// List<Map<String, Object>> list_kcmx = new
				// ArrayList<Map<String, Object>>();
				map_fymx.put("YFSB", yfsb);
				map_fymx.put(
						"YPSL",
						MedicineUtils.formatDouble(
								4,
								MedicineUtils.parseDouble(map_fymx.get("FYSL"))
										* MedicineUtils.parseInt(map_fymx
												.get("YFBZ")) / yfbz));
				// list_kcmx.add(map_fymx);
				// MedicinesPharmacyManageModel mmd = new
				// MedicinesPharmacyManageModel(
				// dao);
				// List<Map<String, Object>> list_ret = mmd.
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

//				int yplx = MedicineUtils.parseInt(map_bqyz.get("YPLX"));
//				long fyxm = BSPHISUtil
//						.getfygb(yplx,
//								MedicineUtils.parseLong(map_fymx.get("YPXH")),
//								dao, ctx);// 费用项目
//				double zfbl = getZfbl(
//						MedicineUtils.parseLong(map_fymx.get("ZYH")),
//						MedicineUtils.parseLong(map_fymx.get("YPXH")), fyxm);
				double zfbl=MedicineUtils.parseDouble(map_fymx.get("ZFBL"));
//				Map<String, Object> map_par_yp = new HashMap<String, Object>();
//				map_par_yp.put("ypxh",
//						MedicineUtils.parseLong(map_fymx.get("YPXH")));
//				Map<String, Object> map_yp = dao.doLoad(hql_yp.toString(),
//						map_par_yp);
				Map<String, Object> map_yp=MedicineUtils.getRecord(list_ykyp_temp, MedicineUtils.parseLong(map_fymx.get("YPXH")), "YPXH");
				long fyxm =MedicineUtils.parseLong(map_yp.get("ZBLB"));
				double fyje = 0;// 费用总金额
				for (Map<String, Object> map_kcmx : list_ret) {
					// 更新ZY_FYMX
					Map<String, Object> map_zy_fymx = new HashMap<String, Object>();
					map_zy_fymx.put("JGID", jgid);
					map_zy_fymx.put("ZYH", map_fymx.get("ZYH"));
					map_zy_fymx.put("FYRQ", map_fymx.get("JFRQ"));
					map_zy_fymx.put("FYXH", map_fymx.get("YPXH"));
					map_zy_fymx.put("FYMC", map_bqyz.get("YZMC"));
					map_zy_fymx.put("YPCD", map_fymx.get("YPCD"));
					map_zy_fymx.put("FYSL", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_kcmx.get("YPSL"))
									* yfbz
									/ MedicineUtils.parseInt(map_fymx
											.get("YFBZ"))));
					map_zy_fymx.put("FYDJ", MedicineUtils.formatDouble(
							4,
							MedicineUtils.parseDouble(map_kcmx.get("LSJG"))
									* MedicineUtils.parseInt(map_fymx
											.get("YFBZ")) / yfbz));
					map_zy_fymx.put("ZJJE", MedicineUtils.formatDouble(
							2,
							MedicineUtils.parseDouble(map_zy_fymx.get("FYSL"))
									* MedicineUtils.parseDouble(map_zy_fymx
											.get("FYDJ"))));
					fyje += MedicineUtils.parseDouble(map_zy_fymx.get("ZJJE"));
					map_zy_fymx.put("ZFJE", MedicineUtils.formatDouble(
							2,
							zfbl
									* MedicineUtils.parseDouble(map_zy_fymx
											.get("ZJJE"))));
					map_zy_fymx.put("YSGH", map_bqyz.get("YSGH"));
					map_zy_fymx.put("SRGH", userid);
					map_zy_fymx.put("QRGH", userid);
					map_zy_fymx.put("FYBQ", map_bqyz.get("SRKS"));
					map_zy_fymx.put("FYKS", map_fymx.get("FYKS"));
					map_zy_fymx.put("ZXKS", map_bqyz.get("ZXKS") == null ? 0
							: map_bqyz.get("ZXKS"));
					map_zy_fymx.put("JFRQ", d);
					map_zy_fymx.put("XMLX", 2);
					map_zy_fymx.put("YPLX", map_bqyz.get("YPLX"));
					map_zy_fymx.put("FYXM", fyxm);
					map_zy_fymx.put("ZFBL", zfbl);
					map_zy_fymx.put("YZXH", map_fymx.get("YZXH"));
					map_zy_fymx.put("JSCS", 0);
					map_zy_fymx.put("ZLJE", 0);
					map_zy_fymx.put("ZLXZ", map_fymx.get("ZLXZ"));
					map_zy_fymx.put("YEPB", map_fymx.get("YEPB"));
					map_zy_fymx.put("DZBL", 0);
					map_zy_fymx = dao.doSave("create",
							BSPHISEntryNames.ZY_FYMX, map_zy_fymx, false);
					isRollBack = false;
					// 更新YF_ZYFYMX
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
					map_yf_zyfymx_data.put("JFID", map_zy_fymx.get("JLXH"));
					dao.doSave("create", BSPHISEntryNames.YF_ZYFYMX,
							map_yf_zyfymx_data, false);
				}
				// 更新bq_tj02
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
				long l = dao.doCount("BQ_TJ02", hql_tj02_bqyz.toString(),
						map_par_bqyz);
//				Map<String,Object> map_sffy_temp=MedicineUtils.getRecord(list_tj02_temp, MedicineUtils.parseLong(map_fymx.get("YZXH")), "YZXH");
//				long l=MedicineUtils.parseLong(map_sffy_temp.get("NUM"));
				if (l == 0) {
					map_bqyz.put("SYBZ", 0);
				}
				if (map_bqyz.get("QRSJ") == null
						|| ((Date) map_bqyz.get("QRSJ")).getTime() < (sdf
								.parse(map_fymx.get("QRRQ") + "")).getTime()) {
					map_bqyz.put("QRSJ", sdf.parse(map_fymx.get("QRRQ") + ""));
				}
				// if (map_bqyz.get("TZSJ") != null) {
				// map_bqyz.put("LSBZ", 1);
				// }
				// 更新病区医嘱
				dao.doSave("update", BSPHISEntryNames.ZY_BQYZ_ZYFY, map_bqyz,
						false);
			}
			if (isRollBack) {
				Session session = (Session) ctx.get(Context.DB_SESSION);
				session.getTransaction().rollback();
				ret.put("code", 9000);
				ret.put("msg", "未找到发药记录");
				return ret;
			}
			// 更新bq_tj01(明细都发药后fybz更新为1)
			List<Long> list_tjxh = new ArrayList<Long>();
			for (Map<String, Object> map_fymx : list_fymx) {
				if (list_tjxh.contains(MedicineUtils.parseLong(map_fymx
						.get("TJXH")))) {
					continue;
				}
				list_tjxh.add(MedicineUtils.parseLong(map_fymx.get("TJXH")));
			}
			Set<Long> s_tjxhs=new HashSet<Long>();
			for (long tjxh : list_tjxh) {
				Map<String, Object> map_par_count = new HashMap<String, Object>();
				map_par_count.put("tjxh", tjxh);
				long l = dao.doCount("BQ_TJ02", hql_count.toString(),
						map_par_count);
				if (l > 0) {
					continue;
				}
				s_tjxhs.add(tjxh);
				//dao.doUpdate(hql_bq01_update.toString(), map_par_count);
			}
			if(s_tjxhs.size()>0){
				Map<String,Object> map_par_tj01update=new HashMap<String,Object>();
				map_par_tj01update.put("tjxhs", s_tjxhs);
				dao.doUpdate(hql_bq01_update.toString(), map_par_tj01update);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病区发药失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "病区发药验证失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "病区发药失败", e);
		}
		// if (list_kcbg.size() > 0) {
		// ret.put("msg", list_kcbg.toString());
		// }
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 取整策略计算发药数量
	 * @updateInfo
	 * @param list_tj02
	 * @param yfsb
	 * @throws ModelDataOperationException
	 */
	public void qzcl(List<Map<String, Object>> list_tj02, long yfsb,List<Map<String, Object>> list_yfyp)
			throws ModelDataOperationException {
//		StringBuffer hql_qzcl = new StringBuffer();// 查询取整策略
//		hql_qzcl.append("select QZCL as QZCL from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh ");
		for (int i = 0; i < list_tj02.size(); i++) {
			Map<String, Object> map_tj02 = list_tj02.get(i);
//			Map<String, Object> map_par_qzcl = new HashMap<String, Object>();
//			map_par_qzcl.put("ypxh",
//					MedicineUtils.parseLong(map_tj02.get("YPXH")));
//			map_par_qzcl.put("yfsb", yfsb);
			int qzcl = 1;
//			try {
//				Map<String, Object> map_qzcl = dao.doLoad(hql_qzcl.toString(),
//						map_par_qzcl);
			Map<String, Object> map_qzcl =MedicineUtils.getRecord(list_yfyp, MedicineUtils.parseLong(map_tj02.get("YPXH")), "YPXH");
				if (map_qzcl == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "取整策略查询失败");
				}
				qzcl = MedicineUtils.parseInt(map_qzcl.get("QZCL"));
//			} catch (PersistentDataOperationException e) {
//				MedicineUtils.throwsException(logger, "取整策略查询失败", e);
//			}
			double fysl = 0;
			double fyje = 0;
			// 通过取整策略计算发药数量,发药金额
			if (qzcl == 0) {// 按次取整
				fysl = MedicineUtils.parseDouble(Math.ceil(MedicineUtils
						.parseDouble(map_tj02.get("YCSL"))));
				fyje = MedicineUtils.simpleMultiply(2, fysl, map_tj02.get("YPDJ"));
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
						fyje = MedicineUtils.simpleMultiply(2, fysl, map_tj02.get("YPDJ"));
						break;
					}
				}
				if (isqz == 0) {
					int num = 0;
					double yjje=0;
					for (Map<String, Object> m : list_tj02) {
						if (MedicineUtils
								.compareMaps(m, new String[] { "YZXH", "YPXH",
										"YPCD", "YPDJ" }, map_tj02,
										new String[] { "YZXH", "YPXH", "YPCD",
												"YPDJ" })) {
							num++;
							yjje+=	MedicineUtils.simpleMultiply(2, MedicineUtils
									.formatDouble(2, MedicineUtils
											.parseDouble(map_tj02.get("YCSL"))), map_tj02.get("YPDJ"));
						}
					}
					// 按每天发药数量去整时 取相同医嘱药品总和取整-除这条以外的记录和
					fysl = MedicineUtils.parseDouble(Math.ceil(MedicineUtils
							.parseDouble(map_tj02.get("YCSL")) * num))
							- MedicineUtils.formatDouble(
									2,
									MedicineUtils.parseDouble(map_tj02
											.get("YCSL")) * (num - 1));
					fyje=MedicineUtils.formatDouble(2, MedicineUtils.simpleMultiply(2, Math.ceil(MedicineUtils
							.parseDouble(map_tj02.get("YCSL")) * num), map_tj02.get("YPDJ")))-yjje+MedicineUtils.simpleMultiply(2, MedicineUtils
									.formatDouble(2, MedicineUtils
											.parseDouble(map_tj02.get("YCSL"))), map_tj02.get("YPDJ"));
				}
			} else {// 不取整
				fysl = MedicineUtils.formatDouble(2,
						MedicineUtils.parseDouble(map_tj02.get("YCSL")));
				fyje = MedicineUtils.simpleMultiply(2, fysl, map_tj02.get("YPDJ"));
			}
			map_tj02.put("FYSL", fysl);
			map_tj02.put("FYJE", fyje);
			map_tj02.put("QZCL", qzcl);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 获取自付比例
	 * @updateInfo
	 * @param zyh
	 * @param ypxh
	 * @param fyxm
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double getZfbl(long zyh, long ypxh, long fyxm)
			throws ModelDataOperationException {
		double d = 1;
		StringBuffer hql_brzx = new StringBuffer();// 查询病人性质
		hql_brzx.append("select BRXZ as BRXZ from ZY_BRRY where ZYH=:zyh");
		StringBuffer hql_ypjy = new StringBuffer();// 从gy_ypjy中查询自付比例
		hql_ypjy.append("select ZFBL as ZFBL from GY_YPJY  where BRXZ=:brxz and YPXH=:ypxh");
		StringBuffer hql_zfbl = new StringBuffer();// 从GY_ZFBL中查询自付比例
		hql_zfbl.append("select ZFBL as ZFBL from GY_ZFBL where BRXZ=:brxz and SFXM=:fyxm");
		Map<String, Object> map_par = new HashMap<String, Object>();
		double zfbl = 1;
		try {
			map_par.put("zyh", zyh);
			Map<String, Object> map_brxz = dao.doLoad(hql_brzx.toString(),
					map_par);
			if (map_brxz == null) {
				return d;
			}
			map_par.clear();
			map_par.put("brxz", MedicineUtils.parseLong(map_brxz.get("BRXZ")));
			map_par.put("ypxh", ypxh);
			Map<String, Object> map_zfbl = dao.doLoad(hql_ypjy.toString(),
					map_par);
			if (map_zfbl != null) {
				return MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
			}
			map_par.remove("ypxh");
			map_par.put("fyxm", fyxm);
			map_zfbl = dao.doLoad(hql_zfbl.toString(), map_par);
			if (map_zfbl == null) {
				return d;
			}
			zfbl = MedicineUtils.parseDouble(map_zfbl.get("ZFBL"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自付比例查询失败", e);
		}
		return zfbl;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 医嘱发药全退
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicineFullRefund(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_bqbr = (List<Map<String, Object>>) body
				.get("bq");// 病区tj01
		StringBuffer hql_tj01 = new StringBuffer();// bq_tj01的fybz改为1
		hql_tj01.append("update BQ_TJ01 set FYBZ=1 where TJXH=:tjxh");
		StringBuffer hql_tj02 = new StringBuffer();// bq_tj02的fybz改为2
		hql_tj02.append("update BQ_TJ02  set FYBZ=2 where TJXH=:tjxh and FYBZ!=1");
		StringBuffer hql_tj02_load = new StringBuffer();// 通过bq_tj01查找02记录
		hql_tj02_load
				.append(" select  distinct YZXH as YZXH from BQ_TJ02 where TJXH=:tjxh");
		StringBuffer hql_tj02_bqyz = new StringBuffer();// 查询相同yzxh的tj02是否全都退回了
		hql_tj02_bqyz.append(" YZXH=:yzxh and YFBZ=0");
		StringBuffer hql_bqyz = new StringBuffer();// 更新病区医嘱
		hql_bqyz.append(" update ZY_BQYZ set SYBZ=0 where JLXH=:yzxh");
		StringBuffer hql_tjxh = new StringBuffer();// 查询提交序号
		hql_tjxh.append("select distinct a.TJXH as TJXH from BQ_TJ01 a,BQ_TJ02 b where a.TJXH=b.TJXH and b.ZYH=:zyh and a.FYBZ=0 and b.FYBZ=0");
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
					long l = dao.doCount("BQ_TJ02", hql_tj02_bqyz.toString(),
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
			MedicineUtils.throwsException(logger, "病区全部退回失败", e);
		}

	}

	@SuppressWarnings("unchecked")
	public void saveMedicineRefund(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_fymx = (List<Map<String, Object>>) body
				.get("fymx");// 病区tj02
		StringBuffer hql_tj02_update = new StringBuffer();// 更新bq_tj02的发药标志字段
		hql_tj02_update
				.append("update BQ_TJ02 set FYBZ=2 where JLXH=:jlxh and FYBZ!=1");
		StringBuffer hql_tj01_update = new StringBuffer();// 更新bq_tj01的发药标志字段
		hql_tj01_update.append("update BQ_TJ01  set FYBZ=1 where TJXH=:tjxh");
		StringBuffer hql_tj02_count = new StringBuffer();// 查询提交明细是否都退回
		hql_tj02_count.append(" TJXH=:tjxh and FYBZ=0");
		StringBuffer hql_tj02_bqyz_count = new StringBuffer();// 用于判断相同yzxh的明细是否都已退回
		hql_tj02_bqyz_count.append(" YZXH=:yzxh and FYBZ=0");
		StringBuffer hql_bqyz_update = new StringBuffer();// 更新医嘱表的sybz字段
		hql_bqyz_update.append(" update ZY_BQYZ set SYBZ=0 where JLXH=:yzxh");
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
				long l = dao.doCount("BQ_TJ02", hql_tj02_bqyz_count.toString(),
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
				long l = dao.doCount("BQ_TJ02", hql_tj02_count.toString(),
						map_par_tjxh);
				if (l == 0) {
					dao.doUpdate(hql_tj01_update.toString(), map_par_tjxh);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医嘱退回失败", e);
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 查询药房是否已经维护领药科室
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> queryMedicineDepartment(Context ctx)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("select LYKS as LYKS from YF_YFLB where YFSB=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put(
				"yfsb",
				MedicineUtils.parseLong(UserRoleToken.getCurrent().getProperty(
						"pharmacyId")));
		try {
			Map<String, Object> map_lyks = dao.doLoad(hql.toString(), map_par);
			if (map_lyks == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "领药科室查询失败");
			}
			if (map_lyks.get("LYKS") != null
					&& (map_lyks.get("LYKS") + "").length() > 0) {
				return MedicineUtils.getRetMap();
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药科室查询失败", e);
		}
		return MedicineUtils.getRetMap("请先维护领药科室");
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 病区待发药记录查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensingWard(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		int yzlx = MedicineUtils.parseInt(body.get("YZLX"));
		UserRoleToken user = UserRoleToken.getCurrent();
		StringBuffer hql_lyks = new StringBuffer();// 查询领药科室
		hql_lyks.append("select LYKS as LYKS from YF_YFLB where YFSB=:yfsb");
		StringBuffer hql = new StringBuffer();
		hql.append("select a.TJBQ as TJBQ,a.FYFS as FYFS,count(1) as FYTS,c.OFFICENAME as KSMC,d.FSMC as FSMC from BQ_TJ01 a,BQ_TJ02 b,SYS_Office c,ZY_FYFS  d where a.FYFS=d.FYFS and a.JGID=:jgid and a.TJBQ=c.ID and c.HOSPITALAREA=1 and a.TJYF=:yfsb and a.TJBQ in (:lyks) and a.JGID=b.JGID and a.TJXH=b.TJXH and a.FYBZ=0 and b.FYBZ=0 ");
		if (yzlx == 0) {// bq_tj01的医嘱类型,1是药品 2是急诊用药
			hql.append(" and  ( a.YZLX=1)");
		} else if (yzlx == 1) {
			hql.append(" and  b.LSYZ=1 and a.YZLX=1");
		} else if (yzlx == 2) {
			hql.append(" and  b.LSYZ=0 and a.YZLX=1");
		} else if (yzlx == 3) {
			hql.append(" and a.YZLX=2");
		} else if (yzlx == 4) {
			hql.append(" and a.YZLX=3");
		} else if (yzlx == -1) {
			hql.append(" and a.YZLX=-1");
		}
		hql.append(" group by a.TJBQ,a.FYFS,c.OFFICENAME,d.FSMC order by a.TJBQ,d.FSMC,a.FYFS,c.OFFICENAME ");
		List<Map<String, Object>> list_ret = null;
		try {
			Map<String, Object> map_par_lyks = new HashMap<String, Object>();
			map_par_lyks.put("yfsb",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			Map<String, Object> map_lyks = dao.doLoad(hql_lyks.toString(),
					map_par_lyks);
			List<Object> lyks = new ArrayList<Object>();
			lyks.add(0);
			if (map_lyks != null && map_lyks.get("LYKS") != null) {
				String[] lykss = (map_lyks.get("LYKS") + "").split(",");
				for (int i = 0; i < lykss.length; i++) {
					lyks.add(MedicineUtils.parseInt(lykss[i]));
				}
			}
			Map<String, Object> map_par = new HashMap<String, Object>();
			map_par.put("jgid", user.getManageUnit().getId());
			map_par.put("yfsb",
					MedicineUtils.parseLong(user.getProperty("pharmacyId")));
			map_par.put("lyks", lyks.toArray());
			list_ret = dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医嘱发药病区列表查询失败", e);
		}
		return list_ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 待发药记录按病人查询
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
						"select distinct c.ZYH as ZYH,c.BRXM as BRXM,c.BRCH as BRCH,c.ZYHM as ZYHM,d.FSMC as FSMC,a.FYFS as FYFS,a.TJBQ as TJBQ from BQ_TJ01 a,BQ_TJ02 b,ZY_BRRY c,ZY_FYFS d where a.TJXH=b.TJXH and a.FYBZ = 0 and b.FYBZ = 0 and a.JGID =:jgid and a.TJYF=:yfsb  and b.ZYH=c.ZYH and a.FYFS=d.FYFS and ")
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

	// /**
	// *
	// * @author caijy
	// * @createDate 2014-9-1
	// * @description 待发药记录按病区查询
	// * @updateInfo
	// * @param body
	// * @param cnd
	// * @param ctx
	// * @return
	// * @throws ModelDataOperationException
	// */
	// public List<Map<String, Object>> queryDispensing_bq(List<?> cnd,Context
	// ctx)
	// throws ModelDataOperationException {
	// UserRoleToken user = UserRoleToken.getCurrent();
	// List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
	// try {
	// StringBuffer hql = new StringBuffer();
	// hql.append("select distinct a.YZLX as YZLX,a.XMLX as XMLX,a.TJSJ as TJSJ,a.FYFS as FYFS,a.TJBQ as TJBQ,b.PERSONNAME as PERSONNAME,a.TJGH as TJGH,a.FYBZ as FYBZ,a.JGID as JGID,a.TJXH as TJXH from BQ_TJ01 a,SYS_Personnel b,BQ_TJ02 c, ZY_BQYZ e where  a.JGID =:jgid and a.TJYF=:yfsb and a.TJGH=b.PERSONID and a.TJXH=c.TJXH and e.JLXH=c.YZXH ")
	// .append(ExpressionProcessor.instance().toString(cnd));
	// Map<String, Object> map_par = new HashMap<String, Object>();
	// map_par.put("yfsb",
	// MedicineUtils.parseLong(user.getProperty("pharmacyId")));
	// map_par.put("jgid", user.getManageUnit().getId());
	// ret = dao.doSqlQuery(hql.toString(), map_par);
	// SchemaUtil.setDictionaryMassageForForm(ret,
	// "phis.application.hph.schemas.BQ_TJ01_TJ");
	// } catch (PersistentDataOperationException e) {
	// MedicineUtils.throwsException(logger, "待发药记录按病人查询失败", e);
	// } catch (ExpException e) {
	// MedicineUtils.throwsException(logger, "待发药记录按病人查询失败", e);
	// }
	// return ret;
	// }
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
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
		//hql_yfbz.append("select YFBZ as YFBZ from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
		hql_yfbz.append("select YFBZ as YFBZ,QZCL as QZCL,YPXH as YPXH from YF_YPXX where YFSB=:yfsb and YPXH in (:ypxhs)");
		StringBuffer hql_kc = new StringBuffer();// 查询库存
		//hql_kc.append("select YPXH as YPXH,YPCD as YPCD,sum(YPSL) as YPSL,LSJG as LSJG from YF_KCMX where YPCD=:ypcd and YPXH=:ypxh and YFSB=:yfsb and LSJG=:lsjg  and JYBZ!=1 group by YPXH,YPCD,LSJG ");
		hql_kc.append("select YPXH as YPXH,YPCD as YPCD,sum(YPSL) as YPSL,LSJG as LSJG from YF_KCMX where YFSB=:yfsb and YPXH in (:ypxhs) and JYBZ!=1 group by YPXH,YPCD,LSJG,YPXH,YPCD ");
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		List<Map<String, Object>> list_tj02 = null;
		List<Map<String,Object>> list_kcsls=null;
		try {
			// cnd = CNDHelper.toListCnd(CNDHelper.createArrayStringCnd("and",
			// cnd, CNDHelper.toListCnd(CNDHelper.createArrayStringCnd(
			// "and", CNDHelper.toListCnd(CNDHelper
			// .createSimpleStringCnd("eq", "d.TJYF", "l",
			// yfsb)), CNDHelper
			// .toListCnd(CNDHelper.createSimpleStringCnd(
			// "eq", "a.JGID", "s", jgid))))));
			list_tj02 = dao.doList(cnd, null, BSPHISEntryNames.BQ_TJ02_YZFY);
			Set<Long> l_ypxhs=new HashSet<Long>();
			for(Map<String,Object> map_temp:list_tj02){
				l_ypxhs.add(MedicineUtils.parseLong(map_temp.get("YPXH")));
			}
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("ypxhs", l_ypxhs);
			map_par.put("yfsb", yfsb);
			List<Map<String,Object>> l_yfyp_temp=dao.doSqlQuery(hql_yfbz.toString(), map_par);
			qzcl(list_tj02, yfsb,l_yfyp_temp);
			list_kcsls=dao.doSqlQuery(hql_kc.toString(), map_par);
			for (Map<String, Object> map_tj02 : list_tj02) {
				if (MedicineUtils.parseInt(map_tj02.get("CFTS")) == 0) {
					map_tj02.put("CFTS", "");
				}
//				Map<String, Object> map_par_yfbz = new HashMap<String, Object>();
//				map_par_yfbz.put("yfsb", yfsb);
//				map_par_yfbz.put("ypxh",
//						MedicineUtils.parseLong(map_tj02.get("YPXH")));
//				Map<String, Object> map_yfbz = dao.doLoad(hql_yfbz.toString(),
//						map_par_yfbz);
				Map<String, Object> map_yfbz=MedicineUtils.getRecord(l_yfyp_temp, MedicineUtils.parseLong(map_tj02.get("YPXH")), "YPXH");
				if (map_yfbz == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "无对应药房药品信息!");
				}
				double fysl = MedicineUtils.formatDouble(4,
						MedicineUtils.parseDouble(map_tj02.get("FYSL"))
								* MedicineUtils.parseInt(map_tj02.get("YFBZ"))
								/ MedicineUtils.parseInt(map_yfbz.get("YFBZ")));
				map_tj02.put("ZT", "可发");
//				map_tj02.put(
//						"FYJE",
//						MedicineUtils.formatDouble(
//								2,
//								MedicineUtils.parseDouble(map_tj02.get("FYSL"))
//										* MedicineUtils.parseDouble(map_tj02
//												.get("YPDJ"))));
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
//					Map<String, Object> map_par_kc = new HashMap<String, Object>();
//					map_par_kc.put("ypxh",
//							MedicineUtils.parseLong(map_tj02.get("YPXH")));
//					map_par_kc.put("ypcd",
//							MedicineUtils.parseLong(map_tj02.get("YPCD")));
//					map_par_kc.put("yfsb", yfsb);
//					map_par_kc.put("lsjg",
//							MedicineUtils.parseDouble(map_tj02.get("YPDJ")));
//					List<Map<String, Object>> list_kc_q = dao.doSqlQuery(
//							hql_kc.toString(), map_par_kc);
					Map<String, Object> map_par_compare = new HashMap<String, Object>();
					map_par_compare.put("YPXH", MedicineUtils.parseLong(map_tj02.get("YPXH")));
					map_par_compare.put("YPCD", MedicineUtils.parseLong(map_tj02.get("YPCD")));
					map_par_compare.put("LSJG", MedicineUtils.parseDouble(map_tj02.get("YPDJ")));
					List<Map<String, Object>> list_kc_q=MedicineUtils.getListRecord(list_kcsls, new String[]{"YPXH","YPCD","LSJG"}, map_par_compare, new String[]{"YPXH","YPCD","LSJG"});
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
					map_yz = dao.doLoad(BSPHISEntryNames.ZY_BQYZ_ZYFY,
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
	 * @createDate 2015-8-26
	 * @description 获取药品自负比例
	 * @updateInfo
	 * @param list_fymx
	 * @param list_ykyp_temp
	 * @throws ModelDataOperationException
	 */
	public void getZFBL(List<Map<String,Object>> list_fymx,List<Map<String,Object>> list_ykyp_temp) throws ModelDataOperationException{
		Set<Long> l_zyhs=new HashSet<Long>();//所有需要查询的住院号
		Set<Long> l_ypxhs=new HashSet<Long>();//所有需要查询的药品序号
		for(Map<String,Object> m_temp: list_fymx ){
			l_zyhs.add(MedicineUtils.parseLong(m_temp.get("ZYH")));
			l_ypxhs.add(MedicineUtils.parseLong(m_temp.get("YPXH")));
		}
		StringBuffer hql_brzx = new StringBuffer();// 查询病人性质
		hql_brzx.append("select BRXZ as BRXZ,ZYH as ZYH from ZY_BRRY where ZYH in (:zyhs)");
		StringBuffer hql_ypjy = new StringBuffer();// 从gy_ypjy中查询自付比例
		hql_ypjy.append("select ZFBL as ZFBL,BRXZ as BRXZ,YPXH as YPXH from GY_YPJY  where BRXZ in (:brxzs) and YPXH in (:ypxhs)");
		StringBuffer hql_zfbl = new StringBuffer();// 从GY_ZFBL中查询自付比例
		hql_zfbl.append("select ZFBL as ZFBL,BRXZ as BRXZ,SFXM as SFXM from GY_ZFBL where BRXZ in (:brxzs) and SFXM in (:fyxms)");
		Set<Long> l_fyxms=new HashSet<Long>();//所有需要fyxm
		for(Map<String,Object> m_temp: list_ykyp_temp){
			if(m_temp.get("ZBLB")==null||MedicineUtils.parseLong(m_temp.get("ZBLB"))==0){
				throw new ModelDataOperationException("药品["+m_temp.get("YPMC")+"]未维护账簿类别");
			}
			l_fyxms.add(MedicineUtils.parseLong(m_temp.get("ZBLB")));
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("zyhs", l_zyhs);
		try {
			List<Map<String,Object>> list_brxz=dao.doSqlQuery(hql_brzx.toString(), map_par);//查询出ZYH对应的所有病人性质
			if(list_brxz==null||list_brxz.size()==0){
				throw new ModelDataOperationException("获取病人性质错误");
			}
			Set<Long> l_brxzs=new HashSet<Long>();//所有需要查询的病人性质
			for(Map<String,Object> m_temp: list_brxz){
				l_brxzs.add(MedicineUtils.parseLong(m_temp.get("BRXZ")));
			}
			map_par.clear();
			map_par.put("brxzs", l_brxzs);
			map_par.put("ypxhs", l_ypxhs);
			List<Map<String,Object>> list_ypjy=dao.doSqlQuery(hql_ypjy.toString(), map_par);//查询出gy_ypjy中自付比例
			map_par.remove("ypxhs");
			map_par.put("fyxms", l_fyxms);
			List<Map<String,Object>> list_zfbl=dao.doSqlQuery(hql_zfbl.toString(), map_par);//查询出GY_ZFBL中自付比例
			for(Map<String,Object> m_temp: list_fymx){
				Map<String,Object> map_brxz=MedicineUtils.getRecord(list_brxz, MedicineUtils.parseLong(m_temp.get("ZYH")), "ZYH");
				if(map_brxz==null||map_brxz.size()==0){
					throw new ModelDataOperationException("获取病人性质错误");
				}
				long brxz=MedicineUtils.parseLong(map_brxz.get("BRXZ"));
				Map<String,Object> map_compare_temp=new HashMap<String,Object>();
				map_compare_temp.put("YPXH", MedicineUtils.parseLong(m_temp.get("YPXH")));
				map_compare_temp.put("BRXZ", brxz);
				Map<String,Object> map_zfbl=MedicineUtils.getRecord(list_ypjy, new String[]{"YPXH","BRXZ"}, map_compare_temp, new String[]{"YPXH","BRXZ"});
				if(map_zfbl!=null){
					m_temp.put("ZFBL", MedicineUtils.parseDouble(map_zfbl.get("ZFBL")));
					continue;
				}
				long fyxm=MedicineUtils.parseLong(MedicineUtils.getRecord(list_ykyp_temp, MedicineUtils.parseLong(m_temp.get("YPXH")), "YPXH").get("ZBLB"));
				map_compare_temp.clear();
				map_compare_temp.put("SFXM", fyxm);
				map_compare_temp.put("BRXZ", brxz);
				map_zfbl=MedicineUtils.getRecord(list_zfbl, new String[]{"SFXM","BRXZ"}, map_compare_temp, new String[]{"SFXM","BRXZ"});
				if(map_zfbl==null||map_zfbl.size()==0){
					throw new ModelDataOperationException("查询自负比例失败,SFXM:"+fyxm+",BRXZ:"+brxz);
				}
				m_temp.put("ZFBL", MedicineUtils.parseDouble(map_zfbl.get("ZFBL")));
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询自负比例失败", e);
		}
		
	}
	
	/**
	 * 病区发药按提交单查询
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryFyjltjd(List<?> cnd, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		try {
			if (cnd != null) {
				ret = dao.doList(cnd, null, BSPHISEntryNames.YF_FYJL_LSCX);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "发药记录查询失败", e);
		}
		return ret;
	}
	
	/**
	 * 病区发药按病人查询
	 * @param cnd
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
//	public List<Map<String, Object>> queryFyjlbr(List<?> cnd, Context ctx)
//			throws ModelDataOperationException {
//		UserRoleToken user = UserRoleToken.getCurrent();
//		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
//		try {
//			if (cnd != null) {
//				List<Map<String, Object>> fyjls = dao.doList(cnd, null, BSPHISEntryNames.YF_FYJL_LSCX);
//				if(fyjls.size()>0){
//					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//					for(Map<String,Object> fyjl:fyjls){//根据发药记录JLID查询发药明细
//						List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "JLID", "d", Long.parseLong(fyjl.get("JLID")+""));
//						List<Map<String, Object>> datalist = dao.doList(cnd1, null,
//									BSPHISEntryNames.YF_ZYFYMX);
//						if(datalist!=null && datalist.size()>0){
//							list.addAll(datalist);
//						}
//					}
//					if(list.size()==0){
//						return ret;
//					}
//					List<Long> zyhs = new ArrayList<Long>();
//					for(Map<String, Object> fyjl:list){
//						Long f= Long.parseLong(fyjl.get("ZYH")+"");
//						if(f!=null && !zyhs.contains(f)){
//							zyhs.add(f);
//						}
//					}
//					if(zyhs.size()>0){
//						Long[] strs = new Long[zyhs.size()];
//						for(int j=0;j<zyhs.size();j++){
//							long zyh = zyhs.get(j);
//							strs[j] = zyh;
//						}
//						StringBuffer hql = new StringBuffer();
//						for(int n = 0; n < strs.length; n++) {
//							hql.append("'"+strs[n]+"',");
//					    }
//					    String str = (hql.substring(0,hql.length()-1)).toString();//把最后多余的","去掉
//						StringBuffer sql = new StringBuffer();
//						sql.append("select a.ZYH as ZYH,a.JGID as JGID,a.BRCH as BRCH,a.ZYHM as ZYHM,a.BRXM as BRXM ");
//						sql.append("from ZY_BRRY a where a.JGID=:jgid and a.ZYH in ("+str+") order by a.BRCH");
//						Map<String, Object> map_par = new HashMap<String, Object>();
//						map_par.put("jgid", user.getManageUnit().getId());
//						ret = dao.doSqlQuery(sql.toString(), map_par);
//					}
//				}
//			}
//		} catch (PersistentDataOperationException e) {
//			MedicineUtils.throwsException(logger, "发药记录查询失败", e);
//		}
//		return ret;
//	}

	/**
	 * 病区发药按病人查询
	 * @param
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public void queryFyjlbr(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", JGID);

			StringBuffer sql = new StringBuffer();
			sql.append("select b.ZYH as ZYH,b.JGID as JGID,b.BRCH as BRCH,b.ZYHM as ZYHM,b.BRXM as BRXM from YF_ZYFYMX a " +
					"left join ZY_BRRY b " +
					"on a.zyh=b.zyh " +
					"where a.jgid=:jgid ");

			if (body.get("dateFrom") != null) {
				sql.append(" and a.fyrq>=to_date('").append(body.get("dateFrom")).append("'").append(",'yyyy-mm-dd hh24:mi:ss')");
			}
			if (body.get("dateTo") != null) {
				sql.append(" and a.fyrq<=to_date('").append(body.get("dateTo")).append("'").append(",'yyyy-mm-dd hh24:mi:ss')");
			}
			if (body.get("FYFS") != null) {
				sql.append(" and a.FYLX=:FYFS ");
				parameters.put("FYFS", MedicineUtils.parseLong(body.get("FYFS")));
			}
			if (body.get("FYBQ") != null) {
				sql.append(" and  a.LYBQ=:FYBQ ");
				parameters.put("FYBQ", MedicineUtils.parseLong(body.get("FYBQ")));
			}
			if (body.get("FYGH") != null) {
				sql.append(" and a.QRGH ='")
						.append(body.get("FYGH")).append("'");
			}
			if (body.get("YF") != null) {
				sql.append(" and a.YFSB=:YFSB ");
				parameters.put("YFSB", MedicineUtils.parseLong(body.get("YF")));
			}
			sql.append(" group by b.ZYH,b.JGID ,b.BRCH,b.ZYHM,b.BRXM ");
			List<Map<String, Object>> list = dao.doSqlQuery(sql.toString(), parameters);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "发药记录查询失败", e);
		}

	}
	
	/**
	 * 查询病人发药明细
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void queryBRFYXM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String JGID = user.getManageUnitId();// 用户的机构ID
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", JGID);
			Long bq = 0L;
			if (user.getProperty("wardId") != null
					&& user.getProperty("wardId") != "") {
				bq = Long.parseLong(user.getProperty("wardId") + "");
			}
			int pageSize = 25;
			if (req.containsKey("pageSize")) {
				pageSize = (Integer) req.get("pageSize");
			}
			int first = 0;
			if (req.containsKey("pageNo")) {
				first = (Integer) req.get("pageNo") - 1;
			}
			//add by LIZHI 2018-03-09已退药的不查询
			StringBuffer tyHql = new StringBuffer(
					"select a.TYGL as TYGL ")
					.append(" from  YF_ZYFYMX a, ZY_BRRY b")
					.append(" where a.JGID=:jgid and a.ZYH=b.ZYH")
					.append(" and a.YPSL<0 ");
			
			StringBuffer hql1 = new StringBuffer(
					"select b.BRCH as BRCH,b.BRXM as BRXM,d.YPMC as YPMC,d.BFGG as BFGG,d.BFDW as BFDW,e.CDMC as CDMC,sum(a.YPSL) as YPSL,a.YPDJ as YPDJ,sum(a.LSJE) as LSJE ")
					.append(" from  YF_ZYFYMX a, ZY_BRRY b, YK_TYPK d, YK_CDDZ e ")
					.append(" where a.JGID=:jgid and a.ZYH=b.ZYH")
					.append(" and a.YPXH=d.YPXH")
					.append(" and a.YPCD=e.YPCD and a.YPSL>0 ");
			StringBuffer hql = new StringBuffer();
			if(body.containsKey("JLID")){//根据提交单
				if(body.get("JLID") != null) {
					hql.append(" and a.JLID=:JLID");
//					tyHql.append(" and a.JLID=:JLID");
					parameters.put("JLID",
							MedicineUtils.parseLong(body.get("JLID")));
				}
				if (body.get("dateFrom") != null) {
					tyHql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
							.append(body.get("dateFrom")).append("'");
				}
				if (body.get("dateTo") != null) {
					tyHql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
							.append(body.get("dateTo")).append("'");
				}
				if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
					tyHql.append(" and a.FYFS=:FYFS ");
					parameters.put("FYFS",
							MedicineUtils.parseLong(body.get("FYFS")));
				}
				if (body.get("YF") != null && !body.get("YF").equals("")) {
					tyHql.append(" and a.YFSB=:YF ");
					parameters.put("YF", MedicineUtils.parseLong(body.get("YF")));
				}
				if (body.get("FYBQ") != null && !body.get("FYBQ").equals("")) {
					bq = MedicineUtils.parseLong(body.get("FYBQ"));
					if(bq>0){
						tyHql.append(" and a.LYBQ=:bqsb ");
						parameters.put("bqsb", bq);
					}
				}
				if (body.get("FYGH") != null && !body.get("FYGH").equals("")) {
					tyHql.append(" and a.QRGH=:QRGH ");
					parameters.put("QRGH", body.get("FYGH"));
				}
				
			}else{//根据病人
				if (body.get("dateFrom") != null) {
					hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
							.append(body.get("dateFrom")).append("'");
					tyHql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')>='")
							.append(body.get("dateFrom")).append("'");
				}
				if (body.get("dateTo") != null) {
					hql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
							.append(body.get("dateTo")).append("'");
					tyHql.append(" and to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')<='")
							.append(body.get("dateTo")).append("'");
				}
				if (body.get("FYFS") != null && !body.get("FYFS").equals("")) {
					hql.append(" and a.FYFS=:FYFS ");
					tyHql.append(" and a.FYFS=:FYFS ");
					parameters.put("FYFS",
							MedicineUtils.parseLong(body.get("FYFS")));
				}
				if (body.get("YF") != null && !body.get("YF").equals("")) {
					hql.append(" and a.YFSB=:YF ");
					tyHql.append(" and a.YFSB=:YF ");
					parameters.put("YF", MedicineUtils.parseLong(body.get("YF")));
				}
				long zyh = 0;
				if (body.get("ZYH") != null) {
					zyh = Long.parseLong(body.get("ZYH") + "");
				}
				hql.append(" and b.ZYH=:ZYH ");
				tyHql.append(" and b.ZYH=:ZYH ");
				parameters.put("ZYH", zyh);
				if (body.get("FYBQ") != null && !body.get("FYBQ").equals("")) {
					bq = MedicineUtils.parseLong(body.get("FYBQ"));
					if(bq>0){
						hql.append(" and a.LYBQ=:bqsb ");
						tyHql.append(" and a.LYBQ=:bqsb ");
						parameters.put("bqsb", bq);
					}
				}
				if (body.get("FYGH") != null && !body.get("FYGH").equals("")) {
					hql.append(" and a.QRGH=:QRGH ");
					tyHql.append(" and a.QRGH=:QRGH ");
					parameters.put("QRGH", body.get("FYGH"));
				}
			}
			hql.append(" group by b.BRCH,b.BRXM,d.YPMC,d.BFGG,d.BFDW,e.CDMC,a.YPSL,a.YPDJ,a.LSJE ");
			tyHql.append(" group by a.TYGL ");
			hql1.append(" and a.JLXH not in ("+tyHql.toString()+")");

			int total = Integer.parseInt(dao
					.doSqlQuery(
							"select count(*) as COUNT from (" + hql1.toString() + hql.toString()
									+ ")", parameters).get(0).get("COUNT")
					+ "");
			parameters.put("first", first * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doQuery(hql1.toString() + hql.toString(),
					parameters);
			res.put("pageSize", pageSize);
			res.put("pageNo", first);
			res.put("totalCount", total);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询病人发药明细失败");
		}
	}

}
