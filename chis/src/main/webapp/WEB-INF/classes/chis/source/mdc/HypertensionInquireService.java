/**
 * @(#)HypertensionInquireService.java Created on 2012-1-17 上午9:45:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 高血压询问服务类-- Hypertension Inquire Service
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HypertensionInquireService extends MDCService {
	private static final Log logger = LogFactory
			.getLog(HypertensionInquireService.class);

	/**
	 * 保存高血压询问
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHypertensionInquire(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionInquireModel him = new HypertensionInquireModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		// 判断当天是否有高血压询问记录
		String phrId = (String) reqBody.get("phrId");
		String inquireDate = (String) reqBody.get("inquireDate");
		String op = (String) req.get("op");
		Long rsCount = 0L;
		if("create".equals(op)){
			try {
				rsCount = him.CheckHasCurInquireRecord(phrId, inquireDate);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to checkHasCurVisitReocrd", e);
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "验证当天是否有询问记录失败。");
				throw new ServiceException(e);
			}
		}
		boolean hasRecord = rsCount > 0 ? true : false;
		Map<String, Object> resBody = new HashMap<String, Object>();
		if (hasRecord) {// 有随访
			resBody.put("hasRecord", hasRecord);
			res.put("body", resBody);
			return;
		}
		try {
			resBody = him.saveHypertensionInquireInfo(op, reqBody, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save hypertension inquire record failed.", e);
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存高血压询问记录失败。");
			throw new ServiceException(e);
		}
		String inquireId = (String) reqBody.get("inquireId");
		String empiId = (String) reqBody.get("empiId");
		if("create".equals("op")){
			inquireId = (String) resBody.get("inquireId");
		}
		vLogService.saveVindicateLog(MDC_HypertensionInquire, op, inquireId, dao, empiId);
		resBody.put("hasRecord", hasRecord);
		String isReferral = (String) reqBody.get("isReferral");
		int referral = 0;
		if ("2".equals(isReferral) && "create".equals(op)) {
			int constriction = (Integer) reqBody.get("constriction");
			int diastolic = (Integer) reqBody.get("diastolic");
			if (constriction < 140 && diastolic < 90) {
				// 本次正常
				referral = 0;
			} else {
				// @@ 判断是否有连续两次血压异常,如果是提示转诊。
				Map<String, Object> lhiMap = null;
				try {
					lhiMap = him.getLastHyperInquireRecord(empiId, inquireDate);
				} catch (ModelDataOperationException e) {
					logger.error(
							"Get last hypertension inquire record failed.", e);
					throw new ServiceException(e);
				}
				if (lhiMap != null && lhiMap.size() > 0) {
					int lastConstriction = (Integer) lhiMap.get("constriction");
					int lastDiastolic = (Integer) lhiMap.get("diastolic");
					if (lastConstriction < 140 && lastDiastolic < 90) {
						referral = 0;
					} else {
						referral = 1;
					}
				}
			}
		}
		resBody.put("referral", referral);
		res.put("body", resBody);
	}

	/**
	 * 获取高血压询问页面权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyperInquireControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MDC_HypertensionInquire, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HypertensionInquire control error.", e);
			throw e;
		}
		res.put("body", data);
	}
}
