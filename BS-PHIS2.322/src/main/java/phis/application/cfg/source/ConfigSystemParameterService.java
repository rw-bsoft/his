package phis.application.cfg.source;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.util.context.Context;

public class ConfigSystemParameterService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigSystemParameterService.class);

	public void doQueryAblePjGyQy(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigSystemParameterModule cspm = new ConfigSystemParameterModule(dao);
		cspm.doQueryAblePjGyQy(req, res);
	}
}
