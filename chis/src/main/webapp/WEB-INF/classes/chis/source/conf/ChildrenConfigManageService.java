/**
 * @(#)ChildrenConfigManageService.java Created on 2012-2-16 下午02:23:43
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

import com.alibaba.fastjson.JSONException;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * 
 * @author <a href="mailto:chenhb@bsoft.com.cn">chenhuabin</a>
 */
public class ChildrenConfigManageService extends AbstractActionService
		implements DAOSupportable {
	private static Logger logger = LoggerFactory
			.getLogger(ChildrenConfigManageService.class);
	// $$ 儿童档案参数设置配置信息
	public static final String CHILDREN_REGISTER_AGE = "childrenRegisterAge";
	public static final String CHILDREN_DIE_AGE = "childrenDieAge";
	public static final String CHILDREN_FIRST_VISIT_DAYS = "childrenFirstVistDays";

	/**
	 * 儿童模块参数设置相关记录
	 * 
	 * @param rqe
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws JSONException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected void doSaveConfig(Map<String, Object> rqe,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (HashMap<String, Object>) rqe.get("body");
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
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			// chb 删除方案类型表中原有的儿童参数配置数据
			vpm.deletePlanInstanceByInstanceType(BusinessType.CD_IQ);
			vpm.deletePlanInstanceByInstanceType(BusinessType.CD_CU);
		} catch (ModelDataOperationException e) {
			logger.error("failed to delete the old plan type records for chilren!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "保存儿童随访参数配置信息失败！");
			throw new ServiceException(e);
		}
		// chb 所有月龄随访间隔一致标志
		boolean same = (Boolean) formBody.get("visitIntervalSame");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		if (!same) {
			// chb 保存提交的所有儿童管理参数配置数据
			for (int i = 0; i < gridBody.size(); i++) {
				Map<String, Object> planBody = gridBody.get(i);
				String startMonth = String.valueOf(planBody.get("startMonth"));
				String endMonth = String.valueOf(planBody.get("endMonth"));
				String expression = "['and', ['ge', ['i', 'months'], ['i', "
						+ startMonth + "]], ['le', ['i', 'months'], ['i', "
						+ endMonth + "]]]";
				planBody.put("expression", expression);
				try {
					// chb 保存儿童询问计划
					Map<String, Object> data1 = new HashMap<String, Object>(
							planBody);
					data1.put("instanceType", BusinessType.CD_IQ);
					vpm.savePlanInstanceRecord("create", data1);
					// chb 保存儿童体格检查计划
					Map<String, Object> data2 = new HashMap<String, Object>(
							planBody);
					data2.put("instanceType", BusinessType.CD_CU);
					vpm.savePlanInstanceRecord("create", data2);

				} catch (ModelDataOperationException e) {
					logger.error("failed to insert plan instance records!");
					res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
					res.put(RES_MESSAGE, "保存儿童随访参数配置信息失败！");
					throw new ServiceException(e);
				}
			}
		}
//		// chb 首次随访天数修改后处理计划业务数据
//		try {
//			smm.updatebChildrenBusinessData(formBody, CHILDREN_FIRST_VISIT_DAYS);
//		} catch (ModelDataOperationException e) {
//			logger.error("failed to update children business records!");
//			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
//			res.put(RES_MESSAGE, "保存儿童业务数据失败！");
//			throw new ServiceException(e);
//		}

		// chb 修改配置文件信息
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put(CHILDREN_REGISTER_AGE,
				formBody.get("childrenRegisterAge"));
		configMap.put(CHILDREN_DIE_AGE, formBody.get("childrenDieAge"));
		configMap.put(CHILDREN_FIRST_VISIT_DAYS,
				formBody.get("childrenFirstVistDays"));
		String planTypeCode = (String) formBody.get("planTypeCode");
		configMap.put(BusinessType.CD_IQ + "_planTypeCode", planTypeCode);
		configMap.put(BusinessType.CD_CU + "_planTypeCode", planTypeCode);
		boolean visitIntervalSame = (Boolean) formBody.get("visitIntervalSame");
		configMap.put(BusinessType.CD_IQ + "_visitIntervalSame",
				visitIntervalSame);
		configMap.put(BusinessType.CD_CU + "_visitIntervalSame",
				visitIntervalSame);
		int precedeDays = (Integer) formBody.get("precedeDays");
		configMap.put(BusinessType.CD_IQ + "_precedeDays", precedeDays);
		configMap.put(BusinessType.CD_CU + "_precedeDays", precedeDays);
		int delayDays = (Integer) formBody.get("delayDays");
		configMap.put(BusinessType.CD_IQ + "_delayDays", delayDays);
		configMap.put(BusinessType.CD_CU + "_delayDays", delayDays);
		try {
			smm.saveSystemConfigData(configMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 儿童模块参数设置相关记录
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException 
	 */
	protected void doQueryConfig(Map<String, Object> rqe,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException {
		// chb 读取配置文件信息
		Map<String, String> map = new HashMap<String, String>();
		map.put(CHILDREN_REGISTER_AGE, "childrenRegisterAge");
		map.put(CHILDREN_DIE_AGE, "childrenDieAge");
		map.put(CHILDREN_FIRST_VISIT_DAYS, "childrenFirstVistDays");
		map.put(BusinessType.CD_IQ + "_planTypeCode", "planTypeCode");
		map.put(BusinessType.CD_IQ + "_visitIntervalSame", "visitIntervalSame");
		map.put(BusinessType.CD_IQ + "_precedeDays", "precedeDays");
		map.put(BusinessType.CD_IQ + "_delayDays", "delayDays");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> data = null;
		try {
			data = smm.getSystemConfigData(map);
		} catch (ModelDataOperationException e) {
			logger.error("failed to get children config data !");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "获取儿童随访参数配置信息失败！");
			throw new ServiceException(e);
		}
		// chb 拼下拉框数据格式
		Map<String, Object> reBody = SchemaUtil.setDictionaryMessageForForm(
				data, ADMIN_ChildrenConfig);
		res.put("body", reBody);
	}

	/**
	 * 获取参数列表数据
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
			list = vpm.getPlanInstanceExpressions(BusinessType.CD_IQ, ctx);
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
						Object startMonth = ge.get(1);
						result.put("startMonth", startMonth);
					}
					List<?> end = (List<?>) cnd.get(2);
					if (end.size() > 2) {
						List<?> lt = (List<?>) end.get(2);
						Object endMonth = lt.get(1);
						result.put("endMonth", endMonth);
					}
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("instanceType", inst.getInstanceType());
			result.put("planTypeCode", inst.getPlanTypeCode());
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_ChildrenConfigDetail));
		}
		res.put("body", abody);
	}
}
