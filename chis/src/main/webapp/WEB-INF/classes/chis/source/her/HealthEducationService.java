/**
 * @(#)HealthEducationService.java Created on 2012-4-24 下午03:30:51
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.check.HealthCheckService;
import chis.source.control.ControlRunner;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.CNDHelper;
import chis.source.util.InputStreamUtils;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.worklist.WorkListModel;
import ctd.account.UserRoleToken;
import ctd.account.user.UserController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class HealthEducationService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(HealthCheckService.class);

	/**
	 * 加载健康教育计划数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadHealthEducationPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String setId = (String) body.get("setId");
		List<?> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		PlanCreateModule pcModule = new PlanCreateModule(dao);
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			Map<String, Object> formData = pcModule.loadPlanSet(setId);
			formData = SchemaUtil.setDictionaryMessageForForm(formData,
					HER_EducationPlanSet);
			Map<String, Object> record = peModule.loadPlanExe(queryCnd, setId,
					DEFAULT_PAGESIZE, DEFAULT_PAGENO);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) record
					.get("body");
			listData = SchemaUtil.setDictionaryMessageForList(listData,
					HER_EducationPlanExe);
			if (formData != null) {
				resBody.put(HER_EducationPlanSet + Constants.DATAFORMAT4FORM,
						formData);
			}
			if (listData != null && listData.size() > 0) {
				for (Map<String, Object> map : listData) {
					int index = peModule.queryRecordByExeId(
							(String) map.get("exeId")).size();
					map.put("planNumber", index);
				}
				resBody.put(HER_EducationPlanExe + Constants.DATAFORMAT4LIST,
						listData);
			}
			res.put("body", resBody);
			res.put("totalCount", record.get("totalCount"));
		} catch (ModelDataOperationException e) {
			logger.error("load HealthEducationPlan is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存健康教育计划数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHealthEducationPlan(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PlanCreateModule pcModule = new PlanCreateModule(dao);
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		WorkListModel wlModel = new WorkListModel(dao);
		String op = (String) req.get("op");
		try {
			String[] oldNames = null;
			if ("update".equals(op)) {
				oldNames = ((String) pcModule.loadPlanSet(
						(String) body.get("setId")).get("executePerson"))
						.split(",");
			}
			String[] newNames = StringUtils.trimToEmpty(
					(String) body.get("executePerson")).split(",");
			Map<String, Object> pkey = pcModule.savePlanSet(op, body);
			Map<String, Object> record = new HashMap<String, Object>();
			Map<String, Object> workRecord = new HashMap<String, Object>();
			Map<String, Object> deleteWordRecord = new HashMap<String, Object>();
			Map<String, Object> exeKey = new HashMap<String, Object>();
			if ("create".equals(op)) {
				for (String name : newNames) {
					String[] nameAndUnit = name.split("@");
					record.put("setId", pkey.get("setId"));
					record.put("executePerson", name);
					record.put("executeUnit", nameAndUnit[0]);
					record.put("lastModifyUser", body.get("lastModifyUser"));
					record.put("lastModifyDate", body.get("lastModifyDate"));
					exeKey = peModule.savePlanExe(op, record);
					workRecord.put("workType", "16");
					workRecord.put("empiId", "");
					workRecord.put("otherId", pkey.get("setId"));
					workRecord.put("recordId", exeKey.get("exeId"));
					workRecord.put("manaUnitId", nameAndUnit[0]);
					workRecord.put("doctorId", nameAndUnit[1]);
					workRecord.put("beginDate", body.get("beginDate"));
					workRecord.put("endDate", body.get("endDate"));
					wlModel.insertWorkList(workRecord);
					res.put("body", pkey);
				}
			} else {
				List<String> addNames = new ArrayList<String>();
				List<String> removeNames = new ArrayList<String>();
				label1: for (String newName : newNames) {
					for (String oldName : oldNames) {
						if (newName.equals(oldName)) {
							continue label1;
						}
					}
					addNames.add(newName);
				}
				for (String name : addNames) {
					String[] nameAndUnit = name.split("@");
					record.put("setId", body.get("setId"));
					record.put("executePerson", name);
					record.put("executeUnit", nameAndUnit[0]);
					record.put("lastModifyUser", body.get("lastModifyUser"));
					record.put("lastModifyDate", body.get("lastModifyDate"));
					exeKey = peModule.savePlanExe("create", record);
					workRecord.put("workType", "16");
					workRecord.put("empiId", "");
					workRecord.put("otherId", pkey.get("setId"));
					workRecord.put("recordId", exeKey.get("exeId"));
					workRecord.put("manaUnitId", nameAndUnit[0]);
					workRecord.put("doctorId", nameAndUnit[1]);
					workRecord.put("beginDate", body.get("beginDate"));
					workRecord.put("endDate", body.get("endDate"));
					wlModel.insertWorkList(workRecord);
				}
				label2: for (String oldName : oldNames) {
					for (String newName : newNames) {
						if (newName.equals(oldName)) {
							continue label2;
						}
					}
					removeNames.add(oldName);
				}
				for (String name : removeNames) {
					peModule.removePlanExe((String) body.get("setId"), name);
					String[] nameAndUnit = name.split("@");
					deleteWordRecord.put("manaUnitId", nameAndUnit[0]);
					deleteWordRecord.put("doctorId", nameAndUnit[1]);
					deleteWordRecord.put("otherId", body.get("setId"));
					deleteWordRecord.put("workType", "16");
					wlModel.deleteWorkList(deleteWordRecord);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("save HealthEducationPlan is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询健康教育计划执行任务数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryEducationRecordCount(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String setId = (String) body.get("setId");
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		try {
			List<Map<String, Object>> data = peModule.queryRecordBySetId(setId);
			int count = data.size();
			res.put("count", count);
		} catch (ModelDataOperationException e) {
			logger.error("query EducationRecordCount is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 删除健康教育计划相关数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doRemoveHealthEducation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String setId = (String) req.get("pkey");
		PlanCreateModule pcModule = new PlanCreateModule(dao);
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		WorkListModel wlModel = new WorkListModel(dao);
		try {
			pcModule.removePlanSetBySetId(setId);
			peModule.removePlanExeBySetId(setId);
			Map<String, Object> deleteWordRecord = new HashMap<String, Object>();
			deleteWordRecord.put("otherId", setId);
			deleteWordRecord.put("workType", "16");
			wlModel.deleteWorkList(deleteWordRecord);
		} catch (ModelDataOperationException e) {
			logger.error("remove HealthEducation is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 作废健康教育计划
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogOutHealthEducationSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PlanCreateModule pcModule = new PlanCreateModule(dao);
		WorkListModel wlModel = new WorkListModel(dao);
		String setId = (String) body.get("setId");
		try {
			pcModule.logOutHealthEducationSet(setId);
			Map<String, Object> deleteWordRecord = new HashMap<String, Object>();
			deleteWordRecord.put("otherId", setId);
			deleteWordRecord.put("workType", "16");
			wlModel.deleteWorkList(deleteWordRecord);
		} catch (ModelDataOperationException e) {
			logger.error("logOut HealthEducationSet is fail!", e);
			throw new ServiceException("注销健康教育计划失败！", e);
		}
	}

	/**
	 * 加载执行计划及计划执行情况数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadPlanExeData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String exeId = (String) body.get("exeId");
		List<?> queryCnd = null;
		if (body.containsKey("cnd")) {
			queryCnd = (List<?>) body.get("cnd");
		}
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			Map<String, Object> formData = peModule.loadPlanExeData(exeId).get(
					0);
			paraMap.put("setId", formData.get("setId"));
			paraMap.put("exeId", formData.get("exeId"));
			paraMap.put("executePerson", formData.get("executePerson"));
			paraMap.put("beginDate", formData.get("beginDate"));
			paraMap.put("endDate", formData.get("endDate"));
			String executePerson = formData.get("executePerson").toString()
					.split("@")[1];
			List<Map<String, Object>> listData = peModule.queryRecordsByExeId(
					queryCnd, exeId);
			formData = SchemaUtil.setDictionaryMessageForForm(formData,
					HER_EducationPlanExe);
			listData = SchemaUtil.setDictionaryMessageForList(listData,
					HER_EducationRecord);
			resBody.put(HER_EducationPlanExe + Constants.DATAFORMAT4FORM,
					formData);
			resBody.put(HER_EducationRecord + Constants.DATAFORMAT4LIST,
					listData);

			Map<String, Boolean> pedConMap = this.getPlanExeDataControl(
					paraMap, ctx);
			UserRoleToken user = UserRoleToken.getCurrent();
			String userId = user.getUserId();
			if (!userId.equals(executePerson)) {
				pedConMap.put("update", false);
				pedConMap.put("create", false);
			}
			resBody.put(HER_EducationRecord + "_actions", pedConMap);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("load PlanExeData is fail!", e);
			throw new ServiceException(e);
		}
	}

	private Map<String, Boolean> getPlanExeDataControl(
			Map<String, Object> paraMap, Context ctx) throws ServiceException {
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(HER_EducationPlanExe, paraMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check HER_EducationPlanExe control error.", e);
			throw e;
		}
		return data;
	}

	/**
	 * 保存健康教育具体任务执行情况
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePlanRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String exeId = (String) body.get("exeId");
		String op = (String) req.get("op");
		String pkey = (String) body.get("recordId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		WorkListModel wlModel = new WorkListModel(dao);
		try {
			Map<String, Object> result = peModule.savePlanRecord(op, body);
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("recordId", exeId);
			m.put("workType", "16");
			wlModel.deleteWorkList(m);
			if ("create".equals(op)) {
				pkey = (String) result.get("recordId");
			}
			resBody.put("recordId", pkey);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("save PlanRecord is fail!", e);
			throw new ServiceException(e);
		}
	}

	private static final int DEFAULT_PAGESIZE = 25;
	private static final int DEFAULT_PAGENO = 1;

	/**
	 * 以执行编号为条件查询健康教育计划执行任务数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	protected void doLoadPlanExe(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		boolean flag = true;
		List<?> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		if (queryCnd.size() == 1) {
			queryCnd = CNDHelper.createSimpleCnd("eq", "b.status", "s", "0");
			flag = false;
		}
		String queryCndsType = "query";
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
		}
		int pageSize = DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		boolean planNumber = false;
		if (req.containsKey("planNumber")) {
			planNumber = (Boolean) req.get("planNumber");
		}
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		try {
			Map<String, Object> record=null;
			if (planNumber == false) {
				record = peModule.loadPlanExe(queryCnd,
						queryCndsType, sortInfo, pageSize, pageNo);
			} else {
				record = peModule.loadPlanExeByPlanNumber(queryCnd,
						queryCndsType, sortInfo, pageSize, pageNo);
			}
			List<Map<String, Object>> resBody = (List<Map<String, Object>>) record
					.get("body");
			List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
			if (flag) {
				for (Map<String, Object> r : resBody) {
					String exeId = (String) r.get("exeId");
					int index = peModule.queryRecordByExeId(exeId).size();
					r.put("planNumber", index);
				}
			} else {
				for (Map<String, Object> r : resBody) {
					String exeId = (String) r.get("exeId");
					String executePerson = (String) r.get("executePerson");
					String[] nameAndUnit = executePerson.split("@");
					if (nameAndUnit.length > 1) {
						int index = peModule.queryRecordByExeId(exeId).size();
						if (index == 0
								&& UserUtil.get(UserUtil.USER_ID).equals(
										nameAndUnit[1])) {
							Date now = new Date(System.currentTimeMillis());
							Date now_a = new Date(System.currentTimeMillis()
									- 1000 * 60 * 60 * 24);
							if (((Date) r.get("beginDate")).before(now)
									&& ((Date) r.get("endDate")).after(now_a)) {
								r.put("planNumber", index);
								body.add(r);
							}
						}
					}
				}
				resBody = body;
			}
			res.put("body", resBody);
			res.put("totalCount", record.get("totalCount"));
		} catch (ModelDataOperationException e) {
			logger.error("load PlanExe is fail!");
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据计划执行编号查询执行详细信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryPlanRecordData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		String exeId = (String) req.get("exeId");
		List<?> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		try {
			List<Map<String, Object>> resBody = peModule.queryRecordsByExeId(
					queryCnd, exeId);
			res.put("body", resBody);
		} catch (ModelDataOperationException e) {
			logger.error("query PlanRecord data is fail!");
			throw new ServiceException(e);
		}
	}

	/**
	 * 根据计划制定编号查询计划执行信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doQueryPlanExeData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		String setId = (String) req.get("setId");
		List<?> queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (List<?>) req.get("cnd");
		}
		int pageSize = DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		} else {
			pageSize = 25;
		}
		int pageNo = DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		} else {
			pageNo = 1;
		}
		try {
			Map<String, Object> record = peModule.loadPlanExe(queryCnd, setId,
					pageSize, pageNo);
			List<Map<String, Object>> resBody = (List<Map<String, Object>>) record
					.get("body");
			for (Map<String, Object> map : resBody) {
				String exeId = (String) map.get("exeId");
				int index = peModule.queryRecordByExeId(exeId).size();
				map.put("planNumber", index);
			}
			res.put("body", resBody);
			res.put("totalCount", record.get("totalCount"));
		} catch (ModelDataOperationException e) {
			logger.error("query PlanExe data is fail!");
			throw new ServiceException(e);
		}
	}

	protected void doRemovePlanRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		// PlanCreateModule pcModule = new PlanCreateModule(dao);
		// WorkListModel wlModel = new WorkListModel(dao);
		try {
			// Map<String, Object> planRecord =
			// peModule.queryRecordByRecordId(pkey).get(0);
			peModule.removePlanRecordByRecordId(pkey);
			// int count = peModule.queryRecordByExeId(
			// (String) planRecord.get("exeId")).size();
			// if (count == 0) {
			// Map<String, Object> planExe = peModule.loadPlanExeData(
			// (String) planRecord.get("exeId")).get(0);
			// Map<String, Object> planSet = pcModule
			// .loadPlanSet((String) planRecord.get("setId"));
			// Map<String, Object> workRecord = new HashMap<String, Object>();
			// String[] nameAndUnit =
			// ((String)planExe.get("executePerson")).split("@");
			// workRecord.put("workType", "16");
			// workRecord.put("empiId", "");
			// workRecord.put("otherId", planRecord.get("setId"));
			// workRecord.put("recordId", planRecord.get("exeId"));
			// workRecord.put("manaUnitId", nameAndUnit[0]);
			// workRecord.put("doctorId", nameAndUnit[1]);
			// workRecord.put("beginDate", planSet.get("beginDate"));
			// workRecord.put("endDate", planSet.get("endDate"));
			// wlModel.insertWorkList(workRecord);
			// }
		} catch (ModelDataOperationException e) {
			logger.error("remove PlanRecord is fail!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:获取健康处方
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-6-6 下午6:15:16
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetHealthRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String wayId = (String) reqBodyMap.get("wayId");
		Map<String, Object> resBodyMap = null;
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		try {
			resBodyMap = peModule.getHealthRecipeList(wayId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get health recipe.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 健康教育执行-保存健康教育处方
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-11 下午5:56:03
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveEducationRecipel(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		PlanExecutionModule peModule = new PlanExecutionModule(dao);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		try {
			resBodyMap = peModule.saveEduRecipel(op, reqBodyMap, false);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save health education recipel.", e);
			throw new ServiceException(e);
		}
		res.put("body", resBodyMap);
	}
}
