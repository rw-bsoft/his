package chis.source.print.instance;

import java.util.Date;
import java.util.List;
import java.util.Map;


import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.util.HtmlUtils;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class CVDFile extends BSCHISPrint implements IHandler {

	protected String inquireId;

	protected String empiId;

	protected Context ctx;

	protected HibernateTemplate ht;

	@SuppressWarnings({ "unchecked" })
	public void getParameters(Map<String, Object> map,
			Map<String, Object> parameters, Context ctx) throws PrintException {
		inquireId = (String) map.get("inquireId");
		getDao(ctx);
		ht = new HibernateTemplate(AppContextHolder.getBean(
				AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class));
		Map<String, Object> resultMap = null;
		try {
			Map<String, Object> param = getFirstRecord(getBaseInfo(
					MPI_DemographicInfo, map, ctx));
			parameters.putAll(param);
			resultMap = this.getFirstRecord(this.getBaseInfo(
					CVD_AssessRegister, map, ctx));
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		double tc = (Double) resultMap.get("tc") == null ? 0
				: (Double) resultMap.get("tc");

		String answer = resultMap.get("answer") == null ? ""
				: (String) resultMap.get("answer");
		if (!answer.equals("")) {
			int wrong = 0;
			for (int i = 0; i < answer.length(); i++) {
				String tmp = answer.substring(i, i + 1);
				if (tmp.equals("0")) {
					wrong += 1;
				}

			}
			int score = (100 - wrong * 3);
			parameters.put("score", score);
		}

		if ((Double) resultMap.get("tc") == null) {
			parameters.put("isTc", "不详");
		} else {
			if (tc >= 4) {
				parameters.put("isTc", "否");
			} else {
				parameters.put("isTc", "是");
			}
		}

		double diabetes = (Double) resultMap.get("fbs") == null ? 0
				: (Double) resultMap.get("fbs");
		if (diabetes >= 6.1) {
			parameters.put("isDiabetes", "否");
		} else {
			parameters.put("isDiabetes", "是");
		}
		int constriction = Integer.valueOf(resultMap.get("constriction")
				.toString());
		int diastolic = Integer.valueOf(resultMap.get("diastolic").toString());
		if (constriction > 140 || diastolic > 90) {
			parameters.put("isBp", "否");
		} else {
			parameters.put("isBp", "是");
		}
		String exp = "['eq',['$','inquireId'],['s','" + inquireId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null,
					BSCHISEntryNames.CVD_Appraisal);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

		Map<String, Object> m = this.getFirstRecord(list);

		parameters.putAll(resultMap);
		parameters.put("riskPrediction", m.get("riskPrediction"));
		parameters.put("riskAssessment", m.get("riskAssessment") == null ? "无"
				: m.get("riskAssessment"));
		if (m.get("lifeStyle") != null) {
			String lifeStyle = (String) m.get("lifeStyle");
			parameters.put("lifeStyle", HtmlUtils.htmlUnescape(lifeStyle).replaceAll("<br>", ""));
		} else {
			parameters.put("lifeStyle", "无");
		}
		if (m.get("drugs") != null) {
			String drugs = (String) m.get("drugs");
			parameters.put("drugs", HtmlUtils.htmlUnescape(drugs).replaceAll("<br>", ""));
		} else {
			parameters.put("drugs", "无");
		}
		parameters.put(
				"riskiness_text",
				resultMap.get("riskiness_text") == null ? "无" : resultMap
						.get("riskiness_text"));
		parameters.put("printDate", new Date());
		sqlDate2String(parameters);

	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
