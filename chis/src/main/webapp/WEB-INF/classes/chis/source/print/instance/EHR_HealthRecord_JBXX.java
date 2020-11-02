package chis.source.print.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class EHR_HealthRecord_JBXX extends BSCHISPrint implements IHandler {
	protected String empiId;
	protected Context ctx;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		getDao(ctx);
		String cnd = "";

		/**
		 * 获取个人基本信息——MPI_DemographicInfo的数据
		 */
		if (empiId != null) {
			cnd = "['eq',['$','empiId'],['s','" + empiId + "']]";
		}
		List<Map<String, Object>> list_demographicInfo = null;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			list_demographicInfo = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.MPI_DemographicInfo);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (list_demographicInfo.size() > 0 && list_demographicInfo != null) {
			data = list_demographicInfo.get(0);

			if (data.get("insuranceCode") != null) {
				String insuranceCode = data.get("insuranceCode") + "";
				if (insuranceCode.contains("1")) {
					data.put("insuranceCode1", "√");
				}
				if (insuranceCode.contains("2")) {
					data.put("insuranceCode2", "√");
				}
				if (insuranceCode.contains("3")) {
					data.put("insuranceCode3", "√");
				}
				if (insuranceCode.contains("4")) {
					data.put("insuranceCode4", "√");
				}
				if (insuranceCode.contains("5")) {
					data.put("insuranceCode5", "√");
				}
				if (insuranceCode.contains("6")) {
					data.put("insuranceCode6", "√");
				}
				if (insuranceCode.contains("7")) {
					data.put("insuranceCode7", "√");
				}
				if (insuranceCode.contains("99")) {
					data.put("insuranceCode8", "√");
					//data.put("insuranceCode800", "");
				}
			}
			if (data.get("sexCode") != null) {
				String sexCode = data.get("sexCode") + "";
				if (sexCode.contains("0")) {
					data.put("sexCode0", "√");
				}
				if (sexCode.contains("1")) {
					data.put("sexCode1", "√");
				}
				if (sexCode.contains("2")) {
					data.put("sexCode2", "√");
				}
				if (sexCode.contains("9")) {
					data.put("sexCode9", "√");
				}
			}
			if (data.get("registeredPermanent") != null) {
				String registeredPermanent = data.get("registeredPermanent")
						+ "";
				if (registeredPermanent.contains("1")) {
					data.put("registeredPermanent1", "√");
				}
				if (registeredPermanent.contains("2")) {
					data.put("registeredPermanent2", "√");
				}
			}
			if (data.get("nationCode_text") != null) {
				String nationCode_text = data.get("nationCode_text") + "";
				data.put("nationCode_text", nationCode_text);
			}
			if (data.get("bloodTypeCode") != null) {
				String bloodTypeCode = data.get("bloodTypeCode") + "";
				if (bloodTypeCode.contains("1")) {
					data.put("bloodTypeCode1", "√");
				}
				if (bloodTypeCode.contains("2")) {
					data.put("bloodTypeCode2", "√");
				}
				if (bloodTypeCode.contains("3")) {
					data.put("bloodTypeCode3", "√");
				}
				if (bloodTypeCode.contains("4")) {
					data.put("bloodTypeCode4", "√");
				}
				if (bloodTypeCode.contains("5")) {
					data.put("bloodTypeCode5", "√");
				}
			}
			// rhBloodCode
			if (data.get("rhBloodCode") != null) {
				String rhBloodCode = data.get("rhBloodCode") + "";
				if (rhBloodCode.contains("1")) {
					data.put("rhBloodCode1", "√");
				}
				if (rhBloodCode.contains("2")) {
					data.put("rhBloodCode2", "√");
				}
				if (rhBloodCode.contains("3")) {
					data.put("rhBloodCode3", "√");
				}
			}
			// educationCode
			if (data.get("educationCode") != null) {
				String educationCode = data.get("educationCode") + "";
				if (educationCode.contains("9")) {
					data.put("educationCode9", "√");
				}
				if (educationCode.contains("8")) {
					data.put("educationCode8", "√");
				}
				if (educationCode.contains("7")) {
					data.put("educationCode7", "√");
				}
				if (educationCode.contains("6")) {
					data.put("educationCode6", "√");
				}
				if (educationCode.contains("2")) {
					data.put("educationCode2", "√");
				}
				if (educationCode.contains("91")) {
					data.put("educationCode91", "√");
				}
			}
			// workCode
			if (data.get("workCode") != null) {
				String workCode = data.get("workCode") + "";
				if (workCode.contains("0")) {
					data.put("workCode0", "√");
				}
				if (workCode.contains("1/2")) {
					data.put("workCode1", "√");
				}
				if (workCode.contains("3")) {
					data.put("workCode3", "√");
				}
				if (workCode.contains("4")) {
					data.put("workCode4", "√");
				}
				if (workCode.contains("5")) {
					data.put("workCode5", "√");
				}
				if (workCode.contains("9-9")) {
					data.put("workCode9", "√");
				}
				if (workCode.contains("X")) {
					data.put("workCodeX", "√");
				}
				if (workCode.contains("Y")) {
					data.put("workCodeY", "√");
				}
			}
			// maritalStatusCode
			if (data.get("maritalStatusCode") != null) {
				String maritalStatusCode = data.get("maritalStatusCode") + "";
				if (maritalStatusCode.contains("10")) {
					data.put("maritalStatusCode0", "√");
				}
				if (maritalStatusCode.contains("21")) {
					data.put("maritalStatusCode1", "√");
				}
				if (maritalStatusCode.contains("22")) {
					data.put("maritalStatusCode2", "√");
				}
				if (maritalStatusCode.contains("23")) {
					data.put("maritalStatusCode3", "√");
				}
				if (maritalStatusCode.contains("30")) {
					data.put("maritalStatusCode4", "√");
				}
				if (maritalStatusCode.contains("40")) {
					data.put("maritalStatusCode5", "√");
				}
				if (maritalStatusCode.contains("60")) {
					data.put("maritalStatusCode6", "√");
				}
			}
		}
		/**
		 * 个人健康档案的数据
		 */

		List<Map<String, Object>> list_HealthRecord = null;
		Map<String, Object> data1 = null;
		Map<String, Object> dataS = new HashMap<String,Object>();
		
		try {
			list_HealthRecord = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.EHR_HealthRecord);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (list_HealthRecord.size() > 0 && list_HealthRecord != null) {
			data1 = list_HealthRecord.get(0);

			if (data1.get("signFlag") != null) {
				String signFlag = data1.get("signFlag") + "";
				if (signFlag.contains("y")) {
					data.put("signFlagy", "√");
				}
				if (signFlag.contains("n")) {
					data.put("signFlagn", "√");
				}
			}
			if (data1.get("isAgrRegister") != null) {
				String isAgrRegister = data1.get("isAgrRegister") + "";
				if (isAgrRegister.contains("y")) {
					data.put("isAgrRegistery", "√");
				}
				if (isAgrRegister.contains("n")) {
					data.put("isAgrRegistern", "√");
				}
			}
			// incomeSource
			if (data1.get("incomeSource") != null) {
				String incomeSource = data1.get("incomeSource") + "";
				if (incomeSource.contains("1")) {
					data.put("incomeSource1", "√");
				}
				if (incomeSource.contains("2")) {
					data.put("incomeSource2", "√");
				}
				if (incomeSource.contains("3")) {
					data.put("incomeSource3", "√");
				}
			}
			// deadFlag
			if (data1.get("deadFlag") != null) {
				String deadFlag = data1.get("deadFlag") + "";
				if (deadFlag.contains("y")) {
					data.put("deadFlagy", "√");
				}
				if (deadFlag.contains("n")) {
					data.put("deadFlagn", "√");
				}
			}
			if (data1.get("masterFlag") != null) {
				String masterFlag = data1.get("masterFlag") + "";
				if (masterFlag.contains("y")) {
					data.put("masterFlagy", "√");
				}
				if (masterFlag.contains("n")) {
					data.put("masterFlagn", "√");
				}
			}
			if (data1.get("deadDate") != null) {
				String deadDate = data1.get("deadDate") + "";
				data.put("deadDate", deadDate);
			}
			if (data1.get("deadReason") != null) {
				String deadReason = data1.get("deadReason") + "";
				data.put("deadReason", deadReason);
			}
			if (data1.get("regionCode_text") != null) {
				String regionCode_text = data1.get("regionCode_text") + "";
				data.put("regionCode_text", regionCode_text);
			}//
			if (data1.get("manaDoctorId_text") != null) {
				String manaDoctorId_text = data1.get("manaDoctorId_text") + "";
				data.put(
						"manaDoctorId_text",manaDoctorId_text);
//						manaDoctorId_text.substring(0,
//								manaDoctorId_text.indexOf("-")));
			}
			// 组装管辖机构
//			String name = DictionaryController.instance()
//					.getDic("chis.dictionary.manageUnit")
//					.getText(data1.get("manaUnitId") + "");
			dataS.put("manaUnitId", data1.get("manaUnitId"));
		Map<String,Object>mm=SchemaUtil.setDictionaryMessageForForm(dataS, "chis.application.hr.schemas.EHR_HealthRecord");
		String name=mm.get("manaUnitId")+"";	
		String nameL=name.substring(name.indexOf("=")+1, name.indexOf(","));
			data.put("manaUnitId", nameL);

		}
		/**
		 * 
		 * 生活环境的数据
		 * 
		 */

		List<Map<String, Object>> list_FamilyMiddle = null;
		Map<String, Object> data2 = null;
		try {
			list_FamilyMiddle = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.EHR_FamilyMiddle);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (list_FamilyMiddle.size() > 0 && list_FamilyMiddle != null) {
			data2 = list_FamilyMiddle.get(0);

			if (data2.get("cookAirTool") != null) {
				String cookAirTool = data2.get("cookAirTool") + "";
				if (cookAirTool.contains("1")) {
					data.put("cookAirTool1", "√");
				}
				if (cookAirTool.contains("2")) {
					data.put("cookAirTool2", "√");
				}
				if (cookAirTool.contains("3")) {
					data.put("cookAirTool3", "√");
				}
				if (cookAirTool.contains("4")) {
					data.put("cookAirTool4", "√");
				}
				if (cookAirTool.contains("9")) {
					data.put("cookAirTool9", "√");
				}
			}

			if (data2.get("fuelType") != null) {
				String fuelType = data2.get("fuelType") + "";
				if (fuelType.contains("1")) {
					data.put("fuelType1", "√");
				}
				if (fuelType.contains("2")) {
					data.put("fuelType2", "√");
				}
				if (fuelType.contains("3")) {
					data.put("fuelType3", "√");
				}
				if (fuelType.contains("4")) {
					data.put("fuelType4", "√");
				}
				if (fuelType.contains("5")) {
					data.put("fuelType5", "√");
				}
				if (fuelType.contains("9")) {
					data.put("fuelType9", "√");
				}
			}
			if (data2.get("waterSourceCode") != null) {
				String waterSourceCode = data2.get("waterSourceCode") + "";
				if (waterSourceCode.contains("1")) {
					data.put("waterSourceCode1", "√");
				}
				if (waterSourceCode.contains("2")) {
					data.put("waterSourceCode2", "√");
				}
				if (waterSourceCode.contains("3")) {
					data.put("waterSourceCode3", "√");
				}
				if (waterSourceCode.contains("4")) {
					data.put("waterSourceCode4", "√");
				}
				if (waterSourceCode.contains("5")) {
					data.put("waterSourceCode5", "√");
				}
				if (waterSourceCode.contains("9")) {
					data.put("waterSourceCode9", "√");
				}
			}
			if (data2.get("washroom") != null) {
				String washroom = data2.get("washroom") + "";
				if (washroom.contains("1")) {
					data.put("washroom1", "√");
				}
				if (washroom.contains("2")) {
					data.put("washroom2", "√");
				}
				if (washroom.contains("3")) {
					data.put("washroom3", "√");
				}
				if (washroom.contains("4")) {
					data.put("washroom4", "√");
				}
				if (washroom.contains("5")) {
					data.put("washroom5", "√");
				}
				if (washroom.contains("9")) {
					data.put("washroom9", "√");
				}
			}
			if (data2.get("livestockColumn") != null) {
				String livestockColumn = data2.get("livestockColumn") + "";
				if (livestockColumn.contains("1")) {
					data.put("livestockColumn1", "√");
				}
				if (livestockColumn.contains("2")) {
					data.put("livestockColumn2", "√");
				}
				if (livestockColumn.contains("3")) {
					data.put("livestockColumn3", "√");
				}
			}

		}
		/**
		 * 个人过往史的数据
		 */
		List<Map<String, Object>> list_PastHistory = null;
		Map<String, Object> data3 = null;
		try {
			list_PastHistory = dao.doQuery(toListCnd(cnd), null,
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (list_PastHistory.size() > 0 && list_PastHistory != null) {
			data3 = list_PastHistory.get(0);
			String pastHisTypeCode=null;
			List<String> list_11=new ArrayList<String>();
			list_11.add("1101");
			list_11.add("1102");
			list_11.add("1103");
			list_11.add("1104");
			list_11.add("1105");
			list_11.add("1106");
			list_11.add("1107");
			list_11.add("1108");
			list_11.add("1109");
			list_11.add("1199");
			List<String> list_10=new ArrayList<String>();
			list_10.add("1001");
			list_10.add("1002");
			list_10.add("1003");
			list_10.add("1004");
			list_10.add("1005");
			list_10.add("1006");
			list_10.add("1007");
			list_10.add("1008");
			list_10.add("1009");
			list_10.add("1010");
			list_10.add("1011");
			list_10.add("1099");
			List<String> list_09=new ArrayList<String>();
			list_09.add("0901");
			list_09.add("0902");
			list_09.add("0903");
			list_09.add("0904");
			list_09.add("0905");
			list_09.add("0906");
			list_09.add("0907");
			list_09.add("0908");
			list_09.add("0909");
			list_09.add("0910");
			list_09.add("0911");
			list_09.add("0999");	
			List<String> list_08=new ArrayList<String>();
			list_08.add("0801");
			list_08.add("0802");
			list_08.add("0803");
			list_08.add("0804");
			list_08.add("0805");
			list_08.add("0806");
			list_08.add("0807");
			list_08.add("0808");
			list_08.add("0809");
			list_08.add("0810");
			list_08.add("0811");
			list_08.add("0899");	
			List<String> list_07=new ArrayList<String>();
			list_07.add("0701");
			list_07.add("0702");
			list_07.add("0703");
			list_07.add("0704");
			list_07.add("0705");
			list_07.add("0706");
			list_07.add("0707");
			list_07.add("0708");
			list_07.add("0709");
			list_07.add("0710");
			list_07.add("0711");
			list_07.add("0799");
			List<String> list_01=new ArrayList<String>();
			list_01.add("0101");
			list_01.add("0102");
			list_01.add("0103");
			list_01.add("0104");
			list_01.add("0109");
			List<String> list_12=new ArrayList<String>();
			list_12.add("1201");
			list_12.add("1202");
			list_12.add("1203");
			list_12.add("1204");
			List<String> list_02=new ArrayList<String>();
			list_02.add("0201");
			list_02.add("0202");
			list_02.add("0203");
			list_02.add("0204");
			list_02.add("0205");
			list_02.add("0206");
			list_02.add("0207");
			list_02.add("0208");
			list_02.add("0209");
			list_02.add("0210");
			list_02.add("0211");
			list_02.add("0212");
			list_02.add("0213");
			list_02.add("0214");
			list_02.add("0298");
			list_02.add("0299");
			list_02.add("02");
			int sxs=1;//输血史
			int wss=1;//外伤史
			int sss=1;//手术史
			for (Map<String, Object> o : list_PastHistory) {
				/**
				 * <item key="01" text="药物过敏史" /> <item key="02" text="疾病史" />
				 * <item key="03" text="手术史" /> <item key="04" text="输血史" />
				 * <item key="05" text="遗传病史" /> <item key="06" text="外伤史" />
				 * <item key="07" text="家族疾病史-父亲" /> <item key="08"
				 * text="家族疾病史-母亲" /> <item key="09" text="家族疾病史-兄弟姐妹" /> <item
				 * key="10" text="家族疾病史-子女" /> <item key="11" text="残疾状况" />
				 * <item key="12" text="暴露史" />
				 */
				if (o.get("pastHisTypeCode") != null
						&& (o.get("pastHisTypeCode") + "").length() > 0
						&& !"".equals(o.get("pastHisTypeCode"))) {
					 pastHisTypeCode=o.get("pastHisTypeCode")+"";
				   if(pastHisTypeCode.contains("11")){
					data.putAll(getPal(list_11,o.get("diseaseCode")+"","diseaseText"));
					//特殊情况
                    if(o.get("diseaseCode").equals("1199")){
                    	data.put("diseaseText119900", o.get("diseaseText"));
                    }
				}	
				//遗传病史
				if(pastHisTypeCode.contains("05")){
					if((o.get("diseaseCode")+"").equals("0502")){
						data.put("diseaseCode0502","√" );
						data.put("diseaseText0502", o.get("diseaseText")+"");
					}
				}
				//子女	
				if(pastHisTypeCode.contains("10")){
					data.putAll(getPal(list_10,o.get("diseaseCode")+"","diseaseText"));
					  if(o.get("diseaseCode").equals("1099")){
	                    	data.put("diseaseText109900", o.get("diseaseText"));
	                    }
					
				}		
				if(pastHisTypeCode.contains("09")){
					data.putAll(getPal(list_09,o.get("diseaseCode")+"","diseaseText"));
					  if(o.get("diseaseCode").equals("0999")){
	                    	data.put("diseaseText099900", o.get("diseaseText"));
	                    }
					
				}	
				if(pastHisTypeCode.contains("08")){
					data.putAll(getPal(list_08,o.get("diseaseCode")+"","diseaseText"));
					  if(o.get("diseaseCode").equals("0899")){
	                    	data.put("diseaseText089900", o.get("diseaseText"));
	                    }
					
				}		
				if(pastHisTypeCode.contains("07")){
					data.putAll(getPal(list_07,o.get("diseaseCode")+"","diseaseText"));
					  if(o.get("diseaseCode").equals("0799")){
	                    	data.put("diseaseText079900", o.get("diseaseText"));
	                    }
				}		
				//药物过敏史的数据	
				if(pastHisTypeCode.contains("01")){
					data.putAll(getPal(list_01,o.get("diseaseCode")+"","diseaseText"));
					  if(o.get("diseaseCode").equals("0109")){
	                    	data.put("diseaseText010900", o.get("diseaseText"));
	                    }
				}		
				//暴露史的数据	
				if(pastHisTypeCode.contains("12")){
					data.putAll(getPal(list_12,o.get("diseaseCode")+"","diseaseText"));
				}
				//输血史的数据	
				if(pastHisTypeCode.contains("04")){
					if((o.get("diseaseCode")+"").equals("0402")){
						data.put("diseaseCode0402","√" );
						data.put("diseaseText0402"+sxs, o.get("diseaseText")+"");
						data.put("diseaseText040200"+sxs, o.get("startDate")+"");
						sxs++;
					}
				}
				
				//外伤的数据	
				if(pastHisTypeCode.contains("06")){
					if((o.get("diseaseCode")+"").equals("0602")){
						data.put("diseaseCode0602","√" );
						data.put("diseaseText0602"+wss, o.get("diseaseText")+"");
						data.put("diseaseText060200"+wss, o.get("startDate")+"");
						wss++;
					}
				}
				//手术史的数据	
				if(pastHisTypeCode.contains("03")){
					if((o.get("diseaseCode")+"").equals("0302")){
						data.put("diseaseCode0302","√" );
						data.put("diseaseText0302"+sss, o.get("diseaseText")+"");
						data.put("diseaseText030200"+sss, o.get("startDate")+"");
						sss++;
					}
				}
				//疾病史的数据
				   if(pastHisTypeCode.contains("02")){
						data.putAll(getPal(list_02,o.get("diseaseCode")+"","diseaseText"));
						 if(o.get("diseaseCode").equals("0202")){
		                    	data.put("diseaseText020200", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0204")){
		                    	data.put("diseaseText020400", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0206")){
		                    	data.put("diseaseText020600", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0208")){
		                    	data.put("diseaseText020800", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0210")){
		                    	data.put("diseaseText021000", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0213")){
		                    	data.put("diseaseText021300", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0205")){
		                    	data.put("diseaseText020500", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0203")){
		                    	data.put("diseaseText020300", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0207")){
		                    	data.put("diseaseText020700", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0209")){
		                    	data.put("diseaseText020900", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0214")){
		                    	data.put("diseaseText021400", o.get("confirmDate"));//时间
		                    }
						 if(o.get("diseaseCode").equals("0211")){
		                    	data.put("diseaseText021100", o.get("confirmDate"));//时间
		                    }
						 
						//特殊情况
	                    if(o.get("diseaseCode").equals("0212")){
	                    	data.put("diseaseText021211", o.get("diseaseText"));//结果描述
	                    	data.put("diseaseText021200", o.get("confirmDate"));//时间
	                    }
	                    if(o.get("diseaseCode").equals("0298")){
	                    	data.put("diseaseText029811", o.get("diseaseText"));
	                    	data.put("diseaseText029800", o.get("confirmDate"));
	                    }
	                    if(o.get("diseaseCode").equals("0299")){
	                    	data.put("diseaseText029911", o.get("diseaseText"));
	                    	data.put("diseaseText029900", o.get("confirmDate"));
	                    }
	                    
	                    
	                    
	                    
	                    
	                    
					}
				

				}

			}

		}

		response.putAll(data);

	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}
	/**
	  * 组装过往史的特定方法:s  是字符串做拼接用
	  */
	
	 public Map<String,Object> getPal(List<String>args,String o,String s){
		 Map<String,Object> map_data=new HashMap<String,Object>();
		 for(String oo:args){
			 if(oo.equals(o)){
				 map_data.put(s+oo, "√");
			 }
		 }
		return map_data;
	 }
	
	
	
	
	
	

}
