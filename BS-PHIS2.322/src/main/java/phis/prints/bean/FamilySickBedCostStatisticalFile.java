package phis.prints.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilySickBedCostStatisticalFile implements IHandler {
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		int tjfs=MedicineUtils.parseInt(request.get("tjfs"));
		StringBuffer sql=new StringBuffer();
		List<Map<String,Object>> l=getXMS(MedicineUtils.parseString(request.get("dateFrom")),MedicineUtils.parseString(request.get("dateTo")),ctx);
		if(l==null||l.size()==0){
			return;
		}
		if(tjfs==1){//按病人
			sql.append("select c.ZYHM as HM,c.BRXM as XM,sum(case when a.YPLX>0 then a.ZJJE else 0 end) as YPFY,sum(a.ZJJE) as ZJJE ");
			for(int i=0;i<l.size();i++){
				Map<String,Object> m=l.get(i);
				sql.append(",sum(case when a.FYXM="+m.get("SFXM")+" then a.ZJJE else 0 end) as XM"+(i+1));
			}
			sql.append(" from JC_FYMX a,GY_SFXM b,JC_BRRY c where a.FYXM=b.SFXM and a.ZYH=c.ZYH and a.JGID=:jgid and a.FYRQ>=:dateF and a.FYRQ<=:dateT group by c.ZYHM,c.BRXM  ");
		}else{//按医生
			sql.append("select c.PERSONID as HM,c.PERSONNAME as XM,sum(case when a.YPLX>0 then a.ZJJE else 0 end) as YPFY,sum(a.ZJJE) as ZJJE ");
			for(int i=0;i<l.size();i++){
				Map<String,Object> m=l.get(i);
				sql.append(",sum(case when a.FYXM="+m.get("SFXM")+" then a.ZJJE else 0 end) as XM"+(i+1));
			}
			sql.append(" from JC_FYMX a,GY_SFXM b,SYS_Personnel c where a.FYXM=b.SFXM and a.YSGH=c.PERSONID and a.JGID=:jgid and a.FYRQ>=:dateF and a.FYRQ<=:dateT group by c.PERSONID,c.PERSONNAME  ");
		}
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> map_par=new HashMap<String,Object>();
		try {
			map_par.put("dateF", sdfdate.parse(request.get("dateFrom")+" 00:00:00"));
			map_par.put("dateT", sdfdate.parse(request.get("dateTo")+" 23:59:59"));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnitId());
			List<Map<String,Object>> li=dao.doSqlQuery(sql.toString(), map_par);
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					Map<String,Object> m=li.get(i);
					if(MedicineUtils.parseDouble(m.get("ZJJE"))==0){
						li.remove(i);
						i--;
						continue;
					}
					m.put("YPBL",MedicineUtils.formatDouble(2, MedicineUtils.parseDouble(m.get("YPFY"))/MedicineUtils.parseDouble(m.get("ZJJE"))*100) +"%");
				}
			}
			records.addAll(li);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new PrintException(9000,"日期参数转换失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new PrintException(9000,"数据查询失败");
		}
		
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("TITLE", user.getManageUnit().getName()+"家庭病床费用统计");
		response.put("DATE", request.get("dateFrom")+"---"+request.get("dateTo"));
		int tjfs=MedicineUtils.parseInt(request.get("tjfs"));
		if(tjfs==1){
			response.put("HM", "家床号");
			response.put("XM", "病人姓名");
		}else{
			response.put("HM", "医生工号");
			response.put("XM", "医生姓名");
		}
		List<Map<String,Object>> l=getXMS(MedicineUtils.parseString(request.get("dateFrom")),MedicineUtils.parseString(request.get("dateTo")),ctx);
		if(l!=null&&l.size()>0){
			for(int i=0;i<l.size();i++){
				response.put("XM"+(i+1), l.get(i).get("SFMC"));
			}
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2015-7-6
	 * @description 查询时间段里面所有大项目
	 * @updateInfo
	 * @param dateFrom
	 * @param dateTo
	 * @param ctx
	 * @return
	 * @throws PrintException
	 */
	public List<Map<String,Object>> getXMS(String dateFrom,String dateTo, Context ctx) throws PrintException{
		BaseDAO dao = new BaseDAO(ctx);
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer hql=new StringBuffer();
		hql.append("select distinct b.SFMC as SFMC,b.SFXM as SFXM from JC_FYMX a,GY_SFXM b where a.FYXM=b.SFXM and a.JGID=:jgid and a.FYRQ>=:dateF and a.FYRQ<=:dateT  order by b.SFXM");
		Map<String,Object> map_par=new HashMap<String,Object>();
		List<Map<String,Object>> l=null;
		try {
			map_par.put("dateF", sdfdate.parse(dateFrom+" 00:00:00"));
			map_par.put("dateT", sdfdate.parse(dateTo+" 23:59:59"));
			map_par.put("jgid", UserRoleToken.getCurrent().getManageUnitId());
			l=dao.doSqlQuery(hql.toString(), map_par);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new PrintException(9000,"日期参数转换失败");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new PrintException(9000,"数据查询失败");
		}
		return l;
		
	}
}
