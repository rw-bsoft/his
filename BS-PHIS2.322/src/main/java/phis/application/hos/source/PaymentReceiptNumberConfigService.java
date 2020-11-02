package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class PaymentReceiptNumberConfigService extends AbstractActionService
		implements DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(PaymentReceiptNumberConfigService.class);

	// 判断所有号码是否重复
	@SuppressWarnings("unchecked")
	public void doPaymentReceiptNumberConfigVerification(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		PaymentReceiptNumberConfigModule prncm = new PaymentReceiptNumberConfigModule(dao);
		String schemaDetailsList = (String) req.get("schemaDetailsList");
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		prncm.doPaymentReceiptNumberConfigVerification(body, res, schemaDetailsList, op,
				dao, ctx);
	}

	// 判断号码段是否冲突
	@SuppressWarnings("unchecked")
	public void doPaymentReceiptNumberConflictVerification(
			Map<String, Object> req, Map<String, Object> res, BaseDAO dao,
			Context ctx) throws ModelDataOperationException {
		PaymentReceiptNumberConfigModule prncm = new PaymentReceiptNumberConfigModule(dao);
		String schemaDetailsList = (String) req.get("schemaDetailsList");
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		prncm.doPaymentReceiptNumberConflictVerification(body, res, schemaDetailsList,
				op, dao, ctx);
	}
	// 判断号码公有参数有没有启用
		public void doQueryJksjCount(Map<String, Object> req,
				Map<String, Object> res, BaseDAO dao, Context ctx)
				throws ModelDataOperationException {
			PaymentReceiptNumberConfigModule prncm = new PaymentReceiptNumberConfigModule(dao);
			String schemaDetailsList = (String) req.get("schemaDetailsList");
			String op = (String) req.get("op");
			prncm.doQueryJksjCount(req, res, schemaDetailsList,op, dao, ctx);
		}
}
