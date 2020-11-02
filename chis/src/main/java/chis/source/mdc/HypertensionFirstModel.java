/**
 * @(#)HypertensionFirstModel.java Created on 2012-2-20 下午3:10:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;

/**
 * @description 高血压疑似病人核实(首诊测压表)
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFirstModel extends MDCBaseModel {

	public HypertensionFirstModel(BaseDAO dao) {
		super(dao);
	}

	/**
	 * 根据empiId获取高血压首诊信息
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getHypertensionFistByEmpiId(String empiId)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "recordNumber desc", MDC_HypertensionFirst);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取首诊信息失败！", e);
		}
		return rsList;
	}

	/**
	 * 查询 -首诊测压-表
	 * 
	 * @param cnd
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getHypertensionFirstByCnd(String cnd,
			String orderBy) throws ModelDataOperationException {
		List<?> cndList = null;
		try {
			cndList = CNDHelper.toListCnd(cnd);
		} catch (ExpException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询参数错误！");
		}
		if (null == cndList) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询参数错误！");
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cndList, orderBy, MDC_HypertensionFirst);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询首诊测压失败！");
		}
		return rsList;
	}

	/**
	 * 查询-首诊测压明细-表
	 * 
	 * @param cnd
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getHypertensionFirstDetailByCnd(
			String cnd, String orderBy) throws ModelDataOperationException {
		List<?> cndList = null;
		try {
			cndList = CNDHelper.toListCnd(cnd);
		} catch (ExpException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询参数错误！");
		}
		if (null == cndList) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询参数错误！");
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cndList, orderBy, MDC_HypertensionFirstDetail);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询首诊测压明细失败！");
		}
		return rsList;
	}

	/**
	 * 获取高血压 首诊测压 的编号
	 * 
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public String getRecordNumberOfHypertensionFirst(String empiId)
			throws ModelDataOperationException {
		Calendar startc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		startc.set(startc.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		endc.set(endc.get(Calendar.YEAR), 11, 31, 23, 59, 59);
		String hql = new StringBuffer("select recordNumber as recordNumber from ")
				.append(MDC_HypertensionFirst)
				.append(" where empiId = :empiId")
				.append(" and hypertensionFirstDate>cast(:startDate as date)")
				.append(" and hypertensionFirstDate<cast(:endDate as date)")
				.toString();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		param.put("startDate", startc.getTime());
		param.put("endDate", endc.getTime());

		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, param);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询首诊测压失败！");
		}

		return rsMap.size() > 0 ? (String) rsMap.get("recordNumber") : null;
	}

	/**
	 * 保存首诊测压信息
	 * 
	 * @param op
	 * @param entryName
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionFirstInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			rsMap = dao.doSave(op, MDC_HypertensionFirst, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压信息数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压信息失败！");
		}
		return rsMap;
	}

	/**
	 * 保存首诊测压明细
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveHypertensionFirstDetailInfo(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = new HashMap<String, Object>();
		try {
			dao.doSave(op, MDC_HypertensionFirstDetail, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压明细失败数据验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首诊测压明细失败！");
		}
		return rsMap;
	}

}
