/**
 * @(#)HypertensionModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.HypertensionRiskStatus;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.log.VindicateLogService;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionRiskModel extends HypertensionModel {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionRiskModel.class);
	public HypertensionRiskModel(BaseDAO dao) {
		super(dao);
	}

	public void createHypertensionRiskVisitPlan(Map<String, Object> body)
			throws ValidateException, PersistentDataOperationException {
		Map<String, Object> plan = new HashMap<String, Object>();
		plan.put("recordId", body.get("phrId"));
		plan.put("empiId", body.get("empiId"));
		plan.put("beginDate", body.get("beginDate"));
		plan.put("endDate", body.get("endDate"));
		plan.put("planStatus", PlanStatus.NEED_VISIT);
		plan.put("planDate", body.get("planDate"));
		plan.put("sn", body.get("sn"));
		plan.put("businessType", BusinessType.HypertensionRisk);
		dao.doInsert(PUB_VisitPlan, plan, false);
	}

	public void writeOffHypertensionRiskVisitPlan(Map<String, Object> body)
			throws ServiceException {
		try {
			String[] businessTypes = { BusinessType.HypertensionRisk };
			this.logOutVisitPlan("empiId", (String) body.get("empiId"),
					businessTypes);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void logOutVisitPlan(String whereField, String whereValue,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyDate=:lastModifyDate")
				.append(" where 1=1 ").append(" and ").append(whereField)
				.append(" = :whereValue");
		Map<String, Object> map = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				map.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		map.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		map.put("lastModifyDate", new Date());
		map.put("planStatus", PlanStatus.WRITEOFF);
		map.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销计划信息失败!", e);
		}
	}

	public void removeHypertensionRecordWorkList(String empiId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer(" delete from ").append(
				"PUB_WorkList").append(
				" where  empiId=:empiId and workType=:workType");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("workType", WorkType.MDC_HYPERTENSIONRECORD);
		dao.doUpdate(hql.toString(), parameters);
	}

	public List<Map<String, Object>> getHypertensionRiskByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append("MDC_HypertensionRisk")
				.append(" where empiId =:empiId and (status =:status1 or status=:status2 )")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status1", "0");
		parameters.put("status2", "1");
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高血压高危核实记录失败");
		}
		return rsList;
	}

	public void updateFirstAssessmentDate(Map<String, Object> m)
			throws PersistentDataOperationException,
			ModelDataOperationException {
		String hql = new StringBuffer(" update ")
				.append(MDC_HypertensionRisk)
				.append(" set firstAssessmentDate =:firstAssessmentDate where empiId =:empiId and status=:status")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", m.get("empiId"));
		parameters.put("firstAssessmentDate",
				BSCHISUtil.toDate((String) m.get("estimateDate")));
		parameters.put("status", HypertensionRiskStatus.CONFIRM);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病高危核实记录失败");
		}
	}

	/**
	 * 注销高血压高危档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutHypertensionRisk(String empiId,
			String cancellationReason, String deadReason, String deadDate)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_HypertensionRisk")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" closeUser = :closeUser, ")
				.append(" closeDate = :closeDate, ")
				.append(" closeUnit = :closeUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason, ")
				.append(" deadDate = :deadDate ")
				.append(" where empiId = :empiId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("closeUser", userId);
		parameters.put("closeDate", new Date());
		parameters.put("closeUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("deadDate", BSCHISUtil.toDate(deadDate));
		parameters.put("deadReason", deadReason);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销高血压高危档案失败！", e);
		}
	}

	/**
	 * 恢复高血压高危档案
	 * 
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void revertHypertensionRisk(String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_HypertensionRisk")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" closeUser = :closeUser, ")
				.append(" closeDate = :closeDate, ")
				.append(" closeUnit = :closeUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason, ")
				.append(" deadDate = :deadDate ")
				.append(" where empiId = :empiId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("closeUser", null);
		parameters.put("closeDate", null);
		parameters.put("closeUnit", null);
		parameters.put("cancellationReason", null);
		parameters.put("deadDate", null);
		parameters.put("deadReason", null);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复高血压高危档案失败！", e);
		}

	}
	
	/**
	 * 档案注销恢复管理
	 * 删除注销标志
	 * @param empiId
	 * @throws ModelDataOperationException
	 */
	public void revertEhrrecordinfo(String empiId)
			throws ModelDataOperationException {
		//String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("EHR_RecordInfo")
				.append(" set LOGOUT = :LOGOUT ")
				.append(" where empiId = :empiId");

		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("LOGOUT", 0);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除注销标志失败！", e);
		}

	}
	
	
	
	
	
	/*public void revertEhrrecordinfo(BaseDAO dao,String empiId)
			throws ServiceException{
		//String userId = UserRoleToken.getCurrent().getUserId();
		try {
		Map param=new HashMap();
		param.put("LOGOUT", 0);
		param.put("empiId", empiId);
		
		StringBuffer hql = new StringBuffer("update ")
				.append("EHR_RECORDINFO")
				.append(" set LOGOUT=:LOGOUT ")
				.append(" where empiId=:empiId ");
				

		//Map<String, Object> parameters = new HashMap<String, Object>();
		
		//parameters.put("empiId", empiId);
		
			dao.doUpdate("update EHR_RECORDINFO set LOGOUT=:LOGOUT where empiId=:empiId", param);
		} catch (Exception e) {
			logger.error("saveRecordInfo failed.", e);
		} finally {
		}

	}*/
	

	public Map<String, Object> listHypertensionRiskVistPlan(
			Map<String, Object> req) throws ModelDataOperationException {
		List<?> queryCnd = null;
		if (req.get("cnd") instanceof List) {
			queryCnd = (List<?>) req.get("cnd");
		} else if (req.get("cnd") instanceof String) {
			try {
				queryCnd = CNDHelper.toListCnd((String) req.get("cnd"));
			} catch (ExpException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
			}
		}
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController
					.instance()
					.get("chis.application.hy.schemas.MDC_HypertensionRiskVisitPlan");
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.isVirtual()) {
				continue;
			}
			if (si.hasProperty("refAlias")) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" PUB_VisitPlan a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" left join MDC_HypertensionRiskVisit d on a.empiId=d.empiId and a.visitid=d.visitid");
		StringBuffer countSQL = new StringBuffer(
				"select count(a.planId) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where);
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> thrvpcList = null;
		try {
			thrvpcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询高血压高危随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (thrvpcList != null && thrvpcList.size() > 0) {
			Map<String, Object> trMap = thrvpcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select ").append(sfBuffer.substring(1)).append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where);
			}
			sql.append(" order by a.planDate ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询高血压高危随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.isVirtual()) {
							continue;
						}
						if (si.hasProperty("refAlias")) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					if (tpMap.get("birthday") != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(
								(Date) tpMap.get("birthday"), new Date()));
					}
					tpList.add(tpMap);
				}
				res.put("body",
						SchemaUtil
								.setDictionaryMessageForList(tpList,
										"chis.application.hy.schemas.MDC_HypertensionRiskVisitPlan"));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}

	public void saveHealthTeach(Map<String, Object> body,
			Map<String, Object> rsMap, VindicateLogService vLogService)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String wayId = (String) body.get("visitId");
		if (wayId == null) {
			wayId = (String) rsMap.get("visitId");
		}
		Map<String, Object> JKCF = (Map<String, Object>) body.get("JKCF");
		body.remove(JKCF);
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> reBody = new HashMap<String, Object>();
		try {
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
					wayId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay",
					"s", "07");
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_GXYGWSF);
			for (Iterator<String> it = JKCF.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
				boolean isUpdate = false;
				for (Map<String, Object> map : list) {
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						m.put("id", map.get("id"));
						isUpdate = true;
						break;
					}
				}
				Map<String, Object> mBody = new HashMap<String, Object>();
				mBody.put("empiId", body.get("empiId"));
				mBody.put("phrId", body.get("phrId"));
				mBody.put("recordId", m.get("recordId"));
				mBody.put("wayId", wayId);
				mBody.put("recipeName", m.get("recipeName"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", body.get("visitUnit"));
				mBody.put("guideDate", body.get("visitDate"));
				mBody.put("guideUser", body.get("visitDoctor"));
				mBody.put("guideWay", "07");
				mBody.put("lastModifyUnit", manageUnit);
				mBody.put("lastModifyDate", new Date());
				mBody.put("lastModifyUser", user.getUserId() + "");
				if (isUpdate == true) {
					mBody.put("id", m.get("id"));
					updateList.add(mBody);
				} else {
					mBody.put("inputUnit", manageUnit);
					mBody.put("inputDate", new Date());
					mBody.put("inputUser", user.getUserId() + "");
					createList.add(mBody);
				}
			}
			for (Map<String, Object> map : list) {
				boolean isRemove = true;
				for (Iterator<String> it = JKCF.keySet().iterator(); it
						.hasNext();) {
					String key = (String) it.next();
					Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						isRemove = false;
						break;
					}
				}
				if (isRemove == true) {
					removeList.add(map);
				}
			}
			list.clear();
			for (Map<String, Object> map : createList) {
				Map<String, Object> result = dao.doSave("create",
						HER_HealthRecipeRecord_GXYGWSF, map, true);
				map.put("childId", result.get("id"));
				Map<String, Object> result2 = dao.doSave("create",
						HER_HealthRecipeRecord, map, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYGWSF,
						"create", result.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "create",
						result2.get("id") + "", dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_GXYGWSF, map, true);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='07'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				result2.putAll(map);
				result2.put("id", zjId);
				dao.doSave("update", HER_HealthRecipeRecord, result2, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYGWSF,
						"update", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "update",
						zjId, dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_GXYGWSF);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='07'")
						.toString();
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("childId", map.get("id"));
				List<Map<String, Object>> li = dao.doQuery(hql, parameters);
				if (li == null || li.size() == 0) {
					continue;
				}
				Map<String, Object> result2 = li.get(0);
				String zjId = result2.get("id") + "";
				dao.doRemove(zjId, HER_HealthRecipeRecord);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_GXYGWSF,
						"delete", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "delete",
						zjId, dao);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_GXYGWSF));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压高危健康教育记录失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高血压高危健康教育记录失败！", e);
		} catch (ServiceException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危健康教育日志记录失败！", e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Object> loadHypertensionRiskVisit(String wayId)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "07");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_GXYGWSF);
			if (list != null && list.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_GXYGWSF));
			}
			Map<String, Object> map = dao.doLoad(MDC_HypertensionRiskVisit,
					wayId);
			if (map != null && map.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(map,
						MDC_HypertensionRiskVisit));
				String sql = "select status as status from "
						+ MDC_HypertensionRisk + " where empiId=:empiId";
				Map<String, Object> para = new HashMap<String, Object>();
				para.put("empiId", map.get("empiId"));
				Map<String, Object> m = dao.doLoad(sql, para);
				result.putAll(m);
			}
			result.put("JKCFRecords", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "加载随访中医辨体信息失败！", e);
		}
		return result;
	}

	/**
	 * 插入高血压高危记录
	 * 
	 * @param empiId
	 * @param phrId
	 * @param constriction收缩压
	 *            (mmHg)
	 * @param diastolic舒张压
	 *            (mmHg)
	 * @param dataSource数据来源
	 */
	public void insertHypertensionRisk(String empiId, String phrId,
			Integer constriction, Integer diastolic, String dataSource) {
		if (constriction == null && diastolic == null) {
			return;
		}
		if (!"7".equals(dataSource)
				&& (constriction != null && (constriction < 120 || constriction > 139))
				&& (diastolic != null && (diastolic < 80 || diastolic > 89))) {
			return;
		}
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		boolean hasHypertensionRecord = false;
		boolean hasHypertensionRisk = false;
		try {
			List<Map<String, Object>> list1 = dao.doQuery(cnd, "a.empiId",
					MDC_HypertensionRecord);
			List<Map<String, Object>> list2 = dao.doQuery(cnd, "a.empiId",
					MDC_HypertensionRisk);
			if (list1 != null && list1.size() > 0) {
				hasHypertensionRecord = true;
			}
			if (list2 != null && list2.size() > 0) {
				hasHypertensionRisk = true;
			}
			if (phrId == null || "".equals(phrId)) {
				List<Map<String, Object>> list3 = dao.doQuery(cnd, "a.empiId",
						EHR_HealthRecord);
				if (list3 != null && list3.size() > 0) {
					phrId = (String) list3.get(0).get("phrId");
				}
			}
			if (phrId == null || "".equals(phrId)) {
				return;
			}
			Map<String, Object> empiMap = dao.doLoad(MPI_DemographicInfo,
					empiId);
			if (!hasHypertensionRecord && !hasHypertensionRisk) {
				UserRoleToken urt = UserRoleToken.getCurrent();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("empiId", empiId);
				map.put("phrId", phrId);
				map.put("dataSource", dataSource);
				map.put("age", BSCHISUtil.calculateAge(
						(Date) empiMap.get("birthday"), new Date()));
				map.put("constriction", constriction);
				map.put("diastolic", diastolic);
				map.put("registerDate", new Date());
				map.put("registerUser", urt.getUserId());
				map.put("registerUnit", urt.getManageUnitId());
				map.put("statusCase", "0");
				if ("7".equals(dataSource)) {
					map.put("statusCase", "1");
				}
				map.put("effectCase", "1");
				map.put("createFlag", "0");
				map.put("status", "0");
				map.put("lastModifyDate", new Date());
				map.put("lastModifyUser", urt.getUserId());
				map.put("lastModifyUnit", urt.getManageUnitId());
				dao.doInsert(MDC_HypertensionRisk, map, false);
			} else if (!hasHypertensionRecord) {
				if (list2.get(0).get("effectCase") != null
						&& "3".equals(list2.get(0).get("effectCase"))) {
					String sql = "update MDC_HypertensionRisk set effectCase='1'," +
							"stopDate=null,effect=null,statusCase='1'"
							+ " where empiId=:empiId";
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("empiId", empiId);
					dao.doUpdate(sql, parameters);
					Application app = ApplicationUtil
							.getApplication(Constants.UTIL_APP_ID);
					String planTypeCode = (String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_planTypeCode");
					Map<String, Object> planType = dao.doLoad(PUB_PlanType,
							planTypeCode);
					if (planType == null) {
						throw new ModelDataOperationException(
								Constants.CODE_RECORD_NOT_FOUND,
								"高血压高危人群随访参数未设置");
					}
					Map<String, Object> body = new HashMap<String, Object>();
					body.put("empiId", empiId);
					body.put("recordId", phrId);
					body.put("phrId", phrId);
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					body.put("planDate", c.getTime());
					c.add(Calendar.DAY_OF_YEAR, -Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
					body.put("beginDate", c.getTime());
					c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_precedeDays")));
					c.add(Calendar.DAY_OF_YEAR, Integer.valueOf((String) app
							.getProperty(BusinessType.HypertensionRisk
									+ "_delayDays")));
					body.put("endDate", c.getTime());
					body.put("planStatus", PlanStatus.NEED_VISIT);
					body.put("sn", 0);
					createHypertensionRiskVisitPlan(body);
					updateFirstAssessmentDate(body);
				}
				if (list2.get(0).get("status") != null
						&& "1".equals(list2.get(0).get("status"))) {
					revertHypertensionRisk(empiId);
				}
			} else {
				if (hasHypertensionRisk && list2.get(0).get("status") != null
						&& "1".equals(list2.get(0).get("status"))) {
					revertHypertensionRisk(empiId);
				}
			}
			// if (!hasHypertensionRisk) {

			// }
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public void updateHypertensionRisk(String empiId, Map<String, Object> body,
			boolean flag) {
		String sql = "update MDC_HypertensionRisk set effectCase=:effectCase, "
				+ "effect=:effect,stopDate=:stopDate";
		if (flag) {
			sql += ",statusCase=:statusCase,confirmUser=:confirmUser,"
					+ "confirmUnit=:confirmUnit,confirmDate=:confirmDate";
		}
		sql += " where empiId=:empiId";
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("empiId", empiId);
		para.put("effectCase", body.get("visitEffect"));
		para.put("effect", body.get("stopCause"));
		try {
			if (body.get("stopDate") != null
					&& !"".equals(body.get("stopDate"))) {
				para.put(
						"stopDate",
						new SimpleDateFormat("yyyy-MM-dd").parse(body
								.get("stopDate") + ""));
			} else {
				para.put("stopDate", null);
			}
			if (flag) {
				UserRoleToken urt = UserRoleToken.getCurrent();
				para.put("statusCase", "9");
				para.put("confirmUser", urt.getUserId());
				para.put("confirmUnit", urt.getManageUnitId());
				para.put("confirmDate", new Date());
			}
			dao.doUpdate(sql, para);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> initializeHypertensionRiskAssessment(
			String empiId) {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("XY", false);
		result.put("QFTLYD", false);
		result.put("GXYJZS", false);
		result.put("TNBHZ", false);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, "",
					EHR_PastHistory);
			for (Map<String, Object> map : list) {
				if ("0702".equals(map.get("diseaseCode"))
						|| "0802".equals(map.get("diseaseCode"))
						|| "0902".equals(map.get("diseaseCode"))
						|| "1002".equals(map.get("diseaseCode"))) {
					result.put("GXYJZS", true);
				}
			}
			List<Map<String, Object>> list2 = dao
					.doList(cnd, "", EHR_LifeStyle);
			if (list2 != null && list2.size() > 0) {
				Map<String, Object> m = list2.get(0);
				if ("1".equals(m.get("smokeFreqCode"))
						|| "2".equals(m.get("smokeFreqCode"))) {
					result.put("XY", true);
				}
				if ("4".equals(m.get("trainFreqCode"))) {
					result.put("QFTLYD", true);
				}
			}
			List<Map<String, Object>> list3 = dao.doList(cnd, "",
					MDC_DiabetesRecord);
			if (list3 != null && list3.size() > 0) {
				result.put("TNBHZ", true);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}
}
