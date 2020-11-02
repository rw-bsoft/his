package phis.application.pcm.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.CNDHelper;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class PrescriptionCommentsModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(PrescriptionCommentsModel.class);

	public PrescriptionCommentsModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-20
	 * @description 处方点评抽取保存
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> saveCfCQ(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date cyf=sdf.parse(MedicineUtils.parseString(body.get("CFRQF")));//处方日期范围开始
			Date cyt=sdf.parse(MedicineUtils.parseString(body.get("CFRQT")));//处方日期范围结束
			int cyff=MedicineUtils.parseInt(body.get("CYFF"));//抽样方式
			long cysl=MedicineUtils.parseLong(body.get("CYSL"));//抽样数量
			int dplx=MedicineUtils.parseInt(body.get("DPLX"));//点评类型
			UserRoleToken user = UserRoleToken.getCurrent();
			String userid = MedicineUtils.parseString(user.getUserId());// 用户ID
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			StringBuffer hql_count=new StringBuffer();//查询有多少条在时间范围内未点评的处方
			hql_count.append("select count(*) as NUM from MS_CF02 a,MS_CF01 b,YK_TYPK d where a.YPXH=d.YPXH and  a.CFSB=b.CFSB and a.JGID=:jgid and b.KFRQ>=:ks and b.KFRQ<=:js+1 and a.CFSB not in (select CFSB as CFSB from PCIS_CFDP02 where JGID=:jgid)");
			StringBuffer hql_ywjl=new StringBuffer();//查询该时间范围内是否已经有抽样记录
			hql_ywjl.append("b.CFSB=a.CFSB and b.CFSB=c.CFSB and b.KFRQ>=:ks and b.KFRQ<=:js and b.JGID=:jgid");
			StringBuffer hql=new StringBuffer();//查询出保存到点评02的数据
			hql.append("select CFSB as CFSB,CFHM as CFHM,KSDM as KSDM,YSGH as YSGH,PYGH as PYGH,FYGH as FYGH,JBYW as JBYW,KJYW as KJYW,ZSYW as ZSYW,SFHL as SFHL,ZDMC as ZDMC from (select b.CFSB as CFSB,b.CFHM as CFHM,b.KSDM as KSDM,b.YSDM as YSGH,b.PYGH as PYGH,b.FYGH as FYGH,sum(case d.JYLX when 2 then 1 else 0 end) as JBYW,sum(d.KSBZ) as KJYW,sum((case  when d.YPSX in ( 77 , 18 , 14) then 1 else 0 end )) as ZSYW,1 as SFHL,max(c.ZDMC) as ZDMC from MS_CF02 a ,YK_TYPK d,MS_CF01 b left outer join MS_BRZD c on b.JZXH=c.JZXH and c.ZZBZ=1 where  a.YPXH=d.YPXH and  a.CFSB=b.CFSB and a.JGID=:jgid and b.KFRQ>=:ks and b.KFRQ<=:js+1 and a.CFSB not in (select CFSB as CFSB from PCIS_CFDP02 where JGID=:jgid) ");
			StringBuffer hql_yppz=new StringBuffer();//查询处方药品品种数
			hql_yppz.append("select count(*) as NUM,sum(HJJE) as CFJE from MS_CF02 where CFSB=:cfsb");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("jgid", jgid);
			map_par.put("ks", cyf);
			map_par.put("js", cyt);
			StringBuffer hql_other=new StringBuffer();//其他条件 根据点评类型判断
			if(dplx==2){
				//hql_othertj.append(" and b.CFSB in (select distinct x.CFSB from MS_CF02 x,YK_TYPK y where x.YPXH=y.YPXH and x.JGID=:jgid and y.JYLX=2 and x.CFSB not in (select CFSB as CFSB from PCIS_CFDP02 where JGID=:jgid))");
				hql_other.append(" and y.JYLX=2");
			}else if(dplx==1){
				hql_other.append(" and y.KSBZ=1");
				//hql_othertj.append(" and b.CFSB in (select distinct x.CFSB from MS_CF02 x,YK_TYPK y where x.YPXH=y.YPXH and x.JGID=:jgid and y.KSBZ=2 and x.CFSB not in (select CFSB as CFSB from PCIS_CFDP02 where JGID=:jgid))");
			}else if(dplx==0){
				//hql_othertj.append(" and d.JYLX!=2 and d.KSBZ!=1");
				hql_other.append(" and (y.JYLX!=2 or y.JYLX is null) and (y.KSBZ!=1 or y.KSBZ is null)");
			}
			if(body.containsKey("ypxhs")){
				if(body.get("ypxhs")!=null){
					hql_other.append(" and y.YPXH in (:ypxhs)");
					List<Object> ypxhs=(List<Object>)body.get("ypxhs");
					List<Long> ys=new ArrayList<Long>();
					for(Object ypxh:ypxhs){
						ys.add(MedicineUtils.parseLong(ypxh));
					}
					map_par.put("ypxhs", ys);
				}
			}
			StringBuffer hql_othertj=new StringBuffer();//其他条件 根据点评类型判断
			if(hql_other.length()>0){
				hql_othertj.append(" and b.CFSB in (select distinct x.CFSB from MS_CF02 x,YK_TYPK y where x.YPXH=y.YPXH and x.JGID=:jgid  and x.CFSB not in (select CFSB as CFSB from PCIS_CFDP02 where JGID=:jgid) ").append(hql_other.toString()).append(")");
			}
			//update by  caijy at 2015-09-14 for 未发药的处方不查询
			hql_othertj.append(" and b.PYGH is not null and b.FYGH is not null and b.DJLY=1 ");
			List<Map<String,Object>> list_count=dao.doSqlQuery(hql_count.append(hql_othertj.toString()).toString(), map_par);
			if(list_count==null||list_count.size()==0||list_count.get(0)==null||list_count.get(0).size()==0||MedicineUtils.parseLong(list_count.get(0).get("NUM"))==0){
				return MedicineUtils.getRetMap("没有符合条件的处方");
			}
			long l=dao.doCount("PCIS_CFDP02 a,MS_CF01 b,MS_CF02 c,YK_TYPK d ", hql_ywjl.append(hql_othertj.toString()).toString(), map_par);
			if(l>0){
				if(MedicineUtils.parseInt(body.get("isJX"))==0){
					return MedicineUtils.getRetMap("有记录", 900);
				}
			}
			Map<String,Object> map_cfdp01=new HashMap<String,Object>();
			map_cfdp01.put("JGID", jgid);
			map_cfdp01.put("CYRQ", new Date());
			map_cfdp01.put("DPLX", dplx);
			map_cfdp01.put("CYGH", userid);
			map_cfdp01.put("KSRQ", cyf);
			map_cfdp01.put("JSRQ", cyt);
			map_cfdp01.put("CYFF", cyff);
			map_cfdp01.put("CYSL", cysl);
			map_cfdp01.put("WCZT", 0);
			map_cfdp01.put("ZFBZ", 0);
			long cyxh=MedicineUtils.parseLong(dao.doSave("create", "phis.application.pcm.schemas.PCIS_CFDP01", map_cfdp01, false).get("CYXH"));
			hql.append(hql_othertj.toString()).append(" group by b.CFSB,b.CFHM,b.KSDM,b.YSDM,b.PYGH,b.FYGH").append(") order by  dbms_random.value");
			//map_par.put("dpsl", cysl);
			List<Map<String,Object>> list_cfxx=dao.doSqlQuery(hql.toString(), map_par);
			int i=1;
			for(Map<String,Object> map_cfdp02:list_cfxx){
				if(i>cysl){
					break;
				}
				Map<String,Object> map_par_yppz=new HashMap<String,Object>();
				map_par_yppz.put("cfsb", MedicineUtils.parseLong(map_cfdp02.get("CFSB")));
				List<Map<String,Object>> list_yppz=dao.doSqlQuery(hql_yppz.toString(), map_par_yppz);
				if(list_yppz==null||list_yppz.size()==0||list_yppz.get(0)==null){
					continue;
				}
				Map<String,Object> map_yppz=list_yppz.get(0);
				if(MedicineUtils.parseInt(map_yppz.get("NUM"))==0){
					continue;
				}
				map_cfdp02.put("YPPZ", MedicineUtils.parseInt(map_yppz.get("NUM")));
				map_cfdp02.put("CFJE", MedicineUtils.parseDouble(map_yppz.get("CFJE")));
				if(MedicineUtils.parseInt(map_cfdp02.get("KJYW"))>0){
					map_cfdp02.put("KJYW", 1);
				}
				map_cfdp02.put("JGID", jgid);
				map_cfdp02.put("CYXH", cyxh);
				map_cfdp02.put("DPLX", dplx);
				map_cfdp02.put("DPBZ", 0);
				map_cfdp02.put("SFHL", 0);
				map_cfdp02.put("DPGH", userid);
				map_cfdp02.put("DPRQ", new Date());
				map_cfdp02.put("ZFBZ", 0);
				map_cfdp02.put("TYMS", map_cfdp02.get("YPPZ"));
				dao.doSave("create", "phis.application.pcm.schemas.PCIS_CFDP02", map_cfdp02, false);
				i++;
			}
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "处方日期范围输入错误", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "处方点评保存出错", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "处方点评保存出错", e);
		}
		
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-21
	 * @description 处方点评-左边list查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryCQRQ(Map<String, Object> body)
			throws ModelDataOperationException {
		List<Map<String,Object>> list_ret=null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int dplx=MedicineUtils.parseInt(body.get("DPLX"));//点评类型
		int year=MedicineUtils.parseInt(body.get("YEAR"));//年份
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, 0);
		a.set(Calendar.DATE, 1);
		Date cysjf=a.getTime();
		a.set(Calendar.YEAR, year+1);
		a.add(Calendar.DATE, -1);// 日期回滚一天，也就是上一年的最后一天
		Date cysjt=a.getTime();
		StringBuffer hql=new StringBuffer();//查询数据
		hql.append("select a.WCZT as WCZT,a.CYXH as CYXH,max(a.CYRQ) as CYRQ from PCIS_CFDP01 a,PCIS_CFDP02 b where a.CYXH=b.CYXH  and a.CYRQ>=:ks and a.CYRQ<=:js and a.JGID=:jgid and a.DPLX=:dplx");
