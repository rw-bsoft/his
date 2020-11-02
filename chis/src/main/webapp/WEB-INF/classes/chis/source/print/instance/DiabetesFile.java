package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.IHandler;
import ctd.print.PrintException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class DiabetesFile extends BSCHISPrint implements IHandler {

	protected String empiId;

	protected Context ctx;

	protected HibernateTemplate ht;

	@SuppressWarnings("unused")
	private int j = 0;

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		this.ctx = ctx;
		getDao(ctx);
		ht = new HibernateTemplate(AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class));
		
		String jrxml = (String) request.get("jrxml");
		try {
			if (jrxml.contains("diabetescard")) {
				response.putAll(this.getDiabetesRecordMap(request, ctx));
			} else if (jrxml.contains("diabetesVisit")) {
				response.putAll(this.getDiabetesVisitMap(request, ctx));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		sqlDate2String(response);
		response.put("printDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		change2String(response);
	}

	private Map<String, Object> getDiabetesVisitMap(
			Map<String, Object> request, Context ctx) throws PrintException,
			ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String exp = "['eq',['$','a.empiId'],['s','" + empiId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp),null,
					BSCHISEntryNames.MDC_DiabetesVisit);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		parameters.putAll(this.getFilterList(list, parameters));

		List<Map<String, Object>> l1 = getBaseInfo(MDC_DiabetesRecord, request,
				ctx);
		if (l1.size() == 0) {
			return parameters;
		}
		List<Map<String, Object>> l2 = getBaseInfo(MPI_DemographicInfo,
				request, ctx);

		parameters.putAll(getFirstRecord(l1));
		parameters.putAll(getFirstRecord(l2));

		return parameters;
	}

	private Map<String, Object> getFilterList(List<Map<String, Object>> list,
			Map<String, Object> parameters) throws PrintException,
			ServiceException {
		// Map<String, Object> data = new HashMap<String, Object>();
		int n = 0;
		if (list.size() > 0) {
			Map<String, Object> m = new HashMap<String, Object>();
			for (int j = 0; j < list.size(); j++) {
				n = j + 1;
				m = list.get(j);
				m.put("targetTrain",
						getStringFromInt((Integer) m
								.get("targetTrainTimesWeek"))
								+ "次/周"
								+ "  "
								+ getStringFromInt((Integer) m
										.get("targetTrainMinute")) + "分钟/次");
				m.put("train",
						getStringFromInt((Integer) m.get("trainTimesWeek"))
								+ "次/周"
								+ "  "
								+ getStringFromInt((Integer) m
										.get("trainMinute")) + "分钟/次");
				m.put("hbA1c",
						"糖化血红蛋白: " + getStringFromInt((Double) m.get("hbA1c"))
								+ " %");
				m.put("testDate", "检查日期: "
						+ getStringFromInt(m.get("testDate")));
				m.put("fbs", getStringFromInt(m.get("fbs")));
				Iterator<?> it = m.keySet().iterator();
				while (it.hasNext()) {
					String s = (String) it.next();
					parameters.put(s + n, m.get(s));
				}
				String phrId = (String) m.get("phrId");
				String visitId = (String) m.get("visitId");
				String exp = "['and',['eq',['$','phrId'],['s','" + phrId
						+ "']],['eq',['$','visitId'],['s','" + visitId + "']]]";
				List<Map<String, Object>> medicineList = null;
				try {
					medicineList = dao.doQuery(toListCnd(exp),null,
					BSCHISEntryNames.MDC_DiabetesMedicine);
				} catch (PersistentDataOperationException e) {
					e.printStackTrace();
				}
				Map<String, Object> o = new HashMap<String, Object>();
				int k = 0;
				System.out.println("|medicineList.size()|"
						+ medicineList.size());
				if (medicineList.size() > 0) {
					for (int i = 0; i < medicineList.size(); i++) {
						k = i + 1;
						o = medicineList.get(i);
						Iterator<?> iterator = o.keySet().iterator();
						while (iterator.hasNext()) {
							String s = (String) iterator.next();
							parameters.put(s + n + k, o.get(s));
						}
					}
				}

			}
		} else {
			Map<String, Object> m = new HashMap<String, Object>();
			fillData(m, BSCHISEntryNames.MDC_DiabetesVisit);
			m.put("fbs1", getStringFromInt(m.get("fbs")));
			parameters.putAll(m);
		}

		System.out.println("|parameters|" + parameters);
		return parameters;
	}

	private Map<String, Object> getDiabetesRecordMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<Map<String, Object>> l1 = getBaseInfo(MDC_DiabetesRecord, map, ctx);
		if (l1.size() == 0) {
			return parameters;
		}
		List<Map<String, Object>> l2 = getBaseInfo(MPI_DemographicInfo, map,
				ctx);

		parameters.putAll(getFirstRecord(l1));
		parameters.putAll(getFirstRecord(l2));
		this.getFamilyHistroy(parameters);
		this.calculateYears(parameters);
		this.calculateBMI(parameters);
		this.getComplicationCode(parameters);
		return parameters;
	}

	private void calculateYears(Map<String, Object> parameters) {
		Date diagnosisDate = (Date) parameters.get("diagnosisDate");
		Date d = new Date();

		long m = d.getTime() - diagnosisDate.getTime();
		int time = (int) (m / (1000 * 60 * 60 * 24));
		String years = "";
		if (time > 365) {
			years = (int) time / 365 + "年";
		} else if (time < 31) {
			years = time + "天";
		} else {
			years = (int) time / 31 + "月";
		}
		parameters.put("years", years);
	}

	private void getComplicationCode(Map<String, Object> parameters)
			throws PrintException, ServiceException {
		String phrId = (String) parameters.get("phrId");
		String exp = "['eq',['$','phrId'],['s','" + phrId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null,
					BSCHISEntryNames.MDC_DiabetesComplication);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String complicationCode = "";
		Map<String, Object> d = new HashMap<String, Object>();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				d = list.get(i);
				if (complicationCode.equals("")) {
					complicationCode = (String) d.get("complicationCode_text");
				} else {
					complicationCode += "," + d.get("complicationCode_text");
				}
			}
			parameters.put("complicationCode_text", complicationCode);
		}

	}

	private void calculateBMI(Map<String, Object> parameters) {
		double weight = (Double) parameters.get("weight");
		double height = (Double) parameters.get("height");

		double b = weight / (height * height / 10000);
		parameters.put("bmi", b);
	}

	private void getFamilyHistroy(Map<String, Object> map)
			throws PrintException, ServiceException {
		String exp = "['and',['eq',['$','empiId'],['s','" + empiId
				+ "']],['eq',['$','pastHisTypeCode'],['s','08']]]";
		Map<String, Object> d = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), "pastHisTypeCode",
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		String diseaseText = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				d = list.get(0);
				if (diseaseText.equals("")) {
					diseaseText += d.get("diseaseText");
				} else {
					diseaseText = "," + d.get("diseaseText");
				}
			}
			map.put("fatherHistory", diseaseText);
		}

		exp = "['and',['eq',['$','empiId'],['s','" + empiId
				+ "']],['eq',['$','pastHisTypeCode'],['s','09']]]";
		d = new HashMap<String, Object>();
		try {
			list = dao.doQuery(toListCnd(exp), "pastHisTypeCode",
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e2) {
			e2.printStackTrace();
		}
		diseaseText = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				d = list.get(0);
				if (diseaseText.equals("")) {
					diseaseText += d.get("diseaseText");
				} else {
					diseaseText = "," + d.get("diseaseText");
				}
			}
			map.put("motherHistory", diseaseText);
		}

		exp = "['and',['eq',['$','empiId'],['s','" + empiId
				+ "']],['eq',['$','pastHisTypeCode'],['s','10']]]";
		d = new HashMap<String, Object>();
		try {
			list = dao.doQuery(toListCnd(exp), "pastHisTypeCode",
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		diseaseText = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				d = list.get(0);
				if (diseaseText.equals("")) {
					diseaseText += d.get("diseaseText");
				} else {
					diseaseText = "," + d.get("diseaseText");
				}
			}
			map.put("brotherHistory", diseaseText);
		}

		exp = "['and',['eq',['$','empiId'],['s','" + empiId
				+ "']],['eq',['$','pastHisTypeCode'],['s','11']]]";
		d = new HashMap<String, Object>();
		try {
			list = dao.doQuery(toListCnd(exp), "pastHisTypeCode",
					BSCHISEntryNames.EHR_PastHistory);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		diseaseText = "";
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				d = list.get(0);
				if (diseaseText.equals("")) {
					diseaseText += d.get("diseaseText");
				} else {
					diseaseText = "," + d.get("diseaseText");
				}
			}
			map.put("childrenHistory", diseaseText);
		}
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
