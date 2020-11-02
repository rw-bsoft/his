/*
 * @(#)DownloadOffLineDataService.java Created on 2013年11月6日9:07:21
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mobile;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.account.AccountCenter;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

/**
 * @description
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a> 离线数据管理
 */
public class DownloadOffLineDataService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(DownloadOffLineDataService.class);
	private static ObjectMapper jsonMapper = new ObjectMapper();
	private static Map<String, String> allEmpiId = new HashMap<String, String>();

	@Override
	public void setTransactedActions(List<String> transactedActions) {
		// TODO Auto-generated method stub
		super.setTransactedActions(transactedActions);
		transactedActions.add("downLoadOffLineData");
		transactedActions.add("saveByValidityDays");
		transactedActions.add("saveByStatus");
	}

	/**
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	protected void doDownLoadOffLineData(final Map<String, Object> req,
			final Map<String, Object> res, BaseDAO dao_, final Context ctx)
			throws ServiceException, JsonGenerationException,
			JsonMappingException, IOException {
		final Map<String, Object> reqBody = (Map<String, Object>) req
				.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		final String manaDoctorId = reqBody.get("manaDoctorId").toString();
		final Map<String, List<?>> cndMap = createArrayCnd(reqBody);
		String urole = (String) req.get("urole");
		String password = (String) req.get("psw");
		String downLoadStatus = (String) req.get("downLoadStatus");
		final String recordId = (String) req.get("recordId");
		int index = urole.indexOf("@");
		String manaUnitId = urole.substring(0, index);
		String roleId = urole.substring(index + 1, urole.length());
		String currentDate = BSCHISUtil.toString(new Date(), null);
		String serverDate = "date('" + currentDate + "')";
		final List<?> cnd = CNDHelper.createSimpleCnd("eq", "recordId", "s",
				recordId);

		try {
			if ("0".equals(downLoadStatus)) {
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("userId", manaDoctorId);
				record.put("roleId", roleId);
				record.put("password", password);
				record.put("manaUnitId", manaUnitId);
				record.put("downLoadStatus", "0");
				record.put("downLoadDate", currentDate);
				Map<String, Object> resRecord = dao_.doSave("create",
						BSCHISEntryNames.DLL_DownLoadLog, record, true);
				res.put("downLoadStatus", "0");
				res.putAll(resRecord);
				res.put(RES_CODE, Constants.CODE_OK);
				res.put(RES_MESSAGE, "获取移动离线数据成功。");
			} else if ("1".equals(downLoadStatus)) {
				final Map<String, Object> log = dao_.doLoad(cnd,
						BSCHISEntryNames.DLL_DownLoadLog);

				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							putUserRoleToken(req);
							BaseDAO dao = new BaseDAO();
							final Map<String, Object> log = dao.doLoad(cnd,
									BSCHISEntryNames.DLL_DownLoadLog);
							String recordId_ = (String) log.get("recordId");
							List<?> cndDia = null;
							if (cndMap.containsKey("DIA")) {
								cndDia = cndMap.get("DIA");
							}
							List<?> cndHyp = null;
							if (cndMap.containsKey("HYP")) {
								cndHyp = cndMap.get("HYP");
							}
							List<?> cndOld = null;
							if (cndMap.containsKey("SENIOR")) {
								cndOld = cndMap.get("SENIOR");
							}
							List<?> cndFamily = null;
							if (cndMap.containsKey("FAMILY")) {
								cndFamily = cndMap.get("FAMILY");
							}
							List<?> cndPregant = null;
							if (cndMap.containsKey("PREGNANT")) {
								cndPregant = cndMap.get("PREGNANT");
							}
							Map<String, Object> dataMap = new HashMap<String, Object>();
							List<Map<String, Object>> visitPlan = new ArrayList<Map<String, Object>>();
							// 糖尿病随访
							if (null != cndDia && cndDia.size() > 0) {
								Map<String, Object> dataMapDia = doOffLineDiabetesData(
										cndDia, res, dao);
								if (dataMapDia != null && !dataMapDia.isEmpty()) {
									List<Map<String, Object>> visitPlanDataDia = (List<Map<String, Object>>) dataMapDia
											.get("PUB_VisitPlan");
									List<Map<String, Object>> preVisitDataDia = (List<Map<String, Object>>) dataMapDia
											.get("MDC_DiabetesVisit");
									List<Map<String, Object>> recordDataDia = (List<Map<String, Object>>) dataMapDia
											.get("MDC_DiabetesRecord");
									List<Map<String, Object>> medicineDataDia = (List<Map<String, Object>>) dataMapDia
											.get("MDC_DiabetesMedicine");
									if (visitPlanDataDia != null
											&& !visitPlanDataDia.isEmpty()) {
										visitPlan.addAll(visitPlanDataDia);
									}
									if (preVisitDataDia != null
											&& !preVisitDataDia.isEmpty()) {
										String preVisitDiaJsonStr = listToJsonString(preVisitDataDia);
										dataMap.put("MDC_DiabetesVisit"
												.toUpperCase(),
												preVisitDiaJsonStr);
									}
									if (recordDataDia != null
											&& !recordDataDia.isEmpty()) {
										String recordDatatDiaJsonStr = listToJsonString(recordDataDia);
										dataMap.put("MDC_DiabetesRecord"
												.toUpperCase(),
												recordDatatDiaJsonStr);
									}
									if (medicineDataDia != null
											&& !medicineDataDia.isEmpty()) {
										String medicineDatatDiaJsonStr = listToJsonString(medicineDataDia);
										dataMap.put("MDC_DiabetesMedicine"
												.toUpperCase(),
												medicineDatatDiaJsonStr);
									}
								}
							}
							// 高血压随访
							if (null != cndHyp && cndHyp.size() > 0) {
								Map<String, Object> dataMapHyp = doOffLineHyperData(
										cndHyp, res, dao);
								if (dataMapHyp != null && !dataMapHyp.isEmpty()) {
									List<Map<String, Object>> visitPlanDataHyp = (List<Map<String, Object>>) dataMapHyp
											.get("PUB_VisitPlan");
									List<Map<String, Object>> preVisitDataHyp = (List<Map<String, Object>>) dataMapHyp
											.get("MDC_HypertensionVisit");
									List<Map<String, Object>> recordDataHyp = (List<Map<String, Object>>) dataMapHyp
											.get("MDC_HypertensionRecord");
									List<Map<String, Object>> medicineDataHyp = (List<Map<String, Object>>) dataMapHyp
											.get("MDC_HypertensionMedicine");
									List<Map<String, Object>> fixGroupDataHyp = (List<Map<String, Object>>) dataMapHyp
											.get("MDC_HypertensionFixGroup");
									if (visitPlanDataHyp != null
											&& !visitPlanDataHyp.isEmpty()) {
										visitPlan.addAll(visitPlanDataHyp);
									}
									if (preVisitDataHyp != null
											&& !preVisitDataHyp.isEmpty()) {
										String preVisitOldJsonStr = listToJsonString(preVisitDataHyp);
										dataMap.put("MDC_HypertensionVisit"
												.toUpperCase(),
												preVisitOldJsonStr);
									}
									if (recordDataHyp != null
											&& !recordDataHyp.isEmpty()) {
										String recordDataHypJsonStr = listToJsonString(recordDataHyp);
										dataMap.put("MDC_HypertensionRecord"
												.toUpperCase(),
												recordDataHypJsonStr);
									}
									if (medicineDataHyp != null
											&& !medicineDataHyp.isEmpty()) {
										String medicineDatatHypJsonStr = listToJsonString(medicineDataHyp);
										dataMap.put("MDC_HypertensionMedicine"
												.toUpperCase(),
												medicineDatatHypJsonStr);
									}
									if (fixGroupDataHyp != null
											&& !fixGroupDataHyp.isEmpty()) {
										String fixGroupDatatHypJsonStr = listToJsonString(fixGroupDataHyp);
										dataMap.put("MDC_HypertensionFixGroup"
												.toUpperCase(),
												fixGroupDatatHypJsonStr);
									}
								}
							}
							// 老年人随访
							if (null != cndOld && cndOld.size() > 0) {
								Map<String, Object> dataMapOld = doOffLineOldPeopleData(
										cndOld, res, dao);
								if (dataMapOld != null && !dataMapOld.isEmpty()) {
									List<Map<String, Object>> visitPlanDataOld = (List<Map<String, Object>>) dataMapOld
											.get("PUB_VisitPlan");
									List<Map<String, Object>> preVisitDataOld = (List<Map<String, Object>>) dataMapOld
											.get("MDC_OldPeopleVisit");
									List<Map<String, Object>> recordDataOld = (List<Map<String, Object>>) dataMapOld
											.get("MDC_OldPeopleRecord");
									if (visitPlanDataOld != null
											&& !visitPlanDataOld.isEmpty()) {
										visitPlan.addAll(visitPlanDataOld);
									}
									if (preVisitDataOld != null
											&& !preVisitDataOld.isEmpty()) {
										String preVisitDiaJsonStr = listToJsonString(preVisitDataOld);
										dataMap.put("MDC_OldPeopleVisit"
												.toUpperCase(),
												preVisitDiaJsonStr);
									}
									if (recordDataOld != null
											&& !recordDataOld.isEmpty()) {
										String recordDatatOldJsonStr = listToJsonString(recordDataOld);
										dataMap.put("MDC_OldPeopleRecord"
												.toUpperCase(),
												recordDatatOldJsonStr);
									}
								}
							}
							// 妇幼保健
							if (cndPregant != null && cndPregant.size() > 0) {
								Map<String, Object> dataMapPregnant = doOffLinePregnantBabyData(
										cndPregant, res, dao);
								if (dataMapPregnant != null
										&& !dataMapPregnant.isEmpty()) {
									List<Map<String, Object>> pregnantRecord = (List<Map<String, Object>>) dataMapPregnant
											.get("MHC_PregnantRecord");
									List<Map<String, Object>> postnatalVisitInfo = (List<Map<String, Object>>) dataMapPregnant
											.get("MHC_PostnatalVisitInfo");
									List<Map<String, Object>> postnatal42dayRecord = (List<Map<String, Object>>) dataMapPregnant
											.get("MHC_Postnatal42dayRecord");
									List<Map<String, Object>> babyVisitInfo = (List<Map<String, Object>>) dataMapPregnant
											.get("MHC_BabyVisitInfo");
									List<Map<String, Object>> babyVisitRecord = (List<Map<String, Object>>) dataMapPregnant
											.get("MHC_BabyVisitRecord");

									if (pregnantRecord != null
											&& !pregnantRecord.isEmpty()) {
										String pregnantRecordJsonStr = listToJsonString(pregnantRecord);
										dataMap.put("MHC_PregnantRecord"
												.toUpperCase(),
												pregnantRecordJsonStr);
									}
									if (postnatalVisitInfo != null
											&& !postnatalVisitInfo.isEmpty()) {
										String postnatalVisitInfoJsonStr = listToJsonString(postnatalVisitInfo);
										dataMap.put("MHC_PostnatalVisitInfo"
												.toUpperCase(),
												postnatalVisitInfoJsonStr);
									}
									if (postnatal42dayRecord != null
											&& !postnatal42dayRecord.isEmpty()) {
										String postnatal42dayRecordJsonStr = listToJsonString(postnatal42dayRecord);
										dataMap.put("MHC_Postnatal42dayRecord"
												.toUpperCase(),
												postnatal42dayRecordJsonStr);
									}
									if (babyVisitInfo != null
											&& !babyVisitInfo.isEmpty()) {
										String babyVisitInfoJsonStr = listToJsonString(babyVisitInfo);
										dataMap.put("MHC_BabyVisitInfo"
												.toUpperCase(),
												babyVisitInfoJsonStr);
									}
									if (babyVisitRecord != null
											&& !babyVisitRecord.isEmpty()) {
										String babyVisitRecordJsonStr = listToJsonString(babyVisitRecord);
										dataMap.put("MHC_BabyVisitRecord"
												.toUpperCase(),
												babyVisitRecordJsonStr);
									}
								}
							}
							// 家医服务模块
							if (cndFamily != null && !cndFamily.isEmpty()) {
								Map<String, Object> dataMapFamilyDoctor = doOffLineFamilyDoctorData(
										cndFamily, res, dao);
								if (dataMapFamilyDoctor != null
										&& !dataMapFamilyDoctor.isEmpty()) {
									List<Map<String, Object>> familyRecord = (List<Map<String, Object>>) dataMapFamilyDoctor
											.get("EHR_FamilyRecord");
									List<Map<String, Object>> familyContractBase = (List<Map<String, Object>>) dataMapFamilyDoctor
											.get("EHR_FamilyContractBase");
									List<Map<String, Object>> familyContractService = (List<Map<String, Object>>) dataMapFamilyDoctor
											.get("EHR_FamilyContractService");
									if (familyRecord != null
											&& !familyRecord.isEmpty()) {
										String familyRecordJsonStr = listToJsonString(familyRecord);
										dataMap.put("EHR_FamilyRecord"
												.toUpperCase(),
												familyRecordJsonStr);
									}
									if (familyContractBase != null
											&& !familyContractBase.isEmpty()) {
										String familyContractBaseJsonStr = listToJsonString(familyContractBase);
										dataMap.put("EHR_FamilyContractBase"
												.toUpperCase(),
												familyContractBaseJsonStr);
									}
									if (familyContractService != null
											&& !familyContractService.isEmpty()) {
										String familyContractServiceJsonStr = listToJsonString(familyContractService);
										dataMap.put("EHR_FamilyContractService"
												.toUpperCase(),
												familyContractServiceJsonStr);
									}
								}

							}
							// 基本信息、健康档案、生活习惯和既往史
							Map<String, Object> dataMapEhr = doOffLineEhrData(
									res, dao);
							if (dataMapEhr != null && !dataMapEhr.isEmpty()) {
								List<Map<String, Object>> baseInfoData = (List<Map<String, Object>>) dataMapEhr
										.get("MPI_DemographicInfo");
								List<Map<String, Object>> ehrRecordData = (List<Map<String, Object>>) dataMapEhr
										.get("EHR_HealthRecord");
								List<Map<String, Object>> lifeStyleData = (List<Map<String, Object>>) dataMapEhr
										.get("EHR_LifeStyle");
								List<Map<String, Object>> pastHistoryData = (List<Map<String, Object>>) dataMapEhr
										.get("EHR_PastHistory");
								if (baseInfoData != null
										&& !baseInfoData.isEmpty()) {
									String baseInfoDataJsonStr = listToJsonString(baseInfoData);
									dataMap.put(
											"MPI_DemographicInfo".toUpperCase(),
											baseInfoDataJsonStr);
								}
								if (ehrRecordData != null
										&& !ehrRecordData.isEmpty()) {
									String ehrRecordDataJsonStr = listToJsonString(ehrRecordData);
									dataMap.put(
											"EHR_HealthRecord".toUpperCase(),
											ehrRecordDataJsonStr);
								}
								if (lifeStyleData != null
										&& !lifeStyleData.isEmpty()) {
									String lifeStyleDataJsonStr = listToJsonString(lifeStyleData);
									dataMap.put("EHR_LifeStyle".toUpperCase(),
											lifeStyleDataJsonStr);
								}
								if (pastHistoryData != null
										&& !pastHistoryData.isEmpty()) {
									String pastHistoryDataJsonStr = listToJsonString(pastHistoryData);
									dataMap.put(
											"EHR_PastHistory".toUpperCase(),
											pastHistoryDataJsonStr);
								}

							}
							dataMap.putAll(doOffLineMPMData(res, reqBody, dao));
							dataMap.putAll(doOffLineSystemConfigData(res, dao));
							String visitPlanStr = listToJsonString(visitPlan);
							dataMap.put("PUB_VisitPlan".toUpperCase(),
									visitPlanStr);
							String data = mapToJsonString(dataMap);
							ZipInputStream zipIn = ZipUtil.streamInput(data,
									manaDoctorId + recordId_);
						} catch (Exception e) {
							res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
							res.put(RES_MESSAGE, "获取移动离线数据失败。");
							e.printStackTrace();
						}
					}
				}).start();
				log.put("downLoadStatus", "1");
				dao_.doSave("update", BSCHISEntryNames.DLL_DownLoadLog, log,
						false);
				Session ss = (Session) ctx.get(Context.DB_SESSION);
				ss.flush();

			}
		} catch (PersistentDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取移动离线数据失败。");
			e.printStackTrace();
		}
	}

	/**
	 * 糖尿病相关数据
	 * 
	 * @param cnd
	 * @param res
	 * @param dao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> doOffLineDiabetesData(List<?> cnd,
			Map<String, Object> res, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 随访计划
		List<Map<String, Object>> visitPlanAllDataList = new ArrayList<Map<String, Object>>();
		// 当天的随访计划
		List<Map<String, Object>> visitPlanTodayAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访计划
		List<Map<String, Object>> visitPlanPreAllDataList = new ArrayList<Map<String, Object>>();
		// 下次随访计划
		List<Map<String, Object>> visitPlanNextAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访信息
		List<Map<String, Object>> preVisitInfoAllDataList = new ArrayList<Map<String, Object>>();
		// 服药情况
		List<Map<String, Object>> visitMedicineAllDataList = new ArrayList<Map<String, Object>>();
		// 糖尿病档案
		List<Map<String, Object>> recordAllDataList = new ArrayList<Map<String, Object>>();
		if (cnd == null || cnd.isEmpty()) {
			return allData;
		}
		// --------start----------随访计划------------------------------------------------
		try {
			visitPlanTodayAllDataList = dao.doList(cnd, null, PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to get Today Diabetes PUB_VisitPlan !");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取当天糖尿病随访计划失败!");
			e.printStackTrace();
		}
		if (null == visitPlanTodayAllDataList
				|| visitPlanTodayAllDataList.isEmpty()) {
			return allData;
		}
		// --------end----------随访计划------------------------------------------------
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			String planId = (String) map.get("planId");
			String recordId = (String) map.get("recordId");
			allEmpiId.put((String) map.get("empiId"),
					(String) map.get("empiId"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planId", planId);
			parameters.put("recordId", recordId);
			// --------start----------下次随访计划------------------------------------------------
			StringBuffer nextVisitPlanHql = new StringBuffer(
					"select min(planId) as planId from PUB_VisitPlan t  ")
					.append(" where  t.planId > :planId").append(" and  ")
					.append(" t.recordId =  :recordId");

			Map<String, Object> nextVisitPlan = null;
			try {
				nextVisitPlan = dao.doLoad(nextVisitPlanHql.toString(),
						parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of next VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取糖尿病下次随访计划的planId失败!");
				e.printStackTrace();
			}
			if (nextVisitPlan != null && !nextVisitPlan.isEmpty()
					&& null != nextVisitPlan.get("planId")) {
				String nextPlanId = (String) nextVisitPlan.get("planId");
				List<?> cndNextPlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", nextPlanId);
				try {
					List<Map<String, Object>> nextVisitPlanDataList = dao
							.doList(cndNextPlan, null, PUB_VisitPlan);
					if (null != nextVisitPlanDataList
							&& !nextVisitPlanDataList.isEmpty()) {
						Map<String, Object> nextVisitPlanDataMap = nextVisitPlanDataList
								.get(0);
						nextVisitPlanDataMap.put("type", "2");
						nextVisitPlanDataList.clear();
						nextVisitPlanDataList.add(nextVisitPlanDataMap);
						visitPlanNextAllDataList.addAll(nextVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get next VisitPlan !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取糖尿病下次随访计划的失败!");
					e.printStackTrace();
				}

			}
			// --------end----------下次随访计划------------------------------------------------
			// --------start----------上次随访计划------------------------------------------------
			StringBuffer preHql = new StringBuffer(
					"select max(planId) as planId  from PUB_VisitPlan t  ")
					.append(" where  t.planId < :planId").append(" and  ")
					.append(" t.recordId =  :recordId");
			Map<String, Object> preVisitPlan = null;
			try {
				preVisitPlan = dao.doLoad(preHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of pre VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取糖尿病上次次随访计划的planId失败!");
				e.printStackTrace();
			}
			List<Map<String, Object>> preVisitPlanDataList = null;
			if (preVisitPlan != null && !preVisitPlan.isEmpty()
					&& null != preVisitPlan.get("planId")) {
				String prePlanId = (String) preVisitPlan.get("planId");
				List<?> cndPrePlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", prePlanId);

				try {
					preVisitPlanDataList = dao.doList(cndPrePlan, null,
							PUB_VisitPlan);
					if (null != preVisitPlanDataList
							&& !preVisitPlanDataList.isEmpty()) {
						Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
								.get(0);
						preVisitPlanDataMap.put("type", "0");
						preVisitPlanDataList.clear();
						preVisitPlanDataList.add(preVisitPlanDataMap);
						visitPlanPreAllDataList.addAll(preVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get pre VisitPlan !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取糖尿病上次随访计划的失败!");
					e.printStackTrace();
				}

			}
			// --------end----------上次随访计划------------------------------------------------
			// --------start----------上次随访信息------------------------------------------------
			if (preVisitPlanDataList != null && !preVisitPlanDataList.isEmpty()) {
				Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
						.get(0);
				if (null != preVisitPlanDataMap.get("visitId")
						&& !"".equals(preVisitPlanDataMap.get("visitId"))) {
					String preVisitId = (String) preVisitPlanDataMap
							.get("visitId");
					List<?> cndPreVisitId = CNDHelper.createSimpleCnd("eq",
							"a.visitId", "s", preVisitId);
					try {
						List<Map<String, Object>> preVisitInfoData = dao
								.doList(cndPreVisitId, null, MDC_DiabetesVisit);
						if (null != preVisitInfoData
								&& !preVisitInfoData.isEmpty()) {
							preVisitInfoAllDataList.addAll(preVisitInfoData);
						}
					} catch (PersistentDataOperationException e) {
						logger.info("failed to get pre Visit Data  !");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取糖尿病上次随访信息的失败!");
						e.printStackTrace();
					}
				}
			}
			// --------end----------上次随访信息------------------------------------------------
			// --------start----------服药情况------------------------------------------------
			if (preVisitPlanDataList != null && !preVisitPlanDataList.isEmpty()) {// 上次随访服药情况
				Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
						.get(0);
				if (null != preVisitPlanDataMap.get("visitId")
						&& !"".equals(preVisitPlanDataMap.get("visitId"))) {
					String preVisitId = (String) preVisitPlanDataMap
							.get("visitId");
					List<?> cndPreVisitId = CNDHelper.createSimpleCnd("eq",
							"a.visitId", "s", preVisitId);
					try {
						List<Map<String, Object>> preVisitMedicineData = dao
								.doList(cndPreVisitId, null,
										MDC_DiabetesMedicine);
						if (null != preVisitMedicineData
								&& !preVisitMedicineData.isEmpty()) {
							visitMedicineAllDataList
									.addAll(preVisitMedicineData);
						}
					} catch (PersistentDataOperationException e) {
						logger.info("failed to get Diabetes medicine  !");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取糖尿病上次随访服药情况失败!");
						e.printStackTrace();
					}
				}
			} else {// 上次随访不存在,查询糖尿病档案服药情况
				List<?> cndPhrId = CNDHelper.createSimpleCnd("eq", "a.phrId",
						"s", recordId);
				try {
					List<Map<String, Object>> recordMedicineData = dao.doList(
							cndPhrId, null, MDC_DiabetesMedicine);
					if (null != recordMedicineData
							&& !recordMedicineData.isEmpty()) {
						visitMedicineAllDataList.addAll(recordMedicineData);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get Diabetes medicine  !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取糖尿病档案服药情况失败!");
					e.printStackTrace();
				}
			}
			// --------end----------服药情况------------------------------------------------
			// --------start----------糖尿病档案------------------------------------------------
			List<?> cndPhrId = CNDHelper.createSimpleCnd("eq", "a.phrId", "s",
					recordId);
			try {
				List<Map<String, Object>> recordData = dao.doList(cndPhrId,
						null, MDC_DiabetesRecord);
				if (null != recordData && !recordData.isEmpty()) {
					recordAllDataList.addAll(recordData);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get Diabetes record  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取糖尿病档案失败!");
				e.printStackTrace();
			}
			// --------end----------糖尿病档案------------------------------------------------
		}
		List<Map<String, Object>> visitPlanTodayAllDataTypeList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			map.put("type", "1");
			map.put("isSubmit", "0");
			visitPlanTodayAllDataTypeList.add(map);
		}
		visitPlanAllDataList.addAll(visitPlanTodayAllDataTypeList);
		visitPlanAllDataList.addAll(visitPlanPreAllDataList);
		visitPlanAllDataList.addAll(visitPlanNextAllDataList);
		Map<String, Object> visitPlanAllDataMap = new HashMap<String, Object>();
		for (Map<String, Object> oneVisitPlan : visitPlanAllDataList) {
			String planId = (String) oneVisitPlan.get("planId");
			visitPlanAllDataMap.put(planId, oneVisitPlan);
		}
		Iterator<String> iterator = visitPlanAllDataMap.keySet().iterator();
		visitPlanAllDataList.clear();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Map<String, Object> value = (Map<String, Object>) visitPlanAllDataMap
					.get(key);
			visitPlanAllDataList.add(value);
		}
		allData.put("MDC_DiabetesMedicine", visitMedicineAllDataList);
		allData.put("PUB_VisitPlan", visitPlanAllDataList);
		allData.put("MDC_DiabetesVisit", preVisitInfoAllDataList);
		allData.put("MDC_DiabetesRecord", recordAllDataList);

		return allData;

	}

	/**
	 * 老年人相关数据
	 * 
	 * @param cnd
	 * @param res
	 * @param dao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> doOffLineOldPeopleData(List<?> cnd,
			Map<String, Object> res, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 随访计划
		List<Map<String, Object>> visitPlanAllDataList = new ArrayList<Map<String, Object>>();
		// 当天的随访计划
		List<Map<String, Object>> visitPlanTodayAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访计划
		List<Map<String, Object>> visitPlanPreAllDataList = new ArrayList<Map<String, Object>>();
		// 下次随访计划
		List<Map<String, Object>> visitPlanNextAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访信息
		List<Map<String, Object>> preVisitInfoAllDataList = new ArrayList<Map<String, Object>>();
		// 糖尿病档案
		List<Map<String, Object>> recordAllDataList = new ArrayList<Map<String, Object>>();
		if (cnd == null || cnd.isEmpty()) {
			return allData;
		}
		// --------start----------随访计划------------------------------------------------
		try {
			visitPlanTodayAllDataList = dao.doList(cnd, null, PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to get OldPeople PUB_VisitPlan !");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取老年人随访计划失败!");
			e.printStackTrace();
		}
		if (null == visitPlanTodayAllDataList
				|| visitPlanTodayAllDataList.isEmpty()) {
			return allData;
		}
		// --------end----------随访计划------------------------------------------------
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			String planId = (String) map.get("planId");
			String recordId = (String) map.get("recordId");
			allEmpiId.put((String) map.get("empiId"),
					(String) map.get("empiId"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planId", planId);
			parameters.put("recordId", recordId);
			// --------start----------下次随访计划------------------------------------------------
			StringBuffer nextVisitPlanHql = new StringBuffer(
					"select min(planId) as planId from PUB_VisitPlan t  ")
					.append(" where  t.planId > :planId").append(" and  ")
					.append(" t.recordId =  :recordId");

			Map<String, Object> nextVisitPlan = null;
			try {
				nextVisitPlan = dao.doLoad(nextVisitPlanHql.toString(),
						parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of next OldPeople VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取老年人下次随访计划的planId失败!");
				e.printStackTrace();
			}
			if (nextVisitPlan != null && !nextVisitPlan.isEmpty()
					&& null != nextVisitPlan.get("planId")
					&& !"".equals(nextVisitPlan.get("planId"))) {
				String nextPlanId = (String) nextVisitPlan.get("planId");
				List<?> cndNextPlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", nextPlanId);
				try {
					List<Map<String, Object>> nextVisitPlanDataList = dao
							.doList(cndNextPlan, null, PUB_VisitPlan);
					if (null != nextVisitPlanDataList
							&& !nextVisitPlanDataList.isEmpty()) {
						Map<String, Object> nextVisitPlanDataMap = nextVisitPlanDataList
								.get(0);
						nextVisitPlanDataMap.put("type", "2");
						nextVisitPlanDataList.clear();
						nextVisitPlanDataList.add(nextVisitPlanDataMap);
						visitPlanNextAllDataList.addAll(nextVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get next OldPeople VisitPlan !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取老年人下次随访计划的失败!");
					e.printStackTrace();
				}

			}
			// --------end----------下次随访计划------------------------------------------------
			// --------start----------上次随访计划------------------------------------------------
			StringBuffer preHql = new StringBuffer(
					"select max(planId) as planId from PUB_VisitPlan t  ")
					.append(" where  t.planId < :planId").append(" and  ")
					.append(" t.recordId =  :recordId");
			Map<String, Object> preVisitPlan = null;
			try {
				preVisitPlan = dao.doLoad(preHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of pre OldPeople VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取老年人上次次随访计划的planId失败!");
				e.printStackTrace();
			}
			List<Map<String, Object>> preVisitPlanDataList = null;
			if (preVisitPlan != null && !preVisitPlan.isEmpty()
					&& null != preVisitPlan.get("planId")
					&& !"".equals(preVisitPlan.get("planId"))) {
				String prePlanId = (String) preVisitPlan.get("planId");
				List<?> cndPrePlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", prePlanId);

				try {
					preVisitPlanDataList = dao.doList(cndPrePlan, null,
							PUB_VisitPlan);
					if (null != preVisitPlanDataList
							&& !preVisitPlanDataList.isEmpty()) {
						Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
								.get(0);
						preVisitPlanDataMap.put("type", "0");
						preVisitPlanDataList.clear();
						preVisitPlanDataList.add(preVisitPlanDataMap);
						visitPlanPreAllDataList.addAll(preVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get pre VisitPlan of OldPeople !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取老年人上次随访计划的失败!");
					e.printStackTrace();
				}

			}
			// --------end----------上次随访计划------------------------------------------------
			// --------start----------上次随访信息------------------------------------------------
			if (preVisitPlanDataList != null && !preVisitPlanDataList.isEmpty()) {
				Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
						.get(0);
				if (null != preVisitPlanDataMap.get("visitId")
						&& !"".equals(preVisitPlanDataMap.get("visitId"))) {
					String preVisitId = (String) preVisitPlanDataMap
							.get("visitId");
					List<?> cndPreVisitId = CNDHelper.createSimpleCnd("eq",
							"a.visitId", "s", preVisitId);
					try {
						List<Map<String, Object>> preVisitInfoData = dao
								.doList(cndPreVisitId, null, MDC_OldPeopleVisit);
						if (null != preVisitInfoData
								&& !preVisitInfoData.isEmpty()) {
							preVisitInfoAllDataList.addAll(preVisitInfoData);
						}
					} catch (PersistentDataOperationException e) {
						logger.info("failed to get pre Visit Data of OldPeopleVisit!");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取老年人上次随访信息的失败!");
						e.printStackTrace();
					}
				}
			}
			// --------end----------上次随访信息------------------------------------------------
			// --------start----------老年人档案------------------------------------------------
			List<?> cndPhrId = CNDHelper.createSimpleCnd("eq", "a.phrId", "s",
					recordId);
			try {
				List<Map<String, Object>> recordData = dao.doList(cndPhrId,
						null, MDC_OldPeopleRecord);
				if (null != recordData && !recordData.isEmpty()) {
					recordAllDataList.addAll(recordData);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get OldPeopleRecord  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取老年人档案失败!");
				e.printStackTrace();
			}
			// --------end----------老年人档案------------------------------------------------
		}
		List<Map<String, Object>> visitPlanTodayAllDataTypeList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			map.put("type", "1");
			map.put("isSubmit", "0");
			visitPlanTodayAllDataTypeList.add(map);
		}
		visitPlanAllDataList.addAll(visitPlanTodayAllDataTypeList);
		visitPlanAllDataList.addAll(visitPlanPreAllDataList);
		visitPlanAllDataList.addAll(visitPlanNextAllDataList);
		Map<String, Object> visitPlanAllDataMap = new HashMap<String, Object>();
		for (Map<String, Object> oneVisitPlan : visitPlanAllDataList) {
			String planId = (String) oneVisitPlan.get("planId");
			visitPlanAllDataMap.put(planId, oneVisitPlan);
		}
		Iterator<String> iterator = visitPlanAllDataMap.keySet().iterator();
		visitPlanAllDataList.clear();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Map<String, Object> value = (Map<String, Object>) visitPlanAllDataMap
					.get(key);
			visitPlanAllDataList.add(value);
		}
		allData.put("PUB_VisitPlan", visitPlanAllDataList);
		allData.put("MDC_OldPeopleVisit", preVisitInfoAllDataList);
		allData.put("MDC_OldPeopleRecord", recordAllDataList);
		return allData;

	}

	/**
	 * 妇幼保健相关数据
	 * 
	 * @param cnd
	 * @param res
	 * @param dao
	 * @return
	 */
	private Map<String, Object> doOffLinePregnantBabyData(List<?> cnd,
			Map<String, Object> res, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 孕妇档案
		List<Map<String, Object>> pregnantRecordDataList = new ArrayList<Map<String, Object>>();
		// 产后访视
		List<Map<String, Object>> postnatalVisitInfoDataList = new ArrayList<Map<String, Object>>();
		// 产后42天
		List<Map<String, Object>> postnatal42dayRecordDataList = new ArrayList<Map<String, Object>>();
		// 新生儿基本信息
		List<Map<String, Object>> babyVisitInfoDataList = new ArrayList<Map<String, Object>>();
		// 新生儿访视
		List<Map<String, Object>> babyVisitRecordDataList = new ArrayList<Map<String, Object>>();
		if (cnd == null || cnd.isEmpty()) {
			return allData;
		}
		// --------start---------孕妇基本信息------------------------------------------------
		try {
			pregnantRecordDataList = dao.doList(cnd, "pregnantId",
					MHC_PregnantRecord);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to get MHC_PregnantRecord  !");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取孕妇基本信息失败!");
			e.printStackTrace();
		}
		// --------end---------孕妇基本信息------------------------------------------------
		if (pregnantRecordDataList == null || pregnantRecordDataList.isEmpty()) {
			return allData;
		}
		for (Map<String, Object> map : pregnantRecordDataList) {
			String pregnantId = (String) map.get("pregnantId");
			String empiId = (String) map.get("empiId");
			allEmpiId.put(empiId, empiId);
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.pregnantId", "s",
					pregnantId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			// --------start---------产后访视信息------------------------------------------------
			try {
				List<Map<String, Object>> postnatalVisitInfoList = dao.doList(
						cnd2, "visitId", MHC_PostnatalVisitInfo);
				if (postnatalVisitInfoList != null
						&& !postnatalVisitInfoList.isEmpty()) {
					postnatalVisitInfoDataList.addAll(postnatalVisitInfoList);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get MHC_PostnatalVisitInfo  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取产后访视信息失败!");
				e.printStackTrace();
			}
			// --------end---------产后访视信息----------------------------------------------------------
			// --------start---------产后42天健康检查记录表------------------------------------------------
			try {
				List<Map<String, Object>> postnatal42dayRecordList = dao
						.doList(cnd2, "recordId", MHC_Postnatal42dayRecord);
				if (postnatal42dayRecordList != null
						&& !postnatal42dayRecordList.isEmpty()) {
					postnatal42dayRecordDataList
							.addAll(postnatal42dayRecordList);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get MHC_Postnatal42dayRecord  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取产后42天健康检查记录表失败!");
				e.printStackTrace();
			}
			// --------end---------产后42天健康检查记录表------------------------------------------------
			// --------start---------新生儿访视基本信息------------------------------------------------
			List<Map<String, Object>> babyVisitInfoList = null;
			try {
				babyVisitInfoList = dao.doList(cnd2, "babyId",
						MHC_BabyVisitInfo);
				if (babyVisitInfoList != null && !babyVisitInfoList.isEmpty()) {
					babyVisitInfoDataList.addAll(babyVisitInfoList);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get MHC_BabyVisitInfo  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取新生儿访视基本信息失败!");
				e.printStackTrace();
			}
			// --------end---------取出新生儿访视基本信息------------------------------------------------
			// --------start---------新生儿父母的empiId------------------------------------------------
			if (babyVisitInfoList != null && !babyVisitInfoList.isEmpty()) {
				for (Map<String, Object> map2 : babyVisitInfoList) {
					Object fatherEmpiId = map2.get("fatherEmpiId");
					if (fatherEmpiId != null) {
						allEmpiId.put(fatherEmpiId.toString(),
								fatherEmpiId.toString());
					}
				}
			}
			// --------end--------取出新生儿父母的empiId------------------------------------------------

			// --------start--------新生儿访视记录-----------------------------------------------
			try {
				List<Map<String, Object>> babyVisitRecordList = dao.doList(
						cnd1, "visitId", MHC_BabyVisitRecord);
				if (babyVisitRecordList != null
						&& !babyVisitRecordList.isEmpty()) {
					babyVisitRecordDataList.addAll(babyVisitRecordList);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get MHC_BabyVisitRecord  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取新生儿访视记录失败!");
				e.printStackTrace();
			}
			// --------end---------新生儿访视记录----------------------------------------------------------
		}
		allData.put("MHC_PregnantRecord", pregnantRecordDataList);
		allData.put("MHC_PostnatalVisitInfo", postnatalVisitInfoDataList);
		allData.put("MHC_Postnatal42dayRecord", postnatal42dayRecordDataList);
		allData.put("MHC_BabyVisitInfo", babyVisitInfoDataList);
		allData.put("MHC_BabyVisitRecord", babyVisitRecordDataList);
		return allData;

	}

	/**
	 * 高血压相关数据
	 * 
	 * @param cnd
	 * @param res
	 * @param dao
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> doOffLineHyperData(List<?> cnd,
			Map<String, Object> res, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 随访计划
		List<Map<String, Object>> visitPlanAllDataList = new ArrayList<Map<String, Object>>();
		// 当天的随访计划
		List<Map<String, Object>> visitPlanTodayAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访计划
		List<Map<String, Object>> visitPlanPreAllDataList = new ArrayList<Map<String, Object>>();
		// 下次随访计划
		List<Map<String, Object>> visitPlanNextAllDataList = new ArrayList<Map<String, Object>>();
		// 上次随访信息
		List<Map<String, Object>> preVisitInfoAllDataList = new ArrayList<Map<String, Object>>();
		// 服药情况
		List<Map<String, Object>> visitMedicineAllDataList = new ArrayList<Map<String, Object>>();
		// 高血压档案
		List<Map<String, Object>> recordAllDataList = new ArrayList<Map<String, Object>>();
		// 高血压评估
		List<Map<String, Object>> fixGroupAllDataList = new ArrayList<Map<String, Object>>();
		if (cnd == null || cnd.isEmpty()) {
			return allData;
		}
		// --------start----------随访计划------------------------------------------------
		try {
			visitPlanTodayAllDataList = dao.doList(cnd, null, PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to get Hyper PUB_VisitPlan !");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取高血压随访计划失败!");
			e.printStackTrace();
		}
		if (null == visitPlanTodayAllDataList
				|| visitPlanTodayAllDataList.isEmpty()) {
			return allData;
		}
		// --------end----------随访计划------------------------------------------------
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			String planId = (String) map.get("planId");
			String recordId = (String) map.get("recordId");
			allEmpiId.put((String) map.get("empiId"),
					(String) map.get("empiId"));
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("planId", planId);
			parameters.put("recordId", recordId);
			// --------start----------下次随访计划------------------------------------------------
			StringBuffer nextVisitPlanHql = new StringBuffer(
					"select min(planId) as planId from PUB_VisitPlan t  ")
					.append(" where  t.planId > :planId").append(" and  ")
					.append(" t.recordId =  :recordId");

			Map<String, Object> nextVisitPlan = null;
			try {
				nextVisitPlan = dao.doLoad(nextVisitPlanHql.toString(),
						parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of next VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取高血压下次随访计划的planId失败!");
				e.printStackTrace();
			}
			if (nextVisitPlan != null && !nextVisitPlan.isEmpty()
					&& null != nextVisitPlan.get("planId")) {
				String nextPlanId = (String) nextVisitPlan.get("planId");
				List<?> cndNextPlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", nextPlanId);
				List<Map<String, Object>> nextVisitPlanDataList = null;
				try {
					nextVisitPlanDataList = dao.doList(cndNextPlan, null,
							PUB_VisitPlan);
					if (null != nextVisitPlanDataList
							&& !nextVisitPlanDataList.isEmpty()) {
						Map<String, Object> nextVisitPlanDataMap = nextVisitPlanDataList
								.get(0);
						nextVisitPlanDataMap.put("type", "2");
						nextVisitPlanDataList.clear();
						nextVisitPlanDataList.add(nextVisitPlanDataMap);
						visitPlanNextAllDataList.addAll(nextVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get next VisitPlan !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取高血压下次随访计划的失败!");
					e.printStackTrace();
				}
			}
			// --------end----------下次随访计划------------------------------------------------
			// --------start----------上次随访计划------------------------------------------------
			StringBuffer preHql = new StringBuffer(
					"select max(planId) as planId from PUB_VisitPlan t  ")
					.append(" where  t.planId < :planId").append(" and  ")
					.append(" t.recordId =  :recordId");
			Map<String, Object> preVisitPlan = null;
			try {
				preVisitPlan = dao.doLoad(preHql.toString(), parameters);
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get  the planId of pre VisitPlan!");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取高血压上次次随访计划的planId失败!");
				e.printStackTrace();
			}
			List<Map<String, Object>> preVisitPlanDataList = null;
			if (preVisitPlan != null && !preVisitPlan.isEmpty()
					&& null != preVisitPlan.get("planId")) {
				String prePlanId = (String) preVisitPlan.get("planId");
				List<?> cndPrePlan = CNDHelper.createSimpleCnd("eq", "planId",
						"s", prePlanId);
				try {
					preVisitPlanDataList = dao.doList(cndPrePlan, null,
							PUB_VisitPlan);
					if (null != preVisitPlanDataList
							&& !preVisitPlanDataList.isEmpty()) {
						Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
								.get(0);
						preVisitPlanDataMap.put("type", "0");
						preVisitPlanDataList.clear();
						preVisitPlanDataList.add(preVisitPlanDataMap);
						visitPlanPreAllDataList.addAll(preVisitPlanDataList);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get pre VisitPlan !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取高血压上次随访计划的失败!");
					e.printStackTrace();
				}

			}
			// --------end----------上次随访计划------------------------------------------------
			// --------start----------上次随访信息------------------------------------------------
			if (preVisitPlanDataList != null && !preVisitPlanDataList.isEmpty()) {
				Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
						.get(0);
				if (null != preVisitPlanDataMap.get("visitId")
						&& !"".equals(preVisitPlanDataMap.get("visitId"))) {
					String preVisitId = (String) preVisitPlanDataMap
							.get("visitId");
					List<?> cndPreVisitId = CNDHelper.createSimpleCnd("eq",
							"a.visitId", "s", preVisitId);
					try {
						List<Map<String, Object>> preVisitInfoData = dao
								.doList(cndPreVisitId, null,
										MDC_HypertensionVisit);
						if (null != preVisitInfoData
								&& !preVisitInfoData.isEmpty()) {
							preVisitInfoAllDataList.addAll(preVisitInfoData);
						}
					} catch (PersistentDataOperationException e) {
						logger.info("failed to get pre Visit Data  !");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取高血压上次随访信息的失败!");
						e.printStackTrace();
					}
				}
			}
			// --------end----------上次随访信息------------------------------------------------
			// --------start----------服药情况------------------------------------------------
			if (preVisitPlanDataList != null && !preVisitPlanDataList.isEmpty()) {// 上次随访服药情况
				Map<String, Object> preVisitPlanDataMap = preVisitPlanDataList
						.get(0);
				if (null != preVisitPlanDataMap.get("visitId")
						&& !"".equals(preVisitPlanDataMap.get("visitId"))) {
					String preVisitId = (String) preVisitPlanDataMap
							.get("visitId");
					List<?> cndPreVisitId = CNDHelper.createSimpleCnd("eq",
							"a.visitId", "s", preVisitId);
					try {
						List<Map<String, Object>> preVisitMedicineData = dao
								.doList(cndPreVisitId, null,
										MDC_HypertensionMedicine);
						if (null != preVisitMedicineData
								&& !preVisitMedicineData.isEmpty()) {
							visitMedicineAllDataList
									.addAll(preVisitMedicineData);
						}
					} catch (PersistentDataOperationException e) {
						logger.info("failed to get HypertensionMedicine  !");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取上次随访服药情况失败!");
						e.printStackTrace();
					}
				}
			} else {// 上次随访不存在,查询高血压档案服药情况
				List<?> cndPhrId = CNDHelper.createSimpleCnd("eq", "a.phrId",
						"s", recordId);
				try {
					List<Map<String, Object>> recordMedicineData = dao.doList(
							cndPhrId, null, MDC_HypertensionMedicine);
					if (null != recordMedicineData
							&& !recordMedicineData.isEmpty()) {
						visitMedicineAllDataList.addAll(recordMedicineData);
					}
				} catch (PersistentDataOperationException e) {
					logger.info("failed to get HypertensionMedicine  !");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取高血压档案服药情况失败!");
					e.printStackTrace();
				}
			}
			// --------end----------服药情况------------------------------------------------
			// --------start----------高血压档案------------------------------------------------
			List<?> cndPhrId = CNDHelper.createSimpleCnd("eq", "a.phrId", "s",
					recordId);
			try {
				List<Map<String, Object>> recordData = dao.doList(cndPhrId,
						null, MDC_HypertensionRecord);
				if (null != recordData && !recordData.isEmpty()) {
					recordAllDataList.addAll(recordData);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get HypertensionRecord !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取高血压档案失败!");
				e.printStackTrace();
			}
			// --------end----------高血压档案------------------------------------------------
			// --------start----------高血压评估------------------------------------------------

			try {
				List<Map<String, Object>> fixGroupData = dao.doList(cndPhrId,
						null, MDC_HypertensionFixGroup);
				if (null != fixGroupData && !fixGroupData.isEmpty()) {
					fixGroupAllDataList.addAll(fixGroupData);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get HypertensionFixGroup  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取高血压评估失败!");
				e.printStackTrace();
			}
			// --------end----------高血压评估------------------------------------------------
		}
		List<Map<String, Object>> visitPlanTodayAllDataTypeList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : visitPlanTodayAllDataList) {
			map.put("type", "1");
			map.put("isSubmit", "0");
			visitPlanTodayAllDataTypeList.add(map);
		}
		visitPlanAllDataList.addAll(visitPlanTodayAllDataTypeList);
		visitPlanAllDataList.addAll(visitPlanPreAllDataList);
		visitPlanAllDataList.addAll(visitPlanNextAllDataList);
		Map<String, Object> visitPlanAllDataMap = new HashMap<String, Object>();
		for (Map<String, Object> oneVisitPlan : visitPlanAllDataList) {
			String planId = (String) oneVisitPlan.get("planId");
			visitPlanAllDataMap.put(planId, oneVisitPlan);
		}
		Iterator<String> iterator = visitPlanAllDataMap.keySet().iterator();
		visitPlanAllDataList.clear();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Map<String, Object> value = (Map<String, Object>) visitPlanAllDataMap
					.get(key);
			visitPlanAllDataList.add(value);
		}
		allData.put("PUB_VisitPlan", visitPlanAllDataList);
		allData.put("MDC_HypertensionRecord", recordAllDataList);
		allData.put("MDC_HypertensionFixGroup", fixGroupAllDataList);
		allData.put("MDC_HypertensionMedicine", visitMedicineAllDataList);
		allData.put("MDC_HypertensionVisit", preVisitInfoAllDataList);
		return allData;

	}

	private Map<String, Object> doOffLineEhrData(Map<String, Object> res,
			BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 基本信息
		List<Map<String, Object>> mpiDataList = new ArrayList<Map<String, Object>>();
		// 健康档案
		List<Map<String, Object>> ehrDataList = new ArrayList<Map<String, Object>>();
		// 生活习惯
		List<Map<String, Object>> lifeStyleDataList = new ArrayList<Map<String, Object>>();
		// 既往史
		List<Map<String, Object>> pastHistoryDataList = new ArrayList<Map<String, Object>>();
		Iterator<String> iterator = allEmpiId.keySet().iterator();
		while (iterator.hasNext()) {
			String empiId = iterator.next();
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
					empiId);
			try {
				List<Map<String, Object>> mpiDataMap = dao.doList(cnd, null,
						MPI_DemographicInfo);
				if (null != mpiDataMap && !mpiDataMap.isEmpty()) {
					mpiDataList.addAll(mpiDataMap);
				}
				List<Map<String, Object>> ehrDataMap = dao.doList(cnd, null,
						EHR_HealthRecord);
				if (null != ehrDataMap && !ehrDataMap.isEmpty()) {
					ehrDataList.addAll(ehrDataMap);
				}
				List<Map<String, Object>> lifeStyleDataMap = dao.doList(cnd,
						null, EHR_LifeStyle);
				if (null != lifeStyleDataMap && !lifeStyleDataMap.isEmpty()) {
					lifeStyleDataList.addAll(lifeStyleDataMap);
				}
				List<Map<String, Object>> pastHistoryData = dao.doList(cnd,
						"pastHistoryId", EHR_PastHistory);
				if (null != pastHistoryData && !pastHistoryData.isEmpty()) {
					pastHistoryDataList.addAll(pastHistoryData);
				}
			} catch (PersistentDataOperationException e) {
				logger.info("failed to get ehr info  !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取基本信息、健康档案、生活习惯和既往史失败!");
				e.printStackTrace();
			}

		}
		allData.put("MPI_DemographicInfo", mpiDataList);
		allData.put("EHR_HealthRecord", ehrDataList);
		allData.put("EHR_LifeStyle", lifeStyleDataList);
		allData.put("EHR_PastHistory", pastHistoryDataList);
		return allData;

	}

	/**
	 * 获取系统配置信息
	 * 
	 * @param dao
	 * @return
	 */
	private Map<String, Object> doOffLineSystemConfigData(
			Map<String, Object> res, BaseDAO dao) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> configMap = new HashMap<String, Object>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		SystemCofigManageModel scmm = new SystemCofigManageModel(dao);
		String[] configArr = { "oldPeopleStartMonth", "oldPeopleEndMonth",
				"oldPeopleAge", "2_planTypeCode", "4_planTypeCode",
				"4_visitIntervalSame", "hypertensionStartMonth",
				"hypertensionEndMonth", "diabetesStartMonth",
				"diabetesEndMonth", "childrenRegisterAge", "childrenDieAge",
				"childrenFirstVistDays", "5_planTypeCode", "6_planTypeCode",
				"5_visitIntervalSame", "6_visitIntervalSame", "7_planTypeCode",
				"debilityChildrenExceptionalCase", "8_planTypeCode",
				"8_visitIntervalSame", "10_visitIntervalSame",
				"psychosisStartMonth", "psychosisEndMonth",
				"hypertensionOther_planTypeCode", "1_planMode", "2_planMode",
				"4_planMode", "8_planMode", "10_planMode", "4_precedeDays",
				"4_delayDays", "1_precedeDays", "1_delayDays", "2_precedeDays",
				"5_precedeDays", "6_precedeDays", "7_precedeDays",
				"2_delayDays", "5_delayDays", "6_delayDays", "7_delayDays",
				"10_precedeDays", "10_delayDays", "11_precedeDays",
				"11_delayDays", "8_delayDays", "9_planMode", "9_delayDays",
				"9_precedeDays", "8_precedeDays", "validityDays" };
		String applications = null;
		try {
			for (String config : configArr) {
				String configData = scmm.getSystemConfigData(config);
				configMap.put(config, configData);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			configMap.put("serverDate", sdf.format(new Date()));
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			configMap.put("serverDateTime", sdf1.format(new Date()));
			String jsonStr = mapToJsonString(configMap);
			jsonMap.put("APPLICATIONS", jsonStr);
			data.add(jsonMap);
			applications = listToJsonString(data);

		} catch (Exception e) {
			logger.info("获取离线系统配置数据失败!");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取离线系统配置数据失败!");
			e.printStackTrace();
		}
		dataMap.put("SYS_APPLICATIONS", applications);
		return dataMap;
	}

	/**
	 * 组装查询条件 {FAMILY=0, BABY=0, PREGNANT=0, EHR=0, DIA=0, HYP=0,
	 * manaDoctorId=tianj, TREAT=0, SENIOR=0, SURVEY=0}
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	private Map<String, List<?>> createArrayCnd(Map<String, Object> body) {
		Map<String, List<?>> cnd = new HashMap<String, List<?>>();
		if (null != body && body.size() > 0) {
			String manaDoctorId = body.get("manaDoctorId").toString();
			String manaUnitId = body.get("manaUnitId").toString();
			String roleId = body.get("roleId").toString();
			String currentDate = BSCHISUtil.toString(new Date(), null);
			String serverDate = "date('" + currentDate + "')";
			List<?> taskDoctorIdCnd = CNDHelper.createSimpleCnd("eq",
					"taskDoctorId", "s", manaDoctorId);

			List<?> manaDoctorIdCnd_ = CNDHelper.createSimpleCnd("eq",
					"manaDoctorId", "s", manaDoctorId);
			List<?> beginDate = CNDHelper.createSimpleCnd("le", "beginDate",
					"$", serverDate);
			List<?> endDate = CNDHelper.createSimpleCnd("ge", "endDate", "$",
					serverDate);
			Iterator iterator = body.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				String value = (String) body.get(key);
				if ("DIA".equals(key) && "0".equals(value)) {
					List<?> typeCnd = CNDHelper.createSimpleCnd("eq",
							"businessType", "s", "2");
					List<?> statusCnd = CNDHelper.createSimpleCnd("eq",
							"planStatus", "s", '0');
					List<?> dateCnd = CNDHelper.createArrayCnd("and",
							beginDate, endDate);
					List<?> cnd1 = CNDHelper.createArrayCnd("and",
							taskDoctorIdCnd, dateCnd);
					List<?> cnd2 = CNDHelper.createArrayCnd("and", typeCnd,
							cnd1);
					List<?> cndDia = CNDHelper.createArrayCnd("and", statusCnd,
							cnd2);
					cnd.put("DIA", cndDia);
				} else if ("HYP".equals(key) && "0".equals(value)) {
					List<?> typeCnd = CNDHelper.createSimpleCnd("eq",
							"businessType", "s", "1");
					List<?> statusCnd = CNDHelper.createSimpleCnd("eq",
							"planStatus", "s", '0');
					List<?> dateCnd = CNDHelper.createArrayCnd("and",
							beginDate, endDate);
					List<?> cnd1 = CNDHelper.createArrayCnd("and",
							taskDoctorIdCnd, dateCnd);
					List<?> cnd2 = CNDHelper.createArrayCnd("and", typeCnd,
							cnd1);
					List<?> cndHyp = CNDHelper.createArrayCnd("and", statusCnd,
							cnd2);
					cnd.put("HYP", cndHyp);
				} else if ("SENIOR".equals(key) && "0".equals(value)) {
					List<?> typeCnd = CNDHelper.createSimpleCnd("eq",
							"businessType", "s", "4");
					List<?> statusCnd = CNDHelper.createSimpleCnd("eq",
							"planStatus", "s", '0');
					List<?> dateCnd = CNDHelper.createArrayCnd("and",
							beginDate, endDate);
					List<?> cnd1 = CNDHelper.createArrayCnd("and",
							taskDoctorIdCnd, dateCnd);
					List<?> cnd2 = CNDHelper.createArrayCnd("and", typeCnd,
							cnd1);
					List<?> cndSenior = CNDHelper.createArrayCnd("and",
							statusCnd, cnd2);
					cnd.put("SENIOR", cndSenior);
				} else if ("FAMILY".equals(key) && "0".equals(value)) {
					List<?> statusCnd = CNDHelper.createSimpleCnd("eq",
							"status", "s", "0");
					List<?> cndFamily = CNDHelper.createArrayCnd("and",
							manaDoctorIdCnd_, statusCnd);
					cnd.put("FAMILY", cndFamily);

				} else if ("PREGNANT".equals(key) && "0".equals(value)) {
					List<?> statusCnd = CNDHelper.createSimpleCnd("eq",
							"a.status", "s", "0");
					if ("chis.01".equals(roleId) || "chis.05".equals(roleId)) {
						if (manaUnitId != null && manaUnitId.length() > 9) {
							String centerManaUnitId = manaUnitId
									.substring(0, 9);
							List<?> centerManaUnitIdCnd = CNDHelper
									.createSimpleCnd("like", "a.manaUnitId",
											"s", centerManaUnitId + "%");
							List<?> cndPregant = CNDHelper.createArrayCnd(
									"and", centerManaUnitIdCnd, statusCnd);
							cnd.put("PREGNANT", cndPregant);
						}
					} else if ("chis.08".equals(roleId)) {
						List<?> centerManaUnitIdCnd = CNDHelper
								.createSimpleCnd("eq", "a.manaUnitId", "s",
										manaUnitId);
						List<?> cndPregant = CNDHelper.createArrayCnd("and",
								centerManaUnitIdCnd, statusCnd);
						cnd.put("PREGNANT", cndPregant);
					}

				}
			}
		}
		return cnd;
	}

	/**
	 * ,查询当前管辖机构内的所有模板
	 * 
	 * @param res
	 * @param dao
	 * @return
	 */
	private Map<String, Object> doOffLineMPMData(Map<String, Object> res,
			Map<String, Object> body, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		if (null != body && body.size() > 0) {
			String inputUnit = (String) body.get("inputUnit");
			// 网络管理员
			String manaIdNet = "";
			// System4
			String manaIdSys4 = "";
			// System6
			String manaIdSys6 = "";
			// 直辖市
			String manaIdCity = "";
			if (!"".equals(inputUnit)) {
				if (inputUnit.length() > 9) {
					manaIdNet = inputUnit.substring(0, 9);
				}
				if (inputUnit.length() > 4) {
					manaIdSys4 = inputUnit.substring(0, 4);
				}
				if (inputUnit.length() > 6) {
					manaIdSys6 = inputUnit.substring(0, 6);
				}
				if (inputUnit.length() > 2) {
					manaIdCity = inputUnit.substring(0, 2);
				}
			}
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "inputUnit", "s",
					manaIdNet);
			List<?> cnd3 = CNDHelper.createSimpleCnd("eq", "inputUnit", "s",
					manaIdSys4);
			List<?> cnd4 = CNDHelper.createSimpleCnd("eq", "inputUnit", "s",
					manaIdCity);
			List<?> cnd5 = CNDHelper.createSimpleCnd("eq", "inputUnit", "s",
					manaIdSys6);
			List<?> cnd6 = CNDHelper.createArrayCnd("or", cnd2, cnd3);
			List<?> cnd7 = CNDHelper.createArrayCnd("or", cnd4, cnd5);
			List<?> cnd = CNDHelper.createArrayCnd("or", cnd6, cnd7);
			List<Map<String, Object>> masterplateMaintainData = null;
			List<Map<String, Object>> fieldMasterRelationData = null;
			List<Map<String, Object>> dictionaryMaintainData = null;

			try {
				// ---------start-------------模版维护-------------------------------------------------
				masterplateMaintainData = dao.doList(cnd, null,
						MPM_MasterplateMaintain);
				if (masterplateMaintainData != null
						&& masterplateMaintainData.size() > 0) {
					List<Map<String, Object>> masterplateMaintainList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> masterplateMaintainMap : masterplateMaintainData) {
						masterplateMaintainList.add(masterplateMaintainMap);
					}
					String masterplateMaintainDataStr = listToJsonString(masterplateMaintainList);
					allData.put("MPM_MasterplateMaintain".toUpperCase(),
							masterplateMaintainDataStr);

				}
				// ---------end-------------模版维护-------------------------------------------------
				// ----------start-------------------数据结构与模版的联系表---------------------------------------------------
				List<Map<String, Object>> fieldMasterRelationList = new ArrayList<Map<String, Object>>();
				Map<String, Object> fieldIdMap = new HashMap<String, Object>();
				for (Map<String, Object> fieldMasterRelation : masterplateMaintainData) {

					String masterplateId = (String) fieldMasterRelation
							.get("masterplateId");

					List<?> fieldMasterRelationCnd = CNDHelper.createSimpleCnd(
							"eq", "masterplateId", "s", masterplateId);
					fieldMasterRelationData = dao.doList(
							fieldMasterRelationCnd, null,
							MPM_FieldMasterRelation);
					for (Map<String, Object> fieldMasterRelationMap : fieldMasterRelationData) {
						String fieldId = (String) fieldMasterRelationMap
								.get("fieldId");
						fieldIdMap.put(fieldId, fieldId);
						fieldMasterRelationList.add(fieldMasterRelationMap);
					}

				}
				String fieldMasterRelationStr = listToJsonString(fieldMasterRelationList);
				allData.put("MPM_FieldMasterRelation".toUpperCase(),
						fieldMasterRelationStr);
				// ----------end-------------------数据结构与模版的联系表---------------------------------------------------
				// ----------start------------------数值值域维护---------------------------------------------------
				List<Map<String, Object>> dictionaryMaintainList = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = fieldIdMap.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = (String) fieldIdMap.get(key);
					List<?> fieldIdCnd = CNDHelper.createSimpleCnd("eq",
							"fieldId", "s", value);
					dictionaryMaintainData = dao.doList(fieldIdCnd, null,
							MPM_DictionaryMaintain);
					for (Map<String, Object> dictionaryMaintainMap : dictionaryMaintainData) {
						dictionaryMaintainList.add(dictionaryMaintainMap);
					}

				}

				String dictionaryMaintainStr = listToJsonString(dictionaryMaintainList);
				allData.put("MPM_DictionaryMaintain".toUpperCase(),
						dictionaryMaintainStr);
				// ----------end------------------数值值域维护---------------------------------------------------
				// ----------start------------------数据结构维护---------------------------------------------------
				List<Map<String, Object>> fieldMaintainList = new ArrayList<Map<String, Object>>();
				Iterator<?> fieldMaintainIterator = fieldIdMap.keySet()
						.iterator();
				while (fieldMaintainIterator.hasNext()) {
					Map<String, Object> map = new HashMap<String, Object>();
					String key = fieldMaintainIterator.next().toString();
					String value = (String) fieldIdMap.get(key);
					List<?> fieldIdCnd = CNDHelper.createSimpleCnd("eq",
							"fieldId", "s", value);
					List<Map<String, Object>> doList = new ArrayList<Map<String, Object>>();

					doList = dao.doList(fieldIdCnd, null, MPM_FieldMaintain);
					if (null != doList && doList.size() > 0) {
						map = doList.get(0);
						map.put("isNotNull", map.get("notNull"));
						map.remove("notNull");
						fieldMaintainList.add(map);
					}

				}
				String fieldMaintainStr = listToJsonString(fieldMaintainList);
				allData.put("MPM_FieldMaintain".toUpperCase(), fieldMaintainStr);
				// ----------end------------------数据结构维护---------------------------------------------------
			} catch (Exception e) {
				logger.info("failed to get  MPMData !");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取移动离线模板数据失败。");
				e.printStackTrace();
			}

		}
		return allData;
	}

	/**
	 * 家医服务数据
	 * 
	 * @param res
	 * @param body
	 * @param dao
	 * @return
	 */
	private Map<String, Object> doOffLineFamilyDoctorData(List<?> cnd,
			Map<String, Object> res, BaseDAO dao) {
		Map<String, Object> allData = new HashMap<String, Object>();
		// 家庭档案
		List<Map<String, Object>> familyRecordDataList = new ArrayList<Map<String, Object>>();
		// 家医签约
		List<Map<String, Object>> familyContractBaseDataList = new ArrayList<Map<String, Object>>();
		// 家医服务项目
		List<Map<String, Object>> familyContractServiceDataList = new ArrayList<Map<String, Object>>();
		if (null == cnd || cnd.isEmpty()) {
			return allData;
		}
		try {
			// 家庭档案
			List<Map<String, Object>> familyRecordList = dao.doList(cnd, null,
					EHR_FamilyRecord);
			for (Map<String, Object> familyRecordMap : familyRecordList) {
				String familyId = (String) familyRecordMap.get("familyId");
				List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.F_Id", "s",
						familyId);
				List<?> cnd2 = CNDHelper.createSimpleCnd("eq",
						"a.FC_Sign_Flag", "s", "1");
				List<?> cnd3 = CNDHelper.createArrayCnd("and", cnd1, cnd2);
				// 家庭签约
				List<Map<String, Object>> familyContractBaseList = dao.doList(
						cnd3, null, EHR_FamilyContractBase);
				if (familyContractBaseList != null
						&& !familyContractBaseList.isEmpty()) {
					Map<String, Object> familyContractBaseMap = familyContractBaseList
							.get(0);
					String FC_Id = (String) familyContractBaseMap.get("FC_Id");
					List<?> cnd4 = CNDHelper.createSimpleCnd("eq", "a.FC_Id",
							"s", FC_Id);
					// 服务项目
					List<Map<String, Object>> familyContractServiceList = dao
							.doList(cnd4, null, EHR_FamilyContractService);
					if (familyContractServiceList != null
							&& !familyContractServiceList.isEmpty()) {
						familyRecordDataList.add(familyRecordMap);
						familyContractBaseDataList
								.addAll(familyContractBaseList);
						familyContractServiceDataList
								.addAll(familyContractServiceList);
						for (Map<String, Object> familyContractServiceMap : familyContractServiceList) {
							String empiId = (String) familyContractServiceMap
									.get("FS_EmpiId");
							allEmpiId.put(empiId, empiId);
						}
						List<?> cnd5 = CNDHelper.createSimpleCnd("eq",
								"a.familyId", "s", familyId);
						List<?> cnd6 = CNDHelper.createSimpleCnd("eq",
								"a.masterFlag", "s", "y");
						List<?> cnd7 = CNDHelper.createArrayCnd("and", cnd5,
								cnd6);
						Map<String, Object> masterEhrRecordMap = dao.doLoad(
								cnd7, EHR_HealthRecord);
						if (masterEhrRecordMap != null
								&& !masterEhrRecordMap.isEmpty()) {
							String empiId = (String) masterEhrRecordMap
									.get("empiId");
							allEmpiId.put(empiId, empiId);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("failed to get familyDoctorData !");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取离线家医服务数据失败。");
			e.printStackTrace();
		}
		allData.put("EHR_FamilyRecord", familyRecordDataList);
		allData.put("EHR_FamilyContractBase", familyContractBaseDataList);
		allData.put("EHR_FamilyContractService", familyContractServiceDataList);
		return allData;
	}

	/**
	 * 超过服务有效性时间
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveByValidityDays(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		body.put("dataStatus", "2");
		try {
			dao.doSave("create", DLL_UpLoadLog, body, true);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to save offline data by validityDays!");
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "保存离线数据失败(超过服务有效性时间)。");
			e.printStackTrace();
		}
	}

	/**
	 * 档案注销
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveByStatus(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		body.put("dataStatus", "3");
		try {
			dao.doSave("create", DLL_UpLoadLog, body, true);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to save offline data by status!");
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "保存离线数据失败(档案已经注销)。");
			e.printStackTrace();
		}
	}

	/**
	 * 数据重复
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveByRepeat(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		body.put("dataStatus", "4");
		try {
			dao.doSave("create", DLL_UpLoadLog, body, true);
		} catch (PersistentDataOperationException e) {
			logger.info("failed to save offline data by repeat !");
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "保存离线数据失败(数据重复)。");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetFamilyRecordStatus(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) jsonReq.get("body");
		String phrId = (String) body.get("phrId");
		List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.phrId", "s", phrId);
		try {
			Map<String, Object> healthRecordMap = dao.doLoad(cnd,
					BSCHISEntryNames.EHR_HealthRecord);
			if (healthRecordMap != null && !healthRecordMap.isEmpty()) {
				String familyId = (String) healthRecordMap.get("familyId");
				List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.familyId",
						"s", familyId);
				List<Map<String, Object>> familyRecord = dao.doList(cnd1, null,
						BSCHISEntryNames.EHR_FamilyRecord);
				jsonRes.put("body", familyRecord);
			}
		} catch (PersistentDataOperationException e) {
			logger.info("failed to get family record status !");
			jsonRes.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			jsonRes.put(RES_MESSAGE, "获取家庭档案状态失败!");
			e.printStackTrace();
		}

	}

	/**
	 * 解决 not exist in thread context
	 * 
	 * @param jsonReq
	 * @throws ControllerException
	 */
	private void putUserRoleToken(Map<String, Object> jsonReq)
			throws ControllerException {
		String uid = (String) jsonReq.get("uid");
		String rid = (String) jsonReq.get("rid");
		String urole = (String) jsonReq.get("urole");
		int index = urole.indexOf("@");
		String manaUnitId = urole.substring(0, index);
		User user = AccountCenter.getUser(uid);
		Collection<UserRoleToken> userRoleTokens = user.getUserRoleTokens();
		for (UserRoleToken userRoleToken : userRoleTokens) {
			String roleId = userRoleToken.getRoleId();
			String manageUnitId = userRoleToken.getManageUnitId();
			if (rid.equals(roleId) && manaUnitId.equals(manageUnitId)) {
				ContextUtils.put(Context.USER_ROLE_TOKEN, userRoleToken);
				ContextUtils.put(Context.USER, userRoleToken);
			}
		}
	}

	/**
	 * 将map转换成json字符串
	 * 
	 * @param map
	 * @return
	 */
	private String mapToJsonString(Map<String, Object> map) {
		StringWriter out = new StringWriter();
		try {
			jsonMapper.writeValue(out, map);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("mapToJsonString[" + out.toString() + "].");
		return out.toString();
	}

	/**
	 * 将List转换成json字符串
	 * 
	 * @param list
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	private String listToJsonString(List<?> list)
			throws JsonGenerationException, JsonMappingException, IOException {
		String jsonStr = "";

		jsonStr = jsonMapper.writeValueAsString(list);
		logger.info("listToJsonString[" + jsonStr + "].");
		return jsonStr;
	}

	/**
	 * 一年的第一天
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getFirstDayOfYear() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -calendar.get(Calendar.MONTH));

		calendar.add(Calendar.DAY_OF_MONTH,
				-calendar.get(Calendar.DAY_OF_MONTH) + 1);
		return formatter.format(calendar.getTime());
	}

	/**
	 * 一年最后一天
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getLastDayOfYear() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 11 - calendar.get(Calendar.MONTH));

		calendar.add(Calendar.DAY_OF_MONTH,
				-calendar.get(Calendar.DAY_OF_MONTH) + 1);
		calendar.add(Calendar.DAY_OF_MONTH,
				31 - calendar.get(Calendar.DAY_OF_MONTH));
		return formatter.format(calendar.getTime());
	}

	public static void main(String[] args) {
		Date currentDate = new Date();
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String date = formate.format(currentDate);
		String downLodaDate = "to_date(" + "'" + date + "'," + "'yyyy-mm-dd')";
		System.out.println(downLodaDate);
	}
}
