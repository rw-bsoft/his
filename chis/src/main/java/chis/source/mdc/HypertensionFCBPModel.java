/**
 * @(#)HypertensionFCBPModel.java Created on 2014-2-20 下午4:45:57
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.empi.EmpiModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFCBPModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public HypertensionFCBPModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:判断 是否 需要做高血压35岁首诊测压
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-2-20 下午4:54:48
	 * @Modify:
	 */
	public boolean isNeedHypertensionFCBP(String empiId)
			throws ModelDataOperationException {
		boolean needHFCBP = false;
		EmpiModel em = new EmpiModel(dao);
		int age = -1;
		try {
			age = em.getAge(empiId);
		} catch (ModelDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR,
					"Failed to get age of people.", e);
		}
		if (age >= 35) {
			List<Map<String, Object>> rsList = getCurYearHypertensionFCBP(empiId);
			if (rsList == null || (rsList != null && rsList.size() == 0)) {
				needHFCBP = true;
			}
		}

		return needHFCBP;
	}

	/**
	 * 
	 * @Description:查某人领导有没有做过首诊测压
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-2-20 下午5:03:27
	 * @Modify:
	 */
	public List<Map<String, Object>> getCurYearHypertensionFCBP(String empiId)
			throws ModelDataOperationException {
		Calendar startc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		startc.set(startc.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		endc.set(endc.get(Calendar.YEAR), 11, 31, 23, 59, 59);
		String hql = new StringBuffer(" from ").append(MDC_Hypertension_FCBP)
				.append(" where empiId = :empiId")
				.append(" and measureDate>:startDate")
				.append(" and measureDate<:endDate").toString();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("startDate", startc.getTime());
		param.put("endDate", endc.getTime());

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询首诊测压失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:保存首诊测压
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-2-20 下午5:35:10
	 * @Modify:
	 */
	public Map<String, Object> saveHypertensionFCBP(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			if(record.get("fcbpId")!=null && !"".equals(record.get("fcbpId"))){//add by lizhi 2017-11-09如果主键存在，执行跟新
				String fcbpId = record.get("fcbpId").toString();
				Map<String,Object> queryResult = dao.doLoad(MDC_Hypertension_FCBP, fcbpId);
				if(queryResult!=null){
					op = "update";
				}
			}
			rsMap = dao.doSave(op, MDC_Hypertension_FCBP, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压失败！", e);
		}
		return rsMap;
	}
	
	/**
	 * 
	 * @Description:更新门诊病程记录血压值
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author lizhi 2017-10-28
	 * @Modify:
	 */
	public void updateMsBcjl(Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		try {
			if(record.get("JZXH")==null || record.get("BRID")==null){
				return;
			}
			if(record.get("constriction")==null || "".equals(record.get("constriction"))){
				return;
			}
			if(record.get("diastolic")==null || "".equals(record.get("diastolic"))){
				return;
			}
			String hql = new StringBuffer("update MS_BCJL set SSY=:SSY, SZY=:SZY")
			.append(" where JZXH=:JZXH and BRID=:BRID")
			.toString();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SSY", Integer.parseInt(record.get("constriction")+""));
			parameters.put("SZY", Integer.parseInt(record.get("diastolic")+""));
			parameters.put("JZXH", Long.parseLong(record.get("JZXH")+""));
			parameters.put("BRID", Long.parseLong(record.get("BRID")+""));
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新门诊病程记录血压值失败！", e);
		}
	}
}
