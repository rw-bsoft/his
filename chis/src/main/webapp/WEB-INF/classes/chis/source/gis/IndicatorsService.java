package chis.source.gis;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.Constants;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class IndicatorsService implements Service {

	private static Log logger = LogFactory.getLog(IndicatorsService.class);

	@SuppressWarnings("rawtypes")
	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		String fileName=(String) ((Map)jsonReq.get("body")).get("fileName");
		String xmlString;
		try {
			xmlString = ServiceUtil.readXMLToString(fileName+".xml");
			if(xmlString.equals("")){
				xmlString=ServiceUtil.readXMLToString("ColorBase.xml");
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve data.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			return;
		}
		jsonRes.put("body", xmlString);
	}

}
