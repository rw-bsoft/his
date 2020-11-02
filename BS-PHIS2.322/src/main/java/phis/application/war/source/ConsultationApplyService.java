/**
 * @(#)ConsultationApplyService.java Created on 2013-5-2 下午9:02:46
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package phis.application.war.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.Constants;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class ConsultationApplyService extends AbstractActionService implements
		DAOSupportable {

	protected Logger logger = LoggerFactory
			.getLogger(ConsultationApplyService.class);

	/**
	 * @Description:初始化会诊申请页面
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-3 下午7:36:10
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doInitConsModule(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Integer ZYH = (Integer) reqBodyMap.get("ZYH");
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnitId = user.getManageUnitId();
		ConsultationApplyModel caModel = new ConsultationApplyModel(dao);

		Map<String, Object> brMap = null;
		try {
			brMap = caModel.getBRInfo(ZYH.longValue(), manageUnitId);
		} catch (ModelDataOperationException e) {
			logger.error("Get patient info of consultation apply failed.", e);
			throw new ServiceException(e);
		}
		Map<String, Object> brInfoMap = new HashMap<String, Object>();
		if (brMap != null && brMap.size() > 0) {
			brInfoMap = SchemaUtil.setDictionaryMassageForForm(brMap,
					BSPHISEntryNames.ZY_BRRY_CONS);
		}
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put(ZY_BRRY_CONS + Constants.DATAFORMAT4FORM, brInfoMap);

		List<Map<String, Object>> bqList = null;
		try {
			bqList = caModel.getBQInfo(user.getManageUnit().getRef());
		} catch (ModelDataOperationException e) {
			logger.error("Get Ward Info of consultation apply failed.", e);
			throw new ServiceException(e);
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (bqList != null && bqList.size() > 0) {
			list = SchemaUtil.setDictionaryMassageForList(bqList,
					BSPHISEntryNames.GY_KSDM_CONS);
		}
		resBodyMap.put(GY_KSDM_CONS + Constants.DATAFORMAT4LIST, list);

		res.put("body", resBodyMap);
	}

	/**
	 * 
	 * @Description:保存会诊申请
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013-5-4 下午5:05:40
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveConsApply(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		boolean success = false;
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Integer HZKS = (Integer) reqBodyMap.get("HZKS");
		String zyhStr = (String) reqBodyMap.get("ZYH");
		long ZYH = Long.parseLong(zyhStr);
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnitId = user.getManageUnitId();
		ConsultationApplyModel caModel = new ConsultationApplyModel(dao);
		try {
			caModel.updateConsApply(HZKS.longValue(), ZYH, manageUnitId);
		} catch (ModelDataOperationException e) {
			logger.error("Save consultation apply info failed.", e);
			throw new ServiceException(e);
		}
		success = true;
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		resBodyMap.put("success", success);
		res.put("body", resBodyMap);
	}
}
