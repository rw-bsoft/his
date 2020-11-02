/*
 * @(#)GetBusinessDataService.java Created on 2013-04-15 下午15:17:46
 *
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mobile;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.conf.SystemCofigManageModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:suny@bsoft.com.cn">suny</a> 离线数据管理
 */
public class GetBusinessDataService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(GetBusinessDataService.class);
	private static ObjectMapper jsonMapper = new ObjectMapper();
	// 根据phrId查询单条数据
	private static Map<String, String> tablesPhrIdSingle = new HashMap<String, String>();
	static {
		tablesPhrIdSingle.put("1", EHR_HealthRecord);
		tablesPhrIdSingle.put("2", MDC_HypertensionRecord);
		tablesPhrIdSingle.put("3", MDC_DiabetesRecord);
		tablesPhrIdSingle.put("4", MDC_OldPeopleRecord);
	}

	// MDC_DiabetesFixGroup
	// 根据phrId查询多条数据
	private static Map<String, String> tablesPhrIdMultiple = new HashMap<String, String>();
	static {
		tablesPhrIdMultiple.put("1", MDC_OldPeopleVisit);
		tablesPhrIdMultiple.put("2", MDC_DiabetesVisit);
		tablesPhrIdMultiple.put("3", MDC_HypertensionVisit);
		tablesPhrIdMultiple.put("4", MDC_DiabetesMedicine);
		tablesPhrIdMultiple.put("5", MDC_HypertensionFixGroup);
		tablesPhrIdMultiple.put("6", MDC_HypertensionMedicine);
	}

	// 根据empiId查询单条数据
	private static Map<String, String> tablesEmpiIdSingle = new HashMap<String, String>();
	static {
		tablesEmpiIdSingle.put("1", EHR_LifeStyle);
		tablesEmpiIdSingle.put("2", MPI_DemographicInfo);
	}

	// 根据empiId查询多条数据
	private static Map<String, String> tablesEmpiIdMultiple = new HashMap<String, String>();
	static {
		tablesEmpiIdMultiple.put("1", EHR_PastHistory);
	}

	private static Map<String, String> tablesPregnantId = new HashMap<String, String>();
	static {
		tablesPregnantId.put("1", MHC_PostnatalVisitInfo);
		tablesPregnantId.put("2", MHC_Postnatal42dayRecord);
		tablesPregnantId.put("3", MHC_BabyVisitInfo);
		tablesPregnantId.put("4", MHC_BabyVisitRecord);
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
	protected void doBusinessData(Map<String, Object> req,
			final Map<String, Object> res, final BaseDAO dao, final Context ctx)
			throws ServiceException, JsonGenerationException,
			JsonMappingException, IOException {
		final Map<String, Object> body = (Map<String, Object>) req.get("body");
		final String manaDoctorId = body.get("manaDoctorId").toString();
		final Map<String, List<?>> cndMap = createArrayCnd(body);

		// new Thread(new Runnable() {
		// public void run() {
		try {
			List<?> cndDia = null;
			if (cndMap.containsKey("DIA")) {
				cndDia = cndMap.get("DIA");
			}
			List<?> cndHyp = null;
			if (cndMap.containsKey("HYP")) {
				cndHyp = cndMap.get("HYP");
			}
			List<?> cndSenior = null;
			if (cndMap.containsKey("SENIOR")) {
				cndSenior = cndMap.get("SENIOR");
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
			List<Map<String, Object>> pregantRecord = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> healthRecordFamily = new ArrayList<Map<String, Object>>();
			// 糖尿病随访
			if (null != cndDia && cndDia.size() > 0) {
				System.out.println("doBusinessData="
						+ ((Session) ctx.get(Context.DB_SESSION)).isOpen());
				List<Map<String, Object>> visitPlanDia = dao.doList(cndDia,
						null, PUB_VisitPlan);
				visitPlan.addAll(visitPlanDia);
			}
			// 高血压随访
			if (null != cndHyp && cndHyp.size() > 0) {
				List<Map<String, Object>> visitPlanHyp = dao.doList(cndHyp,
						null, PUB_VisitPlan);
				visitPlan.addAll(visitPlanHyp);
			}
			// 老年人随访
			if (null != cndSenior && cndSenior.size() > 0) {
				List<Map<String, Object>> visitPlanSenior = dao.doList(
						cndSenior, null, PUB_VisitPlan);
				visitPlan.addAll(visitPlanSenior);
			}
			// 孕妇档案
			if (null != cndPregant && cndPregant.size() > 0) {
				pregantRecord = dao
						.doList(cndPregant, null, MHC_PregnantRecord);
			}
			// 根据家庭档案查询家庭成员的健康档案
			healthRecordFamily = getHealthRecord(res, cndFamily, dao);
			Map<String, Object> uniquePhrIdMap = new HashMap<String, Object>();
			Map<String, Object> uniqueEmpiIdMap = new HashMap<String, Object>();
			Map<String, Object> uniquePregnantIdMap = new HashMap<String, Object>();
			// 随访计划表
			for (Map<String, Object> map : visitPlan) {
				uniquePhrIdMap.put((String) map.get("recordId"),
						map.get("recordId"));
				uniqueEmpiIdMap.put((String) map.get("empiId"),
						map.get("empiId"));
			}
			// 根据家庭档案查询成员健康档案
			if (null != healthRecordFamily && healthRecordFamily.size() > 0) {
				for (Map<String, Object> map : healthRecordFamily) {
					uniquePhrIdMap.put((String) map.get("phrId"),
							map.get("phrId"));
					uniqueEmpiIdMap.put((String) map.get("empiId"),
							map.get("empiId"));
				}
			}
			// 孕产妇档案
			if (null != pregantRecord && pregantRecord.size() > 0) {
				for (Map<String, Object> map : pregantRecord) {
					uniquePhrIdMap.put((String) map.get("phrId"),
							map.get("phrId"));
					uniqueEmpiIdMap.put((String) map.get("empiId"),
							map.get("empiId"));
					uniquePregnantIdMap.put((String) map.get("pregnantId"),
							map.get("pregnantId"));
				}
			}
			dataMap.putAll(getDataByPregnantId(res, uniquePregnantIdMap, dao));
			dataMap.putAll(getSingleDataByPhrId(res, uniquePhrIdMap, dao));
			dataMap.putAll(getMultipleDataByPhrId(res, uniquePhrIdMap, dao));
			dataMap.putAll(getSingleDataByEmpiId(res, uniqueEmpiIdMap, dao));
			dataMap.putAll(getMultipleDataByEmpiId(res, uniqueEmpiIdMap, dao));
			dataMap.putAll(getMPMData(res, body, dao));
			dataMap.putAll(getFamilyDoctorData(res, cndFamily, dao));
			dataMap.putAll(getSystemConfigData(res, dao));
			String visitPlanStr = listToJsonString(visitPlan);
			String pregantRecordStr = listToJsonString(pregantRecord);
			dataMap.put("PUB_VisitPlan".toUpperCase(), visitPlanStr);
			dataMap.put("MHC_PregnantRecord".toUpperCase(), pregantRecordStr);
			String data = mapToJsonString(dataMap);
			ZipInputStream zipIn = ZipUtil.streamInput(data, manaDoctorId);
			body.put(RES_CODE, Constants.CODE_OK);
			body.put(RES_MESSAGE, "获取离线数据成功。");
			res.put("body", body);
		} catch (Exception e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取离线数据失败。");
			e.printStackTrace();
		}
		// finally{
		// }
		// }
		// }).start();

	}

	/**
	 * 获取系统配置信息
	 * 
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getSystemConfigData(Map<String, Object> res,
			BaseDAO dao) {
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
				"9_precedeDays", "8_precedeDays" };
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
	 * 根据phrId查询单条数据
	 * 
	 * @param res
	 * @param visitPlan
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getDataByPregnantId(Map<String, Object> res,
			Map<String, Object> uniquePregnantIdMap, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (null != uniquePregnantIdMap && uniquePregnantIdMap.size() > 0) {
			for (String key : tablesPregnantId.keySet()) {
				String table = tablesPregnantId.get(key);
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = uniquePregnantIdMap.keySet().iterator();
				while (iterator.hasNext()) {
					String pregnantId = (String) iterator.next();
					List<?> cnd = CNDHelper.createSimpleCnd("eq",
							"a.pregnantId", "s", pregnantId);
					List<Map<String, Object>> businessData = null;
					try {
						businessData = dao.doList(cnd, null, table);
						if (businessData != null && businessData.size() > 0) {
							for (Map<String, Object> map : businessData) {
								data.add(map);
							}
						}

					} catch (Exception e) {
						logger.info("获取离线妇幼保健数据失败(数据库操作)!");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取离线妇幼保健数据失败!");
						e.printStackTrace();
					}
				}
				try {
					String str = listToJsonString(data);
					int index = table.lastIndexOf(".");
					table = table.substring(index + 1, table.length());
					dataMap.put(table.toUpperCase(), str);
				} catch (Exception e) {
					logger.info("获取离线妇幼保健数据失败(List 2 Json)!");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取离线妇幼保健数据失败。");
					e.printStackTrace();
				}

			}
		}
		return dataMap;
	}

	/**
	 * 根据phrId查询单条数据
	 * 
	 * @param res
	 * @param visitPlan
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getSingleDataByPhrId(Map<String, Object> res,
			Map<String, Object> uniquePhrIdMap, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (null != uniquePhrIdMap && uniquePhrIdMap.size() > 0) {
			for (String key : tablesPhrIdSingle.keySet()) {
				String table = tablesPhrIdSingle.get(key);
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = uniquePhrIdMap.keySet().iterator();
				while (iterator.hasNext()) {
					String phrId = (String) iterator.next();
					List<?> cnd = CNDHelper.createSimpleCnd("eq", "a.phrId",
							"s", phrId);
					try {
						List<Map<String, Object>> dataList = dao.doList(cnd,
								null, table);
						if (dataList != null && dataList.size() > 0) {
							Map<String, Object> businessData = dataList.get(0);
							data.add(businessData);

						}

					} catch (Exception e) {
						logger.info("通过phrId获取" + table + "数据失败!");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取离线SingleDataByPhrId数据失败。");
						e.printStackTrace();
					}
				}
				try {
					String str = listToJsonString(data);
					int index = table.lastIndexOf(".");
					table = table.substring(index + 1, table.length());
					dataMap.put(table.toUpperCase(), str);
				} catch (Exception e) {
					logger.info("the method getSingleDataByPhrId failed");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取离线SingleDataByPhrId数据失败。");
					e.printStackTrace();
				}

			}
		}
		return dataMap;
	}

	/**
	 * 根据phrId查询多条数据
	 * 
	 * @param res
	 * @param visitPlan
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getMultipleDataByPhrId(Map<String, Object> res,
			Map<String, Object> uniquePhrIdMap, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (null != uniquePhrIdMap && uniquePhrIdMap.size() > 0) {
			for (String key : tablesPhrIdMultiple.keySet()) {
				String table = tablesPhrIdMultiple.get(key);
				String firstDate = "date('" + getFirstDayOfYear() + "')";
				String lastDate = "date('" + getLastDayOfYear() + "')";
				List<?> firstCnd = null;
				List<?> lastCnd = null;
				if (MDC_DiabetesMedicine.equals(table)
						|| MDC_HypertensionMedicine.equals(table)) {
					firstCnd = CNDHelper.createSimpleCnd("ge", "createDate",
							"$", firstDate);

					lastCnd = CNDHelper.createSimpleCnd("le", "createDate",
							"$", lastDate);
				} else if (MDC_HypertensionFixGroup.equals(table)) {
					firstCnd = CNDHelper.createSimpleCnd("ge", "fixDate", "$",
							firstDate);

					lastCnd = CNDHelper.createSimpleCnd("le", "fixDate", "$",
							lastDate);
				} else {
					firstCnd = CNDHelper.createSimpleCnd("ge", "a.visitDate",
							"$", firstDate);

					lastCnd = CNDHelper.createSimpleCnd("le", "a.visitDate",
							"$", lastDate);
				}

				List<?> cnd1 = CNDHelper.createArrayCnd("and", firstCnd,
						lastCnd);
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = uniquePhrIdMap.keySet().iterator();
				while (iterator.hasNext()) {

					String phrId = (String) iterator.next();
					List<?> cnd2 = null;
					if (MDC_OldPeopleVisit.equals(table)
							|| MDC_DiabetesVisit.equals(table)
							|| MDC_HypertensionVisit.equals(table)) {
						cnd2 = CNDHelper.createSimpleCnd("eq", "a.phrId", "s",
								phrId);
					} else {
						cnd2 = CNDHelper.createSimpleCnd("eq", "phrId", "s",
								phrId);
					}
					List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
					List<Map<String, Object>> businessData = null;
					try {

						if (MDC_DiabetesMedicine.equals(table)) {
							businessData = dao.doQuery(cnd, "recordId", table);
							SchemaUtil.setDictionaryMessageForList(
									businessData, MDC_DiabetesMedicine);
						} else {
							businessData = dao.doList(cnd, null, table);
						}
						if (businessData != null && businessData.size() > 0) {
							for (Map<String, Object> mapPhr : businessData) {
								data.add(mapPhr);
							}
						}

					} catch (Exception e) {
						logger.info("the method getMultipleDataByPhrId failed");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取离线MultipleDataByPhrId数据失败。");
						e.printStackTrace();
					}
				}
				try {
					String str = listToJsonString(data);
					int index = table.lastIndexOf(".");
					table = table.substring(index + 1, table.length());
					dataMap.put(table.toUpperCase(), str);
				} catch (Exception e) {
					logger.info("the method getMultipleDataByPhrId failed");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取离线MultipleDataByPhrId数据失败。");
					e.printStackTrace();
				}

			}
		}
		return dataMap;
	}

	/**
	 * 根据empiId查询单条数据
	 * 
	 * @param res
	 * @param visitPlan
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getSingleDataByEmpiId(Map<String, Object> res,
			Map<String, Object> uniqueEmpiIdMap, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (null != uniqueEmpiIdMap && uniqueEmpiIdMap.size() > 0) {
			for (String key : tablesEmpiIdSingle.keySet()) {
				String table = tablesEmpiIdSingle.get(key);
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = uniqueEmpiIdMap.keySet().iterator();
				while (iterator.hasNext()) {
					String empiId = (String) iterator.next();
					List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId",
							"s", empiId);
					List<Map<String, Object>> doList = new ArrayList<Map<String, Object>>();
					try {
						doList = dao.doList(cnd, null, table);

						if (doList != null && doList.size() > 0) {
							data.add(doList.get(0));
						}

					} catch (Exception e) {
						logger.info("the method getSingleDataByEmpiId failed");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取离线SingleDataByEmpiId数据失败。");
						e.printStackTrace();
					}
				}
				try {
					String str = listToJsonString(data);
					int index = table.lastIndexOf(".");
					table = table.substring(index + 1, table.length());
					dataMap.put(table.toUpperCase(), str);
				} catch (Exception e) {
					logger.info("the method getSingleDataByEmpiId failed");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取离线SingleDataByEmpiId数据失败。");
					e.printStackTrace();
				}

			}
		}
		return dataMap;
	}

	/**
	 * 根据empiId查询多条数据
	 * 
	 * @param res
	 * @param visitPlan
	 * @param dao
	 * @return
	 */
	private Map<String, Object> getMultipleDataByEmpiId(
			Map<String, Object> res, Map<String, Object> uniqueEmpiIdMap,
			BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		if (null != uniqueEmpiIdMap && uniqueEmpiIdMap.size() > 0) {
			for (String key : tablesEmpiIdMultiple.keySet()) {
				String table = tablesEmpiIdMultiple.get(key);
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				Iterator<?> iterator = uniqueEmpiIdMap.keySet().iterator();
				while (iterator.hasNext()) {
					String empiId = (String) iterator.next();
					List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId",
							"s", empiId);
					List<Map<String, Object>> businessData = null;
					try {
						businessData = dao.doList(cnd, null, table);
						if (businessData != null && businessData.size() > 0) {
							for (Map<String, Object> mapPhr : businessData) {
								data.add(mapPhr);
							}
						}

					} catch (Exception e) {
						logger.info("the method getMultipleDataByEmpiId failed");
						res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
						res.put(RES_MESSAGE, "获取离线MultipleDataByEmpiId数据失败。");
						e.printStackTrace();
					}
				}
				try {
					String str = listToJsonString(data);
					int index = table.lastIndexOf(".");
					table = table.substring(index + 1, table.length());
					dataMap.put(table.toUpperCase(), str);
				} catch (Exception e) {
					logger.info("the method getMultipleDataByEmpiId failed");
					res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
					res.put(RES_MESSAGE, "获取离线MultipleDataByEmpiId数据失败。");
					e.printStackTrace();
				}

			}
		}
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
					List<?> manaDoctorIdCnd = CNDHelper.createSimpleCnd("eq",
							"a.manaDoctorId", "s", manaDoctorId);
					List<?> cndPregant = CNDHelper.createArrayCnd("and",
							manaDoctorIdCnd, statusCnd);
					cnd.put("PREGNANT", cndPregant);
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
	private Map<String, Object> getMPMData(Map<String, Object> res,
			Map<String, Object> body, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
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
				manaIdNet = inputUnit.substring(0, 9);
				manaIdSys4 = inputUnit.substring(0, 4);
				manaIdSys6 = inputUnit.substring(0, 6);
				manaIdCity = inputUnit.substring(0, 2);
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
				masterplateMaintainData = dao.doList(cnd, null,
						MPM_MasterplateMaintain);
				if (masterplateMaintainData != null
						&& masterplateMaintainData.size() > 0) {
					List<Map<String, Object>> masterplateMaintainList = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> masterplateMaintainMap : masterplateMaintainData) {
						masterplateMaintainList.add(masterplateMaintainMap);
					}
					String masterplateMaintainDataStr = listToJsonString(masterplateMaintainList);
					dataMap.put("MPM_MasterplateMaintain".toUpperCase(),
							masterplateMaintainDataStr);

				}
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
				dataMap.put("MPM_FieldMasterRelation".toUpperCase(),
						fieldMasterRelationStr);
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
				dataMap.put("MPM_DictionaryMaintain".toUpperCase(),
						dictionaryMaintainStr);
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
				dataMap.put("MPM_FieldMaintain".toUpperCase(), fieldMaintainStr);
			} catch (Exception e) {
				logger.info("the method getMPMData failed");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取离线模板数据失败。");
				e.printStackTrace();
			}

		}
		return dataMap;
	}

	/**
	 * 健康档案
	 * 
	 * @param res
	 * @param body
	 * @param dao
	 * @return
	 */
	private List<Map<String, Object>> getHealthRecord(Map<String, Object> res,
			List<?> cnd, BaseDAO dao) {
		List<Map<String, Object>> healthRecordList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> familyRecordList = new ArrayList<Map<String, Object>>();
		if (null != cnd && cnd.size() > 0) {
			try {
				familyRecordList = dao.doList(cnd, null, EHR_FamilyRecord);
				for (Map<String, Object> familyRecordMap : familyRecordList) {
					if (null != familyRecordMap && familyRecordMap.size() > 0) {
						String familyId = (String) familyRecordMap
								.get("familyId");
						List<?> familyIdCnd = CNDHelper.createSimpleCnd("eq",
								"familyId", "s", familyId);
						List<Map<String, Object>> healthRecordByFamilyId = dao
								.doList(familyIdCnd, null, EHR_HealthRecord);
						if (null != healthRecordByFamilyId
								&& healthRecordByFamilyId.size() > 0) {
							for (Map<String, Object> map : healthRecordByFamilyId) {
								healthRecordList.add(map);
							}
						}
					}
				}
			} catch (PersistentDataOperationException e) {
				logger.info("the method getHealthRecord failed");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取离线健康档案数据失败。");
				e.printStackTrace();
			}
		}
		return healthRecordList;
	}

	/**
	 * 家医服务数据
	 * 
	 * @param res
	 * @param body
	 * @param dao
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private Map<String, Object> getFamilyDoctorData(Map<String, Object> res,
			List<?> cnd, BaseDAO dao) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Map<String, Object>> familyRecordList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> familyContractBaseList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> familyContractServiceList = new ArrayList<Map<String, Object>>();
		if (null != cnd && cnd.size() > 0) {
			try {
				// 家庭档案
				familyRecordList = dao.doList(cnd, null, EHR_FamilyRecord);

				for (Map<String, Object> familyRecordMap : familyRecordList) {
					if (null != familyRecordMap && familyRecordMap.size() > 0) {
						String familyId = (String) familyRecordMap
								.get("familyId");
						List<?> familyIdCnd = CNDHelper.createSimpleCnd("eq",
								"familyId", "s", familyId);
						List<?> F_IdCnd = CNDHelper.createSimpleCnd("eq",
								"F_Id", "s", familyId);
						// 家庭签约
						List<Map<String, Object>> familyContractBase = dao
								.doList(F_IdCnd, null, EHR_FamilyContractBase);
						Map<String, Object> FCIdMap = new HashMap<String, Object>();
						if (null != familyContractBase
								&& familyContractBase.size() > 0) {
							for (Map<String, Object> map : familyContractBase) {
								FCIdMap.put(map.get("FC_Id").toString(),
										map.get("FC_Id"));
								familyContractBaseList.add(map);
							}
						}
						// 服务项目
						Iterator<?> iterator = FCIdMap.keySet().iterator();
						while (iterator.hasNext()) {
							String FC_Id = iterator.next().toString();
							List<?> FC_IdCnd = CNDHelper.createSimpleCnd("eq",
									"FC_Id", "s", FC_Id);
							List<Map<String, Object>> FamilyContractService = dao
									.doList(FC_IdCnd, null,
											EHR_FamilyContractService);
							for (Map<String, Object> map : FamilyContractService) {
								familyContractServiceList.add(map);
							}
						}
					}
				}

			} catch (Exception e) {
				logger.info("the method getFamilyDoctorData failed");
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "获取离线家医服务数据失败。");
				e.printStackTrace();
			}

		}
		try {
			String familyRecordStr = listToJsonString(familyRecordList);
			String familyContractBaseStr = listToJsonString(familyContractBaseList);
			String familyContractServiceStr = listToJsonString(familyContractServiceList);
			dataMap.put("EHR_FamilyContractBase".toUpperCase(),
					familyContractBaseStr);
			dataMap.put("EHR_FamilyContractService".toUpperCase(),
					familyContractServiceStr);
			dataMap.put("EHR_FamilyRecord".toUpperCase(), familyRecordStr);
		} catch (Exception e) {
			logger.info("the method getFamilyDoctorData failed");
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取离线家医服务数据失败。");
			e.printStackTrace();
		}
		return dataMap;
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
}
