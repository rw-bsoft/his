package chis.source.print.instance;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.print.base.BSCHISPrint;

import com.bsoft.mpi.util.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.S;
import ctd.util.context.Context;

public class ReferralInfoReportFile extends BSCHISPrint implements IHandler{
	public void getFields(Map<String, Object> req, List<Map<String, Object>> records, Context ctx)
		    throws PrintException {
		try{
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			getDao(ctx);
			//查询健康档案号：select  t.phrid from  ehR_HEALTHRECORD t ,MPI_DEMOGRAPHICINFO  s where t.empiid = s.empiid and s.idcard = '身份证号' 
		    String hql = "select a.recordId as recordId,a.mpiId as mpiId,a.hospitalCode as hospitalCode,a.treatResult as treatResult,a.exchangeTime as exchangeTime,a.healthAdvice as healthAdvice," +
			      "a.doctorName as doctorName,a.submitAgency as submitAgency,a.submitTime as submitTime,a.submitor as submitor,a.reserveNo as reserveNo,a.leaveConclusion as leaveConclusion," +
			      "a.payType as payType,a.brxz as brxz,a.emrNo as emrNo,a.hosNo as hosNo, " +
			      "b.personName as personName,b.idCard as idCard,b.sexCode as sexCode,b.birthday as birthday,b.contactNo as contactNo "+
			      "from drIt_sendExchangeReport a, DR_DemographicInfo b where a.mpiId = b.mpiId";
		    Map<String,Object> params = new HashMap<String, Object>();
//		    Date submitTime = (Date)req.get("submitTime");
			String reserveNo = (String)req.get("reserveNo");
//			String submitTimeStr = "";
//			if(submitTime!=null){
//				submitTimeStr = sdf.format(submitTime);
//			}
		    if(S.isNotEmpty(reserveNo)){
		    	hql+=" and a.reserveNo=:reserveNo";
		    	params.put("reserveNo",reserveNo);
		    }
//		    if(S.isNotEmpty(submitTimeStr)){
//		    	hql+=" and to_char(a.submitTime,'YYYY-MM-DD')=:submitTime";
//		    	params.put("submitTime",submitTimeStr);
//		    }
		    List<Map<String,Object>> referralList = dao.doQuery(hql, params);
		    if(referralList.size()>0){
		    	Map<String,Object> referral = referralList.get(0);
		    	String payType_text = "□合作医疗 □职工医保 □自费";
		    	if(referral.get("payType")!=null){
		    		String payType = referral.get("payType").toString();
			    	if("0".equals(payType)){
			    		payType_text = "自费";
			    	}else if("1".equals(payType)){
			    		payType_text = "合作医疗";
			    	}else if("2".equals(payType)){
			    		payType_text = "职工医保";
			    	}
		    	}
		    	referral.put("payType_text", payType_text);
		    	String brxz_text = "□急诊 □门诊 □住院 □其他";
		    	if(referral.get("brxz")!=null){
			    	String brxz = referral.get("brxz").toString();
			    	if("9".equals(brxz)){
			    		brxz_text = "其他";
			    	}else if("1".equals(brxz)){
			    		brxz_text = "急诊";
			    	}else if("2".equals(brxz)){
			    		brxz_text = "门诊";
			    	}else if("3".equals(brxz)){
			    		brxz_text = "住院";
			    	}
		    	}
		    	referral.put("brxz_text", brxz_text);
		    	String emrNo = referral.get("emrNo")!=null?referral.get("emrNo").toString():"";
		    	referral.put("emrNo", emrNo);
		    	String hosNo = referral.get("hosNo")!=null?referral.get("hosNo").toString():"";
		    	referral.put("hosNo", hosNo);
		    	
		    	String hospitalCode = referral.get("hospitalCode")!=null?referral.get("hospitalCode").toString():"";
		    	String hospitalName = DictionaryController.instance().get("chis.dictionary.communityCode").getText(hospitalCode);
		    	referral.put("hospitalName", hospitalName);
		    	
		    	String idCard = referral.get("idCard")!=null?referral.get("idCard").toString():"";
		    	String healthHql = "select t.phrId as phrid from EHR_HealthRecord t,DR_DemographicInfo s where t.empiId = s.mpiId and s.idCard=:idCard";
		    	params.clear();
		    	params.put("idCard",idCard);
		    	List<Map<String,Object>> phridList = dao.doQuery(healthHql, params);
		    	if(phridList.size()>0){
		    		String phrId = phridList.get(0).get("phrId").toString();
		    		if(S.isNotEmpty(phrId)){
		    			referral.put("phrId", phrId);
		    		}
		    	}
		    	
		    	String sexCode_text = "";
		    	String sexCode = referral.get("sexCode").toString();
		    	sexCode_text = "1".equals(sexCode)?"男":"2".equals(sexCode)?"女":"";
		    	referral.put("sexCode_text", sexCode_text);
		    	
		    	Timestamp submitTime = (Timestamp)(referral.get("submitTime"));
		    	String st = sdf2.format(submitTime);
		    	String submitTimeYear = st.substring(0, 4);
		    	String submitTimeMonth = st.substring(5, 7);
		    	String submitTimeDay = st.substring(8, 10);
		    	String submitTimeHour = st.substring(11, 13);
		    	String submitTimeMinute = st.substring(14, 16);
		    	referral.put("submitTimeYear", submitTimeYear);
		    	referral.put("submitTimeMonth", submitTimeMonth);
		    	referral.put("submitTimeDay", submitTimeDay);
		    	referral.put("submitTimeHour", submitTimeHour);
		    	referral.put("submitTimeMinute", submitTimeMinute);
		    	
		    	Date birthDay = (Date)(referral.get("birthday"));
		    	Calendar cal = Calendar.getInstance();
		    	int yearNow = cal.get(Calendar.YEAR);
		        int monthNow = cal.get(Calendar.MONTH);
		        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		        cal.setTime(birthDay);
		        int yearBirth = cal.get(Calendar.YEAR);
		        int monthBirth = cal.get(Calendar.MONTH);
		        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		        int age = yearNow - yearBirth;
		        if (monthNow <= monthBirth) {
		            if (monthNow == monthBirth) {
		                if (dayOfMonthNow < dayOfMonthBirth) age--;
		            }else{
		                age--;
		            }
		        }
		        referral.put("age", age);
		    }
		    records.addAll(SchemaUtil.setDictionaryMassageForList(referralList, 
		    		  "chis.application.dr.schemas.drIt_sendExchangeReport"));
	    }catch (Exception e){
	    	throw new PrintException(6000, e.getMessage());
	    }
	}

	public void getParameters(Map<String, Object> req, Map<String, Object> res, Context ctx)
	    throws PrintException{
		 Dictionary dic;
		 try{
			 dic = (Dictionary)DictionaryController.instance().get(
				        "chis.dictionary.manageUnit");
		 }catch (ControllerException e){
			 throw new PrintException(500, e.getMessage());
		 }
		 UserRoleToken urt = (UserRoleToken)ctx.get("$userRoleToken");
		 String unit = urt.getManageUnit().getId();
		 String unit_text = dic.getText(unit);
		 res.put("Title", unit_text + "转诊通知单");
	}
}
