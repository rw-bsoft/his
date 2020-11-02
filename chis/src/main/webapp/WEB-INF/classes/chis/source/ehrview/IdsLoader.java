/**
 * @(#)IdsLoader.java Created on Mar 16, 2010 7:54:37 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ehrview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.control.ControlRunner;
import chis.source.util.SchemaUtil;
import ctd.dictionary.DictionaryController;
import ctd.domain.DomainUtil;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.service.dao.DBService;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class IdsLoader extends DBService implements Service {

	private static final Log logger = LogFactory.getLog(IdsLoader.class);

	private IdLoader healthRecordIdLoader = null;
	private EmpiLoader empiLoader = null;

	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) {
		Map<String, Object> reqBody = (HashMap<String, Object>) jsonReq
				.get("body");
		try {
			Map<String, Object> ids = new HashMap<String, Object>();
			jsonRes.put("ids", ids);
			Map<String, Object> empiData = loadEmpi(reqBody, jsonRes, ctx);
			jsonRes.put("empiData", empiData);
			Map<String, Object> control = new HashMap<String, Object>();
			jsonRes.put("control", control);
			Map<String, Object> map = loadHealthRecord(reqBody, jsonRes, ctx);
			setParameterType(reqBody, jsonRes, ctx);
			ControlRunner.setData("empiId", empiData.get("empiId"), ctx);
			ControlRunner.setData("EHR_HealthRecord", map, ctx);
			if (AbstractIdLoader.NOT_CREATED.equals(healthRecordIdLoader
					.getStatus(map))) {
				Map<String, Boolean> res = new HashMap<String, Boolean>();
				res.put("update", true);
				control.put(healthRecordIdLoader.getEntryName() + "_control",
						res);
				return;
			}
			setControlInfo(jsonRes, map, healthRecordIdLoader, ctx);

			// ** 获取家庭档案编号
			ids.put("familyId", map.get("familyId"));

			// ** 获取其他档案的相关信息
			loadIds(jsonReq, jsonRes, ctx);

		} catch (Exception e) {
			logger.error("IdsLoader execute failed.", e);
			if (jsonRes.get(RES_CODE) != null) {
				return;
			}
			jsonRes.put(RES_CODE, Constants.CODE_SERVICE_ERROR);
			jsonRes.put(RES_MESSAGE, "IdsLoader 服务失败。");
		}
	}

	private void setParameterType(Map<String, Object> reqBody,
			Map<String, Object> jsonRes, Context ctx) {
		BaseDAO dao = new BaseDAO();
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		try {
			String healthCheckType = scmm
					.getSystemConfigData("healthCheckType");
			String areaGridShowType = scmm
					.getSystemConfigData("areaGridShowType");
			String debilityShowType = scmm
					.getSystemConfigData("debilityShowType");

			String postnatalVisitType = scmm
					.getSystemConfigData("postnatalVisitType");
			String postnatal42dayType = scmm
					.getSystemConfigData("postnatal42dayType");

			jsonRes.put("healthCheckType", healthCheckType);
			jsonRes.put("areaGridShowType", areaGridShowType);

			jsonRes.put("debilityShowType", debilityShowType);
			
			//TODO

			jsonRes.put("postnatalVisitType", postnatalVisitType);
			jsonRes.put("postnatal42dayType", postnatal42dayType);

		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}

	}



	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> loadHisInfo(Map<String, Object> reqBody,
			Map<String, Object> jsonRes, Context ctx) throws Exception {
		String empiId = (String) reqBody.get("empiId");
		String sql = "select a.BRID as BRID,a.MZHM as MZHM,a.BRXZ as BRXZ from MS_BRDA a where empiId=:empiId";
		Session session = (Session) ctx.get(Context.DB_SESSION);
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("empiId", empiId);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		Map<String, Object> l = (Map<String, Object>) query.uniqueResult();
		session.flush();
		if (l != null && l.get("BRXZ") != null) {
			l.put("BRXZ",
					DictionaryController.instance()
							.get("phis.dictionary.patientProperties")
							.getText(l.get("BRXZ") + ""));
		}
		return l;
	}

	/**
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> loadEmpi(Map<String, Object> reqBody,
			Map<String, Object> jsonRes, Context ctx) throws Exception {
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> empiData;
		Map<String, Object> empi = new HashMap<String, Object>();
		try {
			empiData = empiLoader.load(empiId, ctx);
			empiData = SchemaUtil.setDictionaryMessageForList(empiData,
					BSCHISEntryNames.MPI_DemographicInfo);
			CopyOnWriteArraySet<String> adMap = DomainUtil.getActiveDomains();
			boolean phisActive = false;
			if(adMap.contains("phis")){
				phisActive = true;
			}
			if(phisActive){
				// ** 获取基层医疗的相关信息
				Map<String, Object> his = loadHisInfo(reqBody, jsonRes, ctx);
				if (his != null) {
					empiData.putAll(his);
					Map<String, Object> ids = (HashMap<String, Object>) jsonRes
							.get("ids");
					ids.put("brid", his.get("BRID") + "");
				}
			}
			for (Iterator<String> keys = empiData.keySet().iterator(); keys
					.hasNext();) {
				String key = (String) keys.next();
				Object value = empiData.get(key);
				if (value != null) { // ** 去除空的字段，解决ehrView上个人基本信息如果为空会显示null的问题
					empi.put(key, value);
				}
			}
		} catch (Exception e) {
			logger.error("Get empi data failed.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "获取个人基本信息失败！");
			throw e;
		}
		Map<String, Object> ids = (HashMap<String, Object>) jsonRes.get("ids");
		ids.put("empiId", empi.get("empiId"));
		return empi;
	}

	/**
	 * 获取个人健康档案数据
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @param session
	 * @throws Exception
	 */
	private Map<String, Object> loadHealthRecord(Map<String, Object> reqBody,
			Map<String, Object> jsonRes, Context ctx) throws Exception {
		Map<String, Object> map;
		try {
			map = healthRecordIdLoader
					.load((String) reqBody.get("empiId"), ctx);
			//将责任医生放入jsonRes中
			Map empiData=(Map)jsonRes.get("empiData");
			empiData.put("manaDoctorId_text",map.get("manaDoctorId_text")==null?"":map.get("manaDoctorId_text"));
			empiData.put("manaDoctorId", map.get("manaDoctorId"));
			
			//add by2019-01-07 Wangjl
			if(empiData.get("empiId")!=null){
			String sql =" select  e.ysxm as ysxm ,a.phrid phrid,"+
				      " to_char(a.createdate, 'yyyy-mm-dd') createdate,"+
				      " b.organizname createunit,"+
				      " c.personname createuser,"+
				      " case  when length(a.regioncode)>=2 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,2)) else '' end province,"+
				      " case  when length(a.regioncode)>=4 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,4)) else '' end  city,"+
				      " case  when length(a.regioncode)>=6 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,6)) else '' end  district,"+
				      " case  when length(a.regioncode)>=9 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,9)) else '' end  town,"+
				      " case  when length(a.regioncode)>=12 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,12)) else '' end  village,"+
				      "  case  when length(a.regioncode)>=19 then (select regionname from ehr_areagrid where regioncode=substr(a.regioncode,0,19)) else '' end  village2"+
				      "  from ehr_healthrecord a JOIN  sys_organization b ON substr(a.createunit, 0, 9) = b.organizcode"+
				      "  JOIN sys_personnel c ON a.createuser = c.personid"+
				      "  JOIN ehr_areagrid d  ON  a.regioncode = d.regioncode"+
				      "  LEFT JOIN  (select distinct t.favoreeempiid,to_char(wm_concat(distinct(select a.personname from sys_personnel a  where a.personid = t.createuser))) ysxm  from scm_signcontractrecord t group by t.favoreeempiid ) E ON e.favoreeempiid=a.empiId"+
				      " where a.empiId =:empiId"
				     ;
			Session session = (Session) ctx.get(Context.DB_SESSION);
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter("empiId", empiData.get("empiId"));
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			Map<String, Object> l = (Map<String, Object>) query.uniqueResult();
			session.flush();
			if (l != null) {
				empiData.put("createunit", l.get("CREATEUNIT")==null?"":l.get("CREATEUNIT").toString());
				empiData.put("createuser",l.get("CREATEUSER")==null?"":l.get("CREATEUSER").toString());
				empiData.put("province",l.get("PROVINCE")==null?"":l.get("PROVINCE").toString());
				empiData.put("city",l.get("CITY")==null?"":l.get("CITY").toString());
				empiData.put("district",l.get("DISTRICT")==null?"":l.get("DISTRICT").toString());
				empiData.put("town",l.get("TOWN")==null?"":l.get("TOWN").toString());
				empiData.put("village",l.get("VILLAGE")==null?"":l.get("VILLAGE").toString());
				empiData.put("village2",l.get("VILLAGE2")==null?"":l.get("VILLAGE2").toString());
				empiData.put("manaunitid",l.get("MANAUNITID")==null?"":l.get("MANAUNITID").toString());
				empiData.put("createdate",l.get("CREATEDATE")==null?"":l.get("CREATEDATE").toString());
				empiData.put("phrid",l.get("PHRID")==null?"":l.get("PHRID").toString());
				empiData.put("ysxm",l.get("YSXM")==null?"":l.get("YSXM").toString());
		 	}
	}
			//如果存在家庭档案编号则查询家庭地址
			//yx注销下面代码
//			if(map.get("familyId")!=null)
//			{
//				String sql = "select familyAddr from ehr_familyrecord where familyId=:familyId";
//				Session session = (Session) ctx.get(Context.DB_SESSION);
//				SQLQuery query = session.createSQLQuery(sql);
//				query.setParameter("familyId", map.get("familyId"));
//				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//				Map<String, Object> l = (Map<String, Object>) query.uniqueResult();
//				session.flush();
//				if (l != null && l.get("FAMILYADDR") != null) {
//					empiData.put("address",l.get("FAMILYADDR"));
//
//				}
//			}
			
		} catch (ServiceException e) {
			logger.error("Load id and status of health record failed.", e);
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "获取健康档案的档案号和状态失败！");
			throw e;
		}
		getIdsData(jsonRes, map, healthRecordIdLoader, ctx);
		return map;
	}

	@SuppressWarnings("unchecked")
	private void loadIds(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) throws Exception {
		ApplicationContext ac = AppContextHolder.get();
		Map<String, Object> reqBody = (Map<String, Object>) jsonReq.get("body");
		if (reqBody.get("idsLoader") == null) {
			return;
		}
		String[] loaders = ((String) reqBody.get("idsLoader")).split(",");
		for (int i = 0; i < loaders.length; i++) {
			IdLoader idLoader = (IdLoader) ac.getBean(loaders[i]);
			String loadBy = (String) reqBody.get("empiId");
			String loadType = (String) reqBody.get("recordType");
			String loadByPkey = (String) reqBody.get("Pkey");
			if (reqBody.containsKey(idLoader.getLoadBy())) {
				loadBy = (String) reqBody.get(idLoader.getLoadBy());
				ctx.put(idLoader.getLoadBy(), loadBy);
			}
			if (reqBody.containsKey(idLoader.getLoadType())) {
				loadType = (String) reqBody.get(idLoader.getLoadType());
				ctx.put(idLoader.getLoadType(), loadType);
			}
			if (reqBody.containsKey(idLoader.getLoadPkey())) {
				loadByPkey = (String) reqBody.get(idLoader.getLoadPkey());
				ctx.put(idLoader.getLoadPkey(), loadByPkey);
			}
			Map<String, Object> data;
			try {
				if (StringUtils.isNotBlank(loadType)) {
					data = idLoader.load(loadBy, loadType, ctx);
				} else if (StringUtils.isNotBlank(loadByPkey)) {
					data = idLoader.loadByPkey(loadByPkey, ctx);
				} else {
					data = idLoader.load(loadBy, ctx);
				}
			} catch (ServiceException e) {
				logger.error("Load id and status of " + idLoader.getEntryName()
						+ " failed.", e);
				jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				jsonRes.put(RES_MESSAGE, "获取子档的档案号和状态失败！");
				throw e;
			}
			getIdsData(jsonRes, data, idLoader, ctx);
			setControlInfo(jsonRes, data, idLoader, ctx);
		}

	}

	/**
	 * 获取ids相关数据
	 * 
	 * @param jsonRes
	 * @param data
	 * @param idLoader
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	private void getIdsData(Map<String, Object> jsonRes,
			Map<String, Object> data, IdLoader idLoader, Context ctx)
			throws ServiceException {
		// ** ids
		Map<String, Object> ids = (HashMap<String, Object>) jsonRes.get("ids");
		ids.put(idLoader.getIdName(), idLoader.getId(data));
		if (idLoader.hasStatus()) {
			ids.put(idLoader.getIdName() + ".status", data.get(IdLoader.STATUS));
		}
		if (idLoader.hasCloseFlag()) {
			ids.put(idLoader.getIdName() + ".closeFlag",
					data.get(IdLoader.CLOSEFLAG));
		}
	}
	

	/***
	 * 用于控制前台按钮权限
	 * 
	 * @param jsonRes
	 * @param data
	 * @param idLoader
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	private void setControlInfo(Map<String, Object> jsonRes,
			Map<String, Object> data, IdLoader idLoader, Context ctx)
			throws ServiceException {
		// ** control
		Map<String, Object> control = (HashMap<String, Object>) jsonRes
				.get("control");
		Map<String, Boolean> controlData = idLoader.getControlData(data, ctx);
		if (controlData != null && controlData.size() > 0) {
			control.put(idLoader.getEntryName() + "_control", controlData);
		}
	}

	/**
	 * @param ctx
	 * @return
	 */
	protected static Session getSession(Context ctx) {
		WebApplicationContext wac = (WebApplicationContext) ctx
				.get("_applicationContext");
		SessionFactory sessionFactory = (SessionFactory) wac
				.getBean("mySessionFactory");
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return session;
		} catch (Exception e) {
			if (session != null && session.isOpen()) {
				session.close();
			}
			return null;
		}
	}

	public IdLoader getHealthRecordIdLoader() {
		return healthRecordIdLoader;
	}

	public void setHealthRecordIdLoader(IdLoader healthRecordIdLoader) {
		this.healthRecordIdLoader = healthRecordIdLoader;
	}

	public EmpiLoader getEmpiLoader() {
		return empiLoader;
	}

	public void setEmpiLoader(EmpiLoader empiLoader) {
		this.empiLoader = empiLoader;
	}
}
