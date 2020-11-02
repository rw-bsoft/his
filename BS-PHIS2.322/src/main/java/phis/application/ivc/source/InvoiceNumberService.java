package phis.application.ivc.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.util.context.Context;
//判断所有号码是否重复
public class InvoiceNumberService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(InvoiceNumberService.class);
	@SuppressWarnings("unchecked")
	public void doInvoiceNumberVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		InvoiceNumberModule inm = new InvoiceNumberModule(dao);
		String schemaDetailsList = (String)req.get("schemaDetailsList");
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		inm.invoiceNumberVerification(body, res, schemaDetailsList, op, dao, ctx);
	}
	//判断号码段是否冲突
	@SuppressWarnings("unchecked")
	public void doInvoiceNumberRangeVerification(Map<String, Object> req,
			Map<String, Object> res,BaseDAO dao, Context ctx)throws ModelDataOperationException{
		InvoiceNumberModule inm = new InvoiceNumberModule(dao);
			String schemaDetailsList = (String)req.get("schemaDetailsList");
			String op = (String)req.get("op");
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			inm.invoiceNumberRangeVerification(body, res, schemaDetailsList, op, dao, ctx);
	}
}
