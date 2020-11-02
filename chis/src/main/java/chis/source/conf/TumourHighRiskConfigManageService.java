/**
 * @(#)TumourHighRiskConfigManageService.java Created on 2014-4-10 下午5:13:40
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

import chis.source.BaseDAO;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanModel;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class TumourHighRiskConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(TumourHighRiskConfigManageService.class);
	// 肿瘤高危人群随访有效时间范围
	public static final String THR_START_MONTH = "tumourHighRiskStartMonth";
	public static final String THR_END_MONTH = "tumourHighRiskEndMonth";

	/**
	 * @Description:保存肿瘤高危人群随访计划生成配置参数
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-11 上午9:55:01
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
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
				.get("form");
		ArrayList<Map<String, Object>> gridBody = (ArrayList<Map<String, Object>>) body
				.get("grid");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			// 保存配置文件信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(BusinessType.THR+"_planMode", formBody.get("planMode"));
			map.put(THR_START_MONTH, StringUtils.trimToEmpty((String) formBody
					.get("startMonth")));
			map.put(THR_END_MONTH,
					StringUtils.trimToEmpty((String) formBody.get("endMonth")));
			map.put(BusinessType.THR + "_precedeDays",
					formBody.get("precedeDays"));
			map.put(BusinessType.THR + "_delayDays", formBody.get("delayDays"));
			smm.saveSystemConfigData(map);

			// 保存随访计划配置前,将旧计划删除
			VisitPlanModel vpm = new VisitPlanModel(dao);
			vpm.deletePlanInstanceByInstanceType(BusinessType.THR);
			// 保存随访计划配置
			for (int i = 0; i < gridBody.size(); i++) {
				Map<String, Object> planBody = (HashMap<String, Object>) gridBody
						.get(i);
				Object group = planBody.get("group");
				Object tumourType = planBody.get("tumourType");
				if (group != null && tumourType != null) {
					if (!StringUtils.isEmpty(group.toString())) {
						String expression = "['and',['eq', ['s', 'group'], ['s', '"
								+ group
								+ "']],['eq',['s','tumourType'],['s','"
								+ tumourType + "']]]";
						planBody.put("expression", expression);
					}
				}
				planBody.put("instanceType", BusinessType.THR);
				vpm.savePlanInstanceRecord("create", planBody);
			}
		} catch (Exception e) {
			logger.error(
					"Failed to delete the old plan type records OR save new plan type records for tumour !",
					e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:获取配置文件中配置的参数
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-11 上午10:43:26
	 * @Modify:
	 */
	public void doQueryFormConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		// 读取配置文件年度开始月份和年度结束月份信息
		Map<String, Object> body = null;
		try {
			Map<String, String> configMap = new HashMap<String, String>();
			configMap.put(THR_START_MONTH, "startMonth");
			configMap.put(THR_END_MONTH, "endMonth");
			configMap.put(BusinessType.THR + "_precedeDays", "precedeDays");
			configMap.put(BusinessType.THR + "_delayDays", "delayDays");
			configMap.put(BusinessType.THR + "_planMode", "planMode");
			body = smm.getSystemConfigData(configMap);
			res.put("body", smm.makeFormResBody(body, ADMIN_TumourHighRiskConfig));
		} catch (Exception e) {
			logger.error("failed to get tumour hight risk parameter of config!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 
	 * @Description:
	 * 	从数据中记录的计划生成方案表达式中解析出计划生成方案配置
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2014-4-11 上午10:46:42
	 * @Modify:
	 */
	public void doQueryListConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<PlanInstance> list = null;
		try {
			list = vpm.getPlanInstanceExpressions(BusinessType.THR, ctx);
		} catch (CreateVisitPlanException e) {
			logger.error("Failed to get tumour high risk plan instance records!", e);
			throw new ServiceException(e);
		}
		ArrayList<Map<String, Object>> abody = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			PlanInstance inst = list.get(i);
			String expression = inst.getExpression();
			Map<String, Object> result = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(expression)) {
				try {
					List<?> cnd = CNDHelper.toListCnd(expression);
					List<?> start = (List<?>) cnd.get(1);
					if (start.size() > 2) {
						List<?> ge = (List<?>) start.get(2);
						Object group = ge.get(1);
						result.put("group", group);
					}
					List<?> end = (List<?>) cnd.get(2);
					if (end.size() > 2) {
						List<?> lt = (List<?>) end.get(2);
						Object tumourType = lt.get(1);
						result.put("tumourType", tumourType);
					}
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("instanceType", inst.getInstanceType());
			result.put("planTypeCode", inst.getPlanTypeCode());
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_TumourHighRiskConfigDetail));
		}
		res.put("body", abody);
	}
}
