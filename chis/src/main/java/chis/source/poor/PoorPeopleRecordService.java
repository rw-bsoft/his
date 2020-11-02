/**
 * @(#)PoorPeopleRecordService.java Created on 2012-3-28 下午05:17:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.poor;

import java.util.HashMap;
import java.util.Map;

import ctd.dictionary.DictionaryController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.DiseaseType;
import chis.source.dic.YesNo;
import chis.source.mdc.DiabetesRecordModel;
import chis.source.mdc.HypertensionModel;
import chis.source.phr.HealthRecordModel;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.SchemaUtil;
import ctd.dictionary.Dictionary;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class PoorPeopleRecordService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PoorPeopleRecordService.class);

	/**
	 * 保存贫困人群随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSavePoorPeopleVisitRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		boolean updateIncomeSource = (Boolean) body.get("updateIncomeSource");
		String incomeSource = (String) body.get("incomeSource");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			PoorPeopleRecordModel pprModel = new PoorPeopleRecordModel(dao);
			resBody.putAll(SchemaUtil.setDictionaryMessageForForm(body,
					EHR_PoorPeopleVisit));
			Map<String, Object> value = pprModel.savePoorPeopleVisitRecord(op,
					body);
			if ("create".equals(op) || updateIncomeSource) {
				HealthRecordModel hrModel = new HealthRecordModel(dao);
				hrModel.updateIncomeSourceFlag(empiId, incomeSource);
			}
			if (value != null) {
				resBody.putAll(value);
			}
		} catch (ModelDataOperationException e) {
			logger.error("save savePoorPeopleVisitRecord is fail");
			throw new ServiceException(e);
		}
	}

	/**
	 * 载入贫困人群随访初始化数据
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doLoadInitPoorPeopleRecord(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		String empiId = (String) body.get("empiId");
		Map<String, Object> resBody = new HashMap<String, Object>();
		res.put("body", resBody);
		try {
			HealthRecordModel hrModel = new HealthRecordModel(dao);
			Map<String, Object> empiData = hrModel
					.getHealthRecordByEmpiId(empiId);
			empiData = SchemaUtil.setDictionaryMessageForForm(empiData,
					EHR_HealthRecord);
			if (empiData != null) {
				Map<String, Object> incomeSource = (Map<String, Object>) empiData
						.get("incomeSource");
				resBody.put("incomeSource", incomeSource);
			}
			HypertensionModel hsModel = new HypertensionModel(dao);
			Map<String, Object> hsMap = hsModel.getHypertensionByEmpiId(empiId);
			hsMap = SchemaUtil.setDictionaryMessageForForm(hsMap,
					MDC_HypertensionRecord);

			String diseaseTypeKey = "";
			String diseaseTypeText = "";
			Dictionary dic = DictionaryController.instance().getDic(
					"diseaseType_PoorVisit");
			if (hsMap != null) {
				diseaseTypeKey = DiseaseType.GXY;
				diseaseTypeText = dic.getText(diseaseTypeKey);
			}

			DiabetesRecordModel drModel = new DiabetesRecordModel(dao);
			Map<String, Object> drMap = drModel.getDiabetesByEmpiId(empiId);
			drMap = SchemaUtil.setDictionaryMessageForForm(drMap,
					MDC_DiabetesRecord);
			if (drMap != null) {
				if (diseaseTypeKey.equals("")) {
					diseaseTypeKey = DiseaseType.TNB;
					diseaseTypeText = dic.getText(diseaseTypeKey);
				} else {
					String key = DiseaseType.TNB;
					String text = dic.getText(key);
					diseaseTypeKey = diseaseTypeKey + "," + key;
					diseaseTypeText = diseaseTypeText + "," + text;
				}
			}

			if (!diseaseTypeKey.equals("")) {
				Map<String, Object> diseaseTypeMap = new HashMap<String, Object>();
				diseaseTypeMap.put("key", diseaseTypeKey);
				diseaseTypeMap.put("text", diseaseTypeText);
				resBody.put("diseaseType", diseaseTypeMap);

				Dictionary yesOrNo = DictionaryController.instance().getDic("yesOrNo");
				Map<String, Object> isSickMap = new HashMap<String, Object>();
				isSickMap.put("key", YesNo.YES);
				isSickMap.put("text", yesOrNo.getText(YesNo.YES));
				resBody.put("isSick", isSickMap);
			}

		} catch (ModelDataOperationException e) {
			logger.error("save loadInitPoorPeopleRecord is fail");
			throw new ServiceException(e);
		}
	}
}
