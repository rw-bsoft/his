package phis.application.sto.source;

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
import phis.source.utils.CNDHelper;

public class StorehouseMedicinesConservationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseMedicinesConservationModel.class);

	public StorehouseMedicinesConservationModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-21
	 * @description 养护明细查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryConservationDetail(Map<String, Object> body,String op,Map<String, Object> req)
			throws ModelDataOperationException {
		String yhdh=MedicineUtils.parseString(body.get("YHDH"));
		List<Map<String,Object>> list_ret=null;
		Map<String,Object> map_ret=new HashMap<String,Object>();
		List<?> queryCnd=null;//用于拼音码查询
		if(req.containsKey("cnd")){
			queryCnd=(List<?>)req.get("cnd");
		}
		try {
		if("0".equals(yhdh)){//新增
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 获取JGID
			long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
			StringBuffer hql=new StringBuffer();
			hql.append("select b.PYDM as PYDM,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as CDMC,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,0 as SHSL,2 as TYPE,a.KCSL as KCSL,a.KCSL as YKKC,a.JGID as JGID,a.YPXH as YPXH,a.YPCD as YPCD,a.PFJG as PFJG,a.JHJE as JHJE,a.PFJE as PFJE,a.LSJE as LSJE,a.BZLJ as BZLJ,a.SBXH as KCSB from  YK_KCMX a,YK_TYPK b,YK_CDDZ c,YK_YPXX d where a.YPXH=d.YPXH and a.JGID=d.JGID and d.YKSB=:xtsb and  a.YPXH=b.YPXH and a.JGID=:jgid and a.YPCD=c.YPCD and a.TYPE=1 ");
			if(queryCnd!=null){
				hql.append(" and ").append(ExpressionProcessor.instance().toString(queryCnd));
			}
			hql.append(" order by a.SBXH");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("jgid", jgid);
			map_par.put("xtsb", yksb);
			MedicineCommonModel model=new MedicineCommonModel(dao);
			return model.getPageInfoRecord(req, map_par, hql.toString(), "phis.application.sto.schemas.YK_YH02");
			//return SchemaUtil.setDictionaryMassageForList(dao.doQuery(hql.toString(), map_par), "phis.application.sto.schemas.YK_YH02") ;
		}
		long xtsb=MedicineUtils.parseLong(body.get("XTSB"));
		List<?> cnd=CNDHelper.createArrayCnd("and", CNDHelper.createSimpleCnd("eq", "YHDH", "s", yhdh), CNDHelper.createSimpleCnd("eq", "XTSB", "l", xtsb));
		if(queryCnd!=null){
			cnd=CNDHelper.createArrayCnd("and",cnd,queryCnd);
		}
		list_ret=dao.doList(cnd, null, "phis.application.sto.schemas.YK_YH02");
		if("read".equals(op)){//如果是查看,直接显示合格数量
			//return list_ret;
			return dao.doList(cnd, null, "phis.application.sto.schemas.YK_YH02", (Integer)req.get("pageNo"), (Integer)req.get("pageSize"), null);
		}
		//非查看 实时更新库存数量
		StringBuffer hql_kcsl=new StringBuffer();//实时更新库存数量
		hql_kcsl.append("select KCSL as KCSL from YK_KCMX where SBXH=:kcsb");
		StringBuffer hql_yh02_delete=new StringBuffer();//删除因为库存变为0的yh02记录
		hql_yh02_delete.append("delete from YK_YH02 where SBXH=:sbxh");
		List<Map<String,Object>> list_temp=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map_yh02:list_ret){
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("kcsb", MedicineUtils.parseLong(map_yh02.get("KCSB")));
			Map<String,Object> map_kcsl=dao.doLoad(hql_kcsl.toString(), map_par);
			if(map_kcsl==null||map_kcsl.size()==0){
				list_temp.add(map_yh02);
				Map<String,Object> map_par_yh02=new HashMap<String,Object>();
				map_par_yh02.put("sbxh", MedicineUtils.parseLong(map_yh02.get("SBXH")));
				dao.doSqlUpdate(hql_yh02_delete.toString(), map_par_yh02);
			}else{
				if(MedicineUtils.parseDouble(map_kcsl.get("KCSL"))<MedicineUtils.parseDouble(map_yh02.get("SHSL"))){
					map_yh02.put("SHSL", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
					map_yh02.put("KCSL", 0);
					map_yh02.put("YKKC", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
				}else{
					map_yh02.put("KCSL", MedicineUtils.parseDouble(map_kcsl.get("KCSL"))-MedicineUtils.parseDouble(map_yh02.get("SHSL")));
					map_yh02.put("YKKC", MedicineUtils.parseDouble(map_kcsl.get("KCSL")));
				}
			}
		}
		for(Map<String,Object> map_yh02:list_temp){
			list_ret.remove(map_yh02);
		}
		map_ret.put("totalCount", list_ret.size());
		List<Map<String,Object>> list_ret_body=new ArrayList<Map<String,Object>>();
		Map<String,Object> map_pageinfo=new HashMap<String,Object>();
		MedicineUtils.getPageInfo(req, map_pageinfo);
		if(map_pageinfo.size() >0){
		for(int i=(Integer)map_pageinfo.get("first");i<(Integer)map_pageinfo.get("first")+(Integer)map_pageinfo.get("max");i++){
			if(list_ret.size()>i){
				list_ret_body.add(list_ret.get(i));
			}
		}
		}else{
			list_ret_body.addAll(list_ret);
		}
		map_ret.put("body", list_ret_body);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护明细查询失败", e);
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "养护明细查询失败,查询条件输入错误", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-24
	 * @description 养护单保存
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> saveConservation(Map<String, Object> body,String op)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		Map<String,Object> yk_yh01=(Map<String,Object>)body.get("YK_YH01");
		List<Map<String,Object>> list_yh02=(List<Map<String,Object>>)body.get("YK_YH02");//界面上修改的明细
		try {
		if("create".equals(op)){
			String yhdh="1";
			StringBuffer hql_yhdh=new StringBuffer();//查询当前药库的最大养护单号
			hql_yhdh.append("select max(YHDH) as YHDH from YK_YH01 where XTSB=:xtsb");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("xtsb", yksb);
			List<Map<String,Object>> list_yhdh=dao.doSqlQuery(hql_yhdh.toString(), map_par);
			if(list_yhdh!=null&&list_yhdh.size()>0&&list_yhdh.get(0)!=null&&list_yhdh.get(0).size()>0&&list_yhdh.get(0).get("YHDH")!=null&&!"".equals(list_yhdh.get(0).get("YHDH")+"")){
				yhdh=(MedicineUtils.parseLong(list_yhdh.get(0).get("YHDH"))+1)+"";
			}
			yk_yh01.put("YHDH", yhdh);
			yk_yh01.put("XTSB", yksb);
			yk_yh01.put("JGID", jgid);
			yk_yh01.put("YPLB", 0);
			yk_yh01.put("KWLB", 0);
			yk_yh01.putAll(dao.doSave("create", BSPHISEntryNames.YK_YH01, yk_yh01, false));
			StringBuffer hql_all=new StringBuffer();//查询所有库存信息
			hql_all.append("select b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,c.CDMC as CDMC,a.LSJG as LSJG,a.JHJG as JHJG,a.YPPH as YPPH,a.YPXQ as YPXQ,0 as SHSL,2 as TYPE,a.KCSL as KCSL,a.KCSL as YKKC,a.JGID as JGID,a.YPXH as YPXH,a.YPCD as YPCD,a.PFJG as PFJG,a.JHJE as JHJE,a.PFJE as PFJE,a.LSJE as LSJE,a.BZLJ as BZLJ,a.SBXH as KCSB from  YK_KCMX a,YK_TYPK b,YK_CDDZ c,YK_YPXX d where a.YPXH=d.YPXH and a.JGID=d.JGID and d.YKSB=:xtsb and  a.YPXH=b.YPXH and a.JGID=:jgid and a.YPCD=c.YPCD and a.TYPE=1");
			Map<String,Object> map_par_all=new HashMap<String,Object>();
			map_par_all.put("jgid", jgid);
			map_par_all.put("xtsb", yksb);
			List<Map<String,Object>> list_all=dao.doQuery(hql_all.toString(), map_par_all);
			for(Map<String,Object> map_yh02_all:list_all){
				boolean b=false;
				for(Map<String,Object> map_yh02:list_yh02){
					if(MedicineUtils.parseLong(map_yh02_all.get("KCSB"))==MedicineUtils.parseLong(map_yh02.get("KCSB"))){
						map_yh02.put("YHDH", yhdh);
						map_yh02.put("XTSB", yksb);
						map_yh02.put("JGID", jgid);
						map_yh02.put("PFJG", 0);
						map_yh02.put("JHJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("JHJG")));
						map_yh02.put("PFJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("PFJG")));
						map_yh02.put("LSJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("LSJG")));
						dao.doSave("create", BSPHISEntryNames.YK_YH02, map_yh02, false);
						b=true;
						break;
					}
				}
				if(!b){
					map_yh02_all.put("YHDH", yhdh);
					map_yh02_all.put("XTSB", yksb);
					map_yh02_all.put("JGID", jgid);
					map_yh02_all.put("PFJG", 0);
					dao.doSave("create", BSPHISEntryNames.YK_YH02, map_yh02_all, false);
				}	
			}
			
		}else{
			StringBuffer hql_yqr=new StringBuffer();//查询是否确认,已确认不能保存
			hql_yqr.append(" YHDH=:yhdh and XTSB=:xtsb and YSGH is not null");
			Map<String,Object> map_par_yqr=new HashMap<String,Object>();
			map_par_yqr.put("yhdh", MedicineUtils.parseString(yk_yh01.get("YHDH")));
			map_par_yqr.put("xtsb", MedicineUtils.parseLong(yk_yh01.get("XTSB")));
			long l=dao.doCount("YK_YH01", hql_yqr.toString(), map_par_yqr);
			if(l>0){
				return MedicineUtils.getRetMap("养护单已确认,不能保存!");
			}
			dao.doSave("update", BSPHISEntryNames.YK_YH01, yk_yh01, false);
			for(Map<String,Object> map_yh02:list_yh02){
				map_yh02.put("JHJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("JHJG")));
				map_yh02.put("PFJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("PFJG")));
				map_yh02.put("LSJE", MedicineUtils.simpleMultiply(4,map_yh02.get("KCSL") , map_yh02.get("LSJG")));
				dao.doSave("update", BSPHISEntryNames.YK_YH02, map_yh02, false);
			}
		}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "养护单保存失败", e);
		}
		
		Map<String,Object> reMap = MedicineUtils.getRetMap();
		reMap.put("body", yk_yh01);
		return reMap;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-24
	 * @description 养护单确认
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> saveConservationCommit(Map<String, Object> body, Context ctx)throws ModelDataOperationException {
		List<Map<String,Object>> list_yh02=(List<Map<String,Object>>)body.get("YK_YH02");
		Map<String,Object> yk_yh01=(Map<String,Object>)body.get("YK_YH01");
		Map<String,Object> map_saveret=saveConservation(body,"update");//确认前先保存数据
		if(MedicineUtils.parseInt(map_saveret.get("code"))>300){
			return map_saveret;
		}
		StringBuffer hql_kc_update=new StringBuffer();//更新库存  注:由于库存总量不变 所以产地表不更新
		hql_kc_update.append("update YK_KCMX set KCSL=KCSL-:kcsl,PFJE=PFJE-:pfje,LSJE=LSJE-:lsje,JHJE=JHJE-:jhje where SBXH=:kcsb");
		StringBuffer hql_kcsl=new StringBuffer();//查询库存数量,用于判断当前库存是否小于损坏数量
		hql_kcsl.append("select a.KCSL as KCSL ,b.YPMC as YPMC,c.CDMC as CDMC from YK_KCMX a,YK_TYPK b,YK_CDDZ c where a.YPXH=b.YPXH and a.YPCD=c.YPCD and a.SBXH=:kcsb");
		StringBuffer hql_yh02_delete=new StringBuffer();//删除损坏数量为0的养护记录
		hql_yh02_delete.append("delete from YK_YH02 where SHSL=0 and XTSB=:xtsb and YHDH=:yhdh");
		StringBuffer hql_kcls_insert=new StringBuffer();//往历史表插数据
		hql_kcls_insert.append("insert into YK_KCMX_LS  select * from YK_KCMX where SBXH=:sbxh ");
		StringBuffer hql_kc_delete=new StringBuffer();
		hql_kc_delete.append("delete from YK_KCMX where SBXH=:sbxh");
		StorehouseCheckInOutModel model=new StorehouseCheckInOutModel(dao);
		try {
		for(Map<String,Object> map_yh02:list_yh02){
			if(MedicineUtils.parseDouble(map_yh02.get("SHSL"))!=0){//有破损记录
				long kcsb=MedicineUtils.parseLong(map_yh02.get("KCSB"));
				Map<String,Object> map_par_kcsl=new HashMap<String,Object>();
				map_par_kcsl.put("kcsb", kcsb);
				Map<String,Object> map_kcsl=dao.doLoad(hql_kcsl.toString(), map_par_kcsl);
				if(MedicineUtils.parseDouble(map_yh02.get("SHSL"))>MedicineUtils.parseDouble(map_kcsl.get("KCSL"))){
					throw new ModelDataOperationException("药品["+map_kcsl.get("YPMC")+"]产地["+map_kcsl.get("CDMC")+"]由于库存变动导致库存数量少于损坏数量,请重新打开!");
				}
				Map<String,Object> map_sh=model.saveKc(MedicineUtils.parseDouble(map_yh02.get("SHSL")), map_yh02, new Date(), MedicineUtils.parseInt(map_yh02.get("TYPE")), ctx);//保存损坏库存
				if(MedicineUtils.parseDouble(map_yh02.get("KCSL"))==0){//清空0库存记录
					Map<String,Object> map_par_kc=new HashMap<String,Object>();
					map_par_kc.put("sbxh", MedicineUtils.parseLong(map_yh02.get("KCSB")));
					dao.doSqlUpdate(hql_kcls_insert.toString(), map_par_kc);
					dao.doSqlUpdate(hql_kc_delete.toString(), map_par_kc);
				}
				Map<String,Object> map_par_kcupdate=new HashMap<String,Object>();
				map_par_kcupdate.put("kcsl", MedicineUtils.parseDouble(map_yh02.get("SHSL")));
				map_par_kcupdate.put("pfje", MedicineUtils.simpleMultiply(4, map_yh02.get("SHSL"), map_yh02.get("PFJG")));
				map_par_kcupdate.put("lsje", MedicineUtils.simpleMultiply(4, map_yh02.get("SHSL"), map_yh02.get("LSJG")));
				map_par_kcupdate.put("jhje", MedicineUtils.simpleMultiply(4, map_yh02.get("SHSL"), map_yh02.get("JHJG")));
				map_par_kcupdate.put("kcsb", kcsb);
				dao.doUpdate(hql_kc_update.toString(), map_par_kcupdate);//修改合格库存
				model.saveKcFl(kcsb, kcsb, map_yh02, MedicineUtils.parseDouble(map_yh02.get("KCSL")));//保存合格库存分裂
				model.saveKcFl(kcsb, MedicineUtils.parseLong(map_sh.get("SBXH")), map_yh02, MedicineUtils.parseDouble(map_yh02.get("SHSL")));//保存损坏的库存分裂
			}
		}
		yk_yh01.put("YSGH", UserRoleToken.getCurrent().getId());
		yk_yh01.put("ZXRQ", new Date());
		dao.doSave("update", BSPHISEntryNames.YK_YH01, yk_yh01, false);
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", MedicineUtils.parseLong(yk_yh01.get("XTSB")));
		map_par.put("yhdh", MedicineUtils.parseString(yk_yh01.get("YHDH")));
		dao.doSqlUpdate(hql_yh02_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单确认失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "养护单确认失败", e);
		}
		return MedicineUtils.getRetMap();
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-24
	 * @description 删除养护单
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> removeConservation(Map<String, Object> body)throws ModelDataOperationException {
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("yhdh", MedicineUtils.parseString(body.get("YHDH")));
		map_par.put("xtsb", MedicineUtils.parseLong(body.get("XTSB")));
		StringBuffer hql_isDelete=new StringBuffer();//查询是否已经删除
		hql_isDelete.append(" XTSB=:xtsb and YHDH=:yhdh");
		StringBuffer hql_isCommit=new StringBuffer();//查询是否已经确认
		hql_isCommit.append(" XTSB=:xtsb and YHDH=:yhdh and YSGH is not null");
		StringBuffer hql_yh01_delete=new StringBuffer();//删除养护01
		StringBuffer hql_yh02_delete=new StringBuffer();//删除养护02
		hql_yh01_delete.append("delete from YK_YH01 where XTSB=:xtsb and YHDH=:yhdh");
		hql_yh02_delete.append("delete from YK_YH02 where XTSB=:xtsb and YHDH=:yhdh");
		try {
			long l=dao.doCount("YK_YH01", hql_isDelete.toString(), map_par);
			if(l==0){
				return MedicineUtils.getRetMap("养护单已删除,已刷新页面!");
			}
			l=dao.doCount("YK_YH01", hql_isCommit.toString(), map_par);
			if(l>0){
				return MedicineUtils.getRetMap("养护单已确认,已刷新页面!");
			}
			dao.doSqlUpdate(hql_yh02_delete.toString(), map_par);
			dao.doSqlUpdate(hql_yh01_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "养护单删除失败", e);
		}
		return MedicineUtils.getRetMap();
	}
}
