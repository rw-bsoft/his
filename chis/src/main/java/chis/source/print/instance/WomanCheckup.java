/**
 * @(#)PsychosisAnnualAssessmentModel.java Created on 2012-4-5 上午9:42:17
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.print.base.BSCHISPrint;

import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">Ҧʿǿ</a>
 */
public class WomanCheckup extends BSCHISPrint implements IHandler {

	private String empiId;
	private String checkupId;

	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		String jrxml = (String) request.get("jrxml");
		try {
//			if (jrxml.startsWith("womanCheckup")) {
//				response.putAll(getWomanInfoMap(request, response, ctx));
//			} else
			if (jrxml.startsWith("chis.prints.template.deathReport")) {
				response.putAll(getDeathReportMap(request, response, ctx));
			} 
//			else if (jrxml.startsWith("unitMonitor")) {
//				response.putAll(getUnitMonitorMap(request, response, ctx));
//			} else if (jrxml.startsWith("defectRegister")) {
//				response.putAll(getDefectRegisterMap(request, response, ctx));
//			}else if (jrxml.startsWith("womanBaseRecord")) {
//				response.putAll(getWomanBaseRecordMap(request, response, ctx));
//			}
			sqlDate2String(response);
			change2String(response);
		} catch (Exception e) {
			throw new PrintException(Constants.CODE_PERSISTENT_ERROR,
					e.getMessage());
		}

	}
