package chis.source.print.instance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class PrenatalChildrenRecord extends BSCHISPrint implements IHandler {
	protected String visitId;
	protected Context ctx;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		getDao(ctx);
		visitId = (String) request.get("visitId");
		Map<String, Object> infoData = new HashMap<String, Object>();
		Map<String, Object> recordeData = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		Map<String, Object> param = new HashMap<String, Object>();
		String babyId = null;
		String empiId = null;
		// 获取儿保的新生儿随访记录的信息
		param.put("visitId", visitId);
		sb.append(
				" select babyId as babyId, weight as weight,feedWay as feedWay, eatNum as eatNum,eatCount as eatCount,vomit as vomit ,stoolStatus as stoolStatus,stoolTimes as stoolTimes,temperature as temperature,"
						+ "pulse as pulse ,respiratoryFrequency as respiratoryFrequency,face as face,faceOther as faceOther,jaundice as jaundice,bregmaTransverse as bregmaTransverse,bregmaLongitudinal as bregmaLongitudinal,"
						+ " bregmaStatus as bregmaStatus,otherStatus as otherStatus ,eye as eye ,eyeAbnormal as eyeAbnormal ,limbs as limbs ,limbsAbnormal as limbsAbnormal,ear as ear,earAbnormal as earAbnormal ,neck as neck,nose as nose "
						+ ",noseAbnormal as noseAbnormal,skin as skin,skinAbnormal as skinAbnormal,mouse as mouse,mouseAbnormal as mouseAbnormal,anal as anal ,analAbnormal as analAbnormal,heartlung as heartlung,heartLungAbnormal as heartLungAbnormal,"
						+ "genitalia as genitalia ,genitaliaAbnormal as genitaliaAbnormal,abdominal as abdominal,abdominalabnormal as abdominalabnormal,spine as spine,spineAbnormal as spineAbnormal,umbilical as umbilical,umbilicalOther as umbilicalOther,"
						+ "referral as referral,referralUnit as referralUnit,referralReason as referralReason,guide as guide ,visitDate as visitDate,nextVisitAddress as nextVisitAddress,nextVisitDate as nextVisitDate ,visitDoctor as visitDoctor ")
				.append(" from " + BSCHISEntryNames.MHC_BabyVisitRecord)
				.append(" where visitId=:visitId ");

		try {
			recordeData = dao.doLoad(sb.toString(), param);
			// recordeData=dao.doLoad(BSCHISEntryNames.MHC_BabyVisitRecord, param);
			
			if (recordeData != null && recordeData.size() > 0) {
				babyId = (String) recordeData.get("babyId");

				// vomit
				if ("n".equals((String) recordeData.get("vomit"))) {
					recordeData.put("vomit", "1");
				}
				if ("y".equals((String) recordeData.get("vomit"))) {
					recordeData.put("vomit", "2");
				}
				if ("0".equals((String) recordeData.get("vomit"))) {
					recordeData.put("vomit", "");
				}
				// feedWay
				if ("0".equals((String) recordeData.get("feedWay"))) {
					recordeData.put("feedWay", "");
				}
				// stoolStatus
				if ("2".equals((String) recordeData.get("stoolStatus"))) {
					recordeData.put("stoolStatus", "1");
				}
				if ("3".equals((String) recordeData.get("stoolStatus"))) {
					recordeData.put("stoolStatus", "2");
				}
				if ("0".equals((String) recordeData.get("stoolStatus"))) {
					recordeData.put("stoolStatus", "");
				}
				// face
				if ("99".equals((String) recordeData.get("face"))) {
					recordeData.put("face", "3");
				}
				if ("0".equals((String) recordeData.get("face"))) {
					recordeData.put("face", "");
				}
				// jaundice
				if ("0".equals((String) recordeData.get("jaundice"))) {
					recordeData.put("jaundice", "");
				}
				// bregmaStatus
				if ("0".equals((String) recordeData.get("bregmaStatus"))) {
					recordeData.put("bregmaStatus", "");
				}
				// eye
				if ("99".equals((String) recordeData.get("eye"))) {
					recordeData.put("eye", "2");
				}
				if ("0".equals((String) recordeData.get("eye"))) {
					recordeData.put("eye", "");
				}
				// ear
				if ("n".equals((String) recordeData.get("ear"))) {
					recordeData.put("ear", "1");
				}
				if ("y".equals((String) recordeData.get("ear"))) {
					recordeData.put("ear", "2");
				}
				if ("0".equals((String) recordeData.get("ear"))) {
					recordeData.put("ear", "");
				}
				// limbs
				if ("0".equals((String) recordeData.get("limbs"))) {
					recordeData.put("limbs", "");
				}
				// neck
				if ("n".equals((String) recordeData.get("neck"))) {
					recordeData.put("neck", "1");
				}
				if ("y".equals((String) recordeData.get("neck"))) {
					recordeData.put("neck", "2");
				}
				if ("0".equals((String) recordeData.get("neck"))) {
					recordeData.put("neck", "");
				}
				// skin
				if ("1".equals((String) recordeData.get("skin"))) {
					recordeData.put("skin", "1");
				}
				if ("7".equals((String) recordeData.get("skin"))) {
					recordeData.put("skin", "2");
				}
				if ("8".equals((String) recordeData.get("skin"))) {
					recordeData.put("skin", "3");
				}
				if ("99".equals((String) recordeData.get("skin"))) {
					recordeData.put("skin", "4");
				}
				if ("0".equals((String) recordeData.get("skin"))) {
					recordeData.put("skin", "");
				}
				// mouse
				if ("14".equals((String) recordeData.get("mouse"))) {
					recordeData.put("mouse", "2");
				}
				if ("0".equals((String) recordeData.get("mouse"))) {
					recordeData.put("mouse", "");
				}
				// nose

				if ("0".equals((String) recordeData.get("nose"))) {
					recordeData.put("nose", "");
				}
				// anal
				if ("0".equals((String) recordeData.get("anal"))) {
					recordeData.put("anal", "");
				}
				// heartlung
				if ("0".equals((String) recordeData.get("heartlung"))) {
					recordeData.put("heartlung", "");
				}
				// genitalia
				if ("0".equals((String) recordeData.get("genitalia"))) {
					recordeData.put("genitalia", "");
				}
				// abdominal
				if ("5".equals((String) recordeData.get("abdominal"))) {
					recordeData.put("abdominal", "2");
				}
				if ("0".equals((String) recordeData.get("abdominal"))) {
					recordeData.put("abdominal", "");
				}
				// spine
				if ("0".equals((String) recordeData.get("spine"))) {
					recordeData.put("spine", "");
				}
				// umbilical
				if ("0".equals((String) recordeData.get("umbilical"))) {
					recordeData.put("umbilical", "");
				}
				if ("9".equals((String) recordeData.get("umbilical"))) {
					recordeData.put("umbilical", "4");
				}
				// referral
				if ("n".equals((String) recordeData.get("referral"))) {
					recordeData.put("referral", "1");
				}
				if ("y".equals((String) recordeData.get("referral"))) {
					recordeData.put("referral", "2");
				}
				if ("0".equals((String) recordeData.get("referral"))) {
					recordeData.put("referral", "");
				}//
				SimpleDateFormat sdfv = new SimpleDateFormat("yyyy-MM-dd");
				if (null != recordeData.get("nextVisitDate")) {

					String nextVisitDate = sdfv.format(recordeData
							.get("nextVisitDate"));
					nextVisitDate = this.getFormDate(nextVisitDate);
					recordeData.put("nextVisitDate", nextVisitDate);
				}
				if (null != recordeData.get("visitDate")) {

					String visitDate = sdfv
							.format(recordeData.get("visitDate"));
					visitDate = this.getFormDate(visitDate);
					recordeData.put("visitDate", visitDate);
				}
				// guide
				String guide = (String) recordeData.get("guide");
				if (guide != null && guide.length() > 0) {
					if (guide.indexOf(",") != -1) {
						String[] arryData = guide.split(",");
						for (int k = 0; k < arryData.length; k++) {
							StringBuffer sbr = new StringBuffer("guide");
							recordeData.put(sbr.append(k + 1).toString(),
									arryData[k]);
						}
					}
					if (guide.length() == 1) {
						recordeData.put("guide1", guide);
					}
				}
				// 医生
				String visitDoctor = (String) recordeData.get("visitDoctor");
				Map<String, Object> visitDoctorData = new HashMap<String, Object>();
				visitDoctorData.put("visitDoctor", visitDoctor);
				Map<String, Object> visitDoctorDatas = SchemaUtil
						.setDictionaryMessageForList(visitDoctorData,
								BSCHISEntryNames.MHC_BabyVisitRecord);
				recordeData.put("visitDoctor",
						visitDoctorDatas.get("visitDoctor_text"));
			} else {
				return;
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获取儿保的新生儿随访基本的信息
		sb.setLength(0);
		param.clear();
		param.put("babyId", babyId);
		sb.append(
				" select  empiId as empiId,babyName as babyName,babySex as babySex,babyBirth as babyBirth,babyIdCard as babyIdCard,babyAddress as babyAddress,fatherName as fatherName,fatherJob as fatherJob ,"
						+ "fatherPhone as fatherPhone ,fatherBirth as fatherBirth,motherName as motherName ,motherJob as motherJob,motherPhone as motherPhone,"
						+ "motherBirth as motherBirth,gestation as gestation,pregnancyDisease as pregnancyDisease,otherDisease as otherDisease,deliveryUnit as deliveryUnit,"
						+ "birthStatus as birthStatus,otherStatus as otherStatus,asphyxia as asphyxia,apgar1 as apgar1 ,apgar5 as apgar5,malforMation as malforMation,malforMationDescription as malforMationDescription,"
						+ "hearingTest as hearingTest ,illnessScreening as illnessScreening ,otherIllness as otherIllness ,weight as weight,length as length ")
				.append(" from " + BSCHISEntryNames.MHC_BabyVisitInfo)
				.append(" where babyId=:babyId ");
		try {
			infoData = dao.doLoad(sb.toString(), param);

			if (infoData != null && infoData.size() > 0) {
				Map<String, Object> changeData = new HashMap<String, Object>();
				Map<String, Object> resultData = new HashMap<String, Object>();
				changeData.put("fatherJob",
						infoData.get("fatherJob") == null ? ""
								: (String) infoData.get("fatherJob"));
				// {fatherJob_text=国家机关、党群组织、企业、事业单位负责人, fatherJob=0}
				resultData = SchemaUtil.setDictionaryMessageForList(changeData,
						BSCHISEntryNames.MHC_BabyVisitInfo);
				infoData.put("fatherJob", resultData.get("fatherJob_text"));
				changeData.clear();
				resultData.clear();
				changeData.put("motherJob",
						infoData.get("motherJob") == null ? ""
								: (String) infoData.get("motherJob"));
				resultData = SchemaUtil.setDictionaryMessageForList(changeData,
						BSCHISEntryNames.MHC_BabyVisitInfo);
				infoData.put("motherJob", resultData.get("motherJob_text"));
				// 出生情况
				String birthStatus = (String) infoData.get("birthStatus");
				if (birthStatus != null && birthStatus.length() > 0) {
					if (birthStatus.indexOf(",") != -1) {
//						String[] arr = birthStatus.split(",");
//
//						if (arr.length == 2) {
//							infoData.put("birthStatus1", arr[0]);
//							infoData.put("birthStatus2", arr[1]);
//						}
					Map<String,Object> bData=this.returnDic(birthStatus,"birthStatus");
					infoData.putAll(bData);
					}
					if (birthStatus.length() == 1) {
						infoData.put("birthStatus1", birthStatus);
					}
				}
				// 母亲妊娠期患病情况
				if ("0".equals((String) infoData.get("pregnancyDisease"))) {
					infoData.put("pregnancyDisease", "");
				}
				// asphyxia
				if ("n".equals((String) infoData.get("asphyxia"))) {
					infoData.put("asphyxia", "1");
				}
				if ("y".equals((String) infoData.get("asphyxia"))) {
					infoData.put("asphyxia", "2");
				}
				if ("0".equals((String) infoData.get("asphyxia"))) {
					infoData.put("asphyxia", "");
				}
				// malforMation
				if ("n".equals((String) infoData.get("malforMation"))) {
					infoData.put("malforMation", "1");
				}
				if ("y".equals((String) infoData.get("malforMation"))) {
					infoData.put("malforMation", "2");
				}
				if ("0".equals((String) infoData.get("malforMation"))) {
					infoData.put("malforMation", "");
				}
				// hearingTest
				if ("0".equals((String) infoData.get("hearingTest"))) {
					infoData.put("hearingTest", "");
				}
				// illnessScreening
				if ("0".equals((String) infoData.get("illnessScreening"))) {
					infoData.put("illnessScreening", "");
				}
				// 出生日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (null != infoData.get("babyBirth")) {
					String babyBirth = sdf.format(infoData.get("babyBirth"));
					babyBirth = this.getFormDate(babyBirth);
					infoData.put("babyBirth", babyBirth);
				}
				empiId = (String) infoData.get("empiId");
				recordeData.putAll(infoData);
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//获取儿童档案编号
		sb.setLength(0);
		sb.append(" select phrId as phrId from EHR_HealthRecord where empiId=:empiId ");
		Map<String,Object> parameters=new HashMap<String,Object>();
		parameters.put("empiId", empiId);
		try {
			Map<String,Object> phData=dao.doLoad(sb.toString(), parameters);
			if(phData!=null&&phData.size()>0){
				String phrId=(String)phData.get("phrId");
				if(null!=phrId&&phrId.length()>0){
					response.put("phrId", phrId);
				}	
			}
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.putAll(recordeData);
	}
	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}
	// 日期转换
	public String getFormDate(String date) throws ParseException {
		StringBuffer returnDate = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date f = sdf.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(f);
		int year = cal.get(Calendar.YEAR); // 日
		int month = cal.get(Calendar.MONTH) + 1; // 月(从0开始, 一般加1，实际是否 Calendar
													// 里面常量的值决定的)
		int day = cal.get(Calendar.DAY_OF_MONTH); // 年

		returnDate.append(String.valueOf(year)).append("年")
				.append(String.valueOf(month)).append("月")
				.append(String.valueOf(day)).append("日");

		return returnDate.toString();

	}
	//字典转化:stringName  字符串的标识
		public Map<String ,Object> returnDic(String Stringdic,String stringName){
			Map<String,Object> dicDate=new HashMap<String,Object>();
			if (Stringdic.length() > 1) {
				String[] array = Stringdic.split(",");
				for (int i = 0; i < array.length; i++) {
					StringBuffer sbr = new StringBuffer(stringName);
					dicDate.put(sbr.append(i + 1).toString(), array[i]);
				}
			}
			if (Stringdic.length() <= 1) {
				dicDate.put(stringName+1, Stringdic);
			}
			return dicDate;
		}
}
