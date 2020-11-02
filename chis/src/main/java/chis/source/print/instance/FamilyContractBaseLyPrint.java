package chis.source.print.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;
import chis.source.fhr.FamilyRecordModule;
import chis.source.phr.HealthRecordModel;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.SchemaUtil;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

public class FamilyContractBaseLyPrint extends BSCHISPrint implements IHandler {

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		String fcId = (String) request.get("recordId");
		getDao(ctx);
		FamilyRecordModule frModule = new FamilyRecordModule(dao);
		try {
			Map<String, Object> contract = frModule.getFamilyContractBaseByPkey(fcId);
			String F_Id = (String) contract.get("F_Id");
			Map<String, Object> familyRecord = frModule.getFamilyRecordById(F_Id);
			contract = SchemaUtil.setDictionaryMessageForList(contract,
					"chis.application.fhr.schemas.EHR_FamilyContractBase");
			contract.put("familyAddr", familyRecord.get("familyAddr"));
			contract.put("familyDoc", contract.get("FC_Party") + "医生为乙方的家庭医生");
			String fc_id=contract.get("FC_Id")+"";
			response.put("recordId", fc_id.substring(fc_id.length()-8));
			if(familyRecord.get("familyHome")!=null && !"".equals(familyRecord.get("familyHome")+"")){
				response.put("familyHome", familyRecord.get("familyHome"));
			}else{
				response.put("familyHome", "/");
			}
			if(familyRecord.get("bookletNumber")!=null && !"".equals(familyRecord.get("bookletNumber")+"")){
				response.put("bookletNumber", familyRecord.get("bookletNumber"));
			}else{
				response.put("bookletNumber", "/");
			}
			if(contract.containsKey("FC_SingingType")){
				String FC_SingingType=contract.get("FC_SingingType")+"";
				if(FC_SingingType.length() >0 ){
					if(FC_SingingType.indexOf("01")>=0){
						response.put("FC_SingingType01", "基本服务组合");
					}else{
						response.put("FC_SingingType01", "");
					}
					if(FC_SingingType.indexOf("02")>=0){
						response.put("FC_SingingType01", "基本服务组合");
						response.put("FC_SingingType02", "诊疗优惠组合");
					}else{
						response.put("FC_SingingType02", "");
					}
					if(FC_SingingType.indexOf("03")>=0){
						response.put("FC_SingingType01", "基本服务组合");
						response.put("FC_SingingType02", "诊疗优惠组合");
						response.put("FC_SingingType03", "健康管理组合");
					}else{
						response.put("FC_SingingType03", "");
					}
				}
			}
			response.put("ownerName", familyRecord.get("ownerName"));
			if(contract.containsKey("FC_CreateDate") && contract.containsKey("FC_Begin")){
				String FC_CreateDate=contract.get("FC_CreateDate")+"";
				String FC_Begin=contract.get("FC_Begin")+"";
				if("2018".equals(FC_Begin.substring(0,4))){
					response.put("signNature", FC_Begin.substring(0,4)+" 续");
				}else
				if(FC_CreateDate.substring(0,4).equals(FC_Begin.substring(0,4))){
					response.put("signNature", FC_Begin.substring(0,4)+" 新");
				}else{
					response.put("signNature", FC_CreateDate.substring(0,4)+" 续");
				}
			}
			
			response.putAll(contract);
			change2String(response);
			sqlDate2String(response);
			String cysql="select b.personName as personName,b.idCard as idCard,a.masterFlag as masterFlag,a.signFlag as signFlag from EHR_HealthRecord a ,MPI_DemographicInfo b " +
					" where a.empiId=b.empiId and a.familyId=:F_Id";
			Map<String, Object> p=new HashMap<String, Object>();
			p.put("F_Id",F_Id);
			try {
				List<Map<String, Object>> cylist=dao.doQuery(cysql, p);
				if(cylist!=null && cylist.size()>0 ){
					for(int i=0;i<cylist.size();i++){
						if("y".equals(cylist.get(i).get("masterFlag")+"")){
							response.put("personName1", cylist.get(i).get("personName"));
							response.put("idCard1", cylist.get(i).get("idCard"));
							if("y".equals(cylist.get(i).get("signFlag")+"")){
								response.put("sign1","签约");
							}else if("n".equals(cylist.get(i).get("signFlag")+"")){
								response.put("sign1","拒签");
							}
						}
					}
				}
				if(cylist!=null && cylist.size()>0 ){
					int j=2;
					for(int i=0;i<cylist.size();i++){
						if("y".equals(cylist.get(i).get("masterFlag")+"")){
							continue;
						}else{
							response.put("personName"+j, cylist.get(i).get("personName"));
							response.put("idCard"+j, cylist.get(i).get("idCard"));
							if("y".equals(cylist.get(i).get("signFlag")+"")){
								response.put("sign"+j,"签约");
							}else if("n".equals(cylist.get(i).get("signFlag")+"")){
								response.put("sign"+j,"拒签");
							}
							j++;
						}
					}
				}
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}
}
