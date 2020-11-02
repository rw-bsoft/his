package chis.source.gis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class IntegratedService implements Service {
	private static Log logger = LogFactory.getLog(IntegratedService.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(Map<String,Object> jsonReq, Map<String,Object> jsonRes, Context ctx){
		ArrayList rsBody = new ArrayList();
		String code = (String) jsonReq.get("mapSign");
		String l = "select manaUnitId from EHR_AreaGrid where mapSign='" + code
				+ "'";
		String mCode = "";
		if (code.equals("15,0")) {
			mCode = "3301";
		} else {
			try {
				if (ServiceUtil.executeSql(l, ctx).size() > 0) {
					mCode = ServiceUtil.executeSql(l, ctx).get(0).toString();
				} else {
					mCode = "3301";
				}
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to retrieve data.", e);
				jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				return;
			}
		}
		Map subjects = (Map) jsonReq.get("subjects");
		Iterator itSubjects = subjects.keySet().iterator();
		int lastLayer = LayerDic.getManaUnitNextLayerLength(mCode.length());
		while (itSubjects.hasNext()) {
			String kpiCode = (String) itSubjects.next();
			Map<String,Object> kMap = (Map<String,Object>) subjects.get(kpiCode);
			String kpiCodeText = (String) kMap.get("kpicodeText");
			String date[] = ((String)jsonReq.get("date")).split("-");
			String season = "04";
			if ("01".endsWith(date[1]) || "02".endsWith(date[1])
					|| "03".endsWith(date[1])) {
				season = "1";
			} else if ("04".endsWith(date[1]) || "05".endsWith(date[1])
					|| "06".endsWith(date[1])) {
				season = "2";
			} else if ("07".endsWith(date[1]) || "08".endsWith(date[1])
					|| "09".endsWith(date[1])) {
				season = "3";
			} else if ("10".endsWith(date[1]) || "11".endsWith(date[1])
					|| "12".endsWith(date[1])) {
				season = "4";
			}
			String sql = "select substring(manaUnitId,1," + lastLayer
					+ "),str(sum(" + kpiCode
					+ ")) from EHR_ComprehensiveReport where year='" + date[0]
					+ "'and season='" + season
					+ "' and substring(manaUnitId,1," + mCode.length() + ")='"
					+ mCode + "' group by substring(manaUnitId,1," + lastLayer
					+ ")";
			try {
				rsBody.add(ServiceUtil.executeSqlAy(sql, ctx, kpiCode,
						kpiCodeText));
			} catch (PersistentDataOperationException e) {
				logger.error("Failed to retrieve data.", e);
				jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				return;
			}
		}
		String hql = "";
		if (!"".equals(mCode)) {
			hql = "select manaUnitId,mapSign from EHR_AreaGrid where substring(manaUnitId,1,"
					+ mCode.length()
					+ ")='"
					+ mCode
					+ "' and (length(manaUnitId)="
					+ LayerDic.getManaUnitNextLayerLength(mCode.length())
					+ " or length(manaUnitId)=" + mCode.length() + ")";
		}
		Map<String,Object> listObj;
		try {
			listObj = ServiceUtil.executeSqlObj(hql, ctx);
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to retrieve data.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			return;
		}
		jsonRes.put("body", rsBody);
		jsonRes.put("codeList", listObj);
	}
}