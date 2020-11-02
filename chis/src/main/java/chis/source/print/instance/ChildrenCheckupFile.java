/**
 * @(#)ChildrenRecordReportFile.java Created on 10:10:01 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class ChildrenCheckupFile extends BSCHISPrint implements IHandler {
	@SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(ChildrenCheckupFile.class);

	@SuppressWarnings("unused")
	private String empiId;

	private HibernateTemplate ht;

	// private String phrId;

	@SuppressWarnings({ "unchecked" })
	public void getParameters(Map<String, Object> map,
			Map<String, Object> parameters, Context ctx) throws PrintException {
		getDao(ctx);
		ht = new HibernateTemplate((AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class)));
		String checkupId = (String) map.get("checkupId");
		String exp = "['eq',['$','checkupId'],['s','" + checkupId + "']]";
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(toListCnd(exp), null,
					"CDH_Checkup");
		} catch (PersistentDataOperationException e3) {
			e3.printStackTrace();
		}

		if (list.size() > 0) {
			parameters.putAll(this.getFirstRecord(list));
		}
		String phrId = (String) map.get("phrId");

		Map<String, Object> m = (Map<String, Object>) ht.find(
				"from EHR_HealthRecord where phrId = ?", phrId).get(0);
		String empiId = (String) m.get("empiId");
		map.put("empiId", empiId);

		List<Map<String, Object>> listc = null;
		try {
			listc = dao.doQuery(toListCnd(exp), null,
						"CDH_Correction");
		} catch (PersistentDataOperationException e2) {
			e2.printStackTrace();
		}
		parameters.putAll(this.getFirstRecord(listc));

		String exp_visitplan = "['and',['eq',['$','empiId'],['s','"
				+ empiId
				+ "']],['eq',['$','businessType'],['s','5']],['eq',['$','extend1'],['s','"
				+ parameters.get("checkupStage") + "']]]";
		List<Map<String, Object>> listPlan = null;
		try {
			listPlan = dao.doQuery(toListCnd(exp_visitplan),
					"beginDate", BSCHISEntryNames.PUB_VisitPlan);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}

		if (listPlan.size() > 0) {
			Map<String, Object> temp = listPlan.get(0);
			String inquireId = (String) temp.get("visitId");
			if (inquireId != null) {
				Map<String, Object> mFeedWay = null;
				try {
					mFeedWay = getFirstRecord(getBaseInfo(CDH_Inquire, map, ctx));
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				parameters.put("feedWay_text", mFeedWay.get("feedWay_text"));
			}
		}

		try {
			parameters.putAll(this.getFirstRecord(this.getBaseInfo(
					MPI_DemographicInfo, map, ctx)));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> suggestions = (List<Map<String, Object>>) ht
				.find("from CDH_Correction where checkupId = ?", checkupId);
		if (suggestions.size() > 0) {
			String suggestion = ((String) suggestions.get(0).get("suggestion"));
			suggestion = suggestion.replaceAll("<br>", "");
			suggestion = suggestion.replaceAll("<BR>", "");
			suggestion = suggestion.replaceAll("&nbsp;", "");
			suggestion = suggestion.replaceAll(" ", "");
			suggestion = suggestion.replaceAll("</br>", "");
			parameters.put("history",
					((String) suggestions.get(0).get("history")));
			parameters.put("suggestion", suggestion);
			parameters.put("conclusion",
					((String) suggestions.get(0).get("conclusion")));
		}
		parameters.put("month", map.get("month"));

		String areaGrid = (String) ctx.get("server.manage.topUnit");
		Map<String, Object> topAreaGrid = null;
		try {
			topAreaGrid = getFirstRecord(dao.doQuery(
					toListCnd("['eq',['$','a.regionCode'],['s','" + areaGrid
							+ "']]"), "orderNo asc , regionCode asc",
							BSCHISEntryNames.EHR_AreaGrid));
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parameters.put("regionName", topAreaGrid.get("regionName"));
		sqlDate2String(parameters);
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
