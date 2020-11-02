package chis.source.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class AssociateService implements Service {

	private static Log logger = LogFactory.getLog(AssociateService.class);

	private static Context myCtx = null;

	private ArrayList returnJson = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		myCtx = ctx;
		jsonReq = (Map<String, Object>) jsonReq.get("body");
		if (returnJson == null) {
			String hql = "from EHR_AreaGridChild where substring(mapsign,1,2)<>'1,' and substring(mapsign,1,3)<>'12,' and mapsign is not null";
			try {
				returnJson = (ArrayList) getAssociate(hql, "mapSign", "regionName");
			} catch (PersistentDataOperationException e) {
				logger.error("Data retrieve failed.", e);
				jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				return;
			}
		}
		jsonRes.put("body", returnJson);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAssociate(String hql, String key, String field)
			throws PersistentDataOperationException {
		ArrayList associateArr = new ArrayList();
		Map<String,Object> associateObj;
		List assList = ServiceUtil.executeSql(hql, myCtx);
		for (int i = 0; i < assList.size(); i++) {
			Object object = (Object) assList.get(i);
			Map assMap = (Map) object;
			associateObj = new HashMap<String,Object>();
			associateObj.put(key, (String) assMap.get(key));
			associateObj.put(field, (String) assMap.get(field));
			associateArr.add(associateObj);
		}
		return associateArr;
	}
}
