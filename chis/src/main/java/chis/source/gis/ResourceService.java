package chis.source.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;

public class ResourceService extends AbstractActionService implements
		DAOSupportable {

	private static Log logger = LogFactory.getLog(ResourceService.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(HashMap<String, Object> jsonReq,
			HashMap<String, Object> jsonRes, Context ctx) {
		try {
			HashMap<String, Object> body = (HashMap<String, Object>) jsonReq
					.get("body");
			ArrayList arrayData = (ArrayList) body.get("datas");
			for (int i = 0; i < arrayData.size(); i++) {
				HashMap<String, Object> b = (HashMap<String, Object>) arrayData
						.get(i);
				// String key = KeyManager.getKeyByName("PUB_Resource", ctx);
				String key = (String) ExpRunner.run("['idGen','PUB_Resource']",
						ctx);
				if (b.containsKey("areaGrid")) {
					String sql = "from PUB_Resource where checkDate='"
							+ b.get("year") + " and season=" + b.get("season")
							+ "' and areaGrid='" + b.get("areaGrid")
							+ "' and KPICode='" + b.get("key") + "'";
					List l = ServiceUtil.executeSql(sql, ctx);
					HashMap<String, Object> map = new HashMap<String, Object>();
					String sc = (String) jsonReq.get("schema");
					Schema s = SchemaController.instance().getSchema(sc);
					map.put("id", key);
					map.put("areaGrid", b.get("areaGrid"));
					map.put("checkDate", b.get("year"));
					map.put("season", b.get("season"));
					map.put("KPICode", b.get("key"));
					map.put("KPI", b.get("text"));
					if (l.size() == 0) {
						BaseDAO dao = new BaseDAO();
						dao.doSave("create",  jsonReq.get("schema").toString(), map, false);
					} else {
						String k = "";
						for (int j = 0; j < l.size(); j++) {
							HashMap o = (HashMap) l.get(j);
							if (o.containsKey("id")) {
								k = (String) o.get("id");
							}
						}
						map.put("id", k);
						BaseDAO dao = new BaseDAO();
						dao.doSave("update",  jsonReq.get("schema").toString(), map, false);
					}
				} else {
					String sql = "from PUB_Resource where checkDate="
							+ b.get("year") + " and season=" + b.get("season")
							+ " and manageUnit=" + b.get("manageUnit")
							+ " and KPICode='" + b.get("key") + "'";
					List l = ServiceUtil.executeSql(sql, ctx);
					HashMap<String, Object> map = new HashMap<String, Object>();
					String sc = (String) jsonReq.get("schema");
					Schema s = SchemaController.instance().getSchema(sc);
					map.put("id", key);
					map.put("manageUnit", b.get("manageUnit"));
					map.put("checkDate", b.get("year"));
					map.put("season", b.get("season"));
					map.put("KPICode", b.get("key"));
					map.put("KPI", b.get("text"));
					if (l.size() == 0) {
						BaseDAO dao = new BaseDAO();
						dao.doSave("create",  jsonReq.get("schema").toString(), map, false);
					} else {
						String k = "";
						for (int j = 0; j < l.size(); j++) {
							HashMap o = (HashMap) l.get(j);
							if (o.containsKey("id")) {
								k = (String) o.get("id");
							}
						}
						map.put("id", k);
						BaseDAO dao = new BaseDAO();
						dao.doSave("update",  jsonReq.get("schema").toString(), map, false);
					}
				}
			}
		} catch (Exception e) {
			logger.error("save children record error .", e);
			jsonRes.put(Service.RES_CODE, 500);
			jsonRes.put(Service.RES_MESSAGE, "数据保存失败!");
		}
	}

	@Override
	public List<String> getTransactedActions() {
		// TODO Auto-generated method stub
		return null;
	}
}
