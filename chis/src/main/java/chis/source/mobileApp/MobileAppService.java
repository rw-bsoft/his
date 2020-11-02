package chis.source.mobileApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.dictionary.DictionaryItem;
import ctd.mvc.controller.support.DictionaryLoader;
import ctd.schema.DictionaryIndicator;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.mdc.DiabetesVisitModel;
import chis.source.mdc.DiabetesVisitService;
import chis.source.mdc.HypertensionVisitService;
import chis.source.ohr.OldPeopleRecordService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.visitplan.VisitPlanModel;

public class MobileAppService extends AbstractActionService implements
		DAOSupportable {

	private static final Logger logger = LoggerFactory
			.getLogger(MobileAppService.class);

	/**
	 * 返回健康随访计划列表（结合高血压，糖尿病，老年人）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetMdcVisitPlanList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

	}

	/**
	 * 获取某个人最近一年的随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetMdcVisitInYear(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

	}

	/**
	 * 根据visitId获取随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetVisitDataByVisitId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

	}

	/**
	 * 保存随访记录（结合高血压，糖尿病，老年人）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveVisitData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		if (body == null) {
			return;
		}
		String planId_LNR = (String) body.get("planId_LNR");
		String planId_GXY = (String) body.get("planId_GXY");
		String planId_TNB = (String) body.get("planId_TNB");
		String[] planIds = { planId_LNR, planId_GXY, planId_TNB };
		Map<String, Object> schemaIds = new HashMap<String, Object>();
		schemaIds.put(planId_LNR, BSCHISEntryNames.MDC_OldPeopleVisit);
		schemaIds.put(planId_GXY, BSCHISEntryNames.MDC_HypertensionVisit);
		schemaIds.put(planId_TNB, BSCHISEntryNames.MDC_DiabetesVisit);
		Map<String, Object> ops = new HashMap<String, Object>();
		VisitPlanModel vpm = new VisitPlanModel(dao);
		Map<String, Object> planInfo = null;
		Map<String, Object> allSaveData = new HashMap<String, Object>();
		try {
			for (int j = 0; j < planIds.length; j++) {
				String planId = planIds[j];
				if (planId == null || planId.length() == 0
						|| "null".equals(planId)) {
					continue;
				}
				planInfo = vpm.getPlan(planId);
				String schemaId = (String) schemaIds.get(planId);
				ops.put(schemaId, "create");
				if (planInfo.get("visitId") != null
						&& !"".equals(planInfo.get("visitId"))) {
					ops.put(schemaId, "update");
				}
				Schema sc = SchemaController.instance().get(schemaId);
				List<SchemaItem> items = sc.getItems();
				for (SchemaItem it : items) {
					String itemId = it.getId();
					Object value = body.get(itemId);
					if (value != null) {
						planInfo.put(itemId, value);
					}
				}
				allSaveData.put(schemaId, planInfo);
			}
			if (allSaveData.containsKey(BSCHISEntryNames.MDC_OldPeopleVisit)) {
				Map<String, Object> saveData = (Map<String, Object>) allSaveData
						.get(BSCHISEntryNames.MDC_OldPeopleVisit);
				Map<String, Object> visitRecord=new HashMap<String, Object>();
				visitRecord.put("visitRecord", saveData);
				req.put("body", visitRecord);
				req.put("op", ops.get(BSCHISEntryNames.MDC_OldPeopleVisit));
				OldPeopleRecordService oprs = new OldPeopleRecordService();
				Map<String, Object> LNRRes = new HashMap<String, Object>();
				oprs.doSaveOldPeopleVisitRecord(req, LNRRes, dao, ctx);
				res.put("body_LNR", LNRRes);
			}
			if (allSaveData.containsKey(BSCHISEntryNames.MDC_HypertensionVisit)) {
				Map<String, Object> saveData = (Map<String, Object>) allSaveData
						.get(BSCHISEntryNames.MDC_HypertensionVisit);
				req.put("body", saveData);
				req.put("op", ops.get(BSCHISEntryNames.MDC_HypertensionVisit));
				HypertensionVisitService hvs = new HypertensionVisitService();
				Map<String, Object> GXYRes = new HashMap<String, Object>();
				hvs.doSaveHypertensionVisit(req, GXYRes, dao, ctx);
				res.put("body_GXY", GXYRes);
			}
			if (allSaveData.containsKey(BSCHISEntryNames.MDC_DiabetesVisit)) {
				Map<String, Object> saveData = (Map<String, Object>) allSaveData
						.get(BSCHISEntryNames.MDC_DiabetesVisit);
				req.put("body", saveData);
				req.put("op", ops.get(BSCHISEntryNames.MDC_DiabetesVisit));
				DiabetesVisitService dvs = new DiabetesVisitService();
				Map<String, Object> TNBRes = new HashMap<String, Object>();
				dvs.doSaveDiabetesVisit(req, TNBRes, dao, ctx);
				res.put("body_TNB", TNBRes);
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取计划信息失败!", e);
		} catch (ControllerException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取schema失败!", e);
		} catch (Exception e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"保存随访记录失败!", e);
		}

	}

	/**
	 * 返回所有系统的账号信息列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetAllUserList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String sql = "select a.id as id,a.name as name,a.password as password,"
				+ "a.status as status,b.gender as gender,b.birthday as birthday,b.mobile as mobile "
				+ "from User a,SYS_Personnel b where a.id=b.personId";
		try {
			List<Map<String, Object>> list = dao.doQuery(sql, null);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取所有系统的账号信息失败!", e);
		}
	}

	/**
	 * 返回个人账号信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String, Object> doGetUserInfo(String id)
			throws ServiceException {
		Map<String, Object> m = null;
		Context ctx = ContextUtils.getContext();
		String sql = "select a.id as id,a.name as name,a.password as password,"
				+ "a.status as status,b.gender as gender,b.birthday as birthday,b.mobile as mobile "
				+ "from User a,SYS_Personnel b where a.id=b.personId and a.id=:id";
		try {
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			if (ss == null) {
				SessionFactory sf = AppContextHolder.getBean(
						AppContextHolder.DEFAULT_SESSION_FACTORY,
						SessionFactory.class);
				ss = sf.openSession();
				ctx.put(Context.DB_SESSION, ss);
			}
			Query q = ss.createQuery(sql).setResultTransformer(
					Transformers.ALIAS_TO_ENTITY_MAP);
			q.setParameter("id", id);
			m = (Map<String, Object>) q.uniqueResult();
			ss.flush();
		} catch (HibernateException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"获取个人账号信息失败!", e);
		}
		return m;
	}

	/**
	 * 返回特定schema的字典内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doGetSchemaDicList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String[] schemaIds = { BSCHISEntryNames.EHR_HealthRecord,
				BSCHISEntryNames.EHR_PastHistory,
				BSCHISEntryNames.EHR_PersonProblem,
				"platform.reg.schemas.SYS_Personnel",
				"platform.reg.schemas.BASE_User",
				"platform.reg.schemas.BASE_UserRoles",
				BSCHISEntryNames.EHR_LifeCycle,
				BSCHISEntryNames.MDC_OldPeopleVisit,
				BSCHISEntryNames.MDC_HypertensionVisit,
				BSCHISEntryNames.MDC_DiabetesVisit };
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> dics = new HashMap<String, Object>();
		Map<String, Object> dicNames = new HashMap<String, Object>();
		try {
			for (int i = 0; i < schemaIds.length; i++) {
				String schemaId = schemaIds[i];
				Schema sc = SchemaController.instance().get(schemaId);
				List<SchemaItem> items = sc.getItems();
				for (SchemaItem it : items) {
					DictionaryIndicator dici = it.getDic();
					if (dici == null) {
						continue;
					}
					String dicId = dici.getId();
					if (dics.get(dicId) != null) {
						continue;
					}
					if (dicId.indexOf("user") > 0
							|| dicId.indexOf("areaGrid") > 0
							|| dicId.indexOf("Personnel") > 0) {
						continue;
					}
					Dictionary dic = DictionaryController.instance().get(dicId);
					List<DictionaryItem> dicItems = dic.itemsList();
					if (dicItems == null || dicItems.size() == 0) {
						dicItems = dic.getSlice(null, 0, null);
					}
					if (dicItems == null || dicItems.size() == 0) {
						System.out.println(dicId);
						continue;
					}
					Map<String, Object> dicMap = new HashMap<String, Object>();
					for (DictionaryItem dicIt : dicItems) {
						dicMap.put(dicIt.getKey(), dicIt.getText());
					}
					dics.put(dicId, dicMap);
					dicNames.put(it.getAlias(), dicMap);
				}
			}
			body.put("dics", dics);
			body.put("dicNames", dicNames);
			res.put("body", body);
		} catch (ControllerException e) {
			throw new ServiceException(Constants.CODE_DATABASE_ERROR,
					"返回特定schema的字典内容失败!", e);
		}
	}
}
