/**
 * @(#)PsychosisAnnualAssessmentService.java Created on 2012-4-5 上午9:43:58
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.psy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.control.ControlRunner;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsychosisAnnualAssessmentService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PsychosisRecordPaperService.class);

	/**
	 * 保存精神病年度评估
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePsyAnnualAssessment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		
		PsychosisAnnualAssessmentModel psyaaModel = new PsychosisAnnualAssessmentModel(
				dao);
		Map<String, Object> paaMap = null;
		try {
			paaMap = psyaaModel.savePsyAnnualAssessment(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save psychosis annual assessment failed.", e);
			throw new ServiceException(e);
		}
		String assessmentId = (String) reqBodyMap.get("assessmentId");
		if("create".equals(op)){
			assessmentId = (String) paaMap.get("assessmentId");
		}
		vLogService.saveVindicateLog(PSY_AnnualAssessment, op, assessmentId, dao);
		res.put("body", paaMap);
	}

	/**
	 * 获取精神病年度评估表单权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPsyPaperControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Object> resBodyMap = new HashMap<String, Object>();
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(PSY_AnnualAssessment, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check PSY_AnnualAssessment record control error .", e);
			throw e;
		}
		resBodyMap.put("_actions", data);
		res.put("body", resBodyMap);
	}
}
