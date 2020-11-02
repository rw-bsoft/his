package phis.application.fsb.source;

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

import phis.application.hph.source.HospitalPharmacyBackMedicineModel;
import phis.application.hph.source.HospitalPharmacyDispensingModel;
import phis.application.mds.source.MedicineCommonModel;
import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;

public class FamilySickBedMedicalBackApplicationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(FamilySickBedMedicalBackApplicationModel.class);

	public FamilySickBedMedicalBackApplicationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 保存退药申请记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void saveBackApplication(List<Map<String, Object>> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		Map<String, Object> map_mx = body.get(0);
		StringBuffer hql_delete = new StringBuffer();
		hql_delete
				.append("delete from JC_TYMX")
				.append(" where ZYH=:zyh and JGID=:jgid and (TJBZ = 0 Or TJBZ Is Null) and TYRQ Is Null");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", Long.parseLong(map_mx.get("ZYH") + ""));
		map_par.put("jgid", (String) user.getManageUnitId());
		try {
			dao.doUpdate(hql_delete.toString(), map_par);
			for (int i = 1; i < body.size(); i++) {
				Map<String, Object> map_tymx = body.get(i);
				Map<String, Object> map_tymx_insert = new HashMap<String, Object>();
				for (String key : map_tymx.keySet()) {
					if ("YFSB".equals(key) || "YPXH".equals(key)
							 || "YPCD".equals(key)
							|| "YZID".equals(key) || "ZYH".equals(key)) {
						map_tymx_insert.put(key,MedicineUtils.parseLong(map_tymx.get(key)));
					} else {
						map_tymx_insert.put(key, map_tymx.get(key));
					}
				}
				map_tymx_insert.put("THBZ", 0);
				map_tymx_insert.put("TJBZ", 0);
				map_tymx_insert.put("SQRQ", new Date());
				map_tymx_insert.put("CZGH", user.getUserId() + "");
				map_tymx_insert.put("TYLX", 2);
				map_tymx_insert.put("YPSL",
						-Double.parseDouble(map_tymx.get("YPSL") + ""));
				dao.doSave("create", "phis.application.fsb.schemas.JC_TYMX_TYSQ",
						map_tymx_insert, false);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "退药明细记录保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "退药明细记录保存验证失败", e);
		}

	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 提交退药记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveCommitBackApplication(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			String JCKCGL = ParameterUtil.getParameter(jgid,
					BSPHISSystemArgument.JCKCGL, ctx);
			if ("1".equals(JCKCGL)) {// 直接退费
				StringBuffer hql_commit = new StringBuffer();
				hql_commit
						.append("update JC_TYMX")
						.append(" set TYRQ = :tyrq where ZYH=:zyh and JGID=:jgid and (TJBZ = 0 Or TJBZ Is Null) and TYRQ Is Null");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zyh", MedicineUtils.parseLong(body.get("zyh")));
				map_par.put("jgid", user.getManageUnitId());
				map_par.put("tyrq", new Date());
				dao.doUpdate(hql_commit.toString(), map_par);
			}else if ("2".equals(JCKCGL)) {// 直接退药退费
				Map<String,Object> ret=yfty(MedicineUtils.parseLong(body.get("zyh")),ctx);
				if(MedicineUtils.parseString(ret.get("msg")).length()!=0){
					map_ret.put("code", 9000);
					map_ret.put("msg", MedicineUtils.parseString(map_ret.get("msg")));
				}
			}else if ("3".equals(JCKCGL)) {// 正常流程
				StringBuffer hql_commit = new StringBuffer();
				hql_commit
						.append("update JC_TYMX")
						.append(" set TJBZ = 1 where ZYH=:zyh and JGID=:jgid and (TJBZ = 0 Or TJBZ Is Null) and TYRQ Is Null");
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zyh", MedicineUtils.parseLong(body.get("zyh")));
				map_par.put("jgid", user.getManageUnitId());
				dao.doUpdate(hql_commit.toString(), map_par);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "退药明细记录提交失败", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-28
	 * @description 药房退药
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> yfty(long zyh, Context ctx)
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
		hql_yfbz.append("select YFBZ as YFBZ,YFSB as YFSB,YPXH as YPXH from YF_YPXX where YFSB=:yfsb and YPXH=:ypxh");
		StringBuffer hql_yksb=new StringBuffer();
		hql_yksb.append("select YKSB as YKSB,YPXH as YPXH from YK_YPXX where JGID=:jgid");
		Map<String, Object> map_ret = new HashMap<String, Object>();
		try {
			List<?> cnd=ExpressionProcessor.instance().parseStr("['and',['eq',['$','a.ZYH'],['l',"+zyh+"]],['eq',['$','a.TJBZ'],['i',0]],['isNull',['$','a.TYRQ']]]");
			List<Map<String,Object>> list_tymx=dao.doList(cnd, null, "phis.application.fsb.schemas.JC_TYMX_TYSQ");
			List<Long> yfsbs=new ArrayList<Long>();//存药房识别
			if(list_tymx==null||list_tymx.size()==0){
				return map_ret;
			}
			for(Map<String,Object> map:list_tymx){
				boolean tag=false;
				for(Long yfsb:yfsbs){
					if(yfsb==MedicineUtils.parseLong(map.get("YFSB"))){
						tag=true;
						break;
					}
				}
				if(!tag){
					yfsbs.add(MedicineUtils.parseLong(map.get("YFSB")));
				}
			}
//			Map<String,Object> map_par_yfbz=new HashMap<String,Object>();
//			map_par_yfbz.put("yfsb", yfsbs);
//			List<Map<String,Object>> list_yfbz=dao.doSqlQuery(hql_yfbz.toString(), map_par_yfbz);//退药药房的所有药品包装
			Map<String,Object> map_par_yksb=new HashMap<String,Object>();
			map_par_yksb.put("jgid", jgid);
			List<Map<String,Object>> list_yksb=dao.doQuery(hql_yksb.toString(), map_par_yksb);
			//退药申请直接退药,可能有多个药房的发药记录,循环 按药房退药
			for(Long yfsb:yfsbs){
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
			map_yf_fyjl_data = dao.doSave("create", "phis.application.fsb.schemas.JC_FYJL",
					map_yf_fyjl_data, false);
			for (Map<String, Object> map_tymx : list_tymx) {
				if(MedicineUtils.parseLong(map_tymx.get("YFSB"))!=yfsb){
					continue;
				}
				Map<String, Object> map_par_sfty = new HashMap<String, Object>();
				map_par_sfty.put("jlxh", MedicineUtils.parseLong(map_tymx.get("JLXH")));
				double ktsl = 0;
				double ktsl_cy = 0;
				// 查询药房包装
				Map<String, Object> map_yfyp = new HashMap<String, Object>();
				map_yfyp.put("ypxh", MedicineUtils.parseLong(map_tymx.get("YPXH")));
				map_yfyp.put("yfsb", yfsb);
				Map<String, Object> map_yfbz =dao.doLoad(hql_yfbz.toString(), map_yfyp);
				if (map_yfbz == null) {
					throw new ModelDataOperationException(
							ServiceCode.CODE_DATABASE_ERROR, "无对应的药房药品,请确认是否家床库存管理参数改变导致(不管理保存记录,改成直接发药退药提交会导致)");
				}
				int yfbz = MedicineUtils.parseInt(map_yfbz.get("YFBZ"));
				double tysl =MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_tymx.get("YPSL"))) ;
				// 判断退药数量有没超过计费数量
				Map<String, Object> map_par = new HashMap<String, Object>();
				map_par.put("zyh", MedicineUtils.parseLong(map_tymx.get("ZYH")));
				map_par.put("jgid", jgid);
				map_par.put("ypxh", MedicineUtils.parseLong(map_tymx.get("YPXH")));
				map_par.put("ypcd", MedicineUtils.parseLong(map_tymx.get("YPCD")));
				Map<String, Object> map_ktsl_fymx = dao.doLoad(
						hql_ktsl_fymx.toString(), map_par);
				if (map_ktsl_fymx != null) {
					ktsl = MedicineUtils.parseDouble(map_ktsl_fymx.get("KTSL"));
				}
				if (ktsl + ktsl_cy + tysl < 0) {
					list_jfslcg.add(map_tymx.get("YPMC") + "");
					continue;
				}
				// 增加YF_ZYFYMX负记录
				map_par.remove("zyh");
				map_par.remove("jscs");
				map_par.put("yzxh", MedicineUtils.parseLong(map_tymx.get("YZID")));
				map_par.put("lsjg", MedicineUtils.parseDouble(map_tymx.get("YPJG")));
				Map<String, Object> map_jcfymx_sum = dao.doLoad(
						hql_yffymx_sum.toString(), map_par);
				if (map_jcfymx_sum == null
						|| MedicineUtils.parseDouble(map_jcfymx_sum.get("YPSL")) < tysl) {
					list_fyslcg.add(map_tymx.get("YPMC") + "");
					continue;
				}
				StringBuffer s_cnd = new StringBuffer();
				s_cnd.append("['and',['eq',['$','YPCD'],['d',")
						.append(MedicineUtils.parseLong(map_tymx.get("YPCD")))
						.append("]],['and',['eq',['$','YPDJ'],['d',")
						.append(MedicineUtils.parseDouble(map_tymx.get("YPJG")))
						.append("]],['and',['eq',['$','YPXH'],['d',")
						.append(MedicineUtils.parseLong(map_tymx.get("YPXH")))
						.append("]],['and',['eq',['$','YZXH'],['d',")
						.append(MedicineUtils.parseLong(map_tymx.get("YZID")))
						.append("]],['eq',['$','JGID'],['s',").append(jgid)
						.append("]]]]]]");
				cnd_yffymx = CNDHelper.toListCnd(s_cnd.toString());
				List<Map<String, Object>> list_jcfymx = dao.doList(cnd_yffymx,
						"YPSL desc","phis.application.fsb.schemas.YF_JCFYMX");
				for (Map<String, Object> map_jcfymx : list_jcfymx) {
					if (MedicineUtils.parseDouble(map_jcfymx.get("YPSL")) < 0) {
						continue;
					}
					if (tysl == 0) {
						break;
					}
					// 下面3个价格调价用 故提取出来
					double lsjg = MedicineUtils.parseDouble(map_jcfymx.get("LSJG"));
					double pfjg = MedicineUtils.parseDouble(map_jcfymx.get("PFJG"));
					double jhjg = MedicineUtils.parseDouble(map_jcfymx.get("JHJG"));
					double jdsl = 0;
					if (MedicineUtils.parseDouble(map_jcfymx.get("YPSL")) > -tysl) {
						jdsl = tysl;
					} else {
						jdsl = -MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(map_jcfymx.get("YPSL")));
					}
					map_jcfymx.put("TYGL", map_jcfymx.get("JLXH"));
					map_jcfymx.remove("JLXH");
					map_jcfymx.put("FYLX", 5);
					map_jcfymx.put("YPSL", jdsl);
					map_jcfymx.put("FYRQ", d);
					// map_jcfymx.put("JFID",map_yf_fyjl_data.get("JLID"));
					map_jcfymx.put("TYXH", MedicineUtils.parseLong(map_tymx.get("JLXH")));
					map_jcfymx.put("TJXH", 0);
					map_jcfymx
							.put("FYJE",
									-MedicineUtils.formatDouble(
											4,
											-jdsl
													* MedicineUtils.parseDouble(map_jcfymx
															.get("YPDJ"))));
					map_jcfymx.put("LSJE", -MedicineUtils.formatDouble(4, -jdsl * lsjg));
					map_jcfymx.put("PFJE", -MedicineUtils.formatDouble(4, -jdsl * pfjg));
					map_jcfymx.put("JHJE", -MedicineUtils.formatDouble(4, -jdsl * jhjg));

					// 库存处理
					HospitalPharmacyBackMedicineModel tymodel=new HospitalPharmacyBackMedicineModel(dao);
					tymodel.saveIncreaseInventory(
							MedicineUtils.formatDouble(4,
									-jdsl * MedicineUtils.parseInt(map_jcfymx.get("YFBZ"))
											/ yfbz),
											MedicineUtils.parseLong(map_jcfymx.get("KCSB")),
											MedicineUtils.parseLong(map_jcfymx.get("YPXH")),
											MedicineUtils.parseLong(map_jcfymx.get("YPCD")), jgid, yfsb,
											MedicineUtils.parseLong(map_jcfymx.get("JLID")));
					// 处理调价
					Map<String,Object> map_yksb=MedicineUtils.getRecord(list_yksb, new String[]{"YPXH"}, map_yfyp,new String[]{"YPXH"});
					if(map_yksb!=null){
						savePriceAdjustment(
								MedicineUtils.parseLong(map_yksb.get("YKSB")),
								jgid,
								yfsb,
								map_jcfymx,
								-(jdsl * (MedicineUtils.parseInt(map_jcfymx.get("YFBZ")) / yfbz)),
								userid, yfbz);
					}
					
					tysl = MedicineUtils.formatDouble(4, tysl - jdsl);
					// 新增ZY_FYMX负记录

					Map<String, Object> map_jcyz = new HashMap<String, Object>();
					map_jcyz = dao.doLoad("phis.application.fsb.schemas.JC_BRYZ_ZYFY",
							MedicineUtils.parseLong(map_tymx.get("YZID")));
					int yplx = MedicineUtils.parseInt(map_jcyz.get("YPLX"));
					long ypxh = MedicineUtils.parseLong(map_jcyz.get("YPXH"));
					long fyxm = BSPHISUtil.getfygb(yplx, ypxh, dao, ctx);
					HospitalPharmacyDispensingModel model=new HospitalPharmacyDispensingModel(dao);
					double zfbl = model.getZfbl(MedicineUtils.parseLong(map_tymx.get("ZYH")),
							MedicineUtils.parseLong(map_tymx.get("YPXH")), fyxm);
					Map<String, Object> map_jc_fymx = new HashMap<String, Object>();
					map_jc_fymx.put("JGID", jgid);
					map_jc_fymx.put("ZYH", map_tymx.get("ZYH"));
					map_jc_fymx.put("FYRQ", d);
					map_jc_fymx.put("FYXH", map_tymx.get("YPXH"));
					map_jc_fymx.put("FYMC", map_jcyz.get("YZMC"));
					map_jc_fymx.put("YPCD", map_tymx.get("YPCD"));
					map_jc_fymx.put("FYSL", -MedicineUtils.formatDouble(2, -jdsl));
					map_jc_fymx.put("FYDJ", map_tymx.get("YPJG"));
					map_jc_fymx.put("ZJJE", -MedicineUtils.formatDouble(2, -MedicineUtils.parseDouble(map_jc_fymx.get("FYSL")) * lsjg));
					map_jc_fymx
							.put("ZFJE",
									-MedicineUtils.formatDouble(
											2,
											zfbl
													* (-MedicineUtils.parseDouble(map_jc_fymx
															.get("ZJJE")))));
					map_jc_fymx.put("YSGH", map_jcyz.get("YSGH"));
					map_jc_fymx.put("SRGH", userid);
					map_jc_fymx.put("QRGH", userid);
					map_jc_fymx.put("FYBQ", map_jcyz.get("SRKS"));
					map_jc_fymx.put("FYKS", map_jcyz.get("BRKS"));
					map_jc_fymx.put("ZXKS", map_jcyz.get("ZXKS") == null ? 0
							: map_jcyz.get("ZXKS"));
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
					map_jc_fymx = dao.doSave("create","phis.application.fsb.schemas.JC_FYMX", map_jc_fymx, false);
					map_jcfymx.put("JFID", map_jc_fymx.get("JLXH"));
					map_jcfymx.put("JLID", map_yf_fyjl_data.get("JLID"));
					dao.doSave("create", "phis.application.fsb.schemas.YF_JCFYMX",
							map_jcfymx, false);
				}
				// 更新BQ_TYMX表
				map_tymx.put("TYRQ", new Date());
				map_tymx.put("TJBZ", 0);
				map_tymx.put("JLID", map_yf_fyjl_data.get("JLID"));
				dao.doSave("update", "phis.application.fsb.schemas.JC_TYMX_TY", map_tymx,
						false);
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
	 * @createDate 2015-2-26
	 * @description 退药申请查询已发药记录
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDispensingRecords(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String JCKCGL = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.JCKCGL, ctx);
		StringBuffer hql = new StringBuffer();
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", MedicineUtils.parseLong(body.get("zyh")));
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnitId());
		if(!"1".equals(JCKCGL)){
			hql.append(
					"select a.JGID as JGID,a.YFSB as YFSB,a.YPXH as YPXH,d.YZMC as YPMC,a.YPCD as YPCD,c.CDMC as CDMC,a.YPDJ as YPDJ,sum(YPSL) as YPSL,b.YFMC as YFMC,a.YPGG as YPGG,a.YFDW as YFDW,a.YFBZ as YFBZ,a.YEPB as YEPB,a.ZFBL as ZFBL,a.ZFPB as ZFPB,a.YZXH as YZXH,a.ZYH as ZYH from ")
					.append("YF_JCFYMX a,YF_YFLB b,YK_CDDZ c,JC_BRYZ d,JC_FYMX e where (e.JSCS=0 or e.JSCS is null) and a.JFID=e.JLXH and a.YZXH=d.JLXH and a.YFSB=b.YFSB and a.JGID=b.JGID and a.YPCD =c.YPCD and a.ZYH=:zyh  and a.FYLX!=0 and a.JGID=:jgid group by a.JGID ,a.YFSB,a.YPXH,d.YZMC,a.YPCD,c.CDMC,a.YPDJ,b.YFMC,a.YPGG ,a.YFDW,a.YFBZ  ,a.YEPB ,a.ZFBL,a.ZFPB,a.YZXH,a.ZYH");
		}else{
			hql.append("select a.JGID as JGID,a.FYXH as YPXH,c.YZMC as YPMC,a.YPCD as YPCD,b.CDMC as CDMC,a.FYDJ as YPDJ,sum(a.FYSL) as YPSL,c.YFGG as YPGG,c.YFDW as YFDW,c.YFBZ as YFBZ,a.YEPB as YEPB,a.ZFBL as ZFBL,c.ZFPB as ZFPB,a.YZXH as YZXH,a.ZYH as ZYH from JC_FYMX a, YK_CDDZ b,JC_BRYZ c where (a.JSCS=0 or a.JSCS is null) and a.YZXH=c.JLXH and a.YPCD =b.YPCD and a.JGID=:jgid and a.ZYH=:zyh and a.YPLX!=0 group by a.JGID ,a.FYXH ,c.YZMC ,a.YPCD ,b.CDMC ,a.FYDJ ,c.YFGG,c.YFDW,c.YFBZ ,a.YEPB ,a.ZFBL  ,c.ZFPB,a.YZXH ,a.ZYH ");
		}
		try {
			List<Map<String, Object>> list = dao.doSqlQuery(hql.toString(),
					map_par);
			if (list != null) {
				for (Map<String, Object> map : list) {
					if (Double.parseDouble(map.get("YPSL") + "") > 0) {
						ret.add(map);
					}
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "已发药记录查询失败", e);
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 查询可退数量
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public double queryTurningBackNumber(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String JCKCGL = ParameterUtil.getParameter(jgid,
				BSPHISSystemArgument.JCKCGL, ctx);
		StringBuffer hql_fysl = new StringBuffer();// 统计发药数量
		StringBuffer hql_tysl = new StringBuffer();// 统计退药数量(从退费的数量里面查..不知道为什么..)
		StringBuffer hql_ytjsl = new StringBuffer();// 统计已提交的退药数量
		if(!"1".equals(JCKCGL)){
			hql_fysl.append("select sum(YPSL) as YPSL from YF_JCFYMX")
			.append(" where ZYH=:zyh  and YPSL>0 and FYLX!=0 and JGID=:jgid and YPXH=:ypxh and YPCD=:ypcd  and YZXH=:yzxh and YPDJ=:ypdj");
		}else{
			hql_fysl.append("select sum(FYSL) as YPSL from JC_FYMX")
			.append(" where JGID=:jgid and ZYH=:zyh and FYXH=:ypxh and YPCD=:ypcd and YZXH=:yzxh and FYSL>0 and FYDJ=:ypdj");
		}
		hql_tysl.append("select sum(FYSL) as YPSL from JC_FYMX")
				.append(" where JGID=:jgid and ZYH=:zyh and FYXH=:ypxh and YPCD=:ypcd and YZXH=:yzxh and FYSL<0");
		hql_ytjsl
				.append("select sum(YPSL) as YPSL from JC_TYMX")
				.append(" where TJBZ = 1 and ZYH=:zyh and TYRQ is null and  YPXH=:ypxh and YPCD=:ypcd and YZID=:yzxh and JGID=:jgid");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("zyh", MedicineUtils.parseLong(body.get("zyh")));
		map_par.put("ypxh", MedicineUtils.parseLong(body.get("ypxh")));
		map_par.put("ypcd", MedicineUtils.parseLong(body.get("ypcd")));
		map_par.put("jgid", UserRoleToken.getCurrent().getManageUnitId());
		map_par.put("yzxh", MedicineUtils.parseLong(body.get("yzxh")));
		try {
			map_par.put("ypdj", MedicineUtils.parseDouble(body.get("ypdj")));
			Map<String, Object> map_fysl = dao.doLoad(hql_fysl.toString(),
					map_par);
			if (map_fysl == null || map_fysl.get("YPSL") == null) {
				return 0;
			}
			double fysl = MedicineUtils.parseDouble(map_fysl.get("YPSL"));
			map_par.remove("lybq");
			map_par.remove("ypdj");
			Map<String, Object> map_tysl = dao.doLoad(hql_tysl.toString(),
					map_par);
			double tysl = 0;
			if (map_tysl != null && map_tysl.get("YPSL") != null) {
				tysl = MedicineUtils.parseDouble(map_tysl.get("YPSL"));
			}
			Map<String, Object> map_tjsl = dao.doLoad(hql_ytjsl.toString(),
					map_par);
			double tjsl = 0;
			if (map_tjsl != null && map_tjsl.get("YPSL") != null) {
				tjsl = MedicineUtils.parseDouble(map_tjsl.get("YPSL"));
			}
			return fysl + tysl + tjsl;
		} catch (PersistentDataOperationException e) {
			logger.error("Dispensing records query failed", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "已发药记录查询失败");
		}
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2015-2-26
	 * @description 退药申请查询退药记录
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void querytyRecords(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		List<Object> cnd = (List<Object>) req.get("cnd");
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select a.TYRQ as TYRQ,b.YPMC as YPMC,a.YPGG as YPGG,b.YFDW as YFDW,a.YPSL as YPSL,a.YPJG as YPJG,c.CDMC as CDMC from ")
				.append("JC_TYMX a,YK_TYPK b,YK_CDDZ c,JC_TJ02 d where    d.JLXH=a.JLXH and a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.JLXH=d.JLXH   ");
		if (cnd != null) {
			if (cnd.size() > 0) {
				ExpressionProcessor exp = new ExpressionProcessor();
				String where;
				try {
					where = " and " + exp.toString(cnd);
					hql.append(where);
				} catch (ExpException e) {
					MedicineUtils.throwsException(logger, "退药申请查询退药记录失败", e);
				}
			}
		}
		MedicineCommonModel model = new MedicineCommonModel(dao);
		Map<String,Object> map_par=new HashMap<String,Object>();
		res.putAll(model.getPageInfoRecord(req, map_par, hql.toString(), null));
	}
	
	
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
			StringBuffer hql_tjjl_count = new StringBuffer();//查询是否有调价
			StringBuffer hql_tjjl_sum = new StringBuffer();//查出最后调价记录主键
			map_par.clear();
			if(body.containsKey("KCSB")&&MedicineUtils.parseLong(body.get("KCSB"))!=0){
				hql_tjjl_count.append(" KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum.append("select max(SBXH) as SBXH from YF_TJJL  where KCSB=:kcsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("kcsb", MedicineUtils.parseLong(body.get("KCSB")));
			}else{
				hql_tjjl_count.append(" YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				hql_tjjl_sum
				.append("select max(SBXH) as SBXH from YF_TJJL  where YPXH=:ypxh and YPCD=:ypcd and YFSB=:yfsb and TJDH>0 and TJSL>0 and TJRQ>=:fyrq");
				map_par.put("ypxh", MedicineUtils.parseLong(body.get("YPXH")));
				map_par.put("ypcd", MedicineUtils.parseLong(body.get("YPCD")));
				map_par.put("yfsb", yfsb);
			}
			map_par.put("fyrq", map_fysj.get("FYSJ"));
			long l = dao.doCount("YF_TJJL",
					hql_tjjl_count.toString(), map_par);
			if (l != 0) {
				Map<String, Object> map_tjjl_sbxh = dao.doLoad(
						hql_tjjl_sum.toString(), map_par);
				StringBuffer hql_tjjl = new StringBuffer();
				hql_tjjl.append(
						"select XLSJ as XLSJ,XJHJ as XJHJ,XPFJ as XPFJ from YF_TJJL where SBXH=:sbxh");
				map_par.clear();
				map_par.put("sbxh", MedicineUtils.parseLong(map_tjjl_sbxh.get("SBXH")));
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
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("LSJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原零售价
				map_tjjl.put(
						"YPFJ",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("PFJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原批发价
				map_tjjl.put(
						"YJHJ",
						MedicineUtils.formatDouble(4, MedicineUtils.parseDouble(body.get("JHJG")) * yfbz
								/ MedicineUtils.parseInt(body.get("YFBZ"))));// 原进货价
				map_tjjl.put("CZGH", userid);// 操作工号
				map_tjjl.put("TJWH", "住院退药");
				map_tjjl.put("YLSE",
						MedicineUtils.formatDouble(4, (Double) map_tjjl.get("YLSJ")) * tjsl);// 原零售价
				map_tjjl.put("YPFE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl.get("YPFJ")) * tjsl));// 原批发金额
				map_tjjl.put("YJHE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl.get("YJHJ")) * tjsl));// 原进货金额
				map_tjjl.put(
						"XLSE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XLSJ"))
								* tjsl));// 新零售金额
				map_tjjl.put(
						"XPFE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XPFJ"))
								* tjsl));// 新批发金额
				map_tjjl.put(
						"XJHE",
						MedicineUtils.formatDouble(4, ((Double) map_tjjl_r.get("XJHJ"))
								* tjsl));// 新进货金额
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
}
