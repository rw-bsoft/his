/**
 * @description 病人性质
 * 
 * @author zhangyq 2012.05.30
 */
package phis.application.cfg.source;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.dictionary.DictionaryController;
import ctd.util.context.Context;

public class ConfigPaymentCategoryService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPaymentCategoryService.class);

	@SuppressWarnings("unchecked")
	public void doPaymentCategoryVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigPaymentCategoryModel cpcm = new ConfigPaymentCategoryModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		cpcm.paymentCategoryVerification(body, res, schemaDetailsList, op, dao,
				ctx);
	}
	
	public void doResetFkfsDic(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		DictionaryController.instance().reload("phis.dictionary.payCategoryZy");
		DictionaryController.instance().reload("phis.dictionary.payCategory");
	}

	@SuppressWarnings("unchecked")
	public void doPaymentCategoryDelVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigPaymentCategoryModel cpcm = new ConfigPaymentCategoryModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		cpcm.paymentCategoryDelVerification(body, res, schemaDetailsList, op,
				dao, ctx);
	}
}
