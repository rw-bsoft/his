package phis.application.reg.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class AnAppointmentService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(AnAppointmentService.class);

	/**
	 * 病人查询
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryPerson(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		AnAppointmentModel aam = new AnAppointmentModel(dao);
		try {
			aam.doQueryPerson(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("患者信息查询失败！", e);
		}
	}

	/**
	 * 预约病人查询 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doGetAnAppointment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		AnAppointmentModel aam = new AnAppointmentModel(dao);
		aam.doGetAnAppointment(req, res, ctx);
	}
}
