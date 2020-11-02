/**
 * @(#)PublicModel.java Created on 2012-1-12 上午9:43:35
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.HypertensionFirstCheck;
import chis.source.dic.PlanStatus;
import chis.source.dic.VisitEffect;
import chis.source.empi.EmpiModel;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PublicModel implements BSCHISEntryNames {
	private static final Logger logger = LoggerFactory
			.getLogger(PublicModel.class);

	protected BaseDAO dao = null;

	/**
	 * 
	 * @param dao
	 */
	public PublicModel(BaseDAO dao) {
		this.dao = dao;
	}

	public PublicModel(Context ctx) {
		this.dao = new BaseDAO();
	}

	/**
	 * 获取责任医生ID
	 * 
	 * @param empiId
	 * @param recordEntry
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getManaDoctor(String empiId, String recordEntry)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select manaDoctorId as manaDoctorId from ")
				.append(recordEntry)
				.append(" where empiId = :empiId and status=:status")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		String manaDoctorId = "";
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("get manaDoctorId failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取责任医生ID失败！");
		}
		if (null != rsMap) {
			manaDoctorId = (String) rsMap.get("manaDoctorId");
		}
		return manaDoctorId;
	}

	/**
	 * 取主档管辖机构
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getManaUnit(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer("select manaUnitId from ")
				.append(EHR_HealthRecord)
				.append(" where empiId=:empiId ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error("Get manaUnitId of EHR_HealthRecord with empiId = ["
					+ empiId + "] and status = ["
					+ Constants.CODE_STATUS_NORMAL + "]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "取主档管辖机构失败！");
		}
		if (rsMap == null) {
			logger.error("Get manaUnitId of EHR_HealthRecord with empiId = ["
					+ empiId + "] and status = ["
					+ Constants.CODE_STATUS_NORMAL + "]");
			throw new ModelDataOperationException(
					"Get manaUnitId of EHR_HealthRecord with empiId = ["
							+ empiId + "] and status = ["
							+ Constants.CODE_STATUS_NORMAL + "]");
		}
		return (String) rsMap.get("manaUnitId");
	}

	/**
	 * 判断是否需要进行首诊测压、高血压核实
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public int ifHypertensionFirst(String empiId)
			throws ModelDataOperationException {
		EmpiModel em = new EmpiModel(dao);
		int age = em.getAge(empiId);
		if (age == -1) {
			logger.error("Could not found empi record by empiId:" + empiId
					+ " or birthday info is not registed.");
			throw new ModelDataOperationException(
					Constants.CODE_RECORD_NOT_FOUND, "该个人编号无法找到个人信息或出生日期未填写。");
		}
		if (age < 35) {
			return HypertensionFirstCheck.DO_HYPERTENSION_NON;
		}

		// query hyertensionfisrt record
		Calendar startc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		startc.set(startc.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		endc.set(endc.get(Calendar.YEAR), 11, 31, 23, 59, 59);

		String hql = new StringBuffer("select count(*) as c from ")
				.append(MDC_HypertensionRecord)
				.append(" where empiId=:empiId and status=:status").toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		paramMap.put("status", String.valueOf(Constants.CODE_STATUS_NORMAL));
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to count of MDC_HypertensionRecord with empiId = ["
							+ empiId + "] and status = [0]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "无状态为正常的高血压记录！");
		}
		long l = ((Long) rsMap.get("c")).longValue();
		if (l > 0) {
			return HypertensionFirstCheck.DO_HYPERTENSION_NON;
		}

		String hypertensionFirstHql = new StringBuffer(
				"select diagnosisType as diagnosisType from ")
				.append(MDC_HypertensionFirst)
				.append(" where empiId = :empiId and hypertensionFirstDate>")
				.append(":startDate and hypertensionFirstDate<:endDate")
				.toString();
		paramMap.clear();
		paramMap.put("empiId", empiId);
		paramMap.put("startDate", startc.getTime());
		paramMap.put("endDate", endc.getTime());
		rsMap.clear();
		try {
			rsMap = dao.doLoad(hypertensionFirstHql, paramMap);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to get diagnosisType of MDC_HypertensionFirst", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取首诊测压核实结果失败！");
		}
		if (rsMap == null) {
			return HypertensionFirstCheck.DO_HYPERTENSION_FIRST;
		}
		String diagnosisType = (String) rsMap.get("diagnosisType");
		if (diagnosisType == null) {
			return HypertensionFirstCheck.DO_HYPERTENSION_FIRST;
		}
		if ("3".equals(diagnosisType)) {
			return HypertensionFirstCheck.DO_HYPERTENSION_NON;
		}
		if ("1".equals(diagnosisType)) {
			return HypertensionFirstCheck.DO_HYPERTENSION_CREATE;
		}
		return HypertensionFirstCheck.DO_HYPERTENSION_CHECK;
	}

	/**
	 * 获取生命周期
	 * 
	 * @param birthday
	 * @param calculateDate
	 * @param session
	 * @return lifeCycle的code，text以及年龄。
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> getLifeCycle(Date birthday, Date calculateDate)
			throws PersistentDataOperationException {
		Map<String, Object> record = new HashMap<String, Object>();
		int age = BSCHISUtil.calculateAge(birthday, calculateDate);
		List<?> cnd1 = CNDHelper.createSimpleCnd("le", "startAge", "i", age);
		List<?> cnd2 = CNDHelper.createSimpleCnd("ge", "endAge", "i", age);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> records = dao.doQuery(cnd, "", EHR_LifeCycle);
		if (records != null && records.size() > 0) {
			record = records.get(0);
		}
		record.put("age", age);
		return record;
	}

	// ##================= 档案注销、核实、恢复 操作 公共方法 ================##
	/**
	 * 注销子档时，如果是死亡注销，则填写健康档案中的死亡信息
	 * 
	 * @param jsonReq
	 * @throws ModelDataOperationException
	 */
	public void setDeadFlag(Map<String, Object> parametersMap)
			throws ModelDataOperationException {
		String sql = new StringBuffer("update ").append(EHR_HealthRecord)
				.append(" set deadFlag=:deadFlag")
				.append(",deadDate=:deadDate")
				.append(",deadReason=:deadReason")
				.append(" where phrId=:phrId").toString();

		Map<String, Object> pam = new HashMap<String, Object>();
		pam.put("deadFlag", parametersMap.get("deadFlag"));
		pam.put("deadReason", parametersMap.get("deadReason"));
		pam.put("deadDate",
				BSCHISUtil.toDate((String) parametersMap.get("deadDate")));
		pam.put("phrId", parametersMap.get("phrId"));

		try {
			dao.doUpdate(sql, pam);
		} catch (chis.source.PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改健康档案死亡标记失败！", e);
		}
	}

	/**
	 * 删除终止管理的随访记录
	 * 
	 * @param entryName
	 * @param phrId
	 * @param businessType
	 * @throws ModelDataOperationException
	 */
	public void delEndManageVisit(String entryName, String phrId,
			String businessType) throws ModelDataOperationException {
		// 查询已访随访记录--获取随访计划ID
		StringBuffer search = new StringBuffer(
				"select visitId as visitId from ").append(entryName)
				.append(" where visitEffect = :visitEffect")
				.append(" and phrId = :phrId ");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("visitEffect", VisitEffect.END);
		param.put("phrId", phrId);
		Map<String, Object> vidMap = null;
		try {
			vidMap = dao.doLoad(search.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取已访随访ID失败！", e);
		}

		if (null == vidMap) {
			return;
		}

		String visitId = (String) vidMap.get("visitId");
		if (StringUtils.isNotEmpty(visitId)) {
			// 如果存在 修改已访随访计划
			StringBuffer update = new StringBuffer("update ")
					.append(PUB_VisitPlan)
					.append(" set planStatus = :planStatus ,")
					.append(" visitDate = :visitDate ,")
					.append(" visitId = :visitId ,")
					.append(" lastModifyUser = :lastModifyUser ,")
					.append(" lastModifyDate = :lastModifyDate ")
					.append(" where recordId = :recordId ")
					.append(" and visitId = :visitId1 ")
					.append(" and businessType= :businessType ");

			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("planStatus", PlanStatus.NEED_VISIT);
			paraMap.put("visitDate", BSCHISUtil.toDate(""));
			paraMap.put("visitId", "");
			paraMap.put("lastModifyUser", "");
			paraMap.put("lastModifyDate", BSCHISUtil.toDate(""));
			paraMap.put("recordId", phrId);
			paraMap.put("visitId1", visitId);
			paraMap.put("businessType", businessType);

			try {
				dao.doUpdate(update.toString(), paraMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "恢复已访随访计划失败！", e);
			}

			// 删除以前的档案随访记录
			StringBuffer del = new StringBuffer("delete from ")
					.append(entryName).append(" where visitId = :visitId")
					.append(" and phrId = :phrId ");

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visitId", visitId);
			parameters.put("phrId", phrId);

			try {
				dao.doUpdate(del.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "删除档案随访记录失败", e);
			}
		}
	}

	// ===========================-- 写日志 --===================================
	/**
	 * 写日志
	 * 
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void writeLog(Map<String, Object> paraMap, Context ctx)
			throws ModelDataOperationException, ValidateException {
		HashMap<String, Object> body = new HashMap<String, Object>();
		UserRoleToken ur = UserRoleToken.getCurrent();
		body.put("operType", (String) paraMap.get("operType"));
		body.put("recordType", (String) paraMap.get("recordType"));
		body.put("empiId", (String) paraMap.get("empiId"));
		body.put("personName", (String) paraMap.get("personName"));
		body.put("idCard", (String) paraMap.get("idCard"));
		body.put("createUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		body.put("createUser", ur.getUserId());
		body.put("createTime", BSCHISUtil.toString(new Date(), null));
		try {
			dao.doSave("create", PUB_Log, body, true);
		} catch (PersistentDataOperationException e) {
			logger.error("create log data of PUB_Log failed.", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "日志记录失败！");
		}
	}

	/**
	 * 写日志
	 * 
	 * @param body
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public void writeLog(Map<String, Object> body,boolean validate)
			throws ModelDataOperationException {
		try {
			dao.doSave("create", PUB_Log, body, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "日志记录数据验证失败！");
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "日志记录失败！");
		}
	}
	
	public Map<String, Object> queryRecordList(String schemaId,
			List queryCnd, String queryCndsType, String sortInfo, int pageSize,
			int pageNo, String year, String checkType) {
		Map<String, Object> parameters=new HashMap<String, Object>();
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("pageNo", pageNo);
		result.put("pageSize", pageSize);
		String countSql = BSCHISUtil.getCountSQLBySchemaAddTable(schemaId,
				queryCnd, queryCndsType, sortInfo, year, checkType);
		String sql = BSCHISUtil.getSQLBySchemaAddTable(schemaId, queryCnd,
				queryCndsType, sortInfo, year, checkType);
		try {
			List<Map<String, Object>> l=dao.doQuery(countSql, parameters);
			long totalCount=(Long) l.get(0).get("totalCount");
			result.put("totalCount", totalCount);
			parameters.put("first", (pageNo-1)*pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doSqlQuery(sql, parameters);
			list=SchemaUtil.setDictionaryMessageForListFromSQL(list, schemaId);
			result.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 健康档案退回
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveVerify(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String roleId = user.getRoleId();
		String empiId = body.get("empiId")+"";
		String phrId = body.get("phrId")+"";
		String backReason = body.get("backReason")!=null?body.get("backReason").toString():"";
		int backTimes = 1;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("empiId", empiId);
		paramMap.put("phrId", phrId);
		String backHql = new StringBuffer("select backTimes as backTimes from ")
				.append(EHR_HealthBackRecord)
				.append(" where PHRID=:phrId and EMPIID=:empiId").toString();
//		Map<String, Object> rsCountMap = null;
		List<Map<String,Object>> rsCountMap = null;
		try {
			rsCountMap = dao.doQuery(backHql, paramMap);
			if(rsCountMap!=null && rsCountMap.size()>0){
				backTimes = rsCountMap.size()+1;
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to count of EHR_HealthBackRecord with empiId = ["
							+ empiId + "] and phrId = ["+phrId+"]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询退回记录失败！");
		}
		
		String hql = new StringBuffer("select phrId as phrId,empiId as empiId,manaDoctorId as manaDoctorId,")
				.append("offerLastModifyPerson as offerLastModifyPerson,firstVerifyPerson as firstVerifyPerson,")
				.append("secondVerifyPerson as secondVerifyPerson from ")
				.append(EHR_HealthRecord)
				.append(" where phrId=:phrId and empiId=:empiId").toString();
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, paramMap);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Failed to search of EHR_HealthRecord with empiId = ["
							+ empiId + "] and phrId = ["+phrId+"]", e);
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "无健康档案记录！");
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("phrId", phrId);
		StringBuffer hql1 = new StringBuffer();
		hql1.append("update EHR_HealthRecord set isOffer='b',offerlastmodifyperson = null, offerlastmodifydate = null, unofferlastmodifyperson =null, unofferlastmodifydate =null,isfirstverify = null,unfirstverifyperson = null,unfirstverifydate = null,firstverifyperson = null,firstverifydate = null,issecondverify = null,unsecondverifyperson = null,unsecondverifydate = null,secondverifyperson = null,secondverifydate = null where phrId=:phrId");
		try {
			dao.doUpdate(hql1.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(500, "修改档案当前状态失败", e);
		}
		
		if(rsMap!=null){
			//保存退回日志
			UserRoleToken ur = UserRoleToken.getCurrent();
			Map<String,Object> backRecord = new HashMap<String, Object>();
			backRecord.put("phrId", phrId);
			backRecord.put("backTimes", backTimes);
			backRecord.put("empiId", empiId);
			backRecord.put("backReason", backReason);
			backRecord.put("backPerson", ur.getUserId());
			backRecord.put("backDate", BSCHISUtil.toString(new Date(), null));
			if("chis.22".equals(roleId)){//二级审核
				backRecord.put("lastVerifyLevel", '1');
				backRecord.put("lastVerifyPerson", rsMap.get("offerLastModifyPerson")+"");
			}else if("chis.23".equals(roleId)){//三级审核
				backRecord.put("lastVerifyLevel", '2');
				backRecord.put("lastVerifyPerson", rsMap.get("firstVerifyPerson")+"");
			}else if("chis.24".equals(roleId)){//四级审核
				backRecord.put("lastVerifyLevel", '3');
				backRecord.put("lastVerifyPerson", rsMap.get("secondVerifyPerson")+"");
			}
			try {
				dao.doSave("create", EHR_HealthBackRecord, backRecord, true);
			}catch (PersistentDataOperationException e) {
				logger.error("create log data of PUB_Log failed.", e);
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "日志记录失败！");
			}catch (ValidateException e) {
				logger.error("create log data of PUB_Log failed.", e);
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "日志记录失败！");
			}
		}
	}
}