//		if(dplx==2){
//			hql.append(" and e.JBYWBZ=1");
//		}else if(dplx==1){
//			hql.append(" and e.KSBZ=1");
//		}else if(dplx==0){
//			hql.append(" and e.KSBZ!=1 and e.JBYWBZ!=1");
//		}
		hql.append(" group by a.CYXH,a.WCZT order by a.CYXH desc");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("jgid", jgid);
		map_par.put("ks", cysjf);
		map_par.put("js", cysjt);
		map_par.put("dplx", dplx);
		try {
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "抽样日期查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 问题维护-删除,注销,取消注销
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> updateCfWT(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		long wtxh=MedicineUtils.parseLong(body.get("WTXH"));
		String tag=MedicineUtils.parseString(body.get("TAG"));
		String wtdm=MedicineUtils.parseString(body.get("WTDM"));
		try {
		if("sc".equals(tag)||"zx".equals(tag)){
			if(sfbsy(wtdm)){//判断是否已经被使用
				return MedicineUtils.getRetMap("问题代码已经被使用,无法删除或注销");
			}
			if("sc".equals(tag)){
			dao.doRemove(wtxh, "phis.application.pcm.schemas.PCIS_WTDM");
			return map_ret;
			}
		}
		StringBuffer hql=new StringBuffer();
		hql.append("update PCIS_WTDM set ZFPB=:zfpb where WTXH=:wtxh");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("wtxh", wtxh);
		if("zx".equals(tag)){
			map_par.put("zfpb", 1);
		}else{
			map_par.put("zfpb", 0);
		}
		dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "数据更新失败", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 查询该问题代码是否被使用
	 * @updateInfo
	 * @param wtdm
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean sfbsy(String wtdm) throws ModelDataOperationException{
		StringBuffer hql=new StringBuffer();
		hql.append(" WTDM||',' like :wtdm");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("wtdm", wtdm+",");
		long l=0;
		try {
			l=dao.doCount("PCIS_CFDP02", hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询是否被使用失败", e);
		}
		if(l>0){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 问题维护-保存
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveCfWT(Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=MedicineUtils.getRetMap();
		long wtxh=0;
		String op=MedicineUtils.parseString(body.get("op"));
		String wtdm=MedicineUtils.parseString(body.get("WTDM"));
		try {
		StringBuffer hql_count=new StringBuffer();
		hql_count.append("WTDM=:wtdm");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("wtdm", wtdm);
		if(!"create".equals(op)){
			wtxh=MedicineUtils.parseLong(body.get("WTXH"));
			hql_count.append(" and WTXH!=:wtxh");
			map_par.put("wtxh", wtxh);
		}
		long l=dao.doCount("PCIS_WTDM", hql_count.toString(), map_par);
		if(l>0){
			return MedicineUtils.getRetMap("已经存在相同的问题代码");
		}
		if(!"create".equals(op)){
			Map<String,Object> map_wtdm=dao.doLoad("phis.application.pcm.schemas.PCIS_WTDM", wtxh);
			String wtdm_old=MedicineUtils.parseString(map_wtdm.get("WTDM"));
			if(!wtdm.equals(wtdm_old)){//如果改变 查询是否有被使用
				if(sfbsy(wtdm_old)){
					return MedicineUtils.getRetMap("问题代码已经被使用,不能修改");
				}
			}
		}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "数据更新失败", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-25
	 * @description 保存处方点评里面的合理和不合理
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void saveCfdpWt(Map<String, Object> body)
			throws ModelDataOperationException {
		int tag=MedicineUtils.parseInt(body.get("TAG"));//1是合理,2是不合理
		long dpxh=MedicineUtils.parseLong(body.get("DPXH"));
		StringBuffer hql=new StringBuffer();
		hql.append("update PCIS_CFDP02 set DPBZ=1");
		Map<String,Object> map_par=new HashMap<String,Object>();
		if(tag==2){
			hql.append(",SFHL=1,WTDM=:wtdm");
			map_par.put("wtdm", MedicineUtils.parseString(body.get("WTDMS")));
		}else if(tag==1){
			hql.append(",SFHL=0,WTDM=''");
		}
		hql.append(" where DPXH=:dpxh");
		map_par.put("dpxh", dpxh);
		try {
			dao.doUpdate(hql.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "更新点评明细失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-26
	 * @description 处方点评-删除,注销,取消注销
	 * @updateInfo
	 * @param body
	 * @throws ModelDataOperationException
	 */
	public void updateCfdp(Map<String, Object> body)
			throws ModelDataOperationException {
		String tag=MedicineUtils.parseString(body.get("TAG"));//sc是删除,wc是完成,qxwc是取消完成
		long cyxh=MedicineUtils.parseLong(body.get("CYXH"));
		try {
			StringBuffer hql=new StringBuffer();
			Map<String,Object> map_par=new HashMap<String,Object>();
		if("sc".equals(tag)){
			hql.append("delete from PCIS_CFDP02 where CYXH=:cyxh");
			map_par.put("cyxh", cyxh);
			dao.doUpdate(hql.toString(), map_par);
			dao.doRemove(cyxh, "phis.application.pcm.schemas.PCIS_CFDP01");
		}else{
			hql.append("update PCIS_CFDP01 set WCZT=:wczt where CYXH=:cyxh");
			map_par.put("cyxh", cyxh);
			if("wc".equals(tag)){
				map_par.put("wczt", 1);
			}else{
				map_par.put("wczt", 0);
			}
			dao.doUpdate(hql.toString(), map_par);
		}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "更新点评记录失败", e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-27
	 * @description  查询处方点评结果记录
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryCfdpjg(Map<String, Object> body)
			throws ModelDataOperationException {
		long cyxh=MedicineUtils.parseLong(body.get("CYXH"));
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgmc=user.getManageUnit().getName();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Map<String,Object> map_ret=new HashMap<String,Object>();
		List<?> cnd=CNDHelper.createArrayCnd("and", CNDHelper.createSimpleCnd("eq", "b.DPBZ", "i", 1), CNDHelper.createSimpleCnd("eq", "b.CYXH", "l", cyxh));
		try {
			List<Map<String,Object>> list_cymx=dao.doList(cnd, null, "phis.application.pcm.schemas.PCIS_CFDP02_DPJG");
			Map<String,Object> map_cy01=dao.doLoad("phis.application.pcm.schemas.PCIS_CFDP01_DFORM", cyxh);
			SchemaUtil.setDictionaryMassageForForm(map_cy01, "phis.application.pcm.schemas.PCIS_CFDP01_DFORM");
			if(list_cymx==null||list_cymx.size()==0){
				map_ret.put("JGMC", jgmc);
				map_ret.put("DPR", "");
				map_ret.put("TBRQ", sdf.format(new Date()));
				map_ret.put("dpList", list_cymx);
				map_ret.put("A", "A=0");
				map_ret.put("B", "B=0");
				map_ret.put("C", "C=0");
				map_ret.put("D", "D=0");
				map_ret.put("E", "E=0");
				map_ret.put("F", "F=0");
				map_ret.put("G", "G=0");
				map_ret.put("H", "H=0");
				map_ret.put("I", "I=0");
				map_ret.put("J", "J=0");
				map_ret.put("K", "K=0");
				map_ret.put("L", "L=0");
				map_ret.put("O", "O=0");
				map_ret.put("P", "P=0");
				return map_ret;
			}
			SchemaUtil.setDictionaryMassageForList(list_cymx, "phis.application.pcm.schemas.PCIS_CFDP02_ULIST");
			double A=0;
			double i=1;
			double C=0;
			double E=0;
			double G=0;
			double I=0;
			double K=0;
			int O=0;
			for(Map<String,Object> map_cymx:list_cymx){
				map_cymx.put("KJYW", MedicineUtils.parseInt(map_cymx.get("KJYW"))==0?0:1);
				map_cymx.put("ZSYW", MedicineUtils.parseInt(map_cymx.get("ZSYW"))==0?0:1);
				removeNull(map_cymx);
				map_cymx.put("SFHL", MedicineUtils.parseInt(map_cymx.get("SFHL"))==0?1:0);
				A+=MedicineUtils.parseInt(map_cymx.get("YPPZ"));
				C+=MedicineUtils.parseInt(map_cymx.get("KJYW"));
				E+=MedicineUtils.parseInt(map_cymx.get("ZSYW"));
				G+=MedicineUtils.parseInt(map_cymx.get("JBYW"));
				I+=MedicineUtils.parseInt(map_cymx.get("TYMS"));
				K+=MedicineUtils.parseDouble(map_cymx.get("CFJE"));
				O+=MedicineUtils.parseInt(map_cymx.get("SFHL"));
				map_cymx.put("YSGH", map_cymx.get("YSGH_text"));
				map_cymx.put("PYGH", "null".equals(MedicineUtils.parseString(map_cymx.get("PYGH_text")))?"":MedicineUtils.parseString(map_cymx.get("PYGH_text")));
				map_cymx.put("FYGH", "null".equals(MedicineUtils.parseString(map_cymx.get("FYGH_text")))?"":MedicineUtils.parseString(map_cymx.get("FYGH_text")));
				map_cymx.put("XH", i);
				map_cymx.put("KFRQ", ((map_cymx.get("KFRQ")+"").split(" "))[0]);
				if(!"".equals(map_cymx.get("CSNY"))){
					map_cymx.put("NL", BSHISUtil.calculateAge(sdf.parse(map_cymx.get("CSNY")+""),null));
				}
				i++;
				//nulltoBlank(map_cymx);
			}
			map_ret.put("JGMC", jgmc);
			map_ret.put("DPR", ((Map<String,Object>)map_cy01.get("CYGH")).get("text"));
			map_ret.put("TBRQ", sdf.format(new Date()));
			map_ret.put("dpList", list_cymx);
			map_ret.put("A", "A="+A);
			map_ret.put("B", "B="+MedicineUtils.formatDouble(1, (A/(i-1))));
			map_ret.put("C", "C="+C);
			map_ret.put("D", "D="+MedicineUtils.formatDouble(1, (C/(i-1))*100));
			map_ret.put("E", "E="+E);
			map_ret.put("F", "F="+MedicineUtils.formatDouble(1,(E/(i-1))*100));
			map_ret.put("G", "G="+G);
			map_ret.put("H", "H="+MedicineUtils.formatDouble(1,(G/A)*100));
			map_ret.put("I", "I="+I);
			map_ret.put("J", "J="+MedicineUtils.formatDouble(1,(I/A)*100));
			map_ret.put("K", "K="+MedicineUtils.formatDouble(2, K));
			map_ret.put("L", "L="+MedicineUtils.formatDouble(1,(K/(i-1))));
			map_ret.put("O", "O="+O);
			map_ret.put("P", "P="+MedicineUtils.formatDouble(1,(O/(i-1))*100));
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "查询处方点评结果记录失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "查询处方点评结果记录失败", e);
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-27
	 * @description 去null
	 * @updateInfo
	 * @param map
	 */
	public void removeNull(Map<String,Object> map){
		for(String key:map.keySet()){
			map.put(key, MedicineUtils.parseString(map.get(key)));
			if("null".equals(map.get(key))){
				map.put(key, "");
			}
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-8-28
	 * @description 将空数据转成页面的空格(打印的时候如果没数据会没边框,未找到原因故特殊处理) 
	 * @updateInfo
	 * @param map
	 */
	public void nulltoBlank(Map<String,Object> map){
		for(String key:map.keySet()){
			if("null".equals(map.get(key))||"".equals(map.get(key))){
				map.put(key, "&nbsp;");
			}
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-2
	 * @description 处方点评报表 list数据查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryTJBBSJ(Map<String, Object> body)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		int dplx=MedicineUtils.parseInt(body.get("DPLX"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		List<Map<String,Object>> list_ret=new ArrayList<Map<String,Object>>();
		StringBuffer hql=new StringBuffer();
		hql.append("select KSDM as KSDM,count(DPXH) as DPSL,sum(SFHL) as BHLSL from PCIS_CFDP02 where JGID=:jgid and DPBZ=1 and DPRQ>=:dprqks and DPRQ<=:dprqjs");
		if(dplx==2){
			hql.append(" and JBYW>0");
		}else if(dplx==1){
			hql.append(" and KJYW>0");
		}else if(dplx==0){
			hql.append(" and KJYW=0 and JBYW=0");
		}
		hql.append(" group by KSDM");
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("jgid", jgid);
			map_par.put("dprqks", sdf.parse(MedicineUtils.parseString(body.get("DPRQKS"))));
			map_par.put("dprqjs", sdf1.parse(MedicineUtils.parseString(body.get("DPRQJS")+" 23:59:59")));
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
			SchemaUtil.setDictionaryMassageForList(list_ret, "phis.application.pcm.schemas.PCIS_CFDP02_TJBB");
			int bhlzs=0;//不合理数合计
			double dpcfzs=0;//总数合计
			for(Map<String,Object> map_dp:list_ret){
				int bhlsl=MedicineUtils.parseInt(map_dp.get("BHLSL"));
				bhlzs+=bhlsl;
				dpcfzs+=MedicineUtils.parseInt(map_dp.get("DPSL"));
				if(bhlsl==0){
					map_dp.put("BHLSL",null );//不合理数量
				}else{
					map_dp.put("BHLSL",bhlsl );	
				}
				map_dp.put("BHLBL",MedicineUtils.formatDouble(2, (bhlsl/MedicineUtils.parseDouble(map_dp.get("DPSL")))*100));
			}
			Map<String,Object> map_hj=new HashMap<String,Object>();
			map_hj.put("KSDM", 0);
			map_hj.put("KSDM_text", "");
			map_hj.put("HJ", "合计");
			if(list_ret==null||list_ret.size()==0){
				map_hj.put("DPSL", 0);
				map_hj.put("BHLSL", 0);
				map_hj.put("BHLBL",0);
			}else{
				map_hj.put("DPSL", dpcfzs);
				map_hj.put("BHLSL", bhlzs);
				map_hj.put("BHLBL",MedicineUtils.formatDouble(2, (bhlzs/dpcfzs)*100));
			}
			list_ret.add(map_hj);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "日期范围输入出错", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "报表数据查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-9-2
	 * @description 点评报表明细数据查询
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryTJBBSJMX(List<?> cnd,int pageSize,int pageNo)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			map_ret=dao.doList(cnd, null, "phis.application.pcm.schemas.PCIS_CFDP02_DPJG", pageNo, pageSize, "");
			//list_cymx=dao.doList(cnd, null, "phis.application.pcm.schemas.PCIS_CFDP02_DPJG");
			if(map_ret.get("body")==null){
				return map_ret;
			}
			List<Map<String,Object>> list_cymx=(List<Map<String,Object>>)map_ret.get("body");
			SchemaUtil.setDictionaryMassageForList(list_cymx, "phis.application.pcm.schemas.PCIS_CFDP02_ULIST");
			for(Map<String,Object> map_cymx:list_cymx){
				map_cymx.put("PYGH", "null".equals(MedicineUtils.parseString(map_cymx.get("PYGH_text")))?"":MedicineUtils.parseString(map_cymx.get("PYGH_text")));
				map_cymx.put("FYGH", "null".equals(MedicineUtils.parseString(map_cymx.get("FYGH_text")))?"":MedicineUtils.parseString(map_cymx.get("FYGH_text")));
				map_cymx.put("KFRQ", ((map_cymx.get("KFRQ")+"").split(" "))[0]);
				if(!"".equals(map_cymx.get("CSNY"))){
					map_cymx.put("NL", BSHISUtil.calculateAge(sdf.parse(map_cymx.get("CSNY")+""),null));
				}
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "报表明细数据查询失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "病人出生年月数据有误", e);
		}
		return map_ret;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-9-17
	 * @description 自动点评接口数据查询
	 * @updateInfo
	 * @param cyxh
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String,Object>> queryZddp(long cyxh, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = (String) user.getUserId();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		parameters1.put("userId", uid);
		List<Map<String,Object>> ret=new ArrayList<Map<String,Object>>();
		StringBuffer hql=new StringBuffer();//查出当前点评的所有处方
		hql.append("select CFSB as CFSB,DPXH as DPXH from PCIS_CFDP02 where CYXH=:cyxh");
		StringBuffer hql_cf=new StringBuffer();//查询病人信息及处方信息
		hql_cf.append("select a.BRID as BRID,a.JZXH as JZXH,nvl(c.ZSXX,'') as ZSXX,nvl(c.W,0) as W,nvl(c.RSBZ,0) as RSBZ,d.MZHM as MZHM,d.BRXM as BRXM,d.CSNY as CSNY,d.BRXB as BRXB,a.KSDM as KSDM,e.OFFICENAME as KSMC  from MS_CF01 a,YS_MZ_JZLS b,MS_BCJL c,MS_BRDA d,SYS_Office e where c.JZXH=a.JZXH and a.KSDM=e.ID and d.BRID=b.BRBH and a.JZXH=b.JZXH and a.CFSB=:cfsb");
		StringBuffer hql_cfmx=new StringBuffer();//查询处方明细
		hql_cfmx.append("select a.YPXH as YPXH,b.YPMC as YPMC,a.YPCD as YPCD,a.YCJL as YCJL,a.YFDW as JLDW,a.MRCS as MRCS,a.YPYF as YPYF,a.GYTJ as GYTJ,c.XMMC as YFMC,a.YPZH as YPZH from MS_CF02 a,YK_TYPK b,ZY_YPYF c where a.GYTJ=c.YPYF and a.YPXH=b.YPXH and CFSB=:cfsb ");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("cyxh", cyxh);
		try {
			Map<String, Object> retMap = dao.doLoad(
					"platform.reg.schemas.BASE_User", uid);
			String password=MedicineUtils.parseString(retMap.get("password"));
			String HLYYIP=MedicineUtils.parseString(ParameterUtil.getParameter(
					ParameterUtil.getTopUnitId(), "HLYYIP", ctx)) ;
			List<Map<String,Object>> l_cfsbs=dao.doQuery(hql.toString(), map_par);
			if(l_cfsbs==null||l_cfsbs.size()==0){
				throw new ModelDataOperationException("未查到处方信息");
			}
			for(Map<String,Object> m_cfsb:l_cfsbs){
				long cfsb=MedicineUtils.parseLong(m_cfsb.get("CFSB"));
				long dpxh=MedicineUtils.parseLong(m_cfsb.get("DPXH"));
				Map<String,Object> map_par_cfsb=new HashMap<String,Object>();
				map_par_cfsb.put("cfsb", cfsb);
				Map<String,Object> map_cf=dao.doLoad(hql_cf.toString(), map_par_cfsb);
				if(map_cf==null||map_cf.size()==0){
					throw new ModelDataOperationException("未查到处方信息");
				}
				map_cf.put("NL", BSPHISUtil.getPersonAge((Date)map_cf.get("CSNY"),new Date()).get("ages"));
				map_cf.put("password", password);
				map_cf.put("HLYYIP", HLYYIP);
				map_cf.put("DPXH", dpxh);
				List<Map<String,Object>> list_cfmx=dao.doQuery(hql_cfmx.toString(), map_par_cfsb);
				if(list_cfmx==null||list_cfmx.size()==0){
					throw new ModelDataOperationException("未查到处方明细信息");
				}
				map_cf.put("cfmx", list_cfmx);
				ret.add(map_cf);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "自动点评接口数据查询失败", e);
		}
		return ret;
	}
}
