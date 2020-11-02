/**
 * @(#)PregnantRecordService.java Created on 2012-4-9 下午5:10:46
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.mhc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import chis.source.admin.AreaGridModel;
import chis.source.admin.SystemUserModel;
import chis.source.cdh.ChildrenHealthModel;
import chis.source.dic.BusinessType;
import chis.source.dic.Gender;
import chis.source.dic.MHCPastHistory;
import chis.source.dic.PastHistoryCode;
import chis.source.dic.PersonHistory;
import chis.source.dic.PlanMode;
import chis.source.dic.RolesList;
import chis.source.empi.EmpiModel;
import chis.source.phr.HealthRecordModel;
import chis.source.phr.LifeStyleModel;
import chis.source.phr.PastHistoryModel;
import chis.source.service.ServiceCode;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;

import com.alibaba.fastjson.JSONException;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */
public class PregnantRecordService extends MHCService {

	private static final Logger logger = LoggerFactory
			.getLogger(PregnantRecordService.class);

	// ** 孕妇首次随访编号
	private final String FIRST_VISIT_ID = "0000000000000000";

	/**
	 * 判断是否可以新建孕妇档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doWhetherNeedPregnantRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) reqBody.get("empiId");
		EmpiModel em = new EmpiModel(dao);
		Map<String, Object> empiMap;
		try {
			empiMap = em.getEmpiInfoByEmpiid(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get empi data.", e);
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}
		// ** 非女性不需建档。
		if (!empiMap.get("sexCode").equals(Gender.WOMEN)) {
			resBody.put("needCreate", 2);
			resBody.put("message", "性别不符。");
			return;
		}
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			List<Map<String, Object>> records = prm
					.getPregnantRecordByEmpiId(empiId);
			if (records != null && records.size() > 0) {
				resBody.put("result", SchemaUtil.setDictionaryMessageForList(
						records, MHC_PregnantRecord));
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get pregnant record.", e);
			throw new ServiceException(e);
		}
		try {
			// ** 如果未建过孕妇档案，返回1（可以建档）。
			Map<String, Object> result = prm.getLastPregnantRecord(empiId);
			if (result == null || result.size() < 1) {
				resBody.put("needCreate", 1);
				resBody.put("message", "可以建档。");
				return;
			} else {
				String status = (String) result.get("status");
				if (status.equals(Constants.CODE_STATUS_WRITE_OFF)) {
					resBody.put("needCreate", 0);
					resBody.put("message", "档案已注销无法新建，请走恢复流程。");
					return;
				} else if (status.equals(Constants.CODE_STATUS_END_MANAGE)) {
					resBody.put("needCreate", 1);
					resBody.put("message", "可以建档。");
					return;
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get normal pregnant record.", e);
			throw new ServiceException(e);
		}

		Map<String, Object> map;
		try {
			map = prm.getLastMenstrualPeriod(empiId); // ** 获取末次月经
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get last menstrual period.", e);
			throw new ServiceException(e);
		}
		if (map == null) {
			resBody.put("needCreate", 1);
			resBody.put("message", "可以建档。");
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) map.get("lastMenstrualPeriod"));
			int period = BSCHISUtil.getPeriod(c.getTime(), null);
			if (period > 310) { // ** 末次月经和今天间隔超过310天，返回1（可以建档）
				resBody.put("needCreate", 1);
				resBody.put("message", "可以建档。");
			} else {
				resBody.put("needCreate", 0);
				resBody.put("message", "有处于管理状态的档案，需要先将该档案结案掉才能新建档案！");
				resBody.put("pregnantId", map.get("pregnantId"));
			}
		}
	}

	/**
	 * 初始化建档数据。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doDocCreateInitialization(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) reqBody.get("empiId");
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		// ** 获取妇女基本信息
		Map<String, Object> wrMap;
		try {
			wrMap = prm.getMaternalHelathCard(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get woman record data.", e);
			throw new ServiceException(e);
		}
		if (wrMap != null && wrMap.size() > 0) {
			resMap.putAll(wrMap);
		}
		PastHistoryModel phm = new PastHistoryModel(dao);
		LifeStyleModel lsm = new LifeStyleModel(dao);
		// ** 获取孕妇几个既往史
		Map<String, Object> past;
		try {
			past = phm.getPersonPastHistory(empiId);
			String personHistory = null;
			if (past != null && past.size() > 0) {
				past.put("gynecologyOPS", past.get("operationHistory"));
				personHistory = (String) past.get("personHistory");
			} else {
				past = new HashMap<String, Object>();
			}
			Map<String, Object> lifeStyle = lsm.getLifeStyleByEmpiId(empiId);
			if (lifeStyle != null && lifeStyle.size() > 0) {
				Map<String, Object> smokeFreqCode = (Map<String, Object>) lifeStyle
						.get("smokeFreqCode");
				if (smokeFreqCode != null && smokeFreqCode.size() > 0) {
					String smokeCode = (String) smokeFreqCode.get("key");
					if (smokeCode.equals("1") || smokeCode.equals("2")) {
						personHistory = (personHistory == null || ""
								.equals(personHistory)) ? PersonHistory.SMOKE
								: personHistory + "," + PersonHistory.SMOKE;
					}
				}
				Map<String, Object> drinkFreqCode = (Map<String, Object>) lifeStyle
						.get("drinkFreqCode");
				if (drinkFreqCode != null && drinkFreqCode.size() > 0) {
					String drinkCode = (String) drinkFreqCode.get("key");
					if (!drinkCode.equals("1")) {
						personHistory = (personHistory == null || ""
								.equals(personHistory)) ? PersonHistory.DRINK
								: personHistory + "," + PersonHistory.DRINK;
					}
				}
				past.put("personHistory", personHistory);
			}
			resMap.putAll(past);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get past history.", e);
			throw new ServiceException(e);
		}
		HealthRecordModel hrm = new HealthRecordModel(dao);
		try {
			// ** 获取孕妇丈夫信息，包括丈夫家族史
			Map<String, Object> partner = hrm.getHealthRecordByEmpiId(empiId);
			if (partner != null && partner.size() > 0) {
				String husbandEmpiId = (String) partner.get("partnerId");
				resMap.put("husbandEmpiId", husbandEmpiId);
				resMap.put("husbandName", partner.get("partnerName"));
				if (husbandEmpiId != null && !"".equals(husbandEmpiId)) {
					Map<String, Object> husband = hrm
							.getHealthRecordByEmpiId(husbandEmpiId);
					if (husband != null && husband.size() > 0) {
						resMap.put("husbandPhrId", husband.get("phrId"));
					}
					String husbandFamilyHistory = phm
							.getFamilyPastHistoryByEmpiId(husbandEmpiId);
					resMap.put("husbandFamilyHistory", husbandFamilyHistory);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get husband information.", e);
			throw new ServiceException(e);
		}
		// ** 获取孕妇管理信息
		Map<String, Object> healthInfo;
		try {
			healthInfo = hrm.getHealthRecordByEmpiId(empiId);
			if (healthInfo != null && healthInfo.size() > 0) {
				String homeAddress = (String) healthInfo.get("regionCode");
				resMap.put("manaDoctorId", healthInfo.get("manaDoctorId"));
				resMap.put("restRegionCode", homeAddress);
				resMap.put("homeAddress", homeAddress);
				AreaGridModel agm = new AreaGridModel(dao);
				Map<String, Object> data = agm.getNodeInfo(homeAddress);
				String mhcDoctor = (String) data.get("mhcDoctor");
				if (mhcDoctor != null && !mhcDoctor.equals("")) {
					resMap.put("mhcDoctorId", mhcDoctor);
					SystemUserModel suModel = new SystemUserModel(dao);
					List<Map<String, Object>> manageUnits = suModel
							.getUserByLogonName(mhcDoctor, RolesList.FBYS);
					resMap.put("manageUnits", manageUnits);
				}
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get health record information.", e);
			throw new ServiceException(e);
		}

		// ** 获取孕妇前次结案的相关信息。
		Map<String, Object> endMap;
		try {
			endMap = prm.getLastEndManageInfo(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get last end manage information.", e);
			throw new ServiceException(e);
		}
		if (endMap != null && endMap.size() > 0) {
			resMap.putAll(endMap);
		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) SchemaUtil
				.setDictionaryMessageForForm(resMap, MHC_PregnantRecord);
		res.put("body", resBody);
	}

	/**
	 * 通过孕妇编号，获取孕妇信息，并且判断是否已经随访过
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetPregnantRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pkey = (String) reqBody.get("pkey");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		Map<String, Object> resMap;
		try {
			// ** 获取孕妇档案信息
			resMap = prm.getPregnantRecord(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get pregnant record .", e);
			throw new ServiceException(e);
		}
		if (resMap == null || resMap.size() < 1) {
			return;
		}
		EmpiModel em = new EmpiModel(dao);
		String husbandEmpiId = (String) resMap.get("husbandEmpiId");
		Map<String, Object> husband = null;
		try {
			husband = em.getEmpiInfoByEmpiid(husbandEmpiId);
		} catch (ModelDataOperationException e1) {
			logger.error("Failed to get husband record .", e1);
			throw new ServiceException(e1);
		}
		if (husband != null && husband.size() > 0) {
			resMap.put("husbandName", husband.get("personName"));
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		resBody = SchemaUtil.setDictionaryMessageForForm(resMap,
				MHC_PregnantRecord);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			// ** 判断是否已经进行过孕妇随访
			String planId = vpm.getLastVisitedPlanId(pkey,
					BusinessType.MATERNAL, BusinessType.PREGNANT_HIGH_RISK);
			boolean visited = false;
			if (planId != null && !"".equals(planId)) {
				visited = true;
			}
			resBody.put("hasVisited", visited);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get has plan visited .", e);
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}

	/**
	 * 初始化儿童高危因素
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitHighRiskReason(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) reqBody.get("pregnantId");
		String empiId = (String) reqBody.get("empiId");
		List<String> riskList = new ArrayList<String>();
		// ** 从个人基本信息提取相关信息去初始化高危因素。
		EmpiModel em = new EmpiModel(dao);
		Map<String, Object> empi;
		try {
			empi = em.getEmpiInfoByEmpiid(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("get woman empi data failed.", e);
			throw new ServiceException(e);
		}
		Date birthday = (Date) empi.get("birthday");
		int age = BSCHISUtil.calculateAge(birthday, new Date());
		if (age < 18) {
			riskList.add("a10101");
		} else if (age > 40) {
			riskList.add("b10101");
		} else if (age >= 35) {
			riskList.add("a1010101");
		}

		String rhBloodCode = (String) empi.get("rhBloodCode");
		if (rhBloodCode.equals("1")) {// ** Rh血型阴性
			riskList.add("c10113");
		}
		Map<String, Object> pregnantRecord = (HashMap<String, Object>) reqBody
				.get("pregnantRecord");
		if (pregnantRecord == null) {
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			try {
				pregnantRecord = prm.getPregnantRecord(pregnantId);
			} catch (ModelDataOperationException e) {
				logger.error("get woman pregnant record failed.", e);
				throw new ServiceException(e);
			}
		}
		if (pregnantRecord != null) {
			Object ht = pregnantRecord.get("height");
			if (null != ht) {
				double height = (Double) SchemaUtil.getValue(
						MHC_PregnantRecord, "height", ht);
				if (height < 140) {
					riskList.add("b1010101");
				} else if (height <= 145) {
					riskList.add("a10102");
				}
			}
			if ((Integer) pregnantRecord.get("trafficFlow")
					+ (Integer) pregnantRecord.get("naturalAbortion") >= 2) {
				riskList.add("a10201");
			}
			if ((Integer) pregnantRecord.get("preterm") == 1) {
				riskList.add("a1020101");
			}
			if ((Integer) pregnantRecord.get("naturalAbortion") == 3
					|| (Integer) pregnantRecord.get("preterm") >= 2) {
				riskList.add("b10201");
			}
		}
		List<Object> checkList = (List<Object>) reqBody.get("checkList");
		if (checkList != null) {
			for (int i = 0; i < checkList.size(); i++) {
				HashMap<String, Object> check = (HashMap<String, Object>) checkList
						.get(i);
				// ** 血红蛋白
				if (check.get("indexCode").equals("50101")
						&& BSCHISUtil
								.isNumber((String) check.get("indexValue"))) {
					double value = Double.valueOf((String) check
							.get("indexValue"));
					if (value >= 61 && value <= 90) {// ** 中度贫血
						riskList.add("a20501");
					} else if (value >= 31 && value <= 60) {
						riskList.add("b20501");// ** 重度贫血
					}
					continue;
				}
				// ** 血小板 ，单位10^9/L
				if (check.get("indexCode").equals("50103")
						&& BSCHISUtil
								.isNumber((String) check.get("indexValue"))) {
					double value = Double.valueOf((String) check
							.get("indexValue"));
					if (value >= 50 && value <= 70) {
						riskList.add("a20502");
					} else if (value >= 20 && value <= 50) {
						riskList.add("b20502");
					} else if (value < 20) {
						riskList.add("c20502");
					}
				}
			}
		}
		HashMap<String, Object> visitRecord = (HashMap<String, Object>) reqBody
				.get("visitRecord");
		if (visitRecord != null) {
			Object wgh = visitRecord.get("weight");
			// ** 体重
			if (null != wgh && !"".equals(wgh)) {
				double weight = (Double) SchemaUtil.getValue(MHC_VisitRecord,
						"weight", wgh);
				if (weight < 40) {
					riskList.add("b1010102");
				} else if (weight < 45) {
					riskList.add("a1010201");
				} else if (weight >= 80) {
					riskList.add("a1010202");
				}
			}
			// ** 浮肿2度以上。
			String edemaStatus = (String) visitRecord.get("edemaStatus");
			if (edemaStatus != null && !"".equals(edemaStatus)) {
				if (Integer.valueOf(edemaStatus) >= 3) {
					riskList.add("a30604");
				}
			}

			// ** 胎心率<120或>160间。
			Object fetalHeartRate = visitRecord.get("fetalHeartRate");
			if (fetalHeartRate != null && !"".equals(fetalHeartRate)) {
				Integer fhr = (Integer) SchemaUtil.getValue(MHC_VisitRecord,
						"fetalHeartRate", fetalHeartRate);
				if (fhr < 120) {
					riskList.add("b31301");

				} else if (fhr > 160) {
					riskList.add("b3130101");
				}
			}
			String checkWeek = (String) visitRecord.get("checkWeek");
			if (checkWeek != null && !"".equals(checkWeek)) {
				Integer cw = Integer.valueOf(checkWeek);
				Object fp = visitRecord.get("fetalPosition");
				if (cw >= 32 && fp != null && !"".equals(fp)) {// ** 胎方位
					String fetalPosition = (String) SchemaUtil.getValue(
							MHC_VisitRecord, "fetalPosition", fp);
					int fpt = Integer.valueOf(fetalPosition);
					if (fpt >= 13 && fpt <= 18) {
						riskList.add("a30201");
					} else if (fpt >= 19 && fpt <= 22) {
						riskList.add("b30201");
					}
				}
			}

			// ** 羊水。
			if (visitRecord.containsKey("amnioticFluidDepth")) {
				String amnioticFluidDepth = (String) visitRecord
						.get("amnioticFluidDepth");
				if (BSCHISUtil.isNumber(amnioticFluidDepth)) {
					double afd = Double.parseDouble(amnioticFluidDepth);
					if (afd > 7) {
						riskList.add("b30801");
					} else if (afd <= 2) {
						riskList.add("b3080101");
					}
				}
			} else if (visitRecord.containsKey("afi")) {
				String strAfi = (String) visitRecord.get("afi");
				if (BSCHISUtil.isNumber(strAfi)) {
					double afi = Double.parseDouble(strAfi);
					if (afi > 18) {
						riskList.add("b30801");
					} else if (afi <= 5) {
						riskList.add("b3080101");
					}
				}
			}
		}
		res.put("body", riskList);
	}

	/**
	 * 初始化孕妇产后42天记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitPostnatal42dayRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) reqBody.get("pregnantId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> data = prm.getPregnantRecord(pregnantId);
			if (data == null || data.size() < 1) {
				return;
			}
			Map<String, Object> resBody = new HashMap<String, Object>();
			resBody.put("pregnantId", data.get("pregnantId"));
			resBody.put("checkManaUnit", data.get("manaUnitId"));
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					MHC_Postnatal42dayRecord));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to init  postnatal 42day record .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 初始化孕妇产后访视记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitPostnatalVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) reqBody.get("pregnantId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> record = prm.getPregnantRecord(pregnantId);
			if (record == null || record.size() < 1) {
				return;
			}
			Map<String, Object> resBody = new HashMap<String, Object>();
			resBody.put("checkManaUnit", record.get("manaUnitId"));
			resBody.put("pregnantId", record.get("pregnantId"));
			Map<String, Object> data = prm
					.getFirstPostnatalVisitInfo(pregnantId);
			if (data != null && data.size() > 0) {
				resBody.put("birthDay", data.get("birthDay"));
			}
			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					MHC_PostnatalVisitInfo));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to init  postnatal 42day record .", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 初始化新生儿访视信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitBabyVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String empiId = (String) reqBody.get("empiId"); // ** 新生儿母亲empiId
		String pregnantId = (String) reqBody.get("pregnantId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		try {
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			Map<String, Object> data = prm.getFirstBabyVisitInfo(pregnantId);
			if (data != null && data.size() > 0) {
				resBody.put("motherJob", data.get("motherJob"));
				resBody.put("motherPhone", data.get("motherPhone"));
				resBody.put("motherBirth", data.get("motherBirth"));
				resBody.put("motherName", data.get("motherName"));
				resBody.put("motherCardNo", data.get("motherCardNo"));
				resBody.put("motherEmpiId", data.get("motherEmpiId"));
				resBody.put("fatherJob", data.get("fatherJob"));
				resBody.put("fatherPhone", data.get("fatherPhone"));
				resBody.put("fatherBirth", data.get("fatherBirth"));
				resBody.put("fatherName", data.get("fatherName"));
				resBody.put("fatherCardNo", data.get("fatherCardNo"));
				resBody.put("fatherEmpiId", data.get("fatherEmpiId"));
				resBody.put("babyBirth", data.get("babyBirth"));
				resBody.put("babyAddress", data.get("babyAddress"));
				resBody.put("gestation", data.get("gestation"));
				resBody.put("deliveryUnit", data.get("deliveryUnit"));
				res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
						MHC_BabyVisitInfo));
				return;
			}
			EmpiModel em = new EmpiModel(dao);
			HealthRecordModel hrm = new HealthRecordModel(dao);
			Map<String, Object> motherInfo = em.getEmpiInfoByEmpiid(empiId);
			if (motherInfo == null || motherInfo.size() < 1) {
				return;
			}
			resBody.put("motherJob", motherInfo.get("workCode"));
			resBody.put("motherPhone", motherInfo.get("contactPhone"));
			resBody.put("motherBirth", motherInfo.get("birthday"));
			resBody.put("motherName", motherInfo.get("personName"));
			resBody.put("motherCardNo", motherInfo.get("idCard"));
			resBody.put("motherEmpiId", motherInfo.get("empiId"));
			Map<String, Object> fatherRecord = hrm
					.getPartnerHealthRecordByEmpiId(empiId);
			if (fatherRecord != null && fatherRecord.size() > 0) {
				String fatherEmpi = (String) fatherRecord.get("empiId");
				if (fatherEmpi != null && !"".equals(fatherEmpi)) {
					Map<String, Object> fatherInfo = em
							.getEmpiInfoByEmpiid(empiId);
					resBody.put("fatherJob", fatherInfo.get("workCode"));
					resBody.put("fatherPhone", fatherInfo.get("contactPhone"));
					resBody.put("fatherBirth", fatherInfo.get("birthday"));
					resBody.put("fatherName", fatherInfo.get("personName"));
					resBody.put("fatherCardNo", fatherInfo.get("idCard"));
					resBody.put("fatherEmpiId", fatherInfo.get("empiId"));
				}
			}

			res.put("body", SchemaUtil.setDictionaryMessageForForm(resBody,
					MHC_BabyVisitInfo));
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get mother info.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存孕妇建档信息以及首次随访信息,体检信息。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePregnantRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		HashMap<String, Object> pregnantRecord = (HashMap<String, Object>) reqBody
				.get("pregnantRecord");
		HashMap<String, Object> firstVisit = (HashMap<String, Object>) reqBody
				.get("firstVisit");
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		String op = (String) req.get("op");
		String pregnantId = (String) pregnantRecord.get("pregnantId");
		String empiId = (String) pregnantRecord.get("empiId");
		pregnantRecord.put("isCreateRecord", true); // **标识为建档操作
		// ** 如果是更新档案要重建随访计划。
		String lastMenstrualPeriod = (String) pregnantRecord
				.get("lastMenstrualPeriod");
		// ** 如果末次月经时间修改或者计划重新生成过，则需要刷新孕妇随访模块
		Boolean refreshVisitModule = false;
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			boolean result = prm.checkLastMenstrualPeriod(lastMenstrualPeriod,
					empiId, pregnantId);
			if (result) {
				res.put(RES_CODE, ServiceCode.CODE_TARGET_EXISTS);
				res.put(RES_MESSAGE, "该末次月经的记录已经存在，请更新记录!");
				throw new ServiceException("该末次月经的记录已经存在!");

			}
		} catch (ModelDataOperationException e1) {
			logger.error(
					"Failed to check lst menstrual period for pregnant record.",
					e1);
			throw new ServiceException(e1);
		}
		boolean lmpChanged = true;
		if (op.equals("update")) {
			String gsetational = lastMenstrualPeriod.substring(0, 10);
			Date lmp;
			try {
				lmp = prm.getGsetational(pregnantId);
				if (lmp != null) {
					if (gsetational.equals(BSCHISUtil.toString(lmp, null))) {
						lmpChanged = false;
					}
				}
			} catch (ModelDataOperationException e) {
				logger.error(
						"Failed to get lst menstrual period for pregnant record.",
						e);
				throw new ServiceException(e);
			}
		}
		// ** 末次月经修改，需要刷新随访模块
		if (lmpChanged == true) {
			refreshVisitModule = true;
			res.put("refreshVisitModule", refreshVisitModule);
		}

		// ** 判断是否已经进行过孕妇随访
		VisitPlanModel vpm = new VisitPlanModel(dao);
		String planId = null;
		try {
			planId = vpm.getLastVisitedPlanId(pregnantId,
					BusinessType.MATERNAL, BusinessType.PREGNANT_HIGH_RISK);
		} catch (ModelDataOperationException e1) {
			logger.error("Failed to check pregnant visited.", e1);
			throw new ServiceException(e1);
		}
		// ** 如果已经随访过，不调整计划
		boolean visited = false;
		if (planId != null && !"".equals(planId)) {
			visited = true;
		}

		// ** 更新个人既往史
		try {
			updatePastHistory(pregnantRecord, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to update pastHistory record.", e);
			throw new ServiceException(e);
		}

		// ** 保存孕妇档案
		Map<String, Object> recordRes;
		try {
			recordRes = prm.savePregnantRecord(pregnantRecord, op);
			resBody.put("pregnantRecord", recordRes);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant record.", e);
			throw new ServiceException(e);
		}
		if (op.equals("create")) {
			pregnantId = (String) recordRes.get("pregnantId");
			resBody.put("pregnantId", pregnantId);
			pregnantRecord.put("pregnantId", pregnantId);
		}
		vLogService.saveVindicateLog(MHC_PregnantRecord, op, pregnantId, dao,
				empiId);
		vLogService.saveRecords("FU", op, dao,empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, YFJDHDYCCJ_YCFFW, null, dao);
		// ** 更新儿童出生缺陷中的母亲信息
		try {
			updateChildDefect(pregnantRecord, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to update child defect record.", e);
			throw new ServiceException(e);
		}

		try {
			// ** 更新妇女档案
			prm.updateWomanRecord(pregnantRecord, vLogService);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to update worman record.", e);
			throw new ServiceException(e);
		}
		// ** 生成高危随访的计划类型编号
		String highRiskPlanType = null;
		boolean highRisknessesChanged = false;
		if (firstVisit != null) {
			highRisknessesChanged = (Boolean) firstVisit
					.get("highRisknessesChanged");
			firstVisit.put("pregnantId", pregnantId);
			try {
				// ** 保存首次随访
				Map<String, Object> visitRes = prm.savePregnantFirstVisit(
						firstVisit, op);
				visitRes.put("pregnantId", pregnantId);
				resBody.put("firstVisit", visitRes);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save first visit record.", e);
				throw new ServiceException(e);
			}
			// ** 修改孕妇一览表
			String screeningDate = (String) firstVisit.get("screeningDate");
			try {
				prm.updateHighRiskVisitReasonList(screeningDate, pregnantId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Failed to update high rish visit reason list record.",
						e);
				throw new ServiceException(e);
			}
			// **保存高危因素相关信息
			if (highRisknessesChanged) {
				try {
					// ** 删除原有高危因素
					prm.deleteHighRiskness(pregnantId, FIRST_VISIT_ID);
					List<Object> highRisknesses = (List<Object>) firstVisit
							.get("highRisknesses");
					if (highRisknesses != null) {
						firstVisit.put("visitId", FIRST_VISIT_ID);
						firstVisit.put("endDate", BSCHISUtil.getEndDateOfWeek(
								lastMenstrualPeriod, new Date()));
						firstVisit.put("lastMenstrualPeriod",
								lastMenstrualPeriod);
						// ** 保存高危因素
						prm.saveHighRisknessReasons(firstVisit);
						Date visitDate = prm.getPregnantScreenDate(pregnantId);
						firstVisit.put("visitDate", visitDate);
						// ** 删除高危因素一览表
						prm.deleteHighRisknessReasonsList(pregnantId);
						// ** 保存高危因素一览表
						prm.saveHighRisknessReasonsList(firstVisit);
						// ** 如果没有随访过则需要生成高危随访计划
						if (visited == false) {
							pregnantRecord
									.put("highRisknesses", highRisknesses);
							highRiskPlanType = makeNextHighVisitPlan(
									pregnantRecord, ctx);
							// ** 计划调整需要刷新孕妇随访模块
							refreshVisitModule = true;
							res.put("refreshVisitModule", refreshVisitModule);
						}
					}
				} catch (Exception e) {
					logger.error("Failed to save high risknesses.", e);
					throw new ServiceException(e);
				}
			}
		}
		// ** 保存妇科检查数据。
		List<Object> checkList = (List<Object>) reqBody.get("checkUpList");
		if (checkList != null) {
			try {
				prm.deletePregnantCheckList(pregnantId, null);
				prm.savePregnantCheckList(checkList, pregnantId, null);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save check list.", e);
				throw new ServiceException(e);
			}
		}
		// @@ 如果前面保存高危因素时未生成高危计划，有两种可能，一种是高危因素没了，
		// @@ 另一种是存在的高危因素不需要生成高危计划
		String planMode;
		try {
			planMode = ApplicationUtil.getProperty(Constants.UTIL_APP_ID,
					BusinessType.MATERNAL + "_planMode");
		} catch (ControllerException e) {
			throw new ServiceException(e);
		}
		if (planMode == null || planMode.equals("")) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "未配置随访计划生产方式，请到系统配置界面中配置。");
			return;
		}

		try {
			if (highRiskPlanType == null || highRiskPlanType.equals("")) {
				// @@ 如果建档的时候是按预约生成计划，以建档日期为第一次计划日期。
				if (planMode.equals(PlanMode.BY_RESERVED)) {
					pregnantRecord.put("reserveDate",
							pregnantRecord.get("createDate"));
				}
				if (op.equals("create")) {
					// @@ 如果是新建档案直接生成常规随访计划。
					pregnantRecord.put("businessType", BusinessType.MATERNAL);
					pregnantRecord.put("pregnantId", pregnantId);
					pregnantRecord.put("nextDate",
							BSCHISUtil.toString(new Date(), null));
					getVisitPlanCreator().create(pregnantRecord, ctx);

					return;
				}
				if (planMode.equals(PlanMode.BY_PLAN_TYPE)) {
					if (highRisknessesChanged == false && lmpChanged == false)
						return;
				} else {
					if (lmpChanged == false) {
						return;
					}
				}
				// ** 已经随访过不再重新生成计划
				if (visited == true) {
					return;
				}
				// @@ 如果是修改了末次月经日期，需要判断原来有没有高危因素，
				// @@ 这些高危因素需不需要生成高危计划，以此判定是生成危还是常规计划
				int maxFrequence = 0;
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				if (planMode.equals(PlanMode.BY_PLAN_TYPE)) {
					list = prm.getFirstHighRiskReasons(pregnantId,
							FIRST_VISIT_ID);
					for (Map<String, Object> map : list) {
						Object fqc = map.get("frequence");
						int f = (Integer) SchemaUtil.getValue(
								MHC_HighRiskVisitReason, "frequence", fqc);
						if (f > maxFrequence) {
							maxFrequence = f;
						}
					}
				}
				// @@ 如果前面没有生成高危计划并且档案里没有高危因素，说明是正常孕妇
				if (maxFrequence == 0) {
					pregnantRecord.put("businessType", BusinessType.MATERNAL);
					pregnantRecord.put("pregnantId", pregnantId);
					pregnantRecord.put("nextDate",
							BSCHISUtil.toString(new Date(), null));
					getVisitPlanCreator().create(pregnantRecord, ctx);
					refreshVisitModule = true;
				} else {
					HashMap<String, Object> obj = new HashMap<String, Object>();
					obj.put("isCreateRecord", true);
					obj.put("pregnantId", pregnantId);
					obj.put("lastMenstrualPeriod", lastMenstrualPeriod);
					obj.put("highRisknesses", list);
					obj.put("empiId", empiId);
					obj.put("createDate", pregnantRecord.get("createDate"));
					makeNextHighVisitPlan(obj, ctx);
					refreshVisitModule = true;
				}
				res.put("visitPlanChanged", refreshVisitModule);
			}
		} catch (Exception e) {
			logger.error("Failed to create pregnant visit plan.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 终止管理孕妇档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveEndManage(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			endManagePregnant(body, dao);
		} catch (Exception e) {
			logger.error("Failed to end manage pregnant record.", e);
			throw new ServiceException(e);
		}
		String pregnantId = (String) body.get("pregnantId");
		String empiId = (String) body.get("empiId");
		vLogService.saveVindicateLog(MHC_PregnantRecord, "7", pregnantId, dao,
				empiId);
	}

	/**
	 * 注销孕妇档案
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLogOutPregnantRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String pregnantId = (String) body.get("pregnantId");
		String empiId = (String) body.get("empiId");
		if (pregnantId == null || "".equals(pregnantId)) {
			return;
		}
		String deadReason = StringUtils.trimToEmpty((String) body
				.get("deadReason"));
		String cancellationReason = StringUtils.trimToEmpty((String) body
				.get("cancellationReason"));
		try {
			PregnantRecordModel prm = new PregnantRecordModel(dao);
			prm.logOutPregnantRecord("pregnantId", pregnantId,
					cancellationReason, deadReason);
			VisitPlanModel vpm = new VisitPlanModel(dao);
			vpm.logOutVisitPlan(vpm.RECORDID, pregnantId,
					BusinessType.MATERNAL, BusinessType.PREGNANT_HIGH_RISK);
		} catch (Exception e) {
			logger.error("Failed to logout pregnant record.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MHC_PregnantRecord, "3", pregnantId, dao,
				empiId);
		vLogService.saveRecords("FU", "logout", dao,empiId);
	}

	/**
	 * 获取孕妇体检列表信息
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException
	 * @throws JSONException
	 */
	protected void doGetPregnantCheckUp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ControllerException {

		List<?> cnd = (List<?>) req.get("cnd");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		List<Map<String, Object>> data;
		try {
			data = prm.getPregnantCheckUp(cnd);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get pregnant check up data list");
			throw new ServiceException(e);
		}
		List<Map<String, Object>> resBody = new ArrayList<Map<String, Object>>();
		res.put("body", resBody);
		if (data == null || data.size() < 1) {
			return;
		}
		String[] indexValue1 = new String[] { "尿蛋白", "尿糖", "尿酮体" };
		String[] indexValue2 = new String[] { "HBSAG", "HBSAB", "HBEAG",
				"HBEAB", "HBCAB", "梅毒血清学试验", "HIV抗体检测", "弓形虫", "巨细胞病毒", "风疹病毒" };
		Dictionary dic = null;
		for (int i = 0; i < data.size(); i++) {
			HashMap<String, Object> record = (HashMap<String, Object>) data
					.get(i);
			String indexName = (String) record.get("indexName");
			String indexValue = (String) record.get("indexValue");
			boolean isDic = false;
			if (indexValue != null && !indexValue.equals("")) {
				for (String subValue1 : indexValue1) {
					if (indexName.equalsIgnoreCase(subValue1)) {
						dic = DictionaryController.instance().get(
								"chis.dictionary.indexValue1");
						record.put("indexValue_text", dic.getText(indexValue));
						isDic = true;
						break;
					}
				}
				if (isDic) {
					resBody.add(record);
					continue;
				}
				for (String subValue2 : indexValue2) {
					if (indexName.equalsIgnoreCase(subValue2)) {
						dic = DictionaryController.instance().get(
								"chis.dictionary.indexValue2");
						record.put("indexValue_text", dic.getText(indexValue));
						isDic = true;
						break;
					}
				}

				if (isDic) {
					resBody.add(record);
					continue;
				}
				if (indexName.equalsIgnoreCase("阴道分泌物")) {
					dic = DictionaryController.instance().get(
							"chis.dictionary.indexValue3");
					record.put("indexValue_text", dic.getText(indexValue));
					isDic = true;
				}
				if (isDic) {
					resBody.add(record);
					continue;
				}
				if (indexName.equalsIgnoreCase("阴道清洁度")) {
					dic = DictionaryController.instance().get(
							"chis.dictionary.indexValue4");
					record.put("indexValue_text", dic.getText(indexValue));
					isDic = true;
				}
				if (!isDic) {
					record.put("indexValue_text", indexValue);
				}
			} else {
				record.put("indexValue_text", indexValue);
			}
			resBody.add(record);
		}
	}

