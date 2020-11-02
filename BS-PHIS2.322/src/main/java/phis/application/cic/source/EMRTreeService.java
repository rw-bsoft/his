package phis.application.cic.source;

import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class EMRTreeService extends AbstractActionService implements
		DAOSupportable {
	public void doLoadNavTree(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EMRTreeModule module = new EMRTreeModule(dao);
		Map<String, Object> map = module.doLoadNavTree(req, res, ctx);
		res.put("tjjyTree", map.get("tjjyTree"));
		res.put("pacsTree", map.get("pacsTree"));
		res.put("pacsTree_zy", map.get("pacsTree_zy"));
	}
}
