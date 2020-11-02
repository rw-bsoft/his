/**
 * @(#)DiabetesVisitFile.java Created on 2015-2-28 下午1:52:10
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
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.chart.define.CHISUnionReportDefine;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">YuBo</a>
 */
public class DiabetesVisitFile extends BSCHISPrint implements IHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		try {
			String empiId = (String) request.get("empiId");
			String phrId = (String) request.get("phrId");
			String visitdate=BSCHISUtil.toString(new Date());
			if(request.containsKey("visitdate")){
				visitdate=request.get("visitdate")+"";
			}
			getDao(ctx);
			Map<String, Object> personInfo = dao.doLoad(
					BSCHISEntryNames.MPI_DemographicInfo, empiId);
			response.put("personName", personInfo.get("personName"));
			Map<String, Object> recordInfo = dao.doLoad(
					BSCHISEntryNames.MDC_DiabetesRecord, phrId);
			double height = 0.00;
			if (recordInfo!=null){
				height = (Double) recordInfo.get("height");	
			}
			response.put("phrId", phrId);
//			List<Map<String, Object>> visitRecords = getVisitRecords(empiId);
			List<Map<String, Object>> visitRecords = getVisitRecords(empiId,visitdate);
			int n = visitRecords.size();
			if (n > 4) {
				n = 4;
			}
			for (int i = 0; i < n; i++) {
				Map<String, Object> record = visitRecords.get(i);
				record = SchemaUtil.setDictionaryMessageForList(record,
						BSCHISEntryNames.MDC_HypertensionVisit);
				if (record.get("targetWeight") != null
						&& !"".equals(record.get("targetWeight") + "")) {
					double targetWeight = (Double) record.get("targetWeight");
					double targetBmi = targetWeight
							/ ((height / 100.0) * (height / 100.0));
					String b = String.format("%.2f", targetBmi);
					record.put("targetBmi", b);
				}
				setVisitDate(record);
				Set<String> key = record.keySet();
				key = record.keySet();
				for (Iterator it = key.iterator(); it.hasNext();) {
					String s = (String) it.next();
					if (record.get(s) == null || "null".equals(record.get(s))) {
						record.put(s, "");
					}
				}
				record.put("fbs", record.get("fbs") + "mmol/L");
				setFood(record);
				getMedicineInfo(record);
				setAdverseReactions(record);
				setSymptoms(record);
				setBloodPressure(record);
				setWeight(record);
				setBMI(record);
				setSmokeCount(record);
				setDrinkCount(record);
				setTrain(record);
				// TODO 胰岛素打印字段数据...

				for (Iterator it = key.iterator(); it.hasNext();) {
					String s = (String) it.next();
					if ("99".equals(s)) {
						continue;
					}
					response.put(s + (n - i), record.get(s));
				}
			}
			change2String(response);
			sqlDate2String(response);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	private void setFood(Map<String, Object> record) {
		String food = record.get("food") + "";
		String targetFood = record.get("targetFood") + "";
		record.put("food", food + "/" + targetFood);
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

	private void setSymptoms(Map<String, Object> record) {
		String symptoms = (String) record.get("symptoms");
		if (symptoms == null || "".equals(symptoms)) {
			return;
		}
		String[] c = symptoms.split(",");
		for (int i = 0; i < c.length; i++) {
			int key = Integer.parseInt(c[i]);
			record.put("symptoms" + (i + 1), key);
		}
	}

	private void setAdverseReactions(Map<String, Object> record) {
		String medicineBadEffect = record.get("adverseReactions") + "";
		if ("2".equals(medicineBadEffect)) {
			record.put("adverseReactions", "1");
		} else if ("1".equals(medicineBadEffect)) {
			record.put("adverseReactions", "2");
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
		Date testDate = (Date) record.get("testDate");
		if (testDate != null) {
			c.setTime(testDate);
			String td = c.get(Calendar.YEAR) + "年"
					+ (c.get(Calendar.MONTH) + 1) + "月"
					+ c.get(Calendar.DAY_OF_MONTH) + "日";
			record.put("nextDate", td);
		}
	}

	private void getMedicineInfo(Map<String, Object> record)
			throws PersistentDataOperationException {
		String visitId = (String) record.get("visitId");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
		List<Map<String, Object>> list = dao.doList(cnd, "createDate desc",
				BSCHISEntryNames.MDC_DiabetesMedicine);
		if (list == null || list.size() == 0) {
			return;
		}
		// list = SchemaUtil.setDictionaryMessageForList(list,
		// BSCHISEntryNames.MDC_HypertensionMedicine);
		int num = list.size();
		if (num > 3) {
			num = 3;
		}
		for (int i = 0; i < num; i++) {
			Map<String, Object> m = list.get(i);
			change2String(m);
			for (String key : m.keySet()) {
				if ("medicineFrequency".equals(key)) {
					StringBuffer sb = new StringBuffer("每日")
							.append(m.get("medicineFrequency")).append("次,每次")
							.append(m.get("medicineDosage"))
							.append(m.get("medicineUnit"));
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
				BSCHISEntryNames.MDC_DiabetesVisit);
	}
	private List<Map<String, Object>> getVisitRecords(String empiId,String visitdate)
			throws PersistentDataOperationException {
		String visitdatestr = "date('" + visitdate + "')";
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("le", "a.visitDate", "$", visitdatestr);
		List<?> cnd=CNDHelper.createArrayCnd("and", cnd1, cnd2);
		return dao.doList(cnd, "a.visitDate desc",
				BSCHISEntryNames.MDC_DiabetesVisit);
	}

	@Override
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
