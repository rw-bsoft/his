package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HospitalAntibacterialService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalAntibacterialService.class);

	/**
	 * 保存抗菌药物提交和作废信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveAntibactril(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalAntibacterialModel ham = new HospitalAntibacterialModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			ham.doSaveAntibactril(body, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * 查询本次住院可以使用的审批数量
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doCheckApplyInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalAntibacterialModel ham = new HospitalAntibacterialModel(dao);
		try {
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			res.put("body", ham.doCheckApplyInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 查询抗菌药物信息 打印
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalAntibacterialModel ham = new HospitalAntibacterialModel(dao);
		try {
			//String body = req.get("body")+"";
			ham.doQueryList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
