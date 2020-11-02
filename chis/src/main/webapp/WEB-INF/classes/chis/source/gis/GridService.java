//package chis.source.gis;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import chis.source.Constants;
//import chis.source.PersistentDataOperationException;
//import ctd.service.core.Service;
//import ctd.util.context.Context;
//
//public class GridService extends SimpleReport implements Service {
//	private static Log logger = LogFactory.getLog(GridService.class);
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public void execute(HashMap<String,Object> jsonReq, HashMap<String,Object> jsonRes, Context ctx){
//		String code = StringUtils.trimToEmpty((String)jsonReq.get("regionCode"));
//		HashMap<String,Object> subjects = (HashMap<String, Object>) jsonReq.get("subjects");
//		String hql = "select regionCode,mapSign from EHR_AreaGrid where regionCode like  concat('"
//				+ code
//				+ "','%') and length(regionCode)="
//				+ LayerDic.layerMapping.get(String.valueOf(code.length())) + "";
//		HashMap<String,Object> listObj;
//		try {
//			listObj = ServiceUtil.executeSqlObj(hql, ctx);
//		} catch (PersistentDataOperationException e) {
//			logger.error("Failed to retrieve data.", e);
//			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
//			return;
//		}
//		List returnArray = BuildDefine.getGridIntegratedCfg(jsonReq,
//				listObj, ctx);
//		for(int i=0;i<returnArray.size();i++){
//			HashMap<String,Object> returnObj = getSubjectObj(((HashMap<String,Object>)returnArray.get(i)), subjects, listObj);
//			returnObj.put("codeList", listObj);
//			jsonRes.put("body", returnObj);
//		}
//
//	}
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private HashMap<String,Object> getSubjectObj(HashMap<String,Object> returnArray,
//			HashMap<String,Object> subjects, HashMap<String,Object> listObj){
//		HashMap<String,Object> returnObj = new HashMap<String,Object>();
//		HashMap<String,Object> subjectObj = null;
//		HashMap<String,Object> body = returnArray;
//		List codeList = new ArrayList();
//		Iterator it = subjects.entrySet().iterator();
//		for (; it.hasNext();) {
//			String key = (String) it.next();
//			subjectObj = new HashMap<String,Object>();
//			for (int i = 0; i < body.size(); i++) {
//				HashMap<String,Object> subjectCode = new HashMap<String,Object>();
//				HashMap<String,Object> subBody = (HashMap<String, Object>) body.get(i);
//				String regionCode = StringUtils.trimToEmpty((String)subBody.get("areaGrid"));
//				String mapSign = StringUtils.trimToEmpty((String)listObj.get(regionCode));
//				if ("".equals(mapSign) || mapSign.length() == 0)
//					continue;
//				String areaGrid = (String) subBody.get("areaGrid");
//				codeList.add(areaGrid);
//				if (subBody.containsKey(key)) {
//					String value = (String) subBody.get(key);
//					subjectCode.put(areaGrid, value);
//					subjectObj.put(mapSign, subjectCode);
//				}
//			}
//			returnObj.put(key, subjectObj);
//		}
//		return returnObj;
//	}
//
//}
