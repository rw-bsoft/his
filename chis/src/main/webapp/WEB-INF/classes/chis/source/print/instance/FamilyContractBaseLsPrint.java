package chis.source.print.instance;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.phr.HealthRecordModel;
import chis.source.print.base.BSCHISPrint;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilyContractBaseLsPrint extends BSCHISPrint implements IHandler {
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		getDao(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		String	SCID  = (String) request.get("SCID");
		String	empiId  = (String) request.get("empiId");
		String	zj  = (String) request.get("zj");
		parameters.put("scid", SCID);
		parameters.put("empiId", empiId);
		parameters.put("zj", zj);
		String hql = "select distinct a.favoreeempiid   as empiid, (select aa.organizname from sys_organization aa where a.createunit=aa.organizcode)   as createunit," +
				"(select bb.personname from sys_personnel bb where a.createuser=bb.personid) as ysxm, (select aa.organizname from sys_organization aa where substr(a.createunit,1,9)=aa.organizcode)   as createunit1,peopleFlag as peopleFlag," +
				"(select bb.mobile from sys_personnel bb where a.createuser=bb.personid) as ysdh,a.secondpartyname as personname,b.idcard  as idcard ,b.address as address, b.contactrelation as contactrelation," +
				"(CASE WHEN b.mobilenumber IS null then '1' else b.mobilenumber end ) as phone,(CASE WHEN b.contactPhone IS null then '1' else b.contactPhone end ) as contactphone, to_char(a.scdate,'yyyy') as scdate1,to_char(a.scdate,'mm') as scdate2,to_char(a.scdate,'dd') as scdate3,to_char(a.begindate,'yyyy') as begindate1," +
				"to_char(a.begindate,'mm') as begindate2, to_char(a.begindate,'dd') as begindate3,to_char(a.stopdate,'yyyy') as stopdate1,to_char(a.stopdate,'mm') as stopdate2,to_char(a.stopdate,'dd') as stopdate3,a.signflag as signflag " +
				"from scm_signcontractrecord a left join  mpi_demographicinfo b on a.favoreeempiid = b.empiid left join ehr_healthrecord c on  a.favoreeempiid=c.empiid where   a.SCID="+SCID ;
		String hql2 = "select  to_char(t1.enddate,'yyyy') as enddate1,to_char(t1.enddate,'mm') as enddate2,to_char(t1.enddate,'dd') as enddate3,1 as a from scm_signcontractrecord t1 where t1.favoreeempiid =(select t.favoreeempiid as empiid from scm_signcontractrecord t where t.scid = "
				+SCID
				+" ) order by scid desc " ;
		String hql3 = "select replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(T.PEOPLEFLAG,'06','严重精神障碍患者'),'02','0-6岁儿童'),'07','肺结核患者'),'08','残疾人'),'09','计划生育特殊家庭人员'),'11','城乡低保和特困人员'),'12','重点优抚对象'),'14','其他'),'15','白血病患儿'),'16','麻风病患者'),'17','离休干部'),'13','农村建档立卡低收入人口')  as peopleFlag9"+ 
             " from scm_signcontractrecord t  where T.PEOPLEFLAG not in ('01','03','04','05','10') and t.scid=" +SCID;
		if(zj.contains("1")){
//			String sql = "select * from (select   distinct e.spid,case when e.spid='0000000000000002' then '15'when  e.spid='0000000000000001' then '0.00' when e.spid='0000000000000003'then '0.00'else'-' end as gr1,  "+
//					" case when e.spid='0000000000000002' then '90'when  e.spid='0000000000000001' then '90' when e.spid='0000000000000003'then '75' else '-'end as xj1,"+
//					" e.sprealprice from scm_signcontractrecord a,scm_signcontractpackage b,scm_servicepackageitems c,SCM_ServicePackage e where a.scid=b.scid and  b.spid = c.spid and c.spid = e.spid  and e.spid in('0000000000000001','0000000000000002','0000000000000003')and a.scid =" +SCID
//					+" )aa  where aa.spid in('0000000000000001', '0000000000000002', '0000000000000003')";
//			List<Map<String, Object>> list6 = null;
//			try {
//				list6 = dao.doSqlQuery(sql, null);
//			} catch (PersistentDataOperationException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			Map<String, Object> record6 =list6.get(0);
//			String gr1=(String) record6.get("GR1");
//			String xj1=(String) record6.get("XJ1");
//			parameters.put(gr1, record6.get("gr1"));
//			parameters.put(xj1, record6.get("xj1"));
			String hql5 = "select * from (select   distinct e.spid,e.packagename,case when e.packagename='基本服务包' then '15' when e.packagename='0-6岁儿童基本服务包' then '15'  when  e.packagename='基本服务包(减免)' then '0.00' when e.packagename='基本服务包（不含基本医疗服务）'then '0.00'else'-' end as gr,  "+
			" case when e.packagename='基本服务包' then '90' when e.packagename='0-6岁儿童基本服务包' then '90' when  e.packagename='基本服务包(减免)' then '90' when  e.packagename='0-6岁儿童基本服务包(减免)' then '90'  when e.packagename='基本服务包（不含基本医疗服务）'then '75' else '-'end as xj,"+
			" e.sprealprice from scm_signcontractrecord a,scm_signcontractpackage b,scm_servicepackageitems c,SCM_ServicePackage e where a.scid=b.scid and  b.spid = c.spid and c.spid = e.spid  and e.packagename in('基本服务包(减免)','基本服务包','基本服务包（不含基本医疗服务）','0-6岁儿童基本服务包','0-6岁儿童基本服务包(减免)')and a.scid =" +SCID
		+" )aa  where aa.packagename in('基本服务包(减免)', '基本服务包', '基本服务包（不含基本医疗服务）','0-6岁儿童基本服务包','0-6岁儿童基本服务包(减免)')";
			List<Map<String, Object>> list5 = null;
			try {
				list5 = dao.doSqlQuery(hql5, null);
			} catch (PersistentDataOperationException e) {
				
				//e.printStackTrace();
			} 
			Map<String, Object> record5 =list5.get(0);
			String spid = (String) record5.get("SPID");
			String pkname = (String) record5.get("PACKAGENAME");
			String gr = (String) record5.get("GR");
			String xj = (String) record5.get("XJ");
			parameters.put(gr, record5.get("GR"));
		    parameters.put(xj, record5.get("XJ"));
			if(pkname.equals("基本服务包")){
	        	response.put("spid1", "√");
	        	response.put("grzf", "15");
	        	response.put("xjzf", "90");
	        	response.put("jm", "0");
	        	response.put("yyzf", "0");
	        	response.put("jb", "①免一般诊疗服务个人自付部分");
	        	response.put("jb1", "②测血糖");
	        	response.put("jb2", "③测血压");
	        	response.put("jb3", "⑤肝、肾功能检测");
	        	response.put("jbfw", "-");
	        	response.put("jbfw1", "1");
	        	response.put("jbfw2", "2");
	        	response.put("jbfw3", "1");
			}
			if(pkname.equals("0-6岁儿童基本服务包")){
	        	response.put("spid1", "√");
	        	response.put("grzf", "15");
	        	response.put("xjzf", "90");
	        	response.put("jm", "0");
	        	response.put("yyzf", "0");
	        	response.put("jb", "①免一般诊疗服务个人自付部分");
	        	response.put("jb1", "②血常规（五分类）检测2次");
	        	response.put("jb2", "③ABO血型检测1次");
	        	response.put("jb3", "⑤测血压（按需）");
	        	response.put("jbfw", "-");
	        	response.put("jbfw1", "2");
	        	response.put("jbfw2", "1");
	        	response.put("jbfw3", "-");
			}
			if(pkname.equals("基本服务包(减免)")){
	        	response.put("spid2", "√");
	        	response.put("jm", "15");
	        	response.put("grzf", "0");
	        	response.put("yyzf", "0");
	        	response.put("xjzf", "90");
	        	response.put("jb", "①免一般诊疗服务个人自付部分");
	        	response.put("jb1", "②测血糖");
	        	response.put("jb2", "③测血压");
	        	response.put("jb3", "⑤肝、肾功能检测");
	        	response.put("jbfw", "-");
	        	response.put("jbfw1", "1");
	        	response.put("jbfw2", "2");
	        	response.put("jbfw3", "1");
			}
			if(pkname.equals("0-6岁儿童基本服务包(减免)")){
			   	response.put("spid1", "√");
	        	response.put("grzf", "15");
	        	response.put("xjzf", "90");
	        	response.put("jm", "0");
	        	response.put("yyzf", "0");
	        	response.put("jb", "①免一般诊疗服务个人自付部分");
	        	response.put("jb1", "②血常规（五分类）检测2次");
	        	response.put("jb2", "③ABO血型检测1次");
	        	response.put("jb3", "⑤测血压（按需）");
	        	response.put("jbfw", "-");
	        	response.put("jbfw1", "2");
	        	response.put("jbfw2", "1");
	        	response.put("jbfw3", "-");
			}
			if(pkname.equals("基本服务包（不含基本医疗服务）")){
	        	response.put("spid3", "√");
	        	response.put("grzf", "0");
	        	response.put("yyzf", "0");
	        	response.put("xjzf", "75");
	        	response.put("jm", "0");
	        	response.put("jb", "-");
	        	response.put("jb1", "-");
	        	response.put("jb2", "-");
	        	response.put("jb3", "-");
	        	response.put("jbfw", "-");
	        	response.put("jbfw1", "-");
	        	response.put("jbfw2", "-");
	        	response.put("jbfw3", "-");
			}
			response.put("gr",gr);
			response.put("xj",xj);
			
			//String hql4 = "select to_char(wm_concat( distinct e.packagename)) packagename,SUM(d.price) as pricezj,sum(d.servicetimes*d.price) as allpricezj,sum(d.servicetimes*d.price*0.3) as oddpricezj,sum(d.servicetimes*d.price*0.7) as realpricezj  from scm_signcontractrecord  a, scm_increaseitems  b,scm_servicepackageitems c, scm_serviceitems  d ,SCM_ServicePackage e where a.scid = b.scid and   b.spiid = c.spiid and c.itemcode = d.itemcode and c.spid=e.spid and a.scid =" +SCID;
			String hql4 = "select to_char(wm_concat( distinct e.packagename)) packagename, SUM(d.price) as pricezj, (sum((case when b.servicetimes is null then '0' else b.servicetimes end  ) * d.price)+" +
					xj+") as allpricezj,sum((case when b.servicetimes is null then '0' else b.servicetimes end  )*d.price*0.3) as oddpricezj, (sum((case when b.servicetimes is null then '0' else b.servicetimes end  ) * d.price * 0.7)+" +
					gr+") as realpricezj  from  " +
					" scm_signcontractpackage b,scm_servicepackageitems c, scm_serviceitems  d ,SCM_ServicePackage e where  b.spiid = c.spiid and c.itemcode = d.itemcode and c.spid=e.spid and b.scid =" +SCID
			+" and  b.empiid= '"+empiId +"'";
			List<Map<String, Object>> list4 = null;
			
			try {
				list4 = dao.doSqlQuery(hql4, null);
			} catch (PersistentDataOperationException e) {
				
				e.printStackTrace();
			}  
			Map<String, Object> record4 =list4.get(0);
			String pricezj = ""+record4.get("PRICEZJ");
			String allpricezj = ""+record4.get("ALLPRICEZJ");
			String oddpricezj = ""+record4.get("ODDPRICEZJ");
			String realpricezj = ""+record4.get("REALPRICEZJ");
			String packagename = ""+record4.get("PACKAGENAME");
			response.put("pricezj", pricezj);
			response.put("allpricezj", allpricezj);
			response.put("oddpricezj", oddpricezj);
			response.put("realpricezj", realpricezj);
			response.put("packagename", packagename);
		}else{
			String hql4 = "select to_char(wm_concat( distinct e.packagename)) packagename, SUM(d.price) as pricezj, (sum((case when b.servicetimes is null then '0' else b.servicetimes end  ) * d.price)" +
					//xj1+
					") as allpricezj,sum((case when b.servicetimes is null then '0' else b.servicetimes end  )*d.price*0.3) as oddpricezj, (sum((case when b.servicetimes is null then '0' else b.servicetimes end  ) * d.price * 0.7)" +
					//gr1+
					") as realpricezj  from  " +
					" scm_signcontractpackage b,scm_servicepackageitems c, scm_serviceitems  d ,SCM_ServicePackage e where  b.spiid = c.spiid and c.itemcode = d.itemcode and c.spid=e.spid and b.scid =" +SCID
			+" and  b.empiid= '"+empiId +"'";
			List<Map<String, Object>> list4 = null;
			try {
				list4 = dao.doSqlQuery(hql4, null);
			} catch (PersistentDataOperationException e) {
				
				e.printStackTrace();
			}  
			
			Map<String, Object> record4 =list4.get(0);
			String pricezj = ""+record4.get("PRICEZJ");
			String allpricezj = ""+record4.get("ALLPRICEZJ");
			String oddpricezj = ""+record4.get("ODDPRICEZJ");
			String realpricezj = ""+record4.get("REALPRICEZJ");
			String packagename = ""+record4.get("PACKAGENAME");
			response.put("pricezj", pricezj);
			response.put("allpricezj", allpricezj);
			response.put("oddpricezj", oddpricezj);
			response.put("realpricezj", realpricezj);
			response.put("packagename", packagename);
			response.put("jm", "0");
		}
		
		
		//parameters.put(gr1, value)
		//parameters.put("xj1", xj1);
		//parameters.put("gr1", gr1);
		//Map<String, Object> sql = null;
		//sql = dao.doSqlQuery(sql, parameters);
		//parameters.put("CREATEUSER_TEXT", sql.get("CREATEUSER_TEXT"));

	
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> list2 = null;
		List<Map<String, Object>> list3 = null;
		
		
		try {
			list = dao.doSqlQuery(hql, null);
		} catch (PersistentDataOperationException e) {
			
			e.printStackTrace();
		} 
		try {
			list2 = dao.doSqlQuery(hql2, null);
		} catch (PersistentDataOperationException e) {
			
			e.printStackTrace();
		} 
		try {
			list3 = dao.doSqlQuery(hql3, null);
		} catch (PersistentDataOperationException e) {
			
			e.printStackTrace();
		}  
		
				Map<String, Object> record =list.get(0);
        if(list2.size()==1){
            	response.put("flag", "√");
		     	
		}else{
			Map<String, Object> record2 =list2.get(0);
			String endDate1 = (String) record2.get("ENDDATE1");
			String endDate2 = (String) record2.get("ENDDATE2");
			String endDate3 = (String) record2.get("ENDDATE3");
			response.put("endDate1",endDate1);
			response.put("endDate2",endDate2);
			response.put("endDate3",endDate3);
			if (endDate1 != null) {
				response.put("xyFlag", "√");
			}
//			if (endDate1 == null) {
//				response.put("flag", "√");
//			}
		}
		if(list3.isEmpty()){
			response.put("peopleFlag10", "");
		}else{
			Map<String, Object> record3 =list3.get(0);
			String peopleFlag9 = (String) record3.get("PEOPLEFLAG9");
			 peopleFlag9 = (peopleFlag9+",").replace("03,","").replace("10,","").replace("05,","").replace("04,","").replace("01,","");
		        if(!peopleFlag9.equals("")){
		        	response.put("peopleFlag10", "√");
		        	response.put("peopleFlag9", peopleFlag9.replace(",", ""));
		        }
		}
	
		
		//String empiId =  (String) record.get("EMPIID");
		String personname =  (String) record.get("PERSONNAME");
		String idcard =  (String) record.get("IDCARD");
		String address =  (String) record.get("ADDRESS");
		String phone =  (String) record.get("PHONE");
		String contactphone =  (String) record.get("CONTACTPHONE");
		String peopleFlag =  (String) record.get("PEOPLEFLAG");
		String createUnit = (String) record.get("CREATEUNIT");
		String createUnit1 = (String) record.get("CREATEUNIT1");
		String contactrelation = (String) record.get("CONTACTRELATION");
		
		if(peopleFlag.contains("10")){
        	response.put("peopleFlag", "√");
		}
        if(peopleFlag.contains("03")){
        	response.put("peopleFlag2", "√");
		}
        if(peopleFlag.contains("01")){
        	response.put("peopleFlag3", "√");
		}
        if(peopleFlag.contains("04")){
        	response.put("peopleFlag4", "√");
		}
        if(peopleFlag.contains("05")){
        	response.put("peopleFlag5", "√");
		}
       
       
		String scDate1 = (String) record.get("SCDATE1");
		String scDate2 = (String) record.get("SCDATE2");
		String scDate3 = (String) record.get("SCDATE3");
		String beginDate1 = (String) record.get("BEGINDATE1");
		String beginDate2 = (String) record.get("BEGINDATE2");
		String beginDate3 = (String) record.get("BEGINDATE3");
		String stopDate1 = (String) record.get("STOPDATE1");
		String stopDate2 = (String) record.get("STOPDATE2");
		String stopDate3 = (String) record.get("STOPDATE3");
		String ysxm = (String) record.get("YSXM");
		String ysdh = (String) record.get("YSDH");
		String signFlag = (String) record.get("SIGNFLAG");
		String manageUnitId = (String) request.get("manageUnitId");

		
		
	
			response.put("personname",personname);
			response.put("idcard",idcard);
			response.put("address",address);
			response.put("phone",phone);
			response.put("contactphone",contactphone);
			response.put("contactrelation",contactrelation); 
			response.put("createUnit",createUnit);
			response.put("createUnit1",createUnit1);
			response.put("SCID",SCID); 
			
			response.put("scDate1",scDate1);
			response.put("scDate2",scDate2);
			response.put("scDate3",scDate3);
			response.put("beginDate1",beginDate1);
			response.put("beginDate2",beginDate2);
			response.put("beginDate3",beginDate3);
			response.put("stopDate1",stopDate1);
			response.put("stopDate2",stopDate2);
			response.put("stopDate3",stopDate3);
			response.put("signFlag",signFlag);
			
			response.put("ysxm", ysxm);
			response.put("ysdh", ysdh);
			response.put("manageUnitId", manageUnitId);
			response.put("jfmc", createUnit+ysxm);//甲方名称
			
			//response.put("empiid", empiid);
			
	
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	
		getDao(ctx);
		Map<String, Object> parameters = new HashMap<String, Object>();
		String	SCID  = (String) request.get("SCID");
		String	empiId  = (String) request.get("empiId");
		parameters.put("scid", SCID);
		parameters.put("empiId", empiId);
		//String sql ="select d.itemname as  itemname,case when d.servicetimes is null then '按需' else to_char(d.servicetimes) end  as servicetimes,d.price as price,(d.servicetimes*d.price) as allprice,(d.servicetimes*d.price*0.7) as realprice  from scm_signcontractrecord  a, scm_increaseitems  b,scm_servicepackageitems c, scm_serviceitems  d where a.scid = b.scid and   b.spiid = c.spiid and c.itemcode = d.itemcode and a.scid ="+SCID;		
		String sql ="select distinct d.itemname as  itemname,case when b.servicetimes is null then '按需' else to_char(b.servicetimes) end  as servicetimes,case when to_char(d.price) is null then '-' else to_char(d.price) end as price,case when to_char((b.servicetimes*d.price)) is null then '-' else to_char((b.servicetimes*d.price)) end as allprice,case when to_char((b.servicetimes*d.price*0.7)) is null then '-' else to_char((b.servicetimes*d.price*0.7)) end as realprice  from  scm_signcontractpackage  b,scm_servicepackageitems c, scm_serviceitems  d,scm_servicepackage e where b.spiid = c.spiid and b.spid=e.spid and e.packagename not in('基本服务包(减免)','基本服务包','基本服务包（不含基本医疗服务）') and c.itemcode = d.itemcode and b.scid ="+SCID
				+" and b.empiId='"+empiId+"'";		
		List<Map<String, Object>> list = null;
		try {
			list = dao.doSqlQuery(sql, null);
			int size=list.size();
			for (int i = 0; i <size; i = i + 1) {
				Map<String, Object> cm = new HashMap<String, Object>();
				cm.put("ITEMNAME", (list.get(i)).get("ITEMNAME")+"");
				cm.put("SERVICETIMES", (list.get(i)).get("SERVICETIMES")+"");
				cm.put("PRICE", (list.get(i)).get("PRICE")+"");
				cm.put("ALLPRICE", (list.get(i)).get("ALLPRICE")+"");
				cm.put("REALPRICE", (list.get(i)).get("REALPRICE")+"");
			//	cm.put("ODDPRICE", (list.get(i)).get("ODDPRICE")+"");
				records.add(cm);
			}
		} catch (PersistentDataOperationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
}
}
