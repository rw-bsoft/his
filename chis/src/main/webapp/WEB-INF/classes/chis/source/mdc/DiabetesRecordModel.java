/**
 * @(#)DiabetesModel.java Created on 2012-1-18 下午3:05:31
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
public class DiabetesRecordModel extends DiabetesModel {

	public DiabetesRecordModel(BaseDAO dao) {
		super(dao);
	}

	public static Map<String, Object> planTypes = new HashMap<String, Object>();

	public void addDiabetesRecordWorkList(Map<String, Object> m)
			throws ValidateException, PersistentDataOperationException {
		m.put("workType", WorkType.MDC_DIABETESRECORD);
		dao.doInsert(PUB_WorkList, m, false);
	}

	public void removeDiabetesRecordWorkList(String empiId)
			throws PersistentDataOperationException {
		StringBuffer hql = new StringBuffer(" delete from ").append(
				"PUB_WorkList").append(
				" where  empiId=:empiId and workType=:workType");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("workType", WorkType.MDC_DIABETESRECORD);
		dao.doUpdate(hql.toString(), parameters);
	}

	/**
	 * 注销后更改档案中“是否糖尿病”这个字段的值
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void setHealthRecordIsDiabetes(String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("EHR_HealthRecord")
				.append(" set isDiabetes=:isDiabetes where phrId =:phrId")
				.toString();
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phrId", phrId);
		paramMap.put("isDiabetes", YesNo.YES);
		try {
			dao.doUpdate(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新健康档案是否糖尿病字段错误！");
		}
	}

	/**
	 * 恢复糖尿病档案
	 * 
	 * @param phrId
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void SetDiabetesRecordNormal(String phrId, Context ctx)
			throws ModelDataOperationException {
		StringBuilder sb = new StringBuilder("update ")
				.append("MDC_DiabetesRecord")
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
					Constants.CODE_DATABASE_ERROR, "恢复糖尿病档案状态失败！");
		}
	}

	/**
	 * 更新糖尿病档案 糖尿病组别字段
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void updateDiabetesRecordDiabetesGroup(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		HashMap<String, Object> upBody = new HashMap<String, Object>();
		upBody.put("diabetesGroup", body.get("diabetesGroup"));
		upBody.put("phrId", body.get("phrId"));
		try {
			Map<String, Object> genValues = dao.doSave("update",
					MDC_DiabetesRecord, upBody, false);
			res.put("body", genValues);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新糖尿病定级信息时数据验证失败!");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新糖尿病定级信息失败!");
		}
	}

	/**
	 * 更新糖尿病记录体重信息
	 * 
	 * @param weight
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateDiabetesRecordWeight(double weight, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MDC_DiabetesRecord")
				.append(" set weight = :weight")
				.append(" where phrId = :phrId").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("weight", weight);
		parameters.put("phrId", phrId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新糖尿病档案身高体重失败！");
		}
	}

	/**
	 * 依据phrId查询糖尿病档案数
	 * 
	 * @param phrId
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getCountDiabetesRecord(String phrId, String empiId)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(
				"select count(*) as recordNum from ")
				.append(MDC_DiabetesRecord);
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
					Constants.CODE_DATABASE_ERROR, "获取糖尿病档案数失败！");
		}
		return rsMap != null ? (Long) rsMap.get("recordNum") : 0;
	}

	public List<Map<String, Object>> getRecordMedicine(String phrId)
			throws ModelDataOperationException {
		List<Object> cnd1 = CNDHelper
				.createSimpleCnd("eq", "phrId", "s", phrId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s",
				"0000000000000000");
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "recordId",
					BSCHISEntryNames.MDC_DiabetesMedicine);
			SchemaUtil.setDictionaryMessageForList(list,
					BSCHISEntryNames.MDC_DiabetesMedicine);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病档案药品失败。");
		}
		return list;
	}

	/**
	 * 保存糖尿病档案信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesRecord, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病档案数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病档案信息失败！");
		}
		return m;
	}

	/**
	 * 保存糖尿病定级信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void saveDiabetesFixGroup(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		//传日志到大数据接口（人群健康诊断） --wdl
		String curUserId = UserUtil.get(UserUtil.USER_ID);
		String curUnitId = UserUtil.get(UserUtil.MANAUNIT_ID);
		String organname = UserUtil.get(UserUtil.MANAUNIT_NAME);
		String USER_NAME = UserUtil.get(UserUtil.USER_NAME);
		
		Date curDate = new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String curDate1= sdf.format( new Date());
		int num =(int) (Math.random( )*50+50) ;
		try {
		String ip = PublicService.getIpByEthNum();	
		String ipc = InetAddress.getLocalHost().getHostAddress();
				String json="{ \n"+
			"\"orgCode\":\""+curUnitId+"\",\n"+
			"\"orgName\":\""+organname+"\",\n"+
			"\"ip\":\""+ipc+"\",\n"+
			"\"opertime\":\""+curDate1+"\",\n"+
			"\"operatorCode\":\""+curUserId+"\",\n"+
			"\"operatorName\":\""+USER_NAME+"\",\n"+
			"\"callType\":\"02\",\n"+
			"\"apiCode\":\"RQJKZD\",\n"+
			"\"operSystemCode\":\"ehr\",\n"+
			"\"operSystemName\":\"健康档案系统\",\n"+
			"\"fromDomain\":\"ehr_yy\",\n"+
			"\"toDomain\":\"ehr_mb\",\n"+
			"\"clientAddress\":\""+ipc+"\",\n"+
			"\"serviceBean\":\"esb.RQJKZD\",\n"+
			"\"methodDesc\":\"void doSaveConfirmfromhis()\",\n"+
			"\"statEnd\":\""+curDate1+"\",\n"+
			"\"stat\":\"1\",\n"+
			"\"avgTimeCost\":\""+num+"\",\n"+
			"\"request\":\"PublicService.httpURLPOSTCase(json)\",\n"+
			"\"response\":\"200\"\n"+
		          "}";	
				System.out.println(json);
            PublicService.httpURLPOSTCase(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		try {
			dao.doSave(op, MDC_DiabetesFixGroup, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病定级数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病定级信息失败！");
		}
	}

	/**
	 * 依据phrId获取糖尿病信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getDiabetesRecordByPkey(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_DiabetesRecord, phrId);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病信息失败！");
		}
		return rsMap;
	}

	public List<Map<String, Object>> findDiabetesRecordByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "createDate desc", MDC_DiabetesRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病档案记录失败！", e);
		}
		return rsList;
	}

	/**
	 * 根据empiId查询未被注销过的糖尿病档案
	 * 
	 * @param empiId
	 * @param ifSetDicInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getDiabetesByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ne", "status", "s",
				Constants.CODE_STATUS_WRITE_OFF);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

		Map<String, Object> rsInfo = null;
		try {
			rsInfo = dao.doLoad(cnd, MDC_DiabetesRecord);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病信息失败！");
		}
		return rsInfo;
	}

	public List<Map<String, Object>> getFixGroupList(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> list = null;
		try {
			list = dao.doQuery(cnd, "fixId", MDC_DiabetesFixGroup);
			SchemaUtil.setDictionaryMessageForList(list, MDC_DiabetesFixGroup);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病定转组失败。");
		}
		return list;
	}

	/**
	 * 跟新糖尿病档案随访计划表 planStatus of PUB_VisitPlan<br>
	 * 注销糖尿病档案随访计划
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void SetDiabetesVisitPlanLogout(String phrId)
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
					Constants.CODE_DATABASE_ERROR, "注销糖尿病档案随访计划失败！");
		}
	}

	/**
	 * 判断是否有糖尿病定转组信息
	 * 
	 * @param phrId
	 * @return true(有) false(无)
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> isHasDiabetesFixGroup(String phrId,
			String fixType) throws ModelDataOperationException {
		StringBuffer fhql = new StringBuffer(
				"select diabetesGroup as diabetesGroup from ").append(
				MDC_DiabetesFixGroup).append(" where phrId =:phrId ");
		if (StringUtils.isNotBlank(fixType)) {
			fhql.append(" and fixType = :fixType");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		parameters.put("fixType", fixType);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(fhql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询糖尿病定转组信息失败！");
		}
		if (rsList != null && rsList.size() > 0) {
			map.put("hasFixGroup", true);
			map.put("diabetesGroup", rsList.get(0).get("diabetesGroup"));
		} else {
			map.put("hasFixGroup", false);
		}
		return map;
	}

	/**
	 * 获取糖尿病档案是否有未完成的随访计划
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
	 * 恢复糖尿病随访计划
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
	 * 生成糖尿病随访计划
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
					Constants.CODE_DATABASE_ERROR, "生成糖尿病随访计划失败！", e);
		}
		// 生成计划以后取回planTypeCode
		if (null != planType) {
			String planTypeCode = planType.getPlanTypeCode();
			if (planTypeCode == null) {
				planTypeCode = (String) app.getProperty(BusinessType.TNB
						+ "_planTypeCode");
			}
			this.updateDiabetesPlanTypeCode(planTypeCode,
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
	 * 更新糖尿病计划类型
	 * 
	 * @param planTypeCode
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void updateDiabetesPlanTypeCode(String planTypeCode, String phrId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MDC_DiabetesRecord")
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
				.append("MDC_DiabetesFixGroup")
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
	 * 保存糖尿病服务情况
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDiabetesMedicine(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesMedicine, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病服务数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病服务信息失败！");
		}
		return m;
	}

	/**
	 * 恢复糖尿病档案
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void revertDiabetesRecord(String phrId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_DiabetesRecord")
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复糖尿病档案失败！", e);
		}
	}

	/**
	 * 注销糖尿病档案
	 * 
	 * @param phrId
	 * @param cancellationReason
	 * @param deadReason
	 * @throws ModelDataOperationException
	 */
	public void logoutDiabetesRecord(String phrId, String cancellationReason,
			String deadReason) throws ModelDataOperationException {
		String userId = UserUtil.get(UserUtil.USER_ID);
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_DiabetesRecord").append(" set status = :status, ")
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
					ServiceCode.CODE_DATABASE_ERROR, "注销糖尿病档案失败！", e);
		}
	}

	/**
	 * 通过phrId获取糖尿病定转组信息
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFixGroupListByPhrId(String phrId)
			throws ModelDataOperationException {
		List<Map<String, Object>> list = null;
		StringBuilder hql = new StringBuilder(" from ").append(
				MDC_DiabetesFixGroup).append(
				" where phrId =:phrId order by fixDate desc");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		try {
			list = dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病定转组失败。");
		}
		return list;
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

	public String getDiabetesGroupByConfig(String diabetesType, double fbs,
			double pbs) throws ModelDataOperationException {
		Application app = null;
		try {
			app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病组别失败。");
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

	@SuppressWarnings({ "rawtypes", "null" })
	public void saveDiabetesYearAssess(VisitPlanCreator visitPlanCreator,
			Context ctx) throws ModelDataOperationException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		UserRoleToken urt = UserRoleToken.getCurrent();
		String startMonth = smm.getSystemConfigData("diabetesStartMonth");
		String endMonth = smm.getSystemConfigData("diabetesEndMonth");
		String assessYearCon = smm.getSystemConfigData("assessYearCon");
		String normManage = smm.getSystemConfigData("normManage");
		String normScale1 = smm.getSystemConfigData("normScale1");
		String normScale2 = smm.getSystemConfigData("normScale2");
		String normScale3 = smm.getSystemConfigData("normScale3");
		String badScore1 = smm.getSystemConfigData("badScore1");
		String badScore2 = smm.getSystemConfigData("badScore2");
		String scaleNum1 = smm.getSystemConfigData("scaleNum1");
		String scaleNum2 = smm.getSystemConfigData("scaleNum2");
		String threeTurn = smm.getSystemConfigData("threeTurn");
		List<Object> cnd = new ArrayList<Object>();
		List<Map<String, Object>> records = null;
		try {

			// 机构过滤，只对当前机构及下属机构进行年度评估
			List cnd0 = CNDHelper
					.toListCnd("['like',['$','a.manaUnitId'],['s','"
							+ urt.getManageUnitId() + "%']]");
			cnd.add(cnd0);
			// 排除注销档案，注销档案不需生成随访和转组记录
			if (assessYearCon.indexOf("1") > -1) {
				List cnd1 = CNDHelper
						.toListCnd("['eq',['$','a.status'],['s','0']]");
				cnd.add(cnd1);
			}

			// 排除新病人
			String startDate = getStartDateForYear(startMonth);
			String endDate = getEndDateForYear(endMonth);
			// 因是否新病人都需生成随访，故在循环中判断
			// if (assessYearCon.indexOf("2") > -1) {
			// List cnd1 = CNDHelper.toListCnd("['lt',['$','a.createDate'],"
			// + "['todate',['s','" + startDate
			// + "'],['s', 'yyyy-mm-dd HH24:mi:ss']]]");
			// cnd.add(cnd1);
			// }
			if (cnd.size() > 1) {
				cnd.add(0, "and");
			}
			// 获取需要评估的糖尿病档案
			String sql = "select a.phrId as phrId, a.empiId as empiId,a.manaDoctorId as manaDoctorId from "
					+ MDC_DiabetesRecord
					+ " a where "
					+ ExpressionProcessor.instance().toString(cnd);
			records = dao.doQuery(sql, null);
			// 根据年度评估维护的规范管理标准判断是否进行评估，获取空腹血糖、餐后血糖
			Map<String, Object> groupMap = null;
			sql = "select fbs as fbs,pbs as pbs,diabetesGroup as diabetesGroup,"
					+ "fixDate as fixDate,oldGroup as oldGroup "
					+ "from MDC_DiabetesFixGroup where empiId=:empiId "
					+ "and createDate=(select max(createDate) from MDC_DiabetesFixGroup"
					+ " where empiId=:empiId)";
			String sql1 = "select count(planId) as count from PUB_VisitPlan where empiId=:empiId "
					+ "and businessType='2' and planDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and planDate<=to_date('"
					+ endDate + "','yyyy-mm-dd HH24:mi:ss')";
			String sql2 = "select planDate as planDate from PUB_VisitPlan where empiId=:empiId "
					+ "and businessType='2' "
					+ "and planDate=(select max(planDate) from PUB_VisitPlan"
					+ " where empiId=:empiId and businessType='2' and planDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and planDate<=to_date('"
					+ endDate + "','yyyy-mm-dd HH24:mi:ss'))";
			String sql3 = "select recordId from MDC_DiabetesYearAssess "
					+ "where empiId=:empiId and to_char(inputDate,'yyyy')=:year";
			String sql4 = "select fbs as fbs,pbs as pbs,visitDate as visitDate from MDC_DiabetesVisit "
					+ "where empiId=:empiId and visitDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and visitDate<=to_date('"
					+ endDate
					+ "','yyyy-mm-dd HH24:mi:ss') order by visitDate desc";
			String sql5 = "select superDiagnose as superDiagnose from MDC_DiabetesVisit "
					+ "where empiId=:empiId and registerDate>=to_date('"
					+ startDate
					+ "','yyyy-mm-dd HH24:mi:ss') and registerDate<=to_date('"
					+ endDate + "','yyyy-mm-dd HH24:mi:ss')";
			String sql6 = "select empiId as empiId from MDC_DiabetesRecord where empiId=:empiId and createDate<to_date('"
					+ startDate + "','yyyy-mm-dd HH24:mi:ss')";
			String sql7 = "select empiId as empiId from MDC_DiabetesRecord where empiId=:empiId and status='0'";
			Map<String, Object> groupPara = new HashMap<String, Object>();
			List<Map<String, Object>> list = null;
			List<Map<String, Object>> ogtt = null;
			List<Map<String, Object>> mList = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			for (Map<String, Object> map : records) {
				// map：年度评估参数
				// 转组参数
				Map<String, Object> group = new HashMap<String, Object>();
				group.putAll(map);
				// 随访计划生成参数
				HashMap<String, Object> fixJsonReq = new HashMap<String, Object>();
				fixJsonReq.put("nextYear", true);
				fixJsonReq.putAll(map);
				String empiId = (String) map.get("empiId");

				groupPara.put("empiId", empiId);
				groupPara.put("year", sdf.format(new Date()));
				// 判断是否已经进行过该年度的年度评估，如是者跳过
				list = dao.doSqlQuery(sql3, groupPara);
				if (list != null && list.size() > 0) {
					continue;
				}
				groupPara.remove("year");
				// 查询最后一次转组记录以作参考
				groupMap = dao.doSqlLoad(sql, groupPara);
				groupMap = setDictionaryMessageForListFromSQL(groupMap,
						MDC_DiabetesFixGroup);
				map.putAll(groupMap);
				String diabetesGroup = (String) groupMap.get("diabetesGroup");
				int controlBadCount = 0;// 该病人控制不良数
				boolean needTurn = false;
				String newGroup = diabetesGroup;
				// 查询本年度已做的随访
				list = dao.doSqlQuery(sql4, groupPara);
				int visitCount = list.size();
				map.put("visitCount", visitCount);// 随访次数
				map.put("normManage", "1");
				map.put("diabetesGroup", newGroup);
				map.put("oldGroup", diabetesGroup);
				map.put("fixDate", new Date());
				Date lastVisitDate = null;
				for (Map<String, Object> r : list) {
					double fbs = paresToDouble(r.get("FBS") + "");
					double pbs = paresToDouble(r.get("PBS") + "");
					group.put("fbs", fbs);
					group.put("pbs", pbs);
					lastVisitDate = (Date) r.get("VISITDATE");
					if (r.get("PBS") != null) {
						if (pbs > paresToDouble(badScore2)) {
							controlBadCount++;
						}
					} else {
						if (fbs > paresToDouble(badScore1)) {
							controlBadCount++;
						}
					}
				}

				// 如果注销档案也需评估，判断档案是否注销，是则只生成年度评估，否则继续判断
				if (assessYearCon.indexOf("1") == -1) {
					mList = dao.doSqlQuery(sql7, groupPara);
					if (mList == null || mList.size() == 0) {
						saveAssessment(map);
						continue;
					}
				}
				group.put("controlResult", "2");
				group.put("fixType", "6");
				group.put("fixDate", new Date());
				String nextDate = "";
				Date lastPlanDate = null;
				// 查询本年度最后一条计划日期以便确定下一年度计划
				mList = dao.doSqlQuery(sql2, groupPara);
				if (mList != null && mList.size() > 0) {
					lastPlanDate = (Date) mList.get(0).get("PLANDATE");
					nextDate = getNextVisitDate(diabetesGroup, lastPlanDate);
				} else {
					nextDate = startDate.substring(0, 10).replace(
							startDate.substring(0, 4),
							BSCHISUtil.parseToInt(startDate.substring(0, 4))
									+ 1 + "");
				}

				fixJsonReq.put("fixType", "1");
				fixJsonReq.put("instanceType", BusinessType.TNB);
				fixJsonReq.put("groupCode", diabetesGroup);
				fixJsonReq.put("fixGroupDate", groupMap.get("fixDate"));
				fixJsonReq.put("fbs", group.get("fbs"));
				fixJsonReq.put("nextDate", nextDate);
				fixJsonReq.put("adverseReactions", "");
				fixJsonReq.put("lastPlanDate", lastPlanDate);
				fixJsonReq.put("lastVisitDate", lastVisitDate);
				fixJsonReq.put("visitResult", VisitResult.SATISFIED);
				fixJsonReq.put("taskDoctorId", fixJsonReq.get("manaDoctorId"));
				if (assessYearCon.indexOf("2") > -1) {
					// 如果新病人不评估，判断是否新病人,是者只生成随访计划，否者继续判断
					mList = dao.doSqlQuery(sql6, groupPara);
					if (mList != null && mList.size() > 0) {
						map.put("normManage", "3");
						saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
						continue;
					}
				}

				// 查询本年度计划随访数
				Map<String, Object> allPlan = dao.doSqlLoad(sql1, groupPara);
				int allCount = Integer.parseInt(allPlan.get("COUNT") + "");
				// 如果本年度随访数为0或者随访计划数为0，则生成随访
				if (visitCount == 0 || allCount == 0) {
					saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
					continue;
				}
				// 根据原组别和控制情况和一二三组转组标准判断该病人是否需要转组以及新组别
				if ((controlBadCount / visitCount) * 100 > paresToDouble(scaleNum2)
						&& "02".equals(diabetesGroup)) {
					needTurn = true;
					newGroup = "01";
					group.put("controlResult", "1");
				} else if (((visitCount - controlBadCount) / visitCount) * 100 > paresToDouble(scaleNum1)
						&& "01".equals(diabetesGroup)) {
					needTurn = true;
					newGroup = "02";
				} else if (threeTurn.indexOf("2") > -1
						&& "03".equals(diabetesGroup) && visitCount > 0
						&& list.get(visitCount - 1) != null) {
					newGroup = getDiabetesGroupByConfig("4", paresToDouble(list
							.get(visitCount - 1).get("FBS") + ""),
							paresToDouble(list.get(visitCount - 1).get("PBS")
									+ ""));
					if (!newGroup.equals(diabetesGroup)) {
						needTurn = true;
					}
				} else if (threeTurn.indexOf("1") > -1
						&& "03".equals(diabetesGroup)) {
					ogtt = dao.doSqlQuery(sql5, groupPara);
					if (ogtt != null && ogtt.size() > 0) {
						for (Map<String, Object> m : ogtt) {
							String superDiagnose = (String) m
									.get("SUPERDIAGNOSE");
							if ("y".equals(superDiagnose)) {
								needTurn = true;
								newGroup = "02";
								break;
							}
						}
					}
				}
				if (mList != null && mList.size() > 0) {
					nextDate = getNextVisitDate(newGroup, lastPlanDate);
				} else {
					nextDate = startDate.substring(0, 10).replace(
							startDate.substring(0, 4),
							BSCHISUtil.parseToInt(startDate.substring(0, 4))
									+ 1 + "");
				}
				fixJsonReq.put("nextDate", nextDate);
				// 如果病人属于该原组别并且未维护该组别随访最低标准，
				// 则全部评估并生成随访计划，需转组的转组
				if ((normManage.indexOf("1") == -1 && "01"
						.equals(diabetesGroup))
						|| (normManage.indexOf("2") == -1 && "02"
								.equals(diabetesGroup))
						|| (normManage.indexOf("3") == -1 && "03"
								.equals(diabetesGroup))) {
					if (needTurn == true) {
						group.put("diabetesGroup", newGroup);
						group.put("oldGroup", diabetesGroup);
						map.put("diabetesGroup", newGroup);
						map.put("oldGroup", diabetesGroup);
						map.put("fixDate", new Date());
						fixJsonReq.put("groupCode", newGroup);
						fixJsonReq.put("fixGroupDate", new Date());
						saveDiabetesGroup(group);
					}
					saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
					saveAssessment(map);
					continue;
				}

				double scaleNum = paresToDouble(visitCount + "")
						/ paresToDouble(allCount + "") * 100;// 随访比例
				// 如果未规范管理的也要年度评估，则全部评估并生成随访计划，需转组的转组
				// 否则规范的要评估，不规范的只生成随访
				if (assessYearCon.indexOf("3") == -1) {
					// 判断随访比例是否大于该组别所维护的最低随访比例，
					// 如是则评估并生成随访计划，需转组的转组
					if (("01".equals(diabetesGroup) && scaleNum > Double
							.parseDouble(normScale1))
							|| ("02".equals(diabetesGroup) && scaleNum > Double
									.parseDouble(normScale2))
							|| ("03".equals(diabetesGroup) && scaleNum > Double
									.parseDouble(normScale3))) {
						map.put("normManage", "1");
					} else {
						map.put("normManage", "2");
					}
					if (needTurn == true) {
						group.put("diabetesGroup", newGroup);
						group.put("oldGroup", diabetesGroup);
						map.put("diabetesGroup", newGroup);
						map.put("oldGroup", diabetesGroup);
						map.put("fixDate", new Date());
						fixJsonReq.put("groupCode", newGroup);
						fixJsonReq.put("fixGroupDate", new Date());
						saveDiabetesGroup(group);
					}
					saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
					saveAssessment(map);
					continue;
				} else {
					if (("01".equals(diabetesGroup) && scaleNum > Double
							.parseDouble(normScale1))
							|| ("02".equals(diabetesGroup) && scaleNum > Double
									.parseDouble(normScale2))
							|| ("03".equals(diabetesGroup) && scaleNum > Double
									.parseDouble(normScale3))) {
						map.put("normManage", "1");
						if (needTurn == true) {
							group.put("diabetesGroup", newGroup);
							group.put("oldGroup", diabetesGroup);
							map.put("diabetesGroup", newGroup);
							map.put("oldGroup", diabetesGroup);
							map.put("fixDate", new Date());
							fixJsonReq.put("groupCode", newGroup);
							fixJsonReq.put("fixGroupDate", new Date());
							saveDiabetesGroup(group);
						}
						saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
						saveAssessment(map);
						continue;
					} else {
						saveNextYearVisitPlan(fixJsonReq, visitPlanCreator, ctx);
					}
				}
			}
		} catch (ExpException e1) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取表达式失败。");
		} catch (ParseException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取日期失败。");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病档案失败。");
		}

	}

	@SuppressWarnings("unchecked")
	public String getNextVisitDate(String diabetesGroup, Date planDate) {
		Map<String, Object> record = (Map<String, Object>) planTypes
				.get(diabetesGroup);
		Calendar cDay1 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cDay1.setTime(planDate);
		if (record != null) {
			int frequency =BSCHISUtil.parseToInt(record.get("frequency")+"");
			int cycle = (Integer) record.get("cycle");
			cDay1.add(frequency, cycle);
			Date date = new Date();
			date.setTime(cDay1.getTimeInMillis());
			return sdf.format(date);
		}
		Schema sc = null;
		String itemid = "";
		try {
			sc = SchemaController.instance().get(ADMIN_DiabetesConfig);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if ("03".equals(diabetesGroup)) {
			itemid = "planType3";
		} else if ("02".equals(diabetesGroup)) {
			itemid = "planType2";
		} else {
			itemid = "planType1";
		}
		String exp = null;
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem si : items) {
			if (itemid.equals(si.getId())) {
				exp = (String) si.getProperty("expression");
			}
		}
		String planTypeCode = null;
		if (exp != null) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			String sql = "select planTypeCode as planTypeCode from "
					+ PUB_PlanInstance
					+ " where instanceType='2' and expression=:expression";
			try {
				parameters.put("expression", exp);
				List<Map<String, Object>> list = dao.doQuery(sql, parameters);
				if (list != null && list.size() > 0) {
					planTypeCode = (String) list.get(0).get("planTypeCode");
				}
				record = new HashMap<String, Object>();
				if (planTypeCode != null) {
					record = dao.doLoad(PUB_PlanType, planTypeCode);
				}
				planTypes.put(diabetesGroup, record);
				int frequency = BSCHISUtil.parseToInt(record.get("frequency")+"");
				int cycle = (Integer) record.get("cycle");
				cDay1.add(frequency, cycle);
			} catch (PersistentDataOperationException e) {
				e.printStackTrace();
			}
		}
		Date date = new Date();
		date.setTime(cDay1.getTimeInMillis());
		return sdf.format(date);
	}

	/**
	 * 生成糖尿病下一年度随访计划
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
					"chis.application.dbs.schemas.MDC_DiabetesYearAssess", map,
					true);
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 保存转组记录
	 * 
	 * @param group
	 */
	private void saveDiabetesGroup(Map<String, Object> group) {
		UserRoleToken urt = UserRoleToken.getCurrent();
		group.put("createUser", urt.getUserId());
		group.put("createUnit", urt.getManageUnitId());
		group.put("createDate", new Date());
		group.put("lastModifyUser", urt.getUserId());
		group.put("lastModifyUnit", urt.getManageUnitId());
		group.put("lastModifyDate", new Date());
		try {
			dao.doSave("create", MDC_DiabetesFixGroup, group, true);
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

	public void setDiabetesRecordEnd(HashMap<String, Object> endBody)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append("MDC_DiabetesRecord set ")
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
					Constants.CODE_DATABASE_ERROR, "更新糖尿病档案转归失败！");
		}

	}

	public void setLastVisitEffect(String phrId, String visitEffect,
			String noVisitReason) throws ModelDataOperationException {
		String sql = "update MDC_DiabetesVisit set visitEffect=:visitEffect,"
				+ "noVisitReason=:noVisitReason where phrId=:phrId "
				+ "and visitDate=(select max(visitDate) from MDC_DiabetesVisit"
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
					+ "and visitId=(select max(visitId) from MDC_DiabetesVisit"
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

	public Map<String, Object> saveDiabetesRecordEnd(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesRecordEnd, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病档案数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病档案信息失败！");
		}
		return m;
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
