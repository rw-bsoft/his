/**
 * @(#)PlanExecutionModule.java Created on 2012-4-27 下午05:12:01
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.security.support.condition.FilterCondition;
import ctd.service.core.ServiceException;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class PlanExecutionModule implements BSCHISEntryNames {
	private BaseDAO dao;

	public PlanExecutionModule(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 根据计划执行编号加载 HER_EducationPlanExe 表信息数据
	 * 
	 * @param exeId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> loadPlanExeData(String exeId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "exeId", "s", exeId);
		try {
			return dao.doList(cnd, null, HER_EducationPlanExe);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载计划执行信息数据失败！", e);
		}
	}

	/**
	 * 根据计划执行编号查询 HER_EducationRecord 表中计划执行情况数据
	 * 
	 * @param exeId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryRecordByExeId(String exeId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.exeId", "s", exeId);
		try {
			return dao.doList(cnd, null, HER_EducationRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载计划执行情况数据失败！", e);
		}
	}

	/**
	 * 根据计划执行编号查询 HER_EducationRecord 表中计划执行情况数据
	 * 
	 * @param exeId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryRecordsByExeId(List<?> queryCnd,
			String exeId) throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper
					.createSimpleCnd("eq", "a.exeId", "s", exeId);
			if (queryCnd != null) {
				cnd = CNDHelper.createArrayCnd("and", cnd, queryCnd);
			}
			return dao.doList(cnd, null, HER_EducationRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载计划执行情况数据失败！", e);
		}
	}

	/**
	 * 保存健康教育具体任务执行情况
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 */
	public Map<String, Object> savePlanRecord(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ServiceException {
		try {
			return dao.doSave(op, HER_EducationRecord, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存健康教育具体任务执行情况失败！", e);
		}
	}

	/**
	 * 根据角色数据权限查询 HER_EducationPlanExe 表中数据并过滤
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param sortInfo
	 * @param queryCndsType
	 * @param queryCnd
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Object> loadPlanExe(List queryCnd, String queryCndsType,
			String sortInfo, int pageSize, int pageNo)
			throws ModelDataOperationException {
		try {
			return dao.doList(queryCnd, sortInfo, HER_EducationPlanExe, pageNo,
					pageSize, queryCndsType);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"根据角色数据权限查询 HER_EducationPlanExe 表中数据并过滤失败！", e);
		}
	}

	/**
	 * 根据计划编号删除执行计划数据
	 * 
	 * @param setId
	 * @throws ModelDataOperationException
	 */
	public void removePlanExeBySetId(String setId)
			throws ModelDataOperationException {
		try {
			dao.doRemove("setId", setId, HER_EducationPlanExe);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("根据计划编号删除执行计划数据失败！", e);
		}
	}

	/**
	 * 保存计划需要执行任务的相关数据
	 * 
	 * @param op
	 * @param record
	 * @return
	 * @throws ServiceException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePlanExe(String op, Map<String, Object> body)
			throws ServiceException, ModelDataOperationException {
		try {
			return dao.doSave(op, HER_EducationPlanExe, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存计划需要执行任务的相关数据失败！", e);
		}
	}

	/**
	 * 根据计划制定编号加载计划执行数据
	 * 
	 * @param setId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadPlanExe(List<?> queryCnd, String setId,
			int pageSize, int pageNo) throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper
					.createSimpleCnd("eq", "a.setId", "s", setId);
			if (queryCnd != null) {
				cnd = CNDHelper.createArrayCnd("and", cnd, queryCnd);
			}
			return dao.doList(cnd, null, HER_EducationPlanExe, pageNo,
					pageSize, null);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载计划制定情况数据失败!", e);
		}
	}

	/**
	 * 按条件删除计划执行数据
	 * 
	 * @param setId
	 *            计划编号
	 * @param executePerson
	 *            计划执行人
	 * @throws ModelDataOperationException
	 */
	public void removePlanExe(String setId, String executePerson)
			throws ModelDataOperationException {
		String hql = new StringBuffer()
				.append("delete from ")
				.append(HER_EducationPlanExe)
				.append(" where setId = :setId and executePerson = :executePerson")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("setId", setId);
		parameters.put("executePerson", executePerson);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("按条件删除计划执行数据失败！", e);
		}
	}

	/**
	 * 以计划编号为条件查询健康教育计划执行任务数据
	 * 
	 * @param setId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryRecordBySetId(String setId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.setId", "s", setId);
		try {
			return dao.doList(cnd, null, HER_EducationRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("以计划编号为条件查询健康教育计划执行任务数据失败！",
					e);
		}
	}

	/**
	 * 以记录编号为条件查询健康教育计划执行任务数据
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryRecordByRecordId(String recordId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.recordId", "s",
				recordId);
		try {
			return dao.doQuery(cnd, null, HER_EducationRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("以记录编号为条件查询健康教育计划执行任务数据失败！",
					e);
		}
	}

	/**
	 * 删除健康教育具体执行数据
	 * 
	 * @param pkey
	 * @throws ModelDataOperationException
	 */
	public void removePlanRecordByRecordId(String pkey)
			throws ModelDataOperationException {
		try {
			dao.doRemove(pkey, HER_EducationRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("删除健康教育具体执行数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:获取健康处方信息
	 * @param recordId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-6-6 下午7:18:25
	 * @Modify:
	 */
	public Map<String, Object> getHealthRecipeList(String wayId)
			throws ModelDataOperationException {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "06");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					HER_HealthRecipeRecord_JHZX);
			if (list != null && list.size() > 0) {
				result.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_JHZX));
			}
			result.put("JKCFRecords", list);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康处方失败！", e);
		}
		return result;
	}

	/**
	 * 保存健康教育处方
	 * 
	 * @Description:
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-11 下午5:59:22
	 * @Modify:
	 */
	public Map<String, Object> saveEduRecipel(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> JKCF = (Map<String, Object>) record.get("JKCF");
		String wayId = record.get("wayId") + "";
		record.remove(JKCF);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> reBody = new HashMap<String, Object>();
		try {
			List cnd1 = CNDHelper.createSimpleCnd("eq", "wayId", "s", wayId);
			List cnd2 = CNDHelper.createSimpleCnd("eq", "guideWay", "s", "06");
			List cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
			List<Map<String, Object>> list = dao.doList(cnd, "diagnoseId",
					HER_HealthRecipeRecord_JHZX);
			for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
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
				mBody.put("guideWay", "06");
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
				for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
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
						HER_HealthRecipeRecord_JHZX, map, true);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						HER_HealthRecipeRecord_JHZX, map, true);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove((String) map.get("id"),
						HER_HealthRecipeRecord_JHZX);
			}
			if (list != null && list.size() > 0) {
				reBody.putAll(SchemaUtil.setDictionaryMessageForForm(
						list.get(0), HER_HealthRecipeRecord_JHZX));
			}
			reBody.put("JKCFRecords", list);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存健康教育处方数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "保存健康教育处方失败！", e);
		}
		return reBody;
	}

	public Map<String, Object> loadPlanExeByPlanNumber(List<?> queryCnd,
			String queryCndsType, String sortInfo, int pageSize, int pageNo) {
		Map<String, Object> result = new HashMap<String, Object>();
		String sql = "select a.exeId as exeId,a.setId as setId,b.planType as planType,b.beginDate as beginDate,"
				+ "b.endDate as endDate,b.planContent as planContent,b.planPerson as planPerson,b.planDate as planDate,"
				+ "b.status as status,a.executePerson as executePerson,a.executeUnit as executeUnit,a.createUser as createUser,"
				+ "a.createUnit as createUnit,a.createDate as createDate,a.lastModifyUser as lastModifyUser,a.lastModifyUnit as lastModifyUnit,"
				+ "a.lastModifyDate as lastModifyDate from HER_EducationPlanExe  a, "
				+ "HER_EducationPlanSet  b where a.setId=b.setId ";
		try {
			if (queryCnd.size() > 0) {
				String where = null;

				where = ExpressionProcessor.instance().toString(queryCnd);
				sql += " and " + where;
			}
			Schema sc = SchemaController.instance().get(HER_EducationPlanExe);
			FilterCondition c = (FilterCondition) sc
					.lookupCondition(queryCndsType);
			if (c != null) {
				List<Object> roleCnd = (List<Object>) c.getDefine();
				if (roleCnd != null && !roleCnd.isEmpty()) {
					String where = ExpressionProcessor.instance().toString(
							roleCnd);
					sql += " and " + where;
				}
			}
			sql += " and (select count(*) from HER_EducationRecord c where c.exeId=a.exeId)=0";
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<Map<String, Object>> list = null;
			list = dao.doSqlQuery(sql, parameters);
			result.put("totalCount", list.size());
			parameters.put("first", (pageNo-1)*pageSize);
			parameters.put("max", pageSize);
			list = dao.doSqlQuery(sql, parameters);
			list=SchemaUtil.setDictionaryMessageForListFromSQL(list, HER_EducationPlanExe);
			result.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ExpException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}

		return result;
	}
}
