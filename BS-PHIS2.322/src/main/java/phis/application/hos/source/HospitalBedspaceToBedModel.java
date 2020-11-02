package phis.application.hos.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.util.context.Context;

public class HospitalBedspaceToBedModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalBedspaceToBedModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 转床
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveBedToBedVerification(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("al_zyh", body.get("al_zyh"));
		parameters.put("as_cwhm_Old", body.get("as_cwhm_Old"));
		parameters.put("as_cwhm_New", body.get("as_cwhm_New"));
		parameters.put("il_brks", body.get("il_brks"));
		BSPHISUtil.cwgl_zccl(parameters, dao, ctx);
	}
}
