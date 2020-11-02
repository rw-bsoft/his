package phis.application.cic.source;

import java.util.Map;


import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ClinicPerscriptionService extends AbstractActionService implements
		DAOSupportable {

	@SuppressWarnings("unchecked")
	public void doPerscriptionCopyCheck(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicPerscriptionModel cmm = new ClinicPerscriptionModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			res.put("body", cmm.perscriptionCopyCheck(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException("校验处方拷贝信息失败，请联系管理员！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "success");
	}
}