//
//	@SuppressWarnings("unchecked")
//	private Map<String, Object> getWomanBaseRecordMap(
//			Map<String, Object> request, Map<String, Object> response,
//			Context ctx) throws PrintException {
//		Map<String, Object> mpiMap = getFirstRecord(getBaseInfo(
//				MPI_DemographicInfo, request, ctx));
//		response.putAll(mpiMap);
//		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
//		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("queryCndsType", "a");
//		jsonReq.put("schema", "MPI_Certificate");
//		Map<String, Object> res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) res
//				.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> map = list.get(0);
//			response.putAll(map);
//		}
//		String womanRecordId=(String) request.get("womanRecordId");
//		exp = "['eq',['$','a.womanRecordId'],['s','" + womanRecordId + "']]";
//		jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("schema", "MHC_WomanBaseRecord");
//		res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		list = (List<Map<String, Object>>) res
//				.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> map = list.get(0);
//			changeHaveOrNot(map, "MHC_WomanBaseRecord");
//			changeYesOrNo(map, "MHC_WomanBaseRecord");
//			response.putAll(map);
//		}
//		return response;
//	}
//
//	@SuppressWarnings("unchecked")
//	private Map<String, Object> getDefectRegisterMap(
//			Map<String, Object> request, Map<String, Object> response,
//			Context ctx) throws PrintException {
//		String childEmpiId = "";
//		String motherEmpiId = "";
//		String motherIdCard="";
//		String schema="";
//		String exp = "";
//		exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
//		schema="CDH_DefectRegister";
//		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("queryCndsType", "a");
//		jsonReq.put("schema", schema);
//		Map<String, Object> res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) res
//				.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> map = list.get(0);
//			childEmpiId = (String) map.get("childEmpiId");
//			motherEmpiId = (String) map.get("motherEmpiId");
//			String literacy = (String) map.get("literacy");
//			motherIdCard=(String) map.get("motherCardNo");
//			if (literacy != null && !literacy.equals("")) {
//				literacy = literacy.substring(0, 1);
//				if (literacy.equals("1") || literacy.equals("2")
//						|| literacy.equals("3")) {
//					map.put("literacy", "5");
//				} else if (literacy.equals("4") || literacy.equals("6")) {
//					map.put("literacy", "4");
//				} else if (literacy.equals("7")) {
//					map.put("literacy", "3");
//				} else if (literacy.equals("8")) {
//					map.put("literacy", "2");
//				} else if (literacy.equals("9")) {
//					map.put("literacy", "1");
//				}
//			}
//			changeYesOrNo(map, "CDH_DefectRegister");
//			Map<String, Object> m = changeCombobox(map, "CDH_DefectRegister");
//			response.putAll(m);
//		}
//		Map<String, Object> mpiMap = null;
//		if (childEmpiId != null && !childEmpiId.equals("")) {
//			request.put("empiId", childEmpiId);
//			mpiMap = getFirstRecord(getBaseInfo(MPI_DemographicInfo, request,
//					ctx));
//			if (mpiMap != null) {
//				if (mpiMap.get("sexCode") != null
//						&& (mpiMap.get("sexCode").equals("0") || mpiMap.get(
//								"sexCode").equals("9"))) {
//					mpiMap.put("sexCode", "3");
//				}
//				response.put("sexCode", mpiMap.get("sexCode"));
//				response.put("birthday", mpiMap.get("birthday"));
//			}
//		}
//		if (motherEmpiId != null && !motherEmpiId.equals("")) {
//			request.put("empiId", motherEmpiId);
//			mpiMap = getFirstRecord(getBaseInfo(MPI_DemographicInfo, request,
//					ctx));
//			if (mpiMap != null) {
//				Date birthday = (Date) mpiMap.get("birthday");
//				int age = BSCHISUtil.calculateAge(birthday, new Date());
//				if(response.get("motherAge")==null||"".equals(response.get("motherAge"))){
//					response.put("motherAge", age);
//				}
//				if(response.get("motherName")==null||"".equals(response.get("motherName"))){
//				response.put("motherName", mpiMap.get("personName"));
//				}
//				if(response.get("motherNationCode_text")==null||"".equals(response.get("motherNationCode_text"))){
//				response.put("motherNationCode_text", mpiMap.get("nationCode_text"));
//				}
//				if(response.get("postCode")==null||"".equals(response.get("postCode"))){
//				response.put("postCode", mpiMap.get("zipCode"));
//				}
//			}
//		}
//		if(motherEmpiId==null&&motherIdCard!=null){
//			exp = "['eq',['$','a.idCard'],['s','" + motherIdCard + "']]";
//			jsonReq.put("cnd", toListCnd(exp));
//			jsonReq.put("schema", "MPI_DemographicInfo");
//			res = new HashMap<String, Object>();
//			new SimpleQuery().execute(jsonReq, res, ctx);
//			list = (List<Map<String, Object>>) res
//					.get("body");
//			if(list!=null&&list.size()>0){
//				mpiMap=list.get(0);
//			}
//			if (mpiMap != null) {
//				Date birthday = (Date) mpiMap.get("birthday");
//				int age = BSCHISUtil.calculateAge(birthday, new Date());
//				if(response.get("motherAge")==null||"".equals(response.get("motherAge"))){
//					response.put("motherAge", age);
//				}
//				if(response.get("motherName")==null||"".equals(response.get("motherName"))){
//				response.put("motherName", mpiMap.get("personName"));
//				}
//				if(response.get("motherNationCode_text")==null||"".equals(response.get("motherNationCode_text"))){
//				response.put("motherNationCode_text", mpiMap.get("nationCode_text"));
//				}
//				if(response.get("postCode")==null||"".equals(response.get("postCode"))){
//				response.put("postCode", mpiMap.get("zipCode"));
//				}
//			}
//		}
//		return response;
//	}
//
//	@SuppressWarnings("unchecked")
//	private Map<String, Object> getUnitMonitorMap(Map<String, Object> request,
//			Map<String, Object> response, Context ctx) throws PrintException, ServiceException {
//		String id = (String) request.get("id");
//		String exp = "['eq',['$','a.id'],['s','" + id + "']]";
//		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("queryCndsType", "a");
//		jsonReq.put("schema", "CMM_UnitMonitor");
//		Map<String, Object> res = new HashMap<String, Object>();
////		new SimpleQuery().execute(jsonReq, res, ctx);
////		getDao(ctx);
////		dao.doQuery(cnd, orderBy, entryName)
//		List<Map<String, Object>> list = (List<Map<String, Object>>) res
//				.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> map = list.get(0);
//			changeToYesOrNo(map, "whetherCanFollowingCheck");
//			changeToYesOrNo(map, "whetherFirstaid");
//			changeToYesOrNo(map, "emergencyEquipment");
//			changeToYesOrNo(map, "useEmergencyEquipment");
//			changeToYesOrNo(map, "clinicalOrganDysfunction");
//			changeToYesOrNo(map, "laboratoryIndexing");
//			changeToYesOrNo(map, "severeTherapeuticIndex");
//			response.putAll(map);
//		}
//		return response;
//	}
//
//	private void changeToYesOrNo(Map<String, Object> map, String id) {
//		if (map == null) {
//			return;
//		}
//		Map<String, Object> m = new HashMap<String, Object>();
//		List<String> list = new ArrayList<String>();
//		String v = String.valueOf(map.get(id));
//		if (v != null && !v.equals("")) {
//			Schema sc = SchemaController.instance()
//					.getSchema("CMM_UnitMonitor");
//			List<SchemaItem> scItems = sc.getItems();
//			for (SchemaItem schemaItem : scItems) {
//				if (schemaItem.getId().equals(id)) {
//					Dictionary dic = schemaItem.getRefDic();
//					Map<String, DictionaryItem> items = dic.getItems();
//					Iterator<String> it = items.keySet().iterator();
//					while (it.hasNext()) {
//						list.add(it.next());
//					}
//				}
//			}
//			String[] vs = v.split(",");
//			for (int i = 0; i < vs.length; i++) {
//				String value = vs[i];
//				m.put(id + value, "1");
//				if (list.contains(value)) {
//					list.remove(value);
//				}
//			}
//			for (String str : list) {
//				m.put(id + str, "2");
//			}
//		}
//		map.putAll(m);
//	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getDeathReportMap(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException, ServiceException {
		Map<String, Object> mpiMap = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, request, ctx));
		response.putAll(mpiMap);
		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
		jsonReq.put("cnd", toListCnd(exp));
		jsonReq.put("queryCndsType", "a");
		jsonReq.put("schema", BSCHISEntryNames.DEA_DeathReportCard);
		Map<String, Object> res = new HashMap<String, Object>();
		new SimpleQuery().execute(jsonReq, res, ctx);
		List<Map<String, Object>> list = (List<Map<String, Object>>) res
				.get("body");
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			changeDeathReportCard(map);
			response.putAll(map);
		}
		return response;
	}

	private void changeDeathReportCard(Map<String, Object> map) {
		if (map == null) {
			return;
		}
		if (map.get("nationCode") != null) {
			if (map.get("nationCode").equals("01")) {
				map.put("nationCode", "1");
			} else {
				map.put("nationCode", "2");
			}
		}
		if(map.get("educationCode")!=null){
			String code=(String) map.get("educationCode");
			code=code.substring(0, 1);
			 if(code.equals("2")||code.equals("3")||code.equals("1")){
				map.put("educationCode", "1");
			}else if(code.equals("4")||code.equals("6")){
				map.put("educationCode", "2");
			}else if(code.equals("7")){
				map.put("educationCode", "3");
			}else if(code.equals("8")){
				map.put("educationCode", "4");
			}else if(code.equals("9")){
				map.put("educationCode", "5");
			}
		}
		if (map.get("lastMenstrualPeriod") != null) {
			changeTimeToSingle(map, (Date) map.get("lastMenstrualPeriod"),
					"yyyy-MM-dd", "lastMenstrualPeriod");
		}
		if (map.get("deliveryDate") != null) {
			changeTimeToSingle(map, (Date) map.get("deliveryDate"),
					"yyyy-MM-dd HH:mm", "deliveryDate");
		}
		if (map.get("deadTime") != null) {
			changeTimeToSingle(map, (Date) map.get("deadTime"),
					"yyyy-MM-dd HH:mm", "deadTime");
		}
		if (map.get("provinceAppraise") != null) {
			if (map.get("provinceAppraise").equals("y")) {
				map.put("provinceAppraise", "1");
			} else if (map.get("provinceAppraise").equals("n")) {
				map.put("provinceAppraise", "2");
			}
		}
		if (map.get("stateAppraise") != null) {
			if (map.get("stateAppraise").equals("y")) {
				map.put("stateAppraise", "1");
			} else if (map.get("stateAppraise").equals("n")) {
				map.put("stateAppraise", "2");
			}
		}
		if (map.get("newWayAccouche") != null) {
			if (map.get("newWayAccouche").equals("y")) {
				map.put("newWayAccouche", "1");
			} else if (map.get("newWayAccouche").equals("n")) {
				map.put("newWayAccouche", "2");
			}
		}
		if (map.get("prenatalScreen") != null) {
			if (map.get("prenatalScreen").equals("y")) {
				map.put("prenatalScreen", "1");
			} else if (map.get("prenatalScreen").equals("n")) {
				map.put("prenatalScreen", "2");
			}
		}
	}

	private void changeTimeToSingle(Map<String, Object> map, Date date,
			String format, String fieldName) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String time = sdf.format(date);
		int k = 1;
		if (time.length() <= 10) {
			for (int i = 0; i < time.length(); i++) {
				if (i == 4 || i == 7) {
					continue;
				}
				map.put(fieldName + k, time.charAt(i));
				k++;
			}
		}
		if (time.length() > 10) {
			for (int i = 0; i < time.length(); i++) {
				if (i == 4 || i == 7 || i == 10 || i == 13 || i == 16) {
					continue;
				}
				map.put(fieldName + k, time.charAt(i));
				k++;
			}
		}

	}

