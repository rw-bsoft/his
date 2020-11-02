package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import chis.source.BSCHISEntryNames;
import chis.source.PersistentDataOperationException;
import chis.source.print.base.BSCHISPrint;
import chis.source.util.BSCHISUtil;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public class PregnantHighRiskVisitFile extends BSCHISPrint implements IHandler {

	protected String pregnantId;

	protected String empiId;

	protected String visitId;

	protected Context ctx;

	public void getParameters(Map<String, Object> request,
			Map<String, Object> respons, Context ctx) throws PrintException {
		empiId = (String) request.get("empiId");
		pregnantId = (String) request.get("pregnantId");
		visitId = (String) request.get("visitId");
		getDao(ctx);
		request.put("MHC_PregnantRecord.pregnantId", pregnantId);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String cnd = "['eq',['$','a.visitId'],['s','" + visitId + "']]";
		try {
			respons.putAll(this.getFirstRecord(this.getBaseInfo(
					MPI_DemographicInfo, request, ctx)));
			respons.putAll(this.getFirstRecord(this.getBaseInfo(
					MHC_PregnantRecord, request, ctx)));
			list = dao.doQuery(toListCnd(cnd), "a.empiId", BSCHISEntryNames.MHC_VisitRecord);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		respons.putAll(this.getFirstRecord(list));
		respons.put("printDate",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

		cnd = "['eq',['$','a.visitId'],['s','" + visitId + "']]";

		// 高危因素
		try {
			list = dao.doQuery(toListCnd(cnd), null, BSCHISEntryNames.MHC_HighRiskVisitReason);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}
		if (list.size() > 0) {
			StringBuffer highRiskReason = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> data = list.get(i);
				if (i == list.size() - 1) {
					highRiskReason.append(data.get("highRiskReasonId_text"));
				} else {
					highRiskReason.append(data.get("highRiskReasonId_text"))
							.append(" ");
				}
			}
			respons.put("highRiskReason", highRiskReason.toString());
		}

		// 孕周
		String lastMenstrualPeriod = (String) respons
				.get("lastMenstrualPeriod").toString();
		String visitDate = (String) respons.get("visitDate").toString();
		int days = BSCHISUtil.getPeriod(BSCHISUtil.toDate(lastMenstrualPeriod),
				BSCHISUtil.toDate(visitDate));
		if (days > 0) {
			String pregnantWeek = (days / 7) + "周" + (days % 7) + "天";
			respons.put("pregnantWeek", pregnantWeek);
		}

		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
		Session session = sf.openSession();
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select count(*) as cnt from ")
					.append(BSCHISEntryNames.PUB_VisitPlan)
					.append(" where empiId =:empiId and str(planDate,'yyyy-MM-dd') <= :visitDate and planStatus = '1' and businessType=:planType");
			Query query = session.createQuery(hql.toString());
			query.setString("empiId", empiId);
			query.setString("visitDate", visitDate);
			query.setString("planType", (String) request.get("planType"));
			Object count = query.list().get(0);
			respons.put("count", Integer.parseInt(count.toString()));

			StringBuffer hql2 = new StringBuffer();
			hql2.append("select a.heightFundusUterus ,t.extend1 from ")
					.append(BSCHISEntryNames.MHC_VisitRecord)
					.append(" a, ")
					.append(BSCHISEntryNames.PUB_VisitPlan)
					.append(" t where a.visitId=t.visitId and t.businessType=:planType and t.extend1 >= 20 and str(t.planDate,'yyyy-MM-dd') <= :visitDate order by a.visitDate");
			Query query2 = session.createQuery(hql2.toString())
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			// query2.setString("empiId", empiId);
			query2.setString("visitDate", visitDate);
			query2.setString("planType", (String) request.get("planType"));
			List<?> list2 = query2.list();
			respons.put("heightFundusUterusList", list2);
			sqlDate2String(respons);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.flush();
				session.close();
			}
		}
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}
}
