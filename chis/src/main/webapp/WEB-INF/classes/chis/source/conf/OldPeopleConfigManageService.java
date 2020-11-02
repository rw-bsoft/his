/**
 * @(#)OldPeopleConfigManageService.java Created on december 30, 2010 9:46:04 AM
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
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

/**
 * @description
 * @author <a href="mailto:yuyd@bsoft.com.cn">yuyd</a>
 * 
 */

public class OldPeopleConfigManageService extends AbstractActionService
		implements DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(OldPeopleConfigManageService.class);

	/**
	 * 保存老年人模块参数设置相关记录
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
		if (body == null || body.size() < 1) {
			logger.error("body  is missing!");
			res.put(RES_CODE, ServiceCode.CODE_INVALID_REQUEST);
			res.put(RES_MESSAGE, "请求数据丢失！");
			return;
		}
		Map<String, Object> formBody = (HashMap<String, Object>) body
				.get("form");
		List<Map<String, Object>> gridBody = new ArrayList<Map<String, Object>>();
		if (formBody.get("planMode").equals("1")) {
			gridBody = (ArrayList<Map<String, Object>>) body.get("grid");
		} else if (formBody.get("planMode").equals("2")) {
			Map<String, Object> json = new HashMap<String, Object>();
			json.put("expression", SystemCofigManageModel.PLANMODE_EXPRESS);
			json.put("planTypeCode", SystemCofigManageModel.PLANMODE_PLANTYPE);
			gridBody.add(json);
		}
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			vpm.deletePlanInstanceByInstanceType(BusinessType.LNR);
			boolean same = (Boolean) formBody.get("oldPeopleVisitIntervalSame");
			if (!same) {
				if (gridBody != null) {
					for (int i = 0; i < gridBody.size(); i++) {
						Map<String, Object> planBody = (HashMap<String, Object>) gridBody
								.get(i);
						String startAge = (String) planBody
								.get("oldPeopleStartAge");
						String endAge = (String) planBody
								.get("oldPeopleEndAge");
						String expression = "['and', ['ge', ['i', 'age'], ['i', "
								+ startAge
								+ "]], ['le', ['i', 'age'], ['i', "
								+ endAge + "]]]";
						planBody.put("expression", expression);
						planBody.put("instanceType", BusinessType.LNR);
						vpm.savePlanInstanceRecord("create", planBody);
					}
				}
			}
			Map<String, Object> configMap = new HashMap<String, Object>();
			configMap.put(BusinessType.LNR + "_planTypeCode",
					formBody.get("oldPeoplePlanType"));
			configMap.put("oldPeopleStartMonth",
					formBody.get("oldPeopleStartMonth"));
			configMap.put("oldPeopleAge", formBody.get("oldPeopleAge"));
			configMap.put("oldPeopleEndMonth",
					formBody.get("oldPeopleEndMonth"));
			configMap.put(BusinessType.LNR + "_visitIntervalSame",
					formBody.get("oldPeopleVisitIntervalSame"));
			String planMode = (String) formBody.get("planMode");
			configMap.put(BusinessType.LNR + "_planMode", planMode);
			configMap.put(BusinessType.LNR + "_precedeDays",
					formBody.get("precedeDays"));
			configMap.put(BusinessType.LNR + "_delayDays",
					formBody.get("delayDays"));
			SystemCofigManageModel smm = new SystemCofigManageModel(dao);
			smm.saveSystemConfigData(configMap);
			// 更新随访schema
			smm.changeSchema(planMode, MDC_OldPeopleVisit);

		} catch (Exception e) {
			logger.error("Failed to save oldpeople plan instance information!");
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询老年人spring-context.xml配置数据
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
		Map<String, String> map = new HashMap<String, String>();
		map.put(BusinessType.LNR + "_planTypeCode", "oldPeoplePlanType");
		map.put("oldPeopleStartMonth", "oldPeopleStartMonth");
		map.put("oldPeopleAge", "oldPeopleAge");
		map.put(BusinessType.LNR + "_planMode", "planMode");
		map.put("oldPeopleEndMonth", "oldPeopleEndMonth");
		map.put(BusinessType.LNR + "_visitIntervalSame",
				"oldPeopleVisitIntervalSame");
		map.put(BusinessType.LNR + "_precedeDays", "precedeDays");
		map.put(BusinessType.LNR + "_delayDays", "delayDays");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			Map<String, Object> reBody = smm.getSystemConfigData(map);
			res.put("body", smm.makeFormResBody(reBody, ADMIN_OldPeopleFormConfig));
		} catch (ModelDataOperationException e) {
			logger.error("get old people  config message failed!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "获取老年人配置信息失败!");
			throw new ServiceException("获取老年人配置信息失败！");
		}
	}

	/**
	 * 读取 老年人模块参数设置,LIST数据读取
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
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		List<Map<String, Object>> list = null;
		try {
			list = smm.getPlanInstanceForOld(BusinessType.LNR, null);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to get debility children plan!", e);
			throw new ServiceException(e);
		}
		ArrayList<Map<String, Object>> abody = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> inst = list.get(i);
			String expression = (String) inst.get("expression");
			Map<String, Object> result = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(expression)) {
				try {
					List<?> cnd = CNDHelper.toListCnd(expression);
					List<?> start = (List<?>) cnd.get(1);
					if (start.size() > 2) {
						List<?> ge = (List<?>) start.get(2);
						Object oldPeopleStartAge = ge.get(1);
						result.put("oldPeopleStartAge", oldPeopleStartAge);
					}
					List<?> end = (List<?>) cnd.get(2);
					if (end.size() > 2) {
						List<?> lt = (List<?>) end.get(2);
						Object oldPeopleEndAge = lt.get(1);
						result.put("oldPeopleEndAge", oldPeopleEndAge);
					}
				} catch (ExpException e) {
					e.printStackTrace();
				}
			}
			result.put("planTypeCode", inst.get("planTypeCode"));
			abody.add(SchemaUtil.setDictionaryMessageForList(result,
					ADMIN_OldPeopleListConfig));
		}
		res.put("body", abody);
	}

	/**
	 * 查询老年人spring-context.xml配置数据 老年人年龄
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryConfigAge(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("oldPeopleAge", "oldPeopleAge");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		Map<String, Object> reBody;
		try {
			reBody = smm.getSystemConfigData(map);
			res.put("body", reBody);
		} catch (ModelDataOperationException e) {
			logger.error("get old people age config message failed!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "获取老年人年龄配置信息失败!");
			throw new ServiceException("获取老年人年龄配置信息失败！");
		}

	}
}
