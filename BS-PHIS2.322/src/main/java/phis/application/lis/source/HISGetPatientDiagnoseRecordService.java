package phis.application.lis.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 获取诊断信息
 * 
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class HISGetPatientDiagnoseRecordService extends AbstractActionService
		implements DAOSupportable {

	protected Logger logger = LoggerFactory
			.getLogger(HISGetPatientDiagnoseRecordService.class);
	
	/**
	 * 获取门诊诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPatientDiagnose(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PHISCommonModel phis = new PHISCommonModel(dao);
		try {
			res.putAll(phis.getPatientDiagnose(body,ctx));
		} catch (ModelOperationException e) {
			logger.error("[get patient diagnose failed!]");
		}
	}


	/**
	 * 获取住院诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetPatientHospital(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		PHISCommonModel phis = new PHISCommonModel(dao);
		try {
			res.putAll(phis.getPatientHospital(body,ctx));
		} catch (ModelOperationException e) {
			logger.error("[get patient hospital failed!]");
		}
	}
	
	/**
	 * 根据挂号科室查科室代码
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @return
	 * @throws ServiceException
	 */
	public Map<String, Object> doFindKsdm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		
		try {
			PHISCommonModel phis = new PHISCommonModel(dao);
			res.putAll(phis.findKsdm(req,ctx));
		} catch (ModelDataOperationException e) {
			logger.error("[get mzks from ms_ghks failed!]"+e.getMessage());
		}
		return res;
	}
	
	public Map<String, Object> doGetLisXTCS(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		
		try {
			PHISCommonModel phis = new PHISCommonModel(dao);
			res.putAll(phis.getLisXTCS(req,ctx));
		} catch (ModelDataOperationException e) {
			logger.error("[get mzks from ms_ghks failed!]"+e.getMessage());
		}
		return res;
	}
}
