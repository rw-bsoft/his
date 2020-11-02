package phis.application.war.source;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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


import phis.application.mds.source.MedicineUtils;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;
import phis.source.utils.BSPHISUtil;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;

public class WardDoctorPrintModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(WardDoctorPrintModel.class);
	public WardDoctorPrintModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-11
	 * @description 医嘱套打 左边病人列表数据查询
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryYzdyBrxx(Map<String,Object> body)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		String zyhm=MedicineUtils.parseString(body.get("ZYHM"));
//		if(!"".equals(zyhm)){
//			zyhm=BSPHISUtil.get_public_fillleft(zyhm, "0",
//					BSPHISUtil.getRydjNo(UserRoleToken.getCurrent().getManageUnit().getId(), "ZYHM", "", dao).length());
//		}
		String brch=MedicineUtils.parseString(body.get("BRCH"));
		long bq=MedicineUtils.parseLong(body.get("BQ"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(bq==0&&"".equals(zyhm)){
			return null;
		}
		List<Map<String,Object>> list_brxx=null;
		try {
		if("".equals(zyhm)){//在院病人
			StringBuffer hql=new StringBuffer();
			hql.append("select ZYH as ZYH,BRCH as BRCH,BRXM as BRXM,BRXB as BRXB,CSNY as CSNY from ZY_BRRY where BRBQ=:bq and CYPB<8");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("bq", bq);
			if(!"".equals(brch)){
				hql.append(" and BRCH=:brch");
				map_par.put("brch", brch);
			}
			list_brxx=dao.doQuery(hql.toString(), map_par);
		}else{//出院或者转科病人
			StringBuffer hql_cy=new StringBuffer();//查询是否有出院记录
			hql_cy.append("select ZYH as ZYH,BRCH as BRCH,BRXM as BRXM,BRXB as BRXB,CSNY as CSNY from ZY_BRRY where ZYHM=:zyhm and CYPB>=8 and JGID=:jgid");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyhm", zyhm);
			map_par.put("jgid", jgid);
			list_brxx=dao.doQuery(hql_cy.toString(), map_par);
			if(list_brxx==null||list_brxx.size()==0){//查询是否有转科记录
				StringBuffer hql_zk=new StringBuffer();
				hql_zk.append("select distinct a.ZYH as ZYH,a.BRCH as BRCH,a.BRXM as BRXM,a.BRXB as BRXB,a.CSNY as CSNY from ZY_BRRY a,ZY_ZKJL b where a.ZYH=b.ZYH and b.HQBQ=:bq and a.ZYHM=:zyhm and a.JGID=:jgid");
				map_par.put("bq", bq);
				list_brxx=dao.doSqlQuery(hql_zk.toString(), map_par);
			}
		}
		SchemaUtil.setDictionaryMassageForList(list_brxx, "phis.application.war.schemas.YZDY_BR");
		for(Map<String,Object> map_brxx:list_brxx){
			if(!"".equals(map_brxx.get("CSNY"))){
				map_brxx.put("BRNL", BSHISUtil.calculateAge(sdf.parse(map_brxx.get("CSNY")+""),null));
			}
		}	
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "病人列表查询失败", e);
		} catch (ParseException e) {
			MedicineUtils.throwsException(logger, "病人列表查询失败", e);
		}
		
		return list_brxx;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-11
	 * @description 医嘱套打 查询医嘱信息
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryYz(Map<String,Object> body, Context ctx)throws ModelDataOperationException {
		int yzzt=MedicineUtils.parseInt(body.get("YZZT"));//医嘱状态,停嘱或非停嘱
		long zyh=MedicineUtils.parseLong(body.get("ZYH"));//住院号
		int yzlx=MedicineUtils.parseInt(body.get("YZLX"));//医嘱类型,长期医嘱1和临时医嘱2
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		StringBuffer hql=new StringBuffer();
		hql.append("select * from (select c.*, row_number() over(partition by c.YZMC,c.YZZH order by c.YZZH) rn from ( select a.LSYZ as LSYZ,a.ZFYP as ZFYP,a.YDLB as YDLB,a.YZZH as YZZH,b.JLDW as JLDW," +
				" a.YCJL as YCJL,a.SYPC as SYPC,a.YPYF as YPYF,a.JGID as JGID,a.ZYH as ZYH ,a.JLXH as JLXH," +
				" a.KSSJ as KSSJ,A.bzxx as BZXX,case when a.bzxx like '%检验电子申请%' then (select c.examinaim from l_lis_sqd@lis c where c.doctrequestno=a.sqid ) when a.bzxx like '%检查开单项目,若要修改请先在开单界面删除再重开%'  then （select to_char(wm_concat(f.LBMC||'/'||g.BWMC||'/'||d.XMMC)) from YJ_JCSQ_KD02 e,YJ_JCSQ_JCLB f,YJ_JCSQ_JCBW g, YJ_JCSQ_JCXM d where f.LBID = e.LBID and g.BWID = e.BWID and d.XMID = e.XMID and e.SQDH = a.YZZH  and e.YLLB = '2') else  a.YZMC  end as YZMC,a.YSGH as YSGH,a.TZSJ as TZSJ,a.TZYS as TZYS,a.FHGH as FHGH," +
				" a.FHSJ as FHSJ from ZY_BQYZ a left outer join YK_TYPK b on a.YPXH=b.YPXH where ZYH=:zyh  ");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("zyh", zyh);
		StringBuffer hql_count=new StringBuffer();
		hql_count.append(" ZYH=:zyh");
		if(yzlx==2){
			hql.append(" and  LSYZ=1 and (a.YDLB!=302 or a.YDLB is null)");	
		}else{
			hql.append(" and  (LSYZ=0 or a.YDLB=302)");	
			if(yzzt==2){
				hql.append(" and TZSJ is not null and (DYZT!=2 or DYZT is null)");
			}else{
				//hql.append(" and TZSJ is  null");
			}
		}
		
		List<Map<String,Object>> list_ret=null;
		List<Map<String,Object>> list_ret_new=new ArrayList<Map<String,Object>>();
		try {
			if(yzzt!=2){
				long l=dao.doCount("EMR_YZDY", hql_count.toString(), map_par);
				if(l>0){
					hql.append(" and (DYZT='0' or DYZT is null) ");
				}
			}
			int YZBDYSJ =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, BSPHISSystemArgument.YZBDYSJ, ctx)) ;//每页打印行数
			if(YZBDYSJ==1){//只显示提交后的记录
//				hql.append(" and ((YSBZ=1 and YSTJ=1) or YSBZ=0 or a.YDLB=302)");
				hql.append(" and ((YSBZ=1 and YSTJ=1) or a.YDLB=302)) c） where rn=1");//add by lizhi 2017-11-08只打印已提交的医嘱
			}else{//只显示复核后的记录
				hql.append(" and (FHBZ=1 or a.YDLB=302)) c） where rn=1");
			}
			hql.append(" order by YZZH");
			list_ret=dao.doSqlQuery(hql.toString(), map_par);
			//添加转换 add zhanghao 2019-12-07
			for(int i=0; i<list_ret.size();i++){
				Map<String,Object> ret=list_ret.get(i);
				if("检验电子申请".equals(ret.get("BZXX"))&&ret.get("YZMC").toString().indexOf("+")>0){
					String[] rs=ret.get("YZMC").toString().split("\\+");
					for(int j=0;j<rs.length;j++) {
						Map<String, Object> ret_new = new HashMap<String, Object>();
						ret_new.putAll(ret);
						ret_new.put("YZMC", rs[j]);                //名称
						//ret_new.put("JLXH", ret.get("JLXH")+""+j);                //记录序号，对应医嘱本YZBXH
						list_ret_new.add(ret_new);
					}
				}
				else{
					list_ret_new.add(ret);
				}
			}

			SchemaUtil.setDictionaryMassageForList(list_ret_new, "phis.application.war.schemas.YZDY_CQYZ");
			if(!body.containsKey("YZTD")){//界面查询 不需要显示转科记录
				List<Map<String,Object>> list_jmRet=new ArrayList<Map<String,Object>>();
				for(int i=0;i<list_ret_new.size();i++){
					if(!"302".equals(list_ret_new.get(i).get("YDLB")+"")){
						list_jmRet.add(list_ret_new.get(i));
					//	list_ret.remove(i);
					}
				}
				return list_jmRet;
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医嘱信息查询失败", e);
		}
		return list_ret_new;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-15
	 * @description 医嘱套打前的业务操作
	 * @updateInfo
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String,Object> saveWardDoctorPrint(Map<String,Object> body,Context ctx)throws ModelDataOperationException {
		Map<String,Object> ret=MedicineUtils.getRetMap(); 
		String type=MedicineUtils.parseString(body.get("TYPE"));
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		long zyh=MedicineUtils.parseLong(body.get("ZYH"));//住院号
		StringBuffer dyyzs=new StringBuffer();
		StringBuffer tzyzs=new StringBuffer();
		int yzqx=MedicineUtils.parseInt(body.get("YZLX"));
		try {
		if("jxdy".equals(type)){//继续打印,将没打印的医嘱保存到打印表 并返回医嘱序号的集合
			body.put("YZTD", true);
			List<Map<String,Object>> list_yzs=queryYz(body,ctx);
			if(list_yzs==null||list_yzs.size()==0){
				return ret;
			}
			int YZTDMYTS =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZTDMYTS", ctx)) ;//每页打印行数
			if(YZTDMYTS>30){//根据实际情况设置每页行数最大值,现在暂时定为28行
				ret.put("code", 9000);
				ret.put("msg", "设置的每页行数大于纸张能打的最大高度,请重新设置,小于28的整数");
				return ret;
			}
			int ZKCZHHYDY =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "ZKCZHHYDY", ctx)) ;//转科重整后换页打印
			StringBuffer hql_max_dyjl=new StringBuffer();//查出最后一条打印记录
			hql_max_dyjl.append("select DYHH as DYHH,DYYM as DYYM  from EMR_YZDY where ZYH=:zyh and YZQX=:yzqx");
			StringBuffer hql_tzyz_pc=new StringBuffer();//停嘱医嘱继续打印要判断打印表是否有该记录
			hql_tzyz_pc.append("select DYXH as DYXH from EMR_YZDY where YZBXH=:yzxh and CZBZ!=1");
			StringBuffer hql_yz_update=new StringBuffer();//更新医嘱表的打印状态
		    hql_yz_update.append("update ZY_BQYZ set DYZT=:dyzt where JLXH in (:yzxh)");
			StringBuffer hql_yz_update1=new StringBuffer();//更新医嘱表的打印状态
			hql_yz_update1.append("update zy_bqyz t set  t.DYZT=1 where t.kssj=(select a.kssj from  zy_bqyz a where  a.jlxh=:yzxh) and t.yzzh=(select b.yzzh from  zy_bqyz b where  b.jlxh=:yzxh) and (t.DYZT is null or t.DYZT=0)");		
			StringBuffer hql_yz_update_tz=new StringBuffer();//更新医嘱表的打印状态(停嘱)
			hql_yz_update_tz.append("update ZY_BQYZ set DYZT=2 where JLXH in (:yzxh)");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyh", zyh);
			map_par.put("yzqx", yzqx);
			List<Map<String,Object>> list_zdyzdy=dao.doSqlQuery(hql_max_dyjl.toString(), map_par);
			int dyym=1;
			int dyhh=1;
			if(list_zdyzdy!=null&&list_zdyzdy.size()>0&&list_zdyzdy.get(0)!=null){
				Map<String,Object> map_zdyzdy=getMaxDyjl(list_zdyzdy);
				dyym=MedicineUtils.parseInt(map_zdyzdy.get("DYYM"));
				dyhh=MedicineUtils.parseInt(map_zdyzdy.get("DYHH"))+1;
			}
			List<Long> list_jlxhs=new ArrayList<Long>();
			List<Long> list_jlxhs_tz=new ArrayList<Long>();
			for(int i=0;i<list_yzs.size();i++){
				Map<String,Object> map_zdyzdy=list_yzs.get(i);
				int k=1;
				for(int j=i+1;j<list_yzs.size();j++){
					if(MedicineUtils.parseLong(map_zdyzdy.get("YZZH"))==MedicineUtils.parseLong(list_yzs.get(j).get("YZZH"))){
						k++;
					}
				}
//				if(k>YZTDMYTS){
//					ret.put("code", 9000);
//					ret.put("msg", "同组药品数量大于每页打印行数");
//					return ret;
//				}
				if(k>1&&(k+dyhh-1)>YZTDMYTS){//同组的如果分页了,补全,同组的显示在同一页
					int xh=YZTDMYTS-dyhh;
					 for(int x=0;x<=xh;x++){
						 	Map<String,Object> map_czyz=new HashMap<String,Object>();
							map_czyz.put("YZBXH", -1);//-1表示因为同组在不同页而用于填充的空行
							map_czyz.put("ZYH", zyh);
							map_czyz.put("YZQX", yzqx);
							map_czyz.put("DYRQ", new Date());
							map_czyz.put("DYNR", "空行");
							map_czyz.put("DYYM", dyym);
							map_czyz.put("DYHH", dyhh);
							map_czyz.put("CZBZ", 0);
							map_czyz.put("JGID",jgid);
							Map<String,Object> map_key=dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_czyz, false);
							dyhh++;
							if(dyyzs.length()>0){
								dyyzs.append(",").append(map_key.get("DYXH"));
							}else{
								dyyzs.append(map_key.get("DYXH"));
							}
					 }
				}
				//转科医嘱特殊判断
				String YDLB=MedicineUtils.parseString(map_zdyzdy.get("YDLB"));
				if("302".equals(YDLB)){
					if(ZKCZHHYDY==1){
						if(dyhh!=1){
							int xh=YZTDMYTS-dyhh;
							 for(int x=0;x<=xh;x++){
								 	Map<String,Object> map_czyz=new HashMap<String,Object>();
									map_czyz.put("YZBXH", -1);//-1表示因为同组在不同页而用于填充的空行
									map_czyz.put("ZYH", zyh);
									map_czyz.put("YZQX", yzqx);
									map_czyz.put("DYRQ", new Date());
									map_czyz.put("DYNR", "空行");
									map_czyz.put("DYYM", dyym);
									map_czyz.put("DYHH", dyhh);
									map_czyz.put("CZBZ", 0);
									map_czyz.put("JGID",jgid);
									Map<String,Object> map_key=dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_czyz, false);
									dyhh++;
									if(dyyzs.length()>0){
										dyyzs.append(",").append(map_key.get("DYXH"));
									}else{
										dyyzs.append(map_key.get("DYXH"));
									}
							 }
							 dyym++;
							 dyhh=1;
						}
					}
					 Map<String,Object> map_zkyz=new HashMap<String,Object>();
					 map_zkyz.put("YZBXH", MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));//医嘱本序号  值待定
					 map_zkyz.put("ZYH", zyh);
					 map_zkyz.put("YZQX", yzqx);
					 map_zkyz.put("DYRQ", new Date());
					 map_zkyz.put("DYNR", "转科后");
					 map_zkyz.put("DYYM", dyym);
					 map_zkyz.put("DYHH", dyhh);
					 map_zkyz.put("CZBZ", 0);
					 map_zkyz.put("JGID",jgid);
					 Map<String,Object> map_key= dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_zkyz, false);
					 dyhh++;
					 if(dyyzs.length()>0){
							dyyzs.append(",").append(map_key.get("DYXH"));
						}else{
							dyyzs.append(map_key.get("DYXH"));
						}
					 list_jlxhs.add(MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
					 continue;
				}
				///转科医嘱特殊判断结束
				if(MedicineUtils.parseInt(map_zdyzdy.get("LSYZ"))!=1&&map_zdyzdy.containsKey("TZSJ")&&map_zdyzdy.get("TZSJ")!=null&&!"".equals(map_zdyzdy.get("TZSJ")+"")){
					list_jlxhs_tz.add(MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
				}else{
					list_jlxhs.add(MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
				}
				Map<String,Object> map_key=null;
				Map<String,Object> map_par_tzyz_pc=new HashMap<String,Object>();
				//检验电子申请只做一次处理
				if(!"检验电子申请".equals(map_zdyzdy.get("BZXX")+"")) {
				map_par_tzyz_pc.put("yzxh", MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
				map_key=dao.doLoad( hql_tzyz_pc.toString(), map_par_tzyz_pc);
				}
				if(map_key==null){
					if(dyhh>YZTDMYTS){
						dyym++;
						dyhh=1;
					}
					Map<String,Object> map_yzdy=new HashMap<String,Object>();
					String dynr=MedicineUtils.parseString(map_zdyzdy.get("YZMC"))+" "+(MedicineUtils.parseDouble(map_zdyzdy.get("YCJL"))==0?"":MedicineUtils.parseDouble(map_zdyzdy.get("YCJL")))+MedicineUtils.parseString(map_zdyzdy.get("JLDW"))+" "+MedicineUtils.parseString(map_zdyzdy.get("SYPC_text"))+" "+MedicineUtils.parseString(map_zdyzdy.get("YPYF_text"));
					if(MedicineUtils.parseInt(map_zdyzdy.get("ZFYP"))==1){
						dynr="(自备)"+dynr;
					}
					map_yzdy.put("YZBXH", MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
					map_yzdy.put("ZYH", MedicineUtils.parseLong(map_zdyzdy.get("ZYH")));
					map_yzdy.put("YZQX", yzqx);
					map_yzdy.put("DYRQ", new Date());
					map_yzdy.put("DYNR", dynr);
					map_yzdy.put("DYYM", dyym);
					map_yzdy.put("DYHH", dyhh);
					map_yzdy.put("CZBZ", 0);
					map_yzdy.put("JGID",jgid);
					map_key=dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_yzdy, false);
					dyhh++;
				}else{
					if(tzyzs.length()>0){
						tzyzs.append(",").append(map_key.get("DYXH"));
					}else{
						tzyzs.append(map_key.get("DYXH"));
					}
				}
				if(dyyzs.length()>0){
					dyyzs.append(",").append(map_key.get("DYXH"));
				}else{
					dyyzs.append(map_key.get("DYXH"));
				}
				
			}
			ret.put("dyyzs", dyyzs.toString());
			ret.put("tzyzs", tzyzs.toString());
			int yzzt=MedicineUtils.parseInt(body.get("YZZT"));//医嘱状态,停嘱或非停嘱
			if(list_jlxhs.size()>0){
				Map<String,Object> map_par_update=new HashMap<String,Object>();
				map_par_update.put("dyzt", yzzt);
				map_par_update.put("yzxh", list_jlxhs);
				dao.doSqlUpdate(hql_yz_update.toString(), map_par_update);
				for (int i = 0; i < list_jlxhs.size(); i++) {
					Map<String,Object> map_par_update1=new HashMap<String,Object>();
					map_par_update1.put("yzxh", list_jlxhs.get(i));
				    dao.doSqlUpdate(hql_yz_update1.toString(), map_par_update1);
				}
			}
			if(list_jlxhs_tz.size()>0){
				Map<String,Object> map_par_update_tz=new HashMap<String,Object>();
				map_par_update_tz.put("yzxh", list_jlxhs_tz);
				dao.doUpdate(hql_yz_update_tz.toString(), map_par_update_tz);
			}
	//		throw new ModelDataOperationException("测试");
		}
		else if("aydy".equals(type)){//按页打印,查询是否有当前输入页的打印数据
			int pageFrom=MedicineUtils.parseInt(body.get("pageFrom"));
			StringBuffer hql=new StringBuffer();
			hql.append(" ZYH=:zyh and YZQX=:yzqx and DYYM>=:pageFrom ");
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyh", zyh);
			map_par.put("yzqx", yzqx);
			map_par.put("pageFrom",pageFrom);
			int pageTo=MedicineUtils.parseInt(body.get("pageTo"));
			if(pageTo!=0){
				hql.append(" and DYYM<=:pageTo")	;
				map_par.put("pageTo",pageTo);
			}
			long l=dao.doCount("EMR_YZDY", hql.toString(), map_par);
			if(l>0){
				ret.put("sfysj", "true");
			}else{
				ret.put("sfysj", "false");
			}
		}else if("czdy".equals(type)){//重整打印
			StringBuffer hql_qdy=new StringBuffer();//重整前判断开嘱医嘱是否已经全部打印
			hql_qdy.append("ZYH=:zyh  and TZSJ is null and JLXH not in (select YZBXH from EMR_YZDY where ZYH=:zyh)");
			StringBuffer hql=new StringBuffer();//查询需要重整的医嘱
			hql.append("select a.ZFYP as ZFYP,a.YZZH as YZZH,a.TZSJ as TZSJ,b.JLDW as JLDW,a.YCJL as YCJL,a.SYPC as SYPC,a.YPYF as YPYF,a.ZYH as ZYH ,a.JLXH as JLXH,a.YZMC as YZMC from ZY_BQYZ a left outer join YK_TYPK b on a.YPXH=b.YPXH where ZYH=:zyh  and TZSJ is null ");
			if(yzqx==1){
				hql.append(" and LSYZ=0");
				hql_qdy.append(" and LSYZ=0");
			}else{
				hql.append(" and LSYZ=1");
				hql_qdy.append(" and LSYZ=1");
			}
			int YZBDYSJ =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZBDYSJ", ctx)) ;//每页打印行数
			if(YZBDYSJ==1){//只显示提交后的记录
				hql.append(" and ((YSBZ=1 and YSTJ=1) or YSBZ=0)");
				hql_qdy.append(" and ((YSBZ=1 and YSTJ=1) or YSBZ=0)");
			}else{//只显示复核后的记录
				hql.append(" and FHBZ=1");
				hql_qdy.append(" and FHBZ=1");
			}
			Map<String,Object> map_par=new HashMap<String,Object>();
			map_par.put("zyh", zyh);
			long l=dao.doCount("ZY_BQYZ", hql_qdy.toString(), map_par);
			if(l>0){
				ret.put("code", 9000);
				ret.put("msg", "有未打印的医嘱,请先打印");
				return ret;
			}
			hql.append(" order by a.YZZH");
			List<Map<String,Object>> list_yzs=dao.doSqlQuery(hql.toString(), map_par);
			if(list_yzs==null||list_yzs.size()==0){
				ret.put("code", 9000);
				ret.put("msg", "没有需要重整的记录");
				return ret;
			}
			SchemaUtil.setDictionaryMassageForList(list_yzs, "phis.application.war.schemas.YZDY_CQYZ");
			int YZTDMYTS =MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZTDMYTS", ctx)) ;//每页打印行数
			StringBuffer hql_max_dyjl=new StringBuffer();//查出最后一条打印记录
			hql_max_dyjl.append("select DYHH as DYHH,DYYM as DYYM  from EMR_YZDY where ZYH=:zyh and YZQX=:yzqx");
			StringBuffer hql_yz_update=new StringBuffer();//更新医嘱表的打印状态
			hql_yz_update.append("update ZY_BQYZ set DYZT=:dyzt where JLXH=:jlxh");
			map_par.put("yzqx", yzqx);
			List<Map<String,Object>> list_zdyzdy=dao.doSqlQuery(hql_max_dyjl.toString(), map_par);
			int dyym=1;
			int dyhh=1;
			if(list_zdyzdy!=null&&list_zdyzdy.size()>0&&list_zdyzdy.get(0)!=null){
				Map<String,Object> map_zdyzdy=getMaxDyjl(list_zdyzdy);
				dyym=MedicineUtils.parseInt(map_zdyzdy.get("DYYM"));
				dyhh=MedicineUtils.parseInt(map_zdyzdy.get("DYHH"))+1;
			}
			if(dyhh>YZTDMYTS){
				dyym++;
				dyhh=1;
			}
			StringBuffer hql_update=new StringBuffer();
			hql_update.append("update EMR_YZDY set CZBZ=1 where ZYH=:zyh and YZQX=:yzqx");
			dao.doUpdate(hql_update.toString(), map_par);
			Map<String,Object> map_czyz=new HashMap<String,Object>();
			map_czyz.put("YZBXH", 0);//医嘱本序号  值待定
			map_czyz.put("ZYH", zyh);
			map_czyz.put("YZQX", yzqx);
			map_czyz.put("DYRQ", new Date());
			map_czyz.put("DYNR", "重整医嘱");
			map_czyz.put("DYYM", dyym);
			map_czyz.put("DYHH", dyhh);
			map_czyz.put("CZBZ", 0);
			map_czyz.put("JGID",jgid);
			dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_czyz, false);
			dyhh++;
			for(int i=0;i<list_yzs.size();i++){
				Map<String,Object> map_zdyzdy=list_yzs.get(i);
				int k=1;
				for(int j=i+1;j<list_yzs.size();j++){
					if(MedicineUtils.parseLong(map_zdyzdy.get("YZZH"))==MedicineUtils.parseLong(list_yzs.get(j).get("YZZH"))){
						k++;
					}
				}
//				if(k>YZTDMYTS){
//					ret.put("code", 9000);
//					ret.put("msg", "同组药品数量大于每页打印行数");
//					return ret;
//				}
				if(k>1&&(k+dyhh)>YZTDMYTS){//同组的如果分页了,补全,同组的显示在同一页
					int xh=YZTDMYTS-dyhh;
					 for(int x=0;x<=xh;x++){
						 	Map<String,Object> map_czyz_kg=new HashMap<String,Object>();
						 	map_czyz_kg.put("YZBXH", -1);//-1表示因为同组在不同页而用于填充的空行
						 	map_czyz_kg.put("ZYH", zyh);
						 	map_czyz_kg.put("YZQX", yzqx);
						 	map_czyz_kg.put("DYRQ", new Date());
						 	map_czyz_kg.put("DYNR", "空行");
						 	map_czyz_kg.put("DYYM", dyym);
						 	map_czyz_kg.put("DYHH", dyhh);
						 	map_czyz_kg.put("CZBZ", 2);
						 	map_czyz_kg.put("JGID",jgid);
							dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_czyz_kg, false);
							dyhh++;
					 }
				}
				
				Map<String,Object> map_par_update=new HashMap<String,Object>();
				map_par_update.put("jlxh", MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
				if(map_zdyzdy.get("TZSJ")==null){
					map_par_update.put("dyzt", 1);
				}else{
					map_par_update.put("dyzt", 2);
				}
				dao.doUpdate(hql_yz_update.toString(), map_par_update);
				if(dyhh>YZTDMYTS){
					dyym++;
					dyhh=1;
				}
				Map<String,Object> map_yzdy=new HashMap<String,Object>();
				String dynr=MedicineUtils.parseString(map_zdyzdy.get("YZMC"))+" "+(MedicineUtils.parseDouble(map_zdyzdy.get("YCJL"))==0?"":MedicineUtils.parseDouble(map_zdyzdy.get("YCJL")))+MedicineUtils.parseString(map_zdyzdy.get("JLDW"))+" "+MedicineUtils.parseString(map_zdyzdy.get("SYPC_text"))+" "+MedicineUtils.parseString(map_zdyzdy.get("YPYF_text"));
				if(MedicineUtils.parseInt(map_zdyzdy.get("ZFYP"))==1){
					dynr="(自备)"+dynr;
				}
				map_yzdy.put("YZBXH", MedicineUtils.parseLong(map_zdyzdy.get("JLXH")));
				map_yzdy.put("ZYH", MedicineUtils.parseLong(map_zdyzdy.get("ZYH")));
				map_yzdy.put("YZQX", yzqx);
				map_yzdy.put("DYRQ", new Date());
				map_yzdy.put("DYNR", dynr);
				map_yzdy.put("DYYM", dyym);
				map_yzdy.put("DYHH", dyhh);
				map_yzdy.put("CZBZ", 2);
				map_yzdy.put("JGID",jgid);
				dao.doSave("create", "phis.application.war.schemas.EMR_YZDY", map_yzdy, false);
				dyhh++;
			}
		}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "医嘱打印处理失败", e);
		} catch (ValidateException e) {
			MedicineUtils.throwsException(logger, "医嘱打印处理失败", e);
		}
		return ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-16
	 * @description 获取最后一行打印的页码和行数
	 * @updateInfo
	 * @param l
	 * @return
	 */
	private Map<String,Object> getMaxDyjl(List<Map<String,Object>> l){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.putAll(l.get(0));
		for(int i=1;i<l.size();i++){
			if(MedicineUtils.parseInt(l.get(i).get("DYYM"))>MedicineUtils.parseInt(map_ret.get("DYYM"))){
				map_ret.putAll(l.get(i));
			}else if(MedicineUtils.parseInt(l.get(i).get("DYYM"))==MedicineUtils.parseInt(map_ret.get("DYYM"))){
				if(MedicineUtils.parseInt(l.get(i).get("DYHH"))>MedicineUtils.parseInt(map_ret.get("DYHH"))){
					map_ret.putAll(l.get(i));
				}
			}
		}
		return map_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-18
	 * @description 查询指定行打印数据
	 * @updateInfo
	 * @param cnd
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryDyjl(List<Object> cnd)throws ModelDataOperationException {
		StringBuffer hql=new StringBuffer();
		List<Map<String,Object>> list_ret=null;
		hql.append("select b.ZYH as ZYH,b.YZBXH as JLXH,b.DYXH as DYXH,to_char(a.KSSJ,'yyyy-mm-dd hh24:mi:ss') as KSSJ,b.DYNR as YZMC,a.YSGH as YSGH,a.FHGH as FHGH,to_char(a.TZSJ,'yyyy-mm-dd hh24:mi:ss') as TZSJ,a.TZYS as TZYS,a.FHGH as FHGH,to_char(a.FHSJ,'yyyy-mm-dd hh24:mi:ss') as FHSJ,b.DYYM as DYYM,b.DYHH as DYHH from EMR_YZDY b left outer join ZY_BQYZ a on b.YZBXH=a.JLXH ");
		try {
			hql.append(" where ").append(ExpressionProcessor.instance().toString(cnd)).append(" order by b.DYYM,b.DYHH");
			list_ret=dao.doSqlQuery(hql.toString(), null);
			SchemaUtil.setDictionaryMassageForList(list_ret, "phis.application.war.schemas.YZDY_ZDH");
		} catch (ExpException e) {
			MedicineUtils.throwsException(logger, "指定行打印信息查询失败", e);
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "指定行打印信息查询失败", e);
		}
		return list_ret;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-7-29
	 * @description 查询是否需要套打参数
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int querySFTD(Context ctx)throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		return MedicineUtils.parseInt(ParameterUtil.getParameter(jgid, "YZBDYSFTD", ctx)) ;//是否需要套打;
	}
}
