/**
 * @(#)MZFModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.dic.BusinessType;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.PlanMode;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.dic.VisitResult;
import chis.source.dic.WorkType;
import chis.source.dic.YesNo;
import chis.source.pub.PublicService;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import chis.source.visitplan.PlanType;
import chis.source.visitplan.VisitPlanCreator;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MZFRecordModel extends MZFModel {

	public MZFRecordModel(BaseDAO dao) {
		super(dao);
	}

	public static Map<String, Object> planTypes = new HashMap<String, Object>();

	public void addMZFRecordWorkList(Map<String, Object> m)
			throws ValidateException, PersistentDataOperationException {
		m.put("workType", WorkType.MDC_DIABETESRECORD);
		dao.doInsert(PUB_WorkList, m, false);
	}

	public void removeMZFRecordWorkList(String empiId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer(" delete from ").append(
				"PUB_WorkList").append(
				" where  empiId=:empiId and workType=:workType");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("workType", WorkType.MDC_DIABETESRECORD);
		dao.doUpdate(hql.toString(), parameters);
	}
	
	public void removeMZFRecord(String phrId)
			throws PersistentDataOperationException {
		dao.doRemove("phrId", phrId, MZF_DocumentRecord);
		dao.doRemove("phrId", phrId, MZF_VisitRecord);	
	}
	/**
	 * 注销后更改档案中“是否慢阻肺”这个字段的值
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void setHealthRecordIsMZF(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set isMZF=:isMZF where phrId =:phrId")
				.toString();
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phrId", phrId);
		paramMap.put("isMZF", YesNo.YES);
		try {
			dao.doUpdate(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新健康档案是否慢阻肺字段错误！");
		}
	}

	/**
	 * 恢复慢阻肺档案
	 * 
	 * @param phrId
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void SetMZFRecordNormal(String phrId, Context ctx)
			throws ModelDataOperationException {
		StringBuilder sb = new StringBuilder("update ")
				.append("MZF_DocumentRecord")
				.append(" set cancellationReason = :cancellationReason")
				.append(", cancellationUser = :cancellationUser")
				.append(", deadReason = :deadReason")
				.append(", cancellationCheckUnit = :cancellationCheckUnit")
				.append(", cancellationCheckUser = :cancellationCheckUser")
				.append(", cancellationDate = :cancellationDate")
				.append(", cancellationCheckDate = :cancellationCheckDate")
				.append(", status = :status")
				.append(", lastModifyUser = :lastModifyUser")
				.append(", lastModifyDate = :lastModifyDate ")
				.append(" where phrId = :phrId and status = :status1");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cancellationReason", "");
		paramMap.put("deadReason", "");
		paramMap.put("cancellationUser", "");
		paramMap.put("cancellationCheckUnit", "");
		paramMap.put("cancellationCheckUser", "");
		paramMap.put("cancellationDate", "");
		paramMap.put("cancellationCheckDate", "");
		paramMap.put("lastModifyUser", UserRoleToken.getCurrent().getUserId());
		paramMap.put("lastModifyDate", new Date());
		paramMap.put("status", "" + Constants.CODE_STATUS_NORMAL);
		paramMap.put("phrId", phrId);
		paramMap.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(sb.toString(), paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复慢阻肺档案状态失败！");
		}
	}

	/**
	 * 更新慢阻肺档案 慢阻肺组别字段
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void updateMZFRecordMZFGroup(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		HashMap<String, Object> upBody = new HashMap<String, Object>();
		upBody.put("diabetesGroup", body.get("diabetesGroup"));
		upBody.put("phrId", body.get("phrId"));
		try {
			Map<String, Object> genValues = dao.doSave("update",
					MZF_DocumentRecord, upBody, false);
			res.put("body", genValues);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新慢阻肺定级信息时数据验证失败!");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新慢阻肺定级信息失败!");
		}
	}

	/**
	 * 更新慢阻肺记录体重信息
	 * 
	 * @param weight
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateMZFRecordWeight(double weight, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MZF_DocumentRecord")
				.append(" set weight = :weight")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("weight", weight);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新慢阻肺档案身高体重失败！");
		}
	}

	/**
	 * 依据phrId查询慢阻肺档案数
	 * 
	 * @param phrId
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getCountMZFRecord(String phrId, String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(*) as recordNum from ")
				.append(MZF_DocumentRecord);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		if (phrId != null && !"".equals(phrId)) {
			hql.append(" where phrId =:phrId");
			paraMap.put("phrId", phrId);
		} else {
			hql.append(" where empiId =:empiId");
			paraMap.put("empiId", empiId);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺档案数失败！");
		}
		return rsMap != null ? (Long) rsMap.get("recordNum") : 0;
	}

	/**
	 * 保存慢阻肺档案信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveMZFRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MZF_DocumentRecord, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺档案数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存慢阻肺档案信息失败！");
		}
		return m;
	}

	/**
	 * 依据phrId获取慢阻肺信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMZFRecordByPkey(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MZF_DocumentRecord, phrId);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺信息失败！");
		}
		return rsMap;
	}

	public List<Map<String, Object>> findMZFRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "createDate desc", MZF_DocumentRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺档案记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 根据empiId查询未被注销过的慢阻肺档案
	 * 
	 * @param empiId
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getMZFByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

		Map<String, Object> rsInfo = null;
		try {
			rsInfo = dao.doLoad(cnd, MZF_DocumentRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺信息失败！");
		}
		return rsInfo;
	}

	/**
	 * 跟新慢阻肺档案随访计划表 planStatus of PUB_VisitPlan<br>
	 * 注销慢阻肺档案随访计划
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void SetMZFVisitPlanLogout(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus")
				.append(" where recordId=:recordId and businessType=:businessType")
				.append(" and planStatus = :planStatus1").toString();
		HashMap<String, Object> js = new HashMap<String, Object>();
		js.put("recordId", phrId);
		js.put("businessType", BusinessType.TNB);
		js.put("planStatus", PlanStatus.WRITEOFF);
		js.put("planStatus1", PlanStatus.NEED_VISIT);
		try {
			dao.doUpdate(hql, js);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "注销慢阻肺档案随访计划失败！");
		}
	}

	/**
	 * 获取慢阻肺档案是否有未完成的随访计划
	 * 
	 * @param recordId
	 * @return true(有) false(无)
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getUnfinishedVisitPlan(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select * from ").append("PUB_VisitPlan")
				.append(" where recordId =:recordId and planDate >= :planDate")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", recordId);
		parameters.put("planDate", new Date());
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否有未完成的随访计划失败！");
		}

		return rsList;
	}

	/**
	 * 恢复慢阻肺随访计划
	 * 
	 * @param recordId
	 * @throws ModelDataOperationException
	 */
	public void setVisitPlanNormal(String recordId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus= '0' where recordId=:recordId and planType=:planType")
				.append(" and planStatus= '9'").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("recordId", recordId);
		parameters.put("planType", BusinessType.TNB);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "恢复随访计划失败！");
		}
	}

	/**
	 * 生成慢阻肺随访计划
	 * 
	 * @param body
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String createVisitPlan(Map<String, Object> body,
			VisitPlanCreator visitPlanCreator, Context ctx)
			throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e1) {
			throw new ModelDataOperationException(e1);
		}
		String planMode = (String) app.getProperty(BusinessType.TNB
				+ "_planMode");
		if (planMode == null || planMode.equals("")) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "未配置随访计划生成方式，请到系统配置界面中配置。");
		}
		// 生成随访计划参数
		HashMap<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.put("recordId", body.get("phrId"));
		reqBody.put("fixType", body.get("fixType"));
		reqBody.put("businessType", BusinessType.TNB);
		reqBody.put("empiId", body.get("empiId"));
		reqBody.put("groupCode", body.get("groupCode"));
		reqBody.put("fixGroupDate", body.get("fixGroupDate"));
		reqBody.put("adverseReactions", body.get("adverseReactions"));
		reqBody.put("fbs", body.get("fbs"));
		reqBody.put("nextDate", body.get("nextDate"));
		reqBody.put("sn", body.get("sn"));
		reqBody.put("lastVisitDate", body.get("lastVisitDate"));
		reqBody.put("lastPlanDate", body.get("lastPlanDate"));
		reqBody.put("complicationChange", body.get("complicationChange"));
		reqBody.put("visitResult", body.get("visitResult"));
		reqBody.put("lastestData", body.get("lastestData"));
		reqBody.put("taskDoctorId", body.get("taskDoctorId"));
		reqBody.put("visitMeddle", body.get("visitMeddle"));
		if (PlanMode.BY_RESERVED.equals(planMode)) {
			reqBody.put("reserveDate", body.get("fixGroupDate"));
		}
		boolean nextYear = false;
		if (body.get("nextYear") != null) {
			nextYear = (Boolean) body.get("nextYear");
		}
		reqBody.put("$sectionCutOffDate",
				BSCHISUtil.getSectionCutOffDate("diabetesEndMonth", nextYear));
		reqBody.put("$planMode", planMode);
		// 生成随访
		PlanType planType = null;
		try {
			planType = visitPlanCreator.create(reqBody, ctx);
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "生成慢阻肺随访计划失败！", e);
		}
		// 生成计划以后取回planTypeCode
		if (null != planType) {
			String planTypeCode = planType.getPlanTypeCode();
			if (planTypeCode == null) {
				planTypeCode = (String) app.getProperty(BusinessType.TNB
						+ "_planTypeCode");
			}
			this.updateMZFPlanTypeCode(planTypeCode,
					(String) body.get("phrId"));
		}
		return null;
	}

	/**
	 * 是否需要生成随访 年度评估模式除首次生成外不生成计划，随访平估都要生成
	 * 
	 * @param empiId
	 * @param startDate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public boolean needCreatePlanNoAsses(String empiId, Date endDate)
			throws ModelDataOperationException {
		try {
			List<Map<String, Object>> rsList = null;
			String hql = new StringBuffer(" from ").append(PUB_VisitPlan)
					.append(" where empiId =:empiId and businessType='2'")
					.append(" and planDate >= :planDate and planstatus='1' ").toString();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("empiId", empiId);
			param.put("planDate", endDate);
			rsList = dao.doQuery(hql, param);
			if (rsList == null || rsList.size() == 0) {
				return true;
			}
			Application app;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e) {
				throw new ModelDataOperationException(e);
			}
			String assessType = (String) app.getProperty("assessType");
			if ("2".equals(assessType)) {
				return false;
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度随访记录失败！", e);
		}

		return true;
	}

	/**
	 * 更新慢阻肺计划类型
	 * 
	 * @param planTypeCode
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateMZFPlanTypeCode(String planTypeCode, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MZF_DocumentRecord")
				.append(" set planTypeCode=:planTypeCode where phrId=:phrId")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("planTypeCode", planTypeCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新档案里的计划类型失败！");
		}
	}

	/**
	 * 更新定转组中的血糖值
	 * 
	 * @param phrId
	 * @param fbs
	 * @param bps
	 * @throws ModelDataOperationException
	 */
	public void updateDiatebetesFixGroupGLYX(String phrId, double fbs,
			double pbs, String diabetesGroup)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MDC_MZFFixGroup")
				.append(" set fbs= :fbs ,pbs = :pbs,diabetesGroup=:diabetesGroup, ")
				.append(" fixDate= :fixDate ,createUser = :createUser ")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("fbs", fbs);
		parameters.put("pbs", pbs);
		parameters.put("diabetesGroup", diabetesGroup);
		parameters.put("fixDate", new Date());
		parameters.put("createUser", UserRoleToken.getCurrent().getUserId());
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新定转组中的血糖值失败！");
		}
	}

	/**
	 * 恢复慢阻肺档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertMZFRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MZF_DocumentRecord")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationCheckUser = :cancellationCheckUser, ")
				.append(" cancellationCheckDate = :cancellationCheckDate, ")
				.append(" cancellationCheckUnit = :cancellationCheckUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId ")
				.append(" and status = :status1").toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		parameters.put("lastModifyUser", userId);
		parameters.put("lastModifyDate", new Date());
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("cancellationCheckUser", "");
		parameters.put("cancellationCheckDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationCheckUnit", "");
		parameters.put("cancellationUser", "");
		parameters.put("cancellationDate", BSCHISUtil.toDate(""));
		parameters.put("cancellationUnit", "");
		parameters.put("cancellationReason", "");
		parameters.put("deadReason", "");
		parameters.put("phrId", phrId);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复慢阻肺档案失败！", e);
		}
	}

	/**
	 * 注销慢阻肺档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutMZFRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		String userId = UserUtil.get(UserUtil.USER_ID);
		StringBuffer hql = new StringBuffer("update ")
				.append("MZF_DocumentRecord").append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason, ")
				.append(" deadReason = :deadReason ")
				.append(" where phrId = :phrId and (cancellationReason<>'6' ")
				.append(" or cancellationReason is null) ")
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
		parameters.put("deadReason", deadReason);
		parameters.put("phrId", phrId);
		//注销随访
		String zxsfsql="update PUB_VisitPlan set planStatus='9' where recordId=:phrId " +
				" and planStatus in ('0','3') and businessType='2' ";
		try {
			dao.doUpdate(hql.toString(), parameters);
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("phrId", phrId);
			dao.doUpdate(zxsfsql, p);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销慢阻肺档案失败！", e);
		}
	}


	/**
	 * 同步个人既往史
	 * 
	 * @param record
	 * @param ctx
	 * @throws PersistentDataOperationException
	 * @throws ValidateException
	 * @throws ControllerException
	 */
	public void updatePastHistory(Map<String, Object> record)
			throws PersistentDataOperationException, ValidateException,
			ControllerException {
		Dictionary dic = DictionaryController.instance().get(
				"chis.dictionary.pastHistory");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", record.get("empiId"));
		String hql = "";
		hql = "select count(*) as cnt from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and pastHisTypeCode=:pastHisTypeCode";
		parameters.put("pastHisTypeCode", PastHistoryCode.SCREEN);
		parameters.put("diseaseCode", PastHistoryCode.PASTHIS_SCREEN_DIABETES);
		List<?> l = dao.doQuery(hql, parameters);
		if ((Long) ((Map<?, ?>) l.get(0)).get("cnt") == 0) {
			hql = "delete from EHR_PastHistory where empiId=:empiId and diseaseCode=:diseaseCode and pastHisTypeCode=:pastHisTypeCode";
			parameters = new HashMap<String, Object>();
			parameters.put("empiId", record.get("empiId"));
			parameters.put("pastHisTypeCode", PastHistoryCode.SCREEN);
			parameters.put("diseaseCode",
					PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE);
			dao.doUpdate(hql, parameters);

			String diseaseText = dic.getItem(
					PastHistoryCode.PASTHIS_SCREEN_DIABETES).getText();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("empiId", record.get("empiId"));
			m.put("diseaseCode", PastHistoryCode.PASTHIS_SCREEN_DIABETES);
			m.put("diseaseText", diseaseText);
			m.put("confirmDate", record.get("diagnosisDate"));
			m.put("pastHisTypeCode", PastHistoryCode.SCREEN);
			m.put("recordUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
			m.put("recordDate",
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			dao.doSave("create", EHR_PastHistory, m, false);
		}

	}

	public String getMZFGroupByConfig(String diabetesType, double fbs,
			double pbs) throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取慢阻肺组别失败。");
		}
		String manageType = (String) app.getProperty("2_manageType");
		String DBS_bloodNum11 = (String) app.getProperty("DBS_bloodNum11");
		String DBS_bloodNum12 = (String) app.getProperty("DBS_bloodNum12");
		String DBS_bloodNum21 = (String) app.getProperty("DBS_bloodNum21");
		String DBS_bloodNum22 = (String) app.getProperty("DBS_bloodNum22");
		String DBS_bloodNum31 = (String) app.getProperty("DBS_bloodNum31");
		String DBS_bloodNum32 = (String) app.getProperty("DBS_bloodNum32");
		String DBS_bloodNum41 = (String) app.getProperty("DBS_bloodNum41");
		String DBS_bloodNum42 = (String) app.getProperty("DBS_bloodNum42");
		String diabetesGroup = "";
		if ("1".equals(manageType)) {
			diabetesGroup = "01";
			return diabetesGroup;
		}
		if (!"1".equals(diabetesType) && !"2".equals(diabetesType)
				&& !"3".equals(diabetesType)) {
			diabetesGroup = "03";
			return diabetesGroup;
		}
		if (fbs != 0) {
			if (fbs >= paresToDouble(DBS_bloodNum11)
					&& fbs < paresToDouble(DBS_bloodNum12)) {
				diabetesGroup = "01";
			} else if (fbs >= paresToDouble(DBS_bloodNum31)
					&& fbs <= paresToDouble(DBS_bloodNum32)) {
				diabetesGroup = "02";
			}
		}
		if (pbs != 0) {
			if (pbs >= paresToDouble(DBS_bloodNum21)
					&& pbs < paresToDouble(DBS_bloodNum22)) {
				diabetesGroup = "01";
			} else if (pbs >= paresToDouble(DBS_bloodNum41)
					&& pbs <= paresToDouble(DBS_bloodNum42)) {
				diabetesGroup = "02";
			}
		}
		if ("".equals(diabetesGroup)) {
			diabetesGroup = "01";
		}
		return diabetesGroup;
	}

	public double paresToDouble(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Double.parseDouble(value);
	}

	/**
	 * 生成慢阻肺下一年度随访计划
	 * 
	 * @param fixJsonReq
	 * @param visitPlanCreator
	 * @param ctx
	 */
	private void saveNextYearVisitPlan(HashMap<String, Object> fixJsonReq,
			VisitPlanCreator visitPlanCreator, Context ctx) {
		try {
			createVisitPlan(fixJsonReq, visitPlanCreator, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存年度评估记录
	 * 
	 * @param map
	 */
	private void saveAssessment(Map<String, Object> map) {
		try {
			UserRoleToken urt = UserRoleToken.getCurrent();
			map.put("inputUser", urt.getUserId());
			map.put("inputUnit", urt.getManageUnitId());
			map.put("inputDate", new Date());
			map.put("lastModifyUser", urt.getUserId());
			map.put("lastModifyUnit", urt.getManageUnitId());
			map.put("lastModifyDate", new Date());
			dao.doSave("create",
					"chis.application.dbs.schemas.MDC_MZFYearAssess", map,
					true);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取配置的年度结束日期
	 * 
	 * @param endMonth
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public String getEndDateForYear(String endMonth) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (endMonth == null || "".equals(endMonth) || "12".equals(endMonth)) {
			source = sdf.format(new Date()) + "-12-31 23:59:59";
			return source;
		}
		int month = 11;
		if (endMonth.startsWith("0")) {
			month = Integer.parseInt(endMonth.substring(1)) - 1;
		} else {
			month = Integer.parseInt(endMonth) - 1;
		}
		Calendar cDay1 = Calendar.getInstance();
		Date date = new Date();
		date.setMonth(month);
		cDay1.setTime(date);
		int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		source = sdf.format(new Date()) + "-" + endMonth + "-" + lastDay
				+ " 23:59:59";
		return source;
	}

	/**
	 * 获取配置的年度开始日期
	 * 
	 * @param startMonth
	 * @return
	 * @throws ParseException
	 */
	public String getStartDateForYear(String startMonth) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (startMonth == null || "".equals(startMonth)
				|| "01".equals(startMonth)) {
			source = sdf.format(new Date()) + "-01-01 00:00:00";
			return source;
		}
		source = (Integer.parseInt(sdf.format(new Date())) - 1) + "-"
				+ startMonth + "-01 00:00:00";
		return source;
	}

	/**
	 * 根据schema设置字典文本数据,且该数据用sqlLoad查询得到
	 * 
	 * @param info
	 * @param schemaName
	 * @return
	 */
	public static Map<String, Object> setDictionaryMessageForListFromSQL(
			Map<String, Object> info, String schemaName) {
		if (info == null || info.size() < 1) {
			return null;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		Schema schema = null;
		try {
			schema = SchemaController.instance().get(schemaName);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		List<SchemaItem> itemList = schema.getItems();
		for (Iterator<SchemaItem> iterator = itemList.iterator(); iterator
				.hasNext();) {
			SchemaItem item = iterator.next();
			String itemKey = item.getId();
			Object keyValue = info.get(itemKey.toUpperCase());
			if (keyValue == null) {
				continue;
			}
			data.put(itemKey, keyValue);

		}
		return data;
	}

	public void setMZFRecordEnd(HashMap<String, Object> endBody)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MZF_DocumentRecord set ")
				.append("visitEffect = :visitEffect,")
				.append("noVisitReason = :noVisitReason,")
				.append("visitDate = :visitDate,endCheck=:endCheck ")
				.append(" where phrId = :phrId").toString();
		String phrId = (String) endBody.get("phrId");
		String visitEffect = (String) endBody.get("visitEffect");
		String noVisitReason = (String) endBody.get("noVisitReason");
		Date visitDate = (Date) endBody.get("visitDate");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("visitEffect", visitEffect);
		parameters.put("noVisitReason", noVisitReason);
		parameters.put("visitDate", visitDate);
		parameters.put("phrId", phrId);
		parameters.put("endCheck", "1");
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新慢阻肺档案转归失败！");
		}

	}

	public void setLastVisitEffect(String phrId, String visitEffect,
			String noVisitReason) throws ModelDataOperationException {
		String sql = "update MDC_MZFVisit set visitEffect=:visitEffect,"
				+ "noVisitReason=:noVisitReason where phrId=:phrId "
				+ "and visitDate=(select max(visitDate) from MDC_MZFVisit"
				+ " where phrId=:phrId)";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("visitEffect", visitEffect);
		parameters.put("noVisitReason", noVisitReason);
		try {
			dao.doUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新转归失败！");
		}
		if (VisitEffect.LOST.equals(visitEffect)) {
			sql = "update PUB_VisitPlan set planStatus=:planStatus "
					+ " where recordId=:recordId and businessType=:businessType "
					+ "and visitId=(select max(visitId) from MDC_MZFVisit"
					+ " where phrId=:phrId) and visitId is not null";
			parameters = new HashMap<String, Object>();
			parameters.put("recordId", phrId);
			parameters.put("phrId", phrId);
			parameters.put("planStatus", PlanStatus.LOST);
			parameters.put("businessType", BusinessType.TNB);
			try {
				dao.doUpdate(sql, parameters);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "更新转归失败！");
			}
		}

	}

	public void deleteNoVisitPlan(String empiId, Date endDate)
			throws ModelDataOperationException {
		try {
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			String hql="delete from PUB_VisitPlan where empiId ='"+empiId+"'and " +
					   "businessType='2' and to_char(planDate,'yyyy-mm-dd') >='"+sf.format(endDate)+"'" +
					   " and planStatus='0' ";
			Session con =dao.getSession();
			Transaction t=con.beginTransaction();
			t.begin();
			con.createSQLQuery(hql).executeUpdate();
			t.commit();
		} catch (Exception e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取本年度随访记录失败！", e);
		}
	}
	
	public void updatediaPastVisitPlanStatus(String empiId, Date fixdate ) throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("PUB_VisitPlan")
				.append(" set planStatus ='2' ")
				.append(" where empiId = :empiId and businessType ='2' ")
				.append(" and  to_char(planDate,'yyyy') < :year ")
				.toString();
		Calendar tempdate = Calendar.getInstance();
		tempdate.setTime(fixdate);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("empiId", empiId);
		map.put("year", ""+tempdate.get(Calendar.YEAR));
		try {
			dao.doUpdate(hql, map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新过期随访计划失败！", e);
		}
	}
	
}
