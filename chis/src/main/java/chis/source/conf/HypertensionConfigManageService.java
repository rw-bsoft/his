/**
 * @(#)PlanTypeManage.java Created on 2012-2-20 上午10:23:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanCreator;
import chis.source.visitplan.VisitPlanModel;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class HypertensionConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(HypertensionConfigManageService.class);

	private String fixGroupType = "1";

	private String otherExpression = "['eq', ['s', 'group'], ['s', '99']]";

	private String otherPlanTypeCode = "hypertensionOther_planTypeCode";

	// ** 高血压管理------年度起始月份、计划类型、前几后几
	public static final String HYPERTENSION_START_MONTH = "hypertensionStartMonth";
	public static final String HYPERTENSION_END_MONTH = "hypertensionEndMonth";

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
	 * 保存高血压模块相关参数设置
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings({ "unchecked" })
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null || body.size() < 1) {
			logger.error("body  is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		Map<String, Object> formBody = (HashMap<String, Object>) body
				.get("formData");
		if (formBody == null || formBody.size() < 1) {
			logger.error("config datas is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "配置数据丢失！");
			return;
		}
		List<Map<String, Object>> listBody = (List<Map<String, Object>>) body
				.get("listData");
		if (listBody == null || listBody.size() < 1) {
			logger.error("config datas is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "配置数据丢失！");
			return;
		}
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		VisitPlanModel vpm = new VisitPlanModel(dao);
		// ** 删除计划方案表中高血压的原始配置数据
		try {
			// Map<String, Boolean> planTypeChanged = smm.checkConfigUpdate(
			// formBody, ADMIN_HypertensionConfig, BusinessType.GXY);
			vpm.deletePlanInstanceByInstanceType(BusinessType.GXY);
			// ** 判断高血压随访配置是否改变
			// ** 保存用户提交的所有高血压的计划配置数据
			smm.saveHypertensionPlanInstance(formBody, otherPlanTypeCode,
					otherExpression);
			// if (planTypeChanged != null) {
			// // ** 处理业务数据
			// smm.updatebHypertensionBusinessData(planTypeChanged, formBody,
			// visitPlanCreator);
			// }

			// ** 保存用户提交的所有高血压评估字典原始的管理分层配置数据
			smm.saveFixGroupDictionary(listBody, fixGroupType);

			// ** 修改配置文件信息
			Map<String, Object> configMap = new HashMap<String, Object>();
			configMap.put(HYPERTENSION_START_MONTH, formBody.get("startMonth"));
			configMap.put(HYPERTENSION_END_MONTH, formBody.get("endMonth"));
			String planMode = (String) formBody.get("planMode");
			configMap.put(BusinessType.GXY + "_planMode", planMode);
			configMap.put(BusinessType.GXY + "_precedeDays",
					formBody.get("precedeDays"));
			configMap.put(BusinessType.GXY + "_delayDays",
					formBody.get("delayDays"));
			smm.saveSystemConfigData(configMap);
			// 更新随访schema
			smm.changeSchema(planMode, MDC_HypertensionVisit);

		} catch (Exception e) {
			logger.error("failed to save hypertension manage year config!");
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveAssessmentConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HashMap<String, Object> body = (HashMap<String, Object>) req
				.get("body");
		if (body == null || body.size() < 1) {
			logger.error("body  is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		Map<String, Object> formBody = (HashMap<String, Object>) body
				.get("formData");
		if (formBody == null || formBody.size() < 1) {
			logger.error("config datas is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "配置数据丢失！");
			return;
		}
		List<Map<String, Object>> listBody = (List<Map<String, Object>>) body
				.get("listData");
		if (listBody == null || listBody.size() < 1) {
			logger.error("config datas is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "配置数据丢失！");
			return;
		}
		HypertensionConfigManageModel hm = new HypertensionConfigManageModel(
				dao);
		try {
			hm.saveHypertensionAssessParamete(formBody);
			hm.saveHypertensionBPControl(listBody);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHypertensionControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		HypertensionConfigManageModel hm = new HypertensionConfigManageModel(
				dao);
		try {
			hm.saveHypertensionControl(body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryAssessmentFormConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try {
			List<Map<String, Object>> records = dao.doList(null, null,
					MDC_HypertensionAssessParamete);
			if (records != null && records.size() > 0) {
				res.put("body", records.get(0));
			}
		} catch (PersistentDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 获取高血压档案相关系统配置信息
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
		// ** 获取高血压管理年度配置信息
		Map<String, Object> body = null;
		try {
			Map<String, String> configMap = new HashMap<String, String>();
			configMap.put(HYPERTENSION_START_MONTH, "startMonth");
			configMap.put(HYPERTENSION_END_MONTH, "endMonth");
			configMap.put(BusinessType.GXY + "_planMode", "planMode");
			configMap.put(BusinessType.GXY + "_precedeDays", "precedeDays");
			configMap.put(BusinessType.GXY + "_delayDays", "delayDays");
			body = (HashMap<String, Object>) smm.getSystemConfigData(configMap);

			VisitPlanModel vpm = new VisitPlanModel(dao);
			// ** 获取高血压计划方案配置信息
			List<PlanInstance> list = null;
			try {
				list = vpm.getPlanInstanceExpressions(BusinessType.GXY, ctx);
			} catch (CreateVisitPlanException e) {
				logger.error("failed to get hypertension plan instance records!");
				res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
				res.put(RES_MESSAGE, "获取高血压计划方案配置信息失败！");
				throw new ServiceException(e);
			}
			for (int i = 0; i < list.size(); i++) {
				PlanInstance inst = list.get(i);
				String express = inst.getExpression().replaceAll("\"", "'");
				String fieldName = "";
				Schema sc = SchemaController.instance().get(
						ADMIN_HypertensionConfig);
				for (SchemaItem si : sc.getItems()) {
					Object expName = si.getProperty("expression");
					if (expName == null || expName.equals("")) {
						continue;
					}
					String exp = expName.toString().replaceAll("\"", "'");
					if (exp.equalsIgnoreCase(express)) {
						fieldName = si.getId();
						break;
					}
				}
				if (fieldName != null && !"".equals(fieldName)) {
					body.put(fieldName, inst.getPlanTypeCode());
				}
			}
			res.put("body", smm.makeFormResBody(body, ADMIN_HypertensionConfig));
		} catch (Exception e) {
			logger.error("failed to get hypertension manage year config!");
			res.put(RES_CODE, Constants.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "获取高血压管理年度配置信息失败！");
			throw new ServiceException(e);
		}
	}

}
