/**
 * @(#)ChildrenConfigManageService.java Created on 2012-2-16 下午04:32:43
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
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import chis.source.visitplan.PlanInstance;
import chis.source.visitplan.VisitPlanModel;

import com.alibaba.fastjson.JSONException;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description 体弱儿模块参数配置服务类
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class DebilityChildrenConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(ChildrenConfigManageService.class);
	// ^^ 体弱儿档案参数设置配置信息
	public static final String DEBILITY_CHILDREN_EXCEPTIONAL_CASE = "debilityChildrenExceptionalCase";

	/**
	 * 保存体弱儿模块参数
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
	protected void doSaveConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
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
		// 保存配置文件信息
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put(BusinessType.CD_DC + "_planTypeCode",
				fBody.get("planTypeCode"));
		configMap.put(DEBILITY_CHILDREN_EXCEPTIONAL_CASE,
				fBody.get("exceptionalCase"));
		configMap.put(BusinessType.CD_DC + "_precedeDays",
				fBody.get("precedeDays"));
		configMap
				.put(BusinessType.CD_DC + "_delayDays", fBody.get("delayDays"));
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			smm.saveSystemConfigData(configMap);

			// 判断"例外情况",如果非真,则计划不保存,并删除已有随访计划
			Object ec = fBody.get("exceptionalCase");
			VisitPlanModel vpm = new VisitPlanModel(dao);
			if (!(Boolean) ec) {
				vpm.deletePlanInstanceByInstanceType(BusinessType.CD_DC);
				logger.info("exceptionalCase is true, discard save and delete plan ...");
				return;
			}
			vpm.deletePlanInstanceByInstanceType(BusinessType.CD_DC);

			// 保存随访计划配置
			for (int i = 0; i < gBody.size(); i++) {
				Map<String, Object> planBody = gBody.get(i);
				Object debilityReason = planBody.get("debilityReason");
				if (debilityReason != null) {
					if (!StringUtils.isEmpty(debilityReason.toString())) {
						String expression = "['eq', ['s', 'type'], ['s', '"
								+ debilityReason + "']]";
						planBody.put("expression", expression);
					}
				}
				planBody.put("instanceType", BusinessType.CD_DC);
				vpm.savePlanInstanceRecord("create", planBody);
			}
		} catch (Exception e) {
			logger.error("failed to insert plan for debility children!", e);
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "保存体弱儿随访参数配置信息失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 读取 体弱儿模块参数设置
	 * 
	 * @param jsonReq
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doQueryFormConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		// 读取配置文件计划类型和例外情况
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		HashMap<String, Object> body = null;
		try {
			Map<String, String> configMap = new HashMap<String, String>();
			configMap.put(BusinessType.CD_DC + "_planTypeCode", "planTypeCode");
			configMap
					.put(DEBILITY_CHILDREN_EXCEPTIONAL_CASE, "exceptionalCase");
			configMap.put(BusinessType.CD_DC + "_precedeDays", "precedeDays");
			configMap.put(BusinessType.CD_DC + "_delayDays", "delayDays");
			body = (HashMap<String, Object>) smm.getSystemConfigData(configMap);
			res.put("body",
					smm.makeFormResBody(body, ADMIN_DebilityChildrenConfig));
		} catch (Exception e) {
			logger.error("failed to get psychosis manage year config!", e);
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "获取体弱儿配置信息失败！");
			throw new ServiceException(e);
		}
	}

	/**
	 * 读取 体弱儿模块参数设置,LIST数据读取,由于体弱儿分类来自其他表,所以重写LIST数据读取服务
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	public void doQueryListConfig(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		VisitPlanModel vpm = new VisitPlanModel(dao);
		List<PlanInstance> list = null;
		try {
			list = vpm.getPlanInstanceExpressions(BusinessType.CD_DC, ctx);
		} catch (Exception e) {
			logger.error("failed to get debility children plan!", e);
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "获取体弱儿计划方案总数失败！");
			throw new ServiceException(e);
		}

		// 分页
		ArrayList<Map<String, Object>> abody = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> result = new HashMap<String, Object>();
			PlanInstance inst = list.get(i);
			// 特殊处理表达式字段,拆分出体弱儿分类字段数据
			String expression = inst.getExpression();
			String debilityReason = "";
			if (!StringUtils.isEmpty(expression)) {
				try {
					List<?> cnd = CNDHelper.toListCnd(expression);
					debilityReason = (String) ((List<?>) cnd.get(2)).get(1);
					result.put("debilityReason", debilityReason);
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("debilityReason", debilityReason);
			result.put("planTypeCode", inst.getPlanTypeCode());
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_DebilityChildrenConfigDetail));
		}
		res.put("body", abody);
	}
}
