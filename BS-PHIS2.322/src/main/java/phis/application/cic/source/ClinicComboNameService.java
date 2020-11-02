package phis.application.cic.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class ClinicComboNameService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory.getLogger(ClinicComboNameService.class);
	@SuppressWarnings("unchecked")
	public void doClinicComboNameVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		ClinicComboNameModel ccnm = new ClinicComboNameModel(dao);
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ccnm.ClinicComboNameVerification(body, res,op, dao, ctx);
	}
}
