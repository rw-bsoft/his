/**
 * @(#)ChildrenCheckupService.java Created on 2012-1-13 上午11:03:59
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */

package chis.source.cdh;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DebilityDiseaseType;
import chis.source.dic.DevEvaluation;
import chis.source.dic.PlanStatus;
import chis.source.empi.EmpiModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.ApplicationUtil;
import chis.source.util.BSCHISUtil;
import chis.source.util.InputStreamUtils;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.VisitPlanModel;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 儿童体格检查服务
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yaozh</a>
 */

public class ChildrenCheckupService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(ChildrenCheckupService.class);

	/**
	 * 初始化儿童体格检查随访信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	protected void doInitChildCheckUp(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenBaseModel cbm = new ChildrenBaseModel(dao);
		String schema = (String) req.get("schema");
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String phrId = (String) body.get("phrId");
		String birthday = (String) body.get("birthday");
		Boolean needInit = (Boolean) body.get("needInit");
		Integer checkupStage = (Integer) body.get("checkupStage");
		Object nextDate = body.get("nextCheckupDate");
		String checkupDate = body.get("checkupDate").toString();
		Map<String, Object> visitRecord = new HashMap<String, Object>();
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			Map<String, Object> plan = cbm.getPlanVisitDate(empiId,
					checkupStage, BusinessType.CD_IQ, PlanStatus.VISITED);
			if (plan != null && plan.size() > 0) {
				String visitDate = plan.get("visitDate").toString();
				if (visitDate != null && !"".equals(visitDate.toString())) {
					checkupDate = visitDate;
					Date nextChekcupDate = vpm.getNextPlanDate(phrId,
							visitDate, BusinessType.CD_IQ);
					nextDate = nextChekcupDate;
				}
			}
			if (nextDate == null) {
				nextDate = vpm.getNextPlanDate(phrId, checkupDate,
						BusinessType.CD_CU);
			}
			visitRecord.put("checkupDate", checkupDate.substring(0, 10));
			visitRecord.put("nextCheckupDate", nextDate == null ? "" : nextDate
					.toString().substring(0, 10));
			int age = BSCHISUtil.getMonths(BSCHISUtil.toDate(birthday),
					BSCHISUtil.toDate(checkupDate));
			visitRecord.put("checkupStage", age);
			if (needInit != null && needInit == false) {
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						visitRecord, schema));
				return;
			}
			// chb 从直系亲属基本信息（MPI_ChildInfo）中获取出生证号
			Map<String, Object> childObject = cbm.getChildInfoByEmpiId(empiId);
			if (childObject == null || childObject.isEmpty()) {
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						visitRecord, schema));
				return;
			}
			String certificateNo = StringUtils.trimToEmpty((String) childObject
					.get("certificateNo"));
			if (certificateNo == null || !"".equals(certificateNo)) {
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						visitRecord, schema));
				return;
			}
			// chb 根据出生证号 获取 新生儿访视记录
			ChildrenHealthModel chm = new ChildrenHealthModel(dao);
			List<Map<String, Object>> list = chm
					.getBabyVisitRecordByCertificateNoForCheckUp(certificateNo);
			if (list.size() > 0) {
				visitRecord
						.putAll((Map<String, Object>) list.get(list.size() - 1));
			}
			if (visitRecord.size() < 1) {
				res.put("body", visitRecord);
			} else {
				res.put("body", SchemaUtil.setDictionaryMessageForForm(
						visitRecord, schema));
			}
		} catch (ModelDataOperationException e) {
			logger.error("Init children checkup failed.", e);
			throw new ServiceException(e);
		}

	}

	/**
	 * 检验下次随访计划是否已经随访过
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetNextPlanVisited(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> data = (HashMap<String, Object>) req.get("body");
		String checkupStage = (String) data.get("checkupStage");
		String recordId = (String) data.get("recordId");
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		try {
			boolean result = ccm.getNextPlanVisited(checkupStage, recordId);
			res.put("nextPlanVisted", result);
		} catch (ModelDataOperationException e) {
			logger.error("faild to check next plan  visited.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存儿童体格检查信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveChildCheckup(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String schema = (String) req.get("schema");
		String op = (String) req.get("op");
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			String phrId = StringUtils.trimToEmpty((String) body.get("phrId"));
			String empiId = StringUtils
					.trimToEmpty((String) body.get("empiId"));
			Object ht = body.get("height");
			Object wt = body.get("weight");
			double height = (Double) SchemaUtil.getValue(schema, "height", ht);
			double weight = (Double) SchemaUtil.getValue(schema, "weight", wt);
			// 获取年龄 和性别 取标准用
			EmpiModel empi = new EmpiModel(dao);
			Map<String, Object> map = empi.getSexAndBirthdayByPhrId(phrId);
			String sexCode = (String) map.get("sexCode");
			Date birthDay = (Date) map.get("birthday");

			// 年龄别身高体重
			String checkupDate = (String) body.get("checkupDate");
			Date ckupDate = BSCHISUtil.toDate(checkupDate);
			int age = BSCHISUtil.getMonths(birthDay, ckupDate);
			int days = BSCHISUtil.getPeriod(birthDay, ckupDate);
			if (days >= 28 && age < 1) {
				age = 1;
			}
			String devEvaluat = null;
			Map<String, Object> whoage = ccm.getAgeForHW(sexCode, age,
					CDH_WHOAge);
			if (whoage != null && !whoage.isEmpty()) {
				String appraiseWY = ccm.getAppraiseWY(whoage, weight);
				body.put("weightDevelopment", appraiseWY);
				String appraiseHY = ccm.getAppraiseHY(whoage, height);
				body.put("heightDevelopment", appraiseHY);
				String devEvaluatWH = ccm.getDevEvaluationAWH(whoage, weight,
						height);
				if (devEvaluatWH != null && !"".equals(devEvaluatWH)) {
					devEvaluat = devEvaluatWH;
				}
			}
			Map<String, Object> bmis = ccm.getAgeForBMI(sexCode, age);
			if (bmis != null && !bmis.isEmpty()) {
				Double bmi = weight / (height * height / 100 / 100);
				String devEvaluatBMI = ccm.getDevEvaluationBMI(bmis, bmi);
				if (devEvaluatBMI != null) {
					devEvaluat = devEvaluat == null ? devEvaluatBMI
							: devEvaluat + "," + devEvaluatBMI;
				}
			}
			body.put("devEvaluation", devEvaluat);
			ccm.saveCheckupRecord(body, op, schema, res);
			Map<String, Object> respBody = (Map<String, Object>) res
					.get("body");
			String checkupId = (String) body.get("checkupId");
			if (op.equals("create")) {
				checkupId = (String) respBody.get("checkupId");
				body.put("checkupId", checkupId);
			}
			vLogService.saveVindicateLog(schema, op, checkupId, dao);
			//标识签约任务完成
			int monthAge = BSCHISUtil.getMonths(birthDay, ckupDate);
			if(1<=monthAge&&monthAge<=8){
				this.finishSCServiceTask(empiId, ETJKJC_ETFWONE, monthAge+"", dao);
			}
			if(12<=monthAge&&monthAge<=30){
				this.finishSCServiceTask(empiId, ETJKJC_ETFWTOW, monthAge+"", dao);
			}
			if(36<=monthAge&&monthAge<=72){
				this.finishSCServiceTask(empiId, ETJKJC_ETFWTHREE, monthAge+"", dao);
			}
			respBody.putAll(body);
			res.put("body",
					SchemaUtil.setDictionaryMessageForForm(respBody, schema));
			ccm.updatePlan(op, body);
			// 纸质版保存儿童询问
			Application app = null;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e) {
				logger.error(e.getMessage(), e);
			}
			String type = (String) app.getProperty("childrenCheckupType");
			String checkupStage = body.get("checkupStage") + "";
			if ("paper".equals(type)) {
				Schema scQuire = SchemaController.instance().get(
						"chis.application.cdh.schemas.CDH_Inquire");
				List<SchemaItem> items = scQuire.getItems();
				Map<String, Object> quireBody = new HashMap<String, Object>();
				for (int i = 0; i < items.size(); i++) {
					SchemaItem it = items.get(i);
					String itemId = it.getId();
					if (it.getDefaultValue() != null) {
						quireBody.put(itemId, it.getDefaultValue());
					}
				}
				quireBody.put("phrId", body.get("phrId"));
				quireBody.put("manaUnitId", body.get("manaUnitId"));
				quireBody.put("inquireDate", body.get("checkupDate"));
				quireBody.put("ageDate", days);

				quireBody.put("breastMilkCount", "9");
				quireBody.put("vitaminADFlage", "3");
				quireBody.put("appetite", "4");
				quireBody.put("calciumFlage", "3");
				quireBody.put("fecesColor", "4");
				quireBody.put("illness", "2");
				quireBody.put("illnessType", null);
				if (body.get("illnessType") != null
						&& !"".equals(body.get("illnessType"))) {
					String illnessType = (String) body.get("illnessType");
					// 有
					if (!illnessType.endsWith("0")) {
						quireBody.put("illness", "1");
						quireBody.put("illnessType", illnessType);
					}
				}
				quireBody.put("pneumoniaCount", body.get("pneumoniaCount"));
				quireBody.put("diarrheaCount", body.get("diarrheaCount"));
				quireBody.put("traumaCount", body.get("traumaCount"));
				quireBody.put("otherCount", body.get("otherCount"));

				if (body.get("hwhd") != null && !"".equals(body.get("hwhd"))) {
					double hwhd = Double.parseDouble(body.get("hwhd") + "");
					if (hwhd >= 2) {
						quireBody.put("outdoorActivities", "1");
					} else if (hwhd >= 1 && hwhd < 2) {
						quireBody.put("outdoorActivities", "2");
					} else if (hwhd < 1 && hwhd > 0) {
						quireBody.put("outdoorActivities", "3");
					} else {
						quireBody.put("outdoorActivities", "4");
					}
				}
				if (body.get("kylgbz") != null
						&& !"".equals(body.get("kylgbz"))) {
					quireBody.put("ricketsSymptom", body.get("kylgbz"));
				}
				if (body.get("fywss") != null && !"".equals(body.get("fywss"))) {
					quireBody.put("vitaminADFlage", "1");
					quireBody.put("vitaminAD", body.get("fywss"));
				}
				if (body.get("hbqk") != null && !"".equals(body.get("hbqk"))) {
					String hbqk = body.get("hbqk") + "";
					if ("2".equals(hbqk)) {
						quireBody.put("illness", "1");
					}
				}
				if (body.get("kyglbtz") != null
						&& !"".equals(body.get("kyglbtz"))) {
					String kyglbtz = body.get("kyglbtz") + "";
					if ("1".equals(checkupStage) || "3".equals(checkupStage)) {
						if ("1".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "01");
						} else if ("2".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "02");
						} else if ("3".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "03");
						} else if ("4".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "04");
						}
					} else if ("6".equals(checkupStage)
							|| "9".equals(checkupStage)) {
						if ("1".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "05");
						} else if ("2".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "06");
						} else if ("3".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "07");
						} else if ("4".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "08");
						} else if ("5".equals(kyglbtz)) {
							quireBody.put("ricketsSign", "09");
						}
					} else {
						quireBody.put("ricketsSign", kyglbtz);
					}

				}
				Map<String, Object> plan = ccm.getVisitPlan(checkupStage,
						body.get("phrId") + "");
				if (plan.get("visitId") != null) {
					quireBody.put("inquireId", plan.get("visitId") + "");
				}
				Map<String, Object> Inquire = ccm.saveInquireRecord(quireBody,
						op);
				String inquireId = null;
				if (op.equalsIgnoreCase("create")) {
					inquireId = (String) Inquire.get("inquireId");
				} else {
					inquireId = (String) quireBody.get("inquireId");
				}
				vLogService.saveVindicateLog(CDH_Inquire, op, inquireId, dao);
				plan.put("visitId", inquireId);
				plan.put("planStatus", PlanStatus.VISITED);
				plan.put("visitDate", body.get("checkupDate"));
				try {
					VisitPlanModel vpm = new VisitPlanModel(dao);
					vpm.saveVisitPlan("update", plan);
				} catch (Exception e) {
					logger.error(
							"update children inquire plan  record error .", e);
					throw new ServiceException(e);
				}
			}

			boolean isWeek = false;
			StringBuilder sb = new StringBuilder();
			if (devEvaluat != null && !devEvaluat.equals("")) {
				if (devEvaluat.indexOf(DevEvaluation.LBW) > -1) {
					isWeek = true;
					sb.append(DebilityDiseaseType.LBW);
					sb.append(",");
				}

				if (devEvaluat.indexOf(DevEvaluation.STUNT) > -1) {
					isWeek = true;
					sb.append(DebilityDiseaseType.STUNT);
					sb.append(",");
				}
			}
			Object hgb = body.get("hgb");
			Object hgbN = SchemaUtil.getValue(schema, "hgb", hgb);
			Double hgbNum = hgbN == null ? null : (Double) hgbN;
			if (hgbNum != null && hgbNum < 110) {
				isWeek = true;
				sb.append(DebilityDiseaseType.ANEMIA);
				sb.append(",");
			}
			if (!isWeek) {
				return;
			}
			if (sb.length() < 1) {
				return;
			}
			res.put("isWeek", isWeek);
			String debilityType = sb.substring(0, sb.length() - 1);
			Map<String, Object> debilityMap = new HashMap<String, Object>();
			debilityMap.put("debilityReason", debilityType);
			DebilityChildrenModel dcm = new DebilityChildrenModel(dao);
			int count = dcm.getNoCloseRecordCount(empiId);
			res.put("recordCount", count);
			Map<String, Object> debilitys = SchemaUtil
					.setDictionaryMessageForForm(debilityMap,
							CDH_DebilityChildren);
			res.putAll(debilitys);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to save children check up record.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存儿童体格检查中医辨体信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCheckupDescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		try {
			ccm.saveCheckupDescription(body, op, res);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to save  children check up description record.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) body.get("recordId");
		Map<String, Object> resBody = (Map<String, Object>) res.get("body");
		if ("create".equals(op)) {
			recordId = (String) resBody.get("recordId");
		}
		vLogService.saveVindicateLog(CDH_ChildrenCheckupDescription, op,
				recordId, dao);
	}

	/**
	 * 保存儿童体格检查健康教育信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveCheckupHealthTeach(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		try {
			ccm.saveCheckupHealthTeach(body, op, res);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to save  children check up description record.", e);
			throw new ServiceException(e);
		}
		String recordId = (String) body.get("recordId");
		if ("create".equals(op)) {
			Map<String, Object> resBody = (Map<String, Object>) res.get("body");
			recordId = (String) resBody.get("recordId");
		}
		vLogService.saveVindicateLog(CDH_CheckupHealthTeach, op, recordId, dao);
	}

	/**
	 * 获取儿童体格检查中医辨体信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetCheckupDescription(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ValidateException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String checkupId = (String) body.get("checkupId");
		String checkupType = (String) body.get("checkupType");
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		try {
			Map<String, Object> data = ccm.getCheckupDescription(checkupType,
					checkupId);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(data,
					CDH_ChildrenCheckupDescription));
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to get  children check up description record.", e);
		}
	}

	/**
	 * 获取儿童体格检查健康教育信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetCheckupHealthTeach(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String checkupId = (String) body.get("checkupId");
		String checkupType = (String) body.get("checkupType");
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		try {
			Map<String, Object> data = ccm.getCheckupHealthTeach(checkupType,
					checkupId);
			res.put("body", SchemaUtil.setDictionaryMessageForForm(data,
					CDH_CheckupHealthTeach));
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to get  children check up health teach record.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取月龄
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetCheckupMonths(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(StringUtils.trimToEmpty((String) req
				.get("checkupDate")));
		Date birthDay = sdf.parse(StringUtils.trimToEmpty((String) req
				.get("birthDay")));
		int month = this.getMonths(date, birthDay);
		Map<String, Object> body = (HashMap<String, Object>) res.get("body");
		if (body == null) {
			body = new HashMap<String, Object>();
		}
		body.put("month", month);
		res.put("body", body);
	}

	/**
	 * 获取儿童出生月龄
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ParseException
	 * @throws ServiceException
	 */
	protected void executeGetMonths(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ParseException, ServiceException {
		EmpiModel em = new EmpiModel(dao);
		String phrId = StringUtils.trimToEmpty((String) req.get("phrId"));
		Map<String, Object> record;
		try {
			record = em.getSexAndBirthdayByPhrId(phrId);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save children check up record.", e);
			throw new ServiceException(e);
		}
		Date birthDay = (Date) record.get("birthday");
		int month = this.getMonths(new Date(), birthDay);
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("month", month);
		res.put("body", resBody);
	}

	/**
	 * 获取儿童下次体检预约日期
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doGetNextCheckupDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		String beginDate = StringUtils.trimToEmpty((String) reqBody
				.get("beginDate"));
		String phrId = StringUtils.trimToEmpty((String) reqBody.get("phrId"));
		Date nextChekcupDate;
		try {
			nextChekcupDate = vpm.getNextPlanBeginDate(phrId, beginDate,
					BusinessType.CD_CU);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get  children next check up date.", e);
			throw new ServiceException(e);
		}
		HashMap<String, Object> resBody = new HashMap<String, Object>();
		resBody.put("nextChekcupDate", nextChekcupDate);
		res.put("body", resBody);

	}

	/**
	 * 获取月龄
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private int getMonths(Date date1, Date date2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH);
		calendar.setTime(date2);
		int yy = calendar.get(Calendar.YEAR);
		int mm = calendar.get(Calendar.MONTH);
		int month = Math.abs((y - yy) * 12 + (m - mm));
		return month;
	}

	/**
	 * 获取儿童健康指导信息
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-12 下午3:54:21
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doGetHealthGuidance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String checkupId = (String) body.get("checkupId");
		String checkupType = (String) body.get("checkupType");
		String checkupStage = ((Integer) body.get("checkupStage")).toString();
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		Map<String, Object> data = null;
		try {
			data = ccm.getHealthGuidance(checkupType, checkupId);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to get children check up health guidance record.",
					e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		boolean giSelect = false;
		if (data != null && data.size() > 0) {
			byte[] rcByte = (byte[]) data.get("guidingIdea");
			try {
				String guidingIdea = InputStreamUtils.byteTOString(rcByte);
				data.put("guidingIdea", guidingIdea);
			} catch (Exception e) {
				logger.error("byte[] transition String failed.", e);
				throw new ServiceException(e);
			}
			resBodyMap.putAll(SchemaUtil.setDictionaryMessageForForm(data,
					CDH_HealthGuidance));
		} else {
			List<Map<String, Object>> rsList = null;
			try {
				rsList = ccm.getDictCorrection(checkupStage);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get children correction record.", e);
				throw new ServiceException(e);
			}
			if (rsList != null && rsList.size() > 0) {
				if (rsList.size() == 1) {
					Map<String, Object> dcMap = rsList.get(0);
					resBodyMap.put("guidingIdea", dcMap.get("suggestion"));
				} else {
					giSelect = true;
					resBodyMap.put("giList", SchemaUtil
							.setDictionaryMessageForList(rsList,
									CDH_DictCorrection));
				}
			}
		}
		resBodyMap.put("giSelect", giSelect);
		res.put("body", resBodyMap);
	}

	/**
	 * 保存儿童健康指导信息
	 * 
	 * @Description:
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-12 下午4:07:50
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveHealthGuidance(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String guidingIdea = StringUtils.trimToEmpty((String) body
				.get("guidingIdea"));
		try {
			byte[] rc = InputStreamUtils.InputStreamTOByte(InputStreamUtils
					.StringTOInputStream(guidingIdea));
			body.put("guidingIdea", rc);
		} catch (IOException e) {
			logger.error("Failed to InputStream transition to byte.", e);
			throw new ServiceException(
					Constants.CODE_INPUTSTREAM_TO_BYTE_ERROR,
					"将InputStream转换成byte数组失败！", e);
		} catch (Exception e) {
			logger.error("Failed to String transition to InputStream.", e);
			throw new ServiceException(
					Constants.CODE_STRING_TO_INPUTSTREAM_ERROR,
					"将String转换成InputStream失败！", e);
		}
		ChildrenCheckupModel ccm = new ChildrenCheckupModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = ccm.saveHealthGuidance(op, body, true);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Failed to save  children check up description record.", e);
			throw new ServiceException(e);
		}
		String hgId = (String) body.get("hgId");
		if ("create".equals(op)) {
			hgId = (String) rsMap.get("hgId");
		}
		vLogService.saveVindicateLog(CDH_HealthGuidance, op, hgId, dao);
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		if (rsMap != null && rsMap.size() > 0) {
			resBodyMap.putAll(rsMap);
		}
		res.put("body", resBodyMap);
	}
}
