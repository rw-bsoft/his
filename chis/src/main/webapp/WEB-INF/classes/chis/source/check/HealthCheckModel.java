/**
 * @(#)HealthCheckModel.java Created on 2012-4-16 上午10:05:13
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.check;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;

import ctd.controller.exception.ControllerException;
import ctd.schema.DisplayModes;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.security.Permission;
import ctd.security.support.condition.FilterCondition;
import ctd.service.core.ServiceException;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class HealthCheckModel implements BSCHISEntryNames {
	private BaseDAO dao;

	/**
	 * @param dao
	 */
	public HealthCheckModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存健康检查记录
	 * 
	 * @param op
	 * @param body
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public Map<String, Object> saveAnnualHealthCheck(String op,
			Map<String, Object> body) throws ModelDataOperationException,
			ValidateException {
		if (body.containsKey("healthCheck")) {
		     String healthCheck=(String) body.get("healthCheck");
		     if (healthCheck.length()>10) {
		        String sql="select  t.createunit as CREATEUNIT from HC_HealthCheck t where t.healthCheck="+healthCheck;
		        Map<String, Object> sql1 = null;
		       try {
		       	sql1 = dao.doSqlLoad(sql, null);
	         	} catch (PersistentDataOperationException e1) {
		    	// TODO Auto-generated catch block
			     e1.printStackTrace();
	        	}
		    if(((String) sql1.get("CREATEUNIT")).indexOf("320124010")!=-1){
			Map<String, Object> p=new HashMap<String, Object>();
			 p.put("tjcs","2");
            p.put("createUnit", body.get("lastModifyUnit"));
            p.put("createUser", body.get("lastModifyUser"));
			String updatesql="update HC_HealthCheck t set t.tjcs=:tjcs, t.createUnit=:createUnit , t.createUser=:createUser where t.healthCheck='"+healthCheck+"'";
			try {
				dao.doUpdate(updatesql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(((String) sql1.get("CREATEUNIT")).indexOf("320124011")!=-1){
			Map<String, Object> p=new HashMap<String, Object>();
			 p.put("tjcs","1");
             p.put("createUnit", body.get("lastModifyUnit"));
             p.put("createUser", body.get("lastModifyUser"));
			String updatesql="update HC_HealthCheck t set t.tjcs=:tjcs, t.createUnit=:createUnit , t.createUser=:createUser where t.healthCheck='"+healthCheck+"'";
			try {
				dao.doUpdate(updatesql, p);
			} catch (PersistentDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		     }
		}
		Map<String, Object> record;
		try {
			record = dao.doSave(op, HC_HealthCheck, body, false);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康检查记录失败！", e);
		}
		return record;
	}

	/**
	 * 保存住院治疗情况信息
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> saveInhospitalSituation(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		Map<String, Object> record;
		try {
			record = dao.doSave(op, HC_InhospitalSituation, body, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存住院治疗情况失败！", e);
		}
		return record;
	}

	/**
	 * 保存用药情况
	 * 
	 * @param op
	 * @param record
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> saveMedicineSituation(String op,
			Map<String, Object> record) throws ModelDataOperationException,
			ValidateException {
		try {
			return dao.doSave(op, HC_MedicineSituation, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存用药情况失败！", e);
		}
	}

	/**
	 * 保存辅助检查
	 * 
	 * @param op
	 * @param record
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> saveAccessoryExaminationData(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, HC_AccessoryExamination, record, validate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存辅助检查！", e);
		}
	}

	/**
	 * 加载住院情况数据
	 * 
	 * @param healthCheck
	 * @return
	 */
	public List<Map<String, Object>> loadInhospitalSituationData(
			String healthCheck) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
				healthCheck);
		try {
			return dao.doList(cnd, null, HC_InhospitalSituation);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载住院情况数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:加载住院情况数据
	 * @param healthCheck
	 * @param type
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-15 上午10:55:35
	 * @Modify:
	 */
	public List<Map<String, Object>> loadInhospitalSituationData(
			String healthCheck, String type) throws ModelDataOperationException {
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
				healthCheck);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "type", "s", type);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doList(cnd, "a.createDate desc", HC_InhospitalSituation);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载住院情况数据失败！", e);
		}
	}

	/**
	 * 加载用药情况数据
	 * 
	 * @param healthCheck
	 * @return
	 */
	public List<Map<String, Object>> loadMedicineSituationData(
			String healthCheck) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
				healthCheck);
		try {
			return dao.doList(cnd, "a.createDate desc", HC_MedicineSituation);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载用药情况数据失败！", e);
		}
	}

	/**
	 * 通过年检编号加载生活方式数据
	 * 
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadLifestySituationData(String healthCheck)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
					healthCheck);
			return dao.doLoad(cnd, HC_LifestySituation);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载生活方式数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:加载 非免疫规划预防接种 数据
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-15 下午12:02:05
	 * @Modify:
	 */
	public List<Map<String, Object>> loadNonimmuneInoculationData(
			String healthCheck) throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
				healthCheck);
		try {
			return dao
					.doList(cnd, "a.createDate desc", HC_NonimmuneInoculation);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载非免疫规划预防接种失败！", e);
		}
	}

	/**
	 * 通过年检编号加载查体数据
	 * 
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadExaminationData(String healthCheck)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
					healthCheck);
			return dao.doLoad(cnd, HC_Examination);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载查体数据失败！", e);
		}
	}

	/**
	 * 通过年检编号加载健康评价数据
	 * 
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadHealthAssessmentData(String healthCheck)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
					healthCheck);
			return dao.doLoad(cnd, HC_HealthAssessment);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载健康评价数据失败！", e);
		}
	}

	/**
	 * 通过年检编号加载辅助检查数据
	 * 
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadAccessoryExaminationData(String healthCheck)
			throws ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "healthCheck", "s",
					healthCheck);
			return dao.doLoad(cnd, HC_AccessoryExamination);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载辅助检查数据失败！", e);
		}
	}

	/**
	 * 保存非免疫规划预防接种数据
	 * 
	 * @param record
	 * @param op
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	public Map<String, Object> saveNonimmuneInoculation(
			Map<String, Object> record, String op) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, HC_NonimmuneInoculation, record, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存非免疫规划预防接种数据失败！", e);
		}
	}

	/**
	 * 加载健康检查年检数据
	 * 
	 * @param healthCheck
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> loadHealthCheckData(Date beginDate,
			Date endDate, String empiId) throws ModelDataOperationException {
		try {
			StringBuilder hql = new StringBuilder("select 1 from ")
					.append(HC_HealthCheck)
					.append(" where checkDate >= :beginDate")
					.append(" and checkDate <= :endDate and empiId = :empiId");
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("beginDate", beginDate);
			parameters.put("endDate", endDate);
			parameters.put("empiId", empiId);
			return dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("加载健康检查年检数据失败！", e);
		}
	}

	/**
	 * 按主键id删除一条健康检查表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteHealthCheckRecord(String healthCheck)
			throws ModelDataOperationException {
		try {
			dao.doRemove(healthCheck, HC_HealthCheck);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("按主键id删除一条健康检查表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_LifestySituation表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteLifestySituationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_LifestySituation)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_LifestySituation表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_Examination表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteExaminationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(HC_Examination)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_Examination表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_HealthAssessment表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteHealthAssessmentRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_HealthAssessment)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_HealthAssessment表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_InhospitalSituation表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteInhospitalSituationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_InhospitalSituation)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_InhospitalSituation表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_MedicineSituation表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteMedicineSituationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_MedicineSituation)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_MedicineSituation表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_AccessoryExamination表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteAccessoryExaminationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_AccessoryExamination)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_AccessoryExamination表数据失败！", e);
		}
	}

	/**
	 * 按healthCheck删除HC_NonimmuneInoculation表数据
	 * 
	 * @param healthCheck
	 * @throws ModelDataOperationException
	 */
	public void deleteNonimmuneInoculationRecord(String healthCheck)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(HC_NonimmuneInoculation)
				.append(" where healthCheck = :healthCheck ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("healthCheck", healthCheck);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					"按healthCheck删除HC_NonimmuneInoculation表数据失败！", e);
		}
	}

	/**
	 * 
	 * @Description:保存健康体检表单数据
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-7-14 下午3:11:52
	 * @Modify:
	 */
	public Map<String, Object> saveHealthCheckData(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HC_HealthExamination, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康体检表单数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康体检表单失败", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:根据主键加载健康检查基本信息
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-7 上午8:48:57
	 * @Modify:
	 */
	public Map<String, Object> loadHealthCheckByPkey(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(HC_HealthCheck, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "根据主键加载健康检查基本信息失败", e);
		}
		return rsMap;
	}

	/**
	 * ]
	 * 
	 * @Description:保存生活方式数据
	 * @param op
	 * @param reocrd
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-7 下午3:00:36
	 * @Modify:
	 */
	public Map<String, Object> saveLifestySituation(String op,
			Map<String, Object> reocrd, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HC_LifestySituation, reocrd, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存生活方式数据时数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存生活方式数据失败", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存查体记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-7 下午3:11:23
	 * @Modify:
	 */
	public Map<String, Object> saveExamination(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HC_Examination, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存查体记录时数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存查体记录失败", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存健康评价记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-8-7 下午3:42:15
	 * @Modify:
	 */
	public Map<String, Object> saveHealthAssessment(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HC_HealthAssessment, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康评价记录时数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查体记录时数据验证失败", e);
		}
		return rsMap;
	}
	public Map<String, Object> saveWProgestationask(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, hc_w_progestationask, record, validate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存妻子孕前询问！", e);
		}
	}
	public Map<String, Object> saveWProgestationCheck(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, hc_w_progestationcheck, record, validate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存妻子孕前检查！", e);
		}
	}
	public Map<String, Object> saveMProgestationask(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, hc_m_progestationask, record, validate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存丈夫孕前询问！", e);
		}
	}
	public Map<String, Object> saveMProgestationCheck(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException, ValidateException {
		try {
			return dao.doSave(op, hc_m_progestationcheck, record, validate);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("保存丈夫孕前检查！", e);
		}
	}
	public Map<String, Object> queryNeedCheckList(String schemaId,List queryCnd,String queryCndsType,String sortInfo,int pageSize,
		int pageNo, String manaUnitId) {
		Map<String, Object> parameters=new HashMap<String, Object>();
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("pageNo", pageNo);
		result.put("pageSize", pageSize);
		Calendar c=Calendar.getInstance();
		c.setTime(new Date());
		String countSql=getCountSQL(manaUnitId,queryCnd,queryCndsType,sortInfo,c.get(Calendar.YEAR));
		String sql=getSQLBySchemaAddTable(schemaId,queryCnd,queryCndsType,sortInfo,manaUnitId,c.get(Calendar.YEAR));
		System.out.println(countSql);
		System.out.println(sql);
		try{
			List<Map<String, Object>> l=dao.doSqlQuery(countSql, parameters);
//			String totalCount=;
			result.put("totalCount", l.get(0).get("TOTALCOUNT"));
			parameters.put("first", (pageNo-1)*pageSize);
			parameters.put("max", pageSize);
			List<Map<String, Object>> list = dao.doSqlQuery(sql,parameters);
			list=SchemaUtil.setDictionaryMessageForListFromSQL(list,schemaId);
			result.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getCountSQL(String manaUnitId,List queryCnd,String queryCndsType,String sortInfo,int year){
		String sql = "select count(1) as totalCount from EHR_HealthRecord a,MPI_DemographicInfo b ";
		List cnds = new ArrayList();
		String qc=" a.empiId=b.empiId and a.manaUnitId like '"+manaUnitId+"%' and floor(months_between(SYSDATE,b.birthday)/12)>=65 "+
				" and not exists (select 1 from HC_HealthCheck c where a.empiId=c.empiId and to_char(c.checkdate,'yyyy')='"+year+"')";
		if(StringUtils.isEmpty(queryCndsType)){
			queryCndsType = "query";
		}
		if(queryCnd != null && !queryCnd.isEmpty()) {
			cnds.add(queryCnd);
		}
		List<Object> whereCnd = null;
		int cndCount = cnds.size();
		if (cndCount == 0) {
			whereCnd = (List<Object>) queryCnd;
		} else if(cndCount == 1) {
			whereCnd = (List<Object>) cnds.get(0);
		}else{
			whereCnd = new ArrayList<Object>();
			whereCnd.add("and");
			for (Object cd : cnds) {
				whereCnd.add((List<Object>) cd);
			}
		}
		try{
			if(whereCnd==null){
				sql=sql+" where "+qc;
			}else{
				sql=sql+" where "+ ExpressionProcessor.instance().toString(whereCnd)+" and "+qc;
			}
		}catch(ExpException e) {
			e.printStackTrace();
		}
		return sql;
	}
	public static String getSQLBySchemaAddTable(String schemaId, List queryCnd,
	String queryCndsType, String sortInfo, String manaUnitId,int year) {
		Schema sc = null;
		String sql = "select ";
		String sqlCopy = "select ";
		List cnds = new ArrayList();
		String queryCondition = " a.empiId=b.empiId and a.manaUnitId like '"+manaUnitId+"%' and floor(months_between(SYSDATE,b.birthday)/12)>=65 "+
		" and not exists (select 1 from HC_HealthCheck c where a.empiId=c.empiId and to_char(c.checkdate,'yyyy')='"+year+"')";
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		boolean flag = true;
		String tableNames = schemaId.substring(schemaId.lastIndexOf(".") + 1,schemaId.length()) + " a,";
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if (!(p.getMode().isAccessible())) {
				continue;
			}
			if (it.getDisplayMode() == DisplayModes.NO_LIST_DATA) {
				continue;
			}
			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
				sqlCopy = sqlCopy + "t1." + fid + " as " + fid + ",";
				if (loadedRelation.containsKey(refAlias)) {
					continue;
				}
				SchemaRelation sr = sc.getRelationByAlias((String) it.getProperty("refAlias"));
				tableNames = tableNames+sr.getFullEntryName().substring(
								sr.getFullEntryName().lastIndexOf(".")+1,
								sr.getFullEntryName().length()) + " ,";
				List<?> cd = sr.getJoinCondition();
				if (cd != null) {
					cnds.add(cd);
				}
				loadedRelation.put(refAlias, true);
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";
				sqlCopy = sqlCopy + "t1." + fid + " as " + fid + ",";
			}
		}
		tableNames = tableNames.substring(0, tableNames.length() - 1) + " ";
		if (StringUtils.isEmpty(queryCndsType)) {
			queryCndsType = "query";
		}
		FilterCondition c = (FilterCondition) sc.lookupCondition(queryCndsType);
		if(flag){
		if (c != null) {
			List<Object> roleCnd = (List<Object>) c.getDefine();
			if (roleCnd != null && !roleCnd.isEmpty()) {
				cnds.add(roleCnd);
			}
		}
		}
		if (queryCnd != null && !queryCnd.isEmpty()) {
			cnds.add(queryCnd);
		}
		List<Object> whereCnd = null;
		int cndCount = cnds.size();
		if (cndCount == 0) {
			whereCnd = (List<Object>) queryCnd;
		} else if (cndCount == 1) {
			whereCnd = (List<Object>) cnds.get(0);
		} else {
			whereCnd = new ArrayList<Object>();
			whereCnd.add("and");
			for (Object cd : cnds) {
				whereCnd.add((List<Object>) cd);
			}
		}
		if (StringUtils.isEmpty(sortInfo)) {
			sortInfo = sc.getSortInfo();
		}
		sql = sql.substring(0, sql.length()-1) + " from "+tableNames;
		try{
			if(whereCnd==null){
				sql=sql+" where "+queryCondition;
			}else{
				sql=sql+" where "+ ExpressionProcessor.instance().toString(whereCnd)+" and "+queryCondition;
			}
		}catch(ExpException e){
			e.printStackTrace();
		}
		if (sortInfo != null){
			sql = sql + " order by " + sortInfo;
		}
		return sql;
	}
}
