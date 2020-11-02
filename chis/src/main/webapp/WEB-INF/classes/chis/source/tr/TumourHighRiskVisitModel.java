/**
 * @(#)TumourHighRiskVisitModel.java Created on 2014-4-15 上午9:53:37
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.log.VindicateLogService;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskVisitModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourHighRiskVisitModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存肿瘤高危随访记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-16 上午10:13:10
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> saveTHRVisit(String op,
			Map<String, Object> record, VindicateLogService vLogService)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		UserRoleToken user = UserRoleToken.getCurrent();
		String visitEffect = (String) record.get("visitEffect");
		boolean validate = false;
		if (visitEffect.equals(VisitEffect.CONTINUE)) {
			validate = true;
		}
		try {
			rsMap = dao.doSave(op, MDC_TumourHighRiskVisit, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危随访记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危随访记录失败！", e);
		}
		String wayId = (String) record.get("visitId");
		if (wayId == null) {
			wayId = (String) rsMap.get("visitId");
		}
		Map<String, Object> JKCF = (Map<String, Object>) record.get("JKCF");
		record.remove(JKCF);
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> reBody = new HashMap<String, Object>();
		try {
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s",
					wayId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay",
					"s", "04");
			List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_ZLSF);
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
				mBody.put("empiId", record.get("empiId"));
				mBody.put("phrId", record.get("phrId"));
				mBody.put("recordId", m.get("recordId"));
				mBody.put("wayId", wayId);
				mBody.put("recipeName", m.get("recipeName"));
				mBody.put("diagnoseName", m.get("diagnoseName"));
				mBody.put("diagnoseId", m.get("diagnoseId"));
				mBody.put("ICD10", m.get("ICD10"));
				mBody.put("healthTeach", m.get("healthTeach"));
				mBody.put("examineUnit", record.get("examineUnit"));
				mBody.put("guideDate", record.get("guideDate"));
				mBody.put("guideUser", record.get("guideUser"));
				mBody.put("guideWay", "04");
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
						HER_HealthRecipeRecord_ZLSF, map, true);
				map.put("childId", result.get("id"));
				Map<String, Object> result2 = dao.doSave("create",
						HER_HealthRecipeRecord, map, true);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_ZLSF,
						"create", result.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "create",
						result2.get("id") + "", dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_ZLSF, map, true);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='04'")
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
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_ZLSF,
						"update", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "update",
						zjId, dao);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_ZLSF);
				String hql = new StringBuffer(" from ")
						.append(HER_HealthRecipeRecord)
						.append(" where childId = :childId and guideWay='04'")
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
				vLogService.saveVindicateLog(HER_HealthRecipeRecord_ZLSF,
						"delete", map.get("id") + "", dao);
				vLogService.saveVindicateLog(HER_HealthRecipeRecord, "delete",
						zjId, dao);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_TNBSF));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危健康教育记录失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危健康教育记录失败！", e);
		} catch (ServiceException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤高危健康教育日志记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:更新随访计划状态
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-16 下午5:01:26
	 * @Modify:
	 */
	public Map<String, Object> updateTHRVisitPlan(Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave("update", PUB_VisitPlan, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新随访计划数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新随访计划失败！");
		}
		return rsMap;
	}

	/**
	 * 更新过期"应该"随访计划为"未访"
	 * 
	 * @param empiId
	 * @param beginDate
	 * @param userId
	 * @throws ModelDataOperationException
	 */
	public int updatePastDueVisitPlanStatus(String empiId, String recordId,
			String beginDate, String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,lastModifyUser = :lastModifyUser")
				.append(",lastModifyDate = :lastModifyDate")
				.append(" where empiId = :empiId and businessType = :businessType")
				.append(" and recordId=:recordId")
				.append(" and planStatus = :planStatus0 and beginDate < :beginDate")
				.toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planStatus", PlanStatus.NOT_VISIT);
		map.put("businessType", BusinessType.THR);
		map.put("empiId", empiId);
		map.put("recordId", recordId);
		map.put("planStatus0", PlanStatus.NEED_VISIT);
		map.put("beginDate", BSCHISUtil.toDate(beginDate));
		map.put("lastModifyUser", userId);
		map.put("lastModifyDate", new Date());
		int rsInt = 0;
		try {
			rsInt = dao.doUpdate(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！");
		}
		return rsInt;
	}

	/**
	 * 更新(下一次)随访提醒时间
	 * 
	 * @param planType
	 * @param nextPlanId
	 * @param nextRemindDate
	 * @param session
	 * @throws Exception
	 */
	protected void setNextRemindDate(String businessType, String nextPlanId,
			Date nextRemindDate) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("PUB_VisitPlan")
				.append(" set beginVisitDate = ")
				.append("cast(:beginVisitDate as date) where planId = :planId")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("planId", nextPlanId);
		param.put("beginVisitDate", nextRemindDate);

		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新下一次随访提醒时间失败！");
		}
	}

	/**
	 * 
	 * @Description:获取某人的肿瘤高危随访记录，结果按随访ID倒序
	 * @param THRID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-20 下午1:33:46
	 * @Modify:
	 */
	public List<Map<String, Object>> getTHRVisitRecords(String THRID)
			throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "THRID", "s", THRID);
		try {
			rsList = dao
					.doQuery(cnd, " visitId desc ", MDC_TumourHighRiskVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取肿瘤高危随访记录失败！", e);
		}
		return rsList;
	}

	public Map<String, Object> getTHRVisit(String pkey, String schema)
			throws ModelDataOperationException {
		Map<String, Object> reMap = new HashMap<String, Object>();
		try {
			reMap = dao.doLoad(schema, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取肿瘤高危随访记录失败！", e);
		}
		if (reMap != null && reMap.size() > 0) {
			String visitId = (String) reMap.get("visitId");
			try {
				List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "wayId",
						"s", visitId);
				List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay",
						"s", "04");
				List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
				List<Map<String, Object>> list = dao.doList(cnd, "recordId",
						HER_HealthRecipeRecord_ZLSF);
				if (list != null && list.size() > 0) {
					reMap.putAll(SchemaUtil.setDictionaryMessageForForm(
							list.get(0), HER_HealthRecipeRecord_ZLSF));
				}
				reMap.put("JKCFRecords", list);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "获取肿瘤教育指导信息失败!", e);
			}
		}
		return reMap;
	}

	/**
	 * 
	 * @Description:统计随访次数，year=0 时全部随访次数，否则统计某年的随访次数
	 * @param empiId
	 * @param highRiskType
	 *            高危类型
	 * @param year
	 *            年份标识
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 上午9:59:19
	 * @Modify:
	 */
	public long getVisitNumber(String empiId, String highRiskType, int year)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(a.visitId) as visitNum from ")
				.append(MDC_TumourHighRiskVisit)
				.append(" as a ")
				.append(" where a.empiId=:empiId and a.highRiskType=:highRiskType ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		if (year > 1000) {
			Date startDate = BSCHISUtil.toDate(year + "-01-01 00:00:00");
			Date endDate = BSCHISUtil.toDate(year + "-12-31 23:59:59");
			hql.append(" and (a.createDate>=:startDate and a.createDate<=:endDate) ");
			pMap.put("startDate", startDate);
			pMap.put("endDate", endDate);
		} else if (year != 0) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "统计随访次数年份参数不对");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计随访次数失败！", e);
		}
		long visitNumber = 0;
		if (rsMap != null && rsMap.size() > 0) {
			visitNumber = (Long) rsMap.get("visitNum");
		}
		return visitNumber;
	}

	/**
	 * 
	 * @Description:统计随访计划次数，year=0 时全部随访次数，否则统计某年的随访次数
	 * @param empiId
	 * @param THRID
	 *            高危档案编号
	 * @param year
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 上午10:46:55
	 * @Modify:
	 */
	public long getVisitPlanNumber(String empiId, String THRID, int year)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(a.planId) as vpNum from ").append(PUB_VisitPlan)
				.append(" as a ")
				.append(" where a.empiId=:empiId and recordId=:recordId ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("recordId", THRID);
		if (year > 1000) {
			Date startDate = BSCHISUtil.toDate(year + "-01-01 00:00:00");
			Date endDate = BSCHISUtil.toDate(year + "-12-31 23:59:59");
			hql.append(" and (a.planDate>=:startDate and a.planDate<=:endDate) ");
			pMap.put("startDate", startDate);
			pMap.put("endDate", endDate);
		} else if (year != 0) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "统计随访次数年份参数不对");
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计随访计划次数失败！", e);
		}
		long visitPlanNumber = 0;
		if (rsMap != null && rsMap.size() > 0) {
			visitPlanNumber = (Long) rsMap.get("vpNum");
		}
		return visitPlanNumber;
	}

	/**
	 * 
	 * @Description:分页加载肿瘤高危随访计划列表
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-17 下午4:23:48
	 * @Modify:
	 */
	public Map<String, Object> loadTHRVisitPlanPageList(Map<String, Object> req)
			throws ModelDataOperationException {
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
			sc = SchemaController.instance().get(
					MDC_TumourHighRiskVisitPlanListView);
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.hasProperty("refAlias") || si.isVirtual()) {
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
				.append(" left join MDC_TumourHighRisk d on a.recordId=d.thrid")
				.append(" left join MDC_TumourHighRiskVisit e on a.recordid=e.thrid and a.visitid=e.visitid ");
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
					Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划统计总记录数时失败！", e);
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
						Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tpList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tpMap = new HashMap<String, Object>();
					Date birthday = (Date) rMap.get("BIRTHDAY");
					if (birthday != null) {
						tpMap.put("age", BSCHISUtil.calculateAge(birthday, null));
					}
					for (SchemaItem si : sc.getItems()) {
						if (si.hasProperty("refAlias") || si.isVirtual()) {
							String refItemId = (String) si
									.getProperty("refItemId");
							tpMap.put(refItemId,
									rMap.get(refItemId.toUpperCase()));
						} else {
							String f = si.getId();
							tpMap.put(f, rMap.get(f.toUpperCase()));
						}
					}
					tpList.add(tpMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tpList,
						MDC_TumourHighRiskVisitPlanManagerListView));
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

	/**
	 * 
	 * @Description:获取上次随访时间
	 * @param empiId
	 * @param recordId
	 * @param planId
	 * @param businessType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-18 下午2:43:17
	 * @Modify:
	 */
	public Date getLastVisitDate(String empiId, String recordId,String planId,
			String businessType) throws ModelDataOperationException {
		String hql = new StringBuffer("select a.planId as planId, a.visitDate as visitDate from ")
				.append(PUB_VisitPlan).append(" as a ")
				.append(" where a.visitId is not null and a.planId < :planId")
				.append(" and a.businessType=:businessType")
				.append(" and a.empiId=:empiId")
				.append(" and a.recordId=:recordId")
				.append(" order by a.visitDate desc ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("recordId", recordId);
		parameters.put("planId", planId);
		parameters.put("businessType", businessType);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询随访计划获取上次随访时间失败！", e);
		}
		Calendar calendar = Calendar.getInstance();
		Date lastVisitDate = calendar.getTime();
		if(rsList != null && rsList.size() > 0){
			Map<String, Object> rsMap = rsList.get(0);
			lastVisitDate = (Date) rsMap.get("visitDate");
			if(lastVisitDate == null){
				lastVisitDate = calendar.getTime();
			}
		}
		return lastVisitDate;
	}
}
