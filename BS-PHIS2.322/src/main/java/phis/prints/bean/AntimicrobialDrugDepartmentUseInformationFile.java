package phis.prints.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSHISUtil;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * 抗菌药物住院科室使用情况
 * 
 * @author Bollen
 * 
 */
public class AntimicrobialDrugDepartmentUseInformationFile implements IHandler {
	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		ret.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String dateFrom = request.get("dateFrom") + "";
		String dateTo = request.get("dateTo") + "";
		String TJSJFW = request
				.get("dateFrom")
				.toString()
				.substring(0, 10)
				.replaceFirst("-", ".")
				.replaceFirst("-", ".")
				.concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10)
						.replaceFirst("-", ".").replaceFirst("-", "."));

		try {
			//出院病人人数, 出院判别 | 0：在院病人 1：出院证明 2：预结出院 8：正常出院 9：终结出院 99 注销出院 (0 ,1都算在院)
			String sql_brrs = "select brks as BRKS ,ksmc as ksmc ,count(*) as BRRS from zy_brry left join gy_ksdm on zy_brry.brks=gy_ksdm.ksdm" +
							" WHERE   to_char(cyrq,'yyyy-mm-dd') between '"
							+ dateFrom
							+ "' and '"
							+ dateTo+"' and zy_brry.jgid like '"+jgid+"%' and zy_brry.cypb not in(0,1,99) group by brks,ksmc" ; 
			List<Map<String,Object>> brrs = dao.doSqlQuery(sql_brrs, null);
			String sql_cybr = "select zyh from zy_brry" +
							" WHERE   to_char(cyrq,'yyyy-mm-dd') between '"
							+ dateFrom
							+ "' and '"
							+ dateTo+"' and jgid like '"+jgid+"%' and zy_brry.cypb not in(0,1,99)" ;
			//查询出院病人中使用了抗菌药物人数
			String sql_sykjybrrs ="select BRKS as BRKS,count(*) as KJYWSYRS from (select distinct c.BRKS,a.zyh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+"))group by BRKS ";
			List<Map<String,Object>> sykjybrrs = dao.doSqlQuery(sql_sykjybrrs, null);
			//一级抗菌药使用人数
			String sql_yjkjysyrs ="select BRKS as BRKS,count(*) as YJSYRS from (select distinct c.BRKS,a.zyh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+") and kssdj=1) group by BRKS ";
			List<Map<String,Object>> yjkjybrrs = dao.doSqlQuery(sql_yjkjysyrs, null);
			//二级抗菌药使用人数
			String sql_ejkjysyrs ="select BRKS as BRKS,count(*) as EJSYRS from (select distinct c.BRKS,a.zyh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+") and kssdj=2) group by BRKS ";
			List<Map<String,Object>> ejkjybrrs = dao.doSqlQuery(sql_ejkjysyrs, null);
			//三级抗菌药使用人数
			String sql_sjkjysyrs ="select BRKS as BRKS,count(*) as SJSYRS from (select distinct c.BRKS,a.zyh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+") and kssdj=3) group by BRKS ";
			List<Map<String,Object>> sjkjysyrs = dao.doSqlQuery(sql_sjkjysyrs, null);
			//抗菌药物使用品种(按科室)
			String sql_kjywsypz="select BRKS as BRKS,count(*) as KJYWSYPZ from (select distinct c.BRKS,a.ypxh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+") ) group by BRKS ";
			List<Map<String,Object>> kjywsypz = dao.doSqlQuery(sql_kjywsypz, null);
			//抗菌药物使用品种(全院)
			String sql_kjywsypz_all="select count(*) as KJYWSYPZ from (select distinct a.ypxh from (select fyxh as ypxh,zyh from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1 and a.zyh in("+sql_cybr+") )  ";
			List<Map<String,Object>> kjywsypz_all = dao.doSqlQuery(sql_kjywsypz_all, null);
			//药品总费用
			String sql_ypzfy="select c.BRKS as BRKS,round(sum(a.fyje),2) as YWZFY from (select fyxh as ypxh,zyh,ZJJE as fyje from ZY_FYMX where yplx<>0) a left join zy_brry c on a.zyh=c.zyh where a.zyh in("+sql_cybr+") group by c.BRKS ";
			List<Map<String,Object>> ypzfy = dao.doSqlQuery(sql_ypzfy, null);
			//抗菌药物总费用
			String sql_kjyzfy="select c.BRKS as BRKS,round(sum(a.fyje),2) as KJYWZFY from (select fyxh as ypxh,zyh,ZJJE as fyje  from ZY_FYMX where yplx<>0) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where b.ksbz=1  and a.zyh in("+sql_cybr+") group by c.BRKS ";
			List<Map<String,Object>> kjyzfy = dao.doSqlQuery(sql_kjyzfy, null);
			//总费用
			String sql_zfy="select c.BRKS as BRKS,round(sum(a.fyje),2) as ZSR from (select fyxh as ypxh,zyh,ZJJE as fyje  from ZY_FYMX) a left join yk_typk b on a.ypxh=b.ypxh left join zy_brry c on a.zyh=c.zyh where  a.zyh in("+sql_cybr+") group by c.BRKS ";
			List<Map<String,Object>> zfy = dao.doSqlQuery(sql_zfy, null);
			//住院人天数
			String sql_zyrts = "select brks ,sum(trunc(cyrq)-trunc(ryrq)+1) as ZYRTS from zy_brry  " +
					" WHERE   to_char(cyrq,'yyyy-mm-dd') between '"
					+ dateFrom
					+ "' and '"
					+ dateTo+"' and zy_brry.jgid like '"+jgid+"%' and zy_brry.cypb not in(0,1,99) group by brks" ; 
			List<Map<String,Object>> zyrts = dao.doSqlQuery(sql_zyrts, null);
			
			ret.addAll(brrs);
			map_Splice(ret, sykjybrrs);
			map_Splice(ret, yjkjybrrs);
			map_Splice(ret, ejkjybrrs);
			map_Splice(ret, sjkjysyrs);
			map_Splice(ret, kjywsypz);
			map_Splice(ret, ypzfy);
			map_Splice(ret, kjyzfy);
			map_Splice(ret, zfy);
			map_Splice(ret, zyrts);
			//把结果集里面没有值的指标都设置为零
			setEmptToZero(response);
			response.put("KJYWSYPZ_ALL", kjywsypz_all.get(0).get("KJYWSYPZ"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(pDouble(response.get("BRRS_ALL"))!=0){
			response.put("KJYWSYL_ALL", String.format("%.2f", pDouble(response.get("KJYWSYRS_ALL"))*100/pDouble(response.get("BRRS_ALL")))+"%");
			response.put("YJSYL_ALL", String.format("%.2f", pDouble(response.get("YJSYRS_ALL"))*100/pDouble(response.get("BRRS_ALL")))+"%");
			response.put("EJSYL_ALL", String.format("%.2f", pDouble(response.get("EJSYRS_ALL"))*100/pDouble(response.get("BRRS_ALL")))+"%");
			response.put("SJSYL_ALL", String.format("%.2f", pDouble(response.get("SJSYRS_ALL"))*100/pDouble(response.get("BRRS_ALL")))+"%");
			
			response.put("PJSYPZS_ALL", String.format("%.2f", pDouble(response.get("KJYWSYPZ_ALL"))/pDouble(response.get("BRRS_ALL"))));
			response.put("KJYWRJFY_ALL", String.format("%.2f", pDouble(response.get("KJYWZFY_ALL"))/pDouble(response.get("BRRS_ALL"))));
		}
		if(!(response.get("YWZFY_ALL")+"").equals("0") && response.get("YWZFY_ALL")!=null){
			response.put("KJYWZYB_ALL", String.format("%.2f", pDouble(response.get("KJYWZFY_ALL"))*100/pDouble(response.get("YWZFY_ALL")))+"%");
		}else{
			response.put("KJYWZYB_ALL","0.00%");
		}
		if(!(response.get("ZSR_ALL")+"").equals("0") && response.get("ZSR_ALL")!=null){
			response.put("KJYWZZSR_ALL", String.format("%.2f", pDouble(response.get("KJYWZFY_ALL"))*100/pDouble(response.get("ZSR_ALL")))+"%");
		}else{
			response.put("KJYWZZSR_ALL","0.00%");
		}
		response.put("TITLE", jgname+"住院抗菌药物使用统计");  
		response.put("TJSJFW", TJSJFW);
		response.put("ZB", user.getUserName());
		response.put("ZBRQ", BSHISUtil.toString(new Date()));
	}

	/**
	 * 把结果集里面没有值的指标都设置为零，并计算各项指标的和
	 */
	private void setEmptToZero(Map<String,Object> response) {
		for(Map<String,Object> m : ret){
			
			if((m.get("BRRS")+"").equals("null")){
				m.put("BRRS","0");
			}
			response.put("BRRS_ALL", add(response.get("BRRS_ALL"), m.get("BRRS")));
			if((m.get("KJYWSYRS")+"").equals("null")){
				m.put("KJYWSYRS","0");
			}
			response.put("KJYWSYRS_ALL", add(response.get("KJYWSYRS_ALL"), m.get("KJYWSYRS")));
			if((m.get("YJSYRS")+"").equals("null")){
				m.put("YJSYRS","0");
			}
			
			response.put("YJSYRS_ALL", add(response.get("YJSYRS_ALL"), m.get("YJSYRS")));
			if((m.get("EJSYRS")+"").equals("null")){
				m.put("EJSYRS","0");
			}
			response.put("EJSYRS_ALL", add(response.get("EJSYRS_ALL"), m.get("EJSYRS")));
			if((m.get("SJSYRS")+"").equals("null")){
				m.put("SJSYRS","0");
			}
			response.put("SJSYRS_ALL", add(response.get("SJSYRS_ALL"), m.get("SJSYRS")));
			
			if((m.get("KJYWSYPZ")+"").equals("null")){
				m.put("KJYWSYPZ","0");
			}
//			response.put("KJYWSYPZ_ALL", add(response.get("KJYWSYPZ_ALL"), m.get("KJYWSYPZ")));
			if((m.get("YWZFY")+"").equals("null")){
				m.put("YWZFY","0");
			}
			response.put("YWZFY_ALL", add(response.get("YWZFY_ALL"), m.get("YWZFY")));
			if((m.get("KJYWZFY")+"").equals("null")){
				m.put("KJYWZFY","0");
			}
			response.put("KJYWZFY_ALL",add(response.get("KJYWZFY_ALL"), m.get("KJYWZFY")));
			if((m.get("ZSR")+"").equals("null")){
				m.put("ZSR","0");
			}
			response.put("ZSR_ALL",add(response.get("ZSR_ALL"), m.get("ZSR")));
			if((m.get("ZYRTS")+"").equals("null")){
				m.put("ZYRTS","0");
			}
			response.put("ZYRTS_ALL",add(response.get("ZYRTS_ALL"), m.get("ZYRTS")));
			
			m.put("KJYWSYL", String.format("%.2f", pDouble(m.get("KJYWSYRS"))*100/pDouble(m.get("BRRS")))+"%");
			m.put("YJSYL", String.format("%.2f", pDouble(m.get("YJSYRS"))*100/pDouble(m.get("BRRS")))+"%");
			m.put("EJSYL", String.format("%.2f", pDouble(m.get("EJSYRS"))*100/pDouble(m.get("BRRS")))+"%");
			m.put("SJSYL", String.format("%.2f", pDouble(m.get("SJSYRS"))*100/pDouble(m.get("BRRS")))+"%");
			
			m.put("PJSYPZS", String.format("%.2f", pDouble(m.get("KJYWSYPZ"))/pDouble(m.get("BRRS"))));
			if(!m.get("YWZFY").toString().equals("0")){
				m.put("KJYWZYB", String.format("%.2f", pDouble(m.get("KJYWZFY"))*100/pDouble(m.get("YWZFY")))+"%");
			}else{
				m.put("KJYWZYB", "0.00%");
			}
			if(!m.get("ZSR").toString().equals("0")){
				m.put("KJYWZZSR", String.format("%.2f", pDouble(m.get("KJYWZFY"))*100/pDouble(m.get("ZSR")))+"%");
			}else{
				m.put("KJYWZZSR", "0.00%");
			}
			m.put("KJYWRJFY", String.format("%.2f", pDouble(m.get("KJYWZFY"))/pDouble(m.get("BRRS"))));
			
		}
	}

	/**
	 * 拼接整合map对象,得到结果集
	 * @param cfzx
	 * @param kjywcfs
	 * @return 
	 */
	private  AntimicrobialDrugDepartmentUseInformationFile map_Splice(List<Map<String, Object>> ret,
			List<Map<String, Object>> ret_child) {
		for(Map<String,Object> m1 : ret){
			for(Map<String,Object> m2: ret_child){
//				System.out.println(m1.get("BRKS").toString().equals(m2.get("BRKS"));
				if(m1.get("BRKS").toString().equals(m2.get("BRKS").toString())){
					m1.putAll(m2);
				}
			}
		}
		return this;
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
}
