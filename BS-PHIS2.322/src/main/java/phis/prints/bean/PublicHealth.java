/**
 * @(#)RabiesRecordModel.java Created on 2012-4-19 下午02:27:48
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.prints.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.utils.CNDHelper;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author
 */
public class PublicHealth implements IHandler {
	private String empiId;
	private Context ctx;
	private Map<String, Object> cycleNum = new HashMap<String, Object>();

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		BaseDAO dao = new BaseDAO(ctx);
		empiId = (String) request.get("empiId");
		this.ctx = ctx;
		String jrxml = (String) request.get("jrxml");
		try {
			addCycleNum();
			if (jrxml.contains("infectiousDisease")) {
				response.putAll(this.getInfectiousDiseaseMap(request, ctx));
			} else if (jrxml.contains("burstPublicHealth")) {
				response.putAll(this.getBurstPublicHealthMap(request, ctx));
			} else if (jrxml.contains("sanitationEvent")) {
				response.putAll(this.getSanitationEventMap(request, ctx));
			}
		} catch (Exception e) {
			throw new PrintException(445,
					e.getMessage());
		}
		sqlDate2String(response);
		change2String(response);
	}

	private Map<String, Object> getSanitationEventMap(
			Map<String, Object> request, Context ctx2) throws PrintException,
			ServiceException {
		BaseDAO dao = new BaseDAO(ctx2);
		Map<String, Object> param = new HashMap<String, Object>();
		String RecordID = (String) request.get("RecordID");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "RecordID", "s", RecordID);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, null,
					BSPHISEntryNames.PHE_EnvironmentalEvent);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			changeFieldToTrue(map, "air");
			changeFieldToTrue(map, "water");
			changeFieldToTrue(map, "biologicReason");
			changeFieldToTrue(map, "indoorPollution");
			changeFieldToTrue(map, "industrialPollution");
			changeFieldToCycle(map, "eventType", 3);
			changeFieldToCycle(map, "pathogenicFactor", 4);
			changeFieldToCycle(map, "eventReason", 7);
			changeFieldToCycle(map, "triggerTarget", 8);
			changeFieldToCycle(map, "contaminatedEnviroment", 9);
			changeFieldToCycle(map, "treatmentProcess", 7);
			changeFieldToCycle(map, "controlMeasures", 10);
			if (map.get("reportDate") != null) {
				Date date = (Date) map.get("reportDate");
				String d = changeDateByFormat(date, null);
				map.put("reportDate", d);
			}
			param.putAll(map);
		}
		return param;
	}

	private Map<String, Object> getBurstPublicHealthMap(
			Map<String, Object> request, Context ctx2) throws PrintException,
			ServiceException {
		BaseDAO dao = new BaseDAO(ctx2);
		Map<String, Object> param = new HashMap<String, Object>();
		String RecordID = (String) request.get("RecordID");
//		String exp = "['eq',['$','a.RecordID'],['s','" + RecordID + "']]";
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "RecordID", "s", RecordID);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, null,
					BSPHISEntryNames.PHE_RelevantInformation);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			changeFieldToTrue(map, "reportCatagory");
			changeFieldToTrue(map, "medicalOrganizationsType");
			changeFieldToTrue(map, "medicalOrganizationsDep");
			changeFieldToTrue(map, "schoolType");
			changeFieldToCycle(map, "messageType", 10);
			changeFieldToCycle(map, "eventLevel", 6);
			changeFieldToCycle(map, "eventAdreess", 17);
			changeFieldToCycle(map, "eventSource", 12);
			changeFieldToCycle(map, "cardinalSymptom", 6);
			if (map.get("reportDate") != null) {
				Date date = (Date) map.get("reportDate");
				String d = changeDateByFormat(date, null);
				map.put("reportDate", d);
			}
			if (map.get("primaryDiagnosisTime") != null) {
				Date date = (Date) map.get("primaryDiagnosisTime");
				String d = changeDateByFormat(date, null);
				map.put("primaryDiagnosisTime", d);
			}
			if (map.get("correctionDiagnosisTime") != null) {
				Date date = (Date) map.get("correctionDiagnosisTime");
				String d = changeDateByFormat(date, null);
				map.put("correctionDiagnosisTime", d);
			}
			if (map.get("confirmLevelTime") != null) {
				Date date = (Date) map.get("confirmLevelTime");
				String d = changeDateByFormat(date, null);
				map.put("confirmLevelTime", d);
			}
			if (map.get("correctionLevelTime") != null) {
				Date date = (Date) map.get("correctionLevelTime");
				String d = changeDateByFormat(date, null);
				map.put("correctionLevelTime", d);
			}
			if (map.get("eventTime") != null) {
				Date date = (Date) map.get("eventTime");
				String d = changeDateByFormat(date, "yyyy年MM月dd日HH时mm分");
				map.put("eventTime", d);
			}
			if (map.get("comeToTime") != null) {
				Date date = (Date) map.get("comeToTime");
				String d = changeDateByFormat(date, "yyyy年MM月dd日HH时mm分");
				map.put("comeToTime", d);
			}
			if (map.get("firstInvasionTime") != null) {
				Date date = (Date) map.get("firstInvasionTime");
				String d = changeDateByFormat(date, "yyyy年MM月dd日HH时mm分");
				map.put("firstInvasionTime", d);
			}
			if (map.get("lastInvasionTime") != null) {
				Date date = (Date) map.get("lastInvasionTime");
				String d = changeDateByFormat(date, "yyyy年MM月dd日HH时mm分");
				map.put("lastInvasionTime", d);
			}
			param.putAll(map);
		}
		return param;
	}

	private void changeFieldToTrue(Map<String, Object> map, String fieldName) {
		if (map.get(fieldName) != null) {
			Object value = map.get(fieldName);
			String v = String.valueOf(value);
			if (v != null && !v.equals("")) {
				String[] vs = v.split(",");
				for (int i = 0; i < vs.length; i++) {
					String val = vs[i];
					map.put(fieldName + val, "√");
				}
			}
		}
	}

	private String changeDateByFormat(Date date, String format) {
		if (format == null) {
			format = "yyyy年MM月dd日";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	private void changeFieldToCycle(Map<String, Object> map, String fieldName,
			int dicSum) {
		for (int i = 0; i < dicSum; i++) {
			int j = i + 1;
			map.put(fieldName + j, String.valueOf(j));
		}
		if (map.get(fieldName) != null) {
			Object value = map.get(fieldName);
			String v = String.valueOf(value);
			if (v != null && !v.equals("")) {
				String[] vs = v.split(",");
				for (int i = 0; i < vs.length; i++) {
					String val = vs[i];
					if (Integer.parseInt(val) < 10 && val.length() == 2) {
						val = val.substring(1);
					}
					map.put(fieldName + val, cycleNum.get(val));
				}
			}
		}
	}

	private void addCycleNum() throws ServiceException {
		try {
			Dictionary cycleDoc = DictionaryController.instance().get(
					"chis.dictionary.cycle");
			List<DictionaryItem> list = cycleDoc.itemsList();
			for (DictionaryItem di : list) {
				String key = di.getKey();
				String text = di.getText();
				cycleNum.put(key, text);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private Map<String, Object> getInfectiousDiseaseMap(
			Map<String, Object> request, Context ctx2) throws PrintException,
			ServiceException {
		BaseDAO dao = new BaseDAO(ctx2);
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> mpilist = new ArrayList<Map<String,Object>>();
		try {
			mpilist = dao.doList(cnd, "a.createTime desc", BSPHISEntryNames.MPI_DemographicInfo);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mpilist.size()>0){
			parameters = mpilist.get(0);
		}
		if (parameters.get("sexCode") != null) {
			changeFieldToTrue(parameters, "sexCode");
		}
		if (parameters.get("birthday") != null) {
			Date date = (Date) parameters.get("birthday");
			String d = changeDateByFormat(date, null);
			parameters.put("birthday", d);
		}
		if (parameters.get("dateAccident") != null) {
			Date date = (Date) parameters.get("dateAccident");
			String d = changeDateByFormat(date, null);
			parameters.put("dateAccident", d);
	}
//		if (parameters.get("diagnosedDate") != null) {
//			Date date = (Date) parameters.get("diagnosedDate");
//			String d = changeDateByFormat(date, null);
//		parameters.put("diagnosedDate", d);
//		}
		if (parameters.get("diagnosedDate") != null) {
			Date date = (Date) parameters.get("diagnosedDate");
			String d = changeDateByFormat(date, "yyyy年MM月dd日HH时mm分");
			parameters.put("diagnosedDate", d);
		}
//		if (parameters.get("fillDate") != null) {
//			Date date = (Date) parameters.get("fillDate");
//			String d = changeDateByFormat(date, null);
//			parameters.put("fillDate", d);
//		}
//		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doList(cnd, "a.phrId desc", BSPHISEntryNames.EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			if (map.get("regionCode_text") != null) {
				parameters.put("regionCode_text", map.get("regionCode_text"));
			}
		}
		try {
			list = dao.doQuery(cnd, null,
					BSPHISEntryNames.IDR_Report2);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			Map<String, Object> body = changeCombobox(map, "chis.application.idr.schemas.IDR_Report");
			parameters.putAll(body);
		}
		return parameters;
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
	}
	
	protected Map<String, Object> changeCombobox(Map<String, Object> map,
			String schemaId) throws PrintException {
		if (map == null) {
			return null;
		}
		Map<String, Object> m = new HashMap<String, Object>();
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			throw new PrintException(444, e.getMessage());
		}
		List<SchemaItem> scItems = sc.getItems();
		for (SchemaItem schemaItem : scItems) {
			boolean f = schemaItem.isCodedValue();
			String id = schemaItem.getId();
			String v = String.valueOf(map.get(id));
			if (f == true && v != null && !v.equals("")) {
				String[] vs = v.split(",");
				for (int i = 0; i < vs.length; i++) {
					String value = vs[i];
					m.put(id + value, "√");
				}
			}
		}
		m.putAll(map);
		return m;
	}

	@SuppressWarnings("rawtypes")
	protected void sqlDate2String(Map<String, Object> response) {
		Set set = response.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object o = response.get(key);
			if (o instanceof Date) {
				if(key.equals("diagnosedDate")){
					//response.put(key, new SimpleDateFormat("yyyy-MM-dd").format(o));
					//Date date = (Date) response.get(key);
					String d = changeDateByFormat((Date) response.get(key), "yyyy年MM月dd日HH时");
					response.put("diagnosedDate",d);
				}
				else{
				response.put(key, new SimpleDateFormat("yyyy年MM月dd日").format(o));
				}
			} else if (o instanceof String) {
				String s = (String) o;
				if (s.length() >= 10) {
					s = s.substring(0, 10);
					if (s.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
						response.put(key, s);
					}
				}
			}
		}
	}
	
	protected void change2String(Map<String, Object> response) {
		Set<String> set = response.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object o = response.get(key);
			if (!(o instanceof Date)) {
				response.put(key,
						String.valueOf(response.get(key) != null ? response
								.get(key) : ""));
			}
		}
	}
}
