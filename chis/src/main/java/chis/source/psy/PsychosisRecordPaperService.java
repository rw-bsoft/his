/**
 * @(#)PsyRecordPaperService.java Created on 2011-3-31 下午11:36:02
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
public class PsychosisRecordPaperService extends AbstractActionService
		implements DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(PsychosisRecordPaperService.class);

	/**
	 * 保存记录纸要记录的信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSavePsyRecordPaper(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		PsychosisRecordPaperModel prpModel = new PsychosisRecordPaperModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = prpModel.saveRecordPapgerInfo(op, reqBodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Save psychosis record paper info failed.", e);
			throw new ServiceException(e);
		}
		String recordPaperId = (String) reqBodyMap.get("recordPaperId");
		if("create".equals(op)){
			recordPaperId = (String) rsMap.get("recordPaperId");
		}
		vLogService.saveVindicateLog(PSY_RecordPaper, op, recordPaperId, dao);
		res.put("body", rsMap);
	}

	/**
	 * 获取精神病记录纸权限控制
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
			data = ControlRunner.run(PSY_RecordPaper, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check PSY_RecordPaper record control error .", e);
			throw e;
		}
		resBodyMap.put("_actions", data);
		res.put("body", resBodyMap);
	}
}
