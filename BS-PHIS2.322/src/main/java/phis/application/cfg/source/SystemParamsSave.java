package phis.application.cfg.source;

import java.util.Map;

import phis.source.utils.ParameterUtil;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleSave;
import ctd.util.context.Context;

public class SystemParamsSave extends SimpleSave {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		super.execute(req, res, ctx);
		String _JGID = (String) ((Map<String, Object>) req.get("body"))
				.get("JGID");
		String _CSMC = (String) ((Map<String, Object>) req.get("body"))
				.get("CSMC");
		ParameterUtil.reloadParams(_JGID, _CSMC);
	}

}
