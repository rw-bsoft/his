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

public class HospitalBedspaceToBedService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalBedspaceToBedService.class);

	@SuppressWarnings("unchecked")
	public void doSaveBedToBedVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalBedspaceToBedModel hbtbm = new HospitalBedspaceToBedModel(dao);
		try {
			hbtbm.doSaveBedToBedVerification(body, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e.getMessage());
		}
	}
}
