package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;
import ctd.print.IHandler;
import ctd.print.PrintException;

public class ChineseMedicineProjectFile implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		UserRoleToken user = UserRoleToken.getCurrent();
		response.put("Title", user.getManageUnitName()+"中医创建项目统计");
		response.put("BeginDate", request.get("dateFrom"));
		response.put("EndDate", request.get("dateTo"));
		response.put("Lister", user.getUserName());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		response.put("DateTabling", sdf.format(new Date()));
		String counttype=(String) request.get("counttype");
		Boolean totalflag=counttype.indexOf("9")>=0?true:false;
		String[] countarr=counttype.split(",");
		String tjfs="";
		if(totalflag){
			tjfs+="全部";
		}else{
			for(int m=0;m<countarr.length;m++){
				if(countarr[m].equals("1")){
					tjfs+=" 0-3岁儿童";
				}else if (countarr[m].equals("2")){
					tjfs+=" 65岁及以上老年人";
				}else if (countarr[m].equals("3")){
					tjfs+=" 高血压";
				}else if (countarr[m].equals("4")){
					tjfs+=" 糖尿病";
				}
				}
			}
	
		response.put("TJFS", tjfs);
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnitId()+"%";
			String dateFrom = (String) request.get("dateFrom");
			String dateTo = (String) request.get("dateTo");
			String counttype=(String) request.get("counttype");
			Boolean totalflag=counttype.indexOf("9")>=0?true:false;
			String[] countarr=counttype.split(",");
			List<Map<String, Object>> l_data = new ArrayList<Map<String, Object>>();
			BaseDAO dao = new BaseDAO(ctx);
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("jgid", jgid);
			parameters.put("dateTo", dateTo);
			parameters.put("dateFrom", dateFrom);
			HashMap<String, Map<String, Object>> datamap=new HashMap<String, Map<String, Object>>();
			String sql="select a.organizcode as ORGANIZCODE,a.organizname as ORGANIZNAME"+
					" from sys_organization a where a.organizcode like :jgid and a.logoff='0' order by a.organizcode";
			Map<String, Object> pa = new HashMap<String, Object>();
			Map<String, Object> total = new HashMap<String, Object>();
			total.put("JGMC", "合计");
			for(int i=1;i<=6;i++){
				total.put("XM"+i, 0);
			}
			total.put("XMALL", 0);
			pa.put("jgid", jgid);
			List<Map<String, Object>> jglist = new ArrayList<Map<String,Object>>();
			jglist=dao.doSqlQuery(sql, pa);
			for(Map<String, Object> one : jglist ){
				HashMap<String ,Object> temp=new HashMap<String ,Object>();
				temp.put("JGMC",one.get("ORGANIZNAME")+"");
				for(int i=1;i<=6;i++){
					temp.put("XM"+i, 0);
				}
				temp.put("XMALL", 0);
				datamap.put(one.get("ORGANIZCODE")+"", temp);
			}
			StringBuffer hql = new StringBuffer();
			String or="";
			String where1=" where a.mzxh=b.mzxh and a.yjxh=d.yjxh and d.ylxh=e.fyxh and a.brid=da.brid and" +
						" a.jgid like :jgid and b.sfrq >=to_date(:dateFrom,'yyyy-mm-dd')"+
						" and b.sfrq <=to_date(:dateTo,'yyyy-mm-dd') and e.zycjgb is not null";
			String where2=" where a.mzxh=c.mzxh and a.yjxh=d.yjxh and d.ylxh=e.fyxh and a.brid=da.brid and" +
						" a.jgid like :jgid and c.zfrq >=to_date(:dateFrom,'yyyy-mm-dd')"+
						" and c.zfrq <=to_date(:dateTo,'yyyy-mm-dd') and e.zycjgb is not null";
			if(totalflag){
					or="floor(months_between(sysdate,da.csny)/12)<=3 or floor(months_between(sysdate,da.csny)/12)>=65"
							+" or exists (select 1 from mdc_hypertensionrecord h where da.empiid=h.empiid and h.status='0')"
							+" or exists (select 1 from mdc_diabetesrecord t where da.empiid=t.empiid and t.status='0')";
			}else{
					for(int m=0;m<countarr.length;m++){
						if(countarr[m].equals("1")){
							or+="or floor(months_between(sysdate,da.csny)/12)<=3";
						}else if (countarr[m].equals("2")){
							or+="or floor(months_between(sysdate,da.csny)/12)>=65";
						}else if (countarr[m].equals("3")){
							or+="or exists (select 1 from mdc_hypertensionrecord h where da.empiid=h.empiid and h.status='0')";
						}else if (countarr[m].equals("4")){
							or+="or exists (select 1 from mdc_diabetesrecord t where da.empiid=t.empiid and t.status='0')";
						}
					}
					or =or.substring(2);
				}
				where1 =where1 +" and ("+or+")";
				where2 =where2 +" and ("+or+")";
				hql.append("select jgid as JGID,zycjgb as ZYCJGB ,sum(ylsl) as YLSL from ("+
						" select d.jgid,e.zycjgb,d.ylsl as ylsl"+
						" from ms_yj01 a,ms_yj02 d,gy_ylsf e,ms_brda da,ms_mzxx b "+where1+
						" union all"+
						" select d.jgid,e.zycjgb,-d.ylsl as ylsl"+
						" from ms_yj01 a,ms_yj02 d,gy_ylsf e,ms_brda da,ms_zffp c "+where2+
						" ) group by jgid,zycjgb order by jgid");
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				list=dao.doSqlQuery(hql.toString(),parameters);
				for(Map<String, Object>  one :list){
					datamap.get(one.get("JGID")+"").put("XM"+one.get("ZYCJGB"), 
								Long.parseLong(datamap.get(one.get("JGID")+"").get("XM"+one.get("ZYCJGB"))+"")
								+Long.parseLong(one.get("YLSL")+""));
					datamap.get(one.get("JGID")+"").put("XMALL", 
							Long.parseLong(datamap.get(one.get("JGID")+"").get("XMALL")+"")
							+Long.parseLong(one.get("YLSL")+""));
					total.put("XM"+one.get("ZYCJGB"), Long.parseLong(total.get("XM"+one.get("ZYCJGB"))+"")+Long.parseLong(one.get("YLSL")+""));
					total.put("XMALL",Long.parseLong(total.get("XMALL")+"")+Long.parseLong(one.get("YLSL")+""));
				}
			
			Iterator iter = datamap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				l_data.add((HashMap<String, Object>)entry.getValue());
			}
			l_data.add(total);
			records.addAll(l_data);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
