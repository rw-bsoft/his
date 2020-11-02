/**
 * @(#)TumourPatientVisitModel.java Created on 2014-4-29 下午4:33:48
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
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
import chis.source.util.BSCHISUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourPatientVisitModel extends AbstractVisitPlanRelevantModel
		implements BSCHISEntryNames {
	private final static String DIC_TUMOUR_CVS="chis.dictionary.tumourCSV";

	public TumourPatientVisitModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 
	 * @Description:保存肿瘤患者随访记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 上午10:26:50
	 * @Modify:
	 */
	public Map<String, Object> saveTPVisitRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourPatientVisit, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者随访记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者随访记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:获取上次随访日期和计划日期
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 上午10:51:54
	 * @Modify:
	 */
	public Map<String, Object> getLastVistDateAndPlanDate(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select a.visitDate as visitDate")
				.append(",b.planDate as planDate from ")
				.append("MDC_TumourPatientVisit").append(" a,")
				.append("PUB_VisitPlan").append(" b ")
				.append(" where a.empiId =:empiId and a.visitId = b.visitId ")
				.append(" and a.visitDate <= :visitDate")
				.append(" order by a.visitId desc").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("visitDate", new Date());
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取上次随访日期和计划日期失败！");
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (rsList != null && rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:随访中生成新的随访计划
	 * @param paraMap
	 * @param visitPlanCreator
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 上午10:54:28
	 * @Modify:
	 */
	public PlanType createVisitPlan(Map<String, Object> paraMap,
			VisitPlanCreator visitPlanCreator)
			throws ModelDataOperationException {
		String CSV = "";
		if (paraMap.get("CSV") instanceof String) {
			CSV = (String) paraMap.get("CSV");
		} else if (paraMap.get("CSV") instanceof Integer) {
			CSV = ((Integer) paraMap.get("CSV")).intValue() + "";
		} else {
			CSV = ((Object) paraMap.get("CSV")).toString();
		}
		Map<String, Object> genVPMap = new HashMap<String, Object>();
		String empiId = (String) paraMap.get("empiId");
		String TPRCID = (String) paraMap.get("TPRCID");
		genVPMap.put("empiId", empiId);
		genVPMap.put("TPRCID", TPRCID);
		genVPMap.put("recordId", TPRCID);
		genVPMap.put("businessType", BusinessType.TPV);
		genVPMap.put("createDate", BSCHISUtil.toString(new Date()));
		genVPMap.put("group", CSV);
		Map<String, Object> rsMap = this.getLastVistDateAndPlanDate(empiId);
		if (rsMap != null && rsMap.size() > 0) {
			genVPMap.put("lastVisitDate", rsMap.get("visitDate"));
			genVPMap.put("lastPlanDate", rsMap.get("planDate"));
		}
		// 生成计划
		this.hasPlanType(CSV);
		PlanType pt = null;
		try {
			pt = visitPlanCreator.create(genVPMap, dao.getContext());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
		}
		return pt;
	}

	/**
	 * 
	 * @Description:更新随访计划状态
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-29 上午11:27:04
	 * @Modify:
	 */
	public Map<String, Object> updateTPVisitPlan(Map<String, Object> record,
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
	public int updatePastDueVisitPlanStatus(String TPRCID, String beginDate,
			String userId) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,lastModifyUser = :lastModifyUser")
				.append(",lastModifyDate = :lastModifyDate")
				.append(" where recordId = :TPRCID and businessType = :businessType")
				.append(" and planStatus = :planStatus0 and beginDate < :beginDate")
				.toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planStatus", PlanStatus.NOT_VISIT);
		map.put("businessType", BusinessType.TPV);
		map.put("TPRCID", TPRCID);
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

	@Override
	protected boolean runExpression(Map<String, Object> params,
			String expression) throws ModelDataOperationException {
		expression = expression.replace("group", (String) params.get("group"));
		try {
			return (Boolean) ExpressionProcessor.instance().run(expression);
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					"Failed to run instance expression.", e);
		}
	}

	/**
	 * 
	 * @Description:检查随访配置
	 * @param businessType
	 * @param managerGroup
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-19 下午2:43:43
	 * @Modify:
	 */
	public void hasPlanType(String group)
			throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessType", BusinessType.TPV);
		params.put("group", group);
		String planTypeCode = this.getPlanTypeCode(params);
		if(StringUtils.isEmpty(planTypeCode)){
			try {
				Dictionary cvsDic = DictionaryController.instance().get(
						DIC_TUMOUR_CVS);
				String groupText = cvsDic.getText(group);
				throw new ModelDataOperationException(
						Constants.CODE_RECORD_NOT_FOUND,
						"系统管理-->系统配置管理-->肿瘤高危人群随访参数配置中找不到随访分组为["
								+ groupText + "]的计划类型配置!");
			} catch (ControllerException e) {
				throw new ModelDataOperationException(Constants.CODE_NOT_FOUND,
						"字典文件未找到或不存在！", e);
			}
		}
	}
}
