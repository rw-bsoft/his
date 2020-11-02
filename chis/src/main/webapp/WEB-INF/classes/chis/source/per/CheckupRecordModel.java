/**
 * @(#)CheckupRecordModel.java Created on 2012-4-23 下午7:55:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupRecordModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public CheckupRecordModel(BaseDAO dao) {
		this.dao = dao;
	}

	public CheckupRecordModel(Context ctx) {
		this.dao = new BaseDAO();
	}

	/**
	 * 作废体检记录
	 * 
	 * @param checkupNo
	 * @throws ModelDataOperationException
	 */
	public void logoutCheckupRecord(String checkupNo)
			throws ModelDataOperationException {
		StringBuffer hqlBuffer = new StringBuffer("update ")
				.append(PER_CheckupRegister).append(" set status = :status ")
				.append(" where checkupNo = :checkupNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_WRITE_OFF);
		parameters.put("checkupNo", checkupNo);
		try {
			dao.doUpdate(hqlBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "作废体检记录失败！", e);
		}
	}

	/**
	 * ID Loader load中ID查询
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> idsLoader(String loadBy)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select checkupNo ").append(
				" as id ").append(", status as status");
		hql.append(",checkupOrganization as checkupOrganization, ")
				.append("totalCheckupDate as totalCheckupDate from ")
				.append(PER_CheckupRegister).append(" where empiId = :id ")
				.append(" order by checkupTime desc ,checkupNo desc");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", loadBy);

		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取ID失败！", e);
		}

		return rsList;
	}

	/**
	 * 保存体检登记
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckupRegister(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_CheckupRegister, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(Constants.CODE_UNAUTHORIZED,
					"保存体检登记数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检登记失败！", e);
		}
		return rsMap;
	}

	/**
	 * 依据checkupNo删除 体检ICD
	 * 
	 * @param checkupNo
	 * @throws ModelDataOperationException
	 */
	public void deletePERICDbyCheckupNo(String checkupNo)
			throws ModelDataOperationException {
		StringBuilder sb = new StringBuilder(100);
		sb.append("delete from ");
		sb.append(PER_ICD);
		sb.append(" where checkupNo = :checkupNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		try {
			dao.doUpdate(sb.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除体检ICD失败！", e);
		}
	}

	/**
	 * 保存体检ICD
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> savePERICD(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_ICD, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(Constants.CODE_UNAUTHORIZED,
					"保存体检ICD数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检ICD失败！", e);
		}
		return rsMap;
	}

	/**
	 * 获取某体检记录的ICD记录数
	 * 
	 * @param checkupNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public long getPERICD(String checkupNo) throws ModelDataOperationException {
		String hqlString = new StringBuffer(
				"select count(*) as recordNum from ").append(PER_ICD)
				.append(" where checkupNo = :checkupNo").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hqlString, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检ICD记录数失败", e);
		}
		long rs = 0L;
		if (rsMap != null && rsMap.size() > 0) {
			rs = (Long) rsMap.get("recordNum");
		}
		return rs;
	}

	/**
	 * 获取体检登记信息
	 * 
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadPerRegisterByPkey(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(PER_CheckupRegister, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检登记信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 体检总检
	 * 
	 * @param checkupNo
	 * @param totalCheckupDate
	 * @throws ModelDataOperationException
	 */
	public void finalCheck(String checkupNo, Date totalCheckupDate)
			throws ModelDataOperationException {
		StringBuffer sbBuffer = new StringBuffer(200);
		sbBuffer.append("update ")
				.append(PER_CheckupRegister)
				.append(" set totalCheckupDate = :tcDate where checkupNo = :checkupNo ");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tcDate", totalCheckupDate);
		parameters.put("checkupNo", checkupNo);
		try {
			dao.doUpdate(sbBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "体检总检失败！", e);
		}
	}

	/**
	 * 撤销体检记录总检
	 * 
	 * @param checkupNo
	 * @throws ModelDataOperationException
	 */
	public void checkRevoke(String checkupNo)
			throws ModelDataOperationException {
		StringBuilder sb = new StringBuilder(" update ").append(
				PER_CheckupRegister).append(
				" set totalCheckupDate = null where checkupNo = :checkupNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		try {
			dao.doUpdate(sb.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "撤销体检记录总检失败！", e);
		}
	}

	/**
	 * 获取体检明细
	 * 
	 * @param cnd
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCheckupDatail(List<?> cnd,
			String orderBy) throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, orderBy, PER_CheckupDetail);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检明细失败！", e);
		}
		return rsList;
	}

	/**
	 * 获取体检小结
	 * 
	 * @param cnd
	 * @param orderBy
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCheckupSummary(List<?> cnd,
			String orderBy) throws ModelDataOperationException {
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, orderBy, PER_CheckupSummary);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检小结信息失败！", e);
		}
		return rsList;
	}

	/**
	 * 删除所有的科室小结
	 * 
	 * @param checkupNo
	 * @param projectOfficeCode
	 * @throws ModelDataOperationException
	 */
	public void deleteCheckupSummary(String checkupNo, String projectOfficeCode)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ")
				.append(PER_CheckupSummary)
				.append(" where checkupNo  = :checkupNo")
				.append(" and projectOfficeCode = :projectOfficeCode")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		parameters.put("projectOfficeCode", projectOfficeCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除科室小结失败！", e);
		}
	}

	/**
	 * 删除所有体检项目
	 * 
	 * @param checkupNo
	 * @param projectOfficeCode
	 * @throws ModelDataOperationException
	 */
	public void deleteCheckupDetail(String checkupNo, String projectOfficeCode)
			throws ModelDataOperationException {
		String hql = new StringBuffer("delete from ").append(PER_CheckupDetail)
				.append(" where checkupNo  = :checkupNo")
				.append(" and projectOfficeCode = :projectOfficeCode")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		parameters.put("projectOfficeCode", projectOfficeCode);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "删除体检项目失败！", e);
		}
	}

	/**
	 * 保存体检小结
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckSummary(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_CheckupSummary, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检小结数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检小结失败！", e);
		}
		return rsMap;
	}

	/**
	 * 保存体检明细信息
	 * 
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveCheckupDetail(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PER_CheckupDetail, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检明细数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检明细信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 获取体检异常综述
	 * 
	 * @param checkupNo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getCheckupExce(String checkupNo)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select ")
				.append(" checkupProjectId as checkupProjectId ")
				.append(" , checkupProjectName as checkupProjectName")
				.append(" ,ifException as ifException ")
				.append(", checkupOutcome as checkupOutcome ").append(" from ")
				.append(PER_CheckupDetail)
				.append(" where checkupNo = :checkupNo ")
				.append(" and checkupOutcome='2'")
				// .append(" and ifException in ('2','3','5','6','7','8','10','11','12','14','16','17','19','21','23','25','35','36','37','39','40','41')")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("checkupNo", checkupNo);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取体检异常综述失败！", e);
		}
		return rsList;
	}

	/**
	 * 返回存在字典的体检项目列表
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getDicItems() throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String hql = new StringBuffer(
				"select checkupProjectId as checkupProjectId,checkupDic as checkupDic from ")
				.append(PER_CheckupDict).append(" where length(checkupDic)>0")
				.toString();
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取存在字典的体检项目失败！", e);
		}
		if (rsList == null) {
			return null;
		}
		parameters.clear();
		for (int i = 0; i < rsList.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) rsList.get(i);
			String checkupProjectId = map.get("checkupProjectId").toString();
			String checkupDic = map.get("checkupDic").toString();
			parameters.put(checkupProjectId, "chis.dictionary."+checkupDic);
		}
		return parameters;
	}

	/**
	 * 保存体检异常综述
	 * 
	 * @param checkupNo
	 * @param sumupException
	 * @throws ModelDataOperationException
	 */
	public void savePerSumupException(String checkupNo, String sumupException)
			throws ModelDataOperationException {
		StringBuffer seBuffer = new StringBuffer("update ")
				.append(PER_CheckupRegister)
				.append(" set checkupExce = :sumupException where checkupNo = :checkupNo");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sumupException", sumupException);
		parameters.put("checkupNo", checkupNo);
		try {
			dao.doUpdate(seBuffer.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存体检异常综述失败！", e);
		}
	}
}
