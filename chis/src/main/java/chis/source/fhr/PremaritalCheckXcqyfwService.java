package chis.source.fhr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.phr.PastHistoryModel;
import chis.source.pub.PublicModel;
import chis.source.service.AbstractActionService;
import chis.source.service.AbstractService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;


import ctd.account.user.User;
import ctd.schema.Schema;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;

public class PremaritalCheckXcqyfwService extends AbstractActionService implements
DAOSupportable 
{

	private static final Log logger = LogFactory.getLog(PremaritalCheckXcqyfwService.class);
	/**
	 * 
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws Exception 
	 */
	protected void doLoaddata(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws Exception {
		String phrId=(String) req.get("phrId");
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PER_XCQYFW, phrId);
			if(rsMap!=null){
			rsMap = SchemaUtil.setDictionaryMessageForForm(rsMap,
					PER_XCQYFW);
			rsMap.put("op", "update");
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new Exception("签约信息失败！");
		}
		res.put("body", rsMap);
		
	}
	
	protected void doSavePremaritalCheckFemale(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws JSONException, ServiceException {
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try 
		{
			dao.doSave(op,PER_XCQYFW,body,true);
		} 
		catch (Exception e) 
		{
			logger.error("Save PremaritalCheckFemale failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "");
			throw new ServiceException(e);
		}
	}
	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException 
	 * @throws PersistentDataOperationException 
	 */
	protected void doListPremaritalCheckFemale(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws JSONException, ServiceException, PersistentDataOperationException {
		
		String schemaId=(String)req.get("schema");
		List queryCnd =(List) req.get("cnd");
		Map<String, Object> resultMap = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List) req.get("cnd");
		}
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		String year = new Date().getYear() + "";
		if (req.containsKey("year")) {
			year = req.get("year") + "";
		}
		String checkType = "1";
		if (req.containsKey("checkType")) {
			checkType = (String) req.get("checkType");
		}
		PublicModel hrModel = new PublicModel(dao);
		resultMap = hrModel.queryRecordList(schemaId, queryCnd, queryCndsType,
				null, pageSize, pageNo, year, checkType);
		List<Map<String, Object>> resBody = (List<Map<String, Object>>) resultMap
				.get("body");
		
//		List<Map<String, Object>> resBody=dao.doList(queryCnd, "", schemaId);
//		List<String> empiIdList = new ArrayList<String>();
//		for (int i = 0; i < resBody.size(); i++) {
//			Map<String, Object> one= resBody.get(i);
//			String empiId = (String)one.get("empiId");
//			empiIdList.add(empiId);
//		}
//		if (empiIdList.size() == 0) {
//			return;
//		}
		res.put("body", resBody);
//		Session session = null ;
//		session = (Session)ctx.get(Context.DB_SESSION);
//		List<Map<String, Object>> list = checkHasVisitUndo(empiIdList,session);
//		for (int i = 0; i < list.size(); i++) {
//			Map<String, Object> l=list.get(i);
//			String empiId = (String)l.get("empiId");
//			for (Iterator<Map<String, Object>> it = list.iterator(); it
//					.hasNext();) {
//				Map<String, Object> map = it.next();
//				if (empiId.equals(map.get("empiId"))) {
//					if ((Long) map.get("count") > 0) {
//						l.put("needDoVisit", true);
//					}
//					it.remove();
//					break;
//				}
//			}
//		}
	}
	public static List<Map<String, Object>> checkHasVisitUndo(
			List<String> empiId,Session session) throws HibernateException {
		String hql = new StringBuffer(
				"select empiId as empiId, count(*) as count from ")
				.append(PUB_VisitPlan)
				.append(" where empiId in (:empiId) and businessType=:businessType and")
				.append(" planStatus=:planStatus and beginDate<=:beginDate ")
				.append(" group by empiId").toString();
		Query query = session.createQuery(hql).setResultTransformer(
				Transformers.ALIAS_TO_ENTITY_MAP);
		query.setParameterList("empiId", empiId);
		query.setString("businessType", "1");
		query.setString("planStatus",
				String.valueOf(Constants.CODE_STATUS_NORMAL));
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.set(Calendar.DAY_OF_YEAR, day + 10);
		Date cc = calendar.getTime();
		query.setDate("beginDate", cc);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> l = (List<Map<String, Object>>) query.list();
		return l;
	}
	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 */
	protected void doGetPremaritalCheckFemale(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws JSONException 
	{
		String hql = "select count(*) from PER_XCQYFW where phrId =:phrId";
		Session session=dao.getSession();
		Query query = session.createSQLQuery(hql);
		query.setParameter("phrId", req.get("phrId"));
		List<?> l = query.list();
		res.put("count", l.get(0));
	}
	


//	@Override
//	public List<String> getTransactedActions() 
//	{
//		List<String> list = new ArrayList<String>();
//		list.add("savePremaritalCheckFemale");
//		list.add("getPremaritalCheckFemale");
//		return list;
//	}
}
