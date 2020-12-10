package phis.prints.bean;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSHISUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 住院患者基本药物使用率
 * @author renwei 2020-08-07
 */
public class BasicDrugUseStatistics_ZYCFFile implements IHandler {
	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

	@Override
	public void getParameters(Map<String, Object> request, Map<String, Object> response, Context ctx) throws PrintException {
		ret.clear();
		BaseDAO dao = new BaseDAO(ctx);
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		String jgname = user.getManageUnit().getName();
		String TJSJFW = request.get("dateFrom").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", ".").concat(" 00:00:00 ").concat(" -- ")
				.concat(request.get("dateTo").toString().substring(0, 10).replaceFirst("-", ".").replaceFirst("-", "."))
				.concat(" 23:59:59 ");
		try {
			StringBuffer hql;
			hql = getSqlAndParameters_Doctor(request, JGID);
			if("noResult".equals(hql.toString())){
				response.put("TITLE", jgname + "(当前时间段内无病人出院)");
			}else {
				response.put("TITLE", jgname + "住院患者基本药物使用率");
				ret = dao.doSqlQuery(hql.toString(), null);
			}
			response.put("JGMC", jgname);
			response.put("TJSJFW", TJSJFW);
			response.put("ZB", user.getUserName());
			response.put("ZBRQ", BSHISUtil.toString(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StringBuffer getSqlAndParameters_Doctor(Map<String, Object> request, String jgid) throws PersistentDataOperationException {
		String dateFrom=request.get("dateFrom")+" 00:00:00";
		String dateTo=request.get("dateTo")+" 23:59:59";
		//判断时间段内是否有人出院，否则会造成除数为0
		String isCY="select * from zy_brry where jgid='"+jgid+"' and to_char(cyrq,'yyyy-mm-dd hh24:mi:ss')>='"+dateFrom+"' " +
				" and to_char(cyrq,'yyyy-mm-dd hh24:mi:ss')<='"+dateTo+"' and cypb='8'";
		BaseDAO dao = new BaseDAO();
		List<Map<String,Object>> brcy = dao.doSqlQuery(isCY, null);
		if(brcy.size()>0) {
			//出院总人数、出院药品总金额
			String sql_1 = "(select count(distinct zyh) as CYZRS,sum(zjje) as CYYPZJE from " +
					"(select distinct a.fymc,a.jgid,a.zyh,a.fyxh,a.zjje,b.jylx from zy_fymx a,yk_typk b,zy_bqyz c,zy_brry d where a.fyxh=b.ypxh and a.jgid=c.jgid and a.zyh=c.zyh " +
					"and a.jgid=d.jgid and a.zyh=d.zyh and a.jgid='" + jgid + "' and a.xmlx='2'  and to_char(d.cyrq,'yyyy-mm-dd hh24:mi:ss')>='" + dateFrom + "' " +
					"and to_char(d.cyrq,'yyyy-mm-dd hh24:mi:ss')<='" + dateTo + "' and d.cypb='8')) m,";
			//基本药物出院总人数、基本药物出院总金额
			String sql_2 = "(select count(distinct zyh) as JYCYRS,sum(zjje) as JYCYYPZJE from " +
					"(select distinct a.fymc,a.jgid,a.zyh,a.fyxh,a.zjje,b.jylx from zy_fymx a,yk_typk b,zy_bqyz c,zy_brry d where a.fyxh=b.ypxh and a.jgid=c.jgid and a.zyh=c.zyh " +
					"and a.jgid=d.jgid and a.zyh=d.zyh and a.jgid='" + jgid + "' and a.xmlx='2'  and to_char(d.cyrq,'yyyy-mm-dd hh24:mi:ss')>='" + dateFrom + "' " +
					"and to_char(d.cyrq,'yyyy-mm-dd hh24:mi:ss')<='" + dateTo + "' and d.cypb='8' and jylx='2')) n";
			//非基本药物出院总人数=出院总人数-基本药物出院总人数    非基本药物出院总金额=出院药品总金额-基本药物出院总金额
			StringBuffer hql = new StringBuffer("select m.cyzrs as CYZRS,n.jycyrs as JYCYRS,m.cyypzje as CYYPZJE,n.jycyypzje as JYCYYPZJE,(nvl(m.cyzrs,0)-nvl(n.jycyrs,0)) as FJYCYRS," +
					"(nvl(m.cyypzje,0)-nvl(n.jycyypzje,0)) as FJYCYYPZJE,nvl(round(n.jycyrs*100/m.cyzrs,2),'0.00')||'%' as ZYJYBL," +
					"nvl(round((nvl(m.cyzrs,0)-nvl(n.jycyrs,0))*100/m.cyzrs,2),'0.00')||'%' as ZYFJYBL from")
					.append(sql_1)
					.append(sql_2);
			return hql;
		}else{
			StringBuffer result = new StringBuffer("noResult");
			return result;
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
