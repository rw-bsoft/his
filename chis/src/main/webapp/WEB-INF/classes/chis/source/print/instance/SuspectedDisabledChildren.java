package chis.source.print.instance;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ctd.print.PrintException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import chis.source.print.base.PrintImpl;
import ctd.util.context.Context;

public class SuspectedDisabledChildren extends PrintImpl{

	
	@SuppressWarnings("unchecked")
	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		SessionFactory factory = getSessionFactory(ctx);
		Session session = factory.openSession();
		try{
			/*SQLQuery q = session.createSQLQuery("SELECT b.personName,b.sexCode,b.birthday,b.mobileNumber,b.address," +
					"a.disabilityReason," +
					"(select fatherName from EHR_HealthRecord where empiId = b.empiId and rownum <= 1) as fatherName " +
					"FROM CDH_DisabilityMonitor a, MPI_DemographicInfo b where a.empiId = b.empiId " +
					"and a.inputUnit like '"+requestData.get("manageUnit") + "%' and to_char(a.inputDate,'yyyy')="+requestData.get("year") + 
					" and str(a.inputDate,'mm')="+requestData.get("month"));*/
			SQLQuery q = session.createSQLQuery("SELECT b.personName,b.sexCode,b.birthday,b.mobileNumber,b.address," +
					"a.disabilityReason," +
					"(select fatherName from EHR_HealthRecord where empiId = b.empiId ) as fatherName " +
					"FROM CDH_DisabilityMonitor a, MPI_DemographicInfo b where a.empiId = b.empiId " +
					"and a.inputUnit like '"+request.get("manageUnit") + "%' and to_char(a.inputDate,'yyyy')="+request.get("year") + 
					" and str(a.inputDate,'mm')="+request.get("month"));
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			q.setFirstResult(1);
			q.setMaxResults(1);
			
			records = q.list();
			Iterator<Map<String, Object>> it = records.iterator();
			while(it.hasNext()){
				Map<String, Object> r = it.next();
				dicKeyToText(r, "chis.dictionary.gender", "SEXCODE");
				dicKeyToText(r, "chis.dictionary.disabilityType", "disabilityType");
			}
//			JRDataSource ds = new JRMapCollectionDataSource(list);
//			return ds;
		}finally{
			if (session != null && session.isOpen()) {
				session.flush();
				session.close();
			}
		}
	}

	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx)
			throws PrintException {
		response.put("manageUnit", dicKeyToText("chis.@manageUnit", request.get("manageUnit").toString()));
		response.put("reportDate", request.get("year")+"年"+request.get("month")+"月");
	}

}
