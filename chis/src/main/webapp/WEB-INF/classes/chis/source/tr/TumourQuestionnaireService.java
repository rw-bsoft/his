/**
 * @(#)TumourQuestionnaireService.java Created on 2014-6-18 上午10:02:49
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.controller.exception.ControllerException;
import ctd.dictionary.Dictionary;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.S;
import ctd.util.context.Context;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.CancellationReason;
import chis.source.dic.Gender;
import chis.source.empi.EmpiModel;
import chis.source.mpm.MasterplateMaintainModel;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourQuestionnaireService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(TumourQuestionnaireService.class);

	public static String hrt2mt(String highRiskType) {
		Map<String, String> hrt2mt = new HashMap<String, String>();
		hrt2mt.put("1", "03");
		hrt2mt.put("2", "04");
		hrt2mt.put("3", "05");
		hrt2mt.put("4", "06");
		hrt2mt.put("5", "07");
		hrt2mt.put("6", "08");
		return hrt2mt.get(highRiskType);
	}

	public static String mt2hrt(String masterplateType) {
		Map<String, String> mt2hrt = new HashMap<String, String>();
		mt2hrt.put("03", "1");
		mt2hrt.put("04", "2");
		mt2hrt.put("05", "3");
		mt2hrt.put("06", "4");
		mt2hrt.put("07", "5");
		mt2hrt.put("08", "6");
		return mt2hrt.get(masterplateType);
	}

	/**
	 * 
	 * @Description:获取问卷试题
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-18 上午10:37:11
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetQuestionnaireMasterplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String masterplateId = (String) reqBodyMap.get("masterplateId");
		String gcId = (String) reqBodyMap.get("gcId");
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		MasterplateMaintainModel mmModel = new MasterplateMaintainModel(dao);
		Map<String, Object> mmMap = null;
		try {
			mmMap = mmModel.getMasterplateMaintain(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("Get questionnaire masterplate record failure.", e);
			throw new ServiceException(e);
		}
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		String jr = "";
		try {
			jr = tqModel.getTHQJudgementResult(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("Get questionnaire judgement result failure.", e);
			throw new ServiceException(e);
		}
		if (mmMap != null && mmMap.size() > 0) {
			Map<String, Object> titleMap = new HashMap<String, Object>();
			titleMap.put("fieldId", "TT_" + masterplateId);
			// titleMap.put("alias", mmMap.get("masterplateName"));
			titleMap.put("alias", "");
			titleMap.put("id", mmMap.get("whmb") + "_title");
			titleMap.put("type", "string");
			titleMap.put("dicRender", "0");
			titleMap.put("xtype", "title");
			titleMap.put("display", 2);
			titleMap.put("acValue", 1);
			titleMap.put("width", 765);
			titleMap.put("height", 30);
			titleMap.put("judgementResult", "判定结果： " + jr);
			itemList.add(titleMap);
		}
		List<Map<String, Object>> rsList = null;
		try {
			rsList = tqModel.getQuestionnaireByMasterplateId(masterplateId);
		} catch (ModelDataOperationException e) {
			logger.error("Get questionnaire masterplate failure.", e);
			throw new ServiceException(e);
		}
		if (rsList != null && rsList.size() > 0) {
			List<Map<String, Object>> filelds = null;
			for (Map<String, Object> fieldAttribute : rsList) {
				String dicRender = (String) fieldAttribute.get("dicRender");
				fieldAttribute.put("display", 2);
				fieldAttribute.put("acValue", 1);
				if ("0".equals(dicRender)) {
					itemList.add(fieldAttribute);
				} else if ("1".equals(dicRender)) {// 单选
					filelds = tqModel.getListFilelds(fieldAttribute, dao);
					List<Map<String, Object>> items = TumourQuestionnaireModel
							.resetDicItems(filelds, dicRender);
					Map<String, Object> dic = new HashMap<String, Object>();
					dic.put("id",
							"chis.application.tr.schemas.PHQ_TumourQuestionnaire."
									+ fieldAttribute.get("id"));
					dic.put("items", items);
					dic.put("render", "Radio");
					dic.put("colWidth", 70);
					dic.put("columns", 3);
					fieldAttribute.put("dic", dic);
					itemList.add(fieldAttribute);
				} else if ("2".equals(dicRender)) {// 多选
					filelds = tqModel.getListFilelds(fieldAttribute, dao);
					List<Map<String, Object>> items = TumourQuestionnaireModel
							.resetDicItems(filelds, dicRender);
					Map<String, Object> dic = new HashMap<String, Object>();
					dic.put("id",
							"chis.application.tr.schemas.PHQ_TumourQuestionnaire."
									+ fieldAttribute.get("id"));
					dic.put("items", items);
					dic.put("render", "Checkbox");
					dic.put("colWidth", 70);
					dic.put("columns", 3);
					fieldAttribute.put("dic", dic);
					itemList.add(fieldAttribute);
				} else if ("3".equals(dicRender)) {
					fieldAttribute.put("xtype", "label");
					itemList.add(fieldAttribute);
				}
			}
		}
		// System.out.println("#####@@@:=="+itemList.toString());
		res.put("body", itemList);
	}

	/**
	 * 
	 * @Description:初始化问卷
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-19 上午10:39:16
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLoadTumourQuestionnaireData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String gcId = (String) reqBodyMap.get("gcId");
		String empiId = (String) reqBodyMap.get("empiId");
		String masterplateId = (String) reqBodyMap.get("masterplateId");
		String source = (String) reqBodyMap.get("source");
		Map<String, Object> resBody = new HashMap<String, Object>();
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrModel.getHealthRecordListByEmpiId(empiId);
		} catch (ModelDataOperationException e1) {
			logger.error("Get health record by empiId failure.", e1);
			throw new ServiceException(e1);
		}
		boolean update = true;
		if (hrMap != null && hrMap.size() > 0) {
			String status = (String) hrMap.get("status");
			if (Constants.CODE_STATUS_WRITE_OFF.equals(status)) {
				update = false;
			}
		}
		resBody.put("update", update);
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		if (StringUtils.isNotEmpty(gcId)) {
			Map<String, Object> gcMap = null;
			try {
				gcMap = tqModel.getGeneralCasebyPkey(gcId);
			} catch (ModelDataOperationException e) {
				logger.error("Get PHQ_GeneralCase record by Pkey[" + gcId
						+ "] failure.", e);
				throw new ServiceException(e);
			}
			gcMap = SchemaUtil.setDictionaryMessageForForm(gcMap,
					PHQ_GeneralCase);
			resBody.put(PHQ_GeneralCase + Constants.DATAFORMAT4FORM, gcMap);
			List<Map<String, Object>> arList = null;
			try {
				arList = tqModel.getAnswerRecordByGCID(gcId);
			} catch (ModelDataOperationException e) {
				logger.error("Get tumour health questions answer by gcId["
						+ gcId + "] failure.", e);
				throw new ServiceException(e);
			}
			Map<String, Object> arMap = new HashMap<String, Object>();
			for (Map<String, Object> map : arList) {
				String fieldName = (String) map.get("FIELDNAME");
				String fieldValue = (String) map.get("FIELDVALUE");
				String text = (String) map.get("TEXT");
				if (StringUtils.isNotEmpty(text)) {
					Map<String, Object> dicMap = new HashMap<String, Object>();
					dicMap.put("key", fieldValue);
					dicMap.put("text", text);
					arMap.put(fieldName, dicMap);
				} else {
					arMap.put(fieldName, fieldValue);
				}
			}
			resBody.put("THQ" + Constants.DATAFORMAT4FORM, arMap);
		} else if (StringUtils.isNotEmpty(empiId)) {
			EmpiModel eModel = new EmpiModel(dao);
			Map<String, Object> eMap = null;
			try {
				eMap = eModel.getEmpiInfoByEmpiid(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get demographic info by empiId[" + empiId
						+ "] failure.", e);
				throw new ServiceException(e);
			}
			Map<String, Object> gcMap = new HashMap<String, Object>();
			if (eMap != null) {
				gcMap.put("empiId", empiId);
				gcMap.put("personName", eMap.get("personName"));
				gcMap.put("idCard", eMap.get("idCard"));
				gcMap.put("sexCode", eMap.get("sexCode"));
				gcMap.put("birthday", eMap.get("birthday"));
				gcMap.put("mobileNumber", eMap.get("mobileNumber"));
				gcMap.put("residentialAddress", eMap.get("address"));
				if (eMap.get("educationCode") != null) {
					String educationCode = (String) eMap.get("educationCode");
					String ec = "";
					int ecd = Integer.parseInt(educationCode);
					if (ecd >= 80) {
						ec = "01";
					} else if (ecd >= 70 && ecd < 80) {
						ec = "02";
					} else if (ecd >= 40 && ecd < 70) {
						ec = "03";
					} else if (ecd >= 30 && ecd < 40) {
						ec = "04";
					} else if (ecd < 30) {
						ec = "05";
					}
					gcMap.put("educationCode", ec);
				}
				String type = "";
				try {
					type = tqModel.getQuestionnaireType(masterplateId);
				} catch (ModelDataOperationException e) {
					logger.error("Get questionnaire type failure.", e);
					throw new ServiceException(e);
				}
				gcMap.put("highRiskType", type);
				gcMap.put("source", source);
				gcMap = SchemaUtil.setDictionaryMessageForForm(gcMap,
						PHQ_GeneralCase);
				resBody.put(PHQ_GeneralCase + Constants.DATAFORMAT4FORM, gcMap);
			}
		}
		res.put("body", resBody);
	}

	/**
	 * 
	 * @Description:保存肿瘤问卷数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-8-1 下午3:28:09
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourQuestionnaireData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> baseData = (Map<String, Object>) reqBodyMap
				.get("baseData");
		Map<String, Object> thqData = (Map<String, Object>) reqBodyMap
				.get("thqData");
		String empiId = (String) reqBodyMap.get("empiId");
		String masterplateId = (String) reqBodyMap.get("masterplateId");
		baseData.put("empiId", empiId);
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		String phrId = "";
		try {
			phrId = hrModel.getPhrId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get phrId by empiId failure.", e);
			throw new ServiceException(e);
		}
		baseData.put("phrId", phrId);
		String op = "create";
		String gcId = (String) baseData.get("gcId");
		String source = (String) baseData.get("source");
		if (StringUtils.isNotEmpty(gcId)) {
			op = "update";
		}
		dao.beginTransaction();
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		Map<String, Object> rsbMap = null;
		try {
			rsbMap = tqModel.saveGeneralCaseRecord(op, baseData, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save redord of PHQ_GeneralCase failure.", e);
			dao.rollbackTransaction();
			throw new ServiceException(e);
		}
		if ("create".equals(op)) {
			if (rsbMap != null) {
				gcId = (String) rsbMap.get("gcId");
			}
		}
		try {
			tqModel.deleteAnswerRecord(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("Delete redord of PHQ_AnswerRecord by gcId[" + gcId
					+ "] failure.", e);
			dao.rollbackTransaction();
			throw new ServiceException(e);
		}
		for (String key : thqData.keySet()) {
			Map<String, Object> arMap = new HashMap<String, Object>();
			arMap.put("empiId", empiId);
			arMap.put("gcId", gcId);
			arMap.put("masterplateId", masterplateId);
			arMap.put("fieldId", key.subSequence(0, key.indexOf(".")));
			arMap.put("fieldName",
					key.subSequence(key.indexOf(".") + 1, key.length()));
			arMap.put("fieldValue", thqData.get(key));
			try {
				tqModel.saveAnswerRecord("create", arMap, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save redord of PHQ_AnswerRecord failure.", e);
				dao.rollbackTransaction();
				throw new ServiceException(e);
			}
		}
		dao.commitTransaction();
		dao.beginTransaction();
		// 判断问卷是否与标准匹配，返回判断是否要进入初筛标识
		boolean passport = false;
		try {
			passport = tqModel.isAccordWithCriterion(empiId, masterplateId,
					gcId, source,null);
		} catch (ModelDataOperationException e) {
			logger.error(
					"Judge tumour questionnaire answer accord with the tumour questionnaire criterion failure.",
					e);
			dao.rollbackTransaction();
			throw new ServiceException(e);
		}
		if (rsbMap == null) {
			rsbMap = new HashMap<String, Object>();
		}
		rsbMap.put("passport", passport);
		// 增加初筛记录
		if (passport) {
			String userId = UserUtil.get(UserUtil.USER_ID);
			String utilId = UserUtil.get(UserUtil.MANAUNIT_ID);
			Calendar calendar = Calendar.getInstance();
			Date tDate = calendar.getTime();
			Map<String, Object> tsMap = new HashMap<String, Object>();
			tsMap.put("empiId", empiId);
			tsMap.put("phrId", phrId);
			String masterplateType = baseData.get("masterplateType") + "";
			int mt = Integer.parseInt(masterplateType);
			String highRiskType = (mt - 2) + "";
			tsMap.put("highRiskType", highRiskType);
			rsbMap.put("itemType", highRiskType);
			tsMap.put("TQDate", tDate);
			tsMap.put("screeningDate", tDate);
			tsMap.put("year", calendar.get(Calendar.YEAR));
			tsMap.put("screeningDoctor", userId);
			String highRiskSource = "2";
			String JZXH = baseData.get("JZXH") + "";
			if (StringUtils.isNotEmpty(JZXH)) {
				highRiskSource = "1";
			}
			tsMap.put("highRiskSource", highRiskSource);
			tsMap.put("nature", "1");
			tsMap.put("criterionMark", "y");
			// 检查高危因素
			String highRiskFactor = "";
			try {
				highRiskFactor = tqModel.getHighRiskFactor(empiId,
						highRiskType, highRiskSource);
			} catch (ModelDataOperationException e1) {
				logger.error("Get high risk factor of turmour failure.", e1);
				dao.rollbackTransaction();
				throw new ServiceException(e1);
			}
			if (StringUtils.isNotEmpty(highRiskFactor)) {
				tsMap.put("highRiskMark", "y");
			} else {
				tsMap.put("highRiskMark", "n");
			}
			String tqpv = "0";
			try {
				tqpv = tqModel.getQuestionnairePositive(gcId);
			} catch (ModelDataOperationException e1) {
				logger.error(
						"Get questionnaire positive value of turmour failure.",
						e1);
				throw new ServiceException(e1);
			}
			tsMap.put("questionnairePositive", tqpv);// 问卷阳性
			tsMap.put("checkPositive", "0");// 检查阳性
			String tspv = "0";
			if ("1".equals(tqpv)) {
				tspv = "1";
			}
			tsMap.put("syntheticalPositive", tspv);// 综合阳性
			tsMap.put("highRiskFactor", highRiskFactor);
			tsMap.put("status", "0");
			tsMap.put("screeningDoctor", baseData.get("surveyUser"));
			tsMap.put("createUser", userId);
			tsMap.put("createUnit", utilId);
			tsMap.put("createDate", tDate);
			tsMap.put("lastModifyUnit", utilId);
			tsMap.put("lastModifyUser", userId);
			tsMap.put("lastModifyDate", tDate);
			TumourScreeningModel tsModel = new TumourScreeningModel(dao);
			Map<String, Object> rsMap = null;
			try {
				rsMap = tsModel.saveTumourScreening("create", tsMap, true);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save tumour screening data.", e);
				dao.rollbackTransaction();
				throw new ServiceException(e);
			}
			dao.commitTransaction();
			String screeningId = "";
			if (rsMap != null) {
				screeningId = rsMap.get("recordId") + "";
			}
			rsbMap.put("screeningId", screeningId);
		}
		res.put("body", rsbMap);
	}

	/**
	 * 
	 * @Description:保存多模板同进问卷的数据
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-1-16 下午4:22:30
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveTumourQuestionnaireManyData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> baseData = (Map<String, Object>) reqBodyMap
				.get("baseData");
		Map<String, Object> thqManyData = (Map<String, Object>) reqBodyMap
				.get("thqManyData");
		Map<String, Object> mtidMap = (Map<String, Object>) reqBodyMap
				.get("mtidMap");
		Map<String, Object> rsbMap = new HashMap<String, Object>();
		boolean hasTS = false;
		StringBuffer itemType = new StringBuffer();
		String hrtStr = (String) baseData.get("highRiskType");
		String[] hrts = hrtStr.split(",");
		for (int i = 0, len = hrts.length; i < len; i++) {
			String highRiskType = hrts[i];
			baseData.put("highRiskType", highRiskType);
			String masterplateType = hrt2mt(highRiskType);
			baseData.put("masterplateType", masterplateType);
			String masterplateId = (String) mtidMap.get(masterplateType);
			baseData.put("masterplateId", masterplateId);
			String empiId = (String) reqBodyMap.get("empiId");
			baseData.put("empiId", empiId);
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			String phrId = "";
			try {
				phrId = hrModel.getPhrId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get phrId by empiId failure.", e);
				throw new ServiceException(e);
			}
			baseData.put("phrId", phrId);
			String op = "create";
			String gcId = (String) baseData.get("gcId");
			if (StringUtils.isNotEmpty(gcId)) {
				op = "update";
			}
			dao.beginTransaction();
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			Map<String, Object> rsMap = null;
			try {
				rsMap = tqModel.saveGeneralCaseRecord(op, baseData, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save redord of PHQ_GeneralCase failure.", e);
				throw new ServiceException(e);
			}
			if ("create".equals(op)) {
				if (rsMap != null) {
					gcId = (String) rsMap.get("gcId");
				}
			}
			if(S.isNotEmpty(gcId)){
				try {
					tqModel.deleteAnswerRecord(gcId);
				} catch (ModelDataOperationException e) {
					logger.error("Delete redord of PHQ_AnswerRecord by gcId["
							+ gcId + "] failure.", e);
					throw new ServiceException(e);
				}
			}
			Map<String, Object> thqData = (Map<String, Object>) thqManyData
					.get(highRiskType);
			for (String key : thqData.keySet()) {
				Map<String, Object> arMap = new HashMap<String, Object>();
				arMap.put("empiId", empiId);
				arMap.put("gcId", gcId);
				arMap.put("masterplateId", masterplateId);
				arMap.put("fieldId", key.subSequence(0, key.indexOf(".")));
				arMap.put("fieldName",
						key.subSequence(key.indexOf(".") + 1, key.length()));
				arMap.put("fieldValue", thqData.get(key));
				try {
					tqModel.saveAnswerRecord("create", arMap, true);
				} catch (ModelDataOperationException e) {
					logger.error("Save redord of PHQ_AnswerRecord failure.", e);
					throw new ServiceException(e);
				}
			}
			dao.commitTransaction();
			dao.beginTransaction();
			// 判断问卷是否与标准匹配，返回判断是否要进入初筛标识
			boolean passport = false;
			try {
				String source = (String) baseData.get("source");
				passport = tqModel.isAccordWithCriterion(empiId, masterplateId,
						gcId, source, null);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Judge tumour questionnaire answer accord with the tumour questionnaire criterion failure.",
						e);
				throw new ServiceException(e);
			}
			if (rsbMap == null) {
				rsbMap = new HashMap<String, Object>();
			}
			// 增加初筛记录
			if (passport) {
				hasTS = true;
				itemType.append(highRiskType);
				if (i < len - 1) {
					itemType.append(",");
				}
				String userId = UserUtil.get(UserUtil.USER_ID);
				String utilId = UserUtil.get(UserUtil.MANAUNIT_ID);
				Calendar calendar = Calendar.getInstance();
				Date tDate = calendar.getTime();
				Map<String, Object> tsMap = new HashMap<String, Object>();
				tsMap.put("empiId", empiId);
				tsMap.put("phrId", phrId);
				tsMap.put("highRiskType", highRiskType);
				tsMap.put("TQDate", tDate);
				tsMap.put("screeningDate", tDate);
				tsMap.put("year", calendar.get(Calendar.YEAR));
				tsMap.put("screeningDoctor", userId);
				String highRiskSource = "2";
				String JZXH = S.trimToEmpty((String) baseData.get("JZXH"));
				if (StringUtils.isNotEmpty(JZXH)) {
					highRiskSource = "1";
				}
				tsMap.put("highRiskSource", highRiskSource);
				tsMap.put("nature", "1");
				tsMap.put("criterionMark", "y");
				tsMap.put("isTrace", "0");// 是否追踪
				tsMap.put("traceNorm", "0");// 追踪规范
				// 检查高危因素
				String highRiskFactor = "";
				try {
					highRiskFactor = tqModel.getHighRiskFactor(empiId,
							highRiskType, highRiskSource);
				} catch (ModelDataOperationException e1) {
					logger.error("Get high risk factor of turmour failure.", e1);
					throw new ServiceException(e1);
				}
				if (StringUtils.isNotEmpty(highRiskFactor)) {
					tsMap.put("highRiskMark", "y");
				} else {
					tsMap.put("highRiskMark", "n");
				}
				String tqpv = "0";
				try {
					tqpv = tqModel.getQuestionnairePositive(gcId);
				} catch (ModelDataOperationException e1) {
					logger.error(
							"Get questionnaire positive value of turmour failure.",
							e1);
					throw new ServiceException(e1);
				}
				tsMap.put("questionnairePositive", tqpv);// 问卷阳性
				tsMap.put("checkPositive", "0");// 检查阳性
				String tspv = "0";
				if ("1".equals(tqpv)) {
					tspv = "1";
				}
				tsMap.put("syntheticalPositive", tspv);// 综合阳性
				tsMap.put("highRiskFactor", highRiskFactor);
				tsMap.put("status", "0");
				tsMap.put("screeningDoctor", baseData.get("surveyUser"));
				tsMap.put("createUser", userId);
				tsMap.put("createUnit", utilId);
				tsMap.put("createDate", tDate);
				tsMap.put("lastModifyUnit", utilId);
				tsMap.put("lastModifyUser", userId);
				tsMap.put("lastModifyDate", tDate);
				TumourScreeningModel tsModel = new TumourScreeningModel(dao);
				Map<String, Object> rsTSMap = null;
				try {
					rsTSMap = tsModel.saveTumourScreening("create", tsMap, true);
				} catch (ModelDataOperationException e) {
					logger.error("Failed to save tumour screening data.", e);
					throw new ServiceException(e);
				}
				dao.commitTransaction();
				String screeningId = "";
				if (rsTSMap != null) {
					screeningId = rsTSMap.get("recordId") + "";
				}
				rsbMap.put("screeningId", screeningId);
			}
		}
		rsbMap.put("hasTS", hasTS);
		rsbMap.put("itemType", itemType.toString());
		res.put("body", rsbMap);
	}

	/**
	 * 
	 * @Description:获取健康档案状态
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-12-18 下午5:25:40
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doIsDelete(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrModel.getHealthRecordListByEmpiId(empiId);
		} catch (ModelDataOperationException e1) {
			logger.error("Get health record by empiId failure.", e1);
			throw new ServiceException(e1);
		}
		boolean isDelete = true;
		if (hrMap != null && hrMap.size() > 0) {
			String status = (String) hrMap.get("status");
			if (Constants.CODE_STATUS_WRITE_OFF.equals(status)) {
				isDelete = false;
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("isDelete", isDelete);
	}

	/**
	 * 
	 * @Description:删除肿瘤问卷记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-8-5 下午1:48:00
	 * @Modify:
	 */
	public void doRemoveTumourQuestionnaireRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String gcId = (String) req.get("pkey");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		// 删除问卷答案记录
		try {
			tqModel.deleteAnswerRecord(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("delete redord of PHQ_AnswerRecord by gcId[" + gcId
					+ "] failure.", e);
			throw new ServiceException(e);
		}
		// 删除问卷基本信息记录
		try {
			tqModel.deleteGeneralCaseByPkey(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("Save redord of PHQ_GeneralCase by pkey[" + gcId
					+ "] failure.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:一键删除问卷记录和肿瘤初筛记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-1-23 上午9:37:21
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doOneKeyRemoveTQAndTS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String gcId = (String) reqBodyMap.get("gcId");
		String empiId = (String) reqBodyMap.get("empiId");
		String highRiskType = (String) reqBodyMap.get("highRiskType");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		// 删除问卷答案记录
		try {
			tqModel.deleteAnswerRecord(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("delete record of PHQ_AnswerRecord by gcId[" + gcId
					+ "] failure.", e);
			throw new ServiceException(e);
		}
		// 删除问卷基本信息记录
		try {
			tqModel.deleteGeneralCaseByPkey(gcId);
		} catch (ModelDataOperationException e) {
			logger.error("Save redord of PHQ_GeneralCase by pkey[" + gcId
					+ "] failure.", e);
			throw new ServiceException(e);
		}
		// 删除初筛
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		try {
			tsModel.deleteTumourScreening(empiId, highRiskType);
		} catch (ModelDataOperationException e) {
			logger.error("Delete record of MDC_TumourSeemingly by empiId["
					+ empiId + "] and highRiskType[" + highRiskType
					+ "] failure. ", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:获取肿瘤检查项目
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-8-21 上午8:48:24
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetTumourInspectionItems(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String itemType = (String) reqBodyMap.get("itemType");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		long tiiNum = 0;
		try {
			tiiNum = tqModel.getTumourInspectionItems(itemType);
		} catch (ModelDataOperationException e) {
			logger.error("Get tumour inspection items failure.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("tiiNum", tiiNum);
		res.put("body", resMap);
	}

	/**
	 * 
	 * @Description:保存建议检查项
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-8-21 下午5:36:29
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckItems(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		List<Map<String, Object>> checkItems = (List<Map<String, Object>>) reqBodyMap
				.get("checkItems");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		for (int i = 0; i < checkItems.size(); i++) {
			Map<String, Object> ciMap = checkItems.get(i);
			try {
				tqModel.saveCheckItem("create", ciMap, false);
			} catch (ModelDataOperationException e) {
				logger.error("Save check items of tumour screening failure.", e);
				throw new ServiceException(e);
			}
		}
	}

	/**
	 * 根据就诊序号和模版序号获取问卷数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryMasterplateKey(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// Map<String, Object> body = (Map<String, Object>) req.get("body");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		try {
			tqModel.queryMasterplateKey(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:注销问卷
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-10-17 上午9:39:51
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutTumourQuestionnaireRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String gcId = (String) reqBodyMap.get("gcId");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		try {
			tqModel.logoutTumourQuestionnaire(gcId, CancellationReason.OTHER);
		} catch (ModelDataOperationException e) {
			logger.error("Logout tumour questionnaire record by gcId[" + gcId
					+ "]", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:判断是否存在健康档案
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-11-4 上午10:51:21
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doExistHR(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		boolean existHR = false;
		try {
			existHR = tqModel.existHealthRecord(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Judge threalth record is existent failuer.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("existHR", existHR);
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:取该人某问卷的类型
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doGetTumourQuestionnaire(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		List<Map<String, String>> type = (List<Map<String, String>>) body
				.get("type");
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = type.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		type = newList;
		String typeStr = type.toString().substring(1,
				type.toString().length() - 1);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("empiId", empiId);
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct");
		sql.append(" chr(39)||masterplateType||chr(39)");
		sql.append(" from PHQ_GeneralCase phq_genera0_");
		sql.append(" where phq_genera0_.empiId =:empiId");
		sql.append(" and phq_genera0_.masterplateType in (").append(typeStr)
				.append(")");
		list = dao.doSqlQuery(sql.toString(), param);
		type.removeAll(list);
		res.put("body", type);
	}

	/**
	 * 
	 * @Description:加载问卷记录列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception
	 * @author ChenXianRui 2014-11-19 上午10:24:50
	 * @Modify:
	 */
	public void doLoadTHQRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = tqModel.loadTHQRecordsPage(req);
		} catch (ModelDataOperationException e) {
			logger.error(
					"paging and querying tumour health questionnaire records failure.",
					e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}

	/**
	 * 
	 * @Description:如果没有对应问卷，不弹出问卷模板界面
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws Exception
	 * @Modify:
	 */
	public void doCkTHQTemplate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> rsMap = null;
		try {
			TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
			rsMap = tqModel.doCkTHQTemplate(req);
		} catch (ModelDataOperationException e) {
			logger.error("doCkTHQTemplate failure.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}

	/**
	 * 
	 * @Description:获取最新全部肿瘤问卷模板
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-1-9 下午3:39:44
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doGetQuestionnaireMasterplateMany(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> mtMap = new HashMap<String, Object>();
		mtMap.put("03", "大肠");
		mtMap.put("04", "胃");
		mtMap.put("05", "肝");
		mtMap.put("06", "肺");
		mtMap.put("07", "乳腺");
		mtMap.put("08", "宫颈");
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String sexCode = (String) reqBodyMap.get("sexCode");
		List<String> masterplateTypes = (ArrayList<String>) reqBodyMap
				.get("masterplateTypes");
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		List<Map<String, Object>> lthqmList = null;
		try {
			lthqmList = tqModel.getLatestTHQMasterplate();
		} catch (ModelDataOperationException e) {
			logger.error(
					"Get lastest tumour health questionnaire masterplate failure.",
					e);
			throw new ServiceException(e);
		}
		List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
		Map<String, Object> itemMap = new HashMap<String, Object>();
		Map<String, Object> mtidMap = new HashMap<String, Object>();
		StringBuffer mtes = new StringBuffer();
		if (lthqmList != null && lthqmList.size() > 0) {
			int groupNum = 0;
			for (int i = 0, len = lthqmList.size(); i < len; i++) {
				Map<String, Object> lmMap = lthqmList.get(i);
				String whmb = (String) lmMap.get("WHMB");
				if (masterplateTypes.toString().indexOf(whmb) == -1) {
					continue;
				}
				String masterplateId = (String) lmMap.get("MASTERPLATEID");
				String masterplateName = (String) lmMap.get("MASTERPLATENAME");
				if ("08".equals(whmb) && !Gender.WOMEN.equals(sexCode)) {
					// 只有性别为“女”，才返回“肿瘤-宫颈"问卷模板
					continue;
				}
				String group = (String) mtMap.get(whmb);
				List<Map<String, Object>> iList = tqModel.packageItems(
						masterplateId, masterplateName, group);
				itemList.addAll(iList);
				itemMap.put(whmb, groupNum);
				groupNum += 1;
				mtidMap.put(whmb, masterplateId);
				mtes.append(whmb);
				if (i < len - 1) {
					mtes.append(",");
				}
			}
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("itemList", itemList);
		resBodyMap.put("mtiMap", itemMap);
		resBodyMap.put("mtidMap", mtidMap);
		resBodyMap.put("mtes", mtes.toString());
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:做肿瘤问卷前做些业务判断等
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2015-1-15 上午9:42:30
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doBeforAddTHQ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String type = (String) reqBodyMap.get("type");
		boolean addHEListener = (Boolean) reqBodyMap.get("addHEListener");
		String courseId = (String) reqBodyMap.get("courseId");
		if (addHEListener) {
			TumourHealthEducationCourseModel thecModel = new TumourHealthEducationCourseModel(
					dao);
			boolean isExist = false;
			try {
				isExist = thecModel.isExistListener(empiId, courseId);
			} catch (ModelDataOperationException e) {
				logger.error(
						"Judge whether exist the listener in the course failure.",
						e);
				throw new ServiceException(e);
			}
			if (!isExist) {
				try {
					thecModel.saveListtener("create", reqBodyMap, true);
				} catch (ModelDataOperationException e) {
					logger.error("Save record of PHQ_AttendPersonnel failure.",
							e);
					throw new ServiceException(e);
				}
				// 更新参加人数
				try {
					thecModel.updateNumberOfParticipants(courseId);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Update numberOfParticipants of PHQ_HealthEducationCourse failure.",
							e);
					throw new ServiceException(e);
				}
			}
		}
		TumourQuestionnaireModel tqModel = new TumourQuestionnaireModel(dao);
		// 判断是否有健康档案
		boolean existHR = false;
		try {
			existHR = tqModel.existHealthRecord(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Judge threalth record is existent failuer.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("existHR", existHR);
		TumourScreeningModel tsModel = new TumourScreeningModel(dao);
		TumourHighRiskModel thrModel = new TumourHighRiskModel(dao);
		TumourConfirmedModel tcModel = new TumourConfirmedModel(dao);
		if (existHR) {
			Dictionary hrtDic = null;
			try {
				hrtDic = DictionaryController.instance().get(
						"chis.dictionary.tumourHighRiskType");
			} catch (ControllerException e1) {
				e1.printStackTrace();
			}
			if (hrtDic == null) {
				throw new ServiceException(Constants.CODE_NOT_FOUND,
						"肿瘤高危类别字典加载失败！");
			}
			List<Map<String, Object>> lthqmList = null;
			try {
				lthqmList = tqModel.getLatestTHQMasterplate();
			} catch (ModelDataOperationException e) {
				logger.error(
						"Get lastest tumour health questionnaire masterplate failure.",
						e);
				throw new ServiceException(e);
			}
			StringBuffer latestHT = new StringBuffer();
			if (lthqmList != null && lthqmList.size() > 0) {
				for (int i = 0, len = lthqmList.size(); i < len; i++) {
					Map<String, Object> lmMap = lthqmList.get(i);
					String whmb = (String) lmMap.get("WHMB");
					latestHT.append(mt2hrt(whmb));
					if (i < len - 1) {
						latestHT.append(",");
					}
				}
			}
			String[] types = type.split(",");
			List<String> noTRtypes = new ArrayList<String>();
			Map<String, String> hasTRtypes = new HashMap<String, String>();
			for (int i = 0, len = types.length; i < len; i++) {
				String ht = types[i];
				String dicText = hrtDic.getText(ht);
				boolean isExist = false;
				// 1年内1个问卷只能有一条记录
				try {
					isExist = tqModel.existTHQatThisYear(empiId, ht);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Judge whether exist tumour questionnaire failure.",
							e);
					throw new ServiceException(e);
				}
				if (isExist) {
					hasTRtypes.put(ht, "该病人已经存在本年度" + dicText + "问卷，不用再做"
							+ dicText + "问卷。");
					continue;
				} else {
					// 是否已经是初筛
					try {
						isExist = tsModel.isTumourScreening(empiId, ht);
					} catch (ModelDataOperationException e) {
						logger.error(
								"Judge whether exist tumour screening record failure.",
								e);
						throw new ServiceException(e);
					}
					if (isExist) {
						hasTRtypes.put(ht, "该病人已进本年度" + dicText + "初筛人群管理，不用再做"
								+ dicText + "问卷。");
						continue;
					} else {
						try {
							isExist = thrModel.isExistTHR(empiId, ht);
						} catch (ModelDataOperationException e) {
							logger.error(
									"Judge whether exist tumour high risk record failure.",
									e);
							throw new ServiceException(e);
						}
						if (isExist) {
							hasTRtypes.put(ht, "该病人已进" + dicText
									+ "高危人群管理，不用再做" + dicText + "问卷。");
							continue;
						} else {
							try {
								isExist = tcModel.isTumourConfirmed(empiId, ht);
							} catch (ModelDataOperationException e) {
								logger.error(
										"Judge whether exist tumour confirmed record failure.",
										e);
								throw new ServiceException(e);
							}
							if (isExist) {
								hasTRtypes.put(ht, "该病人已进" + dicText
										+ "确诊人群管理，不用再做" + dicText + "问卷。");
								continue;
							} else {
								if (latestHT.indexOf(ht) != -1) {// 有最新的问卷模板，没有模板也不显示
									noTRtypes.add(ht);
								} else {
									hasTRtypes.put(ht, dicText
											+ "无问卷模板，请到 系统管理->动态模板维护 中为"
											+ dicText + "类型问卷增加模板。");
								}
							}
						}
					}
				}
			}
			resBodyMap.put("noTRtypes", StringUtils.join(noTRtypes, ","));
			resBodyMap.put("hasTRtypes", hasTRtypes);
		}
		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:加载多模板问卷的个人基本信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-6-19 上午10:39:16
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doInitTumourQuestionnaireBaseInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		// String gcId = (String) reqBodyMap.get("gcId");
		String empiId = (String) reqBodyMap.get("empiId");
		// String masterplateId = (String) reqBodyMap.get("masterplateId");
		String source = (String) reqBodyMap.get("source");
		Map<String, Object> resBody = new HashMap<String, Object>();
		HealthRecordModel hrModel = new HealthRecordModel(dao);
		Map<String, Object> hrMap = null;
		try {
			hrMap = hrModel.getHealthRecordListByEmpiId(empiId);
		} catch (ModelDataOperationException e1) {
			logger.error("Get health record by empiId failure.", e1);
			throw new ServiceException(e1);
		}
		boolean update = true;
		if (hrMap != null && hrMap.size() > 0) {
			String status = (String) hrMap.get("status");
			if (Constants.CODE_STATUS_WRITE_OFF.equals(status)) {
				update = false;
			}
		}
		resBody.put("update", update);
		if (StringUtils.isNotEmpty(empiId)) {
			EmpiModel eModel = new EmpiModel(dao);
			Map<String, Object> eMap = null;
			try {
				eMap = eModel.getEmpiInfoByEmpiid(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get demographic info by empiId[" + empiId
						+ "] failure.", e);
				throw new ServiceException(e);
			}
			Map<String, Object> gcMap = new HashMap<String, Object>();
			if (eMap != null) {
				gcMap.put("empiId", empiId);
				gcMap.put("personName", eMap.get("personName"));
				gcMap.put("idCard", eMap.get("idCard"));
				gcMap.put("sexCode", eMap.get("sexCode"));
				gcMap.put("birthday", eMap.get("birthday"));
				gcMap.put("mobileNumber", eMap.get("mobileNumber"));
				gcMap.put("residentialAddress", eMap.get("address"));
				String msc = (String) eMap.get("maritalStatusCode");
				if ("21".equals(msc) || "23".equals(msc)) {
					msc = "20";
				}
				gcMap.put("maritalStatusCode", msc);
				if (eMap.get("educationCode") != null) {
					String educationCode = (String) eMap.get("educationCode");
					String ec = "";
					int ecd = Integer.parseInt(educationCode);
					if (ecd >= 80) {
						ec = "01";
					} else if (ecd >= 70 && ecd < 80) {
						ec = "02";
					} else if (ecd >= 40 && ecd < 70) {
						ec = "03";
					} else if (ecd >= 30 && ecd < 40) {
						ec = "04";
					} else if (ecd < 30) {
						ec = "05";
					}
					gcMap.put("educationCode", ec);
				}
				gcMap.put("source", source);
				if (hrMap != null && hrMap.size() > 0) {
					gcMap.put("residentialAddress",
							hrMap.get("regionCode_text"));
				}
				gcMap = SchemaUtil.setDictionaryMessageForForm(gcMap,
						PHQ_GeneralCaseMany);
				resBody.put(PHQ_GeneralCaseMany + Constants.DATAFORMAT4FORM,
						gcMap);
			}
		}
		res.put("body", resBody);
	}
}
