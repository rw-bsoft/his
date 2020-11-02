/**

 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cic.source;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class ClinicDisposalEntryService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ClinicDisposalEntryService.class);

	/**
	 * 处方组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ControllerException 
	 */
	public void doSaveDisposalEntry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException, ControllerException {
		int clinicId = 0;
		if (req.get("clinicId") != null
				&& req.get("clinicId").toString().length() > 0) {
			clinicId = Integer.parseInt(req.get("clinicId") + "");
		}
		long brid = Long.parseLong(req.get("brid") + "");
		String brxm = req.get("brxm") + "";
		int djly = Integer.parseInt(req.get("djly") + "");
		long ghgl = 0L;
		if (req.get("ghgl") != null) {
			ghgl = Long.parseLong(req.get("ghgl") + "");
		}
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		cdem.doSaveDisposalEntry(req, res, dao, ctx, clinicId, brid, brxm,
				djly, ghgl);
	}

	/**
	 * 处置明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doRemoveDisposalEntry(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int pkey = (Integer) req.get("pkey");
		String schemaList = req.get("schemaList") + "";
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		cdem.doRemoveDisposalEntry(body, pkey, schemaList, schemaDetailsList,
				res, dao, ctx);
	}

	/**
	 * 根据组套载入组套明细信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadPersonalSet(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", cdem.doLoadPersonalSet(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询组套信息失败，请联系管理员！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 根据项目序号获取药品信息（助手方式）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadCostInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			res.put("body", cdem.doLoadCostInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 根据项目序号获取全部项目信息（助手方式）
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ControllerException 
	 */
	@SuppressWarnings("unchecked")
	public void doLoadclinicAll(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ControllerException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			res.put("body", cdem.doLoadclinicAll(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取药品信息失败!", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}

	/**
	 * 根据根据病人信息和费用序号获取zfbl
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetZFBL(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		cdem.doGetZFBL(body, res, dao, ctx);
	}

	/**
	 * 根据根据就诊序号获取处置录入的ID
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doGetCZYJXH(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		cdem.doGetCZYJXH(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doCheckProjectMaterials(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> bodys = (List<Map<String, Object>>) req
				.get("bodys");
		long ghgl = 0;
		if (req.get("ghgl") != null) {
			ghgl =  Long.parseLong(req.get("ghgl")+"");
		}
		int mzzy = 0;
		if (req.get("mzzy") != null) {
			mzzy =  Integer.parseInt(req.get("mzzy")+"");
		}
		
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		cdem.doCheckProjectMaterials(bodys,ghgl,mzzy, res, ctx);
	}
	
	/**
	 * 判断是否需要审核
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryIsNeedVerify(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			cdem.doQueryIsNeedVerify(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 根据医生代码找到医生所在科室和科室名称
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doGetKsxx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ClinicDisposalEntryModule cdem = new ClinicDisposalEntryModule(dao);
		try {
			cdem.doGetKsxx(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
