package chis.source.his;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.controller.exception.ControllerException;
import ctd.dao.SimpleDAO;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.gdr.GroupDinnerRecordService;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;

public class MedicalHistoryService extends AbstractActionService implements
		DAOSupportable {
	private static final Logger logger = LoggerFactory
			.getLogger(MedicalHistoryService.class);

	/**
	 * 载入病历首页信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doLoadClinicInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		MedicalHistoryModel cmm = new MedicalHistoryModel(dao);
		try {
			res.putAll(cmm.loadClinicInfo(body, ctx));
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

	public void doQueryHistoryList(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		MedicalHistoryModel cmm = new MedicalHistoryModel(dao);
		try {
			cmm.doQueryHistoryList(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}

}
