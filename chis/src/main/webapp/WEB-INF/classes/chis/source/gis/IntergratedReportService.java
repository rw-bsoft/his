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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.report.DynamicReport;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class IntergratedReportService implements Service {

	@SuppressWarnings("unused")
	private static Log logger = LogFactory
			.getLog(IntergratedReportService.class);

	@SuppressWarnings({ "unused", "rawtypes" })
	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		Map<String,Object> jsonBody = new HashMap<String,Object>();
//		ArrayList subjects = (ArrayList) jsonReq.get("subjects");
//		DynamicReport report = new DynamicReport(ctx,jsonReq);
//		HashMap<String, Object> hashMap = new HashMap<String, Object>(jsonReq);
//		Context qCtx = ContextUtils.getContext();
//		ctx.put("q", qCtx);
//		Map<String,Object> schema=report.getSchema();
//		ArrayList data=(java.util.ArrayList) report.getData();
//		jsonBody.put("data", data);
//		jsonBody.put("schema", schema);
//		jsonRes.put("body", jsonBody);
	}
}
