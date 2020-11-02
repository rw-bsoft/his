package chis.source.print.instance;

import java.util.HashMap;
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

public class TiRuoRrZhuiFangDan extends BSCHISPrint implements IHandler {

	@SuppressWarnings({ "unused" })
	public void getParameters(Map<String, Object> request,
			Map<String, Object> response, Context ctx) throws PrintException {
		getDao(ctx);
		String empiId = request.get("empiId").toString();
		String visitId = request.get("visitId").toString();
		Map<String, Object> map = new HashMap<String, Object>();
		HibernateTemplate ht = new HibernateTemplate(AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class));
		
		Map<String, Object> d1 = null;
		Map<String, Object> d2 = null;
		Map<String, Object> d3 = null;
		try {
			d1 = getFirstRecord(getBaseInfo(MPI_DemographicInfo, request, ctx));
			d2 = getFirstRecord(getBaseInfo(CDH_DebilityChildren, request, ctx));
			d3 = getFirstRecord(getBaseInfo(CDH_HealthCard, request, ctx));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Map<String, Object> d4 = new HashMap<String, Object>();
		try {
			d4 = dao.doQuery(toListCnd("['eq',['$','visitId'],['s','" + visitId
					+ "']]"), null,
					BSCHISEntryNames.CDH_DebilityChildrenVisit).get(0);
		} catch (PersistentDataOperationException e1) {
			e1.printStackTrace();
		}

		String areaGrid = (String) ctx.get("server.manage.topUnit");
		Map<String, Object> topAreaGrid = null;
		try {
			topAreaGrid = getFirstRecord(dao.doQuery( toListCnd("['eq',['$','a.regionCode'],['s','"
					+ areaGrid + "']]"), "orderNo asc , regionCode asc",
					BSCHISEntryNames.EHR_AreaGrid));
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		map.put("regionName", topAreaGrid.get("regionName"));

		map.putAll(d1);
		map.putAll(d2);
		map.putAll(d3);
		map.putAll(d4);
		response.putAll(map);
		sqlDate2String(response);
	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {

	}

}
