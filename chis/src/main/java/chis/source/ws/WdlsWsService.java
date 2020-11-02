/**
 * @(#)TestWsService.java Created on 2013-9-27 下午12:07:30
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.hibernate.Query;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;

import chis.source.util.BSCHISUtil;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class WdlsWsService extends AbstractWsService {

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
			
			if(idcard.length() <18){
				res.put("status","2");
				res.put("msg","身份证不对");
				res.put("info","{}");
			}else{
				JSONObject info=new JSONObject();
				String sql="select a.empiid ,a.personname ,a.sexcode,a.birthday,a.idcard,a.workplace," +
						" a.mobileNumber,a.contact,a.contactPhone,a.registeredPermanent,a.nationCode," +
						" a.bloodtypecode,a.educationCode,a.workCode,a.maritalStatusCode,a.insuranceCode" +
						" from  mpi_demographicinfo a where a.idcard='"+idcard+"'";
				Query query=session.createSQLQuery(sql);
				List<?> l=query.list();
				query=null;
				if(l.size() >0){
					res.put("status","0");
					res.put("msg","操作成功");
					Object[] obj = (Object[]) l.get(0);
					try {
					info.put("id", obj[0]==null?"无":obj[0]);
					info.put("userName",obj[1]==null?"无":obj[1]);
					info.put("sex",obj[2]==null?"无":DictionaryController.instance().
								get("chis.dictionary.gender").getText(obj[2]+""));
					info.put("birthday",obj[3]==null?"无":(obj[3]+"").replaceAll("-","."));
					info.put("idCard",obj[4]==null?"无":obj[4]);
					info.put("company",obj[5]==null?"无":obj[5]);
					info.put("tel",obj[6]==null?"无":obj[6]);
					info.put("contactName",obj[7]==null?"无":obj[7]);
					info.put("contactTel",obj[8]==null?"无":obj[8]);
					info.put("residentType",obj[9]==null?"无":DictionaryController.instance().
							get("chis.dictionary.registeredPermanent").getText(obj[9]+""));
					
					info.put("nation",obj[10]==null?"无":DictionaryController.instance().
							get("chis.dictionary.ethnic").getText(obj[10]+""));
					info.put("bloodType",obj[11]==null?"无":DictionaryController.instance().
							get("chis.dictionary.blood").getText(obj[11]+""));
					info.put("education",obj[12]==null?"无":DictionaryController.instance().
							get("chis.dictionary.education").getText(obj[12]+""));
					info.put("career",obj[13]==null?"无":DictionaryController.instance().
							get("chis.dictionary.jobtitle").getText(obj[13]+""));
					
					info.put("marriage",obj[14]==null?"无":DictionaryController.instance().
							get("chis.dictionary.maritals").getText(obj[14]+""));
					ArrayList<String> payment=new ArrayList<String>();
					if(obj[15]!=null){
					payment.add(DictionaryController.instance().
							get("chis.dictionary.payMode").getText(obj[15]+""));
					}
					info.put("payment",payment==null?"无":payment);
					
					String pastsql="select a.pasthistypecode,a.diseasecode,a.diseasetext, " +
							" case when a.confirmdate is null then to_char(a.startdate,'yyyy') ||'年'|| to_char(a.startdate,'mm')||'月' " +
							" else to_char(a.confirmdate,'yyyy') ||'年'|| to_char(a.confirmdate,'mm')||'月' end " +
							" from ehr_pasthistory a where a.empiid='"+obj[0]+"'"; ;
					//药物过敏史（数组类型）
					ArrayList<String> allergyHistory=new ArrayList<String>();
					//暴露史（数组类型）
					ArrayList<String> exposeHistory=new ArrayList<String>();
					//既往史
					JSONObject illnessHistory=new JSONObject();
					//遗传病史
					String heredopathiaHistory="无";
					//残疾情况（数组类型）
					ArrayList<String> deformity=new ArrayList<String>();
					ArrayList<String> father=new ArrayList<String>();
					ArrayList<String> mother=new ArrayList<String>();
					ArrayList<String> broAndSis=new ArrayList<String>();
					ArrayList<String> children=new ArrayList<String>();
					
					Query pastquery=session.createSQLQuery(pastsql);
					List<?> pl=pastquery.list();
					pastquery=null;
					if(pl.size() > 0 ){
						JSONArray disease=new JSONArray();//疾病
						JSONArray operation=new JSONArray();//手术
						JSONArray trauma=new JSONArray();//外伤
						JSONArray transfusion=new JSONArray();//输血
						for(int i=0;i<pl.size();i++){
							Object[] pastone = (Object[]) pl.get(i);
							if((pastone[0]+"").equals("01")){//过敏
								if(!(pastone[1]+"").equals("0101"))
								allergyHistory.add(pastone[2]+"");
							}else if((pastone[0]+"").equals("02")){//疾病
								if(!(pastone[1]+"").equals("0201")){
								JSONObject tempdisease=new JSONObject();
								tempdisease.put("name",pastone[2]==null?"":pastone[2]);
								tempdisease.put("time",pastone[3]==null?"":pastone[3]);
								disease.put(tempdisease);
								}
							}else if((pastone[0]+"").equals("03")){//手术史
								if(!(pastone[1]+"").equals("0301")){
								JSONObject tempoperation=new JSONObject();
								tempoperation.put("name",pastone[2]==null?"":pastone[2]);
								tempoperation.put("time",pastone[3]==null?"":pastone[3]);
								operation.put(tempoperation);
								}
							}else if((pastone[0]+"").equals("04")){//输血史
								if(!(pastone[1]+"").equals("0401")){
								JSONObject temptransfusion=new JSONObject();
								temptransfusion.put("name",pastone[2]==null?"":pastone[2]);
								temptransfusion.put("time",pastone[3]==null?"":pastone[3]);
								transfusion.put(temptransfusion);
								}
							}else if((pastone[0]+"").equals("05")){//遗传病史
								if(!(pastone[1]+"").equals("0501")){
								heredopathiaHistory=pastone[2]==null?"无":pastone[2]+"";
								}
							}else if((pastone[0]+"").equals("06")){//外伤史
								if(!(pastone[1]+"").equals("0601")){
								JSONObject temptrauma=new JSONObject();
								temptrauma.put("name",pastone[2]==null?"":pastone[2]);
								temptrauma.put("time",pastone[3]==null?"":pastone[3]);
								trauma.put(temptrauma);
								}
							}else if((pastone[0]+"").equals("07")){//家族疾病史-父亲
								if(!(pastone[1]+"").equals("0701")){
									father.add(pastone[3]==null?"":pastone[2]+"");
								}
							}else if((pastone[0]+"").equals("08")){//家族疾病史-母亲
								if(!(pastone[1]+"").equals("0801")){
									mother.add(pastone[3]==null?"":pastone[2]+"");
								}
							}else if((pastone[0]+"").equals("09")){//家族疾病史-兄弟姐妹
								if(!(pastone[1]+"").equals("0901")){
									broAndSis.add(pastone[3]==null?"":pastone[2]+"");
								}
							}else if((pastone[0]+"").equals("10")){//家族疾病史-子女
								if(!(pastone[1]+"").equals("1001")){
									children.add(pastone[3]==null?"":pastone[2]+"");
								}
							}else if((pastone[0]+"").equals("11")){//残疾状况
								if(!(pastone[1]+"").equals("1101")){
								deformity.add(pastone[2]==null?"":pastone[2]+"");
								}
							}else if((pastone[0]+"").equals("12")){//暴露史
								if(!(pastone[1]+"").equals("1201")){
								exposeHistory.add(pastone[2]+"");
								}
							}
						}
						illnessHistory.put("disease", disease);
						illnessHistory.put("operation", operation);
						illnessHistory.put("trauma", trauma);
						illnessHistory.put("transfusion", transfusion);
					}
					info.put("allergyHistory", allergyHistory);
					info.put("exposeHistory", exposeHistory);
					info.put("illnessHistory", illnessHistory);
					info.put("heredopathiaHistory", heredopathiaHistory);
					info.put("deformity", deformity);
					
					//生活环境
					JSONObject environment=new JSONObject();
					//家族史
					JSONObject familyHistory=new JSONObject();
					String ehrsql="select b.cookAirTool,b.fuelType," +
							" b.waterSourceCode,b.washroom,b.livestockColumn"+
							" from ehr_familymiddle b " +
							" where b.empiid='"+obj[0]+"'";
					Query ehrquery=session.createSQLQuery(ehrsql);
					List<?> el=ehrquery.list();
				
					if(el.size() >0 ){
						Object[] ehrxx = (Object[]) el.get(0);
						environment.put("kitchen", ehrxx[0]==null?"无":DictionaryController.instance().
								get("chis.dictionary.cookAirTool").getText(ehrxx[0]+""));
						environment.put("fuelType", ehrxx[1]==null?"无":DictionaryController.instance().
								get("chis.dictionary.fuelType").getText(ehrxx[1]+""));
						environment.put("water", ehrxx[2]==null?"无":DictionaryController.instance().
								get("chis.dictionary.waterSourceCode").getText(ehrxx[2]+""));
						environment.put("toilet", ehrxx[3]==null?"无":DictionaryController.instance().
								get("chis.dictionary.washroom").getText(ehrxx[3]+""));
						environment.put("animalFence", ehrxx[4]==null?"无":DictionaryController.instance().
								get("chis.dictionary.livestockColumn").getText(ehrxx[4]+""));
					}else{
						environment.put("kitchen","无");
						environment.put("fuelType","无");
						environment.put("water","无");
						environment.put("toilet","无");
						environment.put("animalFence","无");
					}
					familyHistory.put("father", father);
					familyHistory.put("mother", mother);
					familyHistory.put("broAndSis", broAndSis);
					familyHistory.put("children", children);
					
					info.put("familyHistory", familyHistory);
					info.put("environment", environment);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					res.put("info",info);
				}else{
					res.put("status","1");
					res.put("msg","查无信息");
					res.put("info","{}");
				}
			}
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

}
