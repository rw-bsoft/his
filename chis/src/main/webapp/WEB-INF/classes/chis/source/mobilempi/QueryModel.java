package chis.source.mobilempi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;
import chis.source.pub.PublicModel;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;

import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.security.Permission;

public class QueryModel extends PublicModel {
	public QueryModel(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	//查询个人的个人健康档案
	public Map<String, Object> queryInfo(String empiId, String schemaId,
			BaseDAO dao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSub2 = new ArrayList<Map<String, Object>>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();

			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";

			}
		}
		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "select "+sql+",d.smokeFreqCode as smokeFreqCode,d.drinkFreqCode as drinkFreqCode,"+
				" d.trainFreqCode as trainFreqCode,d.tabooFood as tabooFood,d.eateHabit as eateHabit,"+
				" e.cookAirTool as cookAirTool,e.fuelType as fuelType,e.livestockColumn as livestockColumn,"+
				" e.washroom as washroom,e.waterSourceCode as waterSourceCode "+ 
				" from  MPI_DemographicInfo b left join EHR_healthrecord a on a.empiId=b.empiId left join "+ 
				" EHR_LifeStyle d on a.empiId = d.empiId left join EHR_FamilyMiddle e on a.empiId = e.empiId" +
				" where a.empiId='"+empiId+"'";
		try {
			result = dao.doSqlLoad(sqls, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String sql2 = "select a.pastHisTypeCode as pastHisTypeCode ,a.diseaseText as diseaseText," +
				" a.diseaseCode as diseaseCode,a.recordDate as recordDate" +
				" from EHR_PastHistory a where a.pastHisTypeCode<>'01' and  a.empiId='"+empiId+"'";
		try {
			listSub = dao.doSqlQuery(sql2, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String sql3 = "select a.pastHisTypeCode as pastHisTypeCode ,a.diseaseText as diseaseText," +
				" a.diseaseCode as diseaseCode,a.recordDate as recordDate" +
				" from EHR_PastHistory a where a.pastHisTypeCode='01' and a.empiId='"+empiId+"'";
		try {
			listSub2 = dao.doSqlQuery(sql3, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		result.put("PASTHISTORY", listSub);
		result.put("ALLERGY", listSub2);
		return result;
	}
	
	//查询个人档案  2019-06-20 Wangjl
	public Map<String, Object> querygrInfo(String empiId, String schemaId,
			BaseDAO dao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		Map<String, Object> parameters2 = new HashMap<String, Object>();
		Map<String, Object> parameters3 = new HashMap<String, Object>();
		 Map<String, Object> parameters4 = new HashMap<String, Object>();
		 Map<String, Object> parameters5 = new HashMap<String, Object>();
		 Map<String, Object> parameters6 = new HashMap<String, Object>();
		 Map<String, Object> parameters7 = new HashMap<String, Object>();
		 Map<String, Object> parameters8 = new HashMap<String, Object>();
		 Map<String, Object> parameters9 = new HashMap<String, Object>();
		 Map<String, Object> parameters10 = new HashMap<String, Object>();
		 Map<String, Object> parameters11 = new HashMap<String, Object>();
		 Map<String, Object> parameters12 = new HashMap<String, Object>();
		 Map<String, Object> parameters13 = new HashMap<String, Object>();
		 Map<String, Object> parameters14 = new HashMap<String, Object>();
		 Map<String, Object> parameters15 = new HashMap<String, Object>();
		 Map<String, Object> parameters16 = new HashMap<String, Object>();
		 Map<String, Object> parameters17 = new HashMap<String, Object>();
		 Map<String, Object> parameters18 = new HashMap<String, Object>();
		 Map<String, Object> parameters19 = new HashMap<String, Object>();
		 Map<String, Object> parameters20 = new HashMap<String, Object>();
		 Map<String, Object> parameters21 = new HashMap<String, Object>();
		 Map<String, Object> parameters22 = new HashMap<String, Object>();
		 Map<String, Object> parameters23 = new HashMap<String, Object>();
		 Map<String, Object> parameters24 = new HashMap<String, Object>();
		 Map<String, Object> parameters25 = new HashMap<String, Object>();
		 Map<String, Object> parameters26 = new HashMap<String, Object>();
		 Map<String, Object> parameters27 = new HashMap<String, Object>();
		 Map<String, Object> parameters28 = new HashMap<String, Object>();
		 Map<String, Object> parameters29 = new HashMap<String, Object>();
		 Map<String, Object> parameters30 = new HashMap<String, Object>();
		 Map<String, Object> parameters31 = new HashMap<String, Object>();
		 Map<String, Object> parameters32 = new HashMap<String, Object>();
		 Map<String, Object> parameters33 = new HashMap<String, Object>();
		 Map<String, Object> parameters34 = new HashMap<String, Object>();
		 Map<String, Object> parameters35 = new HashMap<String, Object>();
		 Map<String, Object> parameters36 = new HashMap<String, Object>();
		 Map<String, Object> parameters37 = new HashMap<String, Object>();
		 Map<String, Object> parameters38 = new HashMap<String, Object>();
		 Map<String, Object> parameters39 = new HashMap<String, Object>();
		 Map<String, Object> parameters40 = new HashMap<String, Object>();
		 Map<String, Object> parameters41 = new HashMap<String, Object>();
		 Map<String, Object> parameters42 = new HashMap<String, Object>();
		 Map<String, Object> parameters43 = new HashMap<String, Object>();
		 Map<String, Object> parameters44 = new HashMap<String, Object>();
		 Map<String, Object> parameters45 = new HashMap<String, Object>();
		 Map<String, Object> parameters46 = new HashMap<String, Object>();
		 Map<String, Object> parameters47 = new HashMap<String, Object>();
		 Map<String, Object> parameters48 = new HashMap<String, Object>();
		 Map<String, Object> parameters49 = new HashMap<String, Object>();
		 Map<String, Object> parameters50 = new HashMap<String, Object>();
		 Map<String, Object> parameters51 = new HashMap<String, Object>();
		 Map<String, Object> parameters52 = new HashMap<String, Object>();
		 Map<String, Object> parameters53 = new HashMap<String, Object>();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSub2 = new ArrayList<Map<String, Object>>();

		String sqlName = " ";
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();

			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";

			}
		}
		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "select "+sql+",a.manaUnitId as manaUnitId,a.manaDoctorId as manaDoctorId,a.createUnit as createUnit,a.createUser as createUser,nvl(c.personName,'') as  manaDoctorId_text, d.organizname as manaUnitId_text,a.createDate as createDate,b.contact as contact,b.contactphone as contactphone,a.signFlag as signFlag,a.isAgrRegister as isAgrRegister,b.registeredPermanent as registeredPermanen,b.nationCode as nationCode,b.bloodTypeCode as bloodTypeCode,b.rhBloodCode as rhBloodCode,b.educationCode as educationCode,b.workCode as workCode,b.maritalStatusCode as maritalStatusCode,b.insuranceCode as insuranceCode,a.regionCode as regionCode,a.regioncode_text as regioncode_Text,a.incomeSource as incomeSource,a.masterFlag as masterFlag,a.relaCode as relaCode from EHR_healthrecord a left join mpi_demographicinfo b on a.empiId=b.empiId"+ 
				" left join sys_personnel c on  a.manadoctorid=c.personid left join sys_organization d on a.manaunitid= d.organizcode"+
				" where a.status in ('0','2') and a.empiId='"+empiId+"'";
		try {
			parameters1 = dao.doSqlLoad(sqls, parameters);
			
			if (parameters1.get("BIRTHDAY") != null) {
				Date birthday = (Date) parameters1.get("BIRTHDAY");
				int age = MobileAppService.calculateAge(birthday, null);
				parameters1.put("AGE", age);
			} else {
				parameters1.put("AGE", "");
			}
			if (parameters1.get("CREATEUSER") != null) {
				String createuser = (String) parameters1.get("CREATEUSER");
				String sql11="select  c.personName as createUser_text from sys_personnel c  where c.personid='"+createuser+"'";
				Map<String, Object> sql111 = null;
				sql111 = dao.doSqlLoad(sql11, null);
				parameters1.put("CREATEUSER_TEXT", sql111.get("CREATEUSER_TEXT"));
			}else {
				parameters1.put("CREATEUSER_TEXT", "");
			}
			if (parameters1.get("CREATEUNIT") != null) {
				String createUnit = (String) parameters1.get("CREATEUNIT");
				String sql12="select  c.organizname as createUnit_text	 from sys_organization c  where c.organizcode='"+createUnit+"'";
				Map<String, Object> sql121 = null;
				sql121 = dao.doSqlLoad(sql12, null);
				parameters1.put("CREATEUNIT_TEXT", sql121.get("CREATEUNIT_TEXT"));
			}else {
				parameters1.put("CREATEUNIT_TEXT", "");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		
		String sql2 = "select a.pastHisTypeCode as pastHisTypeCode ,a.diseaseText as diseaseText," +
				" a.diseaseCode as diseaseCode,a.confirmdate as recordDate" +
				" from EHR_PastHistory a where  a.empiId='"+empiId+"'";
		try {
			listSub = dao.doSqlQuery(sql2, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String sql3 = "select d.middleId as lifestyleid,d.fuelType as fuelType,"+
		" d.cookAirTool as cookAirTool,d.waterSourceCode as waterSourceCode,d.washroom as washroom,"+
		" d.livestockColumn as livestockColumn from EHR_HealthRecord a left join  EHR_FamilyMiddle d on a. empiId=d.empiId  where a.status in ('0','2') and d.empiId='"+empiId+"'";
		try {
			parameters2 = dao.doSqlLoad(sql3, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String sql4="select to_char(wm_concat(t.diseasecode)) as drugAllergens from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '01'";
		try {
			parameters4 = dao.doSqlLoad(sql4, parameters);
			if (parameters4!= null) {
			parameters52.put("drugAllergens", parameters4.get("DRUGALLERGENS"));
			}else {
				parameters52.put("drugAllergens", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql5="select to_char(wm_concat(t.diseasecode)) as riskFactorsTypeCod from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '12'";
		try {
			parameters5 = dao.doSqlLoad(sql5, parameters);
			if (parameters5!= null) {
			parameters52.put("riskFactorsTypeCod", parameters5.get("RISKFACTORSTYPECOD"));
			}else {
				parameters52.put("riskFactorsTypeCod", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql6="select to_char(wm_concat(t.diseasecode)) as pastHiss from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '02'";
		try {
			parameters6 = dao.doSqlLoad(sql6, parameters);
			if (parameters6!= null) {
			parameters52.put("pastHiss", parameters6.get("PASTHISS"));
		    }else {
			parameters52.put("pastHiss", null);
		    }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql7="select distinct t.diseasecode as operationHistorys from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '03'";
		try {
			parameters7 = dao.doSqlLoad(sql7, parameters);
			if (parameters7!= null) {
			parameters52.put("operationHistorys", parameters7.get("OPERATIONHISTORYS"));
			 }else {
					parameters52.put("operationHistorys", null);
		  }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql8="select distinct t.diseasecode as bloodTransfusions from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '04'";
		try {
			parameters8 = dao.doSqlLoad(sql8, parameters);
			if (parameters8!= null) {
			parameters52.put("bloodTransfusions", parameters8.get("BLOODTRANSFUSIONS"));
			 }else {
					parameters52.put("bloodTransfusions", null);
		     }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql9="select to_char(wm_concat(t.diseasecode)) as geneticDiseaseHist from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '05'";
		try {
			parameters9 = dao.doSqlLoad(sql9, parameters);
			if (parameters9!= null) {
			parameters52.put("geneticDiseaseHist", parameters9.get("GENETICDISEASEHIST"));
			}else {
				parameters52.put("geneticDiseaseHist", null);
	     }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql10="select distinct t.diseasecode as traumas from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '06'";
		try {
			parameters10 = dao.doSqlLoad(sql10, parameters);
			if (parameters10!= null) {
			parameters52.put("traumas", parameters10.get("TRAUMAS"));
			}else {
				parameters52.put("traumas", null);
	     }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql11="select to_char(wm_concat(t.diseasecode)) as fqs from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '07'";
		try {
			parameters11 = dao.doSqlLoad(sql11, parameters);
			if (parameters11!= null) {
			parameters52.put("fqs", parameters11.get("FQS"));
			}else {
				parameters52.put("fqs", null);
	     }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql12="select to_char(wm_concat(t.diseasecode)) as mqs from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '08'";
		try {
			parameters12 = dao.doSqlLoad(sql12, parameters);
			if (parameters12!= null) {
			parameters52.put("mqs", parameters12.get("MQS"));
			}else {
				parameters52.put("mqs", null);
	     }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql13="select to_char(wm_concat(t.diseasecode)) as xds from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '09'";
		try {
			parameters13 = dao.doSqlLoad(sql13, parameters);
			if (parameters13!= null) {
			parameters52.put("xds", parameters13.get("XDS"));
			}else {
				parameters52.put("xds", null);
	       }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql14="select to_char(wm_concat(t.diseasecode)) as zns from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '10'";
		try {
			parameters14 = dao.doSqlLoad(sql14, parameters);
			if (parameters14!= null) {
			parameters52.put("zns", parameters14.get("ZNS"));
			}else {
				parameters52.put("zns", null);
	       }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql15="select to_char(wm_concat(t.diseasecode)) as disabilityCodes from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.pasthistypecode = '11'";
		try {
			parameters15 = dao.doSqlLoad(sql15, parameters);
			if (parameters15!= null) {
			parameters52.put("disabilityCodes", parameters15.get("DISABILITYCODES"));
			}else {
				parameters52.put("disabilityCodes", null);
	       }
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql16="select t.diseasetext as drugAllergenOther from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0109'";
		try {
			parameters16 = dao.doSqlLoad(sql16, null);
			if (parameters16 != null) {
			   parameters52.put("drugAllergenOther", parameters16.get("DRUGALLERGENOTHER"));
			}else {
				parameters52.put("drugAllergenOther", null);
			}  
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql17="select t.confirmdate as  Hypertension from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0202'";
		try {
			parameters17 = dao.doSqlLoad(sql17, parameters);
			if (parameters17!= null) {
			parameters52.put("Hypertension", parameters17.get("HYPERTENSION"));
			}else {
				parameters52.put("Hypertension", null);
			} 
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql18="select t.confirmdate as Diabetes from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0203'";
		try {
			parameters18 = dao.doSqlLoad(sql18, parameters);
			if (parameters18!= null) {
		     	parameters52.put("Diabetes", parameters18.get("DIABETES"));
			}else {
				parameters52.put("Diabetes", null);
			} 
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql19="select t.confirmdate as ache from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0204'";
		try {
			parameters19 = dao.doSqlLoad(sql19, parameters);
			if (parameters19!= null) {
			parameters52.put("ache", parameters19.get("ACHE"));
			}else {
				parameters52.put("ache", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql20="select t.confirmdate as manxingzuse from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0205'";
		try {
			parameters20 = dao.doSqlLoad(sql20, parameters);
			if (parameters20!= null) {
			parameters52.put("manxingzuse", parameters20.get("MANXINGZUSE"));
			}else {
				parameters52.put("manxingzuse", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql21="select t.confirmdate as stomacheTime  from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0206'";
		try {
			parameters21 = dao.doSqlLoad(sql21, parameters);
			if (parameters21!= null) {
			parameters52.put("stomacheTime", parameters21.get("STOMACHETIME"));
			}else {
				parameters52.put("stomacheTime", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql22="select t.confirmdate as nczqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0207'";
		try {
			parameters22 = dao.doSqlLoad(sql22, parameters);
			if (parameters22!= null) {
			parameters52.put("nczqzsj", parameters22.get("NCZQZSJ"));
			}else {
				parameters52.put("nczqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql23="select t.confirmdate as jinshenbin  from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0208'";
		try {
			parameters23 = dao.doSqlLoad(sql23, parameters);
			if (parameters23 != null) {
			parameters52.put("jinshenbin", parameters23.get("JINSHENBIN"));
			}else {
				parameters52.put("jinshenbin", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql24="select t.confirmdate as jiehebing from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0209'";
		try {
			parameters24 = dao.doSqlLoad(sql24, parameters);
			if (parameters24!= null) {
			parameters52.put("jiehebing", parameters24.get("JIEHEBING"));
			}else {
				parameters52.put("jiehebing", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql25="select t.confirmdate as ganyan from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0210'";
		try {
			parameters25 = dao.doSqlLoad(sql25, parameters);
			if (parameters25 != null) {
			parameters52.put("ganyan", parameters25.get("GANYAN"));
			}else {
				parameters52.put("ganyan", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql26="select t.confirmdate as xtjxqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0211'";
		try {
			parameters26 = dao.doSqlLoad(sql26, parameters);
			if (parameters26 != null) {
			parameters52.put("xtjxqzsj", parameters26.get("XTJXQZSJ"));
			}else {
				parameters52.put("xtjxqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql27="select t.confirmdate as zhiyebinshijian from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0212'";
		try {
			parameters27 = dao.doSqlLoad(sql27, parameters);
			if (parameters27 != null) {
			parameters52.put("zhiyebinshijian", parameters27.get("ZHIYEBINSHIJIAN"));
			}else {
				parameters52.put("zhiyebinshijian", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql28="select t.confirmdate as szjbqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0213'";
		try {
			parameters28 = dao.doSqlLoad(sql28, parameters);
			if (parameters28 != null) {
			parameters52.put("szjbqzsj", parameters28.get("SZJBQZSJ"));
			}else {
				parameters52.put("szjbqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql29="select t.confirmdate as pxqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0214'";
		try {
			parameters29 = dao.doSqlLoad(sql29, parameters);
			if (parameters29 != null) {
			parameters52.put("pxqzsj", parameters29.get("PXQZSJ"));
			}else {
				parameters52.put("pxqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql30="select t.confirmdate as qtfdcrbqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0298'";
		try {
			parameters30 = dao.doSqlLoad(sql30, parameters);
			if (parameters30 != null) {
			parameters52.put("qtfdcrbqzsj", parameters30.get("QTFDCRBQZSJ"));
			}else {
				parameters52.put("qtfdcrbqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql31="select t.confirmdate as qtjbsqzsj from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0299'";
		try {
			parameters31 = dao.doSqlLoad(sql31, parameters);
			if (parameters31 != null) {
			parameters52.put("qtjbsqzsj", parameters31.get("QTJBSQZSJ"));
			}else {
				parameters52.put("qtjbsqzsj", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql32="select t.diseasetext as chuanranbin from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0298'";
		try {
			parameters32 = dao.doSqlLoad(sql32, parameters);
			if (parameters32 != null) {
			parameters52.put("chuanranbin", parameters32.get("CHUANRANBIN"));
			}else {
				parameters52.put("chuanranbin", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql33="select t.diseasetext as qtjbs from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0299'";
		try {
			parameters33 = dao.doSqlLoad(sql33, parameters);
			if (parameters33 != null) {
			parameters52.put("qtjbs", parameters33.get("QTJBS"));
			}else {
				parameters52.put("qtjbs", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql34="select t.diseasetext as zhiyebin from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0212'";
		try {
			parameters34 = dao.doSqlLoad(sql34, parameters);
			if (parameters34 != null) {
			parameters52.put("zhiyebin", parameters34.get("ZHIYEBIN"));
			}else {
				parameters52.put("zhiyebin", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql35="select * from ( select t.diseasetext as operationHistory,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0302') where rn=1";
		try {
			parameters35 = dao.doSqlLoad(sql35, parameters);
			if (parameters35 != null) {
			parameters52.put("operationHistory", parameters35.get("OPERATIONHISTORY"));
			}else {
				parameters52.put("operationHistory", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql36="select * from (select t.startdate as operationTime,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0302') where rn=1";
		try {
			parameters36 = dao.doSqlLoad(sql36, parameters);
			if (parameters36 != null) {
			parameters52.put("operationTime", parameters36.get("OPERATIONTIME"));
			}else {
				parameters52.put("operationTime", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql37="select * from (select t.diseasetext as operationHistory1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0302') where rn=2";
		try {
			parameters37 = dao.doSqlLoad(sql37, parameters);
			if (parameters37!= null) {
			parameters52.put("operationHistory1", parameters37.get("OPERATIONHISTORY1"));
			}else {
				parameters52.put("operationHistory1", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql38=" select * from (select t.startdate as operationTime1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0302') where rn=2";
		try {
			parameters38 = dao.doSqlLoad(sql38, parameters);
			if (parameters38 != null) {
			parameters52.put("operationTime1", parameters38.get("OPERATIONTIME1"));
			}else {
				parameters52.put("operationTime1", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql39=" select * from (select t.diseasetext as traumaName,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0602') where rn=1";
		try {
			parameters39 = dao.doSqlLoad(sql39, parameters);
			if (parameters39 != null) {
			parameters52.put("traumaName", parameters39.get("TRAUMANAME"));
			}else {
				parameters52.put("traumaName", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql40="select * from (select t.startdate as traumaTime,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0602') where rn=1";
		try {
			parameters40 = dao.doSqlLoad(sql40, parameters);
			if (parameters40 != null) {
			parameters52.put("traumaTime", parameters40.get("TRAUMATIME"));
			}else {
				parameters52.put("traumaTime", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql41="select * from (select t.diseasetext as traumaName1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0602') where rn=2";
		try {
			parameters41 = dao.doSqlLoad(sql41, parameters);
			if (parameters41 != null) {
			parameters52.put("traumaName1", parameters41.get("TRAUMANAME1"));
			}else {
				parameters52.put("traumaName1", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql42="select * from (select t.startdate as traumaTime1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0602') where rn=2";
		try {
			parameters42 = dao.doSqlLoad(sql42, parameters);
			if (parameters42 != null) {
			parameters52.put("traumaTime1", parameters42.get("TRAUMATIME1"));
			}else {
				parameters52.put("traumaTime1", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql43="select * from (select t.diseasetext as transfusionReason,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0402') where rn=1";
		try {
			parameters43 = dao.doSqlLoad(sql43, parameters);
			if (parameters43 != null) {
			parameters52.put("transfusionReason", parameters43.get("TRANSFUSIONREASON"));
			}else {
				parameters52.put("transfusionReason", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql44="select * from (select t.startdate as transfusionsTime,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0402') where rn=1";
		try {
			parameters44 = dao.doSqlLoad(sql44, parameters);
			if (parameters44 != null) {
			parameters52.put("transfusionsTime", parameters44.get("TRANSFUSIONSTIME"));
			}else {
				parameters52.put("transfusionsTime", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql45="select * from (select t.diseasetext as transfusionReason1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0402') where rn=2";
		try {
			parameters45 = dao.doSqlLoad(sql45, parameters);
			if (parameters45 != null) {
			parameters52.put("transfusionReason1", parameters45.get("TRANSFUSIONREASON1"));
			}else {
				parameters52.put("transfusionReason1",null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql46="select * from (select t.startdate as transfusionTime1,row_number()over(partition by t.empiId order by t.PASTHISTORYID) rn from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0402') where rn=2";
		try {
			parameters46 = dao.doSqlLoad(sql46, parameters);
			if (parameters46!= null) {
			parameters52.put("transfusionTime1", parameters46.get("TRANSFUSIONTIME1"));
			}else {
				parameters52.put("transfusionTime1", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql47="select t.diseasetext as fqo from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0799'";
		try {
			parameters47 = dao.doSqlLoad(sql47, parameters);
			if (parameters47 != null) {
			parameters52.put("fqo", parameters47.get("FQO"));
			}else {
				parameters52.put("fqo", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql48="select t.diseasetext as mqsqt from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0899'";
		try {
			parameters48 = dao.doSqlLoad(sql48, parameters);
			if (parameters48 != null) {
			parameters52.put("mqsqt", parameters48.get("MQSQT"));
			}else {
				parameters52.put("mqsqt",null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql49="select t.diseasetext as xdo from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0999'";
		try {
			parameters49 = dao.doSqlLoad(sql49, parameters);
			if (parameters49 != null) {
			parameters52.put("xdo", parameters49.get("XDO"));
			}else {
				parameters52.put("xdo", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql50="select t.diseasetext as zno from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '1099'";
		try {
			parameters50 = dao.doSqlLoad(sql50, parameters);
			if (parameters50 != null) {
			parameters52.put("zno", parameters50.get("ZNO"));
			}else {
				parameters52.put("zno",null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql51="select t.diseasetext as ycbsjbmc from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '0502'";
		try {
			parameters51 = dao.doSqlLoad(sql51, parameters);
			if (parameters51 != null) {
			parameters52.put("ycbsjbmc", parameters51.get("YCBSJBMC"));
			}else {
				parameters52.put("ycbsjbmc", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		String sql52="select t.diseasetext as qtcjqk from ehr_pasthistory t  where t.empiid = '"+empiId+"' and t.diseasecode = '1199'";
		try {
			parameters53 = dao.doSqlLoad(sql52, parameters);
			if (parameters53 != null) {
			parameters52.put("qtcjqk", parameters53.get("QTCJQK"));
			}else {
				parameters52.put("qtcjqk", null);
			}
		 } catch (PersistentDataOperationException e) {
			e.printStackTrace();
	 	}
		

		
		result.put("BASEINFO", parameters1);
		result.put("HISTORY", listSub);
		result.put("LIVESYS", parameters2);
		result.put("HISTORYLIST", parameters52);
		return result;
	}
	//查询个人的个人信息
	public Map<String, Object> getQueryInfo(String empiId, String schemaId,
			BaseDAO dao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listSub2 = new ArrayList<Map<String, Object>>();
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();

			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";

			}
		}
		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "select "+sql+" from  MPI_DemographicInfo b,EHR_healthrecord a" +
				" where a.empiId=b.empiId and a.empiId='"+empiId+"'";
		try {
			result = dao.doSqlLoad(sqls, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}

	//查询个人的随访信息
	public Map<String, Object> query(String empiId, String schemaId, BaseDAO dao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();

			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";

			}
		}
		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "select "
				+ sql
				+ " from PUB_VisitPlan a join MPI_DemographicInfo b on a.empiId=b.empiId "
				+ " join EHR_HealthRecord c on a.empiId=c.empiId where a.empiId='"
				+ empiId
				+ "'and a.businesstype in (1,2,4) and  "
				+ "plandate>ADD_MONTHS(to_date(to_char(sysdate, 'yyyy-mm-dd'),'yyyy-mm-dd'), -12)";
		try {
			result = dao.doSqlLoad(sqls, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	// 通过visitId获取随访信息
	public Map<String, Object> queryToVisitId(String visitId, String schemaId,
			BaseDAO dao, Map<String, Object> req) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		Schema sc = null;
		String businesstype = "";
		if (req.containsKey("businesstype")) {
			businesstype = (String) req.get("businesstype");
		}
		if (businesstype.equals("1")) {
			schemaId = "chis.application.hy.schemas.MDC_HypertensionVisit";
		}
		if (businesstype.equals("2")) {
			schemaId = "chis.application.hy.schemas.MDC_DiabetesVisit";

		}
		if (businesstype.equals("4")) {

			schemaId = "chis.application.hy.schemas.MDC_OldPeopleVisit";
		}

		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();

			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";

			}
		}

		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "";
		if (businesstype.equals("1")) {
			sqls = "select "
					+ sql
					+ " from  where MDC_HypertensionVisit a join MPI_DemographicInfo b  a.empiId=b.empiId join EHR_HealthRecord c on a.empiId = c.empiId "
					+ "join MDC_HypertensionRecord d on a.empiId = d.empiId and a.visitId='"
					+ visitId + "'";
		}
		if (businesstype.equals("2")) {
			sqls = "select "
					+ sql
					+ " from  where MDC_HypertensionVisit a join MPI_DemographicInfo b  a.empiId=b.empiId join EHR_HealthRecord c on a.empiId = c.empiId "
					+ "join MDC_DiabetesRecord d on a.empiId = d.empiId and a.visitId='"
					+ visitId + "'";

		}
		if (businesstype.equals("4")) {

			sqls = "select "
					+ sql
					+ " from  where MDC_HypertensionVisit a join MPI_DemographicInfo b  a.empiId=b.empiId join EHR_HealthRecord c on a.empiId = c.empiId "
					+ "join PUB_VisitPlan d on a.empiId = d.empiId and a.visitId='"
					+ visitId + "'";
		}
		try {
			result = dao.doSqlLoad(sqls, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public Map<String, Object> queryRecordList2(String schemaId,
			String queryCnd, int pageSize, int pageNo) {
		Schema sc = null;
		String tableNames = schemaId + " a,";
		if (tableNames
				.equals("chis.application.mobileApp.schemas.EHR_HealthRecordApp a,")) {
			tableNames = "EHR_HealthRecord a,";
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("pageNo", pageNo);
		result.put("pageSize", pageSize);
		String countSql = "select count(*) totalCount ";
		String sql = "";
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		try {
			List<Map<String, Object>> l = dao.doQuery(countSql, parameters);
			long totalCount = (Long) l.get(0).get("totalCount");
			result.put("totalCount", totalCount);
			parameters.put("first", (pageNo - 1) * pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			list = SchemaUtil
					.setDictionaryMessageForListFromSQL(list, schemaId);
			result.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}
	public void updatetoappflag(String recordid,String category,String toappflag){
		StringBuffer upappsql=new StringBuffer("update"); ;
		if (category.equals("201")) {
			upappsql.append(" MDC_HypertensionVisit ");
		}else if(category.equals("202")) {
			upappsql.append(" MDC_DiabetesVisit ");
		}else if(category.equals("203")) {
			upappsql.append(" MDC_OldPeopleVisit ");
		}else if(category.equals("102")) {
			upappsql.append(" MDC_DiabetesRecord ");
		}else if(category.equals("101")) {
			upappsql.append(" MDC_HypertensionRecord ");
		}else if(category.equals("100")) {
			upappsql.append(" EHR_HealthRecord ");
		}else{
			return;
		}
		upappsql.append(" set toapp=:toappflag where ");
		if (category.equals("201")||category.equals("202")||category.equals("203")) {
			upappsql.append(" visitId=:recordid ");
		}else if(category.equals("102")||category.equals("101")||category.equals("100")) {
			upappsql.append(" phrId=:recordid ");
		}else{
			return;
		}
		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("recordid", recordid);
		pars.put("toappflag", toappflag);
		try {
			dao.doUpdate(upappsql.toString(), pars);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//查询生活环境
	public Map<String, Object> queryShhj(String empiId, String schemaId, BaseDAO dao) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		//Map<String, Object> Dictionary = new HashMap<String, Object>();
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		Schema sc = null;
		//Schema Dictionary = null;
	//	DictionaryController d=DictionaryController.instance();
		//String livestockColumn="";
	//	try{
		//	Dictionary fs=d.get("chis.dictionary.livestockColumn");
		//	livestockColumn=fs.getItem("livestockColumn").getText();
	//	}catch(ControllerException e3){
	//		e3.printStackTrace();
		//}

		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<SchemaItem> items = sc.getItems();
		String sql = "";
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";
			}
		}
		sql = sql.substring(0, sql.length() - 1) + " ";
		String sqls = "select "
				//+ sql
				+" case COOKAIRTOOL  when '1' then'无' when '2' then'油烟机' when '3' then'换气扇' else'烟筒' end as COOKAIRTOOL,case FUELTYPE  when '1' then'液化气' when '2' then'煤' when '3' then'天然气'when '4' then'沼气' when '5' then'柴火'  else'其他' end as FUELTYPE,"
				+" case WATERSOURCECODE  when '1' then'自来水' when '2' then'经净化过滤的水' when '3' then'井水'when '4' then'河湖水' when '5' then'塘水'else'其他' end as WATERSOURCECODE,case livestockColumn  when '1' then'单设' when '2' then'室内' else'室外' end as WASHROOM,"
				+" case WASHROOM  when '1' then'卫生厕所' when '2' then'一格或二格粪池式' when '3' then'马桶'when '4' then'露天粪坑' else'简易棚厕' end as WATERSOURCECODE "
				+" ,case livestockColumn  when '1' then'单设' when '2' then'室内' when '3' then'室外' end as livestockColumn "
				+ " from EHR_FamilyMiddle a where a.empiId='"
				+ empiId+"'"
				;
		try {
			result = dao.doSqlLoad(sqls, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	//查询个人的既往史
		public Map<String, Object> QueryPastHistory(String empiId, String schemaId, BaseDAO dao) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> result = new HashMap<String, Object>();
			HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
			List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();		
			List<Map<String, Object>> listSub2 = new ArrayList<Map<String, Object>>();			
			List<Map<String, Object>> listSub3 = new ArrayList<Map<String, Object>>();			

			Schema sc = null;
			try {
				sc = SchemaController.instance().get(schemaId);
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<SchemaItem> items = sc.getItems();
			String sql = "";
			for (SchemaItem it : items) {
				if (it.isVirtual()) {
					continue;
				}
				String fid = it.getId();
				Permission p = it.lookupPremission();

				if (it.hasProperty("refAlias")) {
					fid = (String) it.getProperty("refItemId");
					String refAlias = (String) it.getProperty("refAlias");
					sql = sql + refAlias + "." + fid + " as " + fid + ",";
				} else {
					sql = sql + "a." + fid + " as " + fid + ",";

				}
			}
			sql = sql.substring(0, sql.length() - 1) + " ";
			String sqls = "select "
					+ sql
					+",case a.pasthistypecode  when '01' then'药物过敏史' when '02' then'疾病史' when '03' then'手术史' when '04' then'输血史'when '05' then'遗传病史' when '06' then'外伤史'when '07' then'家族疾病史-父亲'when '08'then'家族疾病史-母亲'when '09' then'家族疾病史-兄弟姐妹'when '10' then'家族疾病史-子女'when '11' then'残疾状况'when '12' then'暴露史' end as PASTHISTYPE"
					+ " from EHR_Pasthistory a join MPI_DemographicInfo b on a.empiId=b.empiId "
					+ " join EHR_HealthRecord c on a.empiId=c.empiId where  a.pasthistypecode in ('02','03','04','06') and a.empiId='"
					+ empiId+"'"
					;
			try {
				listSub=  dao.doSqlQuery(sqls, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sql2 = "select "
					+ sql
					+" ,DISEASETEXT,case a.pasthistypecode  when '01' then'药物过敏史' when '02' then'疾病史' when '03' then'手术史' when '04' then'输血史'when '05' then'遗传病史' when '06' then'外伤史'when '07' then'家族疾病史-父亲'when '08'then'家族疾病史-母亲'when '09' then'家族疾病史-兄弟姐妹'when '10' then'家族疾病史-子女'when '11' then'残疾状况'when '12' then'暴露史' end as PASTHISTYPE"
					+ " from EHR_Pasthistory a join MPI_DemographicInfo b on a.empiId=b.empiId "
					+ " join EHR_HealthRecord c on a.empiId=c.empiId where a.pasthistypecode in ('07','08','09','10') and a.empiId='"
					+ empiId+"'"
				//	+"order by "
					;
			try {
				listSub2 =  dao.doSqlQuery(sql2, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sql3 = "select "
					+ sql
					+" ,DISEASETEXT,case a.pasthistypecode  when '01' then'药物过敏史' when '02' then'疾病史' when '03' then'手术史' when '04' then'输血史'when '05' then'遗传病史' when '06' then'外伤史'when '07' then'家族疾病史-父亲'when '08'then'家族疾病史-母亲'when '09' then'家族疾病史-兄弟姐妹'when '10' then'家族疾病史-子女'when '11' then'残疾状况'when '12' then'暴露史' end as PASTHISTYPE"
					+ " from EHR_Pasthistory a join MPI_DemographicInfo b on a.empiId=b.empiId "
					+ " join EHR_HealthRecord c on a.empiId=c.empiId where a.pasthistypecode in ('05','11','01','12') and a.empiId='"
					+ empiId+"'"
					;
			try {
				listSub3 =  dao.doSqlQuery(sql3, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("PastHistory", listSub);
			result.put("PastHistory2", listSub2);
			result.put("PastHistory3", listSub3);
			return result;
		}
		//查询一般情况
		public Map<String, Object> queryHealthCheck(String HEALTHCHECK, String schemaId, BaseDAO dao) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> result = new HashMap<String, Object>();
			HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
			List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(schemaId);
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<SchemaItem> items = sc.getItems();
			String sql = "";
			for (SchemaItem it : items) {
				if (it.isVirtual()) {
					continue;
				}
				String fid = it.getId();
				Permission p = it.lookupPremission();

				if (it.hasProperty("refAlias")) {
					fid = (String) it.getProperty("refItemId");
					String refAlias = (String) it.getProperty("refAlias");
					sql = sql + refAlias + "." + fid + " as " + fid + ",";
				} else {
					sql = sql + "a." + fid + " as " + fid + ",";

				}
			}
			sql = sql.substring(0, sql.length() - 1) + " ";
			String sqls = "select "
					+ sql
					+",case a.healthStatus when '1' then '满意' when '2' then '基本满意'when '3' then '说不清楚'when '4' then '不太满意'when '5' then '不满意'end as healthStatus"
					+",case a.selfCare when '1' then '可自理' when '2' then '轻度依赖'when '3' then '中度依赖'when '4' then '不能自理' end as selfCare"
					+",case a.cognitive when '1' then '粗筛阴性' when '2' then '粗筛阳性' end as cognitive"
					+",case a.emotion when '1' then '粗筛阴性' when '2' then '粗筛阳性' end as emotion"
					+ " from HC_HealthCheck a join MPI_DemographicInfo b on a.empiId=b.empiId "
					+ " join EHR_HealthRecord c on a.empiId=c.empiId where a.HealthCheck='"
					+ HEALTHCHECK+"'"
					;
			try {
				listSub = dao.doSqlQuery(sqls, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("HealthCheck", listSub);
			return result;
		}
		//查询生活方式
		public Map<String, Object> queryLifeStyle(String HEALTHCHECK, String schemaId, BaseDAO dao) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> result = new HashMap<String, Object>();
			HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
			List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(schemaId);
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<SchemaItem> items = sc.getItems();
			String sql = "";
			for (SchemaItem it : items) {
				if (it.isVirtual()) {
					continue;
				}
				String fid = it.getId();
				Permission p = it.lookupPremission();

				if (it.hasProperty("refAlias")) {
					fid = (String) it.getProperty("refItemId");
					String refAlias = (String) it.getProperty("refAlias");
					sql = sql + refAlias + "." + fid + " as " + fid + ",";
				} else {
					sql = sql + "a." + fid + " as " + fid + ",";

				}
			}
			sql = sql.substring(0, sql.length() - 1) + " ";
			String sqls = "select "
					+ sql
					+",case physicalExerciseFrequency when '1' then'每天' when '2' then'每周一次以上'when '3' then'偶尔'when '4' then'不锻炼' end as physicalExerciseFrequency "
				+",case dietaryHabit when '1' then'荤素均衡' when '2' then'荤食为主'when '3' then'素食为主'when '4' then'嗜盐嗜盐' when '5' then'嗜油' when '5' then'嗜糖' end as dietaryHabit "
				+",case wehtherSmoke when '1' then'从不吸烟' when '2' then'已戒烟'when '3' then'吸烟'  end as dietaryHabit,case drinkingFrequency when '1' then'从不' when '2' then'偶尔'when '3' then'经常' when '4' then'每天' end as drinkingFrequency "
				+",case whetherDrink when '1' then'未戒酒' when '2' then'已戒酒' end as whetherDrink ,case isDrink when '1' then'是' when '2' then'否' end as isDrink"
				+",case mainDrinkingVvarieties when '1' then'白酒(≥42度)' when '2' then'白酒(＜42度)'when '3' then'啤酒'when '4' then'黄酒、米酒'when '5' then'红酒'when '9' then'其它' end as mainDrinkingVvarieties ,case occupational when '1' then'无' when '2' then'有' end as occupational"
				+ " from HC_LifestySituation a   where a.HEALTHCHECK='"
					+ HEALTHCHECK +"'"
					;
			try {
				listSub = dao.doSqlQuery(sqls, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("healthcheck", listSub);
			return result;
		}
		//查询辅助检查
		public Map<String, Object> queryAccessoryExamination(String HEALTHCHECK, String schemaId, BaseDAO dao) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			Map<String, Object> result = new HashMap<String, Object>();
			HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
			List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(schemaId);
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<SchemaItem> items = sc.getItems();
			String sql = "";
			for (SchemaItem it : items) {
				if (it.isVirtual()) {
					continue;
				}
				String fid = it.getId();
				Permission p = it.lookupPremission();

				if (it.hasProperty("refAlias")) {
					fid = (String) it.getProperty("refItemId");
					String refAlias = (String) it.getProperty("refAlias");
					sql = sql + refAlias + "." + fid + " as " + fid + ",";
				} else {
					sql = sql + "a." + fid + " as " + fid + ",";

				}
			}
			sql = sql.substring(0, sql.length() - 1) + " ";
			String sqls = "select "
					+ sql
					+",case lip when '1'then'红润'when '2'then'苍白'when '3'then'发绀'when '4'then'皲裂'when '5'then'疱疹' end as lipcase,case denture when '1'then'正常'when '2'then'缺齿'when '3'then'龋齿'when '4'then'义齿' end as denture"
				+",case pharyngeal when '1'then'无充血'when '2'then'充血'when '3'then'淋巴滤泡增生' end as pharyngeal,case hearing when '1'then'听见'when '2'then'听不清或无法听见' end as hearing "
					+ " ,case MOTION when '1' then'可顺利完成'when'2'then'无法独立完成其中任何一个动作' end as MOTION from HC_AccessoryExamination a   where a.HEALTHCHECK='"
					+ HEALTHCHECK +"'"
					;
			try {
				listSub = dao.doSqlQuery(sqls, parameters);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("healthcheck", listSub);
			return result;
		}
		//查询健康查体
				public Map<String, Object> queryExamination(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+",case fundus when '1' then'正常'when'2'then'异常'end as fundus,case skin when '1' then'正常'when'2'then'潮红'when'3'then'苍白'when'4'then'发绀'when'5'then'黄染'when'6'then'色素沉着'when'7'then'其他'end as skin "
						+",case sclera when '1' then'正常'when'2'then'黄染'when'3'then'充血'when'4'then'其他'end as sclera,case lymphnodes when '1' then'未触及'when'2'then'锁骨上'when'3'then'腋窝'when'4'then'其他'end as lymphnodes "
+",case barrelChest when '1'then'否'when'2'then'是' end as barrelChest,case breathSound when '1'then'正常'when'2'then'异常' end as breathSound,case rales when '1'then'无'when'2'then'干罗音' when '1'then'湿罗音'when '1'then'其他'end as rales"
+",case heartMurmur when '1'then'无'when'2'then'有' end as heartMurmur,case abdominAltend when '1'then'无'when'2'then'有' end as abdominAltend,case adbominAlmass when '1'then'无'when'2'then'有'end as adbominAlmass, case liverBig when '1'then'无'when'2'then'有' end as liverBig,case dullness when '1'then'无'when'2'then'有' end as dullness"							
+",case edema when '1'then'无'when'2'then'单侧' when'3'then'双侧不对称' when'4'then'双侧对称' end as edema,case footPulse when '1'then'未触及'when'2'then'触及双侧对称'when'3'then'触及左侧弱或消失'when'4'then'触及右侧弱或消失' end as footPulse,case dre when '1'then'未及异常'when'2'then'触痛'when'3'then'包块'when'4'then'前列腺异常'when'5'then'其他'end as dre, case breast when '1'then'未见异常'when'2'then'乳房切除' when'3'then'异常泌乳'when'4'then'乳腺包块'when'5'then'其他'end as breast"
+",case vulva when '1'then'未见异常'when'2'then'异常' end as vulva,case vaginal when '1'then'未见异常'when'2'then'异常'end as vaginal,case cervix when '1'then'未及异常'when'2'then'异常'end as cervix, case palace when '1'then'未见异常'when'2'then'异常'end as palace,case attachment when '1'then'未见异常'when'2'then'异常'end as attachment "
+",case rhythm when '1'then'齐'when'2'then'不齐' when'3'then'绝对不齐' end as rhythm,case splenomegaly when '1'then'无'when'2'then'有'end as splenomegaly"
+ " from HC_EXAMINATION a   where a.HEALTHCHECK='"
							+HEALTHCHECK+"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询辅助检查
				public Map<String, Object> queryHC_ACCESSORYEXAMINATION(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+",case ecg when '1'then'正常'when'2'then'异常' end as ecg,case fob when '1'then'阴性'when'2'then'阳性'end as fob,case hbsag when '1'then'阴性'when'2'then'阳性'end as hbsag, case b when '1'then'正常'when'2'then'异常'end as b,case ps when '1'then'正常'when'2'then'异常'end as ps "
							+ " ,a.ALT,AST,ALB,TBIL,DBIL,B  from HC_ACCESSORYEXAMINATION a   where a.HEALTHCHECK='"
							+HEALTHCHECK+"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询健康评价
				public Map<String, Object> queryHealthAssessment(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+ " from HC_HEALTHASSESSMENT a   where a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询健康评价
				public Map<String, Object> queryINHOSPITALSITUATION(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();	
					List<Map<String, Object>> listSub2 = new ArrayList<Map<String, Object>>();	
					List<Map<String, Object>> listSub3 = new ArrayList<Map<String, Object>>();	
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
					//	+ sql
							+"  a.healthCheck as healthCheck,a.type  as type, to_char(a.inhospitalDate,'yyyy-mm-dd')||'/'|| to_char(a.outhospitalDate,'yyyy-mm-dd') as inoutdate,a.inhospitalReason as reason, a.medicalestablishmentName as unitname,a.medicalrecordNumber  as bah "
							+ " from HC_INHOSPITALSITUATION a  " +
							"where  a.type='1'and a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sql2 = "select "
							//+ sql
							+"  a.healthCheck as healthCheck,a.type  as type, to_char(a.inhospitalDate,'yyyy-mm-dd')||'/'|| to_char(a.outhospitalDate,'yyyy-mm-dd') as inoutdate,a.inhospitalReason as reason, a.medicalestablishmentName as unitname,a.medicalrecordNumber  as bah "
							+ " from HC_INHOSPITALSITUATION a  " +
							"where a.type='2'and a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub2 = dao.doSqlQuery(sql2, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sql3 = "select "
							//+ sql
							+" b.medicine as medicine,b.use ,b.eachDose as EACHDOSE,b.useDate as USEDATE,case b.medicineYield when '1' then '规律'when'2'then'间断'when'3'then '不服药'end as MEDICINEYIELD "
							+ " from   HC_MEDICINESITUATION  b " +
							"where b.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub3 = dao.doSqlQuery(sql3, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("zys", listSub);
					result.put("jtbcs", listSub2);
					result.put("zyyyqk", listSub3);
					return result;
				}
				//查询非免疫规划预防接种
				public Map<String, Object> queryNonimmuneInoculation(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+ " from HC_NONIMMUNEINOCULATION a   where a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询健康体检表首页
				public Map<String, Object> queryHealCheckSY(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+",(select a.personname from sys_personnel a,(select manadoctorid from ehr_healthrecord where empiid=( select empiid from hc_healthcheck where HEALTHCHECK='"
							+ HEALTHCHECK 
							+"')) b where b.manadoctorid=a.personid) as manaDoctorId,case when d.ABNORMALITY1 is not null and d.ABNORMALITY2 is not null  and d.ABNORMALITY3 is not null and d.ABNORMALITY4 is not null then (d.ABNORMALITY1||','||d.ABNORMALITY2||','||d.ABNORMALITY3||','||d.ABNORMALITY4) when d.ABNORMALITY1 is not null and d.ABNORMALITY2 is  null   then (d.ABNORMALITY1)   when d.ABNORMALITY1 is not null and d.ABNORMALITY2 is not null and d.ABNORMALITY3 is null then (d.ABNORMALITY1||','||d.ABNORMALITY2)when d.ABNORMALITY1 is not null and d.ABNORMALITY2 is not null  and d.ABNORMALITY3 is not null and d.ABNORMALITY4 is  null then (d.ABNORMALITY1||','||d.ABNORMALITY2||','||d.ABNORMALITY3) else '' end as ABNORMALITY1"
							+",replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(a.symptom,'02','头痛'),'03','头晕'),'04','心悸'),'05','胸闷'),'06','胸痛'),'07','慢性咳嗽'),'08','咳痰'),'09','呼吸困难'),'10','多饮'),'01','无症状') ,'11','多尿'),'12','体重下降'),'13','乏力'),'14','关节肿痛'),'15','视力模糊'),'16','手脚麻木'),'17','尿急'),'18','尿痛'),'19','便秘'),'20','腹泻'),'21','恶心呕吐'),'22','眼花'),'23','耳鸣'),'24','乳房胀痛'),'25','其他')as symptom "
							+",replace(replace(replace(d.mana,'1','纳入慢性病患者健康管理'),'2','建议复查'),'3','建议转诊') as mana"
							+", replace(replace(replace(replace(replace(replace(replace(d.RISKFACTORSCONTROL,'1','戒烟'),'2','健康饮酒'),'3','饮食'),'4','锻炼'),'5','减体重'),'6','建议接种疫苗'),'7','其他') as RISKFACTORSCONTROL"
							+ " from hc_healthcheck a join mpi_demographicinfo b on a.empiid=b.empiid " 
//							+" join sys_personnel c on a.manadoctorid=c.personid"
							+" join HC_HEALTHASSESSMENT d on a.healthcheck=d.healthcheck"
							+" where a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询健康体检表列表
				public Map<String, Object> queryHealCheckLb(String EMPIID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+",b.organizname,replace(replace(replace(replace(replace(checkWay,'1','健康档案'),'2','老年人'),'3','高血压'),'4','糖尿病'),'5','精神病') as checkWay "
							+ " from hc_healthcheck a join sys_organization b on a.createunit=b.organizcode  " 
							+" where to_char(a.checkdate,'yyyy')>=2018 and a.empiId='"
							+ EMPIID +"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("empiId", listSub);
					return result;
				}
				//查询现存主要健康问题
				public Map<String, Object> queryXczywt(String HEALTHCHECK, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
					List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
						//+ sql
							+" case cerebrovascularDiseases when '1'then'未发现'when'2'then'缺血性卒中' when'3'then'脑出血' when'4'then'蛛网膜下腔出血' when'5'then'短暂性脑缺血发作' when'9'then'其他' end as cerebrovascularDiseases,case kidneyDiseases when '1'then'未发现'when'2'then'糖尿病肾病'when'3'then'肾衰竭'when'4'then'急性肾炎'when'5'then'慢性肾炎'when'9'then'其他'end as kidneyDiseases,case heartDisease when '1'then'未发现'when'2'then'心肌梗塞'when'3'then'心绞痛'when'4'then'冠状动脉血运重建'when'5'then'充血性心力衰竭'when'6'then'心前区疼痛'when'9'then'其他'end as heartDisease, case VascularDisease when '1'then'未发现'when'2'then'夹层动脉瘤'when'3'then'动脉闭塞性疾病'when'9'then'其他'end as VascularDisease,case eyeDiseases when '1'then'未发现'when'2'then'视网膜出血或渗出'when'3'then'视乳头水肿'when'4'then'白内障'when'9'then'其他'end as eyeDiseases"
							+",case neurologicalDiseases when  '1' then '未发现'when  '2' then '有' end as neurologicalDiseases,case otherDiseasesone when  '1' then '未发现'when  '2' then '有' end as otherDiseasesone "
							+ " from HC_HealthCheck a   where a.HEALTHCHECK='"
							+ HEALTHCHECK +"'"
							;
					try {
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("healthcheck", listSub);
					return result;
				}
				//查询高血压随访
				public Map<String, Object> queryMDC_HypertensionVisit(String VISITID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select distinct "
							+ sql
							+" ,c.personname as visitDoctor,case a.visitWay  when '1' then'门诊就诊' when '2' then'站点就诊' when '3' then'社区随访' when '4' then'上门随访'when '5' then'电话随访' when '6' then'群组随访'else'其他' end as visitWay"
							+" ,replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(a.currentSymptoms,2,'头痛头晕'),3,'恶心呕吐'),4,'眼花耳鸣'),5,'呼吸困难'),6,'心悸胸闷'),7,'鼻衄出血不止'),8,'四肢发麻'),9,'下肢水肿'),10,'其他'),1,'无症状') as currentSymptoms "
							+" ,case a.salt  when 1 then'轻' when 2 then'中' when 3 then'重'end as salt,case a.psychologyChange  when '1' then'良好' when '2' then'一般' when '3' then'差'end as psychologyChange,case a.obeyDoctor  when '1' then'良好' when '2' then'一般' when '3' then'差'end as obeyDoctor"							
							+" ,case a.medicine  when '1' then'规律' when '2' then'间断' when '3' then'不服药' when '4' then'拒绝服药'end as medicine,case a.medicineBadEffect  when 'y' then'有' when 'n' then'无' end as medicineBadEffect,case a.visitEvaluate  when '1' then'控制满意' when '2' then'控制不满意' when '3' then'不良反应' when '4' then'并发症'end as visitEvaluate "							
							+ " from MDC_HYPERTENSIONVISIT a join MDC_HYPERTENSIONMEDICINE b on a.visitid=b.visitid  join sys_personnel c on a.visitdoctor=c.personid" 
							+" where a.visitId='"
							+ VISITID +"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						//listSub = dao.doSqlQuery(sqls, parameters);
						result = dao.doSqlLoad(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						e.printStackTrace();
					}
					String sql2 = "select " 
							+"b.medicinename,'每日'||MEDICINEFREQUENCY||'次每次'||b.medicinedosage||b.medicineunit as MEDICINEFREQUENCY"
							+" from MDC_HYPERTENSIONVISIT a join MDC_HYPERTENSIONMEDICINE b on a.visitid=b.visitid where a.visitId='"
							+VISITID+"'";
					try {
						listSub = dao.doSqlQuery(sql2, parameters);
					} catch (PersistentDataOperationException e) {
						e.printStackTrace();
					}
					result.put("MEDICINEWAY", listSub);
					return result;
				}
				//查询糖尿病随访
				public Map<String, Object> queryMDC_DiabetesVisit(String VISITID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select  distinct  "
							+ sql
							+" ,c.personname as visitDoctor,replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(a.symptoms,2,'多饮'),3,'多食'),4,'多尿'),5,'视力模糊'),6,'感染'),7,'手脚麻木'),8,'下肢浮肿'),9,'体重明显下降'),99,'其他症状'),1,'无症状') as currentSymptoms "
				            +",case a.visitWay  when '1' then'门诊就诊' when '2' then'站点就诊' when '3' then'社区随访' when '4' then'上门随访'when '5' then'电话随访' when '6' then'群组随访'else'其他' end as visitWay,case a.psychologyChange  when '1' then'良好' when '2' then'一般' when '3' then'差'end as psychologyChange,case a.obeyDoctor  when '1' then'良好' when '2' then'一般' when '3' then'差'end as obeyDoctor,case a.medicine  when '1' then'规律' when '2' then'间断' when '3' then'不服药' when '4' then'拒绝服药'end as medicine,case a.adverseReactions  when 'y' then'有' when 'n' then'无' end as adverseReactions"
						    +",case a.adverseReactions  when '1' then'无' when '2' then'偶尔' when '3' then'频繁' end as adverseReactions,case a.visitType  when '1' then'控制满意' when '2' then'控制不满意' when '3' then'不良反应' when '4' then'并发症'end as visitType "
				            + " from mdc_diabetesvisit a left join mdc_diabetesmedicine b on a.visitid=b.visitid join sys_personnel c on a.visitdoctor=c.personid" 
							+" where a.visitId='"
							+ VISITID +"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						result = dao.doSqlLoad(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						e.printStackTrace();
					}
					String sql2 = "select " 
							+"b.medicinename,'每日'||MEDICINEFREQUENCY||'次每次'||b.medicinedosage||b.medicineunit as MEDICINEFREQUENCY"
							+" from mdc_diabetesvisit a join mdc_diabetesmedicine b on a.visitid=b.visitid where a.visitId='"
							+VISITID+"'";
					try {
						listSub = dao.doSqlQuery(sql2, parameters);
					} catch (PersistentDataOperationException e) {
						e.printStackTrace();
					}
					result.put("MEDICINEWAY", listSub);
					return result;
				}
				//查询高血压
				public Map<String, Object> queryMDC_Hypertension(String EMPIID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select lx, sflx,visitDate,visitId from(select "
							+ sql
							+",'高血压' as lx,case a.visitway when '1'then'门诊就诊'when '2'then'站点就诊'when '3'then'社区随访' when '4'then'上门随访'when '5'then'电话随访'when '6'then'群组随访'else'其他'end as sflx"
							+ " from mdc_hypertensionvisit a" 
							+" where a.empiId='"
							+ EMPIID +"'"
							+" order by a.visitdate desc) where rownum<=4 "
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("empiId", listSub);
					return result;
				}
				//查询糖尿病
				public Map<String, Object> queryMDC_Diabetes(String EMPIID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select lx, sflx,visitDate,visitId from(select "
							+ sql
							+",'糖尿病' as lx,case a.visitway when '1'then'门诊就诊'when '2'then'站点就诊'when '3'then'社区随访' when '4'then'上门随访'when '5'then'电话随访'when '6'then'群组随访'else'其他'end as sflx"
							+ " from mdc_diabetesvisit a" 
							+" where a.empiId='"
							+ EMPIID +"'"
							+"  order by a.visitdate desc) where rownum<=4 "
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("empiId", listSub);
					return result;
				}
				//根据idcard查empiid-zhj
				public Map<String, Object> queryEmpiIdByIdcard(String IDCARD, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+ "from  mpi_demographicinfo a join ehr_healthrecord b on a.empiid=b.empiid" 
							+" where b.ISSECONDVERIFY=2 and a.idcard='"
							+IDCARD+"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(listSub.isEmpty()){
						return null;
					}else{
					result.put("idcard", listSub);
				}
					return result;
				}	
				//根据idcard查empiid-zhj
				public Map<String, Object> queryJbxx(String EMPIID, String schemaId, BaseDAO dao) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					Map<String, Object> result = new HashMap<String, Object>();
					HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
				    List<Map<String, Object>> listSub = new ArrayList<Map<String, Object>>();			
					Schema sc = null;
					try {
						sc = SchemaController.instance().get(schemaId);
					} catch (ControllerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<SchemaItem> items = sc.getItems();
					String sql = "";
					for (SchemaItem it : items) {
						if (it.isVirtual()) {
							continue;
						}
						String fid = it.getId();
						Permission p = it.lookupPremission();

						if (it.hasProperty("refAlias")) {
							fid = (String) it.getProperty("refItemId");
							String refAlias = (String) it.getProperty("refAlias");
							sql = sql + refAlias + "." + fid + " as " + fid + ",";
						} else {
							sql = sql + "a." + fid + " as " + fid + ",";

						}
					}
					sql = sql.substring(0, sql.length() - 1) + " ";
					String sqls = "select "
							+ sql
							+ "，case sexCode when '1' then '男' else'女' end as sexCode,case registeredPermanent when '1' then '户籍' else'非户籍' end as registeredPermanent ,b.text as nationCode" 
							+ "，case bloodTypeCode when '1' then 'A型' when '2' then 'B型'when '3' then 'O型'when '4' then 'AB型'else'不详' end as bloodTypeCode,case educationCode when '1' then '文盲或半文盲籍'  when '2' then '小学'  when '3' then '初中' when '4' then '高中/技校/中专'  when '5' then '大学专科及以上' else'不详' end as educationCode  " 
							+ "，case workCode when '0' then '国家机关、党群组织、企业、事业单位负责人' when '1/2' then '专业技术人员'when '3' then '办事人员和有关人员'when '4' then '商业、服务业人员' when '5' then '农、林、牧、渔、水利业生产人员' when '9-9' then '生产、运输设备操作人员及有关人员'when 'X' then '军人'else'不便分类的其他从业人员' end as workCode,case maritalStatusCode when '1' then '未婚'  when '2' then '已婚'  when '3' then '丧偶'  when '4' then '离婚'   else'未说明的婚姻状况' end as maritalStatusCode  " 
							+ "，case insuranceCode when '01' then '城镇职工基本医疗保险' when '02' then '城镇居民基本医疗保险'when '03' then '新型农村合作医疗'when '04' then '贫困救助' when '05' then '商业医疗保险' when '06' then '全公费'when '07' then '全自费'else'其他' end as insuranceCode " 
							+ "from  mpi_demographicinfo a ,ethnic b" 
							+" where a.nationCode=b.key and a.empiId='"
							+ EMPIID +"'"
							;
					try {
						//result = dao.doSqlLoad(sqls, parameters);
						listSub = dao.doSqlQuery(sqls, parameters);
					} catch (PersistentDataOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					result.put("empiId", listSub);
					return result;
				}	
}
