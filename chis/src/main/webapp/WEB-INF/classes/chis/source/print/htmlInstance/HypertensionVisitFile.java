/**
 * @(#)HypertensionVisitFile.java Created on 2015-2-12 上午9:56:39
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.print.htmlInstance;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class HypertensionVisitFile extends BSCHISPrint implements IHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			String empiId = (String) request.get("empiId");
			String phrId = (String) request.get("phrId");
			getDao(ctx);
			Map<String, Object> personInfo = dao.doLoad(
					BSCHISEntryNames.MPI_DemographicInfo, empiId);
			response.put("personName", personInfo.get("personName"));
			response.put("phrId", phrId);
			List<Map<String, Object>> visitRecords = getVisitRecords(empiId);
			int n = visitRecords.size();
			if (n > 4) {
				n = 4;
			}
			for (int i = 0; i < n; i++) {
				Map<String, Object> record = visitRecords.get(i);
				record = SchemaUtil.setDictionaryMessageForList(record,
						BSCHISEntryNames.MDC_HypertensionVisit);
				setVisitDate(record);
				Set<String> key = record.keySet();
				for (Iterator it = key.iterator(); it.hasNext();) {
					String s = (String) it.next();
					if (record.get(s) == null || "null".equals(record.get(s))) {
						record.put(s, "");
					}
				}
				getMedicineInfo(record);
				setMedicineBadEffect(record);
				setCurrentSymptoms(record);
				setBloodPressure(record);
				setWeight(record);
				setBMI(record);
				setHeartRate(record);
				setSmokeCount(record);
				setDrinkCount(record);
				setTrain(record);
				setSalt(record);
				key = record.keySet();
				for (Iterator it = key.iterator(); it.hasNext();) {
					String s = (String) it.next();
					response.put(s + (n - i), record.get(s));
				}
			}
			change2String(response);
			sqlDate2String(response);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	private void setMedicineBadEffect(Map<String, Object> record) {
		String medicineBadEffect = (String) record.get("medicineBadEffect");
		if ("n".equals(medicineBadEffect)) {
			record.put("medicineBadEffect", "1");
		} else if ("y".equals(medicineBadEffect)) {
			record.put("medicineBadEffect", "2");
		}
	}

	private void setSalt(Map<String, Object> record) {
		String v1 = record.get("salt") + "";
		int k1 = 0;
		if (v1 != null && !"".equals(v1) && !"null".equals(v1)) {
			k1 = Integer.parseInt(v1);
		}
		String v2 = record.get("targetSalt") + "";
		int k2 = 0;
		if (v2 != null && !"".equals(v2) && !"null".equals(v1)) {
			k2 = Integer.parseInt(v2);
		}
		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s4 = "";
		String s5 = "";
		String s6 = "";
		switch (k1) {
		case 1:
			s1 = "√";
			break;
		case 2:
			s2 = "√";
			break;
		case 3:
			s3 = "√";
			break;
		}
		switch (k2) {
		case 1:
			s4 = "√";
			break;
		case 2:
			s5 = "√";
			break;
		case 3:
			s6 = "√";
			break;
		}
		String salt = "轻" + s1 + "/中" + s2 + "/重" + s3;
		String targetSalt = " /轻" + s4 + "/中" + s5 + "/重" + s6;
		record.put("salt", salt);
		record.put("targetSalt", targetSalt);
	}

	private void setTrain(Map<String, Object> record) {
		String trainTimesWeek = record.get("trainTimesWeek") + "";
		String trainMinute = record.get("trainMinute") + "";
		String targetTrainTimesWeek = record.get("targetTrainTimesWeek") + "";
		String targetTrainMinute = record.get("targetTrainMinute") + "";
		record.put("train", trainTimesWeek + " 次/周 " + trainMinute + " 分钟/次");
		record.put("targetTrain", targetTrainTimesWeek + " 次/周 "
				+ targetTrainMinute + " 分钟/次");
	}

	private void setDrinkCount(Map<String, Object> record) {
		String drinkCount = record.get("drinkCount") + "";
		String targetDrinkCount = record.get("targetDrinkCount") + "";
		record.put("drinkCount", drinkCount + " / " + targetDrinkCount);
	}

	private void setSmokeCount(Map<String, Object> record) {
		String smokeCount = record.get("smokeCount") + "";
		String targetSmokeCount = record.get("targetSmokeCount") + "";
		record.put("smokeCount", smokeCount + " / " + targetSmokeCount);
	}

	private void setHeartRate(Map<String, Object> record) {
		String heartRate = record.get("heartRate") + "";
		String targetHeartRate = record.get("targetHeartRate") + "";
		record.put("heartRate", heartRate + " / " + targetHeartRate);
	}

	private void setBMI(Map<String, Object> record) {
		String bmi = record.get("bmi") + "";
		String targetBmi = record.get("targetBmi") + "";
		record.put("bmi", bmi + " / " + targetBmi);
	}

	private void setWeight(Map<String, Object> record) {
		String weight = record.get("weight") + "";
		String targetWeight = record.get("targetWeight") + "";
		record.put("weight", weight + " / " + targetWeight);
	}

	private void setBloodPressure(Map<String, Object> record) {
		String constriction = record.get("constriction") + "";
		String diastolic = record.get("diastolic") + "";
		record.put("bloodPressure", constriction + " / " + diastolic);
	}

	private void setCurrentSymptoms(Map<String, Object> record) {
		String currentSymptoms = (String) record.get("currentSymptoms");
		if (currentSymptoms == null || "".equals(currentSymptoms)) {
			return;
		}
		String[] c = currentSymptoms.split(",");
		for (int i = 0; i < c.length; i++) {
			String s = "";
			int key = Integer.parseInt(c[i]);
			switch (key) {
			case 9:
				s = "1";
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				s = key + 1 + "";
				break;
			}
			if (!"".equals(s)) {
				record.put("currentSymptoms" + (i + 1), s);
			}
		}
	}

	private void setVisitDate(Map<String, Object> record) {
		Date visitDate = (Date) record.get("visitDate");
		Calendar c = Calendar.getInstance();
		if (visitDate != null) {
			c.setTime(visitDate);
			String vd = c.get(Calendar.YEAR) + "年"
					+ (c.get(Calendar.MONTH) + 1) + "月"
					+ c.get(Calendar.DAY_OF_MONTH) + "日";
			record.put("visitDate", vd);
		}
		Date nextDate = (Date) record.get("nextDate");
		if (nextDate != null) {
			c.setTime(nextDate);
			String nd = c.get(Calendar.YEAR) + "年"
					+ (c.get(Calendar.MONTH) + 1) + "月"
					+ c.get(Calendar.DAY_OF_MONTH) + "日";
			record.put("nextDate", nd);
		}
	}

	private void getMedicineInfo(Map<String, Object> record)
			throws PersistentDataOperationException {
		String visitId = (String) record.get("visitId");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
		List<Map<String, Object>> list = dao.doList(cnd, "createDate desc",
				BSCHISEntryNames.MDC_HypertensionMedicine);
		if (list == null || list.size() == 0) {
			return;
		}
		// list = SchemaUtil.setDictionaryMessageForList(list,
		// BSCHISEntryNames.MDC_HypertensionMedicine);
		int num = list.size();
		if (num > 4) {
			num = 4;
		}
		for (int i = 0; i < num; i++) {
			Map<String, Object> m = list.get(i);
			for (String key : m.keySet()) {
				if ("medicineFrequency".equals(key)) {
					StringBuffer sb = new StringBuffer("每日")
							.append(m.get("medicineFrequency")).append("次,每次")
							.append(m.get("medicineDosage"));
					if (m.get("medicineUnit") != null) {
						sb.append(m.get("medicineUnit"));
					}
					record.put("usage" + (i + 1), sb);
				}
				record.put(key + (i + 1), m.get(key));
			}
		}
	}

	private List<Map<String, Object>> getVisitRecords(String empiId)
			throws PersistentDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		return dao.doList(cnd, "a.visitDate desc",
				BSCHISEntryNames.MDC_HypertensionVisit);
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		// TODO Auto-generated method stub

	}

}
