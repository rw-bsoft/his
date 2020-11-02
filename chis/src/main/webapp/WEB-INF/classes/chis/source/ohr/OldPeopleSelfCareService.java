/**
 * @(#)OldPeopleSelfCareService.java Created on 2013年8月5日 下午1:53:45
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class OldPeopleSelfCareService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(OldPeopleSelfCareService.class);

	/**
	 * 
	 * @Description:保存老年人自我评估信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @author ChenXianRui 2013年8月5日 下午2:23:28
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public void doSaveOldPeopleSelfCare(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqbodyMap = (Map<String, Object>) req.get("body");
		String op = (String) req.get("op");
		String SCID = (String) reqbodyMap.get("SCID");
		String empiId = (String) reqbodyMap.get("empiId");
		OldPeopleSelfCareModel opscModel = new OldPeopleSelfCareModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = opscModel.saveOPSC(op, reqbodyMap, true);
		} catch (ModelDataOperationException e) {
			logger.error("Failed to save old people self care.", e);
			throw new ServiceException(e);
		}
		res.put("body", rsMap);
		if("create".equals(op)){
			SCID = (String) rsMap.get("SCID");
		}
		vLogService.saveVindicateLog(MDC_OldPeopleSelfCare, op, SCID, dao, empiId);
		//标识签约任务完成
		this.finishSCServiceTask(empiId, LNRZLPG_LNRFW, null, dao);
	}
}
