package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 门诊基本药物使用统计
 * 
 * @author renwei 2020-08-07
 * 
 */
public class BasicDrugUseStatistics_MZFile implements IHandler {
	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		ret.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		long yfsb=MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		String TJSJFW = request
				.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" 00:00:00 ")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."))
						.concat(" 23:59:59 ");
		//String flag = request.get("flag")+"";
		System.out.println("TJSJFW="+TJSJFW);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer hql=new StringBuffer();
			//拼接sql
			//if("1".equals(flag)){//部门
			//	 hql = getSqlAndParameters_Ddpt(request, JGID, parameters);		
			//	 response.put("TITLE", jgname+"门诊基本药物使用统计");
			//}else{//医生
				 hql = getSqlAndParameters_Doctor(request, JGID, parameters,yfsb);	 
				 response.put("TITLE", jgname+"门诊基本药物使用统计");
			//}				
			ret = dao.doSqlQuery(hql.toString(), parameters);
			getSumColumn(response);
			if(pDouble(response.get("CFZS_ALL"))!=0){
				response.put("JYBL_ALL", String.format("%.2f", pDouble(response.get("JYFYJE_ALL"))*100/pDouble(response.get("FYJE_ALL")))+"%");
				response.put("FJYBL_ALL", String.format("%.2f", pDouble(response.get("FJYFYJE_ALL"))*100/pDouble(response.get("FYJE_ALL")))+"%");
				//response.put("EJKJCFZB_ALL", String.format("%.2f", pDouble(response.get("EJKJCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
				//response.put("SJKJCFZB_ALL", String.format("%.2f", pDouble(response.get("SJKJCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
			}
			response.put("JGMC", jgname);
			response.put("TJSJFW", TJSJFW);
			response.put("ZB", user.getUserName());
			response.put("ZBRQ", BSHISUtil.toString(new Date()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//拼接sql（医生）
	private StringBuffer getSqlAndParameters_Doctor(
			Map<String, Object> request, String jGID,
			Map<String, Object> parameters,long yfsb) {
		//处方总数、处方金额
		//String sql_1 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as CFZS,   sum(a.hjje) as CFJE      from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid and b.ksdm =:ksdm group by  c.personid,c.personname) V1 LEFT JOIN";
		String sql_1 ="(select  count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= :dateFrom  and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <=:dateTo)) and a.JGID =:jgid and a.YFSB =:yfsb and (c.jylx=1 Or c.jylx=2) group by a.ysdm order by ysdm) V1 LEFT JOIN ";
		//基本药物处方数、基本药物处方金额
		String sql_2 = "(select count(distinct(a.CFSB)) AS JYCFZS,sum(b.HJJE) as JYFYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= :dateFrom  and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <= :dateTo)) and a.JGID =:jgid  and a.YFSB =:yfsb and c.jylx=2 group by a.ysdm order by ysdm) V2  ON V1.ysdm=V2.ysdm LEFT JOIN ";
		//String sql_2 = "(select  count(distinct(a.CFSB)) AS jycfzs,sum(b.HJJE) as jyfyje,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0   and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= :dateFrom and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <=:dateTo)) and a.JGID =:jgid and (a.cflx=1) group by a.ysdm order by ysdm)V2 ON V1.ysdm=V2.ysdm LEFT JOIN ";
		//非基本药物处方数、非基本药物处方金额
		String sql_3 = "(select count(distinct(a.CFSB)) AS FJYCFZS,sum(b.HJJE) as FJYFYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= :dateFrom  and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <= :dateTo)) and a.JGID =:jgid and a.YFSB =:yfsb and c.jylx=1 group by a.ysdm order by ysdm) V3  ON V1.ysdm=V3.ysdm ";
		
		//非基本药物处方数
		//String sql_4 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as EJKJCFS 						    from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.jylx=2 ) and  to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid and b.ksdm =:ksdm group by c.personid,c.personname)V4 ON V1.YSDM=V4.YSDM left join ";
		
		StringBuffer hql = new StringBuffer("SELECT  v1.ysxm as YSXM,V1.ysdm as YSGH,(nvl(V2.JYCFZS,0)+nvl(V3.FJYCFZS,0)) AS CFZS,V1.FYJE,nvl(V2.jycfzs,0) as jycfzs,nvl(V2.jyfyje,0) as jyfyje,nvl(round(V2.jyfyje*100/V1.fyje,2),'0.00')||'%' AS jybl,nvl(V3.fjycfzs,0) as fjycfzs,nvl(V3.fjyfyje,0) as fjyfyje,nvl(round(V3.fjyfyje*100/V1.fyje,2),'0.00')||'%' AS fjybl FROM ")
		.append(sql_1)
		.append(sql_2)
		.append(sql_3);
		//.append(sql_4);
		//.append(sql_5);
		parameters.put("dateFrom", request.get("dateFrom")+" 00:00:00");
		parameters.put("dateTo", request.get("dateTo")+" 23:59:59" );
		//parameters.put("ysgh", request.get("ysgh"));
		parameters.put("jgid", jGID);
		parameters.put("yfsb", yfsb);
		return hql;
	}

	
	//拼接sql（部门）
	/*private StringBuffer getSqlAndParameters_Ddpt(Map<String, Object> request,
			String JGID, Map<String, Object> parameters) {
		//处方张数、处方金额
		String sql_1 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as CFZS,   sum(a.hjje) as CFJE      from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid group by ksdm, OFFICENAME) V1 LEFT JOIN";
		//抗菌药物处方张数、抗菌药物处方金额
		String sql_2 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as KJYWCFS,sum(a.hjje) as KJYWCFJE  from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1  ) and  to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid group by ksdm, OFFICENAME)V2 ON V1.KSDM=V2.KSDM LEFT JOIN ";
		//一级抗菌药物处方数
		String sql_3 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as YJKJCFS 						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=1 ) and  to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid group by ksdm, OFFICENAME)V3 ON V1.KSDM=V3.KSDM left join ";
		//二级抗菌药物处方数
		String sql_4 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as EJKJCFS 						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=2 ) and  to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid group by ksdm, OFFICENAME)V4 ON V1.KSDM=V4.KSDM left join ";
		//三级抗菌药物处方数
		String sql_5 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as SJKJCFS  						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=3 ) and  to_char(b.kfrq,'yyyy-mm-dd') between :dateFrom and :dateTo and a.jgid=:jgid group by ksdm, OFFICENAME)V5 ON V1.KSDM=V5.KSDM";

		StringBuffer hql = new StringBuffer("SELECT   v1.ksdm as KSDM,V1.OFFICENAME as KS,V1.CFZS,V1.CFJE,nvl(V2.KJYWCFS,0) as KJYWCFS,nvl(V2.KJYWCFJE,0) as KJYWCFJE,nvl(round(V2.KJYWCFS*100/V1.CFZS,2),'0.00')||'%' AS KJYCFBL,nvl(V3.YJKJCFS,0) as YJKJCFS,nvl(round(V3.YJKJCFS*100/V1.CFZS,2),'0.00')||'%'as YJKJCFZB,nvl(V4.EJKJCFS,0) as EJKJCFS,nvl(round(V4.EJKJCFS*100/V1.CFZS,2),'0.00')||'%'as EJKJCFZB,nvl(V5.SJKJCFS,0) as SJKJCFS,nvl(round(V5.SJKJCFS*100/V1.CFZS,2),'0.00')||'%'as SJKJCFZB FROM ")
		.append(sql_1)
		.append(sql_2)
		.append(sql_3)
		.append(sql_4)
		.append(sql_5);
		parameters.put("dateFrom", request.get("dateFrom") + "");
		parameters.put("dateTo", request.get("dateTo") + "");
		parameters.put("jgid", JGID);
		return hql;
	}*/

	private void getSumColumn(Map<String, Object> response) { //总计
		for(Map<String, Object> m:ret){
			response.put("CFZS_ALL", add_Long(response.get("CFZS_ALL"), m.get("CFZS")));//处方总数
			response.put("FYJE_ALL", add(response.get("FYJE_ALL"), m.get("FYJE")));//处方金额
			response.put("JYCFZS_ALL", add_Long(response.get("JYCFZS_ALL"), m.get("JYCFZS"))); //基本药物处方数
			response.put("JYFYJE_ALL", add(response.get("JYFYJE_ALL"), m.get("JYFYJE"))); //基本药物处方金额 
			response.put("FJYCFZS_ALL", add_Long(response.get("FJYCFZS_ALL"), m.get("FJYCFZS"))); //非基本药物处方数
			response.put("FJYFYJE_ALL", add(response.get("FJYFYJE_ALL"), m.get("FJYFYJE"))); //非基本药物处方金额
			//response.put("SJKJCFS_ALL",add_Long(response.get("SJKJCFS_ALL"), m.get("SJKJCFS")));
		}
	}


	//用bigDecimal做浮点数加法计算
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		records.addAll(ret);
	}
	private double pDouble(Object o){
		if(o==null){
			return 0;
		}
		return Double.parseDouble(o+"");
	}
	
	//用bigDecimal做浮点数加法计算
	private String add(Object x,Object y){
		if(x==null){
			x=0;
		}
		if(y==null){
			y=0;
		}
		double ret = new BigDecimal(x.toString()).add(new BigDecimal(y.toString())).doubleValue();
		return ret+"";
	}
	
	private String add_Long(Object x,Object y){
		if(x==null){
			x="0";
		}
		if(y==null){
			y="0";
		}
		Long ret = Long.parseLong(x+"")+Long.parseLong(y+"");
		return ret+"";
	}
}
