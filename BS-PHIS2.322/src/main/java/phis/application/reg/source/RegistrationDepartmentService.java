package phis.application.reg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class RegistrationDepartmentService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(RegistrationDepartmentService.class);

	/**
	 * add by shiwy
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doRegistrationDepartmentVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		RegistrationDepartmentModel inajm = new RegistrationDepartmentModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		inajm.registrationDepartmentVerification(body, res, schemaDetailsList,
				op, dao, ctx);
	}

	/**
	 * add by zhanyq 挂号科室保存
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveRegistrationDepartment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegistrationDepartmentModel rdm = new RegistrationDepartmentModel(dao);
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String, Object> kenGen = rdm.doSaveRegistrationDepartment(op,
					body, ctx);
			if (op.equals("create")) {
				body.put("KSDM", kenGen.get("KSDM"));
			}
			res.put("body", body);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("挂号科室保存失败！", e);
		}
	}

	public void doRemoveRegistrationDepartment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RegistrationDepartmentModel rdm = new RegistrationDepartmentModel(dao);
		Object pkey = req.get("pkey");
		try {
			rdm.doRemoveRegistrationDepartment(pkey);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
