package chis.source.print.instance;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.S;
import ctd.util.context.Context;

public class FirstVisitRecord extends BSCHISPrint implements IHandler {
	protected String pregnantId;
	protected Context ctx;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		getDao(ctx);
		pregnantId = (String) request.get("pregnantId");
		String empiId = null;
		// MHC_PregnantRecord孕妇基本信息
		Map<String, Object> PregnantData = null;
		try {
			PregnantData = dao.doLoad(BSCHISEntryNames.MHC_PregnantRecord, pregnantId);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (PregnantData != null && PregnantData.size() > 0) {// 字典转化以及时间的处理
			//计算bmi
			double weight = (Double) PregnantData.get("weight");
			double height = (Double) PregnantData.get("height");
			DecimalFormat df = new DecimalFormat("0.00");
		    double bmi = weight/(height*height)*10000;
		    response.put("bmi", df.format(bmi));
			// 既往史
			String pastHistory = (String) PregnantData.get("pastHistory");
			if (pastHistory != null && pastHistory.length() > 0) {
				Map<String, Object> pastHistoryR = this.returnDic(
						pastHistory, "pastHistory");
				PregnantData.putAll(pastHistoryR);
			}
			// 家族史
			String familyHistory = (String) PregnantData
					.get("familyHistory");
			if (familyHistory != null && familyHistory.length() > 0) {
				Map<String, Object> familyHistoryR = this.returnDic(
						familyHistory, "familyHistory");
				PregnantData.putAll(familyHistoryR);
			}
			// 个人史
			String personHistory = (String) PregnantData
					.get("personHistory");
			if (personHistory != null && personHistory.length() > 0) {
				Map<String, Object> personHistoryR = this.returnDic(
						personHistory, "personHistory");
				PregnantData.putAll(personHistoryR);
			}
			empiId = (String) PregnantData.get("empiId");
			//妇科手术史
			String gynecologyOPS = (String) PregnantData.get("gynecologyOPS");
			if(S.isNotEmpty(gynecologyOPS)){
				PregnantData.put("gynecologyOPSKey", "2");
			}else{
				PregnantData.put("gynecologyOPSKey", "1");
			}
			//丈夫信息
			String husbandEmpiId = (String) PregnantData.get("husbandEmpiId");
			if(S.isNotEmpty(husbandEmpiId)){
				EmpiModel eModel = new EmpiModel(dao);
				Map<String, Object> hMap = null;
				try {
					hMap = eModel.getEmpiInfoByEmpiid(husbandEmpiId);
				} catch (ModelDataOperationException e) {
					e.printStackTrace();
				}
				if(hMap != null && hMap.size() > 0){
					response.put("husbandName", hMap.get("personName"));
					response.put("husbandPhone", hMap.get("mobileNumber"));
					Date husbandBirthday = (Date) hMap.get("birthday");
					response.put("husbandAGE", BSCHISUtil.calculateAge(husbandBirthday, null));//丈夫年龄
				}
			}
			response.putAll(PregnantData);
		}
			
		try {
			Map<String, Object> mapData = dao.doLoad(
					BSCHISEntryNames.MHC_FirstVisitRecord, pregnantId);
			if (mapData != null && mapData.size() > 0) {// 字典转化以及时间的处理
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (null != mapData.get("createDate")) {
					String createDate = sdf.format(mapData.get("createDate"));
					createDate = this.getFormDate(createDate);
					mapData.put("createDate", createDate);
				}
				// visitPrecontractTime
				if (null != mapData.get("visitPrecontractTime")) {
					String visitPrecontractTime = sdf.format(mapData
							.get("visitPrecontractTime"));
					visitPrecontractTime = this
							.getFormDate(visitPrecontractTime);
					mapData.put("visitPrecontractTime", visitPrecontractTime);
				}
				String suggestion = (String) mapData.get("suggestion");
				if(S.isNotEmpty(suggestion)){
					String[] suggs = suggestion.split(",");
					for(int i=0,len=suggs.length;i<len;i++){
						mapData.put("suggestion"+(i+1), suggs[i]);
					}
				}
				// 责任医生
				// 医生
				String visitDoctorCode = (String) mapData
						.get("visitDoctorCode");
				Map<String, Object> visitDoctorData = new HashMap<String, Object>();
				visitDoctorData.put("visitDoctorCode", visitDoctorCode);
				Map<String, Object> visitDoctorDatas = SchemaUtil
						.setDictionaryMessageForList(visitDoctorData,
								BSCHISEntryNames.MHC_FirstVisitRecord);
				mapData.put("visitDoctorCode",
						visitDoctorDatas.get("visitDoctorCode_text"));
				response.putAll(mapData);
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// MHC_PregnantWomanIndex
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(
					" select indexValue as indexValue,indexCode as indexCode,indexValue as indexValue,ifException as ifException,exceptionDesc as exceptionDesc from ")
					.append(" MHC_PregnantWomanIndex ")
					.append(" where pregnantId=:pregnantId ");
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> indexData = new HashMap<String, Object>();
			parameters.put("pregnantId", pregnantId);
			List<Map<String, Object>> list = dao.doQuery(sb.toString(),
					parameters);
			if (list != null && list.size() > 0) {// 字典转化以及时间的处理(包括单选框和文本框两种的数据)
				for (Map<String, Object> map : list) {
					StringBuffer hq = new StringBuffer("JY_");
					String ifException = (String) map.get("ifException");
					String indexCode = (String) map.get("indexCode");
					if (S.isNotEmpty(indexCode)) {
						hq.append(map.get("indexCode"));
						if(S.isNotEmpty(ifException)){
							indexData.put(hq.toString(), ifException);
						}
					}
					String exceptionDesc = (String) map.get("exceptionDesc");
					if (exceptionDesc != null && exceptionDesc.length() > 0) {
						indexData.put(hq.append("_other").toString(),
								exceptionDesc);
					}
					StringBuffer hqx = new StringBuffer("JY_");
					String indexValue = (String) map.get("indexValue");
					if (indexValue != null && indexValue.length() > 0) {
						if(indexValue.indexOf(",") > -1){
							String[] ivs = indexValue.split(",");
							String  icKey= hqx.append(map.get("indexCode")).toString();
							for(int i=0,len=ivs.length;i<len;i++){
								indexData.put(icKey+(i+1), ivs[i]);
							}
						}else{
							indexData.put(hqx.append(map.get("indexCode"))
									.toString(), indexValue);
						}
					}
				}
				response.putAll(indexData);
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		try {
			StringBuffer sbs = new StringBuffer();
			sbs.append(
					" select personName as personName,birthday as birthday,bloodTypeCode as bloodTypeCode,rhBloodCode as rhBloodCode from MPI_DemographicInfo ")
					.append(" where empiId=:empiId ");
			Map<String, Object> parametersEmpiId = new HashMap<String, Object>();
			parametersEmpiId.put("empiId", empiId);
			Map<String, Object> bloodInfo = dao.doLoad(sbs.toString(),
					parametersEmpiId);
			if (bloodInfo != null && bloodInfo.size() > 0) {
				response.putAll(bloodInfo);
				Map<String, Object> mm = SchemaUtil
						.setDictionaryMessageForList(bloodInfo,
								BSCHISEntryNames.MPI_DemographicInfo);
				response.put("bloodTypeCode", mm.get("bloodTypeCode_text"));
				response.put("rhBloodCode", mm.get("rhBloodCode_text"));
				//
				String csrq = bloodInfo.get("birthday") + "";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date dt2;
				try {
					dt2 = sdf.parse(csrq);
					Date dt1 = new Date();// 当前日期 
					int m = BSCHISUtil.calculateAge(dt2, dt1);
					String ageString = String.valueOf(m);
					response.put("age", ageString);//年龄
				} catch (ParseException e) {
					 System.out.println("转换年龄出错！");
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
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
		int month = cal.get(Calendar.MONTH) + 1; // 月(从0开始, 一般加1，实际是否 Calendar//
													// 里面常量的值决定的)
		int day = cal.get(Calendar.DAY_OF_MONTH); // 年
		returnDate.append(String.valueOf(year)).append("年")
				.append(String.valueOf(month)).append("月")
				.append(String.valueOf(day)).append("日");
		return returnDate.toString();
	}

	// 字典转化:stringName 字符串的标识
	public Map<String, Object> returnDic(String Stringdic, String stringName) {
		Map<String, Object> dicDate = new HashMap<String, Object>();
		if (Stringdic.length() > 1) {
			String[] array = Stringdic.split(",");
			for (int i = 0; i < array.length; i++) {
				StringBuffer sbr = new StringBuffer(stringName);
				dicDate.put(sbr.append(i + 1).toString(), array[i]);
			}
		}
		if (Stringdic.length() <= 1) {
			dicDate.put(stringName + 1, Stringdic);
		}
		return dicDate;
	}
}
