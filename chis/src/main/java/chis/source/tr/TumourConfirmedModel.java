/**
 * @(#)TumourConfirmedModel.java Created on 2014-4-22 下午5:06:29
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourConfirmedModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourConfirmedModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:分页加载肿瘤确诊记录
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-22 下午3:47:05
	 * @Modify:
	 */
	public Map<String, Object> loadTCPageList(Map<String, Object> req)
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
		// String queryCndsType = null;
		// if (req.containsKey("queryCndsType")) {
		// queryCndsType = (String) req.get("queryCndsType");
		// }
		// String sortInfo = null;
		// if (req.containsKey("sortInfo")) {
		// sortInfo = (String) req.get("sortInfo");
		// }
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(MDC_TumourConfirmedPageList);
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.hasProperty("refAlias") || si.isVirtual()) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" MDC_TumourConfirmed a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" left join MDC_TumourExpertReview d on a.TCID=d.TCID ");
		StringBuffer countSQL = new StringBuffer(
				"select count(a.TCID) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where);
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> tctcList = null;
		try {
			tctcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (tctcList != null && tctcList.size() > 0) {
			Map<String, Object> trMap = tctcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select ").append(sfBuffer.substring(1)).append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where);
			}
			sql.append(" order by d.assessDate desc ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tcList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tcMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.hasProperty("refAlias") || si.isVirtual()) {
							String refItemId = (String) si
									.getProperty("refItemId");
							Object refItemValObject = rMap.get(refItemId
									.toUpperCase());
							if (refItemId.equals("assessment")
									&& refItemValObject == null) {
								refItemValObject = "0";
							}
							tcMap.put(refItemId, refItemValObject);
						} else {
							String f = si.getId();
							Object fv = rMap.get(f.toUpperCase());
							tcMap.put(f, fv);
						}
					}
					Date birthday = (Date) tcMap.get("birthday");
					if (birthday != null) {
						tcMap.put("age",
								BSCHISUtil.calculateAge(birthday, null));
					}
					tcList.add(tcMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tcList,
						MDC_TumourConfirmedListView));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}

	/**
	 * 
	 * @Description:获取肿瘤确诊记录 <br/>
	 *                       empiId+highRiskType 唯一确定一条肿瘤确诊记录
	 * @param empiId
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-15 上午11:13:31
	 * @Modify:
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> getTC(String empiId, String highRiskType)
			throws ModelDataOperationException {
		List<Object> cnd1 = new CNDHelper().createSimpleCnd("eq", "a.empiId",
				"s", empiId);
		List<Object> cnd2 = new CNDHelper().createSimpleCnd("eq",
				"a.highRiskType", "s", highRiskType);
		List<Object> cnd = new CNDHelper().createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "a.TCID desc", MDC_TumourConfirmed);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤确诊记录失败!", e);
		}
		Map<String, Object> tcMap = null;
		if (rsList != null && rsList.size() > 0) {
			tcMap = rsList.get(0);
		}
		return tcMap;
	}

	/**
	 * 
	 * @Description:保存肿瘤确诊记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-22 下午5:34:54
	 * @Modify:
	 */
	public Map<String, Object> saveTumourConfirmedRecord(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourConfirmed, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤确诊记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤确诊记录证失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:获取肿瘤确诊记录
	 * @param TCID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-22 下午5:58:32
	 * @Modify:
	 */
	public Map<String, Object> getTumourConfirmedRecord(String TCID)
			throws ModelDataOperationException {
		Map<String, Object> tcMap = null;
		try {
			tcMap = dao.doLoad(MDC_TumourConfirmed, TCID);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤确诊记录失败！", e);
		}
		return tcMap;
	}

	/**
	 * 
	 * @Description:修改性质属性
	 * @param phrId
	 * @param nature
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-23 下午5:16:14
	 * @Modify:
	 */
	public void updateNature(String TCID, String nature)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MDC_TumourConfirmed")
				.append(" set nature=:nature  ")
				.append(" , lastModifyUnit=:lastModifyUnit")
				.append(" ,lastModifyUser=:lastModifyUser")
				.append(" ,lastModifyDate=:lastModifyDate")
				.append(" where TCID=:TCID").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nature", nature);
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("TCID", TCID);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新高危记录性质失败！", e);
		}
	}

	/**
	 * 
	 * @Description:修改肿瘤确诊记录的状态
	 * @param phrId
	 * @param status
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-24 上午10:04:15
	 * @Modify:
	 */
	public void updateStatus(String TCID, String status)
			throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append("MDC_TumourConfirmed")
				.append(" set status=:status  ")
				.append(" , lastModifyUnit=:lastModifyUnit")
				.append(" ,lastModifyUser=:lastModifyUser")
				.append(" ,lastModifyDate=:lastModifyDate")
				.append(" where TCID=:TCID").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", status);
		parameters.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		parameters.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		parameters.put("lastModifyDate", new Date());
		parameters.put("TCID", TCID);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新肿瘤确诊记录的状态失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤确诊记录(一条记录)
	 * @param TCID
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午3:13:09
	 * @Modify:
	 */
	public void lonoutTumourConfirmedRecord(String TCID,
			String cancellationReason) throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourConfirmed")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where TCID = :TCID ")
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
		parameters.put("TCID", TCID);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销肿瘤确诊记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤确诊记录(一条记录)
	 * @param empiId
	 * @param highRiskType
	 *            高危类别
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-25 下午2:45:49
	 * @Modify:
	 */
	public void lonoutTumourConfirmedRecord(String empiId, String highRiskType,
			String cancellationReason) throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourConfirmed")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where empiId = :empiId ")
				.append(" and highRiskType = :highRiskType ")
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
		parameters.put("highRiskType", highRiskType);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销肿瘤确诊记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销某人的全部肿瘤确诊记录
	 * @param empiId
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午3:13:33
	 * @Modify:
	 */
	public void logoutAllTC(String empiId, String cancellationReason)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourConfirmed")
				.append(" set status = :status, ")
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
					ServiceCode.CODE_DATABASE_ERROR, "注销某人的全部肿瘤确诊记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复肿确诊记录
	 * @param TPRCID
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午4:45:04
	 * @Modify:
	 */
	public void revertTCRecord(String TCID) throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_TumourConfirmed")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where TCID = :TCID ")
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
		parameters.put("TCID", TCID);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复肿确诊记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复该人员的全部肿确诊记录
	 * @param TCID
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午5:03:59
	 * @Modify:
	 */
	public void revertAllTCRecord(String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ").append("MDC_TumourConfirmed")
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复该人员的全部肿确诊记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:是否传报
	 * @param notification
	 * @param empiId
	 * @param highRiskType
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-26 下午4:38:53
	 * @Modify:
	 */
	public void updateNotification(String notification, String empiId,
			String highRiskType) throws ModelDataOperationException {
		String hql = new StringBuffer("update ").append(MDC_TumourConfirmed)
				.append(" set notification=:notification ")
				.append(" where empiId=:empiId")
				.append(" and highRiskType=:highRiskType").toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("notification", notification);
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		try {
			dao.doUpdate(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATE_PASE_ERROR, "修改确诊记录中是否传报失败！", e);
		}
	}

	/**
	 * 
	 * @Description:判断是否已经存在肿瘤确诊记录
	 * @param empiId
	 * @param highRiskType
	 * @return true : 存在 ，false : 不存在
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-1-15 下午2:39:21
	 * @Modify:
	 */
	public boolean isTumourConfirmed(String empiId, String highRiskType)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourConfirmed)
				.append(" where empiId=:empiId and highRiskType=:highRiskType")
				.toString();
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("empiId", empiId);
		pMap.put("highRiskType", highRiskType);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断是否已经存在肿瘤确诊记录失败！", e);
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
	 * @Description:保存肿瘤专家评审记录
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-22 下午6:17:31
	 * @Modify:
	 */
	public Map<String, Object> saveTumourExpertReview(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourExpertReview, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤专家评审记录时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤专家评审记录时失败！", e);
		}
		return rsMap;
	}

	// ---------------------------癌前期管理------------------------------------------------
	/**
	 * 
	 * @Description:分页加载肿瘤确诊记录
	 * @param req
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-22 下午3:47:05
	 * @Modify:
	 */
	public Map<String, Object> loadTPPageList(Map<String, Object> req)
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
		// String queryCndsType = null;
		// if (req.containsKey("queryCndsType")) {
		// queryCndsType = (String) req.get("queryCndsType");
		// }
		// String sortInfo = null;
		// if (req.containsKey("sortInfo")) {
		// sortInfo = (String) req.get("sortInfo");
		// }
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		// 组装SQL
		Schema sc = null;
		try {
			sc = SchemaController.instance().get(MDC_TumourConfirmedPageList);
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		StringBuffer sfBuffer = new StringBuffer();
		for (SchemaItem si : sc.getItems()) {
			if (si.hasProperty("refAlias") || si.isVirtual()) {
				String ref = (String) si.getProperty("ref");
				sfBuffer.append(",").append(ref).append(" as ")
						.append(si.getProperty("refItemId"));
			} else {
				String f = si.getId();
				sfBuffer.append(",a.").append(f).append(" as ").append(f);
			}
		}
		String where = "";
		try {
			where = ExpressionProcessor.instance().toString(queryCnd);
		} catch (ExpException e) {
			throw new ModelDataOperationException(Constants.CODE_EXP_ERROR,
					"查询表达式转SQL失败", e);
		}
		StringBuffer from = new StringBuffer();
		from.append(" from ")
				.append(" MDC_TumourConfirmed a join MPI_DemographicInfo b on a.empiId=b.empiId ")
				.append(" join EHR_HealthRecord c on a.empiId=c.empiId ")
				.append(" left join MDC_TumourExpertReview d on a.TCID=d.TCID ");
		StringBuffer countSQL = new StringBuffer(
				"select count(a.TCID) as totalCount ").append(from);
		if (S.isNotEmpty(where)) {
			countSQL.append(" where ").append(where);
		}
		Map<String, Object> pMap = new HashMap<String, Object>();
		List<Map<String, Object>> tctcList = null;
		try {
			tctcList = dao.doSqlQuery(countSQL.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划统计总记录数时失败！", e);
		}
		long totalCount = 0;
		if (tctcList != null && tctcList.size() > 0) {
			Map<String, Object> trMap = tctcList.get(0);
			totalCount = ((BigDecimal) trMap.get("TOTALCOUNT")).longValue();
		}
		if (totalCount > 0) {
			StringBuffer sql = new StringBuffer();
			sql.append("select ").append(sfBuffer.substring(1)).append(from);
			if (S.isNotEmpty(where)) {
				sql.append(" where ").append(where);
			}
			sql.append(" order by d.assessDate desc ");
			int first = (pageNo - 1) * pageSize;
			pMap.put("first", first);
			pMap.put("max", pageSize);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = dao.doSqlQuery(sql.toString(), pMap);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "分页查询肿瘤高危随访计划时失败！", e);
			}
			res.put("totalCount", totalCount);
			if (rsList != null && rsList.size() > 0) {
				List<Map<String, Object>> tcList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> rMap : rsList) {
					Map<String, Object> tcMap = new HashMap<String, Object>();
					for (SchemaItem si : sc.getItems()) {
						if (si.hasProperty("refAlias") || si.isVirtual()) {
							String refItemId = (String) si
									.getProperty("refItemId");
							Object refItemValObject = rMap.get(refItemId
									.toUpperCase());
							if (refItemId.equals("assessment")
									&& refItemValObject == null) {
								refItemValObject = "0";
							}
							tcMap.put(refItemId, refItemValObject);
						} else {
							String f = si.getId();
							Object fv = rMap.get(f.toUpperCase());
							tcMap.put(f, fv);
						}
					}
					Date birthday = (Date) tcMap.get("birthday");
					if (birthday != null) {
						tcMap.put("age",
								BSCHISUtil.calculateAge(birthday, null));
					}
					tcList.add(tcMap);
				}
				res.put("body", SchemaUtil.setDictionaryMessageForList(tcList,
						MDC_TumourPrecancerListView));
			} else {
				res.put("body", new ArrayList<Map<String, Object>>());
			}
		} else {
			res.put("totalCount", 0);
			res.put("body", new ArrayList<Map<String, Object>>());
		}
		res.put("pageSize", pageSize);
		res.put("pageNo", pageNo);
		return res;
	}

	/**
	 * 
	 * @Description:加载肿瘤专家评定
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2015-3-24 上午10:21:07
	 * @Modify:
	 */
	public Map<String, Object> getTumourConfirmedReview(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_TumourExpertReview, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "加载肿瘤专家评定失败！", e);
		}
		return rsMap;
	}
}
