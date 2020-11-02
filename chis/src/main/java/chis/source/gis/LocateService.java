/**
 * HttpService.java Created on Aug 3, 2009 12:27:24 PM
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */

/**
 * @author <a href="mailto:yangwf@bsoft.com.cn">weifeng yang</a>
 * 
 * @description:
 */
package chis.source.gis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import ctd.service.core.Service;
import ctd.util.context.Context;

/**
 * @author <a href="mailto:yangwf@bsoft.com.cn">weifeng yang</a>
 * 
 * @description:
 */
public class LocateService implements Service {

	private static Log logger = LogFactory.getLog(LocateService.class);

	private static Context myCtx = null;

	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		myCtx = ctx;
		Map<String,Object> bodyJson = new HashMap<String,Object>();
		try {
			bodyJson.put("mapSign", getSubjectObj(jsonReq));
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to retrieve data.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			return;
		}
		jsonRes.put("body", bodyJson);
	}

	@SuppressWarnings("rawtypes")
	public String getSubjectObj(Map<String,Object> jsonObj) throws PersistentDataOperationException {
		String regionCode = StringUtils.trimToEmpty((String)jsonObj.get("regionCode"));
		String code = "";
		String codeSQL = "SELECT mapSign from EHR_AreaGrid where RegionCode='"
				+ regionCode + "'";
		List codelist = ServiceUtil.executeSql(codeSQL, myCtx);
		if (codelist.size() > 0) {
			code = (String) codelist.get(0);
		}

		while (code == null && regionCode.length() > 6) {
			if (regionCode.length() > 10) {
				regionCode = regionCode.substring(0, regionCode.length() - 3);
			} else {
				regionCode = regionCode.substring(0, regionCode.length() - 2);
			}
			codeSQL = "SELECT mapSign from EHR_AreaGrid where RegionCode='"
					+ regionCode + "'";
			codelist = ServiceUtil.executeSql(codeSQL, myCtx);
			if (codelist.size() > 0)
				code = (String) codelist.get(0);
		}
		return code;

	}
}
