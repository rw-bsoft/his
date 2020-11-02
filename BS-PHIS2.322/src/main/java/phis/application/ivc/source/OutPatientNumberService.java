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
public class OutPatientNumberService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(OutPatientNumberService.class);
	@SuppressWarnings("unchecked")
	public void doOutPatientNumberVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		OutPatientNumberModule opnm = new OutPatientNumberModule(dao);
		String schemaDetailsList = (String)req.get("schemaDetailsList");
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		opnm.outPatientNumberVerification(body, res, schemaDetailsList, op, dao, ctx);
	}
	//判断号码段是否冲突
	@SuppressWarnings("unchecked")
	public void doOutPatientNumberRangeVerification(Map<String, Object> req,
			Map<String, Object> res,BaseDAO dao, Context ctx)throws ModelDataOperationException{
		OutPatientNumberModule opnm = new OutPatientNumberModule(dao);
			String schemaDetailsList = (String)req.get("schemaDetailsList");
			String op = (String)req.get("op");
			Map<String, Object> body = (Map<String, Object>) req.get("body");
			opnm.outPatientNumberRangeVerification(body, res, schemaDetailsList, op, dao, ctx);
	}
}
