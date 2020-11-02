/**
 * @(#)PlanTypeManage.java Created on 2012-2-17 下午02:23:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import com.alibaba.fastjson.JSONException;

import ctd.app.ApplicationController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class HypertensionRiskConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(HypertensionRiskConfigManageService.class);
	// chb 糖尿病管理年度起始月份
	public static final String HEPERTENSIONRISK_START_MONTH = "hypertensionRiskStartMonth";
	public static final String HEPERTENSIONRISK_END_MONTH = "hypertensionRiskEndMonth";

	private VisitPlanCreator visitPlanCreator;

	/**
	 * 获得visitPlanCreator
	 * 
	 * @return the visitPlanCreator
	 */
	public VisitPlanCreator getVisitPlanCreator() {
		return visitPlanCreator;
	}

	/**
	 * 设置visitPlanCreator
	 * 
	 * @param visitPlanCreator
	 *            the visitPlanCreator to set
	 */
	public void setVisitPlanCreator(VisitPlanCreator visitPlanCreator) {
		this.visitPlanCreator = visitPlanCreator;
	}

	/**
	 * 保存糖尿病模块相关参数设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		if (body == null || body.size() < 1) {
			logger.error("body  is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			// // ** 判断高血压随访配置是否改变
			// Map<String, Boolean> planTypeChanged =
			// smm.checkConfigUpdate(body,
			// ADMIN_HypertensionConfig, BusinessType.HypertensionRisk);
			// ** 删除计划方案表中糖尿病的原始配置数据
			vpm.deletePlanInstanceByInstanceType(BusinessType.HypertensionRisk);
			// ** 保存用户提交的所有糖尿病的计划配置数据
			body.put("planMode", "1");
			smm.saveHypertensionRiskPlanInstance(body);

			// if (planTypeChanged != null) {
			// // ** 处理业务数据
			// if (((String) body.get("planMode")).equals("2")) {
			// body.put("planType1", 15);
			// }
			// smm.updatebHypertensionBusinessData(planTypeChanged, body,
			// visitPlanCreator);
			// }

			// ** 修改配置文件信息
			Map<String, Object> configMap = new HashMap<String, Object>();
			configMap.put(HEPERTENSIONRISK_START_MONTH, body.get("startMonth"));
			configMap.put(HEPERTENSIONRISK_END_MONTH, body.get("endMonth"));
			String planMode = (String) body.get("planMode");
			configMap.put(BusinessType.HypertensionRisk + "_planMode", planMode);
			configMap.put(BusinessType.HypertensionRisk + "_precedeDays",
					body.get("precedeDays"));
			configMap.put(BusinessType.HypertensionRisk + "_delayDays",
					body.get("delayDays"));
			configMap.put(BusinessType.HypertensionRisk + "_planTypeCode", body.get("planType1"));
			smm.saveSystemConfigData(configMap);
			ApplicationController.instance().reload(Constants.UTIL_APP_ID);
		} catch (Exception e) {
			logger.error("Failed to save the hypertension config records!");
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取糖尿病档案相关系统配置信息
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public void doQueryConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> body = null;
		try {
			Map<String, String> configMap = new HashMap<String, String>();
			configMap.put(HEPERTENSIONRISK_START_MONTH, "startMonth");
			configMap.put(HEPERTENSIONRISK_END_MONTH, "endMonth");
			configMap.put(BusinessType.HypertensionRisk + "_planMode", "planMode");
			configMap.put(BusinessType.HypertensionRisk + "_planTypeCode", "planType1");
			configMap.put(BusinessType.HypertensionRisk + "_precedeDays", "precedeDays");
			configMap.put(BusinessType.HypertensionRisk + "_delayDays", "delayDays");
			body = smm.getSystemConfigData(configMap);
		} catch (Exception e) {
			logger.error("failed to get hypertensionrisk manage year config!");
			throw new ServiceException(e);
		}
//		VisitPlanModel vpm = new VisitPlanModel(dao);
//		List<PlanInstance> list = null;
//		Schema sc = null;
//		try {
//			list = vpm.getPlanInstanceExpressions(BusinessType.HypertensionRisk, ctx);
//			sc = SchemaController.instance().get(ADMIN_HypertensionRiskConfig);
//		} catch (Exception e) {
//			logger.error("Failed to get hypertensionrisk plan instance records!");
//			throw new ServiceException(e);
//		}
//		for (int i = 0; i < list.size(); i++) {
//			PlanInstance inst = list.get(i);
//			String express = inst.getExpression().replaceAll("\"", "'");
//			String fieldName = "";
//			for (SchemaItem si : sc.getItems()) {
//				Object expName = si.getProperty("expression");
//				if (expName == null || expName.equals("")) {
//					continue;
//				}
//				String exp = expName.toString().replaceAll("\"", "'");
//				if (exp.equalsIgnoreCase(express)) {
//					fieldName = si.getId();
//				}
//			}
//			body.put(fieldName, inst.getPlanTypeCode());
//		}
		try {
			res.put("body", smm.makeFormResBody(body, ADMIN_HypertensionRiskConfig));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

}