	/**
	 * 获取孕妇的末次月经时间
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetPregnantGsetational(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) body.get("pregnantId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Date date = prm.getGsetational(pregnantId);
			res.put("body", date);
		} catch (ModelDataOperationException e) {
			logger.info("faild to get lastMenstrualPeriod");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取孕周。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param ctx
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetPregnantWeek(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) reqBody.get("pregnantId");
		String strDatum = (String) reqBody.get("datum");
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT);
		Date datum;
		try {
			datum = sdf.parse(strDatum);
		} catch (ParseException e) {
			logger.error("Failed to parse date : " + strDatum, e);
			res.put(RES_CODE, Constants.CODE_DATE_PASE_ERROR);
			res.put(RES_MESSAGE, "解析时间[" + strDatum + "]失败。");
			throw new ServiceException(e);
		}
		int weeks;
		try {
			weeks = getGsetationalWeeks(pregnantId, datum, dao);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get pregnant week.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "获取孕周失败。");
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = (HashMap<String, Object>) res
				.get("body");
		if (resBody == null) {
			resBody = new HashMap<String, Object>();
			res.put("body", resBody);
		}
		resBody.put("pregnantWeek", weeks);
	}

	/**
	 * 保存孕妇特殊情况信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePregnantScreen(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String pregnantId = (String) reqBody.get("pregnantId");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.savePregnantScreen(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant  screen record info.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBodyMap = (Map<String, Object>) res
					.get("body");
			pregnantId = (String) resBodyMap.get("pregnantId");
		}
		vLogService.saveVindicateLog(MHC_PregnantScreen, op, pregnantId, dao,
				empiId);
	}

	/**
	 * 保存孕妇特殊情况信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveSpecialRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String specialId = (String) reqBody.get("specialId");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.saveSpecialRecord(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant  special record info.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MHC_PregnantSpecial, op, specialId, dao,
				empiId);
	}

	/**
	 * 保存孕妇产后42天健康检查信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePostnatal42dayRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String recordId = (String) reqBody.get("recordId");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> map = prm.savePostnatal42dayRecord(reqBody, op,
					res);
			if ("create".equals(op)) {
				recordId = (String) map.get("recordId");
				Map<String, Object> body = new HashMap<String, Object>();
				body.putAll(reqBody);
				body.put("recordId", recordId);
				body = SchemaUtil.setDictionaryMessageForForm(body,
						MHC_Postnatal42dayRecord);
				res.put("body", body);
			}
			if(reqBody.get("treat")!=null&&reqBody.get("treat").equals("1")){
				prm.logOutPregnantRecord("empiId", empiId, "9", null);
			}
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to save pregnant  postnatal 42day record info.", e);
			throw new ServiceException(e);
		}

		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			recordId = (String) resBody.get("recordId");
		}
		vLogService.saveVindicateLog(MHC_Postnatal42dayRecord, op, recordId,
				dao, empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, CH42TFS_YCFFW, null, dao);
	}

	/**
	 * 保存孕妇产后访视信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePostnatalVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String visitId = (String) reqBody.get("visitId");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		if (visitId != null && !"".equals(visitId)) {
			op = "update";
		}
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.savePostnatalVisitInfo(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant  postnatal visit info.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBodyMap = (Map<String, Object>) res
					.get("body");
			visitId = (String) resBodyMap.get("visitId");
		}
		vLogService.saveVindicateLog(MHC_PostnatalVisitInfo, op, visitId, dao,
				empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, CHFS_YCFFW, null, dao);
	}

	/**
	 * 保存新生儿访视信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBabyVisitInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String babyId = (String) reqBody.get("babyId");
		String empiId = (String) reqBody.get("empiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.saveBabyVisitInfo(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save baby visit info.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			babyId = (String) resBody.get("babyId");
		}
		vLogService
				.saveVindicateLog(MHC_BabyVisitInfo, op, babyId, dao, empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, CHFS_YCFFW, null, dao);
	}

	/**
	 * 保存新生儿随访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBabyVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		String visitId = (String) reqBody.get("visitId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.saveBabyVisitRecord(reqBody, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save baby visit record.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			visitId = (String) resBody.get("visitId");
		}
		vLogService.saveVindicateLog(MHC_BabyVisitRecord, op, visitId, dao);
	}

	/**
	 * 获取孕妇终止管理一览表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetEndManagementRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> result = prm.queryEndManagement(req);
			if (result == null || result.size() < 1) {
				return;
			}
			res.putAll(result);
			List<Map<String, Object>> resBody = (List<Map<String, Object>>) res
					.get("body");
			for (Map<String, Object> body : resBody) {
				Date endDate = (Date) body.get("endDate");
				Date lastMenstrualPeriod = (Date) body
						.get("lastMenstrualPeriod");
				int weeks = BSCHISUtil.getWeeks(lastMenstrualPeriod, endDate);
				body.put("week", weeks);
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get pregnant end management record.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 获取孕周
	 * 
	 * @param paregnantId
	 * @param session
	 * @return
	 * @throws ModelDataOperationException
	 */
	private int getGsetationalWeeks(String pregnantId, Date datum, BaseDAO dao)
			throws ModelDataOperationException {
		if (pregnantId == null || pregnantId.trim().length() == 0) {
			return -1;
		}
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		Date lastTimeOfCatamenia = prm.getGsetational(pregnantId);
		if (lastTimeOfCatamenia == null) {
			return -1;
		}
		return BSCHISUtil.getWeeks(lastTimeOfCatamenia, datum);
	}

