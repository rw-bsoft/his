/**
 * @(#)CheckupRecordService.java Created on 2012-4-23 下午7:53:56
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.per;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.dic.DiabetesRiskDataSource;
import chis.source.dic.DiagnosisType;
import chis.source.dic.HypertensionRiskDataSource;
import chis.source.dic.OperType;
import chis.source.dic.RecordType;
import chis.source.dic.WorkType;
import chis.source.mdc.DiabetesOGTTModel;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.DiabetesRiskModel;
import chis.source.mdc.DiabetesSimilarityModel;
import chis.source.mdc.HypertensionModel;
import chis.source.mdc.HypertensionRiskModel;
import chis.source.mdc.HypertensionSimilarityModel;
import chis.source.phr.HealthRecordModel;
import chis.source.pub.PublicModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.util.UserUtil;
import ctd.dictionary.Dictionary;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CheckupRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(CheckupRecordService.class);

	/**
	 * 作废体检记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLogoutCheckupRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String checkupNo = (String) reqBodyMap.get("checkupNo");
		String empiId = (String) reqBodyMap.get("empiId");
		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		try {
			crmModel.logoutCheckupRecord(checkupNo);
		} catch (ModelDataOperationException e) {
			logger.error("Logout checkup record failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(PER_CheckupRegister, "3", checkupNo, dao,
				empiId);
		// 记录日志
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("empiId", reqBodyMap.get("empiId"));
		body.put("operType", OperType.LOGOUT);
		body.put("recordType", RecordType.PER_CHECKUP);
		PublicModel pmModel = new PublicModel(dao);
		try {
			pmModel.writeLog(body, ctx);
		} catch (ModelDataOperationException e) {
			logger.error("Logout checkup record write log failed.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 体检登记信息保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveCheckupRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> checkRecord = (Map<String, Object>) reqBodyMap
				.get("checkUp");
		List<Map<String, Object>> icds = (List<Map<String, Object>>) reqBodyMap
				.get("icd");
		String op = (String) req.get("op");
		String checkupNo = (String) checkRecord.get("checkupNo");
		String empiId = (String) checkRecord.get("empiId");
		Date birthday = BSCHISUtil.toDate((String) checkRecord.get("birthday"));
		int age = BSCHISUtil.calculateAge(birthday, null);
		checkRecord.put("age", age);

		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		Map<String, Object> checkupMap = null;
		try {
			checkupMap = crmModel.saveCheckupRegister(op, checkRecord, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save checkup register record failed.", e);
			throw new ServiceException(e);
		}
		res.put("body", checkupMap);
		if (StringUtils.isEmpty(checkupNo) && null != checkupMap) {
			checkupNo = (String) checkupMap.get("checkupNo");
		}
		vLogService.saveVindicateLog(PER_CheckupRegister, op, checkupNo, dao,
				empiId);
		try {
			crmModel.deletePERICDbyCheckupNo(checkupNo);
		} catch (ModelDataOperationException e) {
			logger.error("Delete per_icd failed by checkupNo", e);
			// throw new ServiceException(e);
		}

		if (null != icds && icds.size() > 0) {
			for (int i = 0; i < icds.size(); i++) {
				Map<String, Object> icdMap = icds.get(i);
				icdMap.put("checkupNo", checkupNo);
				try {
					crmModel.savePERICD("create", icdMap, true);
				} catch (ModelDataOperationException e) {
					logger.error("Save per_icd failed.", e);
					throw new ServiceException(e);
				}
			}
		}
	}

	/**
	 * 加载体检登记表单数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doInitPerRegisterForm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String pkey = (String) req.get("pkey");
		Map<String, Object> resBody = new HashMap<String, Object>();
		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = crmModel.loadPerRegisterByPkey(pkey);
		} catch (ModelDataOperationException e) {
			logger.error("Initialize checkup register form data failed.", e);
			throw new ServiceException(e);
		}
		if (null == rsMap) {
			Map<String, Boolean> actions = new HashMap<String, Boolean>();
			actions.put("create", true);
			actions.put("update", false);
			actions.put("print", false);
			resBody.put("_actions", actions);

			res.put("body", resBody);
			return;
		}
		resBody = SchemaUtil.setDictionaryMessageForForm(rsMap,
				PER_CheckupRegister);
		res.put("body", resBody);
	}

	/**
	 * 体检总检
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doFinalCheck(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String checkupNo = (String) reqBodyMap.get("checkupNo");
		String empiId = (String) reqBodyMap.get("empiId");
		Date totalCheckupDate = new Date();
		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		try {
			crmModel.finalCheck(checkupNo, totalCheckupDate);
		} catch (ModelDataOperationException e) {
			logger.error("checkup final check failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(PER_CheckupRegister, "7", checkupNo, dao,
				empiId);
		reqBodyMap.put("totalCheckupDate", totalCheckupDate);
		res.put("body", reqBodyMap);
	}

	/**
	 * 体检记录总检撤销
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckRevoke(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String empiId = (String) reqBodyMap.get("empiId");
		String checkupNo = (String) reqBodyMap.get("checkupNo");
		HealthRecordModel hrmModel = new HealthRecordModel(dao);
		Map<String, Object> hrInfoMap = null;
		try {
			hrInfoMap = hrmModel.getHealthRecordByEmpiId(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Get health record Info failed.", e);
			throw new ServiceException(e);
		}
		if (null == hrInfoMap || hrInfoMap.size() == 0) {
			return;
		}

		Map<String, Object> piMap = new HashMap<String, Object>();
		piMap.put("personName", hrInfoMap.get("personName"));
		piMap.put("idCard", hrInfoMap.get("idCard"));
		piMap.put("empiId", empiId);
		piMap.put("manaDoctorId", hrInfoMap.get("manaDoctorId"));
		piMap.put("regionCode", hrInfoMap.get("regionCode"));
		piMap.put("manaUnitId", hrInfoMap.get("manaUnitId"));
		piMap.put("recordType", RecordType.PER_CHECKUP);
		piMap.put("operType", OperType.REVOKE);
		piMap.put("createUnit", UserUtil.get(UserUtil.MANAUNIT_ID));
		piMap.put("createUser", UserUtil.get(UserUtil.USER_ID));
		piMap.put("createTime", BSCHISUtil.toString(new Date(), null));
		PublicModel pmModel = new PublicModel(dao);
		try {
			pmModel.writeLog(piMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save log failed.", e);
			throw new ServiceException(e);
		}

		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		try {
			crmModel.checkRevoke(checkupNo);
		} catch (ModelDataOperationException e) {
			logger.error("Revoke physical examination final Check failed.", e);
			throw new ServiceException(e);
		}
		vLogService.saveVindicateLog(PER_CheckupRegister, "5", checkupNo, dao,
				empiId);
	}

	/**
	 * 获取某个体检记录对应的所有明细信息即小结信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPerDetailInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resData = new HashMap<String, Object>();

		String checkupNo = (String) req.get("checkupNo");
		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		// 查询体检类型
		Map<String, Object> crMap = null;
		try {
			crMap = crmModel.loadPerRegisterByPkey(checkupNo);
		} catch (ModelDataOperationException e) {
			logger.error("Get checkup register record failed.", e);
			throw new ServiceException(e);
		}

		if (null == crMap || crMap.size() == 0) {
			return;
		}
		resData.put("checkupType", crMap.get("checkupType"));

		List<?> cnd = CNDHelper.createSimpleCnd("eq", "checkupNo", "s",
				checkupNo);
		// 查询体检明细
		List<Map<String, Object>> cdList = null;
		try {
			cdList = crmModel.getCheckupDatail(cnd, " id asc ");
		} catch (ModelDataOperationException e) {
			logger.error("Get checkup datail record failed.", e);
			throw new ServiceException(e);
		}
		// 处理体检结果字典显示
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < cdList.size(); i++) {
			Map<String, Object> itemMap = cdList.get(i);
			String checkupProjectId = (String) itemMap.get("checkupProjectId");
			String ifException = (String) itemMap.get("ifException");
			if (StringUtils.isNotEmpty(ifException)) {
				CheckupDictionaryModel dicModel = new CheckupDictionaryModel(
						dao);
				Map<String, Object> dicMap = null;
				try {
					dicMap = dicModel.getCheckupDictByPkey(checkupProjectId);
					if (dicMap != null && dicMap.size() > 0) {
						String checkupDic = (String) dicMap.get("checkupDic");
						if (StringUtils.isNotEmpty(checkupDic)) {
							Dictionary dic = DictionaryController.instance()
									.get("chis.dictionary." + checkupDic);
							String ifException_text = dic.getText(ifException);
							itemMap.put("ifException_text", ifException_text);
						}
					}
				} catch (Exception e) {
					logger.error("Get checkup dictionary item by pkey failed.",
							e);
					throw new ServiceException(e);
				}
			}
			rsList.add(itemMap);
		}

		resData.put("itemList", rsList);

		// 查询科室小结
		List<Map<String, Object>> csList = null;
		try {
			csList = crmModel.getCheckupSummary(cnd, " id asc ");
		} catch (ModelDataOperationException e) {
			logger.error("Get checkup summary record failed.", e);
			throw new ServiceException(e);
		}
		resData.put("sumList", SchemaUtil.setDictionaryMessageForList(csList,
				PER_CheckupSummary));

		this.groupData(resData);

		// 加载权限
		Map<String, Object> cMap = new HashMap<String, Object>();
		cMap.put("empiId", crMap.get("empiId"));
		cMap.put("phrId", crMap.get("phrId"));
		cMap.put("checkupNo", crMap.get("checkupNo"));
		cMap.put("status", crMap.get("status"));
		cMap.put("totalCheckupDate", crMap.get("totalCheckupDate"));
		try {
			// 获取体检明细小结表单权限
			Map<String, Boolean> data = ControlRunner.run(PER_CheckupSummary,
					cMap, ctx, ControlRunner.UPDATE);
			resData.put("_actions", data);
		} catch (ServiceException e) {
			logger.error("check PER_CheckupRegister record control error .", e);
			throw e;
		}
		res.put("body", resData);
	}

	// 科室小结、体检项目明细根据科室进行分组。
	@SuppressWarnings("unchecked")
	private void groupData(Map<String, Object> data) {
		List<Map<String, Object>> itemList = (List<Map<String, Object>>) data
				.get("itemList");
		List<Map<String, Object>> sumList = (List<Map<String, Object>>) data
				.get("sumList");
		Map<String, Object> sumGroups = new HashMap<String, Object>();
		Map<String, Object> itemGroups = new HashMap<String, Object>();
		for (int i = 0; i < sumList.size(); i++) {
			Map<String, Object> sumItem = (Map<String, Object>) sumList.get(i);
			String projCode = (String) sumItem.get("projectOfficeCode");
			sumGroups.put(projCode, sumItem);
		}

		for (int i = 0; i < itemList.size(); i++) {
			Map<String, Object> item = (Map<String, Object>) itemList.get(i);
			String projCode = (String) item.get("projectOfficeCode");
			if (itemGroups.get(projCode) == null) {
				itemGroups.put(projCode, new ArrayList<Map<String, Object>>());
			}
			List<Map<String, Object>> subGroup = (List<Map<String, Object>>) itemGroups
					.get(projCode);
			subGroup.add(item);
		}
		data.put("sumList", sumGroups);
		data.put("itemList", itemGroups);
	}

	/**
	 * 保存体检明细
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws PersistentDataOperationException
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePerDetailInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, PersistentDataOperationException,
			ModelDataOperationException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String checkupNo = (String) reqBodyMap.get("checkupNo");
		String projectOfficeCode = (String) reqBodyMap.get("projectOfficeCode");
		String recordNo = (String) reqBodyMap.get("phrId");
		String empiId = (String) reqBodyMap.get("empiId");

		CheckupRecordModel crmModel = new CheckupRecordModel(dao);
		// 删除所有的科室小结
		try {
			crmModel.deleteCheckupSummary(checkupNo, projectOfficeCode);
		} catch (ModelDataOperationException e) {
			logger.error("Delete checkup sumary by checkupNo failed.", e);
			throw new ServiceException(e);
		}
		// 删除所有体检项目
		try {
			crmModel.deleteCheckupDetail(checkupNo, projectOfficeCode);
		} catch (ModelDataOperationException e) {
			logger.error("Delete checkup detail by checkupNo failed.", e);
			throw new ServiceException(e);
		}

		List<Map<String, Object>> itemList = (List<Map<String, Object>>) reqBodyMap
				.get("itemList");
		Map<String, Object> summary = (Map<String, Object>) reqBodyMap
				.get("sumList");

		StringBuilder excDesc = new StringBuilder();
		for (int j = 0; j < itemList.size(); j++) {
			Map<String, Object> obj = (Map<String, Object>) itemList.get(j);
			// 插入体检项
			obj.put("checkupNo", checkupNo);
			obj.put("recordNo", recordNo);
			obj.put("empiId", empiId);
			// 糖尿病高危
			if ((obj.get("checkupProjectId").equals("0000000000000174") && !BSCHISUtil
					.isBlank(obj.get("ifException")))
					|| (obj.get("checkupProjectId").equals("0000000000000175") && !BSCHISUtil
							.isBlank(obj.get("ifException")))) {
				double fbs = 0.0;
				double pbs = 0.0;
				if (obj.get("checkupProjectId").equals("0000000000000174")) {
					fbs = BSCHISUtil.parseToDouble(obj.get("ifException") + "");
				}
				if (obj.get("checkupProjectId").equals("0000000000000175")) {
					pbs = BSCHISUtil.parseToDouble(obj.get("ifException") + "");
				}
				DiabetesOGTTModel drm = new DiabetesOGTTModel(dao);
				drm.insertDiabetesOGTTRecord(empiId,
						(String) reqBodyMap.get("phrId"), fbs, pbs, reqBodyMap);
				DiabetesSimilarityModel dsm = new DiabetesSimilarityModel(dao);
				dsm.insertDiabetesSimilarity(checkupNo, empiId,
						(String) reqBodyMap.get("phrId"), fbs, pbs,
						DiabetesRiskDataSource.PER, reqBodyMap);
			}
			Integer ifExceptionValue = null;
			if (!BSCHISUtil.isBlank(obj.get("ifException"))
					&& obj.get("ifException").toString().indexOf(".") > -1) {
				String value = obj.get("ifException").toString();
				value = value.substring(0, value.indexOf("."));
				ifExceptionValue = Integer.parseInt(value);
			}
			// 高血压高危
			if ((obj.get("checkupProjectId").equals("0000000000000054")
					&& !BSCHISUtil.isBlank(obj.get("ifException")) && (ifExceptionValue >= 120 && ifExceptionValue <= 139))
					|| (obj.get("checkupProjectId").equals("0000000000000057")
							&& !BSCHISUtil.isBlank(obj.get("ifException")) && (ifExceptionValue >= 80 && ifExceptionValue <= 89))) {
				HypertensionRiskModel hrm = new HypertensionRiskModel(dao);
				int constriction = 0;
				int diastolic = 0;
				if (obj.get("checkupProjectId").equals("0000000000000054")) {
					constriction = ifExceptionValue;
				}
				if (obj.get("checkupProjectId").equals("0000000000000057")) {
					diastolic = ifExceptionValue;
				}
				hrm.insertHypertensionRisk(empiId,
						(String) reqBodyMap.get("phrId"), constriction,
						diastolic, "4");
			}
			// 高血压生成疑似记录
			if ((obj.get("checkupProjectId").equals("0000000000000054")
					&& !BSCHISUtil.isBlank(obj.get("ifException")) && ifExceptionValue >= 140)
					|| (obj.get("checkupProjectId").equals("0000000000000057")
							&& !BSCHISUtil.isBlank(obj.get("ifException")) && ifExceptionValue >= 90)) {
				HypertensionSimilarityModel hrm = new HypertensionSimilarityModel(
						dao);
				int constriction = 0;
				int diastolic = 0;
				if (obj.get("checkupProjectId").equals("0000000000000054")) {
					constriction = ifExceptionValue;
				}
				if (obj.get("checkupProjectId").equals("0000000000000057")) {
					diastolic = ifExceptionValue;
				}
				hrm.insertHypertensionSimilarity(empiId,
						(String) reqBodyMap.get("phrId"), constriction,
						diastolic, reqBodyMap);
			}

			Map<String, Object> rsMap = null;
			try {
				rsMap = crmModel.saveCheckupDetail("create", obj, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save checkup detail failed.", e);
				throw new ServiceException(e);
			}
			String id = "";
			if (rsMap != null) {
				id = (String) rsMap.get("id");
			}
			vLogService.saveVindicateLog(PER_CheckupDetail, "1", id, dao,
					empiId);

			if (!obj.containsKey("checkupOutcome")) {
				continue;
			}

			if (obj.get("checkupOutcome") == null
					|| "null".equals(obj.get("checkupOutcome"))) {
				continue;
			}

			if ((!obj.get("checkupOutcome").equals("2"))) {
				continue;
			}

			String ifExceptionText = "";
			if (obj.containsKey("ifException_text")
					&& (null != obj.get("ifException_text") && !obj.get(
							"ifException_text").equals(""))) {
				ifExceptionText = (String) obj.get("ifException_text");
			} else if (obj.containsKey("ifException")
					&& null != obj.get("ifException")
					&& !obj.get("ifException").equals("")) {
				ifExceptionText = (String) obj.get("ifException");
			} else {
				ifExceptionText = "异常";
			}
			String checkupProjectName = (String) obj.get("checkupProjectName");
			excDesc.append(checkupProjectName).append(":")
					.append(ifExceptionText).append(";");

		}

		// 插入科室小结
		if (summary != null) {
			summary.put("exceptionDesc", excDesc.toString());
			summary.put("summaryDate", BSCHISUtil.toString(new Date(),
					Constants.DEFAULT_SHORT_DATE_FORMAT));
			summary.put("checkupNo", checkupNo);
			summary.put("recordNo", recordNo);
			summary.put("empiId", empiId);

			try {
				crmModel.saveCheckSummary("create", summary, true);
			} catch (ModelDataOperationException e) {
				logger.error("Save checkup summary failed.", e);
				throw new ServiceException(e);
			}
		}

		// 返回结果描述到前台
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("ExcDes", excDesc.toString());
		res.put("body", resBodyMap);

		// 获取体检异常综述
		List<Map<String, Object>> rsList = null;
		try {
			rsList = crmModel.getCheckupExce(checkupNo);
		} catch (ModelDataOperationException e) {
			logger.error("Get physical examination exception failed.", e);
			throw new ServiceException(e);
		}
		StringBuffer sumupExceptionBuffer = new StringBuffer();
		Map<String, Object> dicItems = null;
		try {
			dicItems = crmModel.getDicItems();
			for (int i = 0; i < rsList.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) rsList.get(i);
				String checkupProjectId = map.get("checkupProjectId")
						.toString();
				String checkupProjectName = map.get("checkupProjectName")
						.toString();
				String ifException = "";
				if (map.get("ifException") != null) {
					ifException = map.get("ifException").toString();
				} else {
					ifException = "异常";
				}
				if (dicItems.containsKey(checkupProjectId)) {
					String dicId = dicItems.get(checkupProjectId).toString();
					ifException = DictionaryController.instance().get(dicId)
							.getItem(ifException).getText();
				}
				sumupExceptionBuffer.append(checkupProjectName + ":"
						+ ifException + ";");
			}
		} catch (Exception e) {
			logger.error("Get physical examination have dictionary items", e);
			throw new ServiceException(e);
		}
		String sumupException = sumupExceptionBuffer.toString();
		resBodyMap.put("sumupException", sumupException);
		// 保存体检异常综述
		try {
			crmModel.savePerSumupException(checkupNo, sumupException);
		} catch (ModelDataOperationException e) {
			logger.error("Save physical examination exception sum up fialed.",
					e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取体检项字典
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetCheckupDict(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<?> cnd = (List<?>) req.get("cnd");
		CheckupDictionaryModel cdModel = new CheckupDictionaryModel(dao);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = cdModel.getCheckupDict(cnd);
		} catch (ModelDataOperationException e) {
			logger.error("Get physical examination project dictionary failed.",
					e);
			throw new ServiceException(e);
		}
		res.put("body", rsList);
	}

	/**
	 * 获取体检注册页面权限控制<br/>
	 * 包括 注册表单及ICD列表
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPerReegisterControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> crfData = new HashMap<String, Boolean>();
		try {
			crfData = ControlRunner.run(PER_CheckupRegister, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE, "finalCheck",
					"print");
		} catch (ServiceException e) {
			logger.error("check PER_CheckupRegister record control error .", e);
			throw e;
		}
		Map<String, Boolean> icdData = new HashMap<String, Boolean>();
		try {
			icdData = ControlRunner.run(PER_ICD, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check PER_ICD record control error .", e);
			throw e;
		}
		resBody.put(PER_CheckupRegister + "_control", crfData);
		resBody.put(PER_ICD + "_control", icdData);
		res.put("body", resBody);
	}
}
