/**
 * @(#)HyBaselineService.java Created on 2012-1-17 上午9:45:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.common.HttpclientUtil;
import chis.source.control.ControlRunner;
import chis.source.dr.DrApplyModel;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;


public class HypertensionBaselineService extends MDCService {
	private static final Log logger = LogFactory
			.getLog(HypertensionBaselineService.class);

	/**
	 * 保存慢阻肺询问
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public void doSaveHyBaseline(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		HypertensionBaselineModel hym = new HypertensionBaselineModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		// 判断当天是否有慢阻肺询问记录
		String empiId = (String) reqBody.get("empiId");
		String createDate = (String) reqBody.get("createDate");
		String op = (String) req.get("op");
		Long rsCount = 0L;
		if("create".equals(op)){
			try {
				rsCount = hym.CheckHasCurHyBaselineRecord(empiId, createDate);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to CheckHasCurHyBaselineRecord", e);
				res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
				res.put(RES_MESSAGE, "验证当天是否有记录失败。");
				throw new ServiceException(e);
			}
		}
		boolean hasRecord = rsCount > 0 ? true : false;
		Map<String, Object> resBody = new HashMap<String, Object>();
		if (hasRecord) {// 有记录
			resBody.put("hasRecord", hasRecord);
			res.put("body", resBody);
			return;
		}
		try {
			resBody = hym.saveHyBaselineInfo(op, reqBody, true);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存基线调查表失败。");
			throw new ServiceException(e);
		}
		res.put("body", resBody);
	}
	
	/**
	 * 根据主键获取慢阻肺随访记录
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetHyBaselineByPkey(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionBaselineModel hym = new HypertensionBaselineModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		if(reqBody.get("recordId") != null){
			try {
				resBody = hym.getHyBaselineByPkey(reqBody.get("recordId").toString());
				res.put("body", resBody);
			} catch (ModelDataOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	/**
	 * 获取慢阻肺询问页面权限控制
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetHyBaselineControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MZF_VisitRecord, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_HyBaseline control error.", e);
			throw e;
		}
		res.put("body", data);
	}
	
	public void doListHyBaselinePlanQC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionBaselineModel hym = new HypertensionBaselineModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hym.listHyBaselinePlanQC(req);
		} catch (ModelDataOperationException e) {
			logger.error("doListHyBaselinePlanQC failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
	
	public void doGetHealthCheckInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HypertensionBaselineModel hym = new HypertensionBaselineModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = hym.getHealthCheckInfo(req,res,dao,ctx);
		} catch (ModelDataOperationException e) {
			logger.error("doGetHealthCheckInfo failed.", e);
		}
		res.putAll(rsMap);
	}
	
	public void doGetJxdcUrl_HTTPPOST(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
		    throws ServiceException{
		HypertensionBaselineModel hym = new HypertensionBaselineModel(dao);
		try {
			hym.doGetJxdcUrl_HTTPPOST(req, res, ctx);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
