package phis.source.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import ctd.util.AppContextHolder;

import phis.source.ws.AbstractWsService;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class KfyyService extends AbstractWsService{
	private static final Log logger = LogFactory.getLog(KfyyService.class);
	@WebMethod
	public String execute(String request) {
		Session session = getSessionFactory().openSession();
		logger.info("Received request data[" + request + "].");
		JSONObject jsonResPKS = new JSONObject();
		return jsonResPKS.toString();
	}
	@WebMethod
	public String getValue(String name){
		return "我叫："+name;
	}
	public String getDepOrWard(String hospitalId)throws Exception{
		JSONObject rmsg=new JSONObject();
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		session =factory.openSession();
		JSONObject hospitalIdobj=new JSONObject(hospitalId);
		hospitalId="";
		if(!hospitalIdobj.optString("hospitalId").equals("")){
			hospitalId=hospitalIdobj.getString("hospitalId");
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "搞事情啊！hospitalId不能为空！");
			return rmsg.toString();
		}
		String xxsql="select  b.registernumber as HOSPITALID,b.organizname as HOSPITALNAME,"+
						" case when a.mzsy='1' then to_char(a.ksdm) else '' end as DEPARTMENTID," +
						" case when a.mzsy='1' then a.ksmc else '' end as DEPARTMENTNAME,"+
						" case when a.bqsy='1' then  to_char(a.ksdm)  else '' end as WARDID," +
						" case when a.bqsy='1' then  a.ksmc  else '' end as WARDNAME"+
						" from gy_ksdm a "+
						" join sys_organization b on a.jgid=b.organizcode"+
						" where b.registernumber='"+hospitalId+"'  and (a.bqsy='1') ";
		Query query=session.createSQLQuery(xxsql);
		List<?> xxl=query.list();
		query=null;
		session.close();
		JSONArray xxlist=new JSONArray();
		for(int i=0;i<xxl.size();i++){
			Object[] one = (Object[]) xxl.get(i);
			JSONObject temp=new JSONObject();
			temp.put("hospitalId", one[0]==null?"":one[0]+"");
			temp.put("hospitalName", one[1]==null?"":one[1]+"");
			temp.put("departmentId", one[2]==null?"":one[2]+"");
			temp.put("departmentName", one[3]==null?"":one[3]+"");
			temp.put("wardId", one[4]==null?"":one[4]+"");
			temp.put("wardName", one[5]==null?"":one[5]+"");
			xxlist.put(temp);
		}
		
		String response="100";
		String message="成功";
		if(xxlist==null || xxlist.length()==0){
			response="200";
			message="未找到该机构编码为:"+hospitalId+"的科室信息，请核对编码！";
		}
		rmsg.put("response", response);
		rmsg.put("message", message);
		rmsg.put("result", xxlist.toString());
		return rmsg.toString();
	}
	public String getBedSchedule(String msg)throws Exception{
		JSONObject rmsg=new JSONObject();
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		session =factory.openSession();
		JSONObject msgobj=new JSONObject(msg);
		String hospitalId="";
		if(!msgobj.optString("hospitalId").equals("")){
			hospitalId=msgobj.getString("hospitalId");
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "搞事情啊！hospitalId不能为空！");
			return rmsg.toString();
		}
		String departmentId="";
		if(!msgobj.optString("departmentId").equals("")){
			departmentId=msgobj.getString("departmentId");
		}
		
		String wardId="";
		if(!msgobj.optString("wardId").equals("")){
			wardId=msgobj.getString("wardId");
		}
				
		String bedType="";
		if(!msgobj.optString("bedType").equals("")){
			bedType=msgobj.getString("bedType");
		}
		
		String bedId="";
		if(!msgobj.optString("bedId").equals("")){
			bedId=msgobj.getString("bedId");
		}
		
		String cwxxsql="select b.registernumber||'_'||a.brch as bedId,"+
						" b.registernumber as hospitalId,"+
						" b.organizname as hospitalName,"+
						" '' as departmentId ,"+
						" '' as departmentName,"+
						" a.ksdm as wardId,"+
						" c.ksmc as wardName,"+
						" a.brch as bedNumber,"+
						" '1' as bedType,"+
						" a.cwfy as price,"+
						" '0' as sex "+
						" from zy_cwsz a "+
						" join sys_organization b on a.jgid=b.organizcode"+
						" join gy_ksdm c on a.ksdm=c.ksdm"+
						" where b.registernumber='"+hospitalId+"' and a.zyh is null" +
						" and b.organizcode||'_'||a.brch not in( select y.bedid  from ms_yybr y where y.qrbz='0' ) ";
		if(bedId!=null && bedId.length() >0){
			cwxxsql=cwxxsql+" and b.organizcode||'_'||a.brch='"+bedId+"'";
		}
		Query query=session.createSQLQuery(cwxxsql);
		List<?> cwxxl=query.list();
		query=null;
		session.close();
		JSONArray cwxxlist=new JSONArray();
		for(int i=0;i<cwxxl.size();i++){
			Object[] one = (Object[]) cwxxl.get(i);
			JSONObject cwtemp=new JSONObject();
			cwtemp.put("bedId",  one[0]==null?"":one[0]+"");
			cwtemp.put("hospitalId",  one[1]==null?"":one[1]+"");
			cwtemp.put("hospitalName",  one[2]==null?"":one[2]+"");
			cwtemp.put("departmentId", one[3]==null?"":one[3]+"");
			cwtemp.put("departmentName",  one[4]==null?"":one[4]+"");
			cwtemp.put("wardId",  one[5]==null?"":one[5]+"");
			cwtemp.put("wardName", one[6]==null?"":one[6]+"");
			cwtemp.put("bedNumber", one[7]==null?"":one[7]+"");
			cwtemp.put("bedType",one[8]==null?"":one[8]+"");
			cwtemp.put("price", one[9]==null?"":one[9]+"");
			cwtemp.put("sex", one[10]==null?"":one[10]+"");
			cwxxlist.put(cwtemp);
		}
		String response="100";
		String message="成功";
		if(cwxxlist==null || cwxxlist.length()==0){
			response="200";
			message="未找到该机构编码为:"+hospitalId+",病区编码为："+wardId+"的床位信息，请核对编码！";
		} 
		rmsg.put("response", response);
		rmsg.put("message", message);
		rmsg.put("result", cwxxlist.toString());
		return rmsg.toString();
	}
	public String createReservation(String msg)throws Exception{
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		session =factory.openSession();
		
		JSONObject rmsg=new JSONObject();
		String response="100";
		String message="成功";
		JSONObject msgobj=new JSONObject(msg);
		String bedId="";
		if(!msgobj.optString("bedId").equals("")){
			bedId=msgobj.getString("bedId");
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "搞事情啊！bedId不能为空！");
			return rmsg.toString();
		}
		String findslq="select count(1) as sfyy from ms_yybr where bedid='"+bedId+"' and qrbz='0' ";
		Query query=session.createSQLQuery(findslq);
		List<?> findset=query.list();
		query=null;
        int count=0;
        count=Integer.parseInt(findset.get(0)+"");
		if(count >0){
			rmsg.put("response", "200");
			rmsg.put("message", "bedId此床已被预约！");
			return rmsg.toString();
		}
		
		String name="";
		if(!msgobj.optString("name").equals("")){
			name=msgobj.getString("name");
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "name不能为空！");
			return rmsg.toString();
		}
		
		String sex="";
		if(!msgobj.optString("sex").equals("")){
			sex=msgobj.getString("sex");
		}
		String age="";
		if(!msgobj.optString("age").equals("")){
			age=msgobj.getString("age");
		}
		if(age==null || age.length()<=0)
		{
			age="18";
		}
		String idNum="";
		if(!msgobj.optString("idNum").equals("")){
			idNum=msgobj.getString("idNum");
			if(!(idNum.length()==18)){
				rmsg.put("response", "200");
				rmsg.put("message", "idNum长度不够18位！");
				return rmsg.toString();
			}
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "idNum不能为空！");
			return rmsg.toString();
		}
		
		String mobilePhone="";
		if(!msgobj.optString("mobilePhone").equals("")){
			mobilePhone=msgobj.getString("mobilePhone");
		}
		 
		String telephone="";
		if(!msgobj.optString("telephone").equals("")){
			telephone=msgobj.getString("telephone");
		}
		String bedDate="";
		if(!msgobj.optString("bedDate").equals("")){
			bedDate=msgobj.getString("bedDate");
		}
		String sexcode="";
		if(sex.equals("男") ||sex.equals("1") ){
			sexcode="1";
		}else if(sex.equals("女")||sex.equals("2") ){
			sexcode="2";
		}else{
			sexcode="9";
		}
		int id=0;
		String idsql="select max(zcid) as zcid from ms_yybr";
		Query idquery=session.createSQLQuery(idsql);
		Object idset= idquery.uniqueResult();
		if(idset!=null){
			id=Integer.parseInt(idset+"")+1;
		}
		idset=null;

		try{
		String insql="insert into ms_yybr(zcid,zcrq,brxm,brxb,brnl,brxz,lxdh,bedid,sfzh,YYZS,qrbz)" +
				" values("+id+",sysdate,'"+name+"','"+sexcode+"','"+age+"','1000','"+mobilePhone+"','"+bedId+"','"+idNum+"',1,0)";
		System.out.println("insql:"+insql);
		session.createSQLQuery(insql).executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			response="200";
			message="预约失败！";
		}
		session.close();
		rmsg.put("response", response);
		rmsg.put("message", message);
		rmsg.put("hisResId", id);  
		System.out.println(rmsg.toString());
		return rmsg.toString();
	}
	
	//取消预约
	public String cancelReservation(String msg)throws Exception{
		JSONObject msgobj=new JSONObject(msg);
		JSONObject rmsg=new JSONObject();
		String bedId="";
		SessionFactory factory = (SessionFactory)AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY);
		Session session = null;
		session =factory.openSession();
		if(!msgobj.optString("bedId").equals("")){
			bedId=msgobj.getString("bedId");
		}else{
			rmsg.put("response", "200");
			rmsg.put("message", "搞事情啊！bedId不能为空！");
			return rmsg.toString();
		}
		String hisResId="";
		if(!msgobj.optString("hisResId").equals("")){
			hisResId=msgobj.getString("hisResId");
		}
		String response="100";
		String message="退约成功";
		try{
		String deletesql="update ms_yybr a set a.qrbz=2 where a.bedid='"+bedId+"'";
		session.createSQLQuery(deletesql).executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			response="200";
			message="退约失败！";
		}
		session.close();
		rmsg.put("response", response);
		rmsg.put("message", message);
		return rmsg.toString();
	}
	public static void main (String [] args){
		KfyyService a=new KfyyService();
		JSONObject req=new JSONObject();
		try {
			req.put("hospitalId", "E9379427732011112B1001");
			System.out.println(req.toString());
//			String aa=a.getDepOrWard(req.toString());
//			String aa=a.getBedSchedule("E9379427732011112B1001","","1117","");
//			String aa=a.createReservation("E9379427732011112B1001_11","测试","男","22","11111111","12580","","2016-01-01");
//			System.out.println(aa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
