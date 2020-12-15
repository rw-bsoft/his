package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
 * @author renwei 2020-08-07
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
		String TJSJFW = request.get("dateFrom").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", ".").concat(" 00:00:00 ").concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", "."))
				.concat(" 23:59:59 ");
		try {
			StringBuffer hql;
			hql = getSqlAndParameters_Doctor(request, JGID,yfsb);
			response.put("TITLE", jgname+"门诊基本药物使用统计");
			ret = dao.doSqlQuery(hql.toString(), null);
			getSumColumn(response);
			if(pDouble(response.get("CFZS_ALL"))!=0){
				response.put("JYBL_ALL", String.format("%.2f", pDouble(response.get("JYFYJE_ALL"))*100/pDouble(response.get("FYJE_ALL")))+"%");
				response.put("FJYBL_ALL", String.format("%.2f", pDouble(response.get("FJYFYJE_ALL"))*100/pDouble(response.get("FYJE_ALL")))+"%");
			}
			response.put("JGMC", jgname);
			response.put("TJSJFW", TJSJFW);
			response.put("ZB", user.getUserName());
			response.put("ZBRQ", BSHISUtil.toString(new Date()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StringBuffer getSqlAndParameters_Doctor(Map<String, Object> request, String jgid,long yfsb) {
		String dateFrom=request.get("dateFrom")+" 00:00:00";
		String dateTo=request.get("dateTo")+" 23:59:59";
		String YFSB=yfsb+"";
		//处方总数、处方金额
		String sql_1 ="(select  count(distinct(a.CFSB)) AS CFZS,sum(b.HJJE) as FYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= '"+dateFrom+"'  and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <='"+dateTo+"')) and a.JGID ='"+jgid+"' and a.YFSB ='"+YFSB+"' and (c.jylx=1 Or c.jylx=2) group by a.ysdm) V1 LEFT JOIN ";
		//基本药物处方数、基本药物处方金额
		String sql_2 = "(select count(distinct(a.CFSB)) AS JYCFZS,sum(b.HJJE) as JYFYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= '"+dateFrom+"' and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"+dateTo+"')) and a.JGID ='"+jgid+"'  and a.YFSB ='"+YFSB+"' and c.jylx=2 group by a.ysdm) V2  ON V1.ysdm=V2.ysdm LEFT JOIN ";
		//非基本药物处方数、非基本药物处方金额
		String sql_3 = "(select count(distinct(a.CFSB)) AS FJYCFZS,sum(b.HJJE) as FJYFYJE,a.ysdm as YSDM,(select d.personname from sys_personnel d where  d.personid=a.ysdm) as YSXM  FROM  MS_CF02 b,MS_CF01 a, YK_TYPK c  where c.YPXH = b.YPXH and a.CFSB = b.CFSB and  a.ZFPB = 0  and (b.ZFYP != 1 and (to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') >= '"+dateFrom+"'  and to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss') <= '"+dateTo+"')) and a.JGID ='"+jgid+"' and a.YFSB ='"+YFSB+"' and c.jylx=1 group by a.ysdm) V3  ON V1.ysdm=V3.ysdm ";

		StringBuffer hql = new StringBuffer("SELECT  v1.ysxm as YSXM,V1.ysdm as YSGH,(nvl(V2.JYCFZS,0)+nvl(V3.FJYCFZS,0)) AS CFZS,V1.FYJE,nvl(V2.jycfzs,0) as jycfzs,nvl(V2.jyfyje,0) as jyfyje,nvl(round(V2.jyfyje*100/V1.fyje,2),'0.00')||'%' AS jybl,nvl(V3.fjycfzs,0) as fjycfzs,nvl(V3.fjyfyje,0) as fjyfyje,nvl(round(V3.fjyfyje*100/V1.fyje,2),'0.00')||'%' AS fjybl FROM ")
		.append(sql_1)
		.append(sql_2)
		.append(sql_3);

		return hql;
	}

	private void getSumColumn(Map<String, Object> response) { //总计
		for(Map<String, Object> m:ret){
			response.put("CFZS_ALL", add_Long(response.get("CFZS_ALL"), m.get("CFZS")));//处方总数
			response.put("FYJE_ALL", add(response.get("FYJE_ALL"), m.get("FYJE")));//处方金额
			response.put("JYCFZS_ALL", add_Long(response.get("JYCFZS_ALL"), m.get("JYCFZS"))); //基本药物处方数
			response.put("JYFYJE_ALL", add(response.get("JYFYJE_ALL"), m.get("JYFYJE"))); //基本药物处方金额 
			response.put("FJYCFZS_ALL", add_Long(response.get("FJYCFZS_ALL"), m.get("FJYCFZS"))); //非基本药物处方数
			response.put("FJYFYJE_ALL", add(response.get("FJYFYJE_ALL"), m.get("FJYFYJE"))); //非基本药物处方金额
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
