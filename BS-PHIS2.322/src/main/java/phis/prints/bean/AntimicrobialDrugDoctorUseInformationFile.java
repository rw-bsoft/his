package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 门诊抗菌药物使用情况
 *
 * @author Zhagnxw
 *
 */
public class AntimicrobialDrugDoctorUseInformationFile implements IHandler {
	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request,
							  Map<String, Object> response, Context ctx) throws PrintException {
		ret.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String TJSJFW = request
				.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."));
		String flag = request.get("flag")+"";
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer hql=new StringBuffer();
			//拼接sql
			if("1".equals(flag)){//部门
				hql = getSqlAndParameters_Ddpt(request, JGID);
				response.put("TITLE", jgname+"门诊抗菌药物处方统计");
			}else{//医生
				hql = getSqlAndParameters_Doctor(request, JGID);
				response.put("TITLE", jgname+"门诊抗菌药物处方统计(医生)");
			}
			ret = dao.doSqlQuery(hql.toString(), null);
			getSumColumn(response);
			if(pDouble(response.get("CFZS_ALL"))!=0){
				response.put("KJYCFBL_ALL", String.format("%.2f", pDouble(response.get("KJYWCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
				response.put("YJKJCFZB_ALL", String.format("%.2f", pDouble(response.get("YJKJCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
				response.put("EJKJCFZB_ALL", String.format("%.2f", pDouble(response.get("EJKJCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
				response.put("SJKJCFZB_ALL", String.format("%.2f", pDouble(response.get("SJKJCFS_ALL"))*100/pDouble(response.get("CFZS_ALL")))+"%");
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
	private StringBuffer getSqlAndParameters_Doctor(Map<String, Object> request, String JGID) {
		String dateFrom=request.get("dateFrom") + " 00:00:00";
		String dateTo=request.get("dateTo") + " 23:59:59";
		String jgid=JGID;
		String ksdm=request.get("ksdm")+"";
		//处方张数、处方金额
		String sql_1 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as CFZS,   sum(a.hjje) as CFJE      from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' and b.ksdm ='"+ksdm+"' group by  c.personid,c.personname) V1 LEFT JOIN";
		//抗菌药物处方张数、抗菌药物处方金额
		String sql_2 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as KJYWCFS,sum(a.hjje) as KJYWCFJE  from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1  and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' and b.ksdm ='"+ksdm+"' group by c.personid,c.personname)V2 ON V1.YSDM=V2.YSDM LEFT JOIN ";
		//一级抗菌药物处方数
		String sql_3 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as YJKJCFS 						    from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1  and yk.kssdj=1 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' and b.ksdm ='"+ksdm+"' group by c.personid,c.personname)V3 ON V1.YSDM=V3.YSDM left join ";
		//二级抗菌药物处方数
		String sql_4 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as EJKJCFS 						    from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1  and yk.kssdj=2 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' and b.ksdm ='"+ksdm+"' group by c.personid,c.personname)V4 ON V1.YSDM=V4.YSDM left join ";
		//三级抗菌药物处方数
		String sql_5 = "(select  c.personid as YSDM,c.personname as YSMC,count(distinct b.cfsb) as SJKJCFS  						from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb  left join sys_personnel c on b.ysdm = c.personid where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1  and yk.kssdj=3 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' and b.ksdm ='"+ksdm+"' group by c.personid,c.personname)V5 ON V1.YSDM=V5.YSDM";

		StringBuffer hql = new StringBuffer("SELECT V1.YSDM as YSDM,V1.YSMC as YSMC,V1.CFZS,V1.CFJE,nvl(V2.KJYWCFS,0) as KJYWCFS,nvl(V2.KJYWCFJE,0) as KJYWCFJE,nvl(round(V2.KJYWCFS*100/V1.CFZS,2),'0.00')||'%' AS KJYCFBL,nvl(V3.YJKJCFS,0) as YJKJCFS,nvl(round(V3.YJKJCFS*100/V1.CFZS,2),'0.00')||'%'as YJKJCFZB,nvl(V4.EJKJCFS,0) as EJKJCFS,nvl(round(V4.EJKJCFS*100/V1.CFZS,2),'0.00')||'%'as EJKJCFZB,nvl(V5.SJKJCFS,0) as SJKJCFS,nvl(round(V5.SJKJCFS*100/V1.CFZS,2),'0.00')||'%'as SJKJCFZB FROM ")
				.append(sql_1)
				.append(sql_2)
				.append(sql_3)
				.append(sql_4)
				.append(sql_5);
		//parameters.put("dateFrom", request.get("dateFrom"));
		//parameters.put("dateTo", request.get("dateTo") );
		//parameters.put("ksdm", request.get("ksdm"));
		//parameters.put("jgid", jGID);
		return hql;
	}


	//拼接sql（部门）
	private StringBuffer getSqlAndParameters_Ddpt(Map<String, Object> request,String JGID) {
		String dateFrom=request.get("dateFrom") + " 00:00:00";
		String dateTo=request.get("dateTo") + " 23:59:59";
		String jgid=JGID;
		//处方张数、处方金额
		String sql_1 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as CFZS,   sum(a.hjje) as CFJE      from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' group by ksdm, OFFICENAME) V1 LEFT JOIN";
		//抗菌药物处方张数、抗菌药物处方金额
		String sql_2 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as KJYWCFS,sum(a.hjje) as KJYWCFJE  from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.ksbz=1 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' group by ksdm, OFFICENAME)V2 ON V1.KSDM=V2.KSDM LEFT JOIN ";
		//一级抗菌药物处方数
		String sql_3 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as YJKJCFS 						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=1 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' group by ksdm, OFFICENAME)V3 ON V1.KSDM=V3.KSDM left join ";
		//二级抗菌药物处方数
		String sql_4 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as EJKJCFS 						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=2 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' group by ksdm, OFFICENAME)V4 ON V1.KSDM=V4.KSDM left join ";
		//三级抗菌药物处方数
		String sql_5 = "(select ksdm,OFFICENAME,count(distinct b.cfsb) as SJKJCFS  						   from ms_cf02 a  left join ms_cf01 b on a.cfsb = b.cfsb left join sys_office c on b.ksdm = c.ID where b.cfsb in (select cfsb from  ms_cf02 cf left join yk_typk yk on cf.ypxh=yk.ypxh where yk.kssdj=3 and cf.gytj!='27' and cf.pspb='0') and  to_char(b.kfrq,'yyyy-mm-dd hh24:mm:ss') between '"+dateFrom+"' and '"+dateTo+"' and a.jgid='"+jgid+"' group by ksdm, OFFICENAME)V5 ON V1.KSDM=V5.KSDM";

		StringBuffer hql = new StringBuffer("SELECT   v1.ksdm as KSDM,V1.OFFICENAME as KS,V1.CFZS,V1.CFJE,nvl(V2.KJYWCFS,0) as KJYWCFS,nvl(V2.KJYWCFJE,0) as KJYWCFJE,nvl(round(V2.KJYWCFS*100/V1.CFZS,2),'0.00')||'%' AS KJYCFBL,nvl(V3.YJKJCFS,0) as YJKJCFS,nvl(round(V3.YJKJCFS*100/V1.CFZS,2),'0.00')||'%'as YJKJCFZB,nvl(V4.EJKJCFS,0) as EJKJCFS,nvl(round(V4.EJKJCFS*100/V1.CFZS,2),'0.00')||'%'as EJKJCFZB,nvl(V5.SJKJCFS,0) as SJKJCFS,nvl(round(V5.SJKJCFS*100/V1.CFZS,2),'0.00')||'%'as SJKJCFZB FROM ")
				.append(sql_1)
				.append(sql_2)
				.append(sql_3)
				.append(sql_4)
				.append(sql_5);
		return hql;
	}

	private void getSumColumn(Map<String, Object> response) {
		for(Map<String, Object> m:ret){
			response.put("CFZS_ALL", add_Long(response.get("CFZS_ALL"), m.get("CFZS")));
			response.put("CFJE_ALL", add(response.get("CFJE_ALL"), m.get("CFJE")));
			response.put("KJYWCFS_ALL", add_Long(response.get("KJYWCFS_ALL"), m.get("KJYWCFS")));
			response.put("KJYWCFJE_ALL", add(response.get("KJYWCFJE_ALL"), m.get("KJYWCFJE")));
			response.put("YJKJCFS_ALL", add_Long(response.get("YJKJCFS_ALL"), m.get("YJKJCFS")));
			response.put("EJKJCFS_ALL", add_Long(response.get("EJKJCFS_ALL"), m.get("EJKJCFS")));
			response.put("SJKJCFS_ALL",add_Long(response.get("SJKJCFS_ALL"), m.get("SJKJCFS")));
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
