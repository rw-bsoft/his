package phis.application.mds.source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.context.Context;
import ctd.validator.ValidateException;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;

/**
 * 药品管理
 * @author caijy
 *
 */
public class MedicineManageModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(MedicineManageModel.class);
	public MedicineManageModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-23
	 * @description 保存公用药品信息
	 * @updateInfo
	 * @param op
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveMedicinesInfomation(String op, Map<String, Object> body,Context ctx) throws ModelDataOperationException {
		StringBuffer yp = new StringBuffer();
		yp.append(MedicineUtils.parseString(body.get("YPMC"))).append(MedicineUtils.parseString(body.get("YPGG"))).append(MedicineUtils.parseString(body.get("YPDW")));
		Map<String,Object> map_repeat=new HashMap<String,Object>();
		map_repeat.put("keyName", "YPMC||YPGG||YPDW");
		map_repeat.put("keyValue", yp.toString());
		map_repeat.put("tableName", "YK_TYPK");
		if (!"create".equals(op)) {
			map_repeat.put("PKName", "YPXH");
			map_repeat.put("PKValue", MedicineUtils.parseLong(body.get("YPXH")));
		}
		//因为前台有去掉JBYWBZ 默认0
		String JBYWBZ=body.get("JBYWBZ")+"";
		if("null".equals(JBYWBZ) || "".equals(JBYWBZ) || JBYWBZ==null){
			body.put("JBYWBZ", 0);
		}

		MedicineCommonModel model =new MedicineCommonModel(dao);
		if(model.repeatVerification(map_repeat)){
			return MedicineUtils.getRetMap("相同名称规格单位的药品已经存在");
		}
		Map<String, Object> req = null;
		long ypxh=0;
		try {
			req = dao.doSave(op,BSPHISEntryNames.YK_TYPK, body, false);// 保存基础信息YK_TYPK
			ypxh =MedicineUtils.parseLong((op.equals("create") ? req.get("YPXH") : body.get("YPXH")));
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败", e);
		}
		updatePharmacyMedicinesUpdateTime(ypxh, null);// 更新药房药品信息的修改时间
		saveAliasInformation(body, ypxh, op);// 保存别名信息
		saveLimitInformation(body, ypxh);// 保存限用信息
		savePriceInformation(body, ypxh);// 保存价格信息
		return MedicineUtils.getRetMap();
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 更新药房药品修改时间(药品公用信息或私用信息修改时)
	 * @updateInfo
	 * @param ypxh
	 * @param jgid
	 * @throws ModelDataOperationException
	 */
	public void updatePharmacyMedicinesUpdateTime(long ypxh, String jgid)throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh", ypxh);
		parameters.put("xgsj", new Date());
		hql.append("update YF_YPXX  set XGSJ=:xgsj where YPXH=:ypxh");
		if (jgid != null) {
			hql.append(" and JGID=:jgid");
			parameters.put("jgid", jgid);
		}
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "更新药房药品更新时间失败", e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 保存别名信息
	 * @updateInfo
	 * @param body
	 * @param ypxh
	 * @param op
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveAliasInformation(Map<String, Object> body, long ypxh,String op) throws ModelDataOperationException {
		try {
			List<Map<String, Object>> list_bm  = (List<Map<String, Object>>) body.get("mdsaliastab");
			if ("update".equals(op)) {
				StringBuffer whereHql = new StringBuffer();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("ypmc", body.get("YPMC"));
				parameters.put("ypxh", ypxh);
				whereHql.append(" YPMC=:ypmc and YPXH=:ypxh ");
				Long l = dao.doCount("YK_YPBM",whereHql.toString(), parameters);//BSPHISEntryNames.YK_YPBM
				if (l < 1) {
					if (list_bm == null) {
						Map<String, Object> map_bm = new HashMap<String, Object>();
						map_bm.put("YPMC", body.get("YPMC"));
						map_bm.put("YPXH", body.get("YPXH"));
						map_bm.put("PYDM", body.get("PYDM"));
						map_bm.put("WBDM", body.get("WBDM"));
						map_bm.put("JXDM", body.get("JXDM"));
						map_bm.put("QTDM", body.get("QTDM"));
						map_bm.put("BMFL", 1);
						map_bm.put("_opStatus", "create");
						dao.doSave("create",BSPHISEntryNames.YK_YPBM, map_bm,false);
						return;
					} else {
						int i = 0;
						for (Map<String, Object> map_bm : list_bm) {
							if (MedicineUtils.parseString(map_bm.get("YPMC")).equals(MedicineUtils.parseString(body.get("YPMC")))) {
								i = 1;
								break;
							}
						}
						if (i == 0) {
							Map<String, Object> map_bm = new HashMap<String, Object>();
							map_bm.put("YPMC", body.get("YPMC"));
							map_bm.put("YPXH", body.get("YPXH"));
							map_bm.put("PYDM", body.get("PYDM"));
							map_bm.put("WBDM", body.get("WBDM"));
							map_bm.put("JXDM", body.get("JXDM"));
							map_bm.put("QTDM", body.get("QTDM"));
							map_bm.put("BMFL", 1);
							map_bm.put("_opStatus", "create");
							list_bm.add(map_bm);
						}
					}
				}
			}
			if (list_bm != null && list_bm.size() > 0) {
				dao.removeByFieldValue("YPXH", ypxh,BSPHISEntryNames.YK_YPBM);
				for (Map<String, Object> alias : list_bm) {
					alias.put("YPXH", ypxh);
					dao.doSave("create",BSPHISEntryNames.YK_YPBM, alias, false);// 保存别名信息BSPHISEntryNames.YK_YPBM  "phis.application.mds.schemas.YK_YPBM" 
				}
			}
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "别名保存验证失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "别名保存验证失败", e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 保存限用信息
	 * @updateInfo
	 * @param body
	 * @param ypxh
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveLimitInformation(Map<String, Object> body, long ypxh)throws ModelDataOperationException {
		List<Map<String, Object>> list_xy = (List<Map<String, Object>>) body.get("mdslimittab");
		if (list_xy != null && list_xy.size() > 0) {
			try {
				dao.removeByFieldValue("YPXH", ypxh, BSPHISEntryNames.GY_YPJY);// deleteGY_YPJY
				for (Map<String, Object> map_xy : list_xy) {
					map_xy.put("YPXH", ypxh);
					map_xy.put("YPXE", "0");
					dao.doSave("create", BSPHISEntryNames.GY_YPJY, map_xy, false);
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "限用保存失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "限用保存失败", e);
			}
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 药品公共信息价格信息保存
	 * @updateInfo
	 * @param body
	 * @param ypxh
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void savePriceInformation(Map<String, Object> body, long ypxh)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_jg = (List<Map<String, Object>>) body.get("mdspricetab");
		if (list_jg != null && list_jg.size() > 0) {
			try {
				StringBuffer hql=new StringBuffer();
				hql.append("update YK_CDXX set GYJJ=:gyjj,GYLJ=:gylj where YPXH=:ypxh and YPCD=:ypcd ");
				for (Map<String, Object> map_jg : list_jg) {
					if ("create".equals(map_jg.get("_opStatus"))) {
						map_jg.put("YPXH", ypxh);
						dao.doSave("create", BSPHISEntryNames.YK_YPCD, map_jg,
								false);
					} else {
						dao.doSave("update",BSPHISEntryNames.YK_YPCD, map_jg,false);// 如果需要以后改成只更新修改过的数据
						Map<String,Object> map_par =new HashMap<String,Object>();
						map_par.put("gyjj", MedicineUtils.parseDouble(map_jg.get("JHJG")) );
						map_par.put("gylj", MedicineUtils.parseDouble(map_jg.get("LSJG")));
						map_par.put("ypxh", MedicineUtils.parseLong(map_jg.get("YPXH")));
						map_par.put("ypcd", MedicineUtils.parseLong(map_jg.get("YPCD")));
						dao.doUpdate(hql.toString(), map_par);//更新私有表的限制价格信息
					}
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "价格保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "价格保存验证失败", e);
			}
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 药品作废和取消作废
	 * @updateInfo
	 * @param body {ypxh,op}
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> invalidMedicines(Map<String, Object> body)throws ModelDataOperationException {
		Long ypxh =MedicineUtils.parseLong(body.get("ypxh"));
		int zfpb =MedicineUtils.parseInt(body.get("zfpb")) == 1 ? 0 : 1;
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh", ypxh);
		parameters.put("zfpb", zfpb);
		hql.append("update YK_TYPK set ZFPB=:zfpb where YPXH=:ypxh");
		try {
			dao.doUpdate(hql.toString(), parameters);
			updatePharmacyMedicinesUpdateTime(ypxh, null);// 更新药房药品信息的修改时间
		} catch (PersistentDataOperationException e) {
			if (zfpb == 0) {
				MedicineUtils.throwsException(logger, "取消作废失败", e);
			} else {
				MedicineUtils.throwsException(logger, "作废失败", e);
			}
		}
		return MedicineUtils.getRetMap();
	}

	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 药品信息导入
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMedicinesPrivateImportInformation(Map<String,Object> body,Context ctx) throws ModelDataOperationException{
		List<Map<String, Object>> yk_infos = (List<Map<String, Object>>)body.get("yk_infos");
		List<Map<String,Object>> list_yps=(List<Map<String,Object>>)body.get("yps");//需要导入的药品信息
		StringBuffer hql_count=new StringBuffer();//查询当前机构是否有该药
		hql_count.append(" JGID=:jgid and YPXH=:ypxh");
		StringBuffer hql_jg_count=new StringBuffer();//查询机构有没有该产地
		hql_jg_count.append("YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		StringBuffer hql_jg_update=new StringBuffer();//如果有产地了 更新公有零价和公有进货价
		hql_jg_update.append("update YK_CDXX set GYJJ=:gyjj,GYLJ=:gylj where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
		try {
			for(Map<String, Object> yk_info : yk_infos) {
				String jgid = (String)yk_info.get("JGID");
				long yksb =MedicineUtils.parseLong(yk_info.get("YKSB"));
				for(Map<String,Object> map_yp:list_yps){
					long ypxh=MedicineUtils.parseLong(map_yp.get("YPXH"));
					int cflx=MedicineUtils.parseInt(map_yp.get("CFLX"));
					Map<String,Object> map_par_count=new HashMap<String,Object>();
					map_par_count.put("ypxh", ypxh);
					map_par_count.put("jgid", jgid);
					long l=dao.doCount("YK_YPXX", hql_count.toString(), map_par_count);
					if(l==0){
						saveMedicinesPrivateInformation(map_yp, jgid, yksb);//新增私有信息
					}else{
						updateMedicinesPrivatePrescriptionCategory(ypxh,cflx,jgid);	//更新处方类别
					}
					List<?> list_cnd_ypxh=CNDHelper.createSimpleCnd("eq", "YPXH", "l", ypxh);
					List<?> list_cnd_zfpb=CNDHelper.createSimpleCnd("ne", "ZFPB", "i", 1);
					List<?> list_cnd_jg=CNDHelper.createArrayCnd("and", list_cnd_ypxh, list_cnd_zfpb);
					List<Map<String,Object>> list_jg=dao.doList(list_cnd_jg, null, BSPHISEntryNames.YK_YPCD);
					for(Map<String,Object> map_jg:list_jg){
						Map<String,Object> map_par_jg=new HashMap<String,Object>();
						map_par_jg.put("ypxh", ypxh);
						map_par_jg.put("jgid", jgid);
						map_par_jg.put("ypcd", MedicineUtils.parseLong(map_jg.get("YPCD")));
						long l_jg=dao.doCount("YK_CDXX", hql_jg_count.toString(), map_par_jg);
						if(l_jg==0){
							//新增私有价格信息
							map_jg.put("JGID", jgid);
							map_jg.put("PFJG", 0);
							map_jg.put("GYJJ", map_jg.get("JHJG"));
							map_jg.put("GYLJ",  map_jg.get("LSJG"));
							dao.doSave("create", BSPHISEntryNames.YK_CDXX, map_jg, false);
						}else{
							//更新私有价格限制信息
							map_par_jg.put("gyjj", map_jg.get("JHJG"));
							map_par_jg.put("gylj", map_jg.get("LSJG"));
							dao.doUpdate(hql_jg_update.toString(), map_par_jg);
						}
					}
				}
			}
			saveYpxxForYf(yk_infos,list_yps);//导入到药房药品信息
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息导入失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "药品信息导入失败", e);
		}

	}
	/**
	 *
	 * @author caijy
	 * @createDate 2014-9-9
	 * @description 机构对应的药房增加调入药品信息(存在的不导入)
	 * @updateInfo
	 * @param yk_infos
	 * @param list_yps
	 * @throws ModelDataOperationException
	 */
	public void saveYpxxForYf(List<Map<String, Object>> yk_infos,List<Map<String,Object>> list_yps)throws ModelDataOperationException{
		StringBuffer hql_yfInfors=new StringBuffer();
		hql_yfInfors.append("select XYQX as XYQX,ZYQX as ZYQX,CYQX as CYQX,BZLB as BZLB,JGID as JGID,YFSB as YFSB from YF_YFLB where JGID in (:jgids)");
		Set<String>  jgids=new HashSet<String>();
		Set<Long>  ypxhs=new HashSet<Long>();
		for(Map<String, Object> yk_info : yk_infos) {
			jgids.add(MedicineUtils.parseString(yk_info.get("JGID")));
		}
		for(Map<String,Object> map_yp:list_yps){
			ypxhs.add(MedicineUtils.parseLong(map_yp.get("YPXH")));
		}
		String date="sysdate";
		StringBuffer hql_yf=new StringBuffer();
		hql_yf.append("insert into YF_YPXX (JGID,YFSB,YPXH,YFGG,YFDW,YFBZ,YFGC,YFDC,YFZF,KWBM,QZCL,DRSJ,XGSJ) ( select :jgid,:yfsb,yp.YPXH,yp.YFGG,yp.YFDW,yp.YFBZ,0,0,0,'',yp.QZCL,"+date+","+date+"  from  YK_TYPK yp  where yp.YPXH in (:ypxhs) and yp.YPXH not in (select YPXH from YF_YPXX yf where  yf.YFSB=:yfsb)  and yp.TYPE in (:yplxs) )");
		StringBuffer hql_bf=new StringBuffer();
		hql_bf.append("insert into YF_YPXX (JGID,YFSB,YPXH,YFGG,YFDW,YFBZ,YFGC,YFDC,YFZF,KWBM,QZCL,DRSJ,XGSJ) ( select :jgid,:yfsb,yp.YPXH,yp.BFGG,yp.BFDW,yp.BFBZ,0,0,0,'',yp.QZCL,"+date+","+date+"  from  YK_TYPK yp  where yp.YPXH in (:ypxhs) and yp.YPXH not in (select YPXH from YF_YPXX yf where  yf.YFSB=:yfsb)  and yp.TYPE in (:yplxs) )");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgids", jgids);
		try {
			List<Map<String,Object>> list_yfs=dao.doQuery(hql_yfInfors.toString(), map_par);
			for(Map<String,Object> map_yfs:list_yfs){
				List<String> list = new ArrayList<String>();
				if ("1".equals(map_yfs.get("XYQX") + "")) {
					list.add("1");
				}
				if ("1".equals(map_yfs.get("ZYQX") + "")) {
					list.add("2");
				}
				if ("1".equals(map_yfs.get("CYQX") + "")) {
					list.add("3");
				}
				if (list.size() == 0) {
					continue;
				}
				Map<String,Object> map_par_yf=new HashMap<String,Object>();
				map_par_yf.put("yfsb", MedicineUtils.parseLong(map_yfs.get("YFSB")));
				map_par_yf.put("jgid", MedicineUtils.parseString(map_yfs.get("JGID")));
				map_par_yf.put("ypxhs", ypxhs);
				map_par_yf.put("yplxs", list);
				if ("1".equals(map_yfs.get("BZLB") + "")) {
					dao.doSqlUpdate(hql_yf.toString(), map_par_yf);
				}else{
					dao.doSqlUpdate(hql_bf.toString(), map_par_yf);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息导入失败", e);
		}

	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 私有信息新增
	 * @updateInfo
	 * @param yp
	 * @param jgid
	 * @param yksb
	 * @throws ModelDataOperationException
	 */
	public void saveMedicinesPrivateInformation(Map<String,Object> yp, String jgid, long yksb) throws ModelDataOperationException{
		int gcsl = 0;// 高储数量
		int dcsl = 0;// 低储数量
		int ykzf = 0;// 药库作废
		try {
			Map<String,Object> map_ykxx=new HashMap<String,Object>();
			map_ykxx.putAll(yp);
			map_ykxx.put("JGID", jgid);
			map_ykxx.put("YKSB", yksb);
			map_ykxx.put("GCSL", gcsl);
			map_ykxx.put("DCSL", dcsl);
			map_ykxx.put("YKZF", ykzf);
			dao.doSave("create", BSPHISEntryNames.YK_YPXX, map_ykxx, false);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "私有信息保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "私有信息保存失败", e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-25
	 * @description 更新处方类别
	 * @updateInfo
	 * @param ypxh
	 * @param cflx
	 * @param jgid
	 * @throws ModelDataOperationException
	 */
	public void updateMedicinesPrivatePrescriptionCategory(long ypxh,int cflx, String jgid) throws ModelDataOperationException{
		StringBuffer hql_update=new StringBuffer();
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("cflx", cflx);
		map_par.put("ypxh", ypxh);
		map_par.put("jgid", jgid);
		hql_update.append("update YK_YPXX set CFLX=:cflx where YPXH=:ypxh and JGID=:jgid");
		try {
			dao.doUpdate(hql_update.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "处方类别保存失败", e);
		}
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description  验证是否有库存和入库(如果有最小包装不能修改)
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> verifiedUsing(Map<String, Object> body,Context ctx) throws ModelDataOperationException {
		if(body.get("YPXH")==null){
			return MedicineUtils.getRetMap("", ServiceCode.CODE_OK);
		}
		long ypxh =MedicineUtils.parseLong(body.get("YPXH"));
		StringBuffer hql_rk_count = new StringBuffer();
		hql_rk_count.append("  YPXH=:ypxh");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh", ypxh);
		try {
			Long l_rk = dao.doCount("YF_RK02",
					hql_rk_count.toString(), parameters);
			if (l_rk > 0) {
				return MedicineUtils.getRetMap("该药品已有入库单,不能修改最小包装");
			}
			Long l_kc = dao.doCount("YF_KCMX", hql_rk_count.toString(), parameters);
			if (l_kc > 0) {
				return MedicineUtils.getRetMap("该药品已有库存,不能修改最小包装");
			}

		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "入库记录查询失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description 用药限制查询
	 * @updateInfo
	 * @param ypxh
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> medicinesLimitList(int ypxh, Context ctx)throws ModelDataOperationException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh", ypxh);
		sql.append(
				"select a.BRXZ as BRXZ,a.XZMC as XZMC,(case  when b.ZFBL is null then null else b.ZFBL*100 end) as ZFBL from  GY_BRXZ  a left outer join GY_YPJY  b on a.BRXZ=b.BRXZ and b.ypxh=:ypxh where a.BRXZ not in (select SJXZ from GY_BRXZ  where BRXZ<>SJXZ) order by a.SJXZ ");
		List<Map<String, Object>> rs=new ArrayList<Map<String,Object>>();
		try {
			rs = dao.doSqlQuery(sql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "用药限制查询失败.", e);
		}
		return rs;
	}
	/**
	 *
	 * @author caijy
	 * @createDate 2013-12-26
	 * @description 药品公共价格维护
	 * @updateInfo
	 * @param
	 * @param op
	 * @throws ModelDataOperationException
	 */
	public void reomovePriceInformation(Map<String, Object> body, int op)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ypxh",MedicineUtils.parseLong(body.get("YPXH")));
		parameters.put("ypcd",MedicineUtils.parseLong(body.get("YPCD")));
		parameters.put("zfpb", op);
		StringBuffer sql = new StringBuffer();
		sql.append("update YK_YPCD set ZFPB=:zfpb where  YPCD=:ypcd and YPXH=:ypxh");
		try {
			dao.doUpdate(sql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			if (op == 0) {
				MedicineUtils.throwsException(logger, "价格恢复失败.", e);
			} else {
				MedicineUtils.throwsException(logger, "价格注销失败.", e);
			}
		}
	}

	/**
	 * @author yanghe
	 * @createDate 2020/9/15
	 * @description 药品数据导入功能
	 * @updateInfo
	 */
	public void saveZslypsjdr(List<Map<String,Object>> body, Context ctx)
			throws ModelDataOperationException {
		for (Map<String, Object> medicineMap : body) {
			Map<String, Object> map_repeat = new HashMap<String, Object>();
			map_repeat.put("keyName", "YPID");
			map_repeat.put("keyValue", MedicineUtils.parseString(medicineMap.get("YPID")));
			map_repeat.put("tableName", "YK_YPCD");
			MedicineCommonModel model = new MedicineCommonModel(dao);
			if (model.repeatVerification(map_repeat)) {
//				throw new ModelDataOperationException(9000,"当前选中药品已调入无需重复调入!");
				continue;
			}
			saveMedicinesIntroduceInfomation("create",medicineMap,ctx);
		}
	}

	//	保存新引入的公用药品信息	yanghe	2020-9-18
	public Map<String,Object> saveMedicinesIntroduceInfomation(String op, Map<String, Object> body,Context ctx) throws ModelDataOperationException {
		StringBuffer yp = new StringBuffer();
		//因为前台有去掉JBYWBZ 默认0
		String JBYWBZ=body.get("JBYWBZ")+"";
		if("null".equals(JBYWBZ) || "".equals(JBYWBZ) || JBYWBZ==null){
			body.put("JBYWBZ", 0);
		}
		MedicineCommonModel model =new MedicineCommonModel(dao);
		Map<String, Object> req = null;
		long ypxh=0;
		try {
			//验证重复
			StringBuffer hql=new StringBuffer();
			hql.append("select count(1) as NUM from YK_TYPK a, YK_YPCD b where a.ypxh = b.ypxh and a.YPMC=:ypmc and a.YPGG=:ypgg and b.YPCD=:ypcd and a.ZXBZ=:zxbz");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("ypmc",MedicineUtils.parseString(body.get("YPMC")));
			map_par.put("ypgg",MedicineUtils.parseString(body.get("YPGG")));
			map_par.put("ypcd",MedicineUtils.parseString(body.get("YPCD")));
			map_par.put("zxbz",MedicineUtils.parseString(body.get("ZXBZ")));
			List<Map<String,Object>> list_ret=dao.doSqlQuery(hql.toString(), map_par);
			if(list_ret!=null&&list_ret.size()>0){
				long l=MedicineUtils.parseLong(list_ret.get(0).get("NUM"));
				if(l>0){
					return MedicineUtils.getRetMap("相同名称规格单位的药品已经存在");
				}
			}
			req = dao.doSave(op,BSPHISEntryNames.YK_TYPK, body, false);// 保存基础信息YK_TYPK
			ypxh =MedicineUtils.parseLong((op.equals("create") ? req.get("YPXH") : body.get("YPXH")));
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "药品信息保存失败", e);
		}
		updatePharmacyMedicinesUpdateTime(ypxh, null);// 更新药房药品信息的修改时间
		saveAliasInformation(body, ypxh, op);// 保存别名信息
		saveLimitInformation(body, ypxh);// 保存限用信息
		savePriceIntroduceInformation(body, ypxh);// 保存价格信息
		Map<String, Object> map_ret = MedicineUtils.getRetMap();
		map_ret.put("YPXH",ypxh);
		return map_ret;
	}

	//	新引入的药品公共信息价格信息保存	yanghe	2020-9-18
	@SuppressWarnings("unchecked")
	public void savePriceIntroduceInformation(Map<String, Object> body, long ypxh)
			throws ModelDataOperationException {
		List<Map<String, Object>> list_jg = (List<Map<String, Object>>) body.get("mdspricetab");
		if (list_jg != null && list_jg.size() > 0) {
			try {
				StringBuffer hql=new StringBuffer();
				hql.append("update YK_CDXX set GYJJ=:gyjj,GYLJ=:gylj where YPXH=:ypxh and YPCD=:ypcd ");
				for (Map<String, Object> map_jg : list_jg) {
					if ("create".equals(map_jg.get("_opStatus"))) {
						map_jg.put("YPXH", ypxh);
						dao.doSave("create", BSPHISEntryNames.YK_YPCD, map_jg, false);
					}
				}
			} catch (ValidateException e) {
				MedicineUtils.throwsException(logger, "价格保存验证失败", e);
			} catch (PersistentDataOperationException e) {
				MedicineUtils.throwsException(logger, "价格保存验证失败", e);
			}
		}
	}
}
