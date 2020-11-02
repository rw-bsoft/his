package chis.source.gis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import com.alibaba.fastjson.JSONException;

import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpressionProcessor;

public class HistoryService extends AbstractActionService implements
		DAOSupportable {
	private static Log logger = LogFactory.getLog(HistoryService.class);

	private static Context myCtx = null;

	@Override
	public List<String> getTransactedActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getNoDBActions() {
		List<String> list = super.getNoDBActions();
		list.add("saveHistory");
		return list;
	}

	/**
	 * 添加历史记录
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings({ "null", "unchecked" })
	protected void executeSaveHistory(HashMap<String, Object> jsonReq,
			HashMap<String, Object> jsonRes, Schema sc, Context ctx) {
		Session session = null;
		try {
			StringBuilder hql = new StringBuilder(
					"insert into GIS_History values(:eventId,:type,:description, :happenedPlace, date(:happenedDate), :addedPerson, :addedDate,:mapSign) ");
			Query query = session.createSQLQuery(hql.toString());
			String uid = ((HttpSession) ctx.get(Context.WEB_SESSION))
					.getAttribute("uid").toString();
			HashMap<String, Object> obj = (HashMap<String, Object>) jsonReq
					.get("body");
			query.setParameter("eventId",
					ExpressionProcessor.instance().run("['idGen','GIS_History']"));

			// query.setParameter("eventId", KeyManager.getKeyByName(
			// "GIS_History", ctx));
			query.setParameter("type",
					StringUtils.trimToEmpty((String) obj.get("type")));
			query.setParameter("description",
					StringUtils.trimToEmpty((String) obj.get("description")));
			query.setParameter("happenedPlace",
					StringUtils.trimToEmpty((String) obj.get("happenedPlace")));
			query.setParameter("happenedDate",
					StringUtils.trimToEmpty((String) obj.get("happenedDate")));
			query.setParameter("addedPerson", uid);
			query.setParameter("mapSign",
					StringUtils.trimToEmpty((String) obj.get("mapSign")));
			query.setParameter("addedDate", new Date());
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("Failed to get gis save event.", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/**
	 * 获取历史事件
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	protected void executeGetHistory(HashMap<String, Object> jsonReq,
			HashMap<String, Object> jsonRes, Session session, Context ctx)
			throws ServiceException {
		HashMap<String, Object> obj = (HashMap<String, Object>) jsonReq
				.get("body");
		String startDate = StringUtils.trimToEmpty((String) obj
				.get("queryStartDate"));
		String endDate = StringUtils.trimToEmpty((String) obj
				.get("queryEndDate"));
		String type = StringUtils.trimToEmpty((String) obj.get("queryType"));
		String schemaId = StringUtils.trimToEmpty((String) jsonReq
				.get("schemaId"));
		StringBuilder hql = new StringBuilder("from GIS_History");
		hql.append(" where happenedDate>=date(:startDate) and happenedDate<=date(:endDate) and type=:queryType order by happenedDate");
		Query query = session.createQuery(hql.toString());
		query.setString("startDate", startDate);
		query.setString("endDate", endDate);
		query.setString("queryType", type);
		Schema sc = null;
		if (schemaId.length() > 0)
			try {
				sc = SchemaController.instance().get(schemaId);
				List<?> list = query.list();
				String history = "";
				String temp = "";
				if (list.size() > 0) {
					List jsonArr = new ArrayList();
					for (int i = 0; i < list.size(); i++) {
						HashMap hash = (HashMap) list.get(i);
						HashMap<String, Object> jsonchild = new HashMap<String, Object>();
						Iterator keys = hash.keySet().iterator();
						for (; keys.hasNext();) {
							String key = (String) keys.next();
							jsonchild.put(key, hash.get(key));
						}
						jsonArr.add(jsonchild);
					}
					jsonRes.put("body", jsonArr);
					jsonRes.put("schema", sc.getItems());
				}
			} catch (Exception e) {
				logger.error("Failed to get gis event history.", e);
			}
	}

	// public void execute(HashMap<String,Object> jsonReq,
	// HashMap<String,Object> jsonRes, Context ctx)
	// throws JSONException {
	// myCtx = ctx;
	// HashMap<String, Object> query=jsonReq.getHashMap();
	// String description = jsonReq.optString("description");
	// String happenedPlace = jsonReq.optString("happenedPlace");
	// String happenedTime=jsonReq.optString("happenedTime");
	// String hql = "update GIS_History set description='"
	// +description
	// +"',happenedPlace='"
	// + happenedPlace
	// + "',happenedTime='"
	// + happenedTime
	// + "'";
	// WebApplicationContext wac = (WebApplicationContext) ctx
	// .get("_applicationContext");
	// SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class);
	// Session session = sf.openSession();
	//
	//
	// HashMap<String,Object> listObj = ServiceUtil.executeSqlObj(hql, ctx);
	// JSONArray
	// buildSc=BuildDefine.getIntegratedCfg(jsonReq,listObj,myCtx,query);
	// jsonRes.put("body", buildSc);
	// jsonRes.put("codeList", listObj);
	// System.out.println(jsonRes);
	// }
}