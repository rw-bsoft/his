/**
 * @(#)TcmService.java Created on 2018-07-11 上午10:26:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.tcm.source;

import java.util.Map;

import phis.application.ccl.source.CheckApplyModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 省中医馆平台接口调用
 * 
 * @author zhaojian</a>
 */
public class TcmService extends AbstractActionService implements
		DAOSupportable {

	/**
	 * 查询需推送到省中医馆平台的草药信息条数
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doHerbalMedicineCount(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doHerbalMedicineCount(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询需推送到省中医馆平台的草药信息条数出错！",e);
		}
	}
	/**
	 * 推送草药信息到省中医馆平台
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUploadHerbalMedicine(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doUploadHerbalMedicine(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("推送草药信息到省中医馆平台出错！",e);
		}
	}
	/**
	 * 查询未匹配的西医诊断（HIS）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDiagnosisContrastHIS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue =tcm.doQueryDiagnosisContrastHIS(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的西医诊断（HIS）出错！",e);
		}
	}
	/**
	 * 查询未匹配的西医诊断（TCM）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryDiagnosisContrastTCM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue = tcm.doQueryDiagnosisContrastTCM(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的西医诊断（TCM）出错！",e);
		}
	}

	/**
	 * 删除已对照疾病诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doRemoveDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 保存对照的疾病诊断信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doSaveDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 自动匹配西医诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doAuotMatchingDiagnosisContrast(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doAuotMatchingDiagnosisContrast(req,res,ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自动匹配西医诊断信息出错！",e);
		}
	}
	
	

	/**
	 * 查询未匹配的中医诊断（HIS）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZyDiagnosisContrastHIS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue =tcm.doQueryZyDiagnosisContrastHIS(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的中医诊断（HIS）出错！",e);
		}
	}
	/**
	 * 查询未匹配的西医诊断（TCM）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZyDiagnosisContrastTCM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue = tcm.doQueryZyDiagnosisContrastTCM(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的中医诊断（TCM）出错！",e);
		}
	}

	/**
	 * 删除已对照中医疾病诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveZyDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doRemoveZyDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 保存对照的中医疾病诊断信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZyDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doSaveZyDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 自动匹配中医诊断信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doAuotMatchingZyDiagnosisContrast(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doAuotMatchingZyDiagnosisContrast(req,res,ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自动匹配中医诊断信息出错！",e);
		}
	}
	
	


	/**
	 * 查询未匹配的中医证候（HIS）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZhDiagnosisContrastHIS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue =tcm.doQueryZhDiagnosisContrastHIS(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的中医证候（HIS）出错！",e);
		}
	}
	/**
	 * 查询未匹配的西医诊断（TCM）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryZhDiagnosisContrastTCM(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			Map<String, Object> dataValue = tcm.doQueryZhDiagnosisContrastTCM(req,res,ctx);
			res.putAll(dataValue);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询未匹配的中医证候（TCM）出错！",e);
		}
	}

	/**
	 * 删除已对照中医证候诊断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveZhDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doRemoveZhDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 保存对照的中医证候诊断信息
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveZhDiagnosisContrastDz(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			tcm.doSaveZhDiagnosisContrastDz(body,res,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 自动匹配中医证候信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doAuotMatchingZhDiagnosisContrast(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doAuotMatchingZhDiagnosisContrast(req,res,ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("自动匹配中医证候信息出错！",e);
		}
	}
	/**
	 * 获取跨域url页面内容
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetUrlPageContent(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doGetUrlPageContent(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取跨域url页面内容出错！",e);
		}
	}
	/**
	 * 推送患者信息到省中医馆平台
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doUploadBrxxToTcm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doUploadBrxxToTcm(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("推送患者信息到省中医馆平台出错！",e);
		}
	}
	/**
	 * 根据jgid获取对照的省中医馆平台机构编码
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetTcmJgid(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doGetTcmJgid(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("根据jgid获取对照的省中医馆平台机构编码出错！",e);
		}
	}
	/**
	 * 从省中医馆平台获取电子病历信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetDzblFromTcm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doGetDzblFromTcm(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("从省中医馆平台获取电子病历信息出错！",e);
		}
	}
	/**
	 * 从省中医馆平台获取处方信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetCfxxFromTcm(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String,Object> body = (Map<String, Object>) req.get("body");
		TcmModel tcm = new TcmModel(dao);
		try {
			res.put("body", tcm.doGetCfxxFromTcm(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("从省中医馆平台获取处方信息出错！",e);
		}
	}
}
