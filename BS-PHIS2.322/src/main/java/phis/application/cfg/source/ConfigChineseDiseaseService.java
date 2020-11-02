package phis.application.cfg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.util.context.Context;

public class ConfigChineseDiseaseService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigChineseDiseaseService.class);

	public void doSaveChineseDiseaseUnion(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigChineseDiseaseModel ccd = new ConfigChineseDiseaseModel(dao);
		ccd.doSaveChineseDiseaseUnion(req, res, ctx);
	}

}
