/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.ivc.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 诊断信息维护
 * 
 * @author yangl</a>
 */
public class ClinicDiagnossisService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 保存诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiagnossis(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		ClinicDiagnossisModel cdm = new ClinicDiagnossisModel(dao);
		try {
			res.put("JBPB", cdm.doSaveClinicDiagnossis(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存诊断信息出错！",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doRemoveDiagnossis(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		ClinicDiagnossisModel cdm = new ClinicDiagnossisModel(dao);
		try {
			cdm.doRemoveClinicDiagnossis(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("保存诊断信息出错！",e);
		}
	}
}
