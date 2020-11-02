/**
 * @(#)PregnantVisitModel.java Created on 2012-4-27 下午8:09:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mhc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PregnantVisitModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public PregnantVisitModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存孕妇随访记录
	 * 
	 * @param op
	 * @param visitRecord
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void savePregnantVisitRecord(Map<String, Object> data, String op,
			Map<String, Object> res)throws ValidateException,
			ModelDataOperationException {
		try {
			Map<String, Object> genValues = dao.doSave(op, MHC_VisitRecord,
					data, false);
			res.put("body", genValues);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存随访信息失败!",e);
		}
	}

	/**
	 * 更改本次随访之前未处理过的随访计划的状态。
	 * 
	 * @param pregnantId
	 * @param sn
	 * @throws ModelDataOperationException
	 */
	public void updateOverVisitPlanState(String pregnantId, int sn)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ")
				.append(PUB_VisitPlan)
				.append(" set planStatus = ")
				.append(":planStatus where recordId = :recordId and ")
				.append("planStatus = :planStatus0 and businessType = :businessType and sn < :sn ")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("planStatus", PlanStatus.NOT_VISIT);
		param.put("recordId", pregnantId);
		param.put("planStatus0", PlanStatus.NEED_VISIT);
		param.put("businessType", BusinessType.MATERNAL);
		param.put("sn", sn);
		try {
			dao.doUpdate(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "修改过期的随访计划失败!",e);
		}
	}

	/**
	 * 获取多胞胎信息
	 * 
	 * @param visitId
	 * @param saveFlag
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getFetals(String visitId, String saveFlag)
			throws ModelDataOperationException {
		String sBuilder = new StringBuilder("select fetalId  as fetalId from ")
				.append(MHC_FetalRecord)
				.append(" where visitId = :visitId and saveFlag = :saveFlag")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("visitId", visitId);
		param.put("saveFlag", saveFlag);
		try {
			return dao.doQuery(sBuilder.toString(), param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取多胞胎信息失败!",e);
		}
	}

	/**
	 * 保存多胞胎信息
	 * 
	 * @param op
	 * @param fetalRecord
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveFetals(String op, Map<String, Object> fetalRecord)
			throws ModelDataOperationException, ValidateException {
		try {
			dao.doSave(op, MHC_FetalRecord, fetalRecord, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存多胞胎信息失败!",e);
		}
	}

	/**
	 * 删除多胞胎信息
	 * @param visitId
	 * @param saveFlag
	 * @throws ModelDataOperationException
	 */
	public void deleteFetals(String visitId, String saveFlag) throws ModelDataOperationException {
		String sb = new StringBuffer("delete from ").append(MHC_FetalRecord)
				.append(" where visitId = :visitId  and saveFlag = :saveFlag")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("visitId", visitId);
		param.put("saveFlag", saveFlag);
		try {
			dao.doUpdate(sb, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除多胞胎信息失败!",e);
		}
	}
	
	/**
	 * 保存多胞胎信息
	 * 
	 * @param op
	 * @param fetalRecord
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	public void saveDesc(String op, Map<String, Object> descRecord)
			throws ModelDataOperationException, ValidateException {
		try {
			dao.doSave(op, MHC_VisitRecordDescription, descRecord, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存中医辩体信息失败!",e);
		}
	}
	
	
	/**
	 * 
	 * 
	 * @param body
	 * @param planDate
	 * @param businessType
	 * @return -1表示不需要<br/>
	 *         1(倒数第二条)<br/>
	 *         2（倒数第一条）
	 * @throws ModelDataOperationException 
	 */
	protected int whetherLastVisit(HashMap<String, Object> body, Date planDate,
			String businessType) throws ModelDataOperationException {
		String hql = new StringBuffer("select planStatus as planStatus from ")
				.append(PUB_VisitPlan)
				.append(" where empiId=:empiId and businessType=:businessType and planDate>=:planDate")
				.toString();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", body.get("empiId"));
		param.put("businessType", businessType);
		param.put("planDate", planDate);
		List<Map<String, Object>> list;
		try {
			list = dao.doQuery(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(e);
		}
		int i = 0;
		for (Map<String, Object> map : list) {
			String planStatus = (String) map.get("planStatus");
			if (!planStatus.equals(PlanStatus.NEED_VISIT) && !planStatus.equals(PlanStatus.WRITEOFF)) {
				i++;
			}
		}
		if (i >= 2) {
			body.put("isLastVisitRecord", "0");
		}
		int count = (list == null ? -1 : list.size());
		if (count == 2) {
			return 1;
		}
		if (count == 1) {
			return 2;
		}
		return -1;
	}

}
