/**
 * @(#)TumourHighRiskModel.java Created on 2014-4-2 下午3:26:54
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.util.AppContextHolder;
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
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.TumourNature;
import chis.source.dic.VisitEffect;
import chis.source.dic.YesNo;
import chis.source.phr.HealthRecordModel;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.UserUtil;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskModel extends AbstractVisitPlanRelevantModel
		implements BSCHISEntryNames {
	private final static String DIC_TUMOUR_HIGH_RISK_TYPE = "chis.dictionary.tumourHighRiskType";
	private final static String DIC_TUMOUR_MANAGER_GROUP = "chis.dictionary.tumourManagerGroup";

	public TumourHighRiskModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 
	 * @Description:分布加载（查询）肿瘤高危列表
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 上午9:20:05
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> loadTHRPage(Map<String, Object> req)
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
		String queryCndsType = null;
		if (req.containsKey("queryCndsType")) {
			queryCndsType = (String) req.get("queryCndsType");
		}
		String sortInfo = null;
		if (req.containsKey("sortInfo")) {
			sortInfo = (String) req.get("sortInfo");
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
		try {
			Map<String, Object> rsMap = dao.doList(queryCnd, sortInfo,
					MDC_TumourHighRisk, pageNo, pageSize, queryCndsType);
			List<Map<String, Object>> rsList = (List<Map<String, Object>>) rsMap
					.get("body");
			TumourHighRiskVisitModel thrVisitModel = new TumourHighRiskVisitModel(
					dao);
			for (Map<String, Object> map : rsList) {
				Date birthday = (Date) map.get("birthday");
				if (birthday != null) {
					map.put("age", BSCHISUtil.calculateAge(birthday, null));
				}
				String createStatus = (String) map.get("createStatus");
				if ("1".equals(createStatus)) {
					String empiId = (String) map.get("empiId");
					String highRiskType = (String) map.get("HighRiskType");
					String THRID = (String) map.get("THRID");
					int year = Calendar.getInstance().get(Calendar.YEAR);
					// 本年随访次数
					long yearVisitNumber = thrVisitModel.getVisitNumber(empiId,
							highRiskType, year);
					map.put("yearVisitNumber", yearVisitNumber);
					// 本年计划次数
					long yearVisitPlanNumber = thrVisitModel
							.getVisitPlanNumber(empiId, THRID, year);
					map.put("yearVisitPlanNumber", yearVisitPlanNumber);
					// 随访次数
					long visitNumber = thrVisitModel.getVisitNumber(empiId,
							highRiskType, 0);
					map.put("visitNumber", visitNumber);
				} else {
					map.put("yearVisitNumber", 0);
					map.put("yearVisitPlanNumber", 0);
					map.put("visitNumber", 0);
				}
			}
			res.put("totalCount", rsMap.get("totalCount"));
			res.put("body", rsList);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "数据加载失败！", e);
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}

	/**
	 * 
	 * @Description: 获取高危管理级别<br/>
	 *               规则：初筛 检查结果中无 阳性 检查结果 为 常规组
	 * @param screeningId
	 *            初筛记录ID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-3 下午5:36:24
	 * @Modify:
	 */
	public String getManagerGroup(String screeningId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourScreeningCheckResult).append(
						" where checkResult='2' ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		// 取全部检查结果来判断
		if (StringUtils.isNotEmpty(screeningId)) {
			// 取某次初筛的检查结果来断送
			hql.append(" and screeningId=:screeningId");
			pMap.put("screeningId", screeningId);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高危管理级别失败！", e);
		}
		String mg = "1";// 默认常规组
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				mg = "2";
			}
		}
		return mg;
	}

	/**
	 * 
	 * @Description:获取高危肿瘤记录
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-2 下午3:49:22
	 * @Modify:
	 */
	public Map<String, Object> getTumourHighRiskRecord(String THRID)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_TumourHighRisk, THRID);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取高危肿瘤记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:获取该人的全部查检结果
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-2 下午4:01:05
	 * @Modify:
	 */
	public List<Map<String, Object>> getAllCheckResult(String empiId,
			String highRiskType) throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "highRiskType",
				"s", highRiskType);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, " recordId desc",
					MDC_TumourScreeningCheckResult);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取该人的全部查检结果失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:保存高危肿瘤记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-2 下午5:10:35
	 * @Modify:
	 */
	public Map<String, Object> saveTumourHighRisk(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourHighRisk, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高危肿瘤记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存高危肿瘤记录失败！", e);
		}
		return rsMap;
	}

	/**
	 * @Description:获取随访记录
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-14 下午4:22:43
	 * @Modify:
	 */
	public List<Map<String, Object>> getVistRecord(String THRID)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select a.visitDate as visitDate")
				.append(",b.planDate as planDate from ")
				.append("MDC_TumourHighRiskVisit").append(" a,")
				.append("PUB_VisitPlan").append(" b ")
				.append(" where b.recordId =:THRID and a.visitId = b.visitId ")
				.append(" and a.visitDate <= :visitDate")
				.append(" order by a.visitId desc").toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("THRID", THRID);
		param.put("visitDate", new Date());
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录失败！");
		}

		return rsList;
	}

	/**
	 * 
	 * @Description:保存档案时生成 随访计划
	 * @param paraMap
	 * @param visitPlanCreator
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-14 下午4:34:40
	 * @Modify:
	 */
	public void createVisitPlan(Map<String, Object> paraMap,
			VisitPlanCreator visitPlanCreator)
			throws ModelDataOperationException {
		HashMap<String, Object> vBodyMap = new HashMap<String, Object>();
		String createDate = (String) paraMap.get("createDate") == null ? BSCHISUtil
				.toString(new Date()) : (String) paraMap.get("createDate");
		String empiId = (String) paraMap.get("empiId");
		String highRiskType = (String) paraMap.get("highRiskType");
		String THRID = (String) paraMap.get("THRID");
		vBodyMap.put("empiId", empiId);
		vBodyMap.put("THRID", THRID);
		vBodyMap.put("recordId", THRID);
		vBodyMap.put("businessType", BusinessType.THR);
		vBodyMap.put("createDate", createDate);
		vBodyMap.put("highRiskType", paraMap.get("highRiskType"));
		vBodyMap.put("managerGroup", paraMap.get("managerGroup"));
		vBodyMap.put("reserveDate", createDate);// 下次预约方式生成计划时必须字段
												// nextDate=createDate
		String oldManagerGroup = this.getLastMangerGroup(empiId, highRiskType);
		if (StringUtils.isEmpty(oldManagerGroup)) {// 说明为第一次建档则否为修改档案
			oldManagerGroup = (String) paraMap.get("managerGroup");
		}
		vBodyMap.put("oldManagerGroup", oldManagerGroup);
		// get lastVisitDate lastPlanDate
		List<Map<String, Object>> visitList = getVistRecord(THRID);
		if (visitList.size() > 0) {
			Map<String, Object> lastVisitData = visitList.get(0);
			if (lastVisitData != null) {
				vBodyMap.put("lastVisitDate", lastVisitData.get("visitDate"));
				vBodyMap.put("lastPlanDate", lastVisitData.get("planDate"));
			}
		}
		// 判断是否配置 随访计划类型
		this.hasPlanType(BusinessType.THR,
				(String) paraMap.get("managerGroup"),
				(String) paraMap.get("highRiskType"));
		boolean nextYear = false;
		if (paraMap.get("nextYear") != null) {
			nextYear = (Boolean) paraMap.get("nextYear");
		}
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String planMode = (String) app.getProperty(BusinessType.THR
				+ "_planMode");
		if (planMode == null || planMode.equals("")) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "未配置随访计划生成方式，请到系统配置界面中配置。");
		}
		vBodyMap.put("$sectionCutOffDate",
				BSCHISUtil.getSectionCutOffDate("tumourHighRiskEndMonth", nextYear));
		vBodyMap.put("$planMode", planMode);
		// 生成计划
		try {
			visitPlanCreator.create(vBodyMap, dao.getContext());
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤高危档案
	 * @param THRID
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-18 下午4:53:40
	 * @Modify:
	 */
	public void logoutTumourHighRiskRecord(String THRID,
			String cancellationReason) throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourHighRisk").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where THRID = :THRID ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("THRID", THRID);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销肿瘤高危档案失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤高危档案
	 * @param empiId
	 *            个人主索引
	 * @param highRiskType
	 *            高危类别
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-25 下午2:38:33
	 * @Modify:
	 */
	public void logoutTumourHighRiskRecord(String empiId, String highRiskType,
			String cancellationReason) throws ModelDataOperationException {
		Map<String, Object> rsMap = this.getTHR(empiId, highRiskType);
		if (rsMap != null && rsMap.size() > 0) {
			String THRID = (String) rsMap.get("THRID");
			// 注销档案
			this.logoutTumourHighRiskRecord(THRID, cancellationReason);
			// 注销随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			vpModel.logOutVisitPlan(vpModel.RECORDID, THRID, BusinessType.THR);
		}
	}

	/**
	 * 
	 * @Description:注销该人的全部肿瘤高危档案
	 * @param phrId
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-18 下午4:53:40
	 * @Modify:
	 */
	public void logoutAllTHR(String empiId, String cancellationReason)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourHighRisk").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where empiId = :empiId ")
				.append("  and  status = :normal");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("normal", Constants.CODE_STATUS_NORMAL);
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", userId);
		parameters.put("cancellationDate", new Date());
		parameters.put("cancellationUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationReason", cancellationReason);
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销该人的全部肿瘤高危档案失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复肿瘤高危档案
	 * @param phrId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-18 下午5:17:29
	 * @Modify:
	 */
	public void revertTumourHighRiskRecord(String THRID)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_TumourHighRisk")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where THRID = :THRID ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("THRID", THRID);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复高血压档案失败！", e);
		}
		// 恢复随访控制
		this.revertVisitControl(THRID);
	}

	/**
	 * 
	 * @Description:恢复随访控制
	 * @param THRID
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-8 上午10:02:55
	 * @Modify:
	 */
	private void revertVisitControl(String THRID)
			throws ModelDataOperationException {
		// 判断是否有终止管理的随访记录
		String hql = new StringBuffer(" from ").append(MDC_TumourHighRiskVisit)
				.append(" where visitEffect=:visitEffect and THRID=:THRID")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("visitEffect", VisitEffect.END);
		pMap.put("THRID", THRID);
		List<Map<String, Object>> endList = null;
		try {
			endList = dao.doQuery(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查终止管理随访记录失败！", e);
		}
		if (endList != null && endList.size() > 0) {
			for (Map<String, Object> endMap : endList) {
				String endVisitId = (String) endMap.get("visitId");
				String upVPHQL = new StringBuffer("update ")
						.append(PUB_VisitPlan)
						.append(" set planStatus='0',visitId='',visitDate=null ")
						.append(" where recordId=:recordId and businessType='15' and visitId=:visitId")
						.toString();
				Map<String, Object> pvMap = new HashMap<String, Object>();
				pvMap.put("recordId", THRID);
				pvMap.put("visitId", endVisitId);
				try {
					dao.doUpdate(upVPHQL, pvMap);
				} catch (PersistentDataOperationException e1) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "清除终止管理随访计划失败！", e1);
				}
				try {
					dao.doRemove(endVisitId, MDC_TumourHighRiskVisit);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "删除终止管理随访记录失败！", e);
				}
			}
		}
		// 判断是否要重新生成随访计划
		String vpHQL = new StringBuffer("select count(*) as recNum from ")
				.append(PUB_VisitPlan)
				.append(" where businessType='15' and recordId=:recordId")
				.append(" and endDate >=:currentDate").toString();
		Map<String, Object> pvMap = new HashMap<String, Object>();
		pvMap.put("recordId", THRID);
		pvMap.put("currentDate", new Date());
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(vpHQL, pvMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR,
					"查询是否有随访结束日期大于或等于当前日期的随访计划失败！", e);
		}
		long recNum = 0;
		if (rsMap != null && rsMap.size() > 0) {
			recNum = (Long) rsMap.get("recNum");
		}
		if (recNum == 0) {// 没有可用随访计划，重新生成随访计划
			// 取随访计划生成方式
			Application app = null;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e) {
				throw new ModelDataOperationException(e);
			}
			String planMode = (String) app.getProperty(BusinessType.THR
					+ "_planMode");
			// 取最后一次正常的随访记录
			String lvHQL = new StringBuffer(" from ")
					.append(MDC_TumourHighRiskVisit)
					.append(" where visitEffect='1' and THRID=:THRID order by createDate desc")
					.toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("THRID", THRID);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doQuery(lvHQL, parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "检查正常随访记录失败！", e);
			}
			Map<String, Object> lvMap = null;
			String managerGroup = "";
			if (rsList != null && rsList.size() > 0) {
				lvMap = rsList.get(0);
				managerGroup = (String) lvMap.get("fixGroup");
			}
			if (lvMap == null) {
				try {
					lvMap = dao.doLoad(MDC_TumourHighRisk, THRID);
				} catch (PersistentDataOperationException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "加载肿瘤高危档案失败！", e);
				}
				managerGroup = (String) lvMap.get("managerGroup");
			}
			if (PlanMode.BY_PLAN_TYPE.equals(planMode)) {// 按计划类型
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("THRID", THRID);
				paraMap.put("empiId", lvMap.get("empiId"));
				paraMap.put("highRiskType", lvMap.get("highRiskType"));
				paraMap.put("managerGroup", managerGroup);
				WebApplicationContext wac = (WebApplicationContext) AppContextHolder
						.get();
				VisitPlanCreator visitPlanCreator = (VisitPlanCreator) wac
						.getBean("chis.visitPlanCreator");
				this.createVisitPlan(paraMap, visitPlanCreator);
			} else {// 按下次预约时间
				int delayDays = Integer.parseInt((String) app
						.getProperty(BusinessType.THR + "_delayDays"));
				int precedeDays = Integer.parseInt((String) app
						.getProperty(BusinessType.THR + "_precedeDays"));
				Calendar calendar = Calendar.getInstance();
				Date currentDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_YEAR, -precedeDays);
				Date beginDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_YEAR, precedeDays + delayDays);
				Date endDate = calendar.getTime();
				Map<String, Object> planMap = new HashMap<String, Object>();
				planMap.put("beginDate", beginDate);
				planMap.put("planDate", currentDate);
				planMap.put("endDate", endDate);
				planMap.put("planStatus", PlanStatus.NEED_VISIT);
				planMap.put("businessType", BusinessType.THR);
				planMap.put("recordId", THRID);
				planMap.put("empiId", lvMap.get("empiId"));
				planMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
				planMap.put("lastModifyDate", currentDate);
				planMap.put("lastModifyUnit",
						UserUtil.get(UserUtil.MANAUNIT_ID));
				VisitPlanModel vpModel = new VisitPlanModel(dao);
				try {
					vpModel.saveVisitPlan("create", planMap);
				} catch (ValidateException e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "按下次预约时间*生成计划失败！", e);
				}
			}
		}
	}

	/**
	 * 
	 * @Description:恢复该人员的全部肿瘤高危档案
	 * @param phrId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-18 下午5:17:29
	 * @Modify:
	 */
	public void revertAllTHRRecord(String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_TumourHighRisk")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where empiId = :empiId ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("empiId", empiId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复该人员的全部高血压档案失败！", e);
		}
		// 恢复随访控制
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> thrList = null;
		try {
			thrList = dao.doQuery(cnd, null, MDC_TumourHighRisk);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取该人的全部肿瘤高危档案失败！", e);
		}
		if (thrList != null && thrList.size() > 0) {
			for (Map<String, Object> thrMap : thrList) {
				String THRID = (String) thrMap.get("THRID");
				this.revertVisitControl(THRID);
			}
		}
	}

	/**
	 * 
	 * @Description:保存肿瘤分组信息
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-20 上午10:43:04
	 * @Modify:
	 */
	public Map<String, Object> saveTumourHighRiskGroup(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourHighRiskGroup, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤分组信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤分组信息失败", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:更新高危记录性质
	 * @param empiId
	 * @param highRiskType
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-22 下午5:20:07
	 * @Modify:
	 */
	public void updateNature(String empiId, String highRiskType, String nature,
			String... THRID) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourHighRisk").append(" set nature=:nature  ")
				.append(",lastModifyUnit=:lastModifyUnit ")
				.append(",lastModifyUser=:lastModifyUser ")
				.append(",lastModifyDate=:lastModifyDate ")
				.append("  where empiId=:empiId")
				.append(" and highRiskType=:highRiskType");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nature", nature);
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("empiId", empiId);
		parameters.put("highRiskType", highRiskType);
		if (THRID.length == 1) {
			hql.append(" and THRID=:THRID");
			parameters.put("THRID", THRID[0]);
		}
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新高危记录性质为确诊失败！", e);
		}
	}

	/**
	 * 
	 * @Description:转正常
	 * @param empiId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-2 下午3:24:37
	 * @Modify:
	 */
	public void turnNormal(String empiId, String highRiskType)
			throws ModelDataOperationException {
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		// 疑似
		String hqlTSY = new StringBuffer("update ").append(MDC_TumourSeemingly)
				.append(" set nature='2' ").append(" where empiId=:empiId")
				.append(" and highRiskType=:highRiskType").toString();
		// 初筛
		String hqlTSG = new StringBuffer("update ").append(MDC_TumourScreening)
				.append(" set nature='2' ").append(" where empiId=:empiId")
				.append(" and highRiskType=:highRiskType").toString();
		// 高危
		String hqlTHR = new StringBuffer("update ").append(MDC_TumourHighRisk)
				.append(" set nature='2' ").append(" where empiId=:empiId")
				.append(" and highRiskType=:highRiskType").toString();
		try {
			dao.doUpdate(hqlTSY, pMap);
			dao.doUpdate(hqlTSG, pMap);
			dao.doUpdate(hqlTHR, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "高危转正常失败！", e);
		}
	}

	/**
	 * 
	 * @Description:依据empiId，phrId判断是否存在肿瘤高危档案
	 * @param empiId
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-12 下午3:15:48
	 * @Modify:
	 */
	public boolean isExistTHR(String empiId, String highRiskType)
			throws ModelDataOperationException {
		String hqlString = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourHighRisk)
				.append(" where empiId=:empiId and highRiskType=:highRiskType")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlString, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否存在肿瘤高危档案失败！", e);
		}
		boolean isExist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recNum = (Long) rsMap.get("recNum");
			if (recNum > 0) {
				isExist = true;
			}
		}
		return isExist;
	}

	/**
	 * 
	 * @Description:判断档案是否是注销状态
	 * @param THRID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-7 上午9:25:46
	 * @Modify:
	 */
	public boolean isLogoutTHR(String THRID) throws ModelDataOperationException {
		String hql = new StringBuffer("select status as status from ")
				.append(MDC_TumourHighRisk).append(" where THRID = :THRID")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("THRID", THRID);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断档案是否是注销状态失败！", e);
		}
		boolean isLogout = false;
		if (rsMap != null && rsMap.size() > 0) {
			String status = (String) rsMap.get("status");
			if (!Constants.CODE_STATUS_NORMAL.equals(status)) {
				isLogout = true;
			}
		}
		return isLogout;
	}

	/**
	 * 
	 * @Description:判断肿瘤高危随访是否是注销状态
	 * @param THRID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-4-23 下午7:32:41
	 * @Modify:
	 */
	public boolean isLogoutTHRVisit(String THRID)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(a.planId) as recNum from ")
				.append(PUB_VisitPlan).append(" a ")
				.append(" where a.businessType='15' ")
				.append(" and a.planStatus='9' ")
				.append(" and a.recordId=:recordId").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("recordId", THRID);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤高危随访注销的计划数失败！", e);
		}
		boolean exitLogoutTHRVisitPlan = false;
		if(rsMap != null && rsMap.size() > 0){
			long recNum = (Long) rsMap.get("recNum");
			if(recNum > 0){
				exitLogoutTHRVisitPlan = true;
			}
		}
		return exitLogoutTHRVisitPlan;
	}

	/**
	 * 
	 * @Description:肿瘤 empiId（个人主索引）+高危类别 可唯一确定一条记录
	 * @param empiId
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-15 上午10:30:46
	 * @Modify:
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> getTHR(String empiId, String highRiskType)
			throws ModelDataOperationException {
		List<Object> cnd1 = new CNDHelper().createSimpleCnd("eq", "a.empiId",
				"s", empiId);
		List<Object> cnd2 = new CNDHelper().createSimpleCnd("eq",
				"a.highRiskType", "s", highRiskType);
		List<Object> cnd = new CNDHelper().createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "a.THRID desc", MDC_TumourHighRisk);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤档案失败！", e);
		}
		Map<String, Object> thrMap = null;
		if (rsList != null && rsList.size() > 0) {
			thrMap = rsList.get(0);
		}
		return thrMap;
	}

	@Override
	protected boolean runExpression(Map<String, Object> params,
			String expression) throws ModelDataOperationException {
		expression = expression.replace("group",
				(String) params.get("managerGroup"));
		expression = expression.replace("tumourType",
				(String) params.get("highRiskType"));
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
	 * @param highRiskType
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-19 下午2:24:47
	 * @Modify:
	 */
	public void hasPlanType(String businessType, String managerGroup,
			String highRiskType) throws ModelDataOperationException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessType", businessType);
		params.put("managerGroup", managerGroup);
		params.put("highRiskType", highRiskType);
		String planType = this.getPlanTypeCode(params);
		if (StringUtils.isEmpty(planType)) {
			try {
				Dictionary mgDic = DictionaryController.instance().get(
						DIC_TUMOUR_MANAGER_GROUP);
				String managerGroupText = mgDic.getText(managerGroup);
				Dictionary thrtDic = DictionaryController.instance().get(
						DIC_TUMOUR_HIGH_RISK_TYPE);
				String highRiskTypeText = thrtDic.getText(highRiskType);
				throw new ModelDataOperationException(
						Constants.CODE_RECORD_NOT_FOUND,
						"系统管理-->系统配置管理-->肿瘤高危人群随访参数配置中找不到随访分组为["
								+ managerGroupText + "]高危类别为["
								+ highRiskTypeText + "]的计划类型配置!");

			} catch (ControllerException e) {
				throw new ModelDataOperationException(Constants.CODE_NOT_FOUND,
						"字典文件未找到或不存在！", e);
			}
		}
	}

	/**
	 * 
	 * @Description:获取上次转组 组别
	 * @param empiId
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-12-2 下午5:00:39
	 * @Modify:
	 */
	public String getLastMangerGroup(String empiId, String highRiskType)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "highRiskType",
				"s", highRiskType);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> gList = null;
		try {
			gList = dao.doList(cnd, "a.createDate desc ",
					MDC_TumourHighRiskGroup);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询肿瘤高危转组信息失败！", e);
		}
		String managerGroup = "";
		if (gList != null && gList.size() > 0) {
			Map<String, Object> gMap = gList.get(0);
			managerGroup = (String) gMap.get("fixGroup");
		}
		return managerGroup;
	}

	/**
	 * 
	 * @Description:初筛转高危时 增加高危记录
	 * @param paraMap
	 *            [screeningId,empiId,highRiskType,phrId]
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 下午3:46:09
	 * @Modify:
	 */
	public Map<String, Object> turnHighRiskToAddTHR(Map<String, Object> paraMap)
			throws ModelDataOperationException {
		String highRiskType = (String) paraMap.get("highRiskType");
		String empiId = (String) paraMap.get("empiId");
		String screeningId = (String) paraMap.get("screeningId");// 初筛记录ID
		String highRiskSource = (String) paraMap.get("highRiskSource");
		Map<String, Object> rsMap = null;
		Map<String, Object> thrMap = null;
		boolean succeed = false;
		boolean haveTHR = false;
		if (S.isNotEmpty(empiId) && S.isNotEmpty(highRiskType)) {
			thrMap = this.getTHR(empiId, highRiskType);
		}
		if (thrMap == null) {
			thrMap = new HashMap<String, Object>();
			thrMap.put("empiId", empiId);
			thrMap.put("phrId", paraMap.get("phrId"));
			thrMap.put("highRiskType", highRiskType);
			Calendar calendar = Calendar.getInstance();
			Date currDate = calendar.getTime();
			thrMap.put("turnHighRiskDate", currDate);
			String currUserId = UserUtil.get(UserUtil.USER_ID);
			thrMap.put("turnHighRiskDoctor", currUserId);
			thrMap.put("highRiskSource", highRiskSource);
			thrMap.put("nature", TumourNature.T_HighRisk);
			// 高危因素
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			String highRiskFactor = tqModel.getHighRiskFactor(empiId,
					highRiskType, highRiskSource);
			thrMap.put("highRiskFactor", highRiskFactor);
			// 初筛阳性 --0=否，1=是；检查结果有阳性为1
			thrMap.put("screeningPositive",
					this.getScreeningPositive(screeningId));
			// 初筛疾病 --0=否，1=是；检查结果有疾病为1
			thrMap.put("screeningSickness",
					this.getScreeningSickness(screeningId));
			thrMap.put("year", calendar.get(Calendar.YEAR));
			thrMap.put("createStatus", "0");// 建卡标志
			thrMap.put("timelyCreation", "0");// 建卡及时
			// 判断高危管理级别 == 初筛 检查结果中无 阳性 检查结果 为 常规组
			String managerGroup = this.getManagerGroup(screeningId);
			thrMap.put("managerGroup", managerGroup);
			// 获取个人档案，取出其他责任医生相关信息初始化责任医生及管辖机构
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> hrMap = hrModel.getHealthRecordByEmpiId(empiId);
			if (hrMap != null) {
				thrMap.put("manaDoctorId", hrMap.get("manaDoctorId"));
				thrMap.put("manaUnitId", hrMap.get("manaUnitId"));
			}
			thrMap.put("criterionMark", "n");// 规范标志
			thrMap.put("status", "0");
			thrMap.put("createUser", currUserId);
			String currUserUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
			thrMap.put("createUnit", currUserUnitId);
			thrMap.put("createDate", currDate);
			thrMap.put("lastModifyUser", currUserId);
			thrMap.put("lastModifyUnit", currUserUnitId);
			thrMap.put("lastModifyDate", currDate);
			try {
				rsMap = dao.doSave("create", MDC_TumourHighRisk, thrMap, true);
			} catch (ValidateException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存肿瘤高危记录时数据验证失败！", e);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "保存肿瘤高危记录时失败！", e);
			}
			succeed = true;
		} else {
			haveTHR = true;
			// 有记录
			String THRID = (String) thrMap.get("THRID");
			// 判断是否是注销的状态
			boolean isLogout = this.isLogoutTHR(THRID);
			if (isLogout) {
				// 恢复档案
				this.revertTumourHighRiskRecord(THRID);
			}
			isLogout = this.isLogoutTHRVisit(THRID);
			if(isLogout){
				// 恢复随访计划
				VisitPlanModel vpModel = new VisitPlanModel(dao);
				vpModel.revertVisitPlan(vpModel.RECORDID, THRID, BusinessType.THR);
			}
			succeed = true;
			if (rsMap == null) {
				rsMap = new HashMap<String, Object>();
			}
			rsMap.put("THRID", THRID);
		}
		if (succeed) {
			// 转高危成功，修改肿瘤初筛性质
			if (S.isNotEmpty(screeningId)) {
				Map<String, Object> tsMap = new HashMap<String, Object>();
				tsMap.put("recordId", screeningId);
				tsMap.put("nature", TumourNature.T_HighRisk);
				tsMap.put("highRiskMark", YesNo.YES);
				TumourScreeningModel tsModel = new TumourScreeningModel(dao);
				tsModel.saveTumourScreening("update", tsMap, false);
			}
		}
		rsMap.put("succeed", succeed);
		rsMap.put("haveTHR", haveTHR);
		return rsMap;
	}

	/**
	 * @Description:获取 初筛阳性 值 (0=否，1=是；检查结果有阳性为1)
	 * @param screeningId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 下午4:42:40
	 * @Modify:
	 */
	public String getScreeningPositive(String... screeningId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(a.recordId) as spNum from ")
				.append(MDC_TumourScreeningCheckResult).append(" as a ")
				.append(" where checkResult='2' ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (screeningId.length == 1) {
			hql.append(" and screeningId=:screeningId");
			pMap.put("screeningId", screeningId[0]);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计初筛阳性检查结果失败！", e);
		}
		String screeningPositive = "0";
		if (rsMap != null) {
			long spNum = (Long) rsMap.get("spNum");
			if (spNum > 0) {
				screeningPositive = "1";
			}
		}
		return screeningPositive;
	}

	/**
	 * @Description:获取 初筛阳性 值 (0=否，1=是；检查结果有阳性为1)
	 * @param screeningId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-13 下午4:42:40
	 * @Modify:
	 */
	public String getScreeningSickness(String... screeningId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(a.recordId) as spNum from ")
				.append(MDC_TumourScreeningCheckResult).append(" as a ")
				.append(" where checkResult='3' ");
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (screeningId.length == 1) {
			hql.append(" and screeningId=:screeningId");
			pMap.put("screeningId", screeningId[0]);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "统计初筛阳性检查结果失败！", e);
		}
		String screeningSickness = "0";
		if (rsMap != null) {
			long spNum = (Long) rsMap.get("spNum");
			if (spNum > 0) {
				screeningSickness = "1";
			}
		}
		return screeningSickness;
	}
}
