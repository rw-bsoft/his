/**
 * @(#)RabiesRecordModel.java Created on 2012-4-19 下午02:27:48
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.instance;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class PublicHealth extends BSCHISPrint implements IHandler {
	private String empiId;
	private Context ctx;
	private Map<String, Object> cycleNum = new HashMap<String, Object>();

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		this.ctx = ctx;
		getDao(ctx);
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
			throw new PrintException(Constants.CODE_PERSISTENT_ERROR,
					e.getMessage());
		}
		sqlDate2String(response);
		change2String(response);
	}

	private Map<String, Object> getSanitationEventMap(
			Map<String, Object> request, Context ctx2) throws PrintException,
			ServiceException {
		Map<String, Object> param = new HashMap<String, Object>();
		String RecordID = (String) request.get("RecordID");
		String exp = "['eq',['$','a.RecordID'],['s','" + RecordID + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null,
					BSCHISEntryNames.PHE_EnvironmentalEvent);
		} catch (PersistentDataOperationException e) {
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
		Map<String, Object> param = new HashMap<String, Object>();
		String RecordID = (String) request.get("RecordID");
		String exp = "['eq',['$','a.RecordID'],['s','" + RecordID + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null,
					BSCHISEntryNames.PHE_RelevantInformation);
		} catch (PersistentDataOperationException e) {
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

	protected File getFile(String filePath) throws ModelDataOperationException,
			IOException {
		String path = BSCHISUtil.getConfigPath(filePath);
		return new File(path);
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
		// Element dic = (Element) cycleDoc.selectSingleNode("//dic");
		// List<Element> re = dic.elements();
		// for (Element el : re) {
		// String key=el.attributeValue("key");
		// String text=el.attributeValue("text");
		// cycleNum.put(key, text);
		// }
	}

	private Map<String, Object> getInfectiousDiseaseMap(
			Map<String, Object> request, Context ctx2) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, request, ctx));
		if (parameters.get("sexCode") != null) {
			changeFieldToTrue(parameters, "sexCode");
		}
		if (parameters.get("birthday") != null) {
			Date date = (Date) parameters.get("birthday");
			String d = changeDateByFormat(date, null);
			parameters.put("birthday", d);
		}
		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), "a.phrId desc",
					BSCHISEntryNames.EHR_HealthRecord);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			if (map.get("regionCode_text") != null) {
				parameters.put("regionCode_text", map.get("regionCode_text"));
			}
		}
		try {
			list = dao.doQuery(toListCnd(exp), null,
					BSCHISEntryNames.IDR_Report);
		} catch (PersistentDataOperationException e) {
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

}
