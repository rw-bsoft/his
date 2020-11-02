/**
 * @(#)VisitMZFService.java Created on 2012-1-17 上午9:45:47
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.text.ParseException;
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
 * @description 慢阻肺询问服务类-- MZF Inquire Service
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MZFVisitService extends MDCService {
	private static final Log logger = LogFactory
			.getLog(MZFVisitService.class);

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
	public void doSaveVisitMZF(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ParseException {
		MZFVisitModel mvm = new MZFVisitModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		// 判断当天是否有慢阻肺询问记录
		String phrId = (String) reqBody.get("phrId");
		String SFRQ = (String) reqBody.get("SFRQ");
		String op = (String) req.get("op");
		Long rsCount = 0L;
		if("create".equals(op)){
			try {
				rsCount = mvm.CheckHasCurInquireRecord(phrId, SFRQ);
			} catch (ModelDataOperationException e) {
				logger.error("Failed to checkHasCurVisitReocrd", e);
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
			resBody = mvm.saveVisitMZFInfo(op, reqBody, true);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, Constants.CODE_DATABASE_ERROR);
			res.put(RES_MESSAGE, "保存慢阻肺记录失败。");
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
	public void doGetVisitMZFByPkey(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MZFVisitModel mvm = new MZFVisitModel(dao);
		Map<String, Object> reqBody = (Map<String, Object>) req.get("body");
		Map<String, Object> resBody = new HashMap<String, Object>();
		if(reqBody.get("visitId") != null){
			try {
				resBody = mvm.getVisitMZFByPkey(reqBody.get("visitId").toString());
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
	public void doGetVisitMZFControl(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> reqBodyMap = (Map<String, Object>) req.get("body");
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		try {
			data = ControlRunner.run(MZF_VisitRecord, reqBodyMap, ctx,
					ControlRunner.CREATE, ControlRunner.UPDATE);
		} catch (ServiceException e) {
			logger.error("check MDC_VisitMZF control error.", e);
			throw e;
		}
		res.put("body", data);
	}
	
	public void doListMZFVisitPlanQC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MZFVisitModel mvm = new MZFVisitModel(dao);
		Map<String, Object> rsMap = null;
		try {
			rsMap = mvm.listMZFVisitPlanQC(req);
		} catch (ModelDataOperationException e) {
			logger.error("list MZF Vist Plan failed.", e);
			throw new ServiceException(e);
		}
		res.putAll(rsMap);
	}
}
