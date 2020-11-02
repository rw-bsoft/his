/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.chis.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

/**
 * @description 公卫获取医疗数据
 * 
 * @author zhangyq 2012.05.28
 */
public class PhisToChisService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(PhisToChisService.class);

	/**
	 * 根据病人id查询最近开处方时间
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doGetLastCfDate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		PhisToChisModule ccm = new PhisToChisModule(dao);
		ccm.doGetLastCfDate(req, res, dao, ctx);
	}

//	/**
//	 * 家床诊疗计划组套明细删除
//	 * 
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 */
//	@SuppressWarnings("unchecked")
//	public void doRemovePrescriptionDetails(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		long pkey = Long.parseLong( req.get("pkey").toString());
//		String schemaList = req.get("schemaList") + "";
//		String schemaDetailsList = req.get("schemaDetailsList") + "";
//		PhisToChisModule ccm = new PhisToChisModule(dao);
//		ccm.doRemovePrescriptionDetails(body, pkey, schemaList,
//				schemaDetailsList, res, dao, ctx);
//	}
//
//	/**
//	 * 家床诊疗计划组套启用
//	 * 
//	 * @param req
//	 * @param res
//	 * @param dao
//	 * @param ctx
//	 */
//	@SuppressWarnings("unchecked")
//	public void doUpdatePrescriptionStack(Map<String, Object> req,
//			Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		String schemaList = req.get("schemaList") + "";
//		String schemaDetailsList = req.get("schemaDetailsList") + "";
//		PhisToChisModule ccm = new PhisToChisModule(dao);
//		ccm.savePersonalComboExecute(body, schemaList, schemaDetailsList, res,
//				dao, ctx);
//	}
}
