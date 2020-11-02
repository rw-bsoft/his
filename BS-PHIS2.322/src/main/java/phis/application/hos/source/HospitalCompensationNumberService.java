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

public class HospitalCompensationNumberService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalCompensationNumberService.class);

	@SuppressWarnings("unchecked")
	public void doGetCompensationNumber(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		HospitalCompensationNumberModel hcnm = new HospitalCompensationNumberModel(
				dao);
		try {
			hcnm.doGetCompensationNumber(body, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		}
	}
}
