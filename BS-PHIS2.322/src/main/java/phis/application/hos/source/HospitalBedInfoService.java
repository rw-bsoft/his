package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.util.context.Context;

public class HospitalBedInfoService extends AbstractActionService implements
DAOSupportable{
	protected Logger logger = LoggerFactory.getLogger(HospitalBedInfoService.class);
	public void doGetBedInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException{
		HospitalBedInfoModel dsm = new HospitalBedInfoModel(dao);
			dsm.doGetBedInfo(req, res, ctx);
	}
}
