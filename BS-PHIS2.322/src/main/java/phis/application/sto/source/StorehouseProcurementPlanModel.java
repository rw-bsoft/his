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
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;
import phis.source.utils.SchemaUtil;

public class StorehouseProcurementPlanModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(StorehouseProcurementPlanModel.class);

	public StorehouseProcurementPlanModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-26
	 * @description 药库计划单自动计划
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryZdjh() throws ModelDataOperationException {
		List<Map<String, Object>> ret= new ArrayList<Map<String, Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		StringBuffer hql=new StringBuffer();
		hql.append("select f.YPMC as YPMC,f.YPGG as YPGG,f.YPDW as YPDW, f.CDMC as CDMC, f.GJJG as GJJG,nvl(e.KCSL,0) as KCSL, f.GCSL-nvl(e.KCSL,0) as JHSL,round(( f.GCSL-nvl(e.KCSL,0))*f.GJJG,2) as GJJE,0 as SPSL,0 as CGSL,f.GCSL as GCSL,f.DCSL as DCSL, f.XTSB as XTSB, f.YPXH as YPXH,f.YPCD as YPCD from (select a.YPMC as YPMC,a.YPGG as YPGG, a.YPDW as YPDW, d.CDMC as CDMC, c.LSJG as GJJG, 0      as JHSL, 0      as GJJE,  0      as SPSL,  0      as CGSL, b.GCSL as GCSL, b.DCSL as DCSL,  b.YKSB as XTSB, a.YPXH as YPXH, c.YPCD as YPCD from YK_TYPK a, YK_YPXX b, YK_CDXX c, YK_CDDZ d  where a.YPXH = b.YPXH and b.JGID = c.JGID  and b.YPXH = c.YPXH and c.YPCD = d.YPCD  and b.YKSB = :yksb) f left outer join (select sum(KCSL) as KCSL, YPXH as YPXH, YPCD as YPCD from YK_KCMX where JGID = :jgid group by YPXH, YPCD) e on e.YPCD = f.YPCD and f.YPXH = e.YPXH where nvl(KCSL,0) <f.DCSL");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", jgid);
		map_par.put("yksb", yksb);
		try {
			List<Map<String, Object>> records=dao.doSqlQuery(hql.toString(), map_par);
			if(records==null){
				return records;
			}
			//库存有多个产地的相同药品,随机取一个产地(zww提的缺陷2.4.4bug114)
			for(Map<String,Object> map:records){
				boolean tag=false;
				for(Map<String,Object> m:ret){
					if(MedicineUtils.parseLong(map.get("YPXH"))==MedicineUtils.parseLong(m.get("YPXH"))){
						tag=true;
						break;
					}
				}
				if(!tag){
					ret.add(map);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自动计划查询失败", e);
		}
		
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-29
	 * @description 药库采购计划单保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveCgjh(Map<String,Object> body) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userid = user.getUserId();// 用户ID
		Calendar a = Calendar.getInstance();
		int year=a.get(Calendar.YEAR);
		Map<String,Object> jh01=(Map<String,Object>)body.get("d01");
		List<Map<String,Object>> jh02=(List<Map<String,Object>>)body.get("d02");
		String op=MedicineUtils.parseString(body.get("op"));
		int jhdh=MedicineUtils.parseInt((year-2000)+"0001");
		try {
		if("create".equals(op)){
			StringBuffer hql_jhdh=new StringBuffer();
			hql_jhdh.append("select max(JHDH) as JHDH from YK_JH01 where XTSB=:yksb");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yksb", yksb);
			List<Map<String,Object>> l=dao.doSqlQuery(hql_jhdh.toString(),map_par);
			if(l!=null&&l.size()>0&&l.get(0)!=null){
				Map<String,Object> map_jhdh=l.get(0);
				if(map_jhdh.size()>0&&map_jhdh.get("JHDH")!=null){
					jhdh=MedicineUtils.parseInt(map_jhdh.get("JHDH"))+1;
					if(jhdh>MedicineUtils.parseInt((year-2000)+"9999")){
						throw new ModelDataOperationException("当年计划单数量已达上限");
					}
				}
			}
			jh01.put("JHDH", jhdh);
			jh01.put("XTSB", yksb);
			jh01.put("JGID", jgid);
		}else if("update".equals(op)){
			jhdh=MedicineUtils.parseInt(jh01.get("JHDH"));
			StringBuffer hql_delete =new StringBuffer();
			hql_delete.append("delete from YK_JH02 where XTSB=:yksb and JHDH=:jhdh");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("yksb", yksb);
			map_par.put("jhdh", jhdh);
			dao.doUpdate(hql_delete.toString(), map_par);
		}else if("sp".equals(op)){
			jhdh=MedicineUtils.parseInt(jh01.get("JHDH"));
			jh01.put("SPGH", userid);
			jh01.put("SPRQ", new Date());
		}
		dao.doSave("sp".equals(op)?"update":op, "phis.application.sto.schemas.YK_JH01", jh01, false);
		for(Map<String,Object> map_jh02:jh02){
			map_jh02.put("JHDH", jhdh);
			map_jh02.put("XTSB", yksb);
			map_jh02.put("JGID", jgid);
			dao.doSave("sp".equals(op)?"update":"create", "phis.application.sto.schemas.YK_JH02", map_jh02, false);
		}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单保存失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "计划单保存失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-30
	 * @description 查询计划单
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryJHDS(Map<String, Object> req) throws ModelDataOperationException {
		Map<String, Object> ret= new HashMap<String,Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		try {
			List<?> cnds=CNDHelper.toListCnd("['eq',['$','a.XTSB'],['l',"+yksb+"]]");
			//List<Map<String,Object>> list_jh01=dao.doList(cnds, null, "phis.application.sto.schemas.YK_JH01");
			int pageSize = MedicineUtils.parseInt(req.get("pageSize"));
			int pageNo = MedicineUtils.parseInt(req.get("pageNo"));
			ret=dao.doList(cnds, null, "phis.application.sto.schemas.YK_JH01",pageNo, pageSize, null);
			if(MedicineUtils.parseInt(ret.get("totalCount"))==0){
				return ret;
			}
			List<Map<String,Object>> list_jh01=(List<Map<String,Object>>)ret.get("body");
			StringBuffer hql_hjje=new StringBuffer();
			hql_hjje.append("select sum(GJJE) as HJJE ,JHDH as JHDH,XTSB as XTSB from YK_JH02 where XTSB=:xtsb group by JHDH,XTSB ");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("xtsb", yksb);
			List<Map<String,Object>> list_jh02=dao.doSqlQuery(hql_hjje.toString(), map_par);
			for(Map<String,Object> map_jh01:list_jh01){
				if(map_jh01.get("SPGH")!=null){
					map_jh01.put("SP", 1);
				}else{
					map_jh01.put("SP", 0);
				}
				if(map_jh01.get("ZXGH")!=null){
					map_jh01.put("ZX", 1);
				}else{
					map_jh01.put("ZX", 0);
				}
				for(Map<String,Object> map_jh02:list_jh02){
					if(MedicineUtils.compareMaps(map_jh01, new String[]{"XTSB","JHDH"}, map_jh02, new String[]{"XTSB","JHDH"})){
						map_jh01.put("CKJE", MedicineUtils.parseDouble(map_jh02.get("HJJE")));
						break;
					}
				}
			}
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "计划单查询失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单查询失败", e);
		}
		return ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-30
	 * @description 计划单明细form表单数据查询
	 * @updateInfo
	 * @param sbxh
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> queryJhdForm(long sbxh)throws ModelDataOperationException {
		Map<String,Object> ret=new HashMap<String,Object>();
		try {
			ret=dao.doLoad("phis.application.sto.schemas.YK_JH01", sbxh);
			Map<String,Object> map_dwmc=dao.doLoad("phis.application.mds.schemas.YK_JHDW", MedicineUtils.parseLong(ret.get("DWXH")));
			ret.put("DWMC", map_dwmc.get("DWMC"));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单表单查询失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-30
	 * @description 计划单明细list数据查询
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryJhdList(List<?> cnd,String op) throws ModelDataOperationException {
		List<Map<String, Object>> list_jh02= new ArrayList<Map<String, Object>>();
		try {
			 list_jh02=dao.doList(cnd, null, "phis.application.sto.schemas.YK_JH02");
			if(list_jh02==null||list_jh02.size()==0){
				return list_jh02;
			}
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			StringBuffer hql=new StringBuffer();
			hql.append("select sum(KCSL) as KCSL from YK_KCMX where YPXH=:ypxh and YPCD=:ypcd and JGID=:jgid");
			for(Map<String,Object> map_jh02:list_jh02){
				if("sp".equals(op)){
					map_jh02.put("SPSL", MedicineUtils.parseDouble(map_jh02.get("JHSL")));
				}
				Map<String,Object> map_par=new HashMap<String,Object>();
				map_par.put("ypxh", MedicineUtils.parseLong(map_jh02.get("YPXH")));
				map_par.put("ypcd", MedicineUtils.parseLong(map_jh02.get("YPCD")));
				map_par.put("jgid", jgid);
				List<Map<String,Object>> l=dao.doSqlQuery(hql.toString(), map_par);
				if(l==null||l.size()==0||l.get(0)==null||l.get(0).size()==0||l.get(0).get("KCSL")==null){
					map_jh02.put("KCSL", 0);
				}else{
					map_jh02.put("KCSL", MedicineUtils.parseDouble(l.get(0).get("KCSL")));
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单表单查询失败", e);
		}
		return list_jh02;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-12-31
	 * @description 计划单删除
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void removeStorehouseProcurementPlan(Map<String,Object> body ) throws ModelDataOperationException{
		long xtsb=MedicineUtils.parseLong(body.get("XTSB"));
		int jhdh=MedicineUtils.parseInt(body.get("JHDH"));
		StringBuffer hql_jh01_delete =new StringBuffer();
		hql_jh01_delete.append("delete from YK_JH01 where XTSB=:xtsb and JHDH=:jhdh");
		StringBuffer hql_jh02_delete =new StringBuffer();
		hql_jh02_delete.append("delete from YK_JH02 where XTSB=:xtsb and JHDH=:jhdh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", xtsb);
		map_par.put("jhdh", jhdh);
		try {
			dao.doUpdate(hql_jh02_delete.toString(), map_par);
			dao.doUpdate(hql_jh01_delete.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单删除失败", e);
		}
		
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-4
	 * @description 采购入库-计划单引入
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryStorehouseProcurementPlanSelectRecord()throws ModelDataOperationException{
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		StringBuffer hql=new StringBuffer();
		hql.append("select a.SBXH as SBXH,a.JGID as JGID,a.XTSB as XTSB,a.JHDH as JHDH,a.BZRQ as BZRQ,'审批' as ZT,a.BZGH as BZGH,a.JHBZ as JHBZ,a.DWXH as DWXH,c.DWMC as DWMC from YK_JH01 a,YK_JH02 b,YK_JHDW C where a.SPGH is not null and a.ZXGH is null and a.XTSB=:xtsb and  a.XTSB=b.XTSB and a.JHDH=b.JHDH and a.DWXH=c.DWXH and b.SBXH not in (select JHSBXH as JHSBXH from YK_RK02 where JHSBXH is not null and XTSB=:xtsb)");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("xtsb", yksb);
		try {
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
			if(list_ret!=null&&list_ret.size()>0){
				SchemaUtil.setDictionaryMassageForList(list_ret, "phis.application.sto.schemas.YK_JH01_YR");
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-1-4
	 * @description 采购入库-计划单明细查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryStorehouseProcurementPlanDetailRecord(List<String> body)throws ModelDataOperationException{
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yksb = MedicineUtils.parseLong(user.getProperty("storehouseId"));// 用户的药库识别
		List<?> cnd=CNDHelper.createArrayCnd("and", CNDHelper.createInCnd("a.JHDH", body), CNDHelper.createSimpleCnd("eq", "a.XTSB", "l", yksb));
		try {
			list_ret=dao.doList(cnd, null, "phis.application.sto.schemas.YK_JH02");
			if(list_ret!=null&&list_ret.size()>0){
				for(Map<String,Object> map_jh02:list_ret){
					map_jh02.put("JHSBXH", MedicineUtils.parseLong(map_jh02.get("SBXH")));
					map_jh02.remove("SBXH");
					map_jh02.put("RKSL", MedicineUtils.parseDouble(map_jh02.get("SPSL")));
					map_jh02.put("JHJG", MedicineUtils.parseDouble(map_jh02.get("GJJG")));
					map_jh02.put("JHHJ", MedicineUtils.simpleMultiply(2, MedicineUtils.parseDouble(map_jh02.get("SPSL")), MedicineUtils.parseDouble(map_jh02.get("GJJG"))));
					map_jh02.put("LSJG", map_jh02.get("JHJG"));
					map_jh02.put("LSJE", map_jh02.get("JHHJ"));
					map_jh02.put("CJHJ", 0);
					map_jh02.put("KCSB", 0);
					map_jh02.put("DJFS", 0);
					map_jh02.put("FKJE", 0);
					map_jh02.put("YFJE", 0);
					map_jh02.put("JBYWBZ", 1);
					map_jh02.put("YPKL", 0);
					map_jh02.put("PFJG", 0);
					map_jh02.put("PFJE", 0);
					map_jh02.put("YSDH", 0);
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "计划单明细查询失败", e);
		}
		return list_ret;
	}
}
