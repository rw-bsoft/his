package phis.application.cfg.source;

import java.util.Map;

import phis.source.utils.ParameterUtil;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleRemove;
import ctd.util.context.Context;

public class SystemParamsRemove extends SimpleRemove {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		super.execute(req, res, ctx);
		// 重新载入修改的参数值
		String _JGID = (String) ((Map<String, Object>) req.get("pkey"))
				.get("JGID");
		String _CSMC = (String) ((Map<String, Object>) req.get("pkey"))
				.get("CSMC");
		ParameterUtil.reloadParams(_JGID, _CSMC);
	}

}
