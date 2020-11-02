package phis.application.cfg.source;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class circulationMethodsModuleService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);

	@SuppressWarnings("unchecked")
	/**
	 * 修改流转方式的方式状态
	 */
	public void doSaveDisable(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException,
			ValidateException {
		circulationMethodsModule cbcm = new circulationMethodsModule(dao);
		cbcm.disable(req, res);
	}

	// 判断名称不能重复
	@SuppressWarnings("unchecked")
	public void doQueryFSMCVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		circulationMethodsModule cbcm = new circulationMethodsModule(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		cbcm.doQueryFSMCVerification(body, res, schemaDetailsList, op, dao,
				ctx);
	}
}
