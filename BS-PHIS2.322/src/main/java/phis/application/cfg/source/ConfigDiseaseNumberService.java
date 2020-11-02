package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.util.context.Context;

public class ConfigDiseaseNumberService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory.getLogger(ConfigDiseaseNumberService.class);
	@SuppressWarnings("unchecked")
	public void doICD10NumberAndJBMCVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		ConfigDiseaseNumberModel inajm = new ConfigDiseaseNumberModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		inajm.iCD10NumberAndJBMCVerification(body, res,schemaDetailsList,op, dao, ctx);
	}
	
	/**
	 * 中医疾病保存前判断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doZYJBVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		ConfigDiseaseNumberModel inajm = new ConfigDiseaseNumberModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		inajm.ZYJBVerification(body, res,schemaDetailsList,op, dao, ctx);
	}
	/**
	 * 中医证侯保存前判断
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doZYZHVerification(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		ConfigDiseaseNumberModel inajm = new ConfigDiseaseNumberModel(dao);
		String schemaDetailsList = req.get("schemaDetailsList") + "";
		String op = (String)req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		inajm.ZYZHVerification(body, res,schemaDetailsList,op, dao, ctx);
	}
}
