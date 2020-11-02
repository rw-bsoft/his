package chis.source.his;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import chis.source.util.InputStreamUtils;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;

public class HospitalDischargeService extends AbstractActionService implements
		DAOSupportable {

	private static Logger logger = LoggerFactory
			.getLogger(HospitalDischargeService.class);
	
	public void doGetHtmlData(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalDischargeModule hdModule = new HospitalDischargeModule(dao);
		try {
			hdModule.getHtmlData(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	public void doGetHtmlDataMZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		HospitalDischargeModule hdModule = new HospitalDischargeModule(dao);
		try {
			hdModule.getHtmlDataMZ(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void doQueryList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		HospitalDischargeModule hpm = new HospitalDischargeModule(dao);
		hpm.queryList(req,res,ctx);
	}
	
	public void doHospitalCostDetalsQueryYZGS(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		HospitalDischargeModule hpm = new HospitalDischargeModule(dao);
		hpm.doHospitalCostDetalsQueryYZGS(req,res,ctx);
	}
	
	public void doHospitalCostDetalsQuery(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		HospitalDischargeModule hpm = new HospitalDischargeModule(dao);
		hpm.doHospitalCostDetalsQuery(req,res,ctx);
	}
	public void doGetSkinTestHistroy(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException, ModelDataOperationException, ExpException {
		HospitalDischargeModule hpm = new HospitalDischargeModule(dao);
		hpm.getSkinTestHistroy(req,res,ctx);
	}

}
