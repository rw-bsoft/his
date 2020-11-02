/**
 * @(#)ChildrenConfigManageService.java Created on 2012-2-21 下午03:23:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.xml.XMLHelper;

/**
 * @description 精神病模块参数配置服务类
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class PsychosisConfigManageService extends AbstractActionService
		implements DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(PregnantConfigManageService.class);
	private static final String SCH = "spring/spring-schedule.xml";
	// ^^ 精神病档案参数设置配置信息
	public static final String PSYCHOSIS_START_MONTH = "psychosisStartMonth";
	public static final String PSYCHOSIS_END_MONTH = "psychosisEndMonth";

	/**
	 * 保存 精神病模块参数设置,保存年度开始月份和年度结束月份&&随访计划信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) req.get("body");
		Map<String, Object> fBody = (HashMap<String, Object>) body.get("form");
		ArrayList<Map<String, Object>> gBody = (ArrayList<Map<String, Object>>) body
				.get("grid");
		if (body == null || body.size() < 1) {
			logger.error("body is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			// 保存配置文件信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(PSYCHOSIS_START_MONTH,
					StringUtils.trimToEmpty((String) fBody.get("startMonth")));
			map.put(PSYCHOSIS_END_MONTH,
					StringUtils.trimToEmpty((String) fBody.get("endMonth")));
			map.put(BusinessType.PSYCHOSIS + "_precedeDays",
					fBody.get("precedeDays"));
			map.put(BusinessType.PSYCHOSIS + "_delayDays",
					fBody.get("delayDays"));
			smm.saveSystemConfigData(map);
			// 根据开始月份 设置年检提醒时间
			File schedule = smm.getFile(SCH);
			Document doc = smm.getFileDoc(schedule, "mo",
					"http://www.springframework.org/schema/beans");
			Element psy = (Element) doc
					.selectSingleNode("//mo:beans/mo:bean[@id='psyAnnualAssessmentTrigger']/mo:property[@name='cronExpression']");
			psy.addAttribute(
					"value",
					"0 0 0 "
							+ Integer.parseInt(StringUtils
									.trimToEmpty((String) fBody
											.get("startMonth"))) + " 1 ?");
			XMLHelper.putDocument(schedule, doc);
			// 保存随访计划配置前,将旧计划删除
			VisitPlanModel vpm = new VisitPlanModel(dao);
			vpm.deletePlanInstanceByInstanceType(BusinessType.PSYCHOSIS);

			// 保存随访计划配置
			for (int i = 0; i < gBody.size(); i++) {
				Map<String, Object> planBody = (HashMap<String, Object>) gBody
						.get(i);
				Object instanceType = planBody.get("instanceType");
				if (instanceType != null) {
					if (!StringUtils.isEmpty(instanceType.toString())) {
						String expression = "['eq', ['s', 'visitType'], ['s', '"
								+ instanceType + "']]";
						planBody.put("expression", expression);
					}
				}
				planBody.put("instanceType", BusinessType.PSYCHOSIS);
				vpm.savePlanInstanceRecord("create", planBody);

			}
		} catch (Exception e) {
			logger.error(
					"Failed to delete the old plan type records for psychosis!",
					e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 读取 精神病模块参数设置,读取年度开始月份和年度结束月份
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryFormConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		// 读取配置文件年度开始月份和年度结束月份信息
		Map<String, Object> body = null;
		try {
			Map<String, String> configMap = new HashMap<String, String>();
			configMap.put(PSYCHOSIS_START_MONTH, "startMonth");
			configMap.put(PSYCHOSIS_END_MONTH, "endMonth");
			configMap.put(BusinessType.PSYCHOSIS + "_precedeDays",
					"precedeDays");
			configMap.put(BusinessType.PSYCHOSIS + "_delayDays", "delayDays");
			body = smm.getSystemConfigData(configMap);
			res.put("body", smm.makeFormResBody(body, ADMIN_PsychosisConfig));
		} catch (Exception e) {
			logger.error("failed to get psychosis manage year config!", e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 读取 精神病模块参数设置,读取年度开始月份和年度结束月份
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryListConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<PlanInstance> list = null;
		try {
			list = vpm.getPlanInstanceExpressions(BusinessType.PSYCHOSIS, ctx);
		} catch (CreateVisitPlanException e) {
			logger.error("Failed to get psychosis plan instance records!", e);
			throw new ServiceException(e);
		}
		ArrayList<Map<String, Object>> abody = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			PlanInstance inst = list.get(i);
			String expression = inst.getExpression();
			String instanceType = "";
			Map<String, Object> result = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(expression)) {
				try {
					List<?> cnd = CNDHelper.toListCnd(expression);
					instanceType = (String) ((List<?>) cnd.get(2)).get(1);
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("instanceType", instanceType);
			result.put("planTypeCode", inst.getPlanTypeCode());
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_PsychosisConfigDetail));
		}
		res.put("body", abody);
	}

}