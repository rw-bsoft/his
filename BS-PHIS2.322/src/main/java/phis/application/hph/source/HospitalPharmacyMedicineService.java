package phis.application.hph.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
public class HospitalPharmacyMedicineService extends AbstractActionService implements DAOSupportable{
	/**
	 * 
	 * @author taojh
	 * @createDate 2014-03-11
	 * @description 按病区发药汇总查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryCollectRecordsHZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyHistoryDispensingCollectModel model = new HospitalPharmacyHistoryDispensingCollectModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryCollectRecordsHZ(body,req,ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	public void doQueryBRFYXX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPharmacyMedicineModel model = new HospitalPharmacyMedicineModel(dao);
		try {
			model.queryBRFYXX(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	public void doQueryBRFYMX(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPharmacyMedicineModel model = new HospitalPharmacyMedicineModel(dao);
		try {
			model.queryBRFYXM(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public void doQueryCollectRecordsBackHZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		HospitalPharmacyHistoryDispensingCollectModel model = new HospitalPharmacyHistoryDispensingCollectModel(dao);
		Map<String,Object> body = (Map<String,Object>) req.get("body");
		try {
			Map<String,Object> ret=model.queryCollectRecordsBackHZ(body,req,ctx);
			res.put("totalCount", ret.get("totalCount"));
			res.put("body", ret.get("body"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	public void doQueryBRFYMXBack(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		HospitalPharmacyMedicineModel model = new HospitalPharmacyMedicineModel(dao);
		try {
			model.queryBRFYXMBack(req, res, dao, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
}
