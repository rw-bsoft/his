/**
 * @(#)TumourPatientReportCardModel.java Created on 2014-4-27 下午3:31:01
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.WebApplicationContext;

import ctd.account.UserRoleToken;
import ctd.util.AppContextHolder;
import ctd.validator.ValidateException;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.PlanStatus;
import chis.source.dic.YesNo;
import chis.source.service.ServiceCode;
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
public class TumourPatientReportCardModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourPatientReportCardModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:
	 * @param empiId
	 * @param highRiskType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-15 下午2:14:03
	 * @Modify:
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> getTPRC(String empiId, String highRiskType)
			throws ModelDataOperationException {
		List<Object> cnd1 = new CNDHelper().createSimpleCnd("eq", "a.empiId",
				"s", empiId);
		List<Object> cnd2 = new CNDHelper().createSimpleCnd("eq",
				"a.highRiskType", "s", highRiskType);
		List<Object> cnd = new CNDHelper().createArrayCnd("and", cnd1, cnd2);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doList(cnd, "a.TPRCID desc",
					MDC_TumourPatientReportCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤档案失败！", e);
		}
		Map<String, Object> tprcMap = null;
		if (rsList != null && rsList.size() > 0) {
			tprcMap = rsList.get(0);
		}
		return tprcMap;
	}

	/**
	 * 
	 * @Description:获取肿瘤患者记录
	 * @param TPRCID
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-28 下午4:37:58
	 * @Modify:
	 */
	public Map<String, Object> getTPRCbyPkey(String TPRCID)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(MDC_TumourPatientReportCard, TPRCID);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤患者报卡记录失败！", e);
		}
		return rsMap;
	}

	public List<Map<String, Object>> getTPRCbyPhrId(String phrId)
			throws ModelDataOperationException {
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.phrId", "s", phrId);
		try {
			List<Map<String, Object>> list = dao.doList(cnd, null,
					MDC_TumourPatientReportCard);
			return list;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤患者报卡记录失败！", e);
		}
	}

	/**
	 * 
	 * @Description:获取肿瘤患者核实信息
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午9:03:04
	 * @Modify:
	 */
	public List<Map<String, Object>> getTumourBaseCheckInfo(String TPRCID)
			throws ModelDataOperationException {
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "TPRCID", "s",
				TPRCID);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(cnd, "firstVisitId desc",
					MDC_TumourPatientBaseCase);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取肿瘤患者核实信息失败！", e);
		}
		return rsList;
	}

	/**
	 * 
	 * @Description:获取首次随访记录
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午9:52:10
	 * @Modify:
	 */
	public Map<String, Object> getFirstVisitRecord(String TPRCID)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append(MDC_TumourPatientVisit)
				.append(" where firstVisit=:firstVisit and TPRCID=:TPRCID")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("firstVisit", YesNo.YES);
		parameters.put("TPRCID", TPRCID);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取首次随访记录失败！", e);
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (rsList != null && rsList.size() > 0) {
			rsMap = rsList.get(0);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存肿瘤患者报告卡信息
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午10:19:58
	 * @Modify:
	 */
	public Map<String, Object> saveTPRC(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourPatientReportCard, record,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者报告卡数据时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者报告卡数据失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存肿瘤患者基础核实数据
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午10:27:14
	 * @Modify:
	 */
	public Map<String, Object> saveTPBC(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, MDC_TumourPatientBaseCase, recordMap, true);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者基础核实数据时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤患者基础核实数据失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:保存首次随访信息
	 * @param op
	 * @param recordMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午10:32:27
	 * @Modify:
	 */
	public Map<String, Object> saveTPFV(String op,
			Map<String, Object> recordMap, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			recordMap.put("firstVisit", YesNo.YES);
			rsMap = dao.doSave(op, MDC_TumourPatientFirstVisit, recordMap,
					validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首次随访信息时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存首次随访信息失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:更新肿瘤患者首次随访计划
	 * @param empiId
	 * @param visitId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-29 上午11:25:20
	 * @Modify:
	 */
	public void updateTPFirstVisitPlan(String TPRCID, String visitId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"update PUB_VisitPlan set visitId=:visitId ")
				.append(" ,planStatus=:planStatus")
				.append(" ,visitDate=:visitDate")
				.append(" ,lastModifyUser=:lastModifyUser")
				.append(" ,lastModifyUnit=:lastModifyUnit")
				.append(" ,lastModifyDate=:lastModifyDate")
				.append(" where recordId=:TPRCID")
				.append(" and businessType=:businessType")
				.append(" and planDate=:planDate").toString();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("visitId", visitId);
		paraMap.put("planStatus", PlanStatus.VISITED);
		paraMap.put("visitDate", new Date());
		paraMap.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		paraMap.put("lastModifyUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		paraMap.put("lastModifyDate", new Date());
		paraMap.put("TPRCID", TPRCID);
		paraMap.put("businessType", BusinessType.TPV);
		paraMap.put("planDate", new Date());
		try {
			dao.doUpdate(hql, paraMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "更新肿瘤患者的首次随访计划信息失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤患者报告卡
	 * @param phrId
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 下午2:06:00
	 * @Modify:
	 */
	public void logoutTPRCRecord(String TPRCID, String cancellationReason)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourPatientReportCard")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where TPRCID = :TPRCID ")
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
		parameters.put("TPRCID", TPRCID);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销肿瘤患者报告卡失败！", e);
		}
	}

	/**
	 * 
	 * @Description:注销肿瘤患者报告卡
	 * @param empiId
	 * @param highRiskType
	 *            高危类别
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-25 下午2:52:24
	 * @Modify:
	 */
	public void logoutTPRCRecord(String empiId, String highRiskType,
			String cancellationReason) throws ModelDataOperationException {
		Map<String, Object> rsMap = this.getTPRC(empiId, highRiskType);
		if (rsMap != null && rsMap.size() > 0) {
			String TPRCID = (String) rsMap.get("TPRCID");
			this.logoutTPRCRecord(TPRCID, cancellationReason);
			// 注释随访计划
			VisitPlanModel vpModel = new VisitPlanModel(dao);
			vpModel.logOutVisitPlan(vpModel.RECORDID, TPRCID, BusinessType.TPV);
		}
	}

	/**
	 * 
	 * @Description:注销该人员的全部肿瘤现患报卡
	 * @param TPRCID
	 * @param cancellationReason
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-18 下午3:20:03
	 * @Modify:
	 */
	public void logoutAllTPRCRecord(String empiId, String cancellationReason,
			String deadFlag, String deadDate)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		StringBuffer hql = new StringBuffer("update ")
				.append("MDC_TumourPatientReportCard")
				.append(" set status = :status, ").append(" isDeath=:isDeath,")
				.append(" deathDate=:deathDate,")
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
		parameters.put("isDeath", deadFlag);
		parameters.put("deathDate", BSCHISUtil.toDate(deadDate));
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
					ServiceCode.CODE_DATABASE_ERROR, "注销肿瘤患者报告卡失败！", e);
		}
	}

	/**
	 * 
	 * @Description:恢复肿瘤患者报告卡
	 * @param phrId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 下午2:07:26
	 * @Modify:
	 */
	public void revertTPRCRecord(String TPRCID)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("MDC_TumourPatientReportCard")
				.append(" set status = :status, ")
				.append(" lastModifyUser = :lastModifyUser, ")
				.append(" lastModifyDate = :lastModifyDate, ")
				.append(" lastModifyUnit = :lastModifyUnit, ")
				.append(" cancellationUser = :cancellationUser, ")
				.append(" cancellationDate = :cancellationDate, ")
				.append(" cancellationUnit = :cancellationUnit, ")
				.append(" cancellationReason = :cancellationReason ")
				.append(" where TPRCID = :TPRCID ")
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
		parameters.put("TPRCID", TPRCID);
		parameters.put("status1", "" + Constants.CODE_STATUS_WRITE_OFF);

		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "恢复肿瘤患者报告卡失败！", e);
		}
		// 恢复随访控制
		this.revertVisitControl(TPRCID);
	}

	/**
	 * 
	 * @Description:恢复随访控制
	 * @param TPRCID
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-10-8 下午3:45:03
	 * @Modify:
	 */
	private void revertVisitControl(String TPRCID)
			throws ModelDataOperationException {
		// 判断是否有终止管理的随访记录(已撤销 或 死亡)
		String vHQL = new StringBuffer(" from ").append(MDC_TumourPatientVisit)
				.append(" where annulControl='2' or isDeath='y' ").toString();
		List<Map<String, Object>> vList = null;
		try {
			vList = dao.doQuery(vHQL, new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "检查已撤销或死亡的随访记录失败！", e);
		}
		if (vList != null && vList.size() > 0) {
			for (Map<String, Object> vMap : vList) {
				String firstVisit = (String) vMap.get("firstVisit");
				String visitId = (String) vMap.get("visitId");
				// 删除首次随访记录
				try {
					dao.doRemove(visitId, MDC_TumourPatientVisit);
				} catch (PersistentDataOperationException e1) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "删除终止管理的随访记录失败！", e1);
				}
				if (YesNo.YES.equals(firstVisit)) {
					// 删除首次随访计划
					String dpvHQL = new StringBuffer("delete ")
							.append(PUB_VisitPlan)
							.append(" where businessType=:businessType and recordId=:recordId and visitId=:visitId")
							.toString();
					Map<String, Object> pMap = new HashMap<String, Object>();
					pMap.put("businessType", BusinessType.TPV);
					pMap.put("recordId", TPRCID);
					pMap.put("visitId", visitId);
					try {
						dao.doUpdate(dpvHQL, pMap);
					} catch (PersistentDataOperationException e) {
						throw new ModelDataOperationException(
								Constants.CODE_DATABASE_ERROR,
								"删除手动生成的首次随访计划失败！", e);
					}
				} else {
					String upvHQL = new StringBuffer("update ")
							.append(PUB_VisitPlan)
							.append(" set planStatus='0',visitId='',visitDate=null ")
							.append(" where businessType=:businessType and recordId=:recordId and visitId=:visitId")
							.toString();
					Map<String, Object> pMap = new HashMap<String, Object>();
					pMap.put("businessType", BusinessType.TPV);
					pMap.put("recordId", TPRCID);
					pMap.put("visitId", visitId);
					try {
						dao.doUpdate(upvHQL, pMap);
					} catch (PersistentDataOperationException e) {
						throw new ModelDataOperationException(
								Constants.CODE_DATABASE_ERROR, "恢复随访计划状态失败！", e);
					}
				}
			}
		}
		// 恢复随访计划 * 有首次随访 且 最后一条计划的endDate 大于当前日期
		String hfvHQL = new StringBuffer("select count(*) as fvNum from ")
				.append(MDC_TumourPatientVisit)
				.append(" where firstVisit='y' and annulControl='1' and isDeath='n' ")
				.toString();
		Map<String, Object> rsfvMap = null;
		try {
			rsfvMap = dao.doLoad(hfvHQL, new HashMap<String, Object>());
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询正常的首次随访记录失败！", e);
		}
		long fvNum = 0;
		if (rsfvMap != null && rsfvMap.size() > 0) {
			fvNum = (Long) rsfvMap.get("fvNum");
		}
		String vpHQL = new StringBuffer("select count(*) as recNum from ")
				.append(PUB_VisitPlan)
				.append(" where businessType='16' and recordId=:recordId")
				.append(" and endDate >=:currentDate").toString();
		Map<String, Object> pvMap = new HashMap<String, Object>();
		pvMap.put("recordId", TPRCID);
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
		// 判断是否要重新生成随访计划
		if (fvNum > 0 && recNum == 0) {// 重新生成计划
			// 取最后一次正常的随访记录
			String lvHQL = new StringBuffer(" from ")
					.append(MDC_TumourPatientVisit)
					.append(" where TPRCID=:TPRCID and annulControl='1' and isDeath='n' ")
					.append(" order by createDate desc").toString();
			List<Map<String, Object>> lvList = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("TPRCID", TPRCID);
			try {
				lvList = dao.doQuery(lvHQL, parameters);
			} catch (PersistentDataOperationException e) {
				throw new ModelDataOperationException(
						Constants.CODE_DATABASE_ERROR, "查询肿瘤现患最后一次正常的随访记录失败！",
						e);
			}
			if (lvList != null && lvList.size() > 0) {
				Map<String, Object> lvMap = lvList.get(0);
				int CSV = (Integer) lvMap.get("CSV");
				String empiId = (String) lvMap.get("empiId");
				Map<String, Object> genVPMap = new HashMap<String, Object>();
				genVPMap.put("empiId", empiId);
				genVPMap.put("recordId", TPRCID);
				genVPMap.put("businessType", BusinessType.TPV);
				genVPMap.put("createDate", BSCHISUtil.toString(new Date()));
				genVPMap.put("group", CSV + "");
				// 检查随访配置
				TumourPatientVisitModel tpvModel = new TumourPatientVisitModel(
						dao);
				tpvModel.hasPlanType((CSV + ""));
				// 生成计划
				WebApplicationContext wac = (WebApplicationContext) AppContextHolder
						.get();
				VisitPlanCreator visitPlanCreator = (VisitPlanCreator) wac
						.getBean("chis.visitPlanCreator");
				try {
					visitPlanCreator.create(genVPMap, dao.getContext());
				} catch (Exception e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "生成随访计划失败！", e);
				}
			}
		}
	}

	/**
	 * 
	 * @Description:恢复该人员的全部肿瘤患者报告卡
	 * @param phrId
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-4-30 下午2:07:26
	 * @Modify:
	 */
	public void revertAllTPRCRecord(String empiId)
			throws ModelDataOperationException {
		String userId = UserRoleToken.getCurrent().getUserId();
		String hql = new StringBuffer("update ")
				.append("MDC_TumourPatientReportCard")
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
					ServiceCode.CODE_DATABASE_ERROR, "恢复该人员的全部肿瘤患者报告卡失败！", e);
		}
		// 恢复随访控制
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s",
				empiId);
		List<Map<String, Object>> tprcList = null;
		try {
			tprcList = dao.doQuery(cnd, null, MDC_TumourPatientReportCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取该人的全部肿瘤现患档案失败！", e);
		}
		if (tprcList != null && tprcList.size() > 0) {
			for (Map<String, Object> tprcMap : tprcList) {
				String TPRCID = (String) tprcMap.get("TPRCID");
				this.revertVisitControl(TPRCID);
			}
		}
	}

	/**
	 * 
	 * @Description:取个人生活习惯中 吸烟情况
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-22 下午5:07:35
	 * @Modify:
	 */
	public Map<String, Object> getSmokeCaseOfLifeStyle(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select smokeFreqCode as smokingCode")
				.append(",secondSmokeSiteCode as passiveSmoking")
				.append(",smokeStartAge as smokingStartAge")
				.append(",smokeEndAge as stopSmokingAge")
				.append(",smokeCount as smokingNumber").append(" from ")
				.append(EHR_LifeStyle).append(" where empiId=:empiId")
				.append("").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		Map<String, Object> rsMap = null;
		try {
			List<Map<String, Object>> rsList = dao.doQuery(hql, parameters);
			if (rsList != null && rsList.size() > 0) {
				rsMap = rsList.get(0);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "取个人生活习惯中 吸烟情况 失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:获取个人既往史
	 * @param empiId
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-9-22 下午5:50:25
	 * @Modify:
	 */
	public Map<String, Object> getTumourPastHistory(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select pastHisTypeCode as pastHisTypeCode,diseaseText as diseaseText")
				.append(" from ")
				.append(EHR_PastHistory)
				.append(" where empiId=:empiId and diseaseCode in ('0706','0806','0906','1006') order by pastHistoryId desc")
				.toString();
		List<Map<String, Object>> rsList = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取个人既往史失败！", e);
		}
		Map<String, Object> rsMap = new HashMap<String, Object>();
		if (rsList != null && rsList.size() > 0) {
			Map<String, Object> phMap = rsList.get(0);
			String pastHisTypeCode = (String) phMap.get("pastHisTypeCode");
			if ("07".equals(pastHisTypeCode) || "08".equals(pastHisTypeCode)) {
				rsMap.put("relationshipCode", "02");
			}
			if ("09".equals(pastHisTypeCode)) {
				rsMap.put("relationshipCode", "03");
			}
			if ("10".equals(pastHisTypeCode)) {
				rsMap.put("relationshipCode", "04");
			}
			rsMap.put("tumourType", phMap.get("diseaseText"));
		}
		return rsMap;
	}
}
