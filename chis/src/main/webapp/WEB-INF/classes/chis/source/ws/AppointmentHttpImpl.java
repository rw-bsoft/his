/**
 * @(#)TestWsService.java Created on 2018-1-27 下午12:07:30
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.common.entity.JSONUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:liz@bsoft.com.cn">lizhi</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class AppointmentHttpImpl extends AbstractWsService {
//	private static final Log logger = LogFactory.getLog(AppointmentHttpImpl.class);

	/* (non-Javadoc)
	 * @see chis.source.ws.Service#execute(java.lang.String)
	 */
	@Override
	@WebMethod
	public String execute(String request) {
		JSONObject res=new JSONObject();
		Session session = getSessionFactory().openSession();
		try {
			JSONObject req=new JSONObject(request);
			String idcard=req.getString("idcard");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return res.toString();
	}
	
	/**
	 * 查询机构科室列表
	 * http://localhost:8082/chis/AppointmentInterface?method=getHospitalAndDeptInfo&version=20171222
	 * http://192.168.10.122:8081/lsehr/...
	 * @param request
	 * @return
	 */
	public String doGetHospitalAndDeptInfo(String request) {
		JSONObject res=new JSONObject();
		Session session = getSessionFactory().openSession();
		try {
			JSONObject req=new JSONObject(request);
			String version=req.getString("version");
			if(version!=null && !"".equals(version)){
				SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
				if(version.equals(sf.format(new Date()))){//校验版本号
					res.put("result","0");
					res.put("errMsg","版本号有误！");
					res.put("data","{}");
					return res.toString();
				}
				StringBuffer sb = new StringBuffer();
				sb.append("select a.organizCode as id,a.organizName as name,a.address as address,a.telphone as mobile,'' as remark,'' as lon,'' as lat from SYS_Organization a order by a.organizCode");
				Query query=session.createQuery(sb.toString());
				List<?> l=query.list();
				if(l.size() >0){
//					List<JSONObject> organizationList = new ArrayList<JSONObject>();//医院列表
					JSONArray organizationArr = new JSONArray();//机构Array
					res.put("result","1");
					for(int i=0;i<l.size();i++){
						Object[] obj = (Object[])l.get(i);
						JSONObject organization=new JSONObject();
						String id = obj[0]==null?"":obj[0]+"";
						organization.put("id", id);
						organization.put("name", obj[1]==null?"":obj[1]);
						organization.put("address", obj[2]==null?"":obj[2]);
						organization.put("mobile", obj[3]==null?"":obj[3]);
						organization.put("remark", obj[4]==null?"":obj[4]);
						organization.put("lon", obj[5]==null?"":obj[5]);
						organization.put("lat", obj[6]==null?"":obj[6]);
						//查询机构科室类别
						StringBuffer sb1 = new StringBuffer();
						sb1.append("select distinct a.organizType as classifyCode from SYS_Office a where a.organizCode = '" + id + "'");
						Query query1=session.createQuery(sb1.toString());
						List<?> l1=query1.list();
						if(l1.size() >0){
							List<JSONObject> sectionList = new ArrayList<JSONObject>();//科室列表
							for(int j=0;j<l1.size();j++){
								JSONObject classify=new JSONObject();
								String classifyCode = l1.get(j)==null?"":l1.get(j)+"";
								String classifyName = DictionaryController.instance()
										.get("platform.reg.dictionary.officeType")
										.getText(classifyCode);
								classify.put("classify", classifyName);
								//查询机构科室列表
								StringBuffer sb2 = new StringBuffer();
								sb2.append("select a.officeCode as officeCode,a.officeName as officeName from SYS_Office a where a.organizCode = '"
										+ id +"' and a.organizType = '" + classifyCode + "'");
								Query query2=session.createQuery(sb2.toString());
								List<?> l2=query2.list();
								if(l2.size() >0){
									List<JSONObject> section = new ArrayList<JSONObject>();//科室列表
									for(int k=0;k<l2.size();k++){
										Object[] dept = (Object[])l2.get(k);
										JSONObject deptObj=new JSONObject();
										deptObj.put("id", dept[0]==null?"":dept[0]);
										deptObj.put("name", dept[1]==null?"":dept[1]);
										deptObj.put("price", 0);
										section.add(deptObj);
									}
									classify.put("section", section);
								}else{
									classify.put("section", "");
								}
								sectionList.add(classify);
							}
							organization.put("sectionList",sectionList);
						}else{
							organization.put("sectionList","");
						}
//						organizationList.add(organization);
						organizationArr.put(organization);
					}
					res.put("data",organizationArr);
				}else{
					res.put("data","");
				}
			}else{
				res.put("result","0");
				res.put("errMsg","版本号不能为空！");
				res.put("data","{}");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return res.toString();
	}

	/**
	 * 查询医生排班列表
	 * @param request
	 * @return
	 */
	@WebMethod
	public String doGetDoctorAndWorkList(String request) {
		JSONObject res=new JSONObject();
		return res.toString();
	}

	/**
	 * 预约挂号
	 * http://localhost:8082/chis/AppointmentInterface?method=appointment&hospitalId=1&hospitalName=中医院&sectionId=123&sectionName=中医科&doctorId=123&doctorName=王医生&startTime=2018-03-12 14:00:00&endTime=2018-03-12 14:30:00&patientName=小王&patientCard=340803193508119809
	 * http://192.168.10.122:8081/lsehr/...
	 * @param request
	 * @return
	 */
	@WebMethod
	public String doAppointment(String request) {
		JSONObject res=new JSONObject();
		Session session = getSessionFactory().openSession();
		String imagePath = this.getClass().getClassLoader().getResource("").getPath();
		try {
			JSONObject req=new JSONObject(request);
			String doctorId=req.getString("doctorId");
			String doctorName=req.getString("doctorName");
			String hospitalId=req.getString("hospitalId");
			String hospitalName=req.getString("hospitalName");
			String sectionId=req.getString("sectionId");
			String sectionName=req.getString("sectionName");
			String startTime=req.getString("startTime");
			String endTime=req.getString("endTime");
			String patientName=req.getString("patientName");
			String patientCard=req.getString("patientCard");
			String queryString = "select count(*) as NUM from APPOINTMENT_RECORD where STATUS > 0 and PATIENTCARD = '"+patientCard+"' and HOSPITALID = '"+hospitalId+"' and SECTIONID = '"+sectionId+"'" +
					" and to_char(STARTTIME,'YYYY-MM-DD hh24:mi:ss') >= '"+startTime+"' and to_char(ENDTIME,'YYYY-MM-DD hh24:mi:ss') <= '"+endTime+"'";
			Query query1=session.createSQLQuery(queryString);
			List<?> dataList1=query1.list();
			if(dataList1.size() >0 && dataList1.get(0)!=null && Long.parseLong(dataList1.get(0)+"")>0){
				res.put("result","0");
				res.put("errMsg","该病人已存在"+startTime+"的预约信息！");
				return res.toString();
			}
//			long serialId = 1;
//			StringBuffer sb = new StringBuffer();
//			sb.append("select max(ID) from APPOINTMENT_RECORD");
//			Query query=session.createSQLQuery(sb.toString());
//			List<?> dataList=query.list();
//			if(dataList.size() >0 && dataList.get(0)!=null){
//				long appointmentObj = Long.parseLong(dataList.get(0)+"");
//				serialId = appointmentObj+1;
//			}
			Map<String,Object> record = new HashMap<String,Object>();
			record.put("patientName", patientName);
			record.put("patientCard", patientCard);
			record.put("hospitalId", hospitalId);
			record.put("hospital", hospitalName);
			record.put("doctorId", doctorId);
			record.put("doctor", doctorName);
			record.put("sectionId", sectionId);
			record.put("section", sectionName);
			record.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime));
			record.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime));
			record.put("status", 1);
			session.save(BSCHISEntryNames.HIS_AppointmentRecord,record);
			session.flush();
			//add by lizhi 2018-03-06增加二维码生成
//			String content = record.get("id")+"";
//			imagePath.substring(0,imagePath.lastIndexOf("\\")+1);
//			QRCodeUtil.QRCodeCreate(content, imagePath+"qrCodeImages/"+content+".jpg", 15, "E://icon.png");
//			record.put("codeImage", "qrCodeImages/"+content+".jpg");
//			session.merge(record);
			record.put("codeImg", record.get("id"));
			res.put("result","1");
			res.put("data",JSONUtil.toJson(record));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.clear();
				session.close();
			}
		}
		return res.toString();
	}

	/**
	 * 取消预约
	 * http://localhost:8082/chis/AppointmentInterface?method=cancelAppointment&hospitalId=1&hospitalName=中医院&sectionId=123&sectionName=中医科&doctorId=123&doctorName=王医生&startTime=2018-03-12 14:00:00&patientName=小王&patientCard=340803193508119809
	 * http://192.168.10.122:8081/lsehr/...
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@WebMethod
	public String doCancelAppointment(String request) {
		JSONObject res=new JSONObject();
		Session session = getSessionFactory().openSession();
		try {
			JSONObject req = new JSONObject(request);
			String patientCard=req.getString("patientCard");
			String hospitalId=req.getString("hospitalId");
			String sectionId=req.getString("sectionId");
			String startTime=req.getString("startTime");
			String endTime=req.getString("endTime");
			String queryString = "select * from APPOINTMENT_RECORD where STATUS=1 and PATIENTCARD =:patientCard and HOSPITALID=:hospitalId and SECTIONID=:sectionId" +
					" and to_char(STARTTIME,'YYYY-MM-DD hh24:mi:ss')>=:startTime and to_char(ENDTIME,'YYYY-MM-DD hh24:mi:ss')<=:endTime";
			Query query = session.createSQLQuery(queryString).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setString("patientCard", patientCard);
			query.setString("hospitalId", hospitalId);
			query.setString("sectionId", sectionId);
			query.setString("startTime", startTime);
			query.setString("endTime", endTime);
			List<Map<String,Object>> dataList = query.list();
			if(dataList.size() >0){
				Map<String,Object> obj  = (Map<String,Object>) dataList.get(0);
				Map<String,Object> appointment  = new HashMap<String, Object>();
				appointment.put("id", Long.parseLong(obj.get("ID")+""));
				appointment.put("patientName", obj.get("PATIENTNAME")!=null?obj.get("PATIENTNAME")+"":"");
				appointment.put("patientCard", obj.get("PATIENTCARD")!=null?obj.get("PATIENTCARD")+"":"");
				appointment.put("patientMobile", obj.get("PATIENTMOBILE")!=null?obj.get("PATIENTMOBILE")+"":"");
				appointment.put("doctor", obj.get("DOCTOR")!=null?obj.get("DOCTOR")+"":"");
				appointment.put("hospital", obj.get("HOSPITAL")!=null?obj.get("HOSPITAL")+"":"");
				appointment.put("section", obj.get("SECTION")!=null?obj.get("SECTION")+"":"");
				appointment.put("doctorId", obj.get("DOCTORID")!=null?obj.get("DOCTORID")+"":"");
				appointment.put("hospitalId", obj.get("HOSPITALID")!=null?obj.get("HOSPITALID")+"":"");
				appointment.put("sectionId", obj.get("SECTIONID")!=null?obj.get("SECTIONID")+"":"");
				appointment.put("startTime", obj.get("STARTTIME")!=null?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.get("STARTTIME")+""):null);
				appointment.put("endTime", obj.get("ENDTIME")!=null?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.get("ENDTIME")+""):null);
//				appointment.put("codeImg", obj.get("CODEIMG")!=null?obj.get("CODEIMG")+"":"");
				appointment.put("codeImg", obj.get("ID")+"");
				appointment.put("status", 0);
				session.merge(BSCHISEntryNames.HIS_AppointmentRecord,appointment);
				session.flush();
				res.put("result","1");
			}else{
				res.put("result","0");
				res.put("errMsg","未查到病人在"+startTime+"的预约信息！");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.clear();
				session.close();
			}
		}
		return res.toString();
	}

	/**
	 * 查询预约列表
	 * http://localhost:8082/chis/AppointmentInterface?method=getAppointmentList&patientCard=340803193508119809
	 * http://192.168.10.122:8081/lsehr/...
	 * @param request
	 * @return
	 */
	@WebMethod
	public String doGetAppointmentList(String request) {
		JSONObject res=new JSONObject();
		Session session = getSessionFactory().openSession();
		try {
			JSONObject req=new JSONObject(request);
			String patientCard=req.getString("patientCard");
			JSONObject appointmentList=new JSONObject();
			List<JSONObject> waitList = new ArrayList<JSONObject>();//待就诊信息
			List<JSONObject> hirstoyList = new ArrayList<JSONObject>();//历史预约信息
			StringBuffer sb = new StringBuffer();
			sb.append("select PATIENTNAME,PATIENTCARD,PATIENTMOBILE,DOCTOR,HOSPITAL,SECTION,STARTTIME,ENDTIME,ID,STATUS,HOSPITALID,SECTIONID from APPOINTMENT_RECORD where STATUS=1 and PATIENTCARD = '"+patientCard+"'");
			Query query=session.createSQLQuery(sb.toString());
			List<?> dataList=query.list();
			if(dataList.size() >0){
				for(int i=0;i<dataList.size();i++){
					Object[] appointmentObj = (Object[])dataList.get(i);
					JSONObject appointment=new JSONObject();
					appointment.put("patientName", appointmentObj[0]==null?"":appointmentObj[0]);
					appointment.put("patientCard", appointmentObj[1]==null?"":appointmentObj[1]);
					appointment.put("patientMobile", appointmentObj[2]==null?"":appointmentObj[2]);
					appointment.put("doctor", appointmentObj[3]==null?"":appointmentObj[3]);
					appointment.put("hospital", appointmentObj[4]==null?"":appointmentObj[4]);
					appointment.put("section", appointmentObj[5]==null?"":appointmentObj[5]);
					appointment.put("startTime", appointmentObj[6]==null?"":appointmentObj[6]);
					appointment.put("endTime", appointmentObj[7]==null?"":appointmentObj[7]);
					appointment.put("codeImg", appointmentObj[8]==null?"":appointmentObj[8]);
					appointment.put("hospitalId", appointmentObj[10]==null?"":appointmentObj[10]);
					appointment.put("sectionId", appointmentObj[11]==null?"":appointmentObj[11]);
					appointment.put("price", 0);
					int status = Integer.parseInt(appointmentObj[9]==null?"":appointmentObj[9]+"");
					if(status > 0 && status < 3){//1已预约；2已取号
						waitList.add(appointment);
					}else if(status == 3){//3已就诊
						hirstoyList.add(appointment);
					}
				}
			}
			appointmentList.put("waitList", waitList);
			appointmentList.put("hirstoyList", hirstoyList);
			res.put("result","1");
			res.put("data",appointmentList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return res.toString();
	}
}
