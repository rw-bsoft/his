/**
 * @(#)ChildrenConfigManageService.java Created on 2012-2-21 下午02:23:43
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.CreateVisitPlanException;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class PregnantConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(PregnantConfigManageService.class);

	/**
	 * 孕妇随访参数设置相关记录
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
		boolean visitIntervalSame = (Boolean) formBody.get("visitIntervalSame");// chb
																				// 所有孕妇随访间隔一致
		// 标志
		// chb 删除方案类型表中原有的孕妇随访参数配置数据
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			vpm.deletePlanInstanceByInstanceType(BusinessType.MATERNAL);
			String planMode = (String) formBody.get("planMode");
			if (visitIntervalSame == false && planMode.equals("1")) {
				// chb 保存提交的所有孕妇随访参数配置数据
				for (int i = 0; i < gridBody.size(); i++) {
					Map<String, Object> planBody = (HashMap<String, Object>) gridBody
							.get(i);
					String startWeek =  String.valueOf(planBody.get("startWeek"));
					String endWeek =  String.valueOf(planBody.get("endWeek"));
					String expression = "['and', ['ge', ['i', 'weeks'], ['i', "
							+ startWeek + "]], ['le', ['i', 'weeks'], ['i', "
							+ endWeek + "]]]";
					planBody.put("expression", expression);
					planBody.put("instanceType", BusinessType.MATERNAL);
					vpm.savePlanInstanceRecord("create", planBody);
				}
			}
			if (planMode.equals("2")) {
				Map<String, Object> plan = new HashMap<String, Object>();
				plan.put("instanceType", BusinessType.MATERNAL);
				plan.put("expression", SystemCofigManageModel.PLANMODE_EXPRESS);
				plan.put("planTypeCode",
						SystemCofigManageModel.PLANMODE_PLANTYPE);
				vpm.savePlanInstanceRecord("create", plan);

			}
			// chb 修改配置文件信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(BusinessType.MATERNAL + "_planTypeCode", StringUtils
					.trimToEmpty((String) formBody.get("planTypeCode")));
			map.put(BusinessType.MATERNAL + "_visitIntervalSame",
					visitIntervalSame);
			map.put(BusinessType.MATERNAL + "_planMode", planMode);
			map.put(BusinessType.PREGNANT_HIGH_RISK + "_planMode", planMode);
			int precedeDays = (Integer) formBody.get("precedeDays");
			map.put(BusinessType.MATERNAL + "_precedeDays", precedeDays);
			map.put(BusinessType.PREGNANT_HIGH_RISK + "_precedeDays",
					precedeDays);
			int delayDays = (Integer) formBody.get("delayDays");
			map.put(BusinessType.MATERNAL + "_delayDays", delayDays);
			map.put(BusinessType.PREGNANT_HIGH_RISK + "_delayDays", delayDays);
			SystemCofigManageModel smm = new SystemCofigManageModel(dao);
			smm.saveSystemConfigData(map);
			// 更新随访schema
			smm.changeSchema(planMode, MHC_VisitRecord);
		} catch (Exception e) {
			logger.error("Failed to insert plan instance records for pregnant!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "保存孕妇随访参数配置信息失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 孕妇模块参数设置相关记录
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		// chb 读取配置文件信息
		Map<String, String> map = new HashMap<String, String>();
		map.put(BusinessType.MATERNAL + "_planTypeCode", "planTypeCode");
		map.put(BusinessType.MATERNAL + "_visitIntervalSame",
				"visitIntervalSame");
		map.put(BusinessType.MATERNAL + "_planMode", "planMode");
		map.put(BusinessType.MATERNAL + "_precedeDays", "precedeDays");
		map.put(BusinessType.MATERNAL + "_delayDays", "delayDays");
		Map<String, Object> reBody;
		try {
			reBody = smm.getSystemConfigData(map);
			// chb 拼下拉框数据格式
			Map<String, Object> configMap = SchemaUtil.setDictionaryMessageForForm(
					reBody, ADMIN_PregnantConfig);
			res.put("body", configMap);
		} catch (ModelDataOperationException e) {
			logger.error("get pregnant  config message failed!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "获取孕妇配置信息失败!");
			throw new ServiceException("获取孕妇配置信息失败！");
		}
		
	}

	public void doQueryListConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<PlanInstance> list = null;
		try {
			list = vpm.getPlanInstanceExpressions(BusinessType.MATERNAL, ctx);
		} catch (CreateVisitPlanException e) {
			logger.error("Failed to get psychosis plan instance records!", e);
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
						Object startWeek = ge.get(1);
						result.put("startWeek", startWeek);
					}
					List<?> end = (List<?>) cnd.get(2);
					if (end.size() > 2) {
						List<?> lt = (List<?>) end.get(2);
						Object endWeek = lt.get(1);
						result.put("endWeek", endWeek);
					}
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("instanceType", inst.getInstanceType());
			result.put("planTypeCode", inst.getPlanTypeCode());
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_PregnantConfigDetail));
		}
		res.put("body", abody);
	}
}
