/**
 * @(#)RVCConfigManageService.java Created on december 30, 2010 9:46:04 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */

package chis.source.conf;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.service.ServiceCode;
import chis.source.visitplan.VisitPlanModel;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * @author <a href="mailto:yuyd@bsoft.com.cn">yuyd</a>
 * 
 */

public class RVCConfigManageService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(RVCConfigManageService.class);

	/**
	 * 保存离休干部模块参数设置相关记录
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
		VisitPlanModel vpm = new VisitPlanModel(dao);
		try {
			vpm.deletePlanInstanceByInstanceType(BusinessType.RVC);
			Map<String, Object> configMap = new HashMap<String, Object>();
			configMap.put(BusinessType.RVC + "_planTypeCode",
					body.get("RVCPlanType"));
			configMap.put("RVCStartMonth", body.get("RVCStartMonth"));
			configMap.put("RVCEndMonth", body.get("RVCEndMonth"));
			String planMode = (String) body.get("planMode");
			configMap.put(BusinessType.RVC + "_planMode", planMode);
			configMap.put(BusinessType.RVC + "_precedeDays",
					body.get("precedeDays"));
			configMap.put(BusinessType.RVC + "_delayDays",
					body.get("delayDays"));
			SystemCofigManageModel smm = new SystemCofigManageModel(dao);
			smm.saveSystemConfigData(configMap);
			// 更新随访schema
			smm.changeSchema(planMode, RVC_RetiredVeteranCadresVisit);

		} catch (Exception e) {
			logger.error("Failed to save RVC plan instance information!");
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询配置数据
	 * 
	 * @param req
	 * @param res
	 * @param session
	 * @param sc
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryConfig(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(BusinessType.RVC + "_planTypeCode", "RVCPlanType");
		map.put("RVCStartMonth", "RVCStartMonth");
		map.put("RVCAge", "RVCAge");
		map.put(BusinessType.RVC + "_planMode", "planMode");
		map.put("RVCEndMonth", "RVCEndMonth");
//		map.put(BusinessType.RVC + "_visitIntervalSame", "RVCVisitIntervalSame");
		map.put(BusinessType.RVC + "_precedeDays", "precedeDays");
		map.put(BusinessType.RVC + "_delayDays", "delayDays");
		SystemCofigManageModel smm = new SystemCofigManageModel(dao);
		try {
			Map<String, Object> reBody = smm.getSystemConfigData(map);
			res.put("body", smm.makeFormResBody(reBody, ADMIN_RVCFormConfig));
		} catch (ModelDataOperationException e) {
			logger.error("get old people  config message failed!", e);
			res.put(Service.RES_CODE, 500);
			res.put(Service.RES_MESSAGE, "获取离休干部配置信息失败!");
			throw new ServiceException("获取离休干部配置信息失败！");
		}
	}

}
