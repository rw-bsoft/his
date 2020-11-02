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
import java.util.Iterator;
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
public class HighLightService implements Service {

	private static Log logger = LogFactory.getLog(HighLightService.class);

	private static Context myCtx = null;

	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		myCtx = ctx;
		Map<String,Object> bodyJson;
		try {
			bodyJson = getSubjectObj(jsonReq);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to retrieve data.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			return;
		}
		jsonRes.put("body", bodyJson);
		if (bodyJson == null) {
			jsonRes.put(RES_CODE, 888);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,Object> getSubjectObj(Map<String,Object> jsonObj) throws PersistentDataOperationException {
		Map<String,Object> json = null;
		Map<String,Object> jsonReturn = new HashMap<String,Object>();
		StringBuffer mapSignSQL = new StringBuffer();
		try {
			Map<String,Object> subjects = (Map<String, Object>) jsonObj.get("subjects");
			String mapsign = StringUtils.trimToEmpty((String)jsonObj.get("mapSign"));
			List codelist;
			String codesSQL;
			String code = "";
				String codeSQL = "SELECT regionCode from EHR_AreaGrid where MapSign='"
						+ mapsign + "'";
				codelist = ServiceUtil.executeSql(codeSQL, myCtx);
				if (codelist.size() > 0) {
					code = codelist.get(0).toString();
				}
			codesSQL = "from EHR_AreaGrid where substring(regionCode,1,"
						+ code.length() + ")='" + code
						+ "' and (length(regionCode)="
						+ LayerDic.layerMapping.get(String.valueOf(code.length()) ) + " or length(regionCode)="
						+code.length() +")";
			
			List codes = ServiceUtil.executeSql(codesSQL, myCtx);
			StringBuffer stCodes = new StringBuffer();
			for (int i = 0; i < codes.size(); i++) {
				Object object = (Object) codes.get(i);
				Map map = (Map) object;
				Iterator iter = map.keySet().iterator();
				Map<String,Object> regionCodeObj = new HashMap<String,Object>();
				String regionCode = new String();
				String rsmapSign = new String();
				while (iter.hasNext()) {
					if (iter.next().toString().equals("regionCode")) {
						regionCode = (String)map.get("regionCode");
						rsmapSign = (String)map.get("mapSign");
						if(regionCode==null||rsmapSign==null){
							continue;
						}
						regionCodeObj.put(rsmapSign, regionCode);
					}
				}
				if (i == 0) {
					stCodes.append("regionCode='" + regionCode + "'");
				} else {
					stCodes.append(" or regionCode='" + regionCode + "'");
				}
			}
			mapSignSQL.append("from EHR_AreaGrid where ");
			mapSignSQL.append(stCodes);
			List MapSignList = ServiceUtil.executeSql(mapSignSQL.toString(),
					myCtx);
			Iterator it=subjects.entrySet().iterator();
			String date = StringUtils.trimToEmpty((String)jsonObj.get("date"));
			for(;it.hasNext();){
				String key=(String) it.next();
				Map<String,Object> value=(Map<String, Object>) subjects.get(key);
				String kpicode = StringUtils.trimToEmpty((String)value.get("kpicode"));
				json = getKPI(kpicode, stCodes, date,MapSignList);
				if (json == null)
					return null;
				jsonReturn.put(key, json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonReturn;
	}

	@SuppressWarnings("rawtypes")
	private Map<String,Object> getKPI(String subjectId, StringBuffer stCodes,String date,List MapSignList) throws PersistentDataOperationException {
		Map<String,Object> mapSignObj = new HashMap<String,Object>();
		StringBuffer kpiSQL = new StringBuffer();
		kpiSQL.append("from STAT_Grid where KPICode='" + subjectId + "'");
		kpiSQL.append(" and (");
		kpiSQL.append(stCodes);
		kpiSQL.append(")");
		kpiSQL.append(" and statDate=(select max(statDate)from STAT_Grid");
		
		if(date!=null && !date.equals("")){
			kpiSQL.append(" where (str(statDate,'yyyy-MM-dd')='"
					+ date
					+ "' or str(statDate,'yyyy-MM-dd')<'"
					+ date + "' and KPICode='" + subjectId + "') and KPICode='" + subjectId + "'");
		}else{
		kpiSQL.append(" where KPICode='" + subjectId + "') and KPICode='" + subjectId + "'");
		}
		kpiSQL.append(")");
		List KPIList = ServiceUtil.executeSql(kpiSQL.toString(), myCtx);
		for (int j = 0; j < KPIList.size(); j++) {
			Object objKPI = (Object) KPIList.get(j);
			Map KPIListMap = (Map) objKPI;
			Iterator KPIListIter = KPIListMap.keySet().iterator();
			String mapSign = new String();
			String recode = new String();
			String KPI = new String();
			Map<String,Object> KPIObject = new HashMap<String,Object>();
			while (KPIListIter.hasNext()) {
				if (ServiceUtil.getToString(KPIListIter.next()).equals(
						"regionCode")) {
					recode = ServiceUtil.getToString(KPIListMap
							.get("regionCode"));
					KPI = ServiceUtil.getToString(KPIListMap.get("KPI"));
				}
			}
			for (int s=0;s<MapSignList.size();s++)
			{
				Object objMapSign = (Object) MapSignList.get(s);
				Map MapSignMap = (Map) objMapSign;
				if(MapSignMap.get("regionCode").equals(recode))
				{
					mapSign=(String)MapSignMap.get("mapSign");
				}
			
			}
			KPIObject.put(recode, KPI);
			if(mapSign==null){
				continue;
			}
			mapSignObj.put(mapSign, KPIObject);
		}
		return mapSignObj;
	}

	public static String getmapSignlayer(String mapsign) {
		String[] map = mapsign.split(",");
		String len = new String();
		if ("16".equals(map[0])) {
			len = "6";
		}
		if ("15".equals(map[0])) {
			len = "8";
		}
		if ("14".equals(map[0])) {
			len = "10";
		}
		if ("13".equals(map[0])) {
			len = "13";
		}
		if ("12".equals(map[0])) {
			len = "16";
		}
		if ("1".equals(map[0])) {
			len = "16";
		}
		return len;
	}
}
