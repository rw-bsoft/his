package phis.application.cic.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ClinicHospitalAppointmentService extends AbstractActionService
		implements DAOSupportable {
	public void doQueryYyksInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(
				dao);
		try {
			cham.doQueryYyksInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("引入获取数据失败！", e);
		}
	}

	public void doQueryBrryinfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(
				dao);
		try {
			cham.doQueryBrryinfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("引入获取数据失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doSaveHospitalAppointment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(
				dao);
		try {
			cham.doSaveHospitalAppointment(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doUpdateHospitalAppointment(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(
				dao);
		try {
			cham.doUpdateHospitalAppointment(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doSaveInpatientCertificate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(dao);
		try {
			cham.doSaveInpatientCertificate(body, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}

	public void doUpdateInpatientCertificate(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(dao);
		try {
			cham.doUpdateInpatientCertificate(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	public void doQueryZDMC(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicHospitalAppointmentModel cham = new ClinicHospitalAppointmentModel(
				dao);
		try {
			cham.doQueryZDMC(req, res, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
}
