package chis.source.print.instance;

import java.util.HashMap;
import java.util.Map;
import ctd.print.PrintException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class TumourFile extends PersonalFile {

	public void getParameters(Map<String, Object> map,
			Map<String, Object> response, Context ctx) throws PrintException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String jrxml = (String) map.get("jrxml");
		if (jrxml.contains("tumourrecord")) {
			try {
				parameters = getTumourMap(map,ctx);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		response.put("reportDate", parameters);
		sqlDate2String(response);
	}

	// 肿瘤档案
	private Map<String, Object> getTumourMap(Map<String, Object> map,
			Context ctx) throws PrintException, ServiceException {
		Map<String, Object> parameters = getFirstRecord(getBaseInfo(
				MPI_DemographicInfo, map, ctx));
		Map<String, Object> tumourData = getFirstRecord(getBaseInfo(
				MDC_TumourRecord, map, ctx));
		parameters.putAll(tumourData);
		return parameters;
	}


}
