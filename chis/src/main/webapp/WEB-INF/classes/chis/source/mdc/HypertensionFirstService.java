/**
 * @(#)HypertensionFirstService.java Created on 2012-3-21 上午10:11:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.HypertensionFirstCheck;
import chis.source.pub.PublicModel;
import chis.source.util.BSCHISUtil;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionFirstService extends MDCService {
	private static final Logger logger = LoggerFactory
			.getLogger(HypertensionFirstService.class);

	/**
	 * 通过查询首诊测压信息，核实高血压
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetHypertensionFirstId(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		if (empiId == null || empiId.trim().length() == 0) {
			logger.info("empiId is missing.");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "个人ID丢失。");
			return;
		}
		HypertensionFirstModel hfm = new HypertensionFirstModel(dao);
		try {
			String rn = hfm.getRecordNumberOfHypertensionFirst(empiId);
			if (rn == null || rn.trim().length() == 0) {
				logger.info("no hypertensionFirst record is missing.");
				res.put(RES_CODE, Constants.CODE_NOT_FOUND);
				res.put(RES_MESSAGE, "该人没有做过首诊测压，无法进行核实。");
				return;
			}
			List<String> body = new ArrayList<String>();
			body.add(rn);
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get hypertension first record.", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取高血压疑似病人核实--核实表单加载时一些参数<br>
	 * 业务概述：1.根据empiId取高血压首诊信息的"核实结果(diagnosisType)"<br>
	 * 2.如果"核实结果(diagnosisType)"值为 核实 (1)，则去查有无高血压档案，<br>
	 * 放一个参数到前端，以提示用户是否要去创建高血压档案<br>
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public void doGetLoadHypertensionFirstParam(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> resBody = new HashMap<String, Object>();
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		String diagnosisType = (String) body.get("diagnosisType");
		String hypertensionFirstId = (String) body.get("hypertensionFirstId");

		if (StringUtils.isEmpty(diagnosisType)) {
			HypertensionFirstModel hfm = new HypertensionFirstModel(dao);
			List<Map<String, Object>> rsList = null;
			try {
				rsList = hfm.getHypertensionFistByEmpiId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get hypertension fist info failed.", e);
				throw new ServiceException(e);
			}
			Map<String, Object> hfrMap = new HashMap<String, Object>();
			if (rsList != null && rsList.size() > 0) {
				hfrMap = rsList.get(0);
			}
			if (hfrMap.size() > 0) {
				diagnosisType = (String) hfrMap.get("diagnosisType");
				hypertensionFirstId = (String) hfrMap.get("recordNumber");
			}
		}

		boolean hasHypertensionRecord = false;// 是否有高血压档案
		if (diagnosisType
				.equals(HypertensionFirstCheck.DIAGNOSISTYPE_CONFIRMED)) {
			// 确诊为高血压 查有无高血压档案
			HypertensionModel hm = new HypertensionModel(dao);
			List<Map<String, Object>> rsList;
			try {
				rsList = hm.findHypertensionRecordByEmpiId(empiId);
			} catch (ModelDataOperationException e) {
				logger.error("Get Hypertension record by empiId failed.", e);
				throw new ServiceException(e);
			}
			if (null != rsList && rsList.size() > 0) {
				hasHypertensionRecord = true;
			}
		}
		resBody.put("hasHypertensionRecord", hasHypertensionRecord);
		resBody.put("empiId", empiId);
		resBody.put("diagnosisType", diagnosisType);
		resBody.put("hypertensionFirstId", hypertensionFirstId);
		res.put("body", resBody);
	}

	/**
	 * 保存首诊测压信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHypertensionFirst(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> reqBody = (HashMap<String, Object>) req
				.get("body");
		if (reqBody == null) {
			logger.error("body is missing");
			return;
		}

		String hypertensionFirstId = (String) reqBody
				.get("hypertensionFirstId");
		// if need to create HypertensionFirst record
		Format dateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
		String dateStr = dateFormat.format(new Date());
		dateStr = dateStr.replace(" ", "T");
		String doctor = (String) reqBody.get("measureDoctor");
		String unit = (String) reqBody.get("measureUnit");
		boolean ifDoCheckResult = true;
		HypertensionFirstModel hfm = new HypertensionFirstModel(dao);
		String op = (String) req.get("op");
		if(op==null){
			op="create";
		}
		if (hypertensionFirstId == null
				|| hypertensionFirstId.trim().length() == 0) {
			String checkResult = (String) reqBody.get("diagnosisType");
			if ("1".equals(checkResult) || "3".equals(checkResult)) {
				reqBody.put("diagnosisDoctor", doctor);
				reqBody.put("diagnosisDate", dateStr);
				reqBody.put("diagnosisUnit", unit);
			}
			if (op.equals("update")) {
				reqBody.put("lastModifyDate",
						BSCHISUtil.toString(new Date(), null));
				reqBody.put("lastModifyUser", doctor);
			}
			// reqBody.put("recordNumber", hypertensionFirstId);
			Map<String, Object> rsMap = null;
			try {
				rsMap = hfm.saveHypertensionFirstInfo(op, reqBody, true);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save hypertension first record.", e);
				throw new ServiceException(e);
			}
			if(null != rsMap){
				hypertensionFirstId = (String) rsMap.get("recordNumber");
			}
			ifDoCheckResult = false;
		}

		// save HypertensionFirst check record
		Object conObj = reqBody.get("constriction1");
		Object diaObj = reqBody.get("diastolic1");
		int con = 0 ; 
		int dia = 0 ;
		if(conObj instanceof Integer){
			con = (Integer) conObj;
			dia = (Integer) diaObj;
		}else {
			con = Integer.parseInt((String)conObj);
			dia = Integer.parseInt((String)diaObj);
		}
		int hypertensionLevel = MDCBaseModel.decideHypertensionGrade(con, dia);
		reqBody.put("recordNumber", hypertensionFirstId);
		reqBody.put("measureDate1", dateStr);
		reqBody.put("hypertensionLevel1", hypertensionLevel);
		reqBody.put("measureDoctor1", doctor);
		reqBody.put("measureUnit1", unit);
		try {
			// 保存首诊测压明细
			hfm.saveHypertensionFirstDetailInfo("create", reqBody, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save detail of first record.", e);
			throw new ServiceException(e);
		}

		// 判断是否进行确诊或排除高血压
		if (ifDoCheckResult) {
			String cnd = "['eq',['$','recordNumber'],['s','"
					+ hypertensionFirstId + "']]";
			req.put("cnd", cnd);
			List<Map<String, Object>> hypertensionDetialsBody = null;
			try {
				hypertensionDetialsBody = hfm.getHypertensionFirstDetailByCnd(
						cnd, null);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to check wether has check result.", e);
				throw new ServiceException(e);
			}
			if (hypertensionDetialsBody.size() < 2)
				return;

			// 保存不同一天内血压异常记录中的时间字符串。相同日期的将被覆盖
			Set<Object> exceptionRecords = new HashSet<Object>();
			Set<Object> normalRecords = new HashSet<Object>();
			for (int i = 0; i < hypertensionDetialsBody.size(); i++) {
				HashMap<String, Object> record = (HashMap<String, Object>) hypertensionDetialsBody
						.get(i);
				int level = (Integer) record.get("hypertensionLevel1");
				Date date = (Date) record.get("measureDate1");
				// 正常血压级别
				if ("0".equals(level) || "4".equals(level) || "5".equals(level)
						|| "6".equals(level)) {
					normalRecords.add(date);
					continue;
				}
				exceptionRecords.add(date);
			}
			// 存在血压级别的测压次数小于2，并且总测压次数小于三次的，仍保持之前的状态。
			if (exceptionRecords.size() < 2
					&& hypertensionDetialsBody.size() < 3)
				return;
			List<Map<String, Object>> hypertensionFirstBody = null;
			try {
				// 获取高血压首诊测压信息
				hypertensionFirstBody = hfm
						.getHypertensionFirstByCnd(cnd, null);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to get hypertension first record.", e);
				throw new ServiceException(e);
			}
			if (hypertensionFirstBody.size() == 0)
				return;
			HashMap<String, Object> firstRecord = (HashMap<String, Object>) hypertensionFirstBody
					.get(0);
			firstRecord.put("diagnosisDoctor", doctor);
			firstRecord.put("diagnosisDate", dateStr);
			firstRecord.put("diagnosisUnit", unit);
			if (exceptionRecords.size() >= 2) {// 如果两次测压都有血压级别，确诊
				firstRecord.put("diagnosisType", "1");
			} else if (normalRecords.size() >= 2) {// 有三次测压，但有血压级别的小于2次，排除。
				firstRecord.put("diagnosisType", "3");
			}
			try {
				hfm.saveHypertensionFirstInfo("update", firstRecord, true);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to save hypertension first record.", e);
				throw new ServiceException(e);
			}
		}

	}

	/**
	 * 判断是否需要首诊测压
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doIfHypertensionFirst(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		String empiId = (String) req.get("empiId");
		if (empiId == null || empiId.trim().length() == 0) {
			logger.error("empiId is missing");
			return;
		}

		int task = HypertensionFirstCheck.DO_HYPERTENSION_NON;
		PublicModel pm = new PublicModel(dao);
		try {
			task = pm.ifHypertensionFirst(empiId);
		} catch (ModelDataOperationException e) {
			logger.error("Check if hypertension first failed.", e);
			throw new ServiceException(e);
		}

		if (task == HypertensionFirstCheck.DO_HYPERTENSION_NON) {
			res.put("hypertensionFirst", "0");
			res.put("hypertensionFirstCheck", "0");
			return;
		}

		if (task == HypertensionFirstCheck.DO_HYPERTENSION_FIRST) {
			res.put("hypertensionFirst", "1");
			res.put("hypertensionFirstCheck", "0");
			return;
		}

		if (task == HypertensionFirstCheck.DO_HYPERTENSION_CHECK) {
			res.put("hypertensionFirst", "0");
			res.put("hypertensionFirstCheck", "1");
			return;
		}
	}
}