	/**
	 * 同步既往病史到个人既往史
	 * 
	 * @param pregnantRecord
	 * @param dao
	 * @throws ModelDataOperationException
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	private void updatePastHistory(HashMap<String, Object> pregnantRecord,
			BaseDAO dao) throws ModelDataOperationException, ValidateException {
		String pastHistory = (String) pregnantRecord.get("pastHistory");
		if (pastHistory == null || "".equals(pastHistory)) {
			return;
		}
		String pregnantId = (String) pregnantRecord.get("pregnantId");
		String empiId = (String) pregnantRecord.get("empiId");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		Map<String, Object> record = prm.getPregnantRecord(pregnantId);
		String prePastHistory = null;
		if (record != null && record.size() > 0) {
			prePastHistory = StringUtils.trimToEmpty((String) record
					.get("pastHistory"));
		}
		if (pastHistory.equals(prePastHistory)) {
			return;
		}
		String[] temp = pastHistory.split(",");
		if (temp.length < 1) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			String past = temp[i];
			if (past.equals(MHCPastHistory.NOT_HAVE)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_NOT_HAVE).append(",");
			} else if (past.equals(MHCPastHistory.CARDIOPATHY)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_CARDIOPATHY).append(
						",");
			} else if (past.equals(MHCPastHistory.RENAL)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_RENAL).append(",");
			} else if (past.equals(MHCPastHistory.LIVER)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_LIVER).append(",");
			} else if (past.equals(MHCPastHistory.HYPERTENSION)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_HYPERTENSION).append(
						",");
			} else if (past.equals(MHCPastHistory.ANAEMIA)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_ANAEMIA).append(",");
			} else if (past.equals(MHCPastHistory.DIABETES)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_DIABETES).append(",");
			} else if (past.equals(MHCPastHistory.OTHER)) {
				sb.append(PastHistoryCode.PASTHIS_SCREEN_OTHER).append(",");
			}
		}
		if (sb.length() < 1) {
			return;
		}
		String data = sb.substring(0, sb.length() - 1);
		if (data.length() < 1) {
			return;
		}
		Map<String, Object> upRecord = (Map<String, Object>) pregnantRecord
				.clone();
		upRecord.put("pastHistory", data);
		PastHistoryModel phm = new PastHistoryModel(dao);
		phm.deletePastHistory(empiId, PastHistoryCode.SCREEN);
		phm.updatePastHistory(upRecord, "pastHistory", "otherPastHistory",
				PastHistoryCode.SCREEN, PastHistoryCode.PASTHIS_SCREEN_OTHER);
	}

	/**
	 * 修改儿童出生缺陷信息
	 * 
	 * @param pregnantRecord
	 * @param dao
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	private void updateChildDefect(Map<String, Object> pregnantRecord,
			BaseDAO dao) throws ValidateException, ModelDataOperationException {
		String empiId = (String) pregnantRecord.get("empiId");
		ChildrenHealthModel chm = new ChildrenHealthModel(dao);
		List<Map<String, Object>> datas = chm.getDefectByMotherEmpiId(empiId);
		if (datas == null) {
			return;
		}
		EmpiModel em = new EmpiModel(dao);
		Map<String, Object> empi = em.getEmpiInfoByEmpiid(empiId);
		if (empi == null || empi.size() < 1) {
			return;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("postCode", empi.get("zipCode"));
		data.put("literacy", empi.get("educationCode"));
		HealthRecordModel hrm = new HealthRecordModel(dao);
		Map<String, Object> health = hrm.getHealthRecordByEmpiId(empiId);
		String regionCode_text = (String) health.get("regionCode_text");
		data.put("homeAddress", regionCode_text);
		data.put("pregnancyTimes", pregnantRecord.get("gravidity"));
		int vaginalDelivery = (Integer) SchemaUtil.getValue(MHC_PregnantRecord,
				"vaginalDelivery", pregnantRecord.get("vaginalDelivery"));
		int abdominalDelivery = (Integer) SchemaUtil.getValue(
				MHC_PregnantRecord, "abdominalDelivery",
				pregnantRecord.get("abdominalDelivery"));
		int birthTimes = vaginalDelivery + abdominalDelivery;
		data.put("birthTimes", birthTimes);
		for (Map<String, Object> map : datas) {
			map.putAll(data);
			chm.saveDefectChildrenRecord(map, "update");
		}
	}

	/**
	 * 根据网格地址编码获取妇保医生信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetMhcDoctorInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String regionCode = (String) body.get("regionCode");
		res.put("body", body);
		try {
			AreaGridModel agModel = new AreaGridModel(dao);
			Map<String, Object> nodeMap = agModel.getNodeInfo(regionCode);
			body.put("nodeMap", nodeMap);
			if (nodeMap == null) {
				return;
			}
			String mhcDoctor = (String) nodeMap.get("mhcDoctor");
			body.putAll(SchemaUtil.setDictionaryMessageForForm(nodeMap,
					EHR_AreaGridChild));
			if (mhcDoctor == null) {
				return;
			}

			SystemUserModel suModel = new SystemUserModel(dao);
			List<Map<String, Object>> manageUnits = suModel.getUserByLogonName(
					mhcDoctor, RolesList.FBYS);
			body.put("manageUnits", manageUnits);
		} catch (ModelDataOperationException e) {
			logger.error("Get children base infomation by phrId failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存孕妇产时信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDeliveryRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String DRID = (String) body.get("DRID");
		String empiId = (String) body.get("empiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			prm.saveDeliveryRecord(body, op, res);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant delivery record.", e);
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			DRID = (String) resBody.get("DRID");
		}
		vLogService.saveVindicateLog(MHC_DeliveryOnRecord, op, DRID, dao,
				empiId);
	}

	/**
	 * 保存孕妇产时新生儿信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveDeliveryChildren(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		String DRCID = (String) body.get("DRCID");
		String empiId = (String) body.get("childEmpiId");
		String op = (String) req.get("op");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> resBody = prm.saveDeliveryChildren(body, op);
			res.put("body", resBody);
			if ("create".equals(op)) {
				DRCID = (String) resBody.get("DRCID");
			}
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save pregnant delivery children record.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(MHC_DeliveryOnRecordChild, op, DRCID, dao,
				empiId);
	}

	/**
	 * 保存新生儿随访基本信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBabyVisitInfoToHtml(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			String s = body.get("birthStatus").toString();// 将list类型转换string类型
			String ss = s.substring(s.indexOf("[") + 1, s.indexOf("]"))
					.replace(" ", "");
			// if (s != null && s.length() > 0) {
			// Pattern p = Pattern.compile("[^0-9]");
			// Matcher m = p.matcher(s);
			// body.put("birthStatus", m.replaceAll("").trim());
			// }
			body.put("birthStatus", ss);
			Map<String, Object> resBody = prm.saveBabyVisitInfoToHtml(body);

			resBody.put("birthStatus", s);
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("fatherJob", resBody.get("fatherJob"));
			resMap.put("motherJob", resBody.get("motherJob"));
			resBody.putAll(SchemaUtil.setDictionaryMessageForForm(resMap,
					MHC_BabyVisitInfo));
			res.put("body", resBody);

		} catch (ModelDataOperationException e) {
			logger.error("保存新生儿随访基本信息失败。", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存新生儿随访记录 html
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveBabyVisitRecordToHtml(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			Map<String, Object> mData = prm.saveBabyVisitRecordToHtml(reqBody);
			HashMap<String, Object> visitData = new HashMap<String, Object>();
			visitData.put("visitDoctor", mData.get("visitDoctor"));
			mData.putAll(SchemaUtil.setDictionaryMessageForForm(visitData,
					BSCHISEntryNames.MHC_BabyVisitRecord));
			res.put("body", mData);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save baby visit record.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 孕妇html页面的初始化数据拼接
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadHtmlData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		// ** 获取妇女基本信息
		Map<String, Object> wrMap;
		try {
			wrMap = prm.LoadHtmlData(body);
			res.put("body", wrMap);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get woman record data.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 孕妇html页面的初始化数据拼接
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHtmlData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		// ** 获取妇女基本信息
		Map<String, Object> wrMap;
		try {
			wrMap = prm.SaveHtmlData(body);
			res.put("body", wrMap);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get woman record data.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 初始化新生儿访视信息(个人)
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doInitBabyVisitInfoHtml(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String babyId = (String) reqBody.get("babyId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		data = dao.doLoad(MHC_BabyVisitInfo, babyId);
		if (data != null && data.size() > 0) {
			resBody.put("motherJob", data.get("motherJob"));
			resBody.put("fatherJob", data.get("fatherJob"));
		}
		data.putAll(SchemaUtil.setDictionaryMessageForForm(resBody,
				MHC_BabyVisitInfo));
		res.put("body", data);
	}

	/**
	 * 获取新生儿随访的记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doSelectBabyVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String visitId = (String) reqBody.get("visitId");
		String visitDate = (String) reqBody.get("visitDate");
		Map<String, Object> resBody = null;
		Map<String, Object> data = new HashMap<String, Object>();
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			resBody = prm.selectBabyVisitRecord(visitId, visitDate);
			if (resBody != null && resBody.size() > 0) {
				data.put("visitDoctor", resBody.get("visitDoctor"));
				data.put("ear", resBody.get("ear"));
				data.put("eye", resBody.get("eye"));
				resBody.putAll(SchemaUtil.setDictionaryMessageForForm(data,
						MHC_BabyVisitRecord));
				if (resBody.get("ear") != null) {
					Map<String, Object> map = (Map<String, Object>) resBody
							.get("ear");
					String key = (String) map.get("key");
					String text = (String) map.get("text");
					if (!"1".equals(key)) {
						resBody.put("ear", "7");
						if (!"7".equals(key)
								&& resBody.get("earAbnormal") != null
								&& !"".equals(resBody.get("earAbnormal"))) {
							resBody.put("earAbnormal",
									resBody.get("earAbnormal") + "," + text);
						}else if ("7".equals(key)) {
							resBody.put("earAbnormal",
									resBody.get("earAbnormal"));
						}  else {
							resBody.put("earAbnormal", text);
						}
					} else {
						resBody.put("ear", "1");
					}
				}
				if (resBody.get("eye") != null) {
					Map<String, Object> map = (Map<String, Object>) resBody
							.get("eye");
					String key = (String) map.get("key");
					String text = (String) map.get("text");
					if (!"1".equals(key)) {
						resBody.put("eye", "10");
						if (!"10".equals(key)
								&& resBody.get("eyeAbnormal") != null
								&& !"".equals(resBody.get("eyeAbnormal"))) {
							resBody.put("eyeAbnormal",
									resBody.get("eyeAbnormal") + "," + text);
						} else if ("10".equals(key)) {
							resBody.put("eyeAbnormal",
									resBody.get("eyeAbnormal"));
						} else {
							resBody.put("eyeAbnormal", text);
						}
					} else {
						resBody.put("eye", "1");
					}
				}
				res.put("body", resBody);
			}
		} catch (ModelDataOperationException e) {
			logger.error("获取新生儿随访记录失败", e);
			throw new ServiceException(e);
		}

	}

	//
	/**
	 * 获取新生儿随访的记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetPregnantId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBody.get("empiId");
		Map<String, Object> resBody = null;
		Map<String, Object> data = new HashMap<String, Object>();
		PregnantRecordModel prm = new PregnantRecordModel(dao);
		try {
			data.put("empiId", empiId);
			resBody = prm.getPregnantId(data);
			if (resBody != null && resBody.size() > 0) {

				res.put("body", resBody);
			}
		} catch (ModelDataOperationException e) {
			logger.error("获取孕妇档案编号失败！", e);
			throw new ServiceException(e);
		}

	}
}
