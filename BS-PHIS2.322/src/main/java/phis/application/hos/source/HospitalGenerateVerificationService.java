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

public class HospitalGenerateVerificationService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalGenerateVerificationService.class);

	@SuppressWarnings("unchecked")
	public void doGenerateVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(
				dao);
		dsm.doGenerateVerification(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doGenerateAfterVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(
				dao);
		dsm.doGenerateAfterVerification(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doCollectVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(
				dao);
		dsm.doCollectVerification(body, res, ctx);
	}

	@SuppressWarnings("unchecked")
	public void doSaveCollect(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(
				dao);
		try {
			dsm.doSaveCollect(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void doQueryVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(
				dao);
		dsm.doQueryVerification(body, res, ctx);
	}
	
	//取消汇总查询
	public void doQueryCancelCommit(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(dao);
		dsm.doQueryCancelCommit(req, res, dao, ctx);
	}
	
	public void doCancelCommit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		HospitalGenerateVerificationModel dsm = new HospitalGenerateVerificationModel(dao);
		dsm.doCancelCommit(req, res, dao, ctx);
	}
}