//	@SuppressWarnings("unchecked")
//	private Map<String, Object> getWomanInfoMap(Map<String, Object> request,
//			Map<String, Object> response, Context ctx) throws PrintException {
//		checkupId = (String) request.get("checkupId");
//		Map<String, Object> mpiMap = getFirstRecord(getBaseInfo(
//				MPI_DemographicInfo, request, ctx));
//		response.putAll(mpiMap);
//		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
//		HashMap<String, Object> jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("queryCndsType", "a");
//		jsonReq.put("schema", "MPI_Certificate");
//		Map<String, Object> res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		List<Map<String, Object>> list = (List<Map<String, Object>>) res
//				.get("body");
//		if(list!=null&&list.size()>0){
//			response.putAll(list.get(0));
//		}
//		if (checkupId == null) {
//			return response;
//		}
//		exp = "['eq',['$','a.checkupId'],['s','" + checkupId + "']]";
//		jsonReq = new HashMap<String, Object>();
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("schema", "MHC_WomanCheckupBaseInfo");
//		res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		list = (List<Map<String, Object>>) res
//				.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> m = list.get(0);
//			changeHaveOrNot(m, "MHC_WomanCheckupBaseInfo");
//			changeYesOrNo(m, "MHC_WomanCheckupBaseInfo");
//			response.putAll(m);
//		}
//		exp = "['eq',['$','checkupId'],['s','" + checkupId + "']]";
//		jsonReq.put("cnd", toListCnd(exp));
//		jsonReq.put("schema", "MHC_WomanCheckupInfo");
//		res = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		list = (List<Map<String, Object>>) res.get("body");
//		if (list != null && list.size() > 0) {
//			Map<String, Object> m = list.get(0);
//			changeHaveOrNot(m, "MHC_WomanCheckupInfo");
//			response.putAll(m);
//		}
//		jsonReq.put("schema", "MHC_WomanCheckupMedicine");
//		res = new HashMap<String, Object>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		new SimpleQuery().execute(jsonReq, res, ctx);
//		list = (List<Map<String, Object>>) res.get("body");
//		if (list != null && list.size() > 0) {
//			int len = 4;
//			if (list.size() <= len) {
//				len = list.size();
//			}
//			for (int i = 0; i < len; i++) {
//				int j = i + 1;
//				Map<String, Object> m = list.get(i);
//				map.put("medicineName" + j, m.get("medicineName"));
//				map.put("medicineWay" + j, m.get("medicineWay"));
//			}
//			response.putAll(map);
//		}
//		return response;
//	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		// TODO Auto-generated method stub

	}
}
